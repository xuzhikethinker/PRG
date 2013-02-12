/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.AdjacencyMatrix;
import TimGraph.timgraph;
//import java.io.PrintStream;
 

/**
 * Quality defined with no predefined quality matrix.
 * <p>The null model contribution to the quality entries
 * are calculated each time from a product of vectors.
 * <p>Here an adjacency matrix is calculated.  However the null matrix is left
 * in terms as vectors to save memory.
 * Seems little point unless we need the pi vectors for directed graphs.
 * May as well have the whole quality matrix as a sparse matrix
 * so use the plain {@link Quality} class.
 * If timgraph has no multiple edges and it is undirected 
 * then look at {@link QualityMinimalMemory}.
 * <p>Works with all types of graph as it will use the pi Vector (page rank vector)
 * when working with the modularity of directed graphs.
 * @author time
 */
public class QualitySparse extends Quality {
    
    final static QualityType  sparseType = new QualityType("SM");
    
    
    
    /**
     * The adjacency matrix for the graph.
     * <br>The null model and so the quality matrix is not explicitly constructed to
     * reduce memory usage but the elements are constructed as needed.
     * The adjacency matrix is calculated however which allows for the Pi
     * vectors to be found.
     */
    AdjacencyMatrix AMatrix;

    /**
     * Defines simple quality (Newman basic modularity) from given graph.
     * @param graph timgraph defining the graph. 
     * @param qdef selects modularity definition to use
     * @param nullModel index  of null model to use
     * @param newlambda scaling factor for null model in quality function
     * @see TimGraph.Community.Quality#nullModelSwitch
     */
    public QualitySparse(timgraph graph, int qdef, int nullModel, double newlambda, int infoLevelNew){
        initialisePreAdjacencyMatrix(graph, qdef, nullModel, newlambda, infoLevelNew);
        initialiseAdjacencyMatrix(graph);
        if (infoLevel>0) AMatrix.check();
        if (infoLevel>0) AMatrix.printMatrix(" ",true);
        if (infoLevel>0) System.out.println("Check Quality Matrix = "+check());
    }

    /**
     * Initialise Adjacency matrix using sparse matrix storage and simplest (Newman) definition.
     * @param graph timgraph defining the graph. 
     */
    @Override
    public void initialiseAdjacencyMatrix(timgraph graph) {
        AMatrix = new AdjacencyMatrix(graph, Qdefinition); 
       //AMatrix = new AdjacencyMatrix(graph, Qdefinition); 
        //AMatrix.printMatrix(" ", true);
        numberVertices= AMatrix.dimension();
        AMatrix.calculateInOutVectors();
        if (nullModelSwitch==4) AMatrix.calculatePiVector();
        if (this.infoLevel>0) System.out.println("Total Weight = "+AMatrix.totalWeight());
    }
    
    
    
    /**
     * Gets the quality contribution for the edge from vertex s to t.
     * @param s source vertex
     * @param t target vertex
     * @return the quality matrix entry
     */
    @Override
    public double get(int s, int t){
        double nullModelValue=-1;
        double nullModelNorm = (nullModelSwitch==4?1:AMatrix.totalWeight()*AMatrix.totalWeight());
        switch (nullModelSwitch){
                    case 5:
                        nullModelValue = 1.0/AMatrix.totalWeight();// nullModelNorm;
                        break;
                    case 4:
                        nullModelValue = AMatrix.getNormalisedPi(s) * AMatrix.getNormalisedPi(t);
                        break;
                    case 3:
                        nullModelValue = AMatrix.getInStrength(s) * AMatrix.getInStrength(t)/nullModelNorm;
                        break;
                    case 2:
                        nullModelValue = AMatrix.getOutStrength(s) * AMatrix.getInStrength(t)/nullModelNorm;
                        break;
                    case 1:
                        nullModelValue = AMatrix.getInStrength(s) * AMatrix.getOutStrength(t)/nullModelNorm;
                        break;
                    case 0:
                    default:
                        nullModelValue = AMatrix.getOutStrength(s) * AMatrix.getOutStrength(t)/nullModelNorm;
                        break;
                }        
        return (AMatrix.get(s, t)/AMatrix.totalWeight() - lambda*nullModelValue);
    }

   /**
     * Sets qualityTypeString;
     */
    @Override
    protected void setQualityType(){qualityType=sparseType;}

}
