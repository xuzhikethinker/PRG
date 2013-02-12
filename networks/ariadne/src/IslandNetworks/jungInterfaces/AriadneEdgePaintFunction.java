/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

import IslandNetworks.islandNetwork;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import java.awt.Color;
import java.awt.Paint;

/**
 * Colours Edges.
 * @author time
 */
public class AriadneEdgePaintFunction 
 implements EdgePaintFunction
{
    private final islandNetwork INET;
    private final Object ISLANDCLUSTERKEY;// = "ISLANDCLUSTERKEY";
    private final Color REMOVEDCOLOUR; 
            
    public AriadneEdgePaintFunction (islandNetwork inet, Object islandClusterKey, Color removedColour){
        INET=inet;
        ISLANDCLUSTERKEY=islandClusterKey;
        REMOVEDCOLOUR=removedColour;
    }
    
            public Paint getDrawPaint(Edge e) {
                Integer edgeID =  (Integer) e.getUserDatum(JungConverter.EID_key);
                double edgeDisplayValue = INET.edgeSet.getEdgeDisplaySize(edgeID );
                if (edgeDisplayValue  < INET.edgeSet.zeroColourFrac) return REMOVEDCOLOUR;
                Color k = (Color) e.getUserDatum(ISLANDCLUSTERKEY);
                if (k != null)
                    return k;
                return Color.BLACK;
            }
            public Paint getFillPaint(Edge e)
            {
                return null;
            }

}
    

