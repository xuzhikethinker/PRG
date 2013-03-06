/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import IslandNetworks.Edge.EdgeTypeSelection;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
         
import IslandNetworks.Vertex.VertexTypeSelection;


/**
 *  Gives JFrame asking for the input values and initiating a run.
 *  <p>Built up from Celcius Converter programme of SWING example web site.
 * @author time
 */
      
    public class InputParameterFrame implements ActionListener {
    JFrame inputFrame;
    JPanel buttonPanel,inputPanel,displayPanel,modelNumberPanel, modePanel, DVTPanel;
    JTextField  fileOutputValue, runnameValue,
               muValue,jValue,kappaValue,lambdaValue,distScaleValue,reldistScaleValue,alphaValue,betaValue,bValue,gammaValue,
               updateModeValue, maxVertexValue, edgeModeValue, majorModelNumberValue, minorModelNumberValue;
    JLabel fileInputValue, fileInputMessage, fileOutputMessage, runnameMessage,
            muMessage,jMessage,kappaMessage,lambdaMessage,distScaleMessage,reldistScaleMessage,alphaMessage,betaMessage,bMessage,gammaMessage,
               updateModeMessage, maxVertexMessage, edgeModeMessage, modelNumberMessage;
    JLabel notInPPAlabel =  new JLabel("Not used in PPA", SwingConstants.RIGHT);
    JTextField sSFValue, eWFValue, mCFValue, zCFValue, DMVSValue, DMESValue, ipValue, ctsValue; 
    JLabel sSFMessage, eWFMessage, mCFMessage, zCFMessage, DMVSMessage, DMESMessage, DVTMessage, ipMessage, ctsMessage;
    // ButtonGroup updateModeGroup; // updateDVTGroup;
    //JRadioButton DPMode,VPMode,PPAMode,MCMode;
    JRadioButton InfluenceMode, RankMode, SizeMode;
           JLabel SWMMessage;
           JPanel SWMPanel1,SWMPanel2;
           ButtonGroup SWMGroup;
           JRadioButton alphaMode, weightMode, rankMode, rankOverWeightMode;

    
    JLabel messageLabel, drawMessageLabel;
    JButton calcButton, drawButton;
    JCheckBox mcHotStartCB, autonameCB;

    JComboBox calcModeChooser, mcStartChooser;
    
    String resMessage="OK";

    private islandNetwork inet;

    private static final double betaInitialMCnew =3.0;
    private static final double betaInitialRWGM =1.1;
    private static final double betaInitialPPA = 3;
    private static final double betaInitialAlonso = 1.1;
    private String betaInitialText = "UNSET";
    private String betaInitialTextTip = "NOT YET SET";
    private double betaInitialValue = betaInitialMCnew;
    private String muText = "UNSET";
    private String muTextTip = "NOT YET SET";
    private double muValueNumber = 1.0;

    
    public InputParameterFrame(islandNetwork inetinput) {
        inet = inetinput;  //do not deep copy so can pass values back.
        //Create and set up the window.
        //Date d = new Date();
        Calendar cal = Calendar.getInstance();
        String time=cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"."+cal.get(Calendar.MILLISECOND);
        inputFrame = new JFrame("Parameters for "+inet.inputFile.getNameRoot()+" "+time);
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setSize(new Dimension(400,400));
        //inputFrame.setIconImage(new ImageIcon("images/pot1616.gif").getImage());
        inputFrame.setIconImage(new AriadneIcon().getPotBlue16());

        //Create and set up the panel.
        buttonPanel = new JPanel(new GridLayout(1, 2));
        inputPanel = new JPanel(new GridLayout(16, 2));
        displayPanel = new JPanel(new GridLayout(13, 2));

        //Add the widgets.
        addWidgets();
        

        //Set the default button.
        inputFrame.getRootPane().setDefaultButton(calcButton);

        // Add buttons and info box
        calcButton = new JButton("CALCULATE");
        buttonPanel.add(calcButton);
        //Listen to events from the Calc button.
        calcButton.addActionListener(this);

        drawButton = new JButton("REDRAW");
        buttonPanel.add(drawButton);
        //Listen to events from the Draw button.
        drawButton.addActionListener(this);


        
        
        //Add the panel to the window.
        inputFrame.getContentPane().add(inputPanel, BorderLayout.WEST);        
        inputFrame.getContentPane().add(displayPanel, BorderLayout.EAST);
        inputFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        //Display the window.
        inputFrame.pack();
        inputFrame.setVisible(true);
    } // eo constructor InputParametersWindow()

    /**
     * Create and add the widgets.
     */
    private void addWidgets() {
        //Create widgets.
        
 // Code fragment on how to make this active       
 //        DocumentListener myListener = ??;
 //    JTextField myArea = ??;
 //    myArea.getDocument().addDocumentListener(myListener);
        
        islandNetwork i = inet; //new islandNetwork(1);
                
        fileInputMessage = new JLabel("Basic Input File Name ", SwingConstants.RIGHT);
        fileInputValue = new JLabel(inet.inputFile.getFullFileRoot(), SwingConstants.CENTER);
        inputPanel.add(fileInputMessage);
        inputPanel.add(fileInputValue);
        
        
// try setting up instances of the model variable boxes  first

         // set up calculational mode chooser, use getSelectedIndex() to access
        // set up MC start mode chooser, use getSelected Index() to access
        calcModeChooser = new JComboBox();
        mcStartChooser = new JComboBox();

        autonameCB= new JCheckBox("Automatic Output File Names");
        autonameCB.setSelected(true);

        String edgeModeInitial = ""+i.edgeSet.edgeMode.edgeModeValue();
        //final JTextField edgeModeValue = new JTextField(edgeModeInitial,4);
        edgeModeValue = new JTextField(edgeModeInitial,4);
        edgeModeMessage = new JLabel("Maximum Edge Value ", SwingConstants.RIGHT);
        setToolTipText(edgeModeValue, edgeModeMessage, "Maximum value to allow for edge");

        String maxVertexInitial = ""+i.vertexMode.getModeValue();
        maxVertexValue = new JTextField(maxVertexInitial,4);
        maxVertexMessage = new JLabel("Vertex Mode Value ", SwingConstants.RIGHT);
        setToolTipText(maxVertexValue,maxVertexMessage,"Max. vertex value if positive, minus the max weight if negative");
        //.setToolTipText("Max. vertex value if positive, minus the max weight if negative");

        modelNumberPanel = new JPanel(new GridLayout(1, 3));
        String majorInitial = i.modelNumber.major+" ";
        String minorInitial = i.modelNumber.minor+" ";
        majorModelNumberValue = new JTextField(majorInitial,1);
        minorModelNumberValue = new JTextField(minorInitial,1);
        modelNumberPanel.add(majorModelNumberValue);
        modelNumberPanel.add(new JLabel("."));
        modelNumberPanel.add(minorModelNumberValue);
        modelNumberMessage = new JLabel("Model Number ", SwingConstants.RIGHT);
        modelNumberMessage.setToolTipText("Model numbers are used to choose different MC Hamiltonians");

//        fileOutputValue = new JTextField(outputFile.getBasicRoot().substring(0,15),16);
        fileOutputValue = new JTextField(inet.outputFile.getBasicRoot(),16);
        fileOutputMessage = new JLabel("Basic Root for Output Files ", SwingConstants.RIGHT);
        setToolTipText(fileOutputValue,fileOutputMessage,"Output files will start with this string");

        runnameValue = new JTextField(""+inet.outputFile.sequenceNumber,16);
        runnameMessage = new JLabel("Run Number ", SwingConstants.RIGHT);
        setToolTipText(runnameValue,runnameMessage,"Number used to distinguish different runs with same parameters");

        String jInitial = ""+i.Hamiltonian.vertexSource;
        jValue = new JTextField(jInitial,4);
        jMessage = new JLabel("j ", SwingConstants.RIGHT);
        setToolTipText(jValue,jMessage,"Value of j parameter in Hamiltonian");

        String muInitial = ""+i.Hamiltonian.edgeSource;
        muValue = new JTextField(muInitial,4);
        muMessage = new JLabel("mu ", SwingConstants.RIGHT);
        setToolTipText(muValue,muMessage,"Value of mu parameter in Hamiltonian");

        String kappaInitial = ""+i.Hamiltonian.kappa;
        kappaValue = new JTextField(kappaInitial,4);
        kappaMessage = new JLabel("kappa ", SwingConstants.RIGHT);
        setToolTipText(kappaValue,kappaMessage,"Value of kappa parameter in Hamiltonian");

        String lambdaInitial = ""+i.Hamiltonian.lambda;
        lambdaValue = new JTextField(lambdaInitial,4);
        lambdaMessage = new JLabel("lambda ", SwingConstants.RIGHT);
        setToolTipText(lambdaValue,lambdaMessage,"Value of lambda parameter in Hamiltonian");

        String distScaleInitial = ""+i.Hamiltonian.distanceScale;
        distScaleValue = new JTextField(distScaleInitial,4);
        distScaleMessage = new JLabel("Distance Scale ", SwingConstants.RIGHT);
        setToolTipText(distScaleValue,distScaleMessage,"Value of distance scale used in all models");

        String reldistScaleInitial = ""+i.Hamiltonian.shortDistanceScale;
        reldistScaleValue = new JTextField(reldistScaleInitial,4);
        reldistScaleMessage = new JLabel("Short Distance ", SwingConstants.RIGHT);
        setToolTipText(reldistScaleValue,reldistScaleMessage,"Value of short distance scale used only in MC");

        String betaInitial = ""+betaInitialValue;
        betaValue = new JTextField(betaInitial,4);
        betaMessage = new JLabel(betaInitialText, SwingConstants.RIGHT);

        String bInitial = ""+i.Hamiltonian.getb();
        bValue = new JTextField(bInitial,4);
        bMessage = new JLabel("b ", SwingConstants.RIGHT);
        bValue.setEnabled(false);
        setToolTipText(bValue,bMessage,"Non linear site parameter used in old MC models");


        // Now set up the modes and alter
        for (int m=0; m<i.updateMode.getNumberModes(); m++) calcModeChooser.addItem(i.updateMode.getString(m));
        calcModeChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inet.updateMode.set(calcModeChooser.getSelectedIndex());
                calcModeChooser.setToolTipText("Current calculational mode: " + inet.updateMode.toLongString());
                mcStartChooser.setEnabled(inet.updateMode.isMC());
                edgeModeValue.setEnabled(inet.updateMode.isMC());
                maxVertexValue.setEnabled(inet.updateMode.isMC());
                majorModelNumberValue.setEnabled(inet.updateMode.isMC());
                minorModelNumberValue.setEnabled(inet.updateMode.isMC());
                jValue.setEnabled(inet.updateMode.isMC());
                //muValue.setEnabled(inet.updateMode.isMC());
                setMuStuff(inet);
                kappaValue.setEnabled(inet.updateMode.isMC());
                lambdaValue.setEnabled(inet.updateMode.isMC());
                distScaleValue.setEnabled(!inet.updateMode.isPPA());
                reldistScaleValue.setEnabled(inet.updateMode.isMC());
                setBetaStuff(inet);
//                betaValue.setText(""+betaInitialValue);
//                betaMessage.setText(betaInitialText);
//                betaValue.setToolTipText(betaInitialTextTip);
//                betaMessage.setToolTipText(betaInitialTextTip);
            }
        });
        calcModeChooser.setSelectedIndex(i.updateMode.getNumber());
        calcModeChooser.setToolTipText("Current calculational mode: " + i.updateMode.toLongString());
        inputPanel.add(calcModeChooser);
 
        mcStartChooser.setToolTipText("Choose Monte Carlo starting configuration");
        for (int m=0; m<i.monteCarloStartMode.getNumberModes(); m++) mcStartChooser.addItem(i.monteCarloStartMode.getString(m));
        //for (int m=0; m<i.monteCarloStartMode.getNumberModes(); m++) mcStartChooser.addItem("mode "+m);        
        mcStartChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inet.monteCarloStartMode.set(mcStartChooser.getSelectedIndex());
            }
        });
        mcStartChooser.setEnabled(inet.updateMode.isMC());
        mcStartChooser.setSelectedIndex(i.monteCarloStartMode.getNumber());
        inputPanel.add(mcStartChooser);
        

        inputPanel.add(autonameCB);
        
        inputPanel.add(new JLabel());
        
        
        inputPanel.add(edgeModeMessage);
        inputPanel.add(edgeModeValue);
        
        inputPanel.add(maxVertexMessage);
        inputPanel.add(maxVertexValue);
        
        inputPanel.add(modelNumberMessage);
        inputPanel.add( modelNumberPanel);
         
        inputPanel.add(fileOutputMessage);
        inputPanel.add(fileOutputValue);
        
        inputPanel.add(runnameMessage);
        inputPanel.add(runnameValue);
        
        inputPanel.add(jMessage);
        //if (updateMode==0) inputPanel.add(notInPPAlabel); else 
        inputPanel.add(jValue);
        
        inputPanel.add(muMessage);
        inputPanel.add(muValue);
        
        inputPanel.add(kappaMessage);
        inputPanel.add(kappaValue);
        inputPanel.add(lambdaMessage);
        inputPanel.add(lambdaValue);
        
        inputPanel.add(distScaleMessage);
        inputPanel.add(distScaleValue);
        
        inputPanel.add(reldistScaleMessage);
        inputPanel.add(reldistScaleValue);
        
        if (inet.updateMode.isPPA()) i.Hamiltonian.beta=3.0;
        setBetaStuff(i);
        inputPanel.add(betaMessage);
        inputPanel.add(betaValue);

