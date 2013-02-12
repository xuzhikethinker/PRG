/*
 * Permutation.java
 *
 * Created on 04 August 2006, 13:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

import java.util.Random;


/**
 * This sets up a permutation of N numbers.
 * <br>The permutation is in list[] with everything from list[p>=left] 
 * having been permuted randomly.  Below is still the old order. 
 * @author time
 */
public class Permutation {
    //boolean permset;
    private int size; // original size
    private int left; // numbers left to be chosen/permuted
    private int [] list; // list of numbers;
    //int last; // last number picked
    Random rnd;
    
    /** Creates a new instance of Permutation.
     * Does not create the permutation yet.  Call next or set.
     *@param s size of permutation (numbers 0 .. (s-1))
     */
    public Permutation(int s) {
        //permset = false;
        rnd = new Random();
        size =s;
        list = new int [size];
        for (int i=0; i<size; i++) list[i]=i; 
        newPermutation();
    }
 
    /*
     * Creates a new permutation of same size. 
     * <br>Note this can be used to give a second (or later) permutation of the 
     * same numbers.  The list starts shuffled and its just reshuffled as on the first time.
     */
    public void newPermutation()
    {
        left =size;               
    }
    
    /** Gets next random number of permutation.
     * <br>Does not check to see run out of entries.
     */
    public int next() {
        if (left==1) {left =0; return list[0];}
        int r = rnd.nextInt(left);
        // put swap entry r with (left-1) entry, dec. left
        int p = list[r];
        list[r] = list[--left];
        list[left]=p;
        return(p);
    }

    /** Gets next random number of permutation.
     * <br>Just keeps cycling round numbers.
     * If left=size then indicates just finished one cylce.
     */
    public int endlessnext() {
        if (left==1) {left =size; return list[0];}
        int r = rnd.nextInt(left);
        // put swap entry r with (left-1) entry, dec. left
        int p = list[r];
        list[r] = list[--left];
        list[left]=p;
        return(p);
    }

   /** Sets up complete permutation.
     */
    public void setall() {
         get(0);
    }

    /** Gets entry number p of permutation of 0 .. (size-1).
     * Initialises permutation if needed.
     *@param p the entry of the permutation.
     */
    public int get(int p) {
         while (left>p) next(); 
         return (list[p]);
    }
    
    /* How many of permutation remain to be set. 
     * i.e. left .. (size-1) are set while 0<=perm.get(n)<left have not been updated.
     * @return size of the permutation.
     */
    public int getLeft() {
         return (left);
    }
    /* true if more numbers left in thepermutation. 
     * <br>tru if left>0
     * @return true if more numbers left in permutation.
     */
    public boolean hasMore() {
         return (left>0?true:false);
    }
    /* Gets size of permutation. 
     * i.e. permutation of 0 .. (size-1)
     * @return size of the permutation.
     */
    public int getSize() {
         return (size);
    }

}
