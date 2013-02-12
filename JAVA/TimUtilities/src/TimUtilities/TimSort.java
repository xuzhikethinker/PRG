/*
 * TimSort.java
 *
 * Created on 06 March 2007, 14:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

/**
 * Sorts Vectors.
 * <br>Given a list of values this finds and stores the rank of the entries.  
 * It does not change the order of the entries in the given vector.
 * Class does the sorting using a quick sort algorithm.
 * It may be better implemented using the Collections classes of java.
 * @author time
 */
public class TimSort {
    /**
     * Ranking Order vector.
     * <br><tt>orderVector[r]</tt> is the index of input vector ranked <tt>r</tt>-th
     * <br>Access this from the <tt>getIndex</tt> method.
     */
    int [] orderVector; // Ranking Order orderVector[r] = index of vector ranked r-th        
    
    /** Creates a new instance of TimSort.
     * <p>Rank runs 0 (smallest) to (dim-1) (largest).     
     * @param valueVector is vector of double values to be ordered
     */
    public TimSort(double [] valueVector) {
        calcVectorOrderSmallestFirst(valueVector);
    }

    /** Creates a new instance of TimSort.
     * <p>Rank runs 0 (smallest) to (dim-1) (largest) if smallestFirst is true.
     * Otherwise ranked     0 (largest) to (dim-1) (smallest) 
     * @param valueVector is vector of double values to be ordered
     *@param smallestFirst true if smallest first, false if largest first
     */
    public TimSort(double [] valueVector, boolean smallestFirst) {
        if (smallestFirst) calcVectorOrderSmallestFirst(valueVector);
        else calcVectorOrderLargestFirst(valueVector);
    }
    
   /** Produces a list of the rank (order) of a vector of values.
     *  <br> valueVector[orderVector[r]] is value ranked r-th.
     * Rank runs 0 (smallest) to (dim-1) (largest).     
     * @param valueVector is vector of double values to be ordered
     */
    public void calcVectorOrderSmallestFirst(double [] valueVector)
    {
         int n = valueVector.length;
         orderVector = new int[n];
         int temp=-99999;

// Now calculate Ranking Order orderVector[r] = index of vector ranked r-th                
         for (int i=0; i<n; i++) orderVector[i]=i;
         for (int i=0; i<n; i++) 
            {
                double best = valueVector[orderVector[i]];
                for (int j=i+1; j<n; j++)
                {
                 double newbest = valueVector[orderVector[j]];
                 if (best > newbest)  
                 {
                     best = newbest;
                     temp=orderVector[j];
                     orderVector[j] = orderVector[i];
                     orderVector[i]=temp;
                 }
                }//eo for j                
            }//eo for i
    }

   /** Produces a list of the rank (order) of a vector of values.
     *  <br> valueVector[orderVector[r]] is value ranked r-th.
     * Rank runs 0 (largest) to (dim-1) (smallest).     
     * @param valueVector is vector of double values to be ordered
     */
    public void calcVectorOrderLargestFirst(double [] valueVector)
    {
         int n = valueVector.length;
         orderVector = new int[n];
         int temp=-99999;

// Now calculate Ranking Order orderVector[r] = index of vector ranked r-th                
         for (int i=0; i<n; i++) orderVector[i]=i;
         for (int i=0; i<n; i++) 
            {
                double best = valueVector[orderVector[i]];
                for (int j=i+1; j<n; j++)
                {
                 double newbest = valueVector[orderVector[j]];
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
    
      // ...................................................................

/*
 * @(#)QSortAlgorithm.java  1.3   29 Feb 1996 James Gosling
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
  */

/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @version     @(#)QSortAlgorithm.java 1.3, 29 Feb 1996
 */

// Adapted to sort index array
    
    //public class QSortAlgorithm extends SortAlgorithm {
   /** This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    *   QuickSort(a, 0, a.length - 1);
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition
    * @param hi0     right boundary of array partition
    */

  
//   public void sort(int a[]) throws Exception
//   {
//      QuickSort(a, 0, a.length - 1);
//   }
//}end of sort

    /** 
    * Sorts integer array.
    *@param criteria used to select condition to apply to array as in compareWeights(criteria)
    *@param a returned as list of indices of edgeweightlist in ranked order.
    *@param lo0 the starting index
    *@param hi0 the maximum index (inclusive)
    */      
    private void QuickSort(int criteria, int a[], int lo0, int hi0)
   {
      int T;
      int lo = lo0;
      int hi = hi0;
      int midindex;
      double mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = a[ ( lo0 + hi0 ) / 2 ];
         midindex = ( lo0 + hi0 ) / 2 ;

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
             while( ( lo < hi0 ) && ( a[lo] < mid ) ) 
               ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
            while( ( hi > lo0 ) && ( a[hi] > mid ) )
               --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            { // swap elements
               T = a[lo]; 
               a[lo] = a[hi];
               a[hi] = T;
               
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            QuickSort(criteria, a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            QuickSort(criteria, a, lo, hi0 );

      }
   }



}
