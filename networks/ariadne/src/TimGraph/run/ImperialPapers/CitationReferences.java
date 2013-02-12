/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run.ImperialPapers;

import TimGraph.run.*;
import JavaNotes.TextReader;
import JavaNotes.TextReaderTabSeparated;
import TimGraph.io.FileInput;
import TimUtilities.FileUtilities.FileNameSequence;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Imperial Papers Citations and References Analysis
 * @author time
 */
public class CitationReferences {

    static final String SEP="\t";
    static String dirRoot = "/PRG/networks/timgraph/";

    UniversityOrganisation organisation;
    SetOfPapers setOfPapers;

    int infoLevel=0;
    FileNameSequence inputName;
    FileNameSequence outputName;




    public static void main(String[] args)
    {
      System.out.println("*** ImperialPapersCR");
      CitationReferences icr = new CitationReferences();
      
      boolean test=false;
      String nameroot="IC20090521";
      if (test) nameroot="ICtest20090521";
      
      icr.inputName = new FileNameSequence(dirRoot,"input/", nameroot,"");
      icr.outputName = new FileNameSequence(dirRoot,"output/", nameroot,"");
      
      icr.readPaperSectionFile(true);
      icr.readPaperSectionFile(false);

      icr.readPaperCitationsFile();

      boolean headerOn=true;
      boolean sectionsOn=true;
      boolean levelOn=true;

 //      icr.setOfPapers.print(System.out, SEP, headerOn, sectionsOn);
//      icr.organisation.printAllNames(System.out, SEP, headerOn, levelOn);
 
      icr.setOfPapers.outputPapers(icr.outputName, SEP, headerOn, sectionsOn);
      
      icr.organisation.outputFile(icr.outputName, SEP, headerOn, levelOn);
      
      icr.outputUnits(SEP, headerOn);
      
      icr.outputName.setNameRoot(nameroot+"_psec");
      icr.setOfPapers.makeGraphEdgeList(icr.outputName,SEP,1);
      
      icr.outputName.setNameRoot(nameroot+"_pdept");
      icr.outputUnits(SEP, headerOn,1);
      icr.setOfPapers.makeGraphEdgeList(icr.outputName,SEP, icr.organisation.getFilterFactor(1));
      
      icr.outputName.setNameRoot(nameroot);
      
      icr.makeAllGRCYList();
      icr.makeLevelGRCYList(0);
      icr.makeLevelGRCYList(1);

     }

    
    /**
     * Makes a set of GRC or GRCY files (gid-ref-cite-year lists), one for each unit at given level of organisation.
     * @param level level at which files are required.
     */
    public void makeLevelGRCYList(int level){
        String levelName=organisation.getLevelName(level);
        outputName.setDirectoryEnd("output/"+levelName+"/");
        outputName.makeDirectories();
        int nUnits=organisation.getNumber(level);
        for (int nl=1; nl<nUnits; nl++){
            HashSet<Paper> chosenPapers =makeUnitSubList(nl, level);
            boolean headerOn=true;
            boolean yearOn=true;
            boolean infoOn=true;
            // get name and remove spaces
            String name=organisation.getNameFromLocalIndex(nl, level);
            name=UniversityOrganisation.toFileName(name);
            outputName.setNameRoot(inputName.getNameRoot()+name);
            SetOfPapers.outputGRCYList(outputName, SEP, chosenPapers, headerOn, yearOn, infoOn);
        }
    }
    /**
     * Makes a set of GRC or GRCY files (gid-ref-cite-year lists), one for each unit at given level of organisation.
     */
    public void makeAllGRCYList(){
        outputName.setDirectoryEnd("output/");
        boolean headerOn=true;
        boolean yearOn=true;
        boolean infoOn=true;
        outputName.setNameRoot(inputName.getNameRoot()+"All");
        setOfPapers.outputAllGRCYList(outputName, SEP, headerOn, yearOn, infoOn);
    }
    
    /**
     * Returns set of papers which are in unit of given local index and level.
     * @param nlocal local index of required uit
     * @param level levl of required unit
     * @return set of papers satisfiying these conditions
     */
    public HashSet<Paper> makeUnitSubList(int nlocal, int level){
        HashSet<Paper> chosenPapers = new HashSet(); 
        Collection<Paper> papers = setOfPapers.getPapers();
        for (Paper p:papers){
             for ( Integer s: p.getSections()){
                 if (organisation.isInUnit(s, nlocal, level)) {
                     chosenPapers.add(p);
                     break;
                 }
             }             
        }
        return chosenPapers;
    }
    
