/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import TimGraph.OutputMode;

/**
 * Basic analysis of a graph.
 * @author time
 */
public class BasicAnalysis {
    
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static timgraph tg;
    static int infoLevel=0;
    
    public BasicAnalysis(timgraph newtg){tg=newtg;}
    
    
    /**
     * Basic routine to analyse a given graph.
     * <p>BasicAnalysis arguments are just the usual <tt>timgraph</tt> arguments.
     * Specify input file name and graph characteristics using timgraph arguments 
     * and the outputs will be those defined by the <tt>OutputControl</tt> class.
     * @param args <tt>timgraph</tt> the command line arguments
     * @see TimGraph.timgraph#printUsage
     * @see TimGraph.OutputMode
     */
    public static void main(String[] args) 
    { 
      System.out.println("BasicAnalysis Arguments: just specify file name and characteristics using timgraph arguments alone");
      System.out.println("                         Output is controled by OutputMode argument -oNNN");
      OutputMode o = new OutputMode(255);
      o.printUsage(System.out,"");

//     String basicFileName="TGCGn1x6y6t108p0";
     //String basicFileName="aegean39S1L3a_v1_3e-1.0j0.0m0.5k1.0l4.5b1.2D100.0MC_r4_edgeweight";
     //String basicFileName="MSC-2010classbygroup";
     //String basicFileName="MSC-2010subclassbygroup";
     //String [] aList = { "-fir"+basicFileName, "-fieinputBVNLS.dat", "-o511"};
     //String basicFileName="RAEmanAuthorPaper";
     //String [] aList = { "-fin"+basicFileName+"inputBVNLS.dat", "-o511"};
//     String basicFileName="ukwardsinputELS.dat";
//     String [] aList = { "-fin"+basicFileName, "-o511"};
     //String basicFileName="karateTSE";
     //String [] aList = { "-fin"+basicFileName, "-fieinputEL.dat", "-o511"};
//     String basicFileName="UKHEI08";
//     String [] aList = { "-fin"+basicFileName, "-fieinputLAM.dat", "-gn99",  "-e0", "-o255", "-xi0"};
//     String basicFileName="BiTest";
//     String [] aList = { "-fin"+basicFileName, "-fieinputBVNLS.dat", "-gn99",  "-e0", "-o255", "-xi0"};
//     String basicFileName="burtinputELS.dat";
//     String [] aList = { "-fin"+basicFileName,"-fisinput/burt","-fosoutput/burt","-o511"};
     String basicFileName="vaneshtestinput.net";
     String [] aList = { "-fin"+basicFileName,"-fisinput/vanesh","-fosoutput/vanesh","-o511", "-xv10", "-xe20"};


      if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
     tg.outputControl.printMode(System.out, " ");
        
     tg.setNameRoot(tg.inputName.getNameRoot());
//     tg.setNetworkFromInputFile(); // this will read in the network
//     tg.printParametersBasic();

     int xColumn=1;
     int yColumn=2;
     boolean headerOn=false;
     boolean infoOn=false;
     int nameColumn =1;
     tg.setNetworkFromInputFile(xColumn, yColumn, nameColumn, headerOn, infoOn); // this will read in the network
     tg.printParametersBasic();
     System.out.println("Output: "+tg.outputControl.getModeString(" "));

      
      if (tg.getNumberVertices()<20) tg.printNetwork(true);
      analyse(tg);
    }

    
    /**
     * Basis analysis of a graph
     * @param tg graph to analyse
     */
     static public void analyse(timgraph tg){
      basicroot = tg.inputName.getNameRoot();
      tg.calcStrength();
      FileOutput fo = new FileOutput(tg);
       boolean vertexListOn=true;
       boolean printTriangles=true;
       boolean printSquares=true;
       boolean printNearestNeighbours=false;
       boolean edgeListOn=true;
       boolean graphMLOn=false;
       fo.informationGeneral("", SEP,
            vertexListOn,
            printTriangles, printSquares,
            printNearestNeighbours,
            edgeListOn, graphMLOn,
            tg.outputControl);

//      fo.informationGeneral("", SEP);
      //fo.edgeListSimple();
      //if (tg.isVertexLabelled()) fo.printEdges(true, false, false, false, false);

    }
    

}
