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
import java.awt.Dimension;

import JavaNotes.TextReader;
import TimUtilities.Range;
import TimUtilities.StatisticalQuantity;
//import IslandNetworks.ClusteringWindow;

/**
 * Runs <tt>islandNetwork</tt> multiple times.
 * @author time
 */
public class MultipleAnalysis 
{
    String MAVERSION="MA080406";
    final String SEP = "\t";
    int nruns;
    int nrunsmax;
    int nentries;
    int noprentries;
    int nfixedparameters=5;
    int noutputvariables=8;//4+temp.edgeSet.numberMetrics;
        
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

    
    /** Creates a new instance of MultipleAnalysis */
    public MultipleAnalysis() 
    {
            firstLine="";
            parameterString= new String [100]; // maximum 100 parameters
            islandNetwork in = new islandNetwork(1); 
            noutputvariables=4+in.edgeSet.numberMetrics;
            j=new Range(in.Hamiltonian.vertexSource);
            mu = new Range(in.Hamiltonian.edgeSource);
            kappa = new Range(in.Hamiltonian.kappa);
            lambda = new Range(in.Hamiltonian.lambda);
            distScale = new Range(in.Hamiltonian.distanceScale);
            repeat =1;
            runOffset=in.outputFile.sequenceNumber;
        
   }
    
/**
     * Runs island networks for multiple parameters values.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // use dummy initial network to set up and store parameters
        islandNetwork a = new islandNetwork(1); // dummy network with dfault values
        int res = a.Parse(args);
        if (res>0) {System.out.println("Command line argument failure - return code "+res);
            return;
            }
        res = a.getSiteData();
        System.out.println("Data reading OK, number of sites is "+a.numberSites);

        a.setInfomationLevel(-1); // switch off outputs
        
            MultipleAnalysis ma = new MultipleAnalysis();
            ma.readVariableRanges(a.inputFile.getFullLocationFileRoot());
            ma.calcRange(a);
            // ??????????????????????????????????????????????????????????????????????????????
            a.outputFile.setParameterName("v"+a.modelNumber.major+"_"+a.modelNumber.minor);
            ma.FileOutputStatistics(a.outputFile);
 
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
        
        runOffset=a.outputFile.sequenceNumber;
  
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
        double betaInitial=a.Hamiltonian.beta;
        a.autoSetOutputFileName=true;
                                
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
                                
                                oneParameterResults[entry++].add(a.siteWeightStats.minimum);
                                oneParameterResults[entry++].add(a.siteWeightStats.maximum);
                                oneParameterResults[entry++].add(a.siteWeightStats.getAverage());
                                oneParameterResults[entry++].add(a.siteWeightStats.getSigma());
                                
                                oneParameterResults[entry++].add(a.siteStrengthInStats.minimum);
                                oneParameterResults[entry++].add(a.siteStrengthInStats.maximum);
                                oneParameterResults[entry++].add(a.siteStrengthInStats.getAverage());
                                oneParameterResults[entry++].add(a.siteStrengthInStats.getSigma());
                                
                                oneParameterResults[entry++].add(a.edgeValueStats.minimum);
                                oneParameterResults[entry++].add(a.edgeValueStats.maximum);
                                oneParameterResults[entry++].add(a.edgeValueStats.getAverage());
                                oneParameterResults[entry++].add(a.edgeValueStats.getSigma());
                                
                                oneParameterResults[entry++].add(a.edgeWeightStats.minimum);
                                oneParameterResults[entry++].add(a.edgeWeightStats.maximum);
                                oneParameterResults[entry++].add(a.edgeWeightStats.getAverage());
                                oneParameterResults[entry++].add(a.edgeWeightStats.getSigma());
                                
                                for (int iii=1; iii<a.edgeSet.numberMetrics; iii++) {
                                    a.edgeSet.metricNumber=iii;
                                    a.calcNetworkSeparations();
                                    oneParameterResults[entry++].add(a.siteDistanceStats.minimum);
                                    oneParameterResults[entry++].add(a.siteDistanceStats.maximum);
                                    oneParameterResults[entry++].add(a.siteDistanceStats.getAverage());
                                    oneParameterResults[entry++].add(a.siteDistanceStats.getSigma());
                                }// eo for iii

                                
                                } catch (Exception e)
                                {
                                    System.out.println("*** Problem with run "+r+" - exception "+e);
                                }
                                
                                
                                // do outputs for this one run
                                try{if (r==0) oneNetworkFileOutput(r, a, true) ;
                                    else oneNetworkFileOutput(r, a, false) ;}
                                catch (Exception e){ System.out.println("*** Problem with general network file outputs, run "+r+" - exception "+e);}
                                
                                try{
                                    oneNetworkJpegOutput(r,a);}
                                catch (Exception e){ System.out.println("*** Problem with jpeg output run "+r+" - exception "+e);}
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
                                
                                for (int iii =1; iii<5; iii++) {
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry].getAverage();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getError();
                                    results[nruns][entry++]=oneParameterResults[oprentry++].getAverage();
                                }
                                
                                for (int iii=1; iii<a.edgeSet.numberMetrics; iii++) {
                                    a.edgeSet.metricNumber=iii;
                                    a.calcNetworkSeparations();
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
     *  <br> <emph>nameroot</emph>_info.dat general info
     * @param fl filelocation used to form file names.
     */
    public void FileOutputStatistics(FileLocation fl) 
    {
        
       // String SepString = "\t";
        String filenamecomplete =  fl.getRootDirectoryPath()+fl.getNameRoot()+"_MAstats.dat";        
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
     *  <br> <emph>nameroot</emph>_info.dat
     * @param PS PrintSteam such as System.out
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
        cw.initialiseClusteringWindow(in, new Dimension(1000,1000));
        cw.setUpPrintableView(600,600);
        cw.writeJPEGImage(); 
        return;

    } // eo oneNetworkFileOutput
  
    
}
              