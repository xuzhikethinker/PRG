/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import JavaNotes.TextReader;
import TimUtilities.StringUtilities.Stemmers.Porter;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

/**
 * Stems words in a file.
 * @author time
 */
public class FileStemmer {
    
    
          /**
       * Maximum numer of items on a line
       */
       public int nmax=1000;
 
     

         /**
      * Reads in list of strings from filefilename.
      * <p>Each line has entries separated by whitespace. Recommend that a TreeMap is used.
      * @param inputFileName full file name including directory of file with input data
      * @param outputFileName full file name including directory of output file
      * @param ignoreColumn do not stem or filter this column. First column is numbered one and zero or negative means all columns processed.
      * @param stemMap if not null, is returned with map from words altered to stemmed words
      * @param sf string filter to apply
      * @param sampleFrequency number of lines to skip, 1 or less and all are taken   
      * @param showProcess show process goin on screen.
      */
    static public void processStringFile(String inputFileName, String outputFileName, 
            int ignoreColumn, String sep, 
            Map<String,String> stemMap, 
            StringFilter sf, int sampleFrequency, boolean showProcess)
    {
        
        boolean makeMap=true;
        if (stemMap==null) makeMap=false;
        
        boolean readSome=true; // store only every sampleFrequency lines
        if (sampleFrequency<2) readSome=false;
        
        Porter stemmer =new Porter();
        
//        System.out.println(" Opening Input File:  " + inputFileName);
//        TextReader data = new TextReader(new FileReader(inputFileName));        

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
                column=0;
                if (readSome && ((linenumber%sampleFrequency)!=1)) {data.getln(); continue;}
                if (showProcess) System.out.print(linenumber+": ");
                while (!data.eoln()){
                    w = data.getWord();
                    column++;
                    if (showProcess) System.out.print("\t" + w);
                    if (column == ignoreColumn){
                        PS.print(w + sep);
                        continue;
                    }
                   // only stem and filter non-ignored Columns
                   if(sf.isAcceptableElseRemember(w)) {
                       String s = stemmer.stem(w);
                       PS.print(s + sep);
                       if ((showProcess) && (s.length() != w.length())) {
                         System.out.print("->" + s);
                       }
                      if (makeMap && (s.length() != w.length())) stemMap.put(w, s);
                   } // eo if acceptable
                   else if (showProcess) System.out.print(w + "<-### "+sep);                    
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
        
        System.out.println(" Finished " + outputFileName+" stemmed "+stemMap.size()+" words and filtered out "+sf.numberRejectedString()+" words");
        return;
    }
    
    
    /**
     * Prints out the map from words to stemmed words
     * @param PS PrintStream such as System.out
     * @param sep separation string
     * @param stemMap keys are words, values are the stemmed words
     */
    static public void printMap(PrintStream PS, String sep, Map<String,String> stemMap){
        Set<String> keys = stemMap.keySet();
        for (String k : keys) {PS.println(k+sep+stemMap.get(k));}
    }
    
    
    /**
     * Tests to see if a string is only made of letters
     * @param s string to be tested
     * @return true if string only contains characters which are letters as defined by <tt>Character</tt> wrapper class
     */
    static public boolean isAlphabetic(String s){
     for (int c=0; c<s.length();c++ ) if (!Character.isLetter(s.charAt(c))) return false;
     return true;
 }
    
}
