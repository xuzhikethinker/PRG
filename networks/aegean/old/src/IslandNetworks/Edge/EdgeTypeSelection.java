/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Edge;

import edu.uci.ics.jung.graph.decorators.UserDatumNumberEdgeValue;
 

/**
 *
 * @author time
 */
// ---------------------------------------------------------------

public class EdgeTypeSelection{


 public static final String[] name =  {IslandEdge.name[IslandEdge.valueINDEX],
                                       IslandEdge.name[IslandEdge.weightINDEX],
                                       IslandEdge.name[IslandEdge.potential1INDEX],
                                       IslandEdge.name[IslandEdge.geneCorrelationINDEX],
                                       IslandEdge.name[IslandEdge.separationINDEX],
                                       IslandEdge.name[IslandEdge.correlationINDEX]};
 public static final int numberTypes = name.length;
 public static int [] edgeVariableIndex = new int[name.length] ; // gives the index in IslandEdge of the variable corresponding to name[]
 private int variable=0;
 private UserDatumNumberEdgeValue[] jungUDNEV =  new UserDatumNumberEdgeValue[name.length] ;
 
public EdgeTypeSelection(){
 variable=0;
 setEdgeVariableIndex();
 //setUserDatumNumberEdgeValue();
}

/**
 * Deep copy.
 * @param vt edgeType to be deep copied.
 */
public EdgeTypeSelection(EdgeTypeSelection vt){
 setValue(vt.getValue());
 setEdgeVariableIndex();
 //setUserDatumNumberEdgeValue();
}

private void setEdgeVariableIndex(){
    for (int i=0; i<name.length;i++) edgeVariableIndex[i]=IslandEdge.getIndex(name[i]);
}

public void setUserDatumNumberEdgeValue(){
    for (int i=0; i<name.length;i++) jungUDNEV [i] = new UserDatumNumberEdgeValue(name[i]);
}

/**
 * Gives numerical variable of mode
 * @return numerical variable of mode
 */
public int getValue(){return variable;}

/**
 * Gives index for IslandEdge corresponding to the variable of mode.
 * @return index for IslandEdge for variable of mode or -1 if no correspondence
 */
 public int getValueIndex(){return edgeVariableIndex[variable];}

        
/**
 * Sets variable of mode to be that given if a valid number is given.
 * @param variable new variable of mode.
 * @return true if set, false if not a good variable in which case no changes made.
 */
public boolean setValue(int variable){
    if ((variable<0) || (variable>= name.length)) return false;
    this.variable=variable;
    return true;
}
    
/** Sets numerical code for edge types from string , -1 if not known.
     *@return numerical code used internally to represent the edge type
     */
    public int setEdgeType(String s)
    {
        for (int i=0; i<name.length; i++)
            if (name[i].startsWith(s.substring(0,1))) {variable=i; return variable;}
        variable=-1;
        return variable;
    }

   /* Returns string for edge type.
     *@param i the edge mode number.
     *@return short string describing the cluster type
    * @deprecated use <code>edgeType.name[t]</code>
     */
//     private String getString(int i){
//     if ((i<0) || (i>=name.length) ) return "UNKNOWN";
//     return name[i];
//    }

    /* Returns string for current edge type.
     *@return short string describing the current edge type
     */
     public String getCurrentTypeString(){
     return name[variable];
    }
     
     /**
      * Returns UserDatumNumberEdgeValue for use in Jung
      * @param v edge mode
      * @return a Jung UserDatumNumberEdgeValue type
      */
     public UserDatumNumberEdgeValue getUserDatumNumberEdgeValue (int v){
         if ((v>=0) && (v<name.length)) return this.jungUDNEV[v];
         else return null;
     }
     /**
      * Returns current UserDatumNumberEdgeValue for use in Jung
      * @return a Jung UserDatumNumberEdgeValue type
      */
     public UserDatumNumberEdgeValue getUserDatumNumberEdgeValue (){
         return jungUDNEV[variable];
     }

}//eo edgeType class

