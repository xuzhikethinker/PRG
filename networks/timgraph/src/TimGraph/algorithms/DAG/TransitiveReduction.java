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
public class TransitiveReduction {

    timgraph tg;
    //ArrayList<Integer> sourceSet;
    TreeSet<Integer> sourceNNSet;

    static final int UNDELETED= 1;
    static final int DELETEDEDGE=-86421;

    final int numberVertices;


    public TransitiveReduction(timgraph tginput){
        if (!tginput.isDirected()) throw new RuntimeException("Input graph must be directed");
        if (tginput.isWeighted()) System.err.println("Weights on Input graph are ignored");
        tg=tginput;
        if (!tginput.isVertexEdgeListOn()) tg.createVertexGlobalEdgeList();
        tg.setEdgeLabels(UNDELETED); // labels used for path lengths
        numberVertices=tg.getNumberVertices();
    }

    /**
     *  Performs partial transitive reduction.
     * <p>Should work on any acyclic directed network
     * (directed acyclic graph).  Eliminates triangle shortcuts only.
     * @return number of deleted edges
     */
    public int reducePartially(){
        int n1=-1;
        int n2=-1;
        int kout=-1;
        int e=-1; // a globel edge label
        int deleted=0;
        int totalDeleted=0;
        int dotsPerLine=10;
        int numberLines=10;
        int verticesPerDot =numberVertices / ( dotsPerLine * numberLines) ;
        int vertexCount=verticesPerDot;
        int dotCount=dotsPerLine;
        for (int v=0; v<numberVertices; v++) {
           kout = tg.getVertexOutDegree(v);
           deleted=0;
           for (int e1=0; e1<kout; e1++){
               n1=tg.getVertexTargetQuick(v, e1); 
               for (int e2=e1+1; e2<kout; e2++){
                 n2=tg.getVertexTargetQuick(v, e2);
                 e=tg.getFirstEdgeGlobal(n1,n2);
                 if (e>=0) {
                     e=tg.getStub(v, e2);
                     if (tg.getEdgeLabel(e)<0) continue;
                     tg.setEdgeLabelQuick(e,DELETEDEDGE);
                     deleted++;
                     //if (infoOn) System.out.println("Deleting "+v+"->"+n2+" leaving "+v+"->"+n1+"->"+n2);
                     continue;
                 }
                 e=tg.getFirstEdgeGlobal(n2,n1);
                 if (e>=0) {
                     e=tg.getStub(v, e1);
                     if (tg.getEdgeLabel(e)<0) continue;
                     deleted++;
                     tg.setEdgeLabelQuick(e,DELETEDEDGE);
                     //if (infoOn) System.out.println("Deleting "+v+"->"+n1+" leaving "+v+"->"+n2+"->"+n1);
                 }

                }//for e2
           }//for e1
           //if (deleted>0) System.out.println("Deleted "+deleted+" edges from vertex "+v);
           totalDeleted+=deleted;
           if (numberVertices>999){
               if ((--vertexCount)== 0) {
                 vertexCount=verticesPerDot;
                 System.out.print(".");
                 if ((dotCount--)==0) {
                     dotCount=dotsPerLine;
                     System.out.println(" -"+totalDeleted+"e, v="+v);
                 }
               }
            }

        }//for v
        return totalDeleted;
    }



    /**
     *  Performs full transitive reduction.
     * <p>Assume vertices ordered
     * so all edges are ordered so that the indices of the vertices have
     * source lower than target.
     * @return number of deleted edges
     */
    public int reduceFully(){
      int totalDeleted=0;
//      int dotsPerLine=10;
//      int numberLines=10;
//      int verticesPerDot =tg.getNumberVertices() / ( dotsPerLine * numberLines) ;
//      int vertexCount=verticesPerDot;
//      int dotCount=dotsPerLine;
      int deleted=0;
      int [] vertexVisited = new int[numberVertices]; // reuqire zeros initially
      TreeSet<Integer> path = new TreeSet();
      int kin=-1;
      for(int s=numberVertices-1; s>=0; s--){
         kin = tg.getVertexInDegree(s);
         if (kin>0) continue;
         deleted=checkNextVertex(s, vertexVisited, s, s, path);
         totalDeleted+=deleted;
         //if (infoOn && deleted>0) System.out.println("v="+s+", deleted "+deleted+", total deleted "+totalDeleted);
//         if ((--vertexCount)== 0) {
//             vertexCount=verticesPerDot;
//             System.out.print(".");
//             if ((dotCount--)==0) {
//                 dotCount=dotsPerLine;
//                 System.out.println(" -"+totalDeleted+"e, v="+s);
//             }
//         }
      }
      return totalDeleted;
    }

