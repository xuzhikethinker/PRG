/*
 * Correlation.java
 *
 * Created on 17 December 2006, 17:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import java.io.*;


/**
 * Correlation class.
 * Given a vector of artifacts for each individuals (the artifacts may be the sites)
 * it will calculate various correlation measures.
 * @author time
 */
public class Correlation {
    int dimInd =-1;
    int dimArt =-1;
    final int dimType = 2;
    double [][] valueVector; //  valueVector[dimInd][dimArt] are the values of individual i for artifact j
    double [][] valueNorm; //  valueNorm[i][n] normalisation of individual vectors i using normalisation type n
                           // n=0 normalisation by sum of components
                           // n=1 normalisation by square root of sum of squares components    
    boolean correlationCalculated=false;
    double [][][] correlationVector; //  correlationVector[dimInd][dimInd][dimType] 
                                     // [i][j][n] is a correlation of entry i with entry j of type n
    
    /** Creates a new instance of Correlation.
     *Makes a deep copy of input values.
     *@param dimIndividual number of individuals
     *@param dimArtifact number of artifacts
     *@param valueVector array of double [dimInd][dimArt] with values.
     */
    public Correlation(int dimIndividual, int dimArtifact, double [][] valueVector) {
        dimInd = dimIndividual;
        dimArt = dimArtifact;
        this.valueVector = new double[dimInd][dimArt];
        correlationVector = new double[dimInd][dimInd][dimType];    
        valueNorm = new double [dimInd][dimType];
        double v=-1;
        for (int i=0; i<dimInd; i++)
        {
            for (int n=0; n<dimInd; n++) valueNorm[i][n]=0;
            for (int a=0; a<dimArt; a++) 
            {
                v= valueVector[i][a];
                this.valueVector[i][a] = v;
                valueNorm[i][0]+=v;
                valueNorm[i][1]+=v*v;
            }
            valueNorm[i][1]=Math.sqrt(valueNorm[i][1]);            
            for (int j=0; j<dimInd; j++)  {
                for (int n=0; n<dimInd; n++) if (i==j) correlationVector[i][j][n]=1;
                else correlationVector[i][j][n]=0;
            }
 
        } // eo for i
        correlationCalculated=false;     
    }
    
    /**
     * Returns artifact correlations, caluclates them if needed.
     * @param i first individual
     * @param j second individual
     * @param n type of correlation to use
     *@return dot product of normalised culture vectors 
     */
    public double getCorrelation(int i, int j, int n)
    {  
        if (!correlationCalculated) calcCorrelations();
        return correlationVector[i][j][n];
    }

    /**
     * Calculates artifact correlations for two sites
     */
    public void calcCorrelations() {
        double c=-99;
        for (int i=0; i<dimInd; i++)
        {
          for (int j=0; j<dimInd; j++)
          {
              c=calcCorrelation(i, j);
              for (int n=0; n<dimInd; n++) 
              {
                  switch (n) {
                      case 0: correlationVector[i][j][0] = c/(valueNorm[i][0]*valueNorm[j][0]); break;
                      case 1: correlationVector[i][j][1] = Math.acos(c/(valueNorm[i][1]*valueNorm[j][1]) ); break;
                      default: // ERROR
                  } //eo switch
              }
          }             
        }
        
    }

    
    /**
     * Calculates artifact correlations for two sites
     *@param PS a PrintStream such as System.out
     */
    public String correlationTypeString(PrintStream PS) {
        String s="Unknown type";
    for (int n=0; n<dimInd; n++) 
              {
                  switch (n) {
                      case 0:  s="Probability style"; break;
                      case 1:  s="Angle in artifact space"; break;
                      default: // ERROR
                  } //eo switch
              }
        return s;
    }
    
        /**
     * Calculates unnormalised artifact correlations for two sites
     *@param i first individual
     *@param j second individual
     */
    private double calcCorrelation(int i, int j) {
        double c=0;
        for (int a=0; a<dimArt; a++) c+= valueVector[i][a]*valueVector[j][a];
        return c;
    }

//    c=c/(valueNorm1[i]*valueNorm1[j]);
//        correlationCalculated=true;
    
}
