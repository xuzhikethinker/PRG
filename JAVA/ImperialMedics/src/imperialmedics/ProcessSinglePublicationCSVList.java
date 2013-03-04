package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



//import au.com.bytecode.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
/**
 * Apache POI for MS Office files
 * @see  http://poi.apache.org/spreadsheet/quick-guide.html
 */
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import ebrp.ASJCclasses;
import java.util.Comparator;
import java.util.Set;

/**
 *
 * @authorList time
 */
public class ProcessSinglePublicationCSVList {

//    /**
//     * Label of column with list of authors
//     */
//    static final String AUTHORLABEL="Authors";
//    /**
//     * Label of column with year of publication
//     */
//    static final String YEARLABEL="Publication Year";
//    /**
//     * Label of column with titles of papers
//     */
//    static final String TITLELABEL="Document Title";
//    /**
//     * Label of column with journal ISSN string
//     */
//    static final String ISSNLABEL="ISSN";
    /**
     * Array to specify boundaries between cells.
     * Period 0 is year &lt; yearBoundary[0],
     * Period L=yearBoundary.length is year &gt; yearBoundary[L-1],
     * Otherwise Period P is yearBoundary[P-1] &lt; year &lt; yearBoundary[P].
     * Vanash requires 2001-2003, 2004-2006, 2007-2009
     */
    static double [] yearBoundary = {2000.5, 2003.5, 2006.5, 2009.5};



    /**
     * Constant used to indicate unset double variable
     */
    static final double DUNSET = -1.23456789E12;
    /**
     * Constant used to indicate unset int variable
     */
    static final int IUNSET = -9876543;

    /**
     * Constant used to indicate unset String variable
     */
    static final String SUNSET = "UNSET";

        /**
     * Label of column with list of authors
     */
    static final String AUTHORLABEL="Authors";
    /**
     * Label of column with year of publication
     */
    static final String YEARLABEL="Publication Year";
    /**
     * Label of column with titles of papers
     */
    static final String TITLELABEL="Document Title";
    /**
     * Label of column with journal ISSN string
     */
    static final String ISSNLABEL="ISSN";

    static final String JOURNALTITLELABEL = "Journal Title";

    static final String VOLUMELABEL = "Volume";

    static final String ISSUELABEL = "Issue";

    static final String [] ALLLABELLIST = { AUTHORLABEL,
                                YEARLABEL,
                                TITLELABEL,
                                ISSNLABEL,
                                JOURNALTITLELABEL,
                                VOLUMELABEL,
                                ISSUELABEL};


    static final int authorColumnIndex=0;
    static final int yearColumnIndex=1;
    static final int titleColumnIndex=2;
    static final int issnColumnIndex=3;
    static final int journaltitleColumnIndex=4;
    static final int volumeColumnIndex=5;
    static final int issueColumnIndex=6;

    int authorColumn=IUNSET;
    int yearColumn=IUNSET;
    int titleColumn=IUNSET;
    int issnColumn=IUNSET;
    int journaltitleColumn=IUNSET;;
    int volumeColumn=IUNSET;
    int issueColumn=IUNSET;

    int labelRow=IUNSET;



    /**
     * Start of name of file
     */
    String rootFileName = SUNSET;
    /**
     * Name of input directory
     */
    String inputDirectory ="input\\individuals\\";
    /**
     * Name of output directory
     */
    String outputDirectory = "output\\individuals\\";
          

    List dataHolderCSV;

    /**
     * Holds all versions of name of primary author.
     * The primary authorList should be listed as a separate cell of the type
     * <tt>Authors: Aggarwal, R.; Aggarwal, Rajesh K.</tt>
     * This is processed by a specific routine and this stores them in an 
     * ArrayList with each version of the name,
     * e.g. here [0]=Aggarwal, R. and [1] Aggarwal, Rajesh K.
     * @see #setPrimaryAuthorCSV(java.util.List) 
     */
    ArrayList<Author> primaryAuthorList;
    
    int numberPapers=IUNSET;
//    int numberFirstAuthor=IUNSET;
//    int numberSecondAuthor=IUNSET;
//    int numberFinalAuthor=IUNSET;
//    int numberPenultimateAuthor=IUNSET;
//    int primaryAuthorPositionOther=IUNSET;

    PeriodData [] periodStats;

    /**
     * Set of allowed ISSN numbers
     */
    TreeSet<JournalWithASJC> journalSet;

    public String summaryString;
    static final public String SEP="\t";

    static public String summaryStringLabel = "Author"+SEP+"ListedPapers"
            +SEP+"Papers"+SEP
            +"Warnings"+SEP+"Errors"
            +SEP+ASJCclasses.getAllTypes(SEP)
            +SEP+"Uniformity";

    public static void main(String[] args) {

        ProcessSinglePublicationCSVList pa = new ProcessSinglePublicationCSVList();
        //pa.rootFileName = "Aggarwal, Mr Rajesh";
        //pa.rootFileName = "Butt, Dr Simon Julian Bevan";
        //pa.rootFileName = "Da Silva Xavier, Dr Gabriela";
        //pa.rootFileName = "Basanez, Dr Maria-Gloria";
        //pa.rootFileName = "Brooks, Professor David James MD, DSc, FRCP, FMedSci";
        //pa.rootFileName = "Jarvelin, Professor Marjo-Riitta";
        //pa.rootFileName = "Hughes, Professor Alun David";
        //pa.rootFileName = "Thom, Professor Simon Alasdair McG MD FRCP";
        pa.rootFileName = "Ashcroft, F M";
        pa.inputDirectory ="input\\individuals\\";
        boolean infoOn=false;
        //CoauthorshipGraphs cg = null; // period stats will be found
        pa.processCSVFileForAuthorData(infoOn);
        String outputFullFileName = pa.outputDirectory+pa.rootFileName+" PeriodStats.dat";
        //System.out.println("Periods "+pa.periodStats.length);
        writePeriodStatsData(outputFullFileName,  pa.periodStats, infoOn);
        String outputAuthorDataByPaperFileName = pa.outputDirectory+pa.rootFileName+" Publications.dat";
        pa.writeAuthorDataByPaper(outputAuthorDataByPaperFileName, infoOn);
    }


