/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.algorithms.Projections;
import TimGraph.timgraph;
//import java.io.PrintStream;
/**
 * Implements the Louvain community method.  See
 * <code>http://findcommunities.googlepages.com/</code>
 * @author time
 * @see <a href="http://findcommunities.googlepages.com/">Louvain community method</a>
 */
public class LouvainVertexPartition extends VertexPartition {

    /**
     * Maximum number of sweeps to try.
     */
    private static int MAXSWEEPS =100;  
    
    /**
     *  Number of recursions made.
     */
    private int level =VertexPartition.UNSET;
    
    /**
     * Constructor using basic initialisation of VertexPartition class.
     * <br>This sets the quality to be the simple one (basic Newman modularity).
     * nullModel set on directed nature of graph.
     * @param tg graph to be analysed as a timgraph
     * @param qdef sets Modularity method to be used - see Quality class for defintions.  0=simple Newman.
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param infolevel controls level of information to be output, negative for less, positive for more.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public LouvainVertexPartition(timgraph tg, int qdef,  int qualityType, int infolevel){
        int nullModel=(tg.isDirected()?4:0);
        initialise(tg,qdef,nullModel, qualityType,true);
        infoLevel=infolevel;
        setLouvainName();
    }
    /**
     * Constructor using basic initialisation of VertexPartition class.
     * <br>This sets the quality to be the simple one (basic Newman modularity).
     * @param tg graph to be analysed as a timgraph
     * @param qdef sets Modularity method to be used - see Quality class for defintions.  0=simple Newman.
     * @param nullModel index of null model to use
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param infolevel controls level of information to be output, negative for less, positive for more.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public LouvainVertexPartition(timgraph tg, int qdef, int nullModel, int qualityType, int infolevel){
        initialise(tg,qdef,nullModel, qualityType,true);
        infoLevel=infolevel;
        setLouvainName();
    }
    
    /**
     * Constructor using basic initialisation of VertexPartition class.
     * <br>This sets the quality to be the simple one (basic Newman modularity).
     * @param tg graph to be analysed as a timgraph
     * @param qdef sets Modularity method to be used - see Quality class for defintions.  0=simple Newman.
     * @param nullModel index of null model to use
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param lambda scaling factor for null model in quality function
     * @param infolevel controls level of information to be output, negative for less, positive for more.
     * @param maxNumberCommunities Maximum number of communities to use, each chosen at random. 
     * If this is negative then each vertex is placed it its own unique community.
     * If this is zero then all vertices are placed into a single community zero.
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public LouvainVertexPartition(timgraph tg, int qdef, int nullModel, int qualityType, double lambda,
            int infolevel, int maxNumberCommunities){
    infoLevel=infolevel;
    initialise(tg,qdef, nullModel, qualityType, lambda, maxNumberCommunities);
    setLouvainName();
    }
    
    /**
     * Sets name of Louvain partition
     */
    private void setLouvainName(){
        name="Louvain"+quality.QdefinitionShortString();
    }
 
    /**
     * Iterates to find better communities.
     * <br>Projects onto graph of communities and repeats until can not get any smaller.
     * Level of recursion is forced to be zero for initial graph.
     */
    public void calculate(){
        calcQuality(); // need to set initial value of quality and the community vector for bestQuality
        //bestCommunity = new VertexPartition(this.getQuality(),this.numberVertices,this.numberEdges,this.communityOfVertex);
        calculate(0);
    }
    
    /**
     * Iterates to find better communities.
     * <br>Projects onto graph of communities and repeats until can not get any smaller.
     * <br>Leaves quality <code>Q</code> and <code>communityOfVertex[]</code> 
     * set to be the current best values for all lower (greater l) recursion levels 
     * @param l level of recursion
     */
    private void calculate(int l){
        level=l;
        if (infoLevel>0) System.out.println("--- recursion level "+level);
        if (infoLevel>2) graph.printVertices(System.out, true, true, true);
        if (infoLevel>1) graph.printEdges();
        calculateBestGreedyCommunity(MAXSWEEPS);
        boolean ignoreNegativeLabels=false;
        relabelCommunities(ignoreNegativeLabels);
        calcQuality(); // calculate absolute value of quality.
        if (infoLevel>0) System.out.println("Number vertices = "+graph.getNumberVertices()+", number communities = "+getNumberOfCommunities()+", quality = "+getQuality());
        if (infoLevel>1) this.printCommunityMatrix(System.out, " ", " , ");
        if(graph.getNumberVertices()>  getNumberOfCommunities()) 
        {
//            timgraph projg = new timgraph(graph,communityOfElement, numberCommunities, false, false);
            timgraph projg = Projections.ontoVertexPartition(graph.inputName.getNameRoot()+"!",graph,communityOfElement, numberCommunities, false, false);
            LouvainVertexPartition projLC = new LouvainVertexPartition(projg, quality.Qdefinition, quality.nullModelSwitch, quality.getQualityTypeNumber(), quality.lambda, infoLevel,-1 );
            projLC.calculate(l+1);
            if(projLC.getQuality()>getQuality() ){
            setQuality(projLC.getQuality());
            for (int s=0; s<numberElements; s++) {
                communityOfElement[s] = projLC.getCommunity(communityOfElement[s]);
            } // eo for s   
            } // eo if projQ>Q 
        }
    }
    

}
