/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/** Class for keeping track of updates in Metropolis algorithms.
* @author time
     */
    public class UpdateRecord {
        int tried=0;
        int made=0;
//        double frac=0.0;
        int totaltried=0;
        int totalmade=0;
//        double totalfrac=0.0;
        
        public UpdateRecord(){
        }        

        public void UpdateRecord(int t, int m, int tt, int tm)
        {
        tried=t;
        made=m;
        totaltried=tt;
        totalmade=tm;       
//        frac= (tried>0) ?  made/((double) tried) : 0.0;
//        totalfrac= (totaltried>0) ?  totalmade/((double) totaltried) : 0.0;
        }        
        
        public void reset()
        {
        tried=0;
        made=0;
//        frac=0.0;
        totaltried=0;
        totalmade=0;
//        totalfrac=0.0;       
        }        
        
        public void update(int t, int m)
        {
        tried=t;
        made=m;
        totaltried+=t;
        totalmade+=m; 
//        frac= (tried>0) ?  made/((double) tried) : 0.0;
//        totalfrac= (totaltried>0) ?  totalmade/((double) totaltried) : 0.0;
        }        

        /**
         * Number of successful updates made on last round.
         * @return Number of successful updates made on last round.
         */
        public int getMade(){return made;}
        /**
         * Number of successful updates tried on last round.
         * @return Number of successful updates tried on last round.
         */
        public int getTried(){return tried;}
        /**
         * Number of successful updates made on last round.
         * @return Number of successful updates made on last round.
         */
        public int getTotalMade(){return totalmade;}
        /**
         * Number of successful updates tried on last round.
         * @return Number of successful updates tried on last round.
         */
        public int getTotalTried(){return totaltried;}
        /**
         * Fraction of successful updates on last round.
         * @return Fraction of sucessful updates on last round.
         */
        public double getFractionMade(){return (tried>0) ?  made/((double) tried) : 0.0;}
        
        /**
         * Fraction of successful updates in total.
         * @return Fraction of sucessful updates in total.
         */
        public double  getTotalFractionMade(){return (totaltried>0) ?  totalmade/((double) totaltried) : 0.0;}
        
        @Override
        public String toString()
        {
        return( "Tried "+ tried + 
                ", made "+ made + 
                ", total tried "+ totaltried + 
                ", total made "+ totalmade );       
        } 
        

    }// eo UpdateRecord
