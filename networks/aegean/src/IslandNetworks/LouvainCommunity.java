/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import java.io.PrintStream;
//import java.util.TreeSet;

import TimUtilities.Permutation;
import TimUtilities.UpdateRecord;
/**
 * Implements the Louvain community method.  See
 * <code>http://findcommunities.googlepages.com/</code>
 * @author time
 */
public class LouvainCommunity {

    /**
     * Negative integer used to give temporary community witth just one site.
     */
    private static int TEMPCOMMUNITYLABEL =-97531;  
    /**
     * Maximum number of sweeps to try.
     */
    private static int MAXSWEEPS =5;  
    private islandNetwork in;
    private double outVector[];
    private double inVector[];
    private double outNormalisation;
    private double inNormalisation;
    private double [][] influenceQ;
    
    private int communityOfSite[]; // the label of the community of given site
    private int numberInCommunity[]; // the number in a community
    private int numberOfCommunities; // the number in a community
//    /**
//     * Labels of communities are integers numbered from 0.
//     * <br>Exception is the temporary community label;
//     */
//    private TreeSet<Integer> communityLabels; 
    
    private Permutation perm;
    
    private UpdateRecord updateRecord;
    
    /**
     * Constructor.
     * <br>Initialises the out vector to be the weight of each site. 
     * The in vector is initialised to be the incoming strength. 
     * 
     */
    public LouvainCommunity(islandNetwork inputIn){
    in=inputIn;
    inVector = new double[in.numberSites];
    outVector = new double[in.numberSites];
    //communityLabels = new TreeSet<Integer>();
    communityOfSite = new int[in.numberSites];
    numberInCommunity = new int[in.numberSites];
    for (int s=0; s<in.numberSites; s++){
        outVector[s] = in.siteSet.getWeight(s);
        inVector[s] = in.siteSet.getStrengthIn(s);
        communityOfSite[s]=s;
        numberInCommunity[s]=1;   
    }
    numberOfCommunities=in.numberSites;
    inNormalisation = normalise(inVector);
    outNormalisation = normalise(outVector);
    
    
    }
    
