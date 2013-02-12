/*
 * TimIntegerArrayList.java
 *
 * Created on 15 November 2006, 16:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

import cern.colt.list.IntArrayList;

/**
 * Useful addition functions for the CERN colt library IntegerArrayList.
 * @author time
 */
public class TimIntegerArrayList {
    
    /** Creates a new instance of TimIntegerArrayList */
    public TimIntegerArrayList() {
    }
    
    // -----------------------------------------------------------------------       
    /**
     * adds value to the element at index extending with zero entries
     * or starting list if necessary
     * @param ial IntArrayList
     * @param index
     * @param value to set:  ial[index]=value
     */
    public void addExtendIntArrayList(IntArrayList ial, int index,  int value)  
    {
        int size = ial.size();
        // *** SURELY WRONG only initialise ial if zero size.
        if ((index==0) && (size==0)) {ial.add(value); return;};
        for (int i=size; i<=index; i++) ial.add(0);
        ial.set(index, ial.get(index)+value);
        return;
                  
    }

}
