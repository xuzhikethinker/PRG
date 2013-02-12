/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*
* Adapted by TSE from 
* C:\JAVA\JUNG\src\samples\graph\ClusteringDemo.java in jung 1.7.6 release
*/
package IslandNetworks;

import IslandNetworks.Vertex.IslandSiteSet;
import IslandNetworks.Edge.IslandEdgeSet;
import IslandNetworks.jungInterfaces.JungConverter;
import IslandNetworks.jungInterfaces.GeographicalLayout;
import IslandNetworks.jungInterfaces.EdgePercolationClusterer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberEdgeValue;

import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.MultiPickedState;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.subLayout.CircularSubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

/**
 * This simple app demonstrates how one can use our algorithms and visualization libraries in unison.
 * In this case, we generate use the Zachary karate club data set, widely known in the social networks literature, then
 * we cluster the vertices using an edge-betweenness clusterer, and finally we visualize the graph using
 * Fruchtermain-Rheingold layout and provide a slider so that the user can adjust the clustering granularity.
 * @author Scott White
 */
public class CWTest extends JPanel {

    private static final Object ISLANDCLUSTERKEY = "ISLANDCLUSTERKEY";
//    private static final String XCOORDKEY=
//            , YCOORDKEY
    private static final int MAXSLIDERVALUE = 100;
    private static final double maxEdgeValue=1.0;
    private JungConverter jc;
    
    private islandNetwork inet;
    private int layoutType =0;
    
    /* Constructor.
    *@paramt esInput island network edge set
    *@param sitesInput island network site array  
    */
    public CWTest(islandNetwork inetInput) {
            //maxEdgeValue=inputMaxEdgeValue;
            jc = new JungConverter(inet);
            inet = inetInput;
            layoutType = getLayoutType("circular");

            JTextField test = new JTextField("testing JLabel class CWTest constructor");
            this.add(test);
    } // eo constructor

    
    public final Color[] similarColors =
    {
        new Color(216, 134, 134),
        new Color(135, 137, 211),
        new Color(134, 206, 189),
        new Color(206, 176, 134),
        new Color(194, 204, 134),
        new Color(145, 214, 134),
        new Color(133, 178, 209),
        new Color(103, 148, 255),
        new Color(60, 220, 220),
        new Color(30, 250, 100)
    };
    
//    public static void main(String[] args) throws IOException {
//        
//        CWTest cd = new CWTest();
//        cd.start();
//        // Add a restart button so the graph can be redrawn to fit the size of the frame
//        JFrame jf = new JFrame();
//        jf.getContentPane().add(cd);
//        
//        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jf.pack();
//        jf.setVisible(true);
//    }

    
    
    //ClusteringLayout layout;
    
   public void init() {
        
        JTextField test = new JTextField("testing init");
        this.add(test);
   }
    
    public void start() {
        
        JTextField test = new JTextField("testing start");
        this.add(test);
//        try
//        {
//            setUpView();
//        }
//        catch (Exception e)
//        {
//            System.out.println("Error "+e);
//            e.printStackTrace();
//        }
    }

//    private void setUpView() throws IOException {
    private void setUpView() {
        final Graph graph = jc.getGraph( );

        final SubLayoutDecorator layout;
         layout = new SubLayoutDecorator(new CircleLayout(graph));
        
        final PickedState ps = new MultiPickedState();
        PluggableRenderer pr = new PluggableRenderer();
        pr.setVertexPaintFunction(new VertexPaintFunction() {
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
        });

        pr.setEdgePaintFunction(new EdgePaintFunction() {
            public Paint getDrawPaint(Edge e) {
                Color k = (Color) e.getUserDatum(ISLANDCLUSTERKEY);
                if (k != null)
                    return k;
                return Color.blue;
            }
            public Paint getFillPaint(Edge e)
            {
                return null;
            }
        });

        pr.setEdgeStrokeFunction(new EdgeStrokeFunction()
            {
                protected final Stroke THIN = new BasicStroke(1);
                protected final Stroke THICK= new BasicStroke(2);
                public Stroke getStroke(Edge e)
                {
                    Color c = (Color)e.getUserDatum(ISLANDCLUSTERKEY);
                    if (c == Color.LIGHT_GRAY)
                        return THIN;
                    else 
                        return THICK;
                }
            });


        final VisualizationViewer vv = new VisualizationViewer(layout, pr, new Dimension(100,100));
        vv.setBackground( Color.yellow );
        //Tell the renderer to use our own customized color rendering
        
//        Container content = getContentPane();
//        JPanel content = new JPanel();
//        content.add(new GraphZoomScrollPane(vv), BorderLayout.CENTER);
        JTextField test = new JTextField("testing");
        this.add(test);
        

    } //eo setUpView
 
    
    
    
    
// -------------------------------------------------------------------------    

    public void clusterAndRecolor(SubLayoutDecorator layout,
        int sliderValue,
        Color[] colors, boolean groupClusters) {
        //Now cluster the vertices by removing the top 50 edges with highest betweenness
        //      if (numEdgesToRemove == 0) {
        //          colorCluster( g.getVertices(), colors[0] );
        //      } else {
        
        Graph g = layout.getGraph();
        layout.removeAllSubLayouts();

//        EdgeBetweennessClusterer clusterer =
//            new EdgeBetweennessClusterer(numEdgesToRemove);
//        ClusterSet clusterSet = clusterer.extract(g);
//        List edges = clusterer.getEdgesRemoved();
        EdgePercolationClusterer clusterer = new EdgePercolationClusterer(g, inet);
        double edgeMinValue = maxEdgeValue*sliderValue/MAXSLIDERVALUE; // needs max value to be fed into here
        ClusterSet clusterSet = clusterer.extract(edgeMinValue); // 
        List edges = clusterer.getEdgesRemoved();

        
        
        int i = 0;
        //Set the colors of each node so that each cluster's vertices have the same color
        for (Iterator cIt = clusterSet.iterator(); cIt.hasNext();) {

            Set vertices = (Set) cIt.next();
            Color c = colors[i % colors.length];

            colorCluster(vertices, c);
            if(groupClusters == true) {
                groupCluster(layout, vertices);
            }
            i++;
        }
        for (Iterator it = g.getEdges().iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            if (edges.contains(e)) {
                e.setUserDatum(ISLANDCLUSTERKEY, Color.LIGHT_GRAY, UserData.REMOVE);
            } else {
                e.setUserDatum(ISLANDCLUSTERKEY, Color.BLACK, UserData.REMOVE);
            }
        }

    }

    private void colorCluster(Set vertices, Color c) {
        for (Iterator iter = vertices.iterator(); iter.hasNext();) {
            Vertex v = (Vertex) iter.next();
            v.setUserDatum(ISLANDCLUSTERKEY, c, UserData.REMOVE);
        }
    }
    
    private void groupCluster(SubLayoutDecorator layout, Set vertices) {
        if(vertices.size() < layout.getGraph().numVertices()) {
            Point2D center = layout.getLocation((ArchetypeVertex)vertices.iterator().next());
            SubLayout subLayout = new CircularSubLayout(vertices, 20, center);
            layout.addSubLayout(subLayout);
        }
    }

    private int getLayoutType(String s)
    {
        if (s.substring(0,1)=="g") return 0; // geographical layout 
        if (s.substring(0,1)=="c") return 1; // circular layout 
        if (s.substring(0,1)=="f") return 2; // Fruchterman-Rheingold layout algorithm layout  
        return 0;
    }


}
