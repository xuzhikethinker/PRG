/*
 * NetworkGenerator.java
 *
 * Created on 15 March 2006, 17:46
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package IslandNetworks.run;

import IslandNetworks.islandNetwork;
import TimUtilities.StringAsBoolean;
import cern.jet.random.Normal;
import cern.jet.random.engine.DRand;
import java.io.*;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

/**
  * Generates site file with distances included for different types of artificial network.
  * <br> Types: 
  * <ul>
  * <li>0 (default) lattice (2D)</li>
  * <li>1 torus (2D)</li>
  * <li>2 line</li>
  * <li>3 torus (1D)</li>
  * <li>4 circle</li>
  * </ul>
  * {@value #mode.getNumber()Name}
  * <br>A torus means points at extreme ends are close but they are physically located
  * at largely separated places.  The 1D torus and circle differ as the circle
  * is embedded in a two dimensional space so the distances between points are different.
  * @author time
 */
public class NetworkGenerator {
    String SEP = "\t";
    //String dirname="/PRG/networks/aegean/output/";
    String dirname = java.lang.System.getProperty("user.dir");
    String nameroot="test";
    NetworkGeneratorMode mode;
//   /**
//     * Names of models available.
//     * <br> Types: 0 (default) lattice (2D); 1 torus (2D); 2 line; 3 torus (1D); 4 circle.
//     * {@value #mode.getNumber()Name}
//     * <br>A torus means points at extreme ends are close but they are physically located
//     * at largely separated places.  The 1D torus and circle differ as the circle
//     * is embedded in a two dimensional space so the distances between points are different.
//     */
//    final String [] mode.getNumber()Name = {"lattice", "torus2D", "line", "torus1D", "circle"};
//    final int NUMBERTYPES=mode.getNumber()Name.length;
    int numberSites=100;
    /**
     * Number of points to set per site
     */
    int communitySize=1;
    /**
     * Distance scale for separation of vertices
     */
    double scale=150;
//    /**
//     * Distance scale for separation of vertices within communities
//     */
//    double communityScale;
    /**
     * Scale for Poissonian random positioning around fixed scale
     */
    double jiggleScale=(scale/8.0);
    /**
     * Normal distribution generator.
     * @see cern.jet.random.Normal
     */
    Normal normalDistribution;
    double [] Xpos;
    double [] Ypos;
    double [][] dist;
    /**
     * Size of site
     */
    double [] siteSize;
    double siteSizeIncrease=2.0;
    NetworkGeneratorSiteMode siteSizeType; 
    boolean siteSizeRandomise=true;
    boolean siteSizeSpread=true;
        
    
    
    String [] name;
    String [] shortname;
    //int mode.getNumber()=0;
    double sizex=1.0;
    double sizey=1.0;
    
    /** Creates a new instance of NetworkGenerator. */
    public NetworkGenerator() 
    {
        mode = new NetworkGeneratorMode("lattice"); //"circle"
        siteSizeType = new NetworkGeneratorSiteMode();
    }

   /**
     * Creates a new instance of NetworkGenerator with no communities.
     * <br>Use island network to define quantities.
     *@param a island network to use to set parameters
     * @see NetworkGenerator
     */
    public NetworkGenerator(islandNetwork a){
        double siteSizeIncreaseInput=2.0;
        int siteSizeTypeInput=0;
        boolean siteSizeRandomiseInput=false;
        boolean siteSizeSpreadInput=false;
        create(a.getGenerateType(),a.getGenerateNumber(),a.getDistanceScale(),1,a.getJiggleScale(),
               siteSizeIncreaseInput, siteSizeTypeInput,siteSizeRandomiseInput, siteSizeSpreadInput);
    }

