/*
 * IslandEdge.java
 *
 * Created on 27 July 2006, 16:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks.Edge;

import IslandNetworks.*;
import TimUtilities.NumbersToString;

/**
 * Defines characteristics of an edge in an Island Network.
 * @author time
 */
public class IslandEdge {
    final static String IEVERSION = "IE060728";
    final static double DUNSET = -97531e88;
    final static int IUNSET = -97531;
    final static double MAXDISTANCE = 99999;
    final static int UNIQUENAME = 13; // number of characters at start of name[] needed to specify unique entry
    final static String [] name ={"value", "weight", "distance", 
                                  "potential1", "geneCorrelation", 
                                   "separation",
                                   "influenceWeight", "betweenness",
                                   "influence\'Weight", "betweenness\'",
                                   "nbetweenness", "nbetweenness\'"};
    //"colour", "penalty", "barePotential1",
    final static int numberVariables = name.length;
    public double [] valueArray = new double [name.length];
    final public static int valueINDEX = getIndex("value");
    final public static int weightINDEX = getIndex("weight");
//    final public static int colourINDEX = getIndex("colour");
    final public static int distanceINDEX = getIndex("distance");
//    final public static int penaltyINDEX = getIndex("penalty");
    final public static int potential1INDEX = getIndex("potential1");
    final public static int geneCorrelationINDEX = getIndex("geneCorrelation");
    //final public static int correlationINDEX = getIndex("correlation");
    final public static int influenceWeightINDEX = getIndex("influenceWeight");
    final public static int betweennessINDEX = getIndex("betweenness");
    final public static int influenceprimeWeightINDEX = getIndex("influence\'Weight");
    final public static int betweennessprimeINDEX = getIndex("betweenness\'");
    final public static int nbetweennessINDEX = getIndex("nbetweenness");
    final public static int nbetweennessprimeINDEX = getIndex("nbetweenness\'");
//    final public static int rankINDEX = getIndex("rank");
    /**
     * Effective distance e.g.\ set using Dijkstra
     */
    final public static int separationINDEX = getIndex("separation");
    
//    /**
//     * Bare potential is the V function without lambda or weighting w_{ij}.
//     */
//    final public static int barePotential1INDEX = getIndex("barePotential1");

// These are needed to reconstruct the network    
    final static String [] parameterName ={"value", "distance", "geneCorrelation"};
//    final static int numberParameters = parameterName.length;
    

    
    /** Creates a new instance of IslandEdge */
    public IslandEdge() {
        for (int v=0; v<numberVariables;v++) valueArray[v]=DUNSET;
    }

    /** Creates a new instance of IslandEdge by deep copying existing edge*/
    public IslandEdge(IslandEdge e) {
        System.arraycopy(e.valueArray, 0, valueArray, 0, numberVariables);
    }
    
    /**
     * Tests to see if gene correlations has been set.
     * @return true (false) if gene correlations are being used
     */
    public boolean isGeneCorrelationSet(){return (valueArray[geneCorrelationINDEX]==DUNSET?false:true); }
    
    /**
     * Tests to see if variable has been set.
     * @param i index of variable
     * @return true (false) if variable is set (unset)
     */
    public boolean isSet(int i){return (valueArray[i]==DUNSET?false:true); }
    
    /**
     * returns value of variable of given type.
     * @param type name of variable
     * @return value of variable
     */
      public double getVariable(String type){
        return getVariable(getIndex(type));
      }
     /**
     * returns value of variable of given type.
     * @param index index of variable
     * @return value of variable
     */
      public double getVariable(int index){
        return valueArray[index];
      }
  
      /**
         *  Returns first index number of edge variable with given name.
         * <br>Compares the first {@code #UNIQUENAME} characters (or full length,
         * which ever is less) of input string.
         *@param input name of variable being requested
         * @return variable number, -1 if none found.
         */
        public static int getFirstIndex(String input)
        {
            String s=(input.length()<UNIQUENAME?input:input.substring(0,UNIQUENAME));
            for (int v=0; v<numberVariables;v++)
              {
                  if (name[v].startsWith(s)) return v;
              }
            return -1;
        }

      /**
         *  Returns index number of edge variable with given name.
         * <br>Input string must be exactly equal to one in
         * {@code #name} ignoring case.
         * @param input name of variable being requested
         * @return variable number, -1 if none found.
         */
        public static int getIndex(String input)
        {
           for (int v=0; v<numberVariables;v++)
              {
                  if (name[v].equalsIgnoreCase(input)) return v;
              }
            return -1;
        }


