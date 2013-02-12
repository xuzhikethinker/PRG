/*
 * MakeTimeSeries.java
 *
 * Created on 06 June 2006, 17:47
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package DataAnalysis;

//import DataAnalysis.LogBin;

//import cern.colt.list.IntArrayList;
import cern.colt.list.DoubleArrayList;
import java.io.*;
import java.util.Date;



//import DataAnalysis.DataRead;

/**
 * Analysis of discrete events occuring at different real times.
 * @author time
 */
public class EventTimeSeries {
    static String ETSVersion = "ETS060613";
    String SEP="\t";
    int infolevel=1;
    int outputmode=255; // bits set outputs
    
    int tFileCol=2;
        
    DoubleArrayList timeSeries ;
    StatisticalQuantity [] timeSeriesBinFreq;
    double timeSeriesFreqMin;
    double binScale = 1.1; // scale used to decide bin sizes
    boolean logBinOn = true;  // set logarithmic binning on
    int numberBins=0;
    double timeSeriesMinimum=1e99; // smallest time in the time series
    double timeSeriesMaximum=-timeSeriesMinimum;

    IntegerEvent avalancheLength ;
    IntegerEvent avalancheSize;
    double avBinScale=3600.0;  // bin scale used to produce avalanche time series
    double avLogBinScale=1.1;// bin scale for log bin results of avalanche time series
    double diffLogBinScale = 1.1; // scale used to decide bin sizes for differences
    
    String filename="testtimes.dat";
    String inputdirname="/PRG/networks/photogallery/input/";
    String outputdirname="/PRG/networks/photogallery/output/";
    String labelLine="gallery";
            
    
    /** Creates a new instance of EventTimeSeries */
    public EventTimeSeries() {
                

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("*** TimeSeries ***");
        
        DataRead dr = new DataRead();
        EventTimeSeries ts = new EventTimeSeries();
        if (ts.ParamParse(args)>0) return;
        
//        ts.filename="picture-creation-times.dat";
//        ts.inputdirname="/PRG/networks/photogallery/input/";
//        ts.outputdirname="/PRG/networks/photogallery/output/";
//        ts.labelLine="gallery";
        
        String filenameroot = ts.outputdirname+ts.removeExtension(ts.filename);
        
        String cc="#";
        
        // read data in
        int numberColumnsToBeRead=1;
        int [] columnsToBeRead = new int[numberColumnsToBeRead];
        int tDataCol=0;
        columnsToBeRead[tDataCol]=ts.tFileCol;
        dr.getData(ts.filename, ts.inputdirname, cc,  ts.labelLine, numberColumnsToBeRead, columnsToBeRead);
        System.out.println("!!! read "+dr.getTotalNumbersOfDataLines()+" lines" );

        // now get waiting times = time differences
        EventTimeSeries tsdiff = new EventTimeSeries();
        if (ts.ParamParse(args)>0) return;
        
        tsdiff.calctimeSeriesDifference(dr.columnData[tDataCol]);
        
        String fulltimeseriesfilename;
        
        if ((ts.outputmode & 1)>0)
        {
        fulltimeseriesfilename = filenameroot+"diffraw.dat";
        tsdiff.FileOutputTimeSeries(fulltimeseriesfilename,"#", false);
        }
        if ((ts.outputmode & 2)>0)
        {
        tsdiff.timeSeries.sort();
        fulltimeseriesfilename = filenameroot+"diffrank.dat";
        tsdiff.FileOutputTimeSeries(fulltimeseriesfilename,"#", false);
        }

        String fulloutputfilename="";
                
        if ((ts.outputmode & 4)>0)
        {
        tsdiff.logBinOn=true;
        tsdiff.binScale=ts.diffLogBinScale;
        tsdiff.calctimeSeriesFreq();
        fulloutputfilename = filenameroot+"DiffLB"+tsdiff.binScale+"intervalfreq.dat";
        tsdiff.FileOutputTimeSeriesFreq(fulloutputfilename," ");
        }
         
        // now get avalanche times from times (not time differences)
        ts.doAvalanche(filenameroot, "#",  dr.columnData[tDataCol]);
        
    }// eo main\
    
    
    
