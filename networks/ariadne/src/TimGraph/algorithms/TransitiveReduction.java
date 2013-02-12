/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.algorithms;

import TimGraph.timgraph;
import TimUtilities.FileUtilities.FileNameSequence;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

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



        /**
         * Calculate maximum distances from v to all other vertices.
         * <p>Assumes that graph vertices are ordered so that edges point
         * from low to high numbered vertices.
         * <p>If edge labels are on will test to see is these are set to deleted 
         * value.
         * @param v distances from this vertex are returned
         * @return array distance[target] which gives distance from vertex v to target. 0 if not reachable. 
         */
        public int [] maximumDistance(int v){
            if (!tg.isVertexInGraph(v)) throw new RuntimeException("maximumDistance vertex "+v+" is out of range");
            boolean eLabel=false;
            if (tg.isEdgeLabelled()) eLabel=true;
            int [] distance = new int[numberVertices]; // require zeros initially
            TreeSet<Integer> toVisit = new TreeSet();
            toVisit.add(v);
            int nn=-1;
            int stub=-1; // global stub index
            int currentv=v;
            int kout=-1;
            int dnn=-1;
            int dvp1=-1;
            while (toVisit.size()>0){
              currentv=toVisit.first();
              toVisit.remove(currentv);
              kout = tg.getVertexDegree(currentv);
              dvp1= distance[currentv]+1;
              for (int e=0; e<kout; e++){
                    nn=tg.getVertexTargetQuick(currentv, e); // this is the e-th `anti'-neighbour of v going against the direction of edges
                    if (nn<=currentv) throw new RuntimeException("maximumDistance: graph is not ordered, first vertex "+v+", current vertex "+currentv+", neighbouring vertex "+nn);
                    if (eLabel) {stub = tg.getStub(currentv, e); // we have an edge from nn (source) to v (target) and
                                 if (tg.getEdgeLabel(stub)==TransitiveReduction.DELETEDEDGE) continue; // this edge is deleted
                    }
                    dnn=distance[nn];
                    if (dnn==0){ // not yet visited
                        toVisit.add(nn);
                        distance[nn]=dvp1;
                    } else{
                        distance[nn]=Math.max(dnn, dvp1);
                    }
              } // eo for e
            }
            return distance;
    }//eo maximumDistance


   public void findMaximumDistances(){
        int [] distances;
        PrintStream PS;
        FileOutputStream fout;
        FileNameSequence fileName= new FileNameSequence(tg.outputName);
        fileName.setNameEnd("maxdist.dat");
        try {
            System.out.println("Writing distances matrix to file "+ fileName.getFullFileName());
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            for (int v=0; v<numberVertices; v++){
             distances = maximumDistance(v);
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
      * Number of vertices.
      * @return number of vertices
      */
        public int getNumberVertices(){
            return numberVertices;
    }
        
}