    /**
     * Process Vanash type file of publications to add to set of publications.
     * NOT FINISHED
     * @param infoOn true if want info on screen
     */
    public void processCSVFileForPublicationData(Set pubSet, boolean infoOn){
        String inputFullFileName = inputDirectory+rootFileName+".csv";
        System.out.println("\n.....................................\nProcessing CSV file "+inputFullFileName);

        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(inputFullFileName));
            dataHolderCSV = reader.readAll();
        } catch (IOException ex) {
            Logger.getLogger(ProcessSinglePublicationCSVList.class.getName()).log(Level.SEVERE, null, ex);
        }

        // find the primary authorList names
        setPrimaryAuthorCSV(dataHolderCSV);
        if (primaryAuthorList==null) {
            System.out.println("... no primary author cell found in file, using file name "+rootFileName);
            primaryAuthorList = new ArrayList();
            Author frn = new Author(rootFileName,',');
            primaryAuthorList.add(frn);
        }

        if (infoOn) {
            System.out.print("Primary Author:- ");
            for (int v=0; v<primaryAuthorList.size(); v++) System.out.print(primaryAuthorList.get(v)+"; ");
            System.out.println();
        }

        String [] labelList = ProcessSinglePublicationCSVList.ALLLABELLIST;
        int [] labelRowInfo = findLabelRow(dataHolderCSV, labelList);
        if (labelRowInfo==null) throw new RuntimeException("*** no label row found");

        authorColumn=labelRowInfo[0];
        yearColumn=labelRowInfo[1];
        titleColumn=labelRowInfo[2];
        issnColumn=labelRowInfo[3];
        journaltitleColumn=labelRowInfo[4];
        volumeColumn=labelRowInfo[5];
        issueColumn=labelRowInfo[6];

        labelRow=labelRowInfo[labelRowInfo.length-1];

        if (infoOn) System.out.println("Label row "+labelRow
                +", year column "+yearColumn
                +", author column "+authorColumn
                +", ISSN column "+issnColumn
                +", title column "+titleColumn);

        //

        //addToPublicationSet(pubSet,infoOn);
    }



    /**
     * Process Vanash type file of publications by one author to provide info on that author.
     * @param cg coauthorship graph data structure if want these, null if want period stats
     * @param infoOn true if want info on screen
     */
    public void processCSVFileForAuthorData(boolean infoOn){
        String inputFullFileName = inputDirectory+rootFileName+".csv";
        System.out.println("\n.....................................\nProcessing CSV file "+inputFullFileName);

        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(inputFullFileName));
            dataHolderCSV = reader.readAll();
        } catch (IOException ex) {
            Logger.getLogger(ProcessSinglePublicationCSVList.class.getName()).log(Level.SEVERE, null, ex);
        }

        // find the primary authorList names
        setPrimaryAuthorCSV(dataHolderCSV);
        if (primaryAuthorList==null) {
            System.out.println("... no primary author cell found in file, using file name "+rootFileName);
            primaryAuthorList = new ArrayList();
            Author frn = new Author(rootFileName,',');
            primaryAuthorList.add(frn);
        }

        if (infoOn) {
            System.out.print("Primary Author:- ");
            for (int v=0; v<primaryAuthorList.size(); v++) System.out.print(primaryAuthorList.get(v)+"; ");
            System.out.println();
        }

        String [] labelList = { ProcessSinglePublicationCSVList.AUTHORLABEL,
                                ProcessSinglePublicationCSVList.YEARLABEL,
                                ProcessSinglePublicationCSVList.TITLELABEL,
                                ProcessSinglePublicationCSVList.ISSNLABEL};
        int [] labelRowInfo = findLabelRow(dataHolderCSV, labelList);
        if (labelRowInfo==null) throw new RuntimeException("*** no label row found");

        authorColumn=labelRowInfo[0];
        yearColumn=labelRowInfo[1];
        titleColumn=labelRowInfo[2];
        issnColumn=labelRowInfo[3];
        labelRow=labelRowInfo[labelRowInfo.length-1];

        if (infoOn) System.out.println("Label row "+labelRow
                +", year column "+yearColumn
                +", author column "+authorColumn
                +", ISSN column "+issnColumn
                +", title column "+titleColumn);

        //

        authorshipStatisticsCSV(infoOn);
    }


    /**
     * Process info on journal of publications
     * @param cellLineVector single row of file
     * @param journalType array counting number of publication in each top level
     */
    public void issnValue(String [] cellLineVector, double [] journalType){
                        // issn
                    if (journalSet!=null){
                        // find data on journal
                        String issnCellValue = cellLineVector[issnColumn];
                        JournalWithASJC testj= new JournalWithASJC("Test",issnCellValue);
                        if (journalSet.contains(testj)){
                            JournalWithASJC j=journalSet.floor(testj);
                            if (j.ASJCList.size()>0){
                                // if journal is in more than one category use fractional count
                                double fc= 1.0/j.ASJCList.size();
                                for (Integer asjc: j.ASJCList){
                                 int tc=ASJCclasses.toTopLevel(asjc);
                                 journalType[tc]+=fc;
                                }
                            }// if j.

                        } //if journalSet
                        else { // can't find journal
                           journalType[ASJCclasses.Unknown_TYPE]+=1;
                           //numberWarnings++;
                           //System.err.println("!!! Excel Row "+(rowNumber+1)+", unknown journal, issn is "+issnCellValue+", title is "+titleCellValue);
                        }
                    }
}
        /**
         * Produce results from authorList.
         * Calculate the statistics for each period.
         * If primary author is found more than once in title this is flagged
         * and last position in list is used.
         * If primary author not found in list of authors of a paper,
         * that paper is flagged but ignored
         * @param infoOn true (false) if want (do not want) info on screen
         */
    public void authorshipStatisticsCSV_NEW(boolean infoOn){
            //ArrayList<Integer> numberAuthors= new ArrayList();
            int numberPeriods=yearBoundary.length+1;
            periodStats= new PeriodData[numberPeriods];
            for (int p=0; p<numberPeriods; p++) periodStats[p] = new PeriodData();
            Integer numberErrors=0;
            Integer numberWarnings=0;
            numberPapers=0; // per period
            int totalPapers=0; // all periods
            ArrayList<Integer> primaryAuthorPositions;
            ArrayList<Integer> repeatedPrimarySurnameRow =new ArrayList();
            // this will keep track of types of journals published in
            // last entry is unlisted journal
            double [] journalType = new double[ASJCclasses.topLevelNames.length];
            Boolean hasRepeatedPrimarySurnameRow=false;
            Publication pub;
            for (int rowNumber = labelRow+1; rowNumber< dataHolderCSV.size(); rowNumber++) {
                    //if (rowNumber > labelRow+3) break;
                    //repeatedPrimarySurnameRow.clear();
                    String [] cellLineVector = (String []) dataHolderCSV.get(rowNumber);
                    Integer primaryAuthorPosition=IUNSET;
                    String messagePrefix="row number "+(rowNumber+1);
                    pub=processOnePublication(cellLineVector,  numberErrors,
                            numberWarnings, messagePrefix, primaryAuthorPosition,
                            hasRepeatedPrimarySurnameRow);
                    if (hasRepeatedPrimarySurnameRow) repeatedPrimarySurnameRow.add(rowNumber+1);

                    int period = getPeriod(pub.getYear());
                    // statistics
                    numberPapers++;
                    //numberAuthors.add(authorList.size());
                    periodStats[period].addOnePaper(primaryAuthorPosition, pub.getNumberAuthors(), pub.isAlphabeticalOrder());

                    if (infoOn) System.out.println("--- Paper "+numberPapers+" "+pub.getShortTitle(10)+" --------------");
                    if (infoOn) System.out.println("Period "+period+", Author rank "+(1+primaryAuthorPosition)+" of "+pub.getNumberAuthors());
            } //eo for rowNumber
            int listedPapers=dataHolderCSV.size()-labelRow;
            int missingPapers=listedPapers- numberPapers;
            if (!repeatedPrimarySurnameRow.isEmpty()){
                System.err.print("--- Repeated surnames "+primaryAuthorList.get(0).getSurnames()+" found on following rows:-");
                int c=0;
                for (Integer r:repeatedPrimarySurnameRow){
                    if ((c%10)==0) System.err.print("\n--- ");
                    System.err.print(" "+r);
                }
                System.err.println(" ---");
            }
            if (numberErrors>0) {
                System.out.println("*** Number of papers were "+listedPapers+", of which "+numberPapers+" processed");
                System.out.println("*** Number of errors were "+numberErrors);
                System.err.println("***\n*** "+numberErrors+" errors found for author (all variants):-");
                for (Author a: primaryAuthorList) System.err.println("*** "+a.toString());
                if (numberWarnings>0) System.err.println("!!! "+numberWarnings+" warnings found for author");
                System.err.println("***");
            }
            else  {
                if (numberWarnings>0) {
                    System.err.println("!!!\n!!! "+numberWarnings+" warnings found for author "+getPrimaryAuthor()+"\n!!!");
                    System.out.println("!!! "+numberWarnings+" warnings found for author");
                }
                System.out.println("... Number of papers were "+listedPapers+", of which "+numberPapers+" processed");
                System.out.println("... No errors found for author "+getPrimaryAuthor());
            }

            for (int p=0;p<periodStats.length; p++) totalPapers+=periodStats[p].numberPapers;
            if (totalPapers!=numberPapers) System.err.println("Total number of papers wrong "+totalPapers+" != "+numberPapers);
            summaryString = getPrimaryAuthor()+SEP+listedPapers+SEP+numberPapers+SEP+numberWarnings+SEP+numberErrors;

            if (journalSet!=null){
                double dtp = (double) totalPapers;
                double uniformity=0;
                System.out.print("... ");
                for (int t=0; t<journalType.length; t++){
                    double frac = journalType[t]/(dtp);
                    uniformity += frac*frac;
                    System.out.print(ASJCclasses.topLevelNames[t].substring(0, 4)+"="+Math.round(100*frac)+"% ");
                    summaryString = summaryString + SEP + String.format("%6.4g",frac);
                }
                System.out.println("\n... Uniformity = "+String.format("%3d",Math.round(100*uniformity))+"%");
                summaryString = summaryString + SEP + String.format("%6.4g",uniformity);
            } // if journalSet

            outputSummary(infoOn);
    }

    /**
     * infoToColumn[i] is the column that contains the item described by
     * ALLLABELLIST[i].
     * @param rowData
     * @param infoToColumn
     * @param numberErrors
     * @param numberWarnings
     * @param messagePrefix
     * @param primaryAuthorPosition
     * @param hasRepeatedPrimarySurnameRow
     * @return
     */
    public Publication processOnePublication(String [] rowData,
            Integer numberErrors,
            Integer numberWarnings, String messagePrefix,
            Integer primaryAuthorPosition, Boolean hasRepeatedPrimarySurnameRow){
       Publication pub = new Publication();
        hasRepeatedPrimarySurnameRow =false;
        //hasRepeatedPrimarySurnameRow.clear();
                    //String [] rowData = (String []) dataHolderCSV.get(rowNumber);

            //int labelRow=IUNSET;
//    int authorColumn=infoToColumn[authorColumnIndex];
//    int yearColumn=infoToColumn[yearColumnIndex];//=IUNSET;
//    int issnColumn=infoToColumn[issnColumnIndex];//=IUNSET;
//    int journaltitleColumn=infoToColumn[journaltitleColumnIndex];//=IUNSET;
//    int volumeColumn=infoToColumn[volumeColumnIndex];//=IUNSET;
//    int issueColumn=infoToColumn[issueColumnIndex];//=IUNSET;

       // title
//        int titleColumn=infoToColumn[titleColumnIndex];//=IUNSET;
        String titleCellValue="UNKNOWN";
        if (titleColumn>=0){
                    titleCellValue = rowData[titleColumn];
                    pub.setTitle(titleCellValue);
                    int l=Math.min(20, titleCellValue.length());
                    String shortTitle = String.format("%10s",titleCellValue.substring(0, l));
        }
                    // issn
        if (issnColumn>=0){
            String issnCellValue= rowData[issnColumn];
            //pub.setJournalFromISSN(issnCellValue);
//                    double [] journalType = new double[ASJCclasses.topLevelNames.length];
//                    if (journalSet!=null) issnValue(rowData, journalType);
        }
//                    if (journalSet!=null){
//                        // find data on journal
//                        String issnCellValue = rowData[issnColumn];
//                        JournalWithASJC testj= new JournalWithASJC("Test",issnCellValue);
//                        if (journalSet.contains(testj)){
//                            JournalWithASJC j=journalSet.floor(testj);
//                            if (j.ASJCList.size()>0){
//                                // if journal is in more than one category use fractional count
//                                double fc= 1.0/j.ASJCList.size();
//                                for (Integer asjc: j.ASJCList){
//                                 int tc=ASJCclasses.toTopLevel(asjc);
//                                 journalType[tc]+=fc;
//                                }
//                            }// if j.
//
//                        } //if journalSet
//                        else { // can't find journal
//                           journalType[ASJCclasses.Unknown_TYPE]+=1;
//                           //numberWarnings++;
//                           //System.err.println("!!! Excel Row "+(rowNumber+1)+", unknown journal, issn is "+issnCellValue+", title is "+titleCellValue);
//                        }
//                    }

                    // process authorList
                    String authorCellValue = rowData[authorColumn];
                    // This next line splits cell at commas,
                    // but does so multiple times  (the plus sign)
//                    String [] authorList = authorCellValue.split(",+");
//                    Author [] authorList = new Author[authorList.length];
//                    for (int a=0; a<authorList.length; a++) authorList[a] = new Author(authorList[a]);
                    // example format fo authorList cell is
                    //Gurusamy K.S., Aggarwal R., Palanivelu L., Davidson B.R.
                    //Cunninghame Graham D.S., Vyse T.J.
                    ArrayList<Author> authorList=Author.authorList(authorCellValue, ",");
                    pub.setAuthorList(authorList);

                    boolean alphabeticalOrder=Author.isAlphabeticalOrder(authorList);

                    String yearCellValue = rowData[yearColumn];

                    //int primaryAuthorPosition=-1; // = findPrimaryAuthor(authorList);
                    //primaryAuthorPositions = findPrimaryAuthorListBySurname(authorList);
                    ArrayList<Integer> primaryAuthorPositions;

                    primaryAuthorPositions = findAuthorListBySurname(primaryAuthorList,authorList);

                    // if no primary author found by surname try harder
                    if (primaryAuthorPositions.isEmpty()) {
                          ArrayList<Author> authorShiftedList = new ArrayList();
                          for (Author a : authorList) authorShiftedList.add(a.surnameFirstNameSplit());
                          //primaryAuthorPositions = findPrimaryAuthorListBySurname(authorShiftedList);
                          primaryAuthorPositions = findAuthorListBySurname(primaryAuthorList,authorShiftedList);
                          if (primaryAuthorPositions.isEmpty()) {
                              numberErrors++;
                              System.err.println("*** "+messagePrefix+", can not find surname of primary author "+getPrimaryAuthor()+" in list, title is "+titleCellValue);
                              findPrimaryAuthor(authorList); // for debugging
                              return null;
                          }
                          numberWarnings++;
                          System.err.println("!!! Excel Row "+messagePrefix+", surname ends in first name found and dealt with for "+getPrimaryAuthor()+", title is "+titleCellValue);
                          authorList=authorShiftedList; // have found author surname in here
                    }
                    // at least one primary author has been found by surname
                    int minValue=Author.SURNAMEOFFSET*256*256;
                    if (primaryAuthorPositions.size()>1){
                        // more than one p
                        hasRepeatedPrimarySurnameRow=true;
//                        System.err.println("--- Excel Row "+(rowNumber+1)+
//                                    ", primary author surname repeated, primary author "+
//                                    getPrimaryAuthor()+", title is "+
//                                    titleCellValue);
                        ArrayList<Integer> primaryAuthorPositionsImproved = new ArrayList();
                        for (Integer n:  primaryAuthorPositions){
                            boolean newSurname=true;
                            for (int v=0; v<this.primaryAuthorList.size(); v++){ // version v of primary author
                              int c= primaryAuthorList.get(v).compareToSurnameInitialsAsPossible(authorList.get(n));
                              int comp = Math.abs(c);
                              if (comp<minValue) {
                                  minValue=comp;
                                  primaryAuthorPositionsImproved.clear();
                                  primaryAuthorPositionsImproved.add(n);
                                  newSurname=false; // don't match other version of this author
                              }
                              if (comp==minValue && newSurname) primaryAuthorPositionsImproved.add(n);
                            } // eo for v
                        } // eo for n
                        if (primaryAuthorPositionsImproved.isEmpty()) { // this shouldn't happen
                            numberErrors++;
                            System.err.println("*** "+messagePrefix+", serious problem, primary author "+getPrimaryAuthor()+" in list, title is "+titleCellValue);
                            findPrimaryAuthor(authorList);// for debugging
                            return null;
                        }
                        if (minValue>0){
                            numberWarnings++;
                            System.err.println("!!! "+messagePrefix+", partial initials match (value "+minValue+"), primary author "+
                                    getPrimaryAuthor()+", title is "+
                                    titleCellValue);
                        }
                        if (primaryAuthorPositionsImproved.size()>1){
                            numberErrors++;
                            System.err.println("*** "+messagePrefix+", equal quality match to primary author "+getPrimaryAuthor()+", title is "+titleCellValue);
                            for (Integer p:primaryAuthorPositionsImproved){
                            System.err.println("***                            "
                                    + " matched author "
                                    + p +"="+authorList.get(p));
                            }
                        }
                        // have found one or more authors with surnames matching
                        // and some initials matching, so take first as match.
                        primaryAuthorPosition=primaryAuthorPositionsImproved.get(0);
                    } // if (authorPositions.size()>1)
                    else { // just one primary author was found by surname
                        primaryAuthorPosition=primaryAuthorPositions.get(0);
                        minValue=Author.SURNAMEOFFSET;
                    }

                    // now process year
                    double year=DUNSET;
                    try{
                        year = Double.parseDouble(yearCellValue);
                    }
                    catch (RuntimeException ex) {
                        numberErrors++;
                        year=-9234590;
                    }
                    if (year<0) {
                        numberErrors++;
                        System.err.println("*** "+messagePrefix
                                +" has wrong cell value, "+yearCellValue
                                +", title is "+titleCellValue);
                        return null;
                    }
                    pub.setYear((int) Math.round(year));

                                        return pub;

    }

                    //int period = getPeriod(year);