    public double getValue(){return valueArray[valueINDEX]; }
    public double getWeight(){return valueArray[weightINDEX]; }
    /**
     * Deprecated
     * @deprecated
     */
    public double getColour(){return DUNSET;} // valueArray[colourINDEX]; }
    public double getDistance(){return valueArray[distanceINDEX]; }
//    public double getPenalty(){ return valueArray[penaltyINDEX]; }
    /**
     * Returns 1 if geneCorrelcation is not set.
     */
    public double getGeneCorrelation(){return (isGeneCorrelationSet()?valueArray[geneCorrelationINDEX]:1); }
    public double getSeparation(){return valueArray[separationINDEX]; }
//    public double getRank(){return valueArray[rankINDEX]; }

    public void setValue(double d){ valueArray[valueINDEX] =d; }
    public void setWeight(double d){ valueArray[weightINDEX] =d; }
    /**
     * Deprecated
     * @param d
     * @deprecated
     */
    public void setColour(double d){ } //valueArray[colourINDEX] =d; }
    public void setDistance(double d){ valueArray[distanceINDEX] =d; }
//    public void setPenalty(double d){ valueArray[penaltyINDEX] =d; }
    public void setGeneCorrelation(double d){valueArray[geneCorrelationINDEX]=d; }
    public void setSeparation(double d){ valueArray[separationINDEX] =d; }
    public void setInfluenceWeight(double d){ valueArray[influenceWeightINDEX] =d; }
    public void setBetweenness(double d){ valueArray[betweennessINDEX] =d; }
    public void setInfluencePrimeWeight(double d){ valueArray[influenceprimeWeightINDEX] =d; }
    public void setBetweennessPrime(double d){ valueArray[betweennessprimeINDEX] =d; }
    public void setNewmanBetweenness(double d){ valueArray[nbetweennessINDEX] =d; }
    public void setNewmanBetweennessPrime(double d){ valueArray[nbetweennessprimeINDEX] =d; }
//    public void setRank(double d){ valueArray[rankINDEX] =d; }
    
    /**
     * Sets value of variable of given index.
     * @param index index of variable
     * @param value of variable
     */
      public void setVariable(int index, double value){
       valueArray[index]=value;   
      }
  

      /**
       * Checks to see if index is in range
       * @param index index of edge parameter
       * @return true if in range, false otherwise.
       */
      static public boolean checkIndex(int index){
          if ((index<0) || (index>=name.length)) return false; 
          else return true;}

   /**
     * Gives the potential between two site variables using effective distance.
     * @param H the Island Hamiltonian
     * @return returns the edge potential * lambda for model 1
     */
    public double getEdgePotentialSeparation1(IslandHamiltonian H) 
    {
      return(  H.edgePotential1(valueArray[separationINDEX],valueArray[geneCorrelationINDEX]));
    }

    /**
     * Gets the potential between two site variables.
     * <p>The potential times lambda <tt>\lambda V(d_{ij}/d_s)</tt>.
     * Must have been previously set.
     * @return returns the edge potential * lambda for model 1
     */
    public double getEdgePotential1() {return(  valueArray[potential1INDEX] );}

//    /**
//     * Gets the bare potential between two site variables.
//     * <p>The potential <tt>V(d_{ij}/d_s)</tt> with no lambda.
//     * <p> Must have been previously set.
//     * @return returns the edge potential * lambda for model 1
//     */
//    public double getEdgeBarePotential1() {return(  valueArray[barePotential1INDEX] );}

    /**
     * Sets the potential between two site variables.
     * <p>Includes the bare model 1 distance potential V_1, lambda and any gene weighting factors but no short distance effects
     * i.e. <tt>w_{ij} \lambda V_1(d_{ij}/d_s)</tt> 
     * @param H the Island Hamiltonian
     * @return returns the edge potential * lambda * weighting for model 1
     */
    public double setEdgePotential1(IslandHamiltonian H) 
    {
      if (isGeneCorrelationSet()) valueArray[potential1INDEX] = H.edgePotential1(valueArray[distanceINDEX],valueArray[geneCorrelationINDEX]);
      else valueArray[potential1INDEX] = H.edgePotential1(valueArray[distanceINDEX],1);
      //valueArray[potential1INDEX] = H.edgeBarePotential1(valueArray[distanceINDEX]);
      return(  valueArray[potential1INDEX] );
    }
    
