/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.algorithms.DAG;

import TimGraph.timgraph;
import TimUtilities.FileUtilities.FileNameSequence;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
//import cern.jet.random.Beta;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.IllinoisSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.special.Beta;

/**
 *
 * @author time
 */
public class DimensionDAG {

    //timgraph tg;
    //final int numberVertices;

    static final double DUNSET = -3579e35;

    public DimensionDAG(timgraph tginput){
        if (!tginput.isDirected()) throw new RuntimeException("Input graph must be directed");
        if (tginput.isWeighted()) System.err.println("Weights on Input graph are ignored");
//        tg=tginput;
//        if (!tginput.isVertexEdgeListOn()) tg.createVertexGlobalEdgeList();
//        tg.setEdgeLabels(UNDELETED); // labels used for path lengths
//        numberVertices=tg.getNumberVertices();
    }


    static public boolean testTimGraph(timgraph tginput){
        if (!tginput.isDirected()) throw new RuntimeException("Input graph must be directed");
        if (tginput.isWeighted()) System.err.println("Weights on Input graph are ignored");
        return true;
    }


        /**
         * Calculate maximum distances FROM v TO all other vertices.
         * @see #maximumDistanceFromVertex(int, int)
         * @param v distances from this vertex are returned
         * @return array distance[target] which gives distance from vertex v to target. 0 if not reachable.
         */
        static public int [] maximumDistanceFromVertex(timgraph tg, int v){
            if (!testTimGraph(tg)) return null;
            int maxv=tg.getNumberVertices(); // the last index is this minus one
            return maximumDistanceFromVertex(tg, v, maxv);
        }
       /**
         * Calculate maximum distances FROM v TO all other vertices upto given limit.
         * <p>Assumes that graph vertices are ordered so that edges point
         * from low to high numbered vertices.
         * <p>If edge labels are on will test to see is these are set to deleted
         * value.
         * <p>Any vertices with index greater than maxv parameter are ignored.
         * The vertex with maxv index is allowed.  This can be used to  limit
         * consideration to a given causality cone
         * @param v distances from this vertex are returned
         * @param maxv largest vertex index allowed
         * @return array distance[target] which gives distance from vertex v to target. 0 if not reachable.
         */
        static public int [] maximumDistanceFromVertex(timgraph tg, int v, int maxv){
            if (!testTimGraph(tg)) return null;
            if (!tg.isVertexInGraph(v)) throw new RuntimeException("maximumDistance vertex "+v+" is out of range");
            boolean eLabel=false;
            int numberVertices=tg.getNumberVertices();
            if (tg.isEdgeLabelled()) eLabel=true;
            int [] distance = new int[numberVertices]; // require zeros initially
            TreeSet<Integer> toVisit = new TreeSet();
            toVisit.add(v);
            int nn=-1;
            int stub=-1; // global stub index
            int currentv=v;
            int kout=-1;
            int dnn=-1;
            /**
             * Distance to vertex currentv plus one
             */
            int dvp1=-1;
            while (toVisit.size()>0){
              currentv=toVisit.first();
              toVisit.remove(currentv);
              kout = tg.getVertexDegree(currentv);
              dvp1= distance[currentv]+1;
              for (int e=0; e<kout; e++){
                    nn=tg.getVertexTargetQuick(currentv, e); // nn is the e-th neighbour of v going with the direction of edges
                    if (nn<=currentv) throw new RuntimeException("maximumDistance: graph is not ordered, first vertex "+v+", current vertex "+currentv+", neighbouring vertex "+nn);
                    if (nn>maxv) continue; // outside range we wish to investigate
                    if (eLabel) {stub = tg.getStub(currentv, e); // we have an edge from currentv (source) to nn (target) and
                                 if (tg.getEdgeLabel(stub)==TransitiveReduction.DELETEDEDGE) continue; // this edge is deleted
                    }
                    dnn=distance[nn];
                    if (dnn==0){ // not yet visited
                        toVisit.add(nn);
                        distance[nn]=dvp1;
                    } else{ // found a route from v to currentv and then nn
                        distance[nn]=Math.max(dnn, dvp1);
                    }
              } // eo for e
            }
            return distance;
    }//eo maximumDistanceFromVertex

