/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.timgraph;
import TimUtilities.GeneralMode;

/**
 *
 * @author time
 */
public class QualityType extends GeneralMode {
    
        /**
     * Static array of short names used to indicate modes
     */
    final static public String [] qualityName={"DM","SM","MM"}; // = {"info","pajek"};
    /**
     * Static array of long names used to describe modes
     */
    final static public String []qualityLongName ={
    "Basic Dense Matrix",
    "Sparse Matrix",
    "Minimal Memory"};  //= {"general information","pajek files"};


    public QualityType(String name){
        setUniqueNameLength(2);
        setUp( qualityName, qualityLongName, name);
    } 
    
/**
 * Initialises features related to an input graph.
 * @param tg timgraph whose vertex partition is given
 * @param qdef selects modularity definition to use
 * @param qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory   
 * @param newlambda scaling factor for null model in quality function  
 */
    static Quality makeQuality(timgraph tg, int qdef, int qualityType, double newlambda, int infoLevel){
            switch (qualityType){
            case 1: return new QualitySparse(tg, qdef, newlambda, infoLevel);
            case 2: return new QualityMinimalMemory(tg, qdef, newlambda, infoLevel);
            default: return new Quality(tg, qdef, newlambda, infoLevel);
            }
    }
    
}
 
