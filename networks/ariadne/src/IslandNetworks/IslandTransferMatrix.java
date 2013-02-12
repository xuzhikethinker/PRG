/*
 * IslandTransferMatrix.java
 *
 * Created on 27 May 2007, 18:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;


import IslandNetworks.Vertex.IslandSiteSet;
import IslandNetworks.Edge.IslandEdgeSet;
import IslandNetworks.Edge.IslandEdge;


//import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.colt.matrix.linalg.Algebra;

import TimUtilities.NumbersToString;
import TimUtilities.TimSort;
import java.io.PrintStream;


/**
 * Sets up a transfer-like matrix.
 *<p>
 * Uses the edge variable values selected for display  
 * and on site weights of an Island Network
 * <p>The influence matrix is the diffusion matrix where there is a probability p of continuing.
 * The influenceValueIn and Out arrays are just sums of rows and columns while influenceStrengthIn is
 * <pre>sum_j I_{ij}*(Sv)_j</pre>, that is it is the weighted influence.
 * <p>To find the influence matrix it uses the CERN Colt library and its eigenvalue decomposition.
 * The following is taken from the <code>cern.colt.matrix.linalg.EigenvalueDecomposition</code>
 * about the representation used.  Note that for non-negative real but unsymmetric matrices 
 * the non-leading eigenvalues can be complex pairs.  However they can be represented in a real (SO(2) not U(1)) 
 * doublet real representation and that is what is used in the CERN Colt routines.
 * <p>If <tt>A</tt> is symmetric, then <tt>A = V*D*V'</tt> where the eigenvalue matrix <tt>D</tt> is
 * diagonal and the eigenvector matrix <tt>V</tt> is orthogonal.
 * I.e. <tt>A = V.mult(D.mult(transpose(V)))</tt> and
 * <tt>V.mult(transpose(V))</tt> equals the identity matrix.
 * <P>
 * If <tt>A</tt> is not symmetric, then the eigenvalue matrix <tt>D</tt> is block diagonal
 * with the real eigenvalues in 1-by-1 blocks and any complex eigenvalues,
 * <tt>lambda + i*mu</tt>, in 2-by-2 blocks, <tt>[lambda, mu; -mu, lambda]</tt>.
 * The columns of <tt>V</tt> represent the eigenvectors in the sense that <tt>A*V = V*D</tt>,
 * i.e. <tt>A.mult(V) equals V.mult(D)</tt>.  The matrix <tt>V</tt> may be badly
 * conditioned, or even singular, so the validity of the equation
 * <tt>A = V*D*inverse(V)</tt> depends upon <tt>Algebra.cond(V)</tt>.
 *
 * @author time
 */
public class IslandTransferMatrix {
    static final double DUNSET = -97531.0;
    static final int IUNSET = -86420;
    /**
     * mode number as specified in the {@link Islandnetworks.Islandnetworks.TransferMatrixMode} class.
     */
    private int modeNumber=0;
    int edgeVariableIndex=IslandEdge.weightINDEX;
    int dimension=IUNSET; 
// Care: transferMatrix[i][j] is from j to i as in normal matrix language
    Algebra alg;    
    /**
     * Complete transfer matrix, T.
     */
    DenseDoubleMatrix2D transferMatrix;
    /**
     * Transfer matrix without largest eigenvalue in its decomposition, T'.
     */
    DoubleMatrix2D transferprimeMatrix;
    double[] restartVector;
    boolean restartVectorRandomSites = false; // true if forcing random sites for restart vector, else will be site weights.
    
    // following are not defined if evcalc=false, call calc
    /**
     * True if e/value calculation has been attempted
     */
    boolean evcalc = false;
     /**
      * True if e/value calculation attempted and failed.
      */
    boolean evCalcFailed = false;
        EigenvalueDecomposition tMED;
        DoubleMatrix2D Vmatrix;
        /**
         * Diagonal eigenvalue matrix
         */
        DoubleMatrix2D Dmatrix;
        /**
         * Diagonal eigenvalue matrix without largest eigenvalue
         */
        DoubleMatrix2D Dprimematrix;
    private double [] absevalues; // absolute value of eigenvalues
        TimSort evOrder;
        double tolerance = DUNSET;
        /**
         * Normalisation factor used on eigenvalues.
         */
        private double normaliseFactor=1.0;

        
    
    
    // following are not defined if inflcalc=false
    boolean inflcalc =false;
    private double influenceProbability=DUNSET;
    double influenceSteps = DUNSET; //# steps
    /**
     * Influence matrix with all eigenvalues.
     * <p>Based on D matrix.
     */
    DoubleMatrix2D influenceMatrix;
    /**
     * Influence matrix without largest eigenvalue.
     * <p>Based on Dprime matrix.
     */
    DoubleMatrix2D influenceprimeMatrix;
    DoubleMatrix2D Vinverse;
    double[] influenceValueIn;
    double[] influenceStrengthIn;
    double[] influenceValueOut;
    double[] influenceprimeValueIn;
    double[] influenceprimeStrengthIn;
    double[] influenceprimeValueOut;
    //double[] influenceValueTotal;

                
        
//    /** Creates a new instance of IslandTransferMatrix */
//    public IslandTransferMatrix(){
//    }
        
