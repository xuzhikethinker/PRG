/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import DataAnalysis.EntropyStatistics;
import DataAnalysis.MutualInformation;
import DataAnalysis.MutualInformationStatistics;
import TimGraph.Community.Community;
import TimGraph.Community.QualitySparse;
import TimGraph.Community.QualityType;
import TimGraph.Community.VertexPartition;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Compares vertex partitions of one graph while varying gamma.
 * <p>Uses mutual information measures. Runs Louvain
 * algorithm several times for same gamma and compares these
 * pairwise.
 * @author time
 */
public class CompareVertexPartitionsGamma {
    
    final static String SEP = "\t";
    static String basicroot="UNSET";
    int infoLevel=0;
    
      
    EntropyStatistics sStats;
    EntropyStatistics sStatsTwo;
    MutualInformationStatistics miStats;


    public CompareVertexPartitionsGamma(){
    }


    /**
     * Basic routine to study vertex communities.
     * <p>The initial arguments start which with a ':' 
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>).
     * <p>First ':' argument is the number of the network as specified by <tt>setUpNetwork</tt>.  
     * 0 means use the file specified in the timgraph arguments.     
     * ? means print out some help.
     * <p>Second ':' argument is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing
     * <p>Third ':' argument is type of quality to use, 0=Q(A), 1=Q(A*A-A)
     * <p>Fourth ':' argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory ");
     * <p>Fifth ':' argument is value of gamma, scaling parameter of null model.
     * <p>Remaining arguments should start with a '-' and are parsed by <tt>timgraph</tt>.
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
      boolean infoOn=true;
      System.out.println("CompareVertexPartitions Arguments :<method> :<qualitydef> :<qualitytype> :<gammaMin> :<gammaMax> :<gammaStep> :<graphMLOn> :<numberRuns>");
      
      int ano=0;
      int method =0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) method=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Using community method "+LineGraphCommunities.COMMUNITYMETHOD[method]);


      // 0="QS", 1="QA2mA"
      int qdef=0; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qdef=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality definition used "+QualitySparse.QdefinitionString[qdef]);

      // qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
      int qualityType=2;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qualityType=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality class type "+QualityType.qualityLongName[qualityType]);
     

      double gammaMin = 1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaMin=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      
      double gammaMax = gammaMin+0.25;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaMax=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      

      double minStep=1e-3;
      double gammaStep = 0.1; //(gammaMax-gammaMin)/4.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaStep=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      if (Math.abs(gammaStep)<minStep) {
          throw new RuntimeException("Step size must be greater than "+minStep+" in magnitude");
      }
      System.out.println("--- Modularity null model scaling gamma min, max step ="+gammaMin+"  "+gammaMax+"  "+gammaStep);
      
      int numberRuns=3;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) numberRuns=Math.max(1,Integer.parseInt(args[ano].substring(1, args[ano].length())));
      System.out.println("--- Number of runs for each gamma value ="+numberRuns);


      boolean fileOutput=false;
      boolean graphMLOutput=false;
      boolean projectedGraphOutput=false;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) graphMLOutput=StringFilter.trueString(args[ano].charAt(1));
      System.out.println("--- file output is "+StringFilter.onOffString(fileOutput));
      System.out.println("--- graphML output is "+StringFilter.onOffString(graphMLOutput));
      System.out.println("--- projected graph output is "+StringFilter.onOffString(projectedGraphOutput));

      
        
      
      //SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      //tg = SetUpNetwork.setUpNetwork(n, args);
      
     // if no argument list use the following set up (i.e. for testing)   
     String basicFileName="karateTSE"; //BowTieW";
     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gelf", "-gbf", "-fin"+basicFileName, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     String basicFileName="sltest"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileName, "-fieinputEL.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     String basicFileName="BowTieWWLG"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-gbt", "-fin"+basicFileName, "-fieinputEL.dat", "-gn99",  "-e0", "-o23", "-xi0"};
     if (args.length>0) aList=args;

     timgraph tg = new timgraph();
     tg.parseParam(aList);
     
     if (!tg.isVertexEdgeListOn()) System.err.println("\n *** Vertex Edge List NOT on and it is required, use -gvet option ***\n");
        
     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFiles(); // this will read in the network
     tg.printParametersBasic();
     
     tg.calcStrength();

     CompareVertexPartitionsGamma cvp = new CompareVertexPartitionsGamma();
     cvp.compareUsingMethodFluctuations(tg, qdef, qualityType, gammaMin, gammaMax, gammaStep, method, numberRuns, fileOutput, graphMLOutput, projectedGraphOutput);

     cvp.compareUsingLouvainDeltaGamma(tg, qdef, qualityType, gammaMin, gammaMax, gammaStep, method, numberRuns, fileOutput, graphMLOutput, projectedGraphOutput);
     cvp.compareUsingRandomisedGraph(tg, qdef, qualityType, gammaMin, gammaMax, gammaStep, method, numberRuns, fileOutput, graphMLOutput, projectedGraphOutput);


    }
    
    /**
     * Compare partitions of graph againsts a randomised graph
     * @param tg graph being analysed
     * @param qdef definition of quality to use
     * @param qualityType type of quality class to use
     * @param gammaMin gamma min
     * @param gammaMax gamma max
     * @param gammaStep gamma step
     * @param method graph partition method to use
     * @param numberRuns number of runs to be used in comparison for each gamma value
     * @param fileOutput output files for each partition
     * @param graphMLOutput output graphml file for each partition
     * @param projectedGraphOutput output files for projected community graph
     */
    public void compareUsingRandomisedGraph(timgraph tg,
            int qdef, int qualityType,
            double gammaMin, double gammaMax, double gammaStep,
            int method, int numberRuns,
            boolean fileOutput, boolean graphMLOutput, boolean projectedGraphOutput){

      System.out.println("Comparing Vertex Partitions against randomised graphs");
      FileOutput fo = new FileOutput(tg);
      PrintStream PS;
      FileOutputStream fout;
      String extension = "_r"+numberRuns+"_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"Rand_gamma.dat";
      fo.fileName.setNameEnd(extension);

      VertexPartition [] currentVP = new VertexPartition [numberRuns];
      VertexPartition [] randomVP = new VertexPartition [numberRuns];
      sStats = new EntropyStatistics("G(g)_");
      sStatsTwo = new EntropyStatistics("Rand_");
      miStats= new MutualInformationStatistics();

      timgraph rtg =tg ;
      int nullModel = (tg.isDirected()?4:0); 
      //int vpInfoLevel=-2;
      try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            //CompareVertexPartitionsGamma cvp = new CompareVertexPartitionsGamma();
            //Community c = new Community(tg, qdef, gammaMin, qualityType,vpInfoLevel);
            PS.println("name"+SEP+"gamma"+SEP+sStats.toLabel(SEP)+SEP+sStatsTwo.toLabel(SEP)+SEP+miStats.toLabel(SEP));
            for (double gamma=gammaMin; gamma<=gammaMax; gamma+=gammaStep){
               if (gamma<=0) break;
               System.out.println("\n--- gamma="+String.format("%6.3f", gamma)+" ----------------------------------------------------");
               for (int r=0; r<numberRuns; r++){
                   VertexPartition vp = MakeVertexPartition.calculateVertexPartition(tg, qdef, nullModel, qualityType, gamma, method, r, infoLevel,
                           fileOutput, graphMLOutput, projectedGraphOutput);
                   currentVP[r]=vp;

                   rtg = new timgraph(tg, tg.getMaximumVertices(), tg.getMaximumStubs(),
                                     false, false, false);
                   rtg.randomiseGraph();
                   rtg.addToNameRoot("Rand");
                   VertexPartition rvp = MakeVertexPartition.calculateVertexPartition(rtg, qdef, nullModel, qualityType, gamma, method, r, infoLevel,
                           fileOutput, graphMLOutput, projectedGraphOutput);
                   randomVP[r]=vp;
                   //System.out.println("run "+r+" # partitions "+vp.getNumberOfCommunities());
               }
               doComparison(currentVP,randomVP);
               System.out.println(sStats.averagesLabel(SEP)+SEP+sStatsTwo.averagesLabel(SEP)+SEP+miStats.averagesLabel(SEP));
               System.out.println(sStats.averagesString(SEP)+SEP+sStatsTwo.averagesString(SEP)+SEP+miStats.averagesString(SEP));
               PS.println(currentVP[0].getName()+SEP+gamma+SEP+sStats.toString(SEP)+SEP+sStatsTwo.toString(SEP)+SEP+miStats.toString(SEP));

            }
            if (infoLevel>-1) System.out.println("Finished writing VP various gamma information file to "+ fo.fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fo.fileName.getFullFileName()+", "+e.getMessage());
            return;
        }

