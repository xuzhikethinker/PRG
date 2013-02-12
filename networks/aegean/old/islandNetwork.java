/*
 * islandNetwork.java
 *
 * Created on 01 April 2004, 14:14
 * Requires TextReader.java - see David Eck, javanotes4.pdf, ch10, p402.
 * @author  time
 */

package IslandNetworks;
    
import java.io.*;
import java.lang.*;
import java.lang.Math.*;
import java.util.Date;
import java.util.Random;
import javax.swing.*;          
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;

import JavaNotes.TextReader;

import IslandNetworks.NetworkGenerator;
import IslandNetworks.StatisticalQuantity;

    
    
/**
 * @author Tim Evans
 * <tt>islandNetwork</tt> is a class used to represent an island network.  
 * The files, input and output, have names built from the root of 
 * <tt>inputnameroot</tt> and <tt>outputnameroot</tt>.
 * <tt>numberSites</tt> is the number of sites.
 * Each site is a vertex and is associated with an array <tt>siteArray[]</tt> of
 * class <tt>Site</tt>.  Carries a site size which is a fixed characteristic size for the site.  
 * The vertex also has a variable value <tt>siteArray[i].value</tt>
 * which should have universal values between 0 and <tt>vertexMaximum</tt> typically.
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
 *       <tt>double shortDistScaleH</tt>
 *
 * There are also records of the updates
 * <tt>updateRecord edgeUR</tt> and <tt>updateRecord vertexUR</tt>
 * Finally there is a variable controling information output <tt>int infolevel</tt>
 * and a variable controling the update mode <tt>int updateMode</tt>
*/
public class islandNetwork {    
      
       
        //Specify the look and feel to use.  Valid values:
        //null (use the default), "Metal", "System", "Motif", "GTK+"
        final static String LOOKANDFEEL = "System";
        
        final static String iNVERSION = "iN060621";
 
        final static int MAXSITENUMBER = 100; // maxmimum number of sites
        
        String SepString = "\t"; // Separation Character for column indication
             
        boolean inputGUI = true;
        boolean dataread = false;
    
        String inputFileExtension="_input.dat";
        String parameterInputFileExtension="_parameterinput.dat";
        String inputnameroot;
        String outputnameroot;
        String runname;
        boolean autoSetOutputFileName;
        double jiggleScale = 0.0; // sets jiggleScale
        int numberSites = MAXSITENUMBER;
        double[][] distValue = new double [numberSites][numberSites]; // fixed characteristic distances
        double[][] penalty = new double [numberSites][numberSites];   // distance penalties  
        double[][] edgeValue = new double [numberSites][numberSites];  // variable size
        
        Site [] siteArray = new Site[numberSites];
//        double [] siteValue = new double [numberSites]; // fixed characteristic size
//        double [] vertexValue = new double [numberSites]; // variable size
//        String [] siteName = new String [numberSites];
        int [] siteAlphabeticalOrder = new int[numberSites];  // Sites in order of names [i] = no. of i-th site
//        double [] siteX = new double [numberSites];
//        double [] siteY = new double [numberSites];
//        double [] siteZ = new double [numberSites];
        double minX=0;
        double maxX=0;
        double minY=0;
        double maxY=0;
//        double windowScale;
        
        double[][] edgeColour = new double [numberSites][numberSites];  // variable size
//        int [] siteDisplaySize = new int [numberSites]; // variable size 
        // Now Hamiltonian coefficients
        double edgeSourceH;
        double vertexSourceH;
        double alphaH ;
        double betaH ;
        double gammaH ;
        double kappaH ;
        double lambdaH ;
        double distScaleH ;
        double shortDistScaleH;
        double bH; // power used for site variables
//        boolean bEqualsOneH; // true if bH == 1
        double outputcoeffH;
        double consumptioncoeffH;
        
        // Network Statistics
        int metricNumber=0; // set type of distance measurements to use
//        double allmaxSiteWeight=-1.0;
        double allmaxedgeweight=-1.0;
        int allmaxedgeweightindex = -1;
        double maxSiteWeight =-1;
        int maxSiteWeightIndex = -1;
//        int maxvedgeweightindex = -1; 
        double maxOutSiteStrength = -1;
        int maxOutSiteStrengthIndex = -1;
        
        StatisticalQuantity siteWeightStats;
        StatisticalQuantity edgeValueStats;
        StatisticalQuantity edgeWeightStats;
        StatisticalQuantity siteStrengthStats;

        // properties of the network
        double totVertexWeight=-1;
        double totEdgeWeight=-1;
        double totVertexValue=-1;
        double totEdgeValue=-1;

        double[] inSiteStrengthWeight;
        double[] outSiteStrengthWeight;
        double[] inSiteStrengthValue;
        double[] outSiteStrengthValue;
//        double [] siteWeight;  // S_i * v_i calculated as a statistic
        int [] siteWeightOrder;  // points in order [i] = no. of i-throws biggest Size
//        int [] siteWeightRank;  //  Rank of site [i] by weight
        

        double [] maxEdgeWeight = new double [numberSites]; 
//        double [] outsitestrength = new double [numberSites]; 
        int [] maxEdgeWeightIndex = new int [numberSites]; 
        // used for Dijkstra distances unlike the distValue[][]
        double [] [] distance  = new double [numberSites][numberSites]; // Input distances
//        int DijkstraVertex = -1;
        double DijkstraMaxDist;
        StatisticalQuantity siteDistanceStats;

        SiteRanking siteRank;  
        double influenceProb=0.5;

        // update records
        updateRecord edgeUR = new updateRecord() ;
        updateRecord vertexUR= new updateRecord() ;
        // General parameters
        int infolevel=-1;
        int updateMode;
        int outputMode=255;
        boolean edgeModeBinary;
        double edgeMode;
        double vertexMaximum;
//        double modelNumber;
        int majorModelNumber;
        int minorModelNumber;
        boolean mMN0; // use these to record bits of the minorModelNumbers
        boolean mMN1;
        boolean mMN2;

        // Display factors
        double zeroColourFrac=0.05;
        double minColourFrac=0.1;
        int siteWeightFactor = 20;
        int edgeWidthFactor = 10;
        double DisplayMaxVertexScale=0.0; //<=0 then display vertices relative to largest
                                        // else display vertices relative to this size       
        double DisplayMaxEdgeScale=0.0;   //<=0 then display edges relative to largest
                                        // else display edges relative to this size       
        int DisplayVertexType=0; // 0=size, 1 = rank;
        int siteWindowMode=3; // 0=numerical, 1=size, 2=Rank, 3=alphabetical 
        int maxNumberColours=100;
        int numberColours=100;
        Color [] javaColour = new Color[maxNumberColours];
        String[] pajekColour = new String[maxNumberColours];
        String[] pajekGrey = new String[maxNumberColours];
        Random rnd;
        double MAXDISTANCE = 9999999.9;
        double MAXH = 1e6;
        
        // Generate Network paramters
        int generateNumber=-1; // if <0 then does not generate a network, reads in from a file
        int generateType =0; // see NetworkGenerator for info on this



    
    /** Creates a new instance of network */
    public islandNetwork() {
           
            inputGUI = true;
            infolevel = -1;
             

            for (int i =0; i<numberSites; i++) {
                siteArray[i]= new Site(i);
                siteArray[i].size=-88.0;                
                siteArray[i].value=-89.0;
                for (int j =0; j<numberSites; j++) {
                    distValue[i][j]=-100.0; edgeValue[i][j]=-100.0;
//                    if (i==j) {distValue[i][j]=-100.0; edgeValue[i][j]=-100.0;}
//                    else { distValue[i][j]=-100.0; edgeValue[i][j]=-100.0;};
                };
            };
            setColours();
            if (infolevel>2)
            {
             for (int i =0; i<=numberColours; i++)
              System.out.println(i+":  "+pajekColour[i]+" , "+pajekGrey[i]);
            };
            inputnameroot="testsimple";
            outputnameroot="AUTO";
            runname="r0";
            autoSetOutputFileName = true;
        
        // Now Hamiltonian coefficients
             edgeSourceH = 0.5; // mu 
             vertexSourceH = 0.0; // j
             betaH = 1.0;  // doubles as the number of edges per site in PPA mode
             alphaH = 4.0;
             gammaH = 1.0;
             kappaH = 1.0 ;
             lambdaH = 1.0 ;
             distScaleH = 100;
             shortDistScaleH = distScaleH/20.0;
             bH = 0.95;
             outputcoeffH=1.0;
             consumptioncoeffH=2.0*outputcoeffH;   
             
           // update records zeroed on construction
             // general parameters
             majorModelNumber = 1;
             minorModelNumber = 3;
             updateMode = 1; // MC Sweep
             edgeModeBinary = false;
             edgeMode =1.0;
             vertexMaximum=Math.max((DisplayMaxVertexScale>0?DisplayMaxVertexScale:5.0), 1.0);
             DijkstraMaxDist = MAXDISTANCE;
             rnd = new Random(); // Give long integer arg else uses time to set seed
    }//eo constructor
    

    
    
   
    /** 
    * Creates a deep copy of a network
     * Makes a deep copy of the input network. 
    *@param islnetinput is an inout islandNetwork to be deep copied to new one.
    */
    public islandNetwork(islandNetwork islnetinput) {
        rnd = new Random(); // Give long integer arg else uses time to set seed
        numberSites = islnetinput.numberSites;
        inputGUI = islnetinput.inputGUI;
        dataread = islnetinput.dataread ;
        inputnameroot = islnetinput.inputnameroot;
        outputnameroot =islnetinput.outputnameroot;
        runname=islnetinput.runname;
        if (infolevel>-2) System.out.println("Creating new Island Network, input name = "+inputnameroot);
        
        distValue = new double [numberSites][numberSites]; // fixed characteristic size
        penalty = new double [numberSites][numberSites];   // distance penalties  
        edgeValue = new double [numberSites][numberSites];  // variable size
        
        for (int i=0; i<numberSites; i++) siteArray[i] = new Site(islnetinput.siteArray[i]);
//        siteValue = new double [numberSites]; // fixed characteristic size
//        vertexValue = new double [numberSites]; // variable size
//        if (siteRank==null) siteRank = new SiteRanking(numberSites); 
//        else siteRank = new SiteRanking(islnetinput.siteRank); // deep copy 
        siteRank = new SiteRanking(islnetinput.siteRank); // deep copy 
//        siteWeight = new double[numberSites];  // S_i * v_i calculated as a statistic
        siteWeightOrder = new int[numberSites];  // points in order [i] = no. of i-th biggest Size
//        siteWeightRank = new int[numberSites];  // points in order [i] = no. of i-th biggest Size
//        siteName = new String [numberSites];
        siteAlphabeticalOrder = new int[numberSites];  // Sites in order of names [i] = no. of i-th site
//        siteX = new double [numberSites];
//        siteY = new double [numberSites];
//        siteZ = new double [numberSites];
        
// perform deep copies        
        for (int i=0; i<numberSites; i++)
        {
//          siteValue [i] = islnetinput.siteValue [i] ;
//          vertexValue  [i]  = islnetinput.vertexValue  [i]; // variable size
//          siteName  [i] = islnetinput.siteName  [i];
//          siteX  [i] = islnetinput.siteX  [i];
//          siteY  [i] = islnetinput.siteY  [i] ;
//          siteZ  [i] = islnetinput.siteZ  [i] ;
//          siteArray[i].displaySize = islnetinput.siteArray[i].displaySize;
//          siteArray[i].weight = islnetinput.siteArray[i].weight;
          siteWeightOrder[i] = islnetinput.siteWeightOrder[i]; 
//          siteArray[i].weightRank = islnetinput.siteArray[i].weightRank; 
          siteAlphabeticalOrder[i] = islnetinput.siteAlphabeticalOrder [i]; 
          for (int j=0; j<numberSites; j++)
          {
            distValue[i][j]  = islnetinput.distValue[i][j];
            penalty[i][j]  = islnetinput.edgeValue[i][j] ;
            edgeValue[i][j] = islnetinput.edgeValue[i][j]  ;
            edgeColour[i][j] = islnetinput.edgeColour[i][j];
          }
        }
        
        minX=islnetinput.minX;
        maxX=islnetinput.maxX;
        minY=islnetinput.minY;
        maxY=islnetinput.maxY;
//        windowSize = islnetinput.windowSize ;
//        double windowScale = windowSize;
        
        // Now Hamiltonian coefficients
        edgeSourceH=islnetinput.edgeSourceH;
        vertexSourceH=islnetinput.vertexSourceH;
        alphaH=islnetinput.alphaH ;
        betaH=islnetinput.betaH ;
        gammaH=islnetinput.gammaH ;
        kappaH=islnetinput.kappaH ;
        lambdaH=islnetinput.lambdaH ;
        distScaleH=islnetinput.distScaleH ;
        shortDistScaleH=islnetinput.shortDistScaleH;
        bH = islnetinput.bH;
//        bEqualsOneH =islnetinput.bEqualsOneH;
        outputcoeffH=islnetinput.outputcoeffH;
        consumptioncoeffH=islnetinput.consumptioncoeffH;

        // properties of the network
        metricNumber=islnetinput.metricNumber;
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
        siteStrengthStats = islnetinput.siteStrengthStats ;     

        
        // General parameters
        updateMode=islnetinput.updateMode;
        outputMode=islnetinput.outputMode;
        edgeModeBinary=islnetinput.edgeModeBinary;
        edgeMode=islnetinput.edgeMode;
        vertexMaximum=islnetinput.vertexMaximum;
        majorModelNumber=islnetinput.majorModelNumber;
        minorModelNumber=islnetinput.minorModelNumber;

        // Display factors
        zeroColourFrac=islnetinput.zeroColourFrac;
        minColourFrac=islnetinput.minColourFrac;
        siteWeightFactor = islnetinput.siteWeightFactor ;
        edgeWidthFactor =islnetinput.edgeWidthFactor ;
        DisplayMaxVertexScale=islnetinput.DisplayMaxVertexScale;
        DisplayMaxEdgeScale=islnetinput.DisplayMaxEdgeScale;
        numberColours=islnetinput.numberColours;
        DisplayVertexType = islnetinput.DisplayVertexType;
        siteWindowMode = islnetinput.siteWindowMode;

        
} //eo constructor copy network
    
    
    
