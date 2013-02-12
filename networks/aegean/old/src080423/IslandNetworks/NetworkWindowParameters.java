/*
 * NetworkWindowParameters.java
 * Passes parameters
 * Created on 05 October 2007, 00:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;



/**
 *
 * @author time
 */
public class NetworkWindowParameters {
    
    String layoutTypeString="Circular";
    String clusterTypeString="Percolation";

    /** Creates a new instance of NetworkWindowParameters */
    public NetworkWindowParameters() {
    }
    
        /* Returns numerical code for layout types, -1 if not known.
     *@return numerical code used internally to represent the layout type
     */
    public int getLayoutType()
    {
        String s= layoutTypeString;
        if (s.substring(0,1)=="G") return 0; // geographical layout 
        if (s.substring(0,1)=="C") return 1; // circular layout 
        if (s.substring(0,1)=="F") return 2; // Fruchterman-Rheingold layout algorithm layout  
        return -1;
    }

 
    /* Returns numerical code for clustering method types, -1 if not known.
     *@return numerical code used internally to represent the layout type
     */
    public int getClusterType()
    {
        String s= clusterTypeString;
        if (s.substring(0,1)=="P") return 0; // percolation
        if (s.substring(0,1)=="T") return 1; // test
        if (s.substring(0,1)=="E") return 2; // edge betweenness
        return -1;
    }


    
}