    /**
     * Finds first entry in time series between min and max values.
     * @param min
     *@param max
     *@param index where first value found or -1 if not found.
     */
    public int findFirstIndex(double min, double max) {
        double t;
        int indexFound =-1;
        for (int r=0; r<timeSeries.size(); r++) {
            t=timeSeries.get(r);
            if ((t>min) && (t<max)) {indexFound =r; break; }
        }
        return indexFound;
    }
    
    /**
     * Sets up the times in timeSeries from an input DoubleArrayList.
     * Sorted in ascending time order.  Maximum and minumum set too.
     * @param DoubleArrayList of time values in increasing order
     */
    public int copyTimeSeries(DoubleArrayList timeValues)
    {
        int nDataPoints= timeValues.size() ;
        timeSeries = new DoubleArrayList ();
        boolean sortOn =false;
        double t;
        // set up first time
        t=timeValues.get(0);
        timeSeries.add(t);
        timeSeriesMinimum=t;
        timeSeriesMaximum=t;
        double tlast = t;
        for (int n=1; n<nDataPoints; n++) 
        {
            t=timeValues.get(n);
            timeSeries.add(t);
            timeSeriesMinimum=Math.min(timeSeriesMinimum,t);
            timeSeriesMaximum=Math.max(timeSeriesMaximum,t);
            if (tlast>t) sortOn = true;
            tlast =t;
        }
        if (sortOn) timeSeries.sort();
        if (timeSeriesMinimum<0) return -1;
        return 0;
    }

    /**
     * Sets up the time differences in timeSeries from an input DoubleArrayList.
     * Sorted in order of increasing time differences.
     * @param DoubleArrayList of time values
     */
    public int calctimeSeriesDifference(DoubleArrayList timeValues)
    {
        int nDataPoints= timeValues.size() ;
        double t2,tdiff;
        double t1=timeValues.get(0);
        double tlast=-1e-99;
        boolean sortOn=false;
        timeSeries = new DoubleArrayList ();
        
        for (int n=1; n<nDataPoints; n++) 
        {
            t2=timeValues.get(n);
            tdiff=(t2-t1);
            timeSeries.add(tdiff);
            timeSeriesMinimum=Math.min(timeSeriesMinimum,tdiff);
            timeSeriesMaximum=Math.max(timeSeriesMaximum,tdiff);
            if ((tlast>tdiff) && (n>1) ) sortOn = true;
            tlast=tdiff;
            t1=t2;
        }
        if (sortOn) timeSeries.sort();
        if (timeSeriesMinimum<0) return -1;
        return 0;
    }

    /**
     * Puts whole time series into bins.
     * Uses global minimum and maximum time differences to do this.
     */
    public int calctimeSeriesFreq()
    {
        double tmin = timeSeriesMinimum;
        if ((logBinOn) && (tmin<=0)) tmin=1.0;
        return calctimeSeriesFreq(tmin, timeSeriesMaximum);
    }
    /**
     * Puts frequency of time series into  bins.
     * Sets up timeSeriesBinFreq[b] as an array of StatisticalQuantities.
     * Bin 0 is anything less than tmin input value, 
     * and the last bin is anything greater than tmax.
     * @param time for bottom edge of lowest bin
     * @param time for top edge of highest bin
     */
    public int calctimeSeriesFreq(double tmin, double tmax)
    {
        if ((logBinOn) && (binScale<=1)) return -1;
        if (tmax<tmin) return -2;
        if ((logBinOn) && (tmin<=0)) return -3;
        int nDataPoints= timeSeries.size() ;
        timeSeriesFreqMin =tmin;
        int nbins = calcNumberBins(tmin,tmax);
        timeSeriesBinFreq = new StatisticalQuantity [nbins];
        for (int b=0; b<nbins; b++) timeSeriesBinFreq[b] = new StatisticalQuantity();
        
        double t;
        int b;
        for (int n=0; n<nDataPoints; n++) 
        {
            t=timeSeries.get(n);
            b = calcBin(t);
            if (b>= nbins)
            {
                System.out.println("*** Error in calctimeSeriesFreq(double tmin, double tmax)"+b+" "+numberBins);
                break;
            }
            timeSeriesBinFreq[b].add(t);
        }
        
        return 0;
    }

