/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.Community.Partition;
import TimGraph.Community.VertexPartition;
//import TimGraph.timgraph;
import DataAnalysis.MutualInformation;
import TimGraph.Community.Community;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.util.TreeMap;

/**
 * Comparing vertex communities.
 * @author time
 */
public class CompareVertexPartitions {

        final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static int infoLevel=0;
    
    
    public CompareVertexPartitions(){}
    

    /**
     * Basic routine to compare two given vertex partition structures.
     * <p>File names of the two files are the only arguments.
     * <p>If file ends in <tt>BVNLS.dat</tt> treats it as a vertex neighbour list.
     * Here each line has a vertex followed by the communities it is in 
     * (separated by white space). It is assumed that each community has equal weight.
     * <p>If file ends in <tt>vcis</tt> it is treated as a list of vertex properties.
     * The first column is the one with the vertex label, the second is its community and the eighth
     * gives the fractional membership of that community.
     * @param args the command line arguments
     * @see TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT
     * @see TimGraph.Community.VertexPartition
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
      String indexLabel1="Index";
      String communityLabel1="Community";
      String indexLabel2=indexLabel1;
      String communityLabel2=communityLabel1;
      MutualInformation mi = doComparision(fileName1, indexLabel1, communityLabel1,
                                           fileName2, indexLabel2, communityLabel2);

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
    public static MutualInformation doComparision(String fileName1,
            String indexLabel1, String communityLabel1, 
            String fileName2, String indexLabel2, String communityLabel2){
      // vertex names set by first community file
      TreeMap<String,Integer>vertexNameToIndex;
      vertexNameToIndex=null;
      boolean forceNormalisation=true;
      boolean checkNormalisation=true;
      String cc="";
      VertexPartition vp1 = new VertexPartition();
      String name1 = StringFilter.beforeLastString(StringFilter.afterLastString(fileName1,"/"),".");
      vp1.setName(name1);
//      vp1.readIntPartition(fileName1, infoLevel, infoLevel, checkNormalisation, forceNormalisation).readVertexPartition(fileName1, vertexNameToIndex,
//             forceNormalisation, checkNormalisation);

      int [] columnReadList = null; // will be set by routine
      String [] columnLabelList1={indexLabel1,communityLabel1};
      boolean forceLowerCase=false;
      boolean infoOn=(infoLevel>1);

      vp1.readStringPartition(fileName1, 
           cc, columnReadList, columnLabelList1, 
           forceLowerCase, infoOn);

      if (vp1.getNumberElements()<20) vp1.printCommunityMatrix(System.out, "", " : ");

      VertexPartition vp2= new VertexPartition();
      String name2 = StringFilter.beforeLastString(StringFilter.afterLastString(fileName2,"/"),".");
      vp2.setName(name2);
      String [] columnLabelList2 =  {indexLabel2,communityLabel2};
      vp2.readStringPartition(fileName2,
           cc, columnReadList, columnLabelList2,
           forceLowerCase, infoOn);

      if (vp2.getNumberElements()<20) vp2.printCommunityMatrix(System.out, "", " : ");

      return Community.doComparision(vp1 ,vp2);
    }

      
//    /**
//     * Compares two communities.
//     * @param vp1 first community
//     * @param vp2 second community
//     */
////    static public MutualInformation doComparision(VertexPartition vp1,VertexPartition vp2){
//    static public MutualInformation doComparision(Community vp1,Community vp2){
//
//      MutualInformation mi = Partition.calcMutualInformation(vp1, vp2, null);
//      if (mi==null){throw new RuntimeException("Partition.calcMutualInformation failed to produce anything");}
//      int r=mi.checkNormalisations();
//      if (r>0) throw new RuntimeException("Normalisation failure, "+mi.checkNormalisationsString(r));
//      else System.out.println(" --- "+mi.checkNormalisationsString(r));
//      r=mi.checkConsistentcy();
//      if (r<0) throw new RuntimeException("Consistency Failure");
//      else System.out.println(" --- Consistent probabilties");
//      return mi;
//    }

    

    
}
