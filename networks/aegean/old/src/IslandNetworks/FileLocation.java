/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import java.io.File;
import java.io.PrintStream;
/**
 * Describes file name.
 * <br>Form is 
 * <br>rootDirectory + subDirectory + namebasic  + "_" + parameterName +_r<tt>sequenceNmber</tt> + ending + . + extension
 * <br> First "_" indicates start of paremeterName, last "_r" indicates start of sequence number.
 * <br>Last "." indicates start of extension
 * <br>PATH = PARENT+NAME
 * @author time
 */
public class FileLocation {

    private File rootDirectory; 
    private File fullDirectory; 
  //    private String rootDirectory; // stem for all directories e.g. c:/ariadne/output/
//    private String subDirectory; // subDirectory name e.g. aegean34sl_v1_3e-1.0m0.5j0.0k1.0l1.0b1.2s50.0MC/
    // example filename is aegean34sl_v1_3e-1.0m0.5j0.0k1.0l1.0b1.2s50.0MC_r1Value.vec
    private String basicRoot; // basic stem for all file names e.g. aegean34sl
    private String parameterName; // string describing the parameters e.g. v1_3e-1.0m0.5j0.0k1.0l1.0b1.2s50.0MC
    private String ending; // e.g. Value
    private String extension; // does not incule the '.' in name
    private static final int NOSEQNO=-24680;
    public int sequenceNumber=NOSEQNO; // number to be used for sequence, negative mean do include this
    private static final String SEQIND = "_r"; // indicates start of sequence number in filenames
    //public boolean autoNameRoot = true; // used to generate automatic addition to getNameRoot()

    /**
     * Inialise all values from given strings and given number.
     * @param dr stem for all directories
     * @param d full subDirectory name
     * @param br basic file name root
     * @param pn parameter name for all file names 
     * @param seqno sequence integer used to set sequence number 
     * @param ending string to add to ending of file name
     * @param ext extension
     */
    public FileLocation(String dr, String d, String br, String pn, int seqno, String ending, String ext){
        setAll(dr, d, br, pn, seqno, ending, ext);
    }
    
    /**
     * Inialise all values from given strings except for sequence number.
     * <br>No sequence number will be used in file names
     * @param dr stem for all directories
     * @param d full subDirectory name
     * @param br basic file name root
     * @param pn parameter name for all file names 
     * @param ending string to add to ending of file name
     * @param ext extension
     */
    public FileLocation(String dr, String d, String br, String pn, String ending, String ext){
        setAll(dr, d, br, pn, FileLocation.NOSEQNO, ending, ext);
    }
    
    /**
     * Inialise all values except sequence number from given strings.
     * <br>No sequence number will be used in file names
     * @param dr stem for all directories
    *@param d full subDirectory name
    *@param nr stem for all file names
     */
    public FileLocation(String dr, String d, String nr){
        setNamesLocations(dr, d, nr);
        sequenceNumber=NOSEQNO;
    } 

        /**
     * Inialise all values except sequence number from given strings.
     * <br>No sequence number will be used in file names
     * @param s string with filename
         */
    public FileLocation(String s){
        setFileLocationName(s);
    } 
        /**
     * Inialise all valuesfrom full file name including extension.
     * <br>No sequence number will be used in file names
     * @param f file of 
         */
    public FileLocation(File f){
        if (!f.isFile()) {System.out.println("*** ERROR "+f.getPath()+" is not a file in FileLocation"); return;}

        
        setFileLocationName(f.getPath());
    } 

    /**
     * Inialise all values except sequence number from given strings.
     * @param dr stem for all directories
    *@param d full subDirectory name
    *@param nr stem for all file names
     */
    public FileLocation(String dr, String d, String nr, int n){
        setNamesLocations(dr, d, nr);
        sequenceNumber=n;
    } 
    
    /**
     * Deep copy.
     * @param fl a file location variable
     */
    public FileLocation(FileLocation fl ){
        rootDirectory = fl.rootDirectory;
        fullDirectory = fl.fullDirectory; // full subDirectory name
        basicRoot = fl.basicRoot;
        parameterName=fl.parameterName;
        sequenceNumber = fl.sequenceNumber;
        ending=fl.ending;
        extension=fl.extension;
    } 
    
