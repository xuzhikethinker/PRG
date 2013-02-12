/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.StringUtilities.Filters;

import JavaNotes.TextReader;
import TimUtilities.StringUtilities.Stemmers.Porter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Filters then stems words in a file.
 * <p>The filter can be changed easily by recoding but currently is an
 * {@link TimUtilities.StringUtilities.Filters.ImperialPapersFilter}.
 * This filter may include stop words being removed.
 * Output is list in another file along with lists of words removed and
 * stemmed.
 * <P><b>Current filters do not deal with punctuation, hyphens and apostrophes</b>
 * @author time
 */
public class TextFileProcessor {
    
    
     /**
       * Maximum number of items on a line
       */
       public int nmax=1000;

       /**
        * Process a text file.
        * <p>Entries are filtered and stemmed.
        *
        * @param args
        */
   public static void main(String[] args)
    {
      System.out.println("TextFileProcessor arguments are:");
      System.out.println("TextFileProcessor :<inputFullFileName> :<outputFileNameRoot>"+
              ":<ignoreColumn> :<stemmerOn>");

      //First arg chooses input file
      //String inputFileName="input/RAEmanTestPTinputBVNLS.dat";
      //String inputFileName="input/MSC-2010classinputBVNLS.dat";
      //String inputFileName="input/MSC-2010subclassinputBVNLS.dat";
      String inputFileName="input/EBRP5ptinputBVNLS.dat";
      //String inputFileName="netrev/netrev.dat";
      //String inputFileName="input/netrevindex.txt";
      int ano=0;
      if (args.length>ano ) inputFileName=args[ano];
      System.out.println("--- Using input file "+inputFileName);


      //Second arg chooses second community file
      //String outputNameRoot="output/RAEmanTestStemPT";
      //String outputNameRoot="output/RAEmanStemPT";
      //String outputNameRoot="output/netrevindexStemIPF";
      //String outputNameRoot="output/MSC-2010class";
      //String outputNameRoot="output/MSC-2010subclass";
      String outputNameRoot="output/EBRP5pt";
      ano++;
      if (args.length>ano ) outputNameRoot=args[ano];
      System.out.println("--- Using output file name root "+outputNameRoot);

      //This arg chooses column to be ignored (the index column)
      int ignoreColumn = 1;
      ano++;
      if (args.length>ano ) ignoreColumn = Integer.parseInt(args[ano]);
      System.out.println("--- Ignoring column "+ignoreColumn);

      int mode=-1;
      String outputExt="UNKNOWN";
      if (inputFileName.endsWith("inputELS.dat")) {
          mode=1;
          outputExt="outputELS.dat";
      }
      if (inputFileName.endsWith(".txt")) {
          mode=1;
          outputExt=".txt";
      }
      if (inputFileName.endsWith("inputBVNLS.dat")) {
          mode=2;
          outputExt="outputBVNLS.dat";
      }

      boolean stemmerOn=true;
      ano++;
      if (args.length>ano ) stemmerOn =StringFilter.trueString(args[ano].charAt(1));
      System.out.println("--- Porter stemming "+StringFilter.onOffString(stemmerOn));


      String inputSep=" "; //\t";
      String sep="\t";
      TreeMap<String,String> stemMap = new TreeMap();
      TreeMap<String,Integer> acceptedCountMap = new TreeMap();

      ImperialPapersFilter ipf = new ImperialPapersFilter(2,3,true);
      int sampleFrequency=1;
      boolean showProcess=true;
      boolean convertIgnoreColumn=false;

      switch (mode) {
          case 1:
          System.out.println("--- Processing file assuming simple list of words ");
          convertIgnoreColumn=false;
          processWordListFile(inputFileName, outputNameRoot+outputExt,
            ignoreColumn, convertIgnoreColumn, sep, 
            stemMap, acceptedCountMap,
            ipf, sampleFrequency, showProcess);
          break;

          case 2:
              System.out.println("--- Processing file assuming index column ");
              processIndexSentenceFile(inputFileName, outputNameRoot+outputExt,
                inputSep, sep, 
                stemmerOn, stemMap, acceptedCountMap,
                ipf, sampleFrequency, showProcess);
               break;

//          case 3:
//          System.out.println("--- Processing file assuming simple list of words, no stemming");
//          convertIgnoreColumn=false;
//          stemMap=null;
//          processWordListFile(inputFileName, outputNameRoot+outputExt,
//            ignoreColumn, convertIgnoreColumn, sep,
//            stemMap, acceptedCountMap,
//            ipf, sampleFrequency, showProcess);
//          break;
//

          default: throw new RuntimeException("Unknown mode "+mode);
      }
//      if (mode==2){
//          System.out.println("--- Processing file assuming index column ");
//          processIndexSentenceFile(inputFileName, outputNameRoot+outputExt,
//            inputSep, sep, stemMap,
//            ipf, sampleFrequency, showProcess);
//      System.out.println("Applied Filter "+ipf.description());
////      System.out.println("Producing file "+postStemmedFileName);
//      String outputStemMapFile = outputNameRoot+"_StemMap.dat";
//      TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputStemMapFile, sep, stemMap, true);
//      String outputRejectFile = outputNameRoot+"_RejectList.dat";
//      ipf.FileOutputRejectedList(outputRejectFile, showProcess);
//      }


      System.out.println("Applied Filter "+ipf.description());
//      System.out.println("Producing file "+postStemmedFileName);

      if (stemMap!=null){
                String outputStemMapFile = outputNameRoot+"ptStemMap.dat";
                TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputStemMapFile, sep, stemMap, true);
      }

