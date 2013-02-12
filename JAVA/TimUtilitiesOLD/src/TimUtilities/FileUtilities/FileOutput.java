/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.FileUtilities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author time
 */
public class FileOutput {

   /** 
     * Prints a list of objects in a Collection.
     * @param PS PrintStream such as System.out
     * @param C any collection C
     */
    static public void printCollection(PrintStream PS, Collection C) 
    {
        for (Object e : C) {PS.println(e);}
    }
   
    
    
   /**
     * Output file containing a list of objects in a collection.
     * @param fullfilename full file name (including directory) for output
     * @param C any collection
     * @param mesagesOn true (false) if want messages on screen
     */
    static public void FileOutputCollection(String fullfilename, Collection C, boolean messagesOn)
    {
        PrintStream PS;
        FileOutputStream fout;
        if (messagesOn) {
            System.out.println("Writing list of objects from a collection to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            for (Object e : C) {PS.println(e);}
            if (messagesOn) {
                System.out.println("Finished writing list of "+C.size()+" objects from a collection to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;

    }

   /**
     * Output file containing a list of objects in a collection.
    * <p>Converts them using given map.  If no map given then outputs as given.
     * @param fullfilename full file name (including directory) for output
     * @param sep string used to separate items
     * @param C any collection
     * @param M if exists, objects in collection C used as keys for this map to find output object
     * @param numberPerLine number of objects to put on each line
     * @param mesagesOn true (false) if want messages on screen
     */
    static public void FileOutputCollection(String fullfilename, String sep, Collection C,
            Map M, int numberPerLine, boolean messagesOn)
    {
        PrintStream PS;
        FileOutputStream fout;
        if (messagesOn) {
            System.out.println("Writing list of objects, "+numberPerLine+" per line, from a collection to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            int c=numberPerLine;
            boolean noMap=false;
            if (M==null) noMap=true;
            for (Object e : C) {
                if (noMap) PS.print(e);
                else {
                    Object v = M.get(e);
                    PS.print(v);
                }
                if ((--c)==0) {PS.println(); c=numberPerLine;} else PS.print(sep);
            }
            if (messagesOn) {
                System.out.println("Finished writing list of "+C.size()+" objects, "+numberPerLine+" per line, from a collection to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;

    }

    /** 
     * Output file containing a list of keys then values in a Map
     * @param fullfilename full file name (including directory) for output
     * @param sep separation string
     * @param M any map
     * @param messagesOn true if want basic messages on
     */
    static public void FileOutputMap(String fullfilename, String sep, Map M, boolean messagesOn) 
    {
        PrintStream PS;
        FileOutputStream fout;
        if (messagesOn) {
            System.out.println("Writing list of keys and values of a map to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
             Set<Object> keys = M.keySet();
             for (Object k : keys) {PS.println(k+sep+M.get(k));}
             if (messagesOn) {
             System.out.println("Finished writing list of "+M.size()+" keys and values of a map to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;
 
    }
       /**
     * Prints out the map from words to stemmed words
     * @param PS PrintStream such as System.out
     * @param sep separation string
     * @param stemMap keys are words, values are the stemmed words
     */
    static public void printMapStringString(PrintStream PS, String sep, Map<String,String> map){
        Set<String> keys = map.keySet();
        for (String k : keys) {PS.println(k+sep+map.get(k));}
    }

}
