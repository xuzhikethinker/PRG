package DistributionAnalysis;
/*
 * DistributionAnalysis.java
 *
 * Created on Tuesday, April 27, 2004 at 16:55
 * Updated 5th September 2004
 */

/**
 *
 * @author  time
 */

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;

import java.io.*;
import java.util.Date;

import JavaNotes.TextReader;



public class DistributionAnalysis {
            // Input parameters
            String DAVersion="DistributionAnalysis050201";
            String nameroot;
            String dirname;
            String inputddext;
            String inputdiststatsext ;
            double lbratio;
            boolean NormaliseDD;
            public int infolevel;
            int outputcontrol;
            int minDegreeRead;
            // Internal paramters
            String[] filelist;
            String[] filenamerootlist;
            DoubleArrayList ddarr = new DoubleArrayList();
            DoubleArrayList ddlbarr = new DoubleArrayList();
            DoubleArrayList ddlberrarr = new DoubleArrayList();
            DoubleArrayList ddlbsizearr = new DoubleArrayList();
            DoubleArrayList btokarr = new DoubleArrayList();
            DoubleArrayList totddarr = new DoubleArrayList();
            DoubleArrayList totdd2arr = new DoubleArrayList();
            long Nvertices;
            long Nedges;
            int kmin;
            int kcont;
            int kmax;
            int totkmin;
            int totkcont;
            int totkmax;
            int nruns;
            OneParamStatistics diameter;
            OneParamStatistics totdistance;
            OneParamStatistics onesdistance;
                  int diametermax;
                  int diametermin;
                  int diameterbigmin;
                  int diameterbigmax;
                  double diameterav;
                  double diametererr;
                  double diametersigma;
                  double totdistanceav;
                  double totdistanceerr;
                  double totdistancesigma;
                  double onesdistanceav;
                  double onesdistanceerr;
                  double onesdistancesigma;
                  
                  int columnnumber;
                  int choosecolumnnumber;

            
    /** Creates a new instance of DistributionAnalysis */
    public DistributionAnalysis() {
                initialiseDistributionAnalysis("test","/PRG/networks/DistributionAnalysis/output/",1.1);
                 }

        /** Creates a new instance of DistributionAnalysis with given file name stem and directory name.
         *@param namert root of name to use for files e.g. test
         *@param dname full path name of directory to use with slash at end e.g. /programme/
         *@param lb log bin ratio to use
         */
    public DistributionAnalysis(String namert, String dname, double lbr)  {
                initialiseDistributionAnalysis(namert,dname, lbr);
                 }

        /** Initialises parameters for constructors.
         *@param namert root of name to use for files e.g. test
         *@param dname full path name of directory to use with slash at end e.g. /programme/
         *@param lb log bin ratio to use
         */
    private void initialiseDistributionAnalysis(String namert, String dname, double lbr) {
                nameroot = namert;
                dirname= dname;
                lbratio=lbr;
                inputddext = ".Jdd.txt";
                inputdiststatsext = ".Jdiststat.txt";
                infolevel = 0;
                outputcontrol = 255;
                NormaliseDD = false;
                minDegreeRead=1;
                nruns=0;
                totkmin=9999999;
                totkcont=-1;
                totkmax=-1;
                diameter= new OneParamStatistics ();
                totdistance = new OneParamStatistics ();
                onesdistance = new OneParamStatistics();
                diameterbigmin = totkmin;
                diameterbigmax = 0;

                columnnumber=2; // columns in data file
                choosecolumnnumber=2; //column with distribution data in

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DistributionAnalysis wa = new DistributionAnalysis();
        int res = wa.ParamParse(args);
        wa.print();
        if (res!=0) return;
        wa.processAll("#" ,  wa.inputddext, "lb"+wa.inputddext,false);
        if ( (wa.outputcontrol & 2) > 0) wa.processDistanceAll("#" ,  "diststats"+wa.inputddext);
        
    }// eo main
    
    



   
// ----------------------------------------------------------------------
    /**
     *  Method of DistributionAnalysis
     *  Calculates log bin data for individual runs
     */
      public int calcLBind() {
        return calcLBany(ddarr,kmin,kmax,1);
    }
    
// ----------------------------------------------------------------------
    /**
     *  Method of DistributionAnalysis
     *  Calculates log bin data for individual runs
     *  Note it returns results in individual logbin arrays
     */
      public int calcLBtot() {
        return calcLBany(totddarr,totkmin,totkmax,nruns);
    }
    
    
// ----------------------------------------------------------------------
    /**
     * Sets up the log bins. 
     * From kminbin to kmaxbin, it breaks
     * things up into integer sized bins but tries to keep the ratio of
     * upper bin edge to lower bin edge to kinc rounding down when this
     * is not an integer.  The list bindata[n] gives information on the
     * n-th bin and the ktobin[k] tells you which bin number degree k
     * belongs to.
     * @param ddarr DoubleArrayList of the degree data
     * @param kmin lowest degree
     * @param kmaxinput largest degree
     * @param number of runs.
     *
     */
    public int calcLBany(DoubleArrayList ddarr, int kmin, int kmaxinput, int numruns) {
    
      if (lbratio<=1) return 1;
      int kmax =kmaxinput;
      if (ddarr.size() <= kmax) kmax=ddarr.size()-1; 
      double sigma2;
      int lowerbink,upperbink;
      int kintd;
      // Global
      ddlbarr = new DoubleArrayList();
      ddlberrarr = new DoubleArrayList();
      ddlbsizearr = new DoubleArrayList();
      btokarr = new DoubleArrayList();

      lowerbink=kmin;                             // lower bin edge
      while (lowerbink<=kmax) {
      upperbink =(int)(lowerbink*lbratio);        //this is the effective upper edge of bin
      btokarr.add( ( (double)(upperbink+lowerbink) ) /2.0);     // bin location in k
//      klocation2 = sqrt(lowerbink*(upperbink)); // alternative bin location in k
      kintd= (upperbink+1-lowerbink);    // size of bin, number of k values included in it

      double nn,err;
      double nb=0;
      double nb2=0;

      for (int kkk =lowerbink; (kkk<=upperbink) && (kkk<=kmax); kkk++)
      { 
         nn = ddarr.get(kkk); 
         nb+=nn; 
         nb2+=nn*nn; 
      };
      ddlbarr.add(((double) nb) / ((double)(kintd*numruns) ));
      if (kintd>1) sigma2 =  (nb2 - nb*nb/((double)kintd) ) /((double)(kintd*(kintd-1))); else sigma2=-1;       
      if (sigma2>0) err = Math.sqrt( sigma2 ); else err =0;
      ddlberrarr.add( err/ ((double)numruns) );
      ddlbsizearr.add( kintd );
      lowerbink=upperbink+1; // set lower end of next bin
      } //eo while
      
      return 0;

  } // eo CalcLB


// ----------------------------------------------------------------------
    /**
     *  Method of DistributionAnalysis
     * @param DoubleArrayList ddar degree distribution
     *
     */
    public void calcNumbers(DoubleArrayList ddarr) 
    {
        Nedges=0;
        Nvertices=0;
        int n=0;
        for(int i=0; i<ddarr.size(); i++) {
            n=(int) (ddarr.get(i)+0.5); 
            Nvertices+=n; 
            Nedges+=n*i;
        }
        return;
    }
    
// ******************************************************************    
// File Output methods    
    
