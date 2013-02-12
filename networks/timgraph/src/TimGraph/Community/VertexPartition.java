/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import DataAnalysis.IntegerSequence;
import TimGraph.VertexLabel;
import TimGraph.algorithms.Projections;
import TimGraph.timgraph;

import java.util.TreeSet;
import java.util.Iterator;


import java.io.PrintStream;
import java.util.Random; //p524 Schildt

import TimUtilities.Permutation;
import TimUtilities.TimMemory;
import TimUtilities.TimTiming;
import TimUtilities.UpdateRecord;

/**
 * Vertex partition of a graph.
 * <br>This can handle all types if graph.  By default the modularity used
 * in the calculation routines is chosen with a null model given
 * by the degree vector, if undirected, or the pi vector (page rank vector)
 * if directed.
 * <br>Contains greedy improvement algorithm.
 * <br>The vertices can be those of a line graph and thus this can be used
 * to make an edge partition.
 * @author time
 */
public class VertexPartition extends Partition {

    private Random Rnd = new Random();
    
    /**
     * Number of Edges
     * <br>Is this really needed?
     */
    protected int numberEdges=UNSET;


    /**
     * Methods available
     */
    public final static String [] METHODS = {"Louvain Vertex Partition", "Simulated Annealing Partition"};
    
    /**
     * Number of edges in this vertex partition
     */
    int edgesInVP=-1; 
       
    /**
     * Gives permutation of vertices.  Used for greedy algorithm.
     */
    private Permutation perm;
    
    /**
     * Used for greedy algorithm to follow update success.
     */
    protected UpdateRecord greedyUpdateRecord;



        int[] degreeArray;// = null;
        int[] degreeInCArray;// = null;
        double[] strengthArray;// = null;
        double[] strengthInCArray;// = null;
        double[] qualityArray;// = null;

    
 /**
 * Empty Constructor needed for extensions of VertexPartition.
 */
public VertexPartition(){setDefaultNames();
    }

 /**
 * Constructor used to set basic parameters from given timgraph.
  * @param tg input graph
 */
public VertexPartition(timgraph tg){name="generalVP"; initialise(tg);};

/**
 * Constructor used to prepare a community.
 * @param nv number of vertices
 */
public VertexPartition(int nv){
    name="generalVP";
    setNumberElements(nv);
    }
/**
 * Constructor used to prepare a community.
 * @param newname used to set name of partition
 * @param nv number of vertices
 */
public VertexPartition(String newname, int nv){
    setDefaultNames();
    setNumberElements(nv);
    }
/**
 * Constructor used to store basic information about a community.
 * <br>This does not necessarily provide any ability to recalculate.  
 * It merely stores basic information about a community structure e.g. the best one.
 * @param q quality
 * @param nv number of vertices
 * @param ne number of edges
 * @param cov vector with community labels, i.e. <code>cov[s]</code> is community of vertex s.
 */
public VertexPartition(double q, int nv, int ne, int [] cov){
    setDefaultNames();
    name=name+quality.QdefinitionShortString();
    Q=q;
    setNumberElements(nv);
    numberEdges=ne;
    setCommunity(cov);
    }

 /**
 * Constructor used to project line graph vertex partition (onto vertex partition.
  * @param tg input graph
  * @param lgvp line graph vertex partition (i.e. an edge partition) where each edge <tt>e</tt> (global edge index)  in the given graph is the vertex <tt>(e/2)</tt> in the given partition.
 */
public VertexPartition(timgraph tg, VertexPartition lgvp){
    setDefaultNames();
    name=lgvp.getName()+"ep2vp";
    initialiseFromLineGraphVertexPartition(tg,lgvp);
}

/**
 * Sets up default names for this partition
 */
private void setDefaultNames(){
    name="generalVP";
    nameOfElements="vertices";
}

