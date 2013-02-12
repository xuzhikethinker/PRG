/*
 * IslandEdge.java
 *
 * Created on 27 July 2006, 16:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks.Edge;

import IslandNetworks.*;
import TimUtilities.NumbersToString;

/**
 * Defines characteristics of an edge in an Island Network.
 * @author time
 */
public class IslandEdge {
    final static String IEVERSION = "IE060728";
    
    public double value;
    public double colour;
    private double distance; // fixed distance
    double penalty;
    private double potential1;
    private double separation; // effective distance e.g. set using Dijkstra
    private double edgeRank; // ranking amongst all directed edges, 
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
    
    
    public void setDistance(double d){ distance =d; }
    
    public double getDistance(){return distance; }

    public void setSeparation(double d){ separation =d; }
    
    public double getSeparation(){return separation; }

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

    /**
     * Gets the potential between two site variables.
     * <p> Must have been previously set.
     * @return returns the edge potential * lambda for model 1
     */
    public double getEdgePotential1() {return(  potential1 );}

    /**
     * Returns edge as a single string.
     * @param sep separation character
     * @return returns a string of values separated by spearation character
     *@param dec integer number of decimal palces to display
    */
    public String toString(String sep, int dec) 
    {
        NumbersToString ns = new NumbersToString(dec);
      String s= ns.toString(value)+sep+ns.toString(colour) +sep+ ns.toString(distance) +sep+ ns.toString(penalty) +sep+ ns.toString(potential1) +sep+ ns.toString(separation) +sep+ ns.toString(edgeRank);
      return(  s);
    }

    /**
     * Returns edge values which can not be derived as a single string.
     * @param sep separation character
     * @param dec integer number of decimal palces to display
     * @return returns a string of values separated by spearation character
     */
    public String parameterString(String sep, int dec) 
    {
        NumbersToString ns = new NumbersToString(dec);
      String s= ns.toString(value)+sep+ ns.toString(distance) +sep+ ns.toString(penalty) ;
      return(  s);
    }
    /**
     * Returns edge values which can not be derived as a single string.
     * @param sep separation character
     * @return returns a string of values separated by spearation character
    */
    public String parameterNames(String sep) 
    {      return(  "value"+sep+ "distance" +sep+ "penalty" );}

}