     /*
     * Gives full Directory location.
     * <br><tt>rootDirectory+subDirectory</tt>
     * @param ext extension
     * @return full file name with given extension but not with subDirectory
     */

    /**
     * Get full path of root Directory
     * @return root name for all directories
    */
    public String getRootDirectoryPath(){return rootDirectory.getPath(); }

        /**
         * Gets subdirectory name.
         *@return subdirectory name
          */
    public String getSubDirectoryName(){return fullDirectory.getName(); }
        /**
         * Gets full path of subdirectory.
         *@return subdirectory name
          */
    public String getSubDirectoryPath(){return fullDirectory.getPath(); }
    public String getFullLocation(){return fullDirectory.getPath();}
            
/*
     * Gives full name root used for files and directories.
     * <br><tt>basicRoot+"_"+ parameterName</tt>
     * @return basic name root of files and directories
     */
    public String getNameRoot()
    {
        return basicRoot+getParameterName();
    }
    
    /*
     * Gives ordinary file name but with no Directory.
     * <br><tt>getNameRoot()+"_r"+sequenceNumber</tt>
     * @return full file name with given extension but not with subDirectory
     */
    public String getFullFileRoot()
    {
        return getNameRoot()+sequenceString();
    }

        /*
     * Gives full file name including ending and extension but no directory
     * @return full file name including ending and extension but no directory
     */
//    public String getFileName(){return getNameRoot()+sequenceString()+ending+"."+extension;}
    public String getFullFileName() { return getNameRoot()+sequenceString()+ending+"."+extension;}
    
    /*
     * Gives ordinary file name but with no Directory.
     * <br><tt>getNameRoot()+"_r"+sequenceNumber+ending+"."+ext</tt>
     * @param ext extension
     * @return full file name with given extension but not with subDirectory
     */
    //public String getFullFileName(String ext) { extension=ext; return getFullFileName();  }

    /*
     * Gives ordinary file name but with no Directory.
     * <br><tt>getNameRoot()+"_r"+sequenceNumber+"r_"+ending+"."+ext</tt>
     * @param ending string to add to ending of file name
     * @param ext extension
     * @return full file name with given extension but not with subDirectory
     */
    public String getFullFileName(String ending, String ext) { this.ending=ending; extension=ext;return getFullFileName(); }
 
     /*
     * Gives full directory+ordinary file name root.
      * <p>Note there is no sequence number so this is the nameroot used for directories but set up as full file location and name.
     * <br><tt>rootDirectory+subDirectory+getNameRoot()</tt>
     * @return full file name root but no directory, ending or extension.
     */
    public String getFullLocationFileRoot()
    {
        return fullDirectory.getPath()+File.separator+getRootName();
    }
    
     /*
     * Gives full directory+ordinary file name root + sequence number string.
      * <p>This is used for the start of all file names.
     * <br><tt>rootDirectory+subDirectory+getNameRoot()+"_r"+sequenceNumber</tt>
     * @return full file name root but no directory, ending or extension.
     */
    public String getFullLocationFileRootSN()
    {
        return fullDirectory.getPath()+File.separator+getRootName()+sequenceString();
    }
    
     /*
     * Gives full directory+ordinary file name.
     * <br><tt>rootDirectory+subDirectory+getNameRoot()+"_r"+sequenceNumber+ending+"."+ext</tt>
     * @return full file name with directory, ending and extension.
     */
    public String getFullLocationFileName()
    {
        return fullDirectory.getPath()+File.separator+getFullFileName();
    }
     /*
     * Gives full directory+simple file name.
     * <br><tt>rootDirectory+subDirectory+basicRoot+ending+"."+ext</tt>
     * @return full file name with directory, ending and extension.
     */
    public String getFullLocationSimpleFileName()
    {
        return fullDirectory.getPath()+File.separator+basicRoot+ending+"."+extension;
    }
     /*
     * Gives full directory+simple file name.
     * <br><tt>rootDirectory+subDirectory+basicRoot+ending+"."+ext</tt>
     * @param ending string to add to ending of file name
     * @param ext extension
     * @return full file name with directory, ending and extension.
     */
    public String getFullLocationSimpleFileName(String ending, String ext)
    {  this.ending=ending; extension=ext; return getFullLocationSimpleFileName(); }
    
