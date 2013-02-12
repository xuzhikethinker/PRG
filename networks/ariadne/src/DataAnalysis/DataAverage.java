/*
 * DataAverage.java
 *
 * Created on Tuesday, April 27, 2004 at 16:55
 * Updated 5th September 2004
 */

package DataAnalysis;

/**
 *
 * @author  time
 */

//import cern.colt.list.DoubleArrayList;
//import cern.colt.list.IntArrayList;

import java.io.*;
import java.util.Date;

import JavaNotes.TextReader;



public class DataAverage {
            // Input parameters
            String DAVersion="DataAverage050601";
            String SEP="\t";
            String nameroot;
            String dirname;
            String inputFileExtension;
            
            // This array indicates where the x axis is [0], then rest
            // have the columns with the y data [1], [2] etc
            int MaxColumnsToBeRead;
            int [] columnsToBeRead; // = new int[MaxColumnsToBeRead];
            int numberColumnsToBeRead;
            String [] columnLabel;
            String labelLine;
            int totalNumbersOfDataLines;
            
            int MaxNumberValues;
            int [] xValue; // stores xvalues
            StatisticalQuantity [][] yValue; // stores y values
            
            
            
            // old parameters
            int infolevel;
            int outputcontrol;
            // Internal paramters
            String[] filelist;
            String[] filenamerootlist;
            
            
    /** Creates a new instance of DataAverage */
    public DataAverage() {
                dirname= "/PRG/networks/DataAnalysis/output/";
                nameroot = "test";
                inputFileExtension = ".tdata.dat";
                labelLine="#t";
                infolevel = 2;
                outputcontrol = 255;
                
                MaxColumnsToBeRead=20;
                columnsToBeRead = new int[MaxColumnsToBeRead];
                columnsToBeRead[0]=0;
                numberColumnsToBeRead=1;
                columnLabel = new String [MaxColumnsToBeRead] ;
                MaxNumberValues=1000;
                //xValue = new int[MaxNumberValues]; // stores xvalues
                // yValue = new StatisticalQuantity [MaxNumberValues][MaxColumnsToBeRead]; // stores y values
                totalNumbersOfDataLines=-1;
            
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DataAverage wa = new DataAverage();
        int res = wa.ParamParse(args);
        wa.printParameters();
        if (res!=0) return;
        wa.processAll("#" );
        wa.FileOutputParameters("#"); 
        wa.FileOutputTotalData("#"); 
        System.out.println("**** Finished Data Average ***\n");
    }// eo main
    
    




    

// ******************************************************************    
// File Output methods    
    
  // *******************************************************************
  /**
     * Outputs general information on files.
     *  <dirname><nameroot>_info.dat general info
     * @param cc comment characters put at the start of every line
     */
    void FileOutputParameters(String cc)  {

        String filename = dirname+ nameroot +"_info.dat";
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filename);
            PS = new PrintStream(fout);
            Date date = new Date();
            printParameters(PS);
            
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filename);
            return;
        }
    }

// ----------------------------------------------------------------------

     /**
     * Outputs total  data
     *  <filenameroot><inputFileExtension>.TOT.dat general info
     * @param cc comment characters put at the start of every line
     */
    void FileOutputTotalData(String cc)
                            {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        String typeext = inputFileExtension;
        int dotat = inputFileExtension.lastIndexOf('.');
        if (dotat>=0) typeext = inputFileExtension.substring(0,dotat);
        String filenamefull = dirname + nameroot+typeext+".TOT.dat";
        try {
            fout = new FileOutputStream(filenamefull);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing averaged data to "+SEP+filenamefull);
            Date date = new Date();
            PS.println(cc+" Averaged data for "+SEP+filelist.length+SEP+" files of "+SEP+nameroot+"*"+inputFileExtension+SEP+DAVersion+SEP+date+" ");
            PS.print(columnLabel[0]+SEP);
            for (int c=1; c<numberColumnsToBeRead; c++) PS.print(columnLabel[c]+" Av"+SEP+columnLabel[c]+" Sigma"+SEP+columnLabel[c]+" min"+SEP+columnLabel[c]+" max"+SEP);
            PS.println();
            for (int l=0; l<totalNumbersOfDataLines; l++)
            {
                PS.print(xValue[l]+SEP);
                for (int c=1; c<numberColumnsToBeRead; c++) PS.print(yValue[l][c-1].getAverage()+SEP+yValue[l][c-1].getSigma()+SEP+yValue[l][c-1].minimum+SEP+yValue[l][c-1].maximum+SEP);
                PS.println();
            }
            
            try{fout.close();
                } catch (IOException e) { System.out.println("File Close Error "+filenamefull);}
            } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filenamefull);
        } //eo catch
        
            
      } //eo FileOutputTotDD
    



    
