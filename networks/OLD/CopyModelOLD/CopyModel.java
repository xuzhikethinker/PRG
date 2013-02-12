/*
 * CopyModel.java
 *
 * Created on 16 December 2005, 17:47
 *
 * Simulates Copy Models a la Bentley and related generalised bipartite rewiring
 * problems.
 */

/**
 *
 * @author time
 */

    import cern.colt.list.IntArrayList;
    import cern.colt.list.DoubleArrayList;
//import cern.colt.list.ObjectArrayList;
    
    import java.util.*;
    import java.util.Date;
    import java.util.Random; //p524 Schildt
//import java.util.*;
    import java.util.AbstractSet;
//import java.lang.Object.*;
//import java.lang.Math.*;
    
    import java.io.*;


public class CopyModel {
    
    String SEP = "\t "; // separation character e.g. \t for tab
    String Version = "060201";
    Date date = new Date();
    Random Rnd;
    String nameroot = "test";
    String dirNameOutput = "/PRG/networks/CopyModel/output/";
    int infolevel =0;
    int outputcontrol =15;
    int runMode = 0; // Selects type of model run
    boolean modeBentley;
    double initialtime;
    
    boolean addIndToArtError=false;

        int numevents=10;
        int maxNumberArtefacts=100;
        int maxNumberIndividuals=100;
        int numberIndividuals = 10;
        int numberArtefacts = 10;
        int numberActiveArtefacts =0;
        int initialIndividuals =0;
        int initialArtefacts = 0;
        int weightCountUpdate= -1;

        
        int[] individualArray;
        int[] individualArray2;
        Vertex[] artefactArray;
        int initialgraph =0;
        
        double pbar=0;
        double ppref=1;
        double prand=0.0;
        double prandact=0.0;
        
        StatQuant degreeArt; 
        IntArrayList artDDArr;
        int degreeArtCont =-1;
//        IntArrayList artDDTotArr;
        StatQuant weightArt; 
        IntArrayList artWDArr;
//        IntArrayList artWDTotArr; // could be used for multgiple runs
        int weightArtCont =-1;
        int totalWeight;

    
    
