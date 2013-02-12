package CopyModel;
/*
 * CopyModel class implements the copying models.
 * @author time
 */

//    import java.util.*;
    import java.util.Date;
    import java.util.Random; //p524 Schildt
//import java.util.*;
//    import java.util.AbstractSet;
//import java.lang.Object.*;
//import java.lang.Math.*;
     import java.io.IOException;
     import java.io.File;
     import java.io.FileNotFoundException;
     import java.io.FileOutputStream;
     import java.io.PrintStream;
     
//    import java.awt.image.MemoryImageSource;
//    import java.awt.Image;
    import java.awt.image.BufferedImage;
//    import java.awt.Toolkit;
    import javax.imageio.ImageIO;
    
    import DistributionAnalysis.DistributionAnalysis;
    import cern.colt.list.IntArrayList;
//    import cern.colt.list.DoubleArrayList;
//import cern.colt.list.ObjectArrayList;
    
    import DataAnalysis.StatisticalQuantity;
    
//    import JavaNotes.TextReader;
    
    import TimGraph.timgraph;
    import TimGraph.RandomWalk;
    
    import TimUtilities.BooleanAsString;
    import TimUtilities.StringAsBoolean;
    import TimUtilities.TimMessage;
    import TimUtilities.ParameterInput;
    import TimUtilities.TimTiming;  // Weird IDE finds methods but compiler finds some but not others unless put in same package
    import TimUtilities.Permutation;

/**
 * CopyModel class implements the copying models.
 * <br>Simulates Copy Models a la Bentley and related generalised bipartite rewiring.
 * These include the more general Urn type of model.
 * <p>The main routine used to run the model is {@link #runGeneralModel() }
 * @author time
 */
public class CopyModel {
    
    final static String VERSION = "CM110316";
    final static String SEP = "\t "; // separation character e.g. \t for tab
    final static Character CC = '#'; // use this to start comment lines
    final static String COMMENTSTRING = CC.toString(); // use this to start comment lines
    final static Character PARAM = '-'; // use this to start a parameter
    final static int IUNSET = -24680; // use to flag unset int
    final static int INDNOTATTACHED = -9876; // use to flag an individual that is not attached to any artefact
    final static int DISPLAYREWIRING=20; // if fewer individuals than this then detailed ino on each rewiring given depending on infolevel used
    /**
     * Individual used to copy artefact from.
     * <p>If outside allowed range 0 to (numberIndividuals-1)
     * then no copying from an individual happened.
     * <p>Global variable used to return extra information from some routines
     * where sometimes this is used, sometimes not.
     * <p>{@link CopyModel#chooseArtFromInd(int, int) }
     */
    private static int COPYFROMIND =-26374859;
    Date date = new Date();
    Random Rnd;
    String inputNameRoot = "test";
    boolean getInputFile = false; // true if we have been given an input file name        
    String outputNameRoot = "test";
    String dirRoot= "/PRG/networks/CopyModel/";
    String dirNameOutput = dirRoot+ "output/";
    String dirNameInput = dirRoot+ "input/";
    int infolevel =0;
    TimMessage message;
    
//    int outputcontrol =1+16+32;
    OutputMode outputControl = new OutputMode(1+16+32+64);
    RewireMode rewireMode = new RewireMode(6); // Selects type of model run
    //boolean modeBentley;
    int ppMode = 0; // Selects manner of pPref event update
    int prMode = 0; // Selects manner of pRand event update
    double initialtime;
    int repeat=1; // number of times to repeat run.
    
    boolean addIndToArtError=false;

        int maxNumberArtefacts=0; // automatic choice //1010;
        int maxNumberIndividuals=0; // automatic choice //1010;
        int numberIndividuals = 5;
        int numberArtefacts = 5;
        int numberActiveArtefacts =0;
        int initialIndividuals = numberIndividuals;
        int initialArtefacts = numberArtefacts;
        // Timing Inputs then derived
        int numberRewiringsTotal=numberIndividuals*3; // total number of rewirings
        int numberRewiringsPerUpdate=numberIndividuals; // total number of rewirings in one (weighting) interval
        int numberRewiringsPerEvent = numberIndividuals/2; // /2; // number of rewirings in one event
        /**
         * Number of rewirings to make before taking the first statistics update
         */
        int numberRewiringsFirstUpdate =0;
        
        int numEventsTotal=-1; // one event is one update of network
        int numEventsPerUpdate= -1; // number of events before updating statistics
        /**
         * Total number of update intervals
         */
        int numUpdates = -1; // number of last update so the number of updates is (numUpdates+1);
        /**
         * Number of times statistics are actually updated .
         */
        int numStatsUpdates = -1; // number of last update so the number of updates is (numUpdates+1);
        /**
         * Number of events to have before taking the first statistics update
         */
        int numEventsFirstUpdate=0;
        
        IntArrayList individualArray;  // stores the artefact chosen by each individual
        IntArrayList individualArray2; // used in True Bentley to record future choice of individual
        Vertex[] artefactArray; // if needed stores information on artefacts.  Depending on mode it may or may not include its individual neighbours
        // Possibly declare next as type Vertex rather than keep full list of neighbours and rewiremode flag
        Vertex InactiveIndividuals; // use an artificial artefact vertex to indicate all inactive individuals
        Vertex ActiveIndividuals; // use an artificial  vertex to indicate active individuals
        IntArrayList activeArtefacts; // used to keep track of active artefacts
        //int turnoverY = 0; // Follow top Y ranked list, if <=0 then no calculated
        Rank artefactDegreeRank;
//        boolean artefactNeighbourList = false; // true if using artefact vertices with neighbour lists
        int[][] artefactDegreeEvolution; // artefactDegreeEvolution[w][a] records degree of artefact a at time interval w
        int initialBiGraph = 0;
        
        EventProbability prob;
        
// Individual Network parameters used for pPref updates
        boolean indNetworkOn =false;
        String [] indNetworkArgs = new String[100];
        int indNetworkNumberArgs=0;
        double indRndWalkLength = 2.0;  //  next two are for walks on the individual network
        int indRndWalkMode=3;
        timgraph indNetwork;  // graph between individuals only
        RandomWalk indRandomWalk;  // random walk to use on individual graph

// Artefact Network parameters used for pRand updates
        boolean artNetworkOn =false;
        String [] artNetworkArgs = new String[100];
        int artNetworkNumberArgs=0;
        double artRndWalkLength = 2.0;  //  next two are for walks on the individual network
        int artRndWalkMode=3;
        timgraph artNetwork;  // graph between individuals only
        RandomWalk artRandomWalk;  // random walk to use on artefact graph



        /*
         * Influence network degree distribution statistics
         */

        /**
         * Array used to store individual influence network.
         * <p>The influence network is the directed network of individuals in which
         * the directed edges point from each individual to the last person
         * they copied from.  The array records the target vertex for given
         * source vertex (out degree is always 1) and if this target vertex index is
         * outside the valid range then no copying was done.
         * <p><tt>indCopiedFrom[i]</tt> is the index of the individual last copied from.
         */
        int [] indCopiedFrom;
        boolean inflNetworkOn =false;
        /**
         * Degree distribution of Influence Network..
         * For details on influence network see {@link #indCopiedFrom}.
         */
        StatisticalQuantity degreeInfl; //StatQuant artDegree;
        IntArrayList inflDDArr;
        int degreeInflCont =-1;
        int numberLeaders=0;
        boolean inflDDStatsOn=false;
        DDStats inflDDStatsTemplate; // use as template for statistics to keep
        DDStats [] inflDDStatsArr;  // Degree Distribution statistics for each update time
//        int inflnkbottom; // next three to specify n(k) statistics ranges
//        int inflnkinterval;
//        int inflnktop;
        double inflStatisticsError=1e-3; // used to control error requested when sampling
//        Rank influenceDegreeRank;
//        Vertex[] influenceArray; // if needed stores information on influence.  Depending on mode it may or may not include its individual neighbours


        /*
         * Artefact degree distribution statistics
         */
        StatisticalQuantity artDegree; //StatQuant artDegree;
        IntArrayList artDDArr;
        boolean artDDStatsOn=false;
        DDStats artDDStatsTemplate; // use as template for statistics to keep
        DDStats [] artDDStatsArr;  // Degree Distribution statistics for each update time
//        int artnkbottom; // nbext three to specify n(k) statistics ranges
//        int artnkinterval;
//        int artnktop;
//        int maxForder=6;  // maxF maximum order of F_n to calculate
//        int numberkvalues=11;  // number of k values to follow includes 0 and E
//        int maxrhonumber=+1;  // how many rho[?] to calculate.  <0 means do not calculate rho    
//        int statisticsMode=1+2+4+8+16; // calculate all statistics
        double artStatisticsError=1e-3; // used to control error requested when sampling
        int degreeArtCont =-1;
//        IntArrayList artDDTotArr;
        StatisticalQuantity weightArt; // StatQuant weightArt; 
        
        IntArrayList artWDArr;
//        IntArrayList artWDTotArr; // could be used for multgiple runs
        int weightArtCont =-1;
        int totalWeight;

    
    
