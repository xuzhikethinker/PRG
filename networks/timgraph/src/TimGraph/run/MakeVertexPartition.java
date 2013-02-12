/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.Community.Community;
import TimGraph.Community.Quality;
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
import java.util.ArrayList;

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
     * <ol>
     * <li>':' argument  is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing
     * </li>
     * <li>':' argument is type of quality to use, 0=Q(A), 1=Q(A*A-A)
     * </li>
     * <li>':' argument is null model to use, 
     *      <ul>
     *      <li>0 = k_out k_out/W^2</li>
     *      <li>1 = k_in k_out/W^2</li>
     *      <li>1 = k_in k_out/W^2</li>
     *      <li>2 = k_out k_in/W^2</li>
     *      <li>3 = k_in k_in/W^2</li>
     *      <li>4 = pi pi</li>
     *      <li>5 = 1/(W^2)</li>
     *      </ul>
     * </li>
     * <li>':' argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory ");
     * </li>
     * <li>':' argument is minimum value of gamma, scaling parameter of null model.
     * </li>
     * <li>':' argument is maximum value of gamma, scaling parameter of null model.
     * </li>
     * <li>':' argument is size of increase in gamma, scaling parameter of null model.
     * </li>
     * <li>':' argument is the number of runs to make for each value of gamma
     * </li>
     * <li>':' argument is 't' or 'y' if graphml output is required.
     * </li>
     * <li>':' argument is 't' or 'y' if single file of output is required.
     * </li>
     * <li>Remaining arguments should start with a '-' and are parsed by <tt>timgraph</tt>.
     * </li>
     * </ol>
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    { 
      System.out.println("MakeVertexCommunities Arguments :<method>"
              + " :<qualitydef>  :<nullModel> "
              + ":<qualitytype> :<gammaMin> :<gammaMax> :<gammaStep> "
              + ":<numberRuns> :<graphMLOn>");
      
      int ano=0;
      //0="Louvain Vertex Partition", 1="Simulated Annealing Partition"
      int method =0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) method=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Using community method "+VertexPartition.METHODS[method]);

      // 0="QS", 1="QA2mA"
      int qdef=0; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qdef=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality definition used "+QualitySparse.QdefinitionString[qdef]);

//      * Null Model switch.
//     * <ul>
//     * <li>0 = k_out k_out/W^2</li>
//     * <li>1 = k_in k_out/W^2</li>
//     * <li>2 = k_out k_in/W^2</li>
//     * <li>3 = k_in k_in/W^2</li>
//     * <li>4 = pi pi</li>
//     * <li>5 = 1/(W^2)</li>
//     * </ul>     
      int nullModel=0; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) nullModel=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Null model used "+Quality.nullModelString[nullModel]);

      // qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
      int qualityType=2;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qualityType=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality class type "+QualityType.qualityLongName[qualityType]);
     

      double gammaMin = 1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaMin=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      
      double gammaMax = gammaMin+0.6;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaMax=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      
      double minStep=1e-3;
      double gammaStep = 0.5; //gammaMax-gammaMin+0.5;
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

      boolean produceSingleFile=true;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) produceSingleFile=StringFilter.trueString(args[ano].charAt(1));
      System.out.println("--- vp data is output as "+(produceSingleFile?"single file":"multiple files"));

      //SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      //tg = SetUpNetwork.setUpNetwork(n, args);
      
      //if no argument list use the following set up (i.e. for testing)   
//     String basicFileName="karateTSE"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gelf", "-gbf", "-fin"+basicFileName, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
       String basicFileName="karateTSE"; //BowTieW";
       String inputSubDirectory = "input/karate";
       String outputSubDirectory = "output/karate";
       String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbf",
       "-fin"+basicFileName+"inputELS.dat",  "-fis"+inputSubDirectory, 
       "-fos"+outputSubDirectory,
       "-gn99",  "-e0", "-o23", "-xi0"};
       