    /** Creates a new instance of CopyModel */
        public CopyModel()
        {
            setInitTime();    
            Rnd = new Random(); //Schildt p524, time is used as seed
                if (infolevel>0) System.out.println("Uses time to seed Rnd");
        }
        
//        public CopyModel(int setMaxNumberIndividuals, int setMaxNumberArtefacts)
//        {
//          maxNumberArtefacts=setMaxNumberArtefacts;
//          maxNumberIndividuals=setMaxNumberIndividuals;
//          artefactArray= new Vertex[setMaxNumberArtefacts];
//          individualArray = new int[setMaxNumberIndividuals];  
//          numberIndividuals = 0;
//          numberArtefacts = 0;
//        }
  
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        CopyModel cm = new CopyModel();
        System.out.println("\n***********************************************************");
        System.out.println("       STARTING CopyModel version "+ cm.Version+" on "+cm.date+"\n");
        cm.ppref=0.99;
        cm.numevents=10000;
        cm.initialIndividuals=1000;
        cm.initialArtefacts=1000;
        cm.runMode=0;  
        if (cm.parseParam(args)>0) return;
        int initialIndividuals = cm.numberIndividuals;
        int initialArtefacts = cm.numberArtefacts;
        
        
        cm.runGeneralModel() ;
    
    }

    
      
        
    
    /** Runs a general version of the model
     * @param args the command line arguments
     */
    public void runGeneralModel() 
    {
        initialiseModel(initialIndividuals , initialArtefacts, 0,0);

        printParameters(System.out, "");
        if ((numberIndividuals<20)  || ((infolevel & 1) == 1))
        {
            printIndividuals(System.out, " ",false);
            printArtefacts(System.out, " ",false);
        }

        System.out.println("Number Artefacts vs array length"+SEP+numberArtefacts+SEP+artefactArray.length);
        System.out.print("Adding artefacts:\n");
        int eventnote = numevents+1; //should give no progress indication on screen
        double eventnotefactor=0.01;
        if (eventnotefactor >0) eventnote = (int) ( ((double)numevents) * eventnotefactor);
        if (eventnote<1) eventnote=1;

        int artadd=-1;
        setInitTime(); 
        boolean trueBentley = ( (runMode==0) ? true : false );
        for (int t=1; t<=numevents; t++) 
        {
                if (trueBentley) artadd=trueBentleyRewire(); else artadd=rewire();
                
                if (infolevel>2)
                {
                    System.out.print(artadd+" ");
                    if (t%numevents == 0) System.out.println();
                 }
                else
                {
                    if (t%eventnote == 0 ) 
                    { 
                        System.out.print(".");
                        if (t%(10*eventnote) == 0) 
                        {
                         System.out.println(" "+runTimeString());   
                        }
                    }
               // Updates the weights at given time
               if (t%weightCountUpdate ==0) for (int a=0; a<numberArtefacts; a++) artefactArray[a].weight+=artefactArray[a].degree;
               
                 
                }//eo else
        }//eo for i
        System.out.println("\n Finnished adding "+numberArtefacts+" artefacts in "+runTimeString()); 
        
        if (numberIndividuals<20)  printIndividuals(System.out, " ", true);
        int numBentleyArt =-1;
        if (modeBentley)
        {   // now recalculate the artefact array degree based on the individual array data
//            System.out.println("Counted vs Prepared Artefact number"+SEP+numberArtefacts+SEP+artefactArray.length);
            for (int art=0; art<numberArtefacts; art++) artefactArray[art].degree=0;            
            for (int ind=0; ind<numberIndividuals; ind++) artefactArray[individualArray[ind] ].degree++;            
            //System.out.println("Counted vs Calculated Artefact number"+SEP+numBentleyArt+SEP+numberArtefacts);
        }// eo if modeBentley
        
                if ((numberIndividuals<20)  || ( (infolevel & 1) == 1) )  printArtefacts(System.out, " ",true);      
        
        
        DistributionAnalysis ddAnalysis; 
            
        calcArtefactDegreeDistribution();
        if ((outputcontrol & 1) >0 ) 
        {
            FileOutputDegreeDistribution("#", false,artDDArr) ;
            ddAnalysis = new DistributionAnalysis();
            ddAnalysis.nameroot=nameroot;
            ddAnalysis.dirname=dirNameOutput;
            ddAnalysis.getDegreeDistribution(artDDArr);
            ddAnalysis.processOne("#", "lbdd.dat");

            FileOutputDegreeDistribution("#", false,artDDArr) ;
            ddAnalysis = new DistributionAnalysis();
            ddAnalysis.nameroot=nameroot;
            ddAnalysis.dirname=dirNameOutput;
            ddAnalysis.getDegreeDistribution(artDDArr);
            ddAnalysis.lbratio=1.999;
            ddAnalysis.processOne("#", "lbdd.dat");
            
        }
        
        
        calcArtefactWeightDistribution();
        if ((outputcontrol & 2) >0 ) 
        {
            FileOutputWeightDistribution("#", false,artWDArr) ;
            
            ddAnalysis = new DistributionAnalysis();
            ddAnalysis.nameroot=nameroot;
            ddAnalysis.dirname=dirNameOutput;
            ddAnalysis.getDegreeDistribution(artWDArr);
            ddAnalysis.processOne("#", "lbwd.dat");
            
            ddAnalysis = new DistributionAnalysis();
            ddAnalysis.nameroot=nameroot;
            ddAnalysis.dirname=dirNameOutput;
            ddAnalysis.getDegreeDistribution(artWDArr);
            ddAnalysis.lbratio=1.999;
            ddAnalysis.processOne("#", "lbwd.dat");
            
            ddAnalysis = new DistributionAnalysis();
            ddAnalysis.nameroot=nameroot;
            ddAnalysis.dirname=dirNameOutput;
            ddAnalysis.getDegreeDistribution(artWDArr);
            ddAnalysis.lbratio=2.718;
            ddAnalysis.processOne("#", "lbwd.dat");
            
            
        }
        
        printDegreeInfo(System.out, "");
        printActiveDegreeInfo(System.out, "");
        printWeightInfo(System.out, "");
        
        
    }

    
    /** initialiseModel
     * @param int initial individuals 
     * @param int initial artefacts 
     * @param int maximum number of individuals, = 0 if automatic choice
     * @param int maximum number of artefacts, = 0 if automatic choice
     * 
     */
        public int initialiseModel(int initialIndividuals, int initialArtefacts, int setMaxNumberIndividuals, int setMaxNumberArtefacts)
        {
           pbar=1.0-ppref-prand-prandact;
           if (pbar <-1e-6 ) return(-3);
            
           maxNumberIndividuals=setMaxNumberIndividuals;
           if (setMaxNumberIndividuals==0) maxNumberIndividuals = initialIndividuals + 1;
           if (initialIndividuals > maxNumberIndividuals ) return(-1);
           individualArray = new int[maxNumberIndividuals];
           individualArray2 = new int[maxNumberIndividuals];
           
//           int initialArtefacts = initialIndividuals;
           maxNumberArtefacts=setMaxNumberArtefacts;
           double averageArtAdd = numevents*pbar+1.0; //expected number of artefacts to be addded
           if (runMode==0) averageArtAdd  = averageArtAdd *2*initialIndividuals;
           if (setMaxNumberArtefacts==0) maxNumberArtefacts = initialArtefacts+ (int) (averageArtAdd + 4.0*Math.sqrt(averageArtAdd)+1.0);
           artefactArray= new Vertex[maxNumberArtefacts];
//           System.out.println("maxNumberArtefacts "+maxNumberArtefacts);
           
                     
           int artAdd=-1;
           numberArtefacts=0;
            switch (initialgraph) {
                case 0:
                default:
                {
                    for (numberIndividuals=0; numberIndividuals <initialIndividuals; numberIndividuals ++) {
                     artAdd =numberIndividuals % initialArtefacts;
                     individualArray[numberIndividuals] = artAdd;
                     if (addIndToArt(artAdd, numberIndividuals)<0) return(-2); // add ind as new neighbour to artefact
                     
                 }// eo for
                 break;
                }// eo default
            }
            return(0);
        }
    
    /** true Bentley rewire routine
     * This sets up a fresh set of individuals who copy from the last iteration's individuals
     * exactly as in the model of Bentley et al. It repeats this so we have two generations or
     * two Bentley time steps for each call, or (2*numberIndividuals) individual rewirings
     * per call of this routine.
     *@return dummy result
     */
        
        public int trueBentleyRewire()
        { 
            if (!modeBentley) return(-1);
            int artadd;
            for (int ind=0; ind<numberIndividuals; ind++)
            {
                if (Rnd.nextDouble()<pbar) artadd = numberArtefacts; // artefact to add
                else artadd = individualArray[Rnd.nextInt(numberIndividuals)]; // copy existing artefact                
                individualArray2[ind] = artadd; // note ind's new artefact
                addIndToArt(artadd, ind); // add ind as new neighbour to artefact
            }
            for (int ind=0; ind<numberIndividuals; ind++)
            {
                if (Rnd.nextDouble()<pbar) artadd = numberArtefacts; // artefact to add
                else artadd = individualArray2[Rnd.nextInt(numberIndividuals)]; // copy existing artefact                
                individualArray[ind] = artadd; // note ind's new artefact
                addIndToArt(artadd, ind); // add ind as new neighbour to artefact
            }
            return(0);
        }// eo trueBentleyRewire

        
    /** general rewire routine
     * This rewires the artefact end of the one edge from an individual.
     * The individual (or equivalently its edge) are choosen randomly, the reconnection or the
     * new artefact are choosen appropriately.
     * If modeBentley=true then the number of artefacts
     *@return the artefact added
     */
        
            public int rewire()
        {
            int indrw = Rnd.nextInt(numberIndividuals);  // individual to rewire
            int artrem = individualArray[indrw]; // artefact to remove
            int artadd=-1; // artefact to add
            if (Rnd.nextDouble()<pbar)
            { // add new artefact
                  artadd = numberArtefacts; // artefact to add
            }
            else
            { // copy existing artefact
                int indcopy = Rnd.nextInt(numberIndividuals); //copy this individual
                artadd = individualArray[indcopy]; // set to this artefact
                }
            individualArray[indrw] = artadd; // note indrw's new artefact
            // update artefact array here
            addIndToArt(artadd, indrw); // add indrw as new neighbour to artefact
            if (!modeBentley)   artefactArray[artrem].removeNeighbour(indrw); //remove indrw from artefact artrem
            return (artadd);
        }// eo rewire


