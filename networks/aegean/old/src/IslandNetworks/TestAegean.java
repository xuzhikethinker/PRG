/*
 * DataInput.java
 *
 * Created on 31 March 2004, 18:27
 * last update on Friday, February 25, 2005 at 18:25
 */


package IslandNetworks;

//import java.io.*;
import java.util.Date;
//import javax.swing.*;          
//import java.awt.*;
//import java.awt.event.*;
import java.io.File;
//import java.io.FileInputStream;
//import cern.colt.io.Converting;
//import TextReader;
import IslandNetworks.Edge.IslandEdge;
//import IslandNetworks.MultipleAnalysis;
//import IslandNetworks.InputFileWindow;

/**
 * Runs <tt>islandNetwork</tt> class for MBA aegean simulations.
 * @author  time 
 */

public class TestAegean {
    
    String SEP = "\t";
    /** Creates a new
     * 
     * instance of aegean */
    public TestAegean() {    
        
        //dataread=false;
        

    }
    
    /**
     * Runs aegean simulations.
     * <br>If GUI argument given then runs in full netwrok generation GUI mode.
     * <br>If no GUI chosen then will run network generator if non-negative network number given for generation.
     * <br> otherwise will run multiple analysis.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
//        boolean guiMode =false;            
        Date date = new Date();
        System.out.println(date);
//        String [] newArgs; // put new list or args in this
        
        int res;
        islandNetwork a = new islandNetwork(1); // dummy network with dfault values
        
        //a.drawInputWindow();
        a.inputGUI=false;
        //String rootString = "aegean34S1L3";
        String rootString = "testaegean5";
        
        a.inputFile = new FileLocation("input/", "", rootString, "", "_input", "dat");
        a.outputFile = new FileLocation("output/", "", rootString, "", 0, "", "dat");
            
        
//        String oldinputnameroot = a.inputnameroot;
        res = a.Parse(args);
        if (res>0) {System.out.println("Command line argument failure - return code "+res);
            return;
            }
//        for (int n=0; n<args.length;n++) {
//            if (args[n].substring(0,2)=="-wy") guiMode=true;
//            if (args[n].substring(0,2)=="-wn") guiMode=false;
//        }
        
        // next line uncommented for debugging
        //System.out.println(" $$$ DEBUGGING"); a.inputGUI =false; a.inputFile.setBasicRoot("aegean34S1L3"); 
        
        //if (test()) return;
        
        if (a.inputGUI) 
        {
//                    InputDataFrame idf = new InputDataFrame(a);
//                    idf.drawFrame();
            
                    testGUIDirect(a);
            
        }
        else {
             if (a.generateNumber>=0) 
             {
                 NetworkGenerator ng =new NetworkGenerator(a.generateType,a.generateNumber,a.Hamiltonian.distanceScale);
                 ng.FileOutputSite(2);
                 return;
             }
            
             testLouvain(a,args);
             
             // get distance and locations to use
//            res = a.getSiteData();
//            System.out.println("Data reading OK, number of sites is "+a.numberSites);
//            if (res<0) {
//                System.out.println("Data reading failed - return code "+res+" trying full distance data read");
//                res = a.getSiteData();
//                a.FileOutputSiteDistance("#");
//                if (res<0) {
//                    System.out.println("Position data reading failed - return code "+res);
//                    return;
//                }
//            }
            //a.showSiteValuesPos("#", 3);
            //a.showDistanceValues("#", 3);
            //        a.showScaledDistanceValues();
            //       a.showPotentialDistanceValues();
            //       a.showdHValues(1.0);
            
            
//            if (a.updateMode == 0) testPPA(a);
//                else {System.out.println("Update Mode is MC");

            // MC and results
//           a.doMC();
//            //a.showNetwork("#",3);
//            a.calcNetworkStats();
//            a.FileOutputNetworkForData(rootString, 4);
//            a.showNetworkStatistics("#",System.out,3);
            
            

            
             
//            MultipleAnalysis ma = new MultipleAnalysis();
//            a.setInfomationLevel(-1); // switch off outputs
//            ma.readVariableRanges(a.inputFile.getFullLocationFileRoot());
//            ma.calcRange(a);
//?????????????????????????????????????????????????????????            ????
//            a.outputFile.setParameterName("v"+a.modelNumber.major+"_"+a.modelNumber.minor);
//            ma.FileOutputStatistics(a.outputFile);
        }
        
        
    }//eo main

    public static void testLouvain(islandNetwork a, String [] args){
        //testaegean5_v1_3e-1.0m0.5j0.0k1.0l4.0b1.2s100.0MC_r0.anf
        //a.inputFile.setAll("input/", "", "aegean34S1L3", "", 0, "_input", "dat");
        a.inputFile.setAll("input/", "", "testaegean5", "", 0, "_input", "dat");
        File ifl;  
        if (args.length>0) { a.Parse(args); ifl = new File(a.outputFile.getFullLocationFileName("",islandNetwork.NETWORKDATAFILEEXT));}
        else {
            a.setOutputFileName();
            a.outputFile.sequenceNumber=0;
            ifl = new File(a.outputFile.getFullLocationFileName("", islandNetwork.NETWORKDATAFILEEXT));            
        }
        System.out.println("Trying to read from "+ifl.getPath());
        islandNetwork firstData = new islandNetwork(ifl,a);
        firstData.calcNetworkStats();
        //firstData.transferMatrix.printTransferMatrix("", "   ", System.out, 3, true);
       //NetworkWindow nw = new NetworkWindow(firstData);
        //nw.drawNetworkWindow();
            

            
            //firstData.transferMatrix.modeNumber=0;
            firstData.calcTransferMatrix(0, IslandEdge.valueINDEX);
            LouvainCommunity lc = new LouvainCommunity(firstData);
            lc.calcCommunity(0.5);
            lc.printCommunities(System.out, "", " | ");

    }
    
    /**
     * Tests GUI going directly to the drawing of the results.
     * @param a input islandNetwork
     */
    public static void testGUIDirect(islandNetwork a){
                // go straight to running
            a.outputMode.setAllOn(); 
            //a.inputFile.setNameRoot("testaegean5" ); a.
            //a.inputFile.setNameRoot("testaegean5b" );
            //a.inputFile.setNameRoot("testviking5" ); 
            a.inputFile.setNameRoot("viking134g17LSN" ); a.Hamiltonian.beta=1.0;
            a.Hamiltonian.lambda=4.0;
            a.getSiteData();
            a.dataread =true;
            a.outputFile.setBasicRoot(a.inputFile.getBasicRoot());
            // now call up the window asking for parameters
            //InputParameterFrame iw = new InputParameterFrame(a);
            a.setOutputFileName();
            a.doMC();
            //a.calcNetworkStats(); This is in showNetwork
            a.showNetwork("#", 3);

}
    
    
/* Test Vertex and Edge Updates
 *
 */
public static boolean testFileDecomposition(){
    FileLocation fl = new FileLocation("dirroot/","subdir/", "nameroot", "paramname",29,"eee","ext");
    String f = fl.getFullLocationFileName();
    FileLocation fl2 = new FileLocation(f);
    System.out.println (fl2.getFullLocationFileName());
    //System.out.println("root= "+fl2.getRootDirectory()+", sub= "+fl.getSubDirectory()+", file name root= "+fl.getSubDirectory());
    return true;
}


/* Test Vertex and Edge Updates
 *
 */
public static void test5(islandNetwork a)
          {
              double zeroColourFrac=0.35;
              double minColourFrac=0.65;
              int siteSizeFactor = 10;
              int edgeWidthFactor = 5;
              a.DisplayMaxVertexScale=1; //<=0 then display vertices relative to largest
                                        // else display vertices relative to this size       
              a.edgeSet.DisplayMaxEdgeScale=1;   //<=0 then display edges relative to largest
                                        // else display edges relative to this size     

              System.out.println("--- test5 routine: cooling MC, vertex and edge updates");
              a.showHamiltonianParameters();
              int estot =10; 
              int vstot =estot*a.numberSites; 
              double betamin = 1000;
              double betamax = 1000000;
              double betafactor = 2;
              double updatefrac =0.0;
              double beta;
              int betainc =0;
              long initialtime = System.currentTimeMillis ();
              for (beta = betamin; beta<=betamax; beta=beta*betafactor)
              {  
                 a.vertexUR.reset();
                 for (int i=0; i<vstot; i++) 
                 {
                     a.Hamiltonian.beta=beta;
                     a.vertexSweep();
 //                  System.out.println("Finished vertex sweep "+i);
                 }
                 
                 if (a.getInformationLevel() >1) System.out.println("b="+beta+" : ");
                 a.edgeUR.reset();
                 for (int i=0; i<estot; i++) 
                 {
                     a.Hamiltonian.beta=beta;
                     a.edgeSweep();
 //                  System.out.println("Finished edge sweep "+i);
                 }
                 
                 //System.out.println("b="+beta+"  E: "+a.edgeUR.toString()+"  V: "+a.vertexUR.toString());
                 if (((++betainc)%10) ==0) 
                 {
                    printEllapsedTime(initialtime);
                    System.out.println(" b="+beta+"\n  E: "+a.edgeUR.toString()+"\n  V: "+a.vertexUR.toString());
                    
                    }
                 else System.out.print(".");
                 if ((a.vertexUR.getTotalFractionMade()<0.001) && (a.edgeUR.getTotalFractionMade()<0.01) ) break;
                 
              }
              System.out.println("\nFinal beta "+beta);
              System.out.println("**********************************************");
              a.showNetworkStatistics("#",System.out,3);
              a.edgeSet.minColourFrac=0.0;
              System.out.println("Minimum Fraction for coloured edge = "+minColourFrac);
              a.calcNetworkStats();
              a.showColourValues("#", 3);
              System.out.println("  Edge stats: "+a.edgeUR.toString());
              System.out.println("Vertex stats: "+a.vertexUR.toString());
              a.edgeSet.doDijkstra(a.siteSet.getSiteArray(), a.Hamiltonian.shortDistanceScale);
              //a.showEdgeValues("#", 3);
              a.showDijkstraValues("#", 3);
              a.edgeSet.minColourFrac=0.0;
              a.edgeSet.zeroColourFrac=0.1;
              System.out.println("Scale for largest vertices in .net file= "+a.DisplayMaxVertexScale);
              System.out.println("Scale for maximum edges in .net file = "+a.edgeSet.DisplayMaxEdgeScale);
              System.out.println("Minimum Fraction for coloured edge in .net file = "+minColourFrac);
              a.calcNetworkStats();
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,0);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,1);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,2);
              a.FileOutputBareNetwork("#");
              a.FileOutputNetworkStatistics("#", 3);
              //NetworkWindow PW;
              //PW = new islandNetwork.PictureWindow(a);
              
         }


