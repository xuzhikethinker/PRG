/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import JavaNotes.TextReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
//import java.lang.Integer;
//import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Synchronises the list of papers in one file with that in another.
 * <p>Also converts the names being used to synchronise into numbers, the
 * map being outpout in <tt>NameToIndex.dat</tt> files.
 * <p>That is it creates a list of paper ids common to both files,
 * creates a map of these ids to numbers (in alphabetical order)
 * and finally outputs copies (with <tt>.sync.dat</tt> extensions)
 * of both files.
 * @author time
 */
public class PaperSynchoniser {
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static boolean infoOn=true;
  
   /**
   * Synchronises the list of papers in one file with that in another.
   * <p>Also converts the names being used to synchronise into numbers, the
   * map being outpout in <tt>NameToIndex.dat</tt> files.
   * <p>That is it creates a list of paper ids common to both files,
   * creates a map of these ids to numbers (in alphabetical order)
   * and finally outputs copies (with <tt>.sync.dat</tt> extensions)
   * of both files.
   * @param args command line arguments
   */
     public static void main(String[] args) 
    { 
      System.out.println("PaperSynchoniser Arguments: (input file 1) (input file 2) (ouput file name of name to index list) (output file name 1) (output file name 2)"); 
      
      //First arg chooses  file with papers listed
      //String fileName1="input/ICteststemptinputBVNLS.dat";
      //String fileName1="input/ICNS090729stemptinputBVNLS.dat";
      String fileName1="input/ICNS090224stemptinputBVNLS.dat";
      int ano=0;
      if (args.length>ano ) fileName1=args[ano];
      System.out.println("--- File defining vertex set "+fileName1);
      
      //Second arg chooses second file which needs to be synchronised
      //String fileName2="input/ICtest_psecinputBVNLS.dat";
      //String fileName2="input/ICNS090224npstemppew0020inputELS.dat";;
      String fileName2="input/IC090521_psecinputBVNLS.dat";
      ano++;
      if (args.length>ano ) fileName2=args[ano];
      System.out.println("--- File to be transformed "+fileName2);
      
      //Output file for name to index 
      String fullNameToIndexFileName=fileName1+".NameToIndex.dat";
      //String fileName2="input/ICNS090224npstemppew0020inputELS.dat";;
      ano++;
      if (args.length>ano ) fullNameToIndexFileName=args[ano];
      System.out.println("--- Name to Index file "+fullNameToIndexFileName);
      
      //Output file for copy of synchronised of first file 
      String fullOutputFileName1=fileName1+".sync.dat";
      //String fileName2="input/ICNS090224npstemppew0020inputELS.dat";;
      ano++;
      if (args.length>ano ) fullOutputFileName1=args[ano];
      System.out.println("--- Output file 1 "+fullOutputFileName1);
      
      //Output file for copy of synchronised of second file
      String fullOutputFileName2=fileName2+".sync.dat";
      //String fileName2="input/ICNS090224npstemppew0020inputELS.dat";;
      ano++;
      if (args.length>ano ) fullOutputFileName2=args[ano];
      System.out.println("--- Output file 2 "+fullOutputFileName2);
      
     FileInput fi = new FileInput((infoOn?0:-2));
     String cc="";
     int columnRead=1;
     boolean forceLowerCase=true;
     TreeSet<String> vertexLL1 = new TreeSet();
     vertexLL1.addAll(fi.readStringColumnFromFile(fileName1, 
            cc, columnRead, forceLowerCase));

     columnRead=1;
     forceLowerCase=true;
     TreeSet<String> vertexLL2 = new TreeSet();
     vertexLL2.addAll(fi.readStringColumnFromFile(fileName2, 
            cc, columnRead, forceLowerCase));

     // make set of common vertices (intersection)
     TreeSet<String> vertexAll = new TreeSet();
     vertexAll.addAll(vertexLL1);
     vertexAll.retainAll(vertexLL2);
     if (infoOn) System.out.println("Found "+vertexAll.size()+" common vertices from "+vertexLL1.size()+" in file 1 and "+vertexLL2.size()+" in file 2");

     //  convert common vertices to numbers
     TreeMap<String,Integer> vertexNameToIndex = new TreeMap();
     int index=0;
     for (String name: vertexAll) vertexNameToIndex.put(name, index++);
     
     //for (String k: vertexNameToIndex.keySet()) System.out.println(k+" : "+vertexNameToIndex.get(k));
            
     
     String sep="\t";
     
     
     String [] headerLines = new String[1];
     headerLines[0]="#name"+sep+"index";     
     FileOutput.map(vertexNameToIndex, fullNameToIndexFileName, headerLines, sep, infoOn);
     
     boolean convertToIndex = true;
     int vertexColumn=1;
     synchroniseVertexNeighbourList(vertexNameToIndex, fileName1, fullOutputFileName1,
              cc, sep, vertexColumn, convertToIndex, forceLowerCase, infoOn);
      
     synchroniseVertexNeighbourList(vertexNameToIndex, fileName2, fullOutputFileName2,
              cc, sep, vertexColumn, convertToIndex, forceLowerCase, infoOn);
      
     }
     
