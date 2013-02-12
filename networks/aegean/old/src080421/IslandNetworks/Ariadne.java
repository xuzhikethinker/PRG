/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;


import java.io.File;
////import java.util.Date;
//import javax.swing.*;          
//import javax.swing.JFrame;

//import java.awt.*;
//import java.awt.event.*;
//import java.io.File;
//import java.io.FileInputStream;
//import cern.colt.io.Converting;
//import TextReader;
//import IslandNetworks.islandNetwork;
//import IslandNetworks.MultipleAnalysis;
//import IslandNetworks.InputFileWindow;

//import TimUtilities.LookAndFeel;

/**
 * Main entry routines for <tt>islandNetwork</tt>runs.
 * @author  time 
 */

public class Ariadne {
    
//    String SEP = "\t";
    /** Creates a new
     * 
     * instance of aegean */
    public Ariadne() {    
        
        //dataread=false;
        

    }
    
    /**
     * Runs aagean simulations.
     * <br>If GUI argument given then runs in full netwrok generation GUI mode.
     * <br>If no GUI chosen then will run network generator if non-negative network number given for generation.
     * <br> otherwise will run multiple analysis.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Date date = new Date();
        
        
        int res;
        islandNetwork a = new islandNetwork(1); // dummy network with dfault values
        //a.executeMode.setMode("analysis");
        a.executeMode.setMode("ariadne");
        
        res = a.Parse(args);
        if (res>0) {System.out.println("Command line argument failure - return code "+res);
            return;
            }
        
        System.out.println("--- Running in mode "+a.executeMode.toString());
// *********** Aegean *************************************************   
        if (a.executeMode.isMode("Ariadne"))
       { 
                    InputDataFrame idf = new InputDataFrame(a);
                    idf.drawFrame();
        }

        
        
// *********** MultipleAnalysis *************************************************   
        if (a.executeMode.isMode("MultipleAnalysis"))
       {
                        res = a.getSiteData();
            System.out.println("Data reading OK, number of sites is "+a.numberSites);
  
            MultipleAnalysis ma = new MultipleAnalysis();
            a.setInfomationLevel(-1); // switch off outputs
            ma.readVariableRanges(a.inputFile.getFullLocationFileRoot());
            ma.calcRange(a);
//?????????????????????????????????????????????????????????            ????
            a.outputFile.setParameterName("v"+a.modelNumber.major+"_"+a.modelNumber.minor);
            ma.FileOutputStatistics(a.outputFile);
        }
    
        
        
// *********** Analysis *************************************************        
        if (a.executeMode.isMode("Analysis"))
       {
            islandNetwork in = new islandNetwork(1);
        in.inputFile.setAll("input/", "", "aegean34S1L3", "", 0, "_input", "dat");
        File ifl;  
        if (args.length>0) { in.Parse(args); ifl = new File(in.outputFile.getFullLocationFileName("",a.NETWORKDATAFILEEXT));}
        else {
            in.setOutputFileName();
            in.outputFile.sequenceNumber=0;
            ifl = new File(in.outputFile.getFullLocationFileName("", a.NETWORKDATAFILEEXT));            
        }
        System.out.println("Trying to read from "+ifl.getPath());
        islandNetwork firstData = new islandNetwork(ifl,in);
        firstData.calcNetworkStats();
        NetworkWindow nw = new NetworkWindow(firstData);
        nw.drawNetworkWindow();
    }

 // *********** NetworkGenerator *************************************************        
       if (a.executeMode.isMode("NetworkGenerator"))
       {
                NetworkGenerator ng =new NetworkGenerator(a.generateType,a.generateNumber,a.Hamiltonian.distanceScale);
                 ng.FileOutputSite(2);
                 return;

        }
        

        
        
    }//eo main

    


}//eo aegean    
    
    

    
    
    
    
    
    
