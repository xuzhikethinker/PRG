/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

/**
 *
 * @author time
 */
public class Journal implements Comparable<Journal> {

    /**
     * ISSN string for journal.
     * null or empty string if not set.
     */
    String ISSN="";

    /**
     * Data set ID (Scopus Sourcerecord id) string for journal.
     * null or empty string if not set.
     */
    String did="";

    /**
     * Title for journal
     * null or empty string if not set.
     */
    String title="";

    /**
     * String unset constant
     */
    final static String SUNSET="UNSET";

    /**
     * Empty constructor.
     * Needed for extends.
     */
    protected Journal(){    }
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
     *
     * @param title journal title
     * @param ISSN ISSN code of journal as string
     * @param did ID string for journal from data set (Scopus Sourcerecord id).
     */
    public Journal(String title, String ISSN, String did){
        this.title=title;
        this.ISSN=ISSN;
        this.did=did;
    }

    /**
     * Set journal title.
     * @param title journal title
     */
    public void setTitle(String title){this.title=title;}
    /**
     * Test for Set journal title.
     * @return true if title not empty string
     */
    public boolean hasTitle(){return !title.isEmpty();}
    /**
     * Set ISSN
     * @param ISSN ISSN code of journal as string
     */
    public void setISSN(String ISSN){this.ISSN=ISSN;};
    /**
     * Test for ISSN
     * @return true if ISSN not empty string
     */
    public boolean hasISSN(){return !ISSN.isEmpty();};
    /**
     * Set data setID of journal
     * @param did Data ID of journal
     */
     public void setDataID(String did){this.did=did;}
   /**
     * test for data set ID string.
     * @return true data set ID string is not empty
     */
     public boolean hasDataID(){return !did.isEmpty();}

    /**
     * Compares two journals.
     * First by ISSN if both journals have it.
     * Next by data source ID if both journals have it.
     * Lastly by title.
     * Negative if this journal comes before argument otherJournal.
     * Zero if equal.
     * @param otherJournal
     * @return compareTo results
     * @see #compareToISSNwithoutLeadingZeros(ebrp.Journal)
     * @see #compareToDataID(ebrp.Journal)
     * @see #compareToTitle(ebrp.Journal)
     */
    public int compareTo(Journal otherJournal){
        if (hasISSN() && otherJournal.hasISSN()) return compareToISSNwithoutLeadingZeros(otherJournal);
        //if (hasDataID()&& otherJournal.hasDataID()) return compareToDataID(otherJournal);
        return 0;
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
     * Compares two journals using data id strings.
     * Ignores case by using standard String compareToIgnoreCase result.
     * @param otherJournal other journal
     * @return standard String compareToIgnoreCase result.
     */
    public int compareToDataID(Journal otherJournal){
        return did.compareToIgnoreCase(otherJournal.did);
    }

    /**
     * Compares two journals by title.
     * Ignores case by using standard String compareToIgnoreCase result.
     * @param otherJournal
     * @return tests title ignoring case.
     */
    public int compareToTitle(Journal otherJournal){
        return title.compareToIgnoreCase(otherJournal.title);
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
        int r=-7537960;
        try {
            r=compareTo((Journal) otherJournal);
        }
        catch(RuntimeException e){return false;}
        return (r==0);
    }

    @Override
    public String toString(){
        return title+(hasISSN()?"(ISSN"+this.ISSN+")":(hasDataID()?"(did"+this.did+")":""));
    }
}
