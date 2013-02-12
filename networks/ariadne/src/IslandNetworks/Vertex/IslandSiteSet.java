/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Vertex;

import IslandNetworks.Edge.IslandEdge;
import IslandNetworks.IslandTransferMatrix;
import IslandNetworks.Edge.IslandEdgeSet;

import IslandNetworks.PajekColours;
import java.io.PrintStream;
import java.util.ArrayList;
//import java.util.Random;

import TimUtilities.NumbersToString;
import TimUtilities.Distances;
import TimUtilities.StatisticalQuantity;

//import TimUtilities.MinMaxDouble;
import java.util.Random;

/**
 *
 * @author time
 */
public class IslandSiteSet {
    private int numberSites;
    private IslandSite [] siteArray;
    
    public int [] siteAlphabeticalOrder; // = new int[numberSites];  // Sites in order of names [i] = no. of i-th site
    public int [] siteWeightOrder;  // points in order [i] = no. of i-throws biggest Size

        public StatisticalQuantity siteValueStats;
        public StatisticalQuantity siteWeightStats;
        public StatisticalQuantity siteStrengthOutStats;
        public StatisticalQuantity siteStrengthInStats;
//        double maxSiteValue =-1.0; Use siteValueStats.maximum
        public int maxSiteValueIndex =-1; 
//        double maxSiteWeight =-1; Use siteWeightStats.maximum
        public int maxSiteWeightIndex = -1;
//        double maxOutSiteStrength = -1; Use siteStrengthOutStats.maximum
        public int maxOutSiteStrengthIndex = -1;
//        double maxInSiteStrength = -1; siteStrengthOutStats.minimum
        public int maxInSiteStrengthIndex = -1;
  
//        public double totVertexWeight=-1;
//        public double totVertexValue=-1;
        
        


//    double minLat=+999;
//    double minLong=+999;
//    double maxLat=-999;
//    double maxLong=-999;
//    double scaleLatLong=-1;
        
        public double minX=-987;
        public double maxX=-987;
        public double minY=-987;
        public double maxY=-987;
        public double XYScale=-1;
        
        public double siteSizeNormalisation =-1;
        
        // Ranking
        public StatisticalQuantity siteRankStats;
        public StatisticalQuantity siteInfluenceStats;
        public StatisticalQuantity siteBetweennessStats;
        public StatisticalQuantity siteInfluenceprimeStats;
        public StatisticalQuantity siteBetweennessprimeStats;
        public StatisticalQuantity siteNBetweennessStats;
        public StatisticalQuantity siteNBetweennessprimeStats;
        
        /**
         * List of IslandSite variable indices corresponding to those with statistics found here.
         */
        static final int [] statisticsList = {
            IslandSite.valueIndex, 
            IslandSite.weightIndex, 
            IslandSite.strengthInIndex, 
            IslandSite.strengthOutIndex, 
            IslandSite.rankingIndex, 
            IslandSite.influenceIndex,
            IslandSite.betweennessIndex,
            IslandSite.influenceprimeIndex,
            IslandSite.betweennessprimeIndex,
            IslandSite.nbetweennessIndex,
            IslandSite.nbetweennessprimeIndex
        };
        
        // USED IN SITE tabbed pane but may be in stats quantities
        //public double siteInflMax=-1.0;
        //public double siteRankMax=-1.0;
        public double siteRankOverWeightMax=-1.0;
        public int [] siteRankOrder; // [i]= site ranked i-th by Rank
        /**
         * i'th entry gives number of site ranked i-th by Rank/Size
         */
        public int [] siteRankOverWeightOrder; // [i]= site ranked i-th by Rank/Size
        /**
         * i'th entry gives number of site ranked i-th by Incoming Influence Weight
         */
        public int [] siteInfluenceOrder;
        /**
         * i'th entry gives number of site ranked i-th by Betweenness Weight
         */
        public int [] siteBetweennessOrder;
        /**
         * i'th entry gives number of site ranked i-th by Incoming Influence Prime Weight
         */
        public int [] siteInfluenceprimeOrder;
        /**
         * i'th entry gives number of site ranked i-th by Betweenness Prime Weight
         */
        public int [] siteBetweennessprimeOrder;

        /**
         * i'th entry gives number of site ranked i-th by Newman Betweenness Weight
         */
        public int [] siteNBetweennessOrder;
        /**
         * i'th entry gives number of site ranked i-th by Newman Betweenness Prime Weight
         */
        public int [] siteNBetweennessprimeOrder;

        /**
         * Maximum display size.
         */
        public double maxDisplaySize=1;
        /**
         * Index of variable used to set display values.
         * If out of range (say zero) then 1.0 is used for all vertices.
         */
        int displayIndex=-1; 
              
        NumbersToString n2s = new NumbersToString(3);
        
        ArrayList<String> geneName;

        public IslandSiteSet(int ns){
            numberSites = ns;
            siteArray = new IslandSite[numberSites];
            for (int i=0; i<numberSites; i++) siteArray[i] = new IslandSite();
            initialiseSet(-1,-1,-1);
            setMinMaxXY();
            
            geneName = new ArrayList<String>();  // is size=0 then no genes


            
          //maybe delay this?  
          siteAlphabeticalOrder = new int [numberSites];
 //         calcAlphabeticalOrder();
          siteWeightOrder = new int [numberSites]; 
//          calcSiteWeightOrder();
          
           siteRankOrder = new int [numberSites]; // [i] = number of site of rank i by rank
           siteRankOverWeightOrder = new int [numberSites]; // [i] = number of site of rank i by rank/size
           siteInfluenceOrder = new int [numberSites]; //[i] = number of site ranked i by influenceInt
           siteBetweennessOrder = new int [numberSites]; //[i] = number of site ranked i by influenceInt
           siteInfluenceprimeOrder = new int [numberSites]; //[i] = number of site ranked i by influenceInt
           siteBetweennessprimeOrder = new int [numberSites]; //[i] = number of site ranked i by influenceInt
           siteNBetweennessOrder = new int [numberSites]; //[i] = number of site ranked i by influenceInt
           siteNBetweennessprimeOrder = new int [numberSites]; //[i] = number of site ranked i by influenceInt

        }
        
        
        /**
         * Deep copies an IslandSiteSet.
         * <p>NEEDS TO BE FINISHED
         * @param iniss input IslandSiteSet
         */
       
