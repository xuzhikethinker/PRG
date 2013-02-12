/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.FileUtilities;

import java.io.File;

/**
 *
 * @author time
 */
public class FileUtilities {

    /*
     * Get the extension of a file.
     */  
    static public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /**
     * Takes a string and splits it into the last directory and the rest
     * <p>First entry in array returned is the substring from the
     * first character after the last slash to the end of the input string.
     * The second entry only exists if there was a slash and this is the first
     * part of the string upto and including the last slash.
     * This works for forward and backwards slashes.
     * @param d string to split up
     * @return string array input split at last slash.
     */
       static String [] slashSplit(String d){
        final String [] s;
        int slash = Math.max(d.lastIndexOf('/'), d.lastIndexOf('\\'));
        if (slash<0) {
            s= new String[1]; s[0]=d;
        }
        else {
            s= new String[2]; s[1]=d.substring(0, slash); s[0]=d.substring(slash+1);
        }
        return s;
    }

    
}
