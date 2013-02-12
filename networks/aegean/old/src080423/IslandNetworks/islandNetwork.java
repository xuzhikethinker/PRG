/*
 * islandNetwork.java
 *
 * Created on 01 April 2004, 14:14
 * Requires TextReader.java - see David Eck, javanotes4.pdf, ch10, p402.
 * @author  time
 */

package IslandNetworks;
    
import IslandNetworks.Vertex.IslandSite;
import IslandNetworks.Edge.IslandEdgeSet;
import java.io.*;
//import java.lang.*;
import java.lang.Math.*;
import java.util.Date;
import java.util.Random;
//import javax.swing.*; 
//import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;

import JavaNotes.TextReader;

import TimUtilities.TimMessage;

//import TimUtilities.FileUtilities.GeneralEndingFilter;
import TimUtilities.StatisticalQuantity;


//import IslandNetworks.NetworkGenerator;
//import IslandNetworks.StatisticalQuantity;
//import IslandNetworks.IslandSite;
//import IslandNetworks.IslandHamiltonian;
//import IslandNetworks.NetworkPicture;
import IslandNetworks.Vertex.VertexMode;

    
    
/**
 * <tt>islandNetwork</tt> is the central class used to represent an island network.  
 * <p>The files, input and output, have names built from the root of 
 * <tt>inputnameroot</tt> and <tt>outputnameroot</tt>.
 * <tt>numberSites</tt> is the number of sites.
 * Each site is a vertex and is associated with an array <tt>siteArray[]</tt> of
 * class <tt>Site</tt>.  Carries a site size which is a fixed characteristic size for the site.  
 * The vertex also has a variable value <tt>siteArray[i].value</tt>
 * which should have universal values between 0 and <tt>vertexMaximum</tt> typically.
 * The edges exist between all sites.  They have a fixed characteristic, the distance
 * <tt>double distValue[numberSites][numberSites]</tt> which ought to be symmetric.
 * They then have a variable value which is strictly in [0,1) but need not be symmetric
 * <tt>double edgeValue[numberSites][numberSites]</tt> <tt>edgeSet.getEdgeValue(i,j)</tt> represents the
 * value of the edge from site i to site j and is stored in the IslandEdgeSet class.</p>
 *<p>The Hamiltonian coefficients are:-
 *<ul><li> <tt>double Hamiltonian.edgeSource</tt>
 *       <li> <tt>double Hamiltonian.vertexSource</tt>
 *       <li> <tt>double Hamiltonian.alpha</tt>
 *       <li> <tt>double Hamiltonian.beta</tt>
 *       <li> <tt>double Hamiltonian.gamma</tt>
 *       <li> <tt>double Hamiltonian.kappa</tt>
 *       <li> <tt>double Hamiltonian.lambda</tt>
 *       <li> <tt>double Hamiltonian.distanceScale</tt>
 *       <li> <tt>double Hamiltonian.shortDistanceScale</tt>
 *</ul><p>
 *<p>
 * There are also records of the updates
 * <tt>updateRecord edgeUR</tt> and <tt>updateRecord vertexUR</tt>
 * Finally there is a variable controling information output <tt>int message.getInformationLevel()</tt>
 * and a variable controling the update mode <tt>int updateMode</tt>.</p>
 *@author Tim Evans
 * 
*/
public class islandNetwork {    
      
       
        //Specify the look and feel to use.  Valid values:
        //null (use the default), "Metal", "System", "Motif", "GTK+"
//        final static LookAndFeel lookAndFeel = new LookAndFeel("System");  
        
        final static String iNVERSION = "iN080423";
 
        final static int MAXSITENUMBER = 100; // maxmimum number of sites
        
        final String SEPSTRING = "\t"; // Separation Character for column indication
        final String NETWORKDATAFILEEXT = "anf"; // extension for ariadne network data file
        boolean inputGUI = false;
        boolean dataread = false;

        FileLocation inputFile;
        final static String inputFileEnding="_input";
        FileLocation outputFile;
        boolean autoSetOutputFileName;
        
        IslandArguments commandLine = new IslandArguments();
        
        double jiggleScale = 0.0; // sets jiggleScale
        int numberSites = MAXSITENUMBER;
        IslandEdgeSet edgeSet;        
        IslandSite [] siteArray; // = new IslandSite[numberSites];
        int [] siteAlphabeticalOrder; // = new int[numberSites];  // Sites in order of names [i] = no. of i-th site

        double minX=0;
        double maxX=0;
        double minY=0;
        double maxY=0;

        IslandHamiltonian Hamiltonian;
        
        // Network Statistics
        double maxSiteValue =-1.0;
        int maxSiteValueIndex =-1;
        double allmaxedgeweight=-1.0;
        int allmaxedgeweightindex = -1;
        double maxSiteWeight =-1;
        int maxSiteWeightIndex = -1;
//        int maxvedgeweightindex = -1; 
        double maxOutSiteStrength = -1;
        int maxOutSiteStrengthIndex = -1;
        double maxInSiteStrength = -1;
        int maxInSiteStrengthIndex = -1;
        
        StatisticalQuantity siteWeightStats;
        StatisticalQuantity edgeValueStats;
        StatisticalQuantity edgeWeightStats;
        StatisticalQuantity siteStrengthOutStats;
        StatisticalQuantity siteStrengthInStats;

        // properties of the network
        double energy = 0;
        
        double totVertexWeight=-1;
        double totEdgeWeight=-1;
        double totVertexValue=-1;
        double totEdgeValue=-1;

        double[] inSiteStrengthWeight;
        double[] outSiteStrengthWeight;
        double[] inSiteStrengthValue;
        double[] outSiteStrengthValue;
        int [] siteWeightOrder;  // points in order [i] = no. of i-throws biggest Size
        double [] maxEdgeWeight; // = new double [numberSites]; 
        int [] maxEdgeWeightIndex; // = new int [numberSites]; 
        StatisticalQuantity siteDistanceStats;

        IslandCulture networkCulture;      
        double culturePSiteCopy = 0.45;
        double culturePCopy =0.45;
        double culturePInnovate =0.05;
        double cultureTimeScale=10000.0;
      
        SiteRanking siteRank;  
        double influenceProb=0.5;

        // update records
        updateRecord edgeUR = new updateRecord() ;
        updateRecord vertexUR= new updateRecord() ;

        // General parameters
        TimMessage message = new TimMessage(0);
        CalculationParameters calcParam; // use this new class to replcae some variables
        ExecuteMode executeMode; // used to indicate how this is to be processed
        UpdateMode updateMode;
        VertexMode vertexMode; //double vertexMaximum;
        ModelNumber modelNumber;
        boolean coldStart=false;
        int outputMode=255;
        int MCsweeps =100; 
        
        // Display factors
        double zeroColourFrac=0.03;
        double minColourFrac=0.1;
        int siteWeightFactor = 20;
        int edgeWidthFactor = 10;
        double DisplayMaxVertexScale=1.0; //<=0 then display vertices relative to largest
                                        // else display vertices relative to this size       
        double DisplayMaxEdgeScale=1.0;   //<=0 then display edges relative to largest
                                        // else display edges relative to this size       
        int DisplayVertexType=0; // 0=size, 1 = rank;
        SiteWindowMode siteWindowMode= new SiteWindowMode(1); // 0=numerical, 1=sweight, 2=Rank, 3=alphabetical 
        int maxNumberColours=100;
        int numberColours=100;
        Color [] javaColour = new Color[maxNumberColours];
        String[] pajekColour = new String[maxNumberColours];
        String[] pajekGrey = new String[maxNumberColours];
        int visualisationWidth=760;
        int visualisationHeight=760;

        
        static Random rnd;
        double MAXH = 1e6;
        
        // Generate Network paramters
        int generateNumber=-1; // if <0 then does not generate a network, reads in from a file
        int generateType =0; // see NetworkGenerator for info on this



    
    /** Creates a new instance of network.
     *@param ns number of Sites
     */
    public islandNetwork(int ns) {
            inputGUI = true;
            executeMode= new ExecuteMode();
            //message.getInformationLevel() = -1;
            
            numberSites=ns;
            initialiseArrays(-1.0);
// for (int i =0; i<numberSites; i++) { siteArray[i].size=-88.0;  siteArray[i].value=-89.0;};
            setColours();
            if (message.getInformationLevel()>2)
            {for (int i =0; i<=numberColours; i++)
              System.out.println(i+":  "+pajekColour[i]+" , "+pajekGrey[i]);
            }
            //inputnameroot="testsimple";
            inputFile = new FileLocation("input/", "", "aegean34S1L3", "", "_input", "dat");
            outputFile = new FileLocation("output/", "", "aegean34S1L3", "", 0, "", "dat");
            //runname="r0";
            autoSetOutputFileName = true;
        
        // Now Hamiltonian coefficients
            Hamiltonian = new IslandHamiltonian();
             
           // update records zeroed on construction
             // general parameters
             modelNumber = new ModelNumber(1.3);
             updateMode = new UpdateMode("MC"); // MC Sweep
             vertexMode = new VertexMode(5.0); //Math.max((DisplayMaxVertexScale>0?DisplayMaxVertexScale:5.0), 1.0);
//             DijkstraMaxDist = MAXSEPARATION;
             rnd = new Random(); // Give long integer arg else uses time to set seed
    }//eo constructor
    

    
    
   
    /** 
    * Creates a deep copy of a network
     * Makes a deep copy of the input network. 
    *@param islnetinput is an inout islandNetwork to be deep copied to new one.
    */
    public islandNetwork(islandNetwork islnetinput) {
        //rnd = new Random(); // Give long integer arg else uses time to set seed
        numberSites = islnetinput.numberSites;
        inputGUI = islnetinput.inputGUI;
        dataread = islnetinput.dataread ;
        inputFile  = new FileLocation(islnetinput.inputFile);
        outputFile  = new FileLocation(islnetinput.outputFile);
        if (message.getInformationLevel()>-2) System.out.println("Creating new Island Network, input name = "+inputFile.getFullFileRoot());

        initialiseArrays(islnetinput.edgeSet.edgeMode.maximumValue); // set up core arrays to be of correct size.
        edgeSet  = new IslandEdgeSet(islnetinput.edgeSet);          
        for (int i=0; i<numberSites; i++) siteArray[i] = new IslandSite(islnetinput.siteArray[i]);
        
        // General parameters
        copyGeneralParameters(islnetinput);

        // Copy Hamiltonian coefficients, deep copy not needed as these are unchanged
        Hamiltonian = islnetinput.Hamiltonian;

        // display parameters
        copyDisplayParameters(islnetinput);

        // The following can be derived from the edge and site values after these are defined
        if (islnetinput.siteRank==null) siteRank=null;
            else siteRank = new SiteRanking(islnetinput.siteRank); // deep copy if defined
        
        if (islnetinput.networkCulture==null) networkCulture = null;
        else networkCulture = new IslandCulture(islnetinput.networkCulture); 
// perform deep copies        
        if (islnetinput.siteWeightOrder != null) 
        {
            siteWeightOrder = new int[numberSites];  // points in order [i] = no. of i-th biggest 
            for (int i=0; i<numberSites; i++) siteWeightOrder[i] = islnetinput.siteWeightOrder[i];
        } 
        if (islnetinput.siteAlphabeticalOrder != null) 
        {
                    siteAlphabeticalOrder = new int[numberSites];  // Sites in order of names [i] = no. of i-th site
                    for (int i=0; i<numberSites; i++) siteAlphabeticalOrder[i] = islnetinput.siteAlphabeticalOrder [i];
        } 
        
        minX=islnetinput.minX;
        maxX=islnetinput.maxX;
        minY=islnetinput.minY;
        maxY=islnetinput.maxY;
//        windowSize = islnetinput.windowSize ;
//        double windowScale = windowSize;
        
        
        // derived properties of the network
        //metricNumber=islnetinput.metricNumber;
        
        energy=islnetinput.energy;
        maxSiteValue = islnetinput.maxSiteValue;
        maxSiteValueIndex = islnetinput.maxSiteValueIndex;
        allmaxedgeweight= islnetinput.allmaxedgeweight;
        allmaxedgeweight=islnetinput.allmaxedgeweight;
        maxSiteWeight = islnetinput.maxSiteWeight;
        maxSiteWeightIndex = islnetinput.maxSiteWeightIndex ;
//        maxvedgeweightindex =islnetinput.maxvedgeweightindex ; 
        maxOutSiteStrength = islnetinput.maxOutSiteStrength;
        maxOutSiteStrengthIndex = islnetinput.maxOutSiteStrengthIndex;
               
        totVertexWeight=islnetinput.totVertexWeight;
        totEdgeWeight=islnetinput.totEdgeWeight;
        totVertexValue=islnetinput.totVertexValue;
        totEdgeValue=islnetinput.totEdgeValue;      
        
        influenceProb = islnetinput.influenceProb;
        
        siteWeightStats = islnetinput.siteWeightStats ;
        edgeWeightStats = islnetinput.edgeWeightStats ;     
        edgeValueStats = islnetinput.edgeValueStats ;     
        siteStrengthOutStats = islnetinput.siteStrengthOutStats ;     
        siteStrengthInStats = islnetinput.siteStrengthInStats ;     

        
        
        
} //eo constructor copy network
    
    
    
        /** 
     * Initialises islandNetwork using data from file specified in <i>inputFile</i><tt>.anf</tt>
     * <br>Lines starting with # are comments.
     * Lines starting with *param are parsed as islandNetwork parameters, 
     * Section starting with *sites nnn has nnn sites listed with first line as labels of variables 
     * followed by nnn lines in numerical order, each line with values for one site
     * Section starting with *edge line has first line as labels of values then each of the following lines
     * conbtains values for one edge.  Edges are listed in order line number = (source*number of sites) +target. 
     *@param ifl input FileLocation specifying input file (assumed to be .anf) and can use runnumber too.
         * @param inparam island network carrying display variable parameter settings
     */
    public islandNetwork(File ifl, islandNetwork inparam) 
    {
        //rnd = new Random(); // Give long integer arg else uses time to set seed
        inputFile = inparam.inputFile;
        outputFile = new FileLocation(ifl);
//        outputFile.mkDirs();
        
        // General parameters
        copyGeneralParameters(inparam);
        
        // Copy Hamiltonian coefficients
        Hamiltonian = new IslandHamiltonian(inparam.Hamiltonian);

        // display parameters
        copyDisplayParameters(inparam);
        
        // The following can be derived from the edge and site values after these are defined
        
        setColours();

      String filename = outputFile.getFullLocationFileName("",NETWORKDATAFILEEXT);
      System.out.println("Starting to read islandNetwork from " + filename);
      TextReader data;     // Character input stream for reading data.
      String tempstring="";
      String datatype="Unknown";
      int linenumber=0;
      int numberCt;
      int nargs=0;
      final int MAXPARAMETERS = 100;
      double[] darray = new double [MAXPARAMETERS ];
      String [] parameterString= new String [MAXPARAMETERS ]; // maximum 100 parameters
      String [] labelString= new String [MAXPARAMETERS ]; // maximum 100 parameters
            
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         System.out.println("Can't find file "+filename);
         return;  
      }
         try{ 
          while (!data.eof()) 
          { // start reading new line
              linenumber++;
              datatype = data.getWord(); 
//            System.out.println("first word is "+datatype);
              if (datatype.startsWith("#")) 
              { // this line is a comment
                  tempstring=datatype+data.getln(); //comment read to end of line
                  System.out.println("Comment: "+tempstring);
                  continue;
              }
              
              if (datatype.startsWith("*param")) 
              { // this line is to parsed as parameters for the run
                   while (!data.eoln() ) parameterString[nargs++]=data.getWord(); //comment read to end of line
                   System.out.print("Parameter string: ");
                   for (int iii=0; iii<nargs; iii++) System.out.print(SEPSTRING+parameterString[iii]);
                   System.out.println();                                      
                  continue;
              }

              
              if ( datatype.startsWith("*sites") || datatype.startsWith("*edges")) break; 
              
            }//eo while !data.eof
   
          // WHERE DO WE READ IN PARAMETER STRING?
          if (parameterString.length>0) Parse(parameterString);
          
          // read in site data
          if (!datatype.startsWith("*sites"))  {
              System.out.println("*** ERROR - next line should start with *sites");return;
          }
          
                  numberSites= Integer.parseInt(data.getWord());
                  initialiseArrays(-1.0);
                  
                  // first line has the labels of the variables
                  linenumber++;
                  nargs=0;
                  do labelString[nargs++]=data.getWord(); while (!data.eoln() );
                  int [] index = new int[nargs];
                  for (int i =0; i<nargs; i++) index[i]= siteArray[0].getIndex(labelString[i]);
                  // next lines have the data for sites, one site per line in numerical order
                  for (int s= 0; s<numberSites; s++)
                  {
                      linenumber++; 
                      for (int i=0; i<nargs; i++) {
                          tempstring=data.getWord();
                          siteArray[s].setValue(index[i], tempstring);
                      } 
                  }
          
          // next line after site list ought to be start of edge list
          linenumber++;
          datatype = data.getWord(); 
          if (datatype.startsWith("*edges"))  
          {
              // first line after *edges line has the labels of the variables
              // these are not currently used
                  linenumber++;
                  nargs=0;
                  do labelString[nargs++]=data.getWord(); while (!data.eoln() );
             // Each line after that has the entries in order specifed by edge class
              // where     line number = (source*numberSites+target)      
               // Currently eavh line is "value"+sep+ "distance" +sep+ "penalty"
                  for (int s=0; s<numberSites; s++)
                  {
                      linenumber++;                          
                      for (int t=0; t<numberSites; t++)
                      {
                          edgeSet.setEdgeValue(s,t, data.getDouble());
                          edgeSet.setEdgeDistance(s,t, data.getDouble());
                          edgeSet.setEdgePenalty(s,t, data.getDouble());
                      }
                  } // for s
                      
          }
          
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
       System.out.println("Finished reading from " + filename+ " last line studied was "+linenumber);
       
           // sort sites by alphabetical order
    siteAlphabeticalOrder = new int [numberSites];
    calcSiteOrder(1, siteAlphabeticalOrder, numberSites);

    }  // end of constructor - read from file
    
    
                    // General parameters
/**
 *  Copy General parameters from existing islandNetwork.
 *@param islnetinput is an inout islandNetwork to be deep copied to new one.
    */
    public void copyGeneralParameters(islandNetwork islnetinput)
    {        
        executeMode = new ExecuteMode(islnetinput.executeMode);
        updateMode= new UpdateMode(islnetinput.updateMode);
        vertexMode= new VertexMode(islnetinput.vertexMode);
        modelNumber = new ModelNumber(islnetinput.modelNumber);
        outputMode= islnetinput.outputMode;
        coldStart = islnetinput.coldStart;
        
    }

    
/**
 *  Copy display parameters from existing islandNetwork
 *@param islnetinput is an inout islandNetwork to be deep copied to new one.
    */
    public void copyDisplayParameters(islandNetwork islnetinput)
    {
        zeroColourFrac=islnetinput.zeroColourFrac;
        minColourFrac=islnetinput.minColourFrac;
        siteWeightFactor = islnetinput.siteWeightFactor ;
        edgeWidthFactor =islnetinput.edgeWidthFactor ;
        DisplayMaxVertexScale=islnetinput.DisplayMaxVertexScale;
        DisplayMaxEdgeScale=islnetinput.DisplayMaxEdgeScale;
        numberColours=islnetinput.numberColours;
        DisplayVertexType = islnetinput.DisplayVertexType;
        siteWindowMode = islnetinput.siteWindowMode;
    }
    
    /**
     ** Initialise arrays needed to size numberSites.
     * @param edgeValueMax edge value maximum, if negative limits strength.
     */
    public void initialiseArrays(double edgeValueMax)
    {
            maxEdgeWeight = new double [numberSites]; 
            maxEdgeWeightIndex = new int [numberSites]; 
            siteAlphabeticalOrder = new int[numberSites];  // Sites in order of names [i] = no. of i-th site
            siteArray = new IslandSite[numberSites];
            for (int s=0; s<numberSites; s++) siteArray[s]= new IslandSite(s);
            edgeSet = new IslandEdgeSet(numberSites,edgeValueMax) ;
    }

    
    
