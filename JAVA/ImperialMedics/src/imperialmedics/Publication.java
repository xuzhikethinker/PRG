/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imperialmedics;

import TimGraph.run.ImperialPapers.Paper;
import java.util.ArrayList;

/**
 *
 * @author time
 */


public class Publication extends Paper {


    /**
     * Title of publication.
     */
    String title="";

    /**
     * Journal of publication.
     * Title and ISSN set
     */
    Journal journal;

    /**
     * List of authors in order on publication.
     */
    ArrayList<Author> authorList;

    public void setTitle(String t){title=t;}
    public String getTitle(){return title;}
    public String getShortTitle(int length){
        int l=Math.min(length, title.length());
        return String.format("%"+length+"s",title.substring(0, l));
    }

    public void setJournal(String journalTitle, String ISSN){
        journal=new Journal(journalTitle, ISSN);
    }
    public Journal getJournal(){return journal;}

    /**
     * Set list of authors from given list.
     * List in order on publication.
     * @param aList
     */
    public void setAuthorList(ArrayList<Author> aList){
        authorList=aList;
    }

    /**
     * Set list of authors from string.
     * List in order on publication separated by given character.
     * @param aLists string of authors separated by sep
     * @param sep string separating authors
     * @see Author#authorList(java.lang.String, java.lang.String)
     */
    public void setAuthorList(String aList, String sep){
        authorList=Author.authorList(aList, ",");
    }
    public boolean hasAuthorList(){
        return(authorList==null?false:true);
    }

    /**
     * Number of authors.
     * Negative if list not set.
     * @return number of authors
     */
    public int numberOfAuthors(){return (authorList==null?IUNSET:authorList.size());}

    /**
     * Tests to see if authors in alphabetical order
     * @return
     */
    public boolean isAlphabeticalOrder(){
        return Author.isAlphabeticalOrder(authorList);
    }

}
