/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TimGraph.algorithms;

import TimGraph.Coordinate;
import TimGraph.EdgeValue;
import TimGraph.timgraph;
import java.util.ArrayList;

/**
 * Uses vertex positions to calculate edge separations
 * @author time
 */
public class EdgeSeparation {
    
    /**
     * Makes list of distances.
     * <p>Distances are for each edge in the same order as in the given
     * timgraph.
     * @param tg timgraph whose edge separations are to be found
     * @return list of edge distances in the order given in the file
     */
   
    static public ArrayList<Double> calculateDistance2D(timgraph tg){
        int sv, tv=0; // source and target vertex index
        Coordinate scoord, tcoord;
        ArrayList<Double> edgeDistance = new ArrayList();
        Double d;
        for (int stub=0; stub<tg.getNumberStubs(); stub++){
            //EdgeValue edge = tg.getEdgeWeightAll(stub);
            try{
                sv=tg.getVertexFromStub(stub++);
                tv=tg.getVertexFromStub(stub);
                scoord = tg.getVertexPosition(sv);
                tcoord = tg.getVertexPosition(tv);
                d = Coordinate.calcLength(scoord, tcoord);
            } catch (RuntimeException e){
                d=timgraph.DUNSET;
            }
            edgeDistance.add(d);
        }
        return edgeDistance;
    }
}
