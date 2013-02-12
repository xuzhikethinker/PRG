/*
 * TestAdjacencyMatrix.java
 *
 * Created on 24 January 2007, 12:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test;

import TimGraph.AdjacencyMatrix;
/**
 *
 * @author time
 */
public class TestAdjacencyMatrix {
    
    /** Creates a new instance of TestAdjacencyMatrix */
    public TestAdjacencyMatrix() {
    }
    
   /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       TestAdjacencyMatrix TAM = new TestAdjacencyMatrix();
       if (args.length>0) TAM.doTest(Integer.parseInt(args[0])); 
       else for (int i=0; i<6; i++) TAM.doTest(i);
    }
    
    

    /**
     * doTest tests different graphs and their adjacency matrices.
     * @param type slects type of graph to test
     */
    public void doTest(int type)
    {
        int nvertices=16;
        double averageDegree=1.5;
//        AdjacencyMatrix AM = new AdjacencyMatrix(nvertices, averageDegree);
//        switch (type )
//        {
//            case 5: System.out.println("*** 3D Torus ");  AM.make3DTorus(); break;
//            case 4: System.out.println("*** 2D Torus ");  AM.make2DTorus(); break;
//            case 3: System.out.println("*** Circle ");  AM.makeCircle(); break;
//            case 2: System.out.println("*** BA ");  AM.makeBA(); break;
//            case 1: System.out.println("*** Random Exponential ");  AM.makeRandomExponential(); break;
//            case 0: 
//            default: System.out.println("*** Random ER ");  AM.makeER(); break;
//        }
//        
//        System.out.println("--- Adjacency Matrix ---");
//        AM.printMatrix();
//        System.out.println("--- Network ---");
//        AM.printNetwork();
        
    }
}
