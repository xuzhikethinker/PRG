/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.visualization.PickedState;
import java.awt.Color;
import java.awt.Paint;

   /**
     * Controls the colour for each vertex.
     * @author time
     */
    public final class AriadneVertexPaintFunction  
            implements VertexPaintFunction
    {
        PickedState ps;
        private final Object ISLANDCLUSTERKEY;// = "ISLANDCLUSTERKEY";

        public AriadneVertexPaintFunction (PickedState ps, Object islandClusterKey){
            this.ps=ps;
            ISLANDCLUSTERKEY=islandClusterKey;
        }

        public void setPickedState(PickedState ps){this.ps=ps;}
        
        public Paint getFillPaint(Vertex v) {
                Color k = (Color) v.getUserDatum(ISLANDCLUSTERKEY);
                if (k != null)
                    return k;
                return Color.white;
            }

            public Paint getDrawPaint(Vertex v) {
                if(ps.isPicked(v)) {
                    return Color.cyan;
                } else {
                    return Color.BLACK;
                }
            }
        }