  // *******************************************************************
  /**
     * Outputs information for a connected Undirected graph
     *  <filenameroot>info.txt general info
     * @param filenameroot basis of name of file as string
     * @param cc comment characters put at the start of every line
     */
    void FileOutputDegreeDistribution(String filenameroot, String cc)  {

        String filename = dirname+ filenameroot +".Jdd.txt";
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filename);
            PS = new PrintStream(fout);
            Date date = new Date();
            double n=0;
            double nlast = 0;
            double klast = 0;
            PS.println(cc+" No.vertices:\t"+ Nvertices+" No.edges:\t "+ Nedges +"\t "+DAVersion+"\t "+date);
            PS.println(cc+" k \t n(k) \t gamma(k) ");
            for (int k=0; k<ddarr.size(); k++){
            n = ddarr.get(k);
            if (n>0)  
            {
             PS.print(k+"\t "+n);
             if ((nlast>0) && (klast>0)) PS.print( "\t "+(- Math.log(nlast/n)/Math.log(klast/k) ));
                PS.println();
                nlast=n; 
                klast=k;
            }
            };

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filename);
            return;
        }
    }

  // *******************************************************************
  /**
     * Outputs concise standard version of degree distribution file
     *  <filenameroot><norm>.stddd.txt general info
     * @param filenameroot basis of name of file as string
     * @param cc comment characters put at the start of every line
     */
    void FileOutputDD(String cc, String filenameroot)  
    {
        String normLabel = (NormaliseDD)?"norm":"";
        String filename = dirname + filenameroot + normLabel+".stddd.txt";
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filename);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing data to "+filename);
            Date date = new Date();
            double n=0;
            double nlast = 0;
            double klast = 0;
            PS.println(cc+" No.vertices:\t"+ Nvertices+" No.edges:\t "+ Nedges +"\t "+DAVersion+"\t "+date);
            PS.println(cc+" x \t n(x) \t gamma(x) \t Lines "+ddarr.size());
            for (int k=0; k<ddarr.size(); k++){
            n = ddarr.get(k) ;
            if (n>0)  
            {
             PS.print(k+"\t "+n);
             if ((nlast>0) && (klast>0)) PS.print( "\t "+(- Math.log(nlast/n)/Math.log(klast/k) ));
                PS.println();
                nlast=n; 
                klast=k;
            }
            };

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filename);
            return;
        }
    }

 // ----------------------------------------------------------------------

     /**
     * Outputs information for a connected Undirected graph
     *  <filename> general info
     * @param filename name of file as string
     * @param cc comment characters put at the start of every line
     */
    void FileOutputLogBinDD(String filename, String cc)
    {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        String filenamefull = dirname+ filename;
        try {
            fout = new FileOutputStream(filenamefull);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing log bin data to "+filenamefull);
            Date date = new Date();
            PS.println(cc+" Log Binned Degree Distribution ratio\t "+lbratio+"\t "+DAVersion+"\t "+date);
            
            double n, gamma,k, err, binsize;
            // Calc totals from binned data
            double ntot =0;
            double etot =0;
            for (int b =0; b<ddlbarr.size(); b++)
            {
             n =  ddlbarr.get(b);
             if (n>0) 
              { 
               k= btokarr.get(b);
               binsize =ddlbsizearr.get(b);
               ntot+=n*binsize;
               etot+=k*n*binsize;
              }// if n>0               
             }// for b
            PS.println(cc+" N=\t "+Nvertices+"\t E= \t "+Nedges+"\t (from bins): \t Tot.Vertices \t"+ntot +"\t Tot.Edges \t"+etot);
            PS.println(cc+" k   \t   n(k)   \t   +/-   \t bin size dk \t gamma(k) \t n(k)*dk \t k*n(k)*dk \t log10(k)  \t log10(n(k))");
            
            // Now output binned results 
            double nlast = 0;
            double klast = 0;
            for (int b =0; b<ddlbarr.size(); b++)
            {
             n =  ddlbarr.get(b);
             if (n>0) 
              { 
               k= btokarr.get(b);
               err = ddlberrarr.get(b);
               binsize =ddlbsizearr.get(b);
               PS.print( k+"\t " +n+"\t "+err+"\t "+binsize); 
               if ((nlast>0) && (klast>0)) PS.print( "\t "+(- Math.log(nlast/n)/Math.log(klast/k) ));
               PS.print("\t"+n*binsize+"\t"+k*n*binsize);
               if (k>0) PS.print("\t"+Math.log10(k) ); else PS.print("\t ");
               if (n>0) PS.print("\t"+Math.log10(n) ); else PS.print("\t ");
               PS.println();
               nlast=n; 
               klast=k;
              }// if n>0               
             }// for b
            try{
//                System.out.println("Trying to close "+filenamefull);
                fout.close();
                } catch (IOException e) { System.out.println("File Close Error "+filenamefull);}
            } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filenamefull);
        } //eo catch
        
            if (infolevel>0) System.out.println("\n Finished Log Bin Output to "+filename);
      } //eo FileOutputLogBinDD
    