// This next section is information if the paper lies outside boundaries
//                    if (period==0) {
//                        if (infoOn) System.out.println("*** Row "+rowNumber+
//                                " has year "+year+
//                                " which is below lower boundary "+yearBoundary[0]+", , title is "+shortTitle);
//                        //continue;
//                    }
//                    if (period==yearBoundary.length) {
//                        if (infoOn) System.out.println("*** Row "+rowNumber+
//                                " has year "+year+
//                                " which is above upper boundary "+
//                                yearBoundary[yearBoundary.length-1]
//                                +", , title is "+shortTitle);
//                        //continue;
//                    }

    public void outputSummary(boolean infoOn){
            String noEntry=" . ";
            String name="All.";
            if (infoOn){
                String sep="  ";
                PositionCounts [] pcall = new PositionCounts[periodStats.length];
                for (int p=0; p<periodStats.length; p++) pcall[p]=periodStats[p].getAllPapersPositionCounts();
                System.out.println("Period"+sep+PositionCounts.tableHeader(name,sep));
                for (int p=0; p<periodStats.length; p++) System.out.println(String.format("%6d",p)
                        +sep+pcall[p].tableDataRow(name, sep));

                System.out.println("Period"+sep+PositionCounts.tableHeaderPercentage(name, sep));
                for (int p=0; p<periodStats.length; p++) System.out.println(String.format("%6d",p)+sep+pcall[p].tableDataRowPercentage(name, sep, pcall[p].numberPapers, noEntry));

                System.out.println("Period"+sep+PeriodData.tableNumberAuthorsHeader(sep));
                for (int p=0; p<periodStats.length; p++) System.out.println(String.format("%6d",p)+sep+periodStats[p].tableNumberAuthorsDataRow(sep));
            }

            //if (infoOn) System.out.println("Writing file of authorList data "+ outputFileName);
            //if (infoOn) System.out.println("Finished writing file of authorList data "+ outputFileName);


        }

    /**
     * Finds all versions of name of primary authorList.
     * The primary authorList should be listed as a separate cell of the type
     * <tt>Authors: Aggarwal, R.; Aggarwal, Rajesh K.</tt>
     * The routine should then return an array with each version of the name,
     * e.g. here [0]=Aggarwal, R. and [1] Aggarwal, Rajesh K.
     * @param dataHolderXLS vector of vectors of cells
     * @return array of different versions of primary authorList names, null if non found
     */
    public void setPrimaryAuthorCSV(List dataHolder){
        final String authorLabel1 = "Author:";
        final String authorLabel2 = "Authors:";
        String separatorNames=";";
        char separatorSurnameInitials=',';
        for (int rowNumber = 0; rowNumber< dataHolder.size(); rowNumber++)
        {
            String [] cellLineVector = (String []) dataHolderCSV.get(rowNumber);

            // find cell with common authorList name for this sheet
            String authorCellValue=SUNSET;
            int column=0;
            for (column = 0; column < cellLineVector.length; column++) {
                authorCellValue = cellLineVector[column];
                if (authorCellValue.startsWith(authorLabel1) || authorCellValue.startsWith(authorLabel2)  ) break;
                }
            if (column< cellLineVector.length) {
                int nameListStart = authorCellValue.indexOf(':')+1;
                String allVersionsAuthor= authorCellValue.substring(nameListStart);
                primaryAuthorList = Author.authorList(allVersionsAuthor,separatorNames, separatorSurnameInitials);
                return;
            }
        }
        return;
    }

    /**
     * Returns name of first primary in authorList.
     * @return string using first primary authorList representation, empty string if none
     */
    public String getPrimaryAuthor(){
        if (primaryAuthorList==null || primaryAuthorList.isEmpty()) return "";
        return primaryAuthorList.get(0).toStringWithTitlesAndID();
    }



    /**
     * Finds an author in given list.
     * Tests using first surname only.
     * The author searched for can have multiple names.
     * All matches between two lists are returned.
     * @param findThisAuthor list of alternative version of same author to be found.
     * @param authorList ArrayList of authors
     * @return list of positions (counting from 0) in list of primary authorList.  More than one indicates a problem.
     * @see Author#equalUptoFirstInitial(java.lang.Object)
     */
    static public ArrayList<Integer> findAuthorListBySurname(ArrayList<Author> findThisAuthor, ArrayList<Author> author){
        ArrayList<Integer> pos=new ArrayList();
        for (int a=0; a<author.size(); a++){
            for (Author findMe: findThisAuthor){ // version v of author a
              if (author.get(a).equalsSurnamesOnly(findMe)) {
                  pos.add(a);
                  break; // check no more versions of the find me names, check next name in list
              } // eo if
            } // eo for v
        } // eo for a
        return pos;
    }
    /**
     * Finds an authorSet in given list.
     * Tests are done using the comparator built into the authorSet.
     * All matches are returned.
     * @param findThisAuthor author to be found.
     * @param authorSet TreeSet of authors to search
     * @return list of authors in authorSet found to match
     */
    static public ArrayList<Author> findAuthorInSet(Author findThisAuthor, TreeSet<Author> authorSet){
        ArrayList<Author> pos= new ArrayList();
        TreeSet<Author> newSet =  authorSet;
        Comparator acomp = authorSet.comparator();
        while(!newSet.isEmpty()){
           newSet = (TreeSet) newSet.tailSet(findThisAuthor);
           if (newSet.isEmpty()) break;
           Author a =newSet.pollFirst(); // removes first
           if (acomp.compare(a,findThisAuthor)==0) pos.add(a);
        }
        return pos;
    }

