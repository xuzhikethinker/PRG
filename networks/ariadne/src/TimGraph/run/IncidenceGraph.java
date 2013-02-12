/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.timgraph;
import TimGraph.io.FileOutput;
/**
 *
 * @author time
 */
public class IncidenceGraph {
    
    static int infoLevel=2;
            
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
       
      int n=1;
      if (args.length>0) n=Integer.parseInt(args[0]);

      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      
      timgraph tg = SetUpNetwork.setUpNetwork(n, args);
      tg.setInformationLevel(infoLevel);
      timgraph  ig = tg.makeIncidenceGraph(tg.isDirected(), true, tg.isWeighted(), true);
      
      if(ig.getNumberVertices()<21) ig.printNetwork(true);

      // output graph as is.
      //String basicroot = tg.inputName.getNameRoot();
      FileOutput fo = new FileOutput(ig);
      fo.informationNetworkBasic("", "\t");
      fo.graphML();
      fo.edgeListSimple();
      boolean printTriangles=true;
      boolean printSquares=true;
      fo.printVertices("","\t", null, printTriangles, printSquares, true,true,false,false,false,false,false,false, true);
      
    }
}