// ----------------------------------------------------------------------

    
    // ----------------------------------------------------------------------

     /**
     * Outputs total degree distribution data.
     * Output full file name is  <dirname + filename>
     * @param filename file name
     * @param cc comment characters put at the start of every line
     */
    public void FileOutputTotDD(String filename, String cc)
                            {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        String filenamefull = dirname + filename;
        FileOutputTotDD(filenamefull, cc, "\t");
      } //eo FileOutputTotDD
    


     /**
     * Outputs total degree distribution data.
     * @param filenamefull full file name
     * @param cc comment characters put at the start of every line
     */
    public void FileOutputTotDD(String filenamefull, String cc, String Sep)
                            {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamefull);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing total DD data to  " + Sep +filenamefull);
            Date date = new Date();
            PS.println(cc+" Total Degree Distribution for  " + Sep + nruns+ Sep + "  runs of  " + Sep + filenamefull+ Sep + "  "+DAVersion+ Sep + "  "+date+" ");
            //PS.println(cc+" No.vertices: " + Sep + + Nvertices+" No.edges: Sep  "+ Nedges );
            PS.println(cc+"k "+ Sep+" <n(k)> " + Sep + " +/- " + Sep +" gamma(k)");
            double n, err, gamma;
            double nlast = 0;
            double klast = 0;
            for (int k =0; k<totddarr.size(); k++){
            n =  totddarr.get(k)/((double)nruns);
            if (nruns>1) err = Math.sqrt( (totdd2arr.get(k) - n*n*((double)nruns) ) /((double)(nruns*(nruns-1))) ); else err =0;
            if (n>0) 
              { 
                PS.print( k+ Sep + "  " +n+ Sep + "  "+err); 
                if ((nlast>0) && (klast>0))  PS.print( Sep + "  "+(- Math.log(nlast/n)/Math.log(klast/k) ));
                PS.println();
                nlast=n; 
                klast=k;
              };
            };
            try{fout.close();
                } catch (IOException e) { System.out.println("File Close Error "+filenamefull);}
            } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filenamefull);
        } //eo catch
        
            
      } //eo FileOutputTotDD
    


// ----------------------------------------------------------------------

     /**
     * Outputs total distance stats
     *  <filenameroot>info.txt general info
     * @param filenameroot basis of name of file as string
     * @param cc comment characters put at the start of every line
     */
    void FileOutputDistanceStats(String filename, String cc)
                            {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        String filenamefull = dirname + filename;
        try {
            fout = new FileOutputStream(filenamefull);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing total DD data to \t"+filename);
            Date date = new Date();
            PS.println(cc+" Total Distance stats for \t"+diameter.number+"\t runs of \t"+filename+"\t "+DAVersion+"\t "+date+" ");
            PS.println(cc+ "Extremes of diameter: \t"+ diameterbigmin + " \t "+diameterbigmin);
            diameter.print(PS,"diameter");
            totdistance.print(PS,"totdistance");
            onesdistance.print(PS,"onesdistance");
            
            try{fout.close();} catch (IOException e) 
                                { System.out.println("File Close Error "+filename);}
            } catch (FileNotFoundException e) 
            {
                 System.out.println("Error opening output file "+filename);
            } //eo catch
        
            
      } //eo FileOutputTotDD
    

// ----------------------------------------------------------------------

     /**
     * Outputs total distance stats
     * @param filenameroot basis of name of file as string
     * @param cc comment characters put at the start of every line
     */
    void FileOutputDistanceStatsExcel(String filename, String cc)
                            {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        String filenamefull = dirname + filename;
        try {
            fout = new FileOutputStream(filenamefull);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing total DD data to \t"+filename);
            Date date = new Date();
            PS.print(date+"\t "+filename+" \t"+diameter.number);
            totdistance.printExcel(PS,"totdistance");
            onesdistance.printExcel(PS,"onesdistance");
            diameter.printExcel(PS,"diameter");
            PS.println();
            try{fout.close();} catch (IOException e) 
                                { System.out.println("File Close Error "+filename);}
            } catch (FileNotFoundException e) 
            {
                 System.out.println("Error opening output file "+filename);
            } //eo catch
        
            
      } //eo FileOutputTotDD
    


    
