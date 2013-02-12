package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Vector;
/**
 * Apache POI for MS Office files
 * @see  http://poi.apache.org/spreadsheet/quick-guide.html
 */
import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * Process Publication lists for one author as XLS files.
 * @authorOnPaper time
 * @deprecated use {@link imperialmedics.ProcessSinglePublicationCSVList}
 */
public class ProcessSinglePublicationXLSList extends ProcessSinglePublicationCSVList {

          
    Vector dataHolderXLS;


    public static void main(String[] args) {

        ProcessSinglePublicationXLSList pa = new ProcessSinglePublicationXLSList();
        //pa.rootFileName = "Aggarwal Raj";
        pa.rootFileName = "Butt, Dr Simon Julian Bevan";
        pa.inputDirectory ="input\\";
        boolean infoOn=true;
        pa.processXLSFile(infoOn);
        String outputFullFileName = pa.outputDirectory+pa.rootFileName+" PeriodStats.dat";
        //System.out.println("Periods "+pa.periodStats.length);
        writePeriodStatsData(outputFullFileName,  pa.periodStats, infoOn);

    }

    /**
     * Process Vanesh type file of publications by one authorOnPaper
     * @param infoOn true if want info on screen
     */
    public void processXLSFile(boolean infoOn){
        String inputFullFileName = inputDirectory+rootFileName+".xls";
        System.out.println("Processing XLS file "+inputFullFileName);

        dataHolderXLS =  ReadExcelXLSFile.ReadXLSFile(inputFullFileName);

        // find the primary authorOnPaper names
        setPrimaryAuthorXLS(dataHolderXLS);
        if (primaryAuthorList==null) {
            System.out.println("!!! no primary author cell found in file, using file name "+rootFileName);
            primaryAuthorList = new ArrayList();
            Author frn = new Author(rootFileName,',');
            primaryAuthorList.add(frn);
        }

        if (infoOn) {
            System.out.print("Primary Author:- ");
            for (int v=0; v<primaryAuthorList.size(); v++) System.out.print(primaryAuthorList.get(v)+"; ");
            System.out.println();
        }

        // now find the label row
//        int [] labelRowInfo = findLabelRowAuthor(dataHolderXLS);
//        if (labelRowInfo==null) throw new RuntimeException("*** no label row found");
//        labelRow=labelRowInfo[0];
//        yearColumn=labelRowInfo[1];
//        authorColumn=labelRowInfo[2];
//        titleColumn=labelRowInfo[3];
        String [] labelList = {AUTHORLABEL,YEARLABEL,TITLELABEL};
        int [] labelRowInfo = findLabelRow(dataHolderXLS, labelList);
        if (labelRowInfo==null) throw new RuntimeException("*** no label row found");
        yearColumn=labelRowInfo[1];
        authorColumn=labelRowInfo[0];
        titleColumn=labelRowInfo[2];
        labelRow=labelRowInfo[3];
        if (infoOn) System.out.println("Label row "+labelRow
                +", year column "+yearColumn
                +", author column "+authorColumn
                +", title column "+titleColumn);

        //

        authorshipStatisticsXLS(infoOn);
    }


