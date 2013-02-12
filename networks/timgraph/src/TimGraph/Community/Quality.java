/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.AdjacencyMatrix;
import TimGraph.timgraph;
import cern.colt.matrix.DoubleMatrix2D;
//import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import java.io.PrintStream;
 

/**
 * General class for measures of community quality or modularity.
 * <br>An adjacency matrix and a quality matrix are both precalculated
 * and are stored using sparse matrices. 
 * Works with all types of graph as it will use the pi Vector (page rank vector)
 * when working with the modularity of directed graphs.
 * Override relevant classes to provide different optimisations.
 * To seethe different options for the null model see 
 * {@link TimGraph.Community.Quality#nullModelSwitch}.
 * <p>For less memory usage when you have no multiple edges
 * see {@link TimGraph.Community.Community.QualityMinimalMemory}.
  * @see TimGraph.Community.QualityMinimalMemory
  * @see TimGraph.Community.Quality#nullModelSwitch
 * @author time
 */
public class Quality {
    
    final static QualityType  basicType = new QualityType("DM");
    
    /**
     * The matrix giving the quality contribution from each edge
     */
    protected DoubleMatrix2D QMatrix;

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
     * Null Model switch.
     * <p>Chooses equilibrium random walker vector for null model.
     */
    protected boolean equilibrium = true;

    /**
     * Null Model switch.
     * <ul>
     * <li>0 = k_out k_out/W^2</li>
     * <li>1 = k_in k_out/W^2</li>
     * <li>2 = k_out k_in/W^2</li>
     * <li>3 = k_in k_in/W^2</li>
     * <li>4 = pi pi</li>
     * <li>5 = 1/(W^2)</li>
     * </ul>
     * where pi is the normalised page rank vector, the eigenvector of the largest
     * eigenvalue or the limit of A^n (k_in/W)
     */
    protected int nullModelSwitch = 0;
    public final static String [] nullModelString = {"out-out", "in-out", "out-in", "in-in", "Pi-Pi", "constant"};


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
    public Quality(){ setQualityType();  }
    /**
     * Defines simple quality (Newman basic modularity) from given graph.
     * <br>Directed graph should use Pi-Pi not simple out-out null model type.
     * @param graph timgraph defining the graph.
     * @param qdef selects modularity definition to use
     * @param nullModel index  of null model to use
     * @param newlambda scaling factor for null model in quality function
     * @param infoLevelNew sets level of information to be given
     * @see #nullModelSwitch
     */
    public Quality(timgraph graph, int qdef, int nullModel, 
            double newlambda, int infoLevelNew){
        initialisePreAdjacencyMatrix(graph, qdef, nullModel, newlambda, infoLevelNew);
        initialiseAdjacencyMatrix(graph);
        if (infoLevel>0) System.out.println("Check Quality Matrix = "+check());

    }