// ----------------------------------------------------------------------

    
/**
 *  Method of DistributionAnalysis
 *  Filter to find only one type of file, set up filelist and filenamerootlist
 *  See Schildt p544
 */
    public void getFileList(String ext) {
            // next part Schildt p544
        File dir = new File(dirname);
        if (!dir.isDirectory())
        {System.out.println(dirname+" not a directory");
         return;
        }
        if (infolevel>0) System.out.println("Looking at directory "+dirname);

        FilenameFilter only = new OnlyOneParamSet(nameroot,ext);
        filelist = dir.list(only);
        if (infolevel>0) System.out.println("Found  "+filelist.length+" files with extension "+ext);
        filenamerootlist = new String[filelist.length];
        for (int i =0; i<filelist.length; i++)
        {   filenamerootlist[i] =  getFileNameRoot(filelist[i], ext);
            if (infolevel>0) System.out.println(filelist[i]+"\t "+filenamerootlist[i]);
            
        };
            

    }

// ----------------------------------------------------------------------
/**
 *  Method of DistributionAnalysis
 *  Filter to find only one type of file
 *  See Schildt p544
 */
    public String getFileNameRoot(String filename, String ext){
        int i = filename.lastIndexOf(ext);
        if (i<0) return null;
        return filename.substring(0,i);
              
    }
    
    
    

    // -----------------------------------------------------------------------       
    /**
     * Calculates degree distribution from a graph
     * @param intddarr DoubleArrayList of degree distribution 
     */
    public void getDegreeDistribution(DoubleArrayList doubleddarr)  {
       kmin=99999999;
       kcont=99999999;
       kmax=-1;
//       Nvertices=0;
       int s= doubleddarr.size();
       ddarr = new DoubleArrayList(s);
       double nk;
       for (int k=0; k<s; k++) 
       {   
           nk = doubleddarr.get(k);
           ddarr.add( nk );
           if ((k<kmin) && (nk>0)) kmin=k;
           if ((k>kmin) && (k<kcont) && (nk==0)) kcont=k;
           if ((k>kmax) && (nk>0)) kmax=k;
       }
    }
  // -----------------------------------------------------------------------       
    /**
     * Calculates degree distribution from a graph
     * @param intddarr IntegerArrayList of degree distribution 
     */
    public void getDegreeDistribution(IntArrayList intddarr)  {
       kmin=99999999;
       kcont=99999999;
       kmax=-1;
//       Nvertices=0;
       int s= intddarr.size();
       ddarr = new DoubleArrayList(s);
       for (int k=0; k<s; k++) 
       {   
           int nk = intddarr.get(k);
           ddarr.add( (double) nk  );
           if ((k<kmin) && (nk>0)) kmin=k;
           if ((k>kmin) && (k<kcont) && (nk==0)) kcont=k;
           if ((k>kmax) && (nk>0)) kmax=k;
       }
    }

    // ----------------------------------------------------------------------
        /** 
         *  Method of DistributionAnalysis
         * Reads in Degree Distribution data from file filename ignoring initial lines
         * starting with cc and assuming pairs of doubles after that.
         * @param String filename name of file to read knk data
         * @param String used for comments, first character used only
         *
         */
        public int getDDDataOld(String filename, String cc) {

        String fullfilename = dirname+filename;
      if (infolevel>0) System.out.println("Starting to Degree Distribution data from " + fullfilename);
      TextReader data;     // Character input stream for reading data.

//      double[] number = new double[1000];  // An array to hold all
                                           //   the numbers that are
                                           //   read from the file.

      int res=0;  // error code.

      try {  // Create the input stream.
         data = new TextReader(new FileReader(fullfilename));
      }
      catch (FileNotFoundException e) {
         System.out.println("Can't find file "+filename);
         return 1;
      }

      try {
          if (infolevel>0) System.out.println(" File: "+filename);
          // Read the data from the input file.
          while (data.peek()==cc.charAt(0)) {
          String commentline = data.getln();
          if (infolevel>0) System.out.println(commentline);
          }
          ddarr = new DoubleArrayList();
          int kv=0;
          int k;
          kmin=99999999;
          kcont=99999999;
          kmax=-1;
          double nk;
          while (data.eof() == false) {  // Read until end-of-file.
              k=data.getInt();
              nk= data.getDouble();
 //             System.out.println("Read " + k+"\t "+nk);
              if ((k<kmin) && (nk>0)) kmin=k;
              if ((k>kmin) && (k<kcont) && (nk==0)) kcont=k;
              if ((k>kmax) && (nk>0)) kmax=k;
              while (kv<k) {kv++; ddarr.add(0.0);}
              ddarr.add(nk);
              kv++;
          }//eofile
          if (infolevel>0) System.out.println("Finished " + filename+"\t kmin,kcont,kmax = \t"+kmin+"\t "+kcont+"\t "+kmax);
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          System.out.println("Input Error: " + e.getMessage());
          res=2;
       }
       finally {
          // Finish by closing the files,
          //     whatever else may have happened.
          data.close();
        }
       return res;
    }  // end of getDDData() method
    
  
    // ----------------------------------------------------------------------
        /** 
         *  Method of DistributionAnalysis
         * Reads in  Distribution data from file filename ignoring initial lines
         * starting with cc and assuming columnnumber columns after that
         * The first is the index (degree) and then we choose column
         * columnnumber for the data.
         * @param String filename name of file to read knk data
         * @param String used for comments, first character used only
         * @param Column for the distribution data
         *
         */
        public int getDDData(String filename, String cc) {

        String fullfilename = dirname+filename;
        if (infolevel>0) System.out.println("Starting to Degree Distribution data from " + fullfilename);
        TextReader data;     // Character input stream for reading data.

//      double[] number = new double[1000];  // An array to hold all
                                           //   the numbers that are
                                           //   read from the file.

      int res=0;  // error code.

      try {  // Create the input stream.
         data = new TextReader(new FileReader(fullfilename));
      }
      catch (FileNotFoundException e) {
         System.out.println("Can't find file "+filename);
         return 1;
      }

      try {
          if (infolevel>0) System.out.println(" File: "+filename);
          // Read the data from the input file.
          while (data.peek()==cc.charAt(0)) {
          String commentline = data.getln();
          if (infolevel>0) System.out.println(commentline);
          }
          ddarr = new DoubleArrayList();
          int kv=0;
          int k;
          kmin=99999999;
          kcont=99999999;
          kmax=-1;
          double nk;
          while (data.eof() == false) {  // Read until end-of-file.
              k=((int) (0.5+data.getDouble()));
//              System.out.print("k="+k);
              nk=-99999;
              double datum = nk;
              int colno;
              for (colno=2; data.eoln()==false ; colno++) 
              {
                  datum = data.getDouble(); 
//                  System.out.print("  "+datum);
                  if (colno == choosecolumnnumber) nk=datum;
              };
//              System.out.println(" | "+(colno-1)+" \t "+nk);
              if (k<minDegreeRead) nk=0;
              if (nk<-99998) {System.out.println("Error in getDDData at k="+k); return 3;};
//              if (nk>0) System.out.println("x= " + k+"\t n(x)="+nk+"\t col="+(colno-1));
              if ((k<kmin) && (nk>0)) kmin=k;
              if ((k>kmin) && (k<kcont) && (nk==0)) kcont=k;
              if ((k>kmax) && (nk>0)) kmax=k;
              while (kv<k) {kv++; ddarr.add(0.0);}
              ddarr.add(nk);
              kv++;
          }//eofile
          if (infolevel>0) System.out.println("Finished " + filename+"\t kmin,kcont,kmax = \t"+kmin+"\t "+kcont+"\t "+kmax);
      }//eo try
       catch (TextReader.Error e) {
          // Some problem reading the data from the input file.
          if (infolevel>0) System.out.println("Input Error: " + e.getMessage());
          res=2;
       }
       finally {
          // Finish by closing the files,
          //     whatever else may have happened.
          data.close();
        }
       return res;
    }  // end of getDDData() method
    
  
    // ----------------------------------------------------------------------
        /** 
         *  Method of DistributionAnalysis
         * Reads in Distance Statistics data from file filename igoring initial lines
         * starting with cc and assuming pairs of doubles after that.
         * @param String filename name of file to read knk data
         * @param String used for comments, first character used only
         *
         */
        public int getDistStats(String filename, String cc) 
        {
          String fullfilename = dirname+filename;
          if (infolevel>0) System.out.println("Starting to get Distance Statistics from " + fullfilename);
          TextReader data;     // Character input stream for reading data.
          int res=0;  // error code.

          try {  // Create the input stream.
             data = new TextReader(new FileReader(fullfilename));
          }
          catch (FileNotFoundException e) {
             System.out.println("Can't find file "+filename);
             return 1;
          }

          try {
              if (infolevel>0) System.out.println(" File: "+filename);
              // Read the data from the input file.
              while (data.peek()==cc.charAt(0))  
              {   // skip initial lines with cc at start
                      String commentline = data.getln();
                      if (infolevel>0) System.out.println(commentline);
              }  

                  // first line is diameter data
                  diametermax=data.getInt();
                  diametermin=data.getInt();
                  diameterav=data.getDouble();
                  diametererr=data.getDouble();
                  diametersigma=data.getlnDouble();
                  //String comment = data.getln();
                  //System.out.println(comment);
                      
                  // second line is diameter data
                  totdistanceav = data.getDouble();
                  totdistanceerr=data.getDouble();
                  totdistancesigma=data.getlnDouble();
                  //comment = data.getln();
                  //System.out.println(comment);
                      
                  // third line is diameter data
                  onesdistanceav=data.getDouble();
                  onesdistanceerr=data.getDouble();
                  onesdistancesigma=data.getDouble();

//                  System.out.println("  Distance Stats in getdistStats");
//                  System.out.println("Diameter max, min, big max, big min"+" \t "+diametermax+" \t "+ diametermin+" \t "+diameterbigmax+" \t "+diameterbigmin);
//                  System.out.println("Diameter av, err, sigma"+" \t "+diameterav+" \t "+diametererr+" \t "+diametersigma);
//                  System.out.println("Tot Distance av, err, sigma"+" \t "+totdistanceav+" \t "+totdistanceerr+" \t "+totdistancesigma);
//                  System.out.println("One Sample distance av, err, sigma"+" \t "+onesdistanceav+" \t "+onesdistanceerr+" \t "+onesdistancesigma);

              if (infolevel>0) System.out.println("Finished " + filename);
          }//eo try
           catch (TextReader.Error e) {
              // Some problem reading the data from the input file.
              System.out.println("Input Error: " + e.getMessage());
              res=2;
           }
           finally {
              // Finish by closing the files,
              //     whatever else may have happened.
              data.close();
            }
           return res;
        }  // end of getDDData() method
    
 
  void setDiameterData(int Diammax, int Diammin, double Diamav, double Diamerr, double Diamsigma)
  {
                  diametermax=Diammax;
                  diametermin=Diammin;
                  diameterav=Diamav;
                  diametererr=Diamerr;
                  diametersigma=Diamsigma;
  }
  
  void setTotalDistanceData(double tdav, double tderr, double tdsigma){
//                      totdistanceav = data.getDouble();
//                  totdistanceerr=data.getDouble();
//                  totdistancesigma=data.getlnDouble();
//                  
                  };

  void setOneDistanceData(double odav, double oderr, double odsigma)
                  {
//                  onesdistanceav=data.getDouble();
//                  onesdistanceerr=data.getDouble();
//                  onesdistancesigma=data.getDouble();
                  }

        
