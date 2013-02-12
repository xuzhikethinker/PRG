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
import TimUtilities.StringUtilities.Filters.TextFileProcessor;
import java.util.TreeMap;

/**
 * Basic analysis of a graph.
 * @author time
 */
public class ProcessAcademicPapers {
    
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static int infoLevel=0;
    
    static timgraph tg;

    public ProcessAcademicPapers(){}
    
    
    /**
     * Basic routine to analyse academic paper data.
     * <p>If first argument starts with a '*','^' or ':'
     * (specified by <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>
     * {@link TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT})
     * the rest of the argument is the network number for the setup routine
     * If arguments exits but the first has no ':' then network is set up from file read in using the arguments
     * to point it to the right file.
     * <p>Second ':' argument controls action performed on file
     * <p>Third ':' argument controls output and is the number defined by the OutputControl class,
     * <p>Fourth ':' argument must be followed by lower case y if you want basic analysis performed.
     * <p>Fifth ':' argument used to set minimum edge weights and edge percolation mode, if non-zero.
     * <p>Sixth ':' argument used to set lambda, negative means ignore this.
     * @param args the command line arguments
     * @see TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT
     */
    public static void main(String[] args)
    {
      System.out.println("ImperialPapers Arguments :<basicfilename>  :<actionMode> :<outputMode> :<basicAnalysis> :<minWeight> :<lambda>");
      System.out.println("  Action modes are:");
      System.out.println("   i: preprocess file.");
      System.out.println("   o: process Original file .");
      System.out.println("   t: process Term-Term");
      System.out.println("   s: process Sections");
      System.out.println("   p: process Paper-Paper");

//      //First arg chooses network
//      int n=110;
//      int ano=0;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) {
//          if (args[ano].charAt(1)=='?') {SetUpNetwork.printNetworkTypes(System.out, "", "  "); n=0;}
//          else n=Integer.parseInt(args[ano].substring(1, args[ano].length()));
//      }
//      else n=0;
//      System.out.println("--- Using network  "+SetUpNetwork.typeString(n));
      int ano=0;
      String inputDir="input/";
      String outputDir="output/";
      String basicFileName="EBRP5";
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) basicFileName=args[ano].substring(1, args[ano].length());
      System.out.println("--- data from file "+basicFileName);

      //Second arg chooses output mode
      String actionMode="o";
      ano++;
      if (args.length>ano )
          if (timgraph.isOtherArgument(args[ano])) actionMode=args[ano].substring(1, args[ano].length());
      System.out.println("--- Action Mode "+actionMode);

      //3rd arg chooses output mode
      int outputMode=511; //30; //60; //1 30;
      ano++;
      if (args.length>ano )
          if (timgraph.isOtherArgument(args[ano])) outputMode=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      OutputMode om = new OutputMode(outputMode);
      System.out.println("--- Output Mode "+outputMode+": "+om.getModeString(" "));

      //4th arg chooses basic analysis on or off
      boolean basicAnalysis=true;
      ano++;
      if (args.length>ano )
          if ((timgraph.isOtherArgument(args[ano])) && (args[ano].charAt(1)=='y') ) basicAnalysis=true;
      System.out.println("--- Basic analysis is "+(basicAnalysis?"on":"off"));

      //5th arg sets min weight cut value
      double minWeightCut=0.0;
      ano++;
      if (args.length>ano )
          if (timgraph.isOtherArgument(args[ano])) minWeightCut = Double.parseDouble(args[ano].substring(1, args[ano].length()));
      if (minWeightCut>0) System.out.println("--- Minimum Weight is  "+minWeightCut);
      else System.out.println("--- No Minimum Weight cut ");

      //6th arg sets lambda value
      double lambda=1.0;
      ano++;
      if (args.length>ano )
          if (timgraph.isOtherArgument(args[ano])) lambda = Double.parseDouble(args[ano].substring(1, args[ano].length()));
      if (lambda>0) System.out.println("--- lambda is  "+lambda);
      else System.out.println("--- No lambda, no vertex partitions ");



      if (minWeightCut>0) {
          minEdgeWeight(minWeightCut, outputMode, basicAnalysis);
          Runtime.getRuntime().exit(0);
      }

