/*
 * NetworkReader.java
 *
 * Created on 05 February 2007, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimGraph;

import java.io.*;

import JavaNotes.TextReader;  // provides free routines to read in numbers etc from data files.

/**
 *
 * @author time
 */
public class NetworkReader {
    
    int [][] intMatrix;
    int maximumDimension; // maximum dimension of matrix
    String filename;
        
    
    /** Creates a new instance of NetworkReader */
    public NetworkReader() {
        maximumDimension=1010;
        filename ="/PRG/networks/timgraph/input/ERn100k4.0r0tgadjmat.dat";
    }

   /** Creates a new instance of NetworkReader.
    * @param fName filename
    *@param maxDim maximum size of matrix
    */
    public NetworkReader(String fName, int maxDim) {
        maximumDimension=maxDim;
        filename=fName;
    }

    /**
     * Example of use of routines.
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        NetworkReader nr = new NetworkReader();
        int result = nr.readSquareIntegerMatrix();
        if (result<0) {System.out.println("*** ERROR readSquareIntegerMatrix returned "+result); return;}
        nr.printOutStatistics();
    }
    
    /**
     * Prints out basic statistics.
     */
    public void printOutStatistics() 
    {
        int edges=0;
        double secondMoment=0;
        int vertices =intMatrix.length;
        int e=-1;
        for (int i=0; i<vertices; i++) for (int j=0; j<vertices; j++)  
        {
            e = intMatrix[i][j];
            if (e==0) continue;
            edges+=e;
            secondMoment+=e*e;
        }
        System.out.println(" Network in file "+filename+" has "+vertices+" vertices, "+edges+" edges");
        double averageDegree = (((double) edges)/((double)vertices));
        secondMoment = secondMoment/vertices;
        System.out.println("     average degree "+averageDegree+", second moment "+secondMoment);
    }
    
    
    /** 
     * Reads in a square matrix of integers from file and sets it equal to intMatrix[][].
     *@return if it returns a negative number then an error occured
     */
    public int readSquareIntegerMatrix() {

        
      System.out.println("Starting to read double matrix data from " + filename);
      TextReader data;     // Character input stream for reading data.
      
      int[] number = new int[maximumDimension];  // An array to hold one line of dataall
                                           //   the numbers that are
                                           //   read from the file.

      int dimension;  // dimension of the matrix.
      int line=1; // line of file being read
      
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         System.out.println("Can't find file "+filename);
         return -1;  
      }

      try {
      
          // Read the first line of data from the input file.
          
          dimension = 0;
          while (data.eoln() == false) {  // Read until end-of-line
              if (dimension==maximumDimension) { // stop if too many items
                  System.out.println("*** ERROR more than "+maximumDimension+" numbers of first line of file "+filename);
                  return -2;
              }
              number[dimension++] = data.getInt();
          }
          
          // now set up intMatrix and set its first row  equal to first line of data
          intMatrix = new int[dimension][dimension];
          for (int j=0; j<dimension; j++) intMatrix[0][j] = number[j];
          
          // now read in remaining (dimension-1) lines

          while (data.eof() == false) {  // Read until end-of-file.
              if (line==dimension) { // stop if file has another line yet already read enough lines
                  System.out.println("*** ERROR more than "+dimension+" lines of data in file "+filename);
                  return -3;
              } 
              int column=0;
              while (data.eoln() == false) {  // Read until end-of-file.
               if (column==dimension) { // stop if too many items on line
                  System.out.println("*** ERROR more than "+dimension+" numbers on line "+(line+1)+" of file "+filename);
                  return -4;
              } // end of if
               intMatrix[line][column++] = data.getInt();   
             }// end of while (data.eoln() 
             line++;             
             }// end of while (data.eof() 
          System.out.println("Finished reading from " + filename);
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          System.out.println("*** Input Error: " + e.getMessage());
          return -5;
       }
       catch (IndexOutOfBoundsException e) {
          // Must have tried to put too many numbers in the array.
          System.out.println("*** Too many numbers in data file"+filename);
          System.out.println("    Processing has been aborted.");
          return -6;
       }
       finally {
          // Finish by closing the files, 
          //     whatever else may have happened.
          data.close();
        }
       return 0;
    } // end of readSquareIntegerMatrix

    
}// end of NetworkReader file