package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



//import au.com.bytecode.opencsv.CSVReader;
import TimUtilities.MessageError;
import TimUtilities.MessageWarning;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
/**
 * Apache POI for MS Office files
 * @see  http://poi.apache.org/spreadsheet/quick-guide.html
 */
import java.util.TreeSet;
import ebrp.ASJCclasses;
import java.util.Comparator;

/**
 *
 * @primaryAuthorList time
 */
public class CoAuthorshipSinglePublicationCSV extends ProcessSinglePublicationCSVList{


    public static void main(String[] args) {

        CoAuthorshipSinglePublicationCSV pa = new CoAuthorshipSinglePublicationCSV();
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
        CoauthorshipGraphs cg = null; // period stats will be found
        //pa.processCSVFileForAuthorData(cg, infoOn);
        String outputFullFileName = pa.outputDirectory+pa.rootFileName+" PeriodStats.dat";
        //System.out.println("Periods "+pa.periodStats.length);
        writePeriodStatsData(outputFullFileName,  pa.periodStats, infoOn);
        String outputAuthorDataByPaperFileName = pa.outputDirectory+pa.rootFileName+" Publications.dat";
        pa.writeAuthorDataByPaper(outputAuthorDataByPaperFileName, infoOn);
    }



        /**
         * Update coauthorship graphs from primaryAuthorList.
         * If primary author is found more than once in title this is flagged
         * and last position in list is used.
         * If primary author not found in list of authors of a paper,
         * that paper is flagged but ignored
         * @param cg coauthorship graph data structure
         * @param specialAuthorSet
         * @param firstIndexOrdinaryAuthor,
         * @param ordinaryAuthorSet,
         * @param includeOrdinaryAuthors
         * @param infoOn true (false) if want (do not want) info on screen
         */
    public void authorshipGraphCSV(CoauthorshipGraphs cg,
            TreeSet<Author> specialAuthorSet,
            int firstIndexOrdinaryAuthor,
            TreeSet<Author> ordinaryAuthorSet,
            boolean includeOrdinaryAuthors,
            boolean infoOn){
            //ArrayList<Integer> numberAuthors= new ArrayList();
            int numberPeriods= PeriodBoundary.getNumberOfPeriods();
            if (cg==null) { // do period stats
                periodStats= new PeriodData[numberPeriods];
                for (int p=0; p<numberPeriods; p++) periodStats[p] = new PeriodData();
            }
            else {
                periodStats=null;
            }
            //int numberErrors=0;
            //int numberWarnings=0;
            MessageWarning warningMessage = new MessageWarning();
            MessageError errorMessage = new MessageError();
            numberPapers=0; // per period
            int totalPapers=0; // all periods
            ArrayList<Integer> primaryAuthorPositions;
            ArrayList<Integer> repeatedPrimarySurnameRow =new ArrayList();
            // this will keep track of types of journals published in
            // last entry is unlisted journal
            double [] journalType = new double[ASJCclasses.topLevelNames.length];
            for (int rowNumber = labelRow+1; rowNumber< dataHolderCSV.size(); rowNumber++) {
                    //if (rowNumber > labelRow+3) break;
                    repeatedPrimarySurnameRow.clear();
                    String [] cellLineVector = (String []) dataHolderCSV.get(rowNumber);

                    // title
                    String titleCellValue = cellLineVector[titleColumn];
                    int l=Math.min(20, titleCellValue.length());
                    String shortTitle = String.format("%10s",titleCellValue.substring(0, l));

                    // issn
                    if (journalSet!=null) issnValue(cellLineVector, journalType);

                    // get and process year
                    String yearCellValue = cellLineVector[yearColumn];
                    double year=DUNSET;
                    try{
                        year = Double.parseDouble(yearCellValue);
                    }
                    catch (RuntimeException ex) {
                        errorMessage.printlnErr("Excel Row "+(rowNumber+1)+" "+ex);
                        year=-9234590;
                    }
                    if (year<0) {
                        errorMessage.printlnErr("Excel Row "+(rowNumber+1)+
                                " has wrong cell value, "+yearCellValue+", title is "+titleCellValue);
                        continue;
                    }
                    int period = getPeriod(year);

                    //get and process coauthors
                    String authorCellValue = cellLineVector[authorColumn];
                    // This next line splits cell at commas,
                    // but does so multiple times  (the plus sign)
//                    String [] primaryAuthorList = authorCellValue.split(",+");
//                    Author [] primaryAuthorList = new Author[primaryAuthorList.length];
//                    for (int a=0; a<primaryAuthorList.length; a++) primaryAuthorList[a] = new Author(primaryAuthorList[a]);
                    // example format fo primaryAuthorList cell is
                    //Gurusamy K.S., Aggarwal R., Palanivelu L., Davidson B.R.
                    //Cunninghame Graham D.S., Vyse T.J.
                    ArrayList<Author> coauthorList=Author.authorList(authorCellValue, ",");
                    boolean alphabeticalOrder=Author.isAlphabeticalOrder(coauthorList);


                    // FIRST find primary author i.e. position in coauthors of owner of this file
                    int primaryAuthorPosition=IUNSET; // = findPrimaryAuthor(primaryAuthorList);
                    primaryAuthorPositions = findAuthorListBySurname(this.primaryAuthorList, coauthorList);
                    //primaryAuthorPositions = findPrimaryAuthorListBySurname(primaryAuthorList);

                    // if no primary author found by surname try harder
                    if (primaryAuthorPositions.isEmpty()) {
                          ArrayList<Author> authorShiftedList = new ArrayList();
                          for (Author a : coauthorList) authorShiftedList.add(a.surnameFirstNameSplit());
                          //primaryAuthorPositions = findPrimaryAuthorListBySurname(authorShiftedList);
                          primaryAuthorPositions = findAuthorListBySurname(this.primaryAuthorList, authorShiftedList);
                          if (primaryAuthorPositions.isEmpty()) {
                              errorMessage.printlnErr("Excel Row "+(rowNumber+1)+", can not find surname of primary author "+getPrimaryAuthor()+" in list, title is "+titleCellValue);
                              findPrimaryAuthor(coauthorList); // for debugging
                              continue;
                          }
                          warningMessage.printlnErr("Excel Row "+(rowNumber+1)+", surname ends in first name found and dealt with for "+getPrimaryAuthor()+", title is "+titleCellValue);
                          coauthorList=authorShiftedList; // have found author surname in here
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
                              int c= this.primaryAuthorList.get(v).compareToSurnameInitialsAsPossible(coauthorList.get(n));
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
                            errorMessage.printlnErr("Excel Row "+(rowNumber+1)+", serious problem, primary author "+getPrimaryAuthor()+" in list, title is "+titleCellValue);
                            findPrimaryAuthor(coauthorList);// for debugging
                            continue;
                        }
                        if (minValue>0){
                            warningMessage.printlnErr("Excel Row "+(rowNumber+1)+
                                    ", partial initials match (value "+minValue+"), primary author "+
                                    getPrimaryAuthor()+", title is "+
                                    titleCellValue);
                        }
                        if (primaryAuthorPositionsImproved.size()>1){
                            errorMessage.printlnErr("Excel Row "+(rowNumber+1)+", equal quality match to primary author "+getPrimaryAuthor()+", title is "+titleCellValue);
                            for (Integer p:primaryAuthorPositionsImproved){
                            System.err.println("***                            "
                                    + " matched author "
                                    + p +"="+coauthorList.get(p));
                            }
                        }
                        // have found one or more authors with surnames matching
                        // and some initials matching, so take first as match.
                        primaryAuthorPosition=primaryAuthorPositionsImproved.get(0);
                    } // if (coAuthorPossibleList.size()>1)
                    else { // just one primary author was found by surname
                        primaryAuthorPosition=primaryAuthorPositions.get(0);
                        minValue=Author.SURNAMEOFFSET;
                    }
                    Author primaryAuthor = coauthorList.get(primaryAuthorPosition);
                    int primaryAuthorIndex = primaryAuthor.getID();


                    // next locate all other speical authors and check against primary author is in there
                    ArrayList<Author> identifiedCoSpecialAuthorList = processCoAuthorList(specialAuthorSet,
                                coauthorList, warningMessage,
                                "Special Author, Excel Row "+(rowNumber+1)+", title is "+titleCellValue);
//                    if (primaryAuthor!=null) {
//                        primaryAuthorIndex = primaryAuthor.getID(); // set primary author index if possible
//                        // check if this primary author index is consistent
//                        ia=identifiedCoSpecialAuthorList.get(primaryAuthorIndex);
//                        if (ia==null){
//                            errorMessage.printlnErr("Excel Row "+(rowNumber+1)
//                                    +" primary author not found in special author list"
//                                    +", title is "+titleCellValue);
//                        } else{
//                            if (ia.getID()!=primaryAuthorIndex) errorMessage.printlnErr("Excel Row "+(rowNumber+1)
//                                    +" primary author index "+primaryAuthorIndex
//                                    +" does not match matching special author "+ia+", index "+ia.getID()
//                                    +", title is "+titleCellValue);
//                        }
//                    }

                    // next locate all ordinary authors and check against primary author is in there
                    ArrayList<Author> identifiedCoauthorList = processCoAuthorList(ordinaryAuthorSet,
                                coauthorList, warningMessage,
                                "Ordinary Author, Excel Row "+(rowNumber+1)+", title is "+titleCellValue);
//                    if (primaryAuthorIndex>=0) {
//                        // check if this primary author index is consistent
//                        ia=identifiedCoauthorList.get(primaryAuthorIndex);
//                        if (ia!=null){
//                            if (ia.getID()==primaryAuthorIndex) errorMessage.printlnErr("Excel Row "+(rowNumber+1)
//                                    +" primary author index "+primaryAuthorIndex
//                                    +" matches index of ordinary author "+ia
//                                    +", title is "+titleCellValue);
//                            else warningMessage.printlnErr("Excel Row " + (rowNumber + 1)
//                                    + " primary author also matched to different ordinary author "+ia+" but no action taken"
//                                    + ", title is " + titleCellValue);
//                        }
//                    }

                    // check assignments and set up ID as the vertex index
                    int [] coauthorIndex = new int[coauthorList.size()];
                    int [] coauthorType = new int[coauthorList.size()];
                    //int [] coauthorIndex = new int[coauthorList.size()];
                    int index=IUNSET;
                    int type=IUNSET;
                    Author ias;
                    Author iao;
                    for (int pos=0; pos<coauthorList.size(); pos++){
                        ias=identifiedCoSpecialAuthorList.get(pos);
                        iao=identifiedCoSpecialAuthorList.get(pos);
                        int switchIndex = ((ias==null)?2:0)+((iao==null)?1:0);
                        switch (switchIndex){
                            case 3: // ((ias == null) && (iao == null)) 
                                if (pos==primaryAuthorPosition){
                                    errorMessage.printlnErr("Excel Row "+(rowNumber+1)
                                            +" coauthor "+pos
                                            +" primary author index "+primaryAuthorIndex
                                            +" matches index of ordinary author "+iao
                                            +", title is "+titleCellValue);
                                    index=primaryAuthorIndex;
                                    type =0;
                                }else{// no author found, must be new ordinary author
//                                    errorMessage.printlnErr("Excel Row " + (rowNumber + 1)
//                                      +"coauthor "+coauthorList.get(pos)+" not identified");
                                    Author a = coauthorList.get(pos);
                                    index=firstIndexOrdinaryAuthor+ordinaryAuthorSet.size();
                                    a.setID(index);
                                    ordinaryAuthorSet.add(a);
                                    if (!includeOrdinaryAuthors) index=IUNSET;
                                    type =1;
                                }
                            break;
                            case 2: // ordinary author found
                                if (pos==primaryAuthorPosition){
                                    if (iao.getID()==primaryAuthorIndex) errorMessage.printlnErr("Excel Row "+(rowNumber+1)
                                            +" coauthor "+pos
                                            +", primary author index "+primaryAuthorIndex
                                            +" matches index of ordinary author "+iao
                                            +" but no action taken"
                                            +", title is "+titleCellValue);
                                    else warningMessage.printlnErr("Excel Row " + (rowNumber + 1) 
                                            +" coauthor "+pos
                                            +", primary author also matched to different ordinary author "+iao
                                            +" but have different indices so no action taken"
                                            + ", title is " + titleCellValue);
                                    index=primaryAuthorIndex;
                                    type =0;
                                }else{ // ordinary not not primary author
                                    if (includeOrdinaryAuthors) index=iao.getID();                                
                                    else index=IUNSET;
                                    type =1;
                                }
                            break;
                            case 1: // special author found
                                if (pos==primaryAuthorPosition){
                                    if (ias.getID()!=primaryAuthorIndex) errorMessage.printlnErr("Excel Row "+(rowNumber+1)
                                            +" coauthor "+pos
                                            +", primary author index "+primaryAuthorIndex
                                            +" does not match matching special author "+ias+", index "+ias.getID()
                                            +", title is "+titleCellValue);
                                    index=primaryAuthorIndex;
                                    type =0;
                                }
                                else{ //special not primary author
                                    index = ias.getID();
                                    type =0;
                                }
                            break;
                            case 0: // ordinary and special author found
                                if (pos==primaryAuthorPosition){
                                    if (ias.getID()!=primaryAuthorIndex) errorMessage.printlnErr("Excel Row "+(rowNumber+1)
                                            +" coauthor "+pos
                                            +", primary author index "+primaryAuthorIndex
                                            +" does not match matching special author "+ias+", index "+ias.getID()
                                            +" nor matching ordinary author "+iao+", index "+iao.getID()
                                            +", title is "+titleCellValue);
                                    index=primaryAuthorIndex;
                                    type =0;
                                }
                                else{ //special not primary author
                                    warningMessage.printlnErr("Excel Row " + (rowNumber + 1)
                                          +"coauthor "+pos
                                          +" identified as special author "+ias
                                          +" id="+ias.getID()
                                          +", and ordinary author "+iao
                                          +" id="+iao.getID());
                                    index=ias.getID();
                                    type =0;
                                }
                            break;
                        } //eo switch
                        coauthorIndex[pos]=index;
                        coauthorType[pos]=type;
                    } // eo for pos

                    

// This next section is information if the paper lies outside boundaries
//                    if (period==0) {
//                        if (infoOn) System.out.println("*** Row "+rowNumber+
//                                " has year "+year+
//                                " which is below lower boundary "+PeriodBoundary.yearBoundary[0]+", , title is "+shortTitle);
//                        //continue;
//                    }
//                    if (period==PeriodBoundary.yearBoundary.length) {
//                        if (infoOn) System.out.println("*** Row "+rowNumber+
//                                " has year "+year+
//                                " which is above upper boundary "+
//                                PeriodBoundary.yearBoundary[PeriodBoundary.yearBoundary.length-1]
//                                +", , title is "+shortTitle);
//                        //continue;
//                    }

                    // statistics
                    numberPapers++;
                    //numberAuthors.add(primaryAuthorList.size());
//                    if (cg==null) periodStats[period].addOnePaper(primaryAuthorPosition, coauthorList.size(), alphabeticalOrder);
//                    else cg.setCoauthorshipWeights(coauthorList,period,infoOn);

                    // now update graphs
                    if (primaryAuthorPosition<0){throw new RuntimeException("HELP!!!");}
                    int source = primaryAuthorPosition; // currently only updates from primary author
                    double w10SelfLoopCorrection=-1;
                    cg.setCoauthorshipEdgesFromSource(coauthorIndex, period, source, w10SelfLoopCorrection, infoOn);

                    // problem is how to do ordinary-ordinary edges when papers have moe than one special author


                    // final messages on this paper
                    if (infoOn) System.out.println("--- Paper "+numberPapers+" "+shortTitle+" --------------");
                    if (infoOn) System.out.println("Period "+period+", Author rank "+(1+primaryAuthorPosition)+" of "+coauthorList.size());
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
            if (errorMessage.getCount()>0) {
                System.out.println(MessageError.errorLabel+" Number of papers were "+listedPapers+", of which "+numberPapers+" processed");
                System.out.println(MessageError.errorLabel+" Number of errors were "+errorMessage.getCount());
                System.err.println(MessageError.errorLabel+" "+errorMessage.getCount()+" errors found for author (all variants):-");
                for (Author a: primaryAuthorList) System.err.println(MessageError.errorLabel+" "+a.toString());
                if (warningMessage.getCount()>0) System.err.println(MessageWarning.warningLabel+" "+warningMessage.getCount()+" warnings found for author");
                System.err.println(MessageError.errorLabel);
            }
            else  {
                if (warningMessage.getCount()>0)  {
                    if (warningMessage.getCount()>0) System.err.println(MessageWarning.warningLabel+" "+warningMessage.getCount()+" warnings found for author "+getPrimaryAuthor()+"\n!!!");
                    if (warningMessage.getCount()>0) System.out.println(MessageWarning.warningLabel+" "+warningMessage.getCount()+" warnings found for author");
                }
                System.out.println("... Number of papers were "+listedPapers+", of which "+numberPapers+" processed");
                System.out.println("... No errors found for author "+getPrimaryAuthor());
            }

            if (cg==null){
                for (int p=0;p<periodStats.length; p++) totalPapers+=periodStats[p].numberPapers;
                if (totalPapers!=numberPapers) System.err.println("Total number of papers wrong "+totalPapers+" != "+numberPapers);
            }
            summaryString = getPrimaryAuthor()+SEP+listedPapers+SEP+numberPapers+SEP+warningMessage.getCount()+SEP+errorMessage.getCount();

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
     * Identifies coAuthors in given list with authors in given set.
     * If more than one author in set matches then first is taken.
     * If none is found then null is given.
     * @param auhtorSet set of authors used for identification.
     * @param coAuthorList list of coAuthors on a particular paper
     * @param mw generates warning messages if needed
     * @param problemStringPrefix message to prefix warning messages
     * @return list of authors from set in order given in coAuthor list (null if none found)
     */
    static public ArrayList<Author>  processCoAuthorList(TreeSet<Author> authorSet,
            ArrayList<Author> coAuthorList,
            MessageWarning mw, String problemStringPrefix){
        ArrayList<Author> coAuthorPossibleList;
        ArrayList<Author> identifiedCoAuthorList = new ArrayList(coAuthorList.size());
        Author coAuthor;
        for (int caindex=0; caindex<coAuthorList.size(); caindex++){
            coAuthor=coAuthorList.get(caindex);
            coAuthorPossibleList = findAuthorInSet(coAuthor,authorSet);
//            if (! coAuthorPossibleList.isEmpty() && coAuthorPossibleList.size()==1 ) {
            if (! coAuthorPossibleList.isEmpty() ) {
                  coAuthor=coAuthorPossibleList.get(0);
            }
            else {
                // no author found or more than one found
                  mw.printlnErr(problemStringPrefix+", too many possible coauthors for - "+coAuthor);
                  coAuthor=null;
                  continue;
            }
            identifiedCoAuthorList.add(coAuthor);
        } // eo for (int caindex
        return identifiedCoAuthorList;
    }


    /**
     * Finds an author in given list.
     * Tests using first surname only.
     * The author searched for can have multiple names.
     * All matches between two lists are returned.
     * @param findThisAuthor list of alternative version of same author to be found.
     * @param primaryAuthorList ArrayList of authors
     * @return list of positions in list of primary primaryAuthorList.  More than one indicates a problem.
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
     * Finds an author in given set.
     * Tests are done using the comparator built into the set.
     * <b>All</b> matches are returned.
     * @param findThisAuthor author to be found.
     * @param primaryAuthorList TreeSet of authors to search
     * @return list of authors in primaryAuthorList found to match
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




}





