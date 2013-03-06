/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import TimGraph.Community.Partition;
import java.io.PrintStream;
import TimGraph.timgraph;
import TimGraph.Community.VertexPartition;
import TimGraph.Coordinate;
import TimGraph.VertexLabel;
import TimUtilities.JavaColours;
import java.awt.Color;

/*
 * Note that line styles seem to be done in visone
 *           <visone:edgeRealizerData>
 *           <visone:line width="2.0" style="round_dotted" />
 *        </visone:edgeRealizerData>
*/


/**
 * Class to produce GraphML format files for Visone.
 * <br>Note that some of the key elements are part of extensions used by <tt>visone</tt>
 * and in turn by the <tt>yfiles</tt> package.
 * @author time
 */
public class GraphMLGenerator {
    
    final static String [] firstLines={"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
     "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\" xmlns:visone=\"http://visone.info/xmlns\" xmlns:y=\"http://www.yworks.com/xml/graphml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\">"};
    final static String [] keyDefinitions = 
    {"<key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>",
     "<key id=\"d1\" for=\"node\" attr.name=\"id\" attr.type=\"string\"></key>",
     "<key id=\"d2\" for=\"edge\" yfiles.type=\"edgegraphics\"/>",
     "<key id=\"d5\" for=\"edge\" attr.name=\"id\" attr.type=\"string\"></key>"
    };
    
   // counts how much to indent.//
    int indent=1;

    public final static double DUNSET = -9753472e42;

    /**
     * Scaling factor for drawing arcs.
     * <p>Value is {@value }
     */
    final static double ARCFACTOR = -0.01   ;

    final static String indentString="                                                        ";

    /**
     * Default edge colour.
     * <p>{@value } should be black
     */
    final static String DEFAULTEDGECOLOUR = JavaColours.RGB(Color.BLACK);//"000000";

    /**
     * Default edge grey colour.
     * <p>{@value } used to indicate negative edge labels.
     */
    final static String DEFAULTEDGEGREY = JavaColours.RGB(Color.LIGHT_GRAY);

    /**
     * Default edge colour.
     * <p>{@value } should be red
     */
    final static String DEFAULTVERTEXCOLOUR = JavaColours.RGB(Color.RED);//"FF0000";

    /**
     * Default size used for vertices.
     * <p>{@value } should be red
     */
    public final static double DEFAULT_VERTEX_SIZE = 30.0;

    /**
     * Default size used for layout of vertices.
     * <p>{@value } should be red
     */
    public final static double DEFAULT_LAYOUT_SCALE = 300.0;


  /**
   * List of allowed yfiles shape types.
   * <br>The first is used as a default.
   */
    public final static String [] shapeTypes = {"ellipse", "roundrectangle", "triangle", "diamond", "hexagon", "octagon", 
     "trapezoid", "parallelogram", "trapezoid2"};
    
    /**
     * Index of default shape for vertices.
     * <p>{@value }
     */
    public static final int DEFAULT_SHAPE_INDEX=0;
    /**
     * Index of default shape.
     * <p>{@value }
     */
    public static final  String DEFAULT_SHAPE=getNodeShape(DEFAULT_SHAPE_INDEX);
    /**
     * Index of default shape for grey vertices.
     * <p>(i.e. with negative numbers in vertex labels)
     * <p>{@value }
     */
    public static final int DEFAULT_GREY_SHAPE_INDEX=shapeTypes.length-1;
    /**
     * Index of default shape for grey coloured vertices.
     * <p>(i.e. with negative numbers in vertex labels)
     * <p>{@value }
     */
    public static final  String DEFAULT_GREY_SHAPE=getNodeShape(DEFAULT_GREY_SHAPE_INDEX);

    /**
     * Constructor.
     */
    public  GraphMLGenerator(){
        System.err.println("*** #### !!! New GraphMLGenerator");
    }
    

//    /**
//     * Output graph to a graphml file using yfiles extensions.
//     * <p>Width is 10.0 in graphml file used for edge of largest weight
//     * @param PS PrintStream for output
//     * @param tg graph to be output
//     * @param edgeWeightMinimum only edges of this weight or larger are displayed
//     */
//    public void output(PrintStream PS, timgraph tg, double edgeWeightMinimum ){
//       double edgeWidthMaximum=10.0;
//       output( PS,  tg, edgeWidthMaximum, edgeWeightMinimum);
//    }

//   /**
//    * Output graph to a graphml file using yfiles extensions.
//    * <p>Colours the edges and vertices in a sensible way, using the edge labels
//    * and the numbers of the vertex labels, if any and if non-negative.
//    * The size of the vertices is from the default settings and this sets the
//    * size of the circle used to set positions if no position is given.
//     * @param PS PrintStream for output
//     * @param tg graph to be output
//     * @param edgeWidthMaximum Width in graphml file to be used for edge of largest weight
//    * @param edgeWeightMinimum only edges of this weight or larger are displayed
//    */
//    public void output(PrintStream PS, timgraph tg, double edgeWidthMaximum, double edgeWeightMinimum){
//       int numberVertexLabels=0;
//       if (tg.isVertexLabelled()){
//           for (int v=0; v<tg.getNumberVertices(); v++){
//               numberVertexLabels=Math.max(numberVertexLabels, tg.getVertexNumber(v, 0));
//           }
//       }
//       numberVertexLabels++;
//
//       double edgeWidthMultiplier=1.0;
//       int numberEdgeLabels=0;
//       if (tg.isWeighted()){
//           double maxew=1e-6;
//           for (int e=0; e<tg.getNumberStubs(); e++) {
//               maxew=Math.max(maxew,tg.getEdgeWeight(e));
//               numberEdgeLabels=Math.max(numberEdgeLabels,tg.getEdgeLabelOrZero(e,0));
//           }
//           edgeWidthMultiplier=edgeWidthMaximum/maxew;
//       }
//       numberEdgeLabels++; //as counting from 0
//
//       boolean targetArrowsOn = tg.isDirected();
//       //outputGraph( PS,  tg, edgeWidthMultiplier, targetArrowsOn, DEFAULTEDGECOLOUR);
//       double vertexPositionScale=1.0;
//
//       outputGraph(PS, tg, numberVertexLabels, numberEdgeLabels, vertexPositionScale, edgeWeightMinimum, edgeWidthMultiplier, targetArrowsOn);
//    }

//    /**
//     * Output graph to a graphml file using yfiles extensions.
//     * <p>Uses vertex labels and edge weights to set properties if they exist.
//     * @param PS PrintStream for output
//     * @param tg graph to be output
//     * @param numberVertexColours used to set range of vertex colours.  Anything less than 2 will produce default colours.
//     * @param numberEdgeColours used to set range of edge colours.  Anything less than 2 will produce default colours.
//     * @param edgeWidthMultiplier multiply edge weights by this factor to get edge width
//     * @param targetArrowsOn true (false) if want arrows on (off)
//     * @deprecated
//     */
//    public void outputGraphOLD(PrintStream PS, timgraph tg, int numberVertexColours, int numberEdgeColours,
//             double edgeWidthMultiplier, boolean targetArrowsOn){
//       // PS.println("comments");
//        printInitialLines(PS, tg.outputName.getNameRoot(), tg.isDirected() );
//
//        double w = GraphMLGenerator.DEFAULT_VERTEX_SIZE;
//        double h = GraphMLGenerator.DEFAULT_VERTEX_SIZE;
//        JavaColours jc = null;
//        if (numberVertexColours>1) jc = new JavaColours(numberVertexColours+1,true);
//        for (int v=0; v<tg.getNumberVertices(); v++)
//            printNodeColoured(PS, v, tg.getNumberVertices(),
//                    (tg.isVertexLabelled() ? tg.getVertexLabel(v):null),
//                            w, h, jc);
//
//        int s=-1;
//        int t=-1;
//        double width=-1.0;
//        String edgeColour = GraphMLGenerator.DEFAULTEDGECOLOUR;
//        if (numberEdgeColours>1) jc = new JavaColours(numberEdgeColours+1,true);
//        for (int e=0; e<tg.getNumberStubs(); e++){
//            s=tg.getVertexFromStub(e++);
//            t=tg.getVertexFromStub(e);
//            width=tg.getEdgeWeight(e)*edgeWidthMultiplier;
//            int el = tg.getEdgeLabelSimple(e);
//            if (numberEdgeColours<2 || el==EdgeValue.NOLABEL) edgeColour = GraphMLGenerator.DEFAULTEDGECOLOUR;
//            else edgeColour = jc.RGB(el+1);
//            printEdgeColoured(PS, e-1, s, t,tg.getEdgeLabelString(e), width, targetArrowsOn, edgeColour);
//        }
//        printFinalLines(PS );
//    }

