/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;

//import java.lang.Integer;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Simple Route Matrix.
 * <p>This can be used to store information on one route from each 
 * source-target vertex pair. These can be shortest distances, largest potentials
 * or anything else.  Note that this removes multi-edges from the input list
 * so is optimising all given paths between source and target.  It does <b>not</b>
 * optimise for routes that may take in more vertices.
 * @author time
 */
public class BestRouteMatrix {

    /**
     * Indicates that a double is not set.
     * <p>Value is {@value }
     */
    public static final double DUNSET = -8.6421357e78;
    /**
     * Indicates that a double is not set.
     * <p>Value is {@value }
     */
    public static final int IUNSET = -86421357;
//    /**
//     * Indicates that length to this veretx not yet set.
//     * <p>Used for vertexLength and optimal length calculation.
//     * <p>Value is {@value }
//     */
//    public static final double NOTVISITED = -9999999;
    /**
     * Dimension of matrix
     * <p>If negative then not set.
     */
    int dim= IUNSET;;
    /**
     * Smallest vertex index.
     * <p>If negative then not set.
     */
    int vMin= IUNSET;
    
    /**
     * Simple length matrix.
     * <p>This is the adjacency matrix of a simple weighted
     * complete graph (no multi-edges or self-loops but all other edges present).
     * <p>Defined to be <tt>dm[source][target]</tt>.
     * Stores at most a single value of a length measure (distance or potential)
     * for all pairs of vertices. Note that this removes multi-edges from the input list
     * so is optimising all given paths between source and target.  It does <b>not</b>
     * optimise for routes that may take in more vertices.
     */
    double [][] bestLenMat;
    
    /**
     * Matrix of the rank of target vertices by length.
     * <p>Lower rank <tt>rankByLengthMat[s][t]</tt> means 
     * target <tt>t</tt> closer to source <tt>s</tt>.
     */
   int [][] rankByLengthMat;

   /**
    * If set then has calculated vertex betweenness.
    */
   int [] vertexBetweenness;

    /**
     * Matrix of the actual routes used for simple length matrix.
     * <p>These are stored as full paths.
     * Defined to be <tt>bestRouteMat[source][target]</tt>
     */
   Route [][] bestRouteMat;
    
    /**
     * Matrix of the actual routes used for simple length matrix.
     * <p>These are stored as simple sequences of vertices.
     * Defined to be <tt>bestRouteVertexMat[source][target]</tt>
     */
   ArrayList<Integer> [][] bestRouteVertexMat;
    
   /**
     * Length measure used to define values in the matrix.
     */
    PathMeasure pathMeasure;
    

    
    BestRouteMatrix(){    }
    


   /**
    * Finds optimal length and associated best route for all vertex pairs.
    * <p>For vertex v0 it will find the shortest length (shortest distance or maximum potential)
    * between v0 and all other vertices. It will record this value in the bestMat array
    * and the best route in bestRoute array.  It requires a matrix of lengths and associated routes
    * for vertex pairs which need not be complete.  Negative lengths indicates no
    * direct route is known for that pair.
    * @param srm simple route matrix of paths between vertex pairs
    * @param infoOn true (false) if want information on screen
    */
    public void calcLength(SimpleRouteMatrix srm, boolean infoOn){
        this.pathMeasure=srm.pathMeasure;
        if (!srm.check()) throw new RuntimeException("SimpleRouteMatrix failed check");
        calcLength(srm.dim, srm.vMin,srm.lenMat, srm.routeMat, infoOn);
    }
    
