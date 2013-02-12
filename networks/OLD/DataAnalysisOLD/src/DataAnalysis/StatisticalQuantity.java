/*
 * StatisticalQuantity.java
 *
 * Created on 12 May 2006, 16:46
 *
 *  Defines a statistical quantity, keeping running totals of 
 *  sums and sums of squares with moments, sigma and errors too. 
 */

package DataAnalysis;

/**
 * Defines a statistical quantity, keeping running totals of 
 *  sums and sums of squares with moments, sigma and errors too. 
 *
 * @author time
 */
    
    
    public class StatisticalQuantity    
    {
     String Version = "StatisticalQuantity:060809";
    
// the following are to be updated as we go   
     double maximum;
     double minimum;
     int count;
     //The following are not kept updated
     private double total;
     private double squaretotal;
     private double average;
     private double sigma;
     private double error;
     private double secondmoment;
     private boolean updatestatistics; // true if need updating
        
        public StatisticalQuantity()
        {
          setDefaultValues(7e99,-7e99);
        }

        public StatisticalQuantity(double intialMinimum, double initialMaximum)
        {
          setDefaultValues(initialMaximum,intialMinimum);
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
        
        /*
         * Calculates the average in the StatsQuant.
         *@return average as a double.
         */
        public double getAverage()
        {
           if (updatestatistics) calcStatistics();  
           return average;
        }
        
        /*
         * Gives the number of measurements made.
         *@return count.
         */
        public int getCount()
        {
           return count;
        }
        
        /*
         * Calculates the maximum in the StatsQuant.
         *@return maximum as a double.
         */
        public double getMaximum()
        {
           return maximum;
        }
        
        /*
         * Calculates the minimum in the StatsQuant.
         *@return minimum as a double.
         */
        public double getMinimum()
        {
           return minimum;
        }
        
        /*
         * Calculates the second moment in the StatsQuant.
         *@return second moment as a double.
         */
        public double getSecondMoment()
        {
           if (updatestatistics) calcStatistics();  
           return secondmoment;
        }
        
        /*
         * Calculates the rms in the StatsQuant.
         *@return sigma is the error in the one result.
         */
        public double getSigma()
        {
           if (updatestatistics) calcStatistics();  
           return sigma;
        }

        /*
         * Tests to see if error in the average is less than given tolerances.
         *@param relativeError the relative error required so (error/average) must be less than this
         *@param absoluteError the absolute error required so error must be less than this 
         *@param maximumAbsoluteValue if mod(average) is less than this value use absolute error
         *@param true if error is within tolerances else return zero. 
         *@return result true if average is within tolerances given.
         */
        public boolean testAverage(double relativeError, double absoluteError, double maximumAbsoluteValue)
        {
           boolean result=false;
           if (updatestatistics) calcStatistics();  
           if (Math.abs(average) <  maximumAbsoluteValue) 
           {if (error<absoluteError) result= true;}
           else {if (error<relativeError*average) result= true;}
           return result;
        }

        /*
         * Calculates the error in the StatsQuant.
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
            updatestatistics =false;
        }
        
        /*
         * Returns a string labelling the toString result.
         *@param string used to separate items
         *@return string labelleing the toSTring() output
         */
        public String labelString(String sep)
        {
            String s="Average"+sep+"Error"+sep+"Maximum"+sep+"Minimum"+sep+"Count";
            return s;
        }
        
                /*
         * Returns a string representation.
         *@param string used to separate items
         *@return string representing the statistical quantity
         */
        public String toString(String sep)
        {
            String s=getAverage()+sep+getError()+sep+maximum+sep+minimum + sep + count;
            return s;
        }

        /*
         * Returns a string representation of average and error.
         *@param string used to separate items
         *@return string representing the average and its error
         */
        public String avErrString(String sep)
        {
            return (getAverage()+sep+getError());
        }

        
        
        
    }   
