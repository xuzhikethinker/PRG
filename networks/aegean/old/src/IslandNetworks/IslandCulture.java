/*
 * IslandCulture.java
 *
 * Created on 13 December 2006, 11:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import java.util.Random;
import java.io.*;
 
import TimUtilities.TimTiming;
import TimUtilities.NumbersToString;
import TimUtilities.TimMessage;

/** 
 * To investigate the cultural transmission on the island networks.
 * @author time
 */
public class IslandCulture {
    
    public TimMessage message = new TimMessage(0);
    int numberSites=0;
    EventProbability eProb; // probability for different copy events
    private int numberInd =0;
    private double totalWeight;
    private int [] indArray; // the cultural value of each individual 
                     // indArray[s]/numberSites = generation of artifact  
                     // indArray[s]%numberSites = site culture associated with this site
    
    private int [] indSite; // the site value of each individual
    private int [] firstInd; // firstInd[s] is the number of the first individual of site s
    private int [] nIndAtSite; // number of individuals at site s
    private int [] nArtifact; // number of artifacts associated with site s
    private EventProbability [] neighbourProb; // probability of going to neighbours
    private Random Rnd = new Random();   
    
    // Information data
    double siteCultureVector[][]; // siteCultureVector[i][j] is the fraction of j culture at i
    double siteCultureF2[]; // siteF2[i] is F2 homegeneity measure using site culture = culture%numbersites
    double cultureF2[]; // siteF2[i] is F2 homegeneity measure using full culture 
    double maxCulture[]; // maxCulture[s] is the largest culture vector entry for site s
    int maxCultureSite[]; // maxCultureSite[s] is the site with the largest cultural influence on site s
    double cultureCorrelation [][]; //cultureCorrlation[s][t] is the symmetric matrix of the dot product 
                                    // between the culture vectors
    /**  Creates a new instance of IslandCulture 
     * Deficit is probability of innovation where return to home culture
     *@param pSiteCopy probability of copying withoin site
     *@param pCopy probability of copying from neighbouring sites 
     *@param pInnovate probability of innovation keeping current culture 
     **/
    public IslandCulture(double pSiteCopy, double pCopy, double pInnovate) 
    {
     eProb = new EventProbability(4);
     eProb.addProbability(pSiteCopy);
     eProb.addProbability(pCopy);
     eProb.addProbability(pInnovate);
     eProb.addProbability(1.0-pSiteCopy-pCopy-pInnovate);
    }
    
    /**  Deep copy of IslandCulture **/
    public IslandCulture(IslandCulture ic) {
         numberSites=ic.numberSites;
         siteCultureVector = new double [numberSites][numberSites];
         cultureCorrelation = new double [numberSites][numberSites];
         siteCultureF2= new double [numberSites]; 
         cultureF2= new double [numberSites]; 
        for (int s=0; s<numberSites; s++) 
        {
            cultureF2[s]=ic.cultureF2[s];
            siteCultureF2[s]=ic.siteCultureF2[s];
            for (int t=0; t<numberSites; t++) siteCultureVector[s][t]=ic.siteCultureVector[s][t];
        }
        eProb = new EventProbability (ic.eProb);
    }
    
