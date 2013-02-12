/**
 * IslandEdgeSet.java
 *
 * Created on 27 July 2006, 16:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks.Edge;

//import IslandNetworks.Edge.EdgeTypeSelection;

import IslandNetworks.Vertex.IslandSite;
import IslandNetworks.Vertex.IslandSiteSet;
import IslandNetworks.IslandHamiltonian;
import IslandNetworks.IslandTransferMatrix;

import TimUtilities.Permutation;
import TimUtilities.StatisticalQuantity;
import TimUtilities.NumbersToString;
import java.util.Random;
import java.io.PrintStream;




/**
 * Defines set of all edges for the Island Network.
 *<br> replaces edgeValue[][]
 * @author time
 */
public class IslandEdgeSet 
{
        final static String IESVERSION = "IES080412";
        final static int STRUPDATE = 1000; // no. of times to access strength before recalculates fresh.
        public static double MAXSEPARATION = 8.8e22;
        static NumbersToString n2s = new NumbersToString(3);

        
        private int numberSites;
        private int numberEdges = -1; // this is set positive when edges have been ranked by value
        public EdgeMode edgeMode;
        private IslandEdge [][] edge; // edge values from site i to site j
       // edgeValueRankList[r] = j*numbersites + k = the number of the edge ranked r-th is [j][k] where
        private int [] edgeValueRankList; // edge Value Rank list
        private int [] edgeWeightRankList; // edge Weight Rank list
        private double [] outEdgeStrength; // strength to be updated incrementally
        private double [] inEdgeStrength;
        private int [] strengthInCount ; // force immediate complete strength calculation
        private int [] strengthOutCount ; // force immediate complete strength calculation
        
        // Statistics
        public StatisticalQuantity [] edgeStats;
        public int maxEdgeWeightSource=-1;
        public int maxEdgeWeightTarget=-1;
        
        // Separation (effective distance) parameters
        public DistanceMetric metric; // set type of separation (eff.distance) measurements to use
        public static int numberMetrics=6; // no. of possible metrics numbered 0 .. (numberMetrics-1)
        public double DijkstraMaxSep;
        
        // Display characteristics - these are currently usually implemented via colours
        // and these are calculated in islandNetwork.calcNetworkStats
        public EdgeTypeSelection DisplayEdgeType; // 
        //private String edgeCorrelationType="Unset";
        public double zeroColourFrac=0.03;  // relative value of variable
        public double minColourFrac=0.1;    // relative value of variable
        public double DisplayMaxEdgeScale=1.0;   //<=0 then display edges relative to largest
                                        // else display edges relative to this absolute size       
        public double displayMaximumEdgeWeight; // display edges above this absolute variable size as thickness and colour of largest possible edge
        public double displayMinimumEdgeWeight; // display edges below this absolute variable size as thickness and colour of smallest possible non-zero edge
        public double displayZeroEdgeWeight; // display edges below this absolute variable size as zero weight edges
        
        private double distanceDiameter =-1; // negative means not set yet
        private double separationDiameter =-1; // negative means not set yet
    
    /** 
     * Creates a new instance of IslandEdgeSet 
     *@param ns number of sites.
     *@param edgeModeValue =0 for binary mode, >0 max value mode, <0 max Out strength Mode
     */
    public IslandEdgeSet(int ns, double edgeModeValue) 
    {
        numberSites=ns;
        outEdgeStrength = new double [numberSites];
        inEdgeStrength = new double [numberSites];
        strengthInCount = new int [numberSites];
        strengthOutCount = new int [numberSites];
        edge = new IslandEdge [numberSites][numberSites] ;
        edgeMode = new EdgeMode(edgeModeValue);
        for (int i=0; i<numberSites; i++) 
        {
          outEdgeStrength[i] = 0;  
           inEdgeStrength[i] = 0;  
           strengthInCount[i]=STRUPDATE; 
           strengthOutCount[i]=STRUPDATE;
           
          for (int j=0; j<numberSites; j++) edge[i][j] = new IslandEdge();
        }
       
        // Distance parameters
        metric= new DistanceMetric(0); // set type of separation (eff.distance) measurements to use
        DijkstraMaxSep = MAXSEPARATION;        
        DisplayEdgeType = new EdgeTypeSelection();
    }

    /** 
     * Creates a new instance of IslandEdgeSet by deep copying existing one.
     *@param es IslandEdgeSet to copy.
     */
    public IslandEdgeSet(IslandEdgeSet es) 
    {
        numberSites=es.numberSites;
        outEdgeStrength = new double [numberSites];
        inEdgeStrength = new double [numberSites];
        strengthInCount = new int [numberSites];
        strengthOutCount = new int [numberSites];
        edge = new IslandEdge [numberSites][numberSites] ;
        edgeMode = new EdgeMode(es.edgeMode);
        
        for (int i=0; i<numberSites; i++)
        {
            for (int j=0; j<numberSites; j++)  edge[i][j] = new IslandEdge(es.edge[i][j]);            
        }
        
        //only after edge values are set can we initialise values and counts for strengths
        for (int i=0; i<numberSites; i++)
        {
            calcInStrength(i);
            calcOutStrength(i);            
        }
        
        DisplayEdgeType = new EdgeTypeSelection(es.DisplayEdgeType);
        
        
        // deal with statistics
        // TODO shoul;d we just call a calcStats routine?
        edgeStats = new StatisticalQuantity [IslandEdge.numberVariables];
        for (int v=0; v<IslandEdge.numberVariables; v++) edgeStats[v] = new StatisticalQuantity(es.edgeStats[v]);
//        edgeWeightStats = new StatisticalQuantity(es.edgeValueStats);
//        edgeValueStats= new StatisticalQuantity(es.edgeWeightStats); 
//        edgeDistanceStats= new StatisticalQuantity(es.edgeDistanceStats);     
//        edgeBarePotential1Stats= new StatisticalQuantity(es.edgeBarePotential1Stats);    
//        edgePotential1Stats= new StatisticalQuantity(es.edgePotential1Stats);     
//        edgeGeneCorrelationStats = new StatisticalQuantity(es.edgeGeneCorrelationStats);     

        maxEdgeWeightSource=es.maxEdgeWeightSource;
        maxEdgeWeightTarget=es.maxEdgeWeightTarget;
        

        
        // Distance parameters
        metric = new DistanceMetric(es.metric); // set type of separation (eff.distance) measurements to use
        DijkstraMaxSep = es.DijkstraMaxSep;
        
        // Display characteristics - these are currently usually implemented via colours
        // and these are calculated in islandNetwork.calcNetworkStats
        zeroColourFrac=es.zeroColourFrac;
        minColourFrac=es.minColourFrac;
        DisplayMaxEdgeScale=es.DisplayMaxEdgeScale;
        
        displayMaximumEdgeWeight = es.displayMaximumEdgeWeight; // display edges above this weight as thickness and colour of largest possible edge
        displayMinimumEdgeWeight = es.displayMinimumEdgeWeight; // display edges below this weight as thickness and colour of smallest possible non-zero edge
        displayZeroEdgeWeight = es.displayZeroEdgeWeight; // display edges below this weight as zero weight edges
        
    }

    public int getNumberSites(){return  numberSites;}
    
    /**
     * Returns edge.
     *@param i source (from) site
     *@param j target (to) site
     *@return edge[i][j]
     */
    