// ----------------------------------------------------------------------
        /** add Individual as neighbour to an Artefact
         * artefactArray uses degree for current value 
         * except in true Bentley mode when it is just rubbish
         * but the weight should always be the cumulative degree
         * The list of neighbours should be null in any Bentley mode
         *@param int artefact 
         *@param int individual which neighbours the individual
         *@return 0 if OK -1 otherwise
     */
        
       
        public int addIndToArt(int artAdd, int individual)
        {
//            System.out.println(artAdd+SEP+individual+SEP+numberArtefacts+SEP+artefactArray.length );    
            if (artAdd >= artefactArray.length ) 
                    {
                        if(!addIndToArtError) System.out.println("Error *** In addIndToArt artefact array is too short "+artAdd+" " + individual);
                        addIndToArtError=true;
                                return(-1);
                    };
             
            while (artAdd>=numberArtefacts) 
                {
                    
                    
                    artefactArray[numberArtefacts++] = new Vertex (); // add new artefact
                }
//              System.out.println("art.add, ind "+SEP+artAdd+SEP+individual);
                if (artefactArray[artAdd] == null) 
                {
                  System.out.println("Error *** In addIndToArt artefact array not initialised "+artAdd+" " + individual+" "+numberArtefacts);
                  return(-3);
                }
                if (modeBentley) artefactArray[artAdd].degree++;
                else artefactArray[artAdd].addNeighbour(individual); // add new artefact to ind link
//                artefactArray[artAdd].weight++;
                return (0);
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
        int size = ial.size();
        // *** SURELY WRONG only initialise ial if zero size.
        if ((index==0) && (size==0)) {ial.add(value); return;};
        for (int i=size; i<=index; i++) ial.add(0);
        ial.set(index, ial.get(index)+value);
        return;
                  
    }
        
