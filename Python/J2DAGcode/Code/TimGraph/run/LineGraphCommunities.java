/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.io.FileOutput;
import TimGraph.*;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.TreeSet;

import TimGraph.Community.VertexPartition;
import TimGraph.Community.LouvainVertexPartition;
import TimGraph.Community.SimulatedAnnealingvertexPartition;
import TimGraph.io.GraphViz;
import TimGraph.io.GraphMLGenerator;
import TimGraph.Community.QualitySparse;
import TimGraph.Community.QualityType;
//import java.util.TreeSet;
import TimGraph.Community.VertexCommunity;
import TimGraph.algorithms.LineGraphProjector;




/**
 * Basic routine that produces communities for vertices and edges, the latter via a line graph.
 * <p>
 * @author time
 */
public class LineGraphCommunities {
    
    final static String [] COMMUNITYMETHOD = {"Louvain","SimAn"};
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static timgraph tg;
    static timgraph lineGraphtg; 
    static int infoLevel=0;
    
    public LineGraphCommunities(timgraph newtg){tg=newtg;}
    
    
    static public void usage(PrintStream PS){
     char arg=timgraph.NOT_TIMGRAPH_ARGUMENT[0];   
     PS.println("LineGraphCommunities "+arg+"<filename> "+arg+"<method> "+arg+"<lineGraphType> "+arg+"<quality> "+arg+"<lambda> -<timgraphArgument> ...");   
     PS.println("Arguments for this class start with any of "+timgraph.otherArguments(", ")+", but "+arg+" used for example here.");
     PS.println("First "+arg+" argument is the network number for the setup routine OR ? for this help");
     PS.println("Second "+arg+" argument is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing");
     PS.println("Third "+arg+" argument is type of line graph to use 0=C, 1=Ctilde, 2=D, 3=E (Dtilde)");
     PS.println("Fourth "+arg+" argument is definition of quality to use, 0=Q(A), 1=Q(A"+arg+"A-A)");
     PS.println("Fifth "+arg+" argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory ");
     PS.println("Sixth "+arg+" argument is value of lambda, scaling parameter of null model.");
     PS.println("Remaining arguments should start with a "+timgraph.TIMGRAPH_ARGUMENT+" and are parsed by <tt>timgraph</tt>.");  
     SetUpNetwork.printNetworkTypes(PS, "", "  ");
    }
    
