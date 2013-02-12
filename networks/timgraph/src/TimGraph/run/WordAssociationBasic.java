/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.timgraph;

/**
 *
 * @author time
 */
public class WordAssociationBasic {

    static int infoLevel =0;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
       SetUpNetwork setnet = new SetUpNetwork(infoLevel);
       timgraph tg = SetUpNetwork.setUpNetwork(70, args);
       
    }
}
