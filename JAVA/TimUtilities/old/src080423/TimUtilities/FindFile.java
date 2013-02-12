/*
 * FindFile.java
 *
 * Created on 01 November 2006, 10:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;


import java.io.*;
import JavaNotes.TextReader;
import TimUtilities.TimMessage;


/**
 * Finds files in a given directory.
 * @author time
 */
public class FindFile {
    
    int infolevel = 1;
    
    String [] filelist;  // list of files found - full resolved name
    String [] filenamerootlist; // list of header of files
    TimMessage message = new TimMessage(2);
    
    
    /** Creates a new instance of FindFile */
    public FindFile() {
    }
 // ----------------------------------------------------------------------

    
/**
 *  Finds full names of all files of form <nameroot>*<ext> in directory dirname.
 * Sets up global lists filenamelist[] and filenamerootlist[], latter leaves off *<ext> part of name.
 *@param dirname name of directory
 *@param nameroot start of file names
 *@param ext extension (ending) of files
 *  See Schildt p544
 */
    public void  getFileList(String dirname, String nameroot, String ext) {
            // next part Schildt p544
        File dir = new File(dirname);
        if (!dir.isDirectory()) {
            message.printERROR(dirname+" not a directory");
            return ;
        }
        message.println(1,"Looking at directory "+dirname); 
        FilenameFilter only = new OnlyOneParamSet(nameroot,ext);
        filelist = dir.list(only);
        message.println(1,"Found  "+filelist.length+" files with extension "+ext);
        for (int i =0; i<filelist.length; i++) {
            filenamerootlist[i] =  getFileNameRoot(filelist[i], ext);
            if (infolevel>0) System.out.println(filelist[i]+"\t "+filenamerootlist[i]);
        };
    }

// ----------------------------------------------------------------------
    
/**
 *  Cuts the extension off the file name.
 *@param filename input name
 *@param ext string to remove from end
 *@return null if ext is not at the end of filename otherwise it returns filename with ext removed from the end.
 */
    public String getFileNameRoot(String filename, String ext){
        int i = filename.lastIndexOf(ext);
        if (i<0) return null;
        return filename.substring(0,i);
    }
    

    
// ****************************************************************************      
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

    } // eo OnlyOneParamSet

            
}