         /**
           *  Returns string name of one edge variable.
           *@param index edge variable number to return.
           *@return string with name of data variable number value.
           */
        public static String dataName(int index)
        {
            if ((index<0) || (index>=name.length)) return "value unknown";
            return name[index];
        }
         /**
           *  Returns string name of one edge parameter.
           * <p> Parameters are variables needed for complete reconstruction of results.
           *@param index edge parameter variable number to return.
           *@return string with name of data variable number value.
           */
        public static String parameterName(int index)
        {
            if ((index<0) || (index>=parameterName.length)) return "value unknown";
            return parameterName[index];
        }

           /**
           *  Returns string name of one edge variable.
           *@param index edge variable number to return.
           *@return string with information on data variable.
           */
        public static String dataNameInformation(int index)
        {
            if ((index<0) || (index>=name.length)) return "value unknown";
            String s=name[index]+": ";
            if (index==valueINDEX) return s+"edge value derived from modelling"; 
            if (index==weightINDEX) return s+"edge weight (edge * source site) derived from modelling";
//            if (index==colourINDEX) return s+"used to colour edges"; 
            if (index==distanceINDEX) return s+"geographical distance, may be in units of time"; 
//            if (index==penaltyINDEX) return s+"may be used to add a penalty to geographical distance"; 
            if (index==potential1INDEX) return s+"geographical distance potential term used in model one, includes lambda etc."; 
            if (index==geneCorrelationINDEX) return s+"correlation between genes of source/target sites"; 
//            if (index==barePotential1INDEX) return s+"geographical distance potential used in model one without any prefactors"; 
            if (index==separationINDEX) return s+"effective distance set for instance by Dijstra routines"; 
            if (index==influenceWeightINDEX) return s+"influence weight I_{ij}S_jv_j";
            if (index==betweennessINDEX) return s+"betweenness";
            if (index==influenceprimeWeightINDEX) return s+"influence' weight I'_{ij}S_jv_j";
            if (index==betweennessprimeINDEX) return s+"betweenness'";
            if (index==nbetweennessINDEX) return s+"nbetweenness";
            if (index==nbetweennessprimeINDEX) return s+"nbetweenness'";
            return s+" valid but no information given";
        }
      
    
    
    /**
     * Returns edge as a single string.
     * @param sep separation character
     * @param dec integer number of decimal places to display
     * @return returns a string of values separated by separation character
    */
    public String toString(String sep, int dec) 
    {
        NumbersToString ns = new NumbersToString(dec);
       String s="";
       for (int i=0; i<name.length;i++) s= s+ns.toString(getVariable(name[i]))+sep;
//      String s= ns.toString(value)+sep+ns.toString(weight)+sep+ns.toString(colour) +sep+ ns.toString(distance) +sep+ ns.toString(penalty) +sep+ ns.toString(potential1) +sep+ ns.toString(separation); // +sep+ ns.toString(edgeRank);
      return(  s);
    }

    /**
     * Returns string suitable for labelling columns of the toString output.
     * @param sep separation character
     * @return returns a string of values separated by separation character
    */
    public static String toStringLabel(String sep) 
    {
       String s="";
       for (int i=0; i<name.length;i++) s= s+name[i]+sep;
       return(  s);
    }

    /**
     * Returns edge values which can not be derived as a single string.
     * @param sep separation character
     * @param dec integer number of decimal places to display
     * @return returns a string of values separated by separation character
     */
    public String parameterString(String sep, int dec) 
    {
        NumbersToString ns = new NumbersToString(dec);
       String s="";
       for (int i=0; i<parameterName.length;i++) s= s+ns.toString(getVariable(parameterName[i]))+sep;
//      String s= ns.toString(value)+sep+ns.toString(weight)+sep+ns.toString(colour) +sep+ ns.toString(distance) +sep+ ns.toString(penalty) +sep+ ns.toString(potential1) +sep+ ns.toString(separation); // +sep+ ns.toString(edgeRank);
      return(  s);
    }
    /**
     * Returns edge values which can not be derived as a single string.
     * @param sep separation character
     * @return returns a string of values separated by separation character
    */
    public static String parameterNames(String sep) 
    {            String s="";
       for (int i=0; i<parameterName.length;i++) s= s+parameterName[i]+sep;
       return(  s);
}

}