        public IslandSiteSet(IslandSiteSet iniss){
            numberSites = iniss.numberSites;
            siteArray = new IslandSite[numberSites];
            for (int i=0; i<numberSites; i++) siteArray[i] = new IslandSite(iniss.siteArray[i]); 
        if (iniss.siteAlphabeticalOrder != null) 
        {
            siteAlphabeticalOrder = new int[numberSites];  // Sites in order of names [i] = no. of i-th site
            System.arraycopy(iniss.siteAlphabeticalOrder, 0, siteAlphabeticalOrder, 0, numberSites);
        } 
        if (iniss.siteWeightOrder != null) 
        {
            siteWeightOrder = new int[numberSites];  // points in order [i] = no. of i-th biggest 
            System.arraycopy(iniss.siteWeightOrder, 0, siteWeightOrder, 0, numberSites);
        } 
        // do we need this?
        siteValueStats = new StatisticalQuantity(iniss.siteValueStats) ;
        siteWeightStats = new StatisticalQuantity(iniss.siteWeightStats)  ;
        siteStrengthOutStats = new StatisticalQuantity(iniss.siteStrengthOutStats ) ;
        siteStrengthInStats = new StatisticalQuantity (iniss.siteStrengthInStats);
        maxSiteValueIndex = iniss.maxSiteValueIndex;
        maxSiteWeightIndex = iniss.maxSiteWeightIndex ;
        maxOutSiteStrengthIndex = iniss.maxOutSiteStrengthIndex;               
//        totVertexWeight=iniss.totVertexWeight;
//        totVertexValue=iniss.totVertexValue;

            
        geneName = new ArrayList<String>(); 
        for (int i=0; i<iniss.geneName.size(); i++) geneName.add(iniss.geneName.get(i)); 
            
        
        minX=iniss.minX;
        maxX=iniss.maxX;
        minY=iniss.minY;
        maxY=iniss.maxY;
        XYScale=iniss.XYScale;
        siteSizeNormalisation=iniss.siteSizeNormalisation;

        //Ranking 
        siteRankStats = new StatisticalQuantity(iniss.siteRankStats) ;
        siteInfluenceStats = new StatisticalQuantity(iniss.siteInfluenceStats );
        siteBetweennessStats = new StatisticalQuantity(iniss.siteBetweennessStats );
        siteInfluenceprimeStats = new StatisticalQuantity(iniss.siteInfluenceprimeStats );
        siteBetweennessprimeStats = new StatisticalQuantity(iniss.siteBetweennessprimeStats);
        siteNBetweennessStats = new StatisticalQuantity(iniss.siteNBetweennessStats );
        siteNBetweennessprimeStats = new StatisticalQuantity(iniss.siteNBetweennessprimeStats);
      //siteInflMax=iniss.siteInflMax;
      //siteRankMax=iniss.siteRankMax;
         siteRankOverWeightMax= iniss.siteRankOverWeightMax;

         siteRankOrder = new int [numberSites];
        siteRankOverWeightOrder = new int [numberSites]; // [i] = number of site of rank i by rank/size
        siteInfluenceOrder  = new int [numberSites];
        siteBetweennessOrder  = new int [numberSites];
        siteInfluenceprimeOrder  = new int [numberSites];
        siteBetweennessprimeOrder  = new int [numberSites];
        siteNBetweennessOrder  = new int [numberSites];
        siteNBetweennessprimeOrder  = new int [numberSites];
        for (int i=0; i<numberSites; i++)
            {
                siteRankOrder[i]= iniss.siteRankOrder[i];
                siteRankOverWeightOrder[i] = iniss.siteRankOverWeightOrder[i];
                siteInfluenceOrder[i] = iniss.siteInfluenceOrder[i];
                siteBetweennessOrder[i] = iniss.siteBetweennessOrder[i];
                siteInfluenceprimeOrder[i] = iniss.siteInfluenceprimeOrder[i];
                siteBetweennessprimeOrder[i] = iniss.siteBetweennessprimeOrder[i];
                siteNBetweennessOrder[i] = iniss.siteNBetweennessOrder[i];
                siteNBetweennessprimeOrder[i] = iniss.siteNBetweennessprimeOrder[i];
            }//eo for i


            //TODO NEEDS TO BE FINISHED

        }
        
        
        public boolean isRegionSet(){return siteArray[0].isRegionSet();}
        public boolean isXYSet(){return siteArray[0].isXYSet();}
        public boolean isLatLongSet(){return siteArray[0].isLatLongSet();}
        public boolean isGenetic(){return ( (geneName.size()>0) ?true:false);}
        
    public boolean hasStatistics(int index) {
        for (int iii = 0; iii < statisticsList.length; iii++) {
            if (index == statisticsList[iii]) {
                return true;
            }
        }
        return false;
    }

        public int getNumberSites(){return numberSites;}
        public IslandSite [] getSiteArray(){return siteArray;} 
        public String getName(int s){return siteArray[s].name;}
        public String getRegion(int s){return siteArray[s].region;}
        public double getX(int i){return siteArray[i].getX();}
        public double getY(int i){return siteArray[i].getY();}
        public double getLatitude(int i){return siteArray[i].latitude;}
        public double getLongitude(int i){return siteArray[i].longitude;}
        /**
         * Gets site value, <tt>v_i</tt>.
         * <p>This is a variable quantity.
         * @param i index of site
         * @return value for site
         */
        public double getValue(int i){return siteArray[i].getValue();}
        /**
         * Gets site value, <tt>v_i</tt>.
         * <p>This is a variable quantity.
         * @param i index of site
         * @return value for site
         */
        public double getInValue(int i){return siteArray[i].getInValue();}
        /**
         * Gets site size, <tt>S_i</tt>.
         * <p>This is a fixed quantity.
         * @param i index of site
         * @return size for site
         */
        public double getSize(int i){return siteArray[i].size;}
        /**
         * Gets site in size, currently same as size <tt>S_i</tt>.
         * <p>This is a fixed quantity.
         * @param i index of site
         * @return size for site
         */
        public double getInSize(int i){return siteArray[i].size;}
        public double getWeight(int i){return siteArray[i].getWeight();}
        public double getInWeight(int i){return siteArray[i].getInWeight();}
        public double getStrength(int i){return siteArray[i].strength;}
        public double getStrengthIn(int i){return siteArray[i].strengthIn;}
        public double getStrengthOut(int i){return siteArray[i].strengthOut;}
        public double getDisplaySize(int s){return siteArray[s].displaySize;}
        public double getRanking(int s){return siteArray[s].ranking;}
        public int getRankingRank(int s){return siteArray[s].rankingRank;}
        public double getTotalInfluenceWeight(int i){return siteArray[i].influence;}
        public int getInfluenceRank(int s){return siteArray[s].influenceRank;}
        public double getBetweennessWeight(int i){return siteArray[i].betweenness;}
        public int getBetweennessRank(int s){return siteArray[s].betweennessRank;}
        public double getTotalInfluenceprimeWeight(int i){return siteArray[i].influenceprime;}
        public int getInfluenceprimeRank(int s){return siteArray[s].influenceprimeRank;}
        public double getBetweennessprimeWeight(int i){return siteArray[i].betweennessprime;}
        public int getBetweennessprimeRank(int s){return siteArray[s].betweennessprimeRank;}
 //       public ArrayList getGene(int s){return siteArray[s].getGene();}
        /**
         * Returns value of given variable for specified site.
         * @param i index of site
         * @param v index of variable
         * @return double value of variable v for site i
         */
        public double getVariable(int i, int v){return siteArray[i].getVariable(v);}
        /**
         * Returns value of given variable for specified site.
         * @param i index of site
         * @param s variable name
         * @return double value of variable name for site i
         */
        public double getVariable(int i, String s){return siteArray[i].getVariable(s);}
        
        
        /**
         * Sets site value, <tt>v_i</tt>.
         * <p>This is a variable quantity.
         * @param i index of site
         * @param v new value for site
         */
        public void setValue(int i, double v){siteArray[i].value=v;}
        /**
         * Sets site in value, <tt>u_i</tt>.
         * <p>This is a variable quantity.
         * @param i index of site
         * @param u new value for site
         */
        public void setInValue(int i, double u){siteArray[i].inValue=u;}
        /**
         * Sets site size, <tt>S_i</tt>.
         * <p>This is a fixed value of the site.
         * @param i index of site
         * @param v new value for site
         */
        public void setSize(int i, double v){siteArray[i].size=v;}
        public void setName(int s, String name){siteArray[s].setName(name);}
        private void setDisplaySize(int i, double v){siteArray[i].displaySize=v;}
        public void setXY(int i, double x, double y){siteArray[i].X=x;siteArray[i].Y=y;}
        
