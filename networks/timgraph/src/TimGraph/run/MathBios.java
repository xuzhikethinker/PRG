/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

//import java.io.PrintStream;
//import java.io.FileOutputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;

import TimGraph.timgraph;
//import TimGraph.FileInput;
import TimGraph.io.FileOutput;
//import TimGraph.Community.Community;
//import TimGraph.Community.Partition;
//import TimGraph.Community.LouvainPartition;
//import TimGraph.Community.SimulatedAnnealingPartition;
//import TimGraph.io.GraphViz;
//import TimGraph.io.GraphMLGenerator;


/**
 * Runs analysis of MacTutor data base
 * @author time
 */
public class MathBios {

    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static timgraph tg;
    static timgraph lineGraphtg; 
    static int infoLevel=0;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
      int n=0;  
      if (args.length<1) n= 50;

      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      
      
      tg = setnet.setUpNetwork(n, args);
      // Check this out an adapt to give a better general output
      //tg.OutputGraphInfo(PS, SEP, n);
      
      // output graph as is.
      basicroot = tg.inputName.getNameRoot();
      FileOutput fo = new FileOutput(tg);
//      fo.edgeListSimple();
//      fo.graphML();
//      fo.informationNetworkBasic("", SEP);
//      fo.printVertices("", SEP, true, true, false, false, false, false);
      fo.informationGeneral("", SEP);

    }

}
