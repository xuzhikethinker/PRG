/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.algorithms;

import TimGraph.EdgeValue;
import TimGraph.timgraph;
import TimUtilities.FileUtilities.FileNameSequence;

/**
 *
 * @author time
 */
public class Projections {

    
    /**
     * Makes projection of input graph onto vertex partition.
     * <br>Each vertex of input graph is mapped to a vertex corresponding to its 
     * partition label. These labels must be an integer between 0 and  <code>numberPartitions</code>.
     * If a label is not present in <code>partition[]</code> a diconnected vertex will still be created.
     * Edges are mapped following the way their source and target vertices are mapped.
     * Multiple edges are merged. The weights of new edges being the sum of  weights of old edges.
     * <code>vertexEdgeListOn</code> is set for this projection to work.
     * <p>If the old graph is undirected then selfloops in the new graph coming 
     * from none self-loops in the old graph have their weights doubled.  
     * Self-loops in an undirected original graph add only their own weight (not 2x) to the new graph.
     * @param rootName new root for input and output file names.
     * @param oldtg existing timegraph to be deep copied
     * @param partition vector with vertex partition labels so <code>partition[v]</code> is partition of vertex <tt>v</tt>
     * @param numberPartitions <code>partition[v]</code> must be an integer between 0 and (numberPartitions-1) inclusive.
     * @param forceUndirected true if want to force undirected new copy
     */
    public static timgraph ontoVertexPartition(String rootName, timgraph oldtg, int [] partition, int numberPartitions, boolean forceUndirected, boolean makeUnweighted) 
    {
        timgraph tg = new timgraph();
        tg.infoLevel=oldtg.infoLevel;
        tg.setVertexEdgeList(true);
        tg.setDirectedGraph(oldtg.isDirected());
        if (forceUndirected) tg.setDirectedGraph(false);
        tg.setWeightedEdges(!makeUnweighted);
        tg.setVertexlabels(false);
        
        tg.inputName = new FileNameSequence(oldtg.inputName);
        tg.outputName = new FileNameSequence(oldtg.outputName);
        tg.setNameRoot(rootName);
        
        int maximumVertices = numberPartitions;
        int maximumEdges = 0;
        // Remember edges are really stubs
        if (tg.isDirected()) maximumEdges = maximumVertices *maximumVertices*2 ;   
        else maximumEdges = maximumVertices *(maximumVertices +1) ;   
        tg.setMaximumVertices(maximumVertices);
        tg.setMaximumStubs(maximumEdges);
        tg.setNetwork();
        
        // add vertices
        for (int v=0; v<numberPartitions; v++) tg.addVertex( ) ;
        
        // add edges
        int s=-1;
        int t=-1;
        int cs=-1;
        int ct=-1;
        double w=1;
        boolean oldIsWeighted = oldtg.isWeighted();
        for (int e=0; e<oldtg.getNumberStubs();e++) {
            s =oldtg.getVertexFromStub(e++);
            t =oldtg.getVertexFromStub(e);
            cs = partition[s];
            ct = partition[t];
            if (tg.isWeighted()) {
                w=(oldIsWeighted ? oldtg.getEdgeWeight(e) : 1);
                // Tadpoles in new graph come with double weight if they came from a
                // none self-loop in original graph
                if (!oldtg.isDirected() && (cs==ct) && (s!=t)) w=w+w; 
                tg.increaseEdgeWeight(cs,ct,w);                
            }
            else tg.addEdgeUnique(cs,ct);
        }
        return tg;
    }

