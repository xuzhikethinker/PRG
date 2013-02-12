/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Vertex;

import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;
import java.awt.Color;
 

/**
 *
 * @author time
 */
// ---------------------------------------------------------------

public class VertexTypeSelection{


 final static int smallC =90;
 final static int largeC = 210;
 final static int smallCprime =70;
 final static int largeCprime = 190;
 final static Color SIZECOLOUR = new Color(105,105,105); // Grey
 final static Color WEIGHTCOLOUR = new Color(largeC, smallC, smallC); //new Color(216, 134, 134); //Color.RED;
 final static Color RANKCOLOUR = new Color(smallC, smallC, largeC); //new Color(134, 134, 216); //Color.BLUE;
 final static Color STRENGTHCOLOUR = new Color(largeC, largeC, smallC);
 final static Color STRENGTHINCOLOUR = new Color(largeC, smallC, largeC);
 final static Color INFLUENCECOLOUR = new Color(smallC, largeC, smallC); //new Color(134, 216, 134); //Color.GREEN;
 final static Color BETWEENNESSCOLOUR = new Color(smallC, largeC, largeC);
 final static Color INFLUENCEPRIMECOLOUR = new Color(smallCprime, largeCprime, largeCprime);
 final static Color BETWEENNESSPRIMECOLOUR = new Color(smallCprime, largeCprime, largeCprime);
 final static Color NBETWEENNESSCOLOUR = new Color(largeCprime, smallCprime, largeCprime);
 final static Color NBETWEENNESSPRIMECOLOUR = new Color(largeCprime, largeCprime, smallCprime);
 final static Color NOTHINGCOLOUR = new Color(245, 245, 245); // off white

 public static final String[] name =  {"Size", "Weight", "PageRank",
                                         "Strength", "StrengthIn",
                                         "Influence", "Betweenness",
                                         "Influence\'", "Betweenness\'",
                                         "NBetweenness",  "NBetweenness\'",
                                         "Nothing"};

 public static final Color[] COLOUR =  {SIZECOLOUR, WEIGHTCOLOUR, RANKCOLOUR,
                                         STRENGTHCOLOUR, STRENGTHINCOLOUR,
                                         INFLUENCECOLOUR, BETWEENNESSCOLOUR,
                                         INFLUENCEPRIMECOLOUR, BETWEENNESSPRIMECOLOUR,
                                         NBETWEENNESSCOLOUR, NBETWEENNESSPRIMECOLOUR,
                                         NOTHINGCOLOUR};
 public static final int numberTypes = name.length;
 public static int [] siteValueIndex = new int[numberTypes] ; // gives the index in IslandSite of the value corresponding to name[]
 private int value=0;
 private UserDatumNumberVertexValue[] jungUDNVV =  new UserDatumNumberVertexValue[numberTypes] ;
 public final static int WEIGHTINDEX=VertexTypeSelection.getVertexType("Weight"); //
 public final static int PAGERANKINDEX=VertexTypeSelection.getVertexType("PageRank"); // weight
 public final static int INFLUENCEINDEX=VertexTypeSelection.getVertexType("Influence"); // weight
 public final static int NOTHINGINDEX=VertexTypeSelection.getVertexType("Nothing"); // weight

public VertexTypeSelection(){
 value=0;
 setSiteValueIndex();
 //setUserDatumNumberVertexValue();
}

/**
 * Deep copy.
 * @param vt vertexType to be deep copied.
 */
public VertexTypeSelection(VertexTypeSelection vt){
 setValue(vt.getValue());
 setSiteValueIndex();
 //setUserDatumNumberVertexValue();
}

private static void setSiteValueIndex(){
    int index=-1;
    for (int i=0; i<numberTypes;i++) {
        index=IslandSite.getIndex(name[i]);
        if (index<0) System.err.println("!!! VertexTypeSelection can not find index of "+name[i]);
        siteValueIndex[i] =index;
    }
}

public void setUserDatumNumberVertexValue(){
    for (int i=0; i<numberTypes;i++) jungUDNVV [i] = new UserDatumNumberVertexValue(name[i]);
}

/**
 * Gives numerical value of mode
 * @return numerical value of mode
 */
public int getValue(){return value;}

/**
 * Gives index for IslandSite corresponding to the value of mode.
 * @return index for IslandSite for value of mode or -1 if no correspondence (nothing)
 */
 public int getValueIndex(){return siteValueIndex[value];}

        
/**
 * Sets value of mode to be that given if a valid number is given.
 * @param value new value of mode.
 * @return true if set, false if not a good value in which case no changes made.
 */
public boolean setValue(int value){
    if ((value<0) || (value>= numberTypes)) return false;
    this.value=value;
    return true;
}
    
/** Sets numerical code for vertex types from string , -1 if not known.
     *@return numerical code used internally to represent the vertex type
     */
    public int setVertexType(String s)
    {
        value=getVertexType(s);
        return value;
    }

/** Gets numerical code for vertex types from string, -1 if not known.
 * <p>Names of vertex types are in {@see #name}.
 * @param s string which contains whole of name of vertex (ignoring case)
     *@return numerical code used internally to represent the vertex type
     */
    static public int getVertexType(String s)
    {
        for (int i=0; i<name.length; i++)
            if (name[i].equalsIgnoreCase(s)) {return i;}
        return -1;
    }

/** Gets numerical code for vertex types from string, -1 if not known.
 * <p>Names of vertex types are in {@see #name}.
 * @param s string which contains start or whole of name of vertex
     *@return numerical code used internally to represent the vertex type
     */
    static public int getVertexTypeFirst(String s)
    {
        for (int i=0; i<name.length; i++)
            if (name[i].startsWith(s.substring(0,1))) {return i;}
        return -1;
    }

   /* Returns string for vertex type.
     *@param i the vertex mode number.
     *@return short string describing the cluster type
    * @deprecated use <code>VertexType.name[t]</code>
     */
//     private String getString(int i){
//     if ((i<0) || (i>=name.length) ) return "UNKNOWN";
//     return name[i];
//    }

    /* Returns string for current vertex type.
     *@return short string describing the current vertex type
     */
     public String getCurrentTypeString(){
     return name[value];
    }
     
     /**
      * Returns UserDatumNumberVertexValue for use in Jung
      * @param v vertex mode
      * @return a Jung UserDatumNumberVertexValue type
      */
     public UserDatumNumberVertexValue getUserDatumNumberVertexValue (int v){
         if ((v>=0) && (v<name.length)) return this.jungUDNVV[v];
         else return null;
     }
     /**
      * Returns current UserDatumNumberVertexValue for use in Jung
      * @return a Jung UserDatumNumberVertexValue type
      */
     public UserDatumNumberVertexValue getUserDatumNumberVertexValue (){
         return jungUDNVV[value];
     }

}//eo VertexType class

