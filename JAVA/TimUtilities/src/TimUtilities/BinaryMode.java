/*
 * BinaryMode.java
 *
 * Created on 16 November 2006, 10:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

import TimUtilities.GeneralMode;
import java.io.PrintStream;

/**
 * Stores multiple modes which are binary in nature.
 * <br>i.e. these are check boxes.
 * @author time
 */
public class BinaryMode extends GeneralMode{
    
    private int number=0;
    
    
    private int [] bit; //= new int [name.length];;
    

    /** Creates a new instance of BinaryMode with all output off.
     */
    public BinaryMode(String [] newName, String [] newLongName) {
        setUp(newName, newLongName, 0);
    }
    /** Creates a new instance of BinaryMode.
     *@param newNumber new rewire mode number
     */
    public BinaryMode(String [] newName, String [] newLongName, int newNumber) {
         setUp(newName, newLongName, newNumber);
    }
    
    /** Creates a new instance of BinaryMode by using existing BinaryMode example.
     * <br>Relies on strings being copied automatically due to static type
     *@param old old BinaryMode
     */
    public BinaryMode(BinaryMode old) {
         set(old.number);
    }
    
    /**
     * Sets up necessary variables.
     * <br>Including an array of 2^m to use to encode mode number as bits.
     * @param newName list of short names to identify different modes
     * @param newLongName list of long names used for description of modes
     * @param n new mode number
     */
    @Override
    protected void setUp(String [] newName, String [] newLongName, int n)
    {
        longName=newLongName;
        if (name.length != longName.length) 
            throw new IllegalArgumentException("name and longName have different lengths"
                    +name.length+" != "+longName.length);
        bit[0]=1;
        for (int m=1; m<name.length; m++) bit[m]=bit[m-1] << 1;
        set(n);
    }
    
    /** Sets all modes on.
     */
    public void setAllOn(String [] name) {
        number = (1 << name.length)-1; // right shift is cheap power of two
     }
    
    /** Sets all modes off.
     */
    public void setAllOff() {
        number = 0;
     }
    

    /** Sets mode number from a string representing the mode name.
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

    /** Unsets mode number from a string representing the mode name.
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
     * Status of a mode.
     * @param m number of mode
     * @return true (false) if mode m is on (off or if m is out of range)
     */
    public boolean isSet(int m){
        if ((m<0) || (m>=name.length)) return false;
        return ((bit[m] & number)>0?true:false);
    }


    /**
     * Test mode number
     * @param m between 0 and the number of bits
     * @return true (false) if mode number correct
     */
   public boolean testModeNumber(int m){
       return ((m>=0 && m<bit.length)?true:false);
               }

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
}

