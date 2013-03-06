/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import java.awt.GridLayout;
import java.io.PrintStream;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Stores, outputsand calculates global islandNetwork properties.
 * @author time
 */
public class GlobalProperties {
    
    static final double DUNSET=-1.3579e24;
    private double [] value;
    
    static final int UNIQUENAMELENGTH=6;

    /**
     * Static array of short names used to indicate modes
     */
    static final public String [] name = {
        "Energy",
        "TEWeight",
        "InSSEntropy",
        "OutSSEntropy",
        "EWEntropy",
        "EWMInformation",
        "EWMIDistance"}; 
    
    /**
     * Static array of long names used to describe modes
     */
    static final String [] longName = {
        "Energy",
        "Total Edge Weight",
        "In Site Strength Entropy",
        "Out Site Strength Entropy",
        "Edge Weight Entropy",
        "Edge Weight Mutual Information ",
        "Edge Weight M.I. Distance"};  //= {"general information","pajek files"};
    

    static final int EnergINDEX=getIndex("Energy");
    static final int TEWeiINDEX=getIndex("TEWeight");
    static final int InSSEINDEX=getIndex("InSSEntropy");
    static final int OutSSINDEX=getIndex("OutSSEntropy");
    static final int EWEntINDEX=getIndex("EWEntropy");
    static final int EWMInINDEX=getIndex("EWMInformation");
    static final int EWMIDINDEX=getIndex("EWMIDistance");
    
    public GlobalProperties(){initialise();
        }
    
    /**
     * Deep copy constructor.
     * @param old a GlobalProperties object to be deep copied.
     */
    public GlobalProperties(GlobalProperties old){
        initialise();
        for (int i=0; i<name.length; i++) value[i]=old.getValue(i);
    }
    
    private void initialise(){
    value = new double[name.length];
        for (int i=0; i<name.length; i++) value[i]=DUNSET;
    }
    
    
    public double getValue(int i){
        return value[i];
    }
    
    /**
     * Returns string for value requested
     * @param i index of value as given by names[] array.
     * @return formated string of value or UNSET if not yet set.
     */
    public String getValueString(int i){
        double v=getValue(i);
        if (v==DUNSET) return "UNSET";
        return String.format("%12.6g",v);
    }
    
    public double getEnergy(){return value[GlobalProperties.EnergINDEX];}
    //public void setEnergy(double e){energy=e;}
    