   /**
     * Output graph to a graphml file using yfiles extensions.
     * <p>Uses vertex labels and edge values to set properties if they exist.
     * @param PS PrintStream for output
     * @param tg graph to be output
     * @param numberVertexColours used to set range of vertex colours.  Anything less than 2 will produce default colours.
     * @param numberEdgeColours used to set range of edge colours.  Anything less than 2 will produce default colours.
     * @param edgeWidthMultiplier multiply edge weights by this factor to get edge width
     * @param targetArrowsOn true (false) if want arrows on (off)
     */
    public void outputGraph(PrintStream PS, timgraph tg,
             double edgeWidthMultiplier, boolean targetArrowsOn){
       double edgeWeightMinimum=1e-44;
       double vertexPositionScale=1.0;
       if (tg.getMaximumVertexLabel()==null) tg.calcMaximumVertexLabel();
       int numberVertexColours = tg.getMaximumVertexNumber();
       if (tg.getMaximumEdgeValue()==null) tg.calcMaximumEdgeValue();
       int numberEdgeColours= tg.getMaximumEdgeLabel();
       outputGraph(PS,  tg, numberVertexColours, numberEdgeColours,
            vertexPositionScale, edgeWeightMinimum,
            edgeWidthMultiplier, targetArrowsOn);
    }
       /**
     * Output graph to a graphml file using yfiles extensions.
     * <p>Uses vertex labels and edge weights to set properties if they exist.
     * @param PS PrintStream for output
     * @param tg graph to be output
     * @param numberVertexColours used to set range of vertex colours.  Anything less than 2 will produce default colours.
     * @param numberEdgeColours used to set range of edge colours.  Anything less than 2 will produce default colours.
     * @param edgeWidthMultiplier multiply edge weights by this factor to get edge width
     * @param targetArrowsOn true (false) if want arrows on (off)
     */
    public void outputGraph(PrintStream PS, timgraph tg, int numberVertexColours, int numberEdgeColours,
             double edgeWidthMultiplier, boolean targetArrowsOn){
       double edgeWeightMinimum=1e-44;
       double vertexPositionScale=1.0;
        outputGraph(PS,  tg, numberVertexColours, numberEdgeColours,
            vertexPositionScale, edgeWeightMinimum,
            edgeWidthMultiplier, targetArrowsOn);
    }

