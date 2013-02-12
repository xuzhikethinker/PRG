/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run.ImperialPapers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Class to define the characteristics of a paper.
 * @author time
 */
public class Paper extends Object {

     /**
     * Negative integer used to indicate unset value. {@value}
     */
    public static final int IUNSET=-912345687;
    
    /**
     * Internal id of paper.
     * <p>IUNSET value means unset
     * @see #IUNSET
     */
    int id=IUNSET;
    
    /**
     * External name of paper.
     * Empty string indicates unset value.
     */
    String gid="";
    
    /**
     * Year of publication
     * <p>IUNSET value means unset
     * @see #IUNSET
     */
    int year = IUNSET;

    /**
     * Month and day of paper
     * Empty string indicates unset value.
     */
    String date = "";

    /**
     * Number of references
     * <p>IUNSET value means unset
     * @see #IUNSET
     */
    int ref=IUNSET;
    
    /**
     * Number of citations
     * <p>IUNSET value means unset
     * @see #IUNSET
     */
    int cite=IUNSET;
    
    /**
     * Set of sections associated with this paper.
     * <p>Each author can have multiple sections and each paper has mulitple papers.
     * This is a NOT a set so this also counts how many authors are in the list
     */
    ArrayList<Integer> sectionNumber;

    public Paper(){}
    
    public Paper(int id, String gid){
        this.id=id;
        this.gid=gid;
        sectionNumber = new ArrayList(); //TreeSet();
    }
    
    public Paper(int id, String gid, int section){
        this.id=id;
        this.gid=gid;
        sectionNumber = new ArrayList(); //TreeSet();
        addSection(section);
    }
    
    public void setReferences(int r){ref=r;}
    public void setCitations(int c){cite=c;}
    public void setYear(int y){year=y;}
    public void setDate(String d){date=d;}
    public int getReferences(){return ref;}
    public int getCitations(){return cite;}
    public int getYear(){return year;}
    public String getDate(){return date;}
    public int getNumberAuthors(){return sectionNumber.size();}
    public Collection<Integer> getSections(){return sectionNumber;}
        
    public void addSection(int n){sectionNumber.add(n);}
    
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
    
    public String toString(String sep){return id+sep+gid+sep+year+sep+date+sep+getNumberAuthors()+sep+cite+sep+ref;}
    static public String toStringLabel(String sep){return "id"+sep+"gid"+sep+"year"+sep+"date"+sep+"no.authors"+sep+"cite"+sep+"ref";}

    public String stringOfSections(String sep){
        return stringOfSections(sep, 1);
    }
    public String stringOfSections(String sep, int factor){
        if (sectionNumber.isEmpty()) return "";
        String s="";
        for (Integer i:sectionNumber){s=s+(s.length()>0?sep:" ")+(i/factor); }
        return s;
    }

    public boolean hasID(){return ((id==IUNSET)?false:true);}
    public boolean hasCite(){return ((cite==IUNSET)?false:true);}
    public boolean hasRef(){return ((ref==IUNSET)?false:true);}
    public boolean hasYear(){return ((year==IUNSET)?false:true);}
    public boolean hasSectionNumber(){return (( (sectionNumber==null) || sectionNumber.isEmpty() )?false:true);}
    public boolean hasExternalID(){return ((gid.isEmpty())?false:true);}
    
    }
