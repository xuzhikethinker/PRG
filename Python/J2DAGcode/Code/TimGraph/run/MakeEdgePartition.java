/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.io.FileOutput;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import TimGraph.Community.VertexPartition;
//import TimGraph.Community.LouvainVertexPartition;
//import TimGraph.Community.SimulatedAnnealingvertexPartition;
//import TimGraph.io.GraphViz;
//import TimGraph.io.GraphMLGenerator;
import TimGraph.Community.QualitySparse;
import TimGraph.Community.QualityType;
import TimGraph.Community.VertexCommunity;
import TimGraph.OutputMode;
import TimGraph.algorithms.LineGraphProjector;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.StringFilter;




/**
 * Basic routine that produces communities for edges via a line graph.
 * <p>Produces much information including compaisons with vertex communities.
 * @author time
 */
public class MakeEdgePartition {
    
    final static String [] COMMUNITYMETHOD = {"Louvain","SimAn"};
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static timgraph tg;
    static timgraph lineGraphtg; 
    static int infoLevel=0;
    
    public MakeEdgePartition(timgraph newtg){tg=newtg;}
    
    
    static public void usage(PrintStream PS){
     char arg=timgraph.NOT_TIMGRAPH_ARGUMENT[0];   
     PS.println("LineGraphCommunities "+arg+"<filename> "+arg+"<method> "+arg+"<lineGraphType> "+arg+"<quality> "+arg+"<lambda> -<timgraphArgument> ...");   
     PS.println("Arguments for this class start with any of "+timgraph.otherArguments(", ")+", but "+arg+" used for example here.");
     PS.println("First "+arg+" argument is the type of line graph");
     PS.println("Second "+arg+" argument is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing");
     PS.println("Third "+arg+" argument is type of line graph to use 0=C, 1=Ctilde, 2=D, 3=E (Dtilde)");
     PS.println("Fourth "+arg+" argument is definition of quality to use, 0=Q(A), 1=Q(A"+arg+"A-A)");
     PS.println("Fifth "+arg+" argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory ");
     PS.println("Sixth "+arg+" argument is value of lambda, scaling parameter of null model.");
     PS.println("Remaining arguments should start with a "+timgraph.TIMGRAPH_ARGUMENT+" and are parsed by <tt>timgraph</tt>.");  
     SetUpNetwork.printNetworkTypes(PS, "", "  ");
    }

