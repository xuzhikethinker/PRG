/*
 * IslandIslandParameterBox.java
 *
 * Created on 23 August 2006, 12:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import IslandNetworks.Edge.IslandEdge;
import TimUtilities.NumbersToString;
import TimUtilities.StatisticalQuantity;


import javax.swing.Box;          
//import javax.swing.BoxLayout;          
import javax.swing.JLabel;          
import javax.swing.JPanel;          
import java.awt.GridLayout;          

/**
 * Defines JPanels which describe input and output parameters.
 * Makes a JFrames or Boxes with parameter information on an island network.
 * @author time
 */
public class IslandParameterWindow{
    private islandNetwork islnet;
    
// Constructor
 public IslandParameterWindow(islandNetwork inputIslandNetwork)
 {
     islnet = new islandNetwork(inputIslandNetwork);
 }
 
 /**
  * Returns a Box with parameter information
  *@return a Box ready to be added to panels etc.
  */
 public Box getParameterBox()
 {
     Box pb = Box.createVerticalBox();
     pb.add(getBasicSettingsPanel());
     pb.add(getInputVariablesPanel());
     return pb;
 }
 
 /**
  * Returns a JPanel with parameter information
  *@return a Box ready to be added to panels etc.
  */
 public JPanel getParameterPanel()
 {
     JPanel pb = new JPanel();
     pb.add(getBasicSettingsPanel());
     pb.add(getInputVariablesPanel());
     return pb;
 }
 
 /**
  * Returns a JPanel with basic programme settings given.
  *@return a JPanel ready to be added to panels etc.
  */
 public JPanel getBasicSettingsPanel()
 {
        JPanel topBox= new JPanel();
        topBox.setLayout( new GridLayout(7,2));
        addValuePair(topBox,"Input file : ",islnet.inputFile.getNameRoot());
        addValuePair(topBox,"Output files : ",islnet.outputFile.getNameRoot());
        addValuePair(topBox,"Model : ",islnet.modelNumber.major+"_"+islnet.modelNumber.minor+",  "+islnet.modelNumber.majorString +"+"+islnet.modelNumber.minorString);
        String s=islnet.updateMode.toLongString(); 
        if ( islnet.updateMode.isCurrentMode("MC") ) s=s+", "+(islnet.monteCarloStartMode.toLongString());
        s=s+" ("+islnet.updateMode.getNumber()+")";
        addValuePair(topBox,"Calculational Mode : ",s);
        addValuePair(topBox,"Edge Update Mode : ",islnet.edgeSet.edgeMode.description());
        addValuePair(topBox,"Vertex Update Mode : ",islnet.vertexMode.descriptionValue(", value="));
        return topBox;
 }
 
 /**
  * Returns a JPanel with input parameter values.
  *@return a JPanel ready to be added to panels etc.
  */
 public JPanel getInputVariablesPanel()
 {
        JPanel inputBox = new JPanel();
        inputBox.setLayout( new GridLayout(5,4));
        addValuePair(inputBox,"kappa",islnet.Hamiltonian.kappa);
        addValuePair(inputBox,"lambda",islnet.Hamiltonian.lambda);        
        addValuePair(inputBox,"j",islnet.Hamiltonian.vertexSource);
        addValuePair(inputBox,"mu",islnet.Hamiltonian.edgeSource);
        addValuePair(inputBox,"distance scale",islnet.Hamiltonian.distanceScale);        
        addValuePair(inputBox,"short distance scale",islnet.Hamiltonian.shortDistanceScale);        
        addValuePair(inputBox,"b",islnet.Hamiltonian.getb());        
        //addValuePair(inputBox, islnet.vertexMode.description(), islnet.vertexMode.getValue());
        addValuePair(inputBox,"metric number ",islnet.Hamiltonian.shortDistanceScale);        
        addValuePair(inputBox,"initial beta ",islnet.betaInitial);        
        JPanel jp = new JPanel(); // use default flow layout to avoud spaces
        jp.add(inputBox);
        return jp;
}//eo 


