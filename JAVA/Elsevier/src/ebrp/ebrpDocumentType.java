/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

import java.util.HashMap;

/**
 *
 * @author time
 */
public class ebrpDocumentType {

    HashMap<String,Integer> typeToIndex;
    static final String [] typeArray = {"ar","cp","re","un"}; // must be alphabetical, last is unknown type
    static final String [] typeLongArray = {"article","proceedings","review","unknown"}; // same order as above


    public ebrpDocumentType(){
        Integer index=0;
        for (String t: typeArray) typeToIndex.put(t, index++);
    }

    private void setTypeToIndexMap(){
        Integer index=0;
        for (String t: typeArray) typeToIndex.put(t, index++);
    }
    public boolean isDocumentType(String shortString){
        if (shortString.length()<2) return false;
        return typeToIndex.containsKey(shortString);
    }

}
