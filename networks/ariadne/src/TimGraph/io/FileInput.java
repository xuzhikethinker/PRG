/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import JavaNotes.TextReader;
import JavaNotes.TextReaderTabSeparated;
import TimGraph.Coordinate;
import TimGraph.EdgeValue;
import TimGraph.VertexLabel;
import cern.colt.list.IntArrayList;
import cern.colt.list.DoubleArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Iterator;

//import TimGraph.io.InputGML;
//import TimGraph.io.FileOutput;
//import TimUtilities.FileUtilities.FileNameSequence;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.io.Reader;
import java.util.Scanner;
import java.util.Set;


/**
 * Routines for constructing a timgraph from an input file.
 *<p>Most use <tt>TextReader</tt> but this has problems with delimters,
 * spaces in words and consecutive white space (tabs, eoln) cause problems.
 * A solution is to use the <tt>java.util.Scanner.readLine()</tt> and to parse 
 * this internally. 
 * @author time
 */
public class FileInput {

     
     private String SEP="\t";
     final static int UNSET = -975573;
     static TextReader data;     // Character input stream for reading noNamedata.
//     int minVertexLabel=9999999;
//     int maxVertexLabel=-minVertexLabel;
       
     static private int infoLevel=0;
      /**
       * Maximum number of items on a line
       */
     static public int nmax=1000;



    /** 
     * Creates a new instance of FileInput.
     *@param infoLevel level set for information produced, -2 none, +2 lots.
     */
    public FileInput(int infoLevel)
    {
        this.infoLevel = infoLevel;
    }


    /** 
     * Creates a new instance of FileInput.
     *@param tginput timgraph to be created
     */
    public FileInput(timgraph tginput)
    {
        //tg = tginput;
        infoLevel = tginput.infoLevel;
    }

