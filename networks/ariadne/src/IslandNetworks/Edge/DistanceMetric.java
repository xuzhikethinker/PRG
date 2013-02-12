/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Edge;

import java.io.PrintStream;

/**
 *
 * @author time
 */
public class DistanceMetric {

    /**
     * Number of characters used to detect mode from short name
     */final static int UNIQUENAME =3; 
    private int number; // set type of separation (eff.distance) measurements to use
    public static final String [] name = {"plain","d/(e*V)","1/e","d/(Sve*V)","1/(Sve)","d/V"};
    public static final String [] longName = {"plain physical distance",
    "sum of individual physical distance/potential using edge values",
    "inverse edge value",
    "sum of individual physical distance/potential using edge weights",
    "inverse edge weight",
    "sum of physical distance/potential (but no edge values)"};

    /**
     * Initialises to mode 0.
     */
    public DistanceMetric(){set(0);}
    /**
     * Initialises to given mode, or 0 if illegal mode given.
     * @param m mode number
     */
    public DistanceMetric(int m){set(0); set(m);}
    /**
     * Deep Copy.
     * @param m DistanceMetric to be copied
     */
    public DistanceMetric(DistanceMetric m){set(0); set(m.getNumber());}
    
         /**
         *  Returns distance metric.
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

    /** Sets distance metric from a double.
     *@param newNumber new model number
     */
    public void set(double newNumber) {
                set(Math.round(newNumber));
    }
    /** Sets distance metric from a double.
     * <br>Will not set an illegal number.
     *@param newNumber new model number
     * @return new mode number or -1 if unknown or illegal in which case no changes made.
     */
    public int set(int newNumber) {
        if ((number<0) || (number>=name.length)) return -1;
        else number = newNumber;
        return number;
     }
    
    /** Sets distance metric from a string representing of a double.
     *@param newNumber new model number as string
     */
    public void set(String newNumber) {
        set(Double.parseDouble(newNumber));
     }
    
//    /** Sets distance metric from a string representing name of mode.
//     *@param modeName name of mode
//     */
//    public int setMode(String modeName) {
//        int n=getIndex(modeName);
//        set(n);
//        return n;
//     }
    
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
     * Lists all the different modes against their distance metric.
     * <br> Includes short name
     * @param PS Printstream such as System.out
     * @param cc string to start line
     * @param sep separation string between bit value and name
     */
    public static void listAll(PrintStream PS, String cc, String sep) 
    {
       for (int m=0; m<name.length; m++) PS.println(cc+m+sep+longName[m]+" ("+name[m]+")");
    };
    
}
