package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import JavaNotes.TextReader;
import ebrp.FileReadUtilities;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;
import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * Process existing data on all authors.
 * Data has each author with ID and period along with data for that period on each row.
 * @author time
 */
public class ProcessAllAuthorFile {


    /**
     * Label of column with list of authors
     */
    static final String FULLNAMELABEL="Full Name";
    /**
     * Label of column with year of publication
     */
    static final String IDLABEL="ID";
    /**
     * Label of column with titles of papers
     */
    static final String PERIODLABEL="Time Period";

    int fullNameColumn=ProcessSinglePublicationCSVList.IUNSET;
    int idColumn=ProcessSinglePublicationCSVList.IUNSET;
    int periodColumn=ProcessSinglePublicationCSVList.IUNSET;
    int labelRow=ProcessSinglePublicationCSVList.IUNSET;
    /**
     * Number of columns used in label row.
     * Needed as sparse representation of rows is used in Vector of HSSFCells
     */
    int numberDataColumns =ProcessSinglePublicationCSVList.IUNSET;

    /**
     * Complete set of authors.
     * Uses the equals comparison which uses only some of the information
     * in the author.  Thus we can link a record in a new file to the
     * existing fundamental name and id of an author even if way given is not
     * exactly the same.
     */
    TreeSet<Author> authorSet;

    /**
     * Start of name of file
     */
    String rootFileName = ProcessSinglePublicationCSVList.SUNSET;
    /**
     * Name of input directory
     */
    String inputDirectory ="input\\";
    /**
     * Name of output directory
     */
    String outputDirectory = "output\\";

    Vector dataHolderXLS;


   public static void main(String[] args) {

        ProcessAllAuthorFile paaf = new ProcessAllAuthorFile();
        //paaf.rootFileName = "Stata10networkspreadsheetfinalNoPW";
        paaf.rootFileName = "Stata10networkspreadsheetfinalsorted";
        paaf.inputDirectory ="input\\";
        boolean infoOn=true;
        paaf.processXLSFile(infoOn);

    }

    /**
     * Process simple list of Imperial Authors and ID.
     * Data has each author with their ID on each row.
     * File must be tab separated with relevant columns labelled using
     * {@value imperialmedics.ProcessAllAuthorFile#FULLNAMELABEL}, and
     * {@value imperialmedics.ProcessAllAuthorFile#IDLABEL}, and
     * {@value imperialmedics.ProcessAllAuthorFile#PERIODLABEL}.
     * @param infoOn true if want info on screen
     */
    public void processSimpleListFile(boolean infoOn){
        String inputFullFileName = inputDirectory+rootFileName+".dat";
        System.out.println("********************\nProcessing all author simple data file "+inputFullFileName);

        int infoLevel=(infoOn?2:0);
        authorSet= readSimpleImperialAuthorList(inputFullFileName, infoLevel);
    }


    /**
     * Process existing data on all authors.
     * Data has each author with ID and period along with data for that period on each row.
     * File must be xls excel format with relevant columns labelled using
     * {@value imperialmedics.ProcessAllAuthorFile#FULLNAMELABEL}, and
     * {@value imperialmedics.ProcessAllAuthorFile#IDLABEL}, and
     * {@value imperialmedics.ProcessAllAuthorFile#PERIODLABEL}.
* @param infoOn true if want info on screen
     */
    public void processXLSFile(boolean infoOn){
        String inputFullFileName = inputDirectory+rootFileName+".xls";
        System.out.println("********************\nProcessing all author data XLS file "+inputFullFileName);

        dataHolderXLS =  ReadExcelXLSFile.ReadXLSFile(inputFullFileName);

        // now find the label row
        String [] labelList = {FULLNAMELABEL,IDLABEL,PERIODLABEL};
        int [] labelRowInfo = ProcessSinglePublicationXLSList.findLabelRow(dataHolderXLS, labelList);
        if (labelRowInfo==null) throw new RuntimeException("*** no label row found");
        fullNameColumn=labelRowInfo[0];
        idColumn=labelRowInfo[1];
        periodColumn=labelRowInfo[2];
        labelRow=labelRowInfo[3];
        Vector labelRowVector = (Vector) dataHolderXLS.elementAt(labelRow);
        HSSFCell firstLabelCell = (HSSFCell) labelRowVector.firstElement();
        numberDataColumns = firstLabelCell.getRow().getPhysicalNumberOfCells();
        if (infoOn) System.out.println("Label row "+labelRow
                +", full name column "+fullNameColumn
                +", id column "+idColumn
                +", period column "+periodColumn
                +", number of data columns "+numberDataColumns);
        processAuthorPeriodData(infoOn);
    }