 // ***************************************************************************
        /**
         * Opens file for reading.
         * @param fullfilename full name of file including any directory path
         */
    static public TextReader openFile(String fullfilename)
    {
        TextReader newTR;
        System.out.println("Starting to read from " + fullfilename);
        try {  // Create the input stream.
            newTR = new TextReader(new FileReader(fullfilename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Can't find file "+fullfilename+", "+e.getMessage());
            //return null;
        }
        return newTR;
    }
  
 // ***************************************************************************
        /**
         * Opens tab separated file for reading.
         * @param fullfilename full name of file including any directory path
         */
    static public TextReaderTabSeparated openTabSeparatedFile(String fullfilename)
    {
        TextReaderTabSeparated newTR;
        System.out.println("Starting to read from " + fullfilename);
        try {  // Create the input stream.
            newTR = new TextReaderTabSeparated(new FileReader(fullfilename));
        } catch (FileNotFoundException e) {
            System.err.println("*** Can't find file "+fullfilename);
            return null;
        }
        return newTR;
    }
    
    /**
     * Read in list of words, strings separated by white space.
     * <p>Use <tt>(String[]) FileInputreadStringList(fullFileName).toArray()</tt>
     * to get array of strings instead of an ArrayList.
     * @param fullFileName name of file including directories
     * @return list of words found.
     */
    static public ArrayList<String> readStringList(String fullFileName){
        TextReader tr = FileInput.openFile(fullFileName);
        if (tr==null) return null;
        System.out.println("Starting to read list of strings from " + fullFileName);
        ArrayList<String> words = new ArrayList();
        try {
            while (tr.eof() == false) words.add(tr.getWord());
            System.out.println("Finished reading list of strings from file " + fullFileName+" found "+words.size()+" words");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            throw new RuntimeException("*** Input Error: readStringList failed after "+words.size()+" words, " + e.getMessage());
        } finally {
            tr.close();
        }
        return words;
    }

  
               // ***************************************************************************
        /**
         * Reads in table of weights.
         * <p>Each line has entries separated by whitespace.  No headers.
         * Sets up new graph fom this noNamedata.
         * @param tg graph to be set up from its adjacency matrix file
         * @deprecated use processAdjacencyMatrix
         */
    public int processAdjacencyMatrixOld(timgraph tg)
    {
        int res = 0;
        String fullFileName=tg.inputName.getFullFileName();
        Reader inputReader;
        try {
            inputReader = new FileReader(fullFileName);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("*** Can't find file "+fullFileName+", "+ex);
        }
        Scanner inputScanner = new Scanner(inputReader);
        System.out.println("Starting to read "+(tg.isWeighted()?"":"un")+"weighted adjacency matrix file from " + fullFileName);
        double [][] matrix = new double[1][1]; // dummy initial size
        int linenumber=0;
        ArrayList<String> numbers = new ArrayList();
        int dimension=-1;
        int row=0;
        try {
            while (inputScanner.hasNextLine())
                {  // Read until end-of-file.
                   linenumber++;
                   //if (infoLevel>2) System.out.println(linenumber+": ");
                   String lineString=inputScanner.nextLine();
                   int start=0;
                   int end=0;
                   numbers.clear();
                   while (end<lineString.length()){
                       end=lineString.indexOf('\t', start);
                       if (end<0) end=lineString.length();
                        numbers.add(lineString.substring(start, end));
                       start=end+1; 
                   }
                if (infoLevel>1) {
                    System.out.print(linenumber);
                    for (String s : numbers) System.out.print(", "+s);
                    System.out.println();
                }
                    try {
                     if (dimension<0) {
                         dimension=numbers.size();
                         matrix = new double [dimension][dimension];
                     }
                     else {
                         if (numbers.size()!=dimension){
                             throw new RuntimeException(" found "+numbers.size()+" columns, wanted dimension "+dimension);
                         }
                     }
                     if (row==dimension) {
                         System.out.println("!!!Warning, found one row too many, wanted only "+dimension+", ignoring remaining lines");
                         break;
                     }
                     for (int c=0; c<dimension; c++) matrix[row][c]=Double.parseDouble(numbers.get(c));
                     row++;
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                    }
               }//eofile
            System.out.println("Finished adjacency file input from " + fullFileName+" found "+dimension+" rows and columns ");
        }//eo try
        catch (Error e) {
            // Some problem reading the noNamedata from the input file.
            throw new RuntimeException("*** Input Error on line " + linenumber + ", " + e.getMessage());
            //res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            inputScanner.close();
        }
        
        tg.setNetworkFromMatrix(matrix);
        
        return res;
        
    }  // end of readAdjacencyMatrix
    
        /**
         * Reads in table of weights.
         * <p>Each line has entries separated by tabs.  No numberColumns or row headers.
         * Sets up new graph fom this noNamedata.
         * @param tg graph to be set up from its adjacency matrix file
         * @deprecated use processAdjacencyMatrix
         */
    public int processAdjacencyMatrixTabSeparated(timgraph tg)
    {
         boolean headerOn=false;
         boolean rowLabelOn=false;
         boolean trimLabelWhiteSpaceOn=true; // irrelevant
         return processAdjacencyMatrix( tg, '\t', headerOn, rowLabelOn, trimLabelWhiteSpaceOn);
    }
        /**
         * Reads in table of weights separated specified regular expression String..
         * <p>Row and or columns can have text labels, and these
         * may be stripped of white space from their heads and tails of labels.
         * Sets up new graph fom this noNamedata.
         * Can handle empty cells, i.e. two tabs in a row, or a tab then eoln.
         * @param tg graph to be set up from its adjacency matrix file
         * @param findHeader first row is a label for the columns
         * @param rowLabelOn first numberColumns is a label for the row
         * @see #processAdjacencyMatrixTabSeparated
         */
    public int processAdjacencyMatrixTabSeparated(timgraph tg, boolean headerOn, boolean rowLabelOn, boolean trimLabelWhiteSpaceOn)
    {
       return processAdjacencyMatrix( tg, '\t', headerOn, rowLabelOn, trimLabelWhiteSpaceOn);
    }
    
            /**
         * Reads in table of weights separated specified regular expression String..
         * <p>Row and or columns can have text labels, and these
         * may be stripped of white space from their heads and tails of labels.
         * Sets up new graph fom this noNamedata.
         * Can handle empty cells, i.e. two tabs in a row, or a tab then eoln.
         * <p>Note that the <tt>wordSeparator</tt> is typically just a single tab or comma.  However
         * a special character like tab is written as string of two characters, the ascii pair <tt>\t</tt>.  However
         * as backslash is a special character in java strings this is written with two backslashes.  Thus we need
         * <tt>&quot;\\t&\quot;</tt>.  This is used in the <tt>processAdjacencyMatrixTabSeparated</tt>
         * <p>Uses <tt>java.util.Scanner</tt> not <tt>TextReader</tt>
         * @param tg graph to be set up from its adjacency matrix file
         * @param wordSeparator a regular expression used to decide where to split up each line.  
         * @param findHeader first row is a label for the vertices
         * @param rowLabelOn first numberColumns is a label for the rows
         * @see #processAdjacencyMatrixTabSeparated(TimGraph.timgraph, boolean, boolean, boolean)
         */
    public int processAdjacencyMatrix(timgraph tg, char wordSeparator, boolean headerOn, boolean rowLabelOn, boolean trimLabelWhiteSpaceOn)
    {
        int res = 0;
        String fullFileName=tg.inputName.getFullFileName();
        Reader inputReader;
        try {
            inputReader = new FileReader(fullFileName);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("*** Can't find file "+fullFileName+", "+ex);
        }
        Scanner inputScanner = new Scanner(inputReader);
        System.out.println("Starting to read "+(tg.isWeighted()?"":"un")+"weighted adjacency matrix file from " + fullFileName);
        System.out.println("   word separator character is "+((int)wordSeparator)+ ", header labels "+StringFilter.onOffString(headerOn)+", row labels on "+StringFilter.onOffString(rowLabelOn)+", trimming white space from labels "+StringFilter.onOffString(trimLabelWhiteSpaceOn));
        double [][] matrix;
        ArrayList<String> numbers = new ArrayList();
        TreeSet<String> vertexLabelSet  = new TreeSet();
        String [] vertexName;
        //String [] words;
        int firstColumn;
        int dimension=-1;
        int linenumber=0;
        matrix = null; // dummy initial size
        int row=0;
        vertexName = null; // dummy initial size
        if (rowLabelOn) firstColumn=1; else firstColumn=0;
        try {
            while (inputScanner.hasNextLine())
            {  // Read until end-of-file.
                   linenumber++;
                   //if (infoLevel>2) System.out.println(linenumber+": ");
                   String lineString=inputScanner.nextLine();
                   //words = lineString.split(wordSeparator); this fails when tabs are at end of line
                   int start=0;
                   int end=0;
                   numbers.clear();
                   while (end<lineString.length()){
                       end=lineString.indexOf('\t', start);
                       if (end<0) end=lineString.length();
                        numbers.add(lineString.substring(start, end));
                       start=end+1; 
                   }

                if (infoLevel>1) {
                    System.out.print(linenumber);
                    for (int w=0; w<numbers.size(); w++) System.out.print(", "+numbers.get(w));
                    System.out.println();
                }
                try {
                     if (dimension<0) {
                         dimension=numbers.size()-firstColumn;
                         matrix = new double [dimension][dimension];
                         if( headerOn) {
                            vertexName = new String[dimension];
                            if (trimLabelWhiteSpaceOn) for (int c=0; c<dimension; c++) vertexName[c]=numbers.get(c+firstColumn).trim();
                            for (int c=0; c<dimension; c++) {
                                String newLabel=numbers.get(c+firstColumn);
                                if (newLabel.length()<1) {
                                    newLabel="v"+c;
                                    System.err.println("!!!Warning, on column "+c+" column label is null, using \""+newLabel+"\" instead" ); 
                                }
                                vertexName[c]=newLabel;
                            }
                            continue; // if the first row is a header don't continue to process it.
                         }
                     }
                     else {
                         if (numbers.size()!=dimension+firstColumn){
                             throw new RuntimeException(" found "+numbers.size()+" columns, wanted "+(dimension+firstColumn));
                         }
                     }
                     if (row==dimension) {
                         System.err.println("!!!Warning, found one row too many, wanted only "+dimension+", ignoring remaining lines");
                         break;
                     }
                    // process the row labels
                    if (rowLabelOn) {
                        String rowLabel = numbers.get(0);
                        String colLabel = (headerOn ? vertexName[row] : rowLabel);
                        String newLabel = rowLabel;
                        if (!rowLabel.equals(colLabel)) {

                            if (rowLabel.length() > colLabel.length()) {
                                if (rowLabel.startsWith(colLabel)) {
                                    System.err.println("!!!Warning, on row " + row + " column label \"" + colLabel + "\" is a substring of the label \"" + rowLabel + "\", using longer row string");
                                } else {
                                    System.err.println("!!!Warning, on row " + row + " column label \"" + colLabel + "\" is not substring of the label \"" + rowLabel + "\", using longer row string");
                                }
                                newLabel = rowLabel;
                            } else {
                                if (colLabel.startsWith(rowLabel)) {
                                    System.err.println("!!!Warning, on row " + row + " row label \"" + rowLabel + "\" is a substring of the label \"" + colLabel + "\", using longer col string");
                                } else {
                                    System.err.println("!!!Warning, on row " + row + " row label \"" + rowLabel + "\" is not a substring of the label \"" + colLabel + "\", using longer col string");
                                }
                                newLabel = rowLabel;
                            }
                        } // if !rowLabel.equals(colLabel) 

                        if (vertexLabelSet.contains(newLabel)) {
                            System.err.println("!!!Warning, on row " + row + " new label \"" + newLabel + "\" is not unique, already used.  Will use for a ditinct vertex.");
                        }
                        vertexName[row] = newLabel;
                        vertexLabelSet.add(newLabel);
                    } //eo if rowLabelOn


                     for (int c=0; c<dimension; c++) {
                         String s=numbers.get(c+firstColumn);
                         if (s.length()==0) matrix[row][c]=0;
                         else try{matrix[row][c]=Double.parseDouble(s);
                         } catch (RuntimeException e){
                             System.err.println("!!!Warning, entry \""+s+"\" is not a double at line "+linenumber+", column "+(c+firstColumn)+", row "+(row)+", treated as zero.");
                             matrix[row][c]=0;
                         }
                     }
                     row++;
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                    }
               }//eofile
            System.out.println("Finished adjacency file input from " + fullFileName+" found "+dimension+" rows and columns ");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            throw new RuntimeException("*** Input Error: " + e.getMessage());
            //res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            inputScanner.close();
        }
        
        tg.setNetworkFromMatrix(vertexName, matrix);
        
        return res;

    }  // end of readAdjacencyMatrix

        /**
         * Reads in table of weights separated specified regular expression String..
         * <p>Row and or columns can have text labels, and these
         * may be stripped of white space from their heads and tails of labels.
         * Sets up new graph fom this noNamedata.
         * Can handle empty cells, i.e. two tabs in a row, or a tab then eoln.
         * <p>Note that the <tt>wordSeparator</tt> is typically just a single tab or comma.  However
         * a special character like tab is written as string of two characters, the ascii pair <tt>\t</tt>.  However
         * as backslash is a special character in java strings this is written with two backslashes.  Thus we need
         * <tt>&quot;\\t&\quot;</tt>.  This is used in the <tt>processAdjacencyMatrixTabSeparated</tt>
         * <p>Uses <tt>java.util.Scanner</tt> not <tt>TextReader</tt>
         * <p>HELP the strip function does not appear to work for sequences of 
         * tabs at the end of a string but does if they are in the middle
         * despite the appropriate example seen of how java regular expressions work.
         * @param tg graph to be set up from its adjacency matrix file
         * @param wordSeparator a regular expression used to decide where to split up each line.  
         * @param findHeader first row is a label for the vertices
         * @param rowLabelOn first numberColumns is a label for the rows
         * @see #processAdjacencyMatrixTabSeparated(TimGraph.timgraph, boolean, boolean, boolean)
         */
    public int processAdjacencyMatrixOld(timgraph tg, String wordSeparator, boolean headerOn, boolean rowLabelOn, boolean trimLabelWhiteSpaceOn)
    {
        int res = 0;
        String fullFileName=tg.inputName.getFullFileName();
        Reader inputReader;
        try {
            inputReader = new FileReader(fullFileName);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("*** Can't find file "+fullFileName+", "+ex);
        }
        Scanner inputScanner = new Scanner(inputReader);
        System.out.println("Starting to read "+(tg.isWeighted()?"":"un")+"weighted adjacency matrix file from " + fullFileName);
        System.out.println("  "+wordSeparator.length()+" word separator characters are "+StringFilter.toHex(wordSeparator," ")+ ", header labels "+StringFilter.onOffString(headerOn)+", row labels on "+StringFilter.onOffString(rowLabelOn)+", trimming white space from labels "+StringFilter.onOffString(trimLabelWhiteSpaceOn));
        double [][] matrix;
        String [] vertexName;
        String [] words;
        int firstColumn;
        int dimension=-1;
        int linenumber=0;
        matrix = null; // dummy initial size
        int row=0;
        vertexName = null; // dummy initial size
        if (rowLabelOn) firstColumn=1; else firstColumn=0;
        try {
            while (inputScanner.hasNextLine())
            {  // Read until end-of-file.
                   linenumber++;
                   //if (infoLevel>2) System.out.println(linenumber+": ");
                   String lineString=inputScanner.nextLine();
                   words = lineString.split(wordSeparator);

                if (infoLevel>-1) {
                    System.out.print(linenumber);
                    for (int w=0; w<words.length; w++) System.out.print(", "+words[w]);
                    System.out.println();
                }
                try {
                     if (dimension<0) {
                         dimension=words.length-firstColumn;
                         matrix = new double [dimension][dimension];
                         if( headerOn) {
                            vertexName = new String[dimension];
                            if (trimLabelWhiteSpaceOn) for (int c=0; c<dimension; c++) vertexName[c]=words[c+firstColumn].trim();
                            for (int c=0; c<dimension; c++) vertexName[c]=words[c+firstColumn];
                            continue; // if the first row is a header don't continue to process it.
                         }
                     }
                     else {
                         if (words.length!=dimension+firstColumn){
                             throw new RuntimeException(" found "+words.length+" columns, wanted "+(dimension+firstColumn));
                         }
                     }
                     if (row==dimension) {
                         System.err.println("!!!Warning, found one row too many, wanted only "+dimension+", ignoring remaining lines");
                         break;
                     }
                     if (!words[0].equals(vertexName[row]))
                     {
                         System.err.println("!!!Warning, row "+row+" label \""+words[0]+"\" does not match column label \""+vertexName[row]+"\"");                             
                         if (vertexName[row].length()>words[0].length()) {
                            System.err.println("!!!         switching to use the longer row label"); 
                             vertexName[row]=words[0];
                         }
                     }

                     for (int c=0; c<dimension; c++) {
                         String s=words[c+firstColumn];
                         if (s.length()==0) matrix[row][c]=0;
                         else try{matrix[row][c]=Double.parseDouble(s);
                         } catch (RuntimeException e){
                             System.err.println("!!!Warning, entry \""+s+"\" is not a double at line "+linenumber+", column "+(c+firstColumn)+", row "+(row)+", treated as zero.");
                             matrix[row][c]=0;
                         }
                     }
                     row++;
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                    }
               }//eofile
            System.out.println("Finished adjacency file input from " + fullFileName+" found "+dimension+" rows and columns ");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            throw new RuntimeException("*** Input Error: " + e.getMessage());
            //res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            inputScanner.close();
        }
        
        tg.setNetworkFromMatrix(vertexName, matrix);
        
        return res;
        
    }  // end of readAdjacencyMatrix


    
      // ***************************************************************************
        /**
         * Reads in GML format file.
         * <p>Each line has entries separated by whitespace.
         * The source and target vertices must be in columns specified by arguments.
         * The weights must be in the numberColumns specified by <tt>columnWeigh</tt>
         * unless this is negative in which case no weights are read.
         * Note that columns are counted with first numberColumns being word one.
         * Weighted and labelled nature set by input parameters.
         * <br>Sets up new graph fom this noNamedata with directed natrue set by gml parameter.
         * If vertex labelling is on then word=gml id and name= gml label.
         * @param ext extension of filename name of file to read noNamedata
         * @param weighted true if want to read a weighted graph otherwise weights (if present) are ignored.
         * @param vertexLabelled true if want vertex labels
         */
    public int processGMLFile(timgraph tg, String ext, boolean weighted, boolean vertexLabelled )
    {
        int res = 0;
        String fullFileName=tg.inputName.getFullFileName();
        data=openFile(fullFileName);
        if (data==null) return -1;
        InputGML gml = new InputGML(data, weighted);
        System.out.println("Starting to read "+(tg.isWeighted()?"":"un")+"weighted gml network file from " + fullFileName);
        try {
            System.out.println(" File: " + fullFileName);
            gml.findGraph();        
            System.out.println("Finished GML file input from " + fullFileName + " found " + gml.getNumberVertices() + " vertices, " + gml.getNumberEdges() + " edges");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            System.err.println("*** Input Error: " + e.getMessage());
            res = -2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }

        gml.setTimGraph(tg, weighted, vertexLabelled);
        // Set up empty network of correct size.
        // could  set sizes only if some flag says to e.g. if size<=0
        
        FileOutput fo = new FileOutput(tg);
        fo.printVertices("", "\t", null, false, false, false);
 
        
        return 0;

        
    }

    
   // ***************************************************************************
        /**
         * Reads in edges labelled by integers from file.
         * <p>Each line has entries separated by whitespace.
         * The source and target vertices must be in columns specified by arguments.
         * The weights must be in the numberColumns specified by <tt>columnWeigh</tt>
         * unless this is negative in which case no weights are read.
         * The labels of edges must be in the numberColumns specified by <tt>columnLabel</tt>
         * unless this is negative in which case no edge labels are read.
         * Note that columns are counted with first numberColumns being word one.
         * Directed, weighted and labelled nature set by input parameters.
         * Sets up new graph fom this noNamedata. The vertex indexes are exactly
         * as given in the file.  Negative integers must not be in the file.
         * Missing indices are treated as vertices with degree 0.
         * Multiedges allowed.
         * @param tg timgraph to be set up from this noNamedata
         * @param ext extension of filename name of file to read noNamedata
         * @param columnSource numberColumns with source, counting from 1
         * @param columnTarget numberColumns with target, counting from 1
         * @param columnWeight numberColumns with weight, counting from 1.  If negative no weights read.
         * @param columnLabel numberColumns with edge label, counting from 1.  If negative no edge labels read.
         * @param directed true if directed graph being input
         * @param vertexLabelled true if want vertex labels
         * @param infoOn true if want each line printed out as read in
         */
    static public int processIntEdgeFile(timgraph tg,
            String ext,
            int columnSource, int columnTarget, int columnWeight, int columnLabel,
            boolean directed, boolean vertexLabelled, boolean infoOn ){
        return processIntEdgeFile(tg, -987654321,
            ext,
            columnSource, columnTarget, columnWeight, columnLabel,
            directed, vertexLabelled,  true, infoOn );

    }
        /**
         * Reads in edges labelled by integers from file.
         * <p>Each line has entries separated by whitespace.
         * The source and target vertices must be in columns specified by arguments.
         * The weights must be in the numberColumns specified by <tt>columnWeigh</tt>
         * unless this is negative in which case no weights are read.
         * The labels of edges must be in the numberColumns specified by <tt>columnLabel</tt>
         * unless this is negative in which case no edge labels are read.
         * Note that columns are counted with first numberColumns being word one.
         * Directed, weighted and labelled nature set by input parameters.
         * Sets up new graph fom this noNamedata. The vertex indexes are exactly
         * as given in the file.  Negative integers must not be in the file.
         * Missing indices are treated as vertices with degree 0. If numberVertices is
         * bigger than the largest index found then this is used to set the total
         * word of vertices and again the extra vertices are treated as being
         * of degree 0.
         * @param tg timgraph to be set up from this noNamedata
         * @param numberVertices if positive sets word of vertices to be this word
         * @param ext extension of filename name of file to read noNamedata
         * @param columnSource numberColumns with source, counting from 1
         * @param columnTarget numberColumns with target, counting from 1
         * @param columnWeight numberColumns with weight, counting from 1.  If negative no weights read.
         * @param columnLabel numberColumns with edge label, counting from 1.  If negative no edge labels read.
         * @param directed true if directed graph being input
         * @param vertexLabelled true if want vertex labels
         * @param multiEdge true if want to allow multiEdges
         * @param infoOn true if want each line printed out as read in
         */
    static public int processIntEdgeFile(timgraph tg, int numberVertices,
            String ext,
            int columnSource, int columnTarget, int columnWeight, int columnLabel,
            boolean directed, boolean vertexLabelled, boolean multiEdges, boolean infoOn )
    {
        boolean weightsOn = true;
        if (columnWeight<0) weightsOn = false;
        boolean labelsOn = true;
        if (columnLabel<0) labelsOn = false;
        tg.setWeightedEdges(weightsOn | labelsOn);

        tg.setVertexlabels(vertexLabelled);
        tg.setDirectedGraph(directed);

        tg.setMultiEdge(multiEdges);
        if (multiEdges) {
            tg.setVertexEdgeList(true);
        }


        ArrayList<Integer> edgeLL = new ArrayList();
        DoubleArrayList weightLL = new DoubleArrayList();
        IntArrayList labelLL = new IntArrayList();
        // Make a sorted list of the integer labels - will work if we treat these as string labels
        // but then integers won't be sorted in order as "10" not "2" comes just after "1".
        //TreeSet<String> sourceLL = new TreeSet();
        TreeSet<Integer> sourceLL = new TreeSet();
        int res = readIntEdgeFile(tg, ext, columnSource, columnTarget, columnWeight, columnLabel, sourceLL, edgeLL, weightLL, labelLL, infoOn) ;
        if (res<=0) throw new RuntimeException("FileInput.processIntEdgeFile returned maximum vertex index as "+res);
        int nv=Math.max(res+1,numberVertices);
        System.out.println("Largest vertex index was "+res+" creating graph with "+nv+" vertices");
        // Set up empty network of correct size.
        // could  set sizes only if some flag says to e.g. if size<=0
        tg.setMaximumVertices(nv);
        tg.setMaximumStubs(edgeLL.size());
        tg.setNetwork();

        // note this sets disconnected vertices for those whihc don't apper in edge list
        for (int v=0; v<nv; v++){
            tg.addVertex();
        } // eo while

        // Now add the edges
        int source=-1;
        int target=-1;
        double weight=-1;
        int label=-1;
        for (int e=0; e < edgeLL.size(); e++)
        {
//            source = vertexLabelToIndex.get(edgeLL.get(e++));
//            target = vertexLabelToIndex.get(edgeLL.get(e));
            source = edgeLL.get(e++);
            target = edgeLL.get(e);
            if (tg.isWeighted()) {
                if (weightsOn) weight=weightLL.get(e/2); else weight =1.0;
                if (labelsOn) label=labelLL.get(e/2); else label =0;
                if (multiEdges) tg.increaseEdgeWeight(source,target,weight);
                else tg.addEdge(source,target,new EdgeValue(label, weight));
            }
            else if (multiEdges) tg.increaseEdgeWeight(source,target,1.0);
                 else tg.addEdge(source,target);
        }// eo for e

        return nv;
    }

// ***************************************************************************
        /**
         * Reads in edge noNamedata labelled by integers from file filename.
         * <p>Each line has entries separated by whitespace.
         * Edges are given by a line with a source and target vertex integer, together with optional double weight.
         * Vertices are deduced from this so no disconnected vertices are in this graph.
         * <br>Simple to change so that vertices are given as string labels but sorting numerical strings does not give same order as
         * equivalent integers.
         * @param ext extension of filename name of file to read noNamedata
         * @param columnSource numberColumns with source, first numberColumns is word 1
         * @param columnTarget numberColumns with target, counting from 1
         * @param columnWeight numberColumns with weight, counting from 1.  If negative no weights read
         * @param columnLabel numberColumns with edge label, counting from 1.  If negative no edge labels read.
         * @param edgeLL list of source then target of each edge
         * @param weightLL list of weights so <tt>weightLL.get(e)</tt> associated with the edge with source <tt>edgeLL.get(2e)</tt>
         * @param labelLL list of edge labels so <tt>labelLL.get(e)</tt> associated with the edge with source <tt>edgeLL.get(2e)</tt>
         * @param infoOn true (false) if want each line printed out as read in
         */
    static public int readIntEdgeFile(timgraph tg, String ext,
            int columnSource, int columnTarget, int columnWeight, int columnLabel, 
            TreeSet<Integer> sourceLL, ArrayList<Integer> edgeLL, DoubleArrayList weightLL, IntArrayList labelLL,
            boolean infoOn)
    {
        boolean weightsOn = true;
        if (columnWeight<0) weightsOn = false; 
        boolean labelsOn = true;
        if (columnLabel<0) labelsOn = false; 
        //TreeMap<Integer,Integer> vertexLabelToIndex = new TreeMap();
//        int minVertexLabel=9999999;
        int maxVertexIndex=-9999999;

//        tg.maximumCoordinate = new Coordinate();
//        tg.minimumCoordinate = new Coordinate();
        int res=0;  // error code.
        tg.inputName.setNameEnd(ext);
        String fullFileName=tg.inputName.getFullFileName();
        TextReader data=openFile(fullFileName);
        if (data==null) return -1;
        System.out.println("Starting to read "+(tg.isDirected()?"":"un")+"directed "+(tg.isWeighted()?"":"un")+"weighted network file using an edge list from " + fullFileName);

        try {
            System.out.println(" File: "+fullFileName);
            // Read the noNamedata from the input file.
            //String line;
            //int first;
            //int n;
            int linenumber=0;
            //int edgenumber=0;
            //int nmax=10; // max no. columns
            String [] numbers = new String [nmax];
//            String source ="NOTSET";
//            String target ="NOTSET";
            Integer source = new Integer(UNSET);
            Integer target = new Integer(UNSET);
            double weight=-97531;
            int label=-86420;
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   //if (infoLevel>2) System.out.println(linenumber+": ");
                   int column=0;
                    while (data.eoln() == false)
                    {  // Read until end-of-line.
                       numbers[column++] = data.getWord() ;
                    } //eoln
                if (infoOn) {
                    System.out.print(linenumber);
                    for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                    System.out.println();
                }
                    try {
                     if (columnSource > column) {
                         res = -10;
                         //edgeLL.add("T.F.C. line " + linenumber);
                         edgeLL.add(UNSET);
                         throw new RuntimeException("*** Too few columns on line " + linenumber + " expected edge source in " + columnSource + " found " + column);
                     } else {
                         source = Integer.parseInt(numbers[columnSource - 1]);
                         edgeLL.add(source);
                         sourceLL.add(source);
                         maxVertexIndex=Math.max(maxVertexIndex,source);
                     }
                     
                     if (columnTarget > column) {
                         //edgeLL.add("T.F.C. line " + linenumber);
                         edgeLL.add(UNSET);
                         res = -11;
                         throw new RuntimeException("*** Too few columns on line " + linenumber + " expected edge target in " + columnTarget + " found " + column);
                     }
                     else {
                         target = Integer.parseInt(numbers[columnTarget - 1]);
                         edgeLL.add(target);
                         sourceLL.add(target);
                         maxVertexIndex=Math.max(maxVertexIndex,target);
                     }
                         
     
                     if (weightsOn){
                     if (columnWeight <= column) {
                                weight = Double.parseDouble(numbers[columnWeight - 1]);
                                weightLL.add(weight);
                            } else {
                                res=-12;
                                throw new RuntimeException("*** Too few columns on line " + linenumber + " expected weight in" + columnWeight + " found " + column);
                            }
                     }
                      
                     if (labelsOn){
                     if (columnLabel <= column) {
                                label = Integer.parseInt(numbers[columnLabel - 1]);
                                labelLL.add(label);
                            } else {
                                res=-13;
                                throw new RuntimeException("*** Too few columns on line " + linenumber + " expected label in " + columnLabel + " found " + column);
                                
                            }
                     }
                            
                        
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                        //return -100;
                    }

               }//eofile

            if (res==0) res =sourceLL.size();
            System.out.println("Finished edge list file input from " + fullFileName+" found "+sourceLL.size()+" vertices, "+edgeLL.size()+" stubs");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            res=-2;
            throw new RuntimeException("*** Input Error: " + e.getMessage());
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }
        return maxVertexIndex;
    }  // end of FileInput
    
    
   // ***************************************************************************
        /**
         * Reads in edges labelled by strings from file.
         * <p>Each line has entries separated by whitespace.
         * The source and target vertices must be in columns specified by arguments.
         * The weights must be in the numberColumns specified by <tt>columnWeight</tt>
         * unless this is negative in which case no weights are read.
         * Note that columns are counted with first numberColumns being word one.
         * Directed, weighted and labelled nature set by input parameters.
         * Sets up new graph from this noNamedata.
         * <p>If the input graph does not have multiedges then there is a check
         * to see if the edge has already been set.  Only the first edge is processed
         * and a finite word of warnings are given.
         * Will expand the maximum number of vertices and stubs allowed
         * to accommodate file but if sufficient will leave these unchanged.
         * <P>TODO DOES NOT APPEAR TO DO BIPARTITE GRAPHS
         * @param ext extension of filename name of file to read noNamedata
         * @param columnSource numberColumns with source, counting from 1
         * @param columnTarget numberColumns with target, counting from 1
         * @param columnWeight numberColumns with weight, counting from 1.  If negative no weights read.
         * @param columnLabel numberColumns with edge label, counting from 1.  If negative no edge labels read.
         * @param directed true if directed graph being input
         * @param vertexLabelled true if want vertex labels
         * @return negative indicates failure, code from readStringEdgeFile else if less than -20 its a repeated edge warning.
         */
    public int processStringEdgeFile(timgraph tg, String ext, 
            int columnSource, int columnTarget, int columnWeight,  int columnLabel, 
            boolean directed, boolean vertexLabelled )
    {
        boolean weightsOn = true;
        if (columnWeight<0) weightsOn = false; 
        boolean labelsOn = true;
        if (columnLabel<0) labelsOn = false; 
        tg.setWeightedEdges(weightsOn | labelsOn);

        tg.setVertexlabels(vertexLabelled);
        tg.setDirectedGraph(directed);

        ArrayList<String> edgeLL = new ArrayList();
        DoubleArrayList weightLL = new DoubleArrayList();
        IntArrayList labelLL = new IntArrayList();
        // Make a sorted list of the integer labels
        TreeSet<String> sourceLL = new TreeSet();
        tg.inputName.setNameEnd(ext);
        String fullFileName = tg.inputName.getFullFileName();

        int res = readStringEdgeFile(fullFileName, 
                columnSource, columnTarget, columnWeight, columnLabel,
                sourceLL, null, edgeLL, weightLL, labelLL) ;
        if (res<0) return res;

        // Set up empty network of correct size.
        // could  set sizes only if some flag says to e.g. if size<=0
        tg.setMaximumVertices(Math.max(tg.getMaximumVertices(),sourceLL.size()));
        //int [] vertexLabelToIndex = new int[tg.setMaximumVertices];
        tg.setMaximumStubs(Math.max(tg.getMaximumStubs(),edgeLL.size()));
        tg.setNetwork();

        // Associate each vertex label to an integer index of a vertex in a timgraph
        TreeMap<String,Integer> vertexLabelToIndex = new TreeMap();
        Iterator<String> iter = sourceLL.iterator();
        int v=0;
        String current="NOTUSED";
        VertexLabel newLabel = new VertexLabel();
        while (iter.hasNext()) {
            current=iter.next();
            vertexLabelToIndex.put(current, v++ );
            if (tg.isVertexLabelled()){
                newLabel.setName( current);
                tg.addVertex(newLabel);
                }
            else tg.addVertex();
        } // eo while


        // Now add the edges
        int source=-1;
        int target=-1;
        double weight=-1;
        int label =-1;
        int maxWarning=20;
        res=0;
        for (int e=0; e < edgeLL.size(); e++)
        {
            source = vertexLabelToIndex.get(edgeLL.get(e++));
            target = vertexLabelToIndex.get(edgeLL.get(e));
            if ((source == target) && tg.isSelfLooped()) continue;
            if (tg.isWeighted()) {
                if (weightsOn) weight=weightLL.get(e/2); else weight =1.0;
                if (labelsOn) label=labelLL.get(e/2); else label = EdgeValue.NOLABEL;
                if (tg.isMultiEdge() || !tg.edgeExists(source,target) ) tg.addEdge(source,target,new EdgeValue(label, weight));
                else {
                  if (res>-maxWarning && (infoLevel>-2)) System.out.println("!!! edge "+(e/2)+" is second edge from "+source+"("+edgeLL.get(e-1)+") to "+target+"("+edgeLL.get(e)+") -> ignored");
                  if (res==-maxWarning) System.out.println("!!! Reached maximum number of warnings, "+(-res)+", no more given.");
                  res=res-1;
                }
            }
            else if (tg.isMultiEdge() || !tg.edgeExists(source,target) ) tg.addEdge(source,target);
                else {
                  if (res>-maxWarning && (infoLevel>-2)) System.out.println("!!! edge "+(e/2)+" is second edge from "+source+"("+edgeLL.get(e-1)+") to "+target+"("+edgeLL.get(e)+") -> ignored");
                  if (res==-maxWarning) System.out.println("!!! Reached maximum number of warnings, "+(-res)+", no more given.");
                  res=res-1;
                }

        }// eo for e

        return res-maxWarning;
    }


        /**
         * Reads in edge noNamedata labelled by strings from file filename.
         * <p>This version assumes no header on the file.
         * @see #readStringEdgeFile(java.lang.String, int, int, int, int, java.util.TreeSet, java.util.TreeSet, java.util.ArrayList, cern.colt.list.DoubleArrayList, cern.colt.list.IntArrayList, boolean)
         */
    static public int readStringEdgeFile(String fullFileName,
            int columnSource, int columnTarget, int columnWeight, int columnLabel,
            TreeSet<String> sourceLL, TreeSet<String> targetLL, ArrayList<String> edgeLL, DoubleArrayList weightLL, IntArrayList labelLL)
    {
        boolean headerOn=false;
        return readStringEdgeFile(fullFileName,
            columnSource, columnTarget, columnWeight, columnLabel,
            sourceLL, targetLL, edgeLL, weightLL, labelLL,
            headerOn);
    }
        /**
         * Reads in edge noNamedata labelled by strings from file filename.
         * <p>Each line has entries separated by whitespace.
         * Edges are given by a line with a source and target vertex integer, together with optional double weight.
         * Vertices are deduced from this so no disconnected vertices are in this graph.
         * The set of all known source and target vertices is given in
         * <tt>sourceLL</tt> and <tt>targetLL</tt> respectively.
         * If the latter is null then all vertices are in the source list.
         * @param fullFileName full file name file with noNamedata
         * @param columnSource numberColumns with source, first numberColumns is word 1
         * @param columnTarget numberColumns with target, counting from 1
         * @param columnWeight numberColumns with weight, counting from 1.  If negative no weights read
         * @param columnLabel numberColumns with edge label, counting from 1.  If negative no edge labels read.
         * @param sourceLL set of all distinct source vertex labels. If <tt>targetLL</tt> is null then all vertices are in this list.
         * @param targetLL set of all distinct target vertex labels. If null then all vertices are in the source list.
         * @param edgeLL list of source then target of each edge
         * @param weightLL list of weights so <tt>weightLL.get(e)</tt> associated with the edge with source <tt>edgeLL.get(2e)</tt>
         * @param labelLL list of edge labels so <tt>labelLL.get(e)</tt> associated with the edge with source <tt>edgeLL.get(2e)</tt>
         * @param findHeader if true skip first line (its a header)
         */
    static public int readStringEdgeFile(String fullFileName,
            int columnSource, int columnTarget, int columnWeight, int columnLabel,
            TreeSet<String> sourceLL, TreeSet<String> targetLL, ArrayList<String> edgeLL,
            DoubleArrayList weightLL, IntArrayList labelLL,
            boolean headerOn)
    {
        boolean weightsOn = true;
        if (columnWeight<0) weightsOn = false; 
        boolean labelsOn = true;
        if (columnLabel<0) labelsOn = false; 
        boolean targetSetOn=true;
        if (targetLL==null) targetSetOn=false;
        int minVertexLabel=9999999;
        int maxVertexLabel=-minVertexLabel;

//        tg.maximumCoordinate = new Coordinate();
//        tg.minimumCoordinate = new Coordinate();
        int res=0;  // error code.

        data=openFile(fullFileName);
        if (data==null) return -1;
        System.out.println("Starting to read edge list with string vertex labels from " + fullFileName);

        try {
            System.out.println(" File: "+fullFileName);
            int linenumber=0;
            String [] numbers = new String [nmax];
            String source ="NOTSET";
            String target ="NOTSET";
            double weight=-97531;
            int label=-86420;
            if (headerOn) {data.getln();linenumber++;}
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoLevel>2) System.out.println(linenumber+": ");
                   int column=0;
                    while (data.eoln() == false)
                    {  // Read until end-of-line.
                       numbers[column++] = data.getWord() ;
                    } //eoln
                if (infoLevel>1) {
                    System.out.print(linenumber);
                    for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                    System.out.println();
                }
                    try {
                     if (columnSource > column) {
                         res = -10;
                         edgeLL.add("T.F.C. line " + linenumber);
                         throw new RuntimeException("*** Too few columns on line " + linenumber + " expected " + columnSource + " found " + column);
                     } else {
                         source = numbers[columnSource - 1];
                         edgeLL.add(source);
                         sourceLL.add(source);
                     }

                     if (columnTarget > column) {
                         edgeLL.add("T.F.C. line " + linenumber);
                         res = -11;
                         throw new RuntimeException("*** Too few columns on line " + linenumber + " expected " + columnTarget + " found " + column);
                     }
                     else {
                         target = numbers[columnTarget - 1];
                         edgeLL.add(target);
                         if (targetSetOn) targetLL.add(target); 
                         else sourceLL.add(target);
                     }


                     if (weightsOn){
                     if (columnWeight <= column) {
                                weight = Double.parseDouble(numbers[columnWeight - 1]);
                                weightLL.add(weight);
                            } else {
                                res=-12;
                                throw new RuntimeException("*** Too few columns on line " + linenumber + " expected weight in" + columnWeight + " found " + column);
                            }
                     }
                      
                     if (labelsOn){
                     if (columnLabel <= column) {
                                label = Integer.parseInt(numbers[columnLabel - 1]);
                                labelLL.add(label);
                            } else {
                                res=-13;
                                throw new RuntimeException("*** Too few columns on line " + linenumber + " expected label in " + columnLabel + " found " + column);
                                
                            }
                     }

                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                    }

               }//eofile

            if (res==0) res =sourceLL.size();
            System.out.println("Finished edge list file input from " + fullFileName+" found "+sourceLL.size()+" vertices, "+edgeLL.size()+" stubs");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            res=-2;
            throw new RuntimeException("*** Input Error: " + e.getMessage());

        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }


