/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run.DAG;

import TimGraph.algorithms.DAG.DimensionDAG;
import TimGraph.algorithms.DAG.TransitiveReduction;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import cern.colt.matrix.DoubleMatrix2D;




/**
 *
 * @author time
 */
public class DoTransitiveReduction {

    public DoTransitiveReduction (){

    }

    public static void main(String[] args) {
     int ano=0;
     //String basicFileName="dag1";
     //String basicFileName="DAG_V100m3.01dV10FbSy";
     //String basicFileName="DAGv10m3.01dv5fb";
     //String basicFileName="DAGv100m3.01dv10fb";
     String basicFileName="MTSO_BTF";
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) basicFileName=args[ano].substring(1, args[ano].length());
     System.out.println("--- Using files "+basicFileName);
     // if no argument list use the following set up (i.e. for testing)
     String [] aList = { "-gvet", "-gdt", "-gewf", "-gvlf", "-gemf", "-gbf", "-fin"+basicFileName+"inputEL.dat", "-gn99",  "-e0", "-o23", "-xi0"};
     
     timgraph tg = new timgraph();
     tg.parseParam(aList);

     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFile(); // this will read in the network
     tg.printParametersBasic();
     TransitiveReduction tr = new TransitiveReduction(tg);
     int deleted=0;
     deleted=tr.reducePartially();
     System.out.println("Partial transitive reduction has deleted "+deleted+" edges");
     boolean asNames=true;
     boolean infoOn=true;
     boolean headerOn=true;
     boolean edgeIndexOn=true;
     boolean edgeLabelOn=true;
     double minWeight=0;
     int minLabel=-1; //timgraph.IUNSET-1;
     if (tg.getNumberEdges()<41) tg.printEdges(System.out, timgraph.COMMENTCHARACTER, timgraph.SEP,
             minWeight, minLabel,
             asNames, infoOn, headerOn, edgeIndexOn, edgeLabelOn);
     deleted=tr.reduceFully();
     System.out.println("Full transitive reduction has deleted "+deleted+" edges");

     FileOutput fotg = new FileOutput(tg);
     fotg.fileName.appendToNameRoot("_TR");
     if (tg.getNumberEdges()<41) tg.printEdges(System.out, timgraph.COMMENTCHARACTER, timgraph.SEP,
             minWeight, minLabel,
             asNames, infoOn, headerOn, edgeIndexOn, edgeLabelOn);
     fotg.printEdges(minWeight, minLabel, asNames, infoOn, headerOn, edgeIndexOn, edgeLabelOn);

     String sep="\t";
     boolean fromOn =true;
     DoubleMatrix2D  distance = DimensionDAG.getDistanceMatrix(tg, fromOn);
     DoubleMatrix2D  dimension = DimensionDAG.getDimensionMatrix(tg, distance, fromOn);
 //    DimensionDAG.FileOutputMaximumDistances(tg,fromOn);
     DimensionDAG.FileOutputMaximumDistances(sep,tg,distance,fromOn);
     DimensionDAG.FileOutputDimensions(sep,tg,dimension,fromOn);
     fromOn = false;
     DimensionDAG.FileOutputMaximumDistances(tg,fromOn);
     


    }
    
    


}