    /**
      * Lists names of all levels of organisation with at least one paper.
      * @param PS PrintStream such as System.out
      * @param sep separation string
      * @param headerOn true for header line
      */
     public void printUnits(PrintStream PS, String sep, boolean headerOn, int level){        
        // First find all units assigned to at least one paper
        TreeSet<Integer> allFullUnitsUsed= setOfPapers.getAllUnitsUsed();
        
        if (headerOn) PS.println("index"+sep+organisation.levelNameString(sep));
        TreeSet<Integer> allUnitsUsed = new TreeSet();
        int f= organisation.getFilterFactor(level);
        if (f==1) allUnitsUsed=allFullUnitsUsed;
        else for (Integer n:allFullUnitsUsed) allUnitsUsed.add(n/f);
        for (Integer n:allUnitsUsed) {
             PS.print(n);
             String [] s=organisation.getName(n*f, level);
             for (int l=0;l<s.length; l++) PS.print(sep+s[l]);
             PS.println();
         }
    }
     
     /**
      * Outputs list of all units which have a paper.
      * <p>File name ends in <tt>UsedUnits.dat</tt>.
      * @param sep separation string
      * @param headerOn true for header line
      */
     public void outputUnits(String sep, boolean headerOn) 
{
         outputName.setNameEnd("UsedUnits.dat");
         outputUnits(outputName.getFullFileName(),  sep, headerOn, UniversityOrganisation.NUMBERLEVELS-1);
     }
     /**
      * Outputs list of all units at given level which have a paper.
      * <p>File name ends in <em>UnitName</em><tt>UsedUnits.dat</tt>.
      * @param sep separation string
      * @param headerOn true for header line
      * @param level name of units to this level only, lower levels ignored.
      */
     public void outputUnits(String sep, boolean headerOn, int level) 
{
         outputName.setNameEnd("UsedUnits.dat");
         outputUnits(outputName.getFullFileName(),  sep, headerOn, level);
     }
     
