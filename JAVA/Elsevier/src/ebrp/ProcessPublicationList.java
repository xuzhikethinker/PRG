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
public class ProcessPublicationList {

    /**
     * ISSN column label
     */
    String ISSNLabel = "Print-ISSN";
    /**
     * Journal Title column label
     */
    String JournalTitleLabel="Source Title";

    /**
     * ASJC column label
     */
    String ASJCLabel="ASJC";

    /**
     * Columns in EBRP publication list
     */
    final public static String [] ebrpPublicationColumnLabels={"eid","fieldname","journal_title",
    "scopus_source_id","publication_year","article_title","author_keywords",
    "citations_as_of_may01_2012","bibliography_length","document_type"};

    /**
     * Record the document types.
     */
    TreeSet<String> documentTypeSet;
    /**
     * Record the fieldName types.
     */
    TreeSet<String> fieldnameTypeSet;


    public static void main(String[] args) {
        ProcessPublicationList psjl = new ProcessPublicationList();
        String rootFileName = "ebrp_03_set_01_documents.dat";
        String inputDirectory ="input/";
        boolean infoOn=true;
        String fullFileName=inputDirectory+rootFileName;
        int infoLevel=0;
        TreeSet<ebrpPublication> pubList;
        pubList = psjl.readEBRPPublicationData(fullFileName, infoLevel);
    }

    /**
     * Switches on recording of document types
     */
    public void recordDocumentTypes(){
        documentTypeSet = new TreeSet();
    }
    /**
     * Switches on fieldname of document types
     */
    public void recordFieldnameTypes(){
        fieldnameTypeSet = new TreeSet();
    }

    
    /**
     * Read in EBRP file as strings separated by tabs.
     * <p>Use <tt>(String[]) FileInputreadStringList(fullFileName).toArray()</tt>
     * to get array of strings instead of an ArrayList.
     * @param fullFileName name of file including directories
     * @param infoLevel 0 = normal, 2= debugging, -2 = silent
     * @return TreeSet of publications found.
     */
    public TreeSet<ebrpPublication> readEBRPPublicationData(String fullFileName, int infoLevel){
        TextReader tr = FileReadUtilities.openFile(fullFileName);
        if (tr==null) return null;
        if (infoLevel>-2) System.out.println("Starting to read list of strings from " + fullFileName);
        ArrayList<String> words = new ArrayList();
        TreeSet<ebrpPublication> publicationList = new TreeSet();

        String line=Publication.SUNSET;
        ebrpPublication pub;
        String [] labelList =ebrpPublicationColumnLabels;
        int rowNumber=0;
        int pubNumber=0;
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
                columnIndex = FileReadUtilities.testLabelRow(column, labelList);
            }
            if (columnIndex==null) throw new RuntimeException("*** no header columns found in fullFileName");
            if (infoLevel>-1) System.out.println("... header in row "+rowNumber);
            if (infoLevel>0) for (int c=0; c<columnIndex.length; c++)System.out.println(labelList[c]+" in column "+columnIndex[c]);

            // now process main data
//            {"eid","fieldname","journal_title",
//    "scopus_source_id","publication_year","article_title","author_keywords",
//    "citations_as_of_may01_2012","bibliography_length","document_type"}

            while (tr.eof() == false) {
                rowNumber++;
                line = tr.getln();
                column = line.split("\\t"); // split at every tab
                if (column.length<10) System.err.println("!!! Warning publication "+(column.length>0?column[0]:"?")+" at line "+rowNumber+" has only "+column.length+" columns");
                pub = new ebrpPublication(column);
                pub.setInternalID(pubNumber++);
                publicationList.add(pub);
                pub.setDateToDefault();
                if (documentTypeSet!=null) documentTypeSet.add(pub.documentType);
                if (fieldnameTypeSet!=null) fieldnameTypeSet.add(pub.fieldName);
                if (infoLevel>1) System.out.println(rowNumber+" j="+pub.title+", eid="+pub.eid);
            }
            if (infoLevel>-2) System.out.println("Finished reading publications from file "
                    + fullFileName+" found "+publicationList.size()+" publications");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            throw new RuntimeException("*** Input Error: readEBRPPublicationData failed after "
                    +publicationList.size()+" publications, row "+rowNumber+", "
                    + e.getMessage());
        } finally {
            tr.close();
        }
        if ((documentTypeSet!=null)&& infoLevel>-2) {
            System.out.println("---"+documentTypeSet.size()+" document types are:-");
            for (String dt: documentTypeSet) System.out.println("--- "+dt);            
        }
        if ((fieldnameTypeSet!=null)&& infoLevel>-2) {
            System.out.println("---"+fieldnameTypeSet.size()+" fieldname types are:-");
            for (String fnt: fieldnameTypeSet) System.out.println("--- "+fnt);            
        }
        return publicationList;
    }

    public void findJournal(TreeSet<Journal> journalSet){


    }

 
}
