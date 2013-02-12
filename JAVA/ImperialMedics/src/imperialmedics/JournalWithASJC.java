/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imperialmedics;

import java.util.ArrayList;

/**
 *
 * @author time
 */
public class JournalWithASJC extends Journal  {

    /**
     * list of classes
     */
    ArrayList<Integer> ASJCList;

    /**
     * Defines journal without ASJC numbers.
     * @param title
     * @param ISSN
     */
    public JournalWithASJC(String title, String ISSN){
        this.title=title;
        this.ISSN=ISSN;
    }
    /**
     * Defines journal with ASJC numbers.
     * Sets class numbers from string of integers.
     * Each class number is separated by character given
     * @param title journal title
     * @param ISSN ISSN number, perhaps with leading zeros missing
     * @param classString list of class numbers separated by classSep character
     * @param classSep regex separation string e.g. escaped tab \\t
     */
    public JournalWithASJC(String title, String ISSN, String classString, String classSep){
        this.title=title;
        this.ISSN=ISSN;
        setASJCNumbers(classString, classSep);
    }

    /**
     * Sets class numbers from string of integers.
     * Each class number is separated by character given
     * @param classString list of class numbers separated by classSep character
     * @param classSep regex separation string e.g. escaped tab \\t
     */
    public void setASJCNumbers(String classString, String classSep){
        String [] classArray = classString.split(classSep);
        ASJCList = new ArrayList();
        for (int c=0; c<classArray.length; c++){
            try {
                //int i=Integer.pparseInt("1");
                //int i=Integer.parseInt(classArray[c]);
                if (classArray[c].length()>0) ASJCList.add(Integer.parseInt(classArray[c]));
            } catch (RuntimeException e)
            {}
        } // for c
    }

//    /**
//     * Compares two journals using all initials possible.
//     * @param otherJournal
//     * @return tests surname and all initials.
//     * @see #compareToISSN(imperialmedics.Journal)
//     */
//    public int compareTo(JournalWithASJC otherJournal){
//        return compareToISSNwithoutLeadingZeros(otherJournal);
//    }


//    /**
// * Tests for equality of two journals.
// * This only tests surname and initials.
// * If list of initials is different does not compares those in the longer list
// * beyond the number in the shorter list
// * @param otherJournal
// * @return
// */
//    @Override
//    public boolean equals(Object otherJournal){
//        return true;
//    }

//    @Override
//    public String toString(){
//        return title+"("+this.ISSN+")";
//    }
}
