/*
 * PrintMatrix2D.java
 *
 * Created on 06 March 2007, 14:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TransferMatrix;

import java.io.*;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

import TimUtilities.NumbersToString;

/**
 * Prints Cern Colt Matrices
 * @author time
 */
public class PrintMatrix2D {
    
    /**
     * Creates a new instance of PrintMatrix2D
     */
    public PrintMatrix2D() {
    }
    
    
    // ***************************************************************
        /** Prints CERN Colt DoubleMatrix Matrix to PrintStream.
         *@param matrix Cern Colt Double Matrix
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public String toString(DoubleMatrix2D matrix, String SepString, int dec)
    {
        NumbersToString n2s = new NumbersToString();
        String s="";
        for (int i=0; i<matrix.rows(); i++)
        {
           for (int j=0; j<matrix.columns(); j++)
           {
            s=s+n2s.TruncDec(matrix.get(i,j),dec)+SepString;
            }
           s=s+"\n";
           }
        return s;
    }
        // ***************************************************************
        /** Prints CERN Colt DoubleMatrix Matrix to PrintStream.
         *@param matrix Cern Colt Double Matrix
         *@param PS printstream
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printMatrix(DoubleMatrix2D matrix, PrintStream PS, String SepString, int dec)
    {
        NumbersToString n2s = new NumbersToString();
        for (int i=0; i<matrix.rows(); i++)
        {
           for (int j=0; j<matrix.columns(); j++)
           {
            PS.print(n2s.TruncDec(matrix.get(i,j),dec)+SepString);
            }
           PS.println();
           }
    } // eo printMatrix
 
            // ***************************************************************
        /** Prints row of CERN Colt DoubleMatrix Matrix to PrintStream.
         *@param row row to be printed
         *@param matrix Cern Colt Double Matrix
         *@param PS printstream
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printMatrixRow(int row, DoubleMatrix2D matrix, PrintStream PS, String SepString, int dec)
    {
        NumbersToString n2s = new NumbersToString();
        for (int j=0; j<matrix.columns(); j++) PS.print(n2s.TruncDec(matrix.get(row,j),dec)+SepString);
        PS.println();        
    } // eo printMatrix
 
        /** Prints row of CERN Colt DoubleMatrix Matrix to PrintStream.
         *@param column column to be printed
         *@param matrix Cern Colt Double Matrix
         *@param PS printstream
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printMatrixColumn(int column, DoubleMatrix2D matrix, PrintStream PS, String SepString, int dec)
    {
        NumbersToString n2s = new NumbersToString();
        for (int j=0; j<matrix.columns(); j++) PS.print(n2s.TruncDec(matrix.get(j,column),dec)+SepString);
        PS.println();        
    } // eo printMatrix
 
// ...........................................................................
        /** Prints Prints CERN Colt DoubleMatrix Matrix to on std output.
         *@param matrix Cern Colt Double Matrix
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printMatrix(DoubleMatrix2D matrix,  String SepString, int dec)
    {
        printMatrix(matrix, System.out, SepString, dec);
    }

    
}