//    /**
//     * Finds primary authors in given list.
//     * Tests using first initial only.
//     * @param authorList ArrayList of authors
//     * @return position in list of primary authorList.  negative indicates a problem.
//     * @see Author#equalUptoFirstInitial(java.lang.Object)
//     */
//    public ArrayList<Integer> findPrimaryAuthorListBySurnameInitialsAsPossible(ArrayList<Author> author){
//        ArrayList<Integer> pos=new ArrayList();
//        for (int a=0; a<author.size(); a++){
//            for (int v=0; v<this.coAuthor.size(); v++){ // version v of author a
//              if (author.get(a).equalsSurnamesOnly(coAuthor.get(v))) {
//                  pos.add(a);
//                  break; // check no more versions of this name, check next name
//              } // eo if
//            } // eo for v
//        } // eo for a
//        return pos;
//    }


    public int getPrimaryAuthorTestValue(){
        String psurname = primaryAuthorList.get(0).getSurnames();
        if (psurname.equalsIgnoreCase("Warner")) return 1;
        if (psurname.equalsIgnoreCase("Smith")) return 1;
        if (psurname.equalsIgnoreCase("Taylor")) return 1;
        if (psurname.equalsIgnoreCase("Thomas")) return 1;
        return 0;
    }
    /**
     * Finds primary authors in given list.
     * Tests used depends on getPrimaryAuthorTestValue().
     * @param authorList ArrayList of authors
     * @return position in list of primary authorList.  negative indicates a problem.
     * @see Author#equalUptoFirstInitial(java.lang.Object)
     * @see #getPrimaryAuthorTestValue()
     */
    public int  findPrimaryAuthor(ArrayList<Author> author){
        int primaryAuthorTest=getPrimaryAuthorTestValue();
        switch (primaryAuthorTest){
            case 1: return findPrimaryAuthorAllPossibleInitials(author);
            case 0:
            default:
                return findPrimaryAuthorFirstInitial(author);
        }
    }


    /**
     * Finds primary authors in given list.
     * Tests using first initial only.
     * @param authorList ArrayList of authors
     * @return position in list of primary authorList.  negative indicates a problem.
     * @see Author#equalUptoFirstInitial(java.lang.Object)
     */
    public int  findPrimaryAuthorFirstInitial(ArrayList<Author> author){
        int pos=IUNSET;
        for (int a=0; a<author.size(); a++){
            for (int v=0; v<this.primaryAuthorList.size(); v++){
              if (author.get(a).equalUptoFirstInitial(primaryAuthorList.get(v))) {
                  if (pos>=0) return (-a-1); // found second primary authorList
                  pos=a;
                  break; //
              } // eo if
            } // eo for v
        } // eo for a
        return pos;
    }

    /**
     * Finds primary authors in given list.
     * Tests using as many initials as possible but extra initials are ignored.
     * @param authorList ArrayList of authors
     * @return position in list of primary authorList.  negative indicates a problem.
     * @see Author#equalUptoFirstInitial(java.lang.Object)
     */
    public int  findPrimaryAuthorAllPossibleInitials(ArrayList<Author> author){
        int pos=IUNSET;
        for (int a=0; a<author.size(); a++){
            for (int v=0; v<this.primaryAuthorList.size(); v++){
              if (author.get(a).equalsAllPossibleInitials(primaryAuthorList.get(v))) {
                  if (pos>=0) return (-a-1); // found second primary authorList
                  pos=a;
                  break; //
              } // eo if
            } // eo for v
        } // eo for a
        return pos;
    }

   

    

     /**
     * Finds row of column labels.
     * Looks for a row containing given labels.  Must match exactly.
      * The list returned is a list of the column numbers (first column is column 0)
      * for each of the labels in the list given, in the same order.  The last entry
      * is the number of the row with these labels.
     * @param dataHolder list of string arrays, each array representing one row
     * @param labelList list of strings with labels
     * @return null if not found, an array of {column of label 0, ... column of last label, row number}
     */
    public static int [] findLabelRow(List dataHolder, String [] labelList){
        int [] column=new int[labelList.length+1];
        for (int rowNumber = 0; rowNumber< dataHolder.size(); rowNumber++)
        {
            boolean foundLabelRow=true;
            String [] cellLineVector = (String []) dataHolder.get(rowNumber);
            for(int l=0; l<labelList.length; l++){
                column[l] =ProcessSinglePublicationCSVList.findColumn(cellLineVector, labelList[l], false);
                if (column[l]<0) {foundLabelRow=false; break;}
            }
            if (foundLabelRow) {
                column[labelList.length]=rowNumber;
                return column;
            }
        }
        return null;
    }

    /**
     * Finds which column has specified label.
     * Operates on header row.
     * @param cellRowArray array of strings for row of column labels
     * @return column (numbered from 0) with label, negative if non found.
     */
    public static int findColumn(String [] cellRowArray, String label, boolean infoOn){
                int labelColumn=-1;
                for (int j = 0; j < cellRowArray.length; j++) {
                                String stringCellValue = cellRowArray[j];
                                if (stringCellValue.startsWith(label)) labelColumn=j;
                        }
                if (labelColumn<0) {
                    if (infoOn) System.err.println("*** No column starts with "+label);
                }
                else if (infoOn) System.out.println("column "+labelColumn+ " is labelled with "+label);
                return labelColumn;
    }


        /**
         * Finds which period year falls into.
         * Period boundaries defined by yearBoundary array.
         * Period p means year satisfies
         * <tt>yearBoundary[p+1] > year >= yearBoundary[p]</tt>.
         * @param year input year
         * @return period number, 0 if too early, (yearBoundary.length) if beyond top end
         */
    public static int getPeriod(double year){
        if (year<yearBoundary[0]) return 0;
        for (int b=1; b< yearBoundary.length; b++) if ((year<yearBoundary[b]) && (year>=yearBoundary[b-1]) )
            return b;
        return yearBoundary.length;
    }

    public void writeAuthorData(String outputFileName,
            boolean infoOn){
        int firstdataRow=this.labelRow+1;
        int authorCol=this.authorColumn;
        writeAuthorDataByPaper(outputFileName,
            firstdataRow, authorCol,
            dataHolderCSV,
            infoOn);
    }