    /**
    * Finds optimal length and associated best route for all vertex pairs.
    * <p>For vertex v0 it will find the shortest length (shortest distance or maximum potential)
    * between v0 and all other vertices. It will record this value in the bestMat array
    * and the best route in bestRoute array.  It requires a matrix of lengths and associated routes
    * for vertex pairs which need not be complete.  Negative lengths indicates no
    * direct route is known for that pair.
    * <p>The vertex indices are assumed to run consecutively from vMin to (vMin+dim-1) inclusive.
    * @param dimension dimension of arrays
    * @param vMin index of lowest vertex.
    * @param lenMat matrix of lengths
    * @param routeMat matrix of paths between vertex pairs
    * @param infoOn true (false) if want information on screen
    */
    public void calcLength(int dimension, int vMin, double [][] lenMat, Path [][] routeMat, boolean infoOn){
        this.vMin=vMin;
        dim = dimension;
        bestLenMat = new  double[dim][dim];
        bestRouteMat = new Route[dim][dim];
        bestRouteVertexMat = new ArrayList[dim][dim];
        rankByLengthMat= new  int[dim][dim];
        for (int v0=0; v0<dim; v0++) {
            for (int v=0; v<dim; v++) {
             bestLenMat[v0][v]=lenMat[v0][v];
             rankByLengthMat[v0][v]=IUNSET;
            }
            bestLenMat[v0][v0]=0;
            rankByLengthMat[v0][v0]=0;
        }
        for (int v0=0; v0<dim; v0++) calcLengthNext(v0, routeMat, infoOn);//calcLengthNext(v0, lenMat, routeMat);
    }
        
   /**
    * Finds all vertices given distance away from initial vertex. 
    * <p>For vertex v0 it will find the shortest length (shortest distance or maximum potential)
    * between v0 and all other vertices. It will record this value in the bestMAt array
    * and the best route in bestRoute array.  It requires a matrix of lengths and associted routes
    * for vertex pairs which need not be complete.  Negative lengths indicates no
    * direct route is known for that pair..
    * @param v0 initial vertex
    * @param lenMat matrix of lengths
    * @param routeMat matrix of paths between vertex pairs
    * @param infoOn true (false) if want information on screen
    */
    private void calcLengthNext(int v0, 
            //double [][] lenMat, 
            Path [][] routeMat, boolean infoOn){
        boolean fullRoutesOn=false;
        
        // initialise to find best route from v0 to all other vertices
        ArrayList<OrderedVertex> vertexListSorted = new ArrayList();
        OrderedVertex [] vertexListOrdered= new OrderedVertex[dim];
        for (int v=0; v<dim; v++) {
            OrderedVertex ov = new OrderedVertex(v0,v, bestLenMat[v0][v], pathMeasure);
            if (routeMat[v][v0]!=null) ov.addToBestRoute(v); // set direct route
            if (v==v0){
                bestLenMat[v0][v0]=0;
//                ov best route etc already set within class
            }
            vertexListOrdered[v]=ov;
            vertexListSorted.add(ov);
            //vertexListOrdered[v].setBestRoute(v0,v);
        } //eo for v
        int v=IUNSET;
        double lenvtovn=DUNSET;
        double newLength =DUNSET;
        for (int distanceRank=0; distanceRank<dim; distanceRank++){
            // Produce sorted list which has sorted refences to fundamental 
            // OrderedVertex objects.  This way vertexListOrdered remains a set of
            // references to same objects but in index order.
            Collections.sort(vertexListSorted); 
            
            // next nearest vertex is vertex index v, its (distanceRank)-th in sorted list
            OrderedVertex ov =vertexListSorted.get(distanceRank);
            ov.fixed=true;
            v=ov.index;
            if (v==v0) continue;
            // now check all neighbours of v and update v0->v->vn paths if needed
            for (int vn=0; vn<dim; vn++){
                lenvtovn = bestLenMat[v][vn];
                if (v==vn)  continue;
                //double lenv0tov=  lenMat[v0][v];
                newLength = bestLenMat[v0][v]+ bestLenMat[v][vn]; //lenvtovn;
                if (newLength==bestLenMat[v0][vn]) continue;
                if  (pathMeasure.compareMeasures(newLength,bestLenMat[v0][vn])) continue;
                // update path as v0 to v to vn is better that whats already there
                bestLenMat[v0][vn]=newLength;
                vertexListOrdered[vn].replaceBestRoute(ov.bestRoute); //deep copy needed
                vertexListOrdered[vn].addToBestRoute(vn);
                vertexListOrdered[vn].length=newLength;
                
            }           
        } //eo for i
        
        if (infoOn){
            System.out.println("\n --- Best Routes from "+v0);
             for (v=0; v<dim; v++){
                 System.out.println(vertexListOrdered[v].pathPrettyString(" "));
             }
        }
        
        // store simple vertex list
        for (v=0; v<dim; v++) {
            bestRouteVertexMat[v0][v]=new ArrayList();
            for (Integer vi :vertexListOrdered[v].bestRoute) bestRouteVertexMat[v0][v].add(vi);
        } 
        
        // record rank of targets by length
        Collections.sort(vertexListSorted);
        for (int rank=0; rank<dim; rank++){
            v= vertexListSorted.get(rank).index;
            rankByLengthMat[v0][v]=rank;
        }
        
        //Now set up best route matrix
        if (fullRoutesOn){
            // note there are gaps currently. 
            // We need to update the paths when we update the vertex list paths
            // OR BETTER, construct paths directly using vertex list route.
            //TODO add option to either do the full routes properly or not at all.            
            int vprevious=IUNSET;
            Path p = null;
            for (v=0; v<dim; v++){
                bestRouteMat[v0][v]=new Route();
                if (v==v0) continue;
                vprevious=IUNSET;
                // look at route from v0 to v
                for (Integer vnext: vertexListOrdered[v].bestRoute){
                    if (vprevious>=0){
                     try{
                         p=routeMat[vprevious][vnext];  
                         if (p==null) bestRouteMat[v0][v].addToRoute(new Path (vprevious,vnext));
                         else bestRouteMat[v0][v].addToRoute(p);
                     } catch (Exception e){
                         System.err.println("Exception "+e.getMessage()+" updating best route");
                         System.err.println("source, target,current, next vertices "+v0+", "+v+", "+vprevious+", "+vnext+". ");
                         throw new RuntimeException(e.getMessage());
                     }
                    }
                    vprevious=vnext;
                } //for vnext
            }
        }// end of full route update
        
    }