    /**
     * Returns unique index for edge.
     * @param i source vertex number
     * @param j target vertex number
     * @return edge index = (source vertex number)*numbersites+(target vertex number)
     */
    public int getIndex(int i, int j)
    {
        return(i*numberSites+j);
    }
    
    
    /**
     * Returns pointer to edge.
     * @param i source vertex number
     * @param j target vertex number
     * @return pointer to edge
     */
    public IslandEdge getEdge(int i, int j)
    {
        return(edge[i][j]);
    }

    /**
     * Returns pointer to edge.
     * @param id edge index 
     * @return pointer to edge
     */
    public IslandEdge getEdge(int id)
    {
        return(edge[id/numberSites][id%numberSites]);
    }

        /**
     * Returns source vertex id .
     * @param id edge index 
     * @return source vertex
     */
    public int getSource(int id)
    {
        return(id/numberSites);
    }

        /**
     * Returns target vertex id .
     * @param id edge index 
     * @return target vertex
     */
    public int getTarget(int id)
    {
        return(id%numberSites);
    }


    /**
     * Returns variable value.
     * @param id edge index 
     * @param index edge variable index
     * @return variable value
     */
    public double getVariable( int id, int index ){
        return(getEdge(id).getVariable(index) );
    }


    
    /**
     * Returns value of indexed edge variable.
     *@param i source (from) site
     *@param j target (to) site
     *@param index edge variable index
     *@return value of variable index for edge[i][j].
     */
    public double getVariable(int i, int j, int index)
    {
        return(edge[i][j].getVariable(index) );
    }
    /**
     * Returns value of named edge variable.
     *@param i source (from) site
     *@param j target (to) site
     * @param name variable's name
     *@return value of variable name i.e. edge[i][j].name
     */
    public double getVariable(int i, int j, String name)
    {
        return(edge[i][j].getVariable(name) );
    }
    /**
     * Returns edge value.
     *@param i source (from) site
     *@param j target (to) site
     *@return edge[i][j].getValue()
     */

    /**
     * Returns value for edge variable being used for display.
     * @param id edge index 
     * @return value for edge variable being used for display
     * @deprecated use getDisplayEdgeVariable()
     */
    public double getDisplayVariable( int id){
        return(getEdge(id).getVariable(DisplayEdgeType.getValueIndex()) );
    }
    /**
     * Returns value for edge variable being used for display.
     * @param s source (from) site
     * @param t target (to) site
     * @return value for edge variable being used for display
     * @deprecated use getDisplayEdgeVariable()
     */
    public double getDisplayVariable( int s, int t){
        return(getEdge(s,t).getVariable(DisplayEdgeType.getValueIndex()));
    }
    /**
     * Index of variable being used for display variables.
     * @return index as integer
     */
    public int getDisplayEdgeVariable(){ return DisplayEdgeType.getValueIndex();}
    /**
     * Name of variable being used for display variables.
     * @return name as string
     */
    public String getDisplayEdgeVariableName(){ return DisplayEdgeType.getCurrentTypeString();}
    /**
     * Returns value to be used to when displaying edge.
     * <p>Gives a value between 0 and 1.  The variable used is that whose index is given by  
     * <code>DisplayEdgeType.getValueIndex()</code>.
     * In general the value is scaled relative to <code>displayMaximumEdgeWeight</code> 
     * but values above this are returned as one.
     * If the relative value is less than <code>zeroColourFrac</code> then zero is returned 
     * while if its between this value and <code>minColourFrac</code> then <code>minColourFrac</code> is returned.
     *@param id index of edge
     * @return value used for edge in display from between 0 and 1
     */
    public double getEdgeDisplaySize(int id){
        return (getEdgeDisplaySize(getDisplayVariable(id)/displayMaximumEdgeWeight));
    }
    /**
     * Returns value to be used to when displaying edge.
     * <p>Gives a value between 0 and 1.  The variable used is that whose index is gioven by  
     * <code>DisplayEdgeType.getValueIndex()</code>.
     * In general the value is scaled relative to <code>displayMaximumEdgeWeight</code> 
     * but values above this are returned as one.
     * If the relative value is less than <code>zeroColourFrac</code> then zero is returned 
     * while if its between this value and <code>minColourFrac</code> then <code>minColourFrac</code> is returned.
     *@param s source (from) site
     *@param t target (to) site
     * @return value used for edge in display from between 0 and 1
     */
    public double getEdgeDisplaySize(int s, int t){
        return (getEdgeDisplaySize(getDisplayVariable(s,t)/displayMaximumEdgeWeight));
    }
    /**
     * Returns value to be used to when displaying edge.
     * <p>Gives a value between 0 and 1.  The variable used is that whose index is gioven by  
     * <code>DisplayEdgeType.getValueIndex()</code>.
     * In general the value is scaled relative to <code>displayMaximumEdgeWeight</code> 
     * but values above this are returned as one.
     * If the relatoive value is less than <code>zeroColourFrac</code> then zero is returned 
     * while if its between this value and <code>minColourFrac</code> then <code>minColourFrac</code> is returned.
     *@param v relative value of display variable
     * @return value used for edge in display from between 0 and 1
     */
    private double getEdgeDisplaySize(double v){
        if (v<minColourFrac) v=(v<zeroColourFrac?0:zeroColourFrac);
        else if (v>1) v=1;
        return v;
    }

    /**
     * Returns average value in network for edge variable being used for display.
     * @param index index of variable whose average is required.
     * @return average value for edge variable
     */
    public double getAverage(int index){
        return(edgeStats[index].getAverage() );
    }
    /**
     * Returns maximum value in network for edge variable being used for display.
     * @param index index of variable whose maximum is required.
     * @return maximum value for edge variable
     */
    public double getMaximum(int index){
        return(edgeStats[index].maximum );
    }
    /**
     * Returns maximum value in network for edge variable being used for display.
     * @return maximum value for edge variable being used for display
     */
    public double getDisplayMaximum(){
        return(edgeStats[DisplayEdgeType.getValueIndex()].maximum );
    }

    public double getEdgeValue(int i, int j)
    {
        return(edge[i][j].getValue() );
    }
    /**
     * Returns edge weight (S_iv_ie_ij).
     *@param i source (from) site
     *@param j target (to) site
     *@return edge[i][j].weight
     */
    public double getEdgeWeight(int i, int j)
    {
        return(edge[i][j].getWeight() );
    }
    
    /**
     * Returns edge value squared.
     *@param i source (from) site
     *@param j target (to) site
     *@return edgeValue[i][j]
     */
    public double getEdgeValueSquared(int i, int j)
    {
        double v=edge[i][j].getValue();
        return( v*v);
    }

    /**
     * Returns edge Colour.
     *@param i source (from) site
     *@param j target (to) site
     *@return edgeColour[i][j]
     *@deprecated
     */
    public double getEdgeColour(int i, int j)
    {
        return(edge[i][j].getColour() );
    }

    /**
     * Returns edge physical fixed distance.
     *@param i source (from) site
     *@param j target (to) site
     *@return edgeDistance[i][j]
     */
    public double getEdgeDistance(int i, int j)
    {
        return(edge[i][j].getDistance() );
    }

    /**
     * Returns edge physical fixed diameter distance.
     *@return fixed distance diamater
     */
    public double getDistanceDiameter()
    {
        if (distanceDiameter<0)
        {
            for (int i=0; i<numberSites; i++){
                for (int j=0; j< numberSites; j++){
                    if (distanceDiameter<edge[i][j].getDistance()) distanceDiameter =edge[i][j].getDistance();
                }
            }
        }
        return( distanceDiameter);
    }