// ----------------------------------------------------------------------

    
/**
 *  Filter to find only one type of file.
 *  Lists files in directory dirname of form <nameroot>*<ext>.
 *  See Schildt p544.
 *@param extension of files to be combined.
 */
    public void getFileList(String ext) {
            // next part Schildt p544
        File dir = new File(dirname);
        if (!dir.isDirectory())
        {System.out.println(dirname+" not a directory");
         return;
        }
        if (infolevel>0) System.out.println("Looking at directory "+dirname);

        FilenameFilter only = new OnlyOneParamSet(nameroot,ext);
        filelist = dir.list(only);
        if (infolevel>0) System.out.println("Found  "+filelist.length+" files with extension "+ext);
        filenamerootlist = new String[filelist.length];
        for (int i =0; i<filelist.length; i++)
        {   filenamerootlist[i] =  getFileNameRoot(filelist[i], ext);
            if (infolevel>0) System.out.println(filelist[i]+"\t "+filenamerootlist[i]);
            
        };
            

    }

// ----------------------------------------------------------------------
/**
 *  Method of DataAverage
 *  Filter to find only one type of file
 *  See Schildt p544
 */
    public String getFileNameRoot(String filename, String ext){
        int i = filename.lastIndexOf(ext);
        if (i<0) return null;
        return filename.substring(0,i);
              
    }
    
    
    
 
    // ----------------------------------------------------------------------
        /** 
         * Reads in data from file.
         * Reads in data from file filename.
         * Ignores lines starting with cc
         * and assuming columnnumber columns after that
         * The first is the index (degree) and then we choose column
         * columnnumber for the data.
         * @param String filename name of file to read knk data
         * @param String used for comments, first character used only
         * @param true if this is the first file to be read in
         */
    public int getData(String filename, String cc, boolean firstFile) {
        
        String fullfilename = dirname+filename;
        if (infolevel>1) System.out.println("Starting to read from " + fullfilename);
        TextReader data;     // Character input stream for reading data.
        int numberDataColumns=100;
        String [] word = new String [numberDataColumns];
        int res=0;  // error code.
        
        if (firstFile) {
            xValue = new int[MaxNumberValues]; // stores xvalues
            yValue = new StatisticalQuantity [MaxNumberValues][numberColumnsToBeRead-1]; // stores y values
        }
        try {  // Create the input stream.
            data = new TextReader(new FileReader(fullfilename));
        } catch (FileNotFoundException e) {
            System.out.println("Can't find file "+filename);
            return 1;
        }
        
        int c,x;
        double y;
        try {
            // Read the data from the input file.
            int linenumber=0;
            int dataEntry=0;
            int colno=0;
            // remaining lines are data or comments
            while (data.eof() == false) {  // Read until end-of-file.
                linenumber++;
                for (colno=0; data.eoln()==false ; colno++) word[colno] = data.getWord();
                if (colno==0) continue; // no data on this line
                if (word[0].startsWith(labelLine)) {          // line is header
                    if (firstFile) 
                    {
                        columnLabel[0]=word[0];
                        for (int n=1; n<numberColumnsToBeRead; n++) {
                        c =columnsToBeRead[n];
                        if (c>=colno) printWarning(" in getData too few columns in label line at line "+linenumber+", colno="+colno+", c="+c);
                        columnLabel[n] = word[c];
                        }
                        System.out.println(" --- Label line is line number "+linenumber);
                    }
                    continue;
                }
                if (word[0].startsWith(cc)) continue; // comment line
                if (colno>=numberDataColumns) printWarning(" in getData too many columns at line "+linenumber+", colno="+colno+", numberDataColumns="+numberDataColumns);
                
                
                
                // read next line of data as words
                if (colno>=numberDataColumns) printWarning(" in getData too many columns at line "+linenumber+", colno="+colno+", numberDataColumns="+numberDataColumns);
                x = Integer.parseInt(word[columnsToBeRead[0]]);
                if (firstFile) xValue[dataEntry]=x;
                else if (xValue[dataEntry]!=x) {
                    printError(" in getData wrong value of x line "+linenumber+" data entry number "+dataEntry+", found "+x+" should be "+xValue[dataEntry]);
                    return 3;
                }
                
                for (int n=1; n<numberColumnsToBeRead; n++) {
                    c =columnsToBeRead[n];
                    if (c>=colno) printWarning(" in getData too few columns at line "+linenumber+", colno="+colno+", c="+c);
                    y = Double.parseDouble(word[c]);
                    if (firstFile) {
                        yValue[dataEntry][n-1] = new StatisticalQuantity();
                        yValue[dataEntry][n-1].add(y);
                    } else yValue[dataEntry][n-1].add(y);
                }
                dataEntry++;
            }//eofile
            if (firstFile) {
                totalNumbersOfDataLines=dataEntry;
                System.out.println("Read "+totalNumbersOfDataLines+" lines of data");
            }
            if (totalNumbersOfDataLines!=dataEntry) printError(" in getData found "+dataEntry+" data lines but wanted "+ totalNumbersOfDataLines+" lines of data");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the data from the input file.
            if (infolevel>0) System.out.println("Input Error: " + e.getMessage());
            res=2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }
        return res;
    }  // end of getData() method
    
  
         
        
