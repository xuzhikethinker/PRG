/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;


//import java.awt.BasicStroke;
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Container;
import java.awt.Dimension;
//import java.awt.Graphics2D;
import java.awt.GridLayout;
//import java.awt.image.BufferedImage;
//import java.awt.Paint;
//import java.awt.Shape;
//import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.awt.geom.Point2D;
//import java.awt.print.PrinterJob;
////import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.PrintStream;

import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
//import javax.imageio.ImageIO;
//import javax.swing.BorderFactory;
//import javax.swing.Box;
import javax.swing.ButtonGroup;
//import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
//import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
//import javax.swing.JSlider;
import javax.swing.JTextField;
//import javax.swing.JToggleButton;
//import javax.swing.border.TitledBorder;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
import javax.swing.SwingConstants;
         


/**
 *  Gives Frame asking for the basic site input file.
 *   <p>Built up from Celcius Converter programme of SWING example web site.
 * <p> This was <tt>InputWindow</tt> class in islandNetwork
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
    ButtonGroup updateModeGroup, updateDVTGroup;
    JRadioButton DPMode,VPMode,PPAMode,MCMode, InfluenceMode, RankMode, SizeMode;
           JLabel SWMMessage;
           JPanel SWMPanel1,SWMPanel2;
           ButtonGroup SWMGroup;
           JRadioButton alphaMode, weightMode, rankMode, rankOverWeightMode;

    
    JLabel messageLabel, drawMessageLabel;
    JButton calcButton, drawButton;
    JCheckBox autonameCB;
    
    String resMessage="OK";

    private islandNetwork inet;
    
    public InputParameterFrame(islandNetwork inetinput) {
        inet = inetinput;  //do not deep copy so can pass values back.
        //Create and set up the window.
        inputFrame = new JFrame("Parameters for "+inet.inputFile.getFullFileRoot()+ " Ariadne "+ islandNetwork.iNVERSION);
        inputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputFrame.setSize(new Dimension(400,400));

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
        
        islandNetwork i = new islandNetwork(1);
                
        fileInputMessage = new JLabel("Basic Input File Name ", SwingConstants.RIGHT);
        fileInputValue = new JLabel(inet.inputFile.getFullFileRoot(), SwingConstants.CENTER);
        inputPanel.add(fileInputMessage);
        inputPanel.add(fileInputValue);
        
        // Buttons for processing mode
        updateModeGroup = new ButtonGroup();
        VPMode = new JRadioButton("VP Mode");
        VPMode.setActionCommand("V");
        VPMode.setToolTipText("Edges set equal to potential");
        DPMode = new JRadioButton("DP Mode");
        DPMode.setActionCommand("D");
        DPMode.setToolTipText("Edges set equal to 1/(1+distance/scale)");
        PPAMode = new JRadioButton("PPA Mode");
        PPAMode.setActionCommand("P");
        PPAMode.setToolTipText("Proximal Point Analysis with edges ranked by distance");
        MCMode = new JRadioButton("MC Mode");
        MCMode.setToolTipText("Monte Carlo mode");
        MCMode.setActionCommand("M");
//        MCMode.setSelected(true);
        updateModeGroup.add(VPMode);
        updateModeGroup.add(DPMode);
        updateModeGroup.add(PPAMode);
        updateModeGroup.add(MCMode);
        JPanel modePanel1 = new JPanel(new GridLayout(1, 2));
        JPanel modePanel2 = new JPanel(new GridLayout(1, 2));
        modePanel1.add(VPMode);
        modePanel1.add(DPMode);
        modePanel2.add(PPAMode);
        modePanel2.add(MCMode);
        inputPanel.add(modePanel1);
        inputPanel.add(modePanel2);
        inputPanel.add(new JLabel(""));
        switch (inet.updateMode.getValue())
        {
            case 0: PPAMode.setSelected(true);  break;
            case 2: DPMode.setSelected(true);  break;
            case 3: VPMode.setSelected(true);  break;
            case 1: 
            default: MCMode.setSelected(true);  
        }
//        if (command.equals("P")) updateMode=0;
//            if (command.equals("M")) updateMode=1;

        
        // Register a listener for the radio buttons.
        RadioListener modeListener = new RadioListener();
        VPMode.addActionListener(modeListener);
        DPMode.addActionListener(modeListener);
        PPAMode.addActionListener(modeListener);
        MCMode.addActionListener(modeListener);
        
        autonameCB= new JCheckBox("Automatic Output File Names");
        autonameCB.setSelected(true);
        inputPanel.add(autonameCB);
        
        
        String edgeModeInitial = ""+i.edgeSet.edgeMode.edgeModeValue();
        edgeModeValue = new JTextField(edgeModeInitial,4);
        edgeModeMessage = new JLabel("Maximum Edge Value ", SwingConstants.RIGHT);
        inputPanel.add(edgeModeMessage);
        inputPanel.add(edgeModeValue);
        
        String maxVertexInitial = ""+i.vertexMode.getModeValue();
        maxVertexValue = new JTextField(maxVertexInitial,4);
        maxVertexMessage = new JLabel("Vertex Mode Value ", SwingConstants.RIGHT);
        maxVertexMessage.setToolTipText("Max. vertex value if positive, minus the max weight if negative");
        maxVertexValue.setToolTipText("Max. vertex value if positive, minus the max weight if negative");
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
        fileOutputValue = new JTextField(inet.outputFile.getBasicRoot(),16);
        fileOutputMessage = new JLabel("Basic Root for Output Files ", SwingConstants.RIGHT);
        inputPanel.add(fileOutputMessage);
        inputPanel.add(fileOutputValue);
        
        runnameValue = new JTextField(""+inet.outputFile.sequenceNumber,16);
        runnameMessage = new JLabel("Run Number ", SwingConstants.RIGHT);
        inputPanel.add(runnameMessage);
        inputPanel.add(runnameValue);
        
        String jInitial = ""+i.Hamiltonian.vertexSource;
        jValue = new JTextField(jInitial,4);
        jMessage = new JLabel("j ", SwingConstants.RIGHT);
        inputPanel.add(jMessage);
        //if (updateMode==0) inputPanel.add(notInPPAlabel); else 
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
        
        if (inet.updateMode.isPPA()) i.Hamiltonian.beta=3.0;
//        else i.Hamiltonian.beta=1.0;
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
        
        // Now list display vraiables
        displayPanel.add(new JLabel("<html><em>Display Variables</em></html>", SwingConstants.CENTER) );
        displayPanel.add(new JLabel("<html><em>Values</em>", SwingConstants.LEFT) );
        
        String sSFInitial = ""+i.siteWeightFactor;
        sSFValue = new JTextField(sSFInitial,4);
        sSFMessage = new JLabel("<html>Maximum Site Size </html>", SwingConstants.RIGHT);
        displayPanel.add(sSFMessage);
        displayPanel.add(sSFValue);
        
        String eWFInitial = " "+i.edgeWidthFactor;
        eWFValue = new JTextField(eWFInitial,4);
        eWFMessage = new JLabel("<html>Maximum Edge Width </html>", SwingConstants.RIGHT);
        displayPanel.add(eWFMessage);
        displayPanel.add(eWFValue);
        
        String zCFInitial = " "+i.zeroColourFrac;
        zCFValue = new JTextField(zCFInitial,4);
        zCFMessage = new JLabel("<html>Fraction for Zero Colour </html>", SwingConstants.RIGHT);
        displayPanel.add(zCFMessage);
        displayPanel.add(zCFValue);
        
        String mCFInitial = " "+i.minColourFrac;
        mCFValue = new JTextField(mCFInitial,4);
        mCFMessage = new JLabel("<html>Fraction for Minimum Colour </html>", SwingConstants.RIGHT);
        displayPanel.add(mCFMessage);
        displayPanel.add(mCFValue);
        
        String DMVSInitial = " "+i.DisplayMaxVertexScale;
        DMVSValue = new JTextField(DMVSInitial,4);
        DMVSMessage = new JLabel("<html>Max. Vertex Size </html>", SwingConstants.RIGHT);
        displayPanel.add(DMVSMessage);
        displayPanel.add(DMVSValue);
        
        String DMESInitial = " "+i.DisplayMaxEdgeScale;
        DMESValue = new JTextField(DMESInitial,4);
        DMESMessage = new JLabel("<html>Max. Edge Size </html>", SwingConstants.RIGHT);
        //DMESMessage.setForeground(Color.CYAN);
        displayPanel.add(DMESMessage);
        displayPanel.add(DMESValue);

        String ipInitial = " "+i.influenceProb;
        ipValue = new JTextField(ipInitial,4);
        ipMessage = new JLabel("<html>Influence Prob.</html>", SwingConstants.RIGHT);
        //DMESMessage.setForeground(Color.CYAN);
        displayPanel.add(ipMessage);
        displayPanel.add(ipValue);
        
        String ctsInitial = " "+i.cultureTimeScale;
        ctsValue = new JTextField(ctsInitial,4);
        ctsMessage = new JLabel("<html>Culture Time</html>", SwingConstants.RIGHT);
        //DMESMessage.setForeground(Color.CYAN);
        displayPanel.add(ctsMessage);
        displayPanel.add(ctsValue);
        
        // Buttons for DisplayVertexType
        updateDVTGroup = new ButtonGroup();
        SizeMode = new JRadioButton("Size");
        SizeMode.setActionCommand("NVSize");
        RankMode = new JRadioButton("Rank");
        RankMode.setActionCommand("NVRank");
//        RankMode.setSelected(true);
        InfluenceMode = new JRadioButton("Influence");
        InfluenceMode.setActionCommand("NVInfluence");
        updateDVTGroup.add(SizeMode);
        updateDVTGroup.add(RankMode);
        updateDVTGroup.add(InfluenceMode);
        DVTPanel = new JPanel(new GridLayout(1, 3));
        DVTPanel.add(SizeMode);
        DVTPanel.add(RankMode);
        DVTPanel.add(InfluenceMode);
        DVTMessage = new JLabel("<html>Display vertices by</html>", SwingConstants.RIGHT);
        displayPanel.add(DVTMessage);
        displayPanel.add(DVTPanel);
        // set set button to be equal to current value
        switch (inet.DisplayVertexType)
        {
            case 2: InfluenceMode.setSelected(true);  break;
            case 1: RankMode.setSelected(true);  break;
            case 0: 
            default: SizeMode.setSelected(true);  
        }

        // Register a listener for the radio buttons.
        //RadioListener DVTmodeListener = new RadioListener();
        SizeMode.addActionListener(modeListener);
        RankMode.addActionListener(modeListener);
        InfluenceMode.addActionListener(modeListener);
        
        //
        //NewNetworkStyleCB = new JCheckBox("New style network display");
        //NewNetworkStyleCB.setSelected(newNetworkDisplaySyle);
        //NewNetworkStyleCB.addActionListener(modeListener);
        //displayPanel.add(new JLabel(" "));
        //displayPanel.add(NewNetworkStyleCB);
        
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
//        RadioListener SWMListener = new RadioListener();
        numMode.addActionListener(modeListener);
        alphaMode.addActionListener(modeListener);
        weightMode.addActionListener(modeListener);
        rankMode.addActionListener(modeListener);
        rankOverWeightMode.addActionListener(modeListener);

        
        /*  modelNumber.major = 1;
             modelNumber.minor = 0;
             message.getInformationLevel() = 0;
             updateMode = 1; // Sweep
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
            if (command.equals("P")) inet.updateMode.setMode(0);
            if (command.equals("M")) inet.updateMode.setMode(1);
            if (command.equals("D")) inet.updateMode.setMode(2);
            if (command.equals("V")) inet.updateMode.setMode(3);
            if (command.equals("NVSize")) inet.DisplayVertexType = 0;
            if (command.equals("NVRank")) inet.DisplayVertexType = 1;
            if (command.equals("NVInfluence")) inet.DisplayVertexType = 2; 
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
            inet.Hamiltonian.beta = Double.parseDouble(betaValue.getText());  
            inet.Hamiltonian.setb( Double.parseDouble(bValue.getText()) );  
            double nmn = Double.parseDouble(minorModelNumberValue.getText());  
            double jmn = Double.parseDouble(majorModelNumberValue.getText());  
            inet.modelNumber.set (jmn,nmn);
            inet.siteWeightFactor = (int) (Double.parseDouble(sSFValue.getText()) +0.5); 
            inet.edgeWidthFactor = (int) (Double.parseDouble(eWFValue.getText()) +0.5); 
            inet.zeroColourFrac = Double.parseDouble(zCFValue.getText()); 
            inet.minColourFrac = Double.parseDouble(mCFValue.getText()) ; 
            inet.DisplayMaxVertexScale = Double.parseDouble(DMVSValue.getText()) ; 
            inet.DisplayMaxEdgeScale = Double.parseDouble(DMESValue.getText()); 
            inet.influenceProb= Double.parseDouble(ipValue.getText()); 
            inet.cultureTimeScale= Double.parseDouble(ctsValue.getText()); 
            
            //newNetworkDisplaySyle = NewNetworkStyleCB.isSelected();
                    
            inet.setOutputFileName();
            if (command.equals("CALCULATE")) {
                
                Date date = new Date();
                if (inet.message.getInformationLevel()>-1) System.out.println(date);
                if (inet.message.getInformationLevel()>0) inet.showFixedSiteVariables("#",3);
                if (inet.message.getInformationLevel()>0) inet.showDistanceValues("#",3);
                //message.println(2,"Data reading OK, number of sites is "+numberSites);
                    
                    switch (inet.updateMode.getValue()){
                        case 0: inet.doPPA(); break;
                        case 1: inet.doMC(); break;
                        case 2: inet.doDP(); break;
                        case 3: inet.doVP(); break;
                    }
            }
            inet.showNetwork("#", 3);
            
        } catch (Exception e) 
        {
         res=10;
         resMessage = "Input Window actionPerformed error: "+e;  
         inet.message.println(-2,resMessage);
         if (inet.message.getInformationLevel()>=-2) JOptionPane.showMessageDialog(inputFrame, resMessage, "Input Parameter Window Error", JOptionPane.ERROR_MESSAGE);
        
        }
        
    } // eo public void actionPerformed(ActionEvent event)

   

}// eo public class InputParametersWindow implements ActionListener 



