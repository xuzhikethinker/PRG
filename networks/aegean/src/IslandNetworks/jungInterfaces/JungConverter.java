package IslandNetworks.jungInterfaces;

import IslandNetworks.islandNetwork;
//import IslandNetworks.Vertex.IslandSiteSet;
//import IslandNetworks.Vertex.VertexValueSelection;

//import IslandNetworks.Edge.IslandEdgeSet;
import java.util.Iterator;
//import javax.swing.*;
//import java.awt.Dimension;
//import java.awt.Stroke;
//import java.awt.Shape;
//import java.awt.BasicStroke;
//import java.awt.event.MouseEvent;

//import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.utils.TypedVertexGenerator;

//import IslandNetworks.*;
import java.util.Set;


/**
 * An a class that adapts an <code>IslandEdgeSet</code> and <code>IslandSiteSet</code> array
 * into a JUNG <code>Graph</code>. 
 * <p>Sites are 
 * <p>Only the edges whose weight (S_i v_i e_{ij}) is above <code>IslandEdgeSet.displayMaximumEdgeWeight</code> are included.
 * <p>It is very much incomplete, and support for only the most
 * rudimentary of display parameters is currently included.
 *
 * @author  David Weir, (minor adaptations TSE 060825)
 */
public class JungConverter {

        final static String JCVERSION = "JA060825";  // 
        
    public DirectedSparseGraph g;
//    private double edgeZeroThreshold;  // do not display edges below this weight
//        private double edgeMinimumThreshold; //display edges below this as smallest weight possible
//        private double edgeMaximumThreshold; // display edges above this as of this weight
 
//        VertexValueSelection vertexValueSelection;
    /**
     * JUNG key for UserDatum storing vertex id of islandNetworks.
     */
    public static final String VID_key = "IN_VIDKey";

    /**
     * JUNG key for UserDatum storing edge id of islandNetworks.
     */
    public static final String EID_key = "IN_EIDKey";

    public static final String X_key = "IN_XKey";
    public static final String Y_key = "IN_YKey";

    islandNetwork inet;
//    IslandSiteSet sites;
//    IslandEdgeSet edges;


    public JungConverter(islandNetwork inetInput) {

//        vertexValueSelection =vvs; // no deep copy
        g = new DirectedSparseGraph();
        inet = inetInput;
//        edgeZeroThreshold = edges.displayZeroEdgeWeight;

        TypedVertexGenerator vg = new TypedVertexGenerator(g.getVertexConstraints());


        // Use a StringLabeller to put the names on vertices
        StringLabeller sl = StringLabeller.getLabeller(g);

        // Use UserDatumNumberVertexValu class to attach numerical attributes to vertices
//        vertexValueSelection.setUserDatumNumberVertexValue();
        UserDatumNumberVertexValue vID = new UserDatumNumberVertexValue(VID_key);
//        UserDatumNumberVertexValue vSize = new UserDatumNumberVertexValue(SIZ_key);
//        UserDatumNumberVertexValue vRank = new UserDatumNumberVertexValue(RANK_key);
//        UserDatumNumberVertexValue vInfl = new UserDatumNumberVertexValue(INFL_key);
//        UserDatumNumberVertexValue vStrIn = new UserDatumNumberVertexValue(STRIN_key);

        // x,y coordinates for each vertex
        UserDatumNumberVertexValue xCoord = new UserDatumNumberVertexValue(X_key);
        UserDatumNumberVertexValue yCoord = new UserDatumNumberVertexValue(Y_key);

        int numSites = inet.siteSet.getNumberSites();

        for(int i=0;i<numSites;i++) {
            Vertex v = vg.create();

            vID.setNumber(v, i);
//            vSize.setNumber(v, sites.getWeight(i));
//            vRank.setNumber(v,sites.getRanking(i)*numSites);
//            vInfl.setNumber(v, sites.getTotalInfluenceWeight(i));
//            vStrIn.setNumber(v, sites.getStrengthIn(i));
            xCoord.setNumber(v, inet.siteSet.getX(i));
            yCoord.setNumber(v, inet.siteSet.getY(i));

            try{
                g.addVertex(v);
                sl.setLabel(v,inet.siteSet.getName(i));
            } catch (StringLabeller.UniqueLabelException e) {
                System.out.println("Error in JungConverter: name conflict. Skipping another vertex with name: \"" +
                    inet.siteSet.getName(i) + "\"");
            }

        }


//        UserDatumNumberEdgeValue vEdgeWeight = new UserDatumNumberEdgeValue(EW_key);
        

        addAllEdges();
        
    }
    
/**
 * Adds all edges whose value is above allowed value specified by associated islandNetwork.
 * <p>Must have no existing edges.
 */
    private void addAllEdges(){
     UserDatumNumberEdgeValue vEdgeID = new UserDatumNumberEdgeValue(EID_key);
     for(int i=0;i<inet.siteSet.getNumberSites();i++) {
            for(int j=0;j<inet.siteSet.getNumberSites();j++) {
                if (inet.edgeSet.displayZeroEdgeWeight>inet.edgeSet.getDisplayVariable(i, j)) continue;
                Vertex v1 = getVertexWithID(i);
                Vertex v2 = getVertexWithID(j);
                DirectedSparseEdge e = new DirectedSparseEdge(v1,v2);
                vEdgeID.setNumber(e, inet.edgeSet.getIndex(i,j));
                g.addEdge(e);
            }
        }
}
/**
 * Adds all edges whose value is above allowed value.
 */
    public void replaceAllEdges(){
        g.removeAllEdges();
        addAllEdges();
}
    
    /**
     * Number of edges in Jung display.
     * @return Number of edges in Jung display
     */
     public int getNumberEdges(){return g.numEdges();}

                
    /**
     * Needed to find correct <code>Vertices</code> to connect with <code>Edges</code>.
     */
    public Vertex getVertexWithID(int i) {

        Iterator it = g.getVertices().iterator();

        while(it.hasNext()) {
            Vertex next = (Vertex)it.next();
            if (next.getUserDatum(VID_key).equals(i))
                return next;
        }
        return null;
    }


    /**
     * Access method to return underlying <code>Graph</code>.
     */
    public islandNetwork getIslandNetwork() {
        return inet;
    }

    /**
     * Access method to return underlying <code>Graph</code>.
     */
    public Graph getGraph() {
        return g;
    }

    /**
     * Access method to return set of jung edges.
     */
    public Set getEdges() {
        return g.getEdges();
    }

    /**
     * Access method to return set of jung vertices.
     */
    public Set getVertices() {
        return g.getVertices();
    }

    /**
     * Gives key used for x coordiantes
     */
    public String getXCoordKey() {
        return X_key;
    }

    /**
     * Gives key used for y coordiantes
     */
    public String getYCoordKey() {
        return Y_key;
    }
    

//   /**
//     * Gives key used for EdgeWeight
//     */
//    public String getEdgeWeightKey() {
//        return EW_key;
//    }




}//eo JungConverter
