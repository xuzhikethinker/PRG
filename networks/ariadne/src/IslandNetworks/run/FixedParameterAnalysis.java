/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.run;

import IslandNetworks.islandNetwork;

/**
 * Given site data creates edges and analyses single network.
 * @author time
 */
public class FixedParameterAnalysis {
    
    /**
     * Creates a network for analysis of existing data files.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("\n**** Running FixedParameterAnalysis ****\n");
        String [] aList;
        boolean directed=true;
        if (args.length >0) {aList = args;}
        else{
        // Update modes are "PPA","DPPA","MDN","MC","VP","DCGM","RWGM","SGM"
        //String [] aList={"-Eariadne", "-fibaegean10S1L3a", "-uPPA", "-bt3.0"};
            String s="";
            int modeNumber=2;
            switch (modeNumber) {
                case 0: s = "-uPPA -bt3.0"; directed=false; break;
                case 1: s = "-uDPPA -bt3.0"; break;
                case 2: s = "-uMDN -dl100.0"; directed=false; break;
                case 3: s = "-uMC -dl100.0"; break;
                case 4: s = "-uVP -dl100.0"; directed=false; break;
                case 5: s = "-uDCGM -dl100.0"; break;
                case 6: s = "-uRWGM -dl100.0"; break;
                case 7: s = "-uSGM -dl100.0"; break;
                default: throw new RuntimeException("unknown mode");
            }
            String rootname="circleN10D100.0-C5-J50.0";
            //String rootname="circleN40D150.0-C10-J50.0";

            String ud = System.getProperty("user.dir");
            String fullArgString= "-Eariadne -fib"+rootname+" "+s;
            System.out.println(" arguments are:- "+fullArgString);
            aList=fullArgString.split("\\s+");
        }
        islandNetwork in = new islandNetwork(1); // dummy network with default values
//        a.outputMode.setAllOff(); // no file outputs by default
        int res = in.Parse(aList);
        if (res>0) {System.err.println("Command line argument failure - return code "+res);
            return;
            }
        res = in.getSiteData();
        System.out.println("Data reading OK, number of sites is "+in.getNumberSites());

        //in.setInfomationLevel(-1); // switch off outputs

        //run(in,10);

        in.showModelInputParameters(System.out, "");
        in.calculateEdgeModel();
        in.FileOutputBareNetwork("", islandNetwork.SEPSTRING);
        //in.FileOutputNetworkForData(islandNetwork.COMMENT, 4);
        in.FileOutputNetworkStatistics(islandNetwork.COMMENT, 4);
        in.FileOutputKMLNetwork(islandNetwork.COMMENT, 4);
        in.FileOutputEdgeList(islandNetwork.COMMENT, 4);
//        * @param cc comment characters put at the start of every line
//     * @param siteWeightFactor Maximum dot size for sites
//     * @param edgeWidthFactor Maximum edge width in diagrams
//     * @param minColourFrac fraction of total colours represented as colour 1
//     * @param zeroColourFrac fraction of total colours represented as colour 0
//     * @param fileType 0=plain, 1= BW, 2= colour, 3=Influence Matrix, 4=Culture Corrrelation Matrix
//     */
        int maxEdgeWidthSize=10;
        boolean colourOn=true;
        in.FileOutputPajek(islandNetwork.COMMENT, maxEdgeWidthSize, colourOn) ;
        in.FileOutputGraphMLNetwork(islandNetwork.COMMENT, maxEdgeWidthSize, directed, colourOn);
    }

    static void run(islandNetwork inputIN, String mode, int numberRuns){
        for (int r=0; r<numberRuns; r++){
            islandNetwork in = new islandNetwork(inputIN);   
        }
    }

}
