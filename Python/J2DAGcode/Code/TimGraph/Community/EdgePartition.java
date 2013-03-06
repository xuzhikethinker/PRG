/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.timgraph;
import TimGraph.io.FileInput;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Iterator;


import java.io.PrintStream;
import java.util.Random; //p524 Schildt

import TimUtilities.Permutation;
import TimUtilities.UpdateRecord;

/**
 * Edge partition of a graph.
 * <p>For an undirected graph it is the global edge index <tt>(e/2)</tt> which is used
 * to index the community of an edge.  For directed graphs
 * it is the full global stub index <tt>e</tt>.
 * <br>Contains greedy improvement algorithm.
 * @author time
 */
public class EdgePartition extends Partition {

    private Random Rnd = new Random();
    
//    /**
//     * Number of Edges
//     * <br>Is this really needed?
//     */
//    protected int numberEdges=UNSET;
    
    
    
    /**
     * Number of vertices in this Edge partition
     */
    int verticessInEP=-1; 
       
    /**
     * Gives permutation of Edges.  Used for greedy algorithm.
     */
    private Permutation perm;
    
    /**
     * Used for greedy algorithm to follow update success.
     */
    protected UpdateRecord greedyUpdateRecord;


    
    
 /**
 * Empty Constructor needed for extensions of EdgePartition.
 */
public EdgePartition(){name="generalEP";
    }

 /**
 * Constructor used to set basic parameters from given timgraph.
  * @param tg input graph
 */
public EdgePartition(timgraph tg){name="general"; initialise(tg);};

/**
 * Constructor used to prepare a community.
 * @param n number of Edges
 */
public EdgePartition(int n){
    setDefaultNames();
    setNumberElements(n);
    }
/**
 * Constructor used to prepare a community.
 * @param newname used to set name of partition
 * @param n number of Edges
 */
public EdgePartition(String newname, int n){
    setDefaultNames();
    setNumberElements(n);
    }
/**
 * Constructor used to store basic information about a community.
 * <br>This does not necessarily provide any ability to recalculate.  
 * It merely stores basic information about a community structure e.g. the best one.
 * @param q quality
 * @param nv number of vertices (not used)
 * @param ne number of edges
 * @param cov vector with community labels, i.e. <code>cov[s]</code> is community of Edge s.
 */
public EdgePartition(double q, int nv, int ne, int [] cov){
    setDefaultNames();
    name=name+quality.QdefinitionShortString();
    Q=q;
    setNumberElements(ne);
    setCommunity(cov);
    }

/**
 * Sets up default names for this partition
 */
private void setDefaultNames(){
    name="generalEP";
    nameOfElements="edges";
}

    /**
     * Initialise general EdgePartition class entities.
     * <br>Uses simple Quality (Newman basic modularity)
     *@param tg graph to be analysed as a timgraph
     */
    public void initialise(timgraph tg){
    initialise(tg,0,1,true);
   }
    
    
    /**
     * Initialise general EdgePartition class entities.
     * <br>Uses simple Quality (Newman basic modularity)
     * @param tg graph to be analysed as a timgraph
     * @param qualityDefinition flag to choose formula for modularity.
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param individualCommunities true to set all Edges to their own community (community label = Edge label).  
     * Otherwise all are put into community 0.
     */
    public void initialise(timgraph tg, int qualityDefinition, int qualityType, boolean individualCommunities){
        initialise(tg, qualityDefinition, qualityType, 1.0, (individualCommunities?0:-1));
    }
    
    /**
     * Initialise general EdgePartition class entities.
     * <br>Uses simple Quality (Newman basic modularity)
     * @param tg graph to be analysed as a timgraph
     * @param qualityDefinition flag to choose formula for modularity.
     * @param qualityType   type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param maxNumberCommunities true if one to set all Edges to their own community (label-Edge label).  Otherwise all are putinto community 0.
     */
    public void initialise(timgraph tg, int qualityDefinition, int qualityType, int maxNumberCommunities){
        initialise(tg, qualityDefinition, qualityType, 1.0, maxNumberCommunities);
    }
    
    /**
     * Initialise general EdgePartition class entities.
     * <br>Uses simple Quality (Newman basic modularity).
     * Puts Edges into a single, all distinct or randomly chosen communities.
     * @param tg graph to be analysed as a timgraph
     * @param qualityDefinition flag to choose formula for modularity.
     * @param qualityType   type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param lambda scaling factor for null model in quality function
     * @param maxNumberCommunities Maximum number of communities to use, each chosen at random. 
     * If this is negative then each Edge is placed it its own unique community.
     * If this is zero then all Edges are placed into a single community zero.
     */
    public void initialise(timgraph tg, int qualityDefinition, int qualityType, double lambda, int maxNumberCommunities){
    initialiseGraph(tg, qualityDefinition, qualityType,lambda);
    numberElements = graph.getNumberDistinctEdges();
    //quality = new QualitySparse(tg,qualityDefinition,lambda);
    if (this.infoLevel>1 && this.numberElements<21) quality.printMatrix(System.out, " ", true);
    setCommunity(maxNumberCommunities);
    Q = UNSET; // indicate that Q has not been calculated by using number less than -1
   }
        