// **************************************************************************    
    /**
     * Method of DistributionAnalysis
     * @param args the command line arguments
     */
    public int ParamParse(String[] ArgList){
                for (int i=0;i< ArgList.length ;i++){
                    if (infolevel>0) System.out.println("Parameter "+i+" is "+ArgList[i]);
                        if (ArgList[i].length() <2) {
                        System.out.println("\n*** Argument "+i+" is too short");
                        return 1;};
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** Argument "+i+" does not start with -");
                            return 2;};
                            switch (ArgList[i].charAt(1)) {
                                case 'f': {nameroot = ArgList[i].substring(2);
                                break;}
                                case 'd': {dirname = ArgList[i].substring(2);
                                break;}
//                                case 's': {subdirroot = ArgList[i].substring(2);
//                                break;}
                                case 'e': {inputddext = ArgList[i].substring(2);
                                break;}
                                case 'r': {lbratio = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'c': {choosecolumnnumber = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'n': {NormaliseDD= (ArgList[i].charAt(2)=='y') ; 
                                break;}
                                case 'o': {outputcontrol = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'x': {minDegreeRead = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                default:{
                                    System.out.println("\n*** Argument "+i+" not known, usage:");
                                    Usage();
                                    return 3;
                                }

                            }
                }
                File d = new File (dirname);
                if (!d.isDirectory()) {
                        System.out.println(dirname+" is not a directory");
                        return 1;};
//                int res = setSubDir();
            return 0;
            } // eo ParamParse

// **************************************************************
     /**
     * Processes all the files of types requested
     * @param filenameroot basis of name of file as string
     * @param double lbratio log binning ratio
     * @param cc comment characters put at the start of every line
     * @param String extdd extension for pure degree distribution output files 
     * @param String extddlb extension for log binned degree distribution output files
     * @param boolean IndLB is true if you want log bin file output for every input file
      */
     void processAll(String cc, String extdd, String extddlb, boolean IndLB)  {
         int lbn = (int) ((lbratio-1.0)*1000.0+0.5);
         getFileList(inputddext);
         if (filelist.length==0) {System.out.println(" *** No "+inputddext+" files found"); return;};
         String normLabel = (NormaliseDD)?"norm":"";
         for (int fn =0; fn<filelist.length; fn++){
            String fname=filelist[fn];
            String fnroot = filenamerootlist[fn];
            if (getDDData(fname,cc)!=0) continue;
            processOneDD(fnroot, cc, extdd, extddlb, IndLB);            
        }
 //           showTotDDData();
        calcLBtot();
//        showLBData();
        FileOutputLogBinDD(nameroot+"lb"+normLabel+lbn+".tot"+extddlb, cc);
        FileOutputTotDD(nameroot+normLabel+".tot"+extdd, cc);
}

     /**
     * Adds data from given Integer Array list containing degree distribution data.
     * @param intddarr IntegerArrayList of degree distribution 
     *@param fnroot root of file name 
     * @param cc comment characters put at the start of every line
     * @param String extdd extension for pure degree distribution output files 
     * @param String extddlb extension for log binned degree distribution output files
     * @param boolean IndLB is true if you want log bin file output for every input file
      */
     public void addOneDD(IntArrayList intddarr, String fnroot, String cc, String extdd, String extddlb, boolean IndLB)  {
         getDegreeDistribution(intddarr);
         processOneDD(fnroot, cc, extdd, extddlb, IndLB);
     }

     /**
     * Processes current degree distribution data in ddarr.
      *@param fnroot root of file name 
     * @param cc comment characters put at the start of every line
     * @param String extdd extension for pure degree distribution output files 
     * @param String extddlb extension for log binned degree distribution output files
     * @param boolean IndLB is true if you want log bin file output for every input file
      */
     void processOneDD(String fnroot, String cc, String extdd, String extddlb, boolean IndLB)  {
   int lbn = (int) ((lbratio-1.0)*1000.0+0.5);
         String normLabel = (NormaliseDD)?"norm":"";
            if (NormaliseDD) 
            {
             calcNumbers(ddarr);
             renormaliseDD(); 
            };
            if (IndLB) 
            { 
             calcLBind();
             FileOutputLogBinDD(fnroot+"lb"+normLabel+lbn+"."+extddlb, cc);
            }
            updateTotDD();
}

     
     
     
// **************************************************************
     /**
     * Processes all the files of types requested
     * @param filenameroot basis of name of file as string
     * @param double lbratio log binning ratio
     * @param cc comment characters put at the start of every line
     * @param String extdiststats extension for distance statistics files 
      */
     void processDistanceAll(String cc, String extdiststats)  {
         getFileList(inputdiststatsext);
         if (filelist.length==0) {System.out.println(" *** No "+inputdiststatsext+" files found"); return;};
         for (int fn =0; fn<filelist.length; fn++){
            String fname=filelist[fn];
            String fnroot = filenamerootlist[fn];
            if (getDistStats(fname,cc)!=0) continue;
            showDistStats();
            if (diameterbigmax>diametermax) diameterbigmax=diametermax;
            if (diameterbigmin<diametermin) diameterbigmin=diametermin;
            diameter.update(diameterav);
            totdistance.update(totdistanceav);
            onesdistance.update(onesdistanceav);
            //showDistStats();
        }
        
        diameter.print("diameter");
        if (infolevel>0) System.out.println("Extremes of diameter: \t"+ diameterbigmin + " \t "+diameterbigmin);
        totdistance.print("totdistance");
        onesdistance.print("onesdistance");
        
        FileOutputDistanceStats(nameroot+".tot"+extdiststats, cc);
        FileOutputDistanceStatsExcel(nameroot+".totxls"+extdiststats, cc);
}




// **************************************************************
     /**
     * Processes one file of types requested.
     * @param filenameroot basis of name of file as string
     * @param double lbratio log binning ratio
     * @param cc comment characters put at the start of every line
     * @param String extdd extension for pure degree distribution output files 
     * @param String extddlb extension for log binned degree distribution output files
      */
      
    public void processOne(String cc, String extddlb)  
     {
         int lbn = (int) ((lbratio-1.0)*1000.0+0.5);
         String normLabel = (NormaliseDD)?"norm":"";
            calcNumbers(ddarr);
            if (NormaliseDD) 
            {
             renormaliseDD(); 
            };
            calcLBind();
            FileOutputLogBinDD(nameroot+"lb"+normLabel+lbn+"."+extddlb, cc);
     }
     
     
     // ----------------------------------------------------------------------
    /**
     *  Method of DistributionAnalysis
    *
     */
    public void renormaliseDD() {
        if (Nvertices>0)
        for(int i=0; i<ddarr.size(); i++) {ddarr.set(i,ddarr.get(i)/((double)Nvertices) );};

    }
 
    
// ------------------------------------------------------------------------
/**
 * sets sub directory name
 **/
//        public int setSubDir(){
//        subdirname=dirname+subdirroot+"/";
//        File dir = new File(subdirname);
//        if (!dir.isDirectory())
//        {System.out.println("*** Error in parameters: sub directory "+subdirname+" not a directory");
//         return -1;
//        };
//        return 0;
//        }
        
// ----------------------------------------------------------------------        
/**
 * shows ddarr data
 **/
        public void showDDData(){
            System.out.println(" Individual degree distribution");
            System.out.println(" k  \t   n(k) ");
            for (int k=0; k< ddarr.size(); k++){
                double nk = ddarr.get(k);
                if (nk>0) System.out.println(k+"\t "+nk);}
            return;
        }
        
// ----------------------------------------------------------------------        
/**
 * shows ddarr data
 **/
        public void showDistStats(){
            System.out.println(" Individual Distance Stats");
            System.out.println("Diameter max, min, big max, big min"+" \t "+diametermax+" \t "+ diametermin+" \t "+diameterbigmax+" \t "+diameterbigmin);
            System.out.println("Diameter av, err, sigma"+" \t "+diameterav+" \t "+diametererr+" \t "+diametersigma);
            System.out.println("Tot Distance av, err, sigma"+" \t "+totdistanceav+" \t "+totdistanceerr+" \t "+totdistancesigma);
            System.out.println("One Sample distance av, err, sigma"+" \t "+onesdistanceav+" \t "+onesdistanceerr+" \t "+onesdistancesigma);

            return;
        }
        
// ----------------------------------------------------------------------        
/**
 * shows totddarr data
 **/
        public void showTotDDData(){
            System.out.println(" Total degree distribution");
            System.out.println(" k  \t   n(k)  \t   n^2(k)");
            for (int k=0; k< totddarr.size(); k++){
                System.out.println(k+"\t "+totddarr.get(k)+"\t "+totdd2arr.get(k));}
            return;
        }
        
// ----------------------------------------------------------------------
 /**
 * shows ddlbarr data
 **/
        public void showLBData(){
            System.out.println(" Individual log binned degree distribution");
            System.out.println(" b  \t k  \t   n(k)  \t +/-  \t bin size");
            for (int b=0; b< ddlbarr.size(); b++){
                System.out.println(b+"\t "+btokarr.get(b)+"\t "+ddlbarr.get(b)+"\t "+ddlberrarr.get(b)+"\t "+ddlbsizearr.get(b));}
            return;
        }
        
        
// ----------------------------------------------------------------------
    /**
     *  Method of DistributionAnalysis
     * Updates total degree distribution statistics
     * @param String filename name of file to read knk data
     *
     */
    public int updateTotDD() {
        double n2tot,ntot,n;
        // boolean ? true : false
        int kstop = (totddarr.size()<ddarr.size()) ? totddarr.size() : ddarr.size() ;
        int k;
        for (k=0; k<kstop; k++){
         n= ddarr.get(k);
         ntot= totddarr.get(k) + n;
         n2tot= totdd2arr.get(k) + n*n;
         totddarr.set(k,ntot);
         totdd2arr.set(k,n2tot);
              if ((k<totkmin) && (ntot>0)) totkmin=k;
              if ((k>totkmin) && (k<kcont) && (ntot==0)) totkcont=k;
              if ((k>totkmax) && (ntot>0)) totkmax=k;
//         System.out.println("Updating entry "+k+n+ntot+n2tot);
        }
        for (; k<ddarr.size(); k++){
         n= ddarr.get(k);
         totddarr.add(n);
         totdd2arr.add(n*n);
              if ((k<totkmin) && (n>0)) totkmin=k;
              if ((k>totkmin) && (k<kcont) && (n==0)) totkcont=k;
              if ((k>totkmax) && (n>0)) totkmax=k;
//         System.out.println("Adding entry "+k+n);
        }
        nruns++;
        return 0;
    }//eo updateTotDD

        
        
// ----------------------------------------------------------------------
            public void Usage(){
                DistributionAnalysis d = new DistributionAnalysis();
                System.out.println("...............................................................................");
                System.out.println("Usage: ");
                System.out.println("DistributionAnalysis <options> ");
                System.out.println(" where options are -<char><value> separated by space as follows ");
                System.out.println("  -f<nameroot>       Sets root of input and output files to be nameroot, default "+d.nameroot);
                System.out.println("  -d<dirname>        Sets directory name, default "+d.dirname);
//                System.out.println("  -s<subdirroot>     Sets subdirectory name, default "+d.subdirroot);
                System.out.println("  -e<inputddext>     Extension of input degree distribution files, default "+d.inputddext);
                System.out.println("  -c#                Choose column # of data, default "+d.choosecolumnnumber);
                System.out.println("  -r#                Ratio of upper to lower bin positions, default "+d.lbratio);
                System.out.println("  -n<y|n>            Normalise n(k) to p(k), yes or no, default "+(d.NormaliseDD?"yes":"no"));
                System.out.println("  -x#                Minumum degree analysed, default "+d.minDegreeRead);
                System.out.println(" -o<int> output modes , default "+d.outputcontrol);
                System.out.println("  o modes: (o& 1) ? Degree distribution analysis on : (off)");
                System.out.println("         : (o& 2) ? Distance statistics on : (off)");
                System.out.println("...............................................................................");

            } //eo usage

            // Print out parameters in param class
            public void print(){
                System.out.println("\n-------------------------------------------------------");
                System.out.println("                        Filename root:\t "+nameroot);
                System.out.println("                       Directory name:\t "+dirname);
//                System.out.println("                    Subdirectory root:\t "+subdirroot);
//                System.out.println("                    Subdirectory name:\t "+subdirname);
                System.out.println("                       Data in column:\t "+choosecolumnnumber);
                System.out.println("                        Log Bin ratio:\t "+lbratio);
                System.out.println("              Input DD file extension:\t "+inputddext);
                System.out.println("               Normalise n(k) to p(k):\t "+(NormaliseDD?"yes":"no"));
               System.out.println  ("-------------------------------------------------------");
            } //eo print

           public void listdir(){
            File dir = new File(dirname);
            if (dir.isDirectory()) {
                String dirlist[] = dir.list();
                for (int j=0; j<dirlist.length; j++){
                System.out.println(dirlist[j]);}
            };
           }


/**
 *  Method of DistributionAnalysis
 *  Filter to find only one type of file
 *  See Schildt p544
 */
    public class OnlyOneParamSet implements FilenameFilter{
          String ext;
          String header;

          // constructor;
          public OnlyOneParamSet (String header, String ext){
           this.ext=ext;
           this.header=header;
           }

          public boolean accept(File dir, String name){
           return ( (name.endsWith(ext)) & (name.startsWith(header)));
          }

    } // eo OnlyOneSet





public class OneParamStatistics {
    
    double minimum;
    double maximum;
    double average;
    double error;
    double sigma;
    double sigma2;
    double total;
    double totalsquares;
    int number;
    
    /** Creates a new instance of OneParamStatistics **/
    public OneParamStatistics() {
        minimum=9999999;
        maximum=0;
        average=0;
        error=0;
        sigma=0;
        sigma2=0;
        total=0;
        totalsquares=0;
        number=0;
    }

// ----------------------------------------------------------------------
    /**
     *  Method of OneParamStatistics
     * Updates statistics
     * @param 
     *
     */
     public void update(double newvalue)
     {
        number++;
        total+=newvalue;
        totalsquares+=newvalue*newvalue;
        if (newvalue<minimum) minimum=newvalue;
        if (newvalue>maximum) maximum=newvalue;
        average = total/((double) number);
        sigma2 = (totalsquares/((double) number)  -average*average);
        if (sigma2>0) sigma=Math.sqrt( (totalsquares/((double) number)  -average*average)  );
        else sigma=0.0;
        if (number>1) error = sigma/Math.sqrt((double)(number-1));
        else error =0;        
     }
    
// ----------------------------------------------------------------------
    /**
     *  Method of OneParamStatistics
     * Outputs statistics to screen
     * @param oneparamname name of the parameter as string 
     *
     */
     public void print(String oneparamname)
     {
        System.out.println(minimum+" <= "+oneparamname+" <= "+maximum);
        System.out.println("<"+oneparamname+"> = "+average+" +/- "+error);
        System.out.println("           sigma = "+sigma+", samples = "+number);
     }
    
    



// PS = new PrintStream(fout);
// ----------------------------------------------------------------------
    /**
     *  Method of OneParamStatistics
     * Outputs statistics to printstream
     * @param PS printstream for output 
     * @param oneparamname name of the parameter as string 
     *
     */
     public void print(PrintStream PS, String oneparamname)
     {
        PS.println(minimum+" <= "+oneparamname+" <= "+maximum);
        PS.println("<"+oneparamname+"> = "+average+" +/- "+error);
        PS.println("           sigma = "+sigma+", samples = "+number);
     }
    
// ----------------------------------------------------------------------
    /**
     *  Method of OneParamStatistics
     * Outputs statistics to printstream
     * @param PS printstream for output 
     * @param oneparamname name of the parameter as string 
     *
     */
     public void printExcel(PrintStream PS, String oneparamname)
     {
        PS.print(" \t"+oneparamname+" \t"+ average+" \t"+error + " \t"+sigma+" \t"+ minimum+" \t "+maximum+" \t"+number);
     }
    
    
} //eo class OneParamStatistics
            
        
}//eo  DistributionAnalysis