    /**
     * Initialise general VertexPartition class entities.
     * <br>Uses simple Quality (Newman basic modularity)
     *@param tg graph to be analysed as a timgraph
     */
    public void initialise(timgraph tg){
     int nullModel=(tg.isDirected()?4:0);
     initialise(tg,0,nullModel,1,true);
   }
    
    
    /**
     * Initialise general VertexPartition class entities.
     * <br>Uses simple Quality (Newman basic modularity)
     * @param tg graph to be analysed as a timgraph
     * @param qualityDefinition flag to choose formula for modularity.
     * @param nullModel index of null model to use
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param individualCommunities true to set all vertices to their own community (community label = vertex label).  
     * Otherwise all are put into community 0.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public void initialise(timgraph tg, int qualityDefinition, int nullModel, 
            int qualityType, boolean individualCommunities){
        initialise(tg, qualityDefinition, nullModel, qualityType, 1.0, (individualCommunities?0:-1));
    }
    
    /**
     * Initialise general VertexPartition class entities.
     * <br>Uses simple Quality (Newman basic modularity)
     * @param tg graph to be analysed as a timgraph
     * @param qualityDefinition flag to choose formula for modularity.
     * @param nullModel index of null model to use
     * @param qualityType   type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param maxNumberCommunities true if one to set all vertices to their own community (label-vertex label).  Otherwise all are putinto community 0.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public void initialise(timgraph tg, int qualityDefinition, int nullModel, 
            int qualityType, int maxNumberCommunities){
        initialise(tg, qualityDefinition, nullModel, qualityType, 1.0, maxNumberCommunities);
    }
    
    /**
     * Initialise general VertexPartition class entities.
     * <br>Uses simple Quality (Newman basic modularity).
     * Puts vertices into a single, all distinct or randomly chosen communities.
     * @param tg graph to be analysed as a timgraph
     * @param qualityDefinition flag to choose formula for modularity.
     * @param nullModel index of null model to use
     * @param qualityType   type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param lambda scaling factor for null model in quality function
     * @param maxNumberCommunities Maximum number of communities to use, each chosen at random. 
     * If this is negative then each vertex is placed it its own unique community.
     * If this is zero then all vertices are placed into a single community zero.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public void initialise(timgraph tg, int qualityDefinition, int nullModel, 
            int qualityType, double lambda, int maxNumberCommunities){
        initialiseGraph(tg, qualityDefinition, nullModel, qualityType,lambda);
        numberElements = graph.getNumberVertices();
        numberEdges = graph.getNumberStubs();
        //quality = new QualitySparse(tg,qualityDefinition,lambda);
        if (infoLevel>1 && numberElements<21) quality.printMatrix(System.out, " ", true);
        setCommunity(maxNumberCommunities);
        Q = UNSET; // indicate that Q has not been calculated by using number less than -1
   }
    /**
     * Constructs a vertex partition from a line graph vertex partition.
     *<p>Assigns the community of the vertex to be that of the largest number 
     * of edges incident at that vertex.
     * <br>If the graph is directed then out degree or strength is used to weight the vertices at a vertex.
     * @param tg timgraph whose vertex partition is given
     * @param lgvp line graph vertex partition so an edge partition of the graph.
     */
    private void initialiseFromLineGraphVertexPartition(timgraph tg, VertexPartition lgvp) {
        initialiseFromLineGraphVertexPartition (tg, lgvp, DUNSET, false);
    }
    /**
     * Constructs a vertex partition from a line graph vertex partition.
     * <p>Sums the weight (degree if unweighted) of edges in each community incident at a vertex.
     * It assigns the vertex to be in the edge community with the largest weight if this eceeds 
     * the specified <tt>minimumValue</tt>, 
     * absolutely or relatively depending on the <tt>normaliseOn</tt> setting.
     * Should no community dominate at a vertex in this way, the vertex is assigned to the 
     * negative valued <tt>NOCOMMUNITYLABEL</tt> VertexCommunity class constant.
     * <p>If <tt>minimumValue</tt> is negative and <tt>normaliseOn</tt> is false then
     * it assigns the community of the vertex to be that of the largest number 
     * of edges incident at that vertex.
     * <br>If the graph is directed then out degree or strength is used to weight the vertices at a vertex.
     * @param tg timgraph whose vertex partition is given
     * @param lgvp line graph vertex partition so an edge partition of the graph.
     * @param minimumValue the minimum score for a vertex if it is to be assigned to a community.
     * @param normaliseOn true (false) if want to normalise the score by the total edge weight at a vertex. 
     */
    private void initialiseFromLineGraphVertexPartition (timgraph tg, VertexPartition lgvp, double minimumValue, boolean normaliseOn){
        final double MINNORM=1e-6; // smallest normlisation to accept
        initialise(tg,0,(tg.isDirected()?4:0),2,false);
        int nc = lgvp.getNumberOfCommunities();
        double [] score = new double[nc];
        for (int v=0; v<numberElements; v++){
            for (int c=0; c<nc; c++) score[c]=0;
            double norm=0;
            for (int ei=0; ei<tg.getVertexOutDegree(v); ei++) {
                int e = tg.getStub(v, ei);
                int ce = lgvp.getCommunity(e/2);
                double value = (tg.isWeighted()?tg.getEdgeWeight(e):1.0);
                norm+=value;
                score[ce]+=value;
            }
            double maxValue=minimumValue;
            int cbest=NOCOMMUNITYLABEL;
            if (norm<MINNORM) {communityOfElement[v]=NOCOMMUNITYLABEL; continue;}
            if (!normaliseOn) norm=1;
            double vmaxValue = maxValue*norm;
            for (int c=0; c<nc; c++) {
                if (vmaxValue > score[c]) continue;
                if ((vmaxValue == score[c]) && (Rnd.nextDouble()<0.5)) continue;
                vmaxValue = score[c]; 
                cbest = c;
            } 
            communityOfElement[v]=cbest;
        }
        recalculateCommunityLabels();
    }