      //rtg.outputControl.set(255);
      //BasicAnalysis.analyse(rtg);
}
    /**
     * Compare partitions of graph for different gamma values.
     * @param tg graph being analysed
     * @param qdef definition of quality to use
     * @param qualityType type of quality class to use
     * @param gammaMin gamma min
     * @param gammaMax gamma max
     * @param gammaStep gamma step
     * @param method graph partition method to use
     * @param numberRuns number of runs to be used in comparison for each gamma value
     * @param fileOutput output files for each partition
     * @param graphMLOutput output graphml file for each partition
     * @param projectedGraphOutput output files for projected community graph
     */
    public void compareUsingLouvainDeltaGamma(timgraph tg,
            int qdef, int qualityType,
            double gammaMin, double gammaMax, double gammaStep,
            int method, int numberRuns,
            boolean fileOutput, boolean graphMLOutput, boolean projectedGraphOutput){

      System.out.println("Comparing Vertex Partitions for gamma against gamma-delta gamma");
      FileOutput fo = new FileOutput(tg);
      PrintStream PS;
      FileOutputStream fout;
      String extension = "_r"+numberRuns+"_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"dgamma_gamma.dat";
      fo.fileName.setNameEnd(extension);

      VertexPartition [] currentVP = new VertexPartition [numberRuns];
      VertexPartition [] previousVP = new VertexPartition [numberRuns];
      sStats = new EntropyStatistics("G(g)_");
      sStatsTwo = new EntropyStatistics("G(g-dg)_");
      miStats= new MutualInformationStatistics();

      int nullModel=(tg.isDirected()?4:0);
      //int vpInfoLevel=-2;
      try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            //CompareVertexPartitionsGamma cvp = new CompareVertexPartitionsGamma();
            //Community c = new Community(tg, qdef, gammaMin, qualityType,vpInfoLevel);
            PS.println("name1"+SEP+"name2"+SEP+"gamma"+SEP+sStats.toLabel(SEP)+SEP+sStatsTwo.toLabel(SEP)+SEP+miStats.toLabel(SEP));
            for (double gamma=gammaMin; gamma<=gammaMax; gamma+=gammaStep){
               if (gamma<=0) break;
               System.out.println("\n--- gamma="+String.format("%6.3f", gamma)+" ----------------------------------------------------");
               for (int r=0; r<numberRuns; r++){
                   VertexPartition vp = MakeVertexPartition.calculateVertexPartition(tg, qdef, nullModel, qualityType, gamma, method, r, infoLevel,
                           fileOutput, graphMLOutput, projectedGraphOutput);
                   previousVP[r]=currentVP[r];
                   currentVP[r]=vp;

                   //System.out.println("run "+r+" # partitions "+vp.getNumberOfCommunities());
               }
               if (gamma>gammaMin) {
                 doComparison(currentVP,previousVP);
                 System.out.println(sStats.averagesLabel(SEP)+SEP+sStatsTwo.averagesLabel(SEP)+SEP+miStats.averagesLabel(SEP));
                 System.out.println(sStats.averagesString(SEP)+SEP+sStatsTwo.averagesString(SEP)+SEP+miStats.averagesString(SEP));
                 PS.println(currentVP[0].getName()+SEP+previousVP[0].getName()+SEP+gamma+SEP+sStats.toString(SEP)+SEP+sStatsTwo.toString(SEP)+SEP+miStats.toString(SEP));
               }

            }
            if (infoLevel>-1) System.out.println("Finished writing VP various gamma information file to "+ fo.fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fo.fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
}

    /**
     * Compare partitions using fluctuations in Louvain partitions
     * @param tg graph being analysed
     * @param qdef definition of quality to use 
     * @param qualityType type of quality class to use
     * @param gammaMin gamma min
     * @param gammaMax gamma max
     * @param gammaStep gamma step
     * @param method graph partition method to use
     * @param numberRuns number of runs to be used in comparison for each gamma value
     * @param fileOutput output files for each partition
     * @param graphMLOutput output graphml file for each partition
     * @param projectedGraphOutput output files for projected community graph
     */
    public void compareUsingMethodFluctuations(timgraph tg,
            int qdef, int qualityType,
            double gammaMin, double gammaMax, double gammaStep,
            int method, int numberRuns,
            boolean fileOutput, boolean graphMLOutput, boolean projectedGraphOutput){
      System.out.println("Comparing Vertex Partitions for gamma against each other");
      int nullModel=(tg.isDirected()?4:0); 
      FileOutput fo = new FileOutput(tg);
      PrintStream PS;
      FileOutputStream fout;
      String extension = "_r"+numberRuns+"_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"_gamma.dat";
      fo.fileName.setNameEnd(extension);

      VertexPartition [] currentVP = new VertexPartition [numberRuns];
      VertexPartition [] previousVP = new VertexPartition [numberRuns];
      sStats = new EntropyStatistics("G(g)_");
      //sStatsTwo = new EntropyStatistics();
      miStats= new MutualInformationStatistics();
//int vpInfoLevel=-2;
      try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            //CompareVertexPartitionsGamma cvp = new CompareVertexPartitionsGamma();
            //Community c = new Community(tg, qdef, gammaMin, qualityType,vpInfoLevel);
            PS.println("name"+SEP+"gamma"+SEP+sStats.toLabel(SEP)+SEP+miStats.toLabel(SEP));
            for (double gamma=gammaMin; gamma<=gammaMax; gamma+=gammaStep){
               if (gamma<=0) break;
               System.out.println("\n--- gamma="+String.format("%6.3f", gamma)+" ----------------------------------------------------");
               for (int r=0; r<numberRuns; r++){
                   VertexPartition vp = MakeVertexPartition.calculateVertexPartition(tg, qdef, nullModel, qualityType, gamma, method, r, infoLevel,
                           fileOutput, graphMLOutput, projectedGraphOutput);
                   currentVP[r]=vp;
                   //System.out.println("run "+r+" # partitions "+vp.getNumberOfCommunities());
               }
               doComparison(currentVP);
               System.out.println(sStats.averagesLabel(SEP)+SEP+miStats.averagesLabel(SEP));
               System.out.println(sStats.averagesString(SEP)+SEP+miStats.averagesString(SEP));
               PS.println(currentVP[0].getName()+SEP+gamma+SEP+sStats.toString(SEP)+SEP+miStats.toString(SEP));

            }
            if (infoLevel>-1) System.out.println("Finished writing VP various gamma information file to "+ fo.fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fo.fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
}
    

    public void doComparison(VertexPartition [] vpArray){
        int n=vpArray.length;
        miStats.reset();
        sStats.reset();
        for (int p1=0; p1<vpArray.length; p1++){
           for (int p2=p1+1; p2<vpArray.length; p2++){
               //MutualInformation mi = doComparision(vpArray[p1] ,vpArray[p2]);
               MutualInformation mi = Community.calcMutualInformation(vpArray[p1] ,vpArray[p2], null);
               miStats.add(mi);
               if (p2==p1+1) {
                   sStats.addTwo(mi);
                   //System.out.println("run "+p2+" # partitions "+mi.getOrderTwo());
                   if (p1==0) {
                       //System.out.println("run "+p1+" # partitions "+mi.getOrderOne());
                       sStats.addOne(mi);
                   }
               }
           }
        }

    }

    public void doComparison(VertexPartition [] vpArray, VertexPartition [] vpArray2){
        //int n=vpArray.length;
        miStats.reset();
        sStats.reset();
        sStatsTwo.reset();
        for (int p1=0; p1<vpArray.length; p1++){
           for (int p2=0; p2<vpArray2.length; p2++){
               //MutualInformation mi = doComparision(vpArray[p1] ,vpArray[p2]);
               MutualInformation mi = Community.calcMutualInformation(vpArray[p1] ,vpArray2[p2], null);
               miStats.add(mi);
               if (p2==p1) {
                   sStatsTwo.addTwo(mi);
                   sStats.addOne(mi);
               }
           }
        }
    }

    
    
}