    /**
     * Normalise a vector so sum of entries is one.
     * @param vector to be normalised
     * @return normalisation, if absolute value less than 1e-10 then vector is not normalised
     */
    private double normalise(double [] vector)
    {
        double norm=0;
        for (int i=0; i< vector.length; i++) norm+= vector[i];
        if (Math.abs(norm)>1e-10) for (int i=0; i< vector.length; i++) vector[i] = vector[i]/norm;
        return norm;
    }
    /**
     * Caluculate communities using Louvain method.
     * <p>Assumes that transfer matrix is already set up but will set up influence matrix.
     * @param prob influence probability
     */
    public void calcCommunity(double prob){
        in.transferMatrix.calcInfluenceMatrix(prob);
        int smi = in.transferMatrix.checkInfluenceSubMarkovian(1e-6);
        if (smi>=0) System.err.println("!!! Influence is no subMarkovian in column "+smi);
        int nni = in.transferMatrix.checkInfluenceNonNegative(1e-6);
        if (nni>=0) System.err.println("!!! Influence is no subMarkovian in row*dimension+ column "+nni);
        int test=in.transferMatrix.checkDecomposition(1e-3);
        if (test<0) System.err.println("*** in calcCommunity, transfer matrix decomposition check failed, result ="+test);
        in.transferMatrix.printEigenValueList("", "   ", System.out, 4, true);
        in.transferMatrix.printTransferMatrix("", "   ", System.out, 4, true);
        in.transferMatrix.printInfluenceMatrix("", "   ", System.out, 4, true);
        
        
        influenceQ = new double[in.numberSites][in.numberSites];
        // normalise the Influence part of Quality
        for (int s=0; s<in.numberSites; s++) 
            for (int t=0; t<in.numberSites; t++) 
                influenceQ[s][t] = in.transferMatrix.getInfluence(s, t)*outVector[t];
        perm = new Permutation(in.numberSites);
         
        updateRecord = new UpdateRecord();
        System.out.println("Initial Quality "+calcQuality()+", number of communities "+this.numberOfCommunities);
        for (int n=0; n<MAXSWEEPS; n++)
        {
            double totalQualityChange = oneSweep();
            System.out.println("Quality "+calcQuality()+", number of communities "+this.numberOfCommunities+", Quality change "+totalQualityChange+", "+updateRecord.toString());
            if (updateRecord.getMade()==0) break;
        }
        
    }
    
    
    /**
     * One greedy sweep of all sites.
     * <br>Takes sites in a random order. 
     * Tries all possible existing communities and new solo community option.
     * Joins site to community with largest quality gain.
     */
    private double oneSweep(){
        double totalQualityChange=0;
        int updateTried=0;
        int updateMade=0;
        double deltaQremove = 0;
        int oldCommunity=-1;
        perm.newPermutation();
        int s=-1;
        double deltaQadd = 0;
        double deltaQaddmax = 0;
        int cmax=-1;  // non existent community
        for (int i=0; i<in.numberSites;i++) {
            s=perm.next();
            oldCommunity = communityOfSite[s];
            int n = --numberInCommunity[oldCommunity];
            if (n==0) numberOfCommunities--;
            communityOfSite[s] = TEMPCOMMUNITYLABEL;
            deltaQremove = -deltaQuality(s, oldCommunity);
            deltaQaddmax= -deltaQremove;
            if (deltaQremove>0) cmax=in.numberSites; // use to flag that giving this its own unique community raises quality
            else cmax=TEMPCOMMUNITYLABEL; // this ensures will be returned to old community
            for (int c=0; c<numberInCommunity.length; c++){
                if (numberInCommunity[c]==0) continue; 
                if (c==oldCommunity) continue; 
                deltaQadd = deltaQuality( s, c);
                if (deltaQadd < deltaQaddmax) continue;
                cmax=c;
                deltaQaddmax = deltaQadd;
            }
            updateTried++;
        
            if (cmax>0) { // can increase quality by swapping communities
                totalQualityChange+=deltaQremove;
                if (cmax==in.numberSites) cmax=getEmptyCommunity();
                else totalQualityChange+=deltaQadd;
                communityOfSite[s]=cmax;
                numberInCommunity[cmax]++;
                if (numberInCommunity[cmax]==1) numberOfCommunities++;           
                updateMade++;
            }
            else {
            communityOfSite[s] = oldCommunity;
            numberInCommunity[oldCommunity]++;          
            }            

        }
        updateRecord.update(updateTried, updateMade);
        return totalQualityChange;
    }
    
//
    /**
     * Contribution to Quality from site s if it is in community c.
     * <p>Does not include the contribution from removing site s from its current community.
     * To get that call this routine with c equal to the current  community label of s.
     * <br> <code>Q=\sum_{i,j} (influenceQ_{ij} - inVector[i] OutVector[j]) delta(c_i,c_j)</code>
     * where <code>influenceQ_{ij}</code> ought to be influence normalised by the (out) strength
     * and the in/out vectors are the in/out strength of each vertex normalised by total strength.
     * @param s site 
     * @param c community label it is to be added to
     * @return Contribution to Quality 
     */
    public double deltaQuality(int s, int c){
        double deltaQ = 0;
        for (int t=0; t<in.numberSites; t++){
            if ((communityOfSite[t]==c) && (s !=t)) {
                deltaQ+= influenceQ[s][t] - inVector[s]*outVector[t];
                deltaQ+= influenceQ[t][s] - inVector[t]*outVector[s];
            }
        }
        return deltaQ;
    }
    
    public double calcQuality() {
        int c;
        double Q=0;
        for (int s = 0; s < in.numberSites; s++) {
            c = communityOfSite[s];
            for (int t = 0; t < in.numberSites; t++) {
                if ((communityOfSite[t] == c) && (s != t)) {
                    Q += influenceQ[s][t] - inVector[s] * outVector[t];
                }
            }
        }
        return Q;
    }
    
    /**
     * Finds label of empty community.
     * @return lable of empty community, -1 if none found.
     */
    private int getEmptyCommunity(){
        for (int c=0; c<numberInCommunity.length; c++)
                if (numberInCommunity[c]==0) return c;
        return -1;
    }
    
    
    /** Prints Communities.
         *@param PS a print stream for the output such as System.out
         *@param cc comment string
         *@param sep separation string
         */     
     public void  printCommunities(PrintStream PS, String cc, String sep){ 
         PS.println(cc+"Number of Communities"+sep+ numberOfCommunities);
         int count = numberOfCommunities;
         PS.println(cc+"Count"+sep+"Label"+sep+" Number");
         for (int c=0; c<numberInCommunity.length; c++)
                if (numberInCommunity[c]>0) PS.println(cc+(count--)+sep+ c+sep+numberInCommunity[c]);
         PS.println(cc+"Site"+sep+"Label");
         for (int s=0; s<in.numberSites; s++)
             PS.println(cc+s+sep+communityOfSite[s]);
    }
    
}
