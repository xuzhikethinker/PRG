/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.AdjacencyMatrix;
import TimGraph.timgraph;
import java.io.PrintStream;
 

/**
 * General class for measures of community quality or modularity.
 * <br>Implements a simple definition of precalculated quality using matrices.
 * Override relevant classes to provide different optimisations.
 * @author time
 */
public class QualityLineGraph {
    
    final static QualityType  basicType = new QualityType("DM");
    
    /**
     * The matrix giving the quality contribution from each edge
     */
    protected double [][] QMatrix;
    protected int numberVertices;
    /**
     * Selects definition of Q to use.
     */
    protected int Qdefinition=-124578;
    
    /**
     * Selects type of Q in use.
     */
    protected  QualityType  qualityType;

    

            /**
     * Selects scaling of null model contribution
     */
    protected double lambda=-985421;
    
    public final static String [] QdefinitionString = {"Basic Newman", "(A^2-A)"};
    public final static String [] QdefinitionShortString = {"QS", "QA2mA"};
    public static final int QSindex=0;
    public static final int QA2mAindex=1;
    
    public int infoLevel=0;

    
    /**
     * Bare constructor.
     */
    public QualityLineGraph(){ setQualityType();  }
    /**
     * Defines simple quality (Newman basic modularity) from given graph.
     * @param graph timgraph defining the graph. 
     * @param qdef selects modularity definition to use
     * @param newlambda scaling factor for null model in quality function
     */
    public QualityLineGraph(timgraph graph, int qdef, double newlambda, int infoLevelNew){
        infoLevel= infoLevelNew;
        setQualityType();
        Qdefinition =qdef;
        setNullModelScaling(newlambda);
        initialiseAdjacencyMatrix(graph);
    }

    /**
     * Initialise quality using simplest (Newman) definition.
     * @param graph timgraph defining the graph. 
     */
    public void initialiseAdjacencyMatrix(timgraph graph) {

        AdjacencyMatrix AMatrix = new AdjacencyMatrix(graph, Qdefinition); 
        numberVertices= AMatrix.dimension();
        AMatrix.calculateInOutVectors();
        QMatrix = new double[numberVertices][numberVertices];
        if (infoLevel>0) System.out.println("Total Weight = "+AMatrix.totalWeight());
        
        for (int s = 0; s < numberVertices; s++) {
            for (int t = 0; t < numberVertices; t++) {
                QMatrix[s][t] = (AMatrix.get(s, t) - lambda*AMatrix.getInStrength(s) * AMatrix.getOutStrength(t)/AMatrix.totalWeight())/AMatrix.totalWeight() ;
            }
        }
    }
    
    
    
    /**
     * Sums all elements of the QMatrix.
     * <br>This should be zero and is equivalent to putting all vertices into the same community.
     * @return sum of all elements in the Quality matrix.
     */
    public double check(){
        double total=0;
        for (int s = 0; s < numberVertices; s++) 
            for (int t = 0; t < numberVertices; t++) total+=get(s,t);
        return total;
    }
    /**
     * Gets the quality contribution for the edge from vertex s to t
     * @param s source vertex
     * @param t target vertex
     * @return the quality matrix entry
     */
    public double get(int s, int t){
        return QMatrix[s][t];
    }
    
