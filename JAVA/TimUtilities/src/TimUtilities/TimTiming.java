package TimUtilities;
/*
 * TimTiming.java
 *
 * Created on 15 February 2006, 13:55
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/**
 * Class used to time programme segments.
 * @author time
 */
public class TimTiming 
{    
        long initialtime=-1;
        long currenttime=-1;
        
        boolean intervalTimerOn=false;
        double intervalTimeSecs=300;
        double nextTimeSecs=intervalTimeSecs; 
        
        
        /** Creates a new instance of TimTimTiming */
        public TimTiming ()
        {
            initialtime =  System.currentTimeMillis();
        }
           
    /**
     * Set initial time using internal clock.
     */
    public void setInitialTime()
    {
      initialtime = System.currentTimeMillis ();
      return;
      //return ( initialtime);
    }
   /**
     * Sets the interval time.
     * @param intervalTimeMinutes interval time in minutes.
     */
    public void setIntervalTimeMinutes(double intervalTimeMinutes){
        if (intervalTimeMinutes<=0) intervalTimerOn=false;
        intervalTimerOn=true;
        intervalTimeSecs=intervalTimeMinutes*60; 
        nextTimeSecs=intervalTimeSecs;
    }
    
    /**
     * Tests to see if interval has passed since last time test returned true.
     * <p>Waits for at least <tt>intervalTimeMinutes</tt> seconds since the 
     * last positive test before before returning true.
     * @return true if at least <tt>intervalTimeMinutes</tt> seconds since last positive
     */
    public boolean testIntervalTime(){
        if ((!intervalTimerOn) || (elapsedTime()<nextTimeSecs) ) return false;
        nextTimeSecs=elapsedTime()+intervalTimeSecs;
        return true;
    }
            
    public void test()
    {
        String s=runTimeString(0);
    }
    
    /**
     * Set current time using internal clock.
     */
    public void setCurrentTime()
    {
      currenttime = System.currentTimeMillis ();
      return;
      }

    /**
     * Sets current time variable and returns elapsed time in seconds relative to initial time.
     *@return elapsed time in seconds as double
     */
    public double elapsedTime()
    {
      setCurrentTime();  
      return ( (double) (currenttime -initialtime)/1000.0 );
    }

    /**
     * Sets current time variable and returns elapsed time in seconds relative to initial time.
     *@return elapsed time as String
     */
    public String elapsedTimeString()
    {
      return ( runTimeString(elapsedTime()) );
    }
    
    

    /**
     * Estimates remaining time if current-initial time is fraction f of process.
     *@param f fraction of process elapsed
     *@return string of remaining time estimate
     */
    public String estimateRemainingTimeString(double f)
    {
        double r = estimateRemainingTime(f) ;
      if (r<0) return("gave zero fraction of process elapsed");  
      return( runTimeString(r) );
    }

    /**
     * Estimates remaining time if current-initial time is fraction f of process.
     *@param f fraction of process elapsed
     *@return predicted remaining time as double
     */
    public double estimateRemainingTime(double f)
    {
      if (f==0) return -1;  
      return(  elapsedTime()*(1.0-f)/f);
    }

    /**
     * Gives a string of the time relative to initial time. 
     */
    public String runTimeString()
    {
         return ( runTimeString(elapsedTime()) );
    }

    /**
     * Returns string with time in second, minutes etc .
     *@param dtime time in seconds.
     *@return String of input time
     */
    static public String runTimeString(double dtime)
    {
         int time = (int) ( dtime +0.5);
         int secs = time % 60;
         time = time/60;
         int minutes =time%60;
         time = time/60;
         int hours = time %24;
         int days = time /24;
         String s="";
         if (hours>0) s=s+hours+"h";
         if (minutes>0) s=s+minutes+"m ";
         s=s+secs+"s";
         return ( s );
    }
    
    
}//eo TimTiming class
