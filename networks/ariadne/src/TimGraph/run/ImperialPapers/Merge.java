/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run.ImperialPapers;

import TimGraph.OutputMode;
import TimGraph.algorithms.BipartiteTransformations;
import TimGraph.run.BasicAnalysis;
import TimGraph.run.SetUpNetwork;
import TimGraph.timgraph;

/**
 * Merges various paper-X paper-Y bipartite graphs into weighted X-Y bipartite graphs
 * @author time
 */
public class Merge {

        final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static int infoLevel=0;
    
    
    public Merge(){}
    

    /**
     * Basic routine to merge two bipartite graphs.
     * <p>If first argument starts with a '*','^' or ':'
     * (specified by <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>
     * {@link TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT})
     * the rest of the argument is the network number for the setup routine
     * If arguments exits but the first has no ':' then network is set up from file read in using the arguments
     * to point it to the right file.
     * <p>Second :' argument number of second network
     * <p>Third :' argument controls output and is the number defined by the OutputControl class,
     * <p>Fourth ':' argument must be followed by lower case y if you want basic analysis performed.
     * @param args the command line arguments
     * @see TimGraph.timgraph#NOT_TIMGRAPH_ARGUMENT
     */
    public static void main(String[] args) 
    { 
      System.out.println("ImperialPapers Arguments :<network1number> :<network2number> :<outputMode> :<basicAnalysis>");
      
      //First arg chooses network
      int n1=139; 
      int ano=0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) {
          if (args[ano].charAt(1)=='?') {SetUpNetwork.printNetworkTypes(System.out, "", "  "); n1=0;} 
          else n1=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      }
      else n1=0;
      System.out.println("--- Using network one "+SetUpNetwork.typeString(n1));

          //Second arg chooses second network
      int n2=119; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) {
          if (args[ano].charAt(1)=='?') {SetUpNetwork.printNetworkTypes(System.out, "", "  "); n2=0;} 
          else n2=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      }
      else n2=0;
      System.out.println("--- Using network two "+SetUpNetwork.typeString(n2));

            //Third arg chooses output mode
      int outputMode=255; //30; //60; //1 30;
      ano++;
      if (args.length>ano ) 
          if (timgraph.isOtherArgument(args[ano])) outputMode=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      OutputMode om = new OutputMode(outputMode);
      System.out.println("--- Output Mode "+outputMode+": "+om.getModeString(" "));
      
      //Fourth arg chooses basic analysis on or off
      boolean basicAnalysis=true;
      ano++;
      if (args.length>ano ) 
          if ((timgraph.isOtherArgument(args[ano])) && (args[ano].charAt(1)=='y') ) basicAnalysis=true;
      System.out.println("--- Basic analysis is "+(basicAnalysis?"on":"off"));


      boolean basicAnalysis1=false;
      boolean basicAnalysis2=false;
      timgraph tg1 = processNetworkFile(n1,outputMode,basicAnalysis1, args);
      timgraph tg2 = processNetworkFile(n2,outputMode,basicAnalysis2, args);
    
      String newNameRoot=tg1.inputName.getDirectoryRoot();
      String newDirectoryRoot=tg1.inputName.getDirectoryRoot();
      boolean makeLabelled=true;
      boolean makeVertexEdgeList=true;
      timgraph ng = BipartiteTransformations.merge(tg1, tg2, true, true, newNameRoot, newDirectoryRoot, infoLevel, outputMode, makeLabelled,  makeVertexEdgeList);
      if (ng.getNumberVertices()<40) ng.printNetwork(true);
      if (basicAnalysis) BasicAnalysis.analyse(ng);
      
      
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
