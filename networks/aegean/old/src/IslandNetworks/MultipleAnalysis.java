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

//import IslandNetworks.Edge.IslandEdgeSet;
import IslandNetworks.Edge.IslandEdge;
import java.io.*;
import java.awt.Dimension;

import JavaNotes.TextReader;
import TimUtilities.Range;
import TimUtilities.StatisticalQuantity;
import TimUtilities.TimTime;
import TimUtilities.TimTiming;

//import IslandNetworks.ClusteringWindow;

/**
 * Runs <tt>islandNetwork</tt> multiple times.
 * @author time
 */
public class MultipleAnalysis 
{
    static String MAVERSION="MA080429";
    static String SEP = "\t";
    
    /**
     * Number of runs to make before outputing more results
     */
    static int NRUNS = 100;
    /**
     * Number of entries needed in the results array
     */
    int nentries;
    
    /**
     * Number of entries in the array of statistical quantities.
     * This will be one for each input parameter, 4 for each single output parameter and 4 for each general ouput variable.
     */
    int noprentries;
    /**
     * Number of Fixed Input Parameters {@value}
     */
    static int nfixedparameters=5;

    /**
     * Number of Single Ouptut Paramneters e.g. energy {@value}
     */
    static int nsingleouputparameters=1;
    
    /**
     * Number of output variables to be monitored (=4 + number of metrics)
     */
    final int noutputvariables; //=4+metricList.length;
    
    /**
     * List of integers containing metric numbers required
     */
    static int metricList[] = {1,2,3,4};
    
    String endingMAStats;
        
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
    int runOffset;
    
    /**
     * Record if have already started the statistics output file
     */
    boolean fileOutputStatisticsStarted = false;
    
    
    
