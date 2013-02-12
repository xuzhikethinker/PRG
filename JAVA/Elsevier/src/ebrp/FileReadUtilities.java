/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

import JavaNotes.TextReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author time
 */
public class FileReadUtilities {

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
                columnIndex[c] =findColumn(columns, labelList[c], false);
                if (columnIndex[c]<0) return null; //{foundLabelRow=false; break;}
        }
        return columnIndex;
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



}