     /*
     * Gives full directory+ordinary file name as File.
     * <br><tt>rootDirectory+subDirectory+getNameRoot()+"_r"+sequenceNumber+ending+"."+ext</tt>
     * @return a file with full lfile name represented.
     */
    public File getFullLocationFile() { return  new File(this.getFullLocationFileName());}
    
  
    /*
     * Gives ordinary file name with full Directory.
     * <br><tt>rootDirectory+subDirectory+getNameRoot()+"_r"+sequenceNumber+"."+ext</tt>
     * @param ext extension
     * @return full file name with given extension but not with subDirectory
     */
    //public String getFullLocationFileName(String ext) { extension=ext; return getFullLocationFileName(); }
    
    /*
     * Gives ordinary file name with Directory.
     * <br><tt>rootDirectory+subDirectory+getNameRoot()+"_r"+sequenceNumber+ending+"."+ext</tt>
     * @param ending string to add to ending of file name
     * @param ext extension
     * @return full file name with given extension but not with subDirectory
     */
    public String getFullLocationFileName(String ending, String ext)
    {  this.ending=ending; extension=ext; return getFullLocationFileName(); }
    
    
    public String getBasicRoot(){return basicRoot;}
    public String getEnding(){return ending;} // e.g. Value
    public String getExtension(){return extension;}
    public String getRootName(){return basicRoot+getParameterName();}
    
         /*
     * Gives full directory+simple file name.
     * <br><tt>rootDirectory+basicRoot+ending+"."+ext</tt>
     * @param ending string to add to ending of file name
     * @param ext extension
     * @return root directory and basic file name with ending and extension added.
     */
    public String getRootLocationSimpleFileName(String ending, String ext)
    {  this.ending=ending; extension=ext; return getRootLocationSimpleFileName(); }
    
         /*
     * Gives full directory+simple file name.
     * <br><tt>rootDirectory+basicRoot+ending+"."+ext</tt>
     * @return root directory and basic file name with ending and extension added.
     */
    public String getRootLocationSimpleFileName()
    {  return     rootDirectory.getPath()+File.separator+basicRoot+ending+"."+extension; }
    

    /*
     * @return if parameterName is empty return null string otherwise appends "_" to create paremeter name string
     */
    public String getParameterName(){if (parameterName.length()>0) return "_"+parameterName; else return "";}
    
    
        /**
     * Makes all directories if needed.
         * @return true (false) if sucessful (failed)
     */
    public boolean mkDirs(){ return ( fullDirectory.mkdirs() );} 

    
   /**
     * Inialise all values except sequence number from given strings.
     * @param dr stem for all directories
    *@param d subsubDirectory name
    *@param nr name root stem for all file names
     */
    public void setNamesLocations(String dr, String d, String nr){
        setRootDirectory(dr);
        setSubDirectory(d); // full subDirectory name
        setNameRoot(nr);
    } 

   /**
     * Inialise all values except sequence number from given strings.
     * @param dr stem for all directories
    *@param d subsubDirectory name
    *@param br basic root for all file names
     *@param pn parameter name 
     */
    public void setNamesLocations(String dr, String d, String br, String pn){
        setRootDirectory(dr);
        setSubDirectory(d); // full subDirectory name
        basicRoot=br;
        parameterName=pn;
    } 


    
    /**
     * Inialise all values except sequence number from given strings.
     * @param dr stem for all directories (need not have slash at end, this will be added)
    *@param d subDirectory name (need not have slash at end, this will be added)
    *@param nr stem for all file names
     * @param seqno sequence number
     * @param ending ending
     * @param ext extension (no .)
     */
    public void setAll(String dr, String d, String nr, int seqno, String ending, String ext)
    {
        setNamesLocations(dr, d, nr);
                this.sequenceNumber=seqno;
        this.ending=ending;
        this.extension=ext;
    }

