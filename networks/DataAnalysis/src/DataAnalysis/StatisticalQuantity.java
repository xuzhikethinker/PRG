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
 * <p>Note that this can easily run out of precision especially for higher moments.
 *
 * @author time
 */
    
    
    public class StatisticalQuantity    
    {
     String Version = "StatisticalQuantity:0901123";

     /**
      * Name of quantity.
      * <p>Not used if null.
      */
     private String name ="";
    
// the following are to be updated as we go   
     double maximum;
     double minimum;
     int count;
     //The following are not kept updated
     //private double total;
     //private double squaretotal;
     private double average;
     private double sigma;
     private double error;
     private double secondmoment;
     private boolean updatestatistics; // true if need updating
     
     /**
      * Maximum order to be used for moments.
      */
     private final int maximumOrder; 
        

     /**
      * Array of current totals of powers.
      * <p>powerTotal[n] = sum k^(n-1)
      */
     private double [] powerTotal;
     
     /**
      * Number of warnings to be issued before further messages are supressed.
      */
     private int warningCount;
     
     /**
         * Constructor.
         * <p>Maximum order of moments is 3.
         * Initial minimum (maximum) values are very large positive (negative) values.
         */
        public StatisticalQuantity()
        {
          maximumOrder=3;
          setDefaultValues(7e99,-7e99);
        }

     /**
         * Constructor.
         * <p>Maximum order of moments is 3.
         * Initial minimum (maximum) values are very large positive (negative) values.
      * @param warningCountInput number of warning to give.
         */
        public StatisticalQuantity(int warningCountInput)
        {
          maximumOrder=3;
          setDefaultValues(7e99,-7e99);
          warningCount=warningCountInput;
        }

        /**
         * Constructor, maximum order of moments is 3.
         * @param intialMinimum initial minimum value
         * @param initialMaximum initial maximum value
         */
        public StatisticalQuantity(double intialMinimum, double initialMaximum)
        {
          maximumOrder=3;
          setDefaultValues(initialMaximum,intialMinimum);
        }

        /**
         * Constructor.
         * @param intialMinimum initial minimum value
         * @param initialMaximum initial maximum value
         * @param maxOrder maximum order of moments to be calculated (will be set to be at least 3)
         */
        public StatisticalQuantity(double intialMinimum, double initialMaximum, int maxOrder)
        {
          maximumOrder=(maxOrder<3?3:maxOrder);
          setDefaultValues(initialMaximum,intialMinimum);
        }

        public void setDefaultValues(double intialMinimum, double initialMaximum)
        {
          warningCount=10;
//          powerTotal[0]=0;
//          squaretotal=0;
          count=0;
          average=0;
          secondmoment=0;
          sigma=0;
          error=0;
          maximum=initialMaximum;
          minimum=intialMinimum;
          updatestatistics=false; 
          powerTotal = new double[maximumOrder];
        }

        public String getName(){return name;}
        public void setName(String newName){name=newName;}
        /**
         * Used to add new measurement of the quantity.
         * @param x
         */
        public void add(double x)
        {
            updatestatistics =true;
            double p=1;
            boolean isNotSmall = ((count>0) && (Math.abs(x)>powerTotal[0]*1e-7) );
            for (int n=0; n<maximumOrder; n++) {
                p*=x;
                powerTotal[n]+=p;
                if (isNotSmall &&   warningCount>0 && (Math.abs(p)<powerTotal[n]*1e-7)){
                    System.err.println("!!! value "+x+" gives x^"+n+" of "+p+" which is too small for current total this power of "+powerTotal[n]);
                    warningCount--;
                }
            }
//            total += x;
//            squaretotal += x*x;
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
        
        /**
         * Calculates the average in the StatisticalQuantity.
         *@return average as a double.
         */
        public double getAverage()
        {
           if (updatestatistics) calcStatistics();  
           return average;
        }
        
        /**
         * Gives the number of measurements made.
         *@return count.
         */
        public int getCount()
        {
           return count;
        }
        
        /**
         * Calculates the maximum in the StatisticalQuantity.
         *@return maximum as a double.
         */
        public double getMaximum()
        {
           return maximum;
        }
        
        /**
         * Calculates the minimum in the StatisticalQuantity.
         *@return minimum as a double.
         */
        public double getMinimum()
        {
           return minimum;
        }
        
        /**
         * Calculates the second moment in the StatisticalQuantity.
         *@return second moment as a double.
         */
        public double getSecondMoment()
        {
           if (updatestatistics) calcStatistics();  
           return secondmoment;
        }
        
        /**
         * Calculates the n-th moment in the StatisticalQuantity.
         *@param n order reuqired for moment.
         *@return n-th moment as a double, -1 indicates an error.
         */
        public double getMoment(int n)
        {
           if (updatestatistics) calcStatistics();  
           if ((n>0) && (n<=maximumOrder) && (count>0)) return ((double) powerTotal[n-1])/((double) count);
           return -1;
        }
        
        /**
         * Calculates the rms in the StatisticalQuantity.
         *@return sigma is the error in the one result.
         */
        public double getSigma()
        {
           if (updatestatistics) calcStatistics();  
           return sigma;
        }

        /**
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

        /**
         * Calculates the error in the StatisticalQuantity.
         *@return error is the error in the average.
         */
        public double getError()
        {
           if (updatestatistics) calcStatistics();  
           return error;
        }

        /**
         * Calculates the statistics.
         */
        private void calcStatistics()
        {
            if (!updatestatistics) return;
            if (count==0) return;
            average=((double) powerTotal[0])/((double) count);
            secondmoment=((double) powerTotal[1])/((double) count);
            if (count>1) 
            {
             double s = secondmoment-average*average;
             if (s<0) sigma=0; else sigma=Math.sqrt(s);
             error=sigma/Math.sqrt(count);   
             }
            else 
            {
                sigma=0;
                error=0;
            }
            updatestatistics =false;
        }
        
        /**
         * Returns a string labelling the toString result.
         * <p>Use to prouce a header line in output.
         *@param string used to separate items
         *@return string labelleing the toSTring() output
         */
        public String labelString(String sep)
        {
            String s=(name.length()==0?"":name+" ")+"Average"+sep+"Error"+sep+"Maximum"+sep+"Minimum"+sep+"Count";
            return s;
        }
        
        /**
         * Returns a string representation.
         *@param string used to separate items
         *@return string representing the statistical quantity
         */
        public String toString(String sep)
        {
            String s=getAverage()+sep+getError()+sep+maximum+sep+minimum + sep + count;
            return s;
        }

        /**
         * Returns a string representation of average and error.
         *@param string used to separate items
         *@return string representing the average and its error
         */
        public String avErrString(String sep)
        {
            return (getAverage()+sep+getError());
        }

        
        
        
    }   