    /**
     * Initialises everything except the adjacency matrix.
     * @param graph timgraph defining the graph.
     * @param qdef selects modularity definition to use
     * @param nullModel index of null model to use
     * @param newlambda scaling factor for null model in quality function
     * @param infoLevelNew sets level of information to be given
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
   public void initialisePreAdjacencyMatrix(timgraph graph, int qdef, int nullModel, 
           double newlambda, int infoLevelNew){
        infoLevel= infoLevelNew;
        setQualityType();
        Qdefinition =qdef;
        nullModelSwitch=nullModel;
        setNullModelScaling(newlambda);
    }
    /**
     * Initialise quality using simplest (Newman) definition.
     * @param graph timgraph defining the graph. 
     */
    public void initialiseAdjacencyMatrix(timgraph graph) {

        AdjacencyMatrix AMatrix = new AdjacencyMatrix(graph, Qdefinition); 
        numberVertices= AMatrix.dimension();
        AMatrix.calculateInOutVectors();
        if (nullModelSwitch==4) AMatrix.calculatePiVector();
        QMatrix = new SparseDoubleMatrix2D(numberVertices,numberVertices);
        if (infoLevel>0) System.out.println("Total Weight = "+AMatrix.totalWeight());
        double nullValue=-1;
        double v=-1;
        double nullModelNorm = (nullModelSwitch==4?1:AMatrix.totalWeight()*AMatrix.totalWeight());
        for (int s = 0; s < numberVertices; s++) {
            for (int t = 0; t < numberVertices; t++) {
                switch (nullModelSwitch){
                    case 5:
                        nullValue = 1.0/AMatrix.totalWeight();
                        break;
                    case 4:
                        nullValue = AMatrix.getNormalisedPi(s) * AMatrix.getNormalisedPi(t);
                        break;
                    case 3:
                        nullValue = AMatrix.getInStrength(s) * AMatrix.getInStrength(t)/nullModelNorm;
                        break;
                    case 2:
                        nullValue = AMatrix.getOutStrength(s) * AMatrix.getInStrength(t)/nullModelNorm;
                        break;
                    case 1:
                        nullValue = AMatrix.getInStrength(s) * AMatrix.getOutStrength(t)/nullModelNorm;
                        break;
                    case 0:
                    default:
                        nullValue = AMatrix.getOutStrength(s) * AMatrix.getOutStrength(t)/nullModelNorm;
                        break;
                }
                v = (AMatrix.get(s, t)/AMatrix.totalWeight()) - (lambda*nullValue);
                QMatrix.set(s,t,v);
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
        return QMatrix.get(s,t);
    }

    /**
     * Value of null model scaling.
     * @return gamma null model scaling factor.
     */
    public double getLambda(){return lambda;}
    
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
     * @deprecated use sparse matrix version
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
     * Calculates the quality measure using a community matrix.
     * <br><code>QMatrix[s][t]</code> is the contribution to the quality measure of the edge from vertex s to vertex t.
     * That is modularity (the quality of the community structure) is
     * <code>Q = sum_(c) sum_(s, t) QMatrix[s][t] cM[s][c] cM[t][c]</code> where cM[s][c] is the fraction that vertex s is in community c
     * <br>NOTE this includes tadpoles s=t.
     * @param communityMatrix matrix containing assignment of each vertex to communities
     * @return the quality of the community partition.
     */
    public double calc(DoubleMatrix2D communityMatrix, int numberCommunities) {
        double Q=0;
        for (int s = 0; s < numberVertices; s++) {
            for (int t = 0; t < numberVertices; t++)
                for (int c=0; c<numberCommunities; c++) Q += get(s,t)*communityMatrix.get(s,c)*communityMatrix.get(t,c);
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
     * Sets switch for null model form.
     * @param nullModelSwitch new value for null model switch
     */
    public void setNullModelSwitch(int newNullModelSwitch){
       if (newNullModelSwitch<0 || newNullModelSwitch>=nullModelString.length) {
            System.err.println("!!! null model unchanged, model number "
                    +newNullModelSwitch
                    +" invalid, must be between 0 and "+nullModelString.length);
        }
       nullModelSwitch=newNullModelSwitch;
    }

    /**
     * Gives nullModelSwitch, the value used to switch between  null models.
     * @return null model switch
     */
    public int getNullModelSwitch(){return nullModelSwitch;}
    /**
     * Gets abbreviation description of null model
     */
    public String getNullModelShortString(){return nullModelString[nullModelSwitch];}


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
        return QualitySparse.QdefinitionString[Qdefinition] +sep+getNullModelShortString() + sep+"lambda="+lambda;
    }

    public String Qdefinition() {return Qdefinition("_");
    }

    public String QdefinitionShortString() {
        long l = Math.round(lambda * 1000);
        return QualitySparse.QdefinitionShortString[Qdefinition]+getNullModelShortString()+l;}
    


    
        /** Prints Quality Matrix.
         *@param PS a print stream for the output such as System.out
         *@param sep separation string
         *@param labelsOn true if want row and column labels
         */     
     public void  printMatrixOLD(PrintStream PS,String sep, boolean labelsOn){
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

    
    /**
     * Prints matrix to a PrintStream.
     * @param PS printstream such as System.out
     * @param sep separation string such as a tab character.'
     * @param headersOn true if want row and column labelled with names (if given) or numbers.
     */
    public void printMatrix(PrintStream PS, String sep, boolean headersOn)
    {
        String doubleFormat ="%6.3f";
        String intFormat ="%6d";
        //System.out.println(name+" matrix");
        if (headersOn){
            PS.print("      "+sep);
            for (int j=0; j<numberVertices;j++) PS.print(String.format(intFormat, j)+sep);
            PS.println();
        }
        for (int i=0; i<numberVertices;i++)
        {
            if (headersOn) PS.print(String.format(intFormat, i)+sep);
            for (int j=0; j<numberVertices; j++) PS.print(String.format(doubleFormat,get(i,j))+sep);
            PS.println();
        }
    }//eo printMatrix

}
