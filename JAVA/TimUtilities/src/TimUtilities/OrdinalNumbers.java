/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TimUtilities;

/**
 * Produces strings with ordinal numbers.
 * @author time
 */
public class OrdinalNumbers {

    /**
     * These are the known ordinal numbers.
     */
    final static public String [] ordinalString = {"Zeroth","First", "Second", "Third", 
        "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth"};
    
    /**
     * Given number returns its ordinal number with first letter capitalised
     * <p>If name not known, returns <em>n</em>th.
     * @param n number whose ordinal number is needed (0=zeroth)
     * @return string of ordinal number, first letter capitalised
     */
    public static String getOrdinalNumber(int n){
        if ((n<0) || (n>=ordinalString.length)) return n+"th";
        return ordinalString[n];
    }
    /**
     * Given number returns its ordinal number in lower case.
     * <p>If name not known, returns <em>n</em>th.
     * @param n number whose ordinal number is needed (0=zeroth)
     * @return string of ordinal number in lower case
     */
    public static String getOrdinalNumberLC(int n){
        return getOrdinalNumber(n).toLowerCase();
    }
}
