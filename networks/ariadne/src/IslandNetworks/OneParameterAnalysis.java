/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

/**
 *
 * @author time
 */
public class OneParameterAnalysis {
    
    /**
     * Runs island networks for multiple parameters values.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                // use dummy initial network to set up and store parameters
        islandNetwork a = new islandNetwork(1); // dummy network with dfault values
        a.outputMode.setAllOff(); // no file outputs by default
        int res = a.Parse(args);
        if (res>0) {System.out.println("Command line argument failure - return code "+res);
            return;
            }
        res = a.getSiteData();
        System.out.println("Data reading OK, number of sites is "+a.numberSites);

        a.setInfomationLevel(-1); // switch off outputs
        MultipleAnalysis ma = new MultipleAnalysis();
        a.outputFile.setParameterName("v"+a.modelNumber.major+"_"+a.modelNumber.minor);
        int numberRuns=2;
        double [] results = ma.calcOneParameterSet(a,numberRuns);
        ma.printOneParameterStatistics(System.out, "\t", results);

        
    }

    
    
}
