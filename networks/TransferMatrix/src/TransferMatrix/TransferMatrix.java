/*
 * TransferMatrix.java
 *
 * Created on 06 March 2007, 13:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TransferMatrix;

import java.io.*;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.colt.matrix.linalg.Algebra;

import TimUtilities.NumbersToString;
//import TimUtilities.TimMessage;


/**
 *
 * @author time
 */
public class TransferMatrix {
    double tolerance = 1e-6;
    
    DenseDoubleMatrix2D tmat; // transfer matrix
    int dim=-1; // dimension of matrix, negative if not created
    double invDim=-99; // = 1/dim
    
    EigenvalueDecomposition ev; // eigenvalues and eigenvectors
    DenseDoubleMatrix1D eValueList; // eigenvalue list
    SortDoubleMatrix1D  evOrder ; // gives index of eigenvalues from smallest upwards
    DenseDoubleMatrix2D eVectorMatrix; // eigenvector list
    DenseDoubleMatrix2D eVectorMatrixInv; // eigenvector list
    Algebra A;
    
    DenseDoubleMatrix2D influenceMatrix ; //  \sum_n  (pT)^n
    double influenceProbability=-1;
    DenseDoubleMatrix2D timingMatrix; // \sum_t t (pT)^t
    double timingProbability=-1;
    
    /** Creates a new instance of TransferMatrix of dimension (dim)^2. 
     *@param dimension dimension.
     */
    public TransferMatrix(int dimension) {
        dim=dimension;
        tmat = new DenseDoubleMatrix2D (dim,dim);
    }
    
    /** Creates a new instance of TransferMatrix using input array. 
     *@param inputArray square array of doubles.  If not square dim=-2 set.
     */
    public TransferMatrix(double [][] inputArray) {
        tmat = new DenseDoubleMatrix2D (inputArray);
        if (tmat.columns() == tmat.rows()) dim=tmat.rows(); else dim = -2;
        invDim = 1.0/((double)dim);
        
    }
    
    /** Ensure TransferMatrix is Markovian.
     * @return true if can be nromalised to satisfy Markovian properties else false
     */
    public boolean normaliseTransferMatrix() {
        boolean result = true;
        double colsum=-999;
        for (int j=0; j<dim; j++) 
         {  colsum=0; 
            for (int i=0; i<dim; i++) colsum+=tmat.get(i,j);
            if (colsum>tolerance){ // normalise columns so sum to one
             for (int i=0; i<dim; i++) tmat.set(i,j,tmat.get(i,j)/colsum);                
            }
            else
            {
                if (colsum<-tolerance) result=false;
                  for (int i=0; i<dim; i++) tmat.set(i,j,0.0); // kill walks at dead ends
            }
        }
        return result;
    }


    /** Calculate Eigenvector decomposition. 
     */
    public void calcEigenVectors() {
        ev= new EigenvalueDecomposition(tmat);
        eValueList = new DenseDoubleMatrix1D(dim);
        eValueList.assign(ev.getRealEigenvalues());
        evOrder = new SortDoubleMatrix1D(eValueList);  
        eVectorMatrix = new DenseDoubleMatrix2D(dim,dim); // Matrix B where T= B Lambda B^{-1} columns are the eigenvectors
        eVectorMatrix.assign(ev.getV());
        eVectorMatrixInv = new DenseDoubleMatrix2D(dim,dim); // Matrix B^{-1}
        A = new Algebra(); // uses default tolerance
        eVectorMatrixInv.assign(A.inverse(eVectorMatrix));
        
    }
    