    /**
     * Inialise all values except sequence number from given strings.
     * @param dr stem for all directories (need not have slash at end, this will be added)
    *@param d subDirectory name (need not have slash at end, this will be added)
    *@param br basic root
     *@param pn parameter name
     * @param seqno sequence number
     * @param ending ending
     * @param ext extension (no .)
     */
    public void setAll(String dr, String d, String br, String pn, int seqno, String ending, String ext)
    {
        setNamesLocations(dr, d, br, pn);
                this.sequenceNumber=seqno;
        this.ending=ending;
        this.extension=ext;
    }
    
    
    /**
     * Set name of root Directory
     * @param dr root directory
    */
    public void setRootDirectory(String dr){
        rootDirectory = new File(dr);
    } 

        /**
         * Set subdirectory name.
         *@param d subDirectory name not its full path
          */
    public void setSubDirectory(String d){
        fullDirectory=new File(rootDirectory.getPath()+File.separator+d); // full subDirectory name
    } 

 
       
     public void setNextExistingSequenceNumber()
     {
         sequenceNumber++;
         if (!isFullFile()) sequenceNumber=0;
     }    

     /*
      * Finds next unused sequence number above given one in current directory.
      * @param sn start from this sequence number
      */
     public void setFirstFreeSequenceNumber(int sn)
     {
         sequenceNumber=(sn>0?sn:0);
         while (getFullLocationFile().isFile()) sequenceNumber++;
     }    

      /*
      * Finds next unused sequence number above current one in current directory.
      */
     public void setFirstFreeSequenceNumber() { 
         sequenceNumber=0; 
         while (getFullLocationFile().isFile()) sequenceNumber++; }    

    
        public void setNoSequence() { sequenceNumber=NOSEQNO;  }
        
        public String sequenceString()
        {
           if (sequenceNumber==NOSEQNO) return "";
           else return SEQIND+sequenceNumber;
        }
        
        /*
         * Sets filename from string.
         * <br>filenames are rootdir/subdir/nameroot_rnnn+ending.ext
         * <br>subsdirectory is between the last two / characters and rootdir is before that.
         * <br>nameroot is everthing before the _r and numbers immediately following give nnn the sequence number.
         * <br>After that comes the ending upto the last . symbol and finally the extension.
         *@param s string with full path and name of file
         */ 
        public void setFileLocationName(String s)
        {
            setAll("NOROOTDIR","NOSUBDIR","NONAMEROOT", NOSEQNO,"NOENDING","NOEXT");
            try{
                // what if s is a dircetory?
                File fullLocationName= new File(s);
                if (fullLocationName.isDirectory())
                {
                    rootDirectory= fullLocationName.getParentFile();
                    fullDirectory =  fullLocationName;
                    return;
                }
                    
                    String name=fullLocationName.getName();
                fullDirectory = fullLocationName.getParentFile();
                rootDirectory = fullDirectory.getParentFile();

                // now split up filename
                setFileName(name);
            }
            catch (RuntimeException e) {
            System.out.println(e);
            } // always run, leave in unset values 
            return ;
        }
 