    /**
     * Creates a new instance of NetworkGenerator with no communities.
     * <br> Types: 0 (default) lattice (2D); 1 torus (2D); 2 line; 3 torus (1D); 4 circle.
     * {@value #mode.getNumber()Name}
     *@param inputType type of network to generate.
     *@param number number of sites
     *@param inputscale distance between nearest neighbours
     * @see NetworkGenerator
     */
    public NetworkGenerator(int inputType, int number,  double inputscale){
        double siteSizeIncreaseInput=2.0;
        int siteSizeTypeInput=0;
        boolean siteSizeRandomiseInput=false;
        boolean siteSizeSpreadInput=false;
        create(inputType, number,  inputscale,1,-1,
                siteSizeIncreaseInput, siteSizeTypeInput,siteSizeRandomiseInput, siteSizeSpreadInput);
    }
   /**
     * Creates a new instance of NetworkGenerator.
     * <br> Types: 0 (default) lattice (2D); 1 torus (2D); 2 line; 3 torus (1D); 4 circle.
     * {@value #mode.getNumber()Name}
     *@param inputType type of network to generate.
     *@param number number of sites
     *@param inputscale distance between nearest neighbours
     * @param inputcommunitySize number of vertices in each community
     * @param inputjigglescale if positive this is scale used to perturb positions
     * @see NetworkGenerator
     */
    public NetworkGenerator(int inputType, int number,
            double inputscale,
            int inputcommunitySize, double inputjigglescale,
            double siteSizeIncreaseInput,
        int siteSizeTypeInput, 
        boolean siteSizeRandomiseInput, 
        boolean siteSizeSpreadInput
        ){
        create(inputType, number,  inputscale, inputcommunitySize, inputjigglescale, 
                siteSizeIncreaseInput, siteSizeTypeInput, siteSizeRandomiseInput, siteSizeSpreadInput);
    }

    /**
     * Creates a site and distance file for artifical network.
     * <p>
     * @param args the command line arguments, see {@link IslandNetworks.run.NetworkGenerator.usage()}
     */
    public static void main(String[] args) {
        NetworkGenerator ng = new NetworkGenerator();
        ng.parse(args);
        ng.create();
        ng.FileOutputSite(6);
    }
    