// **********************************************************************
// STATISTICAL CALCULATIONS

// -----------------------------------------------------------------------       
    /**
     * Calculates artefact degree distribution and degree stats
     */
    void calcArtefactWeightDistribution()  {
        
        //numberActiveArtefacts =0;
        
        weightArtCont =0;
        weightArt=new StatQuant(); 
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
        weightArtCont = ((int) weightArt.maximum) + 1; //
        int kminimum = ((int) weightArt.minimum);
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
            System.out.println("     TNV="+numberArtefacts+", calc = "+n);
        }
        
        
        return;
    }//eo calcWeightDistribution

// -----------------------------------------------------------------------       
    /**
     * Calculates artefact degree distribution and degree stats
     */
    void calcArtefactDegreeDistribution()  {
        
        numberActiveArtefacts =0;
        
        degreeArtCont =0;
        degreeArt=new StatQuant(); 
        artDDArr = new IntArrayList() ;
        //if (artDDTotArr==null) artDDTotArr = new IntArrayList() ;

        int k;
        for(int a=0; a<numberArtefacts; a++){
//          if (artefact[a]==null) {ddarr.set(0,ddarr.get(0)+1); break;}
          k = artefactArray[a].degree;
          degreeArt.add(k);
          addExtendIntArrayList(artDDArr, k, 1);
          //addExtendIntArrayList(artDDTotArr, k, 1);
          
          }
        
        int n=0;
//        int na=0;
        int ne=0;
        int nk = 0;
        degreeArtCont = ((int) (degreeArt.maximum+0.5))  + 1; //
        int kminimum = ((int) (degreeArt.minimum+0.5)) ;
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
            System.out.println("     TNV="+numberArtefacts+", calc = "+n);
        }
        
        if (ne != numberIndividuals) 
        {
            System.out.println("*** Error in calcArtefactDegreeDistribution edge totals mismatch");
            System.out.println("     TNE="+numberIndividuals+", calc = "+ne);
        }
        
        return;
    }//eo calcDegreeDistribution


// ***********************************************************************
// FILE OUTPUT ROUTINES