    /** Sets up a new instance of IslandTransferMatrix.
     * <br>Transfer matrix is calculated but not influence matrix.
     *@param mode mode number as specified in the showTransferMatrixType routine
     *@param numberSites number of sites
     *@param siteSet set of sites
     *@param edgeSet set of all edges
     *@param edgeVariableIndexInput index of edge variable to use
     */
    public IslandTransferMatrix(int mode, int numberSites, IslandSiteSet siteSet, IslandEdgeSet edgeSet, int edgeVariableIndexInput)
          {
           alg = new Algebra();
           edgeVariableIndex=edgeVariableIndexInput;
           calcTransferMatrix(mode, numberSites, siteSet, edgeSet);
    }
    
    /** Deep Copy of IslandTransferMatrix */
    public IslandTransferMatrix(IslandTransferMatrix old ){
        alg = new Algebra();
        modeNumber=old.modeNumber;
        edgeVariableIndex=old.edgeVariableIndex;
        dimension=old.dimension; 
        transferMatrix = (DenseDoubleMatrix2D) old.transferMatrix.copy();  // deep copy
        if (old.transferprimeMatrix!=null) transferprimeMatrix = (DenseDoubleMatrix2D) old.transferprimeMatrix.copy();  // deep copy
        //transferMatrix = new DenseDoubleMatrix2D(dimension,dimension);
        restartVector = new double [dimension];
        System.arraycopy(old.restartVector, 0, restartVector, 0, dimension);
        
        // recalculate rather than copy values        
        if (old.evcalc)
        {
            calcEigenvalueDecomposition(old.tolerance);            
            if (old.inflcalc) calcInfluenceMatrix(old.influenceProbability);
        }        
    }// eo constructor deep copy
    
 // ----------------------------------------------------------------------
   /** Creates a new instance of IslandTransferMatrix.
     *@param mode mode number as specified in the showTransferMatrixType routine
     *@param numberSites number of sites
     *@param siteArray array of sites
     *@param edgeSet set of all edges
     */
    private void calcTransferMatrix(int mode, int numberSites, IslandSiteSet siteSet, IslandEdgeSet edgeSet)
          {
           modeNumber=mode;
           //edgeVariableIndex=edgeVariableIndexInput;
           dimension= numberSites;
           // Care: transferMatrix[i][j] is from j to i as in normal matrix language
           //transferMatrix = new DenseDoubleMatrix2D(dimension,dimension);
           restartVector = new double [dimension];
           calcRestartVector(siteSet);
           transferMatrix = new DenseDoubleMatrix2D(dimension,dimension);
           calcTransferMatrix(edgeSet);
    }