    /** 
     * Set up the individuals.
     * <br> Uses symmetrised edge weights (s_iv_i e_{ij}) as
     * isolated sites with no incoming edges get input from in and outgoing traffic.
     *@param siteWeight site weights
     *@param edgeValue edge values
     *@param nInd number of individual vertices to use (may be a few more).
     *@param mode =0 set culture uniform at each site, = 1 all different at each site
     */
    public void setup(double [] siteWeight, double [][] edgeValue, int nInd, int mode)
    {
        numberSites=siteWeight.length;
        totalWeight=0;
        for (int s=0; s<numberSites; s++) totalWeight +=siteWeight[s];
        double weightPerInd = totalWeight/nInd;
        indArray = new int [nInd+numberSites]; // leave some spare capacity
        indSite = new int [nInd+numberSites]; // leave some spare capacity
        firstInd = new int [numberSites+1];
        nIndAtSite = new int [numberSites];
        nArtifact = new int [numberSites]; 
        neighbourProb = new EventProbability [numberSites] ;
        int ni;
        numberInd=0;
        for (int s=0; s<numberSites; s++) 
        {
            nIndAtSite[s]=(int) (0.5+siteWeight[s]/weightPerInd);
            if (nIndAtSite[s]<1) nIndAtSite[s]=1;
            firstInd[s] = numberInd;
            for (int i=0; i<nIndAtSite[s]; i++) {
                indArray[i+numberInd]=s+ ((mode==0)?0:i*numberSites);
                 indSite[i+numberInd]=s;
            }            
            if (mode==0) nArtifact[s]=1;
            else nArtifact[s]=numberSites;
            numberInd+=nIndAtSite[s];
        }
        firstInd[numberSites]=numberInd;
        
        // set up neighbour probabilities
        // use symmetric edge values as isolated sites with no incoming edges
        // get input both ways
        for (int s=0; s<numberSites; s++) 
        {
            neighbourProb[s] = new EventProbability(numberSites);
            double strength=0;
            for (int t=0; t<numberSites; t++) if (s!=t) strength+=siteWeight[s]*edgeValue[s][t]+siteWeight[t]*edgeValue[t][s];
            if (strength>0)
            {
                for (int t=0; t<numberSites; t++)   if (s==t) neighbourProb[s].addProbability(1.0-strength) ;                 
                else neighbourProb[s].addProbability((siteWeight[s]*edgeValue[s][t]+siteWeight[t]*edgeValue[t][s])/strength) ;                 
            }
            else
            {
             for (int t=0; t<numberSites; t++)   if (s==t) neighbourProb[s].addProbability(1.0);
             else neighbourProb[s].addProbability(0.0);    
            }
            
        }// for s
        
    }// eo setup
    
    public void evolve(int nevents)
    {
        // set up screen indicators
        int eventnotetemp = nevents+1; //should give no progress indication on screen
        int tendotcounter = 10;            
        double eventnotefactor=0.01;
        if (eventnotefactor >0) eventnotetemp = (int) ( ((double)nevents) * eventnotefactor);
        if (eventnotetemp<1) eventnotetemp=1;
        final int eventnote = eventnotetemp;
        int dotcounter = eventnote;
        TimTiming timing= new TimTiming();
        
        int sourceInd=-1;
        int targetInd = -1;
        int sourceSite =-1;
        int targetSite =-1;
        int sourceCulture =-1;
        int targetCulture =-1;
        int newCulture  =-1;
        int e=-1;
        
        timing.setInitialTime();
        message.println(0," /// Starting Culture Evolution /// ");
        for (int t=0; t<nevents; t++)
        { // target will copy the culture of the source or innovate
            sourceInd = Rnd.nextInt(numberInd);
            sourceSite = indSite[sourceInd];
            e=eProb.getEvent(Rnd.nextDouble());
            switch(e)
            {
                case 0: // target copies within site, 
                    targetInd = firstInd[sourceSite] + Rnd.nextInt(nIndAtSite[sourceSite]); 
                    newCulture = indArray[sourceInd];
                    break;
                case 1: //  neighbouring sites 
                    targetSite = neighbourProb[sourceSite].getEvent(Rnd.nextDouble());
                    targetInd = firstInd[targetSite] + Rnd.nextInt(nIndAtSite[targetSite]);  
                    newCulture = indArray[sourceInd];
                    break;
                    
                case 2: // innovate but keep current culture
                    targetInd=sourceInd;
                    sourceCulture = indArray[sourceSite]%numberSites;
                    newCulture = nArtifact[sourceCulture]*numberSites + sourceCulture; 
                    nArtifact[sourceCulture]++;
                    break;                    

                case 3: // innovate but use home culture
                    targetInd=sourceInd;
                    newCulture = nArtifact[sourceSite]*numberSites + sourceSite; 
                    nArtifact[sourceSite]++;
                    break;                    
}
            indArray[targetInd]=newCulture;
            
            if (message.testInformationLevel(1)) 
            { if (0 == (--dotcounter)  ) {
                        System.out.print(".");
                        dotcounter = eventnote;  // reset counter
                        if (0 == (--tendotcounter)  ) {
                            System.out.println(" "+ timing.runTimeString());
                            tendotcounter =10;  // reset counter
                        }
                    }
            }
            
        }// eo t
        
    }// eo evolve


    
 /**
 * Calculates Homogeneity
 * @param firstInd number of first individual
 * @param lastp1Ind number of last individual +1
 */
    public double calcHomogeneity(int firstInd, int lastp1Ind) {
        int same =0;
        for (int i = firstInd; i<lastp1Ind;i++)
         for (int j = i+1; j<lastp1Ind;j++)   
             if (indArray[i]==indArray[j]) same++;
        double numberInd =  (double) (lastp1Ind-firstInd);
        double F2 =  same*2.0/(numberInd*(numberInd-1.0));       
        return (F2);
        
    }
 
/**
 * Calculates Site Homogeneity
 * @param firstInd number of first individual
 * @param lastp1Ind number of last individual +1
 */
    public double calcSiteHomogeneity(int firstInd, int lastp1Ind) {
        int same =0;
        for (int i = firstInd; i<lastp1Ind;i++)
         for (int j = i+1; j<lastp1Ind;j++)   
             if ((indArray[i]%numberSites)==(indArray[j]%numberSites)) same++;
       double numberInd =  (double) (lastp1Ind-firstInd);
       double F2 =  same*2.0/(numberInd*(numberInd-1.0));
       return (F2);
    }
 
    
    /**
  * Calculates Homogeneity factors for whole system.
  */
    public void calcHomogeneity() {
        cultureF2 = new double [numberSites];
        siteCultureF2 = new double [numberSites];
        int same =0;
        for (int s=0; s<numberSites; s++) {
            siteCultureF2[s]=calcSiteHomogeneity(firstInd[s], firstInd[s+1]);
                cultureF2[s]=calcHomogeneity(firstInd[s], firstInd[s+1]);
        }// eo for s
}
 /**
  * Calculates Site Culture Vector.
  *<br>siteCultureVector[i][j] is the fraction of type j culture at site i
  */
    public void calcCultureVectors() {
        siteCultureVector = new double [numberSites][numberSites];
        maxCulture = new double [numberSites];
        maxCultureSite = new int [numberSites];
        int same =0;
        double d=-1;
        for (int s=0; s<numberSites; s++) {
            for (int t=0; t<numberSites; t++) siteCultureVector[s][t]=0;
            double oneInd= 1.0/ nIndAtSite[s];
            for (int i = firstInd[s]; i<firstInd[s+1];i++)  {
                siteCultureVector[s][indArray[i]%numberSites]+=oneInd;
            }            
            maxCulture[s]=0;
            maxCultureSite[s]=-1;
            for (int t=0; t<numberSites; t++) 
            {
                d = siteCultureVector[s][t];
                if ((d>maxCulture[s]) || ((d==maxCulture[s]) && (Rnd.nextBoolean()))) {
                    maxCulture[s] = d;
                    maxCultureSite[s] = t;                    
                }
            }
            
        }// eo for s
}
 