  /**
  * Returns a JPanel with output statistics information.
  *@param decPoints number of decimal points to show
  *@return a JPanel ready to be added to panels etc.
  */
 public JPanel getOutputMeasuresPanel(int decPoints )
 {
     NumbersToString n2s = new NumbersToString(decPoints);
     JPanel infoBox= new JPanel();
     infoBox.setLayout( new GridLayout(7,5));
//        infoBox.add(new JLabel(" Maximum Site Weight  "+TruncDec(islnet.maxSiteWeight)));
        //maxSiteWeight is the largest vertex weight, 
//        infoBox.add(new JLabel(" Maximum Edge Weight  "+TruncDec(islnet.allmaxedgeweight )));
//        infoBox.add(new JLabel(" Max. Site Out Strength  "+TruncDec(islnet.maxOutSiteStrength)));
     infoBox.setToolTipText("These are different measures of the output for this network.");
// Header line
        infoBox.add(new JLabel("Quantity/Value :",JLabel.RIGHT));
        infoBox.add(new JLabel("Average",JLabel.CENTER));
        infoBox.add(new JLabel("Sigma",JLabel.CENTER));
        infoBox.add(new JLabel("Minimum",JLabel.CENTER));
        infoBox.add(new JLabel("Maximum",JLabel.CENTER));
        //
        infoBox.add(makeRightLabelWithToolTip("Site Value :","Fractional population of one site relative to sites fixed value (v_i)"));
        addStatsValues(infoBox,  "Site value", islnet.siteSet.siteValueStats, decPoints);
        //
        infoBox.add(makeRightLabelWithToolTip("Site Weight :","Population of one site, fixed value * variable fraction (S_i v_i)"));
        addStatsValues(infoBox,  "Site weight", islnet.siteSet.siteWeightStats, decPoints);
        //
        infoBox.add(makeRightLabelWithToolTip("Site Out W.Strength :","Total outgoing trade from one site (sum_j S_i v_i e_(ij) )"));
        addStatsValues(infoBox, "Site out w.strength", islnet.siteSet.siteStrengthOutStats, decPoints);
        //
        infoBox.add(makeRightLabelWithToolTip("Site In W.Strength :","Total incoming trade from one site (sum_j S_j v_j e_(ij) )"));
        addStatsValues(infoBox, "Site in w.strength", islnet.siteSet.siteStrengthInStats, decPoints);
//        infoBox.add(new JLabel(n2s.toString(islnet.siteStrengthInStats.getAverage() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.siteStrengthInStats.getSigma() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.siteStrengthInStats.minimum ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.siteStrengthInStats.maximum ),JLabel.CENTER));
        //
        infoBox.add(makeRightLabelWithToolTip("Edge Weight :","Total trade along one edge, (S_i v_i e_(ij) )"));
        addStatsValues(infoBox, "Edge weight", islnet.edgeSet.edgeStats[IslandEdge.weightINDEX], decPoints);
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeWeightStats.getAverage() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeWeightStats.getSigma() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeWeightStats.minimum ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeWeightStats.maximum ),JLabel.CENTER));
        //
        infoBox.add(makeRightLabelWithToolTip("Edge Value :", "Fraction of trade along one edge, e_(ij) "));
        addStatsValues(infoBox, "Edge value", islnet.edgeSet.edgeStats[IslandEdge.valueINDEX], decPoints);
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeValueStats.getAverage() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeValueStats.getSigma() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeValueStats.minimum ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeValueStats.maximum ),JLabel.CENTER));

        
        
        
        JPanel infoPanel = new JPanel();
//        infoPanel.setLayout(new BoxLayout)
        //infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        infoPanel.add(infoBox);
        infoPanel.add(this.islnet.globalProperties.getPanel());
        return infoPanel;
 } // eo class get
 
 /**
  * Adds values from a statistical quantity to a JLabel.
  * @param label values are added to this JLabel 
  * @param s string describing the quantity
  * @param stats the StatisticalQuantity
  * @param decPoints no. of decimal points to display results to.
  */
private void addStatsValues(JPanel label, String s, StatisticalQuantity stats, int decPoints){
    NumbersToString n2s = new NumbersToString(decPoints);
        label.add(makeCenterLabelWithToolTip(n2s.toString(stats.getAverage() ),s+" average"));
        label.add(makeCenterLabelWithToolTip(n2s.toString(stats.getSigma() ),s+" sigma"));
        label.add(makeCenterLabelWithToolTip(n2s.toString(stats.minimum ),s+" min"));
        label.add(makeCenterLabelWithToolTip(n2s.toString(stats.maximum ),s+" max"));
}

private void addValuePair(JPanel label, String s, double value){
    label.add(new JLabel(s,JLabel.RIGHT));
    String sv=": "+value;
    label.add(new JLabel(sv,JLabel.LEFT));
}

private void addValuePair(JPanel label, String s, double value, int dec){
    label.add(new JLabel(s,JLabel.RIGHT));
    String fstring="%12.6g";
    if (dec>0) fstring ="%"+(dec+6)+"."+dec;
    String sv=": "+String.format(fstring, value);
    label.add(new JLabel(sv,JLabel.CENTER));
}


private void addValuePair(JPanel label, String s, double value, String tooltip){
    label.add(new JLabel(s,JLabel.RIGHT));
    String sv=": "+value;
    JLabel l = new JLabel(": "+value,JLabel.LEFT);
    l.setToolTipText(tooltip);
    label.add(l);
}

private void addValuePair(JPanel label, String sleft, String sright){
    label.add(new JLabel(sleft,JLabel.RIGHT));
    label.add(new JLabel(sright,JLabel.LEFT));
}

private void addValuePair(JPanel label, String sleft, String sright, String tooltip){
    label.add(new JLabel(sleft,JLabel.RIGHT));
    JLabel l = new JLabel(sright,JLabel.LEFT);
    l.setToolTipText(tooltip);
    label.add(l);
}

private JLabel makeRightLabelWithToolTip(String s, String tooltip){
    JLabel l = new JLabel(s,JLabel.RIGHT);
    l.setToolTipText(tooltip);
    return l;
}
private JLabel makeCenterLabelWithToolTip(String s, String tooltip){
    JLabel l = new JLabel(s,JLabel.CENTER);
    l.setToolTipText(tooltip);
    return l;
}

 
}// eo IslandParameterWindow class