    // p488 Eck
//    public int compareTo( T obj )
//The value returned by obj1.compareTo(obj2) should be negative if and only if obj1 comes
//before obj2, when the objects are arranged in ascending order. It should be positive if and
//only if obj1 comes after obj2. A return value of zero means that the objects are considered
//to be the same for the purposes of this comparison.
    
    public class OrderedVertex implements Comparable<OrderedVertex>{
        
        int index;
        double length;
        //int rank;
        PathMeasure pm;
        boolean fixed;
        /**
         * Best route to this vertex as ordered sequence of vertex indices.
         * <p>First in list gives the source for the route and should be set even if no route yet found.
         * Last should be this vertex.
         */
        private ArrayList<Integer> bestRoute;
        
        public OrderedVertex(int routeSource, int index, double length, PathMeasure pm){
            this.index=index;
            this.length=length;
            this.pm=pm;
            //rank=IUNSET;
            bestRoute = new ArrayList(); 
            bestRoute.add(routeSource);
            if (index==routeSource) length=pm.smallestValue();
            fixed=false;
        }
        
        /**
         * Returns -1 (+1) if length of this vertex is less than length of given vertex.
         * <p>Uses path measure to decide on this.
         * @param ov vertex to be compared against
         * @return -1 (+1) if length of this vertex is less than length of given vertex, 0 if equal lengths
         */
        public int compareTo( OrderedVertex ov ){
            if (length==ov.length) return 0;
            if (pm.compareMeasures(length,ov.length)) return +1;
            return -1;
        }
        
        /**
         * Checks to see if has a best route set.
         * <p>Even if a length is set it may be a very long one if no route exists..
         * This is the way to see if a best route has been set.
         * Need to deal with vertex beign source of all paths i.e. exclude self-loops.
         * @return true (false) if a valid route has (not) been set.
         */
        public boolean hasBestRoute(){
            return (((bestRoute.size()>1) || isSource() ) ? true:false);
        }
        
