/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimUtilities.FileUtilities.FileNameSequence;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author time
 */
public class SetOfPapers {

    /**
     * Maps key=gid - global id of papers to value=instances of paper
     */
    TreeMap<String,Paper> paperMap;
    
    /**
     * Keeps track of number of distinct papers
     */
    int numberPapers=0;
    
    
    public SetOfPapers(){
        paperMap = new TreeMap();
    }
    
    int infoLevel=0;
    
    /**
     * Returns set of all papers.
     * @return Colelction of Papers
     */
    public Collection<Paper>  getPapers(){
        return paperMap.values();
    }
    /**
     * Gets paper of given global id.
     * @param gid global id (a string)
     * @return the paper required, or null if not present.
     */
    public Paper getPaper(String gid){
        return paperMap.get(gid);
    }
    

    /**
     * Returns an ordered set of the units used.
     * @return set of all units with at least one paper in order.
     */
    public TreeSet<Integer> getAllUnitsUsed(){
        TreeSet<Integer> allUnitsUsed= new TreeSet();
        for (Paper p:paperMap.values()) allUnitsUsed.addAll(p.getSections());
        return allUnitsUsed;
    }

    
    /**
     * Adds unit of given index tro list of those associated with paper.
     * <p>If paper already exists, no new paper is created. 
     * Likewise if paper laready in this section, nothing new is added.
     * @param gid
     * @param unitIndex
     */
    public void addUnitsToPaper(String gid, int unitIndex){
        Paper p=getPaper(gid);
        if (p==null) {
            p=new Paper(numberPapers++, gid,  unitIndex);
            paperMap.put(gid,p);
        }
        else p.addSection(unitIndex);
    }
    
    /**
     * Sorst all the sections in the paper
     */
    public void sortSections(){
        for (Paper p:paperMap.values()) p.sortSections();
    }
    
    /**
      * Lists all papers.
      * @param PS PrintStream such as System.out
      * @param sep separation string
      * @param headerOn true for header line
      * @param sectionsOn true if want section numbers printed
      */
     public void print(PrintStream PS, String sep, boolean headerOn, boolean sectionsOn){        
         Collection<Paper> papers = paperMap.values();
         if (headerOn) PS.println(Paper.toStringLabel(sep)+(sectionsOn?sep+"sections":""));
         for (Paper p:papers) PS.println(p.toString(sep)+(sectionsOn?sep+p.stringOfSections(sep):""));
    }
     