        /** Parses input parameter string.
         * <br>null in array terminates processing.
         * <br>See usage for details of parameters.
         *@param ArgList array of strings containing -?<value>
         *@return any non zero number is an error.
         */       
       public int Parse(String[] ArgList){
                
                for (int i=0;i< ArgList.length ;i++){
                    if (ArgList[i]==null) break;
                    if (ArgList[i].length() <2) {
                        System.out.println("\n*** Argument "+i+", "+ArgList[i]+", is too short");
                        return 1;}
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** Argument "+i+", "+ArgList[i]+", does not start with -");
                            return 2;}
                            switch (ArgList[i].charAt(1)) {
                                case 'a': {Hamiltonian.alpha = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'b': {
                                     if (ArgList[i].charAt(2)=='t') Hamiltonian.beta = Double.parseDouble(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='s') Hamiltonian.setb( Double.parseDouble(ArgList[i].substring(3)) );
                                break;}
                                case 'c': {
                                     if (ArgList[i].charAt(2)=='c') culturePCopy = Double.parseDouble(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='s') culturePSiteCopy = Double.parseDouble(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='i') culturePInnovate = Double.parseDouble(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='t') cultureTimeScale = Double.parseDouble(ArgList[i].substring(3));
                                     break;}
                                case 'd': {
                                     if (ArgList[i].charAt(2)=='l') Hamiltonian.distanceScale = Double.parseDouble(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='m') edgeSet.metricNumber = Integer.parseInt(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='s') Hamiltonian.shortDistanceScale  = Double.parseDouble(ArgList[i].substring(3));
                                break;}
                                case 'e': {
                                    edgeSet.edgeMode.setEdgeMode(Double.parseDouble(ArgList[i].substring(2)));
                                break;}
                                case 'f': { if (ArgList[i].charAt(2)=='i') 
                                { 
                                    if (ArgList[i].charAt(3)=='j') jiggleScale = Double.parseDouble(ArgList[i].substring(3));
                                    else inputFile.set(ArgList[i].substring(3));
                                }
                                         if (ArgList[i].charAt(2)=='o') 
                                         {
                                             if (ArgList[i].charAt(3)=='a')  autoSetOutputFileName=true;
                                             else if (ArgList[i].charAt(3)=='A')  autoSetOutputFileName=false;
                                             else outputFile.set(ArgList[i].substring(3));
                                         }                                            
                                break;}
                                case 'g': {Hamiltonian.gamma = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'i': {message.setInformationLevel(Integer.parseInt(ArgList[i].substring(2)));
                                           //infolevel=message.getInformationLevel();    
                                break;}
                                case 'j': {Hamiltonian.vertexSource = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'k': {Hamiltonian.kappa = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'l': {Hamiltonian.lambda = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'm': {Hamiltonian.edgeSource = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'o': {outputMode = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'u': {
                                    if (!updateMode.setMode(ArgList[i].substring(2))) updateMode.setMode(Integer.parseInt(ArgList[i].substring(2)));
                                break;}
                                case 'v': {modelNumber.set(Double.parseDouble(ArgList[i].substring(2)));
                                break;}
                                case 'w': {
                                    inputGUI=true;
                                    if (ArgList[i].substring(2,3).equals("n"))
                                        inputGUI =false;
                                break;}
                                case 'x': {
                                    if (ArgList[i].charAt(2)=='v') vertexMode.setMaxValueModeOn(Double.parseDouble(ArgList[i].substring(3)));
                                    if (ArgList[i].charAt(2)=='w') vertexMode.setConstantWeightModeOn(Double.parseDouble(ArgList[i].substring(3)));
                                break;}
                                case 'C': {if (ArgList[i].charAt(2)=='c') coldStart=true;
                                           if (ArgList[i].charAt(2)=='h') coldStart=false;
                                                                  
                                break;}

                                case 'D': {if (ArgList[i].charAt(2)=='v') 
                                           {
                                             if (ArgList[i].charAt(3)=='s') DisplayMaxVertexScale = Double.parseDouble(ArgList[i].substring(4));
                                             if (ArgList[i].charAt(3)=='t') DisplayVertexType = Integer.parseInt(ArgList[i].substring(4));                                              
                                           }
                                           if (ArgList[i].charAt(2)=='e') DisplayMaxEdgeScale = Double.parseDouble(ArgList[i].substring(3));
                                           if (ArgList[i].charAt(2)=='z') zeroColourFrac = Double.parseDouble(ArgList[i].substring(3));
                                           if (ArgList[i].charAt(2)=='n') minColourFrac = Double.parseDouble(ArgList[i].substring(3));
                                           if (ArgList[i].charAt(2)=='x') visualisationWidth= Integer.parseInt(ArgList[i].substring(3));
                                           if (ArgList[i].charAt(2)=='y') visualisationHeight= Integer.parseInt(ArgList[i].substring(3));
                                   
                                break;}

                                case 'E': { executeMode.setMode(ArgList[i].substring(2));
                                    break;}

                                case 'Z': {
                                   if (ArgList[i].charAt(2)=='n') generateNumber= Integer.parseInt(ArgList[i].substring(3));
                                   if (ArgList[i].charAt(2)=='t') generateType= Integer.parseInt(ArgList[i].substring(3));
                                break;}
                                
                                                                  
                                // this is DEPRECACTED   
                                case 'X': {
                                    System.err.println(" *** ERROR, agrument -X deprecated, use -x?ddd ");
                                    //vertexMaximum = Double.parseDouble(ArgList[i].substring(2));
                                    return 5;}
                                case 'F': {
                                           if (ArgList[i].charAt(2)=='i') inputFile.setRootDirectory(ArgList[i].substring(3));
                                           if (ArgList[i].charAt(2)=='o') outputFile.setRootDirectory(ArgList[i].substring(3));
                                    
                                break;}
                                case 'S': {
                                           if (ArgList[i].charAt(2)=='i') inputFile.setSubDirectory(ArgList[i].substring(3));
                                           if (ArgList[i].charAt(2)=='o') outputFile.setSubDirectory(ArgList[i].substring(3));
                                    
                                break;}

 

                                case '?': {Usage(); 
                                return 4;}
                                
                                default:{
                                    System.out.println("\n*** Argument "+i+", "+ArgList[i]+", not known, usage:");
                                    Usage();
                                    return 3;
                                }
                                
                            }
                }
                
                
//                if (outputFile.basicRoot.equals("AUTO")) autoSetOutputFileName=true;
//                else  autoSetOutputFileName=false;
                setOutputFileName();
                
                // set j to equal average site weight
                if (vertexMode.constantWeightOn) Hamiltonian.vertexSource= vertexMode.totalWeight/numberSites;

                return 0;
            } // eo ParamParse

        /** Shows command line arguments.
         */
            public void Usage(){
                
                islandNetwork i = new islandNetwork(1);
                System.out.println("...............................................................................");
                System.out.println("Ariadne, iNVERSION "+islandNetwork.iNVERSION+" usage: ");
                System.out.println("aegean <options> ");
                System.out.println(" where options are -<char><value> separated by space as follows ");
                System.out.println("  -a<alpha>    Sets alpha, short distance power in edge potential, default "+i.Hamiltonian.alpha);
                System.out.println("  -bs<b>       Sets b, power used for site terms, default "+i.Hamiltonian.getb());
                System.out.println("  -bt<beta>    Sets beta, inverse temperature, default "+i.Hamiltonian.beta);
                System.out.println("                Also number of edges per site in PPA mode");
                System.out.println("  -cc<culturePCopy>     probability that will copy culture of neighbour, default "+i.culturePCopy);
                System.out.println("  -cc<culturePSiteCopy> probability that will copy culture within site, default "+i.culturePSiteCopy);
                System.out.println("  -cc<culturePInnovate> probability that will innovate new culture keeping existing culture, default "+i.culturePInnovate);
                System.out.println("  -cc<cultureTimeScale> time scale in units of tau_2, default "+i. cultureTimeScale);
                System.out.println("  -dl<distanceScale> Sets distance scale, "+i.Hamiltonian.distanceScale);
                System.out.println("  -dm<metricNumber>  Sets metric for Dijkstra calculations, default "+i.Hamiltonian.shortDistanceScale);
                System.out.println("  -ds<shortdistanceScale> Sets short distance scale, default "+i.Hamiltonian.shortDistanceScale);
                System.out.println("  -e<edgeMode>      Sets edge max edge value or binary (0 or 1) if 0,");
                System.out.println("                    Negative indicates maximum strength, default "+i.edgeSet.edgeMode.edgeModeValue());
                System.out.println("  -fa          Sets the output file name to AUTO,");
                System.out.println("                    i.e. automatic format as noted above");
                System.out.println("                            default "+i.inputFile.getFullFileRoot());
                System.out.println("  -fij<jiggleScale>  Sets scale to jiggle input site locations, default "+i.jiggleScale);
                System.out.println("  -fi?<string>  Sets input file names and directories where ? given below");
                System.out.println("  -foa        Sets filename to automatic based on parameters, default "+i.autoSetOutputFileName);
                System.out.println("  -foA        No automatic file name");
                    System.out.println("  -fo?<string> Sets input file names and directories where ? given below");
                System.out.println("               ? is a character taken from:-");
                inputFile.printSetStrings(System.out,"              ");
                System.out.println("  -g<gamma>         Sets gamma, short distance power in edge potential,");
                System.out.println("                            default "+i.Hamiltonian.gamma);
                System.out.println("  -i<message.getInformationLevel()>     Sets information level, 0 lowest, default "+i.message.getInformationLevel());
                System.out.println("  -j<j>        Sets j, sum over all vertex weights, default "+i.Hamiltonian.vertexSource);
                System.out.println("  -k<kappa>         Sets kappa, coefficient of vertex potential, default "+i.Hamiltonian.lambda);
                System.out.println("  -l<lambda>        Sets lambda, coefficient of edge potential, default "+i.Hamiltonian.lambda);
                System.out.println("  -m<mu>       Sets mu, sum over all edge weights, default "+i.Hamiltonian.edgeSource);
                System.out.println("  -o<outputMode>    Sets output mode by bits 1 (0)= on (off):");
                System.out.println("                    128 = distance. Default  "+i.outputMode);
                System.out.println("  -u<updateMode>    Sets update mode: "+i.updateMode.allModesShort(" ",", ")+". Default "+i.updateMode);
                System.out.println("  -v<modelNumber>   Sets model number to use, default "+i.modelNumber.major+"_"+i.modelNumber.minor);
                System.out.println("  -w[y|n]      Sets window mode on -wy or off -wn, default "+i.inputGUI);
                System.out.println("  -xv<vertexMaximum>  Sets vertices to have a maximum value");
                System.out.println("  -xw<constantWeight> Sets total weight of vertices to be constant");
                System.out.println("                      Vertex mode and value defaults are "+i.vertexMode.descriptionValue(", value="));
                System.out.println("  -Cc MC calculation is started from a cold start");
                System.out.println("  -Ch MC calculation is started from a hot start, default "+(i.coldStart?"COLD":"HOT") );
                System.out.println("  -De<DisplayMaxEdgeScale> Sets size of edge weight (s[i]v[i]e[i][j]) for maximum");
                System.out.println("                              colour in display (absolute),");
                System.out.println("                              if 0 then largest value actual value used (relative),");
                System.out.println("                               default "+i.DisplayMaxEdgeScale);
                System.out.println("  -Dn<minColourFrac> Sets fraction of largest colour which is to be");
                System.out.println("                       represented as colour value 1 (next to minimum),");
                System.out.println("                         default "+i.minColourFrac);
                System.out.println("  -Dvt<DisplayVertexType> Sets type of vertex displayed, 0 size, 1 rank");
                System.out.println("                          default "+i.DisplayVertexType);
                System.out.println("  -Dvs<DisplayMaxVertexScale> Sets size of vertex weight (s[i]v[i]) for");
                System.out.println("                              maximum colour in display (absolute),");
                System.out.println("                              if 0 then largest value actual value used (relative),");
                System.out.println("                               default "+i.DisplayMaxEdgeScale);
                System.out.println("  -Dx<visualisationWidth>  Sets preferred width for windows in pixels, default "+i.visualisationWidth);
                System.out.println("  -Dy<visualisationHeight> Sets preferred height for windows in pixels, default "+i.visualisationHeight);
                System.out.println("  -Dz<zeroColourFrac> Sets fraction of largest colour which is to be");
                System.out.println("                       represented as zero colour, default "+i.zeroColourFrac);
                System.out.println("  -E<String> sets operational mode to be that of given name, default "+i.executeMode.toString());
                System.out.println("             Options are: "+i.executeMode.allOptionsString(", ")+".");
                System.out.println("  -Zn<n> Generates network of n sites");
                System.out.println("  -Zt<t> Generates network of type t");
                NetworkGenerator ng = new NetworkGenerator();
                for (int t=0; t<ng.NUMBERTYPES;t++) System.out.println("           Type "+t+":"+ng.networkName(t));
                System.out.println();
                System.out.println("  -???      This usage screen. Also try ariadne.html for help.");
                System.out.println("...............................................................................");
                
            } //eo usage
    
// -------------------------------------------------------------------
  /**
     * Returns outputMode description. 
     */
    public String outputModeString()  
    {
        String s="";
        if ((outputMode & 128) >0 ) s+="distances ";
        return(s);
    }

// -------------------------------------------------------------------
  /**
     * Sets up output file name.
   * <br>Takes next free sequence name and creates directories if needed.
     */
    void setOutputFileName()  
    {
        if (autoSetOutputFileName)
        {
            outputFile.setRootDirectory("output/");
            String sb=inputFile.getBasicRoot();
            outputFile.setBasicRoot(sb);
            String sp="";
            switch (updateMode.getValue()){
                    case 0: sp="beta"+Hamiltonian.beta+updateMode.shortDescription(); break;
                case 1: sp="v" + modelNumber.major+"_"+modelNumber.minor +
                    "e"+edgeSet.edgeMode.edgeModeValue()+
                    Hamiltonian.parameterString("")+updateMode.shortDescription(); break;
                case 2: 
                case 3: sp=updateMode.shortDescription(); break;
        }
            outputFile.setSubDirectory(sb+sp);
            outputFile.setParameterName(sp);
            outputFile.setEnding("");
            outputFile.setExtension(NETWORKDATAFILEEXT);
            outputFile.setFirstFreeSequenceNumber(); // base this on search for existing anf files
            if ((!outputFile.isFullLocationDirectory()) && (!outputFile.mkDirs() ) ) System.err.println("*** ERROR creating directory "+outputFile.getFullLocation());
            
        }
    }        
            
// .............................................................    
    /** Shows distance data table.
     * 
     */
     
    /** Shows distance values on standard output.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
     public void showDistanceValues(String cc, int dec) 
     {
        showDistanceValues(System.out,cc,dec);
        };
        
     
    /** Shows fixed distance values on printstream.
         *@param PS a print stream for the output such as System.out
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
     public void showDistanceValues(PrintStream PS, String cc, int dec) 
     {
        PS.println(cc+"*** Fixed DISTANCE VALUES, scale = "+SEPSTRING+Hamiltonian.distanceScale+SEPSTRING+" short scale="+SEPSTRING+Hamiltonian.shortDistanceScale+SEPSTRING+"***************** ");
        for (int i =0; i<numberSites; i++) {        
            PS.print(SEPSTRING+siteArray[i].name);
        }
        PS.println();
        for (int i =0; i<numberSites; i++) {        
            PS.print(siteArray[i].name);
                for (int j =0; j<numberSites; j++) {        
            PS.print(SEPSTRING+TruncDec(edgeSet.getEdgeDistance(i,j),dec));
            }
            PS.println();
        }
        
    }//eo showDistanceValues

    /** Prints distance values on standard file.
     * <emph>outputnameroot</emph><kbd>_distvalues.dat</kbd> is used.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
public void FileOutputDistanceValues(String cc, int dec) 
    {
      
        String filenamecomplete =  outputFile.getFullLocationFileName("_distvalues","dat");        
        if (message.getInformationLevel()>-2) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            showDistanceValues(PS, cc, dec);

            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-2)System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputDistanceValues



// .............................................................    
    /** Shows scaled distance data table.
         *@param PS a print stream for the output such as System.out
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
      public void showScaledDistanceValues(PrintStream PS, String cc, int dec) {
        double sdv=0.0;
        PS.println(cc+ "--- SCALED DISTANCES in units of "+TruncDec(Hamiltonian.distanceScale,dec)+" -------------");
        for (int i =0; i<numberSites; i++) {        
            PS.print(SEPSTRING+siteArray[i].name);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(edgeSet.getEdgeDistance(i,j)/Hamiltonian.distanceScale,dec);        
            PS.print(SEPSTRING+sdv);
            }
            PS.println();
        }
        
    }//eo showData

    /** Shows scaled distance data table.
    *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
     public void showScaledDistanceValues(String cc, int dec) {
          showScaledDistanceValues(System.out, cc,dec);
     }


// .............................................................    
    /** Shows distance data table in terms of potential.
    *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
     public void showPotentialDistanceValues(String cc, int dec) 
     {
      showPotentialDistanceValues(System.out, cc,  dec);
     }
     
     public void showPotentialDistanceValues(PrintStream PS, String cc, int dec) {
        double sdv=0.0;
        PS.println("--- POTENTIAL SCALE (edge value 1.0) ------");
        for (int i =0; i<numberSites; i++) {        
            PS.print(SEPSTRING+siteArray[i].name);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(edgeSet.getEdgePotential1(i,j),dec);        
            PS.print(SEPSTRING+sdv);
            }
            PS.println();
        }
        
    }//eo showData


// .............................................................    
    /** Shows distance data table in terms of dH values.
     * 
     */
     public void showdHValues(double ev) {
        //String SEPSTRING = "\t";
        int dec = 3;
        double sdv=0.0;
        System.out.println("--- dH Values for edge value 1.0 ------");
        for (int i =0; i<numberSites; i++) {        
            System.out.print(SEPSTRING+siteArray[i].name);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(deltaEdgeHamiltonian(i, j, ev),dec);        
            System.out.print(SEPSTRING+sdv);
            }
            System.out.println();
        }
        
    }//eo showData

// .............................................................
     // .................................................................
    /** Shows edge value table.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void showEdgeValues(String cc,  int dec) 
    {   
       showEdgeValues(cc, System.out, dec);
    }

/**
     * Shows edge value table
     *  <emph>nameroot</emph>_edgevalues.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputEdgeValues(String cc, int dec) 
    {
      
        //String filenamecomplete =  outputnameroot+ "_edgevaluesM"+edgeSet.metricNumber+".dat";        
        String filenamecomplete =  outputFile.getFullLocationFileName("_edgevaluesM"+edgeSet.metricNumber,"dat");        
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            PS.println(cc+"Number of Sites"+SEPSTRING+numberSites);                  
            showEdgeValues(cc, PS, dec);

            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputDijstraStats
    

    /**
     * Makes a string to create a columated break.
     *@param bs string used to make break
     *@param n number of columns to be filled
     *@param sep separtor string e.g. tab
     */
    public String breakString(String bs, int n, String sep)
    {
        String s="";
         for (int i=0; i<n; i++) s+=bs+sep;
        return s;
    }
    
    /** Prints edge value table
     *@param cc comment characters put at the start of every line
     *@param PS PrintStream such as System.out
     *@param dec integer number of decimal palces to display
     */     
public void showEdgeValues(String cc, PrintStream PS, int dec)

{
        double [] toedgecount = new double[numberSites];
        //double fromedgecount;
        PS.println(cc+"EDGE VALUES"+breakString("---",numberSites,SEPSTRING));        
        PS.print("From/to");
        for (int i =0; i<numberSites; i++) {toedgecount[i]=0;}
        for (int i =0; i<numberSites; i++) {PS.print(SEPSTRING+siteArray[i].name);}
        PS.println(SEPSTRING+"Total Out"+SEPSTRING+"Total^2 Out");       
        for (int i =0; i<numberSites; i++) {        
            PS.print(siteArray[i].name);
            for (int j =0; j<numberSites; j++) {
                PS.print(SEPSTRING+TruncDec(edgeSet.getEdgeValue(i,j),dec));
            }
            PS.println(SEPSTRING + TruncDec(edgeSet.getOutEdgeStrength(i) ,dec) + SEPSTRING + TruncDec(edgeSet.getOutStrengthSquared(i) ,dec) );
        }
        PS.print("Total In");
        double total=0.0;
        double ev=0.0;
        for (int i =0; i<numberSites; i++) 
        {
            ev=edgeSet.getInEdgeStrength(i);
            total+=ev;
            PS.print(SEPSTRING+TruncDec(ev,dec));
        }
        PS.println(SEPSTRING+TruncDec(total,dec));
        
        PS.print("Total^2 In");
        total=0.0;
        for (int i =0; i<numberSites; i++) 
        {
            ev=edgeSet.getInStrengthSquared(i);
            total+=ev;
            PS.print(SEPSTRING+TruncDec(ev,dec));
        }
        PS.println(SEPSTRING+TruncDec(total,dec));
        
     }//eo showEdgeValues
    

    

    /** 
     * Prints edge weight table.
     *@param cc comment characters put at the start of every line
     *@param PS PrintStream such as System.out
     *@param dec integer number of decimal palces to display
     */     
public void showEdgeWeights(String cc, PrintStream PS, int dec)
{

    double [][] weights = new double[numberSites][numberSites];
    for (int i=0; i<numberSites; i++)
    {
        for (int j=0; j<numberSites; j++) weights[i][j]=siteArray[i].getWeight()*edgeSet.getEdgeValue(i,j);
    }
    showTable("Weights", weights, cc, PS, dec);
}

//    PS.println(cc+" ***"+SEPSTRING+"Edge Weights"+breakString("***",numberSites,SEPSTRING));        
//        PS.print(cc+"Name");        
//        for (int i=0; i<numberSites; i++)
//        { 
//            PS.print(SEPSTRING+siteArray[i].name);
//        }
//        PS.println(SEPSTRING+"Tot.Out E.W.");       
//        for (int i=0; i<numberSites; i++)
//        {
//           PS.print(siteArray[i].name);
//           for (int j=0; j<numberSites; j++)
//           {
//               PS.print(SEPSTRING+TruncDec(siteArray[i].getWeight()*edgeSet.getEdgeValue(i,j),dec));
//           }
//           PS.println(SEPSTRING+TruncDec(inSiteStrengthWeight[i],dec)+SEPSTRING+siteArray[i].name);                          
//        }
//        PS.print("Tot.OUT E.W.");        
//        for (int i=0; i<numberSites; i++)
//        { 
//                PS.print(SEPSTRING+TruncDec(outSiteStrengthWeight[i],dec));
//        };
//        PS.println();         

    /** 
     * Prints edge weight table.
     *@param dataName name of data
     *@param data square array of doubles of size numberSites, 
     *@param cc comment characters put at the start of every line
     *@param PS PrintStream such as System.out
     *@param dec integer number of decimal palces to display
     */     
public void showTable(String dataName, double [][] data, String cc, PrintStream PS, int dec)
{
       double [] inData = new double [numberSites];
       PS.println(cc+dataName+breakString("***",numberSites,SEPSTRING));        
        PS.print(cc+"Name");        
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SEPSTRING+siteArray[i].name);
            inData[i]=0;
        }
        PS.println(SEPSTRING+"Tot.OUT."+dataName);
        double outTotal=0;
        for (int i=0; i<numberSites; i++)
        {
           PS.print(siteArray[i].name);
           for (int j=0; j<numberSites; j++)
           {
               outTotal+=data[i][j];
               PS.print(SEPSTRING+TruncDec(data[i][j],dec));
           }
           PS.println(SEPSTRING+TruncDec(outTotal,dec)+SEPSTRING+siteArray[i].name);                          
        }
        PS.println();
        double total=0;
        PS.print("Tot.In "+dataName);        
        for (int i=0; i<numberSites; i++)
        { 
            total+=inData[i];
            PS.print(SEPSTRING+TruncDec(inData[i],dec));
        }
        PS.println(SEPSTRING+total);         
}

// ...........................................................................
     
     // .................................................................
    /** Shows colour data values.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void showColourValues(String cc,  int dec) 
    {   
       showColourValues(cc, System.out, dec);
    }

/**
     * Shows colour data values.
     *  <emph>nameroot</emph>_colour.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputColourValues(String cc, int dec) 
    {
      
        //String filenamecomplete =  outputnameroot+ "_colour.dat";        
        String filenamecomplete =  outputFile.getFullLocationFileName("_colour.dat","dat");        
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            PS.println(cc+"Number of Sites"+SEPSTRING+numberSites);                  
            showColourValues(cc, PS, dec);

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
    }//eo FileOutputDijstraStats
    
     /** Shows colour data values
         *@param cc comment characters put at the start of every line
      *@param PS print stream
         *@param dec integer number of decimal palces to display
         */     
public void showColourValues(String cc, PrintStream PS, int dec) 
     {
        PS.println(cc+"--- Colour Values ------------------------------");
        PS.print("From/to");
        for (int i =0; i<numberSites; i++) {PS.print(SEPSTRING+siteArray[i].name);}
        PS.println();       
        PS.print("S.Col.");
        for (int i =0; i<numberSites; i++) {PS.print(SEPSTRING+TruncDec(siteArray[i].displaySize,3));}
        PS.print("\n\nFrom/to");
        for (int i =0; i<numberSites; i++) {PS.print(SEPSTRING+siteArray[i].name);}
        PS.println();       
        for (int i =0; i<numberSites; i++) {        
            PS.print(siteArray[i].name);
            for (int j =0; j<numberSites; j++) {        
            PS.print(SEPSTRING+TruncDec(edgeSet.getEdgeColour(i,j), 3));
            }
            PS.println();
        }
     }//eo showColourValues

         
     
     
     /** Shows site positions and size (fixed data)
         *@param PS a print stream for the output such as System.out
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
     public void showFixedSiteVariables(PrintStream PS, String cc, int dec) 
     {
        PS.println(cc+SEPSTRING+siteArray[0].fixedDataNameString(SEPSTRING) );
        for (int i =0; i<numberSites; i++) 
        {
            PS.println(i+SEPSTRING + siteArray[i].fixedDataString(SEPSTRING));
        } 
        PS.println(cc+SEPSTRING+"MIN"+SEPSTRING+" "+SEPSTRING+minX+SEPSTRING+minY+SEPSTRING+" ");
        PS.println(cc+SEPSTRING+"MAX"+SEPSTRING+" "+SEPSTRING+maxX+SEPSTRING+maxY+SEPSTRING+" ");
        
    }//eo showSiteValuePos

     /* Prints fixed site variables to standard output.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
public void showFixedSiteVariables(String cc, int dec) 
     {
        showFixedSiteVariables(System.out, cc, dec); 
     }


        
    /** 
     * Reads in distance data from file <emph>inputnameroot</emph>_dist.dat.
     * 
     */
    public int getDistanceData() {

        
      String filename = inputFile.getFullFileName("_dist","dat");
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
             siteArray[numberCt]= new IslandSite(numberCt);
             siteArray[numberCt].setName(data.getWord());
             numberCt++;
          }
          numberSites=numberCt;
          int siteFrom,siteTo;
          double dist;
          siteFrom=0;
          while (data.eof() == false) {  // Read until end-of-file.
              siteTo=0;
              data.getWord(); // first entry is name
              while (data.eoln() == false) {  // Read until end-of-file.
              dist= data.getDouble();
              edgeSet.setEdgeDistance(siteFrom,siteTo,dist); 
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





// -------------------------------------------------------------------------
    /** Reads in site values and position from <emph>basicName</emph>_input.dat.
     * <br>First lines are for site data.  
     * Any line starting with a # is a comment and is discarded.
     * Each starts with name of data variable as a key word, 1st 4 char used case insensitive. 
     * Key words at start are: Shor (ShortName), Name, XPos, YPos, ZPos, Size
     * Site values follow after the keyword sparated by white space list of appropriate values.
     * Any line starting with a * indicates the start of a table of distance data with:
     * *Pe... indicates a penalty table applied to as-the-crow-flies distance calculation
     * *Di... indicates distances given directly
     * Each line of a tables start with a dummy entry (e.g. name of site).
     * Finally calculates dist[i][j] data with penalties if necessary
     *@return errors if <0, OK otherwise with 0= no table, 1=penalty table, 2=distance table
     */
    public int getSiteData() 
    {
//      boolean getPenalties = false;
      String filename = inputFile.getFullLocationSimpleFileName(inputFileEnding,"dat"); 
      //if (message.getInformationLevel()>-1) 
      System.out.println("getSiteData() Starting to read site values and position data from " + filename);
      TextReader data;     // Character input stream for reading data.
      
      int numberCt;  // Number of items actually stored in the array
      String datatype="RUBBISH"; // type of data on the line
      String tempstring; 
      String [] sarray = new String[MAXSITENUMBER]; // large number of possible sites allowed for
      IslandSite tempsite = new IslandSite();
      int tabletype=0;
      double [][] temptable = new double [MAXSITENUMBER][MAXSITENUMBER];
          
      
      try {  // Create the input stream.
         data = new TextReader(new FileReader(filename));
      }
      catch (FileNotFoundException e) { 
         System.out.println("Can't find file "+filename);
         return -100;  
      }

      try {
          // Read the data from the input file.
          // read in first line - site names          
          boolean sitedata = true;
          int linenumber=0;
          numberSites = 0;
          while (sitedata && (!data.eof()) )
          { // start reading new line
              linenumber++;
              datatype = data.getWord(); //ignore first item, labels row
              message.println(1,"first word is "+datatype);
              if (datatype.startsWith("#")) 
              { // this line is a comment
                  tempstring=datatype+data.getln(); //comment read to end of line
                  message.println(1,"Comment: "+tempstring);
                  continue;
              }
              
              if (datatype.startsWith("*")) 
              { // this line indicates the start of a new section a la Pajek
                  tempstring=datatype+data.getln(); //comment read to end of line
                  if (message.testInformationLevel(1)) 
                  System.out.println("Table Start: "+tempstring);
                  sitedata=false;
                  continue;
              }
              
              int v = tempsite.dataNameNumber(datatype,4);
              if (v<0) 
              { // No Name of this type found
                  System.out.println("WARNING line "+linenumber+": unknown first word "+datatype+" - line ignored");
                  continue;
              }
            // have recognised type of line now read data on the line in
              numberCt = 0;
              while (data.eoln() == false) 
              {  // Read as strings until end-of-line.
                  sarray[numberCt++] =data.getWord();
              }
            // Initialise if this is the first data line  
              if (numberSites<=0) 
              { // initialise if reading first proper line
                  numberSites= numberCt;
                  initialiseArrays(-1);
              }
              else if (numberSites != numberCt) { 
                  System.out.println("WARNING line "+linenumber+": found  "+numberCt+" columns expected "+numberSites);
                  continue;
              }          
              
              for (int s=0; s<numberSites; s++) 
                   if (siteArray[s].setValue(v,sarray[s])<0)
                   {
                     System.out.println("WARNING line "+linenumber+": wrong type of input in column "+s+" - line ignored");
                     continue;
                   }
               
          }//eo while sitedata

          // set up X,Y positions if latitude (angle from the equator) and longitude only given.
          // otherwise just rescale XY to be 0..1
          if ( siteArray[0].isLatLongSet() && (!siteArray[0].isXYSet()) ) 
          {   double minLat=+999;
              double minLong=+999;
              double maxLat=-999;
              double maxLong=-999;
              for (int s=0; s<numberSites; s++) {
                  if (minLat  > siteArray[s].latitude)  minLat= siteArray[s].latitude;
                  if (minLong > siteArray[s].longitude) minLong = siteArray[s].longitude;
                  if (maxLat  < siteArray[s].latitude)  maxLat= siteArray[s].latitude;
                  if (maxLong < siteArray[s].longitude) maxLong = siteArray[s].longitude;
              }
              double scale=Math.max((maxLong-minLong),(maxLat-minLat))/0.8;
              for (int s=0; s<numberSites; s++) {
              siteArray[s].X = (siteArray[s].longitude-(minLong+maxLong)/2.0) /scale+0.5; 
              siteArray[s].Y = (siteArray[s].latitude-(minLat+maxLat)/2.0) /scale+0.5; 
              }
          }
          else rescaleXY();
          
          if (datatype.substring(0,3).equalsIgnoreCase("*Di")) tabletype=1; // Penalty Table
          if (datatype.substring(0,3).equalsIgnoreCase("*Pe")) tabletype=2; // Distance Table
          //if (data.eof() == false) tabletype=0; // No table
          // Now try to read a table of data
          if (data.eof() == false) 
          {
                int siteFrom,siteTo;
                siteFrom=0;
                while (data.eof() == false) 
                {  // Read until end-of-file.
                    linenumber++;
                    siteTo=0;
                    data.getWord(); // first entry is name
                    while (data.eoln() == false) 
                    {  // Read until end-of-file.
                       temptable[siteFrom][siteTo] = data.getDouble();
                       siteTo++;
                    } //eoln
                    if (siteTo!=numberSites) System.out.println("WARNING line "+linenumber+": Wrong number ("+siteTo+") of distances from site "+siteFrom);
                    if (siteTo<numberSites) tabletype=-3; // this is fatal
                    siteFrom++;              
                }//eofile
                if (siteFrom!=numberSites) System.out.println("WARNING line "+linenumber+": Wrong number ("+siteFrom+") of distance entries in table in "+filename);
                if (siteFrom<numberSites) tabletype=-2; // this is fatal
                                 
          } //eo if penalty
                    
          switch (tabletype)
          {
              case 0: message.println(0,"Finished reading from " + filename+" - No table found"); break;
              case 1: message.println(0,"Finished reading from " + filename+" - Distances read"); break;
              case 2: message.println(0,"Finished reading from " + filename+" - Penalties read"); break;
              default: message.printERROR("Finished reading from " + filename+" *** Unknown Table Type or error in reading it"); tabletype = -10+tabletype; break;
          }
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          System.out.println("Problem reading the data from the input file. Error: " + e.getMessage());
          tabletype=-20+tabletype;
       }
       catch (IndexOutOfBoundsException e) {
          // Must have tried to put too many numbers in the array.
          System.out.println("Too many numbers in data file"+filename);
          System.out.println("Processing has been aborted.");
          tabletype=-30 + tabletype;
       }
       finally {
          // Finish by closing the files, 
          //     whatever else may have happened.
          data.close();
        }

       // Randomise site locations if necessary
       if (jiggleScale>0.0) jiggleSiteData(Hamiltonian.distanceScale*jiggleScale);
       
              // set short names, min,max coord if not already set
       for (int i =0; i<numberSites; i++) 
       {
           siteArray[i].setShortName();
           maxX=Math.max(maxX,siteArray[i].X);
           maxY=Math.max(maxY,siteArray[i].Y);
           minX=Math.min(minX,siteArray[i].X);
           minY=Math.min(minY,siteArray[i].Y);
       }
       
       if (tabletype>=0) { // calculate site distances from X Y positions
           double dist=-99.753;
           for (int i =0; i<numberSites; i++) {
               for (int j =0; j<numberSites; j++) {
                   switch (tabletype) {
                       case 0: //No table found
                           if (siteArray[0].isLatLongSet()) dist = sphericalDistance ( siteArray[i].longitude,siteArray[i].latitude, siteArray[j].longitude, siteArray[j].latitude) ;
                               else dist = euclideanDistance ( siteArray[i].X,siteArray[i].Y, siteArray[j].X, siteArray[j].Y) ;
                           break;
                      case 1: // Distances read
                           dist = temptable[i][j];
                           break;
                       case 2: // Penalties read; 
                           if (siteArray[0].isLatLongSet()) dist = sphericalDistance ( siteArray[i].longitude,siteArray[i].latitude, siteArray[j].longitude, siteArray[j].latitude) ;
                               else dist = euclideanDistance ( siteArray[i].X,siteArray[i].Y, siteArray[j].X, siteArray[j].Y) ;
                           dist = dist * temptable[i][j];
                           break;
                      
                       default: System.out.println("*** Unknown Table Type or error in reading it");  tabletype =-40; break; 
                   } // eo switch
                   edgeSet.setEdgeDistance(i,j,dist);
               } // eo for j
           } //eo for i
       }
    
    // sort sites by alphabetical order
    siteAlphabeticalOrder = new int [numberSites];
    calcSiteOrder(1, siteAlphabeticalOrder, numberSites);

    return tabletype;
    }  // end of getSitePenaltyData() method

    

// **************************************************************************        
    /** 
     * Jiggle coordinates of sites.
     * @param scale absolute distance scale used to jiggle site positions.       
     */
    public void jiggleSiteData(double scale) 
    {
      double theta=-1.0;
      double r=-1.0;
      double dscale=-1.0;
      double newX=0;
      double newY = 0;
      maxX=-9e99;
      maxY=maxX;
      minX=+9e99;
      minY=minX;
      double newdist=0;
      for (int i=0; i<numberSites; i++)
      {
          theta = 2.0*Math.PI*rnd.nextDouble();
          r = (2.0*rnd.nextDouble()-1.0)*scale;
          newX = siteArray[i].X + r*Math.sin(theta);
          newY = siteArray[i].Y + r*Math.cos(theta);
         for (int j=0; j<numberSites; j++)
         {
             dscale = edgeSet.getEdgeDistance(i,j) / euclideanDistance ( siteArray[i].X,siteArray[i].Y, siteArray[j].X, siteArray[j].Y) ;
             newdist = euclideanDistance (newX, newY, siteArray[j].X, siteArray[j].Y)  * dscale;
             edgeSet.setEdgeDistance(i,j, newdist);
             edgeSet.setEdgeDistance(j,i, newdist);
         }
          siteArray[i].X = newX;
          siteArray[i].Y = newY;
          
          maxX=Math.max(maxX,siteArray[i].X);
          maxY=Math.max(maxY,siteArray[i].Y);
          minX=Math.min(minX,siteArray[i].X);
          minY=Math.min(minY,siteArray[i].Y);
      }//eo for i
      


      }// eo  jiggle
    
    
       /** 
     * Convert X Y locations to 0 - 1 scale.
     */
    public void rescaleXY() 
    {
        double minX=siteArray[0].X;
        double maxX=siteArray[0].X;
        double minY=siteArray[0].Y;
        double maxY=siteArray[0].Y;
        for (int i=1; i<numberSites; i++)
        {
            if (siteArray[i].X>maxX) maxX=siteArray[i].X;
            if (siteArray[i].X<minX) minX=siteArray[i].X;
            if (siteArray[i].Y>maxY) maxY=siteArray[i].Y;
            if (siteArray[i].Y<minY) minY=siteArray[i].Y;
        }
        double XOffset = (maxX+minX)/2.0;
        double YOffset = (maxY+minY)/2.0;
        double XScale = (maxX-minX)/0.8;
        double YScale = (maxY-minY)/0.8;
        double Scale = (XScale>YScale) ? XScale : YScale;
        for (int i=0; i<numberSites; i++)
        {
             siteArray[i].X = (( (siteArray[i].X-XOffset)/Scale)+0.5);
             siteArray[i].Y = (( (siteArray[i].Y-YOffset)/Scale)+0.5) ;
        }
    }// eo rescaleXY
        


/**
     * Shows statistics for culture.
     *  <emph>nameroot</emph>_culture.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputCultureStatistics(String cc, int dec) 
    {
        if (networkCulture == null) return;
        //String filenamecomplete =  outputnameroot+ "_culture.dat";        
        String filenamecomplete =  outputFile.getFullLocationFileName("_culture.dat","dat");
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            PS.println(cc+"Number of Sites"+SEPSTRING+numberSites);                  
            networkCulture.print(PS,cc, SEPSTRING, dec);

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
    }//eo FileOutputCultureStatistics
              

//    /**
//     * Shows correlation statistics for culture.
//     *  <emph>nameroot</emph>_cultcorr.dat general info
//     * @param cc comment characters put at the start of every line
//     * @param dec number of decimal places to show
//     */
//    public void FileOutputCultureCorrelations(String cc, int dec) 
//    {
//      
//        String filenamecomplete =  outputnameroot+ "_cultcorr.dat";        
//        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
//            
//        PrintStream PS;
//
//        // next bit of code p327 Schildt and p550
//        FileOutputStream fout;
//        try {
//            fout = new FileOutputStream(filenamecomplete);
//            PS = new PrintStream(fout);
//            showiNVERSION(cc,PS);
//            PS.println(cc+"Number of Sites"+SEPSTRING+numberSites);                  
//            networkCulture.print(PS,cc, SEPSTRING, dec);
//
//            try
//            { 
//               fout.close ();
//               System.out.println("Finished writing to "+ filenamecomplete);
//            } catch (IOException e) { System.out.println("File Error");}
//
//        } catch (FileNotFoundException e) {
//            System.out.println("Error opening output file "+ filenamecomplete);
//            return;
//        }
//        return;
//    }//eo FileOutputCultureCorrelations
//              

    
// -------------------------------------------------------------------
  /**
     * Outputs site information suitable for input.
     * Writes site values, positions and distance data to file <emph>inputnameroot</emph><kbd>_svpdist.dat</kbd>.
     * First line site names
     * Second line site Values (fixed sizes)
     * Third line site X positions
     * Fourth line site Y positions
     * Remaining lines are the dist[i][j] data
     *@param cc comment characters put at the start of every line
         */     

    void FileOutputSiteDistance(String cc)
    {
        //String SEPSTRING = "\t";
        //String filenamecomplete =  inputnameroot+ "_svpdist.dat";        
        String filenamecomplete =  outputFile.getFullLocationFileName("_svpdist","dat");
        //if (message.getInformationLevel()>-1)
            System.out.println("Attempting to write to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            PS.print("Name");            
            for (int i =0; i<numberSites; i++) PS.print(SEPSTRING+siteArray[i].name );
            PS.println();
            PS.print("Size");            
            for (int i =0; i<numberSites; i++) PS.print(SEPSTRING+siteArray[i].size);
            PS.println();
            if (siteArray[0].isXYSet())
            {
                    PS.print("XPos");            
            for (int i =0; i<numberSites; i++) PS.print(siteArray[i].X + SEPSTRING);
            PS.println();
            PS.print("YPos");            
            for (int i =0; i<numberSites; i++) {PS.print(siteArray[i].Y + SEPSTRING);}
            PS.println();
            }
            if (siteArray[0].isLatLongSet())
            {
                    PS.print("Lat");            
            for (int i =0; i<numberSites; i++) PS.print(siteArray[i].latitude + SEPSTRING);
            PS.println();
            PS.print("Long");            
            for (int i =0; i<numberSites; i++) {PS.print(siteArray[i].longitude + SEPSTRING);}
            PS.println();
            }
            for (int i =0; i<numberSites; i++) 
            {
               PS.print(siteArray[i].name );
               for (int j =0; j<numberSites; j++) PS.print(SEPSTRING + edgeSet.getEdgeDistance(i,j));
               PS.println();
            }        
            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputBareNetwork




// ---------------------------------------------------------------------------------

    /** Calculate site distances from X Y positions
     *@param usePenalties true if use penalties[x][y]
     */
    public void calcDistances(boolean usePenalties)
    {
       
    }  // end of calcDistances() method



        



/* ************************************************************************
 *
 * OUTPUT ROUTINES
 *
 */  
    
 // -------------------------------------------------------------------
  /**
     * Outputs basic parameters.
     *  <emph>nameroot</emph>_param.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputNetworkParam(String cc, int dec) 
    {
       // String SEPSTRING = "\t";
        //String filenamecomplete =  outputnameroot+ "_param.dat";        
        String filenamecomplete =  outputFile.getFullLocationFileName("_param","dat");
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            PS.println("Number of Sites"+SEPSTRING+numberSites);                  
        showHamiltonianParameters(PS);
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputNetworkStats

    
    
    
    // -------------------------------------------------------------------
  /**
     * Outputs bare network in simple tab delimited format.
     *  <emph>nameroot</emph>_info.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputNetworkStatistics(String cc, int dec) 
    {
       // String SEPSTRING = "\t";
        //String filenamecomplete =  outputnameroot+ "_info.dat";        
        String filenamecomplete =  outputFile.getFullLocationFileName("_info","dat");
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            PS.println("Number of Sites"+SEPSTRING+numberSites);                  
        showHamiltonianParameters(PS);
        // calcNetworkStats(); must be calc first
        showNetworkStatistics(cc, PS, dec);
        if ((outputMode & 128) >0) showDistanceValues(PS,cc, dec);
              

            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputNetworkStats
    
    /**
     * Prints iNVERSION.
     *@param cc comment string
     */
    public void showiNVERSION(String cc) {
        showiNVERSION(cc,System.out);
    }//eo showiNVERSION
    
    /**
     * Shows iNVERSION.
     *@param cc comment string
     *@param PS a print stream such as System.out
     */
    public void showiNVERSION(String cc, PrintStream PS) 
    {
        Date date = new Date();
        PS.println(cc+SEPSTRING+" ---"+"ARIADNE"+SEPSTRING+" iNVERSION "+SEPSTRING+iNVERSION+SEPSTRING+", date "+SEPSTRING+date+SEPSTRING+" ---");
    }//eo showiNVERSION
   
    
        /* 
         * Shows Parameters of Hamiltonian on std output
         */
    
    public void showHamiltonianParameters() 
    {
        showHamiltonianParameters(System.out);
        }//eo showHam

        

           /* 
         * Shows Parameters on a PrintStream in form suitable for printing.
         *@param PS a print stream such as System.out
         */
    public void showHamiltonianParameters(PrintStream PS) {
             PS.println("--- Parameters for Hamiltonian Model number"+SEPSTRING+modelNumber.major+"_"+modelNumber.minor );
             PS.println("    "+modelNumber.majorString + " " +modelNumber.minorString );
             Hamiltonian.printParameters(PS,SEPSTRING);
             PS.println("      metric number "+SEPSTRING+edgeSet.metricNumber);
             PS.println("           edgeMode "+SEPSTRING+edgeSet.edgeMode.edgeModeValue()+ SEPSTRING+edgeSet.edgeMode.description());
             PS.println("         vertexMode "+SEPSTRING+vertexMode.descriptionValue(SEPSTRING));
    }//eo showHam


        /* 
         * Shows input parameters in form suitable for data file.
         *@param PS a print stream such as System.out
         */
    public void showInputParameters(PrintStream PS) {
             PS.println("Model"+SEPSTRING+ modelNumber.majorString +SEPSTRING+ modelNumber.minorString );
             Hamiltonian.printParameters(PS,SEPSTRING);
             PS.println("Metric" +SEPSTRING +edgeSet.metricNumber);
             PS.println("edgeMode" +SEPSTRING + edgeSet.edgeMode.edgeModeValue());
    }//eo showInputParameters


        /** Prints Network Statistics.
         * @param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void printNetworkStatistics(String cc, int dec) {
        showNetworkStatistics(cc, System.out, dec);
    }

    
    
    /** Prints Network and Basic Statistics in Tim Network File (tnf) ASCII form for later reinput.
     * <br><i>outputnameroot</i><tt>.anf</tt> file produced.
     *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void FileOutputNetworkForData(String cc, int dec) 
    {    
       String filenamecomplete =  outputFile.getFullLocationFileName("",NETWORKDATAFILEEXT);        
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showNetworkForData(PS, cc, dec);
            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }
    
    /** Shows Network and Basic Statistics in ASCII form for later reinput.
     *@param PS printstream
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    private void showNetworkForData(PrintStream PS, String cc, int dec) 
    {    
        
          showiNVERSION(cc,PS);
           // general modelling  parameters
          PS.print("*param");
          PS.print(SEPSTRING+ "-v"+modelNumber.number);
          PS.print(SEPSTRING+ "-u"+updateMode.getValue());
          PS.println(SEPSTRING+ vertexMode.modeString());
          
          //Hamiltonian parameters
          PS.println("*param"+SEPSTRING+Hamiltonian.inputParametersString(SEPSTRING));
          
          PS.println("*sites"+SEPSTRING+numberSites);
          PS.println(siteArray[0].parameterStringNames(SEPSTRING));
          for (int s=0; s<this.numberSites; s++) PS.println(siteArray[s].parameterStringValues(SEPSTRING,5));
          
          PS.println("*edges");
          edgeSet.printNames(PS,  SEPSTRING);
          edgeSet.printValues(PS,  SEPSTRING, dec);    
    }
    
    /** Shows Network and Basic Statistics in ASCII form.
     * <tt>calcNetworkStats()</tt> must be called first.
         *@param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal palces to display
         */
    public void showNetworkStatistics(String cc, PrintStream PS, int dec) 
    {    
        //double ew,vw,ev,vv;
        PS.println("Energy "+SEPSTRING+energy);
        PS.println(cc+" ***"+SEPSTRING+"Site Weights and Values"+breakString("***",numberSites,SEPSTRING));        
        int numberSiteVariables = siteArray[0].getNumberVariables();
        for (int v=0; v<numberSiteVariables; v++)
        {
           PS.print(siteArray[0].dataName(v)); 
           for (int i=0; i<numberSites; i++)
                PS.print(SEPSTRING+siteArray[i].toString(v));
           PS.println();         
        }
     
        
        // edge values
        showEdgeValues(cc, PS, dec);
        
        // edge weights
        showEdgeWeights(cc, PS, dec);
        
        // Influence
        PS.println(cc+" ***"+SEPSTRING+" Influence");
        PS.println(cc+" Influence Prob. "+influenceProb);
        PS.print(cc+"Name");        
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SEPSTRING+siteArray[i].name);
        }
        PS.println();
        PS.print("Infl.Rank");
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SEPSTRING+siteArray[i].influenceRank);
        }
        PS.println(SEPSTRING+"Infl.Rank");
//        showTable("Influence",siteRank.influenceMatrix,cc,PS,dec);
        siteRank.showInfluenceMatrix(cc,PS,dec);


        // Culture
        if (networkCulture != null) {
            PS.println(cc+" ***"+SEPSTRING+"Culture"+SEPSTRING + "(column j's culture at site row i) ***** ");
            PS.print(cc+" Culture Probabilities  "+networkCulture.eProb.toString(SEPSTRING));
            PS.print(cc+"Name"+SEPSTRING+SEPSTRING);
            for (int i=0; i<numberSites; i++) {
                PS.print(SEPSTRING+siteArray[i].name);
            }
            PS.println();
            networkCulture.print(PS,cc,SEPSTRING,3);
        }

        
        PS.println(cc+"*** SCALES *******************");    
    PS.println(cc+"Single Edge Max Value        "+SEPSTRING+TruncDec(maxSiteValue,dec) +SEPSTRING+" at vertex "+maxSiteValueIndex);   
    PS.println(cc+"Single Edge Max Weight       "+SEPSTRING+TruncDec(allmaxedgeweight,dec)+SEPSTRING+" at vertex "+allmaxedgeweightindex);   
    PS.println(cc+"Single Site Max Weight       "+SEPSTRING+TruncDec(maxSiteWeight,dec)+SEPSTRING+" at vertex "+maxSiteWeightIndex);   
    PS.println(cc+"Single Site Max out Strength "+SEPSTRING+TruncDec(maxOutSiteStrength,dec)+SEPSTRING+" at vertex "+maxOutSiteStrengthIndex);   
    PS.println(cc+"*** TOTALS *******************");    
    PS.println(cc+"Total Vertex Value and Weight "+SEPSTRING+TruncDec(totVertexValue,dec)+SEPSTRING+TruncDec(totVertexWeight,dec));        
    PS.println(cc+"  Total Edge Value and Weight "+SEPSTRING+TruncDec(totEdgeValue,dec)+SEPSTRING+TruncDec(totEdgeWeight,dec));         

//        int maxvedgeweightindex = -1; 
//


    
    PS.println(cc+"--- Display Factors --------------------------------");    
    PS.println(cc+"Zero, Minimum Fraction for coloured edges = "+SEPSTRING+zeroColourFrac+SEPSTRING+minColourFrac);
    PS.println(cc+"Max Site and Edge Size = "+SEPSTRING+siteWeightFactor+SEPSTRING+edgeWidthFactor);
    

              
    } //eo showNet.Stats
    

        /** 
         * Produces various types of Pajek vertex files.
         *@param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal palces to display
         *@param variableNumber write file associated with this Site class variable number
         */
    public void printPajekSiteFiles(String cc, PrintStream PS, int dec, int variableNumber) 
    {
        for (int i=0; i<numberSites; i++) PS.println(siteArray[i].toString(variableNumber));
    }
    
//        /** Produces values associated with sites as a string.
//         *@param valuenumber 
//         */
//    public String siteString(int valuenumber) 
//    {
//        String s="Unknown Value"+valuenumber;
//        switch (valuenumber)
//        {
//            
//            case 0:  break;
//            default: 
//        }
//             
//        return s;
//    }
// .................................................................
        /** Shows Network in various ways.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void showNetwork(String cc, int dec) 
    {   //minColourFrac=0.0;
        setOutputFileName();       
        System.out.println("\n --- Displaying Network --------------------------------------");
              System.out.println("Output mode "+outputMode+" " +outputModeString());
              System.out.println("Scale for largest vertices in .net file= "+DisplayMaxVertexScale);
              System.out.println("Scale for maximum edges in .net file = "+DisplayMaxEdgeScale);
              System.out.println("Minimum Fraction for coloured edge in .net file = "+minColourFrac);
              System.out.println("Zero Fraction for coloured edge = "+zeroColourFrac);
              calcNetworkStats();
              FileOutputNetworkForData( "#" , 5);
              FileOutputNetworkStatistics("#",3);
              //FileOutputColourValues("#", dec);
//              int oldmetricNumber=edgeSet.metricNumber;
//              for (int mn=0; mn<edgeSet.numberMetrics; mn++)
//              {
//                  edgeSet.metricNumber=mn;
//                  edgeSet.doDijkstra(siteArray);
//                  FileOutputEdgeValues("#", dec);
//                  FileOutputDijkstraStatistics("#", dec);
//                  }
//              edgeSet.metricNumber=oldmetricNumber;
//              edgeSet.doDijkstra(siteArray);
              //siteRank.FileOutputTransferMatrix("#", dec);
              //siteRank.FileOutputInfluenceMatrix("#", dec);
              
              //for (int v=0; v<siteArray[0].getNumberVariables(); v++) FileOutputPajekSiteFiles(v);
              
              // Pajek file output
              final String[] pn =
                 {"Value", "Weight", "Ranking", "Strength", "StrengthIn", "StrengthOut"};
              for (int i=0; i<pn.length; i++) FileOutputPajekSiteFiles(pn[i]);         
              FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,0);
//              FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,1);
//              FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,2);
//              FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,3);
//              FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,4);
//              FileOutputCultureStatistics("#",3);
//              FileOutputCultureCorrelations("#",3);
//              FileOutputBareNetwork("#");
              message.println(2,"showNetwork inputFile " + inputFile.getFullLocationFileRoot());
              NetworkWindow PW = new NetworkWindow(this);
              message.println(2,"showNetwork PW.islnet.inputnameroot " + PW.islnet.inputFile.getFullFileRoot());
              PW.drawNetworkWindow();
              message.println(2,"In showNetwork finished call to PW.drawNetworkWindow()");
//              SiteWindow SW = new SiteWindow(this);
//              message.println(2,"showNetwork SW.islnet.inputnameroot " + SW.islnet.inputnameroot );
//              SW.drawSiteWindow();
//              message.println(2,"In showNetwork finished call to SW.drawSiteWindow
              
    }
    
// .................................................................
    /** Shows Dijstra Statistics on std output.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void showDijkstraValues(String cc,  int dec) 
    {
        showDijkstraValues(cc,System.out, dec);
    }

        // -------------------------------------------------------------------
  /**
     * Outputs bare network in simple tab delimited format.
     *  <emph>nameroot</emph>_dijkdist.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputDijkstraStatistics(String cc, int dec) 
    {
      
        //String filenamecomplete =  outputnameroot+ "_dijkdistM"+edgeSet.metricNumber+".dat";        
        String filenamecomplete =  outputFile.getFullLocationFileName("_dijkdistM"+edgeSet.metricNumber,"dat");        
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
        PS.println(cc+"Number of Sites"+SEPSTRING+numberSites);                  
        showDijkstraValues(cc, PS, dec);

            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputDijstraStats
    

 
    /** Shows Dijstra Statistics
         *@param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal places to display
         */
    public void showDijkstraValues(String cc, PrintStream PS, int dec) 
    {
        PS.println("--- Dijkstra Results for metric "+edgeSet.getMetricString()+", metric number"+edgeSet.metricNumber);                  
        if (edgeSet.DijkstraMaxSep<edgeSet.MAXSEPARATION) PS.println("Connected, max distance "+ TruncDec(edgeSet.DijkstraMaxSep,dec));                  
        else PS.println("DISCONNECTED");    
        double totaldistance =0;
        // site names on line 1
        PS.print("From/To");        
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SEPSTRING+siteArray[i].name);
//            if (i==DijkstraVertex) PS.print("#");
//            else PS.print(" ");         
        }
        PS.println();        
        // Dijkstra values on line 2
        for (int i=0; i<numberSites; i++)
        {
           PS.print(siteArray[i].name);        
           for (int j=0; j<numberSites; j++)
           {
               
            if (edgeSet.getEdgeSeparation(i,j)==edgeSet.MAXSEPARATION) PS.print(SEPSTRING+" x ");
            else 
            {
                totaldistance+=edgeSet.getEdgeSeparation(i,j);
                PS.print(SEPSTRING+TruncDec(edgeSet.getEdgeSeparation(i,j),dec));
            }
           }
        PS.println();        
        }
        PS.println("\n Total Distance (excl. x) "+totaldistance);
        for (int i=0; i<numberSites; i++) PS.println("..."+SEPSTRING);        
    }    


    


    
    
 // *******************************************************************************    
    
         /** Calculate Network Separations (Effective Distances).
          *
         */
         
    public void calcNetworkSeparations() 
    {
        siteDistanceStats = new StatisticalQuantity(edgeSet.MAXSEPARATION, -1.0);
        edgeSet.doDijkstra(siteArray);
        for (int i=0; i<numberSites; i++) 
        {
            for (int j=0; j<numberSites; j++)
            {
                double d= edgeSet.getEdgeSeparation(i,j);
                if (d<edgeSet.MAXSEPARATION) siteDistanceStats.add(edgeSet.getEdgeSeparation(i,j));
            }
        }
    }
  // *******************************************************************************    
    
         /** Calculate some Network Statistics.
          * Sets maxSiteWeight is the largest vertex weight, 
          *  and comes from vertex maxSiteWeightindex.
          * maxEdgeWeight[i] is largest edge weight if vertex i and
          * allmaxedgeweight is the largest overall
          *  Does not set minimum and zero colours.
         */
         
    public void calcNetworkStats() 
    {
      double vw=0;
      double ew=0;
      maxSiteValue=0;
      maxSiteValueIndex=0;
      allmaxedgeweight =0;
      allmaxedgeweightindex =0;
      maxSiteWeightIndex = 0;
      maxSiteWeight =0;
//      maxvedgeweightindex = 0;  
      maxOutSiteStrength =0;
      maxOutSiteStrengthIndex =0;
      totVertexValue=0;
      totEdgeValue=0;
      totVertexWeight=0;
      totEdgeWeight=0;
        
      siteRank = new SiteRanking(numberSites);
      //siteRank.calcRanking(numberSites*20+10);
      siteRank.calcRanking(1);
      siteRank.calcInfluence(influenceProb);
      
      // CULTURE SECTION
//      calcCulture();
//      for (int i=0; i<numberSites; i++) {
//          siteArray[i].cultureMax = networkCulture.maxCulture[i];
//          siteArray[i].cultureSite = networkCulture.maxCultureSite[i];
//      }
      

      // Now calculate Ranking Order siteWeightOrder[i] = number of site ranked i   
      siteWeightOrder = new int [numberSites]; 
      for (int i=0; i<numberSites; i++) siteArray[i].setWeight();
      calcSiteRankOrder(7,siteWeightOrder,8); //7 = weight, 8= WeightRank
      
        // site and edge weights and value
       inSiteStrengthWeight =  new double[numberSites];
       outSiteStrengthWeight =  new double[numberSites];
       inSiteStrengthValue =  new double[numberSites];
       outSiteStrengthValue =  new double[numberSites];
       double [] inSiteStrengthValue2 =  new double[numberSites];
       double [] outSiteStrengthValue2 =  new double[numberSites];
       for (int i=0; i<numberSites; i++)
        {
          inSiteStrengthValue[i] =0.0;
          inSiteStrengthWeight[i]=0.0;  
          outSiteStrengthWeight[i]=0.0;
          outSiteStrengthValue[i]=0.0;
          inSiteStrengthValue2[i]=0.0;
          outSiteStrengthValue2[i]=0.0;
        }
      double vV,eV,eV2;
      siteWeightStats = new StatisticalQuantity(99999.0,-1.0);
      edgeWeightStats = new StatisticalQuantity(99999.0,-1.0);     
      edgeValueStats = new StatisticalQuantity(99999.0,-1.0);     
      siteStrengthOutStats = new StatisticalQuantity(99999.0,-1.0);     
      siteStrengthInStats = new StatisticalQuantity(99999.0,-1.0);     
        for (int i=0; i<numberSites; i++)
        {
          vV=siteArray[i].value;
          vw=siteArray[i].getWeight(); // siteArray[i].name has weight vw  
          siteWeightStats.add(vw); // updates the stats on the site weights
          if (maxSiteValue<vV) {maxSiteValue = vV; maxSiteValueIndex=i;}
      
          
          if (vw > maxSiteWeight) 
          {
           maxSiteWeight = vw;
           maxSiteWeightIndex = i;
          }
          totVertexValue+= vV;
          totVertexWeight+= vw;

           // find largest edge weight from i to any j
           maxEdgeWeight[i]=0;   
           maxEdgeWeightIndex[i] = 0;
           outSiteStrengthWeight[i]=0.0;
           outSiteStrengthValue[i]=0.0;
           outSiteStrengthValue2[i]=0.0;
           for (int j=0; j<numberSites; j++)
           {
               eV=edgeSet.getEdgeValue(i,j);
               eV2=eV*eV;
               ew = vw*eV; // edge weight
               totEdgeValue  += eV; 
               totEdgeWeight += ew; 
               inSiteStrengthValue[j]+=eV;
               inSiteStrengthValue2[j]+=eV2;
               inSiteStrengthWeight[j]+=ew;
               outSiteStrengthValue[i]+=eV;
               outSiteStrengthValue2[i]+=eV2;
               outSiteStrengthWeight[i]+=ew;
               edgeValueStats.add(eV); // updates the stats on the edge weights
               edgeWeightStats.add(ew); // updates the stats on the edge weights
               if (ew > maxEdgeWeight[i]) 
               {
                 maxEdgeWeight[i] = ew;
                 maxEdgeWeightIndex[i] = j;
               }
           }// eo for j
//           outSiteStrength[i]=outstrength;
           siteStrengthOutStats.add(outSiteStrengthWeight[i]);
           if (maxOutSiteStrength < outSiteStrengthWeight[i])  
           {
               maxOutSiteStrength = outSiteStrengthWeight[i];
               maxOutSiteStrengthIndex =i;
           }
           siteStrengthInStats.add(inSiteStrengthWeight[i]);
           if (maxInSiteStrength < inSiteStrengthWeight[i])  
           {
               maxInSiteStrength = inSiteStrengthWeight[i];
               maxInSiteStrengthIndex =i;
           }
           if (maxEdgeWeight[i] > allmaxedgeweight) 
           {
              allmaxedgeweight = maxEdgeWeight[i];
              allmaxedgeweightindex = maxEdgeWeightIndex[i];
           }
           
        }// eo for i
      
 
      
//      int minColourNumber =(int)(numberColours*minColourFrac);
//      int zeroColourNumber =(int)(numberColours*zeroColourFrac);
      // set site and edge weight colours
      int vwcolour;
      int ewcolour;
      double mvw,amew;
      double swi=1.0; // No site weight on edges in PPA mode
      if (DisplayMaxVertexScale>0) mvw = DisplayMaxVertexScale;
      else mvw= maxSiteWeight;
      if (DisplayVertexType ==1) mvw=1.0;
      if (DisplayMaxEdgeScale>0) amew =  DisplayMaxEdgeScale;
      else amew = allmaxedgeweight;
      
      edgeSet.displayZeroEdgeWeight = amew*zeroColourFrac;
      edgeSet.displayMinimumEdgeWeight = amew*minColourFrac;
      edgeSet.displayMaximumEdgeWeight = amew;

        for (int i=0; i<numberSites; i++)
        {
           siteArray[i].strengthIn=inSiteStrengthWeight[i];
           siteArray[i].strengthSquaredIn=inSiteStrengthValue2[i];
           siteArray[i].strengthOut=outSiteStrengthWeight[i];
           siteArray[i].strengthSquaredOut=outSiteStrengthValue2[i];
           siteArray[i].strength=inSiteStrengthWeight[i]+outSiteStrengthWeight[i];
           switch(DisplayVertexType)
           {
               case 1: siteArray[i].displaySize = siteArray[i].ranking*numberColours/mvw; 
                   break;
               case 2:
               case 0:
               default: siteArray[i].displaySize = siteArray[i].getWeight()*numberColours/mvw; // siteArray[i].name has weight vw
           }            
           //if (siteArray[i].displaySize>numberColours) siteArray[i].displaySize=numberColours;
           if (siteArray[i].displaySize<0) siteArray[i].displaySize=0;
           siteArray[i].Z=siteArray[i].displaySize;
           if (updateMode.getValue()>0) swi=siteArray[i].getWeight();
// Problem here???  for (int i=0; i<numberSites; i++) siteArray[i].getWeight(); 
           for (int j=0; j<numberSites; j++)
           {
               edgeSet.setEdgeColour(i,j, swi*numberColours*edgeSet.getEdgeValue(i,j)/amew); // edge weight colour 
               if (edgeSet.getEdgeColour(i,j)>numberColours) edgeSet.setEdgeColour(i,j,numberColours);
               if (edgeSet.getEdgeColour(i,j)<numberColours*minColourFrac)
               {  if (edgeSet.getEdgeColour(i,j)<numberColours*zeroColourFrac) edgeSet.setEdgeColour(i,j,0);
                  else edgeSet.setEdgeColour(i,j, numberColours*zeroColourFrac);
               }
               
           }           
        }
      if (updateMode.getValue()>0) 
      {
        for (int i=0; i<numberSites; i++)
        {
           for (int j=0; j<numberSites; j++)
           {
            if ((edgeSet.getEdgeColour(i,j)<=zeroColourFrac*numberColours) &&  (edgeSet.getEdgeColour(j,i)>zeroColourFrac*numberColours) ) 
                 edgeSet.setEdgeColour(i,j, numberColours*zeroColourFrac); 
           }
        }
      }
      
     calcEnergy(); 
      
    }
    
    // -------------------------------------------------------------------
  /**
     * Calculates the culture for the network. 
     */
    void calcCulture()  
    {
      double [] siteWeight = new double[numberSites];
      double [] [] edgeValue = new double[numberSites][numberSites];
     
      for (int s=0; s< numberSites; s++)
      {
          siteWeight[s]= siteArray[s].getWeight();
          for (int t=0; t< numberSites; t++) edgeValue[s][t] = edgeSet.getEdge(s,t).value;
      }
      int indPerSite = 20;
      int numberInd = numberSites*indPerSite;
      double pr = 1.0/((double)indPerSite );
      double fracinnovate = 0.5;
      culturePInnovate = pr*fracinnovate;
      double fraccopy = 1.0-1.0/((double) numberSites);
      culturePSiteCopy=(1.0 - pr)*fraccopy;
      culturePCopy=(1.0 - pr)*(1.0-fraccopy);
      networkCulture = new IslandCulture(culturePSiteCopy ,culturePCopy , culturePInnovate );
      networkCulture.setup(siteWeight, edgeValue, numberInd, 1);
      int tau2 = (int)(0.5 -1.0/Math.log(1-2*(culturePSiteCopy)/(numberSites)));
      networkCulture.message.setInformationLevel(message.getInformationLevel());
      networkCulture.evolve((int)(tau2*cultureTimeScale+0.5));
      networkCulture.calcAllStats();
    }
 // -------------------------------------------------------------------
  /**
     * Sets up colours for Pajek. 
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
            gns = Integer.toString(gn);
            if (gn>90) gns="95";
            if (gn<10) gns = "05";
            pajekGrey[i] = "Gray"+gns;
        }
        pajekGrey[numberColours] = "Black";
        
        Color purple = new Color(255,0,255);
        javaColour[0]=Color.white;
        javaColour[1]=Color.yellow;
        javaColour[2]=Color.pink;
        javaColour[3]=Color.cyan;
        javaColour[4]=Color.orange;
        javaColour[5]=Color.magenta;
        javaColour[6]= purple;
        javaColour[7]=Color.green;
        javaColour[8]=Color.blue;
        javaColour[9]=Color.red;
        javaColour[numberColours]=Color.black;
        
    }   

// ###################################################################
// SiteRanking class includes transfer matrix and influence functions


/**
 *  Site Ranking Class
 *
 */
    public class SiteRanking
    {
// Care: transferMatrix[i][j] is from j to i as in normal matrix language
        IslandTransferMatrix transferMatrix;
        
        int [] siteRankOrder; // [i]= site ranked i-throws by Rank
        int [] siteRankOverWeightOrder; // [i]= site ranked i-throws by Rank/Size
        int [] siteInfluenceOrder;
        int numberRankSites;
        
        // next are all in transferMatrix now
         // int influenceSteps=-1;
        //double influenceProb=-1.0;
        //double [] [] influenceMatrix;
        

          // constructor;
          public SiteRanking (int numberSites)
          {
           numberRankSites= numberSites;
           // Care: transferMatrix[i][j] is from j to i as in normal matrix language
//           transferMatrix = new IslandTransferMatrix();
           //influenceMatrix = new double [numberSites][numberSites];
           siteRankOrder = new int [numberRankSites]; // [i] = number of site of rank i by rank
           siteRankOverWeightOrder = new int [numberRankSites]; // [i] = number of site of rank i by rank/size
           siteInfluenceOrder = new int [numberRankSites]; //[i] = number of site ranked i by influence
           }

          // constructor deep copy;
          public SiteRanking (SiteRanking oldsr)
          {
              numberRankSites= oldsr.numberRankSites ;
           //influenceProb = oldsr.influenceProb;
           // Care: transferMatrix[i][j] is from j to i as in normal matrix language
//           transferMatrix = new IslandTransferMatrix(oldsr.transferMatrix);
           //influenceMatrix = new double [numberSites][numberSites];
           siteRankOrder = new int [numberRankSites];
           siteRankOverWeightOrder = new int [numberRankSites]; // [i] = number of site of rank i by rank/size
           siteInfluenceOrder  = new int [numberRankSites];
           
           for (int i=0; i<numberRankSites; i++)
            {
                siteRankOrder[i]= oldsr.siteRankOrder[i];
                siteRankOverWeightOrder[i] = oldsr.siteRankOverWeightOrder[i];
                siteInfluenceOrder[i] = oldsr.siteInfluenceOrder[i];
                
            }//eo for i

           }//eo deep copy constructor
          
// *******************************************************************************



         /**
          * Calculate some ranking based on diffusion transfer matrix.
          *@param tranferMatrixType type of Transfer matrix to choose
          */

    public void calcRanking(int tranferMatrixType)
    {
        transferMatrix = new IslandTransferMatrix(1, numberRankSites, siteArray, edgeSet);
        int m = transferMatrix.checkMarkovian(1e-3);
        if (m>=0) System.out.println(" !!! transferMatrix failed Markovian test at column "+m);
        double evmax = transferMatrix.getAbsEigenValue(0); // count from zero!
        if (evmax<0.99) System.out.println("!!! WARNING in calcRanking largest absolute eigenvalue of transfer matrix has value "+evmax);
        
        double [] evectorMax = new double[numberRankSites];
        evectorMax = transferMatrix.getEigenVector(0); //count from zero, may have negative entries

        double norm=0.0;
        for (int i=0; i<numberRankSites; i++) {
            siteArray[i].ranking = Math.abs(evectorMax[i]);
            norm+=siteArray[i].ranking;
        }
        if (norm>0) for (int i=0; i<numberRankSites; i++) siteArray[i].ranking=siteArray[i].ranking/norm;
        for (int i=0; i<numberRankSites; i++) 
        {
            if (siteArray[i].getWeight()>0) siteArray[i].rankOverWeight = siteArray[i].ranking/siteArray[i].getWeight();
            else siteArray[i].rankOverWeight = 0;
        }

        // Now calculate Ranking Order siteRankOrder[i] = number of site ranked i-th by ranking
         calcSiteRankOrder(9, siteRankOrder, 10 ); //9=Rank, 10=RankingRank
        
        // Now calculate Order using siteRankOverWeightOrder[i] = number of site ranked i-th by rankOverSize
         calcSiteRankOrder(19, siteRankOverWeightOrder, 20 ); //19=rankOverWeight, 20=rankOverWeightRank

        }// eo calcRanking


// ----------------------------------------------------------------------
    /** Calculate an Influence Matrix, influenceMatrix[i][j] is influence of j on i.
      *
      *@param influenceProbNew probability to use for influence calculations
      */

    public void calcInfluence(double influenceProbNew)
    {
        influenceProb = influenceProbNew;
        transferMatrix.calcInfluenceMatrix(influenceProb);
        
        
    }
    
    
    /** Calculate an Influence Matrix, influenceMatrix[i][j] is influence of j on i.
      * <br> IM(t+1) = 1 + IM(t)* (p TM); IM(t_final)= IM(t_final)*siteWeight
      *
      *@param influenceProbNew probability to use for influence calculations
      */

    public void calcInfluenceOld(double influenceProbNew)
    {
        siteInfluenceOrder = new int [numberRankSites]; //[i] = number of site ranked i by influence

        double [] [] influenceMatrix = new double [numberSites][numberSites]; // added to avoid errors
        double [] [] influenceMatrixOdd = new double [numberSites][numberSites];
        double [] [] pTMatrix = new double [numberSites][numberSites];
        influenceProb = influenceProbNew;
        if ((influenceProb<0) || (influenceProb>1) ) influenceProb=0.0;
        double influenceSteps = numberSites/2; // initialise to maximum value
        double influenceProbMax = influenceSteps/((double) (1.0+influenceSteps));
        if (influenceProb<influenceProbMax )
             influenceSteps = ((int) (0.5+ influenceProb/(1.0-influenceProb)));

        if (message.getInformationLevel()>-1) System.out.println(" --- Starting to calculate TransferMatrix for Influence ");

        transferMatrix = new IslandTransferMatrix(1, numberSites, siteArray, edgeSet);
        
//        System.out.println(" --- Starting to calculate Influence over "+influenceSteps+" steps");
        // initialise and set up first step away from site
        // Remember  edgeValue[j][i] is from j to i
        //double firstStepRemain =maxOutSiteStrength;
        for (int i=0; i<numberSites; i++)
        {
          for (int j=0; j<numberSites; j++)
          {
             //pTMatrix[i][j]=transferMatrix[i][j]*influenceProb;
             pTMatrix[i][j]=edgeSet.getEdgeValue(j,i)*influenceProb;
             influenceMatrix[i][j]=  (( (i==j) ? 1 : 0.0 ) + edgeSet.getEdgeValue(j,i)*influenceProb ) *siteArray[j].getWeight() ;
          }
        }// eo for i

        for (int t=1; t<2*influenceSteps; t+=2)
        {
//          System.out.println(" --- Starting Influence step "+t);
          // IM(t+1) = 1 + IM(t)* (p TM) for even t
          for (int i=0; i<numberSites; i++)
          {
           for (int j=0; j<numberSites; j++)
           {
            double tot=0;
            for (int k=0; k<numberSites; k++)
            {
              tot+=pTMatrix[i][k]*influenceMatrix[k][j];
            }//eo for k
            influenceMatrixOdd[i][j]=((i==j)?siteArray[j].getWeight() : 0)+tot;
           }// eo for j
          }//eo for i

//          System.out.println(" --- Starting Influence 2nd step "+(t+1));

          // IM(t+1) = 1 + IM(t)* (p TM) for odd t
          for (int i=0; i<numberSites; i++)
          {
           for (int j=0; j<numberSites; j++)
           {
            double tot=0;
            for (int k=0; k<numberSites; k++)
            {
              tot+=pTMatrix[i][k]*influenceMatrixOdd[k][j];
            }//eo for k
            influenceMatrix[i][j]=(((i==j)?siteArray[j].getWeight() : 0 ) + tot);
           }// eo for j
          }//eo for i

        }// eo for t


        // Now calculate max influence on each site
         double [] totalInfluenceWeight = new double [numberRankSites]; // Total influence by weight of site i
        int [] siteInfluence = new int [numberRankSites]; // owner of site i by influence
        int [] siteInfluenceRank = new int [numberRankSites]; // rank by influence

         for (int i=0; i<numberRankSites; i++)
            {
                siteInfluence[i]=0;
                double maxInfluence = influenceMatrix[i][0];
                for (int j=1; j<numberRankSites; j++)
                {
                 if ( (maxInfluence < influenceMatrix[i][j])
                  || ( (maxInfluence < influenceMatrix[i][j]) && (rnd.nextDouble() <0.5)) )
                 {
                     maxInfluence = influenceMatrix[i][j];
                     siteInfluence[i] = j;
                 }
                }//eo for j
            }//eo for i

        // Now remove client states
        boolean notupdated=true;
        for  (int t=0; t<numberRankSites; t++)
        {
        notupdated=true;
         for (int i=0; i<numberRankSites; i++)
            {
                if (siteInfluence[i] != i)
                {
                  for (int j=1; j<numberRankSites; j++)
                  {
                      if (siteInfluence[j] == i) siteInfluence[j]=siteInfluence[siteInfluence[i]];
                      notupdated = false;
                  }// eo for j
                } // eo if
            }//eo for i
          if (notupdated) break;
        }//eo for t
        if (!notupdated) System.out.println(" *** Warning circular influence suspected");
        for (int i=0; i<numberRankSites; i++) siteArray[i].influence=siteInfluence[i];
        

        // calculate total weighted influence of site
        for (int i=0; i<numberRankSites; i++) totalInfluenceWeight[i]=0;
        for (int i=0; i<numberRankSites; i++) totalInfluenceWeight[siteInfluence[i]]+=siteArray[i].getWeight();
        // Rank total influence
         calcVectorRankOrder(totalInfluenceWeight,siteInfluenceOrder,siteInfluenceRank);
         for (int i=0; i<numberRankSites; i++)
         {
             siteArray[i].totalInfluenceWeight=totalInfluenceWeight[i];
             siteArray[i].influenceRank=siteInfluenceRank[i];
         }

    }// eo calcInfluence
// -------------------------------------------------------------------

     /*
     * Returns value of the Influence matrix I_{ij}.
     *@param i the target site
     *@param j the source site
     *@return the entry or -97531 if there is a problem.
     */
     public double getInfluenceMatrix(int i, int j)
     {
       return transferMatrix.getInfluence(i,j);
     }

  /**
   *  Method of Rank
     * Outputs transfer matrix used for ranking in simple tab delimited format
     *  <emph>nameroot</emph>_transmat.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputTransferMatrix(String cc, int dec)
    {

        //String filenamecomplete =  outputnameroot+ "_transmat.dat";
        String filenamecomplete =  outputFile.getFullLocationFileName("_transmat.dat","dat");
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);

        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
        PS.println(cc+"Number of Sites"+SEPSTRING+numberRankSites);
        showTransferMatrix(cc, PS, dec);

            try
            {
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputTransferMatrix

    // ...............................................................
        /** Shows TransferMatrix.
         *@param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal places to display
         */
    public void showTransferMatrix(String cc, PrintStream PS, int dec)
    {
        transferMatrix.printTransferMatrix(cc, SEPSTRING, PS, dec, true);
    } // eo showTransferMatrix
// ...........................................................................
        /** Shows Transfer Matrix on std output.
          *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void showTransferMatrix(String cc,  int dec)
    {
        showTransferMatrix(cc,System.out, dec);
    }


// -------------------------------------------------------------------
  /**
     * Outputs influence matrix used for ranking in simple tab delimited format.
     *  <emph>nameroot</emph>_transmat.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputInfluenceMatrix(String cc, int dec)
    {

        //String filenamecomplete =  outputnameroot+ "_inflmat.dat";
        String filenamecomplete =  outputFile.getFullLocationFileName("_inflmat.dat","dat");        
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);

        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
        PS.println(cc+"Number of Sites"+SEPSTRING+numberRankSites);
        showInfluenceMatrix(cc, PS, dec);

            try
            {
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputInfluenceMatrix

    // ...............................................................
        /** Shows InfluenceMatrix.
         *@param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal places to display
         */
    public void showInfluenceMatrix(String cc, PrintStream PS, int dec)
    {
        transferMatrix.printInfluenceMatrix(cc, SEPSTRING, PS, dec, true);
    } // eo showInfluenceMatrix
// ...........................................................................
        /** Shows Influence Matrix on std output.
          *  Method of Rank
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void showInfluenceMatrix(String cc,  int dec)
    {
        showInfluenceMatrix(cc,System.out, dec);
    }





} // eo Rank Class

// ######################################################################
    
    
// *******************************************************************
        
// -------------------------------------------------------------------
  /**
     * Outputs bare network in simple tab delimited format.
     *  <emph>nameroot</emph>BareNet.dat general info
     * @param cc comment characters put at the start of every line
     * 
     */
    void FileOutputBareNetwork(String cc){

        //String SEPSTRING = "\t";
        //String filenamecomplete =  outputnameroot+ "BareNet.dat";
        String filenamecomplete =  outputFile.getFullLocationFileName("_BareNet","dat");
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
        PS.println(cc+"Line 0"+SEPSTRING+"Number Sites,"
           +SEPSTRING+"Line 1"+SEPSTRING+"Site Size,"
           +SEPSTRING+"Line 2"+SEPSTRING+"Vertex Values,"+SEPSTRING
                     +"Line >=3"+SEPSTRING+"Edge Values Value");
        PS.println(numberSites);                  
        for (int i =0; i<numberSites; i++) {PS.print(siteArray[i].size + SEPSTRING);}
        PS.println();
        for (int i =0; i<numberSites; i++) {PS.print(siteArray[i].value + SEPSTRING);}
        PS.println();
        for (int i =0; i<numberSites; i++) {        
            for (int j =0; j<numberSites; j++) {
            PS.print(edgeSet.getEdgeValue(i,j) + SEPSTRING);
            }
            PS.println();
          } // eo for i

            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputBareNetwork

// -------------------------------------------------------------------
  /**
     * Outputs network in Pajek file format.
     *  <emph>nameroot</emph>.net general info
     * @param cc comment characters put at the start of every line
     * @param siteWeightFactor Maximum dot size for sites
     * @param edgeWidthFactor Maximum edge width in diagrams
     * @param minColourFrac fraction of total colours represented as colour 1
     * @param zeroColourFrac fraction of total colours represented as colour 0
     * @param fileType 0=plain, 1= BW, 2= colour, 3=Influence Matrix, 4=Culture Corrrelation Matrix
     */
    void FileOutputNetwork(String cc, int siteWeightFactor, int edgeWidthFactor, 
                           double minColourFrac, double zeroColourFrac, 
                           int fileType)  {

        double ew,vw;
        
        // Convert X Y locations to 0 - 1 scale for Pajek
        // now in rescaleXY()
//        double minX=siteArray[0].X;
//        double maxX=siteArray[0].X;
//        double minY=siteArray[0].Y;
//        double maxY=siteArray[0].Y;
//        for (int i=1; i<numberSites; i++)
//        {
//            if (siteArray[i].X>maxX) maxX=siteArray[i].X;
//            if (siteArray[i].X<minX) minX=siteArray[i].X;
//            if (siteArray[i].Y>maxY) maxY=siteArray[i].Y;
//            if (siteArray[i].Y<minY) minY=siteArray[i].Y;
//        }
//        double XOffset = (maxX+minX)/2.0;
//        double YOffset = (maxY+minY)/2.0;
//        double XScale = (maxX-minX)/0.8;
//        double YScale = (maxY-minY)/0.8;
//        double Scale = (XScale>YScale) ? XScale : YScale;
        
        String filenamecomplete;
        switch (fileType)
        {
            case 1: filenamecomplete = outputFile.getFullLocationFileName("BW","net"); break;
            case 2: filenamecomplete = outputFile.getFullLocationFileName("C","net"); break;
            case 3: filenamecomplete = outputFile.getFullLocationFileName("Infl","net"); break;
            case 4: filenamecomplete = outputFile.getFullLocationFileName("CultCorr","net"); break;
            case 0: 
            default: filenamecomplete = outputFile.getFullLocationFileName("PLAINV","net");
        }
        
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write to "+ filenamecomplete);
            
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
            double fracSize;
            for (int i=0; i<numberSites; i++)
            { 
              int sc=(int) (0.499999+ siteArray[i].displaySize);
              if (sc>numberColours) sc= numberColours;
              if (sc<minColourFrac*numberColours) sc=1;
              if (sc<zeroColourFrac*numberColours) sc=0;
              PS.print((i+1)+"  \""+siteArray[i].name+"\" "
                       + siteArray[i].X + " " 
                       + siteArray[i].Y + " ");
              switch (fileType) 
              {
                  case 2:
                  case 1: PS.print( siteArray[i].Z
                          +" s_size 1 " 
                          + " x_fact "+TruncDec(siteArray[i].displaySize,2) 
                          + " y_fact "+TruncDec(siteArray[i].displaySize,2));
                          break;
                  case 4:
                  case 3:        
                  case 0: 
                  default: PS.print( "  "+siteArray[i].getWeight()); 
              }
              switch (fileType) 
              {
                  case 1: PS.println(" ic "+pajekGrey[sc]+"   bc "+pajekGrey[numberColours]); break;
                  case 2: PS.println(" ic "+pajekColour[sc]+"   bc "+pajekColour[numberColours]); break;
                  case 4:
                  case 3:
                  case 0:
                  default: PS.println();
              }
       
            }
            
            //for the arcs
            //      1      2       1 c Blue
            // gives an arc between vertex 1 and 2, value 1 colour black
            switch (fileType) {
                case 4:
                case 3:
                    PS.println("*Arcs    "+numberSites*numberSites);
                    break;
                case 0:
                case 1:
                case 2:
                default:
                    int arcCount =0;
                    for (int i=0; i<numberSites; i++) {
                        for (int j=0; j<numberSites; j++) {
                            if (edgeSet.getEdgeColour(i,j)> numberColours*zeroColourFrac) arcCount++;
                        }
                    };
                    PS.println("*Arcs    "+arcCount);
                    break;
            }

            int width;
            for (int i=0; i<numberSites; i++) {
                vw=siteArray[i].getWeight();
                for (int j=0; j<numberSites; j++) {
                    if (fileType==3) {
                        PS.println((i+1)+"  "+(j+1)+"   " + siteRank.getInfluenceMatrix(i,j) + " w "+ vw*edgeSet.getEdgeValue(i,j));
                        continue;
                    }
                    if (fileType==4) {
                        if ((i!=j) && (networkCulture != null)) PS.println((i+1)+"  "+(j+1)+"   " + networkCulture.cultureCorrelation[i][j] + " w "+ vw*edgeSet.getEdgeValue(i,j));
                        continue;
                    }
                    
                    int ec=(int) (0.499999+edgeSet.getEdgeColour(i,j));
                    if ((i==j) || (ec<=zeroColourFrac*numberColours) ) continue;
                    if (ec>numberColours) ec=numberColours;
                    if (ec<minColourFrac*numberColours) ec=1;
                    if (updateMode.isPPA()) ew=((double) ec)/((double) numberColours);
                    else ew= vw*edgeSet.getEdgeValue(i,j);
                    width = (edgeWidthFactor*ec)/numberColours;
                    PS.print((i+1)+"  "+(j+1)+"   " + ew + " w "+ width);
                    switch (fileType) {
                        case 1: PS.println("  c "+pajekGrey[ec]); break;
                        case 2: PS.println("  c "+pajekColour[ec]); break;
                        case 0:
                        default: PS.println();
                    } // eo switch                    
                }
            }// eo for i

            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }

// -------------------------------------------------------------------
  /**
     * Outputs files for sites in Pajek file formats.
     *  <emph>nameroot</emph>.ext
     * where the .ext is selected by the input string and is 
     * .clu for integers (partitions) and .vec for doubles (vectors).
     * @param variableName start of name of site variable required
     * 
     */
    public boolean  FileOutputPajekSiteFiles(String variableName)  
    {
        return FileOutputPajekSiteFiles(siteArray[0].getIndex(variableName));
    }
    
 // -------------------------------------------------------------------
  /**
     * Outputs files for sites in Pajek file formats.
     *  <emph>nameroot</emph>.ext
     * where the .ext is selcted by the input parameter and is 
     * .clu for integers (partitions) and .vec for doubles (vectors).
     * @param variableNumber number of site variable required
     * 
     */
    public boolean FileOutputPajekSiteFiles(int variableNumber)  
    {

        IslandSite site = siteArray[0];
        String ext="No Such File";
//        switch(variableNumber)
//            {
//                case 5: ext="vec"; break; //s=Double.toString(size); break;
//                case 6: ext="vec"; break; // s=Double.toString(value); break;
//                case 7: ext="vec"; break; //s=Double.toString(weight); break;
//                case 8: ext="clu"; break; //s=Integer.toString(weightRank); break;
//                case 9: ext="vec"; break; //s=Double.toString(ranking); break;
//                case 10: ext="clu"; break; //s=Integer.toString(rankingRank); break;
//                case 11: ext="vec"; break; //s=Double.toString(totalInfluenceWeight); break;
//                case 12: ext="clu"; break; //s=Integer.toString(influence); break; 
//                case 13: ext="clu"; break; // int influenceRank=-1;  //13
//                case 14: ext="vec"; break; //int displaySize=-1; //14
//                case 15: ext="vec"; break; //double strength=-1; //15
//                case 16: ext="vec"; break; // double strengthIn=-1; //16
//                case 17: ext="vec"; break; // double strengthOut=-1; //17
//                case 21: ext="vec"; break; // double cultureMax =-1; //21 value of largest cultural influence
//                case 22: ext="clu"; break; // 22 site  of largest cultural influenceint cultureSite =1; 
//            default:    ext="No Such File";          
//            }

        // only give file if variable is of type integer or double
        if (site.isInt(variableNumber)) ext="clu";
        else if (site.isDouble(variableNumber)) ext="vec";
        else return false;
        
        String filenamecomplete =  outputFile.getFullLocationFileName(site.dataName(variableNumber),ext);
        if (message.getInformationLevel()>-1) System.out.println("Attempting to write to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            PS.println("*Vertices "+numberSites);
            for (int i=0; i<numberSites; i++ ) PS.println(siteArray[i].toString(variableNumber));

            try
            { 
               fout.close ();
               if (message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error"); return false;}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return false;
        }
        return true;
    }
    
    
/* *******************************************************************************
 * Windowing Routines
 */

    
// replaced by TimUtility    
//     public static void initLookAndFeel() {
//        String lookAndFeel = null;
//
//        if (LOOKANDFEEL != null) {
//            if (LOOKANDFEEL.equals("Metal")) {
//                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
//            } else if (LOOKANDFEEL.equals("System")) {
//                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
//            } else if (LOOKANDFEEL.equals("Motif")) {
//                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
//            } else if (LOOKANDFEEL.equals("GTK+")) { //new in 1.4.2
//                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
//            } else {
//                System.err.println("Unexpected value of LOOKANDFEEL specified: "
//                                   + LOOKANDFEEL);
//                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
//            }
//
//            try {
//                UIManager.setLookAndFeel(lookAndFeel);
//            } catch (ClassNotFoundException e) {
//                System.err.println("Couldn't find class for specified look and feel:"
//                                   + lookAndFeel);
//                System.err.println("Did you include the L&F library in the class path?");
//                System.err.println("Using the default look and feel.");
//            } catch (UnsupportedLookAndFeelException e) {
//                System.err.println("Can't use the specified look and feel ("
//                                   + lookAndFeel
//                                   + ") on this platform.");
//                System.err.println("Using the default look and feel.");
//            } catch (Exception e) {
//                System.err.println("Couldn't get specified look and feel ("
//                                   + lookAndFeel
//                                   + "), for some reason.");
//                System.err.println("Using the default look and feel.");
//                e.printStackTrace();
//            }
//        }
//    }// private static void initLookAndFeel()



        





 // ------------------------------------------------------------------------------
 //  class PictureWindow was here
 // ------------------------------------------------------------------------------

 // ------------------------------------------------------------------------------
 // class SiteWindow was here
 // ------------------------------------------------------------------------------ 


 // ------------------------------------------------------------------------------
 // class InputWindow was here
 // ------------------------------------------------------------------------------ 



        
///**  *******************************************************************************
// *
// *  InputWindow class.  This is now InputParameterFrame.
// *   <p>Built up from Celcius Converter programme of SWING example web site.
// */
//      
//    public class InputWindow implements ActionListener {
//    JFrame inputFrame;
//    JPanel buttonPanel,inputPanel,displayPanel,modelNumberPanel, modePanel, DVTPanel;
//    JTextField  fileOutputValue, runnameValue,
//               muValue,jValue,kappaValue,lambdaValue,distScaleValue,reldistScaleValue,alphaValue,betaValue,bValue,gammaValue,
//               updateModeValue, maxVertexValue, edgeModeValue, majorModelNumberValue, minorModelNumberValue;
//    JLabel fileInputValue, fileInputMessage, fileOutputMessage, runnameMessage,
//            muMessage,jMessage,kappaMessage,lambdaMessage,distScaleMessage,reldistScaleMessage,alphaMessage,betaMessage,bMessage,gammaMessage,
//               updateModeMessage, maxVertexMessage, edgeModeMessage, modelNumberMessage;
//    JLabel notInPPAlabel =  new JLabel("Not used in PPA", SwingConstants.RIGHT);
//    JTextField sSFValue, eWFValue, mCFValue, zCFValue, DMVSValue, DMESValue, ipValue, ctsValue; 
//    JLabel sSFMessage, eWFMessage, mCFMessage, zCFMessage, DMVSMessage, DMESMessage, DVTMessage, ipMessage, ctsMessage;
//    ButtonGroup updateModeGroup, updateDVTGroup;
//    JRadioButton DPMode,VPMode,PPAMode,MCMode, InfluenceMode, RankMode, SizeMode;
//    //JCheckBox NewNetworkStyleCB;
//// for site window    
//           JLabel SWMMessage;
//           JPanel SWMPanel1,SWMPanel2;
//           ButtonGroup SWMGroup;
//           JRadioButton alphaMode, weightMode, rankMode, rankOverWeightMode;
//
//    
//    JLabel messageLabel, drawMessageLabel;
//    JButton calcButton, drawButton;
//    JCheckBox autonameCB;
//    
//    String resMessage="OK";
//    
//    public InputWindow() {
//        //Create and set up the window.
//        inputFrame = new JFrame("Inputs for "+inputFile.getFullFileRoot()+ " Ariadne Network"+ iNVERSION);
//        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        inputFrame.setSize(new Dimension(400,400));
//
//        //Create and set up the panel.
//        buttonPanel = new JPanel(new GridLayout(1, 2));
//        inputPanel = new JPanel(new GridLayout(16, 2));
//        displayPanel = new JPanel(new GridLayout(13, 2));
//
//        //Add the widgets.
//        addWidgets();
//        
//
//        //Set the default button.
//        inputFrame.getRootPane().setDefaultButton(calcButton);
//
//        // Add buttons and info box
//        calcButton = new JButton("CALCULATE");
//        buttonPanel.add(calcButton);
//        //Listen to events from the Calc button.
//        calcButton.addActionListener(this);
//
//        drawButton = new JButton("REDRAW");
//        buttonPanel.add(drawButton);
//        //Listen to events from the Draw button.
//        drawButton.addActionListener(this);
//
//
//        
//        
//        //Add the panel to the window.
//        inputFrame.getContentPane().add(inputPanel, BorderLayout.WEST);        
//        inputFrame.getContentPane().add(displayPanel, BorderLayout.EAST);
//        inputFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
//        
//        //Display the window.
//        inputFrame.pack();
//        inputFrame.setVisible(true);
//    } // eo constructor InputWindow()
//
//    /**
//     * Create and add the widgets.
//     */
//    private void addWidgets() {
//        //Create widgets.
//        
// // Code fragment on how to make this active       
// //        DocumentListener myListener = ??;
// //    JTextField myArea = ??;
// //    myArea.getDocument().addDocumentListener(myListener);
//        
//        islandNetwork i = new islandNetwork(1);
//                
//        fileInputMessage = new JLabel("Basic Input File Name ", SwingConstants.RIGHT);
//        fileInputValue = new JLabel(inputFile.getFullFileRoot(), SwingConstants.CENTER);
//        inputPanel.add(fileInputMessage);
//        inputPanel.add(fileInputValue);
//        
//        // Buttons for processing mode
//        updateModeGroup = new ButtonGroup();
//        VPMode = new JRadioButton("VP Mode");
//        VPMode.setActionCommand("V");
//        VPMode.setToolTipText("Edges set equal to potential");
//        DPMode = new JRadioButton("DP Mode");
//        DPMode.setActionCommand("D");
//        DPMode.setToolTipText("Edges set equal to 1/(1+distance/scale)");
//        PPAMode = new JRadioButton("PPA Mode");
//        PPAMode.setActionCommand("P");
//        PPAMode.setToolTipText("Proximal Point Analysis with edges ranked by distance");
//        MCMode = new JRadioButton("MC Mode");
//        MCMode.setToolTipText("Monte Carlo mode");
//        MCMode.setActionCommand("M");
////        MCMode.setSelected(true);
//        updateModeGroup.add(VPMode);
//        updateModeGroup.add(DPMode);
//        updateModeGroup.add(PPAMode);
//        updateModeGroup.add(MCMode);
//        JPanel modePanel1 = new JPanel(new GridLayout(1, 2));
//        JPanel modePanel2 = new JPanel(new GridLayout(1, 2));
//        modePanel1.add(VPMode);
//        modePanel1.add(DPMode);
//        modePanel2.add(PPAMode);
//        modePanel2.add(MCMode);
//        inputPanel.add(modePanel1);
//        inputPanel.add(modePanel2);
//        inputPanel.add(new JLabel(""));
//        switch (updateMode)
//        {
//            case 0: PPAMode.setSelected(true);  break;
//            case 2: DPMode.setSelected(true);  break;
//            case 3: VPMode.setSelected(true);  break;
//            case 1: 
//            default: MCMode.setSelected(true);  
//        }
////        if (command.equals("P")) updateMode=0;
////            if (command.equals("M")) updateMode=1;
//
//        
//        // Register a listener for the radio buttons.
//        RadioListener modeListener = new RadioListener();
//        VPMode.addActionListener(modeListener);
//        DPMode.addActionListener(modeListener);
//        PPAMode.addActionListener(modeListener);
//        MCMode.addActionListener(modeListener);
//        
//        autonameCB= new JCheckBox("Automatic Output File Names");
//        autonameCB.setSelected(true);
//        inputPanel.add(autonameCB);
//        
//        
//        String edgeModeInitial = " "+i.edgeSet.edgeMode.edgeModeValue();
//        edgeModeValue = new JTextField(edgeModeInitial,4);
//        edgeModeMessage = new JLabel("Maximum Edge Value ", SwingConstants.RIGHT);
//        inputPanel.add(edgeModeMessage);
//        inputPanel.add(edgeModeValue);
//        
//        String maxVertexInitial = " "+i.vertexMaximum;
//        maxVertexValue = new JTextField(maxVertexInitial,4);
//        maxVertexMessage = new JLabel("Max Vertex Value ", SwingConstants.RIGHT);
//        inputPanel.add(maxVertexMessage);
//        inputPanel.add(maxVertexValue);
//        
//        modelNumberPanel = new JPanel(new GridLayout(1, 3));
//        String majorInitial = i.modelNumber.major+" ";
//        String minorInitial = i.modelNumber.minor+" ";
//        majorModelNumberValue = new JTextField(majorInitial,1);
//        minorModelNumberValue = new JTextField(minorInitial,1);     
//        modelNumberPanel.add(majorModelNumberValue);
//        modelNumberPanel.add(new JLabel("."));
//        modelNumberPanel.add(minorModelNumberValue); 
//        modelNumberMessage = new JLabel("Model Number ", SwingConstants.RIGHT);
//        inputPanel.add(modelNumberMessage);
//        inputPanel.add( modelNumberPanel);
//         
////        fileOutputValue = new JTextField(outputFile.getBasicRoot().substring(0,15),16);
//        fileOutputValue = new JTextField(outputFile.getBasicRoot(),16);
//        fileOutputMessage = new JLabel("Basic Root for Output Files ", SwingConstants.RIGHT);
//        inputPanel.add(fileOutputMessage);
//        inputPanel.add(fileOutputValue);
//        
//        runnameValue = new JTextField(""+outputFile.sequenceNumber,16);
//        runnameMessage = new JLabel("Run Number ", SwingConstants.RIGHT);
//        inputPanel.add(runnameMessage);
//        inputPanel.add(runnameValue);
//        
//        String jInitial = ""+i.Hamiltonian.vertexSource;
//        jValue = new JTextField(jInitial,4);
//        jMessage = new JLabel("j ", SwingConstants.RIGHT);
//        inputPanel.add(jMessage);
//        //if (updateMode==0) inputPanel.add(notInPPAlabel); else 
//        inputPanel.add(jValue);
//        
//        String muInitial = " "+i.Hamiltonian.edgeSource;
//        muValue = new JTextField(muInitial,4);
//        muMessage = new JLabel("mu ", SwingConstants.RIGHT);
//        inputPanel.add(muMessage);
//        inputPanel.add(muValue);
//        
//        String kappaInitial = ""+i.Hamiltonian.kappa;
//        kappaValue = new JTextField(kappaInitial,4);
//        kappaMessage = new JLabel("kappa ", SwingConstants.RIGHT);
//        inputPanel.add(kappaMessage);
//        inputPanel.add(kappaValue);
//        
//        String lambdaInitial = ""+i.Hamiltonian.lambda;
//        lambdaValue = new JTextField(lambdaInitial,4);
//        lambdaMessage = new JLabel("lambda ", SwingConstants.RIGHT);
//        inputPanel.add(lambdaMessage);
//        inputPanel.add(lambdaValue);
//        
//        String distScaleInitial = ""+i.Hamiltonian.distanceScale;
//        distScaleValue = new JTextField(distScaleInitial,4);
//        distScaleMessage = new JLabel("Distance Scale ", SwingConstants.RIGHT);
//        inputPanel.add(distScaleMessage);
//        inputPanel.add(distScaleValue);
//        
//        String reldistScaleInitial = ""+i.Hamiltonian.shortDistanceScale;
//        reldistScaleValue = new JTextField(reldistScaleInitial,4);
//        reldistScaleMessage = new JLabel("Short Distance ", SwingConstants.RIGHT);
//        inputPanel.add(reldistScaleMessage);
//        inputPanel.add(reldistScaleValue);
//        
//        if (updateMode==0) i.Hamiltonian.beta=3.0;
////        else i.Hamiltonian.beta=1.0;
//        String betaInitial = ""+i.Hamiltonian.beta;
//        betaValue = new JTextField(betaInitial,4);
//        betaMessage = new JLabel("beta ", SwingConstants.RIGHT);
//        inputPanel.add(betaMessage);
//        inputPanel.add(betaValue);
//        
//        String bInitial = ""+i.Hamiltonian.getb();
//        bValue = new JTextField(bInitial,4);
//        bMessage = new JLabel("b ", SwingConstants.RIGHT);
//        inputPanel.add(bMessage);
//        inputPanel.add(bValue);
//        
//        // Now list display vraiables
//        displayPanel.add(new JLabel("<html><em>Display Variables</em></html>", SwingConstants.CENTER) );
//        displayPanel.add(new JLabel("<html><em>Values</em>", SwingConstants.LEFT) );
//        
//        String sSFInitial = ""+i.siteWeightFactor;
//        sSFValue = new JTextField(sSFInitial,4);
//        sSFMessage = new JLabel("<html>Maximum Site Size </html>", SwingConstants.RIGHT);
//        displayPanel.add(sSFMessage);
//        displayPanel.add(sSFValue);
//        
//        String eWFInitial = " "+i.edgeWidthFactor;
//        eWFValue = new JTextField(eWFInitial,4);
//        eWFMessage = new JLabel("<html>Maximum Edge Width </html>", SwingConstants.RIGHT);
//        displayPanel.add(eWFMessage);
//        displayPanel.add(eWFValue);
//        
//        String zCFInitial = " "+i.zeroColourFrac;
//        zCFValue = new JTextField(zCFInitial,4);
//        zCFMessage = new JLabel("<html>Fraction for Zero Colour </html>", SwingConstants.RIGHT);
//        displayPanel.add(zCFMessage);
//        displayPanel.add(zCFValue);
//        
//        String mCFInitial = " "+i.minColourFrac;
//        mCFValue = new JTextField(mCFInitial,4);
//        mCFMessage = new JLabel("<html>Fraction for Minimum Colour </html>", SwingConstants.RIGHT);
//        displayPanel.add(mCFMessage);
//        displayPanel.add(mCFValue);
//        
//        String DMVSInitial = " "+i.DisplayMaxVertexScale;
//        DMVSValue = new JTextField(DMVSInitial,4);
//        DMVSMessage = new JLabel("<html>Max. Vertex Size </html>", SwingConstants.RIGHT);
//        displayPanel.add(DMVSMessage);
//        displayPanel.add(DMVSValue);
//        
//        String DMESInitial = " "+i.DisplayMaxEdgeScale;
//        DMESValue = new JTextField(DMESInitial,4);
//        DMESMessage = new JLabel("<html>Max. Edge Size </html>", SwingConstants.RIGHT);
//        //DMESMessage.setForeground(Color.CYAN);
//        displayPanel.add(DMESMessage);
//        displayPanel.add(DMESValue);
//
//        String ipInitial = " "+i.influenceProb;
//        ipValue = new JTextField(ipInitial,4);
//        ipMessage = new JLabel("<html>Influence Prob.</html>", SwingConstants.RIGHT);
//        //DMESMessage.setForeground(Color.CYAN);
//        displayPanel.add(ipMessage);
//        displayPanel.add(ipValue);
//        
//        String ctsInitial = " "+i.cultureTimeScale;
//        ctsValue = new JTextField(ctsInitial,4);
//        ctsMessage = new JLabel("<html>Culture Time</html>", SwingConstants.RIGHT);
//        //DMESMessage.setForeground(Color.CYAN);
//        displayPanel.add(ctsMessage);
//        displayPanel.add(ctsValue);
//        
//        // Buttons for DisplayVertexType
//        updateDVTGroup = new ButtonGroup();
//        SizeMode = new JRadioButton("Size");
//        SizeMode.setActionCommand("NVSize");
//        RankMode = new JRadioButton("Rank");
//        RankMode.setActionCommand("NVRank");
////        RankMode.setSelected(true);
//        InfluenceMode = new JRadioButton("Influence");
//        InfluenceMode.setActionCommand("NVInfluence");
//        updateDVTGroup.add(SizeMode);
//        updateDVTGroup.add(RankMode);
//        updateDVTGroup.add(InfluenceMode);
//        DVTPanel = new JPanel(new GridLayout(1, 3));
//        DVTPanel.add(SizeMode);
//        DVTPanel.add(RankMode);
//        DVTPanel.add(InfluenceMode);
//        DVTMessage = new JLabel("<html>Display vertices by</html>", SwingConstants.RIGHT);
//        displayPanel.add(DVTMessage);
//        displayPanel.add(DVTPanel);
//        // set set button to be equal to current value
//        switch (DisplayVertexType)
//        {
//            case 2: InfluenceMode.setSelected(true);  break;
//            case 1: RankMode.setSelected(true);  break;
//            case 0: 
//            default: SizeMode.setSelected(true);  
//        }
//
//        // Register a listener for the radio buttons.
//        //RadioListener DVTmodeListener = new RadioListener();
//        SizeMode.addActionListener(modeListener);
//        RankMode.addActionListener(modeListener);
//        InfluenceMode.addActionListener(modeListener);
//        
//        //
//        //NewNetworkStyleCB = new JCheckBox("New style network display");
//        //NewNetworkStyleCB.setSelected(newNetworkDisplaySyle);
//        //NewNetworkStyleCB.addActionListener(modeListener);
//        //displayPanel.add(new JLabel(" "));
//        //displayPanel.add(NewNetworkStyleCB);
//        
//        SWMGroup = new ButtonGroup();
//        JRadioButton numMode = new JRadioButton("Num.");
//        numMode.setActionCommand("SWNum");
//        alphaMode = new JRadioButton("Alph.");
//        alphaMode.setActionCommand("SWAlpha");
////        alphaMode.setSelected(true);
//        weightMode = new JRadioButton("Weight");
//        weightMode.setActionCommand("SWWeight");
//        rankMode = new JRadioButton("Rank");
//        rankMode.setActionCommand("SWRank");
//        rankOverWeightMode = new JRadioButton("Rank/Weight");
//        rankOverWeightMode.setActionCommand("SWRankOverWeight");
//        SWMGroup.add(numMode);
//        SWMGroup.add(alphaMode);
//        SWMGroup.add(weightMode);
//        SWMGroup.add(rankMode);
//        SWMGroup.add(rankOverWeightMode);
//        SWMPanel1 = new JPanel(new GridLayout(1, 3));
//        SWMPanel1.add(numMode);
//        SWMPanel1.add(alphaMode);
//        SWMPanel1.add(weightMode);
//        SWMPanel2 = new JPanel(new GridLayout(1, 3));
//        SWMPanel2.add(rankMode);
//        SWMPanel2.add(rankOverWeightMode);
//        // set set button to be equal to current value
//        switch (siteWindowMode)
//        {
//            case 4: rankOverWeightMode.setSelected(true);  break;
//            case 2: rankMode.setSelected(true);  break;
//            case 1: weightMode.setSelected(true);  break;
//            case 0: numMode.setSelected(true);  break;
//            case 3: 
//            default: alphaMode.setSelected(true);  
//        }
//
//
//        SWMMessage = new JLabel("Site Window by", SwingConstants.RIGHT);
//        displayPanel.add(SWMMessage);
//        displayPanel.add(SWMPanel1);
//        displayPanel.add(new JLabel(" "));
//        displayPanel.add(SWMPanel2);
//        // Register a listener for the radio buttons.
////        RadioListener SWMListener = new RadioListener();
//        numMode.addActionListener(modeListener);
//        alphaMode.addActionListener(modeListener);
//        weightMode.addActionListener(modeListener);
//        rankMode.addActionListener(modeListener);
//        rankOverWeightMode.addActionListener(modeListener);
//
//        
//        /*  modelNumber.major = 1;
//             modelNumber.minor = 0;
//             message.getInformationLevel() = 0;
//             updateMode = 1; // Sweep
//             edgeModeBinary = false;
//          */   
//        //Add the widgets to the container.
//        
////        messageLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
////        jLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//    } // eo addWidgets
//
//
//
//
//
//    /** Listens to the radio buttons. */
//    class RadioListener implements ActionListener 
//    { 
//        public void actionPerformed(ActionEvent e) 
//        {
//            String command = e.getActionCommand() ;
//            if (command.equals("P")) updateMode=0;
//            if (command.equals("M")) updateMode=1;
//            if (command.equals("D")) updateMode=2;
//            if (command.equals("V")) updateMode=3;
//            if (command.equals("NVSize")) DisplayVertexType = 0;
//            if (command.equals("NVRank")) DisplayVertexType = 1;
//            if (command.equals("NVInfluence")) DisplayVertexType = 2; 
//            if (command.equals("SWNum")) siteWindowMode = 0;
//            if (command.equals("SWAlpha")) siteWindowMode = 3;
//            if (command.equals("SWWeight")) siteWindowMode = 1;
//            if (command.equals("SWRank")) siteWindowMode = 2;
//            if (command.equals("SWRankOverWeight")) siteWindowMode = 4;
//
//            
//        }
//    }
//    
//    
//    
//    
//    public void actionPerformed(ActionEvent event) {
//        //Parse degrees Celsius as a double and convert to Fahrenheit.
//        int res=0;
//                
//        try
//        {
//            String command = event.getActionCommand();
//            
//            inputFile.setNameRoot(fileInputValue.getText());
//            outputFile.setNameRoot(fileOutputValue.getText());
//            outputFile.sequenceNumber = Integer.parseInt(runnameValue.getText());
//            autoSetOutputFileName = autonameCB.isSelected();
//            edgeSet.edgeMode.setEdgeMode(Double.parseDouble(edgeModeValue.getText()));
//            vertexMaximum = Double.parseDouble(maxVertexValue.getText());
//            double mu = Double.parseDouble(muValue.getText());  
//            Hamiltonian.edgeSource = mu;
//            double j = Double.parseDouble(jValue.getText());  
//            Hamiltonian.vertexSource = j;
//            Hamiltonian.kappa = Double.parseDouble(kappaValue.getText());  
//            Hamiltonian.lambda = Double.parseDouble(lambdaValue.getText());  
//            Hamiltonian.distanceScale = Double.parseDouble(distScaleValue.getText());  
//            Hamiltonian.shortDistanceScale = Double.parseDouble(reldistScaleValue.getText());  
//            Hamiltonian.beta = Double.parseDouble(betaValue.getText());  
//            Hamiltonian.setb( Double.parseDouble(bValue.getText()) );  
//            double nmn = Double.parseDouble(minorModelNumberValue.getText());  
//            double jmn = Double.parseDouble(majorModelNumberValue.getText());  
//            modelNumber.set (jmn,nmn);
//            siteWeightFactor = (int) (Double.parseDouble(sSFValue.getText()) +0.5); 
//            edgeWidthFactor = (int) (Double.parseDouble(eWFValue.getText()) +0.5); 
//            zeroColourFrac = Double.parseDouble(zCFValue.getText()); 
//            minColourFrac = Double.parseDouble(mCFValue.getText()) ; 
//            DisplayMaxVertexScale = Double.parseDouble(DMVSValue.getText()) ; 
//            DisplayMaxEdgeScale = Double.parseDouble(DMESValue.getText()); 
//            influenceProb= Double.parseDouble(ipValue.getText()); 
//            cultureTimeScale= Double.parseDouble(ctsValue.getText()); 
//            
//            //newNetworkDisplaySyle = NewNetworkStyleCB.isSelected();
//                    
//            setOutputFileName();
//            if (command.equals("CALCULATE")) {
//                
//                Date date = new Date();
//                if (message.getInformationLevel()>-1) System.out.println(date);
//                if (message.getInformationLevel()>0) showFixedSiteVariables("#",3);
//                if (message.getInformationLevel()>0) showDistanceValues("#",3);
//                //message.println(2,"Data reading OK, number of sites is "+numberSites);
//                    
//                    switch (updateMode){
//                        case 0: doPPA(); break;
//                        case 1: doMC(); break;
//                        case 2: doDP(); break;
//                        case 3: doVP(); break;
//                    }
//            }
//            showNetwork("#", 3);
//            
//        } catch (Exception e) 
//        {
//         res=10;
//         resMessage = "Input Window actionPerformed error: "+e;  
//         message.println(-2,resMessage);
//         infoErrorBox(-2,inputFrame, resMessage,"Input Window Error");         
//         
//        }
//        
//    } // eo public void actionPerformed(ActionEvent event)
//
//   
//
//}// eo public class InputWindow implements ActionListener 





        
        



// ********************************************************************** 
//        IslandNetworkWindow was here, is now InputDataFrame
// **********************************************************************                
//        /**
//         * Main Window for GUI running of islandNetwork.

    
///** Run InfileWindow as GUI.
// Now in ariadne.java        
//public void runGUI(String commandLineArgs[])
    


/* *******************************************************************************
    File and directory routines  
*/

// ----------------------------------------------------------------------

/**
 *  test directory
 */
    public String testDirectory(String dirname) 
    {
        String mess="";
        File dir = new File(dirname);
        if (!dir.isDirectory())
        { 
         mess = dirname+" not a directory";
         message.printERROR(mess);
         }
        else 
        {
            mess="Looking at directory "+dirname;
            message.println(0,mess);
        }        
        return (mess);
    }


/**
 *  Filter to find only one type of file, set up filelist (names without extension)
 *  See Schildt p544
 */
    public String getFileList(String ext, String dirname, String [] filenamelist ) 
    {
            // next part Schildt p544
        String mess ="";
        File dir = new File(dirname);
        if (!dir.isDirectory())
        { 
         mess = dirname+" not a directory";
         message.printERROR(mess);
         return (mess);
        }
        message.println(0,"Looking at directory "+dirname);
        FilenameFilter only = new OnlyExtensionSet(ext);
        String [] filelist = dir.list(only);
        mess = "Found  "+filelist.length+" files with extension "+ext+" in directory "+dirname;
        message.println(0,mess);

        filelist = new String[filelist.length];
        for (int i =0; i<filelist.length; i++)
        {   filenamelist[i] =  getFileNameRoot(filelist[i], ext);
            message.println(0,filelist[i]+SEPSTRING+filenamelist[i]);
            
        }
        return (mess);
    }//eo getFileList

    /**
 *  Method of DistributionAnalysis
 *  Filter to find only one type of file
 *  See Schildt p544
 */
    public String getFileNameRoot(String filename, String ext){
        int i = filename.lastIndexOf(ext);
        if (i<0) return null;
        return filename.substring(0,i);
              
    }
    
 /**
 *  Method of DistributionAnalysis
 *  Filter to find only one type of file
 *  See Schildt p544
 */
    public class OnlyExtensionSet implements FilenameFilter{
          String ext;
          
          // constructor;
          public OnlyExtensionSet (String ext){
           this.ext=ext;
           }

          public boolean accept(File dir, String name){
           return ( (name.endsWith(ext) ));
          }

    } // eo OnlyExtensionSet
   
/* *******************************************************************************    
   Routines for Hamiltonians of different models
*/       
    
    /**
     * Sets energy to be current value of Hamiltonian.
     */
    public void calcEnergy()
    {
           energy=0;
           double supply = 1;
           double demand = 1;
           double sourceWeight =-1;
       switch (modelNumber.major) 
        { 
           case 1:   // Model 1
               for (int s = 0; s < numberSites; s++) {
                   sourceWeight = siteArray[s].getWeight();
                   supply = (modelNumber.bit0 ? (sourceWeight) : 1);
                   energy += Hamiltonian.vertexSource * sourceWeight;
                   energy -= Hamiltonian.vertexPotential1(siteArray[s].size, siteArray[s].value);
                   for (int t = 0; t < numberSites; t++) {
                       demand = (modelNumber.bit1 ? siteArray[t].getWeight() : 1);
                       energy -= edgeSet.getEdgePotential1(s, t) * edgeSet.getEdgeValue(s, t) *demand*supply; // comes in with negative sign
                       energy += Hamiltonian.edgeSource * sourceWeight * edgeSet.getEdgeValue(s, t);
                   } // eo for t           
                } // eo for s
                
                break;
               
                
                        
       } // eo switch
       
    }
    
    /**
     * Gives change in Hamiltonian if one edge value is changed
     * @param i number of source (from) site
     * @param j number of target (to) site
     * @param newValue new value for edge from i to j
     * 
     */
    public double deltaEdgeHamiltonian(int i, int j, double newValue) 
    {
        double dH=0;
        double de=0;
        switch (modelNumber.major) 
        { 
            case 5:   // Model 5
            {
                double oldedge = edgeSet.getEdgeValue(i,j);
                de = newValue-oldedge;
                dH = Hamiltonian.edgeSource*siteArray[i].size*de ;
                double outstrengthi = edgeSet.getOutEdgeStrength(i);
                double sizei = siteArray[i].size;
                double valuei = siteArray[i].value;
                dH +=  Hamiltonian.vertexPotential5(sizei, valuei+outstrengthi) 
                     - Hamiltonian.vertexPotential5(sizei , valuei+outstrengthi+de) ;        
                double demandj = ( modelNumber.bit1 ? siteArray[j].size*(siteArray[j].value+ edgeSet.getOutEdgeStrength(j)) : 1 );
                double dp = edgeSet.getEdgePotential1(i,j);
                double oldsupply =  ( modelNumber.bit0 ? sizei * (valuei +outstrengthi ) : 1 );
                double newsupply =  ( modelNumber.bit0 ? sizei * (valuei +outstrengthi+de) : 1 );
                dH +=   oldsupply * dp * oldedge  * demandj ; // old i->j trade value
                dH += - newsupply * dp * newValue * demandj ; // new i->j trade value
                if (modelNumber.bit0 ) for (int k=0; k<numberSites; k++) 
                    if ((k!=i)&& (k!=j))
                {
                    dH+= - sizei * de   * ( modelNumber.bit1 ? siteArray[k].size*(siteArray[k].value+ edgeSet.getOutEdgeStrength(k)) : 1 )     
                          * edgeSet.getEdgePotential1(i,k) * edgeSet.getEdgeValue(i,k) ;
                }
                if (modelNumber.bit1 ) for (int k=0; k<numberSites; k++) 
                    if (k!=i)
                {
                    dH+= - sizei * de   * ( modelNumber.bit0 ? siteArray[k].size*(siteArray[k].value+ edgeSet.getOutEdgeStrength(k)) : 1 )     
                          * edgeSet.getEdgePotential1(k,i) * edgeSet.getEdgeValue(k,i) ;
                }
                
                break;
            }//eo model 5

         case 4:  // Model 4; uses Hamiltonian.alpha to set critical value of degree
         {
           de=(newValue-edgeSet.getEdgeValue(i,j));
           dH = Hamiltonian.edgeSource*siteArray[i].getWeight() *de ;
           dH+= Hamiltonian.lambda*de*edgeSet.getEdgeDistance(i,j);
           double ki = edgeSet.getOutEdgeStrength(i);
           double kinew = ki + de;
//           System.out.println("Hamiltonian.alpha, ki, kinew = "+Hamiltonian.alpha+" , "+ki+" , "+kinew);
           if (ki<=Hamiltonian.alpha) dH-=(1+Hamiltonian.alpha-ki)*MAXH;
           if (kinew<=Hamiltonian.alpha) dH+=(1+Hamiltonian.alpha-kinew)*MAXH;
           break;
           }//eo model 4
         case 3: 
         {
             dH= Hamiltonian.edgeSource
                     * ( modelNumber.bit0 ? siteArray[i].getWeight() : 1 )
                     * ( modelNumber.bit1 ? siteArray[j].getWeight() : 1 )
                     * (newValue-edgeSet.getEdgeValue(i,j)) ;
             dH+= edgePotentialTotal3(); 
             double oldValue = edgeSet.getEdgeValue(i,j);
             edgeSet.setEdgeValue(i,j,newValue);
             dH-= edgePotentialTotal3(); 
             edgeSet.setEdgeValue(i,j,oldValue);
             
             break;
         }    
           case 2: 
         { 
           double ci=calcConsumption(i);
           double deltaci = ((newValue-edgeSet.getEdgeValue(i,j))*siteArray[i].value* Hamiltonian.consumptioncoeff);
           double cj=calcConsumption(j);
           double oi = calcOutput(siteArray[i].value,siteArray[i].size);
           double oj = calcOutput(siteArray[j].value,siteArray[j].size);
           double dvj = siteArray[j].getWeight()*(oj-cj);
           
           dH = -(oi-ci) * dvj * 
                    edgeSet.getEdgePotential1(i,j)* siteArray[i].value*edgeSet.getEdgeValue(i,j);
           dH+=  (oi- (ci+ deltaci ) ) * dvj 
                  * edgeSet.getEdgePotential1(i,j)* siteArray[i].value*newValue;
           
           dH+=   Hamiltonian.kappa*deltaci + Hamiltonian.edgeSource*siteArray[i].getWeight() *(newValue-edgeSet.getEdgeValue(i,j));
           break;
         }   
         
            case 1:   // Model 1          
         { 
           dH= ( ( modelNumber.bit0 ? (siteArray[i].getWeight()) : 1 )
                 * ( modelNumber.bit1 ? siteArray[j].getWeight() : 1 )
                 * edgeSet.getEdgePotential1(i,j) * ( - newValue  + edgeSet.getEdgeValue(i,j) )
                + Hamiltonian.edgeSource*siteArray[i].getWeight()*(newValue-edgeSet.getEdgeValue(i,j))) ;
           break;
         }//eo model 1 
            default: break;
        }//eo switch
        return (dH);
    }

    
    /**
     * Gives the potential between two site variables.
     * @param vertexValue variable value of vertex 
     * @param siteSize fixed site size 
     * @return the output of vertex 
     * 
     */
    public double calcOutput(double vertexValue, double siteSize) 
    {  return( (1+Hamiltonian.outputcoeff) * siteSize*
               (1- Hamiltonian.outputcoeff/(Hamiltonian.outputcoeff+vertexValue)) );
    };
    
    /**
     * Calculates the consumption of site i.
     * @param i the vertex number
     * @return the consumption of vertex i
     * 
     */
    public double calcConsumption(int i) 
    {  
        double w=0;
        for (int j=0; j<numberSites; j++) w+=edgeSet.getEdgeValue(i,j); 
        return( (siteArray[i].value+w)*siteArray[i].size* Hamiltonian.consumptioncoeff ) ;
    };
    
    

    /**
     * Gives the total edge potential term for model 3.
     * @return total edge potential term for model 3.
     */
    public double edgePotentialTotal3() 
    {
        double c;
        double potl=0.0;
        edgeSet.doDijkstra(siteArray);
        for (int i=0; i<numberSites; i++)
        {
            c=( modelNumber.bit0 ? siteArray[i].getWeight() : 1 );
            for (int j=0; j<numberSites; j++)
            {
                if ((i==j) || (edgeSet.getEdgeSeparation(i,j)==0) || (edgeSet.getEdgeValue(i,j)==0)) continue;
                potl+= c* ( modelNumber.bit2 ? edgeSet.getEdgeSeparation(i,j) : edgeSet. getEdgePotentialSeparation1(i,j,Hamiltonian) )
                * ( modelNumber.bit1 ? siteArray[j].getWeight() : 1 );
            }
        }
        // distance[j][i] was used to put common factor out of loop.        
    return( potl );
    }

// --------------------------------------------------------------------
    /**
     * Gives change in Hamiltonian if one vertex value is changed.
     * <p>Assumes Dijkstra values distance[][] already set.
     * @param i the index of the site to be changed.
     * @param newValue the new value for the vertex i.
     * @return change in Hamiltonian if tried new vertex value.
     */
 
     public double deltaVertexHamiltonian(int i, double newValue) 
    {   
//        double newbValue = (bEqualsOne ? newValue : Math.pow(newValue,Hamiltonian.b));
        double dH=0;
        switch (modelNumber.major) 
        { 
         case 4: 
         {
           double dv = (newValue-siteArray[i].value);
        // pure vertex terms as model 1
            dH= dv*siteArray[i].size * Hamiltonian.vertexSource  
                   - Hamiltonian.vertexPotential1(siteArray[i].size,newValue)
                   + Hamiltonian.vertexPotential1(siteArray[i].size,siteArray[i].value);
            // ??? NO edge terms???
          }//eo model 4

            case 2: 
         {
           double ci=calcConsumption(i);
           double deltaci=(newValue-siteArray[i].value)*siteArray[i].size* Hamiltonian.consumptioncoeff;
           double oi = calcOutput(siteArray[i].value,siteArray[i].size);
           double deltaoi = calcOutput(newValue,siteArray[i].size)-oi;
           dH = Hamiltonian.kappa*(deltaci-deltaoi); 
           dH+= (newValue-siteArray[i].value) *siteArray[i].size * Hamiltonian.vertexSource  ;
           // edge/vertex terms now
           for (int j=0; j<numberSites; j++)
           {                 
             if (i==j) continue;
             dH+= (deltaoi-deltaci)
                  *(calcOutput(siteArray[j].value,siteArray[j].size)-calcConsumption(j))
                  *edgeSet.getEdgePotential1(i,j)
                  *( siteArray[i].value *edgeSet.getEdgeValue(i,j)  + siteArray[j].value *edgeSet.getEdgeValue(j,i) );
            } 
           break;
         }// eo case 2   
            case 5: // Model 5
         {
           double currentSiteValue = siteArray[i].value;
           double currentSiteSize = siteArray[i].size;
           double dv = (newValue-currentSiteValue);
           double dw = dv*currentSiteSize ;
           double currentOutEdgeStrength= edgeSet.getOutEdgeStrength(i);
           // pure vertex terms first
            dH  =   Hamiltonian.vertexSource *dw ;
//            dH += - Hamiltonian.vertexPotential5(currentSiteSize, newValue ) 
//                  + Hamiltonian.vertexPotential5(currentSiteSize , currentSiteValue) ;        
            dH += - Hamiltonian.vertexPotential5(currentSiteSize, newValue+currentOutEdgeStrength) 
                  + Hamiltonian.vertexPotential5(currentSiteSize , currentSiteValue+currentOutEdgeStrength) ;        
            
        // edge/vertex terms now
            for (int j=0; j<numberSites; j++) if (i !=j)  
            {
               // change is site i as the source (from) site, j as the target (to) site
               if ( modelNumber.bit0  ) dH+= 
                            -   dw
                              * edgeSet.getEdgePotential1(i,j)
                              * currentSiteSize * edgeSet.getEdgeValue(i,j)
                              * ( modelNumber.bit1 ? siteArray[j].size*(siteArray[j].value+edgeSet.getOutEdgeStrength(j)) : 1 );
               // now site i is target (to) site and j is source (from) site
                if ( modelNumber.bit1  ) dH+= 
                            - ( modelNumber.bit0 ? siteArray[j].size*(siteArray[j].value+edgeSet.getOutEdgeStrength(j)) : 1 )
                            * edgeSet.getEdgePotential1(j,i)
                            * siteArray[j].size * edgeSet.getEdgeValue(j,i)                
                            * dw ;
            }// eo for j if i!=j 
            break;
          }//eo model 5
            case 3:  // Model 3 must have dijkstra set
            case 1:  // Models 1 and 3
         {
           double dv = (newValue-siteArray[i].value);
           double dw = dv*siteArray[i].size;
        // pure vertex terms first
            dH= Hamiltonian.vertexSource *dw ;
            dH+= - Hamiltonian.vertexPotential1(siteArray[i].size, newValue)
                      + Hamiltonian.vertexPotential1(siteArray[i].size, siteArray[i].value);
        // edge/vertex terms now
            for (int j=0; j<numberSites; j++) if (i !=j)  
            {
                dH+= Hamiltonian.edgeSource * dw* edgeSet.getEdgeValue(i,j);
               // change is site i as the source (from) site, j as the target (to) site
               if ( modelNumber.bit0  ) dH+= 
                            - dw * ( modelNumber.bit1 ? siteArray[j].getWeight() : 1 )
                            * edgeSet.getEdgePotential1(i,j)* edgeSet.getEdgeValue(i,j) ;
               // now site i is target (to) site and j is source (from) site
                if ( modelNumber.bit1  ) dH+= 
                            - dw * ( modelNumber.bit0 ? siteArray[j].getWeight() : 1 )
                            * edgeSet.getEdgePotential1(j,i)  * edgeSet.getEdgeValue(j,i)   ;                
            }// eo for j if i!=j 
            break;
          }//eo models 1 and 3.
            
            default:  // other model numbers no action
         }//eo switch             
         return  ( dH  ) ;
    }
    
     
     
     // --------------------------------------------------------------------
    /**
     * Gives change in Hamiltonian if two vertex weights are updated, conserving total weight.
     * <p>For model 3 this assumes Dijkstra values distance[][] already set.
     * @param i the index of the site to be decreased in weight.
     * @param i2 the index of the site to be increased in weight.
     * @param changeWeight a positive double, the amount Weight (size*value) of site i is to be decreased.
     * @return change in Hamiltonian if tried new vertex value. If >1e20 then update is illegal.
     */
 
     public double deltaVertexHamiltonian(int i, int i2, double changeWeight) 
    {   
        double dH=0;
        
        switch (modelNumber.major) 
        { 
            case 3:  // Model 3 must have dijkstra set
            case 1:  // Models 1 and 3
         {
           double newValue = siteArray[i].value-changeWeight/siteArray[i].size;
           if (!vertexMode.testValue(newValue)) return +99e20; // make sure this is never done
           
           double newValue2 = siteArray[i2].value+changeWeight/siteArray[i2].size;
           if (!vertexMode.testValue(newValue2)) return +88e20; // make sure this is never done
           
           
        // pure vertex terms first
            dH += - Hamiltonian.vertexPotential1(siteArray[i].size, newValue)
                      + Hamiltonian.vertexPotential1(siteArray[i].size, siteArray[i].value);
            dH += - Hamiltonian.vertexPotential1(siteArray[i2].size, newValue2)
                      + Hamiltonian.vertexPotential1(siteArray[i2].size, siteArray[i2].value);
        // edge/vertex terms now
            dH -= Hamiltonian.edgeSource * changeWeight * edgeSet.getOutEdgeStrength(i);
            dH += Hamiltonian.edgeSource * changeWeight * edgeSet.getOutEdgeStrength(i2);
                              
            // Note in the following the case where i2==j calculates the i to i2 case 
            // while the i==j gives the i2 to i case which have quadratic changeWeight contributions.  
            // The remaining cases have up to four linear terms for the links i to/from j and i2 to/from j
             for (int j = 0; j < numberSites; j++) {
                 if (i2 == j) { 
                     dH += +(modelNumber.bit0 ? siteArray[i].getWeight() : 1) * edgeSet.getEdgePotential1(i, i2) * edgeSet.getEdgeValue(i, i2) * (modelNumber.bit1 ? siteArray[i2].getWeight() : 1) 
                           -(modelNumber.bit0 ? siteArray[i].getWeight()-changeWeight : 1) * edgeSet.getEdgePotential1(i, i2) * edgeSet.getEdgeValue(i, i2) * (modelNumber.bit1 ? siteArray[i2].getWeight() +changeWeight : 1); 
                     }
                 else if (i ==j) {
                     dH += +(modelNumber.bit0 ? siteArray[i2].getWeight() : 1) * edgeSet.getEdgePotential1(i2, i) * edgeSet.getEdgeValue(i2, i) * (modelNumber.bit1 ? siteArray[i].getWeight() : 1) 
                           -(modelNumber.bit0 ? siteArray[i2].getWeight()+changeWeight : 1) * edgeSet.getEdgePotential1(i2, i) * edgeSet.getEdgeValue(i2, i) * (modelNumber.bit1 ? siteArray[i].getWeight() - changeWeight : 1); 
                     }
                 else {
                     // change is site i as the source (from) site, j as the target (to) site
                     if (modelNumber.bit0) 
                         dH +=  changeWeight * edgeSet.getEdgePotential1(i, j) * edgeSet.getEdgeValue(i, j) * (modelNumber.bit1 ? siteArray[j].getWeight() : 1) ;
                     // now site i is target (to) site and j is source (from) site
                     if (modelNumber.bit1) 
                         dH +=  (modelNumber.bit0 ? siteArray[j].getWeight() : 1) * edgeSet.getEdgePotential1(j, i) * edgeSet.getEdgeValue(j, i) * changeWeight ;
                 
                     // change is site i2 as the source (from) site, j as the target (to) site
                     if (modelNumber.bit0) 
                         dH -=  changeWeight * edgeSet.getEdgePotential1(i2, j) * edgeSet.getEdgeValue(i2, j) * (modelNumber.bit1 ? siteArray[j].getWeight() : 1) ;
                     // now site i is target (to) site and j is source (from) site
                     if (modelNumber.bit1) 
                         dH -= (modelNumber.bit0 ? siteArray[j].getWeight() : 1) * edgeSet.getEdgePotential1(j, i2) * edgeSet.getEdgeValue(j, i2) * changeWeight ;
                 }// eo if i2!=j 
             }// eo for j
                
                break;
          }//eo models 1 and 3.
            
            default:  // other model numbers no action
         }//eo switch             
         return  ( dH  ) ;
    }
 
  
    
// **********************************************************************
// MC routines    
    /**
     * Sweeps through all edges updating acording to Metropolis.
     * 
     */
    public void edgeSweep() {
        
        double dH=-98765432.1;
        double newEdgeValue=0;
        double otherOutStrength=-1.0; 
//        double newOutStrength = -1.0;
//        double edgeMax =edgeSet.edgeMode.maximumValue; //1.0;
        int i=-1;
        int j=-1;
        int updateTried=0;
        int updateMade=0;
        Permutation sourcePerm = new Permutation(numberSites);
        Permutation targetPerm;
        final double strErrLimit = edgeSet.edgeMode.maximumValue*1.01;
        //if (edgeSet.edgeMode.maxValueModeOn) edgeMax = edgeSet.edgeMode.maximumValue;
        for (int ip=0; ip<numberSites; ip++){
            i = sourcePerm.next();
            targetPerm = new Permutation(numberSites);                
//            double edgeMax =edgeSet.edgeMode.maximumValue; //1.0;

//            if (edgeSet.edgeMode.outStrengthLimitOn) edgeMax = edgeSet.edgeMode.maximumValue - edgeSet.getOutEdgeStrength(i); // total out edge degree < (-edgeMode)
//            if (edgeMax<0)
//                {
//                    System.out.println(edgeMax+ " = edgeMax <0 ");
//                    return; //continue;
//                }
            for (int jp=0; jp<numberSites; jp++){
                j = targetPerm.next();
                if (i==j) continue;
                updateTried++;
                // If edgeMode==0 then set edge to 0 or 1
                // If edgeMode>0 then set up upto maximum of edgeMode
                // if edgeMode<0 then set site out strength to maximum of (-edgeMode)
                
                if (edgeSet.edgeMode.outStrengthLimitOn)
                {
                    otherOutStrength = edgeSet.getOutEdgeStrength(i)-edgeSet.getEdgeValue(i, j);
                    newEdgeValue = (edgeSet.edgeMode.maximumValue-otherOutStrength)*rnd.nextDouble();
                    if ((otherOutStrength + newEdgeValue)>strErrLimit)
                    {
                    System.out.println((otherOutStrength + newEdgeValue) + " = newOutStrength >1 ");
                     return; //continue;
                    }
                }
                else if (edgeSet.edgeMode.maxValueModeOn) newEdgeValue = rnd.nextDouble()*edgeSet.edgeMode.maximumValue;
                     else newEdgeValue = ((rnd.nextBoolean())? 1.0: 0.0); 
                
                // these are checks, may be unnecessary
                if (newEdgeValue<0)
                {
                    System.out.println(newEdgeValue+ " = newEdgeValue <0 ");
                     return; //continue;
                }
                

               
                
                dH = deltaEdgeHamiltonian(i, j, newEdgeValue);
//                System.out.println(dH+"  "+Hamiltonian.beta+"  "+dH);
                if ((dH<0) || ( rnd.nextDouble() < Math.exp(-Hamiltonian.beta*dH) ) ) 
                {
                    edgeSet.setEdgeValue(i,j,newEdgeValue);
                    updateMade++;
                    
                }
            }//eo j
        
        }// eo i
       edgeUR.update(updateTried,updateMade);           
    }
        
// .....................................................................
    
    /**
     * Sweeps through all vertices updating acording to Metropolis.
     * 
     */
    public void vertexSweep() 
    {    
        int updateTried=0;
        int updateMade=0;
        Permutation sitePerm = new Permutation(numberSites);
        int ip=0;
        if (vertexMode.maxValueModeOn) {
            for (ip=0; ip<numberSites; ip++) if ( vertexUpdate(sitePerm.next())) updateMade++;
        }
        if (vertexMode.constantWeightOn) {
            for (ip=0; ip<numberSites; ip++) if ( vertexPairUpdate(sitePerm.next())) updateMade++;
        }
        
        updateTried+=ip;
        vertexUR.update(updateTried,updateMade);
    }
        
    /**
     * Tries an update on one vertex according to Metropolis.
     * <br>Used only if maximum vertex value mode on.
     *@param i vertex to try to update
     *@return true if site update made 
     */
    public boolean vertexUpdate(int i) 
    {
        double newVertexValue=  rnd.nextDouble()*vertexMode.maximumValue; 
        double dH = deltaVertexHamiltonian(i, newVertexValue);
//                System.out.println(dH+"  "+Hamiltonian.beta+"  "+dH);
        if ((dH<0) || ( rnd.nextDouble() < Math.exp(-Hamiltonian.beta*dH) ) ) 
                {
                    siteArray[i].value = newVertexValue;
                    return true;
                }
       return false;
    }   
    
    /**
     * Tries an update on pair of vertices keeping total weight constant according to Metropolis.
     * <p>Tries to reduce the weight of given vertex by n/10 where n is an integer from 1 to 10.
     * <p>Second site is chosen at random and has its weight increased by same amount if update accepted.
     *@param i first vertex to try to update
     *@return true if site update made 
     */
    public boolean vertexPairUpdate(int i) 
    {
        int i2 = rnd.nextInt(numberSites-1);
        if (i2>=i) i2++;
        double w= siteArray[i].getWeight();
        double dw =  w/10.0;
        if (dw<0.001) dw=0.001;
        dw=dw*(1+rnd.nextInt(10));
        if (dw>w) dw=w;
        
        double dH = deltaVertexHamiltonian(i, i2, dw);
//                System.out.println(dH+"  "+Hamiltonian.beta+"  "+dH);
        if (dH>1e20) return false;
        if ((dH<0) || ( rnd.nextDouble() < Math.exp(-Hamiltonian.beta*dH) ) ) 
                {
                    siteArray[i].value = (w-dw)/siteArray[i].size;
                    siteArray[i2].value = (siteArray[i2].getWeight()+dw)/siteArray[i2].size;
                    return true;
                }
       return false;
    }   
    
// ***********************************************************************    
    
    
    /**
     * Sets edges to be equal to 1/(1+distance).
     * <br> This is the probability that you need in a Markov chain to <emph>not</emph> reach that distance
     */
    public void doDP() {
        
        edgeSet.edgeMode.setMaxValueModeOn(numberSites);
        edgeSet.setEdgePotential1(Hamiltonian);
        double ev=-1.0;
        double [] str = new double[numberSites];
        double maxstr=-9.87654321;
        for (int i=0; i<numberSites; i++)
        {
            str[i]=0;
            for (int j=0; j<numberSites; j++)
            {
                if (i==j) ev=0;
                else ev=1.0/(1+edgeSet.getEdgeDistance(i,j)/Hamiltonian.distanceScale );
                str[i]+=ev;
                edgeSet.setEdgeValue(i,j,ev);
                edgeSet.setEdgeColour(i,j, numberColours*ev);
            }
            if (maxstr<str[i]) maxstr=str[i];
        }
        
        double dnc = ((double) numberColours);
        for (int i=0; i<numberSites; i++) {
            siteArray[i].value=1; //str[i]/maxstr;
            siteArray[i].size=1; //maxstr;
            siteArray[i].displaySize = Math.round(str[i]);
            siteArray[i].setWeight();
        }  
        calcNetworkStats();
        if (message.getInformationLevel()>=1) printNetworkStatistics("#",3);
        //FileOutputNetworkStatistics("#", 3);
              
    }// eo doPotential
        
    /**
     * Sets edges to be equal to V(distance/distanceScale
     * <br> This is the probability that you need in a Markov chain to <emph>not</emph> reach that distance
     */
    public void doVP() {
        
        edgeSet.edgeMode.setMaxValueModeOn(numberSites);
        edgeSet.setEdgePotential1(Hamiltonian);
        double ev=-1.0;
        double [] str = new double[numberSites];
        double maxstr=-9.87654321;
        for (int i=0; i<numberSites; i++)
        {
            str[i]=0;
            for (int j=0; j<numberSites; j++)
            {
                if (i==j) ev=0;
                else ev=Hamiltonian.edgeBarePotential1(edgeSet.getEdgeDistance(i,j));
                str[i]+=ev;
                edgeSet.setEdgeValue(i,j,ev);
                edgeSet.setEdgeColour(i,j, numberColours*ev);
            }
            if (maxstr<str[i]) maxstr=str[i];
        }
        
        double dnc = ((double) numberColours);
        for (int i=0; i<numberSites; i++) {
            siteArray[i].value=1; //str[i]/maxstr;
            siteArray[i].size=1; //maxstr;
            siteArray[i].displaySize = Math.round(str[i]);
            siteArray[i].setWeight();
        }  
        calcNetworkStats();
        if (message.getInformationLevel()>=1) printNetworkStatistics("#",3);
        //FileOutputNetworkStatistics("#", 3);
              
    }// eo doPotential
        
    /**
     * Does PPA analysis on network.
     * <br> See Broodbank page 180.
     * Note that always adds an integer number of edges.
     */
    public void doPPA() {
        
        int numEdgesPerSite = (int) (Hamiltonian.beta+0.5);
        if (numEdgesPerSite<0) numEdgesPerSite = 3;
        int i,j,k,l,ef,ec;
        int[] edgeOrder = new int [numberSites+1]; 
        double [][] darr = new double[numberSites][numberSites];
        if (modelNumber.major==3) edgeSet.doDijkstra(siteArray);
//        initialiseMinorModel();
        edgeSet.setEdgePotential1(Hamiltonian);
        for (i=0; i<numberSites; i++)
        {
            siteArray[i].value=1.0;
            siteArray[i].size=1.0;
            for (j=0; j<numberSites; j++)
            {
                edgeSet.setEdgeValue(i,j,1.0);
                darr[i][j]= ( (modelNumber.major==3) ? edgeSet.getEdgeSeparation(i,j) : edgeSet.getEdgeDistance(i,j));
            }
        }
        
        double dnc = ((double) numberColours);
        for (i=0; i<numberSites; i++)
        {
            ef=0;   
//          if (message.getInformationLevel()>1) System.out.println("From "+i+"-th site, "+siteArray[i].name);  
            for (j=0; j<numberSites; j++)
            {
                edgeSet.setEdgeValue(i,j,0.0);  // all edges zero by default
                if (i==j) continue;
                for (k = 0; k<ef; k++)
                {
                    if (darr[i][j]<darr[i][edgeOrder[k]]) break;
                }//eo for k 
//              if ((k==ef) && (ef < numEdges)) {edgeOrder[ef++]=j; continue;}
//                if (message.getInformationLevel()>1) System.out.println("i+j+k+ef="+i+j+k+ef);
                for (l = ef; l>k; l--){ edgeOrder[l] = edgeOrder[l-1]; }// for l
                edgeOrder[k]=j;
                ef++;
//                if (ef<numEdges) ef++;
            }//eo j
            double newvalue=0;
            double newcolour=0;
            for (k = 0; k<numEdgesPerSite; k++) 
            {
//                    System.out.println(k+"-th biggest is "+edgeOrder[k]);  
                    ec = numberColours-k;
                    newcolour =(ec>0) ? ec : 1;
                    edgeSet.setEdgeColour(i,edgeOrder[k], newcolour);
                    newvalue = (k<numberColours) ? ((double) ec)/dnc : 1.0/dnc;
                    edgeSet.setEdgeValue(i,edgeOrder[k], newvalue)  ;
            }
        
        }// eo i
        
        // Now set site colours based on in degree
        int [] inDegree = new int[numberSites];
        int maxInDegree = 0;
        int ind;
        for (i=0; i<numberSites; i++)
        {
            ind=edgeSet.getInDegree(i);
            inDegree[i]=ind;
            if (ind>maxInDegree) maxInDegree=ind;
        }
        int vc;
        for (i=0; i<numberSites; i++)
        {
            ind=inDegree[i];
            vc = (int) (0.5+ (dnc * ind) / maxInDegree);
            siteArray[i].value = ((double) ind) / maxInDegree;
            siteArray[i].displaySize = (vc>0) ? vc :0;
            
        }

        for (i=0; i<numberSites; i++) siteArray[i].setWeight();
        calcNetworkStats();
        if (message.getInformationLevel()>=0) printNetworkStatistics("#",3);
        //FileOutputNetworkStatistics("#", 3);
              
    }// eo doPPA
        

    
    /**
     * Does full Monte Carlo.
     *
     */
    public void doMC()
    {
        boolean MChistory=true;
        MonteCarloHistory mch = new MonteCarloHistory(1000);
        final int dotInterval=10;
        StatisticalQuantity enewsq;
        StatisticalQuantity elastsq;
        //MCsweeps; 
              double betamin = Hamiltonian.beta;
              double betamax = 1e20;
              double betafactor = 2;
              //double updatefrac =0.0;
              double beta;
              int betainc =-1;
              
              // initialise vertices
              if (coldStart) {
                  double v=0; 
                  if (!vertexMode.maxValueModeOn) 
                  { // find constant value such that weights would add up to fixed total
                      double St=0;
                      for (int i=0; i<numberSites; i++) St+=siteArray[i].size;
                      v=vertexMode.totalWeight/St;
                  }
                  for (int i=0; i<numberSites; i++) siteArray[i].value = v;
              }
              else
              {
                  if (vertexMode.maxValueModeOn) for (int i=0; i<numberSites; i++) siteArray[i].value = rnd.nextDouble()*vertexMode.maximumValue;
                  else {
                      for (int i=0; i<numberSites; i++) siteArray[i].value = vertexMode.totalWeight/numberSites;
                      Permutation perm = new Permutation(numberSites);
                      int s,t;
                      double dw,vt;
                      for (int i=0; i<numberSites; i++) {
                          s = perm.next();
                          t = rnd.nextInt(numberSites-1);
                          if (t>=s) t++;
                          dw = siteArray[s].getWeight()*rnd.nextDouble();
                          vt = siteArray[t].value+dw/siteArray[t].size;
                          if (vertexMode.testValue(vt))
                          {
                              siteArray[t].value=vt;
                              siteArray[s].value-=dw/siteArray[s].size;
                          }
                      }
                  }
              }
              
              
              // initialise edges
              if (coldStart) edgeSet.setEdgeValues(0);
              else edgeSet.setRandomEdgeValues(rnd);
              edgeSet.setEdgePotential1(Hamiltonian);
              
              // Other initialisations
              long initialtime = System.currentTimeMillis ();
              if (modelNumber.major ==3) edgeSet.doDijkstra(siteArray); // initialise separations distance[][]
              enewsq = new StatisticalQuantity();
              elastsq = null;
              int eqtest=0;
              for (beta = betamin; beta<=betamax; beta=beta*betafactor)
              {
                 Hamiltonian.beta=beta;
                 vertexUR.reset();
                 edgeUR.reset();
                 for (int s=0; s<MCsweeps; s++) {
                     vertexSweep(); edgeSweep();
                     calcEnergy(); enewsq.add(energy);
//                 if (MChistory) {calcEnergy();mch.add(beta,energy,vertexUR.made, edgeUR.made);}
                } //eo for s

                 if (elastsq!=null){ 
                     if (Math.abs(enewsq.getAverage()-elastsq.getAverage())<(Math.abs(enewsq.getError())+Math.abs(elastsq.getError())))
                         eqtest++;
                     else eqtest=0;                 
                 }
                 
                 //System.out.println("beta ="+beta+"  E: "+a.edgeUR.toString()+"  V: "+a.vertexUR.toString());
                 if (MChistory) {calcEnergy();mch.add(beta,energy,vertexUR.made, edgeUR.made);}
                 
                 if (((++betainc)%dotInterval) ==0) 
                 {
                    calcEnergy(); 
                    printEllapsedTime(initialtime);
                    System.out.println(" beta="+beta+", energy="+energy+",  stable for last "+eqtest+" runs. \n  E: "+edgeUR.toString()+"\n  V: "+vertexUR.toString());
                    
                    }
                 else System.out.print(".");
                 if ((vertexUR.totalfrac<0.01) && (edgeUR.totalfrac<0.01) ) break;
                 elastsq = new StatisticalQuantity(enewsq);
                 enewsq = new StatisticalQuantity();
                 
              }
              calcEnergy();
              System.out.println("\n Final beta "+beta+", energy="+energy + ",  stable for last "+eqtest+" runs. \n Edge stats: "+edgeUR.toString()+". Vertex stats: "+vertexUR.toString());
              System.out.println("****************************************************************************");
              this.setOutputFileName();
              mch.FileOutputMonteCarloHistory(this.outputFile.getFullLocationFileName("_MChistory","dat"));
             
              
              //if (message.getInformationLevel()>=0) FileOutputNetworkStatistics("#", 3);
              for (int i=0; i<numberSites; i++) siteArray[i].setWeight();
              calcNetworkStats();
              //if (message.getInformationLevel()>=0) printNetworkStatistics("#",3);
              
              for (int i=0; i<numberSites; i++)
              { if (!testEnergyVertex()) System.err.println("*** ERROR failed vertex test "+i);
                if (!testEnergyEdge()) System.err.println("*** ERROR failed Edge test "+i);
              }

              
         }//eo doMC
    
    /**
     * Tests change in energy due to vertex change.
     * @return true if OK, false if a problem
     */
    public boolean testEnergyVertex(){
        if (vertexMode.maxValueModeOn) return testEnergyVertexSingle();
        else return testEnergyVertexPair();
    }
    /**
     * Tests change in energy due to vertex change.
     * @return true if OK, false if a problem
     */
    public boolean testEnergyVertexSingle(){
        int i = rnd.nextInt(numberSites);
        double newv = rnd.nextDouble()*maxSiteValue;
        double oldv = siteArray[i].value;
        calcEnergy();
        double oldEnergy = energy;
        double dH = deltaVertexHamiltonian(i, newv);
        siteArray[i].value=newv;
        calcEnergy();
        double newEnergy = energy;
        siteArray[i].value=oldv;
        if (Math.abs(newEnergy-oldEnergy-dH)>1e-6) {
            System.err.println("*** ERROR testEnergyVertex(), complete dH="+(newEnergy-oldEnergy)+", direct dH="+dH);
            return false;
        }
        return true;
    }
 
        /**
     * Tests change in energy due to vertex changes which keep the weight constant.
     * @return true if OK, false if a problem
     */
    public boolean testEnergyVertexPair(){
        
        int i = rnd.nextInt(numberSites);
        double oldv = siteArray[i].value;
        double newv = rnd.nextDouble()*oldv;
        double dw=(oldv-newv)*siteArray[i].size;
        
        int i2 = rnd.nextInt(numberSites-1);
        if (i2>=i) i2++;
        double oldv2 = siteArray[i2].value;
        double newv2 = oldv2 +dw/siteArray[i2].size;
        
        calcEnergy();
        double oldEnergy = energy;
        
        double dH = deltaVertexHamiltonian(i, i2, dw);
        siteArray[i].value=newv;
        siteArray[i2].value=newv2;
        calcEnergy();
        double newEnergy = energy;
        siteArray[i].value=oldv;        
        siteArray[i2].value=oldv2;
        if (Math.abs(newEnergy-oldEnergy-dH)>1e-6) {
            System.err.println("*** ERROR testEnergyVertexPair(), complete dH="+(newEnergy-oldEnergy)+", direct dH="+dH);
            return false;
        }
        return true;
    }

    
    
    /**
     * Tests change in energy due to edge change.
     * @return true if OK, false if a problem
     */
    public boolean testEnergyEdge(){
        
        int s = rnd.nextInt(numberSites);
        int t = rnd.nextInt(numberSites-1);
        if (t>=s) t++;
       
        final double strErrLimit = edgeSet.edgeMode.maximumValue*1.01;
        double oldEdgeValue = edgeSet.getEdgeValue(s, t);
        double newEdgeValue = -1.0;
        
        if (edgeSet.edgeMode.outStrengthLimitOn)
                {
                    double otherOutStrength = edgeSet.getOutEdgeStrength(s)-edgeSet.getEdgeValue(s, t);
                    newEdgeValue = (edgeSet.edgeMode.maximumValue-otherOutStrength)*rnd.nextDouble();
                    if ((otherOutStrength + newEdgeValue)>strErrLimit)
                    {
                    System.out.println((otherOutStrength + newEdgeValue) + " = newOutStrength >1 ");
                     return false; //continue;
                    }
                }
                else if (edgeSet.edgeMode.maxValueModeOn) newEdgeValue = rnd.nextDouble()*edgeSet.edgeMode.maximumValue;
                     else newEdgeValue = ((rnd.nextBoolean())? 1.0: 0.0); 
                
                // these are checks, may be unnecessary
                if (newEdgeValue<0)
                {
                    System.out.println(newEdgeValue+ " = newEdgeValue <0 ");
                     return false; //continue;
                }
                
        calcEnergy();
        double oldEnergy = energy;
        double dH = deltaEdgeHamiltonian(s, t, newEdgeValue);
        edgeSet.setEdgeValue(s, t, newEdgeValue);
        calcEnergy();
        double newEnergy = energy;
        edgeSet.setEdgeValue(s, t, oldEdgeValue);
        if (Math.abs(newEnergy-oldEnergy-dH)>1e-6) {
            System.err.println("*** ERROR testEnergyEdge(), complete dH="+(newEnergy-oldEnergy)+", direct dH="+dH);
            return false;
        }
        return true;
    }
    
    
// *********************************************************


// ----------------------------------------------------------------------
    /**
     * Gives description string for the minor model type.
     * @return the string with description of minor model type.
     * 
     */
    public String getMinorModelString() 
    {  
        //int metricNumber=getMetricNumber();
        String s="unknown";
        switch (modelNumber.minor&3)
        {
            case 0: s="no site terms in trade term";  break;
            case 1: s="Supply Side (source site in trade term)";  break;
            case 2: s="Demand Side (target site in trade term)"; break;
            case 3: s="Gravity (source and target site in trade term)"; break;
            }
        if (modelNumber.major ==3) switch (modelNumber.minor&4)
        {
            case 0: s=s+"physical distance used in potential";  break;
            case 4: s=s+"edge potential used in potential";  break;
            }
        return(s) ;
    };

        
// *********************************************************
    /**
     * Gives the out degree of vertex.
     * @param i vertex number
     * @return the degree of vertex i
     * 
     */
    public int getOutDegree(int i) 
    {  
        int d=0;
        for (int j=0; j<numberSites; j++) if (edgeSet.getEdgeValue(i,j)>0) d++; 
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
     * Set up simple edge network for testing 
     */
    public void setEdgesTest1() 
    {
       for (int i=0; i<numberSites; i++){
            for (int j=0; j<numberSites; j++){                 
                if (i==j) continue;
                if (i==j+1) edgeSet.setEdgeValue(i,j,1.0);
                else edgeSet.setEdgeValue(i,j,0.0);
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
    
// **********************************************************    
/** produces a list of the rank (order) of a vector of values
     * @param valueVector is vector of double values to be ordered
     * @param orderVector is vector of integers [i] = no. of i-th value
     */
    public void calcVectorOrder(double [] valueVector, int [] orderVector)
    {
        int n = valueVector.length;
// Now calculate Ranking Order orderVector[i] = number of site ranked i        
         for (int i=0; i<n; i++) orderVector[i]=i;
         for (int i=0; i<n; i++) 
            {
                double best = valueVector[orderVector[i]];
                for (int j=i+1; j<n; j++)
                {
                 double newbest = valueVector[orderVector[j]];
                 if (best < newbest)  
                 {
                     best = newbest;
                     int temp=orderVector[j];
                     orderVector[j] = orderVector[i];
                     orderVector[i]=temp;
                 }
                }//eo for j                
            }//eo for i
    }

public void calcVectorRankOrder(double [] valueVector, int [] orderVector, int [] rankVector )
    {
        calcVectorOrder(valueVector, orderVector);
        // Now calculate rank of sites so rankVector[i] = rank of site in terms of values        
         for (int i=0; i<valueVector.length; i++) rankVector[orderVector[i]]=i;
    }
      
    
// **********************************************************    
/** Produces a list of the rank (order) of a vector of values.
     * @param valueNumber is number of entry in Site class to be ordered
     * @param orderVector is vector of integers [i] = no. of i-th value
     * @param n is number of entries to order
     */
    public void calcSiteOrder(int valueNumber, int [] orderVector, int n)
    {
// Now calculate Ranking Order orderVector[i] = number of site ranked i 
         boolean alpha = siteArray[0].isAlpha(valueNumber);
         for (int i=0; i<n; i++) orderVector[i]=i;
         for (int i=0; i<n; i++) 
            {
                int best = orderVector[i];
                for (int j=i+1; j<n; j++)
                {
                 int newbest = orderVector[j];
                 if ( ((alpha) && (siteArray[best].toString(valueNumber).compareToIgnoreCase(siteArray[newbest].toString(valueNumber))>0)  )
                     || ((!alpha) && (siteArray[best].getValue(valueNumber) < siteArray[newbest].getValue(valueNumber) )  ) )
                 {
                     best = newbest;
                     orderVector[j] = orderVector[i];
                     orderVector[i]=best;
                 }
                }//eo for j                
            }//eo for i
    }

    public void calcSiteRankOrder(int valueNumber, int [] orderVector, int rankNumber )
    {
        int n = orderVector.length;
        calcSiteOrder(valueNumber, orderVector,n);
        // Now calculate rank of sites so rankVector[i] = rank of site in terms of values        
         for (int i=0; i<n; i++) siteArray[orderVector[i]].setInt(rankNumber, i);
        n=0;
    }

    


 public int getInfomationLevel()
 {
     return message.getInformationLevel();
 }

 public void setInfomationLevel(int i)
 {
     message.setInformationLevel(i);
 }

/** Truncates a double to number of decimal points.
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public double TruncDec(double value, int dec)
    {
      double shift = Math.pow(10,dec);
      return ( ( (double) ((int) (value*shift+0.5)))/shift);
    }

    
    /** Truncates a double to number of decimal points.
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public String TruncDecString(double value, int dec)
    {
        
      double shift = Math.pow(10,dec);
      Double d =( ( (double) ((int) (value*shift+0.5)))/shift) ;
      return d.toString();
    }
    
    /** 
     * Get start of string.
     * @param s input string to be truncated
     * @param numberCharacters is number of characters to keep
     */
    public String getStartString(String s, int numberCharacters)
    {
      if (s.length()<numberCharacters) return(s);
        return ( s.substring(0, numberCharacters) );
    }
    
    /** Gives first numberCharacters of string s padded by spaces if necessary.
     * @param s string for the source of characters
     * @param numberCharacters number of characters to return
     */
    public String getPaddedStartString(String s, int numberCharacters)
    {
      String sss="";
      for (int i=0; i<numberCharacters; i++)
      {
          if (i<s.length()) sss+=s.substring(i,i+1);
          else sss+=" ";
      }
        return ( sss );
    }

    /** 
     * Calculates Euclidean distance.
     * @param x1 first point's x coordinate       
     * @param y1 first point's y coordinate       
     * @param x2 second point's x coordinate       
     * @param y2 second point's y coordinate       
     *@return distance between point one and two
     */
    public double euclideanDistance(double x1, double y1, double x2, double y2) 
    {
        return (Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
    }

    //Hexa Software Development Center © All Rights Reserved 2004            
/** This routine calculates the distance in km between two points given the latitude/longitude of those points.
 * <br> South latitudes are negative, east longitudes are positive
 *@param lat1 Latitude  of point 1 
 *@param lon1 Longitude of point 1 
 *@param lat2 Latitude  of point 2 
 *@param lon2 Longitude of point 2 
*/
private double sphericalDistance(double lat1, double lon1, double lat2, double lon2) {
  double theta = lon1 - lon2;
  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
  dist = Math.acos(dist);
  dist = rad2deg(dist);
  dist = dist * 60 * 1.1515;
  dist = dist * 1.609344;
  return (dist);
}

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts decimal degrees to radians             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
private double deg2rad(double deg) {
  return (deg * Math.PI / 180.0);
}

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts radians to decimal degrees             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
private double rad2deg(double rad) {
  return (rad * 180.0 / Math.PI);
}




   /**
     * @param initialtime set using long initialtime = System.currentTimeMillis ();
     */
    public static void printEllapsedTime(long initialtime)
  {
       long finaltime= System.currentTimeMillis ();
       long time = finaltime - initialtime;
       long ms = time %1000;
       time = time /1000; // time now in sec
       long sec = time % 60;
       time = time /60;  // time now in min
       long minutes = time % 60;
       time = time /60;  // time now in hours
       long hours = time % 24;
       long days = time /24;  
       if (days>0) System.out.print(days+" day");
       if (days>1) System.out.print("s ");
       if (hours>0) System.out.print(hours+" hour");
       if (hours>1) System.out.print("s ");
       if (minutes>0) System.out.print(minutes+" minute");
       if (minutes>1) System.out.print("s ");
       System.out.print(sec+" sec");
       if (sec>1) System.out.print("s ");
       System.out.println();  
  }


}// eo islandNetwork class
