/*
 * NumbersToString.java
 *
 * Created on 11 December 2006, 17:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

import java.util.Formatter;



/**
 * Utilities for manipulating numbers into Strings.
 * @author time
 * TODO Use <tt>java.util.Formatter</tt> and <tt> and the <tt>System.out.format(.....)</tt>
 */
public class NumbersToString {
    
    private int decimalPlaces = 3;
    private double shift;
    private String dFormatString=doubleFormatString(decimalPlaces);

      
    /** Creates a new instance of NumbersToString */
    public NumbersToString() {
    }
    
    /** Creates a new instance of NumbersToString */
    public NumbersToString(int dec) {
        setDecimalPlaces(dec);
    }
    
    void test(){
       Formatter f = new Formatter();
       double d = 1.85;
       f.format("value of double is ", d);
       String s = f.toString();
    }
    public void setDecimalPlaces(int dec)
    {
        if (dec<1) decimalPlaces =3; else decimalPlaces =dec;
        dFormatString=doubleFormatString(decimalPlaces);
        shift = Math.pow(10,dec);
    }
    /**
     * Returns double truncated to standard number of requested number of decimal places.
     * <p>Sets decimanl places stored in class to that given here.
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public double TruncDecimal(double value, int dec)
    {
        setDecimalPlaces(dec);
      return ( (double) (Math.round(value*shift)/shift));
    }
    
    /**
     * Returns double truncated to standard number of requested number of decimal places.
     * <p>Sets decimanl places stored in class to that given here.
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public double TruncDecimalNew(double value, int dec)
    {
        setDecimalPlaces(dec);
      return ( (double) (Math.round(value*shift)/shift));
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
    public String toStringOLD(double value)
    {
      Double d =TruncDecimal(value,decimalPlaces);
      return ( d.toString() );
    }

    /**
     *  Returns string of double truncated to standard number of decimal places.
     * @param value has tractional part truncated
     */
    public String toString(double value)
    {
      return String.format(dFormatString, value);
    }
    
    /**
     * Returns a format string for <tt>dec</tt> decimal places
     * @param dec Number of decimal palces to use (6 assumed if this is not positive)
     * @return format string for doubles
     */
    static String doubleFormatString(int dec){
        String fstring="%12.6g";
        if (dec>0) fstring ="%"+(dec+6)+"."+dec+"g";
        return fstring;
    }
    

    /**
     * Returns string of double truncated to standard number of decimal places.
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    static public String toString(double value, int dec)
    {
      return ( String.format(doubleFormatString(dec), value) );
    }
}
