package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author time
 */
class PeriodData{
     /**
      * Total number of papers.
      */
     int numberPapers=0;
     /**
      * Counts of different types of papers by paperType.
      * @see #paperTypeString(int)
      * @see ImperialMedics.ImperialMedics.PositionCounts
      */
     PositionCounts [] positionCountsByType=new PositionCounts[4];


//     int [] numberSecondAuthor=new int[4];
//     int [] numberFinalAuthor=new int[4];
//     int [] numberPenultimateAuthor=new int[4];
//     int [] primaryAuthorPositionOther=new int[4];
     ArrayList<Integer> numberAuthorsList=new ArrayList();
     ArrayList<Integer> authorPositionList=new ArrayList();

     /**
      * Constant for no value assigned.
      */
     static final double DNOVALUE = -97531246e67;

     /**
      * String used when no value assigned.
      */
     static final String SNOVALUE = " ";

     /**
      * String to label number of author data
      */
     static final String NUMBERAUTHORSLABEL="No.Auth.";

     /**
      * String to label team size data
      */
     static final String TEAMSIZELABEL="Team.";

     /*
      * String used to abbreviate word number in table.
      */
     static final String NUMBERSTRING ="#";

     public PeriodData(){
         for (int pt=0; pt<positionCountsByType.length; pt++) {
             positionCountsByType[pt] = new PositionCounts();
         }
     }

     /**
      * Deep copy.
      * @param PerodData structure to be copied
      */
     public PeriodData(PeriodData pd){
         this.numberPapers=pd.numberPapers;
         for (int pt=0; pt<positionCountsByType.length; pt++) {
             positionCountsByType[pt] = new PositionCounts(pd.positionCountsByType[pt]);
         }
         this.numberAuthorsList= new ArrayList(pd.numberAuthorsList);
         this.authorPositionList= new ArrayList(pd.numberAuthorsList);

     }

     /**
      * Gives paper type code.
      * Boolean bits indicate type of paper.
      * @param numberAuthors
      * @param alphabeticalOrder
      * @return
      * @see #paperTypeString(int)
      */
     static protected int getPaperType(int numberAuthors, boolean alphabeticalOrder){
         if (numberAuthors>1) return (alphabeticalOrder?3:2);
         return (alphabeticalOrder?1:0);
     }

     /**
      * Paper type description.
      * Boolean bits indicate type of paper.
      * @param paperType
      * @return description of type of paper
      */
     private String paperTypeString(int paperType){
         boolean teamAuthor = ( ((paperType & 2) >0 )?true:false);
         boolean alphabeticalOrder = ( (paperType & 2) >0?true:false);
         return (teamAuthor?"Team":"Solo")+" "+(alphabeticalOrder?"":"Non")+"Alphabetical";
     }

     /**
      * Update statistics with one additional paper.
      * @param primaryAuthorPosition
      * @param numberAuthors
      * @param alphabeticalOrder true if authors on paper were in alphabetical order
      */
     public void addOnePaper(int primaryAuthorPosition, int numberAuthors, boolean alphabeticalOrder){
         numberPapers++;
         int paperType = getPaperType(numberAuthors, alphabeticalOrder);
         //numberPapersByType[paperType]++;
         numberAuthorsList.add(numberAuthors);
         authorPositionList.add(primaryAuthorPosition);
         this.positionCountsByType[paperType].addOnePaper(primaryAuthorPosition, numberAuthors);

     }

      /**
       * Mean number of authors on all papers.
       * If undefined (no such papers) then DNOVALUE is returned.
       * @return mean team size
       */
     public double getMeanNumberAuthors(){
         if (numberPapers==0) return DNOVALUE;
         int ta=0;
         for (Integer na: numberAuthorsList) ta += na;
         return ((double) ta)/((double) numberPapers);
     }

      /**
       * Mean number of authors on team papers.
       * A team is the set of coauthors  on a paper.
       * Here team papers are ones with more than one author.
       * If undefined (no such papers) then DNOVALUE is returned.
       * @return mean team size on team papers.
       */
     public double getMeanTeamSize(){
         int numberTeamPapers=0;
         int ta=0;
         for (Integer na: numberAuthorsList) {
             if (na>1) {
                 ta += na;
                 numberTeamPapers++;
             }
         }
         if (numberTeamPapers==0) return DNOVALUE;
         return ((double) ta)/((double) numberTeamPapers);
     }

//      /**
//       * Mean number of authors on team papers.
//       * A team is the set of coauthors  on a paper.
//       * Here team papers are ones with more than one author.
//       * If undefined (no such papers) then DNOVALUE is returned.
//       * @return mean team size on team papers.
//       */
//     public double getMeanTeamSizeX(){
//         int numberTeamPapers=0;
//         double ta=0;
//         for (Integer na: numberAuthorsList) {
//             if (na>1) {
//                 ta += na;
//                 numberTeamPapers++;
//             }
//         }
//         if (numberTeamPapers==0) return DNOVALUE;
//         return ta/numberTeamPapers;
//     }



