/*
 * islandNetwork.java
 *
 * Created on 01 April 2004, 14:14
 * last update Monday, June 28, 2004 at 19:10
 * Requires TextReader.java - see David Eck, javanotes4.pdf, ch10, p402,
 * @author  time
 */

    
import java.io.*;
import java.lang.*;
import java.lang.Math.*;
import java.util.Random;
import javax.swing.*;          
import java.awt.*;
import java.awt.event.*;


//import java.io.File;
//import java.io.FileInputStream;
//import cern.colt.io.Converting;
//import TextReader;



    
    
    
/**
 * @author Tim Evans
 * <tt>islandNetwork</tt> is a class used to represent an island network.  
 * The files, input and output, have names built from the root of 
 * <tt>inputnameroot</tt> and <tt>outputnameroot</tt>.
 * <tt>numberSites</tt> is the number of sites.
 * Each site is a vertex and carries a site value <tt>double [] siteValue[numberSites]</tt>
 * which is a fixed characteristic size for the site.  
 * The name of the site is <tt>String siteName[i]</tt>.
 * The vertex also has a variable value <tt>double vertexValue[numberSites]</tt>
 * which should have universal values between 0 and 1 typically.
 * The edges exist between all sites.  They have a fixed characteristic, the distance
 * <tt>double distValue[numberSites][numberSites]</tt> which ought to be symmetric.
 * They then have a variable value which is strictly in [0,1) but need not be symmetric
 * <tt>double edgeValue[numberSites][numberSites]</tt> <tt>edgeValue[i][j]</tt> represents the
 * value of the edge from site i to site j.
 * The Hamiltonian coefficients are
 *       <tt>double edgeSourceH</tt>
 *       <tt>double vertexSourceH</tt>
 *       <tt>double alphaH</tt>
 *       <tt>double betaH</tt>
 *       <tt>double gammaH</tt>
 *       <tt>double kappaH</tt>
 *       <tt>double lambdaH</tt>
 *       <tt>double distScaleH</tt>
 * There are also records of the updates
 * <tt>updateRecord edgeUR</tt> and <tt>updateRecord vertexUR</tt>
 * Finally there is a variable controling information output <tt>int infolevel</tt>
 * and a variable controling the update mode <tt>int updateMode</tt>
*/
public class islandNetwork {    
      
       
        //Specify the look and feel to use.  Valid values:
        //null (use the default), "Metal", "System", "Motif", "GTK+"
        final static String LOOKANDFEEL = "System";
 
        String inputnameroot;
        String outputnameroot;
        
        int numberSites = 100;
        double[][] distValue = new double [numberSites][numberSites]; // fixed characteristic size
        double[][] edgeValue = new double [numberSites][numberSites];  // variable size
        double [] siteValue = new double [numberSites]; // fixed characteristic size
        double [] vertexValue = new double [numberSites]; // variable size
        String [] siteName = new String [numberSites];
        double [] siteX = new double [numberSites];
        double [] siteY = new double [numberSites];
        double [] siteZ = new double [numberSites];
        double minX=0;
        double maxX=0;
        double minY=0;
        double maxY=0;
        int windowSize = 500;
        double windowScale;
        
        int[][] edgeColour = new int [numberSites][numberSites];  // variable size
        int [] siteColour = new int [numberSites]; // variable size 
        // Now Hamiltonian coefficients
        double edgeSourceH;
        double vertexSourceH;
        double alphaH ;
        double betaH ;
        double gammaH ;
        double kappaH ;
        double lambdaH ;
        double distScaleH ;
        double outputcoeffH;
        double consumptioncoeffH;
        // Network Statistics
//        double allmaxvertexweight=-1.0;
        double allmaxedgeweight=-1.0;
        int allmaxedgeweightindex = -1;
        double maxvertexweight =0;
        int maxvertexweightindex=-1;
        double maxsitesize =0;
        int maxsitesizeindex = -1;
        int maxvedgeweightindex = 0;  
        
        
        double [] maxEdgeWeight = new double [numberSites]; 
        int [] maxEdgeWeightIndex = new int [numberSites]; 
        double [] [] distance  = new double [numberSites][numberSites];
//        int DykstraVertex = -1;
        double DykstraMaxDist;
        // update records
        updateRecord edgeUR = new updateRecord() ;
        updateRecord vertexUR= new updateRecord() ;
        // General parameters
        int infolevel;
        int updateMode;
        boolean edgeModeBinary;
        double edgeMode;
        int modelNumber;
        int maxNumberColours=100;
        int numberColours=100;
        double DisplayMaxVertexScale=0; //<=0 then display vertices relative to largest
                                        // else display vertices relative to this size       
        double DisplayMaxEdgeScale=0;   //<=0 then display edges relative to largest
                                        // else display edges relative to this size       

        String[] pajekColour = new String[maxNumberColours];
        String[] pajekGrey = new String[maxNumberColours];
        Random rnd;
        double MAXDISTANCE = 9999999.9;
        double MAXH = 1e6;



    
    /** Creates a new instance of network */
    public islandNetwork() {
            for (int i =0; i<numberSites; i++) {
                siteValue[i]=1.0;
                vertexValue[i]=0.5;
                for (int j =0; j<numberSites; j++) {        
                    if (i==j) {distValue[i][j]=0.0; edgeValue[i][j]=0.0;}
                    else { distValue[i][j]=1.0; edgeValue[i][j]=0.5;};
                };
            };
            setColours();
            if (infolevel>2)
            {
             for (int i =0; i<=numberColours; i++)
              System.out.println(i+":  "+pajekColour[i]+" , "+pajekGrey[i]);
            };
            inputnameroot="testsimple";
            outputnameroot="testsimple";
            // Now Hamiltonian coefficients
             edgeSourceH = 0.0; //doubles as the number of edges per site in PPA mode
             vertexSourceH = 0.0;
             betaH = 1.0;  
             alphaH = 4.0;
             gammaH = 1.0;
             kappaH = 1.0 ;
             lambdaH = 1.0 ;
             distScaleH = 100;
             outputcoeffH=1.0;
             consumptioncoeffH=2.0*outputcoeffH;
           // update records zeroed on construction
             // general parameters
             modelNumber = 1;
             infolevel = 0;
             updateMode = 1; // Sweep
             edgeModeBinary = false;
             edgeMode =1.0;
             DykstraMaxDist = MAXDISTANCE;
             rnd = new Random(); // Give long integer arg else uses time to set seed
    }//eo constructor
    

    
    
