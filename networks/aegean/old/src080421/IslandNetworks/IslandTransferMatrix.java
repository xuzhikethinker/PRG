/*
 * IslandTransferMatrix.java
 *
 * Created on 27 May 2007, 18:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;


import IslandNetworks.Edge.IslandEdgeSet;
import java.io.*;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.colt.matrix.linalg.Algebra;

import TimUtilities.NumbersToString;
import TimUtilities.TimSort;


/**
 * Sets up a transfer matrix based on the edge values and site weights of an Island Network.
 *<br>
 * It can also calculate the eigenvalues, eigenvectors, diagonal decomposition where T=V.D.V^{-1}.
 * V is has the eigenvectors as columns while D is diagoanl with eigenvalue entries.
 * The influence matrix is the diffusion matrix where there is a probability p of continuing.
 * @author time
 */
public class IslandTransferMatrix {
    int modeNumber=0;
    int dimension=-1; 
// Care: transferMatrix[i][j] is from j to i as in normal matrix language
    Algebra alg;    
    DenseDoubleMatrix2D transferMatrix;
    double[] restartVector;
    boolean restartVectorRandomSites = false; // true if forcing random sites for restart vector, else will be site weights.
    
    // following are not defined if evcalc=false, call calc
    boolean evcalc = false;
        EigenvalueDecomposition tMED;
        DoubleMatrix2D Vmatrix;
        DoubleMatrix2D Dmatrix;
   private double [] absevalues; // absolute value of eigenvalues
        TimSort evOrder;
        double tolerance = -99.0;
        
    
    
    // following are not defined if inflcalc=false
    boolean inflcalc =false;
    double influenceProbability=-97531.0;
    double influenceSteps = -8642.0; //# steps
    DoubleMatrix2D influenceMatrix;
    DoubleMatrix2D Vinverse;
    double[] influenceStrengthIn;
    double[] influenceStrengthOut;
    //double[] influenceStrengthTotal;

                
        
//    /** Creates a new instance of IslandTransferMatrix */
//    public IslandTransferMatrix(){
//    }
        
    /** Sets up a new instance of IslandTransferMatrix.
     *@param mode mode number as specified in the showTransferMatrixType routine
     *@param numberSites number of sites
     *@param siteArray array of sites
     *@param edgeSet set of all edges
     */
    public IslandTransferMatrix(int mode, int numberSites, IslandSite [] siteArray, IslandEdgeSet edgeSet)
          {
           alg = new Algebra();
           calcTransferMatrix(mode, numberSites, siteArray, edgeSet);
    }
    