    /**
     * Calculate best greedy community.
     * <br>Takes current community and makes several greedy sweeps trying 
     * to maximise quality of community partition.
     * Stops when no more changes made or when maxSweeps done.
     * @param maxSweeps maximum number of greedy seeps to make
     */
    public int calculateBestGreedyCommunity(int maxSweeps){
        perm = new Permutation(numberElements);
        if (!graph.isVertexEdgeListOn()) graph.createVertexGlobalEdgeList();
        greedyUpdateRecord = new UpdateRecord();
        final int printlevel=1;
        if (infoLevel>printlevel) System.out.println("Initial Quality "+calcQuality()+", number of communities "+getNumberOfCommunities());
        for (int n=0; n<maxSweeps; n++)
        {
            double totalQualityChange = oneGreedySweep();
            if (infoLevel>printlevel){
                recalculateCommunityLabels();
                System.out.println("Greedy Sweep "+n+", quality "+calcQuality()+", number of communities "+getNumberOfCommunities()+", Quality change "+totalQualityChange+", "+greedyUpdateRecord.toString());
            }
            if (greedyUpdateRecord.getMade()==0) break;
        }       
        return greedyUpdateRecord.getMade();
    }
  
   
    /**
     * One greedy sweep of all edges.
     * <br>Takes edges in a random order. 
     * For each edge tries the communities of all neighbouring edges
     * and possibility of being a new solo community.
     * Joins site to community with largest quality gain, or leaves it be if thats is best.
     * @return change in quality
     */
    @SuppressWarnings("static-access")
    private double oneGreedySweep(){
        double totalQualityChange=0;
        int updateTried=0;
        int updateMade=0;
        double deltaQremove = 0;
        int oldCommunity=-1;
        perm.newPermutation();
        int e=-1;
        double deltaQ = 0;
        double deltaQmax = 0;
        int cmax=-1;  // non existent community
        for (int i=0; i<graph.getNumberStubs();i++) {
            e=perm.next();
            int eglobal = (graph.isDirected()?e:e<<1);
            oldCommunity = communityOfElement[e];
            deltaQremove = -quality.delta(e, oldCommunity, communityOfElement);

            if (deltaQremove>0) { // better to put in own single Edge community
                cmax=EdgePartition.NEWCOMMUNITYLABEL; 
                //deltaQ=deltaQremove;
                deltaQmax=deltaQremove;
            }
            else { // better not to put in own single Edge community
                //deltaQ = 0;
                deltaQmax=0;
                cmax = oldCommunity;
            }
            // find out all the distinct neighbouring community labels
            TreeSet<Integer> nc = new TreeSet<Integer> ();
            int elast = (graph.isDirected()?eglobal+1:eglobal+2); 
            for (int eg=eglobal; eg<elast;eg++)
            {
                int v=graph.getVertexFromStub(eg);
                int kout = graph.getVertexOutDegree(v);
                for (int e2=0; e2<kout;e2++) {
                    int enn = graph.getStub(v,e2);
                    if (enn==e2) continue;
                    int cnn = communityOfElement[enn];
                    nc.add(cnn);
                }
            }

            
           if (nc.size() ==0) continue; // edge has no neigbouring edges

            // Now find out if its better to join a distinct community of one of the neighbours
            Iterator<Integer> citer = nc.iterator();
            int c=-1;
            while(citer.hasNext()){
                c=citer.next();
                if (c==oldCommunity) continue; 
                //deltaQ = quality.delta(e, c, communityOfElement)+deltaQremove;
                System.out.println("*** THIS NEEDS TO BE CHANGED ***");
                if (UNSET<0) throw new RuntimeException("*** THIS NEEDS TO BE CHANGED ***");
                if (deltaQ < deltaQmax) continue;
                cmax=c;
                deltaQmax = deltaQ;
            }
            updateTried++;
        
            if (cmax!=oldCommunity) { // can increase quality by changing communities
                totalQualityChange+=deltaQmax;
                if (cmax==EdgePartition.NEWCOMMUNITYLABEL) cmax=getEmptyCommunity();
                communityOfElement[e]=cmax;
                updateMade++;
            }
        }
        greedyUpdateRecord.update(updateTried, updateMade);
        return totalQualityChange;
    }
 
   
}
