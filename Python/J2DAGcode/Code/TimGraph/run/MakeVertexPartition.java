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
 * Makes a vertex partition of a network.
 * @author time
 */
public class MakeVertexPartition {
    
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static timgraph tg;
    static int infoLevel=-1;




    /**
     * Basic routine to study vertex communities.
     * <p>The initial arguments start which with a ':' 
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>).
     * <p>First ':' argument  is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing
     * <p>Second ':' argument is type of quality to use, 0=Q(A), 1=Q(A*A-A)
     * <p>Third ':' argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory ");
     * <p>Fourth ':' argument is minimum value of gamma, scaling parameter of null model.
     * <p>Fifth ':' argument is maximum value of gamma, scaling parameter of null model.
     * <p>Sixth ':' argument is size of increase in gamma, scaling parameter of null model.
     * <p>Seventh ':' argument is the number of runs to make for each value of gamma
     * <p>Eighth ':' argument is 't' of 'y' if graphml output is required.
     * <p>Remaining arguments should start with a '-' and are parsed by <tt>timgraph</tt>.
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    { 
      System.out.println("MakeVertexCommunities Arguments :<method> :<qualitydef> :<qualitytype> :<gammaMin> :<gammaMax> :<gammaStep> :numberRuns :<graphMLOn>");
      
      int ano=0;
      int method =0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) method=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Using community method "+VertexPartition.METHODS[method]);


      // 0="QS", 1="QA2mA"
      int qdef=0; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qdef=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality definition used "+QualitySparse.QdefinitionString[qdef]);

      // qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
      int qualityType=1;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qualityType=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality class type "+QualityType.qualityLongName[qualityType]);
     

      double gammaMin = 1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaMin=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      
      double gammaMax = gammaMin+1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaMax=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      

      double minStep=1e-3;
      double gammaStep = gammaMax-gammaMin+1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaStep=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      if (Math.abs(gammaStep)<minStep) {
          throw new RuntimeException("Step size must be greater than "+minStep+" in magnitude");
      }
      System.out.println("--- Modularity null model scaling gamma min, max step ="+gammaMin+"  "+gammaMax+"  "+gammaStep);
      
      int numberRuns=1;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) numberRuns=Math.max(1,Integer.parseInt(args[ano].substring(1, args[ano].length())));
      System.out.println("--- Number of runs for each gamma value ="+numberRuns);


      boolean fileOutput=true;
      boolean graphMLOutput=true;
      boolean projectedGraphOutput=false;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) graphMLOutput=StringFilter.trueString(args[ano].charAt(1));
      System.out.println("--- file output is "+StringFilter.onOffString(fileOutput));
      System.out.println("--- graphML output is "+StringFilter.onOffString(graphMLOutput));
      System.out.println("--- projected graph output is "+StringFilter.onOffString(projectedGraphOutput));

      
        
      
      //SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      //tg = SetUpNetwork.setUpNetwork(n, args);
      
     // if no argument list use the following set up (i.e. for testing)   
//     String basicFileName="karateTSE"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gelf", "-gbf", "-fin"+basicFileName, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
       String basicFileName="karateTSE"; //BowTieW";
       String subDirectory = "input/karate";
       String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbf", 
       "-fin"+basicFileName+"inputELS.dat",  "-fis"+subDirectory,
       "-gn99",  "-e0", "-o23", "-xi0"};
     if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
     
     if (!tg.isVertexEdgeListOn()) System.err.println("\n *** Vertex Edge List NOT on and it is required, use -vgvet option ***\n");
        
     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFile(); // this will read in the network
     //tg.setNetworkFromInputFile(int xColumn, int yColumn, int nameColumn, boolean headerOn, boolean infoOn)
     tg.printParametersBasic();
     
     tg.calcStrength();

      FileOutput fo = new FileOutput(tg);
      PrintStream PS;
      FileOutputStream fout;
      String extension = "_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"_gamma.dat";
      fo.fileName.setNameEnd(extension);

      try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            Community c = new Community(tg, qdef, gammaMin, qualityType,infoLevel);
            PS.println("name"+SEP+c.informationNumbersLabel("", SEP));
            for (double gamma=gammaMin; gamma<=gammaMax; gamma+=gammaStep){
               if (gamma<=0) break;
               System.out.println("\n--- gamma="+String.format("%6.3f", gamma)+" ----------------------------------------------------");
               
               for (int r=0; r<numberRuns; r++){
                   VertexPartition vp = calculateVertexPartition(tg, qdef, qualityType,
                           gamma, method, r, infoLevel,
                           fileOutput, graphMLOutput, projectedGraphOutput);
                   PS.println(r+SEP+vp.getName()+SEP+vp.informationNumbers("", SEP));
               }

              
            } 
            if (infoLevel>-2) System.out.println("Finished writing VP various gamma information file to "+ fo.fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fo.fileName.getFullFileName()+", "+e.getMessage());
            return;
        }

      
    }

    /**
     * Calculates a vertex partition of a graph.
     * @param tg graph whose vertex partition is required
     * @param qdef index defining the definition of quality function to use
     * @param qualityType defines the type of quality function
     * @param gamma rescales the null model term
     * @param method method to use to optimise quality
     * @param runNumber number of runs
     * @param infoLevel information level
     * @param fileOutput true (false) if (don't) want file output
     * @param graphMLOutput true (false) if (don't) want graphml output
     * @param projectedGraphOutput true (false) if (don't) want output for the graph of the projection onto the community
     * @return vertex partition
     */
    static public VertexPartition calculateVertexPartition(timgraph tg, 
            int qdef, int qualityType, double gamma, int method,
            int runNumber, int infoLevel,
            boolean fileOutput, boolean graphMLOutput, boolean projectedGraphOutput ){
      VertexPartition vp = VertexPartition.calculate(tg, qdef, qualityType, gamma, method, infoLevel);
      if (runNumber>=0) vp.setName(vp.getName()+"r"+runNumber);
      tg.setVertexNumbers(vp);
      if (!tg.hasVertexPositions()) tg.setVertexPositionsFromPartition(vp, 30.0,90.0);
      if (fileOutput){
          FileOutput fo = new FileOutput(tg);
          fo.informationPartition(vp, "", SEP, true);
          fo.printPartitionStats(vp, "", SEP, true, true);
          fo.printVertices("", SEP, false, false, vp, true);
          fo.printVertices("", SEP, false, true, vp, false);
          //fo.informationGeneral("", SEP);
          if (tg.outputControl.pajekFileOn) fo.pajekPartition(vp.getName()+".clu", vp);
          if (graphMLOutput) fo.graphMLVertexPartition(vp);
      }
      if (projectedGraphOutput){
          FileOutput fo = new FileOutput(tg);
          timgraph projg = vp.getVertexPartitionGraph();
          fo = new FileOutput(projg);
          fo.informationGeneral("", SEP);
      }
      return vp;
    }

    
    
}