/* Test Vertex Updates Only
 *
 */
public static void test4(islandNetwork a)
          {
              double zeroColourFrac=0.1;
              double minColourFrac=0.5;
              int siteSizeFactor = 10;
              int edgeWidthFactor = 10;

              System.out.println("--- test4 routine: cooling MC, vertex updates only");
              a.showHamiltonianParameters();
              int estot =1000; 
              double betamin = 0.01;
              double betamax = 100000;
              double betafactor = 1.2;
              double updatefrac =0.0;
              for (double beta = betamin; beta<=betamax; beta=beta*betafactor)
              {  a.vertexUR.reset();
                 for (int i=0; i<estot; i++) 
                 {
                     a.Hamiltonian.beta=beta;
                     a.vertexSweep();
 //                  System.out.println("Finished vertex sweep "+i);
                 }
                 
                 if (a.getInformationLevel() >1) System.out.println("b="+beta+" : "+a.vertexUR.toString());
                 if (a.vertexUR.getTotalFractionMade()<0.001) break;
              }
              System.out.println("**********************************************");
              a.showNetworkStatistics("#",System.out,3);
              System.out.println("Minimum Fraction for coloured edge = "+minColourFrac);
              a.calcNetworkStats();
              a.showColourValues("#", 3);
              System.out.println(a.vertexUR.toString());
              minColourFrac=0.5;
              System.out.println("Minimum Fraction for coloured edge in .net file = "+minColourFrac);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,1);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,2);
         }



