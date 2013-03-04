package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Counts number of papers classified by position of authors
 * @author time
 */
class PositionCounts{
     int numberPapers= 0;
     int numberFirstAuthor=  0;
     int numberSecondAuthor= 0;
     int numberFinalAuthor=  0;
     int numberPenultimateAuthor= 0;
     int primaryAuthorPositionOther=0;

     /**
      * Initialise all counts to zero.
      * @param pc existing PositionCounts
      */
     public PositionCounts(){}

     /**
      * Deep copy.
      * @param pc existing PositionCounts
      */
     public PositionCounts(PositionCounts pc){
         add(pc);
     }
     /**
      * Update counts with information from one additional paper.
      * @param primaryAuthorPosition 0=first author, last author = (numberAuthors-1)
      * @param numberAuthors number of authors on paper
      */
     public void addOnePaper(int primaryAuthorPosition, int numberAuthors){
         numberPapers++;
         if (primaryAuthorPosition==0) numberFirstAuthor++;
                    else {
                        if (primaryAuthorPosition==(numberAuthors-1)) numberFinalAuthor++;
                        else {
                            if (primaryAuthorPosition==1) numberSecondAuthor++;
                            else {
                                if (primaryAuthorPosition==(numberAuthors-2)) numberPenultimateAuthor++;
                                else primaryAuthorPositionOther++;
                            }
                        }
                    }
     }
     /**
      * Update counts with information from a PositionCounts object.
      * @param pc contains counts
      */
     public void add(PositionCounts pc){
         this.numberPapers+=pc.numberPapers;
         this.numberFirstAuthor+=pc.numberFirstAuthor;
         this.numberFinalAuthor+=pc.numberFinalAuthor;
         this.numberSecondAuthor+=pc.numberSecondAuthor;
         this.numberPenultimateAuthor+=pc.numberPenultimateAuthor;
         this.primaryAuthorPositionOther+=pc.primaryAuthorPositionOther;
     }

    /**
     * Header for position counts in table.
     * @param name name of data
     * @param sep separation string used to separate entries
     * @return string for use as header in data table
     */
    static public String tableHeader(String name, String sep){
        return (
                tableCountHeader(name,"1st")+sep+
                tableCountHeader(name,"Last")+sep+
                tableCountHeader(name,"2nd")+sep+
                tableCountHeader(name,"PenU")+sep+
                tableCountHeader(name,"Other")+sep+
                tableCountHeader(name,"Total")
                );
     }
    /**
     * Position counts as row in table.
     * @param name name of data
     * @param sep separation string used to separate entries
     * @return string for use ion data table
     */
     public String tableDataRow(String name, String sep){
         String fs = "%"+(name.length()+5)+"d";
         return (
                 tableCountEntry(name, numberFirstAuthor) + sep +
                 tableCountEntry(name, numberFinalAuthor) + sep +
                 tableCountEntry(name, numberSecondAuthor) + sep +
                 tableCountEntry(name, numberPenultimateAuthor) + sep +
                 tableCountEntry(name, primaryAuthorPositionOther) + sep +
                 tableCountEntry(name, numberPapers)
                 );
    }

    /**
     * Header for position counts in table.
     * @param name name of data
     * @param countName name of count type (1st, last etc)
     * @return string for use as header in data table
     */
    static public String tableCountHeader(String name, String countName){
        String fs = "%"+(name.length()+5)+"s";
        return (String.format(fs,name+countName));
     }
    /**
     * Position counts as row in table.
     * @param name name of data
     * @param sep separation string used to separate entries
     * @return string for use ion data table
     */
     public String tableCountEntry(String name, int value){
         String fs = "%"+(name.length()+5)+"d";
         return (String.format(fs,value));
    }

    /**
     * Number of Author  counts as row in table.
     * Use tableCountHeader(name, "Total") for header
     * @param name name of data
     * @return string for use in data table
     */
     public String tableNumberPapersDataRow(String name){
         return tableCountEntry(name, numberPapers);
    }
    /**
     * Number papers fraction.
     * For header use ratioStringHeader(name+"Total")
     * @param name name of data
     * @return string for use in data table
     */
     public String tableNumberPapersDataRowPercentage(String name, int normalisation, String noValue){
         return ratioString(name+"Total",numberPapers,normalisation, noValue);
    }


    public String tableDataRowPercentage(String name, String sep, int normalisation, String noValue){
         return (ratioString(name+"1st",numberFirstAuthor,normalisation, noValue)+sep+
                    ratioString(name+"Last",numberFinalAuthor,normalisation, noValue)+sep+
                    ratioString(name+"2nd",numberSecondAuthor,normalisation, noValue)+sep+
                    ratioString(name+"PenU",numberPenultimateAuthor,normalisation, noValue)+sep+
                    ratioString(name+"Other",primaryAuthorPositionOther,normalisation, noValue)+sep+
                    ratioString(name+"Total",numberPapers,normalisation, noValue)
                    );
    }
    static public String tableHeaderPercentage(String name, String sep){
         return (ratioStringHeader(name+"1st")+sep+
                    ratioStringHeader(name+"Last")+sep+
                    ratioStringHeader(name+"2nd")+sep+
                    ratioStringHeader(name+"PenU")+sep+
                    ratioStringHeader(name+"Other")+sep+
                    ratioStringHeader(name+"Total")
                    );
    }


     public static String ratioStringHeader(String name){
         String fs = "%"+(name.length()+6)+"s";
         return String.format(fs,name+"%");
     }
     public static String ratioString(String name,  int n, int d, String noValue){
         if (d>0) {
             String fs = "%"+(name.length()+6)+".2f";
             return String.format(fs,((double) n)/((double) d));
         }
         String fs = "%"+(name.length()+6)+"s";
         return String.format(fs,noValue);
     }

    /**
     * Header for position counts in table.
     * @param name name of data
     * @param sep separation string used to separate entries
     * @return string for use as header in data table
     */
    static public String tableHeaderOLD(String name, String sep){
        String fs = "%"+(name.length()+5)+"s";
        return (String.format(fs+sep+fs+sep+fs+sep+fs+sep+fs+sep+fs,
                 name+"1st",
                 name+"Last",
                 name+"2nd",
                 name+"PenU",
                 name+"Other",
                 name+"Total"));
     }
    /**
     * Position counts as row in table.
     * @param name name of data
     * @param sep separation string used to separate entries
     * @return string for use ion data table
     */
     public String tableDataRowOLD(String name, String sep){
         String fs = "%"+(name.length()+5)+"d";
         return (String.format(fs+sep+fs+sep+fs+sep+fs+sep+fs+sep+fs,
                    numberFirstAuthor,
                    numberFinalAuthor,
                    numberSecondAuthor,
                    numberPenultimateAuthor,
                    primaryAuthorPositionOther,
                    numberPapers));
    }




}