   public int getNumberVertices(){return numberElements;};
   public int getNumberVerticesInCommunityFast(int c){return getNumberElementsInCommunity(c);};
   public int getNumberVerticesInCommunity(int c){
        if (numberElementsArray==null) analyse();
        return numberElementsArray[c];
    }
public int getDegreeOfCommunity(int c){return degreeArray[c];};
   public int getDegreeInCommunity (int c){return degreeInCArray[c];};
   public double getStrengthOfCommunity (int c){return strengthArray[c];};
   public double getStrengthInCommunity (int c){return strengthInCArray[c];};
   public double getQualityOfCommunity (int c){return qualityArray[c];};

   
  
   
    /**
     * Makes a projected graph using the vertex partition.
     * @return graph projected onto the vertex partition
     */
    public timgraph getVertexPartitionGraph(){
        return Projections.ontoVertexPartition(graph.inputName.getNameRoot()+"proj"+name,graph,communityOfElement, numberCommunities, false, false);
    }
    
    
    /**
     * Calculate number of edges in this vertex partition and how many of these are in given edge partition.
     * <br>Number of edges in this community stored globally and accessed via routine.
     * @param ep edge partition to be compared to this vertex one.
     * @return number of edges contained in these vertex communities which are also in the given edge partition.
     */
   public int compareEPtoVP(EdgePartition ep){
       edgesInVP=0; 
       int edgesInVPEP=0;
       int s=-1;
       int t=-1;
       int cs=-1;
       int ct=-1;
       for (int e=0; e<graph.getNumberStubs(); e++){
       s=graph.getVertexFromStub(e++);
       t=graph.getVertexFromStub(e);
       cs=this.communityOfElement[s];
       ct=this.communityOfElement[t];
       if (ct==cs) {
                    edgesInVP++;
                    edgesInVPEP++;
                }                
            
        }
       return edgesInVPEP;
    }

