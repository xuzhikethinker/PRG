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
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * University Organisation.
 * <p>The top of the tree is level 0 (faculty), 
 * below is department (level 1) and  the bottom is the section
 * (level <tt>levelName.length</tt>).
 * <p>Partially written for generalisation to fdifferent numbers of levels.
 * @author time
 */
public class UniversityOrganisation {

    /**
     * Names of the different levels.  Level 0 is the top of the tree.
     */
    public final static String [] levelName={"Faculty","Department","Section"};
    /**
     * Plural of Names of the different levels.  Level 0 is the top of the tree.
     */
    public final static String [] levelNamePlural={"Faculties","Departments","Sections"};
    final static int NUMBERLEVELS=levelName.length;
    /**
     * String used to indicate no unit has been assigned.
     * <p>This must be the first entry in a TreeSet so must 
     * start with an ASCII character whose code is less than that of A.
     */
    final static String NOUNIT="(NO UNIT)";
    /**
     * String used in a file name to indicate no unit has been assigned.
     * <p>This must be the first entry in a TreeSet so must 
     * start with an ASCII character whose code is less than that of A.
     */
    final static String NO_UNIT="(NO_UNIT)";
    TreeSet<String> facultySet;
    TreeSet<String> departmentSet;
    TreeSet<String> sectionSet;
    ArrayList<String> faculty;
    ArrayList<String> department;
    ArrayList<String> section;
    boolean fixedSections=false;
    /**
     * Maximum index number +1.
     */
    int nMax=-1;
    
    int infoLevel=0;
    
    public UniversityOrganisation(){
        // make sure first in set is NOUNIT
    facultySet = new TreeSet();
    departmentSet = new TreeSet();
    sectionSet = new TreeSet();
    facultySet.add(NOUNIT);
    departmentSet.add(NOUNIT);
    sectionSet.add(NOUNIT);
    }
    
    /**
     * Cleans up names of units.
     * <p>Removes characters at start and end which come before A.
     * If no dept is given then faculty name is used to fill in the dept 
     * provided there is a section name.
         * This must be coordinated with the <tt>getUnit</tt> routine.
     * <p>TODO would like this to remove most non-alphanumeric characters?
     * @param newfaculty faculty name
     * @param newdepartment department name
     * @param newsection section name
     */
    public void addSection(String newfaculty, String newdepartment, String newsection){
        if (fixedSections) throw new RuntimeException("Sections fixed, can not add more sections");

        String sec = cleanUp(newsection);
        sectionSet.add(sec);

        String fac = cleanUp(newfaculty);
        facultySet.add(fac);

        String dept = cleanUp(newdepartment);
        if (dept.equals(NOUNIT) && !sec.equals(NOUNIT) ) dept=fac; 
        departmentSet.add(dept);
    }
    
    /**
     * Cleans up input.
     * <p>If the input string is null then section is set to no unit.
     * If it starts, or starts and ends with characters that come 
     * before A then these are stripped off.
     * @param input string to be cleaned
     * @return cleaned up string
     */
    static public String cleanUp(String input){
        if (input.length()==0 || input.equals("")) return NOUNIT;
        if (input.charAt(0)<'A') {
            if (input.charAt(input.length()-1)<'A') return input.substring(1,input.length()-1);
            else return input.substring(1);
        }
        return input;
    }
    
    /**
     * Converts unit names into strings suitable for file names.
     * <p>Uses cleanUp routine then replaces spaces with underscores 
     * and ampersands with "and".
     * @param input string to be cleaned
     * @return string suitable for filenames
     */
    static public String toFileName(String input){
        String fn = cleanUp(input);
        fn = fn.replace(" ", "_"); // remove spaces
        fn = fn.replace("&", "and"); // remove ampersands
        return fn;
    }
    
    public void finishSetUp(){
       fixedSections=true;
       nMax=sectionSet.size()*departmentSet.size()*facultySet.size();
       faculty = new ArrayList<String>(facultySet);
       // unclear if we have made a deep copy.  If not we can not remove the sets
       department = new ArrayList<String>(departmentSet);
       section = new ArrayList<String>(sectionSet);
    }
    
