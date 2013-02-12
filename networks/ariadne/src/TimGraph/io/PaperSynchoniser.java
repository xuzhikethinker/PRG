/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import JavaNotes.TextReader;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
//import java.lang.Integer;
//import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
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
   * Synchronises a list of columns in one file with that of a second file.
   * <p>Can also convert the names being used to synchronise into numbers, (third argument)
   * the map being output in <em>fileName1</em><tt>NameToIndex.dat</tt> files
   * (unless specified by the 4th argument).
   * <p>That is it creates a list of paper ids common to both files,
   * creates a map of these ids to numbers (in alphabetical order)
   * and finally outputs copies (with <tt>.sync.dat</tt> extensions
   * to input filenames unless specified by last two arguments)
   * of both files.
   * @param args command line arguments
   */
     public static void main(String[] args) 
    { 
      System.out.println("PaperSynchoniser Arguments: (input file 1) (input file 2) (convertToIndex y,t or n,f)(ouput file name of name to index list) (output file name 1) (output file name 2)");
      
      //First arg chooses  file with papers listed
      //String fileName1="input/ICteststemptinputBVNLS.dat";
      //String fileName1="input/ICteststemppinputELS.dat";
      //String fileName1="input/ICNS090729stemptinputBVNLS.dat";
      //String fileName1="input/ICNS090224stemptinputBVNLS.dat";
      //String fileName1="input/RAEmanSubGCCinputELS.dat";
      String fileName1="input/RAEmanGCCinputELS.dat";
      int ano=0;
      if (args.length>ano ) fileName1=args[ano];
      System.out.println("--- File defining first vertex set "+fileName1);
      
      //Second arg chooses second file which needs to be synchronised
      //String fileName2="input/ICtest_psecinputBVNLS.dat";
      //String fileName2="input/ICNS090224npstemppew0020inputELS.dat";;
      //String fileName2="input/IC090521_psecinputBVNLS.dat";
      //String fileName2="input/RAEmanAuthorDisciplineinputBVNLS.dat";
      //String fileName2="input/RAEmanSubAuthorDisciplineinputBVNLS.dat";
      String fileName2="input/RAEmanAuthorInstituteinputBVNLS.dat";
      ano++;
      if (args.length>ano ) fileName2=args[ano];
      System.out.println("--- File defining second vertex set "+fileName2);


      boolean convertToIndex = false;
      ano++;
      if (args.length>ano ) convertToIndex=StringFilter.trueString(args[ano]);
      System.out.println("--- "+(convertToIndex?"C":"Not c")+"onverting names to strings");

      //Output file for name to index 
      int c=-1;
      
      c= fileName1.lastIndexOf('.');
      if (c<0) c=fileName1.length();
      String fullNameToIndexFileName=fileName1.substring(0,c)+".NameToIndex.dat";
      //String fileName2="input/ICNS090224npstemppew0020inputELS.dat";;
      ano++;
      if (args.length>ano ) fullNameToIndexFileName=args[ano];
      System.out.println("--- Name to Index file "+fullNameToIndexFileName);
      
      //Output file for copy of synchronised of first file
      int c1 = fileName1.lastIndexOf('.');
      if (c1<0) c=fileName1.length();
      String fullOutputFileName1=fileName1.substring(0,c1)+".sync.dat";
      //String fileName2="input/ICNS090224npstemppew0020inputELS.dat";;
      ano++;
      if (args.length>ano ) fullOutputFileName1=args[ano];
      System.out.println("--- Output file 1 "+fullOutputFileName1);
      
      //Output file for copy of synchronised of second file
      int c2 = fileName2.lastIndexOf('.');
      if (c2<0) c2=fileName2.length();
      String fullOutputFileName2=fileName2.substring(0,c2)+".sync.dat";
      //String fileName2="input/ICNS090224npstemppew0020inputELS.dat";;
      ano++;
      if (args.length>ano ) fullOutputFileName2=args[ano];
      System.out.println("--- Output file 2 "+fullOutputFileName2);
      
     FileInput fi = new FileInput((infoOn?0:-2));
     String cc="";
     TreeSet<Integer> vertexColumnSet1= new TreeSet();
     vertexColumnSet1.add(1);
     if (fileName1.endsWith("ELS.dat") || fileName1.endsWith("EL.dat") ) vertexColumnSet1.add(2);
     boolean forceLowerCase=true;
     TreeSet<String> vertexLL1 = new TreeSet();
     vertexLL1.addAll(fi.readStringColumnsFromFile(fileName1,
            cc, vertexColumnSet1, forceLowerCase));

     TreeSet<Integer> vertexColumnSet2= new TreeSet();
     vertexColumnSet2.add(1);
     if (fileName2.endsWith("ELS.dat") || fileName2.endsWith("EL.dat") ) vertexColumnSet2.add(2);
     forceLowerCase=true;
     TreeSet<String> vertexLL2 = new TreeSet();
     vertexLL2.addAll(fi.readStringColumnsFromFile(fileName2,
            cc, vertexColumnSet2, forceLowerCase));

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
     
     synchroniseVertexList(vertexNameToIndex, 
              fileName1, fullOutputFileName1, fileName2,
              cc, sep, vertexColumnSet1, convertToIndex, forceLowerCase, infoOn);
      
     synchroniseVertexList(vertexNameToIndex,
              fileName2, fullOutputFileName2, fileName1,
              cc, sep, vertexColumnSet2, convertToIndex, forceLowerCase, infoOn);
      
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
         * @param fullOtherInputFileName full filename of second input file needed for synchronisation
         * @param cc if, after any white space, first word starts with this string then line is treated as a comment line.
         * @param sep sepation character, e.g. tab
         * @param vertexColumnList list of columns to be compared
         * @param convertToIndex convert the vertex names to an index as given by the map provided
         * @param forceLowerCase force all strings to be lower case
         * @param infoOn true if want more info printed out
         */
    static public int synchroniseVertexList(Map<String,Integer> vertexNameToIndex, 
            String fullInputFileName, String fullOutputFileName, String fullOtherInputFileName,
            String cc, String sep,
           Set<Integer> vertexColumnList,
           boolean convertToIndex, boolean forceLowerCase,
            boolean infoOn)
    {
        int maxColumn=-1;
        for (Integer vertexColumn: vertexColumnList){
            if (vertexColumn<1) throw new RuntimeException("first column is numbered 1, column with vertices given as "+vertexColumn);
            maxColumn=Math.max(vertexColumn,maxColumn);
        }
        int res=0;  // error code.
        boolean dontTestForCommentLine=false;
        if (cc.length()==0) dontTestForCommentLine=true;
        
        //set up input file
        TextReader data=FileInput.openFile(fullInputFileName);
        if (data==null) {
             System.err.println("*** synchroniseVertexList input file " + fullInputFileName+" not opened");
             return -1;
        }
        if (infoOn) {
            System.out.print("Starting to do vertex synchronisation on columns ");
            for (Integer vertexColumn: vertexColumnList)System.out.print(vertexColumn+", ");
            System.out.println(" of file " + fullInputFileName);
            if (convertToIndex) System.out.println("Converting names to indices");
        }

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
            throw new RuntimeException("**** synchroniseVertexList output file " + fullOutputFileName + " not opened, " + e.getMessage());
            //return -2;
        }
        

        int linenumber=0;
        int verticesInput=0;
        int verticesOutput=0;
        String [] vertexFound = new String[vertexColumnList.size()];
        try {
            // Read the data from the input file.
            String [] numbers = new String[1000];
            int column=0;  // number of columns on each line
            String vertex;
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   column=0;
                   // Read until end-of-line.
                   if (forceLowerCase) while (data.eoln() == false) numbers[column++] = data.getWord().toLowerCase();
                   else                while (data.eoln() == false) numbers[column++] = data.getWord();
                   if (maxColumn>column) throw new RuntimeException("on line "+linenumber+" found "+ column+" columns (first is numbered 1) but synchronising on column "+maxColumn);
                   try {
                     // next tests for first word starting for comment line string but only if this is a nontrivial string
                     if (dontTestForCommentLine || !numbers[0].startsWith(cc))  {
                         boolean lineOK=true;
                           int vc=0;
                           for (Integer vertexColumn: vertexColumnList){
                              vertex=numbers[vertexColumn-1];
                              verticesInput++;
                              if (!vertexNameToIndex.containsKey(vertex)) { lineOK=false;}
                              vertexFound[vc++]=vertex;
                              }
                         if (!lineOK) continue; // the while statement
                         // try to use vertexFoundLL.addAll; verticesOutput+=vertexColumnList.length;
                         for (vc=0; vc<vertexFound.length; vc++){
                             verticesOutput++;
                             vertexFoundLL.add(vertexFound[vc]);
                         } // eo for vc
                     } // eo if (dontTestForCommentLine ...

                     // Now write this line out to output file.
                     // This will happen if its a comment line or if all vertices in specified columns are in the given vertex list
                     int columnMinusOne = column-1; // this will be the index of the last column
                     if (convertToIndex) for (int c=0; c<column; c++)  {
                         PS.print(((vertexColumnList.contains(c))? vertexNameToIndex.get(numbers[c]):numbers[c])+(c==columnMinusOne?"":sep));
                     }
                     else {
                         for (int c=0; c<column; c++)  PS.print(numbers[c]+(c==columnMinusOne?"":sep));
                     }
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
            throw new RuntimeException("**** synchroniseVertexList information file " + infoOutputFileName + " not opened, " + e.getMessage());
        }
            PS.println("Vertex synchronisation on first column of file " + fullInputFileName+"against file "+fullOtherInputFileName+" producing " + fullOutputFileName);
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
