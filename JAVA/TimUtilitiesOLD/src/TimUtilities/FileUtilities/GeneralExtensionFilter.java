/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.FileUtilities;

/**
 * File Filter for one type of extension.
 * <br>Also allows directories so users can navigate.
 * @author time
 */


import java.io.File;
//import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class GeneralExtensionFilter extends FileFilter {
    FileUtilities fu = new FileUtilities();
    final String extension;
    final String description;

    /**
     * Constructs extension filter with generic description.
     * @param ext extension to filter on
     */
    public GeneralExtensionFilter(String ext)
    {extension = ext; description =extension+" files";}

    /**
     * Constructs extension filter and its description.
     * @param ext extension to filter on
     * @param d description of filter
     */
        public GeneralExtensionFilter(String ext, String d)
    {extension = ext; description = d;}

    //Accept all directories and files of specified extension.
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        String ext = fu.getExtension(f);
        if (ext == null) return false;
        if (ext.equals(extension)) return true;
        return false;
        }


    //The description of this filter
    public String getDescription() {
        return description;
    }

}
