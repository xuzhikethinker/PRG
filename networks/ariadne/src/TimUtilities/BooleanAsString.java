/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 * Convert boolean values to string representations.
 * @author time
 */
public class BooleanAsString {

    public BooleanAsString(){}

    public static String booleanToString(boolean b, String trueString, String falseString){
        return (b?trueString:falseString);
    }
    public static String booleanToYesNo(boolean b){
        return (b?"yes":"no");
    }
    public static String booleanToOnOff(boolean b){
        return (b?"off":"on");
    }

}