        public void authorshipStatisticsXLS(boolean infoOn){
            //ArrayList<Integer> numberAuthors= new ArrayList();
            int numberPeriods=yearBoundary.length+1;
            periodStats= new PeriodData[numberPeriods];
            for (int p=0; p<numberPeriods; p++) periodStats[p] = new PeriodData();
            int numberErrors=0;
            numberPapers=0;
            for (int rowNumber = labelRow+1; rowNumber< dataHolderXLS.size(); rowNumber++) {
                    //if (rowNumber > labelRow+3) break;
                    Vector cellLineVector = (Vector) dataHolderXLS.elementAt(rowNumber);

                    // title
                    HSSFCell titleCell = (HSSFCell) cellLineVector.elementAt(titleColumn);
                    String titleCellValue = titleCell.toString();
                    int l=Math.min(20, titleCellValue.length());
                    String shortTitle = String.format("%10s",titleCellValue.substring(0, l));


                    // process authorOnPaper
                    HSSFCell authorCell = (HSSFCell) cellLineVector.elementAt(authorColumn);
                    String authorCellValue = authorCell.toString();
                    // This next line splits cell at commas,
                    // but does so multiple times  (the plus sign)
//                    String [] authorList = authorCellValue.split(",+");
//                    Author [] authorOnPaper = new Author[authorList.length];
//                    for (int a=0; a<authorList.length; a++) authorOnPaper[a] = new Author(authorList[a]);
                    // example format fo authorOnPaper cell is
                    //Gurusamy K.S., Aggarwal R., Palanivelu L., Davidson B.R.
                    ArrayList<Author> authorOnPaper=Author.authorList(authorCellValue, ",",' ');

                    HSSFCell yearCell = (HSSFCell) cellLineVector.elementAt(yearColumn);

                    int primaryAuthorPosition = findPrimaryAuthor(authorOnPaper);
                    if (primaryAuthorPosition<0){
                        numberErrors++;
                        System.err.println("*** Row "+rowNumber+" can not find primary author in list, title is "+titleCellValue);
                        continue;
                    }

                    // now process year
                    int yearCellType = yearCell.getCellType();
                    double year=DUNSET;
                    if (yearCellType == HSSFCell.CELL_TYPE_NUMERIC) year = yearCell.getNumericCellValue();
                    if (yearCellType == HSSFCell.CELL_TYPE_STRING) year = Double.parseDouble(yearCell.toString());
                    if (year<0) {
                        numberErrors++;
                        System.err.println("*** Row "+rowNumber+
                                " has wrong cell type, index number "+yearCellType+", , title is "+titleCellValue);
                        continue;
                    }
                    int period = getPeriod(year);
                    if (period==0) {
                        if (infoOn) System.out.println("*** Row "+rowNumber+
                                " has year "+year+
                                " which is below lower boundary "+yearBoundary[0]+", , title is "+shortTitle);
                        //continue;
                    }
                    if (period==yearBoundary.length) {
                        if (infoOn) System.out.println("*** Row "+rowNumber+
                                " has year "+year+
                                " which is above upper boundary "+
                                yearBoundary[yearBoundary.length-1]
                                +", , title is "+shortTitle);
                        //continue;
                    }

                    // statistics
                    numberPapers++;
                    //numberAuthors.add(authorOnPaper.size());
                    boolean alphaOrder = Author.isAlphabeticalOrder(authorOnPaper);
                    periodStats[period].addOnePaper(primaryAuthorPosition, authorOnPaper.size(), alphaOrder);

                    if (infoOn) System.out.println("--- Paper "+numberPapers+" "+shortTitle+" --------------");
                    if (infoOn) System.out.println("Period "+period+", Author rank "+(1+primaryAuthorPosition)+" of "+authorOnPaper.size());
            } //eo for rowNumber
            int missingPapers=dataHolderXLS.size()-1- numberPapers;
            System.out.println("*** Number of papers were "+(dataHolderXLS.size()-labelRow-1)+", of which "+numberPapers+" in correct eras");
            if (numberErrors>0) System.out.println("*** Number of errors were "+numberErrors);
            else  System.out.println("No errors found");

            int totalPapers=0;
            for (int p=0; p<periodStats.length; p++) totalPapers+=periodStats[p].numberPapers;
            if (totalPapers!=numberPapers) System.err.println("Total number of papers wrong "+totalPapers+" != "+numberPapers);

            outputSummary(infoOn);
    }