        /** Parses input parameter string
         * See usage for details.
         *@param ArgList array of strings containing -?<value>
         *@return any non zero number is an error.
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
                                case 'b': {
                                     if (ArgList[i].charAt(2)=='t') betaH = Double.parseDouble(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='s') bH = Double.parseDouble(ArgList[i].substring(3));
                                break;}
                                case 'd': {
                                     if (ArgList[i].charAt(2)=='l') distScaleH = Double.parseDouble(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='m') metricNumber = Integer.parseInt(ArgList[i].substring(3));
                                     if (ArgList[i].charAt(2)=='s') shortDistScaleH  = Double.parseDouble(ArgList[i].substring(3));
                                break;}
                                case 'e': {
                                    edgeMode = Double.parseDouble(ArgList[i].substring(2));
                                    if (edgeMode==0) edgeModeBinary = true;
                                    else edgeModeBinary = false;
                                break;}
                                case 'f': {if (ArgList[i].charAt(2)=='i') inputnameroot = ArgList[i].substring(3);
                                           if (ArgList[i].charAt(2)=='o') outputnameroot = ArgList[i].substring(3);
                                           if (ArgList[i].charAt(2)=='a') outputnameroot = "AUTO";
                                           if (ArgList[i].charAt(2)=='r') runname = ArgList[i].substring(3);
                                            
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
                                case 'o': {outputMode = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'u': {updateMode = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'v': {double modelNumber = Double.parseDouble(ArgList[i].substring(2));
                                           majorModelNumber = (int) modelNumber;
                                           minorModelNumber = (int) ((modelNumber - majorModelNumber)*10+0.5);
                                break;}
                                case 'w': {
                                    inputGUI=true;
                                    if (ArgList[i].substring(2,3).equals("n"))
                                        inputGUI =false;
                                            // Use this to set inputGUI
//                                    edgeMode = Double.parseDouble(ArgList[i].substring(2));
//                                    if (edgeMode==0) edgeModeBinary = true;
//                                    else edgeModeBinary = false;
                                break;}
                                case 'x': {vertexMaximum = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'D': {if (ArgList[i].charAt(2)=='v') 
                                           {
                                             if (ArgList[i].charAt(3)=='s') DisplayMaxVertexScale = Double.parseDouble(ArgList[i].substring(4));
                                             if (ArgList[i].charAt(3)=='t') DisplayVertexType = Integer.parseInt(ArgList[i].substring(4));                                              
                                           }
                                           if (ArgList[i].charAt(2)=='e') DisplayMaxEdgeScale = Double.parseDouble(ArgList[i].substring(3));
                                           if (ArgList[i].charAt(2)=='z') zeroColourFrac = Double.parseDouble(ArgList[i].substring(3));
                                           if (ArgList[i].charAt(2)=='n') minColourFrac = Double.parseDouble(ArgList[i].substring(3));
                                            
                                break;}
                               case 'Z': {
                                   if (ArgList[i].charAt(2)=='n') generateNumber= Integer.parseInt(ArgList[i].substring(3));
                                   if (ArgList[i].charAt(2)=='t') generateType= Integer.parseInt(ArgList[i].substring(3));
                                   
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
                
                
                if (outputnameroot.equals("AUTO")) autoSetOutputFileName=true;
                else  autoSetOutputFileName=false;
                setOutputFileName();
                return 0;
            } // eo ParamParse

        /** Shows command line arguments.
         */
            public void Usage(){
                
                islandNetwork i = new islandNetwork();
                System.out.println("...............................................................................");
                System.out.println("Ariadne, iNVERSION "+iNVERSION+" usage: ");
                System.out.println("aegean <options> ");
                System.out.println(" where options are -<char><value> separated by space as follows ");
                System.out.println("  -a<alpha>    Sets alpha, short distance power in edge potential, default "+i.alphaH);
                System.out.println("  -bs<b>       Sets b, power used for site terms, default "+i.bH);
                System.out.println("  -bt<beta>    Sets beta, inverse temperature, default "+i.betaH);
                System.out.println("                Also number of edges per site in PPA mode");
                System.out.println("  -dl<distanceScale> Sets distance scale, "+i.distScaleH);
                System.out.println("  -dm<metricNumber>  Sets metric for Dijkstra calculations, default "+i.shortDistScaleH);
                System.out.println("  -ds<shortdistanceScale> Sets short distance scale, default "+i.shortDistScaleH);
                System.out.println("  -e<edgeMode>      Sets edge max edge value or binary (0 or 1) if 0,");
                System.out.println("                            default "+i.edgeMode);
                System.out.println("  -fa          Sets the output file name to AUTO,");
                System.out.println("                    i.e. automatic format as noted above");
                System.out.println("  -fi<inroot>  Sets root of input  files to be  inroot, ");
                System.out.println("                            default "+i.inputnameroot);
                System.out.println("  -fo<outroot> Sets root of output files to be outroot, ");
                System.out.println("               If set to AUTO chooses automatic name based on <inroot> and parameters ");
                System.out.println("                            default "+i.outputnameroot);
                System.out.println("  -fr<runname> Sets the run name for automatic output file name,");
                System.out.println("                            default "+i.runname);
                System.out.println("  -g<gamma>         Sets gamma, short distance power in edge potential,");
                System.out.println("                            default "+i.gammaH);
                System.out.println("  -i<infolevel>     Sets information level, 0 lowest, default "+i.infolevel);
                System.out.println("  -j<j>        Sets j, sum over all vertex weights, default "+i.vertexSourceH);
                System.out.println("  -k<kappa>         Sets kappa, coefficient of vertex potential, default "+i.lambdaH);
                System.out.println("  -l<lambda>        Sets lambda, coefficient of edge potential, default "+i.lambdaH);
                System.out.println("  -m<mu>       Sets mu, sum over all edge weights, default "+i.edgeSourceH);
                System.out.println("  -o<outputMode>    Sets output mode by bits 1 (0)= on (off), "+i.outputMode);
                System.out.println("                    128 = distance");
                System.out.println("  -u<updateMode>    Sets update mode, 0 PPA, 1 MC, default "+i.updateMode);
                System.out.println("  -v<modelNumber>   Sets model number to use, default "+i.majorModelNumber+"_"+i.minorModelNumber);
                System.out.println("  -w[y|n]      Sets window mode on -wy or off -wn, default "+i.inputGUI);
                System.out.println("  -x<vertexMaximum> Sets edge max vertex value, default "+i.edgeMode);
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
                System.out.println("  -Dz<zeroColourFrac> Sets fraction of largest colour which is to be");
                System.out.println("                       represented as zero colour, default "+i.zeroColourFrac);
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
     */
    void setOutputFileName()  
    {
        if (autoSetOutputFileName)
        {
            String s = "output/"+inputnameroot;
            if (updateMode==0) s=s+"b"+betaH+"PPA";
            else s=s+
                    "_v" + majorModelNumber+"_"+minorModelNumber +
                    "e"+edgeMode+
                    "m"+edgeSourceH+
                    "j"+vertexSourceH+
                    "k"+kappaH+
                    "b"+bH+
                    "s"+distScaleH
                       +"MC";
            s=s+runname;
            outputnameroot = s;
        }
        ;
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
        
     
    /** Shows distance values on printstream.
         *@param PS a print stream for the output such as System.out
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
     public void showDistanceValues(PrintStream PS, String cc, int dec) 
     {
        PS.println(cc+"*** DISTANCE VALUES, scale = "+SepString+distScaleH+SepString+" short scale="+SepString+shortDistScaleH+SepString+"***************** ");
        for (int i =0; i<numberSites; i++) {        
            PS.print(SepString+siteArray[i].name);
        };
        PS.println();
        for (int i =0; i<numberSites; i++) {        
            PS.print(siteArray[i].name);
                for (int j =0; j<numberSites; j++) {        
            PS.print(SepString+TruncDec(distValue[i][j],dec));
            };
            PS.println();
        };
        
    }//eo showDistanceValues

    /** Prints distance values on standard file.
     * <emph>outputnameroot</emph><kbd>_distvalues.dat</kbd> is used.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
public void FileOutputDistanceValues(String cc, int dec) 
    {
      
        String filenamecomplete =  outputnameroot+ "_distvalues.dat";        
        if (infolevel>-2) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
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
               if (infolevel>-2)System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputTransferMatrix



// .............................................................    
    /** Shows scaled distance data table.
    *@param PS a print stream for the output such as System.out
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
      public void showScaledDistanceValues(PrintStream PS, String cc, int dec) {
        double sdv=0.0;
        PS.println(cc+ "--- SCALED DISTANCES in units of "+TruncDec(distScaleH,dec)+" -------------");
        for (int i =0; i<numberSites; i++) {        
            PS.print(SepString+siteArray[i].name);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(distValue[i][j]/distScaleH,dec);        
            PS.print(SepString+sdv);
            };
            PS.println();
        };
        
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
            PS.print(SepString+siteArray[i].name);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(edgePotential1(distValue[i][j]),dec);        
            PS.print(SepString+sdv);
            };
            PS.println();
        };
        
    }//eo showData


// .............................................................    
    /** Shows distance data table in terms of dH values.
     * 
     */
     public void showdHValues(double ev) {
        String SepString = "\t";
        int dec = 3;
        double sdv=0.0;
        System.out.println("--- dH Values for edge value 1.0 ------");
        for (int i =0; i<numberSites; i++) {        
            System.out.print(SepString+siteArray[i].name);
            for (int j =0; j<numberSites; j++) {
            sdv = TruncDec(deltaEdgeHamiltonian(i, j, ev),dec);        
            System.out.print(SepString+sdv);
            };
            System.out.println();
        };
        
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
      
        String filenamecomplete =  outputnameroot+ "_edgevaluesM"+metricNumber+".dat";        
        if (infolevel>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            PS.println(cc+"Number of Sites"+SepString+numberSites);                  
            showEdgeValues(cc, PS, dec);

            try
            { 
               fout.close ();
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputDijstraStats
    
    
    /** Shows edge value table
         *@param cc comment characters put at the start of every line
     *@param PS any print stream
         *@param dec integer number of decimal palces to display
         */     
public void showEdgeValues(String cc, PrintStream PS, int dec)
     {
        double [] toedgecount = new double[numberSites];
        double fromedgecount;
        PS.println("--- Edge Values --------------------------------");
        PS.print("From/to");
        for (int i =0; i<numberSites; i++) {toedgecount[i]=0;}
        for (int i =0; i<numberSites; i++) {PS.print(SepString+siteArray[i].name);}; 
        PS.println(SepString+"Tot From");       
        double totaldistance=0;
        double totalweighteddistance=0;
        for (int i =0; i<numberSites; i++) {        
            fromedgecount=0;
            PS.print(siteArray[i].name);
            for (int j =0; j<numberSites; j++) {
            if(edgeValue[i][j]>0) totaldistance+=distance[i][j];
            totalweighteddistance+=distance[i][j]*edgeValue[i][j];
            fromedgecount+=edgeValue[i][j];    
            toedgecount[j]+=edgeValue[i][j]; 
            PS.print(SepString+TruncDec(edgeValue[i][j],dec));
            };
            PS.println(SepString+TruncDec(fromedgecount,dec));
        };
        PS.print("Tot to");
        double totaledges=0;
        for (int i =0; i<numberSites; i++) 
        {
            totaledges+=toedgecount[i];
            PS.print(SepString+TruncDec(toedgecount[i],dec));
        };
        PS.println(SepString+TruncDec(totaledges,dec));
        PS.println("Tot Dist"+SepString+TruncDec(totaldistance,dec));
        PS.println("Tot W.Dist"+SepString+TruncDec(totalweighteddistance,dec));
     }//eo showEdgeValues

     
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
      
        String filenamecomplete =  outputnameroot+ "_colour.dat";        
        if (infolevel>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            PS.println(cc+"Number of Sites"+SepString+numberSites);                  
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
        for (int i =0; i<numberSites; i++) {PS.print(SepString+siteArray[i].name);}; 
        PS.println();       
        PS.print("S.Col.");
        for (int i =0; i<numberSites; i++) {PS.print(SepString+TruncDec(siteArray[i].displaySize,3));}; 
        PS.print("\n\nFrom/to");
        for (int i =0; i<numberSites; i++) {PS.print(SepString+siteArray[i].name);}; 
        PS.println();       
        for (int i =0; i<numberSites; i++) {        
            PS.print(siteArray[i].name);
            for (int j =0; j<numberSites; j++) {        
            PS.print(SepString+TruncDec(edgeColour[i][j], 3));
            };
            PS.println();
        };
     }//eo showColourValues

         
     
     
     /** Shows site positions and size (fixed data)
         *@param PS a print stream for the output such as System.out
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */     
     public void showFixedSiteVariables(PrintStream PS, String cc, int dec) 
     {
        PS.println(cc+SepString+siteArray[0].fixedDataNameString(SepString) );
        for (int i =0; i<numberSites; i++) 
        {
            PS.println(i+SepString + siteArray[i].fixedDataString(SepString));
        }; 
        PS.println(cc+SepString+"MIN"+SepString+" "+SepString+minX+SepString+minY+SepString+" ");
        PS.println(cc+SepString+"MAX"+SepString+" "+SepString+maxX+SepString+maxY+SepString+" ");
        
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
             siteArray[numberCt]= new Site(numberCt);
             siteArray[numberCt].setName(data.getWord());
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





// -------------------------------------------------------------------------
    /** Reads in site values and position from <emph>inputnameroot</emph>_input.dat.
     * First lines are for site data.  
     * Any line starting with a # is a comment and is discarded.
     * Each starts with name of data variable as a key word, 1st 4 char used case insensitive. 
     * Key words at start are: Shor (ShortName), Name, XPos, YPos, ZPos, Size
     * Site values follow after the keyword sparated by white space list of appropriate values.
     * Any line starting with a * indicates the start of a table of distance data with:
     * *Pe... indicates a penalty table applied to as-the-crow-flies distance calculation
     * *Di... indicates diatnces given directly
     * Finally calculates dist[i][j] data with penalties if necessary
     * 
     */
    public int getSiteData() 
    {
      boolean getPenalties = false;
      String filename = new String();
      filename = inputnameroot+ inputFileExtension; 
      if (infolevel>-1) System.out.println("getSiteData() Starting to read site values and position data from " + filename);
      TextReader data;     // Character input stream for reading data.
      
      int numberCt;  // Number of items actually stored in the array
      String datatype="RUBBISH"; // type of data on the line
      String tempstring; 
      String [] sarray = new String[MAXSITENUMBER]; // large number of possible sites allowed for
      Site tempsite = new Site();
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
              if (infolevel>0) System.out.println("first word is "+datatype);
              if (datatype.startsWith("#")) 
              { // this line is a comment
                  tempstring=datatype+data.getln(); //comment read to end of line
                  if (infolevel>-1) System.out.println("Comment: "+tempstring);
                  continue;
              }
              
              if (datatype.startsWith("*")) 
              { // this line indicates the start of a new section a la Pajek
                  tempstring=datatype+data.getln(); //comment read to end of line
                  if (infolevel>-1) 
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
                  for (int s=0; s<numberSites; s++) siteArray[s]= new Site(s);
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

          if (datatype.substring(0,3).equalsIgnoreCase("*Di")) tabletype=1; // Penalty Table
          if (datatype.substring(0,3).equalsIgnoreCase("*Pe")) tabletype=2; // Distance Table
          if (data.eof() != true) tabletype=0; // No table
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
                    if (siteTo!=numberSites) 
                    {
                        System.out.println("WARNING line "+linenumber+": Wrong number of distances to in site "+siteFrom);
                        tabletype=-3;
                    }
                    siteFrom++;              
                }//eofile
                if (siteFrom!=numberSites) 
                {
                    System.out.println("WARNING line "+linenumber+": Wrong number of entries in table in "+filename);
                    tabletype=-2;
                } 
                
          } //eo if penalty
                    
          if (infolevel>-1) System.out.println("Finished reading from " + filename);
          switch (tabletype)
          {
              case 0: if (infolevel>-1) System.out.println("No table found"); break;
              case 1: if (infolevel>-1) System.out.println("Distances read"); break;
              case 2: if (infolevel>-1) System.out.println("Penalties read"); break;
              default: System.out.println("*** Unknown Table Type or error in reading it"); tabletype = -10+tabletype; break;
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
           for (int i =0; i<numberSites; i++) {
               for (int j =0; j<numberSites; j++) {
                   switch (tabletype) {
                       case 0: //No table found
                           distValue[i][j] = Math.sqrt((siteArray[i].X-siteArray[j].X)*(siteArray[i].X-siteArray[j].X)
                                            +(siteArray[i].Y-siteArray[j].Y)*(siteArray[i].Y-siteArray[j].Y) );
                           break;
                       case 1: // Distances read
                           distValue[i][j] = temptable[i][j];
                           break;
                       case 2: // Penalties read; 
                           distValue[i][j] = Math.sqrt((siteArray[i].X-siteArray[j].X)*(siteArray[i].X-siteArray[j].X)
                   +(siteArray[i].Y-siteArray[j].Y)*(siteArray[i].Y-siteArray[j].Y) )
                   * temptable[i][j];
                               break;
                       default: System.out.println("*** Unknown Table Type or error in reading it");  tabletype =-40; break; 
                   }
                   distValue[i][j]= Math.sqrt((siteArray[i].X-siteArray[j].X)*(siteArray[i].X-siteArray[j].X)
                   +(siteArray[i].Y-siteArray[j].Y)*(siteArray[i].Y-siteArray[j].Y) )
                   * (getPenalties ? penalty[i][j] : 1);
               };
           };
       }
       
       
       return tabletype;
    }  // end of getSitePenaltyData() method

    

// **************************************************************************        
    /** 
     * Jiggle coordinates of sites
     * @param number of sites in x direction
     * @param number of sites in y direction
     * @param distance between nearest neighbours
     */
    public void jiggleSiteData(double scale) 
    {
      double theta=-1.0;
      double r=-1.0;
      double dscale=-1.0;
      double newX=0;
      double newY = 0;
      maxX=-9e99;
      maxY=maxY;
      minX=+9e99;
      minY=minX;
      
      for (int i=0; i<numberSites; i++)
      {
          theta = 2.0*Math.PI*rnd.nextDouble();
          r = (2.0*rnd.nextDouble()-1.0)*scale;
          newX = siteArray[i].X + r*Math.sin(theta);
          newY = siteArray[i].Y + r*Math.cos(theta);
         for (int j=0; j<numberSites; j++)
         {
             dscale = distValue[i][j] / Math.sqrt((siteArray[i].X-siteArray[j].X)*(siteArray[i].X-siteArray[j].X)
                          +(siteArray[i].Y-siteArray[j].Y)*(siteArray[i].Y-siteArray[j].Y) );
             distValue[i][j] = Math.sqrt((newX-siteArray[j].X)*(newX-siteArray[j].X)
                          +(newY-siteArray[j].Y)*(newY-siteArray[j].Y) ) * dscale;
             distValue[i][j] = distValue[j][i];
         }
          siteArray[i].X = newX;
          siteArray[i].Y = newY;
          
          maxX=Math.max(maxX,siteArray[i].X);
          maxY=Math.max(maxY,siteArray[i].Y);
          minX=Math.min(minX,siteArray[i].X);
          minY=Math.min(minY,siteArray[i].Y);
      }//eo for i
      


      }// eo  makeLatticeSiteData   
          

// -------------------------------------------------------------------
  /**
     * Outputs site information suitable for input.
     * Writes site values, positions and distance data to file <emph>inputnameroot</emph><kbd>OUT_svpdist.dat</kbd>.
     * First line site names
     * Second line site Values (fixed sizes)
     * Third line site X positions
     * Fourth line site Y positions
     * Remaining lines are the dist[i][j] data
     *@param cc comment characters put at the start of every line
         */     

    void FileOutputSiteDistance(String cc)
    {
        String SepString = "\t";
        String filenamecomplete =  inputnameroot+ "OUT_svpdist.dat";        
        if (infolevel>-1) System.out.println("Attempting to write to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            PS.print("Name");            
            for (int i =0; i<numberSites; i++) PS.print(SepString+siteArray[i].name );
            PS.println();
            PS.print("Size");            
            for (int i =0; i<numberSites; i++) PS.print(SepString+siteArray[i].size);
            PS.println();
            PS.print("XPos");            
            for (int i =0; i<numberSites; i++) PS.print(siteArray[i].X + SepString);
            PS.println();
            PS.print("YPos");            
            for (int i =0; i<numberSites; i++) {PS.print(siteArray[i].Y + SepString);}
            PS.println();
            for (int i =0; i<numberSites; i++) 
            {
               PS.print(siteArray[i].name );
               for (int j =0; j<numberSites; j++) PS.print(SepString + distValue[i][j]);
               PS.println();
            }        
            try
            { 
               fout.close ();
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
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



        


// *********************************************************************************

        /** Sets Parameters of Hamiltonian.
         * @param double j is source for sum of ste values
         * @param double mu is source for sum of edge values
         * @param double beta is inverse temperature
         * @param double b is source power term
         * @param double alpha is scale for short edge scales
         * @param double gamma beta&*gamme is power for long edge scales
         * @param double distScale scale for edge potential 
         * @param double relativeSmallDistance relative short scale for edge potential 
         */
    public void setHamiltonianParameters(double j, double mu, double bt, double bs, double a, double k,  double g, double l, double ds, double ss) {
             edgeSourceH = mu;
             vertexSourceH = j;
             betaH = bt;
             bH = bs;
             alphaH = a;
             gammaH = g;
             kappaH = k;
             lambdaH = l;
             distScaleH = ds;
             shortDistScaleH = ss;
    }//eo setHam
/* ************************************************************************
 *
 * OUTPUT ROUTINES
 *
 */  
    
    // *******************************************************************
        /** Outputs Network Statistics to a tab delimited file
         */
    // -------------------------------------------------------------------
  /**
     * Outputs bare network in simple tab delimited format.
     *  <emph>nameroot</emph>_info.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputNetworkStatistics(String cc, int dec) 
    {
       // String SepString = "\t";
        String filenamecomplete =  outputnameroot+ "_info.dat";        
        if (infolevel>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
            PS.println("Number of Sites"+SepString+numberSites);                  
        showHamiltonianParameters(PS);
        // calcNetworkStats(); must be calc first
        showNetworkStatistics(cc, PS, dec);
        if ((outputMode & 128) >0) showDistanceValues(PS,cc, dec);
              

            try
            { 
               fout.close ();
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
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
        PS.println(cc+SepString+" ---"+"ARIADNE"+SepString+" iNVERSION "+SepString+iNVERSION+SepString+", date "+SepString+date+SepString+" ---");
    }//eo showiNVERSION
   
    
        /* 
         * Shows Parameters of Hamiltonian on std output
         */
    
    public void showHamiltonianParameters() 
    {
        showHamiltonianParameters(System.out);
        }//eo showHam

        

           /* 
         * Shows Parameters of Hamiltonian on a PrintStream.
         *@param PS a print stream such as System.out
         */
    public void showHamiltonianParameters(PrintStream PS) {
             PS.println("--- Parameters for Hamiltonian Model number"+SepString+majorModelNumber+"_"+minorModelNumber );
             PS.println("    "+getMajorModelString() + " " +getMinorModelString() );
             PS.println("     edgeSource (mu)"+SepString+edgeSourceH );
             PS.println("   vertexSource (j) "+SepString+vertexSourceH);
             PS.println("               beta "+SepString+betaH);
             PS.println("                  b "+SepString+bH);
             PS.println("              alpha "+SepString+alphaH);
             PS.println("              gamma "+SepString+gammaH);
             PS.println("              kappa "+SepString+kappaH);
             PS.println("             lambda "+SepString+lambdaH);
             PS.println("          distScale "+SepString+distScaleH);
             PS.println("     shortDistScale "+SepString+shortDistScaleH);
             PS.println("      metric number "+SepString+metricNumber);
             PS.println("           edgeMode "+SepString+edgeMode);
             PS.println("  edgeModeBinary "+SepString+edgeModeBinary);

    }//eo showHam

    
        /** Prints Network Statistics.
         * @param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void printNetworkStatistics(String cc, int dec) {
        showNetworkStatistics(cc, System.out, dec);
    }

    
    
    /** Shows Network Statistics.
         * @param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal palces to display
         */
    public void showNetworkStatistics(String cc, PrintStream PS, int dec) {
//String SepString = "\t"; // Separation Character for column indication
        //int dec = 3;
       double ew,vw,ev,vv;
        
        // calcNetworkStats();  Must be called first
        
        PS.println(cc+" *** Site Weights and Values *************************** ");
        
        int numberSiteVariables = siteArray[0].numberVariables;
        for (int v=0; v<numberSiteVariables; v++)
        {
           PS.print(siteArray[0].dataName(v)); 
           for (int i=0; i<numberSites; i++)
                PS.print(SepString+siteArray[i].toString(v));
           PS.println();         
        }
     
        
        // edge values
        PS.println(cc+" *** Edge Values *************************** ");
        PS.print(cc+"Name");        
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SepString+siteArray[i].name);
        }
        PS.println(SepString+"Tot.IN E.V.");       

        for (int i=0; i<numberSites; i++)
        {
           PS.print(siteArray[i].name);
           for (int j=0; j<numberSites; j++)
           {
               PS.print(SepString+TruncDec(edgeValue[i][j],dec));
           }//eo j
           PS.println(SepString+TruncDec(inSiteStrengthValue[i],dec)+SepString+siteArray[i].name);                          
        }//eo i

        PS.print("Tot.OUT E.V.");        
        for (int i=0; i<numberSites; i++)
        { 
                PS.print(SepString+TruncDec(outSiteStrengthValue[i],dec));
        };
        PS.println();
        
        // edge weights
        PS.println(cc+" *** Edge Weights *************************** ");
        PS.print(cc+"Name");        
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SepString+siteArray[i].name);
        }
        PS.println(SepString+"Tot.IN E.W.");       
        for (int i=0; i<numberSites; i++)
        {
           PS.print(siteArray[i].name);
           for (int j=0; j<numberSites; j++)
           {
               PS.print(SepString+TruncDec(siteArray[i].getWeight()*edgeValue[i][j],dec));
           }
           PS.println(SepString+TruncDec(inSiteStrengthWeight[i],dec)+SepString+siteArray[i].name);                          
        }
        PS.print("Tot.OUT E.W.");        
        for (int i=0; i<numberSites; i++)
        { 
                PS.print(SepString+TruncDec(outSiteStrengthWeight[i],dec));
        };
        PS.println();         
        
        // Influence
        PS.println(cc+" *** Influence Matrix  (col. j's influence on row i) ***** ");
        PS.println(cc+" Influence Prob. "+influenceProb);
        PS.print(cc+"Name");        
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SepString+siteArray[i].name);
        }
        PS.println();
        PS.print("Infl.Rank");
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SepString+siteArray[i].influenceRank);
        }
        PS.println(SepString+"Infl.Rank");
        for (int i=0; i<numberSites; i++)
        {
           PS.print(siteArray[i].name);
           for (int j=0; j<numberSites; j++)
           {
               PS.print(SepString+TruncDec(siteRank.influenceMatrix[i][j],dec));
           }
           PS.println(SepString+siteArray[i].name);                          
        }

        
        PS.println(cc+"*** SCALES *******************");    
    PS.println(cc+"Single Edge Max Weight       "+SepString+TruncDec(allmaxedgeweight,dec)+SepString+" at vertex "+allmaxedgeweightindex);   
    PS.println(cc+"Single Site Max Weight       "+SepString+TruncDec(maxSiteWeight,dec)+SepString+" at vertex "+maxSiteWeightIndex);   
    PS.println(cc+"Single Site Max out Strength "+SepString+TruncDec(maxOutSiteStrength,dec)+SepString+" at vertex "+maxOutSiteStrengthIndex);   
    PS.println(cc+"*** TOTALS *******************");    
    PS.println(cc+"Total Vertex Value and Weight "+SepString+TruncDec(totVertexValue,dec)+SepString+TruncDec(totVertexWeight,dec));        
    PS.println(cc+"  Total Edge Value and Weight "+SepString+TruncDec(totEdgeValue,dec)+SepString+TruncDec(totEdgeWeight,dec));         

//        int maxvedgeweightindex = -1; 
//


    
    PS.println(cc+"--- Display Factors --------------------------------");    
    PS.println(cc+"Zero, Minimum Fraction for coloured edges = "+SepString+zeroColourFrac+SepString+minColourFrac);
    PS.println(cc+"Max Site and Edge Size = "+SepString+siteWeightFactor+SepString+edgeWidthFactor);
    

              
    } //eo showNet.Stats
    