       /**
         * Checks to see if this vertex is the source of all paths.
         * @return true (false) if this vertex is the source of all paths.
         */
        public boolean isSource(){return ((index==getRouteSource())? true:false);}
        /**
         * Gives index of vertex which is the source of all paths.
         * @return index of source vertex of all paths
         */
         public int getRouteSource(){return bestRoute.get(0);}
        
//        /**
//         * USe to record rank by length
//         * @param r
//         */
//        public void setRank(int r){rank=r;}
        
//        public void setBestRouteDirect(int v0, int v1){
//            bestRoute = new ArrayList(); 
//            bestRoute.add(v0);
//            bestRoute.add(v1);            
//        }
         /**
          * Sets up new best route.
          * <p>The first vertex must be the source and this must not change.
          * @param vList list of vertices with source being first and must not change.
          */
        public void setBestRoute(int [] vList){
            int rs=getRouteSource();
            if (vList[0]!=rs) throw new RuntimeException("*** In setBestRoute rote source has changed from "+rs+" to "+vList[0]);
            bestRoute = new ArrayList(vList.length);
            for (int i=0; i<vList.length;i++) bestRoute.add(vList[i]);
        }
        /**
         * Deep copy of an existing best route.
         * <p>??? Is the constructor used a deep copy?
         * @param newBestRoute new best route
         */
        public void replaceBestRoute(ArrayList newBestRoute){
            bestRoute = new ArrayList(newBestRoute); // is this a deep copy?
        }
        /**
         * Adds vertex to existing best route
         * @param v next vertex on route
         */
        public void addToBestRoute(int v){
            bestRoute.add(v);
        }
        
        public String pathPrettyString(String sep){
            String s="l="+sep+length;
            for (Integer v:bestRoute) s=s+sep+(v==getRouteSource()? "" : " -> "+sep)+v;
            return s;
        }
    }

   // --------------------------------------------------------------------------  
    /**
     * Writes Best Route Matrix to file with <tt>outputBRM.dat</tt> extension.
     */
      public void outputBestRouteMatrixFile(String fullFileNameRoot, String sep, String cc, boolean headingsOn, boolean infoOn){
        PrintStream PS;
        FileOutputStream fout;
        String fullFileName = fullFileNameRoot + pathMeasure.descriptionStringCompact()+"outputBRM.dat";
        String descriptionString = "Best Route Matrix to file "+ fullFileName;
        if (infoOn) System.out.println("Writing "+descriptionString);
            try {
            fout = new FileOutputStream(fullFileName);
            PS = new PrintStream(fout);
            
            outputBestRouteMatrixFile(PS, sep, cc, headingsOn, infoOn);
            
            if (infoOn) System.err.println("Finished writing "+descriptionString);
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +descriptionString+", "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening "+descriptionString+", "+e.getMessage());
            return;
        }
        return;
         }

   /**
     * Writes Best Route Matrix to file with <tt>outputBRM.dat</tt> extension.
     */
      public void outputBestRouteMatrixFile(PrintStream PS, String sep, String cc, boolean headingsOn, boolean infoOn){
          if (headingsOn){
              PS.println(cc+pathMeasure.descriptionStringPretty(sep));
              PS.println(" s.t");
              for (int t=0;t<dim; t++) PS.print(sep+(t+vMin)); //((t==0)?"":sep)
              PS.println();
          }
          for (int s=0;s<dim; s++) {
              if (headingsOn) PS.print((s+vMin));
              for (int t=0;t<dim; t++) PS.print(sep+bestLenMat[s][t]);
              PS.println();
          }
      }

