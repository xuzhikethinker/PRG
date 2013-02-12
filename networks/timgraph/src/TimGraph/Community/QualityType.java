/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.timgraph;
import TimUtilities.GeneralMode;

/**
 * Describes the quality class being used.
 * The types are given in <tt>qualityLongName</tt> and their abbreviations in 
 * <tt>qualityName</tt>
 * <ul>
 * <li>0. DM = Basic Dense Matrix (simple inefficient but should always work)</li>
 * <li>1. SM = Sparse Matrix (best for directed)</li>
 * <li>2. MM = Minimal Memory (for not directed but best otherwise)</li>
 * </ul
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
 * @param nullModel index  of null model to use
 * @param qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory   
 * @param newlambda scaling factor for null model in quality function  
 * @see TimGraph.Community.Quality#nullModelSwitch
 */
    static Quality makeQuality(timgraph tg, int qdef, int nullModel, int qualityType, 
            double newlambda, int infoLevel){
            switch (qualityType){
            case 1: return new QualitySparse(tg, qdef, nullModel, newlambda, infoLevel);
            case 2: return new QualityMinimalMemory(tg, qdef, nullModel, newlambda, infoLevel);
            default: return new Quality(tg, qdef, nullModel, newlambda, infoLevel);
            }
    }
    
}
 
