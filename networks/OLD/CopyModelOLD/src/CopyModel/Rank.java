/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CopyModel;

//import java.util.ArrayList;
import java.io.PrintStream;
     
    import cern.colt.list.IntArrayList;

/**
 * Keep a list of the top Y objects ranked by some value. 
 * <p>Currently the values are given by the a degrees in an array of Vertex.
 * Ranking runs from 0 (largest) to (Y-1) (smallest).
 * For an object to move up the ranking it must exceed (not just equal) the value objects of higher ranking.
 * @author time
 */
public class Rank {
    
    /**
     * Use to indicate an object out of the top Y, value = {@value}.
     * Must be negative.
     */
    public final static int UNRANKED = -123456;
//    /**
//     * Value of integer not set.
//     */
//    public final static int IUNSET = -654321;
    
    /**
     * Rank the top Y artefacts
     */
    public final int Y;
    /**
     * Index of lowest ranked value
     */
    private final int Yminus1;
    private final int Yminus2;
    /**
     * The current minimum value of objects in the top Y list.        
     */
    private int minValue = UNRANKED;
        
    /**
     * The number of vertices.        
     */
    private int numberVertices = UNRANKED;
        
    /**
     * <code>rankIndex[r]</code> is the index in array of values of the object ranked r.
     */
    private IntArrayList rankIndex;

    /**
     * <code>delta[y]</code> is the turnover in the top y list since last update.
     */
    private  int [] delta;
    
    
    /**
     * Initialises Ranking.
     * @param Yinput maximum size of turnover list to conisder.
     * @param numberVerticesInput number of vertices.
     * @param vertex array of vertices
     */
    public Rank(int Yinput, int numberVerticesInput, Vertex [] vertex){
        numberVertices = numberVerticesInput; 
        int Ytemp=Yinput; 
        if (Ytemp<=0) {
            System.out.println("!!! Requested length "+Yinput+" too short, no ranking initialised");          
            Ytemp=0;
        }
        if (numberVertices<Ytemp) {
            System.out.println("!!! Requested length "+Yinput+" too long, vertex array is only length "+numberVertices);
            Ytemp=numberVertices;
        }
        Y=Ytemp;
        Yminus1=Y-1; 
        Yminus2=Y-2;
        
        delta=new int[Y];
        rankIndex = new IntArrayList(Y);
        for (int a=0; a<numberVertices;a++) 
           if (a<Y) {
               vertex[a].rank=a;
               rankIndex.add(a);
           } 
           else vertex[a].rank=UNRANKED;
        //QuickSort(vertex); // make sure the initial list of Y vertices is at least ordered within itself.
        //minValue=UNRANKED;  // this forces initialisation
        //minValue =  vertex[rankIndex.getQuick(Yminus1)].degree;
    }

    
    /**
     * Update ranking and turnover.
     * @param numberVerticesInput number of vertices.
     * @param vertex array of vertices
     */
    public void update(int numberVerticesInput, Vertex [] vertex){
        numberVertices = numberVerticesInput; 
        if (Y<=0) return;
        QuickSort(vertex); // make sure old list has been reranked.
        minValue =  vertex[rankIndex.getQuick(Yminus1)].degree;
        
        // this is for DEBUGGING only
//        if (!checkSort(vertex)) 
//        {
//            System.err.println("*** ERROR - not sorted properly");
//            printList(System.err, " : ", vertex);
//            return;
//        }
//        else System.out.println("!!! Sorted properly");
        
        for (int a=0; a<numberVertices;a++){
            if (vertex[a].rank >=0) continue; //  this is already in the right place in the list
            int value = vertex[a].degree;
            if (value>minValue)
            {  // must insert this artefact a in top Y as value is bigger than current lowest value
                int r;
                for (r=Yminus2; r>=0; r--) if (value<=vertex[rankIndex.getQuick(r)].degree) break;
                // insert a in rankIndex list and update minimum value to get into ranked list
                rankIndex.beforeInsert(r+1, a);             
                minValue = vertex[rankIndex.getQuick(Yminus1)].degree;
            }
        }//eo for a

       // now update rank values in the vertex array and, for top Y, find the turnover
       for (int y=0; y<Y; y++) delta[y]=0;
       for (int r=0; r<Y; r++){
                int index = rankIndex.getQuick(r);
                int oldRank = vertex[index].rank;
                vertex[index].rank =r;
                if (oldRank<0) oldRank=Y; //System.out.println("!!! Turnover at Y boundary");
                while (oldRank>r) delta[--oldRank]++;
            }
       // Now make ranked list of size Y, marking those that have fallen out of the Y list as unranked.
       int size=rankIndex.size(); 
       if (size>Y)
       {       
           for (int r=Y; r<size; r++) vertex[rankIndex.getQuick(r)].rank=UNRANKED;
           rankIndex.removeFromTo(Y, size-1);  
       }
       
        // this is for DEBUGGING only
//        if (!checkSort(vertex)) 
//        {
//            System.err.println("*** ERROR - not sorted properly");
//            printList(System.err, " : ", vertex);
//            return;
//        }
//        else System.out.println("!!! Sorted properly");

    }