    /**
     * Basic routine to study communities and line graph communities.
     * <p>If first argument starts with a ':'
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>)
     * the rest of this argument is the network number for the setup routine
     * If arguments exits but the first has no ':' then network is set up from file read in using the arguments
     * to point it to the right file.
     * <p>Second ':' argument is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing
     * <p>Third ':' argument is type of line graph to use 0=C, 1=Ctilde, 2=D, 3=Dtilde 4=E 5=Etilde
     * <p>Fourth ':' argument is type of quality to use, 0=Q(A), 1=Q(A*A-A)
     * <p>Fifth ':' argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory ");
     * <p>Sixth ':' argument is value of lambda, scaling parameter of null model.
     * <p>Remaining arguments should start with a - and are parsed by <tt>timgraph</tt>.
     * <p>The line graph types are specified in terms of those in
     * <em>Edge Partitions and Overlapping Communities in Complex Networks</em>,
     * T.S.Evans and R.Lambiotte <tt>arXiv:0912.4389</tt>.  They are set by the
     * <tt>type</tt> parameter and defined by constant strings in
     * the {@link TimGraph.algorithms.LineGraphProjector} class, namely
     * {@link TimGraph.algorithms.LineGraphProjector#lgExtensionList} for file name additions
     * and {@link TimGraph.algorithms.LineGraphProjector#lgExtensionDescription}.
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
      System.out.println("LineGraphCommunities Arguments :networknumber :method :linegraphtype :qualitydef :qualityclass :lambda");
      
      //First arg chooses network
      int n=142; //30; //60; //1 30;
      int ano=0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) {
          if (args[ano].charAt(1)=='?') {usage(System.out); return;} 
          else n=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      }
      else n=0;
      System.out.println("--- Using network  "+SetUpNetwork.typeString(n));

      int method =0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) method=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Using community method "+COMMUNITYMETHOD[method]);

      int lgmethod = 2; // 0=Line Graph (C), 
                      // 1= Line Graph with self-loops (Ctilde), 
                      // 2=weighted Line Graph (D), 
                      // 3=weighted Line Graph with self loops (Dtilde)
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) lgmethod=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Using Line Graph type "+LineGraphProjector.lgExtensionDescription[lgmethod]);    

      // 0="QS", 1="QA2mA"
      int qdef=0; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qdef=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality definition used "+QualitySparse.QdefinitionString[qdef]);

      // qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory (not for directed graphs so unweighted case here only)
      int qualityType=2; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qualityType=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality class type "+QualityType.qualityLongName[qualityType]);
     

      double lambda = 1;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) lambda=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Line Graph null model scaling lambda="+lambda);    
      
      
        
      
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      
      tg = SetUpNetwork.setUpNetwork(n, args);

      // output graph as is.
      basicroot = tg.inputName.getNameRoot();
      FileOutput fo = new FileOutput(tg);
      boolean printTrianglesSquares=true;
      boolean vertexListOn=true;
      boolean edgeListOn=false;
      boolean graphMLOn=false; 
      boolean printNearestNeighbours=false;
      OutputMode outputControl=null;
      fo.informationGeneral("", SEP, 
              vertexListOn, printTrianglesSquares, printTrianglesSquares,
              printNearestNeighbours, edgeListOn, graphMLOn, outputControl);
      if (tg.isVertexLabelled()) fo.printEdges(true, false, false, false, false);
      else fo.edgeListSimple();
      //printEdges(boolean asNames, boolean infoOn, boolean headerOn, boolean edgeIndexOn, boolean edgeLabelOn) 
            

      

//      VertexPartition louvainvp1 = VertexPartition.calculate(0,method); 
//      vertexPartitionGraphML(tg, louvainvp1);


      System.out.println("\n *** \n *** Making Line Graph type "+LineGraphProjector.lgExtensionDescription[lgmethod]+" *** \n");    
      lineGraphtg = LineGraphProjector.makeLineGraph(tg,lgmethod, true);
      
      //lineGraphtg.setNameRoot(basicroot+lineGraphType);
      FileOutput folg = new FileOutput(lineGraphtg);
      //folg.informationGeneral("", SEP);
      if (graphMLOn) folg.graphML();
      folg.printEdges(true,true,true,true,true);


        
      //lineGraphtg.printEdges();

      VertexPartition louvainep;
//      boolean testLambdaOn=false;
//      if (testLambdaOn){
//      testLambda(method);
//      }
      
      System.out.println("*** Line Graph partition of type "+QualitySparse.QdefinitionString[qdef]+", lambda="+lambda);    
      louvainep = VertexPartition.calculate(lineGraphtg, qdef, qualityType, lambda, method, infoLevel);
      louvainep.setName(louvainep.getName() + LineGraphProjector.lgExtensionList[lgmethod]);
      
        boolean nextbit = true;
        if ((n>=140)&& (n<150)) nextbit=true;
        if (nextbit) {
            //testGraphViz(bestep);
            tg.setEdgeLabels(louvainep);
            //tg.setNameRoot(basicroot+COMMUNITYMETHOD[method]+"EP");
            fo = new FileOutput(tg);
            fo.printEdges(true,true, true, true, louvainep.getName());
            printTrianglesSquares=false;
            fo.printVertices("", SEP, null, printTrianglesSquares, printTrianglesSquares, false);
            boolean splitBipartite=false;
            boolean outputType1=false;
            fo.printEdgeCommunityStats(louvainep.getName(),true, true, splitBipartite, outputType1);

            edgePartitionGraphML(tg, louvainep);
            if (graphMLOn) folg.graphMLVertexPartition(louvainep);
            folg.informationPartition(louvainep, "", SEP, true);
            folg.printVertices("", SEP, louvainep, printTrianglesSquares, printTrianglesSquares, false);

            
            
            //tg.setEdgeLabels(louvainep); // set edge labels to match the edge partition
            //fo.printEdgeCommunityStats(louvainep.getName(),true, true);


            VertexPartition louvainvp = VertexPartition.calculate(tg, qdef, qualityType, lambda, method, infoLevel); 
            vertexPartitionGraphML(tg, louvainvp);
            vertexEdgePartitionGraphML(tg, louvainvp, louvainep);
            //folg.fileName.setNameRoot(basicroot+"LG");//+COMMUNITYMETHOD[method]);
        }
  
      boolean EP2VCprojOn=false;
      if ((n>=140)&& (n<150)) EP2VCprojOn=true;
        if (EP2VCprojOn) {
           System.out.println("Trying a projection from edge partition to vertex community");
           doEP2VC(fo, tg, louvainep, qdef ,lambda, qualityType);
      }
      else System.out.println("No projection from edge partition to vertex community");
      
      boolean actualOn=true;
      if (actualOn) {if (basicroot.startsWith("karate")) karateActualVP(fo,folg, method, louvainep);}
      else System.out.println("No actual partition for karate dealt with");
      
      boolean bestOn=true;
      if (bestOn) {
      if (basicroot.startsWith("karate")) karateBestVP(fo,folg, method, louvainep);}
      else System.out.println("No best  partition for karate dealt with");
      
          //otherKarateTests(fo,folg, method, louvainlgvp);
        
//      VertexPartition louvainep1 = VertexPartition.calculate(lineGraphtg, 1, method);
//      tg.setEdgeLabels(louvainep1);
//      edgePartitionGraphML(tg, louvainlgvp);
//      vertexEdgePartitionGraphML(tg, louvainvp, louvainlgvp);
//      folg.fileName.setNameRoot(basicroot+"LG");//+COMMUNITYMETHOD[method]);
//      folg.graphMLVertexPartition(louvainlgvp);
//      folg.informationVertexPartition(louvainlgvp,"",SEP,true);
  

      boolean incidenceGraphOn=false;
      if (incidenceGraphOn) {
          System.out.println("\n *** \n *** Incidence Graph\n *** ");
           incidenceGraph(method);
      }
      else System.out.println("No incidence graph");
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
      LineGraphCommunities.vertexPartitionGraphML(tg, louvainvp);

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
      boolean printTrianglesSquares =true;
      fo.printVertices("", SEP, null, printTrianglesSquares, printTrianglesSquares, printNearestNeighbours );
      boolean splitBipartite=false;
      boolean outputType1=false;
      fo.printEdgeCommunityStats(true, true, splitBipartite, outputType1);

            LineGraphCommunities.edgePartitionGraphML(tg, louvainlgvp);
            LineGraphCommunities.vertexEdgePartitionGraphML(tg, louvainvp, louvainlgvp);
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
    
    static public void testLambda(int method){
      VertexPartition louvainep;
      double lambda;
      double [] lambdaList= {0.001,0.1,0.2,0.3,0.4,0.425,0.45,0.5,0.55,0.6,0.7,0.8,0.9,1.0,2.0,4.0,10.0,20,100};
      //double [] lambdaList=         {0.1,0.2,0.3,0.4,0.425,0.45,0.5,0.55,0.6,0.7,0.8,0.9,1.0};
      double [] results = new double[lambdaList.length];
      int [] nc = new int[lambdaList.length];
      for (int i=0; i<lambdaList.length; i++){
          lambda=lambdaList[i];
          louvainep = VertexPartition.calculate(lineGraphtg, 0, 1, lambda, method, infoLevel);
          results[i]=louvainep.calcQuality();
          nc[i]=louvainep.getNumberOfCommunities();
      }
      System.out.println("lambda"+SEP+"Q"+SEP+"No.Comm.");
      for (int i=0; i<lambdaList.length; i++){
          System.out.println(lambdaList[i]+SEP+results[i]+SEP+nc[i]);
      }

        
    }
    public static VertexPartition karateActualVP(FileOutput fo, FileOutput folg, int method, VertexPartition louvainep) {
        String pname;
        pname = "Actualvp";
        VertexPartition actualvp = readVP(tg, "karate", pname);
        actualvp.recalculateCommunityLabels();
        actualvp.calcQuality();
        vertexPartitionGraphML(tg, actualvp);
        vertexEdgePartitionGraphML(tg, actualvp, louvainep);
        return actualvp;
    }
   
    public static VertexPartition karateBestVP(FileOutput fo, FileOutput folg, int method, VertexPartition louvainep) {
        String pname;
        pname = "AK07BestQvp";
        VertexPartition bestvp = readVP(tg, "karate", pname);
        bestvp.recalculateCommunityLabels();
        bestvp.calcQuality();
        vertexPartitionGraphML(tg, bestvp);
        vertexEdgePartitionGraphML(tg, bestvp, louvainep);
        return bestvp;
    }
   
     public static void otherKarateTests(FileOutput fo, FileOutput folg, int method, VertexPartition louvainep){
      String pname;
      // LineGraphCommunities input
//      String basicoutputroot = tg.outputName.getNameRoot();
//      String basicinputroot = "karate";
      pname="AK07BestQvp";
//      tg.inputName.setNameRoot(basicinputroot+pname);
      VertexPartition bestvp = readVP(tg, "karate", pname);
      //bestvp.setName("BestQvp");
      bestvp.recalculateCommunityLabels();
      bestvp.calcQuality();
      vertexPartitionGraphML(tg, bestvp);
      

      //       LineGraphCommunities input
//      lineGraphtg.outputName.setNameRoot(basicoutputroot+"LG");
      pname="Other";
      lineGraphtg.inputName.setNameRoot("karateNeATLG");
      //lineGraphtg.infoLevel=2;
      VertexPartition LGothervp = readVP(lineGraphtg,lineGraphtg.inputName.getNameRoot(), pname);
      //LGothervp.setName("Othervp");
      LGothervp.recalculateCommunityLabels();
      LGothervp.calcQuality();
      vertexPartitionGraphML(lineGraphtg,LGothervp);
      LGothervp.printInformation(System.out, " ", "  ");
      //LGothervp.printCommunities(System.out, " ", "  ");
      //lineGraphtg.infoLevel=0;
      
//      tg.outputName.setNameRoot(basicoutputroot);
      pname="Otherep";
      tg.inputName.setNameRoot("karateNeAT"+pname);
      VertexPartition otherep = readEP(tg,pname);
      //otherep.setName("Otherep");
      otherep.printInformation(System.out, " ", "  ");
      tg.setEdgeLabels(otherep);
      fo.fileName.setNameRoot(tg.outputName.getNameRoot());
      fo.printEdges(true,true,true,true,otherep.getName());
//tg.printEdges();
      
      
         
      //otherep.printCommunities(System.out, "", "  ");
      edgePartitionGraphML(tg,otherep);
      folg.fileName.setNameRoot(basicroot+"LG");
      folg.graphMLVertexPartition(otherep);
      
      
      
 

      //tg.setNameRoot("karateAK07BestQVP"+COMMUNITYMETHOD[method]+"EP");
      vertexEdgePartitionGraphML(tg,bestvp,louvainep);

      //tg.setNameRoot("karateActualVPOtherEP");
      //vertexEdgePartitionGraphML(tg,actualvp,otherep);


      //tg.setNameRoot("karateAK07BestQOther");
      vertexEdgePartitionGraphML(tg,bestvp,otherep);

    }
        
   /**
    * Tests Vertex partition of a graph read in from a file
    * <br>Reads from 
    * <tt>tg.inputName.getDirectoryFull()</tt>+<tt>nameRoot</tt>+<tt>vpName</tt>+<tt>inputVP.dat</tt>
    * @param tg graph.
    * @param nameRoot root of file name containing vertex partition
    * @param vpName file name containing vertex partition
    * @return Vertex parttion of original graph
    */
   public static VertexPartition readVP(timgraph tg, String nameRoot, String vpName){
      VertexPartition vp = new VertexPartition(tg);
      vp.initialise(tg);
      vp.setName(vpName);
      boolean relabelOn=true;
      vp.readIntPartition(tg.inputName.getDirectoryFull()+nameRoot+vpName+"inputVP.dat", 1, 2, true, relabelOn);
      //int [] cov = new int[lineGraphtg.getNumberVertices()];
      //for (int v=0; v<lineGraphtg.getNumberVertices();v++) cov[v]=VertexPartition.UNSET;
      //vp.relabelCommunities();
      vp.calcQuality();
      return vp;
   }
   
