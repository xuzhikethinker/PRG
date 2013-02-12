/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package freeciv;

import JavaNotes.TextReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.fitting.PolynomialFitter;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
/**
 * Processes FreeCiv save files.
 * <p>Looks for distribution of city sizes.
 * @author time
 */
public class Main {

    final static int IUNSET = -97532468;
    final static String SUNSET="UNSET";
    final static String SEP="\t";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String rootName = "freeciv-T0810-Y02385-auto";
        int infoLevel=0;
        analyseSavFile(rootName,infoLevel);
    }


    static public int analyseSavFile(String rootName, int infoLevel){
        int res=0;  // error code.
        String fullFileName=rootName+".sav";
        TextReader data =openFile(fullFileName);
        if (data==null) return -1;
        final int minCitySize=50;
        System.out.println("Starting to read city data from " + fullFileName);
        System.out.println(" minimum city size "+minCitySize);
        String cityOutputFileName=SUNSET;
        PrintStream PS;
        FileOutputStream fout;
        ArrayList<String>  nationNameList= new ArrayList();
        ArrayList<Integer>  cityNumberList= new ArrayList();
        int totalNumberCities=0;
        int ncities=IUNSET;

        // read file and output city list for each large nation
        try {
            System.out.println(" File: "+fullFileName);
            int linenumber=0;
            String line =SUNSET;
            final String nationStartsWith="nation=";
            final int nationStartsWithLength=nationStartsWith.length();
            String nationName=SUNSET;

            final String ncitiesStartsWith="ncities=";
            final int ncitiesStartsWithLength=ncitiesStartsWith.length();
            String cityHeader=SUNSET;
            String cityLine=SUNSET;
            //if (headerOn) {data.getln();linenumber++;}
            while (data.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoLevel>2) System.out.println(linenumber+": ");
                   line=data.getln();

                   if (line.startsWith(nationStartsWith)) {
                       int c0=line.indexOf('"');
                       int c1=line.lastIndexOf('"');
                       nationName=line.substring(c0+1,c1);
                       nationNameList.add(nationName);
                   }

                   if (line.startsWith(ncitiesStartsWith)) {
                       ncities=Integer.parseInt(line.substring(ncitiesStartsWithLength));
                       totalNumberCities+=ncities;
                       cityNumberList.add(ncities);
                       if (ncities<minCitySize) System.out.println(ncities+" cities of "+nationName+" - not writing to file");
                       else {
                           try {
                                cityOutputFileName = rootName+"_cities_"+nationName+".csv";
                                if (infoLevel>-2) System.out.println("Started writing data on "+ncities+" cities of "+nationName+" to "+ cityOutputFileName);
                                fout = new FileOutputStream(cityOutputFileName);
                                PS = new PrintStream(fout);
                                cityHeader = data.getln().substring(3);
                                cityLine=SUNSET;
                                String [] lineArray = cityHeader.split(",");
                                int sizeIndex=IUNSET;
                                int foodStockIndex=IUNSET;
                                for (int n=0; n<lineArray.length; n++) {
                                    if (lineArray[n].equals("\"size\"")) sizeIndex=n;
                                    if (lineArray[n].equals("\"food_stock\"")) foodStockIndex=n;
                                }
                                PS.println(cityHeader+",food_used");
                                cityLine=data.getln();
                                int size=IUNSET;
                                int foodStock=IUNSET;
                                int foodUsed=IUNSET;
                                while (cityLine.charAt(0)!='}'){
                                    lineArray = cityLine.split(",");
                                    size=Integer.parseInt(lineArray[sizeIndex]);
                                    foodStock=Integer.parseInt(lineArray[foodStockIndex]);
                                    foodUsed=20+5*size*(1+size)+foodStock;
                                    PS.println(cityLine+","+foodUsed);
                                    cityLine=data.getln();
                                }

                                if (infoLevel>-2) System.out.println("Finished writing "+nationName+" city data file to "+ cityOutputFileName);
                                try{ fout.close (); } catch (IOException e) { System.err.println("*** Error closing file "+cityOutputFileName+", "+e);}

                            } catch (FileNotFoundException e) {
                                System.err.println("*** Error opening output file "+cityOutputFileName+", "+e.getMessage());
                            }

                       }//else
                   }// end of city processing
                   }//eofile
            System.out.println("Finished freciv saved data from " + fullFileName+" found "+nationNameList.size()+" nations with total "+totalNumberCities);
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the data from the input file.
            res=-2;
            throw new RuntimeException("*** Input Error: " + e.getMessage());

        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            data.close();
        }


        // Now output data on all cities
        try {
                cityOutputFileName = rootName+"_allcities.dat";
                if (infoLevel>-2) System.out.println("Started writing data on "+totalNumberCities+" cities of all "+nationNameList.size()+ " nations to "+ cityOutputFileName);
                fout = new FileOutputStream(cityOutputFileName);
                PS = new PrintStream(fout);
                for (int n=0; n<nationNameList.size(); n++)
                    PS.println(nationNameList.get(n)+SEP+cityNumberList.get(n));
                try{ fout.close (); }
                catch (IOException e) { System.err.println("*** Error closing file "+cityOutputFileName+", "+e);
                }

            } catch (FileNotFoundException e) {
                System.err.println("*** Error opening output file "+cityOutputFileName+", "+e.getMessage());
            }

        return res;

    }

 public void fit(){
     // http://commons.apache.org/math/userguide/optimization.html
     int degree=2;
     PolynomialFitter fitter = new PolynomialFitter(degree, new LevenbergMarquardtOptimizer());
     double w=1.0;
     fitter.addObservedPoint(w,-1.00,  2.021170021833143);
//fitter.addObservedPoint(-0.99   2.221135431136975);
//fitter.addObservedPoint(-0.98   2.09985277659314);
//fitter.addObservedPoint(-0.97   2.0211192647627025);
//// lots of lines ommitted
//fitter.addObservedPoint( 0.99, -2.4345814727089854);
     try {
            PolynomialFunction fitted = fitter.fit();
        } catch (OptimizationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

 }

// ***************************************************************************
        /**
         * Opens file for reading.
         * @param fullfilename full name of file including any directory path
         */
    static public TextReader openFile(String fullfilename)
    {
        TextReader newTR;
        System.out.println("Starting to read from " + fullfilename);
        try {  // Create the input stream.
            newTR = new TextReader(new FileReader(fullfilename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Can't find file "+fullfilename+", "+e.getMessage());
            //return null;
        }
        return newTR;
    }

}
