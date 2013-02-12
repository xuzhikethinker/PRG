/*
 * IntegerEvent.java
 *
 * Created on 12 June 2006, 17:35
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package DataAnalysis;


import cern.colt.list.IntArrayList;

import java.io.*;
import java.util.Date;


/**
 * Counts integer events
 * @author time
 */
public class IntegerEvent {

    String SEP="\t";
    String IEVersion = "IE060612";
    
    IntArrayList eventFreq;
    private int totalEvents;
    private int totalSize;
    private int infolevel =1;
    boolean upDateStatistics =true;
    
    /** Creates a new instance of IntegerEvent */
    public IntegerEvent() {
        eventFreq = new IntArrayList();
        totalEvents=0;
        totalSize =0;
        upDateStatistics = false;
    }

// -----------------------------------------------------------------------       
    /**
     * Adds value to the element at index.
     * Adds value to the element at index extending with zero entries
     * or starting list if necessary
     * @param index
     * @param value to set:  eventFreq[index]=value
     */
    public void add(int index,  int value)  
    {
        upDateStatistics =true;
        int size = eventFreq.size();
        // *** SURELY WRONG only initialise ial if zero size.
        if ((index==0) && (size==0)) {eventFreq.add(value); return;};
        for (int i=size; i<=index; i++) eventFreq.add(0);
        eventFreq.set(index, eventFreq.get(index)+value);
        return;
                  
    }

    // -----------------------------------------------------------------------       
    /**
     * Returns total size of all events (E).
     * Updates statistics if necessary.
     * @return total size (E)
     */
    public int getTotalSize()  
    {
        if (upDateStatistics) calcStatistics();
        return totalSize;                  
    }

    /**
     * Returns total number of events (N).
     * Updates statistics if necessary.
     * @return number of events (N)
     */
    public int getTotalEvents()  
    {
        if (upDateStatistics) calcStatistics();
        return totalEvents;                  
    }

    
// -----------------------------------------------------------------------       
    /**
     * Adds value to the element at index.
     * Adds value to the element at index extending with zero entries
     * or starting list if necessary
     * @param index
     * @param value to set:  eventFreq[index]=value
     */
    private void calcStatistics()  
    {
        totalEvents=0;
        totalSize=0;
        int n=0;
            for (int k=0; k<eventFreq.size(); k++)
            {
                n = eventFreq.get(k);
                totalEvents +=n;
                totalSize+=n*k;
            }
        upDateStatistics=false;
}  
 // ----------------------------------------------------------------------

     /**
     * Outputs a event count list to a file.
     * The global eventFreq is an IntArrayList of n(k) number of events of size k.
     * @param full filename name of file as string including any directories.
     * @param cc comment characters put at the start of every line
     * @param true if you want to skip empty bins
     */
    void FileOutputIntegerCountData(String fullfilename, String cc, boolean skipZeros)
    {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing integer count data to "+fullfilename);
            Date date = new Date();
            calcStatistics();
            PS.println(cc+"Event Size k"+ SEP +  "Event Count n(k) " +SEP+" Total Events (N) ="+totalEvents+SEP+" Total Size (E)="+totalSize+SEP+" Max Event Size ="+ (eventFreq.size()-1)  + SEP + IEVersion+ SEP + " "+date);
            int n;
            // Now output event count
            for (int k=0; k<eventFreq.size(); k++)
            {
                n = eventFreq.get(k);
                if (skipZeros && (n==0) ) continue;
                PS.println(k+SEP+ n);
            }
            
            try{
//                System.out.println("Trying to close "+filenamefull);
                fout.close();
                } catch (IOException e) { System.out.println("File Close Error "+fullfilename);}
            } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+fullfilename);
        } //eo catch
        
            if (infolevel>0) System.out.println("\n Finished Log Bin Output to "+fullfilename);
      } //eo FileOutputIntegerCountData
    

    
    
}
