/*
 * OutputMode.java
 *
 * Created on 16 November 2006, 10:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimGraph;

import java.io.*;
    
/**
 * The possible Output Modes for 
 * @author time
 */
public class OutputMode {
    static final int maxNumber = 1+2+4+8+16+32+64+128+256+512;
    private int number;
    String modeString;
    public boolean degreeDistributionOn; // 1
    public boolean distancesOn; // 2
    public boolean clusteringOn; // 4 
    public boolean unusedOn; // 8
    public boolean componentsOn; // 16
    public boolean rankingOn; // 32
    public boolean structuralHolesOn; // 64
    public boolean graphMLFileOn; // 128
    public boolean pajekFileOn; // 256
    public boolean adjacencyFileOn; // 512




    /** Creates a new instance of OutputMode.
     *@param newNumber new rewire mode number
     */
    public OutputMode(double newNumber) {
        set(newNumber);
    }
    
    /** Creates a new instance of OutputMode by using existing OutputMode example.
     *@param old old OutputMode
     */
    public OutputMode(OutputMode old) {
        set(old.number);
    }
    
    /**
     * Returns the mode number
     * @return mode number, an integer whose bits specify the modes
     */
    public int getNumber(){return number;}
    
    /** Sets rewire mode Number from a double.
     *@param newNumber new model number
     */
    public void set(double newNumber) {
        number= (int) (newNumber+0.5);
        setModeString();
        degreeDistributionOn = (((number &  1) >0) ?true : false); // 1
        distancesOn          = (((number &  2) >0) ?true : false); // 2
        clusteringOn         = (((number &  4) >0) ?true : false); // 4
        unusedOn             = (((number &  8) >0) ?true : false); // 8
        componentsOn         = (((number & 16) >0) ?true : false); // 16
        rankingOn            = (((number & 32) >0) ?true : false); // 32
        structuralHolesOn    = (((number & 64) >0) ?true : false); // 64
        graphMLFileOn        = (((number & 128) >0) ?true : false); // 128
        pajekFileOn          = (((number & 256) >0) ?true : false); // 8
        adjacencyFileOn      = (((number & 512) >0) ?true : false); // 256
    }

    /** Sets Model Number from a String.
     *@param newNumber new model number as string
     */
    public void set(String newNumber) {
        set(Double.parseDouble(newNumber));
     }
   // ----------------------------------------------------------------------
    /**
     * Prints description of Mode.
     * @param PS PrintStream such as System.out
     * @param cc Comment character
     */
    public void printMode(PrintStream PS, String cc)
    {
        PS.println("Output control method is "+modeString);
        if (degreeDistributionOn) PS.println("   Degree distribution calculation ON");
        else  PS.println("   Degree distribution calculation OFF");
        if (distancesOn) PS.println("   Distance calculation ON");
        else  PS.println("   Distance calculation OFF");
        if (clusteringOn) PS.println("   Clustering calculation ON");
        else  PS.println("   Clustering calculation OFF");
        if (componentsOn) PS.println("  Component calculations ON");
        else  PS.println("   Component calculations OFF");
        PS.println("   Ranking calculations "+(rankingOn?"ON":"OFF"));
        PS.println("   Structural Hole calculations "+(structuralHolesOn?"ON":"OFF"));
        PS.println("   GraphML output "+(graphMLFileOn?"ON":"OFF"));
        PS.println("   PAJEK file output "+(pajekFileOn?"ON":"OFF"));
        PS.println("   Adjacency matrix output "+(adjacencyFileOn?"ON":"OFF"));
        
     }

    // ----------------------------------------------------------------------
    /**
     * Prints description of OutputMode.
     * @param PS PrintStream such as System.out
     * @param cc string put at start of each line
     */
    public void printUsage(PrintStream PS, String cc)
    {
        PS.println(cc+"(OutputMode &  1)  Degree distribution calculation ON/OFF");
        PS.println(cc+"(OutputMode &  2)  Distance calculation ON/OFF");
        PS.println(cc+"(OutputMode &  4)  Clustering calculation ON/OFF");
        PS.println(cc+"(OutputMode &  8)  unused"); // output  ON/OFF");
        PS.println(cc+"(OutputMode & 16)  Component calculations ON/OFF");
        PS.println(cc+"(OutputMode & 32)  Ranking calculations ON/OFF");
        PS.println(cc+"(OutputMode & 64)  Structural Hole calculations ON/OFF");
        PS.println(cc+"(OutputMode & 128) GraphML output ON/OFF");
        PS.println(cc+"(OutputMode & 256) PAJEK file output  ON/OFF");
        PS.println(cc+"(OutputMode & 512) Adjacency matrix output ON/OFF");
     }




     // ----------------------------------------------------------------------
    /**
     * Returns a description string for the mode.
     */
    public String getModeString(String sep) 
    {
        String s="";
        if (degreeDistributionOn) s=s+"DD"+sep;
        if (distancesOn) s=s+"dist"+sep;  
        if (clusteringOn) s=s+"clust"+sep;  
        if (componentsOn) s=s+"comp"+sep;
        if (rankingOn) s=s+"rank"+sep;
        if (structuralHolesOn) s=s+"sthole"+sep;
        if (graphMLFileOn) s=s+"graphML"+sep;
        if (pajekFileOn) s=s+"pajek"+sep;
        if (adjacencyFileOn) s=s+"adj"+sep;
        return  s ;
    };

    /**
     * Gets the description string for the mode.
     */
    public String getModeString() 
    {  
        if (modeString==null) setModeString();
        return modeString;
    };

    /**
     * Sets the description string for the mode. using a space as a separator.
     */
    private void setModeString() 
    {  
        modeString = getModeString(" ");
    };
    
    
}