      /**
       * Number  of team papers.
       * Any paper with 2 or more authors on it.
       * @return number of team papers.
       */
     public int getNumberTeamPapers(){
         return positionCountsByType[2].numberPapers+positionCountsByType[3].numberPapers;
     }

      /**
       * Number  of team papers.
       * Any paper with just one authors.
       * @return number of solo papers.
       */
     public int getNumberSoloPapers(){
         return positionCountsByType[0].numberPapers+positionCountsByType[1].numberPapers;
     }

     public PositionCounts getAllPapersPositionCounts(){
        PositionCounts allPapers = new PositionCounts();
        for (int t=0; t<this.positionCountsByType.length; t++)
            allPapers.add(this.positionCountsByType[t]);
        return allPapers;
     }
     /**
      * Five numbers for distribution of author number on all papers.
      * @return five numbers for distribution of author number on all papers.
      * @see #fiveNumbers(java.util.ArrayList)
      */
     public int [] fiveNumbersAuthorNumber(){
         return fiveNumbers(numberAuthorsList);
     }
     /**
      * Five numbers for distribution of author number on team papers.
      * Team papers have more than one author.
      * @return five numbers for distribution of author number on all papers.
      * @see #fiveNumbers(java.util.ArrayList)
      */
     public int [] fiveNumbersTeamSize(){
         ArrayList<Integer> teamSizeList = new ArrayList(numberAuthorsList);
         ArrayList<Integer> oneList = new ArrayList();
         oneList.add(new Integer(1));
         teamSizeList.removeAll(oneList);
         return fiveNumbers(teamSizeList);
     }
     /**
      * Returns five numbers for a distribution of numbers.
      * The five numbers are
      * <ul>
      * <li>[0] = min value</li>
      * <li>[1] = 1st quartile</li>
      * <li>[2] = median (2nd quartile)</li>
      * <li>[3] = 3rd quartile</li>
      * <li>[4] = max value</li>
      * </ul>
      * If there is nothing in the list then null is returned.
      * <b>NOTE</b> List is returned sorted.
      * @param integerList list of integers.
      * @return int array of the five numbers describing the distribution.
      */
     static public int [] fiveNumbers(ArrayList<Integer> integerList){
         if (integerList.isEmpty()) {
             //int [] fiveNumArray = {0,0,0,0,0};
             return  null; //fiveNumArray;
         }
         Collections.sort(integerList);
         double quantile=((double) integerList.size())/4.0;
         int q1 = (int) Math.floor(quantile);
         int q2 = (int) Math.floor(quantile*2.0);
         int q3 = (int) Math.floor(quantile*3.0);
         int [] fiveNumArray = {integerList.get(0),
                        integerList.get(q1),
                        integerList.get(q2),
                        integerList.get(q3),
                        integerList.get(integerList.size()-1)};
         return fiveNumArray;
     }


     public String tableDataRowPietro(String sep, String noEntry){
        PositionCounts soloPapers = new PositionCounts(this.positionCountsByType[0]);
        soloPapers.add(this.positionCountsByType[1]);
        PositionCounts teamPapers = new PositionCounts(this.positionCountsByType[2]);
        teamPapers.add(this.positionCountsByType[3]);
        PositionCounts allPapers = new PositionCounts(teamPapers);
        allPapers.add(soloPapers);

        return tableNumberAuthorsDataRow(sep)
                + sep + tableTeamSizeDataRow(sep)
                + sep + allPapers.tableDataRow("All.", sep)
                + sep + allPapers.tableDataRowPercentage("All.",sep,numberPapers,noEntry)
                + sep + soloPapers.tableNumberPapersDataRow("Solo.")
                + sep + soloPapers.tableNumberPapersDataRowPercentage("Solo.Total", numberPapers, noEntry)
                + sep + teamPapers.tableDataRow("Team.", sep)
                + sep + teamPapers.tableDataRowPercentage("Team.",sep,numberPapers,noEntry)
                + sep + positionCountsByType[2].tableDataRow("NAlphaTeam.", sep)
                + sep + positionCountsByType[2].tableDataRowPercentage("NAlphaTeam.",sep,numberPapers,noEntry)
                + sep + positionCountsByType[3].tableDataRow("AlphaTeam.", sep)
                + sep + positionCountsByType[3].tableDataRowPercentage("AlphaTeam.",sep,numberPapers,noEntry)
                ;
    }

     static public String tableHeaderPietro(String sep){
         return tableNumberAuthorsHeader(sep)
                + sep + tableTeamSizeHeader(sep)
                + sep + PositionCounts.tableHeader("All.", sep)
                + sep + PositionCounts.tableHeaderPercentage("All.",sep)
                + sep + PositionCounts.tableCountHeader("Solo.", "Total")
                + sep + PositionCounts.ratioStringHeader("Solo.Total")
                + sep + PositionCounts.tableHeader("Team.", sep)
                + sep + PositionCounts.tableHeaderPercentage("Team.",sep)
                + sep + PositionCounts.tableHeader("NAlphaTeam.", sep)
                + sep + PositionCounts.tableHeaderPercentage("NAlphaTeam.",sep)
                + sep + PositionCounts.tableHeader("AlphaTeam.", sep)
                + sep + PositionCounts.tableHeaderPercentage("AlphaTeam.",sep)
                ;
     }