      if (acceptedCountMap!=null){
                String outputStemMapFile = outputNameRoot+"AcceptedCountMap.dat";
                TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputStemMapFile, sep, acceptedCountMap, true);
      }

      String outputRejectFile = outputNameRoot+"ptRejectList.dat";
      ipf.FileOutputRejectedList(outputRejectFile, showProcess);

   }

    /**
      * Processes a list of indexed sentences from file.
      * <p>Each line on input has first entry as the index string, with 
      * remaining entries being parts of the inputLine.  The latter
      * may be broken up into pieces but it is assumed that these breaks
      * are always word breaks too.  If an index is repeated only the
      * first case is processed, remaining lines with that index are skipped.
      * <p>Output is a file where each line has the index followed by a sequence of entries.
      * These are the terms extracted from the inputLine in the order in which they were found.
      * A hyphen, a single quote (for apostrophes) and a question mark 
      * (for unknown characters) are simply skipped.
      * All other punctuation in the inputLine is treated as word break
      * (e.g. a space, a tab, quotes).
      * Stop words and any limits on number of letters or numbers allowed etc
      * are encoded in the StringFilter given.
      * <p>Output can optionally include a map from words found to stems and from
      * stemmed words to the number of their occurances. It is recommend that
      * TreeMaps are used both cases.
      * @param inputFileName full file name including directory of file with input data
      * @param outputFileName full file name including directory of output file
      * @param indexColumn column to be read as index for this inputLine.  Columns numbered from 1.
      * @param inputLineColumn column to be read as index for this inputLine.  Columns numbered from 1.
      * @param inputSep separation character used in input file to separate entries.
      * @param sep separation character used in output file to separate entries.
      * @param stemmerOn if true words are Porter stemmed.
      * @param stemMap if not null, is returned with map from words altered to stemmed words
      * @param acceptedCountMap if not null, is returned with map from accepted words number of occurances
      * @param sf string filter to apply
      * @param sampleFrequency number of lines to skip, 1 or less and all are taken
      * @param showProcess show process on screen.
      */
    static public void processIndexSentenceFile(String inputFileName, String outputFileName,
            String inputSep, String sep,
            boolean stemmerOn,
            Map<String,String> stemMap,
            Map<String,Integer> acceptedCountMap,
            StringFilter sf, int sampleFrequency, boolean showProcess)
    {
        boolean makeMap=true;
        if (stemMap==null) makeMap=false;

        boolean readSome=true; // store only every sampleFrequency lines
        if (sampleFrequency<2) readSome=false;

        boolean makeAcceptedList=true;
        if (acceptedCountMap==null) makeAcceptedList=false;
        int ccc=-1;

        TreeSet<String> indexSet = new TreeSet();

        Porter stemmer =null;
        if (stemmerOn) stemmer =new Porter();

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
        //int firstSep=0;
        String w;
        //String inputLine;
        String inputLine;
        StringBuffer term = new StringBuffer(200);
        int cn=0;
        char c;
        String index;
        //String [] word;
        try {
            while (!data.eof()){  // Read until end-of-file.
                linenumber++;
                column=0;
                inputLine = data.getln();
                if (readSome && ((linenumber%sampleFrequency)!=1)) {continue;}
                cn=inputLine.length();
                for (int n=0; n<inputSep.length(); n++) {
                    int cn1=inputLine.indexOf((int) inputSep.charAt(n));
                    if (cn1>=0) cn=Math.min(cn,cn1);
                }
                if (cn==inputSep.length()) throw new RuntimeException("*** Too few columns on line "+linenumber+" wanted at least two columns.");
                index=inputLine.substring(0, cn);
                if (indexSet.contains(index)) {continue;} // don't process same title twice
                indexSet.add(index); // 
                PS.print(index); // this is the index
                if (showProcess) System.out.print(linenumber+": \t" + index+"- \t");
                cn++;
                // Found the index so process rest of line as text.
                while (cn<inputLine.length()){
                    //looking for next word
                    term.setLength(0);
                    while (cn<inputLine.length()){
                      c=inputLine.charAt(cn++);
                      if ((c>='a') && (c<='z') ) {term.append(c); continue;}
                      if ((c>='A') && (c<='Z') ) {term.append(((char)(c+32))); continue;}
                      if ((c>='0') && (c<='9') ) {term.append(c); continue;}
                      if ((c=='\'') || (c=='-')  || (c=='?') ) {continue;} // hyphens and apostrophes are ignored
// Test for abreviations
//                      if ((c=='.')){
//                          if ( (cn<inputLine.length()) && (inputLine.charAt(cn)==' ' || ch == '\n' || ch == '\t') ) )
//                      }
                       break; //any other character is taken to be a word break and is ignored
                    }     // 2nd while cn<sent
                   if (term.length()==0) continue; // must be two unacceptable characters in a row
                   w=term.toString();  // a non trivial word has been found
                    // only stem and filter non-ignored Columns
                   if(sf.isAcceptableElseRemember(w)) {
                       String s = (stemmerOn?stemmer.stem(w):w);
                       PS.print(sep+s);
                       if (makeAcceptedList){
                           ccc=1;
                           if (acceptedCountMap.containsKey(s)){
                               ccc=acceptedCountMap.get(s)+1;
                           }
                           acceptedCountMap.put(s, ccc);
                       }

                       if ((showProcess) && (s.length() != w.length())) {
                         System.out.print("\t"+w+"->" + s+" ");
                       }
                      if (makeMap && (s.length() != w.length())) stemMap.put(w, s);
                   } // eo if acceptable
                   else if (showProcess) System.out.print("\t"+w+ "<-### ");
                } //while cn<sent outer loop
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

        System.out.println(" Finished " + outputFileName+" processed "+indexSet.size()+" sentences, stemmed "+stemMap.size()+" words and filtered out "+sf.numberRejectedString()+" words");
        return;
    }

    /**
      * Reads in list of strings from file.
      * <p>Each line has entries separated by whitespace. Recommend that a TreeMap is used.
      * @param inputFileName full file name including directory of file with input data
      * @param outputFileName full file name including directory of output file
      * @param ignoreColumn do not stem or filter this column. First column is numbered one.  A zero or negative value means all columns processed.
      * @param convertIgnoreColumn if a column is being ignored you can convert it to a number equal to the line number minus 1 (so counts from 0).
      * @param stemMap if not null, is returned with map from words altered to stemmed words
      * @param acceptedCountMap if not null, is returned with map from accepted words number of occurances
      * @param sf string filter to apply
      * @param sampleFrequency number of lines to skip, 1 or less and all are taken
      * @param showProcess show process goin on screen.
      */
    static public void processWordListFile(String inputFileName, String outputFileName,
            int ignoreColumn, boolean convertIgnoreColumn, String sep,
            Map<String,String> stemMap,
            Map<String,Integer> acceptedCountMap,
            StringFilter sf, int sampleFrequency, boolean showProcess)
    {

        boolean makeMap=true;
        if (stemMap==null) makeMap=false;

        boolean makeAcceptedList=true;
        if (acceptedCountMap==null) makeAcceptedList=false;
        int ccc=-1;

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
            throw new RuntimeException("Output file " + outputFileName+" not opened, "+e);
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
                        if (convertIgnoreColumn) PS.print((linenumber-1) + sep);
                        else PS.print(w + sep);
                        continue;
                    }
                   // only stem and filter non-ignored Columns
                   if(sf.isAcceptableElseRemember(w)) {
                       String s = stemmer.stem(w);
                       PS.print(s + sep);
                       if (makeAcceptedList){
                           ccc=1;
                           if (acceptedCountMap.containsKey(s)){
                               ccc=acceptedCountMap.get(s)+1;
                           }
                           acceptedCountMap.put(s, ccc);
                       }
                       if ((showProcess) && (s.length() != w.length())) {
                         System.out.print("->" + s);
                       }
                      if (makeMap && (s.length() != w.length())) stemMap.put(w, s);
                   } // eo if acceptable
                   else if (showProcess) System.out.print("<-### "+sep);
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
