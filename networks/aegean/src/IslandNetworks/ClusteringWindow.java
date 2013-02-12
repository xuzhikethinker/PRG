/*
* Adapted by TSE from 
* C:\JAVA\JUNG\src\samples\graph\ClusteringDemo.java in jung 1.7.6 release
*/
package IslandNetworks;

import IslandNetworks.jungInterfaces.EdgePercolationClusterer;
import IslandNetworks.jungInterfaces.GeographicalLayout;
import IslandNetworks.jungInterfaces.GraphToolTip;
import IslandNetworks.jungInterfaces.JungConverter;
import IslandNetworks.jungInterfaces.KKWeightedLayout;
import IslandNetworks.jungInterfaces.ClickableVertexListener;
import IslandNetworks.jungInterfaces.PrintableNetwork;
//import IslandNetworks.ProbabilityDistance;
import IslandNetworks.Edge.EdgeTypeSelection;
import IslandNetworks.Edge.IslandEdge;
import IslandNetworks.Vertex.VertexTypeSelection;
//import IslandNetworks.jungInterfaces.JpegNetwork;
//import IslandNetworks.IslandSite;



import IslandNetworks.jungInterfaces.AriadneEdgePaintFunction;
import IslandNetworks.jungInterfaces.AriadneEdgeStrokeFunction;
import IslandNetworks.jungInterfaces.AriadneVertexPaintFunction;
import IslandNetworks.jungInterfaces.AriadneVertexShapeSizeAspect;
import IslandNetworks.jungInterfaces.GraphML;
import java.awt.Color;
//import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.print.PrinterJob;
//import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException; 
import java.io.FileOutputStream;
import java.io.IOException;

//import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
//import javax.swing.JApplet;
import javax.swing.JButton;
//import javax.swing.JRadioButton;
//import javax.swing.ButtonGroup;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.algorithms.cluster.ClusterSet;
import edu.uci.ics.jung.algorithms.cluster.VertexClusterSet;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberEdgeValue;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;

//import edu.uci.ics.jung.io.PajekNetReader;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.MultiPickedState;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
//import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;

//import edu.uci.ics.jung.visualization.VertexShapeFactory;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.subLayout.CircularSubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayout;
import edu.uci.ics.jung.visualization.subLayout.SubLayoutDecorator;

import TimUtilities.NumbersToString;
import TimUtilities.JavaColours;
import java.awt.Toolkit;
//mport TimUtilities.RationalFraction;
//import TimUtilities.StatisticalQuantity;


/**
 * Shows a given network and allows you to display it in different ways.
 * <p>Adapted by TSE from 
 * <pre>C:\JAVA\JUNG\src\samples\graph\ClusteringDemo.java</pre> in jung 1.7.6 release
 * TODO add checkbox for weak/strong connectivity
 * TODO add check box to reverse the strong/weak selection of the slider
 */
public class ClusteringWindow extends JPanel {

    private static final Object ISLANDCLUSTERKEY = "ISLANDCLUSTERKEY";
    private static final int MAXSLIDERVALUE = 100;
    private static final double DMAXSLIDERVALUE = MAXSLIDERVALUE;
    final static NumbersToString n2s = new NumbersToString(3);
    private double defaultEdgeFraction = 0.5; 
    private String edgeFractionSliderString="NONE";
    private int sliderValue=MAXSLIDERVALUE/2;
    private int isliderValue=MAXSLIDERVALUE/2;
    private Float minEdgeDisplaySize = 0.0f; // maximum size of line to use
    private Float maxEdgeDisplaySize = 10.0f; // maximum size of line to use
    private static final float MAXEDGESTROKESIZE = 20; // Absolute maximum stroke size allowed
    
    private Integer maxVertexDisplaySize = 3; // maximum display size of vertex to use
    private static final int MAXVERTEXDISPLAYSIZE = 40; // absolute maximum size of vertex to use
    
    private ClusterType clusterType;
    private LayoutType layoutType;
    
    AriadneVertexShapeSizeAspect vShapeSizeAspect;
    AriadneVertexPaintFunction vertexPaintFunction;
    AriadneEdgePaintFunction edgePaintFunction;
    AriadneEdgeStrokeFunction edgeStrokeFunction;
    
    public static final Color BACKGROUNDCOLOUR = Color.WHITE;
    public static final Color PERCREMOVEDCOLOUR = Color.LIGHT_GRAY;
    public static final Color REMOVEDCOLOUR = new Color(255,255,255,0); 
            //This should be transparent white otherwise try BACKGROUNDCOLOUR
    //private VertexShapeFactory vertexShapeFactory = new VertexShapeFactory();
    
//    private double maxEdgeWeight=5.0; // need to set this value
    private VisualizationViewer vv;
//    private FileLocation outputFile;
    private JungConverter jc;
    //    private MutableAffineTransformer transformer; Better to use this?
//    private static String JungConverter.SIZ_key;
    private int jpegCount =0;
    private int gmlCount =0;
    
    private islandNetwork inet;
    
    JavaColours javaColours = new JavaColours();
    
    public final static Color[] similarColors =
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
        new Color(30, 250, 100),
        new Color(30, 100, 250),
        new Color(220, 220, 60),
        new Color(250, 100, 30),
        new Color(100, 250, 30),
        new Color(220, 60, 220),
        new Color(100, 30, 250),
        new Color(250, 30, 100)
    };
    

    /** Constructor which sets up window.
     * Does not yet take all parameters from input network.
     * <br>Assumes site positions X,Y are between 0 and 1.
     *@param inNetwork island network containing data.  It is not deep copied.
     *@param size dimension of parent frame in pixels
     */
    public ClusteringWindow(islandNetwork inNetwork, Dimension size) {
        int squareSize=networkVisualisationSize(size.width, size.height);
            initialiseClusteringWindow(inNetwork,  squareSize);
            setUpView(squareSize);
    } // eo constructor

    /**
     * Constructor which does nothing.
     * <br> Can call initialiseClusteringWindow directly to avoid 
     * setting up window.  This way gives access to Jung visualisation of graph.
     */
    public ClusteringWindow() {
    }
    
   public void initialiseClusteringWindow(islandNetwork inetinput, int squareSize) {
            
            inet = inetinput; // do not deep copy
            inet.calcNetworkStats();
            
            jc = new JungConverter(inet);
        
            vertexPaintFunction = new AriadneVertexPaintFunction(new MultiPickedState(),ClusteringWindow.ISLANDCLUSTERKEY);
            vShapeSizeAspect = new AriadneVertexShapeSizeAspect(inet,1);//maxVertexDisplaySize);
            edgePaintFunction = new AriadneEdgePaintFunction(inet, ClusteringWindow.ISLANDCLUSTERKEY, ClusteringWindow.PERCREMOVEDCOLOUR);
            edgeStrokeFunction = new AriadneEdgeStrokeFunction(inet, ClusteringWindow.ISLANDCLUSTERKEY, ClusteringWindow.PERCREMOVEDCOLOUR, minEdgeDisplaySize, maxEdgeDisplaySize);

            clusterType = new ClusterType();
            layoutType = new LayoutType(squareSize); // leave some spare pixels for tabs and menus
             
            // unlike a JApplet, a JFrame (and JLabels?) do not have an init() so this must go in constructor
            //see p309, sec 7.7 of javanotes 4             
    } // eo constructor
    

   /**
    * Calculates the size of the square region used to display network.
    * <p>Leaves some room in the whole frame for tabs etc.
    * Uses current frame width.
    * @return length of sides of the square region used to display network
    */
    private Dimension networkVisualisationDimension(){
        int l=networkVisualisationSize(this.getWidth(), this.getHeight());
        return new Dimension(l,l);
    }

   /**
    * Calculates the size of the square region used to display network.
    * <p>Leaves some room in the whole frame for tabs etc.
    * @param d dimension of total frame.
    * @return length of sides of the square region used to display network
    */
    private Dimension networkVisualisationDimension(Dimension d){
        int l=networkVisualisationSize(d.width, d.height);
        return new Dimension(l,l);
    }

   /**
    * Calculates the size of the square region used to display network.
    * <p>Leaves some room in the whole frame for tabs etc.
    * Uses current frame width.
    * @return integer length of sides of the square region used to display network
    */
    private int networkVisualisationSize(){
        return networkVisualisationSize(this.getWidth(), this.getHeight());
    }

   /**
    * Calculates the size of the square region used to display network.
    * <p>Leaves some room in the whole frame for tabs etc.
    * Should be called by all other <tt>networkVisualisation*</tt> methods.
    * @param frameWidth total width of frame
    * @param frameHeight total width of frame
    * @return integer length of sides of the square region used to display network
    */
    private int networkVisualisationSize(int frameWidth, int frameHeight){
        return Math.max(Math.min(frameWidth, frameHeight)-100,200);
    }

    //ClusteringLayout layout;