        /**
         * Makes a copy of a vertex neighbour list file with only vertices in given list.
         * Reads in a file (typically an vertex neighbour list) and outputs a copy except for
         * any lines whose first entry is not in given vertex list.
         * <p>Each line has entries separated by whitespace.
         * Comment lines are always copied.
         * @param vertexNameToIndex map from vertex name to an index
         * @param fullInputFileName full filename of input file to be copied.
         * @param fullOutputFileName full filename of output file
         * @param cc if, after any white space, first word starts with this string then line is treated as a comment line.
         * @param sep sepation character, e.g. tab
         * @param vertexColumn this is the column to be compared against the given map of vertices (first column is one)
         * @param convertToIndex convert the vertex names to an index as given by the map provided
         * @param forceLowerCase force all strings to be lower case
         * @param infoOn true if want more info printed out
         */
    static public int synchroniseVertexNeighbourList(Map<String,Integer> vertexNameToIndex, String fullInputFileName, String fullOutputFileName, String cc, String sep, 
           int vertexColumn,
           boolean convertToIndex, boolean forceLowerCase,
            boolean infoOn)
    {
        if (vertexColumn<1) throw new RuntimeException("first column is numbered 1, column with vertices given as "+vertexColumn);        
        int testColumn=vertexColumn-1;
        int res=0;  // error code.
        boolean dontTestForCommentLine=false;
        if (cc.length()==0) dontTestForCommentLine=true;
        
        

        //set up input file
        TextReader data=FileInput.openFile(fullInputFileName);
        if (data==null) {
             System.err.println("*** synchroniseVertexNeighbourList input file " + fullInputFileName+" not opened");
             return -1;
        }
        if (infoOn) System.out.println("Starting to do vertex synchronisation on first column of file " + fullInputFileName);

        // Make a copy of vertices and note when they have been found.
        TreeSet<String> vertexFoundLL= new TreeSet();
        TreeSet<String> vertexNotFound = new TreeSet();  // vertices in vertexNameToIndex not in file
        //set up output file
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fullOutputFileName);
            PS = new PrintStream(fout);
            
        } catch (FileNotFoundException e) {
            System.err.println("**** synchroniseVertexNeighbourList output file " + fullOutputFileName + " not opened, " + e.getMessage());
            return -2;
        }
        

        int linenumber=0;
        int verticesInput=0;
        int verticesOutput=0;
        try {
            // Read the data from the input file.
            String [] numbers = new String[1000];
            int column=0;
            String vertex;
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   column=0;
                   // Read until end-of-line.
                   if (forceLowerCase) while (data.eoln() == false) numbers[column++] = data.getWord().toLowerCase();
                   else                while (data.eoln() == false) numbers[column++] = data.getWord();
                   if (vertexColumn>column) throw new RuntimeException("on line "+linenumber+" found "+ column+" columns (first is numbered 1) but synchronising on column "+vertexColumn); 
                   try {
                     // next tests for first word starting for comment line string but only if this is a nontrivial dstring   
                     vertex=numbers[testColumn];
                     if (dontTestForCommentLine || !vertex.startsWith(cc))  {
                         verticesInput++;
                         if (!vertexNameToIndex.containsKey(vertex)) { continue;}
                         verticesOutput++;
                         vertexFoundLL.add(vertex);
                     }
                     // write line out to output file if a comment line or if first column is in vertex list
                     if (convertToIndex) for (int c=0; c<column; c++)  PS.print(((testColumn==c)? vertexNameToIndex.get(vertex):numbers[c])+(c==column?"":sep)); 
                     else for (int c=0; c<column; c++)  PS.print(numbers[c]+(c==column?"":sep)); 
                     PS.println();
                     
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + " of input file, " + e.getMessage());
                    }

               }//eofile

            for (String name:vertexNameToIndex.keySet()) if (!vertexFoundLL.contains(name)) vertexNotFound.add(name);
            
            if (infoOn) {
                System.out.println("Finished vertex synchronisation on first column of file " + fullInputFileName+" producing " + fullOutputFileName);
                System.out.println("    "+linenumber+" input lines found "+verticesInput+" lines with vertices, wrote "+verticesOutput+" lines with vertices in given list.");
                System.out.println("      Given "+vertexNameToIndex.size()+" distinct input vertices, "+vertexNotFound.size()+" were not used.");
                System.out.println("      Output "+vertexFoundLL.size()+" distinct output vertices.");
            }
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the data from the input file.
            res=-3;
            throw new RuntimeException("*** File Error in " + fullInputFileName+" or "+ fullOutputFileName + ", " + e.getMessage());

        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            try{
                data.close();
                fout.close();
            }catch (IOException e) {
                throw new RuntimeException("*** File Error closing " + fullInputFileName+" or "+ fullOutputFileName + ", " + e.getMessage());
            }                
        }// eo finally 
        
        // write out information file
        String infoOutputFileName = fullOutputFileName+".info.txt";
        try {
            fout = new FileOutputStream(infoOutputFileName);
            PS = new PrintStream(fout);
        } catch (FileNotFoundException e) {
            res=  -2;
            throw new RuntimeException("**** synchroniseVertexNeighbourList information file " + infoOutputFileName + " not opened, " + e.getMessage());
        }
            PS.println("Vertex synchronisation on first column of file " + fullInputFileName+" producing " + fullOutputFileName);
            PS.println("    "+linenumber+" input lines found "+verticesInput+" lines with vertices, wrote "+verticesOutput+" lines with vertices in given list.");
            PS.println("      Given "+vertexNameToIndex.size()+" distinct input vertices, "+vertexNotFound.size()+" were not used.");
            PS.println("      Output "+vertexFoundLL.size()+" distinct output vertices.");
            if (vertexNotFound.size()>0) {
              PS.println("Vertices in input list not found in synchronised file");
              PS.println("name"+sep+"index");
              for (String name: vertexNotFound) PS.println(name+sep+vertexNameToIndex.get(name));
            }
        try {
                            }//eo try
        catch (RuntimeException e) {
            res=-4;
            throw new RuntimeException("*** File Error in information output file "+ infoOutputFileName + ", " + e.getMessage());
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            try{
                fout.close();
            }catch (IOException e) {
                throw new RuntimeException("*** File Error closing "+infoOutputFileName + ", " + e.getMessage());
            }
                
        } 

        return res;
    }



}
