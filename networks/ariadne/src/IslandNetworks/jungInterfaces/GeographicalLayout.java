package IslandNetworks.jungInterfaces;

import IslandNetworks.*;

//import java.awt.geom.Point2D;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.AbstractLayout;
//import edu.uci.ics.jung.visualization.transform.Transformer;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;
import java.awt.Dimension;


/**
 * A class to display the the network based on the assigned coordinates.
 * <p>
 * NB: This will produce deprecation warnings when compiled; this is because
 * the AbstractLayout parent class implements various methods which are no longer
 * used. Do not worry about it, this is an internal inconsistency in JUNG.
 *
 * @author  David Weir
 */
public class GeographicalLayout extends AbstractLayout {
//    UserDatumNumberVertexValue xCoord, yCoord;
//    public static final String X_key = "JA_XKey";
//    public static final String Y_key = "JA_YKey";
 private String xKey= "IN_XKey";
 private String yKey= "IN_YKey";

  private double scale;  
  private double shift; 

// Would be better to us a JUNG transformer private Transformer transformer;
  
  /*
     * Constructor.
     *@param g graph
     *@param xkey key for UserDatum used for x coordinate
     *@param ykey key for UserDatum used for y coordinate
     * @param t JUNG transformer for mapping graph to screen coordinates
     */
//(Graph g, UserDatumNumberVertexValue x, UserDatumNumberVertexValue y) 
    public GeographicalLayout(Graph g, String xkey, String ykey, double scale, double shift) {
        super(g);
        xKey = xkey;
        yKey = ykey;
        this.scale=scale;
        this.shift=shift;
		
    }

    //(Graph g, UserDatumNumberVertexValue x, UserDatumNumberVertexValue y) 
    public GeographicalLayout(Graph g, String xkey, String ykey) {
        super(g);
        xKey = xkey;
        yKey = ykey;
        Dimension d = getCurrentSize();
        double size = Math.min(d.height,d.width);
        scale=size*0.8;
        shift=size*0.1;
        	
    }


    /**
     * Assign coordinates from the appropriate <code>UserDatum</code> to
     * the current <code>Vertex</code>.
     */
    public void initialize_local_vertex(Vertex v) {
        Coordinates coord = getCoordinates(v);
        UserDatumNumberVertexValue xCoord = new UserDatumNumberVertexValue(xKey);
        UserDatumNumberVertexValue yCoord = new UserDatumNumberVertexValue(yKey);
        coord.setX(xCoord.getNumber(v).doubleValue() *scale+shift);
        coord.setY(yCoord.getNumber(v).doubleValue() *scale+shift);
    }



    public void advancePositions() {
        // Do nothing - this is not an incremental layout algorithm.
    }



    public boolean incrementsAreDone() {
        return true;
    }


    public boolean isIncremental() {
        return false;
    }
}// eo GeographicalLayout
