/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import TimUtilities.GeneralMode;
import java.io.PrintStream;

/**
 *
 * @author time
 */
public class UpdateMode extends GeneralMode {
    
      final static String[] shortDescription={"PPA","DPPA","MDN","MC","VP","DCGM","RWGM","SGM","ALN"}; //,"DP","XTent"};
      final static boolean[] weighted={false,false,false,true, true, true, true, true}; //,"DP","XTent"};
      final static boolean[] directed={false, true, false,true,false,true,true,true, true}; //,"DP","XTent"};
      final  static String[] longDescription={"Proximal Point Analysis - Undirected",
                                          "Directed Proximal Point Analysis",
                                          "Maximum Distance Network",
                                          "Monte Carlo",
                                          "Hamiltonian Edge Potential",
                                          "Doubly Constrained Gravity Model",
                                          "Rihll-Wilson Gravity Model",
                                          "Simple Gravity Model",
                                          "Alonso Model"}; //"Distance Probability", "XTent"};
      final static int DEFAULTMODE =0;


   /** Basic Constructor
     */
    public UpdateMode() {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
    }
   /** Basic Constructor
    * @param modeName short name of mode to be used to set
     */
    public UpdateMode(String modeName) {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
        this.setFromName(modeName);
    }

   /** Basic Constructor
    * @param mode number of mode to be used to set
     */
    public UpdateMode(int mode) {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
        this.set(mode);
    }
    /**
     * Lists all the different modes against their mode number, long and short names along with properties.
     * <br> Includes short name
     * @param PS Printstream such as System.out
     * @param sep1 string to start line before index
     * @param sep2 string between index a4+nd long name
     * @param sep3 string between long name and short name
     * @param sep4 string between short name and properties
     */
    public void listAllWithProperties(PrintStream PS,
            String sep1, String sep2, String sep3, String sep4)
    {
       for (int m=0; m<name.length; m++) PS.println(sep1+m+sep2+
               this.getLongString(m)+sep3+this.getString(m)+sep4+
               propertiesString());
    }
    /**
     * Short string representing properties of model.
     * <p>Returns W (uw) if (un)weighted
     *        and D (ud) if (un)directed
     * concatenated into short string..
     * @return string representing properties of model
     */
    public String propertiesString(){return (isDirected()?"D":"ud")+(isWeighted()?"W":"uw") ;}

    /**
     * Tests if model gives directed network
     * @return true if directed
     */
    public boolean isDirected(){
        try {return directed[getNumber()];}
        catch (Exception e){ throw new RuntimeException(e+ ": unknown mode "+getNumber());}
    }
    /**
     * Tests if model gives weighted network
     * @return true if weighted
     */
    public boolean isWeighted(){
        try {return weighted[getNumber()];}
        catch (Exception e){ throw new RuntimeException(e+ ": unknown mode "+getNumber());}
    }
    /**
     * Tests if we have simple PPA mode
     * @return true if PPA mode, false otherwise
     */
    public boolean isPPAsimple() { return (isCurrentMode("PPA"));}

    /**
     * Tests if we have DPPA (directed PPA) mode
     * @return true if PPA mode, false otherwise
     */
    public boolean isPPAdirected() { return (isCurrentMode("DPPA"));}

    /**
     * Tests if we have any PPA mode, simple or directed
     * @return true if any (simple or directed) PPA mode, false otherwise
     */
    public boolean isPPA() { return ((isCurrentMode("PPA")) ||(isCurrentMode("DPPA")) ); }

    /**
     * Tests if we have MDN (Maximum Distance Network).
     * @return true if MDN mode, false otherwise
     */
    public boolean isMDN() { return (isCurrentMode("MDN") );}

    /**
     * Tests if we have MC mode
     * @return true if MC mode, false otherwise
     */
    public boolean isMC() { return (isCurrentMode("MC"));}

    /**
     * Tests if we have VP.
     * @return true if VP mode, false otherwise
     */
    public boolean isVP() { return (isCurrentMode("VP"));}

        /**
     * Tests if we have DCGM (Doubly Constrained Gravity Model).
     * @return true if DCGM mode, false otherwise
     */
    public boolean isDCGM() { return (isCurrentMode("DCGM"));}
/**
     * Tests if we have RWGM mode (Rhill-Wilson Gravity Model).
     * @return true if RWGM mode, false otherwise
     */
    public boolean isRWGM() { return (isCurrentMode("RWGM"));}

    /**
     * Tests if we have SGM (Simple Gravity Model).
     * @return true if SGM mode, false otherwise
     */
    public boolean isSGM() { return (isCurrentMode("SGM"));}

    /**
     * Tests if we have ALN (Alonso model).
     * @return true if ALN mode, false otherwise
     */
    public boolean isAlonso() { return (isCurrentMode("ALN"));}
    

}
