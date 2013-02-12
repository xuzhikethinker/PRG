/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.FileUtilities;

import java.io.File;
/**
 *  Stores filename for a sequences of file names.
 * <br> This is a directory name
 * (adding a final forward slash if necessary), file name root followed by a runname
 * @author time
 */
public class FileNameSequence {
    private String directory;
    private String nameroot;
    private String runname;
            
    
    public FileNameSequence(String d, String n, String r)
    {
        setFileName(d,n,r);
    }

    public void setFileName(String d, String n, String r)
    {
        setDirectory(d);
        this.nameroot=n;
        this.runname=r;
    }

    public String getFullName()
    {
        return(directory+nameroot+runname);
    }

    public void setDirectory(String d)
    {
        directory=d;
        int ld = directory.length();
        if (!directory.substring(ld-1,ld).equals("/") ) directory= directory+"/";
    }

        public boolean testDirectory()
    {
        File dir = new File(directory);
        return (dir.isDirectory()); 
        }

}
