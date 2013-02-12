/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.AdjacencyMatrix;
import TimGraph.LineGraphType;
import TimGraph.algorithms.LineGraphProjector;
import TimGraph.timgraph;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import java.io.PrintStream;
 

/**
 * General class for measures of edge community quality or modularity.
 * <br>An adjacency matrix and a quality matrix are both precalculated
 * and are stored using ordinary dense (colt) matrices. The adjacency matrix 
 * is the line graph type specified by <tt>lineGraphType</tt>.
 * 
 * The rest is to be confirmed.
 * <br>The types of Quality function are governed by the types of line graph
 * <em>Edge Partitions and Overlapping Communities in Complex Networks</em>,
 * T.S.Evans and R.Lambiotte <tt>arXiv:0912.4389</tt>.  They are set by the
 * <tt>type</tt> parameters and defined by constant strings in this class,
 * {@link LineGraphProjector#lgExtensionList} for file name additions
 * and {@link LineGraphProjector#lgExtensionDescription}.
 * These are as follows:
 * <ul>
 * <li>0 = Line Graph L(G)=C(G)</li>
 * <li>1 = Line Graph with self-loops, Ctilde</li>
 * <li>2 = Degree Weighted Line Graph, D(G)</li>
 * <li>3 = Degree Weighted Line Graph with self-loops, Dtilde(G)</li>
 * <li>4 = Strength Weighted Line Graph, E(G) </li>
 * <li>5 = Strength Weighted Line Graph with self-loops, Etilde(G)</li>
 * </ul>
 * 
 * Works with all types of graph as it will use the pi Vector (page rank vector)
 * when working with the modularity of directed graphs.
 * Override relevant classes to provide different optimisations.
 * To seethe different options for the null model see 
 * {@link TimGraph.Community.Quality#nullModelSwitch}.
 * <p>For less memory usage when you have no multiple edges
 * see {@link TimGraph.Community.Community.QualityMinimalMemory}.
  * @see TimGraph.Community.QualityMinimalMemory
  * @see TimGraph.Community.Quality#nullModelSwitch
  * @see TimGraph.LineGraphType
 * @author time
 */
public class EdgeQuality {
    
    final static QualityType  basicType = new QualityType("DM");
    
    /**
     * The matrix giving the quality contribution from each edge
     */
    protected DoubleMatrix2D QMatrix;

    /**
     * This is the number of edges.  
     * <p>If undirected then the edges is connected to other edges
     * incident at both ends of edge. Otherwise only edges whose
     * source is the same as another edges target are connected.
     */
    protected int numberEdges;
    /**
     * Selects definition of Q to use.
     */
    protected int Qdefinition=0;
    
    /**
     * Specifies type of Q in use.
     */
    protected  QualityType  qualityType;


    /**
     * Null Model switch.
     * <p>Chooses equilibrium random walker vector for null model.
     */
    protected boolean equilibrium = true;

    /**
     * Null Model selection.
     */
    QualityNullModel nullModel = new QualityNullModel();


   /**
     * Selects scaling of null model contribution
     */
    protected double lambda=-985421;


 
    /**
     * Specifies the type of line graph used for adjacency matrix.
     */
    public LineGraphType lineGraphType;
    
    public int infoLevel=0;

    
    /**
     * Bare constructor.
     */
    public EdgeQuality(){ setQualityType(); lineGraphType= new LineGraphType();   }
    /**
     * Defines simple quality (Newman basic modularity) from given graph.
     * Directed uses Pi-Pi, otherwise out-out is used.
     * Other choices must be set directly.
     * @param graph timgraph defining the graph.
     * @param qdef selects modularity definition to use
     * @param newlambda scaling factor for null model in quality function
     * @param infoLevelNew sets level of information to be given
     */
    public EdgeQuality(timgraph graph, int qdef, double newlambda, int infoLevelNew){
        lineGraphType= new LineGraphType();
        initialisePreAdjacencyMatrix(graph, qdef, newlambda, infoLevelNew);
        initialiseAdjacencyMatrix(graph);
        if (infoLevel>0) System.out.println("Check Quality Matrix = "+check());

    }

