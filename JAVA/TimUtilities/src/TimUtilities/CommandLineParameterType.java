/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 *
 * @author time
 */
public class CommandLineParameterType {

    /**
     * The character used to indicate the start of an argument used in primary class.
     * <p>{@value }
     */
    public static final char ARGUMENT='-';
    /**
     * List of characters used to indicate arguments passed to other classes.
     * <p>Values are '*','^',':'
     */
    public final static char [] NOT_ARGUMENT = {'*','^',':'};

    /**
     * Tests to see if this is an argument for primary class.
     * @param s string to test
     * @return true if it starts with {@value #ARGUMENT}
     */public static boolean isARGUMENT(String s){
        return (s.charAt(0)==ARGUMENT?true:false);
    }
    /**
     * Tests to see if this is an argument for primary class.
     * @param c char to test
     * @return true if it starts with {@value #ARGUMENT}
     */public static boolean isARGUMENT(char c){
        return (c==ARGUMENT?true:false);
    }
    /**
     * Tests to see if this is an argument to be passed to another class.
     * @param s string to test
     * @return true if it starts with character indicating an argument for a different class
     */
    public static boolean isOtherArgument(String s){
        for (int c=0; c<NOT_ARGUMENT.length; c++)
                    if ((s.charAt(0) ==NOT_ARGUMENT[c])) return true;
        return false;
    }
    /**
     * Tests to see if this is an argument to be passed to another class.
     * @param c char to test
     * @return true if it starts with character indicating an argument for a different class
     */
    public static boolean isOtherArgument(char c){
        for (int ccc=0; ccc<NOT_ARGUMENT.length; ccc++)
                    if ((c ==NOT_ARGUMENT[ccc])) return true;
        return false;
    }
    /**
     * String giving characters used to indicate arguments for other classes.
     * @param sep string used to separate the characters.
     * @return string giving characters used to indicate arguments for other classes.
     */
    public static String otherArguments(String sep){
        String s=""+NOT_ARGUMENT[0];
        for (int c=1; c<NOT_ARGUMENT.length; c++)
                    s=s+sep+NOT_ARGUMENT[c];
        return s;
    }


}