// NOT USED UNLESS IN A JAPPLET not a FRAME or JLABEL?    
    public void start() {
        
        try
        {
            setUpView(networkVisualisationSize());
        }
        catch (Exception e)
        {
            System.out.println("Error "+e);
            e.printStackTrace();
        }
    }

    // public needed for multiple analysis jpeg output
/**
 * Sets up a default view of network for printing or jpeg output.
 */
    public void setUpPrintableView(int width, int height) {
        layoutType.setlayoutType("Geo"); // sets to be geographical
        clusterType.setclusterType("Perc"); // sets clustering to be percolation
        defaultEdgeFraction = 3.0/inet.edgeSet.getNumberSites();
        setUpJungViewer(jc.getGraph( ), new Dimension(width,height));
        vv.setSize(width, height);
    }

    /**
     * Set up the JUNG network viewer.
     * @param graph the network to be displayed
     * @param preferredDimension the size of the region to be used for the network.
     */
    public void setUpJungViewer(Graph graph, Dimension preferredDimension){
        final PickedState ps = new MultiPickedState();
        PluggableRenderer pr = new PluggableRenderer();
        
        vertexPaintFunction.setPickedState(ps);
        pr.setVertexPaintFunction(vertexPaintFunction);
                
        pr.setVertexShapeFunction(vShapeSizeAspect);

        // *** TO MAKE EDGES INVISIBLE USE pr.setEdgeIncludePredicate(Predicate p
        //     but need to understand this   
        pr.setEdgePaintFunction(edgePaintFunction);

        // Draws edge
        pr.setEdgeStrokeFunction(edgeStrokeFunction);

//        // next two lines preserve size, not sure if we want this
//        int width = vv.getWidth();
//        int height = vv.getHeight();
        vv = new VisualizationViewer(layoutType.getLayoutType(graph), pr, preferredDimension);
        vv.setBackground( BACKGROUNDCOLOUR );
        vv.setPickedState(ps);
        vv.setSize(preferredDimension);
}
    
    
    
     //    private void setUpView() throws IOException {
    // public needed for multiple analysis jpeg output
    /**
     * Sets up the whole clustering window
     * @param squareSize size of square network region to display
     */
    private void setUpView(int jungSize) {

        Dimension panelSizeDim = this.getSize();
        Dimension screenSizeDim = Toolkit.getDefaultToolkit().getScreenSize();


//  Set up main network display
        System.out.println("--- jung VisualisationViewer size "+jungSize);
        Dimension jungDimension = new Dimension(jungSize,jungSize);
        //Dimension jungDimension = this.networkVisualisationDimension();
        setUpJungViewer(jc.getGraph(), jungDimension);
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        vv.setGraphMouse(gm);
        vv.setPickSupport(new ShapePickSupport());
        vv.addGraphMouseListener(new ClickableVertexListener(new UserDatumNumberVertexValue(JungConverter.VID_key), inet.siteSet));
        final GraphToolTip gTTF = new GraphToolTip(new UserDatumNumberVertexValue(JungConverter.VID_key), inet.siteSet, new UserDatumNumberEdgeValue (JungConverter.EID_key), inet.edgeSet);
        vv.setToolTipFunction(gTTF);

        

        
        JPanel parameterGrid = new JPanel(new GridLayout(5,2)); // (nrows,ncols=4) also looks OK
        
        // set up MAXIMUM VERTEX DISPLAY SIZE input box to set vertex size to be used for maximum vertex weight
        //final Box maximumVertexSizeBox = Box.createHorizontalBox();
        final JLabel maximumVertexSizeLabel = new JLabel("Vertex Display Size", SwingConstants.RIGHT);
        final JTextField maximumVertexSizeText = new JTextField(Double.toString(inet.DisplayMaxVertexScale),4);
        //final JComboBox vertexChooser = new JComboBox();
        maximumVertexSizeLabel.setToolTipText("Display size of vertices of weight 1.0, unless negative when absolute value sets size of largest valued vertex.");
        maximumVertexSizeText.setToolTipText("Display size of vertices of weight 1.0, unless negative when absolute value sets size of largest valued vertex.");
        maximumVertexSizeText.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                inet.DisplayMaxVertexScale=Double.parseDouble(maximumVertexSizeText.getText());
                setVertexDisplaySizes();
            //setMaxVertexSize(Double.parseDouble(maximumVertexSizeText.getText() )) ; 
            }
        });
        parameterGrid.add(maximumVertexSizeLabel);            
        parameterGrid.add(maximumVertexSizeText);     
        

        // set up zero EDGE WEIGHT input box to set edge value to be used for zero edge colour
        //final Box zeroEdgeWeightBox = Box.createHorizontalBox();
        final JLabel zeroEdgeWeightLabel = new JLabel("edge weight zero", SwingConstants.RIGHT);
        //Double.toString(inet.edgeSet.displayZeroEdgeWeight);
        String outString = String.format("%8.6f",inet.edgeSet.displayZeroEdgeWeight);
        final JTextField zeroEdgeWeightText = new JTextField(outString,4);
        zeroEdgeWeightLabel.setToolTipText("Minimum weight of edge shown in display");
        zeroEdgeWeightText.setToolTipText("Minimum weight of edge shown in display");
        zeroEdgeWeightText.addActionListener(new ActionListener ()  {
            public void actionPerformed(ActionEvent e) { inet.edgeSet.displayZeroEdgeWeight=Double.parseDouble(zeroEdgeWeightText.getText()); } } );
        parameterGrid.add(zeroEdgeWeightLabel);            
        parameterGrid.add(zeroEdgeWeightText);            
            
       // set up MINIMUM EDGE WEIGHT input box to set edge value to be used for minimum edge colour
        //final Box minimumEdgeWeightBox = Box.createHorizontalBox();
        final JLabel minimumEdgeWeightLabel = new JLabel("edge weight min", SwingConstants.RIGHT);
        final JTextField minimumEdgeWeightText = new JTextField(Double.toString(inet.edgeSet.displayMinimumEdgeWeight),4);
        minimumEdgeWeightLabel.setToolTipText("Maximum weight of any edges given the thinest displayed edge size");
        minimumEdgeWeightText.setToolTipText("Maximum weight of any edges given the thinest displayed edge size");
        minimumEdgeWeightText.addActionListener(new ActionListener ()  {
            public void actionPerformed(ActionEvent e) { inet.edgeSet.displayMinimumEdgeWeight=Double.parseDouble(minimumEdgeWeightText.getText()); } } );
        parameterGrid.add(minimumEdgeWeightLabel);            
        parameterGrid.add(minimumEdgeWeightText);            
            
        // set up MAXIMUM EDGE WEIGHT input box to set edge value to be used for maximum edge colour
        //final Box maximumEdgeWeightBox = Box.createHorizontalBox();
        final JLabel maximumEdgeWeightLabel = new JLabel("edge weight max", SwingConstants.RIGHT);
        final JTextField maximumEdgeWeightText = new JTextField(Double.toString(inet.edgeSet.displayMaximumEdgeWeight),4);
        maximumEdgeWeightLabel.setToolTipText("Thickest lines associated with edges of this edge weight or greater");
        maximumEdgeWeightText.setToolTipText("Thickest lines associated with edges of this edge weight or greater");
        maximumEdgeWeightText.addActionListener(new ActionListener ()  {
            public void actionPerformed(ActionEvent e) { inet.edgeSet.displayMaximumEdgeWeight=Double.parseDouble(maximumEdgeWeightText.getText()); } } );
        parameterGrid.add(maximumEdgeWeightLabel);            
        parameterGrid.add(maximumEdgeWeightText);            

        // set up MAXIMUM EDGE STROKE SIZE input box to set edge value to be used for maximum edge colour
        //final Box maximumEdgeStrokeBox = Box.createHorizontalBox();
        final JLabel maximumEdgeStrokeLabel = new JLabel("Display width of maximum line", SwingConstants.RIGHT);
        maximumEdgeStrokeLabel.setToolTipText("Maximum width of lines when drawing edges");
        final JTextField maximumEdgeStrokeText = new JTextField(maxEdgeDisplaySize.toString(),4);
        maximumEdgeStrokeLabel.setToolTipText("Maximum width of lines in display");
        maximumEdgeStrokeText.setToolTipText("Maximum width of lines in display");
        maximumEdgeStrokeText.addActionListener(new ActionListener ()  {
            public void actionPerformed(ActionEvent e) { setMaxEdgeStrokeSize(Float.parseFloat(maximumEdgeStrokeText.getText() ) ); } } );
        parameterGrid.add(maximumEdgeStrokeLabel);            
        parameterGrid.add(maximumEdgeStrokeText);            

        
        //Create slider to adjust the number of edges to remove when clustering
        defaultEdgeFraction = 3.0/inet.edgeSet.getNumberSites(); // start with about 3 edges per site
        final JSlider edgeFractionSlider = this.makePercentageSlider(new Dimension(210, 50), "Sets the proportion of black edges used to cluster vertices", defaultEdgeFraction);

        final JPanel sliderPanel = new JPanel();
        sliderPanel.setOpaque(true);
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
        sliderPanel.add(Box.createVerticalGlue());
        sliderPanel.add(edgeFractionSlider);

       final JToggleButton groupVertices = new JToggleButton("Group Clusters");
        groupVertices.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                    clusterAndRecolor(layoutType.getLayoutType(jc.getGraph()), edgeFractionSlider.getValue(), 
                            similarColors, e.getStateChange() == ItemEvent.SELECTED);
            }});
       groupVertices.setToolTipText("If button is depressed then network is coloured using clustering method");

        
        // TODO adapt to change label to reflect clusterType.getCurrentTypeString()
        edgeFractionSliderString = "Clustering percentage: "; // change label to reflect cluster class
        final String eastSize = edgeFractionSliderString + edgeFractionSlider.getValue();
        
        final TitledBorder sliderBorder = BorderFactory.createTitledBorder(eastSize);
        sliderPanel.setBorder(sliderBorder);
        sliderPanel.add(Box.createVerticalGlue());
  
        edgeFractionSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    sliderValue = source.getValue();
                    clusterAndRecolor(layoutType.getLayoutType(jc.getGraph()), sliderValue, similarColors,
                            groupVertices.isSelected());
                    //int sV = edgeFractionSlider.getValue();
                    sliderBorder.setTitle(edgeFractionSliderString + sliderValue);
                    sliderPanel.repaint();
                    vv.validate();
                    vv.repaint();  //redraw jung graph visualisation when slider moved
                }
            }
        }); // end
        
     // initial clustering
       clusterAndRecolor(layoutType.getLayoutType(jc.getGraph()), edgeFractionSlider.getValue() , similarColors, groupVertices.isSelected());


        
        //Create slider to adjust the number of edges to remove when clustering
        double defaultInfluenceProbability=0.5;
        final JSlider influenceProbabilitySlider = this.makePercentageSlider(new Dimension(210, 50), "Sets the inluence probability", defaultInfluenceProbability);

        final JPanel isliderPanel = new JPanel();
        isliderPanel.setOpaque(true);
        isliderPanel.setLayout(new BoxLayout(isliderPanel, BoxLayout.Y_AXIS));
        isliderPanel.add(Box.createVerticalGlue());
        isliderPanel.add(influenceProbabilitySlider);

        // TODO adapt to change label to reflect clusterType.getCurrentTypeString()
        final String iedgeFractionSliderString = "Influence probability percentage: "; 
        final String ieastSize = iedgeFractionSliderString + influenceProbabilitySlider.getValue()+" (distance="+ProbabilityDistance.probabilityToDistanceString(isliderValue/ClusteringWindow.DMAXSLIDERVALUE)+")";
        final TitledBorder isliderBorder = BorderFactory.createTitledBorder(ieastSize);
        isliderPanel.setBorder(isliderBorder);
        isliderPanel.add(Box.createVerticalGlue());
        

        influenceProbabilitySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    isliderValue = source.getValue();
                    inet.calcInfluence(isliderValue/ClusteringWindow.DMAXSLIDERVALUE);
                    System.out.println("!!! Average Influence Weight= "+inet.edgeSet.getAverage(IslandEdge.influenceWeightINDEX)+", prob ="+inet.getInfluenceProbability()+" (distance="+ProbabilityDistance.probabilityToDistanceString(isliderValue/ClusteringWindow.DMAXSLIDERVALUE)+")");
                    System.out.println("!!! Average Betweenness Weight= "+inet.edgeSet.getAverage(IslandEdge.betweennessINDEX)+", prob ="+inet.getInfluenceProbability()+" (distance="+ProbabilityDistance.probabilityToDistanceString(isliderValue/ClusteringWindow.DMAXSLIDERVALUE)+")");
