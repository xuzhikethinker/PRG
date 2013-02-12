/*
 * Shuffle.java
 *
 * Created on 28 March 2007, 13:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

/**
 * Shuffles integer arrays.
 * @author time
 */
public class Shuffle {
    
    /** Creates a new instance of Shuffle */
    public Shuffle() {
    }
    
    // take as input an array of strings and rearrange them in random order
    public static void shuffle(String[] a) {
        int N = a.length;
        String value="";
        int r=-1;
        for (int i = 0; i < N; i++) {
            //r = i + (int) (Math.random() * (N-i));   // between i and N-1
            r = (int) (Math.random() * N );  // allow to shuffle to self - faster than checking i!=r everytime?
            value = a[i];
            a[i]=a[r];
            a[r]=value;
        }
    }

    // take as input an array of strings and print them out to standard output
    public static void show(String[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
    }

    // shuffle the command line arguments
    public static void main(String[] args) { 
        shuffle(args);
        show(args);

    }
}

