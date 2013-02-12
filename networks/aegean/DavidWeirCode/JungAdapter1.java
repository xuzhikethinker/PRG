package IslandNetworks;

import java.util.*;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.BasicStroke;
import edu.uci.ics.jung.visualization.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.utils.*;
import java.awt.event.MouseEvent;


/**
 * An a class that adapts an <code>IslandEdgeSet</code> and <code>IslandSite[]</code> array
 * into a JUNG <code>Graph</code>. It is very much incomplete, and support for only the most
 * rudimentary of display parameters is currently included.
 *
 * @author	David Weir
 */
class JungAdapter {

	private DirectedSparseGraph g;

	// UserData keys
	public static final String ID_key = "JA_IDKey";
	public static final String SIZ_key = "JA_SIZKey";
	public static final String EW_key = "JA_EWKey";

	public static final String X_key = "JA_XKey";
	public static final String Y_key = "JA_YKey";

	IslandSite[] sites;


	public JungAdapter(IslandEdgeSet es, IslandSite[] sitesInput) {

		g = new DirectedSparseGraph();
		sites = sitesInput;

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
			vSize.setNumber(v, sites[i].displaySize);
			xCoord.setNumber(v, sites[i].X);
			yCoord.setNumber(v, sites[i].Y);

			try{
				g.addVertex(v);
				sl.setLabel(v,sites[i].name);
			} catch (StringLabeller.UniqueLabelException e) {
				System.out.println("Error: name conflict. Skipping another vertex with name: \"" +
					sites[i].name + "\"");
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
	 * Remove edges from graph which have a weight below <code>threshold</code>.
	 */
	public void cleanGraph(double threshold) {
		HashSet<Edge> dumpEdges = new HashSet<Edge>();

		UserDatumNumberEdgeValue weights = new UserDatumNumberEdgeValue(EW_key);

		Iterator it = g.getEdges().iterator();
		while(it.hasNext()) {
			Edge currentEdge = (Edge)it.next();
			double currentNumber = weights.getNumber(currentEdge).doubleValue();
			if(currentNumber < threshold)
				dumpEdges.add(currentEdge);
		}

		GraphUtils.removeEdges(g,dumpEdges);

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
	 * Returns a <code>JPanel</code> which will display the network. This is a drop-in
	 * replacement for <code>NetworkPicture</code>.
	 */
	public JPanel getViewer(int width, int height)  {
		PluggableRenderer pr = new PluggableRenderer();

		// Display the site name at each vertex
		pr.setVertexStringer(StringLabeller.getLabeller(g));

		// InterpolatingVertexSizeFunction assigns sizes to each vertex within the specified bounds (here 4 and 10)
		pr.setVertexShapeFunction(new EllipseVertexShapeFunction(new InterpolatingVertexSizeFunction(new UserDatumNumberVertexValue(SIZ_key), 4,10),
			new ConstantVertexAspectRatioFunction((float)1.0)));

		// Our own class to choose the appearance of edges. Only edges above the given threshold value are displayed.
		EdgeManagement edgeFunctions = new EdgeManagement(new UserDatumNumberEdgeValue(EW_key));

		pr.setEdgeStrokeFunction(edgeFunctions);
		pr.setEdgeArrowFunction(edgeFunctions);

		// Remove edges that fall below our threshold this time; this threshold could be passed as an argument to getViewer
		cleanGraph(0.005);

		VisualizationViewer vv = new VisualizationViewer(new RealisticLayout(g,new UserDatumNumberVertexValue(X_key),
			new UserDatumNumberVertexValue(Y_key)),pr, new Dimension(width,height));

		vv.addGraphMouseListener(new ClickableVertexListener(new UserDatumNumberVertexValue(ID_key),sites));

		return (JPanel)vv;

	}
}




/**
 * Class for displaying edges with scaled sizes, and smaller arrowheads
 *
 * @author	David Weir
 */
class EdgeManagement implements EdgeStrokeFunction, EdgeArrowFunction {

	UserDatumNumberEdgeValue edgeWeight;

	DirectionalEdgeArrowFunction arrowFunction;


	public EdgeManagement(UserDatumNumberEdgeValue udnev) {
		arrowFunction = new DirectionalEdgeArrowFunction(5,5,2);
		edgeWeight = udnev;
	}


	public static boolean isSelfReferential(Edge e) {
		Pair ends = e.getEndpoints();
		return ends.getFirst().equals(ends.getSecond());
	}		


	public Stroke getStroke(Edge e) {
		double weight = edgeWeight.getNumber(e).doubleValue();

		return new BasicStroke((float)weight * 2.5f);
	}

	public Shape getArrow(Edge e) {
		return arrowFunction.getArrow(e);
	}

}



/**
 * A class to display the the network based on the assigned coordinates.
 * <p>
 * NB: This will produce deprecation warnings when compiled; this is because
 * the AbstractLayout parent class implements various methods which are no longer
 * used. Do not worry about it, this is an internal inconsistency in JUNG.
 *
 * @author	David Weir
 */
class RealisticLayout extends AbstractLayout {
	UserDatumNumberVertexValue xCoord, yCoord;


	public RealisticLayout(Graph g, UserDatumNumberVertexValue x, UserDatumNumberVertexValue y) {
		super(g);
		xCoord = x;
		yCoord = y;
	}


	/**
	 * Assign coordinates from the appropriate <code>UserDatum</code> to
	 * the current <code>Vertex</code>.
	 */
	public void initialize_local_vertex(Vertex v) {
		Coordinates coord = getCoordinates(v);
		coord.setX(xCoord.getNumber(v).doubleValue());
		coord.setY(yCoord.getNumber(v).doubleValue());
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
}



/**
 * Class for popping up an information dialog when a vertex is double-clicked.
 *
 * @author	David Weir
 */
class ClickableVertexListener implements GraphMouseListener {

	IslandSite[] sites;
	UserDatumNumberVertexValue idKey;


	public ClickableVertexListener(UserDatumNumberVertexValue udnvv, IslandSite[] s) {
		sites = s;
		idKey = udnvv;
	}


	public void graphClicked(Vertex v, MouseEvent me) {
		// Wait for double click
		if (me.getClickCount() == 2) {
			// work out where to look in the sites[] array
			int siteID = idKey.getNumber(v).intValue();


			// build the message from all the fields
			String vertexMessage = "";
			for(int i=0;i<=18;i++)
				vertexMessage = vertexMessage + 
					sites[siteID].dataName(i) + ": " + sites[siteID].toString(i) + "\n";

			JOptionPane.showMessageDialog(me.getComponent(),vertexMessage,
				"Vertex information", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void graphPressed(Vertex v, MouseEvent me) {
		// Don't care
	}

	public void graphReleased(Vertex v, MouseEvent me) {
		// Don't care
	}
}
