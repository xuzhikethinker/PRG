/*
 * IslandTimGraph.java
 *
 * Created on 11 December 2006, 15:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import TimGraph.timgraph;
import TimGraph.VertexLabel;
import TimUtilities.TimMessage;
import java.util.Random; //p524 Schildt


/**
 * Creates a representaiton of the IslandNetwork as a TimGraph.
 * @author time
 */
public class IslandTimGraph {
    
    timgraph tg;
    int [] siteLastVertex;  // siteLastVertex[s]  = number of timgraph vertex +1 corresponding to last vertex of site s
    Random rnd = new Random();
    TimMessage message = new TimMessage(0);
    String SEP="\t";
    
    /** Creates a new instance of IslandTimGraph */
    public IslandTimGraph() {
    }
    
    public void NetworkToTimGraph(int numberVertices, int numberInternalSiteEdges, int numberExternalSiteEdges, islandNetwork inet)
    {
        double indVertexWeight = inet.totVertexWeight/numberVertices; // site weight per vertex in timgraph
        double intEdgeWeight = inet.totVertexWeight/numberInternalSiteEdges; // site weight per internal edge in timgraph
//        double extEdgeWeight = inet.totEdgeWeight/numberExternalSiteEdges; // site weight per internal edge in timgraph
        int maxVertices = 2*inet.numberSites+numberVertices;  // allow for some rounding errors
        int maxEdges = numberInternalSiteEdges + numberExternalSiteEdges+2*inet.numberSites;
        String dname="";
        boolean makeDirected=true;
        boolean makeLabelled=true;
        boolean makeWeighted=false;
        tg = new timgraph(inet.outputFile.getNameRoot(), dname, 0, 0, makeDirected, makeLabelled, makeWeighted, maxVertices, maxEdges);
        siteLastVertex = new int[inet.numberSites];
        for (int s=0; s<inet.numberSites; s++) 
        {
            int n = (int) (inet.siteArray[s].getWeight()/indVertexWeight + 0.5);
            for (int v=0; v<n; v++) tg.addVertex(new VertexLabel (s) );
            siteLastVertex[s]=tg.getNumberVertices();
        }
        int minSource =0; // lowest number of timgraph vertex associated with source site
        int minTarget=0; // lowest number of timgraph vertex associated with target site
        for (int s=0; s<inet.numberSites; s++)
        {
         // add internal site edges
            double weight = inet.siteArray[s].getWeight();
            int intEdges = (int) (weight/intEdgeWeight +0.5);
            int nsv = siteLastVertex[s]-minSource;  // number of vertices associated with source site
            message.println(0,inet.siteArray[s].name+SEP+s+SEP+nsv+SEP+intEdges);
            for (int e=0;e<intEdges; e++) tg.addEdge(minSource+rnd.nextInt(nsv),minSource+rnd.nextInt(nsv));
         // add intersite edges
            minTarget =0;
            for (int t=0; t<inet.numberSites; t++)
               {
                int ntv = siteLastVertex[t]-minTarget;  // number of vertices associated with target site
                if (s==t) continue;
                int extEdges = (int) (inet.edgeSet.getEdgeValue(s,t)*weight/intEdgeWeight +0.5);
                for (int e=0;e<extEdges; e++) tg.addEdge(minSource+rnd.nextInt(nsv),minTarget+rnd.nextInt(ntv));
                
                minTarget=siteLastVertex[t];
                } 
            
            minSource = siteLastVertex[s];
        }// eo for s
        
    }
    
}