    public void processAuthorPeriodData(boolean infoOn){
            int numberPeriods=ProcessSinglePublicationCSVList.yearBoundary.length-1;
            //authorSet = new TreeSet(new AuthorComparatorByID());
            authorSet = new TreeSet();
            //TreeMap<Author,Author> AuthorToAuthor= new TreeMap();

            int warningNumber=20;
            int numberErrors=0;
            int numberDataRows=0;
            for (int rowNumber = labelRow+1; rowNumber< dataHolderXLS.size(); rowNumber++) {
                if (infoOn && warningNumber<1){
                    System.out.println("!!! No more warnings shown.");
                    warningNumber=-1;
                }
                    //if (rowNumber > labelRow+3) break;
                    Vector cellLineVector = (Vector) dataHolderXLS.elementAt(rowNumber);

                    // Full Name
                    // example format of full name cell is
                    // Piot, Professor Lord Peter Karel MD, PhD, FRCP
                    // Pepper, Professor John
                    // Aggarwal, Mr Rajesh
                    // Harris, Ms Jessica Mary
                    // Hooper, Dr Richard
                    // Horwood, Dr Nicole (Nikki)
                    HSSFCell fullNameCell = (HSSFCell) cellLineVector.elementAt(fullNameColumn);                    String titleCellValue = fullNameCell.toString();
                    String fullNameCellValue = fullNameCell.toString();
                    
                    // id
                    HSSFCell idCell = (HSSFCell) cellLineVector.elementAt(idColumn);
                    int id = ReadExcelXLSFile.cellToInteger(idCell);
                    if (id<1 ) {
                        if (infoOn && warningNumber>0) System.out.println("!!! Row "+rowNumber+
                                " has ID "+idCell.toString()+
                                " which is not positive, author is "+fullNameCellValue);
                        warningNumber--;
                        continue;
                    }

                    // period: these can not be 0 or ProcessSinglePublicationCSVList.yearBoundary.length which are used
                    // to store stats outside range specified by ProcessSinglePublicationCSVList.yearBoundary.length
                    HSSFCell periodCell = (HSSFCell) cellLineVector.elementAt(periodColumn);
                    int period = ReadExcelXLSFile.cellToInteger(periodCell);
                    if (period<1 || period >numberPeriods) {
                        if (infoOn && warningNumber>0) System.out.println("!!! Row "+rowNumber+
                                " has period "+periodCell.toString()+
                                " which is not between 1 and  "+numberPeriods+
                                ", author is "+fullNameCellValue);
                        warningNumber--;
                        continue;
                    }

                    // legitimate data row found
                    numberDataRows++;

                    // find out if author already exists
                    Author author = new Author(fullNameCellValue, ',');
                    author.setID(id);
                    Author allFileAuthor = authorSet.floor(author); // nearest existing author
                    boolean newAuthor=false;
//                    if (allFileAuthor==null ||  (allFileAuthor.getID()!=id)){  newAuthor=true;}
//                    else {newAuthor=false;}
                    if ((numberDataRows%3)==1) newAuthor=true; else newAuthor=false;

                    if (newAuthor){// new author
                        AuthorWithData awd = new AuthorWithData(author,numberPeriods+1); // copies author name
                        awd.setID(id);
                        awd.addExcelRow(period,cellLineVector);
                        authorSet.add(awd);
                        if ((numberDataRows%3)!=1) {
                            System.err.println("### row="+numberDataRows+", id="+id+", "+fullNameCellValue+", period "+period+", id conflict with "+allFileAuthor.getID());
                        }
                        if (infoOn) {
                            System.out.println("--- "+numberDataRows+" New Author "+id+" "+fullNameCellValue+", period "+period);
                        }
                    } else { // author already exists in set
                        AuthorWithData awd = (AuthorWithData) allFileAuthor;
                        awd.addExcelRow(period,cellLineVector);
                        if (infoOn) {
                            System.out.println(".   "+numberDataRows+" existing author "+awd.getID()+" "+awd+", period "+period);
                        }

                    }
//                    author.addExcelRow(period,cellLineVector);
//                    authorSet.add(author);
                    
            } //eo for rowNumber
            int missingPapers=dataHolderXLS.size()-1- numberDataRows;
            System.out.println("*** Number of data rows was "+(dataHolderXLS.size()-labelRow-1)+", of which "+numberDataRows+" were correct");
            if (numberErrors>0) System.out.println("*** Number of errors were "+numberErrors);
            else  System.out.println("No errors found");


        }

    /**
     * Write out full data table as text file.
     * File is  outputDirectory+rootFileName+Extended.dat
     * @param infoOn true if want extra info on screen
     */
    public void writeTextFile(boolean infoOn){
        String outputFileName  = outputDirectory+rootFileName+"Extended.dat";
        PrintStream PS;
        FileOutputStream fout;
        //boolean infoOn=true;
        if (infoOn) System.out.println("Writing all author extended data text file "+ outputFileName);
            try {
            fout = new FileOutputStream(outputFileName);
            PS = new PrintStream(fout);

            String sep="\t";
            String noEntry=" ";
            writeDataTable(PS, sep, noEntry);

            try{ fout.close ();
               } catch (IOException e) { throw new RuntimeException("*** File Error with " +outputFileName+" "+e.getMessage());}

        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Error opening output file "+outputFileName+" "+e.getMessage());
        }
        if (infoOn) System.out.println("Finished writing all author extended data text file "+ outputFileName);
        }

