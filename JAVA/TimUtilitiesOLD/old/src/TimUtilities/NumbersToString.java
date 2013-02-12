/*
 * NumbersToString.java
 *
 * Created on 11 December 2006, 17:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;



/**
 * Utilities for manipulating numbers into Strings.
 * @author time
 */
public class NumbersToString {
    
    public int decimalPlaces = 3;
    /** Creates a new instance of NumbersToString */
    public NumbersToString() {
    }
    
    /** Creates a new instance of NumbersToString */
    public NumbersToString(int dec) {
        decimalPlaces = dec;
    }
    
    /**
     * Returns double truncated to standard number of requested number of decimal places.
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public double TruncDecimal(double value, int dec)
    {
      double shift = Math.pow(10,dec);
      return ( ( (double) ((int) (value*shift+0.5)))/shift);
    }
    
        /**
         * Returns double truncated to standard number of decimal places.
     * @param value has tractional part truncated
     */
    public double TruncDecimal(double value)
    {
      return ( TruncDecimal(value,decimalPlaces));
    }



    /**
     *  Returns string of double truncated to standard number of decimal places.
     * @param value has tractional part truncated
     */
    public String toString(double value)
    {
      Double d =TruncDecimal(value,decimalPlaces);
      return ( d.toString() );
    }
    /**
     * Returns string of double truncated to standard number of decimal places.
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public String toString(double value, int dec)
    {
      Double d =TruncDecimal(value,dec);
      return ( d.toString() );
    }
}