    /**
     * Initialises everything except the adjacency matrix.
     * <p>Sets the null model on the basis of the directed nature of the input graph.
     * Directed uses Pi-Pi, otherwise out-out is used.
     * Other choices must be set directly.
     * @param graph timgraph defining the graph.
     * @param qdef selects modularity definition to use
     * @param newlambda scaling factor for null model in quality function
     * @param infoLevelNew sets level of information to be given
     */
   public void initialisePreAdjacencyMatrix(timgraph graph, int qdef, double newlambda, int infoLevelNew){
        infoLevel= infoLevelNew;
        setQualityType();
        Qdefinition =qdef;
        setNullModelScaling(newlambda);
        nullModel.setNumber((graph.isDirected())?4:0);
    }
    /**
     * Initialise quality using simplest (Newman) definition.
     * <p>This may be slow and use large amounts of memory as it generates the 
     * line graph explicitly.
     * @param graph timgraph defining the graph. 
     */
    public void initialiseAdjacencyMatrix(timgraph graphInput) {

        timgraph lineGraph; 
        lineGraph = LineGraphProjector.makeLineGraph(graphInput, lineGraphType.getNumber(), (infoLevel>1));
        AdjacencyMatrix lgAMatrix = new AdjacencyMatrix(lineGraph, Qdefinition); 
        numberEdges= lgAMatrix.dimension();
        lgAMatrix.calculateInOutVectors();
        if (nullModel.usesPiVector()) lgAMatrix.calculatePiVector();
        QMatrix = new SparseDoubleMatrix2D(numberEdges,numberEdges);
        if (infoLevel>0) System.out.println("Total Weight = "+lgAMatrix.totalWeight());
        double nullValue=-1;
        double v=-1;
        double nullModelNorm = (nullModel.usesPiVector()?1:lgAMatrix.totalWeight()*lgAMatrix.totalWeight());
        for (int s = 0; s < numberEdges; s++) {
            for (int t = 0; t < numberEdges; t++) {
                switch (nullModel.getNumber()){
                    case 5:
                        nullValue = 1.0/nullModelNorm;
                        break;
                    case 4:
                        nullValue = lgAMatrix.getNormalisedPi(s) * lgAMatrix.getNormalisedPi(t);
                        break;
                    case 3:
                        nullValue = lgAMatrix.getInStrength(s) * lgAMatrix.getInStrength(t)/nullModelNorm;
                        break;
                    case 2:
                        nullValue = lgAMatrix.getOutStrength(s) * lgAMatrix.getInStrength(t)/nullModelNorm;
                        break;
                    case 1:
                        nullValue = lgAMatrix.getInStrength(s) * lgAMatrix.getOutStrength(t)/nullModelNorm;
                        break;
                    case 0:
                    default:
                        nullValue = lgAMatrix.getOutStrength(s) * lgAMatrix.getOutStrength(t)/nullModelNorm;
                        break;
                }
                v = (lgAMatrix.get(s, t)/lgAMatrix.totalWeight()) - (lambda*nullValue);
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
        for (int s = 0; s < numberEdges; s++) 
            for (int t = 0; t < numberEdges; t++) total+=get(s,t);
        return total;
    }
    /**
     * Gets the quality contribution for pair of edges.
     * <p>If directed then edge s have the incident vertex as its target
     * while edge t must have the incident vertex as its source
     * @param s source edge
     * @param t target edge
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
     * Contribution to Quality from edge s if it is in community c.
     * <p>Does not include the contribution from self-loop term.
     * Also does not include the contribution from removing edge s from its current community.
     * To get that call this routine with c equal to the current  community label of s.
     * <br> <code>Q=\sum_{e,f} (AMatrix_{ef} - inVector[e] OutVector[f]) delta(c_e,c_f)</code>
     * where <code>AMatrix_{ef}</code> ought to be influence normalised by the (out) strength
     * and the in/out vectors are the in/out strength of each vertex normalised by total strength.
     * @param s vertex 
     * @param c community label it is to be added to
     * @param communityOfVertex vector containing labels (integers) of each vertex
     * @return Contribution to Quality 
     */
    public double delta(int s, int c, int [] communityOfVertex){
        double deltaQ = 0;
        for (int t=0; t<numberEdges; t++)
            if ((communityOfVertex[t]==c) && (s !=t)) deltaQ+= get(s,t)+get(t,s);
        return deltaQ;
    }

    
     /**
     * Calculates the quality measure using a community matrix.
     * <br><code>QMatrix[s][t]</code> is the contribution to the quality measure 
     * of the coincidence of edge s to edge t.
     * That is modularity (the quality of the community structure) is
     * <code>Q = sum_(c) sum_(s, t) QMatrix[s][t] cM[s][c] cM[t][c]</code> where cM[s][c] is the fraction that vertex s is in community c
     * <br>NOTE this includes tadpoles s=t.
     * @param communityMatrix matrix containing assignment of each vertex to communities
     * @return the quality of the community partition.
     * @deprecated use sparse matrix version
     */
    public double calc(double [][] communityMatrix, int numberCommunities) {
        double Q=0;
        for (int s = 0; s < numberEdges; s++) {
            for (int t = 0; t < numberEdges; t++)
                for (int c=0; c<numberCommunities; c++) Q += get(s,t)*communityMatrix[s][c]*communityMatrix[t][c];
        }
        return Q;
    }

     /**
     * Calculates the quality measure using a community matrix.
     * <br><code>QMatrix[s][t]</code> is the contribution to the quality measure 
     * of the coincidence of edge s to edge t.
     * That is modularity (the quality of the community structure) is
     * <code>Q = sum_(c) sum_(s, t) QMatrix[s][t] cM[s][c] cM[t][c]</code> where cM[s][c] is the fraction that vertex s is in community c
     * <br>NOTE this includes tadpoles s=t.
     * @param communityMatrix matrix containing assignment of each vertex to communities
     * @return the quality of the community partition.
     */
    public double calc(DoubleMatrix2D communityMatrix, int numberCommunities) {
        double Q=0;
        for (int s = 0; s < numberEdges; s++) {
            for (int t = 0; t < numberEdges; t++)
                for (int c=0; c<numberCommunities; c++) Q += get(s,t)*communityMatrix.get(s,c)*communityMatrix.get(t,c);
        }
        return Q;
    }

    
     /**
     * Calculates the quality measure.
     * <br><code>QMatrix[s][t]</code> is the contribution to the quality measure 
     * of the coincidence of edge s to edge t.
     * That is modularity (the quality of the community structure) is
     * <code>Q = sum_(s, t) QMatrix[s][t] delta(C_s,C_t)</code> where C_s is the community label given to vertex s.
     * <br>NOTE this includes tadpoles s=t.
     * @param communityOfVertex vector containing labels (integers) of each vertex
     * @return the quality of the community partition.
     */
    public double calc(int [] communityOfVertex) {
        int c;
        double Q=0;
        for (int s = 0; s < numberEdges; s++) {
            c = communityOfVertex[s];
            for (int t = 0; t < numberEdges; t++) 
                if (communityOfVertex[t] == c) Q += get(s,t);
        }
        return Q;
    }

    
    /**
     * Calculates the quality measure for given partition.
     * <br><code>QMatrix[s][t]</code> is the contribution to the quality measure
     * of the coincidence of edge s to edge t.
     * That is modularity (the quality of the community structure) is
     * <code>Q = sum_(s != t) QMatrix[s][t] delta(C_s,C_t)</code> where C_s is the community label given to vertex s.
     * <br>NOTE this excludes tadpoles s=t.  Also no checks done
     * @param communityOfVertex vector containing labels (integers) of each vertex
     * @return the quality of the community partition.
     */
    public double calcNoTadpoles(int [] communityOfVertex) {
        int c;
        double Q=0;
        for (int s = 0; s < numberEdges; s++) {
            c = communityOfVertex[s];
            for (int t = 0; t < numberEdges; t++) 
                if ((communityOfVertex[t] == c) && (s != t)) Q += get(s,t);
        }
        return Q;
    }

    /**
     * Calculates the trace of the quality matrix.
     * <br>This is the contribution from the self-loops to the modularity 
     * (the quality of the community structure) is
     * @return the quality of the community partition.
     */
    public double calcTrace() {
        double Q=0;
        for (int s = 0; s < numberEdges; s++) Q += get(s,s);
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
       nullModel.setNumber(newNullModelSwitch);
    }

    /**
     * Gives nullModelSwitch, the value used to switch between  null models.
     * @return null model switch
     */
    public int getNullModelSwitch(){return nullModel.getNumber();}
    /**
     * Gets description of null model
     */
    public String getNullModelDescription(){return nullModel.getDescription();}

        
    /**
     * Gets description of quality type;
     */
    public String getQualityTypeDescription(){return qualityType.toLongString();}

    /**
     * Sets qualityType;
     */
    protected void setQualityType(){qualityType = basicType;}

    /**
     * Gets number of quality type;
     */
    public int getQualityTypeNumber(){return qualityType.getNumber();}
        
    
    public String Qdefinition(String sep) {
        if (lambda == 1) {
            return QualitySparse.QdefinitionString[Qdefinition];
        }
        return QualitySparse.QdefinitionString[Qdefinition] +sep+getNullModelDescription() + sep+"lambda="+lambda;
    }

    public String Qdefinition() {return Qdefinition("_");
    }

    public String QdefinitionShortString() {
        long l = Math.round(lambda * 1000);
        return QualitySparse.QdefinitionShortString[Qdefinition]+getNullModelDescription()+l;}
    
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
            for (int j=0; j<numberEdges;j++) PS.print(String.format(intFormat, j)+sep);
            PS.println();
        }
        for (int i=0; i<numberEdges;i++)
        {
            if (headersOn) PS.print(String.format(intFormat, i)+sep);
            for (int j=0; j<numberEdges; j++) PS.print(String.format(doubleFormat,get(i,j))+sep);
            PS.println();
        }
    }//eo printMatrix

}
