/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.algorithms.LineGraphProjector;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import TimUtilities.TimMemory;
import TimUtilities.TimTiming;

/**
 * Makes a line graph
 * @author time
 */
public class MakeLineGraph {
    
    /**
     * Makes a line graph.
     * <p>If first argument starts with a ':' 
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>)
     * the rest of this argument is the type of line graph to use, default  2=D
     * [0=C, 1=Ctilde, 2=D, 3= Dtilde (E in unweighted paper), 4=E, 5=Etilde]
     * Remaining arguments start with a '-' and are parsed by timgraph.
     * <p>The different types are defined in the weighted graph paper
     * <em>Edge Partitions and Overlapping Communities in Complex Networks</em>,
     * T.S.Evans and R.Lambiotte <tt>arXiv:0912.4389</tt>.  They are set by the
     * <tt>type</tt> parameters and defined by constant strings in this class,
     * {@link LineGraphProjector#lgExtensionList} for file name additions
     * and {@link LineGraphProjector#lgExtensionDescription}.
     * These are as follows:
     * <ul>
     * <li>0 = Line Graph L(G)=C(G)</li>
     * <li>1 = Line Graph with self-loops, Ctilde</li>
     * <li>2 = Degree Weighted Line Graph, D(G)</li>
     * <li>3 = Degree Weighted Line Graph with self-loops, Dtilde(G)</li>
     * <li>4 = Strength Weighted Line Graph, E(G) </li>
     * <li>5 = Strength Weighted Line Graph with self-loops, Etilde(G)</li>
     * </ul>
     * <p>Checked  on weighted undirected BowtieW 02/06/2010
     * @param args list of arguments
     */
    public static void main(String[] args) {
        
        TimTiming timing = new TimTiming();
        TimMemory memory = new TimMemory();
        System.out.println("\n !!! Initial Memory: "+memory.StringAllValues());
     
    int lgmethod = 4; // 0=Line Graph (C),
                      // 1=Line Graph with self-loops (Ctilde), 
                      // 2=Degree weighted Line Graph (D),
                      // 3=Degree weighted Line Graph with self loops (Dtilde)
                      // 4=Strength Weighted Line Graph (E),
                      // 5=Strength Weighted Line Graph with self loops (Etilde)
     int ano=0;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) lgmethod=Integer.parseInt(args[ano].substring(1, args[ano].length()));
     System.out.println("--- Using Line Graph type "+LineGraphProjector.lgExtensionDescription[lgmethod]);    

     // if no argument list use the following set up (i.e. for testing)   
     String basicFileName="BowTieW";
     String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-gbt", "-fin"+basicFileName, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
     if (args.length>0) aList=args;

     timgraph tg = new timgraph();
     tg.parseParam(aList);
        
     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFile(); // this will read in the network
     tg.printParametersBasic();
     
     FileOutput fotg = new FileOutput(tg);
     boolean asNames=true; 
     boolean infoOn=true; 
     boolean headerOn=true; 
     boolean edgeIndexOn=true;  
     boolean edgeLabelOn=false;
     fotg.printEdges(asNames, infoOn, headerOn, edgeIndexOn, edgeLabelOn);
     
    
     System.out.println("\n *** \n *** Making Line Graph type "+LineGraphProjector.lgExtensionDescription[lgmethod]+" *** \n");    
     timgraph lineGraphtg = LineGraphProjector.makeLineGraph(tg,lgmethod, true);
     lineGraphtg.printParametersBasic();
     FileOutput folg = new FileOutput(lineGraphtg);
     folg.informationNetworkBasic(timgraph.COMMENTCHARACTER, timgraph.SEP);
     folg.edgeListSimple(true);
     folg.graphML();
      

     System.out.println("\n *** Finished making Line Graph type "+LineGraphProjector.lgExtensionDescription[lgmethod]);
     System.out.println(" !!! Took "+timing.runTimeString());    
     System.out.println(" !!! Final Memory: "+memory.StringAllValues());


        
    }

}
