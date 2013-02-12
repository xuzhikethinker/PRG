/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;

//import IslandNetworks.Constants;

/**
 * Defines one segment of a path
 * @author time
 */
public class PathSegment {

    static private double DUNSET = -2.4689753e89;
    static private int IUNSET = -24689753;
    
    /**
     * Distance (or potential value) associated with a segment
     */
    private double distance = DUNSET;
    /**
     * Type of segment.
     * <p>Legitimate segment types are from from 0 to <tt>typeString.length</tt>
     * inclusive and correspond to types given by <tt>typeString[type]</tt>.
     */
    private int type = IUNSET;
    /**
     * Array of strings describing type of segment.
     * <p>Segment types are described by given by <tt>typeString[type]</tt>.
     */
    public static final String [] typeString= {"Sea", "Coastal", "Land"};
    
    /**
     * Number of types available.
     * <p>Index of types runs from 0 to this value.
     */
    public static final int numberTypes = typeString.length;
    
    static final String UNKNOWN= "UNKNOWN";
    
    public PathSegment(double d, String t){
        distance = d;
        setType(t);
    }
    
    
       /**
     * Gets distance of segment.
     * @return distance
     */
    public double getDistance(){return distance;}
           /**
     * Sets distance of segment.
     * <p>If negative then set to be negative and 
     * <tt>DUNSET</tt>
     * @param d distance value
     */
    public void setDistance(double d){
        if (d<0) distance=DUNSET;
        else distance=d;
}

    /**
     * Get Type
     * @return integer index of type
     */
    public int getType(){return type;}
    
    /**
     * Character representing current segment type
     * @return character representing current the segment type, character 0 (null) if not legal.
     */
    public char getTypeCharacter(){return getTypeCharacter(type);}
     /**
     * Character which can be used to represent the given segment type
     * @param t segment type index
     * @return character representing the given segment type index, character 0 (null) if not legal.
     */
    static public char getTypeCharacter(int t){
        if (testType(t)) return typeString[t].charAt(0);
        else return 0;
    }
    /**
     * String describing the current segment type
     * @return string describing the segment type, {@value routefinder.PathSegment#UNKNOWN} {@value routefinder.PathSegment#UNKNOWN}, if not known.
     */
    public String getTypeString(){
        return getTypeString(type);
    }
   /**
     * String describing the given segment type.
    * <p>Specified by <tt>typeString[]</tt>.
     * @param t segment type index
     * @return string describing the segment type, {@value routefinder.PathSegment#UNKNOWN}, if not known.
     */
    static public String getTypeString(int t){
        if (testType(t)) return typeString[t];
        else return UNKNOWN;
    }
    
    
    /**
     * Sets type of segment.
     * <p>Specified by integer index of <tt>typeString</tt>
     * or by first letter <tt>typeString</tt> values.
     * @param st string describing segment type.
     */
    public void setType(String st){
        int t=IUNSET;
        try { t=Integer.parseInt(st);  }
        catch (RuntimeException e){  t=testType(st);}
        type=t;
}
    
    /**
     * Tests to see segment is legal.
     * <p>distance must not be negative and type index must 
     * be a legal entry in <tt>typeString</tt> array.
     * @return true (false) if current segment is legal
     */
    public boolean testSegment(){
        return ( ( (distance>=0) && testType() )?true:false);
    }

    /**
     * Takes input string and tries use it to return a type index.
     * Compares first character against the type character.
     * @param s string representing segment type
     * @return index of type, negative if not legitimate index value.
     */
    static public int testType(String s){
        for (int t=0; t<typeString.length; t++){if (s.charAt(0)==getTypeCharacter(t) ) return t;}
        return IUNSET;
    }
    
    /**
     * Tests to see if input segment type index is legal
     * @param t segment type index
     * @return true (false) if current segment type is legal
     */
    static public boolean testType(int t){
        return (((t>=typeString.length) || (t<0))?false:true);
    }
    /**
     * Tests to see if current type index is legal
     * @return true (false) if current segment type is legal
     */
    public boolean testType(){return testType(type); }
    
    /**
     * String representation of segment given as numbers
     * @param sep separation string
     * @return String representation of segment given as numbers
     */
    public String toString(String sep){return distance+sep+type;}
            
    /**
     * String representation of segment given as distance then character for segment type
     * @param dp number of decimal places to use for distance IGNORED
     * @param sep separation string
     * @return String representation of segment given as numbers and a character
     */
    public String descriptiveString(int dp, String sep){return distance+sep+getTypeCharacter();}
            
}
