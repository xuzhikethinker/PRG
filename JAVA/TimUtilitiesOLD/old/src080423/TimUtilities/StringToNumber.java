/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 *
 * @author time
 */
public class StringToNumber {
    
    final static int NOVALUE = 24681357;
    int from;
    int to;
    
    /*
     * Gets integer from next part of string.
     * @param inString input string
     * @param f point in string to start from
     */
    public int getInteger(String inString, int f)
    {
        from=f;    
        to=from;
        try{
            if ((inString.charAt(to)=='-')) to++; // allow for sign
            while ((inString.codePointAt(to)>47) && (inString.codePointAt(to)<58) ) to++;
        } catch (RuntimeException e) {} // always run, as 
        
        try{return Integer.parseInt(inString.substring(from, to));}
        catch (RuntimeException e) {} // always try
        return NOVALUE;            
    }

}
