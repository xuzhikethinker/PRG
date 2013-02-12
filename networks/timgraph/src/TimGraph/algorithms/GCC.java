/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.algorithms;

import TimGraph.timgraph;
import java.util.TreeSet;

/**
 * Algorithms related to the GCC (Giant Connected Component)
 * @author time
 */
public class GCC {
    
        /**
     * Extracts a GCC from a graph.
     * <p>New graph takes characteristics from old graph.
     * <P>Untested but based on routine in SetupNetwork
     * @param fullns graph whose GCC is to be extracted.
     * @return the GCC of the new graph.
     */
    static public timgraph extractGCC(timgraph fullns){
        timgraph tg = new timgraph();
        tg.initialiseSomeParameters(tg.inputName.getNameRoot()+"GCC", tg.inputName.getDirectoryRoot(), tg.infoLevel, tg.outputControl.getNumber());
        tg.setNetworkInitialGraph(true);
        fullns.calcComponents();
        fullns.printComponentInfo();
        TreeSet<Integer> sgvertexList = fullns.getGCC();
        boolean makeBipartite=fullns.isBipartite();
        tg = fullns.projectSubgraph("_GCC",sgvertexList, fullns.isDirected(), 
                true, fullns.isWeighted(), true, makeBipartite) ;
        return tg;
    }

//        /**
//     * Extracts a GCC from a graph.
//     * <P>Untested but based on routine in SetupNetwork.
//     * @param fullns graph whose GCC is to be extracted.
//     * @param name name of original graph, GCC is appended in resulting graph
//     * @param aList list of arguments for the new graph.
//     * @return the GCC of the new graph.
//         * @deprecated list of parameters is overridden in projectSubgraph
//     */
//    static public timgraph extractGCC(timgraph fullns, String name, String [] aList ){
//        timgraph tg = new timgraph();
//        tg.initialiseSomeParameters(tg.inputName.getNameRoot()+"GCC", tg.inputName.getDirectoryRoot(), tg.infoLevel, tg.outputControl.getNumber());
//        tg.parseParam(aList);
//        tg.setNetworkInitialGraph(true);
//        fullns.calcComponents();
//        fullns.printComponentInfo();
//        TreeSet<Integer> sgvertexList = fullns.getGCC();
//        tg = fullns.projectSubgraph("GCC",sgvertexList, fullns.isDirected(), 
//                true, fullns.isWeighted(), true, fullns.isBipartite()) ;
//        return tg;
//    }
//

}
