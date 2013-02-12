/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

import IslandNetworks.islandNetwork;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractVertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexAspectRatioFunction;
import edu.uci.ics.jung.graph.decorators.VertexSizeFunction;
import java.awt.Shape;

/**
     * Controls the shape, size, and aspect ratio for each vertex.
     * Taken from <tt>PluggableRendererDemo.java</tt>
     * @author time
     */
    public final class AriadneVertexShapeSizeAspect
    extends AbstractVertexShapeFunction 
    implements VertexSizeFunction, VertexAspectRatioFunction
    {
        protected boolean stretch = false;
        protected boolean scale = true;
        protected boolean funny_shapes = false;
        protected double vertexScaling;
        private final islandNetwork INET;            
    
        /**
         * COntrols vertex shap and size.
         * @param inet island network
         * @param vertexScaling scaling factor.
         */
        public AriadneVertexShapeSizeAspect(islandNetwork inet, int vertexScaling)
        {
            INET=inet;
            setScaling(vertexScaling);
            setSizeFunction(this);
            setAspectRatioFunction(this);
        }
        
        public void setScaling(double vertexScaling)
        {
            if (vertexScaling>0){
                           this.vertexScaling = vertexScaling;
            setScaling(true);                
            }
            else setScaling(false);
        }
        
        public void setStretching(boolean stretch)
        {
            this.stretch = stretch;
        }
        
        public void setScaling(boolean scale)
        {
            this.scale = scale;
        }
        
        public void useFunnyShapes(boolean use)
        {
            this.funny_shapes = use;
        }
        
        /**
         * Gets the display size to use for a vertex.
         * <p>Requires that siteSet has the sizes already set.
         * @param v jung vertex reference
         * @return vertex size for jung display
         */
        public int getSize(Vertex v)
        {
            if (!scale) return 8;
            Integer s = (Integer) v.getUserDatum(JungConverter.VID_key); // site number
            int vs =(int)( INET.siteSet.getDisplaySize(s) + 0.5);  
            //System.out.println(vs+", "+inet.siteSet.getDisplaySize(s));
            if (vs<0) return 0;
            //if (vs>MAXVERTEXDISPLAYSIZE) return MAXVERTEXDISPLAYSIZE;
            return vs;
        
        }
        
        
        public float getAspectRatio(Vertex v)
        {
            if (stretch)
                return (float)(v.inDegree() + 1) / (v.outDegree() + 1);
            else
                return 1.0f;
        }
        
        public Shape getShape(Vertex v)
        {
            if (funny_shapes)
            {
                if (v.degree() < 5)
                {
                    int sides = Math.max(v.degree(), 3);
                    return factory.getRegularPolygon(v, sides);
                }
                else
                    return factory.getRegularStar(v, v.degree());
            }
            else
                return factory.getEllipse(v);
        }

}