        /**
     * Returns edge separation diameter distance.
     *@return fixed distance diamater
     */
    public double getSeparationDiameter()
    {
        if (separationDiameter<0)
        {
            for (int i=0; i<numberSites; i++){
                for (int j=0; j< numberSites; j++){
                    if (separationDiameter<edge[i][j].getSeparation()) separationDiameter =edge[i][j].getSeparation();
                }
            }
        }
        return( separationDiameter);
    }

//        /**
//     * Returns edge penalty.
//     *@param i source (from) site
//     *@param j target (to) site
//     *@return edgePenalty[i][j]
//     */
//    public double getEdgePenalty(int i, int j)
//    {
//        return(edge[i][j].getPenalty() );
//    }

    /**
     * Returns edge Distance potential including lambda factor under model 1.
     * <p>The potential times lambda <tt>\lambda V(d_{ij}/d_s)</tt>.
     *@param i source (from) site
     *@param j target (to) site
     *@return edge[i][j].potential1
     */
    public double getEdgePotential1(int i, int j)
    {
        return(edge[i][j].getEdgePotential1() );
    }

//    /**
//     * Returns edge bare potential (no including lambda factor) under model 1.
//     * <p>The potential <tt>V(d_{ij}/d_s)</tt> with no lambda.
//     *@param i source (from) site
//     *@param j target (to) site
//     *@return edge[i][j].barePotential1
//     */
//    public double getEdgeBarePotential1(int i, int j)
//    {
//        return(edge[i][j].getEdgeBarePotential1() );
//    }

    /**
     * Sets and returns edge Distance potential under model 1 using separation (effective distance).
     *@param i source (from) site
     *@param j target (to) site
     *@return old edgePotential1(distance[i][j])
     */
    public double getEdgePotentialSeparation1(int i, int j, IslandHamiltonian H)
    {
        return(edge[i][j].getEdgePotentialSeparation1(H));
    }

    /**
     * Returns edge separation, an effective distance..
     *@param i source (from) site
     *@param j target (to) site
     *@return old distance[i][j]
     */
    public double getEdgeSeparation(int i, int j)
    {
        return(edge[i][j].getSeparation());
    }

    /**
     * Returns edge correlation in gene space, an effective distance..
     *@param i source (from) site
     *@param j target (to) site
     *@return gene correlation
     */
    public double getGeneCorrelation(int i, int j)
    {
        return(edge[i][j].getGeneCorrelation());
    }

//    /**
//     * Returns type of edge correlation in use.
//     *@return correlation type
//     */
//    public String correlationType()
//    {
//        return(edgeCorrelationType);
//    }

    /**
     * Gives the in degree of vertex.
     * @param i vertex number
     * @return the in degree of vertex i 
     */
    public int getInDegree(int i) 
    {  
        int d=0;
        for (int j=0; j<numberSites; j++) if (edge[j][i].getValue()>0) d++; 
        return( d ) ;
    };
    
      /**
     * Gives the Out degree of vertex.
     * @param i vertex number
     * @return the out degree of vertex i
     */
    public int getOutDegree(int i) 
    {  
        int d=0;
        for (int j=0; j<numberSites; j++) if (edge[i][j].getValue()>0) d++; 
        return( d ) ;
    };
    
    
    /**
     * Returns out edge Strength.
     *@param i site
     *@return outEdgeStrength[i]
     */
    public double getOutEdgeStrength(int i) { return(outEdgeStrength[i]); }
    /**
     * Returns in edge Strength.
     *@param i site
     *@return inEdgeStrength[i]
     */
    public double getInEdgeStrength(int i) { return(inEdgeStrength[i]);}

    /**
     * Sets and returns edge value, updates strengths while maintaining all appropriate bounds.
     * <p> If edge values have maximum value set then when given value exceeds the maximum value then the maximum value is used.
     * <br> If the strength of of edge is limited then value is set to saturate this if it is too big.
     * <br> If value given is less than zero then edge is set to zero.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new value
     *@return edgeValue[i][j]
     */
    public double setEdgeValueBounded(int i, int j, double value)
    {
        double dv = value-edge[i][j].getValue();
        if ((edgeMode.maxValueModeOn) && (value>edgeMode.maximumValue) )
        {
            value = edgeMode.maximumValue;
            dv = value-edge[i][j].getValue();
        }

        if ( (edgeMode.outStrengthLimitOn) && (outEdgeStrength[i]+dv >edgeMode.maximumValue) )
        {
            dv=edgeMode.maximumValue-outEdgeStrength[i]; 
            value = dv+edge[i][j].getValue();            
        }
        if (value<0) { value=0; dv = value-edge[i][j].getValue();}
        edge[i][j].setValue(value);
        if ((--strengthInCount[j])<0)  calcInStrength(j);  else  inEdgeStrength[j] += dv;
        if ((--strengthOutCount[i])<0) calcOutStrength(i); else outEdgeStrength[i] += dv;
 
        return(value);
    }

    /**
     * Sets and returns edge value, updates strengths but applies no bounds.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new value
     *@return edgeValue[i][j]
     */
    public double setEdgeValueNoBounds(int i, int j, double value)
    {
        double dv = value-edge[i][j].getValue();
        edge[i][j].setValue(value);
        if ((--strengthInCount[j])<0)  calcInStrength(j);  else  inEdgeStrength[j] += dv;
        if ((--strengthOutCount[i])<0) calcOutStrength(i); else outEdgeStrength[i] += dv;
        return(value);
    }
    
    
    /**
     * Sets edge variable without any checking.
     * @param s source vertex 
     * @param t target vertex
     * @param index index of edge variable
     * @param value value assigned to edge variable
     */
        public void setVariable(int s, int t, int index, double value)
        { edge[s][t].setVariable(index, value);
        }

    /**
     * Sets edge variable without any checking.
     * @param id index of vertex
     * @param index index of edge variable
     * @param value value assigned to edge variable
     */
        public void setVariable(int id, int index, double value)
        { edge[id/numberSites][id%numberSites].setVariable(index, value);
        }

    
    /**
     * Sets and returns edge Value if under maximum In/Out strengths.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new colour value
     *@param maxInStrength maximum in strength, no update if exceeded
     *@param maxOutStrength maximum in strength, no update if exceeded
     *@return edgeValue[i][j]
     * @deprecated
     */
    public double setEdgeValue(int i, int j, double value, double maxInStrength, double maxOutStrength)
    {
        double currentValue = edge[i][j].getValue();
        double dv = value-currentValue;
        if (outEdgeStrength[i] +dv>maxOutStrength) return(currentValue);
        if (inEdgeStrength[j] +dv>maxInStrength) return(currentValue);
        return(setEdgeValueBounded(i,j,value));
    }
    
    
    /**
     * Sets and returns edge Colour.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new colour value
     *@return edgeColour[i][j]
     *@deprecated
     */
    public double setEdgeColour(int i, int j, double value)
    {
        edge[i][j].setColour(value);
        return(value);
    }

        /**
     * Sets and returns fixed edge Distance.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new Distance value
     *@return old distValue[i][j]
     */
    public double setEdgeDistance(int i, int j, double value)
    {
        edge[i][j].setDistance(value);
        return(value);
    }

        /**
     * Sets and returns fixed edge gene corrleation.
     *@param i source (from) site
     *@param j target (to) site
     *@param weighting new gene separation
     *@return weighting
     */
    public double setGeneCorrelation(int i, int j, double weighting)
    {
        edge[i][j].setGeneCorrelation(weighting);
        return weighting;
    }
    
