/*
 * DDStats.java
 *
 * Created on 15 November 2006, 16:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package CopyModel;


import java.util.Random; //p524 Schildt

import cern.colt.list.IntArrayList;

import DataAnalysis.StatisticalQuantity;
import TimUtilities.TimMessage;
import TimUtilities.TimIntegerArrayList;
import TimGraph.timgraph;        
    

/** 
 * Degree Distribution Class.
 *<br>Calculates statistics of a Degree Distribution.
 *
 * @author time
 */

    
    public class DDStats
    {
     final static String VERSION = "DDStats260305";
     final static int MAXIMUMFNUMBER=6;  //maximum order of F_n to calculate
     final static int maxStatisticsMode = 1+2+4+8+16+32+64+128;
     Random Rnd = new Random();  //Schildt p524, time is used as seed
     TimIntegerArrayList TimIAL = new  TimIntegerArrayList();
     int numberIndividuals=-99; // assumed to be the number of edges
     int statisticsMode = maxStatisticsMode; // (cm & 1) Basic Data, (cm & 2) Fn, (cm & 4) n(k), (cm & 8) n<=(k),  (cm & 16) rho
     int maxrhonumber=+1;  // how many rho[?] to calculate.  <0 means do not calculate rho    
     double statisticsError=1e-3; // used to control error requested when sampling 
        

     boolean initialised =false;
     
     StatisticalQuantity Nactive; // number non-zero degree artefacts
     StatisticalQuantity kmax;
     StatisticalQuantity kav;
     StatisticalQuantity ksigma; // susceptibility
     //int nkvalues; // number of k values to follow includes 0 and E
     //int [] kvalue;
     ValueRange kvalue;
     StatisticalQuantity [] nkvalue; // degree distribution at selected values
     StatisticalQuantity [] nleqkvalue; // cummulative degree distribution at selected values
     StatisticalQuantity [] rho; // rho[0] = random neighbours, rho[L] = rho for L-th neighbours
     int maxMoment;
     StatisticalQuantity [] mu;

     int maxFnumber;       // maxF maximum order of F_n to calculate
     StatisticalQuantity [] Fn;
     StatisticalQuantity zStat;  // (<k^2>/<k> -1)
     StatisticalQuantity Fkc; // Homogeneity factor Kimura+Crow k^2 version
     StatisticalQuantity S; // entropy
     
     int Y; // maximum length of list considered
     /**
      * <code>turnover[y]</code> holds the statistics on the turnover in the top y list by degree.
      */
     StatisticalQuantity [] turnover; // turnover in <= top Y lists 
     /**
      * <code>degreeRank[r]</code> holds the statistics on the degree of vertices ranked r.
      */
     StatisticalQuantity [] degreeRank;  
     
     
     StatisticalQuantity numberComponents;
     StatisticalQuantity numberMultiComponents;
     StatisticalQuantity averageComponentSize;
     StatisticalQuantity averageMultiComponentSize;
     StatisticalQuantity numberGCCVert;
     StatisticalQuantity GCCDiam;
     StatisticalQuantity GCCDist;
     
     TimMessage message = new TimMessage(-2);
     
     /** Constructs Degree Distribution Stats using parameters of given DDStats.
      *@param dds existing DDStats from which to copy parameters
       */
        public DDStats(DDStats dds)
        {
             initialiseBasicParameters(dds.getMaximumMomentNumber(), dds.getMaximumFNumber(), dds.getkValue(), dds.getMaximumrhoNumber(), dds.Y, dds.getCalculateMode());
        }
        
        /** Constructs Degree Distribution Stats
      *@param maxmu maximum degree moment mu_n to calculate
      *@param maxF maximum order of F_n to calculate
      *@param vr ValueRange specifying the k values to be analysed
      *@param maxrhonum how many rho[?] to calculate,  <0 means do not calculate rho    
      *@param maxY maximum length of turnover list to be considered
      *@param cm calculational mode: (cm & 1) Basic Data, (cm & 2) Fn, (cm & 4) n(k), (cm & 8) n<=(k),  (cm & 16) rho
      */
        public DDStats(int maxmu, int maxF, ValueRange vr,  int maxrhonum, int maxY, int cm)
        {
            initialiseBasicParameters(maxmu, maxF, vr, maxrhonum, maxY, cm);
        }
  
        /** Initialises basic parameters of Degree Distribution Statistics
      *@param maxmu maximum degree moment mu_n to calculate
      *@param maxF maximum order of F_n to calculate
      *@param numk number of k values to follow includes 0 and E
      *@param maxrhonum how many rho[?] to calculate,  <0 means do not calculate rho    
      *@param maxY maximum length of turnover list to be considered
      *@param cm calculational mode: (cm & 1) Basic Data, (cm & 2) Fn, (cm & 4) n(k), (cm & 8) n<=(k),  (cm & 16) rho
      */
        private void initialiseBasicParameters(int maxmu, int maxF, ValueRange vr, int maxrhonum, int maxY, int cm)
        {
            maxMoment=maxmu;
            maxFnumber = maxF;
            maxrhonumber = maxrhonum;
            kvalue = new ValueRange(vr); 
            Y=maxY;
            setStatisticsMode(cm);
            
        }
        
        
      /** Initialises k values to be used.
      */
        private void initialisekValues()
        {
            // set up k values to follow
             int expectE=numberIndividuals;
             kvalue.setMinimum(0);
             kvalue.setMaximum(expectE);
             kvalue.create();
         } // end of set up of nkvalues
         
        
      /** Initialises Degree Distribution Statistics from basic parameters already set.
       * Will only do this once after created.
      */
        private void initialiseParameters()
        {
         if (initialised) return;
         initialised=true;
         initialisekValues(); 
         int warningCount=-1;
         // general values
         Nactive=new StatisticalQuantity(warningCount); // number non-zero degree artefacts
         kmax= new StatisticalQuantity(warningCount);
         kav= new StatisticalQuantity(warningCount);
         ksigma= new StatisticalQuantity(warningCount); // susceptibility
         Fkc= new StatisticalQuantity(warningCount); // Homogeneity factor Kimura+Crow k^2 version
         S= new StatisticalQuantity(warningCount); // entropy
         
         int nkvalues=kvalue.getNumberValues();
         if ( ( ( statisticsMode & 4) >0) || ( ( statisticsMode & 8) >0) ) {
             initialisekValues(); // reinitialise k values in case anything changed
             if (( statisticsMode & 4) >0) nkvalue = new StatisticalQuantity [nkvalues];
             if (( statisticsMode & 8) >0) nleqkvalue = new StatisticalQuantity [nkvalues];
             double dk = ((double) numberIndividuals)/((double)(nkvalues-1)) ;
             for (int i=0; i<nkvalues; i++) {
                 if (( statisticsMode & 4) >0) nkvalue[i] = new StatisticalQuantity(warningCount);
                 if (( statisticsMode & 8) >0) nleqkvalue[i] = new StatisticalQuantity(warningCount);
             }
         }
         
         // mu[n] set up
         if (( statisticsMode & 32) >0) {
             if ((maxMoment>MAXIMUMFNUMBER) || (maxMoment<2)) maxMoment =MAXIMUMFNUMBER;
             mu = new StatisticalQuantity [maxMoment+1]; // mu[n] = mu_n
             for (int i=0; i<=maxMoment; i++) {mu[i]= new StatisticalQuantity(warningCount); }
         } // end of set up for mu[n]
         else maxMoment=2; // always need to set up first two moments
         
         // F[n] set up
         if (( statisticsMode & 2) >0) {
             zStat = new StatisticalQuantity(warningCount); // z = (<k^2>/<k> -1)
             if ((maxFnumber>MAXIMUMFNUMBER) || (maxFnumber<2)) maxFnumber =MAXIMUMFNUMBER;
             Fn = new StatisticalQuantity [maxFnumber+1]; // Fn[n] = F_n
             for (int i=0; i<=maxFnumber; i++) {Fn[i]= new StatisticalQuantity(warningCount); }
         } // end of set up for F[n]
        
         
         
         // rho set up
         if (( statisticsMode & 16) >0) 
         {
//             if (ppMode!=1) maxrhonumber=0; // only random correlation if no individual graph
             if (maxrhonumber>-1) rho = new StatisticalQuantity [maxrhonumber+1];
             for (int i=0; i<=maxrhonumber; i++) {rho[i]= new StatisticalQuantity(warningCount); }
         } // end of rho set up    
         
         // MR stats set up
         if (( statisticsMode & 64) >0) 
         {
             numberComponents =  new StatisticalQuantity(warningCount);
             averageComponentSize =  new StatisticalQuantity(warningCount);
             numberMultiComponents =  new StatisticalQuantity(warningCount);
             averageMultiComponentSize=  new StatisticalQuantity(warningCount);
     
             numberGCCVert =  new StatisticalQuantity(warningCount);
             GCCDiam =  new StatisticalQuantity(warningCount);
             GCCDist =  new StatisticalQuantity(warningCount);
             
         } // end of MR stats set up    
        
         // turnover statistics
        if (( statisticsMode & 128) >0) 
        {
            turnover = new StatisticalQuantity[Y];
            degreeRank = new StatisticalQuantity[Y];
            for (int y=0; y<Y; y++){
                turnover[y] = new StatisticalQuantity(warningCount);
                degreeRank[y] = new StatisticalQuantity(warningCount);
            }
        }
 

         
        }

    /**
     * Calculate statistics from artefact array.
     * <p>This version uses a array of type Vertex and creates an IntArrayList for the
     * degree distribution to be used in {@link #calcStats(CopyModel.Vertex[], cern.colt.list.IntArrayList, cern.colt.list.IntArrayList, TimGraph.timgraph, int, CopyModel.Rank) }
     * <p>Turnover statistics are calculated if <tt>(statisticsMode &amp; 128) &gt; 0</tt> and
     * both <tt>artefactArray</tt> and <tt>artefactDegreeRank</tt> are both defined.
     * <p>Turnover statistics are calculated if <tt>(statisticsMode &amp; 128) &gt; 0</tt> and
     * both <tt>artefactArray</tt> and <tt>artefactDegreeRank</tt> are both defined.
     *@param artefactArray Array of type Vertex describing each artefact vertex. Null for no turnover stats.
     *@param individualArray array of integers, one per (individual) vertex to indicate the type of vertex
     *@param indNetwork timgraph network of individual vertices.
     *@param nind the number of individual vertices
     *@param artefactDegreeRank ranking of artefacts by degree. Null for no turnover stats.
     */
    public void calcStats(Vertex [] artefactArray, IntArrayList individualArray, timgraph indNetwork, int nind, Rank artefactDegreeRank)
    {
        IntArrayList artefactDegreeList = new IntArrayList() ;
        int k;
        int na=0; // number of artefacts
        for(int a=0; a<artefactArray.length; a++){
          k = artefactArray[a].degree;
          TimIAL.addExtendIntArrayList(artefactDegreeList, k, 1);         
          }
        calcStats(artefactArray, artefactDegreeList, individualArray, indNetwork, nind, artefactDegreeRank);

 
    } // eo calcStats
        
        
    /**
     * Calculate statistics from Degree Distribution.
     * <p>Turnover statistics are calculated if <tt>(statisticsMode &amp; 128) &gt; 0</tt> and
     * both <tt>artefactArray</tt> and <tt>artefactDegreeRank</tt> are both defined.
     *@param artefactArray Array of type Vertex describing each artefact vertex. If null no turnover stats.
     *@param DDArr Degree Distribution Integer Array List for artefact vertices.
     *@param individualArray array of integers, one per (individual) vertex to indicate the artefact choice made by individual
     *@param indNetwork timgraph network of individual vertices.
     *@param nind the number of individual vertices
     *@param artefactDegreeRank ranking of artefacts by degree. If null no turnover stats.
     */
    public void calcStats(Vertex [] artefactArray, IntArrayList DDArr, IntArrayList individualArray, timgraph indNetwork, int nind, Rank artefactDegreeRank)
    {
        numberIndividuals=nind;
        initialiseParameters();
        int nk=0;
        int kp=1;
        int k=0;
        int [] kmoment;
        int maxkvalue = -1;
        int [] nleqk = new int [1];
        int nkvalues= kvalue.getNumberValues();
        int nmoments = Math.max(Math.max(maxMoment,maxFnumber),2);// always need at least first two moments
        kmoment= new int [nmoments+1]; // kmoment[j] = \sum k^j
            for (int i=0; i<=nmoments; i++) kmoment[i]=0; 
        
        
            if (( statisticsMode &  8) >0)  {  // needed for label too.
                maxkvalue = kvalue.getMaximum();
                nleqk = new int [maxkvalue+1]; // cumulative distribution
            }
            if ( (( statisticsMode &  4) >0) || (( statisticsMode &  8) >0) ) {  // needed for label too.
                for (int i=0; i<nkvalues; i++ )
                    if (kvalue.get(i)< DDArr.size()) {
                    nkvalue[i].add(DDArr.get(kvalue.get(i)));
                    } else nkvalue[i].add(0);
            }
       
        int kmaxnow=-1;
        double Snow=0;
        int Nactivenow=0;
        int N0now=0;
        for (k=0; k<DDArr.size(); k++ )
        {
            nk=DDArr.get(k);
            kp=1;
            for (int i=0; i<=nmoments; i++) {kmoment[i] += nk*kp; kp*=k; } 
            if (k>0) {
                if (k>kmaxnow) kmaxnow = k;
                Snow+=nk*k*Math.log(k);
                Nactivenow+=nk;
                if ((( statisticsMode &  8) >0) && (k<=maxkvalue) ) nleqk[k]=nleqk[k-1]+nk;
            }
            else 
            {
                N0now+=nk;
                if (( statisticsMode &  8) >0) nleqk[0]=nk;
            }
        }
        int N=kmoment[0];  // number artefacts
        double Ndouble = (double) N;
// now finish cumulative distribution
        if (( statisticsMode &  8) >0) {
            for (; k<=maxkvalue; k++ ) {
                if (k>0) nleqk[k]=nleqk[k-1];
                else nleqk[0]=nk;
            }
            if (N!=nleqk[nleqk.length-1]) message.printERROR(" in calcStats, number of artefacts "+N+" != "+nleqk[nleqk.length-1]);
        }

        int E = kmoment[1];
        
        double kavnow = E/N;
        Nactive.add(Nactivenow);
        kmax.add(kmaxnow);
        kav.add(kavnow);
        ksigma.add(kmoment[2]/Ndouble - kavnow*kavnow);
        Fkc.add(kmoment[2]/((double) (E*E) ));
        S.add(Snow);
        double En =1;
        
        if (( statisticsMode &  8) >0) for (int i=0; i<kvalue.getNumberValues(); i++ ) 
            if (kvalue.get(i)< nleqk.length) 
            {
             nleqkvalue[i].add(nleqk[kvalue.get(i)]);
            } 
            else nleqkvalue[i].add(0);
        
        // update moments
        if (( statisticsMode &  32) >0) for (int i=0; i<=maxMoment; i++) mu[i].add(kmoment[i]/Ndouble);

// *** This calculates F_n
// k
// k^2-   k
// k^3- 3*k^2+ 2*k
// k^4- 6*k^3+11*k^2-  6*k
// k^5-10*k^4+35*k^3- 50*k^2+ 24*k
// k^6-15*k^5+85*k^4-225*k^3+274*k^2-120*k
        if (( statisticsMode &  2) >0) {
            zStat.add(kmoment[2]/(Ndouble*kavnow) -1);
            for (int i=0; i<=maxFnumber; i++) {
                switch (i) {
                    case 0: Fn[i].add(  kmoment[0]/En); break;
                    case 1: Fn[i].add(  kmoment[1]/En); break;
                    case 2: Fn[i].add( (kmoment[2]-   kmoment[1])/En); break;
                    case 3: Fn[i].add( (kmoment[3]- 3*kmoment[2]+ 2*kmoment[1])/En); break;
                    case 4: Fn[i].add( (kmoment[4]- 6*kmoment[3]+11*kmoment[2]-  6*kmoment[1])/En); break;
                    case 5: Fn[i].add( (kmoment[5]-10*kmoment[4]+35*kmoment[3]- 50*kmoment[2]+ 24*kmoment[1])/En);  break;
                    case 6: Fn[i].add( (kmoment[6]-15*kmoment[5]+85*kmoment[4]-225*kmoment[3]+274*kmoment[2]-120*kmoment[1])/En);  break;
                    default: System.out.println("Error in calcStats, maxFnumber ="+maxFnumber +" wrong");
                }
                En*=(kmoment[1]-i);
            }
        } // eo stat mode 2
      
     // now calc rho
        if (( statisticsMode &  16) >0) 
        {
            //  Next part is for slow sampling method to find rho[0]        
            if (maxrhonumber>-1)
        {
            message.println(2," ... Calculating rho[0] - random individuals by sampling");
            double relerr = statisticsError;
            double abserr=statisticsError;
            double abserrvalue =statisticsError;
            int maximumsamples = 100*E;
            if (maximumsamples>1000000) maximumsamples=1000000; 
            calcrho0(individualArray, relerr, abserr,  abserrvalue , 2*E, maximumsamples);
        }
        if(maxrhonumber>0) {
            message.println(2," ... Calculating rho[1] exactly");
            calcrho1(individualArray, indNetwork);
        }
        } // if (( statisticsMode &  2) >0) 
        
   // now calc MR statistics
        if (( statisticsMode &  64) >0) 
        {
            int outputMR=0;
            int infol=-2;
            String namert ="MRStats";
            String dname = "/PRG/networks/CopyModel/output/";
            boolean makeDirected=false;
            boolean makeLabelled=false;
            boolean makeWeighted=false;
            int maxVertices=N; //number artefacts
            // note timgraph constuctor shuffles stub list, must give it a deep copy - or rewrite using a perm?
            int [] stubList = new int[numberIndividuals];
            for (int i=0; i<numberIndividuals; i++) stubList[i]=individualArray.getQuick(i);
            timgraph tg = new timgraph(namert, dname, infol, outputMR, makeDirected, makeLabelled, makeWeighted, maxVertices, stubList);
            tg.calcComponents();
            numberComponents.add(tg.getNumberComponents());
            averageComponentSize.add(tg.getAverageComponentSize());
            numberMultiComponents.add(tg.getNumberMultiComponents());
            averageMultiComponentSize.add(tg.getAverageMultiComponentSize());
     
            numberGCCVert.add(tg.getNumberGCCVertices());
            GCCDiam.add(tg.getGCCDiameter());
            GCCDist.add(tg.getGCCDistance());
        }
        
       
             
        
        // now calc turnover statistics
        if (( statisticsMode & 128) >0 && artefactArray!=null && artefactDegreeRank!=null)
        {
            artefactDegreeRank.update(N, artefactArray);
            for (int y=0; y<artefactDegreeRank.Y; y++){
                int t=artefactDegreeRank.getTurnover(y);
                turnover[y].add(t);
                degreeRank[y].add(artefactArray[artefactDegreeRank.getIndex(y)].degree);
            }
        }

    } // eo calcStats
    
    
        

        /*
     * Calc rho[0] - random individuals, by random sampling.
     * Calculates rho[0] within tolerances given. Does not use individual graph.
     * Will does it for distinct individuals only so should match (1-F[2]).
     *@param individualArray array of integers, one per (individual) vertex to indicate the type of vertex
     *@param relerr relative error to achieve
     *@param abserr absolute error to achieve
     *@param abserrvalue value of modulus below (above) which to use abolute (relative) error 
     *@param samplefreq no of measurements to make between each test
     *@param maxsamples maximum samples to make (or will always make 2*samplefreq)
     */
    public void calcrho0(IntArrayList  individualArray, double relerr, double abserr,  double abserrvalue ,  int samplefreq,  int maxsamples) {
        if (maxrhonumber<0) maxsamples =-1;
        int edge, source, target, sourceArtefact, targetArtefact;
        int nn;
        int smax = maxsamples/samplefreq;
        if (smax<2) smax=2;
        double oldrho0=-99999;
        //final boolean indNetworkOn = (indNetwork == null ? false : true);
        for (int s=0; s<smax; s++) {
            for (int i=0; i<samplefreq; i++) {
                 // first choose random edge and source of that edge
                 source = Rnd.nextInt(numberIndividuals); // indNetwork.getRandomVertex();
                 target=source;
                 while (target==source) target = Rnd.nextInt(numberIndividuals); //indNetwork.getRandomVertex();
                 sourceArtefact = individualArray.getQuick(source);
                 targetArtefact = individualArray.getQuick(target);
                 if (sourceArtefact==targetArtefact) rho[0].add(0); else rho[0].add(1);// find random correlation
//                 message.println(2,"s,t, s artefact, t artefact: "+source+sep+target+sep+sourceArtefact+sep+targetArtefact);
            }// eo for i
           message.println(2," rho[0] measured "+rho[0].getCount()+", result "+rho[0].getAverage()+", old result "+oldrho0);
            if ((s>0) && (testSample(rho[0].getAverage(),oldrho0,relerr,abserr,abserrvalue))) break;
            oldrho0 = rho[0].getAverage();
            }// eo for s
        
    }// eo calcrho0

    /*
     * Calc rho[1] i.e. nearest individual by sample all neighbours.
     *@param individualArray array of integers, one per (individual) vertex to indicate the type of vertex
     *@param indNetwork timgraph network of individual vertice.
     */
    public void calcrho1(IntArrayList individualArray, timgraph indNetwork) {
        int source, target, sourceArtefact, targetArtefact;
        int nn;
        int ne = indNetwork.getNumberStubs();
        for (int edge=0; edge< ne ; edge++) {
                source = indNetwork.getVertexFromEdge(edge++);
                sourceArtefact = individualArray.getQuick(source);
                target = indNetwork.getVertexFromEdge(edge);
                targetArtefact=individualArray.getQuick(target);
                if (sourceArtefact==targetArtefact) rho[1].add(0); else rho[1].add(1);
        }// eo for edge
    }// eo calcrho1
    
    /*
     * Tests to see if new result is within tolerances of old result.
     *@param newres the new value
     *@param oldres the previous result
     *@param relativeError the relative error required so |1-old/new| must be less than this
     *@param absoluteError the absolute error required so |new-old| must be less than this 
     *@param maximumAbsoluteValue if mod(new) is less than this value use absolute error
     *@param true if error is within tolerances else return zero. 
     *@return result true if average is within tolerances given.
     */        
    public boolean testSample(double newres, double oldres, double relativeError, double absoluteError, double maximumAbsoluteValue)
    {
        
        boolean result=false;
        double error = Math.abs(newres-oldres);
           if (Math.abs(newres) <  maximumAbsoluteValue) 
           {if (error<absoluteError) result= true;}
           else {if (error<relativeError*newres) result= true;}
           return result;

    }
    
    
    /*
     * Returns a string used to label columns of data from toString() output.
     *@param sep separation character, usually tab
     */
    public String labelString(String sep)
    {
        int om = statisticsMode;
        String s="";
        String SpmS = sep + "sigma" + sep;
        if (( om &  1) >0) s=s+"Nactive" + SpmS + "kmax_av_over_runs)" + SpmS + "MAX_k_all_runs"+sep+ "kav" + SpmS + "ksigma" + SpmS + "Fkc" + SpmS + "S" +SpmS;
        if (( om &  2) >0) { s=s  +"z" + SpmS; for (int i=2; i<=maxFnumber; i++) s=s  +"F_"+i + SpmS;}
        if (( om &  4) >0) for (int i=0; i<kvalue.getNumberValues(); i++) s=s + "n_"+kvalue.get(i) + SpmS;
        if (( om &  8) >0) for (int i=0; i<kvalue.getNumberValues(); i++) s=s + "ncumm_"+kvalue.get(i) + SpmS;
        if (( om & 16) >0) {
            if (maxrhonumber>-1) s=s + "rho_rnd" + SpmS;
            for (int i=1; i<=maxrhonumber; i++) s=s + "rho_"+i + SpmS;
        }
        if (( om &  32) >0) for (int i=2; i<=maxMoment; i++) s=s  +"mu_"+i + SpmS;
        if (( om &  64) >0) {s=s  +"No.Comp."+SpmS+"Av.Comp.Size"+SpmS +"No.M.Comp."+SpmS+"Av.M.Comp.Size"+SpmS +"No.GCC Vert"+SpmS+"GCC Diam." +SpmS+"GCC Dist." +SpmS;}
        if (( om & 128) >0) for (int y=0; y<turnover.length; y++) s=s + "Y_"+(y+1) + SpmS+ "k_"+(y+1) + SpmS;
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
        if (( om &  1) >0) s=s+Nactive.avErrString(sep) + sep + kmax.avErrString(sep) + sep + kmax.getMaximum() + sep + kav.avErrString(sep) + sep + ksigma.avErrString(sep) +  sep + Fkc.avErrString(sep) + sep + S.avErrString(sep)+ sep;
        if (( om &  2) >0) {
            s=s + zStat.avErrString(sep)+ sep;
            for (int i=2; i<=maxFnumber; i++) s=s  + Fn[i].avErrString(sep)+ sep;
        }
        if (( om &  4) >0) for (int i=0; i<kvalue.getNumberValues(); i++) s=s  + nkvalue[i].avErrString(sep)+ sep;
        if (( om &  8) >0) for (int i=0; i<kvalue.getNumberValues(); i++) s=s + nleqkvalue[i].avErrString(sep)+ sep ;
        if (( om & 16) >0) for (int i=0; i<=maxrhonumber; i++) s=s + rho[i].avErrString(sep)+ sep;
        if (( om & 32) >0) for (int i=2; i<=maxMoment; i++)  s=s + mu[i].avErrString(sep)+ sep;
        if (( om & 64) >0) s=s  + numberComponents.avErrString(sep)+ averageComponentSize.avErrString(sep) + sep + numberMultiComponents.avErrString(sep)+ sep+ averageMultiComponentSize.avErrString(sep) + sep+ numberGCCVert.avErrString(sep)+ sep+ GCCDiam.avErrString(sep)+ sep+ GCCDist.avErrString(sep)+ sep; //  for (int i=2; i<=maxMoment; i++)  s=s + sep+ mu[i].avErrString(sep);
        if (( om &128) >0) for (int y=0; y<turnover.length; y++) s=s  + turnover[y].avErrString(sep)+ sep+ degreeRank[y].avErrString(sep)+ sep;
        return s;
    }

    /** 
     * Sets statistics to calculate.
     *@param generalStatsOn general statistics on
     *@param muStatsOn mu[n] (degree moment) statistics on
     *@param FnStatsOn F[n] statistics on
     *@param nkStatsOn n(k) statistics on
     *@param ngeqkStatsOn n<=(k) statistics on
     *@param rhoStatsOn rho statistics on
     *@param MRProjOn Molloy-Reed projection statistics on
     *@param turnOverOn Study turnover in top Y lists on
     *@return a string representing all the data
     */
    private int setStatisticsMode(boolean generalStatsOn, boolean muStatsOn, boolean FnStatsOn, boolean nkStatsOn, boolean ngeqkStatsOn, boolean rhoStatsOn, boolean MRProjOn, boolean turnoverOn)
    {
        int cm = 0;
        if (generalStatsOn) cm += 1;
        if (FnStatsOn)      cm += 2;
        if (nkStatsOn)      cm += 4;
        if (ngeqkStatsOn)   cm += 8;
        if (rhoStatsOn)     cm += 16;
        if (muStatsOn)      cm += 32;
        if (MRProjOn)       cm += 64;
        if (turnoverOn)     cm +=128;
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
        if (( om &   2) >0) s=s+"z,F[n]"+sep;
        if (( om &   4) >0) s=s+"n(k)"+sep;
        if (( om &   8) >0) s=s+"n<=(k)"+sep;
        if (( om &  16) >0) s=s+"rho[n]"+sep;
        if (( om &  32) >0) s=s+"mu[n]"+sep;
        if (( om &  64) >0) s=s+"MR"+sep;
        if (( om & 128) >0) s=s+"TopY"+sep;
        return s;
    }
     
            public int getCalculateMode()
        {
            return statisticsMode;
        }
        
        public ValueRange getkValue()
        {
            return kvalue;
        }

        
//        /** Check number of k values.
//         * Make sure it is between three and (initial number of individuals+1).
//         *@param initialIndividuals the initial number of individuals
//         */
//        public void checkNumberkValues(int initialIndividuals)
//        {
//        if ((nkvalues<0) || (nkvalues>initialIndividuals)) nkvalues = initialIndividuals+1;
//            if (nkvalues<2)  nkvalues = 3; 
//        }
//        

        public int getMaximumMomentNumber()
        {
            return maxMoment;
        }
 
        public int getMaximumFNumber()
        {
            return maxFnumber;
        }
    
        public int getMaximumrhoNumber()
        {
            return maxrhonumber;
        }
    
    
    } // eo class DDStats    