    /**
     * Output graph using vertex and edge labels to colour each.
     * <br>The labels are assumed to lie within 0 to <tt>numberLabels</tt> inclusive.
     * The <tt>timgraph</tt> routine <tt>makeEdgeSubGraph</tt>, <tt>relabelEdges</tt>
     * and associated routines may be of use here.
     * The label associated with <tt>label= numberLabels</tt> will be output as black.
     * To avoid black edges make the number of labels one more than the number of
     * distinct labels of the edges.
     * <p>To automate some of the parameters use <tt>output</tt>
     * @param PS PrintStream such as System.out
     * @param tg graph to be output
     * @param numberVertexLabels number of vertex labels, less than 2 produces default colour
     * @param numberEdgeLabels number of edge labels, less than 2 produces default colour
     * @param vertexPositionScale scales the positions of vertices if they are given.
     * @param edgeWeightMinimum only edges of this weight or larger are displayed
     * @param edgeWidthMultiplier multiply edge weights by this factor to get edge width
     * @param targetArrowsOn true (false) if want arrows on (off)
     * @see #output(java.io.PrintStream, TimGraph.timgraph, double)
     */
    public void outputGraph(PrintStream PS, timgraph tg,
            int numberVertexLabels, int numberEdgeLabels,
            double vertexPositionScale, double edgeWeightMinimum,
            double edgeWidthMultiplier, boolean targetArrowsOn){
        JavaColours nc = null;
        if (numberEdgeLabels>1) nc = new JavaColours(numberEdgeLabels+1,true); // colour 0 is white
        JavaColours nvc = null;
        if (numberVertexLabels>1) nvc = new JavaColours(numberVertexLabels+1,true);
        if (tg.isInfoLeveLGreaterThan(1)) {
            if (numberEdgeLabels>1) nc.printAllColours(System.out);
            if (numberVertexLabels>1) nvc.printAllColours(System.out);
        }
        printInitialLines(PS, tg.outputName.getNameRoot(), tg.isDirected() );

        double xpos=0;
        double ypos=0;
        double vsize=DEFAULT_VERTEX_SIZE;
        double angle=0;
        final double dtheta=2*Math.PI/tg.getNumberVertices();
        final double radius=GraphMLGenerator.DEFAULT_VERTEX_SIZE/dtheta;


        VertexLabel vl = new VertexLabel();
        int vlabel=0;
        String name="";
        String vcstring=GraphMLGenerator.DEFAULTVERTEXCOLOUR;
        String ecstring=GraphMLGenerator.DEFAULTEDGECOLOUR;
        String nodeShape=GraphMLGenerator.DEFAULT_SHAPE;

        for (int v=0; v<tg.getNumberVertices(); v++) {
            //printNodeColoured(PS, v, tg.getNumberVertices(),tg.getVertexName(v) ,"FFFFFF",0);
            angle=dtheta*v;
            xpos=radius*Math.cos(angle);
            ypos=radius*Math.sin(angle);
            vcstring=GraphMLGenerator.DEFAULTVERTEXCOLOUR;
            nodeShape=GraphMLGenerator.DEFAULT_SHAPE;
            name=tg.getVertexName(v);

            if (tg.isVertexLabelled()){
                vl=tg.getVertexLabel(v);
                if (vl.hasPosition()) {
                    xpos=vertexPositionScale*vl.getPosition().getX();
                    ypos=vertexPositionScale*vl.getPosition().getY();
                }
                if (vl.hasNumber()) {
                    vlabel=vl.getNumber();
                    if (vlabel<0) {
                        vcstring=GraphMLGenerator.DEFAULTEDGEGREY;
                        nodeShape=GraphMLGenerator.DEFAULT_GREY_SHAPE;
                    }
                    else {
                        vcstring=(nvc==null?GraphMLGenerator.DEFAULTVERTEXCOLOUR : nvc.RGB(vlabel+1) );
                        nodeShape=shapeTypes[vlabel%shapeTypes.length];
                    }
                }
            }
            printNodeColoured(PS, v, tg.getNumberVertices(),xpos, ypos, vsize, vsize, name, vcstring, nodeShape);
        }

        int s=-1;
        int t=-1;
        double ew;
        int el=-1;
        Coordinate sCoordinate;
        Coordinate tCoordinate;
        double arcFactor = (tg.isDirected()?ARCFACTOR:DUNSET);
        for (int e=0; e<tg.getNumberStubs(); e++){
            s=tg.getVertexFromStub(e++);
            t=tg.getVertexFromStub(e);
            sCoordinate = tg.getVertexPosition(s);
            tCoordinate = tg.getVertexPosition(t);
            ecstring=GraphMLGenerator.DEFAULTEDGECOLOUR;
            ew=tg.getEdgeWeightSlow(e);
            if (ew<edgeWeightMinimum) continue;
            ew*=edgeWidthMultiplier;
            if (tg.isEdgeLabelled()){
                el=tg.getEdgeLabel(e);
                if (el<0) ecstring=GraphMLGenerator.DEFAULTEDGEGREY;
                else ecstring = (nc==null?GraphMLGenerator.DEFAULTEDGECOLOUR: nc.RGB(el+1));
            }
            printEdgeColoured(PS, e-1, s, t, sCoordinate, tCoordinate, arcFactor, tg.getEdgeLabelString(e), ew, tg.isDirected(), ecstring );
        }
        printFinalLines(PS );
    }


    /**
     * Output a graph using a vertex partition.
     * <p>May be better to use graph's vertex numbers and the OutputGraph routine.
     * @param PS PrintStream such as System.out
     * @param tg network
     * @param c partition
     */
    public void outputVertexPartition(PrintStream PS, timgraph tg, Partition c){
        JavaColours nc = new JavaColours(c.getNumberOfCommunities()+2,true);
        if (tg.isInfoLeveLGreaterThan(1)) nc.printAllColours(System.out);
        
        printInitialLines(PS, tg.outputName.getNameRoot(), tg.isDirected() );
        
        for (int v=0; v<tg.getNumberVertices(); v++) {
            //int cn=c.getCommunity(v);
            printNodeColoured(PS, v, tg.getNumberVertices(), tg.getVertexLabel(v), c.getCommunity(v)+1, -1, nc);
            //printNodeColoured(PS, v, tg.getNumberVertices(), tg.getVertexLabel(v),nc);
        }
        
        int s=-1;
        int t=-1;
        String name="";
        Coordinate sCoordinate;
        Coordinate tCoordinate;
        double arcFactor = (tg.isDirected()?ARCFACTOR:DUNSET);
        for (int e=0; e<tg.getNumberStubs(); e++){
            s=tg.getVertexFromStub(e++);
            t=tg.getVertexFromStub(e);
            sCoordinate = tg.getVertexPosition(s);
            tCoordinate = tg.getVertexPosition(t);
            double ew=tg.getEdgeWeightSlow(e);
            printEdgeColoured(PS, e-1, s, t, sCoordinate, tCoordinate, arcFactor,
                    tg.getEdgeLabelString(e), ew, tg.isDirected(), GraphMLGenerator.DEFAULTEDGECOLOUR);
            //printEdge(PS, e-1, s, t, tg.getEdgeLabelString(e), tg.isDirected() );
        }
        printFinalLines(PS );
    }

