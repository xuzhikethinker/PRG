/*
 * LogBin.java
 *
 * Created on 06 June 2006, 18:53
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package DataAnalysis;

import cern.colt.list.IntArrayList;
import cern.colt.list.DoubleArrayList;
import java.io.*;
import java.util.Date;

//import JavaNotes.TextReader;



/**
 * This log bins integer frequency count data.
 * A typical example will be a debgree distribution n(k).
 * @author time
 */
public class LogBin {
    
    String LBVersion ="LogBin060607";
    String SEP = "\t";
    String dataName = "UNKNOWN";
          DoubleArrayList bToXValueArr;  // gives x location of bins 
          DoubleArrayList bToXMinArr;  // gives x location of bins 
          int numberBins=-1; // number of bins 
                             // one less than size of lists above, 
                             //  equal to size of those below
          DoubleArrayList dataLogBinList; // data in the bins
          DoubleArrayList dataLogBinErrorList; // error of data in the bins
          DoubleArrayList dataLogBinSizeList ;
          double lbratio =-1; 
          int infolevel=1;
          
          
          // some stats
          double totalNumberInBins=0;
//          double totalNumber=0;
          double totalSizeInBins=0;
//          double totalSize=0;


    
    /** Creates a new instance of LogBin */
    public LogBin() 
    {  }
    
    private void initialise(){
      dataLogBinList = new DoubleArrayList();
      dataLogBinErrorList = new DoubleArrayList();
      dataLogBinSizeList = new DoubleArrayList();
      bToXValueArr = new DoubleArrayList();
      bToXMinArr = new DoubleArrayList();
      
    }

// ----------------------------------------------------------------------
    /**
     * Puts frequency of count data into log bins. 
     * <p>
     * The input array has a stores the frequency count data
     * so ddarr.get(k) = n(k) where n is the frequency (number of times events) 
     * of size k have occured in all the <tt>numruns</tt> that the count has been repeated..
     * From kmin to kmaxinput, it breaks
     * things up into integer sized bins but tries to keep the ratio of
     * upper bin edge to lower bin edge to kinc rounding down when this
     * is not an integer.  The list dataLogBinList[b] gives information on the
     * b-th bin and the ktobin[k] tells you which bin number degree k
     * belongs to.
     * @param ddarr list of the count data
     * @param kmin start bining from this count
     * @param kmaxinput stop binning at this count number 
     * @param numruns number of runs over which this data was collected.
     *
     */
    public int calcLogBinAny(IntArrayList ddarr, int kmin, int kmaxinput, int numruns, double lbratioInput) {
      if (lbratioInput<=1) return -1;
      lbratio=lbratioInput;
      initialise();   
      int kmax =kmaxinput;
      if (ddarr.size() <= kmax) kmax=ddarr.size()-1; 
      double sigma2;
      int lowerbink,upperbink;
      int kintd;
      
      lowerbink=kmin;                             // lower bin edge
      while (lowerbink<=kmax) {
          upperbink =(int)(lowerbink*lbratio)+1; // next lowerbin k
          if (lowerbink < 1) upperbink=1; //this is the effective upper edge of bin
          bToXValueArr.add( ( (double)(upperbink-1+lowerbink) ) /2.0);     // bin location in k
//      klocation2 = sqrt(lowerbink*(upperbink)); // alternative bin location in k
          kintd= (upperbink-lowerbink);    // size of bin, number of k values included in it
          
          double nn,err;
          double nb=0;
          double nb2=0;
          
          for (int kkk =lowerbink; (kkk<upperbink) && (kkk<=kmax); kkk++) {
              nn = ddarr.get(kkk);
              nb+=nn;
              nb2+=nn*nn;
          }
          dataLogBinList.add(((double) nb) / ((double)(kintd*numruns) ));
          bToXMinArr.add(lowerbink);
          if (kintd>1) sigma2 =  (nb2 - nb*nb/((double)kintd) ) /((double)(kintd*(kintd-1))); else sigma2=-1;
          if (sigma2>0) err = Math.sqrt( sigma2 ); else err =0;
          dataLogBinErrorList.add( err/ ((double)numruns) );
          dataLogBinSizeList.add( kintd );
          lowerbink=upperbink; // set lower end of next bin
      } //eo while
      bToXMinArr.add(lowerbink);
      return 0;
  } // eo CalcLB

 
 // ----------------------------------------------------------------------

