/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import JavaNotes.TextReader;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeSet;


/**
 * Routines which act directly on edge list files.
 * @author time
 */
public class EdgeListTransformer {

     public static void main(String[] args) {
         
         System.out.println("EdgeListTransformer Arguments: fullInputFileName fullOutputFileName columnWeight weightCut");
         
         String fullInputFileName=args[0];
         String fullOutputFileName=args[1];
         int columnWeight=Integer.parseInt(args[2]);
         double weightCut=Double.parseDouble(args[3]);
                  
         String cc="";
         String sep="\t";
         boolean infoOn=true;
         
       if (infoOn) {
                     System.out.println("\n !!! EdgeListTransformer !!! Input file = "+fullInputFileName+", output file = "+fullOutputFileName);
                     System.out.println("Weights in column "+columnWeight+", minimum weight "+ weightCut);
                     System.out.println(((cc.length()==0)?"No comment lines":"Comment lines start with" +cc));
        }
         
        edgeListPercolationFile( fullInputFileName, fullOutputFileName, cc, sep, columnWeight, weightCut, infoOn); 

     }
    
    // ***************************************************************************
        /**
         * Copies a file of edges but removes line with weakest weights.
         * Reads in a file (typically an edge list) and outputs a copy except all lines
         * with weights below <tt>weightCut</tt> are removed.
         * <p>Each line has entries separated by whitespace.
         * Comment lines are always copied.
         * @param fullInputFileName full filename of input file with data
         * @param fullOutputFileName full filename of output file
         * @param cc if, after any white space, first word starts with this string then line is treated as a comment line.
         * @param sep sepation character, e.g. tab
         * @param columnWeight column with weight, counting first column as number 1.  
         * @param weightCut lines with weights below this value are not written to output file.
         * @param infoOn
         */
    static public int edgeListPercolationFile(String fullInputFileName, String fullOutputFileName, String cc, String sep, int columnWeight, double weightCut, boolean infoOn)
    {
        int res=0;  // error code.
        boolean dontTestForCommentLine=false;
        if (cc.length()==0) dontTestForCommentLine=true;

        if (columnWeight < 1) throw new RuntimeException("*** column with weights must be positive (first column numbered 1), found "+columnWeight);

        //set up input file
        TextReader data=FileInput.openFile(fullInputFileName);
        if (data==null) {
             System.err.println("*** edgeListPercolationFile input file " + fullInputFileName+" not opened");
             return -1;
        }
        if (infoOn) System.out.println("Starting to do edge percolation on edge list file " + fullInputFileName);

        //set up output file
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fullOutputFileName);
            PS = new PrintStream(fout);
            
        } catch (FileNotFoundException e) {
            System.err.println("**** edgeListPercolationFile output file " + fullOutputFileName + " not opened, " + e.getMessage());
            return -2;
        }
        

        try {
            // Read the data from the input file.
            //String line;
            //int first;
            //int n;
            int linenumber=0;
            int numberWeightsInput=0;
            int numberWeightsOutput=0;
            //int nmax=1000; // max no. columns
            String [] numbers = new String[1000];
            double weight=-97531;
            int column=0;
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
//                   if (infoLevel>2) System.out.println(linenumber+": ");
                   column=0;
                   while (data.eoln() == false)
                    {  // Read until end-of-line.
                       numbers[column++]=data.getWord();
                    } //eoln
                   
                   
//                if (infoLevel>1) {
//                    System.out.print(linenumber);
//                    for (int c=0; c<column; c++) System.out.print(", "+numbers.get(c));
//                    System.out.println();
//                }
                    try {
                     // next tests for first word starting for comment line string but only if this is a nontrivial dstring   
                     if (dontTestForCommentLine || !numbers[0].startsWith(cc))  {
                         if (columnWeight <= column) {
                                    numberWeightsInput++;
                                    weight = Double.parseDouble(numbers[columnWeight - 1]);
                                    if (weight<weightCut) continue;
                                    numberWeightsOutput++;
                                } 
                         else {
                                    res=-12;
                                    throw new RuntimeException("*** Too few columns on line " + linenumber + " expected weight in" + columnWeight + " found " + column);
                               }
                     }
                     // write line out to output file if a comment line or if weight >=weightCut
                     for (int c=0; c<column; c++) PS.print(numbers[c]+(c==column?"":sep)); 
                     PS.println();
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + " of input file, " + e.getMessage());
                    }

               }//eofile

            if (infoOn) {
                System.out.println("Finished edge list percolation from " + fullInputFileName+" to " + fullOutputFileName);
                System.out.println("    "+linenumber+" input lines found "+numberWeightsInput+" lines with weight, wrote "+numberWeightsOutput+" lines with weight.");
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
                
        } 
        
                



        return res;
    }

    // ***************************************************************************
        /**
         * Processes an edge list for an undirected graph where edges are given in both directions.
         * <P>The case where the adjacency matrix of non-zero values for an undirected graph
         * has been given as an edge list.  The conventioanl routines assume
         * an undirected edge is given only once.
         * @param fullInputFileName full filename of input file with data
         * @param fullOutputFileName full filename of output file
         * @param cc if, after any white space, first word starts with this string then line is treated as a comment line.
         * @param sep separation character, e.g. tab
         * @param columnSource column with source, first column is number 1
         * @param columnTarget column with target, counting from 1
         * @param columnWeight column with weight, counting from 1.  If negative no weights read
         * @param columnLabel column with edge label, counting from 1.  If negative no edge labels read.
         * @param headerOn if true skip first line (its a header)
         * @param infoOn
         */
    static public void processEdgeListTableFile(String fullInputFileName, String fullOutputFileName, String cc, String sep, 
            int columnSource, int columnTarget, int columnWeight, int columnLabel,        
            boolean headerOn, boolean infoOn)
    {
//        timgraph tg
//        FileInput fi = new FileInput((infoOn?2:0));
//        fi.readStringEdgeFile(fullInputFileName,
//        fi.processStringEdgeFile(tg, ext,
//            int columnSource, int columnTarget, int columnWeight,  int columnLabel,
//            boolean directed, boolean vertexLabelled )
//

//        TreeSet<String> sourceLL = new TreeSet();
//        TreeSet<String> targetLL = null; // assumed to be symmetric
//        ArrayList<String> edgeLL = new ArrayList();
//        DoubleArrayList weightLL;
//        if (columnWeight>0) weightLL = new DoubleArrayList(); else weightLL=null;
//        IntArrayList labelLL;
//        if (columnLabel>0) labelLL = new IntArrayList(); else labelLL=null;
//
//        FileInput fi = new FileInput((infoOn?2:0));
//        fi.readStringEdgeFile(fullInputFileName,
//            columnSource, columnTarget, columnWeight, columnLabel,
//            sourceLL, targetLL, edgeLL,
//            weightLL, labelLL,
//            headerOn);
        
    }
    
}
