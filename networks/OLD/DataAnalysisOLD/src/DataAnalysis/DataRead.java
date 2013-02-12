/*
 * DataRead.java
 *
 * Created on 06 June 2006, 17:14
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package DataAnalysis;

import java.io.*;
//import java.util.Date;

import JavaNotes.TextReader;
import cern.colt.list.DoubleArrayList;



/**
 *
 * @author time
 */
public class DataRead {
    
    int infolevel=0;
    int numberDataColumns=0; // number of columns in total
    int totalNumbersOfDataLines=0; // number of data entries
    
    DoubleArrayList [] columnData;
    double [] columnMin;
    double [] columnMax;    
    String [] columnLabel;

            
    /** Creates a new instance of DataRead */
    public DataRead() {
    }
    
    // ----------------------------------------------------------------------
        /** 
         * Reads in data from file.
         * Reads in data from file filename.
         * Ignores lines starting with cc
         * and assuming columnnumber columns after that
         * The first is the index (degree) and then we choose column
         * columnnumber for the data.
         * @param String filename name of file to read data
         * @param String dirname name of directory to read data
         * @param String used for comments, first character used only
         * @param String that tsarts the line of column labels
         * @param columnsToBeRead array of integers with the numbers of the columns to be read, count from 0
         * @param numberColumnsToBeRead is the number of columns to be read (not the total number of columns)
         */
    public int getData(String filename, String dirname, String cc,  String labelLine, int numberColumnsToBeRead, int [] columnsToBeRead) 
    {
        
        String fullfilename = dirname+filename;
        if (infolevel>1) System.out.println("Starting to read from " + fullfilename);
        TextReader data;     // Character input stream for reading data.
        int maxDataColumns=100;
        String [] word = new String [maxDataColumns];
        columnData  = new DoubleArrayList[numberColumnsToBeRead];
        columnMin  = new double [numberColumnsToBeRead];
        columnMax  = new double [numberColumnsToBeRead];
        for (int c=0; c<numberColumnsToBeRead; c++) {columnMin[c]=1e98;columnMax[c]=-columnMin[c];}
        
        columnLabel  = new String[numberColumnsToBeRead];
        int res=0;  // error code.
       
        try {  // Create the input stream.
            data = new TextReader(new FileReader(fullfilename));
        } catch (FileNotFoundException e) {
            System.out.println("Can't find file "+filename);
            return 1;
        }
        
        int c;
        double x;
        try {
            // Read the data from the input file.
            int linenumber=0;
            int dataEntry=0;
            int colno=0;
            numberDataColumns=0;
            boolean foundLabelLine =false;
            // remaining lines are data or comments
            while (data.eof() == false) {  // Read until end-of-file.
                linenumber++;
                // read in data as strings
                for (colno=0; data.eoln()==false ; colno++) word[colno] = data.getWord();
                
                if (colno==0) continue; // no data on this line
                if (word[0].startsWith(labelLine)) {          // line is header
                        numberDataColumns=colno;
                        for (int n=0; n<numberColumnsToBeRead; n++) 
                        {
                        c =columnsToBeRead[n];
                        if (c>=colno) printWarning(" in getData too few columns in label line at line "+linenumber+", colno="+colno+", c="+c);
                        columnLabel[n] = word[c];
                        columnData[n] = new DoubleArrayList();
                        }
                        System.out.println(" --- Label line is line number "+linenumber);
                    foundLabelLine=true;
                    continue;
                }// header line
                
                if ((!foundLabelLine) || (word[0].startsWith(cc)) ) continue; // comment line
                if (colno>=numberDataColumns) printWarning(" in getData too many columns at line "+linenumber+", colno="+colno+", numberDataColumns="+numberDataColumns);
                
                
                
                // read next line of data as words
                if (colno>=numberDataColumns) printWarning(" in getData too many columns at line "+linenumber+", colno="+colno+", numberDataColumns="+numberDataColumns);
                for (int n=0; n<numberColumnsToBeRead; n++) {
                    c =columnsToBeRead[n];
                    if (c>=colno) printWarning(" in getData too few columns at line "+linenumber+", colno="+colno+", c="+c);
                    String value =word[c];
                    x = Double.parseDouble(value);
                    columnData[n].add(x);
                    columnMax[n] = Math.max(x, columnMax[n]);
                    columnMin[n] = Math.min(x, columnMin[n]);
                }
                dataEntry++;
            }//eofile
            
            totalNumbersOfDataLines=dataEntry;
            System.out.println("Read "+totalNumbersOfDataLines+" lines of data");
            
            }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the data from the input file.
            if (infolevel>0) System.out.println("Input Error: " + e.getMessage());
            res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }
        res = totalNumbersOfDataLines;
        return res;
    }  // end of getData() method
    
  
           /**
            *  Outputs total numbers of data lines.
            *@return total numbers of data lines.
            */           
           public int getTotalNumbersOfDataLines() {
               return totalNumbersOfDataLines;
           }
           
           /**
            *  Outputs a warning.
            *@param warning message string.
            */           
 /**
            *  Outputs a error message.
            *@param warning message string.
            */           
           public void printError(String s) {
               printLine(System.out,"*** "+s);
           }
           /**
            *  Outputs a warning.
            *@param warning message string.
            */           
 
           public void printWarning(String s) {
               if (infolevel>0) printLine(System.out,"--- "+s);
           }
     
           /**
            *  Outputs a line to a printstream.
            *@param the print stream e.g. System.out for screen
            *@param warning message string.
            */
           
           public void printLine(PrintStream PS, String s) {
               PS.println(s);
           }
    
} // eo class DataRead