    /** Calculate some Transfer Matrix for diffusion analysis.
     *<br>Type of matrix set by global <code>modeNumbe</code>.
     * Transfer matrix from site j to site i is of the basic form <code>T_{ij} = e_{ji}</code>
     * but normalisation and dead ends are dealt with differently in different modes.
     * <br>Must ensure that row sums are less than or equal to one which may not happen with different types of edges.
     *@param edgeSet set of all edges
     * @param normalise forces normalisation of out strengths to all be one
     */
    private void calcTransferMatrix(IslandEdgeSet edgeSet)
    {
        // calculate transfer matrix from site i to site j matrix element transferMatrix[j][i]
        double outEdges= -99.0;
        double outEdgeDeficit=-876;
        //boolean normzero = false;
        int j=-345;
        for (int i=0; i<dimension; i++)
        {
            outEdges=0.0;
            if (modeNumber>0) for (j=0; j<dimension; j++) if (i !=j) outEdges+=edgeSet.getVariable(i,j,edgeVariableIndex);
            outEdgeDeficit=1.0-outEdges;
            transferMatrix.set(i,i, 0.0); // no tadpoles unless set later
            switch (modeNumber) {
                 case 3: //"Raw edge values plus normalised restart vector to ensure Markovian."
                    for (j = 0; j < dimension; j++) {
                        transferMatrix.set(j, i, (i == j ? 0 : edgeSet.getVariable(i, j, edgeVariableIndex)) + restartVector[j] * outEdgeDeficit);
                    }
                    break;
                case 2: //"Raw edge values, tadpoles equal to remaining deficit so Markovian."
                    for (j = 0; j < dimension; j++) {
                        if (i != j) {
                            transferMatrix.set(j, i, edgeSet.getVariable(i, j, edgeVariableIndex));
                        }
                    }
                    transferMatrix.set(i, i, outEdgeDeficit);
                    break;
                case 1: //"Normalised edge values (restart vector if deadend), no tadpoles, but Markovian."
                    if (outEdges > 1e-6) {
                        for (j = 0; j < dimension; j++) {
                            if (i != j) {
                                transferMatrix.set(j, i, edgeSet.getVariable(i, j, edgeVariableIndex) / outEdges);
                            }
                        }
                    } else { // dead end so use restart vector
                        for (j = 0; j < dimension; j++) {
                            transferMatrix.set(j, i, restartVector[j]);
                        }
                    }
                    break;

               case 0:
               default://"Raw edge values, no tadpoles, so sub-Markovian."
                    for (j = 0; j < dimension; j++) {
                        if (i != j) {
                            transferMatrix.set(j, i, edgeSet.getVariable(i, j, edgeVariableIndex));
                        }
                    }            
            } // eo switch
        }//eo for i

        // Add small f/dim connection to all elements, and weaken all edges by (1-f)
        // This always ensures a connected transfer matrix
        double connectivityFraction = 1.0/(dimension*dimension); // f
        double edgeFactor=1.0-connectivityFraction;
        double connectivityFactor=connectivityFraction/(dimension-1); // avoid self loops
        double total=0;
        double value=0;
        for (int i=0; i<dimension; i++)
        {
           for (j=0; j<dimension; j++){
               if (i == j) continue;
               value=transferMatrix.getQuick(i, j)*edgeFactor + connectivityFactor;
               transferMatrix.set(i, j, value);
               total+=value;
           }
        }

        int e=checkNonNegative();
        if (e>=0) System.err.println("*** Transfer Matrix is negative at row "+e/dimension+" column "+e%dimension);
        else System.out.println("... Transfer Matrix is non-negative, total of entries "+total);
    }// eo calcTransferMatrix
    
          
    /** Calculate restart vector.
     * <br> Uses normalised site weights to give normalised vector for restarting.  
     * Should the total be zero or the boolean flag is set then just chooses a
     * random site for restart.
     *@param siteArray list of sites 
     *@param randomSite true if want to force use of random sites else  
     */

    private void calcRestartVector(IslandSiteSet siteSet, boolean randomSite)
    {
        restartVectorRandomSites = randomSite;
        double normSite=0.0;
        if (!randomSite) for (int i=0; i<dimension; i++) normSite+=siteSet.getWeight(i);            
        for (int i=0; i<dimension; i++)
        {
            if (normSite>0) restartVector[i]=siteSet.getWeight(i)/normSite;
            else restartVector[i]=1.0/dimension;
        }
            
    } //eo calcRestartVector

        /** Calculate restart vector.
     * <br> Uses normalised site weights to give normalised vector for restarting.  
     * Should the total be zero then just chooses a random site.
     *@param siteArray list of sites 
     */

    private void calcRestartVector(IslandSiteSet siteSet)
    {
        calcRestartVector(siteSet, false);
    } //eo calcRestartVector


// ----------------------------------------------------------------------

