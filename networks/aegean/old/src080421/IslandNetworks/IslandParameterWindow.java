/*
 * IslandIslandParameterBox.java
 *
 * Created on 23 August 2006, 12:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import TimUtilities.NumbersToString;
import TimUtilities.StatisticalQuantity;


import javax.swing.Box;          
import javax.swing.JLabel;          
import javax.swing.JPanel;          
import java.awt.GridLayout;          
//import java.awt.*;
//import java.awt.font.*;
//import java.awt.geom.*;
//import java.awt.image.*;
//import java.awt.event.*;

/**
 * Defines JPanels which describe input and output parameters.
 * @author time
 */
// *********************************************************************************

 /*
  * Makes a JFrames or Boxes with parameter information on an island network.
 */
public class IslandParameterWindow{
    private islandNetwork islnet;
    
// Constructor
 public IslandParameterWindow(islandNetwork inputIslandNetwork)
 {
     islnet = new islandNetwork(inputIslandNetwork);
 }
 
 /*
  * Returns a Box with parameter information
  *@parameter islandNetwork upon which information is required.
  *@return a Box ready to be added to panels etc.
  */
 public Box getParameterBox()
 {
     Box pb = Box.createVerticalBox();
     pb.add(getBasicSettingsPanel());
     pb.add(getInputVariablesPanel());
     return pb;
 }
 
 /*
  * Returns a JPanel with parameter information
  *@parameter islandNetwork upon which information is required.
  *@return a Box ready to be added to panels etc.
  */
 public JPanel getParameterPanel()
 {
     JPanel pb = new JPanel();
     pb.add(getBasicSettingsPanel());
     pb.add(getInputVariablesPanel());
     return pb;
 }
 
 /*
  * Returns a JPanel with basic programme settings given.
  *@return a JPanel ready to be added to panels etc.
  */
 public JPanel getBasicSettingsPanel()
 {
        JPanel topBox= new JPanel();
        topBox.setLayout( new GridLayout(5,2));
        addValuePair(topBox,"Input file :",islnet.inputFile.getNameRoot());
        addValuePair(topBox,"Output files : ",islnet.outputFile.getNameRoot());
        addValuePair(topBox,"Model :",islnet.modelNumber.major+"_"+islnet.modelNumber.minor+",  "+islnet.modelNumber.majorString +"+"+islnet.modelNumber.minorString);
        String s="";
        if ( islnet.updateMode == 0) s="PPA Update"; else s="Monte Carlo Update";
        addValuePair(topBox," ",s+", "+islnet.edgeSet.edgeMode.description());
        return topBox;
 }
 
 /*
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
        addValuePair(inputBox,"max vertex value", islnet.vertexMaximum);
        addValuePair(inputBox,"metric number ",islnet.Hamiltonian.shortDistanceScale);        
        addValuePair(inputBox,"beta ",islnet.Hamiltonian.beta);        
        return inputBox;
}//eo 

 //        inputBox.add(new JLabel("    Zero Colour Frac ",  JLabel.RIGHT));
//        inputBox.add(new JLabel(islnet.zeroColourFrac));        
//        inputBox.add(new JLabel("    Min. Colour Frac ",  JLabel.RIGHT));
//        inputBox.add(new JLabel(islnet.minColourFrac));        
//        if (islnet.DisplayMaxVertexScale>0) inputBox.add(new JLabel(" Absolute Vertex Display, Max "+islnet.DisplayMaxVertexScale));        
//        else inputBox.add(new JLabel("Relative Vertex Display"));        
//        if (islnet.DisplayMaxEdgeScale>0) inputBox.add(new JLabel(" Absolute Edge Display, Max "+islnet.DisplayMaxEdgeScale));        
//        else inputBox.add(new JLabel("Relative Edge Display"));        
//        String s;
//        double ip = islnet.influenceProb; //islnet.siteRank.transferMatrix.influenceProbability;
//        if (ip>0.99999) s=" Infinite Influence Range";
//        else s=" Influence Range " + islnet.toStringString(ip/(1.0-ip),3);
//        if (ip<0) s=" Invalid Influence ";
//        s=s+" (prob="+islnet.toStringString(ip,3)+")";
//        inputBox.add(new JLabel(s));        
//        switch (islnet.DisplayVertexType)
//        {
//            case 2: s="sites by Influence"; break;
//            case 1: s="sites by Rank"; break;
//            case 0:
//            default: s="sites by Size";
//        }
//        inputBox.add(new JLabel(s));
//        inputBox.add(inputBox);

  /*
  * Returns a JPanel with output statistics information.
  *@param decPoints number of decimal points to show
  *@return a JPanel ready to be added to panels etc.
  */
 public JPanel getOutputMeasuresPanel(int decPoints )
 {
     NumbersToString n2s = new NumbersToString(decPoints);
     JPanel infoBox= new JPanel();
     infoBox.setLayout( new GridLayout(6,5));
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
        infoBox.add(makeRightLabelWithToolTip("Site Weight :","Population of one site, fixed value * variable fraction (S_i v_i)"));
        addStatsValues(infoBox,  "Site weight", islnet.siteWeightStats, decPoints);
        //
        infoBox.add(makeRightLabelWithToolTip("Site Out W.Strength :","Total outgoing trade from one site (sum_j S_i v_i e_(ij) )"));
        addStatsValues(infoBox, "Site out w.strength", islnet.siteStrengthOutStats, decPoints);
        //
        infoBox.add(makeRightLabelWithToolTip("Site In W.Strength :","Total incoming trade from one site (sum_j S_j v_j e_(ij) )"));
        addStatsValues(infoBox, "Site in w.strength", islnet.siteStrengthInStats, decPoints);
//        infoBox.add(new JLabel(n2s.toString(islnet.siteStrengthInStats.getAverage() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.siteStrengthInStats.getSigma() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.siteStrengthInStats.minimum ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.siteStrengthInStats.maximum ),JLabel.CENTER));
        //
        infoBox.add(makeRightLabelWithToolTip("Edge Weight :","Total trade along one edge, (S_i v_i e_(ij) )"));
        addStatsValues(infoBox, "Edge weight", islnet.edgeWeightStats, decPoints);
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeWeightStats.getAverage() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeWeightStats.getSigma() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeWeightStats.minimum ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeWeightStats.maximum ),JLabel.CENTER));
        //
        infoBox.add(makeRightLabelWithToolTip("Edge Value :", "Fraction of trade along one edge, e_(ij) "));
        addStatsValues(infoBox, "Edge value", islnet.edgeValueStats, decPoints);
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeValueStats.getAverage() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeValueStats.getSigma() ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeValueStats.minimum ),JLabel.CENTER));
//        infoBox.add(new JLabel(n2s.toString(islnet.edgeValueStats.maximum ),JLabel.CENTER));

        JPanel infoPanel = new JPanel();
//        infoPanel.setLayout(new BoxLayout)
        infoPanel.add(new JLabel("Energy = "+n2s.toString(islnet.energy)));
        infoPanel.add(infoBox);
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

