package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
//import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


/**
 * This reads and XLS file
 * @author time
 */
public class ExcelXLSFile {


       ArrayList<ArrayList<HSSFCell>> rowList = new ArrayList();
    
        public static void main(String[] args) {

                String fileName = "input\\test.xls";
                ArrayList<ArrayList<HSSFCell>> dataHolder = ReadXLSFile(fileName);
                printCellDataToConsole(dataHolder);
        }

        
        /**
         * Reads and stores XLS Excel file.
         * The routine returns a vector, each entry of which represents one row.
         * Each row is a vector of type HSSFCell.
         * Note that empty cells seem not to be be added to vector. Will have to use
         * row number of cell to determine position.
         * <b>WARNING</B> This does not use the cell and row numbers stored in the
         * HSSFCell representation.  In particular empty cells in model or at end of line
         * are not represented.  So a square table can have a sparse representation.
         * @param fileName full name of file
         * @return a vector of vectors of HSSFCell
         */
        public void ReadAndStoreXLSFile(String fileName) {
            rowList = ReadXLSFile(fileName);
        }
        /**
         * Reads an XLS Excel file.
         * The routine returns a vector, each entry of which represents one row.
         * Each row is a vector of type HSSFCell.
         * Note that empty cells seem not to be be added to vector. Will have to use
         * row number of cell to determine position.
         * <b>WARNING</B> This does not use the cell and row numbers stored in the
         * HSSFCell representation.  In particular empty cells in model or at end of line
         * are not represented.  So a square table can have a sparse representation.
         * @param fileName full name of file
         * @return a vector of vectors of HSSFCell
         */
        public static ArrayList<ArrayList<HSSFCell>> ReadXLSFile(String fileName) {
                ArrayList<ArrayList<HSSFCell>> cellVectorHolder = new ArrayList();

                try {
                        FileInputStream myInput = new FileInputStream(fileName);

                        POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

                        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

                        HSSFSheet mySheet = myWorkBook.getSheetAt(0);

                        Iterator rowIter = mySheet.rowIterator();

//                        String myCellString="";
//                        int n=-1;
//                        short m=-1;
                        while (rowIter.hasNext()) {
                                HSSFRow myRow = (HSSFRow) rowIter.next();
//                                n=myRow.getPhysicalNumberOfCells();
//                                m=myRow.getLastCellNum();
                                Iterator cellIter = myRow.cellIterator();
                                ArrayList<HSSFCell> cellStoreVector = new ArrayList();
                                while (cellIter.hasNext()) {
                                        HSSFCell myCell = (HSSFCell) cellIter.next();
                                        //myCellString = myCell.toString();
                                        cellStoreVector.add(myCell);
                                }
                                cellVectorHolder.add(cellStoreVector);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return cellVectorHolder;
        }

//                /**
//         * Reads and XLS Excel file.
//         * The routine returns a vector, each entry of which represents one row.
//         * Each row is a vector of type HSSFCell.
//         * Note that empty cells seem not to be be added to vector. WIll have to use
//         * row number of cell to determine position.
//         * @param fileName full name of file
//         * @return an ArrayList of ArrayLists of HSSFCell
//         */
//        public static ArrayList<ArrayList<HSSFCell>> ReadXLSFile(String fileName) {
//                ArrayList<ArrayList<HSSFCell>> cellVectorHolder = new ArrayList();
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
//                        int col=-1;
////                        short m=-1;
//                        while (rowIter.hasNext()) {
//                                HSSFRow myRow = (HSSFRow) rowIter.next();
////                                n=myRow.getPhysicalNumberOfCells();
////                                m=myRow.getLastCellNum();
//                                Iterator cellIter = myRow.cellIterator();
//                                rowLength= myRow.getPhysicalNumberOfCells();
//                                ArrayList<HSSFCell> cellStoreVector = new ArrayList();
//                                while (cellIter.hasNext()) {
//                                        HSSFCell myCell = (HSSFCell) cellIter.next();
//                                        col=myCell.getColumnIndex();
//                                        //myCellString = myCell.toString();
//                                        cellStoreVector.addElement(myCell);
//                                }
//                                cellVectorHolder.addElement(cellStoreVector);
//                        }
//                } catch (Exception e) {
//                        e.printStackTrace();
//                }
//                return cellVectorHolder;
//        }

        private static void printCellDataToConsole(ArrayList<ArrayList<HSSFCell>> dataHolder) {

                for (int i = 0; i < dataHolder.size(); i++) {
                        ArrayList<HSSFCell> cellStoreVector = dataHolder.get(i);
                        for (int j = 0; j < cellStoreVector.size(); j++) {
                                HSSFCell myCell = (HSSFCell) cellStoreVector.get(j);
                                String stringCellValue = myCell.toString();
                                System.out.print(stringCellValue + "\t");
                        }
                        System.out.println();
                }
        }


        public static int cellToInteger(HSSFCell cell){
            int cellType = cell.getCellType();
            if (cellType == HSSFCell.CELL_TYPE_NUMERIC) return (int) Math.round(cell.getNumericCellValue());
            if (cellType == HSSFCell.CELL_TYPE_STRING) return Integer.parseInt(cell.toString());
            return ProcessSinglePublicationCSVList.IUNSET;
        }

        public static double cellToDouble(HSSFCell cell){
            int cellType = cell.getCellType();
            if (cellType == HSSFCell.CELL_TYPE_NUMERIC) return cell.getNumericCellValue();
            if (cellType == HSSFCell.CELL_TYPE_STRING) return Double.parseDouble(cell.toString());
            return ProcessSinglePublicationCSVList.DUNSET;
        }

/**
 * Converts a row read in to a string.
 * Useful for output. If a cell produces a null string
 * then the emptyCellString argument is used.  For instance you may
 * prefer a space to be used.
 * Uses the excel column number stored in the HSSFCell representation.
 * Will not use columns beyond numberColumns.
 * Null or empty excel row data produces a row of blanks or correct length.
 * @param sep used to separate cells
 * @param cellLineVector ArrayList of HSSFCells cells
 * @param numberColumns number of columns to use 
 * @param emptyCellString used for empty cells
 * @return string representation of row
 */
     public static String cellRowToString(String sep, ArrayList<HSSFCell> cellLineVector, 
             int numberColumns, String emptyCellString){
        //if ((cellLineVector == null ) || cellLineVector.isEmpty()) return "";
        String [] rowAsString= new String[numberColumns];
        int col=-1;
        String myCellString= "";
        if ((cellLineVector != null ) ) {
            for (int j = 0; j < cellLineVector.size(); j++) {
                    HSSFCell myCell = (HSSFCell) cellLineVector.get(j);
                    col = myCell.getColumnIndex();
                    myCellString= myCell.toString();
                    if ((col>=0) && (col<numberColumns)) rowAsString[col]=myCellString;
            }
        }

        String s="";
        for (int c = 0; c < numberColumns; c++) {
                myCellString=rowAsString[c];
                if ( (myCellString==null) || (myCellString.isEmpty() ) ) myCellString=emptyCellString;
                s=s+(c==0?"":sep)+myCellString;
        }
        return s;
     }


}




