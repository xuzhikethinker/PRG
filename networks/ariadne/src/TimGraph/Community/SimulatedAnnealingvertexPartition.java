/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.timgraph;
//import java.io.PrintStream;
import java.util.Random;

import TimUtilities.Permutation;
import TimUtilities.UpdateRecord;
/**
 * Implements the Louvain community method.  See
 * <code>http://findcommunities.googlepages.com/</code>
 * @author time
 */
public class SimulatedAnnealingvertexPartition extends VertexPartition {

    /**
     * Negative integer used to give temporary community witth just one site.
     */
//    private static int TEMPCOMMUNITYLABEL =-97531;  
    /**
     * Maximum number of sweeps to try.
     */
    private static int MAXSWEEPS =5;  
    
    public static final String SIMANNAME = "SimAn";
    
    private Permutation edgePerm;
    private Permutation vertexPerm;
    
    private UpdateRecord updateRecord;
    
    private Random Rnd;
    
    private double bestQuality=-9876543.21;
    private int [] bestcommunityOfElement;
    
    /**
     * Maximum number of community labels.
     */
    private int maxCommunities;
    
    /**
     *  Number of recursions made.
     */
    private int level =VertexPartition.UNSET;
    

    
    /**
     * Constructor using basic initialisation of VertexPartition class.
     * <br>This sets the quality to be the simple one (basic Newman modularity).
     * It sets the maximum number of communities to use to be the number of vertices.
     *@param tg graph to be analysed as a timgraph
     */
    public SimulatedAnnealingvertexPartition(timgraph tg){
    name=SIMANNAME;
    initialise(tg);
    maxCommunities=tg.getNumberVertices();
    Rnd = new Random();
    }
    
    /**
     * Constructor using basic initialisation of VertexPartition class.
     * <br>This sets the quality to be the simple one (basic Newman modularity).
     * @param tg graph to be analysed as a timgraph
     * @param numberCommunities number of communities to use initially, also sets maximum number.  0, 1 = single community, -1 = individual communities
     */
    public SimulatedAnnealingvertexPartition(timgraph tg, int numberCommunities){
    initialise(tg,0,1,1.0,numberCommunities);
    if (numberCommunities<2) maxCommunities=numberElements;
    else maxCommunities=numberCommunities;
    
    Rnd = new Random();
    }
    
        /**
     * Constructor using basic initialisation of VertexPartition class.
     * <br>This sets the quality to be the simple one (basic Newman modularity).
     * @param tg graph to be analysed as a timgraph
     * @param qdef sets Modularity method to be used - see Quality class for defintions.  0=simple Newman.
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param newinfolevel controls level of information to be output, negative for less, positive for more.
     * @param numberCommunities number of communities to use initially
     * @param maxNumberCommunities Maximum number of communities to use, each chosen at random. 
     * If this is negative then each vertex is placed it its own unique community.
     * If this is zero then all vertices are placed into a single community zero.
     * @param lambda scaling factor for null model in quality function
     */
    public SimulatedAnnealingvertexPartition(timgraph tg, int qdef, int qualityType, int newinfolevel, 
            int numberCommunities, int maxNumberCommunities, double lambda){
    initialise(tg,qdef, qualityType, lambda ,numberCommunities);
    name=SIMANNAME+quality.QdefinitionShortString();
    maxCommunities=Math.max(maxNumberCommunities,getNumberCommunities());
    infoLevel=newinfolevel;
    Rnd = new Random();
    }
    