    /** Creates a new instance of network */
    public islandNetwork(int ns) {
            numberSites = ns;
        distValue = new double [numberSites][numberSites]; // fixed characteristic size
        edgeValue = new double [numberSites][numberSites];  // variable size
        siteValue = new double [numberSites]; // fixed characteristic size
        vertexValue = new double [numberSites]; // variable size
        siteName = new String [numberSites];
    }

    
        /** Constuctor sets Parameters from command line arguments
         * @param String[] ArgList[] array of strings containing -?<value>
         */       
       public int Parse(String[] ArgList){
                
                for (int i=0;i< ArgList.length ;i++){
                    if (ArgList[i].length() <2) {
                        System.out.println("\n*** Argument "+i+" is too short");
                        infolevel=-1;
                        return 1;};
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** Argument "+i+" does not start with -");
                            infolevel=-2;
                            return 2;};
                            switch (ArgList[i].charAt(1)) {
                                case 'a': {alphaH = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'b': {betaH = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'e': {
                                    edgeMode = Double.parseDouble(ArgList[i].substring(2));
                                    if (edgeMode==0) edgeModeBinary = true;
                                    else edgeModeBinary = false;
                                break;}
                                case 'f': {if (ArgList[i].charAt(2)=='i') inputnameroot = ArgList[i].substring(3);
                                           if (ArgList[i].charAt(2)=='o') outputnameroot = ArgList[i].substring(3);
                                            
                                break;}
                                case 'g': {gammaH = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'i': {infolevel = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'j': {vertexSourceH = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'k': {kappaH = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'l': {lambdaH = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'm': {edgeSourceH = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 's': {distScaleH = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'u': {updateMode = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'v': {modelNumber = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case '?': {Usage(); infolevel=-4;
                                return 4;}
                                
                                default:{
                                    System.out.println("\n*** Argument "+i+" not known, usage:");
                                    Usage();
                                    infolevel=-3;
                                    return 3;
                                }
                                
                            }
                }
                return 0;
            } // eo ParamParse

        /** Shows command line arguments
         */
            public void Usage(){
                
                islandNetwork i = new islandNetwork();
                System.out.println("...............................................................................");
                System.out.println("Usage: ");
                System.out.println("aegean <options> ");
                System.out.println(" where options are -<char><value> separated by space as follows ");
                System.out.println("  -fi<inroot>  Sets root of input  files to be  inroot, default "+i.inputnameroot);
                System.out.println("  -fo<outroot> Sets root of output files to be outroot, default "+i.outputnameroot);
                System.out.println("  -j<j>    Sets j, sum over all vertex weights, default "+i.vertexSourceH);
                System.out.println("  -m<mu>    Sets mu, sum over all edge weights, default "+i.edgeSourceH);
                System.out.println("  -a<alpha>    Sets alpha, short distnce power in edge potential, default "+i.alphaH);
                System.out.println("  -b<beta>    Sets beta, inverse temperature, default "+i.betaH);
                System.out.println("               Also number of edges per site in PPA mode");
                System.out.println("  -g<gamma>    Sets gamma, short distance power in edge potential, default "+i.gammaH);
                System.out.println("  -k<kappa>    Sets kappa, coefficent of vertex potential, default "+i.lambdaH);
                System.out.println("  -l<lambda>    Sets lambda, coefficent of edge potential, default "+i.lambdaH);
                System.out.println("  -s<distanceScale>    Sets distance scale, "+i.distScaleH);
                System.out.println("  -e<edgeMode>      Sets edge max edge value or binary (0 or 1) if 0, default "+i.edgeMode);
                System.out.println("  -v<modelNumber>      Sets model number to use, default "+i.modelNumber);
                System.out.println("  -i<infolevel>      Sets information level, 0 lowest, default "+i.infolevel);
                System.out.println("  -u<updateMode>      Sets update mode, 0 PPA, 1 MC, default "+i.updateMode);
                System.out.println("...............................................................................");
                
            } //eo usage
    
    
// .............................................................    
    /** Shows distance data table
     * 
     */
     public void showDistanceValues() {
        String SepString = "\t";
        int dec = 3;
        System.out.println("--- ABSOLUTE DISTANCES -------------");
        for (int i =0; i<numberSites; i++) {        
            System.out.print(SepString+siteName[i]);
            for (int j =0; j<numberSites; j++) {        
            System.out.print(SepString+TruncDec(distValue[i][j],dec));
            };
            System.out.println();
        };
        
    }//eo showData

// .............................................................    
    /** Shows scaled distance data table
     * 
     */
     public void showScaledDistanceValues() {
        String SepString = "\t";
        int dec = 3;
        double sdv=0.0;
        System.out.println("--- SCALED DISTANCES -------------");
        for (int i =0; i<numberSites; i++) {        
            System.out.print(SepString+siteName[i]);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(distValue[i][j]/distScaleH,dec);        
            System.out.print(SepString+sdv);
            };
            System.out.println();
        };
        
    }//eo showData



// .............................................................    
    /** Shows distance data table in terms of potential
     * 
     */
     public void showPotentialDistanceValues() {
        String SepString = "\t";
        int dec = 3;
        double sdv=0.0;
        System.out.println("--- POTENTIAL SCALE (edge value 1.0) ------");
        for (int i =0; i<numberSites; i++) {        
            System.out.print(SepString+siteName[i]);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(edgePotential1(distValue[i][j], 1.0),dec);        
            System.out.print(SepString+sdv);
            };
            System.out.println();
        };
        
    }//eo showData


// .............................................................    
    /** Shows distance data table in terms of dH values
     * 
     */
     public void showdHValues(double ev) {
        String SepString = "\t";
        int dec = 3;
        double sdv=0.0;
        System.out.println("--- dH Values for edge value 1.0 ------");
        for (int i =0; i<numberSites; i++) {        
            System.out.print(SepString+siteName[i]);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(deltaEdgeHamiltonian(i, j, ev),dec);        
            System.out.print(SepString+sdv);
            };
            System.out.println();
        };
        
    }//eo showData

// .............................................................
     /** Shows edge value table
     * 
     */
     public void showEdgeValues() {
        String SepString = "\t";
        int dec=3;
        double [] toedgecount = new double[numberSites];
        double fromedgecount;
        System.out.println("--- Edge Values --------------------------------");
        System.out.print("From/to");
        for (int i =0; i<numberSites; i++) {toedgecount[i]=0;}
        for (int i =0; i<numberSites; i++) {System.out.print(SepString+siteName[i]);}; 
        System.out.println(SepString+"Tot From");       
        double totaldistance=0;
        double totalweighteddistance=0;
        for (int i =0; i<numberSites; i++) {        
            fromedgecount=0;
            System.out.print(siteName[i]);
            for (int j =0; j<numberSites; j++) {
            if(edgeValue[i][j]>0) totaldistance+=distance[i][j];
            totalweighteddistance+=distance[i][j]*edgeValue[i][j];
            fromedgecount+=edgeValue[i][j];    
            toedgecount[j]+=edgeValue[i][j]; 
            System.out.print(SepString+TruncDec(edgeValue[i][j],dec));
            };
            System.out.println(SepString+TruncDec(fromedgecount,dec));
        };
        System.out.print("Tot to");
        double totaledges=0;
        for (int i =0; i<numberSites; i++) 
        {
            totaledges+=toedgecount[i];
            System.out.print(SepString+TruncDec(toedgecount[i],dec));
        };
        System.out.println(SepString+TruncDec(totaledges,dec));
        System.out.println("Tot Dist"+SepString+TruncDec(totaldistance,dec));
        System.out.println("Tot W.Dist"+SepString+TruncDec(totalweighteddistance,dec));
     }//eo showEdgeValues

     

     /** Shows colour data values
     * 
     */
     public void showColourValues() {
        String SepString = "\t";
        System.out.println("--- Colour Values ------------------------------");
        System.out.print("From/to");
        for (int i =0; i<numberSites; i++) {System.out.print(SepString+siteName[i]);}; 
        System.out.println();       
        System.out.print("S.Col.");
        for (int i =0; i<numberSites; i++) {System.out.print(SepString+siteColour[i]);}; 
        System.out.print("\n\nFrom/to");
        for (int i =0; i<numberSites; i++) {System.out.print(SepString+siteName[i]);}; 
        System.out.println();       
        for (int i =0; i<numberSites; i++) {        
            System.out.print(siteName[i]);
            for (int j =0; j<numberSites; j++) {        
            System.out.print(SepString+edgeColour[i][j]);
            };
            System.out.println();
        };
     }//eo showColourValues

         
     
     
     /** Shows site positions and value (fixed data)
     * 
     */
     public void showSiteValuesPos() {
        String SepString = "\t";
        System.out.println("#"+SepString+"Name"+SepString+"Value"+SepString+"X"+SepString+"Y"+SepString+"Z");
        for (int i =0; i<numberSites; i++) 
        {
            System.out.println(i+SepString+siteName[i]+SepString+siteValue[i]+SepString+siteX[i]+SepString+siteY[i]+SepString+siteZ[i]);
        }; 
        System.out.println("#"+SepString+"MIN"+SepString+" "+SepString+minX+SepString+minY+SepString+" ");
        System.out.println("#"+SepString+"MAX"+SepString+" "+SepString+maxX+SepString+maxY+SepString+" ");
        
    }//eo showEdgeValues


        
    /** Reads in distance data from file <inputnameroot>_dist.dat
     * 
     */
    public int getDistanceData() {

        
      String filename = new String();
      filename = inputnameroot+"_dist.dat";
      System.out.println("Starting to read distance data from " + filename);
      TextReader data;     // Character input stream for reading data.
      
//      double[] number = new double[1000];  // An array to hold all
                                           //   the numbers that are
                                           //   read from the file.

      int numberCt;  // Number of items actually stored in the array.
      
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         System.out.println("Can't find file "+filename);
         return 1;  
      }

      try {
      
          // Read the data from the input file.
          
          numberCt = 0;
          while (data.eoln() == false) {  // Read until end-of-file.
             siteName[numberCt] = data.getWord();
//             System.out.println("siteName["+numberCt+"] = "+siteName[numberCt]);                     
             numberCt++;
          }
          numberSites=numberCt;
          int siteFrom,siteTo;
          siteFrom=0;
          while (data.eof() == false) {  // Read until end-of-file.
              siteTo=0;
              data.getWord(); // first entry is name
              while (data.eoln() == false) {  // Read until end-of-file.
              distValue[siteFrom][siteTo] = data.getDouble();
              siteTo++;
              if (siteTo!=numberSites) System.out.println("Wrong number of distances to in site "+siteFrom);
              } //eoln
              siteFrom++;              
          }//eofile
          if (siteFrom!=numberSites) System.out.println("Wrong number of distances from in "+filename);
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





    /** Reads in site values, positions and distance data from file <inputnameroot>_svpdist.dat
     * First line site names
     * Second line site Values (fixed sizes)
     * Thrid line site X positions
     * Fourth line site Y positions
     * Remaining lines are the dist[i][j] data
     */
    public int getSiteDistanceData() {

        
      String filename = new String();
      filename = inputnameroot+"_svpdist.dat";
      System.out.println("Starting to read site values, positions and distance data from " + filename);
      TextReader data;     // Character input stream for reading data.
      
      int numberCt;  // Number of items actually stored in the array.
      
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         System.out.println("Can't find file "+filename);
         return 1;  
      }

      try {
      
          // Read the data from the input file.
          // read in first line - site names          
          numberCt = 0;
          String tempstring = data.getWord(); //ignore first item, labels row
          if (infolevel>0) System.out.println("next word is "+tempstring);
          while (data.eoln() == false) 
          {  // Read until end-of-line.
             siteName[numberCt] = data.getWord();
             if (infolevel>0) System.out.println("siteName["+numberCt+"] = "+siteName[numberCt]);                     
             numberCt++;
          }
          numberSites=numberCt;

          //read in second line site values
          tempstring = data.getWord(); //ignore first item, labels row
          if (infolevel>0) System.out.println("next word is "+tempstring);
          numberCt = 0;
          while (data.eoln() == false) 
          {  // Read until end-of-file.
             siteValue[numberCt] = data.getDouble();
             if (infolevel>0) System.out.println("siteValue["+numberCt+"] = "+siteValue[numberCt]);                     
             numberCt++;
          }
          if (numberCt!=numberSites) System.out.println("Wrong number of site values in "+filename+" ("+numberCt+" , "+numberSites+")");

          //read in third line site X positions
          numberCt = 0;
          tempstring = data.getWord(); //ignore first item, labels row
          if (infolevel>0) System.out.println("next word is "+tempstring);
          while (data.eoln() == false) 
          {  // Read until end-of-file.
             siteX[numberCt] = data.getDouble();
             if (numberCt<1) { maxX=siteX[numberCt]; minX=siteX[numberCt];}
             else { 
                  if (siteX[numberCt]>maxX) maxX=siteX[numberCt];
                  if (siteX[numberCt]<minX) minX=siteX[numberCt];
                  }   
             if (infolevel>0) System.out.println("siteX["+numberCt+"] = "+siteX[numberCt]);                     
             numberCt++;
          }
          if (numberCt!=numberSites) System.out.println("Wrong number of site X pos in "+filename+" ("+numberCt+" , "+numberSites+")");
          
          //read in fourth line site X positions
          numberCt = 0;
          tempstring = data.getWord(); //ignore first item, labels row
          if (infolevel>0) System.out.println("next word is "+tempstring);
          while (data.eoln() == false) 
          {  // Read until end-of-file.
             siteY[numberCt] = data.getDouble();
             if (numberCt<1) { maxY=siteY[numberCt]; minY=siteY[numberCt];}
             else { 
                  if (siteY[numberCt]>maxY) maxY=siteY[numberCt];
                  if (siteY[numberCt]<minY) minY=siteY[numberCt];
             }
             if (infolevel>0) System.out.println("siteY["+numberCt+"] = "+siteY[numberCt]);                     
             numberCt++;
          }
          if (numberCt!=numberSites) System.out.println("Wrong number of site Y pos in "+filename+" ("+numberCt+" , "+numberSites+")");
          
          if (infolevel>0) System.out.println("Min/Max X then Y positions:"+minX+", "+maxX+", "+minY+", "+maxY);
          
          int siteFrom,siteTo;
          siteFrom=0;
          while (data.eof() == false) 
          {  // Read until end-of-file.
              siteTo=0;
              data.getWord(); // first entry is name
              while (data.eoln() == false) 
              {  // Read until end-of-file.
                 distValue[siteFrom][siteTo] = data.getDouble();
                 siteTo++;
              } //eoln
              if (siteTo!=numberSites) System.out.println("Wrong number of distances to in site "+siteFrom);
              siteFrom++;              
          }//eofile
          if (siteFrom!=numberSites) System.out.println("Wrong number of distances from in "+filename);
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

// -------------------------------------------------------------------------
    /** Reads in site values and position data from file <inputnameroot>_site.dat
     * First line site names
     * Second line site Values (fixed sizes)
     * Third line site X positions
     * Fourth line site Y positions
     * Calculates dist[i][j] data
     */
    public int getSiteData() {

        
      String filename = new String();
      filename = inputnameroot+"_site.dat";
      System.out.println("Starting to read site values and position data from " + filename);
      TextReader data;     // Character input stream for reading data.
      
      int numberCt;  // Number of items actually stored in the array.
      
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         System.out.println("Can't find file "+filename);
         return 1;  
      }

      try {
      
          // Read the data from the input file.
          // read in first line - site names          
          numberCt = 0;
          String tempstring = data.getWord(); //ignore first item, labels row
          if (infolevel>0) System.out.println("next word is "+tempstring);
          while (data.eoln() == false) 
          {  // Read until end-of-line.
             siteName[numberCt] = data.getWord();
             if (infolevel>0) System.out.println("siteName["+numberCt+"] = "+siteName[numberCt]);                     
             numberCt++;
          }
          numberSites=numberCt;

          //read in second line site values
          tempstring = data.getWord(); //ignore first item, labels row
          if (infolevel>0) System.out.println("next word is "+tempstring);
          numberCt = 0;
          while (data.eoln() == false) 
          {  // Read until end-of-file.
             siteValue[numberCt] = data.getDouble();
             if (infolevel>0) System.out.println("siteValue["+numberCt+"] = "+siteValue[numberCt]);                     
             numberCt++;
          }
          if (numberCt!=numberSites) System.out.println("Wrong number of site values in "+filename+" ("+numberCt+" , "+numberSites+")");

          //read in third line site X positions
          numberCt = 0;
          tempstring = data.getWord(); //ignore first item, labels row
          if (infolevel>0) System.out.println("next word is "+tempstring);
          while (data.eoln() == false) 
          {  // Read until end-of-file.
             siteX[numberCt] = data.getDouble();
             if (numberCt<1) { maxX=siteX[numberCt]; minX=siteX[numberCt];}
             else { 
                  if (siteX[numberCt]>maxX) maxX=siteX[numberCt];
                  if (siteX[numberCt]<minX) minX=siteX[numberCt];
                  }   
             if (infolevel>0) System.out.println("siteX["+numberCt+"] = "+siteX[numberCt]);                     
             numberCt++;
          }
          if (numberCt!=numberSites) System.out.println("Wrong number of site X pos in "+filename+" ("+numberCt+" , "+numberSites+")");
          if (infolevel>0) System.out.println("Min/Max X then Y positions:"+minX+", "+maxX+", "+minY+", "+maxY);
          //read in fourth line site X positions
          numberCt = 0;
          tempstring = data.getWord(); //ignore first item, labels row
          if (infolevel>0) System.out.println("next word is "+tempstring);
          while (data.eoln() == false) 
          {  // Read until end-of-file.
             siteY[numberCt] = data.getDouble();
             if (numberCt<1) { maxY=siteY[numberCt]; minY=siteY[numberCt];}
             else { 
                  if (siteY[numberCt]>maxY) maxY=siteY[numberCt];
                  if (siteY[numberCt]<minY) minY=siteY[numberCt];
             }
             if (infolevel>0) System.out.println("siteY["+numberCt+"] = "+siteY[numberCt]);                     
             numberCt++;
          }
          if (numberCt!=numberSites) System.out.println("Wrong number of site Y pos in "+filename+" ("+numberCt+" , "+numberSites+")");
                    
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
       
       // calculate site distances from X Y positions
       for (int i =0; i<numberSites; i++) 
       {        
            for (int j =0; j<numberSites; j++) 
            {
                distValue[i][j]= Math.sqrt((siteX[i]-siteX[j])*(siteX[i]-siteX[j])
                                          +(siteY[i]-siteY[j])*(siteY[i]-siteY[j]) );
            };
            };
       
       return 0;
    }  // end of getSiteData() method





        /** Sets Parameters of Hamiltonian
         * @param double j is source for sum of ste values
         * @param double mu is source for sum of edge values
         * @param double beta is inverse temperature
         * @param double alpha is scale for short edge scales
         * @param double gamma beta&*gamme is power for long edge scales
         * @param double distScale scale for edge potential 
         */
    public void setHamiltonianParameters(double j, double mu, double b, double a, double k,  double g, double l, double ds) {
             edgeSourceH = mu;
             vertexSourceH = j;
             betaH = b;
             alphaH = a;
             gammaH = g;
             kappaH = k;
             lambdaH = l;
             distScaleH = ds;

    }//eo setHam

        /** Shows Parameters of Hamiltonian
         */
    public void showHamiltonianParameters() {
             String SepString = "\t"; // Separation Character for column indication
             System.out.println("--- Parameters for Hamiltonian Model number"+SepString+modelNumber );
             System.out.println("  edgeSource (mu)"+SepString+edgeSourceH );
             System.out.println("vertexSource (j) "+SepString+vertexSourceH);
             System.out.println("            beta "+SepString+betaH);
             System.out.println("           alpha "+SepString+alphaH);
             System.out.println("           gamma "+SepString+gammaH);
             System.out.println("           kappa "+SepString+kappaH);
             System.out.println("          lambda "+SepString+lambdaH);
             System.out.println("       edgeScale "+SepString+distScaleH);
             System.out.println("        edgeMode "+SepString+edgeMode);
             System.out.println("  edgeModeBinary "+SepString+edgeModeBinary);

    }//eo showHam

    
        /** Shows Network Statistics
         */
    public void showNetworkStatistics() {
        String SepString = "\t"; // Separation Character for column indication
        int dec = 3;
        double totVertexWeight=0;
        double totEdgeWeight=0;
        double ew,vw;
        double[] inEdgeWeight =  new double[numberSites];
        double[] outEdgeWeight =  new double[numberSites];
        
        calcLargestStats(0.0);         
        
        System.out.println(" *** Weights *************************** ");
        System.out.print("Name"+SepString+"S_W");        
        // site names on line 1
        for (int i=0; i<numberSites; i++)
        { 
            System.out.print(SepString+siteName[i]);
            if (i==maxsitesizeindex) System.out.print("#");
            else System.out.print(" ");         
        
        }
        System.out.println(SepString+"E_W ");       
        
        // site values (fixed) on line 2
        System.out.print("S_V"+SepString+" ");            
        for (int i=0; i<numberSites; i++)
        { 
            System.out.print(SepString+TruncDec(siteValue[i],dec));            
        }
        System.out.println();
        
        // site X pos
        System.out.print("S_X"+SepString+" ");            
        for (int i=0; i<numberSites; i++)
        { 
            System.out.print(SepString+siteX[i]);            
        }
        System.out.println();
        
        // site Y pos
        System.out.print("S_Y"+SepString+" ");            
        for (int i=0; i<numberSites; i++)
        { 
            System.out.print(SepString+siteY[i]);            
        }
        System.out.println();
        
        // vertex values (variable) on line 3
        System.out.print("V_V"+SepString+" ");            
        for (int i=0; i<numberSites; i++)
        { 
            System.out.print(SepString+TruncDec(vertexValue[i],dec));            
        }
        System.out.println();
        
        // edge weights
        for (int i=0; i<numberSites; i++){ outEdgeWeight[i]=0;};
        for (int i=0; i<numberSites; i++)
        {
           inEdgeWeight[i]=0;
           vw=vertexValue[i]*siteValue[i];
           totVertexWeight+=vw;
           System.out.print(siteName[i]);
           if (i==maxvertexweightindex) System.out.print("*");
           else System.out.print(" "); 
           System.out.print(SepString+TruncDec(vw,dec));            
               
           for (int j=0; j<numberSites; j++)
           {
               ew = vertexValue[i]*siteValue[i]*edgeValue[i][j];
               System.out.print(SepString+TruncDec(ew,dec));
               inEdgeWeight[i]+=ew;
               outEdgeWeight[j]+=ew;
           }
           System.out.println(SepString+TruncDec(inEdgeWeight[i],dec)+SepString+siteName[i]);              

           totEdgeWeight+=inEdgeWeight[i];
            
        }
        System.out.print("Tot_W"+SepString+TruncDec(totVertexWeight,dec));        
        for (int i=0; i<numberSites; i++)
        { 
                System.out.print(SepString+TruncDec(outEdgeWeight[i],dec));
        };
        System.out.println(SepString+TruncDec(totEdgeWeight,dec));         
                
    } //eo showNet.Stats

// .................................................................
    /** Shows Network Statistics
         */
    public void showDykstraValues() 
    {
        int dec=3;
        String SepString = "\t"; // Separation Character for column indication
        
        System.out.println("--- Dykstra Results ");                  
        if (DykstraMaxDist<MAXDISTANCE ) System.out.println("Connected, max distance "+ TruncDec(DykstraMaxDist,dec));                  
        else System.out.println("DISCONNECTED");    
        double totaldistance =0;
        // site names on line 1
        System.out.print("From/To");        
        for (int i=0; i<numberSites; i++)
        { 
            System.out.print(SepString+siteName[i]);
//            if (i==DykstraVertex) System.out.print("#");
//            else System.out.print(" ");         
        }
        System.out.println();        
        // Dykstra values on line 2
        for (int i=0; i<numberSites; i++)
        {
           System.out.print(siteName[i]);        
           for (int j=0; j<numberSites; j++)
           {
               
            if (distance[i][j]==MAXDISTANCE) System.out.print(SepString+" x ");
            else 
            {
                totaldistance+=distance[i][j];
                System.out.print(SepString+TruncDec(distance[i][j],dec));
            };
           }
        System.out.println();        
        }
        System.out.println("\n Total Distance (excl. x) "+totaldistance);
        System.out.println(" .................................................");        
        
    }    
    
// *******************************************************************************    
    
         /** Calculate some Network Statistics
          * Sets maxvertexweight is the largest vertex weight, 
          *  and comes from vertex maxvertexweightindex.
          * maxEdgeWeight[i] is largest edge weight if vertex i and
          * allmaxedgeweight is the largest overall
          *  @param minColourFraction is the minimum fraction needed for colour to be given
         */
         
    public void calcLargestStats(double minColourFraction) 
    {
      double vw=0;
      double ew=0;
//      allmaxvertexweight = 0;
      allmaxedgeweight =0;
      allmaxedgeweightindex =0;
      maxvertexweightindex = 0;
      maxvertexweight =0;
      maxsitesize =0;
      maxvedgeweightindex = 0;  
      
        // site and edge weights
        for (int i=0; i<numberSites; i++)
        {
           vw=vertexValue[i]*siteValue[i]; // siteName[i] has weight vw            
           if (vw > maxvertexweight) 
           {
              maxvertexweight = vw;
              maxvertexweightindex = i;
           }

           // find largest edge weight for i
           maxEdgeWeight[i]=0;   
           maxEdgeWeightIndex[i] = 0;
           for (int j=0; j<numberSites; j++)
           {
               ew = vw*edgeValue[i][j]; // edge weight
               if (ew > maxEdgeWeight[i]) 
               {
                 maxEdgeWeight[i] = ew;
                 maxEdgeWeightIndex[i] = j;
               }
           }
           if (maxEdgeWeight[i] > allmaxedgeweight) 
           {
              allmaxedgeweight = maxEdgeWeight[i];
              allmaxedgeweightindex = maxEdgeWeightIndex[i];
           }
           
        }
      
      maxsitesize = 0;
      maxsitesizeindex = 0;
      for (int i=0; i<numberSites; i++)
      {
        if (siteValue[i] > maxsitesize) 
        {
           maxsitesize = siteValue[i];
           maxsitesizeindex = i;
      
        }
      }
      
      
      int minColourNumber =(int)(numberColours*minColourFraction);
      // set site and edge weight colours
      int vwcolour;
      int ewcolour;
      double mvw,amew;
      if (DisplayMaxVertexScale>0) mvw = DisplayMaxVertexScale;
      else mvw= maxvertexweight;
      if (DisplayMaxEdgeScale>0) amew =  DisplayMaxEdgeScale;
      else amew = allmaxedgeweight;

        for (int i=0; i<numberSites; i++)
        {
           siteColour[i] = (int) (0.4999 + vertexValue[i]*siteValue[i]*numberColours/mvw); // siteName[i] has weight vw            
           if (siteColour[i]>numberColours) siteColour[i]=numberColours;
           siteZ[i]=siteColour[i];
           for (int j=0; j<numberSites; j++)
           {
               edgeColour[i][j] = (int) (0.4999 + vertexValue[i]*siteValue[i]*numberColours*edgeValue[i][j]/amew); // edge weight colour 
               if (edgeColour[i][j]<minColourNumber) edgeColour[i][j]=0;
               if (edgeColour[i][j]>numberColours) edgeColour[i][j]=numberColours;
           }           
        }
    }
    
 // -------------------------------------------------------------------
  /**
     * Sets up colours for Pajek 
     */
    void setColours()  
    {
        numberColours=10;
        if (maxNumberColours<numberColours) numberColours=maxNumberColours-1; 
        pajekColour[0] = "White"; // always have this as zero
        pajekColour[1] = "Yellow";
        pajekColour[2] = "Pink";
        pajekColour[3] = "Cyan";
        pajekColour[4] = "Orange";
        pajekColour[5] = "Magenta";
        pajekColour[6] = "Purple";
        pajekColour[7] = "Green";
        pajekColour[8] = "Blue";
        pajekColour[9] = "Brown";
        pajekColour[numberColours] = "Black";
//        pajekColour[1] = "Red";
////        pajekColour[12] = "Gray";
        pajekGrey[0] = "White"; // always have this as zero
        int gn=1;
        Integer GN = new Integer(1);
        String gns;
        for (int i =1; i<numberColours; i++) 
        {
            gn = 5*((int) (0.5+(i*20.0)/((double) numberColours)));
            gns = GN.toString(gn);
            if (gn>90) gns="95";
            if (gn<10) gns = "05";
            pajekGrey[i] = "Gray"+gns;
        };
        pajekGrey[numberColours] = "Black";
        
    }   

// *******************************************************************
        /** Outputs Network Statistics to a tab delimited file
         */
    public void FileOutputNetworkStatistics(String cc) 
    {
        
                
    }//eo FileOutputNet.Stats

// -------------------------------------------------------------------
  /**
     * Outputs network in Pajek file format
     *  <nameroot>.net general info
     * @param cc comment characters put at the start of every line
     * @param int siteSizeFactor Maximum dot size for sites
     * @param int edgeWidthFactor Maximum edge width in diagrams
     * @param double minColourFrac fraction of total colours represented as colour 1
     * @param double zeroColourFrac fraction of total colours represented as colour 0
     * @param BWswitch true for BW picture, false for colour
     */
    void FileOutputNetwork(String cc, int siteSizeFactor, int edgeWidthFactor, 
                           double minColourFrac, double zeroColourFrac, 
                           boolean BWswitch)  {

        double ew,vw;
        
        // Convert X Y locations to 0 - 1 scale for Pajek
        double minX=siteX[0];
        double maxX=siteX[0];
        double minY=siteY[0];
        double maxY=siteY[0];
        for (int i=1; i<numberSites; i++)
        {
            if (siteX[i]>maxX) maxX=siteX[i];
            if (siteX[i]<minX) minX=siteX[i];
            if (siteY[i]>maxY) maxY=siteY[i];
            if (siteY[i]<minY) minY=siteY[i];
        };
        double XOffset = (maxX+minX)/2.0;
        double YOffset = (maxY+minY)/2.0;
        double XScale = (maxX-minX)/0.8;
        double YScale = (maxY-minY)/0.8;
        double Scale = (XScale>YScale) ? XScale : YScale;
        
        String filenamecomplete;
        if (BWswitch) filenamecomplete = outputnameroot+ "BW.net";
        else filenamecomplete = outputnameroot+ "C.net";
        
        System.out.println("Attempting to write to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            
            // Example data from lines.net
            //  2 "box" 0.8188    0.2458    0.5000   box x_fact 5 y_fact 3 fos 15 ic LightCyan lc Blue 
            // the 0.8188    0.2458    0.5000 are coordinates 
            //   with (0,0) top left, (1,1) bottom right

            PS.println("*Vertices      "+numberSites);        
            //      1 "a" ic Pink     bc Black
            int size;
            for (int i=0; i<numberSites; i++)
            { 
              size = (siteSizeFactor*siteColour[i])/numberColours;
              PS.print((i+1)+"  \""+siteName[i]+"\" "
               + (((siteX[i]-XOffset)/Scale)+0.5)+" " + (((siteY[i]-YOffset)/Scale)+0.5)
               +" "+ siteZ[i] + " s_size 1 " + " x_fact "+siteColour[i] + " y_fact "+siteColour[i]);
              if (BWswitch) PS.println(" ic "+pajekGrey[siteColour[i]]+"   bc "+pajekGrey[numberColours]);            
              else PS.println(" ic "+pajekColour[siteColour[i]]+"   bc "+pajekColour[numberColours]);
            }
            
            //for the arcs
            //      1      2       1 c Blue
            // gives an arc between vertex 1 and 2, value 1 colour black
            int arcCount =0;
            for (int i=0; i<numberSites; i++)
            {
               for (int j=0; j<numberSites; j++)
               {  if (edgeColour[i][j]>0) arcCount++;
               }
            };
            int width;
            PS.println("*Arcs    "+arcCount);            
            for (int i=0; i<numberSites; i++)
            {
               vw=vertexValue[i]*siteValue[i];   
               for (int j=0; j<numberSites; j++)
               {  
                  if ((i==j) || (edgeColour[i][j]==0)) continue;
                  if (edgeColour[i][j]<= numberColours*zeroColourFrac) continue;
                  ew = vw*edgeValue[i][j];
                  width = (edgeWidthFactor*edgeColour[i][j])/numberColours;
                  if (edgeColour[i][j]< numberColours*minColourFrac) width = 1;
                  PS.print((i+1)+"  "+(j+1)+"   " + ew + " w "+ width);
                  if (BWswitch) PS.println("  c "+pajekGrey[edgeColour[i][j]]);
                  else PS.println("  c "+pajekColour[edgeColour[i][j]]);
               }
            }

            try
            { 
               fout.close ();
               System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }

/* *******************************************************************************
 * Windowing Routines
 */

     private static void initLookAndFeel() {
        String lookAndFeel = null;

        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } else if (LOOKANDFEEL.equals("GTK+")) { //new in 1.4.2
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                                   + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
                UIManager.setLookAndFeel(lookAndFeel);
            } catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            } catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    }// private static void initLookAndFeel()

     public Component createComponents() {
        
        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        NetworkPicture pane = new  NetworkPicture(windowSize,windowSize); 
        pane.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        30, //bottom
                                        30) //right
                                        );

        return pane;
    }

     private class NetworkPicture extends JPanel {
         int siteSizeFactor = 20;
         int edgeWidthFactor = 5; 
         double minColourFrac = 0.2; 
         double zeroColourFrac = 0.1;
         public NetworkPicture (){
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(400, 400));
             setBackground(Color.cyan);

             //setSize(new Dimension(800, 400));
         }
         public NetworkPicture (int xsize, int ysize){
             setOpaque(true);
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(xsize, ysize));
             setBackground(Color.yellow);

             //setSize(new Dimension(800, 400));
         }
         
         public void paint (Graphics g)
         {   int borderSize =30;
             Dimension d = getSize();
             double wsx = (d.width -borderSize-borderSize) / (maxX-minX);
             double wsy = (d.height -borderSize-borderSize)/(maxY-minY);
             if (wsx < wsy) windowScale = wsx; else windowScale = wsy;
             if (windowScale<0) windowScale =1.0;
             System.out.println("Window Scale "+ windowScale);
             int size=20;
             g.setColor(Color.red);
             if (infolevel>2) System.out.println("Window Site i, Size, Coordinates X,Y ");
             for (int i=0; i<numberSites; i++)
             { 
              size = (siteSizeFactor*siteColour[i])/numberColours;
              int zeroSiteSize = siteSizeFactor;
              if (size>0) g.fillOval((int) (borderSize + (siteX[i]-minX)*windowScale - size/2), (int) (borderSize + (siteY[i]-minY)*windowScale - size/2), size, size);
              else g.drawOval((int) (borderSize + (siteX[i]-minX)*windowScale - zeroSiteSize/2), (int) (borderSize + (siteY[i]-minY)*windowScale- zeroSiteSize/2), zeroSiteSize, zeroSiteSize);
              if (infolevel>2)
                  System.out.println(i+" "+size+" "+((int) ((siteX[i]-minX)*windowScale))+" "+ ((int) ((maxY-siteY[i])*windowScale) ));
              //siteName[i]
              //siteColour[i]
              
              }
             
             g.setColor(Color.black);
             double ew,vw;
             int width;
             for (int i=0; i<numberSites; i++)
             {
               vw=vertexValue[i]*siteValue[i];   
               for (int j=0; j<numberSites; j++)
               {  
                  if (infolevel>2) System.out.print(edgeColour[i][j]+"  ");
                  if ((i==j) || (edgeColour[i][j]==0)) continue;
                  if (edgeColour[i][j]<= numberColours*zeroColourFrac) continue;
                  ew = vw*edgeValue[i][j];
                  width = (edgeWidthFactor*edgeColour[i][j])/numberColours;
                  System.out.print(width+"  "+edgeColour[i][j]+": ");
                  if (edgeColour[i][j]< numberColours*minColourFrac) width = 0;
                  //pajekColour(edgeColour[i][j]);
                  if (width>0) g.drawLine( (int) (borderSize + (siteX[i]-minX)*windowScale),(int) (borderSize + (siteY[i]-minY)*windowScale), 
                                           (int) (borderSize + (siteX[j]-minX)*windowScale),(int) (borderSize + (siteY[j]-minY)*windowScale) );

               } //eo for j
               if (infolevel>2) System.out.println();
                  
            }// eo for i
            if (infolevel>2) System.out.println("\n numberColours = "+numberColours+", edgeWidthFactor="+edgeWidthFactor);
             

         }// eo paint
     } //private class NetworkPicture extends JPanel
     
        
     private void createAndShowNetWin() {
        //Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("Network Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //if (maxX-minX > maxY-minY) windowScale = windowSize/(maxX-minX);
        //else windowScale = windowSize/(maxY-minY);
           
        
        Component contents = createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }    
    
     public void drawNetworkWindow() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowNetWin();
            }
        });    
        }
        
/* *******************************************************************************    
   Routines for Hamiltonians of different models
*/       
    
    /**
     * Gives change in Hamiltonian if one edge value is changed
     * @param String filename name of data file
     * @param double[] number an array of doubles
     * 
     */
    public double deltaEdgeHamiltonian(int i, int j, double newValue) 
    {
        double dH=0;
        switch (modelNumber) 
        { 
         case 4:  // Model 4; uses alphaH to set critical value of degree
         {
           double de=(newValue-edgeValue[i][j]);
           dH = edgeSourceH*vertexValue[i]*siteValue[i]*de ;
           dH+= lambdaH*de*distValue[i][j];
           double ki = getOutWeightedDegree(i);
           double kinew = ki + de;
//           System.out.println("alphaH, ki, kinew = "+alphaH+" , "+ki+" , "+kinew);
           if (ki<=alphaH) dH-=(1+alphaH-ki)*MAXH;
           if (kinew<=alphaH) dH+=(1+alphaH-kinew)*MAXH;
// These are needed only if in degree of j matters
//           double kj = getInWeightedDegree(j);
//           double kjnew = kj + de;
//           if (kj<alphaH) dH-=MAXH;
//           if (kinew<alphaH) dH+=MAXH;
           break;
           }//eo model 4
         case 3: 
         {
             dH= edgeSourceH*vertexValue[i]*siteValue[i]*(newValue-edgeValue[i][j]) ;
             dH+= edgePotentialTotal3(); 
             double oldValue = edgeValue[i][j];
             edgeValue[i][j]=newValue;
             dH-= edgePotentialTotal3(); 
             edgeValue[i][j]=oldValue;
             
             break;
         }    
           case 2: 
         { 
           double ci=calcConsumption(i);
           double deltaci = ((newValue-edgeValue[i][j])*siteValue[i]* consumptioncoeffH);
           //double cj=calcConsumption(j);
           double oi = calcOutput(vertexValue[i],siteValue[i]);
           //double oj = calcOutput(vertexValue[j],siteValue[j]);
           double dvj = siteValue[j]*vertexValue[j];
           
           dH = -(oi-ci) * dvj * 
                  edgePotential1(distValue[i][j],siteValue[i]*edgeValue[i][j]);
           dH+=  (oi- (ci+ deltaci ) ) * dvj 
                  * edgePotential1(distValue[i][j], siteValue[i]*newValue);
           
           dH+=   kappaH*deltaci + edgeSourceH*siteValue[i]*(newValue-edgeValue[i][j]);
           break;
         }   
         default:  // Model 1 
         {
           dH= (vertexValue[i]*vertexValue[j] * siteValue[i]*siteValue[j] * 
                 ( - edgePotential1(distValue[i][j],newValue) 
                   + edgePotential1(distValue[i][j],edgeValue[i][j]))
                + edgeSourceH*vertexValue[i]*siteValue[i]*(newValue-edgeValue[i][j])) ;
         }//eo model 1
        }//eo switch
        return (dH);
    }

    
    /**
     * Gives the potential between two site variables
     * @param double vertexValue 
     * @param double siteValue 
     * @return the output of vertex i
     * 
     */
    public double calcOutput(double vertexValue, double siteValue) 
    {  return( (1+outputcoeffH) * siteValue*
               (1- outputcoeffH/(outputcoeffH+vertexValue)) );
    };
    
    /**
     * Gives the potential between two site variables
     * @param int i vertex number
     * @return the output of vertex i
     * 
     */
    public double calcConsumption(int i) 
    {  
        double w=0;
        for (int j=0; j<numberSites; j++) w+=edgeValue[i][j]; 
        return( (vertexValue[i]+w)*siteValue[i]* consumptioncoeffH ) ;
    };
    
    
    /**
     * Gives the potential between two site variables
     * @param double weight fixed value of edge
     * @param double value value associated with edge
     * 
     */
    public double edgePotential1(double weight, double value) 
    {
    return(  lambdaH*value*Math.pow((Math.pow((weight/distScaleH), alphaH) +1.0 ), -gammaH) );
    }
 
    /**
     * Gives the potential between two site variables
     * @param double weight fixed value of edge
     * @param double value value associated with edge
     * 
     */
    public double edgePotentialTotal3() 
    {
        double c;
        double potl=0.0;
        doDykstra();
        for (int i=0; i<numberSites; i++)
        {
            c=siteValue[i]*vertexValue[i];
            for (int j=0; j<numberSites; j++)
            {
                if (i==j) continue;
                potl+=edgePotential1(distance[j][i],c);
            }
        }
        
    return( potl );
    }
 
//   vertexValue[i]*vertexValue[j] * siteValue[i]*siteValue[j] * 
//                 ( - edgePotential(distValue[i][j],newValue) 
//                   + edgePotential(distValue[i][j],edgeValue[i][j]))
//                + edgeSourceH*vertexValue[i]*siteValue[i]*(newValue-edgeValue[i][j])) ;
        
          

// --------------------------------------------------------------------
    /**
     * Gives change in Hamiltonian if one vertex value is changed
     * @param String filename name of data file
     * @param double[] number an array of doubles
     * 
     */
 
     public double deltaVertexHamiltonian(int i, double newValue) 
    {   
        double dH=0;
        switch (modelNumber) 
        { 
         case 4: 
         {
           double dv = (newValue-vertexValue[i]);
        // pure vertex terms as model 1
            dH= dv*siteValue[i] * vertexSourceH  
                   - vertexPotential1(siteValue[i],newValue)
                   + vertexPotential1(siteValue[i],vertexValue[i]);
          }//eo model 4
          case 3: 
         {
             double dv = (newValue-vertexValue[i]);
          // pure vertex terms first
             dH= dv*siteValue[i] * vertexSourceH  
                   - vertexPotential1(siteValue[i],newValue)
                   + vertexPotential1(siteValue[i],vertexValue[i]);
          // edge/vertex terms now
             for (int j=0; j<numberSites; j++)
             {                 
              if (i==j) continue;
              dH-= lambdaH*dv*siteValue[i]/(1+distance[j][i]/distScaleH) ; 
             } 
             break;
         }
         case 2: 
         {
           double ci=calcConsumption(i);
           double deltaci=(newValue-vertexValue[i])*siteValue[i]* consumptioncoeffH;
           double oi = calcOutput(vertexValue[i],siteValue[i]);
           double deltaoi = calcOutput(newValue,siteValue[i])-oi;
           dH = kappaH*(deltaci-deltaoi); 
           dH+= (newValue-vertexValue[i]) *siteValue[i] * vertexSourceH  ;
           // edge/vertex terms now
           for (int j=0; j<numberSites; j++)
           {                 
             if (i==j) continue;
             dH+= (deltaoi-deltaci)
                  *(calcOutput(vertexValue[j],siteValue[j])-calcConsumption(j))
                  *edgePotential1(distValue[i][j],siteValue[i] *edgeValue[i][j]); 
           } 
           break;
         }// eo case 2   
         default:  // Model 1 
         {
           double dv = (newValue-vertexValue[i]);
        // pure vertex terms first
            dH= dv*siteValue[i] * vertexSourceH  
                   - vertexPotential1(siteValue[i],newValue)
                   + vertexPotential1(siteValue[i],vertexValue[i]);
        // edge/vertex terms now
           for (int j=0; j<numberSites; j++)
           {                 
             if (i==j) continue;
             dH+= dv*siteValue[i]* edgeValue[i][j] * edgeSourceH
                - dv*siteValue[i]* vertexValue[j]*siteValue[j] * 
                  edgePotential1(distValue[i][j],edgeValue[i][j]); 
           } 
          }//eo model 1
         }//eo switch             
         return  ( dH  ) ;
    }
    
    /**
     * Gives the potential between two site variables
     * @param double weight fixed value of edge
     * @param double value value assoicated with edge
     * 
     */
    public double vertexPotential1(double weight, double value) {
    return(  kappaH*4.0*value*(1.0-value)*weight);
    }
        

   
    /**
     * Sweeps through all edges updating acording to Metropolis
     * 
     */
    public void edgeSweep() {
        
        double dH, newEdgeValue;
        int i,j,updateTried,updateMade;
        updateTried=0;
        updateMade=0;
        
        for (i=0; i<numberSites; i++){
            for (j=0; j<numberSites; j++){                 
                if (i==j) continue;
                updateTried++;
                if (edgeModeBinary) newEdgeValue = (rnd.nextBoolean())? 1.0: 0.0; 
                else newEdgeValue = rnd.nextDouble()*edgeMode; //Math.random();
                dH = deltaEdgeHamiltonian(i, j, newEdgeValue);
//                System.out.println(dH+"  "+betaH+"  "+dH);
                if ((dH<0) || ( rnd.nextDouble() < Math.exp(-betaH*dH) ) ) 
                {
                    edgeValue[i][j] = newEdgeValue;
                    updateMade++;
                };
            }//eo j
        
        }// eo i
       edgeUR.update(updateTried,updateMade);           
    }
        

// ***********************************************************************    
    
    /**
     * Sweeps through all vertices updating acording to Metropolis
     * 
     */
    public void vertexSweep() {
        
        double dH, newVertexValue;
        int i,updateTried,updateMade;
        updateTried=0;
        updateMade=0;
        
        for (i=0; i<numberSites; i++){
            updateTried++;
            newVertexValue=  rnd.nextDouble(); //Math.random();
            dH = deltaVertexHamiltonian(i, newVertexValue);
//                System.out.println(dH+"  "+betaH+"  "+dH);
            if ((dH<0) || ( rnd.nextDouble() < Math.exp(-betaH*dH) ) ) 
                {
                    vertexValue[i] = newVertexValue;
                    updateMade++;
                };
        
        }// eo i
       vertexUR.update(updateTried,updateMade);           
    }
        

    
    
    
// ***********************************************************************    
    
    
    /**
     * Does PPA analysis on network
     * See Broodbank page 180.
     */
    public void doPPA() {
        
        int numEdges = (int) (betaH+0.5);
        if (numEdges<0) numEdges = 3;
        int i,j,k,l,ef,ec;
        int[] edgeOrder = new int [numberSites+1];
        
        
        for (i=0; i<numberSites; i++)
        {
            ef=0;   
//          if (infolevel>1) System.out.println("From "+i+"-th site, "+siteName[i]);  
            for (j=0; j<numberSites; j++)
            {
                edgeValue[i][j]=0.0;
                if (i==j) continue;
                for (k = 0; k<ef; k++)
                {
                    if (distValue[i][j]<distValue[i][edgeOrder[k]]) break;
                };//eo for k 
//              if ((k==ef) && (ef < numEdges)) {edgeOrder[ef++]=j; continue;}
//                if (infolevel>1) System.out.println("i+j+k+ef="+i+j+k+ef);
                for (l = ef; l>k; l--){ edgeOrder[l] = edgeOrder[l-1]; };// for l
                edgeOrder[k]=j;
                ef++;
//                if (ef<numEdges) ef++;
            }//eo j
            for (k = 0; k<numEdges; k++) 
            {
//                    System.out.println(k+"-th biggest is "+edgeOrder[k]);  
                    ec = numberColours-k;
                    edgeColour[i][edgeOrder[k]]=(ec>0) ? ec : 1;
                    edgeValue[i][edgeOrder[k]]= (ec>0) ? ec : 1;// numberSites - k;
            };
        
        }// eo i
         
    }// eo doPPA
        


        
// *********************************************************
    /**
     * Gives the out degree of vertex
     * @param int i vertex number
     * @return the degree of vertex i
     * 
     */
    public int getOutDegree(int i) 
    {  
        int d=0;
        for (int j=0; j<numberSites; j++) if (edgeValue[i][j]>0) d++; 
        return( d ) ;
    };

// ....................................................................
    /**
     * Gives the in degree of vertex
     * @param int i vertex number
     * @return the in degree of vertex i
     * 
     */
    public int getInDegree(int i) 
    {  
        int d=0;
        for (int j=0; j<numberSites; j++) if (edgeValue[j][i]>0) d++; 
        return( d ) ;
    };

// ....................................................................
    /**
     * Gives the out weighted degree of vertex
     * @param int i vertex number
     * @return the weighted degree of vertex i
     * 
     */
    public double getOutWeightedDegree(int i) 
    {  
        double d=0;
        for (int j=0; j<numberSites; j++) d+=edgeValue[i][j]; 
        return( d ) ;
    };
          

// *********************************************************
    /*
     * Class for keeping track of updates in Metropolis algorithms
     */
    public class updateRecord {
        int tried=0;
        int made=0;
        double frac=0.0;
        int totaltried=0;
        int totalmade=0;
        double totalfrac=0.0;
        
        public updateRecord(){
        }        

        public void updateRecord(int t, int m, int tt, int tm)
        {
        tried=t;
        made=m;
        totaltried=tt;
        totalmade=tm;       
        frac= (tried>0) ?  made/((double) tried) : 0.0;
        totalfrac= (totaltried>0) ?  totalmade/((double) totaltried) : 0.0;
        }        
        
        public void reset()
        {
        tried=0;
        made=0;
        frac=0.0;
        totaltried=0;
        totalmade=0;
        totalfrac=0.0;       
        }        
        
        public void update(int t, int m)
        {
        tried=t;
        made=m;
        totaltried+=t;
        totalmade+=m; 
        frac= (tried>0) ?  made/((double) tried) : 0.0;
        totalfrac= (totaltried>0) ?  totalmade/((double) totaltried) : 0.0;
        }        

        public String toString()
        {
        return( "Tried "+ tried + 
                ", made "+ made + 
                ", total tried "+ totaltried + 
                ", total made "+ totalmade );       
        } 
        


    }// eo updateRecord
    
    // *********************************************************
    /**
     * Does Dykstra, updates distanceFromV global
     * Sets distance[i][j] to shortest distance from i to j
     * and DykstraMaxDist is the longest path between any two sites, 
     * and it equals MAXDISTANCE if disconnected
     */
    public void doDykstra() {
        // look at distances from vertex v
        DykstraMaxDist=0;
        for (int v=0; v<numberSites; v++) {
            int mdv=v;
            double mindistance =MAXDISTANCE;
            double newdist;
            boolean [] notVisited = new boolean[numberSites];
            for (int i=0; i<numberSites; i++) 
            {
                distance[v][i]=MAXDISTANCE; notVisited[i]=true;
            };
            distance[v][v]=0.0;
            for (int n=0; n<numberSites; n++) 
            {
                mindistance =MAXDISTANCE;
                for (int j=0; j<numberSites; j++)
                    if (notVisited[j] && (distance[v][j]<mindistance)) 
                    {
                        mindistance=distance[v][j]; mdv = j;
                    };
                    if (mindistance==MAXDISTANCE) break;    // muist be disconnected
                    notVisited[mdv]=false;
                    for (int j=0; j<numberSites; j++) 
                    {
                        if (edgeValue[mdv][j]==0) continue;
                        newdist = mindistance+distValue[mdv][j];
                        if (distance[v][j]>newdist) distance[v][j]=newdist;
                    }
                    
                    
            }
            if (DykstraMaxDist<mindistance) DykstraMaxDist=mindistance;
        }
    }


    // *********************************************************
/**
     * Set up simple edge network for testing 
     */
    public void setEdgesTest1() 
    {
       for (int i=0; i<numberSites; i++){
            for (int j=0; j<numberSites; j++){                 
                if (i==j) continue;
                if (i==j+1) edgeValue[i][j]=1;
                else edgeValue[i][j]=0;
                }
            }  
 }
    
    // *********************************************************
    /**
     * Does i-st, i-th.
     */
//    public String ith(int i) {
//          String s = new String();
//          s=i.toString()+"-th"; 
//          if (i==1) s=i.toString()+"-st"; 
//          if (i==2) s=i.toString()+"-nd"; 
//         if (i==3) s=i.toString()+"-rd"; 
//          return s;
//   }
        
    
    /**
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public double TruncDec(double value, int dec)
    {
      double shift = Math.pow(10,dec);
      return ( ( (double) ((int) (value*shift+0.5)))/shift);
    }
    
}// eo islandNetwork class
