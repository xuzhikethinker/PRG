/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.timgraph;
//import java.io.PrintStream;
 

/**
 * Makes minimal use of memory.
 * <br>The adjacency matrix is not calculated so the strength of each
 * vertex pair is found explicitly every time which can be slow but works
 * in all cases.
 * Does not work with directed graphs as can not calculate the pi Vector
 * (page rank vector).
 * If this is not true then look at {@link QualitySparse}.
 * @author time
 */
public class QualityMinimalMemory extends Quality {
    
   final static QualityType  minimalMemoryType = new QualityType("MM");
    
    

    
    timgraph graph;
//    double totalWeight;
    
    /**
     * Defines simple quality (Newman basic modularity) from given graph.
     * @param graph timgraph defining the graph. 
     * @param qdef selects modularity definition to use
     * @param newlambda scaling factor for null model in quality function
     */
    public QualityMinimalMemory(timgraph graph, int qdef, double newlambda, int infoLevelNew){
//        infoLevel= infoLevelNew;
//        this.graph=graph;
//        Qdefinition =qdef;
//        setQualityType();
//        setNullModelScaling(newlambda);
        this.graph=graph;
        initialisePreAdjacencyMatrix(graph, qdef, newlambda, infoLevelNew);
        numberVertices= graph.getNumberVertices();
//        totalWeight=0;
//        for (int v=0; v<numberVertices; v++) totalWeight+=graph.getVertexOutStrength(v);

    }

    /**
     * Initalises various factors.
     * <br>No need to call here.
     * @param graph timgraph defining the graph. 
     */
    @Override
    public void initialiseAdjacencyMatrix(timgraph graph) {
    }
    
    
    
    /**
     * Gets the quality contribution for the edge from vertex s to t
     * @param s source vertex
     * @param t target vertex
     * @return the quality matrix entry
     */
    @Override
    public double get(int s, int t){
        if (this.Qdefinition==Quality.QSindex) {
        //double adjacencyMatrixEntry=graph.getAdjacencyMatrixEntry(s, t);
        double nullModelNorm = (nullModelSwitch==4?1:graph.getTotalWeight()*graph.getTotalWeight());
        double nullModelValue=-1.0;
        switch (nullModelSwitch){
                    case 4:
                        //nullModelValue = AMatrix.getNormalisedPi(s) * AMatrix.getNormalisedPi(t);
                        throw new RuntimeException("No Pi vector calculation available in QualityMinimalMemory");
                        
                    case 3:
                        nullModelValue = graph.getVertexInStrength(s) * graph.getVertexInStrength(t)/nullModelNorm;
                        break;
                    case 2:
                        nullModelValue = graph.getVertexInStrength(s) * graph.getVertexInStrength(t)/nullModelNorm;
                        break;
                    case 1:
                        nullModelValue = graph.getVertexOutStrength(s) * graph.getVertexOutStrength(t)/nullModelNorm;
                        break;
                    case 0:
                    default:
                        nullModelValue = graph.getVertexOutStrength(s) * graph.getVertexOutStrength(t)/nullModelNorm;
                        break;
                }
                return ((graph.getAdjacencyMatrixEntry(s, t)/graph.getTotalWeight()) - (lambda*nullModelValue) );
        }
            
        throw new RuntimeException("*** ERROR QualityMinimalMemory not defined for "+Quality.QdefinitionString[Qdefinition]);
    }

   /**
     * Sets qualityType;
     */
    @Override
    protected void setQualityType(){qualityType=minimalMemoryType;}

    
}
