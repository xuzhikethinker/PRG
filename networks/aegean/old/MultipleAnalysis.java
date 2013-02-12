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
    String MAVERSION="MA060704";
    String SEP = "\t";
    int nruns;
    int nrunsmax;
    int nentries;
    int noprentries;
    int nfixedparameters=5;
    int noutputvariables=8;
        
    double results[][];
    String firstLine;
    String [] parameterString;
    int nargs =0;
    Range j;
    Range mu;
    Range kappa;
    Range lambda;
    Range distScale;
    int repeat;        

    
    /** Creates a new instance of MultipleAnalysis */
    public MultipleAnalysis() 
    {
            firstLine="";
            parameterString= new String [100]; // maximum 100 parameters
            islandNetwork in = new islandNetwork(); 
            j=new Range(in.vertexSourceH);
            mu = new Range(in.edgeSourceH);
            kappa = new Range(in.kappaH);
            lambda = new Range(in.lambdaH);
            distScale = new Range(in.distScaleH);
            repeat =1;
        
   }
    

     /**
     * Calculates networks for a range of parameter values.
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void calcRange(islandNetwork a) 
    {
        System.out.println("\n###  Multiple Analysis version "+MAVERSION+ " using islandNetwork "+a.iNVERSION);        
        if (nargs>0) {
            String [] argList = new String[nargs];
            for (int iii =0; iii<nargs; iii++) argList[iii]=parameterString[iii];
            if (a.Parse(argList)!=0) 
            {
               System.out.println("\n***  Error in parameter parsing");        
                return;
            }               
        }
        
  
        noprentries=nfixedparameters+4*noutputvariables;
        nentries=nfixedparameters+5*noutputvariables;
        System.out.println(" Using  "+nfixedparameters+ " fixed parameters, "+noutputvariables+" output variables");        
        nrunsmax = j.count()*mu.count()*kappa.count()*lambda.count()*distScale.count()+ 1;
        System.out.println(" Doing "+nrunsmax+ " different data sets, each repeated "+repeat+" times");        
        System.out.println("Variable"+SEP+"min"+SEP+"max"+SEP+"step"+SEP+"value"+SEP+"Type"+SEP+"count");
        System.out.println("j"+SEP+j.toString());
        System.out.println("mu"+SEP+mu.toString());
        System.out.println("kappa"+SEP+kappa.toString());
        System.out.println("lambda"+SEP+lambda.toString());
        System.out.println("distance scale"+SEP+distScale.toString());
        
        results = new double [nrunsmax][nentries];
        StatisticalQuantity [] oneParameterResults = new StatisticalQuantity [noprentries];
//        nrunsmax=1000;
        nruns=0;
        firstLine="j"+SEP+"mu"+SEP+"kappa"+SEP+"lambda"+SEP+"dist.scale";
        firstLine=firstLine+SEP+"SW min"+SEP+"SW max"+SEP+"SW av"+SEP+"SW +/-"+SEP+"SW sigma";
        firstLine=firstLine+SEP+"SS min"+SEP+"SS max"+SEP+"SS av"+SEP+"SS +/-"+SEP+"SS sigma";
        firstLine=firstLine+SEP+"EV min"+SEP+"EV max"+SEP+"EV av"+SEP+"EV +/-"+SEP+"EV sigma";
        firstLine=firstLine+SEP+"EW min"+SEP+"EW max"+SEP+"EW av"+SEP+"EW +/-"+SEP+"EW sigma";
        firstLine=firstLine+SEP+"D1 min"+SEP+"D1 max"+SEP+"D1 av"+SEP+"D1 +/-"+SEP+"D1 sigma";
        firstLine=firstLine+SEP+"D2 min"+SEP+"D2 max"+SEP+"D2 av"+SEP+"D2 +/-"+SEP+"D2 sigma";
        firstLine=firstLine+SEP+"D3 min"+SEP+"D3 max"+SEP+"D3 av"+SEP+"D3 +/-"+SEP+"D3 sigma";
        firstLine=firstLine+SEP+"D4 min"+SEP+"D4 max"+SEP+"D4 av"+SEP+"D4 +/-"+SEP+"D4 sigma";
        firstLine=firstLine+SEP+"Version"+SEP+MAVERSION+"iN Version"+SEP+a.iNVERSION;
        double betaInitial=a.betaH;
                                
        for (j.value=j.min; j.value<=j.max; j.nextValue()) {
            a.vertexSourceH=j.value;
            
            for (mu.value=mu.min; mu.value<=mu.max; mu.nextValue() ) {
                a.edgeSourceH=mu.value;
                
                for (kappa.value=kappa.min; kappa.value<=kappa.max; kappa.nextValue() ) {
                    a.kappaH=kappa.value;
                    
                    for (lambda.value=lambda.min; lambda.value<=lambda.max; lambda.nextValue() ) {
                        a.lambdaH=lambda.value;
                        
                        for (distScale.value=distScale.min; distScale.value<=distScale.max; distScale.nextValue() ) {
                            a.distScaleH=distScale.value;
                            
                            System.out.println("\n #####################################################");
                            System.out.println("--- Starting data set #"+nruns);

                            if (nruns>=nrunsmax) 
                            {
                                      j.value = j.max;
                                     mu.value = mu.max;
                                  kappa.value = kappa.max;
                                 lambda.value = lambda.max;
                              distScale.value =    distScale.max; 
                              System.out.println(" Reached limit of number of runs");
                              break;
                            }
//                            System.out.println("j="+j.value+", mu="+mu.value+", kappa="+kappa.value+", lambda="+lambda.value+", dist.scale="+distScale.value);
                            a.showHamiltonianParameters();
                            
                            for (int i =0; i<noprentries; i++) oneParameterResults [i] = new StatisticalQuantity();
                            for (int r=0; r<repeat; r++ ) {
                                System.out.println("--- Run #"+r);
                                a.runname="r"+r;
                                a.betaH=betaInitial;
                                try{
                                    a.doMC();
                                int entry=0;
                                
                                oneParameterResults[entry++].add(a.vertexSourceH);
                                oneParameterResults[entry++].add(a.edgeSourceH);
                                oneParameterResults[entry++].add(a.kappaH);
                                oneParameterResults[entry++].add(a.lambdaH);
                                oneParameterResults[entry++].add(a.distScaleH);
                                if (entry!=nfixedparameters) {
                                    System.out.println(" *** ERROR had "+entry+" parameters when expected "+nfixedparameters);
                                    return;
                                }
                                
                                oneParameterResults[entry++].add(a.siteWeightStats.minimum);
                                oneParameterResults[entry++].add(a.siteWeightStats.maximum);
                                oneParameterResults[entry++].add(a.siteWeightStats.getAverage());
                                oneParameterResults[entry++].add(a.siteWeightStats.getSigma());
                                
                                oneParameterResults[entry++].add(a.siteStrengthStats.minimum);
                                oneParameterResults[entry++].add(a.siteStrengthStats.maximum);
                                oneParameterResults[entry++].add(a.siteStrengthStats.getAverage());
                                oneParameterResults[entry++].add(a.siteStrengthStats.getSigma());
                                
                                oneParameterResults[entry++].add(a.edgeValueStats.minimum);
                                oneParameterResults[entry++].add(a.edgeValueStats.maximum);
                                oneParameterResults[entry++].add(a.edgeValueStats.getAverage());
                                oneParameterResults[entry++].add(a.edgeValueStats.getSigma());
                                
                                oneParameterResults[entry++].add(a.edgeWeightStats.minimum);
                                oneParameterResults[entry++].add(a.edgeWeightStats.maximum);
                                oneParameterResults[entry++].add(a.edgeWeightStats.getAverage());
                                oneParameterResults[entry++].add(a.edgeWeightStats.getSigma());
                                
                                for (int iii=1; iii<5; iii++) {
                                    a.metricNumber=iii;
                                    a.calcNetworkDistances();
                                    oneParameterResults[entry++].add(a.siteDistanceStats.minimum);
                                    oneParameterResults[entry++].add(a.siteDistanceStats.maximum);
                                    oneParameterResults[entry++].add(a.siteDistanceStats.getAverage());
                                    oneParameterResults[entry++].add(a.siteDistanceStats.getSigma());
                                }// eo for iii
                                } catch (Exception e)
                                {
                                    System.out.println("*** Problem with run - exception "+e);
                                }
                            } //eo for r
                            
                            //a.calcNetworkStats();
                            int entry=0;
                            int oprentry=0;
                            System.out.println("--- Finished data for data set #"+nruns);
                            for (int iii=0; iii<nfixedparameters; iii++)
                                try{
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                }catch (Exception e) { System.out.println("ERROR "+e+SEP+nruns+" "+entry+" "+oprentry);};
                                
                                for (int iii =1; iii<5; iii++) {
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getError();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                }
                                
                                for (int iii=1; iii<5; iii++) {
                                    a.metricNumber=iii;
                                    a.calcNetworkDistances();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getError();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();

                            }
                            nentries=entry;   
                            nruns++;
                           // noprentries=oprentry;   
                        }// eo distScale
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
        String filenamecomplete =  "output/"+nameroot+"_MAstats.dat";        
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
            } catch (IOException e) { System.out.println("File Error"+e);}

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
     * Lines starting with # are comments.
     * Lines starting with *param are parsed as islandNetwork parameters, 
     * Lines starting with *rep is the number of repititions.
     * Lines starting with *j, *mu, *kappa, *lambda, *distScale set appropriate parameters.
     * For parameters, each line has: type, min, max, step then increment mode .
     */
    public int readVariableRanges(String nameroot) 
    {
      String filename = new String();
      filename = nameroot+"_maranges.dat";
      System.out.println("Starting to read ranges of parameters data from " + filename);
      TextReader data;     // Character input stream for reading data.
      String tempstring="";
      String datatype="Unknown";
      int linenumber=0;
      int numberCt;
      double[] darray = new double [100];
      
//      int numberCt;  // Number of items actually stored in the array.
      
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         System.out.println("Can't find file "+filename);
         return 1;  
      }
         try{ 
//                 tempstring=data.getln(); //comment read to end of line
                  //System.out.println("Comment: "+tempstring);                  
          while (!data.eof()) 
          { // start reading new line
              linenumber++;
              datatype = data.getWord(); //ignore first item, labels row
//            System.out.println("first word is "+datatype);
              if (datatype.startsWith("#")) 
              { // this line is a comment
                  tempstring=datatype+data.getln(); //comment read to end of line
                  System.out.println("Comment: "+tempstring);
                  continue;
              }
              
              if (datatype.startsWith("*param")) 
              { // this line is to parsed as parameters for the run
                   nargs=0;
                   while (!data.eoln() ) 
                   {
                       parameterString[nargs++]=data.getWord(); //comment read to end of line
                       }
                   System.out.print("Parameter string: ");
                   for (int iii=0; iii<nargs; iii++) System.out.print(SEP+parameterString[iii]);
                   System.out.println();                                      
                  continue;
              }
              
              if (datatype.startsWith("*rep")) 
              { // this line is to parsed the number of times to repeat each run.
                   repeat=data.getlnInt() ; // read int and then finish line
                   System.out.println("Repeating "+repeat+" times");
                   continue;
              }

              Range r = new Range();
              // assume line has a range for some data
              r.min= data.getDouble();
              r.max= data.getDouble();
              r.step= data.getDouble();
              r.type= data.getInt();
              if ((r.min>r.max) || (r.step<0)) 
              {
                  double v = r.min;
                  r = new Range (v);
              }
              if (datatype.startsWith("*j")) j=new Range(r);    
              if (datatype.startsWith("*mu")) mu=new Range(r);    
              if (datatype.startsWith("*kap")) kappa=new Range(r);    
              if (datatype.startsWith("*lam")) lambda=new Range(r);    
              if (datatype.startsWith("*dist")) distScale=new Range(r);    

            }//eo while sitedata
   
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
    double value;
    int type=-1; // 0 = add step, 1 = multiply by step, -1 just iterate once

    public Range()
    {
        min=8e99;
        max=-min;
        step=1;  
        value=min;
        type = -1;
    }

    // use to set value to be used, no loops
    public Range(double v)
    {
        if (Math.abs(v)>1e-6)
        {
            min=v;
            max=Math.abs(v)*0.1+v;
            step=v;  
            value=v;
        }
        else
        {
            min=v;
            max=v+2e-6;
            step=1e-5;  
            value=v;
        }
        type = -1;
    }

    // makes a deep copy
    public Range(Range r)
    {
        min=r.min;
        max=r.max;
        step=r.step;  
        value=r.value;
        type = r.type;
    }
    
    public String toString()
    {
        String s= min+SEP+max+SEP+step+SEP+value+SEP+getTypeString()+SEP+count();
        return s;
    }
            
    public String getTypeString()
    {
        String s="Unknown";
        switch (type)
        {
            case -1: s="one value"; break;
            case 1: s="multiply"; break;
            case 0: s="add"; break;
            default: s="UNKNOWN";
        }
        return s;
    }
    public int count()
    {
        int n;
        switch (type)
        {
            case -1: n=1; break;
            case 1: n= (int) (Math.log(max/min)/Math.log(step))+1; break;
            case 0: n =(int) ((max-min)/step) + 1; break;
            default: n=999;
                    
        }
        return ((n>1) ? n : 1);
    }

    public double nextValue()
    {
        switch (type)
        {
            case -1: value=max*2; break;
            case 1: value*=step; break;
            case 0: value+=step; break;
            default: value=max*2;
        }
        return value;
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
    public double getValue()
    {
        return value;
    }

}
                
    
}