        /**
     * Sets and returns fixed edge gene corrleation.
     *@param siteSet set of sites
     */
    public void setGeneCorrelations(IslandSiteSet siteSet){
        siteSet.setGeneLengths();
        for (int i =0; i<numberSites; i++) {
               for (int j =0; j<numberSites; j++) {
                   setGeneCorrelation(i,j,siteSet.getGeneCorrelation(i,j));
               } // eo for j
           } //eo for i
}
    
//        /**
//     * Sets and returns edge Penalty.
//     *@param i source (from) site
//     *@param j target (to) site
//     *@param value new penalty value
//     *@return edgePenalty[i][j]
//     */
//    public double setEdgePenalty(int i, int j, double value)
//    {
//        edge[i][j].setPenalty(value);
//        return(value);
//    }

        /**
     * Sets and returns edge separation, an effective distance..
     *@param i source (from) site
     *@param j target (to) site
     *@param value new penalty value
     *@return old distance[i][j]
     */
    public double setEdgeSeparation(int i, int j, double value)
    {
         separationDiameter =-1; // negative means not set yet
        edge[i][j].setSeparation(value);
        return(value);
    }

    /**
     * Sets the edge values to be a constant  except for self loops and short distance edges which are set to be zero.
     * @param value set the edge values to this number
     */
    public void setEdgeValues(double value) 
    {
       setEdgeValuesZero();
       for (int i=0; i<numberSites; i++) 
        for (int j=0; j<numberSites; j++) if ((i!=j) && (getEdgeDistance(i, j)> IslandHamiltonian.SHORTDISTANCEPOTENTIAL)) setEdgeValueBounded(i,j,value);       
    }

    
    /**
     * Sets the edge values for all sites to value except for loops which are zero.
     */
    public void setEdgeValuesZero() 
    {
       for (int i=0; i<numberSites; i++) 
        {
          outEdgeStrength[i] = 0;  
           inEdgeStrength[i] = 0;  
           strengthInCount[i]=STRUPDATE; 
           strengthOutCount[i]=STRUPDATE;         
           for (int j=0; j<numberSites; j++) edge[i][j].setValue(0);
        }    
    }

    

   /**
     * Sets random edge values for all sites except for self loops and short distance edges.
     *<p> Uses maximumValue and mode already set up.
    * @param rnd a Random variable generetaor
     */
    public void setRandomEdgeValues(Random rnd) 
    {
       if (edgeMode.binary) {setRandomBinaryEdgeValues(rnd); return;} 
       setEdgeValuesZero();
       
       int i=-1;
       int j=-1;
       Permutation sourcePerm = new Permutation(numberSites);
       Permutation targetPerm;
       for (int ip=0; ip<numberSites; ip++) 
       {
           double x=edgeMode.maximumValue;
           i = sourcePerm.next();
           targetPerm = new Permutation(numberSites);                
           for (int jp=0; jp<numberSites; jp++) 
           {
                j = targetPerm.next();
                if ((i!=j) && (getEdgeDistance(i, j)> IslandHamiltonian.SHORTDISTANCEPOTENTIAL)) setEdgeValueBounded(i,j,x*rnd.nextDouble() ); 
                if (edgeMode.outStrengthLimitOn) x=edgeMode.maximumValue-outEdgeStrength[i];           
           }// eo for j
       }// eo for i
    }

     /**
     * Sets the edge values to be 0 or 1 for all sites.
     * @param rnd a Random variable generetaor
     */
    private void setRandomBinaryEdgeValues(Random rnd) 
    {
       setEdgeValuesZero();
       for (int i=0; i<numberSites; i++) 
          for (int j=0; j<numberSites; j++) if ((i!=j) && (getEdgeDistance(i, j)> IslandHamiltonian.SHORTDISTANCEPOTENTIAL)) setEdgeValueBounded(i,j,(rnd.nextBoolean()?1.0:0));
    }
    
    /**
     * Calculates the in strength of given site.
     * @param n the site of interest
     */
    private double calcInStrength(int n) 
    {
        strengthInCount[n]=STRUPDATE;
        double v=0;
        for (int i=0; i<numberSites; i++) v+=getEdgeValue(i,n);
        return v;
    }

    /**
     * Calculates the out strength of given site.
     * @param n the site of interest
     */
    private double calcOutStrength(int n) 
    {
        strengthOutCount[n]=STRUPDATE;
        double v=0;
       for (int i=0; i<numberSites; i++) v+=getEdgeValue(n,i);
        outEdgeStrength[n]=v;
        return v;
    }
    
    /**
     * Calculates the in strength squared of given site.
     * @param n the site of interest
     */
    public double getInStrengthSquared(int n) 
    {
        double v=0;
        double ev=-1;
       for (int i=0; i<numberSites; i++) {ev=getEdgeValue(i,n); v+=ev*ev;}
        return v;
    }

    /**
     * Calculates the out strength sqaured of given site.
     * @param n the site of interest
     */
    public double getOutStrengthSquared(int n) 
    {
        double v=0;
        double ev=-1;
       for (int i=0; i<numberSites; i++) {ev=getEdgeValue(n,i); v+=ev*ev;}
        return v;
    }

    /**
     * Sets the distance potential values for model 1 for all sites.
     * @param H the Hamiltonian to use
     */
    public void setEdgePotential1(IslandHamiltonian H) 
    {
       for (int i=0; i<numberSites; i++) 
        for (int j=0; j<numberSites; j++) edge[i][j].setEdgePotential1(H);
        
    }
    
// ********************************************************************
   
    /**
     * Sets up absolute display values.
     * <br>Calculates relevant statistics.
     * @param siteSet set of site variables
     */
    public void setUpEdgeDisplayValues(IslandSiteSet siteSet){      
      int v=DisplayEdgeType.getValueIndex();
      calcStats(siteSet, v);
      double amew=-1.0;
      if (DisplayMaxEdgeScale>0) amew =  DisplayMaxEdgeScale;
      else amew = edgeStats[v].maximum;
      displayZeroEdgeWeight = amew*zeroColourFrac;
      displayMinimumEdgeWeight = amew*minColourFrac;
      displayMaximumEdgeWeight = amew;
    }    
    /**
     * Sets up absolute display values and colour values.
     * <br>Calculates relevant statistics.
     * @param siteSet set of site variables
     * @param numberColours maximum value to assign to colour of an edge
     * @deprecated 
     */
    public void setUpEdgeDisplayValues(IslandSiteSet siteSet, double numberColours){
      setUpEdgeDisplayValues(siteSet);
      int v=DisplayEdgeType.getValueIndex();    
      for (int i=0; i<numberSites; i++)
        {
           for (int j=0; j<numberSites; j++)
           {
               setEdgeColour(i,j, numberColours*this.getVariable(i,j,v)/displayMaximumEdgeWeight); // edge weight colour 
               if (getEdgeColour(i,j)>numberColours) setEdgeColour(i,j,numberColours);
               if (getEdgeColour(i,j)<numberColours*minColourFrac)
               {  if (getEdgeColour(i,j)<numberColours*zeroColourFrac) setEdgeColour(i,j,0);
                  else setEdgeColour(i,j, numberColours*zeroColourFrac);
               }
           }           
        }//eo for i
}
// *********************************************************
//           *@param metricNumber = 0 plain physical distance, 
//                                = 1 physical distance divided by potential
//                                = 2 inverse edge strength        

