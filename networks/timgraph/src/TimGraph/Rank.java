/*
 * Rank.java
 *
 * Created on 11 December 2006, 17:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimGraph;

import TimUtilities.NumbersToString;

/**
 * Rank class.
 *
 * Ranks vertices.
 *
 * @author time
 */
    
    
    public class Rank 
    {
        static NumbersToString n2s = new NumbersToString();
        int visits;
        double value;
        double value2;
        
        public Rank()
        {
            visits=0;
            value=0;
            value2=0;
        }

         /**
         * Deep copy.
         * @param r old rank
         */
         public Rank(Rank r)
        {
            visits=r.visits;
            value=r.value;
            value2=r.value2;
        }

        /*
         * Updates ranking values.
         *@param rankingValue1 ranking value 1 update
         *@param rankingValue2 ranking value 2 update
         */
        public void updateRanking(double rankingValue1, double rankingValue2)  
        {
                  visits++;
                  value +=  rankingValue1 ;
                  value2 += rankingValue2;
              }

        /**
         * Produces string reading for printing of Rank values.
         *@param sep separation string
         *@param dec number of decimal points to hold.
         */
        public String printString(int dec, String sep)
        {
            String s=visits+sep+NumbersToString.toString(value,dec)+ sep +NumbersToString.toString(value2,dec);
            return s;
        }

        /**
         * Produces string reading for printing of Rank values.
         *@param sep separation string
         *@param dec number of decimal points to hold.
         */
        static public String toString(Rank r, int dec, String sep)
        {
            if (r==null) return "."+sep+"."+ sep +".";
            return r.printString(dec, sep);
        }

        /**
         * Produces label for string of Rank values.
         *@param sep separation string
         */
        static public String labelString(String sep)
        {
            return "visits"+sep+"rank1"+sep+"rank2";
        }

        /**
         * Sets maximum values associated with rank.
         *@param r rank used to update maximum visits and values 1 and 2.
         */
        public void setMaximum(Rank r)
        {
            if (visits<r.visits) visits=r.visits;
            if ( value<r.value) value=r.value;
            if (value2<r.value2) value2=r.value2;
        }

        /**
         * Sets minimum values associated with rank.
         *@param r rank used to update minimum visits and values 1 and 2.
         */
        public void setMinimum(Rank r)
        {
            if (visits>r.visits) visits=r.visits;
            if ( value>r.value) value=r.value;
            if (value2>r.value2) value2=r.value2;
        }

    }