    /**
     * Calculates Avalanche size and frequencies from binned time series.
     * Sets up timeSeriesBinFreq[b] as an array of StatisticalQuantities.
     * Bin 0 is anything less than tmin input value, 
     * and the last bin is anything greater than tmax.
     */
    public int calctimeSeriesAvalanches()
    {
        int nbins = timeSeriesBinFreq.length;
        
        avalancheLength = new IntegerEvent();
        avalancheSize = new IntegerEvent();
        
        int count=0;
        int size=0;
        int length =0;
        for (int b=0; b<nbins; b++) 
        {
            count = timeSeriesBinFreq[b].count;
            if (count>0) {size+=count; length++;}
            else if (length>0)
            {
                avalancheLength.add(length,  1);  
                avalancheSize.add( size,  1);  
                size=0;
                length=0;
            }           
            
        }
        
        return 0;
    }

// -----------------------------------------------------------------------    
    /**
     * Returns upper time edge of bin b.
     * Uses globals binScale, numberBins and logBinOn to calaculate bin for given time.
     * Bin 0 is anything less than tmin input value, 
     * and the last bin is anything greater than tmax.
     * @param bin number
     */
    public double calcTimeFromBin(int b)
    {
        if (b<0) return 1e-96;
        double t;
        if (logBinOn) t = Math.pow(binScale,b)*timeSeriesMinimum ;
        else t = binScale*b+timeSeriesMinimum;
        return t;
    }
    
    
// -----------------------------------------------------------------------    
    /**
     * Returns bin number of time.
     * Uses globals binScale, timeSeriesMiniumum and logBinOn to calaculate time for given bin.
     * Bin 0 is anything less than timeSeriesMiniumum input value, 
     * and the last bin is anything greater than tmax.
     * @param time to be binned
     */
    public int calcBin(double t)
    {
        int b;
        if (logBinOn) b= (int) ((Math.log(t/ timeSeriesMinimum)/Math.log(binScale)))+1;
        else b = (int) ( (t- timeSeriesMinimum)/binScale);
        if (b<1) b=0;
        if (b>=numberBins) b=numberBins-1;
        return b;
    }

// -----------------------------------------------------------------------    
    /**
     * Returns number of bins.
     * Uses globals binScale, numberBins and logBinOn to calaculate bin for given time.
     * Bin 0 is anything less than tmin input value, 
     * and the last bin is anything greater than tmax.
     * @param time for bottom edge of lowest bin
     * @param time for top edge of highest bin
     */
    public int calcNumberBins(double tmin, double tmax)
    {
        int n;
        if (logBinOn) n= (int) ((Math.log(tmax/tmin)/Math.log(binScale)))+1;
        else n = (int) ( (tmax-tmin)/binScale)+1;
        if (n<1) n=0;
        numberBins = n+2;
        return numberBins;
    }
    
