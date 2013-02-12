/*
 * DataAnalysis.java
 *
 * Created on 12 June 2006, 12:09
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package DataAnalysis;

/**
 *
 * @author time
 */
public class DataAnalysis {
    
    /** Creates a new instance of DataAnalysis */
    public DataAnalysis() 
    {
    }
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("*** DataAnalysis ***");
        DataAverage DAV = new DataAverage();
        DataRead DR = new DataRead();
        LogBin LB = new LogBin();
        StatisticalQuantity SQ = new StatisticalQuantity();
        EventTimeSeries TS = new EventTimeSeries();
        IntegerEvent IE = new IntegerEvent ();
        
    }

}
