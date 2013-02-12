/*
 * ParameterInput.java
 *
 * Created on 01 November 2006, 09:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

import JavaNotes.TextReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
 * Reads in parameters from a file.
 * Lines beginning with CC are treated as one line comments.
 * Lines starting with PARAM are treated as a white space separated list of parameters.  
 * Each white separated substring is put into the String array parameterList[] which 
 * can be used as a replacement for command line parameter list so 
 * its length gives the number of parameters.
 * @author time
 */
public class ParameterInput {
    
    final String VERSION = "FI061101";
    TimMessage message = new TimMessage(1); // infolevel 1 by default
    public String [] parameterList; // this contains the parameters found
    int maxNumberParameters=100;
    int numberParameters=0; // number of parameters
    
    Character CC='#'; // comment character
    Character PARAM='-'; // character for start of a parameter

    /**
     * Creates a new instance of ParameterInput with default values.
     */
    public ParameterInput() {
    }
 
    /**
     * Creates a new instance of ParameterInput with default values.
     */
    public ParameterInput(Character cc, Character param, int maximumNumberParameters, int infolevel) {
        CC=cc;
        PARAM=param;
        maximumNumberParameters = maxNumberParameters; 
        message = new TimMessage(infolevel);
    }


//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        // TODO code application logic here
//    }
    
   /** 
     * Reads in ranges of variables from file <filename>.
     * Lines starting with global CC are comments.
     * Lines starting with global PARAM are treated as a white space separated list of parameters.  
     * Each white separated substring is put into the String array plist[]
     * so the length gives the number of paramters.
     * @param filename full name of input file.
     */
    public int readParameters(String filename) 
    {
      
      message.println(0,"Starting to read ranges of parameters data from " + filename);
      TextReader data;     // Character input stream for reading data.
      String tempstring="";
      Character datatype;
      int linenumber=0;
      String [] plist = new String[maxNumberParameters];
      String param;
      int result=0;
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         message.printWarning(-1,"Can't find file "+filename);
         return 1;  
      }
         try{ 
          while (!data.eof()) 
          { // start reading new line
              linenumber++;
              datatype = data.peek(); //ignore first item, labels row
//            System.out.println("first word is "+datatype);
              if (datatype==CC)
              { // this line is a comment
                  tempstring=data.getln(); //comment read to end of line
                  message.println(2,linenumber+": Comment: "+tempstring);
                  continue;
              }
              if (datatype==PARAM)
              {
                  while (!data.eoln()) { // read to end of line
                      param= data.getWord(); 
                      message.println(2,linenumber+": Parameter "+numberParameters+" is "+param);
                      plist[numberParameters++] = param;
                  }
                  continue;
              }
              message.printERROR("Line "+linenumber+" of unknowntype");
              result = -1;
            }//eo while 
   
          message.println(0,"Finished reading from " + filename);
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          message.printERROR("Input Error: " + e.getMessage());
          result=1;
       }
       catch (IndexOutOfBoundsException e) {
          // Must have tried to put too many numbers in the array.
          message.printERROR("Too many parameters in file"+filename+", processing has been aborted.");
          result=2;
       }
       finally {
          // Finish by closing the files, 
          //     whatever else may have happened.
          data.close();
        }
       
       parameterList= new String[numberParameters];
       for (int i=0; i<numberParameters; i++) parameterList[i]=plist[i];
       return result;
    }  // end of getData() method



 
 
        }// eo ParameterInput class
