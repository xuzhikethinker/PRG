/*
 * TestMolloyReed.java
 *
 * Created on 05 March 2007, 15:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test;

import TimGraph.timgraph;

/**
 *
 * @author time
 */
public class TestMolloyReed {
    
    /** Creates a new instance of TestMolloyReed */

    public TestMolloyReed() {
        
        
  
    }
 /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        System.out.println("\n Testing Molloy-Reed constructor in timgraph ***");
        String namert ="TestMR";
        String dname = "/PRG/networks/timgraph/";
        int infol=0;
        int outputc=255;
        boolean makeDirected=false;
        boolean makeLabelled=false;
        boolean makeWeighted=false;
        int maxVertices=11;
        int numberEdges=11;
        int [] stubList = new int [numberEdges];
        int numberVertices =5;
        for (int v=0; v<numberEdges; v++) stubList[v]=v%numberVertices;
        timgraph tg = new timgraph(namert, dname, infol, outputc, makeDirected, makeLabelled, makeWeighted, maxVertices, stubList);
        tg.printNetwork(true);
    }
}
