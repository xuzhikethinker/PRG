/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package betweeness;

import TimGraph.timgraph;
import java.lang.String;
import java.util.ArrayList;


/**
 * Calculates betweenness for weighted graphs.
 * @author time
 */
public class Betweenness {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

    }



    static public void calcVertexBetweenness(int [] vertexBetweenness,  ArrayList<ArrayList<Integer>> indexToLabel){
          int v=timgraph.IUNSET;
          for (ArrayList<Integer> path: indexToLabel){
              for (int i=1; i<path.size()-1; i++) {
                  v=path.get(i);
                  if (v<0 || v>=vertexBetweenness.length) throw new RuntimeException("*** Found illegal vertex label "+v);
                  vertexBetweenness[v]++;
              }
          }

    }
}
