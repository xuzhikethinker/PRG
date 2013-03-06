/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

//import TimGraph.Community.VertexCommunity;
import TimGraph.Community.Partition;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.StringFilter;

/**
 * Make a vertex community from an edge partition.
 * Takes the original file defining the network, say an inputELS.dat file
 * and then takes the Edge partition file, something with on each line
 * one edge specified its edge number of original file and its community label.
 * Other data can be on each line.
 * @author time
 */
public class MakeVertexCommunityFromEdgePartition {

    final static String SEP = "\t";
    static String basicroot="UNSET";

    static timgraph tg;
    static int infoLevel=3;

/**
 * Negative gamma means extract value from the epo string looking for the part between WLG_VP and .dat
 * <p>Ignores any line starting with a * in the file.
 * @param args
 */
   public static void main(String[] args)
    {
      System.out.println("MakeVertexCommunityFromEdgePartition Arguments :<EPfilename> :<edgeIndexColumn> :<edgeCommunityColumn> :<preGammaString>");

      int ano=0;
      String epFilename="input/karateTSE_WLG_VP0.9.dat";
           //"input/ICNS090729stemptWLG_VP1.dat";
           //"input/karateTSE_WLG_VP1.dat"; //"input/BowTie_WLG_VP.dat"
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) epFilename=args[ano].substring(1, args[ano].length());
      System.out.println("--- Edge partition from file "+epFilename);

      ano++;
      int edgeIndexColumn =1;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) edgeIndexColumn=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Column with edge index  "+edgeIndexColumn);

      ano++;
      int edgeCommunityColumn =2;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) edgeCommunityColumn=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Column with edge community  "+edgeCommunityColumn);

//      ano++;
//      int method =0;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) method=Integer.parseInt(args[ano].substring(1, args[ano].length()));
//      System.out.println("--- Used community method "+LineGraphCommunities.COMMUNITYMETHOD[method]);


      // 0="QS", 1="QA2mA"
      int qdef=0;
//      ano++;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qdef=Integer.parseInt(args[ano].substring(1, args[ano].length()));
//      System.out.println("--- Quality definition used "+QualitySparse.QdefinitionString[qdef]);

      // qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory
      int qualityType=2;
//      ano++;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qualityType=Integer.parseInt(args[ano].substring(1, args[ano].length()));
//      System.out.println("--- Quality class type "+QualityType.qualityLongName[qualityType]);


      String preGammaString ="WLG_VP";
      double gamma = -1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano]))  {
          try {
              gamma=Double.parseDouble(args[ano].substring(1, args[ano].length()));
          }
          catch(RuntimeException e){
              preGammaString=args[ano].substring(1, args[ano].length());
          }
      }
          

      if (gamma<0){
          int c0=epFilename.lastIndexOf(preGammaString)+preGammaString.length();
          int c1=epFilename.lastIndexOf(".dat");
          String gammaString=epFilename.substring(c0,c1);
          gamma=Double.parseDouble(gammaString);
      }
      System.out.println("--- Modularity null model scaling gamma ="+gamma);


      boolean graphMLOutput=true;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) graphMLOutput=StringFilter.trueString(args[ano].charAt(1));
      System.out.println("--- graphML output is "+StringFilter.onOffString(graphMLOutput));




      //SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      //tg = SetUpNetwork.setUpNetwork(n, args);

     // if no argument list use the following set up (i.e. for testing)
     String basicFileNameRoot="karateTSE"; //BowTieW";
     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     String basicFileNameRoot="BowTie"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     String basicFileNameRoot="BowTieWWLG"; //BowTieW";
//     String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputEL.dat", "-gn99",  "-e0", "-o23", "-xi0"};
     if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
//     if (!tg.isVertexEdgeListOn()) System.err.println("\n *** Vertex Edge List NOT on and it is required, use -vgvet option ***\n");
     tg.setNameRoot(tg.inputName.getNameRoot());