     // ----------------------------------------------------------------------


// *************************************************************************
 
    
    /**
     * Calculates and outputs data as time series avalanches.
     * From a series of events in time, puts them into bins of size
     * bscale and then calculates and returns the size and length of avalanches
     * where an avalanche are the maximal sequences of non-empty time bins.
     * @param start of file name including full directory if needed.
     * @param comment character
     * @param list of times of events
     */
    public int doAvalanche(String filenameroot, String cc,  DoubleArrayList timeValues)
    {
        copyTimeSeries(timeValues);
        String fulltimeseriesfilename = filenameroot+"raw.dat";
        if ((outputmode & 8)>0)
        {
        FileOutputTimeSeries(fulltimeseriesfilename,cc, false);
        }
        
        logBinOn=false;        
        binScale=avBinScale;
        calctimeSeriesFreq();
        fulltimeseriesfilename = filenameroot+ avBinScale+"rawbinned.dat";
        if ((outputmode & 16)>0)
        {
        FileOutputTimeSeriesFreq(fulltimeseriesfilename,cc, true);
        }
        calctimeSeriesAvalanches();
        if ((outputmode & 32)>0)
        {
        fulltimeseriesfilename = filenameroot+avBinScale+"avalanchelengths.dat";
        avalancheLength.FileOutputIntegerCountData(fulltimeseriesfilename,"#", true);
        LogBin avsizeLB = new LogBin();
        //avsizeLB.lbratio = avLogBinScale;
        avsizeLB.calcLogBinAny(avalancheSize.eventFreq, 0, avalancheSize.eventFreq.size()-1, 1,avLogBinScale); 
        fulltimeseriesfilename = filenameroot + avBinScale+"avsizeLB.dat";
        avsizeLB.FileOutputLogBinFreqData(fulltimeseriesfilename,cc, SEP); 
        }
        
        if ((outputmode & 64)>0)
        {
        fulltimeseriesfilename = filenameroot+ avBinScale+"avalanchesizes.dat";
        avalancheSize.FileOutputIntegerCountData(fulltimeseriesfilename,"#", true);
        LogBin avlengthLB = new LogBin();
        //avlengthLB.lbratio = avLogBinScale;
        avlengthLB.calcLogBinAny(avalancheLength.eventFreq, 0, avalancheLength.eventFreq.size()-1, 1,avLogBinScale); 
        fulltimeseriesfilename = filenameroot + avBinScale+"avlengthLB.dat";
        avlengthLB.FileOutputLogBinFreqData(fulltimeseriesfilename,cc, SEP); 
        }
        
        
        return 0;
}
    
    
 // ----------------------------------------------------------------------