    /**
     * Output edge coloured graph using an edge partition.
     * <br>Note that the community here is a vertex partition 
     * for a line graph of the input timgraph.  Thus the 
     * vertex index <tt>v</tt> of the given community (and of the line graph)
     * corresponds to the edge in the original input graph <tt>tg</tt>  
     * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     * @param PS PrintStream such as System.out
     * @param tg graph to be output
     * @param c edge partition where `vertex' <tt>v</tt> is edge with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     */
    public void outputEdgePartition(PrintStream PS, timgraph tg, Partition c){
        JavaColours nc = new JavaColours(c.getNumberOfCommunities()+2,true);
        if (tg.isInfoLeveLGreaterThan(1)) nc.printAllColours(System.out);
        printInitialLines(PS, tg.outputName.getNameRoot(), tg.isDirected() );
        
        for (int v=0; v<tg.getNumberVertices(); v++) {
            printNodeColoured(PS, v, tg.getNumberVertices(),tg.getVertexLabel(v) ,null);
            //printNode(PS, v);
        }
        
        int s=-1;
        int t=-1;
        Coordinate sCoordinate;
        Coordinate tCoordinate;
        double arcFactor = (tg.isDirected()?ARCFACTOR:DUNSET);
        for (int e=0; e<tg.getNumberStubs(); e++){
            s=tg.getVertexFromStub(e++);
            t=tg.getVertexFromStub(e);
            sCoordinate = tg.getVertexPosition(s);
            tCoordinate = tg.getVertexPosition(t);
            double ew=tg.getEdgeWeightSlow(e);
            printEdgeColoured(PS, e-1, s, t, sCoordinate, tCoordinate, arcFactor,
                    tg.getEdgeLabelString(e), ew, tg.isDirected(), nc.RGB(c.getCommunity(e/2)+1));
        }
        printFinalLines(PS );
    }
    /**
     * Output edge coloured graph using edge labels.
     * <br>The labels are assumed to lie within 0 to <tt>numberLabels</tt> inclusive.
     * The <tt>timgraph</tt> routine <tt>makeEdgeSubGraph</tt>, <tt>relabelEdges</tt> 
     * and associated routines may be of use here.
     * The label associated with <tt>label= numberLabels</tt> will be output as black.  
     * To avoid black edges make the number of labels one more than the number of 
     * distinct labels of the edges.
     * @param PS PrintStream such as System.out
     * @param tg graph to be output
     * @param numberLabels number of labels
     */
    public void outputEdgeLabels(PrintStream PS, timgraph tg, int numberLabels){
        JavaColours nc = new JavaColours(numberLabels+1,true);
        if (tg.isInfoLeveLGreaterThan(1)) nc.printAllColours(System.out);
        printInitialLines(PS, tg.outputName.getNameRoot(), tg.isDirected() );
        
        for (int v=0; v<tg.getNumberVertices(); v++) {
            printNodeColoured(PS, v, tg.getNumberVertices(),tg.getVertexLabel(v) ,null);
            //printNode(PS, v);
        }
        
        int s=-1;
        int t=-1;
        Coordinate sCoordinate;
        Coordinate tCoordinate;
        double arcFactor = (tg.isDirected()?ARCFACTOR:DUNSET);
        for (int e=0; e<tg.getNumberStubs(); e++){
            s=tg.getVertexFromStub(e++);
            t=tg.getVertexFromStub(e);
            sCoordinate = tg.getVertexPosition(s);
            tCoordinate = tg.getVertexPosition(t);
            double ew=tg.getEdgeWeightSlow(e);
            printEdgeColoured(PS, e-1, s, t, sCoordinate, tCoordinate, arcFactor,
                    tg.getEdgeLabelString(e), ew, tg.isDirected(), nc.RGB(tg.getEdgeLabel(e)+1));
        }
        printFinalLines(PS );
    }

    public void outputVertexEdgePartition(PrintStream PS, timgraph tg, VertexPartition vp, VertexPartition ep){
        //JavaColours nc = new JavaColours(vp.getNumberOfCommunities()+ep.getNumberOfCommunities()+2,true);
        JavaColours nc = new JavaColours(Math.max(vp.getNumberOfCommunities(),ep.getNumberOfCommunities())+1,true);
        if (tg.isInfoLeveLGreaterThan(1)) 
        { System.out.println("no vp = "+vp.getNumberOfCommunities()+", no. EP = "+ep.getNumberOfCommunities());
          nc.printColourInfo(System.out);
        }
        
        printInitialLines(PS, tg.outputName.getNameRoot(), tg.isDirected() );
        
        for (int v=0; v<tg.getNumberVertices(); v++) {
            int vc=vp.getCommunity(v);
            printNodeColoured(PS, v, tg.getNumberVertices(), tg.getVertexLabel(v), nc);
        }
        
        int cshift = 1; // vp.getNumberOfCommunities()+1;
        int s=-1;
        int t=-1;
        Coordinate sCoordinate;
        Coordinate tCoordinate;
        double arcFactor = (tg.isDirected()?ARCFACTOR:DUNSET);
        for (int e=0; e<tg.getNumberStubs(); e++){
            s=tg.getVertexFromStub(e++);
            t=tg.getVertexFromStub(e);
            double ew=tg.getEdgeWeightSlow(e);
            sCoordinate = tg.getVertexPosition(s);
            tCoordinate = tg.getVertexPosition(t);
            printEdgeColoured(PS, e-1, s, t, sCoordinate, tCoordinate, arcFactor,
                    tg.getEdgeLabelString(e), ew, tg.isDirected(), nc.RGB(ep.getCommunity(e/2)+cshift));
        }
        printFinalLines(PS );
    }

    /**
     * Uses an incidence graph vertex partition to colour edges and vertices.
     * <p>Incidence graph vertices has original vertices indexed as
     * <tt>0..(tg.getNumberVertices()-1)</tt>.  The original edges have index in the partition of
     * <tt>v=(tg.getNumberVertices())..(tg.getNumberVertices()+tg.getNumberStubs()/2-1)</tt>
     * and the original edge index is then <tt>e=(v-tg.getNumberVertices())*2)</tt> and <tt>(e+1)</tt>
     * @param PS PrintStream such as <tt>System.out</tt>
     * @param tg graph to be output
     * @param ip vertex partition of incidence graph of given graph.
     */
    public void outputIncidenceVertexPartition(PrintStream PS, timgraph tg, VertexPartition ip){
        //JavaColours nc = new JavaColours(vp.getNumberOfCommunities()+ep.getNumberOfCommunities()+2,true);
        JavaColours nc = new JavaColours(ip.getNumberOfCommunities()+2,true);
        if (tg.isInfoLeveLGreaterThan(1)) 
        { System.out.println("no ip = "+ip.getNumberOfCommunities());
          nc.printColourInfo(System.out);
        }
        
        printInitialLines(PS, tg.outputName.getNameRoot(), tg.isDirected() );
        int nv1=tg.getNumberVertices();
        for (int v=0; v<nv1; v++) {
            int ic=ip.getCommunity(v);
            printNodeColoured(PS, v, tg.getNumberVertices(), tg.getVertexLabel(v), nc);
        }
        
        int cshift = 1; // vp.getNumberOfCommunities()+1;
        int s=-1;
        int t=-1;
        Coordinate sCoordinate;
        Coordinate tCoordinate;
        double arcFactor = (tg.isDirected()?ARCFACTOR:DUNSET);
        for (int e=0; e<tg.getNumberStubs(); e++){
            s=tg.getVertexFromStub(e++);
            t=tg.getVertexFromStub(e);
            sCoordinate = tg.getVertexPosition(s);
            tCoordinate = tg.getVertexPosition(t);
            double ew=tg.getEdgeWeightSlow(e);
            printEdgeColoured(PS, e-1, s, t, sCoordinate, tCoordinate, arcFactor,
                    tg.getEdgeLabelString(e), ew, tg.isDirected(), nc.RGB(ip.getCommunity(nv1+e/2)+cshift));
        }
        printFinalLines(PS );
    }

