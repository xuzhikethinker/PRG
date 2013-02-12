/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.FileUtilities;

/**
 *
 * @author time
 */
/**
 * File Filter for one type of ending.
 * <br>Also allows directories so users can navigate.
 * @author time
 */


import java.io.File;
//import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class GeneralEndingFilter extends FileFilter {
    FileUtilities fu = new FileUtilities();
    final String ending;
    final String description;
    /**
     * Constructs ending filter with generic description.
     * @param ext ending to filter on
     */
    public GeneralEndingFilter(String ext)
    {ending = ext; description =ending+" files";}

    /**
     * Constructs ending filter and its description.
     * @param ext ending to filter on
     * @param d description of filter
     */
        public GeneralEndingFilter(String ext,String d)
    {ending = ext;description = d;}

    //Accept all directories and files of specified ending.
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        if (f.getName().contains(ending)) return true;
        return false;
        }


    //The description of this filter
    public String getDescription() {
        return description;
    }

}