    /** Creates a new instance of CopyModel */
        public CopyModel()
        {
            int maxMu=6;  // maxMu maximum moment mu_n calculate
            int maxF=6;  // maxF maximum order of F_n to calculate
            ValueRange vr = new ValueRange(0,0,numberIndividuals/10,numberIndividuals,numberIndividuals);  // number of k values to follow includes 0 and E
            int maxrhonum=-1;  // how many rho[?] to calculate.  <0 means do not calculate rho    
            int statMode=1+64; // which statistics to calc
            int maxY=-1;
            artDDStatsTemplate = new DDStats(maxMu,maxF,vr,maxrhonum,maxY,statMode) ; // use as template for statistics to keep
            inflDDStatsTemplate = new DDStats(maxMu,maxF,vr,maxrhonum,maxY,statMode) ; // use as template for statistics to keep
            //setInitTime();
            Rnd = new Random(); //Schildt p524, time is used as seed
                if (infolevel>1) System.out.println("Uses time to seed Rnd");
            prob = new EventProbability(0.5,0.5);
            message =new TimMessage(infolevel);
        }
        
 
    /**
     * This will run CopyModel.
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        CopyModel cm = new CopyModel();
        System.out.println("\n***********************************************************");
        System.out.println("       STARTING CopyModel version "+ CopyModel.VERSION+" on "+cm.date+"\n");
        if (cm.parseParam(args)>0) return;
        cm.message =new TimMessage(cm.infolevel);
        if (cm.getInputFile) {if (cm.parseParameterFile()>0) return;}
        cm.runGeneralModel() ;    
    }
    
    /**
     * Runs a general version of the model many times.
     * <p>This is the <b>primary routine</b> used to run the model.
     * Global variable <tt>repeat</tt> sets how many times to run.
     */
    public void runGeneralModel() {
        if ((rewireMode.twoSteps) ^ (ppMode==3)) {System.err.println("Two step modes must have ppMode 3 but mode is "+ppMode+", "+ppModeString()); return;}
        if ((rewireMode.twoSteps) && (numberRewiringsPerEvent>=initialIndividuals)) {System.err.println("Two step modes must have fewer rewirings per event than individuals but "+numberRewiringsPerEvent+" not < "+initialIndividuals); return;}
        message.println(-1,"--- Running general copy model ");
//        final double dnumEventsTotal = (double) numEventsTotal;
        String basicName= outputNameRoot;
        
        // set up stuff for progress indicator
        int eventnotetemp = numEventsTotal+1; //should give no progress indication on screen
        double eventnotefactor=0.01;
        if (eventnotefactor >0) eventnotetemp = (int) ( ((double)numEventsTotal) * eventnotefactor);
        if (eventnotetemp<1) eventnotetemp=1;
//        final int eventnote = eventnotetemp;
        //final int eventnoteten = 10*eventnote;
            
        
        //if ((outputcontrol & 32) >0 ) artDDStatsArr = new DDStats [numStatsUpdates];
        //boolean updateArtDDStats = false;
        //boolean updateInflDDStats = false;
        if (!inflNetworkOn && inflDDStatsOn) {
            System.err.println("!!! Warning:- no influence network, no influence degree distribution stats collected.");
            inflDDStatsOn=false;
        }
        if (outputControl.allRunStatisticsOn )
        {
            if (artDDStatsOn) { artDDStatsArr = new DDStats [numStatsUpdates];
                                //updateArtDDStats=true;
            }
            if (inflDDStatsOn ) {
                inflDDStatsArr = new DDStats [numStatsUpdates];
                //updateInflDDStats=true;
            }
        }
        else
        {
            if (artDDStatsOn)  {
                System.err.println("!!! Warning:- no run statistics collected, no artefact degree distribution evolution stats collected.");
                artDDStatsOn=false;
            }
            if (inflDDStatsOn)  {
                System.err.println("!!! Warning:- no run statistics collected, no influence degree distribution evolution stats collected.");
                inflDDStatsOn=false;
            }
        }

//        boolean updateInflDDStats = false;
//        if (outputControl.allRunStatisticsOn )
//        {
//            inflDDStatsArr = new DDStats [numStatsUpdates]; updateInflDDStats=true;
//        }

        boolean generalFileOutput = false;
        boolean runIndicator = false;
        int event =-IUNSET;
        // these are set one time only
        //final boolean multiplePermutationModel = rewireMode.multiplePermutationModel;
        //final boolean multipleSequentialModel = rewireMode.multipleSequentialModel;
        //final boolean activeArtefactList = rewireMode.activeArtefactList;
        int rewireModeNumber = rewireMode.number;

        IntArrayList indrwList = new IntArrayList(1); // default to avoid compilation warnings
            if ((rewireMode.multiplePermutationModel) || (rewireMode.multipleSequentialModel)) 
                indrwList = new IntArrayList(numberRewiringsPerEvent);
            if (rewireMode.artefactRewiring) indrwList = new IntArrayList(maxNumberArtefacts);
            
//            if (rewireMode.activeIndividualList) ActiveIndividuals = new FullVertexIAL();
//            else ActiveIndividuals = new Vertex();
//            
//            if (rewireMode.inactiveIndividualList) InactiveIndividuals = new FullVertexIAL();
//            else InactiveIndividuals = new Vertex();
            
//            if (rewireMode.activeArtefactList) activeArtefacts = new IntArrayList(this.maxNumberArtefacts);
//            else activeArtefacts = new IntArrayList(1); // this last to avoid warnings
            
        Permutation perm = new Permutation(1); // dummy value to avoid warnings when not used

        // now do repeat number of runs
        TimTiming timing= new TimTiming();    
        for (int run=0; run<repeat; run++) {
            runIndicator=false;
            if (run<10) runIndicator=true;
            else if ((run<100) && (run%10 ==0)) runIndicator=true;
            else if ((run<1000) && (run%100 ==0)) runIndicator=true;
            if ((infolevel>-2) && runIndicator) System.out.println("\n *** Run # "+run+" of "+repeat+" **************************************** ");
            
            event=0;
            if ((run>0) && (infolevel>-1)) infolevel=-1;
            if (repeat>0) outputNameRoot = basicName + "r"+run;
            initialiseModel(initialIndividuals , initialArtefacts, 0,0);
            if (rewireMode.permutation) perm = new Permutation(numberIndividuals); 

            if (run==0) FileOutputParameters(COMMENTSTRING, "t"+numEventsTotal);
// Output information if required
            if (infolevel>-1) {
                printParameters("");
                if ((numberIndividuals<DISPLAYREWIRING)  || ((infolevel & 1) == 1)) {
                    printIndividuals(System.out, " ",false);
                    //if (rewireMode.artefactNeighbourList) setArtefactNeighbours();
                    printArtefacts(System.out, " ", SEP, false);
                }
            }
            if (infolevel>1) System.out.println("Number Artefacts vs array length"+SEP+numberArtefacts+SEP+artefactArray.length);
            if (infolevel>-1) System.out.print("*** Adding artefacts:\n");
            
            
            // calculate initial stats if needed
            int statsUpdateNumber =0;
            if (outputControl.allRunStatisticsOn && this.numEventsFirstUpdate==0) {
                if (artDDStatsOn) {
                    if (run==0) artDDStatsArr[statsUpdateNumber] = new DDStats(artDDStatsTemplate);
                    calcArtefactDegreeDistribution(); // make sure artDDArr is up to date
                    artDDStatsArr[statsUpdateNumber].calcStats(artefactArray, artDDArr, individualArray, indNetwork, numberIndividuals, artefactDegreeRank);
                }
                if (inflDDStatsOn) {
                    if (run==0) inflDDStatsArr[statsUpdateNumber] = new DDStats(inflDDStatsTemplate);
                    this.calcInfluenceDegreeDistribution(); // make sure inflDDArr is up to date
                    inflDDStatsArr[statsUpdateNumber].calcStats(null, inflDDArr, individualArray, indNetwork, numberIndividuals, null);
                }

                //if (run==0) inflDDStatsArr[statsUpdateNumber] = new DDStats(inflDDStatsTemplate);
                //calcInfluenceDegreeDistribution(); // make sure inflDDArr is up to date
                //inflDDStatsArr[statsUpdateNumber].calcInflStats(artefactArray, artDDArr, individualArray, indNetwork, numberIndividuals, artefactDegreeRank);
               statsUpdateNumber++;
            }
            if (outputControl.allRunStatisticsOn && this.numEventsFirstUpdate==0) {

            }
            int artadd=-1;
            int artrem=-1;
            int res =0;
            int w =-1;
            int indrw=numberIndividuals; // used in sequential modes
            while (event<numEventsTotal) {
                    switch (rewireModeNumber) {
                        case 0:  // Fisher-Wright (True Bentley, rewire all simultaneously)
                            for (w =numEventsPerUpdate; w>0; w-- ) artadd=trueBentleyRewire(); break;
                        case 1: // Pseudo Bentley (in order from individual 0)
                            for (w =numEventsPerUpdate; w>0; w-- ) {
                            artadd=rewire(--indrw); if (indrw<=0) indrw=numberIndividuals; }
                            break;
                        case 2: //Pseudo Random Bentley (in order from a different permutation of individuals each generation)
                            for (w =numEventsPerUpdate; w>0; w-- ) {
                            artadd=rewire(perm.next()); if (perm.getLeft() ==0) perm.newPermutation(); }
                            break;
                            //message.printERROR("rewire mode 2 unknown in runGeneralModel"); break;
                        case 3:  // Moran Model - individuals at random, simple options, ultra-fast
                            rewireNoneBentley(numEventsPerUpdate); break;
                        case 14:  // Multiple Sequential Model in two steps
                        case 4:  // Multiple Sequential Model simultaneous rewiring
                            //(X individuals removed in numerical order: i, (i+1) mod E , .. ((i+X) mod E))
                            for (w =numEventsPerUpdate; w>0; w-- ) {
                                indrwList.clear();
                                if (numberIndividuals<DISPLAYREWIRING) printActiveArtefacts(System.out, "ACT- ", " ");
                                if (numberIndividuals<DISPLAYREWIRING) printActiveIndividuals(System.out, "IND- ", " ");
                                for (int i=0; i<numberRewiringsPerEvent; i++)
                                {
                                    indrwList.add(--indrw); if (indrw<=0) indrw=numberIndividuals;
                                }
                                rewireList(indrwList);
    //                            res= checkArtefactDegreeDistribution();
                            }
                        break;
                        case 15: //Multiple Permutation Model  in two steps
                        case 5:  //Multiple Permutation Model  simultaneous rewiring
                            //(X individuals from permutation)
                            for (w =numEventsPerUpdate; w>0; w-- ) {
                                indrwList.clear();
                                for (int i=0; i<numberRewiringsPerEvent; i++)
                                {
                                    indrwList.add(perm.next()); if (perm.getLeft() ==0) perm.newPermutation();
                                }
                                if (numberIndividuals<DISPLAYREWIRING) printActiveArtefacts(System.out, "ACT- ", " ");
                                if (numberIndividuals<DISPLAYREWIRING) printActiveIndividuals(System.out, "IND- ", " ");
                                rewireList(indrwList);
                                res = checkArtefactDegreeDistribution();
                             }
                        break;
                        case 16: //Multiple Random Model (X different individuals at random, new permutation each event)  in two steps
                        case 6:  //Multiple Random Model (X different individuals at random, new permutation each event)
                            //(X individuals from permutation)
                            for (w =numEventsPerUpdate; w>0; w-- ) {
                                indrwList.clear();
                                perm.newPermutation();
                                for (int i=0; i<numberRewiringsPerEvent; i++)
                                {
                                    indrwList.add(perm.next()); if (perm.getLeft() ==0) perm.newPermutation();
                                }
                                if (numberIndividuals<DISPLAYREWIRING) printActiveArtefacts(System.out, "ACT- ", " ");
                                if (numberIndividuals<DISPLAYREWIRING) printActiveIndividuals(System.out, "IND- ", " ");
                                rewireList(indrwList);
                                res = checkArtefactDegreeDistribution();
                             }
                        break;
                        case 17: // Multiple Random Model (X random individuals, may be used twice)  in two steps
                        case 7:  // Multiple Random Model (X random individuals, may be used twice)
                            for (w =numEventsPerUpdate; w>0; w-- ) {
                                indrwList.clear();
                                if (numberIndividuals<DISPLAYREWIRING) printActiveArtefacts(System.out, "ACT- ", " ");
                                if (numberIndividuals<DISPLAYREWIRING) printActiveIndividuals(System.out, "IND- ", " ");
                                for (int i=0; i<numberRewiringsPerEvent; i++) indrwList.add(Rnd.nextInt(numberIndividuals));
                                rewireList(indrwList);
    //                            res= checkArtefactDegreeDistribution();
                            }
                        break;
                        
                        case 8: // Rewire all edges of one artefact simultaneously.
                        case 18: // Rewire all edges of one artefact in two steps.
                                // For this we need to keep an artefact neighbour list
                                // i.e. artefactArray[] must be array of FullVertexIAL
                            for (w =numEventsPerUpdate; w>0; w-- ) {
                                indrwList.clear();
                                if (numberIndividuals<DISPLAYREWIRING) printActiveArtefacts(System.out, "ACT- ", " ");
                                if (numberIndividuals<DISPLAYREWIRING) printActiveIndividuals(System.out, "IND- ", " ");
                                artrem = activeArtefacts.get(Rnd.nextInt(activeArtefacts.size()));
                                //System.out.println("removing artefact "+artrem+", degree "+artefactArray[artrem].degree);
                                for (int i=0; i<artefactArray[artrem].degree; i++) indrwList.add(artefactArray[artrem].getNeighbourQuick(i));
                                rewireList(indrwList);
                            } // eo for w
                        break;
                        
                        default: message.printERROR("rewire mode "+rewireMode+" unkown in runGeneralModel"); return;
                    }
//              }// eo else
                event+=numEventsPerUpdate; // update events counter                    
                if (event<numEventsFirstUpdate) continue;  // only start looking at stats from this time
                
                // Update the statistics and output some if required                
                
                // should the weight be updated after every event or at every update?
                for (int a=0; a<numberArtefacts; a++) artefactArray[a].weight+=artefactArray[a].degree;

                    
                if ( (outputControl.distributionsEachTimeOn ) && (event<numEventsTotal) ) generalFileOutput=true; else generalFileOutput=false;
                if (generalFileOutput || artDDStatsOn || inflDDStatsOn) {
                    calcArtefactDegreeDistribution(); // make sure artDDArr is up to date
                    if ( generalFileOutput ) FileOutputGeneral("t"+event*numberRewiringsPerEvent);
                    if ( artDDStatsOn  ) {
                        if (run==0) artDDStatsArr[statsUpdateNumber] = new DDStats(artDDStatsTemplate);
                        // update artefact degree ranking if needed
                        artDDStatsArr[statsUpdateNumber].calcStats(artefactArray, artDDArr, individualArray, indNetwork, numberIndividuals, artefactDegreeRank);
                        if ((infolevel>0)&& (artDDStatsTemplate.Y>0))
                        {
                            artefactDegreeRank.printList(System.out,SEP, artefactArray);
                            artefactDegreeRank.printTurnover(System.out,SEP);
                        }
                    }
                    if (this.inflNetworkOn) calcInfluenceDegreeDistribution(); // make sure artDDArr is up to date
                    if ( inflDDStatsOn  ) {
                        if (run==0) inflDDStatsArr[statsUpdateNumber] = new DDStats(inflDDStatsTemplate);
                        // update artefact degree ranking if needed
                        inflDDStatsArr[statsUpdateNumber].calcStats(null, inflDDArr, individualArray, indNetwork, numberIndividuals, null);
//                        if ((infolevel>0)&& (inflDDStatsTemplate.Y>0))
//                        {
//                            influenceDegreeRank.printList(System.out,SEP, influenceArray);
//                            influenceDegreeRank.printTurnover(System.out,SEP);
//                        }

                    }
                }


                if (outputControl.artefactDegreeEvolutionOn){
                    for (int a=0; a< maxNumberArtefacts; a++) artefactDegreeEvolution[statsUpdateNumber][a]=(a<numberArtefacts?artefactArray[a].degree:0);
                }
                statsUpdateNumber++;
               
            }//eo while (event<numEventsTotal)
        
            message.println(1,"\n Final Number Artefacts "+numberArtefacts+" in "+timing.runTimeString());
            
            calcArtefactDegreeDistribution(); // make sure artDDArr is up to date
            FileOutputGeneral("t"+event*numberRewiringsPerEvent);
            
            // Checks
            if (infolevel > -1) {
                if (rewireMode.activeArtefactList) {
                    for (int a = 0; a < activeArtefacts.size(); a++) {
                        if (artefactArray[activeArtefacts.getQuick(a)].degree < 1) {
                            System.err.println("Artefact " + activeArtefacts.getQuick(a) + " is listed as active but has degree " + artefactArray[activeArtefacts.getQuick(a)].degree);
                        }
                    }
                }
                if ((numberIndividuals < DISPLAYREWIRING) || ((infolevel & 1) > 0)) {
                    printIndividuals(System.out, " ", false);
//                if (rewireMode.artefactNeighbourList) setArtefactNeighbours();
//                printArtefacts(System.out, " ",false);
                    printArtefacts(System.out, " ", SEP, false);
                }
            }
         
        if (outputControl.individualListOn) //((outputcontrol & 128) >0 ) 
        {
            this.FileOutputIndividuals(COMMENTSTRING, "t"+event*numberRewiringsPerEvent, false);
        }
            
        if (outputControl.artefactDegreeEvolutionOn) //((outputcontrol & 64) >0 )
        {
           FileOutputArtefactTimeEvolution(COMMENTSTRING,  false , artefactDegreeEvolution);
           //FileOutputArtefactTimeEvolution(COMMENTSTRING,  true ,  artefactDegreeEvolution);
           FileOutputArtefactTimeCorrelation(COMMENTSTRING,  false ,  artefactDegreeEvolution);
           FileGraphicsOutputArtefactTimeEvolution(COMMENTSTRING,  artefactDegreeEvolution, 0 ,1.0);
           FileGraphicsOutputArtefactTimeEvolution(COMMENTSTRING,  artefactDegreeEvolution, 0 , (1.0/255.0));
           FileGraphicsOutputArtefactTimeEvolution(COMMENTSTRING,  artefactDegreeEvolution, 0.5 , 0.5);
        }
            
            
 
            timing.setCurrentTime();                    
            if (runIndicator) System.out.println(" Finished run "+run+" ("+(100*run/repeat)+"%) - elapsed time " +timing.elapsedTimeString()+" remaining time "+timing.estimateRemainingTimeString( (run+1.0) / ((double) repeat) ) );
        }// eo for run
        System.out.println("\n +++ Finished "+repeat+" runs +++");
        if (outputControl.allRunStatisticsOn) {
            if (artDDStatsOn) FileOutputTimeDependentData("", "t"+event*numberRewiringsPerEvent+"art", artDDStatsArr, false);
            if (this.artDDStatsTemplate.Y>0) FileOutputTurnoverTable(COMMENTSTRING, "t"+event*numberRewiringsPerEvent, false);
            if (inflDDStatsOn) FileOutputTimeDependentData("", "t"+event*numberRewiringsPerEvent+"infl", inflDDStatsArr, false);
        }
    } //eo rungeneralmodel

// ******************************************************************************    
    /** General File Output.
     * Outputs all the information according to outputControl settings.
     * @param nameending is added to end of name root before extension.
     */
        public void FileOutputGeneral(String nameending)
        {
//            int numBentleyArt =-1;
            if (rewireMode.modeBentley) 
            {   // now recalculate the artefact array degree based on the individual array data
                for (int art=0; art<numberArtefacts; art++) artefactArray[art].degree=0;
                for (int ind=0; ind<numberIndividuals; ind++) artefactArray[individualArray.getQuick(ind) ].degree++;
            }// eo if modeBentley

                    
            DistributionAnalysis ddAnalysis; 
            // calcArtefactDegreeDistribution(); MUST now be up to date before entering routine.
        if (outputControl.degreeDistributionOn ) 
        {
            FileOutputArtefactDegreeDistribution(COMMENTSTRING, false,artDDArr, nameending) ;
        }
        
        // set up log bin ratios to use
        double [] lbratio = new double [3];
        lbratio[0]=1.1;
        lbratio[1]=1.999;
        lbratio[2]=2.718; // approx =e
        
        if (outputControl.lbDegreeDistributionOn) 
        {
            for (int i=0; i< lbratio.length; i++)
            {
                ddAnalysis = new DistributionAnalysis(outputNameRoot+nameending,dirNameOutput,lbratio[i]);
                ddAnalysis.getDegreeDistribution(artDDArr);
                ddAnalysis.processOne(COMMENTSTRING, "lbdd.dat");
                if (i>0) FileOutputArtefactDegreeDistribution(COMMENTSTRING, false,artDDArr, nameending) ;
            }            
        }
        
        calcArtefactWeightDistribution();
        if (outputControl.weightDistributionOn) //((outputcontrol & 2) >0 ) 
        {
            FileOutputWeightDistribution(COMMENTSTRING, false,artWDArr, nameending) ;
        }
            

         if (outputControl.lbWeightDistributionOn) //((outputcontrol & 8) >0 ) 
        {
           for (int i=0; i< lbratio[i]; i++)
            {
                ddAnalysis = new DistributionAnalysis(outputNameRoot+nameending,dirNameOutput,lbratio[i]);
                ddAnalysis.getDegreeDistribution(artWDArr);
                ddAnalysis.processOne(COMMENTSTRING, "lbwd.dat");
            }
        }
 
        
        
        if (infolevel>0) 
        {
            printDegreeInfo(System.out, "");
            printActiveDegreeInfo(System.out, "");
            printWeightInfo(System.out, "");
        }

        }    
    
