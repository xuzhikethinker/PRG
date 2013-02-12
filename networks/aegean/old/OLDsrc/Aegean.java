/*
 * DataInput.java
 *
 * Created on 31 March 2004, 18:27
 * last update on Friday, February 25, 2005 at 18:25
 */

/**
 *
 * @author  time 
 */

import java.io.*;
import java.util.Date;
import javax.swing.*;          
import java.awt.*;
import java.awt.event.*;
//import java.io.File;
//import java.io.FileInputStream;
//import cern.colt.io.Converting;
//import TextReader;
//import islandNetwork;

public class Aegean {
    
    //islandNetwork network;
    
    
        
        
    /** Creates a new
     * 
     * instance of aegean */
    public Aegean() {    
        
        //dataread=false;
        

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Date date = new Date();
        System.out.println(date);
        int res;
        islandNetwork a = new islandNetwork();
        
        //a.drawInputWindow();
        
        
        res = a.Parse(args);
        if (res>0) {System.out.println("Command line argument failure - return code "+res);
            return;
            }
        
        if (a.inputGUI) a.runGUI();
        else {
            
            res = a.getSiteData();
            if (res>0) {
                System.out.println("Data reading failed - return code "+res+" trying full distance data read");
                res = a.getSiteDistanceData();
                if (res>0) {
                    System.out.println("Position data reading failed - return code "+res);
                    return;
                }
            }
            a.showSiteValuesPos("#", 3);
            a.showDistanceValues("#", 3);
            //        a.showScaledDistanceValues();
            //       a.showPotentialDistanceValues();
            //       a.showdHValues(1.0);
            System.out.println("Data reading OK, number of sites is "+a.numberSites);if (a.updateMode == 0) testPPA(a);
            else {System.out.println("Update Mode is MC");
            // test5(a);
            };
        }
        
        
    }//eo main



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
              a.DisplayMaxEdgeScale=1;   //<=0 then display edges relative to largest
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
                     a.betaH=beta;
                     a.vertexSweep();
 //                  System.out.println("Finished vertex sweep "+i);
                 }
                 
                 if (a.infolevel>1) System.out.println("b="+beta+" : ");
                 a.edgeUR.reset();
                 for (int i=0; i<estot; i++) 
                 {
                     a.betaH=beta;
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
                 if ((a.vertexUR.totalfrac<0.001) && (a.edgeUR.totalfrac<0.01) ) break;
                 
              }
              System.out.println("\nFinal beta "+beta);
              System.out.println("**********************************************");
              a.showNetworkStatistics("#",System.out,3);
              a.minColourFrac=0.0;
              System.out.println("Minimum Fraction for coloured edge = "+minColourFrac);
              a.calcNetworkStats();
              a.showColourValues("#", 3);
              System.out.println("  Edge stats: "+a.edgeUR.toString());
              System.out.println("Vertex stats: "+a.vertexUR.toString());
              a.doDijkstra();
              a.showEdgeValues("#", 3);
              a.showDijkstraValues("#", 3);
              a.minColourFrac=0.0;
              a.zeroColourFrac=0.1;
              System.out.println("Scale for largest vertices in .net file= "+a.DisplayMaxVertexScale);
              System.out.println("Scale for maximum edges in .net file = "+a.DisplayMaxEdgeScale);
              System.out.println("Minimum Fraction for coloured edge in .net file = "+minColourFrac);
              a.calcNetworkStats();
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,true);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor, minColourFrac,zeroColourFrac,false);
              a.FileOutputBareNetwork("#");
              a.FileOutputNetworkStatistics("#", 3);
              islandNetwork.PictureWindow PW;
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
                     a.betaH=beta;
                     a.vertexSweep();
 //                  System.out.println("Finished vertex sweep "+i);
                 }
                 
                 if (a.infolevel>1) System.out.println("b="+beta+" : "+a.vertexUR.toString());
                 if (a.vertexUR.totalfrac<0.001) break;
              }
              System.out.println("**********************************************");
              a.showNetworkStatistics("#",System.out,3);
              System.out.println("Minimum Fraction for coloured edge = "+minColourFrac);
              a.calcNetworkStats();
              a.showColourValues("#", 3);
              System.out.println(a.vertexUR.toString());
              minColourFrac=0.5;
              System.out.println("Minimum Fraction for coloured edge in .net file = "+minColourFrac);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,true);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,false);
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
                     a.betaH=beta;
                     a.edgeSweep();
 //                  System.out.println("Finished edge sweep "+i);
                 }
                 
                 System.out.println("b="+beta+" : "+a.edgeUR.toString());
                 if (a.edgeUR.totalfrac<0.01) break;
              }
              System.out.println("**********************************************");
              a.showNetworkStatistics("#",System.out,3);
              a.minColourFrac=0.0;
              minColourFrac=0.0;
              System.out.println("Minimum Fraction for coloured edge = "+minColourFrac);
              a.calcNetworkStats();
              a.showColourValues("#", 3);
              System.out.println(a.edgeUR.toString());
              minColourFrac=0.5;
              System.out.println("Minimum Fraction for coloured edge in .net file = "+minColourFrac);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,true);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,false);
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
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,true);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,false);
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
            a.doDijkstra();
            a.showEdgeValues("#", 3);
            a.showDijkstraValues("#", 3);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,true);
              a.FileOutputNetwork("#",siteSizeFactor,edgeWidthFactor,minColourFrac,zeroColourFrac,false);
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
              a.showEdgeValues("#", 3);
              a.doDijkstra();
              a.showDijkstraValues("#", 3);
              
          }
    
    
    
    