    /**
     * Sets variable vname of site i to be equal to value.
     * @param i index of site
     * @param vname name of variable
     * @param value new value of variable 
     */
        public void setVariable(int i, String vname, String value){siteArray[i].setVariable(vname, value);}
    /**
     * Sets variable vindex of site i to be equal to value.
     * @param i index of site
     * @param vindex index of variable
     * @param value new value of variable 
     */
        public void setVariable(int i, int vindex, String value){siteArray[i].setVariable(vindex, value);}
        
        
        public void setWeights(){for (int i=0; i<numberSites; i++) siteArray[i].setWeight();}

        
        /**
         * Initialise set using uniform values.
         * @param v value of all sites
         * @param S Size of all sites
         * @param ds display value of all sites
         */
        public void initialiseSet(double v,double S,double ds)
        {
            for (int i=0; i<numberSites; i++) {
            siteArray[i].value=v; //str[i]/maxstr;
            siteArray[i].size = S; //maxstr;
            siteArray[i].setWeight();
            siteArray[i].displaySize = ds;
        } 
        }
        
    /**
     * Adds the name of a gene and sets all sites to have silly value of this gene.
     * @param name name of gene
     * @return number of gene added
     */
    public int addGene(String name) {
        geneName.add(name);
        int g=geneName.size();
        for (int s = 0; s < numberSites; s++) siteArray[s].addGeneValue();
        return (g-1);
    }


    /**  Sets the values of given gene number from an array of input strings.
     *@param g Gene number to be set.
     *@param str array of strings containing values for each site for this gene.
     *@return 0 if OK, -n if error in column n (count from 1)
     */
    public int setGeneValues(int g, String [] str) {
        int r = 0;
        for (int s = 0; s < numberSites; s++) r=Math.min(r,setGeneValue(s, g, str[s]));
        return r;
    }
    /**  For one site sets the value  of given gene number from a string.
     *@param s site whose gene is to be set
     *@param g Gene number to be set.
     *@param str string containing value this gene.
     *@return 0 if OK, -n if error in column n (count from 1)
     */
    public int setGeneValue(int s, int g, String str) {
        int r = 0;
            try {
                siteArray[s].setGeneValue(g, str);
            } catch (RuntimeException e) {
                return -1 - s;
            }
        return r;
    }
    
    /**  Sets the lengths of all genes.
     */
    public void setGeneLengths() {
        for (int s = 0; s < numberSites; s++) siteArray[s].setGeneLength();
    }
    
     /**
     * Finds length of gene.
      * @param s site for which gene length is wanted
     * @return length of gene
     */
    public double getGeneLength(int s) {
        return siteArray[s].getGeneLength();
    }
     /**
     * Finds value of particular gene or given site.
      * @param s site for which gene length is wanted
      * @param g index of gene whose value is required.
     * @return value gene
     */
    public double getGeneValue(int s, int g) {
        return siteArray[s].getGeneValue(g);
    }
    
   /**  Gives the distance in gene space of the two sites.
     * @param s source site
     * @param t target site
     *@return 0 if OK, -n if error in column n (count from 1)
     */
    public double getGeneCorrelation(int s, int t){
        double dist =0;
        double norm=getGeneLength(s) * getGeneLength(t);
        if (norm<=1e-6) return 0;
        for (int g=0; g<geneName.size();g++) dist+=getGeneValue(s,g)*getGeneValue(t,g);
        return dist/norm;
    }
        

    
        
        /**  Sets the value of a variable from an array of input strings.
           *@param s Site number to be set.
           *@param v index of site variable being set.
           *@param str string of value for variable.
           *@return 0 if OK, -1 if error
         * @deprecated 
           */
           private int setValue(int s, int v , String str) {
            return siteArray[s].setVariable(v, str);
        }
        
        /**  Sets the value of a variable from an array of input strings.
           *@param v index of site variable being set.
           *@param str string of value for variable.
           *@return 0 if OK, -n if error in column n (count from 1)
           */
           public int setValues(int v , String [] str) {
               int r =0;
               for (int s=0; s<numberSites; s++) 
                   if (siteArray[s].setVariable(v,str[s]) <0) r=-1-s;
               return r;
           }

           
        /**  Sets all site values to be same.
           *@param v value to be given to all sites
           */
           public void setValues(double v) {
               for (int s=0; s<numberSites; s++) siteArray[s].setValue(v);
           }
        /**  Sets all site values to random values between 0 and <tt>scale</tt>
           *@param rnd Random class random generator
           *@param scale max value
           */
           public void setValuesRandom(Random rnd, double scale) {
               for (int s=0; s<numberSites; s++) siteArray[s].setValue(scale*rnd.nextDouble());
           }

        /**  Sets all site in values to random values between 0 and <tt>scale</tt>
           *@param rnd Random class random generator
           *@param scale max value
           */
           public void setInValuesRandom(Random rnd, double scale) {
               for (int s=0; s<numberSites; s++) siteArray[s].setInValue(scale*rnd.nextDouble());
           }

