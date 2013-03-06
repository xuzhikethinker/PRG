/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import TimGraph.OutputMode;
import TimGraph.algorithms.BipartiteTransformations;

/**
 * Basic analysis of a graph.
 * @author time
 */
public class Randomise {
    
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static timgraph tg;
    static int infoLevel=0;
    
    public Randomise(timgraph newtg){tg=newtg;}
    
    
    /**
     * Basic routine to randomise a given graph.
     * <p>First Randomize arguments is the number of copies to make (default 1).
     * The rest are just the usual <tt>timgraph</tt> arguments.
     * Specify input file name and graph characteristics using timgraph arguments 
     * and the outputs will be those defined by the {@link TimGraph.OutputMode}  class.
     * @param args <tt>timgraph</tt> the command line arguments
     * @see TimGraph.timgraph#printUsage()
     * @see TimGraph.OutputMode
     */
    public static void main(String[] args) 
    { 
      System.out.println("Randomise Arguments- :<numberCopies> then just specify file name and characteristics using timgraph arguments alone");
      System.out.println("                         Output is controled by OutputMode argument -oNNN");
      OutputMode o = new OutputMode(255);
      o.printUsage(System.out,"");

     int ano=0;
     int numberCopies=1;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) numberCopies=Integer.parseInt(args[ano].substring(1, args[ano].length()));
     System.out.println("--- Number of random copies being made "+numberCopies);

     String basicFileName="BiTest";
     String [] aList = { "-fin"+basicFileName, "-fieinputBVNLS.dat", "-gn99",  "-e0", "-o255", "-xi0"};
     if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
        
     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFile(); // this will read in the network
     tg.printParametersBasic();
      
      if (tg.getNumberVertices()<20) tg.printNetwork(true);
     
     timgraph rtg;
     for (int r=0; r<numberCopies; r++){
         if (tg.isBipartite()){
             rtg = BipartiteTransformations.randomise(tg,"Rand"+r);
             BasicAnalysis.analyse(rtg);
         }
     }//eo for r
    }

    
    /**
     * Basis analysis of a graph
     * @param tg graph to analyse
     */
     static public void analyse(timgraph tg){
      basicroot = tg.inputName.getNameRoot();
      tg.calcStrength();
      FileOutput fo = new FileOutput(tg);
      fo.informationGeneral("", SEP);
      fo.edgeListSimple();
      if (tg.isVertexLabelled()) fo.printEdges(true, false, false, false, false);

    }
    

}