public static void writePeriodStatsData(String outputFileName, PeriodData [] periodStats,
            boolean infoOn){
                PrintStream PS;
        FileOutputStream fout;
        //boolean infoOn=true;
        if (infoOn) System.out.println("Writing file of period statistics data "+ outputFileName);
            try {
            fout = new FileOutputStream(outputFileName);
            PS = new PrintStream(fout);

            String sep="\t";
            String noEntry=" ";
            writePeriodStatsData(PS, sep, noEntry, periodStats);

            try{ fout.close ();
               } catch (IOException e) { throw new RuntimeException("*** File Error with " +outputFileName+" "+e.getMessage());}

        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Error opening output file "+outputFileName+" "+e.getMessage());
        }
        if (infoOn) System.out.println("Finished writing file of period statistics data "+ outputFileName);
        }

     static public void writePeriodStatsData(PrintStream PS, String sep, String noEntry,
             PeriodData [] periodStats)
     {
         PositionCounts [] pcall = new PositionCounts[periodStats.length];
         for (int p=0; p<periodStats.length; p++) pcall[p]=periodStats[p].getAllPapersPositionCounts();

            PS.println("Period"+sep+PositionCounts.tableHeader("All",sep)
                    +sep+PositionCounts.tableHeaderPercentage("All", sep)
                    +sep+PeriodData.tableNumberAuthorsHeader(sep));
            for (int p=0; p<periodStats.length; p++)
                PS.println(String.format("%6d",p)+sep+pcall[p].tableDataRow("All", sep)
                        +sep+pcall[p].tableDataRowPercentage("All", sep, pcall[p].numberPapers, noEntry)
                        +sep+periodStats[p].tableNumberAuthorsDataRow(sep));
     }