    /**
     * Calculates the Eigenvalue decomposition using CERN colt routines.
     * <p>Does not rescale the eigenvalues or matrices.
     * @see {@link IslandNetworks.IslandTransferMatrix.calcEigenvalueDecomposition(double tol, double normaliseFactor)}
     *@param tol tolerance for imaginary part of eigenvalues
     *@return 0 if OK, -ve if problem
     */
private int calcEigenvalueDecomposition(double tol) {
    return calcEigenvalueDecomposition(tol, -99.0);
    }
/**
 * Calculates the Eigenvalue decomposition using CERN colt routines.
 *<br> Sets up Vmatrix pointer but not V inverse.
 *Also sets the tolerance parameter.
 *<br>If A(=transfer matrix) is symmetric, then A = V*D*V' where the eigenvalue matrix D is diagonal 
 *and the eigenvector matrix V is orthogonal. I.e. A = V.mult(D.mult(transpose(V))) 
 *and V.mult(transpose(V)) equals the identity matrix.
 *<br>
 *If A is not symmetric, then the eigenvalue matrix D is block diagonal with the 
 *real eigenvalues in 1-by-1 blocks and any complex eigenvalues, 
 *lambda + i*mu, in 2-by-2 blocks, [lambda, mu; -mu, lambda]. 
 *The columns of V represent the eigenvectors in the sense that A*V = V*D, 
 *i.e. A.mult(V) equals V.mult(D). The matrix V may be badly conditioned, 
 *or even singular, so the validity of the equation A = V*D*inverse(V) 
 *depends upon Algebra.cond(V). 
 *<p>If the ev decomposition failed then evCalcFailed is set true
 * though evalues are set to zero and evcalc is left true (as calc has been performed).
 *<p>If normaliseSCale is positive then normalises eigenvalues and D matrix by this factor
 * divided by the maximum modulus eigenvalue,
 * see {@link IslandNetworks.IslandTransferMatrix#rescaleEigenvalues(double)}.
 *@param tol tolerance for imaginary part of eigenvalues
 *@param normaliseScale if positive normalises eigenvalues by this factor and modulus of maximum, otherwise nothing
 *@return 0 if OK, -ve if problem
 */
private int calcEigenvalueDecomposition(double tol, double normaliseScale) {
    inflcalc=false;
    Vinverse=null;
    Dmatrix=null;
    //Dprimematrix=null;
    absevalues =null;
    tolerance = -88.888888;
    absevalues = new double[dimension];
    try{
        tMED = new EigenvalueDecomposition(transferMatrix);
    } catch (RuntimeException e){
        System.err.println("*** EigenvalueDecomposition has failed, "+e);
        this.evcalc=false;
        this.evCalcFailed=true;
        return -dimension-2;
    }
    evcalc = true;
    evCalcFailed=false;
    tolerance = tol;

    Vmatrix=tMED.getV();
    Dmatrix = tMED.getD(); 
    // check imaginary parts not too big
    int result =0;
    double im=-9.87654321e99;
    double re=-6.54321987e99;
    double maxev=-4.683579e99;
    for (int i=0; i<dimension; i++) {
        im = tMED.getImagEigenvalues().get(i);
        re = tMED.getRealEigenvalues().get(i);
        if (Math.abs(im)>tol) {
            result =-1-i;
            //System.err.println("*** calcEigenvalueDecomposition e/value "+i+" has imaginary e/value "+im);
        }
        absevalues[i] = Math.sqrt(re*re+im*im);
        if (maxev<absevalues[i]) maxev=absevalues[i];
    }
    evOrder = new  TimSort(absevalues,false); // put largest first
    if (rescaleEigenvalues(normaliseScale)<0){
                System.err.println("*** calcEigenvalueDecomposition has failed to normalise, ");
                this.evcalc=false;
                this.evCalcFailed=true;
                return -dimension-dimension-2;
                }
    // check to see if largest eigenvalue is real
    double impartmaxev = getImaginaryPartEigenValue(0);
    if ( Math.abs(impartmaxev) > tolerance) {
                System.err.println("*** calcEigenvalueDecomposition largest eigenvalue has imaginary part "+impartmaxev);
                this.evcalc=false;
                this.evCalcFailed=true;
                return -dimension-dimension-3;
                }
    // now creat prime version of D without laregest evalue
    int maxevindex = evOrder.getIndex(0);
    try{
        if (maxevindex>0 && Dmatrix.getQuick(maxevindex-1, maxevindex)>0 ) throw new RuntimeException("Dmatrix has off diagonal elements");
        if ((maxevindex<(dimension-1)) && (Dmatrix.getQuick(maxevindex, maxevindex+1)>0) ) throw new RuntimeException("Dmatrix has off diagonal elements");
    } catch (RuntimeException eee){
            System.err.println("*** Dprime matrix has failed to initialise, "+eee);
            //Dprimematrix=null;
            this.evcalc=false;
            this.evCalcFailed=true;
            return -dimension-dimension-4;
            }
//     // D is block diagonal so define Dprime
     Dprimematrix=Dmatrix.copy();
     Dprimematrix.setQuick(maxevindex, maxevindex, 0);
     DoubleMatrix2D temp2 =alg.mult(Vmatrix,Dprimematrix);
     if (Vinverse == null) if (calcVinverse()<0) {
         return -1;
     }
     transferprimeMatrix = alg.mult(temp2,Vinverse);

    return result;
}

/**
 * Rescales eigenvalues.
 *<p>If <tt>normaliseScale</tt> parameter is positive
 * then normalises absolute eigenvalues and <tt>Dmatrix</tt> by this factor
 * divided by the maximum modulus eigenvalue,
 * <br><code>normaliseFactor=normaliseScale/|lambda_max|</code>
 * <code>|lambda'[i]|=normaliseFactor*|lambda'[i]|</code>.
 * <br>Note that the
 * eigenvalues stored internally in {@link IslandNetworks.IslandTransferMatrix#tMed}
 * are not rescaled.
 * <p>If normaliseScale is negative, nothing  is done.
 * @param normaliseScale scale used to rescale eigenvalues
 * @return negative if failed, otherwise absolute value of largest eigenvalue after rescaling.
 */
public double rescaleEigenvalues(double normaliseScale){
    normaliseFactor=1.0;
    double maxev = getAbsEigenValue(0);
    if (normaliseScale>0) {
        try{
         // D is block diagonal
         normaliseFactor=normaliseScale/maxev;
         for (int i=0; i<dimension; i++) {
            absevalues[i]*=normaliseFactor;
            for (int j=i-1; j<i+2; j++) {
                if (j<0) continue;
                if (j==dimension) break;
                Dmatrix.setQuick(i,j, Dmatrix.get(i, j)*normaliseFactor);
            }
            maxev*=normaliseFactor;
         }
        } catch (RuntimeException e){
                System.err.println("*** rescaleEigenvalues has failed to normalise, "+e);
                this.evcalc=false;
                this.evCalcFailed=true;
                return -1.0;
                }
    }
    return maxev;
}
/**
     * Calculates the inverse of V, the eigenvector matrix.
     *@return returns flag, negative if a problem occurred.
     */
private int calcVinverse()
     {
    try {
        Vinverse = alg.inverse(Vmatrix);
    }            
    catch(RuntimeException err)
    {
        System.out.println("*** ERROR inverting V in calcVinverse "+err);
        return -1;
    }
    return 0;    
}
    /**
     * Calculates the influence matrix.
     *<p>The influence matrix is <code>I=(1-p)/(1-pT)</code>
     * where <tt>p</tt> is the <tt>influenceProbability</tt> and <tt>T</tt> is 
     * the transfer matrix.  Since this requires the largest
     * eigenvalue to have modulus less than one, this routine will normalise if
     * the absolute value of the largest eigenvalue is bigger than 0.9999.
     *<br>Sets inflcalc true and sets up the influenceMatrix, and decomposition of the
     * transfer matrix, Vinverse and Dmatrix matrices.
     * <p>Note that Dmatrix has block diagonal form for complex eigenvalue pairs and these SO(2)
     * based representations have to be dealt with in slightly more complicated manner.
     *@param inflProb probability of going on one step in random walk
     *@return returns flag, negative if a problem occurred.
     */
public int calcInfluenceMatrix(double inflProb)
     {
    final double tol=1e-4; // internal tolerance
    final double oneminustol=1.0-tol;
    if (( inflProb > 1) || (inflProb<0)) return -2;
    influenceProbability=inflProb;
    double pbar=1-influenceProbability ;
    if (!evcalc) calcEigenvalueDecomposition(tol); // no rescaling here
    if (evCalcFailed) return -2;
    if (Vinverse == null) if (calcVinverse()<0) return -1;
    // now rescale Dmatrix and eigenvalues if largest is bigger than one.
    if(this.getAbsEigenValue(0)>oneminustol) rescaleEigenvalues(oneminustol);
    SparseDoubleMatrix2D inflD =  new SparseDoubleMatrix2D(dimension,dimension);
    int dimensionMinusOne = dimension-1;
    int maxevindex = evOrder.getIndex(0);
    for (int i=0; i<dimension; i++)  {
        //double lambda = influenceProbability*Dmatrix.get(i,i);
        double lbar = (1.0-influenceProbability*Dmatrix.get(i,i)); //lambda);
        if (Math.abs(lbar)> tol)
        {
            double mu = (i==dimensionMinusOne ? 0 : influenceProbability*Dmatrix.get(i,i+1));
            if (mu==0){ // real eigenvalue = Dmatrix.get(i,i)
                inflD.set(i,i,pbar/lbar);
            }
            else{ // deal with 2x2 imaginary eigenvalue pair block
                if (i ==maxevindex) System.err.println("*** in calcInfluenceMatrix, largest eigenvalue has imaginary part");
                double norm = lbar*lbar+mu*mu;
                double diag = pbar*lbar/norm;
                double offdiag = pbar*mu/norm;
                inflD.set(i,i,diag);
                inflD.set(i,i+1,offdiag);
                inflD.set(i+1,i,-offdiag);
                inflD.set(i+1,i+1,diag);
                i++;
            }
        } // eo if lbar>1e-6
        else inflD.set(i,i,1.0);
    }
    // set up influence Dmatrix without largest eigenvalue
    DoubleMatrix2D inflprimeD =  new SparseDoubleMatrix2D(dimension,dimension);
    inflprimeD = inflD.copy();
    inflprimeD.setQuick(maxevindex, maxevindex, 0);
    // now multiply out to get full influence matrix
    DoubleMatrix2D temp2 =alg.mult(Vmatrix,inflD);
    influenceMatrix = alg.mult(temp2,Vinverse);
    DoubleMatrix2D temp3 =alg.mult(Vmatrix,inflprimeD);
    influenceprimeMatrix = alg.mult(temp3,Vinverse);

    inflcalc=true;
    influenceSteps = influenceProbability/(1-influenceProbability);
    influenceValueIn = new double[dimension];
    influenceValueOut = new double[dimension];
    influenceStrengthIn = null;
    int nneg = calcInfluenceMeasures(influenceMatrix,influenceValueIn, influenceValueOut);
    if (nneg>0) System.err.println("!!! Warning:- calcInfluenceMatrix finds "+nneg+" negative entries in influence matrix");

    influenceprimeValueIn = new double[dimension];
    influenceprimeValueOut = new double[dimension];
    influenceprimeStrengthIn = null;
    nneg= calcInfluenceMeasures(influenceprimeMatrix,influenceprimeValueIn, influenceprimeValueOut);
    if (nneg>0) System.err.println("!!! Warning:- calcInfluenceMatrix finds "+nneg+" negative entries in influence prime matrix");

    return 0;
}

/**
 * Sums up the strengths of given matrix.
 * <p>Also zero's negative elements and tests for number of these.
 * @param influenceMatrix
 * @param influenceValueIn
 * @param influenceValueOut
 * @return number of negative elements found
 */
 static public int calcInfluenceMeasures(DoubleMatrix2D influenceMatrix,
         double [] influenceValueIn, double [] influenceValueOut)
     {
    // Now calculate the total weighted influence of each site i.e.
    // inflstrengthin[i] = sum_j  I[i][j] S[j]v[j]
    // inflstrengthout[i] = sum_j  I[j][i]
    // ALSO set negative entries to one.
    int dim=influenceValueIn.length ;
//    influenceStrengthIn = null;
    //influenceValueTotal = new double[dimension] ;
    double v=0;
    double vnegmax=0;
    int nneg=0;
    for (int i=0; i<dim; i++)
    {
            influenceValueIn[i]=0;
            influenceValueOut[i]=0;
            //influenceValueTotal[i]=0;
            for (int j=0; j<dim; j++)
                {
                v=influenceMatrix.get(i,j);
                if (v<0) {
                    vnegmax=Math.min(vnegmax, v);
                    nneg++;
                    influenceMatrix.setQuick(i,j,0);}
                else influenceValueIn[i]+=v;
                v=influenceMatrix.get(j,i);
                if (v>0) influenceValueOut[i]+=v;
            }
    }
    if (nneg>0) System.err.println("!!! Warning:- calcInfluenceMeasures finds "+nneg+" negative entries in influence' matrix, worst is "+String.format("%9.2g",vnegmax));
    return nneg;
    }



///**
// * Current value of the influence probability.
// * @param  p influence probability
// * @return true (false) if probability was (not) set, must be in [0,1]
// */
//    private boolean setInfluenceProbability(double p){
//        if ((p<0) || (p>1)) return false;
//        influenceProbability=p; 
//        return true;
//    }
/**
 * Current value of the influence probability.
 * @return influence probability
 */
    public double getInfluenceProbability(){return influenceProbability;}

//    /**
//     * Calculates the influence strenth vector.
//     *<p>influence strength is the influence matrix <code>I=1/(1-pT)</code>  times the site weight vector <pre>(Sv)_i</pre>
//     * where p is the influenceProbability and T is the transfer matrix.
//     *<br>Calculates the influence matrix if it is not set.
//     *@param inflProb probability of going on one step in random walk
//     * @param siteArray array of IslandSite containing all the site information
//     *@return returns flag, negative if a problem occurred.
//     */
//    public int calcInfluenceStrength(double inflProb, IslandSite[] siteArray) {
//        int res =0;
//        if (!inflcalc) res = calcInfluenceMatrix(inflProb);
//        if (res<0) return res;
//        influenceStrengthIn = new double[dimension];
//        for (int i = 0; i < dimension; i++) {
//            influenceStrengthIn[i] = 0;
//            for (int j = 0; j < dimension; j++) {
//                influenceStrengthIn[i] += influenceMatrix.get(i, j) * siteArray[j].getWeight();
//            }
//        }
//        return 0;
//    }