//                    inet.calcBroadcast(isliderValue/ClusteringWindow.DMAXSLIDERVALUE);
                    isliderBorder.setTitle(
                        iedgeFractionSliderString + isliderValue+" (distance="+ProbabilityDistance.probabilityToDistanceString(isliderValue/ClusteringWindow.DMAXSLIDERVALUE)+")");
                    isliderPanel.repaint();
                    vv.validate();
                    vv.repaint();  //redraw jung graph visualisation when slider moved
                }
            }
        }); // end

// initial influence calculation
        inet.calcInfluence(isliderValue/ClusteringWindow.DMAXSLIDERVALUE);
//        inet.calcBroadcast(isliderValue/ClusteringWindow.DMAXSLIDERVALUE);


       
       
        
        
        // set up layout chooser use getSelectedIndex() to access
        final JComboBox layoutChooser = new JComboBox();
        layoutChooser.setToolTipText("Choose type of layout of network");
        for (int i=0; i<layoutType.numberTypes; i++) layoutChooser.addItem(layoutType.getString(i));
        layoutChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                layoutType.value = layoutChooser.getSelectedIndex();
                vv.setGraphLayout(layoutType.getLayoutType(jc.getGraph()));
                vv.validate();
                vv.repaint();
            }
        });
        layoutChooser.setSelectedIndex(layoutType.value);

        
