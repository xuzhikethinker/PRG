/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 *
 * @author time
 */
public class FractionUtilities {

    /**
     * Converts decimal part of a fraction to an int.
     * <p> e.g. <tt>fractionToString(0.123, 3)=123</ttr>
     * <tt>0 &lt;= f &lt; 1</tt>
     * @param f fraction
     * @param dp number of decimal places to keep
     * @return integer contain dp decimal places of f
     */
    public static int fractionToInt(double f, int dp){
        double ff=f;
        for (; dp>0; dp--) ff*=10;
        return (int) Math.round(ff);
    }

    /**
     * Converts decimal part of a fraction to a string.
     * <p> e.g. <tt>fractionToString(0.123, 3)=123</ttr>
     * 0 &lt;= f &lt; 1
     * @param f fraction
     * @param dp number of decimal places to keep
     * @return string contain dp decimal places of f
     */
    public static String fractionToString(double f, int dp){
        return String.format("%0"+dp+"d",fractionToInt(f,dp));
    }


}