    /** Initialise the model.
     * @param initialIndividuals initial number of individuals 
     * @param initialArtefacts initial number of artefacts
     * @param setMaxNumberIndividuals maximum number of individuals, = 0 if automatic choice
     * @param setMaxNumberArtefacts maximum number of artefacts, = 0 if automatic choice
     * @return 0 if OK, negative if a problem.
     */
        public int initialiseModel(int initialIndividuals, int initialArtefacts, int setMaxNumberIndividuals,
                                   int setMaxNumberArtefacts)
        {
           //pPrefPluspRand = pPref+pRand;
//           pPrefPluspRandrnda = pPrefPluspRand +pRandact;
           //pBar=1.0-pPrefPluspRand;
           
           if (prob. test  () ) {
               System.out.println("*** Error in initialiseModel, probabilities wrong ");
               System.out.println(prob.label(SEP));
               System.out.println(prob.toString(SEP));
               return(-3);
           }
 

           
           // set up individual arrays and lists
           artDDStatsTemplate.numberIndividuals=numberIndividuals;
           inflDDStatsTemplate.numberIndividuals=numberIndividuals;

           maxNumberIndividuals=setMaxNumberIndividuals;
           if (setMaxNumberIndividuals==0) maxNumberIndividuals = initialIndividuals + 1;
           if (initialIndividuals > maxNumberIndividuals ) return(-1);
           individualArray  = new IntArrayList(maxNumberIndividuals);
           individualArray2 = new IntArrayList(maxNumberIndividuals);

           if (rewireMode.activeIndividualList) ActiveIndividuals = new FullVertexIAL();
           else ActiveIndividuals = new Vertex();
            
           if (rewireMode.inactiveIndividualList) InactiveIndividuals = new FullVertexIAL();
           else InactiveIndividuals = new Vertex();

           
//         Set up artefact arrays and lists
           maxNumberArtefacts=setMaxNumberArtefacts;
           double averageArtAdd = numEventsTotal*prob.pBar+1.0; //expected number of artefacts to be addded
           if (rewireMode.trueBentley) averageArtAdd  = averageArtAdd *2*initialIndividuals;
           if (setMaxNumberArtefacts==0) maxNumberArtefacts = initialArtefacts+ (int) (averageArtAdd + 4.0*Math.sqrt(averageArtAdd)+1.0);
           
           // Vertex is a basic class which can be extended to carry more information
           artefactArray= new Vertex[maxNumberArtefacts];
           if (outputControl.artefactDegreeEvolutionOn) artefactDegreeEvolution = new int [numStatsUpdates][maxNumberArtefacts]; //records degree of artefact a at time interval w

           if (rewireMode.activeArtefactList) activeArtefacts = new IntArrayList(this.maxNumberArtefacts);
           else activeArtefacts = new IntArrayList(1); // this last to avoid warnings

//           System.out.println("maxNumberArtefacts "+maxNumberArtefacts);
           int artAdd=-1;
           // set up empty artefact vertices of correct type
           for (numberArtefacts=0; numberArtefacts<initialArtefacts; numberArtefacts++)
             if (rewireMode.artefactNeighbourList) artefactArray[numberArtefacts]= new FullVertexIAL();
             else artefactArray[numberArtefacts]= new Vertex();

//           // set up empty influence vertices of correct type
//           for (int ni=0; ni<numberIndividuals; ni++)
//             if (rewireMode.influenceNetworkOn) influenceArray[ni]= new FullVertexIAL();
//             else influenceArray[ni]= new Vertex();
//           // Vertex is a basic class which can be extended to carry more information
//           influenceArray= new Vertex[maxNumberIndividuals];
//  //         if (outputControl.artefactDegreeEvolutionOn) influenceDegreeEvolution = new int [numStatsUpdates][maxNumberIndividuals]; //records degree of artefact a at time interval w
//
//           if (rewireMode.activeArtefactList) activeArtefacts = new IntArrayList(this.maxNumberArtefacts);
//           else activeArtefacts = new IntArrayList(1); // this last to avoid warnings
//
////           System.out.println("maxNumberArtefacts "+maxNumberArtefacts);
//           int inflAdd=-1;


           // make all individuals unattached and inactive 
           for (int i=0; i<initialIndividuals; i++) {
               individualArray.add(INDNOTATTACHED);
               if (rewireMode.inactiveIndividualList) InactiveIndividuals.addNeighbour(i);
           }
           
           
           // set up individual-artefact links, treat these as wiring not rewiring events
           for (numberIndividuals=0; numberIndividuals <initialIndividuals; numberIndividuals ++) {
                                switch (initialBiGraph) {
                                    //case 3: artAdd = Rnd.nextInt(numberArtefacts); break;
                                    case 2: artAdd = Rnd.nextInt(numberArtefacts); break;
                                    case 1: artAdd=0; break;   
                                    case 0: 
                                    default: artAdd = numberIndividuals % initialArtefacts; break;
                                }
                     //individualArray.add(artAdd); 
                     addArtToIndGeneral(INDNOTATTACHED,artAdd, numberIndividuals);
                                
                     addIndToArt(artAdd, numberIndividuals);// add ind as new neighbour to artefact
           }// eo for
                 
           // Now initial artefact degree has been set up, can set up array used to follow time evolution
           if (outputControl.artefactDegreeEvolutionOn) for (int a=0; a<maxNumberArtefacts; a++) artefactDegreeEvolution[0][a]=(a<numberArtefacts?artefactArray[a].degree:0);
            
           // If needed set up artefact degree ranking
           if (artDDStatsTemplate.Y>0) {
               artefactDegreeRank = new Rank(artDDStatsTemplate.Y, numberArtefacts, artefactArray);
           }
//           // If needed set up influence degree ranking
//           if (inflDDStatsTemplate.Y>0) {
//               influenceDegreeRank = new Rank(inflDDStatsTemplate.Y, numberIndividuals, influenceArray);
//           }
           
           // set up individual Network when needed
           //if ((ppMode ==1) || (artDDStatsTemplate.maxrhonumber>0))
           if (indNetworkOn)
           {       indNetwork = new timgraph(outputNameRoot+"ig", dirRoot, infolevel-1,0);
//                   indRndWalkLength = 2.0;  //  next two are for walks on the individual network
//                   indRndWalkMode =3;
                   if ((indNetworkNumberArgs>0) && (indNetwork.parseParam(indNetworkArgs)>0)) return (1);
                   //make sure total number of vertices at end equals number of individuals
                   indNetwork.setNumberEvents(numberIndividuals); // do not change!
                   if (indNetwork.getInitialVertices() >numberIndividuals) indNetwork.setInitialVertices(numberIndividuals);
                   indNetwork.doOneRun(1); // This will inialise graph and only if need more vertices will it do that
//                   if (infolevel>1) indNetwork.printNetwork(true);
//                   if ((numberIndividuals<DISPLAYREWIRING) && (infolevel>0)) indNetwork.printNetwork(true);
                   indRandomWalk = new RandomWalk(indNetwork);
           }
          // set up artefact network when needed
           if (artNetworkOn) 
           {       artNetwork = new timgraph(outputNameRoot+"ag", dirRoot, infolevel,0);
//                   artRndWalkLength = 3.0;  //  next two are for walks on the artividual network
//                   artRndWalkMode =7;
                   if ((artNetworkNumberArgs>0) && (artNetwork.parseParam(artNetworkArgs)>0)) return (1);
                   //make sure total number of vertices at end equals number of Artefacts
                   artNetwork.setNumberEvents(numberArtefacts); // do not change!
                   if (artNetwork.getInitialVertices() >numberArtefacts) artNetwork.setInitialVertices(numberArtefacts);
                   artNetwork.doOneRun(1); // This will inialise graph and only if need more vertices will it do that
//                   if (infolevel>1) artNetwork.printNetwork(true);
//                   if ((numberArtefacts<DISPLAYREWIRING) && (infolevel>0)) artNetwork.printNetwork(true);
                   artRandomWalk = new RandomWalk(indNetwork);
           }
           
           // set up influence network
           if(inflNetworkOn) {
               indCopiedFrom = new int[numberIndividuals];
           }

// set up for different ways of choosing artefacts on pPref events
           switch (ppMode)
           {
               case 1:
                   // Now initialise the random walk to do on this graph indRndWalkLength
                   indNetwork.initialiseRandomWalk(indRndWalkMode, indRndWalkLength , indRndWalkLength*4.0) ;
                   break;
               case 0:
               default: // do nothing more for copying art of random ind.
           }
           switch (prMode)
           {
               case 1:
                   // Now initialise the random walk to do on this graph artRndWalkLength
                   artNetwork.initialiseRandomWalk(artRndWalkMode, artRndWalkLength , artRndWalkLength*4.0) ;
                   break;
               case 0:
               default: // do nothing more for copying art of random ind.
           }
           
            return(0);
        }
    
    /** true Bentley rewire routine.
     * This sets up a fresh set of individuals who copy from the last iteration's individuals
     * exactly as in the model of Bentley et al. It repeats this so we have two generations or
     * two Bentley time steps for each call, or (2*numberIndividuals) individual rewirings
     * per call of this routine.
     * <br>Does not update the artefact information as going along.
     *@return dummy result
     */
        
        public int trueBentleyRewire()
        { 
            // if (!rewireMode.trueBentley) return(-1);
            int artadd;
            double r = -1;
            for (int ind=0; ind<numberIndividuals; ind++)
            {
                r = Rnd.nextDouble();
                if (r<prob.pPref) artadd = individualArray.getQuick(Rnd.nextInt(numberIndividuals)); // copy existing artefact
                else if (r<prob.pPrefPluspRand) artadd = Rnd.nextInt(numberArtefacts); // add random artefact
                else artadd = numberArtefacts; // add new artefact
            
                individualArray2.set(ind, artadd); // note ind's new artefact
                //addIndToArt(artadd, ind); // add ind as new neighbour to artefact
                //addIndToArt(artadd);
            }
            // Rest degree to zero before adding new links
            for (int a=0; a<numberArtefacts;a++) artefactArray[a].degree=0;
            for (int ind=0; ind<numberIndividuals; ind++)
            {
                r = Rnd.nextDouble();
                if (r<prob.pPref) artadd = individualArray2.getQuick(Rnd.nextInt(numberIndividuals)); // copy existing artefact
                else if (r<prob.pPrefPluspRand) artadd = Rnd.nextInt(numberArtefacts); // add random artefact
                else artadd = numberArtefacts; // add new artefact
            
                //individualArray.setQuick(ind,artadd); // note ind's new artefact
                individualArray.setQuick(ind,artadd); // note ind's new artefact
                //addIndToArt(artadd, ind); // add ind as new neighbour to artefact
                //addIndToArt(artadd);
            }
            return(0);
        }// eo trueBentleyRewire

        
        /** Choose Individual Vertex. 
         * <p>This chooses an individual vertex in various ways.<br>
         * Modes:<br>
         * 0 = Random individual (fixed no. of individuals). <br>
         *        1 = Random walk on individual network. <br>
         * Assumes that number of individuals is fixed.
         *@param mode sets method for choice.  
         *@param currentIndividual the number of the current individual used for finding neighbours, (??? -1 uses current vertex)
         * @return the number of the individual vertex.
         * @deprecated
         */
        public int chooseIndividual(int mode, int currentIndividual) 
        {
//            switch (mode)
//            {
//                case 1: 
//                    indNetwork.randomWalk.setStartVertex(currentIndividual);
//                    return indNetwork.randomWalk.doOneWalk();  //break;
//                case 0: return Rnd.nextInt(numberIndividuals);
//                default: return -987123;
//            }
            return -1;
        }
                
        /** Choose Artefact vertex given an individual vertex.
         * This chooses an artefact vertex in various ways.<br>
         * <br>Modes:
         * <ul>
         * <li>0 = artefact of randomly chosen individual</li>
         * <li>1 = artefact found using random walk on individual network starting from given individual</li>
         * <li>2 = neighbouring artefact of artefact chosen by random individual</li>
         * <li>3 = choose artefact used by an active individual</li>
         * </ul>
         * Note that the parameter copyFromInd must be an Integer because it is
         * used to pass back the number of the individual used to do the copying
         * in modes.  Set in modes 0, 1 and 2 only
         * ToDo: IF activeIndividualLists on then we must choose from activeIndividualLists
         *@param mode sets method for choice. 
         *@param ind the number of an individual
         *@param copyFromInd this is set to be equal to the individual used
         *           to copy from in modes 0, 1 and 2 only.
         *@return the number of the artefact vertex chosen.
         */
        public int chooseArtFromInd(int mode, int ind)
        {
           switch (mode)
            {
                case 3: return ActiveIndividuals.getRandomNeighbour(Rnd);
                case 2: artNetwork.getRandomNeighbour(Rnd.nextInt(numberIndividuals));
                    COPYFROMIND=individualArray.getQuick(ind);
                    return artNetwork.getRandomNeighbour(COPYFROMIND);  //break;
                case 1:  // These must match the choseIndividual cases
                    indNetwork.randomWalk.setStartVertex(ind);
                    COPYFROMIND=indRandomWalk.doOneWalk(indNetwork.binomialNumber);
                    return individualArray.getQuick(COPYFROMIND);  //break;
                case 0: COPYFROMIND=Rnd.nextInt(numberIndividuals);
                    return individualArray.getQuick(COPYFROMIND);
                default: return -987123;

            }
            //return -1;            
        }

        /** Choose Active Individual Vertex.
         *@param mode sets method for choice.  
         *@param currentIndividual the number of the current individual used for finding neighbours, (??? -1 uses current vertex)
         * @return the number of the individual vertex.
         */
        public int chooseActiveIndividual(int mode, int currentIndividual) 
        {
//            switch (mode)
//            {
//                case 0: 
//                    return ActiveIndividuals.getRandomNeighbour();  //break;    
////                case 1: 
////                    indNetwork.randomWalk.setStartVertex(currentIndividual);
////                    return indNetwork.randomWalk.doOneWalk();  //break;
//                default: return -987123;
//            }
            return -1;
        }


        
//1= copy artefact of randomly chosen individual, 2= copy artefact of individual found by random walk
                 
        /** Choose Artefact Vertex given another artefact vertex.
         * <p>This chooses an artefact vertex in various ways starting from an artefact.
         * Typically this would be a p_r random/mutation/innovation event.
         *<br>Modes:<br>
         *       0 = random artefact <br>
         *       1 = artefact found using random walk on artefact network<br>
         *       2 = new artefact<br>
         *       3 = random nearest neighbour or random artefact if no n.n.<br>
         *@param mode sets method for choice. 
         *@param currentArtefact the number of the current artefact used for finding neighbours, -1 uses current vertex
         *@return the number of the artefact vertex chosen.
         */
        public int chooseArtFromArt(int mode, int currentArtefact)
        {
            switch (mode)
            {
//                case 2: //copy artefact of individual found by random walk;
//                        a =individualArray[chooseIndividual(1,-1)]; 
//                        break; 
//                case 1: //copy artefact of randomly chosen individual;
//                        a =individualArray[chooseIndividual(0,-1)]; 
//                        break; 
                case 3: // find neighbour of given artefact;
                    int a = artNetwork.getRandomNeighbour(currentArtefact);
                    if (a<0)  return Rnd.nextInt(numberArtefacts);
                            else return a; //break; 
                case 2: 
                        return numberArtefacts; //break;
                case 1: //copy artefact of individual found by random walk;
                    artNetwork.randomWalk.setStartVertex(currentArtefact);
                    return artRandomWalk.doOneWalk(artNetwork.binomialNumber); //break;
                case 0:
                default: return Rnd.nextInt(numberArtefacts);
            }
            //return -1;            
        }


        
       /** 
        * Fast multiple rewire routine for modes with a single rewiring per event.
         * <br>This rewires the artefact end of the one edge from an individual.
         * The individual (or equivalently its edge) are chosen randomly, the reconnection or the
         * new artefact are chosen appropriately.
        * <br>Should be OK with influence network, but this is untested.
         * <br>Does not maintain the list of neighbours of each artefact, just the artefact degree
        * <br>Does not maintain a list of active artefacts or active/inactive individuals
        *@param  numberRewirings the number of rewirings
        */
        
        public void rewireNoneBentley(int numberRewirings) {
            int indrw =-1; 
            int artrem=-1;
            int artadd=-1; // artefact to add
//            int indcopy =-1; // individual copied    
            double r =-1;
            
            for (int e=numberRewirings; e>0; e--) {
                COPYFROMIND=CopyModel.IUNSET;
                indrw =  Rnd.nextInt(numberIndividuals); // individual to rewire
                //chooseIndividual(0);
                //Rnd.nextInt(numberIndividuals);
                artrem = individualArray.getQuick(indrw); // artefact to remove
                r = Rnd.nextDouble();
                if (r<prob.pPref) { // copy artefact of existing individual
                    //indcopy = chooseIndividual(ppMode,indrw); //copy this individual
                    //chooseIndividual(ppMode); // use general method to choose individual to copy
                    // Rnd.nextInt(numberIndividuals); //copy this individual
                    //artadd = individualArray.getQuick(indcopy); // set to this artefact
                    artadd = chooseArtFromInd(ppMode, indrw);
                } else if (r<prob.pPrefPluspRand) { // add random artefact
                    //artadd = Rnd.nextInt(numberArtefacts);
                    artadd = chooseArtFromArt(prMode, artrem); // choose random existing artefact
                    //Rnd.nextInt(numberArtefacts);
                } else { // (1-r) < pBar so add new artefact
                    artadd = numberArtefacts; // artefact to add
                }
                if (inflNetworkOn){indCopiedFrom[indrw]=COPYFROMIND; }
                individualArray.setQuick(indrw,artadd); // note indrw's new artefact
                // update artefact array here
                //addIndToArt(artadd, indrw); // add indrw as new neighbour to artefact
                addIndToArt(artadd);
                // assuming that this is not a Bentley mode
                artefactArray[artrem].degree--;
                
                // if updating artefact neighbours
                //if (0 == artefactArray[artrem].deleteNeighbour(indrem)) activeArtefacts.remove(artrem); //remove indrw from artefact artrem
                
            }// eo for e
        }// eo rewire