    /** Calculate Influence Matrix. 
     *<br> Influence Matrix = \sum_n (pT)^n = 1/(1-pT)
     *@param p probability of continuing at each step
     */
    public void calcInfluenceMatrix(double p) {
        if (ev == null) calcEigenVectors();
         influenceMatrix = new DenseDoubleMatrix2D (dim,dim);
         DenseDoubleMatrix2D temp = new DenseDoubleMatrix2D (dim,dim);
         temp.assign(eVectorMatrixInv);
         double ddd=-99;
         for (int i=0; i<dim; i++) 
         {
             ddd = (1.0-p*eValueList.get(i)) ;
             for (int j=0; j<dim; j++) temp.set(i,j,eVectorMatrixInv.get(i,j)/ddd );
         }
         influenceMatrix.assign(A.mult(eVectorMatrix,temp));
         influenceProbability=p;
    }
    
    /** Calculate Timing Matrix. 
     *<br> Timing Matrix = \sum_t t (pT)^t = (pT)/(1-pT)^2
     *@param p probability of continuing at each step
     */
    public void calcTimingMatrix(double p) {
        if (ev == null) calcEigenVectors();
         timingMatrix = new DenseDoubleMatrix2D (dim,dim);
         DenseDoubleMatrix2D temp = new DenseDoubleMatrix2D (dim,dim);
         temp.assign(eVectorMatrixInv);
         double ddd=-99;
         double lambda=-99;
         double lbar=-99;
         for (int i=0; i<dim; i++) 
         {
             lambda = p*eValueList.get(i);
             lbar=1.0-lambda;
             ddd = lambda/ (lbar*lbar) ;
             for (int j=0; j<dim; j++) temp.set(i,j,ddd*eVectorMatrixInv.get(i,j));
         }
         timingMatrix.assign(A.mult(eVectorMatrix,temp));
         timingProbability=p;
    }

     /** Gives eigenvector of largest eigenvalue as the ranking, normalised. 
      *@param ranking double array ready to accept largest eigenvalue normalised
     */
    public void getRanking(double [] ranking) {
        if (ev == null) calcEigenVectors();
         int largestIndex = evOrder.getIndex(0);
         double norm=0;
         for (int j=0; j<dim; j++) { norm+=eVectorMatrix.get(j,largestIndex);  }
         for (int j=0; j<dim; j++) { ranking[j] = eVectorMatrix.get(j,largestIndex) /norm; }
    }



    // ***************************************************************
        /** Prints Transfer Matrix to PrintStream.
         *@param PS printstream
         *@param cc comment characters put at the start of every line, if equal NUMBERS then no comments, just numbers
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printTransferMatrix(PrintStream PS, String cc, String SepString, int dec)
    {
        NumbersToString n2s = new NumbersToString();
        if (cc!="NUMBERS") PS.println(cc+ "Transfer Matrix ");
        //PS.println(tmat.toString());
        PrintMatrix2D pm = new PrintMatrix2D();
        pm.printMatrix(tmat, PS,   SepString,  dec);
    } // eo printTransferMatrix
    
// ...........................................................................
        /** Prints the Transfer Matrix on std output.
         *@param cc comment characters put at the start of every line
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printTransferMatrix(String cc,  String SepString, int dec)
    {
        printTransferMatrix(System.out, cc, SepString, dec);
    }

 // ...........................................................................
        /** Prints one eigenvector.
         *@param index eigenvector index
         *@param PS printstream
         *@param cc comment characters put at the start of every line
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printEigenVector(int index, PrintStream PS,  String cc,  String SepString, int dec)
    {
           PrintMatrix2D pm = new PrintMatrix2D();
           pm.printMatrixRow(index, eVectorMatrix, PS,   SepString,  dec);
    }

     // ...........................................................................
        /** Prints eigenvalues and eigenvectors.
         *@param PS printstream
         *@param cc comment characters put at the start of every line
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printEigenInformation(PrintStream PS,  String cc,  String SepString, int dec)
    {
           PrintMatrix2D pm = new PrintMatrix2D();
           PS.print(cc+"e/value" + SepString);
           for (int i=0; i<dim; i++) PS.print(i + SepString);
           PS.println();
           for (int i=0; i<dim; i++)
           {
               PS.print(eValueList.get(i) + SepString);
               pm.printMatrixColumn(i, eVectorMatrix, PS,   SepString,  dec);
           }
    }

}