     /**
     * Outputs time series to a file.
     * @param full filename name of file as string including any directories.
     * @param cc comment characters put at the start of every line
      *@boolean true (false) if want data in given (reverse) order
     */
    void FileOutputTimeSeries(String fullfilename, String cc, boolean forward)
    {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing sorted time series to "+fullfilename);
            Date date = new Date();
//            PS.println(cc+" Sorted time series" + SEP + TSVersion+ SEP + " "+date);
            
            // Calc totals from binned data
            PS.println(cc+"rank r"+ SEP + "  t   "+SEP+cc+" Sorted time series" + SEP + " No. Events ="+ timeSeries.size() + SEP + ETSVersion+ SEP + " "+date);

            
            // Now output series
            int timeSeriesSize=timeSeries.size();
            int ruse=-1;
            for (int r =0; r<timeSeriesSize; r++) 
            {
                ruse = (forward ? r : timeSeriesSize-r-1 );
                PS.println(r+SEP+timeSeries.get(ruse));
            }
            try{
//                System.out.println("Trying to close "+filenamefull);
                fout.close();
                } catch (IOException e) { System.out.println("File Close Error "+fullfilename);}
            } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+fullfilename);
        } //eo catch
        
            if (infolevel>0) System.out.println("\n Finished Log Bin Output to "+fullfilename);
      } //eo FileOutputTimeSeries
   
 // ----------------------------------------------------------------------

     /**
     * Outputs binned time series to a file.
     * @param full filename name of file as string including any directories.
     * @param cc comment characters put at the start of every line
     */
    void FileOutputTimeSeriesFreq(String fullfilename, String cc)
    {
        FileOutputTimeSeriesFreq(fullfilename, cc, false);
    }

     /**
     * Outputs binned time series to a file.
     * @param full filename name of file as string including any directories.
     * @param cc comment characters put at the start of every line
     * @param true if you want to skip empty bins
     */
    void FileOutputTimeSeriesFreq(String fullfilename, String cc, boolean skipZeros)
    {
       // next bit of code p327 Schildt and p550
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            if (infolevel>0) System.out.println(cc+" writing log binned time series to "+fullfilename);
            Date date = new Date();
            
            // Calc totals from binned data
            PS.println(cc+"Bin No."+ SEP + " bin t_min "+ SEP + " bin t_max "+SEP+" Bin t " + SEP + " Normalised Freq " + SEP +"+/-"+SEP+timeSeriesBinFreq[0].labelString(SEP) + SEP+ " No. Events ="+ timeSeries.size()+ SEP+ " No. Bins ="+ timeSeriesBinFreq.length + SEP + ETSVersion+ SEP + " "+date);
//            PS.println(cc+"bin #"+ SEP + " bin t_min "+ SEP + " bin t_max "+SEP+" Bin t " +SEP+timeSeriesBinFreq[0].labelString(SEP) + SEP + TSVersion+ SEP + " "+date);

            
            // Now output series
            double tmin =0;  
            double tmax=timeSeriesFreqMin ;
            double tbin = (tmax+tmin)/2.0;
            int b =0;
            double binWidth = tmax-tmin;
            double binCountValue;
            double binCountError;
            binCountValue = timeSeriesBinFreq[b].count/ binWidth;
            if (binCountValue<1) binCountError =0;
            else binCountError = Math.sqrt(timeSeriesBinFreq[b].count)/ binWidth;
            PS.println(b+SEP+tmin+SEP+tmax+SEP+((tmax+tmin)/2.0) +SEP+ binCountValue + SEP + binCountError +SEP+timeSeriesBinFreq[b].toString(SEP));
//            PS.println(b+SEP+tmin+SEP+tmax+SEP+((tmax+tmin)/2.0)  +SEP+timeSeriesBinFreq[b].toString(SEP));
            for (b=1; b<timeSeriesBinFreq.length -1; b++) 
            {
                tmin=tmax;
                tmax = calcTimeFromBin(b);
                tbin = (tmax+tmin)/2.0;
                //if (calcBin(tbin) != b) {System.out.println("*** ERROR in FileOutputLBTimeSeries"); break;}
                binWidth = tmax-tmin;
                binCountValue = timeSeriesBinFreq[b].count/ binWidth;
                if (binCountValue ==0) continue;
                if (binCountValue<1) binCountError =0;
                else binCountError = Math.sqrt(timeSeriesBinFreq[b].count)/ binWidth;
                PS.println(b+SEP+tmin+SEP+tmax+SEP+((tmax+tmin)/2.0)+SEP+ binCountValue + SEP + binCountError +SEP+ timeSeriesBinFreq[b].toString(SEP));
//                PS.println(b+SEP+tmin+SEP+tmax+SEP+((tmax+tmin)/2.0) +SEP+ timeSeriesBinFreq[b].toString(SEP));
            }
            b=timeSeriesBinFreq.length-1;
            binWidth = tmax*(binScale-1.0);
            binCountValue = timeSeriesBinFreq[b].count/ binWidth;
                if (binCountValue<1) binCountError =0;
                else binCountError = Math.sqrt(timeSeriesBinFreq[b].count)/ binWidth;
                PS.println(b+SEP+tmax+SEP+" "+SEP+">"+tmax+SEP+binCountValue + SEP + binCountError +SEP+timeSeriesBinFreq[b].toString(SEP));
//            PS.println(b+SEP+tmax+SEP+" "+SEP+">"+tmax +SEP+timeSeriesBinFreq[b].toString(SEP));
            
            try{
//                System.out.println("Trying to close "+filenamefull);
                fout.close();
                } catch (IOException e) { System.out.println("File Close Error "+fullfilename);}
            } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+fullfilename);
        } //eo catch
        
            if (infolevel>0) System.out.println("\n Finished Log Bin Output to "+fullfilename);
      } //eo FileOutputTimeSeries


// ***********************************************************************    
    
    /**
     * Removes the extension from a string.
     *@param string to have extension removed (from . onwards)
     *@return input string with extension removed (if any)
     */
    public String removeExtension(String s)
    {
        int dotat = s.lastIndexOf('.');
        if (dotat>=0) s = s.substring(0,dotat);
        return s;
    }
        