        /** Parses input parameter string.
         * <br>null in array terminates processing.
         * <br>See usage for details of parameters.
         * <br>Must start with {@value TimUtilities.CommandLineParameterType#ARGUMENT}
         *@param ArgList array of strings containing -?<value>
         *@return any non zero number is an error.
         */
       public int parse(String[] ArgList){

                for (int i=0;i< ArgList.length ;i++){
                    if (ArgList[i]==null) break;
                    if (ArgList[i].length() <2) {
                        System.out.println("\n*** Argument "+i+", "+ArgList[i]+", is too short");
                        return 1;}
                        if (TimUtilities.CommandLineParameterType.isARGUMENT(ArgList[i])){
                            System.err.println("\n*** Argument "+i
                                    +", "+ArgList[i]
                                    +", does not start with "
                                    +TimUtilities.CommandLineParameterType.ARGUMENT);
                            return 2;}
                    try{
                            switch (ArgList[i].charAt(1)) {
                                case 'd': {this.scale = Double.parseDouble(ArgList[i].substring(2));
                                break;}
//                                case 'D': {this.communityScale = Double.parseDouble(ArgList[i].substring(2));
//                                break;}
                                case 'f': {this.nameroot = ArgList[i].substring(2);
                                break;}

                                case 'J': {this.jiggleScale = Double.parseDouble(ArgList[i].substring(2));
                                break;}

                                case 'n': {this.communitySize = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'N': {this.numberSites = Integer.parseInt(ArgList[i].substring(2));
                                break;}

                                case 's': {
                                    if (ArgList[i].charAt(2)=='i') {
                                        siteSizeIncrease = Double.parseDouble(ArgList[i].substring(3));
                                    }
                                    if (ArgList[i].charAt(2)=='t') {
                                        siteSizeType.setFromName(ArgList[i].substring(3));
                                    }
                                    if (ArgList[i].charAt(2)=='r') {
                                        siteSizeRandomise = StringAsBoolean.isTrue(ArgList[i].charAt(3));
                                    }
                                    if (ArgList[i].charAt(2)=='s') {
                                        siteSizeSpread = StringAsBoolean.isTrue(ArgList[i].charAt(3));
                                    }
                                break;}
                                case 't': {int t = Integer.parseInt(ArgList[i].substring(2));
                                     if (!mode.isValidNumber(t)) {
                                        throw new RuntimeException("Invalid network type "+t);
                                    }
                                     mode.set(t);
                                break;}
                                case 'T': {String t = ArgList[i].substring(2);
                                     if (!mode.isCurrentMode(t)) {
                                        throw new RuntimeException("Invalid network type "+t);
                                    }
                                     mode.setFromName(t);
                                break;}

                                case '?': {usage();
                                return 4;}

                                default:{
                                    System.out.println("\n*** Argument "+i+", "+ArgList[i]+", not known, usage:");
                                    usage();
                                    return 3;
                                }

                            }// eo switch
                    } catch (RuntimeException e){
                        System.err.println("*** Failed to interpret argument "
                                +i+" which was "+ArgList[i]
                                +", error "+e);
                    }
                }

                if ((communitySize>1) &&  (jiggleScale>0)){
                    System.out.println("Community on");
                }
           else {
                    communitySize=1;
                }
                return 0;
    }

        /**
         * Shows command line arguments on standard output (screen).
         */
            public void usage(){usage(System.out);}

    /**
     * Shows command line arguments.
     * @param PS PrintStream for output such as System.out
     */
      public void usage(PrintStream PS){
          final char a = TimUtilities.CommandLineParameterType.ARGUMENT;
          PS.println("********************************");
          PS.println("*** NetworkGenerator usage");
          PS.println("***  Command line arguments start with "+TimUtilities.CommandLineParameterType.ARGUMENT);
          PS.println("***  and are separated by space");
          PS.println("***  Arguments are as follows:-");
          PS.println(a+"d<double>   distance scale between vertices in different communities");
          //PS.println(a+"D<double>   distance scale between vertex communities ");
          PS.println(a+"f<String>   name root of files");
          PS.println(a+"j<double>   scale to move points by random amount (s.d. of Poisson)");
          PS.println(a+"n<int>      number of vertices per community (if 1 or less then no communities)");
          PS.println(a+"N<int>      number of vertices");
          PS.println(a+"si<double>  factor used to increase site sizes");
          PS.println(a+"st<int>     type of site size distribution to use");
          PS.println(a+"st<t|f>     randomise site sizes, true or false");
          PS.println(a+"ss<t|f>     spread site sizes, true or false");
          PS.println(a+"t<int>      type of network to generate using index number");
          PS.println(a+"T<String>   type of network to generate using short name");
          PS.println(  "             networks available are:-");
          mode.listAll(PS, "               ");
          PS.println("********************************");
    }

     /**
     * Creates a new instance of NetworkGenerator.
     * <br> Types: 0 (default) lattice (2D); torus (2D); line; circle.
     * <p>{@value #mode.getNumber()Name}
     *@param inputType type of network to generate.
     *@param number number of sites
     *@param inputscale distance between nearest neighbours
     * @param inputcommunitySize number of vertices in each community
     * @param inputjigglescale if positive this is scale used to perturb positions
     * @param siteSizeIncreaseInput sets scale for site size increases
     * @param siteSizeTypeInput selects type of site size distribution
     * @param siteSizeRandomiseInput true if want to randomise location of site sizes
     * @param siteSizeSpreadInput true if want to randomise location of site sizes
     * @see NetworkGenerator
     */
    public void create(int inputType, int number,  double inputscale,
            int inputcommunitySize, double inputjigglescale,
            double siteSizeIncreaseInput, int siteSizeTypeInput, 
            boolean siteSizeRandomiseInput, boolean siteSizeSpreadInput ){
        mode = new NetworkGeneratorMode(inputType);
        //if (!isValidType(mode.getNumber())) mode.getNumber()=0;
        scale=inputscale;
        numberSites=number;
        communitySize=inputcommunitySize;
        jiggleScale=inputjigglescale;
        siteSizeIncrease=siteSizeIncreaseInput;
        siteSizeType = new NetworkGeneratorSiteMode(siteSizeTypeInput);
        siteSizeRandomise=siteSizeRandomiseInput;
        siteSizeSpread = siteSizeSpreadInput;
        if (!isJiggleOn() && communitySize>1) {
                    throw new RuntimeException("Jiggle is off but have more than one community");
                }
        create();
    }
     /**
     * Creates a new instance of NetworkGenerator.
     * <br> Types: 0 (default) lattice (2D); torus (2D); line; circle.
     * <p>{@value #mode.getNumber()Name}
     * @see NetworkGenerator
     */
    public void create(){
        System.out.println("Generating type "+networkName()
                +", number of sites "+this.numberSites
                +", site size increase factor "+this.siteSizeIncrease
                +", site size type "+siteSizeType.toLongString()
                +", site sizes "+(siteSizeRandomise?"":"not ")+"randomised"
                +", nearest neighbour distance "+scale
                +", "+((communitySize>1) ?" community size "+communitySize:"no communities")
                +", "+(isJiggleOn()?" jiggle Scale "+jiggleScale:"no jiggle"));
        if (isJiggleOn()) {
            createNormalgenerator();
        }
        switch (mode.getNumber())
        {
            case 4: makeCircleSiteData(numberSites, scale);break;
            case 3:
            case 2: makeLineSiteData(numberSites, scale); break;
            case 1:
            case 0:
            default: makeLatticeSiteData(numberSites, scale);
        }
        calcDistanceData();
        makeSiteSizeData(siteSizeIncrease, siteSizeType.getNumber(), siteSizeRandomise, siteSizeSpread);

        
    }

    /**
     * Test to see if sites moved a random amount from their original site.
     * <p>true if jiggleScale is positive.
     * @return true (false) if jiggle is on (off)
     */
    public boolean isJiggleOn(){ return (this.jiggleScale>0);}
     /**
     * Test to see if sites moved a random amount from their original site.
     * <p>true if jiggleScale is positive.
     * @return true (false) if jiggle is on (off)
     */
    public String getJiggleString(){ return "jiggle "+ (isJiggleOn()?"on":"off");}
     /**
     * Returns string of jiggle distance.
      * <p>Empty string if jiggle is off..
     * @return true (false) if jiggle is on (off)
     */
    public String getJiggleValueString(){ return (isJiggleOn()?Double.toString(jiggleScale):"");}
    /**
     * Initialises normal random number generator.
     * <p>Mean is zero, jiggleScale is standardDeviation.
     * Returns null if jiggleScale is not positive.
     */
    public void createNormalgenerator(){
        //Date d = new Date();
        //RandomEngine rnd = new DRand(new Date());
        if (isJiggleOn()) normalDistribution = new Normal(0,this.jiggleScale,new DRand(new Date()));
        else normalDistribution = null;
    }

    /**
     * Returns next random number from normal distribution.
     * Mean is zero and standard deviation is {@link #jiggleScale}.
     * Returns zero if jiggle is off.
     * @return normal distribution of random number
     */
    public double getNextDistanceShift(){
        if (isJiggleOn()) return getNextDistanceShiftQuick();
        return 0;
    }
    /**
     * Returns next random number from normal distribution.
     * No tests to see if generator is defined.
     * @return normal distribution of random number
     */
    public double getNextDistanceShiftQuick(){
        return normalDistribution.nextDouble();}
    // ---------------------------------------------------------------------------------


    /**
     * Creates a square 2D lattice of sites.
     * <p>Lattice is square with the square root of the total number of
     * sites divided by communitySize (rounded to nearest integer) used to set size.
     * @param totalnumber total number of sites
     * @param scale distance between nearest neighbours
     */
    public void makeLatticeSiteData(int totalnumber, double scale)
    {
       int xnumber = (int) Math.round(Math.sqrt(((double) totalnumber) /((double) communitySize) ));
       makeLatticeSiteData(xnumber, xnumber, scale) ;
    }

    /**
     * Creates a 2D lattice of sites.
     * @param xnumber number of site communities in x direction
     * @param ynumber number of site communities in y direction
     * @param scale distance between nearest neighbours
     */
    public void makeLatticeSiteData(int xnumber, int ynumber, double scale)
    {
        sizex=scale*xnumber;
        sizey=scale*ynumber;
        int ns=xnumber*ynumber*communitySize;
        Xpos = new double[ns];
        Ypos = new double[ns];
        siteSize = new double[ns];
        name = new String[ns];
        shortname = new String[ns];
        
      nameroot=networkName()+"X"+xnumber+"Y"+ynumber+"D"+TruncDec(scale, 2);
      namerootAdditions();
//      if (communitySize>1) nameroot=nameroot+"_"+"C"+communitySize;
//      if (isJiggleOn()) nameroot=nameroot+"_"+"J"+TruncDec(jiggleScale, 2);
      numberSites =0;
      for (int x=0; x<xnumber; x++)
      {
        for (int y=0; y<ynumber; y++)
        {
          for (int c=0; c<communitySize; c++){
              name[numberSites] = "("+x+","+y+(communitySize>1?","+c:"")+")";
              shortname[numberSites] = Integer.toString(numberSites);
              Xpos[numberSites] = x*(scale+getNextDistanceShift());
              Ypos[numberSites] = y*(scale+getNextDistanceShift());
              numberSites++;
          }
        }// eo for y
      }//eo for x
    }

  
    /**
     * Add to name of file.
     * <p>Uses joggle and community size settings to make additions to name. 
     */
    private void namerootAdditions(){
      nameroot=nameroot+siteSizeType.toString();
      nameroot=nameroot+String.format("%05.3f",siteSizeIncrease);
      nameroot=nameroot+(siteSizeRandomise?"R":"f");
      nameroot=nameroot+(siteSizeSpread?"S":"f");
      if (communitySize>1) {
            nameroot=nameroot+"-"+"C"+communitySize;
        }
      if (isJiggleOn()) {
            nameroot=nameroot+"-"+"J"+TruncDec(jiggleScale, 2);
        }
    }
// ---------------------------------------------------------------------------------

    /** 
     * Creates a lattice of sites.
     * @param xnumber of site communities in x direction
     * @param scale distance between nearest neighbours
     */
    public void makeLineSiteData(int xnumber, double scale) 
    {
        sizex=scale*xnumber;
        int ns=xnumber;
        Xpos = new double[ns];
        Ypos = new double[ns];
        siteSize = new double[ns];
        name = new String[ns];
        shortname = new String[ns];
        
        nameroot=networkName()+"X"+xnumber+"D"+TruncDec(scale, 2);
        namerootAdditions();
//        if (communitySize>1) nameroot=nameroot+"_"+"C"+communitySize;
//        if (isJiggleOn()) nameroot=nameroot+"_"+"J"+TruncDec(jiggleScale, 2);

      numberSites =0;
      for (int x=0; x<xnumber; x++)
      {
       for (int c=0; c<communitySize; c++){
          name[numberSites] = "("+x+(communitySize>1?","+c:"")+")";
          shortname[numberSites] = Integer.toString(numberSites);
          Xpos[numberSites] = x*(scale+getNextDistanceShift());
          Ypos[numberSites] = scale/2.0;
          numberSites++;
          }//eo for c
      }//eo for x
    }

    // ---------------------------------------------------------------------------------

    /** 
     * Creates a circle of sites.
     * @param number of sites 
     * @param scale is distance between neighbouring sites.
     */
    public void makeCircleSiteData(int number, double scale) 
    {
        int ns=number;
        int numberCommunities = ns/communitySize;
        Xpos = new double[ns];
        Ypos = new double[ns];
        name = new String[ns];
        shortname = new String[ns];

        double theta;
        double radius;
        if (numberCommunities>2){
            theta = 2.0*Math.PI/((double) numberCommunities);
            radius = scale/(2*Math.sin(theta/2.0));
        }
        else {
            theta = Math.PI;
            radius = scale/2.0;
        }
        nameroot=networkName()+"N"+number+"D"+TruncDec(scale, 2);
//        if (isJiggleOn()) nameroot=nameroot+"J"+TruncDec(jiggleScale, 2)+"_";
        namerootAdditions();

        numberSites =0;
        double xc = -1;
        double yc = -1;
        for (int c=0; c<numberCommunities; c++)
        {
          xc = radius*Math.sin(theta*c);
          yc = radius*Math.cos(theta*c);
          for (int n=0; n<communitySize; n++){
              name[numberSites] = "("+c+(communitySize>1?","+n:"")+")";
              shortname[numberSites] = Integer.toString(numberSites);
              double xs = getNextDistanceShift();
              Xpos[numberSites] = xc + xs;
              Ypos[numberSites] = yc + getNextDistanceShift();
              numberSites++;
          }
        }//eo for x
    }

    
    // ---------------------------------------------------------------------------------

    /** 
     * Calculates the distances based on site values.
     * <p>Only the toroidal types use a non-euclidean form.
     */
    public void calcDistanceData() 
    {
        dist = new double[numberSites][numberSites];
      // Now calculates distances
      for (int i =0; i<numberSites; i++) 
       {        
            for (int j =0; j<numberSites; j++) 
            {
                switch (mode.getNumber())
                {
                    case 3:
                    case 1: dist[i][j]=torusDistance(Xpos[i],Ypos[i],Xpos[j],Ypos[j]); break;
                    default: dist[i][j]=euclideanDistance(Xpos[i],Ypos[i],Xpos[j],Ypos[j]);
                }
            }
       }
    }// eo  makeLatticeSiteData   


    /** 
     * Calculates the distances based on site values.
     *@param x1 first site x coordinate
     *@param y1 first site y coordinate
     *@param x2 second site x coordinate
     *@param y2 second site y coordinate
     */
    public double euclideanDistance(double x1,double y1, double x2, double y2) 
    {
        double dx = (x1-x2);
        double dy = (y1-y2);
        return Math.sqrt(dx*dx+dy*dy);
    }

    /** 
     * Calculates the distances based on site values for torus
     *@param x1 first site x coordinate
     *@param y1 first site y coordinate
     *@param x2 second site x coordinate
     *@param y2 second site y coordinate
     */
    public double torusDistance(double x1,double y1, double x2, double y2) 
    {
        double dx = Math.min(Math.abs(x1-x2), sizex - Math.abs((x1-x2)));
        double dy = Math.min(Math.abs(y1-y2), sizey - Math.abs((y1-y2)));
        return Math.sqrt(dx*dx+dy*dy);
    }
    
    // -------------------------------------------------------------------
    /**
     * Set site sizes.
     * The options set by <tt>type</tt> are
     * <ul>
     * <li>0: fixed, equal to <tt>sizeIncrease</tt>(default)</li>
     * <li>1: power law, see{@link IslandNetworks.run.NetworkGenerator#makeSiteSizeDataPowerLaw(double) }</li>
     * <li>2: binomial, see{@link IslandNetworks.run.NetworkGenerator#makeSiteSizeDataBinomial(double)  }</li>
     * </ul>
     * @param sizeIncrease sets scale for site size increases
     * @param type selects type of site size distribution
     * @param randomise true if want to randomise location of site sizes
     * @param spread spread excess population from one site to neighbours
     */
    public void makeSiteSizeData(double sizeIncrease, int type, boolean randomise, boolean spread){
        siteSize= new double[numberSites];
        switch (type){
            case 2: makeSiteSizeDataBinomial(sizeIncrease);break;
            case 1: makeSiteSizeDataPowerLaw(sizeIncrease);break;
            case 0: 
            default: makeSiteSizeDataFixed(1);
        }
        if (randomise) {
            randomiseSiteSizeData();
        }
        if (spread) {
            double maximumDifference=sizeIncrease*2;
            int numberNeighbours=4;
            spreadSiteSizes(maximumDifference, numberNeighbours);
        }
    }
//    public String getSiteSizeTypeDescription(){
//        switch (siteSizeType.){
//            case 2: return "binomial";
//            case 1: return "power law";
//            case 0: 
//            default: return "fixed";
//        }
//    }
    /**
     * Set all site sizes equal
     * @param size all site sizes set equal to this
     */
    public void makeSiteSizeDataFixed(double size){
        for (int i=0; i<numberSites;i++) {
            siteSize[i]=size;
        }
    }
    /**
     * Gives binomial distribution of site sizes.
     * Sets site size of site i to be <tt>sizeIncrease^b[i]</tt>
     * where <tt>b[i]</tt> is the number of ones in the binary representation of i.
     * @param sizeIncrease factor used to increase site sizes
     */
    public void makeSiteSizeDataBinomial(double sizeIncrease){
        for (int i=0; i<numberSites;i++) {
            siteSize[i]=Math.pow(sizeIncrease, countOnes(i));
        }
    }
    private static int countOnes(int i){
        int n=0;
        while (i!=0) {n+=((i&1)>0?1:0); i=i>>>1;}
        return n;
    }
    /**
     * Gives power law distribution of site sizes.
     * Sets site size of site i to be <tt>sizeIncrease^b[i]</tt>
     * where <tt>b[i]</tt> is the position of the least significant zero (0 is
     * zero is in least significant bit).
     * @param sizeIncrease factor used to increase site sizes
     */
    public void makeSiteSizeDataPowerLaw(double sizeIncrease){
        int power=1;
        for (int i=0; i<numberSites;i++) {
            int z=lowestZero(i);
            siteSize[i]=Math.pow(sizeIncrease, lowestZero(i));
        }
    }
    /**
     * Finds position of lowest zero.
     * @param i number whose lowest binary zero is required.
     * @return number of bit with lowest zero (0 is least significant bit)
     */
    private static int lowestZero(int i){
        int n=0;
        while ((i&1)>0) {i=i>>>1; n++;}
        return n;
    }
    
    /**
     * Used to sort nearest neighbours
     */
    protected class SitePair implements Comparable<SitePair> {
            int source;
            int target;
            double distance;
            public SitePair(int s, int t, double d){
                source=s; target=t;
                distance=((s==t)?0:d);
            }
        @Override
            public int compareTo (SitePair sp){
                if (distance<sp.distance) {
                    return -1;
                }
                if (distance>sp.distance) {
                    return 1;
                }
                return (0);
            }
    }
    /**
     * Gives power law distribution of site sizes.
     * Sets site size of site i to be <tt>sizeIncrease^b[i]</tt>
     * where <tt>b[i]</tt> is the position of the least significant zero (0 is
     * zero is in least significant bit).
     * @param sizeIncrease factor used to increase site sizes
     */
    public void spreadSiteSizes(double maximumDifference, int numberNeighbours){
        int [][] nn = new int[numberSites][numberNeighbours];
        for (int source=0; source<numberSites;source++) {
            TreeSet<SitePair> nnset=new TreeSet(); 
            for (int target=0; target<numberSites;target++) {
                if (target!=source) {
                SitePair sp = new SitePair(source,target,dist[source][target]);
                nnset.add(sp);
                }
            }
            Iterator<SitePair> nni= nnset.iterator();
            for (int j=0; j<numberNeighbours;j++) {
                nn[source][j++]=nni.next().target;
            }
        } 
        int maxSweep=numberSites;
        double delta=maximumDifference/4.0;
        boolean noChangesMade=true;
        int sweep;
        for (sweep=0; sweep<maxSweep; sweep++){
            delta=(1.0-(sweep/maxSweep)) * maximumDifference/(2.0*numberNeighbours);
            noChangesMade=true;
            for (int source=0; source<numberSites;source++) {
              if (0<spreadOneSite(source, delta, maximumDifference, numberNeighbours, nn)) {
                    noChangesMade=false;
                }
            }
            if (noChangesMade) {
                break;
            }
        }
        System.out.println("spreadSiteSizes made "+sweep+" sweeps (maximum "+maxSweep+"), on last one"+(noChangesMade?" no":"")+" changes made");
    }
    
    /**
     * Spread population from one site to nearest neighbours.
     * This spreads population of size delta from one site to a neighbour.
     * @param source site whose excess population is to be spread
     * @param delta size of population ot move, must be less than (maximumDifference/2); 
     * @param maximumDifference will only spread population to sites smaller by at least this amount
     * @param numberNeighbours number of neighbours
     * @param nn array of all sites and their neighbours in order, i.e.nn[s][r] is r-th nearest neighbour to site s
     * @return number of changes made
     */
    private int spreadOneSite(int source, double delta, double maximumDifference, int numberNeighbours, int [][] nn){
        if (delta>maximumDifference/2) {
            throw new RuntimeException("*** delta="+delta+
                                 " must be less than "+(maximumDifference/2));
        }
        int numberChanges=0;
        int t;
        for (int r=0; r<numberNeighbours; r++) {
            t = nn[source][r];
            if (siteSize[source]>siteSize[t]+maximumDifference) {
                siteSize[source]-=delta;
                siteSize[t]+=delta;
                numberChanges++;
            }
        }
        return numberChanges;
    }
    
        
  
    /**
     * Randomise existing site sizes.
     */
    public void randomiseSiteSizeData(){
        int j;
        double ss;
        for (int i=0; i<numberSites;i++) {
            j=i;
            while(j==i) {
                j=(int) (Math.random()*numberSites);
            }
            ss=siteSize[i];
            siteSize[i]=siteSize[j];
            siteSize[j]=ss;
        }
    }
    // -------------------------------------------------------------------
  /**
     * Outputs network in islandNetwork format.
     * <br> <emph>dirname</emph>+<emph>nameroot</emph>_input.dat general info
     * @param dec number of significant digits to show
     */
    public void FileOutputSite(int dec) 
    {
       
        String cc="# ";
        String newDirName= dirname;
        final String fs = java.lang.System.getProperty("file.separator");
        if (!dirname.endsWith(fs)) newDirName=dirname+fs;
        String filenamecomplete = newDirName +"output"+fs+nameroot + islandNetwork.inputFileEnding+".dat";
        int swidth = dec+1+(int) Math.round(Math.log10(scale*20));
        String formatString = "%"+swidth+"."+dec+"g";
        System.out.println("Attempting to write general information to "+ filenamecomplete);
        System.out.println(" with field width "+swidth+" and "+dec+" after decimal point "+formatString);
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            
            PS.println("# "+SEP+networkName()+SEP+"Scale"+SEP+scale);
            
            PS.print("Name");
            for (int i=0; i<numberSites;i++) PS.print(SEP+name[i]);
            PS.println();
            
            PS.print("ShortName");
            for (int i=0; i<numberSites;i++) PS.print(SEP+shortname[i]);
            PS.println();

            if (communitySize>1)
            {
                PS.print("Region");
                for (int i=0; i<numberSites;i++) PS.print(SEP+"C"+i/this.communitySize);
                PS.println();
            }

            PS.print("Number");
            for (int i=0; i<numberSites;i++) PS.print(SEP+i);
            PS.println();

            PS.print("Size");
            for (int i=0; i<numberSites;i++) PS.print(SEP+siteSize[i]);
            PS.println();
            
            PS.print("XPos");
            for (int i=0; i<numberSites;i++) PS.print(SEP+String.format(formatString,Xpos[i]));
            PS.println();
            
            PS.print("YPos");
            for (int i=0; i<numberSites;i++) PS.print(SEP+String.format(formatString,Ypos[i]));
            PS.println();
            
            PS.println("*Distances");    
            for (int i=0; i<numberSites;i++) 
            {
                PS.print(name[i]);
                for (int j=0; j<numberSites;j++) PS.print(SEP+String.format(formatString,dist[i][j]));
                PS.println();
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
        return;
    }//eo 
    
    
    
    /**
     * Returns name of type of network. 
     *@return string with name of network type.
     */
    public String networkName()
    {
        return networkName(this.mode.getNumber());
    }

    /**
     * Returns name of type of network. 
     *@param type network type number.
     *@return string with name of network type.
     */
    public String networkName(int type)
    {
        if (!mode.isValidNumber(type)) return "Unknown";
        return mode.toString();
    }

//    /**
//     * Test if valid network type number
//     * @param type integer network type index
//     * @return true (false) if type is (is not) valid index number
//     */
//    public boolean isValidType(int type){
//        return ((type>=0) || (type< this.mode.getNumber()Name.length) ) ;
//        }
         /**
          *Truncates a double to a given number of decimal places.
     * @param value has tractional part truncated
     * @param dec number of decimals to retain
     */
    public double TruncDec(double value, int dec)
    {
      double shift = Math.pow(10,dec);
      return ( ( (double) ((int) (value*shift+0.5)))/shift);
    }

}
    