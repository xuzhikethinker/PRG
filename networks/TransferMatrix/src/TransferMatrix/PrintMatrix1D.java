/*
 * PrintMatrix1D.java
 *
 * Created on 06 March 2007, 15:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TransferMatrix;

import java.io.*;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

import TimUtilities.NumbersToString;

/**
 *
 * @author time
 */
public class PrintMatrix1D {
    
    /** Creates a new instance of PrintMatrix1D */
    public PrintMatrix1D() {
    }
    
    // ***************************************************************
        /** Prints CERN Colt DoubleMatrix Matrix to PrintStream.
         *@param matrix Cern Colt Double Matrix
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public String toString(DoubleMatrix1D matrix, String SepString, int dec)
    {
        NumbersToString n2s = new NumbersToString();
        String s="";
        for (int i=0; i<matrix.size(); i++) s=s+n2s.TruncDec(matrix.get(i),dec)+SepString;
        s=s+"\n";
        return s;
    }
        // ***************************************************************
        /** Prints CERN Colt DoubleMatrix Matrix to PrintStream.
         *@param matrix Cern Colt Double Matrix
         *@param PS printstream
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printMatrix(DoubleMatrix1D matrix, PrintStream PS, String SepString, int dec)
    {
        NumbersToString n2s = new NumbersToString();
        for (int i=0; i<matrix.size(); i++) PS.print(n2s.TruncDec(matrix.get(i),dec)+SepString);
        PS.println();
    } // eo printMatrix
    
// ...........................................................................
        /** Prints Prints CERN Colt DoubleMatrix Matrix to on std output.
         *@param matrix Cern Colt Double 1D Matrix
         *@param SepString string used to separate entires, e.g. tab
         *@param dec integer number of decimal places to display
         */
    public void printMatrix(DoubleMatrix1D matrix,  String SepString, int dec)
    {
        printMatrix(matrix, System.out, SepString, dec);
    }

    
}

