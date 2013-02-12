/*
 * StatisticalQuantity.java
 *
 * Created on 12 May 2006, 16:46
 *
 *  Defines a statistical quantity, keeping running totals of 
 *  sums and sums of squares with moments, sigma and errors too. 
 */

package TimUtilities;

/**
 * Defines a statistical quantity, keeping running totals of 
 *  sums and sums of squares with moments, sigma and errors too. 
 *
 * @author time
 */
    
    
    public class StatisticalQuantity    
    {
        static double MAX = 7e99;
     // the following are to be updated as we go   
     public int count;
     private double total;
     private double squaretotal;
     public double maximum;
     public double minimum;
     //The following are not kept updated
     private double average;
     private double sigma;
     private double error;
     private double secondmoment;
     private boolean updatestatistics; // true if need updating
        
        public StatisticalQuantity()
        {
          setDefaultValues(MAX,-MAX);
        }

        public StatisticalQuantity(double intialMinimum, double initialMaximum)
        {
          setDefaultValues(initialMaximum,intialMinimum);
        }
        
        /**
         * Deep copy of StatisticalQuantity.
         * <p>If input is null then sets up with default values.
         * @param sq StatisticalQuantity to be deep copied.
         */
        public StatisticalQuantity(StatisticalQuantity sq)
        {
          if ( sq == null )  { setDefaultValues(MAX,-MAX); return; }
          total=sq.total;
          squaretotal=sq.squaretotal;
          count=sq.count;
          maximum=sq.maximum;
          minimum=sq.minimum;
          // force rest of variables to be recalculated
          updatestatistics=true;  
          average=0;
          secondmoment=0;
          sigma=0;
          error=0;
        }


        public void setDefaultValues(double intialMinimum, double initialMaximum)
        {
          total=0;
          squaretotal=0;
          count=0;
          average=0;
          secondmoment=0;
          sigma=0;
          error=0;
          maximum=initialMaximum;
          minimum=intialMinimum;
          updatestatistics=false; 
        }


        
        /*
         * Adds another measurement.
         * <br>Updates only those quantities which are kept up to date as gong along.  
         * Lengthy calculations of all other interesting statistics is left till they are required.
         */
        public void add(double x)
        {
            updatestatistics =true;
            total += x;
            squaretotal += x*x;
            count++;
            if (count>1) 
            { 
             if (maximum < x) maximum = x;
             if (minimum > x) minimum = x;
            }
            else 
            {
                maximum=x;
                minimum=x;
            }
        }
        
        public double getTotal(){ return total;}
        /*
         * Calculates the average in the StatsisticalQuantity.
         *@return average as a double.
         */
        public double getAverage()
        {
           if (updatestatistics) calcStatistics();  
           return average;
        }
        
        /*
         * Calculates the second moment in the StatsisticalQuantity.
         *@return second moment as a double.
         */
        public double getSecondMoment()
        {
           if (updatestatistics) calcStatistics();  
           return secondmoment;
        }
        
        /*
         * Calculates the rms in the StatsisticalQuantity.
         *@return sigma is the error in the one result.
         */
        public double getSigma()
        {
           if (updatestatistics) calcStatistics();  
           return sigma;
        }

        /*
         * Calculates the error in the StatsisticalQuantity.
         *@return error is the error in the average.
         */
        public double getError()
        {
           if (updatestatistics) calcStatistics();  
           return error;
        }

        /*
         * Calculates the statistics.
         *
         */
        private void calcStatistics()
        {
            if (!updatestatistics) return;
            updatestatistics =false;
            if (count==0) return;
            average=((double) total)/((double) count);
            secondmoment=((double) squaretotal)/((double) count);
            if (count>1) 
            { 
             sigma=Math.sqrt(secondmoment-average*average);
             error=sigma/Math.sqrt(count);   
             }
            else 
            {
                sigma=0;
                error=0;
            }
        }
        
        
    }   
