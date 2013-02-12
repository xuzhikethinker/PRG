/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

import java.util.Comparator;

/**
 * Use to compare authors by ID
 * @author time
 */
public class JournalComparatorByDataID implements Comparator<Journal>{

    public int compare(Journal j1, Journal j2) {
        return j1.compareToDataID(j2);
    }
}
