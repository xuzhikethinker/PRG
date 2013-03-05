/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imperialmedics;

/**
 *
 * @author time
 */
public class PeriodBoundary {

    /**
     * Array to specify boundaries between cells.
     * Period 0 is year &lt; PeriodBoundary.yearBoundary[0],
     * Period L=PeriodBoundary.yearBoundary.length is year &gt; PeriodBoundary.yearBoundary[L-1],
     * Otherwise Period P is PeriodBoundary.yearBoundary[P-1] &lt; year &lt; PeriodBoundary.yearBoundary[P].
     * Vanash requires 2001-2003, 2004-2006, 2007-2009
     */
    final static double [] yearBoundary = {2000.5, 2003.5, 2006.5, 2009.5};

    /**
     * Provide a descriptive string of intervals defined.
     * @return descriptive string
     */
    static String description(){
        String s="(.-"+((int) Math.floor(yearBoundary[0]))+")";
                for (int p=0; p<(yearBoundary.length-1); p++){
                    s=s+", ("+((int) Math.ceil(yearBoundary[p]))
                            +"-"+((int) Math.floor(yearBoundary[p+1]))+")";
                    }
                s=s+", ("+((int) Math.ceil(yearBoundary[yearBoundary.length-1]))
                            +"-.)";
        return s;
    }
    /**
     * Number of separate periods.
     * Includes before and after last dates as separate periods.
     * @return  number of periods
     */
    static public int getNumberOfPeriods(){return yearBoundary.length+1;}
    
}