        /**  Sets all site in values to be same.
           *@param v value to be given to all sites
           */
           public void setInValues(double v) {
               for (int s=0; s<numberSites; s++) siteArray[s].setInValue(v);
           }

           
           
        
           /**
            * Sets the X Y coordinates to be in range [0.1,0.9].
            * <p>Scale is set using the largest difference 
            * between max and min values of lat or long.
            * <br>Preserves aspect ratio and sets max/min values for lat and long.
            * <br> (X=0,Y=0) is NW corner as latitude (longitude) is Y (X) as its angle north of equator (east of Greenwich meridian)
            * while java has zero in top left corner.
            */
           public void setXYFromLatLong() {
               for (int s=0; s<numberSites; s++) {
                   siteArray[s].Y = -siteArray[s].latitude;
                   siteArray[s].X = siteArray[s].longitude;
              }
               rescaleXY();
           }
        
           /**
            * Sets min and max values of X and Y coordinates
            */
           public void setMinMaxXY()
           {
               minX=siteArray[0].X;
        maxX=siteArray[0].X;
        minY=siteArray[0].Y;
        maxY=siteArray[0].Y;
        for (int i=1; i<numberSites; i++)
        {
            if (siteArray[i].X>maxX) maxX=siteArray[i].X;
            if (siteArray[i].X<minX) minX=siteArray[i].X;
            if (siteArray[i].Y>maxY) maxY=siteArray[i].Y;
            if (siteArray[i].Y<minY) minY=siteArray[i].Y;
        }
           }
           
       /** 
     * Rescale X Y locations to 0.1 to 0.9 scale.
        * <p>based on largest difference in current X-Y scale.
        * <br>Preserves aspect ratio and sets max/min values for X and Y.
     */
    public void rescaleXY() 
    {
        setMinMaxXY();
        double XOffset = (maxX+minX)/2.0;
        double YOffset = (maxY+minY)/2.0;
        double XScale = (maxX-minX)/0.8;
        double YScale = (maxY-minY)/0.8;
        XYScale = (XScale>YScale) ? XScale : YScale;
        for (int i=0; i<numberSites; i++)
        {
             siteArray[i].X = (( (siteArray[i].X-XOffset)/XYScale)+0.5);
             siteArray[i].Y = (( (siteArray[i].Y-YOffset)/XYScale)+0.5) ;
        }
        setMinMaxXY();
    }// eo rescaleXY
           

    
       /**
        * Normalise the site sizes.
        */    
       public void normaliseSiteSizes(){
           siteSizeNormalisation  =0.0;
            for (int s=0; s<numberSites; s++) siteSizeNormalisation +=siteArray[s].size;
            for (int s=0; s<numberSites; s++) siteArray[s].size = siteArray[s].size* numberSites/(siteSizeNormalisation );
           }
           
       
       public void setShortNames(){
           for (int i =0; i<numberSites; i++) siteArray[i].setShortName();
       }
       
       /**
        * Calculates the great circle distance between two sites using lat and long.
        * @param i index of first site
        * @param j index of second site
        * @return spherical distance between i and j
        */
      public double sphericalDistance(int i, int j){
           return
       Distances.sphericalDistance ( siteArray[i].latitude, siteArray[i].longitude, siteArray[j].latitude, siteArray[j].longitude);
       }
       
       /**
        * Calculates the Euclidean distance between two sites using X and Y coordinates.
        * @param i index of first site
        * @param j index of second site
        * @return euclidean distance between i and j
        */
      public double euclideanDistance(int i, int j){
           return
       Distances.euclideanDistance( siteArray[i].X,siteArray[i].Y, siteArray[j].X, siteArray[j].Y);
       }
       
       
       
/**
 * Find alphabetic site order
 */ 
      public void calcAlphabeticalOrder(){calcSiteOrder(1, siteAlphabeticalOrder, numberSites); }
          
    // site and edge weights and value
    
    /** Calculates statistics associated with sites.
     * <p>Includes those using edge values.
     *@param edgeSet set of edges
     */
    public void calcBasicStatistics(IslandEdgeSet edgeSet) 
     {       
    
      siteValueStats = new StatisticalQuantity(1.2345e97,-1.0);
      siteWeightStats = new StatisticalQuantity(1.2345e97,-1.0);
      siteStrengthOutStats = new StatisticalQuantity(1.2345e97,-1.0);     
      siteStrengthInStats = new StatisticalQuantity(1.2345e97,-1.0);
      for (int i=0; i<numberSites; i++)
        {
           siteArray[i].strength =0.0;
           siteArray[i].strengthIn =0.0;
           siteArray[i].strengthOut =0.0;
           siteArray[i].strengthSquaredIn =0.0;
           siteArray[i].strengthSquaredOut =0.0;
        }
      
      double vw,vV,eV,ew,ew2,sin,eV2;
//      totVertexValue=0;
//      totVertexWeight=0;
      for (int i=0; i<numberSites; i++)
        {
          vV=siteArray[i].getValue();
          vw=siteArray[i].getWeight(); 
          if (siteValueStats.maximum  <= vV){maxSiteValueIndex=i;   }
          if (siteWeightStats.maximum <= vw){maxSiteWeightIndex = i;}          
          siteValueStats.add(vV);  // updates the stats on the site values
          siteWeightStats.add(vw); // updates the stats on the site weights
          
          for (int j=0; j<numberSites; j++)
           {
//              totVertexValue+=vV;
//              totVertexWeight+=vw;
               eV=edgeSet.getEdgeValue(i, j);
               eV2=eV*eV;
               ew = vw*eV; // edge weight
               ew2=ew*ew;
               siteArray[i].strengthOut+=ew;
               siteArray[i].strengthSquaredOut+=ew2;
               siteArray[j].strengthIn+=ew;
               siteArray[j].strengthSquaredIn+=ew2;
               siteArray[j].strength+=ew; // TODO WHat is this? (In + Out)? Do we need it?
           }// eo for j

           if (siteStrengthOutStats.maximum <= siteArray[i].strengthOut) maxOutSiteStrengthIndex =i;
           siteStrengthOutStats.add(siteArray[i].strengthOut);
      }// eo for i
      
      for (int i=0; i<numberSites; i++)
        {
          sin=siteArray[i].strengthIn;
          siteStrengthInStats.add(sin);
          if (siteStrengthInStats.maximum <= sin) maxInSiteStrengthIndex =i;
        }// eo for i
      
      calcSiteWeightOrder();       

      for (int i=0; i<numberSites; i++)
             {
                 //if (siteWeightMax<siteSet.getWeight(i) ) siteWeightMax=siteSet.getWeight(i) ;
                 //if (siteInfluenceStats.maximum<getTotalInfluenceWeight(i) ) siteInflMax=siteArray[i].influence ;
                 //if (this.siteRankStats.maximum<siteArray[i].ranking) siteRankMax=siteArray[i].ranking;
                 if (siteRankOverWeightMax<siteArray[i].rankOverWeight) siteRankOverWeightMax=siteArray[i].rankOverWeight;
             }

      
}
 