    /**
     * Calculates culture correlations
     * @param s site
    * @param t site
  *@return dot product of normalised culture vectors 
     */
    public double calcCorrelation(int s, int t)
    {  
        double prod=0;
        double norms=0;
        double normt=0;
        for (int i=0; i<numberSites; i++)
        {
            prod  += siteCultureVector[s][i]  * siteCultureVector[t][i];
            norms += siteCultureVector[s][i]  * siteCultureVector[s][i] ;
            normt += siteCultureVector[t][i]  * siteCultureVector[t][i] ;            
        }
        if ((norms>0) && (normt>0)) prod = prod/(Math.sqrt(norms*normt));
        return prod;
    }
       
    /**
     * Calculates Culture correlations.
     */
    public void calcCorrelation()
    {  
        cultureCorrelation = new double [numberSites][numberSites];
        for (int s=0; s<numberSites; s++) 
            for (int t=0; t<numberSites; t++)   cultureCorrelation [s][t] = calcCorrelation(s,t);
       }
                
                
   /**
  * Calculates All Statistics
  */
    public void calcAllStats() {
        calcCultureVectors();
        calcHomogeneity();
        calcCorrelation();
            
        }// eo for s
 
 
    
    
    
 /**
     * Outputs Culture distribution 
     * @param PS a PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     * @param sep separation character such as tab
  *@param dec number of decimals to display
     */
    public void print(PrintStream PS, String cc, String sep,int dec)  
    {
        PS.println(label(cc,sep));
        for (int s=0; s<numberSites; s++) PS.println(toString(s,cc,sep,dec));
    }    
    
    
 /**
     * String used to label Culture distribution output.
     * @param cc comment characters put at the start of every line
     * @param sep separation character such as tab
     */
    public String label(String cc, String sep)  
    {
        String sss="";
        sss=cc+sep+"#Ind"+sep+"Max.Cult.Site"+sep+"Max.Cult."+sep+"#Art"+sep+"F2"+sep+"F2Site"+sep;
        for (int s=0; s<numberSites; s++) sss+="Cult."+s+"AT row"+sep;
        sss+="cos(theta)"+sep;
        for (int s=0; s<numberSites; s++) sss+= s+sep;
        return sss;
    }
  