   /**
    * Number of edges in this vertex partition.
    * @return number of edges in this vertex partition
    */
   public int getNumberOfEdgesInVP(){return this.edgesInVP;}
    
    
    /**
     * Calculate best greedy community.
     * <br>Takes current community and makes several greedy sweeps trying 
     * to maximise quality of community partition.
     * Stops when no more changes made or when maxSweeps done.
     * <p>Gives 5 or more minutes between information updates.
     * @param maxSweeps maximum number of greedy seeps to make
     */
    public int calculateBestGreedyCommunity(int maxSweeps){
        return calculateBestGreedyCommunity(maxSweeps, 5.0);
    }
    /**
     * Calculate best greedy community.
     * <br>Takes current community and makes several greedy sweeps trying 
     * to maximise quality of community partition.
     * Stops when no more changes made or when maxSweeps done.
     * @param maxSweeps maximum number of greedy seeps to make
     * @param intervalTimeToPrintMinutes interval in minutes between information updates, 0 or less and no info is given
     */
    public int calculateBestGreedyCommunity(int maxSweeps, double intervalTimeToPrintMinutes){
        perm = new Permutation(numberElements);
        greedyUpdateRecord = new UpdateRecord();
        final boolean printInfo=((infoLevel>0) && (intervalTimeToPrintMinutes>0?true:false));
        TimTiming tt = new TimTiming(); 
        TimMemory memory = new TimMemory();
        if (printInfo) {
            System.out.println("\n\nInitial Quality "+calcQuality()+", number of communities "+getNumberOfCommunities());
            System.out.println("                    initial memory "+memory.StringAllValues());
        }
        tt.setIntervalTimeMinutes(intervalTimeToPrintMinutes); 
        for (int n=0; n<maxSweeps; n++)
        {
            double totalQualityChange = oneGreedySweep(intervalTimeToPrintMinutes);
            if (tt.testIntervalTime()){
                recalculateCommunityLabels();
                System.out.println("\nGreedy Sweep "+n+", quality "+calcQuality()+", number of communities "+getNumberOfCommunities()+", Quality change "+totalQualityChange+", "+greedyUpdateRecord.toString()+".");
                System.out.println("                    time ellapsed "+tt.runTimeString()+", memory "+memory.StringAllValues());
            }
            if (greedyUpdateRecord.getMade()==0) break;
        }       
        if (printInfo) {
            System.out.println("Final Quality "+calcQuality()+", number of communities "+getNumberOfCommunities());
            System.out.println("                    initial memory "+memory.StringAllValues()+"\n\n");
        }
        return greedyUpdateRecord.getMade();
    }
  
   
    /**
     * One greedy sweep of all sites.
     * <br>Takes sites in a random order. 
     * For each site tries the communities of all neighbouring sites
     * and possibility of being a new solo community.
     * Joins site to community with largest quality gain, or leaves it be if thats is best.
     * @param intervalTimeToPrintMinutes interval in minutes between information updates, 0 or less and no info is given
     * @return change in quality
     */
    private double oneGreedySweep(double intervalTimeToPrintMinutes){
        TimTiming tt = new TimTiming(); 
        TimMemory memory = new TimMemory();
        tt.setIntervalTimeMinutes(intervalTimeToPrintMinutes); 
        double totalQualityChange=0;
        int updateTried=0;
        int updateMade=0;
        double deltaQremove = 0;
        int oldCommunity=-1;
        perm.newPermutation();
        int s=-1;
        double deltaQ = 0;
        double deltaQmax = 0;
        int cmax=-1;  // non existent community
        double dnv = (double) graph.getNumberVertices();
        for (int i=0; i<graph.getNumberVertices();i++) {
            if (tt.testIntervalTime()){
                recalculateCommunityLabels();
                double f= i/dnv;
                System.out.println("Completed "+Math.round(f*100)+"%, time ellapsed/left "+tt.runTimeString()+"  "+tt.estimateRemainingTimeString(f));
                System.out.println("                  memory "+memory.StringAllValues());
            }
            s=perm.next();
            oldCommunity = communityOfElement[s];
            deltaQremove = -quality.delta(s, oldCommunity, communityOfElement);

            if (deltaQremove>0) { // better to put in own single vertex community
                cmax=VertexPartition.NEWCOMMUNITYLABEL; 
                //deltaQ=deltaQremove;
                deltaQmax=deltaQremove;
            }
            else { // better not to put in own single vertex community
                //deltaQ = 0;
                deltaQmax=0;
                cmax = oldCommunity;
            }
            
            // find out all the distinct neighbouring community labels
            int kout = graph.getVertexOutDegree(s);
            if (kout ==0) continue;
            TreeSet<Integer> nc = new TreeSet<Integer> ();
            for (int e=0; e<kout;e++) {
                int nn = graph.getVertexTarget(s, e);
                int cnn = communityOfElement[nn];
                nc.add(cnn);
            }
                //nc.add(communityOfElement[graph.getVertexTarget(s, e)]);

            // Now find out if its better to join a distinct community of one of the neighbours
            Iterator<Integer> citer = nc.iterator();
            int c=-1;
            while(citer.hasNext()){
                c=citer.next();
                if (c==oldCommunity) continue; 
                deltaQ = quality.delta(s, c, communityOfElement)+deltaQremove;
                if (deltaQ < deltaQmax) continue;
                cmax=c;
                deltaQmax = deltaQ;
            }
            updateTried++;
        
            if (cmax!=oldCommunity) { // can increase quality by changing communities
                totalQualityChange+=deltaQmax;
                if (cmax==VertexPartition.NEWCOMMUNITYLABEL) cmax=getEmptyCommunity();
                communityOfElement[s]=cmax;
                updateMade++;
            }
        }
        greedyUpdateRecord.update(updateTried, updateMade);
        return totalQualityChange;
    }
 
    
    /** Prints vertices of the Vertex Partition with in community statistics.
     * @param PS a print stream for the output such as System.out
     * @param cc comment string used at start of each header or info line
     * @param sep string separating items
     * @param infoOn true (false) if want first row to have basic infomration on partition
     * @param headerOn true (false) if want header row on (off)
         */     
    public void printVertices(PrintStream PS, String cc, String sep, boolean infoOn, boolean headerOn){
        if (graph == null) throw new RuntimeException("vertexPartition.printVertices need graph to be defined");
        if (graph.getNumberVertices()!=numberElements)throw new RuntimeException("vertexPartition.printAnalysis graph has "+graph.getNumberVertices()+" while partition has "+numberElements+" "+nameOfElements);
        
        // for analysis
        int e=-1; // global edge index
        int sc=-1; // community of source vertex
        double q=-1; // quality contributin from source and all its neighbours
        int degree=-1; // out degree source which lie in its community
        double str=-1; // out strength of edges of source which lie in its community
        int degreeInC=-1; // out degree of edges of source which lie in its community
        double strInC=-1; // out strength of edges of source which lie in its community
        double ew=-1; // edge weight of edge e, from source s to target t
        int t=-1; // globel index of target vertex
        boolean printNearestNeighbours =false;
        boolean weighted = graph.isWeighted();

        boolean printTriangles=false;
        boolean printSquares=false;
        boolean printName=false;
            boolean printNumber=false;
            boolean printPosition=false; 
            boolean printType = graph.isBipartite();
            boolean printStrength=false;
            boolean printMaxWeight=false;
            boolean printClusterCoef=false;
              boolean printRank=false;
            boolean printStructuralHoleData=false;
            if (graph.isVertexLabelled())
             {
              VertexLabel l=graph.getVertexLabel(0);
              printName=l.hasName();
              printNumber=l.hasNumber();
              printPosition=l.hasPosition(); 
              printStrength=l.hasStrength();
              printMaxWeight=l.hasStrength();
              printClusterCoef=l.hasClusterCoef();
              printRank=l.hasRank();
             }
        
        
        if (infoOn) {
             PS.println(cc+"Number of "+nameOfElements+sep+ numberElements+sep+"Number of Communities"+sep+ numberCommunities);
        }
        if (headerOn) {
             PS.print(graph.getVertexStringLabel(cc, sep, 
             printTriangles,  printSquares, true, printName, printNumber, printPosition,
             printType,
             printStrength, printMaxWeight, printClusterCoef,
             printRank, printStructuralHoleData, printNearestNeighbours) );
             PS.print(sep+"k_C"+sep+"k_C/k");
             if (weighted) PS.print(sep+"str_C"+sep+"str_C/str");
             PS.println();
         }
        for (int s=0; s<graph.getNumberVertices(); s++){
            sc = communityOfElement[s];
            PS.print(graph.getVertexString(cc, sep, 
             s,sc,
             printTriangles, printSquares, printName, printNumber, printPosition,
             printType,
             printStrength, printMaxWeight, printClusterCoef,
               printRank, printStructuralHoleData,
             printNearestNeighbours));
            q=0;
            if (weighted) strInC=0;
            degree=graph.getVertexOutDegree(s);
            if (weighted) str=graph.getVertexOutStrength(s);
            degreeInC=0;
            for (int el=0; el<degree; el++){
                t = graph.getVertexTargetQuick(s, el);
                q+=quality.get(s, t);
                e=graph.getStub(s,el);
                ew = graph.getEdgeWeight(e);
                if (communityOfElement[t]==sc) {
                    degreeInC++;
                    if (weighted) strInC+=ew;
                }
            }// eo for el
           PS.print(sep+degreeInC+sep+((double)degreeInC)/((double)degree));
           if (weighted) PS.print(sep+strInC+sep+strInC/str);
           PS.println();

         }//eo for s
    }   