         /**
          * Calculate some ranking based on diffusion transfer matrix.
          *@param transferMatrix transfer matrix to use for ranking
          */
    public void calcRanking(IslandTransferMatrix transferMatrix)
    {
        //transferMatrix = new IslandTransferMatrix(1, numberSites, siteSet, edgeSet);
        int m = transferMatrix.checkMarkovian(1e-3);
        if (m>=0) System.out.println(" !!! transferMatrix failed Markovian test at column "+m);
        else System.out.println("... Transfer Matrix is Markovian");
      
        int e=transferMatrix.checkNonNegative();
        if (e>=0) System.err.println("*** Transfer Matrix is negative at row "+e/transferMatrix.getDimension()+" column "+e%transferMatrix.getDimension());
        else System.out.println("... Transfer Matrix is non-negative");

        
        //transferMatrix.printEigenValueList("", " | ", System.out, 3, true, true, false);
        double evmax = transferMatrix.getAbsEigenValue(0); // count from zero!
        if (evmax<0.99) System.out.println("!!! WARNING in calcRanking largest absolute eigenvalue of transfer matrix has value "+evmax);
        
        double [] evectorMax = new double[numberSites];
        evectorMax = transferMatrix.getEigenVector(0); //count from zero, may have negative entries

        double norm=0.0;
        for (int i=0; i<numberSites; i++) {
            siteArray[i].ranking = Math.abs(evectorMax[i]);
            norm+=siteArray[i].ranking;
        }
        if (norm>0) for (int i=0; i<numberSites; i++) siteArray[i].ranking=siteArray[i].ranking/norm;
        for (int i=0; i<numberSites; i++) 
        {
            if (siteArray[i].getWeight()>0) siteArray[i].rankOverWeight = siteArray[i].ranking/siteArray[i].getWeight();
            else siteArray[i].rankOverWeight = 0;
        }

        // Now calculate Ranking Order siteRankOrder[i] = number of site ranked i-th by ranking
         calcSiteRankOrder(9, siteRankOrder, 10 ); //9=Rank, 10=RankingRank
        
        // Now calculate Order using siteRankOverWeightOrder[i] = number of site ranked i-th by rankOverSize
         calcSiteRankOrder(19, siteRankOverWeightOrder, 20 ); //19=rankOverWeight, 20=rankOverWeightRank

        siteRankStats = new StatisticalQuantity(99999.0,-1.0);
        for (int s = 0; s < numberSites; s++) siteRankStats.add(siteArray[s].ranking);

        }// eo calcRanking

    
    /** Calculate an Influence Matrix (unprimed and primed versions).
     * <p>An influence matrix I is based on a transfer matrix T
     * and is defined to be <tt>I=(1-p)/(1-pT)M<tt> with p an influence probability.
     * <p>This then calculates vertex bases statistics such as weighted in and out
     * strength of the influence matrix and betweennness measures.
     *@param transferMatrix transfer matrix must already be defined.
     *@param influenceProb probability to use for influenceInt calculations
     */
    public void calcInfluenceStatistics(IslandTransferMatrix transferMatrix, double influenceProb)
    {
        transferMatrix.calcInfluenceMatrix(influenceProb);
        double [] totalInfluenceWeight = new double [numberSites]; // Total influenceInt by weight of site i
        double [] totalInfluenceprimeWeight = new double [numberSites]; // Total influenceInt by weight of site i
        for (int s = 0; s < numberSites; s++) {
            totalInfluenceWeight[s] = 0;
            totalInfluenceprimeWeight[s] = 0;
            double sourceWeight=siteArray[s].getWeight();
            for (int t = 0; t < numberSites; t++){
                totalInfluenceWeight[s] += siteArray[t].getWeight() * transferMatrix.getInfluence(t, s) * sourceWeight;
                totalInfluenceprimeWeight[s] += siteArray[t].getWeight() * transferMatrix.getInfluencePrime(t, s) * sourceWeight;
            }
        }
        for (int s=0; s<numberSites; s++) {
            siteArray[s].influence = totalInfluenceWeight[s];
            siteArray[s].influenceprime = totalInfluenceprimeWeight[s];
        }
         // Rank total influenceInt using the weighted incoming influenceInt
        calcSiteRankOrder(IslandSite.influenceIndex, siteInfluenceOrder, IslandSite.influenceRankIndex); //11=IslandSite.influence, 13=IslandSite.influenceRank.
        siteInfluenceStats = new StatisticalQuantity(99999.0,-1.0);
        calcSiteRankOrder(IslandSite.influenceprimeIndex, siteInfluenceprimeOrder, IslandSite.influenceprimeRankIndex); //11=IslandSite.influence, 13=IslandSite.influenceRank.
        siteInfluenceprimeStats = new StatisticalQuantity(99999.0,-1.0);
        for (int s = 0; s < numberSites; s++) {
            siteInfluenceStats.add(siteArray[s].influence);
            siteInfluenceprimeStats.add(siteArray[s].influenceprime);
        }
 
        // now do betweeness measures
        double [] totalBetweennessWeight = new double [numberSites]; // Total influenceInt by weight of site i
        double maxbetweenness=-98.1;
        double [] totalBetweennessprimeWeight = new double [numberSites]; // Total influenceInt by weight of site i
        double maxbetweennessprime=-98.1;
//        double [] totalNBetweennessWeight = new double [numberSites]; // Total influenceInt by weight of site i
//        double maxnbetweenness=-98.1;
//        double [] totalNBetweennessprimeWeight = new double [numberSites]; // Total influenceInt by weight of site i
//        double maxnbetweennessprime=-98.1;
        for (int b = 0; b < numberSites; b++) {
            totalBetweennessWeight[b] = 0;
            totalBetweennessprimeWeight[b] = 0;
//            totalNBetweennessWeight[b] = 0;
//            totalNBetweennessprimeWeight[b] = 0;
            for (int s = 0; s < numberSites; s++) {
                if (s==b) continue;
                double sourceWeight=siteArray[s].getWeight();
                for (int t = 0; t < s; t++)
                    if (t!=b) {
                        totalBetweennessWeight[b] += transferMatrix.getInfluence(t, b) *transferMatrix.getInfluence(b, s) * sourceWeight;
                        totalBetweennessprimeWeight[b] += transferMatrix.getInfluencePrime(t, b) *transferMatrix.getInfluencePrime(b, s) * sourceWeight;
                        // now treat source and target in reverse
                        totalBetweennessWeight[b] += transferMatrix.getInfluence(s, b) *transferMatrix.getInfluence(b, t) * sourceWeight;
                        totalBetweennessprimeWeight[b] += transferMatrix.getInfluencePrime(s, b) *transferMatrix.getInfluencePrime(b, t) * sourceWeight;
//                        // now Newman or drift betweenness
//                        totalNBetweennessWeight[b] += transferMatrix.getInfluence(b, s) * sourceWeight;
//                        totalNBetweennessprimeWeight[b] += transferMatrix.getInfluencePrime(t, b) *transferMatrix.getInfluencePrime(b, s) * sourceWeight;
                    }
            }
            if (maxbetweenness<totalBetweennessWeight[b]) maxbetweenness=totalBetweennessWeight[b];
            if (maxbetweennessprime<totalBetweennessprimeWeight[b]) maxbetweennessprime=totalBetweennessprimeWeight[b];
        }
        // normalise
        for (int b = 0; b < numberSites; b++) {
            siteArray[b].betweenness  =totalBetweennessWeight[b]/maxbetweenness;
            siteArray[b].betweennessprime  =totalBetweennessprimeWeight[b]/maxbetweennessprime;
        }
        calcSiteRankOrder(IslandSite.betweennessIndex, siteBetweennessOrder, IslandSite.betweennessRankIndex );
        siteBetweennessStats = new StatisticalQuantity(99999.0,-1.0);
        calcSiteRankOrder(IslandSite.betweennessprimeIndex, siteBetweennessprimeOrder, IslandSite.betweennessprimeRankIndex );
        siteBetweennessprimeStats = new StatisticalQuantity(99999.0,-1.0);
        for (int s = 0; s < numberSites; s++) {
            siteBetweennessStats.add(siteArray[s].betweenness);
            siteBetweennessprimeStats.add(siteArray[s].betweennessprime);
        }
     }
    /**
     * Calculate Newman betweenness.
     * <p>This uses the primed version of a transfer matrix
     * i.e. without any leading eigenvalue. It is equivalent to finding
     * the modulus of the net drift
     * current (so the usual electric current) along an edge for given input source
     * current and output target current (equivalent to a negative source).  The total
     * for both directions along the edge is used.  Needs the random walker
     * currents to be already calculated for the edges. See
     * <br>Newman, M. E. J.
     * <br><em>A measure of betweenness centrality based on random walks</em>
     * <br>Social Networks, 2005, <b>27</b>, 39-54.
     *
     * @param transferMatrix transfer matrix ready defined.  Uses primed version
     * @param edgeSet set of edges with Newman betweenness already calculated.
     */
    public void calcNewmanBetweenness(IslandTransferMatrix transferMatrix, IslandEdgeSet edgeSet)
    {
                for (int b = 0; b < numberSites; b++) {
                   siteArray[b].nbetweenness=0;
                   siteArray[b].nbetweennessprime=0;
                   for (int j = 0; j < numberSites; j++) {
                     siteArray[b].nbetweenness+=Math.abs(edgeSet.getVariable(b, j, IslandEdge.nbetweennessINDEX)-edgeSet.getVariable(j, b, IslandEdge.nbetweennessINDEX));
                     siteArray[b].nbetweennessprime+=Math.abs(edgeSet.getVariable(b, j, IslandEdge.nbetweennessprimeINDEX)-edgeSet.getVariable(j, b, IslandEdge.nbetweennessprimeINDEX));
                   }
                   siteArray[b].nbetweenness/=2.0;
                   siteArray[b].nbetweennessprime/=2.0;
                }
        calcSiteRankOrder(IslandSite.nbetweennessIndex, siteNBetweennessOrder, IslandSite.nbetweennessRankIndex );
        siteNBetweennessStats = new StatisticalQuantity(99999.0,-1.0);
        calcSiteRankOrder(IslandSite.nbetweennessprimeIndex, siteNBetweennessprimeOrder, IslandSite.nbetweennessprimeRankIndex );
        siteNBetweennessprimeStats = new StatisticalQuantity(99999.0,-1.0);
        for (int s = 0; s < numberSites; s++) {
            siteNBetweennessStats.add(siteArray[s].nbetweenness);
            siteNBetweennessprimeStats.add(siteArray[s].nbetweennessprime);
        }

    }

//*************************************************************************

