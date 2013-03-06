/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.Community.Community;
import TimGraph.Community.VertexCommunity;
import TimGraph.Community.VertexPartition;
import TimGraph.algorithms.Projections;
import TimGraph.io.FileInput;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Takes a clique graph and turns it into a vertex community.
 * <p>
 * @author time
 */
public class MakeVertexCommunityFromCliqueGraph {
    final static String SEP = "\t";
    static String basicroot="UNSET";

    static timgraph tg;
    static int infoLevel=0;

/**
 * Negative gamma means extract value from the epo string looking for the part between WLG_VP and .dat
 * <p>Clique graph types
     * <ul>
     * <li>0 Unweighted and no self loops (C of eqn 8)</li>
     * <li>1 Unweighted with self-loops (\tilde{C} of footnote 2)</li>
     * <li>2 Weighted and no self loops (D of eqn 11)</li>
     * <li>3 Weighted with self-loops (E of eqn 14)</li>
     * <li>4 Weighted and no self loops (incidence counts)</li>
     * <li>5 Weighted with self-loops (incidence counts)</li>
     *<ul>
 * <p>Arguments are
 * :RootName :CliqueGraphType :CliqueSize :gammaMin :gammaMax :gammaStep :GraphMLOn
     * @param args
 */
   public static void main(String[] args)
    {
      System.out.println("MakeVertexCommunityFromCliquePartition Arguments " +
              ":<RootName> :<CliqueGraphType> :<CliqueSize> " +
              ":<gammaMin> :<gammaMax> :<gammaStep> :<GraphMLOn>");

      int ano=-1;
//      String tgFilename="input/C3testinputEL.dat";
//      //String ccFilename="input/karateTSE_CCt2c3outputEL.dat";
//           //"input/ICNS090729stemptWLG_VP1.dat";
//           //"input/karateTSE_WLG_VP1.dat"; //"input/BowTie_WLG_VP.dat"
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) tgFilename=args[ano].substring(1, args[ano].length());
//      System.out.println("--- Original graph from file "+tgFilename);
//
//      ano;
      //String cgNameRoot="C3testCG";
      //String coreRoot="karateTSE";
      //String coreRoot="footballTSE";
      String coreRoot="N17";
      //String coreRoot="STCGv128m9c4p85";
      //String coreRoot="TGCGn1x6y6t216p0";
      //String coreRoot="TGCGn32x2y2t768p0";
      if (args.length>(++ano) ) if (timgraph.isOtherArgument(args[ano])) coreRoot=args[ano].substring(1, args[ano].length());
      System.out.println("Core root of file names is "+coreRoot);


      int cgTypeIndex=2;
      if (args.length>(++ano) ) if (timgraph.isOtherArgument(args[ano])) cgTypeIndex=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("Clique type "+cgTypeIndex+", "+cliqueGraphTypeString(cgTypeIndex));

      int cliqueSize=3;
      if (args.length>(++ano) ) if (timgraph.isOtherArgument(args[ano])) cliqueSize=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      
      String cgType="t"+cgTypeIndex+"c"+cliqueSize;
      System.out.println("Clique size "+cliqueSize+", cgtype ="+cgType);
      
//      boolean percOn=((cgTypeIndex==4)?true:false);
//
//      String cgType;
//      if (percOn) {
//                cgType="c"+cliqueSize+"perc";
//                System.out.println("Clique type percolation, size "+cliqueSize+", cgtype ="+cgType);
//      }
//      else {
//          cgType="t"+cgTypeIndex+"c"+cliqueSize;
//          System.out.println("Clique type "+cgTypeIndex+", "+cliqueGraphTypeString(cgTypeIndex));
//          System.out.println("Clique size "+cliqueSize+", cgtype ="+cgType);
//      }
      String nameRoot=coreRoot+"_"+cgType;
      String cgNameRoot=nameRoot+"CG";
      //String cgFilename="input/karateTSE_CGt2c3outputEL.dat";
           //"input/ICNS090729stemptWLG_VP1.dat";
           //"input/karateTSE_WLG_VP1.dat"; //"input/BowTie_WLG_VP.dat"
      //if (args.length>(++ano ) if (timgraph.isOtherArgument(args[ano])) cgNameRoot=args[ano].substring(1, args[ano].length());
      System.out.println("--- Clique Graph from file with name root "+cgNameRoot);

      
//      String cimFilename="input/C3testCIMoutputEL.dat";
        String cimFilename="input/"+nameRoot+"CIMinputEL.dat";
      //String ccFilename="input/karateTSE_CCt2c3outputEL.dat";
           //"input/ICNS090729stemptWLG_VP1.dat";
           //"input/karateTSE_WLG_VP1.dat"; //"input/BowTie_WLG_VP.dat"
      //if (args.length>(++ano) ) if (timgraph.isOtherArgument(args[ano])) cimFilename=args[ano].substring(1, args[ano].length());
      System.out.println("--- Clique Incidence Matrix from file "+cimFilename);

