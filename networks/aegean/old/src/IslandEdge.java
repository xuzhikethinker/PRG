/*
 * IslandEdge.java
 *
 * Created on 27 July 2006, 16:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

/**
 * Defines characteristics of an edge in an Island Network.
 * @author time
 */
public class IslandEdge {
    final static String IEVERSION = "IE060728";
    
    double value;
    double colour;
    double distance; // fixed distance
    double penalty;
    double potential1;
    double separation; // effective distance e.g. set using Dijkstra
    double edgeRank; // ranking amongst all directed edges, 
                     // <0 not set, 0=smallest, N(N-1)=max
    
    /** Creates a new instance of IslandEdge */
    public IslandEdge() {
        value=0.0;
        colour=0.0;
        distance=9999.0;
        penalty=1.0;
        potential1=0;
        separation =0;
        edgeRank=-1; //
    }

    /** Creates a new instance of IslandEdge by deep copying existing edge*/
    public IslandEdge(IslandEdge e) {
        value=e.value;
        colour=e.colour;
        distance=e.distance;
        penalty=e.penalty;
        potential1=e.potential1;
        separation = e.separation;
        edgeRank = e.edgeRank;
    }

        /**
     * Gives the potential between two site variables using effective distance.
     * @param H the Island Hamiltonian
     * @return returns the edge potential * lambda for model 1
     */
    public double getEdgePotentialSeparation1(IslandHamiltonian H) 
    {
      return(  H.edgePotential1(separation));
    }

    /**
     * Sets the potential between two site variables.
     * @param H the Island Hamiltonian
     * @return returns the edge potential * lambda for model 1
     */
    public double setEdgePotential1(IslandHamiltonian H) 
    {
      potential1 = H.edgePotential1(distance);
      return(  potential1 );
    }


}