    /**
     * Basic routine to study edge partitions.
     * <p>The initial arguments start which with a ':'
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>).
     * <p>First ':' argument is the type of line graph
     * <p>Second ':' argument is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing
     * <p>Third ':' argument is type of quality to use, 0=Q(A), 1=Q(A*A-A)
     * <p>Fourth ':' argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory ");
     * <p>Fifth ':' argument is value of gamma, scaling parameter of null model.
     * <p>Remaining arguments should start with a '-' and are parsed by <tt>timgraph</tt>.
     * <p>The different types are defined in the weighted graph paper
     * <em>Edge Partitions and Overlapping Communities in Complex Networks</em>,
     * T.S.Evans and R.Lambiotte <tt>arXiv:0912.4389</tt>.  They are set by the
     * <tt>type</tt> parameters and defined by constant strings in this class,
     * {@link TimGraph.algorithms.LineGraphProjector#lgExtensionList} for file
     * name additions and {@link TimGraph.algorithms.LineGraphProjector#lgExtensionDescription}.
     * These are as follows:
     * <ul>
     * <li>0 = Line Graph L(G)=C(G)</li>
     * <li>1 = Line Graph with self-loops, Ctilde</li>
     * <li>2 = Degree Weighted Line Graph, D(G)</li>
     * <li>3 = Degree Weighted Line Graph with self-loops, Dtilde(G) (E(G) in unweighted paper)</li>
     * <li>4 = Strength Weighted Line Graph, E(G) </li>
     * <li>5 = Strength Weighted Line Graph with self-loops, Etilde(G)</li>
     * </ul>
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
      System.out.println("MakeEdgePartition Arguments :<method> :<qualitydef> :<qualitytype> :<gammaMin> :<gammaMax> :<gammaStep> :numberRuns :<graphMLOn>");

      int ano=0;
      int lgmethod = 2; // 0=Line Graph (C),
                      // 1=Line Graph with self-loops (Ctilde),
                      // 2=Degree weighted Line Graph (D),
                      // 3=Degree weighted Line Graph with self loops (Dtilde)
                      // 4=Strength Weighted Line Graph (E),
                      // 5=Strength Weighted Line Graph with self loops (Etilde)
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) lgmethod=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Using Line Graph type "+LineGraphProjector.lgExtensionDescription[lgmethod]);



      ano++;
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

      double gammaMax = 1.1; //gammaMin+1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaMax=Double.parseDouble(args[ano].substring(1, args[ano].length()));


      double minStep=1e-3;
      double gammaStep = 1.0; //gammaMax-gammaMin+1.0;
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
      boolean projectedGraphOutput=false; //true;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) graphMLOutput=StringFilter.trueString(args[ano].charAt(1));
      System.out.println("--- file output is "+StringFilter.onOffString(fileOutput));
      System.out.println("--- graphML output is "+StringFilter.onOffString(graphMLOutput));
      System.out.println("--- projected graph output is "+StringFilter.onOffString(projectedGraphOutput));




      //SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      //tg = SetUpNetwork.setUpNetwork(n, args);

     // if no argument list use the following set up (i.e. for testing)
     String basicFileName="BowTie";
     String [] aList = { "-fin"+basicFileName, "-fieinputELS.dat", "-o0", "-xi0"};

//     String basicFileName="karateTSE"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gelf", "-gbf", "-fin"+basicFileName, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     String basicFileName="BGCGx6y6c4s360p500proj";
     //String basicFileName="TGCGn1x6y6t216p0";
     //String basicFileName="TGCGn1x6y6t72p0";
     //BGCGx6y6c3s288p100proj"; //UKHEIiikpinvnsl"; //BowTieW";
     //String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gelf", "-gbf", "-gemf", "-gesf",  "-fin"+basicFileName, "-fieinputEL.dat", "-gn99",  "-e0", "-o0", "-xi0"};
//     String basicFileName="sltest"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileName, "-fieinputEL.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     String basicFileName="BowTieWWLG"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-gbt", "-fin"+basicFileName, "-fieinputEL.dat", "-gn99",  "-e0", "-o23", "-xi0"};
     if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);

     if (!tg.isVertexEdgeListOn()) System.err.println("\n *** Vertex Edge List NOT on and it is required, use -vgvet option ***\n");

     tg.setNameRoot(tg.inputName.getNameRoot());
//     tg.setNetworkFromInputFile(); // this will read in the network
     int xColumn=1;
     int yColumn=2;
     boolean headerOn=false;
     boolean infoOn=false;
     int nameColumn =1;
     tg.setNetworkFromInputFile(xColumn, yColumn, nameColumn, headerOn, infoOn); // this will read in the network
     tg.printParametersBasic();

     tg.calcStrength();

      FileOutput fo = new FileOutput(tg);
      PrintStream PS;
      FileOutputStream fout;
      String extension = "_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"_gamma.dat";
      fo.fileName.setNameEnd(extension);

      boolean printTriangles=true;
      boolean printSquares=true;
      boolean vertexListOn=true;
      boolean edgeListOn=false;
      boolean graphMLOn=false;
      boolean EP2VCprojOn=false;
      boolean incidenceGraphOn=false;
      boolean nextbit = true;
      analyseManyEdgePartitions(numberRuns,
                           method, lgmethod, qdef, qualityType,
                           gammaMin, gammaMax, gammaStep,
                           printTriangles, printSquares, vertexListOn, edgeListOn,
                           graphMLOn, EP2VCprojOn, incidenceGraphOn, nextbit);

    }

    
    /**
     * Basic routine to study line graph and its communities.
     * <p>Takes one input graph produces its line graph then
     * runs through several values of gamma, each multiple times,
     * to produce different edge partitions.
     * @param numberRuns number of runs to make
     * @param method selects quality optimisation method for graph
     * @param lgmethod selects quality optimisation method for line graph
     * @param qdef defintion of quality to use
     * @param qualityType type of quality class to use
     * @param printTriangles true (false) if (don't) want to print number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param vertexListOn true (false) if (don't) want to have vertex list given
     * @param edgeListOn true (false) if (don't) want to have edge list output
     * @param graphMLOn true (false) if (don't) want graphml file output
     * @param EP2VCprojOn true (false) if (don't) want to have a vertex community from the edge partition
     * @param incidenceGraphOn true (false) if (don't) want to have incidence graph output
     * @param nextbit true (false) if (don't) want to have next bit of code executed
     */
    public static void analyseManyEdgePartitions(int numberRuns,
            int method, int lgmethod, int qdef, int qualityType, 
            double gammaMin, double gammaMax, double gammaStep,
            boolean printTriangles, boolean printSquares, boolean vertexListOn, boolean edgeListOn,
            boolean graphMLOn, boolean EP2VCprojOn, boolean incidenceGraphOn, boolean nextbit)
    {
      basicroot = tg.inputName.getNameRoot();
      FileOutput fo = new FileOutput(tg);
      OutputMode outputControl=null;
      boolean printNNOn=false;
      fo.informationGeneral("", SEP, 
              vertexListOn, printTriangles, printSquares,
              printNNOn,
              edgeListOn, graphMLOn, outputControl);
      if (tg.isVertexLabelled()) fo.printEdges(true, false, false, false, false);
      else fo.edgeListSimple();

      System.out.println("\n *** \n *** Making Line Graph type "+LineGraphProjector.lgExtensionDescription[lgmethod]+" *** \n");    
      lineGraphtg = LineGraphProjector.makeLineGraph(tg,lgmethod, true);
      
      //lineGraphtg.setNameRoot(basicroot+lineGraphType);
      FileOutput folg = new FileOutput(lineGraphtg);
      //folg.informationGeneral("", SEP);
      if (graphMLOn) folg.graphML();
      folg.printEdges(true,true,true,true,true);

      PrintStream PS;
      FileOutputStream fout;
      String extension = "_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"_gamma.dat";
      fo.fileName.setNameEnd(extension);

        
      //lineGraphtg.printEdges();

      VertexPartition ep;
      VertexPartition louvainvp=null;
      
      try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            for (double gamma=gammaMin; gamma<=gammaMax; gamma+=gammaStep){
               if (gamma<=0) break;
               System.out.println("\n--- gamma="+String.format("%6.3f", gamma)+" ----------------------------------------------------");
               louvainvp = VertexPartition.calculate(tg, qdef, qualityType, gamma, method, infoLevel);
               vertexPartitionGraphML(tg, louvainvp);

               for (int r=0; r<numberRuns; r++){
                   ep =analyseOneEdgePartition(r,
                           method, lgmethod, qdef, qualityType, gamma,
                           printTriangles,  printSquares, vertexListOn, edgeListOn,
                           graphMLOn, EP2VCprojOn, incidenceGraphOn, nextbit, louvainvp);
                   PS.println(r+SEP+ep.getName()+SEP+ep.informationNumbers("", SEP));
               }


            } //eo for gamma

            if (infoLevel>-2) System.out.println("Finished writing EP various gamma information file to "+ fo.fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fo.fileName.getFullFileName()+", "+e.getMessage());
            return;
        }


    }

    /**
     * Analyses one vertex partitions of a line graph.
     * <p>This is done in relation to the original graph.
     * @param runNumber used to distinguish the edge partition, if negative then not used
     * @param method
     * @param lgmethod
     * @param qdef
     * @param qualityType
     * @param gamma
     * @param printTriangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param vertexListOn
     * @param edgeListOn
     * @param graphMLOn
     * @param EP2VCprojOn
     * @param incidenceGraphOn
     * @param nextbit
     * @return a vertex partition of the line graph
     */
    public static VertexPartition analyseOneEdgePartition(int runNumber,
            int method, int lgmethod, int qdef, int qualityType,
            double gamma,
            boolean printTriangles, boolean  printSquares,
            boolean vertexListOn, boolean edgeListOn,
            boolean graphMLOn, boolean EP2VCprojOn, boolean incidenceGraphOn, boolean nextbit,
             VertexPartition louvainvp)
    {
      FileOutput fo = new FileOutput(tg);

      FileOutput folg = new FileOutput(lineGraphtg);


      VertexPartition louvainep;
      System.out.println("*** Line Graph partition of type "+QualitySparse.QdefinitionString[qdef]+", gamma="+gamma);
      louvainep = VertexPartition.calculate(lineGraphtg, qdef, qualityType, gamma, method, infoLevel);
      louvainep.setName(louvainep.getName() + LineGraphProjector.lgExtensionList[lgmethod]+((runNumber<0)?"":"r"+runNumber) );
      System.out.println("    Communities info: "+louvainep.informationNumbers("", SEP));

        //boolean nextbit = true;
        if (nextbit) {
            //testGraphViz(bestep);
            tg.setEdgeLabels(louvainep);
            //tg.setNameRoot(basicroot+COMMUNITYMETHOD[method]+"EP");
            fo = new FileOutput(tg);
            fo.printEdges(true,true, true, true, louvainep.getName());
            //printTriangles=false;
            fo.printVertices("", SEP, null, printTriangles, printSquares, false);
            boolean splitBipartite=false;
            boolean outputType1=false;
            fo.printEdgeCommunityStats(louvainep.getName(),true, true, splitBipartite, outputType1);

            edgePartitionGraphML(tg, louvainep);
            if (graphMLOn) folg.graphMLVertexPartition(louvainep);
            folg.informationPartition(louvainep, "", SEP, true);
            folg.printVertices("", SEP, louvainep, printTriangles, printSquares,  false);

            
            
            //tg.setEdgeLabels(louvainep); // set edge labels to match the edge partition
            //fo.printEdgeCommunityStats(louvainep.getName(),true, true);


            if (louvainvp!=null) try{vertexEdgePartitionGraphML(tg, louvainvp, louvainep);}
            catch (RuntimeException e){ System.err.println("*** Tried and failed to produce vertex and edge parttioned graphML file, "+e);}
            //folg.fileName.setNameRoot(basicroot+"LG");//+COMMUNITYMETHOD[method]);
        }
  
      if (EP2VCprojOn) {
           System.out.println("Trying a projection from edge partition to vertex community");
           doEP2VC(fo, tg, louvainep, qdef ,gamma, qualityType);
      }
      else System.out.println("No projection from edge partition to vertex community");
      
//      boolean actualOn=false;
//      if (actualOn) {if (basicroot.startsWith("karate")) karateActualVP(fo,folg, method, louvainep);}
//      else System.out.println("No actual partition for karate dealt with");
//
//      boolean bestOn=false;
//      if (bestOn) {
//      if (basicroot.startsWith("karate")) karateBestVP(fo,folg, method, louvainep);}
//      else System.out.println("No best  partition for karate dealt with");
      
          //otherKarateTests(fo,folg, method, louvainlgvp);
        
//      VertexPartition louvainep1 = VertexPartition.calculate(lineGraphtg, 1, method);
//      tg.setEdgeLabels(louvainep1);
//      edgePartitionGraphML(tg, louvainlgvp);
//      vertexEdgePartitionGraphML(tg, louvainvp, louvainlgvp);
//      folg.fileName.setNameRoot(basicroot+"LG");//+COMMUNITYMETHOD[method]);
//      folg.graphMLVertexPartition(louvainlgvp);
//      folg.informationVertexPartition(louvainlgvp,"",SEP,true);
  

      if (incidenceGraphOn) {
          System.out.println("\n *** \n *** Incidence Graph\n *** ");
           incidenceGraph(method);
      }
      else System.out.println("No incidence graph");

      return louvainep;
    }


    /**
     * Analyses vertex and edge partitions of graph.
     * <p>Method accessible by other routines to produce edge coloured graph.
     * <br>Also produces various other files including line graph edge files etc.
     * @param tg initial graph to be analysed.
     * @param method method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing
     * @param lgmethod type of line graph to use 0=C, 1=Ctilde, 2=D, 3=E (Dtilde)
     * @param qdef type of quality to use, 0=Q(A), 1=Q(A*A-A)
     * @param qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory   
     * @param lambda value of lambda, scaling parameter of null model.
     */
    static public void analyseGraph(timgraph tg, int method, int lgmethod, int qdef, int qualityType, double lambda){
      System.out.println("\n *** Analysing vertex and edge partitions of graph "+tg.inputName.getNameRoot());  
      VertexPartition louvainvp = VertexPartition.calculate(tg, qdef, qualityType, lambda, method, infoLevel); 
      //testGraphViz(bestvp);
      MakeEdgePartition.vertexPartitionGraphML(tg, louvainvp);

      FileOutput fo = new FileOutput(tg);
      fo.pajekPartition(louvainvp.getName(), louvainvp);

      System.out.println("\n *** Making Line Graph type "+LineGraphProjector.lgExtensionDescription[lgmethod]+" *** \n");    
      timgraph lg = LineGraphProjector.makeLineGraph(tg, lgmethod, true);
      
      //lg.setNameRoot(basicroot+lineGraphType);
      FileOutput folg = new FileOutput(lg);
      //folg.informationGeneral("", SEP);
      folg.graphML();
      folg.printEdges(true,true,true,true,true);
      
      System.out.println("\n ***  Line Graph partition of type "+QualitySparse.QdefinitionString[qdef]+", quality type ="+QualityType.qualityLongName[qualityType]+", lambda="+lambda);    
      VertexPartition louvainlgvp = VertexPartition.calculate(lg, qdef, qualityType, lambda, method, infoLevel);
      louvainlgvp.setName(louvainlgvp.getName() + LineGraphProjector.lgExtensionList[lgmethod]);
            
      tg.setEdgeLabels(louvainlgvp);
      fo.printEdges(true,true, true, true, louvainlgvp.getName());
      boolean printNearestNeighbours=false;
      boolean printTriangles =true;
      boolean  printSquares=true;
      fo.printVertices("", SEP, null, printTriangles,  printSquares, printNearestNeighbours );
      boolean splitBipartite=false;
      boolean outputType1=false;
      fo.printEdgeCommunityStats(true, true, splitBipartite, outputType1);

            MakeEdgePartition.edgePartitionGraphML(tg, louvainlgvp);
            MakeEdgePartition.vertexEdgePartitionGraphML(tg, louvainvp, louvainlgvp);
            folg.graphMLVertexPartition(louvainlgvp);
            folg.informationPartition(louvainlgvp, "", SEP, true);
       System.out.println("*** Finished analysing vertex and edge partitions of graph "+tg.inputName.getNameRoot());  
           
     }

    
    /**
     * Creates a vertex partitions from an edge community.
     * @param fo
     * @param tg
     * @param lgvp
     * @param qdef
     * @param newlambda
     * @param qualityType
     */
    public static void doEP2VC(FileOutput fo, timgraph tg, VertexPartition lgvp, int qdef, double newlambda, int qualityType){
        VertexCommunity vc = new VertexCommunity(tg,lgvp, qdef, qualityType, newlambda);
        vc.calcQuality();
        fo.informationVertexCommunity(vc, "", SEP, true);
        
     }
    
    /**
     * Takes an Edge partition and creates a vertex community and a vertex parttion from this. 
     * @param fo
     * @param tg
     * @param lgvp
     * @param qdef
     * @param newlambda
     * @param qualityType
     */
    public static void doEP2VCandVP(FileOutput fo, timgraph tg, VertexPartition lgvp, int qdef, double newlambda, int qualityType){
        VertexCommunity vc = new VertexCommunity(tg,lgvp, qdef, qualityType, newlambda);
        vc.calcQuality();
        fo.informationVertexCommunity(vc, "", SEP, true);
        VertexPartition pvp = new VertexPartition(tg,lgvp);
        pvp.calcQuality();
//      fo.informationVertexPartition(pvp, "", SEP, true);
        fo.graphMLVertexPartition(pvp);
        vertexEdgePartitionGraphML(tg, pvp, lgvp);
    }

        public static void vertexPartitionGraphML(timgraph tg, VertexPartition vp){
        FileOutput fo = new FileOutput(tg);
        fo.graphMLVertexPartition(vp);
        fo.printPartitionStats(vp, "", SEP, true, true);
        fo.informationPartition(vp, "", SEP, true);
    }


    /**
     * Output graphML file colouring edges according to edge partition.
     * @param ep edge partition produced using a vertex partition of the line graph.
     */
    public static void edgePartitionGraphML(timgraph tg, VertexPartition ep){
        FileOutput fo = new FileOutput(tg);
        fo.graphMLEdgePartition(ep);
        fo.informationEdgePartition(ep, "", SEP, true);
    }

    public static void vertexEdgePartitionGraphML(timgraph tg, VertexPartition vp, VertexPartition ep){
        FileOutput fo = new FileOutput(tg);
        fo.graphMLVertexEdgePartition(vp, ep);
        fo.informationVertexEdgePartition(vp,ep, "", SEP, true);
    }

    public static void incidenceVertexPartitionGraphML(timgraph tg, VertexPartition ip){
        FileOutput fo = new FileOutput(tg);
        fo.graphMLIncidencePartition(ip);
        fo.informationIncidenceVertexPartition(ip, "", SEP, true);
    }

      // ................................................................
    public static void incidenceGraph(int vpmethod) {
        timgraph ig = tg.makeIncidenceGraph(tg.isDirected(), true, tg.isWeighted(), true);
        if (infoLevel > 1) {
            if (ig.getNumberVertices() < 21) {
                ig.printNetwork(true);
            }
        }
        FileOutput foig = new FileOutput(ig);
        foig.informationNetworkBasic("", "\t");
        foig.graphML();
        foig.edgeListSimple();
        foig.printVertices("", "\t", null, true, true, true, false, false, false, false, false, false, false, true);

        VertexPartition iglouvainvp = VertexPartition.calculate(ig, 0, vpmethod, infoLevel);
        vertexPartitionGraphML(ig, iglouvainvp);
        incidenceVertexPartitionGraphML(tg, iglouvainvp);
    }