    /** Runs Dijkstra's algorithm on the network.
     * <p>Does Dijkstra, updates distanceFromV global
     * Sets edgeSet.getEdgeSeparation(i,j) to shortest distance from i to j 
     * using metricNumber to choose the for metric.
     * and DijkstraMaxSep is the longest path between any two sites,
     * and it equals MAXSEPARATION if disconnected
     * <p>TODO use IslandSiteSet as input
     * @param shortDistanceScale short distance scale used to modify some metrics.
     */
    public void doDijkstra(IslandSite [] siteArray, double shortDistanceScale) 
    {
        final double superMaxSep=MAXSEPARATION*1.0001;
        double newsep,eee,eeew,d;
        DijkstraMaxSep=0;
            for (int v=0; v<numberSites; v++) 
            {// look at distances from vertex v
                int mdv=v; // vertex number with minimum separation from v
                double minseparation =MAXSEPARATION; // separation equivalent to infinity
                boolean [] notVisited = new boolean[numberSites];
                for (int i=0; i<numberSites; i++) 
                {
                    setEdgeSeparation(v,i,MAXSEPARATION); notVisited[i]=true;
                }
                setEdgeSeparation(v,v,0.0);
                for (int n=0; n<numberSites; n++) 
                {   // first find mdv, the unvisited vertex with smallest separation from v
                    minseparation = MAXSEPARATION;
                    mdv=-1;
                    for (int j=0; j<numberSites; j++){
                        if ( notVisited[j] && (getEdgeSeparation(v,j)<minseparation) ) 
                        {
                         minseparation=getEdgeSeparation(v,j); 
                         mdv = j;
                        }
                    }
                    if (minseparation==MAXSEPARATION || mdv<0) break;    // must be finished
                    // visit mdv (fix its separation)  and update separation from v to j the neighbours of mdv
                    notVisited[mdv]=false;
                    for (int j=0; j<numberSites; j++)
                    {
                        if (!notVisited[j]) continue;
                        eee=getEdgeValue(mdv,j);
                        // if (eee==0) continue;
                        //Deal with zero edges on metric by metric basis, often by catching an error
                        //eeew=siteArray[mdv].getWeight() *eee  ;
                        newsep = superMaxSep; // if any below fail set new separation to infinite separation
                        try{ // this is the metric
                            switch (metric.getNumber())
                            {
                                case 5:
                                    d=getEdgeDistance(mdv,j);
                                    if (d<=shortDistanceScale) newsep = minseparation+ getEdgeDistance(mdv,j);
                                    else newsep = minseparation+ d / (getEdgePotential1(mdv,j)) ;
                                    break;
                                case 4:
                                    newsep = minseparation+ 1.0 /  (siteArray[mdv].getWeight() *eee  )  ;
                                    break;
                                case 3:
                                    d=getEdgeDistance(mdv,j);
                                    if (d<=shortDistanceScale) newsep = minseparation+ d/ (siteArray[mdv].getWeight()  ) ;
                                    else newsep = minseparation+ d  / (getEdgePotential1(mdv,j)* (siteArray[mdv].getWeight() *eee  ) ) ;
                                    break;
                                case 2:
                                    newsep = minseparation+ 1.0 /  eee  ;
                                    break;
                                case 1:
                                    d=getEdgeDistance(mdv,j);
                                    if (d<=shortDistanceScale) newsep = minseparation+ d;
                                    else newsep = minseparation+ d  / ( getEdgePotential1(mdv,j)* eee ) ;
                                    break;
                                case 0:
                                default:
                                    newsep = minseparation+ getEdgeDistance(mdv,j);
                            }
                            if (getEdgeSeparation(v,j)>newsep) setEdgeSeparation(v,j,newsep);
                        } finally{}
                    }//eo for j
                }//eo for n
                if (DijkstraMaxSep<minseparation) DijkstraMaxSep=minseparation;
            } // eo for v
        
    }

// *********************************************************

//    /**
//     * Gives the metric type as a String
//     * @return the metric number 
//     * 
//     */
//    public String getMetricString() 
//    {  
//        //int metricNumber=getMetricNumber();
//        String s="unknown";
//        switch (metricNumber)
//        {
//            case 0: s="plain physical distance"; break;
//            case 1: s="sum of individual physical distance/potential using edge values";  break;
//            case 2: s="inverse edge value"; break;
//            case 3: s="sum of individual physical distance/potential using edge weights"; break;
//            case 4: s="inverse edge weight"; break;       
//            case 5: s="sum of physical distance/potential (but no edge values)"; break;       
//            default: s="unknown";
//        }
//        return(s) ;
//    };

// ***************************************************************************************
   /** Returns the edge number of edge of given rank.
    *@param rank the rank of the required edge
    *@return the number of the edge from i to j of requested rank in the format i*numbersites + j 
      */   
   public int getEdgeGivenValueRank(int rank){
       if (numberEdges<0) calcEdgeValueOrder();
       return(edgeValueRankList[rank]);
   }
   
   /**
    * Sets the edge Weights S_iv_ie_ij
    * @param siteSet set of sites
    */
   public void setEdgeWeights(IslandSiteSet siteSet){
      double vw;
      for (int i=0; i<numberSites; i++)
        {
          vw=siteSet.getWeight(i); // siteSet.getName(i) has weight vw  
           for (int j=0; j<numberSites; j++) edge[i][j].setWeight(getEdgeValue(i,j)*vw);
        }// eo for i
   }
   
   // ********************************************************************
   
   /**
    * Calculates the statistics of the all values in edge set.
    * <p>Also sets the edge weights.
    * @param siteSet set of sites
    */
   public void calcStats(IslandSiteSet siteSet){
      edgeStats = new StatisticalQuantity [IslandEdge.numberVariables];
      for (int v=0; v<IslandEdge.numberVariables; v++) calcStats(siteSet,v);
   }
   
   /**
    * Calculates the statistics of the all values in edge set.
    * <p>Also sets the edge weights.
    * @param siteSet ste of sites 
    * @param v index of the edge variable
    */
   public void calcStats(IslandSiteSet siteSet, int v){
     if (v==IslandEdge.weightINDEX) setEdgeWeights(siteSet);
     calcStats(v);
   }
   
   /**
    * Calculates the statistics of the all values in edge set.
    * <p>Does NOT set the edge weights so these must be set if doing weight stats.
    * @param v index of the edge variable
    */
   public void calcStats(int v){
     if (edgeStats == null) edgeStats = new StatisticalQuantity [IslandEdge.numberVariables];
     double eV;
     if (!isSet(1, v))  return;
        edgeStats[v] = new StatisticalQuantity(1.23e34,-1.0);
        for (int i=0; i<numberSites; i++)
        {
           for (int j=0; j<numberSites; j++)
           {
               eV=this.getVariable(i, j, v);
               if ((v==IslandEdge.weightINDEX) && (eV > edgeStats[v].maximum))  {maxEdgeWeightSource=i; maxEdgeWeightTarget=j;}               
               edgeStats[v].add(eV); // updates the stats on the edge variable v
           }// eo for j           
        }// eo for i
         }
   
    /**
     * Tests to see if variable has been set.
     * @param id index of edge
     * @param i index of variable
     * @return true (false) if variable is set (unset)
     */
    public boolean isSet(int id, int i){return getEdge(id).isSet(i); }
    