// set up cluster chooser use getSelectedIndex() to access
        final JComboBox clusterChooser = new JComboBox();
        clusterChooser.setToolTipText("Choose Edge Selector");
        for (int i=0; i<clusterType.numberTypes; i++) clusterChooser.addItem(clusterType.getString(i));
        clusterChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //clusterType.setclusterType(arg0.getActionCommand());
                //JComboBox cb = (JComboBox)arg0.getSource();
                //clusterType.setclusterType((String) cb.getSelectedItem());
                //clusterType.setclusterType((String) clusterChooser.getSelectedItem());
                clusterType.value = clusterChooser.getSelectedIndex();
                edgeFractionSliderString = clusterType.getCurrentTypeString()+" clusterer percentage ";
                edgeFractionSlider.setEnabled(!clusterType.noVariableClustering());
                vv.restart();
                //start();
                //setUpView();
            }
        });
        clusterChooser.setSelectedIndex(clusterType.value);
        edgeFractionSlider.setEnabled(!clusterType.noVariableClustering());

        
         // set up vertex type chooser use getSelectedIndex() to access
        final JComboBox vertexChooser = new JComboBox();
        vertexChooser.setToolTipText("Choose type of vertex to display");
        for (int i=0; i<VertexTypeSelection.numberTypes; i++) vertexChooser.addItem(VertexTypeSelection.name[i]);
        vertexChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inet.DisplayVertexType.setValue(vertexChooser.getSelectedIndex());
                setVertexDisplaySizes();
                //maxVertexDisplayValue=???
                vv.restart();
                }
        });
        vertexChooser.setSelectedIndex(inet.DisplayVertexType.getValue());

// set up edge type chooser use getSelectedIndex() to access
        final JComboBox edgeChooser = new JComboBox();
        edgeChooser.setToolTipText("Choose type of edge to display");
        for (int i=0; i<EdgeTypeSelection.numberTypes; i++) edgeChooser.addItem(EdgeTypeSelection.name[i]);
        edgeChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inet.edgeSet.DisplayEdgeType.setValue(edgeChooser.getSelectedIndex());
                jc.replaceAllEdges();
                vv.restart();
                }
        });
        edgeChooser.setSelectedIndex(inet.edgeSet.DisplayEdgeType.getValue());
        
        
        
// *** Define the buttons
//Define  redraw button
        JButton redrawButton = new JButton("Redraw");
        redrawButton.setToolTipText("Redraw network using current parameter values");
        redrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVertexDisplaySizes(Double.parseDouble(maximumVertexSizeText.getText() ), vertexChooser.getSelectedIndex() );
 // Not sure why these are needed when the etxt box should set these, but they are
                inet.edgeSet.displayZeroEdgeWeight=Double.parseDouble(zeroEdgeWeightText.getText());
                inet.edgeSet.displayMinimumEdgeWeight=Double.parseDouble(minimumEdgeWeightText.getText());
                inet.edgeSet.displayMaximumEdgeWeight=Double.parseDouble(maximumEdgeWeightText.getText());
                setMaxEdgeStrokeSize(Float.parseFloat(maximumEdgeStrokeText.getText()));
                clusterAndRecolor(layoutType.getLayoutType(jc.getGraph()), sliderValue, similarColors,
                            groupVertices.isSelected());
                vv.setGraphLayout(layoutType.getLayoutType(jc.getGraph()));
                vv.restart();
                vv.validate();
                vv.repaint();
                //start();
                //setUpView();
            }
        });
        
//Define   restart button
        JButton restartButton = new JButton("Restart");
        restartButton.setToolTipText("Restart network drawing routines current parameter values");
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVertexDisplaySizes(Double.parseDouble(maximumVertexSizeText.getText() ), vertexChooser.getSelectedIndex() );
 // Not sure why these are needed when the etxt box should set these, but they are
                inet.edgeSet.displayZeroEdgeWeight=Double.parseDouble(zeroEdgeWeightText.getText());
                inet.edgeSet.displayMinimumEdgeWeight=Double.parseDouble(minimumEdgeWeightText.getText());
                inet.edgeSet.displayMaximumEdgeWeight=Double.parseDouble(maximumEdgeWeightText.getText());
                setMaxEdgeStrokeSize(Float.parseFloat(maximumEdgeStrokeText.getText()));
                jc.replaceAllEdges();
                clusterAndRecolor(layoutType.getLayoutType(jc.getGraph()), sliderValue, similarColors,
                            groupVertices.isSelected());
                setUpJungViewer(jc.getGraph(), networkVisualisationDimension());
                vv.setGraphLayout(layoutType.getLayoutType(jc.getGraph()));
                vv.restart();
                vv.validate();
                vv.repaint();
                System.out.println("!!! Jung display has "+jc.getNumberEdges()+" edges, width="+vv.getWidth()+", height="+vv.getHeight());
                //start();
                //setUpView();
            }
        });

//Define   reheat button
        JButton reheatButton = new JButton("Reheat");
        reheatButton.setToolTipText("Monte Carlo network reheating starting from current layout");
        reheatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
//                JOptionPane.showMessageDialog(null, "Reheat button does nothing");
//                System.err.println("Reheat button does nothing");    
                islandNetwork reheatNet = new islandNetwork(inet);
                reheatNet.message.setInformationLevel(2);
                reheatNet.updateMode.setFromName("MC");
                reheatNet.monteCarloStartMode.setFromName("Old");
                InputParameterFrame iw = new InputParameterFrame(reheatNet);
            }
        });
        
        
        // Define information button
        final JButton informationButton = new JButton("Info");
        informationButton.setToolTipText("Gives debugging information");
        informationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String s=inet.edgeSet.displaySizeString(": ",", ")
                        +", max edge display size:"+edgeStrokeFunction.getMaxEdgeSize()
                        +",\n Clustering: "+clusterType.getCurrentTypeString()
                        +", Layout: "+layoutType.getCurrentTypeString();
                JOptionPane.showMessageDialog(null, s);
            }
        });
        
        // Define information button
        final JButton gmlButton = new JButton("GraphML");
        gmlButton.setToolTipText("Writes a GraphML file for Visone");
        gmlButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //JOptionPane.showMessageDialog(null, "Empty button does nothing");
                writeGMLImage(vv);
            }
        });
        