    /**
     * Returns number in each level (0 = top of tree, faculty)
     * @param level level (0 = top of tree, faculty)
     * @return number of units at this level (unit 0 is NOUNIT), -1 if level not known
     */
    public int getNumber(int level){
    switch (level)
         {
             case 0: return faculty.size(); 
             case 1: return this.department.size();
             case 2: return section.size();
     }
     return -1;
    }
    
    /**
     * Gives indexes at each level from global unit index.
     * @param n global index of unit
     * @return array of integers [L]= index of level L unit
     */
    public int [] unitIndices(int n){
        if (n<0) throw new RuntimeException("Section index must not be negative");
        if (n>=nMax) throw new RuntimeException("Section index too large, maximum is "+nMax);

        int [] s= new int[NUMBERLEVELS];
        int nsection=n%section.size();
        s[2]=nsection;
        
        int nd=n/section.size();
        int ndepartment=nd%department.size();
        s[1]=ndepartment;
        
        int nf=nd/department.size();
        int nfaculty=nf%department.size();
        s[0]=nfaculty;
        return s;        
    }
    
    /**
     * Names of given level.
     * @param level number of level
     * @return name of level
     */public String getLevelName(int level){
        if ((level<0 || level>=NUMBERLEVELS)) throw new RuntimeException("Level must not be negative or less than "+NUMBERLEVELS+", given level "+level);
        return levelName[level];
    }
    
    /**
     * Plural of Names of given level.
     * @param level number of level
     * @return name of level
     */public String getLevelNamePlural(int level){
        if ((level<0 || level>=NUMBERLEVELS)) throw new RuntimeException("Level must not be negative or less than "+NUMBERLEVELS+", given level "+level);
        return levelNamePlural[level];
    }
    
    /**
     * Gives name of unit at one level from local index and level number.
     * @param n local index 
     * @param level level number required
     * @return name of the unit.
     */
    public String getNameFromLocalIndex(int n, int level){
    switch (level)
         {
             case 0: return faculty.get(n); 
             case 1: return department.get(n);
             case 2: return section.get(n);
         }
    throw new RuntimeException("Level must not be negative or less than "+NUMBERLEVELS+", given level "+level);
    }

    
    
    /**
     * Gives name of unit at one level from global index and level number.
     * @param ng global index 
     * @param level level number required
     * @return name of the unit.
     */
    public String getNameFromGlobalIndex(int ng, int level){
        int nl=getLocalIndexFromGlobal(ng,level);
        return getNameFromLocalIndex(nl,level);
    }

    /**
     * Returns name of unit
     * @param n global index of unit
     * @return array of names of each level in which unit is a member
     */
    public String [] getName(int n){
        if (n<0) throw new RuntimeException("Section index must not be negative");
        if (n>=nMax) throw new RuntimeException("Section index too large, maximum is "+nMax);
        int [] index = unitIndices(n);
        String [] s = new String [NUMBERLEVELS];
        for (int level=0; level<NUMBERLEVELS; level++) s[level]=getNameFromGlobalIndex(n,level); 
        return s;
    }
 
    /**
     * Returns name of unit
     * @param n global index of unit
     * @param maxLevel level down to which names should be specified
     * @return array of names of each level in which unit is a member
     */
    public String [] getName(int n, int maxLevel){
        if ((maxLevel<0) || (maxLevel>=NUMBERLEVELS)) throw new RuntimeException("Level must be between 0 and "+NUMBERLEVELS);
        if (n<0) throw new RuntimeException("Section index must not be negative");
        if (n>=nMax) throw new RuntimeException("Section index too large, maximum is "+nMax);
        int [] index = unitIndices(n);
        String [] s = new String [maxLevel+1];
        for (int level=0; level<maxLevel+1; level++) s[level]=getNameFromGlobalIndex(n,level); 
        return s;
    }
 

