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

import IslandNetworks.ClusteringWindow;
import IslandNetworks.io.FileLocation;
import IslandNetworks.Edge.IslandEdge;
import IslandNetworks.islandNetwork;
import java.awt.Dimension;

import JavaNotes.TextReader;
import TimUtilities.Range;
import TimUtilities.StatisticalQuantity;
import TimUtilities.TimTime;
import TimUtilities.TimTiming;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Runs <tt>islandNetwork</tt> multiple times.
 * @author time
 */
public class MultipleAnalysis
{
    static String MAVERSION="MA120306";
    static String SEP = "\t";
    
    /**
     * Number of runs to make before outputing more results
     */
    static int NRUNS = 1000;
    /**
     * Number of entries needed in the results array
     */
    int nentries;
    
    /**
     * Number of entries in the array of statistical quantities.
     * This will be one for each input parameter, 4 for each single output parameter and 4 for each general ouput variable.
     */
    int noprentries;

//    final static String [] fixedInputParametersLong={"vertexSource",
//            "edgeSource","kappa","lambda","distanceScale","beta"};
    final static String [] fixedInputParameters={"j",
            "mu","kappa","lambda","D","beta"};
    /**
     * Number of Fixed Input Parameters {@value}
     */
    static int nfixedparameters=fixedInputParameters.length;

    /**
     * Number of Single Output Parameters e.g. energy {@value}
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
    /**
     * Short name of update mode used.
     */
    String modeShortName="UNSET";
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
    Range beta;
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
            beta = new Range(in.Hamiltonian.beta);
            repeat =1;
            runOffset=in.outputFile.sequenceNumber;
            
            noprentries = nfixedparameters + nsingleouputparameters + 4 * noutputvariables;
            nentries = nfixedparameters + 5 * (nsingleouputparameters + noutputvariables);

            
            fileOutputStatisticsStarted = false;  
   }
    
