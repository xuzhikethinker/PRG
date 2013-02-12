/*
 * GeneralMode.java
 *
 * Created on 16 November 2006, 10:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

import java.io.PrintStream;

/**
 * Stores single mode.
 * <br>i.e. these are radio boxes.
 * @author time
 */
public class GeneralMode {
    /**
     * Number of characters needed to specify a unique name
     */
    private int uniqueNameLength =3;
    /**
     * Index of mode
     */
    private int number=0;
    /**
     * Array of short names used to indicate modes
     */
    public String [] name; // = {"info","pajek"};
    /**
     * Array of long names used to describe modes
     */
    String [] longName;  //= {"general information","pajek files"};

    
//    final static int KMLIndex = getIndex("KML");
    
    
    /** Dummy constructor needed for extending
     */
    public GeneralMode() {
    }

    /** Creates a new instance of GeneralMode with all output off.
     */
    public GeneralMode(String [] newName, String [] newLongName) {
        setUp(newName, newLongName, 0);
    }
    /** Creates a new instance of GeneralMode.
     *@param newNumber new rewire mode number
     */
    public GeneralMode(String [] newName, String [] newLongName, int newNumber) {
         setUp(newName, newLongName, newNumber);
    }
    
    /** Creates a new instance of GeneralMode by using existing GeneralMode example.
     * <br>Relies on strings being copied automatically due to static type
     *@param old old GeneralMode
     */
    public GeneralMode(GeneralMode old) {
        name=old.name;
        longName=old.longName;
        uniqueNameLength=old.uniqueNameLength;
         set(old.number);
    }
    
    /**
     * Sets up necessary variables.
     * @param newName list of short names to identify different modes
     * @param newLongName list of long names used for description of modes
     * @param n new mode number
     */
    protected void setUp(String [] newName, String [] newLongName, int n)
    {
        name=newName;
        longName=newLongName;
        if (name.length != longName.length) 
            throw new IllegalArgumentException(" name[] and longName[] have different lengths"+name.length+" != "+longName.length);
        set(n);
    }
    
    /**
     * Sets up necessary variables.
     * @param newName list of short names to identify different modes
     * @param newLongName list of long names used for description of modes
     * @param mode mode name as short string
     */
    protected void setUp(String [] newName, String [] newLongName, String mode)
    {
        name=newName;
        longName=newLongName;
        if (name.length != longName.length) 
            throw new IllegalArgumentException("name and longName have different lengths"+name.length+" != "+longName.length);
        setFromName(mode);
    }
    
    
    /** Sets mode number from a double.
     *@param newNumber new mode number
     */
    public void set(double newNumber) {
                set(Math.round(newNumber));
    }
    /** Sets mode Number from an integer, testing to make sure it is valid.
     *@param newNumber new model number
     */
    public void set(int newNumber) {
        if (testModeNumber(newNumber)) number = newNumber;  
        else throw new IllegalArgumentException("Mode number must be between 0 and "+getNumberModes());
     }
    
    /** Sets mode number from a string representing of a double.
     *@param newNumber new mode number as string
     */
    public void setFromDouble(String newNumber) {
        set(Double.parseDouble(newNumber));
     }

    /** Sets mode number from a string
     *@param name short name of mode
     */
    public void setFromName(String name) {
        set(getIndex(name));
     }

    /** Sets mode number from a string.
     * @param name exact short name of mode
     * @return true if OK, false if not
     */
    public boolean setFromExactName(String name) {
        int n=getIndexFromExactName(name);
        if (n<0) return false;
        set(n);
        return true;
     }

    

    /**
     * Tests if valid mode number.
     * @param m mode number to test
     * @return true (false) if m is (in)valid mode number.
     * @deprecated use {@link #isValidNumber(int) }
     */
    public boolean testModeNumber(int m){return ( ((m<0) || (m>=name.length)) ?false:true);}

    /**
     * Tests if valid mode number.
     * @param m mode number to test
     * @return true (false) if m is (in)valid mode number.
     */
    public boolean isValidNumber(int m){return ( ((m<0) || (m>=name.length)) ?false:true);}


        /** Returns mode number.
         * @return number of mode.
         */
        public int getNumber()
        {
            return number;
        }
         /** Returns index from short name given.
         * <br>Compares the first <code>uniqueNameLength</code> length characters of input string
         * against the name array
         * @param input short name of variable being requested
         * @return variable number, -1 if none found.
         */
        public int getIndex(String input)
        {
            String s=input.substring(0,uniqueNameLength);
            for (int v=0; v<name.length;v++) 
              {
                  if (name[v].startsWith(s)) return v;
              }
            return -1;
        }

