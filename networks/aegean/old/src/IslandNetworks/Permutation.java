/*
 * Permutation.java
 *
 * Created on 04 August 2006, 13:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import java.util.Random;


/**
 *
 * @author time
 */
public class Permutation {
    boolean permset;
    int size; // original size
    int left; // numbers left to be chosen/permuted
    int [] list; // list of numbers;
    //int last; // last number picked
    Random rnd;
    
    /** Creates a new instance of Permutation.
     * Does not create the permutation yet.  Call next or set.
     *@param s size of permutation (numbers 0 .. (s-1))
     */
    public Permutation(int s) {
        permset = false;
        rnd = new Random();
        size =s;
        left =s;
        list = new int [size];
        for (int i=0; i<size; i++) list[i]=i;        
        //last = list[left];
    }
    
    /** Gets next random number of permutation.
     **/
    public int next() {
        if (left==1) {left =0; return list[0];};
        int r = rnd.nextInt(left);
        // put swap entry r with (left-1) entry, dec. left
        int p = list[r];
        list[r] = list[--left];
        list[left]=p;
        return(p);
    }

   /** Sets up complete permutation.
     */
    public void set() {
         while (left>0) next(); 
    }

    /** Gets entry number p of permutation of 0 .. (size-1).
     * Initialises permutation if needed.
     *@param p the entry of the permutation.
     */
    public int get(int p) {
         if (left>0) set(); 
         return (list[p]);
    }

}