// test edge updates only    
          public static void test3(islandNetwork a)
          {   
              double zeroColourFrac=0.1;
              double minColourFrac=0.0;
     int siteSizeFactor = 5;
     int edgeWidthFactor = 5;

              System.out.println("--- test3 routine: cooling MC, edge updates only");
              a.showHamiltonianParameters();
              int estot =100; 
              double betamin = 0.01;
              double betamax = 100000;
              double betafactor = 1.2;
              double updatefrac =0.0;
              for (double beta = betamin; beta<=betamax; beta=beta*betafactor)
              {  a.edgeUR.reset();
                 for (int i=0; i<estot; i++) 
                 {
                     a.Hamiltonian.beta=beta;
                     a.edgeSweep();
 //                  System.out.println("Finished edge sweep "+i);
                 }
                 
                 System.out.println("b="+beta+" : "+a.edgeUR.toString());
                 if (a.edgeUR.getTotalFractionMade()<0.01) break;
              }
              System.out.println("**********************************************");
              a.showNetworkStatistics("#",System.out,3);
              a.edgeSet.minColourFrac=0.0;
              minColourFrac=0.0;
              System.out.println("Minimum Fraction for coloured edge = "+minColourFrac);
              a.calcNetworkStats();
              a.showColourValues("#", 3);
              System.out.println(a.edgeUR.toString());
              minColourFrac=0.5;
              System.out.println("Minimum Fraction for coloured edge in .net file = "+minColourFrac);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,1);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,2);
         }

          
          
          public static void test2(islandNetwork a){   
              
              double zeroColourFrac=0.1;
              double minColourFrac=0.0;
     int siteSizeFactor = 5;
     int edgeWidthFactor = 5;
              
              a.showHamiltonianParameters();
              
             int estot =100; 
             for (int i=0; i<estot; i++) {
                 a.edgeSweep();
 //                System.out.println("Finished edge sweep "+i);
             }
             System.out.println("Finished "+estot+" edge sweeps ");
             a.showNetworkStatistics("#",System.out,3);
              System.out.println(a.edgeUR.toString());
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,1);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,2);
          }

