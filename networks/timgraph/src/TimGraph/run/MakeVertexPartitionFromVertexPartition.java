/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

//import TimGraph.Community.VertexCommunity;
import TimGraph.Community.VertexPartition;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.util.Map;

/**
 * Make a vertex partition from a file.
 * <p>Takes the original file defining the network, say an inputELS.dat file
 * and then takes the Vertex partition file, something with on each line
 * one vertex specified its vertex number of original file and its community label.
 * Other data can be on each line.
 * <p>Will also set positions and names from separate files if present.
 * <p>Knows about vertices.dat and inputBVNLS.dat files for the VP files and sets
 * parameters accordingly.
 * @author time
 */
public class MakeVertexPartitionFromVertexPartition {

    final static String SEP = "\t";
    static String basicroot="UNSET";

    static timgraph tg;
    static int infoLevel=3;

/**
 * Negative gamma means extract value from the epo string looking for the part between WLG_VP and .dat
 * @param args
 */
   public static void main(String[] args)
    {
      System.out.println("MakeVertexCommunityFromVertexPartition Arguments :<VPfilename> " +
              ":<VertexIndexColumn> :<PartitionIndexColumn> :<preGammaString> " +
              ":graphMLOn :edgeWeightMinimum :vertexPositionFile");

      int ano=0;
      String vpFilename="input/UKHEIiikpinvnslLouvainQS2000r0vertices.dat";
              //="input/karateTSELouvainQS1000inputBVNLS.dat";
           //"input/ICNS090729stemptWLG_VP1.dat";
           //"input/karateTSE_WLG_VP1.dat"; //"input/BowTie_WLG_VP.dat"
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) vpFilename=args[ano].substring(1, args[ano].length());
      System.out.println("--- Vertex partition from file "+vpFilename);

      ano++;
      int vertexIndexColumn =1;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) vertexIndexColumn=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Column with vertex index  "+vertexIndexColumn);

      ano++;
      int partitionIndexColumn =2;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) partitionIndexColumn=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Column with partition index  "+partitionIndexColumn);

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

      String preGammaString ="LouvainQS";
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
          int c0=vpFilename.lastIndexOf(preGammaString)+preGammaString.length();
          int c1=vpFilename.lastIndexOf(".dat");
          String gammaString=vpFilename.substring(c0,c1);
          gamma=Double.parseDouble(gammaString);
          System.out.println("--- Modularity null model scaling gamma ="+gamma);
      }
        else System.out.println("--- No modularity null model scaling gamma ");

//      String preGammaString ="LouvainQS";
//      double gamma = -1.0;
//      ano++;
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano]))  preGammaString=args[ano].substring(1, args[ano].length());
//          //gamma=Double.parseDouble(args[ano].substring(1, args[ano].length()));
//
//
//      if (preGammaString.startsWith("NONE")) {
//          gamma=0;
//          System.out.println("--- No Modularity null model scaling gamma");
//      }
//      else {
//      String gammaString=Double.toString(gamma);
//      if (gamma<0){
//          int c0=vpFilename.lastIndexOf(preGammaString)+preGammaString.length();
//          //int c1 = Math.min(c1dotdat,c1r);
//          int c1=c0;
//          while (Character.isDigit(vpFilename.charAt(++c1)));
//          gammaString=vpFilename.substring(c0,c1);
//          gamma=Double.parseDouble(gammaString);
//      }
//      System.out.println("--- Modularity null model scaling gamma ="+gamma);
//        }


      boolean graphMLOutput=true;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) graphMLOutput=StringFilter.trueString(args[ano].charAt(1));
      System.out.println("--- graphML output is "+StringFilter.onOffString(graphMLOutput));


      double edgeWeightMinimum =timgraph.DUNSET; // should be negative to ensure all are shown by default
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) edgeWeightMinimum=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      System.out.println("--- graphML output has minimum edge weight of  "+edgeWeightMinimum);


