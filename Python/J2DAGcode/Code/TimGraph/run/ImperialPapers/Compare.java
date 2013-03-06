/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run.ImperialPapers;

import TimGraph.Community.Community;
import TimGraph.Community.VertexCommunity;
import TimGraph.Community.VertexPartition;
import TimGraph.OutputMode;
import TimGraph.run.BasicAnalysis;
import TimGraph.run.SetUpNetwork;
import TimGraph.run.VertexCommunities;
import TimGraph.timgraph;
import DataAnalysis.MutualInformation;
import java.util.TreeMap;

/**
 * Comparing the communities derived or given for Imperial data set.
 * @author time
 */
public class Compare {

        final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static int infoLevel=0;
    
    
    public Compare(){}
    

    /**
     * Basic routine to compare a graph and a given community structure.
     * <p>If first argument starts with a '*','^' or ':'
     * (specified by <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>
     * {@link TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT})
     * the rest of the argument is the network number for the setup routine
     * If arguments exits but the first has no ':' then network is set up from file read in using the arguments
     * to point it to the right file.
     * <p>Second :' argument number of community structure 
     * <p>Third :' argument controls output and is the number defined by the OutputControl class,
     * <p>Fourth ':' argument must be followed by lower case y if you want basic analysis performed.
     * @param args the command line arguments
     * @see TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT
     */
    public static void main(String[] args) 
    { 
      System.out.println("ImperialPapers Arguments :<network> :<communityStructure> :<outputMode> :<basicAnalysis>");
      
      //First arg chooses network
      int n1=149; 
      int ano=0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) {
          if (args[ano].charAt(1)=='?') {SetUpNetwork.printNetworkTypes(System.out, "", "  "); n1=0;} 
          else n1=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      }
      else n1=0;
      System.out.println("--- Using network one "+SetUpNetwork.typeString(n1));

          //Second arg chooses second network to be used as a community structure
      int n2=139; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) {
          if (args[ano].charAt(1)=='?') {SetUpNetwork.printNetworkTypes(System.out, "", "  "); n2=0;} 
          else n2=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      }
      else n2=0;
      System.out.println("--- Using network two "+SetUpNetwork.typeString(n2));

            //Third arg chooses output mode
      int outputMode=0; //30; //60; //1 30;
      ano++;
      if (args.length>ano ) 
          if (timgraph.isOtherArgument(args[ano])) outputMode=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      OutputMode om = new OutputMode(outputMode);
      System.out.println("--- Output Mode "+outputMode+": "+om.getModeString(" "));
      
      //Fourth arg chooses basic analysis on or off
      boolean basicAnalysis=false;
      ano++;
      if (args.length>ano ) 
          if ((timgraph.isOtherArgument(args[ano])) && (args[ano].charAt(1)=='y') ) basicAnalysis=true;
      System.out.println("--- Basic analysis is "+(basicAnalysis?"on":"off"));


      boolean basicAnalysis1=false;
      boolean basicAnalysis2=false;
      timgraph tg1 = processNetworkFile(n1,outputMode,basicAnalysis1, args);
      timgraph tg2 = processNetworkFile(n2,outputMode,basicAnalysis2, args);

      VertexCommunity vc2 = new VertexCommunity(tg1);
      TreeMap<String,Integer>vertexNameToIndex = tg1.getVertexNameIndexMap();
      boolean forceNormalisation=false;
      boolean checkNormalisation=true;
      boolean forceLowerCase=true;
      boolean checkBipartite=true;
      int sampleFrequency=1;
      //boolean forceLowerCase=false;
      tg2.inputName.setNameEnd("inputBVNLS.dat");
      String fullFileName=tg2.inputName.getFullFileName();
      vc2.readStringVertexCommunitiesFile(vertexNameToIndex, 
            fullFileName, forceLowerCase, checkBipartite, sampleFrequency,  forceNormalisation, checkNormalisation);
      
      
      int qdef=0;
      int qualityType=2;
      double lambda=1.0;
      int method=0;
      boolean graphMLOutput=false;
      VertexPartition vp1 = VertexCommunities.calculateVertexPartition(tg1, qdef, qualityType, lambda, method, infoLevel, graphMLOutput);
      //VertexPartition vp2 = VertexCommunities.calculateVertexPartition(tg2, qdef, qualityType, lambda, method, infoLevel, graphMLOutput);

      VertexCommunity vc1 =  new VertexCommunity (tg1, vp1, qdef, qualityType, lambda);
      
      vc1.printCommunities(System.out, "", " ", true, true);
      vc2.printCommunities(System.out, "", " ", true, true);
      
      MutualInformation mi = Community.calcMutualInformation(vc1, vc2, tg1);
      int r=mi.checkNormalisations();
      if (r>0) throw new RuntimeException(mi.checkNormalisationsString(r));
      else System.out.println(" --- "+mi.checkNormalisationsString(r));
      r=mi.checkConsistentcy();
      if (r<0) throw new RuntimeException("Consistency Failure");
      else System.out.println(" --- Consistent probabilties");
      
      System.out.println(mi.toStringLabelDescriptive(" : "));
      System.out.println(mi.toStringLabel(" : "));
      System.out.println(mi.toString(" : "));
    }

        /**
     * Process network files.
     * <p>Produces timgraph.
     * @param n network number
     * @param outputMode set output modes
     * @param basicAnalysis true if want analysis of iunput file
     * @param args additional arguments
     */
    static public timgraph makeVertexPartition(int n, int outputMode, boolean basicAnalysis, String [] args)
    {
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      timgraph tg = SetUpNetwork.setUpNetwork(n, args);
      if (outputMode>=0)  tg.outputControl.set(outputMode);
      if (tg.getNumberVertices()<40) tg.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(tg);
      return tg;
    }

        /**
     * Process network files.
     * <p>Produces timgraph.
     * @param n network number
     * @param outputMode set output modes
     * @param basicAnalysis true if want analysis of iunput file
     * @param args additional arguments
     */
    static public timgraph processNetworkFile(int n, int outputMode, boolean basicAnalysis, String [] args)
    {
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      timgraph tg = SetUpNetwork.setUpNetwork(n, args);
      if (outputMode>=0)  tg.outputControl.set(outputMode);
      if (tg.getNumberVertices()<40) tg.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(tg);
      return tg;
    }

    
}