    /**
     * Print list of current ranking.
     *@param PS PrintStream such as a file or System.out
     * @param vertex array of vertices
     *@param sep separation character, usually tab
     */
    public String printList(PrintStream PS, String sep, Vertex [] vertex){
        String s="";
        PS.println("Rank"+sep+"Index"+sep+vertex[0].stringBasicLabel(sep));
        for (int r=0; r<Y; r++) {
            int v=rankIndex.getQuick(r);
            PS.println(r+sep+v+sep+vertex[v].stringBasic(sep));
        }
        return s;
    }
    /**
     * Print turnover.
     *@param PS PrintStream such as a file or System.out
     *@param sep separation character, usually tab
     */
    public void printTurnover(PrintStream PS, String sep){
        for (int y=0; y<Y; y++) PS.print("y="+(y+1)+sep);
        PS.println();
        for (int y=0; y<Y; y++) PS.print(delta[y]+sep);
        PS.println();
    }
    /**
     * String of current ranking.
     *@param vertex array of vertices
     *@param sep separation character, usually tab
     */
    public String toString(String sep, Vertex [] vertex){
        String s="";
        for (int r=0; r<Y; r++) {
            int v=rankIndex.getQuick(r);
            s=s+v+sep+vertex[v].stringBasic(sep)+sep;
        }
        return s;
    }
    
    /**
     * The turnover in the top y list since last time updated..
     * @param y length of list
     * @return turnover in the top y list 
     */
    public int getTurnover(int y){return delta[y];}
    /**
     * The index of the object ranked r-th.
     * Ranking runs from 0 to (Y-1).
     * @param r rank required
     * @return index of object ranked r.
     */
    public int getIndex(int r){return rankIndex.get(r);}
        
    
    /** 
    * Sorts vertex degree values
    */      
    private void QuickSort(Vertex [] vertex)
   {
        QuickSort(0, Yminus1, vertex);
    }
    /** 
    * Sorts integer array.
    *@param lo0 the starting index
    *@param hi0 the maximum index (inclusive)
    */      
    private void QuickSort(int lo0, int hi0, Vertex [] vertex)
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
         midindex = ( lo0 + hi0 ) / 2 ;
         mid=vertex[rankIndex.get(midindex)].degree;
//         mid = a[ ( lo0 + hi0 ) / 2 ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than
             * the partition element starting from the left Index.
             */
            // while( ( lo < hi0 ) && ( a[lo] < mid ) ) 
            while( ( lo < hi0 ) && ( vertex[rankIndex.get(lo)].degree  > mid ) )
               ++lo;

            /* find an element that is smaller than 
             * the partition element starting from the right Index.
             */
            //while( ( hi > lo0 ) && ( a[hi] > mid ) )
            while( ( hi > lo0 ) && ( vertex[rankIndex.get(hi)].degree  < mid ) )
               --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            { // swap elements
               T = rankIndex.get(lo); 
               rankIndex.set(lo, rankIndex.get(hi));
               rankIndex.set(hi, T);
               
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            QuickSort( lo0, hi, vertex );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            QuickSort(lo, hi0,  vertex );

      }
   }
    /**
     * Cheap but complete sort of top Y indexes into order.
     * @param vertex array of vertices
     */
    private void sort(Vertex [] vertex){
        for (int r=0;r<Y;r++)
            for (int s=r; s<Y; s++)
            {
                if (vertex[rankIndex.getQuick(s)].degree>vertex[rankIndex.getQuick(r)].degree)
                {
                    int sindex = rankIndex.getQuick(s);
                    rankIndex.set(s,rankIndex.getQuick(r));
                    rankIndex.set(r,sindex);
                }
            }
    }

    /**
     * Checks the sort.
     * @return true (false) if (not) OK
     */
    private boolean checkSort(Vertex [] vertex){
        for (int r=1;r<Y;r++)
            if (vertex[rankIndex.getQuick(r-1)].degree <  vertex[rankIndex.getQuick(r)].degree) return false;
        return true;
    }
}