     /**
      * Outputs list of all papers.
      * <p>This is in a <tt>papers.dat</tt> file.
      * @param fns file name sequence.  The ending will be set to <tt>papers.dat</tt>
      * @param sep separation string
      * @param headerOn true for header line
      * @param sectionsOn true if want section numbers printed
      */
     public void outputPapers(FileNameSequence fns, String sep, boolean headerOn, boolean sectionsOn) 
{
        fns.setNameEnd("papers.dat");
        outputPapers(fns.getFullFileName(), sep, headerOn, sectionsOn);
    }   
     /**
      * Outputs list of all papers.
      * @param fullfilename full name of output file including directories
      * @param sep separation string
      * @param headerOn true for header line
      * @param sectionsOn true if want section numbers printed
      */
     public void outputPapers(String fullfilename, String sep, boolean headerOn, boolean sectionsOn) 
{
        PrintStream PS;
        FileOutputStream fout;
        if (infoLevel > -2) {
            System.out.println("Writing list of all known units to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            
            print(PS,  sep, headerOn, sectionsOn);
            
            if (infoLevel > -2) {
                System.out.println("Finished writing list of all known papers to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;
    }

     /**
      * Outputs gid-reference-citation list of papers in given collection.
      * <p>This is in a <tt>grc.dat</tt> file.
      * @param fns file name sequence.  The ending will be set to <tt>grc.dat</tt>
      * @param sep separation string
      * @param papers required in output
      * @param headerOn true for header line
      * @param infoOn true if want info on screen
      */
     static public void outputGRCList(FileNameSequence fns, String sep,  Collection<Paper> papers, 
             boolean headerOn, boolean infoOn) 
{
        fns.setNameEnd("grc.dat");
        outputGRCYList(fns.getFullFileName(), sep, papers, headerOn, false, infoOn);
    }   
     /**
      * Outputs gid-reference-citation(-year list of papers in given collection.
      * <p>This is in a <tt>grc.dat</tt> file or <tt>grcy.dat</tt> file if year on.
      * @param fns file name sequence.  The ending will be set.
      * @param sep separation string
      * @param papers required in output
      * @param headerOn true for header line
      * @param yearOn include year as column 4
      * @param infoOn true if want info on screen
      */
     static public void outputGRCYList(FileNameSequence fns, String sep,  Collection<Paper> papers, 
             boolean headerOn, boolean yearOn, boolean infoOn) 
{
        fns.setNameEnd("grc"+(yearOn?"y":"")+".dat");
        outputGRCYList(fns.getFullFileName(), sep, papers, headerOn, yearOn, infoOn);
    }   
     /**
      * Outputs gid-reference-citation(-year) list of all papers.
      * <p>This is in a <tt>grc.dat</tt> file or <tt>grcy.dat</tt> file if year on.
      * @param fns file name sequence.  The ending will be set.
      * @param sep separation string
      * @param headerOn true for header line
      * @param yearOn include year as column 4
      * @param infoOn true if want info on screen
      */
     public void outputAllGRCYList(FileNameSequence fns, String sep,  boolean headerOn, boolean yearOn, boolean infoOn) 
{
        fns.setNameEnd("grc"+(yearOn?"y":"")+".dat");
        outputGRCYList(fns.getFullFileName(), sep, paperMap.values(), headerOn, yearOn, infoOn);
    }

         /**
      * Outputs gid-reference-citation(-year) list of papers in given collection.
      * @param fullfilename full name of output file including directories
      * @param sep separation string
      * @param papers required in output
      * @param headerOn true for header line
      * @param yearOn include year as column 4
      * @param infoOn true if want info on screen
      */
     static public void outputGRCYList(String fullfilename, String sep, Collection<Paper> papers, boolean headerOn, boolean yearOn, boolean infoOn) 
{
        PrintStream PS;
        FileOutputStream fout;
        if (infoOn) {
            System.out.println("Writing gid-reference-citation"+(yearOn?"-year":"")+" list to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            
            if (headerOn) PS.println("gid"+sep+"References"+sep+"Citations"+(yearOn?sep+"year":""));
            
            for (Paper p:papers) PS.println(p.gid+sep+p.getReferences()+sep+p.getCitations()+(yearOn?sep+p.getYear():""));
            
            if (infoOn) {
                System.out.println("Finished gid-reference-citation"+(yearOn?"-year":"")+" list to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;
    }
 
     
     /**
      * Makes a bipartite vertex neighbour list of strings.
      * <p>This is in the <tt>inputBVNLS.dat</tt> format where
      * each line is given as paper section section section ...
      * <p>The sections are given by (full idnumber)/factor
      * so that we can project out different levels.
      * @param fns file name sequence.  The ending will be set to "inputBVNLS.datt"
      * @param sep separation string
      * @param factor divide unit id numbers by this factor
      */
    public void makeGraphEdgeList(FileNameSequence fns, String sep, int factor) 
{   
        fns.setNameEnd("inputBVNLS.dat");
        makeGraphEdgeList(fns.getFullFileName(), sep, factor);
    }   
     /**
      * Makes a bipartite vertex neighbour list of strings.
      * <p>This is in the <tt>inputBVNLS.dat</tt> format where
      * each line is given as paper section section section ...
      * <p>The sections are given by (full idnumber)/factor
      * so that we can project out different levels.
      * @param fullfilename full file name including directories and extensions
      * @param sep separation string
      * @param factor divide unit id numbers by this factor
      */
    public void makeGraphEdgeList(String fullfilename, String sep, int factor) 
{      
        PrintStream PS;
        FileOutputStream fout;
        if (infoLevel > -2) {
            System.out.println("Writing bipartite vertex neighbour list of strings, paper-section, to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            
            for (Paper p:paperMap.values()) {
              int na = p.getNumberAuthors();
              if (na==0) continue;
              PS.println(p.gid+sep+p.stringOfSections(sep, factor));
              
            }

            if (infoLevel > -2) {
                System.out.println("Finished bipartite vertex neighbour list of strings, paper-section, to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;
        
    }

    
   
}