//     tg.setNetworkFromInputFile(); // this will read in the network
     int xColumn=1;
     int yColumn=2;
     boolean headerOn=false;
     boolean infoOn=false;
     int nameColumn =1;
     tg.setNetworkFromInputFile(xColumn, yColumn, nameColumn, headerOn, infoOn); // this will read in the network
     tg.printParametersBasic();


     tg.printParametersBasic();
     //tg.calcStrength();

     makeCommunity(tg, epFilename, edgeIndexColumn, edgeCommunityColumn, graphMLOutput );

   }
/**
 * Makes a vertex community from an edge partition.
 * <p>Output is to a vcis file with
 * (tg input name root)VC(edge partition name).vcis
 * <p>Ignores any line starting with a * in the file.
 * @param tg graph whose vertex community is to be found.
 * @param epFilename full file name of the edge partitions
 * @param edgeIndexColumn column with the index of the edge
 * @param edgeCommunityColumn column with the community of the edge
 * @param graphMLOutput true if want graphML output
 */
   static public void makeCommunity(timgraph tg, String epFilename,
                                    int edgeIndexColumn, int edgeCommunityColumn,
                                    boolean graphMLOutput ){
     String basicFileNameRoot = tg.inputName.getNameRoot();


     Partition ep = new Partition();
     String epname="VC";
     int c=-1;
     try {
         c = epFilename.indexOf(basicFileNameRoot);
         epname="VC"+epFilename.substring(c+basicFileNameRoot.length(), epFilename.length()-4);}
     catch( RuntimeException e){epname="VC"+epFilename;}
     //System.out.println("EP called "+epname+"  "+c+"   "+basicFileNameRoot.length()+"  "+epFilename.length());
     ep.setName(epname);
     ep.setElementNameEdges();
     // the next few lines should handle pajek .clu files
     boolean headerOn=false;
     boolean relabelOn=true;
     String commentLine="*";
     ep.readIntPartition(epFilename, edgeIndexColumn, edgeCommunityColumn, headerOn, commentLine, relabelOn);
     int neg = ep.hasNegativeCommunityLabels();
     if (neg>=0) System.err.println("*** Found negative community label for "+ep.getNameOfElements()+" number "+neg);
     FileOutput foep = new FileOutput(tg.outputName);
     boolean infoOn=true;
     headerOn=true;
     foep.informationEdgePartition(ep, SEP, SEP, headerOn);
     foep.printPartitionStats(ep, SEP, SEP, infoOn, headerOn);

     if (ep.getNumberElements()!=tg.getNumberEdges())
         throw new RuntimeException("edge partition has "+ep.getNumberElements()
                 +" elements which is not equal to number of edges "+tg.getNumberEdges());

     tg.setEdgeLabels(ep); // set edge labels to match the edge partition
     FileOutput fo = new FileOutput(tg);
     fo.printEdges(true,true, true, true, ep.getName());
     //fo.printVertices("", SEP, null, false);
     // this next one gives .vcis or .vci files which have the real info on VC
     boolean asNames=true;
     headerOn=true;
     boolean splitBipartite=false;
     boolean outputType1=false;
     fo.printEdgeCommunityStats(ep.getName(),asNames,headerOn,  splitBipartite, outputType1);
     if (tg.isBipartite()){
         splitBipartite=true;
         outputType1=true;
         fo.printEdgeCommunityStats(tg.getNameVerticesType1()+ep.getName(),asNames,headerOn,  splitBipartite, outputType1);
         outputType1=false;
         fo.printEdgeCommunityStats(tg.getNameVerticesType2()+ep.getName(),asNames,headerOn,  splitBipartite, outputType1);
     }

     if (graphMLOutput) fo.graphMLEdgePartition(ep);
     boolean listPartitionOn=false;
     fo.informationEdgePartition(ep, "", SEP, listPartitionOn);

//     VertexCommunity vc = new VertexCommunity(tg,ep, qdef, qualityType, gamma);
//     vc.printCommunitityBipartiteGraph(null, SEP, SEP, headerOn, headerOn);



        }
}
