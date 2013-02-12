/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

import JavaNotes.TextReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Process files from Scopus on journals
 * @author time
 */
public class ProcessScopusJournalLists {

    /**
     * ISSN column label
     */
    String ISSNLabel = "Print-ISSN";
    /**
     * Journal Title column label
     */
    String JournalTitleLabel="Source Title";

    /**
     * Scopus Source id for journal
     */
    String JournalIDLabel="Sourcerecord id";

    /**
     * ASJC column label
     */
    String ASJCLabel="ASJC";

    public ProcessScopusJournalLists(){}

    public static void main(String[] args) {
        ProcessScopusJournalLists psjl = new ProcessScopusJournalLists();
        //psjl.rootFileName = "Stata10networkspreadsheetfinalNoPW";
        //String rootFileName = "SCOPUS_Journal_Classification_title_list_TEST.dat";
        String rootFileName = "SCOPUS_Journal_Classification_title_list_Simple.txt";
        //String rootFileName = "ScopusMedicalJournals.dat";
        String inputDirectory ="input\\journals\\";
        boolean infoOn=true;
        String fullFileName=inputDirectory+rootFileName;
        //ArrayList<Journal> journalList;
        int infoLevel=0;
        //ArrayList<Journal> journalList;
        //journalList = psjl.readJournalWithASJCData(fullFileName, infoLevel);
//        TreeSet<Journal> journalSet;
//        journalSet = psjl.readSimpleJournalData(fullFileName, infoLevel);
        TreeSet<Journal> journalSet = new TreeSet(psjl.readJournalWithASJCData(fullFileName, infoLevel));
        System.out.println("Created list of "+journalSet.size()+" unique ISSN numbers");
        Journal testj= new Journal("Test-British Medical Journal","09598146");
        Journal testj2= new Journal("Test-British Medical Journal-NLZ","9598146");
        Journal foundj = journalSet.floor(testj);
        System.out.println("Searched for:-"+testj);
        System.out.println("Found:-"+foundj);
        foundj = journalSet.floor(testj2);
        System.out.println("Searched for:-"+testj2);
        System.out.println("Found:-"+foundj);

    }