      boolean cimCliqueFirstColumn=false;
//      ano;
//      boolean cimCliqueFirstColumn=false;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) cgCliqueFirstColumn=StringFilter.trueString(args[ano].substring(1, args[ano].length()));
//      System.out.println("--- Clique Graph File "+(cgCliqueFirstColumn?"has clique number in first column":"no explicit clique number, just listed  in numerical order"));

//      ano;
//      boolean ccCliqueFirstColumn=false;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) ccCliqueFirstColumn=StringFilter.trueString(args[ano].substring(1, args[ano].length()));
//      System.out.println("--- Clique Community File "+(ccCliqueFirstColumn?"has clique number in first column":"no explicit clique number, just listed  in numerical order"));

//      ano;
//      int method =0;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) method=Integer.parseInt(args[ano].substring(1, args[ano].length()));
//      System.out.println("--- Used community method "+LineGraphCommunities.COMMUNITYMETHOD[method]);


      // 0="QS", 1="QA2mA"
      int qdef=0;
//      ano;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qdef=Integer.parseInt(args[ano].substring(1, args[ano].length()));
//      System.out.println("--- Quality definition used "+QualitySparse.QdefinitionString[qdef]);

      // qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory
      int qualityType=2;

      double gammaMin=0.25;
      double gammaMax=2.0;
      double gammaStep=0.25;
      if (args.length>(++ano) ) if (timgraph.isOtherArgument(args[ano])) gammaMin=Double.parseDouble(args[ano].substring(1, args[ano].length()));

      if (args.length>(++ano) ) if (timgraph.isOtherArgument(args[ano])) gammaMax=Double.parseDouble(args[ano].substring(1, args[ano].length()));


      double minStep = 1e-3;
      if (args.length>(++ano) ) if (timgraph.isOtherArgument(args[ano])) gammaStep=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      if (Math.abs(gammaStep)<minStep) {
          throw new RuntimeException("Step size must be greater than "+minStep+" in magnitude");
      }
      System.out.println("--- Modularity null model scaling gamma min, max, step = "+gammaMin+", "+gammaMax+", "+gammaStep);

      boolean graphMLOutput=true;
      if (args.length>(++ano) ) if (timgraph.isOtherArgument(args[ano])) graphMLOutput=StringFilter.trueString(args[ano].charAt(1));
      System.out.println("--- graphML output is "+StringFilter.onOffString(graphMLOutput));



     // now set up the original graph
     // if no argument list use the following set up (i.e. for testing)
     //String basicFileNameRoot="C3test"; //BowTieW";
     String basicFileNameRoot=coreRoot; //BowTieW";
     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbf", "-fin"+basicFileNameRoot, "-fieinputEL.dat", "-gn99",  "-e0", "-o255", "-xi0"};
//     String basicFileNameRoot="karateTSE"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     String basicFileNameRoot="BowTie"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     String basicFileNameRoot="BowTieWWLG"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputEL.dat", "-gn99",  "-e0", "-o23", "-xi0"};
     //if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
//     if (!tg.isVertexEdgeListOn()) System.err.println("\n *** Vertex Edge List NOT on and it is required, use -vgvet option ***\n");
     tg.setNameRoot(tg.inputName.getNameRoot());
     System.out.println("Reading basic graph from file"+tg.inputName.getFullFileName());
//     tg.setNetworkFromInputFile(); // this will read in the network
//     tg.printParametersBasic();
     //readIntIndexLabelList(String fullfilename, int columnIndex, int columnLabel,
     //       ArrayList<Integer> indexL, ArrayList<Integer> labelL, boolean headerOn, int infoLevel)
//     ArrayList<Double> xCoord = new ArrayList();
//     ArrayList<Double> yCoord = new ArrayList();
     int xColumn=1;
     int yColumn=2;
     boolean headerOn=false;
     boolean infoOn=false;
     int nameColumn =1;
     tg.setNetworkFromInputFile(xColumn, yColumn, nameColumn, headerOn, infoOn); // this will read in the network
     tg.printParametersBasic();


//     String vertexPositionsFullFileName=tg.inputName.getNameRootFullPath()+"inputXY.dat";
//     try {
//     tg.setVertexPositionsFromFile(vertexPositionsFullFileName,"#", xColumn, yColumn,  headerOn, infoOn);
//     }
//     catch (Exception e){
//         System.out.println("Unable to set vertex coordinates from file "+vertexPositionsFullFileName);
//     }
     
//     try {
//     tg.setVertexNamesFromFile("inputNames.dat","#", nameColumn, headerOn, infoOn);
//     }
//     catch (Exception e){
//         System.out.println("Unable to set vertex names from file");
//     }
     // make complete vertex community and then save it to a file
     String cgext="inputEL.dat";
     boolean cliquePercOn = true;
     VertexCommunity vc = makeCommunity(tg, cgType, cgNameRoot, cgext, cgTypeIndex,
             qdef, qualityType, gammaMin, gammaMax, gammaStep,
             cimFilename, cimCliqueFirstColumn, graphMLOutput, cliquePercOn );