        /** produces various types of Pajek vertex files.
         * @param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal palces to display
         *@param type of file to write
         */
    public void printPajekSiteFiles(String cc, PrintStream PS, int dec, int variableNumber) 
    {
        for (int i=0; i<numberSites; i++) PS.println(siteArray[i].toString(variableNumber));
    }
    
        /** Produces values associated with sites as a string.
         *@param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal palces to display
         *@param type of file to write
         */
    public String siteString(int valuenumber) 
    {
        String s="Unknown Value"+valuenumber;
        switch (valuenumber)
        {
            
            case 0:  break;
            default: 
        }
             
        return s;
    }
// .................................................................
        /** Shows Network in various ways.
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void showNetwork(String cc, int dec) 
    {   //minColourFrac=0.0;
              System.out.println("\n --- Displaying Network --------------------------------------");
              System.out.println("Output mode "+outputMode+" " +outputModeString());
              System.out.println("Scale for largest vertices in .net file= "+DisplayMaxVertexScale);
              System.out.println("Scale for maximum edges in .net file = "+DisplayMaxEdgeScale);
              System.out.println("Minimum Fraction for coloured edge in .net file = "+minColourFrac);
              System.out.println("Zero Fraction for coloured edge = "+zeroColourFrac);
              calcNetworkStats();
              FileOutputColourValues("#", dec);
              int oldmetricNumber=metricNumber;
              for (int mn=0; mn<5; mn++)
              {
                  metricNumber=mn;
                  doDijkstra();
                  FileOutputEdgeValues("#", dec);
                  FileOutputDijkstraStatistics("#", dec);
                  }
              metricNumber=oldmetricNumber;
              doDijkstra();
              siteRank.FileOutputTransferMatrix("#", dec);
              siteRank.FileOutputInfluenceMatrix("#", dec);
              for (int v=0; v<siteArray[0].numberVariables;v++) FileOutputPajekSiteFiles(v);
              FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,0);
              FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,1);
              FileOutputNetwork("#",siteWeightFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,2);
              FileOutputBareNetwork("#");
              infoPrint(2,"showNetwork this.inputnameroot " + this.inputnameroot);
              PictureWindow PW = new PictureWindow(this);
              infoPrint(2,"showNetwork PW.islnet.inputnameroot " + PW.islnet.inputnameroot );
              PW.drawNetworkWindow();
              infoPrint(2,"In showNetwork finished call to PW.drawNetworkWindow()");
              SiteWindow SW = new SiteWindow(this);
              infoPrint(2,"showNetwork SW.islnet.inputnameroot " + SW.islnet.inputnameroot );
              SW.drawSiteWindow();
              infoPrint(2,"In showNetwork finished call to SW.drawSiteWindow()");
              
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
      
        String filenamecomplete =  outputnameroot+ "_dijkdistM"+metricNumber+".dat";        
        if (infolevel>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
        PS.println(cc+"Number of Sites"+SepString+numberSites);                  
        showDijkstraValues(cc, PS, dec);

            try
            { 
               fout.close ();
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
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
        PS.println("--- Dijkstra Results for metric "+getMetricString()+", metric number"+metricNumber);                  
        if (DijkstraMaxDist<MAXDISTANCE ) PS.println("Connected, max distance "+ TruncDec(DijkstraMaxDist,dec));                  
        else PS.println("DISCONNECTED");    
        double totaldistance =0;
        // site names on line 1
        PS.print("From/To");        
        for (int i=0; i<numberSites; i++)
        { 
            PS.print(SepString+siteArray[i].name);
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
               
            if (distance[i][j]==MAXDISTANCE) PS.print(SepString+" x ");
            else 
            {
                totaldistance+=distance[i][j];
                PS.print(SepString+TruncDec(distance[i][j],dec));
            };
           }
        PS.println();        
        }
        PS.println("\n Total Distance (excl. x) "+totaldistance);
        for (int i=0; i<numberSites; i++) PS.println("..."+SepString);        
    }    


    
// ###################################################################
// Edge class 

/**
 *  Edge properties class.
 *  
 */
    public class Edge
    {
        private int source=-1; //1
        private int target=-1; //2
        private double value=-1; //3
        private double weight=-1;  // 4 source
        private double displaySize=-1; //5
        
// constructor
        /*
         *@param source the source site number
         *@param target source site number
         */
        public Edge(int sourceSite, int targetSite)
          {
            source=sourceSite;
            target=targetSite;
        }
   
    }    
// ###################################################################
// Site class 
    

/**
 *  Site properties class.
 *  
 */
    public class Site
    {
        int numberVariables=19;
        String nameunset = "!!!!";
        String shortnameunset = "!!";
        // These are counted from 0 upwards
        String name=nameunset; //0
        String shortName=shortnameunset ; //1
        int number=-1; // number not set should be positive or zero
        double X=-9999; //2
        double Y=-9999; //3
        double Z=-9999; //4
        double size=-9999; //5
        double value=-9999; //6
        double weight=-9999; //7
        int weightRank =-1; //8
        double ranking=-9999; //9 
        int rankingRank=-1; //10
        double totalInfluenceWeight;//11
        int influence=-1; //12
        int influenceRank=-1;  //13
        double displaySize=-1; //14
        double strength=-1; //15
        double strengthIn=-1; //16
        double strengthOut=-1; //17
        //int [] siteRankRank; // site i ranked [i] by rank scare
        //double [] totalInfluenceWeight;
        //int [] siteInfluenceRank;
        

        // constructor;
        public Site()
          {
            
        }

        // constructor;
        /*
         *@param the number of the site
         */
        public Site(int siteNumber)
          {
            number = siteNumber;
            name=Integer.toString(siteNumber);
            shortName=name;
        }
        
        // constructor;
        // Deep copies site s
          public Site(Site s)
          {
            numberVariables=s.numberVariables;
            name=s.name;
            shortName=s.shortName;
            number=s.number;
            X=s.X;
            Y=s.Y;
            Z=s.Z;
            
        size=s.size;
        value=s.value;
        weight=s.weight;
        weightRank = s.weightRank;
        ranking=s.ranking;
        rankingRank=s.rankingRank;
        totalInfluenceWeight=s.totalInfluenceWeight;
        influence=s.influence; 
        influenceRank=s.influenceRank; 
        displaySize =s.displaySize;
        strength = s.strength;
        strengthIn = s.strengthIn;
        strengthOut = s.strengthOut;
              
          }
        
          /**
           *  Returns string value all data variables.
           *@param separator string e.g. tab or space.
           *@return string with name of data variable number value.
           *
           */
          public String toString(String SEP)
        {
              String s="";
              for (int i=0;i<numberVariables;i++) s=s+toString(i)+SEP;
              return s;
          }
          
          /**
           *  Gets value of one integer site variable.
           *@return The weight = value*size.
           *
           */
          public double getWeight()
        {
              weight=value*size;
              return weight;
          }

          /**
           *  Gets strength of site variable.
           *@return The strength or -1.0 if not possible.
           *
           */
          public double getStrengthIn()
        {
              strengthIn =0; 
              if ((number<0) || (number>=numberSites)) return -1.0;
              for (int j=0; j<numberSites; j++) strengthIn+=edgeValue[number][j];
              strengthIn*=getWeight();
              return strengthIn;
          }

          /**
           *  Sets value of weight.
           *@return The weight = value*size.
           *
           */
          public double setWeight()
        {
              weight=value*size;
              return weight;
          }

          /**
           *  Sets name of site.
           *@param set name to parameter.
           *@return name.
           *
           */
          public String setName(String s)
        {
              name=s;
              shortName=getStartString(s, 2);
              return s;
          }

          
          /**
           *  Sets short name and name of site.
           *@param set name to parameter.
           *@return name.
           *
           */
          public String setName(String shorts, String s)
        {
              shortName=shorts;
              name=s;
              return s;
          }
          
          /**
           *  If unset makes short name the first two characters of name. 
           * 
           */
          public void setShortName()
        {
              if (shortName==shortnameunset) 
              {
                  if (name.length() >2) shortName=name.substring(0, 2);
                  else shortName=name;
              }
          }

          /**
           *  Gets the weight.Sets value of one integer site variable.
           *@param index of data variable to set.
           *@param value to be taken by site variable.
           *
           */
          public boolean isAlpha(int index)
        {
              boolean res = false;
              switch (index)
              {
                  case 0:  
                  case 1: res=true; break; 
        
                  default: res=false;
              }         
              return res;
          }
          
          
          /**
           *  Sets value of one integer site variable.
           * Returns value if an integer is slected 
           * or -97531 if not an integer variable.
           *@param index of data variable to set.
           *@param value to be taken by site variable.
           *@param value of integer else -97531.
           */
          public int setInt(int index, int value)
        {
              int res =0;
              switch (index)
              {
                  case 8:  weightRank=value; break;
                  case 10: rankingRank=value; break;
                  case 13: influenceRank=value; break;
                  case 18: number=value; break;
        
                  default: res=-97531;
              }         
              return res;
          }

          /**
           *  Returns string value of one site variable.
           *@param index of site variable to return.
           *@return value of data variable.
           *
           */
          public double getValue(int index)
        {
            double s=-247.0;
            switch(index)
            {
                case 0: s=shortName.codePointAt(0); break;
                case 1: s=name.codePointAt(0); break;
                case 2: s=X; break;
                case 3: s=Y; break;
                case 4: s=Z; break;
                case 5: s=size; break;
                case 6: s=value; break;
                case 7: s=weight; break;
                case 8: s=weightRank; break;
                case 9: s=ranking; break;
                case 10: s=rankingRank; break;
                case 11: s=totalInfluenceWeight; break;
                case 12: s=influence; break; 
                case 13: s=influenceRank; break;
                case 14: s=displaySize; break;
                case 15: s=strength; break;
                case 16: s=strengthIn; break;
                case 17: s=strengthOut; break;
                case 18: s=number; break;
            }
            return s;
          }

          
         /**
           * 
           *  Returns string value of one site variable.
           *@param index of site variable to return.
           *@return string with name of data variable number value.
           *
           */
          public String toString(int index)
        {
            String s="value unknown";
            switch(index)
            {
                case 0: s=shortName; break;
                case 1: s=name; break;
                case 2: s=Double.toString(X); break;
                case 3: s=Double.toString(Y); break;
                case 4: s=Double.toString(Z); break;
                case 5: s=Double.toString(size); break;
                case 6: s=Double.toString(value); break;
                case 7: s=Double.toString(weight); break;
                case 8: s=Integer.toString(weightRank); break;
                case 9: s=Double.toString(ranking); break;
                case 10: s=Integer.toString(rankingRank); break;
                case 11: s=Double.toString(totalInfluenceWeight); break;
                case 12: s=Integer.toString(influence); break; 
                case 13: s=Integer.toString(influenceRank); break;
                case 14: s=Double.toString(displaySize); break;
                case 15: s=Double.toString(strength); break;
                case 16: s=Double.toString(strengthIn); break;
                case 17: s=Double.toString(strengthOut); break;
                case 18: s=Integer.toString(number); break;
                
            }
            return s;
          }

         /**
           *  Stes the value of a variable from an input string.
           *@param index of site variable to return.
           *@param string of value for variable.
           *
           */
          public int setValue(int index, String s)
        {
              int res=0;
              try{
                  switch(index) {
                      case 0: shortName=s; break;
                      case 1: name=s; break;
                      case 2: X=new Double(s); break;
                      case 3: Y=new Double(s); break;
                      case 4: Z=new Double(s); break;
                      case 5: size= new Double(s); break;
                      case 6: value =new Double(s); break;
                      case 7: weight =new Double(s); break;
                      case 8: weightRank =new Integer(s); break;
                      case 9: ranking =new Double(s); break;
                      case 10: rankingRank =new Integer(s); break;
                      case 11: totalInfluenceWeight=new Double(s); break;
                      case 12: influence=new Integer(s); break;
                      case 13: influenceRank=new Integer(s); break;
                      case 14: displaySize=new Double(s); break;
                      case 15: strength=new Double(s); break;
                      case 16: strengthIn=new Double(s); break;
                      case 17: strengthOut=new Double(s); break;
                      case 18: number=new Integer(s); break;
                  }
              } catch (NumberFormatException e) {
                  System.out.println("Problem in Site.setValue: "+e);
                  res=-1;
              } 
              return res;
          }
          
          
          /**
           *  Returns string of values of all fixed site data variables.
           *@param separator string e.g. tab or space.
           *@return string with of fixed site data values.
           *
           */
          public String fixedDataNameString(String SEP)
        {
              String s=dataName(0); //shortname
              for (int i=1;i<numberVariables;i++) switch(i)
            {
                case 1: //name; 
                case 2: //X
                case 3: //Y
                case 4: //Z
                case 5: //size
                case 18: // number
                        s=s+SEP+dataName(i);
            }
              return s;
          }

          /**
           *  Returns string of values of all fixed site data variables.
           *@param separator string e.g. tab or space.
           *@return string with of fixed site data values.
           *
           */
          public String fixedDataString(String SEP)
        {
              String s=toString(0); //shortname
              for (int i=1;i<numberVariables;i++) switch(i)
            {
                case 1: //name; 
                case 2: //X
                case 3: //Y
                case 4: //Z
                case 5: //size
                case 18: //number
                        s=s+SEP+toString(i);
            }
              return s;
          }
            
          
          /**
           *  Returns string of names of  all data variables.
           *@param separator string e.g. tab or space.
           *@return string with name of data variable number value.
           *
           */
          public String dataNameString(String SEP)
        {
              String s=dataName(0);
              for (int i=1;i<numberVariables;i++) s=s+SEP+dataName(i);
              return s;
          }
          
