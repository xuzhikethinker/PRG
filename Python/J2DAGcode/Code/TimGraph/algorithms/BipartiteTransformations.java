/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.algorithms;
import TimGraph.VertexLabel;
import TimGraph.timgraph;
import java.util.TreeMap;
/**
 * Set of transformation algorithms on bipartite graphs
 * @author time
 */
public class BipartiteTransformations {

    public BipartiteTransformations (){}
    
    /** Project bipartite graph onto one or other set of vertices.
     * <br>Weights are set by dividing by the number of neighbours
     * to ensure the strength of projected graph vertices is always one for each projected vertex
     * of degree 2 or more the retained vertices are connected to.
     * No mulitedges so there is only one edge per vertex pair in the projected graph.
     * If <tt>noSelfLoops<tt> is false then a
     * self loop is also added to represent the incidence of an edge with itself and
     * then degree one projected vertices galso have some representation in the projected graph.
     * <br>Does not create ggraph with multiple edges.
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     * @param noSelfLoops true (false) if don't (do) want self loops in projection
     */
    static public timgraph project(timgraph tg, boolean ontoTypeOne,  
        boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList, boolean noSelfLoops){
        
        timgraph ng = new timgraph();
        ng.initialiseSomeParameters(tg.inputName.getNameRoot(), tg.inputName.getDirectoryRoot(), tg.infoLevel, tg.outputControl.getNumber());
        ng.setNameRoot(tg.inputName.getNameRoot());
 
       boolean directed = false;
       ng.setGraphProperties(directed, (makeLabelled && tg.isVertexLabelled()), makeWeighted, makeVertexEdgeList);
       
       int nv=-1;
       int firstVertex=-1;
       int lastVertex=-1;
       int firstProjectedVertex=-1;
       int lastProjectedVertex=-1;
       if (ontoTypeOne){
           nv = tg.getNumberVerticesType1();
           firstVertex=0;
           lastVertex=nv;
           firstProjectedVertex=nv;
           lastProjectedVertex=tg.getNumberVertices();
       } 
       else
       {
           nv = tg.getNumberVerticesType2();
           firstVertex=tg.getNumberVerticesType1();
           lastVertex=tg.getNumberVertices();
           firstProjectedVertex=0;
           lastProjectedVertex=firstVertex;  
       }
       int ns=0;// number of new stubs
       for (int v=firstProjectedVertex; v<lastProjectedVertex;v++){
           int k=tg.getVertexDegree(v);
           ns+=k*(k-1);
       }
       if (tg.infoLevel>-1) System.out.println("Porjected graph has  "+nv+" vertices and "+ns+" stubs");
       ng.setNetwork(nv, ns); 
       // first add new vertices 
       for (int v=firstVertex; v<lastVertex;v++){
           if (ng.isVertexLabelled()) {
               if (tg.isVertexLabelled()) ng.addVertex( tg.getVertexLabel(v));
                       else ng.addVertex("v"+v, v);
           }
           else ng.addVertex();
           
       }
       // now add edges where edges incident ot other vertex type
       int s=-1;
       int t=-1;
       int ve=-1;
       int nsl=(noSelfLoops?1:0); // =1 if no self-loops in projection, =0 if allowed. 
       double w=1;
       //if (!tg.isVertexEdgeListOn()) throw new RuntimeException("*** BipartiteTransformations.project requires a vertex edge list");
       for (int v=firstProjectedVertex; v<lastProjectedVertex;v++){
           int k=tg.getVertexDegree(v);
           if (k<1+nsl) continue;
           if (makeWeighted) w = 1.0/((double) (k-nsl));
           for (int e1=0; e1<k; e1++){
               s = tg.getVertexTarget(v, e1)-firstVertex;
               for (int e2=e1+nsl; e2<k; e2++){
                   t = tg.getVertexTarget(v, e2)-firstVertex;
                   if (makeWeighted) ng.increaseEdgeWeight(s,t,w);
                   else ng.addEdgeUnique(s,t);
               }
           }
       }
       return ng;
        
    }