     // Now clever edge colouring
     // need to labels edges/vertices 0 if multiple ownership. community+1 if none


   }
    /**
     * Makes Vertex Community from Clique graph and clique graph community.
     * <p>Clique graph types
     * <ul>
     * <li>0 Unweighted and no self loops (C of eqn 8)</li>
     * <li>1 Unweighted with self-loops (\tilde{C} of footnote 2)</li>
     * <li>2 Weighted and no self loops (D of eqn 11)</li>
     * <li>3 Weighted with self-loops (E of eqn 14)</li>
     * <li>4 Weighted and no self loops (incidence counts)</li>
     * <li>5 Weighted with self-loops (incidence counts)</li>
     *<ul>
     * @param tg input graph
     * @param cgType string indicating type of clique graph
     * @param cgNameRoot name root of clique graph file name, format: source target weight
     * @param cgext extension used for Clique graph file
     * @param cgTypeIndex index used to indicate tyep, 0 and 1 are unweighted clique graphs
     * @param qdef quality definition
     * @param qualityType type of quality routine to use
     * @param gammaMin minimum value for modularity null model scaling parameter, gamma
     * @param gammaMax maximum value for modularity null model scaling parameter, gamma
     * @param gammaStep step size to increase modularity null model scaling parameter, gamma
     * @param cimFilename clique vertex incidence file
     * @param cimCliqueFirstColumn true if want clique number as first column, otherwise line number is clique number
     * @param graphMLOutput true if want each partition ourput in graphml format
     * @param cliquePercOn true if want to do percolation, provided cgTypeIndex==4 too.
     */
    static public VertexCommunity  makeCommunity(timgraph tg, String cgType, 
            String cgNameRoot, String cgext, int cgTypeIndex,
            int qdef, int qualityType, double gammaMin, double gammaMax, double gammaStep,
            String cimFilename, boolean cimCliqueFirstColumn,
            boolean graphMLOutput, boolean cliquePercOn ){

        // read in clique - vertex incidence matrix first to get full number of cliques right
        boolean firstColumnCliqueIndex=false;
        ArrayList<Integer> cliqueLabel= null;
        ArrayList<ArrayList<Integer>> cliqueToVertex = new ArrayList();
        ArrayList<ArrayList<Integer>> vertexToClique = new ArrayList();
        boolean headerOn=false;
        //int infolevel=3;
        FileInput.readIntIndexNeighbourList(cimFilename, cliqueLabel,
            cliqueToVertex, vertexToClique, cimCliqueFirstColumn, headerOn, infoLevel);
        System.out.println("Clique to Vertex matrix, size "+cliqueToVertex.size());

        int numberCliques = cliqueToVertex.size();
        if (numberCliques<20){
            int cn=0;
            for (ArrayList<Integer> clique: cliqueToVertex){
                System.out.print(cn+++": ");
                for (Integer v: clique) System.out.print(v+" ");
                System.out.println();
            }

        }

        // read in clique graph and make its vertex partition
        timgraph cg = new timgraph();
        cg.inputName.setFileName(tg.inputName);
        cg.setNameRoot(cgNameRoot);
        cg.setVertexEdgeList(true);
        int columnSource=1;
        int columnTarget=2;
        int columnWeight=3;
        if ( (cgTypeIndex==0) || (cgTypeIndex==1)) columnWeight=-99;
        int columnLabel=-99;
        boolean directed=false;
        boolean vertexLabelled=false;
        boolean multiEdges=false;
        boolean infoOn = false;
        int maxVertexIndex = FileInput.processIntEdgeFile(cg, numberCliques, cgext,
            columnSource, columnTarget, columnWeight, columnLabel,
            directed, vertexLabelled, multiEdges, infoOn);
        if (maxVertexIndex<=0) throw new RuntimeException("FileInput.processIntEdgeFile returned maximum vertex index as "+maxVertexIndex);


        String cpName="";
        VertexCommunity vc = new VertexCommunity(tg);

        // set minimum edge size to be one less than clique size less a bit more
        int cliqueSize=cliqueToVertex.get(0).size();
        System.out.println("Clique size is "+cliqueSize);
        double minWeight = cliqueSize-1.1;

        FileOutput fo = new FileOutput(cg);
        PrintStream PS;
        FileOutputStream fout;
        String extension = "_"+Math.round(1000*gammaMin)+"_"+Math.round(1000*gammaMax)+"_"+Math.round(1000*gammaStep)+"_gamma.dat";
        fo.fileName.setNameEnd(extension);

        // Louvain VP of CG
        try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            Community c = new Community(tg, qdef, gammaMin, qualityType,infoLevel);
            PS.println("gamma"+SEP+c.informationNumbersLabel("", SEP));

            if (cliquePercOn  && cgTypeIndex==4) {
                vc=makeCliquePercolationCommunity(tg, cg, cgType, cliqueToVertex, minWeight, graphMLOutput);
                PS.println("PERC"+SEP+vc.getName()+SEP+vc.informationNumbers("", SEP));
                System.out.println("--- PERC, found "+vc.getNumberCommunities()+" communities");
            }

            for (double gamma=gammaMin; gamma<=gammaMax; gamma+=gammaStep){
                cpName="CP"+cgType+"l"+Math.round(gamma*1000);
                vc.setName(cpName);
                vc=makeLouvainCliqueCommunity(tg, cg, cgType, cliqueToVertex,
                qdef, qualityType,gamma, graphMLOutput);
                System.out.println("--- gamma="+gamma+", found "+vc.getNumberCommunities()+" communities");
                PS.println(gamma+SEP+vc.getName()+SEP+vc.informationNumbers("", SEP));
            } // eo gamma
            if (infoLevel>-2) System.out.println("Finished writing VP various gamma information file to "+ fo.fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fo.fileName.getFullFileName()+", "+e.getMessage());
            return null;
        }

        return vc;
    }




    /**
     * Makes vertex community from a Louvain parition of clique graph.
     * <p>Also sets the vertex and edge labels as follows.
     * Labels of vertices/edges are -1 if not in any community,
     * 0 if in more than one community and
     * (community label +1) if in a single community.
     * @param tg original graph
     * @param cg its cvlique graph
     * @param cgType type of clique graph
     * @param cliqueToVertex list of tg vertices associated with each clique
     * @param qdef quality definition to use
     * @param qualityType quality type to use
     * @param gamma value of gamma
     * @param graphMLOn true if want graphML output
     * @return vertex community
     */
    static VertexCommunity makeLouvainCliqueCommunity(timgraph tg, timgraph cg, String cgType,
            ArrayList<ArrayList<Integer>> cliqueToVertex,
            int qdef, int qualityType,
            double gamma, boolean  graphMLOn){
        VertexCommunity vc = new VertexCommunity(tg);
        int numberCliques = cg.getNumberVertices();
            if (numberCliques != cliqueToVertex.size()) {
                throw new RuntimeException("Number of cliques not consistent "+cg.getNumberVertices()+" != "+cliqueToVertex.size());
            }
       int method=0;

        VertexPartition cgvp = VertexPartition.calculate(cg, qdef, qualityType, gamma,  method, infoLevel);
        if (cg.getNumberVertices()<20){
         //cgvp.printStatistics(System.out, " ", SEP, true, true);
         cgvp.printVertices(System.out, " ", SEP, true, true);
        }
        int [] cliqueToCommunity = new int[numberCliques];
        for (int m=0; m<numberCliques ; m++) {
            cliqueToCommunity[m]=cgvp.getCommunity(m);
        }

        String cpName="CP"+cgType+"l"+Math.round(gamma*1000);
        vc.setName(cpName);


        // make original graph vertex community

        boolean forceNormalisation=true;
        boolean checkNormalisation=true;
        vc.setUp(tg.getNumberVertices(),  cliqueToVertex,
            cliqueToCommunity,
            forceNormalisation, checkNormalisation );

        printCliqueCommunity(tg, cpName, cliqueToVertex, cliqueToCommunity, vc, graphMLOn);

        // set up edge and vertex labels
        setEdgeLabelsFromCliqueCommunity(tg, cliqueToVertex,cliqueToCommunity);
        setVertexNumbersFromCliqueCommunity(tg, cliqueToVertex,cliqueToCommunity);
        if (graphMLOn){
            FileOutput tgfo = new FileOutput(tg);
            double edgeWeightMinimum = timgraph.DUNSET;
            tgfo.graphML(cpName, edgeWeightMinimum);
        }



        return vc;
    }

    /**
     * Sets the edge labels using a clqiue community.
     * <p>If needed the edgeWeights of the graph are switched on.
     * Labels of edges are -1 if not in any community,
     * 0 if in more than one community and
     * (community label +1) if in a single community
     * @param tg graph
     * @param cliqueToVertex a list of lists giving the vertices in each clique
     * @param cliqueToCommunity an array gigin the community of each clique
     */
    static void setEdgeLabelsFromCliqueCommunity(timgraph tg,
            ArrayList<ArrayList<Integer>> cliqueToVertex,
            int [] cliqueToCommunity
            ){
        if (cliqueToCommunity.length!=cliqueToVertex.size()) throw new RuntimeException("Different numbers of communities in cliqueToCommunity and cliqueToVertex.size() "+cliqueToCommunity.length+", "+cliqueToVertex.size());
        tg.setEdgeLabels(-1);
        int s=-1;
        int t=-1;
        int e=-1;
        int l=-1;
        for (int cl=0; cl<cliqueToCommunity.length; cl++){
            int commp1 = cliqueToCommunity[cl]+1;
            ArrayList<Integer> vl = cliqueToVertex.get(cl);
            for (int i=0; i<vl.size(); i++){
                s=vl.get(i);
                for (int j=i+1; j<vl.size(); j++){
                    t=vl.get(j);
                    e=tg.getFirstEdgeGlobal(s, t);
                    if (e<0) throw new RuntimeException("Looking at vertices "+s+" and "+t+", the "+i+" and "+j+"  vertices of clique "+cl);
                    l=tg.getEdgeLabel(e);
                    if (l<0) tg.setEdgeLabelQuick(e, commp1);
                    else if (l!=commp1) tg.setEdgeLabelQuick(e, 0);

                }
            }
        }

    }
    /**
     * Sets the vertex numbers using a clique community.
     * <p>If needed the vertex labels of the graph are switched on.
     * Labels of vertices are -1 if not in any community,
     * 0 if in more than one community and
     * (community label +1) if in a single community
     * @param tg graph
     * @param cliqueToVertex a list of lists giving the vertices in each clique
     * @param cliqueToCommunity an array gigin the community of each clique
     */
    static void setVertexNumbersFromCliqueCommunity(timgraph tg,
            ArrayList<ArrayList<Integer>> cliqueToVertex,
            int [] cliqueToCommunity
            ){
        if (cliqueToCommunity.length!=cliqueToVertex.size()) throw new RuntimeException("Different numbers of communities in cliqueToCommunity and cliqueToVertex.size() "+cliqueToCommunity.length+", "+cliqueToVertex.size());
        tg.setVertexNumbers(-1);
        int s=-1;
        int n=-1;
        for (int cl=0; cl<cliqueToCommunity.length; cl++){
            int commp1 = cliqueToCommunity[cl]+1;
            ArrayList<Integer> vl = cliqueToVertex.get(cl);
            for (int i=0; i<vl.size(); i++){
                s=vl.get(i);
                n=tg.getVertexNumber(s, -1);
                if (n<0) tg.setVertexNumberQuick(s, commp1);
                else if (n!=commp1) tg.setVertexNumberQuick(s, 0);
                }
            }
        }



    /**
     * Makes vertex community from a Louvain parition of clique graph
     * <p>Also sets the vertex and edge labels as follows.
     * Labels of vertices/edges are -1 if not in any community,
     * 0 if in more than one community and
     * (community label +1) if in a single community.
     * @param tg original graph
     * @param cg its clique graph
     * @param cgType type of clique graph
     * @param cliqueToVertex list of tg vertices associated with each clique
     * @param minWeight minumum weight of edges kept
     * @param graphMLOn true if want graphML output
     * @return vertex community based on clqiue percolation
     */
     static VertexCommunity makeCliquePercolationCommunity(timgraph tg, timgraph cg, String cgType,
            ArrayList<ArrayList<Integer>> cliqueToVertex, double minWeight, boolean graphMLOn){

        VertexCommunity vc = new VertexCommunity(tg);
        int numberCliques = cg.getNumberVertices();
            if (numberCliques != cliqueToVertex.size()) {
                throw new RuntimeException("Number of cliques not consistent "+cg.getNumberVertices()+" != "+cliqueToVertex.size());
            }

        boolean makeLabelled=false;
        boolean makeWeighted=false;
        boolean makeVertexEdgeList=false;
        timgraph newcg=Projections.minimumEdgeWeight(cg, minWeight,  makeLabelled, makeWeighted, makeVertexEdgeList);
        newcg.calcComponents();
        boolean componentDistribution=true;
        boolean vertexToComponent=true;
        //newcg.printComponentInfo(System.out, componentDistribution, vertexToComponent);

        int [] cliqueToCommunity = new int[numberCliques];
        for (int m=0; m<numberCliques ; m++) cliqueToCommunity[m]=newcg.getVertexComponentLabel(m);

        String cpName="CP"+cgType+"Perc";
        vc.setName(cpName);

                // make original graph vertex community

        boolean forceNormalisation=true;
        boolean checkNormalisation=true;
        vc.setUp(tg.getNumberVertices(),  cliqueToVertex,
            cliqueToCommunity,
            forceNormalisation, checkNormalisation );

        printCliqueCommunity(tg,cpName, cliqueToVertex, cliqueToCommunity, vc, graphMLOn);
        
        // set up edge and vertex labels
        setEdgeLabelsFromCliqueCommunity(tg, cliqueToVertex,cliqueToCommunity);
        setVertexNumbersFromCliqueCommunity(tg, cliqueToVertex,cliqueToCommunity);
        if (graphMLOn){FileOutput tgfo = new FileOutput(tg);
        double edgeWeightMinimum = timgraph.DUNSET;
        tgfo.graphML(cpName, edgeWeightMinimum);
        }


        return vc;

     }

     /**
      * Prints information of cliques and their communities.
      * @param cpName name of the clique
      * @param cliqueToVertex list of vertices associated with each clique
      * @param cliqueToCommunity liats of community labels of each clique
      * @param vc vertex community of original graph derived from clique community
      * @param graphMLOn true if want graphml output
      */
     static void  printCliqueCommunity(timgraph tg, String cpName, ArrayList<ArrayList<Integer>> cliqueToVertex,
            int [] cliqueToCommunity, VertexCommunity vc, boolean graphMLOn){
        // output list of cliques with their index and community label
        String cc="";
        String sep=SEP;
        String ext="_"+cpName+"CliqueList.dat";
        boolean headerOn=false;
        FileOutput tgfo = new FileOutput(tg);
        boolean motifIndexOn=true;
        headerOn=true;
        tgfo.printMotifs(cc, sep, ext, cliqueToCommunity, cliqueToVertex,
            motifIndexOn, headerOn);


        cc="";
        sep=SEP;
        headerOn=true;
        boolean ElementLabelsOn=true;
        tgfo.printVertexCommunity(vc, cc, sep, headerOn, ElementLabelsOn);


    }
    /**
     * Clique Graph Types 
     * @param type index from 0 to 5
     * @return string desrbing type of clique graph
     */
    static public String cliqueGraphTypeString(int type) {
        switch (type) {
            case 0:
                return ("Unweighted and no self loops (C of eqn 8)");
            case 1:
                return ("Unweighted with self-loops (\\tilde{C} of footnote 2)");
            case 2:
                return ("Low Weighted and no self loops (D of eqn 11)");
            case 3:
                return ("Low Weighted with self-loops (E of eqn 14)");
            case 4:
                return ("Weighted and no self loops (incidence counts)");
            case 5:
                return ("Weighted with self-loops (incidence counts)");
        }
        return ("*** UNKNOWN Clique Graph Type");
    }
}
