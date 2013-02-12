/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Class to define the characteristics of a paper.
 * @author time
 */
public class Publication implements Comparable<Publication> {

     /**
     * Negative integer used to indicate unset value. {@value}
     */
    public static final int IUNSET=-912345687;

    public static final String SUNSET="UNSET";



    /**
     * Internal id of paper as integer.
     * <p>negative means unset
     */
    int id=IUNSET;
    
    /**
     * External id of paper as string.
     * empty string means unset
     */
    String eid="";
    
    /**
     * Year of publication
     */
    int year = IUNSET;

    /**
     * Month and day of paper
     */
    String date = "";

    /**
     * Number of references
     */
    int ref=IUNSET;
    
    /**
     * Number of citations
     */
    int cite=IUNSET;
    
    /**
     * Set of sections associated with this paper.
     * <p>Each author can have multiple sections and each paper has multiple papers.
     * This is a NOT a set so this also counts how many authors are in the list
     */
    ArrayList<Integer> sectionNumber;

    protected Publication(){}

    public Publication(int id, String gid){
        this.id=id;
        this.eid=gid;
        sectionNumber = new ArrayList(); //TreeSet();
    }
    
    public Publication(int id, String gid, int section){
        this.id=id;
        this.eid=gid;
        sectionNumber = new ArrayList(); //TreeSet();
        addSection(section);
    }
    
    public void setInternalID(int i){id=i;}
    public void setExternalID(String i){eid=i;}
    public void setReferences(int r){ref=r;}
    public void setCitations(int c){cite=c;}
    public void setYear(int y){year=y;}
    public void setDate(String d){date=d;}
    public void setDateToDefault(){
        if (hasYear()) date=year+"0101";
        else date=SUNSET; 
    }
    public int getInternalID(){return id;}
    public String getExternalID(){return eid;}
    public int getReferences(){return ref;}
    public int getCitations(){return cite;}
    public int getYear(){return year;}
    public String getDate(){return date;}
    public int getNumberAuthors(){return sectionNumber.size();}
    public Collection<Integer> getSections(){return sectionNumber;}
        
    public void addSection(int n){sectionNumber.add(n);}

    public boolean hasYear(){return (year==IUNSET?false:true);}

    /**
     * Sorts the section numbers
     */
    public void sortSections(){
        Collections.sort(sectionNumber);
    }

    /**
     * Tests to see if paper is in given section.
     * @param n number of sections
     * @return true if paper is in section
     */public boolean isInSection(int n){
        return sectionNumber.contains(n);
    }
    
    public String toString(String sep){return id+sep+eid+sep+year+sep+date+sep+getNumberAuthors()+sep+cite+sep+ref;}
    static public String toStringLabel(String sep){return "id"+sep+"eid"+sep+"year"+sep+"date"+sep+"no.authors"+sep+"cite"+sep+"ref";}

    public String stringOfSections(String sep){
        return stringOfSections(sep, 1);
    }
    public String stringOfSections(String sep, int factor){
        if (sectionNumber.isEmpty()) return "";
        String s="";
        for (Integer i:sectionNumber){s=s+(s.length()>0?sep:" ")+(i/factor); }
        return s;
    }
    /**
     * Compares two publications using external id string.
     * @param otherPublication other publication
     * @return tests external id
     * @see #compareToExternalID(ebrp.Publication)
     */
    public int compareTo(Publication otherPublication){
        return compareToExternalID(otherPublication);
    }

    /**
     * Compares two publications using external id string.
     * Ignores case by using standard String compareToIgnoreCase result.
     * @param otherPublication other publication
     * @return standard String compareToIgnoreCase result.
     */
    public int compareToExternalID(Publication otherPublication){
        return eid.compareToIgnoreCase(otherPublication.eid);
    }


    }