// Define jpg button
        final JButton jpgButton = new JButton("JPEG");
        jpgButton.setToolTipText("Writes a jpeg file of network");
        jpgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //Dimension jpegDim = networkVisualisationDimension();
                //Dimension jpegDim = new Dimension (3800,3800);
                //System.out.println("pressed jpg button");
                writeJPEGImage();
            }
        });

// Define eps button
        final JButton epsButton = new JButton("EPS");
        epsButton.setToolTipText("Writes a eps file of network");
        epsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //System.out.println("pressed eps button");
                writeEPSImage();
            }
        });

// Define print button
        final JButton printButton = new JButton("Print");
        printButton.setToolTipText("Prints a jpeg file of network");
        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) { 
                    PrintableNetwork pn = new PrintableNetwork(vv, inet.getNameString(", "));
                    PrinterJob printJob = PrinterJob.getPrinterJob();
                    printJob.setPrintable(pn);
                    if (printJob.printDialog()) {
                        try {
                            printJob.print();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
            }
        });
        
// Define save button
        final JButton saveButton = new JButton("Save");
        saveButton.setToolTipText("Saves files for this network ");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                writeJPEGImage(vv, networkVisualisationDimension());
                inet.saveFiles("#",5);
            }
        });


        JPanel grid = new JPanel(new GridLayout(3,3,3,3));
        grid.add(redrawButton);
        grid.add(restartButton);
        grid.add(reheatButton);
        grid.add(informationButton);
        //grid.add(emptyButton);
        grid.add(groupVertices);
        grid.add(jpgButton);
        grid.add(epsButton);
        //grid.add(gmlButton);
        grid.add(printButton);
        grid.add(saveButton);

// *** end of button definitions
        
        
        
        
        JPanel name = new JPanel(new GridLayout(4,2));
        name.add(new JLabel("basic name:  ",JLabel.RIGHT));
        name.add(new JLabel(inet.outputFile.getBasicRoot(),JLabel.LEFT));
        name.add(new JLabel("parameters:  ",JLabel.RIGHT));
        name.add(new JLabel(inet.outputFile.getParameterName(),JLabel.LEFT));
        name.add(new JLabel("run:  ",JLabel.RIGHT));
        name.add(new JLabel(""+inet.outputFile.sequenceNumber,JLabel.LEFT));
        name.add(new JLabel("energy:  ",JLabel.RIGHT));
        //name.add(new JLabel(n2s.toString(inet.globalProperties.getEnergy(),5),JLabel.LEFT));
        name.add(new JLabel(String.format("%10.4g",inet.globalProperties.getEnergy()),JLabel.LEFT));

        JPanel inputValues = new JPanel(new GridLayout(2,5));
        inputValues.add(new JLabel("mu",JLabel.CENTER));
        inputValues.add(new JLabel("j",JLabel.CENTER));
        inputValues.add(new JLabel("k",JLabel.CENTER));
        inputValues.add(new JLabel("l",JLabel.CENTER));
        inputValues.add(new JLabel("s",JLabel.CENTER));
        inputValues.add(new JLabel( n2s.toString(inet.Hamiltonian.edgeSource) , JLabel.CENTER));
        inputValues.add(new JLabel( n2s.toString(inet.Hamiltonian.vertexSource) , JLabel.CENTER));
        inputValues.add(new JLabel( n2s.toString(inet.Hamiltonian.kappa) , JLabel.CENTER));
        inputValues.add(new JLabel( n2s.toString(inet.Hamiltonian.lambda) , JLabel.CENTER));
        inputValues.add(new JLabel( n2s.toString(inet.Hamiltonian.distanceScale) , JLabel.CENTER));
        
        JPanel outputValues = new JPanel(new GridLayout(2,5));
        boolean saturated = false;
        if (inet.vertexMode.maxValueModeOn && (inet.siteSet.siteWeightStats.maximum>inet.vertexMode.maximumValue*0.95)) saturated=true;
        outputValues.add(new JLabel("<Sv>",JLabel.CENTER));
        outputValues.add(new JLabel("Sv.Max",JLabel.CENTER));
        outputValues.add(new JLabel("<Str.In> ",JLabel.CENTER));
        //outputValues.add(new JLabel(" ",JLabel.CENTER));
        JLabel vmaxlabel = new JLabel("v.Max" , JLabel.CENTER);
        if (saturated) 
                {
                    vmaxlabel.setForeground(Color.RED); // Display red text...
                    vmaxlabel.setBackground(Color.BLACK); // on a black background...
                    //vmaxlabel.setFont(new Font("Serif",Font.BOLD,18)); // in a big bold font.
                    vmaxlabel.setOpaque(true);
                } 
        outputValues.add(vmaxlabel);
        outputValues.add(new JLabel("E",JLabel.CENTER));
        
        outputValues.add(new JLabel( n2s.toString(inet.siteSet.siteWeightStats.getAverage() ) , JLabel.CENTER));
        outputValues.add(new JLabel( n2s.toString(inet.siteSet.siteWeightStats.maximum ) , JLabel.CENTER));
        outputValues.add(new JLabel( n2s.toString(inet.siteSet.siteStrengthInStats.getAverage()) , JLabel.CENTER));
        vmaxlabel = new JLabel( n2s.toString(inet.siteSet.siteValueStats.maximum) , JLabel.CENTER);
        if (saturated) 
                {
                    vmaxlabel.setForeground(Color.RED); // Display red text...
                    vmaxlabel.setBackground(Color.BLACK); // on a black background...
                    //vmaxlabel.setFont(new Font("Serif",Font.BOLD,18)); // in a big bold font.
                    vmaxlabel.setOpaque(true);
                } 
        outputValues.add(vmaxlabel);
        outputValues.add(new JLabel( String.format("%10.4g",inet.globalProperties.getEnergy()) , JLabel.CENTER));
        
 
        
// put control components together
        // sizing appears to have no effect - ??? WHY NOT ???
        String outputNameRoot = inet.outputFile.getFullFileRoot();
        int w= Math.max(outputNameRoot.length()*6,90);
        Box controlBox = Box.createVerticalBox();
        //public Dimension(int width, int height)
        Dimension cbPrefDim = new Dimension(Math.min(jungSize/2,screenSizeDim.width-jungSize),jungSize);
//        Dimension cbPrefDim = new Dimension(Math.min(100,screenSizeDim.width-jungSize),jungSize);
        controlBox.setPreferredSize(cbPrefDim);

        controlBox.add(name);
        controlBox.add(inputValues);
        controlBox.add(outputValues);
        controlBox.add(grid);
        controlBox.add(sliderPanel);
        controlBox.add(isliderPanel);
        
        JPanel chooserGrid = new JPanel(new GridLayout(5,2,3,3));
        addJComboBoxToGrid(chooserGrid, "Mouse Mode", gm.getModeComboBox());
        addJComboBoxToGrid(chooserGrid, "Clusterer", clusterChooser);
        addJComboBoxToGrid(chooserGrid, "Layout", layoutChooser);
        addJComboBoxToGrid(chooserGrid, "Vertex Type", vertexChooser);
        addJComboBoxToGrid(chooserGrid, "Edge Type", edgeChooser);
        
