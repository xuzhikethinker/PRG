/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

import cern.colt.list.IntArrayList;

/**
 *
 * @author time
 */
public class IntArrayUtilities {

    /**
     * Adds value to the element at index of a CERN colt <tt>IntArrayList</tt>.
     * <p>Adds value to the element at index extending with zero entries
     * or starting list if necessary
     * @param ial IntArrayList
     * @param index the index in the IntArrayList
     * @param value value to set:  ial[index]=value
     */
    static void addExtendIntArrayList(IntArrayList ial, int index,  int value)  
    {
        int size = ial.size();
        if ((index==0) && (size==0)) {ial.add(value); return;}
        for (int i=size; i<=index; i++) ial.add(0);
        ial.set(index, ial.get(index)+value);          
    }

    
}
