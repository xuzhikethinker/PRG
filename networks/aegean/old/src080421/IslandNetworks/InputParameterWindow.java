/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//   Built up from Celcius Converter programme of SWING example web site  
 
package IslandNetworks;

//import java.io.*;
//import java.lang.*;
import java.lang.Math.*;
//import java.util.Date;
//import java.util.Random;
import javax.swing.*; 
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;

//import JavaNotes.TextReader;

import TimUtilities.TimMessage;


/**
 *  InputParameterWindow class
 * @author time
 */
      
    public class InputParameterWindow implements ActionListener {
        
        IslandHamiltonian Hamiltonian;
        FileLocation inputFile;
        FileLocation outputFile;
        CalculationParameters cp;
        static TimMessage message;
        
        
        
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
    ButtonGroup updateModeGroup, updateDVTGroup;
    JRadioButton PPAMode,MCMode, InfluenceMode, RankMode, SizeMode;
    //JCheckBox NewNetworkStyleCB;
// for site window    
           JLabel SWMMessage;
           JPanel SWMPanel1,SWMPanel2;
           ButtonGroup SWMGroup;
           JRadioButton alphaMode, weightMode, rankMode, rankOverWeightMode;

    
    JLabel messageLabel, drawMessageLabel;
    JButton calcButton, drawButton;
    JCheckBox autonameCB;
    
    String resMessage="OK";
    
    public InputParameterWindow(FileLocation ifl, IslandHamiltonian inputH, CalculationParameters cpin) {
        
        // no deep copies, pass by reference so this class is changing values by 
        Hamiltonian = inputH; 
        inputFile = ifl; 
        cp = cpin;
        
        //Create and set up the window.
        inputFrame = new JFrame("Parameter Inputs for "+inputFile.getFullFileRoot()+ " Ariadne Network"+ islandNetwork.iNVERSION);
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setSize(new Dimension(400,400));

        //Create and set up the panel.
        buttonPanel = new JPanel(new GridLayout(1, 2));
        inputPanel = new JPanel(new GridLayout(15, 2));
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
    } // eo constructor InputParameterWindow()

    /**
     * Create and add the widgets.
     */
    private void addWidgets() {
        //Create widgets.
        
 // Code fragment on how to make this active       
 //        DocumentListener myListener = ??;
 //    JTextField myArea = ??;
 //    myArea.getDocument().addDocumentListener(myListener);
        
        islandNetwork i = new islandNetwork(1);
                
        fileInputValue = new JLabel(inputFile.getFullFileRoot(), SwingConstants.CENTER);
        fileInputMessage = new JLabel("Input File Name Root ", SwingConstants.RIGHT);
        inputPanel.add(fileInputMessage);
        inputPanel.add(fileInputValue);
        
        // Buttons for processing mode
        updateModeGroup = new ButtonGroup();
        PPAMode = new JRadioButton("PPA Mode");
        PPAMode.setActionCommand("P");
        MCMode = new JRadioButton("MC Mode");
        MCMode.setActionCommand("M");
//        MCMode.setSelected(true);
        updateModeGroup.add(PPAMode);
        updateModeGroup.add(MCMode);
        modePanel = new JPanel(new GridLayout(1, 2));
        modePanel.add(PPAMode);
        modePanel.add(MCMode);
        inputPanel.add(modePanel);
        switch (cp.updateMode)
        {
            case 0: PPAMode.setSelected(true);  break;
            case 1: 
            default: MCMode.setSelected(true);  
        }
//        if (command.equals("P")) cp.updateMode=0;
//            if (command.equals("M")) cp.updateMode=1;

        
        // Register a listener for the radio buttons.
        RadioListener modeListener = new RadioListener();
        PPAMode.addActionListener(modeListener);
        MCMode.addActionListener(modeListener);
        
        autonameCB= new JCheckBox("Automatic Output File Names");
        autonameCB.setSelected(true);
        inputPanel.add(autonameCB);
        
        
        String edgeModeInitial = " "+i.edgeSet.edgeMode.edgeModeValue();
        edgeModeValue = new JTextField(edgeModeInitial,4);
        edgeModeMessage = new JLabel("Maximum Edge Value ", SwingConstants.RIGHT);
        inputPanel.add(edgeModeMessage);
        inputPanel.add(edgeModeValue);
        
        String maxVertexInitial = " "+i.vertexMaximum;
        maxVertexValue = new JTextField(maxVertexInitial,4);
        maxVertexMessage = new JLabel("Max Vertex Value ", SwingConstants.RIGHT);
        inputPanel.add(maxVertexMessage);
        inputPanel.add(maxVertexValue);
        
        modelNumberPanel = new JPanel(new GridLayout(1, 3));
        String majorInitial = i.modelNumber.major+" ";
        String minorInitial = i.modelNumber.minor+" ";
        majorModelNumberValue = new JTextField(majorInitial,1);
        minorModelNumberValue = new JTextField(minorInitial,1);     
        modelNumberPanel.add(majorModelNumberValue);
        modelNumberPanel.add(new JLabel("."));
        modelNumberPanel.add(minorModelNumberValue); 
        modelNumberMessage = new JLabel("Model Number ", SwingConstants.RIGHT);
        inputPanel.add(modelNumberMessage);
        inputPanel.add( modelNumberPanel);
         
//        fileOutputValue = new JTextField(outputFile.getBasicRoot().substring(0,15),16);
        fileOutputValue = new JTextField(outputFile.getBasicRoot(),16);
        fileOutputMessage = new JLabel("Basic Root for Output Files ", SwingConstants.RIGHT);
        inputPanel.add(fileOutputMessage);
        inputPanel.add(fileOutputValue);
        
        runnameValue = new JTextField(""+outputFile.sequenceNumber,16);
        runnameMessage = new JLabel("Run Number ", SwingConstants.RIGHT);
        inputPanel.add(runnameMessage);
        inputPanel.add(runnameValue);
        
        String jInitial = ""+i.Hamiltonian.vertexSource;
        jValue = new JTextField(jInitial,4);
        jMessage = new JLabel("j ", SwingConstants.RIGHT);
        inputPanel.add(jMessage);
        //if (cp.updateMode==0) inputPanel.add(notInPPAlabel); else 
        inputPanel.add(jValue);
        
        String muInitial = " "+i.Hamiltonian.edgeSource;
        muValue = new JTextField(muInitial,4);
        muMessage = new JLabel("mu ", SwingConstants.RIGHT);
        inputPanel.add(muMessage);
        inputPanel.add(muValue);
        
        String kappaInitial = ""+i.Hamiltonian.kappa;
        kappaValue = new JTextField(kappaInitial,4);
        kappaMessage = new JLabel("kappa ", SwingConstants.RIGHT);
        inputPanel.add(kappaMessage);
        inputPanel.add(kappaValue);
        
        String lambdaInitial = ""+i.Hamiltonian.lambda;
        lambdaValue = new JTextField(lambdaInitial,4);
        lambdaMessage = new JLabel("lambda ", SwingConstants.RIGHT);
        inputPanel.add(lambdaMessage);
        inputPanel.add(lambdaValue);
        
        String distScaleInitial = ""+i.Hamiltonian.distanceScale;
        distScaleValue = new JTextField(distScaleInitial,4);
        distScaleMessage = new JLabel("Distance Scale ", SwingConstants.RIGHT);
        inputPanel.add(distScaleMessage);
        inputPanel.add(distScaleValue);
        
        String reldistScaleInitial = ""+i.Hamiltonian.shortDistanceScale;
        reldistScaleValue = new JTextField(reldistScaleInitial,4);
        reldistScaleMessage = new JLabel("Short Distance ", SwingConstants.RIGHT);
        inputPanel.add(reldistScaleMessage);
        inputPanel.add(reldistScaleValue);
        
        if (cp.updateMode==0) i.Hamiltonian.beta=3.0;
        else i.Hamiltonian.beta=1.0;
        String betaInitial = ""+i.Hamiltonian.beta;
        betaValue = new JTextField(betaInitial,4);
        betaMessage = new JLabel("beta ", SwingConstants.RIGHT);
        inputPanel.add(betaMessage);
        inputPanel.add(betaValue);
        
        String bInitial = ""+i.Hamiltonian.getb();
        bValue = new JTextField(bInitial,4);
        bMessage = new JLabel("b ", SwingConstants.RIGHT);
        inputPanel.add(bMessage);
        inputPanel.add(bValue);
        
//        // Now list display vraiables
//        displayPanel.add(new JLabel("<html><em>Display Variables</em></html>", SwingConstants.CENTER) );
//        displayPanel.add(new JLabel("<html><em>Values</em>", SwingConstants.LEFT) );
//        
//        String sSFInitial = ""+i.siteWeightFactor;
//        sSFValue = new JTextField(sSFInitial,4);
//        sSFMessage = new JLabel("<html>Maximum Site Size </html>", SwingConstants.RIGHT);
//        displayPanel.add(sSFMessage);
//        displayPanel.add(sSFValue);
//        
//        String eWFInitial = " "+i.edgeWidthFactor;
//        eWFValue = new JTextField(eWFInitial,4);
//        eWFMessage = new JLabel("<html>Maximum Edge Width </html>", SwingConstants.RIGHT);
//        displayPanel.add(eWFMessage);
//        displayPanel.add(eWFValue);
//        
//        String zCFInitial = " "+i.zeroColourFrac;
//        zCFValue = new JTextField(zCFInitial,4);
//        zCFMessage = new JLabel("<html>Fraction for Zero Colour </html>", SwingConstants.RIGHT);
//        displayPanel.add(zCFMessage);
//        displayPanel.add(zCFValue);
//        
//        String mCFInitial = " "+i.minColourFrac;
//        mCFValue = new JTextField(mCFInitial,4);
//        mCFMessage = new JLabel("<html>Fraction for Minimum Colour </html>", SwingConstants.RIGHT);
//        displayPanel.add(mCFMessage);
//        displayPanel.add(mCFValue);
//        
//        String DMVSInitial = " "+i.DisplayMaxVertexScale;
//        DMVSValue = new JTextField(DMVSInitial,4);
//        DMVSMessage = new JLabel("<html>Max. Vertex Size </html>", SwingConstants.RIGHT);
//        displayPanel.add(DMVSMessage);
//        displayPanel.add(DMVSValue);
//        
//        String DMESInitial = " "+i.DisplayMaxEdgeScale;
//        DMESValue = new JTextField(DMESInitial,4);
//        DMESMessage = new JLabel("<html>Max. Edge Size </html>", SwingConstants.RIGHT);
//        //DMESMessage.setForeground(Color.CYAN);
//        displayPanel.add(DMESMessage);
//        displayPanel.add(DMESValue);
//
//        String ipInitial = " "+i.influenceProb;
//        ipValue = new JTextField(ipInitial,4);
//        ipMessage = new JLabel("<html>Influence Prob.</html>", SwingConstants.RIGHT);
//        //DMESMessage.setForeground(Color.CYAN);
//        displayPanel.add(ipMessage);
//        displayPanel.add(ipValue);
//        
//        String ctsInitial = " "+i.cultureTimeScale;
//        ctsValue = new JTextField(ctsInitial,4);
//        ctsMessage = new JLabel("<html>Culture Time</html>", SwingConstants.RIGHT);
//        //DMESMessage.setForeground(Color.CYAN);
//        displayPanel.add(ctsMessage);
//        displayPanel.add(ctsValue);
//        
//        // Buttons for DisplayVertexType
//        updateDVTGroup = new ButtonGroup();
//        SizeMode = new JRadioButton("Size");
//        SizeMode.setActionCommand("NVSize");
//        RankMode = new JRadioButton("Rank");
//        RankMode.setActionCommand("NVRank");
////        RankMode.setSelected(true);
//        InfluenceMode = new JRadioButton("Influence");
//        InfluenceMode.setActionCommand("NVInfluence");
//        updateDVTGroup.add(SizeMode);
//        updateDVTGroup.add(RankMode);
//        updateDVTGroup.add(InfluenceMode);
//        DVTPanel = new JPanel(new GridLayout(1, 3));
//        DVTPanel.add(SizeMode);
//        DVTPanel.add(RankMode);
//        DVTPanel.add(InfluenceMode);
//        DVTMessage = new JLabel("<html>Display vertices by</html>", SwingConstants.RIGHT);
//        displayPanel.add(DVTMessage);
//        displayPanel.add(DVTPanel);
//        // set set button to be equal to current value
//        switch (DisplayVertexType)
//        {
//            case 2: InfluenceMode.setSelected(true);  break;
//            case 1: RankMode.setSelected(true);  break;
//            case 0: 
//            default: SizeMode.setSelected(true);  
//        }
//
//        // Register a listener for the radio buttons.
//        //RadioListener DVTmodeListener = new RadioListener();
//        SizeMode.addActionListener(modeListener);
//        RankMode.addActionListener(modeListener);
//        InfluenceMode.addActionListener(modeListener);
//        
//        //
//        //NewNetworkStyleCB = new JCheckBox("New style network display");
//        //NewNetworkStyleCB.setSelected(newNetworkDisplaySyle);
//        //NewNetworkStyleCB.addActionListener(modeListener);
//        //displayPanel.add(new JLabel(" "));
//        //displayPanel.add(NewNetworkStyleCB);
//        
//        SWMGroup = new ButtonGroup();
//        JRadioButton numMode = new JRadioButton("Num.");
//        numMode.setActionCommand("SWNum");
//        alphaMode = new JRadioButton("Alph.");
//        alphaMode.setActionCommand("SWAlpha");
////        alphaMode.setSelected(true);
//        weightMode = new JRadioButton("Weight");
//        weightMode.setActionCommand("SWWeight");
//        rankMode = new JRadioButton("Rank");
//        rankMode.setActionCommand("SWRank");
//        rankOverWeightMode = new JRadioButton("Rank/Weight");
//        rankOverWeightMode.setActionCommand("SWRankOverWeight");
//        SWMGroup.add(numMode);
//        SWMGroup.add(alphaMode);
//        SWMGroup.add(weightMode);
//        SWMGroup.add(rankMode);
//        SWMGroup.add(rankOverWeightMode);
//        SWMPanel1 = new JPanel(new GridLayout(1, 3));
//        SWMPanel1.add(numMode);
//        SWMPanel1.add(alphaMode);
//        SWMPanel1.add(weightMode);
//        SWMPanel2 = new JPanel(new GridLayout(1, 3));
//        SWMPanel2.add(rankMode);
//        SWMPanel2.add(rankOverWeightMode);
//        // set set button to be equal to current value
//        switch (siteWindowMode)
//        {
//            case 4: rankOverWeightMode.setSelected(true);  break;
//            case 2: rankMode.setSelected(true);  break;
//            case 1: weightMode.setSelected(true);  break;
//            case 0: numMode.setSelected(true);  break;
//            case 3: 
//            default: alphaMode.setSelected(true);  
//        }
//
//
//        SWMMessage = new JLabel("Site Window by", SwingConstants.RIGHT);
//        displayPanel.add(SWMMessage);
//        displayPanel.add(SWMPanel1);
//        displayPanel.add(new JLabel(" "));
//        displayPanel.add(SWMPanel2);
//        // Register a listener for the radio buttons.
////        RadioListener SWMListener = new RadioListener();
//        numMode.addActionListener(modeListener);
//        alphaMode.addActionListener(modeListener);
//        weightMode.addActionListener(modeListener);
//        rankMode.addActionListener(modeListener);
//        rankOverWeightMode.addActionListener(modeListener);
//
        
        /*  modelNumber.major = 1;
             modelNumber.minor = 0;
             message.getInformationLevel() = 0;
             cp.updateMode = 1; // Sweep
             edgeModeBinary = false;
          */   
        //Add the widgets to the container.
        
//        messageLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
//        jLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    } // eo addWidgets





    /** Listens to the radio buttons. */
    class RadioListener implements ActionListener 
    { 
        public void actionPerformed(ActionEvent e) 
        {
            String command = e.getActionCommand() ;
            if (command.equals("P")) cp.updateMode=0;
            if (command.equals("M")) cp.updateMode=1;
//            if (command.equals("NVSize")) DisplayVertexType = 0;
//            if (command.equals("NVRank")) DisplayVertexType = 1;
//            if (command.equals("NVInfluence")) DisplayVertexType = 2; 
//            if (command.equals("SWNum")) siteWindowMode = 0;
//            if (command.equals("SWAlpha")) siteWindowMode = 3;
//            if (command.equals("SWWeight")) siteWindowMode = 1;
//            if (command.equals("SWRank")) siteWindowMode = 2;
//            if (command.equals("SWRankOverWeight")) siteWindowMode = 4;

            
        }
    }
    
    
    
    
    public void actionPerformed(ActionEvent event) {
        //Parse degrees Celsius as a double and convert to Fahrenheit.
        int res=0;
                
        try
        {
            String command = event.getActionCommand();
            
            inputFile.setNameRoot(fileInputValue.getText());
            outputFile.setNameRoot(fileOutputValue.getText());
            outputFile.sequenceNumber = Integer.parseInt(runnameValue.getText());
            cp.autoSetOutputFileName = autonameCB.isSelected();
            cp.edgeMode.setEdgeMode(Double.parseDouble(edgeModeValue.getText()));
            cp.vertexMaximum = Double.parseDouble(maxVertexValue.getText());
            double mu = Double.parseDouble(muValue.getText());  
            Hamiltonian.edgeSource = mu;
            double j = Double.parseDouble(jValue.getText());  
            Hamiltonian.vertexSource = j;
            Hamiltonian.kappa = Double.parseDouble(kappaValue.getText());  
            Hamiltonian.lambda = Double.parseDouble(lambdaValue.getText());  
            Hamiltonian.distanceScale = Double.parseDouble(distScaleValue.getText());  
            Hamiltonian.shortDistanceScale = Double.parseDouble(reldistScaleValue.getText());  
            Hamiltonian.beta = Double.parseDouble(betaValue.getText());  
            Hamiltonian.setb( Double.parseDouble(bValue.getText()) );  
            double nmn = Double.parseDouble(minorModelNumberValue.getText());  
            double jmn = Double.parseDouble(majorModelNumberValue.getText());  
            cp.modelNumber.set (jmn,nmn);
//            siteWeightFactor = (int) (Double.parseDouble(sSFValue.getText()) +0.5); 
//            edgeWidthFactor = (int) (Double.parseDouble(eWFValue.getText()) +0.5); 
//            zeroColourFrac = Double.parseDouble(zCFValue.getText()); 
//            minColourFrac = Double.parseDouble(mCFValue.getText()) ; 
//            DisplayMaxVertexScale = Double.parseDouble(DMVSValue.getText()) ; 
//            DisplayMaxEdgeScale = Double.parseDouble(DMESValue.getText()); 
//            influenceProb= Double.parseDouble(ipValue.getText()); 
//            cultureTimeScale= Double.parseDouble(ctsValue.getText()); 
            
            //newNetworkDisplaySyle = NewNetworkStyleCB.isSelected();
                
            
// **************  Do actions in whtever called this, set output file name and calculate            
            
//            setOutputFileName();
//            if (command.equals("CALCULATE")) {
//                
//                Date date = new Date();
//                if (message.getInformationLevel()>-1) System.out.println(date);
//                if (message.getInformationLevel()>0) showFixedSiteVariables("#",3);
//                if (message.getInformationLevel()>0) showDistanceValues("#",3);
//                message.println(2,"Data reading OK, number of sites is "+numberSites);
//                    
//                    if (cp.updateMode == 0) doPPA();
//                    else doMC();
//            }
//            showNetwork("#", 3);
            
        } catch (Exception e) 
        {
         res=10;
         resMessage = "Input Window actionPerformed error: "+e;  
         message.println(-2,resMessage);
//         if (message.getInformationLevel()>=-2) JOptionPane.showMessageDialog(null, s, this.windowName, JOptionPane.ERROR_MESSAGE);
//        infoErrorBox(-2,inputFrame, resMessage,"Input Window Error");         
         
        }
        
    } // eo public void actionPerformed(ActionEvent event)

   

}// eo public class InputParameterWindow implements ActionListener 
