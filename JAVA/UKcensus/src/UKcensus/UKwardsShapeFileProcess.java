/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package UKcensus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.poifs.filesystem.POIFSFileSystem;
//
/**
 *
 * @author time
 */
public class UKwardsShapeFileProcess {

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


    public static void main(String[] args) {

    }

   public void ProcessShapeFile(){
        int [] distances;
        PrintStream PS;
        FileOutputStream fout;
        // [1] "x"         "y"         "Id"        "PartId"    "NAME"      "X_LA_NAME"
        // [7] "WARD_KEY"
        String inputFileName = "shpexport.xls";
        String outputFileName = "UKwardsvertexinfo.dat";
        try {
            System.out.println("Writing Position Data to file "+ outputFileName);
            fout = new FileOutputStream(outputFileName);
            PS = new PrintStream(fout);

            System.out.println("Processing XLS file "+inputFileName);
//            Vector dataHolderXLS =  ReadXLSFile(inputFileName);

            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +outputFileName+", "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+outputFileName+", "+e.getMessage());
            return;
        }
        System.out.println("Finished writing distances matrix to file "+ outputFileName);
    }

//    /**
//     * Process Vanesh type file of publications by one authorOnPaper
//     * @param infoOn true if want info on screen
//     */
//    public void processXLSFile(Vector dataHolderXLS, PrintStream PS){
//
//
//
//
//
//    }
//
//        /**
//         * Reads and XLS Excel file.
//         * The routine returns a vector, each entry of which represents one row.
//         * Each row is a vector of type HSSFCell.
//         * Note that empty cells seem not to be be added to vector. Will have to use
//         * row number of cell to determine position.
//         * <b>WARNING</B> This does not use the cell and row numbers stored in the
//         * HSSFCell representation.  In particular empty cells in model or at end of line
//         * are not represented.  So a square table can have a sparse representation.
//         * @param fileName full name of file
//         * @return a vector of vectors of HSSFCell
//         */
//        public static Vector ReadXLSFile(PrintStream PS, String fileName) {
//                Vector cellVectorHolder = new Vector();
//
//                try {
//                        FileInputStream myInput = new FileInputStream(fileName);
//
//                        POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
//
//                        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
//
//                        HSSFSheet mySheet = myWorkBook.getSheetAt(0);
//
//                        Iterator rowIter = mySheet.rowIterator();
//
////                        String myCellString="";
////                        int n=-1;
////                        short m=-1;
//                        while (rowIter.hasNext()) {
//                            // read in row
//                            HSSFRow myRow = (HSSFRow) rowIter.next();
////                                n=myRow.getPhysicalNumberOfCells();
////                                m=myRow.getLastCellNum();
//                                Iterator cellIter = myRow.cellIterator();
//                                Vector cellStoreVector = new Vector();
//                                while (cellIter.hasNext()) {
//                                        HSSFCell myCell = (HSSFCell) cellIter.next();
//                                        //myCellString = myCell.toString();
//                                        cellStoreVector.addElement(myCell);
//                                }
//
//
//                                cellVectorHolder.addElement(cellStoreVector);
//                        }
//                } catch (Exception e) {
//                        e.printStackTrace();
//                }
//                return cellVectorHolder;
//        }
//
//
//
//        private static void printCellDataToConsole(Vector dataHolder) {
//
//                for (int i = 0; i < dataHolder.size(); i++) {
//                        Vector cellStoreVector = (Vector) dataHolder.elementAt(i);
//                        for (int j = 0; j < cellStoreVector.size(); j++) {
//                                HSSFCell myCell = (HSSFCell) cellStoreVector.elementAt(j);
//                                String stringCellValue = myCell.toString();
//                                System.out.print(stringCellValue + "\t");
//                        }
//                        System.out.println();
//                }
//        }
//
//
//        public static int cellToInteger(HSSFCell cell){
//            int cellType = cell.getCellType();
//            if (cellType == HSSFCell.CELL_TYPE_NUMERIC) return (int) Math.round(cell.getNumericCellValue());
//            if (cellType == HSSFCell.CELL_TYPE_STRING) return Integer.parseInt(cell.toString());
//            return IUNSET;
//        }
//
//        public static double cellToDouble(HSSFCell cell){
//            int cellType = cell.getCellType();
//            if (cellType == HSSFCell.CELL_TYPE_NUMERIC) return cell.getNumericCellValue();
//            if (cellType == HSSFCell.CELL_TYPE_STRING) return Double.parseDouble(cell.toString());
//            return DUNSET;
//        }
//
///**
// * Converts a row read in to a string.
// * Useful for output. If a cell produces a null string
// * then the emptyCellString argument is used.  For instance you may
// * prefer a space to be used.
// * Uses the excel column number stored in the HSSFCell representation.
// * Will not use columns beyond numberColumns.
// * @param sep used to separate cells
// * @param cellLineVector Vector of HSSFCells cells
// * @param numberColumns number of columns to use
// * @param emptyCellString used for empty cells
// * @return string representation of row
// */
//     public static String cellRowToString(String sep, Vector cellLineVector,
//             int numberColumns, String emptyCellString){
//        if ((cellLineVector == null ) || cellLineVector.isEmpty()) return "";
//        String [] rowAsString= new String[numberColumns];
//        int col=-1;
//        String myCellString= "";
//        for (int j = 0; j < cellLineVector.size(); j++) {
//                HSSFCell myCell = (HSSFCell) cellLineVector.elementAt(j);
//                col = myCell.getColumnIndex();
//                myCellString= myCell.toString();
//                if ((col>=0) && (col<numberColumns)) rowAsString[col]=myCellString;
//        }
//
//        String s="";
//        for (int c = 0; c < numberColumns; c++) {
//                myCellString=rowAsString[c];
//                if ( (myCellString==null) || (myCellString.isEmpty() ) ) myCellString=emptyCellString;
//                s=s+(c==0?"":sep)+myCellString;
//        }
//        return s;
//     }
//

}
