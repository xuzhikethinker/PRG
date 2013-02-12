/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.io.*;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.StringFilter;

/**
 * Reads in a tab separated file.
 * <p>Has options for column and row headers which are used as vertex labels.
 * @author time
 */
public class AdjacencyMatrixConverter {
    
    /**
     * Maximum number of columns allowed.
     */
    public static int maxColumns=1000;

         public static void main(String[] args) {
         
         System.out.println("AdjacencyMatrixConverter Arguments :headerColumnOn :rowLabelOn then timgraph arguments");
         
        int ano=0;
        boolean columnLabelOn= false;
         if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) columnLabelOn= StringFilter.trueString(args[ano].substring(1, 2));

        ano++;
        boolean rowLabelOn= false;
        if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) rowLabelOn= StringFilter.trueString(args[ano].substring(1, 2));

     
        System.out.println("Column labels "+StringFilter.onOffString(columnLabelOn)+", row labels "+StringFilter.onOffString(rowLabelOn));
     
         String cc="";
         String sep="\t";
         boolean infoOn=true;
         
         
     // if no argument list use the following set up (i.e. for testing)   
     //String basicFileName="BowTieWD";
     //basicFileName="MATRIXSNAVSMSystem1";
//     String [] aList = { "-gvef", "-gdt", "-gewt", "-gvlt", "-gbf", "-fin"+basicFileName, "-fieinputLAM.dat", "-gn99",  "-e0", "-o23", "-xi0"};

     // Matrix of weights, no headers
     //String basicFileName="BelgianPhoneDataAgregatedNetwork_DNis";
     //String basicFileName="BelgianPhoneDataAgregatedNetwork_NCallsNis";
     String basicFileName="BelgianPhoneDataAgregatedNetwork_TCallsNis";
     String [] aList = { "-gvef", "-gdt", "-gewt", "-gvlt", "-gbf", "-fin"+basicFileName, "-fieinputAdjMat.dat", "-gn99",  "-e0", "-o0", "-xi0"};
     if (args.length>0) aList=args;

     
     timgraph tg = new timgraph();
     tg.parseParam(aList);
        
     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFiles(); // this will read in the network
     tg.printParametersBasic();
     
     FileOutput fotg = new FileOutput(tg);
     boolean asNames=true; 
     boolean headerOn=true; 
     boolean edgeIndexOn=true;  
     boolean edgeLabelOn=false;
     fotg.printEdges(asNames, infoOn, headerOn, edgeIndexOn, edgeLabelOn);
     boolean generalInfoOn=false;
     if (generalInfoOn) {
         // triangles and squares now fromoutputmode of timgraph
         //boolean printTriangles=true;
         //boolean printSquares=true;
         boolean vertexListOn=true;
         boolean edgeListOn=true;
         boolean graphMLOn=true;
         boolean printNearestNeighbours=false;
         fotg.informationGeneral(cc, sep, 
                 vertexListOn, printNearestNeighbours,
                 edgeListOn, graphMLOn, tg.outputControl);
     }


     }
         
         

          
  
         
    
}
