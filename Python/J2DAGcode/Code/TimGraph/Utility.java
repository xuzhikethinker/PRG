/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph;

import TimUtilities.FileUtilities.FileNameSequence;


/**
 *
 * @author time
 */
public class Utility {

    
   /**
     * Makes Line graph (dual projection) from input graph.
     * <br>Each edge of input graph is mapped to a vertex. These new vertices are
     * connected if the edges in the old graph have a source and target vertex in the old graphj in
     * common.  Thus one vertex in the old graph is mapped to the many edges of a clique (complete sub graph)
     * in the new graph.  The weighting of each edge is to be decided.
     * <code>vertexEdgeListOn</code> is set for this projection to work?
     * Does not automatically edge self loop for undirected graphs since then the 
     * same edge is both and incoming and outgoing stub for one vertex.
     * <br>The vertex index <tt>v</tt> of the line graph corresponds to the egd with indices 
     * <tt>(2v)</tt> and <tt>(2v+1)</tt>in the original input graph <tt>oldtg</tt> 
     * <br><tt>useSelfLoops</tt> is true if we want undirected graphs to create self-loops 
     * for all edges because and edge alpha is both in coming and outgoing to the vertex i.
     * If a weighted line graph version is being created then weights changed accordingly.
     * <br>NOTE ignores weights of old graph.
     * @param oldtg existing timegraph to be deep copied
     * @param makeUndirected true if want to force undirected new copy
     * @param useSelfLoops true (false) if want undirected graphs to create self loops for all edges.
     * @param makeUnlabelled UNUSED (true if want to force unlabelled new copy)
     * @param reverseDirection UNUSED (true if want to reverse direction of edges)
     */
    static public timgraph makeLineGraph(timgraph oldtg, boolean makeUndirected, boolean makeUnweighted, 
                    boolean useSelfLoops, boolean makeUnlabelled, boolean reverseDirection) {
        if (!oldtg.isVertexEdgeListOn()) {
            System.err.println("*** timgraph Line Graph constructor needs vertexEdgeList to be on.");
        }
        timgraph tg = new timgraph();
        tg.infoLevel = oldtg.infoLevel;
        tg.setVertexEdgeList(true);
        tg.directedGraph = oldtg.directedGraph;
        if (makeUndirected) {
            tg.directedGraph = false;
        }
        tg.weightedEdges = (!makeUnweighted);
        tg.vertexlabels = false;

//        inputNameRoot=oldtg.inputNameRoot;
//        inputDirName=oldtg.inputDirName;
//        inputExtension=oldtg.inputExtension;
//        outputNameRoot=oldtg.outputNameRoot;
//        outputDirName=oldtg.outputDirName;
//        outputExtension=oldtg.outputExtension;
        tg.inputName = new FileNameSequence(oldtg.inputName);
        tg.outputName = new FileNameSequence(oldtg.outputName);
        
        int noselfloops = 1;
        if (useSelfLoops) noselfloops=0;
        
        
        tg.setMaximumVertices(oldtg.getNumberStubs() /2); // remember this is the stub number
        // Remember edges are really stubs
        int maximumEdges = 0;
        if (tg.directedGraph) {
            for (int v = 0; v < oldtg.getNumberVertices(); v++) {
                maximumEdges += oldtg.getVertexInDegree(v) * oldtg.getVertexOutDegree(v);
            }
        } else {
            for (int v = 0; v < oldtg.getNumberVertices(); v++) {
                int k = oldtg.getVertexDegree(v);
                maximumEdges += k * (k - noselfloops) / 2;
            }
        }

        tg.setMaximumStubs(maximumEdges*2); // remember that this is really a stub number
        
        tg.setNetwork();

        // add vertices
        for (int v = 0; v < tg.getMaximumVertices(); v++) {
            tg.addVertex();        // add edges
        }
        
        // add edges
        int e1 = -1;
        int e2 = -1;
        double w = 1;
        //EdgeWeight w = new EdgeWeight();
        if (oldtg.isDirected()) { // directed old graph
           int kin = -1;
           int kout = -1;
            for (int v = 0; v < oldtg.getNumberVertices(); v++) {
                kin = oldtg.getVertexDegree(v);
                if (kin < 1) continue;
                kout = oldtg.getVertexDegree(v);
                if (kout < 1) continue;
                if (tg.weightedEdges) w = 1.0 / Math.sqrt((double) kin*kout);
                for (int ei = 0; ei < oldtg.vertexInEdgeList[v].size(); ei++) {
                    e1 = oldtg.vertexInEdgeList[v].get(ei);
                    for (int eo = 0; eo < oldtg.vertexEdgeList[v].size(); eo++) {
                      e2 = oldtg.vertexEdgeList[v].get(eo);
                      if (tg.weightedEdges) tg.increaseEdgeWeight(e1/2, e2/2, w); // note that e and e2 are really stub indices
                      else tg.addEdgeUnique(e1/2, e2/2);
                    }// eo for eo
                } //eo for ei
            } //eo for e
        }// eo old directed case
        else { // undirected old graph
            int kv = -1;
            for (int v = 0; v < oldtg.getNumberVertices(); v++) {
                kv = oldtg.getVertexDegree(v);
                if (kv < 1+noselfloops) continue;
                if (tg.weightedEdges) w = 1.0 / (kv - noselfloops);
                for (int ei = 0; ei < oldtg.vertexEdgeList[v].size(); ei++) {
                    e1 = oldtg.vertexEdgeList[v].get(ei);
                    for (int eo = ei+noselfloops; eo < oldtg.vertexEdgeList[v].size(); eo++) {
                      e2 = oldtg.vertexEdgeList[v].get(eo);
                      if (tg.weightedEdges) tg.increaseEdgeWeight(e1/2, e2/2, w); // note that e and e2 are really stub indices
                      else tg.addEdgeUnique(e1/2, e2/2);
                    }// eo for eo
                } //eo for ei
            } //eo for e
    } // eo old undirected case
        
    return tg;    
    } // eo dual constructor

}