   /** Randomise bipartite graph.
     * <br>Switches edges while maintaining degree of vertices.  Maintains
     * edge weights and the bipartite structure.
    * @param tg bipartite graph to be randomised
    * @param addToNameRoot string to add to old name root
     */
    static public timgraph randomise(timgraph tg, String addToNameRoot){

        if (!tg.isBipartite()) throw new RuntimeException("Must randomise a bipartite graph");
        if (tg.isDirected()) throw new RuntimeException("Can not randomise a directed bipartite graph yet");
        boolean makeUndirected=false;
        boolean makeUnlabelled=false;
        boolean reverseDirection=false;
        timgraph ng = new timgraph(tg, 0,0, makeUndirected, makeUnlabelled, reverseDirection);
        ng.addToNameRoot(addToNameRoot);
        int n1=ng.getNumberVerticesType1();
        int n2=ng.getNumberVerticesType2();
        int s1=-1;
        int s2=-1;
        int e2=-1;
        int nstubs=ng.getNumberStubs();
        for (int e=0; e<nstubs; e++){
            s1 = ng.getStubType1(e);
            if (s1<0) throw new RuntimeException("Can't find type one stub from edge "+e);
            s2=s1;
            while (s1==s2) {e2=ng.Rnd.nextInt(nstubs); s2 = ng.getStubType1(e2);}
            if (s2<0) throw new RuntimeException("Can't find type two stub from edge "+e2);
            ng.rewireEdgePair(s1,s2);
        }

        return ng;

    }