 // ----------------------------------------------------------------------
     
    /**
     * Returns dimension of the transfer matrix.
     */
    public double getDimension(){return dimension;}

    /**
     * Returns value of the transfer matrix <code>T_{ij}</code>.
     *@param i the target site
     *@param j the source site
     */
public double get(int i, int j)
     {
        double entry=-2.0;
        if ((i<0) || (i>= dimension) ) return(-1.0);
        if ((j<0) || (j>= dimension) ) return(-1.1);
        try {
              entry= transferMatrix.get(i,j);
            } catch (ArithmeticException e)
            {
                entry=-3.0;
            }
       return(entry);
     }

    /**
     * Returns value of the transfer prime matrix <code>T'_{ij}</code>.
     *@param i the target site
     *@param j the source site
     */
public double getPrime(int i, int j)
     {
        double entry=-2.0;
        if ((i<0) || (i>= dimension) ) return(-1.0);
        if ((j<0) || (j>= dimension) ) return(-1.1);
        try {
              entry= transferprimeMatrix.get(i,j);
            } catch (ArithmeticException e)
            {
                entry=-3.0;
            }
       return(entry);
     }

    /**
     * Returns value of the Influence matrix <code>I_{ts}</code>.
     * <p>Does not calculate the influence matrix.
     *@param t the target site
     *@param s the source site
     *@return the entry or -97531 if there is a problem.
     */
public double getInfluence(int t, int s)
     {
        if ((s<0) || (s>= dimension) ) return(-1.0);
        if ((t<0) || (t>= dimension) ) return(-1.1);
        if (inflcalc) return influenceMatrix.get(t,s);
        else  return(DUNSET);
     }