//                System.out.println("Period"+sep+PositionCounts.tableHeader("All",sep));
//                for (int p=0; p<periodStats.length; p++) System.out.println(String.format("%6d",p)
//                        +sep+pcall[p].tableDataRow("All", sep));
//
//                System.out.println("Period"+sep+PositionCounts.tableHeaderPercentage("All", sep));
//                for (int p=0; p<periodStats.length; p++) System.out.println(String.format("%6d",p)+sep+pcall[p].tableDataRowPercentage("All", sep, pcall[p].numberPapers, noEntry));
//
//                System.out.println("Period"+sep+PeriodData.tableNumberAuthorsHeader(sep));
//                for (int p=0; p<periodStats.length; p++) System.out.println(String.format("%6d",p)+sep+periodStats[p].tableNumberAuthorsDataRow(sep));


    public void writeAuthorDataByPaper(String outputFileName, boolean infoOn){
            ProcessSinglePublicationCSVList.writeAuthorDataByPaper(outputFileName,
            labelRow+1, authorColumn, dataHolderCSV, infoOn);
    }

    public static void writeAuthorDataByPaper(String outputFileName,
            int firstdataRow, int authorCol,
            List dataHolder,
            boolean infoOn){
                PrintStream PS;
        FileOutputStream fout;
        //boolean infoOn=true;
        if (infoOn) System.out.println("Writing file of author data "+ outputFileName);
            try {
            fout = new FileOutputStream(outputFileName);
            PS = new PrintStream(fout);

            String sep="\t";
            writeAuthorDataByPaper(PS, sep, dataHolder,  firstdataRow, authorCol, "NOAUTHOR");

            try{ fout.close ();
               } catch (IOException e) { throw new RuntimeException("*** File Error with " +outputFileName+" "+e.getMessage());}

        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Error opening output file "+outputFileName+" "+e.getMessage());
        }
        if (infoOn) System.out.println("Finished writing file of author data "+ outputFileName);

                
        }



     static public void writeAuthorDataByPaper(PrintStream PS, String sep, List dataHolder,
             int firstdataRow, int authorColumn, String noAuthor){
         PS.println("NumberAuthors"+sep+"Author1"+sep+"Author2"+sep+"AuthorLast"+sep+"AuthorPenUltimate");
         for (int i = firstdataRow; i< dataHolder.size(); i++) {
                    String [] cellLineVector = (String []) dataHolder.get(i);
                    String stringCellValue = cellLineVector[authorColumn];
                    String[] author = stringCellValue.split(",+");
                    //System.out.println("Line "+(rowNumber+1)+" has "+authorList.length+" authors starting with "+authorList[0]);
                    PS.print(author.length+sep);
                    if (author.length>0)  PS.print(author[0]+sep); 
                    else PS.print(noAuthor+sep);
                    if (author.length>1)  PS.print(author[1]+sep); 
                    else PS.print(noAuthor+sep);
                    if (author.length>2)  PS.print(author[author.length-1]+sep); 
                    else PS.print(noAuthor+sep);
                    if (author.length>3)  PS.print(author[author.length-2]+sep); 
                    else PS.print(noAuthor+sep);
                    PS.println();
         }
     }


             /**
         * Produce results from authorList.
         * Calculate the statistics for each period.
         * If primary author is found more than once in title this is flagged
         * and last position in list is used.
         * If primary author not found in list of authors of a paper,
         * that paper is flagged but ignored
         * @param infoOn true (false) if want (do not want) info on screen
         */
    public void authorshipStatisticsCSV(boolean infoOn){
            //ArrayList<Integer> numberAuthors= new ArrayList();
            int numberPeriods=yearBoundary.length+1;
            periodStats= new PeriodData[numberPeriods];
            for (int p=0; p<numberPeriods; p++) periodStats[p] = new PeriodData();
            int numberErrors=0;
            int numberWarnings=0;
            numberPapers=0; // per period
            int totalPapers=0; // all periods
            ArrayList<Integer> primaryAuthorPositions;
            ArrayList<Integer> repeatedPrimarySurnameRow =new ArrayList();
            // this will keep track of types of journals published in
            // last entry is unlisted journal
            double [] journalType = new double[ASJCclasses.topLevelNames.length];
            for (int rowNumber = labelRow+1; rowNumber< dataHolderCSV.size(); rowNumber++) {
                    //if (rowNumber > labelRow+3) break;
                    //repeatedPrimarySurnameRow.clear();
                    String [] cellLineVector = (String []) dataHolderCSV.get(rowNumber);

                    // title
                    String titleCellValue = cellLineVector[titleColumn];
                    int l=Math.min(20, titleCellValue.length());
                    String shortTitle = String.format("%10s",titleCellValue.substring(0, l));

                    // issn
                     if (journalSet!=null) issnValue(cellLineVector, journalType);
//                    if (journalSet!=null){
//                        // find data on journal
//                        String issnCellValue = cellLineVector[issnColumn];
//                        JournalWithASJC testj= new JournalWithASJC("Test",issnCellValue);
//                        if (journalSet.contains(testj)){
//                            JournalWithASJC j=journalSet.floor(testj);
//                            if (j.ASJCList.size()>0){
//                                // if journal is in more than one category use fractional count
//                                double fc= 1.0/j.ASJCList.size();
//                                for (Integer asjc: j.ASJCList){
//                                 int tc=ASJCclasses.toTopLevel(asjc);
//                                 journalType[tc]+=fc;
//                                }
//                            }// if j.
//
//                        } //if journalSet
//                        else { // can't find journal
//                           journalType[ASJCclasses.Unknown_TYPE]+=1;
//                           //numberWarnings++;
//                           //System.err.println("!!! Excel Row "+(rowNumber+1)+", unknown journal, issn is "+issnCellValue+", title is "+titleCellValue);
//                        }
//                    }

                    // process authorList
                    String authorCellValue = cellLineVector[authorColumn];
                    // This next line splits cell at commas,
                    // but does so multiple times  (the plus sign)
//                    String [] authorList = authorCellValue.split(",+");
//                    Author [] authorList = new Author[authorList.length];
//                    for (int a=0; a<authorList.length; a++) authorList[a] = new Author(authorList[a]);
                    // example format fo authorList cell is
                    //Gurusamy K.S., Aggarwal R., Palanivelu L., Davidson B.R.
                    //Cunninghame Graham D.S., Vyse T.J.
                    ArrayList<Author> authorList=Author.authorList(authorCellValue, ",");
                    boolean alphabeticalOrder=Author.isAlphabeticalOrder(authorList);

                    String yearCellValue = cellLineVector[yearColumn];

                    int primaryAuthorPosition=-1; // = findPrimaryAuthor(authorList);
                    //primaryAuthorPositions = findPrimaryAuthorListBySurname(authorList);
                    primaryAuthorPositions = findAuthorListBySurname(primaryAuthorList,authorList);

                    // if no primary author found by surname try harder
                    if (primaryAuthorPositions.isEmpty()) {
                          ArrayList<Author> authorShiftedList = new ArrayList();
                          for (Author a : authorList) authorShiftedList.add(a.surnameFirstNameSplit());
                          //primaryAuthorPositions = findPrimaryAuthorListBySurname(authorShiftedList);
                          primaryAuthorPositions = findAuthorListBySurname(primaryAuthorList,authorShiftedList);
                          if (primaryAuthorPositions.isEmpty()) {
                              numberErrors++;
                              System.err.println("*** Excel Row "+(rowNumber+1)+", can not find surname of primary author "+getPrimaryAuthor()+" in list, title is "+titleCellValue);
                              findPrimaryAuthor(authorList); // for debugging
                              continue;
                          }
                          numberWarnings++;
                          System.err.println("!!! Excel Row "+(rowNumber+1)+", surname ends in first name found and dealt with for "+getPrimaryAuthor()+", title is "+titleCellValue);
                          authorList=authorShiftedList; // have found author surname in here
                    }
                    // at least one primary author has been found by surname
                    int minValue=Author.SURNAMEOFFSET*256*256;
                    if (primaryAuthorPositions.size()>1){
                        // more than one p
                        repeatedPrimarySurnameRow.add((rowNumber+1));
//                        System.err.println("--- Excel Row "+(rowNumber+1)+
//                                    ", primary author surname repeated, primary author "+
//                                    getPrimaryAuthor()+", title is "+
//                                    titleCellValue);
                        ArrayList<Integer> primaryAuthorPositionsImproved = new ArrayList();
                        for (Integer n:  primaryAuthorPositions){
                            boolean newSurname=true;
                            for (int v=0; v<this.primaryAuthorList.size(); v++){ // version v of primary author
                              int c= primaryAuthorList.get(v).compareToSurnameInitialsAsPossible(authorList.get(n));
                              int comp = Math.abs(c);
                              if (comp<minValue) {
                                  minValue=comp;
                                  primaryAuthorPositionsImproved.clear();
                                  primaryAuthorPositionsImproved.add(n);
                                  newSurname=false; // don't match other version of this author
                              }
                              if (comp==minValue && newSurname) primaryAuthorPositionsImproved.add(n);
                            } // eo for v
                        } // eo for n
                        if (primaryAuthorPositionsImproved.isEmpty()) { // this shouldn't happen
                            numberErrors++;
                            System.err.println("*** Excel Row "+(rowNumber+1)+", serious problem, primary author "+getPrimaryAuthor()+" in list, title is "+titleCellValue);
                            findPrimaryAuthor(authorList);// for debugging
                            continue;
                        }
                        if (minValue>0){
                            numberWarnings++;
                            System.err.println("!!! Excel Row "+(rowNumber+1)+
                                    ", partial initials match (value "+minValue+"), primary author "+
                                    getPrimaryAuthor()+", title is "+
                                    titleCellValue);
                        }
                        if (primaryAuthorPositionsImproved.size()>1){
                            numberErrors++;
                            System.err.println("*** Excel Row "+(rowNumber+1)+", equal quality match to primary author "+getPrimaryAuthor()+", title is "+titleCellValue);
                            for (Integer p:primaryAuthorPositionsImproved){
                            System.err.println("***                            "
                                    + " matched author "
                                    + p +"="+authorList.get(p));
                            }
                        }
                        // have found one or more authors with surnames matching
                        // and some initials matching, so take first as match.
                        primaryAuthorPosition=primaryAuthorPositionsImproved.get(0);
                    } // if (authorPositions.size()>1)
                    else { // just one primary author was found by surname
                        primaryAuthorPosition=primaryAuthorPositions.get(0);
                        minValue=Author.SURNAMEOFFSET;
                    }

                    // now process year
                    double year=DUNSET;
                    try{
                        year = Double.parseDouble(yearCellValue);
                    }
                    catch (RuntimeException ex) {
                        numberErrors++;
                        year=-9234590;
                    }
                    if (year<0) {
                        numberErrors++;
                        System.err.println("*** Excel Row "+(rowNumber+1)+
                                " has wrong cell value, "+yearCellValue+", title is "+titleCellValue);
                        continue;
                    }
                    int period = getPeriod(year);

// This next section is information if the paper lies outside boundaries
//                    if (period==0) {
//                        if (infoOn) System.out.println("*** Row "+rowNumber+
//                                " has year "+year+
//                                " which is below lower boundary "+yearBoundary[0]+", , title is "+shortTitle);
//                        //continue;
//                    }
//                    if (period==yearBoundary.length) {
//                        if (infoOn) System.out.println("*** Row "+rowNumber+
//                                " has year "+year+
//                                " which is above upper boundary "+
//                                yearBoundary[yearBoundary.length-1]
//                                +", , title is "+shortTitle);
//                        //continue;
//                    }

                    // statistics
                    numberPapers++;
                    //numberAuthors.add(authorList.size());
                    periodStats[period].addOnePaper(primaryAuthorPosition, authorList.size(), alphabeticalOrder);

                    if (infoOn) System.out.println("--- Paper "+numberPapers+" "+shortTitle+" --------------");
                    if (infoOn) System.out.println("Period "+period+", Author rank "+(1+primaryAuthorPosition)+" of "+authorList.size());
            } //eo for rowNumber
            int listedPapers=dataHolderCSV.size()-labelRow;
            int missingPapers=listedPapers- numberPapers;
            if (!repeatedPrimarySurnameRow.isEmpty()){
                System.err.print("--- Repeated surnames "+primaryAuthorList.get(0).getSurnames()+" found on following rows:-");
                int c=0;
                for (Integer r:repeatedPrimarySurnameRow){
                    if ((c%10)==0) System.err.print("\n--- ");
                    System.err.print(" "+r);
                }
                System.err.println(" ---");
            }
            if (numberErrors>0) {
                System.out.println("*** Number of papers were "+listedPapers+", of which "+numberPapers+" processed");
                System.out.println("*** Number of errors were "+numberErrors);
                System.err.println("***\n*** "+numberErrors+" errors found for author (all variants):-");
                for (Author a: primaryAuthorList) System.err.println("*** "+a.toString());
                if (numberWarnings>0) System.err.println("!!! "+numberWarnings+" warnings found for author");
                System.err.println("***");
            }
            else  {
                if (numberWarnings>0) {
                    System.err.println("!!!\n!!! "+numberWarnings+" warnings found for author "+getPrimaryAuthor()+"\n!!!");
                    System.out.println("!!! "+numberWarnings+" warnings found for author");
                }
                System.out.println("... Number of papers were "+listedPapers+", of which "+numberPapers+" processed");
                System.out.println("... No errors found for author "+getPrimaryAuthor());
            }

            for (int p=0;p<periodStats.length; p++) totalPapers+=periodStats[p].numberPapers;
            if (totalPapers!=numberPapers) System.err.println("Total number of papers wrong "+totalPapers+" != "+numberPapers);
            summaryString = getPrimaryAuthor()+SEP+listedPapers+SEP+numberPapers+SEP+numberWarnings+SEP+numberErrors;

            if (journalSet!=null){
                double dtp = (double) totalPapers;
                double uniformity=0;
                System.out.print("... ");
                for (int t=0; t<journalType.length; t++){
                    double frac = journalType[t]/(dtp);
                    uniformity += frac*frac;
                    System.out.print(ASJCclasses.topLevelNames[t].substring(0, 4)+"="+Math.round(100*frac)+"% ");
                    summaryString = summaryString + SEP + String.format("%6.4g",frac);
                }
                System.out.println("\n... Uniformity = "+String.format("%3d",Math.round(100*uniformity))+"%");
                summaryString = summaryString + SEP + String.format("%6.4g",uniformity);
            } // if journalSet

            outputSummary(infoOn);
    }


}