     static public String tableNumberAuthorsHeader(String sep){
        return tableSixNumbersHeader(NUMBERAUTHORSLABEL,sep);
     }
     public String tableNumberAuthorsDataRow(String sep){
            double meanNumberAuthors=getMeanNumberAuthors();
            int [] fiveNumArray = this.fiveNumbersAuthorNumber();
            return (tableMeanDataRow(NUMBERAUTHORSLABEL,  meanNumberAuthors)+sep+
                    tableFiveNumbersDataRow(NUMBERAUTHORSLABEL,sep,fiveNumArray));
    }
     static public String tableTeamSizeHeader(String sep){
        return tableSixNumbersHeader(TEAMSIZELABEL,sep);
     }
     public String tableTeamSizeDataRow(String sep){
            double meanTeamSize=getMeanTeamSize();
            int [] fiveNumArray = this.fiveNumbersTeamSize();
            return (tableMeanDataRow(TEAMSIZELABEL,  meanTeamSize)+sep+
                    tableFiveNumbersDataRow(TEAMSIZELABEL,sep,fiveNumArray));
    }

     /**
      * Table header  for mean and five numbers output
      * @param sep separation between entries
      * @param name name of distribution
      * @return
      */
     static public String tableSixNumbersHeader(String name, String sep){
         String fs = "%"+(name.length()+5)+"s";
         return (String.format(fs,name+"Mean")+sep+tableFiveNumbersHeader(name, sep));
     }
     /**
      * Header for five numbers of a distribution.
      * @param name name of distribution to use
      * @param sep separation string between entries
      * @return string
      */
     static public String tableFiveNumbersHeader(String name, String sep){
         String fs = "%"+(name.length()+5)+"s";
         return (String.format(fs+sep+fs+sep+fs+sep+fs+sep+fs,
                 name+"Min",
                 name+"Q1",
                 name+"Q2",
                 name+"Q3",
                 name+"Max"));
     }
     /**
      * Row of data for five numbers of a distribution.
      * @param name name of distribution to use
      * @param sep separation string between entries
      * @param integer array in five number format
      * @param noEntry string to use if fiveNumbers are null
      * @return string of five number data
      * @see #fiveNumbers(java.util.ArrayList)
      */
     static public String tableFiveNumbersDataRow(String name, String sep,
             int [] fiveNumArray){
            if (fiveNumArray==null) {
                String fs = "%"+(name.length()+5)+"s";
                return (String.format(fs+sep+fs+sep+fs+sep+fs+sep+fs,
                        SNOVALUE,SNOVALUE,SNOVALUE,SNOVALUE,SNOVALUE));
            }
            String fs = "%"+(name.length()+5)+"d";
            return (String.format(fs+sep+fs+sep+fs+sep+fs+sep+fs,
                    fiveNumArray[0],
                    fiveNumArray[1],
                    fiveNumArray[2],
                    fiveNumArray[3],
                    fiveNumArray[4]));
    }
     /**
      * String for mean value in table.
      * @param name name of distribution to use
      * @param mean mean value
      * @return string for mean value
      */
     static public String tableMeanDataRow(String name,  double mean){
         return tableDoubleDataRow(name, mean);
     }

     /**
      * String for header of double value in table.
      * @param name full name of value
      * @return string for header of double value
      */
     static public String tableDoubleHeader(String name){
         String fs = "%"+(name.length()+5)+"s";
         return String.format(fs,name);
     }
     /**
      * String for double value in table.
      * @param name name of distribution to use
      * @param value double value to be displayed
      * @return string for value
      */
     static public String tableDoubleDataRow(String name,  double value){
         if (Math.abs(value/DNOVALUE-1.0)<1e-6) {
             String fs = "%"+(name.length()+5)+"s";
             return String.format(fs,SNOVALUE);
         }
         String fs = "%"+(name.length()+5)+".2f";
         return String.format(fs,value);
     }
     /**
      * String for header of fraction entry in table.
      * A percentage sign is added to the name
      * @param name name of distribution to use
      * @return string for value
      */
     static public String tableFractionHeader(String name){
         String fs = "%"+(name.length()+6)+"s";
         return String.format(fs,name+"%");
     }
     /**
      * String for double value in table.
      * @param name name of distribution to use
      * @param value double value to be displayed
      * @param total number of events (must be non negative)
      * @return string for value
      */
     static public String tableFractionDataRow(String name,  double value, int total){
         if ( (total<=0) || (Math.abs(value/DNOVALUE-1.0)<1e-6) ){
             String fs = "%"+(name.length()+6)+"s";
             return String.format(fs,SNOVALUE);
         }
         double fraction = value /total;
         String fs = "%"+(name.length()+6)+".2f";
         return String.format(fs,value);
     }


     static public String tableHeaderPercentage(String sep){
         return (String.format("%6s"+sep+"%6s"+sep+"%6s"+sep+"%6s"+sep+"%6s",
                 "1st%","Last%","2nd%","PenU%","Other%"));
     }


}