    public static String startTag(String s){return "<"+s+">";  }
    public static String startEndTag(String s){return "<"+s+"/>";  }
    public static String endTag(String s){return "</"+s+">";  }
    
    public void printlnStartTag(PrintStream PS,String s){PS.println(indentString.substring(0, indent++)+startTag(s)); }
    public void printlnEndTag(PrintStream PS,String s){PS.println(indentString.substring(0, --indent)+endTag(s));}
    public void printlnStartEndTag(PrintStream PS,String s){PS.println(indentString.substring(0, indent)+startEndTag(s));}
    public void printlnStartEndTag(PrintStream PS,String tag, String info){
        printlnStartTag(PS,tag);
        PS.println(indentString.substring(0, indent)+info);
        printlnEndTag(PS,tag);
    }
    
    public void printlnDataTag(PrintStream PS,String key, String s){
        PS.println(indentString.substring(0, indent)+startTag("data key=\""+key+"\" ")+s+endTag("data"));
    }
    
    
    
    /**
     * Gives String of name equals sign then value in quotes
     * <p><em>valueName</em>=&quote;<em>value</em>&quote;
     * @param valueName name  
     * @param value value
     * @return valueString
     */
    public static String valueString(String valueName, int value) 
    {
        return (valueName+"=\""+value+"\"");
    }
    
    /**
     * Gives String of name equals sign then value in quotes.
     * <p><em>valueName</em>=&quote;<em>value</em>&quote;
     * @param valueName name  
     * @param value value
     * @return valueString
     */
    public static String valueString(String valueName, String value) 
    {
        return (valueName+"=\""+value+"\"");
    }
    
    /**
     * Gives String of name equals sign then value in quotes.
     * <p><em>valueName</em>=&quote;<em>value</em>&quote;
     * @param valueName name  
     * @param value value
     * @return valueString
     */
    public static String valueString(String valueName, double value) 
    {
        return (valueName+"=\""+value+"\"");
    }

    /**
     * Gives String of name equals sign then value in quotes.
     * <p><em>valueName</em>=&quote;<em>value</em>&quote;
     * @param valueName name  
     * @param value value
     * @return valueString
     */
    public static String valueString(String valueName, float value) 
    {
        return (valueName+"=\""+value+"\"");
    }

    
    /**
     * Prints the initial lines
     * @param PS PrintStream
     * @param name used to name the graph
     * @param directed true (false) if (un)directed graph.
     */
    public void printInitialLines(PrintStream PS, String name, boolean directed ){
        for (int i=0; i<firstLines.length; i++) PS.println(firstLines[i]);
        for (int i=0; i<keyDefinitions.length; i++) PS.println(keyDefinitions[i]);
        String ds="";
        if (!directed) ds="un";
        printlnStartTag(PS,"graph id=\""+name+"\" edgedefault=\""+ds+"directed\"");
    }
    
    
    public void printFinalLines(PrintStream PS ){
        printlnEndTag(PS,"graph");        
        printlnEndTag(PS,"graphml");
    }


    public String processNodeName(String name){
         String s="";
         char c;
         for (int i=0; i<name.length(); i++){
             c=name.charAt(i);
             if (c=='&') {s=s+"and";continue;}
             s=s+c;
         }
         return s;
     }

     /**
     * Prints name of node.
     * <p>Ensures name string is not null.
     * <p>It also filters the name as follows:-
     * <ul>
     * <li>ampersand is replaced by string and
     * </ul>
     * @param PS PrintStream
     * @param name node name
     */
     public void printNodeNameTag(PrintStream PS, String name){
         if (name.length()==0) return;
         String s="";
         char c;
         for (int i=0; i<name.length(); i++){
             c=name.charAt(i);
             if (c=='&') {s=s+"and";continue;}
             s=s+c;
         }
         printlnDataTag(PS,"d1",name);
     }
    /**
     * Prints a node tag.
     * @param PS PrintStream
     * @param nodeNumber used to give node id
     * @param name label to give to node, nothing added if length is zero.
     * @deprecated Use printNodeColoured routines
     */
    public void printNode(PrintStream PS, int nodeNumber, String name){
       printlnStartTag(PS,"node "+valueString("id","n"+nodeNumber) );  
       printNodeNameTag(PS, name);
       //if (name.length()>0) printlnDataTag(PS,"d1",name);
       printNodeNameTag(PS, name);
       printlnEndTag(PS,"node");
    }
    
    /**
     * Prints a node tag.
     * <br>Currently has yfile (Visone) tags for colour and circular positions for nodes.
     * If <tt>cstring</tt> does not have length 6 then an empty node is given.
     * The nodeShape index refers to the list in the <tt>shapeTypes</tt> array.
     * If this is out of range the first shape is used.
     * Positions of nodes taken from vertex label given if exits otherwise
     * a default circular layout is used.  Likewise name is taken from vertex label if
     * it exists otherwise label is just the vertex index.
     * Colour and shape taken from vertex number (if exists) otherwise default used.
     * Default size of vertices used.
     * @param PS PrintStream
     * @param nodeNumber used to give node id
     * @param totalNumberNodes total number of nodes, used only if w or h negative
     * @param vl Vertex Label if not null then any name and position stored are used.  Defaults used if vl exits but does not have values required.
     * @param jc JavaColours object set up to have right number of colours.  If null then default colour used
     */
    public void printNodeColoured(PrintStream PS, int nodeNumber, int totalNumberNodes,
            VertexLabel vl, JavaColours jc){
        double w=GraphMLGenerator.DEFAULT_VERTEX_SIZE;
        double h=w;
        printNodeColoured(PS, nodeNumber, totalNumberNodes, vl, -1, -1, w, h, jc);
    }

