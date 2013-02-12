/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 * Helps display how long counts are proceeding.
 * @author time
 */
public class TimCounting {
    
    /**
     * Counting up to this number
     */
    final int totalCount;
    /**
     * Interval between dots 
     */
    final int dotInterval;
    /**
     * Interval between new lines
     */
    final int newLineInterval;
    private int currentCount=-1;
    final String DOT;
    boolean messagesOn=true;

    public TimCounting(int totalCount, boolean messagesOn){
        
        this.totalCount=totalCount;
        if (totalCount<100) dotInterval=1;
        else dotInterval=totalCount/100;
        DOT=".";
        if (totalCount<10) newLineInterval=1;
        else newLineInterval=totalCount/10;
        currentCount=0;
        this.messagesOn=messagesOn;
    }
    
    /**
     * Increments counter and takes appropriate action.
     * <p>Every 1/100 of count total a dot is displayed without a line feed.
     * <br>Every 1/10 of total count will return true.  Then need to supply 
     * any information needed with a line feed.
     * @return true if 1/10 of count reached
     */
    public boolean increment(){
        currentCount++;
        if (isEndOfLine()) return true;
        if (messagesOn && isDot() ) System.out.print(DOT);
        return false;
    }
    
    public int getCount(){return currentCount;}
    
    /**
     * Test to see if need a dot.
     * @return true if need a dot
     */
    public boolean isDot(){if (currentCount%dotInterval==0) return true; else return false;}
    /**
     * Test to see if end of line.
     * @return true if end of line reached.
     */
    public boolean isEndOfLine(){if ((currentCount%newLineInterval==0) || currentCount==totalCount) return true; else return false;}
    /**
     * Test to see if finished.
     * @return true if finished count.
     */
    public boolean isFinished(){if (currentCount<totalCount) return false; else return true;}
}