     int numberLinesToSkip=1;
     boolean showProcess=true;
     if (actionMode.charAt(0) =='i'){
//                System.err.println("*** Note that the pre processing is better done in ");
//                System.err.println("*** TimUtilities.StringUtilities.Filters.TextFileProcessor");
//                System.err.println("*** as this also removes punctuation.");
                preProcess(basicFileName, inputDir, outputDir, numberLinesToSkip, showProcess);
                actionMode = actionMode.substring(1, actionMode.length());
                basicFileName=basicFileName+"stem";
//                System.err.println("*** Note that the pre processing is better done in ");
//                System.err.println("*** TimUtilities.StringUtilities.Filters.TextFileProcessor");
//                System.err.println("*** as this also removes punctuation.");
                return;
        }

     String fullFileName="";
      switch (actionMode.charAt(0)){
           case 'o':
               fullFileName=basicFileName+"ptinputBVNLS.dat";
               break;
          case 't':
               fullFileName=basicFileName+"ttinputBVNLS.dat";
               break;
          case 's':
               fullFileName=basicFileName+"psecinputBVNLS.dat";
              break;
          case 'p':
               fullFileName=basicFileName+"ptinputBVNLS.dat";
              break;
          default: throw new RuntimeException("Unknown action");

      }

      System.out.println("--- full file name "+fullFileName);


     //String [] aList = { "-fin"+basicFileName,"-fisinput/vanesh","-fosoutput/vanesh","-o511", "-xv10", "-xe20"};
     String [] aList = { "-fin"+fullFileName,"-fis"+inputDir,"-fos"+outputDir,"-o511", "-xv10", "-xe20"};


     if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
     tg.outputControl.printMode(System.out, " ");

     tg.setNameRoot(tg.inputName.getNameRoot());


     int xColumn=1;
     int yColumn=2;
     boolean headerOn=false;
     boolean infoOn=false;
     int nameColumn =1;
     tg.setNetworkFromManyInputFiles(xColumn, yColumn, nameColumn, headerOn, infoOn); // this will read in the network
     tg.printParametersBasic();
     System.out.println("Output: "+tg.outputControl.getModeString(" "));