    /**
     * Contribution to Quality from vertex s if it is in community c.
     * <p>Does not include the contribution from self-loop term.
     * Also does not include the contribution from removing vertex s from its current community.
     * To get that call this routine with c equal to the current  community label of s.
     * <br> <code>Q=\sum_{i,j} (AMatrix_{ij} - inVector[i] OutVector[j]) delta(c_i,c_j)</code>
     * where <code>AMatrix_{ij}</code> ought to be influence normalised by the (out) strength
     * and the in/out vectors are the in/out strength of each vertex normalised by total strength.
     * @param s vertex 
     * @param c community label it is to be added to
     * @param communityOfVertex vector containing labels (integers) of each vertex
     * @return Contribution to Quality 
     */
    public double delta(int s, int c, int [] communityOfVertex){
        double deltaQ = 0;
        for (int t=0; t<numberVertices; t++)
            if ((communityOfVertex[t]==c) && (s !=t)) deltaQ+= get(s,t)+get(t,s);
        return deltaQ;
    }

    
     /**
     * Calculates the quality measure using a community matrix.
     * <br><code>QMatrix[s][t]</code> is the contribution to the quality measure of the edge from vertex s to vertex t.  
     * That is modularity (the quality of the community structure) is
     * <code>Q = sum_(c) sum_(s, t) QMatrix[s][t] cM[s][c] cM[t][c]</code> where cM[s][c] is the fraction that vertex s is in community c
     * <br>NOTE this includes tadpoles s=t.
     * @param communityMatrix matrix containing assignment of each vertex to communities
     * @return the quality of the community partition.
     */
    public double calc(double [][] communityMatrix, int numberCommunities) {
        double Q=0;
        for (int s = 0; s < numberVertices; s++) {
            for (int t = 0; t < numberVertices; t++) 
                for (int c=0; c<numberCommunities; c++) Q += get(s,t)*communityMatrix[s][c]*communityMatrix[t][c];
        }
        return Q;
    }

    
     /**
     * Calculates the quality measure.
     * <br><code>QMatrix[s][t]</code> is the contribution to the quality measure of the edge from vertex s to vertex t.  
     * That is modularity (the quality of the community structure) is
     * <code>Q = sum_(s, t) QMatrix[s][t] delta(C_s,C_t)</code> where C_s is the community label given to vertex s.
     * <br>NOTE this includes tadpoles s=t.
     * @param communityOfVertex vector containing labels (integers) of each vertex
     * @return the quality of the community partition.
     */
    public double calc(int [] communityOfVertex) {
        int c;
        double Q=0;
        for (int s = 0; s < numberVertices; s++) {
            c = communityOfVertex[s];
            for (int t = 0; t < numberVertices; t++) 
                if (communityOfVertex[t] == c) Q += get(s,t);
        }
        return Q;
    }

    
    /**
     * Calculates the quality measure for given partition.
     * <br><code>QMatrix[s][t]</code> is the contribution to the quality measure of the edge from vertex s to vertex t.  
     * That is modularity (the quality of the community structure) is
     * <code>Q = sum_(s != t) QMatrix[s][t] delta(C_s,C_t)</code> where C_s is the community label given to vertex s.
     * <br>NOTE this excludes tadpoles s=t.  Also no checks done
     * @param communityOfVertex vector containing labels (integers) of each vertex
     * @return the quality of the community partition.
     */
    public double calcNoTadpoles(int [] communityOfVertex) {
        int c;
        double Q=0;
        for (int s = 0; s < numberVertices; s++) {
            c = communityOfVertex[s];
            for (int t = 0; t < numberVertices; t++) 
                if ((communityOfVertex[t] == c) && (s != t)) Q += get(s,t);
        }
        return Q;
    }

    /**
     * Calculates the trace of the quality matrix.
     * <br>This is the contribution from the sef-loops to the modularity 
     * (the quality of the community structure) is
     * @return the quality of the community partition.
     */
    public double calcTrace() {
        double Q=0;
        for (int s = 0; s < numberVertices; s++) Q += get(s,s);
        return Q;
    }

    /**
     * Gives lambda, the scaling of the null model contribution.
     * @return lambda
     */
    public double getNullModelScaling(){return lambda;}
    
    /**
     * Sets lambda, the scaling of the null model contribution.
     * @param newLambda new value for lambda
     */
    public void setNullModelScaling(double newLambda){lambda=newLambda;}

    /**
     * Sets qualityType;
     */
    protected void setQualityType(){qualityType = basicType;}
        
        
    /**
     * Gets description of quality type;
     */
    public String getQualityTypeDescription(){return qualityType.toLongString();}
        
    /**
     * Gets number of quality type;
     */
    public int getQualityTypeNumber(){return qualityType.getNumber();}
        
    
    public String Qdefinition(String sep) {
        if (lambda == 1) {
            return QualitySparse.QdefinitionString[Qdefinition];
        }
        return QualitySparse.QdefinitionString[Qdefinition] + sep+"lambda="+lambda;
    }

    public String Qdefinition() {return Qdefinition("_");
    }

    public String QdefinitionShortString() {
        if (lambda == 1) {
            return QualitySparse.QdefinitionShortString[Qdefinition];
        }
        int l = (int) (lambda * 1000);
        return QualitySparse.QdefinitionShortString[Qdefinition]+l;}
    


    
        /** Prints Quality Matrix.
         *@param PS a print stream for the output such as System.out
         *@param sep separation string
         *@param labelsOn true if want row and column labels
         */     
     public void  printMatrix(PrintStream PS,String sep, boolean labelsOn){ 
         //PS.println("Number of Vertices "+sep+ numberVertices);
         if (labelsOn) {
             PS.print("[s]/[t]");
         for (int s=0; s<numberVertices; s++) PS.print(sep+s);
         PS.println();
         }
         for (int s=0; s<numberVertices; s++)         {
             if (labelsOn) PS.print(s);
             for (int t=0; t<numberVertices; t++) PS.print(sep+get(s,t));
             PS.println();
         }
             
    }

    

}