// ----------------------------------------------------------------------------    
          public static void testPPA(islandNetwork a)
          {   
            double zeroColourFrac=0.1;
              double minColourFrac=0.0;
     int siteSizeFactor = 5;
     int edgeWidthFactor = 5;
              System.out.println("Testing PPA mode");  
            a.showHamiltonianParameters();
            a.doPPA();
            System.out.println("Finished PPA");
            a.edgeSet.doDijkstra(a.siteSet.getSiteArray(), a.Hamiltonian.shortDistanceScale);
             
            //a.showEdgeValues("#", 3);
            a.showDijkstraValues("#", 3);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,1);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,2);
          }

// ------------------------------------------------------------------------    
 /* Test Dijkstra
 *
 */
          public static void testDijkstra(islandNetwork a)
          {
              double mindist;
              System.out.println("Testing Dijkstra");
              a.doPPA(); //set some edges
              //a.showEdgeValues("#", 3);
              a.edgeSet.doDijkstra(a.siteSet.getSiteArray(), a.Hamiltonian.shortDistanceScale);
              a.showDijkstraValues("#", 3);
              
          }
    
    
    
    
// ------------------------------------------------------------------------    
 /* Test Dijkstra
 *
 */
          public static void testModel4(islandNetwork a)
          {
            a.setEdgesTest1();
            //a.showEdgeValues("#", 3);
            int i=0;
            int j=5;
            double dh=a.deltaEdgeHamiltonian(i,j, 1.0);
            System.out.println("i,j,dH = "+i+" , "+j+" , "+dh);
              
          } 
    
    
    
// ------------------------------------------------------------------------    
    

      public static void test1(islandNetwork a)
      {    
     

}
// **************************************************************************

      


      


// **************************************************************************
   /** Method of aegean
     * Returns string shoing ellapsed time
     * @param initialtime is time in milliseconds
     */    
  public static void printEllapsedTime(long initialtime)
  {
       long finaltime= System.currentTimeMillis ();
       long time = finaltime - initialtime;
       long ms = time %1000;
       time = time /1000; // time now in sec
       long sec = time % 60;
       time = time /60;  // time now in min
       long minutes = time % 60;
       time = time /60;  // time now in hours
       long hours = time % 24;
       long days = time /24;  
       if (days>0) System.out.print(days+" day");
       if (days>1) System.out.print("s ");
       if (hours>0) System.out.print(hours+" hour");
       if (hours>1) System.out.print("s ");
       if (minutes>0) System.out.print(minutes+" minute");
       if (minutes>1) System.out.print("s ");
       System.out.print(sec+" sec");
       if (sec>1) System.out.print("s ");
       System.out.println();  
  }




}//eo aegean    
    
    

    
    
    
    
    
    
