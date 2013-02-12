/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;

//import IslandNetworks.Constants;
import JavaNotes.TextReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author time
 */
public class ReadPathFile {

    
    static private double DUNSET = -5.43216789e87;
    static private int IUNSET = -543216789;
 
    /**
     * Maximum Number of entries on a line.
     * <p>Current value {@value }
     */
    static final int nmax = 200;
    
    

    

    
     /**
      * Reads in list of paths from file filename.
      * <p>Each line is a single path.
      * Each path is given with source then target vertices followed by a list of segments.
      * Each segment is a list of two numbers, distance then the type index.
      * The type index may be either a number of a string representing the type of the segment.
      * Entries are separated by whitespace.
      * Both the vertexSet and pathList must be instantiated
      * @param fullFileName used to determine directory and name of file.
      * @param vertexSet if instantiated, vertices found are added to this set
      * @param pathList if instantiated, ordered list of paths found is added to this list.
      * @param sampleFrequency number of lines to skip, 1 or less and all are taken
      * @param infoOn true (false) if want information on screen on lines being read.
      * @return the number of paths found, negative indicates error.
*/
    static public int processPathFile(String fullFileName,
            Set vertexSet,
            ArrayList<Path> pathList, 
            int sampleFrequency,
            boolean infoOn)
    {
//        int minVertexLabel=9999999;
//        int maxVertexLabel=-minVertexLabel;

        int res=0;  // error code.
        int c=-1;  // column being considered
        
        boolean readSome=true; // store only every sampleFrequency lines
        if (sampleFrequency<2) readSome=false;
        
        boolean vertexSetOn =true;
        if (vertexSet==null) vertexSetOn=false;
        TextReader tr=openFile(fullFileName);
        if (tr==null) return -1;
        System.out.println("Starting to read path file using integer segment type from " + fullFileName);

        try {
            System.out.println(" File: "+fullFileName);
            // Read the data from the input file.
            int linenumber=0;
             // Use ArrayList<String>?
            String [] numbers = new String [nmax];
            int  source =IUNSET;
            int  target =IUNSET;
            double d;
            String t;
            Path path;
            while (tr.eof() == false)
                {  // Read until end-of-file.
                   linenumber++;
                   if (infoOn) System.out.println(linenumber+": ");
                   if (readSome && ((linenumber%sampleFrequency)!=1)) {tr.getln(); continue;}
                   int column=0;
                   // Read until end-of-line.
                   //if (forceLowerCase) while (tr.eoln() == false) numbers[column++] = tr.getWord().toLowerCase(); else                
                   while (tr.eoln() == false) numbers[column++] = tr.getWord();
                   if (infoOn) {
                    for (c=0; c<column; c++) System.out.print(", "+numbers[c]);
                    System.out.println();
                   }
                   if ((column<4) || (column%2>0) ) {
                         System.err.println("*** Wrong number of columns on line " + linenumber + " expected at least four, found " + column);
                         res = -10;
                         continue;
                   }
                   try { c=0;
                         source = Integer.parseInt(numbers[c++]);
                         target = Integer.parseInt(numbers[c++]);
                         path = new Path(source, target);
                         if (path.testSourceTarget()) {
                             if (vertexSet!=null) {
                                 vertexSet.add(source);
                                 vertexSet.add(target);}
                              }
                         else{
                                 System.err.println("*** Bad source or target line " + linenumber +", source "+source+", target  "+target);
                                 res = -20;
                             }
                         while (c<column) {
                             d= Double.parseDouble(numbers[c++]);
                             t= numbers[c++];                             
                             if (!path.addSegment(d,t)) {
                                 System.err.println("*** Bad segment on line " + linenumber + " column "+c+ " found distance type pair of "+d+"  "+t);
                                 res = -30;
                             }
                         }// eo while (c<column)
                         
                         if (pathList!=null) pathList.add(path);
                     
                    }// eo try
                    catch (RuntimeException e) {
                        System.err.println("*** PROBLEM on line " + linenumber + ", column "+c+", number of columns " + column + ". "+e.getMessage());
                        return -100;
                    }

               }//eofile

            if (res==0) res =pathList.size();
            else throw new RuntimeException("Error type "+res+", problems in reading path file"+fullFileName);
            System.out.println("Finished path list file input from " + fullFileName+" read "+linenumber+" lines, found "+pathList.size()+" paths and "+vertexSet.size()+" distinct vertices");
        }//eo try
        catch (TextReader.Error e) {
            // Some problem reading the data from the input file.
            System.err.println("*** Input Error: " + e.getMessage());
            res=-2;
        } finally {
            // Finish by closing the files,
            //     whatever else may have happened.
            tr.close();
        }

       return res;
    }

    
    static public void printInfo(String sep, Set<Integer> vertexSet, List<Path> pathList){ 
          System.out.println("--- Vertex List - found "+vertexSet.size()+" vertices");
          printVertexSet(System.out, sep,vertexSet);
          System.out.println("--- Path List - found "+pathList.size()+" paths");
          printPathList(System.out, sep, pathList);          
    }
          
        

        static public void printVertexSet(PrintStream PS, String sep, Set<Integer> vertexSet){
      int c=0;
      for (Integer v:vertexSet) System.out.print(v+sep+(((++c)%10)==0?"\n":""));
      System.out.println();
    }
    
    static public void printPathList(PrintStream PS, String sep, List<Path> pathList){
      for (Path p:pathList) p.printPretty(PS,sep);
      System.out.println();
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