    /**
     * Combines two bipartite graphs using a common set of vertices.
     * <p>Uses the names in the labels of the common vertices which must therefore be unique.
     * Bipartite grasph is weighted.
     * @param tg1 first graph, non common vertices will be type one vertices of new graph
     * @param tg2 second graph, non common vertices will be type two vertices of new graph
     * @param typeOneIsCommonIn1 true (false) if the type one (two) vertices of first graph are the common vertices
     * @param typeOneIsCommonIn2 true (false) if the type one (two) vertices of second graph are the common vertices
     * @param newNameRoot name root to use for new graph files
     * @param newDirectoryRoot directory root to use for new graph files
     * @param infoLevel information level for new graph
     * @param outputControlNumber output control number for new graph
     * @param makeLabelled true if want to carry labels over from old graph
     * @param makeVertexEdgeList
     * @return timgraph of combined graph
     */
     static public timgraph merge(timgraph tg1, timgraph tg2, boolean typeOneIsCommonIn1, boolean typeOneIsCommonIn2,  
             String newNameRoot, String newDirectoryRoot, 
             int  infoLevel, int outputControlNumber,
             boolean makeLabelled, boolean makeVertexEdgeList){
     
       if (!tg1.isBipartite()) throw new RuntimeException("First graph must be bipartite");
       if (!tg2.isBipartite()) throw new RuntimeException("Second graph must be bipartite");
       if (!tg1.isVertexLabelled()) throw new RuntimeException("First graph must have vertex labels");
       if (!tg2.isVertexLabelled()) throw new RuntimeException("Second graph must have vertex labels");

       
        timgraph ng = new timgraph();
        ng.initialiseSomeParameters(newNameRoot, newDirectoryRoot, infoLevel+10, outputControlNumber);
        ng.setNameRoot(newNameRoot);
 
       boolean directed = false;
       ng.setGraphProperties(directed, makeLabelled , true, makeVertexEdgeList);
       
       // set up first as if type two vertices in old graph one are the common vertices
       int n1=tg1.getNumberVerticesType1(); // number of vertices of type one in new graph
       String name1 = tg1.getNameVerticesType1();
       int nCommon1=tg1.getNumberVerticesType2(); // number of vertices of type one in new graph
       int commonVertexOffset1 = n1; // index of first common vertex in old graph 1
       int typeOneVertexOffset = 0; // index of first vertex in old graph 1 which is a type 1 vertex in new graph
       if (typeOneIsCommonIn1){
           nCommon1=n1;
           n1=tg1.getNumberVerticesType2();
           name1 = tg1.getNameVerticesType2();
           typeOneVertexOffset = commonVertexOffset1;
           commonVertexOffset1 = 0;
       }
       // now build a map for fast transformation from common vertex name to global id in old graph 1
       TreeMap<String,Integer> commonOne = new TreeMap<String,Integer>(); // map from key common vertex name to global vertex id in first graph
       for (int v=0; v<nCommon1; v++){
           int vvv=v+commonVertexOffset1;
           VertexLabel vl = tg1.getVertexLabel(vvv);
           commonOne.put(vl.getName(), vvv);
       }

       int n2=tg2.getNumberVerticesType1(); // number of vertices of type two in new graph
       String name2 = tg2.getNameVerticesType1();
       int nCommon2=tg2.getNumberVerticesType2(); // number of vertices of type one in new graph
       int commonVertexOffset2 = n2; // index of first common vertex in old graph 2
       int typeTwoVertexOffset = 0; // index of first vertex in old graph 1 which is a type 2 vertex in new graph
       if (typeOneIsCommonIn2){
           nCommon2=n2;
           n2=tg2.getNumberVerticesType2();
           name2 = tg2.getNameVerticesType2();
           typeTwoVertexOffset = commonVertexOffset2;
           commonVertexOffset2 = 0;
       }
       // now build a map for fast transformation from common vertex name to global id in old graph 2
       TreeMap<String,Integer> commonTwo = new TreeMap<String,Integer>(); // map from key common vertex name to global vertex id in second graph
       for (int v=0; v<nCommon2; v++){
           int vvv=v+commonVertexOffset2;
           VertexLabel vl = tg2.getVertexLabel(vvv);
           commonTwo.put(vl.getName(), vvv);
       }

       // calculate number of edges
       int vc1=-1;
       int vc2=-1;
       Integer temp;
       int ne=0;
       for (String c:commonOne.keySet()){
           temp=commonOne.get(c);
           if (temp==null) continue;
           vc1=temp;
           temp=commonTwo.get(c);
           if (temp==null) continue;
           vc2=temp;
           int k1=tg1.getVertexDegree(vc1);
           int k2=tg2.getVertexDegree(vc2);
           if ((k1==0) || (k2==0)) continue;
           ne+=k1*k2;
       }

       System.out.println("*** Merged graph has "+(n1+n2)+" vertices and at most "+ne+" edges ");
       //       int n2=(typeOneIsCommonIn2?tg2.getNumberVerticesType2():tg2.getNumberVerticesType1());
       ng.setBipartite(n1,  n2, name1, name2);
       ng.setNetwork(n1+n2, ne*2);
        
       // add new type one vertices
       if (makeLabelled) for (int v=0; v<n1; v++){
           ng.addVertex(tg1.getVertexLabel(v+typeOneVertexOffset));
       }
       else for (int v=0; v<n1; v++)ng.addVertex();
       
       // add new type two vertices
       if (makeLabelled) for (int v=0; v<n2; v++){
           ng.addVertex(tg2.getVertexLabel(v+typeTwoVertexOffset));
       }
       else for (int v=0; v<n2; v++)ng.addVertex();
       
       //now build edges by finding the common vertices  
       vc1=-1;
       vc2=-1;
       int v1=-1;
       int v2=-1;
       double dw=0;
       for (String c:commonOne.keySet()){
           temp=commonOne.get(c);
           if (temp==null) continue;
           vc1=temp;
           temp=commonTwo.get(c);
           if (temp==null) continue;
           vc2=temp;
           int k1=tg1.getVertexDegree(vc1);
           int k2=tg2.getVertexDegree(vc2);
           if ((k1==0) || (k2==0)) continue;
           dw=1.0/(k1*k2);
           for (int el1=0; el1<k1; el1++){
            for (int el2=0; el2<k2; el2++){
              v1 = tg1.getVertexTarget(vc1, el1);
              v2 = tg2.getVertexTarget(vc2, el2);
              ng.increaseEdgeWeight(v1-typeOneVertexOffset, v2+n1-typeTwoVertexOffset,dw);
            }
           }
           
       }
       
         return ng;
     }

}

