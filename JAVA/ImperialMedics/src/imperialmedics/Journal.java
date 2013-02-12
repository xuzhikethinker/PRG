/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imperialmedics;

/**
 *
 * @author time
 */
public class Journal implements Comparable<Journal> {

    /**
     * ISSN string for journal.
     */
    String ISSN;

    /**
     * Title for journal
     */
    String title;

    /**
     * String unset constant
     */
    final static String SUNSET="UNSET";

    /**
     * Empty constructor.
     * Needed for extends.
     */
    protected Journal(){

    }
    /**
     *
     * @param title
     * @param ISSN
     */
    public Journal(String title, String ISSN){
        this.title=title;
        this.ISSN=ISSN;
    }

    /**
     * Compares two journals using all initials possible.
     * @param otherJournal
     * @return tests surname and all initials.
     * @see #compareToISSN(imperialmedics.Journal)
     */
    public int compareTo(Journal otherJournal){
        return compareToISSNwithoutLeadingZeros(otherJournal);
    }

    /**
     * Compares two journals using ISSN strings.
     * Ignores case by using standard String compareToIgnoreCase result.
     * @param otherJournal other journal
     * @return standard String compareToIgnoreCase result.
     */
    public int compareToISSN(Journal otherJournal){
        return ISSN.compareToIgnoreCase(otherJournal.ISSN);
    }

    /**
     * Compares two journals using ISSN strings.
     * This only tests using ISSN ignoring leading zeros and differences in case.
     * Ignores case by using standard String compareToIgnoreCase result.
     * @param otherJournal other journal
     * @return standard String compareToIgnoreCase result.
     */
    public int compareToISSNwithoutLeadingZeros(Journal otherJournal){
        return stringLeadingZeros(ISSN).compareToIgnoreCase(stringLeadingZeros(otherJournal.ISSN));
    }

    /**
     * Compares two journals using ISSN strings.
     * Ignores case by using standard String compareToIgnoreCase result.
     * @param otherJournal other journal
     * @return standard String compareToIgnoreCase result.
     */
    static public String stringLeadingZeros(String s){
        return s.replaceFirst("0+", "");
    }

    /**
 * Tests for equality of two journals.
 * This only tests ISSN ignoring leading zeros and differences in case.
 * @param otherJournal
 * @return
 */
    @Override
    public boolean equals(Object otherJournal){
        Journal j = (Journal) otherJournal;
        return stringLeadingZeros(ISSN).equalsIgnoreCase(stringLeadingZeros(j.ISSN));
    }

    @Override
    public String toString(){
        return title+"("+this.ISSN+")";
    }
}