    /** Prints simple list of vertex community pairs.
     * @param PS a print stream for the output such as System.out
     * @param cc comment string used at start of each header or info line
     * @param sep string separating items
     * @param infoOn true (false) if want first row to have basic information on partition
     * @param headerOn true (false) if want header row on (off)
         */
    public void printSimpleVertexCommunityList(PrintStream PS, String cc, String sep, boolean infoOn, boolean headerOn){
        if (graph == null) throw new RuntimeException("vertexPartition.printVertices need graph to be defined");
        if (graph.getNumberVertices()!=numberElements)throw new RuntimeException("vertexPartition.printAnalysis graph has "+graph.getNumberVertices()+" while partition has "+numberElements+" "+nameOfElements);

        if (infoOn) {
             PS.println(cc+"Number of "+nameOfElements+sep+ numberElements+sep+"Number of Communities"+sep+ numberCommunities);
        }
        if (headerOn) {
             PS.println(cc+"Vertex"+sep+"Community");
         }
        for (int s=0; s<graph.getNumberVertices(); s++) PS.println(graph.getVertexName(s)+sep+communityOfElement[s]);
    }

    

    
    /** Prints statistics on a vertex partition.
     * <p>Calculates them if needed
     * @param PS a print stream for the output such as System.out
     * @param cc comment string used at start of each header or info line
     * @param sep string separating items
     * @param infoOn true (false) if want first row to have basic infomration on partition
     * @param headerOn true (false) if want header row on (off)
         */
    @Override
    public void printStatistics(PrintStream PS, String cc, String sep, boolean infoOn, boolean headerOn){
        if (graph == null) throw new RuntimeException("vertexPartition.printCommunities need graph to be defined");
        if (graph.getNumberVertices()!=numberElements)throw new RuntimeException("vertexPartition.printAnalysis graph has "+graph.getNumberVertices()+" while partition has "+numberElements+" "+nameOfElements);
        
        boolean weighted = graph.isWeighted();
        
        if (infoOn) {
             PS.println(cc+"Number of "+nameOfElements+sep+ numberElements+sep+"Number of Communities"+sep+ numberCommunities);
             if (communityStatistics==null) this.calculateCommunityStatistics();
             PS.println(communityStatistics.labelString(sep));
             PS.println(communityStatistics.toString(sep));
        }
        if (headerOn) {
             PS.print(cc+"Community"+sep+"N_C"+sep+"k"+sep+"k_C"+sep+"k_C/k");
             if (weighted) PS.print(sep+"str"+sep+"str_C"+sep+"str_C/str");
             PS.println(sep+"quality");
         }
//        analyse(numberElementsArray,  degreeArray, degreeInCArray, strengthArray , strengthInCArray, qualityArray );
        analyse();
        for (int c=0; c<this.numberCommunities; c++){
           PS.print(c+sep+numberElementsArray[c]+sep+degreeArray[c]+sep+degreeInCArray[c]);
           if (degreeArray[c]>0) PS.print(sep+((double)degreeInCArray[c])/((double)degreeArray[c]));
           else PS.print(sep+"0");
           if (weighted) {
               PS.print(sep+strengthArray[c]+sep+strengthInCArray[c]);
               if (strengthArray[c]>0) PS.print(sep+strengthInCArray[c]/strengthArray[c]);
               else PS.print(sep+"0");
           }
           PS.println(sep+qualityArray[c]);
         }//eo for s
    }   
    
