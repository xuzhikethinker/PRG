/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities.FileUtilities;

import java.io.File;
/**
 *  Stores filename for a sequences of file names.
 * <p><tt>directoryroot</tt>+<tt>directoryend</tt>+<tt>nameroot</tt>+<tt>nameend</tt>
 * <br> This is a directoryroot name
 * (adding a final forward slash if necessary), file name root followed by a nameend
 * @author time
 */
public class FileNameSequence {
    private String directoryroot;
    private String directoryend;
    private String nameroot;
    private String nameend;
    /**
     * Directory (file) separator character to use.
     * <p>Should be forwards or backwards slash depending on system.
     * Value here is {@value FILESEPARATOR}
     */
    public final static char FILESEPARATOR=java.io.File.separatorChar;

            
    
    public FileNameSequence(FileNameSequence fns)
    {
        setFileName(fns);
    }
    /**
     * Sets file name and root
     * <p><tt>.</tt>+<tt>nameroot</tt>+<tt>nameend</tt>
     * <p>root (start) of directory name uses current directory and
     * <tt>directoryend</tt> is left blank
     * @param n root of file name
     * @param r end of file name
     */
    public FileNameSequence(String n, String r)
    {
        setFileName(System.getProperty("user.dir"),n,r);
    }
    /**
     * Sets file full name.
     * <p>This includes full directory path and file name
     * <p><tt>directoryroot</tt>+<tt>nameroot</tt>+<tt>nameend</tt>
     * <p><tt>directoryend</tt> is left blank
     * @param dr directory root
     * @param n root of file name
     * @param r end of file name
     */
public FileNameSequence(String d, String n, String r)
    {
        setFileName(d,n,r);
    }

    /**
     * Sets file full name.
     * <p>This includes full directory path and file name
     * <p><tt>directoryroot</tt>+<tt>directoryend</tt>+<tt>nameroot</tt>+<tt>nameend</tt>
     * @param dr directory root
     * @param de ending for directory
     * @param n root of file name
     * @param r end of file name
     */
    public FileNameSequence(String dr, String de, String n, String r)
    {
        setFileName(dr, de,n,r);
    }

    public void setFileName(FileNameSequence fns)
    {
     setFileName(fns.getDirectoryRoot(),fns.getDirectoryEnd(),fns.getNameRoot(),fns.getNameRoot());
    }

    /**
     * Sets full file name but leave directory end blank.
     * <p>The ending of the directory is blank.
     * @param dr root (start) of directory name
     * @param n root (start) of file name
     * @param r ending of file name
     */
    public void setFileName(String dr, String n, String r)
    {
        setDirectoryRoot(dr);
        directoryend="";
        this.nameroot=n;
        this.nameend=r;
    }
    /**
     * Sets file full name.
     * <p>This includes full directory path and file name
     * <p><tt>directoryroot</tt>+<tt>directoryend</tt>+<tt>nameroot</tt>+<tt>nameend</tt>
     * @param dr directory root
     * @param de ending for directory
     * @param n root of file name
     * @param r end of file name
     */
    public void setFileName(String dr, String de, String n, String r)
    {
        setDirectoryRoot(dr);
        setDirectoryEnd(de);
        this.nameroot=n;
        this.nameend=r;
    }

    /**
     * Sets root and ending of file name from combination of both.
     * <p>This can include full directory path and file name.
     * The last slash (either type) is used to split off the file name
     * from the directory part.  If this exists the root and ending of the
     * directory are set using
     * {@link TimUtilities.FileUtilities.FileNameSequence#setDirectory(java.lang.String)}.
     * Otherwise the full string, or that after the last slash, are compared with the
     * array of endings to determine how to split the string.  If none of the endings
     * match false is returned and no changes are made.  Otherwise file root
     * and ending (plus directories if found) are set.
     * @param n full file name, possibly with directories attached
     * @param ending array of strings of valid file name endings
     */
    public boolean setFullFileName(String n, String [] ending)
    {
      String [] name = FileUtilities.slashSplit(n);
      for (int e=0; e<ending.length; e++){
       if (name[0].endsWith(ending[e])){
           int l=name[0].length()-ending[e].length();
          nameroot= name[0].substring(0, l);
          nameend=ending[e];
          if (name.length>1) setDirectory(name[1]);
          return true;
          }
        }
       return false;
      }

