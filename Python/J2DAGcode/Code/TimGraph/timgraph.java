package TimGraph;
/*
 * timgraph.java
 *
 * Created Wednesday, July 6, 2005 at 15:36
 * Supercedes timwalk.java
 *
 */


//import edu.uci.ics.jung.graph.impl.*;
//import edu.uci.ics.jung.graph.*;
//import edu.uci.ics.jung.utils.*;
//import edu.uci.ics.jung.statistics.*;
//import edu.uci.ics.jung.statistics.GraphStatistics;
//import edu.uci.ics.jung.algorithms.cluster.*;
//import edu.uci.ics.jung.io.*;

import TimGraph.io.FileInput;
import TimGraph.io.FileOutput;
import TimGraph.io.GraphViz;
import TimGraph.io.InputFileType;
import TimGraph.Community.Community;
import TimGraph.Community.VertexPartition;
import TimUtilities.TimTime;
import TimUtilities.TimTiming;
import TimUtilities.NumbersToString;
import TimUtilities.TimMessage;
import TimUtilities.FileUtilities.FileNameSequence;
import TimUtilities.StringUtilities.Filters.StringFilter;

import cern.colt.list.IntArrayList;
import cern.colt.list.DoubleArrayList;

import java.util.Date;
import java.util.Random; //p524 Schildt

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
//import java.lang.IllegalArgumentException;

import JavaNotes.TextReader;
import TimGraph.Community.Partition;
import TimGraph.algorithms.LineGraphProjector;
import TimGraph.algorithms.StructuralHoleAnalysis;
import TimGraph.algorithms.StructuralHoleVertexData;
import java.util.ArrayList;


/**
 * <tt>timgraph</tt> defines a graph and appropriate methods.
 * <p>The graphs are stored in two main structures.
 * <br>There is an array, <code>vertexList</code>, where each entry 
 * <code>vertexList[v]</code> is a list of the index of neighbouring vertices stored as a 
 * CERN colt library IntArrayList.  The length of the vector may be less than 
 * <tt>NumberVertices</tt> which is the current number of vertices.  
 * These always have indices (<tt>v</tt>) running from <tt>0</tt> to <tt>(TotalNumberVertices-1)</tt>.
 * <br>Then there is an array
 * <code>stubSourceList</code> where <code>stubSourceList[2e]</code> is
 * index of the source vertex of the e-th edge while <code>stubSourceList[2e+1]</code>
 * is the target vertex.  The existing edges have indices running from <tt>0</tt> to 
 * <tt>(TotalNumberStubs-1)</tt> so that <tt>TotalNumberStubs</tt>. is the
 * number of stubs in the graph.  This may be less than the dimension of the 
 * <code>stubSourceList</code> array.
 * Multiple edges between same source and vertex are
 * allowed and are listed separately. 
 * If <code>vertexEdgeListOn</code> is true then we also keep a list of global 
 * edge indices for those edges attached to each vertex.
 * <code>vertexEdgeList[v]</code> is a list of edge indices of those edges
 * attached to neighbours. If its a directed graph then this is only outgoing edges but then 
 * <code>vertexInEdgeList[v]</code> will also be defined with the incoming edge indices.
 * Note that these are storing the global stub indices used in <code>stubSourceList</code>, but
 * the list <code>vertexEdgeList[v]</code> is associating a local index to each stub too.
 * These <code>vertexEdgeList[v]</code> are basically the <b>incident matrix</b> for the graph.
 * <br><code>boolean directedGraph</code> indicates directed edges used.
 * <br><code>boolean vertexlabels</code> indicates vertices are labelled.
 * <br><code>boolean weightedEdges</code> indicates that edges carry EdgeValue class objects which includes a double value.
 * <br><code>boolean labelledEdges</code> indicates edges are labelled by integers.
 * <br><code>boolean multiEdge</code> indicates multiple edges between vertex pairs is allowed.
 * Turning this off is not implemented fully.
 * <br><code>boolean selfLoops</code> indicates if self-loops are allowed (not fully implemented).
 * <br><code>boolean bipartiteGraph</code> indicates that vertices come in two types.
 * Vertices of type one have indices <tt>0</tt> to <tt>(numberVertexType1-1)</tt>
 * while type two vertices run from <tt>numberVertexType1</tt> to 
 * <tt>(numberVertexType1+numberVerticesType2-1)</tt> which ought to equal <tt>TotalNumberVertices</tt>.
 * <br>Vertex labels are listed in <code>vertexLabelList[v]</code> each being a VertexLabel class.
 * <br>EdgeValues, which store weights, lanbels and other edge features,
 * are listed as <code>edgeValuetList[2e]</code>, each an EdgeValue type for the
 * <tt>(2e)</tt> to <tt>(2e+1)</tt> edge while <code>edgeValuetList[2e+1]</code> is always
 * <em>exactly the same EdgeValue object</em> even for directed graphs.  Thus updating either
 * <code>edgeValuetList[2e]</code>  or  <code>edgeValuetList[2e+1]</code> will work, so don't do this twice!
 * <br><code>vertexSourceList[v]</code> is a list of source vertices for edges connected to vertex v.
 * <br>The main routine is dorun which adds edges and vertices up to the maximum numbers.
 * @author  Tim Evans
 */
public class timgraph {

    /**
     * Version
     * <p>{@value }
     */
     static final String VERSION = "tg110504";
    /**
     * Maximum integer allowed.
     */static final int MININT = 2147483647; // In fact 2147483647
    /**
     * Minimum integer allowed.
     */static final int MAXINT = -2147483648; // In fact -2147483648
            
    /**
     * Debugging options on
     */boolean DEBUG = false;
    
    /**
     * Separation String used for output.
     */
     final static public String SEP = "\t "; 
     
     
     /**
      * String used to set default name.
      */
     final static public String NONAME ="timgraph";
    
    /**
     * Comment string used for output.
     */
    public static final String COMMENTCHARACTER = "#";   
    
    /**
     * The character used to indicate the start of an argument used in this class.
     * <p>{@value }
     */
    public static final char TIMGRAPH_ARGUMENT='-';
    /**
     * List of characters used to indicate arguments passed to other classes.
     * <p>Values are '*','^',':'
     */
    public final static char [] NOT_TIMGRAPH_ARGUMENT = {'*','^',':'};
        
    static NumbersToString n2s = new NumbersToString();
    
    /**
     * maximum edge target = maximum integer
     */
    static final int MAXEDGETARGET = 2147483647; // = (2^31)-1 fixed JAVA largest +ve int value.
    /**
     * maximum edge source = maximum integer minus one.
     */
    static final int MAXEDGESOURCE = 2147483646; // = (2^31)-2 fixed JAVA largest +ve int value minus 1.
    
    /**
     * Used to indicate that an int has not been set.
     * <br>Check with {@link #isUnset(int) }.
     */
    public static final int IUNSET = -135798642;
    
    /**
     * Used to indicate that a double has not been set.
     * <p>Also used to indicate not to test weights in printEdges.
     * <br>Check with {@link #isUnset(double) }.
     */
    public static final double DUNSET = -357986421;
    /**
     * Used to indicate that a string has not been set.
     * <br>Check with {@link #isUnset(String) }.
     */
    public static final String SUNSET = "UNSET";
    
    Date date = new Date();
    TimTiming timing = new TimTiming();
    public RandomWalk randomWalk; // generic random walk to use on this graph
    
//    UndirectedGraph g = new UndirectedSparseGraph();
    public Random Rnd; //Schildt p524
    
    /**
     * Total number of actual vertices.
     */
    private int TotalNumberVertices = 0;
    /**
     * Total number of actual stubs.
     */
    private int TotalNumberStubs = 0;
    /**
     * Total weight, sum of all edge weights.
     */
    private double TotalWeight =DUNSET;

    /**
     * Second Moment of degree
     */
    private double degreeSecondMoment =DUNSET;

    /**
     * Total number of triangles in graph.
     * @see TimGraph.timgraph#calcTrianglesTotal()
     * @see TimGraph.timgraph#calcTrianglesSquaresTotal()
     */
    private int TotalNumberTriangles=IUNSET;

    /**
     * Total number of squares in graph.
     * @see TimGraph.timgraph#calcTrianglesSquaresTotal()
     */
    private int TotalNumberSquares=IUNSET;

    /**
     * Global Clustering Coefficient.
     * <p>Not the average of the individual vertex c.c.
     * but three times the number of triangles divided by the number of incident
     * edge pairs
     */
    private double ccGlobal=DUNSET;

    /**
     * List of target vertices.  
     * <p>For undirected graph this is all neighbours
     * but for directed graphs this is only the list of target vertices on outgoing edges.
     */
    IntArrayList[] vertexList; // 
    
    /**
     * <code>vertexSourceList[t]</code> is a list of source vertices
     * for edges arriving at target vertex t so only defined for directed graph.
     */
    IntArrayList[] vertexSourceList; 

    /**
     * Switches use of <code>vertexEdgeList[v]</code>  and <code>vertexInEdgeList[v]</code> on or off.
     */
    private boolean vertexEdgeListOn=false;
     /**
     * List of edges by global edge index connected to a vertex.  
     * <br>For undirected graph this is all edges 
     * but for directed graphs this is only the list of outgoing edges.
     */
    IntArrayList[] vertexEdgeList; // 

     /**
     * List of incoming edges by global edge index incoming to vertex.  
     * <BR>Only defined for directed graphs.
     */
    IntArrayList[] vertexInEdgeList; // 

    
    VertexLabel [] vertexLabelList; // Vertex labels for each vertex
    IntArrayList[] strengthlist; // ???? Remove this?
    IntArrayList[] rvertexList;
    /**
     * List of vertices associated with each stub.
     * <br>For undirected graphs, vertices <code>stubSourceList[2n]</code>
     * and <code>stubSourceList[2n+1]</code>
     * are connected by an edge.
     * For directed graphs <code>stubSourceList[2n]</code>is the source vertex of an edge
     * and <code>stubSourceList[2n+1]</code> is the corresponding target vertex.
     * <br>Needs to be public for CopyModel
     */
    int[] stubSourceList ; //
                    
    /**
     * <code>edgeValuetList[e]</code> is the EdgeValue class entry for edge e.
     * <br>Only defined for weighted graphs.
     * <br>Weight of undirected edge between vertices <code>stubSourceList[2n]</code>
     * and <code>stubSourceList[2n+1]</code> is  <code>edgeValuetList[2n]</code>
     */
    EdgeValue[] edgeValuetList ; // values of undirected edge above is  ewl[2n]
    int[] edgerandomlist ; // erl[n] = edge number = 2m,  m,n<TotalNumberStubs/2
    boolean directedGraph=false; // directed edges in graphs;
    boolean vertexlabels=false; // labelled graphs;
    boolean weightedEdges=false; // Weighted graphs;
    boolean labelledEdges=false; // labelled edges in graph
    /**
     * Self loops allowed.
     * <p>This may not be fully implemented yet.
     * <p>Default value is true
     */
    boolean selfLoops=true; // self loops allowed
    /**
     * Multiple edges between vertices allowed.
     * <p>This may not be fully implemented yet.
     * <p>Default value is true
     */
    boolean multiEdge=true; // multiEdges edges in graph
    private boolean bipartiteGraph = false; // bipartite graph;
    /**
     * Number of vertices of type one in bipartite graph (negative or zero means not bipartite)
     * <p>Vertices index 0 to <tt>(numberVertexType1-1)</tt> are type one vertices
     */
    private int numberVertexType1 =-1;
    /**
     * Name of vertices of type one in bipartite graph.
     * <p>Used in filenames so don't use illegal characters.
     */
    private String nameVertexType1 ="Type1";
    /**
     * Number of vertices of type two in bipartite graph (negative or zero means not bipartite)
     * <p>Vertices index <tt>numberVertexType1</tt> to <tt>(numberVertexType1+numberVerticesType2-1)</tt> 
     * are type two vertices
     */
    private int numberVertexType2 =-1; // bipartite graph vertex numbers, sum must be TNV
    /**
     * Name of vertices of type two in bipartite graph.
     * <p>Used in filenames so don't use illegal characters.
     */
    private String nameVertexType2 ="Type2";
    
    // for vertex positions in labels
    //public Coordinate maximumCoordinate;
    //public Coordinate minimumCoordinate;
    //Rank maxRank; // use to record maximum rank
    /**
     * Stores minimum values of all vertexLabels.
     */
    protected VertexLabel minimumVertexLabel = null;
    /**
     * Stores maximum values of all vertexLabels.
     */
    protected VertexLabel maximumVertexLabel = null;

    /**
     * Stores maximum values of all EdgeWeights.
     */
    protected EdgeValue minimumEdgeValue = null;
    /**
     * Stores maximum values of all EdgeWeights.
     */
    protected EdgeValue maximumEdgeValue = null;


    /**
     * Degree distribution for all vertices
     */
    DegreeDistribution DDtotal; // total degree distribution
    /**
     * In degree distribution for directed graphs
     */
    DegreeDistribution DDin;    // in degree distribution
    /**
     * Out degree distribution for directed graphs
     */
    DegreeDistribution DDout;   // out degree distribution
    /**
     * Total degree distribution for type 1 vertices in bipartite graphs
     */
    DegreeDistribution DD1total; // total degree distribution for type 1 vertices in bipartite
    /**
     * Total degree distribution for type 2 vertices in bipartite graphs
     */
    DegreeDistribution DD2total; // total degree distribution
    
    /**
     * Number of vertices needed if log binned DD given along with DD
     */
    int LogBinVerticesMinimum=99; 
    
    IntArrayList weightdarr = new IntArrayList() ; // weight distribution
    IntArrayList rddarr = new IntArrayList(); // reduced vertex degree distribution
    int maxmoment = 10;
    //StatQuant[] dmomentarr = new StatQuant[10]; // moments of degrees
    int weightmaximum = -1 ;
    // records longest walk made by doRun routine
    int maxonewalk=-2;
// Parameters for one distance sample fromone source    
    int[] vertexdistance; // distance from source vertex of vertices <-1 if not connected
    IntArrayList distancedist;  // number of vertices of distance .get(d)
    DoubleArrayList ringdegree;  // average degree of vertices at distance .get(d)
    int[] vertexComponent; // Component containing vertex v, =-1 if not known
    IntArrayList componentSize;  // number of vertices in component
    IntArrayList componentEdges;  // number of edges in component
    IntArrayList componentmu2;  // mu2 for component
    IntArrayList componentSource;  // A source vertex for component
    IntArrayList componentDist;  // A measure of  component size, max distance from source
    int componentsize;  // number of vertices in component
    int componentSizeMax; // number of vertices in GCC
    int componentSourceMax; // a vertex in the GCC
    int componentDiameterMax; // estimated diameter of GCC
    int componentGCCIndex; // index in arrays of GCC component 
    double componentGCCDist; // average distance estimate in GCC component 
    int componentSingleNumber; // number of components of just one vertex
    int diameter; // maximum distance found 
    double distanceaverage = 0.0;
    double distanceerror = 0.0;
    double distancesigma = 0.0;
// Parameters for several distance samples, nsamples sources    
    int nsamples;
    DoubleArrayList avdistancedist;  // av distance for sample ns .get(ns)
    double onesdistanceav = 0.0;  // average and error over samples 
    double onesdistanceerror = 0.0; //      of average distance per one sample
    double onesdistancesigma = 0.0; //      of average distance per one sample
    DoubleArrayList totdistancedist;  // number of vertices of distance .get(d)
    DoubleArrayList totdistance2dist; // number of vertices of distance .get(d)
    double totdistanceaverage =0.0;  //average from the total av
    double totdistanceerror =0.0;
    double totdistancesigma =0.0;
    IntArrayList diameterdist;  //  diameter for sample ns .get(ns)
    int diametermax; // maximum distance found 
    int diametermin; // minimum distance found 
    double diameteraverage; // averagedistance found 
    double diametererror; // absolute error distance found 
    double diametersigma; // sigma distance found 
    
    // Parameters for Dijkstra
    double DijkstraMaxDist;
    double MAXDISTANCE=1e99;
    double [][] distance;
    
// Parameters for CC samples, CCnsamples sources    
    int CCnsamples;
    double CCaverage;
    double CCerror;
    double CCsigma;
    int CCEdgensamples;
    double CCEdgeaverage;
    double CCEdgeerror;
    double CCEdgesigma;

    double averagesteplength;
    public FileNameSequence inputName;
//    String inputNameRoot; // set in constructors 
//    String inputDirName; // set in constructors
//    String inputExtension; // set in constructors
    /**
     * List of possible extensions for input files, used to select input file type
     */
    public InputFileType inputFileType = new InputFileType(0);
//    private final static String [] extensionList =        
//    {"input.net","inputEL.dat",      "inputELS.dat",
//     "input.gml","inputAdjMat.dat",  "inputVNLS.dat"};
//    private final static String [] extensionDescription = 
//    {"pajek",    "integer edge list","string edge list", 
//     "gml file", "adjacency matrix", "string vertex neighbour list "};
//    public final static GeneralMode inputFileType = new GeneralMode(extensionList,extensionDescription);
//    public final static String [] lgExtensionList  = {"LG", "LGsl", "WLG", "WLGsl"};
//
//    public final static String [] lgExtensionDescription  = {"Line Graph L(G)", "Line Graph with self-loops", "Weighted Line Graph", "Weighted Line Graph with self-loops"};
    
    /**
     * Colours for use with pajek postscript output.
     */
    public final static String [] pajekColour = {
            "White", // always have this as zero
            "Yellow", "Pink", "Cyan", "Orange",
            "Magenta", "Purple", "Green", "Blue", "Brown", 
            "Black"}; // Black always last

    public FileNameSequence outputName;
    
    // next three are used to set array sizes, rest are variable
    /**
     * Absolute maximum number of vertices.
     * <p>Used to set array sizes so can not be exceeded.
     */
    private int maximumVertices =1000;
    /**
     * Absolute maximum number of stubs (twice the maximum number of edges).
     * <p>USed to set array sizes so can not be exceeded.
     */
    private int maximumStubs=1000;
    int maximumconnectivity=1000;
    double connectivity =2;  // should be average degree over two.
    double maxexpectededges = maximumVertices*connectivity*2;
    double maxexpectedvertices = maximumVertices;
    // allow for fluctuations in number of edges
            
    int numevents = 0;
    double averageWalkLength=1.0; // used in random walks, 
    int maxWalkLength  = ((int) (averageWalkLength + 0.5))*3;
    double rankingProbabilityLengthScale=-1.0; // used for ranking
    public int binomialNumber = 1; // used for binomial distributions
    int numruns=1;
    double probnewvertex= 1.0;
    double probpp=1.0;
    double probpr=0.0;
    double probpra=0.0;
    double probqp=1.0;
//    double probqr=0.0;
    double probqra=0.0;
//    double probrndvertex =0.0; // prob of choosing a random vertex
    int initialgraph = 1;
    int initialConnectivity = -1;
    int initialVertices = -1;
    int initialXsize = -1;
    int initialYsize = -1;
    int initialEdges = -1;
    
    TimMessage message = new TimMessage(0);
    public int infoLevel; // set in constructors
    public OutputMode outputControl; // set in constructors
    boolean WeightCalcOn=true; //false;
    double logbinratio =1.1;
    boolean SourceVertex = true;  // used to select existing vertex type as source for new edges
    int randomWalkMode = 1; // v input option
    int edgegenerator=0; // choose edge generator
    
    

    /** Constructor - random seed for random numbers.
     *
     */
    public timgraph() 
    {
      initialiseRandomGenerator();
      // uses current directory
      initialiseSomeParameters( "test",System.getProperty("user.dir"),  0, 0);    
    }

    /** 
     * Constructor - random seed for random numbers.
     *<p>Passes the arguments
     */
    public timgraph(String [] args) 
    {
      initialiseRandomGenerator();
      initialiseSomeParameters( "test",System.getProperty("user.dir"),  0, 0);    
      parseParam(args);
    }


    /** Constructor - new rnd sequence based on given seed.
     *@param rndseed seed for random numbers
     */
    public timgraph(long rndseed) 
    {
      Rnd = new Random(rndseed); //Schildt p524, can fix rnd sequence
      initialiseSomeParameters( "test",System.getProperty("user.dir"),  0, 0);
      
    }


    /*
     * Constructor to set up basic parameters of choice.
     *@param namert root of names used for files e.g. test
     *@param dnameroot root directory name, full path with slash at end e.g. /data/
     *@param infol information level sets information given, >0 for increasing debugging info, <0 for increasing quietness
     *@param outputc controls output levels
     */
  public timgraph(String namert, String dnameroot, int infol, int outputc)
  {
      initialiseRandomGenerator();
      initialiseSomeParameters(namert, dnameroot, infol, outputc);
      if (infoLevel>2) System.out.println("Uses time to seed Rnd");
  }

    /**
     * Constructor to set up basic parameters of choice.
     *@param namert root of names used for files e.g. test
     *@param dnameroot root directory name full path with slash at end e.g. /data/ (input and output added to this directory root name)
     *@param infol information level sets information given, >0 for increasing debugging info, <0 for increasing quietness
     *@param outputc controls output levels
     *@param makeDirected true (false) if want (un)directed graph
     *@param makeLabelled true (false) if want (un)labelled graph
     *@param makeWeighted true (false) if want (un)weighted graph
     *@param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     *@param maxVertices maximum number of vertices
     *@param maxEdges maximum number of edges
     */
  public timgraph(String namert, String dnameroot, 
          int infol, int outputc, 
          boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList,
          int maxVertices, int maxEdges)
  {
      initialiseRandomGenerator();
      initialiseSomeParameters(namert, dnameroot, infol, outputc);
      if (infoLevel>0) System.out.println("Uses time to seed Rnd");
      // set globals
      maximumVertices=maxVertices; 
      maximumStubs=maxEdges;
      directedGraph = makeDirected;
      vertexlabels = makeLabelled;
      weightedEdges = makeWeighted;
      vertexEdgeListOn = makeVertexEdgeList;
      setNetwork(); // creat blank network
  }

    /**
     * Constructor to use for projections where a stub list defines a graph.
     * <br>NOTE: this constructor will shuffle the stublist.
     *@param namert root of names used for files e.g. test
     *@param dname directory name full path with slash at end e.g. /data/
     *@param infol information level sets information given, >0 for increasing debugging info, <0 for increasing quietness
     *@param outputc controls output levels
     *@param makeDirected true (false) if want (un)directed graph
     *@param makeLabelled true (false) if want (un)labelled graph (IGNORED - always unlabelled).
     *@param makeWeighted true (false) if want (un)weighted graph
     *@param maxVertices maximum number of vertices
     *@param stubList list of integers between 0 and (maxVertices-1) giving stubs of edges (ignores last if odd)
     */
  public timgraph(String namert, String dname, 
          int infol, int outputc, 
          boolean makeDirected, boolean makeLabelled, boolean makeWeighted, 
          int maxVertices, int [] stubList)
  {
      initialiseRandomGenerator();
      // set globals
      directedGraph = makeDirected;
      vertexlabels = makeLabelled;
      weightedEdges = makeWeighted;
      maximumVertices=maxVertices; 
      maximumStubs=(stubList.length >> 1) << 1; // make even number
      directedGraph = makeDirected;
      vertexlabels = false; // makeLabelled;
      weightedEdges = makeWeighted;
      boolean shuffleOn = true;
      setNetwork(maximumVertices, stubList, shuffleOn)  ;
  }
  
  
   /**
     * Makes deep copy with unlabelled or undirected options.
    * <p>Handles vertexEdgeList and bipartite graphs.
     *@param oldtg existing timgraph to be deep copied
     *@param maxVertices maximum number of vertices (will use actual number if thats
     *@param maxStubs maximum number of stubs
     *@param makeUndirected true if want to force undirected new copy
     *@param makeUnlabelled true if want to force unlabelled new copy
     *@param reverseDirection true if want to reverse direction of edges
     */
    public timgraph(timgraph oldtg, int maxVertices, int maxStubs,
            boolean makeUndirected, boolean makeUnlabelled, boolean reverseDirection) 
    {
        this.initialiseRandomGenerator();
        infoLevel=oldtg.infoLevel;
        if (infoLevel>0) System.out.println("Uses time to seed Rnd");
        int result =0;
        vertexlabels=oldtg.vertexlabels;
        if (makeUnlabelled) vertexlabels=false;
        directedGraph=oldtg.directedGraph;
        if (makeUndirected) directedGraph=false;
        inputName = new FileNameSequence(oldtg.inputName);
        outputName = new FileNameSequence(oldtg.outputName);
        int TNV=oldtg.TotalNumberVertices;
        int TNS=oldtg.TotalNumberStubs ;
        setNetwork(Math.max(TNV,maxVertices), Math.max(TNS,maxStubs));
        outputControl = new OutputMode(oldtg.outputControl);
        
        // copy vertices
        vertexList = new IntArrayList [TNV];
        if (vertexEdgeListOn) {
            vertexEdgeList = new IntArrayList[TNV];
            if (directedGraph) vertexInEdgeList = new IntArrayList[TNV];
        }
        if (directedGraph) vertexSourceList = new IntArrayList [TNV];
        if (vertexlabels) 
        {
            vertexLabelList = new VertexLabel[TNV];
            if (oldtg.minimumVertexLabel!=null) minimumVertexLabel = new VertexLabel(minimumVertexLabel);
            if (oldtg.maximumVertexLabel!=null) maximumVertexLabel = new VertexLabel(maximumVertexLabel);
        }
        TotalNumberVertices=0;
        if (vertexlabels) for (int v=0; v<TNV; v++) addVertex( oldtg.vertexLabelList[v] ) ;
        else for (int v=0; v<TNV; v++) addVertex( ) ;
        
        // copy edges
        stubSourceList = new int[TNS];
        TotalNumberStubs=0;
        int v1=-1;
        int v2=-1;
        EdgeValue w1 = new EdgeValue();
        EdgeValue w2 = new EdgeValue();
        if (weightedEdges) 
        {   // weighted edges
            for (int e=0; e<oldtg.TotalNumberStubs;) 
            {
                v1 = oldtg.stubSourceList[e];
                w1 = oldtg.edgeValuetList[e++];
                v2 = oldtg.stubSourceList[e];
                w2 = oldtg.edgeValuetList[e++]; // this should be identical to w1
                if (reverseDirection) addEdge(v2,v1, w1);
                else addEdge(v1,v2, w1);
            }           
        } 
        else 
        {   // not weighted edges
            for (int e=0; e<oldtg.TotalNumberStubs;) 
            {
                v1 = oldtg.stubSourceList[e++];
                v2 = oldtg.stubSourceList[e++];
                if (reverseDirection) addEdge(v2,v1);
                else addEdge(v1,v2);
            }            
        }
                             
        if (TNS!=TotalNumberStubs)
        {
            result=1;
            System.out.println("*** Total Number Edges wrong in timgraph constructor");
        }

        if (bipartiteGraph){
            setBipartite(oldtg.getNumberVerticesType1(), oldtg.getNumberVerticesType2(),
                         oldtg.getNameVerticesType1(),   oldtg.getNameVerticesType2());
        }

        if (oldtg.vertexEdgeListOn){
            createVertexGlobalEdgeList();
        }
    }
    
    /**
     * Makes projection of input graph onto partition.
     * <br>Each vertex of input graph is mapped to a vertex corresponding to its 
     * partition label. These labels must be an integer between 0 and  <code>numberPartitions</code>.
     * If a label is not present in <code>partition[]</code> a diconnected vertex will still be created.
     * Edges are mapped following the way their source and target vertices are mapped.
     * Multiple edges are merged. The weights of new edges being the sum of  weights of old edges.
     * <code>vertexEdgeListOn</code> is set for this projection to work.
     * <p>If the old graph is undirected then selfloops in the new graph coming 
     * from none self-loops in the old graph have their weights doubled.  
     * Self-loops in an undirected original graph add only their own weight (not 2x) to the new graph.
     * @param oldtg existing timegraph to be deep copied
     * @param partition vector with vertex partition labels so <code>partition[v]</code> is partition of vertex <tt>v</tt>
     * @param numberPartitions <code>partition[v]</code> must be an integer between 0 and (numberPartitions-1) inclusive.
     * @param makeUndirected true if want to force undirected new copy
     * @deprecated REWRITE THIS AS A STATIC member of CLASS projection in the algorithms section.
     */
    public timgraph(timgraph oldtg, int [] partition, int numberPartitions, boolean makeUndirected, boolean makeUnweighted) 
    {
        infoLevel=oldtg.infoLevel;
        vertexEdgeListOn=true;
        initialiseRandomGenerator();
        directedGraph=oldtg.directedGraph;
        if (makeUndirected) directedGraph=false;
        weightedEdges=(!makeUnweighted);
        vertexlabels=false;
        
//        inputNameRoot=oldtg.inputNameRoot;
//        inputDirName=oldtg.inputDirName;
//        inputExtension=oldtg.inputExtension;
//        outputDirName=oldtg.outputDirName;
//        outputNameRoot=oldtg.outputNameRoot;
//        outputExtension=oldtg.outputExtension;
        inputName = new FileNameSequence(oldtg.inputName);
        outputName = new FileNameSequence(oldtg.outputName);
        
        maximumVertices = numberPartitions;
        // Remember edges are really stubs
        if (directedGraph) maximumStubs = maximumVertices *maximumVertices*2 ;   
        else maximumStubs = maximumVertices *(maximumVertices +1) ;   
        setNetwork();
        
        // add vertices
        for (int v=0; v<numberPartitions; v++) addVertex( ) ;
        
        // add edges
        TotalNumberStubs=0;
        int s=-1;
        int t=-1;
        int cs=-1;
        int ct=-1;
        double w=1;
        //EdgeValue w = new EdgeValue();
        for (int e=0; e<oldtg.TotalNumberStubs;e++) {
            s =oldtg.getVertexFromStub(e++);
            t =oldtg.getVertexFromStub(e);
            cs = partition[s];
            ct = partition[t];
            if (weightedEdges) {
                w=(oldtg.weightedEdges ? oldtg.getEdgeWeight(e) : 1);
                // Tadpoles in new graph come with double weight if they came from a
                // none self-loop in original graph
                if (!oldtg.isDirected() && (cs==ct) && (s!=t)) w=w+w; 
                increaseEdgeWeight(cs,ct,w);                
            }
            else addEdgeUnique(cs,ct);
        }
    }

    /**
     * Makes Line graph (dual projection) from input graph.
     * <br>Each edge of input graph is mapped to a vertex. These new vertices are
     * connected if the edges in the old graph have a source and target vertex in the old graphj in
     * common.  Thus one vertex in the old graph is mapped to the many edges of a clique (complete sub graph)
     * in the new graph.  The weighting of each edge is to be decided.
     * <code>vertexEdgeListOn</code> is set for this projection to work?
     * Does not automatically edge self loop for undirected graphs since then the 
     * same edge is both and incoming and outgoing stub for one vertex.
     * <br>The vertex index <tt>v</tt> of the line graph corresponds to the egd with indices 
     * <tt>(2v)</tt> and <tt>(2v+1)</tt>in the original input graph <tt>oldtg</tt> 
     * <br><tt>useSelfLoops</tt> is true if we want undirected graphs to create self-loops 
     * for all edges because and edge alpha is both in coming and outgoing to the vertex i.
     * If a weighted line graph version is being created then weights changed accordingly.
     * <br>NOTE ignores weights of old graph.
     * @param oldtg existing timegraph to be deep copied
     * @param makeUndirected true if want to force undirected new copy
     * @param useSelfLoops true (false) if want undirected graphs to create self loops for all edges.
     * @param makeUnlabelled UNUSED (true if want to force unlabelled new copy)
     * @param reverseDirection UNUSED (true if want to reverse direction of edges)
     * @deprecated Use the makeLineGraph routine
     */
    public timgraph(timgraph oldtg, boolean makeUndirected, boolean makeUnweighted, 
                    boolean useSelfLoops, boolean makeUnlabelled, boolean reverseDirection) {
        if (!oldtg.isVertexEdgeListOn()) {
            System.err.println("*** timgraph Line Graph constructor needs vertexEdgeList to be on.");
        }
        infoLevel = oldtg.infoLevel;
        vertexEdgeListOn = true;
        initialiseRandomGenerator();
        directedGraph = oldtg.directedGraph;
        if (makeUndirected) {
            directedGraph = false;
        }
        weightedEdges = (!makeUnweighted);
        vertexlabels = false;

//        inputNameRoot=oldtg.inputNameRoot;
//        inputDirName=oldtg.inputDirName;
//        inputExtension=oldtg.inputExtension;
//        outputNameRoot=oldtg.outputNameRoot;
//        outputDirName=oldtg.outputDirName;
//        outputExtension=oldtg.outputExtension;
        inputName = new FileNameSequence(oldtg.inputName);
        outputName = new FileNameSequence(oldtg.outputName);
        
        int noselfloops = 1;
        if (useSelfLoops) noselfloops=0;
        
        
        maximumVertices = oldtg.getNumberStubs() /2; // remember this is the stub number
        // Remember edges are really stubs
        maximumStubs = 0;
        if (directedGraph) {
            for (int v = 0; v < oldtg.getNumberVertices(); v++) {
                maximumStubs += oldtg.getVertexInDegree(v) * oldtg.getVertexOutDegree(v);
            }
        } else {
            for (int v = 0; v < oldtg.getNumberVertices(); v++) {
                int k = oldtg.getVertexDegree(v);
                maximumStubs += k * (k - noselfloops) / 2;
            }
        }

        maximumStubs*=2; // remember that this is really a stub number
        
        setNetwork();

        // add vertices
        for (int v = 0; v < maximumVertices; v++) {
            addVertex();        // add edges
        }
        
        // add edges
        TotalNumberStubs = 0;
        int e1 = -1;
        int e2 = -1;
        double w = 1;
        //EdgeValue w = new EdgeValue();
        if (oldtg.isDirected()) { // directed old graph
           int kin = -1;
           int kout = -1;
            for (int v = 0; v < oldtg.getNumberVertices(); v++) {
                kin = oldtg.getVertexDegree(v);
                if (kin < 1) continue;
                kout = oldtg.getVertexDegree(v);
                if (kout < 1) continue;
                if (weightedEdges) w = 1.0 / Math.sqrt((double) kin*kout);
                for (int ei = 0; ei < oldtg.vertexInEdgeList[v].size(); ei++) {
                    e1 = oldtg.vertexInEdgeList[v].get(ei);
                    for (int eo = 0; eo < oldtg.vertexEdgeList[v].size(); eo++) {
                      e2 = oldtg.vertexEdgeList[v].get(eo);
                      if (weightedEdges) increaseEdgeWeight(e1/2, e2/2, w); // note that e and e2 are really stub indices
                      else addEdgeUnique(e1/2, e2/2);
                    }// eo for eo
                } //eo for ei
            } //eo for e
        }// eo old directed case
        else { // undirected old graph
            int kv = -1;
            for (int v = 0; v < oldtg.getNumberVertices(); v++) {
                kv = oldtg.getVertexDegree(v);
                if (kv < 1+noselfloops) continue;
                if (weightedEdges) w = 1.0 / (kv - noselfloops);
                for (int ei = 0; ei < oldtg.vertexEdgeList[v].size(); ei++) {
                    e1 = oldtg.vertexEdgeList[v].get(ei);
                    for (int eo = ei+noselfloops; eo < oldtg.vertexEdgeList[v].size(); eo++) {
                      e2 = oldtg.vertexEdgeList[v].get(eo);
                      if (weightedEdges) increaseEdgeWeight(e1/2, e2/2, w); // note that e and e2 are really stub indices
                      else addEdgeUnique(e1/2, e2/2);
                    }// eo for eo
                } //eo for ei
            } //eo for e
    } // eo old undirected case
        } // eo dual constructor
    
    
    /**
     * Initialise random generator to use time as seed.
     */
      private void initialiseRandomGenerator(){
         Rnd = new Random(); //Schildt p524, time is used as seed
        if (infoLevel>2) System.out.println("Uses time to seed Rnd");
       }

 
    /*
     * Initialises some basic parameters.
     *@param namert root of names used for input files e.g. test
     *@param dnameroot root directory name (full path) to which input or output are appended as appropriate.   (with or without slash at end)
     *@param infol information level sets information given, >0 for increasing debugging info, <0 for increasing quietness
     *@param outputc controls output levels
     */
  public void initialiseSomeParameters(String namert, String dnameroot, int infol, int outputc){
      String drt=FileNameSequence.makeDirectory(dnameroot);
      //if ((namert.equals(this.NONAME)) && (inputName.getNameRoot()))
      initialiseSomeParameters(namert,drt,namert,drt,infol, outputc);
}
    /*
     * Initialises some basic parameters.
     *@param innamert root of names used for input files e.g. test
     *@param indname directory name full path of input files (with or without slash at end)
     *@param outnamert root of names used for output files e.g. test
     *@param outdname directory name full path of output files (with or without slash at end) 
     *@param infol information level sets information given, >0 for increasing debugging info, <0 for increasing quietness
     *@param outputc controls output levels
     */
  public void initialiseSomeParameters(String innamert, String indname, String outnamert, String outdname, int infol, int outputc)
          {
              inputName = new FileNameSequence(indname,"input/", innamert,"");
              outputName = new FileNameSequence(outdname,"output/", outnamert,"");
              infoLevel=infol;
              message.setInformationLevel(infol);
              outputControl= new  OutputMode(outputc);  
              //randomWalk = new RandomWalk();
              //if (infoLevel>2) System.out.println("***  328 TG inputDirName="+ inputDirName +" ***");             
          }
  
/**
 * Creates a list of global edge indices for each vertex of the edges attached to that vertex.
 * <br>If a directed graph then also creates lists of outgoing and incoming edges.  
 * This routine does NOT yet create <code>vertexEdgeList</code> that is synchronised with the 
 * <code>vertexList</code>.
 */
  public void createVertexGlobalEdgeList(){
      vertexEdgeListOn=true;
      vertexEdgeList = new IntArrayList[maximumVertices];
      for (int s=0; s<TotalNumberVertices; s++ ) vertexEdgeList[s] = new IntArrayList(getVertexDegree(s));
      if (directedGraph) {
          vertexInEdgeList = new IntArrayList[maximumVertices];
          for (int s=0; s<TotalNumberVertices; s++ ) vertexInEdgeList[s] = new IntArrayList(getVertexInDegree(s));
      }
      
      int s=-1; int t=-1;
      for (int e=0;e<TotalNumberStubs; e++){
          s=stubSourceList[e];
          vertexEdgeList[s].add(e);
          t=stubSourceList[++e];
          if (directedGraph) vertexInEdgeList[t].add(e);
          else vertexEdgeList[t].add(e);
      }
  }
  
  
  /**
   * Returns string of version number and date.
   *@return Returns string of version number and date.
     */
  public String informationString()
  {
      return "timgraph version "+VERSION+" on "+ date;
  }
  
       /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        System.out.println("\n***********************************************************");
        timgraph tg = new timgraph(); 
        if (tg.DEBUG) 
        { tg.testtimgraph(); }
        else{
        System.out.println("       STARTING "+tg.informationString() );
        if (tg.parseParam(args)>0) return;
        }
        
        tg.doOneRun(0);
        tg.OutputGraphInfo(System.out, "#", 0.0)  ;
        tg.generalOutput("#",0.0);

    }//eo main

    public void testtimgraph()
    {
        
        System.out.println(" ### TESTING timgraph version "+VERSION+" on "+date);
        System.out.println(" ###  ");
        
        String [] arg = new String [6];
        arg[0]="-gn-5";
        arg[1]="-gv27";
        arg[2]="-gm1";
        arg[3]="-e0";
        arg[4]="-o8";
        arg[5]="-xi2";
        if (parseParam(arg)>0) return;
        
        return;
    }
    
  
// ***********************************************************************    
    
    /**
     * Sets up graph using one random walk growth run.
     * This version assumes that 
     **/  
    
    public void doOneRun()
    {
        doOneRun(0);
    }
    
    /**
     * Sets up graph using one random walk growth run.
     * <br>Sets up the initial graph according to the parameters
     * and then adds edges and vertices using the random walk doing this for numevents.
     * @param mode =0 leave numevents as given, =1 subtract initial number of vertices from numevents (vertuices to be added)
     **/  
    
    public void doOneRun(int mode)
    {
        // weightedEdges=true;
        if (infoLevel>1) printParam();
        setNetwork(initialgraph);
        if (infoLevel>0) printParam();
        switch (mode)
        {
            case 1: 
                numevents=numevents-TotalNumberVertices;
                break;
            case 0:
            default: //leave unchanged
        }
        
        calcNumberVertices();
        calcNumberEdges();
        
        double walktimetaken=-1.0;
        if (numevents>0)
        {
            initialEdges = TotalNumberStubs;
            initialVertices =  TotalNumberVertices;
       
        walktimetaken= addAll(0.01);
        
        if (infoLevel>-2) System.out.println("--- Finished " + outputName.getNameRootFullPath() + " in " + walktimetaken +"sec."  );
        
        if (infoLevel>-1)
        {
            int finaledges = calcNumberEdges();
            int finalvertices =  calcNumberVertices();
            int addedvertices = (finalvertices-initialVertices);
            int addededges = (finaledges-initialEdges);
            System.out.println("    " +  addedvertices + " vertices added, "
                               + n2s.toString(probnewvertex*numevents, 2 ) + " expected, " 
                               + n2s.toString(100.0- (100.0*addedvertices)/(probnewvertex*numevents ) , 2 ) 
                               + "% deviation.");
 
            System.out.println("    " +  (addededges) + " stubs added, requested "
                               + n2s.toString(connectivity*numevents *2,2) + ", " 
                               + n2s.toString(100.0- (100.0*addededges)/((double)(connectivity*numevents*2) ) ,2 ) 
                               + "% failed." );
            System.out.println(" Average step length " + averagesteplength);
        }
        }// eo if numevents>0
        else if (infoLevel>0) System.out.println("--- No vertices added to initial graph ---");
        generalOutput(COMMENTCHARACTER,walktimetaken);
        

        
    }   
   
    
// ***********************************************************************    
    
    /**
     * General output for generic purposes.
     *@param cc comment character
     *@param timetaken time taken so far
     * @deprecated use <tt>FileOutput.informationGeneral</tt>
     */  
    
    public void generalOutput(String cc, double timetaken)
    {
            
            if (infoLevel>-1) OutputGraphInfo(System.out, "-  ", timetaken);
            FileOutputGraphInfo(cc, timetaken);            

            calcMinMaxVertexLabel();

            if (outputControl.componentsOn) 
            {
                if (infoLevel>-1) System.out.println("\n >>> Component analysis ");
                timing.setInitialTime();
                calcComponents();
                if (infoLevel>-1) printComponentInfo();
                //FileOutputComponentDistribution(cc);
                FileOutputComponentInfo(cc,false,false);
                if (infoLevel>-1) System.out.println(" Component analysis took "+timing.runTimeString());               
            }
            
            if (outputControl.distancesOn) 
            {
                if (infoLevel>-1) System.out.println("\n >>> Distance sample randomly ");
                timing.setInitialTime();
                int minsample=100;
                //minsample = (int) (numevents*0.01);
                if (minsample>numevents) minsample=numevents/2;
                calcDistanceSample(0.01,minsample,numevents);
                if (infoLevel>-1) printDistanceTotalDistribution(false);
                //printDistanceDistribution();printDistanceList();
                FileOutputDistanceTotalDistribution(cc);
                FileOutputDistanceStatistics(cc);
                if (infoLevel>-1) System.out.print(" Distance sample took "+timing.runTimeString());
            }


            
            
             
            if (outputControl.clusteringOn) 
            {
                if (infoLevel>-1) System.out.println("\n >>> Cluster Coefficient randomly ");
                timing.setInitialTime();
                calcCCSample(0.01,1000,numevents);
                calcCCEdgeSample(0.01,1000,numevents);
                if (infoLevel>-1) printCC();
                FileOutputCC(cc);
                if (infoLevel>-1) System.out.print(" Cluster Coefficient sample took "+timing.runTimeString());
            }
            
            
           if (outputControl.degreeDistributionOn) 
            {
                if (infoLevel>-1) System.out.println("\n >>> Degree distribution ");
                timing.setInitialTime();
                calcDegreeDistribution();
                if (infoLevel>-1) System.out.println("*** Total Degree Distribution");
                if (infoLevel>-1) DDtotal.outputInformation( System.out , "... ", SEP, 3);
                FileOutputDegreeDistribution(cc,false,true);
                if (TotalNumberVertices>=LogBinVerticesMinimum) FileOutputLogBinnedDegreeDistribution(cc,1.1, true,true);        
            }

            if (outputControl.pajekFileOn) 
            {
                if (infoLevel>-1) System.out.println("\n >>> Pajek file output ");
                timing.setInitialTime();
                FileOutput fo = new FileOutput(this);
                fo.pajek();
//                 if ((outputControl & 1)>0) FileOutputWeightedPajek();
                if (infoLevel>-1) System.out.print(" Pajek File output took "+timing.runTimeString());

            }


            if (outputControl.rankingOn) 
            {
                if (averageWalkLength<0) averageWalkLength=diameter;
                if (averageWalkLength<2.0) averageWalkLength =2.0;
                if (rankingProbabilityLengthScale<0) rankingProbabilityLengthScale = distanceaverage;
                if (rankingProbabilityLengthScale<2.0) rankingProbabilityLengthScale=2.0;
//        double rankingProbability=(1.0 / (1.0+(1.0/rankingProbabilityLengthScale) ) );
                
                int totalNumberaverageWalkLength=TotalNumberStubs*100;
                int startVertex=-1;
                if (infoLevel>-1) System.out.println("\n >>> Random Walk Ranking file output ");
                if (infoLevel>-1) System.out.println("randomWalkMode, averageWalkLength, rankingProbabilityLengthScale, totalNumberaverageWalkLength, startVertex = "
                        +SEP+randomWalkMode+SEP+ averageWalkLength+SEP+ rankingProbabilityLengthScale+SEP+ totalNumberaverageWalkLength+SEP+ startVertex);
                timing.setInitialTime();
                DoRandomWalk(totalNumberaverageWalkLength, startVertex);
                if (TotalNumberStubs<40) FileOutputNetwork(true,true,true);
                else FileOutputVertices(false,false,false);
                if (vertexlabels) FileOutputPajekVertexData();
                if (infoLevel>-1) System.out.print(" Random Walk Ranking file output took "+timing.runTimeString());                
            }

            if (outputControl.structuralHolesOn)
            {
                StructuralHoleAnalysis sha = new StructuralHoleAnalysis(this);
                System.out.println("\n !!! Structural Hole Output not available except via getVertexString");
//                if (infoLevel>-1) System.out.println("\n >>> Structural Hole Analysis ");
//                FileOutput fo = new FileOutput(this);
//                sha.printStructuralHoleMeasuresVertices;
            }
            if (outputControl.adjacencyFileOn) 
            {
                if (infoLevel>-1) System.out.println("\n >>> Adjacency file output ");
                timing.setInitialTime();
                FileOutput fo = new FileOutput(this);
                fo.adjacencyMatrix(SEP,true,true);
                if (infoLevel>-1) System.out.print(" Adjacency File output took "+timing.runTimeString());

            }

} //eo generalOutput    
    
    
// ***********************************************************************    
    
    /**
     * True walk algorithm creates and updates vertexList[].
     * Weights set to be length of walk for value and 
     * label 1 (2) if connected to new+old (old-old) vertex.
     * @param eventnotefactor is fraction of events noted by a . on screen, 0
     */  
    double DoRun(double eventnotefactor)  {
        
        System.out.println("--- Starting doRun ");
        
        boolean StartWalkWithVertex = ((randomWalkMode & 1) >0); // v&1
        boolean always_new_walk_start = ((randomWalkMode & 2) >0);  // v&2
        boolean markov_walk = ((randomWalkMode & 4) >0);  // v&4
        boolean random_connectivity = ((randomWalkMode & 8) >0);  // v&8

        boolean alwaysnewvertex = true;
        if (probnewvertex < 1.0) alwaysnewvertex =false;
        
        if (infoLevel>2) 
        {
            printParam();
            System.out.println("StartWalkWithVertex, always_new_walk_start, markov_walk, random_connectivity "+StartWalkWithVertex + SEP+ always_new_walk_start 
                               + SEP+ markov_walk+ SEP + random_connectivity);
        }    
        
        double connectivityprob = (connectivity-1)/(connectivity);
        // int maxexpectedconnectivity = -((int)((Math.log(numevents)/Math.log(connectivityprob)))) ;
        // Set max connectivity equal to  be bigger than that expected once in a run
        int[] destvert = new int[maximumconnectivity];
        int[] destVertAWL = new int[maximumconnectivity]; //destination vertex average walk length
        int n,nv;
        //int  i,s,k; i=s=k=0; //needed to make catch happy
        int actualaverageWalkLength = ((int) (averageWalkLength +0.5));
        int actualconnectivity = ((int) (connectivity +0.5));
        
        boolean NewVertex;
        int initialedges = TotalNumberStubs;
        initialVertices =  TotalNumberVertices;
        
        int goodevents =0;
        
        Runtime rt = Runtime.getRuntime ();
       
        int eventnote = numevents+1; //should give no progress indication on screen
        if (eventnotefactor >0) eventnote = (int) ( ((double)numevents) * eventnotefactor);
        if (eventnote<1) eventnote=1;
        
        
        int edgessofar;
        
        float edgefraction;
        float timeleftpred;
        
        double walkprob = averageWalkLength/(1.0+averageWalkLength);
        int totalactualaverageWalkLength = 0;
        int totalactualconnectivity = 0;
        int walkvertex = 0;
        int newvertex = 0;
        maxonewalk=-1;
        int maxoneconnect=-1;
        boolean newvertexthistime=false;
        timing.setInitialTime();
        for (int i =0; i<numevents; i++) {
            if (random_connectivity) for (actualconnectivity=1; Rnd.nextDouble()<connectivityprob; actualconnectivity++ ){}
            totalactualconnectivity+=actualconnectivity;
            if (maxoneconnect<actualconnectivity) maxoneconnect=actualconnectivity;
  //          System.out.println("Event "+i+", connectivity= "+actualconnectivity); 
            if (actualconnectivity>maximumconnectivity) 
            {
                actualconnectivity=maximumconnectivity;
                System.out.println("!!! WARNING in DoRun attempted actual connectivity of "+actualconnectivity+", limited to the maximum of "+maximumconnectivity);
            }
            if ((TotalNumberStubs+actualconnectivity)>=maximumStubs) break;
            if ((TotalNumberVertices)>=maximumVertices) break;
            for (int k =0; k<actualconnectivity; k++) {
                if ((always_new_walk_start) || (k==0)) 
                {
                    if (StartWalkWithVertex) walkvertex = Rnd.nextInt(TotalNumberVertices); //getRandomVertex();
                    else walkvertex=stubSourceList[Rnd.nextInt(TotalNumberStubs)];
                }
                if (markov_walk) for (actualaverageWalkLength=0; Rnd.nextDouble()<walkprob; actualaverageWalkLength++ ){}
                totalactualaverageWalkLength+=actualaverageWalkLength;
                if (maxonewalk<actualaverageWalkLength) maxonewalk=actualaverageWalkLength;
                // if no exits from a vertex then chose final vertex as destination
                for(int s=0; ((s<actualaverageWalkLength) && (vertexList[walkvertex].size()>0)); s++) walkvertex = vertexList[walkvertex].getQuick(Rnd.nextInt(vertexList[walkvertex].size()) );
                destvert[k]=walkvertex;
                if (weightedEdges) destVertAWL[k]=actualaverageWalkLength;
                
            } //eo for k
            if ( alwaysnewvertex || (Rnd.nextDouble()<probnewvertex)) 
            { //System.out.println("Source is new vertex. ");
              //add new vertex //addVertex();
                vertexList[TotalNumberVertices] = new IntArrayList();
                newvertex= TotalNumberVertices++;  //increments after assignment
                newvertexthistime=true;
            }
            else 
            { //System.out.println("Source is old vertex. ");
                if (SourceVertex) newvertex = Rnd.nextInt(TotalNumberVertices); //getRandomVertex();
                else newvertex = stubSourceList[Rnd.nextInt(TotalNumberStubs)]; //getRandomVertexFromEdge(); //needs to be random edge
                newvertexthistime=false;
            }
            for(int k=0;k<actualconnectivity;k++) //addEdge(newvertex,destvert[k]);
            {   
                int v2 =destvert[k];
                vertexList[newvertex].add(v2);
                vertexList[v2].add(newvertex);
                if (weightedEdges) 
                {
                    if (newvertexthistime) 
                        edgeValuetList[TotalNumberStubs]= new EdgeValue(1,destVertAWL[k]);
                    else edgeValuetList[TotalNumberStubs]= new EdgeValue(2,destVertAWL[k]);
                }
                stubSourceList[TotalNumberStubs++]=newvertex;
                if (weightedEdges) 
                {
                    if (newvertexthistime) 
                        edgeValuetList[TotalNumberStubs]= new EdgeValue(1,destVertAWL[k]);
                    else edgeValuetList[TotalNumberStubs]= new EdgeValue(2,destVertAWL[k]);
                }
                stubSourceList[TotalNumberStubs++]=v2;
                
            }// for k
            
//            TotalNumberStubs+=(connectivity << 1); // shift left multiplys by 2
        } // eo for i
        timing.setCurrentTime();
        int addedvertices = (TotalNumberVertices-initialVertices);
        int addededges = (TotalNumberStubs-initialedges);
        System.out.println("Added "+addedvertices+" vertices and "+addededges+" edges in " + timing.runTimeString()); 
        double averagenewconnectivity = ((double) addededges)/(addedvertices*2.0);
        System.out.println("Average new connectivity = "+NumbersToString.toString(averagenewconnectivity,3) +
        ", largest one connectivity = "+maxoneconnect); 
        averagesteplength = 2.0*((double) totalactualaverageWalkLength)/((double) addededges);
        System.out.println("average step = "+NumbersToString.toString(averagesteplength,3) +
        ", longest single walk  = "+maxonewalk); 
                
        if (infoLevel>1){
            long currentmem = rt.freeMemory();
            long totalmem = rt.totalMemory();
            System.out.println("\nFree memory "+currentmem+" out of "+totalmem+" total");

            int finaledges = TotalNumberStubs;
            int finalvertices =  TotalNumberVertices;
            int totvertices = (finalvertices-initialVertices);
            int totedges = (finaledges-initialedges);
            
            System.out.println("*** Finished Run in "+timing.runTimeString());

//            System.out.println("    " + goodevents + " good events out of " + numevents +" attempted." );
            System.out.println("    " + numevents +" events." );
            System.out.println("    " +  (finalvertices-initialVertices) + " vertices added." );
            System.out.println("    " +  (finaledges-initialedges) + " edges added, expected " + (goodevents*connectivity) );
            }
     return(0.0);  
    }  
// ***********************************************************************


    /*
     * Initialise the random walk used to study the timgraph.
     * Sets up random walk with no ranking and no fixed vertex starting point by default.
     *@param randomWalk.randomWalkMode sets mode of random walk
     *@param averavgeWalkLength sets average length of random walks
     *@param maxWalkLength sets maximum length of random walks
     */
    public void initialiseRandomWalk(int randomWalkMode, double averageWalkLength, double maxWalkLength) {
        randomWalk = new RandomWalk(this, randomWalkMode, averageWalkLength, maxWalkLength);
    }
    
    
// ***********************************************************************

    /**
     * Does many random walks on the graph.
     * <br>Random Diffusion on graph defined by vertexList[].
     * Random walk for average of averageWalkLength (global).
     * Mode of walks set by bits of the integer randomWalkMode.
     * Always start new walks with a vertex [edge] if (randomWalkMode & 1)=1 [0]
     * If (randomWalkMode & 2)>0 then always jumps to restart a walk 
     * otherwise the walk length just rests the diffusion parameters not its location 
     * when walk length reached or no exit available. 
     * If (randomWalkMode & 4)>0 [0] then uses random  process for walk 
     * else uses fixed length walks to achieve average of averageWalkLength (global) length walks.
     * If (randomWalkMode & 16)>0 [0] then uses binomial distributed walks with averageWalkLength (global)
     * walks and the binomialNumber (global) setting the number of dice (&lt;1 gives fixed length, =1 flat distribution).
     * The alternative is a Markov process where walks continue with probability choosen to achieve averageWalkLength. 
     *
     * If vertices are labelled the rank is set using averageWalkLength and (rankingProbability)^averageWalkLength
     * while number of visits is recorded.  Both assumed to be initialised as this is not done in the routine.
     *
     * @param totalNumberaverageWalkLength total number of averageWalkLength made
     * @param startVertex used to set initial vertex of new walks, random chosen if negative
     */
    public double DoRandomWalk(int totalNumberaverageWalkLength, int startVertex)  
    {
        //randomWalkMode, averageWalkLength, are global. 
        // rankingProbability calculated from global rankingProbabilityLengthScale
        Runtime rt = Runtime.getRuntime ();
        if (infoLevel>0) System.out.println("--- Starting DoRandomWalk ");
        boolean StartWalkWithVertex = ((randomWalkMode & 1) >0); // v&1
        boolean always_new_walk_start = ((randomWalkMode & 2) >0);  // v&2
        boolean markov_walk = ((randomWalkMode & 4) >0);  // v&4
//        boolean random_connectivity = ((randomWalkMode & 8) >0);  // v&8 
        boolean StartWalkWithFixedVertex = true; 
        boolean binomialDistribution = ((randomWalkMode & 16) >0);  // v&16
        
        double walkprob = averageWalkLength/(1.0+averageWalkLength);
        maxWalkLength  = ((int) (averageWalkLength + 0.5))*3;
        int intAverageWalkLength= (int) (averageWalkLength+0.5); //set integer walk length
        
        if (markov_walk) maxWalkLength = totalNumberaverageWalkLength; // maximum number of averageWalkLength
        maxonewalk=-1;
        
        double rankingProbability = rankingProbabilityLengthScale/(1.0+rankingProbabilityLengthScale);
        
        
        if (startVertex >= TotalNumberVertices) 
        {
            System.out.println("*** Error startVertex "+startVertex + ">="+ TotalNumberVertices+" TotalNumberVertices");
            return -1.0;
        }
        if (startVertex < 0)  StartWalkWithFixedVertex = false;
        
        if (infoLevel>2)
        {
            printParam();
           System.out.println("StartWalkWithVertex, always_new_walk_start, markov_walk, random_connectivity "+StartWalkWithVertex
                               + SEP+ markov_walk);
        }

        int e=-1;
        int degree =-1;
        int walkvertex = 0;

         if (vertexlabels) {
             for (int v=0; v<TotalNumberVertices; v++) vertexLabelList[v].setRank(new Rank());
         }
        
        // First event/step is a dummy to get started 
        int stepOnWalk=-1; // this is the number of individual random walk averageWalkLength taken so far
        double diffuseValue=1.0;
        int numberWalks=0;
               
        timing.setInitialTime();
        for (int event =0; event<totalNumberaverageWalkLength; event++) 
        {
            if (stepOnWalk <0) 
            { // start fresh walk
                numberWalks++;
                if (markov_walk) 
                {
                    if (binomialDistribution) stepOnWalk=getRandomBinomial(averageWalkLength,binomialNumber);
                    else stepOnWalk=getRandomMarkov(walkprob,binomialNumber);
                }
                else stepOnWalk = intAverageWalkLength; //fixed length walks
                diffuseValue=1.0;
                if ((always_new_walk_start) || degree<1)
                {
                    if (StartWalkWithVertex)
                    walkvertex = (StartWalkWithFixedVertex ? walkvertex=startVertex : Rnd.nextInt(TotalNumberVertices) ); //getRandomVertex();
                    else 
                    { // find random edge for start
                        if (directedGraph) e = makeEven(Rnd.nextInt(TotalNumberStubs)) ;
                        else e = Rnd.nextInt(TotalNumberStubs) ;
                        walkvertex=stubSourceList[ e ];
                    } //eo if StartWalkWithVertex
                } //if (always_new_walk_start)
                if (maxonewalk<stepOnWalk) maxonewalk=stepOnWalk;                
            }// if Rnd.nextDouble()>walkprob
            else 
            { // continue with walk
                stepOnWalk--;
                diffuseValue*=rankingProbability;
                walkvertex = vertexList[walkvertex].getQuick(Rnd.nextInt(degree));              
            }// else

            degree=vertexList[walkvertex].size();            
            if (vertexlabels) 
              {
                vertexLabelList[walkvertex].rank.updateRanking(stepOnWalk, diffuseValue);
//                  vertexLabelList[walkvertex].rank.visits++;
//                  vertexLabelList[walkvertex].rank.value += stepOnWalk ;
//                  vertexLabelList[walkvertex].rank.value2 += diffuseValue;
              }

            if ( degree ==0) stepOnWalk =-1; // use to start a new walk if no neighbour
              
        } // eo for event
        timing.setCurrentTime(); 
        String rts = timing.runTimeString();

        if (numberWalks>0) averagesteplength = totalNumberaverageWalkLength/numberWalks;
        else averagesteplength = - totalNumberaverageWalkLength;
        System.out.println("No. walks "+SEP+numberWalks+SEP+" average step = "+SEP+NumbersToString.toString(averagesteplength,3) +
        SEP+" longest single walk "+SEP+maxonewalk);

        if (infoLevel>1)
        {
            long currentmem = rt.freeMemory();
            long totalmem = rt.totalMemory();
            System.out.println("\nFree memory "+currentmem+" out of "+totalmem+" total");
            System.out.println("*** Finished DoRandomWalk in "+rts);
            }
     return(timing.elapsedTime());
    }
   // *********************************************************
    /**
     * Returns the vertex label storing the lowest values.
     * <p>Returns null if not set.
     */
    public VertexLabel getMinimumVertexLabel(){
        return minimumVertexLabel;
    }

   /**
     * Returns the vertex label storing the largest values.
     * <p>Returns null if not set.
     */
    public VertexLabel getMaximumVertexLabel(){
        return maximumVertexLabel;
    }

  /**
     * Returns the smallest vertex number (a label not an index) if set.
     * <p>Returns negative value {@value IUNSET} if not set.
     */
    public int getMinimumVertexNumber(){
        if (minimumVertexLabel!=null && minimumVertexLabel.hasNumber())
        return minimumVertexLabel.getNumber();
        return IUNSET;
    }

   /**
     * Returns the largest vertex number (a label not an index) if set.
     * <p>Returns negative value {@value IUNSET} if not set.
     */
    public int getMaximumVertexNumber(){
        if (maximumVertexLabel!=null && maximumVertexLabel.hasNumber())
        return maximumVertexLabel.getNumber();
        return IUNSET;
    }

    /**
     * Returns the minimum coordinate if set.
     */
    public Coordinate getMinimumVertexCoordinate(){
        if (minimumVertexLabel!=null && minimumVertexLabel.hasPosition())
        return minimumVertexLabel.getPosition();
        return null;
    }

    /**
     * Returns the maximum coordinate if set.
     */
    public Coordinate getMaximumVertexCoordinate(){
        if (maximumVertexLabel!=null && maximumVertexLabel.hasPosition())
        return maximumVertexLabel.getPosition();
        return null;
    }

    /**
     * Calculates the maximum values of vertex label values.
     */
    public void calcMinimumVertexLabel()

    {
        if (!isVertexLabelled()) {minimumVertexLabel= null; return;}
        this.minimumVertexLabel= new VertexLabel();
        for (int v=0;v<TotalNumberVertices;v++) vertexLabelList[v].setMinimum(minimumVertexLabel);
    }

    /**
     * Calculates the maximum values of vertex label values.
     */
    public void calcMaximumVertexLabel()

    {
        if (!isVertexLabelled()) {maximumVertexLabel= null; return;}
        this.maximumVertexLabel= new VertexLabel();
        for (int v=0;v<TotalNumberVertices;v++) vertexLabelList[v].setMaximum(maximumVertexLabel);
    }

    /**
     * Calculates the minimum and maximum values of vertex label values.
     */
    public void calcMinMaxVertexLabel()
    {
        calcMinimumVertexLabel(); calcMaximumVertexLabel();
    }

   // *********************************************************
//    /**
//     * Returns the EdgeValue storing the lowest values of weights and labels.
//     * <p>Returns null if not set.
//     */
//    public EdgeValue getMinimumEdgeWeight(){
//        return minimumEdgeValue;
//    }
//
//   /**
//     * Returns the EdgeValue storing the largest values of weights and labels.
//     * <p>Returns null if not set.
//     */
//    public EdgeValue getMaximumEdgeWeight(){
//        return maximumEdgeValue;
//    }

        /**
     * Returns the minimum weight if set.
     */
    public EdgeValue getMinimumEdgeValue(){
        return minimumEdgeValue;
    }

    /**
     * Returns the maximum weight if set.
     */
    public EdgeValue getMaximumEdgeValue(){
        return maximumEdgeValue;
    }

    /**
     * Returns the minimum weight if set.
     */
    public double getMinimumEdgeWeight(){
        if (minimumEdgeValue!=null && minimumEdgeValue.hasWeight())
                return minimumEdgeValue.getWeight();
        return DUNSET;
    }

    /**
     * Returns the maximum weight if set.
     */
    public double getMaximumEdgeWeight(){
        if (maximumEdgeValue!=null && maximumEdgeValue.hasWeight())
                return maximumEdgeValue.getWeight();
        return DUNSET;
    }


  /**
     * Returns the smallest edge label if set.
     * <p>Returns negative value {@value IUNSET} if not set.
     */
    public int getMinimumEdgeLabel(){
        if (minimumEdgeValue!=null && minimumEdgeValue.hasLabel())
        return minimumEdgeValue.getLabel();
        return IUNSET;
    }

   /**
     * Returns the largest edge label if set.
     * <p>Returns negative value {@value IUNSET} if not set.
     */
    public int getMaximumEdgeLabel(){
        if (maximumEdgeValue!=null && maximumEdgeValue.hasLabel())
        return maximumEdgeValue.getLabel();
        return IUNSET;
    }

    /**
     * Calculates the maximum values of vertex ranking values.
     */
    public void calcMinimumEdgeValue()
    {
        if (!this.isWeighted()) {minimumEdgeValue= null; return;}
        this.minimumEdgeValue= new EdgeValue();
        for (int s=0;s<TotalNumberStubs;s++) edgeValuetList[s].setMinimum(minimumEdgeValue);
    }

    /**
     * Calculates the maximum values of vertex ranking values.
     */
    public void calcMaximumEdgeValue()

    {
        if (!this.isWeighted()) {maximumEdgeValue= null; return;}
        this.maximumEdgeValue= new EdgeValue();
        for (int s=0;s<TotalNumberStubs;s++) edgeValuetList[s].setMaximum(maximumEdgeValue);
    }

// *********************************************************
//    /**
//     * Calculates the maximum values of vertex ranking values.
//     *
//     */
//    public void calcMaximumRank()
//
//    {
//        maxRank= new Rank();
//        for (int v=0;v<TotalNumberVertices;v++) maxRank.setMaximum(vertexLabelList[v].rank);
//    }

//// *********************************************************
//    /**
//     * Calculates the maximum and minimum values of coordinates.
//     *
//     */
//
//    public void calcMaxMinCoordinates()
//    {
//        for (int v=0;v<TotalNumberVertices;v++) maxRank.setMaximum(vertexLabelList[v].rank);
//    }
     
// --------------------------------------------------------------------------
   /** 
     * Adjusts the weights of edges added with zero step length (zero weights).
     */    
  public void adjustZeroWalkWeights()
  {
      int zerowalkweight = maxonewalk*2+1;
        for (int e=0; e<TotalNumberStubs; e++)
        {
            if (edgeValuetList[e].weight==0) edgeValuetList[e].weight+= zerowalkweight;
        }
      return;
  }
    
// ---------------------------------------------------------      
   /** 
     * Prints information on walks used for network construction.
    *@param eventcounter 
    *@param initialvertices
    *@param initialedges
    *@param initialmem memory
    *@param rt run time
    *@param eventnote
    *@param infoLevel
     */    
  public void printWalkInfo(int eventcounter, 
                           int initialvertices, int initialedges, 
                           long initialmem, Runtime rt, 
                           int eventnote, int infoLevel )  
 {
     if (infoLevel>1) calcNumberVertices(); calcNumberEdges();
     if (infoLevel>0) {
         if ((eventcounter+1)%eventnote == 0 ) 
         {
             System.out.print(".");
             if ((eventcounter+1)%(10*eventnote) == 0) {
                 long currentmem = rt.freeMemory();
                 long totalmem = rt.totalMemory();
                 int edgessofar = (TotalNumberStubs-initialedges);
                 double edgefraction = ((double) edgessofar)/(2*numevents*connectivity);
                 double timeleftpred =  ( timing.elapsedTime()*( (1.0/ (edgefraction*edgefraction) ) -1.0)) ;
                 
                 System.out.println(" M:"+(totalmem-currentmem)+"/"+totalmem+", " +
                 timing.runTimeString() + " - "+ timing.runTimeString(timeleftpred) +" )");
                 System.out.println("           Ev:" + (eventcounter) + "/" + numevents +", V:" +
                 (TotalNumberVertices-initialvertices) + ", E:"+  edgessofar
                 + "/" + (2*(eventcounter)*connectivity) + ", (Tot "+ ((int) (0.5 + edgefraction*100.0)) +"% ?)" );
                 
             } // if l+1
         } //if i+1
     } //if infoLevel
     return;
  }
    
// ***********************************************************************    
    
    /**
     * True ER graph creator.
     * @param eventnotefactor is fraction of events noted by a . on screen, 0
     */  
    double DoER(double eventnotefactor)  
    {
        System.out.println("--- Starting doER ");
        
        int[] destvert = new int[maximumconnectivity];
        int n,nv;
        //int  i,s,k; i=s=k=0; //needed to make catch happy
        int actualaverageWalkLength = ((int) (averageWalkLength +0.5));
        int actualconnectivity = ((int) (connectivity +0.5));
        
        boolean NewVertex;
        int initialedges = TotalNumberStubs;
        initialVertices =  TotalNumberVertices;
        
        int goodevents =0;
        
        Runtime rt = Runtime.getRuntime ();
       
        int eventnote = numevents+1; //should give no progress indication on screen
        if (eventnotefactor >0) eventnote = (int) ( ((double)numevents) * eventnotefactor);
        if (eventnote<1) eventnote=1;
        
        
        int edgessofar;
        
        float edgefraction;
        float timeleftpred;
        
        long initialtime = System.currentTimeMillis ();
        double totsec;
        
        int maxoneconnect=-1;
        for (int i =0; i<numevents; i++) 
        {
            addVertex();
        } // eo for i
        
        int addedvertices = (TotalNumberVertices-initialVertices);
        
        int sv,tv;
        int ne = (int) (connectivity * numevents);
        for (int i =0; i<ne; i++) 
        {
            sv = Rnd.nextInt(TotalNumberVertices);
            tv = Rnd.nextInt(TotalNumberVertices);
            if (weightedEdges) addEdge(sv,tv, new EdgeValue(1,1.0));
            else addEdge(sv,tv);
        } // eo for i
        int addededges = (TotalNumberStubs-initialedges);
        long finaltime = System.currentTimeMillis ();
        totsec = n2s.TruncDecimal((finaltime-initialtime)/1000.0,2);
        System.out.println("Added "+addedvertices+" vertices and "+addededges+" edges in " +totsec+"sec"); 
        double averagenewconnectivity = ((double) addededges)/(addedvertices*2.0);
        System.out.println("Average new connectivity = "+n2s.toString(averagenewconnectivity,3) +
        ", largest one connectivity = "+maxoneconnect); 
                
        if (infoLevel>1){
            long currentmem = rt.freeMemory();
            long totalmem = rt.totalMemory();
            System.out.println("\nFree memory "+currentmem+" out of "+totalmem+" total");

            int finaledges = TotalNumberStubs;
            int finalvertices =  TotalNumberVertices;
            int totvertices = (finalvertices-initialVertices);
            int totedges = (finaledges-initialedges);
            
            System.out.println("*** Finished Run in "+timing.runTimeString());
//            System.out.println("    " + goodevents + " good events out of " + numevents +" attempted." );
            System.out.println("    " + numevents +" events." );
            System.out.println("    " +  (finalvertices-initialVertices) + " vertices added." );
            System.out.println("    " +  (finaledges-initialedges) + " edges added, expected " + (goodevents*connectivity) );
            }
     return(totsec);  
    }    

// ***********************************************************************    
    
    /**
     * BiPartite Copy Model a la Bentley et al.
     * Assumes bipartite graph already set
     * @param eventnotefactor is fraction of events noted by a . on screen, 0
     */  
    double DoBPCopyModel(double eventnotefactor)  
    {
        if (! bipartiteGraph) {
            System.out.println("--- not a bipartite graph for DoBPCopyModel ");
            return(-1.0);
        }
        System.out.println("--- Starting DoBPCopyModel ");
        
        double probprapp = probpra+probpp;
        double probprprapp = probprapp+probpr;
        
        Runtime rt = Runtime.getRuntime ();
       
        int eventnote = numevents+1; //should give no progress indication on screen
        if (eventnotefactor >0) eventnote = (int) ( ((double)numevents) * eventnotefactor);
        if (eventnote<1) eventnote=1;
        
        
        //int edgessofar;
        
        float edgefraction;
        float timeleftpred;
        
        long initialtime = System.currentTimeMillis ();
        double totsec;

        initialVertices = TotalNumberVertices;
        int initialedges = TotalNumberStubs;
        
        int vs,vt,e,enew,vnew;
        int ne = (int) (numevents);
        double eventvalue;
        int eventtype;
        boolean removetarget;
        for (int i =0; i<ne; i++) 
        {
            // preferential removal
            e = (Rnd.nextInt(TotalNumberStubs) >> 1) << 1; // makes it even = source
            if ((e &1) == 1) System.out.println("ERROR edge not even in DoBPCopyModel"); 
            if (stubSourceList[e]>=numberVertexType1) System.out.println("ERROR edge source not type 2");
//            removetarget = ((stubSourceList[e]<numberVertexType1) ? true : false);
//            eventvalue=Rnd.nextDouble();
//            if (event < probqp ) 
//            {
//                e = Rnd.nextInt(TotalNumberStubs) ;
//                removetarget = ((stubSourceList[e]<numberVertexType1) ? true : false);
//            }
//            else 
//            {
//               // random active vertex selection
//            }
               
            eventtype=0;
            eventvalue = Rnd.nextDouble();
            if (eventvalue < probpp  ) eventtype ++;
            if (eventvalue < probprapp ) eventtype ++;
            if (eventvalue < probprprapp ) eventtype ++;
            vnew=-1;
            switch (eventtype)
            {
                case 0: // pbar event new type 2 vertex
                {   
                    System.out.println(" ... pbar event new type 2 vertex, "+eventtype);
                    vnew = addVertex();
                    numberVertexType2++;
                    break;
                }
                case 1: // pr event random type 2 vertex
                { 
                    System.out.println("... pr event random type 2 vertex, "+eventtype);
                    vnew = Rnd.nextInt(numberVertexType2)+numberVertexType1;
                    break;
                }
                case 2: // pra event random active type 2 vertex
                {
//                      System.out.println("... pra event random active type 2 vertex, "+eventtype);
//                    vnew = activevertexList.get(Rnd.nextInt(activevertexList.size() ));
//                    break;
                }
                case 3:
                {   // Preferential attachment, copying
                    System.out.println("... Preferential attachment, copying, "+eventtype);
                    enew = Rnd.nextInt(TotalNumberStubs) | 1; //makes it odd = target = type 2
                    vnew = stubSourceList[enew];
                    if (vnew<numberVertexType1) vnew-= 1;
                }
            }
            if (vnew<1) 
            {
                System.out.println("--- vnew <0 in DoBPCopyModel ");
                return(-1);
            }
            if (vnew<numberVertexType1) System.out.println("ERROR new vertex not type 2"); 
            rewireEdgeTarget(e, vnew) ;
            
            
        } // eo for i
        
        int addedvertices = (TotalNumberVertices-initialVertices);
        int addededges = (TotalNumberStubs-initialedges);
        
        
        long finaltime = System.currentTimeMillis ();
        totsec = n2s.TruncDecimal((finaltime-initialtime)/1000.0,2);
        System.out.println("Added "+addedvertices+" vertices and "+addededges+" edges in " +totsec+"sec"); 
//        double averagenewconnectivity = ((double) addededges)/(addedvertices*2.0);
//        System.out.println("Average new connectivity = "+n2s.toString(averagenewconnectivity,3) +
//        ", largest one connectivity = "+maxoneconnect); 
                
        if (infoLevel>1){
            long currentmem = rt.freeMemory();
            long totalmem = rt.totalMemory();
            System.out.println("\nFree memory "+currentmem+" out of "+totalmem+" total");

            int finaledges = TotalNumberStubs;
            int finalvertices =  TotalNumberVertices;
            int totvertices = (finalvertices-initialVertices);
            int totedges = (finaledges-initialedges);
            
            System.out.println("*** Finished Run in "+timing.runTimeString());
//            System.out.println("    " + goodevents + " good events out of " + numevents +" attempted." );
            System.out.println("    " + numevents +" events." );
            System.out.println("    " +  (finalvertices-initialVertices) + " vertices added." );
            System.out.println("    " +  (finaledges-initialedges) + " edges added");
            }
     return(totsec);  
    }    
    
    
// **************************************************************************
// add routines
    
// -------------------------------------------------------------------------- 
    /**
     * Chooses routine to use to add new vertices.
     * @param eventnotefactor is fraction of events noted by a . on screen, 0
     */  
    double addAll(double eventnotefactor)  
    {
      double result=-1.0;
      switch (edgegenerator) 
      {
          case 0: 
          {
              result=DoRun(eventnotefactor);  
              break;
          }
          case 1: 
          {
              result = DoER(eventnotefactor);  
              break;
          }
          default: {System.out.println(" *** Edge generator "+edgegenerator+" unknown ***");}
      }
    return result;      
    }
    
    
// ---------------------------------------------------------      
   /** 
     * Adds basic parts of a new vertex to a graph.
     * <br>New vertex has index <code>TotalNumberVertices</code>
    * but other addVertex routines must finish off the process..
     */    
  private void addVertexBasic()  
  {      
      vertexList[TotalNumberVertices] = new IntArrayList();
      if (directedGraph) vertexSourceList[TotalNumberVertices] = new IntArrayList();
      if (vertexEdgeListOn) {
          vertexEdgeList[TotalNumberVertices] = new IntArrayList();
          if (directedGraph) vertexInEdgeList[TotalNumberVertices] = new IntArrayList();
      }
      if (vertexlabels) vertexLabelList[TotalNumberVertices] = new VertexLabel();
  }       
   /** 
     * Adds new vertex.
     * <br>Will deal with directed and undirected graphs and give default label to labelled graph.
     * <br>New vertex has index <code>TotalNumberVertices</code> and then this global is incremented.
     */    
  public int addVertex()  
  {      
      addVertexBasic();
      if (vertexlabels) vertexLabelList[TotalNumberVertices] = new VertexLabel(TotalNumberVertices);
      return TotalNumberVertices++;
  }       
// ---------------------------------------------------------      
   /** 
     * Adds vertex with label vlabel.
     * @param vlabel label of vertex
     */    
  public int addVertex(VertexLabel vlabel)  {      
      addVertexBasic();
      vertexLabelList[TotalNumberVertices] = new VertexLabel(vlabel);
      return TotalNumberVertices++;
  }       
// ---------------------------------------------------------      
   /** 
     * Adds vertex of specified strength and position.
     * @param strength of vertex
     * @param position of vertex
     */    
  public int addVertex(double strength, Coordinate position)  {      
      addVertexBasic();
      String vname="("+position.x+","+position.y+")";
      vertexLabelList[TotalNumberVertices] = new VertexLabel(vname,strength, position);
      return TotalNumberVertices++;
  }       

// ---------------------------------------------------------      
   /** 
     * Adds vertex with vertex label of given of name.
    * <p>Number is set to its index
     * @param name of vertex
     */    
  public int addVertex(String name)  {      
      addVertexBasic();
      vertexLabelList[TotalNumberVertices] = new VertexLabel(name,TotalNumberVertices);
      return TotalNumberVertices++;
  }       

   /** 
     * Adds vertex with vertex label of given of name and number.
     * @param name of vertex
     * @param number of vertex
     */    
  public int addVertex(String name, int number)  {      
      addVertexBasic();
      vertexLabelList[TotalNumberVertices] = new VertexLabel(name,number);
      return TotalNumberVertices++;
  }       

  // ---------------------------------------------------------      
   /** 
     * Adds source parts of edge between from vertex v1 to v2.
    * <br>Deals with directedness and veretxEdgeList but not weights.
    * <br>Index of this edge will be <code>TotalNumberStubs</code> which is NOT updated.
     * <br>No multiedge or self loop checks.
     * @param v1 first vertex
     * @param v2 second vertex
     */    
  private void addEdgeSourceBasic(int v1, int v2)  
  {
//      System.out.println("adding edge "+v1+" : "+v2+", TNS "+TotalNumberStubs);
      vertexList[v1].add(v2);
      if (vertexEdgeListOn) vertexEdgeList[v1].add(TotalNumberStubs);
      stubSourceList[TotalNumberStubs]=v1;
  }
     /** 
     * Adds directed or undirected edge between vertex v1 and v2.
     * <br>No multiedge or self loop checks.
     * @param v1 first vertex
     * @param v2 second vertex
     */    
  private void addEdgeTargetBasic(int v1, int v2)  
  {
      if (!directedGraph) vertexList[v2].add(v1);
      else vertexSourceList[v2].add(v1);
      if (vertexEdgeListOn) if (directedGraph) vertexInEdgeList[v2].add(TotalNumberStubs);
      else vertexEdgeList[v2].add(TotalNumberStubs);
      stubSourceList[TotalNumberStubs]=v2;
  }       
   /** 
     * Adds directed or undirected edge between vertex v1 and v2.
    * <br>Works for all type of graph, with defaults added for weights.
    * <br>For faster adding you may want to use more specific add vertex routines.
    * <br>Note that self-loops in undirected graphs 
    * will have add two entries in the vertexList[v1] list equal to v2=v1.
    * <br>Multi edges allowed, checks for self loops.
     * @param v1 first vertex
     * @param v2 second vertex
     */    
  public void addEdge(int v1, int v2)  
  {
      if (v1==v2 && !selfLoops) return;

      addEdgeSourceBasic(v1, v2);
      if (weightedEdges) edgeValuetList[TotalNumberStubs]= new EdgeValue();
      TotalNumberStubs++;

      addEdgeTargetBasic(v1, v2);  
      if (weightedEdges) edgeValuetList[TotalNumberStubs]= new EdgeValue();
      TotalNumberStubs++;
  }       
   /** 
     * Adds directed or undirected edge between vertex v1 and v2.
     * <br>Assumes we have an unweighted graph.
     * <br>Checks for self-loops
     * <br>Multi edges allowed.
     * @param v1 first vertex
     * @param v2 second vertex
     */    
  public void addEdgeUnweighted(int v1, int v2)  
  {
//      System.out.println("adding edge "+v1+" : "+v2+", TNS "+TotalNumberStubs);
      if (v1==v2 && !selfLoops) return;

      addEdgeSourceBasic(v1, v2);
      TotalNumberStubs++;
      addEdgeTargetBasic(v1, v2);  
      TotalNumberStubs++;
  }       

// ...................................................................
   /** 
    * Adds directed or undirected edge between vertex v1 and v2 for a weighted graph.
     * <br>Weights same object for both directions so update on one will update second.
     * Graph must be weighted, no tests done.
     * <br>Checks for self loops.
     * @param v1 int first vertex
     * @param v2 int second vertex
     * @param edgeweight is edgeweight
     */    
  public void addEdge(int v1, int v2, EdgeValue edgeweight)
  {
      //addEdge(v1, v2, edgeweight, edgeweight)  ;
//      if (!weightedEdges) System.err.println("*** Adding weighted edge when it is not a weighted graph);");      
//      System.out.println("adding edge "+v1+" : "+v2+", TNS "+TotalNumberStubs);

      if (v1==v2 && !selfLoops) return;

      addEdgeSourceBasic(v1, v2);
      edgeValuetList[TotalNumberStubs++]= edgeweight;
      
      addEdgeTargetBasic(v1, v2);
      edgeValuetList[TotalNumberStubs++]= edgeweight;

  }       
// ...................................................................
   /** 
    * Adds directed or undirected edge between vertex v1 and v2.
     * <p>Weights same for both directions
     * Graph must be weighted as no tests done.  Multiedges will be created
     * if requested, no tests done.
     * @param v1 int first vertex
     * @param v2 int second vertex
     * @param edgeweight is the edgeweight (other edge characteistics are not set)
     */    
  public void addEdge(int v1, int v2, double edgeweight)  
  {
  EdgeValue ew = new EdgeValue(edgeweight);
  addEdge(v1, v2, ew);
  }       

  // ...................................................................
   /** 
    * Adds edge with/without direction/weights between vertex v1 and v2.
     * <br>Give dummy weights if not weighted.
     * @param v1 first vertex
     * @param v2 second vertex
     * @param edgeweight1 is edgeweight for edge from first vertex to second vertex
     * @param edgeweight2 is edgeweight for edge from second vertex to first vertex
     * @deprecated Until we decide why we want one (directed) edge to have two separate edgeWeight objects
     */    
  public void addEdge(int v1, int v2, EdgeValue edgeweight1, EdgeValue edgeweight2)
  {
      if (!weightedEdges) System.err.println("*** Adding weighted edge when it is not a weighted graph);");
      
//      System.out.println("adding edge "+v1+" : "+v2+", TNS "+TotalNumberStubs);
      addEdgeSourceBasic(v1, v2);  
      edgeValuetList[TotalNumberStubs++]= edgeweight1;
      
      addEdgeTargetBasic(v1, v2);
      edgeValuetList[TotalNumberStubs++]= edgeweight2;
  }       
    /**
     * Increases an edges weight or creates a new edge.
     * <br>Works for unweighted graphs in which case it merely creates an edge if it doesn't already exist.
     * Takes the first edge with target <tt>t</tt> in the <tt>vertexList[s]</tt>.
     * If undirected it does not check to see if any edges are listed with <tt>t</tt> as source to 
     * <tt>s</tt> as target.  Weight is ignored if unweighted graph.
     * <br>This ensures that there are no multiedges but that setting is not used here.
     * <br>Checks for self loops
     * @param s source vertex
     * @param t target vertex
     * @param dw increase in weight (ignored if no weighted edges)
     * @see #addEdgeUnique(int, int) for unweighted version
     */
    public void increaseEdgeWeight(int s, int t, double dw) {
        if (s==t && !selfLoops) return;
        int e = getFirstEdgeGlobal(s, t);
        if (e < 0) {
            if (weightedEdges) addEdge(s, t, dw);
            else addEdge(s, t);
        } else {
            if (weightedEdges) {
                edgeValuetList[e++].weight += dw;
                //edgeValuetList[e].weight += dw; Not needed as e and e+1 point to same edgeWeight object
            }
        }
        if (infoLevel > 2) {
            System.out.print("Edge from " + s + " to " + t);
            if (weightedEdges) {
                if (e<1) e=TotalNumberStubs-1;
                System.out.println(", edge index "+(e-1)+", has had weight increased by " + dw + " to become " + edgeValuetList[e-1].weight);
                System.out.print("Edge from " + s + " to " + t);
                System.out.println(", edge index "+(e)+", has had weight increased by " + dw + " to become " + edgeValuetList[e].weight);
            } else {
                System.out.println(" now exists.");
            }
        }
    }
      
  /**
   * Creates a new edge but only if such an edge does not already exist.
   * <br>Checks for self loops but multiedge setting is not used.
   * @param s source vertex
   * @param t target vertex
   * @return TotalNumberStubs if edge added, -1 if no edge added
   * @see #increaseEdgeWeight(int, int, double) for alternative
   */
      public int addEdgeUnique(int s, int t){
          if (s==t && !selfLoops) return -1;
          if (!edgeExists(s,t)) {addEdge(s,t); return TotalNumberStubs;}
          return -1;
      }


  /**
   * Creates a new edge with full tests performed.
   * <br>Checks for self-loops and multiedge.  Weight ignored if not weighted.
   * @param s source vertex
   * @param t target vertex
   * @param dw weight for new edge or to be added to existing edge if no multiedges
   * @return TotalNumberStubs if edge added, -1 if no edge added
   * @see #increaseEdgeWeight(int, int, double) for alternative
   */
      public void addEdgeWithTests(int s, int t, double dw){
          if (!weightedEdges) {addEdgeWithTests(s,t); return;}
          if (multiEdge) addEdge(s,t,dw);
          else increaseEdgeWeight(s, t, dw);
      }
  /**
   * Creates a new edge with full tests performed.
   * <br>Checks for self loops and multiedge.  Weight 1 created if weighted.
   * @param s source vertex
   * @param t target vertex
   * @return TotalNumberStubs if edge added, -1 if no edge added
   * @see #increaseEdgeWeight(int, int, double) for alternative
   */
      public int addEdgeWithTests(int s, int t){
                if (!multiEdge) return addEdgeUnique(s,t);
                addEdge(s,t);
                return TotalNumberStubs;

      }

//    /**
//     * Adds edge weighted edge exists.
//     * <br>If edge does not exist it is created otherwise nothing is changed.
//     * Takes the first edge with target <tt>t</tt> in the <tt>vertexList[s]</tt>.
//     * If undirected it does not check to see if any edges are listed with <tt>t</tt> as source to
//     * <tt>s</tt> as target.
//     * <br>Checks for self loops.
//     * <br> {@link #addEdgeUnique(int, int) } very similar
//     * @param s source vertex
//     * @param t target vertex
//     * @param dw increase weight by this amount
//   */
//    public void addEdgeNotMultiple(int s, int t, double dw) {
//        if (s==t && !selfLoops) return;
//        int e = getFirstEdgeGlobal(s, t);
//        if (e < 0) {
//            addEdge(s, t);
//        }
//    }
//

    /**
     * Creates a new edge but only if such an edge does not already exist.
     * <br>Uses local vertex edge index so works even is <code>vertexEdgeListOn</code> is false.
     * @param s source vertex
     * @param t target vertex
     * @return true (false) if at least one edge (no edges) from s to t exist(s)
     */
    public boolean edgeExists(int s, int t) {
        return ((getFirstEdgeLocal(s, t) < 0) ? false : true);
    }
 

// **************************************************************************
// removal routines
        

// ---------------------------------------------------------      
   /** 
    * Deep copies existing graph to new uninitialised graph .
    * keeping all vertices but only edges with non-zero weight
    * @param oldtg existing timgraph
    * @param keepedgelist array of edges to keep if true 
    * @param TNS number of Edges in new graph
    */    
  public int copySomeEdges(timgraph oldtg, int[] keepedgelist, int TNS)
  {    
   int TNV = oldtg.TotalNumberVertices;   
   int result =0;
   stubSourceList = new int[TNS];
   TotalNumberStubs=0;
   for (int e=0; e<oldtg.TotalNumberStubs;)
   { 
       if (keepedgelist[e]!=0) 
       {
         stubSourceList[TotalNumberStubs++] =oldtg.stubSourceList[e++];
         stubSourceList[TotalNumberStubs++] =oldtg.stubSourceList[e++];
       }
       else e+=2;
   }                        
   if (TNS!=TotalNumberStubs) {result=1;
                               System.out.println("*** Total Number Edges wrong in copySomeEdges");
                               }
   //Now reconstruct vertex lists from edges
   TotalNumberVertices=TNV;
   vertexList = new IntArrayList[TNV];
   //if (vertexEdgeListOn) vertexEdgeList = new IntArrayList[TNV];
   vertexEdgeListOn=false;
   for (int v=0; v<TNV; v++) vertexList[v] = new IntArrayList();
   int v1,v2;
   for (int e=0; e<TNS;)
   {
      v1=stubSourceList[e++];
      v2=stubSourceList[e++];
      vertexList[v1].add(v2);
      vertexList[v2].add(v1); 
   }
   return result;
  }//eo copySomeEdges

// ---------------------------------------------------------      
   /** 
    * Deep copies existing graph.
    * <p>Deep copies existing graph to new uninitialised graph
    * keeping all vertices, but only copies first TNS edges
    * noted in edgerandomlist
    * @param oldtg existing timgraph
    * @param TNS number of Edges in new graph
    * @param keeplabel of edges we keep in preference
    */    
  public int copyLabelledRandomEdges(timgraph oldtg, int TNS, int keeplabel)
  {    
   int TNV = oldtg.TotalNumberVertices;   
   int oldTNSo2 = oldtg.TotalNumberStubs/2;
   int result =0;
   int e;
   stubSourceList = new int[TNS];
   if (weightedEdges) edgeValuetList = new EdgeValue[TNS];
   TotalNumberStubs=0;
   int n;
   for (n=0; TotalNumberStubs<TNS; n++)
   { 
       if (n== oldTNSo2) break;
       e=oldtg.edgerandomlist[n]*2; 
       if (oldtg.edgeValuetList[e].label != keeplabel ) continue;
       edgeValuetList[TotalNumberStubs]= new EdgeValue(oldtg.edgeValuetList[e]);
       stubSourceList[TotalNumberStubs++] =oldtg.stubSourceList[e++];
       edgeValuetList[TotalNumberStubs]= new EdgeValue(oldtg.edgeValuetList[e]);
       stubSourceList[TotalNumberStubs++] =oldtg.stubSourceList[e];
       
   }                        
   if (TNS!=TotalNumberStubs) result=-1;
   // RUN OUT OF keeplabels
   for (n=0 ; TotalNumberStubs<TNS; n++)
   { 
       if (n== oldTNSo2) break;
       e=oldtg.edgerandomlist[n]*2; 
       if (oldtg.edgeValuetList[e].label == keeplabel ) continue;
       edgeValuetList[TotalNumberStubs]= new EdgeValue(oldtg.edgeValuetList[e]);
       stubSourceList[TotalNumberStubs++] =oldtg.stubSourceList[e++];
       edgeValuetList[TotalNumberStubs]= new EdgeValue(oldtg.edgeValuetList[e]);
       stubSourceList[TotalNumberStubs++] =oldtg.stubSourceList[e];
       
   }                        
   if (TNS!=TotalNumberStubs) result=-2;
   
   //Now reconstruct vertex lists from edges
   TotalNumberVertices=TNV;
   vertexList = new IntArrayList[TNV];
   for (int v=0; v<TNV; v++) vertexList[v] = new IntArrayList();
   vertexEdgeListOn=false;
   int v1,v2;
   for (e=0; e<TotalNumberStubs;)
   {
      v1=stubSourceList[e++];
      v2=stubSourceList[e++];
      vertexList[v1].add(v2);
      vertexList[v2].add(v1); 
   }
   return result;
  }//eo copySomeEdges

// ---------------------------------------------------------      
   /** 
    * Sets edgerandomlist.
    */    
  public void calcRandomEdgeOrder()  
  {
      int eb;
      int TNSo2 = TotalNumberStubs/2;
      if (edgerandomlist == null) edgerandomlist = new int[TNSo2];
      for (int e=0; e<TNSo2; e++) edgerandomlist[e]=e;
      for (int e=0; e<TNSo2; e++)
      {
          int e2=Rnd.nextInt(TNSo2);
          if (e2 != e) { 
                         eb=edgerandomlist[e2];
                         edgerandomlist[e2]=edgerandomlist[e];
                         edgerandomlist[e]=eb;
                         }
      }
      return; 
      
  }  

  // ---------------------------------------------------------      
   /** 
    * Sets edgerandomlist.
    */    
  public void calcReverseEdgeOrder()  
  {
      int e2,eb;
      int TNSo2 = TotalNumberStubs/2;
      for (int e=0; e<TNSo2/2; e++)
      {
          e2=TNSo2-1-e;
          eb=edgerandomlist[e2];
          edgerandomlist[e2]=edgerandomlist[e];
          edgerandomlist[e]=eb;
          
      }
      return; 
      
  }  



  // ...................................................................

/*
 * @(#)QSortAlgorithm.java  1.3   29 Feb 1996 James Gosling
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
  */

/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @version     @(#)QSortAlgorithm.java 1.3, 29 Feb 1996
 */

// Adapted to sort index array
    
    //public class QSortAlgorithm extends SortAlgorithm {
   /** This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    *   QuickSort(a, 0, a.length - 1);
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition
    * @param hi0     right boundary of array partition
    */

  
//   public void sort(int a[]) throws Exception
//   {
//      QuickSort(a, 0, a.length - 1);
//   }
//}end of sort
  
// ---------------------------------------------------------      
   /** 
    * Sets edgerandomlist with edges ordered by their edgeweight values
    * according to criteria (see compareWeights(...))
    * @param criteria as in compareWeights(criteria)
    */    
  public void calcRandomEdgeWeightOrder(int criteria)  
  {
      int TNSo2 = TotalNumberStubs/2;
      //set initial list to be random
      if (edgerandomlist == null) edgerandomlist = new int[TNSo2];
      for (int e=0; e<TNSo2; e++) edgerandomlist[e]=e;
      int eb,e2;
      for (int e=0; e<TNSo2; e++)
      {
          e2=Rnd.nextInt(TNSo2);
          if (e2 != e) { 
                         eb=edgerandomlist[e2];
                         edgerandomlist[e2]=edgerandomlist[e];
                         edgerandomlist[e]=eb;
                         }
      }
      // now sort by labels or weights as set by criteria
      if (criteria>-1) QuickSort(criteria, edgerandomlist, 0, TNSo2-1);
      return; 
  }

   /** 
    * Sorts integer array.
    *@param criteria used to select condition to apply to array as in compareWeights(criteria)
    *@param a returned as list of indices of edgeValuetList in ranked order.
    *@param lo0 the starting index
    *@param hi0 the maximum index (inclusive)
    */      
    private void QuickSort(int criteria, int a[], int lo0, int hi0)
   {
      int T;
      int lo = lo0;
      int hi = hi0;
      //int midindex;
      EdgeValue mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
          mid = edgeValuetList[a[( lo0 + hi0 ) / 2]*2];
//         mid = a[ ( lo0 + hi0 ) / 2 ];
//         midindex = ( lo0 + hi0 ) / 2 ;

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
            // while( ( lo < hi0 ) && ( a[lo] < mid ) ) 
            while( ( lo < hi0 ) && ( compareWeights(criteria, edgeValuetList[a[lo]*2] , mid ) ))
               ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
            //while( ( hi > lo0 ) && ( a[hi] > mid ) )
            while( ( hi > lo0 ) && ( compareWeights(criteria, mid, edgeValuetList[a[hi]*2]  ) ))
               --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            { // swap elements
               T = a[lo]; 
               a[lo] = a[hi];
               a[hi] = T;
               
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            QuickSort(criteria, a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            QuickSort(criteria, a, lo, hi0 );

      }
   }


// .....................................................     
   /** 
    * Compares weights according to criteria setting .
    * @param criteria are  0 [1] weight1> [<] weight2;  2 [3] label1> [<] label2
    * @param ew1 edgeweight 
    * @param ew2 edge weight
    */    
  public boolean compareWeights(int criteria, EdgeValue ew1, EdgeValue ew2)
  { 
      double value=-1;
      switch (criteria)
      {
          case 0: { value=ew1.weight-ew2.weight; break;}
          case 1: { value=ew2.weight-ew1.weight; break;}
          case 2: { value=ew1.label-ew2.label; break;}
          case 3: { value=ew2.label-ew1.label; break;}
          default: {System.out.println("*** ERROR in compareWeights, criteria "+criteria+"unknown");}
          
      }
      return (value>0 ? true : false );
      //if (value>0) return true;
      //if (value<0) return false;
      //return (Rnd.nextBoolean()? true:false );
  }
   
  
  
// **************************************************************************
// rewire routines
        
// ---------------------------------------------------------

  /**
   * This produces a randomised graph whose vertices have the same degree.
   * <p>If needed the vertexEdgeList is created.
   */
  public void randomiseGraph(){
      int success=-1;
      for (int s1=0; s1<TotalNumberStubs; s1++){
          success=-1;
          while(success<0) success=rewireEdgePair(s1,Rnd.nextInt(TotalNumberStubs));
      }
      if (this.vertexEdgeListOn) createVertexGlobalEdgeList();
  }
   /**
    * Switches edges - Bak+Sneppen rewiring.
    * <p>If undirected will assume input stubs point to 'source' vertices
    * in stubSourceList but if directed will identify correct source vertex
    * of edge.  Routine then either switches sources or targets.  Maintains
    * most lists, but NOT vertexEdgeList.  Leaves weights or labels
    * of edges unchanged. Does not respect any bipartite structure.
    * <p>Routine has s and t for source and target in names
    * @param e1 first stub number
    * @param e2 second stub number
    *@return success number, negative if failure
    */    
  public int rewireEdgePair(int e1, int e2)  
  {    // technically the e1, e2, etc are stub numbers
      if ((e1>=TotalNumberStubs) ||(e2>=TotalNumberStubs) ) return(-1);
      if ((e1<0) ||(e2<0) ) return(-2);
      int edge1 = (e1 >> 1); // these are edge numbers
      int edge2 = (e2 >> 1);
      if (edge1==edge2) return (-3);
      int e1s = e1;
      int e2s = e2;
      if (directedGraph) {
            e1s = edge1 << 1 ; // source stub numbers are even in directed graph
            e2s = edge2 << 1 ;
      }
      //if (e1s==e2s) return(-3);
      int e1t = e1s ^ 1; // XOR of least sig. bit to flip from even to odd and vice versa
      int e2t = e2s ^ 1; // these are the target stubs
      int v1s = stubSourceList[e1s];
      int v1t = stubSourceList[e1t];
      int v2s = stubSourceList[e2s];
      int v2t = stubSourceList[e2t];
      if (infoLevel>1) System.out.println(v1s+SEP+v1t+SEP+v2s+SEP+v2t);
      int vn1t = vertexList[v1s].indexOfFromTo(v1t,0,vertexList[v1s].size()-1); // use .indexOf(vlt)
      int vn1s = vertexList[v1t].indexOfFromTo(v1s,0,vertexList[v1t].size()-1);
      int vn2t = vertexList[v2s].indexOfFromTo(v2t,0,vertexList[v2s].size()-1);
      int vn2s = vertexList[v2t].indexOfFromTo(v2s,0,vertexList[v2t].size()-1);
      if (infoLevel>1) {System.out.println(vn1s+SEP+vn1t+SEP+vn2s+SEP+vn2t);
      System.out.println(vertexList[v1s]);
      System.out.println(vertexList[v1t]);
      System.out.println(vertexList[v2s]);
      System.out.println(vertexList[v2t]);
      }
      boolean choice = Rnd.nextBoolean();
      int success=0;
      if (choice)
      {// switch sources
          if ((v1s==v2t) || (v2s==v1t)) return(-3);
          stubSourceList[e1s]=v2s;
          stubSourceList[e2s]=v1s;
//          stubSourceList[e1t]=v1t;
//          stubSourceList[e2t]=v2t;
          vertexList[v1s].set(vn1t,v2t);
          vertexList[v1t].set(vn1s,v2s);
          vertexList[v2s].set(vn2t,v1t);
          vertexList[v2t].set(vn2s,v1s);
          success=3;
      }
      else 
      {// make source 2 the target of 1
          if ((v1s==v2s) || (v2t==v1t)) return(-4);
//          stubSourceList[e1s]=v1s;
          stubSourceList[e2s]=v1t;
          stubSourceList[e1t]=v2s;
//          stubSourceList[e2t]=v2t;
          vertexList[v1s].set(vn1t,v2s);
          vertexList[v1t].set(vn1s,v2t);
          vertexList[v2s].set(vn2t,v1s);
          vertexList[v2t].set(vn2s,v1t);
          success=4;
      }
      return(success);
  }
  
  
// ---------------------------------------------------------      
   /** 
    * Rewires target of edge.
    * @param e edge number giving source end of pair, is fixed
    * @param vnew new vertex number for new target
    */    
  public int rewireEdgeTarget(int e,  int vnew)  
  {    
      System.out.println(" e, vnew "+e +" : " + vnew);
      if ((e>=TotalNumberStubs) || (e<0)) return(-1);
      int efixed = e ; 
      int emove = efixed ^ 1; // Xor flip last digit (even <-> odd)
//      if (removetarget) { int etemp = efixed; efixed = emove; emove=etemp;}; 
      int vmove = stubSourceList[emove];
      int vfixed = stubSourceList[efixed];
      //int test =vertexList[0].get(29);
      vertexList[vmove].delete(vfixed); // remove vfixed from vmove's list
      vertexList[vnew].add(vfixed);  // add fixed to vnew's list
      int vminfixed = vertexList[vfixed].indexOf(vmove);
      vertexList[vfixed].set(vminfixed,vnew); // replace vfixed by vnew in vfixed's list
//      stubSourceList[efixed]=vfixed;
      stubSourceList[emove]=vnew;
      System.out.println("efixed :emove = vfixed - vmove -> vnew  |  " + efixed + ":" + emove + " = " + vfixed + " - "+vmove + " -> "+vnew);
//      if (activeVertices)
//      {
//         if (vertexList[vmove].size()==0) activevertexList.remove(vmove); 
//      }
      return(0);
  }
    
  
// **************************************************************************
// calc routines
        
// ---------------------------------------------------------      
   /** 
    * Calculates average in result if n samples are given total of v_i .
    *@param v sum of values
    *@param n number of terms in sums
    *@return error in ONE result, v/n
   */    
  public double calcAverage(int v, int n)  
  { return ((double) v)/((double) n);   }

// ---------------------------------------------------------      
   /** 
    * Calculates average in result if n samples are given total of v_i .
    *@param v sum of values
    *@param n number of terms in sums
    *@return error in ONE result, v/n
   */    
  public double calcAverage(long v, long n)  
  { return ((double) v)/((double) n); }

// ---------------------------------------------------------      
   /** 
    * Calculates error in result if n samples are given total of v_i and v_i^2.
    *@param v sum of values
    *@param v2 sum of values squared
    *@param n number of terms in sums
    *@return error in ONE result, v/n
   */    
  public double calcError(long v, long v2, long n)  
  { return calcError((double) v, (double) v2, (double) n);  }
 //---------------------------------------------------------      
   /** 
    * Calculates error in result if n samples are given total of v_i and v_i^2.
    *@param v sum of values
    *@param v2 sum of values squared
    *@param n number of terms in sums
    *@return error in ONE result, v/n
   */    
  public double calcError(int v, int v2, int n)  
  { return calcError((double) v, (double) v2, (double) n);  }

// ---------------------------------------------------------      
   /** 
    * Calculates error in result if n samples are given total of v_i and v_i^2 .
    *@param v sum of values
    *@param v2 sum of values squared
    *@param n number of terms in sums
    *@return error in ONE result, v/n
   */    
  public double calcError(double v, double v2, double n)  
  { 
      if (n>1) 
          return calcSigma(v,v2,n)/Math.sqrt( (n-1) );
      else return 0.0;
  }

  // ---------------------------------------------------------      
   /** 
    * Calculates error in result if n samples are given total of v_i and v_i^2 .
    *@param v sum of values
    *@param v2 sum of values squared
    *@param n number of terms in sums
    *@return error in ONE result, v/n
   */    
  public double calcSigma(double v, double v2, double n)  
  { return Math.sqrt( ( v2 - (v*v/n) ) / n  );  }

// ---------------------------------------------------------      
   /** 
     * Calculates number of vertices directly.
    *@return number of vertices
     */    
  public int calcNumberVertices()  {
      int n;
      for(n=0; n<vertexList.length; n++){
          if (vertexList[n]==null) break;
      }
      if (TotalNumberVertices!=n) 
      {
          System.out.println("*** Error in calcNumberVertices, number of vertices inconsistent");
          System.out.println("     TNV="+TotalNumberVertices+", calc = "+n);
      }
      return n;
  }       

  
// ---------------------------------------------------------      
   /** 
    * Calculates number of edges directly.
    *@return number of edges
    */    
  public int calcNumberEdges()  {
      int neout=0;
      int nein=0;
      for(int v=0; v<TotalNumberVertices; v++){
          if (vertexList[v]!=null) neout+=vertexList[v].size();
          if (directedGraph && vertexSourceList[v]!=null) nein+=vertexSourceList[v].size();
      }
      if (TotalNumberStubs!=nein+neout) 
      {
              System.out.println("*** Error in calcNumberEdges, number of edges inconsistent");
              System.out.println("     TNS="+TotalNumberStubs+", calc out = "+neout+", in = "+nein);
      }
      return nein+neout;
  }       
 
  
// -----------------------------------------------------------------------       
    //private void calcDistanceNext(int distance, int maxDistance, int componentNumber, IntArrayList olddistancevlist, IntArrayList componentVertexList)  
  //* If <tt>componentVertexList</tt> is not null then it is updated with a complete list of vertices visted.
  //* @param componentVertexList is a complete list of all vertices visited  i.e.
 
  /**
     * Finds all vertices given distance away from initial vertex.
     * <br>This is recursive and assumes that vertices <tt>(distance-1)</tt>) have all ready
     * been visited.  A list of neighbours to try is given in olddistancevlist.
     * Updates global <tt>vertexdistance[v]</tt> from -9999999 if not visited, 
     * to -1 if to be visited next round, to distance if visiting in this round.
     * Finds next set of vertices to be studied next,
     * i.e. those v at <tt>(distance+1)</tt>, and marks them as -1 in vertexdistance[v]
     * and adds them to the <tt>distancevlist</tt>, those to be visited next.
     * @param distance is the present distance radius
     * @param maxDistance will stop once this distance is reached.
     * @param componentNumber is the number of the component
     * @param olddistancevlist is the list of vertices to be visited this round.
     */
    private void calcDistanceNext(int distance, int maxDistance, int componentNumber, IntArrayList olddistancevlist)  
    {
        IntArrayList distancevlist = new IntArrayList(100); // ? 1000?
        int count=0;
        int degree=0;
        long totdistdegree=0;
        for (int vod=0; vod <olddistancevlist.size(); vod++)
        {
            int v=olddistancevlist.get(vod);
            if (vertexdistance[v] >-1) break;
            vertexdistance[v]=distance;
            vertexComponent[v]=componentNumber;
            //if (componentVertexList!=null) componentVertexList.add(v);
            count++;
            degree=vertexList[v].size();
            totdistdegree+=degree;
            for (int n=0; n<degree; n++)
            {
                int vn = vertexList[v].get(n);
                if (vertexdistance[vn]<-1)
                {
                    vertexdistance[vn]=-1; // mark it as in list for next distance
                    distancevlist.add(vn); // add it to list for next distance
                }
            }//eo for n
        }//eo for vod
        distancedist.add(count);
//        degreedist.add(totdistdegree/((double)count));
        componentsize+=count;
        if ((distancevlist.size()>0) && (distance<maxDistance)) calcDistanceNext(distance+1, maxDistance, componentNumber,distancevlist);
    }
// -----------------------------------------------------------------------       
    /**
     * Finds all vertices within certain distance of given vertex.
     * <br>Looks only upto <tt>maxDistance</tt> away so set this to be 
     * <tt>TotalNumberVertices</tt> or more if want whole component.
     * Sets global parameters of distance and diameter averages and sigma.  
     * Uses global <code>vertexdistance[v] = -9999999</code> if not visited, -1 if about to
     * be visited, d=(distance from vertex)>=0 if distance found already.
     * All vertices in this component have non-negative <code>vertexdistance[v]</code>
     * All the vertices in this subgraph have component number 1 
     * but the component numbers of others have the java int default value of 0.
     * <p>Ignores edge weights.  Only uses topology.
     * @param vertex is the starting vertex
     * @param maxDistance will stop once this distance is reached.
     */
     private void calcDistanceOne(int vertex,  int maxDistance)  
    {   
        vertexdistance = new int[TotalNumberVertices];
        vertexComponent = new int[TotalNumberVertices];
        distancedist = new IntArrayList();
        IntArrayList distancevlist = new IntArrayList(100);
        distancevlist.add(vertex);
        int componentNumber = 1;
        componentsize = 0;
        for (int u=0; u<TotalNumberVertices; u++) vertexdistance[u] = -999999;
        vertexdistance[vertex]=-1;
        calcDistanceNext(0, maxDistance, componentNumber, distancevlist);
//        if (componentsize != TotalNumberVertices) 
//            System.out.println("!!! Warning cluster has "+componentsize+" out of "+TotalNumberVertices+" vertices");
        diameter = distancedist.size()-1;
        int dt =0;
        int d2t =0;
        int count =0;
        for (int d=1; d<distancedist.size(); d++) 
        {
            int n = distancedist.get(d);
            count+=n;
            dt += n*d;
            d2t += n*d*d;
        }
        if (count>0)
        {
            distanceaverage = calcAverage(dt,count);
            distanceerror = calcError(dt,d2t,count);
            distancesigma = calcSigma(dt,d2t,count);
        }
        else
        {
            distanceaverage = 0;
            distanceerror = 0;
            distancesigma = 0;
        }
            
    }


// -----------------------------------------------------------------------       
    //private void calcDistanceNextComponent(int vertex,  int maxDistance, IntArrayList componentVertexList)  
    //* If <tt>componentVertexList</tt> is not null then it is updated 
    //* with a complete list of vertices visited.

     /**
     * Looks at vertices connected to one vertex.
     * <br>Looks only at vertices upto <tt>maxDistance</tt> away so set this to be 
     * <tt>TotalNumberVertices</tt> or more if want whole component.
     * Sets global parameters of distance and diameter averages and sigma.  
     * Uses global vertexdistance[v] = -9999999 if not visited, -1 if about to
     * be visited, n>=0 if distance found already.
     * Also sets the maximum component size in componentSizeMax.
     * @param vertex is the starting vertex
     * @param maxDistance will stop once this distance is reached.
     */
     
    private void calcDistanceNextComponent(int vertex,  int maxDistance)  
    {   
        componentSource.add(vertex);
        int componentNumber = componentSource.size()-1;
        distancedist = new IntArrayList();
        IntArrayList distancevlist = new IntArrayList(100);
        distancevlist.add(vertex);
        componentsize = 0;
        if (vertexdistance[vertex]>-99) 
            System.out.println("*** Error in calcDistanceNextComponent ");
        vertexdistance[vertex]=-1;
        //calcDistanceNext(0, maxDistance, componentNumber, distancevlist, componentVertexList);
        calcDistanceNext(0, maxDistance, componentNumber, distancevlist);
        componentSize.add(componentsize);
        if (componentSizeMax<componentsize) 
        {
            componentSizeMax=componentsize; 
            componentSourceMax = vertex;
            componentDiameterMax = distancedist.size()-1;
            componentGCCIndex = componentNumber;
        }
        componentDist.add(distancedist.size()-1);
        if (infoLevel>1) System.out.println("Finished looking at component "+componentSize.size()+" around vertex "+vertex);

    }


    
// -----------------------------------------------------------------------       
    /**
     * Finds all components and find their statistics.
     * <p>Sets global parameters of distance and diameter averages and sigma.  
     * Uses global vertexdistance[v] = -9999999 if not visited, -1 if about to
     * be visited, n>=0 if distance found already.
     */
    public void calcComponents()
    {
        IntArrayList componentVertexList; // == null, do not find the list of vertices in a component
        vertexdistance = new int[TotalNumberVertices];
        vertexComponent = new int[TotalNumberVertices];
        distancedist = new IntArrayList();
        componentSize = new IntArrayList();
        componentSource = new IntArrayList();
        componentDist = new IntArrayList();
        IntArrayList distancevlist = new IntArrayList(100);
        componentSizeMax=-1;
        componentSourceMax =-1;
        componentGCCDist = -1;
        componentGCCIndex=-1;
        componentSingleNumber=-1;
        for (int u=0; u<TotalNumberVertices; u++) 
        {
            vertexdistance[u] = -999999;
            vertexComponent[u]= -999999;
        }
        for (int u=0; u<TotalNumberVertices; u++) 
        {
           if (vertexComponent[u]<0)  calcDistanceNextComponent(u, TotalNumberVertices);   
               //calcDistanceNextComponent(u, TotalNumberVertices, componentVertexList);   
        }
// check found all vertices and find average size
        int count=0;
        componentSingleNumber=0;
        for (int c=0; c<componentSize.size(); c++) { 
            count+=componentSize.get(c);
            if (componentSize.get(c)==1) componentSingleNumber++;
        } 
        if (count != TotalNumberVertices) 
            System.out.println("!!! Warning components have total "+count+" out of "+TotalNumberVertices+" vertices");
       
        // Calculate average distance in GCC
        count=0;
        int dist =0;
        for (int u=0; u<TotalNumberVertices; u++) if (vertexComponent[u]==componentGCCIndex) { dist+= vertexdistance[u]; count++;}
        if (count != componentSizeMax) 
            System.out.println("!!! Warning GCC component has total "+count+" vertices but recorded as "+componentSizeMax+" vertices");
        if (componentSizeMax>0) componentGCCDist = dist/componentSizeMax;
        
    }

   /**
     * Finds all vertices connected to given vertex within given distance.
     * <p>Only one component is found and then thats only for vertices with 
     * <tt>distance</tt> of given vertex. The GCC parameters are set to be for this subgraph.
     * Sets global parameters of distance and diameter averages and sigma.  
     * Uses global vertexdistance[v] = -9999999 if not visited, -1 if about to
     * be visited, n>=0 if distance found already.
     * @param vertex is the starting vertex
     * @param maxDistance will collect all vertices with this distance of vertex.
    * @deprecated use calcDistanceOne
     */
    //public void calcRing(int vertex,  int maxDistance, IntArrayList componentVertexList)
    public void calcRing(int vertex,  int maxDistance)
    {
        vertexdistance = new int[TotalNumberVertices];
        vertexComponent = new int[TotalNumberVertices];
        distancedist = new IntArrayList();
        componentSize = new IntArrayList();
        componentSource = new IntArrayList();
        componentDist = new IntArrayList();
        IntArrayList distancevlist = new IntArrayList(100);
        componentSizeMax=-1;
        componentSourceMax =-1;
        componentGCCDist = -1;
        componentGCCIndex=-1;
        componentSingleNumber=-1;
        for (int u=0; u<TotalNumberVertices; u++) 
        {
            vertexdistance[u] = -999999;
            vertexComponent[u]= -999999;
        }
        //calcDistanceNextComponent(vertex, maxDistance, componentVertexList);   
        calcDistanceNextComponent(vertex, maxDistance);   
        
// check found all vertices and find average size
        
        // Calculate average distance in GCC
        int count=0;
        int dist =0;
        for (int u=0; u<TotalNumberVertices; u++) if (vertexComponent[u]==componentGCCIndex) { dist+= vertexdistance[u]; count++;}
        if (count != componentSizeMax) 
            System.out.println("!!! Warning GCC component has total "+count+" vertices but recorded as "+componentSizeMax+" vertices");
        if (componentSizeMax>0) componentGCCDist = dist/componentSizeMax;
        
    }

    
    /**
     * Makes a TreeSet of vertices in GCC.
     * <p>Useful for the subgraph projection.
     * Assumes components already found and so <tt>componentGCCIndex</tt> 
     * already set.
     * @return a set of the indices of vertices in the GCC.
     */
    public TreeSet<Integer> getGCC(){
        return getComponent(componentGCCIndex);
    }
    
    
    /**
     * Makes a TreeSet of vertices in a component.
     * @param componentNumber will collect all vertices in this component
     * @return a set of the indices of vertices in the GCC.
     */
    public TreeSet<Integer> getComponent(int componentNumber){
        TreeSet<Integer> component = new TreeSet();
        for (int v=0; v<TotalNumberVertices; v++)
            if (vertexComponent[v]==componentNumber) component.add(v);
        return component;
    }

    /**
     * Returns component label of vertex.
     * @param v vertex whose compenet label is required
     * @return component label of vertex.
     */
    public int getVertexComponentLabel(int v){
        return vertexComponent[v];
    }

    /**
     * Makes a TreeSet of vertices in a ring, i.e. distance <tt>maxDistance</tt> from <tt>vertex</tt>.
     * <p>Useful for the subgraph projection.
     * Uses non negative <tt>vertexdistance</tt> to indicate membership of component.
     * @param vertex is the starting vertex
     * @param maxDistance will collect all vertices with this distance of vertex.
     * @return a set of the indices of vertices in the GCC.
     */
    public TreeSet<Integer> getRing(int vertex,  int maxDistance){
        calcDistanceOne(vertex,  maxDistance);
        TreeSet<Integer> ring = new TreeSet();
        for (int v=0; v<TotalNumberVertices; v++) 
            if (this.vertexdistance[v]>=0) ring.add(v);
        return ring;
    }
    
    /**
     * Makes a TreeSet of vertices incident with edges of given labels
     * <br>Graph must have edge weights.
     * @param labelSet set of integers specifiying edges with labels to be found. 
     * @return a set of the indices of vertices in the sub graph of defined by edges of given labels.
     */
    public TreeSet<Integer> getEdgeSubGraph(Set<Integer> labelSet){
        if (!weightedEdges) throw new IllegalArgumentException("*** getEdgeSubGraph needed edges to have labels");
        TreeSet<Integer> vertexSubSet = new TreeSet();
        for (int e=0; e<TotalNumberStubs; e+=2) 
            if (labelSet.contains(edgeValuetList[e].label) ) {
                vertexSubSet.add(stubSourceList[e]);
                vertexSubSet.add(stubSourceList[e+1]);
            } 
        return  vertexSubSet;
    }
    
// *********************************************************
    /**
     * Does Dijkstra.
     * Updates distanceFromV global
     * Sets distance[i][j] to shortest distance from i to j
     * and DijkstraMaxDist is the longest path between any two sites,
     * and it equals MAXDISTANCE if disconnected
     */
    //     *@param metricNumber = 0 plain distance, =1 distance divided by potential

    public void doDijkstra() {
        int metricNumber= 0; //getMetricNumber();
        double degree=-1;
        double totdistdegree=0;
                        
        // look at distances from vertex v
        DijkstraMaxDist=0;
        distance = new double[TotalNumberVertices][TotalNumberVertices];
            for (int v=0; v<TotalNumberVertices; v++) {
                int mdv=v;
                double mindistance =MAXDISTANCE;
                double newdist,eee;
                boolean [] notVisited = new boolean[TotalNumberVertices];
                for (int i=0; i<TotalNumberVertices; i++) 
                {
                    distance[v][i]=MAXDISTANCE; 
                    notVisited[i]=true;
                }
                distance[v][v]=0.0;
                for (int n=0; n<TotalNumberVertices; n++) 
                {   // first find mdv, the unvisited vertex with smallest distance from v
                    mindistance =MAXDISTANCE;
                    for (int j=0; j<TotalNumberVertices; j++)
                        if (notVisited[j] && (distance[v][j]<mindistance)) 
                        {
                         mindistance=distance[v][j]; mdv = j;
                        }
                        if (mindistance==MAXDISTANCE) break;    // must be finished
                        // visit mdv (fix its distance)  and update distnace from v to j the neighbours of mdv
                        notVisited[mdv]=false;
                        degree=vertexList[mdv].size();
                        totdistdegree+=degree;
                        for (int e=0; e<degree; e++)
                        {
                            int vn = vertexList[mdv].get(e);
                            try{ // this is the metric
                               
                                newdist = MAXDISTANCE*1.0001;
                                switch (metricNumber) 
                                {
                                    case 0:
                                    default:
                                        newdist = mindistance+ 0;
                                }
                                if (distance[v][vn]>newdist) distance[v][vn]=newdist;
                            } finally{}
                        }//eo for v                       
                }//eo for n
                if (DijkstraMaxDist<mindistance) DijkstraMaxDist=mindistance;
            } // eo for v
                                                        
    }

    
    
  // -----------------------------------------------------------------------       
    /**
     * Finds the ring parameters for componet source s.
     * Sets global parameters of distance and diameter averages and sigma.  
     * Uses global vertexdistance[v] = -9999999 if not visited, -1 if about to
     * be visited, n>=0 if distance found already.
     * @param sourcevertex source vertex
     */
    void calcRingParameters(int sourcevertex)  
    {   
        vertexdistance = new int[TotalNumberVertices];
        vertexComponent = new int[TotalNumberVertices];
        distancedist = new IntArrayList();
        componentSize = new IntArrayList();
        componentSource = new IntArrayList();
        componentDist = new IntArrayList();
        IntArrayList distancevlist = new IntArrayList(100);
        for (int u=0; u<TotalNumberVertices; u++) 
        {
            vertexdistance[u] = -999999;
            vertexComponent[u]= -999999;
        }

        calcDistanceNextComponent(sourcevertex,TotalNumberVertices+1);   
        //diameter = distancedist.size()-1;
        int udegree,udist;
        double nd;
        ringdegree = new DoubleArrayList();
        for (int u=0; u<TotalNumberVertices; u++) 
        {
           if (vertexComponent[u]<0) continue;
           udist=vertexdistance[u];
           udegree=vertexList[u].size();
           while (ringdegree.size()<=udist) ringdegree.add(0);
           nd=ringdegree.get(udist);
           ringdegree.set(udist,nd+udegree);
        }    
        for (int rd=0; rd<ringdegree.size(); rd++)
        {
            ringdegree.set(rd,ringdegree.get(rd)/distancedist.get(rd));
        }
        
    }
    
  // -----------------------------------------------------------------------       
    /**
     * Updates the ring statistics.
     * @param vertexdist vertex distance
     * @param vertexdegree vertex degree
     */
    void updateRingStats(int vertexdist, int vertexdegree)  
    {
        while (ringdegree.size()<vertexdist) ringdegree.add(0);
        ringdegree.get(vertexdist);
    }
 
    
// -----------------------------------------------------------------------       
    /**
     * Calculates total distance distributions and parameters.
     *@param fracerror fraction error to aim for, if >1 then systematic sample of all vertices
     *@param minimumsamples minimum number of samples to use
     *@param maximumsamples maximum number of samples to use
     */
    public void calcDistanceSample(double fracerror, int minimumsamples,  int maximumsamples)  
    {
        boolean doall=false;
        int minsamples = minimumsamples;  
        int maxsamples = maximumsamples;
        if (fracerror>1) 
        { 
            doall=true; 
            maxsamples = TotalNumberVertices;
            minsamples = TotalNumberVertices;
        }
        totdistancedist = new DoubleArrayList(100); 
        totdistance2dist = new DoubleArrayList(100); 
        nsamples = 0;
        int totdiam =0;
        int totdiam2 = 0;
        totdistanceaverage =0.0;
        totdistanceerror =0.0;
        totdistancesigma =0.0;
        diameteraverage= 0.0;
        diametersigma = 0.0;
        diametererror = 0.0;
        diametermin = 9999999;
        DoubleArrayList avdistancedist = new DoubleArrayList();
        IntArrayList diameterdist = new IntArrayList();
        double onesavdt =0.0; // one sample av dist total
        double onesavd2t =0.0; // one sample av dist^2 total
        do 
        {
            nsamples++;
            if (doall) calcDistanceOne(nsamples-1,TotalNumberVertices+1);
            else 
            {
                if ((nsamples==1) || (componentSourceMax<0))
                    calcDistanceOne(Rnd.nextInt(TotalNumberVertices),TotalNumberVertices+1);
                else calcDistanceOne(componentSourceMax,TotalNumberVertices+1);
            }
            avdistancedist.add(distanceaverage);
            onesavdt += distanceaverage;
            onesavd2t += distanceaverage * distanceaverage;
// now update total distance distribution            
            double n = 0;
            double n2 =0;
            int dmax = distancedist.size();
            int dmaxb = totdistancedist.size();
            if (dmaxb<dmax) dmax=dmaxb;
            for (int d=0; d<dmax; d++) 
            {
                    n = distancedist.get(d);
                    n2 =n*n;
                    n += totdistancedist.get(d);
                    totdistancedist.set(d,n);
                    n2 += totdistance2dist.get(d);
                    totdistance2dist.set(d,n2);
            }
            for (int d=dmax; d<distancedist.size(); d++) 
            {
                n = distancedist.get(d);
                n2 = n*n;
                totdistancedist.add(n);
                totdistance2dist.add(n2);
                }
// calculate average distances from total
            double dt =0;
            double d2t =0;
            double count =0;
            for (int d=1; d<totdistancedist.size(); d++) 
            {
                n = totdistancedist.get(d);
                count+=n;
                dt += n*d;
                d2t += n*d*d;
            }
            totdistanceaverage = dt/count; 
            totdistanceerror =calcError(dt,d2t,count);
            totdistancesigma =calcSigma(dt,d2t,count);
//            System.out.println(nsamples+SEP+dt+SEP+d2t+SEP+count+SEP+totdistanceaverage+SEP+totdistanceerror+SEP+totdistancesigma);
            totdiam += diameter;
            totdiam2 += diameter*diameter;
            diameterdist.add(diameter);
            if (diametermin>diameter) diametermin=diameter;
            diameteraverage = calcAverage(totdiam , nsamples);
            diametererror = calcError(totdiam, totdiam2, nsamples);
            diametersigma = calcSigma(totdiam, totdiam2, nsamples);
//            System.out.println("     "+SEP+totdiam+SEP+totdiam2+SEP+diameteraverage+SEP+diametererror+SEP+diametersigma);            
            if (nsamples>=maxsamples) break;
        }while (    (totdistanceerror>totdistanceaverage*fracerror ) 
                 || (nsamples<minsamples) );
// Calculate average over samples of average distance
        onesdistanceav = onesavdt/nsamples;
        onesdistanceerror = calcError(onesavdt,onesavd2t,(double) nsamples);        
        onesdistancesigma = calcSigma(onesavdt,onesavd2t,(double) nsamples);        
        // update total diameter stats
        diametermax = totdistancedist.size()-1;
        }
// -----------------------------------------------------------------------       
    /**
     * Calculates Cluster Coefficient of one vertex.
     *@param v vertex of interest
     *@return cluster coefficient or -1.0 if not defined
     *@deprecated use {@link #calcCCOne(int) }
     */
    double calcCCOneOld(int v)  
    {   
        int v1,v2,s1,s2,vs,vd,ss;
        int nedgepairs =0;
        int ntriangles = 0;
        if ((v<0) || (v>=TotalNumberVertices))
        {
            System.out.println("*** Error in calcCCOne vertex "+v+" invalid with "+TotalNumberVertices+" total vertices");
            return -2.0;
        }
        int vlsize =vertexList[v].size();
        if (vlsize<2) return -1.0;
        for (int e1 =0; e1<vlsize -1; e1++)
        {
            v1 = vertexList[v].get(e1);
            s1 = vertexList[v1].size();
            
            for (int e2 =e1+1; e2<vlsize; e2++)
            {
               nedgepairs++;
               v2 = vertexList[v].get(e2);
               s2 = vertexList[v2].size();
               if (s1<s2) { vs=v1; vd=v2; ss=s1; }
               else { vs=v2; vd=v1; ss=s2;}
                for (int e3=0; e3<ss; e3++)
                {
                   if (vertexList[vs].get(e3) ==vd) { ntriangles++; break;}
                } // eo e3
                
            }// e2   
        }// e1
        return ((double) ntriangles)/((double) nedgepairs);
    }// eo calcCCOne
        
// -----------------------------------------------------------------------       
    /**
     * Calculates Cluster Coefficient of one vertex using directed triangles.
     * <p>Exception if vertex out of range.
     *@param v vertex of interest.
     *@return cluster coefficient, -1.0 or -2.0 if not defined,
     */
    double calcCCOne(int v)
    {
        int v1,v2,s1;
        int nedgepairs =0;
        int ntriangles = 0;
        if ((v<0) || (v>=TotalNumberVertices))
        {
            throw new RuntimeException("*** Error in calcCCOne vertex "+v+" invalid with "+TotalNumberVertices+" total vertices");
            //return -2.0;
        }
        int vlsize =vertexList[v].size();
        if (vlsize<2) return -1.0;
        for (int e1 =0; e1<vlsize -1; e1++)
        {
            v1 = vertexList[v].get(e1);
            s1 = vertexList[v1].size();
            for (int e2 =e1+1; e2<vlsize; e2++)
            {
               nedgepairs++;
               v2 = vertexList[v].get(e2);
               for (int e3=0; e3<s1; e3++)
                   if (vertexList[v1].get(e3) ==v2) ntriangles++;
            }// e2
        }// e1
        return ((double) ntriangles)/((double) nedgepairs);
    }// eo calcCCOne
    /**
     * Calculates number of triangles containing given vertex.
     * <p>The two edges at vertex v must be distinct.
     * Multiedges will cause interesting results.
     * If multiedges exist then self-loops will contribute to number of triangles.
     * <p>Weights ignored, but directions are not so directed graphs will give
     * unspecified results.
     * <p>Exception if vertex out of range.
     *@param v vertex of interest.
     *@return number of triangles
     */
    int calcTriangles(int v)
    {
        int v1,v2,s1;
        int nedgepairs =0;
        int ntriangles = 0;
        if ((v<0) || (v>=TotalNumberVertices))
        {
            throw new RuntimeException("*** Error in calcTriangles vertex "+v+" invalid with "+TotalNumberVertices+" total vertices");
            //return -2.0;
        }
        int vlsize =vertexList[v].size();
        if (vlsize<2) return 0;
        for (int e1 =0; e1<vlsize -1; e1++)
        {
            v1 = vertexList[v].get(e1);
            s1 = vertexList[v1].size();
            for (int e2 =e1+1; e2<vlsize; e2++)
            {
               //nedgepairs++;
               v2 = vertexList[v].get(e2);
               for (int e3=0; e3<s1; e3++)
                   if (vertexList[v1].get(e3) ==v2) ntriangles++;
            }// e2
        }// e1
        return ntriangles;
    }// eo calcCCOne

    /**
     * Calculates number of triangles and squares containing given vertex.
     * <p>The two edges at vertex v must be distinct.
     * Multiedges will cause interesting results.
     * If multiedges exist then self-loops will contribute to number of triangles.
     * <p>Squares are counted only if the input vertex v and the opposite
     * vertex in the square, v3, are distinct.  Again multiedges will give strange
     * results especially if self-loops are present.
     * <p>Weights ignored, but directions are not so directed graphs will give
     * unspecified results.
     * <p>Exception if vertex out of range.
     *
     *@param v vertex of interest.
     *@return two dimensional array, 0=no. triangles, 1= no.squares
     */
    int [] calcTrianglesSquares(int v)
    {
        int [] result = {0,0};
        int v1,v2,v3,s1;
        v3=-1;
//        int ntriangles = 0;
//        int nsquares = 0;
        if ((v<0) || (v>=TotalNumberVertices))
        {
            throw new RuntimeException("*** Error in calcSquares vertex "+v+" invalid with "+TotalNumberVertices+" total vertices");
            //return -2.0;
        }
        int vlsize =vertexList[v].size();
        if (vlsize<2) return result;
        for (int e1 =0; e1<vlsize -1; e1++)
        {
            v1 = vertexList[v].get(e1);
            s1 = vertexList[v1].size();
            IntArrayList v1nn = vertexList[v1];
            for (int e2 =e1+1; e2<vlsize; e2++)
            {
               //nedgepairs++;
               v2 = vertexList[v].get(e2);
               for (int e3=0; e3<s1; e3++){
                   v3=vertexList[v1].get(e3);
                   if (v3 ==v2) result[0]++;
                   else if ((v3 != v)  && vertexList[v2].contains(v3)) result[1]++;
               } // eo for e3
            }// e2
        }// e1
        return result;
    }// eo calcCCOne


// -----------------------------------------------------------------------       
    /**
     * Calculates simplest Clustering Coefficient from a sample.
     *@param fracerror fraction error to aim for, if >1 then systematic sample of all vertices
     *@param minimumsamples minimum number of samples
     *@param maximumsamples maximum number of samples
     */
    public void calcCCSample(double fracerror, int minimumsamples,  int maximumsamples)  
    {
        boolean doall=false;
        int minsamples = minimumsamples;  
        int maxsamples = maximumsamples;
        if (fracerror>=1.0) 
        { 
            doall=true; 
            maxsamples = TotalNumberVertices;
            minsamples = TotalNumberVertices;
        }
        double CC;
        //CCdist = new DoubleArrayList(100); 
        int nattempts = 0;
        CCnsamples = 0;
        CCaverage =0.0;
        CCerror =0.0;
        CCsigma =0.0;
        double CCt =0;
        double CC2t =0;
            
        do 
        {   
            if (doall) CC=calcCCOne(nattempts);
            else CC=calcCCOne(Rnd.nextInt(TotalNumberVertices));
            nattempts++;
            if (CC<0) continue;
            CCnsamples++;
            //CCdist.add(CC);
            CCt += CC;
            CC2t += CC*CC;
            CCaverage = CCt/CCnsamples; 
            CCerror =calcError(CCt,CC2t,CCnsamples);
            CCsigma =calcSigma(CCt,CC2t,CCnsamples);
//            System.out.println(CCnsamples+SEP+dt+SEP+d2t+SEP+count+SEP+totdistanceaverage+SEP+totdistanceerror+SEP+totdistancesigma);
            if (nattempts>=maxsamples) break;
        }while (    (CCerror>CCaverage*fracerror ) 
                 || (nattempts<minsamples) );
        
    }

// -----------------------------------------------------------------------       
    /**
     * Calculates source of unweighted edge e.
     *@param e edge of interest source or target
     *@return source vertex number (target is +1)
     */
    int unweightedEdgeSource(int e)  
    {   
       if (e%2 == 1) return e-1;
       return e;
    }

// -----------------------------------------------------------------------       
    /**
     * Calculates Cluster Coefficient of one edge using directed triangles.
     *@param e edge of interest
     *@return cluster coefficient, -1.0 or -2.0 if not defined,
     */
    double calcCCEdgeOne(int e)  
    {   
        int v1,v2,s1,s2,vs,vt,vd,ss;
        int nedgepairs =0;
        int ntriangles = 0;
        if ((e<0) || (e>=TotalNumberStubs))
        {
            System.out.println("*** Error in calcCCOne vertex "+e+" invalid with "+TotalNumberStubs+" total edges");
            return -2.0;
        }
        
        int es = (e/2)*2; // taken even part
        vs=stubSourceList[es];
        vt=stubSourceList[es+1];
        int vssize =vertexList[vs].size();
        int vtsize =vertexList[vt].size();
        if ((vssize==0) && (vtsize==0)) return -1.0;
        for (int e1 =0; e1<vssize -1; e1++)
        {
            v1 = vertexList[vs].get(e1);
            for (int e2 =0; e2<vtsize; e2++)
            {
               //nedgepairs++;
               v2 = vertexList[vt].get(e2);
               if (v1==v2) ntriangles++;
            }// e2   
        }// e1
        
        //Now check other way
        int vtemp=vs;
        vs=vt;
        vt=vtemp;
        
        vssize =vertexList[vs].size();
        vtsize =vertexList[vt].size();
        if ((vssize==0) && (vtsize==0)) return -1.0;
        for (int e1 =0; e1<vssize -1; e1++)
        {
            v1 = vertexList[vs].get(e1);
            for (int e2 =0; e2<vtsize; e2++)
            {
               //nedgepairs++;
               v2 = vertexList[vt].get(e2);
               if (v1==v2) ntriangles++;
            }// e2   
        }// e1
        
        
        
        return ((double) ntriangles)/((double) vtsize*vssize);
    }// eo calcCCOne
      
    
// -----------------------------------------------------------------------       
    /**
     * Calculates Edge Clustering Coefficient. 
     *@param fracerror fraction error to aim for, if >1 then systematic sample of all vertices
     *@param minimumsamples minimum number of samples
     *@param maximumsamples maximum number of samples
     */
    public void calcCCEdgeSample(double fracerror, int minimumsamples,  int maximumsamples)  
    {
        boolean doall=false;
        int minsamples = minimumsamples;  
        int maxsamples = maximumsamples;
        if (fracerror>=1.0) 
        { 
            doall=true; 
            maxsamples = TotalNumberStubs;
            minsamples = TotalNumberStubs;
        }
        double CC;
        //CCdist = new DoubleArrayList(100); 
        int nattempts = 0;
        CCEdgensamples = 0;
        CCEdgeaverage =0.0;
        CCEdgeerror =0.0;
        CCEdgesigma =0.0;
        double CCt =0;
        double CC2t =0;
        if     ( TotalNumberStubs==0)  return;
        do 
        {   
            if (doall) CC=calcCCEdgeOne(nattempts);
            else CC=calcCCEdgeOne(Rnd.nextInt(TotalNumberStubs));
            nattempts++;
            if (CC<0) continue;
            CCEdgensamples++;
            //CCdist.add(CC);
            CCt += CC;
            CC2t += CC*CC;
            CCEdgeaverage = CCt/CCEdgensamples; 
            CCEdgeerror =calcError(CCt,CC2t,CCEdgensamples);
            CCEdgesigma =calcSigma(CCt,CC2t,CCEdgensamples);
//            System.out.println(CCnsamples+SEP+dt+SEP+d2t+SEP+count+SEP+totdistanceaverage+SEP+totdistanceerror+SEP+totdistancesigma);
            if (nattempts>=maxsamples) break;
        }while (    (CCerror>CCaverage*fracerror ) 
                 || (nattempts<minsamples) );
        
    }
    
   
    
    
// -----------------------------------------------------------------------       
   /**
    * Calculates first moment of (out) degree distribution
    *@return first moment
    */
  public double calcDegreeFirstMoment()  {
        return (TotalNumberStubs/((double)TotalNumberVertices));
  }
   /**
    * Calculates first moment of (out) degree distribution
    *@return first moment
    */
  public double getDegreeFirstMoment()  {
        return (calcDegreeFirstMoment());
  }

  /**
   * Average degree of graph.
   * <p>Zero number of vertices gives negative (DUNSET) number.
   * <p>Treats directed graphs properly.
   * @return 
   */
  public double getAverageDegree(){
      if (TotalNumberVertices==1) return DUNSET;
      if (directedGraph) return ((double) getNumberEdges())/((double) TotalNumberVertices);
      else return ((double) TotalNumberStubs)/((double)TotalNumberVertices);
  }
  /**
     * Gets second moment of (out) degree distribution.
     * <p>If internal result is negative then recalculates
     * exactly and sets internal value.  Otherwise internal value is returned.
     * @return second moment of (out) degree distribution
     */
    public double getDegreeSecondMoment()  {
      if (degreeSecondMoment<0) calcDegreeSecondMoment();
      return degreeSecondMoment;
  }
   /**
    * Calculates second moment of (out) degree exactly.
    * <p>No checks on overflows and visits every vertex so exact result.
    *@return second moment
    */
  public double calcDegreeSecondMoment()  {
      int k2=0;
      int k=0;
      for(int v=0; v<TotalNumberVertices; v++){ k=getVertexDegree(v); k2+=k*k;}
      degreeSecondMoment= k2/((double)TotalNumberVertices);
      return degreeSecondMoment;
  }


    /**
     * Calculates degree distribution.
     * <p>Overal degree distribution always calculated, 
     * bipartite too if has that structure.
     */

    public void calcDegreeDistribution()  {
    calcDegreeDistributionUnipartite();
    calcDegreeDistributionBipartite();   
    }

    /**
     * Calculates bi partite degree distributions.
     * <p>Does nothing if not bipartite.
     */
     public void calcDegreeDistributionBipartite()  {
         String name=inputName.getNameRoot();
         if (!bipartiteGraph) return;
        DD1total = new DegreeDistribution(name+"Type1Total");
        DD1total.calcDegreeDistribution(vertexList , 0, numberVertexType1);                     
        DD2total = new DegreeDistribution(name+"Type2Total");
        DD2total.calcDegreeDistribution(vertexList ,numberVertexType1,TotalNumberVertices);
     }
    /**
     * Calculates degree distribution treating the graph as Unipartite.
     */
     public void calcDegreeDistributionUnipartite()  {

         String name=inputName.getNameRoot();
        if (directedGraph) 
        {
        DDout = new DegreeDistribution(name+"Out");
        DDout.calcDegreeDistribution(vertexList , TotalNumberVertices);  
        DDin = new DegreeDistribution(name+"In");
        DDin.calcDegreeDistribution(vertexSourceList , TotalNumberVertices);  
        // Now calculate the total distribution and measures
        int maximum = Math.max(DDin.maximum,DDout.maximum);
        DDtotal = new DegreeDistribution(name+"Total",maximum);
        DDtotal.calcDegreeDistribution(vertexList ,  vertexSourceList , TotalNumberVertices);
        }//eo if
        else
        {  // only total degree available
           DDtotal = new DegreeDistribution(name+"Total");
           DDtotal.calcDegreeDistribution(vertexList , TotalNumberVertices);                     
        }
        return;
    }//eo calcDegreeDistribution
   
 

// -----------------------------------------------------------------------       
    /**
     * Used to calculates weight (= number multiple links) distribution.
     */
    void ordervertexList()  {
        for(int v=0; v<TotalNumberVertices; v++)
        {
//          System.out.println("Ordering vertex "+v); 
          if (vertexList[v]==null) 
          {
              System.out.println("In ordervertexList null vertexList "+v); 
              return;
          }
          int k = vertexList[v].size();
          for (int e1=0; e1<k; e1++)
          {
              int v1=vertexList[v].get(e1);
              for (int e2=e1; e2<k; e2++) if (v1>vertexList[v].get(e2)) 
              {
                v1=vertexList[v].get(e2);
                vertexList[v].set(e2,vertexList[v].get(e1));
                vertexList[v].set(e1,v1);
              }
          }
        }
        return;
    }//eo ordervertexList

// -----------------------------------------------------------------------       
    /**
     * Calculates multiple link distribution via weights.
     */
    void calcMultipleLinkDistribution()  
    {
        ordervertexList();  
        
        weightmaximum =-1 ;
        weightdarr = new IntArrayList() ;
        weightdarr.add(0); // set first entry to zero
        rddarr = new IntArrayList() ; // reduced degree distribution without 
                                      // multiple edges
        rddarr.add(0); // set first entry to zero
        
        rvertexList = new IntArrayList[maximumVertices];
        strengthlist  = new IntArrayList[maximumVertices];
       
        int k;
        for(int v=0; v<TotalNumberVertices; v++)
        {
          rvertexList[v] = new IntArrayList();
          strengthlist[v]  = new IntArrayList();
          
      
          if (vertexList[v]==null) 
          {
//              System.out.println("!!! Warning vertex "+v+" is disconnected in calcWeightDistrib");
              addExtendIntArrayList(weightdarr, 0,  1) ; 
              break;
              }
          k = vertexList[v].size();
          int v1=vertexList[v].get(0);
          int v2;
          int count =1; //count the edge 0 itself
          for (int e1=1; e1<k; e1++)
          {
              v2=vertexList[v].get(e1);
              if (v1==v2) count++;
              else 
              {
//                  System.out.println("Adding v, v1, count =  "+v+SEP+v1+SEP+count); 
                  strengthlist[v].add(count);
                  rvertexList[v].add(v1);
                  addExtendIntArrayList(weightdarr, count,  1) ;                   
                  v1=v2; 
                  count=1;
              }
          }
          // add last visit
//          System.out.println("Adding last v, v1, count =  "+v+SEP+v1+SEP+count); 
          strengthlist[v].add(count);
          rvertexList[v].add(v1);
          addExtendIntArrayList(weightdarr, count,  1);
          // reduced vertex degree distribution
          addExtendIntArrayList(rddarr, rvertexList[v].size(),  1) ; 
        }
        weightmaximum=weightdarr.size();
        return;
    }//eo calcWeightDistribution

// -----------------------------------------------------------------------       
    /** Calculates strength and weight distribution. 
     */
    void calcReducedWeightDistribution()  
    {
        //ordervertexList();  
        
        weightmaximum =-1 ;
        weightdarr = new IntArrayList() ;
        weightdarr.add(0); // set first entry to zero
        //rddarr = new IntArrayList() ;
        //rddarr.add(0); // set first entry to zero
        
        //rvertexList = new IntArrayList[maximumVertices];
        strengthlist  = new IntArrayList[maximumVertices];
       
        int k;
        for(int v=0; v<TotalNumberVertices; v++)
        {
          rvertexList[v] = new IntArrayList();
          strengthlist[v]  = new IntArrayList();
          
      
          if (vertexList[v]==null) 
          {
//              System.out.println("!!! Warning vertex "+v+" is disconnected in calcWeightDistrib");
              addExtendIntArrayList(weightdarr, 0,  1) ; 
              break;
              }
          k = vertexList[v].size();
          int v1=vertexList[v].get(0);
          int v2;
          int count =1; //count the edge 0 itself
          for (int e1=1; e1<k; e1++)
          {
              v2=vertexList[v].get(e1);
              if (v1==v2) count++;
              else 
              {
//                  System.out.println("Adding v, v1, count =  "+v+SEP+v1+SEP+count); 
                  strengthlist[v].add(count);
                  rvertexList[v].add(v1);
                  addExtendIntArrayList(weightdarr, count,  1) ;                   
                  v1=v2; 
                  count=1;
              }
          }
          // add last visit
//          System.out.println("Adding last v, v1, count =  "+v+SEP+v1+SEP+count); 
          strengthlist[v].add(count);
          rvertexList[v].add(v1);
          addExtendIntArrayList(weightdarr, count,  1);
          // reduced vertex degree distribution
          addExtendIntArrayList(rddarr, rvertexList[v].size(),  1) ; 
        }
        weightmaximum=weightdarr.size();
        return;
    }//eo calcWeightDistribution

    /**
     * Used to calculate the strength of all vertices.
     * <p>This is the out strength if the graph is directed.
     * Its the degree if the graph has unweighted edges.
     * Vertex labels must be on as strength is stored there.
     * <p>Strength is also available vertex by vertex 
     * if <tt>vertexEdgeListOn</tt> is true, and this is used here if available.
     */
    public void calcStrength(){
        if (!vertexlabels) { 
            System.err.println("!!! WARNING calcStrength() requires vertex labels to be on"); 
            return;
        }
        if (!weightedEdges) {
            for (int v=0; v<TotalNumberVertices; v++) 
                vertexLabelList[v].setStrength(getVertexDegree(v));
            TotalWeight=TotalNumberStubs;
            return;
        }
        if (this.vertexEdgeListOn){
            TotalWeight=0;
            double s=-1;
            for (int v=0; v<TotalNumberVertices; v++) {
                s=this.getVertexOutStrength(v);
                vertexLabelList[v].setStrength(s);
                TotalWeight+=s;
            }
            return;
        }
        calcStrengthDirectly();
    }
    
    /**
     * Used to calculate the strength of all vertices directly.
     * <p>This is the out strength if the graph is directed.  
     * Its the degree if the graph has unweighted edges.
     * Vertex labels must be on as strength is stored there.
     * <p>Strength is also available vertex by vertex if 
     * <tt>vertexEdgeListOn</tt> is true, and this is used here if available.
     */
    public void calcStrengthDirectly(){
        if (!vertexlabels) { 
            System.out.println("!!! WARNING calcStrengthDirectly() requires vertex labels to be on"); 
            return;
        }
        if (!weightedEdges) {
            for (int v=0; v<TotalNumberVertices; v++) vertexLabelList[v].setStrength(getVertexDegree(v));
            TotalWeight=TotalNumberStubs;
            return;
        }
        for (int v=0; v<TotalNumberVertices; v++) vertexLabelList[v].setStrength(0);
        int s=-1;
        int t=-1;
        double w=-1;
        TotalWeight=0;
        for (int e=0; e<TotalNumberStubs; e++) {
            w=edgeValuetList[e].weight;
            TotalWeight+=w;
            s=stubSourceList[e++]; // source vertex
            vertexLabelList[s].addStrength(w);
            vertexLabelList[s].updateMaxWeight(w);
            if (!isDirected()) { // target vertex
             t=stubSourceList[e]; // do not increment, done in for loop
             if (s!=t) {
                 vertexLabelList[t].addStrength(w);
                 vertexLabelList[t].updateMaxWeight(w);
             }
            }
        }    
        
    }

    /**
     * Calculate total weight.
     * <p>Does not use vertex strengths.
     */
        public void calcTotalWeight(){
            if (isWeighted()) {
                TotalWeight=0;
                double w=-1;
                int s=-1;
                int t=-1;
                for (int e=0; e<TotalNumberStubs; e++) {
                    s=stubSourceList[e];
                    w=edgeValuetList[e++].weight;
                    TotalWeight+=w;
                    if (!isDirected()){
                        t=stubSourceList[e];
                        if (s!=t) TotalWeight+=w;
                    }
                }
            }
            else TotalWeight=TotalNumberStubs/2;
        }

    /**
     * Calculate total number of triangles in graph.
     * <p>Result stored internally in {@link #TotalNumberTriangles}.
     * @param returns number of triangles
     */
        public int calcTrianglesTotal(){
            int t=0;
            for (int v=0; v<TotalNumberVertices; v++) t+=this.calcTriangles(v);
            TotalNumberTriangles = t/3;
            return TotalNumberTriangles;
        }

    /**
     * Calculate total number of triangles and squares in graph.
     * <p>Result stored internally in {@link #TotalNumberTriangles}
     * and {@link #TotalNumberSquares}.
     * @param returns number of triangles
     */
        public int calcTrianglesSquaresTotal(){
            int s=0;
            int t=0;
            int [] res = {0,0};
            for (int v=0; v<TotalNumberVertices; v++) {
                res=calcTrianglesSquares(v);
                t+=res[0];
                s+=res[1];
            }
            TotalNumberTriangles = t/3;
            TotalNumberSquares = s/4;
            return TotalNumberTriangles;
        }

    /**
     * Get number of triangles in graph.
     * <p>Uses result stored internally unless that is negative in which case an
     * exact calculation of triangles only is performed.
     * @return number of triangles
     */
        public int getNumberTriangles(){
            if (TotalNumberTriangles<0) return calcTrianglesTotal();
            return TotalNumberTriangles;
        }

    /**
     * Get total number of squares in graph.
     * <p>Uses result stored internally unless that is negative in which case an
     * exact calculation is performed.
     * @return number of squares
     */
        public int getNumberSquares(){
            if (TotalNumberSquares<0) return calcTrianglesSquaresTotal();
            return TotalNumberSquares;
        }

    /**
     * Returns global Clustering Coefficient.
     * <p>This is the internally stored value.
     * If that is negative then it is recalculated exactly.
     * <p>Not the average of the individual vertex c.c.
     * but three times the number of triangles divided by the number of incident
     * edge pairs.
     * @return global clustering coefficient
     */
        public double getCCGlobal(){
            if (ccGlobal<0) return calcCCGlobal();
            return ccGlobal;
        }
    /**
     * Calculates global Clustering Coefficient.
     * <p>Not the average of the individual vertex c.c.
     * but three times the number of triangles divided by the number of incident
     * edge pairs. Internal result is updated.
     * <p>NaN returned if there is a problem with the calculation
     * @return global clustering coefficient
     */
        public double calcCCGlobal(){
            try{
                int t=getNumberTriangles()/TotalNumberVertices;
                ccGlobal = 6*t/(this.getDegreeSecondMoment()-getDegreeFirstMoment());
            }
            catch (RuntimeException e){
                ccGlobal = Double.NaN;
            }
            return ccGlobal;
        }



    /**
     * Used to calculate the ranking parameters.
     */
    public void calcRanking(){
        if (averageWalkLength<0) averageWalkLength=diameter;
        if (averageWalkLength<2.0) averageWalkLength =2.0;
        if (rankingProbabilityLengthScale<0) rankingProbabilityLengthScale = distanceaverage;
        if (rankingProbabilityLengthScale<2.0) rankingProbabilityLengthScale=2.0;
//        double rankingProbability=(1.0 / (1.0+(1.0/rankingProbabilityLengthScale) ) );
        
        int totalNumberaverageWalkLength=TotalNumberStubs*100;
        int startVertex=-1;
        if (infoLevel>-2) System.out.println("randomrandomWalk.randomWalkMode, averageWalkLength, rankingProbabilityLengthScale, totalNumberaverageWalkLength, startVertex = "
                           +SEP+randomWalkMode+SEP+ averageWalkLength+SEP+ rankingProbabilityLengthScale+SEP+ totalNumberaverageWalkLength+SEP+ startVertex);
        DoRandomWalk(totalNumberaverageWalkLength, startVertex);
        calcMaximumVertexLabel();
        
    }
    /**
     * Used to calculate the Structural Hole Vertex Data.
     */
    public void calcStructuralHoleVertexData(){

        setVertexLabelsOn();
        boolean edgeDataOn=false;
        for (int s=0; s<this.TotalNumberVertices; s++){
           StructuralHoleVertexData shvd = StructuralHoleAnalysis.calcStructuralHoleMeasuresVertex(this, s,  edgeDataOn);
           this.vertexLabelList[s].setStructuralHoleVertexData(shvd);
        }

    }
// -----------------------------------------------------------------------       
    /**
     * Adds value to the element at index.
     * Adds value to the element at index extending with zero entries
     * or starting list if necessary
     * @param ial IntArrayList
     * @param index the index in the IntArrayList
     * @param value value to set:  ial[index]=value
     */
    void addExtendIntArrayList(IntArrayList ial, int index,  int value)  
    {
        int size = ial.size();
        // *** SURELY WRONG only initialise ial if zero size.
        if ((index==0) && (size==0)) {ial.add(value); return;}
        for (int i=size; i<=index; i++) ial.add(0);
        ial.set(index, ial.get(index)+value);
        return;
                  
    }

// **************************************************************************
    // Check UNSET values
    public boolean isUnset(int i){return (i==IUNSET?true:false);}
    public boolean isUnset(double d){return (d==DUNSET?true:false);}
    public boolean isUnset(String s){return (s.equals(SUNSET)?true:false);}

// **************************************************************************
    // Access to private variables

    public boolean isWeighted(){return this.weightedEdges;}
    public boolean isEdgeLabelled(){return this.labelledEdges;}
    public boolean isMultiEdge(){return this.multiEdge;}
    public boolean isSelfLooped(){return this.selfLoops;}
    public boolean isDirected(){return this.directedGraph;}
    public boolean isVertexEdgeListOn(){return this.vertexEdgeListOn;}
    /**
     * Check to see if vertices are labelled.
     * <p>Vertex labels carry all sorts of information.
     * @return true (false) if vertices have labels.
     */
    public boolean isVertexLabelled(){return this.vertexlabels;}
    /**
     * Check to see if vertices have positions.
     * <p>Uses first vertex label (if it exists) to see if vertices have position.
     * @return true (false) if vertices have positions.
     */
    public boolean hasVertexPositions(){
        if (!vertexlabels ) return false;
        if (this.vertexLabelList==null) return false;
        return vertexLabelList[0].hasPosition();
    }
    /**
     * Check to see if vertices have names.
     * <p>Uses first vertex label (if it exists) to see if vertices have names.
     * @return true (false) if vertices have names.
     */
    public boolean hasVertexNames(){
        if (!vertexlabels ) return false;
        if (this.vertexLabelList==null) return false;
        return vertexLabelList[0].hasName();
    }
    public boolean isBipartite(){return this.bipartiteGraph;}

    /**
     * Performs explicit check of self-loop status.
     * @return true (false) if any self-loops found.
     */
    public boolean checkSelfLoops(){
        for (int s=0; s<this.TotalNumberStubs; s++){
            if (this.isStubSelfLoop(s)) return true;
        }
        return false;
    }

    /**
     * Performs explicit check to see if negative weights exist.
     * @return true (false) if any negative weights found.
     */
    public boolean checkNegativeWeights(){
        if (!this.weightedEdges) return false;
        for (int s=0; s<this.TotalNumberStubs; s++){
            if (this.getEdgeWeight(s)<0) return true;
        }
        return false;
    }

    /**
     * Performs explicit check of multi loop status.
     * @return true (false) if any multi loops found.
     */
    public boolean checkMultiEdges(){
        for (int v=0; v<this.TotalNumberVertices; v++){
            if (getFirstEdgeLocal(v, v)>=0) return true;
        }
        return false;
    }

    /**
     * Tests to see if vertex index is in graph.
     * @param v global vertex index to test
     * @return true (false) if vertex index is (not) allowed
     */
    public boolean isVertexInGraph(int v){
        if (v<0) return false;
        if (v<this.TotalNumberVertices) return true;
        return false;
    }


    /**
     * Tests to see if vertex is type one in a bipartite graph.
     * <p>Returns true if not bipartite.
     * @param v vertex to be tested
     * @return true if bipartite and type one vertex, false if not bipartite or its type 2.
     */
    public boolean isType1(int v){
        if (bipartiteGraph && (v<numberVertexType1)) return true;
        return false;
    }
    /**
     * Tests to see if vertex is type two in a bipartite graph.
     * <p>Returns true if not bipartite.
     * @param v vertex to be tested
     * @return true if bipartite and type two vertex, false if not bipartite or its type 1.
     */
    public boolean isType2(int v){
        if (!bipartiteGraph || (v<numberVertexType1)) return false;
        return true;
    }
    /**
     * Returns true (false) if infoLevel is (not) greater than specified integer i.
     * @param i the integer
     * @return true (false) if infoLevel is (not) greater than  i.
     */public boolean isInfoLeveLGreaterThan(int i){return((this.infoLevel>i)?true:false);}
     
    public void setWeightedEdges(boolean b){weightedEdges=b;}
    public void setLabelledEdges(boolean b){labelledEdges=b;}
    public void setMultiEdge(boolean b){multiEdge=b;}
    public void setSelfLoops(boolean b){selfLoops=b;}
    public void setDirectedGraph(boolean b){directedGraph=b;}
    public void setVertexEdgeList(boolean b){vertexEdgeListOn=b;}
    public void setVertexlabels(boolean b){vertexlabels=b;}
    /**
     * Sets the following properties of a graph.    
     * @param makeDirected true (false) if want (un)directed graph
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     */
    public void setGraphProperties(boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList){
         directedGraph=makeDirected;
         vertexlabels=makeLabelled;
         weightedEdges=makeWeighted;
         vertexEdgeListOn=makeVertexEdgeList;
     }  

    /**
     * Sets number of bipartite vertices.
     * <p>Leaves names of these vertices unchanged.
     * @param n1 number of type 1 vertices
     * @param n2 number of type 2 vertices
     */
    public void setBipartite(int n1, int n2){
        numberVertexType1=n1; numberVertexType2=n2;
    }

    /**
     * Sets number and type name of bipartite vertices.
     * @param n1 number of type 1 vertices
     * @param n2 number of type 2 vertices
     * @param name1 name of type 1 vertices
     * @param name2 name of type 2 vertices
     */
    public void setBipartite(int n1, int n2, String name1, String name2){
        numberVertexType1=n1; numberVertexType2=n2;
        setBipartiteVertexNames(name1,  name2);
        bipartiteGraph = true;
    }

    public void setBipartiteVertexNames(String name1, String name2){
        this.nameVertexType1=name1; this.nameVertexType2=name2;
    }
    /** 
     * Sets root used for both input and output files.
     * @param root input and output name roots are set to be this.
     */    
  public void setNameRoot(String root)  {
      inputName.setNameRoot(root);
      outputName.setNameRoot(root);
  }       

    /** 
     * Adds to root used for both input and output files.
     * @param add input and output name roots have this added to their names
     */    
  public void addToNameRoot(String add)  {
      inputName.setNameRoot(inputName.getNameRoot()+add);
      outputName.setNameRoot(outputName.getNameRoot()+add);
  }       

     
// **************************************************************************
// GET ROUTINES
    
    
// ---------------------------------------------------------      
   /** 
    * Returns number of vertices in the timgraph.
     * @return number of vertices.
     */    
  public int getNumberVertices()  {
      return TotalNumberVertices;
  }    
   /** 
    * Returns number of vertices of type 1 in a bipartite timgraph.
    * <br>No checkiong for bipartite nature.
     * @return number of vertices.
     */    
  public int getNumberVerticesType1()  {
      return this.numberVertexType1;
  }       
   /** 
    * Returns number of vertices of type 2 in a bipartite timgraph.
    * <br>No checkiong for bipartite nature.
     * @return number of vertices.
     */    
  public int getNumberVerticesType2()  {
      return this.numberVertexType2;
  }       
   /**
    * Returns name of vertices of type 1 in a bipartite timgraph.
    *  @return name of vertices of type 1.
     */
  public String getNameVerticesType1()  {
      return this.nameVertexType1;
  }

   /**
    * Returns name of vertices of type 2 in a bipartite timgraph.
    *  @return name of vertices of type 2.
     */
  public String getNameVerticesType2()  {
      return this.nameVertexType2;
  }

// ---------------------------------------------------------      
   /**
    * Returns number of stubs in the timgraph.
    * <p>If directed this is total of in and outgoing stubs.
     * @return number of stubs.
     */
  public int getNumberStubs()  {
      return TotalNumberStubs;
  }

   /**
    * Returns number of edges in the timgraph.
     * @return number of edges = (number stubs/2).
     */
  public int getNumberEdges()  {
      return (TotalNumberStubs>>1);
  }

// ---------------------------------------------------------      
   /** 
    * Returns total weight in the timgraph.
    * <p>Sum of all stub weights, or total degree if unweighted graph
    * If this has not been set then this is recalculated.
    * Does not use vertex strengths.
     * @return total weight in the timgraph.
     */    
  public double getTotalWeight()  {
      if (TotalWeight==DUNSET) calcTotalWeight();
      return TotalWeight;
  }       
   
// ---------------------------------------------------------      
   /** 
    * Returns number of distinct edges in the timgraph.
    * <p>This is the number of stubs for a directed graph or half this for an
    * undirected graph.
     * @return number of distinct edges.
     */    
  public int getNumberDistinctEdges()  {
      return (directedGraph?TotalNumberStubs:TotalNumberStubs/2);
  }       
   

// ---------------------------------------------------------      
  /**
   * Returns infomation level flag, negative is less, positive is more.
   * <p>Try between -2 and +2.
   * @return <tt>infoLevel</tt>
   */public int getInformationLevel()  {
      return infoLevel;
  }       
   
// ---------------------------------------------------------      
  /**
   * Sets infomation level flag, negative is less, positive is more.
   * <p>Try between -2 and +2.
   * @param il new value for <tt>infoLevel</tt>
   */public void setInformationLevel(int il)  {
      infoLevel=il;
  }       
   

// ---------------------------------------------------------      
   /** 
     * Chooses a random edge from whole graph.
     */    
  public int getRandomEdge()  {
    return Rnd.nextInt(TotalNumberStubs); 
  }       

// ---------------------------------------------------------      
   /**
     * Finds edge from given vertices.
    * <br>This gives the first instance where <tt>t</tt> appears in the <tt>vertexList[s]</tt>
    * Note this is the index e for <code>stubSourceList[e]</code>.
    * It is NOT the local index e for <code>vertexList[s].get(e)</code>.
    * <br><code>vertexEdgeList</code> must be on.
    * @param s source vertex
    * @param t target vertex
    * @return global edge index of first edge where <code>vertexList[s]=t</code>, -1 if none found.
     */
  public int getFirstEdgeGlobal(int s, int t)  {
      int e; //=vertexList[s].binarySearch(t);
      for (e=0; e<vertexList[s].size();e++) if (vertexList[s].getQuick(e)==t) break;
      if (e==vertexList[s].size()) return -1;
      return vertexEdgeList[s].get(e);
  }
   /**
     * Finds the adjacency matrix entry A_{s,t}.
    * <br>If this is not a self-loop (s!=t) then this is the total weight
    * of the edge from s to t.  If its undirected this will be the same
    * as the weight from t to s.  For self-loops this is again the edge weight
    * if its directed but for an directed self-loop this is <b>twice</b>
    * the weight of the self-loop edge.  This is the correct way to do things
    * if we are to make sure the total stub weight is the same as
    * \sum_{s,t} A_{s,t} .
    * <br><code>vertexEdgeList</code> must be on.
    * @param s source vertex
    * @param t target vertex
    * @return value of adjaceny matrix entry <code>A_{s,t}</code>.
     */
  public double getAdjacencyMatrixEntry(int s, int t)  {
      double adjacencyMatrixEntry=0;
      for (int n=0; n<vertexList[s].size();n++) {
          if (vertexList[s].getQuick(n)==t) adjacencyMatrixEntry+=this.getEdgeWeight(vertexEdgeList[s].get(n));
      }
      return adjacencyMatrixEntry;
  }
   /** 
     * Finds global edge index from given vertex and its local edge index.
    * <br><code>vertexEdgeList</code> must be on but this is not checked.
    * @param s vertex
    * @param elocal local edge label of vertex s
    * @return global index of edge
    * @see {@link timgraph#getStub(int, int) }
     */    
  public int getGlobalEdgeFromLocal(int s, int elocal)  {
      return vertexEdgeList[s].get(elocal);
  }       
// ---------------------------------------------------------      
   /** 
    * Finds edge from given vertices.
    * <br>Note this is the index e in <code>vertexList[s].get(e)</code>.  
    * It is NOT the global index e for <code>stubSourceList[e]</code>.
    * @param s source vertex
    * @param t target vertex
    * @return local edge index of first edge where <code>vertexList[s]=t</code>, -1 if none found.
     */    
  public int getFirstEdgeLocal(int s, int t)  {
      for (int e=0; e<vertexList[s].size();e++) if (vertexList[s].getQuick(e)==t) return e;
      return -1;
  }       

// ---------------------------------------------------------      
   /** 
     * Finds next edge from given vertices.
    * <br>Note this is the index e in <code>vertexList[s].get(e)</code>.  
    * It is NOT the global index e for <code>stubSourceList[e]</code>.
    * @param einitial initial index of edge in  <code>vertexList[s]</code>
    * @param s source vertex
    * @param t target vertex
    * @return local edge index of first edge where <code>vertexList[s]=t</code>, -1 if none found, -2 if initial edge out of bounds.
     */    
  public int getNextEdgeLocal(int einitial, int s, int t)  {
      if ((einitial<0) || (einitial>=vertexList[s].size())) return -2;
      for (int e=einitial; e<vertexList[s].size();e++) if (vertexList[s].getQuick(e)==t) return e;
      return -1;
  }       

  /**
   * Given an index in the local list of edges attached to a vertex (local stub index), returns the global edge index.
   * <br>If directed this is an outgoing edge.
   * <br>No checks done.
   * @param v the global vertex index.
   * @param localEdgeIndex the local index of a stub in the <tt>vertexEdgeList[v]</tt>
   * @return The global edge index <tt>vertexEdgeList[v].getQuick(localEdgeIndex)</tt>
   * @see {@link timgraph#getGlobalEdgeFromLocal(int, int) }
   */
  public int getStub(int v, int localEdgeIndex){
      return vertexEdgeList[v].getQuick(localEdgeIndex);
  }
  
 
  /**
   * Given an index in the local list of incoming edges attached to a vertex (local stub index), returns the global outgoing stub index.
   * <br>Assumes directed graph in use. No checks done.  Needs vertex edge list to be on.
   * @param v the global vertex index.
   * @param localEdgeIndex the local index of a stub in the <tt>vertexEdgeList[v]</tt>
   * @return The global edge index <tt>vertexEdgeList[v].getQuick(localEdgeIndex)</tt>
   */
  public int getStubIn(int v, int localEdgeIndex){
      return this.vertexInEdgeList[v].getQuick(localEdgeIndex);
  }
  
/**
 * Returns the label (an integer) if edge weights are on, otherwise the edge number is used
 * @param e edge index
 * @return string with suitable edge label
 */
  public String getEdgeLabelString(int e){
      return Integer.toString(getEdgeLabel(e));
  }
  
/**
 * Returns the label (an integer) if edge weights are on, otherwise the edge number is used.
 * @param e edge index
 * @return integer with suitable edge label
 */
  public int getEdgeLabel(int e){
      if (!weightedEdges) return e/2;
      int i = edgeValuetList[e].label;
      if (i==EdgeValue.NOLABEL) return e/2;
      return i;
  }
/**
 * Returns the label (an integer) if edge weights are on, otherwise constant EdgeValue.NOLABEL returned.
 * @param e edge index
 * @return integer with suitable edge label
 */
  public int getEdgeLabelSimple(int e){
      if (!weightedEdges) return EdgeValue.NOLABEL;
      return edgeValuetList[e].label;
  }
/**
 * Returns the label (an integer) if edge weights are on, otherwise returns value given.
 * @param e edge index
 * @param noLabelValue value to return if no value is given
 * @return integer with suitable edge label
 */
  public int getEdgeLabelOrZero(int e, int noLabelValue){
      if (!weightedEdges) return noLabelValue;
      int i = edgeValuetList[e].label;
      if (i==EdgeValue.NOLABEL) return noLabelValue;
      return i;
  }
  // ---------------------------------------------------------      
   /** 
     * Chooses a random vertex from whole graph.
     */    
  public int getRandomVertex()  {
    return Rnd.nextInt(TotalNumberVertices); 
  }       

 // ---------------------------------------------------------      
   /** 
    * Chooses a random vertex from neighbours of given vertex.
     * @param v vertex for which neighbours are required
     * @return random neighbouring vertex or -1 if no neighbours
     */    
  public int getRandomNeighbour(int v)  {
//      if ((v>= TotalNumberVertices) || (v<0)) System.out.println("*** Error in getRandomNeighbour vertex "+v+" out of "+TotalNumberVertices);
         int totalnn = vertexList[v].size();
         if (totalnn>0) return vertexList[v].get(Rnd.nextInt(vertexList[v].size()));
         return -1;
  }       
    
      

// ---------------------------------------------------------      
   /**
     * Chooses a random vertex at random end of random edge chosen from whole graph.  
    * Exploits symmetry in vertexList[].
     */    
  public int getRandomVertexFromEdge()  
  {
      return stubSourceList[Rnd.nextInt(TotalNumberStubs)];
  }       

// ---------------------------------------------------------      
   /** 
     * Returns the source of a randomly chosen edge from whole graph.
     */    
  public int getSourceRandomEdge()  {
    int e = Rnd.nextInt(TotalNumberStubs); 
    int s = (e-e%2);
    return stubSourceList[s];
  }       

// ---------------------------------------------------------      
   /** 
     * Returns the target of a randomly chosen edge from whole graph.
     */    
  public int getTargetRandomEdge()  {
    int e = Rnd.nextInt(TotalNumberStubs); 
    int t = (e-e%2+1);
    return stubSourceList[t];
  }       

// ---------------------------------------------------------      
   /** 
     * Returns the stub at the other end of edge e, i.e. flips last bit of e.
     *@param e edge number
     *@return the stub connected to stub e
     */    
  public int getOtherStubFromEdge(int e)  {
    return (e ^ 1); // XOR to flip last bit
  }       

  /**
   * Returns stub index of edge associated with given stub
   * @param s stub index
   * @return s or (s^1), which ever is of type 1, negative if neither is.
   */
  public int getStubType1(int s){
      if (stubSourceList[s]<this.numberVertexType1) return s;
      int s1= getOtherStubFromEdge(s);
      if (stubSourceList[s1]<this.numberVertexType1) return s1;
      System.err.println("Stub "+s+" has no type one vertices");
      return IUNSET;
  }

  /**
   * Returns stub index of edge associated with given stub
   * @param s stub index
   * @return s or (s^1), which ever is of type 1, negative if neither is.
   */
  public int getStubType2(int s){
      if (stubSourceList[s]>=this.numberVertexType1) return s;
      int s1= getOtherStubFromEdge(s);
      if (stubSourceList[s1]>=this.numberVertexType1) return s1;
      System.err.println("Stub "+s+" has no type two vertices");
      return IUNSET;
  }

// ---------------------------------------------------------      
   /** 
     * Returns the vertex of given stub.
     * <br>Ignores directionality
     *@param s global stub number
     *@return  the global index of the vertex at end of stub s
     */    
  public int getVertexFromStub(int s)  {
    return stubSourceList[s];
  }       
// ---------------------------------------------------------      
   /** 
     * Returns the vertex at the other end of edge specified by the given stub.
     * <br>Ignores directionality
     *@param s global stub index
     *@return the global index of the vertex at other end of edge specified by the stub
     */    
  public int getOtherVertexFromStub(int s)  {
    return stubSourceList[s^1];// XOR to flip last bit
  }       

// ---------------------------------------------------------      
   /** 
     * Returns the source of given edge.
     * <p>This is always the edhge number with last bit set to zero.
     *@param e edge number
     *@return the number of the source vertex of edge e
     */    
  public int getSourceVertexFromEdge(int e)  {
    //int s = (e | MAXEDGESOURCE ); // set last bit to zero (e-e%2);
    int s = (e >> 1) << 1;
    return stubSourceList[s];
  }       
// ---------------------------------------------------------      
   /** 
     * Returns the target of chosen edge.
     * <p>This is always the edhge number with last bit set to zero.
     *@param e edge number
     *@return the number of the target vertex of edge e
     */    
  public int getTargetVertexFromEdge(int e)  {
    int t = (e | 1); // set last bit to one MAXEDGETARGET (e-e%2+1);
    return stubSourceList[t];
  }       
  
  /**
   * Finds global vertex index from name of vertex.
   * <p>Looks for a vertex label starting with given name, case is ignored.
   * Note vertex labels must be present.
   * TODO use a vertex name to vertex index map
   * @param vertexName look for first vertex whose label starts with this string, ignoring case.
   * @return vertex index.  If none found, then this will equal the total number of vertices.
   */
  public int getVertexFromName(String vertexName){
      if (!vertexlabels) throw new IllegalArgumentException("graph with labelled vertices needed to find named vertex.");
        int vertex;
        for (vertex=0; vertex<getNumberVertices();vertex++) 
            if (getVertexName(vertex).equalsIgnoreCase(vertexName)) break;
        if (vertex==getNumberVertices()) System.err.println("*** NOT FOUND VERTEX "+vertexName);
     return vertex;   
  }
      
  /**
   * Vertex name to vertex global index map.
   * @return Vertex name to vertex global index map
   */
  public TreeMap<String,Integer> getVertexNameIndexMap(){
      if (!vertexlabels) throw new IllegalArgumentException("graph with labelled vertices needed to find named vertex.");
      TreeMap<String,Integer> nameToIndex = new TreeMap();
      for (int vertex=0; vertex<getNumberVertices();vertex++) 
               nameToIndex.put(this.getVertexName(vertex), vertex);
      return nameToIndex;
  }
      
// ---------------------------------------------------------      
   /** 
    * Returns degree of a vertex in the timgraph.
    * <br>For directed networks this is the out degree
    * <br>No tests done on input
     * @param v vertex number
     * @return out degree.
     */    
  public int getVertexDegree(int v)  {
      if (vertexList[v]==null) return 0;
      return vertexList[v].size();
  }       
    
// ---------------------------------------------------------
   /**
    * Returns name of a vertex in the timgraph.
    * <p>If vertices have no labels then their number is given as the name.
    * @param v vertex number
    * @return string with some sort of name for the vertex.
    */
  public String getVertexName(int v)  {
      if (!vertexlabels) return Integer.toString(v);
      String s = vertexLabelList[v].getName();
      if (s.equals(VertexLabel.DEFAULTNAME)) return Integer.toString(v);
      return s;
  }

  /**
    * Returns array of names of vertices.
    * <p>If vertices have no labels then their number is given as the name.
    * @return array of strings with some sort of name for each vertex.
    */
  public String [] getVertexNameArray(){
      String [] nameArray = new String [TotalNumberVertices];
      for (int v=0; v<this.TotalNumberVertices; v++) nameArray[v]=getVertexName(v);
      return nameArray;
  }

// ---------------------------------------------------------
   /**
    * Returns number of a vertex in the timgraph.
    * <p>The number is actually an integer stored in the vertex and is really
    * a label (e.g. community label) and not the index of the vertex.
    * If vertices have no labels or if they have no number then the
    * noNumber argument is returned.
    * @param v vertex number
    * @param noNumber value to return if no number found
    * @return number of vertexlabel or noNumber
    */
  public int getVertexNumber(int v, int noNumber)  {
      if (!vertexlabels) return noNumber;
      VertexLabel vl=vertexLabelList[v];
      if (vl.hasNumber()) return vl.getNumber();
      return noNumber;
  }
   /**
    * Returns number of a vertex in the timgraph.
    * <p>The number is actually an integer stored in the vertex and is really
    * a label (e.g. community label) and not the index of the vertex.
    * If vertices have no labels or if they have no number then the
    * timgraph.IUNSET value is returned.
    * @param v vertex number
    * @param noNumber value to return if no number found
    */
  public int getVertexNumber(int v)  {
      if (!vertexlabels) return timgraph.IUNSET;
      VertexLabel vl=vertexLabelList[v];
      if (vl.hasNumber()) return vl.getNumber();
      return timgraph.IUNSET;
  }

   /**
    * Returns position of a vertex in the timgraph.
    * @param v vertex number
    * @return position of vertex, null if none
    */
  public Coordinate getVertexPosition(int v)  {
      if (!vertexlabels) return null;
      VertexLabel vl=vertexLabelList[v];
      if (vl.hasPosition()) return vl.getPosition();
      return null;
  }

// ---------------------------------------------------------
   /**
    * Returns <tt>VertexLabel</tt> in the timgraph.
    * @param v vertex number
    * @return VertexLabel of vertex v or null if no labels used.
    */
  public VertexLabel getVertexLabel(int v)  {
      if (vertexlabels) return vertexLabelList[v];
      else return null;
  }

// ---------------------------------------------------------
   /**
    * Returns reference to <tt>VertexLabel[]</tt> in the timgraph.
    * @return VertexLabelList or null if no labels used.
    */
  public VertexLabel [] getVertexLabelList()  {
      if (vertexlabels) return vertexLabelList;
      else return null;
  }

// ---------------------------------------------------------      
   /** 
    * Returns out degree of a vertex in the timgraph.
    * <br>For undirected networks this is the same as the in degree.
    * <br>No tests done on input
     * @param v vertex number
     * @return out degree.
     */    
  public int getVertexOutDegree(int v)  {
      if (vertexList[v]==null) return 0;
      return vertexList[v].size();
  }       
    
// ---------------------------------------------------------      
   /** 
    * Returns in degree of a vertex in a directed timgraph.
    * <br>Fails for undirected networks but no tests done for this.
    * <br>No tests done on input
     * @param v vertex number
     * @return out degree.
     */    
  public int getVertexInDegree(int v)  {
      if (vertexSourceList[v]==null) return 0;
      return  vertexSourceList[v].size();
  }       
    
// ---------------------------------------------------------
   /**
    * Calculates and returns strength of a vertex in the timgraph.
    * <br>If directed graph this is just the out strength otherwise it is the total strength.
    * <br>If vertexlabels are on and strength has been calculated, then this will be used.
    * <br>Other wise an explicit calculation is done which requires the vertexEdgeListOn.
    * <br>No tests done on input.
    * <p>Note that the strength is stored in the labels and calcStrengthDirectly
    * sets these values for all vertices.
    * @param v vertex number
    * @return out degree.
    */
  public double getVertexOutStrength(int v)  {
      if (!weightedEdges) return ( getVertexOutDegree(v));
      double s=0;
      if (vertexlabels) {
          s=vertexLabelList[v].getStrength();
          if (s!=VertexLabel.DUNSET) return s;
      }
      s=0;
      int edge=-1;
      for (int e=0; e<vertexEdgeList[v].size(); e++)
      {
          edge= vertexEdgeList[v].get(e);
          s+=edgeValuetList[edge].weight;
      }
      return s;
  }

// ---------------------------------------------------------
   /**
    * Calculates and returns strength of a vertex in the timgraph.
    * <br>If directed graph this is just the out strength otherwise it is the total strength.
    * <br>Needs the vertexEdgeListOn
    * <br>No tests done on input.
    * <p>Note that the maximum weight is stored in the labels and calcStrengthDirectly
    * sets these values for all vertices.
     * @param v vertex number
     * @return out degree.
     */
  public double getVertexMaxWeight(int v)  {
      if (!weightedEdges) return ( 1.0);
      double maxw=0;
      if (vertexlabels) {
          maxw=vertexLabelList[v].getMaxWeight();
          if (maxw!=VertexLabel.DUNSET) return maxw;
      }
      int edge=-1;
      for (int e=0; e<vertexEdgeList[v].size(); e++)
      {
          edge= vertexEdgeList[v].get(e);
          maxw=Math.max(maxw, edgeValuetList[edge].weight);
      }
      return maxw;
  }

// ---------------------------------------------------------
   /**
    * Calculates and returns strength of self-loops of a vertex in the timgraph.
    * <br>Needs the vertexEdgeListOn, can be selfloop on or off, with/without multiloops
    * <br>No tests done on input.
    * @param v vertex number
    * @return selfloop strength.
    */
  public double getVertexSelfLoopStrength(int v)  {

      if (!weightedEdges) return ( getVertexOutDegree(v));
      int edge=-1;
      double str=0;
      for (int e=0; e<vertexEdgeList[v].size(); e++)
      {
          edge= vertexEdgeList[v].get(e);
          if (isStubSelfLoop(edge)) str+= (weightedEdges ?edgeValuetList[edge].weight:1.0);
      }
      return str;
  }

  /**
   * Tests to see if stub is part of a self-loop.
   * <br>No tests done on input.
   * @param stub global stub label, can be source or target.
   * @return true (false) if stub is (not) part of a self loop.
   */
  public boolean isStubSelfLoop(int stub){
      if (stubSourceList[stub]==stubSourceList[(stub^1)]) return true;
      else return false;
  }
// ---------------------------------------------------------      
   /**
    * Returns in strength of a vertex of a directed.
    * <br>Defined if a directed or unweighted graph.
    * <br>Needs the vertexEdgeListOn
    * <br>No tests done on input or on graph type.
     * @param v vertex number
     * @return out degree.
     */
  public double getVertexInStrength(int v)  {
      if (!directedGraph) return ( getVertexOutStrength(v));
      if (!weightedEdges) return ( getVertexInDegree(v));
      int edge=-1;
      double s=0;
      for (int e=0; e<this.vertexInEdgeList[v].size(); e++)
          edge= vertexInEdgeList[v].get(e);
          s+=edgeValuetList[edge].weight;
      return s;
  }
    
  
  // ---------------------------------------------------------      
   /**
    * Returns reference to integer array of vertices associated with each stub.
    * <br>this is <code>stubSourceList</code>
     * @return link to stub source list.
     */
  public  int [] getStubSourceList()  {
      return stubSourceList;
  }
   /**
    * Returns weight of an edge in a weighted timgraph.
    * <br>No tests done on input or graph type.
     * @param e edge number
     * @return weight of edge
     */
  public double getEdgeWeightQuick(int e)  {
      return (edgeValuetList[e].weight);
  }
   /** 
    * Returns weight of an edge in a weighted timgraph.
    * <br>Returns 1.0 if graph has no edge weights
    * <br>No tests done on input.
     * @param e edge number
     * @return weight of edge
     */    
  public double getEdgeWeight(int e)  {
      return (weightedEdges?edgeValuetList[e].weight:1.0);
  }       

   /** 
    * Returns weight of an edge in a weighted timgraph.
    * <br>Returns 1.0 if graph has no edge weights
    * <br>If edge index is out of range retruns <tt>timgraph.DUNSET</tt>
     * @param e global edge index
     * @return weight of edge
     */    
  public double getEdgeWeightSlow(int e)  {
      if ((e<0) || (e>=TotalNumberStubs)) return DUNSET;
      return (weightedEdges?edgeValuetList[e].weight:1.0);
  }       

   /** 
    * Returns edge in a weighted timgraph.
    * <br>No tests done on input or graph type.
     * @param e edge number
     * @return EdgeValue of edge
     */    
  public EdgeValue getEdgeWeightAll(int e)  {
      return (edgeValuetList[e]);
  }       

  
  // ---------------------------------------------------------      
   /**
    * Returns global index of neighbouring vertex.
    * <br>Checks that vertex is in range. Works for all graphs.
    * If directed graph this only follows edges in direction of edge
    * i.e. this gets vertices which are thee targets of edges with
    * source input vertex v.
    * @param v global index of vertex for which neighbours are required
    * @param e local index of neighbours
    * @return index of e-th neighbouring vertex to v or -1 if no neighbours
     */
  public int getVertexTarget(int v, int e)  {
         if ((e<vertexList[v].size()) && (e>=0)) return vertexList[v].get(e);
         return -1;
  }

   /**
    * Returns global index of neighbouring vertex.
    * <br>No checks that vertex is in range.
    * @param v global index of vertex for which neighbours are required
    * @param e local index of neighbours
    * @return index of e-th neighbouring vertex to v or -1 if no neighbours
     */
  public int getVertexTargetQuick(int v, int e)  {
         return vertexList[v].get(e);
  }

    /**
     * Returns global index of neighbouring source vertex.
     * <br>For directed graphs only, this looks at incoming edges and finds their
     * source.  That is input vertex v is the target of these edges.  
     * Assumes we have directed graph but checks that vertex is in range.
     * @param v global index of vertex for which neighbours are required
     * @param e local index of neighbours
     * @return index of e-th neighbouring vertex to v or -1 if no neighbours
     */
  public int getVertexSource(int v, int e)  {
         if ((e<vertexSourceList[v].size()) && (e>=0)) return vertexSourceList[v].get(e);
         return -1;
  }
  /**
    * Returns global index of neighbouring source vertex.
    * <br>For directed graphs only, this looks at incoming edges and finds their
    * source.  That is input vertex v is the target of these edges.
    * Assumes we have directed graph and there are no checks that vertex is in range.
    * <br>No checks that vertex is in range or edges are directed.
    * @param v vertex for which neighbours are required
    * @param e local index of sources neighbours
    * @return index of e-th neighbouring vertex to v or -1 if no neighbours
     */
  public int getVertexSourceQuick(int v, int e)  {
         return this.vertexSourceList[v].get(e);
  }

 
  
  // *******************************************************************
  // Various set routines
  
 // ---------------------------------------------------------      
   /** 
    * Gets initial number of vertices for initial graph construction routines.
     * @return initial number of vertices.
     */    
  public int getInitialVertices()  {
      return initialVertices;
  }       

  /** 
    * Sets initial number of vertices for initial graph construction routines.
     * @param iv initial number of vertices.
     */    
  public void setInitialVertices(int iv)  {
      initialVertices = iv;
  }       
  /**
   * Gives the maximum number of edges.
   * <p>Can not be exceeded as it is used to set arrays.
   * @return maximum number of edges
   */
  public int getMaximumStubs(){return maximumStubs;}
  /**
   * Sets the maximum number of stubs.
   * <p>Can not be exceeded as it is used to set arrays.
   * @param n maximum number of stubs
   */
  public void setMaximumStubs(int n){maximumStubs=n;}
  /**
   * Gives the maximum number of vertices.
   * @return maximum number of vertices
   */
  public int getMaximumVertices(){return maximumVertices;}
  /**
   * Sets the maximum number of vertices.
   * <p>Can not be exceeded as it is used to set arrays.
   * @param n maximum number of vertices
   */
  public void setMaximumVertices(int n){maximumVertices=n;}
  
  /** 
    * Sets number of events for growth routines.
     * @param ne number of events.
     */    
  public void setNumberEvents(int ne)  {
      numevents = ne;
  }       

    /**
   * Sets number of a vertex.
   * <br>Create a label if didn't exist but no test to see if labels are on.
   * Tests done so make sure vertex labels are in use.
   * @param v vertex index
   * @param n new number for vertex
   */
  public void setVertexNumber(int v, int n)  {
      if (vertexLabelList[v]==null) vertexLabelList[v] = new VertexLabel(n);
      else vertexLabelList[v].setNumber(n);
  }


  public void setVertexLabelsOn(){
      if (!vertexlabels){
          vertexlabels =true;
          vertexLabelList = new VertexLabel[TotalNumberVertices];
      }
  }

  /**
   * Sets the number of all vertices to single label.
   * <br>Switches on vertex labels if needed.
   * @param n new number for vertex
   */
  public void setVertexNumbers(int n)  {
      setVertexLabelsOn();
      for (int v=0; v<TotalNumberVertices; v++) {
          setVertexNumber(v, n);
      }
  }

  /**
   * Sets number field for a vertex.
   * <br>No tests done so make sure vertex labels are in use.
   * @param v vertex index
   * @param n new number field for vertex
   */
  public void setVertexNumberQuick(int v, int n)  {
     this.vertexLabelList[v].setNumber(n);
  }

  /**
   * Sets position field for a vertex.
   * <br>No tests done so make sure vertex labels are in use.
   * @param v vertex index
   * @param c new coordinate for vertex
   */
  public void setVertexPositionQuick(int v, Coordinate c)  {
     vertexLabelList[v].setPosition(c);
  }

  /**
   * Sets VertexLabel for a vertex.
   * <br>No tests done so make sure vertex labels are initialised.
   * @param v global vertex index
   * @param label c new VertexLabel for vertex
   */
  public void setVertexLabelQuick(int v, VertexLabel label)  {
     vertexLabelList[v]=label;
  }

  /** 
   * Sets label for an edge.
   * <br>No tests done so make sure edge weights are in use.
   * @param e global edge index
   * @param l new label for edge
   */    
  public void setEdgeLabelQuick(int e, int l)  {
      edgeValuetList[e].label = l;
  }       
 
  /** 
   * Sets label for all edges using an vertex partition defined on the line graph.
   * <br>If needed switches on weightedEdges flag and initialises.
   * <br>The vertex index <tt>v</tt> of the given community (and of the line graph)
   * corresponds to the edge in this timgraph   
   * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>.
   * Note that the community here is usually constructed via 
   * a vertex partition for a line graph of this timgraph.
   * <br>Note that the same oEdgeWeight object is used for both edges
   * <tt>(2i)</tt> and <tt>(2i+1)</tt>
   * @param c edge partition
   */    
  public void setEdgeLabels(Partition c)  {
      if (c.getNumberElements() != getNumberEdges() )
          throw new RuntimeException("partition has "+c.getNumberElements()
                 +" elements which is not equal to number of edges "+getNumberEdges());

      if (!weightedEdges){
          weightedEdges=true;
          edgeValuetList = new EdgeValue[maximumStubs];
          for (int e=0; e<TotalNumberStubs; e++) {
              EdgeValue ew = new EdgeValue();
              edgeValuetList[e++] =ew;
              edgeValuetList[e] =ew;
          }
      }
      int [] cov = c.getCommunity();
      int e=0;
      for (int v=0; v<cov.length; v++) {
          edgeValuetList[e++].setLabel(cov[v]);
          edgeValuetList[e++].setLabel(cov[v]); // except for e++, should be redundant as access the same object.
      }
  }       
  /**
   * Sets label for all edges to be a single value.
   * <br>If needed switches on weightedEdges and labelledEdges flag
   * and initialises any EdgeWeights.  Keeps weight values if these are already set.
   * <br>The vertex index <tt>v</tt> of the given community (and of the line graph)
   * corresponds to the edge in this timgraph
   * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>.
   * Note that the community here is usually constructed via
   * a vertex partition for a line graph of this timgraph.
   * <br>Note that the same oEdgeWeight object is used for both edges
   * <tt>(2i)</tt> and <tt>(2i+1)</tt>
   * @param value value to set all edge labels to
   */
  public void setEdgeLabels(int value)  {
      EdgeValue ew;
      if (!weightedEdges){
          weightedEdges=true;
          labelledEdges=true;
          edgeValuetList = new EdgeValue[maximumStubs];
          for (int e=0; e<TotalNumberStubs; e++) {
              ew = new EdgeValue(value);
              edgeValuetList[e++] =ew;
              edgeValuetList[e] =ew;
          }
          return;
      }
      labelledEdges=true;
      for (int e=0; e<TotalNumberStubs; e++) {
          ew = edgeValuetList[e++]; // this sets e and e+1 as they are same object
          ew.setLabel(value); // leave weight value alone
      }
      return;

  }

    /**
   * Sets all the edge weights.
   * <br>If needed switches on weightedEdges flag and initialises new EdgeWeights if needed
   * (though no edge labels in that case).
   * Will resuse any existing EdgeWeights changing only their weight values, leaving
     * labels as they were (if any).
   * <br>The vertex index <tt>v</tt> of the given community (and of the line graph)
   * corresponds to the edge in this timgraph
   * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>.
   * Note that the community here is usually constructed via
   * a vertex partition for a line graph of this timgraph.
   * <br>Note that the same oEdgeWeight object is used for both edges
   * <tt>(2i)</tt> and <tt>(2i+1)</tt>
   * @param value value to set all edge labels to
   */
  public void setEdgeWeights(int value)  {
      EdgeValue ew;
      if (!weightedEdges)
      {
          weightedEdges=true;
          labelledEdges=false;
          edgeValuetList = new EdgeValue[maximumStubs];
      }
      for (int e=0; e<TotalNumberStubs; e++) {
          ew = edgeValuetList[e];
          if (ew==null) ew= new EdgeValue(value);
          else ew.weight=value;
          edgeValuetList[e++]=ew;
          edgeValuetList[e] =ew;
      }
      return;
  }

   /**
     * Sets all the edge weight of given edge.
     * @param ge global edge label (no checks done)
     * @param value value for edge weight
     */
  public void setEdgeWeight(int ge, double value)  {
          edgeValuetList[ge].weight=value;
      return;
  }

  /**
   * Sets numbers for all vertices using a vertex partition.
   * <br>Sets the number of a vertex to be the community label.
   * Initialise new <tt>vertexLabelList</tt> if does not exist.
   * @param vp vertex partition
   */
  public void setVertexNumbers(VertexPartition vp)  {
      if ((vp.getNumberElements()!=TotalNumberVertices) ) throw new RuntimeException ("Wrong number of elements in vertex partition "+vp.getName()+", "+vp.getNumberElements());
      if (!this.vertexlabels) initialiseVertexLabels();
      for (int v=0; v<this.TotalNumberVertices; v++) vertexLabelList[v].setNumber(vp.getCommunity(v));
  }

  /**
   * Initialises list of vertex labels.
   */
  public void initialiseVertexLabels()  {
      this.vertexlabels=true;
      this.vertexLabelList = new VertexLabel[this.maximumVertices];
      for (int v=0; v<this.TotalNumberVertices; v++) vertexLabelList[v] = new VertexLabel();
  }

  /**
   * Sets positions for all vertices using a vertex partition.
   * <br>Sets the number of a vertex to be the community label.
   * Initialise new <tt>vertexLabelList</tt> if does not exist.
   * @param vp vertex partition
   * @param smallScale scale to use to set local position within community
   * @param bigScale scale to use to set each communities position
   */
  public void setVertexPositionsFromPartition(VertexPartition vp, double smallScale, double bigScale)  {
      if ((vp.getNumberElements()!=TotalNumberVertices) ) throw new RuntimeException ("Wrong number of elements in vertex partition "+vp.getName()+", "+vp.getNumberElements());
      if (!this.vertexlabels) initialiseVertexLabels();
      vp.setCommunityLocalLabel();
      vp.analyse();
      double nc=vp.getNumberCommunities();
      double ncc=DUNSET;
      int c=IUNSET;
      double smallf=DUNSET;
      double bigf=DUNSET;
      double x=DUNSET;
      double y=DUNSET;
      for (int v=0; v<this.TotalNumberVertices; v++) {
          c= vp.getCommunity(v);
          ncc = vp.getNumberElementsInCommunity(c);
          smallf= vp.getCommunityLocalLabel(v)/ncc;
          bigf= c/nc;
          x=Math.cos(smallf*2*Math.PI)*smallScale+Math.cos(bigf*2*Math.PI)*bigScale;
          y=Math.sin(smallf*2*Math.PI)*smallScale+Math.sin(bigf*2*Math.PI)*bigScale;
          setVertexPositionQuick(v, new Coordinate(x,y) );
      }
  }


  /**
   * Sets positions for all vertices.
   * <br>Initialises new <tt>vertexLabelList</tt> if does not exist.
   * @param x list of x positions
   * @param y list of y positions
   */
  public void setVertexPositions(ArrayList<Double> x, ArrayList<Double> y)  {
      if ((x.size()!=TotalNumberVertices) || (y.size()!=TotalNumberVertices) )
            throw new RuntimeException ("Wrong number of x or y coordinates, found "+x.size()+", "+y.size());
      if (!this.vertexlabels) initialiseVertexLabels();
      for (int v=0; v<this.TotalNumberVertices; v++) vertexLabelList[v].setPosition(x.get(v),y.get(v));
  }

  /**
   * Sets positions for all vertices.
   * <br>Initialises new <tt>vertexLabelList</tt> if does not exist.
   * @param coord list of x then y coordiantes as strings
   */
  public void setVertexPositions(ArrayList<String> coord)  {
      if ((coord.size()!=2*TotalNumberVertices)  )
            throw new RuntimeException ("Wrong number of x and  y coordinates, found "+coord.size());
      if (!this.vertexlabels) initialiseVertexLabels();
      int c=0;
      double x=DUNSET;
      double y=DUNSET;
      String xstring=SUNSET;
      String ystring=SUNSET;
      for (int v=0; v<this.TotalNumberVertices; v++) {
          xstring=coord.get(c++);
          ystring=coord.get(c++);
          try{x=Double.parseDouble(xstring);
              y=Double.parseDouble(ystring);}
          catch(Exception e){
              System.err.println("!!! WARNING setVertexPositions found vertex "+v+" coordinates "+xstring+", "+ystring);
              x=DUNSET;
              y=DUNSET;
          }
          vertexLabelList[v].setPosition(x,y);
      }
  }

        /**
         * Reads in 2D vertex positions from file.
         * <p>Each line has entries separated by whitespace.
         * Any lines starting with the comment string (after any white space) are skipped.
         * Columns probably numbered with first being number 1.
         * @param fullFileName full file name of file with positions of vertices.
         * @param cc comment character, if first no white space characters are this string, then line is ignored.
         * @param xColumn reads in x position of vertices from this column
         * @param yColumn reads in y position of vertices from this column
         * @param headerOn ignore first line if true
         * @param infoOn true if want the input lines printed out as read in
         */
    public void setVertexPositionsFromFile(String fullFileName, String cc, int xColumn, int yColumn,
            boolean headerOn, boolean infoOn)
    {
        //String fullFileName=inputName.getNameRootFullPath()+ext;
        int [] columnReadList = {xColumn,yColumn};
        boolean forceLowerCase=false;
        ArrayList<String> coord = FileInput.readStringColumnsFromFile(fullFileName, cc,
                columnReadList, forceLowerCase, headerOn, infoOn);
        setVertexPositions(coord);
    }

      /**
   * Sets names for all vertices.
   * <br>Initialises new <tt>vertexLabelList</tt> if does not exist.
   * @param nameList list of names ss strings in order
   */
  public void setVertexNames(ArrayList<String> nameList)  {
      if ((nameList.size()!=TotalNumberVertices)  )
            throw new RuntimeException ("Wrong number of names, found "+nameList.size());
      if (!this.vertexlabels) initialiseVertexLabels();
      int c=0;
      String name="UNSET";
      for (int v=0; v<this.TotalNumberVertices; v++) {
          try{name=nameList.get(c++);}
          catch(Exception e){
              System.err.println("!!! WARNING setVertexNamess found vertex "+v+" name "+name);
              name="UNSET";
          }
          vertexLabelList[v].setName(name);
      }
  }


        /**
         * Reads in vertex names from file.
         * <p>Each line has entries separated by whitespace.
         * Any lines starting with the comment string (after any white space) are skipped.
         * Columns probably numbered with first being number 1.
         * @param ext extension to add to find file with names, inputNames.dat for stand alone files.
         * @param cc comment character, if first no white space characters are this string, then line is ignored.
         * @param nameColumn position of names from this column
         * @param headerOn ignore first line if true
         * @param infoOn true if want the input lines printed out as read in
         */
    public void setVertexNamesFromFile(String ext, String cc, int nameColumn, boolean headerOn, boolean infoOn)
    {
        String fullFileName=inputName.getNameRootFullPath()+ext;
        int [] columnReadList = {nameColumn};
        boolean forceLowerCase=false;
        ArrayList<String> nameList = FileInput.readStringColumnsFromFile(fullFileName, cc,
                columnReadList, forceLowerCase, headerOn, infoOn);
        setVertexNames(nameList);
    }


        /**
         * Reads in vertex numbers from file.
         * <p>Each vertex can have an integer label, its number, which is not necessarily
         * the same as its index.  For instance used for ranking vertices or forming partitions.
         * <br>If non-integer in list then sets number to timgraph.IUNSET
         * <p>Each line has entries separated by whitespace.
         * Any lines starting with the comment string (after any white space) are skipped.
         * Columns probably numbered with first being number 1.
         * @param ext extension to add to find file with names, inputNames.dat for stand alone files.
         * @param cc comment character, if first no white space characters are this string, then line is ignored.
         * @param numberColumn position of names from this column
         * @param headerOn ignore first line if true
         * @param infoOn true if want the input lines printed out as read in
         */
    public void setVertexNumbersFromFile(String ext, String cc, int numberColumn, boolean headerOn, boolean infoOn)
    {
        String fullFileName=inputName.getNameRootFullPath()+ext;
        int [] columnReadList = {numberColumn};
        boolean forceLowerCase=false;
        ArrayList<String> numberList = FileInput.readStringColumnsFromFile(fullFileName, cc,
                columnReadList, forceLowerCase, headerOn, infoOn);
        setVertexNumbers(numberList);
    }

  /**
   * Sets positions for all vertices.
   * <br>Initialises new <tt>vertexLabelList</tt> if does not exist.
   * <br>If non-integer in list then sets number to timgraph.IUNSET
   * @param numberList list of numbers as strings
   */
  public void setVertexNumbers(ArrayList<String> numberList)  {
      if ((numberList.size()!=TotalNumberVertices)  )
            throw new RuntimeException ("Wrong number of vertex numbers, found "+numberList.size());
      if (!this.vertexlabels) initialiseVertexLabels();
      int n=0;
      String numberstring=SUNSET;
      for (int v=0; v<this.TotalNumberVertices; v++) {
          numberstring=numberList.get(v);
          try{n=Integer.parseInt(numberstring);}
          catch(Exception e){
              System.err.println("!!! WARNING setNumberPositions found vertex "+v+" number "+numberstring);
              n=IUNSET;
          }
          vertexLabelList[v].setNumber(n);
      }
  }

    public void setVertexPropertiesFromFile(String ext, String cc, int numberColumn, boolean headerOn, boolean infoOn)
    {
        String fullFileName=inputName.getNameRootFullPath()+ext;
        int [] columnReadList = {numberColumn};
        boolean forceLowerCase=false;
        ArrayList<String> numberList = FileInput.readStringColumnsFromFile(fullFileName, cc,
                columnReadList, forceLowerCase, headerOn, infoOn);
        setVertexNumbers(numberList);
    }


//    /** 
//   * Sets edge weights to be 1/degree of source vertex.
//   * <br>Only really makes sense for bipartite graphs.
//     * Note that as always <tt>edgeValuetList[2e]</tt> and
//     * <tt>edgeValuetList[2e+1]</tt> are the same single object
//     * so changing one will change the other
//   */    
//  public void setEdgeWeights()  {
//      if (!weightedEdges){
//          weightedEdges=true;
//          edgeValuetList = new EdgeValue[maximumStubs];
//          for (int e=0; e<TotalNumberStubs; e++) {
//              EdgeValue ew = new EdgeValue();
//              edgeValuetList[e++]=ew;
//              edgeValuetList[e]=ew;
//          }
//      }
//  }       

 
  
  
// ***********************************************************************
// FILE INPUT ROUTINES
  
    // ........................................................................
    /**
     * Parse the parameters given in file.
     *@param fileName full name inclduing directory and extension for input file
     *@param CC skip lines whose first character is CC
     *@param sourceColumn column with source vertex
     * @param targetColumn column with target vertex 
     * @param weightColumn column with weight of edge
     *@return 0 if no problems
     * @deprecated used routines in FileInput class
     */   
    public int inputSimpleNetwork(String fileName, Character CC, int sourceColumn, int targetColumn, int weightColumn) {
      
      message.println(0,"Starting to search for range of vertex numbers in " + fileName);
      TextReader data;     // Character input stream for reading data.
      String tempstring="";
      Character datatype;
      int column=-1;
      int numberEdges=0;
      int linenumber=0;
      int result=0;
      String param="";
      int source=-2;
      int target=-2;
      double weight=-2.0;
      int minSource= MAXINT;
      int maxSource=-MININT;
      int minTarget=MAXINT;
      int maxTarget=MININT;
      try {  // Create the input stream.
         data = new TextReader(new FileReader(fileName));
      }
      catch (FileNotFoundException e) { 
         message.printWarning(-1,"Can't find file "+fileName);
         return 1;  
      }
        
        try{ 
          while (!data.eof()) 
          { // start reading new line
              linenumber++;
              column=0;
              datatype = data.peek(); //ignore first item, labels row
//            System.out.println("first word is "+datatype);
              // skip lines with first character CC
              if (datatype==CC)
              { // this line is a comment
                  tempstring=data.getln(); //comment read to end of line
                  message.println(2,linenumber+": Comment: "+tempstring);
                  continue;
              }
              // Treat as line of data
              source=-1; 
              target=-1; 
               while (!data.eoln()) { // read to end of line
                      if (column==sourceColumn) {source= data.getInt(); continue;}
                      if (column==targetColumn) {target= data.getInt(); continue;}
                      if (column==weightColumn) {weight= data.getDouble(); continue;}
                      param=data.getWord(); // always read the next bit of data in
                  }
              numberEdges++;
              minSource=Math.min(minSource,source);
              maxSource=Math.max(maxSource,source);
              minTarget=Math.min(minTarget,source);
              maxTarget=Math.max(maxTarget,source);
            }//eo while 
   
          message.println(0,"Finished reading from " + fileName);
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          message.printERROR("Input Error: line "+linenumber+", " + e.getMessage());
          result=1;
       }
       finally {
          // Finish by closing the files, 
          //     whatever else may have happened.
          data.close();
        }        
       int minVertex = Math.min(minSource,minTarget);
       int maxVertex = Math.max(maxSource,maxTarget);
       int numberVertices = maxVertex-minVertex+1;
       message.println(0,"Finished reading from " + fileName);
       message.println(0,"Found " + numberVertices + " vertices and "+numberEdges+" edges");
       inputSimpleNetwork(fileName, CC, sourceColumn, targetColumn, weightColumn, minVertex, numberVertices); 
       
       return 0;
    }   

    
        // ........................................................................
    /**
     * Reads in list of edges as vertex pairs and weights.
     *@param fileName full name inclduing directory and extension for input file
     *@param CC skip lines whose first character is CC
     *@param sourceColumn column with source vertex
     * @param targetColumn column with target vertex 
     * @param weightColumn column with weight of edge
     *@param minVertex minumum vertex number in file
     *@param numberVertices nmumber of vertices in file
     *@return 0 if no problems
     * @deprecated used routines in FileInput class
     */   
    public int inputSimpleNetwork(String fileName, Character CC, int sourceColumn, int targetColumn, int weightColumn, int minVertex, int numberVertices) {
      
      message.println(0,"Starting to read edges and weights from " + fileName);
      TextReader data;     // Character input stream for reading data.
      String tempstring="";
      Character datatype;
      int column=-1;
      int linenumber=0;
      int result=0;
      String param="";
      int source=-2;
      int target=-2;
      double weight=-2.0;
      int minSource= MAXINT;
      int maxSource=-MININT;
      int minTarget=MAXINT;
      int maxTarget=MININT;
      try {  // Create the input stream.
         data = new TextReader(new FileReader(fileName));
      }
      catch (FileNotFoundException e) { 
         message.printWarning(-1,"Can't find file "+fileName);
         return 1;  
      }
        
        try{ 
          while (!data.eof()) 
          { // start reading new line
              linenumber++;
              datatype = data.peek(); //ignore first item, labels row
//            System.out.println("first word is "+datatype);
              // skip lines with first character CC
              if (datatype==CC)
              { // this line is a comment
                  tempstring=data.getln(); //comment read to end of line
                  message.println(2,linenumber+": Comment: "+tempstring);
                  continue;
              }
              // Treat as line of data
              column=0;
              source=-1; 
              target=-1; 
               while (!data.eoln()) { // read to end of line
                      if (column==sourceColumn) {source= data.getInt(); continue;}
                      if (column==targetColumn) {target= data.getInt(); continue;}
                      if (column==weightColumn) {weight= data.getDouble(); continue;}
                      param=data.getWord(); // always read the next bit of data in
                  }
              minSource=Math.min(minSource,source);
              maxSource=Math.max(maxSource,source);
              minTarget=Math.min(minTarget,source);
              maxTarget=Math.max(maxTarget,source);

              message.printERROR("Line "+linenumber+" of unknowntype");
              result = -1;
            }//eo while 
   
          message.println(0,"Finished reading from " + fileName);
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          message.printERROR("Input Error: " + e.getMessage());
          result=1;
       }
       finally {
          // Finish by closing the files, 
          //     whatever else may have happened.
          data.close();
        }        
        return 0;
    }   

  // *******************************************************************
  /**
     * Outputs the degree distributions.
     *  <tt>Jdd.dat</tt> Degree Distribution, or
     *  <tt>Jndd.dat</tt> Normalised Degree Distribution 
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     * @param headersOn true if want header row for data columns
     */
    public void FileOutputDegreeDistribution(String cc, boolean normalise, boolean headersOn)  
    {
        //String filenamecomplete;
        String extension;

        if (normalise) extension=".Jndd.dat";
        else extension =".Jdd.dat";
        outputName.setNameEnd(extension);
        DDtotal.FileOutputDegreeDistribution(outputName.getFullFileName(), cc, SEP, normalise,  headersOn);
        if (directedGraph)
        {
            outputName.setNameEnd("IN"+extension);
            DDin.FileOutputDegreeDistribution(outputName.getFullFileName(), cc,SEP, normalise,  headersOn);
            outputName.setNameEnd("OUT"+extension);
            DDout.FileOutputDegreeDistribution(outputName.getFullFileName(), cc, SEP, normalise,  headersOn);
        }
        if (bipartiteGraph)
        {
            outputName.setNameEnd(nameVertexType1+extension);
            DD1total.FileOutputDegreeDistribution(outputName.getFullFileName(), cc,SEP, normalise,  headersOn);
            outputName.setNameEnd(nameVertexType2+extension);
            DD2total.FileOutputDegreeDistribution(outputName.getFullFileName(), cc, SEP, normalise,  headersOn);
        }
            
    }//eo FileOutputDegreeDistribution
    
  /**
     * Outputs the degree distributions.
     * <p>Extensions are <tt>lbdd.dat</tt> for log binned degree distribution 
     * @param cc comment characters put at the start of every line
     * @param lbratio ratio of top and bottom positions of bins 
     * @param infoOn true if want row of general information
     * @param headersOn true if want header row for data columns
     */
    public void FileOutputLogBinnedDegreeDistribution(String cc, double lbratio,  boolean infoOn, boolean headersOn)  
    {
        //String filenamecomplete;
        String extension="lbdd.dat";
        outputName.setNameEnd(extension);
        DDtotal.FileOutputLogBinnedDegreeDistribution(outputName.getFullFileName(), cc, SEP, lbratio,  infoOn, headersOn);
        if (directedGraph)
        {
            outputName.setNameEnd("IN"+extension);
            DDin.FileOutputLogBinnedDegreeDistribution(outputName.getFullFileName(), cc,SEP, lbratio,  infoOn, headersOn);
            outputName.setNameEnd("OUT"+extension);
            DDout.FileOutputLogBinnedDegreeDistribution(outputName.getFullFileName(), cc, SEP, lbratio,  infoOn, headersOn);
        }
        if (bipartiteGraph)
        {
            outputName.setNameEnd(nameVertexType1+extension);
            DD1total.FileOutputLogBinnedDegreeDistribution(outputName.getFullFileName(), cc,SEP, lbratio,  infoOn, headersOn);
            outputName.setNameEnd(nameVertexType2+extension);
            DD2total.FileOutputLogBinnedDegreeDistribution(outputName.getFullFileName(), cc, SEP, lbratio,  infoOn, headersOn);
        }
            
    }//eo FileOutputDegreeDistribution
    
  /**
     * Outputs the degree distribution information.
     *  inputDirName\fileinputNameRoot.Jdd.dat Degree Distribution, or
     *  inputDirName\fileinputNameRoot.Jndd.dat Normalised Degree Distribution 
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     */
    void printDegreeDistribution(String cc, boolean normalise)  
    {
        DDtotal.print(cc, SEP, normalise);
        if (directedGraph)
        {
            DDin.print(cc, SEP, normalise);
            DDout.print(cc, SEP, normalise);
        }
        if (bipartiteGraph)
        {
            DD1total.print(cc, SEP, normalise);
            DD2total.print(cc, SEP, normalise);
        }
            
    }//eo FileOutputDegreeDistribution

   
            
// --------------------------------------------------------
    
  /**
     * Outputs information for a connected Undirected graph.
     *  <fileinputNameRoot>.Jrdd.dat Reduced Degree Distribution, or
     *  <fileinputNameRoot>.Jnrdd.dat Normalised Reduced Degree Distribution
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     */
    void FileOutputReducedDegreeDistribution(String cc, boolean normalise)  {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        if (normalise) extension = ".Jnrdd.dat";
        else extension=".Jrdd.dat";
        outputName.setNameEnd(extension);
        filenamecomplete = outputName.getFullFileName();

        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(outputName.getFullFileName());
            PS = new PrintStream(fout);
            //Date date = new Date();
            int n=0;
            double p=0;
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            PS.println(cc+" No. vertices: "+SEP+ TotalNumberVertices+" No. edges: "+SEP+ TotalNumberStubs );
            if (TotalNumberVertices<1) return;
            if (normalise) PS.println(cc+" k "+SEP+"rp(k)    Reduced Normalised Degree Distribution ");
            else PS.println(cc+" k "+SEP+"rn(k)     Reduced Unnormalised Degree Distribution ");
            for (int k=0; k<rddarr.size(); k++)
            {
                if (normalise)  
              {
                  p = rddarr.get(k)/((double)TotalNumberVertices);
                  if (p>0) PS.println(k+SEP+p);
              }
              else 
              {
                  n = ((int) (rddarr.get(k)+0.5) );
                  if (n>0) PS.println(k+SEP+n);
              }
            }
            if (infoLevel>-2) System.out.println("Finished writing reduced degree distribution to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }
    

    
// --------------------------------------------------------
  /**
     * Outputs information for a connected Undirected graph.
     *  <fileinputNameRoot>.Jweightd.dat Weight Distribution
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     */
    void FileOutputWeightDistribution(String cc, boolean normalise)  {

        String filenamecomplete;
        PrintStream PS;
        String extension;

        if (normalise) extension = ".Jnwd.dat";
        else extension=".Jwd.dat";
        outputName.setNameEnd(extension);
        filenamecomplete= outputName.getFullFileName();
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            int n=0;
            double p;
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            PS.println(cc+" No. vertices: "+SEP+ TotalNumberVertices+" No. edges: "+SEP+ TotalNumberStubs );
            PS.println(cc+SEP+ weightmaximum+SEP+" = weightmaximum");
            if (normalise) PS.println(cc+" w "+SEP+"p(w)    Normalised Weight Distribution ");
            else PS.println(cc+" w "+SEP+"w(w)     Unnormalised Weight Distribution ");
            for (int w=0; w<weightdarr.size(); w++)
            {
              if (normalise)  
              {
                  p = weightdarr.get(w)/((double)TotalNumberVertices);
                  if (p>0) PS.println(w+SEP+p);
              }
              else 
              {
                  n = ((int) (weightdarr.get(w)+0.5) );
                  if (n>0) PS.println(w+SEP+n);
              }
            }
            if (infoLevel>-2) System.out.println("Finished writing weight distribution to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }


// *******************************************************************
  /**
     * Outputs component size distributions information for a connected Undirected graph.
     *  <fileinputNameRoot>.Jcompdist.dat 
     * @param cc comment characters put at the start of every line
     */
    void FileOutputComponentDistributionOLD(String cc)  {

        outputName.setNameEnd(".Jcompdist.dat");
        String filenamecomplete= outputName.getFullFileName();
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            int n=0;
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            PS.println(cc+" No. vertices: "+SEP+ TotalNumberVertices+" No. edges: "+SEP+ TotalNumberStubs );
            PS.println(cc+" walk mode: "+SEP+ randomWalk.randomWalkMode);
//            PS.println(cc+getrandomWalk.randomWalkModeString());
            PS.println(cc+"Number of Components "+SEP+componentSize.size());
            PS.println(cc+" c"+SEP+"n(c)"+SEP+"v in c"+SEP+"diam_min");
            for (int c=0; c<componentSize.size(); c++) 
              PS.println(c+SEP+componentSize.get(c)+SEP+componentSource.get(c)+SEP+componentDist.get(c));
            if (infoLevel>-2) System.out.println("Finished writing component distribution data to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }

// *******************************************************************
  /**
     * Outputs component size distributions information for a connected Undirected graph.
     *  <em>fileinputNameRoot</em><tt>.Jcompdist.dat</tt>
     * @param cc comment characters put at the start of every line
     */
    public void FileOutputComponentDistribution(String cc)  {

        outputName.setNameEnd(".Jcompdist.dat");
        String filenamecomplete = outputName.getFullFileName();
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            this.printComponentDistribution(PS);
            if (infoLevel>-2) System.out.println("Finished writing component information to "+ filenamecomplete);
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }

// -----------------------------------------------------------------------------
  /**
     * Outputs component size distributions information for a connected Undirected graph.
     *  <em>fileinputNameRoot</em><tt>.Jv2comp.dat</tt>
     * @param cc comment characters put at the start of every line
     */
    public void FileOutputVertexToComponentList(String cc)  {

        outputName.setNameEnd(".Jv2comp.dat");
        String filenamecomplete = outputName.getFullFileName();
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            this.printVertexToComponentList(PS);
            if (infoLevel>-2) System.out.println("Finished writing component information to "+ filenamecomplete);
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }

// -----------------------------------------------------------------------------
  /**
     * Outputs brief info on component sizes.
     * <br>Brief summary of components.
     *  <em>fileinputNameRoot</em><tt>.Jcompinfo.dat</tt>
     * @param componentDistribution true if want distribution of components
     */
    public void FileOutputComponentInfo(String cc)  {
        FileOutputComponentInfo(cc,false,false);
    }
  /**
     * Outputs info on component sizes.
     * <br>Brief summary with optional long detailed information
     *  <em>fileinputNameRoot</em><tt>.Jcompinfo.dat</tt>
     * @param componentDistribution true if want distribution of components
     * @param vertexToComponent true if want list of vertices with their component labels
     * @param cc comment characters put at the start of every line
     */
    public void FileOutputComponentInfo(String cc, boolean componentDistribution, boolean vertexToComponent)  {

        outputName.setNameEnd(".Jcompinfo.dat");
        String filenamecomplete = outputName.getFullFileName();
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            //printComponentInfo(PS);
            printComponentInfo(PS, componentDistribution, vertexToComponent);
            if (infoLevel>-2) System.out.println("Finished writing component information to "+ filenamecomplete);
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }    
// *******************************************************************
  /**
     * Outputs ring information for a  graph.
     *  <em>fileinputNameRoot</em><tt>.Jrinfo.dat</tt>
     * @param cc comment characters put at the start of every line
     */
    void FileOutputRingInfo(String cc, int source)  {

        outputName.setNameEnd(".Jrinfo.dat");
        String filenamecomplete = outputName.getFullFileName();
        //String filenamecomplete = inputDirName+inputNameRoot+ ".Jrinfo.dat";
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            printRingInfo(PS,source);
            if (infoLevel>-2) System.out.println("Finished writing ring information to "+ filenamecomplete);
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }



// *******************************************************************
  /**
     * Outputs information on distance distributions.
     *  <em>fileinputNameRoot</em><tt>.Jtotdistd.dat</tt>
     * @param cc comment characters put at the start of every line
     */
    public void FileOutputDistanceTotalDistribution(String cc)  
    {
        outputName.setNameEnd(".Jtotdistd.dat");
        String filenamecomplete = outputName.getFullFileName();
        //String filenamecomplete = inputDirName+inputNameRoot+ ".Jtotdistd.dat";
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
             printDistanceTotalDistribution(PS, cc);
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        if (infoLevel>-2) System.out.println("Finished writing total distance data to "+ filenamecomplete);
    }

   

 // *******************************************************************
  /**
     * Prints information on distance distribution.
     *@param PS PrintStream for output such as System.out
     *@param cc comment characters put at the start of every line
     */
    public void printDistanceTotalDistribution(PrintStream PS, String cc)  {

        

        // next bit of code p327 Schildt and p550
        PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            PS.println(cc+" No. vertices: "+SEP+ TotalNumberVertices+" No. edges: "+SEP+ TotalNumberStubs );
            PS.println(cc+" walk mode: "+SEP+ ((randomWalk==null)?" NOT DEFINED":randomWalk.randomWalkMode));
            PS.println(cc+"diameter maximum, minimum, average "+SEP+diametermax+SEP+diametermin+SEP+diameteraverage+SEP+" +/- "+diametererror+SEP+", sigma "+diametersigma);
            PS.println(cc+"average distance "+SEP+totdistanceaverage+SEP+" +/- "+SEP+totdistanceerror+", sigma "+SEP+totdistancesigma);
            PS.println(cc+"Average distance from one vertex "+SEP+onesdistanceav+SEP+" +/- "+SEP+onesdistanceerror+SEP+", sigma="+SEP+onesdistancesigma);  // average and error over samples 
            PS.println(cc+" d"+SEP+" n(d) "+SEP+" n2(d) "+ SEP+"<n(d)>"+SEP+" +/- "+SEP+"["+nsamples+" samples]");
            for (int d=0; d<totdistancedist.size(); d++) 
            {
                double n  = totdistancedist.get(d);
                double n2 = totdistance2dist.get(d);
                double nav = ((double) n)/((double) nsamples);
                double naverr = calcError(n,n2,nsamples);
                PS.println(d+SEP+n+SEP+n2+SEP+nav+SEP+naverr);
            }
            

    }

    // *******************************************************************
  /**
     * Outputs information for a connected Undirected graph.
     *  <fileinputNameRoot>.JCCd.dat general info
     * @param cc comment characters put at the start of every line
     */
    public void FileOutputCC(String cc)  {

        outputName.setNameEnd(".JCCd.dat");
        String filenamecomplete = outputName.getFullFileName();
        //String filenamecomplete = inputDirName+inputNameRoot+ ".JCCd.dat";
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            int n=0;
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            PS.println(cc+" No. vertices: "+SEP+ TotalNumberVertices+" No. edges: "+SEP+ TotalNumberStubs );
            PS.println(cc+" walk mode: "+SEP+ randomWalkMode);
//            PS.println(cc+getrandomWalk.randomWalkModeString());
            PS.println(cc+"Average CC "+SEP+CCaverage+SEP+" +/- "+SEP+CCerror+", sigma "+SEP+CCsigma);
            PS.println(cc+"Average Edge CC "+SEP+CCEdgeaverage+SEP+" +/- "+SEP+CCEdgeerror+", sigma "+SEP+CCEdgesigma);
            if (infoLevel>-2) System.out.println("Finished writing average CC info to "+ filenamecomplete);
            
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }


// *******************************************************************
  /**
     * Outputs information for a connected Undirected graph.
     *  <fileinputNameRoot>.Jdiststat.dat general info
     * @param cc comment characters put at the start of every line
     */
    public void FileOutputDistanceStatistics(String cc)  {

       outputName.setNameEnd(".Jdiststat.dat");
        String filenamecomplete = outputName.getFullFileName();
        //String filenamecomplete = inputDirName+inputNameRoot+ ".Jdiststat.dat";
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            //Date date = new Date();
            PS.println(cc+" timgraph.java version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            PS.println(diametermax+SEP+diametermin+SEP+diameteraverage+SEP+diametererror+SEP+diametersigma+SEP+cc+"diameter maximum, minimum, average , error in av, sigma for average");
            PS.println(totdistanceaverage+SEP+totdistanceerror+SEP+totdistancesigma+SEP+cc+" average distance over all paths:  average, error, sigma");
            PS.println(onesdistanceav+SEP+onesdistanceerror+SEP+onesdistancesigma+SEP+cc+" average distance from one vertex: average, error, sigma");  // average and error over samples 
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}
            if (infoLevel>-2) System.out.println("Finished writing distance statistics to "+ filenamecomplete);

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }



    
// -----------------------------------------------------------------------       
    /** 
     * Outputs information for a connected Undirected graph.
     * If outputControl is non zero then outputs general information on graph to
     * <emph>fileinputNameRoot</emph><kbd>info.dat<kbd>.
     * @param cc comment characters put at the start of every line
     * @param timetaken is time for run
     */
    void FileOutputGraphInfo(String cc, double timetaken)  {
        
        if (outputControl.getNumber()==0) return;
        outputName.setNameEnd("info.dat");
        String filenamecomplete = outputName.getFullFileName();
        //String filenamecomplete = inputDirName+inputNameRoot +".Jinfo.dat";
        PrintStream PS;
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            OutputGraphInfo(PS, cc, timetaken);
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            if (infoLevel>-2) System.out.println("Finished writing general graph info to "+ filenamecomplete);
   
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filenamecomplete);
            return;
        }
        return;
    }

    // ----------------------------------------------------------------------
        /** 
         *  Outputs to file all aspects of a graph.
         * If <tt>averageWalkLength&lt;0</tt> then sets this value automatically.
         *  Used to set length of walks made.
         * If <tt>rankingProbabilityLengthScale&lt;0</tt> then sets this value automatically.
         * @param CC comment characters put at the start of every line
         */
    public void outputGraph(String CC) 
    {
        calcDegreeDistribution();
        printDegreeDistribution(CC, false);
        FileOutputDegreeDistribution(CC, false, true);
        if (TotalNumberVertices>=LogBinVerticesMinimum) FileOutputLogBinnedDegreeDistribution(CC, 1.1, true, true);
        FileOutput fo = new FileOutput(this);
        fo.pajek();
        calcDistanceSample(0.01,10, 100);
        FileOutputDistanceStatistics(CC);
        FileOutputDistanceTotalDistribution(CC);
        calcComponents();
        FileOutputComponentDistribution(CC);
        calcRanking();
        boolean printTriangles=true;
        if (TotalNumberStubs<40) FileOutputNetwork(true,  true, true);
        else FileOutputVertices(false, false, false);
        FileOutputPajekVertexData();
        calcCCEdgeSample(0.01, 100, 10000);
        FileOutputCC(CC);
        FileOutputGraphInfo(CC,0);
        
    }
    
    

        
    
// -----------------------------------------------------------------------       
    /** 
     * Deprecated version, use printOutputGraph.  
     * @param PS PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     * @param timetaken time for run
     */
    public void OutputGraphInfo(PrintStream PS, String cc, double timetaken)  
    {
        printWalkGraphInfo( PS, cc, timetaken);
    }

// -----------------------------------------------------------------------       
    /** 
     * Outputs information for a connected Undirected graph.
     * Output only if global infoLevel>0.
     * @param PS PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     * @param timetaken is time for run, if negative this is ignored.
     */
    public void printWalkGraphInfo(PrintStream PS, String cc, double timetaken)  
    {    
            if (infoLevel<0) return;
            //Date date = new Date();
            PS.println(cc+" timgraph.java"+SEP+"version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            PS.println(cc+"Graph type:"+SEP+graphType(SEP));
            printRandomWalkMode(PS,cc);
            if (timetaken>=0) PS.println(timetaken+SEP+cc+" Time taken sec. ");
            PS.println(numevents+SEP+cc+" Number of events"  );
            PS.println(probnewvertex+SEP+cc+" input probability of new vertex "  );
            PS.println(TotalNumberVertices+SEP+cc+" actual No. vertices"  );
            PS.println(TotalNumberStubs +SEP+cc+" actual No. edges");
            PS.println(connectivity +SEP+cc+" k: input average degree requested");
            PS.println(averageWalkLength +SEP+cc+" averageWalkLength: number of random walk averageWalkLength on average");
            PS.println(binomialNumber +SEP+cc+" no. of dice used in binomial random numbers generator");
            PS.println(averagesteplength +SEP+cc+" average no. averageWalkLength per walk");
            PS.println(edgegenerator+SEP+cc+"edge generator");
            PS.println(randomWalkMode+SEP+cc+"walk mode");
            if (vertexlabels && this.minimumVertexLabel.hasPosition()) PS.println(minimumVertexLabel.position.printString(SEP)+SEP+cc+"minimum vertex coordinate");
            if (vertexlabels && this.maximumVertexLabel.hasPosition()) PS.println(maximumVertexLabel.position.printString(SEP)+SEP+cc+"maximum vertex coordinate");
            PS.println(initialgraph +SEP+cc+" initial graph type "+this.initialGraphString(initialgraph));
            PS.println(initialConnectivity +SEP+cc+" initial graph Connectivity");
            PS.println(initialVertices +SEP+cc+" initial graph Vertices");
            PS.println(initialXsize +SEP+cc+" initial graph X size");
            PS.println(initialYsize +SEP+cc+" initial graph Y size");
            PS.println(initialEdges +SEP+cc+" initial graph Edges ");
            
            return;   
    
    }
    
// -----------------------------------------------------------------------       
    /** 
     * Outputs information for a connected Undirected graph
     *  
     * @param PS PrintStream such as System.out
     * @param cc comment characters put at the start of every line
     */
    public void printGeneralGraphInfo(PrintStream PS, String cc)  
    {    
            PS.println(cc+" timgraph.java"+SEP+"version "+SEP+VERSION+SEP+" produced on "+SEP+date);
            PS.println(cc+"Graph type:"+SEP+graphType(SEP));
            PS.println(numevents+SEP+cc+" Number of events"  );
            PS.println(probnewvertex+SEP+cc+" input probability of new vertex "  );
            PS.println(TotalNumberVertices+SEP+cc+" actual No. vertices"  );
            if (bipartiteGraph) PS.println(numberVertexType1+cc+" No. "+this.nameVertexType1+" (type 1) vertices");
            if (bipartiteGraph) PS.println(numberVertexType2+cc+" No. "+this.nameVertexType2+" (type 2) vertices");
            PS.println(TotalNumberStubs +SEP+cc+" actual No. edges");
            PS.println(connectivity +SEP+cc+" k: input average degree requested");
            PS.println(averageWalkLength +SEP+cc+" averageWalkLength: number of random walk averageWalkLength on average");
            PS.println(averagesteplength +SEP+cc+" average no. averageWalkLength per walk");
            PS.println(edgegenerator+SEP+cc+"edge generator");
            if (vertexlabels) PS.println(minimumVertexLabel.position.printString(SEP)+SEP+cc+"minimum vertex coordinate");
            if (vertexlabels) PS.println(maximumVertexLabel.position.printString(SEP)+SEP+cc+"maximum vertex coordinate");
            return;   
    
    }

    
//    /**
//     * 
//     * @param ep
//     */
//  public void FileOutputEdgePartitionGraphML(VertexPartition ep){
//        FileOutput fo = new FileOutput(this);
//        fo.graphMLEdgePartition(ep);
//    }
//
    
// -----------------------------------------------------------------------       
    /** 
     * Returns string with type of graph.
     * @param separator string used to separate items, e.g. use comman or tab
     * @return string type of graph
     */
    public String graphType(String separator)  
    {
        String s="";
        if (directedGraph) s="directed";
        else s="undirected";
        if (vertexlabels) s=s+separator+"vertex labels";
        else s=s+separator+"no vertex labels";
        if (weightedEdges) s=s+separator+"edge weights";
        else s=s+separator+"no edge weights";
        if (bipartiteGraph) s=s+separator+"bipartite";
        else s=s+separator+"unipartite";
        return s;
    }
    
// --------------------------------------------------------------------------  
        
    /**
     * @deprecated Use routines in FileOutput
     */
    public void FileOutputGraphVizOld(){
        GraphViz gv = new GraphViz();
        PrintStream PS;
        FileOutputStream fout;
        outputName.setNameEnd(".gv");
        String filenamecomplete = outputName.getFullFileName();
        //String filenamecomplete = inputDirName+inputNameRoot+".gv";
        if (infoLevel>-2) System.out.println("Writing GraphViz file to "+ filenamecomplete);
            try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            gv.output(PS, this, true);

            if (infoLevel>-2) System.out.println("Finished writing GraphViz file to "+ filenamecomplete);
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +filenamecomplete);}
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filenamecomplete);
            return;
        }
        return;

        
    }
        
// ----------------------------------------------------------------------
    /** 
     * Output in Pajek format. 
     *   inputDirName+inputNameRoot+.net is the file with the walk graph in Pajek format
     * @deprecated Use routines in FileOutput
     */
    void FileOutputPajekOLD()  {
        outputName.setNameEnd(".net");
        String filenamecomplete = outputName.getFullFileName();
        //String filenamecomplete = inputDirName+inputNameRoot +".net";
        PrintStream PS;
        int ew,ec;
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            PS.println("*Vertices "+TotalNumberVertices);
            for (int v=1; v<=TotalNumberVertices; v++) 
            {
                PS.print(v);
                if (vertexlabels) PS.print(" "+vertexLabelList[v-1].pajekString(minimumVertexLabel.position,maximumVertexLabel.position));
                //PS.print(" "+vertexLabelList[v-1].pajekString());
                PS.println();
            }
            // .net  format for the arcs
            //      1      2       1 c Blue
            // gives an arc between vertex 1 and 2, value 1 colour blue
            if (directedGraph) PS.println("*Arcs");
            else PS.println("*Edges");           
            for (int e=0; e<TotalNumberStubs; e+=2)  
                {
                    PS.print(  (stubSourceList[e]+1)+"   "
                               + (stubSourceList[e+1]+1) );
                    if (weightedEdges) PS.println("  " 
                               + edgeValuetList[e].weight
                               + "  c " + getPajekColour(edgeValuetList[e].label) );
                    else PS.println();
                }
            

            if (infoLevel>-2) System.out.println("Finished writing Pajek file to "+ filenamecomplete);
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filenamecomplete);
            return;
        }
        return;
    }

     
//// --------------------------------------------------------------------------
//    /**
//     * Output in Adjacency Matrix.
//     *   inputDirName+inputNameRoot+.net is the file with the walk graph in Pajek format
//     * @deprecated MOve this to FileOutput class
//     */
//     void FileOutputAdjacencyMatrix()  {
//         AdjacencyMatrix am = new AdjacencyMatrix(TotalNumberVertices, TotalNumberStubs, stubSourceList, directedGraph);
//         outputName.setNameEnd("adjmat.dat");
//        String filenamecomplete = outputName.getFullFileName();
//        //String filenamecomplete = inputDirName+inputNameRoot +"adjmat.dat";
//        PrintStream PS;
////        int ew,ec;
//        // next bit of code p327 Schildt and p550
//        FileOutputStream fout;
//        try {
//            fout = new FileOutputStream(filenamecomplete);
//            PS = new PrintStream(fout);
//            am.printMatrix(PS,this.SEP,false);
//            if (infoLevel>-2) System.out.println("Finished writing Adjacency Matrix file to "+ filenamecomplete);
//            try{ fout.close ();
//               } catch (IOException e) { System.out.println("File Error with " +filenamecomplete);}
//
//        } catch (FileNotFoundException e) {
//            System.out.println("Error opening output file "+filenamecomplete);
//            return;
//        }
//        return;
//    }
       
// --------------------------------------------------------------------------    
    /** 
     * Output all vertex data in Pajek formats .
     *   inputDirName+inputNameRoot+.vec is the file with the vertex data in Pajek format
     * @deprecated Move this to FileOutput class
     */
     public void FileOutputPajekVertexData()  
     {
        for (int param=0;param<3;param++)
        {
            FileOutputPajekVertexVector(param);              
            FileOutputPajekVertexPartition(param,10);                    
        }
        return;
    }

// --------------------------------------------------------------------------    
    /** 
     * Output vertex vector data in Pajek formats.
     * 
     *   inputDirName+inputNameRoot+.vec is the file with the vertex data in Pajek format
     *@param param chooses which list to output
     * @deprecated Move this to FileOutput class
     */
     void FileOutputPajekVertexVector(int param)  
     {
        outputName.setNameEnd("ERROR");
//        String filenamecomplete = outputName.getFullFileName();
//        String filenamecomplete = inputDirName+inputNameRoot+"ERROR";
        switch (param)
            {
                case 2:         
                    outputName.setNameEnd("value2.vec");
                    break;
                case 1:         
                    outputName.setNameEnd("value.vec");
                    break;
                case 0:         
                    outputName.setNameEnd("visits.vec");
                    break;
            }
        PrintStream PS;
        int ew,ec;
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(outputName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println("*Vertices "+TotalNumberVertices);
            double maxRankVisits = maximumVertexLabel.getRank().visits;
            double maxRankValue  = maximumVertexLabel.getRank().value;
            double maxRankValue2 = maximumVertexLabel.getRank().value2;
            for (int v=1; v<=TotalNumberVertices; v++) switch (param)
            {
                case 0:         
                       PS.println(" "+((double) vertexLabelList[v-1].rank.visits)/(maxRankVisits) );
                       break;
                case 1:         
                       PS.println(" "+((double) vertexLabelList[v-1].rank.value)/( maxRankValue) );
                       break;
                case 2:         
                       PS.println(" "+((double) vertexLabelList[v-1].rank.value2)/( maxRankValue2) );
                       break;
            }
            if (infoLevel>-2) System.out.println("Finished writing vertex info to Pajek style file "+ outputName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+outputName.getFullFileName());
            return;
        }
        return;
    }

// --------------------------------------------------------------------------    
    /** 
     * Output vertex partition data in Pajek formats.
     * 
     *   inputDirName+inputNameRoot+.clu is the file with the vertex data in Pajek format
     *@param param chooses which list to output
     *@param scale for the maximum vertex value
     * @deprecated Move this to FileOutput class
     */
     void FileOutputPajekVertexPartition(int param, int scale)  
     {
         outputName.setNameEnd("ERROR");
        switch (param)
            {
                case 2:         
                    outputName.setNameEnd("value2.clu");
                    break;
                case 1:         
                    outputName.setNameEnd("value.clu");
                    break;
                case 0:         
                    outputName.setNameEnd("visits.clu");
                    break;
            }
        PrintStream PS;
        int ew,ec;
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(outputName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println("*Vertices "+TotalNumberVertices);
            double maxRankVisits = maximumVertexLabel.getRank().visits;
            double maxRankValue  = maximumVertexLabel.getRank().value;
            double maxRankValue2 = maximumVertexLabel.getRank().value2;
            for (int v=1; v<=TotalNumberVertices; v++) switch (param)
            {
                case 0:         
                       PS.println(" "+ ((int) (0.5+ ( scale*vertexLabelList[v-1].rank.visits)/(maxRankVisits) )));
                       break;
                case 1:         
                       PS.println(" "+((int) (0.5+ ( scale*vertexLabelList[v-1].rank.value)/( maxRankValue) )));
                       break;
                case 2:         
                       PS.println(" "+((int) (0.5+ ( scale*vertexLabelList[v-1].rank.value2)/( maxRankValue2) )));
                       break;
            }
            if (infoLevel>-2) System.out.println("Finished writing vertex info to Pajek style file "+ outputName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+outputName.getFullFileName());
            return;
        }
        return;
    }



// --------------------------------------------------------------------------    
    /** 
     *  Output in Pajek format .
     *   inputDirName+inputNameRoot+.net is the file with the walk graph in Pajek format
     * @deprecated Move this to FileOutput class
     */
     void FileOutputPajekUnweighted()  {
        outputName.setNameEnd("w.net");
        String filenamecomplete = outputName.getFullFileName();
        //String filenamecomplete = inputDirName+inputNameRoot +"w.net";
        PrintStream PS;
        int ew,ec;
        //setColours();
        int numberColours=pajekColour.length+1;
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            PS.println("*Vertices "+TotalNumberVertices);
            for (int v=1; v<=TotalNumberVertices; v++) PS.println(v);
            PS.println("*Arcs");
            for (int v=0; v<TotalNumberVertices; v++) {
                for(int e=0; e<rvertexList[v].size(); e++) 
                {
                    ew = strengthlist[v].get(e);  
                    ec = ew%(numberColours+1);
                    PS.println((v+1)+"   "+(rvertexList[v].get(e)+1) +"   " + ew + "  c "+this.getPajekColour(ew));
                }
            }                        
            if (infoLevel>-2) System.out.println("Finished writing weighted Pajek file to "+ filenamecomplete);
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filenamecomplete);
            return;
        }
        return;
    }

 // -------------------------------------------------------------------
//  /**
//     * Sets up colours for Pajek. 
//     */
//    void setColours()  
//    {
//        //int numberColours=10;
//
//        //if (pajekColour==null) pajekColour = new String[numberColours+1];
////                pajekColour[0] = "White"; // always have this as zero
////        pajekColour[1] = "Yellow";
////        pajekColour[2] = "Pink";
////        pajekColour[3] = "Cyan";
////        pajekColour[4] = "Orange";
////        pajekColour[5] = "Magenta";
////        pajekColour[6] = "Purple";
////        pajekColour[7] = "Green";
////        pajekColour[8] = "Blue";
////        pajekColour[9] = "Brown";
////        pajekColour[numberColours] = "Black";
////        pajekColour[1] = "Red";
////        pajekColour[12] = "Gray";
//        
//    }   
//    
 // .................................................................
  /**
     * Returns colours for Pajek.
   * <br>Negative colours and zero are set to be white.  
   * Any postive integer will give a valid colour as colours will
   * be repeated by cycling around the non-white colours.
   * <br>Colours are stored in static final String array.
     *@param c integer colour number
     */
    public String getPajekColour(int c)  
    {
        if (c<=0) return pajekColour[0];
        return pajekColour[1+((c-1)%(pajekColour.length-1))];
    }   
 
    
    
    /**
     * Tersts to see if this is an argument for <tt>tiumgraph</tt>.
     * @param s string to test
     * @return true if it starts with {@value #TIMGRAPH_ARGUMENT}
     */public static boolean isTIMGRAPH_ARGUMENT(String s){
        return (s.charAt(0)==TIMGRAPH_ARGUMENT?true:false);
    }
    /**
     * Tests to see if this is an argument to be passed to another class.
     * @param s string to test
     * @return true if it starts with character indicating an argument for a different class
     */
    public static boolean isOtherArgument(String s){
        for (int c=0; c<NOT_TIMGRAPH_ARGUMENT.length; c++)
                    if ((s.charAt(0) ==NOT_TIMGRAPH_ARGUMENT[c])) return true;
        return false;
    }
    /**
     * String giving characters used to indicate arguments for other classes.
     * @param sep string used to spearate the characters.
     * @return string giving characters used to indicate arguments for other classes.
     */
    public static String otherArguments(String sep){
        String s=""+NOT_TIMGRAPH_ARGUMENT[0];
        for (int c=1; c<NOT_TIMGRAPH_ARGUMENT.length; c++)
                    s=s+sep+NOT_TIMGRAPH_ARGUMENT[c];
        return s;
    }
// *********************************************************************
    
    
    /** 
     * Parses command arguments.
     * <br><tt>timgraph</tt> commands begin with -, anything starting with * is ignored, 
     * otherwise an error is generated.
     * @param ArgList array of strings containing arguments
     *  @return 0 if no problem, -1 if error, 1 is argument list printed
     */
    public int parseParam(String[] ArgList)  {
//        System.out.println(args.length+" command line arguments");
        if (infoLevel>-1) for (int j =0; j<ArgList.length; j++){System.out.println("Argument "+j+" = "+ArgList[j]);}
        int i=-1;
        try{
                for (i=0;i<ArgList.length ;i++){
                    parseParam(ArgList[i]);
                }// for i
                
        } catch (RuntimeException e){
            System.err.println(" parseParameter problem at argument "+i+": "+e.getMessage());
            printUsage();
            throw new RuntimeException("parseParameter problem: "+e.getMessage());
        } 
        
        // test directories
        if (!inputName.testDirectoryRoot()) {
            throw new IllegalArgumentException("*** Error input directory root " + inputName.getDirectoryRoot() + " is not a directory");
        }
        if (!inputName.testDirectoryFull()) {
            throw new IllegalArgumentException("*** Error full input directory " + inputName.getDirectoryFull() + " is not a directory");
        }
        if (!outputName.testDirectoryRoot()) {
            throw new IllegalArgumentException("*** Error output directory root " + outputName.getDirectoryRoot() + " is not a directory");
        }
        if (!outputName.testDirectoryFull()) {
            throw new IllegalArgumentException("*** Error full output directory " + outputName.getDirectoryFull() + " is not a directory");
        }
        
        // now parse any param.dat file
        parseParameterFile();
            
    return 0;
    
    }//eo ParamParse

    /**
     * Reads in a list of strings and parses them as timgraph parameters.
     * <p>This uses the current <tt>inputName</tt> to set the directories
     * and takes the file name ending to be <tt>param.dat</tt>
     */
    public void parseParameterFile(){
             FileNameSequence parameterFileName = new FileNameSequence(inputName);
             parameterFileName.setNameEnd("param.dat");
             if (parameterFileName.testFullFileName()) {
                ArrayList<String> words = FileInput.readStringList(parameterFileName.getFullFileName()); 
                if (infoLevel>-1) {
                   for( int count=0; count<words.size();count++) System.out.println("File Argument "+count+" = "+words.get(count));
                }
                for (String p: words) parseParam(p);
             }
             else{
                 if (infoLevel>-1) System.out.println(" Parameter file "+parameterFileName.getFullFileName()+" not found");
             }
    }
    
    /**
     * Parse a single string as an argument.
     * @param argument string to be parsed
     */
    private void parseParam(String argument){
                            
                    if (isOtherArgument(argument)) {
                        System.out.println("!!! Ignoring argument \""+argument+"\" as it starts with character indicating a different class argument "+argument);
                        return;
                    }   
//                    if (argument.length() <3) {
//                        throw new IllegalArgumentException("*** Argument "+i+" is too short "+argument);
//                    }
                    if (!isTIMGRAPH_ARGUMENT(argument)){
                            throw new IllegalArgumentException("*** Argument \""+argument+"\" does not start with "+otherArguments(", ")+" or "+TIMGRAPH_ARGUMENT);
                        }
                            switch (argument.charAt(1)) {
                                case 'e': {numevents = Integer.parseInt(argument.substring(2));
                                break;}
                                case 'f': {
                                    if (argument.charAt(2)=='i' )
                                    {
                                    if (argument.charAt(3)=='n' ) if (!inputName.setFullFileName(argument.substring(4),InputFileType.extensionList)) {
                                        throw new IllegalArgumentException("*** Full file name \""+argument.substring(4)+"\" does not have recognised ending\n"+InputFileType.listAllExtensions(" - ","\n"));
                                    }
                                    if (argument.charAt(3)=='r' ) inputName.setNameRoot(argument.substring(4));
                                    if (argument.charAt(3)=='d' ) inputName.setDirectoryRoot(argument.substring(4));
                                    if (argument.charAt(3)=='s' ) inputName.setDirectoryEnd(argument.substring(4));
                                    if (argument.charAt(3)=='e' ) inputName.setNameEnd(argument.substring(4));                                        
                                    }
                                    if (argument.charAt(2)=='o' )
                                    {
                                    if (argument.charAt(3)=='n' ) outputName.setFullFileNameNoEnding(argument.substring(4));
                                    if (argument.charAt(3)=='r' ) outputName.setNameRoot(argument.substring(4));
                                    if (argument.charAt(3)=='d' ) outputName.setDirectoryRoot(argument.substring(4));
                                    if (argument.charAt(3)=='s' ) outputName.setDirectoryEnd(argument.substring(4));
                                    if (argument.charAt(3)=='e' ) outputName.setNameEnd(argument.substring(4));                                        
                                    }
                                break;}
                                case 'g': { 
                                    if (argument.charAt(2)=='v' ){
                                        if (argument.charAt(3)=='e' ) vertexEdgeListOn = (argument.charAt(4) =='t'?true:false);
                                        if (argument.charAt(3)=='l' ) vertexlabels = (argument.charAt(4) =='t'?true:false);
                                        if (argument.charAt(3)=='v' ) initialVertices = Integer.parseInt(argument.substring(4));
                                    } 
                                    if (argument.charAt(2)=='e' ){
                                        if (argument.charAt(3)=='l' ) labelledEdges = (argument.charAt(4) =='t'?true:false);
                                        if (argument.charAt(3)=='m' ) multiEdge = (argument.charAt(4) =='t'?true:false);
                                        if (argument.charAt(3)=='s' ) selfLoops = (argument.charAt(4) =='t'?true:false);
                                        if (argument.charAt(3)=='w' ) weightedEdges = (argument.charAt(4) =='t'?true:false);
                                    }
                                    if (argument.charAt(2)=='b' ) bipartiteGraph = (argument.charAt(3) =='t'?true:false);
                                    if (argument.charAt(2)=='1' ) nameVertexType1 = (argument.substring(3));
                                    if (argument.charAt(2)=='2' ) nameVertexType2 = (argument.substring(3));
                                    if (argument.charAt(2)=='d' ) directedGraph = (argument.charAt(3) =='t'?true:false);
                                    if (argument.charAt(2)=='n' ) initialgraph = Integer.parseInt(argument.substring(3));
                                    if (argument.charAt(2)=='m' ) initialConnectivity = Integer.parseInt(argument.substring(3));
                                    if (argument.charAt(2)=='x' ) initialXsize = Integer.parseInt(argument.substring(3));
                                    if (argument.charAt(2)=='y' ) initialYsize = Integer.parseInt(argument.substring(3));
                                break;}
                                case 'k': {connectivity = Double.parseDouble(argument.substring(2))/2.0;
                                break;}
                                case 'l': {
                                    if (argument.charAt(2)=='w' ) averageWalkLength=Double.parseDouble(argument.substring(3));
                                    if (argument.charAt(2)=='r' ) rankingProbabilityLengthScale=Double.parseDouble(argument.substring(3));
                                break;}
                                case 'm': {connectivity =Double.parseDouble(argument.substring(2));
                                           System.err.println("!!! Warning: Argument "+argument+" is -im is deprecated, use -k for degree instead");
                                break;}
                                case 'o': {outputControl.set(Integer.parseInt(argument.substring(2)));
                                break;}
                                case 'p': {probnewvertex= Double.parseDouble(argument.substring(2));
                                break;}
                                case 'v': {
                                    if (argument.charAt(2)=='w' ) randomWalk.randomWalkMode=Integer.parseInt(argument.substring(3));
                                    if (argument.charAt(2)=='e' ) edgegenerator=Integer.parseInt(argument.substring(3));
                                    if (argument.charAt(2)=='e' ) binomialNumber=Integer.parseInt(argument.substring(3));
                                break;}
                                case 'x': 
                                {
                                    if (argument.charAt(2)=='i' ) {infoLevel = Integer.parseInt(argument.substring(3));
                                    message.setInformationLevel(infoLevel);
                                    }
                                    if (argument.charAt(2)=='v' ) maximumVertices = Integer.parseInt(argument.substring(3));
                                    if (argument.charAt(2)=='s' ) maximumStubs = Integer.parseInt(argument.substring(3));
                                    if (argument.charAt(2)=='e' ) maximumStubs = Integer.parseInt(argument.substring(3))*2;
                                    break;
                                }
// --------------------------- Deprecated args
//                                case 'd': {inputDirName = argument.substring(2);  break;}
//                                case 'i': {infoLevel = Integer.parseInt(argument.substring(2));  break;}
//                                case 'n': {maximumVertices = Integer.parseInt(argument.substring(2)); break;}
//                                case 'q': {probrndvertex= Double.parseDouble(argument.substring(2));
//                                break;}
                                //                    case 'z': {//FullInfoOn=Integer.parseInt(argument.substring(2));
                                //                              break;}
                                //case 't': {initialGraph=Integer.parseInt(argument.substring(2));
                                //                              break;}

// --------------------------- end of deprecated args
                                case '?': {printUsage();
                                           System.exit(1);}
                                default:{
                                    throw new IllegalArgumentException("Unknown argument \""+argument+"\"");
                                }
                            }

    }
    
// -------------------------------------------------------------------

 /**
   * Gives usage of <tt>timgraph</tt> on standard output.
   */
   public void printUsage()  {printUsage(System.out);}

     /**
      * Gives usage of <tt>timgraph</tt> on standard output.
      */
   public void printUsage(PrintStream PS)  {

        timgraph temp = new timgraph();
        RandomWalk rw = new RandomWalk(temp);
                
                PS.println("OPTIONS for timgraph version "+VERSION+"\n");
                PS.println(" -e<int> number of events (time steps), default "+temp.numevents );
                PS.println(" -f?n<FullName> full filename, directories optional, ?=i input or ?=o output, default input="+temp.inputName.getNameRoot()+", output="+temp.outputName.getNameRoot());
                PS.println(" -f?r<NameRoot> root of filename, ?=i input or ?=o output, default input="+temp.inputName.getNameRoot()+", output="+temp.outputName.getNameRoot());
                PS.println(" -f?e<Ending>  ending used for files, ?=i input or ?=o output, default  input="+temp.inputName.getNameEnd()+", output="+temp.outputName.getNameEnd());
                PS.println(" -f?s<SubDirName>  sub directory using forward slashes for ?=i input or ?=o output, default  input="+temp.inputName.getDirectoryEnd()+", output="+temp.outputName.getDirectoryEnd());
                PS.println(" -f?d<DirName>  root of directory using forward slashes for ?=i input or ?=o output, default  input="+temp.inputName.getDirectoryRoot()+", output="+temp.outputName.getDirectoryRoot());
                PS.println("                full file names are DirName/SubDirName/NameRootEnding");
                PS.println(" -gb<String> make a bipartite graph, \"t\"=true, otherwise false, default "+(temp.isDirected()?true:false) );
                PS.println(" -g1<String> name of type one vertices in a bipartite graph, default "+temp.getNameVerticesType1() );
                PS.println(" -g2<String> name of type two vertices in a bipartite graph, default "+temp.getNameVerticesType2() );
                PS.println(" -gd<String> make a directed graph, \"t\"=true, otherwise false, default "+(temp.isDirected()?true:false) );
                PS.println(" -gn<int> initial graph, default "+temp.initialgraph );
                PS.println(" -gm<int> connectivity m for initial graph, default "+temp.initialConnectivity  );
                PS.println(" -gve<String> for each vertex create a list of incident edges by their global label, \"t\"=true, otherwise false, default "+(temp.isVertexEdgeListOn()?true:false) );
                PS.println(" -gvl<String> use vertex labels, \"t\"=true, otherwise false, default "+(temp.isVertexLabelled()?true:false) );
                PS.println(" -gvv<int> number vertices for initial graph, default "+temp.initialVertices  );
                PS.println(" -gel<String> make a edges labelled, \"t\"=true, otherwise false, default "+(temp.isEdgeLabelled()?true:false) );
                PS.println(" -gem<String> allow multi edges, \"t\"=true, otherwise false, default "+(temp.isMultiEdge()?true:false) );
                PS.println(" -ges<String> allow self loops, \"t\"=true, otherwise false, default "+(temp.isSelfLooped()?true:false) );
                PS.println(" -gew<String> make a weighted graph, \"t\"=true, otherwise false, default "+(temp.isWeighted()?true:false) );
                PS.println(" -gx<int> x dimension for initial graph (-1 = make square or cube root of size), default "+temp.initialXsize  );
                PS.println(" -gy<int> y dimension for initial graph (-1 = make equal to x dimension), default "+temp.initialYsize  );
                PS.println(" -k<float> average degree, default "+temp.connectivity/2.0 );
                PS.println(" -lr<float> average walk length equivalent for ranking prob.(<0 = diameter/2), default "+temp.rankingProbabilityLengthScale );
                PS.println(" -lw<float> average walk length for random walks (<0 = diameter/2), default "+temp.averageWalkLength );
                PS.println(" -o<int> output modes (see below), default "+temp.outputControl);
                PS.println(" -p<float> probability in walks of adding a new vertex per event, default "+temp.probnewvertex);
                PS.println(" -ve<int> edge generator: 0 Walk, 1 ER, default "+temp.edgegenerator  );
                PS.println(" -vw<int> walk modes (see below), default "+rw.randomWalkMode);
                PS.println(" -vn<int> binomial distribution integer, default "+temp.binomialNumber);
                PS.println(" -xe<maxEdges>     maximum no. edges,                   default "+temp.maximumStubs/2);
                PS.println(" -xi<infoLevel>    level for information, >0 debugging, default "+temp.infoLevel);
                PS.println(" -xv<maxVertices>  maximum no. vertices,                default "+temp.maximumVertices);
                PS.println(" -xs<maxStubs>     maximum no. stubs,                   default "+temp.maximumStubs);
                PS.println("gn graph types:");
                PS.println("          0= empty no vertices, no edges");
                PS.println("         +1= one initial vertex with no edges");
                PS.println("         +2= two edges between two initial vertices");
                PS.println("         +3= one edge between two initial vertices");
                PS.println("       4..9= various small fixed graphs" );
                PS.println("         99= read in from file" );
                PS.println("         -1= line of numevents vertices and 2*gm neighbours" );
                PS.println("         -2= ring of numevents vertices and 2*gm neighbours" );
                PS.println("         -3= 2-dimensional lattice of side approx sqrt(numevents) vertices and 4*gm neighbours" );
                PS.println("         -4= 2-dimensional torus of side approx sqrt(numevents) vertices and 4*gm neighbours" );
                PS.println("         -5= 3-dimensional lattice of side approx sqrt(numevents) vertices and 4*gm neighbours" );
                PS.println("         -6= 3-dimensional torus of side approx sqrt(numevents) vertices and 4*gm neighbours" );
                PS.println("        -11= line of gx complete subgraphs connected to 2*gm nearest neighbour communities" );
                PS.println("        -12= ring of gx complete subgraphs connected to 2*gm nearest neighbour communities" );
                PS.println("vw modes: (vw& 1) ? Starts walks from random vertex : (end of random edge)");
                PS.println("        : (vw& 2) ? Starts new walk for every edge : (only for every event)");
                PS.println("        : (vw& 4) ? Markovian walk, yes : (or no)");
                PS.println("        : (vw& 8) ? Random number edges per vertex, yes : (or no)");
                PS.println("        : (vw&16) ? Binomial (Markovian) walk length distribution, yes : (or no)");
                PS.println("Default modes are:");
                rw.printAllModes(PS,"        : ");
                PS.println(" o modes:");
                temp.outputControl.printUsage(PS,"         ");
                PS.print  ("Default outputs are:");
                temp.outputControl.printMode(PS,"");
                PS.print  ("Deprecated :");
                PS.println(" -m<float> average connectivity, default "+temp.connectivity );
    }


// -----------------------------------------------------------------------       
    /**
     * Prints reduced degree distribution.
     */
    void printReducedDegreeDistribution()  {
        int k;
        int nk=0;
        System.out.println("Reduced Degree Distribution");
        System.out.println(" k "+SEP+" n(k)");
        for (k=0; k<rddarr.size(); k++) {
           nk=rddarr.get(k);
           if (nk>0) System.out.println(k+SEP+nk);
        }
        return;
    }//eo printDegreeDistribution



    // -----------------------------------------------------------------------       
    /**
     * Prints weight distribution.
     */
    void printWeightDistribution()  {
        int w;
        int nw=0;
        System.out.println("Weight Distribution");
        System.out.println(" w "+SEP+" n(w)");
        for (w=0; w<weightdarr.size(); w++) {
           nw=weightdarr.get(w);
           if (nw>0) System.out.println(w+SEP+nw);
        }
        return;
    }//eo printWeightDistribution

// -----------------------------------------------------------------------       
    /**
     * Prints distance distribution.
     * @param distributionshow is true if want the distribution
     */
    void printDistanceDistribution(boolean distributionshow)  {
        System.out.println("diameter "+SEP+diameter);
        System.out.println("Distance Distribution [one sample]");
        System.out.println("From totals: average distance "+SEP+NumbersToString.toString(distanceaverage, 2) +SEP+" +/- "+SEP+NumbersToString.toString(distanceerror,2)+", sigma "+SEP+n2s.toString(distancesigma,2));
        System.out.println("Average distance from one vertex "+SEP+NumbersToString.toString(onesdistanceav,2)+SEP+" +/- "+SEP+NumbersToString.toString(onesdistanceerror,2)+SEP+", sigma="+SEP+n2s.toString(onesdistancesigma,2));  // average and error over samples
        System.out.println("Diameter "+SEP+NumbersToString.toString(diameter,2)+SEP+" +/- "+SEP+ NumbersToString.toString(diametererror,2) +SEP+", sigma "+SEP+ n2s.toString(diametersigma,2) );
        if (distributionshow) 
        {
            System.out.println(" d"+SEP+"n(d)");
            for (int d=0; d<distancedist.size(); d++) 
            System.out.println(d+SEP+distancedist.get(d));
        }
        return;
    }//eo printDistanceDistribution

// -----------------------------------------------------------------------       
    /**
     * Prints component information to screen.
     */
    public void printComponentInfo()  {
        printComponentInfo(System.out);
        return;
    }//eo printComponentInfo
    /**
     * Prints component information.
     * <p>Gives list of components and their properties
     * but no vertex to component label list.
     *@param PS PrintStream such as System.out
     */
    public void printComponentInfo(PrintStream PS)  {
        printComponentInfo(PS, true, false);
    }//eo printComponentInfo
    /**
     * Prints component information.
     *@param PS PrintStream such as System.out
     * @param componentDistribution true if want list of components and their properties
     * @param vertexToComponent true if want a list of vertex to component label
     */
    public void printComponentInfo(PrintStream PS, boolean componentDistribution, boolean vertexToComponent)  {
        PS.println("Number of Components "+SEP+componentSize.size());
        PS.println("     Vertices in GCC "+SEP+componentSizeMax);
        PS.println("     Diameter of GCC "+SEP+componentDiameterMax);
        PS.println(" Av. Distance of GCC "+SEP+componentGCCDist);
        PS.println("   GCC Component No. "+SEP+componentGCCIndex);
        if (componentDistribution) printComponentDistribution(PS);
        if (vertexToComponent) printVertexToComponentList(PS);
        return;
    }//eo printComponentInfo
// -----------------------------------------------------------------------       
    /**
     * Prints component distribution
     *@param PS PrintStream such as System.out
     */
    void printVertexToComponentList(PrintStream PS)  {
        PS.println("v"+SEP+"c");
        for (int v=0; v<TotalNumberVertices; v++)
           PS.println(v+SEP+this.getVertexComponentLabel(v));
    }//eo printComponentDistribution
    /**
     * Prints list of vertices along with their component label
     *@param PS PrintStream such as System.out
     */
    void printComponentDistribution(PrintStream PS)  {
        PS.println(" c"+SEP+"n(c)"+SEP+"v in c"+SEP+"diam_min");
        int count =0;
        for (int c=0; c<componentSize.size(); c++)
        {
           PS.println(c+SEP+componentSize.get(c)+SEP+componentSource.get(c)+SEP+componentDist.get(c));
           count+=componentSize.get(c);
        }
        PS.println("Tot"+SEP+count);

        return;
    }//eo printComponentDistribution
// -----------------------------------------------------------------------       
    /**
     * Returns Number of Components.
     *@return Number of Components
     */
    public int getNumberComponents()  {
        return componentSize.size();
    }

// -----------------------------------------------------------------------       
    /**
     * Returns Average Component Size.
     *@return Average Component Size.
     */
    public double getAverageComponentSize()  {
        return  ((double) TotalNumberVertices) / ((double) componentSize.size() );
    }

// -----------------------------------------------------------------------       
    /**
     * Gives number of components with just one vertex.
     *<br> If have tadpoles this may count vertices with only tadpole edges as well as vertices with no edges.
     *@return number of components with no edges
     */
    public int getNumberSingleComponents()  {    
        return componentSingleNumber;
    }//eo 

// -----------------------------------------------------------------------       
    /**
     * Gives number of components with at least two vertices.
     *<br> If have tadpoles this may not count vertices with only tadpole edges.
     *@return number of components with at least two vertices, zero if there are none,
     */
    public double getAverageMultiComponentSize()  {
        int nmc = (componentSize.size()- componentSingleNumber);
        if (nmc>0) return ((double) (TotalNumberVertices-  componentSingleNumber) ) / ( (double) nmc )   ;
        else return nmc;
    }//eo 

    // -----------------------------------------------------------------------       
    /**
     * Gives number of components with at least two vertices.
     *<br> If have tadpoles this may not count vertices with only tadpole edges.
     *@return number of components with at least two vertices
     */
    public int getNumberMultiComponents()  {
        return (componentSize.size()- componentSingleNumber);
    }//eo 

    
// -----------------------------------------------------------------------       
    /**
     * Returns No. Vertices in GCC.
     *@return Number of Vertices in GCC
     */
    public int getNumberGCCVertices()  {
        return componentSizeMax;
    }

// -----------------------------------------------------------------------       
    /**
     * Returns GCC diameter estimate.
     *@return GCC diameter estimate.
     */
    public int getGCCDiameter()  {
        return componentDiameterMax;
    }

// -----------------------------------------------------------------------       
    /**
     * Returns GCC average distance estimate.
     *@return GCC average distance estimate.
     */
    public double getGCCDistance()  {
        return componentGCCDist;
    }

    
    // -----------------------------------------------------------------------       
    /**
     * Gives string with information about components.
     *@param sep separation string
     */
    public String componentInfoString(String sep)  {
        return (componentSize.size() +sep +componentSizeMax+sep+componentDiameterMax+sep+componentGCCDist);
    }
    // -----------------------------------------------------------------------       
    /**
     * Gives string labelling the string with information about components.
     *@param sep separation string
     */
    public String componentInfoStringLabel(String sep)  {
        return "No.Comp."+sep+"No.GCC Vert"+sep+"GCC Diam."+sep+"GCC Dist.";        
    }

// -----------------------------------------------------------------------       
// -----------------------------------------------------------------------       
    /**
     * Prints component distribution.
     *
     */
    void printComponentDistribution()  {
        printComponentDistribution(System.out);
        return;
    }//eo printComponentInfo

    // -----------------------------------------------------------------------       
    /**
     * Outputs ring information.
     *@param PS printstream
     *@param source vertex of the ring
     */
    void printRingInfo(PrintStream PS, int source)  {
        PS.println("Ring source "+SEP+source+SEP+" degree"+SEP+vertexList[source].size());
        PS.println("Ring diameter "+SEP+(distancedist.size()-1));
        PS.println("ring"+SEP+"n(r)"+SEP+"<k(r)>"+SEP+"Edges");
        int count=0;
        double edge=0;
        int nv=0;
        double deg=0;
        for (int r=0; r<distancedist.size(); r++) 
        {
            nv=distancedist.get(r);
            deg=ringdegree.get(r);
            count+=nv;
            edge+=nv*deg;
            PS.println(r+SEP+nv+SEP+deg+SEP+NumbersToString.toString(nv*deg,1));
            
        }
        PS.println("Total Vertices"+SEP+count+SEP+", Total Edges "+NumbersToString.toString(edge,2)+", Average degree "+(edge/((double) count)) );
        return;
    }//eo printRingInfo
    
 // -----------------------------------------------------------------------       
    /**
     * Prints distance distribution.
     * @param distributionshow to show distribution
     */
    void printDistanceTotalDistribution(boolean distributionshow)  
    {
        System.out.println("Distance Distribution ["+totdistancedist.get(0)+" samples]");
        System.out.println("diameter maximum, minimum, average "+SEP+NumbersToString.toString(diametermax,2) +SEP+ NumbersToString.toString(diametermin,2) +SEP+ NumbersToString.toString(diameteraverage,2) +SEP+", sigma= "+ NumbersToString.toString(diametersigma,2) );
        System.out.println("average distance "+SEP+NumbersToString.toString(totdistanceaverage,2)+SEP+", sigma= "+SEP+NumbersToString.toString(totdistancesigma,2) );
        
        //System.out.println("From totals: average distance "+SEP+n2s.toString(distanceaverage, 2) +SEP+" +/- "+SEP+n2s.toString(distanceerror,2)+", sigma "+SEP+n2s.toString(distancesigma,2));
        //System.out.println("Average distance from one vertex "+SEP+n2s.toString(onesdistanceav,2)+SEP+" +/- "+SEP+n2s.toString(onesdistanceerror,2)+SEP+", sigma="+SEP+n2s.toString(onesdistancesigma,2));  // average and error over samples 
        //System.out.println("Diameter "+SEP+n2s.toString(diameter,2)+SEP+" +/- "+SEP+ n2s.toString(diametererror,2) +SEP+", sigma "+SEP+ n2s.toString(diametersigma,2) );
        if (distributionshow)
        {
           System.out.println(" d"+SEP+"n(d)"+SEP+" sum_sample (n(d))^2");
           for (int d=0; d<totdistancedist.size(); d++) 
           System.out.println(d+SEP+totdistancedist.get(d)+SEP+totdistance2dist.get(d));
        }
        return;
    }//eo printtDistanceTotalDistribution
    
     
// -----------------------------------------------------------------------       
    /**
     * Prints distance distribution.
     */
    void printDistanceList()  {
        System.out.println(" v \t d");
        for (int v=0; v<vertexdistance.length; v++) 
           System.out.println(v+SEP+vertexdistance[v]);
        return;
    }//eo printDistanceList
    
// -----------------------------------------------------------------------       
    /**
     * Prints distance distribution.
     */
    void printCC()  {
        System.out.println("CC Values ["+CCnsamples+" samples]");
        System.out.println("average CC "+SEP+CCaverage+SEP+" +/- "+SEP+CCerror+SEP+", sigma= "+SEP+CCsigma);
        System.out.println("Edge CC Values ["+CCEdgensamples+" samples]");
        System.out.println("average Edge CC "+SEP+CCEdgeaverage+SEP+" +/- "+SEP+CCEdgeerror+SEP+", sigma= "+SEP+CCEdgesigma);
        return;
    }//eo printCC
    
      
// **********************************************************************
    /** 
     * Prints network on screen.
     *@param printNearestNeighbours true if want n.n. list
     */
     public void printNetwork(boolean printNearestNeighbours)  
     {
         boolean printTriangles=true;
         boolean printSquares=true;
            printVertices(System.out, printTriangles, printSquares, printNearestNeighbours);
            printEdges(System.out);
            System.out.println("***"+SEP+SEP+SEP+"***");
     }

     

// ...........................................................................   
    /** 
     * Writes network information to a file.
     * <p>IInformation on both vertices and edges.
     *   <tt>inputDirName</tt>+<tt>inputNameRoot</tt>+<tt>dat.net</tt> is the file with the graph.
     * @param printTriangles prints number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     *@param printNearestNeighbours true if want n.n. list
     */
     void FileOutputNetwork(boolean printTriangles, boolean printSquares, boolean printNearestNeighbours)
     {
        outputName.setNameEnd("net.dat");
        PrintStream PS;
        int ew,ec;
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(outputName.getFullFileName());
            PS = new PrintStream(fout);
            printVertices(PS, printTriangles, printSquares, printNearestNeighbours);
            printEdges(PS);            
            if (infoLevel>-2) System.out.println("Finished writing Network list to "+ outputName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+outputName.getFullFileName());
            return;
        }
        return;
    }     

     // ...........................................................................   
    /** 
     * Writes vertex information to a file.
     * 
     *   inputDirName+inputNameRoot+dat.net is the file with the graph.
     *@param printNearestNeighbours true if want n.n. list
     * @param printTriangles prints number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @deprecated Use FileOutput routine
     */
     void FileOutputVertices(boolean printTriangles, boolean printSquares, boolean printNearestNeighbours)
     {
        outputName.setNameEnd("vertices.dat");
        PrintStream PS;
        int ew,ec;
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(outputName.getFullFileName());
            PS = new PrintStream(fout);
            printVertices(PS, printTriangles, printSquares, printNearestNeighbours);
            if (infoLevel>-2) System.err.println("Finished writing vertex information to "+ outputName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+outputName.getFullFileName());
            return;
        }
        return;
    }     

// ...........................................................................     
    /** 
     * Prints information on vertices to a PrintStream.
     * <p>Prints only those fields which have been defined.
     *@param PS PrintStream such as System.out
     *@param printTriangles prints number of triangles
     *@param printSquares print number of squares but only if printTriangles also true
     *@param printNearestNeighbours true if want n.n. list
     */
     public void printVertices(PrintStream PS, boolean printTriangles, boolean printSquares,
             boolean printNearestNeighbours)
     {
         printVertices(PS, "", SEP, printTriangles, printSquares, printNearestNeighbours);
     }
// ...........................................................................     
    /** 
     * Prints vertex information on PrintStream.
     * <p>Prints only those fields which have been defined.
     * @param PS PrintStream such as System.out
     * @param cc comment string
     * @param sep separation string
     * @param printTriangles prints number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param printNearestNeighbours true if want n.n. list
     */
     public void printVertices(PrintStream PS, String cc, String sep, 
             boolean printTriangles, boolean printSquares, boolean printNearestNeighbours)
     {
            boolean printCommunityLabel=false;
            PS.print(getVertexStringLabel(cc, sep, 
                    printTriangles, printSquares, 
                    printCommunityLabel, printNearestNeighbours));
            PS.println(sep+"No. Vertices = "+sep+TotalNumberVertices);            
            for (int v=0; v<TotalNumberVertices; v++)
                PS.println(getVertexString(cc, sep, v, IUNSET,
                        printTriangles, printSquares, printNearestNeighbours));
     }


    /** 
     * Prints vertex information on PrintStream.
     * <p>This is the <b>PRIMARY</a> most general vertex printing routine.
     * <p>Will try to print out items even if they are not defined.  Use
     * <tt>printName=hasName()</tt> to avoid this or other <tt>printVertices</tt> routines.
     * @param PS PrintStream such as System.out
     * @param cc comment string
     * @param sep separation string
     * @param vp vertex partition, null if not to be shown
     * @param printTriangles print number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param printName true (false) to (not) print  name
     * @param printNumber true (false) to (not) print number
     * @param printPosition true (false) to (not) print coordinate
     * @param printStrength true (false) to (not) print strength
     * @param printMaxWeight true (false) to (not) print largest weight even if labels not defined
     * @param printRank true (false) to (not) print rank
     * @param printNearestNeighbours true if want n.n. list
     */
     public void printVertices(PrintStream PS, String cc, String sep,
             VertexPartition vp,
             boolean printTriangles, boolean printSquares,
             boolean printName, boolean printNumber, boolean printPosition,
             boolean printStrength, boolean printMaxWeight,
             boolean printClusterCoef,
             boolean printRank,
             boolean printStructuralHoleData,
             boolean printNearestNeighbours)
     {
         boolean printCommunityLabel = (vp !=null);
         PS.println(getVertexStringLabel(cc, sep,
              printTriangles, printSquares,
              printCommunityLabel, printName, printNumber, printPosition, 
              printStrength, printMaxWeight,
              printClusterCoef,
              printRank, printStructuralHoleData, printNearestNeighbours));
         //PS.println(sep+"No. Vertices = "+sep+TotalNumberVertices);
         for (int v=0; v<TotalNumberVertices; v++) PS.println(getVertexString(cc, sep, v, 
                    (printCommunityLabel?vp.getCommunity(v):IUNSET), printTriangles, printSquares,
                    printName, printNumber, printPosition,
                    printStrength, printMaxWeight,
                    printClusterCoef,
                    printRank, printStructuralHoleData,
                    printNearestNeighbours));
     }

    /**
     * Prints a line for each community a vertex is in.
     * @param PS PrintStream for output e.g. system.out
     * @param vertexToCommunity List of communities associated with each vertex
     */
    public void printVertexCommunity(PrintStream PS, ArrayList<ArrayList<Integer>> vertexToCommunity ){
        ArrayList<Integer> vc;
        String cc=""; //timgraph.COMMENTCHARACTER;
        String sep=timgraph.SEP;
        int vertexPartition=timgraph.IUNSET;
        boolean printStrength=isWeighted();
        boolean printMaxWeight=false;
        boolean printNearestNeighbours=false;
        boolean printTriangles=false;
        boolean printSquares=false;
        boolean printCommunityLabel=true;
        PS.println(getVertexStringLabel(cc, sep, printTriangles,  printSquares, 
                printCommunityLabel, printNearestNeighbours)+sep+"Community"+sep+"C.Frac.");
        for (int v=0; v<TotalNumberVertices; v++){
            vc=vertexToCommunity.get(v);
            if (vc.size()>0){
                for (Integer c: vc) {
                    PS.println(getVertexString(cc, sep, v, vertexPartition,
                            printTriangles, printSquares,
                            printNearestNeighbours)+sep+c+sep+(1.0/((double) vc.size()) ) );
                }
            }
            else{
                // vertex not in any clique given unique community number
                PS.println(getVertexString(cc, sep, 
                        v, vertexPartition, printTriangles, printSquares, 
                        printNearestNeighbours)+sep+(-1-v)+sep+"1.0" );
            }
        }
    }


   /** 
     * Returns string of information on vertex.
     * <p><b>Preferred</b> routine as most flexible choice.
     * Many options are determined automatically as possible from the
     * values of the first vertex label. Will try to print out only items already defined.
     * @param cc comment string
     * @param sep separation string
     * @param v global vertex index
     * @param vc community of vertex, not shown if equal to <tt>timgraph.IUNSET</tt>
     * @param printTriangles print number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param printStrength prints strength if labels not defined
     * @param printMaxWeight true (false) to (not) print largest weight even if labels not defined
     * @param printNearestNeighbours true if want n.n. list
     */
     public String getVertexString(String cc, String sep, int v, int vc,
             boolean printTriangles, boolean printSquares,
             boolean printNearestNeighbours)
     {
            boolean printName=false;
            boolean printNumber=false;
            boolean printPosition=false; 
            boolean printStrength=false;
            boolean printClusterCoef=false;
            boolean printMaxWeight=false;
            boolean printRank=false;
            boolean printStructuralHoleData=false;
            if (this.isVertexLabelled())
             {
              VertexLabel l=this.getVertexLabel(0);
              printName=l.hasName();
              printNumber=l.hasNumber();
              printPosition=l.hasPosition(); 
              printStrength=l.hasStrength();
              printClusterCoef=l.hasClusterCoef();
              printMaxWeight=l.hasMaxWeight();
              printRank=l.hasRank();
             }
            return getVertexString(cc, sep, v, vc,
                    printTriangles,printSquares,
                    printName, printNumber, printPosition,
                    printStrength, printMaxWeight, 
                    printClusterCoef,
                    printRank, printStructuralHoleData,
                    printNearestNeighbours);
     }
    /** 
     * Returns string of information on vertex.
     * <p>This is the <b>PRIMARY</b> vertex information string but not most flexible.
     * <p>Will try to print out items even if they are not defined.  Use
     * <tt>printName=hasName()</tt> to avoid this or other <tt>printVertices</tt> routines.
     * @param cc comment string
     * @param sep separation string
     * @param v global vertex index
     * @param vc community of vertex, not shown if equal to <tt>timgraph.IUNSET</tt>
     * @param printTriangles print number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param printName true (false) to (not) print  name
     * @param printNumber true (false) to (not) print number
     * @param printPosition true (false) to (not) print coordinate
     * @param printStrength true (false) to (not) print strength, even if labels not defined
     * @param printMaxWeight true (false) to (not) print largest weight even if labels not defined
     * @param printClusterCoef true (false) to (not) print strength
     * @param printRank true (false) to (not) print rank
     * @param printStructuralHoleInfo true (false) if want structural hole data
     * @param printNearestNeighbours true if want n.n. list
     */
     public String getVertexString(String cc, String sep, int v, int vc,
             boolean printTriangles,boolean printSquares,
             boolean printName, boolean printNumber, boolean printPosition, 
             boolean printStrength, boolean printMaxWeight,
             boolean printClusterCoef,
             boolean printRank,
             boolean printStructuralHoleInfo,
             boolean printNearestNeighbours)
     {
         String s=Integer.toString(v);
            if (vc !=IUNSET) s=s+sep+vc;
            s=s+sep+vertexList[v].size();
            if (printTriangles) {
                if (printSquares) {
                    int [] res = calcTrianglesSquares(v);
                    s=s+sep+res[0]+sep+res[1];
                }
                else s=s+sep+calcTriangles(v);
            }
            if (vertexlabels) s=s+sep+vertexLabelList[v].printString(sep, 
                    printName, printNumber, printPosition,
                    printStrength,printMaxWeight,
                    printClusterCoef,
                    printStructuralHoleInfo, printRank);
            else if (printStrength) s=s+sep+this.getVertexOutStrength(v);
//            if (printStructuralHoleInfo) {
//                StructuralHoleVertexData shd=StructuralHoleAnalysis.calcStructuralHoleMeasuresVertex(this,v, false);
//                s=s+shd.getVertexDataString(sep);
//            }
            if (printNearestNeighbours)  for(int e=0; e<vertexList[v].size(); e++)
                                                    s=s+sep+vertexList[v].get(e);
            return s;
     }
    /** 
     * Returns label for string of information on vertex.
     * <p><b>Preferred</b> as most flexible choice.
     * Many options are determined automatically as possible from the
     * values of the first vertex label.
     * <p>Use with <tt>getVertexString</tt>.
     * <p>Note that if <tt>printNearestNeighbours</tt> is true then this list
     * is of variable length.
     * @param cc comment string
     * @param sep separation string
     * @param printTriangles print number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param printCommunityLabel true if want explicit column for community label (not that stored in vertex label)
     * @param printStrength true (false) to (not) print strength even if labels not defined
     * @param printMaxWeight true (false) to (not) print largest weight even if labels not defined
     * @param printNearestNeighbours true if want n.n. list
     */
     public String getVertexStringLabel(String cc, String sep,
             boolean printTriangles, boolean printSquares,
             boolean printCommunityLabel,
             boolean printNearestNeighbours)
     {
            boolean printName=false;
            boolean printNumber=false;
            boolean printPosition=false;
            boolean printStrength=false;
            boolean printMaxWeight=false;
            boolean printClusterCoef=false;
            boolean printStructuralHoleInfo=false;
            boolean printRank=false;
            if (this.isVertexLabelled())
             {
              VertexLabel l=this.getVertexLabel(0);
              printName=l.hasName();
              printNumber=l.hasNumber();
              printPosition=l.hasPosition(); 
              printStrength=l.hasStrength();
              printMaxWeight=l.hasMaxWeight();
              printClusterCoef=l.hasClusterCoef();
              printStructuralHoleInfo=l.hasStructuralHoleVertexData();
              printRank=l.hasRank();
             }
            return getVertexStringLabel(cc, sep,
             printTriangles, printSquares,
             printCommunityLabel, printName, printNumber, printPosition,
             printStrength,  printMaxWeight, 
             printClusterCoef,
             printRank, printStructuralHoleInfo,
             printNearestNeighbours);
     }

    /** 
     * Returns label for string of information on vertex.
     * <p>Use with <tt>getVertexString</tt>.
     * <p>Note that if <tt>printNearestNeighbours</tt> is true then this list
     * is of variable length.
     * @param cc comment string
     * @param sep separation string
     * @param printTriangles print number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param printCommunityLabel true if want explicit column for community label (not that stored in vertex label)
     * @param printName true (false) to (not) print  name
     * @param printNumber true (false) to (not) print number
     * @param printPosition true (false) to (not) print coordinate
     * @param printStrength true (false) to (not) print strength even if labels not defined
     * @param printMaxWeight true (false) to (not) print largest weight even if labels not defined
     * @param printClusterCoef true (false) to (not) print strength
     * @param printRank true (false) to (not) print rank
     * @param printNearestNeighbours true if want n.n. list
     */
     public String getVertexStringLabel(String cc, String sep, 
             boolean printTriangles, boolean printSquares,
             boolean printCommunityLabel, boolean printName, boolean printNumber, boolean printPosition,
             boolean printStrength, boolean printMaxWeight, 
             boolean printClusterCoef,
             boolean printRank, boolean printStructuralHoleInfo,
             boolean printNearestNeighbours)
     {
            String s="index";
            if (printCommunityLabel) s=s+sep+"Community";
            s=s+sep+"degree";
            if (printTriangles) {
                s=s+sep+"triangles";
                if (printSquares) s=s+sep+"squares";
            }
            if (vertexlabels) s=s+sep+VertexLabel.labelString(sep, 
                    printName, printNumber, printPosition,
                    printStrength, printMaxWeight,
                    printClusterCoef,
                    printStructuralHoleInfo, printRank);
            if (printNearestNeighbours)  s=s+sep+"n.n.";
            return s;
     }


// ...........................................................................
     /** 
     * Prints list of edges on screen.
     * <br>Includes all information possible.
      */
     public void printEdges(){printEdges(System.out);}
     
     /** 
      * Prints list of edges to PrintStream.
      * <br>Includes all information possible.
      *@param PS PrintStream such as System.out
     */    
     public void printEdges(PrintStream PS) 
     {
      printEdges(PS, "#", SEP, DUNSET, IUNSET, true, true, true, true, true);
     }
     
     /** 
      * Prints simple list of edges to PrintStream.
      * <br>Format is source vertex, target vertex, edge weight
      *@param PS PrintStream such as System.out
     */    
     public void printEdgesSimple(PrintStream PS) 
     {
      printEdges(PS, "#", SEP, DUNSET, IUNSET, true, false, true, false, false);
     }
     
     /** 
     * Prints a list of the edges to printstream.
      * <br>Each line has one edge with the edge index of source, edge index of target, 
      * vertex index of source, vertex index of target, edge label, edge weight.
      * Target edge index should be one more than its source and the source indices ought to be even. 
      * @param PS PrintStream such as System.out
      * @param cc Comments string used for information line
      * @param sep separation string e.g. tab character
      * @param minWeight  minimum weight of edge to be retained (ignored if unweighted or if equal to <tt>timgraph.DUNSET</tt>)
      * @param minLabel  minimum label of edge to be retained (ignored if unweighted, unlabelled or if equal to <tt>timgraph.IUNSET</tt>)
      * @param vertexNames true if want to use vertex labels not indices for source and target, otherwise numbers used.
      * @param infoOn if true then first line will be an information line starting with the <tt>cc</tt> string.
      * @param headerOn if true then head of each column labels the item
      * @param edgeIndexOn if true the the first two items will be the edge index used internally.  
      * @param edgeLabelOn shows the label part of edge if weighted
      */    
     public void printEdges(PrintStream PS, String cc, String sep, 
             double minWeight, int minLabel,
             boolean vertexNames, boolean infoOn, boolean headerOn, boolean edgeIndexOn, boolean edgeLabelOn) 
     {
            boolean useLabels = (vertexNames & vertexlabels);
            if (infoOn) PS.println(cc+"Edges    = "+sep+TotalNumberStubs+sep+stubSourceList.length);
            if (headerOn)
            {
            if (edgeIndexOn) PS.print(" e1 "+sep + " e2" +sep);
            PS.print(" Source "+sep+" Target ");
            //if (weightedEdges) PS.print(EdgeValue.headerString(sep));
            if (weightedEdges ) PS.print(sep+"Weight");
            if (weightedEdges && edgeLabelOn) PS.print(sep+"Label");
            PS.println();
            }
            double ew=1;
            int el=0;
            boolean testWeight=(minWeight!=DUNSET);
            boolean testLabel=(minLabel!=IUNSET);
            String nextline="NOTHING";
            for (int e=0; e<TotalNumberStubs; e+=2) 
            {
                nextline="";
                if (weightedEdges ) {ew=edgeValuetList[e].weight;
                                     if ((testWeight) && (ew<minWeight)) continue;
                }
                if (weightedEdges && edgeLabelOn) {el=edgeValuetList[e].label;
                    if ((testLabel) && (el<minLabel)) continue;
                }
                if (edgeIndexOn) nextline=nextline+e+sep+(e+1)+sep;
                int s=stubSourceList[e];
                int t=stubSourceList[e+1];
                if (useLabels) nextline=nextline+vertexLabelList[s].getName()+sep+vertexLabelList[t].getName();
                else nextline=nextline+s+sep+t;
                if (weightedEdges ) nextline=nextline+sep+ew;
                if (weightedEdges && edgeLabelOn) nextline=nextline+sep+el;
                PS.println(nextline);
            }
     }
  
     /** 
     * Prints a list of information on the edge community structure by vertex.
      * <p>Does all vertices even if bipartite.
      * @param PS PrintStream such as System.out
      * @param sep separation string e.g. tab character
      * @param headerOn if true then head of each column labels the item
      * @param vertexNames true if want to use vertex labels not indices for source and target, otherwise numbers used.
      * @param splitBipartite true if want to split a bipartite graph into type one or two (ignored if not bipartite)
      * @param outputType1 true (false) if want type 1 (2) vertices if splitting them for a bipartite graph
      */
     public void printEdgeCommunityStats(PrintStream PS, String sep, boolean headerOn, boolean vertexNames,
            boolean splitBipartite, boolean outputType1 ){
         if (!weightedEdges) throw new IllegalArgumentException("*** printEdgeCommunityStats needed edges to have labels");
         if (!vertexEdgeListOn) throw new IllegalArgumentException("*** printEdgeCommunityStats needed vertexEdgeList");
         if (headerOn) PS.println(edgeCommunityStatsLabel(sep));
         for (int v=0; v<TotalNumberVertices; v++) {
             if (bipartiteGraph && splitBipartite) {
                if (outputType1) { if (this.isType2(v)) continue;}
                else             { if (this.isType1(v)) continue;}
            }
            printEdgeCommunityStats(PS, sep, v, vertexNames); //, splitBipartite,  outputType1 );
         }
     }
     
        /**
    * Prints out statistics on edge communities of one vertex.
    * <p>Allows you to choose which type of vertex to output if bipartite (or does all)
    * <p>If a vertex has no edges then its community is set to be
    * <tt>-v-1</tt> where <tt>v</tt> is the index of the vertex.
    * This is to ensure that all vertices appear in this list but can be
    * an issue in some routines.  In this case the fraction of degree and strength
    * in the community is set to 1.0 (used as membership fraction of communities
    * in some cases) but other data are given as zero.
    * @param PS PrintStream such as System.out
    * @param sep separation string e.g. tab character
    * @param v vertex index
    * @param vertexNames true if want to use vertex labels not indices for source and target, otherwise numbers used.
//    * @param splitBipartite true if want to split a bipartite graph into type one or two (ignored if not bipartite)
//    * @param ouputType1 true (false) if want type 1 (2) vertices if splitting them for a bipartite graph
      */
    private void printEdgeCommunityStats(PrintStream PS, String sep, int v, boolean vertexNames){
//            boolean splitBipartite, boolean outputType1){
//        if (bipartiteGraph && splitBipartite) {
//                if (outputType1) { if (this.isType2(v)) return;}
//                else             { if (this.isType1(v)) return;}
//            }
        String name="";
        if (vertexNames) name=getVertexName(v);
        else name=name+v;
        int k=this.getVertexDegree(v);
        if (k==0){
            int c = -1-v;
            int kc = 0;
            double sc = 0;
            double s=0;
            double fsc=1.0; // needed for when use this as a community membership fraction
            double fkc=1.0;
            double sEntropy=0;
            PS.println(name+sep+c+sep+k+sep+kc+sep+fkc+sep+s+sep+sc+sep+fsc+sep+sEntropy);
            return;
        }
        IntArrayList edgesInCommunity = new IntArrayList(k);
        DoubleArrayList strengthInCommunity = new DoubleArrayList(k);
        TreeMap<Integer,Integer> communityToIndex= new TreeMap();
        IntArrayList indexToCommunity = new IntArrayList(k);
        IntArrayList edgeList =vertexEdgeList[v];
        double s=0;
        for (int ei=0; ei<edgeList.size(); ei++){
            int e=edgeList.getQuick(ei);
            int c= this.edgeValuetList[e].label;
            double w= this.edgeValuetList[e].weight;
            s+=w;
            if (communityToIndex.containsKey(c)) { //known community
                int i = communityToIndex.get(c);
                edgesInCommunity.setQuick(i,edgesInCommunity.getQuick(i)+1);
                strengthInCommunity.setQuick(i,strengthInCommunity.getQuick(i)+w);
            }
            else { // new community
                communityToIndex.put(c, edgesInCommunity.size());
                indexToCommunity.add(c);
                edgesInCommunity.add(1);
                strengthInCommunity.add(w);
            }
        }// eo for ei
        double kd = (double) k;
        double sEntropy=1.0;
        double sc=-1;
        for (int i=0; i<indexToCommunity.size(); i++){
            double p = strengthInCommunity.getQuick(i)/s;
            if (p>1e-20) sEntropy+= -p*Math.log(p);
        }
//        boolean testType = (bipartiteGraph && splitBipartite);
        for (int i=0; i<indexToCommunity.size(); i++){
//            if (testType) {
//                if (outputType1) { if (this.isType2(v)) continue;}
//                else             { if (this.isType1(v)) continue;}
//            }
            int c = indexToCommunity.get(i);
            int kc = edgesInCommunity.getQuick(i);
            sc = strengthInCommunity.getQuick(i);
            PS.println(name+sep+c+sep+k+sep+kc+sep+(kc/kd)+sep+s+sep+sc+sep+(sc/s)+sep+sEntropy);
        }
    }
   /**
    * Label for vertex information on edge communities information.
    * @param sep separation string e.g. tab character
    */
    private String edgeCommunityStatsLabel( String sep){
        return "Name"+sep+"Community"+sep+"Degree k"+sep+"k in C."+sep+"Frac.k In C."+sep+"Strength"+sep+"S.in C."+sep+"Frac.S. in C."+sep+"S.Entropy";
    }
    

    
// **********************************************************************
    /** 
     * Prints network on screen.
     */
    public void printOrderedWeightedNetwork()  
     {
            System.out.println("# Vertices = "+SEP+TotalNumberVertices+SEP+vertexList.length);
            System.out.println("# Edges    = "+SEP+TotalNumberStubs+SEP+stubSourceList.length);
            for (int v=0; v<TotalNumberVertices; v++) {
                System.out.println("Vertex "+v+" has degree "+vertexList[v].size()+" and is connected to:");
                for(int e=0; e<vertexList[v].size(); e++) 
                  System.out.print(vertexList[v].get(e)+SEP);
                System.out.println();
            }
            System.out.println("Edges ");
            System.out.print(" e1 "+SEP + " e2" +SEP+ " Sou "+SEP+" Tar ");
            if (weightedEdges) System.out.println(SEP+" Lab "+SEP+" Wei ");
            int e; 
            for (int er=0; er<TotalNumberStubs/2; er++) 
            {
                e=edgerandomlist[er]*2;
                System.out.println(e+SEP+(e+1)+SEP+stubSourceList[e]+SEP+stubSourceList[e+1]);
                if (weightedEdges) System.out.println(SEP+edgeValuetList[e].label+SEP+edgeValuetList[e].weight);
           }
            System.out.println("***                      ***");
     }
  
// **********************************************************************
    /** 
     *   prints info on directed network.
     */
     void printReducedNetwork()  
     {
            System.out.println("*** Reduced Network Data ***");
            System.out.println("# Vertices = "+SEP+TotalNumberVertices);
            for (int v=0; v<TotalNumberVertices; v++) 
            {
                System.out.println("Vertex "+v+" has reduced degree "+rvertexList[v].size()+" and strength"+vertexList[v].size()+" and is connected to:");
                for(int e=0; e<rvertexList[v].size(); e++) 
                  System.out.print(rvertexList[v].get(e)+" ("+strengthlist[v].get(e)+") "+SEP);
                System.out.println();
            }
            System.out.println("\n***                      ***");
     }
 
     
     
// --------------------------------------------------------------------------    
    /** 
     * Prints parameters to standard output.
     */
    public void printParametersBasic()  {
        printParametersBasic(System.out, SEP+" ") ;
    }
    /** 
     * Prints parameters to standard output.
     * @param PS a PrintStream such as System.out
     * @param sep separation string
     */
    public void printParametersBasic(PrintStream PS, String sep)  {
        TimTime t = new TimTime(sep);
        PS.println("Programme version "+sep+VERSION+sep+t.fullString(sep));
        PS.println(inputName.getNameRoot()+" initial Graph " +sep+ initialgraph+sep + initialGraphString()  );
        PS.println("Number Vertices " +sep+ TotalNumberVertices+sep+"Stubs" +sep+ TotalNumberStubs+sep+"Total Weight" +sep+ this.getTotalWeight());
        if (bipartiteGraph) PS.println("Number "+this.nameVertexType1+" (type 1) and "+this.nameVertexType2+" (type 2) vertices"+sep+ numberVertexType1+sep+numberVertexType2);
        PS.println("Average Out degree "+sep+calcDegreeFirstMoment()+sep+", Second Moment of D.D."+sep+calcDegreeSecondMoment());
        PS.println("Total Number of Triangles "+sep+getNumberTriangles()+", global clustering coefficient "+sep+getCCGlobal());
        PS.println(graphTypeString(sep));
        PS.println(stringOfChecks(sep));
    }

    // **************************************************************************
    /**
     * Returns double as string of specified format.
     * <br>Returns UNSET string if equals DUNSET {@value timgraph.DUNSET}
     * <br>g code used so switches from decimal to scientific notation as needed.
     * @param d double to print
     * @param width width of whole string
     * @param digits number of digits after the decimal point.
     */
    static public String stringDouble(double d,int width, int digits){
        return (d==DUNSET?"UNSET":String.format("%"+width+"."+digits+"g",d));}
    /**
     * Returns integer as string of specified format.
     * <br>Returns UNSET string if equals IUNSET {@value timgraph.IUNSET}
     * <br>i code used.
     * @param d double to print
     * @param width width of whole string
     */
    static public String stringInt(int i, int width){
        return (i==IUNSET?"UNSET":String.format("%"+width+"d",i));}
    /**
     * Returns double as string of specified format.
     * <br>Returns UNSET string if equals DUNSET {@value timgraph.DUNSET}
     * @param d double to print
     */
    static public String stringDouble(double d){
        return (d==DUNSET?"UNSET":Double.toString(d));}
    /**
     * Returns integer as string of specified format.
     * <br>Returns UNSET string if equals IUNSET {@value timgraph.IUNSET}
     * @param d double to print
     */
    static public String stringInt(int i){
        return (i==IUNSET?"UNSET":Integer.toString(i));}


    /**
     * Name for network.
     * <p>Currently uses root of name of initial input file.
     * @return name of network.
     */
     public String stringNetworkName(){
        return inputName.getNameRoot();
    }


    /**
     * Prints out results of dynamical checks of graph properties
     * @param sep separation string e.g. tab
     * @return string  describing results of dynamical checks
     */
    public String stringOfChecks(String sep){
        return "Dynamical checks:"+sep+" multi edges - "+sep+(this.checkMultiEdges()?"yes":"no")+sep+
                " negative weights - "+sep+(this.checkNegativeWeights()?"yes":"no")+sep+
                " self loops - "+sep+(this.checkSelfLoops()?"yes":"no");
    }

    public String stringDirected(){
        return (isDirected()?"":"un")+"directed";
    }
    public String stringWeighted(){
        return (isWeighted()?"":"un")+"weighted";
    }
    public String stringLabelled(){
        return "vertices "+(isVertexLabelled()?"":"un")+"labelled";
    }
    public String stringVertexEdgeList(){
        return " vertex->edge list "+(isVertexEdgeListOn()?"on":"off");
    }
    public String stringEdgeLabelled(){
        return "edges "+(this.isEdgeLabelled()?"":"un")+"labelled";
    }
    public String stringMultiEdge(){
        return "multiedges "+(this.isMultiEdge()?"":"not ")+"allowed";
    }
    public String stringSelfLoops(){
        return "self loops "+(this.isSelfLooped()?"":"not ")+"allowed";
    }
    public String stringBipartite(){
        return (isBipartite()?"bipartite":"unipartite");
    }
    public String graphTypeString(String sep){
        return stringDirected()+sep+stringWeighted()+sep+stringLabelled()+sep+stringEdgeLabelled()+sep+stringMultiEdge()+sep+stringSelfLoops()+sep+stringVertexEdgeList()+sep+stringBipartite();
    }
          
// --------------------------------------------------------------------------    
    /** 
     * Prints parameters to standard output.
     */
    public void printParam()  {
        printParametersBasic();
        System.out.println("Walk has "+SEP+ numevents +SEP+" events,  "); 
        printRandomWalkMode();
        System.out.println(connectivity +SEP+ " edges added per event, " 
                           +SEP+ averageWalkLength +SEP+ " averageWalkLength, "
                           +SEP + probnewvertex +SEP+ " new vertex prob." );
        System.out.println("Info level is "+SEP+infoLevel);
//        System.out.println(getrandomWalk.randomWalkModeString());
        System.out.println(binomialNumber +SEP+" no. of dice used in binomial random numbers generator");
        if (probnewvertex<1) {
          System.out.print("Prob of new vertex is "+SEP+probnewvertex+SEP+" using old ");
          if (SourceVertex) System.out.println("random vertices"); 
          else System.out.println("vertices at end of random edges");
        }
        // use outputControl setting to control network properties calculations and output
        outputControl.printMode(System.out,"");
     }             

    
            // --------------------------------------------------------------------------    
    /** 
     * Prints mode for random walk to standard output.
     */
    public void printRandomWalkMode()  
    {
        printRandomWalkMode(System.out,"");
        return;
     }      

        /**
     * 
     * Prints mode for random walk to printstream.
     * @param cc comment string
     * @param PS a PrintStream such as System.out
     */
    public void printRandomWalkMode(PrintStream PS, String cc)  
    {
        if ((randomWalkMode & 1)>0) PS.println(cc+"   Start every walk from random vertex");
        else  PS.println(cc+"   Start every walk from random end of random edge");
        if ((randomWalkMode & 2)>0) PS.println(cc+"   Jump to new vertex when walk length reached (every new edge)");
        else  PS.println(cc+"   Do not jump to new vertex when walk length reached (unless new event in walk graph creation or no exits in random walk");
        if ((randomWalkMode & 4)>0) PS.println(cc+"   Walk length variable, average fixed");
        else  PS.println(cc+"   Walk length fixed");
        if ((randomWalkMode & 8)>0) PS.println(cc+"   No. of edges added to each new vertex variable, average fixed");
        else  PS.println(cc+"   No. of edges added to each new vertex fixed");
        if ((randomWalkMode & 16)>0) PS.println(cc+"   Binomial distributed walk lengths");
        else  PS.println(cc+"   Markovian walk lengths");
    }
           

// --------------------------------------------------------------------------    
    /** 
     *        Prints Edge Generator method being used.
     *  @param PS PrintStream such as System.out
     */
    public void printEdgeGenerator(PrintStream PS)  
    {
        System.out.print("Edge generator is "+SEP+edgegenerator);
        if (edgegenerator==0) PS.println("   Walk routine used");
        if (edgegenerator==1) PS.println("   ER generator used");


     }      


// ************************************************************************

// -------------------------------------------------------------    
    /** 
     *  Returns string indicating initial graph type.      
     * <br>Set by initialgraph parameter.
     */
    public String initialGraphString(){return initialGraphString(initialgraph);}
    /** 
     *  Returns string indicating initial graph type.      
     *  @param n number of initial graph to use. 
     */
    public String initialGraphString(int n)  
    {
        String s="???";
        switch (n){
            case 0:  s="Empty"; break;
            case 1:  s="One vertex, no edges"; break;
            case 2:  s="Two vertices with two edges (both degree=2)"; break;
            case 3:  s="Two vertices, one edge"; break;
            case 4:  s="Four vertices, eight edges (all degrees=4)"; break;
            case 5:  s="Two vertices, four edges (both degree=4)"; break;
            case 6:  s="Five vertices, four edges (star shape)"; break;
            case 7:  s="Five vertices, four edges"; break;
            case 8:  s="BowTie - Five vertices, six edges (two triangles with common vertex)"; break;
            case 9:  s="Long BowTie - Six vertices, seven edges (two triangles with edge between two of their vertices)"; break;
            case 10:  s="Nuclear - seven vertices, nine edges (three triangles with single common vertex)"; break;
            case 99: s="Input graph from file of specified extension, input file name "+this.inputName.getFullFileName(); break;
            case 100: s="Graph constructed from algorithm, graph name "+this.inputName.getNameRoot(); break;
            case -1:  s="Line"; break;
            case -2:  s="Ring"; break;
            case -3:  s="2D rectangular lattice"; break;
            case -4:  s="2D Torus"; break;
            case -5:  s="3D rectangular lattice"; break;
            case -6:  s="3D Torus"; break;
            case -11:  s="CaveMan Line"; break;
            case -12:  s="CaveMan Ring"; break;
            case -13:  s="CaveMan Common Vertex Line"; break;
            case -14:  s="CaveMan Common Vertex Ring"; break;
            default: s="Unknown Type";
        }   
        return s;
    }

    // -------------------------------------------------------------    
    /** 
     *  Set initial graph number.      
     * @param n number of initial graph
     */
    public void setInitialGraph(int n){
        initialgraph=n;
    }  
    
    /** 
     *  Set initial graph number to indicate constructed by external algorithms.
     * <P>This is type 100.
     */
    public void setInitialGraphExternalAlgorithm(){initialgraph=100;}

    /** 
     *  Set initial test graphs.      
     *  <br>Network chosen is set by the <tt>initialgraph</tt> parameter.
     * @param checkOn true if want basic check of number of edges and vertices.
     */
    public void setNetworkInitialGraph(boolean checkOn){
        setNetwork(initialgraph);
        if (checkOn){
          calcNumberVertices();
          calcNumberEdges();
        }
    }  
    
    
//    /**
//     * Sets up network from input file.
//     * <br>The ending in <tt>inputName</tt> is compared against the <tt>InputFileType</tt>
//     */
//    public void setNetworkFromInputFileXXX() {
//        //System.out.println("Setting up from input file:- "+inputFileType.toLongString());
//        if (this.extensionList.inputName.getNameEnd()) {
//                int res = setNetworkPajek(inputName.getNameEnd(), directedGraph, weightedEdges);
//                initialVertices = TotalNumberVertices;
//                initialEdges = TotalNumberStubs;
//                return;
//            }
//            case 1: {
//                int res = setNetworkEdgeList(inputName.getNameEnd(), directedGraph, weightedEdges, vertexlabels, labelledEdges, true);
//                initialVertices = TotalNumberVertices;
//                initialEdges = TotalNumberStubs;
//                return;
//            }
//            case 2: {
//                int res = setNetworkEdgeList(inputName.getNameEnd(), directedGraph, weightedEdges, vertexlabels, labelledEdges, false);
//                initialVertices = TotalNumberVertices;
//                initialEdges = TotalNumberStubs;
//                return;
//            }
//            case 3: {
//                int res = setNetworkGMLInput(inputName.getNameEnd(), weightedEdges, vertexlabels);
//                initialVertices = TotalNumberVertices;
//                initialEdges = TotalNumberStubs;
//                return;
//            }
//            case 4: {
//                int res = setNetworkMatrixInput(inputName.getNameEnd());
//                initialVertices = TotalNumberVertices;
//                initialEdges = TotalNumberStubs;
//                return;
//            }
//            case 5: {
//                int res = setNetworkVertexNeighbourList(inputName.getNameEnd(), directedGraph, vertexlabels, false);
//                initialVertices = TotalNumberVertices;
//                initialEdges = TotalNumberStubs;
//                return;
//            }
//        }// eo switch
//        
//        throw new RuntimeException("Input file extension " + inputName.getNameEnd() + " unknown"); 
//
//    }
    /**
     * Sets up network from input file.
     * <p>One file only used, as indicated by extension of current <tt>inputname</tt> variable.
     * <br>The ending in <tt>inputName</tt> is compared against the
     * {@link TimGraph.io.InputFileType#extensionList} <tt>InputFileType</tt>.
     */
    public int setNetworkOnlyFromInputFile() {
        initialgraph=99;
        int res =-1;
        if (!inputFileType.setFromExactName(inputName.getNameEnd())) {
            throw new RuntimeException("Input file extension " + inputName.getNameEnd() + " unknown");
        }

        System.out.println("Setting up from input file:- "+inputFileType.toLongString());
        switch (inputFileType.getNumber()) {
            case 0: {
                res = setNetworkPajek(inputName.getNameEnd(), directedGraph, weightedEdges);
                initialVertices = TotalNumberVertices;
                initialEdges = TotalNumberStubs;
                return res;
            }
            case 1: {
                res = setNetworkEdgeList(inputName.getNameEnd(), directedGraph, weightedEdges, vertexlabels, labelledEdges, true);
                initialVertices = TotalNumberVertices;
                initialEdges = TotalNumberStubs;
                return res;
            }
            case 2: {
                res = setNetworkEdgeList(inputName.getNameEnd(), directedGraph, weightedEdges, vertexlabels, labelledEdges, false);
                initialVertices = TotalNumberVertices;
                initialEdges = TotalNumberStubs;
                return res;
            }
            case 3: {
               res = setNetworkGMLInput(inputName.getNameEnd(), weightedEdges, vertexlabels);
                initialVertices = TotalNumberVertices;
                initialEdges = TotalNumberStubs;
                return res;
            }
            case 4: {
                res = setNetworkMatrixInput();
                initialVertices = TotalNumberVertices;
                initialEdges = TotalNumberStubs;
                return res;
            }
            case 5: {
                boolean intList=false; 
                boolean bipartite=false; 
                boolean  forceLowerCase=true;
                boolean checkBipartite=false;
                res = setNetworkVertexNeighbourList(inputName.getNameEnd(), weightedEdges, directedGraph, multiEdge, vertexlabels, intList, bipartite, forceLowerCase, false, 1, null, null, null);
                initialVertices = TotalNumberVertices;
                initialEdges = TotalNumberStubs;
                return res;
            }
            case 6: {
                boolean intList=false; 
                boolean bipartite=true; 
                boolean  forceLowerCase=true;
                boolean checkBipartite=true;
                res = setNetworkVertexNeighbourList(inputName.getNameEnd(), weightedEdges, directedGraph, multiEdge, vertexlabels, intList, bipartite, forceLowerCase, false, 1, null, null, null);
                initialVertices = TotalNumberVertices;
                initialEdges = TotalNumberStubs;
                return res;
            }
            case 7: {
                boolean columnHeaderOn= true;
                boolean rowLabelOn= true;
                boolean trimLabelWhiteSpaceOn=true;
                res = setNetworkMatrixInput(columnHeaderOn, rowLabelOn, trimLabelWhiteSpaceOn);
                initialVertices = TotalNumberVertices;
                initialEdges = TotalNumberStubs;
                return res;
            }
            
        }// eo switch
        return res;

    }

    /**
     * Sets up network and vertex values from input files.
     * <p>Basic network file used is indicated by ending of current <tt>inputName</tt>
     * which is compared against the <tt>InputFileType</tt>.
     * Also looks for <tt>inputXY.dat</tt> to set x and y positions of vertices
     * from 1st and 2nd columns respectively, and
     * names of vertices set from first column of <tt>inputNames.dat</tt> file.
     * It is assumed these last two, if present, have no header row.
     * @return
     */
    public int setNetworkFromInputFile() {
      int xColumn=1;
      int yColumn=2;
      int nameColumn=1;
      boolean headerOn=false;
      boolean infoOn=false;
      return setNetworkFromInputFile(xColumn, yColumn, nameColumn, headerOn, infoOn);
    }

    /**
     * Sets up network from input file.
     * <p>Basic file used is indicated by ending of current in <tt>inputName</tt> 
     * which is compared against the <tt>InputFileType</tt>.
     * Also looks for <tt>inputXY.dat</tt> to set x and y positions of vertices and
     * names of vertices set from <tt>inputNames.dat</tt> file.
     * @param xColumn column of <tt>inputXY.dat</tt> file used to set x coordinate (counting from 1)
     * @param yColumn column of <tt>inputXY.dat</tt> file used to set y coordinate (counting from 1)
     * @param nameColumn number of column with names in <tt>inputNames.dat</tt> file (counting from 1)
     * @param headerOn true (false) to ignored (don't ignore) first line of file
     * @param infoOn true (false) if want (no) information.
     * @return
     */
    public int setNetworkFromInputFile(int xColumn, int yColumn, int nameColumn, boolean headerOn, boolean infoOn) {

        int res=setNetworkOnlyFromInputFile();
        String vertexPositionsFullFileName=inputName.getNameRootFullPath()+"inputXY.dat";
         try {
             setVertexPositionsFromFile(vertexPositionsFullFileName,"#", xColumn, yColumn,  headerOn, infoOn);
         }
         catch (Exception e){
             System.out.println("Unable to set vertex coordinates from file "+vertexPositionsFullFileName+" - "+e);
         }
         try {
              setVertexNamesFromFile("inputNames.dat","#", nameColumn, headerOn, infoOn);
         }
         catch (Exception e){
             System.out.println("Unable to set vertex names from file - "+e);
         }
         try {
              setVertexNumbersFromFile("inputNumbers.dat","#", nameColumn, headerOn, infoOn);
         }
         catch (Exception e){
             System.out.println("Unable to set vertex numbers from file - "+e);
         }
         return res;
}

    /** 
     *  Set initial test graphs.      
     *  <br>The networks set up to be small simple graphs
     *  those chosen by negative numbers are disconnected.
     *  <p><B>Must set initial vertices and edges properly where appropriate.</b>
     *  @param n number of initial graph to use. 0=empty, 2 = two edges between two initial vertices
     */
    void setNetwork(int n)  
    {
        if (infoLevel>-1) System.out.println("Initial Graph Type "+n+": "+initialGraphString(n));
           
        if (n==99){ setNetworkFromInputFile(); return;} 
        
       // Must set initial vertices and edges correctly to allow for correct calculation of total after second (growth) phase 
       switch (n){
          case -14: //Cave Man Common Vertex ring
          case -13: //Cave Man Common Vertex line
          {
                //initialVertices =  initialVertices;
                int cSize = initialVertices/initialXsize; // Basic number of vertices in a community
                initialEdges = (cSize*(cSize+1)+2*initialConnectivity)*initialXsize+1; // overestimate  if periodic
                break;
          }
          case -12: //Cave Man ring
          case -11: //Cave Manline
          {
                //initialVertices =  initialVertices;
                int cSize = initialVertices/initialXsize; // Basic number of vertices in a community
                initialEdges = (cSize*(cSize-1)+2*initialConnectivity)*initialXsize; // overestimate  if periodic
                break;
          }
          case -6: // 3d torus
          case -5: // 3d rectangular lattice (non-periodic)
          {
                //initialVertices =  initialVertices;;
                initialEdges = initialVertices*6*initialConnectivity; // overestimate if periodic
                break;
          }
          case -4: //torus
          case -3: //rectangular lattice (non-periodic)
          {
                //initialVertices =  initialVertices;
                initialEdges = initialVertices*4*initialConnectivity; // overestimate if periodic
                break;
          }
          case -2: //ring
          case -1: //line
          {
                //initialVertices =  initialVertices;;
                initialEdges = initialVertices*2*initialConnectivity; // overestimate  if periodic
                break;
          }
          case 0:
          {
                initialVertices =  0;
                initialEdges = 0;
                break;
          }
          case 1:
          {
                initialVertices =  1;
                initialEdges =  0;
                break;
          }
          case 2:
          {
                initialVertices = 2;
                initialEdges = 4;
                break;
          }
          case 3:
          {
                initialVertices =  2;
                initialEdges =  2;
                break;
          }
          case 4:
          {
                initialVertices =  4;
                initialEdges =  8;
                break;
          }
          case 5:
          {
                initialVertices =  2;
                initialEdges =  8;
                break;
          }
          case 6:
          {
                initialVertices =  5;
                initialEdges =  8;
                break;
          }
          case 7:
          {
                initialVertices =  5;
                initialEdges = 8;
                break;
          }
          case 8:
          {
                initialVertices =  5;
                initialEdges = 12;
                break;
          }
          case 9:
          {
                initialVertices =  6;
                initialEdges = 14;
                break;
          }
          case 10:
          { // nuclear
                initialVertices =  7;
                initialEdges = 18;
                break;
          }
          default:
          {
              System.err.println("***ERROR unknown initial graph type");
              return;
          }
        } //eo switch
       maximumVertices = numevents + initialVertices;
       maxexpectedvertices = probnewvertex*numevents + initialVertices;
       maxexpectededges = numevents*connectivity*2 + initialEdges;
       maximumStubs = ((int) (maxexpectededges+5*Math.sqrt(maxexpectededges)+0.5));
       if (infoLevel>-1)
       {
           System.out.println("Initial vertices and edges are " + initialVertices + SEP + initialEdges);
       System.out.println("Maximum vertices and edges are " + maximumVertices + SEP + maximumStubs);
       }
       
       
       
       if (n<0) {
           // set up generic types of graph
            switch(initialgraph){
                case -1:
                    setNetworkLine(false);
                    break;
                case -2:
                    setNetworkLine(true);
                    break;
                case -3:
                    setNetworkSquareLattice(false); 
                    break;
                case -4:
                    setNetworkSquareLattice(true); 
                    break;
                case -5:
                    setNetworkCubicLattice(false); 
                    break;
                case -6:
                    setNetworkCubicLattice(true); 
                    break;
                case -11:
                    setNetworkCaveManLine(false);
                    break;
                case -12:
                    setNetworkCaveManLine(true);
                    break;
                case -13:
                    setNetworkCaveManCommonVertex(false);
                    break;
                case -14:
                    setNetworkCaveManCommonVertex(true);
                    break;
                
                
            }
            initialEdges = TotalNumberStubs;
            initialVertices =  TotalNumberVertices;

            return;
        } // eo n<0


       
       // Now set up specific graphs
       setNetwork();  // sets up an empty graph     

        
       switch (n){
          case 0:
          {
              break;
          }
          case 1:
          {
            int v0 = addVertex(); 
            break;
          }
           case 3:
          {
            int v0 = addVertex(); 
            int v1 = addVertex(); 
            addEdge(v0,v1);
            break;
          }
            case 4:
          {
            int v0 = addVertex(); 
            int v1 = addVertex(); 
            int v2 = addVertex();
            int v3 = addVertex();
            addEdge(v0,v1);
            addEdge(v1,v0);
            addEdge(v1,v2);
            addEdge(v2,v3);
            addEdge(v3,v2);
            addEdge(v3,v0);
            addEdge(v0,v2);
            addEdge(v1,v3);
            break;
          }
            case 5:
          {
            int v0 = addVertex(); 
            int v1 = addVertex(); 
            addEdge(v0,v1);
            addEdge(v0,v1);
            addEdge(v1,v0);
            addEdge(v1,v0);
            break;
          }
            case 6:
          {
            int v0 = addVertex(); 
            int v1 = addVertex(); 
            int v2 = addVertex();
            int v3 = addVertex();
            int v4 = addVertex();
            addEdge(v0,v1);
            addEdge(v0,v2);
            addEdge(v0,v3);
            addEdge(v0,v4);
            break;
          }

           case 7:
          {
              int v0 = addVertex(); 
              int v1 = addVertex(); 
              int v2 = addVertex();
              int v3 = addVertex();
              int v4 = addVertex();
              addEdge(v0,v1);
              addEdge(v0,v2);
              addEdge(v0,v3);
              addEdge(v0,v4);
              break;
          }
          case 8:
          {
              int v0 = addVertex(); 
              int v1 = addVertex(); 
              int v2 = addVertex();
              int v3 = addVertex();
              int v4 = addVertex();
              addEdge(v0,v1);
              addEdge(v0,v2);
              addEdge(v1,v2);
              addEdge(v2,v3);
              addEdge(v2,v4);
              addEdge(v3,v4);
              break;
          }
          case 9:
          {
              int v0 = addVertex(); 
              int v1 = addVertex(); 
              int v2 = addVertex();
              int v3 = addVertex();
              int v4 = addVertex();
              int v5 = addVertex();
              addEdge(v0,v1);
              addEdge(v0,v2);
              addEdge(v1,v2);
              addEdge(v2,v5);
              addEdge(v5,v3);
              addEdge(v5,v4);
              addEdge(v3,v4);
              break;
          }
          case 10:
          {   // nuclear
              int v0 = addVertex(); 
              int v1 = addVertex(); 
              int v2 = addVertex();
              int v3 = addVertex();
              int v4 = addVertex();
              int v5 = addVertex();
              int v6 = addVertex();
              addEdge(v0,v1);
              addEdge(v0,v2);
              addEdge(v1,v2);
              addEdge(v0,v3);
              addEdge(v0,v4);
              addEdge(v3,v4);
              addEdge(v0,v5);
              addEdge(v0,v6);
              addEdge(v5,v6);
              break;
          }
          case 2:{}
          default:
          {
            int v0 = addVertex();
            int v1 = addVertex(); 
            addEdge(v0,v1);
            addEdge(v1,v0);
          }
        }
       
       if (weightedEdges) for (int e=0; e<TotalNumberStubs; e++) edgeValuetList[e] = new EdgeValue(0,1);
       
        if (initialVertices != TotalNumberVertices) System.out.println("*** ERROR initialVertices != TotalNumberVertices "+ initialVertices +SEP + TotalNumberVertices );
        if (initialEdges    != TotalNumberStubs   ) System.out.println("*** ERROR initialEdges    != TotalNumberEdges "+ initialEdges +SEP + TotalNumberStubs );
            initialEdges = TotalNumberStubs;
            initialVertices =  TotalNumberVertices;

    }

   
    
// -------------------------------------------------------------

    /**
     *  Set up graph with vertices but no edges.
     * <p>Has <tt>maxV</tt> vertices with no edges,
     * can take no more vertices but can have <tt>maxS</tt> stubs.
     *  <br>Uses globals maximumVertices and maximumStubs to set capacity.
     * Key basic routine used to define empty graph ready to be filled by other routines.
     * @param maxV maximum and actual number of vertices
     * @param maxS maximum number of stubs (twice the number of edges)
     */
    public void setNetworkWithVertices(int maxV, int maxS)
    {
      setNetwork(maxV, maxS);
      if (vertexlabels) for (int v=0; v<this.maximumVertices; v++) addVertex(new VertexLabel()) ;
      else for (int v=0; v<maximumVertices; v++) addVertex( ) ;
    }


    /** 
     *  Set up empty graph capable of holding maximumVertices and maximumStubs.
     *  <br>Uses globals maximumVertices and maximumStubs to set capacity.
     * Key basic routine used to define empty graph ready to be filled by other routines.
     * <br>Does NOT initialise any vertices or edges
     * @param maxV maximum number of vertices
     * @param maxS maximum number of stubs (twice the number of edges)
     */
    public void setNetwork(int maxV, int maxS)  
    {
        this.maximumVertices=maxV;
        this.maximumStubs=maxS;
        setNetwork();
    }
    
    
    /** 
     *  Set up empty graph capable of holding maximumVertices and maximumStubs.
     *  <br>Uses globals maximumVertices and maximumStubs to set capacity.
     * Key basic routine used to define empty graph ready to be filled by other routines.
     */
    public void setNetwork()  
    {
       //System.out.println("Maximum vertices and edges are " + maximumVertices + SEP + maximumStubs);
       setNetworkVertices();
       setNetworkEdges();
    }
    /**
     *  Set up empty graph capable of holding maximumVertices.
     *  <br>Uses globals maximumVertices to set capacity.
     * Key basic routine used to define empty vertices ready to be filled by other routines.
     */
    public void setNetworkVertices()
    {
       //System.out.println("Maximum vertices and edges are " + maximumVertices + SEP + maximumStubs);
       vertexList = new IntArrayList[maximumVertices];
       if (vertexEdgeListOn) {
           vertexEdgeList = new IntArrayList[maximumVertices];
           if (directedGraph) vertexInEdgeList = new IntArrayList[maximumVertices];
       }
       if (directedGraph) vertexSourceList = new IntArrayList[maximumVertices];
       if (vertexlabels) vertexLabelList = new VertexLabel[maximumVertices];
       TotalNumberVertices=0;
    }

    /**
     * Set up empty edges capable of holding maximumStubs.
     * <br>Uses globals maximumStubs to set capacity.  Destroys
     * all existing edge information.
     * Basic routine used to define empty edges ready to be filled by other routines.
     */
    public void setNetworkEdges()
    {
       stubSourceList = new int[maximumStubs];
       if (weightedEdges) edgeValuetList = new EdgeValue[maximumStubs];
       TotalNumberStubs =0;
    }

    
    /** 
     *  Set up graph from given matrix.
     *  <br>Dimension of matrix first row used to set dimension of martic, assumed to be square.
     * <br>If not a directed graph then lower triangle of matrix is just ignored.
     * @param matrix adjacency matrix as doubles
     */
    public void setNetworkFromMatrix(double [][] matrix)  
    {
        setNetworkFromMatrix(null, matrix); 
    }
 
    /** 
     *  Set up graph from given matrix.
     *  <br>Dimension of matrix first row used to set dimension of matrix, assumed to be square.
     * <br>If not a directed graph then lower triangle of matrix is just ignored.
     * <br>If a non-null vector of names is given then it is used for vertex names.
     * @param vertexName list of names of the vertices, null is acceptable.
     * @param matrix adjacency matrix as doubles
     */
    public void setNetworkFromMatrix(String[] vertexName, double [][] matrix)  
    {
       int dimension=matrix[0].length;
       boolean useNames=false;
       if (vertexName!=null) useNames=true;
       if ((useNames) && (vertexName.length!=dimension)) throw new RuntimeException("*** number of names "+vertexName.length+" does not match dimension of matrix "+dimension+" in setNetworkFromMatrix");
       maximumVertices=dimension;
       maximumStubs=0;
       int diagonalEntry=0;
       int offDiagonalEntry=0;
       boolean symm=true;
       for (int i=0; i<dimension; i++)
          for (int j=0; j<dimension; j++) {
              if (matrix[i][j]!=0) {if (i==j) diagonalEntry++; else offDiagonalEntry++;}
              if (!isDirected() && symm && (matrix[i][j]!=matrix[j][i])) {
                  System.out.println("!!!Warning undirected graph by unsymmetrical matrix, entry "+i+", "+j);
                  symm=false;
              }
          }
       if (isDirected()) maximumStubs=2*(offDiagonalEntry+diagonalEntry);
       else maximumStubs=offDiagonalEntry+2*diagonalEntry;
       setNetwork();
       VertexLabel vl = new VertexLabel();
       if (vertexlabels) for (int v = 0; v < dimension; v++) {
                                if (useNames) vl.setName(vertexName[v]);
                                else vl.setName(Integer.toString(v));
                                addVertex(vl);
                            }
       else for (int v = 0; v < dimension; v++) addVertex();
       
       for (int s=0; s<dimension; s++)
          for (int t=(isDirected()?0:s); t<dimension; t++)
          {
              if(matrix[s][t]==0) continue;
              if (isWeighted()) addEdge(s,t,matrix[s][t]);
              else addEdge(s,t);
          } //eo for t  
    }


    
// -------------------------------------------------------------    
    /** 
     * Set up graph using a Molloy-Reed projection from a stub list to define a graph.
     * Unless shuffle is on, stubs (2i-1) and (2i) make an edge with last odd stub ignored if it exists.
     *  Set up empty graph capable of holding maximumVertices and maximumStubs.
     *  Uses globals directedGraph, vertexlabels, weightedEdges to set types of vertex and edge, 
     *  and globals maximumVertices and maximumStubs to set capacity.
     *@param numberVertices number of vertices
     *@param stubList list of integers between 0 and (maxVertices-1) giving stubs of edges (ignores last if odd) source then target if directed
     *@param shuffleOn if true shuffles the stublist[] before use (and this is returned shuffled).
     **/
     void setNetwork(  int numberVertices, int [] stubList, boolean shuffleOn)  
    {
       //System.out.println("Maximum vertices and edges are " + maximumVertices + SEP + maximumStubs);    
         
         setNetwork();
         
//       vertexList = new IntArrayList[maximumVertices];
//       if (directedGraph) vertexSourceList = new IntArrayList[maximumVertices];
//       if (vertexlabels) vertexLabelList = new VertexLabel[maximumVertices];
//       TotalNumberVertices=0;
       
       if (vertexlabels) for (int v=0; v<numberVertices; v++) addVertex(new VertexLabel()) ;
       else for (int v=0; v<numberVertices; v++) addVertex( ) ;

       // create edges
//       stubSourceList = new int[maximumStubs];
//       if (weightedEdges) edgeValuetList = new EdgeValue[maximumStubs];
//       TotalNumberStubs =0;
       int v1=-1;
       int v2=-1;
       EdgeValue w1 = new EdgeValue();
       EdgeValue w2 = new EdgeValue();
       int ne = (stubList.length >> 1)<<1; // round down to even number of edges
       //
       if (shuffleOn)
       {
           double N = stubList.length;
           int value=-999;
           int r=-1;
           for (int i = 0; i < N; i++) {
            r = (int) (Math.random() * N );  // allow to shuffle to self - faster than checking i!=r everytime?
            value = stubList[i];
            stubList[i]=stubList[r];
            stubList[r]=value;
        }
       }
       // Now connect up stubs (2i) and (2i+1)
       for (int e=0; e<ne;) 
            {
                v1 = stubList[e++];
                v2 = stubList[e++];
                if (weightedEdges) addEdge(v1,v2, w1, w2);
                else addEdge(v1,v2);
           }            
                             
//       if (TNS!=TotalNumberStubs)  {
//           System.out.println("*** Total Number Edges wrong in timgraph constructor");
//           return 1;
//       }
    
    
     }
    
    
// -------------------------------------------------------------    
    /** 
     * Set network from a pajek network file.
     *@param ext is the ending on the filename+inputDirName of file.
     * @param directed true if directed graph being input
         *@param weighted true if weighted graph being input
         *@return flag from   input.processPajekFile
     */
    public int setNetworkPajek(String ext, boolean directed, boolean weighted)  
    {
       FileInput input = new FileInput(this.infoLevel);
       return input.processPajekFile(this,ext, directed, weighted);       
    }

// -------------------------------------------------------------    
    /** 
     * Set network from an edge list file.
     * <br>Assumes source in in first column, target in second, weights (if required) are in third
     * and edge labels (if required) are in fourth.
     * No header lines, no comments in file.
     * File is <tt>inputDirName</tt>+<tt>inputNameRoot</tt>+<tt>ext</tt>
     *@param ext is the ending on the name of file.
     *@param directed true if directed graph being input
     *@param weighted true if weighted graph being input
     *@param edgeLabelled true if edges have labels in graph being input
     *@param vertexLabelled true if want vertex labels
     *@param intList true if edge list is in integers, otherwise strings with no white space assumed
     *@return flag from   input.processPajekFile
     */
    public int setNetworkEdgeList(String ext, boolean directed, boolean weighted, 
            boolean vertexLabelled , boolean edgeLabelled, boolean intList)
    {
       FileInput input = new FileInput(this.infoLevel);
       if (intList) return FileInput.processIntEdgeFile(this, ext, 1, 2, (weighted?3:-1), (edgeLabelled?4:-1), directed, vertexLabelled, (infoLevel>1) );
       return input.processStringEdgeFile(this, ext, 1, 2, (weighted?3:-1), (edgeLabelled?4:-1), directed, vertexLabelled );       
    }

// -------------------------------------------------------------    
    /** 
     * Reads in list of neighbours of each vertex labelled by strings from file filename.
     * <p>Uses {@link TimGraph.io.FileInput#processStringVertexNeighbourFile(TimGraph.timgraph, java.lang.String, boolean, boolean, boolean, boolean, boolean, boolean, boolean, int, TimUtilities.StringUtilities.Filters.StringFilter, TimUtilities.StringUtilities.Filters.StringFilter, java.util.TreeSet) }
     * so see this for full details.
     * <br>Each line has entries separated by whitespace.
     * First entry is the source vertex and the remaining entries on that line are its neighbours.
     * Can deal with bipartite structures.
     * If <tt>edgeWeightLL</tt>, if given as null then weights are not set.  If it is set then
     * the weight is the 1/(number target vertices on line).  This only makes sense
     * if it is a bipartite or directed graph and if the source vertices (first column) are unique.
     * <br>No header lines, no comments in file.
     * File is <tt>inputDirName</tt>+<tt>inputNameRoot</tt>+<tt>ext</tt>
     * <br>Edge weights are defined to be <tt>1/degree<tt> of the
     * source node so only makes sense for bipartite or directed graphs.
     *@param ext is the ending on the name of file.
     *@param weighted true if weights assigned
     *@param directed true if directed graph being input
     *@param vertexLabelled true if want vertex labels
     *@param intList true if edge list is in integers, otherwise strings with no white space assumed
     *@param bipartite indicates that bipartite graph is expected
     *@param forceLowerCase force all strings to be lower case
     *@param checkBipartite if true will check bipartite nature if appropriate
     *@param sampleFrequency number of lines to skip, 1 or less and all are taken
     *@param stringFilterSource filter to apply to source vertices
     *@param stringFilterTarget filter to apply to source vertices
     *@param filterL list of vertices left out due to filters
     */
    public int setNetworkVertexNeighbourList(String ext, boolean weighted, boolean directed, boolean multiedge, boolean vertexLabelled, boolean intList,
            boolean bipartite, boolean forceLowerCase, boolean checkBipartite, int sampleFrequency,
            StringFilter stringFilterSource,
            StringFilter stringFilterTarget,
            TreeSet<String> filterL)
    {
       FileInput input = new FileInput(this.infoLevel);
       if (intList) return -952384;
       return input.processStringVertexNeighbourFile(this, ext, weighted, directed, multiEdge, vertexLabelled, bipartite,
                                                     forceLowerCase, checkBipartite, sampleFrequency, 
                                                     stringFilterSource, stringFilterTarget, filterL );       
    }

 // -------------------------------------------------------------    
    /** 
     * Set network from a tab separated adjacency matrix file.
     * <br>No header lines, no comments in file.
     * Directed, weighted and labelled nature set by current parameters.
     *@return flag from   input.processAdjacencyMatrixTabSeparated()
     */
    public int setNetworkMatrixInput()
    {
       boolean headerOn=false;
       boolean rowLabelOn=false;
       boolean trimLabelWhiteSpaceOn=false; // should be irrelevant
       return setNetworkMatrixInput(headerOn, rowLabelOn, trimLabelWhiteSpaceOn);
    }

    /** 
     * Set network from a tab separated adjacency matrix file.
     * <br>Row and column labels can be used as vertex labels, comments in file.
     * Directed, weighted and labelled nature set by current parameters.
     * Set network from a labelled adjacency matrix file file.
     * @param headerOn first row is a label for the columns
     * @param rowLabelOn first column is a label for the row
     * @param trimLabelWhiteSpaceOn if true removes whites space from head and tail of labels
     * @return flag from   input.processAdjacencyMatrixTabSeparated()
     */
    public int setNetworkMatrixInput(boolean headerOn, boolean rowLabelOn, boolean trimLabelWhiteSpaceOn)
    {
       FileInput input = new FileInput(this.infoLevel);
       return input.processAdjacencyMatrixTabSeparated(this, headerOn, rowLabelOn, trimLabelWhiteSpaceOn);
    }

   
// -------------------------------------------------------------    
    /** 
     * Set network from a <tt>gml</tt> file.
     * <br>Not all aspects of gml fiel formats used, rest should be ignored.
     * Should be able to read Newman files.
     * File is <tt>inputDirName</tt>+<tt>inputNameRoot</tt>+<tt>ext</tt>
     * <br> See <code>http://www.infosun.fim.uni-passau.de/Graphlet/GML/gml-tr.html</code>
     * for details of gml file format.
     *@param ext is the ending on the name of file.
     *@param weighted true if weighted graph being input
     *@param vertexLabelled true if want vertex labels
     *@return flag from   input.processGMLFile
     */
    public int setNetworkGMLInput(String ext, boolean weighted, boolean vertexLabelled )
    {
       FileInput input = new FileInput(this.infoLevel);
       return input.processGMLFile(this, ext, weighted, vertexLabelled );       
    }

    // ---------------------------------------------------------------------------------

    /** 
     * Creates a network in shape of line or ring using global parameters.
     * Uses globals initialVertices and initialConnectivity .
     * @param ring true if make periodic
     */
    public void setNetworkLine(boolean ring) 
    {
      setNetworkLine(initialVertices, initialConnectivity,  ring);  
    }
    
    
    // ---------------------------------------------------------------------------------

    /** 
     * Creates a network in shape of line or ring.
     * @param nVertices total number of vertices
     * @param m number of neighbours in one direction (so total is 2m)
     * @param ring true if make periodic
     */
    public void setNetworkLine(int nVertices, int m, boolean ring) 
    {
      if (nVertices>maximumVertices) maximumVertices=nVertices;
      int e = nVertices*m*2; // remember count as directed edges
      if (maximumStubs<e) maximumStubs=e;
      setNetwork();  // set up edges and initialise graph
      for (int v=0; v<nVertices; v++) addVertex();
      int vn=-1;
      for (int v=0; v<nVertices; v++){
          for (int n=1; n<=m; n++)
          {
              vn=v+n;
              if ((vn>=nVertices) && (!ring) ) continue; 
              vn=vn%nVertices;
              addEdge(v,vn);
          }
      }
    }
    
    // ---------------------------------------------------------------------------------

    /** 
     * Creates a two-dimensional lattice as close to a square lattice.
     * Uses globals initialVertices and initialConnectivity to set parameters.
     * If global initialXsize is less than one then uses square root of number of vertices to set size.  
     * Will choose as close to a square as possible but will always force a lattice, 
     * periodic if requested.
     * @param periodic true if make periodic  
     **/
    public void setNetworkSquareLattice(boolean periodic)
    { 
        int xnumber =initialXsize;
        if (xnumber<1) xnumber = (int)(0.5+Math.sqrt(initialVertices));
        setNetworkSquareLattice(initialVertices, initialConnectivity, xnumber,  periodic); 
    }
    

// ---------------------------------------------------------------------------------

   /** 
     * Creates a two-dimensional square lattice.
     * Will builds up in row of size xLength.  
     * If nVertices is not exactly divisible by xLength then still creates
     * a lattice (periodic if needed).
     * @param nVertices total number of vertices
     * @param m number of neighbours in ONE direction (so total is 4m)
     * @param xLength number of sites in x direction
     * @param periodic true if make periodic  
     **/
 public void setNetworkSquareLattice(int nVertices, int m, int xLength, boolean periodic)
 {
   if (nVertices > maximumVertices) maximumVertices = nVertices;
   int x1dim = xLength;
   if (x1dim>nVertices)
   {
    x1dim = (int) Math.sqrt(nVertices);
    }
   int zdim = (nVertices-1)/ x1dim+1;
   int e = nVertices*m*2;
   if (maximumStubs<e) maximumStubs=e;
   setNetwork(); // set up empty graph with maximumStubs/Vertices capacity
   for (int v=0; v<nVertices; v++) addVertex(); //set up the vertices
   int v=-1;
   int vn=-1;
   int x1n=-1;
   int zn=-1;
   //EdgeValue ew = new EdgeValue();
   for (int z=0; z<zdim; z++)
   {
     for (int x1=0; x1<x1dim; x1++)
       {
          v = (x1+x1dim*z); //sL2V(x1,x2,z1,xdim,xdim)
          if (v>=nVertices) return; // ??? >= or just = ???
          for (int n=1; n<=m; n++)
          {
            // add neighbour in x1 direction 
            x1n = x1 + n;
            if ((x1n<x1dim) || periodic)
               {
               vn = (x1n%x1dim+x1dim*z);
               if ((vn<nVertices) || periodic) addEdge(v, (vn%nVertices));
               }
              // add neighbour in x1 direction 
            zn = z + n;
            if ((zn<zdim) || periodic)
               {
               vn = (x1+x1dim*(zn%zdim));
               if ((vn<nVertices) || periodic) addEdge(v, (vn%nVertices));
               }
               
          }// eo n

       } // eo x1
     
   } //eo z
   
 } //  setNetworkSquareLattice
 
 /** 
     * Creates a two-dimensional rectangular lattice.
     * Will builds up in lines of size xnumber.  
     * If nVertices is not exactly divisible by xnumber then still creates
     * a lattice (periodic if needed).
     * @param nVertices total number of vertices
     * @param m number of neighbours in ONE direction (so total is 4m)
     * @param xnumber number of sites in x direction
     * @param periodic true if make periodic  
     **/
    public void setNetworkSquareLatticeOLD(int nVertices, int m, int xnumber,  boolean periodic) 
    {
      if (nVertices>maximumVertices) maximumVertices=nVertices;
      int ynumber = (nVertices-1)/xnumber+1;
      int e = nVertices*m*4; // remember count as directed edges
      if (maximumStubs<e) maximumStubs=e;
      setNetwork();  // set up edges and initialise graph
      for (int v=0; v<nVertices; v++) addVertex();
      int v=-1;
      int vn=-1;
      int xn=-1;
      int yn=-1;
      //EdgeValue ew = new EdgeValue();
      for (int y=0; y<ynumber; y++){
          for (int x=0; x<xnumber; x++)
          {
              v=x+y*xnumber;
              if (v>nVertices) continue;
              for (int n=1;n<=m;n++)
              {
                 // neighbour in x direction
                 xn=x+n;
                 if ((xn<xnumber) || periodic)
                 {
                          vn=(xn%xnumber)+y*xnumber;
                          if ((vn<nVertices) || (periodic) ) addEdge(v,(vn%nVertices));
                 }
               // neighbour in y direction
                 vn=x+(y+n)*xnumber;
                 if ((vn<nVertices) || (periodic) ) addEdge(v,(vn%nVertices));
                 
              }// for n
          }//for x
      }// for y
    }// eo setNetworkSquareLatticeOLD


    /** 
     * Creates a three-dimensional lattice as close to a cube as possible.
     * Uses globals initialVertices and initialConnectivity to set parameters.
     * Globals initialXsize and initialYsize set s and y dimensions.  If initialXsize is less than one then
     * uses cube root of number of vertices to set size.  If initialYsize is less than one uses x dimension as 
     * y dimension. Will always force a lattice, periodic if requested.
     * @param periodic true if make periodic  
     **/
    public void setNetworkCubicLattice(boolean periodic)
    { 
        int xnumber =initialXsize;
        int ynumber =initialYsize;
        if (xnumber<1) xnumber = (int)(0.5+Math.pow(initialVertices,0.3333333333333));
        if (ynumber<1) ynumber=xnumber;
        if (initialXsize<0) xnumber = (int)(0.5+Math.pow(initialVertices,0.3333333333333));
        setNetworkCubicLattice(initialVertices, initialConnectivity, xnumber, ynumber,  periodic); 
    }
    

// ---------------------------------------------------------------------------------

    /** 
     * Creates a three-dimensional lattice.
     * Will builds up in rectangles of size xLength * yLength.  
     * If nVertices is not exactly divisible by (xLength * yLength) then still creates
     * a lattice (periodic if needed).
     * @param nVertices total number of vertices
     * @param m number of neighbours in ONE direction (so total is 4m)
     * @param xLength number of sites in x direction
     * @param yLength number of sites in y direction
     * @param periodic true if make periodic  
     **/
 public void setNetworkCubicLattice(int nVertices, int m, int xLength, int yLength, boolean periodic)
 {
   if (nVertices > maximumVertices) maximumVertices = nVertices;
   int x1dim = xLength;
   int x2dim = yLength;
   int x12dim= x1dim*x2dim;
   if (x12dim>nVertices)
   {
    x1dim = (int) Math.sqrt(nVertices);
    x2dim = x1dim;
    x12dim = x1dim*x2dim;
   }
   int zdim = (nVertices-1)/x12dim+1;
   int e = nVertices*m*4;
   if (maximumStubs<e) maximumStubs=e;
   setNetwork(); // set up empty graph with maximumStubs/Vertices capacity
   for (int v=0; v<nVertices; v++) addVertex(); //set up the vertices
   int v=-1;
   int vn=-1;
   int x1n=-1;
   int x2n=-1;
   int zn=-1;
   //EdgeValue ew = new EdgeValue();
   for (int z=0; z<zdim; z++)
   {
     for (int x2=0; x2<x2dim; x2++)
     {
       for (int x1=0; x1<x1dim; x1++)
       {
          v = (x1+x1dim*(x2 + z*x2dim)); //sL2V(x1,x2,z1,xdim,xdim)
          if (v>=nVertices) return; // ??? >= or just = ???
          for (int n=1; n<=m; n++)
          {
            // add neighbour in x1 direction 
            x1n = x1 + n;
            if ((x1n<x1dim) || periodic)
               {
               vn = (x1n%x1dim+x1dim*(x2 + z*x2dim));
               if ((vn<nVertices) || periodic) addEdge(v, (vn%nVertices));
               }
            // add neighbour in x2 direction 
            x2n = x2 + n;
            if ((x2n<x2dim) || periodic)
               {
               vn = (x1+x1dim*(x2n%x2dim + z*x2dim));
               if ((vn<nVertices) || periodic) addEdge(v, (vn%nVertices));
               }
              // add neighbour in x1 direction 
            zn = z + n;
            if ((zn<zdim) || periodic)
               {
               vn = (x1+x1dim*(x2 + (zn%zdim)*x2dim));
               if ((vn<nVertices) || periodic) addEdge(v, (vn%nVertices));
               }
               
          }// eo n

       } // eo x1
     } // eo x2
   } //eo z
   
 } //eo setNetworkCubicLattice

    // ---------------------------------------------------------------------------------
    /** 
     * Creates a network in shape of line or ring using global parameters.
     * <br>Uses globals initialVertices to set total number of vertices and
     * initialXsize to set number of communities.
     * Thus communities are complete graphs of size 
     * <tt>initialVertices/initialXsize</tt>.
     * Additional edges are added from one community to the next <tt>initialConnectivity</tt>
     * communities.
     * @param ring true if make periodic
     */
    public void setNetworkCaveManLine(boolean ring) 
    {
      setNetworkCaveManLine(initialVertices, initialXsize, initialConnectivity, ring);  
    }

    /** 
     * Creates a network in shape of line or ring using global parameters.
     * <br>Uses globals initialVertices to set total number of vertices and
     * initialXsize to set number of communities.
     * Thus communities are complete graphs of size 
     * <tt>initialVertices/initialXsize</tt>.
     * @param ring true if make periodic
     */
    public void setNetworkCaveManCommonVertex(boolean ring) 
    {
      setNetworkCaveManLine(initialVertices, initialXsize, initialConnectivity, ring);  
    }

    /** 
     * Creates a network in shape of line or ring.
     * Communities are complete graphs of size 
     * <tt>inVertices/nCommunities</tt>.
     * Additional edges are added from one community to the next <tt>m</tt>
     * communities.
     * @param nVertices total number of vertices
     * @param nCommunities number of communities
     * @param m number of neighbouring communites in one direction (so total is 2m)
     * @param ring true if make periodic
     */
    public void setNetworkCaveManLine(int nVertices, int nCommunities, int m, boolean ring) 
    {
      //directedGraph=false; ??? Do we need these? 
      //weightedEdges=false;
      //EdgeValue ew = new EdgeValue();
      if (nVertices>maximumVertices) maximumVertices=nVertices;
      int cSize = nVertices/nCommunities; // Basic number of vertices in a community
      nVertices = nCommunities*cSize; // Round off number of vertices, ignore surplus
      int e = (cSize*(cSize-1)+2*m)*nCommunities; // remember count as directed edges
      if (maximumStubs<e) maximumStubs=e;
      setNetwork();  // set up edges and initialise graph
      for (int v=0; v<nVertices; v++) addVertex();
      // first create communities
      int vc=0;
      for (int c=0; c<nCommunities; c++){
          vc=c*cSize;
          for (int v1=0; v1<cSize; v1++){
              for (int v2=v1+1; v2<cSize; v2++) if (v1 !=v2) addEdge(vc+v1,vc+v2);
          }
          // Now connect up the communities to m nearest neighbour communities
          int vn=-1;
          for (int cnn=1; cnn<=m; cnn++)
          {
              vn=(c+cnn)*cSize+Rnd.nextInt(cSize);
              if ((vn>=nVertices) && (!ring) ) continue; 
              vn=vn%nVertices;
              addEdge(vc+Rnd.nextInt(cSize),vn);
          }

      }
      
      
      
    }
 
    /** 
     * Creates a network of communities in shape of line or ring with common vertex.
     * <br>Each community has <tt>1+nVertices/nCommunities</tt> vertices in it with the first 
     * and last shared.
     * @param nVertices total number of vertices
     * @param nCommunities number of communities
     * @param m number of edges to add to each vertex within its community
     * @param ring true if make periodic
     */
    public void setNetworkCaveManCommonVertex(int nVertices, int nCommunities, int m, boolean ring) 
    {
      //directedGraph=false; ??? Do we need these? 
      //weightedEdges=false;
      //EdgeValue ew = new EdgeValue();
      if (nVertices>maximumVertices) maximumVertices=nVertices;
      int cSize = nVertices/nCommunities; // Basic number of vertices in a community minus one to allow for the fact that two are shared
      nVertices = nCommunities*cSize+(ring?0:1); // Round off number of vertices, ignore surplus
      int e = (cSize*(cSize+1))*nCommunities; // remember count as directed edges
      if (maximumStubs<e) maximumStubs=e;
      setNetwork();  // set up edges and initialise graph
      for (int v=0; v<nVertices; v++) addVertex();
      // first create communities
      int vc=0;
      int vs=0;
      int v2=0;
      int vnext; // link to this vertex in next community
      for (int c=0; c<nCommunities; c++){
          vc=c*cSize; // first vertex in community c
          vnext=vc+cSize+Rnd.nextInt(cSize); // join on to this vertex in next community
          if (ring) vnext=vnext%nVertices;
          else if (c>=nVertices) vnext=nVertices-1;
          for (int v1=0; v1<=cSize; v1++){
              for (int i=0; i<m; e++) 
              {
                  vs = Rnd.nextInt(cSize+1);
                  if (vs==cSize) vs=vnext;
                  v2=(vc+vs)%nVertices;
                  addEdge(vc+v1,v2);
              }
          }
          
      }
      
      
      
    }
 
    /**
     * Projects out a subgraph from set of vertices
     * <br>Old index of vertex is left as label
     * @param projStemName name to add to old outputName root for new graph
     * @param sgvertexList the set of indices of vertices to be retained
     * @param makeDirected true (false) if want (un)directed graph
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     * @return subgraph
     */
    public timgraph projectSubgraph(String projStemName, TreeSet<Integer> sgvertexList,
            boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList
          ){
        int stubs=0;
        for (Integer v : sgvertexList) stubs+= this.getVertexDegree(v);
        if (directedGraph) stubs=stubs+stubs;
        String extra=projStemName;
        if (extra.length()==0) extra="proj";
        timgraph newtg = new timgraph(outputName.getNameRoot()+extra, outputName.getDirectoryRoot(),
                infoLevel, outputControl.getNumber(), 
                makeDirected, makeLabelled, makeWeighted, makeVertexEdgeList, 
                sgvertexList.size(), stubs);
        VertexLabel vl = new VertexLabel();
        //Iterator<Integer> vi2 = sgvertexList.iterator();
        TreeMap<Integer,Integer> oldToNew = new TreeMap();
        for (Integer vold : sgvertexList){
            oldToNew.put(vold,newtg.TotalNumberVertices);
            if (infoLevel>1) System.out.println("old="+vold+"  new="+newtg.TotalNumberVertices);
            if (makeLabelled) {
                if (vertexlabels) vl = new VertexLabel(vertexLabelList[vold]);
                vl.setNumber(vold); // add old index as number
                newtg.addVertex(vl); 
            } 
            else newtg.addVertex();
        }
        
        int s=-1; // old source
        int t=-1; // old target
        int ns=-1; // new source
        int nt=-1; // new target
        for (int e=0; e<TotalNumberStubs; e++){
            s=this.stubSourceList[e];
            t=this.stubSourceList[++e];
            if (sgvertexList.contains(s) && sgvertexList.contains(t) ) {
                ns = oldToNew.get(s);
                nt = oldToNew.get(t);
                if (infoLevel>1) System.out.println("old="+s+" "+t+"   new="+ns+" "+nt);
                if (makeWeighted) {
                    if (weightedEdges) newtg.addEdge(ns,nt,edgeValuetList[e]);
                    else newtg.addEdge(ns,nt,new EdgeValue());
                }
                else newtg.addEdge(ns,nt);
            }
        }
        return newtg;
    }
    
    
// -------------------------------------------------------------    
    /** Set initial test graphs in bipartite format.     
     *@param nvtype1 number of type one vertices
     *@param nvtype2 number of type 2 vertices
     * @param name1 name of type one vertices
     * @param name2 name of type two vertices
     *@param initialisationtype 1 for random, otherwise regular, always 1 -> 2 type edges 
     */
    void setBiPartite(int nvtype1, int nvtype2, String name1, String name2, int initialisationtype)
    {
       bipartiteGraph = true; 
       numberVertexType1=nvtype1;
       numberVertexType2=nvtype2;       
       initialVertices= nvtype1+nvtype2;
       int initialedges;
       int maximumVertices = initialVertices+ numevents;
       
       vertexList = new IntArrayList[maximumVertices];
       TotalNumberVertices=0;

       stubSourceList = new int[maximumStubs];
//       if (weightedEdges) edgeValuetList = new EdgeValue[maximumStubs];
       TotalNumberStubs =0;
       for (int i=0; i<initialVertices; i++) addVertex(); 
       int vs,vt;
       for (int i=0; i<nvtype1; i++)
       {
           switch (initialisationtype)
           {
               case 1: { //random bipartite graph
                   vs = i ;
                   vt = Rnd.nextInt(nvtype2)+nvtype1;                    
               }
               
               default: { //regular bipartite graph
                   vs = i; 
                   vt = nvtype1 + i % nvtype2;                    
               }
           }
           addEdge(vs,vt);
//           if (activeVertices) activevertexList.add(vt);
           
       }
       
//       if (weightedEdges) for (int e=0; e<TotalNumberStubs; e++) edgeValuetList[e] = new EdgeValue(0,1);
//       
        if (numberVertexType1+numberVertexType2 != TotalNumberVertices) System.out.println("*** ERROR in setBiPartite: \n                 number of type1 + type 2 vertices  != TotalNumberVertices "+ initialVertices +SEP + TotalNumberVertices );
//        if (initialedges    != TotalNumberStubs   ) System.out.println("*** ERROR initialedges    != TotalNumberStubs "+ initialedges +SEP + TotalNumberStubs );
    }

  
// -------------------------------------------------------------    
    /** Set initial test graphs in bipartite format.     
     * <p>Incident graph properties (directed, weighted, labelled, vertexEdgeListOn)
     * copied from current graph.
     */
    public timgraph  makeIncidenceGraph(){
        return makeIncidenceGraph(this.directedGraph, this.vertexlabels, this.weightedEdges, this.vertexEdgeListOn);  
    }
    /** Set initial test graphs in bipartite format.     
     * @param makeDirected true (false) if want (un)directed graph
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     */
    public timgraph  makeIncidenceGraph(boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList)  
    {
       timgraph ig = new timgraph(inputName.getNameRoot()+"Inc",inputName.getDirectoryRoot(),infoLevel,outputControl.getNumber());
       ig.setGraphProperties(makeDirected, makeLabelled, makeWeighted, makeVertexEdgeList);
       int nv1 = TotalNumberVertices; // number of new vertices corresponding to old vertices
       int nv2 = TotalNumberStubs/2; //Number of new edge vertices equals number of edges but is half the number of stubs
       int nv = nv1+nv2;
       int ne = TotalNumberStubs*2;// number of new stubs is double the old number of stubs.
       if (infoLevel>0) System.out.println("#Type 1 ="+nv1+", #Type 2 ="+nv2+",  #stubs ="+ne);
       ig.setBipartite(nv1,nv2,"OldVertices","OldEdges");
       ig.setNetwork(nv, ne); 
       // first add new vertices for old vertices
       //VertexLabel vl = new VertexLabel();
       for (int v=0; v< nv1; v++) 
           if (ig.vertexlabels) {
               if (isVertexLabelled()) ig.addVertex( this.vertexLabelList[v]);
                       else ig.addVertex("v"+v, v);
           }
           else addVertex();
       // now add new vertices for old edges
       for (int e=0; e< nv2; e++) 
           if (ig.vertexlabels) ig.addVertex("e"+e*2);
           else ig.addVertex();
       
       
       if (ig.isWeighted()) {
           System.err.println("*** incidence graph not yet designed for weighted graphs, weighting switched off" );
           ig.setWeightedEdges(false);
       }
       // now add new edges for each old stub
       int s=-1;
       int t=-1;
       int ve=-1;
       for (int e=0; e< TotalNumberStubs; e++) {
           s= stubSourceList[e++];
           t= stubSourceList[e];
           ve=nv1+e/2;
           if (infoLevel>0) System.out.println("Old edge "+s+" "+t+", adding to new edge vertex "+ve);
           ig.addEdge(s,ve);
           ig.addEdge(t,ve);
       }
       
       return ig;
    }

  
   /**
     * Makes Line graph (dual projection) from current graph.
     * <br>Each edge of input graph is mapped to a vertex. These new vertices are
     * connected if the edges in the old graph have a source and target vertex in the old graphj in
     * common.  Thus one vertex in the old graph is mapped to the many edges of a clique (complete sub graph)
     * in the new graph.  The weighting of each edge is to be decided.
     * <code>vertexEdgeListOn</code> is set for this projection to work?
     * Does not automatically edge self loop for undirected graphs since then the 
     * same edge is both and incoming and outgoing stub for one vertex.
     * <br>The vertex index <tt>v</tt> of the line graph corresponds to the egd with indices 
     * <tt>(2v)</tt> and <tt>(2v+1)</tt>in the original input graph <tt>oldtg</tt> 
     * <br><tt>useSelfLoops</tt> is true if we want undirected graphs to create self-loops 
     * for all edges because and edge alpha is both in coming and outgoing to the vertex i.
     * If a weighted line graph version is being created then weights changed accordingly.
     * <br>Root of filename of input and output files has a LG tag added appropriate to type.
     * <br>NOTE ignores weights of old graph.
     * @param newtype type of line graph 0=Line Graph (C), 1= Line Graph with self-loops (Ctilde), 2=weighted Line Graph (D), 3=weighted Line Graph with self loops (Dtilde)
     * @param makeUndirected true if want to force undirected new copy
    * @deprecated use Timgraph.algorithms.LineGraphProjector
     */
    public timgraph makeLineGraph(int newtype, boolean makeUndirected) {
        if (!this.isVertexEdgeListOn()) {
            throw new RuntimeException("*** makeLineGraph needs vertexEdgeList to be on.");
        }
        
        if (this.isWeighted()) {
            throw new RuntimeException("*** makeLineGraph does not yet consider weights.");
        }
        
        int type=0;
        if ((newtype>0)&&(newtype<LineGraphProjector.lgExtensionList.length)) type = newtype;
        timgraph tg = new timgraph();
        tg.infoLevel = this.infoLevel;
        tg.setVertexEdgeList(true);
        tg.directedGraph = this.directedGraph;
        if (makeUndirected) {
            tg.directedGraph = false;
        }
        tg.weightedEdges = (type<2?false:true);
        tg.vertexlabels = false;

        tg.inputName = new FileNameSequence(this.inputName);
        tg.outputName = new FileNameSequence(this.outputName);
        tg.inputName.appendToNameRoot(LineGraphProjector.lgExtensionList[type]);
        tg.outputName.appendToNameRoot(LineGraphProjector.lgExtensionList[type]);
        int noselfloops = 1; // don't include self loops
        int countfactor = -1;
        if ((type==1) || (type==3)) {// do include self-loops
            countfactor=1;
            noselfloops=0; 
        }
        
        
        
        tg.maximumVertices = this.getNumberStubs() /2; // remember this is the stub number
        // Remember edges are really stubs
        tg.maximumStubs = 0;
        if (tg.directedGraph) {
            for (int v = 0; v < this.getNumberVertices(); v++) {
                tg.maximumStubs += this.getVertexInDegree(v) * this.getVertexOutDegree(v);
            }
            tg.maximumStubs*=2; // remember that this is really a stub number
        } else {
            for (int v = 0; v < this.getNumberVertices(); v++) {
                //if (vertexEdgeList[v].size()!=getVertexDegree(v)) System.err.println("*** vertex "+v+" degree and no. of edges incident disagree");
                int k = this.getVertexDegree(v);
                tg.maximumStubs += k * (k + countfactor); // remember this is stub number
            }
        }

        if (infoLevel>-1) System.out.println("Predicting "+tg.maximumVertices+" vertices and "+tg.maximumStubs+" edges in line graph ");
        
        tg.setNetwork();

        // add vertices
        for (int v = 0; v < tg.maximumVertices; v++) {
            tg.addVertex();        // add edges
        }
        
        // add edges
        int e1 = -1;
        int e2 = -1;
        double w = 1;
        //EdgeValue w = new EdgeValue();
        if (this.isDirected()) { // directed old graph
           int kin = -1;
           int kout = -1;
            for (int v = 0; v < this.getNumberVertices(); v++) {
                kin = this.getVertexDegree(v);
                if (kin < 1) continue;
                kout = this.getVertexDegree(v);
                if (kout < 1) continue;
                if (tg.weightedEdges) w = 1.0 / kout;
                for (int ei = 0; ei < this.vertexInEdgeList[v].size(); ei++) {
                    e1 = this.vertexInEdgeList[v].get(ei);
                    for (int eo = 0; eo < this.vertexEdgeList[v].size(); eo++) {
                      e2 = this.vertexEdgeList[v].get(eo);
                      if (tg.weightedEdges) tg.increaseEdgeWeight(e1/2, e2/2, w); // note that e and e2 are really stub indices
                      else tg.addEdgeUnique(e1/2, e2/2);
                    }// eo for eo
                } //eo for ei
            } //eo for e
        }// eo old directed case
        else { // undirected old graph
            int kv = -1;
            for (int v = 0; v < this.getNumberVertices(); v++) {
                kv = this.getVertexDegree(v);
                if (kv <= noselfloops) continue;
                if (tg.weightedEdges) w = 1.0 / (kv - noselfloops);
                //if (vertexEdgeList[v].size()!=getVertexDegree(v)) System.err.println("*** vertex "+v+" degree and no. of edges incident disagree");
                for (int ei = 0; ei < this.vertexEdgeList[v].size(); ei++) {
                    e1 = this.vertexEdgeList[v].get(ei);
                    for (int eo = ei+noselfloops; eo < this.vertexEdgeList[v].size(); eo++) {
                      e2 = this.vertexEdgeList[v].get(eo);
                      //if (tg.TotalNumberStubs>=tg.maximumStubs) System.err.println("*** vertex "+v+" edge indices "+e1+" "+e2+" to be added as edge "+tg.TotalNumberStubs);
                      if (tg.weightedEdges) tg.increaseEdgeWeight(e1/2, e2/2, w); // note that e and e2 are really stub indices
                      else tg.addEdgeUnique(e1/2, e2/2);
                    }// eo for eo
                } //eo for ei
            } //eo for e
    } // eo old undirected case
        
    return tg;    
    } // eo dual constructor

   /**
     * Makes an unweighted graph from by applying a weight cut to current graph.
     * <br>Each edge of input graph is retained only if its weight is 
     *  above a critical value specified.
     * @param minWeight minimum weight needed for an edge to be accepted
     * @param makeUndirected true if want to force undirected new copy
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     */

    /**
     * Consolidates all multiple links into one.
     * <br>If this is a directed graph and an undirected graph is being made 
     * then the new link has the sum of the weight in both directions.
     * If this starts from an unweighted graph, then edges are assumed to have weight one.
     * Makes an unweighted graph by applying a weight cut to resulting graph with no mulitple edges.
     * <br>Each edge of input graph is retained only if its weight is 
     *  above a critical value specified.
     * @param minWeight minimum weight needed for an edge to be accepted
     * @param makeUndirected true if want to force undirected new copy
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     * @deprecated Now static method in in algorithms.Projections
     */
    public timgraph makeConsolidated(double minWeight, boolean makeUndirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList)   {
        timgraph tg = new timgraph();
        tg.infoLevel = this.infoLevel;
        tg.setVertexEdgeList(makeVertexEdgeList);
        tg.directedGraph = this.directedGraph;
        if (makeUndirected) {
            tg.directedGraph = false;
        }
        tg.weightedEdges = true; // first need to sum up all weights
        tg.vertexlabels = makeLabelled;

        tg.inputName = new FileNameSequence(this.inputName);
        tg.outputName = new FileNameSequence(this.outputName);
        
        tg.maximumVertices = TotalNumberVertices;
        
        tg.maximumStubs = TotalNumberStubs;
        
        tg.setNetwork();

        // add vertices
        for (int v = 0; v < tg.maximumVertices; v++) {
           if (tg.vertexlabels) {
               if (isVertexLabelled()) tg.addVertex( this.vertexLabelList[v]);
                       else tg.addVertex("v"+v, v);
           }
           else tg.addVertex();
        }

        // find old edges, and then make a single consolidated edge in new weighted graph
        
        int s=-1;
        int t=-1;
        int ns=-1;
        int nt=-1;
        double dw=-1;
        for (int e = 0; e < TotalNumberStubs; e++) {
                if (this.weightedEdges) dw = edgeValuetList[e].weight; else dw=1;
                s=this.stubSourceList[e++];
                t=this.stubSourceList[e];
                if (makeUndirected &&  (s>t)) tg.increaseEdgeWeight(t, s, dw);
                else tg.increaseEdgeWeight(s,t,dw);
        }
        
   if (makeWeighted) return tg;
   return tg.makeUnweighted(minWeight, makeUndirected, makeLabelled, makeVertexEdgeList);    
    } // eo weight cut projection
    
   /**
     * Makes an new unweighted graph from by applying a weight cut to current graph.
     * <br>Each edge of input graph is retained only if its weight is 
     *  above a critical value specified.
     * If making an undirected from a directed graph then two undirected edges are created.
     * Use <tt>makeConsolidated</tt> if this is not the required action.
     * @param minWeight minimum weight needed for an edge to be accepted
     * @param makeUndirected true if want to force undirected new copy
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     * @return unweighted timgraph or null if a problem occurred.
     */
    public timgraph makeUnweighted(double minWeight, boolean makeUndirected, boolean makeLabelled, boolean makeVertexEdgeList)   {
        if (!this.weightedEdges) {
            System.err.println("*** timgraph makeUnweighted graph needs original to be weighted.");
            return null;
        }
        timgraph tg = new timgraph();
        tg.infoLevel = this.infoLevel;
        tg.setVertexEdgeList(makeVertexEdgeList);
        tg.directedGraph = this.directedGraph;
        if (makeUndirected) {
            tg.directedGraph = false;
        }
        tg.weightedEdges = false;
        tg.vertexlabels = makeLabelled;

        tg.inputName = new FileNameSequence(this.inputName);
        tg.outputName = new FileNameSequence(this.outputName);
        
        tg.maximumVertices = TotalNumberVertices;
        
        tg.maximumStubs = 0;
        for (int e = 0; e < TotalNumberStubs; e++) {
                if (edgeValuetList[e].weight>=minWeight) tg.maximumStubs++;
        }
        if (infoLevel>-1) System.out.println("Predicting "+tg.maximumStubs+" edges in weight cut graph");
        
        tg.setNetwork();

        // add vertices
        for (int v = 0; v < tg.maximumVertices; v++) {
           if (tg.vertexlabels) {
               if (isVertexLabelled()) tg.addVertex( this.vertexLabelList[v]);
                       else tg.addVertex("v"+v, v);
           }
           else tg.addVertex();
        }

        // add edges
        int s=-1;
        int t=-1;
        for (int e = 0; e < TotalNumberStubs; e++) {
                if (edgeValuetList[e].weight<minWeight) {e++; continue;}
                s=this.stubSourceList[e++];
                if (edgeValuetList[e].weight<minWeight) continue;
                t=this.stubSourceList[e];
//                if (tg.TotalNumberStubs>=tg.maximumStubs){
//                    System.err.println("*** total edges "+tg.TotalNumberStubs+" >= max edges "+tg.maximumStubs);
//                }
                tg.addEdgeUnweighted(s,t);
        }

    return tg;    
    } // eo weight cut projection

   
    /**
     * Removes labels from input timgraph.
     */
    public void makeUnlabelled()   {
       this.vertexlabels = false;
        this.vertexLabelList =null;
    } // eo weight cut projection
    
    /**
     * Removes the names from the vertex labels.
     */
    public void makeLabelsUnnamed()   {
        if (!this.vertexlabels) return;
        for (int v = 0; v < this.maximumVertices; v++) this.vertexLabelList[v].removeName();
    } // eo weight cut projection
    
   /**
    * Makes a ring centred on vertex of given label.
    * <br>A ring is a subgraph of all vertices within <tt>maxDistance</tt>
    * from vertex whose given label starts with <tt>vertexName</tt> (ignoring case).  
    * @param vertexName Start of label of vertex for centre of ring
    * @param maxDistance distance to go out from start vertex (inclusive).
    * @param makeUndirected true if want to force undirected new copy
    * @param makeLabelled true (false) if want (un)labelled graph
    * @param makeWeighted true (false) if want (un)weighted graph
    * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
    * @return the ring
    */
    public timgraph makeRing(String vertexName, int maxDistance, boolean makeUndirected, boolean makeWeighted, boolean makeLabelled, boolean makeVertexEdgeList){
//        if (!vertexlabels) throw new IllegalArgumentException("graph with labelled vertices needed to make a ring.");
//        int vertex;
//        for (vertex=0; vertex<getNumberVertices();vertex++) 
//            if (getVertexName(vertex).equalsIgnoreCase(vertexName)) break;
//        if (vertex==getNumberVertices()) System.err.println("*** NOT FOUND VERTEX "+vertexName);
        int vertex = getVertexFromName(vertexName);
        TreeSet<Integer> sgvertexList= getRing(vertex,maxDistance);
        // boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList,
        if (infoLevel>0) System.out.println("Component of "+inputName.getNameRoot()+" centred on vertex "+vertexName+" number "+vertex+" upto distance "+maxDistance+" found "+sgvertexList.size()+" vertices");
        timgraph newgraph = projectSubgraph("Ring",sgvertexList, makeUndirected, makeLabelled, makeWeighted, makeVertexEdgeList) ;
//        if (tg.getNumberVertices()<20) tg.printNetwork(true);
//        newgraph.inputName.setNameRoot(name+vertexName+maxDistance);
//        newgraph.outputName.setNameRoot(name+vertexName+maxDistance);        
        return newgraph;
    }

   /**
    * Makes a subgraph where all the vertices are have at least one edge from given set of labels.
    * @param vertexName Start of label of vertex for centre of ring
    * @param makeUndirected true if want to force undirected new copy
    * @param makeLabelled true (false) if want (un)labelled graph
    * @param makeWeighted true (false) if want (un)weighted graph
    * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
    * @return the subgraph of vertices connected to edges of type drawn from given input set.
    */
    public timgraph makeEdgeSubGraph(String vertexName, boolean makeUndirected, boolean makeWeighted, boolean makeLabelled, boolean makeVertexEdgeList){
        if (!vertexlabels) throw new IllegalArgumentException("graph with labelled vertices needed to make a ring.");
        
        int vertex = getVertexFromName(vertexName);
        // now find all the labels of edges incident at this vertex
        HashMap<Integer, Integer> labelSet = getEdgeLabelMap(vertex);
        int numberLabels=labelSet.size();
        
        // now find all the vertices connected to edges of these labels
        TreeSet<Integer> sgvertexList = getEdgeSubGraph(labelSet.keySet());
        if (infoLevel>0) System.out.println("Component of "+inputName.getNameRoot()+" centred on vertex "+vertexName+" number "+vertex+" had "+numberLabels+" different incident edge labels making subset of "+sgvertexList.size()+" vertices");
        
        timgraph newgraph = projectSubgraph("proj",sgvertexList, makeUndirected, makeLabelled, makeWeighted, makeVertexEdgeList) ;
        
        // now relabel the edges from 0 to n
        //newgraph.relabelEdges(labelSet, numberLabels);
        newgraph.setNameRoot(newgraph.inputName.getNameRoot()+"_"+vertexName+"ESG");
        return newgraph;
    }

    /**
     * Makes a map of all distinct edge labels incident on vertex.
     * <br>Note that the map has current labels as key and a number from 0 to map.size()-1 
     * as the value, a potential new label .
     * @param vertex number of vertex
     * @return a HashMap of old to new edge labels.
     */
    public HashMap<Integer, Integer> getEdgeLabelMap(int vertex ){
        if (!this.vertexEdgeListOn) throw new IllegalArgumentException("graph with vertex edge list needed in getEdgeLabelMap.");
        if (!this.weightedEdges) throw new IllegalArgumentException("graph with labelled edges needed in getEdgeLabelMap.");
        HashMap<Integer, Integer> labelSet = new HashMap();
        int numberLabels=0;
        for (int ei=0; ei<getVertexDegree(vertex); ei++ ){
            int e=vertexEdgeList[vertex].getQuick(ei);
            int l=edgeValuetList[e].label;
            if (!labelSet.containsKey(l)) labelSet.put(l, numberLabels++);
        }
        return labelSet;
    }
    
    /**
     * Makes set of all labels in graph.
     * <br>Note no need to check the odd edges as the EdgeValue objects
     * are identical for edges (2e) and (2e+1).
     * @return TreeSet of all edge labels
     */
    public TreeSet<Integer> getEdgeLabelSet(){
        if (!this.vertexEdgeListOn) throw new IllegalArgumentException("graph with vertex edge list needed in getEdgeLabelSet.");
        if (!this.weightedEdges) throw new IllegalArgumentException("graph with labelled edges needed in getEdgeLabelSet.");
        TreeSet<Integer> labelSet = new TreeSet();
        int numberLabels=0;
        for (int e=0; e<TotalNumberStubs; e+=2 ){
            labelSet.add(edgeValuetList[e].label);
        }
        return labelSet;
    }

//    public HashMap<Integer, Integer> getEdgeLabelMap(int vertex)
    
    /**
     * Relabels edges using a map.
     * <br>Note that must not check the odd edges as the EdgeValue objects
     * are identical for edges (2e) and (2e+1).
     * @param labelMap Map of old labels to new labels
     * @param notFoundLabel label used to indicate that this edge had a label not in given list.
     */
    public void relabelEdges(Map<Integer, Integer> labelMap, int notFoundLabel){
        for (int e=0; e<TotalNumberStubs; e+=2){
            int l= edgeValuetList[e].label;
            Integer newl = labelMap.get(l);
            if (newl==null) newl = notFoundLabel;
            edgeValuetList[e].label=newl;
        }
    }
    
    /**
     * Relabels edges around given vertex.
     * <p>If there were n distinct edge labels incident at given vertex, 
    * then edges in subgraph are relabelled from 0 to (n -1) with label n given 
    * to any edge in subgraph that did not have one of the labels.
     * @param vertexName name of vertex
    */
    public int relabelEdges(String vertexName){
        int vertex = getVertexFromName(vertexName);
        HashMap<Integer, Integer> labelSet = getEdgeLabelMap(vertex );
        relabelEdges(labelSet, labelSet.size());
        return labelSet.size();
    }
    
   /**
    * Makes a subgraph where all the vertices have a mimimum number of edges from one community.
    * <p>Uses the edge labels so graph must be weighted.
     * Sums the weight (degree if <tt>useWeights</tt> not true) 
     * of edges in each community incident at a vertex, as given by edge labels.
     * It assigns the vertex to be in the edge community with the largest weight if this eceeds 
     * the specified <tt>minimumValue</tt>, 
     * absolutely or relatively depending on the <tt>normaliseOn</tt> setting.
     * Should no community dominate at a vertex in this way, the vertex is assigned to the 
     * negative valued <tt>NOCOMMUNITYLABEL</tt> VertexCommunity class constant.
     * <p>If <tt>minimumValue</tt> is negative and <tt>normaliseOn</tt> is false then
     * it assigns the community of the vertex to be that of the largest number 
     * of edges incident at that vertex.
     * <br>If the graph is directed then out degree or strength is used to weight the vertices at a vertex.
     * @param minimumValue the minimum score for a vertex if it is to be assigned to a community.
     * @param normaliseOn true (false) if want to normalise the score by the total edge weight at a vertex. 
     * @param useWeights true (false) if want to score by weight (by degree).
     * @param makeUndirected true if want to force undirected new copy
    * @param makeLabelled true (false) if want (un)labelled graph
    * @param makeWeighted true (false) if want (un)weighted graph
    * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
    * @return the subgraph of vertices connected to edges of type drawn from given input set.
    */
    public timgraph makeEdgeLabelToVertexSubGraph(double minimumValue, boolean normaliseOn, boolean useWeights,
            boolean makeUndirected, boolean makeWeighted, boolean makeLabelled, boolean makeVertexEdgeList){
  
        if (!this.weightedEdges) throw new IllegalArgumentException("graph with weighted edges needed to make a vertex set  sub graph based on edge communities.");
        if (!this.vertexEdgeListOn) throw new IllegalArgumentException("graph with vertexEdgeList needed to make a vertex set  sub graph based on edge communities.");
        
        TreeSet<Integer> sgvertexList = edgeLabelToVertexSet(minimumValue, normaliseOn, useWeights);
        timgraph newgraph = projectSubgraph("proj",sgvertexList, makeUndirected, makeLabelled, makeWeighted, makeVertexEdgeList) ;
        
        newgraph.setNameRoot(newgraph.inputName.getNameRoot()+"_"+"EPTOVPSG");
        return newgraph;
    }
        /**
     * Returns best assignment of a vertex to an edge community.
     * <p>Uses the edge labels so graph must be weighted.
     * Sums the weight (degree if <tt>useWeights</tt> not true) 
     * of edges in each community incident at a vertex, as given by edge labels.
     * It assigns the vertex to be in the edge community with the largest weight if this eceeds 
     * the specified <tt>minimumValue</tt>, 
     * absolutely or relatively depending on the <tt>normaliseOn</tt> setting.
     * Should no community dominate at a vertex in this way, the vertex is assigned to the 
     * negative valued <tt>NOCOMMUNITYLABEL</tt> VertexCommunity class constant.
     * <p>If <tt>minimumValue</tt> is negative and <tt>normaliseOn</tt> is false then
     * it assigns the community of the vertex to be that of the largest number 
     * of edges incident at that vertex.
     * <br>If the graph is directed then out degree or strength is used to weight the vertices at a vertex.
     * @param v index of vertex
     * @param minimumValue the minimum score for a vertex if it is to be assigned to a community.
     * @param normaliseOn true (false) if want to normalise the score by the total edge weight at a vertex. 
     * @param useWeights true (false) if want to score by weight (by degree).
     */
    private int edgeToVertexLabel(int v, double minimumValue, boolean normaliseOn, boolean useWeights){
        final double MINNORM=1e-6; // smallest normlisation to accept
        int k=this.getVertexDegree(v);
        DoubleArrayList strengthInCommunity = new DoubleArrayList(k);
        TreeMap<Integer,Integer> communityToIndex= new TreeMap();
        IntArrayList indexToCommunity = new IntArrayList(k);
        IntArrayList edgeList =vertexEdgeList[v];
        double s=0;
        for (int ei=0; ei<edgeList.size(); ei++){
            int e=edgeList.getQuick(ei);
            int c= this.edgeValuetList[e].label;
            double w= (useWeights?edgeValuetList[e].weight:1);
            s+=w;
            if (communityToIndex.containsKey(c)) { //known community
                int i = communityToIndex.get(c);
                strengthInCommunity.setQuick(i,strengthInCommunity.getQuick(i)+w);
            }
            else { // new community
                communityToIndex.put(c, strengthInCommunity.size());
                indexToCommunity.add(c);
                strengthInCommunity.add(w);
            }
        }// eo for ei
        if (s<MINNORM) return Community.NOCOMMUNITYLABEL;
        int cbest=Community.NOCOMMUNITYLABEL;
        if (!normaliseOn) s=1;
        double vmaxValue = minimumValue*s;
        double kd = (double) k;
        for (int i=0; i<indexToCommunity.size(); i++){
            int c = indexToCommunity.get(i);
            double sc = strengthInCommunity.getQuick(i);
            if (vmaxValue > sc) continue;
            if ((vmaxValue == sc) && (Rnd.nextDouble()<0.5)) continue;
                vmaxValue = sc; 
                cbest = c;
        }
        // Here is a good place to set the vertex label
        //if (this.vertexlabels) this.vertexLabelList[v].
        return cbest;
    }

    /**
     * Returns subset of vertices definitely assigned to an edge community.
     * <p>Uses the edge labels so graph must be weighted.
     * Sums the weight (degree if <tt>useWeights</tt> not true) 
     * of edges in each community incident at a vertex, as given by edge labels.
     * It assigns the vertex to be in the edge community with the largest weight if this eceeds 
     * the specified <tt>minimumValue</tt>, 
     * absolutely or relatively depending on the <tt>normaliseOn</tt> setting.
     * Should no community dominate at a vertex in this way, the vertex is assigned to the 
     * negative valued <tt>NOCOMMUNITYLABEL</tt> VertexCommunity class constant.
     * <p>If <tt>minimumValue</tt> is negative and <tt>normaliseOn</tt> is false then
     * it assigns the community of the vertex to be that of the largest number 
     * of edges incident at that vertex.
     * <br>If the graph is directed then out degree or strength is used to weight the vertices at a vertex.
     * @param minimumValue the minimum score for a vertex if it is to be assigned to a community.
     * @param normaliseOn true (false) if want to normalise the score by the total edge weight at a vertex. 
     * @param useWeights true (false) if want to score by weight (by degree).
     * @return subset of vertices which meet the criteria given.
     */
    public TreeSet<Integer> edgeLabelToVertexSet(double minimumValue, boolean normaliseOn, boolean useWeights){
        TreeSet<Integer>  vset = new TreeSet();
        for (int v=0; v<TotalNumberVertices; v++){
            int c=edgeToVertexLabel(v, minimumValue, normaliseOn, useWeights);
            if (c>=0) vset.add(v);
        }
        return vset;
    } 

    
    
 // **********************************************************************
/*
 * StatQuant class.
 *
 *  Defines a statistical quantity, keeping running totals of 
 *  sums and sums of squares with moments, sigma and errors too. 
 *@author T.S.Evans
 */
    
    
    public class StatQuant    
    {
     double total;
     double squaretotal;
     int count;
     double average;
     double sigma;
     double error;
     double secondmoment;
        
        public StatQuant()
        {
          total=0;
          squaretotal=0;
          count=0;
          average=0;
          secondmoment=0;
          sigma=0;
          error=0;
        }
        
        public void add(double x)
        {
            total+= x;
            squaretotal+=x*x;
            count++;
            average=total/count;
            secondmoment=squaretotal/count;
            if (count>1) 
            { 
             sigma=(secondmoment-average*average)/Math.sqrt(count-1);
             error=sigma/Math.sqrt(count);   
            }
            else 
            {
                sigma=0;
                error=0;
            }
        }
        
        
    }   
    


    


// **********************************************************************************    
 public class DegreeDistributionOLD 
    {    
       String name="unspecified";
       IntArrayList ddarr;
       int minimum=9999999;
       int maximum=-1;
       int continuous=maximum;
       int totalvertices=0;
       int totaledges=0;
       double average=0;
       double secondmoment=0;
       
       public DegreeDistributionOLD (String inputName)
       {
           ddarr = new IntArrayList();
           name =inputName;
       }
       
       public DegreeDistributionOLD (String inputName, int initialSize)
       {
           ddarr = new IntArrayList(initialSize);
           name =inputName;
       }
       
     
    /**
     * Calculates degree distribution and other values.
     * @param vlist array of lists of neighbours
     * @param TNV total number of vertices in array
     */           
       void calcDegreeDistribution(IntArrayList [] vlist, int TNV )
    {
        maximum =-1 ; 
        ddarr.add(0); // initialise first element
        int k;
        for(int v=0; v<TNV; v++)
        {
          if (vlist[v]==null) {ddarr.set(0,ddarr.get(0)+1); break;}
          k = vlist[v].size();
//          if (k>maximum) maximum=k;
          addExtendIntArrayList(ddarr,k, 1);
        }
        calcValues();
        if (totalvertices != TNV) 
        {
            System.out.println("*** Error in calcDegreeDistribution vertex totals mismatch");      
            System.out.println("     TNV="+TotalNumberVertices+", calc = "+totalvertices);
        }
       }

    /**
     * Calculates degree distribution and other values from source and target lists.
     * @param vlist   array of lists of neighbours (targets)
     * @param vsourcelist array of lists of neighbours (sources)
     * @param TNV     total number of vertices in array
     */           
       void calcDegreeDistribution(IntArrayList [] vlist, IntArrayList [] vsourcelist, int TNV )
    {
        maximum =-1 ; 
        ddarr.add(0); // initialise first element, k=0 value.
        int k=-1;
        for(int v=0; v<TNV; v++)
        {
          k=0;  
          if (vlist[v]!=null) k = vlist[v].size(); //ddarr.set(0,ddarr.get(0)+1); break;}
          if (vsourcelist[v]!=null) k += vsourcelist[v].size();
//          if (k>maximum) maximum=k;
          addExtendIntArrayList(ddarr,k, 1);
        }
        calcValues();
        if (totalvertices != TNV) 
        {
            System.out.println("*** Error in calcDegreeDistribution vertex totals mismatch");      
            System.out.println("     TNV="+TotalNumberVertices+", calc = "+totalvertices);
        }
       }// eo calcDegreeDistribution
       
    /**
     * Calculates values except maximum from degree distribution.
     */    
       void calcValues()
       {
        totalvertices=0;
        totaledges=0;
        int nk=0;
        double nk2=0;
        continuous = maximum+1;
        minimum=0;
        while (ddarr.get(minimum)==0) minimum++;
        for (int k=minimum; k<ddarr.size(); k++) 
        {
           nk=ddarr.get(k);
           totalvertices+=nk;
           totaledges+=nk*k;
           nk2+=nk*k*k;
           if ((nk==0) && (k<continuous)) continuous = k; 
           if (nk>0) maximum=k;
        }
        average = ((double) totaledges)/totalvertices;
        secondmoment = nk2/totalvertices;
        return;
    }//eo calcvalues
       

  /**
     * Outputs the degree distribution information to a standard output.
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     */
    void printDegreeDistribution(String cc, boolean normalise)  
    {
             outputDegreeDistribution(System.out, cc,normalise);            
    }

       
       /**
     * Outputs the degree distribution information to a file.
     * @param filenamecomplete full name including directory and extension
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     */
    void FileOutputDegreeDistribution(String filenamecomplete, String cc, boolean normalise)  
    {
        PrintStream PS;        
// next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            
             outputDegreeDistribution(PS, cc,normalise);
            if (infoLevel>-2) System.out.println("Finished writing "+name+" degree distribution to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }
       
  /**
     * Outputs the degree distribution information to a print stream.
     * @param PS printstream for output such as System.out
     * @param cc comment characters put at the start of every line
     * @param normalise a boolean parameter to swicth on normalisation
     */
    void outputDegreeDistribution(PrintStream PS, String cc, boolean normalise)  
    {
// next bit of code p327 Schildt and p550
            //Date date = new Date();
            double p=0;
            int n=0;
            PS.println(cc+" timgraph"+SEP+"version "+SEP+VERSION+SEP+" date "+SEP+date);
            outputInformation(PS, cc, 2);
            if (totalvertices<1) return;
            if (normalise) PS.println(cc+" k "+SEP+"p(k)    Normalised "+name+" Degree Distribution (not reduced = strength)");
            else PS.println(cc+" k "+SEP+"n(k)     Unnormalised "+name+" Degree Distribution  (not reduced = strength)");
            for (int k=0; k<ddarr.size(); k++)
            {
              if (normalise)  
              {
                  p = ddarr.get(k)/((double) totalvertices);
                  if (p>0) PS.println(k+SEP+p);
              }
              else 
              {
                  n = ((int) (ddarr.get(k)+0.5) );
                  if (n>0) PS.println(k+SEP+n);
              }
            }
            return;
        }// eo outputDegreeDistribution
    
       
       
       /**
        * Output information about degree distribution.
        *@param PS print stream such as System.out
        *@param cc comment string
        * @param dec number of decimal points to retain     
        */
       public void outputInformation(PrintStream PS, String cc, int dec)
       {
           PS.println(cc+"Degree"+SEP+"distribution"+SEP+name);
           PS.println(cc+SEP+"Total Vertices"+SEP+totalvertices+SEP+"Total Edges"+SEP+totaledges);
           PS.println(cc+SEP+"k_min"+SEP+"k_cont"+SEP+"k_max"+SEP+"<k>"+SEP+"<k^2>");
           PS.println(cc+SEP+ minimum+SEP+continuous+SEP+maximum+SEP+NumbersToString.toString(average,dec)+SEP+NumbersToString.toString(secondmoment,dec) );
       }
     
 }
    
    
// **********************************************************************
 
 // ------------------------------------------------------------------------
    /**
     * Generates integer random numbers binomially distributed.
     * @param average is mean value
     * @param N is number of 'dice' used
     *@return integer between 0 and (int) average*2 inclusive
     */
    public int getRandomBinomial(double average, int N)
    {
        if (N<1) return (int)(average+1e-6);
        double total=0;
        for (int n=0;n<N;n++) total+=Rnd.nextDouble();
        return ( (int) (total*average/((double) N) ) );
    }

    // ------------------------------------------------------------------------
    /**
     * Generates integer random numbers Markov distributed.
     * @param prob is the probability you continue.
     * @param N is maximum number of steps to make 
     *@return integer between 0 and N inclusive
     */
    public int getRandomMarkov(double prob, int N)
    {
        if (N<0) return (-N);
        int n=0;
        for (n=0;n<N;n++) if (Rnd.nextDouble()>prob) break;
        return n;
    }




// ---------------------------------------------------------------
   /** Method of timgraph
     * Returns double of ellapsed time in seconds
     * @param initialtime is time in milliseconds
     */    
  public double calcEllapsedTime(long initialtime)
  {
       return ( (double) ( 1000.0*(System.currentTimeMillis () - initialtime)));
  }    

  // **************************************************************************
    /**
     * Rounds integer down to nearest even integer.
     * This does not depend on size of integers.
     * @param value to be truncated      
     */
    public int makeEven(int value)
    {
      return ( value - (value | 1) );
    }
    
  
    

    
    
    



    
      
} //eo timgraph Class