        return res;
    }
    
        /**
         * Reads in one numberColumns from file.
         * <p>Each line has entries separated by whitespace.
         * Any lines starting with the comment string (after any white space) are skipped.
         * @param fullFileName full name of file including directories
         * @param cc comment character, if first no white space characters are this string, then line is ignored.
         * @param columnRead numberColumns to be read in
         * @return list of entries in the order in which they were found.
         */
    public ArrayList<String> readStringColumnFromFile(String fullFileName,
            String cc, int columnRead, boolean forceLowerCase)
    {
        int res=0;  // error code.
        data=openFile(fullFileName);
        if (data==null) throw new RuntimeException("Failed to open " + fullFileName);
        System.out.println("Starting to read edge list with string vertex labels from " + fullFileName);
        ArrayList<String> columnList = new ArrayList();
        boolean testForComments=true;
        if (cc.length()<1) testForComments=false;
        try {
            // Read the noNamedata from the input file.
            int linenumber=0;
            String source;
            String [] numbers = new String [nmax];
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoLevel>2) System.out.println(linenumber+": ");
                   int column=0;
                   while (data.eoln() == false) numbers[column++] = data.getWord();
                if (infoLevel>1) {
                    System.out.print(linenumber);
                    for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                    System.out.println();
                }
                    try {
                     if (testForComments && (numbers[0].startsWith(cc))) continue; // skip comment lines
                     if (columnRead > column) {
                         res = -10;
                         columnList.add("T.F.C. line " + linenumber);
                         throw new RuntimeException("*** Too few columns on line " + linenumber + " expected " + columnList + " found " + column);
                     } else {
                         source = numbers[columnRead - 1];
                         if (forceLowerCase) columnList.add(source.toLowerCase());
                         else columnList.add(source);
                     }
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                    }

               }//eofile

            if (res==0) res =columnList.size();
            System.out.println("Finished reading column +"+columnRead+" from input file " + fullFileName+" found "+linenumber+" lines but took only "+columnList.size()+" entries");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            res=-2;
            throw new RuntimeException("*** Input Error: " + e.getMessage());

        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }
        return columnList;
    }



    /**
     * Finds row of numberColumns labels.
     * Looks for a row containing given labels.  Must match exactly.
     * The list returned is a list of the numberColumns columnEntry (first numberColumns is numberColumns 1)
     * for each of the labels in the list given, in the same order.  
     * @param columnEntry list of strings, each entry representing one column in a row
     * @param labelList list of strings with labels
     * @return an array of column numbers for each label in given list
     */
    public static int [] processHeaderRow(ArrayList<String> columnEntry, String [] labelList){
        int [] column=new int[labelList.length];
        for(int l=0; l<labelList.length; l++){
                column[l] = findColumn(columnEntry, labelList[l], false);
        }
        return column;
    }

    /**
     * Finds which numberColumns has specified label.
     * Operates on header row. Case is ignored.
     * @param cellRowArray list of strings for row of numberColumns labels
     * @return numberColumns (numbered from 1) with label, negative if non found.
     */
    public static int findColumn(ArrayList<String> cellRowArray, String label, boolean infoOn){
                int labelColumn=-1;
                for (int j = 0; j < cellRowArray.size(); j++) {
                                String stringCellValueLC = cellRowArray.get(j).toLowerCase();
                                if (stringCellValueLC.startsWith(label.toLowerCase())) labelColumn=j+1;
                        }
                if (labelColumn<0) {
                    if (infoOn) System.err.println("*** No column starts with "+label);
                }
                else if (infoOn) System.out.println("column "+labelColumn+ " is labelled with "+label);
                return labelColumn;
    }


        /**
         * Reads in several columns from file as strings.
         * <p>Each line has entries separated by whitespace.
         * Any lines starting with the comment string (after any white space) are skipped.
         * The first line after any comments is treated as a header row if
         * readColumnList is null.
         * Results are in the order specified by array specifying the columns
         * (columnReadList or  columnLabelList).  So if this returns r then
         * r.get(c).get(l) will give a string corresponding to the l-th entry
         * of type columnReadList[l] or  columnLabelList[l].
         * This version reads columns specified by numberColumns number
         * (first is numberColumns 1) or by label. If numberColumns numbers given are 0 or less
         * then exception thrown.  If label given
         * not found in header row, then exception thrown. The entry in the file
         * must start with exactly the given string so it can be longer than
         * given label.
         * @param fullFileName full name of file including directories
         * @param cc comment character, if first no white space characters are this string, then line is ignored.
         * @param columnReadList array of integers giving the columns to be read in, null if first non-comment line is to be header row
         * @param columnLabelList array of strings giving headers to search for.
         * @param forceLowerCase forces all input into lower case
         * @param infoOn true if want the input lines printed out as read in
         * @return list of entries in the order in which they were found.
         */
    static public ArrayList<ArrayList<String>> readStringColumnsFromFile(String fullFileName,
            String cc, int [] columnReadList, String [] columnLabelList,
            boolean forceLowerCase, boolean infoOn)
    {
        int res=0;  // error code.
        TextReader datafile=openFile(fullFileName);
        if (datafile==null) throw new RuntimeException("Failed to open " + fullFileName);

        ArrayList<ArrayList<String>> columnList = new ArrayList(); //columnReadList.length);

        // see if need to find header row
        int maxColumn=-1;
        boolean findHeader=false;
        String foundColumns=""; // string with found columns
        String missingColumns=""; // string with missing columns
        int activeEntryInColumnList=-1;
        if (columnReadList==null) {
            findHeader=true;
            System.out.println("Header row required, starting to read columns of file " + fullFileName);
        }
        else{
            System.out.print("No header row, starting to read columns ");
            int column2=-1;
            for (int i=0;i<columnReadList.length;i++){
                column2=columnReadList[i];
                if (column2<1) {
                    System.out.println("!!! Warning First column is numbered 1, specified one column as number "+column2);
                    columnList.add(null);
                    continue;
                }
                maxColumn=Math.max(column2,maxColumn);
                System.out.print(column2+", ");
                columnList.add(new ArrayList());
                activeEntryInColumnList=i;
            }
            System.out.println(" of file " + fullFileName);
        }

        boolean testForComments=true;
        if (cc.length()<1) testForComments=false;
        try {
            // Read the noNamedata from the input file.
            int linenumber=0; // First line will be line 1
            String source;
            ArrayList<String> columnEntry = new ArrayList(); //String [nmax];
            while (datafile.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   //if (infoOn) System.out.println(linenumber+": ");
                   // read each numberColumns into array of strings
                   int numberColumns=0;
                   columnEntry.clear();
                   while (datafile.eoln() == false) columnEntry.add(datafile.getWord());
                   numberColumns=columnEntry.size();
                   if (infoOn) {
                        System.out.print(linenumber);
                        for (int c=0; c<numberColumns; c++) System.out.print((c==0?": ":", ")+columnEntry.get(c));
                        System.out.println();
                   }
                  //if(findHeader && (linenumber==1)) continue;

                  // try processing one line
                   try{
                     if (testForComments && (columnEntry.get(0).startsWith(cc))) continue; // skip comment lines

                     if(findHeader){
                         // first one comment line must be header
                         columnReadList = processHeaderRow(columnEntry, columnLabelList);
                         findHeader=false;
                         // now indicate rows and find max numberColumns

                         int column2=-1;
                         foundColumns=""; // string with found columns
                         missingColumns=""; // string with missing columns
                         for (int i=0;i<columnReadList.length;i++){
                            column2=columnReadList[i];
                            if (column2<1) {
                                missingColumns=missingColumns+" "+columnLabelList[i];
                                columnList.add(null);
                                continue;
                            }
                            maxColumn=Math.max(column2,maxColumn);
                            foundColumns=foundColumns+column2+" ("+columnLabelList[i]+"), ";
                            columnList.add(new ArrayList());
                            activeEntryInColumnList=i;
                         }// eo for i
                         System.out.println("Header row on line "+linenumber
                                 +(foundColumns.length()>0?", reading columns:-"+foundColumns:""));
                         if (missingColumns.length()>0)
                             System.out.println("!!! Warning following columns not found and ignored:-"+missingColumns);
                         continue;
                       }

                     if (maxColumn > numberColumns) {
                         res = -10;
                         //columnList.add("TooFewColumnsOnLine" +linenumber);
                         System.out.println("!!! Warning too few columns on line " + linenumber + " expected " + columnList + " found " + numberColumns);
                     }
                     int column2=-1;
                     for (int i=0;i<columnReadList.length;i++){
                       column2=columnReadList[i];
                       if (column2<1) continue;
                       if (column2>columnEntry.size()){
                           source="";
                       }
                       else {
                           source = columnEntry.get(columnReadList[i] - 1);
                       }
                       if (forceLowerCase) columnList.get(i).add(source.toLowerCase());
                       else columnList.get(i).add(source);
                     }// eo for i
                    }// eo try processing one line
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                    }
                    } // eo while, finished reading all lines

            if (res==0) res =columnList.size();
            System.out.print("Finished reading columns "+foundColumns);
            //for (int i=0;i<columnReadList.length;i++) System.out.print(columnReadList[i]+", ");
            System.out.println(" of file " + fullFileName);
            System.out.println(" Found "+linenumber+" lines, took "
                    +(maxColumn<0?"no columns":columnList.get(activeEntryInColumnList).size()+" entries"));
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            res=-2;
            throw new RuntimeException("*** Input Error: " + e.getMessage());

        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            datafile.close();
        }

        return columnList;
    }

        /**
         * Reads in several columns from file.
         * <p>Each line has entries separated by whitespace.
         * Any lines starting with the comment string (after any white space) are skipped.
         * Note that the set of columns MUST have a definite order so that
         * an iterator will reproduce that same order.  Results are stored line
         * by line, and then in this order specified by the set.
         * Assumes first line contains noNamedata and is not a header.
         * @param fullFileName full name of file including directories
         * @param cc comment character, if first no white space characters are this string, then line is ignored.
         * @param vertexColumnList set of integers giving the columns to be read in
         * @return list of entries in the order in which they were found.
         */
    public ArrayList<String> readStringColumnsFromFile(String fullFileName,
            String cc, Set<Integer> vertexColumnList, boolean forceLowerCase)
    {
        int [] columnList = new int[vertexColumnList.size()];
        int i=0;
        for (Integer vertexColumn: vertexColumnList) columnList[i++]=vertexColumn;
       return readStringColumnsFromFile(fullFileName, cc, columnList, forceLowerCase, false, (infoLevel>1)?true:false);
    }

    /**
         * Reads in several columns from file as strings.
         * <p>Each line has entries separated by whitespace.
         * Any lines starting with the comment string (after any white space) are skipped.
         * Results are stored line by line, and then in the order specified by
         * array of columns.
         * @param fullFileName full name of file including directories
         * @param cc comment character, if first no white space characters are this string, then line is ignored.
         * @param columnReadList array of integers giving the columns to be read in
         * @param findHeader ignore first line if true
         * @param infoOn true if want the input lines printed out as read in
         * @return list of entries in the order in which they were found.
         */
    static public ArrayList<String> readStringColumnsFromFile(String fullFileName,
            String cc, int [] columnReadList, boolean forceLowerCase, boolean headerOn, boolean infoOn)
    {
        int res=0;  // error code.
        TextReader datafile=openFile(fullFileName);
        if (datafile==null) throw new RuntimeException("Failed to open " + fullFileName);
        System.out.print("Starting to read columns ");
        ArrayList<String> columnList = new ArrayList();

        int column2=-1;
        int maxColumn=-1;
        for (int i=0;i<columnReadList.length;i++){
            column2=columnReadList[i];
            if (column2<1) throw new RuntimeException("\nFirst column is numbered 1, specified one column as number "+column2);
            maxColumn=Math.max(column2,maxColumn);
            System.out.print(column2+", ");
        }
        System.out.println(" of file " + fullFileName);

        boolean testForComments=true;
        if (cc.length()<1) testForComments=false;
        try {
            // Read the noNamedata from the input file.
            int linenumber=0; // First line will be line 1
            String source;
            String [] numbers = new String [nmax];
            while (datafile.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoOn) System.out.println(linenumber+": ");
                   int column=0;
                   while (datafile.eoln() == false) numbers[column++] = datafile.getWord();
                   if (infoOn) {
                        System.out.print(linenumber);
                        for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                        System.out.println();
                   }
                   if(headerOn && (linenumber==1)) continue;

                   try {
                     if (testForComments && (numbers[0].startsWith(cc))) continue; // skip comment lines
                     if (maxColumn > column) {
                         res = -10;
                         columnList.add("TooFewColumnsOnLine" +linenumber);
                         throw new RuntimeException("*** Too few columns on line " + linenumber + " expected " + columnList + " found " + column);
                     } else {
                         for (int i=0;i<columnReadList.length;i++){
                           source = numbers[columnReadList[i] - 1];
                           if (forceLowerCase) columnList.add(source.toLowerCase());
                           else columnList.add(source);
                         }
                     }
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                    }

               }//eofile

            if (res==0) res =columnList.size();
            System.out.print("Finished reading columns ");
            for (int i=0;i<columnReadList.length;i++) System.out.print(columnReadList[i]+", ");
            System.out.println(" of file " + fullFileName);
            System.out.println(" Found "+linenumber+" lines but took only "+columnList.size()+" entries");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            res=-2;
            throw new RuntimeException("*** Input Error: " + e.getMessage());

        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            datafile.close();
        }


        return columnList;
    }



    // ***************************************************************************
        /**
         * Reads in list of labels each for a given index.
         * <p>Both label and index are treated as strings and stored in ArrayLists 
         * in the same order as found in the the file.
         * <p>Each line has entries separated by whitespace.
         * Can be used to read in a partition label for a list of edges or vertices given by some index.
         * @param fullfilename full filename name of file (includes extension)
         * @param columnIndex numberColumns with index, counting from 1
         * @param columnLabel numberColumns with label, counting from 1.
         * @param indexL list of indices
         * @param labelL list of labels.
         * @param findHeader First line ignored if true.
         */
    static public int readStringIndexLabelList(String fullfilename, int columnIndex, int columnLabel,
            ArrayList<String> indexL, ArrayList<String> labelL, boolean headerOn)
    {

        int res = 0;
        data=openFile(fullfilename);
        if (data==null) return -1;
        System.out.println("Starting to read index and label list as strings from " + fullfilename);

        try {
            System.out.println(" File: "+fullfilename);
            // Read the noNamedata from the input file.
            //String line;
            //int first;
            //int n;
            int linenumber=0;
            //int edgenumber=0;
            //int nmax=10; // max no. columns
            String [] numbers = new String [nmax];
            String index ="NOTSET";
            String label ="NOTSET";
            if (headerOn){
                System.out.println("Header Line = "+data.getln());
                linenumber++;
            }
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoLevel>1) System.out.println(linenumber+": ");
                   int column=0;
                    while (data.eoln() == false)
                    {  // Read until end-of-line.
                       numbers[column++] = data.getWord() ;
                    } //eoln
                if (infoLevel>1) {
                    System.out.print(linenumber);
                    for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                    System.out.println();
                }
                    try {
                     if (columnIndex > column) {
                         System.out.println("*** Too few columns on line " + linenumber + " expected " + columnIndex + " found " + column);
                         res = -10;
                         indexL.add("T.F.C. line " + linenumber);
                     } else {
                         index = numbers[columnIndex - 1];
                         indexL.add(index);
                     }
                     
                     if (columnLabel > column) {
                         System.out.println("*** Too few columns on line " + linenumber + " expected " + columnLabel + " found " + column);
                         labelL.add("T.F.C. line " + linenumber);
                         res = -11;
                     }
                     else {
                         label = numbers[columnLabel - 1];
                         labelL.add(label);
                     }
                         
     
                    
                    }// eo try
                    catch (RuntimeException e) {
                        System.err.println("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                        return -100;
                    }

               }//eofile

            if (res==0) res =indexL.size();
            System.out.println("Finished index/label list file input from " + fullfilename+" found "+indexL.size()+" indices, "+labelL.size()+" labels");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            System.err.println("*** Input Error: " + e.getMessage());
            res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }


        if (indexL.size()!=labelL.size()) System.err.println("*** Two lists have different sizes");
      
        return res;
    }  // end of readIndexLabelList
        /**
         * Reads in list of labels each for a given index.
         * <p>Both label and index are treated as strings and stored in ArrayLists
         * in the same order as found in the the file.
         * <p>Each line has entries separated by whitespace.
         * Can be used to read in a partition label for a list of edges
         * or vertices given by some index.
         * <p>Uses FileInput infoLevel to set information output
         * @param fullfilename full filename name of file
         * @param columnIndex numberColumns with index, counting from 1.  If less than 1, (line word -1) is used for index if needed.
         * @param columnLabel numberColumns with label, counting from 1.
         * @param indexL list of indices in order found, if null then this is not filled
         * @param labelL list of labels in order found, if null then this is not filled.
         * @param findHeader First line ignored if true.
         */
    public int readIntIndexLabelList(String fullfilename, int columnIndex, int columnLabel,
            ArrayList<Integer> indexL, ArrayList<Integer> labelL, boolean headerOn){
     String commentLine="";
     return readIntIndexLabelList( fullfilename, columnIndex, columnLabel,
            indexL, labelL, headerOn, commentLine, infoLevel);
    }

            /**
         * Reads in list of labels each for a given index.
         * <p>Both label and index are treated as strings and stored in ArrayLists
         * in the same order as found in the the file.
         * <p>Each line has entries separated by whitespace.
         * Can be used to read in a partition label for a list of edges or vertices given by some index.
         * @param fullfilename full filename name of file
         * @param columnIndex numberColumns with index, counting from 1.  If less than 1, (line word -1) is used for index if needed.
         * @param columnLabel numberColumns with label, counting from 1.
         * @param indexL list of indices in order found, if null then this is not filled
         * @param labelL list of labels in order found, if null then this is not filled.
         * @param findHeader First line ignored if true.
         * @param infoLevel 2 or more gives some info on each line, 3 or more gives even more
         */
    static public int readIntIndexLabelList(String fullfilename, int columnIndex, int columnLabel,
            ArrayList<Integer> indexL, ArrayList<Integer> labelL,  String commentLine, int infoLevel)
    {
        boolean headerOn=false;
        return readIntIndexLabelList( fullfilename, columnIndex, columnLabel,
            indexL, labelL, headerOn, commentLine, infoLevel);
    }
        /**
         * Reads in list of labels each for a given index.
         * <p>Both label and index are treated as strings and stored in ArrayLists
         * in the same order as found in the the file.
         * <p>Each line has entries separated by whitespace.
         * Can be used to read in a partition label for a list of edges or vertices given by some index.
         * @param fullfilename full filename name of file
         * @param columnIndex numberColumns with index, counting from 1.  If less than 1, (line word -1) is used for index if needed.
         * @param columnLabel numberColumns with label, counting from 1.
         * @param indexL list of indices in order found, if null then this is not filled
         * @param labelL list of labels in order found, if null then this is not filled.
         * @param findHeader First line ignored if true.
         * @param commentLine if not null, any line starting with this string will be ignored.
         * @param infoLevel 2 or more gives some info on each line, 3 or more gives even more
         */
    static public int readIntIndexLabelList(String fullfilename, int columnIndex, int columnLabel,
            ArrayList<Integer> indexL, ArrayList<Integer> labelL, boolean headerOn, String commentLine,
            int infoLevel)
    {            
        if (columnLabel <1 ) throw new RuntimeException("Column number must be between 1 and number of columns in file");

        int res = 0;
        TextReader tr=openFile(fullfilename);
        if (tr==null) return -1;
        boolean findIndex=true;
        if (indexL==null) findIndex=false;
        boolean findLabel=true;
        if (labelL==null) findLabel=false;
        boolean commentLineOn=true;
        if (commentLine==null || commentLine.length()==0) commentLineOn=false;

        System.out.println("Starting to read integer "+(findIndex?"index":"")+" "+(findLabel?"label":"")+" from " + fullfilename);


        try {
            System.out.println(" File: "+fullfilename);
            // Read the noNamedata from the input file.
            int linenumber=0;
            String [] numbers = new String [nmax];
            Integer index =UNSET;
            Integer label =UNSET;
            if (headerOn){
                System.out.println("--- Ignoring first (header) line = "+tr.getln());
                linenumber++;
            }
            while (tr.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   //if (infoLevel>) System.out.println(linenumber+": ");
                   int column=0;
                    while (tr.eoln() == false)
                    {  // Read until end-of-line.
                       numbers[column++] = tr.getWord() ;
                    } //eoln
                if (infoLevel>1) System.out.print(linenumber+":");
                if (infoLevel>2) for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                if (infoLevel>1) System.out.println();
                    try {
                     if (commentLineOn && column>0 && numbers[0].startsWith(commentLine)){
                         System.out.println("--- Ignoring comment on line " + linenumber + " is " + numbers[0]);
                         continue;
                     }
                     if (findIndex){
                         if (columnIndex > column){
                             System.out.println("*** Too few columns on line " + linenumber + " expected " + columnIndex + " found " + column);
                             res = -10;
                             indexL.add(UNSET);
                         } else {
                             index = (columnIndex<1?(linenumber-1) : new Integer(numbers[columnIndex - 1]));
                             indexL.add(index);
                         }
                     } //eo if findIndex

                     if (findLabel){
                         if (columnLabel > column) {
                             System.out.println("*** Too few columns on line " + linenumber + " expected " + columnLabel + " found " + column);
                             labelL.add(UNSET);
                             res = -11;
                         }
                         else {
                             label = new Integer(numbers[columnLabel - 1]);
                             labelL.add(label);
                         }
                     } //eo if findLabel
     
                    
                    }// eo try
                    catch (RuntimeException e) {
                        System.out.println("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                        return -100;
                    }

               }//eofile

            if (res==0) res =indexL.size();
            System.out.print("Finished index/label list file input from "+ fullfilename+" found");
            if (findIndex) System.out.print(" : "+indexL.size()+" indices");
            if (findIndex) System.out.print(" : "+labelL.size()+" labels");
            System.out.println();
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            System.out.println("*** Input Error: " + e.getMessage());
            res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            tr.close();
        }


        if (indexL.size()!=labelL.size()) System.err.println("*** Two lists have different sizes");
      
        return res;
    }  // end of readIndexLabelList

        /**
         * Reads in list of labels each for a given index.
         * <p>If firstColumnIndex is true then it is assumed that each line starts
         * with the index otherwise the index is taken to start from zero and
         * increment with each line read.  Rest of each line is a list of labels.
         * <p>The result is returned as in labelToIndex.
         * This is a list of indices associated with each label.
         * <p>Each line has entries separated by whitespace.
         * @param fullfilename full filename name of file
         * @param labelToLabel if not null used to translate labels read into another Label (e.g. community label)
         * @param indexToLabel if not null list of labels associated with each index in order found.
         * @param labelToIndex if not null list of indices associated with each label in order found.
         * @param firstColumnIndex true if first numberColumns is the index
         * @param findHeader First line ignored if true.
         * @param infoLevel 2 or more gives some info on each line, 3 or more gives even more
         */
    static public int readIntIndexNeighbourList(String fullfilename,
            ArrayList<Integer> labelToLabel,
            ArrayList<ArrayList<Integer>> indexToLabel,
            ArrayList<ArrayList<Integer>> labelToIndex,
            boolean firstColumnIndex, boolean headerOn, int infoLevel)
    {
        int res = 0;
        TextReader tr=openFile(fullfilename);
        if (tr==null) return -1;
        boolean labelTranslate = ((labelToLabel==null)?false:true);

        System.out.println("Starting to read integer "+(firstColumnIndex?"index":"")+" label lists from " + fullfilename);

        boolean labelToIndexOn=false;
        if (labelToIndex!=null) {
            labelToIndexOn=true;
            System.out.println("Creating label to index list");
        }

        boolean indexToLabelOn=false;
        if (indexToLabel!=null) {
            indexToLabelOn=true;
            System.out.println("Creating index to label list");
        }


        try {
            System.out.println(" File: "+fullfilename);
            // Read the noNamedata from the input file.
            int linenumber=0;
            String [] numbers = new String [nmax];
            int index =-1;
            int nextNumber=UNSET;
            int column=UNSET;
            int label =UNSET;
            if (headerOn){
                System.out.println("Header Line = "+tr.getln());
                linenumber++;
            }
            while (tr.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   //if (infoLevel>) System.out.println(linenumber+": ");
                   column=0;
                   while (tr.eoln() == false)
                    {  // Read until end-of-line.
                       numbers[column++] = tr.getWord() ;
                    } //eoln
                   if (column==0) {
                       System.err.println("Empty line at line number "+linenumber);
                       continue;
                   }
                   if (infoLevel>1) System.out.print(linenumber+":");
                   if (infoLevel>2) for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                   if (infoLevel>1) System.out.println();
                   try {
                     nextNumber=0;
                     if (firstColumnIndex)index = Integer.parseInt(numbers[nextNumber++]);
                     else index++;
                     for (int i=nextNumber; i<column; i++){
                         label = Integer.parseInt(numbers[i]);
                         if (labelTranslate) {
                             if ( (label<0) || (label>=labelToLabel.size()) ) System.err.println("*** found label "+label+" but do not have a labelToLabel entry for it");
                             label=labelToLabel.get(label);
                         }

                         if (indexToLabelOn){
                             while (index>=indexToLabel.size()) indexToLabel.add(new ArrayList<Integer>());
                             indexToLabel.get(index).add(label);
                         }
                         if (labelToIndexOn){
                             while (label>=labelToIndex.size()) labelToIndex.add(new ArrayList<Integer>());
                             labelToIndex.get(label).add(index);
                         }
                     }
                    }// eo try
                    catch (RuntimeException e) {
                        System.out.println("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                        return -100;
                    }

               }//eofile

            if (res==0) res =labelToIndex.size();
            System.out.println("Finished index/label int neighbour list file input from "
                               + fullfilename+" found "+labelToIndex.size()
                               +" indices on "+linenumber+" lines");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            System.out.println("*** Input Error: " + e.getMessage());
            res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            tr.close();
        }

     return res;
    }  // end of readIndexLabelList


   // ***************************************************************************
        /**
         * Reads in edges labelled by strings from file.
         * <p>Each line has entries separated by whitespace.
         * The source vertex must be the first item on the line 
         * and then remaining entries are the different target vertices.
         * Directed, and labelled nature set by input parameters but
         * the graph must be unweighted.
         * Sets up given graph from this noNamedata.
         * @param tg graph to which vertices are to be added.
         * @param ext extension of filename name of file to read noNamedata
         * @param directed true if directed graph being input
         * @param vertexLabelled true if want vertex labels
         * @deprecated Use processStringVertexNeighbourFileUnipartite which handles bipartite graphs
         */
    public int processStringVertexNeighbourFileUnipartite(timgraph tg, String ext, 
            boolean directed, boolean vertexLabelled )
    {
        
        tg.setWeightedEdges(false);
        tg.setVertexlabels(vertexLabelled);
        tg.setDirectedGraph(directed);

        ArrayList<String> edgeLL = new ArrayList();
        // Make a sorted list of the integer labels
        TreeSet<String> sourceLL = new TreeSet();
        int res = readStringVertexNeighbourFileUnipartite(tg, ext, sourceLL, edgeLL) ;
        if (res<0) return res;

        // Set up empty network of correct size.
        // could  set sizes only if some flag says to e.g. if size<=0
        tg.setMaximumVertices(sourceLL.size());
        //int [] vertexLabelToIndex = new int[tg.setMaximumVertices];
        tg.setMaximumStubs(edgeLL.size());
        tg.setNetwork();

        // Associate each vertex label to an integer index of a vertex in a timgraph
        TreeMap<String,Integer> vertexLabelToIndex = new TreeMap();
        Iterator<String> iter = sourceLL.iterator();
        int v=0;
        String current="NOTUSED";
        VertexLabel newLabel = new VertexLabel();
        while (iter.hasNext()) {
            current=iter.next();
            System.out.println(v+" "+current);
            vertexLabelToIndex.put(current, v++ );
            if (tg.isVertexLabelled()){
                newLabel.setName( current);
                tg.addVertex(newLabel);
                }
            else tg.addVertex();
        } // eo while


        // Now add the edges
        int source=-1;
        int target=-1;
        for (int e=0; e < edgeLL.size(); e++)
        {
            source = vertexLabelToIndex.get(edgeLL.get(e++));
            target = vertexLabelToIndex.get(edgeLL.get(e));
            tg.addEdge(source,target);
        }// eo for e

        return 0;
    }

        /**
         * Reads in edges labelled by strings from file.
         * <p>Each line has entries separated by whitespace.
         * The source vertex must be the first item on the line 
         * and then remaining entries are the different target vertices.
         * Directed, and labelled nature set by input parameters but
         * the graph must be unweighted. If bipartite nature is indicated
         * it is assumed that the first entry on each line if a vertex of type one, and the
         * remaining vertices on each line are of the second type.  Option to check this is given 
         * but this option is ignored if bipartite graph not indicated.
         * Sets up given graph from this noNamedata.
         * <br>Edge weights are defined to be <tt>1/degree<tt> of the
         * source node so only makes sense for bipartite or directed graphs.
         * @param tg graph to which vertices are to be added.
         * @param ext extension of filename name of file to read noNamedata
         * @param weighted true if weights are to be assigned to edges
         * @param directed true if directed graph being input
         * @param multiedge true if multiedge graph being input
         * @param vertexLabelled true if want vertex labels
         * @param bipartite indicates that bipartite graph is expected
         * @param forceLowerCase force all strings to be lower case
         * @param checkBipartite if true will check bipartite nature if appropriate
         * @param sampleFrequency word of lines to skip, 1 or less and all are taken
         * @param stringFilterSource filter to apply to source vertices
         * @param stringFilterTarget filter to apply to source vertices
         * @param filterL list of vertices left out due to filters
         */
    public int processStringVertexNeighbourFile(timgraph tg, String ext, 
            boolean weighted, boolean directed, boolean multiedge, boolean vertexLabelled, boolean bipartite,
            boolean forceLowerCase,
            boolean checkBipartite,
            int sampleFrequency,
            StringFilter stringFilterSource,
            StringFilter stringFilterTarget,
            TreeSet<String> filterL)

    {
        
        tg.setWeightedEdges(weighted); // only makes sense for bipartite
        tg.setVertexlabels(vertexLabelled);
        tg.setDirectedGraph(directed);
        tg.setMultiEdge(multiedge);

        ArrayList<String> edgeLL = new ArrayList();
        // Make a sorted list of the integer labels
        TreeSet<String> sourceLL = new TreeSet();
        TreeSet<String> vertex2LL;
        if (bipartite) vertex2LL = new TreeSet(); 
        else vertex2LL=null; // null => not bipartite
        DoubleArrayList edgeWeightLL;
        if (weighted) edgeWeightLL = new DoubleArrayList();
        else edgeWeightLL=null;

        tg.inputName.setNameEnd(ext);
        String fullFileName = tg.inputName.getFullFileName();
        int res = readStringVertexNeighbourFile(fullFileName, sourceLL, vertex2LL, edgeLL, edgeWeightLL, 
                                                forceLowerCase, checkBipartite, sampleFrequency, 
                                                stringFilterSource, stringFilterTarget, filterL, (infoLevel>1)) ;
        if (res<0) return res;

        // Set up empty network of correct size.
        // could  set sizes only if some flag says to e.g. if size<=0
        if (bipartite) {
            tg.setBipartite(sourceLL.size(), vertex2LL.size());
            tg.setMaximumVertices(sourceLL.size()+vertex2LL.size());
        }
        else tg.setMaximumVertices(sourceLL.size());
        tg.setMaximumStubs(edgeLL.size());
        tg.setNetwork();

        // Associate each vertex label to an integer index of a vertex in a timgraph
        TreeMap<String,Integer> vertexLabelToIndex = new TreeMap();
        addVertices(sourceLL, vertexLabelToIndex, tg);
        if (bipartite) addVertices(vertex2LL, vertexLabelToIndex, tg);


        // Now add the edges
        int source=-1;
        int target=-1;
        double dw=1;
        for (int e=0; e < edgeLL.size(); e++)
        {
            source = vertexLabelToIndex.get(edgeLL.get(e++));
            target = vertexLabelToIndex.get(edgeLL.get(e));
            if (weighted) edgeWeightLL.getQuick(e/2);
            tg.addEdgeWithTests(source,target,dw);
//            if (weighted) {
//                if (multiedge) tg.addEdge(source,target,edgeWeightLL.getQuick(e/2));
//                else tg.increaseEdgeWeight(source,target,edgeWeightLL.getQuick(e/2));
//            }
//            else {
//                if (multiedge) tg.addEdge(source,target);
//                else tg.addEdgeUnique(source,target);
//            }

        }// eo for e

        
        return 0;
    }

    /**
     * Add vertices found to timgraph.
     * <br>The griven timgraph must have already been set up with appropriate
     * maximum word of vertices.
     * @param sourceLL collection of vertices to add
     * @param vertexLabelToIndex TreeMap of ordered vertices linking label to global vertex word
     * @param tg timgaph to which vertices are added
     */
    private void addVertices(Collection<String> sourceLL, 
            TreeMap<String,Integer> vertexLabelToIndex,
            timgraph tg){
        Iterator<String> iter = sourceLL.iterator();
        int v=tg.getNumberVertices();
        String current="NOTUSED";
        VertexLabel newLabel = new VertexLabel();
        while (iter.hasNext()) {
            current=iter.next();
            if (infoLevel>0) System.out.println(v+" "+current);
            if (tg.getMaximumVertices()==tg.getNumberVertices()) System.err.println("*** Imminent failure - reached capacity of "+tg.getNumberVertices()+" vertices, "+current+" vertex is one too many");
            vertexLabelToIndex.put(current, v++ );
            if (infoLevel>0) System.out.println(v+" "+current);
            if (tg.isVertexLabelled()){
                newLabel.setName( current);
                tg.addVertex(newLabel);
                }
            else tg.addVertex();
        } // eo while
    }
    
     /**
      * Reads in list of neighbours of each vertex labelled by strings from file filename.
      * <p>Each line has entries separated by whitespace.
      * First entry is the source vertex and the remaining entries on that line are its neighbours.
      * Can deal with bipartite structures.
      * If <tt>edgeWeightLL</tt>, if given as null then weights are not set.  If it is set then
      * the weight is the 1/(word target vertices on line).  This only makes sense
      * if it is a bipartite or directed graph and if the source vertices (first numberColumns) are unique.
      * <p><tt>stringFilter<em>VertexType</em><tt> should be instantiated extensions of  <tt>StringFilter<tt> class or null.
      * Null means no test is performed.  
      * Its instantiated so that it can be defined with run time parameters such as
      * a minumum word of letters or sizes to be required.
      * @param fullFileName used to determine directory and name of file.
      * @param sourceLL set of vertices found, only of first type if bipartite
      * @param vertex2LL if null then make unipartite graph, otherwise it will be a list of the second types vertices found for a bipartite network
      * @param edgeLL list of source then target of each edge
      * @param edgeWeightLL list of weights for each edge
      * @param forceLowerCase force all strings to be lower case
      * @param checkBipartite forces a check of bipartite nature, ignored if not read as bipartite
      * @param sampleFrequency word of lines to skip, 1 or less and all are taken
      * @param stringFilterSource filter to apply to source vertices
      * @param stringFilterTarget filter to apply to source vertices
      * @param filterL list of vertices left out due to filters
      * @param infoOn true (false) if want information on screen on lines being read.
*/
    static public int readStringVertexNeighbourFile(String fullFileName,
            Set<String> sourceLL, Set<String> vertex2LL, 
            ArrayList<String> edgeLL, 
            DoubleArrayList edgeWeightLL, 
            boolean forceLowerCase,
            boolean checkBipartite,
            int sampleFrequency,
            StringFilter stringFilterSource,
            StringFilter stringFilterTarget,
            TreeSet<String> filterL,
            boolean infoOn)
    {
        int minVertexLabel=9999999;
        int maxVertexLabel=-minVertexLabel;

//        tg.maximumCoordinate = new Coordinate();
//        tg.minimumCoordinate = new Coordinate();
        int res=0;  // error code.
        
        boolean bipartite=true;
        if (vertex2LL==null) bipartite=false;

        boolean readSome=true; // store only every sampleFrequency lines
        if (sampleFrequency<2) readSome=false;
        
        final boolean filterSource; // filter the source vertices
        if (stringFilterSource==null) filterSource=false;
        else filterSource=true;
        
        final boolean filterTarget; // filter the target vertices
        if (stringFilterTarget==null) filterTarget=false;
        else filterTarget=true; 
        
        TextReader tr=openFile(fullFileName);
        if (tr==null) return -1;
        System.out.println("Starting to read "+(bipartite?"bipartite":"unipartite")+" vertex neighbour list with string vertex labels from " + fullFileName);

        try {
            System.out.println(" File: "+fullFileName);
            // Read the noNamedata from the input file.
            int linenumber=0;
             // Use ArrayList<String>?
            String [] numbers = new String [nmax];
            String source ="NOTSET";
            String target ="NOTSET";
            while (tr.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoOn) System.out.println(linenumber+": ");
                   if (readSome && ((linenumber%sampleFrequency)!=1)) {tr.getln(); continue;}
                   int column=0;
                   // Read until end-of-line.
                   if (forceLowerCase) while (tr.eoln() == false) numbers[column++] = tr.getWord().toLowerCase();
                   else                while (tr.eoln() == false) numbers[column++] = tr.getWord();
                   if (infoOn) {
                    for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                    System.out.println();
                   }
                    try {
                     if (column<1) {
                         System.err.println("*** Too few columns on line " + linenumber + " expected at least one, found " + column);
                         res = -10;
                         edgeLL.add("T.F.C. line " + linenumber);
                     } else {
                         //boolean rejected=false;
                         source = numbers[0];
                         if ((filterSource) && !stringFilterSource.isAcceptable(source)){
                             if (filterL!=null) filterL.add(source);
                             continue; // do not check target vertices
                             //rejected=true;
                         }
                         else sourceLL.add(source); 
                         double ew = 1.0/((double) (column-1));
                         for (int c = 1; c < column; c++) {
                                target = numbers[c];
                                edgeLL.add(source);
                                edgeLL.add(target);
                                if ((filterTarget) && !stringFilterTarget.isAcceptable(target)) {
                                    if (filterL!=null) filterL.add(target);
                                    continue; // check rest of traget vertices
                                }                                
                                if (edgeWeightLL!=null) edgeWeightLL.add(ew);
                                if (bipartite) {
                                    vertex2LL.add(target);
                                } else {
                                    sourceLL.add(target);
                                }
                            }
                        }
                    }// eo try
                    catch (RuntimeException e) {
                        System.err.println("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                        return -100;
                    }

               }//eofile

            if (res==0) res =sourceLL.size();
            System.out.println("Finished vertex neighbour list file input from " + fullFileName+" found "+sourceLL.size()+" vertices, "+edgeLL.size()+" stubs");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            System.err.println("*** Input Error: " + e.getMessage());
            res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            tr.close();
        }


        if (bipartite && checkBipartite){
          if (checkBiPartite(sourceLL, vertex2LL) ) System.out.println("--- Bipartite structure OK");
          else{
              System.err.println("*** Requested bipartite structure not found");
              res=-3;
          }
        }  
        
        return res;
    }
    
    
    /**
      * Reads in list of source vertices as string labels from first numberColumns of a file.
      * <p>Each line has entries separated by whitespace.
      * First entry is the vertex of interest, rest are ignored.
      * <p> No check for a bipartite graph and no lines are skipped.
      * @param fullFileName used to determine directory and name of file.
      * @return ordered set of vertices found in first numberColumns
      */
    public TreeSet<String> readStringVerticesFile(String fullFileName){
       boolean checkBipartite=false;
     int sampleFrequency=1;
     boolean forceLowerCase=true;
     return readStringVerticesFile(fullFileName,  forceLowerCase, checkBipartite, sampleFrequency);     
    }
      

         /**
      * Reads in list of source vertices as string labels from first numberColumns of a file.
      * <p>Each line has entries separated by whitespace.
      * First entry is the vertex of interest, rest are ignored.
      * @param fullFileName used to determine directory and name of file.
      * @param forceLowerCase focrce all strings to be lower case
      * @param checkBipartite forces a check of bipartite nature, ignored if not read as bipartite
      * @param sampleFrequency word of lines to skip, 1 or less and all are taken
      * @return ordered set of vertices found in first numberColumns
      */
    public TreeSet<String> readStringVerticesFile(
            String fullFileName, 
            boolean forceLowerCase,
            boolean checkBipartite, int sampleFrequency){
        
      System.out.println("Starting to read list with vertices in first column from " + fullFileName);
        
      /**
        * List of vertices found 
        */ 
      TreeSet<String> vertexLL = new TreeSet();
       /**
         * List of the neighbours for the vertices
         */
      TreeSet<String> neighboursLL = new TreeSet();
      /**
        * List of vertex then community of each edge
        */
      ArrayList<String> edgeLL = new ArrayList();
      
      /**
        * Weights list where weight is the 1/(word target vertices on line)
        * <p>Note that these are given for each edge once.
        */
      DoubleArrayList edgeWeightLL = new DoubleArrayList();  
      
      //Null StringFilters means no test is performed on vertices.  
        StringFilter stringFilterSource = null;
        StringFilter stringFilterTarget = null;
        TreeSet<String> filterL = null;
        
        readStringVertexNeighbourFile(fullFileName,
                vertexLL, neighboursLL,
                edgeLL,
                edgeWeightLL,
                forceLowerCase, checkBipartite,
                sampleFrequency,
                stringFilterSource, stringFilterTarget,
                filterL, (infoLevel>1));
        

        return vertexLL;
        
    }

    /**
     * Checks to see if two vertex collections have any elements in common.
     * @param sourceLL
     * @param vertex2LL
     * @return true if collections have zero intersection
     */
    static public boolean checkBiPartite(Collection<String> sourceLL, Collection<String> vertex2LL){
        boolean result=true;
        Collection<String> v1;
        Collection<String> v2;
        if (sourceLL.size()>vertex2LL.size())
        {
            v1=sourceLL;
            v2=vertex2LL;
        }
        else
            {
            v2=sourceLL;
            v1=vertex2LL;
        }
        for (String s: v2){
            if (v1.contains(s)) {
             result = false;   
             System.out.println("checkBiPartite: both vertex sets contain \""+s+"\"");
            }
        }
        return result;
    }

    /**
         * Reads in list of neighbours of each vertex labelled by strings from file filename.
         * <p>Each line has entries separated by whitespace.
         * First entry is the source vertex and the remaining entries on that line are its neighbours.
         * @param ext extension of filename name of file to read noNamedata
         * @param edgeLL list of source then target of each edge
         * @deprecated Use bipartite aware version readStringVertexNeighbourFile
         */
    public int readStringVertexNeighbourFileUnipartite(timgraph tg, String ext, 
            TreeSet<String> sourceLL, ArrayList<String> edgeLL)
    {
        int minVertexLabel=9999999;
        int maxVertexLabel=-minVertexLabel;

//        tg.maximumVertexLabel = new VertexLabel();
//        tg.minimumVertexLabel = new VertexLabel();
        tg.inputName.setNameEnd(ext);
        String fullFileName = tg.inputName.getFullFileName();
        data=openFile(fullFileName);
        if (data==null) return -1;
        int res=0;
        System.out.println("Starting to read vertex neighbour list with string vertex labels from " + fullFileName);

        try {
            System.out.println(" File: "+fullFileName);
            // Read the noNamedata from the input file.
            //String line;
            //int first;
            //int n;
            int linenumber=0;
            //int edgenumber=0;
            //int nmax=10; // max no. columns
            String [] numbers = new String [nmax];
            String source ="NOTSET";
            String target ="NOTSET";
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoLevel>2) System.out.println(linenumber+": ");
                   int column=0;
                    while (data.eoln() == false)
                    {  // Read until end-of-line.
                       numbers[column++] = data.getWord() ;
                    } //eoln
                if (infoLevel>1) {
                    System.out.print(linenumber);
                    for (int c=0; c<column; c++) System.out.print(", "+numbers[c]);
                    System.out.println();
                }
                    try {
                     if (column<1) {
                         System.err.println("*** Too few columns on line " + linenumber + " expected at least one, found " + column);
                         res = -10;
                         edgeLL.add("T.F.C. line " + linenumber);
                     } else {
                         source = numbers[0];
                         sourceLL.add(source); 
                         for (int c=1; c<column; c++) {
                         target = numbers[c];
                         edgeLL.add(source);
                         edgeLL.add(target);
                         sourceLL.add(target); }
                     }
                    }// eo try
                    catch (RuntimeException e) {
                        System.err.println("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                        return -100;
                    }

               }//eofile

            if (res==0) res =sourceLL.size();
            System.out.println("Finished vertex neighbour list file input from " + fullFileName+" found "+sourceLL.size()+" vertices, "+edgeLL.size()+" stubs");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            System.err.println("*** Input Error: " + e.getMessage());
            res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }


        return res;
    }
    
    
    
    
// ***************************************************************************
        /**
         * Reads in graph from pajek format file.
         * <p>Must set maximum number of vertices and edges, and (un)directed
         * nature from input line. If vertex labels not on then all
         * information in vertex section is ignored.
         * <br>Create graph with warning not errors on problems.
         * @param ext extension of filename name of file to read noNamedata
         * @param directed true if directed graph being input
         *@param weighted true if weighted graph being input
         */
    public int processPajekFile(timgraph tg, String ext, boolean directed, boolean weighted)
    {
        final String vertexKeyword = "*Vertices";
        final String arcKeyword = "*Arcs";
        final String edgeKeyword = "*Edges";
        //initialgraph=0;
        //tg.setVertexlabels(true);
        //tg.setDirectedGraph(directed);
//        tg.minimumCoordinate= new Coordinate(9999999,9999999,9999999);
//        tg.maximumCoordinate= new Coordinate(-9999999,-9999999,-9999999);
        tg.setNetwork();
        tg.inputName.setNameEnd(ext);
        String fullFileName = tg.inputName.getFullFileName();
        System.out.println("Starting to read "+(directed?"":"un")+"directed "+(weighted?"":"un")+"weighted Pajek Network file from " + fullFileName);
        int res=0;  // error code.

        data=openFile(fullFileName);
        if (data==null) return -1;

        int linenumber=1;
        try {
            System.out.println(" File: "+fullFileName);
            // Read the noNamedata from the input file.
            String line;
            int TNV=-1; // total number vertices
            //String s;
            // First line should be *Vertices TNV
            line=data.getln();
            String[] word = line.split("\\x20+"); // this splits into words separated by white space
//            if (word[0].charAt(0) !='*')
            if (!word[0].startsWith(vertexKeyword))
            {
                System.err.println("*** ERROR first line ("+linenumber
                        +") should start with with \""+vertexKeyword+"\", found \""+word[0]+"\"");
                return -2;
            }
            try {
                    TNV=Integer.parseInt(word[1]);
                    if ((TNV <1) || TNV>tg.getMaximumVertices()  ) {
                        System.err.println("*** ERROR first line ("+linenumber
                                +") number of vertices must be positive but not greater than "
                                +tg.getMaximumVertices()+", found "
                                +word[1]);
                        return -2;
                    }
                }
            catch (RuntimeException e){
                System.err.println("*** ERROR first line ("+linenumber
                        +") second word must be integer, found \""+word[1]+"\"");
                return -2;
            }
            System.out.println("Vertex Section starts on line "+linenumber+" Total number of vertices given as "+TNV);
            // initialise requested number of vertices
            for (int v=0; v<TNV; v++) tg.addVertex();
            // Now read in the list of vertices
            int r=0;
            while (!data.eof())
            {
                linenumber++;
                line= data.getln();
                if (line.charAt(0)=='*') break;
                if (!tg.isVertexLabelled()) continue; // ignore vertex data
                if (infoLevel>2) System.out.println(linenumber+"/"+TNV+": ");
                r=processPajekVertexLine(line, linenumber, TNV, tg.getVertexLabelList());
                if (r<0) System.err.println("*** ERROR line "+linenumber+"  in reading vertex line.");
            }// for line word end of vertex list reading
            System.out.println("Vertex Section contained "+(linenumber-1)+" vertex lines, total number of vertices given as "+TNV);

            if (data.eof()){
                System.out.println("!!! No edge section found");
                return 1;
            }

            // Current line should be start of edge section
            // starting with *Edges TNE or *Arcs TNE
            word = line.split("\\x20+"); // this splits into words separated by spaces
            if (line.charAt(0) !='*')
            {
                System.err.println("*** ERROR line "+linenumber+
                        " next line should be start of edge/arc section, not \""+line+"\"");
                return -5;
            }

            boolean tempdirected=false;
            if (line.startsWith(arcKeyword)) tempdirected=true;
            else
            {
                if (line.startsWith(edgeKeyword)) tempdirected=false;
                else
                    {
                System.err.println("*** ERROR line "+linenumber
                        +"  first word of edge section should be "
                        +edgeKeyword+" or "+arcKeyword+", not \""+line+"\"");
                return -6;
                }
            }
            if (tempdirected != tg.isDirected())
                System.out.println("--- WARNING line "+linenumber+
                        "  file requests "+line+" but graph will set to be opposite");

            int TNE=-1;
            int maxEdges=tg.getMaximumStubs()/2;
            try {
                    TNE=Integer.parseInt(word[1]);
                    if ((TNE <1) || TNE>maxEdges  ) {
                        System.err.println("*** ERROR edge/arc definition line "+linenumber
                                +", number of edges/arcs must be positive but not greater than "
                                +maxEdges+", found "
                                +word[1]);
                        return -7;
                    }
                }
            catch (RuntimeException e){
                System.err.println("*** ERROR edge/arc definition line "+linenumber
                        +" second word must be integer, found \""+word[1]+"\"");
                return -8;
            }

            System.out.println("Starting to read "+TNE
                    +" edges/arcs on line "+linenumber
                    +" graph Type is "+ tg.graphType(SEP) );
            // Now read in the list of edges
            int vs=timgraph.IUNSET;
            int vt=timgraph.IUNSET;
            double vw=timgraph.DUNSET;
            int edgeNumber=0;
            while (!data.eof())
            {
                linenumber++;
                edgeNumber++;
                if (infoLevel>2) System.out.println(linenumber+": ");
                if (edgeNumber>maxEdges){
                        System.err.println("*** ERROR edge/arc definition line "+linenumber
                                +", this will be edge number "
                                +edgeNumber+" but this bigger than allowed "
                                +maxEdges);
                        return -9;
                    }
                line=data.getln();
                word = line.split("\\x20+"); // this splits into words separated by spaces
                // read source
                try {
                    vs=Integer.parseInt(word[0]);
                    if ((vs <1) || vs> tg.getMaximumVertices()  ) {
                        System.err.println("*** ERROR edge/arc definition line "+linenumber
                                +", source vertex index must be positive but not greater than "
                                +tg.getMaximumVertices()+", found \""
                                +word[0]+"\"");
                        return -10;
                    }
                }
                catch (RuntimeException e){
                    System.err.println("*** ERROR edge/arc definition line "+linenumber
                            +" first word must be integer, found \""+word[0]+"\"");
                    return -11;
                }

                // read target
                try {
                    vt=Integer.parseInt(word[1]);
                    if ((vt <1) || vt> tg.getMaximumVertices()  ) {
                        System.err.println("*** ERROR edge/arc definition line "+linenumber
                                +", target vertex index must be positive but not greater than "
                                +tg.getMaximumVertices()+", found \""+word[1]+"\"");
                        return -12;
                    }
                }
                catch (RuntimeException e){
                    System.err.println("*** ERROR edge/arc definition line "+linenumber
                            +" second word must be integer, found \""+word[1]+"\"");
                    return -13;
                }

                // set edge with weight if needed
                if (weighted) {
                    vw=1.0;
                    if (word.length>1){
                        try { vw=Double.parseDouble(word[2]);}
                        catch (RuntimeException e){
                          System.err.println("*** ERROR edge/arc definition line "+linenumber
                            +" third word must be double if present, found \""+word[2]+"\"");
                          return -14;
                        }
                    } // eo if word.length
                    tg.addEdge((vs-1),(vt-1),vw); // shift index by one
                }
                else tg.addEdge((vs-1),(vt-1)); // shift index by one
                
               }// for line word end of vertex list reading

            System.out.println("Finished reading pajek file " + fullFileName
                    +"  number of lines was "+linenumber);
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            System.err.println("*** Input Error "+fullFileName+" on line "+linenumber+" " + e.getMessage());
            return -99;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }

        if (!tg.isVertexLabelled()) return 0;

        // set positions if labelled and if needed
        tg.calcMinimumVertexLabel();
        tg.calcMaximumVertexLabel();

       if (tg.getMinimumVertexCoordinate().distance2D(tg.getMaximumVertexCoordinate())<1e-6)
       {
            double angle=0.0;
            for (int v=0; v<tg.getNumberVertices();v++)
            {
               double f = ((double) v)/((double) tg.getNumberVertices());
               angle = 2.0*Math.PI*f;
               tg.setVertexPositionQuick(v, new Coordinate(0.5+Math.cos(angle)/2.0,0.5+Math.sin(angle)/2.0,f));
            }
            tg.getMinimumVertexCoordinate().set(0.0,0.0,0);
            tg.getMaximumVertexCoordinate().set(+1.0,+1.0,1.0);
       }

        return 0;
    }  // end of processPajekFile

    /**
     * Processes single line of pajek .net file vertex list.
     * <p>Does not test to see if it is a vertex line.  Uses information to
     * set given list of vertex labels.
     * Line can be separated by any type of white space, not just spaces. It
     * assumes that the first entry is the integer pajek index, so checks that
     * its it is between 1 and totalNumberVertices
     * (error -2 if not, -1 if first entry not an integer).
     * <br>The second argument is optional and can be a vertex name within double quotes.
     * It is used Name of VeretxLabel.  If not present the pajek index is used
     * within square brackets.  Note that any escape character or spaces
     * (ascii codes decimal 32 and lower) are replaced by underscores.
     * Any character with ASCII code 127 (DEL) and above
     * (the 128 plus are often used for foreign characters and other stuff)
     * is replaced by a star *.
     * (Error -3 if single double quote found).
     * <br>The next three arguments after the index or the label (if present)
     * are also optional. Each is treated as a double and used to set coordinates
     * x, y and z in that order.
     * If any fail to exist or are not doubles processing stops with remaining
     * unset coordinates taking default values
     * (placed on unit circle for x and y based on vertex index and
     * the timgraph index for the z coordinate).
     * @param line the line to be processed
     * @param linenumber number of line
     * @param totalNumberVertices total number of vertices
     * @param vertexLabelList array of labels, must already exist.
     * @return timgraph vertex index (non negative) if no error, negative if a problem.
     */
    public int processPajekVertexLine(String line, int linenumber, int totalNumberVertices,
            VertexLabel [] vertexLabelList){
                
        // Must remove quoted string first as spaces in quotes are 
        // interpreted as separators.
        // Second optional item is name of vertex in quotes
        String label="";
        String noQuoteLine=line;
        int firstdquote = line.indexOf('\"');
        if (firstdquote>=0) {
                    int seconddquote = line.indexOf('\"',firstdquote+1);
                    if (seconddquote<0) {
                        System.err.println("At line "+linenumber+
                                " found lone double quote at position "
                                +firstdquote+" in \""+line+"\"");
                        return -3;
                    }
                    label=line.substring(firstdquote+1, seconddquote);
                    label=label.replaceAll("[\\x00-\\x20]", "_");
                    label=label.replaceAll("[\\x7F-\\xFF]", "*");
                    noQuoteLine=line.substring(0, firstdquote)+line.substring(seconddquote+1);                    
                }
        
        String[] word = noQuoteLine.split("\\x20+"); // this splits into words separated by spaces
        int n=0; // count which word we are looking at
                // first set pajek index number - MUST be first item
        int pajekVertexIndex=-1; // this is one more than the timgraph index
        try {
                    pajekVertexIndex=Integer.parseInt(word[n]);
                    if ((pajekVertexIndex <1)   || (pajekVertexIndex> totalNumberVertices ) ) {
                        System.err.println("*** ERROR line "+linenumber
                                +" first item must be pajek vertex index between 1 and "
                                +(totalNumberVertices)
                                +" incl., should not be "+pajekVertexIndex);
                        return -2;
                    }
                }
                catch (RuntimeException e){
                        System.err.println("At line "+linenumber+
                                " Can't convert first word to integer \""+word[n]+"\"");
                        return -1;
                    }
                int timgraphVertexIndex=pajekVertexIndex-1; // timgraph index is one less than pajek index
                vertexLabelList[timgraphVertexIndex].setNumber(pajekVertexIndex);
                
                if (label.length()==0) label = "["+pajekVertexIndex+"]"; // default name
                vertexLabelList[timgraphVertexIndex].setName(label);

                // now set coordinates
                double x=Math.cos(linenumber/totalNumberVertices); 
                double y=Math.sin(linenumber/totalNumberVertices);  
                double z=timgraphVertexIndex;
                try {
                        x=Double.parseDouble(word[n++]);
                        y=Double.parseDouble(word[n++]);
                        z=Double.parseDouble(word[n++]);
                    }//eo try
                    catch (RuntimeException e){
                    }
                vertexLabelList[timgraphVertexIndex].setPosition(new Coordinate(x,y,z) );
                return timgraphVertexIndex;
    }




// ***************************************************************************
        /**
         * Reads in graph from pajek format file.
         * Remove/detect quotes from labels.
         * Create graph with warning not errors on problems.
         * @param ext extension of filename name of file to read noNamedata
         * @param directed true if directed graph being input
         * @param weighted true if weighted graph being input
         * @deprecated
         */
    public int processPajekFileOLD(timgraph tg, String ext, boolean directed, boolean weighted)
    {
        //initialgraph=0;
        tg.setVertexlabels(true);
        tg.setDirectedGraph(directed);
//        tg.minimumCoordinate= new Coordinate(9999999,9999999,9999999);
//        tg.maximumCoordinate= new Coordinate(-9999999,-9999999,-9999999);
        tg.setNetwork();
        tg.inputName.setNameEnd(ext);
        String fullFileName = tg.inputName.getFullFileName();
        System.out.println("Starting to read "+(directed?"":"un")+"directed "+(weighted?"":"un")+"weighted Pajek Network file from " + fullFileName);
        int res=0;  // error code.

        data=openFile(fullFileName);
        if (data==null) return -1;


        int linenumber=1;
        try {
            System.out.println(" File: "+fullFileName);
            // Read the noNamedata from the input file.
            String line;
            int TNV=-1;
            String s;
            // First line should be *Vertices TNV
            s=data.getWord();
            if (s.charAt(0) !='*')
            {
                System.err.println("*** ERROR line "+linenumber+" first word should not be _"+s+"_");
                return -1;
            }
            TNV=data.getlnInt(); //read and discard rest of line
            if (TNV<1)
                {
                System.err.println("*** ERROR line "+linenumber+"  total number of vertices should not be "+TNV);
                return -2;
            }
            System.out.println("Vertex Section starts on line "+linenumber+" Total number of vertices given as "+TNV);


            // Now read in the list of vertices
            int vn=-1;
            double x,y,z;
            for (linenumber++;linenumber<(TNV+2);linenumber++)
            {
                if (infoLevel>2) System.out.println(linenumber+"/"+TNV+": ");
                if (data.eof())
                {
                    System.err.println("*** ERROR line "+linenumber+"  end of file encountered before all"+TNV+" vertices read.");
                    return -3;
                }
                // Read until end-of-line.
                String label = "["+linenumber+"]";
                vn=-1; s=""; x=0; y=0; z=linenumber;
                for (int column =1; !data.eoln(); column++)
                    switch(column)
                    {
                        case 1: vn = data.getInt(); break; // first entry is vertex word
                        case 2: String temp  = data.getWord(); label=temp.substring(1, temp.length()-1)  ; break; // 2nd vertex label
                        case 3: x = data.getDouble(); break; // Next three are positions
                        case 4: y = data.getDouble(); break;
                        case 5: z = data.getDouble(); break;
                        default: s = data.getWord();
                    } //eo switch and for
                    if ((vn != (linenumber-1))  || (vn>(TNV+1) ) ) {
                        System.err.println("*** ERROR line "+linenumber+"  vertex number should not be "+vn);
                        return -4;
                    }
                    Coordinate position = new Coordinate(x,y,z) ;
                    tg.addVertex(new VertexLabel(label, position ) );
            }// for line word end of vertex list reading

            // Next line should be *Edges TNE or *Arcs TNE
            s=data.getWord();
            if (s.charAt(0) !='*')
            {
                System.err.println("*** ERROR line "+linenumber+" next word should be start of edge/arc section, not _"+s+"_");
                return -5;
            }

            boolean tempdirected=false;
            if (s.charAt(1) == 'A') tempdirected=true;
            else
            {
                if (s.charAt(1) == 'E')  tempdirected=false;
                else
                    {
                System.err.println("*** ERROR line "+linenumber+"  first word of edge section should not be "+s);
                return -6;
                }
            }
            if (tempdirected != tg.isDirected()) System.out.println("--- WARNING line "+linenumber+"  file requests "+s+" but graph will set to be opposite");


            int TNE=-1;
            TNE=data.getInt();
            if (TNE<1)
                {
                System.err.println("*** ERROR line "+linenumber+"  total number of edges should not be "+TNE);
                return -7;
            }

            System.out.println("Starting to read edges on line "+linenumber+" graph Type is "+ tg.graphType(SEP) );
            // Now read in the list of edges
            int en=-1;
            int vs=-1;
            int vt=-1;
            int vw=-1;
            while (!data.eof())

            {
                linenumber++;
                if (infoLevel>2) System.out.println(linenumber+": ");
                // Read until end-of-line.
                vs = data.getInt();
                if ((vs<1) || (vs>tg.getNumberVertices()))
                {
                System.err.println("*** ERROR line "+linenumber+"  source vertex number of "+vs+" when total is "+tg.getNumberVertices());
                return -8;
                }

                vt = data.getlnInt(); // reads next int and discards rest of line
                if ((vt<1) || (vt>tg.getNumberVertices()))
                {
                System.err.println("*** ERROR line "+linenumber+"  target vertex number of "+vt+" when total is "+tg.getNumberVertices());
                return -9;
                }

                if (weighted) {
                    vw = data.getlnInt(); // reads next int and discards rest of line
                if ((vt<1) || (vt>tg.getNumberVertices()))
                {
                System.err.println("*** ERROR line "+linenumber+"  weight "+vw+" of edge from "+vs+" to "+vt+" when total is "+tg.getNumberVertices());
                return -10;
                }
                tg.addEdge((vs-1),(vt-1),vw);
                }
                else
                {
                    data.getln();
                    tg.addEdge((vs-1),(vt-1));
                }

               }// for line word end of vertex list reading

            System.out.println("Finished reading pajek file " + fullFileName+"  number of lines was "+linenumber);
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            System.err.println("*** Input Error "+fullFileName+" on line "+linenumber+" " + e.getMessage());
            res=-99;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }

        tg.calcMinimumVertexLabel();
        tg.calcMaximumVertexLabel();

       if (tg.getMinimumVertexCoordinate().distance2D(tg.getMaximumVertexCoordinate())<1e-6)
       {
            double angle=0.0;
            for (int v=0; v<tg.getNumberVertices();v++)
            {
               double f = ((double) v)/((double) tg.getNumberVertices());
               angle = 2.0*Math.PI*f;
               tg.setVertexPositionQuick(v, new Coordinate(0.5+Math.cos(angle)/2.0,0.5+Math.sin(angle)/2.0,f));
            }
            tg.getMinimumVertexCoordinate().set(0.0,0.0,0);
            tg.getMaximumVertexCoordinate().set(+1.0,+1.0,1.0);
       }

        return res;
    }  // end of processPajekFile

}// eo FileInput class


    