//    static public void testLambda(int method){
//      VertexPartition louvainep;
//      double lambda;
//      double [] lambdaList= {0.001,0.1,0.2,0.3,0.4,0.425,0.45,0.5,0.55,0.6,0.7,0.8,0.9,1.0,2.0,4.0,10.0,20,100};
//      //double [] lambdaList=         {0.1,0.2,0.3,0.4,0.425,0.45,0.5,0.55,0.6,0.7,0.8,0.9,1.0};
//      double [] results = new double[lambdaList.length];
//      int [] nc = new int[lambdaList.length];
//      for (int i=0; i<lambdaList.length; i++){
//          lambda=lambdaList[i];
//          louvainep = VertexPartition.calculate(lineGraphtg, 0, 1, lambda, method, infoLevel);
//          results[i]=louvainep.calcQuality();
//          nc[i]=louvainep.getNumberOfCommunities();
//      }
//      System.out.println("lambda"+SEP+"Q"+SEP+"No.Comm.");
//      for (int i=0; i<lambdaList.length; i++){
//          System.out.println(lambdaList[i]+SEP+results[i]+SEP+nc[i]);
//      }
//
//
//    }
//    public static VertexPartition karateActualVP(FileOutput fo, FileOutput folg, int method, VertexPartition louvainep) {
//        String pname;
//        pname = "Actualvp";
//        VertexPartition actualvp = readVP(tg, "karate", pname);
//        actualvp.recalculateCommunityLabels();
//        actualvp.calcQuality();
//        vertexPartitionGraphML(tg, actualvp);
//        vertexEdgePartitionGraphML(tg, actualvp, louvainep);
//        return actualvp;
//    }
//
//    public static VertexPartition karateBestVP(FileOutput fo, FileOutput folg, int method, VertexPartition louvainep) {
//        String pname;
//        pname = "AK07BestQvp";
//        VertexPartition bestvp = readVP(tg, "karate", pname);
//        bestvp.recalculateCommunityLabels();
//        bestvp.calcQuality();
//        vertexPartitionGraphML(tg, bestvp);
//        vertexEdgePartitionGraphML(tg, bestvp, louvainep);
//        return bestvp;
//    }
//
//     public static void otherKarateTests(FileOutput fo, FileOutput folg, int method, VertexPartition louvainep){
//      String pname;
//      // LineGraphCommunities input
////      String basicoutputroot = tg.outputName.getNameRoot();
////      String basicinputroot = "karate";
//      pname="AK07BestQvp";
////      tg.inputName.setNameRoot(basicinputroot+pname);
//      VertexPartition bestvp = readVP(tg, "karate", pname);
//      //bestvp.setName("BestQvp");
//      bestvp.recalculateCommunityLabels();
//      bestvp.calcQuality();
//      vertexPartitionGraphML(tg, bestvp);
//
//
//      //       LineGraphCommunities input
////      lineGraphtg.outputName.setNameRoot(basicoutputroot+"LG");
//      pname="Other";
//      lineGraphtg.inputName.setNameRoot("karateNeATLG");
//      //lineGraphtg.infoLevel=2;
//      VertexPartition LGothervp = readVP(lineGraphtg,lineGraphtg.inputName.getNameRoot(), pname);
//      //LGothervp.setName("Othervp");
//      LGothervp.recalculateCommunityLabels();
//      LGothervp.calcQuality();
//      vertexPartitionGraphML(lineGraphtg,LGothervp);
//      LGothervp.printInformation(System.out, " ", "  ");
//      //LGothervp.printCommunities(System.out, " ", "  ");
//      //lineGraphtg.infoLevel=0;
//
////      tg.outputName.setNameRoot(basicoutputroot);
//      pname="Otherep";
//      tg.inputName.setNameRoot("karateNeAT"+pname);
//      VertexPartition otherep = readEP(tg,pname);
//      //otherep.setName("Otherep");
//      otherep.printInformation(System.out, " ", "  ");
//      tg.setEdgeLabels(otherep);
//      fo.fileName.setNameRoot(tg.outputName.getNameRoot());
//      fo.printEdges(true,true,true,true,otherep.getName());
////tg.printEdges();
//
//
//
//      //otherep.printCommunities(System.out, "", "  ");
//      edgePartitionGraphML(tg,otherep);
//      folg.fileName.setNameRoot(basicroot+"LG");
//      folg.graphMLVertexPartition(otherep);
//
//
//
//
//
//      //tg.setNameRoot("karateAK07BestQVP"+COMMUNITYMETHOD[method]+"EP");
//      vertexEdgePartitionGraphML(tg,bestvp,louvainep);
//
//      //tg.setNameRoot("karateActualVPOtherEP");
//      //vertexEdgePartitionGraphML(tg,actualvp,otherep);
//
//
//      //tg.setNameRoot("karateAK07BestQOther");
//      vertexEdgePartitionGraphML(tg,bestvp,otherep);
//
//    }
//
//   /**
//    * Tests Vertex partition of a graph read in from a file
//    * <br>Reads from
//    * <tt>tg.inputName.getDirectoryFull()</tt>+<tt>nameRoot</tt>+<tt>vpName</tt>+<tt>inputVP.dat</tt>
//    * @param tg graph.
//    * @param nameRoot root of file name containing vertex partition
//    * @param vpName file name containing vertex partition
//    * @return Vertex parttion of original graph
//    */
//   public static VertexPartition readVP(timgraph tg, String nameRoot, String vpName){
//      VertexPartition vp = new VertexPartition(tg);
//      vp.initialise(tg);
//      vp.setName(vpName);
//      boolean relabelOn=true;
//      vp.readIntPartition(tg.inputName.getDirectoryFull()+nameRoot+vpName+"inputVP.dat", 1, 2, true, relabelOn);
//      //int [] cov = new int[lineGraphtg.getNumberVertices()];
//      //for (int v=0; v<lineGraphtg.getNumberVertices();v++) cov[v]=VertexPartition.UNSET;
//      //vp.relabelCommunities();
//      vp.calcQuality();
//      return vp;
//   }
//
//   /**
//    * Reads edge partition of a graph read in from a file.
//    * <br>Constructs line graph so it can calculate quality.
//    * @param tg graph.
//    * @param epname name for partition
//    * @return Edge parttion of original graph
//    */
//   public static VertexPartition readEP(timgraph tg, String epname){
//       boolean makeUnweighted=false;
//       boolean useSelfLoops=false;
//       //boolean makeUnlabelled, boolean reverseDirection
//      //String lineGraphType = "LG"+(makeUnweighted?"uw":"w")+(useSelfLoops?"sl":"nsl");
//      timgraph lgtg = new timgraph(tg,false,makeUnweighted,useSelfLoops,false,false);
//      //lineGraphtg.setNameRoot(basicroot+lineGraphType);
//
//      //timgraph lgtg = new timgraph(tg,false,false);
//      VertexPartition ep = new VertexPartition(lgtg);
//      ep.initialise(lgtg);
//      ep.setName(epname);
//      boolean relabelOn=true;
//      ep.readIntPartition(tg.inputName.getNameRootFullPath()+"inputEP.dat", 1, 2, true, relabelOn);
//      //int [] cov = new int[lineGraphtg.getNumberVertices()];
//      //for (int v=0; v<lineGraphtg.getNumberVertices();v++) cov[v]=VertexPartition.UNSET;
//      //ep.relabelCommunities();
//      ep.calcQuality();
//      return ep;
//   }
//
//    public static void graphML(timgraph tg){
//        FileOutput fo = new FileOutput(tg);
//        fo.graphML();
//    }
//
//

