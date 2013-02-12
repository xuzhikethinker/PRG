/*
  This program assumes that a file named data.dat exists and contains
  a sequence of numbers.  It reads the numbers and then writes them
  in reverse order to a file named result.dat.  The data file can
  contain at most 1000 numbers.  If a file named result.dat already
  exists, it is overwritten with the new data.
  
  The TextReader class must be available to this program.
*/

import java.io.*;

public class ReverseFile {

   public static void main(String[] args) {

      TextReader data;     // Character input stream for reading data.
      PrintWriter result;  // Character output stream for writing data.
      
      double[] number = new double[1000];  // An array to hold all
                                           //   the numbers that are
                                           //   read from the file.

      int numberCt;  // Number of items actually stored in the array.
      
      try {  // Create the input stream.
         data = new TextReader(new FileReader("data.dat"));
      }
      catch (FileNotFoundException e) {
         System.out.println("Can't find file data.dat!");
         return;  // End the program by returning from main().
      }

      try {  // Create the output stream.
         result = new PrintWriter(new FileWriter("result.dat"));
      }
      catch (IOException e) {
         System.out.println("Can't open file result.dat!");
         System.out.println(e.toString());
         data.close();  // Close the input file.
         return;        // End the program.
      }
      
      try {
      
          // Read the data from the input file.
          
          numberCt = 0;
          while (data.eof() == false) {  // Read until end-of-file.
             number[numberCt] = data.getlnDouble();
             numberCt++;
          }
       
          // Output the numbers in reverse order.
          
          for (int i = numberCt-1; i >= 0; i--)
             result.println(number[i]);
             
          System.out.println("Done!");

       }
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          System.out.println("Input Error: " + e.getMessage());
       }
       catch (IndexOutOfBoundsException e) {
          // Must have tried to put too many numbers in the array.
          System.out.println("Too many numbers in data file.");
          System.out.println("Processing has been aborted.");
       }
       finally {
          // Finish by closing the files, 
          //     whatever else may have happened.
          data.close();
          result.close();
       }
         
   }  // end of main()

} // end of class
