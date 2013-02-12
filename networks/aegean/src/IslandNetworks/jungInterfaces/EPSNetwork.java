/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

//import IslandNetworks.ClusteringWindow;
//import IslandNetworks.islandNetwork;
//import TimGraph.io.GraphMLGenerator;
//import edu.uci.ics.jung.graph.Edge;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
//import edu.uci.ics.jung.graph.decorators.EdgeShapeFunction;
//import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
//import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
//import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
//import edu.uci.ics.jung.visualization.Layout;
//import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics2D;
//import java.awt.Rectangle;
//import java.awt.Shape;
//import java.awt.Stroke;
//import java.awt.geom.Point2D;
//import java.io.File;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
//import java.util.Set;
//import java.util.TreeMap;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

/**
 * Routines for EPS output of JUNG
 * @author time
 */
public class EPSNetwork {


  /** Produces Network as EPS file.
     * <br><i>filenamecomplete</i><tt>.eps</tt> file produced.
     * @param fileName full file name including any directory structure except for the <tt>.eps</tt> extension
     * @param jc jung representation of an islandNetwork
     * @param vv jung visualisation of an islandNetwork
     * @param noPercolationRemovedEdges true if do not want edges displayed as light gray as removed by percolation.
     * @param messagesOn true if want messages
     */
    static public void FileOutputEPSNetwork(String fileName, JungConverter jc,
            VisualizationViewer vv, boolean noPercolationRemovedEdges,
            boolean messagesOn)
    {
        String filenamecomplete =  fileName+".eps";
        if (messagesOn) System.out.println("Attempting to write general information to "+ filenamecomplete);
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            outputEPS(PS, vv);
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
     * From a jung visualisation, produces content of EPS file.
     * @param PS Printstream for the output
     * @param vv jung visualisation of an islandNetwork
     */
        static public void outputEPS(PrintStream PS, VisualizationViewer vv){

        EpsGraphics2D g =  new EpsGraphics2D(); //= bi.createGraphics();

        Dimension d=vv.getSize();
        System.out.println("outputEPS size="+d.width+" x "+d.height);
        // use double buffering until now

        // turn it off to capture
        vv.setDoubleBuffered( false );

        // capture: create a BufferedImage
        // create the Graphics2D object that paints to it
        //vv.paintComponent( g );
        vv.paint( g );

        // Get the EPS output.
        String output = g.toString();

        //System.out.println(output);
        PS.print(output);
        g.dispose();

        // turn double buffering back on
        vv.setDoubleBuffered( true );

    }


//    /**
//     * From a jung visualisation, produces content of EPS file.
//     * @param PS Printstream for the output
//     * @param gname name of graph
//     * @param jc jung representation of an islandNetwork
//     * @param vv jung visualisation of an islandNetwork
//     * @param  noPercolationRemovedEdges true if do not want edges displayed as light gray as removed by percolation.
//     */
//        static public void outputEPSfirst(PrintStream PS, String gname, JungConverter jc,
//                VisualizationViewer vv, boolean noPercolationRemovedEdges){
//
//        EpsGraphics2D g =  new EpsGraphics2D(); //= bi.createGraphics();
//
//        Layout layout = vv.getGraphLayout();
//        PluggableRenderer pr = (PluggableRenderer) vv.getRenderer();
//        VertexPaintFunction vpf = pr.getVertexPaintFunction();
//        VertexShapeFunction vsf = pr.getVertexShapeFunction();
//        islandNetwork inet = jc.getIslandNetwork();
//
//
//
//
//        // Do vertices
//        String nodeShape=GraphMLGenerator.DEFAULT_SHAPE;
//        Set vertexSet = jc.getVertices();
//        int numberVertices = vertexSet.size();
//        TreeMap <Integer,Point2D> vertexPosition = new TreeMap();
//        for (Object vo:vertexSet){
//          Vertex v= (Vertex) vo;
//          Point2D position = layout.getLocation(v);
//          int nodeNumber= (Integer) v.getUserDatum(JungConverter.VID_key);
//          vertexPosition.put(nodeNumber, position);
//          Color c = (Color) vpf.getFillPaint(v);
//          Rectangle rect= vsf.getShape(v).getBounds();
//          double w=rect.width;
//          double h=rect.height;
//          //System.out.println("v"+nodeNumber+", x"+position.getX()+", y"+position.getY()+", w"+w+", h"+h);
//          String vname = inet.siteSet.getName(nodeNumber);
////          gml.printNodeColoured(PS, nodeNumber, numberVertices,
////                  position.getX(),  position.getY(), w, h,
////                                  vname, JavaColours.RGB(c), nodeShape);
//          	//public void drawOval(int x, int y, int width, int height)
//	 printNode(g, position.getX(),  position.getY(), w,h,c);
//        }
//
//        // do edges
//        Set edgeSet=jc.getEdges();
//        EdgePaintFunction epf = pr.getEdgePaintFunction();
//        EdgeStrokeFunction esf = pr.getEdgeStrokeFunction();
//        EdgeShapeFunction edgeShapeFunction = pr.getEdgeShapeFunction();
//
//        //String ename = "";
//          for (Object eo:edgeSet){
//          Edge e= (Edge) eo;
//          Color ec = (Color) epf.getDrawPaint(e);
//          if ((noPercolationRemovedEdges) && (ec==ClusteringWindow.PERCREMOVEDCOLOUR) ) continue;
//          // this edge not removed by percolation (or whatever) or wanted all edges displayed.
//          int edgeNumber = (Integer) e.getUserDatum(JungConverter.EID_key);
//          int source = inet.edgeSet.getSource(edgeNumber);
//          int target = inet.edgeSet.getTarget(edgeNumber);
//          BasicStroke eStroke = (BasicStroke) esf.getStroke(e);
//          Shape eShape = edgeShapeFunction.getShape(e);
//          //float width = eStroke.getLineWidth();
////          gml.printEdgeColoured(PS, edgeNumber, source, target,  ename, width, true, JavaColours.RGB(ec));
//          printEdge(g, vertexPosition.get(source), vertexPosition.get(target),  eShape, eStroke, ec);
//        }
//
//          // Get the EPS output.
//         String output = g.toString();
//
//        //System.out.println(output);
//        PS.print(output);
//         g.dispose();
//
//    }


//        /**
//         * Print a node.
//         * @param g EPS graphics object
//         * @param cx x coordinate of centre of node
//         * @param cy y coordinate of centre of node
//         * @param rw x radius of node
//         * @param rh y radius of node* @param c colour
//         */
//        static private void printNode(EpsGraphics2D g, double cx, double cy, double rw, double rh, Color c){
//                printNodeEPS(g, (int)(0.5+cx-rw), (int)(0.5+cy-rw), posInt(2*rw), posInt(2*rh), 0, c);
//        }

//        /**
//         * Print a node.
//         * <p>This uses postscript conventions so specify node by specifying
//         * rectangle containing the node.
//         * @param g EPS graphics object
//         * @param x x coordinate of top left corner of rectangle
//         * @param y r coordinate of top left corner of rectangle
//         * @param w x width of rectangle
//         * @param h y height width of rectangle
//         * @param c colour
//         */
//        static private void printNodeEPS(EpsGraphics2D g, int x, int y, int w, int h, int nodeShape, Color c){
//            //System.out.println("node x"+x+", y"+y+", w"+w+", h"+h+", colour "+c);
//            g.setColor(c);
//                switch( nodeShape) {
//                    case 11: g.drawRect(x,  y, w, h); break;
//                    case 10: g.drawOval(x,  y, w, h); break;
//                    case 1: g.fillRect(x,  y, w, h); break;
//                    case 0:
//                    default: g.fillOval(x,  y, w, h);
//                }
//        }

//        /**
//         * Print Edge
//         * @param g eps graphics object
//         * @param sourcePosition position of source
//         * @param targetPosition position of target
//         * @param sh shape of edge
//         * @param st stroke of edge
//         * @param c colour of edge
//         */
//        static private void printEdge(EpsGraphics2D g,
//                Point2D sourcePosition, Point2D targetPosition,
//                Shape sh, Stroke st, Color c){
//            printEdge(g, roundInt(sourcePosition.getX()), roundInt(sourcePosition.getY()),
//                    roundInt(targetPosition.getX()), roundInt(targetPosition.getY()), sh, st, c);
//        }

//        static private void printEdge(EpsGraphics2D g,
//                int sourceX, int sourceY, int targetX, int targetY,
//                Shape sh,   Stroke s, Color c){
//                     g.setColor(c);
//         g.setStroke(s);
//         g.draw(sh);
//         g.drawLine(sourceX,  sourceY, targetX, targetY);
//        }

//        static private int posInt(double d){return Math.max((int) (d+0.5), 1);}
//        static private int roundInt(double d){return (int) Math.floor(d+0.5);}

//            /**
//     * Draws the edge <code>e</code>, whose endpoints are at <code>(x1,y1)</code>
//     * and <code>(x2,y2)</code>, on the graphics context <code>g</code>.
//     * The <code>Shape</code> provided by the <code>EdgeShapeFunction</code> instance
//     * is scaled in the x-direction so that its width is equal to the distance between
//     * <code>(x1,y1)</code> and <code>(x2,y2)</code>.
//     * <p>Taken from edu.uci.ics.jung.visualization.PlugableRenderer
//     */
//    protected void drawSimpleEdge(Graphics2D g, Edge e, int x1, int y1, int x2, int y2)
//    {
//        Pair endpoints = e.getEndpoints();
//        Vertex v1 = (Vertex)endpoints.getFirst();
//        Vertex v2 = (Vertex)endpoints.getSecond();
//        boolean isLoop = v1.equals(v2);
//        Shape s2 = vertexShapeFunction.getShape(v2);
//        Shape edgeShape = edgeShapeFunction.getShape(e);
//
//        boolean edgeHit = true;
//        boolean arrowHit = true;
//        Rectangle deviceRectangle = null;
//        if(screenDevice != null) {
//            Dimension d = screenDevice.getSize();
//            if(d.width <= 0 || d.height <= 0) {
//                d = screenDevice.getPreferredSize();
//            }
//            deviceRectangle = new Rectangle(0,0,d.width,d.height);
//        }
//
//        AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
//
//        if(isLoop) {
//            // this is a self-loop. scale it is larger than the vertex
//            // it decorates and translate it so that its nadir is
//            // at the center of the vertex.
//            Rectangle2D s2Bounds = s2.getBounds2D();
//            xform.scale(s2Bounds.getWidth(),s2Bounds.getHeight());
//            xform.translate(0, -edgeShape.getBounds2D().getWidth()/2);
//        } else {
//            // this is a normal edge. Rotate it to the angle between
//            // vertex endpoints, then scale it to the distance between
//            // the vertices
//            float dx = x2-x1;
//            float dy = y2-y1;
//            float thetaRadians = (float) Math.atan2(dy, dx);
//            xform.rotate(thetaRadians);
//            float dist = (float) Math.sqrt(dx*dx + dy*dy);
//            xform.scale(dist, 1.0);
//        }
//
//        edgeShape = xform.createTransformedShape(edgeShape);
//
//        edgeHit = viewTransformer.transform(edgeShape).intersects(deviceRectangle);
//
//        if(edgeHit == true) {
//
//            Paint oldPaint = g.getPaint();
//
//            // get Paints for filling and drawing
//            // (filling is done first so that drawing and label use same Paint)
//            Paint fill_paint = edgePaintFunction.getFillPaint(e);
//            if (fill_paint != null)
//            {
//                g.setPaint(fill_paint);
//                g.fill(edgeShape);
//            }
//            Paint draw_paint = edgePaintFunction.getDrawPaint(e);
//            if (draw_paint != null)
//            {
//                g.setPaint(draw_paint);
//                g.draw(edgeShape);
//            }
//
//            float scalex = (float)g.getTransform().getScaleX();
//            float scaley = (float)g.getTransform().getScaleY();
//            // see if arrows are too small to bother drawing
//            if(scalex < .3 || scaley < .3) return;
//
//            if (edgeArrowPredicate.evaluate(e)) {
//
//                Shape destVertexShape =
//                    vertexShapeFunction.getShape((Vertex)e.getEndpoints().getSecond());
//                AffineTransform xf = AffineTransform.getTranslateInstance(x2, y2);
//                destVertexShape = xf.createTransformedShape(destVertexShape);
//
//                arrowHit = viewTransformer.transform(destVertexShape).intersects(deviceRectangle);
//                if(arrowHit) {
//
//                    AffineTransform at;
//                    if (edgeShape instanceof GeneralPath)
//                        at = getArrowTransform((GeneralPath)edgeShape, destVertexShape);
//                    else
//                        at = getArrowTransform(new GeneralPath(edgeShape), destVertexShape);
//                    if(at == null) return;
//                    Shape arrow = edgeArrowFunction.getArrow(e);
//                    arrow = at.createTransformedShape(arrow);
//                    // note that arrows implicitly use the edge's draw paint
//                    g.fill(arrow);
//                }
//                if (e instanceof UndirectedEdge) {
//                    Shape vertexShape =
//                        vertexShapeFunction.getShape((Vertex)e.getEndpoints().getFirst());
//                    xf = AffineTransform.getTranslateInstance(x1, y1);
//                    vertexShape = xf.createTransformedShape(vertexShape);
//
//                    arrowHit = viewTransformer.transform(vertexShape).intersects(deviceRectangle);
//
//                    if(arrowHit) {
//                        AffineTransform at;
//                        if (edgeShape instanceof GeneralPath)
//                            at = getReverseArrowTransform((GeneralPath)edgeShape, vertexShape, !isLoop);
//                        else
//                            at = getReverseArrowTransform(new GeneralPath(edgeShape), vertexShape, !isLoop);
//                        if(at == null) return;
//                        Shape arrow = edgeArrowFunction.getArrow(e);
//                        arrow = at.createTransformedShape(arrow);
//                        g.fill(arrow);
//                    }
//                }
//            }
//            // use existing paint for text if no draw paint specified
//            if (draw_paint == null)
//                g.setPaint(oldPaint);
//            String label = edgeStringer.getLabel(e);
//            if (label != null) {
//                labelEdge(g, e, label, x1, x2, y1, y2);
//            }
//
//
//            // restore old paint
//            g.setPaint(oldPaint);
//        }
//    }

    /**
//     * Copy the visible part of the JUNG graph to an EPS file.
//     * <p>Uses <tt>jlibeps</tt> .
//     * TODO COPY THE GRAPHML CLASS IN THIS SAME PACKAGE
//     * @param fileName full file name (including directories) of file without any extension.
//     * @param labelString string used to label graph.
//     * @param vv a JUNG visualisation viewer with the graph.
//     * @see IslandNetworks.jungInterfaces.GraphML
//     * @link http://www.jibble.org/
//     * @deprecated does not work
//     */
//
//    public static void writeEPSImage(String fileName, String labelString, VisualizationViewer vv, int width, int height, boolean messageOn) {
//        String filenamecomplete =  fileName+".eps";
//        //System.out.println("Attempting to write jpg file to "+ filenamecomplete);
//
//        File file = new File(filenamecomplete);
//        Graphics2D g =  new EpsGraphics2D(); //= bi.createGraphics();
//        vv.paint(g);
//        g.setColor(Color.black);
//        g.drawString(labelString, 2, height-2);
//
//
//        // COPY THE GRAPHML CLASS IN THIS SAME PACKAGE
//
////        // Test lines work
////        g.setColor(Color.black);
////         // Line thickness 2.
////         g.setStroke(new BasicStroke(2.0f));
////         // Draw a line.
////         g.drawLine(10, 10, 50, 10);
////         // Fill a rectangle in blue
////         g.setColor(Color.blue);
////         g.fillRect(10, 0, 20, 20);
//
//
//        // Get the EPS output.
//        String output = g.toString();
//        System.out.println(output);
//        g.dispose();
//
//        if (messageOn) System.out.println("Attempting to write to "+ filenamecomplete);
//
//        PrintStream PS;
//
//        // next bit of code p327 Schildt and p550
//        FileOutputStream fout;
//        try {
//            fout = new FileOutputStream(filenamecomplete);
//            PS = new PrintStream(fout);
//            PS.print(output);
//            try
//            {
//               fout.close ();
//               if (messageOn) System.out.println("Finished writing to "+ filenamecomplete);
//            } catch (IOException e) { System.out.println("File Error");}
//
//        } catch (FileNotFoundException e) {
//            System.err.println("Error opening output file "+ filenamecomplete);
//            return;
//        }
//        return;
//
//
//    }

}