           /**
           *  Returns string name of one site variable.
           *@param index of site variable to return.
           *@return string with name of data variable number value.
           */
        public String dataName(int index)
        {
            String s="value unknown";
            switch(index)
            {
                case -1: s="Degree"; break;
                case 0: s="ShortName"; break;
                case 1: s="Name"; break;
                case 2: s="XPos"; break;
                case 3: s="YPos"; break;
                case 4: s="ZPos"; break;
                case 5: s="Size"; break;
                case 6: s="Value"; break;
                case 7: s="Weight"; break;
                case 8: s="WeightRank"; break;
                case 9: s="Ranking"; break;
                case 10: s="RankingRank"; break;
                case 11: s="TotInfluenceWeight"; break;
                case 12: s="Influence"; break;
                case 13: s="InfluenceRank"; break;                
                case 14: s="DisplaySize"; break;                
                case 15: s="Strength"; break;                
                case 16: s="StrengthIn"; break;                
                case 17: s="StrengthOut"; break;                
                case 18: s="Number"; break;                
            }
            return s;
        }
       
         /**
           *  Returns number of site variable with given name.
          * Compares the first length characters of input string
           *@param input string to test
          * @param number of characters from start to use for comparison
          * @return variable number, -1 if none found, -2 if input too short.
           */
        public int dataNameNumber(String input, int length)
        {
            if (input.length() < length) return -2;
            int res=-1;
            String s=input.substring(0, length);
            for (int v=0; v<numberVariables;v++) 
              {
                  if (s.equalsIgnoreCase(dataName(v).substring(0,length)))
                  {
                      res=v;
                      break;
                  }
              }
            return res;
        }
        

    }// eo site class    

// ###################################################################
// SiteRanking class includes transfer matrix and influence functions


/**
 *  Site Ranking Class
 *  
 */
    public class SiteRanking
    {
       // Care: transferMatrix[i][j] is from j to i as in normal matrix language        
        double [] [] influenceMatrix;
        double [] [] transferMatrix;
        //double [] siteRank; 
        int [] siteRankOrder; // [i]= site ranked i-throws by Rank
        //int [] siteRankRank; // site i ranked [i] by rank scare
        //double [] totalInfluenceWeight;
        //int [] siteInfluenceRank;
        //int [] siteInfluence; // siteInfluence[i] = no. of site that influences site i most;
        int [] siteInfluenceOrder;
        int numberRankSites;
        int influenceSteps=-1;
        double influenceProb=-1.0;

          
          
          // constructor;
          public SiteRanking (int numberSites)
          {
           numberRankSites= numberSites; 
//           influenceProb = infProb;
           // Care: transferMatrix[i][j] is from j to i as in normal matrix language        
           transferMatrix = new double [numberRankSites][numberRankSites];
           influenceMatrix = new double [numberSites][numberSites];
           //siteRank = new double [numberRankSites];
           //siteRankRank = new int [numberRankSites];
           siteRankOrder = new int [numberRankSites]; // [i] = number of site of rank i
           
           //totalInfluenceWeight = new double [numberRankSites]; // Total influence by weight of site i
           //siteInfluence = new int [numberSites];  // owner of site i by influence
           siteInfluenceOrder = new int [numberRankSites]; //[i] = number of site ranked i by influence
           //siteInfluenceRank  = new int [numberSites]; // Rank by influence of site i
           //influenceRank = new int [numberSites]; // 
           }
           
          // constructor deep copy;
          public SiteRanking (SiteRanking oldsr)
          {
           numberRankSites= oldsr.numberRankSites ; 
           influenceProb = oldsr.influenceProb;
           // Care: transferMatrix[i][j] is from j to i as in normal matrix language        
           transferMatrix = new double [numberRankSites][numberRankSites];
           influenceMatrix = new double [numberSites][numberSites];
           //siteRank = new double [numberRankSites];
           siteRankOrder = new int [numberRankSites];
           //siteRankRank = new int [numberRankSites];
           //totalInfluenceWeight = new double [numberRankSites];
           siteInfluenceOrder  = new int [numberRankSites];
           //siteInfluence = new int [numberSites];
           //siteInfluenceRank  = new int [numberSites];

           for (int i=0; i<numberRankSites; i++) 
            {
                //siteRank[i]= oldsr.siteRank[i];
                siteRankOrder[i]= oldsr.siteRankOrder[i];
                //siteRankRank[i]= oldsr.siteRankRank[i];
                //totalInfluenceWeight[i] = oldsr.totalInfluenceWeight[i] ;
                //siteInfluence[i]= oldsr.siteInfluence[i];
                siteInfluenceOrder[i] = oldsr.siteInfluenceOrder[i];
                //siteInfluenceRank[i] = oldsr.siteInfluenceRank[i];
                for (int j=0; j<numberRankSites; j++)
                {
                  transferMatrix[i][j]=oldsr.transferMatrix[i][j]; 
                  influenceMatrix[i][j]=oldsr.influenceMatrix[i][j]; 
                }
            }//eo for i
           
           }//eo deep copy constructor
           
// Methods of Rank class

//     public double getRank(int i)
//     {
//        double rank=-2.0;
//        if ((i<0) || (i>= numberRankSites) ) return(-1.0);
//        try { 
//              rank= siteArray[i].ranking; 
//            } catch (ArithmeticException e)
//            {
//                rank=-3.0;
//            };
//       return(rank);     
//     }
//
//     public double getInfluence(int i)
//     {
//        double rank=-2.0;
//        if ((i<0) || (i>= numberRankSites) ) return(-1.0);
//        try { 
//              rank= siteArray[i].influence; 
//            } catch (ArithmeticException e)
//            {
//                rank=-3.0;
//            };
//       return(rank);     
//     }

     public double getTransferMatrix(int i, int j)
     {
        double entry=-2.0;
        if ((i<0) || (i>= numberRankSites) ) return(-1.0);
        if ((j<0) || (j>= numberRankSites) ) return(-1.1);
        try { 
              entry= transferMatrix[i][j]; 
            } catch (ArithmeticException e)
            {
                entry=-3.0;
            };
       return(entry);     
     }

     public double getInfluenceMatrix(int i, int j)
     {
        double entry=-2.0;
        if ((i<0) || (i>= numberRankSites) ) return(-1.0);
        if ((j<0) || (j>= numberRankSites) ) return(-1.1);
        try { 
              entry= influenceMatrix[i][j]; 
            } catch (ArithmeticException e)
            {
                entry=-3.0;
            };
       return(entry);     
     }
    
// *******************************************************************************    
    
         /** 
          * Calculate some ranking based on Transfer Matrix Diffusion.
          *@param numberIterations = number of diffusion time steps-number of sites
          */
         
    public void calcRanking(int numberIterations) 
    {
        calcTransferMatrix(0);
        double [] newRank = new double [numberRankSites];
        double [] oldRank = new double [numberRankSites];
        for (int i=0; i<numberRankSites; i++) newRank[i]=1.0/((double) numberRankSites);
        for (int i=0; i<numberRankSites; i++) siteArray[i].ranking=0;
                
        for (int t=-numberRankSites; t<numberIterations; t++)
        { 
            for (int i=0; i<numberRankSites; i++) 
                {
                    oldRank[i]=newRank[i];
                    newRank[i]=0;
                }
                
            for (int i=0; i<numberRankSites; i++) 
            {
                for (int j=0; j<numberRankSites; j++)
                {
                 newRank[i]+= transferMatrix[i][j] * oldRank[j];  
                }
                
            }//eo for i
            if (t>0) for (int i=0; i<numberRankSites; i++) siteArray[i].ranking+=newRank[i];
    }// eo for  t
        double norm=0.0;
        for (int i=0; i<numberRankSites; i++) norm+=siteArray[i].ranking;
        if (norm>0) for (int i=0; i<numberRankSites; i++) siteArray[i].ranking=siteArray[i].ranking/norm;;
        
        // Now calculate Ranking Order siteRankOrder[i] = number of site ranked i        
         calcSiteRankOrder(9, siteRankOrder, 10 ); //9=Rank, 10=RankingRank 
        
       
        
        }// eo calcRanking

// ----------------------------------------------------------------------    
    /** Calculate some Transfer Matrix for diffusion analysis.
     *  Method of Rank
      *@param probMethod =0 for ranking, =1 for influence main, =2 influence first step
         */
         
    public void calcTransferMatrix(int probMethod) 
    {
//        int probMethod =0;
        double norm;
        double normSite=0.0;
        double[] randomSite = new double [numberRankSites];
        // calc weighted random site probability
        for (int i=0; i<numberRankSites; i++) normSite+=siteArray[i].getWeight();
        for (int i=0; i<numberRankSites; i++) 
        {
            if (normSite>0) randomSite[i]=siteArray[i].getWeight()/normSite;
            else randomSite[i]=1.0/numberRankSites;
        }
        
        for (int i=0; i<numberRankSites; i++)
        { 
            norm=0.0;
                for (int j=0; j<numberRankSites; j++) {
                    switch (probMethod) {
                        case 2:
                        case 1:
                        case 0:
                        default: norm+=edgeValue[i][j];
                    }
                }//eo for j
            
            for (int j=0; j<numberRankSites; j++)
            {
                if (norm ==0) 
                switch (probMethod) 
                { // vertex i is a dead end
                    case 2:
                    case 1: transferMatrix[j][i]=((i==j)?1:0);
                            break;
                    case 0:
                    default: transferMatrix[j][i]=randomSite[i];
                }
                else switch (probMethod) 
                { // vertex i has some edges
                    case 2: transferMatrix[j][i]=edgeValue[i][j];
                             break;
                    case 1:
                    case 0:
                    default: transferMatrix[j][i]=edgeValue[i][j]/norm;
                };
            }//eo for j
            
        }//eo for i
        
    }// eo calcTransferMatrix
    
// ----------------------------------------------------------------------    
    /** Calculate an Influence Matrix, influenceMatrix[i][j] is influence of j on i.
      *  IM(t+1) = 1 + IM(t)* (p TM); IM(t_final)= IM(t_final)*siteWeight
      *  
      *@param probMethod =0 for ranking, =1 for influence
         */
         
