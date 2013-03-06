/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

//import TimGraph.Community.Partition;
//import TimGraph.Community.QualitySparse;
import TimGraph.Community.VertexPartition;
import TimGraph.timgraph;
import TimGraph.OutputMode;
import TimGraph.algorithms.BipartiteTransformations;
import TimGraph.algorithms.Projections;
import TimGraph.io.FileOutput;
import TimGraph.io.FileStemmer;
import TimUtilities.StringUtilities.Filters.ImperialPapersFilter;
import java.util.TreeMap;

/**
 * Basic analysis of a graph.
 * @author time
 */
public class ProcessImperialPapers {
    
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static int infoLevel=0;
    
    
    public ProcessImperialPapers(){}
    
    
    /**
     * Basic routine to BasicAnalysis.analyse a given graph.
     * <p>If first argument starts with a '*','^' or ':'
     * (specified by <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>
     * {@link TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT})
     * the rest of the argument is the network number for the setup routine
     * If arguments exits but the first has no '^' then network is set up from file read in using the arguments
     * to point it to the right file.
     * <p>Second '^' argument controls output and is the number defined by the OutputControl class,
     * <p>Third '^' argument must be followed by lower case y if you want basic analysis performed.
     * <p>Fourth '^' argument used to set minumum edge weights and edge percolation mode, if non-zero.
     * <p>Fifth '^' argument used to set lambda, negative means ignore this.
     * @param args the command line arguments
     * @see TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT
     */
    public static void main(String[] args) 
    { 
      System.out.println("ImperialPapers Arguments ^<networknumber> ^<outputMode> ^<basicAnalysis> ^<minWeight> ^<lambda>");
      
      //First arg chooses network
      int n=110; 
      int ano=0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) {
          if (args[ano].charAt(1)=='?') {SetUpNetwork.printNetworkTypes(System.out, "", "  "); n=0;} 
          else n=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      }
      else n=0;
      System.out.println("--- Using network  "+SetUpNetwork.typeString(n));
      
      //Second arg chooses output mode
      int outputMode=0; //30; //60; //1 30;
      ano++;
      if (args.length>ano ) 
          if (timgraph.isOtherArgument(args[ano])) outputMode=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      OutputMode om = new OutputMode(outputMode);
      System.out.println("--- Output Mode "+outputMode+": "+om.getModeString(" "));
      
      //Third arg chooses basic analysis on or off
      boolean basicAnalysis=false;
      ano++;
      if (args.length>ano ) 
          if ((timgraph.isOtherArgument(args[ano])) && (args[ano].charAt(1)=='y') ) basicAnalysis=true;
      System.out.println("--- Basic analysis is "+(basicAnalysis?"on":"off"));

      //Fourth arg sets min weight cut value
      double minWeightCut=0.0;
      ano++;
      if (args.length>ano ) 
          if (timgraph.isOtherArgument(args[ano])) minWeightCut = Double.parseDouble(args[ano].substring(1, args[ano].length()));
      if (minWeightCut>0) System.out.println("--- Minimum Weight is  "+minWeightCut);
      else System.out.println("--- No Minimum Weight cut ");

      //Fith arg sets lambda value 
      double lambda=0.0;
      ano++;
      if (args.length>ano ) 
          if (timgraph.isOtherArgument(args[ano])) lambda = Double.parseDouble(args[ano].substring(1, args[ano].length()));
      if (lambda>0) System.out.println("--- lambda is  "+lambda);
      else System.out.println("--- No lambda, no vertex partitions ");

      
      
      if (minWeightCut>0) {
          minEdgeWeight(n, minWeightCut, outputMode, basicAnalysis, args);
          Runtime.getRuntime().exit(0);
      }

      
      
      if ((n==111) || (n==114)) {
          SetUpNetwork.setUpNetwork(n,args); 
          n--;
      }
      if ((n>=110) && (n<120)) processOriginalFile(n, outputMode, basicAnalysis, lambda, args);
      

      if ((n>=120) && (n<130))  processTermTerm(n, outputMode, basicAnalysis, lambda, args);
      
      if ((n>=130) && (n<140))  processSections(n, outputMode, basicAnalysis, args);
      
