/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.run;

import IslandNetworks.*;
import java.io.File;

/**
 * Analyse network island files using Rihll-Wilson Gravity Model.
 * @author time
 */
public class AnalyseRW {

 //final static String NETWORKDATAFILEEXT = "anf"; // extension for ariadne network data file
        
    
    public AnalyseRW()
    {
        
    }
    
    /**
     * Creates a network window for analysis of existing data files.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        islandNetwork in = new islandNetwork(1); // dummy network with default values
//        a.outputMode.setAllOff(); // no file outputs by default
        String  [] aList = {"-bt1.005","-l1.0"};
        if (args.length>0) aList=args;
        int res = in.Parse(aList);
        if (res>0) {System.err.println("Command line argument failure - return code "+res);
            return;
            }
        res = in.getSiteData();
        System.out.println("Data reading OK, number of sites is "+in.getNumberSites());

        in.setUpdateMode("RW");
        in.doRW();
        in.FileOutputNetworkStatistics("#", 3);


    }

}