    /**
     * Prints a node tag.
     * <br>Currently has yfile (Visone) tags for colour and circular positions for nodes.
     * If <tt>cstring</tt> does not have length 6 then an empty node is given.
     * The nodeShape index refers to the list in the <tt>shapeTypes</tt> array.
     * If this is out of range the first shape is used.
     * Positions of nodes taken from vertex label given if exits otherwise
     * a default circular layout is used.  Likewise name is taken from vertex label if
     * it exists otherwise label is just the vertex index.
     * Colour and shape taken from vertex number (if exists) otherwise default used.
     * Default size of vertices used.
     * @param PS PrintStream
     * @param nodeNumber used to give node id
     * @param totalNumberNodes total number of nodes, used only if w or h negative
     * @param vl Vertex Label if not null then any name and position stored are used.  Defaults used if vl exits but does not have values required.
     * @param colourNumber if legal value used to set colour.  Otherwise vertex label used.
     * @param shapeNumber if legal value used to set shape.  Otherwise vertex label used.
     * @param jc JavaColours object set up to have right number of colours.  If null then default colour used
     */
    public void printNodeColoured(PrintStream PS, int nodeNumber, int totalNumberNodes,
            VertexLabel vl, int colourNumber, int shapeNumber, JavaColours jc){
        double w=GraphMLGenerator.DEFAULT_VERTEX_SIZE;
        double h=w;
        printNodeColoured(PS, nodeNumber, totalNumberNodes, vl, colourNumber, shapeNumber, w, h, jc);
    }

    /**
     * Prints a node tag.
     * <br>Currently has yfile (Visone) tags for colour and circular positions for nodes.
     * If <tt>cstring</tt> does not have length 6 then an empty node is given.
     * The nodeShape index refers to the list in the <tt>shapeTypes</tt> array.  
     * If this is out of range the first shape is used.
     * Positions of nodes taken from vertex label given if exits otherwise 
     * a default circular layout is used.  Likewise name is taken from vertex label if
     * it exists otherwise label is just the vertex index.
     * Colour and shape taken from vertex number (if exists) otherwise default used.
     * @param PS PrintStream
     * @param nodeNumber used to give node id
     * @param totalNumberNodes total number of nodes, used only if w or h negative
     * @param vl Vertex Label if not null then any name and position stored are used.  Defaults used if vl exits but does not have values required.
     * @param colourNumber if legal value used to set colour.  Otherwise vertex label used.
     * @param shapeNumber if legal value used to set shape.  Otherwise vertex label used.
     * @param w width of node if negative then default size used
     * @param h hight of node if negative then default size used
      * @param jc JavaColours object set up to have right number of colours.  If null then default colour used
     */
    public void printNodeColoured(PrintStream PS, int nodeNumber, int totalNumberNodes, 
            VertexLabel vl, int colourNumber, int shapeNumber, double w, double h, JavaColours jc){
        double xpos = DEFAULT_LAYOUT_SCALE * nodeNumber / ((float) totalNumberNodes);
        double ypos = 0;
        if (vl!=null && vl.hasPosition()) {
            xpos = vl.getPosition().getX();
            ypos = vl.getPosition().getY();
        } else {
            double posScale = DEFAULT_LAYOUT_SCALE;
            double angle = 2.0 * Math.PI * nodeNumber / ((float) totalNumberNodes);
            xpos = posScale * Math.sin(angle);
            ypos = posScale * Math.cos(angle);
        }
       String name=((vl!=null && vl.hasName())?vl.getName():Integer.toString(nodeNumber));
       String cstring=DEFAULTVERTEXCOLOUR;
       int nodeShape= -1;
       if (vl!=null && jc!=null && vl.hasNumber() ){
           nodeShape= (shapeNumber<0?vl.getNumber():shapeNumber);
           if (colourNumber<1 || colourNumber>jc.getNumberColours()) colourNumber =nodeShape+1;
           cstring=jc.RGB(colourNumber);
       }
       printNodeColoured(PS, nodeNumber, totalNumberNodes,
                                  xpos, ypos, w, h, 
                                  name, cstring, nodeShape);
       
        
    }


    /**
     * Prints a node tag.
     * <br>Currently has yfile (Visone) tags for colour and circular positions for nodes.
     * If <tt>cstring</tt> does not have length 6 then an empty node is given.
     * The nodeShape index refers to the list in the <tt>shapeTypes</tt> array.  
     * If this is out of range the first shape is used.
     * @param PS PrintStream
     * @param nodeNumber used to give node id and position on circle
     * @param totalNumberNodes total number of nodes
     * @param name label to give to node, nothing added if length is zero.
     * @param cstring string of exactly six characters used to represent node colour
     * @param nodeShape index indicating node shape.
     */
    public void printNodeColouredOLD(PrintStream PS, int nodeNumber, int totalNumberNodes,
            String name, String cstring, int nodeShape){
       printlnStartTag(PS,"node "+valueString("id","n"+nodeNumber) );  
       printlnStartTag(PS,"data key=\"d0\" ");
       printlnStartTag(PS,"visone:shapeNode ");
       printlnStartTag(PS,"y:ShapeNode ");
       printNodePosition(PS,nodeNumber, totalNumberNodes);
       if (cstring.length()==6) printlnStartEndTag(PS,"y:Fill color=\"#"+cstring+"\"  transparent=\"false\"");
       else printlnStartEndTag(PS,"y:Fill hasColor=\"false\"  transparent=\"false\"");
       printlnStartEndTag(PS,"y:Shape type=\""+getNodeShape(nodeShape)+"\"");
       //printNodeLabel(PS, name);
       printlnEndTag(PS,"y:ShapeNode ");
       printlnEndTag(PS,"visone:shapeNode ");
       printlnEndTag(PS,"data");
       printNodeNameTag(PS, name);
       //if (name.length()>0) printlnDataTag(PS,"d1",processNodeName(name));
       printlnEndTag(PS,"node");
    }
    /**
     * Prints a node tag.
     * <br>Currently has yfile (Visone) tags for colour and circular positions for nodes.
     * If <tt>cstring</tt> does not have length 6 then an empty node is given.
     * The nodeShape index refers to the list in the <tt>shapeTypes</tt> array.  
     * If this is out of range the first shape is used.
     * @param PS PrintStream
     * @param nodeNumber used to give node id
     * @param totalNumberNodes total number of nodes, used only if w or h negative
     * @param xpos x coordinate unless w or h negative
     * @param ypos y coordinate unless w or h negative
     * @param w width of node if negative then default position and size used
     * @param h hight of node if negative then default position and size used
     * @param name label to give to node, nothing added if length is zero.
     * @param cstring string of exactly six characters used to represent node colour
     * @param nodeShape index indicating node shape, negative produces default shape.
     */
    public void printNodeColoured(PrintStream PS, int nodeNumber, int totalNumberNodes,
                                  double xpos, double ypos, double w, double h, 
                                  String name, String cstring, int nodeShape){
       printNodeColoured(PS, nodeNumber, totalNumberNodes,
                                  xpos, ypos, w, h, 
                                  name, cstring, getNodeShape(nodeShape) );
    }
    /**
     * Prints a node tag.
     * <br>Currently has yfile (Visone) tags for colour and circular positions for nodes.
     * If <tt>cstring</tt> does not have length 6 then an empty node is given.
     * The nodeShape name refers to the list in the <tt>shapeTypes</tt> array.  
     * The name must be given exactly (ignoring case) otherwise a default shape is used.
     * @param PS PrintStream
     * @param nodeNumber used to give node id
     * @param totalNumberNodes total number of nodes, used only if w or h negative
     * @param xpos x coordinate unless w or h negative
     * @param ypos y coordinate unless w or h negative
     * @param w width of node if negative then default position and size used
     * @param h hight of node if negative then default position and size used
     * @param name label to give to node, nothing added if length is zero.
     * @param cstring string of exactly six characters used to represent node colour
     * @param nodeShape exact name of node shape (ignoring case)
     */
    public void printNodeColoured(PrintStream PS, int nodeNumber, int totalNumberNodes,
                                  double xpos, double ypos, double w, double h, 
                                  String name, String cstring, String nodeShape){
       printlnStartTag(PS,"node "+valueString("id","n"+nodeNumber) );  
       printlnStartTag(PS,"data key=\"d0\" ");
       printlnStartTag(PS,"visone:shapeNode ");
       printlnStartTag(PS,"y:ShapeNode ");
       if (w>0 && h>0) printNodePosition(PS, xpos, ypos, w, h);
       else printNodePosition(PS, nodeNumber, totalNumberNodes);
       if (cstring.length()==6) printlnStartEndTag(PS,"y:Fill color=\"#"+cstring+"\"  transparent=\"false\"");
       else printlnStartEndTag(PS,"y:Fill hasColor=\"false\"  transparent=\"false\"");
       printlnStartEndTag(PS,"y:Shape type=\""+shape(nodeShape)+"\"");
       //printNodeLabel(PS, name);
       printlnEndTag(PS,"y:ShapeNode ");
       printlnEndTag(PS,"visone:shapeNode ");
       printlnEndTag(PS,"data");
       printNodeNameTag(PS, name);
       //if (name.length()>0) printlnDataTag(PS,"d1",name);
       printlnEndTag(PS,"node");
    }
    /**
     * Returns a legal node shape.
     * The nodeShape index refers to the list in the <tt>shapeTypes</tt> array.
     * If this is out of range shape {@value #DEFAULT_SHAPE_INDEX} is used.
     * @param n index of node shape for <tt>shapeTypes</tt> array
     * @return legal node shape.
     */
    static private String getNodeShape(int n){
        if ((n<0) || (n>=shapeTypes.length)) return shapeTypes[DEFAULT_SHAPE_INDEX];
        return shapeTypes[n];
    }
    