        /** Returns index from list of strings given.
         * <br>Compares the first <code>uniqueNameLength</code> length characters of input string
         * against the name array
         * @param input short name of variable being requested
         * @param list array of strings to be searched
         * @param uniqueNameLength number of characters to check
         * @return variable number, -1 if none found.
         */
        static public int getIndex(String input, String [] list, int uniqueNameLength)
        {
            String s=input.substring(0,uniqueNameLength);
            for (int v=0; v<list.length;v++) 
              {
                  if (list[v].startsWith(s)) return v;
              }
            return -1;
        }

        
       /** Returns index from exact short name given.
         * <br>Requires the input string to match exactly with one of the short names
         * @param input exact short name of variable being requested
         * @return variable number, -1 if none found.
         */
        public int getIndexFromExactName(String input)
        {
            for (int v=0; v<name.length;v++) 
              {
                  if (name[v].equalsIgnoreCase(input)) return v;
              }
            return -1;
        }

        /**
         * Sets number of characters to be used to define a unique short name.
         * @param l number of characters needed to define a unique short name
         */
        public void setUniqueNameLength(int l){uniqueNameLength=l;}
        
         /** Tests short name given against current mode.
         * <br>Compares the first <code>uniqueNameLength</code> length characters of input string
         * against the name array.  Case matters.
         * @param input short name being tested
         * @return true (false) if input and current short name equal.
         */
        public boolean isCurrentMode(String input)
        {
            return (name[number].startsWith(input.substring(0,uniqueNameLength)));

//            int m = getIndex(input);
//            if (number==m) return true;
//            return false;
        }


    
    // ----------------------------------------------------------------------
    /**
     * Prints description of Mode.
     * @param PS Printstream such as System.out
     * @param cc Comment character
     */
    public void print(PrintStream PS, String cc) 
    {  
        PS.println(cc+" Output mode is "+getNumber());
    };

     // ----------------------------------------------------------------------
    /**
     * Gives an abbreviated description string for the mode.
     * @return a string with the abbreviated description of mode.
     */
    @Override
    public String toString() 
    {
        return name[number];
    };
    /**
     * Gives an abbreviated description string for the mode.
     * @return a string with the abbreviated description of mode.
     */
    public String toLongString() 
    {
        return longName[number];
    };
    /**
     * Lists all the different modes against their mode number.
     * <br> Includes short name
     * @param PS Printstream such as System.out
     * @param cc string to start line
     * @param sep separation string between bit value and name
     */
    public void listAll(PrintStream PS, String cc)
    {
       for (int m=0; m<name.length; m++) PS.println(cc+longName[m]+" ("+name[m]+")");
    };
    /**
     * Lists all the different modes against their mode number, long and short names.
     * <br> Includes short name
     * @param PS Printstream such as System.out
     * @param sep1 string to start line before index
     * @param sep2 string between index and long name
     * @param sep3 string between long name and short name
     */
    public void listAll(PrintStream PS, String sep1, String sep2, String sep3)
    {
       for (int m=0; m<name.length; m++) PS.println(sep1+m+sep2+longName[m]+sep3+name[m]);
    };

    /**
     * Lists all the different modes against their mode number.
     * <br> Includes short name
     * @param PS Printstream such as System.out
     * @param cc string to start line
     * @param sep separation string between bit value and name
     */
    public void listAll(PrintStream PS, String cc, String sep)
    {
       for (int m=0; m<name.length; m++) PS.println(cc+longName[m]+" ("+name[m]+")");
    };

        /**
     * Gives list of short string descriptions and their numbers of all modes.
     * @param sep1 separation string between description and its number
     * @param sep2 separation string between different modes
     * @return list of short descriptions separated by sep string
     */
    public String listAllShort(String sep1, String sep2)
    {
        String s=name[0] + sep1+ "0";
        for (int m=1; m<name.length;m++) s=s+sep2+name[m]+sep1+m;
        return(s);
    }

    /**
     * Gives an abbreviated description string for the mode.
     * @param m mode index
     * @return a string with the abbreviated description of mode.
     */
    public String getString(int m) 
    {
        return name[m];
    };

    /**
     * Gives an full name of mode.
     * @return a string with the full description of mode.
     */
    public String getLongString(int m) 
    {
        return longName[m] ;
    };
    

    /**
     * Returns number of modes.
     * @return number of modes
     */
    public int getNumberModes(){return name.length;}

}

