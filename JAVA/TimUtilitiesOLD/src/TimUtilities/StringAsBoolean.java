/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 * Tests for true or false, yes or no strings or characters.
 * @author time
 */
public class StringAsBoolean {
    static String [] trueStrings={"true", "yes"};
    static String [] falseStrings={"false", "no"};

    /**
     * Test for true/yes characters.
     * <p>True characters are t or y (upper or lower case).
     * @param c character to test
     * @return true if t or y (upper or lower case), otherwise false.
     */
    public static boolean isTrue(char c){
        if (c=='t' || c=='T' || c=='y' || c=='Y') return true;
        return false;
    }
    /**
     * Test for true/yes string using first character.
     * <p>True if first character is t or y (upper or lower case).
     * @param s string whose first character to test
     * @return true if t or y (upper or lower case) is first character, otherwise false.
     */
    public static boolean isFirstCharacterTrue(String s){
        return isTrue(s.charAt(0));
    }

    /**
     * Test for false/no characters.
     * <p>False/No characters are f or n (upper or lower case).
     * @param c character to test
     * @return true if f or n (upper or lower case), otherwise false.
     */
    public static boolean isFalse(char c){
        if (c=='f' || c=='F' || c=='n' || c=='N') return true;
        return false;
    }
    /**
     * Test for first character false/no.
     * <p>False/No characters are f or n (upper or lower case).
     * @param s string whose first character to test
     * @return true if first character is f or n (upper or lower case), otherwise false.
     */
    public static boolean isFirstCharacterFalse(String s){
        return isTrue(s.charAt(0));
    }
}
