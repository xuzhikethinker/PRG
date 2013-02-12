/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;

//import IslandNetworks.Constants;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Simple Route Matrix.
 * <p>This can be used to store information on one route from each 
 * source-target vertex pair. These can be shortest distances, largest potentials
 * or anything else.  Note that this removes multi-edges from the input list
 * so is optimising all given paths between source and target.  It does <b>not</b>
 * optimise for routes that may take in more vertices.
 * @author time
 */
public class SimpleRouteMatrix {

    /**
     * Indicates that a double is not set.
     * <p>Value is {@value }
     */
    public static final double DUNSET = -8.6421357e78;
    
    /**
     * Indicates that an int is not set.
     * <p>Value is {@value }
     */
    static private int IUNSET = -86421357;
 
    /**
     * Indicates if want symmetric or asymmetric matrix.
     */
    boolean symmetric;
    /**
     * Dimension of matrix
     * <p>If negative then not set.
     */
    int dim= IUNSET;

    /**
     * Largest vertex index.
     * <p>If negative then not set.
     */
    int vMax=IUNSET;
    /**
     * Smallest vertex index.
     * <p>If negative then not set.
     */
    int vMin= IUNSET;

    /**
     * Simple length matrix.
     * <p>This is the adjacency matrix of a simple weighted
     * graph (no multi-edges or self-loops).
     * <p>defined to be <tt>dm[source][target]</tt>.
     * Stores at most a single value of a length measure (distance or potential)
     * for all pairs of vertices. Note that this removes multi-edges from the input list
     * so is optimising all given paths between source and target.  It does <b>not</b>
     * optimise for routes that may take in more vertices.
     */
    double [][] lenMat;
    
    /**
     * Matrix of the actual routes used for simple length matrix.
     * <p>defined to be <tt>pm[source][target]</tt>
     */
    Path [][] routeMat;
    
    /**
     * Length measure used to define values in the matrix.
     */
    PathMeasure pathMeasure;
    
    SimpleRouteMatrix(PathMeasure pathMeasure, boolean symmetric){
        this.pathMeasure=pathMeasure;
        this.symmetric = symmetric;
    }
//    SimpleRouteMatrix(int dimension, PathMeasure pathMeasure){
//        dim = dimension;
//        this.pathMeasure=pathMeasure;
//        lenMat = new  double[dim][dim];
//        routeMat = new Path[dim][dim];
//    }
    
    
    /**
     * Makes the adjacency matrix.
     * @param fullFileName used to determine directory and name of file.
     * @param sampleFrequency number of lines to skip, 1 or less and all are taken
     * @param infoOn true (false) if want information on screen on lines being read.
     * @param sumNotProductLengths
     */
    public void makeMatrix(String fullFileName,
            int sampleFrequency,
            boolean infoOn, boolean sumNotProductLengths) {
        ArrayList<Path> pathList = readPathFile(fullFileName,sampleFrequency, infoOn);
        setFromPathList(pathList, sumNotProductLengths);
    }
    /**
      * Sets up an adjacency matrix for a simple graph from list of paths given.
      * <p>Where there are more than one path directly between two vertices the shortest
      * one (largest or smallest length value depending on the measure) is 
      * <p>Where no path is given between two vertices in the list of paths,
      * the matrix is given a negative value equal to largest length 
      * (largest distances or smallest potential) and its route is null.
      * @return the number of paths found, negative indicates error.
      */
    public int setFromPathList(ArrayList<Path> pathList, boolean sumNotProductLengths)
    {
        int s,t;
        // initialise to largest value for this measure
        final double largestLength=pathMeasure.largeValue();
        for (s=0; s<dim; s++) for (t=0; t<dim; t++) {
            lenMat[s][t]=(s==t?pathMeasure.smallestValue():pathMeasure.largeValue());
            routeMat[s][t] = null;
        }

        // look through list and remove multi-edges (duplicates)
        double length;
        for (Path p: pathList){
            s= p.getSource();
            t=p.getTarget();
            if (symmetric && (s>t)) {int temp=s; s=t; t=temp;}
            length=p.totalLength(pathMeasure, sumNotProductLengths);
            if ((routeMat[s][t]==null) || (pathMeasure.compareMeasures(lenMat[s][t], length))) {
                // update routine.
                lenMat[s][t]=length;
                routeMat[s][t]=p;
            }
        }      
        if (symmetric){
            for (s=0; s<dim; s++) for (t=s+1; t<dim; t++) {
                lenMat[t][s]=lenMat[s][t];
                if (routeMat[s][t]!=null) {
                    if (routeMat[s][t].getSource()==s){
                       routeMat[t][s]=Path.reversePath(routeMat[s][t]); // a deep copy
                    }
                    else{
                        routeMat[t][s]=routeMat[s][t]; // this needs to refer to the path not the matrix entry
                        routeMat[s][t]=Path.reversePath(routeMat[t][s]); 
                    }
                }
            }                
        }
        return 0;
    }
    
