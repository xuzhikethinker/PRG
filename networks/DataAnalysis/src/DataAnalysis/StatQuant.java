/*
 * StatQuant.java
 *
 * Created on 14 November 2006, 19:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package DataAnalysis;

import java.math.BigInteger;
    
    
 /*
 * StatQuant class
 *
 *  Defines a statistical quantity, keeping running totals of 
 *  sums and sums of squares with moments, sigma and errors too.
 *  Uses BigIntegers internally.
 * @author time
 */

    
    public class StatQuant    
    {
     // the following are to be updated as we go   
     private int maximum;
     private int minimum;
     private int count;
     //The following are not kept updated
     private BigInteger total;
     private BigInteger squaretotal;
     private double average;
     private double sigma;
     private double error;
     private double secondmoment;
     private boolean updatestatistics; // true if need updating
        
        public StatQuant()
        {
          total=new BigInteger("0");
          squaretotal=new BigInteger("0");
          count=0;
          average=0;
          secondmoment=0;
          sigma=0;
          error=0;
          maximum=0;
          minimum=0;
          updatestatistics=false; 
        }
        
        public void add(int x)
        {
            Integer X = new Integer(x);
            updatestatistics =true;
            BigInteger xval = new BigInteger(X.toString());
            total = total.add(xval);
            xval=xval.multiply(xval);
            squaretotal = squaretotal.add(xval);
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
            average=total.doubleValue()/count;
            secondmoment=squaretotal.doubleValue()/count;
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
        
        
    }   