         /**
         * Calculate maximum distances FROM v TO all other vertices.
         * @see #maximumDistanceFromVertex(int, int)
         * @param v distances from this vertex are returned
         * @return array distance[target] which gives distance from vertex v to target. 0 if not reachable.
         */
        static public int [] maximumDistanceToVertex(timgraph tg, int v){
            if (!testTimGraph(tg)) return null;
            int minv=-1; // the first vertex is 0 so this is always before that
            return maximumDistanceToVertex(tg, v, minv);
        }
      /**
         * Calculate maximum distances TO v FROM all other vertices less than minv.
         * <p>Assumes that graph vertices are ordered so that edges point
         * from low to high numbered vertices.
         * <p>If edge labels are on will test to see is these are set to deleted
         * value.
         * <p>Any vertices with index less than minv parameter are ignored.  
         * The vertex with minv index is allowed.  This can be used to limit 
         * consideration to a given causality cone.
         * @param v distances to this vertex are returned
         * @param minv minimum vertex index allowed
         * @return array distance[target] which gives distance from target to vertex v. 0 if not reachable.
         */
       static public int [] maximumDistanceToVertex(timgraph tg, int v, int minv){
            if (!testTimGraph(tg)) return null;
            if (!tg.isVertexInGraph(v)) throw new RuntimeException("maximumDistance vertex "+v+" is out of range");
            boolean eLabel=false;
            if (tg.isEdgeLabelled()) eLabel=true;
            int numberVertices=tg.getNumberVertices();
            int [] distance = new int[numberVertices]; // require zeros initially
            TreeSet<Integer> toVisit = new TreeSet();
            toVisit.add(v);
            int nn=-1;
            int stub=-1; // global stub index
            int currentv=v;
            int kin=-1;
            int dnn=-1;
            /**
             * Distance to vertex currentv plus one
             */
            int dvp1=-1;
            while (toVisit.size()>0){
              currentv=toVisit.first();
              toVisit.remove(currentv);
              kin = tg.getVertexInDegree(currentv);
              dvp1= distance[currentv]+1;
              for (int e=0; e<kin; e++){
                    nn=tg.getVertexSourceQuick(currentv, e); // this is the e-th `anti'-neighbour of currentv going with the direction of edges
                    if (nn>=currentv) throw new RuntimeException("maximumDistanceFromVertex: graph is not ordered, first vertex "+v+", current vertex "+currentv+", neighbouring vertex "+nn);
                    if (nn<minv) continue; // ignore vertices outside specified causality cone
                    if (eLabel) {stub = tg.getStub(currentv, e); // we have an edge from nn (source) to currentv (target)
                                 if (tg.getEdgeLabel(stub)==TransitiveReduction.DELETEDEDGE) continue; // this edge is deleted
                    }
                    dnn=distance[nn];
                    if (dnn==0){ // not yet visited
                        toVisit.add(nn);
                        distance[nn]=dvp1;
                    } else{ // found route from nn to currentv and then on to v
                        distance[nn]=Math.max(dnn, dvp1);
                    }
              } // eo for e
            }
            return distance;
    }//eo maximumDistanceFromVertex



   /**
    * Finds maximum distances between all vertices.
    * It should not matter whether we use the from or to vertex routines
    * as chosen by boolean option.  That is if we work from one vertex up
    * or down the tree.
    * @param fromOn if true (false) use from (to) vertex routine.
    */
   static public DoubleMatrix2D  getDistanceMatrix(timgraph tg, boolean fromOn){
        if (!testTimGraph(tg)) return null;
        int numberVertices=tg.getNumberVertices();
        DoubleMatrix2D  distanceMatrix = new SparseDoubleMatrix2D(numberVertices,numberVertices);
        int [] distances;

        // calculate all maximum distances and store in sparse matrix
        for (int v=0; v<numberVertices; v++){
             if (fromOn){
                 distances = maximumDistanceFromVertex(tg,v);
                 for (int t=v; t<numberVertices; t++) distanceMatrix.setQuick(v, t, distances[t]);
             } else
             {
                 distances = maximumDistanceToVertex(tg,v);
                 for (int s=0; s<v; s++) distanceMatrix.setQuick(s, v, distances[s]);
             }
        }
        return distanceMatrix;
    }

