/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Vertex;

import TimUtilities.NumbersToString;

/**
 * Stores mode for vertices.
 * <p>Maximum Value mode means vertices are between 0 and maximumValue, a value specified.
 * <p>Constant Weight mode means total vertex weights are fixed equal to totalWeight.
 * @author time
 */
    public class VertexMode {
        final static double UNSET =-9753.1;
        public boolean maxValueModeOn;
        public boolean constantWeightOn;
        public double totalWeight=UNSET;
        public double maximumValue=UNSET;
        static NumbersToString n2s = new NumbersToString(1);
    
    /** Creates a new instance of VertexMode.
     *@param VertexMode &gt;0 max. value mode, otherwise constant weight Mode
     */
    public VertexMode(double VertexMode) {
        setVertexMode(VertexMode);     
    }

     /** Creates a new instance of VertexMode by deep copy.
     *@param em existing VertexMode
      */
    public VertexMode(VertexMode em) {
        maxValueModeOn = em.maxValueModeOn;
        constantWeightOn = em. constantWeightOn;
        maximumValue = em. maximumValue;        
        totalWeight = em. maximumValue;        
    }

    /** Sets value of VertexMode.
     *@param VertexMode &gt;0 max. value mode, &lt;0 constant weight Mode.  Absolute value used to set appropriate value
     */
    public void setVertexMode(double VertexMode) {
        if (VertexMode>0) setMaxValueModeOn(VertexMode);
        else setConstantWeightModeOn(VertexMode);     
    }


    /* Sets Maximum Value Mode mode on.
     *@param value maximum value for each edge
     */
    public void setMaxValueModeOn(double value)
    {
        maxValueModeOn=true;
        constantWeightOn=false;
        maximumValue=Math.abs(value);
        totalWeight=UNSET;
    }

    /* Sets Constant Weight Mode mode on.
     *@param value Total Weight of system
     */
    public void setConstantWeightModeOn(double value)
    {
        maxValueModeOn=false;
        constantWeightOn=true;
        totalWeight=Math.abs(value);
        maximumValue=UNSET;
    }
    /**
     * Returns description.
     * <p>No value given.
     * @return string with description
     */
    public String description()
    {
        String s="*** UNKNOWN ***";
        if (maxValueModeOn) s= "Maximum Vertex Value ";
        if (constantWeightOn) s= "Total Weight Constant ";
        return(s);
    }
    
    /**
     * Returns description and value of mode.
     * @param sep separation string between mode and value
     * @return string with description
     */
    public String descriptionValue(String sep)
    {
        String s="*** UNKNOWN ***";
        if (maxValueModeOn)   s= description() +sep+maximumValue;
        if (constantWeightOn) s= description() +sep+totalWeight;
        return(s);
    }
    
    public String modeString()
    {
        if (maxValueModeOn) return "-xv"+n2s.toString(maximumValue);
        if (constantWeightOn) return "-xw"+n2s.toString(totalWeight);
        return("*** UNKNOWN ***");
    }

    /** Returns value appropriate to mode.
     *@return max value of vertices or total weight depending on mode
     */
    public double getValue()
    {
        double VertexModeValue=UNSET;
        if (maxValueModeOn) VertexModeValue=maximumValue;
        if (constantWeightOn) VertexModeValue=totalWeight;
        return(VertexModeValue);
    }

    /** Returns value in the form used for input and <tt>setVertexMode()</tt> routines.
     * <p>That is is maximum value is on the the maximum value is returned.
     * <p>If the total weight is constant then <emph>minus</emph> the total weight is returned.
     * @return max value of vertices or minus the total weight depending on mode
     */
    public double getModeValue()
    {
        double VertexModeValue=UNSET;
        if (maxValueModeOn) VertexModeValue=maximumValue;
        if (constantWeightOn) VertexModeValue=-totalWeight;
        return(VertexModeValue);
    }

    /** Returns true if value is acceptable.
     * @param v value to be tested
     *@return true if value is acceptable
     */
    public boolean testValue(double v)
    {
        if (maxValueModeOn && (v>maximumValue)) return false;
        if (v<0) return false;
        return true;
    }
}