    /**
     * Finds all versions of name of primary authorOnPaper.
     * The primary authorOnPaper should be listed as a separate cell of the type
     * <tt>Authors: Aggarwal, R.; Aggarwal, Rajesh K.</tt>
     * The routine should then return an array with each version of the name,
     * e.g. here [0]=Aggarwal, R. and [1] Aggarwal, Rajesh K.
     * @param dataHolderXLS vector of vectors of cells
     * @return array of different versions of primary authorOnPaper names, null if non found
     */
    public void setPrimaryAuthorXLS(Vector dataHolder){
        final String authorLabel = "Authors:";
        String separatorNames=";";
        char separatorSurnameInitials=',';
        for (int rowNumber = 0; rowNumber< dataHolder.size(); rowNumber++)
        {
            Vector cellLineVector = (Vector) dataHolder.elementAt(rowNumber);

            // find cell with common authorOnPaper name for this sheet
            String authorCellValue=SUNSET;
            int column=0;
            for (column = 0; column < cellLineVector.size(); column++) {
                HSSFCell authorCell = (HSSFCell) cellLineVector.elementAt(column);
                authorCellValue = authorCell.toString();
                if (authorCellValue.startsWith(authorLabel)) break;
                }
            if (column< cellLineVector.size()) {
                String allVersionsAuthor= authorCellValue.substring(authorLabel.length());
                primaryAuthorList = Author.authorList(allVersionsAuthor,separatorNames, separatorSurnameInitials);
                return;
            }
        }
        return;
    }

//    /**
//     * Returns primary authorOnPaper.
//     * @return string using first primary authorOnPaper representation, empty string if none
//     */
//    public String getPrimaryAuthor(){
//        if (primaryAuthorList==null || primaryAuthorList.isEmpty()) return "";
//        return primaryAuthorList.get(0).toString();
//    }

//    /**
//     * Finds primary authors in given list.
//     * Tests using first initial only.
//     * @param authorOnPaper ArrayList of authors
//     * @return position in list of primary authorOnPaper.  negative indicates a problem.
//     * @see Author#equalUptoFirstInitial(java.lang.Object)
//     */
//    public int  findPrimaryAuthor(ArrayList<Author> author){
//        int pos=IUNSET;
//        for (int a=0; a<author.size(); a++){
//            for (int v=0; v<this.primaryAuthorList.size(); v++){
//              if (author.get(a).equalUptoFirstInitial(primaryAuthorList.get(v))) {
//                  if (pos>=0) return (-a-1); // found second primary authorOnPaper
//                  pos=a;
//                  break; //
//              } // eo if
//            } // eo for v
//        } // eo for a
//        return pos;
//    }

   

    

     /**
     * Finds row of column labels.
     * Looks for a row containing given labels.  Must match exactly.
      * The list returned is a list of the column numbers (first column is column 0)
      * for each of the labels in the list given, in the same order.  The last entry
      * is the number of the row with these labels.
     * @param dataHolderXLS vector of each row, a row being a vector of cells
     * @param labelList list of strings with labels
     * @return null if not found, an array of {column of label 0, ... column of last label, row number}
     */
    public static int [] findLabelRow(Vector dataHolderXLS, String [] labelList){
        int [] column=new int[labelList.length+1];
        for (int rowNumber = 0; rowNumber< dataHolderXLS.size(); rowNumber++)
        {
            boolean foundLabelRow=true;
            Vector cellLineVector = (Vector) dataHolderXLS.elementAt(rowNumber);
            for(int l=0; l<labelList.length; l++){
                column[l] =ProcessSinglePublicationXLSList.findColumn(cellLineVector, labelList[l], false);
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
     * @param cellLineOneVector vector of HSSFCell values for row of column labels
     * @return column (numbered from 0) with label, negative if non found.
     */
    public static int findColumn(Vector cellLineOneVector, String label, boolean infoOn){
                int labelColumn=-1;
                for (int j = 0; j < cellLineOneVector.size(); j++) {
                                HSSFCell myCell = (HSSFCell) cellLineOneVector.elementAt(j);
                                String stringCellValue = myCell.toString();
                                if (stringCellValue.startsWith(label)) labelColumn=j;
                        }
                if (labelColumn<0) {
                    if (infoOn) System.err.println("*** No column starts with "+label);
                }
                else if (infoOn) System.out.println("column "+labelColumn+ " is labelled with "+label);
                return labelColumn;
    }



}