    /**
     * Sets the edge influence and influence' weights.
     * <br> Note that the influence matrix and the edge set have source and target indices transposed.
     * <code>e_{ji}(InfluenceWeight) = I_{ij} S_j v_j =</code>
     * <code>e_{ji}(InfluenceprimeWeight) = I'_{ij} S_j v_j =</code>
     * influence matrix entries multiplied by the source site weights.
     * @param ss set of island sites
     * @param tm transfer matrix.
     */
    public void setInfluenceWeight(IslandSiteSet ss, IslandTransferMatrix tm){
        for (int i=0; i<numberSites; i++)
        {
          for (int j=0; j<numberSites; j++) {
              edge[i][j].setInfluenceWeight(tm.getInfluence(j, i)*ss.getWeight(i));
              edge[i][j].setInfluencePrimeWeight(tm.getInfluencePrime(j, i)*ss.getWeight(i));
          }
        }// eo for i
        //edgeCorrelationType="Influence Weight"+tm.getInfluenceProbability();
        calcStats(IslandEdge.influenceWeightINDEX);
        calcStats(IslandEdge.influenceprimeWeightINDEX);
    }
    /**
     * Sets the edge betweenness.
     * <p>Uses <tt>B_{ij} = \sum_{st} (S_s v_s)[I]_{si}T_{ij}[I]_{jt}</tt>
     * and similar for I' and T' cases.
     * Entries normalised so biggest always 1.0.
     * @param ss set of island sites
     * @param transferMatrix transfer matrix.
     */
    public void setBetweenness(IslandSiteSet ss, IslandTransferMatrix transferMatrix){
        double [][]  betweenness = new double[numberSites][numberSites];
        double maxBetweenness=-97.6;
        double [][]  betweennessp = new double[numberSites][numberSites];
        double maxBetweennessp=-97.6;
        double [][]  nbetweenness = new double[numberSites][numberSites];
        double maxnBetweenness=-97.6;
        double [][]  nbetweennessp = new double[numberSites][numberSites];
        double maxnBetweennessp=-97.6;
        for (int i=0; i<numberSites; i++)
        {
          for (int j=0; j<numberSites; j++) {
            betweenness[i][j] =0; // unneccessary
            betweennessp[i][j] =0; // unneccessary
            nbetweenness[i][j] =0; // unneccessary
            nbetweennessp[i][j] =0; // unneccessary
            if (i==j) continue;
            for (int s = 0; s < numberSites; s++) {
                if (s==i || s==j) continue;
                double sourceWeight=ss.getWeight(s);
                for (int t = 0; t < numberSites; t++)
                    if (t!=s && t!=i && t!=j) {
                        betweenness[i][j]  += transferMatrix.getInfluence(t, j) *transferMatrix.get(j, i)*transferMatrix.getInfluence(i, s) * sourceWeight;
                        betweennessp[i][j] += transferMatrix.getInfluencePrime(t, j) *transferMatrix.getPrime(j,i) *transferMatrix.getInfluencePrime(i, s) * sourceWeight;
                        nbetweenness[i][j]  += transferMatrix.get(j, i)     * (transferMatrix.getInfluence(i, s) - transferMatrix.getInfluence(i, t) )* sourceWeight;
                        nbetweennessp[i][j] += transferMatrix.getPrime(j,i) * (transferMatrix.getInfluencePrime(i, s) - transferMatrix.getInfluencePrime(i, t) ) * sourceWeight;
                    }
            }//eo for s
            if (maxBetweenness<betweenness[i][j]) maxBetweenness=betweenness[i][j];
            if (maxBetweennessp<betweennessp[i][j]) maxBetweennessp=betweennessp[i][j];
          }//eo for j
        }// eo for i
        
        if (maxBetweenness>1e-3)
            for (int i=0; i<numberSites; i++)
            {
              for (int j=0; j<numberSites; j++) {
              edge[i][j].setBetweenness(betweenness[i][j]/maxBetweenness);
              }//eo for j
            }// eo for i
        calcStats(IslandEdge.betweennessINDEX);

        if (maxBetweennessp>1e-3)
            for (int i=0; i<numberSites; i++)
            {
              for (int j=0; j<numberSites; j++) {
              edge[i][j].setBetweennessPrime(betweennessp[i][j]/maxBetweennessp);
              }//eo for j
            }// eo for i
        calcStats(IslandEdge.betweennessprimeINDEX);

       for (int i=0; i<numberSites; i++)
            {
              for (int j=0; j<numberSites; j++) {
              edge[i][j].setNewmanBetweenness(nbetweenness[i][j]);
              edge[i][j].setNewmanBetweennessPrime(nbetweennessp[i][j]);
              }//eo for j
            }// eo for i
       calcStats(IslandEdge.nbetweennessINDEX);
        calcStats(IslandEdge.nbetweennessprimeINDEX);

    }
//    /**
//     * Sets the edge correlation to be given by the product of the potential and the gene correlation.
//     */
//    public void setPotentialGeneCorrelation(){
//        for (int i=0; i<numberSites; i++)
//        {
//          for (int j=0; j<numberSites; j++) edge[i][j].setCorrelation(edge[i][j].getGeneCorrelation()*edge[i][j].getEdgePotential1());
//        }// eo for i
//        //edgeCorrelationType="gene correlation * potential";
//        calcStats(IslandEdge.correlationINDEX);
//    }

//    /**
//     * Sets the edge correlation to be given by the product of the source site rank and the esge value.
//     * @param iss island site set
//     */
//    public void setRank(IslandSiteSet iss){
//        for (int i=0; i<numberSites; i++)
//        {
//          for (int j=0; j<numberSites; j++) edge[i][j].setCorrelation(edge[i][j].getValue()*iss.getRanking(i) );
//        }// eo for i
//        //edgeCorrelationType="edge rank";
//        calcStats(IslandEdge.correlationINDEX);
//    }
    
   
// ***************************************************************************************

    
    /**
           *  Returns string summarising display values.
           *@param sep1 separator between name and data value, e.g. &quot;: &quot;.
           *@param sep2 separator between different variables e.g. tab or space or linefeed.
           *@return string summarising display values.
           */
          public String displaySizeString(String sep1, String sep2){
        return "ew zero"+sep1+displayZeroEdgeWeight+sep2+"ew min"+sep1+displayMinimumEdgeWeight+sep2+"ew max"+sep1+displayMaximumEdgeWeight;
    }
          /**
           *  Returns string to use a tool tip for edges in graph.
           * <p>Will give nothing for a variable if it is not set.
           * @param id index of edge
           *@param sep1 separator between name and data value, e.g. &quot;: &quot;.
           *@param sep2 separator between different variables e.g. tab or space or linefeed.
           *@param dec number of decimal places to keep
           *@return string with name of data variable number value for site index.
           */
          public String toGraphToolTipString(int id, String sep1, String sep2, int dec)
        {
              String s="Source"+sep1+getSource(id)+sep2+"Target"+sep1+getTarget(id);
              int i=this.DisplayEdgeType.getValueIndex();
              if (getEdge(id).isSet(i)) s=s+sep2+IslandEdge.name[i] + sep1 + n2s.TruncDecimal(getVariable(id,i), dec);
              return s;
          }

   
   
          /**
           *  Returns string for a edge with name then value of all data variables.
           * <p>Will give nothing for a variable if it is not set.
           * @param id index of edge
           *@param sep1 separator between name and data value, e.g. &quot;: &quot;.
           *@param sep2 separator between different variables e.g. tab or space or linefeed.
           *@param dec number of decimal places to keep
           *@return string with name of data variable number value for site index.
           */
          public String toString(int id, String sep1, String sep2, int dec)
        {
              String s="Source/Target"+sep1+getSource(id)+" "+getTarget(id)+sep2;
              for (int i=0;i<IslandEdge.numberVariables;i++) if (getEdge(id).isSet(i)) s=s+IslandEdge.name[i] + sep1 + getVariable(id,i)+sep2;
              return s;
          }


