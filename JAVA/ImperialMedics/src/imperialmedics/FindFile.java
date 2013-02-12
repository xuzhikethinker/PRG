package imperialmedics;

/*
 * FindFile.java
 *
 * Created on 01 November 2006, 10:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */




//import TimUtilities.TimMessage;
import java.io.File;
import java.io.FilenameFilter;


/**
 * Finds files in a given directory.
 * @author time
 */
public class FindFile {
    
//    int infolevel = 1;
    
    public String [] filelist;  // list of files found - full resolved name
    public String [] filenamerootlist; // list of header of files
//    TimMessage message = new TimMessage(2);
    public String extension="";
    
    /** Creates a new instance of FindFile */
    public FindFile() {
    }
 // ----------------------------------------------------------------------

    
/**
 *  Finds full names of all files of form <nameroot>*<ext> in directory dirname.
 * Sets up global lists filenamelist[] and filenamerootlist[], latter leaves off *<ext> part of name.
 *@param dirname name of directory
 *@param ext extension (ending) of files
 *  See Schildt p544
 */
    public void  getFileList(String dirname,  String ext) {
       boolean infoOn=false;
       getFileList(dirname,  ext, infoOn);
    }

/**
 *  Finds full names of all files of form <nameroot>*<ext> in directory dirname.
 * Sets up global lists filenamelist[] and filenamerootlist[], latter leaves off *<ext> part of name.
 *@param dirname name of directory
 *@param ext extension (ending) of files
 * @param infoOn true if want messages output
 *  See Schildt p544
 */
    public void  getFileList(String dirname, String ext, boolean infoOn) {
            // next part Schildt p544
       extension=ext;
       File dir = new File(dirname);
        if (!dir.isDirectory()) {
            System.err.println(dirname+" not a directory");
            return ;
        }
        if (infoOn) System.out.println("Looking at directory "+dirname);
        FilenameFilter only = new OnlyOneParamSet(ext);
        filelist = dir.list(only);
        if (infoOn) System.out.println("Found  "+filelist.length+" files with extension "+ext);
        filenamerootlist = new String[filelist.length];
        for (int i =0; i<filelist.length; i++) {
            filenamerootlist[i] =  getFileNameRoot(filelist[i], ext);
            if (infoOn) System.out.println(filelist[i]+"\t "+filenamerootlist[i]);
        }
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

/**
 *  Gives file name of file number f.
 *@param f number of file in list
 *@return name of file f
 */
    public String getFileName(int f){
        if ((f<0) || (f>=filelist.length) ) return "";
        return this.filelist[f];
    }

/**
 *  Gives file root name of file number f.
 *@param f number of file in list
 *@return name root of file f
 */
    public String getFileNameRoot(int f){
        if ((f<0) || (f>=filelist.length) ) return "";
        return getFileNameRoot(filelist[f],extension);
    }


/**
 *  Number of files found.
 *@return number of files found.name of file f
 */
    public int getNumberFiles(){
        return (filelist.length);
    }


    
// ****************************************************************************      
/**
 *  Method of DistributionAnalysis
 *  Filter to find only one type of file
 *  See Schildt p544
 */
    public class OnlyOneParamSet implements FilenameFilter{
          String ext;
          //String header;

          // constructor;
          public OnlyOneParamSet (String ext){
           this.ext=ext;
           //this.header=header;
           }

          public boolean accept(File dir, String name){
           return ( (name.endsWith(ext)) );
          }

    } // eo OnlyOneParamSet

            
}
