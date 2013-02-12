/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Vertex;

import java.util.ArrayList;
/**
 * Describes the features which characterise each vertex
 * @author time
 */
public class VertexGenes {
    ArrayList<String> name;
//    String [] name;
    int number;
    
    /**
     * Set number of distinct genes.
     * @param n number of genes
     */
    VertexGenes(int n){
        number=n;
        name = new ArrayList<String>(n) ;
    }
    
    /**
     * Sets the name of a gene
     * @param g number of gene
     * @param geneName name of gene
     */
    public void setName(int g, String geneName ){name.set(g, geneName);}

    /**
     * Sets the name of a gene.
     * @param geneName name of gene
     */
    public int addGene(String geneName ){name.add(geneName); return name.size();}

    /**
     * Gets the name of a gene
     * @param g number of gene
     * @return name of gene
     */
    public String getName(int g){return name.get(g);}

    /**
     * Gets the number of a gene
     * @param geneName name of gene
     * @return number of gene, negative if not found
     */
    public int getName(String geneName){
        for (int g=0; g<number; g++) if (name.get(g).startsWith(geneName)) return g;
        return -1;}

}
