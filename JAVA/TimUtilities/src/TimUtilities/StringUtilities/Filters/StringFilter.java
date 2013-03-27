/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.StringUtilities.Filters;

import JavaNotes.TextReader;
import TimUtilities.FileUtilities.FileOutput;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.TreeSet;

/**
 * Base class of  filters of strings.
 * <p>This class must not be instantiated but extensions can be.
 * The <tt>isAcceptable</tt> and <tt>isAcceptableElseRemember</tt>
 * and <tt>description</tt> classes should be overrided 
 * as the versions here are trivial.
 * The constructors may also need overriding.
 * @author time
 */
public class StringFilter {
    
    /**
     * If initialised keeps a list of the rejected strings.
     */
     protected TreeSet<String> rejectedList;
       /**
        * This class must not be instantiated but extensions can be.
        */
    protected StringFilter (){}

       /**
        * This class must not be instantiated but extensions can be.
        * <p>Use through <tt>StringFilter.<em>class</em></tt>
        */
    protected StringFilter (boolean rejectedListOn){
    if (rejectedListOn) rejectedList = new TreeSet();
    }

    /**
     * Test is has a list of rejected words.
     * @return true (false) if has (has not) a list of rejected words.
     */
    public boolean hasRejectedList(){return (rejectedList!=null);}
    
    /**
	* Returns true if the string passes test.
        * <p>Extend this class and override this method.
        * This is a dummy class and always returns false.
	* @param s the input string
	* @return false.
	*/
   public boolean isAcceptable(String s)
   {
       return false;
   }
    /**
	* Returns true if the string passes test, otherwise false and stores rejected string.
        * <p>Extend this class and override this method.
        * This is a dummy class and always returns false.
	* @param s the input string
	* @return false.
	*/
   public boolean isAcceptableElseRemember(String s)
   {
       if (rejectedList!=null) rejectedList.add(s);
       return false;
   }
   
       /**
     * Override this description
     * @return string with a long description of the filter
     */
    public String description(){
        return "Basic String filter, always false, rejection list "+(this.hasRejectedList()?"on":"off");
    }

   
	/**
	* Returns true if the string has at least one letter.
	* @param s the input string
	* @return true if the string has at least one letter
	* @see java.lang.Character#isLetter(char).
	*/
   public static boolean hasLetter(String s)
   {
	  int ls = s.length();
	  for (int i = 0; i < ls; i++)
		 if (Character.isLetter( s.charAt(i) )) return true;
	  return false;
   }  

	/**
	* Returns true if the string has at least <tt>n</tt> letters.
        * @param  s the input string
         * @param n num,ber of letters string must have
	* @return true if the string has at least <tt>n</tt> letters.
	* @see java.lang.Character#isLetter(char).
	*/
   public static boolean hasLetters(String s, int n)
   {
	  int ls = s.length();
	  for (int i = 0; i < ls; i++)
		 if (Character.isLetter( s.charAt(i) )) if ((--n)==0) return true;
	  return false;
   }  

    
    /**
	* Returns true if the string consists of digits.
	* @param s the input string
	* @return true if the string consists only of digits.
	* @see java.lang.Character#isDigit(char).
	*/
   public static boolean isDigit(String s)
   {
	  int ls = s.length();
	  for (int i = 0; i < ls; i++)
		 if (!Character.isDigit( s.charAt(i) )) return false;
	  return true;
   }  
    
   
	/**
	* Returns true if the string consists of letters.
	* @param s the input string
	* @return true if the string consists only of letters or digits.
         * @see java.lang.Character#isLetterOrDigit(char).
	*/
   public static boolean isLetter(String s)
   {
	  int ls = s.length();
	  for (int i = 0; i < ls; i++)
		 if (!Character.isLetter( s.charAt(i) )) return false;
	  return true;
   }  
    
	/**
	* Returns true if the string consists of letters or digits.
	* @param s the input string
	* @return true if the string consists only of letters.
	* @see java.lang.Character#isLettert(char).
	*/
   public static boolean isLetterOrDigit(String s)
   {
	  int ls = s.length();
	  for (int i = 0; i < ls; i++)
		 if (!Character.isLetterOrDigit( s.charAt(i) )) return false;
	  return true;
   }

   /**
    * Returns remaining part of string after last occurence of given string.
    * <p>If substring f is not found whole s string is returned.
    * If f is at end of string a null string is returned.
    * @param s input string
    * @param f string sequence to locate
    * @return last part of s after f
    */
   public static String afterLastString(String s, String f)
   {
       int c=s.lastIndexOf(f);
       if (c<0) c=0; else c=c+f.length();
       if (c<s.length()) return s.substring(c);
       return "";
   }

   /**
    * Returns part of string before last occurance of given string.
    * <p>If substring f is not found whole s string is returned.
    * If f is at start of string a null string is returned.
    * @param s input string
    * @param f string sequence to locate
    * @return part of s before last f occurance
    */
   public static String beforeLastString(String s, String f)
   {
       int c=s.lastIndexOf(f);
       if (c<0) c=s.length();
       return s.substring(0,c);
   }