    /**
     * Gives local level index from global index.
     * @param n global index 
     * @param level level number required
     * @return local level index
     */
    public int getLocalIndexFromGlobal(int n, int level){
        if (n<0) throw new RuntimeException("Section index must not be negative");
        if (n>=nMax) throw new RuntimeException("Section index too large, maximum is "+nMax);
        if ((level<0) || (level>=NUMBERLEVELS)) throw new RuntimeException("Level must not be negative or less than "+NUMBERLEVELS+", given level "+level);
            
        int nl=n;
        for (int l=NUMBERLEVELS-1; l>level; l--){
            nl=nl/getNumber(l);
        }
        return nl%getNumber(level);
    }
    
    /**
     * Tests to see if given index is in specified unit.
     * @param nglobal global index of unit to be tested
     * @param nlocal local index of unit required.
     * @param level required level of unit.
     * @return if the global and local indices coincide returns true.
     */
    public boolean isInUnit(int nglobal, int nlocal, int level){
        int nl = getLocalIndexFromGlobal(nglobal,level);
        if (nl==nlocal) return true;
        return false;
    }

    public boolean testGlobalIndex(int n){
        if ((n<0) || (n>=nMax)) return false;
        return true;
    }
    
    /**
     * Gives factor needed to filter out lower levels.
     * <p>If factor is <tt>f</tt> then <tt>(unit id)/f</tt> (integer arithmetic)
     * is a unique id associated with levels 0 to <tt>level</tt>.
     * Alternatively <tt>((unit id)/f)*f</tt> gives a full id but with
     * all units below <tt>level</tt> being set to NOUNIT designation.
     * @param level factor needed to keep this level but to ignore higher levels.
     * @return factor needed.
     */
    public int getFilterFactor(int level){
        int f=1;
        for (int l=level+1; l<levelName.length; l++) f*= getNumber(l);
        return f;
    }
    
    /**
     * Returns integer representing the unit.
     * <p>Returns the section number which equals
     * <br><code>ns+section.size()*(nd+department.size()*nf)</code>.
     * @param faculty name of faculty
     * @param department name of department 
     * @param section name of section 
     * @return unit number, -1 if one given name is not found, -2 if array too short.
     */
    public int getUnitIndex(String faculty, String department, String section){
        String [] s= {faculty, department, section};
        return getUnitIndex(s);
    }
    
   /**
     * Returns integer representing the unit.
     * <p>Returns the global index which equals
     * <br><code>ns+section.size()*(nd+department.size()*nf)</code>
    * where <tt>ns</tt>, <tt>nd</tt>, <tt>nf</tt> are the local indices at each level
    * (levels 2,1, and 0 respectively).
     * <br>If have a section name but no dept name then faculty name is used.
     * This must be coordinated with the <tt>addSection</tt> routine.
     * @param s string array with names of untis with s[0] being the top of the tree (faculty)
     * @return unit number, -1 if one given name is not found, -2 if array too short.
     */
    public int getUnitIndex(String s[]){
        if (s.length==0) return -2;
        if (s.length>NUMBERLEVELS) throw new RuntimeException("Section level "+s.length+"too large, maximum level is "+NUMBERLEVELS);
        int nf = getSection(s[0],faculty);
        int nd = 0;
        if (s.length>0) nd=getSection(s[1],department);
        int ns = 0;
        if (s.length>1) ns=getSection(s[2],section);
        // if have section but no dept name
        if ((nd==0) && (ns>0)) nd=nf;
        if ((nf<0) || (nd<0) ||(ns<0)) return -1;
        return ns+section.size()*(nd+department.size()*nf);
    }
    
    /**
     * Returns the index of string s in given ArrayList.
     * <p>Must be equal but ignores case.  Uses cleanUp routine to strip
     * unwanted characters out of input.
     * @param s string to serach for.
     * @param array ArrayList to be searched
     * @return index of s in array or -1 if not found.
     */
     private int getSection(String s, ArrayList<String> array){
       String s2=cleanUp(s);  
       for (int n=0; n<array.size(); n++) if (array.get(n).equalsIgnoreCase(s2)) return n;
       return -1;
     }
     