    /** Deep Copy of IslandTransferMatrix */
    public IslandTransferMatrix(IslandTransferMatrix old ){
                   alg = new Algebra();
        modeNumber=old.modeNumber;
        dimension=old.dimension; 
        transferMatrix = (DenseDoubleMatrix2D) old.transferMatrix.copy();  // deep copy
        //transferMatrix = new DenseDoubleMatrix2D(dimension,dimension);
        restartVector = new double [dimension];
        for (int i=0; i<dimension; i++) 
        {
            restartVector[i]=old.restartVector[i];
//            for (int j=0; j<dimension; j++)  transferMatrix[j][i]=old.transferMatrix[j][i];
        }
        
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
    private void calcTransferMatrix(int mode, int numberSites, IslandSite [] siteArray, IslandEdgeSet edgeSet)
          {
           modeNumber=mode;
           dimension= numberSites;
           // Care: transferMatrix[i][j] is from j to i as in normal matrix language
           transferMatrix = new DenseDoubleMatrix2D(dimension,dimension);
           restartVector = new double [dimension];
           calcRestartVector(siteArray);
           transferMatrix = new DenseDoubleMatrix2D(dimension,dimension);
           calcTransferMatrix(edgeSet);
    }

    /** Calculate some Transfer Matrix for diffusion analysis.
     *<br> For probMethod = 0 or 1 will include the tadpole edge from site to itself.
     *@param edgeSet set of all edges
     */

    private void calcTransferMatrix(IslandEdgeSet edgeSet)
    {
        // calculate transfer matrix from site i to site j matrix element transferMatrix[j][i]
        double outEdges= -99.0;
        double outEdgeDeficit=-876;
        boolean normzero = false;
        int j=-345;
            for (int i=0; i<dimension; i++)
        {
            outEdges=0.0;
            if (modeNumber>0) for (j=0; j<dimension; j++) if (i !=j) outEdges+=edgeSet.getEdgeValue(i,j);
            outEdgeDeficit=1.0-outEdges;
            
            switch (modeNumber) 
            {
                case 3: //"Raw edge values plus normalised restar vector to ensure normalisation."
                    for (j=0; j<dimension; j++) transferMatrix.set(j,i, (i==j?0:edgeSet.getEdgeValue(i,j)) + restartVector[j]*outEdgeDeficit);
                    break;
                case 2: //"Raw edge values, tadpoles equal to remaining deficit so normalised."
                    for (j=0; j<dimension; j++) if (i!=j) transferMatrix.set(j,i, edgeSet.getEdgeValue(i,j));
                    transferMatrix.set(j,i, 1-outEdges);
                    break;
                case 1: //"Normalised edge values (restart vector if deadend), no tadpoles."
                    if (outEdges>1e-6)  for (j=0; j<dimension; j++) if (i!=j) transferMatrix.set(j,i, edgeSet.getEdgeValue(i,j)/outEdges);
                else for (j=0; j<dimension; j++) transferMatrix.set(j,i, restartVector[j]);
                    break;                    
                
                case 0: //"Raw edge values, no tadpoles, unnormalised."
                    for (j=0; j<dimension; j++) if (i!=j) transferMatrix.set(j,i, edgeSet.getEdgeValue(i,j));
                    default: 
            }
            for (j=0; j<dimension; j++) 
            {
                transferMatrix.set(j,i, edgeSet.getEdgeValue(i,j)/outEdges);
            }
                

        }//eo for i

        
    }// eo calcTransferMatrix
    
          
    /** Calculate restart vector.
     * <br> Uses normalised site weights to give normalised vector for restarting.  
     * Should the total be zero or the boolean flag is set then just choses a 
     * random site for restart.
     *@param siteArray list of sites 
     *@param randomSite true if want to force use of random sites else  
     */

    private void calcRestartVector(IslandSite [] siteArray, boolean randomSite)
    {
        restartVectorRandomSites = randomSite;
        double normSite=0.0;
        if (!randomSite) for (int i=0; i<dimension; i++) normSite+=siteArray[i].getWeight();            
        for (int i=0; i<dimension; i++)
        {
            if (normSite>0) restartVector[i]=siteArray[i].getWeight()/normSite;
            else restartVector[i]=1.0/dimension;
        }
            
    } //eo calcRestartVector

        /** Calculate restart vector.
     * <br> Uses normalised site weights to give normalised vector for restarting.  
     * Should the total be zero then just choses a random site.
     *@param siteArray list of sites 
     */

    private void calcRestartVector(IslandSite [] siteArray)
    {
        calcRestartVector(siteArray, false);
    } //eo calcRestartVector


// ----------------------------------------------------------------------
     
 
/*
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
 *
 *@param tol tolerance for imaginary part of eigenvalues
 *@return 0 if )K, -1 if imaginary part of eigenvalues exceed tolerance
 */
private int calcEigenvalueDecomposition(double tol) {
    inflcalc=false;
    Vinverse=null;
    Dmatrix=null;
    absevalues =null;
    tolerance = -88.0;
    tMED = new EigenvalueDecomposition(transferMatrix);
    Vmatrix=tMED.getV();
    Dmatrix = tMED.getD(); 
    // check imaginary parts not too big
    int result =0;
    absevalues = new double[dimension];
    double im=-987;
    double re=-654;
    for (int i=0; i<dimension; i++) {
        im = tMED.getImagEigenvalues().get(i);
        re = tMED.getRealEigenvalues().get(i);
        if (Math.abs(im)<tol) result =-1-i;
        absevalues[i] = Math.sqrt(re*re+im*im);
    }
    evOrder = new  TimSort(absevalues,false); // put largest first
    evcalc = true;
    tolerance = tol;
    //if (result<0) System.out.println("!!! WARNING Imaginary eigenvalue number "+(1-result));
    return result;
}

/*
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
        System.out.println("*** ERROR "+err+"in calcInfluenceMatrix ");
        return -1;
    }
    return 0;    
}
    /*
     * Calculates the influence matrix.
     *<br>Sets inflcalc true and sets up the influencematrix, Vinverse and Dmatrix matrices.
     *@param inflProb probability of going on one step in random walk
     *@return returns flag, negative if a problem occurred.
     */
public int calcInfluenceMatrix(double inflProb)
     {
    if (( inflProb > 0.999999) || (inflProb<0)) return -2;
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    if (Vinverse == null) if (calcVinverse()<0) return -1;
    SparseDoubleMatrix2D inflD =  new SparseDoubleMatrix2D(dimension,dimension);
    for (int i=0; i<dimension; i++)  inflD.set(i,i,1.0/(1-inflProb*Dmatrix.get(i,i)));
    DoubleMatrix2D temp2 =alg.mult(Vmatrix,inflD);
    influenceMatrix = alg.mult(temp2,Vinverse); 
            
    //return ev.toArray();
    inflcalc=true;
    influenceProbability = inflProb;
    influenceSteps = influenceProbability/(1-influenceProbability);
    // Now calculate the total weighted influence of each site i.e.
    // inflstrengthin[i] = sum_j  I[i][j] S[j]v[j]  
    // inflstrengthout[i] = sum_j  I[j][i] 
    influenceStrengthIn = new double[dimension] ;
    influenceStrengthOut = new double[dimension] ;
    //influenceStrengthTotal = new double[dimension] ;
for (int i=0; i<dimension; i++)  
{
        influenceStrengthIn[i]=0;
        influenceStrengthOut[i]=0;
        //influenceStrengthTotal[i]=0;
        for (int j=0; j<dimension; j++)
            {
        influenceStrengthIn[i]+=influenceMatrix.get(i,j);
        influenceStrengthOut[i]+=influenceMatrix.get(j,i);
        //influenceStrengthTotal[i]=0;
    }
    
}  
    return 0;
    }
 // ----------------------------------------------------------------------
     
    /*
     * Returns value of the transfer matrix T_{ij}.
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
            };
       return(entry);
     }

    /*
     * Returns value of the Influence matrix I_{ij}.
     *@param i the target site
     *@param j the source site
     *@return the entry or -97531 if there is a problem.
     */
public double getInfluence(int i, int j)
     {
    //if (!inflcalc) 
        double entry=-2.0;
        if ((i<0) || (i>= dimension) ) return(-1.0);
        if ((j<0) || (j>= dimension) ) return(-1.1);
        if (inflcalc) return influenceMatrix.get(i,j);
        else  return(-97531);
     }

    /*
     * Returns absolute value of the n-th largest eigenvalue using real parts only.
     *@param n rank of eigenvalue required
     */
public double getAbsEigenValue(int n)
     {  
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    int i = evOrder.getIndex(n);
    return absevalues[i];
    }
 
    /*
     * Returns the eigenvector of the n-th largest eigenvalue using real parts only.
     *@param n rank of eigenvalue required
     */
public double[] getEigenVector(int n)
     {
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    int i = evOrder.getIndex(n);
    DoubleMatrix1D ev = Vmatrix.viewColumn(i);
    return ev.toArray();
    }

// ...............................................................
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
         *@param headersOn true if want column and row labels, false if want pure number table
         *@param cc comment characters put at the start of every line
         *@param SepString separation character between columns
         *@param PS printstream
         *@param dec integer number of decimal places to display
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
    
    /*
           * Returns string showing type of transfer matrix.
      */
          public String typeString() {          
          String s="Unset";
              switch(modeNumber)
              {
                  case 3: s="Raw edge values plus normalised restar vector to ensure normalisation.";    
                  break;
                  case 2: s="Raw edge values, tadpoles equal to remaining deficit so normalised.";    
                  break;
                  case 1: s="Normalised edge values (restart vector if deadend), no tadpoles.";    
                  break;
                  case 0: s="Raw edge values, no tadpoles, unnormalised.";
                  break;
                  default: s="Unknown.";    
              }
              return s;
          }// eo   typeString
// ...............................................................
             
    /*
     * Checks the eigenvalue decomposition.
     *@param tol tolerance
     *@return returns flag, negative if a problem occurred -1=no V inverse, -3 = failed check.
     */
public int checkDecomposition(double tol)
     {
    if (tol<0) return -4;
    DoubleMatrix2D test;
    if (!evcalc) calcEigenvalueDecomposition(1e-3);
    if (Vinverse == null) if (calcVinverse()<0) return -1;
    try
    {
    test = alg.mult(alg.mult(Vmatrix,Dmatrix),Vinverse); 
    }
    catch(RuntimeException err)
    {
        System.out.println("*** ERROR "+err+"in checkDecomposition ");
        return -2;
    }

    for (int i=0; i<dimension; i++)  for (int j=0; j<dimension; j++) if(Math.abs(test.get(i,j)-transferMatrix.get(i,i))<tol) return -3;  
    return 0;
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
    
    
}
