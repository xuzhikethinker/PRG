/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Vertex;

import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;
 

/**
 *
 * @author time
 */
// ---------------------------------------------------------------

public class VertexTypeSelection{


 public static final String[] name =  {"Weight", "Rank", "Influence", "Strength", "Nothing"};
 public static final int numberTypes = name.length;
 public static int [] siteValueIndex = new int[numberTypes] ; // gives the index in IslandSite of the value corresponding to name[]
 private int value=0;
 private UserDatumNumberVertexValue[] jungUDNVV =  new UserDatumNumberVertexValue[numberTypes] ;
 
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

private void setSiteValueIndex(){
    for (int i=0; i<numberTypes;i++) siteValueIndex[i]=IslandSite.getIndex(name[i]);
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
 * @return index for IslandSite for value of mode or -1 if no correspondence
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
        for (int i=0; i<name.length; i++)
            if (name[i].startsWith(s.substring(0,1))) {value=i; return value;}
        value=-1;
        return value;
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