    /**
     * Basic Community Analysis.
     * <p>Could also use <code>projections.ontoVertexPartition</code> 
     * but then need to analyse selfloops.
     * <p>Output are arrays indexed by community number, null if inappropriate.  
     * These are initialised here.
     * <br>TODO not set up for directed graphs.
     * @param numberElementsArray ???
     * @param degreeArray total degree of vertices in each community
     * @param degreeInCArray total number of edges entirely within each community
     * @param strengthArray total strength of vertices in each community
     * @param strengthInCArray total weight of edges entirely within each community
     * @param qualityArray contribution to quality coming from all the vertices within each community
     * @deprecated HAS no content
     */
    public void analyse(int[] numberElementsArray, int[] degreeArray, int[] degreeInCArray, 
            double[] strengthArray , double[] strengthInCArray, double[] qualityArray ){
    }
    
    /**
     * Basic Community Analysis.
     * <p>Could also use <code>projections.ontoVertexPartition</code> 
     * but then need to analyse selfloops.
     * <p>Sets arrays defined in class indexed by community number, null if inappropriate.  
     * These are initialised here.
     * <br>TODO not set up for directed graphs.
     */
    @Override
    public void analyse(){ 
        // for analysis
        int e=-1; // global edge index
        int sc=-1; // community of source vertex
        //double q=-1; // quality contributin from source and all its neighbours
        int degree=-1; // out degree source which lie in its community
        //double str=-1; // out strength of edges of source which lie in its community
        //int degreeInC=-1; // out degree of edges of source which lie in its community
        //double strInC=-1; // out strength of edges of source which lie in its community
        double ew=1; // edge weight of edge e, from source s to target t, default 1 for unweighted
        int t=-1; // globel index of target vertex
        boolean printNearestNeighbours =false;
        boolean weighted = graph.isWeighted();
        numberElementsArray = new  int[numberCommunities];
        degreeArray= new int[numberCommunities];
        degreeInCArray= new int[numberCommunities];
        if (weighted) {
            strengthArray = new double[numberCommunities];
            strengthInCArray = new double[numberCommunities];
        }
        else strengthArray = null;
        qualityArray = new double[numberCommunities];
        
        for (int s=0; s<graph.getNumberVertices(); s++){
            sc = communityOfElement[s];
            numberElementsArray[sc]++;
            degree=graph.getVertexOutDegree(s);
            degreeArray[sc]+=degree;
            for (int el=0; el<degree; el++){
                t = graph.getVertexTargetQuick(s, el);
                qualityArray[sc]+=quality.get(s, t);
                e=graph.getStub(s,el);
                if (weighted) {
                    ew = graph.getEdgeWeight(e);
                    strengthArray[sc]+=ew;
                }
                if (communityOfElement[t]==sc) {
                    degreeInCArray[sc]++;
                    if (weighted) strengthInCArray[sc]+=ew;
                }
            }// eo for el

         }//eo for s   
        ew=0;
        e=1;
    }


