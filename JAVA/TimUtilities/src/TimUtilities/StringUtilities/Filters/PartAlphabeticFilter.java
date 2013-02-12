/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.StringUtilities.Filters;

/**
 * Check if string has at least one letter
 * @author time
 */
public class PartAlphabeticFilter extends StringFilter {
    
    public PartAlphabeticFilter(){};
    
    /**
        * Returns true if the string has at least one letter.
        * <p>Extend this class and override this method.
        * This is a dummy class and always returns false.
	* @param s the input string
	* @return false.
	*/
    @Override
    public boolean isAcceptable(String s){
      int ls = s.length();
	  for (int i = 0; i < ls; i++)
		 if (Character.isLetter( s.charAt(i) )) return true;
	  return false;
    }

}