  /**
   * Gives line summarising Culture distribution of site s.
   *@param s culture distribution data is for this site
   *@param cc comment characters put at the start of every line
   *@param sep separation character such as tab
   *@param dec number of decimals to display
   */
    public String toString(int s, String cc, String sep, int dec) {
        NumbersToString n2s = new NumbersToString();
        String sss="";
        sss=s+sep+nIndAtSite[s]+sep+maxCultureSite[s]+sep+n2s.toString(maxCulture[s],dec)+sep+n2s.toString(nArtifact[s],dec)+sep+n2s.toString(cultureF2[s],dec)+sep+n2s.toString(siteCultureF2[s],dec) ;
        for (int t=0; t<numberSites; t++) sss+=sep+n2s.toString(siteCultureVector[s][t],dec);
        sss+=sep+"   ";
        for (int t=0; t<numberSites; t++) sss+=sep+n2s.toString(cultureCorrelation[s][t],dec);
        return sss;
    }
   
    
    
    
    /** 
 * EventProbability Class.
 * <br> Keeps event probabilities in hand.
 * @author time
 */

    
    public class EventProbability
    {
        double [] prob;
        double [] cummulativeProb;
        int numberProb =-1;
        
        /** Constructs an EventProbability
         *@param nProb maximum number of different probabilities
         */
        public EventProbability(int nProb)
        {
            prob = new double [nProb];
            cummulativeProb = new double [nProb];
            numberProb =0;
        }
        
        /** Deep copy of EventProbability.
         */
        public EventProbability(EventProbability p)
        {
            numberProb = p.numberProb;
            prob = new double [numberProb];
            cummulativeProb = new double [numberProb];
            numberProb =0;
            for (int i =0; i< numberProb; i++) addProbability(p.prob[i]);
            
        }

        /** Sets probability.
         *@param p the probability
         *@return true if there is a problem, false if OK
         */
        public boolean addProbability(double p)
        {
            if (test(p)) return true;
            prob[numberProb]=p;
            if (numberProb>0) cummulativeProb[numberProb]=p+cummulativeProb[numberProb-1];
            else cummulativeProb[numberProb]=p;
            if (test(cummulativeProb[numberProb])) return true;
            numberProb++;
            return test();
            }

//        /** Reworks last entry to make it the last probability.
//         *@return true if there is a problem, false if OK
//         */
//        public boolean finishProbability()
//        {
//            if (numberProb==0) return true; 
//            double p = 1.0-cummulativeProb[numberProb-1];
//            if (p<0) return true;
//            prob[numberProb] = p;
//            cummulativeProb[numberProb]=1.0;
//            numberProb=numberProb+1;
//            return (test());
//            }

        /**
         * Returns the event number for corresponding value
         *@param r double between 0 and 1 corresponding to the probability wanted
         *@return event number
         */
        public int getEvent(double r)
        {
            int e=0;
            while (r>cummulativeProb[e]) e++;
            return e;
        }
        
        /** Test probabilities.
         *@return true if there is a problem, false if OK
         */
        public boolean test()
        {
            if (cummulativeProb[numberProb-1]  > 1.000001 ) return false;
            return true;
        }
        
         /** Test probabilities.
         *@param p
         *@return true if there is a problem, false if OK
         */
        public boolean test(double p)
        {
            if ((p  > 1.000001 ) || (p< 0)) return true;
            return false;
        }
        
        
       /** 
        * Returns a label for columns matching the toString() format.
        * @param Sep separation characters put beteen items
        *@return Label for string of information on probabilities  
        */
        public String label(String Sep)
        {
            String s="";
            for (int i=0; i<numberProb; i++) s=s+"prob[i]"+Sep;
            return s;
        }
            
        /** 
        * Returns a string representing all the probability values.
        * @param Sep comment characters put at the start of every line
        *@return String of information on probabilities  
        */
        public String toString(String Sep)
        {
            String s="";
            for (int i=0; i<numberProb; i++) s=s+prob[i]+Sep;
            return(s);
        }
    

    }//eo EventProbability class
    
    
    

}


