/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

import java.util.Random;

/**
 *
 * @author time
 */
public class TimRandom {
    private Random Rnd;
    
    
    
    /**
     * Inialise random generator to use time as seed.
     * <br> See Schildt p524, time is used as seed
     */
    public TimRandom(){Rnd = new Random(); }

    /**
     * Inialise random generator usinggiven seed.
     * <br> See Schildt p524, time is used as seed
     * @param seed seed for random number generator
     */
    public TimRandom(int seed){Rnd = new Random(seed); }

    // **********************************************************************
 
 // ------------------------------------------------------------------------
    /**
     * Generates integer random numbers binomially distributed.
     * @param average is mean value
     * @param N is number of 'dice' used
     *@return integer between 0 and (int) average*2 inclusive
     */
    public int getRandomBinomial(double average, int N)
    {
        if (N<1) return (int)(average+1e-6);
        double total=0;
        for (int n=0;n<N;n++) total+=Rnd.nextDouble();
        return ( (int) (total*average/((double) N) ) );
    }

    // ------------------------------------------------------------------------
    /**
     * Generates integer random numbers Markov distributed.
     * @param prob is the probability you continue.
     * @param N is maximum number of steps to make 
     *@return integer between 0 and N inclusive
     */
    public int getRandomMarkov(double prob, int N)
    {
        if (N<0) return (-N);
        int n=0;
        for (n=0;n<N;n++) if (Rnd.nextDouble()>prob) break;
        return n;
    }




}
