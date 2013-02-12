/*
 * ModelParameters.java
 *
 * Created on 26 July 2006, 18:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks.Model;

import java.io.PrintStream;

/**
 * Characteristics of the Model Parameters for Island Networks.
 * @author time
 */
public class ModelParameters {
    
        double distanceScale ;
        
    
    /** Creates a new instance of ModelParameters. */
    public ModelParameters() {
             distanceScale = 100;
    }
    
    /**
     * Deep copies ModelParameters.
     *@param H Island Model Parameters to be copied
     */
    public ModelParameters(ModelParameters H) {
             distanceScale = H.distanceScale;
    }
    
    
    
       /** Sets Parameters of Model Parameters.
         * @param ds scale for edge potential (distanceScale)
         */
    public void setParameters(double ds) {
             distanceScale = ds;
    }//eo setHam    
    
        /** 
         * Shows Parameters of Model Parameters on a PrintStream.
         *@param PS a print stream such as System.out
         *@param SepString string used as a separator such as tab or space
         */
    public void printParameters(PrintStream PS, String SepString) {
             PS.println("          distScale "+SepString+distanceScale);
             
    }//eo showHam
    
        /** 
         * Shows parameters of Mode lParameters on a PrintStream suitable for a data file.
         *@param PS a print stream such as System.out
         *@param SepString string used as a separator such as tab or space
         */
    public void printParametersForData(PrintStream PS, String SepString) {
             PS.println("distScale"+SepString+distanceScale);
    }//eo showHam
 
    
        /** 
         * Gives Mode lParameters values as string suitable for Parse routine.
         *@param SepString string used as a separator such as tab or space
         */
    public String inputParametersString(String SepString) {
             String s="";
             s=s+SepString+"-dl"+distanceScale;
             return s;
    }//eo inputParametersString
 
        /** 
         * Gives Model Parameters values as string suitable for Parse routine or for use in simple output.
         *@param firstItem string for first item in list.  If null or empty string then first separator is missed out. 
         *@param SepString string used as a separator such as tab or space
         *@param dec number of decimal points to use
         */
    public String inputParametersString(String firstItem, String SepString, int dec) {
             String dformat = "%f"+(dec+8)+"."+dec;
             String s="";
             if (firstItem.length()>0) {s=firstItem+SepString;}
             s=s+SepString+"-dl"+String.format(dformat,distanceScale);
             return s;
    }//eo inputParametersString
 

       /**
         * Gives string representing Model Parameters parameters.
         *<br> Useful for file names etc.
         *@param SepString string used as a separator such as tab or space
         * @see #parameterString(java.lang.String, int)
         */
    public String parameterString(String SepString)
    {
        String s="D"+this.distanceScale;
        return s;
    }
        /**
         * Gives formated string representing Mode lParameters parameters.
         *<br> Useful for file names etc.  Leading zeros and minimum width
         * three longer than number of decimal places.
         *@param SepString string used as a separator such as tab or space
         *@param dp number of decimal places
         * @see #parameterString(java.lang.String)
         */
    public String parameterString(String SepString, int dp)
    {  
        String fs ="%0"+(dp+3)+"."+dp+"g";
        String s="D"+String.format(fs, this.distanceScale);
        return s;
    }
    
    /**
     * Gives the potential for model 1 between two site variables, modified by short distance and lambda.
     * <p>The potential times lambda times a linear weighting,
     * i.e. <tt>w_{ij} \lambda V_1(d_{ij}/d_s)</tt> where
     * <tt>W_{ij}</tt> is the linear weighting for the edge.
     * @param distance the distance between two sites
     * @param linearWeighting a weighting factor
     * @return returns the (edge potential for model 1)* lambda * linearWeighting .
     */
    public double edgePotential1(double distance, double linearWeighting) 
    {
    return(  (distance>this.distanceScale?0:1) );
    } 

}