/**
     * Runs island networks for multiple parameters values.
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println("\n**** Running MultipleAnalysis ****\n");
        String [] aList;
        boolean directed=true;
        if (args.length >0) {aList = args;}
        else{
        // Update modes are "PPA","DPPA","MDN","MC","VP","DCGM","RWGM","SGM"
        //String [] aList={"-Eariadne", "-fibaegean10S1L3a", "-uPPA", "-bt3.0"};
            String s="";
            int modeNumber=2;
            switch (modeNumber) {
                case 0: s = "-uPPA -bt3.0"; directed=false; break;
                case 1: s = "-uDPPA -bt3.0"; break;
                case 2: s = "-uMDN -dl100.0"; directed=false; break;
                case 3: s = "-uMC -dl100.0"; break;
                case 4: s = "-uVP -dl100.0"; directed=false; break;
                case 5: s = "-uDCGM -dl100.0"; break;
                case 6: s = "-uRWGM -dl100.0"; break;
                case 7: s = "-uSGM -dl100.0"; break;
                default: throw new RuntimeException("unknown mode");
            }
            String rootname="circleN10D100.0-C5-J50.0";
            //String rootname="circleN40D150.0-C10-J50.0";
            String fullArgString= "-Emultipleanalysis -fib"+rootname+" "+s;
            System.out.println("--- Full arguments are:- "+fullArgString);
            aList=fullArgString.split("\\s+");
        }

        // use dummy initial network to set up and store parameters
        islandNetwork a = new islandNetwork(1); // dummy network with default values
        //a.inputFile.setNameRoot("circleN10D100.0-C5-J50.0");
        //a.setOutputFileName();
        a.outputMode.setAllOff(); // no file outputs by default
        int res = a.Parse(aList);
        if (res>0) {System.out.println("Command line argument failure - return code "+res);
            return;
            }
        res = a.getSiteData();
        if (res<0) throw new RuntimeException("Failed to read site data");
        System.out.println("Data reading OK, number of sites is "+a.getNumberSites());

        a.setInfomationLevel(-1); // switch off outputs
        MultipleAnalysis ma = new MultipleAnalysis();
        ma.doMultipleAnalysis(a);
    }

    public void doMultipleAnalysis(islandNetwork a){
        String updateShortName=a.updateMode.toString();
        TimTime time = new TimTime("_");
        endingMAStats = "_"+updateShortName+"_"+time.fullString("_");
        readVariableRanges(a.inputFile.getFullLocationFileRoot(),updateShortName);
        a.setOutputFileName();
        calcAriadneRange(a);        
    }
    
        
     /**
     * Calculates networks for a range of parameter values using Ariadne edge model.
     * @param a the islandNetwork to be processed.
     */
    public void calcAriadneRange(islandNetwork a)
    {
        System.out.println("\n###  Multiple Analysis version "+MAVERSION+ " using islandNetwork "+islandNetwork.iNVERSION);        
        if (nargs>0) {
            String [] argList = new String[nargs];
            System.arraycopy(parameterString, 0, argList, 0, nargs);
            if (a.Parse(argList)!=0) 
            {
               System.out.println("\n***  Error in parameter parsing");        
                return;
            }               
        }
        
        runOffset=a.outputFile.sequenceNumber;
  
        modeShortName=a.updateMode.toString();

        int nruns;
        int noutputs;
        int nrunsmax;
//        noprentries = nfixedparameters + nsingleouputparameters + 4 * noutputvariables;
//        nentries = nfixedparameters + 5 * (nsingleouputparameters + noutputvariables);
        System.out.println(" Update mode is "+modeShortName+", "+a.updateMode.toLongString());
        System.out.println(" Using  "+nfixedparameters+ " fixed parameters, "+noutputvariables+" output variables");
        nrunsmax = j.count()*mu.count()*kappa.count()*lambda.count()*distScale.count()*beta.count()+ 1;
        System.out.println(" Doing "+nrunsmax+ " different data sets, each repeated "+repeat+" times");        
        System.out.println("Variable"+SEP+"min"+SEP+"max"+SEP+"step"+SEP+"value"+SEP+"Type"+SEP+"count");
        System.out.println("j"+SEP+j.toString(SEP));
        System.out.println("mu"+SEP+mu.toString(SEP));
        System.out.println("kappa"+SEP+kappa.toString(SEP));
        System.out.println("lambda"+SEP+lambda.toString(SEP));
        System.out.println("distance scale"+SEP+distScale.toString(SEP));
        System.out.println("beta"+SEP+beta.toString(SEP));

        results = new double [NRUNS][nentries]; //new double [nrunsmax][nentries];
//        StatisticalQuantity [] oneParameterResults = new StatisticalQuantity [noprentries];
//        nrunsmax=1000;
        nruns=0;
        noutputs=0;
        setLabelLine();
        a.autoSetOutputFileName=true;
//        double [] resArray = new double[1];     

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
                        for (beta.value=beta.min; beta.value<=beta.max; beta.nextValue() ) {
                            a.betaInitial=beta.value;
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
                              beta.value =    beta.max;
                              System.out.println(" Reached limit of number of runs");
                              break;
                            }
//                            System.out.println("j="+j.value+", mu="+mu.value+", kappa="+kappa.value+", lambda="+lambda.value+", dist.scale="+distScale.value);
                            a.showHamiltonianParameters();
                            a.outputFile.setFirstFreeSequenceNumber(this.runOffset);
                           
                       try{
                           double [] resArray = calcOneParameterSet(a, repeat);
                                System.arraycopy(resArray, 0, results[nruns], 0, resArray.length);
                       }catch (Exception e) { System.err.println("ERROR "+e+SEP+" run number "+nruns);}
                       System.out.println("--- Finished data for data set #"+nruns);
                       nruns++;
                            
                            // output last NRUNS results
                            if (nruns==NRUNS) {FileOutputStatistics(a.outputFile, nruns); nruns=0; noutputs++;}
                            
                           // noprentries=oprentry;   

                         }// eo beta
                      }// eo distScale
                    }// eo lambda
                }// eo kappa
            }// eo mu
        }//eo j
        
        // output any remaining results
        if (nruns%NRUNS != 0) FileOutputStatistics(a.outputFile, nruns);
  }


    
    public String getLabelLine(){setLabelLine(); return firstLine;}
    public void setLabelLine(){
        firstLine="Type"+SEP+"j"+SEP+"mu"+SEP+"kappa"+SEP+"lambda"+SEP+"dist.scale"+SEP+"beta";
        firstLine=firstLine+variableLabel("En", SEP); //SEP+"En min"+SEP+"En max"+SEP+"En av"+SEP+"En +/-"+SEP+"En sigma";
        firstLine=firstLine+variableLabel("SV", SEP); //+SEP+"SW min"+SEP+"SW max"+SEP+"SW av"+SEP+"SW +/-"+SEP+"SW sigma";
        firstLine=firstLine+variableLabel("SW", SEP); //+SEP+"SW min"+SEP+"SW max"+SEP+"SW av"+SEP+"SW +/-"+SEP+"SW sigma";
        firstLine=firstLine+variableLabel("SS", SEP); //+SEP+"SS min"+SEP+"SS max"+SEP+"SS av"+SEP+"SS +/-"+SEP+"SS sigma";
        firstLine=firstLine+variableLabel("EV", SEP); //+SEP+"EV min"+SEP+"EV max"+SEP+"EV av"+SEP+"EV +/-"+SEP+"EV sigma";
        firstLine=firstLine+variableLabel("EW", SEP); //+SEP+"EW min"+SEP+"EW max"+SEP+"EW av"+SEP+"EW +/-"+SEP+"EW sigma";
        for (int m=0; m<metricList.length;m++) firstLine=firstLine+variableLabel("D"+metricList[m], SEP); 
        firstLine=firstLine+SEP+"Version"+SEP+MAVERSION+"iN Version"+SEP+islandNetwork.iNVERSION;
    }
    
    //public void doOneParameterSet(islandNetwork a, double betaInitial, StatisticalQuantity [] oneParameterResults, int numberRuns){
    /**
     * Does all runs for one set of parameters.
     * <p>Collects results and gives averages and errors for those.
     * @param a island network
     * @param numberRuns number of runs to make
     * @return array of doubles.
     */
    public double [] calcOneParameterSet(islandNetwork a, int numberRuns){
         double [] oneResultSet;
         double betaInitialMC=a.Hamiltonian.beta;
         StatisticalQuantity [] oneParameterResults = new StatisticalQuantity [noprentries];
         for (int i =0; i<noprentries; i++) oneParameterResults [i] = new StatisticalQuantity();
         for (int r=0; r<numberRuns; r++ ) {
                                System.out.println("--- Run #"+r);
                                //a.outputFile.setFirstFreeSequenceNumber();
                                a.setOutputFileName();
                                try{
                                    if (a.updateMode.isMC()) {
                                        a.Hamiltonian.beta=betaInitialMC;
                                    }
                                   a.doEdgeModel();
                                   a.calcNetworkStats();

                                // update statistics on all runs    
                                int entry=0;
                                
                                oneParameterResults[entry++].add(a.Hamiltonian.vertexSource);
                                oneParameterResults[entry++].add(a.Hamiltonian.edgeSource);
                                oneParameterResults[entry++].add(a.Hamiltonian.kappa);
                                oneParameterResults[entry++].add(a.Hamiltonian.lambda);
                                oneParameterResults[entry++].add(a.Hamiltonian.distanceScale);
                                oneParameterResults[entry++].add(a.betaInitial);
                                if (entry!=nfixedparameters) {
                                    System.out.println(" *** ERROR had "+entry+" parameters when expected "+nfixedparameters);
                                    return null;
                                }
                                
                                oneParameterResults[entry++].add(a.globalProperties.getEnergy());
                                
                                entry=addToStatisticalQuantityArray(oneParameterResults, entry, a.siteSet.siteValueStats);
                                
                                entry=addToStatisticalQuantityArray(oneParameterResults, entry, a.siteSet.siteWeightStats);
                                
                                entry=addToStatisticalQuantityArray(oneParameterResults, entry, a.siteSet.siteStrengthInStats);
                                
                                entry=addToStatisticalQuantityArray(oneParameterResults, entry, a.edgeSet.edgeStats[IslandEdge.valueINDEX]);
                                
                                entry=addToStatisticalQuantityArray(oneParameterResults, entry, a.edgeSet.edgeStats[IslandEdge.weightINDEX]);
                                
                                a.setEdgePotentials(); // needed for Dykstra distances
                            
                                for (int m=0; m<metricList.length; m++) {
                                    a.edgeSet.metric.set(metricList[m]);
                                    a.calcNetworkSeparations();
                                    entry=addToStatisticalQuantityArray(oneParameterResults, entry, a.siteDistanceStats);
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

        
    
                            oneResultSet = new double [nentries];
                            int entry=0;
                            int oprentry=0;
                            for (int iii=0; iii<nfixedparameters; iii++)
                                try{
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getAverage();
                                }catch (Exception e) { throw new RuntimeException("ERROR: "+entry+" "+oprentry);}
                                
                            // now add results from a single output parameter
                            oneResultSet[entry++]=oneParameterResults[oprentry].minimum;
                            oneResultSet[entry++]=oneParameterResults[oprentry].maximum;
                            oneResultSet[entry++]=oneParameterResults[oprentry].getAverage();
                            oneResultSet[entry++]=oneParameterResults[oprentry].getError();
                            oneResultSet[entry++]=oneParameterResults[oprentry++].getSigma();

//                            for (int iii =1; iii<5; iii++) {
                            for (int iii =1; iii<nfixedparameters; iii++) {
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getAverage();
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getAverage();
                                    oneResultSet[entry++]=oneParameterResults[oprentry].getAverage();
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getError();
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getAverage();
                                }
                                
                                for (int m=0; m<metricList.length; m++) {
                                    a.edgeSet.metric.set(metricList[m]);
                                    a.calcNetworkSeparations();
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getAverage();
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getAverage();
                                    oneResultSet[entry++]=oneParameterResults[oprentry].getAverage();
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getError();
                                    oneResultSet[entry++]=oneParameterResults[oprentry++].getAverage();

                            }
                            nentries=entry;   

                            return oneResultSet;
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
     * Outputs results[][] in simple tab delimited format.
     * @param PS PrintSteam such as System.out
     * @param count number of entries to print out
     */
    public void printStatistics(PrintStream PS, int count) 
    {
        for (int i=0; i<count; i++)
        {
            PS.print(modeShortName);
            for (int i2=0; i2<nentries; i2++)
            {
                PS.print(((float)results[i][i2])+SEP);
            }
            PS.println();
        }
    }

     public void printOneParameterStatistics(PrintStream PS, String sep, double [] results){      
         PS.println(getLabelLine());
         for (int i2=0; i2<results.length; i2++)
            {
                PS.print(((float)results[i2])+sep);
            }
            PS.println();   
     }
    
    /** 
     * Reads in ranges of variables from file.
     * <p>File is <inputFile.nameRoot>_<updateName>maranges.dat.
     * <p>updatName is the short name of the update mode.
     * Lines starting with # are comments.
     * Lines starting with *param are parsed as islandNetwork parameters, 
     * Lines starting with *rep is the number of repetitions.
     * Lines starting with *j, *mu, *kappa, *lambda, *distScale set appropriate parameters.
     * For parameters, each line has: type, min, max, step then increment mode.
     * <p>If no range is given for a parameter the default
     * value is used but with no loops programmed in this variable.
     *@param nameroot stem used to form file names.
     *@param updateShortName short name for update mode
     */
    public int readVariableRanges(String nameroot, String updateShortName)
    {
      String filename = new String();
      filename = nameroot+"_"+updateShortName+"maranges.dat";
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
         System.err.println("Can't find file "+filename);
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
              if (datatype.startsWith("*beta")) beta=new Range(r);

            }//eo while sitedata
   
          System.out.println("Finished reading from " + filename);
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          System.err.println("Input Error: " + e.getMessage());
       }
       catch (IndexOutOfBoundsException e) {
          // Must have tried to put too many numbers in the array.
          System.err.println("Too many numbers in data file"+filename);
          System.err.println("Processing has been aborted.");
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
         //in.FileOutputPajek("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,0);
         boolean colourOn=true;
         double edgeWidthDisplayMax=1.0;
         in.FileOutputPajek("#", edgeWidthDisplayMax, colourOn);
         final String[] pn =
            {"Value", "Weight", "PageRank", "Strength", "StrengthIn", "StrengthOut"};
         for (int i=0; i<pn.length; i++) in.FileOutputPajekSiteFiles(pn[i]);
         //in.FileOutputBareNetwork(SEP);
         //in.FileOutputNetworkStatistics("#",3);
         edgeWidthDisplayMax=10.0;
         in.FileOutputGraphMLNetwork("", edgeWidthDisplayMax, colourOn);
         return;

    } // eo oneNetworkFileOutput

      /** 
     * Gives fileoutputs for individual runs
       * <br>Problems with size of image. 
     * @param run number of run
     * @param in island network for which output are needed
     */
    public void oneNetworkJpegOutput(int run, islandNetwork in) 
    {
        
//        System.out.println("Attempting to write jpeg image to "+ in.outputFile.nameRoot);
        ClusteringWindow cw = new ClusteringWindow();
        cw.initialiseClusteringWindow(in, 600);
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
              