// **************************************************************************    
    /**
     * Method of DataAverage
     * @param args the command line arguments
     */
    public int ParamParse(String[] ArgList){
                for (int i=0;i< ArgList.length ;i++){
                    if (infolevel>0) System.out.println("Parameter "+i+" is "+ArgList[i]);
                        if (ArgList[i].length() <2) {
                        System.out.println("\n*** Argument "+i+" is too short");
                        return 1;};
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** Argument "+i+" does not start with -");
                            return 2;};
                            switch (ArgList[i].charAt(1)) {
                                case 'c': { if (ArgList[i].charAt(2)=='x') columnsToBeRead[0] = Integer.parseInt(ArgList[i].substring(3));                                
                                            if (ArgList[i].charAt(2)=='y') columnsToBeRead[numberColumnsToBeRead++] = Integer.parseInt(ArgList[i].substring(3));                                
                                break;}
                                case 'd': {dirname = ArgList[i].substring(2);
                                break;}
                                case 'e': {inputFileExtension = ArgList[i].substring(2);
                                break;}
                                case 'f': {nameroot = ArgList[i].substring(2);
                                break;}
                                case 'l': {labelLine = ArgList[i].substring(2);
                                break;}
                                case 'o': {outputcontrol = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'x': {MaxNumberValues = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                default:{
                                    System.out.println("\n*** Argument "+i+" not known, usage:");
                                    Usage();
                                    return 3;
                                }

                            }
                }
                File d = new File (dirname);
                if (!d.isDirectory()) {
                        System.out.println(dirname+" is not a directory");
                        return 1;};
//                int res = setSubDir();
            return 0;
            } // eo ParamParse

// **************************************************************
     /**
     * Processes all the files of types requested.
     * filenameroot basis of name of file as string
     * @param cc comment characters put at the start of every line
     */
     void processAll(String cc)  {
         getFileList(inputFileExtension);
         if (filelist.length==0) {System.out.println(" *** No "+inputFileExtension+" files found"); return;};
         for (int fn =0; fn<filelist.length; fn++){
            String fname=filelist[fn];
            String fnroot = filenamerootlist[fn];
            if (getData(fname,cc, (fn==0)) !=0) continue;
        }
}






 
     
        
   
    
        
// ----------------------------------------------------------------------
            public void Usage(){
                DataAverage d = new DataAverage();
                System.out.println("...............................................................................");
                System.out.println("Usage: ");
                System.out.println("DataAverage <options> ");
                System.out.println(" where options are -<char><value> separated by space as follows ");
                System.out.println("  -f<nameroot>       Sets root of input and output files to be nameroot, default "+d.nameroot);
                System.out.println("  -d<dirname>        Sets directory name, default "+d.dirname);
//                System.out.println("  -s<subdirroot>     Sets subdirectory name, default "+d.subdirroot);
                System.out.println("  -e<inputFileExtension>     Extension of input files, default "+d.inputFileExtension);
                System.out.println("  -cx#                Choose column # of x data, only one accepted. ");
                System.out.println("  -cy#                Choose column # of y data, may repeat many times. ");
                System.out.println("                      *** note columns are numbered starting from 0. ");
//                System.out.println("  -r#                Ratio of upper to lower bin positions, default "+d.lbratio);
//                System.out.println("  -n<y|n>            Normalise n(k) to p(k), yes or no, default "+(d.NormaliseDD?"yes":"no"));
                System.out.println("  -x#                Maximum number of data lines, default "+d.MaxNumberValues);
//                System.out.println(" -o<int> output modes , default "+d.outputcontrol);
//                System.out.println("  o modes: (o& 1) ? Degree distribution analysis on : (off)");
//                System.out.println("         : (o& 2) ? Distance statistics on : (off)");
                System.out.println("...............................................................................");

            } //eo usage

            // Print out parameters in param class
public void printParameters(){
    printParameters(System.out);
} //eo print

            // Print out parameters in param class
public void printParameters(PrintStream PS){
                Date date = new Date();
                PS.println("\n------------------------------------------------------- "+DAVersion+SEP+date);
                PS.println("                        Filename root: "+SEP+nameroot);
                PS.println("                       Directory name: "+SEP+dirname);
                PS.println("                 Input file extension: "+SEP+inputFileExtension);
                PS.println("                        Lines of Data: "+SEP+totalNumbersOfDataLines);
                PS.println("                   Columns of of Data: "+SEP+numberColumnsToBeRead);
                  PS.print("                      Data in columns: "+SEP);
                for (int n=0; n<numberColumnsToBeRead; n++) PS.print(columnsToBeRead[n]+SEP);
                PS.println();
                PS.println  ("-------------------------------------------------------");
            } //eo print

           public void listdir(){
            File dir = new File(dirname);
            if (dir.isDirectory()) {
                String dirlist[] = dir.list();
                for (int j=0; j<dirlist.length; j++){
                System.out.println(dirlist[j]);}
            };
           }

           
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
           
           
/**
 *  Method of DataAverage
 *  Filter to find only one type of file
 *  See Schildt p544
 */
    public class OnlyOneParamSet implements FilenameFilter{
          String ext;
          String header;

          // constructor;
          public OnlyOneParamSet (String header, String ext){
           this.ext=ext;
           this.header=header;
           }

          public boolean accept(File dir, String name){
           return ( (name.endsWith(ext)) & (name.startsWith(header)));
          }

    } // eo OnlyOneSet





public class OneParamStatistics {
    
    double minimum;
    double maximum;
    double average;
    double error;
    double sigma;
    double sigma2;
    double total;
    double totalsquares;
    int number;
    
    /** Creates a new instance of OneParamStatistics **/
    public OneParamStatistics() {
        minimum=9999999;
        maximum=0;
        average=0;
        error=0;
        sigma=0;
        sigma2=0;
        total=0;
        totalsquares=0;
        number=0;
    }

// ----------------------------------------------------------------------
    /**
     *  Method of OneParamStatistics
     * Updates statistics
     * @param 
     *
     */
     public void update(double newvalue)
     {
        number++;
        total+=newvalue;
        totalsquares+=newvalue*newvalue;
        if (newvalue<minimum) minimum=newvalue;
        if (newvalue>maximum) maximum=newvalue;
        average = total/((double) number);
        sigma2 = (totalsquares/((double) number)  -average*average);
        if (sigma2>0) sigma=Math.sqrt( (totalsquares/((double) number)  -average*average)  );
        else sigma=0.0;
        if (number>1) error = sigma/Math.sqrt((double)(number-1));
        else error =0;        
     }
    
// ----------------------------------------------------------------------
    /**
     *  Method of OneParamStatistics
     * Outputs statistics to screen
     * @param oneparamname name of the parameter as string 
     *
     */
     public void print(String oneparamname)
     {
        System.out.println(minimum+" <= "+oneparamname+" <= "+maximum);
        System.out.println("<"+oneparamname+"> = "+average+" +/- "+error);
        System.out.println("           sigma = "+sigma+", samples = "+number);
     }
    
    



// PS = new PrintStream(fout);
// ----------------------------------------------------------------------
    /**
     *  Method of OneParamStatistics
     * Outputs statistics to printstream
     * @param PS printstream for output 
     * @param oneparamname name of the parameter as string 
     *
     */
     public void print(PrintStream PS, String oneparamname)
     {
        PS.println(minimum+" <= "+oneparamname+" <= "+maximum);
        PS.println("<"+oneparamname+"> = "+average+" +/- "+error);
        PS.println("           sigma = "+sigma+", samples = "+number);
     }
    
// ----------------------------------------------------------------------
    /**
     *  Method of OneParamStatistics
     * Outputs statistics to printstream
     * @param PS printstream for output 
     * @param oneparamname name of the parameter as string 
     *
     */
     public void printExcel(PrintStream PS, String oneparamname)
     {
        PS.print(" \t"+oneparamname+" \t"+ average+" \t"+error + " \t"+sigma+" \t"+ minimum+" \t "+maximum+" \t"+number);
     }
    
    
} //eo class OneParamStatistics
            
        
}//eo  DataAverage