   /**
    * Reads edge partition of a graph read in from a file.
    * <br>Constructs line graph so it can calculate quality.
    * @param tg graph.
    * @param epname name for partition
    * @return Edge parttion of original graph
    */
   public static VertexPartition readEP(timgraph tg, String epname){
       boolean makeUnweighted=false;
       boolean useSelfLoops=false;
       //boolean makeUnlabelled, boolean reverseDirection
      //String lineGraphType = "LG"+(makeUnweighted?"uw":"w")+(useSelfLoops?"sl":"nsl");
      timgraph lgtg = new timgraph(tg,false,makeUnweighted,useSelfLoops,false,false);
      //lineGraphtg.setNameRoot(basicroot+lineGraphType);

      //timgraph lgtg = new timgraph(tg,false,false);
      VertexPartition ep = new VertexPartition(lgtg);
      ep.initialise(lgtg);
      ep.setName(epname);
      boolean relabelOn=true;
      ep.readIntPartition(tg.inputName.getNameRootFullPath()+"inputEP.dat", 1, 2, true, relabelOn);
      //int [] cov = new int[lineGraphtg.getNumberVertices()];
      //for (int v=0; v<lineGraphtg.getNumberVertices();v++) cov[v]=VertexPartition.UNSET;
      //ep.relabelCommunities();
      ep.calcQuality();
      return ep;
   }
   