      if ((n>=140) && (n<150))  processPaperPaper(n, outputMode, basicAnalysis, lambda, args);
      
    }
    
    /**
     * Process the paper-section files.
     * @param n
     * @param outputMode
     * @param basicAnalysis
     * @param args
     */
    static public void processSections(int n, int outputMode, boolean basicAnalysis, String [] args){
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      System.out.println("\n *** Processing paper-unit graphs");
      
      // read paper unit graph
      timgraph tgpu = SetUpNetwork.setUpNetwork(n, args);
      if (tgpu.getNumberVertices()<20) tgpu.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(tgpu);
      tgpu.calcStrength();
      
      basicroot=tgpu.inputName.getNameRoot();
      int us = basicroot.lastIndexOf('_');
      String unitName = basicroot.substring(us+2,basicroot.length());
      String stub =  basicroot.substring(0, us);
      BipartiteTransformations bt = new BipartiteTransformations();
      
      int method=0; // 0=Louvain/Greedy, 1= Simulated Annealing
      int lgmethod=2; //0=C, 1=Ctilde, 2=D, 3=E (Dtilde)
      int qdef=0; //0=Q(A), 1=Q(A*A-A)
      int qualityType=2; // 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
      double lambda=1.0; //value of lambda, scaling parameter of null model.
      
      // the unit-unit projection
      System.out.println("\n *** "+unitName+"-"+unitName+" projection");
      timgraph unitg = BipartiteTransformations.project(tgpu,false,
             tgpu.isVertexLabelled(), true, tgpu.isVertexEdgeListOn(), true); 
      unitg.setNameRoot(stub+"_"+unitName);
      if (unitg.getNumberVertices()<20) unitg.printNetwork(true);
      if (basicAnalysis) {
          BasicAnalysis.analyse(unitg);
          FileOutput fo = new FileOutput(unitg);
          fo.pajek(false,false);
      }
      unitg.calcStrength();
      LineGraphCommunities.analyseGraph(unitg, method, lgmethod, qdef, qualityType, lambda);
    
    }
    
        /**
     * Looks at the paper-paper graph formed by projecting paper-term bipartite graph.
     * @param n network number for <tt>SetUpNetwork</tt>
     * @param outputMode
     * @param basicAnalysis
     * @param args
     */
    static public void processPaperPaper(int n, int outputMode, boolean basicAnalysis, double lambda, String [] args)
    {
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      System.out.println("\n *** analysing paper-paper projection vertex partition");
      timgraph termg = SetUpNetwork.setUpNetwork(n, args);
      if (termg.getNumberVertices()<20) termg.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(termg);
      termg.calcStrength();
      
      if (lambda>0){
        System.out.println("\n *** Analysing vertex partitions of graph "+termg.inputName.getNameRoot());  
        int method=0; // 0=Louvain/Greedy, 1= Simulated Annealing
        int lgmethod=2; //0=C, 1=Ctilde, 2=D, 3=E (Dtilde)
        int qdef=0; //0=Q(A), 1=Q(A*A-A)
        int qualityType=2; // Minimal Memory
        //double lambda=1.0; //value of lambda, scaling parameter of null model.
        VertexPartition louvainvp = VertexPartition.calculate(termg, qdef, qualityType, lambda, method, infoLevel);
        LineGraphCommunities.vertexPartitionGraphML(termg, louvainvp);
      }
      
      


    }
    /**
     * Looks at the term-term graph formed by projecting paper-term bipartite graph.
     * @param n network number for <tt>SetUpNetwork</tt>
     * @param outputMode
     * @param basicAnalysis
     * @param args
     */
    static public void processTermTerm(int n, int outputMode, boolean basicAnalysis, double lambda, String [] args)
    {
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      System.out.println("\n *** analysing term-term projection vertex partition");
      timgraph termg = SetUpNetwork.setUpNetwork(n, args);
      if (termg.getNumberVertices()<20) termg.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(termg);
      termg.calcStrength();
      if (lambda>0){
      System.out.println("\n *** Analysing vertex partitions of graph "+termg.inputName.getNameRoot());  
      int method=0; // 0=Louvain/Greedy, 1= Simulated Annealing
      int lgmethod=2; //0=C, 1=Ctilde, 2=D, 3=E (Dtilde)
      int qdef=0; //0=Q(A), 1=Q(A*A-A)
      int qualityType=2; // Minimal Memory
      //double lambda=1.0; //value of lambda, scaling parameter of null model.    
      VertexPartition louvainvp = VertexPartition.calculate(termg, qdef, qualityType, lambda, method, infoLevel);
      LineGraphCommunities.vertexPartitionGraphML(termg, louvainvp);
//    LineGraphCommunities.analyseGraph(termg, method, lgmethod, qdef, qualityType, lambda);
      }
    }    
    /**
     * Performs edge weight percolation.
     * @param n network number for <tt>SetUpNetwork</tt>
     * @param minWeight minimum weight for all edges
     * @param outputMode 
     * @param basicAnalysis
     * @param args
     */
    static public void minEdgeWeight(int n, double minWeight, int outputMode, boolean basicAnalysis, String [] args)
    {
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      System.out.println("\n *** "+SetUpNetwork.typeString(n)+" projection cut on minimum edge weight of "+minWeight);
      timgraph termg = SetUpNetwork.setUpNetwork(n, args);
      if (termg.getNumberVertices()<20) termg.printNetwork(true);
      boolean makeLabelled=termg.isVertexLabelled();
      boolean makeWeighted=termg.isWeighted();
      boolean makeVertexEdgeList=false;
      timgraph consg = Projections.minimumEdgeWeight(termg, minWeight, makeLabelled, makeWeighted, makeVertexEdgeList);
      consg.setNameRoot(termg.inputName.getNameRoot()+"ew"+Math.round(minWeight*1000));
      
      if (consg.getNumberVertices()<20) consg.printNetwork(true);
      consg.calcStrength();
      if (basicAnalysis) BasicAnalysis.analyse(consg);
    }    
    
       /**
     * Does preprocessing of raw Imperial Papers data.
     * <p>Applys stemming and filtering.
     * @param tg input graph
     * @param basicFileName basic file name, other info is added to this.
     * @param numberLinesToSkip Only takes certain fraction of lines
     * @param showProcess true if want information displayed on screen.
     */
    public static void preProcess(timgraph tg,String basicFileName, int numberLinesToSkip, boolean showProcess){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
      String preStemmedFileName=tg.inputName.getDirectoryFull()+basicFileName+"ptinputBVNLS.dat";
      String outputroot = basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem";
      String postStemmedFileName=tg.inputName.getDirectoryFull()+outputroot+"ptinputBVNLS.dat";
      
      TreeMap<String,String> stemMap = new TreeMap();
      int columnIgnored =1;
      String sep="\t";
      ImperialPapersFilter ipf = new ImperialPapersFilter(2,3,true);
      FileStemmer.processStringFile(preStemmedFileName, postStemmedFileName, columnIgnored, "\t ",stemMap, ipf, numberLinesToSkip, false);
      System.out.println("Stemming file "+preStemmedFileName+", taking every "+numberLinesToSkip+" lines only");
      System.out.println("Applied "+ipf.description());
      System.out.println("Producing file "+postStemmedFileName);

      String outputFileName = tg.outputName.getDirectoryFull()+outputroot+"ptStemMap.dat";
      TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputFileName, sep, stemMap, true);
      outputFileName = tg.outputName.getDirectoryFull()+outputroot+"ptRejectList.dat";
      ipf.FileOutputRejectedList(outputFileName, showProcess);
      }

    
    /**
     * process original paper-term files.
     * <p>Produces term-term and paper-paper projections.
     * If lambda positive then also makes vertex partitions.
     * <br>No pre-processing is done in this method.
     * @param n
     * @param outputMode
     * @param basicAnalysis
     * @param lambda if negative then no vertex partitions performed
     * @param args
     */
    static public void processOriginalFile(int n, int outputMode, boolean basicAnalysis, double lambda, String [] args)
    {
      
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      
      timgraph tg = SetUpNetwork.setUpNetwork(n, args);
      
      basicroot = tg.inputName.getNameRoot();
      if (!tg.inputName.getNameRoot().startsWith("IC")) throw new RuntimeException("*** File read in does not start with IC, it was "+ basicroot);
      
      if (outputMode>=0)  tg.outputControl.set(outputMode);
      
      if (tg.getNumberVertices()<40) tg.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(tg);
      
      String stub = basicroot.substring(0, basicroot.length()-2);
      
      BipartiteTransformations bt = new BipartiteTransformations();
      
      // the term-term projection
      System.out.println("\n *** term-term projection");
      timgraph termg = BipartiteTransformations.project(tg,false,
             tg.isVertexLabelled(), true, tg.isVertexEdgeListOn(), true); 
      termg.setNameRoot(stub+"tt");
      if (termg.getNumberVertices()<20) termg.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(termg);
      
      // the paper-paper projection
      System.out.println("\n *** paper-paper projection");
      timgraph paperg = BipartiteTransformations.project(tg,true,
        tg.isVertexLabelled(), true, tg.isVertexEdgeListOn(), true); 
      paperg.setNameRoot(stub+"pp");
      if (paperg.getNumberVertices()<20) paperg.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(paperg);

      
      if (lambda<=0) return;
      System.out.println("\n *** Vertex Communities");
      int method=0; // 0=Louvain/Greedy, 1= Simulated Annealing
      int lgmethod=2; //0=C, 1=Ctilde, 2=D, 3=E (Dtilde)
      int qdef=0; //0=Q(A), 1=Q(A*A-A)
      int qualityType=2; // 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
      //double lambda=1.0; //value of lambda, scaling parameter of null model.
      

      termg.calcStrength();
      LineGraphCommunities.analyseGraph(termg, method, lgmethod, qdef, qualityType, lambda);
      paperg.calcStrength();
      LineGraphCommunities.analyseGraph(paperg, method, lgmethod, qdef, qualityType, lambda);

      
    }

//    /**
//     * Basis analysis of a graph
//     * @param tg graph to analyse
//     */
//     static public void analyse(timgraph tg){
//      System.out.println("\n *** Basic analysis of network "+tg.inputName.getNameRoot());
//      basicroot = tg.inputName.getNameRoot();
//      FileOutput fo = new FileOutput(tg);
//      fo.informationGeneral("", SEP, null);
//      fo.edgeListSimple();
//      if (tg.isVertexLabelled()) fo.printEdges(true, false, false, false, false);
//      System.out.println("Finished basic analysis of network "+tg.inputName.getNameRoot());
//      
//    }
    
}
