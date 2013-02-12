/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.StringUtilities.Filters;

import java.util.TreeSet;

/**
 * Filter words for length, number of alphabetic characters and stop words.
 * <p>Currently uses an edited list from MySQL for the stop words.
 * @author time
 * @see StopWords
 */
public class ImperialPapersFilter extends StringFilter{

    public final int minLength;
    public final int minLetters;
    public final boolean keepRejectList;
    public String [] stopWordList;
    
    
    /**
     * Sets up filter with edited list of MySQL stop words.
     * @param minChar minimum number of characters for acceptance
     * @param minL minimum number of letters for acceptance
     * @param keepRejectList true iif want to keep a list of rejected words
     * @see StopWords#MySQL_STOP_WORDS_EDITED
     */
    public ImperialPapersFilter(int minChar, int minL, boolean keepRejectList){
        minLength=minChar;
        minLetters=minL;
        this.keepRejectList=keepRejectList;
        if (this.keepRejectList) rejectedList= new TreeSet();
        stopWordList = StopWords.MySQL_STOP_WORDS_EDITED;
    }

    /**
     * Sets up filter with stop words provided.
     * @param minChar minimum number of characters for acceptance
     * @param minL minimum number of letters for acceptance
     * @param stopWords array of stop words.
     * @param keepRejectList true iif want to keep a list of rejected words
     */
    public ImperialPapersFilter(int minChar, int minL, String [] stopWords, boolean keepRejectList){
        minLength=minChar;
        minLetters=minL;
        this.keepRejectList=keepRejectList;
        if (this.keepRejectList) rejectedList= new TreeSet();
        stopWordList = stopWords;
    }

        /**
        * Returns true if the string has at least one letter and is longer than <tt>minLength</tt>.
        * <p>Overrides the dummy method of <tt>StringFilter</tt>
        * @param s the input string
	* @return true if the string has at least  <tt>minLength</tt> letters and is longer than <tt>minLength</tt>.
	*/
    @Override
    public boolean isAcceptable(String s){
      int ls = s.length();
      if (ls<minLength) { rejectedList.add(s); return false;}
      if (isStopWord(s)){ rejectedList.add(s); return false;}
      int n=minLetters;
	  for (int i = 0; i < ls; i++){
		 if ((Character.isLetter( s.charAt(i) )) && ((--n) ==0)) return true;
          }
          return false;
    }

        /**
        * Returns true if the string has at least one letter and is longer than <tt>minLength</tt>.
        * <p>Remembers rejected strings.
        * Overrides the dummy method of <tt>StringFilter</tt>
        * @param s the input string
	* @return true if the string has at least <tt>minLength</tt> letters and is longer than <tt>minLength</tt>.
	*/
    @Override
    public boolean isAcceptableElseRemember(String s){
      int ls = s.length();
      if (ls<minLength) {if (hasRejectedList()) rejectedList.add(s); return false;}
      if (isStopWord(s)) {if (hasRejectedList()) rejectedList.add(s); return false;}
      int n=minLetters;
      for (int i = 0; i < ls; i++){
		 if ((Character.isLetter( s.charAt(i) )) && ((--n) ==0)) return true;
          }
	  if (hasRejectedList()) rejectedList.add(s); 
          return false;
    }

    /**
     * Test to see if in stop word list
     * @param s string to test
     * @return true if given string is exactly equal to a word in <tt>stopWordList</tt>, ignoring case.
     */
    public  boolean isStopWord(String s){
     for (int w=0; w<this.stopWordList.length;w++) 
         if (stopWordList[w].equalsIgnoreCase(s)) return true;
     return false;
    }
    
           /**
     * Override this description
     * @return string with a long description of the filter
     */
    @Override
    public String description(){
        return "Imperial Papers filter, rejects strings with less than "+this.minLength+" letters, and not in StopWords.MySQL_STOP_WORDS_EDITED, rejected list "+(this.hasRejectedList()?"on":"off");
    }

    /**
     * Short abbreviation.
     * IPF followed by minimum length (n) dash minimum letters (m).
     * @return IPFn-m
     */
    public String abbreviation(){return "IPF"+minLength+"-"+minLetters;}

}