    public static void graphML(timgraph tg){
        FileOutput fo = new FileOutput(tg);
        fo.graphML();
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
    
//     /**
//     * Calculates a partion using indicated method.
//     * @param method 1=Louvain, 2= Simulated Annealing
//     * @param qualityDefinition sets formula for modularity, 0=simple Newman
//     * @return Best VertexPartition found.
//     * @deprecated don't use this
//     */
//     public static VertexPartition VertexPartition.calculateOLD(int method, int qualityDefinition ){
//      VertexPartition c;
//      
//      switch (method) {
//          default:
//          case 0:
//                if (infoLevel>-1) System.out.println("Louvain VertexCommunity");
//                LouvainVertexPartition lc = new LouvainVertexPartition(tg,qualityDefinition,infoLevel);
//                lc.calculate();
//                c= (VertexPartition) lc;
//                break;
//            case 1:
//                if (infoLevel>-1) System.out.println("Simulated Annealing VertexCommunity");
//                SimulatedAnnealingvertexPartition sac = new SimulatedAnnealingvertexPartition(tg);
//                sac.calc(100, 0.5);
//                c= (VertexPartition) sac;
//                break;
//        }
//        
//      c.reVertexPartition.calculateLabels();
//      System.out.println("*** Best community:-");
//      c.printInformation(System.out, "", " ");
//      //c.printCommunities(System.out, "", " ");
//      return c;
//    }


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

    public static void testLineGraphity(){
        tg.printEdges();
        System.out.println("*** LineGraph Graph");
         boolean makeUnweighted=false;
       boolean useSelfLoops=false;
       //boolean makeUnlabelled, boolean reverseDirection
       String lineGraphType = "LG"+(makeUnweighted?"uw":"w")+(useSelfLoops?"sl":"nsl");
      timgraph ltg = new timgraph(tg,false,makeUnweighted,useSelfLoops,false,false);
      ltg.setNameRoot(basicroot+lineGraphType);
      //timgraph ltg = new timgraph(tg,false,false);
        ltg.printEdges();
    }
    

      public VertexPartition testLouvainCommunityTwoSteps(){
      VertexPartition c;
      int method = 1;
      switch (method) {
          default:
          case 1:
                LouvainVertexPartition lc = new LouvainVertexPartition(tg,0,1,2);
                lc.calculate();
                c= (VertexPartition) lc;
                break;
            case 2:
                SimulatedAnnealingvertexPartition sac = new SimulatedAnnealingvertexPartition(tg);
                sac.calc(10, 0.5);
                c= (VertexPartition) sac;
                break;
        }
      c.recalculateCommunityLabels();
      //c.printCommunities(System.out, " ", " , ");
      boolean ignoreNegativeLabels=true;
      c.relabelCommunities(ignoreNegativeLabels);
      //c.printCommunities(System.out, " ", " , ");
      
      System.out.println("*** now Projected Graph");
      timgraph projg = new timgraph(tg,c.getCommunity(), c.getNumberOfCommunities(), false, false);
      projg.printEdges(System.out);
      switch (method) {
          default:
          case 1:
                LouvainVertexPartition lc = new LouvainVertexPartition(projg,0,1,2);
                lc.calculate();
                c= (VertexPartition) lc;
                break;
            case 2:
                SimulatedAnnealingvertexPartition sac = new SimulatedAnnealingvertexPartition(projg);
                sac.calc(10, 0.5);
                c= (VertexPartition) sac;
                break;
        }
      c.relabelCommunities(ignoreNegativeLabels);
      //c.printCommunities(System.out, " ", " , ");
      return c;
    }

    public static void testLineGraphGraphMLOld(timgraph tg, VertexPartition c){
        GraphMLGenerator gml = new GraphMLGenerator();
        PrintStream PS;
        FileOutputStream fout;
        tg.outputName.setNameEnd(".graphml");
        if (tg.getInformationLevel()>-2) System.out.println("Writing LineGraphGraphML file to "+ tg.outputName.getFullFileName());
            try {
            fout = new FileOutputStream(tg.outputName.getFullFileName());
            PS = new PrintStream(fout);
            gml.outputEdgePartition(PS, tg, c);
            
            if (tg.getInformationLevel()>-2) System.out.println("Finished writing LineGraphGraphML file to "+ tg.outputName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +tg.outputName.getFullFileName());}
            
        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+tg.outputName.getFullFileName());
            return;
        }
        return;

        
    }
    
    
   public static void testGraphViz(timgraph tg, VertexPartition c){
        GraphViz gv = new GraphViz();
        PrintStream PS;
        FileOutputStream fout;
        tg.inputName.setNameEnd(".gv");
        if (tg.getInformationLevel()>-2) System.out.println("Writing GraphViz file to "+ tg.outputName.getFullFileName());
            try {
            fout = new FileOutputStream(tg.outputName.getFullFileName());
            PS = new PrintStream(fout);
            gv.outputVertexCommunity(PS, tg, c, true);

            if (tg.getInformationLevel()>-2) System.out.println("Finished writing GraphViz file to "+ tg.outputName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +tg.outputName.getFullFileName());}
            
        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+tg.outputName.getFullFileName());
            return;
        }
        return;

        
    }


}
