/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.algorithms;

import TimGraph.timgraph;
import TimUtilities.FileUtilities.FileNameSequence;
import TimUtilities.TimCounting;
import TimUtilities.TimMemory;
import TimUtilities.TimTiming;

/**
 * This creates a line graph from a given graph.
 * <p>Works with any given type of input graph.
 * @author time
 */
public class LineGraphProjector {
    
//    public final static String [] lgExtensionList  = {"LG", "LGsl", "WLG", "WLGsl"};
//    public final static String [] lgExtensionDescription  = {"Line Graph L(G)", "Line Graph with self-loops", "Weighted Line Graph", "Weighted Line Graph with self-loops"};
    public final static String [] lgExtensionList  = {"LG", "LGsl", "DWLG", "DWLGsl", "SWLG", "SWLGsl"};
    public final static String [] lgExtensionDescription  = {"Line Graph L(G)=C(G)",
    "Line Graph with self-loops, Ctilde",
    "Degree Weighted Line Graph, D(G)",
    "Degree Weighted Line Graph with self-loops, Dtilde(G)",
    "Strength Weighted Line Graph, E(G)",
    "Strength Weighted Line Graph with self-loops, Etilde(G)"};
    

    

    /**
     * Makes Line graph (dual projection) from current graph.
     * <br>Each edge of input graph is mapped to a vertex. These new vertices are
     * connected if the edges in the old graph have a source and target 
     * vertex in the old graphj in common.  Thus one vertex in the old graph
     * is mapped to the many edges of a clique (complete sub graph)
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
     * <br>Does seem to take account of weights of old graph.
     * <p>The different types are defined in the weighted graph paper
     * <em>Edge Partitions and Overlapping Communities in Complex Networks</em>,
     * T.S.Evans and R.Lambiotte Eur.Phys.J.B <b>77</b> (2010) 265-272 
     * [<tt>arXiv:0912.4389</tt>].  They are set by the
     * <tt>type</tt> parameters and defined by constant strings in this class,
     * {@link #lgExtensionList} for file name additions and {@link #lgExtensionDescription}.
     * These are as follows:
     * <ul>
     * <li>0 = Line Graph L(G)=C(G)</li>
     * <li>1 = Line Graph with self-loops, Ctilde</li>
     * <li>2 = Degree Weighted Line Graph, D(G)</li>
     * <li>3 = Degree Weighted Line Graph with self-loops, Dtilde(G) (E(G) in unweighted paper)</li>
     * <li>4 = Strength Weighted Line Graph, E(G) </li>
     * <li>5 = Strength Weighted Line Graph with self-loops, Etilde(G)</li>
     * </ul>
     * <p>Checked  on unweighted undirected Bowtie 03/06/2010
     * <p>Checked  on weighted undirected BowtieW 02/06/2010
     * @param tg input graph whose line graph is required
     * @param newtype type of line graph 0=Line Graph (C), 1= Line Graph with self-loops (Ctilde), 2=weighted Line Graph (D), 3=weighted Line Graph with self loops (Dtilde or E)
     * @param infoOn true if want information displayed
     */
    public static timgraph makeLineGraph(timgraph tg, int newtype, boolean infoOn) {

//        boolean emergency=true;
//        if (emergency) throw new RuntimeException("*** LineGraphProjector surely using local edge label to get edge weights when should have global edge index?");
//
        if (!tg.isVertexEdgeListOn()) {
            System.err.println("!!! WARNING LineGraphProjector is creating the vertexEdgeList it needs.");
            tg.createVertexGlobalEdgeList();
        }
        
//        if (tg.isWeighted()  && tg.isDirected()) {
//            throw new RuntimeException("*** LineGraphProjector does not yet consider weighted directed graphs.");
//        }
        final double MINNORMALISATION = 1e-20; // very small positive number 
        
        int type=2;
        if ((newtype>=0)&&(newtype<lgExtensionList.length)) {
            type = newtype;
            //if ((type>3) && !tg.isWeighted()) throw new RuntimeException("*** LineGraphProjector needs type to be between 0 and 3 when input graph is specified as unweighted");
        }
        else  throw new RuntimeException("*** LineGraphProjector needs type to be between 0 and "+(lgExtensionList.length-1)+", was given "+type);

        TimTiming timing = new TimTiming();
        TimMemory memory = new TimMemory();
        System.out.println("--- makeLineGraph Initial Memory: "+memory.StringAllValues());
     

        timgraph lg = new timgraph();
        lg.infoLevel = tg.infoLevel;
        lg.setVertexEdgeList(true);
        
        lg.setDirectedGraph( tg.isDirected() || tg.isWeighted() );
        lg.setWeightedEdges( tg.isWeighted() || (type>1) );
        lg.setVertexlabels(false);
        
        lg.inputName = new FileNameSequence(tg.inputName);
        lg.outputName = new FileNameSequence(tg.outputName);
        lg.inputName.appendToNameRoot("_"+lgExtensionList[type]);
        lg.outputName.appendToNameRoot("_"+lgExtensionList[type]);
        lg.setInitialGraphExternalAlgorithm();
        int noselfloops = 1; // don't include self loops
        boolean includeSelfLoops = false;
        int countfactor = -1;
        if ((type==1) || (type==3) || (type==5) ) {// do include self-loops
            countfactor=1;
            noselfloops=0; 
            includeSelfLoops = true;
        }

        // used to distinguish enforce unit normalisation
        boolean unitNormalisation = true;
        if (type>1) unitNormalisation = false;

        // used to distinguish the degree and strength normalisations if unitNormalisation is false
        boolean strengthNormalisation = false;
        if ((type>3) && tg.isWeighted()) strengthNormalisation = true;

        // Add LG vertices i.e. TG edges
        lg.setMaximumVertices(tg.getNumberStubs() /2); // remember this is the stub number
        // Remember edges are really stubs
        int maximumStubs = 0;
        if (tg.isDirected()) {
            for (int v = 0; v < tg.getNumberVertices(); v++) {
                maximumStubs += tg.getVertexInDegree(v) * tg.getVertexOutDegree(v);
            }
            maximumStubs*=2; // remember that this is really a stub number
        } else {
            for (int v = 0; v < tg.getNumberVertices(); v++) {
                //if (vertexEdgeList[v].size()!=getVertexDegree(v)) System.err.println("*** vertex "+v+" degree and no. of edges incident disagree");
                int k = tg.getVertexDegree(v);
                maximumStubs += k * (k + countfactor); // remember this is stub number
                }
            if (lg.isDirected()) maximumStubs += maximumStubs; // double for tg weighted undirected case            
        }
        lg.setMaximumStubs(maximumStubs);

        if (infoOn) {
            System.out.println("Predicting "+lg.getMaximumVertices()+" vertices and "+lg.getMaximumStubs()+" stubs in line graph ");
            System.out.println("Line graph will be "+lg.graphTypeString(" "));
        }
        System.out.println("--- makeLineGraph\n    Time: "+timing.elapsedTimeString()+". Memory: "+memory.StringAllValues());
     
        lg.setNetwork();

        // add vertices
        for (int v = 0; v < lg.getMaximumVertices(); v++) {
            lg.addVertex();        // add edges
        }
        System.out.println("--- makeLineGraph, vertices added.\n    Time: "+timing.elapsedTimeString()+". Memory: "+memory.StringAllValues());
     
        TimCounting counting = new TimCounting(tg.getNumberVertices(), true);
        
        // add LG edges
        int e1 = -1;
        int e2 = -1;
        double dnv = tg.getNumberVertices();
        //EdgeWeight w = new EdgeWeight();
        
        // If started with directed graph then LG is always directed.
        // This includes the type 2 C LG case which always gives directed graphs
        if (tg.isDirected() ) { // directed old graph
           int kin = -1;
           int kout = -1;
           double w = 1;
           double norm=-1;
           for (int v = 0; v < tg.getNumberVertices(); v++) {
                if (infoOn && counting.increment()){
                        System.out.println(" : "+timing.elapsedTimeString()+": "+timing.estimateRemainingTimeString(v/dnv));
                        System.out.println("           : "+memory.StringAllValues());
                }
                kin = tg.getVertexDegree(v);
                if (kin < 1) continue;
                kout = tg.getVertexDegree(v);
                if (kout < 1) continue;
                if (lg.isWeighted()) {
                     norm=(unitNormalisation?1:(strengthNormalisation?tg.getVertexOutStrength(v):kout));
                     if (norm<MINNORMALISATION) continue;
                }
     
                for (int ei = 0; ei < tg.getVertexInDegree(v); ei++) {
                    e1 = tg.getStubIn(v, ei);
                    // no backtrack on directed input tg is possible
                    // so no need for special exclusion for weighted LG D (type 2) case
                    for (int eo = 0; eo < tg.getVertexOutDegree(v); eo++) {
                      e2 = tg.getStub(v,eo);
                      // *** SHOULD THIS BE e2 in next line ???
                      if (lg.isWeighted()) lg.increaseEdgeWeight(e1/2, e2/2, tg.getEdgeWeight(e2)/norm); // note that e and e2 are really stub indices
                      else lg.addEdgeUnique(e1/2, e2/2);
                    }// eo for eo
                } //eo for ei
            } //eo for e
            if (infoOn && counting.increment()){
                        System.out.println("\nFinished makeLineGraph.\n    Time: "+timing.elapsedTimeString()+". Memory: "+memory.StringAllValues());
            }
            return lg;
        }// eo old directed  case
        
        // Now case where tg is undirected and lg is undirected
        // This excludes the weighted input tg to type 2 C LG case 
        // which always gives Directed graphs
        if (!tg.isDirected() && !lg.isDirected()) { 
            if (tg.isWeighted()) throw new RuntimeException(" Input graph must be unweighted in this case");
            int kv = -1;
            double w=1; // this is the weight of an lg edge
            double norm=1; // normalisation
            double s=-1;
            boolean lgweighted = lg.isWeighted();
            for (int v = 0; v < tg.getNumberVertices(); v++) {
                if (infoOn && counting.increment()){
                        System.out.println(" : "+timing.elapsedTimeString()+": "+timing.estimateRemainingTimeString(v/dnv));
                        System.out.println("           : "+memory.StringAllValues());
                }
                kv = tg.getVertexDegree(v);
                if (kv <= noselfloops) continue;
                norm=1;
                if (!unitNormalisation)
                {
                    s=(strengthNormalisation?tg.getVertexOutStrength(v):kv);
                    if (s<MINNORMALISATION) continue;
                    norm=s;
                }
                for (int ei = 0; ei < tg.getVertexDegree(v); ei++) {
                    e1 = tg.getStub(v, ei);
                      // *** SHOULD THIS BE e1 in next line ???
                    if(!unitNormalisation && !includeSelfLoops) norm = (s-(strengthNormalisation?tg.getEdgeWeight(e1):1));
                    if (norm<MINNORMALISATION) continue;
                    for (int eo = ei+noselfloops; eo < tg.getVertexDegree(v); eo++) {
                      e2 = tg.getStub(v, eo);
                      if (lgweighted) {
                      // *** SHOULD THIS BE e2 in next line ???
                          lg.increaseEdgeWeight(e1/2, e2/2, tg.getEdgeWeight(e2)/norm); // note that e and e2 are really stub indices
                      }
                      else lg.addEdgeUnique(e1/2, e2/2);
                    }// eo for eo
                } //eo for ei
            } //eo for e
            if (infoOn && counting.increment()){
                        System.out.println("\nFinished makeLineGraph.\n    Time: "+timing.elapsedTimeString()+". Memory: "+memory.StringAllValues());
            }
            return lg;
    } // eo old undirected unweighted case
        
        //This case should now be just the undirected weighted input tg
        // producing a directed weighted  LG in all cases
        if (lg.isDirected()  && !tg.isDirected()) { 
            if (!lg.isWeighted()) throw new RuntimeException(" LG must be weighted in this case");
            if (!tg.isWeighted()) throw new RuntimeException(" Input graph must be weighted in this case");
            //if (includeSelfLoops) throw new RuntimeException(" LG must exclude self loops in this case");
            int kv = -1;
            double s=-1;
            double norm=-1;
            for (int v = 0; v < tg.getNumberVertices(); v++) {
                if (infoOn && counting.increment()){
                        System.out.println(" : "+timing.elapsedTimeString()+": "+timing.estimateRemainingTimeString(v/dnv));
                        System.out.println("           : "+memory.StringAllValues());
                }
                kv = tg.getVertexDegree(v);
                if (kv <= noselfloops) continue;
                //if (lg.isWeighted()) w = 1.0 / (kv - noselfloops);
                //if (vertexEdgeList[v].size()!=getVertexDegree(v)) System.err.println("*** vertex "+v+" degree and no. of edges incident disagree");

                s=(unitNormalisation?1:(strengthNormalisation?tg.getVertexOutStrength(v):tg.getVertexOutDegree(v)) );
                //s=tg.getVertexOutStrength(v); // this is degree is input tg is unweighted
                if (s<MINNORMALISATION) continue;
                norm=s;
                for (int ei = 0; ei < tg.getVertexDegree(v); ei++) {
                    e1 = tg.getStub(v, ei);
                    //if(!includeSelfLoops) norm = (s-tg.getEdgeWeight(e1));
                    if(!unitNormalisation && !includeSelfLoops) norm = (s-(strengthNormalisation?tg.getEdgeWeight(e1):1));
                    if (norm<MINNORMALISATION) continue;
                    for (int eo = 0; eo < tg.getVertexDegree(v); eo++) {
                      if ((ei==eo) && (!includeSelfLoops)) continue;
                      e2 = tg.getStub(v, eo);
                      lg.increaseEdgeWeight(e1/2, e2/2, tg.getEdgeWeight(e2)/norm); // note that e and e2 are really stub indices
                    }// eo for eo
                } //eo for ei
            } //eo for e
            if (infoOn && counting.increment()){
                        System.out.println("\nFinished makeLineGraph.\n    Time: "+timing.elapsedTimeString()+". Memory: "+memory.StringAllValues());
            }
            return lg;
    } // eo old undirected unweighted case
        
    return null;    
    } // eo dual constructor



}
