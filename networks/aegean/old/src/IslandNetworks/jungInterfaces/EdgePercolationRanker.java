/*
* Based on edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California (All rights reserved) under the BSD license.
*/
package IslandNetworks.jungInterfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

//import org.apache.commons.collections.Buffer;
//import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Element;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Decorator;
import edu.uci.ics.jung.graph.decorators.NumericDecorator;
import edu.uci.ics.jung.utils.MutableDouble;
import edu.uci.ics.jung.utils.PredicateUtils;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.utils.UserDataUtils;

import edu.uci.ics.jung.algorithms.importance.AbstractRanker;

/**
 * Computes betweenness centrality for each vertex and edge in the graph. The result is that each vertex
 * and edge has a UserData element of type MutableDouble whose key is 'centrality.BetweennessCentrality'.
 * Note: Many social network researchers like to normalize the betweenness values by dividing the values by
 * (n-1)(n-2)/2. The values given here are unnormalized.<p>
 *
 * A simple example of usage is:
 * <pre>
 * BetweennessCentrality ranker = new BetweennessCentrality(someGraph);
 * ranker.evaluate();
 * ranker.printRankings();
 * </pre>
 *
 * Running time is: O(n^2 + nm).
 * @see "Ulrik Brandes: A Faster Algorithm for Betweenness Centrality. Journal of Mathematical Sociology 25(2):163-177, 2001."
 * @author Scott White
 */
 
 

public class EdgePercolationRanker extends AbstractRanker {

    public static final String EDGEWEIGHT = "IN_EWKey"; 
    //EDGEWEIGHT.EdgePercolationRanker

    /**
     * Constructor which initializes the algorithm
     * @param g the graph whose nodes are to be analyzed
     */
    public EdgePercolationRanker(Graph g) {
        initialize(g, true, true);
    }

    public EdgePercolationRanker(Graph g, boolean rankNodes) {
        initialize(g, rankNodes, true);
    }

    public EdgePercolationRanker(Graph g, boolean rankNodes, boolean rankEdges)
    {
        initialize(g, rankNodes, rankEdges);
    }
    
//    protected void computeBetweenness(Graph graph) {    }

    private void initializeData(Graph g, BetweennessDataDecorator decorator) {
        for (Iterator vIt = g.getVertices().iterator(); vIt.hasNext();) {
            Vertex vertex = (Vertex) vIt.next();

            if (vertex.getUserDatum(EDGEWEIGHT) == null) {
                vertex.addUserDatum(EDGEWEIGHT, new MutableDouble(), UserData.SHARED);
            }

            decorator.setData(new BetweennessData(), vertex);
        }
        for (Iterator eIt = g.getEdges().iterator(); eIt.hasNext();) {
            Edge e = (Edge) eIt.next();

            if (e.getUserDatum(EDGEWEIGHT) == null) {
                e.addUserDatum(EDGEWEIGHT, new MutableDouble(), UserData.SHARED);
            }
        }
    }

    /**
     * the user datum key used to store the rank scores
     * @return the key
     */
    public String getRankScoreKey() {
        return EDGEWEIGHT;
    }

/**
 * Does not do anything as assumes graph edge weights already set.
 */
    protected double evaluateIteration() {
//        computeBetweenness(getGraph());
        return 0;
    }

    class BetweennessDataDecorator extends Decorator {
        public BetweennessDataDecorator() {
            super("EDGEWEIGHT.BetwennessData", UserData.REMOVE);
        }

        public BetweennessData data(Element udc) {
            return (BetweennessData) udc.getUserDatum(getKey());
        }

        public void setData(BetweennessData value, Element udc) {
            udc.setUserDatum(getKey(), value, getCopyAction());
        }

    }

    class BetweennessData {
        double distance;
        double numSPs;
        List predecessors;
        double dependency;

        BetweennessData() {
            distance = -1;
            numSPs = 0;
            predecessors = new ArrayList();
            dependency = 0;
        }
    }
}