     switch (actionMode.charAt(0)){
          case 'o':
               processOriginalFile(outputMode, basicAnalysis, lambda);
               break;
          case 't':
               processTermTerm(outputMode, basicAnalysis, lambda);
               break;
          case 's':
              processSections(outputMode, basicAnalysis);
              break;
          case 'p':
              processPaperPaper(outputMode, basicAnalysis, lambda);
              break;
          default: throw new RuntimeException("Unknown action");

      }

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
      // read paper unit graph
      tg = SetUpNetwork.setUpNetwork(n, args);
    }
    /**
     * Process the paper-section files.
     * @param outputMode
     * @param basicAnalysis
     */
    static public void processSections(int outputMode, boolean basicAnalysis){
      System.out.println("\n *** Processing paper-unit graphs");

      // read paper unit graph
      timgraph tgpu = tg;
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
      timgraph termg = SetUpNetwork.setUpNetwork(n, args);
      processPaperPaper(outputMode, basicAnalysis,  lambda);
    }
        /**
     * Looks at the paper-paper graph formed by projecting paper-term bipartite graph.
     * @param outputMode
     * @param basicAnalysis
         * @param lambda
     */
    static public void processPaperPaper(int outputMode, boolean basicAnalysis, double lambda)
    {
      System.out.println("\n *** analysing paper-paper projection vertex partition");
      timgraph termg = tg;
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
     * @param outputMode
     * @param basicAnalysis
     * @param lambda
     */
    static public void processTermTerm(int outputMode, boolean basicAnalysis, double lambda)
    {
      System.out.println("\n *** analysing term-term projection vertex partition");
      timgraph termg = tg;
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
     * Looks at the term-term graph formed by projecting paper-term bipartite graph.
     * @param n network number for <tt>SetUpNetwork</tt>
     * @param outputMode
     * @param basicAnalysis
     * @param args
     */
    static public void processTermTerm(int n, int outputMode, boolean basicAnalysis, double lambda, String [] args)
    {
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      tg = SetUpNetwork.setUpNetwork(n, args);
      processTermTerm( outputMode, basicAnalysis,  lambda);
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
     * Performs edge weight percolation.
     * @param minWeight minimum weight for all edges
     * @param outputMode
     * @param basicAnalysis
     */
    static public void minEdgeWeight(double minWeight, int outputMode, boolean basicAnalysis)
    {
      System.out.println("\n *** "+tg.inputName.getFullFileName()+" projection cut on minimum edge weight of "+minWeight);
      timgraph termg = tg;
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
     * <p>Applies stemming and filtering.
        * Note that the pre processing is better done in
        * <code>TimUtilities.StringUtilities.Filters.TextFileProcessor</code>
        * as this also removes punctuation.
     * @param tg input graph
     * @param basicFileName basic file name, other info is added to this.
     * @param numberLinesToSkip Only takes certain fraction of lines
     * @param showProcess true if want information displayed on screen.
        * @see TimUtilities.StringUtilities.Filters.TextFileProcessor
     */
    public static void preProcess(String basicFileName, String inputDir, String outputDir,
            int numberLinesToSkip, boolean showProcess){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
//      String preStemmedFileName=tg.inputName.getDirectoryFull()+basicFileName+"ptinputBVNLS.dat";
//      String outputroot = basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem";
//      String postStemmedFileName=tg.inputName.getDirectoryFull()+outputroot+"ptinputBVNLS.dat";
      String preStemmedFileName=inputDir+basicFileName+"ptinputBVNLS.dat";
      String outputroot = basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem";
      String postStemmedFileName=outputDir+outputroot+"ptinputBVNLS.dat";

      TreeMap<String,String> stemMap = new TreeMap();
      int columnIgnored =1;
      String sep="\t";
      ImperialPapersFilter ipf = new ImperialPapersFilter(2,3,true);

      String inputSep="\t ,";
      boolean stemmerOn=true;
      TreeMap<String,Integer> acceptedCountMap = new TreeMap();
      TextFileProcessor.processIndexSentenceFile(preStemmedFileName, postStemmedFileName,
            inputSep,  sep, stemmerOn, stemMap, acceptedCountMap,
            ipf, numberLinesToSkip,  showProcess);

      //FileStemmer.processStringFile(preStemmedFileName, postStemmedFileName, columnIgnored, "\t ",stemMap, ipf, numberLinesToSkip, false);
      System.out.println("Stemming file "+preStemmedFileName+", taking every "+numberLinesToSkip+" lines only");
      System.out.println("Applied "+ipf.description());
      System.out.println("Producing file "+postStemmedFileName);

      String outputMapFileName = outputDir+outputroot+"ptStemMap.dat";
      TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputMapFileName, sep, stemMap, true);
      outputMapFileName = outputDir+outputroot+"ptAcceptedCountMap.dat";
      TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputMapFileName, sep, acceptedCountMap, true);
      String outputFileName = outputDir+outputroot+"ptRejectList.dat";
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
    static public void processOriginalFile(int n, int outputMode,
            boolean basicAnalysis, double lambda, String [] args)
    {
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      tg = SetUpNetwork.setUpNetwork(n, args);
      processOriginalFile(outputMode, basicAnalysis, lambda);
    }
    /**
     * process original paper-term files.
     * <p>Produces term-term and paper-paper projections.
     * If lambda positive then also makes vertex partitions.
     * <br>No pre-processing is done in this method.
     * @param outputMode
     * @param basicAnalysis
     * @param lambda if negative then no vertex partitions performed
     */
    static public void processOriginalFile(int outputMode, boolean basicAnalysis,
            double lambda)
    {

      basicroot = tg.inputName.getNameRoot();
      //if (!tg.inputName.getNameRoot().startsWith("IC")) throw new RuntimeException("*** File read in does not start with IC, it was "+ basicroot);
      
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
    public static void oldMain(String[] args)
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

//      if ((n==111) || (n==114)) {
//          SetUpNetwork.setUpNetwork(n,args);
//          n--;
//      }
      if ((n>=110) && (n<120)) processOriginalFile(n, outputMode, basicAnalysis, lambda, args);


      if ((n>=120) && (n<130))  processTermTerm(n, outputMode, basicAnalysis, lambda, args);

      if ((n>=130) && (n<140))  processSections(n, outputMode, basicAnalysis, args);

      if ((n>=140) && (n<150))  processPaperPaper(n, outputMode, basicAnalysis, lambda, args);

    }

    
}