    public void calcInfluence(double influenceProbNew) 
    {
        siteInfluenceOrder = new int [numberRankSites]; //[i] = number of site ranked i by influence
        
        double [] [] influenceMatrixOdd = new double [numberSites][numberSites];
        double [] [] pTMatrix = new double [numberSites][numberSites];
        influenceProb = influenceProbNew;
        if ((influenceProb<0) || (influenceProb>1) ) influenceProb=0.0; 
        influenceSteps = numberSites/2; // initialise to maximum value
        double influenceProbMax = influenceSteps/((double) (1.0+influenceSteps));        
        if (influenceProb<influenceProbMax ) 
             influenceSteps = ((int) (0.5+ influenceProb/(1.0-influenceProb)));
        
        if (infolevel>-1) System.out.println(" --- Starting to calculate TransferMatrix for Influence ");
        
        calcTransferMatrix(1);
//        System.out.println(" --- Starting to calculate Influence over "+influenceSteps+" steps");
        // initialise and set up first step away from site
        // Remember  edgeValue[j][i] is from j to i
        //double firstStepRemain =maxOutSiteStrength;
        for (int i=0; i<numberSites; i++) 
        {
          for (int j=0; j<numberSites; j++) 
          {
             //pTMatrix[i][j]=transferMatrix[i][j]*influenceProb;
             pTMatrix[i][j]=edgeValue[j][i]*influenceProb;
             influenceMatrix[i][j]=  (( (i==j) ? 1 : 0.0 ) + edgeValue[j][i]*influenceProb ) *siteArray[j].getWeight() ;
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
        
//        System.out.println(" --- Finishing with (1-p) factor for Influence ");        
//        for (int i=0; i<numberSites; i++) 
//          {
//           for (int j=0; j<numberSites; j++) 
//           {
//            influenceMatrix[i][j]=influenceMatrix[i][j]*(1.0-influenceProb);
//           }// eo for j
//          }//eo for i

//        System.out.println(" --- Starting to calculate max influence on each site ");

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

//       System.out.println(" --- Finished calculating Influence");

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
  /**
   *  Method of Rank
     * Outputs transfer matrix used for ranking in simple tab delimited format
     *  <emph>nameroot</emph>_transmat.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputTransferMatrix(String cc, int dec) 
    {
      
        String filenamecomplete =  outputnameroot+ "_transmat.dat";        
        if (infolevel>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
        PS.println(cc+"Number of Sites"+SepString+numberRankSites);                  
        showTransferMatrix(cc, PS, dec);

            try
            { 
               fout.close ();
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputTransferMatrix

    // ...............................................................
        /** Shows TransferMatrix 
         *  Method of Rank
         *@param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal places to display
         */
    public void showTransferMatrix(String cc, PrintStream PS, int dec) 
    {
        PS.println("Transfer Matrix ");                  
        double totaldistance =0;
        // site names on line 1
        PS.print("To/From");        
        for (int i=0; i<numberRankSites; i++)
        { 
            PS.print(SepString+siteArray[i].name);
        }
        PS.println();        
        // Dijkstra values on line 2
        for (int i=0; i<numberRankSites; i++)
        {
           PS.print(siteArray[i].name);        
           for (int j=0; j<numberRankSites; j++)
           {
            PS.print(SepString+TruncDec(transferMatrix[i][j],dec));
            }
           PS.println();        
           }
        for (int i=0; i<numberRankSites; i++) PS.println("..."+SepString);        
        
    } // eo showTransferMatrix
// ...........................................................................
        /** Shows Transfer Matrix on std output
          *  Method of Rank
         *@param cc comment characters put at the start of every line
         *@param dec integer number of decimal palces to display
         */
    public void showTransferMatrix(String cc,  int dec) 
    {
        showTransferMatrix(cc,System.out, dec);
    }

    
// -------------------------------------------------------------------
  /**
   *  Method of Rank
     * Outputs influence matrix used for ranking in simple tab delimited format
     *  <emph>nameroot</emph>_transmat.dat general info
     * @param cc comment characters put at the start of every line
     * @param dec number of decimal places to show
     */
    public void FileOutputInfluenceMatrix(String cc, int dec)
    {

        String filenamecomplete =  outputnameroot+ "_inflmat.dat";
        if (infolevel>-1) System.out.println("Attempting to write general information to "+ filenamecomplete);

        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
        PS.println(cc+"Number of Sites"+SepString+numberRankSites);
        showInfluenceMatrix(cc, PS, dec);

            try
            {
               fout.close ();
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputInfluenceMatrix

    // ...............................................................
        /** Shows InfluenceMatrix
         *  Method of Rank
         *@param cc comment characters put at the start of every line
         *@param PS printstream
         *@param dec integer number of decimal places to display
         */
    public void showInfluenceMatrix(String cc, PrintStream PS, int dec)
    {
        PS.println("Influence Matrix ");
        double totaldistance =0;
        // site names on line 1
        PS.print("To/From");
        for (int i=0; i<numberRankSites; i++)
        {
            PS.print(SepString+siteArray[i].name);
        }
        PS.println();
        
        for (int i=0; i<numberRankSites; i++)
        {
           PS.print(siteArray[i].name);
           for (int j=0; j<numberRankSites; j++)
           {
            PS.print(SepString+TruncDec(influenceMatrix[i][j],dec));
            }
            PS.println();
           }
        for (int i=0; i<numberRankSites; i++) PS.println("..."+SepString);

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


    
    
 // *******************************************************************************    
    
         /** Calculate Network Distances.
          *
         */
         
    public void calcNetworkDistances() 
    {
        siteDistanceStats = new StatisticalQuantity(MAXDISTANCE, -1.0);
        doDijkstra();
        for (int i=0; i<numberSites; i++) 
        {
            for (int j=0; j<numberSites; j++)
            {
                double d= distance[i][j];
                if (d<MAXDISTANCE) siteDistanceStats.add(distance[i][j]);
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
      siteRank.calcRanking(numberSites*2+10);
      siteRank.calcInfluence(influenceProb);

      // Now calculate Ranking Order siteWeightOrder[i] = number of site ranked i   
      //double [] siteWeight = new double [numberSites]; 
      siteWeightOrder = new int [numberSites]; 
      //int [] siteWeightRank = new int [numberSites]; 
      for (int i=0; i<numberSites; i++) siteArray[i].setWeight();
      calcSiteRankOrder(7,siteWeightOrder,8); //7 = weight, 8= WeightRank
      
        // site and edge weights and value
       inSiteStrengthWeight =  new double[numberSites];
       outSiteStrengthWeight =  new double[numberSites];
       inSiteStrengthValue =  new double[numberSites];
       outSiteStrengthValue =  new double[numberSites];
       for (int i=0; i<numberSites; i++)
        {
          inSiteStrengthValue[i] =0.0;
          inSiteStrengthWeight[i]=0.0;  
          outSiteStrengthWeight[i]=0.0;
          outSiteStrengthValue[i]=0.0; 
        }
      double vV,eV;
      siteWeightStats = new StatisticalQuantity(99999.0,-1.0);
      edgeWeightStats = new StatisticalQuantity(99999.0,-1.0);     
      edgeValueStats = new StatisticalQuantity(99999.0,-1.0);     
      siteStrengthStats = new StatisticalQuantity(99999.0,-1.0);     
        for (int i=0; i<numberSites; i++)
        {
          vV=siteArray[i].value;
          vw=siteArray[i].getWeight(); // siteArray[i].name has weight vw  
          siteWeightStats.add(vw); // updates the stats on the site weights
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
           for (int j=0; j<numberSites; j++)
           {
               eV=edgeValue[i][j];
               ew = vw*eV; // edge weight
               totEdgeValue  += eV; 
               totEdgeWeight += ew; 
               inSiteStrengthValue[j]+=eV;
               inSiteStrengthWeight[j]+=ew;
               outSiteStrengthValue[i]+=eV;
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
           siteStrengthStats.add(outSiteStrengthWeight[i]);
           if (maxOutSiteStrength < outSiteStrengthWeight[i])  
           {
               maxOutSiteStrength = outSiteStrengthWeight[i];
               maxOutSiteStrengthIndex =i;
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

        for (int i=0; i<numberSites; i++)
        {
           siteArray[i].strengthIn=inSiteStrengthWeight[i];
           siteArray[i].strengthOut=outSiteStrengthWeight[i];
           siteArray[i].strength=inSiteStrengthWeight[i]+outSiteStrengthWeight[i];
           switch(DisplayVertexType)
           {
               case 1: siteArray[i].displaySize = siteArray[i].ranking*numberColours/mvw; 
                   break;
               case 2:
               case 0:
               default: siteArray[i].displaySize = siteArray[i].weight*numberColours/mvw; // siteArray[i].name has weight vw
           }            
           //if (siteArray[i].displaySize>numberColours) siteArray[i].displaySize=numberColours;
           if (siteArray[i].displaySize<0) siteArray[i].displaySize=0;
           siteArray[i].Z=siteArray[i].displaySize;
           if (updateMode>0) swi=siteArray[i].getWeight();
// Problem here???  for (int i=0; i<numberSites; i++) siteArray[i].getWeight(); 
           for (int j=0; j<numberSites; j++)
           {
               edgeColour[i][j] = swi*numberColours*edgeValue[i][j]/amew; // edge weight colour 
               if (edgeColour[i][j]>numberColours) edgeColour[i][j]=numberColours;
               if (edgeColour[i][j]<numberColours*minColourFrac)
               {  if (edgeColour[i][j]<numberColours*zeroColourFrac) edgeColour[i][j]=0;
                  else edgeColour[i][j]=1;
               }
               
           }           
        }
      if (updateMode>0) 
      {
        for (int i=0; i<numberSites; i++)
        {
           for (int j=0; j<numberSites; j++)
           {
            if ((edgeColour[i][j]==zeroColourFrac*numberColours) &&  (edgeColour[j][i]>zeroColourFrac*numberColours) ) edgeColour[i][j]=minColourFrac*numberColours; 
           }
        }
      }; 
      
    };
    
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
            gns = GN.toString(gn);
            if (gn>90) gns="95";
            if (gn<10) gns = "05";
            pajekGrey[i] = "Gray"+gns;
        };
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

// *******************************************************************
        
// -------------------------------------------------------------------
  /**
     * Outputs bare network in simple tab delimited format.
     *  <emph>nameroot</emph>BareNet.dat general info
     * @param cc comment characters put at the start of every line
     * 
     */
    void FileOutputBareNetwork(String cc){

        String SepString = "\t";
        String filenamecomplete =  outputnameroot+ "BareNet.dat";
        
        if (infolevel>-1) System.out.println("Attempting to write to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            showiNVERSION(cc,PS);
        PS.println(cc+"Line 0"+SepString+"Number Sites,"
           +SepString+"Line 1"+SepString+"Site Size,"
           +SepString+"Line 2"+SepString+"Vertex Values,"+SepString
                     +"Line >=3"+SepString+"Edge Values Value");
        PS.println(numberSites);                  
        for (int i =0; i<numberSites; i++) {PS.print(siteArray[i].size + SepString);}
        PS.println();
        for (int i =0; i<numberSites; i++) {PS.print(siteArray[i].value + SepString);}
        PS.println();
        for (int i =0; i<numberSites; i++) {        
            for (int j =0; j<numberSites; j++) {
            PS.print(edgeValue[i][j] + SepString);
            };
            PS.println();
          }; // eo for i

            try
            { 
               fout.close ();
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
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
     * @param fileType switch true for BW picture, false for colour
     */
    void FileOutputNetwork(String cc, int siteWeightFactor, int edgeWidthFactor, 
                           double minColourFrac, double zeroColourFrac, 
                           int fileType)  {

        double ew,vw;
        
        // Convert X Y locations to 0 - 1 scale for Pajek
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
        };
        double XOffset = (maxX+minX)/2.0;
        double YOffset = (maxY+minY)/2.0;
        double XScale = (maxX-minX)/0.8;
        double YScale = (maxY-minY)/0.8;
        double Scale = (XScale>YScale) ? XScale : YScale;
        
        String filenamecomplete;
        switch (fileType)
        {
            case 1: filenamecomplete = outputnameroot+ "BW.net"; break;
            case 2: filenamecomplete = outputnameroot+ "C.net"; break;
            case 0: 
            default: filenamecomplete = outputnameroot+ "PLAINV.net";
        }
        
        if (infolevel>-1) System.out.println("Attempting to write to "+ filenamecomplete);
            
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
//              fracSize = siteArray[i].displaySize/numberColours; 
//              if (fracSize>1) fracSize=1;
//              size = siteWeightFactor*fracSize;
              PS.print((i+1)+"  \""+siteArray[i].name+"\" "
                       + (((siteArray[i].X-XOffset)/Scale)+0.5)+" " 
                       + (((siteArray[i].Y-YOffset)/Scale)+0.5)+" ");
              switch (fileType) 
              {
                  case 2:
                  case 1: PS.print( siteArray[i].Z
                          +" s_size 1 " 
                          + " x_fact "+TruncDec(siteArray[i].displaySize,2) 
                          + " y_fact "+TruncDec(siteArray[i].displaySize,2));
                          break;
                  case 0: 
                  default: PS.print( " 0.5 "); 
              }
              switch (fileType) 
              {
                  case 1: PS.println(" ic "+pajekGrey[sc]+"   bc "+pajekGrey[numberColours]); break;
                  case 2: PS.println(" ic "+pajekColour[sc]+"   bc "+pajekColour[numberColours]); break;
                  case 0:
                  default: PS.println();
              }
       
            }
            
            //for the arcs
            //      1      2       1 c Blue
            // gives an arc between vertex 1 and 2, value 1 colour black
            int arcCount =0;
            for (int i=0; i<numberSites; i++)
            {
               for (int j=0; j<numberSites; j++)
               {  if (edgeColour[i][j]> numberColours*zeroColourFrac) arcCount++;
               }
            };
            int width;
            PS.println("*Arcs    "+arcCount);            
            for (int i=0; i<numberSites; i++)
            {
               vw=siteArray[i].getWeight();   
               for (int j=0; j<numberSites; j++)
               {  
                  int ec=(int) (0.499999+edgeColour[i][j]);
                  if ((i==j) || (ec<=zeroColourFrac*numberColours) ) continue;
                  if (ec>numberColours) ec=numberColours;
                  if (ec<minColourFrac*numberColours) ec=1;
                  if (updateMode==0) ew=((double) ec)/((double) numberColours); 
                  else ew= vw*edgeValue[i][j];
                  width = (edgeWidthFactor*ec)/numberColours;
                  PS.print((i+1)+"  "+(j+1)+"   " + ew + " w "+ width);
                  switch (fileType)
        {
            case 1: PS.println("  c "+pajekGrey[ec]); break;
            case 2: PS.println("  c "+pajekColour[ec]); break;
            case 0: 
            default: PS.println();
        }
        
               }
            }

            try
            { 
               fout.close ();
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
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
     * where the .ext is selcted by the input parameter and is 
     * .clu for integers (partitions) and .vec for doubles (vectors).
     * @param variableNumber selects which entry to use from site
     * 
     */
    void FileOutputPajekSiteFiles(int variableNumber)  
    {

        String ext="No Such File";
        switch(variableNumber)
            {
                case 5: ext=".vec"; break; //s=Double.toString(size); break;
                case 6: ext=".vec"; break; // s=Double.toString(value); break;
                case 7: ext=".vec"; break; //s=Double.toString(weight); break;
                case 8: ext=".clu"; break; //s=Integer.toString(weightRank); break;
                case 9: ext=".vec"; break; //s=Double.toString(ranking); break;
                case 10: ext=".clu"; break; //s=Integer.toString(rankingRank); break;
                case 11: ext=".vec"; break; //s=Double.toString(totalInfluenceWeight); break;
                case 12: ext=".clu"; break; //s=Integer.toString(influence); break; 
                case 13: ext=".clu"; break; // int influenceRank=-1;  //13
                case 14: ext=".vec"; break; //int displaySize=-1; //14
                case 15: ext=".vec"; break; //double strength=-1; //15
                case 16: ext=".vec"; break; // double strengthIn=-1; //16
                case 17: ext=".vec"; break; // double strengthOut=-1; //17
            default:    ext="No Such File";          
            }
        if (ext=="No Such File") return;
        
        String filenamecomplete =outputnameroot+siteArray[0].dataName(variableNumber)+ext;
        if (infolevel>-1) System.out.println("Attempting to write to "+ filenamecomplete);
            
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
               if (infolevel>-1) System.out.println("Finished writing to "+ filenamecomplete);
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


// *********************************************************************************

 /*
  * ParameterPanel class makes a Box with parameter information.
 */
public class ParameterBox{
    Box topBox;
    
// Constructor
 public ParameterBox()
 {
     topBox = Box.createVerticalBox();
 }
 
 /*
  * Returns a Box with parameter information
  *@parameter islandNetwork upon which information is required.
  *@return a Box ready to be added to panels etc.
  */
 private Box get(islandNetwork islnet)
 {
        topBox.add(new JLabel("   input file: "+islnet.inputnameroot, JLabel.LEFT));
        topBox.add(new JLabel(" output files: "+islnet.outputnameroot,JLabel.LEFT));
        topBox.add(new JLabel(" Model: "+islnet.getMajorModelString()+"+"+islnet.getMinorModelString() ,JLabel.LEFT),Box.LEFT_ALIGNMENT );
        JPanel inputBox = new JPanel();
        inputBox.setLayout( new GridLayout(6,3));
        inputBox.add(new JLabel(" model number "+majorModelNumber+"_"+islnet.minorModelNumber));
        if ( islnet.updateMode == 0) inputBox.add(new JLabel("         PPA Update "));
        else                  inputBox.add(new JLabel(" Monte Carlo Update "));
        if (islnet.edgeMode<=0) inputBox.add(new JLabel(" Binary Edge Values "));
        else inputBox.add(new JLabel("   max edge value "+islnet.edgeMode));        
        inputBox.add(new JLabel("    max vertex value "+islnet.vertexMaximum));
        inputBox.add(new JLabel("                 mu  "+islnet.edgeSourceH));
        inputBox.add(new JLabel("                  j  "+islnet.vertexSourceH));
        inputBox.add(new JLabel("               kappa "+islnet.kappaH));
        inputBox.add(new JLabel("              lambda "+islnet.lambdaH));        
        inputBox.add(new JLabel("                   b "+islnet.bH));        
        inputBox.add(new JLabel("      distance scale "+islnet.distScaleH));        
        inputBox.add(new JLabel("short distance scale "+islnet.shortDistScaleH));        
        inputBox.add(new JLabel("       metric number "+islnet.shortDistScaleH));        
        inputBox.add(new JLabel("                beta "+islnet.betaH));        
        inputBox.add(new JLabel("    Zero Colour Frac "+islnet.zeroColourFrac));        
        inputBox.add(new JLabel("    Min. Colour Frac "+islnet.minColourFrac));        
        if (islnet.DisplayMaxVertexScale>0) inputBox.add(new JLabel(" Absolute Vertex Display, Max "+islnet.DisplayMaxVertexScale));        
        else inputBox.add(new JLabel("Relative Vertex Display"));        
        if (islnet.DisplayMaxEdgeScale>0) inputBox.add(new JLabel(" Absolute Edge Display, Max "+islnet.DisplayMaxEdgeScale));        
        else inputBox.add(new JLabel("Relative Edge Display"));        
        String s;
        double ip = islnet.siteRank.influenceProb;
        if (ip>0.99999) s=" Infinite Influence Range";
        else s=" Influence Range " + TruncDec(ip/(1.0-ip),3);
        if (ip<0) s=" Invalid Influence ";
        s=s+" (prob="+TruncDec(ip,3)+")";
        inputBox.add(new JLabel(s));        
        switch (DisplayVertexType)
        {
            case 2: s="sites by Influence"; break;
            case 1: s="sites by Rank"; break;
            case 0:
            default: s="sites by Size";
        }
        inputBox.add(new JLabel(s));
        topBox.add(inputBox);
        return topBox;
}//eo get

}// eo ParameterPanel class

// Adds a panel with output information
 /*
  * ParameterPanel class makes a Box with parameter information.
 */
public class OutputMeasuresPanel{
    JPanel infoBox;
    
// Constructor
 public OutputMeasuresPanel()
 {
     infoBox = new JPanel();
 }
 
 /*
  * Returns a Box with output statistics information.
  *@parameter islandNetwork upon which information is required.
  *@parameter decPoints number of decimal points to show
  *@return a Box ready to be added to panels etc.
  */
 private JPanel get(islandNetwork islnet, int decPoints )
 {
        infoBox.setLayout( new GridLayout(5,5));
//        infoBox.add(new JLabel(" Maximum Site Weight  "+TruncDec(islnet.maxSiteWeight,decPoints)));
        //maxSiteWeight is the largest vertex weight, 
//        infoBox.add(new JLabel(" Maximum Edge Weight  "+TruncDec(islnet.allmaxedgeweight ,decPoints)));
//        infoBox.add(new JLabel(" Max. Site Out Strength  "+TruncDec(islnet.maxOutSiteStrength,decPoints)));

// Header line
        infoBox.add(new JLabel("Quantity/Value "));
        infoBox.add(new JLabel("Average"));
        infoBox.add(new JLabel("Sigma"));
        infoBox.add(new JLabel("Minimum"));
        infoBox.add(new JLabel("Maximum"));
        //
        infoBox.add(new JLabel("Site Weight"));
        infoBox.add(new JLabel(TruncDecString(islnet.siteWeightStats.getAverage() ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.siteWeightStats.getSigma() ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.siteWeightStats.minimum ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.siteWeightStats.maximum ,decPoints)));
        //
        infoBox.add(new JLabel("Site Out Strength"));
        infoBox.add(new JLabel(TruncDecString(islnet.siteStrengthStats.getAverage() ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.siteStrengthStats.getSigma() ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.siteStrengthStats.minimum ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.siteStrengthStats.maximum ,decPoints)));
        //
        infoBox.add(new JLabel("Edge Weight"));
        infoBox.add(new JLabel(TruncDecString(islnet.edgeWeightStats.getAverage() ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.edgeWeightStats.getSigma() ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.edgeWeightStats.minimum ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.edgeWeightStats.maximum ,decPoints)));
        //
        infoBox.add(new JLabel("Edge Value"));
        infoBox.add(new JLabel(TruncDecString(islnet.edgeValueStats.getAverage() ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.edgeValueStats.getSigma() ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.edgeValueStats.minimum ,decPoints)));
        infoBox.add(new JLabel(TruncDecString(islnet.edgeValueStats.maximum ,decPoints)));

        return infoBox;
 } // eo class get
 
  /*
  * Returns a Box with older style parameter information.
  *@parameter islandNetwork upon which information is required.
  *@parameter number of decimanl points to show
  *@return a Box ready to be added to panels etc.
  */
 private JPanel get2(islandNetwork islnet, int decPoints )
 {
        infoBox.setLayout( new GridLayout(3,2));
        infoBox.add(new JLabel(" Maximum Site Weight  "+TruncDec(islnet.maxSiteWeight,decPoints)));
        //maxSiteWeight is the largest vertex weight, 
        infoBox.add(new JLabel(" Maximum Edge Weight  "+TruncDec(islnet.allmaxedgeweight ,decPoints)));
        infoBox.add(new JLabel(" Max. Site Out Strength  "+TruncDec(islnet.maxOutSiteStrength,decPoints)));
        infoBox.add(new JLabel(" Total Vertex Value  "+TruncDec(islnet.totVertexValue,decPoints)));
        infoBox.add(new JLabel("   Total Edge Value  "+TruncDec(islnet.totEdgeValue,decPoints)));
        infoBox.add(new JLabel(" Total Vertex Weight "+TruncDec(islnet.totVertexWeight,decPoints)));
        infoBox.add(new JLabel("   Total Edge Weight "+TruncDec(islnet.totEdgeWeight,decPoints)));  
        infoBox.add(new JLabel("   Site Window Mode "+TruncDec(islnet.siteWindowMode,decPoints)));  
        return infoBox;
 } // eo class get2

}// eo class OutputMeasuresPanel

        
//        frame.getContentPane().add(infoBox, BorderLayout.SOUTH);

// **********************************************************************************
 /*
  * class PictureWindow.
 */
public class PictureWindow {
 
        islandNetwork islnet;
        int windowSize=500;
        
// Constructor
 public PictureWindow(islandNetwork islnetinput)
 {
        //System.out.println("PictureWindow constructor, islnetinput.inputnameroot " + islnetinput.inputnameroot);
        islnet = new islandNetwork(islnetinput);
        //createAndShowNetWin(); 
        infoPrint(2,"Finished PictureWindow constructor, islnet.inputnameroot " + islnet.inputnameroot);
        //System.out.println("Finished PictureWindow constructor, islnet.iNVERSION " + islnet.iNVERSION);
        
 }// eo constructor PictureWindow(int ns, islandNetwork islnet)


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
           double windowScale;
           
           

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
         
         public void paint (Graphics ginput)
         {   
             Graphics2D g = (Graphics2D) ginput;
             int borderSize =30;
             int [] sitePositionX = new int[numberSites];
             int [] sitePositionY = new int[numberSites];
             Dimension d = getSize();
             double wsx = (d.width -borderSize-borderSize) / (maxX-minX);
             double wsy = (d.height -borderSize-borderSize)/(maxY-minY);
             if (wsx < wsy) windowScale = wsx; else windowScale = wsy;
             if (windowScale<0) windowScale =1.0;
             // if (islnet.infolevel>2) System.out.println("Window Scale "+ windowScale);
             //if (islnet.infolevel>2) System.out.println("Window Site i, Size, Coordinates X,Y ");
             
             int numSites =islnet.numberSites;
                 for (int i=0; i<numSites; i++)
             { 
                 sitePositionX[i] = (int)((islnet.siteArray[i].X-islnet.minX)*windowScale);
                 sitePositionY[i] = (int)((islnet.siteArray[i].Y-islnet.minY)*windowScale);
             }
             
             
             g.setColor(Color.black);
             //double ew;
             double vw=1.0; // Default value for PPA mode
             double fracEdgeSize;
             double ec;
             int width;
             int greyness;
             int x1,y1,x2,y2;
             for (int i=0; i<numSites; i++)
             {
               if (updateMode>0)  vw=islnet.siteArray[i].getWeight();
               else vw=1;
               for (int j=0; j<numSites; j++)
               {  
                  if (i==j) continue;
                  ec=islnet.edgeColour[i][j];
                  fracEdgeSize=ec/numberColours;                  
                  width = (int)(0.5+islnet.edgeWidthFactor*fracEdgeSize);
                  if ((ec>0) && (width<1)) width=1;
                  if (width>0) 
                  { 
                      greyness = 255-(int)(0.5+(fracEdgeSize*255.0)) ;
                      if (greyness<0) greyness=0;
                      if (greyness>255) greyness=255;
                      g.setColor(new Color(greyness, greyness, greyness));
                          //(greyness, greyness, greyness);
                      BasicStroke bstroke = new BasicStroke((float) width);
                      g.setStroke(bstroke);
                      x1 = sitePositionX[i];
                      y1 = sitePositionY[i];
                      x2 = (sitePositionX[i]+sitePositionX[j])/2;
                      y2 = (sitePositionY[i]+sitePositionY[j])/2;
                      g.drawLine( borderSize + x1, borderSize + y1, 
                                  borderSize + x2, borderSize + y2);
                  }

               } //eo for j
                  
            }// eo for i
            
             int size=20;
             int maxSiteShade =128;
             for (int i=0; i<numSites; i++)
             { 
              double fracsize=  islnet.siteArray[i].displaySize/islnet.numberColours ;  
              int displayShade = 255-(int)(0.5+ maxSiteShade*fracsize) ;
              size = (int) (islnet.siteWeightFactor*Math.sqrt(fracsize)+0.5);
              int zerositeWeight = islnet.siteWeightFactor;
              Color vertexColour;
              switch (DisplayVertexType)
              {
                  case 2: int vc = islnet.numberColours-islnet.siteArray[islnet.siteArray[i].influence].influenceRank;
                          if (vc<0) vc=0;
                          vertexColour = javaColour[vc];
                          break;
                  case 0: vertexColour = new Color(displayShade,0,0); break;
                  case 1: vertexColour = new Color(0,0,displayShade);  break;
                  default: vertexColour = Color.green;
              };
              g.setColor(vertexColour);
              if (size>0) g.fillOval( borderSize + sitePositionX[i] - size/2, borderSize + sitePositionY[i] - size/2, size, size);
              else g.drawOval(borderSize + sitePositionX[i] - zerositeWeight/2, borderSize + sitePositionY[i]- zerositeWeight/2, zerositeWeight, zerositeWeight);
              
              //g.setXORMode(Color.red); // make sure can see writing
              g.setColor(Color.black);
              String siteLabel="";
              switch (DisplayVertexType)
              {
                  case 2: siteLabel=islnet.siteArray[islnet.siteArray[i].influence].shortName;
                          break;
                  case 0:
                  case 1:    
                  default: siteLabel=islnet.siteArray[i].shortName;
              };              
              g.drawString(siteLabel,borderSize + sitePositionX[i], borderSize + sitePositionY[i]);
              g.setPaintMode(); //return to overwrite mode
              
              if (islnet.infolevel>2)
                  System.out.println(i+" "+size+" "+sitePositionX[i]+" "+ sitePositionY[i] );
              //siteArray[i].name
              //siteArray[i].displaySize
              }

         }// eo paint
     } //eo private class NetworkPicture extends JPanel
     
     
// --- createAndShowNetWin     
        
     private void createAndShowNetWin() {
        //Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        if (infolevel>-1) System.out.println(" Ariadne Network Display "+windowSize);
        JFrame frame = new JFrame(islnet.inputnameroot+" Ariadne Network Display "+ islnet.iNVERSION);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //if (maxX-minX > maxY-minY) windowScale = windowSize/(maxX-minX);
        //else windowScale = windowSize/(maxY-minY);
           
        
        Component contents = createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);



        ParameterBox topBox = new ParameterBox();
        frame.getContentPane().add(topBox.get(islnet), BorderLayout.NORTH);


        
        OutputMeasuresPanel outputInfo = new OutputMeasuresPanel ();
        frame.getContentPane().add(outputInfo.get(islnet,2), BorderLayout.SOUTH);


        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }   // eo  private void createAndShowNetWin() 

// ---     drawNetworkWindow
     public void drawNetworkWindow() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //System.out.println("Creating Window");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowNetWin();
            }
        });    
        }//eo drawNetworkWindow()

} //eo private class PictureWindow 







/*
**********************************************************************************
 * class SitePlotWindow
 */
public class SiteWindow extends JPanel {

        islandNetwork islnet;
        int windowSize=500;
        //int siteWindowMode=1;

// Constructor
 public SiteWindow(islandNetwork islnetinput)
 {
        //System.out.println("SiteWindow constructor, islnetinput.inputnameroot " + islnetinput.inputnameroot);
        islnet = new islandNetwork(islnetinput);
        //createAndShowNetWin();
        infoPrint(2,"Finished SiteWindow constructor, islnet.inputnameroot " + islnet.inputnameroot);
        //System.out.println("Finished SiteWindow constructor, islnet.iNVERSION " + islnet.iNVERSION);
        
 }// eo constructor SiteWindow(int ns, islandNetwork islnet)


 public Component createComponents() {

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        SitePicture pane = new  SitePicture(windowSize,windowSize);
        pane.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        30, //bottom
                                        30) //right
                                        );

        return pane;
    }



 
     public class SitePicture extends JPanel{
           double windowScale;




         public SitePicture (){
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(400, 400));
             
         }

         public SitePicture (int xsize, int ysize){
             setOpaque(true);
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(xsize, ysize));
             
         }

         public void init()
         {
//             setForeground(Color.yellow);
//             setBackground(Color.cyan);
             setSize(new Dimension(800, 400));        

         }
         
         

         
         public void paint (Graphics ginput)
         {
             Graphics2D g = (Graphics2D) ginput;
             int borderSize =30;
             int [] sitePositionX = new int[numberSites];
             int [] sitePositionY = new int[numberSites];
             
            // find values for sites
             double dns = (double) islnet.numberSites;
             double siteWeightMax=-1.0;
             double siteRankMax=-1.0;
             double [] sV = new double[islnet.numberSites];
             for (int i=0; i<islnet.numberSites; i++)
             {
              if (siteWeightMax<islnet.siteArray[i].weight) siteWeightMax=islnet.siteArray[i].weight;
              if (siteRankMax<islnet.siteArray[i].ranking) siteRankMax=islnet.siteArray[i].ranking;
              }
             // set window scales
             Dimension d = getSize();
             double wsx = (d.height -borderSize-borderSize) / dns;
             double wsWeight = (d.width -borderSize-borderSize)/ siteWeightMax;
             double wsRank = (d.width -borderSize-borderSize)/ siteRankMax;
             infoPrint(2,"Site Window scales "+wsx+","+wsWeight+","+wsRank+", window = "+d.width+","+d.height);
             infoPrint(2," max site weight ="+siteWeightMax+" max site rank="+siteRankMax);
             Font f = g.getFont();
             infoPrint(2,"Site Window Mode "+islnet.siteWindowMode+", font "+f.getName()+" " +f.getSize()+" "+f.getStyle() );
             double vw=1.0;
             int width;
             int greyness;
             int x,y,w,h;
             String s;
             double numberBars = 2.0;
             width = (int) (wsx/(numberBars))-2;
             g.setColor(Color.red);
             g.drawString("SIZE",borderSize,borderSize);
             g.setColor(Color.blue);
             g.drawString("RANK",borderSize+d.width/2,borderSize);
             for (int iii=0; iii<numberSites; iii++)
             {
                 int i=iii;
                 switch (islnet.siteWindowMode)
                 {
                     case 3: i=islnet.siteAlphabeticalOrder[iii]; break;
                     case 2: i=islnet.siteRank.siteRankOrder[iii]; break;
                     case 1: i=islnet.siteWeightOrder[iii]; break;
                     case 0: 
                     default: i=iii;
                 }
                 
                  int ec = (int) (0.49999+islnet.siteArray[i].displaySize);
//                  width = (islnet.edgeWidthFactor*ec)/islnet.numberColours;
                  if (width<1) width=1;
//                  BasicStroke bstroke = new BasicStroke((float) width);
//                  g.setStroke(bstroke);
                  y = borderSize +( (int) (iii* wsx )) +1; // base for top coordinate
                  x = borderSize; // base for left coordinate
                  w = (int) (wsWeight*islnet.siteArray[i].weight); // width of bar
                  h = width; // height of bar
                  infoPrint(2,"Site "+i+" Rank "+siteArray[i].ranking+", Rank rank "+siteArray[i].rankingRank );
                  infoPrint(2,"Site "+i+" Rank "+islnet.siteArray[i].ranking+", Rank rank "+islnet.siteArray[i].rankingRank );
                  infoPrint(2,"Site "+i+" at ("+x+","+y+") width "+w+", height "+h+" ");
                  g.setColor(Color.red);
//                  g.drawLine( x1, y1, x2, y2);
                  g.fill(new Rectangle2D.Double( x,y,  w , h));                  
                  s = String.valueOf(TruncDec(islnet.siteArray[i].weight,3));
                  g.drawString(s,x+w,y+width/2);
                  // second bar
                  y += width; // base for top coordinate
                  w = (int) (wsRank*islnet.siteArray[i].ranking); // width of bar
                  g.setColor(Color.blue);
//                  g.drawLine( x1, y1, x2, y2);
                  infoPrint(2,"Site "+i+"="+" at ("+x+","+y+") width "+w+", height "+h+" ");
                  g.fill(new Rectangle2D.Double( x,y,  w , h));                  
                  s= String.valueOf(TruncDec(islnet.siteArray[i].ranking ,3));
                  g.drawString(s,x+w,y+width/2);
                  g.setColor(Color.black);
                  g.drawString(islnet.siteArray[i].name,x,y);
                  
            }// eo for i

             int size=20;


         }// eo paint
         
       
     } //eo private class SitePicture extends JPanel


// --- createAndShowNetWin

     private void createAndShowNetWin() {
        //Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        if (infolevel>-1) System.out.println(" Ariadne Site Display "+windowSize);
        JFrame frame = new JFrame(islnet.inputnameroot+" Ariadne Site Display "+ islnet.iNVERSION);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //if (maxX-minX > maxY-minY) windowScale = windowSize/(maxX-minX);
        //else windowScale = windowSize/(maxY-minY);


        Component contents = createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        ParameterBox topBox = new ParameterBox();
        frame.getContentPane().add(topBox.get(islnet), BorderLayout.NORTH);


//// Adds a panel with output information
//        int decPoints =2; // number of decimal points diplayed
////        Box infoBox = Box.createHorizontalBox();
//        JPanel infoBox = new JPanel();
//        infoBox.setLayout( new GridLayout(3,2));
//
//        infoBox.add(new JLabel(" Maximum Site Weight  "+TruncDec(islnet.maxSiteWeight,decPoints)));
//        //maxSiteWeight is the largest vertex weight, 
//        infoBox.add(new JLabel(" Maximum Edge Weight  "+TruncDec(islnet.allmaxedgeweight ,decPoints)));
//        infoBox.add(new JLabel(" Max. Site Out Strength  "+TruncDec(islnet.maxOutSiteStrength,decPoints)));
//        infoBox.add(new JLabel(" Total Vertex Value  "+TruncDec(islnet.totVertexValue,decPoints)));
//        infoBox.add(new JLabel("   Total Edge Value  "+TruncDec(islnet.totEdgeValue,decPoints)));
//        infoBox.add(new JLabel(" Total Vertex Weight "+TruncDec(islnet.totVertexWeight,decPoints)));
//        infoBox.add(new JLabel("   Total Edge Weight "+TruncDec(islnet.totEdgeWeight,decPoints)));  
//        infoBox.add(new JLabel("   Site Window Mode "+TruncDec(islnet.siteWindowMode,decPoints)));  
//        
//
//
//        
//        frame.getContentPane().add(infoBox, BorderLayout.SOUTH);
        OutputMeasuresPanel outputInfo = new OutputMeasuresPanel ();
        frame.getContentPane().add(outputInfo.get(islnet,2), BorderLayout.SOUTH);


        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }   // eo  private void createAndShowNetWin()

//    class RadioListener implements ActionListener
//    {

//    }
     
     
// ---     drawSiteWindow
     public void drawSiteWindow() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //System.out.println("Creating Window");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowNetWin();
            }
        });
        }//eo drawSiteWindow()

} //eo public class SiteWindow








        
/*  *******************************************************************************
 *
 *  InputWindow class
 *   
 *   Built up from Celcius Converter programme of SWING example web site  
 */
      