        /**
         * Rewire or wire all edges in given list.
         * <p>Will do in one or two steps depending on <code>rewireMode.twoSteps</code>
         * <br>This wires or rewires all the individuals given in a list simultaneously. 
         * That is the network is only updated after all choices are made.
         * The artefact is chosen individual (or equivalently its edge) are
         * chosen randomly, the reconnection or the
         * new artefact are chosen appropriately.
         * It will also simply wire an individual if it finds it is not connected.
         * <br>Will maintain the list of neighbours of each artefact if thats in use.
         * <br>May or may NOT allow for variable numbers of individuals.
         * <br>Can have same individual more than once in list.
         *@param indrwList a list of individuals to be rewired
        */        
        public void rewireList(IntArrayList indrwList) {
            int artrem=-99;
            int artadd=-88; // artefact to add
            int indrw = -77; // individual to rewire
            int numberEventRewirings = indrwList.size();
            
            // if disconnect first before connecting second (non-simultaneous)
            if (rewireMode.twoSteps) {
                for (int i=0; i<numberEventRewirings; i++)
                {
                    indrw = indrwList.getQuick(i);
                    artrem = individualArray.getQuick(indrw);
                    if (numberIndividuals<DISPLAYREWIRING) System.out.println(i+": indrw "+indrw+", art rem "+artrem);
                    if (artrem<0) continue; // this artefact already unwired.
                    artefactArray[artrem].deleteNeighbour(indrw);
                    individualArray.setQuick(indrw,INDNOTATTACHED);
                    if (rewireMode.activeIndividualList) ActiveIndividuals.deleteNeighbour(indrw);
                    if (rewireMode.inactiveIndividualList) InactiveIndividuals.addNeighbour(indrw);

                    if ((rewireMode.artefactNeighbourList) && (0 == artefactArray[artrem].deleteNeighbour(indrw)) && (rewireMode.activeArtefactList) ) activeArtefacts.delete(artrem);
                }// eo for i
                if ((rewireMode.activeArtefactList) && (activeArtefacts.size()==0)) message.printERROR("rewireList produced no active artefacts");
            }// eo if rewireMode.twoSteps
            
            double r=-66;
            int [] artaddList = new int[numberEventRewirings];
            // first choose the rewiring events but don't change the network
            for (int i=0; i<numberEventRewirings; i++)
            {
                COPYFROMIND=IUNSET;
                indrw = indrwList.getQuick(i); // rewire this individual
                r = Rnd.nextDouble();
                if (r<prob.pPref) { // copy artefact starting from an existing individual
                    // IF activeIndividualLists on then we must choose from active individualLists
                    artadd = chooseArtFromInd(ppMode, indrw);
                } else if (r<prob.pPrefPluspRand) { // add random artefact
                    //artrem = individualArray.getQuick(indrw);
                    //artadd = Rnd.nextInt(numberArtefacts);
                    artadd = chooseArtFromArt(prMode, individualArray.getQuick(indrw)); // choose random existing artefact
                    //Rnd.nextInt(numberArtefacts);
                } else { // (1-r) < pBar so add new artefact
                    artadd = numberArtefacts; // artefact to add
                }
                if (inflNetworkOn){indCopiedFrom[indrw]=COPYFROMIND; }
                artaddList[i]=artadd;
            }// eo for i
       // now do the wiring or rewiring 
            for (int i=0; i<numberEventRewirings; i++)
            {
                artadd=artaddList[i];
                indrw = indrwList.getQuick(i);
                artrem = individualArray.getQuick(indrw);
                if (numberIndividuals<DISPLAYREWIRING) if (artrem == INDNOTATTACHED)  System.out.println(i+": indrw "+indrw+" NA -> "+artadd);
                    else System.out.println(i+": indrw "+indrw+", art "+artrem+" -> "+artadd);
                if (artrem == INDNOTATTACHED) addArtToIndGeneral(artrem, artadd, indrw); // add artadd as new neighbour to individual
                else {// individual was already attached so must rewire
                      // no need to update active/inactive individual lists here
                      // but must update active Artefact list for the removal.
                    addArtToIndnoAI(artadd, indrw);
                    removeIndFromArt(artrem,indrw);
                }
                individualArray.setQuick(indrw, artadd); // note indrw's new artefact
                addIndToArt(artadd, indrw); // add indrw as new neighbour to artefact
                                            // this deals with active artefacts

            } //eo for i   
        }// eo rewireList
        
       /** 
        * Remove of all edges in given list of Individuals.
        * <br>This removes the individuals so requires the use of Active and Inactive individual lists
        * which is done by using artificial artefact vertices.
        *<br>Maintains the list of neighbours of each artefact and active artefact lists.
        *<br>Active/Inactive individual lists maintained.
        *<br>Can have same individual more than once in list.
        *@param indremList a artefact to be rewired
        * @deprecated Code is written implicitly into the {@link #rewireList(IntArrayList)} routine
        */
        public void removeList(IntArrayList indremList) {
            int artrem=-99;
            int indrem = -99; // individual to be disconnected
            int numberEventRewirings = indremList.size();
            for (int i=0; i<numberEventRewirings; i++)
            { 
                indrem = indremList.getQuick(i);
                artrem = individualArray.getQuick(indrem);
                individualArray.setQuick(indrem,INDNOTATTACHED);
                if (rewireMode.activeIndividualList) 
                {
                    InactiveIndividuals.addNeighbour(indrem);
                    ActiveIndividuals.deleteNeighbour(indrem);
                }
                if ((rewireMode.artefactNeighbourList) && (0 == artefactArray[artrem].deleteNeighbour(indrem)) && (rewireMode.activeArtefactList) ) activeArtefacts.delete(artrem);
            }// eo for i
        }// eo removeList
        
        /** General rewire routine.
         * Allows for pseudoBentley modes (but does not seem to implement them).
         * This rewires the artefact end of the one edge from an individual.
         * The individual (or equivalently its edge) are chosen randomly, the reconnection or the
         * new artefact are chosen appropriately.
         * <br>Does not maintain the list of neighbours of each artefact, just the artefact degree,
         * nor active lists of either type of vertex
         * <br>Should be able to cope with influence network but this is untested.
         *@param indrw individual whose artefact end edge is to be rewired
         *@return the artefact added
         */        
        public int rewire(int indrw) {
            int artrem = individualArray.getQuick(indrw); // artefact to remove
            int artadd=-1; // artefact to add
//            int indcopy =-1; // individual copied
            double r = Rnd.nextDouble();
            COPYFROMIND=IUNSET;
            if (r<prob.pPref) { // copy artefact of existing individual
                if (infolevel>1) System.out.print("Copying: ");
                artadd = this.chooseArtFromInd(ppMode, indrw); // set to this artefact
//                if (infolevel>1) System.out.println("Individual "+indrw+" copys individual "+ indcopy +", changes from artefact "+artrem+" to "+artadd);
                  if (infolevel>1) System.out.println("Individual "+indrw+" copys, changes from artefact "+artrem+" to "+artadd);
            } else if (r<prob.pPrefPluspRand) { // add random artefact
                if (infolevel>1) System.out.print("Random Artefact: ");
                //artadd = Rnd.nextInt(numberArtefacts);
                artadd = chooseArtFromArt(prMode, artrem); // set to this artefact
                 if (infolevel>1) System.out.println("Individual "+indrw+" chooses random artefact so changes from "+artrem+" to "+artadd);
                //chooseArtFromArt(0); // choose random existing artefact
                //Rnd.nextInt(numberArtefacts);
            } else { // (1-r) < pBar so add new artefact
                 artadd = numberArtefacts; // artefact to add
                 if (infolevel>1) System.out.println("Individual "+indrw+" chooses new artefact so changes from "+artrem+" to "+artadd);
            }
            if (inflNetworkOn){indCopiedFrom[indrw]=COPYFROMIND; }
            //if (artrem==artadd) return(artadd);
            individualArray.setQuick(indrw, artadd); // note indrw's new artefact
            // update artefact array here
//            if (rewireMode.artefactNeighbourList) addIndToArt(artadd, indrw);
//                     else addIndToArt(artadd);  // add indrw as new neighbour to artefact
            addIndToArt(artadd, indrw);
            artefactArray[artrem].degree--;
            // artefact neighbours update
//            if (rewireMode.modeBentley)  artefactArray[artrem].degree--;
//            else if (0 == artefactArray[artrem].deleteNeighbour(indrem)) activeArtefacts.delete(artrem); //remove indrw from artefact artrem
            return (artadd);
        }// eo rewire

        
        /** 
         * Wires new edges to individuals in a given list.
         * <br>This rewires all the individuals given in a list.
         *  Assumes that these individuals have disconnected edges already.
         * The artefact is chosen individual (or equivalently its edge) are chosen randomly, the reconnection or the
         * new artefact are chosen appropriately.
         * <br>May or may NOT maintain the list of neighbours of each artefact.
         * <br>May or may NOT allow for variable numbers of individuals.
         * <br> Can have same individual more than once in list.
         *@param indrwList a list of individuals to be rewired
         * @deprecated Should use {@link #rewireList(IntArrayList)}
         */
        public void wireList(IntArrayList indrwList) {
            int artadd=-88; // artefact to add
            int indrw = -77; // individual to rewire
            Integer copyFromInd=IUNSET;
            double r=-66.666;
            int numberEventRewirings = indrwList.size();
            int [] artaddList = new int[numberEventRewirings];
            // first choose the artefacts to receive new edges but don't change the network
            for (int i=0; i<numberEventRewirings; i++)
            { 
                //indrw = indrwList.getQuick(i);
                COPYFROMIND=IUNSET;
                r = Rnd.nextDouble();
                if (r<prob.pPref) { // copy artefact starting from an existing individual
                    artadd = this.chooseArtFromInd(ppMode, indrwList.getQuick(i));
                } else if (r<prob.pPrefPluspRand) { // add random artefact
                    //artrem = individualArray.getQuick(indrw);
                    //artadd = Rnd.nextInt(numberArtefacts);
                    artadd = chooseArtFromArt(prMode, individualArray.getQuick(indrwList.getQuick(i))); // choose random existing artefact
                    //Rnd.nextInt(numberArtefacts);
                } else { // (1-r) < pBar so add new artefact
                    artadd = numberArtefacts; // artefact to add
                }
                if (inflNetworkOn){indCopiedFrom[indrw]=COPYFROMIND; }
                artaddList[i]=artadd;
            }// eo for i
       // Now do the wiring 
            for (int i=0; i<numberEventRewirings; i++)
            {
                indrw = indrwList.getQuick(i);
                artadd=artaddList[i];
                individualArray.setQuick(indrw, artadd); // note indrw's new artefact
                addIndToArt(artadd, indrw); // add indrw as new neighbour to artefact
            } //eo for i   
        }// eo rewire
        
        

        
       /** 
        * Makes specified individual inactive.
        * <br>This assumes the use of Active individual lists and active artefact lists and can cope with inactive individuals if needed.
        * @param  indrem no. of one individual to be made inactive
        * @return number of active individuals, -1 if this mode not on
        */
          public int addInactiveInd(int indrem) {
              if (!rewireMode.activeIndividualList) return -1;
              int artrem = individualArray.getQuick(indrem);
              if (0 == artefactArray[artrem].deleteNeighbour(indrem)) activeArtefacts.delete(artrem);
              individualArray.set(indrem,INDNOTATTACHED);
              if (rewireMode.inactiveIndividualList) InactiveIndividuals.addNeighbour(indrem);
              return (ActiveIndividuals.deleteNeighbour(indrem));
        }// eo rewire
      
// ----------------------------------------------------------------------
        /**
         * Add Individual as a neighbour to an Artefact.
         * <p>Fast and simple routine.
         * <br>artefactArray uses degree for current value
         * except in true Bentley mode when it is just rubbish
         * but the weight should always be the cumulative degree
         *<br> Not updating the list of neighbours to an artefact, just the degree.
         * The list of neighbours should be null in any Bentley mode.
         *@param artAdd the number of the arifact to be added
         *@return 0 if OK, -1 otherwise
         */
        
        public int addIndToArt(int artAdd) {
            while (artAdd>=numberArtefacts) artefactArray[numberArtefacts++] = new Vertex(); // add new artefact
            artefactArray[artAdd].degree++;
            return (0);
        }
// ----------------------------------------------------------------------
        /**
         * Add Individual as a neighbour to an Artefact.
         * <p>artefactArray uses degree for current value
         * except in true Bentley mode when it is just rubbish
         * but the weight should always be the cumulative degree
         *<br> Will update the list of neighbours to an artefact if present, works if not.
         *<br> Will update list of active artefacts if needed.
         *@param artAdd the number of the artefact to be added
         *@param individual the one which neighbours the artefact
         *@return 0 if OK, -1 otherwise
         */
        
        public int addIndToArt(int artAdd, int individual) {
            if (rewireMode.artefactNeighbourList) {while (artAdd>=numberArtefacts) artefactArray[numberArtefacts++] = new FullVertexIAL(); } // add new artefact
            else {while (artAdd>=numberArtefacts) artefactArray[numberArtefacts++] = new Vertex(); } // add new artefact
            if ((rewireMode.activeArtefactList) && (artefactArray[artAdd].degree==0)) activeArtefacts.add(artAdd);
            artefactArray[artAdd].addNeighbour(individual);
            return (0);
        }

// ----------------------------------------------------------------------
    /**
     * Add Artefact as a neighbour to an Individual.
     * <p>Increases individualArray if needed so allows for variable number of individuals.
     * <br>Updates active individuals if needed.
     *@param artRem the number of the artefact being disconnected
     *@param artAdd the number of the artefact to be connected
     *@param indAdd the individual now connected to the artefact
     */
    public void addArtToIndGeneral(int artRem, int artAdd, int indAdd) {
        if (rewireMode.activeIndividualList) {
            if (rewireMode.inactiveIndividualList) addArtToIndAIII(artRem, artAdd, indAdd); 
            else addArtToIndAInoII(artRem, artAdd, indAdd);
        }
        else addArtToIndnoAI(artAdd, indAdd);
        return;
    }// eo addArtToInd
    
    /**
     * Add Artefact as a neighbour to an Individual.
     * <p>Increases individualArray if needed so allows for variable number of individuals.
     * <br>Active/Inactive individual lists are not updates e.g. not in use, or its a rewiring not a wiring event.
     *@param artAdd the number of the artefact to be connected
     *@param indAdd the individual now connected to the artefact
     */
    public void addArtToIndnoAI(int artAdd, int indAdd) {
        if (individualArray.size() > indAdd) {  // individual already exists
            individualArray.set(indAdd, artAdd);
            return;
        }
        // first add empty individuals then the last new one is indAdd and this is attached to artAdd
        while (individualArray.size() < indAdd) individualArray.add(INDNOTATTACHED);
        individualArray.add(artAdd);
        numberIndividuals = individualArray.size();
    }// eo addArtToIndnoAI
    
    /**
     * Add Artefact as a neighbour to an Individual.
     * <p>Increases individualArray if needed so allows for variable number of individuals.
     * <br>Assumes active and inactive individual lists are in use.
     *@param artRem the number of the artefact being disconnected
     *@param artAdd the number of the artefact to be connected
     *@param indAdd the individual now connected to the artefact
     */
    public void addArtToIndAIII(int artRem, int artAdd, int indAdd) {
        //active individual lists assumed
        if (individualArray.size() > indAdd) { //individual already exists
            if (artRem < 0) {
                InactiveIndividuals.deleteNeighbour(indAdd);
                ActiveIndividuals.addNeighbour(indAdd);
            }
            individualArray.set(indAdd, artAdd);
            return;
        }// eo if individualArray.size
        // first add empty individuals then the last new one is indAdd
        while (individualArray.size() < indAdd) {
            individualArray.add(INDNOTATTACHED);
            InactiveIndividuals.addNeighbour(individualArray.size());
        }
        ActiveIndividuals.addNeighbour(indAdd);
        individualArray.add(artAdd);
        numberIndividuals = individualArray.size();
    }// eo addArtToInd
    
    /**
     * Add Artefact as a neighbour to an Individual.
     * <p>Increases individualArray if needed so allows for variable number of individuals.
     * <br>Assumes active individual lists are in use but not inactive individual lists.
     *@param artRem the number of the artefact being disconnected
     *@param artAdd the number of the artefact to be connected
     *@param indAdd the individual now connected to the artefact
     */
    public void addArtToIndAInoII(int artRem, int artAdd, int indAdd) {
        //active individual lists assumed
        if (individualArray.size() > indAdd) { //individual already exists
            if (artRem < 0) {
                //InactiveIndividuals.deleteNeighbour(indAdd);
                ActiveIndividuals.addNeighbour(indAdd);
            }
            individualArray.set(indAdd, artAdd);
            return;
        }// eo if individualArray.size
        // first add empty individuals then the last new one is indAdd
        while (individualArray.size() < indAdd) {
            individualArray.add(INDNOTATTACHED);
            //InactiveIndividuals.addNeighbour(individualArray.size());
        }
        ActiveIndividuals.addNeighbour(indAdd);
        individualArray.add(artAdd);
        numberIndividuals = individualArray.size();
    }// eo addArtToInd


// ----------------------------------------------------------------------
        /**
         * Removes Individual from an Artefact.
         * <br>Does not update the status of the individual 
         *@param artrem the number of the artefact to be disconnected
         *@param indrem the individual no longer connected to the artefact
         */
        