        /**
     * Constructor using basic initialisation of VertexPartition class.
     * <br>This sets the quality to be the simple one (basic Newman modularity).
     * @param tg graph to be analysed as a timgraph
     * @param qdef sets Modularity method to be used - see Quality class for definitions.  0=simple Newman.
     * @param qualityType type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
     * @param lambda scaling factor for null model in quality function
     * @param newinfolevel controls level of information to be output, negative for less, positive for more.
     * @param numberCommunities number of communities to use initially, also sets maximum number.  0, 1 = single community, -1 = individual communities
     */
    public SimulatedAnnealingvertexPartition(timgraph tg, int qdef, int qualityType, double lambda, int newinfolevel, 
            int numberCommunities){
    initialise(tg,qdef, qualityType, lambda,numberCommunities);
    name=SIMANNAME+quality.QdefinitionShortString();
    if (numberCommunities<2) maxCommunities=numberElements;
    else maxCommunities=numberCommunities;
    infoLevel=newinfolevel;
    Rnd = new Random();
    }
    
    /**
     * Iterates to find better communities.
     * <br>Projects best community found as result of one simulated annealing round
     * onto graph of communities and repeats until cannot get any smaller.
     * Level of recursion is forced to be zero for initial graph.
     * @param numberOfSweeps maximum number of sweeps to make
     * @param betaInitial initial value of beta.
     * @param greedyAfterOneSweep true if want to make a greedy update after one edge and vertex sweep.
     */
    public void calculateRecursively(int numberOfSweeps, double betaInitial, boolean greedyAfterOneSweep){
        calcQuality(); // need to set intial value of quality and the community vector for bestQuality
        //bestCommunity = new VertexPartition(this.getQuality(),this.numberElements,this.numberEdges,this.communityOfElement);
        calculateRecursively(0, numberOfSweeps, betaInitial, greedyAfterOneSweep);
    }

           /**
     * Iterates to find better communities.
     * <br>Projects onto graph of communities and repeats until can not get any smaller.
     * <br>Leaves quality <code>Q</code> and <code>communityOfElement[]</code> 
     * set to be the current best values for all lower (greater l) recursion levels 
     * @param l level of recursion
     * @param numberOfSweeps maximum number of sweeps to make
     * @param betaInitial initial value of beta.
     * @param greedyAfterOneSweep true if want to make a greedy update after one edge and vertex sweep.
     */
    private void calculateRecursively(int l, int numberOfSweeps, double betaInitial, boolean greedyAfterOneSweep) {
        level = l;
        if (infoLevel > 0) {
            System.out.println("--- recursion level " + level);
        }
        if (infoLevel > 2) {
            graph.printVertices(System.out, false, false, true);
        }
        if (infoLevel > 1) {
            graph.printEdges();
        }
        calculateBestGreedyCommunity(MAXSWEEPS);
        boolean ignoreNegativeLabels=true;
        relabelCommunities(ignoreNegativeLabels);
        calcQuality(); // calculate absolute value of quality.
        if (infoLevel > 0) {
            System.out.println("Number vertices = " + graph.getNumberVertices() + ", number communities = " + getNumberOfCommunities() + ", quality = " + getQuality());
        }
        if (infoLevel > 1) {
            printCommunityMatrix(System.out, " ", " , ");
        }
        if (graph.getNumberVertices() > getNumberOfCommunities()) {
            timgraph projg = new timgraph(graph, communityOfElement, numberCommunities, false, false);
            SimulatedAnnealingvertexPartition projVP = new SimulatedAnnealingvertexPartition(projg, quality.Qdefinition, quality.getQualityTypeNumber(), quality.lambda,
                    infoLevel, -1);
            projVP.calculateRecursively(l + 1, numberOfSweeps, betaInitial, greedyAfterOneSweep);
            if (projVP.getQuality() > getQuality()) {
                setQuality(projVP.getQuality());
                for (int s = 0; s < numberElements; s++) {
                    communityOfElement[s] = projVP.getCommunity(communityOfElement[s]);
                } // eo for s   
            } // eo if projQ>Q 
        }
    }
    

    /**
     * Calculate communities using method specified.
     * <p>Assumes that transfer matrix is already set up but will set up influence matrix.
     * Recalculates the labels so they run from 0 to (numberCommunities-1).
     * Makes a greedy improvement only at the end.
     * @param numberOfSweeps maximum number of sweeps to make
     * @param betaInitial initial value of beta.
     */
    public void calc(int numberOfSweeps, double betaInitial){
        calc(numberOfSweeps, betaInitial, false );
    }
    