//        chooserGrid.add(new JLabel("Mouse Mode",JLabel.RIGHT));
//        JComboBox mouseModeComboBox = gm.getModeComboBox();
//        mouseModeComboBox.setToolTipText("Set behaviour of mouse when you click and hold on the display");
//        chooserGrid.add(mouseModeComboBox);
//        JLabel clustererLabel = new JLabel("Clusterer",JLabel.RIGHT);
//        clustererLabel.setToolTipText(clusterChooser.getToolTipText());
//        chooserGrid.add(clustererLabel);
//        chooserGrid.add(clusterChooser);
//        JLabel layoutLabel = new JLabel("Layout",JLabel.RIGHT);
//        layoutLabel.setToolTipText(layoutChooser.getToolTipText());
//        chooserGrid.add(layoutLabel);
//        chooserGrid.add(layoutChooser);
//        chooserGrid.add(new JLabel("Vertex Type",JLabel.RIGHT));
//        chooserGrid.add(vertexChooser);
//        chooserGrid.add(new JLabel("Edge Type",JLabel.RIGHT));
//        chooserGrid.add(edgeChooser);
        
        controlBox.add(chooserGrid);
        
        controlBox.add(parameterGrid);

        // Finally set up main JPanel
        // use 'this' to refer to the JPanel being created
        // use default flow layout
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        
        int netVisSize =  networkVisualisationSize();// can be done better?
        gzsp.setSize(netVisSize,netVisSize);
        this.add(gzsp);
        this.add(controlBox);
    } //eo setUpView
 
    /**
     * Takes an existing JComboBox adds it and a JLabel to existing JPanel
     * <br>JLabel takes its tool tip from the combo box.  Grid should be (n by 2).
     * @param grid (n by 2) JPanel grid
     * @param comboBoxName text for JLabel next to JComboBox
     * @param comboBox existing JComboBox
     */
    private void addJComboBoxToGrid(JPanel grid, String comboBoxName, JComboBox comboBox){
        JLabel label = new JLabel(comboBoxName,JLabel.RIGHT);
        label.setToolTipText(comboBox.getToolTipText());
        grid.add(label);
        grid.add(comboBox);

    }
    
    /**
     * Creates a percentage slider.
     * @param d dimension of slider
     * @param toolTipText text for tool tip
     * @param defaultFraction initial setting of slider
     * @return
     */
    private JSlider makePercentageSlider(Dimension d, String toolTipText, double defaultFraction){
        JSlider edgeFractionSlider = new JSlider(JSlider.HORIZONTAL);
        edgeFractionSlider.setToolTipText(toolTipText);
        edgeFractionSlider.setBackground(Color.WHITE);
        edgeFractionSlider.setPreferredSize(d);
        edgeFractionSlider.setPaintTicks(true);
        edgeFractionSlider.setMaximum(MAXSLIDERVALUE);
        edgeFractionSlider.setMinimum(0);
        sliderValue = (int) (MAXSLIDERVALUE*defaultFraction);
        edgeFractionSlider.setValue(sliderValue);
        edgeFractionSlider.setMinorTickSpacing(MAXSLIDERVALUE/20);
        edgeFractionSlider.setMajorTickSpacing(MAXSLIDERVALUE/5);
        edgeFractionSlider.setPaintLabels(true);
        edgeFractionSlider.setPaintTicks(true);
        return edgeFractionSlider;
    }
    
    
 /**
 * Sets the maximum edge display size and appropriate scaling.
 *@param value the display size of an edge of size maxEdgeWeight
 */
           private void setMaxEdgeStrokeSize(Float value)
            {
                maxEdgeDisplaySize =value;
                if (maxEdgeDisplaySize<0) maxEdgeDisplaySize= 0f;
                if (maxEdgeDisplaySize>MAXEDGESTROKESIZE) maxEdgeDisplaySize=MAXEDGESTROKESIZE;
                edgeStrokeFunction.setEdgeSizes(minEdgeDisplaySize, maxEdgeDisplaySize);
                //inet.DisplayMaxEdgeScale
            } 
            
/**
 * Sets the vertex display sizes and related variables.
 * <p>Call whenever there is a change to vertex display variables.
 * <br>If <tt>displayMaxVertexScale</tt> is positive then this is the display 
 * size to use for vertex of value 1.0. Otherwise the absolute value is used for 
 * the display size of the largest valued vertex.
 * <p>If <tt>displayVertexTypeIndex</tt> does not correspond to known site value 
 * then all sites are set to size one.
 * @param displayMaxVertexScale sets islandNetwork parameter of this name
 * @param displayVertexTypeIndex sets vertex type to be displayed
 */
            private void setVertexDisplaySizes(double displayMaxVertexScale, int displayVertexTypeIndex) 
            {  
                inet.DisplayMaxVertexScale=displayMaxVertexScale; //is this OK?
                inet.DisplayVertexType.setValue(displayVertexTypeIndex);
                setVertexDisplaySizes();
            }
/**
 * Sets the vertex display sizes and related variables.
 * <p>Call whenever there is a change to vertex display variables.
 * <br>Uses internal islandNetwork display variables.
 * <br>If <tt>displayMaxVertexScale</tt> is positive then this is tthe display 
 * size to use for vertex of value 1.0. Otherwise the absolute value is used for 
 * the display size of the largest valued vertex.
 * <p>If <tt>displayVertexTypeIndex</tt> does not correspond to known site value 
 * then all sites are set to size one.
 */
            private void setVertexDisplaySizes() 
            {  
                inet.siteSet.setAllDisplaySizes(inet.DisplayVertexType.getValueIndex(), inet.DisplayMaxVertexScale);
                //if (maxVertexDisplaySize>MAXVERTEXDISPLAYSIZE) maxVertexDisplaySize=MAXVERTEXDISPLAYSIZE; 
                //vShapeSizeAspect.setScaling(inet.DisplayMaxVertexScale);   
            }

