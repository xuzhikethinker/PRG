/*
 * OutputMode.java
 *
 * Created on 16 November 2006, 10:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import java.io.PrintStream;

/**
 * The possible Output Modes for 
 * @author time
 */
public class OutputMode {
    //String modeString;
    /**
     * Number of characters needed to specify a unique name
     */
    final static int UNIQUENAME =3; 
    private int number=0;
    public static final String [] name = {"info","pajek","elist","ematrix","gsep","anf","KML"};
    static final String [] longName = {"general information",
                                       "pajek files",
                                       "edges as vector",
                                       "edges as matrix",
                                       "gene separation",
                                       "ariadne network file",
                                       "Google Earth KML network file"};
    final static int KMLIndex = getIndex("KML");
    final static int PajekIndex = getIndex("pajek");
    final static int EdgeListIndex = getIndex("elist");
    final static int EdgeMatrixIndex = getIndex("ematrix");
    final static int InfoIndex = getIndex("info");
    final static int GeneSeparationIndex = getIndex("gsep");
    final static int AriadneNetworkFileIndex = getIndex("anf");
    
    
    private int [] bit= new int [name.length];;
//    private int [] modeOn = new int [name.length];
//    final static char NOCHAR='n';
//    final static char YESCHAR='y';
    

    /** Creates a new instance of OutputMode with all output off.
     */
    public OutputMode() {
        setUp(0);
    }
    /** Creates a new instance of OutputMode.
     *@param newNumber new rewire mode number
     */
    public OutputMode(int newNumber) {
        setUp(newNumber);
    }
    
    /** Creates a new instance of OutputMode by using existing OutputMode example.
     *@param old old OutputMode
     */
    public OutputMode(OutputMode old) {
        setUp(old.number);
    }
    
    /**
     * Sets up necessary variables.
     * <br>Including an array of 2^m to use to encode mode number as bits.
     */
    private void setUp(int n)
    {
        bit[0]=1;
        for (int m=1; m<name.length; m++) bit[m]=bit[m-1] << 1;
        set(n);
    }
    
    /** Sets mode number from a double.
     *@param newNumber new model number
     */
    public void set(double newNumber) {
                set(Math.round(newNumber));
    }
    /** Sets rewire mode Number from a double.
     *@param newNumber new model number
     */
    public void set(int newNumber) {
        number = newNumber;
        
     }
    
    /** Sets mode number from a string representing of a double.
     *@param newNumber new model number as string
     */
    public void set(String newNumber) {
        set(Double.parseDouble(newNumber));
     }

    /** Sets all modes on.
     */
    public void setAllOn() {
        number = (1 << name.length)-1; // right shift is cheap power of two
     }
    
    /** Sets all modes off.
     */
    public void setAllOff() {
        number = 0;
     }
    

    /** Sets mode number from a string represnting the mode name.
     * <br>Compares the first <code>UNIQUENAME</code> length characters of input string
         * against the name array
         *@param s new model name as string
     */
    public void setModeOn(String s) {
        setModeOn(getIndex(s));  
     }
    
    /** Sets mode number.
     * <br>No change if not valid number
     *@param m index of mode to be set
     */
    public void setModeOn(int m) {
        if (testModeNumber(m)) number = (number | bit[m]);
     }

    /** Unsets mode number from a string represnting the mode name.
     * <br>Compares the first <code>UNIQUENAME</code> length characters of input string
         * against the name array.
         *@param s new model name as string
     */
    public void setModeOff(String s) {
        setModeOff(getIndex(s));
     }

    /** Unsets mode number
     * <br>No change if not valid number
     *@param m index of mode to be set
     */
    public void setModeOff(int m) {
        if (testModeNumber(m)) number = number - (number & bit[m]);
     }

    
    /**
     * Tests if valid mode number.
     * @param m mode number to test
     * @return true (false) if m is (in)valid mode number.
     */
    private boolean testModeNumber(int m){return ( ((m<0) || (m>=name.length)) ?false:true);}
        
    /**
     * Status of a mode.
     * @param m number of mode
     * @return true (false) if mode m is on (off or if m is out of range)
     */
    public boolean isSet(int m){
        if ((m<0) || (m>=name.length)) return false;
        return ((bit[m] & number)>0?true:false);
    }


         /**
         *  Returns mode number.
          * <br>The m-th bit indicates if mode named <code>name[m]</code> is on.
         * @return number of mode.
         */
        public int getNumber()
        {
            return number;
        }
         /**
         *  Returns index from short name given.
         * <br>Compares the first <code>UNIQUENAME</code> length characters of input string
         * against the name array
         *@param input name of variable being requested
         * @return variable number, -1 if none found.
         */
        public static int getIndex(String input)
        {
            String s=input.substring(0,UNIQUENAME);
            for (int v=0; v<name.length;v++) 
              {
                  if (name[v].startsWith(s)) return v;
              }
            return -1;
        }

    
    // ----------------------------------------------------------------------
    /**
     * Prints description of Mode.
     * @param PS Printstream such as System.out
     * @param cc Comment character
     */
    public void print(PrintStream PS, String cc) 
    {  
        PS.println(cc+" Output mode is "+getNumber());
    };

     // ----------------------------------------------------------------------
    /**
     * Gives an abbreviated description string for the mode.
     * @param sep separation string
     * @return a string with the abbreviated description of mode.
     */
    public String toString(String sep) 
    {
        String s="";
        for (int m=0; m<name.length; m++) if (isSet(m)) s=s+name[m]+sep;
        return s ;
    };
    /**
     * Lists all the different modes against their mode number.
     * <br> Includes short name
     * @param PS Printstream such as System.out
     * @param cc string to start line
     * @param sep separation string between bit value and name
     */
    public void listAll(PrintStream PS, String cc, String sep) 
    {
       for (int m=0; m<name.length; m++) PS.println(cc+bit[m]+sep+longName[m]+" ("+name[m]+")");
    };
    /**
     * Gives an full name of mode.
     * @param m mode index
     * @return a string with the full description of mode.
     */
    public String longString(int m) 
    {
        return longName[m] ;
    };
}