    /**
     * Calculate communities using method specified.
     * <p>Assumes that transfer matrix is already set up but will set up influence matrix.
     * Recalculates the labels so they run from 0 to (numberCommunities-1).
     * @param numberOfSweeps maximum number of sweeps to make
     * @param betaInitial initial value of beta.
     * @param greedyAfterOneSweep true if want to make a greedy update after one edge and vertex sweep.
     */
    public void calc(int numberOfSweeps, double betaInitial, boolean greedyAfterOneSweep){
        bestcommunityOfElement = new int[numberElements];
        updateBestCommunity();
        bestQuality = quality.calc(bestcommunityOfElement);
        
        double beta=betaInitial;
        
        edgePerm = new Permutation(numberEdges);
        vertexPerm = new Permutation(numberElements);
         
        double totalQualityChange=0;
        final double invnumberElements = 1.0/((double) numberElements);
        final double updateFractionFinal = invnumberElements/1.8;
        final double betaInc1 = 1.0+1.0/((double) numberElements);
        final int NoChangeSweeps =10; // number of sweeps to make with no changes to best result before stopping.
        boolean keepGoing=true;
        double lastBestQuality = DUNSET;
        int countToStop= NoChangeSweeps;
        System.out.println("Initial Quality "+calcQuality()+", number of communities "+getNumberOfCommunities());
        for (int n=0; n<numberOfSweeps; n++)
        {
            updateRecord = new UpdateRecord();
            totalQualityChange = oneEdgeSweep(beta,10);
            //recalculateCommunityLabels();
            double uf=updateRecord.getTotalFractionMade();
            // Check two stop requirements
            if (bestQuality != lastBestQuality)  {
                lastBestQuality = bestQuality;
                countToStop=NoChangeSweeps;
            }
            
            if (infoLevel>0) System.out.println("beta = "+beta+" Sweep "+n+" count to stop "+countToStop+" Best Quality "+bestQuality+", Quality "+calcQuality()+", number of communities "+getNumberOfCommunities()+", Quality change "+totalQualityChange+", % changed = "+uf);
            //totalQualityChange = oneVertexSweep(beta);
            //recalculateCommunityLabels();
            //if (infoLevel>0) System.out.println("beta = "+beta+" Best Quality "+bestQuality+", Quality "+calcQuality()+", number of communities "+getNumberOfCommunities()+", Quality change "+totalQualityChange+", "+updateRecord.toString());
            if (--countToStop <0) {
                if (keepGoing) keepGoing=false;
                else break;
            }
            else keepGoing=true;
            if (uf>0.5) beta=beta*2.0;
            else beta=beta*betaInc1;
            // should we put these next two lines here or after all cooling done?
            if (greedyAfterOneSweep){
            selectBestCommunity(); 
            calculateBestGreedyCommunity(5);
            }
        }
        if (!greedyAfterOneSweep){
            selectBestCommunity(); 
        calculateBestGreedyCommunity(5);
        }
         boolean ignoreNegativeLabels=true;
         relabelCommunities(ignoreNegativeLabels);
         System.out.println("Final Quality "+calcQuality()+", number of communities "+getNumberOfCommunities()+", greedy improvements "+greedyUpdateRecord.toString() );
    }
    
    
    /**
     * One MonteCarlo sweep of all edges.
     * <br>Takes edges in a random order. 
     * Tries all possible existing communities not just occupied ones.
     * Will change community if change in quality passes Metropolis accept/reject.
     * @param beta inverse temperature
     */
    private double oneEdgeSweep(double beta, int iterations) {
        double totalQualityChange = 0;
        double bestQualityChange = 0;
        int oldCommunity = -1;
        int updateTried = 0;
        int updateMade = 0;
        int s = -1;
        int t = -1;
        double deltaQ = 0;
        int cnew = -1;  // non existent community
        int e = -1;
        for (int i = 0; i < iterations; i++) {
            updateTried = 0;
            updateMade = 0;
            edgePerm.newPermutation();
            // could sample vertex pairs in different way e.g. by taking random edges
            while (edgePerm.hasMore()) {
                updateTried++;
                e = edgePerm.next();
                s = graph.getVertexFromStub(e);
                //t = graph.getOtherVertexFromStub(e);
                oldCommunity = communityOfElement[s];
                do{
                cnew = Rnd.nextInt(maxCommunities); //communityOfElement[t];
                } while (cnew == oldCommunity);
                //if (cnew == oldCommunity) cnew = Rnd.nextInt(nextCommunityLabel);
                deltaQ = quality.delta(s, cnew, communityOfElement) - quality.delta(s, oldCommunity, communityOfElement);
                if ((deltaQ > 0) || (Math.exp(beta * deltaQ) > Rnd.nextDouble())) {
                    totalQualityChange += deltaQ;
                    communityOfElement[s] = cnew;
                    if (totalQualityChange > bestQualityChange) {
                        updateBestCommunity();
                        bestQualityChange = totalQualityChange;
                    }
                    updateMade++;
                }
            } //while
            if (updateTried<updateMade*2) continue;
        } //for i
        updateRecord.update(updateTried, updateMade);
        selectBestCurrentCommunity();
        //bestQuality = quality.calc(bestcommunityOfElement);
        return totalQualityChange;
    }

    
        /**
     * One MonteCarlo sweep of attempts to put vertices in their own community.
     * <br>Takes edges in a vertices in a random order. 
     * Will change community if change in quality passes Metropolis accept/reject.
     * @param beta inverse temperature
     */
    private double oneVertexSweep(double beta){
        double totalQualityChange=0;
        double bestQualityChange=0;
        int updateTried=0;
        int updateMade=0;
        //double deltaQremove = 0;
        int oldCommunity=-1;
        vertexPerm.newPermutation();
        int s=-1;
        double deltaQ = 0;
        //int cnew=-1;  // non existent community
        while (vertexPerm.hasMore()){
            updateTried++;
            s=vertexPerm.next();
            oldCommunity = communityOfElement[s];
            deltaQ = -quality.delta(s, oldCommunity, communityOfElement);
            if ((deltaQ>0) || (Math.exp(beta*deltaQ)>Rnd.nextDouble() )) 
            {    
                totalQualityChange+=deltaQ;
                //cnew=getEmptyCommunity();
                communityOfElement[s]=getEmptyCommunity();
                if (totalQualityChange>bestQualityChange) {
                    updateBestCommunity();
                    bestQualityChange = totalQualityChange;
                }
                updateMade++;
            }
        } //while
        updateRecord.update(updateTried, updateMade);
        selectBestCurrentCommunity();
        return totalQualityChange;
    }

    
    /**
     * Sets the current and best communities and their quality values to be the best known.
     */
    private void selectBestCurrentCommunity(){
        recalculateCommunityLabels();
        double currentQuality =quality.calc(communityOfElement);
        if (bestQuality>currentQuality) {
            currentQuality=bestQuality;
            for (int s=0; s<numberElements; s++)
                communityOfElement[s]=bestcommunityOfElement[s];
            }
        else {
            bestQuality=currentQuality;
            updateBestCommunity();
        }
    }

    private void selectBestCommunity(){
        recalculateCommunityLabels();
        double currentQuality =quality.calc(communityOfElement);
        if (bestQuality>currentQuality) for (int s=0; s<numberElements; s++) bestcommunityOfElement[s]=communityOfElement[s];
    }

    private void updateBestCommunity(){
        for (int s=0; s<numberElements; s++) bestcommunityOfElement[s]=communityOfElement[s];
    }
}
