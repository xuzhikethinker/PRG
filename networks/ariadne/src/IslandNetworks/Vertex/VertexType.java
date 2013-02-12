/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Vertex;

/**
 *@deprecated  VertexValueSlection replaces this?
 * @author time
 */
// ---------------------------------------------------------------

public class VertexType{


 public static final String[] name =  {"Weight", "Rank", "Influence", "Strength", "Nothing"};
 public static final int numberTypes = name.length;
 private int value=0;

public VertexType(){
 value=0;
}

/**
 * Deep copy.
 * @param vt vertexType to be deep copied.
 */
public VertexType(VertexType vt){
 setValue(vt.getValue());
}


/**
 * Gives numerical value of mode
 * @return numerical value of mode
 */public int getValue(){return value;}

        
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
            /* Sets numerical code for vertex types from string , -1 if not known.
     *@return numerical code used internally to represent the vertex type
     */
    public void setVertexType(String s)
    {
        for (int i=0; i<name.length; i++)
            if (name[i].startsWith(s.substring(0,1))) {value=i; return;}
        value=-1;
        return;
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

}//eo VertexType class