// **************************************************************************    
    /**
     * Parses parameters.
     * @param args the command line arguments
     */
    public int ParamParse(String[] ArgList){
                for (int i=0;i< ArgList.length ;i++){
                    if (infolevel>0) System.out.println("Parameter "+i+" is "+ArgList[i]);
                        if (ArgList[i].length() <2) {
                        System.out.println("\n*** Argument "+i+" is too short");
                        return 1;}
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** Argument "+i+" does not start with -");
                            return 2;}
                            switch (ArgList[i].charAt(1)) {
                                 case 'b': {
                                    if (ArgList[i].charAt(2)=='a' ) avBinScale = Double.parseDouble(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='b' ) avLogBinScale = Double.parseDouble(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='d' ) diffLogBinScale = Double.parseDouble(ArgList[i].substring(3));
                                    break;}
                               case 'c': {tFileCol = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'd': {
                                    if (ArgList[i].charAt(2)=='i' ) inputdirname = ArgList[i].substring(3);
                                    if (ArgList[i].charAt(2)=='o' ) outputdirname = ArgList[i].substring(3);
                                    break;
                                }
                                case 'f': {filename = ArgList[i].substring(2);
                                break;}
                                default:{
                                    System.out.println("\n*** Argument "+i+" not known, usage:");
                                    Usage();
                                    return 3;
                                }
                                case 'o': {outputmode = Integer.parseInt(ArgList[i].substring(2));
                                break;}


                            }
                }
                // check directories are directories
                File d;
                d = new File (inputdirname);
                if (!d.isDirectory()) {
                        System.out.println(inputdirname+" for file input is not a directory");
                        return 1;}
                d = new File (outputdirname);
                if (!d.isDirectory()) {
                        System.out.println(outputdirname+" for file output is not a directory");
                        return 1;}
            return 0;
            } // eo ParamParse
    
   
    
    // ----------------------------------------------------------------------
            public void Usage(){
                EventTimeSeries d = new EventTimeSeries();
                System.out.println("...............................................................................");
                System.out.println("Usage: ");
                System.out.println("DistributionAnalysis <options> ");
                System.out.println(" where options are -<char><value> separated by space as follows ");
                System.out.println("  -ba#                Bin width for times pre avalanche calculations, default "+d.avBinScale);
                System.out.println("  -bb#                Bin width for log binning avalanche calculations, default "+d.avLogBinScale);
                System.out.println("  -bd#                Bin width for log binning time differences, default "+d.diffLogBinScale);
                System.out.println("  -c#                Choose column # of data, default "+d.tFileCol);
                System.out.println("  -f<nameroot>       Input file name (used to provide root for outfiles), default "+d.filename);
                System.out.println("  -di<inputdirname>  Sets name of input file directory , default "+d.inputdirname);
                System.out.println("  -do<inputdirname>  Sets name of input file directory , default "+d.outputdirname);
                System.out.println(" -o<int> output modes , default "+d.outputmode);
                System.out.println("  o modes:  (o& 1) ? Raw Time differences    on : (off), default"+(((d.outputmode&1)>0)?"on":"off"));
                System.out.println("            (o& 2) ? Ranked Time differences on : (off), default"+(((d.outputmode&2)>0)?"on":"off"));
                System.out.println("            (o& 4) ? Log Binned differences  on : (off), default"+(((d.outputmode&4)>0)?"on":"off"));
                System.out.println("            (o& 8) ? Raw Times               on : (off), default"+(((d.outputmode&8)>0)?"on":"off"));
                System.out.println("           (o& 16) ? Times in Bins           on : (off), default"+(((d.outputmode&16)>0)?"on":"off"));
                System.out.println("           (o& 32) ? Avalanche lengths       on : (off), default"+(((d.outputmode&32)>0)?"on":"off"));
                System.out.println("           (o& 64) ? Avalanche sizes         on : (off), default"+(((d.outputmode&64)>0)?"on":"off"));
                System.out.println("...............................................................................");

            } //eo usage

}
