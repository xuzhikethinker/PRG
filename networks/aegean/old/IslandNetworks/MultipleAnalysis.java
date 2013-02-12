/*
 * MultiplelAnalysis.java
 *
 * Created on 12 May 2006, 17:26
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package IslandNetworks;

import java.io.*;
import JavaNotes.TextReader;


/**
 *
 * @author time
 */
public class MultipleAnalysis 
{
    String SEP = "\t";
    int nruns;
    int nrunsmax;
    int nentries;
    double results[][];
    String firstLine;
    Range j;
    Range mu;
    Range kappa;
    Range lambda;
            

    
    /** Creates a new instance of MultipleAnalysis */
    public MultipleAnalysis() 
    {
            nrunsmax=1000;
            nentries=24;
            results = new double [nrunsmax][nentries];
            firstLine="";
            j=new Range();
            mu = new Range();
            lambda = new Range();
            kappa = new Range();
            
    }
    

     /**
     * Calculates networks for a range of parameter values.
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void calcRange(islandNetwork a) 
    {
            
        int nrunstotal = j.count()*mu.count()*kappa.count()*lambda.count();
        nruns=0;
        firstLine="j"+SEP+"mu"+SEP+"kappa"+SEP+"lambda";
        firstLine=firstLine+SEP+"SW min"+SEP+"SW max"+SEP+"SW av"+SEP+"SW sigma";
        firstLine=firstLine+SEP+"EW min"+SEP+"EW max"+SEP+"EW av"+SEP+"EW sigma";
        firstLine=firstLine+SEP+"SS min"+SEP+"SS max"+SEP+"SS av"+SEP+"SS sigma";
        firstLine=firstLine+SEP+"D1 min"+SEP+"D1 max"+SEP+"D1 av"+SEP+"D1 sigma";
        firstLine=firstLine+SEP+"D2 min"+SEP+"D2 max"+SEP+"D2 av"+SEP+"D2 sigma";
        double betaInitial=a.betaH;
        for (double jvalue=j.min; jvalue<=j.max; jvalue+=j.step ) {
            a.vertexSourceH=jvalue;
            
            for (double muvalue=mu.min; muvalue<=mu.max; muvalue+=mu.step ) {
                a.edgeSourceH=muvalue;
                
                for (double kappavalue=kappa.min; kappavalue<=kappa.max; kappavalue+=kappa.step ) {
                    a.kappaH=kappavalue;
                    
                    for (double lambdavalue=lambda.min; lambdavalue<=lambda.max; lambdavalue+=lambda.step ) {
                        a.lambdaH=lambdavalue;
                        
                        a.betaH=betaInitial;
                        a.doMC();
                                                
                        //a.calcNetworkStats();
                        int entry=0;
                        results[nruns][0]=a.vertexSourceH;
                        results[nruns][1]=a.edgeSourceH;
                        results[nruns][2]=a.kappaH;
                        results[nruns][3]=a.lambdaH;
                        results[nruns][4]=a.siteWeightStats.minimum;
                        results[nruns][5]=a.siteWeightStats.maximum;
                        results[nruns][6]=a.siteWeightStats.getAverage();
                        results[nruns][7]=a.siteWeightStats.getSigma();
                        results[nruns][8]=a.edgeWeightStats.minimum;
                        results[nruns][9]=a.edgeWeightStats.maximum;
                        results[nruns][10]=a.edgeWeightStats.getAverage();
                        results[nruns][11]=a.edgeWeightStats.getSigma();
                        results[nruns][12]=a.siteStrengthStats.minimum;
                        results[nruns][13]=a.siteStrengthStats.maximum;
                        results[nruns][14]=a.siteStrengthStats.getAverage();
                        results[nruns][15]=a.siteStrengthStats.getSigma();
                        entry=16;
                        a.metricNumber=1;
                        a.calcNetworkDistances();
                        results[nruns][entry++]=a.siteDistanceStats.minimum;
                        results[nruns][entry++]=a.siteDistanceStats.maximum;
                        results[nruns][entry++]=a.siteDistanceStats.getAverage();
                        results[nruns][entry++]=a.siteDistanceStats.getSigma();
                        a.metricNumber=2;
                        a.calcNetworkDistances();
                        results[nruns][entry++]=a.siteDistanceStats.minimum;
                        results[nruns][entry++]=a.siteDistanceStats.maximum;
                        results[nruns][entry++]=a.siteDistanceStats.getAverage();
                        results[nruns][entry++]=a.siteDistanceStats.getSigma();
                        
                        nruns++;
                    }// eo lambda
                }// eo kappa
            }// eo mu
        }//eo j
  }
            
    
  /**
     * Outputs bare network in simple tab delimited format.
     *  <nameroot>_info.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputStatistics(String nameroot) 
    {
       // String SepString = "\t";
        String filenamecomplete =  "output/"+nameroot+ "_varystats.dat";        
        System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            
            printStatistics(PS) ;

            try
            { 
               fout.close ();
               System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputNetworkStats
  
  /**
     * Outputs bare network in simple tab delimited format.
     *  <nameroot>_info.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void printStatistics(PrintStream PS) 
    {
        PS.println(firstLine);
        for (int i=0; i<nruns; i++)
        {
            for (int j=0; j<nentries; j++)
            {
                PS.print(((float)results[i][j])+SEP);
            }
            PS.println();
        }
    }
    
    /** 
     * Reads in ranges of variables from file <inputnameroot>_maranges.dat.
     * First line site names
     * Second line site Values (fixed sizes)
     * Third line site X positions
     * Fourth line site Y positions
     * Remaining lines are the dist[i][j] data
     */
    public int readVariableRanges(String nameroot) 
    {
      String filename = new String();
      filename = nameroot+"_maranges.dat";
      System.out.println("Starting to ranges of parameters data from " + filename);
      TextReader data;     // Character input stream for reading data.
      String tempstring="";
      
//      int numberCt;  // Number of items actually stored in the array.
      
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         System.out.println("Can't find file "+filename);
         return 1;  
      }
         try{ 
                 tempstring=data.getln(); //comment read to end of line
                  //System.out.println("Comment: "+tempstring);
                  
             
          j.min=data.getDouble();
          j.max=data.getDouble();
          j.step=data.getDouble();

          mu.min=data.getDouble();
          mu.max=data.getDouble();
          mu.step=data.getDouble();
          
          kappa.min=data.getDouble();
          kappa.max=data.getDouble();
          kappa.step=data.getDouble();
          
          lambda.min=data.getDouble();
          lambda.max=data.getDouble();
          lambda.step=data.getDouble();

          System.out.println("Finished reading from " + filename);
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          System.out.println("Input Error: " + e.getMessage());
       }
       catch (IndexOutOfBoundsException e) {
          // Must have tried to put too many numbers in the array.
          System.out.println("Too many numbers in data file"+filename);
          System.out.println("Processing has been aborted.");
       }
       finally {
          // Finish by closing the files, 
          //     whatever else may have happened.
          data.close();
        }
       return 0;
    }  // end of getData() method

    
/**
 *
 * @author time
 */
public class Range 
{
    double min;
    double max;
    double step;

    
    public Range()
    {
        min=0;
        max=-1;
        step=1;      
    }
    
    public int count()
    {
        int n=(int) ((max-min)/step);
        return ((n>0) ? n : 0);
    }

    public double getMin()
    {
        return min;
    }

    public double getMax()
    {
        return max;
    }

    public double getStep()
    {
        return step;
    }

}
                
    
}