     /** Sets display size of all vertices.
      * <p>The absolute value of <tt>displayScale</tt> is used to set the
      * basic vertex display scale.  For positive (negative) <tt>displayScale</tt> 
      * this will be the size of 
      * vertices of value one ( the size of the vertex with maximum value).  
      * The site values to be used for setting display size 
      * are selected by <tt>siteVariableIndex</tt>.
      * <p>If site index does not correspond to known numerical
      * site value then all sites are set to display at size
      * |<tt>displayScale</tt>|.
      * <p>Otherwise if <tt>displayScale</tt> is positive then this is the
      * display size to use for vertices of value 1.0.
      * If <tt>displayScale</tt> is negative then the absolute value of
      * <tt>displayScale</tt> is used for the display size of the largest valued vertex
      * provided the statistics (the maximum value) is known for this site value
      * so this needs statistics to have been calculated.
      * negative values for the relevant site value are treated as zero so give 
      * zero sized vertex display.
      * @param siteVariableIndex IslandSite index of variable to be used for vertex sizing.
      * @param displayScale if positive for vertex size 1.0, otherwise for largest valued vertex.
      */     
    public void setAllDisplaySizes(int siteVariableIndex, double displayScale){
    displayIndex=siteVariableIndex;
    double scaleSize=1.0;
    boolean variableSizes = false; // true if have variable display sizes
    if (IslandSite.isIndex(displayIndex) && IslandSite.isNumerical(displayIndex) ) 
    {
        variableSizes = true; // site value can provide variable display size
        if ( hasStatistics(displayIndex) && (displayScale<0)) {
            {
                if (displayIndex == IslandSite.valueIndex)  scaleSize=siteValueStats.maximum;
                if (displayIndex == IslandSite.weightIndex)  scaleSize=siteWeightStats.maximum;
                if (displayIndex == IslandSite.strengthInIndex)  scaleSize=siteStrengthInStats.maximum;
                if (displayIndex == IslandSite.strengthOutIndex)  scaleSize=siteStrengthOutStats.maximum;
                if (displayIndex == IslandSite.rankingIndex)  scaleSize=siteRankStats.maximum;
                if (displayIndex == IslandSite.influenceIndex)  scaleSize=siteInfluenceStats.maximum;
                if (displayIndex == IslandSite.betweennessIndex)  scaleSize=siteBetweennessStats.maximum;
                if (displayIndex == IslandSite.influenceprimeIndex)  scaleSize=siteInfluenceprimeStats.maximum;
                if (displayIndex == IslandSite.betweennessprimeIndex)  scaleSize=siteBetweennessprimeStats.maximum;
                if (displayIndex == IslandSite.nbetweennessIndex)  scaleSize=siteNBetweennessStats.maximum;
                if (displayIndex == IslandSite.nbetweennessprimeIndex)  scaleSize=siteNBetweennessprimeStats.maximum;
            }
        }  
        if (scaleSize<1e-6) scaleSize =1.0;
    }
    
    double avSize=0.0;
    final double scale=Math.abs(displayScale); // baseline vertex display size
    double ns=scale; // display size if no variable size to be used
    maxDisplaySize=0;
    for (int i=0; i<numberSites; i++)
        {    
           if (variableSizes) ns=scale*siteArray[i].getVariable(displayIndex)/scaleSize;
           avSize+=ns;
           siteArray[i].displaySize = ns;
           maxDisplaySize=Math.max(maxDisplaySize,ns);
           if (siteArray[i].displaySize<0) siteArray[i].displaySize=0;  
           siteArray[i].Z=siteArray[i].displaySize;
        }//eo for i
    System.out.println("IslandSite: Site Display of "+IslandSite.dataName(displayIndex)
            +"  scale, scaleSize, average, max "
            +scale+"  "+scaleSize+"  "+avSize/numberSites+"  "+maxDisplaySize);
}
      