    /**
     * Returns value of the Influence prime matrix <code>I'_{ts}</code>.
     * <p>Does not calculate the influence matrix.
     *@param t the target site
     *@param s the source site
     *@return the entry or -97531 if there is a problem.
     */
public double getInfluencePrime(int t, int s)
     {
        if ((s<0) || (s>= dimension) ) return(-1.0);
        if ((t<0) || (t>= dimension) ) return(-1.1);
        if (inflcalc) return influenceprimeMatrix.get(t,s);
        else  return(DUNSET);
     }

    /**
     * Returns absolute value of the r-th largest eigenvalue using real parts only.
     *@param r rank of eigenvalue required
     */
public double getAbsEigenValue(int r)
     {  
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    int i = evOrder.getIndex(r);
    return absevalues[i];
    }

    /**
     * Returns real value of the r-th largest eigenvalue using real parts only.
     *@param r rank of eigenvalue required
     */
public double getRealPartEigenValue(int r)
     {  
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    int i = evOrder.getIndex(r);
    return tMED.getRealEigenvalues().get(i)*normaliseFactor;
    }
    /**
     * Returns imaginary value of the r-th largest eigenvalue using real parts only.
     *@param r rank of eigenvalue required
     */
public double getImaginaryPartEigenValue(int r)
     {  
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    int i = evOrder.getIndex(r);
    return tMED.getImagEigenvalues().get(i)*normaliseFactor;
    }