    /** Returns valid shape name.
         * <br>Compares the input string to the list of <tt>shapeTypes</tt>, 
          * ignoring lower case but demanding full equality otherwise.
          * If invalid a default shape is returned.
         * @param input short name of variable being requested
         * @return valid name of a vertex shape.
         */
    static public String shape(String input)
        {
            String s=input;
            for (int v=0; v<shapeTypes.length;v++) 
              {
                  if (shapeTypes[v].equalsIgnoreCase(s)) return shapeTypes[v];
              }
            return shapeTypes[DEFAULT_SHAPE_INDEX];
        }

    
    /**
     * Prints a nodes position tag.
     * <br>The yfile (Visone) tag for a circular position of a node.
     * Uses constants defined in class to set default sizes.
     * @param PS PrintStream
     * @param nodeNumber used to give node id
     * @param totalNumberNodes total number of nodes
     */
    private void printNodePosition(PrintStream PS, int nodeNumber, int totalNumberNodes){
       double w=DEFAULT_VERTEX_SIZE;
       double h=DEFAULT_VERTEX_SIZE;
       double posScale=DEFAULT_LAYOUT_SCALE;
       double angle = 2.0*Math.PI*nodeNumber/((float)totalNumberNodes);
       double xpos = posScale*Math.sin(angle);
       double ypos = posScale*Math.cos(angle);
       printNodePosition(PS,xpos,ypos,w,h);
    }
    
    /**
     * Prints a nodes position tag.
     * @param PS PrintStream 
     * @param xpos x coordinate
     * @param ypos y coordinate
     * @param w width of node
     * @param h hight of node
     */
    public void printNodePosition(PrintStream PS, double xpos, double ypos, double w, double h){
       printlnStartEndTag(PS,"y:Geometry  "+valueString("x",xpos)+" "+valueString("y",ypos)+" "+valueString("width",w)+" "+valueString("height",h));
    }
    
    /**
     * Prints a nodes position tag.
     * <br>The yfile (Visone) tag for a circular position of a node.
     * @param PS PrintStream
     * @param label used to give node label
     */
    private void printNodeLabel(PrintStream PS, String label){
       printlnStartEndTag(PS,"y:Geometry",label);
    }
    
    
    