    /** Creates a new instance of MultipleAnalysis */
    public MultipleAnalysis() 
    {
            firstLine="";
            parameterString= new String [100]; // maximum 100 parameters
            islandNetwork in = new islandNetwork(1); 
            noutputvariables=5+metricList.length;
            j=new Range(in.Hamiltonian.vertexSource);
            mu = new Range(in.Hamiltonian.edgeSource);
            kappa = new Range(in.Hamiltonian.kappa);
            lambda = new Range(in.Hamiltonian.lambda);
            distScale = new Range(in.Hamiltonian.distanceScale);
            repeat =1;
            runOffset=in.outputFile.sequenceNumber;
            
            TimTime time = new TimTime("_");
            endingMAStats = "_MAstats"+time.fullString("_");
            fileOutputStatisticsStarted = false;
        
        
   }
    
/**
     * Runs island networks for multiple parameters values.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // use dummy initial network to set up and store parameters
        islandNetwork a = new islandNetwork(1); // dummy network with dfault values
        a.outputMode.setAllOff(); // no file outputs by default
        int res = a.Parse(args);
        if (res>0) {System.out.println("Command line argument failure - return code "+res);
            return;
            }
        res = a.getSiteData();
        System.out.println("Data reading OK, number of sites is "+a.numberSites);

        a.setInfomationLevel(-1); // switch off outputs
        MultipleAnalysis ma = new MultipleAnalysis();
            ma.readVariableRanges(a.inputFile.getFullLocationFileRoot());
            a.outputFile.setParameterName("v"+a.modelNumber.major+"_"+a.modelNumber.minor);
            ma.calcRange(a);
            // ??????????????????????????????????????????????????????????????????????????????
            //
 
         //        readVariableRanges(a.inputFile.nameRoot);
//        calcRange(a);
//        FileOutputStatistics(a.inputFile.nameRoot+"_v"+a.modelNumber.major+"_"+a.modelNumber.minor);        
    }
    
        
     /**
     * Calculates networks for a range of parameter values.
     * @param a the islandNetwork to be processed.
     */
    public void calcRange(islandNetwork a) 
    {
        System.out.println("\n###  Multiple Analysis version "+MAVERSION+ " using islandNetwork "+islandNetwork.iNVERSION);        
        if (nargs>0) {
            String [] argList = new String[nargs];
            for (int iii =0; iii<nargs; iii++) argList[iii]=parameterString[iii];
            if (a.Parse(argList)!=0) 
            {
               System.out.println("\n***  Error in parameter parsing");        
                return;
            }               
        }
        
        runOffset=a.outputFile.sequenceNumber;
  
        int nruns;
        int noutputs;
        int nrunsmax;
        noprentries = nfixedparameters + nsingleouputparameters + 4 * noutputvariables;
        nentries = nfixedparameters + 5 * (nsingleouputparameters + noutputvariables);
        System.out.println(" Using  "+nfixedparameters+ " fixed parameters, "+noutputvariables+" output variables");        
        nrunsmax = j.count()*mu.count()*kappa.count()*lambda.count()*distScale.count()+ 1;
        System.out.println(" Doing "+nrunsmax+ " different data sets, each repeated "+repeat+" times");        
        System.out.println("Variable"+SEP+"min"+SEP+"max"+SEP+"step"+SEP+"value"+SEP+"Type"+SEP+"count");
        System.out.println("j"+SEP+j.toString(SEP));
        System.out.println("mu"+SEP+mu.toString(SEP));
        System.out.println("kappa"+SEP+kappa.toString(SEP));
        System.out.println("lambda"+SEP+lambda.toString(SEP));
        System.out.println("distance scale"+SEP+distScale.toString(SEP));
        
        results = new double [NRUNS][nentries]; //new double [nrunsmax][nentries];
        StatisticalQuantity [] oneParameterResults = new StatisticalQuantity [noprentries];
//        nrunsmax=1000;
        nruns=0;
        noutputs=0;
        firstLine="j"+SEP+"mu"+SEP+"kappa"+SEP+"lambda"+SEP+"dist.scale";
        firstLine=firstLine+variableLabel("En", SEP); //SEP+"En min"+SEP+"En max"+SEP+"En av"+SEP+"En +/-"+SEP+"En sigma";
        firstLine=firstLine+variableLabel("SV", SEP); //+SEP+"SW min"+SEP+"SW max"+SEP+"SW av"+SEP+"SW +/-"+SEP+"SW sigma";
        firstLine=firstLine+variableLabel("SW", SEP); //+SEP+"SW min"+SEP+"SW max"+SEP+"SW av"+SEP+"SW +/-"+SEP+"SW sigma";
        firstLine=firstLine+variableLabel("SS", SEP); //+SEP+"SS min"+SEP+"SS max"+SEP+"SS av"+SEP+"SS +/-"+SEP+"SS sigma";
        firstLine=firstLine+variableLabel("EV", SEP); //+SEP+"EV min"+SEP+"EV max"+SEP+"EV av"+SEP+"EV +/-"+SEP+"EV sigma";
        firstLine=firstLine+variableLabel("EW", SEP); //+SEP+"EW min"+SEP+"EW max"+SEP+"EW av"+SEP+"EW +/-"+SEP+"EW sigma";
        for (int m=0; m<metricList.length;m++) firstLine=firstLine+variableLabel("D"+metricList[m], SEP); 
        firstLine=firstLine+SEP+"Version"+SEP+MAVERSION+"iN Version"+SEP+islandNetwork.iNVERSION;
        
        double betaInitial=a.Hamiltonian.beta;
        a.autoSetOutputFileName=true;
        
        TimTiming timeEllapsed = new TimTiming();
        
        for (j.value=j.min; j.value<=j.max; j.nextValue()) {
            a.Hamiltonian.vertexSource=j.value;
            
            for (mu.value=mu.min; mu.value<=mu.max; mu.nextValue() ) {
                a.Hamiltonian.edgeSource=mu.value;
                
                for (kappa.value=kappa.min; kappa.value<=kappa.max; kappa.nextValue() ) {
                    a.Hamiltonian.kappa=kappa.value;
                    
                    for (lambda.value=lambda.min; lambda.value<=lambda.max; lambda.nextValue() ) {
                        a.Hamiltonian.lambda=lambda.value;
                        
                        for (distScale.value=distScale.min; distScale.value<=distScale.max; distScale.nextValue() ) {
                            a.Hamiltonian.distanceScale=distScale.value;
                            int setNumber = nruns+noutputs*NRUNS;
                            double frac = ((double) nruns+noutputs*NRUNS)/nrunsmax;
                            System.out.println("\n #####################################################");
                            System.out.println("--- Starting data set #"+setNumber+", fraction completed "+Math.round(frac*100)+"% , time remaining "+timeEllapsed.estimateRemainingTimeString(frac));

                            if (nruns>=NRUNS) 
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
                            a.outputFile.setFirstFreeSequenceNumber(this.runOffset);
                            for (int r=0; r<repeat; r++ ) {
                                System.out.println("--- Run #"+r);
                                //a.outputFile.setFirstFreeSequenceNumber();
                                a.Hamiltonian.beta=betaInitial;
                                a.setOutputFileName();
                                try{
                                    a.doMC();
                                    
                                // update statistics on all runs    
                                int entry=0;
                                
                                oneParameterResults[entry++].add(a.Hamiltonian.vertexSource);
                                oneParameterResults[entry++].add(a.Hamiltonian.edgeSource);
                                oneParameterResults[entry++].add(a.Hamiltonian.kappa);
                                oneParameterResults[entry++].add(a.Hamiltonian.lambda);
                                oneParameterResults[entry++].add(a.Hamiltonian.distanceScale);
                                if (entry!=nfixedparameters) {
                                    System.out.println(" *** ERROR had "+entry+" parameters when expected "+nfixedparameters);
                                    return;
                                }
                                
                                oneParameterResults[entry++].add(a.energy);
                                
                                addToStatisticalQuantityArray(oneParameterResults, entry, a.siteSet.siteValueStats);
//                                oneParameterResults[entry++].add(a.siteSet.siteValueStats.minimum);
//                                oneParameterResults[entry++].add(a.siteSet.siteValueStats.maximum);
//                                oneParameterResults[entry++].add(a.siteSet.siteValueStats.getAverage());
//                                oneParameterResults[entry++].add(a.siteSet.siteValueStats.getSigma());
                                
                                addToStatisticalQuantityArray(oneParameterResults, entry, a.siteSet.siteWeightStats);
//                                oneParameterResults[entry++].add(a.siteSet.siteWeightStats.minimum);
//                                oneParameterResults[entry++].add(a.siteSet.siteWeightStats.maximum);
//                                oneParameterResults[entry++].add(a.siteSet.siteWeightStats.getAverage());
//                                oneParameterResults[entry++].add(a.siteSet.siteWeightStats.getSigma());
                                
                                addToStatisticalQuantityArray(oneParameterResults, entry, a.siteSet.siteStrengthInStats);
//                                oneParameterResults[entry++].add(a.siteSet.siteStrengthInStats.minimum);
//                                oneParameterResults[entry++].add(a.siteSet.siteStrengthInStats.maximum);
//                                oneParameterResults[entry++].add(a.siteSet.siteStrengthInStats.getAverage());
//                                oneParameterResults[entry++].add(a.siteSet.siteStrengthInStats.getSigma());
                                
                                addToStatisticalQuantityArray(oneParameterResults, entry, a.edgeSet.edgeStats[IslandEdge.valueINDEX]);
//                                oneParameterResults[entry++].add(a.edgeSet.edgeStats[IslandEdge.valueINDEX].minimum);
//                                oneParameterResults[entry++].add(a.edgeSet.edgeStats[IslandEdge.valueINDEX].maximum);
//                                oneParameterResults[entry++].add(a.edgeSet.edgeStats[IslandEdge.valueINDEX].getAverage());
//                                oneParameterResults[entry++].add(a.edgeSet.edgeStats[IslandEdge.valueINDEX].getSigma());
                                
                                addToStatisticalQuantityArray(oneParameterResults, entry, a.edgeSet.edgeStats[IslandEdge.weightINDEX]);
//                                oneParameterResults[entry++].add(a.edgeSet.edgeStats[IslandEdge.weightINDEX].minimum);
//                                oneParameterResults[entry++].add(a.edgeSet.edgeStats[IslandEdge.weightINDEX].maximum);
//                                oneParameterResults[entry++].add(a.edgeSet.edgeStats[IslandEdge.weightINDEX].getAverage());
//                                oneParameterResults[entry++].add(a.edgeSet.edgeStats[IslandEdge.weightINDEX].getSigma());
                                
                                a.setEdgePotentials(); // needed for Dykstra distances
                            
                                for (int m=0; m<metricList.length; m++) {
                                    a.edgeSet.metric.set(metricList[m]);
                                    a.calcNetworkSeparations();
                                    addToStatisticalQuantityArray(oneParameterResults, entry, a.siteDistanceStats);
//                                    oneParameterResults[entry++].add(a.siteDistanceStats.minimum);
//                                    oneParameterResults[entry++].add(a.siteDistanceStats.maximum);
//                                    oneParameterResults[entry++].add(a.siteDistanceStats.getAverage());
//                                    oneParameterResults[entry++].add(a.siteDistanceStats.getSigma());
                                }// eo for iii

                                
                                } catch (Exception e)
                                {
                                    System.out.println("*** Problem with run "+r+" - exception "+e);
                                }
                                
                                
                                // do outputs for this one run
                                try{if (r==0) oneNetworkFileOutput(r, a, true) ;
                                    else oneNetworkFileOutput(r, a, false) ;}
                                catch (Exception e){ System.out.println("*** Problem with general network file outputs, run "+r+" - exception "+e);}
                                
//                                try{if (r==0) 1oneNetworkJpegOutput(r,a);}
//                                catch (Exception e){ System.out.println("*** Problem with jpeg output run "+r+" - exception "+e);}
                                // *** NO JPEG OUTPUT - ZERO WIDTH and HEIGHT

                                
                            } //eo for r
                            
                            //a.calcNetworkStats();
                            int entry=0;
                            int oprentry=0;
                            System.out.println("--- Finished data for data set #"+nruns);
                            for (int iii=0; iii<nfixedparameters; iii++)
                                try{
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                }catch (Exception e) { System.out.println("ERROR "+e+SEP+nruns+" "+entry+" "+oprentry);}
                                
                            // now add results from a single output parameter
                            results[nruns][entry++]=oneParameterResults[oprentry].minimum;
                            results[nruns][entry++]=oneParameterResults[oprentry].maximum;
                            results[nruns][entry++]=oneParameterResults[oprentry].getAverage();
                            results[nruns][entry++]=oneParameterResults[oprentry].getError();
                            results[nruns][entry++]=oneParameterResults[oprentry++].getSigma();

                            for (int iii =1; iii<5; iii++) {
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getError();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                }
                                
                                for (int m=0; m<metricList.length; m++) {
                                    a.edgeSet.metric.set(metricList[m]);
                                    a.calcNetworkSeparations();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getError();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();

                            }
                            nentries=entry;   
                            nruns++;
                            
                            // output last NRUNS results
                            if (nruns==NRUNS) {FileOutputStatistics(a.outputFile, nruns); nruns=0; noutputs++;}
                            
                           // noprentries=oprentry;   
                        }// eo distScale
                    }// eo lambda
                }// eo kappa
            }// eo mu
        }//eo j
        
        // output any remaining results
        if (nruns%NRUNS != 0) FileOutputStatistics(a.outputFile, nruns);
  }
    /**
     * Adds relevant entries to for a StatisticalQuantity for one run to list.
     * @param oneParameterResults Stores results for all parameters of a run
     * @param entry next entry number
     * @param sq Statistical quantity to be added
     * @return next entry number
     */
    private int addToStatisticalQuantityArray(StatisticalQuantity[] oneParameterResults, int entry, StatisticalQuantity sq) {
        oneParameterResults[entry++].add(sq.minimum);
        oneParameterResults[entry++].add(sq.maximum);
        oneParameterResults[entry++].add(sq.getAverage());
        oneParameterResults[entry++].add(sq.getSigma());
        return entry;
    }
    /**
     * Outputs bare network in simple tab delimited format.
     *  <p> <emph>nameroot</emph><tt>_MAstats_date_time.dat</tt> general info.
     * Will start a new file with a header if <tt>fileOutputStatisticsStarted</tt> is false
     * otherwise will append to file without a header if <tt>fileOutputStatisticsStarted</tt> is true.
     * @param fl filelocation used to form file names.
     * @param count number of entries to print out
     */
    public void FileOutputStatistics(FileLocation fl, int count) 
    {
        
        String filenamecomplete =  fl.getRootLocationSimpleFileName(endingMAStats,"dat");        
        System.out.println("Attempting to write general information to "+ filenamecomplete);
        PrintStream PS;
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete, fileOutputStatisticsStarted); //
            PS = new PrintStream(fout);
            // header line if needed
            if (!fileOutputStatisticsStarted) PS.println(firstLine);
            printStatistics(PS, count) ;
            try
            { 
               fout.close ();
               System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error"+e);}
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete+"  Error"+e);
            return;
        }
        fileOutputStatisticsStarted = true;
        return;
    }//eo FileOutputNetworkStats
  
    /**
     * Outputs bare network in simple tab delimited format.
     *  <br> <emph>nameroot</emph>_info.dat
     * @param PS PrintSteam such as System.out
     * @param count number of entries to print out
     */
    public void printStatistics(PrintStream PS, int count) 
    {
        for (int i=0; i<count; i++)
        {
            for (int i2=0; i2<nentries; i2++)
            {
                PS.print(((float)results[i][i2])+SEP);
            }
            PS.println();
        }
    }

     
    
    /** 
     * Reads in ranges of variables from file <inputFile.nameRoot>_maranges.dat.
     * Lines starting with # are comments.
     * Lines starting with *param are parsed as islandNetwork parameters, 
     * Lines starting with *rep is the number of repititions.
     * Lines starting with *j, *mu, *kappa, *lambda, *distScale set appropriate parameters.
     * For parameters, each line has: type, min, max, step then increment mode .
     *<br> <emph>nameroot</emph>_info.dat
     *@param nameroot stem used to form file names.
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
     * Gives fileoutputs for individual runs
     * @param run number of run
     * @param in island network for which output are needed
     * @param infoFileOutput true if want an information file for these runs
     */
    public void oneNetworkFileOutput(int run, islandNetwork in, boolean infoFileOutput) 
    {
        
//        String filenamecomplete =  "output/"+in.outputFile.nameRoot+"rall";        
        if (infoFileOutput) 
        {
            System.out.println("Attempting to write general information to "+ in.outputFile.getNameRoot() );
            in.FileOutputNetworkParam(SEP, run);
        }
         int siteWeightFactor=20;
         int edgeWidthFactor=10;
         double minColourFrac=0.2;
         double zeroColourFrac=0.1;
         //fileType 0=plain, 1= BW, 2= colour, 3=Influence Matrix, 4=Culture Corrrelation Matrix
         int fileType = 0; 
         System.out.println("Attempting to write basic network to "+ in.outputFile.getFullFileRoot());
         in.FileOutputNetworkForData( "#" , 5);
         // pajek files
         in.FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,0);
         final String[] pn =
            {"Value", "Weight", "Ranking", "Strength", "StrengthIn", "StrengthOut"};
         for (int i=0; i<pn.length; i++) in.FileOutputPajekSiteFiles(pn[i]);
         //in.FileOutputBareNetwork(SEP);
         //in.FileOutputNetworkStatistics("#",3);
         return;

    } // eo oneNetworkFileOutput

      /** 
     * Gives fileoutputs for individual runs
       * <br>Problems with size of imagge. 
     * @param run number of run
     * @param in island network for which output are needed
     */
    public void oneNetworkJpegOutput(int run, islandNetwork in) 
    {
        
//        System.out.println("Attempting to write jpeg image to "+ in.outputFile.nameRoot);
        ClusteringWindow cw = new ClusteringWindow();
        cw.initialiseClusteringWindow(in, new Dimension(600,600));
        cw.setUpPrintableView(600,600);
        cw.writeJPEGImage(); 
        return;

    } // eo oneNetworkFileOutput
  
    /**
     * Gives label for columns of one variable's output.
     * @param name name of variable 
     * @param SEP separation string
     * @return string with label for columns of one variable's output
     */
    private String variableLabel(String name, String SEP){
        return SEP+name+" min"+SEP+name+" max"+SEP+name+" av"+SEP+name+" av +/-"+SEP+name+" sigma";
    }
    
}
              