/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TimGraph.run;

import JavaNotes.TextReader;
import TimGraph.io.FileInput;
import TimGraph.timgraph;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author time
 */
public class FilterEdgesUsingVertexList {
    
    /**
     * List of names in file.
     * <tt>nameList[i]</tt> is name of vertex index <tt>indexList[i]</tt>
     */

    
    public static void main(String[] args) 
    {
//      String vertexListFileName="input/UKwards/TestCommutingFlowinputVertices.dat";
//      String edgeListInputFileName="input/UKwards/TestCommutingFlowinputELS.dat";
//      String edgeListOutputFileName="output/UKwards/TestCommutingFlow";
      String vertexListFileName="input/UKwards/UKCensus01EWCommutingFlowinputVertices.dat";
      String edgeListInputFileName="input/UKwards/UKCensus01CommutingFlowinputELS.dat";
      String edgeListOutputFileName="output/UKwards/UKCensus01EWCommutingFlow";
      String cc=timgraph.COMMENTCHARACTER;
      boolean headerOn=true;
      boolean infoOn=false;
      TreeSet<String> nameSet;
      boolean forceLowerCase=false;
      nameSet = FileInput.getVertexNamesFromFile(vertexListFileName,cc,headerOn,infoOn);
      String sep=timgraph.SEP;
      filterEdgeListStrings(edgeListInputFileName, edgeListOutputFileName, nameSet,
            cc, sep, forceLowerCase, infoOn);

         
    }
    
    
    
         /**
         * Filters an edge list of strings given a list of acceptable vertices.                                                                                                                                                                                               
         */
    public static void filterEdgeListStrings(String inputFile, 
            String outputRootName, Set nameSet,
            String cc, String sep,
            boolean forceLowerCase, boolean infoOn)
    {
        int res=0;  // error code.
        TextReader datafile=FileInput.openFile(inputFile);
        if (datafile==null) throw new RuntimeException("Failed to open " + inputFile);

        
        // see if need to find header row
        boolean findHeader=false;
        
        boolean testForComments=true;
        if (cc.length()<1) testForComments=false;
        
        int linenumber=0; // First line will be line 1
            int numberCommentLines=0; // Numberof comment Lines
            int numberHeaderLines=0; // Numberof comment Lines
            int numberRejectedLines=0; // Numberof comment Lines
            int numberAcceptedLines=0; // Numberof comment Lines
            
        TreeSet<String> rejectedVertexList= new TreeSet();    
        TreeSet<String> acceptedVertexList= new TreeSet();    
        PrintStream PS;
        FileOutputStream fout;
        String outputFile=outputRootName+"outputELS.dat";
        try {
            fout = new FileOutputStream(outputFile);
            PS = new PrintStream(fout);
        
            // Read the noNamedata from the input file.
            String source;
            ArrayList<String> rowData = new ArrayList(); //String [nmax];
            while (datafile.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   //if (infoOn) System.out.println(linenumber+": ");
                   // read each numberColumns into array of strings
                   int numberColumns=0;
                   rowData.clear();
                   while (datafile.eoln() == false) rowData.add(datafile.getWord());
                   numberColumns=rowData.size();
                   if (infoOn) {
                        System.out.print(linenumber);
                        for (int c=0; c<numberColumns; c++) System.out.print((c==0?": ":", ")+rowData.get(c));
                        System.out.println();
                   }
                  // try processing one line
                   try{
                     // skip comment lines  
                     if (testForComments && (rowData.get(0).startsWith(cc))) {
                       numberCommentLines++;
                       continue; 
                     }

                     if(findHeader){
                         System.out.println("Header row on line "+linenumber);
                         numberHeaderLines++;
                         findHeader=false;
                         continue;
                       }

                     if (numberColumns <2) {
                         res = -10;
                         numberRejectedLines++;
                         //columnList.add("TooFewColumnsOnLine" +linenumber);
                         System.err.println("!!! Rejected line " + linenumber + " too few columns, found " + numberColumns);
                         continue;
                     }
                     
                     boolean reject=false;
                     if (!nameSet.contains(rowData.get(0))) {
                         reject=true;
                         rejectedVertexList.add(rowData.get(0));
                         if (infoOn) System.err.println("!!! Rejected line " + linenumber + ", "+ rowData.get(0)+ " not in vertex set");
                     }
                     if (!nameSet.contains(rowData.get(1))) {
                         reject=true;
                         rejectedVertexList.add(rowData.get(1));
                         if (infoOn) System.err.println("!!! Rejected line " + linenumber + ", "+ rowData.get(1)+ " not in vertex set"); 
                     }

                     if (reject){
                         numberRejectedLines++;
                         continue;
                     }
                     // accept line
                     numberAcceptedLines++;
                     acceptedVertexList.add(rowData.get(0));
                     acceptedVertexList.add(rowData.get(1));
                     boolean firstColumn=true;
                     for (String s: rowData){
                         PS.print((firstColumn?"":sep)+s);
                         firstColumn=false;
                     }// eo for s
                     PS.println();
                    }// eo try processing one line
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                    }
                    } // eo while, finished reading all lines

            if (res==0) res =linenumber;
            System.out.println("Finished reading "+linenumber+" rows of file " + inputFile);
            System.out.println(numberHeaderLines+" header lines");
            System.out.println(numberCommentLines+" comment lines");
            System.out.println("Accepted "+numberAcceptedLines+" lines, and "+acceptedVertexList.size()+" distinct vertex labels");
            System.out.println("Rejected "+numberRejectedLines+" lines, and "+rejectedVertexList.size()+" distinct vertex labels");
            System.out.println("Number of acceptable vertices was "+nameSet.size());
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
            } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+outputFile+", "+e.getMessage());
            }
            catch (TextReader.Error e) {
                res=-2;
                throw new RuntimeException("*** Input Error: " + e.getMessage());
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            datafile.close();
        }

        TimGraph.io.FileOutput.Collection(acceptedVertexList, 
                outputRootName+"acceptedVertices.dat", null, sep, infoOn);    
            // output rejected filelist
        TimGraph.io.FileOutput.Collection(rejectedVertexList, 
                outputRootName+"rejectedVertices.dat", null, sep, infoOn); 
        
    }

   

}
