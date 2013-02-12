/*
 * OutputMode.java
 *
 * Created on 16 November 2006, 10:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package CopyModel;

import java.io.*;

/**
 * The possible Output Modes for 
 * @author time
 */
public class OutputMode {
    final int maxNumber = 1+2+4+8+16+32+64+128+256;
    int number;
    String modeString;
    boolean degreeDistributionOn; // 1
    boolean weightDistributionOn; // 2
    boolean lbDegreeDistributionOn; // 4 
    boolean lbWeightDistributionOn; // 8
    boolean distributionsEachTimeOn; // 16
    boolean allRunStatisticsOn; // 32
    boolean artefactDegreeEvolutionOn; //64
    boolean individualListOn; //128
    boolean influenceDegreeEvolutionOn; //256

    
    

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
    
    /** Sets rewire mode Number from a double.
     *@param newNumber new model number
     */
    public void set(double newNumber) {
                set((int) (newNumber+0.5));
    }
    /** Sets rewire mode Number from a double.
     *@param newNumber new model number
     */
    public void set(int newNumber) {
            number= newNumber;
            degreeDistributionOn = (((number &  1) >0) ?true : false); // 1
            weightDistributionOn = (((number &  2) >0) ?true : false); // 2
            lbDegreeDistributionOn = (((number &  4) >0) ?true : false); // 4
            lbWeightDistributionOn = (((number &  8) >0) ?true : false); // 8
            distributionsEachTimeOn = (((number & 16) >0) ?true : false); // 16
            allRunStatisticsOn = (((number & 32) >0) ?true : false); // 32
            artefactDegreeEvolutionOn = (((number & 64) >0) ?true : false); // 64
            individualListOn = (((number & 128) >0) ?true : false); // 128
            influenceDegreeEvolutionOn = (((number & 256) >0) ?true : false); // 256
            setModeString();
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
     * @param PS Printstream such as System.out
     * @param cc Comment character
     */
    public void printMode(PrintStream PS, String cc) 
    {  
        PS.println(cc+" Output control method is "+number);
        PS.println(cc+"   Degree distribution calculation per run "+ (degreeDistributionOn?"ON":"OFF") )  ;
        PS.println(cc+"   Weight distribution calculation per run "+ (weightDistributionOn?"ON":"OFF") )  ;
        PS.println(cc+"   Log Binned Degree distribution calculation per run "+ (lbDegreeDistributionOn?"ON":"OFF") )  ;
        PS.println(cc+"   Log Binned Weight distribution calculation per run "+ (lbWeightDistributionOn?"ON":"OFF") )  ;
        PS.println(cc+"   Weight distribution for each time "+ (distributionsEachTimeOn?"ON":"OFF") )  ;
        PS.println(cc+"   Time evolution of statistics averaged over all runs "+ (allRunStatisticsOn ?"ON":"OFF") )  ;    
        PS.println(cc+"   Time evolution of Artefact Occupation per run "+ (this.artefactDegreeEvolutionOn ?"ON":"OFF") )  ;    
        PS.println(cc+"   Individual list each run "+ (this.individualListOn ?"ON":"OFF") )  ;    
        PS.println(cc+"   Time evolution of Influence Network per run "+ (this.influenceDegreeEvolutionOn ?"ON":"OFF") )  ;
    };

     // ----------------------------------------------------------------------
    /**
     * Sets the description string for the mode.
     */
    private void setModeString() 
    {
        String sep=" ";
        String s="unknown";
        if (degreeDistributionOn) s=s+"DD"+sep;
        if (weightDistributionOn) s=s+"WD"+sep;  
        if (lbDegreeDistributionOn) s=s+"lbDD"+sep;  
        if (lbWeightDistributionOn) s=s+"lbWD"+sep;
        if (distributionsEachTimeOn) s=s+"dist.time"+sep;
        if (allRunStatisticsOn) s=s+"av.stats"+sep;
        if (artefactDegreeEvolutionOn) s=s+"ArtOcc"+sep;
        if (this.individualListOn) s=s+"Ind"+sep;
        if (influenceDegreeEvolutionOn) s=s+"InflOcc"+sep;
        modeString = s ;
    };
}