   /**
     * Consolidates all multiple links into one.
     * <br>If this is a directed graph and an undirected graph is being made 
     * then the new link has the sum of the weight in both directions.
     * If this starts from an unweighted graph, then edges are assumed to have weight one.
     * <br>Output is always a weighted graph.
     * @param intg input timgraph 
     * @param forceUndirected true if want to force undirected new copy, otherwise will copy status of input graph.
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     */
    static public timgraph makeConsolidated(timgraph intg, boolean forceUndirected, boolean makeLabelled, boolean makeVertexEdgeList)   {
        timgraph tg = new timgraph();
        tg.infoLevel = intg.infoLevel;
        tg.setVertexEdgeList(makeVertexEdgeList);
        tg.setDirectedGraph(intg.isDirected());
        if (forceUndirected) {
            tg.setDirectedGraph(false);
        }
        tg.setWeightedEdges(true); // first need to sum up all weights
        tg.setVertexlabels(makeLabelled);

        tg.inputName = new FileNameSequence(intg.inputName);
        tg.outputName = new FileNameSequence(intg.outputName);
        
        tg.setMaximumVertices(intg.getNumberVertices());
        
        tg.setMaximumStubs(intg.getNumberStubs());
        
        tg.setNetwork();

        // add vertices
        for (int v = 0; v < tg.getMaximumVertices(); v++) {
           if (tg.isVertexLabelled()) {
               if (intg.isVertexLabelled()) tg.addVertex( intg.getVertexLabel(v));
                       else tg.addVertex("v"+v, v);
           }
           else tg.addVertex();
        }

        // find old edges, and then make a single consolidated edge in new weighted graph
        
        int s=-1;
        int t=-1;
        int ns=-1;
        int nt=-1;
        double dw=-1;
        for (int e = 0; e < intg.getNumberStubs(); e++) {
                if (intg.isWeighted()) dw = intg.getEdgeWeight(e); else dw=1;
                s=intg.getVertexFromStub(e++);
                t=intg.getVertexFromStub(e);
                if (forceUndirected &&  (s>t)) tg.increaseEdgeWeight(t, s, dw);
                else tg.increaseEdgeWeight(s,t,dw);
        }
   return tg;
    } // eo weight cut projection
        
    /**
     * Consolidates all multiple links into one.
     * <br>If this is a directed graph and an undirected graph is being made 
     * then the new link has the sum of the weight in both directions.
     * If this starts from an unweighted graph, then edges are assumed to have weight one.
     * <br>If an unweighted output graph is requested then this is done 
     * by applying a weight cut to resulting graph with no mulitple edges.
     * @param minWeight minimum weight needed for an edge to be retained if undirected output graph requested.
     * @param forceUndirected true if want to force undirected new copy, otherwise will copy status of input graph.
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     */
    static public timgraph makeConsolidated(timgraph intg, double minWeight,
            boolean forceUndirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList)   {
        timgraph tg=makeConsolidated(intg, forceUndirected, makeLabelled, makeVertexEdgeList);
        if (makeWeighted) return tg;
        return tg.makeUnweighted(minWeight, forceUndirected, makeLabelled, makeVertexEdgeList);    
    } // eo weight cut projection

   /**
     * Projects onto a graph where all links have a minimum weight.
     * <br>Each edge of input graph is retained in the output graph
     * only if its weight is above a critical value specified.
     * <br>All vertices are retained, even if they have no edges after the projection.
     * <br>If this is a directed graph and an undirected graph is being made
     * then the new link has the sum of the weight in both directions.
     * Should not start from an unweighted graph, null is returned.
     * Multiedges (multiple edges between same vertex pairs) are not
     * consolidated, use  <tt>makeConsolidated</tt>.
     * minimum weight specified in weighted graph.
     * No changes made to directionality of graph.
     * @param minWeight minimum weight in input graph needed for an edge to be copied to output graph..
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     */
    static public timgraph minimumEdgeWeight(timgraph intg, double minWeight,  boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList)   {
        if (!intg.isWeighted()) {
            System.err.println("*** Projections.minimumEdgeWeight requires input graph to be weighted but is wasn't.");
            return null;
        }
        timgraph tg = new timgraph();
        tg.infoLevel = intg.infoLevel;
        tg.setVertexEdgeList(makeVertexEdgeList);
        tg.setDirectedGraph(intg.isDirected());
        tg.setWeightedEdges(makeWeighted);
        tg.setVertexlabels(makeLabelled);

        tg.inputName = new FileNameSequence(intg.inputName);
        tg.outputName = new FileNameSequence(intg.outputName);

        tg.setMaximumVertices(intg.getNumberVertices());

        tg.setMaximumStubs(intg.getNumberStubs());

        tg.setNetwork();

        // add vertices
        for (int v = 0; v < tg.getMaximumVertices(); v++) {
           if (tg.isVertexLabelled()) {
               if (intg.isVertexLabelled()) tg.addVertex( intg.getVertexLabel(v));
                       else tg.addVertex("v"+v, v);
           }
           else tg.addVertex();
        }

        // copy old edges if weight exceeds minimum
        int s=-1;
        int t=-1;
        EdgeValue ew;
        for (int e = 0; e < intg.getNumberStubs(); e++) {
                ew = intg.getEdgeWeightAll(e);
                if (ew.getWeight()<minWeight) continue;
                s=intg.getVertexFromStub(e++);
                t=intg.getVertexFromStub(e);
                if (makeWeighted) tg.addEdge(s,t, ew);
                else tg.addEdge(s,t);
        }

   return tg;
    } // eo weight cut projection

    
}
