/*
 * DDStats.java
 *
 * Created on 15 November 2006, 16:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package DataAnalysis;


import java.util.Random; //p524 Schildt

import cern.colt.list.IntArrayList;

import TimUtilities.TimMessage;
//import TimUtilities.TimIntegerArrayList;
    

/** 
 * Analysis of integer counts.
 *<br>Calculates statistics of a distibution of counts, a sequence of zero or positive integers, 
 * denoted by k.  
 * Can also start from the frequency counts, <tt>n(k)</tt> 
 * where this is the number of times <tt>k</tt> appeared in the sequence. 
 * Can take the averages of many of such counts.
 * @author time
 */

    
    public class IntegerSequence
    {
     final static String VERSION = "DDStats260305";
     final static int MAXIMUMFNUMBER=6;  //maximum order of F_n to calculate
     final static int maxStatisticsMode = 1+2+4;
     final static double DUNSET = -1.35798642e99;
     Random Rnd = new Random();  //Schildt p524, time is used as seed
     //TimIntegerArrayList TimIAL = new  TimIntegerArrayList();
     int numberIndividuals=-99; // assumed to be the number of edges
     int statisticsMode = maxStatisticsMode; // (cm & 1) Basic Data, (cm & 2) Fn, (cm & 4) n(k), (cm & 8) n<=(k),  (cm & 16) rho
     //int maxrhonumber=+1;  // how many rho[?] to calculate.  <0 means do not calculate rho    
     double statisticsError=1e-3; // used to control error requested when sampling 
        

     boolean initialised =false;
     
     int Nactive; // number non-zero entries
     int kmin;
     int kmax;
     int count;
     double dcount;

     /**
      * <tt>n(k)</tt>
      */
     IntArrayList nkvalue; // degree distribution at selected values
     
     /**
      * <tt>n(<=k)</tt>
      */
     IntArrayList   nleqkvalue; // cummulative degree distribution at selected values
     int maxMoment;
     /**
      * Unnormalised moments of the distribution.
      * <p><tt>kmoment[n] = (\sum k^n) </tt>
      */
     int [] kmoment;
     /**
      * Normlaisation Factors for the factorial moments.
      */
     double [] FnNormalisation;
//     /**
//      * Moments of the distribution.
//      * <p><tt>mu[n] = (\sum k^n) / (\sum 1)</tt>
//      */
//     double [] mu;

     int maxFnumber;       // maxF maximum order of F_n to calculate
//     /**
//      * List of <tt>F[n]</tt>  values.
//      * <p>The <tt>F[n]/N</tt> are the factorial moments.
//      */
//     double [] Fn;
     
     /**
      * Number of moments needed.
      * <p>This is at least 2.
      */
     private int nmoments;
        
//     int Fkc; // Homogeneity factor Kimura+Crow k^2 version
     int S; // entropy
     
     
     TimMessage message = new TimMessage(-2);
     
     /** Constructs Integer Count Distribution Stats using parameters of given DDStats.
      *@param dds existing DDStats from which to copy parameters
       */
        public IntegerSequence(IntegerSequence dds)
        {
             initialiseBasicParameters(dds.getMaximumMomentNumber(), dds.getMaximumFNumber(),   dds.getCalculateMode());
        }
        
     /** Constructs Degree Distribution Stats
      *@param maxmu maximum degree moment mu_n to calculate
      *@param maxF maximum order of F_n to calculate
      *@param cm calculational mode: (cm & 1) Basic Data, (cm & 2) Fn, (cm & 4) n(k), (cm & 8) n<=(k),  (cm & 16) UNUSED
      */
        public IntegerSequence(int maxmu, int maxF, int cm)
        {
            initialiseBasicParameters(maxmu, maxF,   cm);
        }
  
        /** Inialises basic parameters of Degree Distribution Statistics
      *@param maxmu maximum degree moment mu_n to calculate
      *@param maxF maximum order of F_n to calculate
      *@param numk number of k values to follow includes 0 and E
      *@param cm calculational mode: (cm & 1) Basic Data, (cm & 2) Fn, (cm & 4) n(k), (cm & 8) n<=(k),  (cm & 16) rho
      */
        private void initialiseBasicParameters(int maxmu, int maxF, int cm)
        {
            maxMoment=maxmu;
            maxFnumber = maxF;
            setStatisticsMode(cm);
        }
        
        
            
        
      /** Inialises Degree Distribution Statistics from basic parameters already set.
       * Will only do this once after created.
      */
        private void initialiseParameters()
        {
         if (initialised) return;
         initialised=true;
         kmin=20000000;
         kmax=-kmin; 
         
         // general values
         count =0;
         dcount =count;
         Nactive=0; // number non-zero degree artifacts
         //Fkc= 0; // Homogeneity factor Kimura+Crow k^2 version
         S= 0; // entropy
         
         nmoments = Math.max(Math.max(maxMoment,maxFnumber),2);// always need at least first two moments
         int length=nmoments+1;
         kmoment = new int[length];
         FnNormalisation = new double[length];
         
        }

    /**
     * Calculate statistics from array of integer counts.
     *<p>This calculates a frequency distribution (histogram) and from this calculates the stats.
     * @param countArray Array of int describing the count (<tt>k</tt>) for each event.
     * @return The frequency of each count value in the sequence, i.e. <tt>n(k)</tt>
     */
    public void calcStats(int [] countArray)
    {
        initialiseParameters();
        int k;
        count=countArray.length;
        dcount=count;
        for(int a=0; a<count; a++){
          k = countArray[a];
          kmin=Math.min(kmin, k);
          kmax=Math.max(kmax, k);
          if (k>1e-6) {double p = k/dcount; S+=p*Math.log(p); Nactive++;}
          int kp=1;
          for (int i=0; i<=nmoments; i++) {
              kmoment[i] += kp;
              kp*=k;
          }
        }
        int norm=1;
        for (int i=0; i<=nmoments; i++) {FnNormalisation[i] = norm; norm*=kmoment[1]-i; } 

        // Entropy calc
        for(int a=0; a<count; a++){
          double p = countArray[a]/((double)kmoment[1]);
          if (p>1e-6) S-=p*Math.log(p); 
        }
    } // eo calcStats
        
        

    /**
     * Calculates n-th moment of sequence, <tt>mu(n)</tt>.
     * @param n order of moment
     * @return n-th moment, <tt>mu(n) = \sum k^n / \sum 1</tt>
     */
    public double getMoment(int n){
        if ((n<0) || (n>nmoments)) throw new RuntimeException("Can not handle less than zero or more than "+nmoments);
        return kmoment[n]/ dcount;
        } 
    
    /**
     * Calculates n-th Homogeneity value of sequence, <tt>F[n]</tt>.
     * @param n order of Homogeneity value 
     * @return n-th moment, <tt>F[n]</tt>
     */
    public double getF(int n){
        if ((n<0) || (n>6)) throw new RuntimeException("Can not handle less than zero or more than 6");
        return getUnnromalisedF(n)/this.FnNormalisation[n];
        } 
    
    public double getUnnromalisedF(int n){
        if ((n<0) || (n>6)) throw new RuntimeException("Can not handle less than zero or more than 6");
        switch (n) {
                    case 0: return kmoment[0];
                    case 1: return (  kmoment[1]); 
                    case 2: return ( (kmoment[2]-   kmoment[1])); 
                    case 3: return ( (kmoment[3]- 3*kmoment[2]+ 2*kmoment[1]));
                    case 4: return ( (kmoment[4]- 6*kmoment[3]+11*kmoment[2]-  6*kmoment[1]));
                    case 5: return ( (kmoment[5]-10*kmoment[4]+35*kmoment[3]- 50*kmoment[2]+ 24*kmoment[1]));  
                    case 6: return ( (kmoment[6]-15*kmoment[5]+85*kmoment[4]-225*kmoment[3]+274*kmoment[2]-120*kmoment[1]));  
                    default: System.out.println("Error in  getUnnromalisedFn, maxFnumber ="+maxFnumber +" wrong");
                }
        return DUNSET;
        } 
      

    /**
     * Finds minimum value in sequence.
     * @return <tt>kmin</tt>
     */
    public int getMin(){return kmin;}
    /**
     * Finds maximum value in sequence.
     * @return <tt>kmax</tt>
     */
    public int getMax(){return kmax;}
    /**
     * Finds number of values in sequence, <tt>N</tt>.
     * @return <tt>N = \sum 1</tt>
     */
    public int getNumber(){return count;}
    /**
     * Finds total of values in sequence, <tt>E= \sum k</tt>.
     * @return <tt>E=\sum k</tt>
     */
    public int getTotalValue(){return kmoment[1];}
    /**
     * Finds average value in sequence.
     * @return <tt>\sum k /\sum 1</tt>
     */
    public double getAverage(){return getMoment(1);}
    /**
     * Finds average value in sequence.
     * @return <tt>\sum k /\sum 1</tt>
     */
    public double getAverageSquared(){double a2= getMoment(1); return a2*a2;}
    /**
     * Finds standard deviation in sequence.
     * @return <tt>sigma^2 = mu(2)-(mu(1))^2</tt>
     */
    public double getVariance(){return getMoment(2)-getAverageSquared();}
    /**
     * Finds standard deviation in sequence.
     * @return <tt>sigma = sqrt(mu(2)-mu(1)^2))</tt>
     */
    public double getSigma(){return Math.sqrt(getVariance());}
    /**
     * Finds error in the average.
     * @return <tt>error = sigma/sqrt(N)</tt>
     */
    public double getError(){return getSigma()/Math.sqrt(count);}
    
    /**
     * Finds z.
     * @return <tt>z= mu(2)/mu(1) -1</tt>
     */
    public double getzValue(){return getMoment(2)/getMoment(1) -1;}
    
    /**
     * Finds entropy, <tt>S</tt>.
     * @return <tt>S= \sum p ln p</tt> where <tt>p=k/N</tt>
     */
    public double getEntropy(){return S;}
    
        


    
    /*
     * Returns a string used to label columns of data from toString() output.
     *@param sep separation character, usually tab
     */
    public String labelString(String sep)
    {
        int om = statisticsMode;
        String s="";
        if (( om &  1) >0) s=s+"N" + sep + "E" + sep + "<k>" + sep + "+/-" + sep + "k_min" + sep + "k_max"+ sep + "sigma" + sep + "z"+ sep + "S" +sep;
        if (( om &  2) >0) { for (int i=2; i<=maxMoment; i++) s=s  +"mu["+i+"]"+sep;}
        if (( om &  4) >0) { for (int i=2; i<=maxFnumber; i++) s=s  +"F["+i+"]"+sep ;}
        return s;
    }

    /** 
     * Produces a string representing all the data.
     *@param sep separation character, usually tab
     *@return a string representing all the data
     */
    public String toString(String sep)
    {
        int om = statisticsMode;
        String s="";
        if (( om &  1) >0) s=s+getNumber() + sep + getTotalValue() + sep + getAverage() + sep + getError() + sep + getMin() + sep + getMax()+ sep + getSigma() + sep + getzValue()+ sep + getEntropy() +sep;
        if (( om &  2) >0) for (int i=2; i<=this.maxMoment; i++) s=s  + this.getMoment(i)+ sep;
        if (( om &  4) >0) for (int i=2; i<=maxFnumber; i++) s=s  + this.getF(i)+ sep;
        return s;
    }

    /** 
     * Sets statistics to calculate.
     *@param generalStatsOn general statistics on
     *@param muStatsOn mu[n] (degree moment) statistics on
     *@param FnStatsOn F[n] statistics on
     *@return a string representing all the data
     */
    private int setStatisticsMode(boolean generalStatsOn, boolean muStatsOn, boolean FnStatsOn)
    {
        int cm = 0;
        if (generalStatsOn) cm += 1;
        if (muStatsOn)      cm += 2;
        if (FnStatsOn)      cm += 4;
        setStatisticsMode(cm);
        return statisticsMode;
    }

    /** 
     * Sets statistics to calculate.
     *@param cm statisticsMode value
     *@return a string representing all the data
     */
    private int setStatisticsMode(int cm)
    {
        statisticsMode = cm;
        if (statisticsMode > maxStatisticsMode)  message.printERROR("Statistics Mode "+statisticsMode+" too big, must be <"+ maxStatisticsMode);
        if (statisticsMode < 0)  message.printERROR("Statistics Mode "+statisticsMode+" negative, must be positive");
        return statisticsMode;
    }

    
    /** 
     * Produces a string representing which calculations are on.
     *@param sep separation string between entries
     *@return a string representing which calculations are on.
     */
    public String statisticsModeString(String sep)
    {
        return statisticsModeString(statisticsMode, sep);
    }

    /** 
     * Produces a string representing given mode.
     *@param om statistics mode
     *@param sep separation string between entries
     *@return a string representing which calculations are on.
     */
    static public String statisticsModeString(int om, String sep)
    {
        // (cm & 1) Basic Data, (cm & 2) Fn, (cm & 4) n(k), (cm & 8) n<=(k),  (cm & 16) rho
        String s="";
        if (( om &   1) >0) s=s+"Basic Data"+sep;
        if (( om &   2) >0) s=s+"mu[n]"+sep;
        if (( om &   4) >0) s=s+"F[n]"+sep;
        return s;
    }
     
    public int getCalculateMode()
        {
            return statisticsMode;
        }
        

    public int getMaximumMomentNumber()
        {
            return maxMoment;
        }
 
    public int getMaximumFNumber()
        {
            return maxFnumber;
        }
    
    
    
    } // eo class DDStats    