//      ano++;
//      String vPosFullFileName="input/UKHEIiikpinvnslLouvainQS1000r0inputXY.dat";
//              //="input/karateTSELouvainQS1000inputBVNLS.dat";
//           //"input/ICNS090729stemptWLG_VP1.dat";
//           //"input/karateTSE_WLG_VP1.dat"; //"input/BowTie_WLG_VP.dat"
//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) vPosFullFileName=args[ano].substring(1, args[ano].length());
//      System.out.println("--- Vertex positions from file "+vPosFullFileName);
//
//
//      //SetUpNetwork setnet = new SetUpNetwork(infoLevel);
//      //tg = SetUpNetwork.setUpNetwork(n, args);
//
//     // if no argument list use the following set up (i.e. for testing)
//      String basicFileNameRoot="UKHEIiikpinvnsl";
//      String [] aList = { "-fin"+basicFileNameRoot, "-fieinputELS.dat", "-o23", "-xi0"};
//
////     String basicFileNameRoot="karateTSE"; //BowTieW";
////     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
////     String basicFileNameRoot="BowTie"; //BowTieW";
////     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
////     String basicFileNameRoot="BowTieWWLG"; //BowTieW";
////     String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-gbt", "-fin"+basicFileNameRoot, "-fieinputEL.dat", "-gn99",  "-e0", "-o23", "-xi0"};
//     if (args.length>0) aList=args;
//
//     tg = new timgraph();
//     tg.parseParam(aList);
////     if (!tg.isVertexVertexListOn()) System.err.println("\n *** Vertex Vertex List NOT on and it is required, use -gvet option ***\n");
//     tg.setNameRoot(tg.inputName.getNameRoot());
//     tg.setNetworkFromManyInputFiles(); // this will read in the network
//     tg.printParametersBasic();
//     //tg.calcStrength();
//
//
//
//
//
//     // set vertex positions and names from file if present
//     boolean headerOn=false;
//     ArrayList<Double> xCoord = new ArrayList();
//     ArrayList<Double> yCoord = new ArrayList();
//     int xColumn=1;
//     int yColumn=2;
//     headerOn=false;
//     boolean infoOn=false;
//     try {
//     tg.setVertexPositionsFromFile(vPosFullFileName, "#", xColumn, yColumn,  headerOn, infoOn);
//     }
//     catch (Exception e){
//         System.out.println("Unable to set vertex coordinates from file"+vPosFullFileName);
//     }
//     int nameColumn =1;
//     try {
//     tg.setVertexNamesFromFile("inputNames.dat","#", nameColumn, headerOn, infoOn);
//     }
//     catch (Exception e){
//         System.out.println("Unable to set vertex names from file");
//     }

      String basicFileNameRoot="UKHEIiikpinvnsl";
      String [] aList = { "-fin"+basicFileNameRoot, "-fieinputELS.dat", "-o23", "-xi0"};
      if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
     tg.setNameRoot(tg.inputName.getNameRoot());
     System.out.println("Reading basic graph from file"+tg.inputName.getFullFileName());
     int xColumn=1;
     int yColumn=2;
     boolean headerOn=false;
     boolean infoOn=false;
     int nameColumn =1;
     tg.setNetworkFromManyInputFiles(xColumn, yColumn, nameColumn, headerOn, infoOn); // this will read in the network
     tg.printParametersBasic();


     boolean indexString=false;
     headerOn=false;
     boolean indexStringIsName=false;
     if (vpFilename.endsWith("inputBVNLS.dat")) {
         headerOn=false;
         indexString=true;
         vertexIndexColumn=1;
         partitionIndexColumn=2;
     }
     if (vpFilename.endsWith("vertices.dat")) {
         indexString=false;
         headerOn=true;
         vertexIndexColumn=1;
         partitionIndexColumn=2;
     }

     boolean vpInformationOn =false; // don't give more info on vertex partition
     makePartition(tg, vpFilename, vertexIndexColumn, partitionIndexColumn,
             edgeWeightMinimum,
             indexString, indexStringIsName,
             graphMLOutput, headerOn ,vpInformationOn );

   }
