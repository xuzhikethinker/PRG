/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

import IslandNetworks.islandNetwork;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

/**
 * Defines edges sizes in jung display.
 * <p>Minimum and Maximum display sizes are references 
 * so display should update when these values are 
 * changed without call to these routines.  
 * @author time
 */
public class AriadneEdgeStrokeFunction 
        implements EdgeStrokeFunction
{
    private final islandNetwork INET;            
    private final Object ISLANDCLUSTERKEY;// = "ISLANDCLUSTERKEY";
    private final Color PERCREMOVEDCOLOUR; 
    protected Stroke THIN; // = new BasicStroke(0);
    protected Stroke THICK; //= new BasicStroke(maxEdgeDisplaySize);
    private Float minEdgeDisplaySize = 0.0f; // Must be non-negative.  If zero it displays the thinnest possible line.
    private Float maxEdgeDisplaySize = 10.0f; // maximum size of line to use
    
    public AriadneEdgeStrokeFunction(islandNetwork inet, Object islandClusterKey, Color percRemovedColour, Float  minSize, Float  maxSize){
                    INET=inet;
                    ISLANDCLUSTERKEY=islandClusterKey;
                    PERCREMOVEDCOLOUR=percRemovedColour;
                    setEdgeSizes(minSize, maxSize);
                }
                
                /**
                 * Change edge sizes.
                 * @param minSize
                 * @param maxSize
                 */
                public void setEdgeSizes(Float minSize, Float maxSize){
                    if ((minSize<0) || (minSize>maxSize)) THIN = new BasicStroke(1.0f);
                        else THIN = new BasicStroke(minSize);
                    if (maxEdgeDisplaySize<0) maxEdgeDisplaySize=1.0f;
                    else maxEdgeDisplaySize=maxSize;
                    THICK= new BasicStroke(maxEdgeDisplaySize);
                }

                /**
                 * Maximum edge size.
                 */
                public float getMaxEdgeSize(){ return maxEdgeDisplaySize;
                }
                /**
                 * Minimum edge size.
                 */
                public float getMinEdgeSize(){ return minEdgeDisplaySize;
                }

                public Stroke getStroke(Edge e)
                {
                    Integer edgeID =  (Integer) e.getUserDatum(JungConverter.EID_key);
                    double edgeDisplayValue = INET.edgeSet.getEdgeDisplaySize(edgeID );
                    Color c = (Color) e.getUserDatum(ISLANDCLUSTERKEY);
                    if (c == PERCREMOVEDCOLOUR) return THIN;
                    else 
                    {
                        if (edgeDisplayValue<INET.edgeSet.minColourFrac) return THIN;
                        return (new BasicStroke((float) (edgeDisplayValue*maxEdgeDisplaySize)) );                        
                    }                        
                }
}