        public void removeIndFromArt(int artrem, int indrem) {
            artefactArray[artrem].deleteNeighbour(indrem);
            if ((rewireMode.activeArtefactList) && (artefactArray[artrem].degree==0)) activeArtefacts.delete(artrem);
        }



        
// -----------------------------------------------------------------------       
    /**
     * adds value to the element at index extending with zero entries
     * or starting list if necessary
     * @param ial IntArrayList
     * @param index
     * @param value to set:  ial[index]=value
     */
    void addExtendIntArrayList(IntArrayList ial, int index,  int value)  
    {
        if (index<ial.size()) {ial.setQuick(index, ial.getQuick(index)+value); return;}
        while (index>ial.size()) ial.add(0);
        ial.add(value);
        return;              
    }

//    void addExtendIntArrayListOld(IntArrayList ial, int index,  int value)  
//    {
//        int size = ial.size();
//        if ((index==0) && (size==0)) {ial.add(value); return;}
//        for (int i=size; i<=index; i++) ial.add(0);
////        ial.set(index, ial.get(index)+value); Surely the get is always 0?
//          ial.set(index, value);
//        return;
//                  
//    }
        
// **********************************************************************
// STATISTICAL CALCULATIONS

// -----------------------------------------------------------------------       
    /**
     * Calculates artefact degree distribution and degree stats
     */
    void calcArtefactWeightDistribution()  {
        
        //numberActiveArtefacts =0;
        
        weightArtCont =0;
        weightArt=new StatisticalQuantity(); 
        artWDArr = new IntArrayList() ;
        //if (artWDTotArr == null) artWDTotArr = new IntArrayList() ;
        
        int w;
        for(int a=0; a<numberArtefacts; a++)
        {          
          w = (int) (artefactArray[a].weight+0.5);
          weightArt.add(w);
          addExtendIntArrayList(artWDArr, w, 1);
          //addExtendIntArrayList(artWDTotArr, w, 1);
        }
        
        int n=0;
//        int na=0;
        totalWeight=0;
        int nk = 0;
        weightArtCont = ((int) weightArt.getMaximum()) + 1; //
        int kminimum = ((int) weightArt.getMinimum());
        for (int k=0; k<artWDArr.size(); k++) 
        {
           nk=artWDArr.get(k);
           n+=nk;
           totalWeight += nk*k;
           if ((k>kminimum) && (nk==0) && (k<weightArtCont)) weightArtCont = k;
        }
        
        if (n != numberArtefacts)
        {
            System.out.println("*** Error in calcArtefactWeightDistribution vertex totals mismatch");
            System.out.println("     numberArtefacts = "+numberArtefacts+", calc = "+n);
        }
        
        
        return;
    }//eo calcWeightDistribution

    // -----------------------------------------------------------------------       
    /**
     * Sets up the neighbours list of the artefacts and resets degree from individual list.
     *<br>Assumes that artefact neighbour lists present in artefact vertices.
     */
    void setArtefactNeighbours()  {
      for   (int a=0;a<numberArtefacts; a++) artefactArray[a].wipeSourceList();
      for   (int i=0;i<numberArtefacts; i++) artefactArray[individualArray.getQuick(i)].addNeighbour(i);
    }
    
// -----------------------------------------------------------------------
    /**
     * Calculates artefact degree distribution and degree stats
     */
    void calcArtefactDegreeDistribution()  {

        numberActiveArtefacts =0;

        degreeArtCont =0;
        artDegree=new StatisticalQuantity();
        artDDArr = new IntArrayList() ;

        int k;
        for(int a=0; a<numberArtefacts; a++){
          k = artefactArray[a].degree;
          artDegree.add(k);
          addExtendIntArrayList(artDDArr, k, 1);
          }

        int n=0;
        int ne=0;
        int nk = 0;
        degreeArtCont = ((int) (artDegree.getMaximum() +0.5))  + 1; //
        int kminimum = ((int) (artDegree.getMinimum() +0.5)) ;
        for (k=0; k<artDDArr.size(); k++)
        {
           nk=artDDArr.get(k);
           n+=nk;
           ne+=nk*k;
           if ((k>kminimum) && (nk==0) && (k<degreeArtCont)) degreeArtCont = k;
        }
        numberActiveArtefacts = n-artDDArr.get(0);

        if (n != numberArtefacts)
        {
            System.out.println("*** Error in calcArtefactDegreeDistribution vertex totals mismatch");
            System.out.println("       numberArtefacts = "+numberArtefacts+", calc = "+n);
        }

        if (ne != numberIndividuals)
        {
            System.out.println("*** Error in calcArtefactDegreeDistribution edge totals mismatch");
            System.out.println("     numberIndividuals = "+numberIndividuals+", calc = "+ne);
        }

        return;
    }//eo calcDegreeDistribution

// -----------------------------------------------------------------------
    /**
     * Calculates influence network degree distribution.
     */
    void calcInfluenceDegreeDistribution()  {

        numberLeaders =0;

        degreeInflCont =0;
        degreeInfl=new StatisticalQuantity();
        inflDDArr = new IntArrayList() ;
        int [] followers = new int[numberIndividuals];
        int leader;
        for(int i=0; i<numberIndividuals; i++) {
            leader=indCopiedFrom[i];
            if (leader>=0 && leader<numberIndividuals) followers[leader]++;
        }
        int k;
        for(int i=0; i<numberIndividuals; i++){
          k = followers[i];
          degreeInfl.add(k);
          addExtendIntArrayList(inflDDArr, k, 1);
          }

        int n=0;
//        int na=0;
        int ne=0;
        int nk = 0;
        degreeInflCont = ((int) (degreeInfl.getMaximum() +0.5))  + 1; //
        int kminimum = ((int) (degreeInfl.getMinimum() +0.5)) ;
        for (k=0; k<inflDDArr.size(); k++)
        {
           nk=inflDDArr.get(k);
           n+=nk;
           ne+=nk*k;
           if ((k>kminimum) && (nk==0) && (k<degreeInflCont)) degreeInflCont = k;
        }
        numberLeaders = n-inflDDArr.get(0);

        if (n != numberIndividuals)
        {
            System.out.println("*** Error in calcInfluenceDegreeDistribution vertex totals mismatch");
            System.out.println("       numberIndividuals = "+numberIndividuals+", calc = "+n);
        }

        if (ne > numberIndividuals)
        {
            System.out.println("*** Error in calcInfluenceDegreeDistribution, edge totals mismatch");
            System.out.println("     numberIndividuals = "+numberIndividuals+", number followers = "+ne);
        }

        return;
    }//eo calcInfluenceDegreeDistribution

// -----------------------------------------------------------------------       
    /**
     * Checks information in artefactArray.
     * @return 0 if OK, otherwise its the difference of the number of edges from artefactArray.degree and number of individuals.
     */
    int checkArtefactDegreeDistribution()  {
        
        int k;
        int ne=0;
        for(int a=0; a<numberArtefacts; a++) ne+=artefactArray[a].degree;
        
        if (ne == numberIndividuals) return 0;
            System.out.println("*** Error in checkArtefactDegreeDistribution() edge totals mismatch");
            System.out.println("     numberIndividuals = "+numberIndividuals+", calc = "+ne);
        return (ne-numberIndividuals);
    }//eo calcDegreeDistribution

      
  
    
// ***********************************************************************
// FILE OUTPUT ROUTINES

     /**
     * Outputs general parameters to a file.
     * File is <dirNameOutput+outputNameRoot+filenameending>.info.dat  
     * @param cc comment characters put at the start of every line.
     * @param filenameending used to end the file name.
     * @param giveGeneralInformation if true first few lines are comments listing the general parameters 
     */


        void FileOutputParameters(String cc, String filenameending)  
        {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        extension=".info.dat";
        filenamecomplete= dirNameOutput+outputNameRoot+filenameending+extension;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete); // open new file mode
            PS = new PrintStream(fout);
            printParameters(PS, cc);            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    
        if (infolevel>0) System.out.println("Finished parameters to "+ filenamecomplete);

        try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}


        }// eo  FileOutputTimeDependentData
    
    
    
    
    /**
     * Outputs artefact degree distribution to a file.
     * <p><em>fileoutputNameRoot</em><tt>.Jdd.dat</tt> Degree Distribution, or
     * <br><em>fileoutputNameRoot</em><tt>.Jndd.dat</tt> Normalised Degree Distribution.
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to switch on normalisation
     * @param artDDArr is CERN Colt Integer Array List of the degree distribution
     * @param filenameending appended to basic file name before extension added
     */
    void FileOutputArtefactDegreeDistribution(String cc, boolean normalise,
                                      IntArrayList artDDArr, String filenameending)
        {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        if (normalise) extension = ".Jndd.dat";
        else extension=".Jdd.dat";
        filenamecomplete= dirNameOutput+outputNameRoot+filenameending+extension;
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
//            date = new Date();
            double p=0;
            int n=0;
            printParameters(PS, cc);
            printProbabilities(PS, cc);
            printDegreeInfo(PS, cc);
            printActiveDegreeInfo(PS, cc);
//            if (numberArtefacts<1) return;
            if (normalise) PS.println(cc+" k "+SEP+"p(k)    Normalised Artefact Degree Distribution");
            else PS.println(cc+" k "+SEP+"n(k)     Unnormalised Artefact Degree Distribution");
            for (int k=0; k<artDDArr.size(); k++)
            {
              if (normalise)  
              {
                  p = artDDArr.get(k)/((double) numberArtefacts);
                  if (p>0) PS.println(k+SEP+p);
              }
              else 
              {
                  n = ((int) (artDDArr.get(k)+0.5) );
                  if (n>0) PS.println(k+SEP+n);
              }
            }
            if (infolevel>0) System.out.println("Finished writing artefact degree distribution to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }

        
        /**
     * Outputs information for a connected Undirected graph
     *  <fileoutputNameRoot>.Jwd.dat Weight Distribution, or
     *  <fileoutputNameRoot>.Jnwd.dat Normalised Weight Distribution 
     * @param fileoutputNameRoot basis of name of file as string
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to switch on normalisation
     * @param artWDArr is CERN Colt Integer Array List of the degree distribution     
     */

        void FileOutputWeightDistribution(String cc, boolean normalise, IntArrayList artWDArr, String filenameending)  
        {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        if (normalise) extension = ".Jnwd.dat";
        else extension=".Jwd.dat";
        filenamecomplete= dirNameOutput+outputNameRoot+filenameending+extension;
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
//            date= new Date();
            double p=0;
            int n=0;
            printParameters(PS, cc);
            printProbabilities(PS, cc);
            printWeightInfo(PS, cc);
            if (normalise) PS.println(cc+" w "+SEP+"p(w)    Normalised Artefact Weight Distribution");
            else PS.println(cc+" w "+SEP+"n(w)     Unnormalised Artefact  Weight Distribution");
            for (int k=0; k<artWDArr.size(); k++)
            {
              if (normalise)  
              {
                  p = artWDArr.get(k)/((double) totalWeight);
                  if (p>0) PS.println(k+SEP+p);
              }
              else 
              {
                  n = ((int) (artWDArr.get(k)+0.5) );
                  if (n>0) PS.println(k+SEP+n);
              }
            }
            if (infolevel>0) System.out.println("Finished writing  Artefact weight distribution to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }


/**
     * Outputs the time evolution of the artefact evolution as text data file.
     *  <em>fileoutputNameRoot</em><tt>.aevol.dat</tt> unnormalised, or
     *  <em>fileoutputNameRoot</em><tt>.aevolnorm.dat</tt> normalised
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to switch on normalisation
     * @param artDegEvol is square array of integers [w][a] is degree of artefact a at time inetrval w, size must be as big or bigger than numStatsUpdates
     */

        void FileOutputArtefactTimeEvolution(String cc, boolean normalise, int[][] artDegEvol)
        {
        String extension;
        if (normalise) extension = ".aevolnorm.dat";
        else extension=".aevol.dat";
        String filenamecomplete = dirNameOutput+outputNameRoot+extension;
        
        // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
//            date= new Date();
            double p=0;
            int n=0;
            printParameters(PS, cc);
            printProbabilities(PS, cc);
            printWeightInfo(PS, cc);
            if (normalise) PS.println(cc+" t "+SEP+"k(a,t)/E    Normalised Artefact Degree");
            else PS.println(cc+" t "+SEP+"k(a,t)     Unnormalised Artefact Degree");
            for (int w=0; w<numStatsUpdates; w++) {
                PS.print(w*numberRewiringsPerUpdate);
                for (int a=0; a<this.numberArtefacts; a++) {
                    if (normalise)  PS.print(SEP+((double) artDegEvol[w][a])/((double) numberIndividuals));
                    else PS.print(SEP+artDegEvol[w][a]);
                } //eo for a
                PS.println();
            } //eo for w
            if (infolevel>0) System.out.println("Finished artefact degree evolution "+ filenamecomplete);
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        } //eo FileOutputArtefactTimeEvolution
  
        
        
    /**
     * Outputs a file with the artefact degree correlations in time.
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to switch on normalisation
     * @param artDegEvol is square array of integers [w][a] is degree of artefact a at time inetrval w, size must be as big or bigger than numStatsUpdates
     */
        void FileOutputArtefactTimeCorrelation(String cc, boolean normalise,  int[][] artDegEvol)
        {
        String extension;
        if (normalise) extension = ".atcorrnorm.dat";
        else extension=".atcorr.dat";
        String filenamecomplete = dirNameOutput+outputNameRoot+extension;
        
        // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            double dwa =0 ;
            //double dw =0 ;
            double dwabs =0 ;
            double dw2 =0 ;
            double k =0;
            double k2 = 0;
            printParameters(PS, cc);
            printProbabilities(PS, cc);
            printWeightInfo(PS, cc);
            PS.println(cc+" t "+SEP+"<k_a^2>"+SEP+"Delta_t (n)"+SEP+"Delta_t (|n|)"+SEP+"Delta_t (n^2)");
            for (int w=1; w<numStatsUpdates; w++) {
                PS.print(w*numberRewiringsPerUpdate);
                //dw =0 ;
                dwabs=0;
                dw2 =0 ;
                k2 = 0;
                for (int a=0; a<this.numberArtefacts; a++) {
                    k=artDegEvol[w][a];
                    k2 += k*k;
                    dwa = k-artDegEvol[w-1][a];
                    //dw += dwa;
                    dwabs += Math.abs(dwa);
                    dw2 += dwa*dwa;
                } //eo for a
                    PS.println(SEP+k2+SEP+dwabs+SEP+dw2);
            } //eo for w
            if (infolevel>0) System.out.println("Finished artefact degree time correlations "+ filenamecomplete);
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        } //eo FileOutputArtefactTimeEvolution
        
            /**
     * Outputs a file with the artefact degree correlations in time.
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to switch on normalisation
     * @param artDegEvol is square array of integers [w][a] is degree of artefact a at time inetrval w, size must be as big or bigger than numStatsUpdates
     * @param deltaUpdates separation in terms of number of updates to consider 
     */
        void FileOutputArtefactTimeCorrelation(String cc, boolean normalise,  int[][] artDegEvol, int deltaUpdates)
        {
        String extension;
        if (normalise) extension = ".atcorrnorm.dat";
        else extension=".atcorr.dat";
        String filenamecomplete = dirNameOutput+outputNameRoot+extension;
        
        // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            double dua =0 ;
            //double dw =0 ;
            double duabs =0 ;
            double du2 =0 ;
            double k =0;
            double k2 = 0;
            printParameters(PS, cc);
            printProbabilities(PS, cc);
            printWeightInfo(PS, cc);
            PS.println(cc+" t "+SEP+"<k_a^2>"+SEP+"Delta_t (n)"+SEP+"Delta_t (|n|)"+SEP+"Delta_t (n^2)");

            
            for (int u=1; u<numStatsUpdates; u++) {
            PS.print(u*numberRewiringsPerUpdate+numEventsFirstUpdate);
             for (int a=0; a<this.numberArtefacts; a++) {
                for (int du=0; du<=deltaUpdates; du++){
                k2 = 0;
                    k=artDegEvol[u][a];
                    k2 += k*k;
                    dua = k-artDegEvol[u-1][a];
                    duabs += Math.abs(dua);
                    du2 += dua*dua;
                    PS.println(SEP+k2+SEP+duabs+SEP+du2);
                } //eo for du
              } //eo for a
             } //eo for u
            
            if (infolevel>0) System.out.println("Finished artefact degree time correlations "+ filenamecomplete);
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        } //eo FileOutputArtefactTimeEvolution


        
        
    /**
     * Outputs the time evolution of the artefact evolution as graphics file.
     *  <em>fileoutputNameRoot</em><tt>w</tt><em>whiteValue*255</em><tt>b</tt><em>blackValue*255</em><tt>.aevol.dat</tt> 
     * unnormalised, or with extension <tt>.aevolnorm.dat</tt> if normalised.
     * Remember internally for RGB colours the scale is inverted with white (black) being colour 255 (0)
     * but in the file names we represent the input fractions.
     * @param cc comment characters put at the start of every line
     * @param artDegEvol is square array of integers [w][a] is degree of artefact a at time interval w
     *@param whiteFraction any artefact of degree (k/E) &lt; whiteFraction will be set to be white (colour 255)
     *@param blackFraction any artefact of degree (k/E) &gt; blackFraction will be set to be black (colour 0), even if it satisfies whiteFraction.
     *@return result returned is 0 if OK, -1 if problem with colour values, -2 if problem writing file.
     *
     */
        int FileGraphicsOutputArtefactTimeEvolution(String cc, int[][] artDegEvol, double whiteFraction, double blackFraction)
        {
        int result=0;
// set colour bounds
        int wv= (int) (255.5-whiteFraction*255);
        int bv= (int) (255.5-blackFraction*255);
        if (bv<0) {bv=0; result =-1;} // black is zero
        if (wv>255) {wv=255; result =-1;} // white is 255
        if (bv>wv) {bv=0; wv=255; result =-1;}
        String extension=".png";
        String filenamecomplete= dirNameOutput+outputNameRoot+"w"+(255-wv)+"b"+(255-bv)+extension;

        // Now convert into a pixel array
        //int [] pixel = new int [numStatsUpdates*maxNumberArtefacts];
        int offset=0;
        int col=-1;
        // Failed to use MemoryImageSource (see Schildt p815, pixel[p]=0xAARRGGBB) 
        // as it makes an Image but ImageIO.write only writes buffered image.
        // Colours are encoded as Hex values 0xAARRGGBB as follows:
        // A=transparency with opaque 255; for RGB colour model see wikipedia
        // (0, 0, 0) is black, (255, 255, 255) is white, (255, 0, 0) is red
        // see Eck 12.1.2 for RGB bitshift or routine at end
        BufferedImage bimg = new BufferedImage(numStatsUpdates,maxNumberArtefacts,BufferedImage.TYPE_BYTE_GRAY) ;
        for (int w=0; w<numStatsUpdates; w++) {
                for (int a=0; a<this.maxNumberArtefacts; a++) {
                    col= (int) (255.49 - (255*artDegEvol[w][a]/((double) numberIndividuals)) );
                    if (col>=wv) col=255;
                    if (col<=bv) col=0;
                    //pixel[offset+a]=(0xFF000000 | (col<<16) | (col <<8)| col );
                    bimg.setRGB(w,a,( (col<<16) | (col <<8)| col  ));
                } //eo for a
                offset+=numStatsUpdates;
            } //eo for w
        // For File see sec. 11.2(v5) Eck: 
        File fout2 = new File(filenamecomplete);
        // For MemoryImageSource see Schildt p815.  pixel[p]=0xAARRGGBB 
        // it produces an Image but ImageIO.write wants a buffered image!!!
        //MemoryImageSource mis = new MemoryImageSource(numStatsUpdates,numberArtefacts,pixel,0,numStatsUpdates);
        //Image img = Toolkit.getDefaultToolkit(). createImage(mis);
        // For ImageIO see sec. 12.1.5(v5) Eck: Images and Resources 
         try{ ImageIO.write(bimg,"png",fout2);} catch (IOException e) { System.out.println("File Error with "+ filenamecomplete); result=-2;}
         return result;
    }

     /**
     * Outputs time evolution data as a bmp file in the manner of speciation.
     * File is <dirNameOutput+outputNameRoot+filenameending>.tdata.bmp  
     * @param filenameending used to end the file name.
     */


        void FileOutputEvolutionImage(String filenameending)  
        {
            
        String filenamecomplete;
        PrintStream PS;
        String extension;

        extension=".tdata.bmp"; 
        filenamecomplete= dirNameOutput+outputNameRoot+filenameending+extension;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete); // open new file mode
            PS = new PrintStream(fout);
            
            // This is where we put the code.
            // Use Schildt p815 java.awt.image.MemoryImageSource to produce java Image object
            // from an array of data.  Then use javaworld.com tip 60 to write this out as bmp file
            // TimBMPSave code in TimUtilities project.
            //
            
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    
        if (infolevel>0) System.out.println("Finished writing degree distribution statistics to "+ filenamecomplete);

        try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}


        }
        
        
     /**
     * Outputs data at different times to same file.
     * <p>File is <em>dirNameOutput</em>+<em>outputNameRoot</em>+<em>filenameending</em><tt>.tdata.dat</tt>
     * if heading given, <tt>.tdatanh.dat</tt> extension if no heading given.
     * @param cc comment characters put at the start of every line.
     * @param filenameending used to end the file name.
     * @param DDStatsArr array of degree distribution statistics, one entry for each time
     * @param giveGeneralInformation if true first few lines are comments listing the general parameters 
     */
        void FileOutputTimeDependentData(String cc, String filenameending, DDStats [] DDStatsArr, boolean giveGeneralInformation)
        {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        if (giveGeneralInformation) extension=".tdata.dat"; else extension=".tdatanh.dat";
        filenamecomplete= dirNameOutput+outputNameRoot+filenameending+extension;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete); // open new file mode
            PS = new PrintStream(fout);
            printTimeDependentData(PS, cc, DDStatsArr, giveGeneralInformation);
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    
        if (infolevel>0) System.out.println("Finished writing degree distribution statistics to "+ filenamecomplete);

        try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}


        }// eo  FileOutputTimeDependentData

             /**
     * Outputs data at different times to same file.
     * File is <dirNameOutput+outputNameRoot+filenameending>.turnover.dat  
     * @param cc comment characters put at the start of every line.
     * @param filenameending used to end the file name.
     * @param giveGeneralInformation if true first few lines are comments listing the general parameters 
     */
        void FileOutputTurnoverTable(String cc, String filenameending, boolean giveGeneralInformation)  
        {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        if (giveGeneralInformation) extension=".turnover.dat"; else extension=".turnoverh.dat";
        filenamecomplete= dirNameOutput+outputNameRoot+filenameending+extension;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete); // open new file mode
            PS = new PrintStream(fout);
            printTurnoverTable(PS, cc, giveGeneralInformation);            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    
        if (infolevel>0) System.out.println("Finished writing degree distribution statistics to "+ filenamecomplete);

        try{ fout.close ();
             } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}


        }// eo  FileOutputTimeDependentData

        