    public class InputWindow implements ActionListener {
    JFrame inputFrame;
    JPanel buttonPanel,inputPanel,displayPanel,modelNumberPanel, modePanel, DVTPanel;
    JTextField  fileOutputValue, runnameValue,
               muValue,jValue,kappaValue,lambdaValue,distScaleValue,reldistScaleValue,alphaValue,betaValue,bValue,gammaValue,
               updateModeValue, maxVertexValue, edgeModeValue, majorModelNumberValue, minorModelNumberValue;
    JLabel fileInputValue, fileInputMessage, fileOutputMessage, runnameMessage,
            muMessage,jMessage,kappaMessage,lambdaMessage,distScaleMessage,reldistScaleMessage,alphaMessage,betaMessage,bMessage,gammaMessage,
               updateModeMessage, maxVertexMessage, edgeModeMessage, modelNumberMessage;
    JLabel notInPPAlabel =  new JLabel("Not used in PPA", SwingConstants.RIGHT);
    JTextField sSFValue, eWFValue, mCFValue, zCFValue, DMVSValue, DMESValue, ipValue; 
    JLabel sSFMessage, eWFMessage, mCFMessage, zCFMessage, DMVSMessage, DMESMessage, DVTMessage, ipMessage;
    ButtonGroup updateModeGroup, updateDVTGroup;
    JRadioButton PPAMode,MCMode, InfluenceMode, RankMode, SizeMode;
// for site window    
           JLabel SWMMessage;
           JPanel SWMPanel;
           ButtonGroup SWMGroup;
           JRadioButton alphaMode, weightMode, rankMode;

    
    JLabel messageLabel, drawMessageLabel;
    JButton calcButton, drawButton;
    JCheckBox autonameCB;
    
    String resMessage="OK";
    
    public InputWindow() {
        //Create and set up the window.
        inputFrame = new JFrame("Inputs for "+inputnameroot+ " Ariadne Network"+ iNVERSION);
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setSize(new Dimension(400,400));

        //Create and set up the panel.
        buttonPanel = new JPanel(new GridLayout(1, 2));
        inputPanel = new JPanel(new GridLayout(15, 2));
        displayPanel = new JPanel(new GridLayout(12, 2));

        //Add the widgets.
        addWidgets();
        

        //Set the default button.
        inputFrame.getRootPane().setDefaultButton(calcButton);

        // Add buttons and info box
        calcButton = new JButton("CALCULATE");
        buttonPanel.add(calcButton);
        //Listen to events from the Calc button.
        calcButton.addActionListener(this);

        drawButton = new JButton("REDRAW");
        buttonPanel.add(drawButton);
        //Listen to events from the Draw button.
        drawButton.addActionListener(this);


        
        
        //Add the panel to the window.
        inputFrame.getContentPane().add(inputPanel, BorderLayout.WEST);        
        inputFrame.getContentPane().add(displayPanel, BorderLayout.EAST);
        inputFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        //Display the window.
        inputFrame.pack();
        inputFrame.setVisible(true);
    } // eo constructor InputWindow()