// -------------------------------------------------------------------------    

    
    /**
     * 
     * @param layout
     * @param sliderValue
     * @param colors
     * @param groupClusters
     */
     public void clusterAndRecolor(SubLayoutDecorator layout,
        int sliderValue,
        Color[] colors, boolean groupClusters) {
        //Now cluster the vertices by removing the top 50 edges with highest betweenness
        //      if (numEdgesToRemove == 0) {
        //          colorCluster( g.getVertices(), colors[0] );
        //      } else {
        
        Graph g = layout.getGraph();
        layout.removeAllSubLayouts();

        ClusterSet clusterSet;
        List edges;

        switch (clusterType.value ) {
            
            case 2:  // Edge Betweenness
            {
             int numEdgesToRemove = (int) (0.5+g.numEdges()*sliderValue/DMAXSLIDERVALUE); // needs max value to be fed into here
             EdgeBetweennessClusterer clusterer = new EdgeBetweennessClusterer(numEdgesToRemove);
             clusterSet = clusterer.extract(g);
             edges = clusterer.getEdgesRemoved();
             break;
            }
            
//            case 1: //test clusterer
//            {
//                int numEdgesToRemove= (int) (0.5+g.numEdges()*sliderValue/DMAXSLIDERVALUE); // needs max value to be fed into here
//             TestClusterer clusterer = new TestClusterer(numEdgesToRemove);
//        clusterSet = clusterer.extract(numEdgesToRemove); //
//        edges = clusterer.getEdgesRemoved();
//        break;}
            
            case 1: //percolation clusterer
            {
             EdgePercolationClusterer clusterer = new EdgePercolationClusterer(g, inet );
             //maxEdgeWeight=Double.parseDouble(maximumEdgeValueBox.getText());
             // set largest weight to correspond to 99 on the scale so 100 should remove it
             double edgeMinWeight = inet.edgeSet.getDisplayMaximum()*1.01010101*sliderValue/DMAXSLIDERVALUE; // needs max value to be fed into here
             clusterSet = clusterer.extract(edgeMinWeight); //
             edges = clusterer.getEdgesRemoved();
             break;
            }
            
//            case 3: //clustering by region
//                     edges = new ArrayList(); // no edges removed
//                     clusterSet = new VertexClusterSet(g);
//                     for (int s =0; s<inet.getNumberSites(); s++){
//                         inet.siteSet.getRegion(s);
//                     }
//
//                     clusterSet.addCluster(g.getVertices()); // all edges in cluster
//                     break;

            case 0: //No clustering
            default: edges = new ArrayList(); // no edges removed
                     clusterSet = new VertexClusterSet(g);
                     clusterSet.addCluster(g.getVertices()); // all edges in cluster



        }

        
        
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
                e.setUserDatum(ISLANDCLUSTERKEY, PERCREMOVEDCOLOUR, UserData.REMOVE);
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

    
    // **********************************************************************
    // Output files
    
 
    
   /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * Uses the global JUNG visualisation viewer vv.
     */
    public void writeJPEGImage() { writeJPEGImage(vv, vv.getPreferredSize());    }
    
    /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * @param vv a JUNG visualisation viewer with the graph.
     */
    private void writeJPEGImage(VisualizationViewer vv, Dimension size) {
        String filenamecomplete =  inet.outputFile.getFullLocationFileRootSN() +"_sl"+sliderValue+"_j"+(jpegCount++);
        inet.message.println(-1,"Attempting to write jpg file to "+ filenamecomplete);
        String label = inet.getNameString(", ");
        IslandNetworks.jungInterfaces.JpegNetwork.writeJPEGImage(filenamecomplete, label, vv, size.width, size.height);
        writeJPEGDescriptionFile(filenamecomplete);
    }
   /**
     * Copy the visible part of the JUNG graph to a file as an EPS image.
     * <p>Uses the global JUNG visualisation viewer vv.
     */
    public void writeEPSImage() { writeEPSImage(vv, vv.getPreferredSize());    }

    /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * @param vv a JUNG visualisation viewer with the graph.
     */
    private void writeEPSImage(VisualizationViewer vv, Dimension size) {
        String filenamecomplete =  inet.outputFile.getFullLocationFileRootSN() +"_sl"+sliderValue+"_j"+(jpegCount++);
        inet.message.println(-1,"Attempting to write eps file to "+ filenamecomplete);
        String label = inet.getNameString(", ");
        //String fileName, JungConverter jc,
        //    VisualizationViewer vv, boolean noPercolationRemovedEdges,
        //    boolean messagesOn
        boolean messageOn=true;
        IslandNetworks.jungInterfaces.EPSNetwork.FileOutputEPSNetwork(filenamecomplete, jc, vv, messageOn, messageOn) ;
        writeImageDescriptionFile(filenamecomplete, "eps");
    }

     /**
      * Description of display parameters used for jpeg image.
      * <p>Writes to the same file name as jpg but with <code>_jpgdescr.dat</code> extension
      * @param nameRoot name used for jpg file without the .jpg extension.
      */
    private void writeJPEGDescriptionFile(String nameRoot) {
        writeImageDescriptionFile(nameRoot, "jpg");
    }

     /**
      * Description of display parameters used for image.
      * <p>Writes to the same file name as image but with
      * <code>_</code><em>type</e,><code>descr.dat</code> extension
      * @param nameRoot name used for image file without the extension.
      * @param type type of image file
      */
    private void writeImageDescriptionFile(String nameRoot, String type) {
        String filenamecomplete =  nameRoot+"_"+type+"descr.dat";
        inet.message.println(-1,"Attempting to write description of "+type+" file to "+ filenamecomplete);
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            inet.printiNVERSION("#",PS);
            inet.showHamiltonianParameters(PS);
            printDisplayVariables(PS,islandNetwork.SEPSTRING);

            try
            {
               fout.close ();
               if (inet.message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.err.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }
    
        /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * @param vv a JUNG visualisation viewer with the graph.
     */
    private void writeGMLImage( VisualizationViewer vv) {
        String filenameroot =  inet.outputFile.getFullLocationFileRootSN() +"_sl"+sliderValue+"_g"+(gmlCount++);        
        GraphML.FileOutputGMLNetwork(filenameroot+".graphml", jc, vv, true, true); 
        writeGMLDescriptionFile(filenameroot);
    }
    
         /**
     * Description of display parameters used for jpeg image.
      * <p>Writes to the same file name as jpg but with <code>_jpgdescr.dat</code> extension
     * @param nameRoot name used for jpg file without the .jpg extension.
     */
    private void writeGMLDescriptionFile(String nameRoot) {
        String filenamecomplete =  nameRoot+"_gmldescr.dat";        
        inet.message.println(-1,"Attempting to write description of graphMLfile to "+ filenamecomplete);
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            inet.printiNVERSION("#",PS);
            inet.showHamiltonianParameters(PS);
            printDisplayVariables(PS,islandNetwork.SEPSTRING);
        
            try
            { 
               fout.close ();
               if (inet.message.getInformationLevel()>-1) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.err.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }

         
    /**
     * Prints display variables to a print stream.
     * @param PS printstream for output
     * @param sep separation character
     */
    private void printDisplayVariables(PrintStream PS, String sep) {
        PS.println("         Slider value "+sep+sliderValue);
        PS.println("            Clusterer "+sep+clusterType.getCurrentTypeString());
        PS.println("               Layout "+sep+layoutType.getCurrentTypeString());
        PS.println("     Vertex Displayed "+sep+inet.DisplayVertexType.getCurrentTypeString());
        PS.println(" defaultEdgeFractiont "+sep+defaultEdgeFraction);
        PS.println("        maxEdgeWeight "+sep+inet.edgeSet.displayMinimumEdgeWeight);
        PS.println("        minEdgeWeight "+sep+inet.edgeSet.displayMinimumEdgeWeight);
        PS.println("   maxEdgeDisplaySize "+sep+maxEdgeDisplaySize);
        PS.println("DisplayMaxVertexScale "+sep+inet.DisplayMaxVertexScale);
        PS.println(" maxVertexDisplaySize "+sep+maxVertexDisplaySize); 
        
    }
   
    /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * <br>Doesn't work.  Maybe need to use setDoubleBuffered image options in JUNG visulaisation viewers?
     * @param vv a JUNG visualisation viewer with the graph.
     * @deprecated 
     */
    private void writeJPEGImageOld(VisualizationViewer vv) {
        String filenamecomplete =  inet.outputFile.getFullLocationFileRoot() + "sl"+sliderValue+"_"+(jpegCount++)+".jpg";        
        System.out.println("Attempting to write jpg file to "+ filenamecomplete);
        
        File file = new File(filenamecomplete);
        
        int width = vv.getWidth();
        int height = vv.getHeight();

        
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();
        vv.paint(graphics);
        graphics.setColor(Color.black);
        graphics.drawString(inet.Hamiltonian.inputParametersString(inet.outputFile.getBasicRoot(),", ",3), 2, height-2);		
        graphics.dispose();
        
        try {
            ImageIO.write(bi, "jpeg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    
    }
    
// ****************************************************************
// Graph visualisation routines and classes
  
   
// ---------------------------------------------------------------
 /**
  * Controls different Layout types.
  * @todo Include influence option.
  */
private class LayoutType{

    
 final String[] layoutTypeArray =  {"Geographical", "Circular", "KK Geographical", "KK Edge Value", "KK Edge Separation",  "FR", }; 
 public final int numberTypes = layoutTypeArray.length;
 public int value=0;
 public double scale =1.0; // scales graph to screen coordinates

 /*
  * @param s scale used to transform graph coordinates to screen coordinatinet.edgeSet.
  */
public LayoutType(double scale){
 value=0;   
 this.scale=scale;
}

            /* Sets numerical code for cluster types from string , -1 if not known.
     *@return numerical code used internally to represent the layout type
     */
    public void setlayoutType(String s)
    {
        for (int i=0; i<layoutTypeArray.length; i++)
            if (s.substring(0,1).equals(layoutTypeArray[i].substring(0,1))) {value=i; return;}
        value=-1;
        return;
    }



   /* Returns string for layout typinet.edgeSet.
     *@param i the cluster mode number.
     *@return short string describing the cluster type
     */
     public String getString(int i){
     if ((i<0) || (i>=layoutTypeArray.length) ) return "UNKNOWN";
     return layoutTypeArray[i];
    }

     /* Returns string for current layout type.
     *@return short string describing the current layout type
     */
     public String getCurrentTypeString(){
     return getString(value);
    }

     private SubLayoutDecorator getLayoutType(Graph graph)    
    {
        SubLayoutDecorator layout;
        double displayLengthFactor=0.5;
        switch (value) {
     
             case 5: { layout = new SubLayoutDecorator(new FRLayout(graph)); break;}
             case 4: { 
                 int ns = inet.edgeSet.getNumberSites();
                 double distanceScale = inet.edgeSet.getSeparationDiameter(); //inet.Hamiltonian.distanceScale ; 
                 double [][] distanceArray = new double[ns][ns];
                 double dMax =   (1.0+(double) sliderValue)/(1.0+MAXSLIDERVALUE); 
                 for (int i=0; i<ns; i++){
                 for (int j=0; j< ns; j++){
                    double d=(inet.edgeSet.getEdgeSeparation(i,j)+inet.edgeSet.getEdgeSeparation(j,i))/(2.0*distanceScale);
                    if(d>dMax) distanceArray[i][j]= 1.0;
                    else distanceArray[i][j]=d;
                }
                 }
                 layout = new SubLayoutDecorator(new KKWeightedLayout(graph,distanceArray,displayLengthFactor )); 
                 break;
             }
             case 3: { 
                 int ns = inet.edgeSet.getNumberSites();
                 double evMin = (1.0+(double) sliderValue)/(1.0+MAXSLIDERVALUE); 
                 //double diameter = 2.0/evMin;
                 double [][] distanceArray = new double[ns][ns];
                 for (int i=0; i<ns; i++){
                 for (int j=0; j< ns; j++){
                    double ev = (inet.edgeSet.getEdgeValue(i,j)+inet.edgeSet.getEdgeValue(j,i))/2.0;
                    if (ev<evMin) distanceArray[i][j]= 1.0; 
                    else distanceArray[i][j]=evMin/ev;
                }
                 }
                 layout = new SubLayoutDecorator(new KKWeightedLayout(graph,distanceArray,displayLengthFactor ) ); 
                 break;
            }
             case 2: { 
                 int ns = inet.edgeSet.getNumberSites();
                 double distanceScale = inet.edgeSet.getDistanceDiameter(); //inet.Hamiltonian.distanceScale ; 
                 double [][] distanceArray = new double[ns][ns];
                 double dMax =   (1.0+(double) sliderValue)/(1.0+MAXSLIDERVALUE); 
                 double dMin =   1.0/(1.0+MAXSLIDERVALUE); 
                 for (int i=0; i<ns; i++){
                  for (int j=0; j< ns; j++){
                    double d=(inet.edgeSet.getEdgeDistance(i,j)+inet.edgeSet.getEdgeDistance(j,i))/(2.0*distanceScale);
                    if(d>dMax) distanceArray[i][j]= 1.0;
                    else {if (d<dMin) distanceArray[i][j]=dMin;
                          else distanceArray[i][j]=d;
                    }
                    if (d<=0) System.err.println("Distance from "+i+" to "+j+" is negative or zero");
                  }
                 }
                 layout = new SubLayoutDecorator(new KKWeightedLayout(graph,distanceArray,displayLengthFactor )); 
                 break;
             }
             case 1: { layout = new SubLayoutDecorator(new CircleLayout(graph)); break;}
             case 0: 
             default: { //Scaling assumes site coordinates laid out in range 0..1.
                 // The next line does not work as the layout does not have any size as yet
                 //layout = new SubLayoutDecorator(new GeographicalLayout(graph, jc.getXCoordKey(), jc.getYCoordKey() )); break;}
                 layout = new SubLayoutDecorator(new GeographicalLayout(graph, jc.getXCoordKey(), jc.getYCoordKey() , scale*0.8, scale*0.1)); break;}
             //{layout = new SubLayoutDecorator(new GeographicalLayout(graph)); break;};
         }
       return layout;
    }

    
}//eo layoutType class

// ---------------------------------------------------------------
 
private class ClusterType{

    
 final String[] clusterTypeArray =  {"Nothing", "Percolation", "Edge Betweenness"}; 
 public final int numberTypes = clusterTypeArray.length;
 public int value=1;

public ClusterType(){
 value=0;   
}

    /* Sets numerical code for cluster types from string , -1 if not known.
     *@return numerical code used internally to represent the layout type
     */
    public void setclusterType(String s)
    {
        for (int i=0; i<clusterTypeArray.length; i++)
            if (clusterTypeArray[i].startsWith(s.substring(0,1))) {value=i; return;}
        value=-1;
        return;
    }


            
   /* Returns string for cluster typinet.edgeSet.
     *@param i the cluster mode number.
     *@return short string describing the cluster type
     */
     public String getString(int i){
     if ((i<0) || (i>=clusterTypeArray.length) ) return "UNKNOWN";
     return clusterTypeArray[i];
    }

    /* Returns string for current cluster type.
     *@return short string describing the current cluster type
     */
     public String getCurrentTypeString(){
     return getString(value);
    }
     /**
      * Check to see if need variable clustering parameter.
      * @return true if clustering has no variable clustering parameter
      */
     public boolean noVariableClustering(){return (value==0?true:false);}

}//eo clusterType class






}// eo clustering window
