/*
* This is based on EdgePercolationClusterer of edu.uci.ics.jung.algorithms.cluster.
* That was copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California (All rights reserved) under the BSD license.
*
*/
package IslandNetworks.jungInterfaces;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.set.ListOrderedSet;

import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.ArchetypeGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberEdgeValue;
import edu.uci.ics.jung.utils.GraphUtils;

import IslandNetworks.islandNetwork;
import IslandNetworks.Edge.IslandEdgeSet;
//import IslandNetworks.Edge.EdgeTypeSelection;


/**
 * An algorithm for computing clusters community structure in graphs based on edge values above a given value.
 */
public class EdgePercolationClusterer {
    private int mNumEdgesToRemove;
    //private ArrayList mEdgesRemoved;
    private ListOrderedSet mEdgesRemoved;
    private Graph graph;
    int edgeVariableIndex=0;
    IslandEdgeSet edgeSet;
//    private String edgePecolationValueKey = "IN_EWKey"; // key for value to be used in percolation
    
//    private EdgePercolationRanker epr;
//    private static final String edgePecolationValueKey = "IN_EWKey"; 
    
//    * @param edgePecolationValueKeyInput string with the key used in JungConverter to access the value used in the percolation
//    public EdgePercolationClusterer (ArchetypeGraph g, String edgePecolationValueKeyInput) {

    /**
    * Constructs a new clusterer for the specified graph.
    * @param g the graph
    */
      public EdgePercolationClusterer (ArchetypeGraph g, islandNetwork inet) {      
       if (!(g instanceof Graph))
            throw new IllegalArgumentException("Argument must be of type Graph.");

        graph = (Graph)g; 
        edgeSet = inet.edgeSet;
        edgeVariableIndex=edgeSet.DisplayEdgeType.getValueIndex();
//        edgePecolationValueKey = edgePecolationValueKeyInput;
    }


        /**
         * Finds the set of clusters formed by projection to simple graph where 
         * link weights greater than specified value.
         * @param minedgeWeight the number of edges to be progressively removed from the graph
         */
    public ClusterSet extract(double minedgeWeight) {
        
        UserDatumNumberEdgeValue vEdgeID = new UserDatumNumberEdgeValue(JungConverter.EID_key);

        mEdgesRemoved = new ListOrderedSet ();
        //mEdgesRemoved = new ArrayList();        
        //HashSet hs = new HashSet();    
        // Can't work out how to convert set (needed for add and removing a set of edges)
        // to a list needed as jung clusterers return lists of edges removed
        
// find edges below required value and both record them and remove them.
        for (Iterator eIt = graph.getEdges().iterator(); eIt.hasNext();) {
            Edge e = (Edge) eIt.next();
            //int eid = vEdgeID.getNumber(e).intValue();
            if (edgeSet.getVariable(vEdgeID.getNumber(e).intValue(),edgeVariableIndex)  <minedgeWeight) {
                mEdgesRemoved.add(e);
            }
            }
      // now remove edges, can't do it while iterating through it  
//      GraphUtils gu = new GraphUtils();
      GraphUtils.removeEdges(graph, mEdgesRemoved);
        
// now evaluate cluster of graph with edges removed       
        WeakComponentClusterer wcSearch = new WeakComponentClusterer();
        ClusterSet clusterSet = wcSearch.extract(graph);
        
        
// now restore graph to original form
        GraphUtils.addEdges(graph, mEdgesRemoved);
        return clusterSet;
    }

    /**
     * Finds the set of clusters formed by projection to simple graph where link weights greater than specified fraction.
     * <bf>Currently not functional</bf>.
     *@param fraction fraction of rank to be retained, 0= all
     *@return a ClusterSet of the the diconnected communities after this process.
     */
    public ClusterSet extractByRank(double fraction) {
        // now evaluate cluster of graph with edges removed       
        WeakComponentClusterer wcSearch = new WeakComponentClusterer();
        ClusterSet clusterSet = wcSearch.extract(graph);
        return clusterSet ;
    }    
    
    /**
     * Retrieves the list of all edges that were removed (assuming extract(...) was previously called. The edges returned
     * are stored in order in which they were removed
     * @return a list of the edges in the original graph
     */
    public List getEdgesRemoved() {
        return mEdgesRemoved.asList();
    }
}
