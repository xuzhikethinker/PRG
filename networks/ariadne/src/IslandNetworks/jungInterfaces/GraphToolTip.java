/*
 * GraphToolTip.java
 *
 * Created on 06 December 2007, 17:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



package IslandNetworks.jungInterfaces;

import java.awt.event.MouseEvent;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.ToolTipFunction;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;

import IslandNetworks.Edge.IslandEdgeSet;
import IslandNetworks.Vertex.IslandSiteSet;

import TimUtilities.NumbersToString;
/**
 *
 * @author time
 */



public class GraphToolTip implements ToolTipFunction {
    
    IslandSiteSet sites;
    IslandEdgeSet edges;
    UserDatumNumberVertexValue siteKey;
    UserDatumNumberEdgeValue edgeKey;
    final static NumbersToString num2String = new NumbersToString();
    
//    public GraphToolTip(UserDatumNumberVertexValue siteID, IslandSite [] inputSites)
//    {
//        sites   = inputSites;
//        siteKey = siteID;
//    }
    
    public GraphToolTip(UserDatumNumberVertexValue siteID, IslandSiteSet inputSites, UserDatumNumberEdgeValue edgeID, IslandEdgeSet inputEdges)
    {
        sites   = inputSites;
        siteKey = siteID;
        edges = inputEdges;
        edgeKey = edgeID;
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.decorators.ToolTipFunction#getToolTipText(edu.uci.ics.jung.graph.Vertex)
     */
    public String getToolTipText(Vertex v) {
        
        int siteID = siteKey.getNumber(v).intValue();
        return sites.getName(siteID)+" "+num2String.toString(sites.getWeight(siteID),3);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.graph.decorators.ToolTipFunction#getToolTipText(edu.uci.ics.jung.graph.Edge)
     */
    public String getToolTipText(Edge e) {
        int edgeID = edgeKey.getNumber(e).intValue();
        return edges.toGraphToolTipString(edgeID, ": ", ", ", 3);
    }

    /* (non-Javadoc)
     * @see edu.uci.ics.jung.visualization.VisualizationViewer.ToolTipListener#getToolTipText(java.awt.event.MouseEvent)
     */
    public String getToolTipText(MouseEvent event) {
        return null;
    }

}