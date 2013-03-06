/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.AdjacencyMatrix;
import TimGraph.timgraph;
//import java.io.PrintStream;
 

/**
 * General class for measures of community quality or modularity.
 * <br>Can see no difference from {@link QualitySparse}
 * @deprecated use {@link QualitySparse}???
 * @author time
 */
public class QualityEquilibrium extends Quality {
    
    final static QualityType  sparseType = new QualityType("SM");
    
    
    
    /**
     * The adjacency matrix for the graph.
     * <br>The quality matrix is not used but elements are constructed as needed 
     * using the sparse adjacency matrix.
     */
    AdjacencyMatrix AMatrix;

    /**
     * Defines simple quality (Newman basic modularity) from given graph.
     * @param graph timgraph defining the graph. 
     * @param qdef selects modularity definition to use
     * @param newlambda scaling factor for null model in quality function
     */
    public QualityEquilibrium(timgraph graph, int qdef, double newlambda, int infoLevelNew){
        infoLevel= infoLevelNew;
        setQualityType();
        Qdefinition =qdef;
        setNullModelScaling(newlambda);
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
        if (this.infoLevel>0) System.out.println("Total Weight = "+AMatrix.totalWeight());
    }
    
    
    
//    /**
//     * Gets the quality contribution for the edge from vertex s to t
//     * @param s source vertex
//     * @param t target vertex
//     * @return the quality matrix entry
//     */
//    @Override
//    public double get(int s, int t){
//        double nullValue=AMatrix.getInStrength(s) /AMatrix.totalWeight();
//        if (inin) nullValue *= AMatrix.getInStrength(t) ;
//             else nullValue *= AMatrix.getOutStrength(t);
//        return (AMatrix.get(s, t) - lambda*nullValue)/AMatrix.totalWeight() ;
//    }

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
        return (AMatrix.get(s, t)/AMatrix.totalWeight() - lambda*nullModelValue)/AMatrix.totalWeight() ;
    }


   /**
     * Sets qualityTypeString;
     */
    @Override
    protected void setQualityType(){qualityType=sparseType;}

}