// ***********************************************************************        
// PRINT routines

        
   /**
     * Outputs data at different times to same file.
     * <p>Suitable for R input if no comment string given
     * @param PS a PrintStream such as System.out
     * @param cc comment characters put at the start of every line.
     * @param DDStatsArr array of degree distribution statistics, one entry for each time
     * @param giveGeneralInformation if true first few lines are comments listing the general parameters
     */
        void printTimeDependentData(PrintStream PS, String cc, DDStats [] DDStatsArr, boolean giveGeneralInformation)
        {
//            date = new Date();
            if (giveGeneralInformation) printParameters(PS, cc);
            // must use artDDStatsArr[0] not template as this is set up with correct number of edges
            PS.println(cc+ "t" +SEP+ DDStatsArr[0].labelString(SEP)+ SEP+ COMMENTSTRING+VERSION +"_"+ date);
            for (int g=0; g<DDStatsArr.length ; g++) PS.println(numberRewiringsPerEvent*numEventsFirstUpdate+g*numberRewiringsPerUpdate +SEP+DDStatsArr[g].toString(SEP));
        }


   /**
     * Outputs table for easy plotting of turnover.
     * File is <dirNameOutput+outputNameRoot+filenameending>.turnover.dat  
     * @param PS a PrintStream such as System.out
     * @param cc comment characters put at the start of every line.
     * @param giveGeneralInformation if true first few lines are comments listing the general parameters
     */

        void printTurnoverTable(PrintStream PS, String cc, boolean giveGeneralInformation)  
        {
            if (giveGeneralInformation) printParameters(PS, cc);
            // must use artDDStatsArr[0] not template as this is set up with correct number of edges
            PS.print("Y" );
            for (int g=0; g<artDDStatsArr.length ; g++) PS.print(SEP+(numberRewiringsPerEvent*numEventsFirstUpdate+g*numberRewiringsPerUpdate)+SEP+"+/-");
            PS.println( SEP+ "AV"+SEP+ "+/-"+SEP+ VERSION +SEP+ date);
            for (int y=0; y<artefactDegreeRank.Y; y++) {
                PS.print((y+1));
                StatisticalQuantity ystats = new StatisticalQuantity();
                for (int g=0; g<artDDStatsArr.length ; g++) 
                {
                    double t = artDDStatsArr[g].turnover[y].getAverage();
                    ystats.add(t);
                    PS.print(SEP+t+SEP+artDDStatsArr[g].turnover[y].getError());
                }
                PS.println(SEP+ ystats.getAverage()+SEP+ ystats.getError());                
            }
            
        }


  /**
     * Outputs information on artefacts' properties to a print stream.
     * @param PS a PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     * @param sep separation string
     *@param nonZeroOnly true if want non zero values only
     */ 
        public void printArtefacts(PrintStream PS, String cc, String sep, boolean nonZeroOnly)
        {
            PS.println(cc+ "number artefacts = " + sep + numberArtefacts + sep+", max = " + sep + maxNumberArtefacts);
            if (rewireMode.artefactNeighbourList)
            {
            PS.println(cc+ "Art" + sep + artefactArray[0].stringInformationLabel(sep));
            for (int art=0; art<numberArtefacts; art ++)
                if ((!nonZeroOnly) || (artefactArray[art].degree >0)) {
                     PS.println(cc+ art + sep + artefactArray[art].stringInformation(sep) );
                }
            }
            printActiveArtefacts(PS, cc, sep);
        }

  /**
     * Outputs information on artefacts' properties to a print stream.
     * @param PS a PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     * @param sep separation string
     */ 
        public void printActiveArtefacts(PrintStream PS, String cc, String sep)
        {
            if (!rewireMode.activeArtefactList) return;
            PS.println(cc+  "number active artefacts = " + sep + activeArtefacts.size());
            for (int art=0; art<activeArtefacts.size(); art ++)
            { PS.print(activeArtefacts.get(art) + sep ); if (art%10 == 9) PS.println();}
            PS.println();
        }

        
    /**
     * Outputs information on individuals' properties to a print stream. 
     * @param PS a PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     * @param nonZeroOnly true if want non zero values only
     */

        public void printIndividuals(PrintStream PS, String cc, boolean nonZeroOnly)
        {
            int art=INDNOTATTACHED;
            PS.println(cc+ "number individuals = " + SEP + numberIndividuals + SEP+", max = " + SEP + maxNumberIndividuals);
            PS.println(cc+ "Ind" + SEP + "Art");
            for (int ind=0; ind<numberIndividuals; ind ++)
            {
                art=individualArray.getQuick(ind);
                if ((!nonZeroOnly) || (art>=0)) PS.println(ind + SEP + (art==INDNOTATTACHED?"*":art));
            }
        }

    /**
     * Outputs information on individuals' properties to a file. 
     * @param cc comment characters put at the start of every line
     * @param nonZeroOnly true if want non zero values only
     * @param filenameending appended to basic file name before extension added
     */
    void FileOutputIndividuals(String cc, String filenameending, boolean nonZeroOnly) {
        String filenamecomplete;
        PrintStream PS;
        String extension = ".ind.dat";
        filenamecomplete = dirNameOutput + outputNameRoot + filenameending + extension;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete); // open new file mode
            PS = new PrintStream(fout);
            this.printIndividuals(PS, cc, nonZeroOnly);
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file " + filenamecomplete);
            return;
        }

        if (infolevel > 0) System.out.println("Finished individuals listing to " + filenamecomplete);
        
        try { fout.close(); } 
        catch (IOException e) {
            System.out.println("File Error with " + filenamecomplete);
        }


    }// eo  FileOutputIndividuals

        
        
  /**
     * Outputs information on active individuals' properties to a print stream. 
     * @param PS a PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     * @param sep separation string
     */ 
        public void printActiveIndividuals(PrintStream PS, String cc, String sep)
        {
            if (!rewireMode.activeIndividualList) return;
            PS.println("Active Individuals");
            for (int i=0; i<ActiveIndividuals.degree; i++)
            { PS.print(ActiveIndividuals.getNeighbourQuick(i) + sep ); if (i%10 == 9) PS.println();}
            PS.println();
            if (!rewireMode.inactiveIndividualList) return;
            PS.println("Inactive Individuals");
            for (int i=0; i<InactiveIndividuals.degree; i++)
            { PS.print(InactiveIndividuals.getNeighbourQuick(i) + sep ); if (i%10 == 9) PS.println();}
            PS.println();
            PS.println(cc+  "number active/inactive individuals = " + sep + ActiveIndividuals.degree+  sep + InactiveIndividuals.degree +  sep+  " (Act + Inact - Total)=" + sep+ (ActiveIndividuals.degree+   InactiveIndividuals.degree-numberIndividuals) );
            
        }


        
// -------------------------------------------------------------
  /**
     * Outputs information on average degree properties to a print stream 
     * @param PS a PrintStream such as System.out
   * * @param cc comment characters put at the start of every line
     */
    public void printDegreeInfo(PrintStream PS, String cc)  
    {
        PS.println(cc+SEP+"k_min"+SEP+"k_cont"+SEP+"k_max"+SEP+"<k>"+SEP+"+/-" +SEP+"sigma"+SEP+"<k^2>");
        PS.println(cc+SEP+ artDegree.getMinimum() +SEP+degreeArtCont+SEP+artDegree.getMaximum()
                     +SEP+TruncDec(artDegree.getAverage(),2)+SEP+TruncDec(artDegree.getError(),2)
                     +SEP+TruncDec(artDegree.getSigma(),2)+SEP+TruncDec(artDegree.getSecondMoment(),2) );
        return;
    }
// -------------------------------------------------------------
  /**
     * Outputs information on average weight properties to a print stream 
     * @param PS a PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     */
    public void printWeightInfo(PrintStream PS, String cc)  
    {
        PS.println(cc+SEP+"w_min"+SEP+"w_cont"+SEP+"w_max"+SEP+"<w>"+SEP+"+/-" +SEP+"sigma" + "<w^2>");
        PS.println(cc+SEP+ weightArt.getMinimum()+SEP+weightArtCont+SEP+weightArt.getMaximum()
                     +SEP+TruncDec(weightArt.getAverage(),2)+SEP+TruncDec(weightArt.getError(),2) 
                     +SEP+TruncDec(weightArt.getSigma(),2)+SEP+TruncDec(weightArt.getSecondMoment(),2) );
        return;
    }

// -------------------------------------------------------------
  /**
     * Outputs information on average degree properties to a print stream 
     * @param PS a PrintStream such as System.out
   * * @param cc comment characters put at the start of every line
     */
    public void printActiveDegreeInfo(PrintStream PS, String cc)  
    {
        PS.println(cc +" No. active artefacts (vertices) : "+SEP+ numberActiveArtefacts);
        double ka = artDegree.getAverage()*numberArtefacts/((double) numberActiveArtefacts);
        PS.println(cc +SEP+ "<k_active>=" +SEP+TruncDec(ka,2) );
        return;
    }
         
// -------------------------------------------------------------
  /**
     * Outputs information on probabilities to a print stream 
     * @param PS a PrintStream such as System.out
   * * @param cc comment characters put at the start of every line
     */
    public void printProbabilities(PrintStream PS, String cc)  
    {
        PS.println(cc+SEP+prob.label(SEP)); 
        PS.println(cc+SEP+prob.toString(SEP) ); 
        return;
    }
         
        
        

//// --------------------------------------------------------------------------    
//    /** 
//     * Prints output method being used to a PrintS=tream.        
//     *  @param PS a PrintStream such as System.out
//     * @param cc comment characters put at the start of every line
//     */
//    public void printOutputMethod(PrintStream PS, String cc)  
//    {
//        PS.println(cc+" Output control method is "+outputcontrol);
//        PS.println(cc+"   Degree distribution calculation "+ (((outputcontrol & 1)>0)?"ON":"OFF") )  ;
//        PS.println(cc+"   Weight distribution calculation "+ (((outputcontrol & 2)>0)?"ON":"OFF") )  ;
////        PS.println(cc+"   Weight distribution calculation "+ (((outputcontrol & 4)>0)?"ON":"OFF") )  ;
////        PS.println(cc+"   Weight distribution calculation "+ (((outputcontrol & 8)>0)?"ON":"OFF") )  ;
//        PS.println(cc+"   Weight distribution for each time "+ (((outputcontrol & 16)>0)?"ON":"OFF") )  ;
//        PS.println(cc+"   Time evolutions of statistics "+ (((outputcontrol & 32)>0)?"ON":"OFF") )  ;    
//    }      

// --------------------------------------------------------------------------    
    /** 
     * Gives string describing the types of initial bipartite graph.
     *@param ibg initial bipartite graph number
     *@return string with description of initial bipartite graph
     */
    public String initialBipartiteGraphString(int ibg)  
    {
        String s=""; //Initial bipartite graph is ;
        switch (ibg)
        {
            case 0: s="even spread of artefacts"; break;
            case 1: s="all connected to artefact 0"; break;
            case 2: s="random artefacts"; break;
            default: s=" *** UNKNOWN ***";
        }
        return s;
    }      

    // --------------------------------------------------------------------------    
    /** 
     * Gives string describing the current initial bipartite graph.
     *@return string with description of initial bipartite graph
     */
    public String initialBipartiteGraphString()  
    {
        return initialBipartiteGraphString(initialBiGraph) ;
    }      

  