    /**
     * Returns the eigenvector of the r-th largest eigenvalue using real parts only.
     *@param r rank of eigenvalue required
     * @return eigenvector
     */
public double[] getEigenVector(int r)
     {
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    int i = evOrder.getIndex(r);
    DoubleMatrix1D ev = Vmatrix.viewColumn(i);
    return ev.toArray();
    }

// ...............................................................
        /** Outputs eigenvalues to a PrintStream
         *@param cc comment characters put at the start of every line
         *@param SepString separation character between columns
         *@param PS printstream
         *@param dec integer number of decimal places to display
         *@param headersOn true if want column and row labels, false if want pure number table
         */
    public void printEigenValueList(String cc, String SepString, PrintStream PS, int dec, boolean headersOn)
    {
        printEigenValueList(cc, SepString, PS, dec, headersOn, true, true);
    } // eo 
    
        /** Outputs eigenvalues to a PrintStream
         *@param cc comment characters put at the start of every line
         *@param SepString separation character between columns
         *@param PS printstream
         *@param dec integer number of decimal places to display
         *@param headersOn true if want column and row labels, false if want pure number table
         * @param absValues show absolute values
         * @param reimValues show real and imaginary values
         */
    public void printEigenValueList(String cc, String SepString, PrintStream PS, int dec, boolean headersOn, boolean absValues, boolean reimValues)
    {
        NumbersToString n2s = new NumbersToString();
        if (headersOn) {
            PS.println("Eigenvalues for transfer matrix  type "+SepString+modeNumber);
            PS.print("Rank");
            if (absValues) PS.print(SepString+"Abs");
            if (reimValues) PS.print(SepString+"Re"+SepString+"Im");
            PS.println();
        }
        for (int i=0; i<dimension; i++) {
            PS.print(i);
            if (absValues) PS.print(SepString+n2s.toString(getAbsEigenValue(i),dec));
            if (reimValues) PS.print(SepString+n2s.toString(getRealPartEigenValue(i),dec)+SepString+n2s.toString(getImaginaryPartEigenValue(i),dec));
            PS.println();      
        }
    } // eo 
    
        /** Outputs TransferMatrix to a PrintStream
         *@param headersOn true if want column and row labels, false if want pure number table
         *@param cc comment characters put at the start of every line
         *@param SepString separation character between columns
         *@param PS printstream
         *@param dec integer number of decimal places to display
         */
    public void printTransferMatrix(String cc, String SepString, PrintStream PS, int dec, boolean headersOn)
    {
        NumbersToString n2s = new NumbersToString();
        if (headersOn) {
            PS.println("Transfer Matrix  type "+SepString+modeNumber);
            PS.print("To/From"+SepString);
            for (int i=0; i<dimension; i++) PS.print(i+SepString);
            PS.println();
        }
        for (int i=0; i<dimension; i++) {
            if (headersOn) PS.print(i+SepString);
            for (int j=0; j<dimension; j++) {
                PS.print(n2s.toString(transferMatrix.get(i,j),dec)+SepString);
            }
            PS.println();
        }

    } // eo showTransferMatrix