     /**
      * Outputs list of all units which have a paper.
      * @param fullfilename full name of output file including directories
      * @param sep separation string
      * @param headerOn true for header line
      * @param level name of units to this level only, lower levels ignored.
      */
     public void outputUnits(String fullfilename, String sep, boolean headerOn, int level) 
{
        PrintStream PS;
        FileOutputStream fout;
        if (infoLevel > -2) {
            System.out.println("Writing list of all "+organisation.getLevelNamePlural(level)+" used to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            
            printUnits(PS,  sep, headerOn, level);
            
            if (infoLevel > -2) {
                System.out.println("Finished writing list of all "+organisation.getLevelNamePlural(level)+" units used to " + fullfilename);
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
      * Reads in list of sections associated with each paper.
      * <p>Each line has gid of paper followed by three columns giving their faculty, department and research group.
      * <p>Filename is set by inputName and ends in <tt>sections.txt</tt>.
      * @param createOrganisation true if want to create organbisational structure first, otherwise set of papers created
      */
    public int readPaperSectionFile(boolean createOrganisation)
    {
        inputName.setNameEnd("sections.txt");
        return readPaperSectionFile(inputName.getFullFileName(), createOrganisation);
    }
        /**
      * Reads in list of sections associated with each paper.
      * <p>Each line has gid of paper followed by three columns giving their faculty, department and research group.
      * @param fullfilename full file name
      * @param createOrganisation true if want to create organbisational structure first, otherwise set of papers created
      */
    public int readPaperSectionFile(String fullfilename, boolean createOrganisation)
    {
        int maxColumns=4;
        if (createOrganisation) organisation = new UniversityOrganisation();
        else setOfPapers = new SetOfPapers();

        int res=0;  // error code.
        TextReaderTabSeparated data=FileInput.openTabSeparatedFile(fullfilename );
        if (data==null) return -1;
        if (res<0) return res;
        System.out.println("Starting to read paper section list from " + fullfilename+" and "+(createOrganisation?"creating orgainisation":"setting up set of papers"));

        try {
            System.out.println(" File: "+fullfilename);
            // Read the data from the input file.
            int linenumber=0;
            //String [] numbers = new String [maxColumns];
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoLevel>2) System.out.println(linenumber+": ");
                   int column=0;
                   String s= data.getln();
                   String [] numbers = s.split("\t",maxColumns+1);
                   if (numbers.length>maxColumns) System.out.println("!!! Warning - line "+linenumber+" does not have "+maxColumns+" columns but has "+numbers.length);

                   if (infoLevel>1) {
                     System.out.print(linenumber);
                     for (int c=0; c<numbers.length; c++) System.out.print(", "+numbers[c]);
                     System.out.println();
                   }
                    try {
                     if (numbers.length<4) {
                         System.out.println("!!! Warning too few columns on line " + linenumber + ", found " + column+", column 0="+numbers[0]);
                         String [] newNumbers= new String [maxColumns];
                         for (int c=numbers.length; c<maxColumns;c++) 
                         {
                             if (c<numbers.length) newNumbers[c]=numbers[c];
                             else newNumbers[c]="";
                         }
                         numbers=newNumbers;
                     }

                         // do something

                         if (createOrganisation){
                             if (numbers[2].length()==0 && numbers[3].length()>0){
                                 System.out.println("at line "+linenumber);
                             }
                             organisation.addSection(numbers[1], numbers[2], numbers[3]);
                         }
                         else{
                           int unitNumber=organisation.getUnitIndex(numbers[1], numbers[2], numbers[3]);
                           setOfPapers.addUnitsToPaper(numbers[0],unitNumber );
                         }

                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                        //return -100;
                    }

               }//eofile


            System.out.println("Finished reading Paper-Sections File from " + fullfilename);
            if (createOrganisation) System.out.println("... " + this.organisation.informationSetString(" "));
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the data from the input file.
            throw new RuntimeException("*** Input Error: " + e.getMessage());
            //res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }

        // VERY IMPORTANT to close off organisation
            if (createOrganisation) {
                organisation.finishSetUp();
                System.out.println("... finished setting up organisation");
            }
            else {
             setOfPapers.sortSections();
             System.out.println("... finished setting up set of papers");
            }


        return res;
    }

     /**
      * Reads in list of papers with information on them inclduing citations.
      * <p>Filename is set by inputName and ends in <tt>citations.txt</tt>.
      * <p>Each line ArticleECGUID | Year | Publication date | Times cited | Reference count
      */
    public int readPaperCitationsFile()
    {
        inputName.setNameEnd("citations.txt");
        return readPaperCitationsFile(inputName.getFullFileName());
    }
     /**
      * Reads in list of papers with information on them inclduing citations.
      * <p>Each line ArticleECGUID | Year | Publication date | Times cited | Reference count
      * @param fullfilename full file name
      */
    public int readPaperCitationsFile(String fullfilename)
    {
        int maxColumns=5;

        int res=0;  // error code.
        TextReaderTabSeparated data=FileInput.openTabSeparatedFile(fullfilename );
        if (data==null) return -1;
        if (res<0) return res;
        System.out.println("Starting to read paper citations list from " + fullfilename);

        try {
            System.out.println(" File: "+fullfilename);
            // Read the data from the input file.
            int linenumber=0;
            //String [] numbers = null; // = new String [maxColumns];
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoLevel>2) System.out.println(linenumber+": ");
                   int column=0;
                   String s= data.getln();

                   // use of split taken from <a href="http://www.rgagnon.com/javadetails/java-0438.html">www.rgagnon.com</a>
                   // numbers[maxColumns] if defined will be everything in the (maxColumn+1)-th column and after, 
                   // that is everything after the (maxColumn)-th tab.
                   String [] numbers = s.split("\t",maxColumns+1); 

                if (infoLevel>1) {
                    System.out.print(linenumber);
                    for (int c=0; c<numbers.length; c++) System.out.print(", "+numbers[c]);
                    System.out.println();
                }
                    try {
                         if (numbers.length!=maxColumns) {
                           System.out.println("!!! WARNING Wrong number of columns on line " + linenumber + ", found " + numbers.length);
                           res = -10;
                         }
                     
                         // do something
                         String gid = numbers[0];
                         int year=tryIntegerParse(numbers[1], "year", linenumber);
                         String date = numbers[2];
                         int citations = tryIntegerParse(numbers[3], "citations", linenumber);
                         int references = tryIntegerParse(numbers[4], "references", linenumber);
                         Paper p = setOfPapers.getPaper(gid);
                         p.setYear(year);
                         p.setDate(date);
                         p.setCitations(citations);
                         p.setReferences(references);
                     
                    }// eo try
                    catch (RuntimeException e) {
                        throw new RuntimeException("*** PROBLEM on line " + linenumber + ", " + e.getMessage());
                        //return -100;
                    }
               }//eofile
            System.out.println("Finished reading Paper-Sections File from " + fullfilename);
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the data from the input file.
            throw new RuntimeException("*** Input Error: " + e.getMessage());
            //res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }
        return res;
    }

    
    /**
     * Parse an integer.
     * <p>Gives message and returns -1 if there is a problem.
     * @param s string with integer representation
     * @param name name of variable for warniong message
     * @param linenumber current line number
     * @return value of integer, or -1 if a problem.
     */
    private int tryIntegerParse(String s, String name, int linenumber){
            int n = -1;
            try {
                  n=Integer.parseInt(s);
                 }
            catch (RuntimeException e){
                 System.out.println("!!! WARNING problem with "+name+" \""+s+"\" on line " + linenumber);
            }
            return n;
    }
    
}
