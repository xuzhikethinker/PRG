/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Edge;

import IslandNetworks.islandNetwork;
 

/**
 *
 * @author time
 */
// ---------------------------------------------------------------

public class CorrelationTypeSelection{


 public static final String[] name =  {"Rank", "Gene*Potential","Influence"};
 public static final int numberTypes = name.length;
 final public static int rankINDEX = getIndex("Rank");
 final public static int genePotentialINDEX = getIndex("Gene*Potential");
 final public static int influenceINDEX = getIndex("Influence");

 //public static int [] edgeVariableIndex = new int[name.length] ; // gives the index in IslandEdge of the variable corresponding to name[]
 private int variable=0;
 //private UserDatumNumberEdgeValue[] jungUDNEV =  new UserDatumNumberEdgeValue[name.length] ;
 
public CorrelationTypeSelection(){
 //variable=0;
 //setEdgeVariableIndex();
 //setUserDatumNumberEdgeValue();
}

/**
 * Deep copy.
 * @param vt edgeType to be deep copied.
 */
public CorrelationTypeSelection(CorrelationTypeSelection vt){
 setType(vt.getType());
 //setEdgeVariableIndex();
 //setUserDatumNumberEdgeValue();
}

//private void setEdgeVariableIndex(){
//    for (int i=0; i<name.length;i++) edgeVariableIndex[i]=IslandEdge.getIndex(name[i]);
//}

//public void setUserDatumNumberEdgeValue(){
//    for (int i=0; i<name.length;i++) jungUDNEV [i] = new UserDatumNumberEdgeValue(name[i]);
//}

/**
 * Gives numerical variable of mode
 * @return numerical variable of mode
 */
public int getType(){return variable;}

   /** Gets numerical code for correlation types from string , -1 if not known.
     *@return numerical code used internally to represent the correlation type
     */
    static public int getIndex(String s)
    {
        for (int i=0; i<name.length; i++)
            if (name[i].startsWith(s.substring(0,1))) return i;
        return -1;
    }

///**
// * Gives index for IslandEdge corresponding to the variable of mode.
// * @return index for IslandEdge for variable of mode or -1 if no correspondence
// */
// public int getValueIndex(){return edgeVariableIndex[variable];}

        
/**
 * Sets variable of mode to be that given if a valid number is given.
 * @param variable new variable of mode.
 * @return true if set, false if not a good variable in which case no changes made.
 */
public boolean setType(int variable){
    if ((variable<0) || (variable>= name.length)) return false;
    this.variable=variable;
    return true;
}
    
/** Sets numerical code for edge types from string , -1 if not known.
     *@return numerical code used internally to represent the edge type
     */
    public int setType(String s)
    {
        variable=getIndex(s);
        return variable;
    }

    /**
     * Sets values of edge correlations according to current type.
     * @param in island network with current values to be used.
     * @return true (false) if correlation (not) set
     */
    public boolean setCorrelation(islandNetwork in){
        if (variable ==rankINDEX){
            in.edgeSet.setRank(in.siteSet);
            return true;}
        if (variable ==genePotentialINDEX){
            in.edgeSet.setPotentialGeneCorrelation();
            return true;}
        if (variable ==influenceINDEX){
            in.edgeSet.setInfluence(in.transferMatrix);return true;}
        return false;
        
    }
    /**
     * Rests values of edge correlations according to current type.
     * <p> Assumes type has not been changed since previous time 
     * so only assumes correlation parameter (if any) changed.
     * @param in island network with current values to be used.
     * @param p correlation parameter.
     * @return true (false) if correlation (not) set
     */
    public boolean recalculateCorrelation(islandNetwork in, double p){
        if (variable ==rankINDEX){
            return true;}
        if (variable ==genePotentialINDEX){
            return true;}
        if (variable ==influenceINDEX){
            in.transferMatrix.calcInfluenceMatrix(p);
            in.edgeSet.setInfluence(in.transferMatrix);return true;}
        return false;
        
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
     
//     /**
//      * Returns UserDatumNumberEdgeValue for use in Jung
//      * @param v edge mode
//      * @return a Jung UserDatumNumberEdgeValue type
//      */
//     public UserDatumNumberEdgeValue getUserDatumNumberEdgeValue (int v){
//         if ((v>=0) && (v<name.length)) return this.jungUDNEV[v];
//         else return null;
//     }
//     /**
//      * Returns current UserDatumNumberEdgeValue for use in Jung
//      * @return a Jung UserDatumNumberEdgeValue type
//      */
//     public UserDatumNumberEdgeValue getUserDatumNumberEdgeValue (){
//         return jungUDNEV[variable];
//     }

}//eo edgeType class

