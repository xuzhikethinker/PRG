/*
 * EventProbability.java
 *
 * Created on 15 November 2006, 16:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package CopyModel;

/**
 * EventProbability Class.
 * <br> Keeps event probabilities in hand.
 * @author time
 */

    
    public class EventProbability
    {
        double pBar;
        double pPref; // p_p Probability for  PREFerential attachment
        double pRand; // p_r Probability for RANDom artifcat choice
        double pPrefPluspRand; // p_p+p_r=1-pBar

        /* Constructs an EventProbability
         *@param pp the preferential attachment (copy edge) event probability
         *@param pr the random artefact choice event probability
         */
        public EventProbability(double pp, double pr)
        {
            setPpPr(pp,pr);            
        }

        /* Sets EventProbability using pp & pr.
         * If either parameter is negative then pp+pr=1 is set using the modulus of the negative parameter as only input.
         *@param pp the preferential attachment (copy edge) event probability
         *@param pr the random artefact choice event probability
         *@return true if there is a problem, false if OK
         */
        public boolean setPpPr(double pp, double pr)
        {
            if (pr<0) {
                pRand=-pr;
                pPref=1-pRand; 
                pPrefPluspRand =1.0; 
                pBar=0.0;
                return test();
            }
            if (pp<0) {
                pPref=-pp;
                pRand=1-pPref; 
                pPrefPluspRand =1.0; 
                pBar=0.0;
                return test();
            }
            pPref=pp; // p_p Probability for  PREFerential attachment
            pRand=pr; // p_r Probability for RANDom artifcat choice            
            pPrefPluspRand =pp+pr; 
            pBar=1.0-pPrefPluspRand;
            return test();
            }

        /* Sets EventProbability using pr assuming pbar zero.
         * <br> p_p = 1-p_r is set.
         *@param pr the random artefact choice event probability
         */
        public void setPrPbarZero(double pr)
        {
            setPpPr(1.0-pr,pr);
        }
        
        /* Test probabilities.
         *@return true if there is a problem, false if OK
         */
        public boolean test()
        {
            if ((pBar<=1.0) && (pBar>=0.0)) return false;
            return true;
        }
        
        
       /**
        * Returns a label for columns matching the toString() format.
        * @param Sep separation characters put beteen items
        *@return Label for string of information on probabilities  
        */
        public String label(String Sep)
        {
            return("p_p"+Sep+"p_r"+Sep+"pBar");
        }
            
        /**
        * Returns a string representing all the probability values.
        * @param Sep comment characters put at the start of every line
        *@return String of information on probabilities  
        */
        public String toString(String Sep)
        {
            return(pPref+Sep+pRand+ Sep +pBar);
        }
    

    }//eo EventProbability class
    
    
    