// b appears to be unused at present
//        inputPanel.add(bMessage);
//        inputPanel.add(bValue);
        
        // Now list display variables
        displayPanel.add(new JLabel("<html><em>Display Variables</em></html>", SwingConstants.CENTER) );
        displayPanel.add(new JLabel("<html><em>Values</em>", SwingConstants.LEFT) );
        
        String sSFInitial = ""+i.siteWeightFactor;
        sSFValue = new JTextField(sSFInitial,4);
        sSFMessage = new JLabel("<html>Maximum Site Size </html>", SwingConstants.RIGHT);
        displayPanel.add(sSFMessage);
        displayPanel.add(sSFValue);
        
        String eWFInitial = ""+i.edgeWidthFactor;
        eWFValue = new JTextField(eWFInitial,4);
        eWFMessage = new JLabel("<html>Maximum Edge Width </html>", SwingConstants.RIGHT);
        displayPanel.add(eWFMessage);
        displayPanel.add(eWFValue);
        
        String zCFInitial = String.format("%6.4f",i.edgeSet.zeroColourFrac);
        zCFValue = new JTextField(zCFInitial,4);
        zCFMessage = new JLabel("<html>Edge Minimum Fraction (grey) </html>", SwingConstants.RIGHT);
        zCFMessage.setToolTipText("Minimum edge weight before edge shown at all.\nIf not in colour, this edge will be light grey and of minimum thickness.");
        displayPanel.add(zCFMessage);
        displayPanel.add(zCFValue);
        
        String mCFInitial = String.format("%6.4f",i.edgeSet.minColourFrac);
        mCFValue = new JTextField(mCFInitial,4);
        mCFMessage = new JLabel("<html>Edge Minimum Fraction (colour) </html>", SwingConstants.RIGHT);
        mCFMessage.setToolTipText("Minimum edge weight before edge shown in colour, given as a fraction of maximum value.");
        displayPanel.add(mCFMessage);
        displayPanel.add(mCFValue);
        
        String DMVSInitial = ""+i.DisplayMaxVertexScale;
        DMVSValue = new JTextField(DMVSInitial,4);
        DMVSMessage = new JLabel("<html>Max. Vertex Size </html>", SwingConstants.RIGHT);
        displayPanel.add(DMVSMessage);
        displayPanel.add(DMVSValue);
        
        String DMESInitial = ""+i.edgeSet.DisplayMaxEdgeScale;
        DMESValue = new JTextField(DMESInitial,4);
        DMESMessage = new JLabel("<html>Max. Edge Size </html>", SwingConstants.RIGHT);
        //DMESMessage.setForeground(Color.CYAN);
        displayPanel.add(DMESMessage);
        displayPanel.add(DMESValue);

        String ipInitial = ""+i.influenceProb;
        ipValue = new JTextField(ipInitial,4);
        ipMessage = new JLabel("<html>Influence Prob.</html>", SwingConstants.RIGHT);
        //DMESMessage.setForeground(Color.CYAN);
        displayPanel.add(ipMessage);
        displayPanel.add(ipValue);
        
        String ctsInitial = ""+i.cultureTimeScale;
        ctsValue = new JTextField(ctsInitial,4);
        ctsMessage = new JLabel("<html>Culture Time</html>", SwingConstants.RIGHT);
        //DMESMessage.setForeground(Color.CYAN);
        displayPanel.add(ctsMessage);
        displayPanel.add(ctsValue);
        
        
        // set up cluster chooser use getSelectedIndex() to access
        final JComboBox vertexChooser = new JComboBox();
        vertexChooser.setToolTipText("Choose type of vertex to display");
        for (int t=0; t<VertexTypeSelection.numberTypes; t++) vertexChooser.addItem(VertexTypeSelection.name[t]);
        vertexChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inet.DisplayVertexType.setValue(vertexChooser.getSelectedIndex());
                }
        });
        DVTMessage = new JLabel("<html>Display vertices by</html>", SwingConstants.RIGHT);
        displayPanel.add(DVTMessage);
        displayPanel.add(vertexChooser);

        // set up cluster chooser use getSelectedIndex() to access
        final JComboBox edgeTypeChooser = new JComboBox();
        edgeTypeChooser.setToolTipText("Choose type of edge to display");
        for (int t=0; t<EdgeTypeSelection.numberTypes; t++) edgeTypeChooser.addItem(EdgeTypeSelection.name[t]);
        vertexChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inet.edgeSet.DisplayEdgeType.setValue(edgeTypeChooser.getSelectedIndex());
                }
        });
        DVTMessage = new JLabel("<html>Display edges by</html>", SwingConstants.RIGHT);
        displayPanel.add(DVTMessage);
        displayPanel.add(edgeTypeChooser);
       
        SWMGroup = new ButtonGroup();
        JRadioButton numMode = new JRadioButton("Num.");
        numMode.setActionCommand("SWNum");
        alphaMode = new JRadioButton("Alph.");
        alphaMode.setActionCommand("SWAlpha");
