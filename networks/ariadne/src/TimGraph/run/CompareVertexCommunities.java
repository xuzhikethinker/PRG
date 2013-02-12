/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.Community.Community;
import TimGraph.Community.VertexCommunity;
//import TimGraph.timgraph;
import DataAnalysis.MutualInformation;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.util.TreeMap;

/**
 * Comparing vertex communities.
 * @author time
 */
public class CompareVertexCommunities {

        final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static int infoLevel=0;
    
    
    public CompareVertexCommunities(){}
    

    /**
     * Basic routine to compare two given community structures.
     * <p>File names of the two files are the only arguments.
     * <p>If file ends in <tt>BVNLS.dat</tt> treats it as a vertex neighbour list.
     * Here each line has a vertex followed by the communities it is in 
     * (separated by white space). It is assumed that each community has equal weight.
     * <p>If file ends in <tt>vcis</tt> it is treated as a list of vertex properties.
     * The first column is the one with the vertex label, the second is its community and the eighth
     * gives the fractional membership of that community.
     * @param args the command line arguments
     * @see TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT
     * @see TimGraph.Community.VertexCommunity
     */
    public static void main(String[] args) 
    { 
      System.out.println("CompareVertexCommunities Arguments: fileName1 fileName2 outputNameRoot");
      
      //First arg chooses first community file
      //String fileName1="input/ICtest_psecinputBVNLS.dat";
      //String fileName1="input/ICNS090224npstemppew0020inputELS.dat";
      //String fileName1="output/karateTSE_VC_WLG_VP0.3output.vcis";
      //String fileName1="output/Karate/unweighted/karateTSELouvainQS1000inputBVNLS.dat";
      String fileName1="input/TestVertexPartition1inputBVNLS.dat";
      //String fileName1="input/ICNS090729_psecinputBVNLS.dat";
      int ano=0;
      if (args.length>ano ) fileName1=args[ano];
      System.out.println("--- Using network one "+fileName1);


      //Second arg chooses second community file
      //String fileName2="input/ICtest_psecinputBVNLS.dat";
      //String filename2="input/ICtest_psecinputBVNLS.dat";
      //String fileName2="input/karateTSEActualVPinputBVNLS.dat";
      //String fileName2="output/IC/PaperTerm/ICNS090729stempt_PapersVCWLG_VP1.5output.vcis";
      //String fileName2="output/Karate/unweighted/karateTSELouvainQS1000inputBVNLS.dat";
      String fileName2="input/TestVertexPartition2inputBVNLS.dat";
      ano++;
      if (args.length>ano ) fileName2=args[ano];
      System.out.println("--- Using network two "+fileName2);

      //Third arg is output file name
      //String fileName2="input/ICtest_psecinputBVNLS.dat";
      //String filename2="input/ICtest_psecinputBVNLS.dat";
      //String rootName3="output/karateTSE";
      //String rootName3="output/testmvc";
      String rootName3="output/TestVP_1_2";
      ano++;
      if (args.length>ano ) rootName3=args[ano];
      System.out.println("--- Output to  "+rootName3);

      MutualInformation mi = doComparision(fileName1,fileName2);

      System.out.println(MutualInformation.toStringLabelDescriptive(" : "));
      if ((mi.getOrderOne()<10) && (mi.getOrderTwo()<10)){
          System.out.println(MutualInformation.toStringLabel(" : "));
          System.out.println(mi.toString(" : "));
      }

      boolean statsOn=true;
      boolean jpOn=true;
      boolean entryLabelsOn=true;
      boolean numbersOn=true;
      boolean processInfoOn=true;
      mi.printToFile(rootName3, "", SEP, statsOn, jpOn, entryLabelsOn, numbersOn, processInfoOn);
      mi.printToFile(rootName3, "", SEP, statsOn, !jpOn, entryLabelsOn, numbersOn, processInfoOn);
      mi.printSummary(System.out, "", SEP, statsOn);


    }
    
    /**
     * Compares two vertex communities
     * @param fileName1 full file name of first file, including directories
     * @param fileName2 full file name of second file, including directories
     */
    public static MutualInformation doComparision(String fileName1,String fileName2){
      // vertex names set by first community file
      TreeMap<String,Integer>vertexNameToIndex;
      vertexNameToIndex=null;
      boolean forceNormalisation=true;
      boolean checkNormalisation=true;
      VertexCommunity vc1 = new VertexCommunity(fileName1);
      String name1 = StringFilter.beforeLastString(StringFilter.afterLastString(fileName1,"/"),".");
      vc1.setName(name1);
      vc1.readVertexCommunity(fileName1, vertexNameToIndex,
             forceNormalisation, checkNormalisation);
      if (vc1.getNumberElements()<20) vc1.printCommunityMatrix(System.out, "", " : ");

      VertexCommunity vc2= new VertexCommunity(fileName2);
      String name2 = StringFilter.beforeLastString(StringFilter.afterLastString(fileName2,"/"),".");
      vc2.setName(name2);
      vc2.readVertexCommunity(fileName2, vertexNameToIndex,
             forceNormalisation, checkNormalisation);
      if (vc2.getNumberElements()<20) vc2.printCommunityMatrix(System.out, "", " : ");

      return doComparision(vc1 ,vc2);
    }
      
    /**
     * Compares two vertex communities.
     * @param vc1 first vertex community
     * @param vc2 second vertex community
     */
    static  MutualInformation doComparision(VertexCommunity vc1 ,VertexCommunity vc2){

      MutualInformation mi = Community.calcMutualInformation(vc1, vc2, null);
      if (mi==null){throw new RuntimeException("Community.calcMutualInformation failed to produce anything");}
      int r=mi.checkNormalisations();
      if (r>0) throw new RuntimeException("Normalisation failure, "+mi.checkNormalisationsString(r));
      else System.out.println(" --- "+mi.checkNormalisationsString(r));
      r=mi.checkConsistentcy();
      if (r<0) throw new RuntimeException("Consistency Failure");
      else System.out.println(" --- Consistent probabilties");
      
      return mi;
    }

    
//        /**
//     * Process network files.
//     * <p>Produces timgraph.
//     * @param n network number
//     * @param outputMode set output modes
//     * @param basicAnalysis true if want analysis of iunput file
//     * @param args additional arguments
//     */
//    static public timgraph makeVertexPartition(int n, int outputMode, boolean basicAnalysis, String [] args)
//    {
//      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
//      timgraph tg = SetUpNetwork.setUpNetwork(n, args);
//      if (outputMode>=0)  tg.outputControl.set(outputMode);
//      if (tg.getNumberVertices()<40) tg.printNetwork(true);
//      if (basicAnalysis) BasicAnalysis.analyse(tg);
//      return tg;
//    }
//
//        /**
//     * Process network files.
//     * <p>Produces timgraph.
//     * @param n network number
//     * @param outputMode set output modes
//     * @param basicAnalysis true if want analysis of input file
//     * @param args additional arguments
//     */
//    static public timgraph processNetworkFile(int n, int outputMode, boolean basicAnalysis, String [] args)
//    {
//      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
//      timgraph tg = SetUpNetwork.setUpNetwork(n, args);
//      if (outputMode>=0)  tg.outputControl.set(outputMode);
//      if (tg.getNumberVertices()<40) tg.printNetwork(true);
//      if (basicAnalysis) BasicAnalysis.analyse(tg);
//      return tg;
//    }

    
}
