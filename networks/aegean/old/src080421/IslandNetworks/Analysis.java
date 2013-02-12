/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import java.io.File;

/**
 * Analyse network island files.
 * @author time
 */
public class Analysis {

 final static String NETWORKDATAFILEEXT = "anf"; // extension for ariadne network data file
        
    
    public Analysis()
    {
        
    }
    
    /**
     * Creates a network window for analysis of existing data files.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        islandNetwork in = new islandNetwork(1);
        in.inputFile.setAll("input/", "", "aegean34S1L3", "", 0, "_input", "dat");
        File ifl;  
        if (args.length>0) { in.Parse(args); ifl = new File(in.outputFile.getFullLocationFileName("",NETWORKDATAFILEEXT));}
        else {
            in.setOutputFileName();
            in.outputFile.sequenceNumber=0;
            ifl = new File(in.outputFile.getFullLocationFileName("", NETWORKDATAFILEEXT));            
        }
        System.out.println("Trying to read from "+ifl.getPath());
        islandNetwork firstData = new islandNetwork(ifl,in);
        firstData.calcNetworkStats();
        NetworkWindow nw = new NetworkWindow(firstData);
        nw.drawNetworkWindow();
    }

}