//        alphaMode.setSelected(true);
        weightMode = new JRadioButton("Weight");
        weightMode.setActionCommand("SWWeight");
        rankMode = new JRadioButton("Rank");
        rankMode.setActionCommand("SWRank");
        rankOverWeightMode = new JRadioButton("Rank/Weight");
        rankOverWeightMode.setActionCommand("SWRankOverWeight");
        SWMGroup.add(numMode);
        SWMGroup.add(alphaMode);
        SWMGroup.add(weightMode);
        SWMGroup.add(rankMode);
        SWMGroup.add(rankOverWeightMode);
        SWMPanel1 = new JPanel(new GridLayout(1, 3));
        SWMPanel1.add(numMode);
        SWMPanel1.add(alphaMode);
        SWMPanel1.add(weightMode);
        SWMPanel2 = new JPanel(new GridLayout(1, 3));
        SWMPanel2.add(rankMode);
        SWMPanel2.add(rankOverWeightMode);
        // set set button to be equal to current value
        switch (inet.siteWindowMode.value)
        {
            case 4: rankOverWeightMode.setSelected(true);  break;
            case 2: rankMode.setSelected(true);  break;
            case 1: weightMode.setSelected(true);  break;
            case 0: numMode.setSelected(true);  break;
            case 3: 
            default: alphaMode.setSelected(true);  
        }


        SWMMessage = new JLabel("Site Window by", SwingConstants.RIGHT);
        displayPanel.add(SWMMessage);
        displayPanel.add(SWMPanel1);
        displayPanel.add(new JLabel(" "));
        displayPanel.add(SWMPanel2);
        // Register a listener for the radio buttons.
        RadioListener modeListener = new RadioListener();
        numMode.addActionListener(modeListener);
        alphaMode.addActionListener(modeListener);
        weightMode.addActionListener(modeListener);
        rankMode.addActionListener(modeListener);
        rankOverWeightMode.addActionListener(modeListener);

    } // eo addWidgets


    private void setToolTipText(JTextField tf, JLabel jl, String ttt){
        tf.setToolTipText(ttt);
        jl.setToolTipText(ttt);
    }


    /**
     * Sets the initial beta global parameters
     * @param i previous island network used for MC old setting.
     */
    private void setBetaStuff(islandNetwork i){
        betaValue.setEnabled(false);
        betaInitialText=" ";
        betaInitialTextTip="Unused in this mode";
        betaInitialValue=betaInitialMCnew;

       if (inet.updateMode.isMC()) {
            if (inet.monteCarloStartMode.isCurrentMode("Old")) betaInitialValue = i.Hamiltonian.beta;
            betaValue.setEnabled(true);
            betaInitialText="Initial beta ";
            betaInitialTextTip="Initial value of inverse temperature for Monte Carlo";
       }
//        else {
//            betaInitialValue=betaInitialMCnew;
//        }
        if (inet.updateMode.isRWGM()) {
            betaValue.setEnabled(true);
            betaInitialValue=betaInitialRWGM;
            betaInitialText="Power of W ";
            betaInitialTextTip="Power to raise W (site inputs)";
        }
        if (inet.updateMode.isAlonso()) {
            betaValue.setEnabled(true);
            betaInitialValue=betaInitialAlonso;
            betaInitialText="Power of I ";
            betaInitialTextTip="Power to raise I (site inputs)";
        }
        if (inet.updateMode.isPPA()) {
            betaValue.setEnabled(true);
            betaInitialValue=betaInitialPPA;
            betaInitialText="Number of Edges ";
            betaInitialTextTip="Number of edges drawn from each site";
        }
//        else {
//            betaInitialText="Initial beta ";
//            betaInitialTextTip="Initial value of inverse temperature for Monte Carlo";
//        }
        betaValue.setText(Double.toString(betaInitialValue));
        betaMessage.setText(betaInitialText);
        betaValue.setToolTipText(betaInitialTextTip);
        betaMessage.setToolTipText(betaInitialTextTip);
//                betaValue.setText(""+betaInitialValue);
//                betaMessage.setText(betaInitialText);
//                betaValue.setToolTipText(betaInitialTextTip);
//                betaMessage.setToolTipText(betaInitialTextTip);
    }

    /**
     * Sets the initial beta global parameters
     * @param i previous island network used for MC old setting.
     */
    private void setMuStuff(islandNetwork i){
        muValue.setEnabled(false);
        muText=" ";
        muTextTip="Unused in this mode";
        muValueNumber=i.Hamiltonian.edgeSource;

       if (inet.updateMode.isMC()) {
            if (inet.monteCarloStartMode.isCurrentMode("Old")) betaInitialValue = i.Hamiltonian.beta;
            muValue.setEnabled(true);
            muText="mu ";
            muTextTip="Initial value of inverse temperature for Monte Carlo";
       }
       if (inet.updateMode.isAlonso()) {
            muValue.setEnabled(true);
            muText="Power of O ";
            muTextTip="Power to raise O (site outputs)";
        }
        muValue.setText(Double.toString(muValueNumber));
        muMessage.setText(muText);
        muValue.setToolTipText(muTextTip);
        muMessage.setToolTipText(muTextTip);

    }


    /** Listens to the radio buttons. */
    class RadioListener implements ActionListener 
    { 
        public void actionPerformed(ActionEvent e) 
        {
            String command = e.getActionCommand() ;
//            if (command.equals("P")) inet.updateMode.setNumber(0);
//            if (command.equals("M")) inet.updateMode.setMode(1);
//            if (command.equals("D")) inet.updateMode.setMode(2);
//            if (command.equals("V")) inet.updateMode.setMode(3);
//            if (command.equals("NVSize")) inet.DisplayVertexType = 0;
//            if (command.equals("NVRank")) inet.DisplayVertexType = 1;
//            if (command.equals("NVInfluence")) inet.DisplayVertexType = 2; 
            if (command.equals("SWNum")) inet.siteWindowMode.value = 0;
            if (command.equals("SWAlpha")) inet.siteWindowMode.value = 3;
            if (command.equals("SWWeight")) inet.siteWindowMode.value = 1;
            if (command.equals("SWRank")) inet.siteWindowMode.value = 2;
            if (command.equals("SWRankOverWeight")) inet.siteWindowMode.value = 4;

            
        }
    }
    
    
    
    
    public void actionPerformed(ActionEvent event) {
        int res=0;        
        try
        {
            String command = event.getActionCommand();
            
            inet.inputFile.setNameRoot(fileInputValue.getText());
            inet.outputFile.setNameRoot(fileOutputValue.getText());
            inet.outputFile.sequenceNumber = Integer.parseInt(runnameValue.getText());
            //inet.coldStart = !mcHotStartCB.isSelected();
            inet.autoSetOutputFileName = autonameCB.isSelected();
            inet.edgeSet.edgeMode.setEdgeMode(Double.parseDouble(edgeModeValue.getText()));
            inet.vertexMode.setVertexMode(Double.parseDouble(maxVertexValue.getText()));
            double mu = Double.parseDouble(muValue.getText());  
            inet.Hamiltonian.edgeSource = mu;
            double j = Double.parseDouble(jValue.getText());  
            inet.Hamiltonian.vertexSource = j;
            inet.Hamiltonian.kappa = Double.parseDouble(kappaValue.getText());  
            inet.Hamiltonian.lambda = Double.parseDouble(lambdaValue.getText());  
            inet.Hamiltonian.distanceScale = Double.parseDouble(distScaleValue.getText());  
            inet.Hamiltonian.shortDistanceScale = Double.parseDouble(reldistScaleValue.getText());  
            inet.betaInitial = Double.parseDouble(betaValue.getText());  
            inet.Hamiltonian.setb( Double.parseDouble(bValue.getText()) );  
            double nmn = Double.parseDouble(minorModelNumberValue.getText());  
            double jmn = Double.parseDouble(majorModelNumberValue.getText());  
            inet.modelNumber.set (jmn,nmn);
            inet.siteWeightFactor = (int) (Double.parseDouble(sSFValue.getText()) +0.5); 
            inet.edgeWidthFactor = (int) (Double.parseDouble(eWFValue.getText()) +0.5); 
            inet.edgeSet.zeroColourFrac = Double.parseDouble(zCFValue.getText()); 
            inet.edgeSet.minColourFrac = Double.parseDouble(mCFValue.getText()) ; 
            inet.DisplayMaxVertexScale = Double.parseDouble(DMVSValue.getText()) ; 
            inet.edgeSet.DisplayMaxEdgeScale = Double.parseDouble(DMESValue.getText()); 
            inet.influenceProb= Double.parseDouble(ipValue.getText()); 
            inet.cultureTimeScale= Double.parseDouble(ctsValue.getText()); 
            
            
            
            //newNetworkDisplaySyle = NewNetworkStyleCB.isSelected();
                    
            inet.setOutputFileName();
            if (command.equals("CALCULATE")) {
                
                //Date date = new Date();
                //if (inet.message.getInformationLevel()>-1) System.out.println(date);
                if (inet.message.getInformationLevel()>0) inet.printNetworkForData("#",3);
                //if (inet.message.getInformationLevel()>0) inet.showDistanceValues("#",3);
                //message.println(2,"Data reading OK, number of sites is "+numberSites);
                //System.out.println("in InputParameterFrame actionPerformed CALCULATE before doMC");
                System.out.println("Calculating using "+inet.updateMode.toLongString());
                inet.calculateEdgeModel();
//                    switch (inet.updateMode.getNumber()){
//                        case 0: inet.doPPA(); break;
//                        case 1: inet.doPPA(); break;
//                        case 2: inet.doMDN(); break;
//                        case 3: inet.doMC(); break;
//                        case 4: inet.doVP(); break;
//                        case 5: inet.doGM(); break;
//                        case 6: inet.doRW(); break;
//                        case 7: inet.doSimpleGM(); break;
////                        case 7: inet.doXTent(); break;
//                        default: System.err.println("Unknown calculational mode in InputParameterFrame "+inet.updateMode.getNumber());
//                        break;
//                    } // eo switch
            }
            //System.out.println("in InputParameterFrame actionPerformed before showNetwork");
            inet.showNetwork("#", 3);
            
        } catch (ArrayIndexOutOfBoundsException e) //catch (Exception e) 
        {
         res=10;
         resMessage = "Input Window actionPerformed error: "+e;  
         inet.message.println(-2,resMessage);
         if (inet.message.getInformationLevel()>=-2) JOptionPane.showMessageDialog(inputFrame, resMessage, "Input Parameter Window Error", JOptionPane.ERROR_MESSAGE);
        
        }
        
    } // eo public void actionPerformed(ActionEvent event)

   

}// eo public class InputParametersWindow implements ActionListener 