    public double getTotalEdgeWeight(){return value[GlobalProperties.TEWeiINDEX];}
    public double getInSiteStrengthEntropy(){return value[GlobalProperties.InSSEINDEX];}
    public double getOutSiteStrengthEntropy(){return value[GlobalProperties.OutSSINDEX];}
    public double getMutualInformation(){return value[GlobalProperties.EWMInINDEX];}
    public double getMutualInformationDistance(){return value[GlobalProperties.EWMIDINDEX];}

    
    /**
     * Sets all values.
     * @param inet island network 
     */
    public void calculateAll(islandNetwork inet)
    {
        calcEnergy(inet);
        calcEntropies(inet);
     }
    /**
     * Sets all values.
     * @param inet island network 
     * @deprecate needed only for development
     */
    public void calculateAll(NewIslandNetwork inet)
    {
//        calcEnergy(inet);
//        calcEntropies(inet);
        System.exit(1);
     }
        /**
     * Sets mutual information and in/out site strength entropy.
     * <p>Assumes edge weights and site strengths already calculated.
     * @deprecate needed only for development
     * @param inet island network 
     */
    public void calcEntropies(NewIslandNetwork inet)
    {
        System.exit(1);
    }
    /**
     * Sets energy to be current value of Hamiltonian.
     * @param inet island network 
     * @deprecate needed only for development
     */
    public void calcEnergy(NewIslandNetwork inet)
    {System.exit(1);
    }
    /**
     * Sets energy to be current value of Hamiltonian.
     * @param inet island network 
     */
    public void calcEnergy(islandNetwork inet)
    {
      double energy=0;
      double supply = 1;
      double demand = 1;
      double sourceWeight =-1;
       switch (inet.modelNumber.major) 
        { 
           case 1:   // Model 1
               for (int s = 0; s < inet.numberSites; s++) {
                   sourceWeight = inet.siteSet.getWeight(s);
                   supply = (inet.modelNumber.bit0 ? (sourceWeight) : 1);
                   energy += inet.Hamiltonian.vertexSource * sourceWeight;
                   energy -= inet.Hamiltonian.vertexPotential1(inet.siteSet.getSize(s), inet.siteSet.getValue(s));
                   for (int t = 0; t < inet.numberSites; t++) {
                       demand = (inet.modelNumber.bit1 ? inet.siteSet.getWeight(t) : 1);
                       energy -= inet.edgeSet.getEdgePotential1(s, t) * inet.edgeSet.getEdgeValue(s, t) *demand*supply; // comes in with negative sign
                       energy += inet.Hamiltonian.edgeSource * sourceWeight * inet.edgeSet.getEdgeValue(s, t);
                   } // eo for t           
                } // eo for s
                
                break;
       } // eo switch
       value[GlobalProperties.EnergINDEX]=energy;
    }

    
    /**
     * Sets mutual information and in/out site strength entropy.
     * <p>Assumes edge weights and site strengths already calculated.
     * @param inet island network 
     */
    public void calcEntropies(islandNetwork inet)
    {
        double mutualInformation=0;
        double totalEdgeWeight=0;
        double inSiteStrengthEntropy=0;
        double outSiteStrengthEntropy=0;
        double edgeWeightEntropy=0;
        for (int s=0; s<inet.numberSites;s++){
            totalEdgeWeight+= inet.siteSet.getStrengthIn(s);
        }
        value[GlobalProperties.TEWeiINDEX]=totalEdgeWeight;
        double ps=-1;
        double pt=-1;
        if (totalEdgeWeight>1e-10){ 
        for (int s=0; s<inet.numberSites;s++){
            ps = inet.siteSet.getStrengthOut(s)/totalEdgeWeight;
            if (ps>1e-10) outSiteStrengthEntropy-= (ps*Math.log(ps));
            for (int t=0; t<inet.numberSites;t++){
                pt= inet.siteSet.getStrengthIn(t)/totalEdgeWeight;
                if (pt>1e-10) inSiteStrengthEntropy -= (pt*Math.log(pt)); 
                double few=inet.edgeSet.getEdgeWeight(s, t)/totalEdgeWeight;
                if (few>1e-10) edgeWeightEntropy-=(few*Math.log(few));
                if ((few<1e-10) || (ps<1e-10) ||(pt<1e-10)) continue;
                mutualInformation+=few*Math.log(few/(ps*pt));
            }
        }
        } // eo if totalEdgeWeight
        double miDistance=0;
//        double max = Math.max(inSiteStrengthEntropy,outSiteStrengthEntropy);
//        if (max>1e-10) miDistance = 1- mutualInformation/max;
        if (edgeWeightEntropy>1e-10) miDistance = 1- mutualInformation/edgeWeightEntropy;
        value[GlobalProperties.InSSEINDEX]=inSiteStrengthEntropy;
        value[GlobalProperties.OutSSINDEX]=outSiteStrengthEntropy;
        value[GlobalProperties.EWEntINDEX]=edgeWeightEntropy;
        value[GlobalProperties.EWMInINDEX]=mutualInformation;
        value[GlobalProperties.EWMIDINDEX]=miDistance;
    }

       /** Returns index from short name given.
         * <br>Compares the first <code>uniqueNameLength</code> length characters of input string
         * against the name array
         * @param input short name of variable being requested
         * @return variable number, -1 if none found.
         */
        static public int getIndex(String input)
        {
            String s=input.substring(0,UNIQUENAMELENGTH);
            for (int v=0; v<name.length;v++) 
              {
                  if (name[v].startsWith(s)) return v;
              }
            return -1;
        }

    
    /**
     * Prints out list of values.
     * @param PS PrintStream such as System.out
     * @param sep separation string such as a tab
     */
    public void print(PrintStream PS, String sep){
       for (int i=0; i<name.length; i++) PS.println(longName[i]+sep+getValueString(i) ); 
    }
    
   public JPanel getPanel(){      
    JPanel globalBox= new JPanel();
    globalBox.setLayout( new GridLayout(name.length+1,2));
     
    globalBox.add(new JLabel("Quantity:",JLabel.RIGHT));
    globalBox.add(new JLabel("Value",JLabel.CENTER));
    
    for (int i=0; i<name.length; i++) {
     globalBox.add(new JLabel(longName[i]+":",JLabel.RIGHT));
     globalBox.add(new JLabel(getValueString(i),JLabel.CENTER));
     }   
     
     return globalBox;
   }
    
}