    /**
     * Writes Best Route Matrix to file with <tt>outputBRL.dat</tt> extension.
     */
      public void outputBestRouteListFile(String fullFileNameRoot, String sep, String cc, boolean headingsOn, boolean infoOn){
        PrintStream PS;
        FileOutputStream fout;
        String fullFileName = fullFileNameRoot + pathMeasure.descriptionStringCompact()+"outputBRL.dat";
        String descriptionString = "Best Route List to file "+ fullFileName;
        if (infoOn) System.out.println("Writing "+descriptionString);
            try {
            fout = new FileOutputStream(fullFileName);
            PS = new PrintStream(fout);
            
            outputBestRouteListFile(PS, sep, cc, headingsOn, infoOn);
            
            if (infoOn) System.err.println("Finished writing "+descriptionString);
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +descriptionString+", "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening "+descriptionString+", "+e.getMessage());
            return;
        }
        return;
         }

   
   /**
     * Writes Best Route Matrix to file with <tt>outputBRL.dat</tt> extension.
     */
      public void outputBestRouteListFile(PrintStream PS, String sep, String cc, boolean headingsOn, boolean infoOn){
          if (headingsOn){
              PS.println(cc+pathMeasure.descriptionStringPretty(sep));
              PS.println("Source"+sep+"Target"+sep+"Distance"); 
          }
          for (int s=0;s<dim; s++) {
              for (int t=0;t<dim; t++) PS.println(String.format("%8d",(s+vMin))+sep
                      +String.format("%8d",(t+vMin))+sep
                      +String.format("%10.4f",bestLenMat[s][t]));
          }
      }

   
    /**
     * Writes file with Best Routes with <tt>outputRoutes.dat</tt> extension.
     */
      public void outputBestRouteFile(String fullFileNameRoot, String sep, String cc, boolean headingsOn, boolean infoOn){
        PrintStream PS;
        FileOutputStream fout;
        String fullFileName = fullFileNameRoot + pathMeasure.descriptionStringCompact()+"outputRoutes.dat";
        String descriptionString = "best routes to file "+ fullFileName;
        if (infoOn) System.out.println("Writing "+descriptionString);
            try {
            fout = new FileOutputStream(fullFileName);
            PS = new PrintStream(fout);
            
            outputBestRouteFile(PS, sep, cc, headingsOn, infoOn);
            
            if (infoOn) System.err.println("Finished writing "+descriptionString);
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +descriptionString+", "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening "+descriptionString+", "+e.getMessage());
            return;
        }
        return;
         }

   /**
     * Writes Best Route Matrix to file with <tt>outputBRM.dat</tt> extension.
    * <p>Also calculates vertex betweenness.
    * <p>This is as a simple sequence of vertices.
     */
      public void outputBestRouteFile(PrintStream PS, String sep, String cc, boolean headingsOn, boolean infoOn){
          if (headingsOn){
              PS.println(cc+pathMeasure.descriptionStringPretty(sep));
              PS.println(" source"+sep+"target"+sep+"target_rank"+sep+"distance"+sep+"vertex_list");
          }
          //ArrayList<Integer> vertexPath;
          vertexBetweenness = new int[dim];
          ArrayList<Integer> bestRouteList;
          int v=IUNSET;
          for (int s=0;s<dim; s++) for (int t=0;t<dim; t++){
              PS.print((s+vMin)+sep+(t+vMin)+sep+rankByLengthMat[s][t]+sep+bestLenMat[s][t]);
              //for (Integer v : bestRouteVertexMat[s][t]) PS.print(sep+v);
              bestRouteList = bestRouteVertexMat[s][t];
              if (bestRouteList.isEmpty()) {PS.println(IUNSET); continue;}
              PS.print(sep+bestRouteList.get(0));
              for (int i=1; i<bestRouteList.size()-1; i++) {
                  v=bestRouteList.get(i);
                  PS.print(sep+v);
                  vertexBetweenness[v]++;
              }
              if (bestRouteList.size()>1) PS.print(sep+bestRouteList.get(bestRouteList.size()-1));
              PS.println();
          } 
          PS.println("Vertex "+sep+"Betweenness");
          for (int s=0;s<dim; s++) PS.println(s+sep+vertexBetweenness[s]);
      }


     static public void calcVertexBetweenness(int [] vertexBetweenness,  ArrayList<ArrayList<Integer>> indexToLabel){
          int v=-97531;
          for (ArrayList<Integer> path: indexToLabel){
              for (int i=1; i<path.size()-1; i++) {
                  v=path.get(i);
                  if (v<0 || v>=vertexBetweenness.length) throw new RuntimeException("*** Found illegal vertex label "+v);
                  vertexBetweenness[v]++;
              }
          }

    }

}
  
    