// ------------------------------------------------------------------------    
 /* Test Dijkstra
 *
 */
          public static void testModel4(islandNetwork a)
          {
            a.setEdgesTest1();
            a.showEdgeValues("#", 3);
            int i=0;
            int j=5;
            double dh=a.deltaEdgeHamiltonian(i,j, 1.0);
            System.out.println("i,j,dH = "+i+" , "+j+" , "+dh);
              
          } 
    
    
    
// ------------------------------------------------------------------------    
    

      public static void test1(islandNetwork a){    
     
        //a.showData();
        double escale=100.0;
        a.setHamiltonianParameters(0.0, 0.5, 1.0, 4.0, 1.0, 1.0, 1.0, escale);
        a.showHamiltonianParameters();
//        a.showEdgeValues();
//        a.showDistanceValues();
        System.out.println(a.edgePotential1(escale,1.0));
        System.out.println(a.edgePotential1(escale*2.0,1.0));
        int i =0;
        System.out.println("potential from "+a.siteName[i]);
        int j;
        j= 1;
        System.out.println("            to "+a.siteName[j]+" distance "+a.distValue[i][j]);
        System.out.println(a.deltaEdgeHamiltonian(i, j, 0.0));
        System.out.println(a.deltaEdgeHamiltonian(i, j, 0.5));
        System.out.println(a.deltaEdgeHamiltonian(i, j, 1.0));
        j = 5;
        System.out.println("            to "+a.siteName[j]+" distance "+a.distValue[i][j]);
        System.out.println(a.deltaEdgeHamiltonian(i, j, 0.0));
        System.out.println(a.deltaEdgeHamiltonian(i, j, 0.5));
        System.out.println(a.deltaEdgeHamiltonian(i, j, 1.0));
        j = 12;
        System.out.println("            to "+a.siteName[j]+" distance "+a.distValue[i][j]);
        System.out.println(a.deltaEdgeHamiltonian(i, j, 0.0));
        System.out.println(a.deltaEdgeHamiltonian(i, j, 0.5));
        System.out.println(a.deltaEdgeHamiltonian(i, j, 1.0));
        System.out.println();
        System.out.println(a.vertexPotential1(1.0,0.0));
        System.out.println(a.vertexPotential1(1.0,0.5));
        System.out.println(a.vertexPotential1(1.0,1.0));
        i=0;
        System.out.println("potential at "+a.siteName[i]);        
        System.out.println(a.deltaVertexHamiltonian(i, 0.0));
        System.out.println(a.deltaVertexHamiltonian(i, 0.5));
        System.out.println(a.deltaVertexHamiltonian(i, 1.0));
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
    
    

    
    
    
    
    
    