     /**
     * Outputs Log Bin Count Frequency data.
     *  @param full filename name of file as string including any directories.
     * @param cc comment characters put at the start of every line
     */
    void FileOutputLogBinFreqData(String fullfilename, String cc, String sep)
    {
     FileOutputLogBinnedFreqData(fullfilename, cc, sep,  true, true);
    }
     /**
      * Outputs Log Bin Count Frequency data.
      * @param full filename name of file as string including any directories.
      * @param cc comment characters put at the start of every line
      * @param sep separation string
      * @param infoOn true if want row of general information
      * @param headersOn true if want header row for data columns
      */
    public void FileOutputLogBinnedFreqData(String fullfilename, String cc, String sep, boolean infoOn, boolean headersOn)
    {
       PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing log bin data to "+fullfilename);
            if (infoOn) {
                Date date = new Date();
                PS.println(cc+" Log Binned Frequency Distribution ratio" + sep + lbratio+ sep + " "+LBVersion+ sep + " "+date);
            }
            double n, gamma, x, err, binsize;
            // Calc totals from binned data
            calcNumbersInBins();
            if (headersOn) PS.println(cc+"count k"+ sep + "   Freqency n(k)   " + sep + "   +/-   " + sep + " bin size dk " + sep + " gamma(k) " + sep + " n(k)*dk " + sep + " k*n(k)*dk " + sep + " log10(k)  " + sep + " log10(n(k)) " + sep + " Total Number (N)=" + sep + " "+totalNumberInBins+"" + sep + " Total Size (E)= " + sep + " "+totalSizeInBins);

            
            // Now output binned results 
            double nlast = 0;
            double klast = 0;
            for (int b =0; b<dataLogBinList.size(); b++)
            {
             n =  dataLogBinList.get(b);
             if (n>0) 
              { 
               x= bToXValueArr.get(b);
               err = dataLogBinErrorList.get(b);
               binsize =getBinSize(b);
               PS.print( x+ sep +n+ sep + err + sep +binsize); 
               if ((nlast>0) && (klast>0)) PS.print(  sep + (- Math.log(nlast/n)/Math.log(klast/x) ));
               PS.print(sep + n*binsize+ sep + x*n*binsize);
               if (x>0) PS.print(sep +Math.log10(x) ); else PS.print( sep );
               if (n>0) PS.print(sep +Math.log10(n) ); else PS.print( sep );
               PS.println();
               nlast=n; 
               klast=x;
              }// if n>0               
             }// for b
            try{
//                System.out.println("Trying to close "+filenamefull);
                fout.close();
                } catch (IOException e) { System.err.println("File Close Error "+fullfilename);}
            } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+fullfilename);
        } //eo catch
        
            if (infolevel>0) System.out.println("\n Finished Log Bin Output to "+fullfilename);
      } //eo FileOutputLogBinDD

    // ----------------------------------------------------------------------

     /**
     * Returns the size of given bin.
     * @param bin number
     */
     public double getBinSize(int b)
     {
         if (b>=dataLogBinList.size()) return -1;
         return (bToXMinArr.get(b+1)-bToXMinArr.get(b));
     }
    
 // ----------------------------------------------------------------------

     /**
     * Outputs information for a connected Undirected graph
     *  <filename> general info
     * @param full filename name of file as string including any directories.
     * @param cc comment characters put at the start of every line
     */
     public int calcNumbersInBins()
     {
             totalNumberInBins = 0;
             totalSizeInBins =0;
         double n,x;
         for (int b = 0; b<dataLogBinList.size(); b++)
         {
             x = bToXValueArr.get(b);
             n =dataLogBinList.get(b)*getBinSize(b);
             totalNumberInBins += n;
             totalSizeInBins += n*x;
         }
         return 0;
     }
    



}// eo LogBin class