    /**
     * Calculates a vertex partition using indicated method.
     * @param g graph to be analysed
     * @param qualityDefinition sets formula for modularity, 0=simple Newman
     * @param nullModel index of null model to use
     * @param method 0=Louvain, 1= Simulated Annealing
     * @param infoLevel controls level of information given
     * @return Best VertexPartition found.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public static VertexPartition calculate(timgraph g, int qualityDefinition, 
            int method, int infoLevel) {
        int nullModel=(g.isDirected()?4:0);
        return calculate(g, qualityDefinition, nullModel, 1, 1.0, method, infoLevel);
    }
    /**
     * Calculates a vertex partition using indicated method.
     * @param g graph to be analysed
     * @param qualityDefinition sets formula for modularity, 0=simple Newman
     * @param nullModel index of null model to use
     * @param method 0=Louvain, 1= Simulated Annealing
     * @param infoLevel controls level of information given
     * @return Best VertexPartition found.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public static VertexPartition calculate(timgraph g, int qualityDefinition, int nullModel,
            int method, int infoLevel) {
        return calculate(g, qualityDefinition, nullModel, 1, 1.0, method, infoLevel);
    }

    /**
     * Calculates a vertex partition using indicated method and given graph..
     * nullModel type set by directed nature of graph.
     * @param g graph to be analysed
     * @param qualityDefinition sets formula for modularity, 0=simple Newman
     * @param lambda scaling factor for null model
     * @param  method 0=Louvain, 1= Simulated Annealing
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param infoLevel controls level of information given
     * @return Best VertexPartition found.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
     public static VertexPartition calculate(timgraph g, 
             int qualityDefinition, 
             int qualityType, double lambda,  int method,
             int infoLevel){
         int nullModel=(g.isDirected()?4:0);
         return calculate(g,qualityDefinition, nullModel, qualityType, lambda,  method,
             infoLevel);
     }
         /**
     * Calculates a vertex partition using indicated method and given graph..
     * @param g graph to be analysed
     * @param qualityDefinition sets formula for modularity, 0=simple Newman
     * @param nullModel index of null model to use
     * @param lambda scaling factor for null model
     * @param  method 0=Louvain, 1= Simulated Annealing
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param infoLevel controls level of information given
     * @return Best VertexPartition found.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
     public static VertexPartition calculate(timgraph g, 
             int qualityDefinition, int nullModel, 
             int qualityType, double lambda,  int method,
             int infoLevel){

        VertexPartition c;
        String methodUsed="";
        switch (method) {
          default:
          case 0:
                LouvainVertexPartition lc = new LouvainVertexPartition(g,qualityDefinition, nullModel, qualityType,lambda,infoLevel, -1);
                lc.calculate();
                c= (VertexPartition) lc;
                methodUsed=METHODS[0];
                break;
            case 1:
                SimulatedAnnealingvertexPartition sac = new SimulatedAnnealingvertexPartition(g,qualityDefinition, nullModel, qualityType, lambda,infoLevel, -1);
                sac.calculateRecursively(1000, 0.5,false);
                c= (VertexPartition) sac;
                methodUsed=METHODS[1];
                break;
        }
        c.recalculateCommunityLabels();
        if (infoLevel>0){
          System.out.println("*** Best community, method "+methodUsed+":-");
          c.printInformation(System.out, "", " ");
          //if (infoLevel>2) c.printCommunities(System.out, "", " ");
        }
        return c;
    }

}