////     /**
////     * Calculates a partion using indicated method.
////     * @param method 1=Louvain, 2= Simulated Annealing
////     * @param qualityDefinition sets formula for modularity, 0=simple Newman
////     * @return Best VertexPartition found.
////     * @deprecated don't use this
////     */
////     public static VertexPartition VertexPartition.calculateOLD(int method, int qualityDefinition ){
////      VertexPartition c;
////
////      switch (method) {
////          default:
////          case 0:
////                if (infoLevel>-1) System.out.println("Louvain VertexCommunity");
////                LouvainVertexPartition lc = new LouvainVertexPartition(tg,qualityDefinition,infoLevel);
////                lc.calculate();
////                c= (VertexPartition) lc;
////                break;
////            case 1:
////                if (infoLevel>-1) System.out.println("Simulated Annealing VertexCommunity");
////                SimulatedAnnealingvertexPartition sac = new SimulatedAnnealingvertexPartition(tg);
////                sac.calc(100, 0.5);
////                c= (VertexPartition) sac;
////                break;
////        }
////
////      c.reVertexPartition.calculateLabels();
////      System.out.println("*** Best community:-");
////      c.printInformation(System.out, "", " ");
////      //c.printCommunities(System.out, "", " ");
////      return c;
////    }
//
//
//
//    public static void testLineGraphity(){
//        tg.printEdges();
//        System.out.println("*** LineGraph Graph");
//         boolean makeUnweighted=false;
//       boolean useSelfLoops=false;
//       //boolean makeUnlabelled, boolean reverseDirection
//       String lineGraphType = "LG"+(makeUnweighted?"uw":"w")+(useSelfLoops?"sl":"nsl");
//      timgraph ltg = new timgraph(tg,false,makeUnweighted,useSelfLoops,false,false);
//      ltg.setNameRoot(basicroot+lineGraphType);
//      //timgraph ltg = new timgraph(tg,false,false);
//        ltg.printEdges();
//    }
//
//
//      public VertexPartition testLouvainCommunityTwoSteps(){
//      VertexPartition c;
//      int method = 1;
//      switch (method) {
//          default:
//          case 1:
//                LouvainVertexPartition lc = new LouvainVertexPartition(tg,0,1,2);
//                lc.calculate();
//                c= (VertexPartition) lc;
//                break;
//            case 2:
//                SimulatedAnnealingvertexPartition sac = new SimulatedAnnealingvertexPartition(tg);
//                sac.calc(10, 0.5);
//                c= (VertexPartition) sac;
//                break;
//        }
//      c.recalculateCommunityLabels();
//      //c.printCommunities(System.out, " ", " , ");
//      boolean ignoreNegativeLabels=true;
//      c.relabelCommunities(ignoreNegativeLabels);
//      //c.printCommunities(System.out, " ", " , ");
//
//      System.out.println("*** now Projected Graph");
//      timgraph projg = new timgraph(tg,c.getCommunity(), c.getNumberOfCommunities(), false, false);
//      projg.printEdges(System.out);
//      switch (method) {
//          default:
//          case 1:
//                LouvainVertexPartition lc = new LouvainVertexPartition(projg,0,1,2);
//                lc.calculate();
//                c= (VertexPartition) lc;
//                break;
//            case 2:
//                SimulatedAnnealingvertexPartition sac = new SimulatedAnnealingvertexPartition(projg);
//                sac.calc(10, 0.5);
//                c= (VertexPartition) sac;
//                break;
//        }
//      c.relabelCommunities(ignoreNegativeLabels);
//      //c.printCommunities(System.out, " ", " , ");
//      return c;
//    }
//
//    public static void testLineGraphGraphMLOld(timgraph tg, VertexPartition c){
//        GraphMLGenerator gml = new GraphMLGenerator();
//        PrintStream PS;
//        FileOutputStream fout;
//        tg.outputName.setNameEnd(".graphml");
//        if (tg.getInformationLevel()>-2) System.out.println("Writing LineGraphGraphML file to "+ tg.outputName.getFullFileName());
//            try {
//            fout = new FileOutputStream(tg.outputName.getFullFileName());
//            PS = new PrintStream(fout);
//            gml.outputEdgePartition(PS, tg, c);
//
//            if (tg.getInformationLevel()>-2) System.out.println("Finished writing LineGraphGraphML file to "+ tg.outputName.getFullFileName());
//            try{ fout.close ();
//               } catch (IOException e) { System.err.println("*** File Error with " +tg.outputName.getFullFileName());}
//
//        } catch (FileNotFoundException e) {
//            System.err.println("Error opening output file "+tg.outputName.getFullFileName());
//            return;
//        }
//        return;
//
//
//    }
//
//
//   public static void testGraphViz(timgraph tg, VertexPartition c){
//        GraphViz gv = new GraphViz();
//        PrintStream PS;
//        FileOutputStream fout;
//        tg.inputName.setNameEnd(".gv");
//        if (tg.getInformationLevel()>-2) System.out.println("Writing GraphViz file to "+ tg.outputName.getFullFileName());
//            try {
//            fout = new FileOutputStream(tg.outputName.getFullFileName());
//            PS = new PrintStream(fout);
//            gv.outputVertexCommunity(PS, tg, c, true);
//
//            if (tg.getInformationLevel()>-2) System.out.println("Finished writing GraphViz file to "+ tg.outputName.getFullFileName());
//            try{ fout.close ();
//               } catch (IOException e) { System.err.println("*** File Error with " +tg.outputName.getFullFileName());}
//
//        } catch (FileNotFoundException e) {
//            System.err.println("Error opening output file "+tg.outputName.getFullFileName());
//            return;
//        }
//        return;
//
//
//    }


}