   /**
    * Number of rejected strings.
    * @return string giving number of rejected strings, or unknown if rejectedList not kept
    */
   public String numberRejectedString(){
   if (rejectedList==null) return "unknown";
   return Integer.toString(rejectedList.size());
   }
   /**
     * Prints out the list of rejected words.
     * @param PS PrintStream such as System.out
     */
    public void printRejectedList(PrintStream PS){
        if (rejectedList!=null) for (String r : rejectedList) {PS.println(r);}
    }

   /**
     * Prints out the list of rejected words.
     * @param PS PrintStream such as System.out
     * @param C any collection C
     */
    public void FileOutputRejectedList(String fullfilename, boolean messagesOn) 
    {
       if (rejectedList==null) {
           if (messagesOn) System.err.println("!!! Warning no rejected strings list in StringFilter, no file "+fullfilename+" produced");
           return;
       }
       FileOutput.FileOutputCollection(fullfilename, rejectedList, messagesOn);
    }

  /**
          * Returns true only if string starts with t, T, y or Y, otherwise false
          * @param s input string
          * @return true only if string starts with t, T, y or Y, otherwise false.
          */
         static public boolean trueString(String s){
             if (s.length()<1) return false;
             return trueString(s.charAt(0));
         }
  
  /**
          * Returns true only if char is 't', 'T', 'y' or 'Y'.
          * @param c input 
          * @return true only if char is 't', 'T', 'y' or 'Y'.
          */
         static public boolean trueString(char c){
             if (c=='t' || c=='T' || c=='y' || c=='Y') return true;
             return false;
         }
         
        /**
          * Returns ON or OFF string
          * @param b 
          * @return ON or OFF depending on b
          */
         static public String onOffString(boolean b){
             return (b?"ON":"OFF");
         }

         /**
          * Converts a string to a list of decimals.
          * @param s input string
          * @param sep separator between decimals
          * @return list of decimal codes representing input 
          */
         static public String toDecimal(String s, String sep){
             if (s.length()<1) return "";
             StringBuffer outs= new StringBuffer(50);
             outs.append(String.format("%3d",s.charAt(0)));
             for (int c=1; c<s.length(); c++) {
                 outs.append(sep);
                 outs.append(String.format("%3d",s.charAt(c)));
             }
            return outs.toString();     
         }
         /**
          * Converts a string to a list of hexadecimal codes.
          * @param s input string
          * @param sep separator between codes
          * @return list of hexadecimal codes representing input 
          */
         static public String toHex(String s, String sep){
             if (s.length()<1) return "";
             StringBuffer outs= new StringBuffer(50);
             outs.append(String.format("%02x",(int) s.charAt(0)));
             for (int c=1; c<s.length(); c++) {
                 outs.append(sep);
                 outs.append(String.format("%02x",(int) s.charAt(c)));
             }
            return outs.toString();     
         }
         /**
          * Strips whitespaces from start and finish of a string
          * @return string without leading or trailing whitespaces
          * @deprecated use <tt>String trim()</tt>
          */
         static public String stripSpaces(){
             return null;
         }
         
  
    
     /**
      * Reads in list of strings from filefilename.
      * <p>Each line has entries separated by whitespace. Recommend that a TreeMap is used.
      * @param inputFileName full file name including directory of file with input data
      * @param outputFileName full file name including directory of output file
      * @param noFilterColumn do not filter this column, first column is numbered one.  Zero or negative means all columns processed.
      * @param sf string filter to apply
      * @param sampleFrequency number of lines to skip, 1 or less and all are taken   
      * @param showProcess true if want process shown on screen
      */
    static public void processStringFile(String inputFileName, String outputFileName, 
            int noFilterColumn , String sep, 
            StringFilter sf, int sampleFrequency, boolean showProcess)
    {
        
        
        final boolean readSome; // store only every sampleFrequency lines
        if (sampleFrequency<2) readSome=false;
        else readSome=true;
        

        TextReader data;
        try {
            System.out.println(" Opening Input File:  " + inputFileName);
            data = new TextReader(new FileReader(inputFileName));
        } catch (FileNotFoundException e) {
          throw new RuntimeException("Input file " + inputFileName+" not found, "+e);
        }

        PrintStream PS;
        try {
            System.out.println(" Opening Output File: " + outputFileName);
            PS = new PrintStream(new FileOutputStream(outputFileName));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Output file " + outputFileName+" not found, "+e);
        }
        
        int linenumber=0;
        int column=0;
        String w;
        try {
            while (!data.eof()){  // Read until end-of-file.
                linenumber++;
                if (readSome && ((linenumber%sampleFrequency)!=1)) {data.getln(); continue;}
                if (showProcess) System.out.print(linenumber+": ");
                while (!data.eoln()){
                    w = data.getWord();
                    column++;
                    if (showProcess) System.out.print("\t" + w);
                    if ((column == noFilterColumn ) || (sf.isAcceptableElseRemember(w))) 
                        PS.print(w + sep);
                    else if (showProcess) System.out.print("<-### ");
                }//eoln
                if (showProcess) System.out.println();
                PS.println();
               }//eofile
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the data from the input file.
            throw new RuntimeException("*** Input Error on line "+linenumber+" column "+column+": " + e.getMessage());
        } finally {
            // Finish by closing the files, whatever else may have happened.
            data.close();
            PS.close();
        }
        return;
    }

    
}