         /*
         * Sets filename from File.
         * <br>filenames are rootdir/subdir/<basic>_<parameter>_rnnn<ending>.ext
         * <br>subsdirectory is between the last two / characters and rootdir is before that.
         * <br>nameroot is everthing before the _r and numbers immediately following give nnn the sequence number.
         * <br>After that comes the ending upto the last . symbol and finally the extension.
         *@param f File with full path and name of file
         */ 
        public void setFileLocationName(File fullLocationName)
        {
            setAll("NOROOTDIR","NOSUBDIR","NONAMEROOT", NOSEQNO,"NOENDING","NOEXT");
            try{
                if (fullLocationName.isDirectory())
                {
                    rootDirectory= fullLocationName.getParentFile();
                    fullDirectory =  fullLocationName;
                    return;
                }
                    
                String name=fullLocationName.getName();
                fullDirectory = fullLocationName.getParentFile();
                rootDirectory = fullDirectory.getParentFile();

                // now split up filename
                if (fullLocationName.isFile()) setFileName(name);
            }
            catch (RuntimeException e) {} // always run, leave in unset values 
            return ;
        }

        
        /*
         * Sets simple filename from string.
         * <br>filenames are rootdir/subdir/<basic><ending>.ext where ending starts with first _ and includes this.
         * <br>subsdirectory is between the last two / characters and rootdir is before that.
         * <br><basic> is everthing before the first _ and ending is everthing from that to the . of the extension.
         * <br>After that comes the ending upto the last . symbol and finally the extension.
         *@param f File with full path and name of file
         */ 
        public void setFileLocationSimpleName(File fullLocationName)
        {
            setAll("NOROOTDIR","NOSUBDIR","NONAMEROOT", NOSEQNO,"NOENDING","NOEXT");
            try{
                if (fullLocationName.isDirectory())
                {
                    rootDirectory= fullLocationName.getParentFile();
                    fullDirectory =  fullLocationName;
                    return;
                }
                    
                String name=fullLocationName.getName();
                fullDirectory = fullLocationName.getParentFile();
                rootDirectory = fullDirectory.getParentFile();

                // now split up filename
                if (fullLocationName.isFile()) setSimpleFileName(name);
            }
            catch (RuntimeException e) {} // always run, leave in unset values 
            return ;
        }

        
        
 
               /*
         * Sets filename from string.
         * <br>filenames are rootdir/subdir/nameroot_rnnn+ending.ext
         * <br>subsdirectory is between the last two / characters and rootdir is before that.
         * <br>nameroot is everthing before the _r and numbers immediately following give nnn the sequence number.
         * <br>After that comes the ending upto the last . symbol and finally the extension.
         *@param name string with full filename (not path)
         */ 
        public void setFileName(String name)
        {
            try{
                 int dot = name.lastIndexOf('.');
            if (dot<0) dot=name.length(); 
            else extension = name.substring(dot+1);
            int seq = name.indexOf(SEQIND);
            if (seq<0) {setNameRoot(name); return;}
            setNameRoot(name.substring(0,seq));
            // next part looks for integer
            int from=seq+SEQIND.length();
            int to=from;
            if ((name.charAt(to)=='-')) to++; // allow for sign
            while ((name.codePointAt(to)>47) && (name.codePointAt(to)<58) ) to++;
            //System.out.println(name.charAt(to)+"  "+name.codePointAt(to)); 
            sequenceNumber=Integer.parseInt(name.substring(from, to));
            ending = name.substring(to, dot);
        }
            catch (RuntimeException e) {} // always run, leave in unset values 
            return ;
        }
 
                /*
         * Splits string into basic root and parameter string and sets variables.
         * <br>simpleRoot = basicRoot + "_" + parameterName.  The "_" is the first in the string
         *@param name string with name root 
         */ 
        public void setNameRoot(String name)
        {
            int u = name.indexOf('_');
            if ((u<0) || ((u+1)>=name.length()))  {basicRoot=name; parameterName =""; }
            else {basicRoot=name.substring(0, u); parameterName= name.substring(u+1);}
            return;
        }

                /*
         * Splits string into basic root, ending and extension.
         * <br>nameRoot = basicRoot + ending . ext  The "_" is the first in the string.
                 * <br>Used for input files.
         *@param s string with file name  
         */ 
        public void setSimpleFileName(String s)
        {
            basicRoot=s; 
            ending = ""; 
            int u = s.indexOf('_');
            if ((u<0) || ((u+1)>=s.length())) return;
            basicRoot=s.substring(0, u);
            ending = s.substring(u+1);
        }

        /*
         * Sets parameter string of file names.
         * <br>Parameter string is the string after the first "_" in the file name.
         * @param s parameter string
         * @return true if set OK
         */
        public boolean setParameterName(String s)
        {
            parameterName=s;
            return true;
        }
        /*
         * Sets basic root of file names.
         * <br>Basic roots do not contain any "_" as these are used to indicate start the parameter string.
         * @param s ending
         * @return false if contains a '_r', true if set OK
         */
        public boolean setBasicRoot(String s)
        {
            if ((s.length()>0) && (s.indexOf('_')>-1)) return false; 
            basicRoot=s;
            return true;
        }
        /*
         * Sets ending string.
         * <br>Endings do not contain any "_r" as these are used to start the sequence string.
         * @param s ending
         * @return false if contains a '_r', true if set OK
         */
        public boolean setEnding(String s)
        {
            if ((s.length()>1) && (s.indexOf(SEQIND)>-1)) return false; 
            ending=s;
            return true;
        }
        
