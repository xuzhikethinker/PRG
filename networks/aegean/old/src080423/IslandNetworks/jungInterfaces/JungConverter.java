package IslandNetworks.jungInterfaces;

import IslandNetworks.Vertex.IslandSite;
import IslandNetworks.Edge.IslandEdgeSet;
import java.util.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.event.MouseEvent;

import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.utils.*;

import IslandNetworks.*;


/**
 * An a class that adapts an <code>IslandEdgeSet</code> and <code>IslandSite[]</code> array
 * into a JUNG <code>Graph</code>. It is very much incomplete, and support for only the most
 * rudimentary of display parameters is currently included.
 *
 * @author  David Weir, (minor adaptations TSE 060825)
 */
public class JungConverter {

        final static String JCVERSION = "JA060825";  // 
        
    public DirectedSparseGraph g;
        private double edgeZeroThreshold;  // do not display edges below this weight
//        private double edgeMinimumThreshold; //display edges below this as smallest weight possible
//        private double edgeMaximumThreshold; // display edges above this as of this weight
 
    // UserData keys
    public static final String ID_key = "IN_IDKey";
    public static final String SIZ_key = "IN_SIZKey";
    public static final String EW_key = "IN_EWKey";

    public static final String X_key = "IN_XKey";
    public static final String Y_key = "IN_YKey";

    IslandSite[] sites;


    public JungConverter(IslandEdgeSet es, IslandSite[] sitesInput) {

        g = new DirectedSparseGraph();
        sites = sitesInput;
                edgeZeroThreshold = es.displayZeroEdgeWeight;

        TypedVertexGenerator vg = new TypedVertexGenerator(g.getVertexConstraints());


        // Use a StringLabeller to put the names on vertices
        StringLabeller sl = StringLabeller.getLabeller(g);

        // Use this provided class to attach numerical attributes to vertices
        UserDatumNumberVertexValue vSize = new UserDatumNumberVertexValue(SIZ_key);
        UserDatumNumberVertexValue vID = new UserDatumNumberVertexValue(ID_key);

        // x,y coordinates for each vertex
        UserDatumNumberVertexValue xCoord = new UserDatumNumberVertexValue(X_key);
        UserDatumNumberVertexValue yCoord = new UserDatumNumberVertexValue(Y_key);

        int numSites = sites.length;

        for(int i=0;i<numSites;i++) {
            Vertex v = vg.create();

            vID.setNumber(v, i);
            vSize.setNumber(v, sites[i].getDisplaySize());
            xCoord.setNumber(v, sites[i].getX());
            yCoord.setNumber(v, sites[i].getY());

            try{
                g.addVertex(v);
                sl.setLabel(v,sites[i].getName());
            } catch (StringLabeller.UniqueLabelException e) {
                System.out.println("Error: name conflict. Skipping another vertex with name: \"" +
                    sites[i].getName() + "\"");
            }

        }


        UserDatumNumberEdgeValue vEdgeWeight = new UserDatumNumberEdgeValue(EW_key);

        // Can only instantiate the edges once all the vertices are in place
        for(int i=0;i<numSites;i++) {
            for(int j=0;j<numSites;j++) {
                Vertex v1 = getVertexWithID(i);
                Vertex v2 = getVertexWithID(j);

                DirectedSparseEdge e = new DirectedSparseEdge(v1,v2);

                vEdgeWeight.setNumber(e,es.getEdgeValue(i,j)*sites[i].getWeight());
                
                g.addEdge(e);
            }
        }
    }



                
    /**
     * Needed to find correct <code>Vertices</code> to connect with <code>Edges</code>.
     */
    public Vertex getVertexWithID(int i) {

        Iterator it = g.getVertices().iterator();

        while(it.hasNext()) {
            Vertex next = (Vertex)it.next();
            if (next.getUserDatum(ID_key).equals(i))
                return next;
        }
        return null;
    }


    /**
     * Access method to return underlying <code>Graph</code>.
     */
    public Graph getGraph() {
        return g;
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

   /**
     * Gives key used for EdgeWeight
     */
    public String getEdgeWeightKey() {
        return EW_key;
    }




}//eo JungConverter