/**
 * Makes a vertex partition from an vertex - partition list.
 * <p>If vp files ends in inputBN
 * <p>Output is to a vcis file with
 * (tg input name root)VC(vertex partition name).vcis
 * @param tg graph whose vertex community is to be found.
 * @param vpFilename full file name of the vertex partitions
 * @param vertexIndexColumn column with the index of the vertex (if -1 uses (line number-1) )
 * @param vertexCommunityColumn column with the community label of the vertex
 * @param edgeWeightMinimum if positive produce second graphml file where only edges of this weight or larger displayed
 * @param indexString true (false) if the index column has vertex name or index (otherwise no vertex index given)
 * @param indexStringIsName true (false) if the index column has vertex name (integer index)
 * @param graphMLOutput true if want graphML output
 * @param headerOn true (false) if header row starts vp file.
 * @param vpInformationOn true (false) if want information on vp based on graph
 */
   static public void makePartition(timgraph tg, String vpFilename,
                                    int vertexIndexColumn, int vertexCommunityColumn,
                                    double edgeWeightMinimum,
                                    boolean indexString, boolean indexStringIsName, 
                                    boolean graphMLOutput,
                                    boolean headerOn, boolean vpInformationOn ){
     String basicFileNameRoot = tg.inputName.getNameRoot();


     VertexPartition vp = new VertexPartition(tg);
     String vpname="VP";
     int c=-1;
     try {
         //c = vpFilename.indexOf(basicFileNameRoot);
         c = vpFilename.lastIndexOf('\\');
         int c1=vpFilename.lastIndexOf('/');
         if (c1>c) c=c1;
         if (c<0) c=0;
//         vpname="VP"+vpFilename.substring(c+basicFileNameRoot.length(), vpFilename.length()-4);}
         vpname="VP"+vpFilename.substring(c+1, vpFilename.length()-4);}
     catch( RuntimeException e){vpname="VP"+vpFilename;}
     //System.out.println("EP called "+epname+"  "+c+"   "+basicFileNameRoot.length()+"  "+vpFilename.length());
     vp.setName(vpname);
     //vp.setElementNameVertices();

     boolean relabelOn=true;
     if (indexString) {
         Map<String,Integer> nameToIndex =null;
         if(indexStringIsName) {
             nameToIndex=tg.getVertexNameToIndexMap();
             if (nameToIndex==null) nameToIndex=tg.setVertexNameToIndexMap();
         }
         vp.readStringPartition(vpFilename, vertexIndexColumn, vertexCommunityColumn, headerOn, nameToIndex);
     }
     else vp.readIntPartition(vpFilename, vertexIndexColumn, vertexCommunityColumn, headerOn, relabelOn);
     int neg = vp.hasNegativeCommunityLabels();
     if (neg>=0) System.err.println("*** Found negative partition label for "+vp.getNameOfElements()+" number "+neg);

     boolean infoOn=true;
     //headerOn=true;
         //fovp.informationVertexPartition(ep, SEP, SEP, headerOn);

     if (vpInformationOn){
         FileOutput fovp = new FileOutput(tg.outputName);
         infoOn=true;
         headerOn=true;
         fovp.printPartitionStats(vp, SEP, SEP, infoOn, headerOn);
     }

     if (vp.getNumberElements()!=tg.getNumberVertices())
         throw new RuntimeException("vertex partition has "+vp.getNumberElements()
                 +" elements which is not equal to number of vertices "+tg.getNumberVertices());

     tg.setVertexNumbers(vp); // set vertex labels to match the vertex partition
     FileOutput fo = new FileOutput(tg);
     boolean simpleListOn=false;
     fo.printVertices("", SEP, infoOn, headerOn, vp, simpleListOn);
     //fo.printVertices("", SEP, null, false);
     // this next one gives .vcis or .vci files which have the real info on VC
     boolean asNames=true;
     headerOn=true;
     boolean splitBipartite=false;
     boolean outputType1=false;
     if (vpInformationOn){fo.printPartitionStats(vp, "", SEP, infoOn, headerOn);}
//     if (tg.isBipartite()){
//         splitBipartite=true;
//         outputType1=true;
//         fo.printVertexCommunityStats(tg.getNameVerticesType1()+vp.getName(),asNames,headerOn,  splitBipartite, outputType1);
//         outputType1=false;
//         fo.printVertexCommunityStats(tg.getNameVerticesType2()+vp.getName(),asNames,headerOn,  splitBipartite, outputType1);
//     }

     if (graphMLOutput) fo.graphMLVertexPartition(vp);
     if ((edgeWeightMinimum>0) && (graphMLOutput)) fo.graphML(vp.getCommunityName(c)+"B", edgeWeightMinimum);
     boolean listPartitionOn=false;
//     fo.informationVertexPartition(ep, "", SEP, listPartitionOn);

//     VertexCommunity vc = new VertexCommunity(tg,ep, qdef, qualityType, gamma);
//     vc.printCommunitityBipartiteGraph(null, SEP, SEP, headerOn, headerOn);



        }


}