   /**
    * Returns a string of all the values stored in a given edge.
    * @param source source vertex number
    * @param target target vertex number
    * @param sep separation string
    * @param dec decimal places to use.
    * @return string with all values associated with edge
    */
   public String getEdgeString(int source, int target, String sep, int dec){
       return edge[source][target].toString(sep, dec);
   }
   

   /** Prints edges as source, target, edge.toString.
    *@param PS PrintStream for output
    * @param cc comment character
    * @param sep separation string
    *@param dec integer number of decimal palces to display
    * @param headerOn true (false) if want header line
    */   
   public void printList(PrintStream PS, String cc, String sep, int dec, boolean headerOn){
       if(headerOn) PS.println(cc+"source"+sep+"Target"+sep+IslandEdge.toStringLabel(sep));
       for (int i=0; i<numberSites; i++) 
                {
           for (int j=0; j<numberSites; j++) 
                {
               PS.println(i+sep+j+sep+edge[i][j].toString(sep,dec));
           }
       }
   }// eo print

   // --------------------------------------------------------------------
   /** Prints edge parameters as bare list (no header).
    * <br> Parameters are those needed to reconstruct the results.
    *<br>printNames routine prints labels of values in order
    * line number = (source*numberSites+target) = edge index
    *@param PS PrintStream for output
    * @param cc comment character
    * @param sep separation string
    *@param dec integer number of decimal palces to display
    * @param headerOn true (false) if want header line
    */   
   public void printParameterList(PrintStream PS,  String cc, String sep, int dec, boolean headerOn){
       if(headerOn) PS.println(cc+IslandEdge.parameterNames(sep));
       for (int i=0; i<numberSites; i++) 
                {
           for (int j=0; j<numberSites; j++) 
                {
               PS.println(edge[i][j].parameterString(sep,dec));
           }
       }
   }// eo printValue

   /** Prints names of edge variables in order of printValues routine.
    * @param PS PrintStream for output
    * @param sep separation string
    */   
   public void printParameterNames(PrintStream PS, String sep)
   {PS.println(IslandEdge.parameterNames(sep));
   }// eo printNames

   
      // --------------------------------------------------------------------
   /** Prints edges as table each row is for one source.
    *@param PS PrintStream for output
    * @param sep separation string
    *@param dec integer number of decimal places to display
    */   
   public void printValueBareTable(PrintStream PS, String sep, int dec){
       for (int i=0; i<numberSites; i++) 
                {
           for (int j=0; j<numberSites; j++) PS.print(edge[i][j].getValue()+sep);
           PS.println();
       }
   }// eo printValue

      // --------------------------------------------------------------------
   /** Prints edges as table each row is for one source.
    *@param PS PrintStream for output
    *@param sep separation string
    *@param dec integer number of decimal places to display
    */   
   public void printDistanceBareTable(PrintStream PS, String sep, int dec){
       for (int i=0; i<numberSites; i++) 
                {
           for (int j=0; j<numberSites; j++) PS.print(edge[i][j].getDistance()+sep);
           PS.println();
       }
   }// eo printValue


    /** Prints table of one type of edge variable with in/out statistics calculated and displayed.
     *@param cc comment characters put at the start of every line
     *@param PS PrintStream such as System.out
     *@param sep separation string
     *@param dec integer number of decimal places to display
     *@param type name of variable to put in table as defined in IslandEdge class
     *@param siteSet set of sites associated with these edges
     */
public void printEdgeFullTable(String cc, PrintStream PS, String sep, int dec, String type, IslandSiteSet siteSet)

{
        int variableIndex = IslandEdge.getIndex(type);
        if (variableIndex<0) {
            System.err.println("*** wrong varible type in printEdgeFullTable, unknown type "+type);
            return;
        }
        printEdgeFullTable(cc, PS, sep, dec, variableIndex, siteSet);
    }
    /** Prints table of one type of edge variable with in/out statistics calculated and displayed.
     *@param cc comment characters put at the start of every line
     *@param PS PrintStream such as System.out
     *@param sep separation string
     *@param dec integer number of decimal places to display
     *@param variableIndex index of edge variable to put in table as defined in IslandEdge class
     *@param siteSet set of sites associated with these edges
     */
public void printEdgeFullTable(String cc, PrintStream PS, String sep, int dec, int variableIndex, IslandSiteSet siteSet)

{
        double [] toedgecount = new double[numberSites];
        //Next part is breakString
        String s="";
        double inTotal[] = new double[numberSites];
        double inTotal2[] = new double[numberSites];
         for (int i=0; i<numberSites; i++) s+="---"+sep;
        PS.println(cc+"EDGE "+IslandEdge.dataName(variableIndex)+s);
        PS.print("From/to");
        for (int i =0; i<numberSites; i++) {toedgecount[i]=0;}
        for (int i =0; i<numberSites; i++) {PS.print(sep+siteSet.getName(i));}
        PS.println(sep+"Total Out"+sep+"Total^2 Out");       
        for (int i =0; i<numberSites; i++) {        
            double outTotal=0;
            double outTotal2=0;
            PS.print(siteSet.getName(i));
            for (int j =0; j<numberSites; j++) {
                if (i==0) {inTotal[j]=0;inTotal2[j]=0;}
                double v=getVariable(i,j,variableIndex);
                outTotal+=v;
                outTotal2+=v*v;
                inTotal[j]+=v;
                inTotal2[j]+=v*v;
                PS.print(sep+n2s.TruncDecimal(v,dec));
            }
            PS.println(sep + n2s.TruncDecimal(outTotal,dec) + sep + n2s.TruncDecimal(outTotal2 ,dec) );
        }
        
        
       PS.print("Total In");
       double total=0.0;
       for (int i =0; i<numberSites; i++) 
        {
            total+=inTotal[i];
            PS.print(sep+n2s.TruncDecimal(inTotal[i],dec));
        }
        PS.println(sep+n2s.TruncDecimal(total,dec));
        
        PS.print("Total^2 In");
        total=0.0;
        for (int i =0; i<numberSites; i++) 
        {
            total+=inTotal2[i];
            PS.print(sep+n2s.TruncDecimal(inTotal2[i],dec));
        }
        PS.println(sep+n2s.TruncDecimal(total,dec));
        
     }//eo printEdgeValueTable

/** Prints Gene Correlation 
     *@param cc comment characters put at the start of every line
     *@param PS PrintStream such as System.out
     *@param sep separation string
     *@param dec integer number of decimal places to display
     *@param siteSet set of sites associated with these edges
 * @deprecated use printEdgeFullTable(cc, PS, sep, dec, "geneCorrelation", siteSet)
     */     
public void printGeneCorrelationFullTable(String cc, PrintStream PS, String sep, int dec, IslandSiteSet siteSet)

{
        double [] toedgecount = new double[numberSites];
        //Next part is breakString
        String s="";
         for (int i=0; i<numberSites; i++) s+="---"+sep;
        PS.println(cc+"EDGE GENE CORRELATIONS"+s);        
        PS.print("From/to");
        for (int i =0; i<numberSites; i++) {toedgecount[i]=0;}
        for (int i =0; i<numberSites; i++) {PS.print(sep+siteSet.getName(i));}
        PS.println(); //sep+"Total Out"+sep+"Total^2 Out");       
        for (int i =0; i<numberSites; i++) {        
            PS.print(siteSet.getName(i));
            for (int j =0; j<numberSites; j++) {
                PS.print(sep+n2s.TruncDecimal(getGeneCorrelation(i, j),dec));
            }
            PS.println(); //sep + n2s.TruncDecimal(getOutEdgeStrength(i) ,dec) + sep + n2s.TruncDecimal(getOutStrengthSquared(i) ,dec) );
        }
           
     }//eo printEdgeValueTable
    