     /** Prints a line of display sizes to a print stream. 
         *@param PS print stream
         *@param sep separation string
         *@param dec integer number of decimal places to display
         */     
public void printDisplaySizes(PrintStream PS, String sep, int dec) 
     {
        for (int i =0; i<numberSites; i++) {PS.print(sep+n2s.toString(siteArray[i].displaySize,dec));}
     }

     /** Prints fixed site data for whole set
         *@param PS a print stream for the output such as System.out
         *@param cc comment characters put at the start of every line
         *@param sep separation string
         */     
     public void printFixedSiteVariables(PrintStream PS, String cc, String sep) 
     {
        PS.println(cc+sep+siteArray[0].fixedDataNameString(sep) );
        for (int i =0; i<numberSites; i++) 
        {
            PS.println(i+sep + siteArray[i].fixedDataString(sep));
        } 
        
     }

    /** Prints site data for whole set.
     * <p>Each line is the data for a different value.
     * No header is given
     *@param PS a print stream for the output such as System.out
     *@param cc comment string
     *@param sep separation string
     */    
     public void printSiteVariables(PrintStream PS, String cc, String sep) 
     {
        for (int v=0; v<IslandSite.numberVariables; v++) printValues(PS, cc, sep, v);
        printGenes(PS, cc, sep);
     }

    /** Returns string of values needed to specify results of model including genes.
     * <br>Includes input parameters, basic outputs, and genes. Can exclude features that are derivable.
     *@param PS a print stream for the output such as System.out
     *@param cc comment string
     *@param sep separation string
     */    
     public void printSiteParameters(PrintStream PS, String cc, String sep) 
     {
        for (int s=0; s<this.numberSites; s++) PS.println(siteArray[s].parameterStringValues(sep,5));
        
     }     
     
     /** Prints data for all sites for one value if site 0 is set.
      * <p>Tests site zero to see if variable v is set.
            *@param PS a print stream for the output such as System.out
            *@param cc comment string
            *@param sep separation string
            *@param v index of value to be printed
            */
     public void printValues(PrintStream PS, String cc, String sep, int v)
     {
           if (siteArray[0].isSet(v)) printValuesAlways( PS, cc, sep, v);
     }

     /** Always prints data for all sites for one value.
            *@param PS a print stream for the output such as System.out
            *@param cc comment string
            *@param sep separation string
            *@param v index of value to be printed
            */
     public void printValuesAlways(PrintStream PS, String cc, String sep, int v)
     {
           PS.print(cc+IslandSite.dataName(v));
           for (int i=0; i<numberSites; i++)
                PS.print(sep+siteArray[i].toString(v));
           PS.println();
     }
     /** Always prints all data for one site.
      * <p>First column is index of site.  Only data that is set is used.
      *@param PS a print stream for the output such as System.out
      *@param cc comment string
      *@param sep separation string
      *@param s index of site to be printed
      */
     public void printAllValuesOneSite(PrintStream PS, String cc, String sep, int s)
     {
           PS.print(cc+s);
           for (int v=0; v<IslandSite.numberVariables; v++)
               if (siteArray[s].isSet(v)) PS.print(sep+siteArray[s].toString(v));
           PS.println();
     }

     /** String of all set data for one site.
      * <p>First column is index of site.  Only data that is set is used.
      *@param sep separation string
      *@param s index of site to be printed
      */
     public String toStringAllValuesOneSiteLabel(String sep)
     {
           String sss = "site";
           for (int v=0; v<IslandSite.numberVariables; v++)
               if (siteArray[0].isSet(v)) sss=sss+sep+IslandSite.dataName(v);
           return sss;
     }

     /** String of all set data for one site.
      * <p>First column is index of site.  Only data that is set is used.
      *@param sep separation string
      *@param s index of site to be printed
      */
     public String toStringAllValuesOneSite(String sep, int s)
     {
           String sss = Integer.toString(s);
           for (int v=0; v<IslandSite.numberVariables; v++)
               if (siteArray[s].isSet(v)) sss=sss+sep+siteArray[s].toString(v);
           return sss;
     }

     /** Prints data for all sites for one value.
            *@param PS a print stream for the output such as System.out
            *@param cc comment string
            *@param sep separation string
            *@param name name of value to be printed
            */     
     public void printValues(PrintStream PS, String cc, String sep, String name) 
     {
         int v = IslandSite.getIndex(name);
         if (v<0) PS.println("Variable "+name+" is unknown");
         else printValues(PS, cc, sep, v);
     }

     /** Prints values of one variable for all sites as a vector.
            *@param PS a print stream for the output such as System.out
            @param name name of value to be printed
            */     
     public void printValueVector(PrintStream PS, String name) 
     {
         int v = IslandSite.getIndex(name);
         if (v<0) PS.println("Variable "+name+" is unknown");
         else printValueVector(PS, v);
     }

     /** Prints values of one variable for all sites as a vector.
            *@param PS a print stream for the output such as System.out
      * @param v index of value
            */     
     public void printValueVector(PrintStream PS, int v) 
     {
         for (int i=0; i<numberSites; i++)
                PS.println(siteArray[i].toString(v));
     }