//      String basicFileName="UKwardsSEJD"; //BowTieW";
//       String inputSubDirectory = "input/UKwards";
//       String outputSubDirectory = "output/UKwards";
//       String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-gbf",
//       "-fin"+basicFileName+"inputELS.dat",  "-fis"+inputSubDirectory,
//       "-fos"+outputSubDirectory,
//       "-gn99",  "-e0", "-o23", "-xi0"};
     if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
     
     if (!tg.isVertexEdgeListOn()) System.err.println("\n *** Vertex Edge List NOT on and it is required, use -gvet option ***\n");
        
     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFiles(); // this will read in the network
     //tg.setNetworkFromManyInputFiles(int xColumn, int yColumn, int nameColumn, boolean headerOn, boolean infoOn)
     tg.printParametersBasic();
     
     tg.calcStrength();
     
     if (produceSingleFile) {produceSingleFile(method, qdef, nullModel, qualityType,
            gammaMin, gammaMax, gammaStep,
            numberRuns,
            fileOutput, graphMLOutput, projectedGraphOutput);
        } else {
          produceIndividualFiles(method, qdef, nullModel, qualityType,
            gammaMin, gammaMax, gammaStep,
            numberRuns,
            fileOutput, graphMLOutput, projectedGraphOutput);

     }
    }


    static public void produceSingleFile(int method, int qdef, int nullModel, 
            int qualityType,
            double gammaMin,double gammaMax,double gammaStep,
            int numberRuns,
            boolean fileOutput, boolean graphMLOutput, boolean projectedGraphOutput){

      String extension = "_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"_gamma.dat";
      ArrayList<VertexPartition> vpList = new ArrayList();
      ArrayList<Double> gList = new ArrayList();
      ArrayList<Integer> rList = new ArrayList();
      VertexPartition vp;

      double gamma=timgraph.DUNSET;
      int r=timgraph.IUNSET;
      FileOutput fo = new FileOutput(tg);
      PrintStream PS;
      FileOutputStream fout;
      fo.fileName.setNameEnd(extension);

      try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            Community c = new Community(tg, qdef, nullModel, gammaMin, qualityType,infoLevel);
            PS.println("name"+SEP+c.informationNumbersLabel("", SEP));

            for (gamma=gammaMin; gamma<=gammaMax; gamma+=gammaStep){
               if (gamma<=0) break;
               System.out.println("\n--- gamma="+String.format("%6.3f", gamma)+" ----------------------------------------------------");
               for (r=0; r<numberRuns; r++){
                   vp= calculateVertexPartition(tg, qdef, nullModel, qualityType,
                           gamma, method, r, infoLevel,
                           fileOutput, graphMLOutput, projectedGraphOutput);
                   vp.relabelCommunities(true);
                   vpList.add(vp);
                   gList.add(gamma);
                   rList.add(r);
                   PS.println(r+SEP+vp.getName()+SEP+vp.informationNumbers("", SEP));
               } // eo r
            }// eo gamma
          if (infoLevel>-2) System.out.println("Finished writing VP various gamma information file to "+ fo.fileName.getFullFileName());
          try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}


        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fo.fileName.getFullFileName()+", "+e.getMessage());
            return;
        } catch (RuntimeException e) {
            System.err.println("*** Error calculating gamma="+String.format("%6.3f", gamma)+", run "+r+", "+e.getMessage());
            return;
        }
      FileOutput fovertex = new FileOutput(tg);
      PrintStream PSvertex;
      FileOutputStream foutvertex;
      String extensionvertex = "_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"_outputVertices.dat";
      fovertex.fileName.setNameEnd(extensionvertex);
      System.out.println("\n--- Single Vertex Information File "+fovertex.fileName.getFullFileName()+" ---");
      try {
            foutvertex = new FileOutputStream(fovertex.fileName.getFullFileName());
            PSvertex = new PrintStream(foutvertex);
            //Community c = new Community(tg, qdef, gammaMin, qualityType,infoLevel);
            String cc=timgraph.COMMENTCHARACTER;
            String sep="\t";
            // make label for partitions first
            String vpLabel="";
            for (int n=0; n<vpList.size(); n++){
               gamma=gList.get(n);
               r=rList.get(n);
               vpLabel=(n==0?"":vpLabel+sep)+"g"+String.format("%06.3f", gamma)+"r"+r;
            }// eo n


            boolean printTriangles=false;
            boolean printSquares=false;
            boolean printName=true;
            boolean printNumber=false;
            boolean printPosition=true;
            boolean printType=tg.isBipartite();
            boolean printStrength=true;
            boolean printMaxWeight=false;
            boolean printClusterCoef=false;
            boolean printRank=false;
            boolean printStructuralHoleData=false;
            boolean printNearestNeighbours=false;
            tg.printVertices(PSvertex, cc, sep,
             vpLabel, vpList,
             printTriangles, printSquares,
             printName, printNumber, printPosition,
             printType,
             printStrength, printMaxWeight,
             printClusterCoef,
             printRank,
             printStructuralHoleData,
             printNearestNeighbours);


            if (infoLevel>-2) System.out.println("Finished writing vertex information file to "+ fovertex.fileName.getFullFileName());
            try{ foutvertex.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fovertex.fileName.getFullFileName()+", "+e.getMessage());
            return;
        }


    }


    /**
     * Produces files for each run and each value of gamma
     * Q methods are 0="Louvain Vertex Partition", 1="Simulated Annealing Partition"
     * Q definitions are 0="QS", 1="QA2mA".
     * Quality types are qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory.
     * @param method defined by VertexPartition.METHODS, try 0
     * @param qdef defined by QualitySparse.QdefinitionString, try 0
     * @param nullModel index of null model to use
     * @param qualityType defined by QualityType.qualityLongName, try 1
     * @param gammaMin min gamma
     * @param gammaMax max gamma
     * @param gammaStep step in gamma
     * @param numberRuns number of runs
     * @param fileOutput true if want network file outputs
     * @param graphMLOutput true if want graphml of vertex partitions
     * @param projectedGraphOutput true if want graph of communities
     * @see timgraph.Communities.VertexPartition#METHODS
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    static public void produceIndividualFiles(int method, int qdef, int nullModel, 
            int qualityType,
            double gammaMin,double gammaMax,double gammaStep,
            int numberRuns,
            boolean fileOutput, boolean graphMLOutput, boolean projectedGraphOutput){

      FileOutput fo = new FileOutput(tg);
      PrintStream PS;
      FileOutputStream fout;
      String extension = "_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"_gamma.dat";
      fo.fileName.setNameEnd(extension);

      try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            Community c = new Community(tg, qdef, nullModel, gammaMin, qualityType,infoLevel);
            PS.println("name"+SEP+c.informationNumbersLabel("", SEP));
            for (double gamma=gammaMin; gamma<=gammaMax; gamma+=gammaStep){
               if (gamma<=0) break;
               System.out.println("\n--- gamma="+String.format("%6.3f", gamma)+" ----------------------------------------------------");
               
               for (int r=0; r<numberRuns; r++){
                   VertexPartition vp = calculateVertexPartition(tg, qdef, nullModel, 
                           qualityType,
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
     * @param nullModel index of null model to use
     * @param qualityType defines the type of quality function
     * @param gamma rescales the null model term
     * @param method method to use to optimise quality
     * @param runNumber number of runs
     * @param infoLevel information level
     * @param fileOutput true (false) if (don't) want file output
     * @param graphMLOutput true (false) if (don't) want graphml output
     * @param projectedGraphOutput true (false) if (don't) want output for the graph of the projection onto the community
     * @return vertex partition
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    static public VertexPartition calculateVertexPartition(timgraph tg, 
            int qdef, int nullModel, int qualityType, double gamma, int method,
            int runNumber, int infoLevel,
            boolean fileOutput, boolean graphMLOutput, boolean projectedGraphOutput ){
      VertexPartition vp = VertexPartition.calculate(tg, qdef, nullModel, qualityType, gamma, method, infoLevel);
      if (runNumber>=0) vp.setName(vp.getName()+"r"+runNumber);
      tg.setVertexNumbers(vp);
      if (!tg.hasVertexPositions()) tg.setVertexPositionsFromPartition(vp, 30.0,90.0);
      if (fileOutput){
          FileOutput fo = new FileOutput(tg);
          fo.informationPartition(vp, "", SEP, true);
          fo.printPartitionStats(vp, "", SEP, true, true);
          fo.printVertices("", SEP, false, false, vp, true);
          fo.printVertices("", SEP, false, true, vp, false);
          //fovertex.informationGeneral("", SEP);
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
