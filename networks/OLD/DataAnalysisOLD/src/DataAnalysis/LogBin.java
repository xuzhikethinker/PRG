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

//      dataLogBinList = new DoubleArrayList();
//      dataLogBinErrorList = new DoubleArrayList();
//      dataLogBinSizeList = new DoubleArrayList();
//      bToXValueArr = new DoubleArrayList();
          
          
          // some stats
          double totalNumberInBins=0;
//          double totalNumber=0;
          double totalSizeInBins=0;
//          double totalSize=0;


    
    /** Creates a new instance of LogBin */
    public LogBin() 
    {
        // Global
      dataLogBinList = new DoubleArrayList();
      dataLogBinErrorList = new DoubleArrayList();
      dataLogBinSizeList = new DoubleArrayList();
      bToXValueArr = new DoubleArrayList();
      bToXMinArr = new DoubleArrayList();
      
    }

// ----------------------------------------------------------------------
    /**
     * Puts frequency of count data into log bins. 
     * The input array has a stores the frequency count data
     * so ddarr.get(k) = n(k) where n is the frequency (number of times events) of size k have occured.
     * From kmin to kmaxinput, it breaks
     * things up into integer sized bins but tries to keep the ratio of
     * upper bin edge to lower bin edge to kinc rounding down when this
     * is not an integer.  The list dataLogBinList[b] gives information on the
     * b-th bin and the ktobin[k] tells you which bin number degree k
     * belongs to.
     * @param ddarr DoubleArrayList of the degree data
     * @param kmin lowest degree
     * @param kmaxinput largest degree
     * @param number of runs.
     *
     */
    public int calcLogBinAny(IntArrayList ddarr, int kmin, int kmaxinput, int numruns) {
    
      if (lbratio<=1) return 1;
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
          };
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
    void FileOutputLogBinFreqData(String fullfilename, String cc)
    {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing log bin data to "+fullfilename);
            Date date = new Date();
            PS.println(cc+" Log Binned Frequency Distribution ratio" + SEP + lbratio+ SEP + " "+LBVersion+ SEP + " "+date);
            
            double n, gamma, x, err, binsize;
            // Calc totals from binned data
            calcNumbersInBins();
            PS.println(cc+"count k"+ SEP + "   Freqency n(k)   " + SEP + "   +/-   " + SEP + " bin size dk " + SEP + " gamma(k) " + SEP + " n(k)*dk " + SEP + " k*n(k)*dk " + SEP + " log10(k)  " + SEP + " log10(n(k)) " + SEP + " Total Number (N)=" + SEP + " "+totalNumberInBins+"" + SEP + " Total Size (E)= " + SEP + " "+totalSizeInBins);

            
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
               PS.print( x+ SEP +n+ SEP + err + SEP +binsize); 
               if ((nlast>0) && (klast>0)) PS.print(  SEP + (- Math.log(nlast/n)/Math.log(klast/x) ));
               PS.print(SEP + n*binsize+ SEP + x*n*binsize);
               if (x>0) PS.print(SEP +Math.log10(x) ); else PS.print( SEP );
               if (n>0) PS.print(SEP +Math.log10(n) ); else PS.print( SEP );
               PS.println();
               nlast=n; 
               klast=x;
              }// if n>0               
             }// for b
            try{
//                System.out.println("Trying to close "+filenamefull);
                fout.close();
                } catch (IOException e) { System.out.println("File Close Error "+fullfilename);}
            } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+fullfilename);
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