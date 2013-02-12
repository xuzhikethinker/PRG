package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.Vector;

/**
 *
 * @author time
 */
public class AuthorWithData  extends Author {

    /**
     * Number of time periods covered
     */
    int numberPeriods;
    /**
     * Statistics for this author and period.
     * Entry [p] is for period (p)
     */
    PeriodData [] periodData;
    /**
     * Row in Stata10... excel file this author and period.
     * Entry [p] is for period (p)
     */
    Vector [] excelRow;


    /**
    * Splits a string into parts of authors name.
    * Uses splitFullName routine to define input
    * with space to indicate the end of surname
    * @param a string to process into parts of author name
    * @param separatorSurnameInitials character which indicates the end of surname and start of initials
    * @param numberPeriods number of time periods.
    * @see #splitFullName(java.lang.String, char)
    */
   public AuthorWithData(String a, char separatorSurnameInitials, int numberPeriods){
        int r = splitFullName(a, separatorSurnameInitials);
        this.numberPeriods=numberPeriods;
        periodData = new PeriodData[numberPeriods];
        excelRow = new Vector[numberPeriods];
    }

    /**
    * Uses name in existing author for name.
    * @param a use name of this author
    * @param numberPeriods number of time periods.
    * @see #splitFullName(java.lang.String, char)
    */
   public AuthorWithData(Author a, int numberPeriods){
        deepCopyBasicAuthorData(a);
        this.numberPeriods=numberPeriods;
        periodData = new PeriodData[numberPeriods];
        excelRow = new Vector[numberPeriods];
    }


   public void setPeriodData(PeriodData[] pd){
       for (int p=0;p< Math.min(pd.length, numberPeriods); p++){
           periodData[p] = new PeriodData(pd[p]);
       }
   }






   /**
    * Add excel row for given period.
    * @param period period between 0 and (numberPeriods-1)
    * @param row Vector of cells representing line from Stata10 file
    * @return 0 if OK, negative if problem
    */
   public int addExcelRow(int period, Vector row){
       if (!isPeriodOK(period) ) return -1;
       excelRow[period]=row;
       return 0;
   }

   /**
    * Excel row as string.
    * If row does not exist for any reason, empty string returned.
    * @param sep separation string between cells
    * @param period period
    * @return string of excel row cells separated by sep strings, empty if doesn't exist.
    */
   public String excelRowString(String sep, int period, int numberColumns){
       //if (!hasExcelRow(period) ) return "";
       return ReadExcelXLSFile.cellRowToString(sep, excelRow[period], numberColumns, " ");
   }

   /**
    * Period data as string.
    * If period data does not exist for any reason, empty string returned.
    * @param sep separation string between cells
    * @param noEntry string to use if fiveNumbers are null
    * @param period period
    * @return string of period data values separated by sep strings, empty if doesn't exist.
    */
   public String periodDataString(String sep, String noEntry, int period){
       if (!hasPeriodData(period) ) return "";
       return periodData[period].tableDataRowPietro(sep, noEntry);
   }

  static public String periodDataHeaderString(String sep, String noEntry){
       return PeriodData.tableHeaderPietro(sep);
   }

   public String allDataString(String sep, String noEntry, int period, int numberColumns){
       if (!isPeriodOK(period) ) return "";
       return excelRowString(sep, period, numberColumns)+sep+periodDataString(sep, noEntry,period);
   }


   /**
    * Checks to see if excel row exists.
    * Checks period is correct, and if non-empty excel row
    * exist for that period.
    * @param period period to check
    * @return true if excel row exists for given period.
    */
   public boolean hasExcelRow(int period){
       if (isPeriodOK(period) && this.excelRow[period]!=null && (!this.excelRow[period].isEmpty())) return true;
       return false;
    }

   /**
    * Checks to see if excel row exists.
    * Checks period is correct, and if non-empty excel row
    * exist for that period.
    * @param period period to check
    * @return true if excel row exists for given period.
    */
   public boolean hasPeriodData(int period){
       if (isPeriodOK(period) && this.periodData[period]!=null) return true;
       return false;
    }


   /**
    * Checks to see if legal period.
    * must be between 0 and (numberOfPeriod-1) inclusive.
    * @param period period to check
    * @return true if period is legal.
    */
   public boolean isPeriodOK(int period){
       if ((period<0) || (period>=numberPeriods)) return false;
       return true;
   }
}