          /** Prints gene data for whole set.
           * <p>Each line is the data for a different gene.
           * No header is given
         *@param PS a print stream for the output such as System.out
         *@param cc comment string
            *@param sep separation string
         */     
     public void printGenes(PrintStream PS, String cc, String sep) 
     {
         for (int g=0; g<geneName.size(); g++)
         {
           PS.print(geneName.get(g)); 
           for (int i=0; i<numberSites; i++) PS.print(sep+siteArray[i].getGeneValue(g));
           PS.println(); 
         }
     }
     
    /**
     *  Returns string of names of genes.
     * <br>Includes input parameters and basic outputs. Can exclude features that are derivable.
     * <br>Starts with a sep string which can be used to add a character 
     * to indicate the label is a gene name not a site parameter name.
     * <p>Is null if there are no genes.
     *@param sep separation string
     *@return string with names and values of parameters.
     */
          public String geneNameString(String sep)
          {
            String s="";
            if (geneName.size()>0) for (int g=0; g<geneName.size(); g++) s=s+sep+geneName.get(g);
            return s;
          }

          /**
           *  Returns string for a site with name then value of all data variables.
           * <p>Will give nothing for a variable if it is not set.
           * @param index index of site
           *@param sep1 separator between name and data value, e.g. &quot;: &quot;.
           *@param sep2 separator between different variables e.g. tab or space or linefeed.
           *@param dec number of decimal places to keep
           *@return string with name of data variable number value for site index.
           *
           */
          public String toString(int index, String sep1, String sep2, int dec)
        {
              String s="";
              for (int i=0;i<IslandSite.numberVariables;i++) if (siteArray[index].isSet(i)) s=s+IslandSite.dataName(i) + sep1 + siteArray[index].toStringShort(i,dec)+sep2;
              for (int g=0; g<geneName.size(); g++) s=s+geneName.get(g)+sep1+siteArray[index].geneToShortDoubleString(g, dec)+sep2;
              return s;
          }

          /**
           * Returns link to a site.
           * @param index index of site
           * @return link to site
           */
          public IslandSite getSite(int index){return siteArray[index];}
     
  // **********************************************************    
     /**
       * Gives the output for the vertex part of a pajek net file.
       * <p>Uses existing display sizes so these must be set first.
       * @param PS a print stream for the output such as System.out
       * @param minColourFrac fraction of total colours represented as colour 1
       * @param zeroColourFrac fraction of total colours represented as colour 0
       * @param colourOn true (false) for colour
       */
     public void printPajekNetFormat(PrintStream PS, double minColourFrac, double zeroColourFrac,
                           boolean colourOn){
         int numberColours = PajekColours.numberColours;
         PS.println("*Vertices      "+numberSites);
            //      1 "a" ic Pink     bc Black
         for (int i=0; i<numberSites; i++)
            {
              int sc=(int) (0.499999+ siteArray[i].displaySize);
              if (sc>numberColours) sc= numberColours;
              if (sc<minColourFrac*numberColours) sc=1;
              if (sc<zeroColourFrac*numberColours) sc=0;
              PS.print((i+1)+"  \""+getName(i)+"\" "
                       + siteArray[i].X + " "
                       + siteArray[i].Y + " ");
              PS.print( siteArray[i].Z
                          +" s_size 1 "
                          + " x_fact "+n2s.TruncDecimal(siteArray[i].displaySize,2)
                          + " y_fact "+n2s.TruncDecimal(siteArray[i].displaySize,2));
              //PS.print( "  "+siteArray[i].getWeight());
              if (colourOn) PS.println(" ic "+PajekColours.colours[sc]+"   bc "+PajekColours.colours[numberColours]);
              else PS.println(" ic "+PajekColours.getGrey(sc)+"   bc "+PajekColours.getGrey(sc));
            }

}
     /**
      * Gives the output for the vertex part of a pajek net file.
      *@param PS a print stream for the output such as System.out
       * @param minColourFrac fraction of total colours represented as colour 1
     * @param zeroColourFrac fraction of total colours represented as colour 0
     * @param fileType 0=plain, 1= BW, 2= colour, 3=Influence Matrix, 4=Culture Correlation Matrix
      * @deprecated needs to be replaced with modern version
       */
     public void printPajekNetFormat(PrintStream PS, double minColourFrac, double zeroColourFrac,
                           int fileType){
         int numberColours = PajekColours.numberColours;
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
              PS.print((i+1)+"  \""+getName(i)+"\" "
                       + siteArray[i].X + " "
                       + siteArray[i].Y + " ");
              switch (fileType)
              {
                  case 2:
                  case 1: PS.print( siteArray[i].Z
                          +" s_size 1 "
                          + " x_fact "+n2s.TruncDecimal(siteArray[i].displaySize,2)
                          + " y_fact "+n2s.TruncDecimal(siteArray[i].displaySize,2));
                          break;
                  case 4:
                  case 3:
                  case 0:
                  default: PS.print( "  "+siteArray[i].getWeight());
              }
              switch (fileType)
              {
                  case 1: PS.println(" ic "+PajekColours.getGrey(sc)+"   bc "+PajekColours.getGrey(sc)); break;
                  case 2: PS.println(" ic "+PajekColours.colours[sc]+"   bc "+PajekColours.colours[numberColours]); break;
                  case 4:
                  case 3:
                  case 0:
                  default: PS.println();
              }

            }

}
     
        // **********************************************************    
     /**
      * Calculate the ranking of sites by weight.
      */
     public void calcSiteWeightOrder(){
          for (int i=0; i<numberSites; i++) siteArray[i].setWeight();
          calcSiteRankOrder(7,siteWeightOrder,8); //7 = weight, 8= WeightRank
     }

/** Produces a list of the rank (order) of a vector of values.
     * @param valueNumber is number of entry in Site class to be ordered
     * @param orderVector is vector of integers [r] = index of value of rank r 
     * @param n is number of entries to order
     */
    private void calcSiteOrder(int valueNumber, int [] orderVector, int n)
    {
// Now calculate Ranking Order orderVector[i] = number of site ranked i 
         boolean alpha = IslandSite.isAlpha(valueNumber);
         for (int i=0; i<n; i++) orderVector[i]=i;
         for (int i=0; i<n; i++) 
            {
                int best = orderVector[i];
                for (int j=i+1; j<n; j++)
                {
                 int newbest = orderVector[j];
                 if ( ((alpha) && (siteArray[best].toString(valueNumber).compareToIgnoreCase(siteArray[newbest].toString(valueNumber))>0)  )
                     || ((!alpha) && (siteArray[best].getVariable(valueNumber) < siteArray[newbest].getVariable(valueNumber) )  ) )
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
   

    
}