    /**
     * Read in list two columns, title then ISSN, strings separated by white space.
     * <p>Use <tt>(String[]) FileInputreadStringList(fullFileName).toArray()</tt>
     * to get array of strings instead of an ArrayList.
     * @param fullFileName name of file including directories
     * @param infoLevel 0 = normal, 2= debugging, -2 = silent
     * @return list of journals found.
     */
    public TreeSet<Journal> readSimpleJournalData(String fullFileName, int infoLevel){
        TextReader tr = ProcessScopusJournalLists.openFile(fullFileName);
        if (tr==null) return null;
        if (infoLevel>-2) System.out.println("Starting to read list of strings from " + fullFileName);
        ArrayList<String> words = new ArrayList();
        TreeSet<Journal> journalList = new TreeSet();

        String [] labelList ={this.JournalTitleLabel, this.ISSNLabel, this.JournalIDLabel};
        int rowNumber=0;
        try {
            String [] column;
            // first find header row and identify columns needed
            String header;
//            column = line.split("\\t+"); // split at every tab
            int [] columnIndex = null;
            while (tr.eof() == false && columnIndex==null) {
                rowNumber++;
                header = tr.getln();
                column = header.split("\\t+"); // split at every tab
                columnIndex = testLabelRow(column, labelList);
            }
            if (columnIndex==null) throw new RuntimeException("*** no header columns found in fullFileName");
            if (infoLevel>-1) System.out.println("... header in row "+rowNumber);
            if (infoLevel>0) for (int c=0; c<columnIndex.length; c++)System.out.println(labelList[c]+" in column "+columnIndex[c]);

            // now process main data
            String line;
            String ISSN;
            String title;
            String journalID;
            Journal journal;
            while (tr.eof() == false) {
                rowNumber++;
                line = tr.getln();
                column = line.split("\\t"); // split at every tab
                title = (column.length>0?column[columnIndex[0]]:Journal.SUNSET);
                ISSN  = (column.length>1?column[columnIndex[1]]:Journal.SUNSET);
                journalID = (column.length>2?column[columnIndex[2]]:Journal.SUNSET);
                journal = new Journal(title, ISSN, journalID);
                journalList.add(journal);
                if (infoLevel>1) System.out.println(rowNumber+" j="+title+", n="+ISSN);
            }
            if (infoLevel>1) System.out.println("Finished reading journals from file "
                    + fullFileName+" found "+journalList.size()+" journals");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            throw new RuntimeException("*** Input Error: readJournalData failed after "
                    +journalList.size()+" journals, row "+rowNumber+", "
                    + e.getMessage());
        } finally {
            tr.close();
        }
        return journalList;
    }
    /**
     * Read in list of words, strings separated by white space.
     * <p>Use <tt>(String[]) FileInputreadStringList(fullFileName).toArray()</tt>
     * to get array of strings instead of an ArrayList.
     * *** Problems with tabs or similar in column headings.
     * @param fullFileName name of file including directories
     * @param infoLevel 0 = normal, 2= debugging, -2 = silent
     * @return list of journals found.
     */
    public ArrayList<JournalWithASJC> readJournalWithASJCData(String fullFileName, int infoLevel){
        TextReader tr = ProcessScopusJournalLists.openFile(fullFileName);
        if (tr==null) return null;
        if (infoLevel>-2) System.out.println("Starting to read list of strings from " + fullFileName);
        ArrayList<String> words = new ArrayList();
        ArrayList<JournalWithASJC> journalList = new ArrayList();

        String [] labelList ={JournalTitleLabel, ISSNLabel, ASJCLabel, this.JournalIDLabel};
        int rowNumber=0;
        try {
            String [] column;
            // first find header row and identify columns needed
            String header;
//            column = line.split("\\t+"); // split at every tab
            int [] columnIndex = null;
            while (tr.eof() == false && columnIndex==null) {
                rowNumber++;
                header = tr.getln();
                column = header.split("\\t"); // split at every tab
                columnIndex = testLabelRow(column, labelList);
            }
            if (columnIndex==null) throw new RuntimeException("*** no header columns found in fullFileName");
            if (infoLevel>-1) System.out.println("... header in row "+rowNumber);
            if (infoLevel>0) for (int c=0; c<columnIndex.length; c++)System.out.println(labelList[c]+" in column "+columnIndex[c]);

            // now process main data
            String line;
            String ISSN;
            String title;
            String ASJC;
            String journalID;
            JournalWithASJC journal;
            while (tr.eof() == false) {
                rowNumber++;
                line = tr.getln();
                column = line.split("\\t"); // split at every tab
                title = (column.length>columnIndex[0]?column[columnIndex[0]]:Journal.SUNSET);
                ISSN  = (column.length>columnIndex[1]?column[columnIndex[1]]:Journal.SUNSET);
                ASJC  = (column.length>columnIndex[2]?column[columnIndex[2]]:Journal.SUNSET);
                journalID = (column.length>3?column[columnIndex[3]]:Journal.SUNSET);
                journal = new JournalWithASJC(title, ISSN, journalID, ASJC,  "\\D+"); // split at characters that are not digits
                journalList.add(journal);
                if (infoLevel>1) System.out.println(rowNumber+" j="+title+", n="+ISSN+", ASJC="+ASJC);
            }
            if (infoLevel>-2) System.out.println("Finished reading journals from file "
                    + fullFileName+" found "+journalList.size()+" journals");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            throw new RuntimeException("*** Input Error: readJournalData failed after "
                    +journalList.size()+" journals, row "+rowNumber+", "
                    + e.getMessage());
        } finally {
            tr.close();
        }
        return journalList;
    }

    /**
     * Tests row to see if contains labels.
     * Given array of strings for entries in one row,
     * looks for a row containing given labels.  Must match exactly.
     * The list returned is a list of the columnIndex numbers 
     * (first column has columnIndex 0)
     * for each of the labels in the list given, in the same order.  
     * @param column array of strings, the column entries in order for one row
     * @param labelList list of strings with labels to be found
     * @return null if not found, an array of {columnIndex of label 0, ... columnIndex of last label}
     */
    public static int [] testLabelRow(String [] columns, String [] labelList){
        int [] columnIndex=new int[labelList.length];
        //boolean foundLabelRow=true;
        for(int c=0; c<labelList.length; c++){
                columnIndex[c] =FileReadUtilities.findColumn(columns, labelList[c], false);
                if (columnIndex[c]<0) return null; //{foundLabelRow=false; break;}
        }
        return columnIndex;
    }


 // ***************************************************************************
        /**
         * Opens file for reading.
         * @param fullfilename full name of file including any directory path
         */
    static public TextReader openFile(String fullfilename)
    {
        TextReader newTR;
        //System.out.println("Starting to read from " + fullfilename);
        try {  // Create the input stream.
            newTR = new TextReader(new FileReader(fullfilename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Can't find file "+fullfilename+", "+e.getMessage());
            //return null;
        }
        return newTR;
    }
   
}