    static public DoubleMatrix2D  getDimensionMatrix(timgraph tg, DoubleMatrix2D  distanceMatrix, boolean fromOn){
        if (!testTimGraph(tg)) return null;
        int numberVertices=tg.getNumberVertices();
        DoubleMatrix2D  dimensionMatrix = new SparseDoubleMatrix2D(numberVertices,numberVertices);
        double dim=-1.0;
        for (int s=0; s<numberVertices; s++)
             for (int t=s+1; t<numberVertices; t++) {
                 dim=calcDimension(tg, s,t,  distanceMatrix);
                 if (dim>0) dimensionMatrix.setQuick(s, t, dim);
             }
        return dimensionMatrix;
    }
   /**
    * Finds dimension for specified interval.
    * It should not matter whether we use the from or to vertex routines
    * as chosen by boolean option.  That is if we work from one vertex up
    * or down the tree.
    * <p>Page 54, Meyer PhD thesis.  It is clear that the number of elements
    * in the formula is the number of z in the interval
    * source &lt; z &lt target <b>excluding</b> source and target.  It seems
    * likely from reading around the equation that
    * the number of chains is based on the same set of points, i.e.
    * those in the interval but <b>excluding</b> source and target.  This is
    * Meyer has implemented in his programme in Appendix B of his thesis.
    * <p><b>Is this formula true if boundary points not at same spatial position?</b>
    * @param source earliest vertex of interval
    * @param target last vertex of interval (must be greater than source)
    * @param distanceMatrix matrix of distances
    */
    static public double calcDimension(timgraph tg, int source, int target, DoubleMatrix2D  distanceMatrix){

        if (!testTimGraph(tg)) return DUNSET;
        int numberVertices=tg.getNumberVertices();

        // calculate dimensions for all pairs and store in sparse matrix
        DoubleMatrix2D  dimensionMatrix = new SparseDoubleMatrix2D(numberVertices,numberVertices);
        int numberTwoChains = 0;
        int numberPoints = 0;
        for (int v=source+1; v<target; v++) {
             if ( (distanceMatrix.getQuick(source,v)>0) && (distanceMatrix.getQuick(v,target)>0) )
             {
                 numberPoints++;
                 for (int u=source+1; u<v; u++){ // should u=v be allowed?
                  if ( (distanceMatrix.getQuick(source,u)>0) && (distanceMatrix.getQuick(u,target)>0) )
                        numberTwoChains++;
                 }// eo for u
            }
        } // eo for v
        double dim=DUNSET;
        if (numberTwoChains>0 && numberPoints>0) {
            dim = findDimension(numberTwoChains/(numberPoints*numberPoints));
        }
        return dim;
    }

    /**
     * Solves for dimension of Euclidean space.
     * <p>The parameter is the ratio of the number of two chains to the
     * square of the number of points.  Both of these are calculated for
     * points WITHIN a given causal set interval x &lt;  y.
     * That is the interval is the set of all points z where  x &lt; z &lt;  y#
     * and the points used for the calculation of the two chains and number of
     * points are those in the interval <b>excluding</b> the boundary points x and y.
     * This is the interpretation of Meyer's thesis, p54, and especially the code
     * in Appendix B, p103.
     * <p>Based on equation (2) of D.Reid, PRD 67 (2003) 024034
     * <p><b>Is this formula true if boundary points not at same spatial position?</b>
     * @see http://commons.apache.org/math/userguide/analysis.html#a4.3_Root-finding
     * @param ratio number of two chains over sqaure of number of points
     * @return dimension estimate
     */
    static public double findDimension(double ratio){
        UnivariateFunction function = new logMryheimMeyersDimensionFunction(ratio); // some user defined function object
        final double relativeAccuracy = 1.0e-8;
        final double absoluteAccuracy = 1.0e-6;
        UnivariateSolver solver   = new IllinoisSolver(relativeAccuracy, absoluteAccuracy);
        double d;
        int maxEval=100;
        double min=0.5;
        double max=100.0;
        try {
           d = solver.solve(maxEval, function, min, max);
        } catch (RuntimeException e) {
          System.err.println("*** Error "+e);
          return timgraph.DUNSET;
            // Retrieve the x value.
        }
        return d;
    }

    /**
     * Natural logarithm of Mryheim-Meyers DimensionDAG function.
     * <p>Based on equation (5) of D.Reid, PRD 67 (2003) 024034
     * Gives the number of two chains divided by the square of the number of points
     * in a given causal set interval x &lt;  y.  That is the set of all points
     * z where  x &lt; z &lt;  y.
     * @see http://commons.apache.org/math/userguide/special.html#a5.4_Beta_funtions
     */
    public static class logMryheimMeyersDimensionFunction implements UnivariateFunction {
     static double lnratio=0.0;

