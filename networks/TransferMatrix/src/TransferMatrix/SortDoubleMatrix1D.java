/*
 * SortDoubleMatrix1D.java
 *
 * Created on 06 March 2007, 15:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TransferMatrix;

import cern.colt.matrix.DoubleMatrix1D;

/**
 * Sorts Vectors.
 * @author time
 */
public class SortDoubleMatrix1D {
    
    int [] orderVector; // Ranking Order orderVector[r] = index of vector ranked r-th        
    
    /** Creates a new instance of SortDoubleMatrix1D.
     * Rank runs 0 (largest) to (dim-1) (smallest).      
     * @param valueVector is vector of double values to be ordered
     */
    public SortDoubleMatrix1D(DoubleMatrix1D  valueVector) {
        calcVectorOrder(valueVector);
    }
    
   /** Produces a list of the rank (order) of a vector of values.
     *  <br> valueVector[orderVector[r]] is value ranked r-th.
     * Rank runs 0 (largest) to (dim-1) (smallest).     
     * @param valueVector is vector of double values to be ordered
     */
    public void calcVectorOrder(DoubleMatrix1D  valueVector)
    {
         int n = valueVector.size();
         orderVector = new int[n];
         int temp=-99999;

// Now calculate Ranking Order orderVector[r] = index of vector ranked r-th                
         for (int i=0; i<n; i++) orderVector[i]=i;
         for (int i=0; i<n; i++) 
            {
                double best = valueVector.get(orderVector[i] );
                for (int j=i+1; j<n; j++)
                {
                 double newbest = valueVector.get(orderVector[j] );
                 if (best < newbest)  
                 {
                     best = newbest;
                     temp=orderVector[j];
                     orderVector[j] = orderVector[i];
                     orderVector[i]=temp;
                 }
                }//eo for j                
            }//eo for i
    }

    // **********************************************************    
   /** Gets index of entry of requested rank.
    *@param rank the rank requested  
    *@return index of vector of required rank
    */ 
    public int getIndex(int rank)
    {
        return orderVector[rank];
    }
}

