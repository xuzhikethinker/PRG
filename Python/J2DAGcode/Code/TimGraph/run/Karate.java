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
public class Karate {
        final static String [] COMMUNITYMETHOD = {"Louvain","SimAn"};
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
      int n=30; //1 30;
      if (args.length>0) n= 0;

      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      
      tg = setnet.setUpNetwork(n, args);

      // output graph as is.
      basicroot = tg.inputName.getNameRoot();
      FileOutput fo = new FileOutput(tg);
      fo.informationGeneral("", SEP);

      LineGraphCommunities lgc = new LineGraphCommunities(tg);
      

  
    }


}