    /**
     * Basic checks
     * @return true (false) if OK.
     */
    public boolean check(){
        Path p;
        boolean res=true;
        for (int s=0; s<dim; s++) for (int t=0; t<dim; t++) {
             p=routeMat[s][t];
             if (p==null) continue;
             if (p.getSource()!=s) {res=false; System.err.println("routeMat source vertex "+p.getSource()+" does not match index "+s);}
             if (p.getTarget()!=t) {res=false; System.err.println("routeMat target vertex "+p.getTarget()+" does not match index "+t);}
        }
        return res;
    }
    
   /**
      * Reads in list of paths from file filename.
      * <p>Each line is a single path.
      * Each path is given with source then target vertices followed by a list of segments.
      * Each segment is a list of two numbers, distance then the type index.
      * The type index may be either a number of a string representing the type of the segment.
      * Entries are separated by whitespace.
      * Both the vertexSet and pathList must be instantiated
      * @param fullFileName used to determine directory and name of file.
      * @param sampleFrequency number of lines to skip, 1 or less and all are taken
      * @param infoOn true (false) if want information on screen on lines being read.
      * @return list of paths found.
      */
    public ArrayList<Path>  readPathFile(String fullFileName,
            int sampleFrequency,
            boolean infoOn)
    {
         TreeSet<Integer> vertexSet = new TreeSet();
         ArrayList<Path> pathList = new ArrayList();
         int res = ReadPathFile.processPathFile(fullFileName, vertexSet, pathList, sampleFrequency,false);
         if (infoOn) ReadPathFile.printInfo(", ", vertexSet, pathList);
         
         vMax=vertexSet.first();
         vMin=vertexSet.first();
         for (Integer v: vertexSet) {
           vMax=Math.max(vMax,v);
           vMin=Math.min(vMin,v);          
         } 
         if (infoOn) System.out.println("index of first and last vertices "+vMin+", "+vMax+" = dimension");
         dim = vMax+1;
         lenMat = new  double[dim][dim];
         routeMat = new Path[dim][dim];

         
         // test integer assignments
         for (int v=0; v<dim; v++) if (!vertexSet.contains(v)) {
             res=-v;
             System.err.println("Missing vertex "+v);
         }
         for (Integer v: vertexSet) if ((v<0) || (v>=dim))
             {
             res=-dim;
             System.err.println("Contains vertex index "+v+" should be from 0 to "+(dim-1)+" inclusive");
         }
         
         if (res<0) throw new RuntimeException("Errors found, number "+res);
         
         return pathList;
    }
    
    
    
   public void printAllPretty(PrintStream PS, String sep){
      for (int s=0; s<dim; s++) for (int t=0; t<dim;t++) {
          double l = lenMat[s][t];
          PS.println("---"+s+" -> "+t+" length = "+(l<0?"NA":l));
          Path p=routeMat[s][t];
          if (p==null) PS.println("No Routes");
          else p.printPretty(PS, sep);
      }
   }
          
    
     }
  
    