        /*
         * Sets extension string.
         * <br>Extensions do not contain any '.', these are edded as necessary.
         * @param s extension
         * @return false if contains a '.'
         */
        public boolean setExtension(String s)
        {
            if ((s.length()>0) && (s.indexOf('.')>-1)) return false; 
            extension=s;
            return true;
        }

        /*
         * Sets different parts of FileLocation.
         * <br>First character selects which variable, rest is used to set that variable.
         * <br> 'r' = root directory path
         * <br> 's' = sub directory name (this is added to the root directory path)
         * <br> 'b' = basic root of file names (no '_')
         * <br> 'p' = parameter string (will have '_' at start added when needed) 
         * <br> 'n' = sequence or run number (integer, '_r' added when needed)
         * <br> 't' = terminating or ending string (no '_r' allowed)
         * <br> 'e' = extension (no '.' allowed as this will be prepended as needed)
         * @param s extension
         * @return false if contains a '.'
         */
        public boolean set(String s)
        {
            try{
            switch (s.charAt(0))
            {
                case 'r': setRootDirectory(s.substring(1)); return true;
                case 's': setSubDirectory(s.substring(1)); return true;
                case 'b': return setBasicRoot(s.substring(1)); 
                case 'p': return setParameterName(s.substring(1)); 
                case 'n': sequenceNumber = Integer.parseInt(s.substring(1)) ; return true;
                case 't': return setEnding(s.substring(1)); 
                case 'e': return setExtension(s.substring(1));
            }
            }catch(RuntimeException e){ return false; }
            return true;
        }

        
        public String setString(char i)
        {
            String s="";
          switch(i)
                  {
                case 'r': s="'r' = root directory path"; break;
                case 's':  s="'s' = sub directory name (relative to root)"; break;
                case 'b':  s="'b' = basic root of file names (no '_')"; break;
                case 'p':  s="'p' = parameter string (will have '_' at start added when needed)"; break;
                case 'n':  s="'n' = sequence or run number (integer)"; break;
                case 't':  s="'t' = terminating or ending string (no '_r' allowed)"; break;
                case 'e':  s="'e' = extension (no '.' allowed)"; break;
              default: s=i+" is unknown"; break;
            }
          return s;
        }

            /** Shows fixed distance values on printstream.
         *@param PS a print stream for the output such as System.out
         * @param header string to go at start of line
         */     
     public void printSetStrings(PrintStream PS, String header) 
     {
         String p = "rsbpnte";
         for (int c =0; c<p.length(); c++) PS.println(header+setString(p.charAt(c)));
     }
        /*
         * Returns integer in string.
         * @param s string
         * @param from location at which string starts
         * @return integer value found in string, -9999999 means no integer found
         */
        private int getInt(String s, int from)
        {
            int res= -888888; 
            int to=from;
            if ((s.charAt(to)=='-')) {to++;} // allow for sign
            while ((s.codePointAt(to)>47) && (s.codePointAt(to)<58) ) to++;
            try{res=Integer.parseInt(s.substring(from, to));}
            catch (RuntimeException e) { return -9999999;}//  no integer found
            return res;
        }
        /*
         * Tests if the root subDirectory is a Directory.
         */
        public boolean isRootLocationDirectory()
       {
        return rootDirectory.isDirectory();
        }
        
        /*
         * Tests if the full subDirectory is a Directory.
         */
        public boolean isFullLocationDirectory()
       {
        return fullDirectory.isDirectory();
        }
        
        /*
         * Tests if the full file is a file
         */
        public boolean isFullFile() { return getFullLocationFile().isFile(); }
        
        
        
  


    
}