// --------------------------------------------------------------------------    
    /** 
     * Gives string with the pPref update mode.        
     * <br>These are the modes used by {@link #chooseArtFromInd(int, int, java.lang.Integer) }
     */
    public String ppModeString()  
    {
        switch (ppMode)
        {
            case 0: return("Copy Artefact of Random Individual");
            case 1: return("Copy Artefact of Neighbour on individual timgraph");
            case 2: return("Neighbouring artefact of artefact chosen by random individual");
            case 3: return("Copy artefact used by an active individual");
         default: 
        }
        return("Unknown ppMode");
     }      

    /** 
     * Gives String with the pRand update mode.        
     * <br>These are the modes used by {@link #chooseArtFromArt(int, int)}
     */
    public String prModeString()  
    {
        switch (prMode)
        {
            case 0: return("Choose random artefacts");
            case 1: return("Copy neighbour in artefact space");
            default: 
        }
        return("Unknown ppMode");
     }      
   
    
    /**
     * Prints to standard output information on general parameters.
     * Only if infolevel>=0.
     * @param cc comment characters put at the start of every line
     */

        public void printParameters(String cc)
        {
           if (infolevel>-1)  printParameters(System.out, cc);
        }
            /**
             *
             * Outputs information on general parameters.
             * @param PS a printstream such as System.out
             * @param cc comment characters put at the start of every line
             */

        public void printParameters(PrintStream PS, String cc)
        {
            PS.println(cc+" CopyModel.java version "+SEP+VERSION+SEP+" produced on "+SEP+ date);
            PS.print(cc+" Input                           : "+SEP);
            if (getInputFile) PS.println(dirNameInput+inputNameRoot);
            else PS.println(" Command Line Only");
            PS.println(cc+" Output                          : "+SEP+ dirNameOutput+outputNameRoot);
            
//            PS.println(cc+" Running Mode                    : "+SEP+ rewireMode.number +SEP+ rewireMode.getModeString() );
            PS.println(cc+" Repeating                       : "+SEP+ repeat);
            PS.println(cc+" pPref update Mode               : "+SEP+ ppMode+SEP+ppModeString() );
            PS.println(cc+" pRand update Mode               : "+SEP+ prMode+SEP+prModeString() );
            PS.println(cc+" Rewiring Mode                   : "+SEP+ rewireMode.number+SEP+rewireMode.getModeString() );
            PS.println(cc+" Influence Network               : "+SEP+ TimUtilities.BooleanAsString.booleanToOnOff(inflNetworkOn));
            PS.println(cc+" Initial No. artefacts           : "+SEP+ initialArtefacts);
            PS.println(cc+" Initial No. individuals         : "+SEP+ initialIndividuals );
            PS.println(cc+" Maximum No. artefacts           : "+SEP+ maxNumberArtefacts);
            PS.println(cc+" Maximum No. individuals         : "+SEP+ maxNumberIndividuals);
            PS.println(cc+" No. artefacts        (vertices) : "+SEP+ numberArtefacts);
            PS.println(cc+" No. individuals        (edges)  : "+SEP+ numberIndividuals );
            PS.println(cc+" Total no. rewirings             : "+SEP+ numberRewiringsTotal );
            PS.println(cc+" Rewirings per Stats update      : "+SEP+ numberRewiringsPerUpdate );
            PS.println(cc+" Rewirings per Network event     : "+SEP+ numberRewiringsPerEvent );
            PS.println(cc+" Initial bipartite graph         : "+SEP+ initialBiGraph+SEP+initialBipartiteGraphString());
            if (artDDStatsTemplate.Y>0) 
            PS.println(cc+" Turnover list length            : "+SEP+ artDDStatsTemplate.Y);
            else 
            PS.println(cc+" No turnover, length             : "+SEP+ artDDStatsTemplate.Y);
            printProbabilities(PS,cc);
            if (indNetwork ==null)  PS.println(cc+" --- No Individual Network");
            else{
                PS.println(cc+" +++ Individual Network Present                  +++");
                indNetwork.printParam();
                indNetwork.printWalkGraphInfo(PS,cc,-1);
                PS.println(cc+" +++ Random Walk for Copying on Individual Graph +++");
                PS.println(cc+" Length of copying random walk on Individual graph :"+SEP+ indRndWalkLength );
                PS.println(cc+"   Mode of copying random walk on Individual graph :"+SEP+ indRndWalkLength );
                if (indRandomWalk == null) PS.println(cc+" No random walk set up on individual network");
                else indRandomWalk.printRandomWalkMode(PS,cc+"      ");
                PS.println(cc+" +++                                             +++");
            }
            if (artNetwork ==null)  PS.println(cc+" --- No Artefact Network");
            else{
                PS.println(cc+" +++ Artefact Network Present                  +++");
                artNetwork.printParam();
                artNetwork.printWalkGraphInfo(PS,cc,-1);
                PS.println(cc+" +++ Random Walk for Copying on Artefact Graph +++");
                PS.println(cc+" Length of copying random walk on Artefact graph :"+SEP+ artRndWalkLength );
                PS.println(cc+"   Mode of copying random walk on Artefact graph :"+SEP+ artRndWalkLength );
                if (artRandomWalk == null) PS.println(cc+" No random walk set up on Artefact network");
                else artRandomWalk.printRandomWalkMode(PS,cc+"      ");
                PS.println(cc+" +++                                             +++");
            }
            PS.println(cc+" Artefact Statistics Collected            : "+SEP+ artDDStatsTemplate.statisticsMode+SEP+artDDStatsTemplate.statisticsModeString(SEP));
            PS.println(cc+" Influence Statistics Collected            : "+SEP+ inflDDStatsTemplate.statisticsMode+SEP+inflDDStatsTemplate.statisticsModeString(SEP));
            outputControl.printMode(PS,cc);
            PS.println(cc+" Information Level               : "+SEP+ infolevel);
            
// print out individual network characteristics
            //if (ppMode == 1)
        }
             

        

// *********************************************************************