     public logMryheimMeyersDimensionFunction(double ratio){
         lnratio=Math.log(ratio*8/3);
     }

     /**
      * Value should be zero when d satisfies Mryheim-Meyers dimension.
      * <p>The residual is ln(S2/(N^2))- ln(f(d))
      * @param d dimension of space-time
      * @return residual
      */
     public double value(double d) {
         return lnratio-Math.log(d)-Beta.logBeta(d+1,d/2) ;
     }
 }



   /**
    * Finds maximum distances between all vertices.
    * It should not matter whether we use the from or to vertex routines
    * as chosen by boolean option.  That is if we work from one vertex up
    * or down the tree.
    * @param fromOn if true (false) use from (to) vertex routine.
    */
        static public void FileOutputMaximumDistances(timgraph tg, boolean fromOn){
        if (!testTimGraph(tg)) return ;
        int numberVertices=tg.getNumberVertices();
        int [] distances;
        PrintStream PS;
        FileOutputStream fout;
        FileNameSequence fileName= new FileNameSequence(tg.outputName);
        fileName.setNameEnd("maxdist"+(fromOn?"from":"to")+".dat");
        try {
            System.out.println("Writing distances matrix to file "+ fileName.getFullFileName());
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            for (int v=0; v<numberVertices; v++){
             distances = (fromOn?maximumDistanceFromVertex(tg,v):maximumDistanceToVertex(tg,v));
             for (int t=0; t<(numberVertices-1); t++) PS.print(distances[t]+timgraph.SEP);
                PS.println(distances[numberVertices-1]);
             }//eo for v
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+", "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        System.out.println("Finished writing distances matrix to file "+ fileName.getFullFileName());
    }

   /**
    * Puts out a distance matrix to a file as list.
    * It should not matter whether we use the from or to vertex routines
    * as chosen by boolean option.  That is if we work from one vertex up
    * or down the tree.
    * @param fromOn if true (false) use from (to) vertex routine.
    */
        static public void FileOutputMaximumDistances(String SEP, timgraph tg,
                DoubleMatrix2D  distanceMatrix, boolean fromOn){
        if (!testTimGraph(tg)) return ;
        int numberVertices=tg.getNumberVertices();
        int [] distances;
        PrintStream PS;
        FileOutputStream fout;
        FileNameSequence fileName= new FileNameSequence(tg.outputName);
        fileName.setNameEnd("maxdist"+(fromOn?"from":"to")+".dat");
        try {
            System.out.println("Writing distances list to file "+ fileName.getFullFileName());
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            for (int s=0; s<numberVertices; s++){
             for (int t=0; t<(numberVertices); t++) {
                 double dist = distanceMatrix.get(s,t);
                 if (dist>0) PS.println(s+SEP+t+SEP+dist);
                 }
             }//eo for s
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+", "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        System.out.println("Finished writing distances list to file "+ fileName.getFullFileName());
    }
   /**
    * Puts out a distance matrix to a file as list.
    * It should not matter whether we use the from or to vertex routines
    * as chosen by boolean option.  That is if we work from one vertex up
    * or down the tree.
    * @param fromOn if true (false) use from (to) vertex routine.
    */
        static public void FileOutputDimensions(String SEP, timgraph tg,
                DoubleMatrix2D  dimensionMatrix, boolean fromOn){
        if (!testTimGraph(tg)) return ;
        int numberVertices=tg.getNumberVertices();
        int [] distances;
        PrintStream PS;
        FileOutputStream fout;
        FileNameSequence fileName= new FileNameSequence(tg.outputName);
        fileName.setNameEnd("maxdist"+(fromOn?"from":"to")+".dat");
        try {
            System.out.println("Writing dimensions list to file "+ fileName.getFullFileName());
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            for (int s=0; s<numberVertices; s++){
             for (int t=0; t<(numberVertices); t++) {
                 double dist = dimensionMatrix.get(s,t);
                 if (dist>0) PS.println(s+SEP+t+SEP+dist);
                 }
             }//eo for s
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+", "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        System.out.println("Finished writing dimensions list to file "+ fileName.getFullFileName());
    }


}