    /**
     * Create and add the widgets.
     */
    private void addWidgets() {
        //Create widgets.
        
 // Code fragment on how to make this active       
 //        DocumentListener myListener = ??;
 //    JTextField myArea = ??;
 //    myArea.getDocument().addDocumentListener(myListener);
        
        islandNetwork i = new islandNetwork();
                
        fileInputValue = new JLabel(inputnameroot, SwingConstants.CENTER);
        fileInputMessage = new JLabel("Input File Name Root ", SwingConstants.RIGHT);
        inputPanel.add(fileInputMessage);
        inputPanel.add(fileInputValue);
        
        // Buttons for processing mode
        updateModeGroup = new ButtonGroup();
        PPAMode = new JRadioButton("PPA Mode");
        PPAMode.setActionCommand("P");
        MCMode = new JRadioButton("MC Mode");
        MCMode.setActionCommand("M");
//        MCMode.setSelected(true);
        updateModeGroup.add(PPAMode);
        updateModeGroup.add(MCMode);
        modePanel = new JPanel(new GridLayout(1, 2));
        modePanel.add(PPAMode);
        modePanel.add(MCMode);
        inputPanel.add(modePanel);
        switch (updateMode)
        {
            case 0: PPAMode.setSelected(true);  break;
            case 1: 
            default: MCMode.setSelected(true);  
        }
//        if (command.equals("P")) updateMode=0;
//            if (command.equals("M")) updateMode=1;

        
        // Register a listener for the radio buttons.
        RadioListener modeListener = new RadioListener();
        PPAMode.addActionListener(modeListener);
        MCMode.addActionListener(modeListener);
        
        autonameCB= new JCheckBox("Automatic Output File Names");
        autonameCB.setSelected(true);
        inputPanel.add(autonameCB);
        
        
        String edgeModeInitial = " "+i.edgeMode;
        edgeModeValue = new JTextField(edgeModeInitial,4);
        edgeModeMessage = new JLabel("Maximum Edge Value ", SwingConstants.RIGHT);
        inputPanel.add(edgeModeMessage);
        inputPanel.add(edgeModeValue);
        
        String maxVertexInitial = " "+i.vertexMaximum;
        maxVertexValue = new JTextField(maxVertexInitial,4);
        maxVertexMessage = new JLabel("Max Vertex Value ", SwingConstants.RIGHT);
        inputPanel.add(maxVertexMessage);
        inputPanel.add(maxVertexValue);
        
        modelNumberPanel = new JPanel(new GridLayout(1, 3));;
        String majorModelNumberInitial = " "+i.majorModelNumber+" ";
        String minorModelNumberInitial = " "+i.minorModelNumber+" ";
        majorModelNumberValue = new JTextField(majorModelNumberInitial,1);
        minorModelNumberValue = new JTextField(minorModelNumberInitial,1);     
        modelNumberPanel.add(majorModelNumberValue);
        modelNumberPanel.add(new JLabel("."));
        modelNumberPanel.add(minorModelNumberValue); 
        modelNumberMessage = new JLabel("Model Number ", SwingConstants.RIGHT);
        inputPanel.add(modelNumberMessage);
        inputPanel.add( modelNumberPanel);
         
        fileOutputValue = new JTextField(outputnameroot.substring(0,15),16);
        fileOutputMessage = new JLabel("Output File Name Root ", SwingConstants.RIGHT);
        inputPanel.add(fileOutputMessage);
        inputPanel.add(fileOutputValue);
        
        runnameValue = new JTextField(runname,16);
        runnameMessage = new JLabel("Run Name ", SwingConstants.RIGHT);
        inputPanel.add(runnameMessage);
        inputPanel.add(runnameValue);
        
        String jInitial = " "+i.vertexSourceH;
        jValue = new JTextField(jInitial,4);
        jMessage = new JLabel("j ", SwingConstants.RIGHT);
        inputPanel.add(jMessage);
        //if (updateMode==0) inputPanel.add(notInPPAlabel); else 
        inputPanel.add(jValue);
        
        String muInitial = " "+i.edgeSourceH;
        muValue = new JTextField(muInitial,4);
        muMessage = new JLabel("mu ", SwingConstants.RIGHT);
        inputPanel.add(muMessage);
        inputPanel.add(muValue);
        
        String kappaInitial = " "+i.kappaH;
        kappaValue = new JTextField(kappaInitial,4);
        kappaMessage = new JLabel("kappa ", SwingConstants.RIGHT);
        inputPanel.add(kappaMessage);
        inputPanel.add(kappaValue);
        
        String lambdaInitial = " "+i.lambdaH;
        lambdaValue = new JTextField(lambdaInitial,4);
        lambdaMessage = new JLabel("lambda ", SwingConstants.RIGHT);
        inputPanel.add(lambdaMessage);
        inputPanel.add(lambdaValue);
        
        String distScaleInitial = " "+i.distScaleH;
        distScaleValue = new JTextField(distScaleInitial,4);
        distScaleMessage = new JLabel("Distance Scale ", SwingConstants.RIGHT);
        inputPanel.add(distScaleMessage);
        inputPanel.add(distScaleValue);
        
        String reldistScaleInitial = " "+i.shortDistScaleH;
        reldistScaleValue = new JTextField(reldistScaleInitial,4);
        reldistScaleMessage = new JLabel("Short Distance ", SwingConstants.RIGHT);
        inputPanel.add(reldistScaleMessage);
        inputPanel.add(reldistScaleValue);
        
        if (updateMode==0) i.betaH=3.0;
        else i.betaH=1.0;
        String betaInitial = " "+i.betaH;
        betaValue = new JTextField(betaInitial,4);
        betaMessage = new JLabel("beta ", SwingConstants.RIGHT);
        inputPanel.add(betaMessage);
        inputPanel.add(betaValue);
        
        String bInitial = " "+i.bH;
        bValue = new JTextField(bInitial,4);
        bMessage = new JLabel("b ", SwingConstants.RIGHT);
        inputPanel.add(bMessage);
        inputPanel.add(bValue);
        
        // Now list display vraiables
        displayPanel.add(new JLabel("<html><em>Display Variables</em>", SwingConstants.CENTER) );
        displayPanel.add(new JLabel("<html><em>Values</em>", SwingConstants.RIGHT) );
        
        String sSFInitial = " "+i.siteWeightFactor;
        sSFValue = new JTextField(sSFInitial,4);
        sSFMessage = new JLabel("<html>Maximum Site Size </html>", SwingConstants.RIGHT);
        displayPanel.add(sSFMessage);
        displayPanel.add(sSFValue);
        
        String eWFInitial = " "+i.edgeWidthFactor;
        eWFValue = new JTextField(eWFInitial,4);
        eWFMessage = new JLabel("<html>Maximum Edge Width </html>", SwingConstants.RIGHT);
        displayPanel.add(eWFMessage);
        displayPanel.add(eWFValue);
        
        String zCFInitial = " "+i.zeroColourFrac;
        zCFValue = new JTextField(zCFInitial,4);
        zCFMessage = new JLabel("<html>Fraction for Zero Colour </html>", SwingConstants.RIGHT);
        displayPanel.add(zCFMessage);
        displayPanel.add(zCFValue);
        
        String mCFInitial = " "+i.minColourFrac;
        mCFValue = new JTextField(mCFInitial,4);
        mCFMessage = new JLabel("<html>Fraction for Minimum Colour </html>", SwingConstants.RIGHT);
        displayPanel.add(mCFMessage);
        displayPanel.add(mCFValue);
        
        String DMVSInitial = " "+i.DisplayMaxVertexScale;
        DMVSValue = new JTextField(DMVSInitial,4);
        DMVSMessage = new JLabel("<html>Max. Vertex Size </html>", SwingConstants.RIGHT);
        displayPanel.add(DMVSMessage);
        displayPanel.add(DMVSValue);
        
        String DMESInitial = " "+i.DisplayMaxEdgeScale;
        DMESValue = new JTextField(DMESInitial,4);
        DMESMessage = new JLabel("<html>Max. Edge Size </html>", SwingConstants.RIGHT);
        //DMESMessage.setForeground(Color.CYAN);
        displayPanel.add(DMESMessage);
        displayPanel.add(DMESValue);

        String ipInitial = " "+i.influenceProb;
        ipValue = new JTextField(ipInitial,4);
        ipMessage = new JLabel("<html>Influence Prob.</html>", SwingConstants.RIGHT);
        //DMESMessage.setForeground(Color.CYAN);
        displayPanel.add(ipMessage);
        displayPanel.add(ipValue);
        
        // Buttons for DisplayVertexType
        updateDVTGroup = new ButtonGroup();
        SizeMode = new JRadioButton("Size");
        SizeMode.setActionCommand("NVSize");
        RankMode = new JRadioButton("Rank");
        RankMode.setActionCommand("NVRank");
//        RankMode.setSelected(true);
        InfluenceMode = new JRadioButton("Influence");
        InfluenceMode.setActionCommand("NVInfluence");
        updateDVTGroup.add(SizeMode);
        updateDVTGroup.add(RankMode);
        updateDVTGroup.add(InfluenceMode);
        DVTPanel = new JPanel(new GridLayout(1, 3));
        DVTPanel.add(SizeMode);
        DVTPanel.add(RankMode);
        DVTPanel.add(InfluenceMode);
        DVTMessage = new JLabel("<html>Network vertices by</html>", SwingConstants.RIGHT);
        displayPanel.add(DVTMessage);
        displayPanel.add(DVTPanel);
        // set set button to be equal to current value
        switch (DisplayVertexType)
        {
            case 2: InfluenceMode.setSelected(true);  break;
            case 1: RankMode.setSelected(true);  break;
            case 0: 
            default: SizeMode.setSelected(true);  
        }

        // Register a listener for the radio buttons.
        //RadioListener DVTmodeListener = new RadioListener();
        SizeMode.addActionListener(modeListener);
        RankMode.addActionListener(modeListener);
        InfluenceMode.addActionListener(modeListener);
        
        SWMGroup = new ButtonGroup();
        JRadioButton numMode = new JRadioButton("Num.");
        numMode.setActionCommand("SWNum");
        alphaMode = new JRadioButton("Alph.");
        alphaMode.setActionCommand("SWAlpha");
//        alphaMode.setSelected(true);
        weightMode = new JRadioButton("Weight");
        weightMode.setActionCommand("SWWeight");
        rankMode = new JRadioButton("Rank");
        rankMode.setActionCommand("SWRank");
        SWMGroup.add(numMode);
        SWMGroup.add(alphaMode);
        SWMGroup.add(weightMode);
        SWMGroup.add(rankMode);
        SWMPanel = new JPanel(new GridLayout(1, 4));
        SWMPanel.add(numMode);
        SWMPanel.add(alphaMode);
        SWMPanel.add(weightMode);
        SWMPanel.add(rankMode);
        // set set button to be equal to current value
        switch (siteWindowMode)
        {
            case 2: rankMode.setSelected(true);  break;
            case 1: weightMode.setSelected(true);  break;
            case 0: numMode.setSelected(true);  break;
            case 3: 
            default: alphaMode.setSelected(true);  
        }
//            if (command.equals("SWNum")) siteWindowMode = 0;
//            if (command.equals("SWAlpha")) siteWindowMode = 3;
//            if (command.equals("SWWeight")) siteWindowMode = 1;
//            if (command.equals("SWRank")) siteWindowMode = 2;


        SWMMessage = new JLabel("<html>Site Window by</html>", SwingConstants.RIGHT);
        displayPanel.add(SWMMessage);
        displayPanel.add(SWMPanel);
        // Register a listener for the radio buttons.
//        RadioListener SWMListener = new RadioListener();
        numMode.addActionListener(modeListener);
        alphaMode.addActionListener(modeListener);
        weightMode.addActionListener(modeListener);
        rankMode.addActionListener(modeListener);
//          alphaMode.addActionListener(this); //style based on Schmidt p747
//          weightMode.addActionListener(this);
//          rankMode.addActionListener(this);

        
        /*  majorModelNumber = 1;
             minorModelNumber = 0;
             infolevel = 0;
             updateMode = 1; // Sweep
             edgeModeBinary = false;
          */   
        //Add the widgets to the container.
        
//        messageLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//        jLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    } // eo addWidgets





    /** Listens to the radio buttons. */
    class RadioListener implements ActionListener 
    { 
        public void actionPerformed(ActionEvent e) 
        {
            String command = e.getActionCommand() ;
            if (command.equals("P")) updateMode=0;
            if (command.equals("M")) updateMode=1;
            if (command.equals("NVSize")) DisplayVertexType = 0;
            if (command.equals("NVRank")) DisplayVertexType = 1;
            if (command.equals("NVInfluence")) DisplayVertexType = 2; 
            if (command.equals("SWNum")) siteWindowMode = 0;
            if (command.equals("SWAlpha")) siteWindowMode = 3;
            if (command.equals("SWWeight")) siteWindowMode = 1;
            if (command.equals("SWRank")) siteWindowMode = 2;

            
        }
    }
    
    
    
    
    public void actionPerformed(ActionEvent event) {
        //Parse degrees Celsius as a double and convert to Fahrenheit.
        int res=0;
                
        try
        {
            String command = event.getActionCommand();
            
            inputnameroot = fileInputValue.getText();
            outputnameroot = fileOutputValue.getText();
            runname = runnameValue.getText();
            if (autonameCB.isSelected()) autoSetOutputFileName=true;
            else  autoSetOutputFileName=false;
            edgeMode = Double.parseDouble(edgeModeValue.getText());
            vertexMaximum = Double.parseDouble(maxVertexValue.getText());
            double mu = Double.parseDouble(muValue.getText());  
            edgeSourceH = mu;
            double j = Double.parseDouble(jValue.getText());  
            vertexSourceH = j;
            kappaH = Double.parseDouble(kappaValue.getText());  
            lambdaH = Double.parseDouble(lambdaValue.getText());  
            distScaleH = Double.parseDouble(distScaleValue.getText());  
            shortDistScaleH = Double.parseDouble(reldistScaleValue.getText());  
            betaH = Double.parseDouble(betaValue.getText());  
            bH = Double.parseDouble(bValue.getText());  
            double nmn = Double.parseDouble(minorModelNumberValue.getText());  
            double jmn = Double.parseDouble(majorModelNumberValue.getText());  
            minorModelNumber = (int) (nmn+0.5);
            majorModelNumber = (int) (jmn+0.5);
            siteWeightFactor = (int) (Double.parseDouble(sSFValue.getText()) +0.5); 
            edgeWidthFactor = (int) (Double.parseDouble(eWFValue.getText()) +0.5); 
            zeroColourFrac = Double.parseDouble(zCFValue.getText()); 
            minColourFrac = Double.parseDouble(mCFValue.getText()) ; 
            DisplayMaxVertexScale = Double.parseDouble(DMVSValue.getText()) ; 
            DisplayMaxEdgeScale = Double.parseDouble(DMESValue.getText()); 
            influenceProb= Double.parseDouble(ipValue.getText()); 
            
            setOutputFileName();
            if (command.equals("CALCULATE")) {
                
                Date date = new Date();
                if (infolevel>-1) System.out.println(date);
                if (infolevel>0) showFixedSiteVariables("#",3);
                if (infolevel>0) showDistanceValues("#",3);
                infoPrint(2,"Data reading OK, number of sites is "+numberSites);
                    
                    if (updateMode == 0) doPPA();
                    else doMC();
            }
            showNetwork("#", 3);
            
        } catch (Exception e) 
        {
         res=10;
         resMessage = "Input Window actionPerformed error: "+e;  
         infoPrint(-1,resMessage);
         infoMessageBox(-1,inputFrame, resMessage);         
         
        }
        
    } // eo public void actionPerformed(ActionEvent event)

   

}// eo public class InputWindow implements ActionListener 



// **********************************************************************
     /**
     * Create the GUI for InputWindow and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        
        initLookAndFeel();
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        InputWindow converter = new InputWindow();
    } //  eo createAndShowGUI

    
        public void drawInputWindow() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    } // eo drawInputWindow

        
        



// **********************************************************************        
        // Build up infile window from Celcius Converter programme of SWING example web site  
     
    public class InfileWindow implements ActionListener 
    { 
        //String  inputnameroot;
        JFrame infileFrame;
        Box contentBox;
        JPanel infilePanel,inputPanel,jigglePanel,buttonPanel;
        JTextField fileInputValue,jiggleScaleInputValue ;
        JLabel fileInputMessage,messageLabel,jiggleMessage;
        JButton calcButton, readButton; //drawButton; 
        String resMessage="OK";
    
    public InfileWindow() {
        // could add browse button and FileDialog box
        //Create and set up the window.
        //inputnameroot = name;
        infileFrame = new JFrame("Input File Name - Ariadne " + iNVERSION);
        infileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        infileFrame.setSize(new Dimension(800,800));

        contentBox = Box.createVerticalBox();
        // Can't get it to find the image
        //contentBox.add(new ariadnePicture(), SwingConstants.CENTER);
        contentBox.add(new JLabel("Input File Name Root ", SwingConstants.CENTER));
        fileInputValue = new JTextField(inputnameroot,16);
        contentBox.add(fileInputValue);
        
        jigglePanel = new JPanel(new GridLayout(1,2));
//        String jiggleInitial = Double.toString(jiggleSource);
        jigglePanel.add(new JLabel("jiggle Scale ", SwingConstants.CENTER));
        jiggleScaleInputValue = new JTextField(Double.toString(jiggleScale),4);
        jigglePanel.add(jiggleScaleInputValue );
        contentBox.add(jigglePanel);
        
        
        buttonPanel = new JPanel(new GridLayout(1,2));
        readButton = new JButton("READ");
        buttonPanel.add(readButton);
        //Listen to events from the Convert button.
        readButton.addActionListener(this);
        //Set the default button.
        infileFrame.getRootPane().setDefaultButton(readButton);

//        calcButton = new JButton("CALC");
//        buttonPanel.add(calcButton);
//        //Listen to events from the Convert button.
//        calcButton.addActionListener(this);
//
        contentBox.add(buttonPanel);


        infileFrame.getContentPane().add(contentBox);
                
        //Display the window.
        infileFrame.pack();
        infileFrame.setVisible(true);
    }


   
    public void actionPerformed(ActionEvent event) {
        //Parse degrees Celsius as a double and convert to Fahrenheit.
        int res=0;
                
        try
        {
            
            String command = event.getActionCommand();
            if (infolevel>2) System.out.println(command);
                
            if (command.equals("READ")) {
                //network = new islandNetwork();
                inputnameroot = fileInputValue.getText();
                jiggleScale = Double.parseDouble(jiggleScaleInputValue.getText()); 
                if (infolevel>2) System.out.println(inputnameroot);
                res = getSiteData();
                if (res<0) {
                    resMessage = "Simple Site Data reading failed - return code "+res+" trying full distance data read"; 
                    infoPrint(0,resMessage);
                    infoMessageBox(0,infileFrame, resMessage+", file "+inputnameroot);
                }
                else {
                    dataread =true;
                    showFixedSiteVariables("#",3);
                    showDistanceValues("#",3);
                    resMessage="Data reading OK,  number of sites is "+numberSites;
                    infoPrint(0,resMessage);
                    infoMessageBox(-1,infileFrame, resMessage+", file "+inputnameroot);
                    if (jiggleScale>0.0) jiggleSiteData(distScaleH*jiggleScale);
                    //drawInputWindow();
                }
                siteAlphabeticalOrder = new int [numberSites];
                calcSiteOrder(1, siteAlphabeticalOrder, numberSites);
                InputWindow iw = new InputWindow();
            }
        } catch (Exception e) 
        {
         res=10;
         dataread=false;
         resMessage="Ariadne Error: "+e;   
         infoPrint(-1,resMessage);
         infoMessageBox(-1,infileFrame, resMessage+", file "+inputnameroot);
                    
        }
        
    } //eo public void actionPerformed(ActionEvent event)


     private class ariadnePicture extends JPanel 
     {
//      Image img = Toolkit.getDefaultToolkit().getImage(URL or file path);
        Image img;

             public ariadnePicture()
              {
                  setMinimumSize(new Dimension(100,100)); //don't hog space
                  setPreferredSize(new Dimension(400, 400));
              }
             
              public void init() {
                  img = Toolkit.getDefaultToolkit().getImage("/ariadneanddionysos.jpg");
              }

              public void paint(Graphics g)
              {
                  g.drawImage(img,0,0,this);
              }
     }// eo private class ariadnePicture 

    } // eo InFileWindow   
    
/* Run InfileWindow as GUI
 *
 */
public void runGUI()
{
    InfileWindow ifw = new InfileWindow();
}
 
    


/* *******************************************************************************
    File and directory routines  
*/

// ----------------------------------------------------------------------

/**
 *  test directory
 */
    public String testDirectory(String dirname) 
    {
        String message="";
        File dir = new File(dirname);
        if (!dir.isDirectory())
        { 
         message = dirname+" not a directory";
         System.out.println(message);
         }
        else if (infolevel>-1) System.out.println("Looking at directory "+dirname);
        return (message);
    }


/**
 *  Filter to find only one type of file, set up filelist (names without extension)
 *  See Schildt p544
 */
    public String getFileList(String ext, String dirname, String [] filenamelist ) 
    {
            // next part Schildt p544
        String message ="";
        File dir = new File(dirname);
        if (!dir.isDirectory())
        { 
         message = dirname+" not a directory";
         System.out.println(message);
         return (message);
        }
        if (infolevel>-1) System.out.println("Looking at directory "+dirname);
        FilenameFilter only = new OnlyExtensionSet(ext);
        String [] filelist = dir.list(only);
        message = "Found  "+filelist.length+" files with extension "+ext+" in directory "+dirname;
        if (infolevel>-1) System.out.println(message);

        filelist = new String[filelist.length];
        for (int i =0; i<filelist.length; i++)
        {   filenamelist[i] =  getFileNameRoot(filelist[i], ext);
            if (infolevel>-1) System.out.println(filelist[i]+"\t "+filenamelist[i]);
            
        };
        return (message);
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
     * Gives change in Hamiltonian if one edge value is changed
     * @param i from site
     * @param j to site
     * @param double new value for edge from i to j
     * 
     */
    public double deltaEdgeHamiltonian(int i, int j, double newValue) 
    {
        double dH=0;
        switch (majorModelNumber) 
        { 
         case 4:  // Model 4; uses alphaH to set critical value of degree
         {
           double de=(newValue-edgeValue[i][j]);
           dH = edgeSourceH*siteArray[i].getWeight() *de ;
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
             dH= edgeSourceH
                     * ( mMN0 ? siteArray[i].getWeight() : 1 )
                     * ( mMN1 ? siteArray[j].getWeight() : 1 )
                     * (newValue-edgeValue[i][j]) ;
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
           double deltaci = ((newValue-edgeValue[i][j])*siteArray[i].value* consumptioncoeffH);
           double cj=calcConsumption(j);
           double oi = calcOutput(siteArray[i].value,siteArray[i].size);
           double oj = calcOutput(siteArray[j].value,siteArray[j].size);
           double dvj = siteArray[j].getWeight()*(oj-cj);
           
           dH = -(oi-ci) * dvj * 
                    edgePotential1(distValue[i][j])* siteArray[i].value*edgeValue[i][j];
           dH+=  (oi- (ci+ deltaci ) ) * dvj 
                  * edgePotential1(distValue[i][j])* siteArray[i].value*newValue;
           
           dH+=   kappaH*deltaci + edgeSourceH*siteArray[i].getWeight() *(newValue-edgeValue[i][j]);
           break;
         }   
         
            case 5:   // Model 5
            case 1:   // Model 1          
         { 
           dH= ( ( mMN0 ? (siteArray[i].getWeight()) : 1 )
                 * ( mMN1 ? siteArray[j].getWeight() : 1 )
                 * edgePotential1(distValue[i][j]) * ( - newValue  + edgeValue[i][j] )
                + edgeSourceH*siteArray[i].getWeight()*(newValue-edgeValue[i][j])) ;
           break;
         }//eo model 1 and 5
            default: break;
        }//eo switch
        return (dH);
    }

    
    /**
     * Gives the potential between two site variables
     * @param double variable value of vertex 
     * @param double fixed site size 
     * @return the output of vertex i
     * 
     */
    public double calcOutput(double vertexValue, double siteSize) 
    {  return( (1+outputcoeffH) * siteSize*
               (1- outputcoeffH/(outputcoeffH+vertexValue)) );
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
        for (int j=0; j<numberSites; j++) w+=edgeValue[i][j]; 
        return( (siteArray[i].value+w)*siteArray[i].size* consumptioncoeffH ) ;
    };
    
    
    /**
     * Gives the potential between two site variables.
     * @param the distance 
     * @param returns the edge potential * lambda for model 1
     */
    public double edgePotential1(double distance) 
    {
    return(  (distance<shortDistScaleH) ? 0 : lambdaH * Math.pow((Math.pow((distance/distScaleH), alphaH) +1.0 ), -gammaH) );
    }
 
    /**
     * Gives the total edge potential term for model 3.
     * @return total edge potential term for model 3.
     */
    public double edgePotentialTotal3() 
    {
        double c;
        double potl=0.0;
        doDijkstra();
        for (int i=0; i<numberSites; i++)
        {
            c=( mMN0 ? siteArray[i].getWeight() : 1 );
            for (int j=0; j<numberSites; j++)
            {
                if ((i==j) || (distance[i][j]==0) || (edgeValue[i][j]==0)) continue;
                potl+= c* ( mMN2 ? distance[i][j] : edgePotential1(distance[i][j]) )
                * ( mMN1 ? siteArray[j].getWeight() : 1 );
            }
        }
        // distance[j][i] was used to put common factor out of loop.        
    return( potl );
    }

// --------------------------------------------------------------------
    /**
     * Gives change in Hamiltonian if one vertex value is changed.
     * Assumes Dijkstra values distance[][] already set.
     * @param i the index of the site to be changed.
     * @param newValue the new value for the vertex i.
     * @return change in Hamiltonian if tried new vertex value.
     */
 
     public double deltaVertexHamiltonian(int i, double newValue) 
    {   
//        double newbValue = (bEqualsOne ? newValue : Math.pow(newValue,bH));
        double dH=0;
        switch (majorModelNumber) 
        { 
         case 4: 
         {
           double dv = (newValue-siteArray[i].value);
        // pure vertex terms as model 1
            dH= dv*siteArray[i].size * vertexSourceH  
                   - vertexPotential1(siteArray[i].size,newValue)
                   + vertexPotential1(siteArray[i].size,siteArray[i].value);
            // ??? NO edge terms???
          }//eo model 4

            case 2: 
         {
           double ci=calcConsumption(i);
           double deltaci=(newValue-siteArray[i].value)*siteArray[i].size* consumptioncoeffH;
           double oi = calcOutput(siteArray[i].value,siteArray[i].size);
           double deltaoi = calcOutput(newValue,siteArray[i].size)-oi;
           dH = kappaH*(deltaci-deltaoi); 
           dH+= (newValue-siteArray[i].value) *siteArray[i].size * vertexSourceH  ;
           // edge/vertex terms now
           for (int j=0; j<numberSites; j++)
           {                 
             if (i==j) continue;
             dH+= (deltaoi-deltaci)
                  *(calcOutput(siteArray[j].value,siteArray[j].size)-calcConsumption(j))
                  *edgePotential1(distValue[i][j]) 
                  *( siteArray[i].value *edgeValue[i][j]  + siteArray[j].value *edgeValue[j][i] );
            } 
           break;
         }// eo case 2   
            case 5: // Model 5
         {
           double dv = (newValue-siteArray[i].value);
           double dw = dv*siteArray[i].size;
        // pure vertex terms first
            //double oneMinusB = 1-bH;
            dH= vertexSourceH *dw/(1-bH) ;
            dH+= - vertexPotential5(siteArray[i].size*newValue)
                      + vertexPotential5(siteArray[i].getWeight());        
            
        // edge/vertex terms now
            for (int j=0; j<numberSites; j++) if (i !=j)  
            {
               // change is site i as the source (from) site, j as the target (to) site
               if ( mMN0  ) dH+= 
                            -   dw
                              * edgePotential1(distValue[i][j])* edgeValue[i][j]
                              * ( mMN1 ? siteArray[j].getWeight() : 1 );
               // now site i is target (to) site and j is source (from) site
                if ( mMN1  ) dH+= 
                            - ( mMN0 ? siteArray[j].getWeight() : 1 )
                            * edgePotential1(distValue[j][i])* edgeValue[j][i]                
                            * dw ;
            }// eo for j if i!=j 
            break;
          }//eo model 5
            case 3:  // Model 3 must have dijkstra set
            case 1:  // Models 1, 3 and 5
         {
           double dv = (newValue-siteArray[i].value);
           double dw = dv*siteArray[i].size;
        // pure vertex terms first
            dH= vertexSourceH *dw ;
            dH+= - vertexPotential1(siteArray[i].size, newValue)
                      + vertexPotential1(siteArray[i].size, siteArray[i].value);
        // edge/vertex terms now
            for (int j=0; j<numberSites; j++) if (i !=j)  
            {
                dH+= edgeSourceH * dw* edgeValue[i][j];
               // change is site i as the source (from) site, j as the target (to) site
               if ( mMN0  ) dH+= 
                            - dw * ( mMN1 ? siteArray[j].getWeight() : 1 )
                            * edgePotential1(distValue[i][j])* edgeValue[i][j] ;
               // now site i is target (to) site and j is source (from) site
                if ( mMN1  ) dH+= 
                            - dw * ( mMN0 ? siteArray[j].getWeight() : 1 )
                            * edgePotential1(distValue[j][i])* edgeValue[j][i] ;                
            }// eo for j if i!=j 
            break;
          }//eo models 1 and 3.
            
            default:  // other model numbers no action
         }//eo switch             
         return  ( dH  ) ;
    }
    
    /**
     * Gives the pure site potential term for model 1.
     * @param double size fixed value of site
     * @param double value is the variable value of site
     * @return the pure site potential term for model 1.
     */
    public double vertexPotential1(double size, double value) {
    return(  kappaH*4.0*value*(1.0-value)*size );
    }
     
    /**
     * Gives the pure site potential term for model 5.
     * @param Land population of site 
     * @return the pure site potential term for model 1.
     */
    public double vertexPotential5(double landpopulation) {
    return(  kappaH*Math.pow(landpopulation,bH)/(1.0-bH) );
    }
    
    
// **********************************************************************
// MC routines    
    /**
     * Sweeps through all edges updating acording to Metropolis.
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
        
// .....................................................................
    
    /**
     * Sweeps through all vertices updating acording to Metropolis.
     * 
     */
    public void vertexSweep() {
        
        double dH, newVertexValue;
        int i,updateTried,updateMade;
        updateTried=0;
        updateMade=0;
        
        for (i=0; i<numberSites; i++){
            updateTried++;
            newVertexValue=  rnd.nextDouble()*vertexMaximum; //Math.random();
            dH = deltaVertexHamiltonian(i, newVertexValue);
//                System.out.println(dH+"  "+betaH+"  "+dH);
            if ((dH<0) || ( rnd.nextDouble() < Math.exp(-betaH*dH) ) ) 
                {
                    siteArray[i].value = newVertexValue;
                    updateMade++;
                };
        
        }// eo i
       vertexUR.update(updateTried,updateMade);           
    }
        
    
    
// ***********************************************************************    
    
    
    /**
     * Does PPA analysis on network.
     * See Broodbank page 180.
     */
    public void doPPA() {
        
        int numEdges = (int) (betaH+0.5);
        if (numEdges<0) numEdges = 3;
        int i,j,k,l,ef,ec;
        int[] edgeOrder = new int [numberSites+1]; 
        double [][] darr = new double[numberSites][numberSites];
        if (majorModelNumber==3) doDijkstra();
        initialiseMinorModel();
        for (i=0; i<numberSites; i++)
        {
            siteArray[i].value=1.0;
            siteArray[i].size=1.0;
            for (j=0; j<numberSites; j++)
            {
                edgeValue[i][j]=1.0;
                darr[i][j]= ( (majorModelNumber==3) ? distance[i][j] :distValue[i][j]);
                
            }
        }
        
        double dnc = ((double) numberColours);
        for (i=0; i<numberSites; i++)
        {
            ef=0;   
//          if (infolevel>1) System.out.println("From "+i+"-th site, "+siteArray[i].name);  
            for (j=0; j<numberSites; j++)
            {
                edgeValue[i][j]=0.0;
                if (i==j) continue;
                for (k = 0; k<ef; k++)
                {
                    if (darr[i][j]<darr[i][edgeOrder[k]]) break;
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
                    edgeValue[i][edgeOrder[k]]= (k<numberColours) ? ((double) ec)/dnc : 1.0/dnc ;// numberSites - k;
            };
        
        }// eo i
        
        // Now set site colours based on in degree
        int [] inDegree = new int[numberSites];
        int maxInDegree = 0;
        int ind;
        for (i=0; i<numberSites; i++)
        {
            ind=getInDegree(i);
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
        if (infolevel>=0) printNetworkStatistics("#",3);
        if (infolevel>=0) FileOutputNetworkStatistics("#", 3);
              
    }// eo doPPA
        

    
    /*
     * DoMC Does full Monte Carlo
     *
     */
    public void doMC()
    {
              int estot =10; 
              int vstot =estot*numberSites; 
              double betamin = betaH;
              double betamax = 1000000;
              double betafactor = 2;
              double updatefrac =0.0;
              double beta;
              int betainc =0;
              // initialise
              initialiseMinorModel();
              for (int i=0; i<numberSites; i++) siteArray[i].value = rnd.nextDouble()*vertexMaximum;
              for (int i=0; i<numberSites; i++){
                    for (int j=0; j<numberSites; j++){
                        if (i==j) edgeValue[i][j]=0.0;
                        else edgeValue[i][j] = edgeMode/2.0;
                    }//eo j
              }// eo i
              long initialtime = System.currentTimeMillis ();
              if (majorModelNumber ==3) doDijkstra(); // initialise distance[][]
              for (beta = betamin; beta<=betamax; beta=beta*betafactor)
              {  
                 vertexUR.reset();
                 for (int i=0; i<vstot; i++) 
                 {
                     betaH=beta;
                     vertexSweep();
 //                  System.out.println("Finished vertex sweep "+i);
                 }
                 
                 if (infolevel>1) System.out.println("b="+beta+" : ");
                 edgeUR.reset();
                 for (int i=0; i<estot; i++) 
                 {
                     betaH=beta;
                     edgeSweep();
 //                  System.out.println("Finished edge sweep "+i);
                 }
                 
                 //System.out.println("b="+beta+"  E: "+a.edgeUR.toString()+"  V: "+a.vertexUR.toString());
                 if (((++betainc)%10) ==0) 
                 {
                    printEllapsedTime(initialtime);
                    System.out.println(" b="+beta+"\n  E: "+edgeUR.toString()+"\n  V: "+vertexUR.toString());
                    
                    }
                 else System.out.print(".");
                 if ((vertexUR.totalfrac<0.001) && (edgeUR.totalfrac<0.01) ) break;
                 
              }
              System.out.println("\nFinal beta "+beta);
              System.out.println("  Edge stats: "+edgeUR.toString());
              System.out.println("Vertex stats: "+vertexUR.toString());
              System.out.println("**********************************************");
              for (int i=0; i<numberSites; i++) siteArray[i].setWeight();
              calcNetworkStats();
              if (infolevel>=0) printNetworkStatistics("#",3);
              if (infolevel>=0) FileOutputNetworkStatistics("#", 3);
              
         }//eo doMC
// *********************************************************
// *********************************************************
    /**
     * Gives the metric number
     * @return the metric number 
     * 
     */
//    public int getMetricNumber() 
//    {  
//        if ((minorModelNumber&4) >0) metricNumber=1;
//        return( metricNumber) ;
//    };

    /**
     * Gives the metric type as a String
     * @return the metric number 
     * 
     */
    public String getMetricString() 
    {  
        //int metricNumber=getMetricNumber();
        String s="unknown";
        switch (metricNumber)
        {
            case 0: s="plain distance"; break;
            case 1: s="sum of individual distance/potential using edge values";  break;
            case 2: s="inverse edge value"; break;
            case 3: s="sum of individual distance/potential using edge weights"; break;
            case 4: s="inverse edge weight"; break;
        
            default: s="unknown";
        }
        return(s) ;
    };

// ----------------------------------------------------------------------
    /**
     * Gives description string for the major model type.
     * @return the string with description of major model type.
     * 
     */
    public String getMajorModelString() 
    {  
        //int metricNumber=getMetricNumber();
        String s="unknown";
        switch (majorModelNumber)
        {
            case 1: s="Standard Hamiltonian";  break;
//            case 2: s="???"; break;
//            case 3: s="!!! Standard Hamiltonian with Island Hopping distances"; break;
//            case 4: s="???"; break;
            case 5: s="! Standard Hamiltonian with power site term and separate land/sea populations"; break;
        
            default: s="*** Major Model number"+majorModelNumber+"NOT OPERATIONAL";
        }
        return(s) ;
    };

// ----------------------------------------------------------------    
    /**
     * Sets boolean switches for Minor Model.
     * 
     */
    
     public void initialiseMinorModel()
     {
         mMN0 = ( (minorModelNumber & 1)>0 ?true:false);
         mMN1 = ( (minorModelNumber & 2)>0 ?true:false);
         mMN2 = ( (minorModelNumber & 4)>0 ?true:false);
     }
    

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
        switch (minorModelNumber&3)
        {
            case 0: s="no site terms in trade term";  break;
            case 1: s="Supply Side (source site in trade term)";  break;
            case 2: s="Demand Side (target site in trade term)"; break;
            case 3: s="Gravity (source and target site in trade term)"; break;
            }
        if (majorModelNumber ==3) switch (minorModelNumber&4)
        {
            case 0: s=s+"physical distance used in potential";  break;
            case 4: s=s+"edge potential used in potential";  break;
            }
        return(s) ;
    };

        
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
     * Gives the in degree of vertex
     * @param int i vertex number
     * @return the in degree of vertex i
     * 
     */
    public double getInWeight(int i) 
    {  
        double w=0;
        for (int j=0; j<numberSites; j++) w+=edgeValue[j][i]; 
        return( w ) ;
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
    /** Runs Dijkstra's algorithm on the network.
     * Does Dijkstra, updates distanceFromV global
     * Sets distance[i][j] to shortest distance from i to j 
     * using metricNumber to choose the for metric.
     * and DijkstraMaxDist is the longest path between any two sites,
     * and it equals MAXDISTANCE if disconnected
     */
//           *@param metricNumber = 0 plain distance, 
//                                = 1 distance divided by potential
//                                = 2 inverse edge strength        

    public void doDijkstra() 
    {
        double newdist,eee,eeew;
        DijkstraMaxDist=0;
            for (int v=0; v<numberSites; v++) 
            {// look at distances from vertex v
                int mdv=v; // vertex number with minimum distance from v
                double mindistance =MAXDISTANCE; // distance equivalent to infinity
                boolean [] notVisited = new boolean[numberSites];
                for (int i=0; i<numberSites; i++) 
                {
                    distance[v][i]=MAXDISTANCE; notVisited[i]=true;
                };
                distance[v][v]=0.0;
                for (int n=0; n<numberSites; n++) 
                {   // first find mdv, the unvisited vertex with smallest distance from v
                    mindistance = MAXDISTANCE;
                    for (int j=0; j<numberSites; j++)
                        if ( notVisited[j] && (distance[v][j]<mindistance) ) 
                        {
                         mindistance=distance[v][j]; 
                         mdv = j;
                        };
                        if (mindistance==MAXDISTANCE) break;    // must be finished
                        // visit mdv (fix its distance)  and update distance from v to j the neighbours of mdv
                        notVisited[mdv]=false;
                        for (int j=0; j<numberSites; j++) 
                        {
                            if (!notVisited[j]) continue;
                            eee=edgeValue[mdv][j];
                            if (eee==0) continue;
                            eeew=siteArray[mdv].getWeight() *eee  ;
                            try{ // this is the metric
                                newdist = MAXDISTANCE*1.0001;
                                switch (metricNumber) 
                                {
                                    case 4:
                                        newdist = mindistance+ 1.0 /  eeew  ;
                                        break;
                                    case 3:
                                        newdist = mindistance+ distValue[mdv][j] /(edgePotential1(distValue[mdv][j])* eeew ) ;
                                        break;
                                    case 2:
                                        newdist = mindistance+ 1.0 /  eee  ;
                                        break;
                                    case 1:
                                        newdist = mindistance+ distValue[mdv][j] /(edgePotential1(distValue[mdv][j])* eee ) ;
                                        break;
                                    case 0:
                                    default:
                                        newdist = mindistance+ distValue[mdv][j];
                                }
                                if (distance[v][j]>newdist) distance[v][j]=newdist;
                            } finally{};
                        }//eo for v                       
                }//eo for n
                if (DijkstraMaxDist<mindistance) DijkstraMaxDist=mindistance;
            } // eo for v
        //                        newdist = mindistance / (edgePotential1(distValue[mdv][j], eee ) );
                                                
//        if (metricNumber ==1){        
//            // now invert to get overall potential
//            if (DijkstraMaxDist>0) DijkstraMaxDist = 1.0/DijkstraMaxDist;
//            for (int i=0; i<numberSites; i++) 
//            { 
//                for (int j=0; j<numberSites; j++) if (distance[i][j]>0) distance[i][j]= 1.0 /distance[i][j];
//            }
//        };
        
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
/** produces a list of the rank (order) of a vector of values
     * @param valueNumber is number of entry in Site class to be ordered
     * @param orderVector is vector of integers [i] = no. of i-th value
     * @param n is number of entries to order
     */
    public void calcSiteOrder(int valueNumber, int [] orderVector, int n)
    {
        //int n = valueVector.length;
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
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public String getStartString(String s, int numberCharacters)
    {
      if (s.length()<numberCharacters) return(s);
        return ( s.substring(0, numberCharacters) );
    }
    
    /** Gives first numberCharacters of string s padded by spaces if necessary
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
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
 
    
public void infoPrint(int i,String s)
{
  if (infolevel>i) System.out.println(s);
}    

public void infoMessageBox(int i, JFrame frame, String s)
{
  if (infolevel>i) JOptionPane.showMessageDialog(frame, s);
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
