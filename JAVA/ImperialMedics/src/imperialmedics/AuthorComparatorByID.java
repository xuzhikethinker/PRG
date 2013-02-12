/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imperialmedics;

import java.util.Comparator;

/**
 * Use to compare authors by ID
 * @author time
 */
public class AuthorComparatorByID implements Comparator<Author>{

    public int compare(Author a1, Author a2) {
        return a1.compareToID(a2);
    }
}