    /**
     * Prints basic edge.
     * @param PS PrintStream
     * @param edgeNumber number of edge to be used as edge id
     * @param source refers to id number of source vertex used in node tags
     * @param target refers to id number of target vertex used in node tags
     * @param name label to give to edge, nothing added if length is zero.
     * @deprecated use {@link #printEdge(java.io.PrintStream, int, int, int, java.lang.String) }
     */
    public void printEdgeSimple(PrintStream PS, int edgeNumber, int source, int target, String name){
       printlnStartTag(PS,"edge "+valueString("id","e"+edgeNumber)+" "+valueString("source","n"+source)+" "+valueString("target","n"+target)+" ");
       if (name.length()>0) printlnDataTag(PS,"d5",name);
       printlnEndTag(PS,"edge");
    }
    /**
     * Prints basic straight edge.
     * <p>Edges are black with minimum width, arrows as requested.
     * @param PS PrintStream
     * @param edgeNumber number of edge to be used as edge id
     * @param source refers to id number of source vertex used in node tags
     * @param target refers to id number of target vertex used in node tags
     * @param name label to give to edge, nothing added if length is zero.
     * @param targetArrowsOn true if want an arrow pointing to the target
     */
    public void printEdgeSimple(PrintStream PS, int edgeNumber, int source, int target,
                          String name, boolean targetArrowsOn){
        printEdgeColoured(PS, edgeNumber, source, target, null, null, DUNSET, name, 1.0, targetArrowsOn, GraphMLGenerator.DEFAULTEDGECOLOUR);
    }
    /**
     * Prints edge with all possible options.
     * <p>If arcFactor equals {@link TimGraph.io.GraphMLGenerator#DUNSET} or
     * the source or target positions are null, then a straight edge is used.
     * Otherwise an arc is used.
     * @param PS PrintStream
     * @param edgeNumber number of edge to be used as edge id
     * @param source refers to id number of source vertex used in node tags
     * @param target refers to id number of target vertex used in node tags
     * @param sourcePosition coordinate of source vertex
     * @param targetPosition coordinate of target vertex
     * @param arcFactor factor used to set scale of bend in arc, DUNSET to indicate arc
     * @param name label to give to edge, nothing added if length is zero.
     * @param targetArrowsOn true if want an arrow pointing to the target
     * @param width if less than one then will be set to one.
     * @param cstring string of exactly six characters used to represent node colour
     */
    public void printEdgeColoured(PrintStream PS, int edgeNumber, int source, int target,
                                 Coordinate sourcePosition,  Coordinate targetPosition,
                                 double arcFactor,
                                 String name, double width, boolean targetArrowsOn, String cstring){

        if (arcFactor==DUNSET || sourcePosition==null || targetPosition==null )
            printPolyLineEdge( PS, edgeNumber, source, target,
                                 name, width, targetArrowsOn, cstring);
        else printArcEdge( PS, edgeNumber, source, target,
                            sourcePosition,  targetPosition, arcFactor,
                                 name, width, targetArrowsOn, cstring);
    }
    /**
     * Prints edge as straight line with all possible options.
     * <p>For curved edges see
     * @see GraphMLGenerator.printArcEdge
     * @param PS PrintStream
     * @param edgeNumber number of edge to be used as edge id
     * @param source refers to id number of source vertex used in node tags
     * @param target refers to id number of target vertex used in node tags
     * @param name label to give to edge, nothing added if length is zero.
     * @param targetArrowsOn true if want an arrow pointing to the target
     * @param width if less than one then will be set to one.
     * @param cstring string of exactly six characters used to represent node colour
     */
    public void printPolyLineEdge(PrintStream PS, int edgeNumber, int source, int target,
                                 String name, double width, boolean targetArrowsOn, String cstring){
       printlnStartTag(PS,"edge "+valueString("id","e"+edgeNumber)+" "+valueString("source","n"+source)+" "+valueString("target","n"+target)+" ");
       printlnStartTag(PS,"data key=\"d2\" ");
       printlnStartTag(PS,"visone:polyLineEdge ");
       printlnStartTag(PS,"y:PolyLineEdge ");
       double visualWidth=(width<1.0?1:width);
       printlnStartEndTag(PS,"y:LineStyle type=\"line\" "+valueString("width",visualWidth)+" color=\"#"+(cstring.length()==6?cstring:DEFAULTEDGECOLOUR)+"\"");
       if (targetArrowsOn) printlnStartEndTag(PS,"y:Arrows source=\"none\" target=\"StandardArrow\"");
       printlnEndTag(PS,"y:PolyLineEdge ");
       printlnEndTag(PS,"visone:polyLineEdge ");
       printlnEndTag(PS,"data");
       if (name.length()>0) printlnDataTag(PS,"d5",name);
       printlnEndTag(PS,"edge");
    }

    /**
     * Prints edge as curved arc with all possible options.
     * <p>For straight edges see
     * @see GraphMLGenerator.printPolyLineEdge
     * @param PS PrintStream
     * @param edgeNumber number of edge to be used as edge id
     * @param source refers to id number of source vertex used in node tags
     * @param target refers to id number of target vertex used in node tags
     * @param sourcePosition coordinate of source vertex
     * @param targetPosition coordinate of target vertex
     * @param arcFactor factor used to set scale of bend in arc
     * @param name label to give to edge, nothing added if length is zero.
     * @param targetArrowsOn true if want an arrow pointing to the target
     * @param width if less than one then will be set to one.
     * @param cstring string of exactly six characters used to represent edge colour
     */
    public void printArcEdge(PrintStream PS, int edgeNumber, int source, int target,
                                 Coordinate sourcePosition,  Coordinate targetPosition,
                                 double arcFactor,
                                 String name, double width, boolean targetArrowsOn, String cstring){
       printlnStartTag(PS,"edge "+valueString("id","e"+edgeNumber)+" "+valueString("source","n"+source)+" "+valueString("target","n"+target)+" ");
       printlnStartTag(PS,"data key=\"d2\" ");
       printlnStartTag(PS,"visone:arcEdge");
       printlnStartTag(PS,"y:ArcEdge");
//            <y:Path sx="-0.0" sy="0.0" tx="-0.0" ty="0.0">
//              <y:Point x="84.0999984741211" y="101.79167175292969"/>
//            </y:Path>
       printlnStartTag(PS,"y:Path sx=\"-0.0\" sy=\"0.0\" tx=\"-0.0\" ty=\"0.0\" ");
       Coordinate arcCoordinate = Coordinate.calcArc2DCoordinate(sourcePosition,  targetPosition, arcFactor);
       printlnStartEndTag(PS,"y:Point x=\""+arcCoordinate.getX()+"\" y=\""+arcCoordinate.getY()+"\" ");
       printlnEndTag(PS,"y:Path");
       double visualWidth=(width<1.0?1:width);
       printlnStartEndTag(PS,"y:LineStyle type=\"line\" "+valueString("width",visualWidth)+" color=\"#"+(cstring.length()==6?cstring:DEFAULTEDGECOLOUR)+"\"");
       if (targetArrowsOn) printlnStartEndTag(PS,"y:Arrows source=\"none\" target=\"StandardArrow\"");
       printlnEndTag(PS,"y:ArcEdge");
       printlnEndTag(PS,"visone:arcEdge");
       printlnEndTag(PS,"data");
       if (name.length()>0) printlnDataTag(PS,"d5",name);
       printlnEndTag(PS,"edge");
    }

    

    

    /**
     * Prints a generic tag
     * @param PS PrintStream
     * @param tagName name of the tag
     * @param text contents of tag
     */
    public  void printTextTag(PrintStream PS, String tagName, String text ){
        printlnStartTag(PS,tagName);
        PS.println(text);
        printlnEndTag(PS,tagName);   
    }

    public static String textTag(String tagName, String text ){
        return startTag(tagName)+text+endTag(tagName);   
    }

    public void printName(PrintStream PS, String name){
        printTextTag(PS, "name", name);    
    } 
    
    public void printDescription(PrintStream PS, String description){
        printTextTag(PS, "description", description);    
    } 
   
   

    
}
