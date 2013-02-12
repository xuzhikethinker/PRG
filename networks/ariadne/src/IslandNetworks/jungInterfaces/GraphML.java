/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

import IslandNetworks.ClusteringWindow;
import IslandNetworks.islandNetwork;
import TimGraph.Coordinate;
import TimGraph.io.GraphMLGenerator;
import TimUtilities.JavaColours;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

/**
 * Converts JUNG visualisation of a graph to a graphml file suitable for visone.
 * @author time
 */
public class GraphML {

   /** Prints Network in graphML format for visone.
     * <br><i>filenamecomplete</i><tt>.graphml</tt> file produced.
     * @param filenamecomplete full file name including any directory strucure except for the .graphml extension
     * @param jc jung representation of an islandNetwork
     * @param vv jung visualisation of an islandNetwork
     * @param  noPercolationRemovedEdges true if do not want edges displayed as light gray as removed by percolation.
     * @param messagesOn true if want messages
     */
    static public void FileOutputGMLNetwork(String filenamecomplete, JungConverter jc, VisualizationViewer vv, boolean noPercolationRemovedEdges, 
            boolean messagesOn) 
    {    
       if (messagesOn) System.out.println("Attempting to write network in graphML format to "+ filenamecomplete);
            
       PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            output(PS, filenamecomplete, jc, vv, noPercolationRemovedEdges);
            try
            { 
               fout.close ();
               if (messagesOn) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.err.println("File Error "+e);}

        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+ filenamecomplete+": "+e);
            return;
        }
        return;
    }

    
    /**
     * From a jung visualisation, produces a graphml file suitable for visone.
     * @param PS Printstream for the output
     * @param gname name of graph
     * @param jc jung representation of an islandNetwork
     * @param vv jung visualisation of an islandNetwork
     * @param  noPercolationRemovedEdges true if do not want edges displayed as light gray as removed by percolation.
     */
        static public void output(PrintStream PS, String gname, JungConverter jc, VisualizationViewer vv, boolean noPercolationRemovedEdges){
        GraphMLGenerator gml = new GraphMLGenerator();

        // PS.println("comments");
        gml.printInitialLines(PS, gname, true );
        
        Layout layout = vv.getGraphLayout();
        PluggableRenderer pr = (PluggableRenderer) vv.getRenderer();
        VertexPaintFunction vpf = pr.getVertexPaintFunction();
        VertexShapeFunction vsf = pr.getVertexShapeFunction();
        islandNetwork inet = jc.getIslandNetwork();
          
        
        // Do vertices
        String nodeShape=GraphMLGenerator.DEFAULT_SHAPE;
        Set vertexSet = jc.getVertices();
        int numberVertices = vertexSet.size();
        for (Object vo:vertexSet){
          Vertex v= (Vertex) vo;
          Point2D position = layout.getLocation(v);    
          int nodeNumber= (Integer) v.getUserDatum(JungConverter.VID_key);
          Color c = (Color) vpf.getFillPaint(v);
          Rectangle rect= vsf.getShape(v).getBounds();
          double w=rect.width;
          double h=rect.height;
          String vname = inet.siteSet.getName(nodeNumber);
          gml.printNodeColoured(PS, nodeNumber, numberVertices,
                  position.getX(),  position.getY(), w, h, 
                                  vname, JavaColours.RGB(c), nodeShape);
        }
        
        // do edges
        Set edgeSet=jc.getEdges();
        EdgePaintFunction epf = pr.getEdgePaintFunction();
        EdgeStrokeFunction esf = pr.getEdgeStrokeFunction();
        double arcFactor=0.1;
        boolean targetArrowsOn=true;
        String ename = ""; 
          for (Object eo:edgeSet){
          Edge e= (Edge) eo;
          Color ec = (Color) epf.getDrawPaint(e);
          if ((noPercolationRemovedEdges) && (ec==ClusteringWindow.PERCREMOVEDCOLOUR) ) continue;
          // this edge not removed by percolation (or whatever) or wanted all edges displayed.
          int edgeNumber = (Integer) e.getUserDatum(JungConverter.EID_key);
          int source = inet.edgeSet.getSource(edgeNumber);
          int target = inet.edgeSet.getTarget(edgeNumber);
          Coordinate sourceCoordinate = new Coordinate(inet.siteSet.getSite(target).X, inet.siteSet.getSite(target).Y);
          Coordinate targetCoordinate = new Coordinate(inet.siteSet.getSite(target).X, inet.siteSet.getSite(target).Y);
          BasicStroke es = (BasicStroke) esf.getStroke(e);
          float width = es.getLineWidth();
          //gml.printEdgeColoured(PS, edgeNumber, source, target, sourceCoordinate, targetCoordinate, ename, width, true, JavaColours.RGB(ec));
          gml.printArcEdge(PS, edgeNumber, source, target,
                  sourceCoordinate, targetCoordinate, arcFactor,
                  ename, width, targetArrowsOn, ename);
        }
          
        gml.printFinalLines(PS);  
    }

    
}