/**
     * Outputs information for a connected Undirected graph
     *  <filenameroot>.Jdd.dat Degree Distribution, or
     *  <filenameroot>.Jndd.dat Normalised Degree Distribution 
     * @param filenameroot basis of name of file as string
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     * @param artDDArr is CERN Colt Integer Array List of the degree distribution
     */


        void FileOutputDegreeDistribution(String cc, boolean normalise, IntArrayList artDDArr)  
        {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        if (normalise) extension = ".Jndd.dat";
        else extension=".Jdd.dat";
        filenamecomplete= dirNameOutput+nameroot+extension;
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            Date date = new Date();
            double p=0;
            int n=0;
            printParameters(PS, cc);
            printProbabilities(PS, cc);
            printDegreeInfo(PS, cc);
            printActiveDegreeInfo(PS, cc);
//            if (numberArtefacts<1) return;
            if (normalise) PS.println(cc+" k "+SEP+"p(k)    Normalised Artefact Degree Distribution");
            else PS.println(cc+" k "+SEP+"n(k)     Unnormalised Degree Distribution");
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
            };
            System.out.println("Finished writing degree distribution to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }
        
/**
     * Outputs information for a connected Undirected graph
     *  <filenameroot>.Jwd.dat Weight Distribution, or
     *  <filenameroot>.Jnwd.dat Normalised Weight Distribution 
     * @param filenameroot basis of name of file as string
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     * @param artWDArr is CERN Colt Integer Array List of the degree distribution     
     */

        void FileOutputWeightDistribution(String cc, boolean normalise, IntArrayList artWDArr)  
        {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        if (normalise) extension = ".Jnwd.dat";
        else extension=".Jwd.dat";
        filenamecomplete= dirNameOutput+nameroot+extension;
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            Date date = new Date();
            double p=0;
            int n=0;
            printParameters(PS, cc);
            printProbabilities(PS, cc);
            printWeightInfo(PS, cc);
            if (normalise) PS.println(cc+" w "+SEP+"p(w)    Normalised Artefact Weight Distribution");
            else PS.println(cc+" w "+SEP+"n(w)     Unnormalised Weight Distribution");
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
            };
            System.out.println("Finished writing weight distribution to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }
        
        
// ***********************************************************************        
// PRINT routines
        
  /**
     * Outputs information on average degree properties to a print stream 
     * @param PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     *@param boolean nonZeroOnly true if want non zero values only
     */
        
        public void printArtefacts(PrintStream PS, String cc, boolean nonZeroOnly)
        {
            PS.println(cc+ "number artefacts = " + SEP + numberArtefacts + SEP+", max = " + SEP + maxNumberArtefacts);
            PS.println(cc+ "Art" + SEP + "Degree" + SEP + "Weight" + SEP + "Individuals");
            for (int art=0; art<numberArtefacts; art ++)
                if (artefactArray[art].degree >0) PS.println(cc+ art + SEP + artefactArray[art].degree + SEP + artefactArray[art].weight + SEP + artefactArray[art].listNeighbours());
            
        }


        
        
/**
     * Outputs information on average degree properties to a print stream 
     * @param PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     *@param boolean nonZeroOnly true if want non zero values only
     */

        public void printIndividuals(PrintStream PS, String cc, boolean nonZeroOnly)
        {
            PS.println(cc+ "number individuals = " + SEP + numberIndividuals + SEP+", max = " + SEP + maxNumberIndividuals);
            PS.println(cc+ "Ind" + SEP + "Art");
            for (int ind=0; ind<numberIndividuals; ind ++)
            {
                if (individualArray[ind]>0) PS.println(cc+ ind + SEP + individualArray[ind]);
            }
        }



        
// -------------------------------------------------------------
  /**
     * Outputs information on average degree properties to a print stream 
     * @param Printstream such as System.out
   * * @param cc comment characters put at the start of every line
     */
    public void printDegreeInfo(PrintStream PS, String cc)  
    {
        PS.println(cc+SEP+"k_min"+SEP+"k_cont"+SEP+"k_max"+SEP+"<k>"+SEP+"<k^2>");
        PS.println(cc+SEP+ degreeArt.minimum+SEP+degreeArtCont+SEP+degreeArt.maximum
                     +SEP+TruncDec(degreeArt.average,2)+SEP+TruncDec(degreeArt.secondmoment,2) );
        return;
    }
// -------------------------------------------------------------
  /**
     * Outputs information on average weight properties to a print stream 
     * @param Printstream such as System.out
   * * @param cc comment characters put at the start of every line
     */
    public void printWeightInfo(PrintStream PS, String cc)  
    {
        PS.println(cc+SEP+"w_min"+SEP+"w_cont"+SEP+"w_max"+SEP+"<w>"+SEP+"<w^2>");
        PS.println(cc+SEP+ weightArt.minimum+SEP+weightArtCont+SEP+weightArt.maximum
                     +SEP+TruncDec(weightArt.average,2)+SEP+TruncDec(weightArt.secondmoment,2) );
        return;
    }

// -------------------------------------------------------------
  /**
     * Outputs information on average degree properties to a print stream 
     * @param Printstream such as System.out
   * * @param cc comment characters put at the start of every line
     */
    public void printActiveDegreeInfo(PrintStream PS, String cc)  
    {
        PS.println(cc +" No. active artefacts (vertices) : "+SEP+ numberActiveArtefacts);
        double ka = degreeArt.average*numberArtefacts/((double) numberActiveArtefacts);
        PS.println(cc +SEP+ "<k_active>=" +SEP+TruncDec(ka,2) );
        return;
    }
         
// -------------------------------------------------------------
  /**
     * Outputs information on probabilities to a print stream 
     * @param Printstream such as System.out
   * * @param cc comment characters put at the start of every line
     */
    public void printProbabilities(PrintStream PS, String cc)  
    {
        PS.println(cc+SEP+"p_p"+SEP+"p_r"+SEP+"p_ra"+SEP+"pbar");
        PS.println(cc+SEP+ ppref+SEP+prand+ SEP +prandact+ SEP+pbar );
        return;
    }
         
        
        

// --------------------------------------------------------------------------    
    /** Method in timgraph.  Parses command arguments      
     *  @param PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     */
    public void printOutputMethod(PrintStream PS, String cc)  
    {
        PS.println(cc+" Output control method is "+outputcontrol);
        PS.println(cc+"   Degree distribution calculation "+ (((outputcontrol & 1)>0)?"ON":"OFF") )  ;
        PS.println(cc+"   Weight distribution calculation "+ (((outputcontrol & 2)>0)?"ON":"OFF") )  ;
    }      

// --------------------------------------------------------------------------    
    /** Method in timgraph.  Parses command arguments      
     *  @param PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     */
    public String runModeString()  
    {
        switch (runMode)
        {
            case 0: return("True Bentley");
            case 1: return("Pseudo Bentley");
            case 2: return("General Copy Model");
            default: //return("Unknown runMode");
        }
        return("Unknown runMode");
     }      
   
    
 /**
     * Outputs information on general parameters 
     * @param PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     */

        public void printParameters(PrintStream PS, String cc)
        {
            PS.println(cc+" CopyModel.java version "+SEP+Version+SEP+" produced on "+SEP+date);
            PS.println(cc+" Running Mode                    : "+SEP+ runModeString() );
            PS.println(cc+" No. events                      : "+SEP+ numevents );
            PS.println(cc+" No. artefacts        (vertices) : "+SEP+ numberArtefacts);
            //PS.println(cc+" No. active artefacts (vertices) : "+SEP+ numberActiveArtefacts);
            PS.println(cc+" No. individuals        (edges)  : "+SEP+ numberIndividuals );
            PS.println(cc+" Weight updated every            : "+SEP+ weightCountUpdate );
            printProbabilities(PS,cc);
            printOutputMethod(PS,cc);
        }
             

        

// *********************************************************************
    /** Method in walk.  Parses command arguments      
     *  @return Undirected sparse graph
     */
    public int parseParam(String[] ArgList)  {

        
//        System.out.println(args.length+" command line arguments");
        for (int j =0; j<ArgList.length; j++){System.out.println("Argument "+j+" = "+ArgList[j]);}
        
                for (int i=0;i<ArgList.length ;i++){
                    if (ArgList[i].length() <3) {
                        System.out.println("\n*** Argument "+i+" is too short");
                        printUsage();
                        return 3;};
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** Argument "+i+" does not start with -, use -? for usage");
                            return 4;};
                            switch (ArgList[i].charAt(1)) {
                                case 'd': {dirNameOutput = ArgList[i].substring(2);
                                break;}
                                case 'e': { //Integer.parseInt(ArgList[i].substring(2));
                                    if (ArgList[i].charAt(2)=='t' ) numevents = Integer.parseInt(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='w' ) weightCountUpdate = Integer.parseInt(ArgList[i].substring(3));
                                break;}
                                case 'f': {nameroot = ArgList[i].substring(2);
                                break;}
                                case 'g': {initialgraph = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'i': {infolevel = Integer.parseInt(ArgList[i].substring(2));
                                break;}                                
                                case 'm': {runMode = Integer.parseInt(ArgList[i].substring(2));
                                break;}                                
                                case 'n': {
                                    if (ArgList[i].charAt(2)=='a' ) initialArtefacts=Integer.parseInt(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='i' ) initialIndividuals=Integer.parseInt(ArgList[i].substring(3));
                                break;}
                                case 'o': {outputcontrol = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'p': {
                                    if (ArgList[i].charAt(2)=='p' ) ppref= Double.parseDouble(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='r' ) prand= Double.parseDouble(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='a' ) prandact= Double.parseDouble(ArgList[i].substring(3));
                                break;}
//                                case 'q': {probrndvertex= Double.parseDouble(ArgList[i].substring(2));
//                                break;}
//                                case 't': {initialGraph=Integer.parseInt(ArgList[i].substring(2));
//                                break;}
//                                case 'v': {
//                                    if (ArgList[i].charAt(2)=='w' ) walkmode=Integer.parseInt(ArgList[i].substring(3));
//                                    if (ArgList[i].charAt(2)=='e' ) edgegenerator=Integer.parseInt(ArgList[i].substring(3));
//                                break;}
//                                case 'z': {//FullInfoOn=Integer.parseInt(ArgList[i].substring(2));
//                                break;}
                                case '?': {printUsage();
                                return 1;}
                                default:{
                                    System.out.println("\n*** Argument "+i+" not known, usage:");
                                    printUsage();
                                    return 2;
                                }
                                
                            }
                }
        
        File dir = new File(dirNameOutput);
            if (!dir.isDirectory()) 
            {
                System.out.println("*** Error "+dirNameOutput+" is not a directory");
                return 1;
            };
            
            if ((runMode==0) || (runMode==1)) modeBentley=true; else modeBentley=false;
            if (runMode==0) 
            { // True Bentley has one event per 2*initialIndividuals rewiring events
             numevents=numevents / (initialIndividuals *2);
             if (weightCountUpdate<1) weightCountUpdate=1;//(initialIndividuals *2)
             else weightCountUpdate = weightCountUpdate / (initialIndividuals *2);
            }
            else if (weightCountUpdate<1) weightCountUpdate=initialIndividuals ;
            //System.out.println("runMode,weightCountUpdate,initialIndividuals = "+runMode+", "+weightCountUpdate+", "+initialIndividuals);
    return 0;    
    }//eo ParamParse
    
    
    
    
// ........................................................................
     /** Method in rewire.  
     * Gives usage of rewire     
     *  
     */

    public void printUsage()  
    {

        CopyModel temp = new CopyModel();
        System.out.println("type rewire followed by the following options:");
        System.out.println(" -d<dirNameOutput>  output directory name, default "+temp.dirNameOutput);
        System.out.println(" -et<int>      number of time steps (rewire events), default "+temp.numevents);
        System.out.println(" -ew<int>      number of time steps per weight counting, default "+temp.weightCountUpdate);
        System.out.println(" -f<nameroot> root used for file names, default "+temp.nameroot);
        System.out.println(" -g<int>      initial graph number, default "+temp.initialgraph);
        System.out.println(" -i<int>      debugging information level, default "+temp.infolevel);
        System.out.println(" -m<int>      model mode, 0 Bentley, 1 PseudoBentley, 2 General, default "+temp.runMode);
        System.out.println(" -na<int>     initial no. artefacts, default "+temp.numberArtefacts);
        System.out.println(" -ni<int>     initial no. individuals, default "+temp.numberIndividuals);
        System.out.println(" -pa<int>     probability of choosing random Active artefact, default "+temp.prandact);
        System.out.println(" -pp<int>     probability of copying artefact, default "+temp.prandact);
        System.out.println(" -pr<int>     probability of choosing random artefact, default "+temp.prandact);
        
//                System.out.println("       -e<#events> -m<connectivity> -l<steps> -p<probnewvertex>");
//                System.out.println("       -g<initialgraph#> -v<mode> -o<outputcontrol> -i<infolevel>\n");
//                System.out.println(" -p<float> probability of adding a new vertex for attachment per event \n");
////                System.out.println(" -q<float> probability of choosing a random old vertex for attachment, else use walk \n");
//                System.out.println(" -vw<int> walk modes \n");
//                System.out.println("vw modes: (vw& 1) ? Starts walks from random vertex : (end of random edge)");
//                System.out.println("        : (vw& 2) ? Starts new walk for every edge : (only for every event)");
//                System.out.println("        : (vw& 4) ? Markovian walk, yes : (or no)");
//                System.out.println("        : (vw& 8) ? Random number edges per vertex, yes : (or no)");
                System.out.println(" -o<int> output modes , default "+temp.outputcontrol);
                System.out.println("  o modes: (o& 1) ? Degree distribution calc and output on : (off)");
                System.out.println("         : (o& 2) ? Weight distribution calc and output on : (off)");
//                System.out.println("         : (o& 4) ? Clustering calc and output on : (off)");
//                System.out.println("         : (o& 8) ? Pajek file output on : (off)");
//                System.out.println("         : (o&16) ? Component calc and output on : (off)");
                
                
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
    
// --------------------------------------------------------------------
    // Timing routines
    
    /**
     * SetInitialTime
     */
    public void setInitTime()
    {
      initialtime = System.currentTimeMillis ();
      return;
      //return ( initialtime);
    }

    /**
     * currentRunTime
     */
    public double currentRunTime()
    {
      return ( (double) (System.currentTimeMillis() -initialtime)/1000.0 );
    }

    /**
     * currentRunTime
     */
    public String runTimeString()
    {
         double dtime = ((double) (System.currentTimeMillis ()-initialtime) ) /1000.0;
         int time = (int) ( dtime +0.5);
         int secs = time % 60;
         time = time/60;
         int minutes =time%60;
         time = time/60;
         int hours = time %24;
         int days = time /24;
         String s="";
         if (hours>0) s=s+hours+"h";
         if (minutes>0) s=s+minutes+"m ";
         s=s+secs+"s";
         return ( s );
    }

// **********************************************************************
/*
 * Artefact class
 *
 *  defines attributes of a vertex
 */

/**
 *
 * @author time
 */

    
    public class Vertex
    {
     int label;
     double weight;
     int degree;
     IntArrayList sourceList;
        
        public Vertex()
        {
            label=0;
          weight=0;
          degree=0;
          sourceList = new IntArrayList();
        }

// *** use empty initialisation plus add routine
// initialise with first neighbour
//        public Vertex(int firstNeighbour)
//        {
//            label=0;
//          weight=0;
//          degree=1;
//          sourceList = new IntArrayList();
//          sourceList.add(firstNeighbour);
//        }
        
        public Vertex(double setweight )
        {
          weight=setweight;
        }
        
        public Vertex(Vertex oldVertex )
        {
          weight=oldVertex.weight;
        }
        
        public int addNeighbour(int neighbour)
        {
            sourceList.add(neighbour);
            degree=sourceList.size();
            return(degree);
        }

        public int removeNeighbour(int neighbour)
        {
            sourceList.delete(neighbour); //deletes first entry equal to neighbour
            degree=sourceList.size();
            return(degree);
        }
        
        public String listNeighbours()
        {
            String s="";
            for (int n=0; n<sourceList.size(); n++) s=s+sourceList.get(n)+SEP;
            return(s);
        }
    } // eo Vertex class
  
    
// **********************************************************************
/*
 * StatQuant class
 *
 *  defines a statistical quantity, keeping running totals of 
 *  sums and sums of squares with moments, sigma and errors too. 
 */

/**
 *
 * @author time
 */
    
    
    public class StatQuant    
    {
     double maximum;
     double minimum;
     double total;
     double squaretotal;
     int count;
     double average;
     double sigma;
     double error;
     double secondmoment;
        
        public StatQuant()
        {
          total=0;
          squaretotal=0;
          count=0;
          average=0;
          secondmoment=0;
          sigma=0;
          error=0;
          maximum=0;
          minimum=0;
        }
        
        public void add(double x)
        {
            total+= x;
            squaretotal+=x*x;
            count++;
            average=total/count;
            secondmoment=squaretotal/count;
            if (count>1) 
            { 
             sigma=(secondmoment-average*average)/Math.sqrt(count-1);
             error=sigma/Math.sqrt(count);   
             if (maximum < x) maximum = x;
             if (minimum > x) minimum = x;
            }
            else 
            {
                maximum=x;
                minimum=x;
                sigma=0;
                error=0;
            }
        }
        
        
    }   
  
    
} // eo CopyModel class
