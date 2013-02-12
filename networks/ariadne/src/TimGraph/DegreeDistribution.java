/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph;

import DataAnalysis.LogBin;
import cern.colt.list.IntArrayList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Stores a degree distribution and its parameters.
 * @author time
 */
public class DegreeDistribution 
    {    
       String name="unspecified";
       IntArrayList ddarr;
       int minimum=9999999;
       int maximum=-1;
       int continuous=maximum;
       int totalvertices=0;
       int totaledges=0;
       double average=0;
       double secondmoment=0;
       
       public int infoLevel=0;
       
       public DegreeDistribution (String inputName)
       {
           ddarr = new IntArrayList();
           name =inputName;
       }
       
       public DegreeDistribution (String inputName, int initialSize)
       {
           ddarr = new IntArrayList(initialSize);
           name =inputName;
       }
       
     
    /**
     * Calculates degree distribution and other values.
     * @param vlist array of lists of neighbours
     * @param TNV total number of vertices in array
     */           
       void calcDegreeDistribution(IntArrayList [] vlist, int TNV )
    {
        maximum =-1 ; 
        ddarr.add(0); // initialise first element
        int k;
        for(int v=0; v<TNV; v++)
        {
          if (vlist[v]==null) {ddarr.set(0,ddarr.get(0)+1); break;}
          k = vlist[v].size();
          if (k<ddarr.size()) {ddarr.setQuick(k, ddarr.get(k)+1); continue;}
          while (ddarr.size()<k) ddarr.add(0);
          ddarr.add(1); // Before add size=k so last entry is for (k-1), so we just add a new k entry with value 1
        }
        calcValues();
       }
    /**
     * Calculates degree distribution and other values.
     * @param vlist array of lists of neighbours
     * @param firstVertex index of first vertex
     * @param lastVertexPlusOne index of last vertex plus one
     */           
       void calcDegreeDistribution(IntArrayList [] vlist, int firstVertex, int lastVertexPlusOne )
    {
        maximum =-1 ; 
        ddarr.add(0); // initialise first element
        int k;
        int fv=firstVertex;
        if (fv<0) {
            fv=0; 
            System.err.println("*** ERROR in calcDegreeDistribution firstVertex="+firstVertex +" negative, now starting from zero");
        }
        int lvp1 = lastVertexPlusOne;
        if (lastVertexPlusOne>vlist.length) {
            lastVertexPlusOne=vlist.length; 
            System.err.println("*** ERROR in calcDegreeDistribution lastVertexPlusOne="
                    +lastVertexPlusOne 
                    +" is too big, changed to be equal length of vlist = "
                    +vlist.length);
        }
        for(int v=firstVertex; v<lastVertexPlusOne; v++)
        {
          if (vlist[v]==null) {ddarr.set(0,ddarr.get(0)+1); break;}
          k = vlist[v].size();
          if (k<ddarr.size()) {ddarr.setQuick(k, ddarr.get(k)+1); continue;}
          while (ddarr.size()<k) ddarr.add(0);
          ddarr.add(1); // Before add size=k so last entry is for (k-1), so we just add a new k entry with value 1
        }
        calcValues();
       }

    /**
     * Calculates degree distribution and other values from source and target lists.
     * @param vlist   array of lists of neighbours (targets)
     * @param vsourcelist array of lists of neighbours (sources)
     * @param TNV     total number of vertices in array
     */           
       void calcDegreeDistribution(IntArrayList [] vlist, IntArrayList [] vsourcelist, int TNV )
    {
        calcDegreeDistribution(vlist, 0, TNV );
       }// eo calcDegreeDistribution
       
    /**
     * Calculates values except maximum from degree distribution.
     */    
       void calcValues()
       {
        totalvertices=0;
        totaledges=0;
        int nk=0;
        double nk2=0;
        continuous = maximum+1;
        minimum=0;
        while (ddarr.get(minimum)==0) minimum++;
        for (int k=minimum; k<ddarr.size(); k++) 
        {
           nk=ddarr.get(k);
           totalvertices+=nk;
           totaledges+=nk*k;
           nk2+=nk*k*k;
           if ((nk==0) && (k<continuous)) continuous = k; 
           if (nk>0) maximum=k;
        }
        average = ((double) totaledges)/totalvertices;
        secondmoment = nk2/totalvertices;
        return;
    }//eo calcvalues
       

       
       /**
     * Outputs the degree distribution information to a file.
     * @param filenamecomplete full name including directory and extension
     * @param cc comment characters put at the start of every line
     * @param sep separation string such as a tab  
     * @param normalise a boolean parameter to swicth on normalisation
     * @param headersOn true if want header row for data columns
     */
    void FileOutputDegreeDistribution(String filenamecomplete, String cc, String sep, boolean normalise, boolean headersOn)  
    {
        PrintStream PS;        
// next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            
            print(PS, cc, sep, normalise, headersOn);
            if (infoLevel>-2) System.out.println("Finished writing "+name+" degree distribution to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.err.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+ filenamecomplete);
            return;
        }
    }

  /**
     * Outputs the degree distribution in log bins to a file.
     * @param filenamecomplete full name including directory and extension
     * @param cc comment characters put at the start of every line
     * @param sep separation string such as a tab  
     * @param lbratio ratio of top and bottom positions of bins 
     * @param infoOn true if want row of general information
     * @param headersOn true if want header row for data columns
      */
    void FileOutputLogBinnedDegreeDistribution(String filenamecomplete, String cc, String sep, double lbratio,  boolean infoOn, boolean headersOn)  
    {
        if (maximum<0) this.calcValues();
        LogBin lb = new LogBin();
        lb.calcLogBinAny(ddarr, minimum, maximum, 1, lbratio);
        lb.FileOutputLogBinnedFreqData(filenamecomplete, cc, sep, infoOn, headersOn);
    }

    
  /**
     * Outputs the degree distribution information to a standard output.
     * @param cc comment characters put at the start of every line
     * @param sep separation string such as a tab  
     * @param normalise a boolean parameter to swicth on normalisation
     */
    void print(String cc, String sep, boolean normalise)  
    {
             print(System.out, cc, sep, normalise, true);            
    }

  /**
     * Outputs the degree distribution information to a print stream.
     * @param PS printstream for output such as System.out
     * @param cc comment characters put at the start of every line
     * @param sep separation string such as a tab  
     * @param normalise a boolean parameter to swicth on normalisation
     * @param headersOn true if want header row for data columns
     */
    void print(PrintStream PS, String cc, String sep, boolean normalise, boolean headersOn)  
    {
// next bit of code p327 Schildt and p550
            //Date date = new Date();
            double p=0;
            int n=0;
            if (totalvertices<1) return;
            if (headersOn){
                PS.print(cc+" k "+sep+(normalise?"p(k)"+sep+"Normalised ":"n(k)"+sep+"Unnormalised ")+name+" Degree Distribution  (not reduced = strength)");
                PS.println(sep+infoString(sep));
            }
            for (int k=0; k<ddarr.size(); k++)
            {
              if (normalise)  
              {
                  p = ddarr.get(k)/((double) totalvertices);
                  if (p>0) PS.println(k+sep+p);
              }
              else 
              {
                  n = ((int) (ddarr.get(k)+0.5) );
                  if (n>0) PS.println(k+sep+n);
              }
            }
            return;
        }// eo print
    
    /**
     * A string summarising the degree distrubution.
     * @param sep separation string
     * @return summary as string
     */
       public String infoString(String sep){
           String s="Min k"+sep+minimum;
           s=s+sep+"Continuum k1"+sep+continuous;
           s=s+sep+"Max k"+sep+maximum;
           s=s+sep+"<k>"+sep+average;
           s=s+sep+"<k^2>"+sep+secondmoment;
           s=s+sep+"N vertices"+sep+totalvertices;
           s=s+sep+"S Stubs"+sep+totaledges;
           return s;
       }
       
       /**
        * Output information about degree distribution.
        * @param PS print stream such as System.out
        * @param cc comment string
        * @param sep separation string such as a tab  
        * @param dec number of decimal points to retain     
        */
       public void outputInformation(PrintStream PS, String cc, String sep, int dec)
       {
           String fs = "%12."+dec+"6g";
           PS.println(cc+"Degree"+sep+"distribution"+sep+name);
           PS.println(cc+sep+"Total Vertices"+sep+totalvertices+sep+"Total Edges"+sep+totaledges);
           PS.println(cc+sep+"k_min"+sep+"k_cont"+sep+"k_max"+sep+"<k>"+sep+"<k^2>");
           PS.println(cc+sep+ minimum+sep+continuous+sep+maximum+sep+String.format(fs, average)+sep+String.format(fs,secondmoment) );
       }
     
 }
    