// ........................................................................
   /**
     * Prints command line options.
     */
    public void printUsage()
    {

        CopyModel temp = new CopyModel();
        System.out.println("OPTIONS for CopyModel version "+VERSION+" on "+date+"\n");
        System.out.println();

        System.out.println("Notes on timings:-");
        System.out.println("In one EVENT (-te), many many rewirings are performed simultaneously.");
        System.out.println("  Thus this should be between 1 and the number of individuals.");
        System.out.println("  Exceptions: currently in true Bentley mode, internally for reasons");
        System.out.println("  of coding, one even contains two network updates and so 2E rewirings.");
        System.out.println("  Also when rewiring an artefact one event is one artefact having its");
        System.out.println("  edges redistributed. Thus the rewirings per event timing input parameter");
        System.out.println("  (-te) is artificial and is only used to set the time scale.");
        System.out.println("  The actual no. of rewirings per event is variable.");
        System.out.println("An UPDATE (-te) is where statistics are taken and updated.");
        System.out.println("   Must contain at least one event.");
        System.out.println("Thus typically (-te) < (-tu) < (-tt) and (-tf) is an integer multiple of (-tu)");

        System.out.println();

        System.out.println(" -ag?         help on timgraph usage");
        System.out.println(" -ag<various> timgraph argument for artefact network e.g. -ag-e100 ");
        System.out.println("              may repeat several times and are cumulative");
        System.out.println(" -awl<double> length of random walks made on the artefact graph, default "+temp.artRndWalkLength);
        System.out.println(" -awm<int>    mode of random walks made on the artefact graph, default "+temp.artRndWalkMode);
        System.out.println(" -di<dirNameInput>    input directory name, default "+temp.dirNameInput);
        System.out.println(" -do<dirNameOutput>  output directory name, default "+temp.dirNameOutput);
//        System.out.println(" -et<int>     number of time steps (rewire events), default "+temp.numEventsTotal);
//        System.out.println(" -ew<int>     number of time steps per weight counting (-1=no mid run updates), default "+temp.numEventsPerUpdate);
        System.out.println(" -fi<inputNameRoot>  use inputNameRoot+_input.dat for input file names, default "+temp.inputNameRoot);
        System.out.println(" -fo<outputNameRoot> use outputNameRoot for root of output file names, default "+temp.outputNameRoot);
        System.out.println(" -Fi<inputNameRoot>  use inputNameRoot+VERSION number+_input.dat for input file");
        System.out.println(" -Fo<outputNameRoot> use outputNameRoot+VERSION number for root of output file names");
        System.out.println(" -g<int>      initial bipartite graph number, default "+temp.initialBiGraph+" =" );
        System.out.println("              0: "+initialBipartiteGraphString(0));
        System.out.println("              1: "+initialBipartiteGraphString(1));
        System.out.println("              2: "+initialBipartiteGraphString(2));
        System.out.println(" -ig?         help on timgraph usage");
        System.out.println(" -ig<various> timgraph argument for individual network e.g. -ig-e1000 ");
        System.out.println("              may repeat several times and are cumulative");
        System.out.println(" -iwl<double> length of random walks made on the individual graph, default "+temp.indRndWalkLength);
        System.out.println(" -iwm<int>    mode of random walks made on the individual graph, default "+temp.indRndWalkMode);



        System.out.println(" -I<boolean>  influence network (leaders and followers) on (off), default "+TimUtilities.BooleanAsString.booleanToOnOff(temp.inflNetworkOn));
        System.out.println("                Note:- Influence network may only work for modes 4 and above.");
        System.out.println(" -mr<int>     rewire mode: default "+temp.rewireMode.number+" = "+temp.rewireMode.getModeString());
        for (int i=0; i<RewireMode.allowedModes.length;i++) System.out.println("                        "+RewireMode.allowedModes[i]+": "+rewireMode.modeString(RewireMode.allowedModes[i]));

        System.out.println(" -na<int>     initial no. artefacts, default "+temp.numberArtefacts);
        System.out.println(" -ni<int>     initial no. individuals, default "+temp.numberIndividuals);
//        System.out.println(" -pa<int>    probability of choosing random Active artefact, default "+temp.pRandact);
        System.out.println(" -ppp<int>    probability of pPref (copying artefact) event, default "+temp.prob.pPref);
        System.out.println(" -ppm<int>    pPref update mode: 0 random, 1 individual network, default "+temp.ppMode);
        System.out.println(" -prp<int>    probability of pRand (random artefact) event, default "+temp.prob.pRand);
        System.out.println(" -prm<int>    pRand update mode: 0 random, 1 artefact network, default "+temp.prMode);
        System.out.println("              (N.B. if either pRand or pPref are negative then the modulus of that value is used");
        System.out.println("                    while the other probability is set so that the sum is one.");
        System.out.println("                    This means pBar is set to zero.)");
        System.out.println(" -r<int>      number of times to repeat the run, default "+temp.repeat);
        System.out.println(" -te<int>     number of rewirings per network change event (X), default "+temp.numEventsTotal);
        System.out.println(" -tf<int>     number of rewirings before first statistics update, default "+temp.numEventsFirstUpdate);
        System.out.println(" -tt<int>     total number of rewirings, default "+temp.numEventsTotal);
        System.out.println(" -tu<int>     number of rewirings per statistics update (-1=no mid run updates), default "+temp.numEventsPerUpdate);
        System.out.println(" -s?...       In the following -s options for time evolution statistics, character 2=? is:-");
        System.out.println("                  ?=a artefact network DD stats,");
        System.out.println("                  ?=i influence DD statistics.");
        System.out.println(" -s?e<double>  error used when measuring a statistic by sampling, default a="+temp.artStatisticsError+", i="+temp.inflStatisticsError);
        System.out.println(" -s?f<int>     order of F_n to keep statistics on, default a="+temp.artDDStatsTemplate.maxFnumber+", i="+temp.inflDDStatsTemplate.maxFnumber);
        System.out.println(" -s?kb<int>    maximum k value of lower continuous range to keep n(k) statistics on, default  a="+temp.artDDStatsTemplate.kvalue.getBottomMaximum()+", i="+temp.inflDDStatsTemplate.kvalue.getBottomMaximum());
        System.out.println(" -s?ki<int>    k intervals of mid range to keep n(k) statistics on, default  a="+temp.artDDStatsTemplate.kvalue.getInterval()+", i="+temp.inflDDStatsTemplate.kvalue.getInterval());
        System.out.println(" -s?kt<int>    minimum k value of upper continuous range to keep n(k) statistics on, default  a="+temp.artDDStatsTemplate.kvalue.getTopMinimum()+", i="+temp.inflDDStatsTemplate.kvalue.getTopMinimum());
        System.out.println(" -s?r<int>     number rho(n) values to keep statistics on");
        System.out.println("                -1 = off, 0= random individual neighbours, n-th n.n.");
        System.out.println("                default  a="+temp.artDDStatsTemplate.maxrhonumber+", i="+temp.inflDDStatsTemplate.maxrhonumber);
        System.out.println(" -s?m<int>     statistics mode, default  a="+temp.artDDStatsTemplate.statisticsMode+", i="+temp.inflDDStatsTemplate.statisticsMode);
        System.out.println(" -s?u<int>     number mu(n) moments to keep statistics on, default a="+temp.artDDStatsTemplate.maxMoment+", i="+temp.artDDStatsTemplate.maxMoment);
        System.out.println(" -s?x<boolean> set time evolution statistics on (off), default a="+temp.artDDStatsOn+", i="+temp.inflDDStatsOn);
        System.out.println(" -s?Y<int>     set the maximum size of turnover list to follow, default a="+artDDStatsTemplate.Y);
        System.out.println(" -s?m<int>     statistics modes:-");
        DDStats tempdds = new DDStats(2, 1, new ValueRange(0,0,0,0,0), 1, artDDStatsTemplate.Y, 0);
        for (int sm=1; sm<DDStats.maxStatisticsMode; sm*=2)
        {
            System.out.println("              (s?m & "+sm+") ? "+DDStats.statisticsModeString(sm,SEP)+" on : (off)");
        }
//        System.out.println(" -sim<int>     influence statistics mode, default i="+temp.inflDDStatsTemplate.statisticsMode);
//        tempdds = new DDStats(2, 1, new ValueRange(0,0,0,0,0), 1, inflDDStatsTemplate.Y, 0);
//        for (int sm=1; sm<DDStats.maxStatisticsMode; sm*=2)
//        {
//            System.out.println("              (sim & "+sm+") ? "+DDStats.statisticsModeString(sm,SEP)+" on : (off)");
//        }

                System.out.println(" -o<int> output modes , default "+temp.outputControl.number+" = "+temp.outputControl.modeString);
                System.out.println("  o modes: (o&  1) ? Each run Raw Degree distribution calc and output on : (off)");
                System.out.println("         : (o&  2) ? Each run Raw Weight distribution calc and output on : (off)");
                System.out.println("         : (o&  4) ? Each run Binned Degree distribution calc and output on : (off)");
                System.out.println("         : (o&  8) ? Each run Binned Weight distribution calc and output on : (off)");
                System.out.println("         : (o& 16) ? Each run Distributions output every time update statistics on : (off)");
                System.out.println("         : (o& 32) ? Output statistics averaged over all runs on : (off)");
                System.out.println("         : (o& 64) ? Each run output time evolution of artefact occupation,");
                System.out.println("                     statistics gathered each statistics update time on : (off)");
                System.out.println("         : (o&128) ? List of Individuals and their artefacts each run on : (off)");
                System.out.println("         : (o&256) ? Each run output time evolution of influence degree,");
                System.out.println("                     statistics gathered each statistics update time on : (off)");
                System.out.println(" -xi<int>      debugging information level, default "+temp.infolevel);


    }

    /** 
     * Parses command arguments.     
     * Arguments are started by a character equal to the PARAM global separated by white space.
     *@param ArgList string array of arguments. 
     *@return 0 if OK, otherwise failed.
     */
    public int parseParam(String[] ArgList)  {
     
        String [] tempindNetworkArgs = new String[100];
        String [] tempartNetworkArgs = new String[100];
        int tempNINA=0; // number of temporary individual network arguments
        int tempNANA=0; // number of temporary artefact network arguments
        int error =0;
//        System.out.println(args.length+" command line arguments");
        double pp=prob.pPref;
        double pr=prob.pRand;
        //numberRewiringsPerEvent =1;        
        if (infolevel>-1) for (int j =0; j<ArgList.length; j++){System.out.println("Argument "+j+" = "+ArgList[j]);}
        
                for (int i=0;i<ArgList.length ;i++){
                    if (ArgList[i].length() <3) {
                        message.printERROR(" Argument "+ArgList[i]+" is too short");
                        printUsage();
                        return 3;}
                        if (ArgList[i].charAt(0) !=PARAM){
                            message.printERROR(" Argument "+ArgList[i]+" does not start with -, use -? for usage");
                            return 4;}
                            switch (ArgList[i].charAt(1)) {
                                case 'a': {
                                    if (ArgList[i].charAt(2)=='g' ) if (ArgList[i].charAt(3)=='?' ) {timgraph tgtemp = new timgraph(); tgtemp.printUsage();}
                                        else tempartNetworkArgs[tempNANA++] = ArgList[i].substring(3);
                                    if (ArgList[i].charAt(2)=='w' ) 
                                    {
                                        if (ArgList[i].charAt(3)=='l' ) artRndWalkLength =  Double.parseDouble(ArgList[i].substring(4));
                                        if (ArgList[i].charAt(3)=='m' ) artRndWalkMode =  Integer.parseInt(ArgList[i].substring(4));
                                    }
                                break;}  
                                case 'd': {
                                    if (ArgList[i].charAt(2)=='i' ) dirNameInput = ArgList[i].substring(3);
                                    if (ArgList[i].charAt(2)=='o' ) dirNameOutput = ArgList[i].substring(3);
                                break;}
                                case 'e': { //Integer.parseInt(ArgList[i].substring(2));
                                    System.out.println("*** Argument -e obsolute, use -t");
                                    break;
                                    //if (ArgList[i].charAt(2)=='t' ) numEventsTotal = Integer.parseInt(ArgList[i].substring(3));
                                    //if (ArgList[i].charAt(2)=='w' ) numEventsPerUpdate = Integer.parseInt(ArgList[i].substring(3));
                                }
                                case 'f': {
                                    if (ArgList[i].charAt(2)=='i' ) {getInputFile =true; inputNameRoot = ArgList[i].substring(3);}
                                    if (ArgList[i].charAt(2)=='o' ) outputNameRoot = ArgList[i].substring(3);
                                break;}
                                case 'F': {
                                    if (ArgList[i].charAt(2)=='i' ) {getInputFile =true; inputNameRoot = ArgList[i].substring(3)+VERSION;}
                                    if (ArgList[i].charAt(2)=='o' ) outputNameRoot = ArgList[i].substring(3)+VERSION;
                                break;}
                                case 'g': {initialBiGraph = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'i': {
                                    if (ArgList[i].charAt(2)=='g' ) if (ArgList[i].charAt(3)=='?' ) {timgraph tgtemp = new timgraph(); tgtemp.printUsage();}
                                        else tempindNetworkArgs[tempNINA++] = ArgList[i].substring(3);
                                    if (ArgList[i].charAt(2)=='w' ) 
                                    {
                                        if (ArgList[i].charAt(3)=='l' ) indRndWalkLength =  Double.parseDouble(ArgList[i].substring(4));
                                        if (ArgList[i].charAt(3)=='m' ) indRndWalkMode =  Integer.parseInt(ArgList[i].substring(4));
                                    }
                                break;}
                                case 'I': {inflNetworkOn = TimUtilities.StringAsBoolean.isFirstCharacterTrue(ArgList[i].substring(2));
                                break;}
                                case 'm': {
                                    if (ArgList[i].charAt(2)=='r' ) rewireMode.set(Integer.parseInt(ArgList[i].substring(3)));
                                break;}                                
                                case 'n': {
                                    if (ArgList[i].charAt(2)=='a' ) initialArtefacts=Integer.parseInt(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='i' ) initialIndividuals=Integer.parseInt(ArgList[i].substring(3));
                                break;}
                                case 'o': {outputControl.set(ArgList[i].substring(2));
                                break;}
                                case 'p': {
                                    if (ArgList[i].charAt(2)=='p' ) 
                                    {
                                       if (ArgList[i].charAt(3)=='p' ) pp= Double.parseDouble(ArgList[i].substring(4));
                                       if (ArgList[i].charAt(3)=='m' ) ppMode = Integer.parseInt(ArgList[i].substring(4));
                                       }
                                    if (ArgList[i].charAt(2)=='r' )
                                    {
                                        if (ArgList[i].charAt(3)=='p' ) pr= Double.parseDouble(ArgList[i].substring(4));
                                        if (ArgList[i].charAt(3)=='m' ) prMode = Integer.parseInt(ArgList[i].substring(4));
                                    }
//                                    if (ArgList[i].charAt(2)=='a' ) pRandact= Double.parseDouble(ArgList[i].substring(3));
                                break;}
//                                case 'q': {probrndvertex= Double.parseDouble(ArgList[i].substring(2));
//                                break;}
                                case 'r': {
                                    repeat = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 's': {
                                 if (ArgList[i].charAt(2)=='a' ){ // artefact network DD statistics
                                    if (ArgList[i].charAt(3)=='e' ) artStatisticsError=Double.parseDouble(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='f' ) artDDStatsTemplate.maxFnumber=Integer.parseInt(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='k' )
                                    {
                                        if (ArgList[i].charAt(4)=='b' ) artDDStatsTemplate.kvalue.setBottomMaximum(Integer.parseInt(ArgList[i].substring(5)));
                                        if (ArgList[i].charAt(4)=='i' ) artDDStatsTemplate.kvalue.setInterval(Integer.parseInt(ArgList[i].substring(5)));
                                        if (ArgList[i].charAt(4)=='t' ) artDDStatsTemplate.kvalue.setTopMinimum(Integer.parseInt(ArgList[i].substring(5)));
                                    }
                                    if (ArgList[i].charAt(3)=='m' ) artDDStatsTemplate.statisticsMode=Integer.parseInt(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='r' ) artDDStatsTemplate.maxrhonumber=Integer.parseInt(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='t' )
                                    {
                                        if (ArgList[i].charAt(4)=='Y' ) artDDStatsTemplate.Y = Integer.parseInt(ArgList[i].substring(5));
                                    }
                                    if (ArgList[i].charAt(3)=='u' ) artDDStatsTemplate.maxMoment=Integer.parseInt(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='x' ) artDDStatsOn = TimUtilities.StringAsBoolean.isFirstCharacterTrue(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='Y' ) artDDStatsTemplate.Y =Integer.parseInt(ArgList[i].substring(4));
                                 } // eo if (2)=='a'
                                 if (ArgList[i].charAt(2)=='i' ){ // influence network DD statistics
                                    if (ArgList[i].charAt(3)=='e' ) inflStatisticsError=Double.parseDouble(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='f' ) inflDDStatsTemplate.maxFnumber=Integer.parseInt(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='k' )
                                    {
                                        if (ArgList[i].charAt(4)=='b' ) inflDDStatsTemplate.kvalue.setBottomMaximum(Integer.parseInt(ArgList[i].substring(5)));
                                        if (ArgList[i].charAt(4)=='i' ) inflDDStatsTemplate.kvalue.setInterval(Integer.parseInt(ArgList[i].substring(5)));
                                        if (ArgList[i].charAt(4)=='t' ) inflDDStatsTemplate.kvalue.setTopMinimum(Integer.parseInt(ArgList[i].substring(5)));
                                    }
                                    if (ArgList[i].charAt(3)=='m' ) inflDDStatsTemplate.statisticsMode=Integer.parseInt(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='r' ) inflDDStatsTemplate.maxrhonumber=Integer.parseInt(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='t' )
                                    {
                                        if (ArgList[i].charAt(4)=='Y' ) inflDDStatsTemplate.Y = Integer.parseInt(ArgList[i].substring(5));
                                    }
                                    if (ArgList[i].charAt(3)=='u' ) inflDDStatsTemplate.maxMoment=Integer.parseInt(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='x' ) inflDDStatsOn = TimUtilities.StringAsBoolean.isFirstCharacterTrue(ArgList[i].substring(4));
                                    if (ArgList[i].charAt(3)=='Y' ) inflDDStatsTemplate.Y =Integer.parseInt(ArgList[i].substring(4));
                                  } // eo if (2)=='i'
                                break;}

//                                case 's': {
//                                    if (ArgList[i].charAt(2)=='e' ) artStatisticsError=Double.parseDouble(ArgList[i].substring(3));
//                                    if (ArgList[i].charAt(2)=='f' ) artDDStatsTemplate.maxFnumber=Integer.parseInt(ArgList[i].substring(3));
//                                    if (ArgList[i].charAt(2)=='i' ) rewireMode.setCopiedFromOn(TimUtilities.StringAsBoolean.isFirstCharacterTrue(ArgList[i].substring(3)));
//                                    if (ArgList[i].charAt(2)=='k' )
//                                    {
//                                        if (ArgList[i].charAt(3)=='b' ) artDDStatsTemplate.kvalue.setBottomMaximum(Integer.parseInt(ArgList[i].substring(4)));
//                                        if (ArgList[i].charAt(3)=='i' ) artDDStatsTemplate.kvalue.setInterval(Integer.parseInt(ArgList[i].substring(4)));
//                                        if (ArgList[i].charAt(3)=='t' ) artDDStatsTemplate.kvalue.setTopMinimum(Integer.parseInt(ArgList[i].substring(4)));
//                                    }
//                                    if (ArgList[i].charAt(2)=='m' ) artDDStatsTemplate.statisticsMode=Integer.parseInt(ArgList[i].substring(3));
//                                    if (ArgList[i].charAt(2)=='r' ) artDDStatsTemplate.maxrhonumber=Integer.parseInt(ArgList[i].substring(3));
//                                    if (ArgList[i].charAt(2)=='t' )
//                                    {
//                                        if (ArgList[i].charAt(3)=='Y' ) artDDStatsTemplate.Y = Integer.parseInt(ArgList[i].substring(4));
//                                    }
//                                    if (ArgList[i].charAt(2)=='u' ) artDDStatsTemplate.maxMoment=Integer.parseInt(ArgList[i].substring(3));
//                                    if (ArgList[i].charAt(2)=='Y' ) artDDStatsTemplate.Y =Integer.parseInt(ArgList[i].substring(3));
//                                break;}
                                case 't': { //Integer.parseInt(ArgList[i].substring(2));
                                    if (ArgList[i].charAt(2)=='e' ) numberRewiringsPerEvent =Integer.parseInt(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='f' ) numberRewiringsFirstUpdate =Integer.parseInt(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='t' ) numberRewiringsTotal = Integer.parseInt(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='u' ) numberRewiringsPerUpdate = Integer.parseInt(ArgList[i].substring(3));
                                break;}
                                
                                case 'x': {
                                    if (ArgList[i].charAt(2)=='i' ) infolevel=Integer.parseInt(ArgList[i].substring(3));
                                break;}
                               case '?': {printUsage();
                                return 1;}
                                default:{
                                    System.out.println("\n*** Argument "+ArgList[i]+"not known, usage:");
                                    printUsage();
                                    return 2;
                                }
                                
                            }
                }
        // Parse individual graph arguments
        if (tempNINA>0){ // found arguments for the individual graph
            if (indNetworkNumberArgs == 0)
            {    // have no existing arguments, copy new ones to permanent list
                indNetworkNumberArgs=tempNINA;
                indNetworkArgs = new String [indNetworkNumberArgs];
                for (int iii=0; iii<indNetworkNumberArgs; iii++) indNetworkArgs[iii] = tempindNetworkArgs[iii] ;
            }
            else
            { // have existing arguments make new list = old list + newly read in ones
              String [] oldArgs = new String [indNetworkNumberArgs];
              for (int iii=0; iii<indNetworkNumberArgs; iii++) oldArgs[iii] = indNetworkArgs[iii] ;    
              indNetworkNumberArgs+=tempNINA;
              indNetworkArgs = new String [indNetworkNumberArgs];
              for (int iii=0; iii<oldArgs.length; iii++) indNetworkArgs[iii] = oldArgs[iii] ; 
              for (int iii=0; iii<tempNINA; iii++) indNetworkArgs[iii+oldArgs.length] = tempindNetworkArgs[iii] ;
            }           
        } // if tempNINA

        // Parse artefact graph arguments
        if (tempNANA>0){ // found arguments for the artefact graph
            if (artNetworkNumberArgs == 0)
            {    // have no existing arguments, copy new ones to permanent list
                artNetworkNumberArgs=tempNANA;
                artNetworkArgs = new String [artNetworkNumberArgs];
                for (int iii=0; iii<artNetworkNumberArgs; iii++) artNetworkArgs[iii] = tempartNetworkArgs[iii] ;
            }
            else
            { // have existing arguments make new list = old list + newly read in ones
              String [] oldArgs = new String [artNetworkNumberArgs];
              for (int iii=0; iii<artNetworkNumberArgs; iii++) oldArgs[iii] = artNetworkArgs[iii] ;    
              artNetworkNumberArgs+=tempNANA;
              artNetworkArgs = new String [artNetworkNumberArgs];
              for (int iii=0; iii<oldArgs.length; iii++) artNetworkArgs[iii] = oldArgs[iii] ; 
              for (int iii=0; iii<tempNANA; iii++) artNetworkArgs[iii+oldArgs.length] = tempartNetworkArgs[iii] ;
            }           
        } //eo  if tempNANA

        if (prob.setPpPr(pp,pr)) {
            System.out.println("*** Error probabilities wrong") ;
            System.out.println(prob.label(SEP));
            System.out.println(prob.toString(SEP));
            return 5;
        }
        
        // Check if we have a real output directory
        File dir = new File(dirNameOutput);
            if (!dir.isDirectory()) 
            {
                message.printERROR(dirNameOutput+" is not a directory");
                return 1;
            }
            
        if ((!rewireMode.fixedNumberIndividuals)  && (ppMode>-1)) 
        {
            ppMode=-1; 
            System.out.println("pp mode must allow variable no. of Ind., set to be "+ppMode);
        }
        
        // set up individual Network when needed
           if ((ppMode ==1) || (artDDStatsTemplate.maxrhonumber>0)|| (inflDDStatsTemplate.maxrhonumber>0)) indNetworkOn=true;
        // set up artefact Network when needed
           if ((prMode ==1) || (ppMode ==2)   ) artNetworkOn=true;
        
        // Set timescales assuming inputs are in terms of rewirings
        // However all are recalculated in terms of numEventsPerUpdate and numberRewiringsPerEvent
        // to allow for int arithmetic rounding issues.
        if (rewireMode.trueBentley) numberRewiringsPerEvent = initialIndividuals *2;
        if (numberRewiringsPerUpdate>0) numEventsPerUpdate = numberRewiringsPerUpdate/numberRewiringsPerEvent;
        else numEventsPerUpdate= numberRewiringsTotal/numberRewiringsPerEvent;
        if (numEventsPerUpdate<1) numEventsPerUpdate=1;
        numberRewiringsPerUpdate = numEventsPerUpdate *numberRewiringsPerEvent;
        numUpdates = numberRewiringsTotal/numberRewiringsPerUpdate; 
        numberRewiringsTotal = numUpdates *numberRewiringsPerUpdate; 
        numEventsTotal = numEventsPerUpdate * numUpdates;
        if (numberRewiringsFirstUpdate<0) numberRewiringsFirstUpdate=0;
        if (numberRewiringsFirstUpdate>numberRewiringsTotal) numberRewiringsFirstUpdate=numberRewiringsTotal;
        int firstUpdateNumber = numberRewiringsFirstUpdate /numberRewiringsPerUpdate ;
        numEventsFirstUpdate=firstUpdateNumber * numEventsPerUpdate;
        numStatsUpdates= numUpdates-firstUpdateNumber+1;
            //System.out.println("rewireMode,numEventsPerUpdate,initialIndividuals = "+rewireMode+", "+numEventsPerUpdate+", "+initialIndividuals);
    return 0;    
    }//eo ParamParse
    
   
// ........................................................................
    /**
     * Parse the parameters given in file.
     *@return 0 if no problems
     */   
    public int parseParameterFile() {
// Check if we have a real input directory
        File dir = new File(dirNameOutput);
        if (!dir.isDirectory()) {
            message.printWARNING(dirNameInput+" is not a directory, no files read in");
            return 6;
        }
        
        ParameterInput pi = new ParameterInput(CC, PARAM, 100, infolevel);
        String fullInputFileName = dirNameInput+inputNameRoot+"_input.dat";
        if (pi.readParameters(fullInputFileName)!=0) {
            message.printERROR(" Failed to read data properly from file "+fullInputFileName);
            return 7;
        }
        if (parseParam(pi.parameterList)!=0) {
            message.printERROR(" Failed to parse parameters read in from input file "+fullInputFileName);
            return 8;
        }
        
        
        return 0;
    }   
    

 

// **********************************************************************
// UTILITY Routines
    /**
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public double TruncDec(double value, int dec)
    {
      double shift = Math.pow(10,dec);
      return ( ( (double) ((int) (value*shift+0.5)))/shift);
    }
    
//// --------------------------------------------------------------------
//    // Timing routines now in TimTime class of TimUtilities package

// (A=transparency with opaque 255; for RGB colour model see wikipedia
        // (0, 0, 0) is black, (255, 255, 255) is white, (255, 0, 0) is red
        // see Eck 12.1.2 for RGB bitshift
        
 public int pixelValue(int alpha, int red, int green, int blue)
    {
     return ((alpha<<24) | (red<<16) | (green <<8)| blue );   
    }


    
    
} // eo CopyModel class
