/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.OutputMode;
import TimGraph.algorithms.GCC;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;

/**
 * Extracts the GCC (giant Connected Component) for a graph.
 * @author time
 */
public class GCCExtractor {

    final static String SEP = "\t";
    static String basicroot="UNSET";

    static timgraph tg;
    static int infoLevel=0;


   /**
     * Basic routine to extract GCC from a graph.
     * <p>GCCExtractor arguments are just the usual <tt>timgraph</tt> arguments.
     * Specify input file name and graph characteristics using timgraph arguments
     * or its <tt>param.dat</tt> file.
     * The output will be edge lists to <em>nameRoot</em><tt>GCCoutputELS.dat</tt>
     * and <em>nameRoot</em><tt>GCCoutputEL.dat</tt>.  Additional output
     * for both original and GCC graph will be those defined by the <tt>OutputControl</tt> class
     * through the <tt>-o</tt><em>NNN</em> option.
     * @param args <tt>timgraph</tt> the command line arguments
     * @see TimGraph.timgraph#printUsage()
     * @see TimGraph.OutputMode
     */
    public static void main(String[] args)
    {
      System.out.println("GCCExtractor Arguments: just specify file name and charcteristics using timgraph arguments alone");
      System.out.println("                         Output is controled by OutputMode argument -oNNN");
      OutputMode o = new OutputMode(255);
      o.printUsage(System.out,"");


//     String basicFileName="netscienceNeAT";
//     String [] aList = { "-fin"+basicFileName, "-fieinputELS.dat",
//                            "-gvet", "-gdf", "-gewf", "-gvlt",
//                            "-gn99",  "-e0", "-o255", "-xi0"};
     String basicFileName="RAEmanAuthorPaper";
     String [] aList = { "-fin"+basicFileName+"inputBVNLS.dat", "-o255"};
     if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);

     tg.setNameRoot(tg.inputName.getNameRoot());
//     tg.setNetworkFromManyInputFiles(); // this will read in the network
//     tg.printParametersBasic();

     int xColumn=1;
     int yColumn=2;
     boolean headerOn=false;
     boolean infoOn=false;
     int nameColumn =1;
     tg.setNetworkFromManyInputFiles(xColumn, yColumn, nameColumn, headerOn, infoOn); // this will read in the network
     tg.printParametersBasic();


     if (tg.getNumberVertices()<20) tg.printNetwork(true);

     timgraph gcc = GCC.extractGCC(tg);
     BasicAnalysis.analyse(gcc);
//     FileOutput fogcc = new FileOutput(gcc);
//     fogcc.edgeListSimple(true);
//     fogcc.edgeListSimple(false);
//     if (tg.outputControl.getNumber()>0) {
//         FileOutput fo = new FileOutput(tg);
//         fo.informationGeneral("", SEP);
//         fogcc.informationGeneral("", SEP);
//     }


    }



}