        /**
         * Checks next vertex for transitive reduction algorithm.
         * <p>Assumes direction of edges is from low to high global vertex index.
         * @param v next vertex to be visited
         * @param vertexVisited list of vertices visited (1= visited, 0 = not)
         * @param path Set of vertices on current path, excluding v
         * @return number of edges deleted from v or on all unvisited vertices connected to v
         */
        private int checkNextVertex(int v, int [] vertexVisited,
                int firstv, int lastv, TreeSet<Integer> path){
            vertexVisited[v]=1;
            int nn=-1;
            int stub=-1; // global stub index
            int deleted=0;
            int kin = tg.getVertexInDegree(v);
            if (kin>1) {
                for (int e=0; e<kin; e++){
                    nn=tg.getVertexSourceQuick(v, e); // this is the e-th `anti'-neighbour of v going against the direction of edges
                    if (nn>=lastv || nn<firstv) continue;
                    stub = tg.getStubIn(v, e); // we have an edge from nn (source) to v (target) and
                    if (tg.getEdgeLabel(stub)<0) continue; // this edge is deleted
                    if (path.contains(nn)){ // there is a longer route from nn to v so kill this edge
                        tg.setEdgeLabelQuick(stub,DELETEDEDGE);
                        //if (infoOn) System.out.println(firstv+" tree - deleting "+nn+"->"+v+" (s,t)=("+tg.getVertexFromStub(stub)+","+tg.getOtherVertexFromStub(stub)+")");
                        deleted++;
                        }
                    }
            }

            path.add(v); // v is now last on path
            int kout = tg.getVertexOutDegree(v);
            for (int e=0; e<kout; e++){
                nn=tg.getVertexTargetQuick(v, e);
                if (vertexVisited[nn]!=0) continue; // done this vertex
                stub = tg.getStub(v, e);
                if (tg.getEdgeLabel(stub)<0) continue; // this edge is deleted
                deleted+=checkNextVertex(nn, vertexVisited, firstv, v, path);
            }
            path.pollLast(); // v must have been last index (highest)
            return deleted;
        }



//        /**
//         * Calculate maximum distances FROM v TO all other vertices.
//         * @see #maximumDistanceFromVertex(int, int)
//         * @param v distances from this vertex are returned
//         * @return array distance[target] which gives distance from vertex v to target. 0 if not reachable.
//         */
//        public int [] maximumDistanceFromVertex(int v){
//            int maxv=tg.getNumberVertices(); // the last index is this minus one
//            return maximumDistanceFromVertex(v, maxv);
//        }
//       /**
//         * Calculate maximum distances FROM v TO all other vertices upto given limit.
//         * <p>Assumes that graph vertices are ordered so that edges point
//         * from low to high numbered vertices.
//         * <p>If edge labels are on will test to see is these are set to deleted
//         * value.
//         * <p>Any vertices with index greater than maxv parameter are ignored.
//         * The vertex with maxv index is allowed.  This can be used to  limit
//         * consideration to a given causality cone
//         * @param v distances from this vertex are returned
//         * @param maxv largest vertex index allowed
//         * @return array distance[target] which gives distance from vertex v to target. 0 if not reachable.
//         */
//        public int [] maximumDistanceFromVertex(int v, int maxv){
//            if (!tg.isVertexInGraph(v)) throw new RuntimeException("maximumDistance vertex "+v+" is out of range");
//            boolean eLabel=false;
//            if (tg.isEdgeLabelled()) eLabel=true;
//            int [] distance = new int[numberVertices]; // require zeros initially
//            TreeSet<Integer> toVisit = new TreeSet();
//            toVisit.add(v);
//            int nn=-1;
//            int stub=-1; // global stub index
//            int currentv=v;
//            int kout=-1;
//            int dnn=-1;
//            /**
//             * Distance to vertex currentv plus one
//             */
//            int dvp1=-1;
//            while (toVisit.size()>0){
//              currentv=toVisit.first();
//              toVisit.remove(currentv);
//              kout = tg.getVertexDegree(currentv);
//              dvp1= distance[currentv]+1;
//              for (int e=0; e<kout; e++){
//                    nn=tg.getVertexTargetQuick(currentv, e); // nn is the e-th neighbour of v going with the direction of edges
//                    if (nn<=currentv) throw new RuntimeException("maximumDistance: graph is not ordered, first vertex "+v+", current vertex "+currentv+", neighbouring vertex "+nn);
//                    if (nn>maxv) continue; // outside range we wish to investigate
//                    if (eLabel) {stub = tg.getStub(currentv, e); // we have an edge from currentv (source) to nn (target) and
//                                 if (tg.getEdgeLabel(stub)==TransitiveReduction.DELETEDEDGE) continue; // this edge is deleted
//                    }
//                    dnn=distance[nn];
//                    if (dnn==0){ // not yet visited
//                        toVisit.add(nn);
//                        distance[nn]=dvp1;
//                    } else{ // found a route from v to currentv and then nn
//                        distance[nn]=Math.max(dnn, dvp1);
//                    }
//              } // eo for e
//            }
//            return distance;
//    }//eo maximumDistanceFromVertex
//
//         /**
//         * Calculate maximum distances FROM v TO all other vertices.
//         * @see #maximumDistanceFromVertex(int, int)
//         * @param v distances from this vertex are returned
//         * @return array distance[target] which gives distance from vertex v to target. 0 if not reachable.
//         */
//        public int [] maximumDistanceToVertex(int v){
//            int minv=-1; // the first vertex is 0 so this is always before that
//            return maximumDistanceToVertex(v, minv);
//        }
//      /**
//         * Calculate maximum distances TO v FROM all other vertices less than minv.
//         * <p>Assumes that graph vertices are ordered so that edges point
//         * from low to high numbered vertices.
//         * <p>If edge labels are on will test to see is these are set to deleted
//         * value.
//         * <p>Any vertices with index less than minv parameter are ignored.
//         * The vertex with minv index is allowed.  This can be used to limit
//         * consideration to a given causality cone.
//         * @param v distances to this vertex are returned
//         * @param minv minimum vertex index allowed
//         * @return array distance[target] which gives distance from target to vertex v. 0 if not reachable.
//         */
//        public int [] maximumDistanceToVertex(int v, int minv){
//            if (!tg.isVertexInGraph(v)) throw new RuntimeException("maximumDistance vertex "+v+" is out of range");
//            boolean eLabel=false;
//            if (tg.isEdgeLabelled()) eLabel=true;
//            int [] distance = new int[numberVertices]; // require zeros initially
//            TreeSet<Integer> toVisit = new TreeSet();
//            toVisit.add(v);
//            int nn=-1;
//            int stub=-1; // global stub index
//            int currentv=v;
//            int kin=-1;
//            int dnn=-1;
//            /**
//             * Distance to vertex currentv plus one
//             */
//            int dvp1=-1;
//            while (toVisit.size()>0){
//              currentv=toVisit.first();
//              toVisit.remove(currentv);
//              kin = tg.getVertexInDegree(currentv);
//              dvp1= distance[currentv]+1;
//              for (int e=0; e<kin; e++){
//                    nn=tg.getVertexSourceQuick(currentv, e); // this is the e-th `anti'-neighbour of currentv going with the direction of edges
//                    if (nn>=currentv) throw new RuntimeException("maximumDistanceFromVertex: graph is not ordered, first vertex "+v+", current vertex "+currentv+", neighbouring vertex "+nn);
//                    if (nn<minv) continue; // ignore vertices outside specified causality cone
//                    if (eLabel) {stub = tg.getStub(currentv, e); // we have an edge from nn (source) to currentv (target)
//                                 if (tg.getEdgeLabel(stub)==TransitiveReduction.DELETEDEDGE) continue; // this edge is deleted
//                    }
//                    dnn=distance[nn];
//                    if (dnn==0){ // not yet visited
//                        toVisit.add(nn);
//                        distance[nn]=dvp1;
//                    } else{ // found route from nn to currentv and then on to v
//                        distance[nn]=Math.max(dnn, dvp1);
//                    }
//              } // eo for e
//            }
//            return distance;
//    }//eo maximumDistanceFromVertex
//
//

//   /**
//    * Finds maximum distances between all vertices.
//    * It should not matter whether we use the from or to vertex routines
//    * as chosen by boolean option.  That is if we work from one vertex up
//    * or down the tree.
//    * @param fromOn if true (false) use from (to) vertex routine.
//    */
//    public DoubleMatrix2D  findDimensionsMaximumDistances(boolean fromOn){
//        DoubleMatrix2D  distanceMatrix = new SparseDoubleMatrix2D(numberVertices,numberVertices);
//        int [] distances;
//        // calculate all maximum distances and store in sparse matrix
//        for (int v=0; v<numberVertices; v++){
//             if (fromOn){
//                 distances = maximumDistanceFromVertex(v);
//                 for (int t=v; t<numberVertices; t++) distanceMatrix.setQuick(v, t, distances[t]);
//             } else
//             {
//                 distances = maximumDistanceToVertex(v);
//                 for (int s=0; s<v; s++) distanceMatrix.setQuick(s, v, distances[s]);
//             }
//        }
//        return distanceMatrix;
//    }
//   /**
//    * Finds maximum distances between all vertices.
//    * It should not matter whether we use the from or to vertex routines
//    * as chosen by boolean option.  That is if we work from one vertex up
//    * or down the tree.
//    * <p>Page 54, Meyer PhD thesis.  It is clear that the number of elements
//    * in the formula is the number of z in the interval
//    * source &lt; z &lt target <b>excluding</b> source and target.  It seems
//    * likely from reading around the equation that
//    * the number of chains is based on the same set of points, i.e.
//    * those in the interval but <b>excluding</b> source and target.  This is
//    * Meyer has implemented in his programme in Appendix B of his thesis.
//    * <p><b>Is this formula true if boundary points not at same spatial position?</b>
//    * @param source earliest vertex of interval
//    * @param target last vertex of interval (must be greater than source)
//    * @param distanceMatrix matrix of distances
//    */
//    public void calcDimension(int source, int target, DoubleMatrix2D  distanceMatrix){
//
//        // calculate dimensions for all pairs and store in sparse matrix
//        DoubleMatrix2D  dimensionMatrix = new SparseDoubleMatrix2D(numberVertices,numberVertices);
//        int numberTwoChains = 0;
//        int numberPoints = 0;
//        for (int v=source+1; v<target; v++) {
//             if ( (distanceMatrix.getQuick(source,v)>0) && (distanceMatrix.getQuick(v,target)>0) )
//             {
//                 numberPoints++;
//                 for (int u=source+1; u<v; u++){ // should u=v be allowed?
//                  if ( (distanceMatrix.getQuick(source,u)>0) && (distanceMatrix.getQuick(u,target)>0) )
//                        numberTwoChains++;
//                 }// eo for u
//            }
//        } // eo for v
//        if (numberTwoChains>0 && numberPoints>0) {
//            double dim = findDimension(numberTwoChains/(numberPoints*numberPoints));
//            dimensionMatrix.setQuick(source,target,dim);
//        }
//
//    }
//
//    /**
//     * Solves for dimension of Euclidean space.
//     * <p>The parameter is the ratio of the number of two chains to the
//     * square of the number of points.  Both of these are calculated for
//     * points WITHIN a given causal set interval x &lt;  y.
//     * That is the interval is the set of all points z where  x &lt; z &lt;  y#
//     * and the points used for the calculation of the two chains and number of
//     * points are those in the interval <b>excluding</b> the boundary points x and y.
//     * This is the interpretation of Meyer's thesis, p54, and especially the code
//     * in Appendix B, p103.
//     * <p>Based on equation (2) of D.Reid, PRD 67 (2003) 024034
//     * <p><b>Is this formula true if boundary points not at same spatial position?</b>
//     * @see http://commons.apache.org/math/userguide/analysis.html#a4.3_Root-finding
//     * @param ratio number of two chains over sqaure of number of points
//     * @return dimension estimate
//     */
//    static public double findDimension(double ratio){
//        UnivariateFunction function = new logMryheimMeyersDimensionFunction(ratio); // some user defined function object
//        final double relativeAccuracy = 1.0e-8;
//        final double absoluteAccuracy = 1.0e-6;
//        UnivariateSolver solver   = new IllinoisSolver(relativeAccuracy, absoluteAccuracy);
//        double d;
//        int maxEval=100;
//        double min=0.5;
//        double max=100.0;
//        try {
//           d = solver.solve(maxEval, function, min, max);
//        } catch (RuntimeException e) {
//          System.err.println("*** Error "+e);
//          return timgraph.DUNSET;
//            // Retrieve the x value.
//        }
//        return d;
//    }
//
//    /**
//     * Natural logarithm of Mryheim-Meyers Dimension function.
//     * <p>Based on equation (5) of D.Reid, PRD 67 (2003) 024034
//     * Gives the number of two chains divided by the square of the number of points
//     * in a given causal set interval x &lt;  y.  That is the set of all points
//     * z where  x &lt; z &lt;  y.
//     * @see http://commons.apache.org/math/userguide/special.html#a5.4_Beta_funtions
//     */
//    public static class logMryheimMeyersDimensionFunction implements UnivariateFunction {
//     static double lnratio=0.0;
//
//     public logMryheimMeyersDimensionFunction(double ratio){
//         lnratio=Math.log(ratio*8/3);
//     }
//
//     /**
//      * Value should be zero when d satisfies Mryheim-Meyers dimension.
//      * <p>The residual is ln(S2/(N^2))- ln(f(d))
//      * @param d dimension of space-time
//      * @return residual
//      */
//     public double value(double d) {
//         return lnratio-Math.log(d)-Beta.logBeta(d+1,d/2) ;
//     }
// }
//
//
////    /**
////     * Natural logarithm of Mryheim-Meyers Dimension function.
////     * <p>Based on equation (2) of D.Reid, PRD 67 (2003) 024034
////     * Gives the number of two chains divided by the square of the number of points
////     * in a given causal set interval x &lt;  y.  That is the set of all points
////     * z where  x &lt; z &lt;  y.
////     * @see http://commons.apache.org/math/userguide/special.html#a5.4_Beta_funtions
////     * @param d
////     * @return
////     */
////   static  public double logMryheimMeyersDimension(double d){
////        return Math.log(3*d/8)+Beta.logBeta(d+1,d/2) ;
////    }
//
////private static class LocalException extends RuntimeException {
////     // The x value that caused the problem.
////     private final double x;
////
////     public LocalException(double x) {
////         this.x = x;
////     }
////
////     public double getX() {
////         return x;
////     }
//// }
//
//
//
//    /**
//     * TODO!
//     * <p>Based on equation (2) of D.Reid, PRD 67 (2003) 024034
//     * @param numberTwoChains
//     * @return
//     */
//     public double calcDimension(int numberTwoChains, int numberPoints){
//
//         return 0.0;
//     }

//   /**
//    * Finds maximum distances between all vertices.
//    * It should not matter whether we use the from or to vertex routines
//    * as chosen by boolean option.  That is if we work from one vertex up
//    * or down the tree.
//    * @param fromOn if true (false) use from (to) vertex routine.
//    */
//        public void FileOutputMaximumDistances(boolean fromOn){
//        int [] distances;
//        PrintStream PS;
//        FileOutputStream fout;
//        FileNameSequence fileName= new FileNameSequence(tg.outputName);
//        fileName.setNameEnd("maxdist"+(fromOn?"from":"to")+".dat");
//        try {
//            System.out.println("Writing distances matrix to file "+ fileName.getFullFileName());
//            fout = new FileOutputStream(fileName.getFullFileName());
//            PS = new PrintStream(fout);
//            for (int v=0; v<numberVertices; v++){
//             distances = (fromOn?maximumDistanceFromVertex(v):maximumDistanceToVertex(v));
//             for (int t=0; t<(numberVertices-1); t++) PS.print(distances[t]+timgraph.SEP);
//                PS.println(distances[numberVertices-1]);
//             }//eo for v
//            try{ fout.close ();
//               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+", "+e.getMessage());}
//
//        } catch (FileNotFoundException e) {
//            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
//            return;
//        }
//        System.out.println("Finished writing distances matrix to file "+ fileName.getFullFileName());
//    }
//



     /**
      * Number of vertices.
      * @return number of vertices
      */
        public int getNumberVertices(){
            return numberVertices;
    }
        
}