    /** Outputs TransferMatrix to a PrintStream
         *@param cc comment characters put at the start of every line
         *@param SepString separation character between columns
         *@param PS printstream
         *@param dec integer number of decimal places to display
         *@param headersOn true if want column and row labels, false if want pure number table
         */
    public void printInfluenceMatrix(String cc, String SepString, PrintStream PS, int dec, boolean headersOn)
    {
        if (!inflcalc) 
        { 
            if (headersOn) PS.println("Influence Matrix not calculated."); 
            return;
        }
        NumbersToString n2s = new NumbersToString();
        if (headersOn) {
            PS.println("Influence Matrix probability "+SepString+influenceProbability+SepString+", steps "+SepString+influenceSteps+SepString+" from transfer matrix type "+modeNumber);
            PS.print("To/From"+SepString);
            for (int i=0; i<dimension; i++) PS.print(i+SepString);
            PS.println();
        }
        for (int i=0; i<dimension; i++) {
            if (headersOn) PS.print(i+SepString);
            for (int j=0; j<dimension; j++) PS.print(n2s.toString(influenceMatrix.get(i,j),dec)+SepString);
            PS.println();
        }
    } // eo showInfluenceMatrix
    
    /**
     * Returns string showing type of transfer matrix.
     */
          public String typeString() {          
          String s="Unset";
              switch(modeNumber)
              {
                  case 3: s="Raw edge values plus normalised restart vector to ensure markovian.";    
                  break;
                  case 2: s="Raw edge values, tadpoles equal to remaining deficit so markovian.";    
                  break;
                  case 1: s="Normalised edge values (restart vector if deadend), no tadpoles but markovian.";    
                  break;
                  case 0: s="Raw edge values, no tadpoles, non-markovian.";
                  break;
                  default: s="Unknown.";    
              }
              return s;
          }// eo   typeString
// ...............................................................
             
    /**
     * Checks the eigenvalue decomposition.
     *@param tol tolerance
     *@return returns flag, negative if a problem occurred -1=no V inverse, -3 = failed check.
     */
public int checkDecomposition(double tol)
     {
    if (tol<0) return -4;
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    if (Vinverse == null) if (calcVinverse()<0) return -1;
    DoubleMatrix2D test;
    try
    {
    test = alg.mult(alg.mult(Vmatrix,Dmatrix),Vinverse); 
    }
    catch(RuntimeException err)
    {
        System.out.println("*** ERROR "+err+"in checkDecomposition ");
        return -2;
    }
    int r=0;
    double [][] dif = new double[dimension][dimension];
    for (int i=0; i<dimension; i++)  for (int j=0; j<dimension; j++) {
        dif[i][j]=Math.abs(test.get(i,j)-transferMatrix.get(i,j));
        if(dif[i][j]>tol) r=-3;
    }
    return r;
    }// eo checkDecomposition
 
    
     /** 
         * Checks Markovian nature to given tolerance.
         *@param tol tolerance
         *@return number of first column which fails to be 1 +- tolerance else -1
         */
    public int checkMarkovian(double tol)
    {
        double colTotal=-99;
        for (int j=0; j<dimension; j++) 
        {
            colTotal=transferMatrix.viewColumn(j).zSum();
            if (Math.abs(colTotal-1)>tol) return j;
        }
        return -1;
    }
    
     /** 
         * Checks to see if a non-negative matrix.
         *@return number of first entru which fails to be non-negative, otherwise -1
         */
    public int checkNonNegative()
    {
        for (int i=0; i<dimension; i++) 
        {
            for (int j=0; j<dimension; j++) if (transferMatrix.getQuick(i, j)<0.0) return (i*dimension+j);
        } 
        return -1;
    }
    

    
    /** 
     * Checks influence matrix is SubMarkovian to given tolerance.
     * <p>That is makes sure columns sum to somewhere between one and zero
     *@param tol tolerance
     *@return number of first column which fails to be 1 +- tolerance else -1
     */
    public int checkInfluenceSubMarkovian(double tol) {
        double colTotal = -99;
        for (int j = 0; j < dimension; j++) {
            colTotal = influenceMatrix.viewColumn(j).zSum();
            if (((colTotal - 1) > tol) || (colTotal < -tol)) {
                return j;
            }
        }
        return -1;
    }
    
    /** 
     * Checks influence matrix is non-negative nature to given tolerance.
     * <p>That is makes sure columns sum to somewhere between one and zero
     *@param tol tolerance
     *@return number (row*dimension+column) which is < - tol
     */
    public int checkInfluenceNonNegative(double tol) {
        for (int i = 0; i < dimension; i++) 
            for (int j = 0; j < dimension; j++) 
                if (influenceMatrix.get(i, j)<-tol) return i*dimension+j;
        return -1;
    }
    
}