    /**
     * Write out full table of data
     * @param PS PrintStream such as System.out
     * @param sep separation string between entries
     */
    public void writeDataTable(PrintStream PS, String sep, String noEntry){
        PS.println("Author"+sep+"Period"+sep+textFileHeader(sep, noEntry));
        for (Author a: authorSet){
            AuthorWithData awd = (AuthorWithData) a;
            for (int p=0; p<=awd.numberPeriods; p++) {
                System.out.println("Author "+awd+" period "+p);
                PS.println(awd+sep+p+sep+awd.allDataString(sep, noEntry, p,  numberDataColumns));
            }
        }
        //
    }

    /**
     * Produces header row string.
     * @param sep separation string between entries
     */
    public String textFileHeader(String sep, String noEntry){
        String s=cellRowToString(sep, labelRow, numberDataColumns);
        return (s+sep+AuthorWithData.periodDataHeaderString(sep, noEntry));
    }

    
//    /**
//     * Produces full data row string.
//     * Both the original excel file data but with author stats also appended.
//     * @param sep separation string between entries
//     */
//    public String textFileDataRow(String sep, int rowNumber){
//        String s=cellRowToString(sep, rowNumber);
//        return (s+periodData.tabtableDataRowAll(sep));
//    }

    /**
     * Converts one cell row to string.
     * This is the original row of the excel file.
     * @param sep separation string between entries
     * @param rowNumber number of row in cellLineVector
     * @return string representing the row
     */
        public String cellRowToString(String sep, int rowNumber, int numberColumns){
         Vector cellLineVector = (Vector) dataHolderXLS.elementAt(rowNumber);
         return ReadExcelXLSFile.cellRowToString(sep, cellLineVector, numberColumns, " ");
        }


    /**
     * Reads in columns of data, setting name and id from two, as strings separated by white space.
     * <p>Set is order by ID.
     * File must be tab separated and relevant columns labelled using
     * {@value imperialmedics.ProcessAllAuthorFile#FULLNAMELABEL} and
     * {@value imperialmedics.ProcessAllAuthorFile#IDLABEL}.
     * @param fullFileName name of file including directories
     * @param infoLevel 0 = normal, 2= debugging, -2 = silent
     * @return set of authors with id, sorted by id.
     */
    public static TreeSet<Author> readSimpleImperialAuthorList(String fullFileName, int infoLevel){
        TextReader tr = ProcessScopusJournalLists.openFile(fullFileName);
        if (tr==null) return null;
        if (infoLevel>-2) System.out.println("Starting to read list Authors and ID numbers from " + fullFileName);
        ArrayList<String> words = new ArrayList();
        Comparator compareID = new AuthorComparatorByID();
        TreeSet<Author> authorSetSimple = new TreeSet(compareID);

        String [] labelList ={FULLNAMELABEL,IDLABEL};
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
            String name;
            String authorID;
            Author author;
            while (tr.eof() == false) {
                rowNumber++;
                line = tr.getln();
                column = line.split("\\t"); // split at every tab
                name = (column.length>columnIndex[0]?column[columnIndex[0]]:Journal.SUNSET);
                authorID = (column.length>columnIndex[1]?column[columnIndex[1]]:Journal.SUNSET);

                                    // find out if author already exists
                author = new Author(name, ',');
                try{author.setID(Integer.parseInt(authorID));}
                catch(RuntimeException e){
                    System.err.println("!!! Warning at row "+rowNumber+" author "+name+" has no id");
                }
                Author allFileAuthor = authorSetSimple.floor(author); // nearest existing author
                boolean newAuthor=true;
                //if (allFileAuthor!=null && allFileAuthor.getID()==author.getID() ) newAuthor=false;
                if (authorSetSimple.contains(author)) newAuthor=false;
                if (newAuthor){// new author
                    authorSetSimple.add(author);
                    if (infoLevel>1) {
                        System.out.println("--- "+rowNumber+": new author "+authorID+" "+name);
                    }
                } else { // author already exists in set
                    if (infoLevel>1)  {
                        System.err.println(".   "+rowNumber+": existing author "+allFileAuthor+" matches "+name);
                    }
                }
            }
            if (infoLevel>-1) System.out.println("Finished reading authors from file "
                    + fullFileName+" found "+authorSetSimple.size()+" authors");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the noNamedata from the input file.
            throw new RuntimeException("*** Input Error: readJournalData failed after "
                    +authorSetSimple.size()+" journals, row "+rowNumber+", "
                    + e.getMessage());
        } finally {
            tr.close();
        }
        return authorSetSimple;
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