    /**
     * Sets root of file name and optional directory structure.
     * <p>This can include full directory path and root of file name.
     * The last slash (either type) is used to split off the file name
     * from the directory part.  If this exists the root and ending of the
     * directory are set using
     * {@link TimUtilities.FileUtilities.FileNameSequence#setDirectory(java.lang.String)}.
     * Otherwise the full string, or that after the last slash, sets file root.
     * @param n full file name, possibly with directories attached
     */
    public boolean setFullFileNameNoEnding(String n)
    {
      String [] name = FileUtilities.slashSplit(n);
      nameroot= name[0];
      if (name.length>1) setDirectory(name[1]);
      return true;
      }


    public String getDirectoryFull()
    {
        return(directoryroot+directoryend);
    }
    public String getDirectoryRoot()
    {
        return(directoryroot);
    }
    public String getDirectoryEnd()
    {
        return(directoryend);
    }
    public String getNameRoot()
    {
        return(nameroot);
    }
    public String getNameEnd()
    {
        return(nameend);
    }

    public String getNameRootFullPath()
    {
        return(directoryroot+directoryend+nameroot);
    }
    public String getFullFileName()
    {
        return(directoryroot+directoryend+nameroot+nameend);
    }

    public String setDirectoryRoot(String dr)
    {
        directoryroot=makeDirectory(dr);
        return directoryroot;
    }
    public String setDirectoryEnd(String de)
    {
        directoryend=makeDirectory(de);
        return directoryroot;
    }
    
    /**
     * Tests character to see if it is some type of file separator.
     * <p>tests for unix and windows i.e. forward/backwards slash.
     * @param c character to test
     * @return true is it is a forwards or backwards slash
     */
    public static boolean isCharacterFileSeparator(char c){
        return (c=='/' || c=='\\'  ) ;
        
    }
    /**
     * Set directory root and ending from full directory name.
     * <p>Splits input at last slash (backwards or forwards).
     * Last part always used for directory ending.  First, if it exists
     * used to set root of directory. Will ignore trailing slash.
     * @param df full directory name
     * @return
     */
    public String setDirectory(String df)
    {
        int ld = df.length();
        if (ld<1) return df;
        if (isCharacterFileSeparator(df.charAt(ld-1))) ld--;
        String [] dname = FileUtilities.slashSplit(df.substring(0, ld));
        if (dname.length>0) return setDirectory(dname[1],dname[0]);
        return setDirectory("",dname[0]);
    }
    public String setDirectory(String dr, String de)
    {
        setDirectoryRoot(dr);
        setDirectoryEnd(de);
        return directoryroot+directoryend;
    }
    /**
     * Adds a forward slash to string if last character is not such a character
     * @param d input string
     * @return d plus a forward slash at end if not already present
     */
    public static String makeDirectory(String d)
    {
        int ld = d.length();
        if (ld<1) return d;
        if (!isCharacterFileSeparator(d.charAt(ld-1) )) d= d+FILESEPARATOR;
        return d;
    }
    /**
     * Adds a forward slash to string if last character is not such a character
     * @param d input string
     * @param create true if want to create these directories
     * @return true if mkdirs() was successful
     */
    public static boolean makeDirectory(String d, boolean create)
    {
        String dnew = makeDirectory(d);
        if (create) return true;
        File f = new File(dnew);
        return f.mkdirs();
    }
    /**
     * Creates directories.
     * @return true if mkdirs() was successful
     */
    public boolean makeDirectories()
    {
        File f = new File(getDirectoryFull());
        return f.mkdirs();
    }
    public void setNameRoot(String nr)
    {
        nameroot=nr;
    }
    public void appendToNameRoot(String nr)
    {
        nameroot=nameroot+nr;
    }
    public void setNameEnd(String ne)
    {
        nameend=ne;
    }

    /**
     * Tests directoryroot
     * @return true (false) if directoryroot is OK
     */
    public boolean testDirectoryRoot()
    {
        File dir = new File(directoryroot);
        return (dir.isDirectory()); 
    }
    /**
     * Tests directoryroot
     * @return true (false) if directoryroot is OK
     */
    public boolean testDirectoryFull()
    {
        File dir = new File(directoryroot+directoryend);
        return (dir.isDirectory()); 
    }
    /**
     * Tests existence of file with full file name.
     * @return true (false) if file exist
     */
    public boolean testFullFileName()
    {
        File f = new File(getFullFileName());
        return (f.exists()); 
    }

}