  // ...................................................................

/**
 * @(#)QSortAlgorithm.java  1.3   29 Feb 1996 James Gosling
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
  */

/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @version     @(#)QSortAlgorithm.java 1.3, 29 Feb 1996
 */

// Adapted to sort index array
    
    //public class QSortAlgorithm extends SortAlgorithm {
   /** This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    *   QuickSort(a, 0, a.length - 1);
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition
    * @param hi0     right boundary of array partition
    */

  
//   public void sort(int a[]) throws Exception
//   {
//      QuickSort(a, 0, a.length - 1);
//   }
//}end of sort
  
// ---------------------------------------------------------      
   /**
    * Sets edgeValueRankList with edges ordered by their edge values.
    */
  public void calcEdgeWeightOrder()
  {
       Random Rnd = new Random(); //Schildt p524
       numberEdges = numberSites * numberSites;
       edgeWeightRankList = new int[numberEdges];
       double [] edgeWeights = new double[numberEdges];
       // set initial list in order
       for (int e=0; e<numberEdges; e++) edgeValueRankList[e]=e;
// Randomise initial list to deal with equality
      int eb,e2;
      for (int e=0; e<numberEdges; e++)
      {
          e2=Rnd.nextInt(numberEdges);
          if (e2 != e) {
                         eb=edgeValueRankList[e2];
                         edgeValueRankList[e2]=edgeValueRankList[e];
                         edgeValueRankList[e]=eb;
                         }
      }
      QuickSort(edgeValueRankList, 0, numberEdges-1);

      return;
  }
   /**
    * Sets edgeValueRankList with edges ordered by their edge values.
    */
  public void calcEdgeValueOrder()
  {
       Random Rnd = new Random(); //Schildt p524
       numberEdges = numberSites * numberSites;
      // edgeValueRankList[i] = the edge ranked ith is [j][k] where
       // i = j*numbersites + k
       edgeValueRankList = new int[numberEdges];
      // set initial list in order 
      for (int e=0; e<numberEdges; e++) edgeValueRankList[e]=e;
// Randomise initial list to deal with equality
      int eb,e2;
      for (int e=0; e<numberEdges; e++) 
      {
          e2=Rnd.nextInt(numberEdges);
          if (e2 != e) { 
                         eb=edgeValueRankList[e2];
                         edgeValueRankList[e2]=edgeValueRankList[e];
                         edgeValueRankList[e]=eb;
                         }
      }
      QuickSort(edgeValueRankList, 0, numberEdges-1);

      return; 
  }

   /** 
    * Sorts integer array.
    *@param a returned as list of indices of edgeweightlist in ranked order.
    *@param lo0 the starting index
    *@param hi0 the maximum index (inclusive)
    */      
    private void QuickSort(int a[], int lo0, int hi0)
   {
      int T;
      int lo = lo0;
      int hi = hi0;
      int midindex;
      double mid;

      if ( hi0 > lo0)
      {

         /** Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         midindex = ( lo0 + hi0 ) / 2 ;
         mid=edge[midindex/numberSites][midindex%numberSites].getValue();
//         mid = a[ ( lo0 + hi0 ) / 2 ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /** find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
            // while( ( lo < hi0 ) && ( a[lo] < mid ) ) 
            while( ( lo < hi0 ) && ( edge[a[lo]/numberSites][a[lo]%numberSites].getValue()  < mid ) )
               ++lo;

            /** find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
            //while( ( hi > lo0 ) && ( a[hi] > mid ) )
            while( ( hi > lo0 ) && ( edge[a[hi]/numberSites][a[hi]%numberSites].getValue()  > mid ) )
               --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            { // swap elements
               T = a[lo]; 
               a[lo] = a[hi];
               a[hi] = T;
               
               ++lo;
               --hi;
            }
         }

         /** If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            QuickSort( a, lo0, hi );

         /** If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            QuickSort(a, lo, hi0 );

      }
   }

// *********************************************************************************
    
    
// **************************************************************************

    /**
     * Stores mode for edges.
     * <p>Binary mode means edges are 0 or 1.
     * <p>Maximum Value mode means edges are between 0 and maximumValue, a value specified.
     * <p>Maximum Out Strength mode means edges out strengths are between 0 and maximumValue.
     */ 
    public class EdgeMode {
        public boolean binary;
        public boolean maxValueModeOn;
        public boolean outStrengthLimitOn;
        public double maximumValue;        
    
    /** Creates a new instance of EdgeMode.
     *@param edgeMode =0 for binary mode, &gt;0 max. value mode, &lt;0 max. Out strength Mode
     */
    public EdgeMode(double edgeMode) {
        setEdgeMode(edgeMode);     
    }

     /** Creates a new instance of EdgeMode by deep copy.
     *@param em existing edgeMode
      */
    public EdgeMode(EdgeMode em) {
        binary = em. binary;
        maxValueModeOn = em.maxValueModeOn;
        outStrengthLimitOn = em. outStrengthLimitOn;
        maximumValue = em. maximumValue;        
    }

    /** Sets value of EdgeMode.
     *@param edgeMode =0 for binary mode, &gt;0 max. value mode, &lt;0 max. Out strength Mode
     */
    public void setEdgeMode(double edgeMode) {
        if (edgeMode==0) setBinaryModeOn();
        else if (edgeMode>0) setMaxValueModeOn(edgeMode);
        else setMaxOutStrengthModeOn(edgeMode);     
    }

    /** Sets binary mode on.
     */
    public void setBinaryModeOn()
    {
        binary=true;
        maxValueModeOn=false;
        outStrengthLimitOn=false;
        maximumValue=1.0;
    }

    /** Sets Maximum Value Mode mode on.
     *@param value maximum value for each edge
     */
    public void setMaxValueModeOn(double value)
    {
        binary=false;
        maxValueModeOn=true;
        outStrengthLimitOn=false;
        maximumValue=Math.abs(value);
    }

    /** Sets Maximum Out Strength Mode mode on.
     *@param value maximum value for each edge
     */
    public void setMaxOutStrengthModeOn(double value)
    {
        binary=false;
        maxValueModeOn=false;
        outStrengthLimitOn=true;
        maximumValue=Math.abs(value);
    }
    
    public String description()
    {
        String s="";
        if (binary) s=s+"Binary Edge Values";
        if (maxValueModeOn) s= s+"Maximum Edge Value "+maximumValue;
        if (outStrengthLimitOn) s= s+"Limits on Out Strength "+maximumValue;
        return(s);
    }

    /** Returns status as an edgeModeValue.
     *@return 0 for binary mode, >0 max value mode, <0 max Out strength Mode
     */
    public double edgeModeValue()
    {
        double edgeModeValue=-987654321;
        if (binary) edgeModeValue=0;
        if (maxValueModeOn) edgeModeValue=maximumValue;
        if (outStrengthLimitOn) edgeModeValue=-maximumValue;
        return(edgeModeValue);
    }
}
    
    
    
}