     /**
      * Basic information, taken from set structures, as a string
      * @param sep separation string
      * @return string of basic information
      */
     public String informationSetString(String sep){
         return "Number Faculties "+sep+facultySet.size()+sep+"Number Departments"+sep+departmentSet.size()+sep+"Number Sections "+sep+sectionSet.size();
     }
     /**
      * Basic information as a string
      * @param sep separation string
      * @return string of basic information
      */
     public String informationString(String sep){
         return "Number Faculties "+sep+faculty.size()+sep+"Number Departments"+sep+department.size()+sep+"Number Sections "+sep+section.size();
     }

     /**
      * Level names as string.
      * @param sep separation string
      * @return string containing the level names
      */
     public String levelNameString(String sep){
         String s=levelName[0];
         for (int l=1; l<levelName.length;l++) s= sep+levelName[l];
         return s;
     }

     /**
      * Lists names of given level of organisation.
      * @param PS PrintStream such as System.out
      * @param sep separation string
      * @param headerOn true for header line
      * @param levelName name of the level of this unit, nothing is given if empty string.
      */
     private void printNames(PrintStream PS, String sep, ArrayList<String> array, boolean headerOn, String levelName){
         if (headerOn) PS.println("Index"+sep+"Name"+(levelName.length()>0?sep+"Level":""));
         String levelString="";
         if (levelName.length()>0) levelString=sep+levelName;
         for (int n=0; n<array.size(); n++) PS.println(n+sep+array.get(n)+levelString);
     }

     /**
      * Lists names of given level of organisation.
      * @param PS PrintStream such as System.out
      * @param sep separation string
      * @param headerOn true for header line
      * @param levelOn true if want column with name of the level of the unit
      */
     public void printNames(PrintStream PS, String sep, int level, boolean headerOn, boolean levelOn){
         if (headerOn) PS.println("Index"+sep+"Name"+(levelOn?sep+"Level":""));
         switch (level)
         {
             case 0: printNames(PS, sep, faculty, headerOn,(levelOn?"faculty":"")); return;
             case 1: printNames(PS, sep, department, headerOn,(levelOn?"department":"")); return;
             case 2: printNames(PS, sep, section, headerOn,(levelOn?"section":"")); return;
     }
         throw new RuntimeException("level "+level+" unknown, must be between 0 and "+(NUMBERLEVELS-1));
     }

     /**
      * Lists names of given level of organisation.
      * @param PS PrintStream such as System.out
      * @param sep separation string
      * @param headerOn true for header line
      * @param levelOn true if want column with name of the level of the unit
      */
     public void printAllNames(PrintStream PS, String sep, boolean headerOn, boolean levelOn){
         if (headerOn) PS.println("Index"+sep+"Name"+(levelOn?sep+"Level":""));
         for (int l=0; l<NUMBERLEVELS; l++) printNames(PS,  sep, l, false, levelOn);
     }

     /**
      * Outputs list of all papers.
      * <p>This will be in a <tt>organisation.dat</tt> file.
      * @param fns name of file, endingf will be changed to "sections.dat"
      * @param sep separation string
      * @param headerOn true for header line
      * @param levelOn true if want column with name of the level of the unit
      */
     public void outputFile(FileNameSequence fns, String sep, boolean headerOn, boolean levelOn) 
{
        fns.setNameEnd("organisation.dat");
        outputFile(fns.getFullFileName(), sep, headerOn, levelOn);
    }   
    /**
     * Outputs list of all known units.
      * @param fullfilename full name of output file including directories
      * @param sep separation string
      * @param headerOn true for header line
      * @param levelOn true if want column with name of the level of the unit
      */
     public void outputFile(String fullfilename, String sep, boolean headerOn, boolean levelOn) 
{
        PrintStream PS;
        FileOutputStream fout;
        if (infoLevel > -2) {
            System.out.println("Writing list of all known units to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            
            printAllNames(PS,  sep, headerOn, levelOn);
            
            if (infoLevel > -2) {
                System.out.println("Finished writing list of all known units to " + fullfilename);
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
