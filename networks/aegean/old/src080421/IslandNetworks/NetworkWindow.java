/*
 * NetworkWindow.java
 *
 * Created on 23 August 2006, 13:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;



//import IslandNetworks.JungAdapter;
//import IslandNetworks.ClusteringWindow;
//import IslandNetworks.CWTest;
import TimUtilities.FileUtilities.FileUtilities;
import TimUtilities.FileUtilities.GeneralExtensionFilter;
import TimUtilities.LookAndFeel;
//import TimUtilities.TimMessage;

import java.io.File;


import javax.swing.JFrame;  
import javax.swing.JPanel;  
import javax.swing.Box;  
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//import javax.swing.KeyStroke;



import java.awt.*;
//import java.awt.font.*;
import java.awt.geom.*;
//import java.awt.image.*;
//import java.awt.event.*;

/**
 * Displays a window which includes a graphical representation of the network.
 * @author time
 */

public class NetworkWindow {

    private final String CLUSTERBUTTONTYPE = "CB_"; // use three character code for buttons
    private final String LAYOUTBUTTONTYPE = "LB_";
    private final String ARIADNENETWORKFILEEXTENSION = "anf";
//    NetworkWindowParameters NWP = new NetworkWindowParameters();

    FileUtilities fileUtilities = new FileUtilities();
    
    JFrame frame;
        islandNetwork islnet;
        Dimension windowSize = new Dimension(900,700);

        /* For checkboxes and radioboxes, it is not always
necessary to register listeners. In many cases, you can simply check the state of each
button when you need to know it, using the isSelected() method. 
         */
//        JRadioButtonMenuItem lbperc, lbeb; // layout radio buttons
//        JRadioButtonMenuItem cbgeog, cbcirc, cbfr; // clustering radio buttons


// Constructor
 public NetworkWindow(islandNetwork islnetinput)
 {
        //System.out.println("NetworkWindow constructor, islnetinput.inputnameroot " + islnetinput.inputnameroot);
        islnet = new islandNetwork(islnetinput);
        //createAndShowNetWin();
        islnet.message.println(2,"Finished NetworkWindow constructor, root of file names " + islnet.inputFile.getNameRoot());
        //System.out.println("Finished NetworkWindow constructor, islnet.iNVERSION " + islnet.iNVERSION);

 }// eo constructor NetworkWindow(int ns, islandNetwork islnet)

 
// ---     drawNetworkWindow
     public void drawNetworkWindow() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //System.out.println("Creating Window");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowNetWin();
            }
        });
        }//eo drawNetworkWindow()
 
// --- createAndShowNetWin

     private void createAndShowNetWin() {
        //Set the look and feel.
        LookAndFeel.initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        islnet.message.println(0," Ariadne Network Display "+windowSize);
        frame = new JFrame("Ariadne Network "+islnet.inputFile.getNameRoot()+" "+ islnet.Hamiltonian.parameterString(" "));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //Set the frame icon to an image loaded from a file.
        frame.setIconImage(new ImageIcon("images/pot1616.gif").getImage());
        // can we find out the screen size automatically?
        windowSize = new Dimension(islnet.visualisationWidth,islnet.visualisationHeight);
        frame.setSize(windowSize);
        
        // set up menu
        frame.setJMenuBar(createMenuBar());

        // Set up the main windows as tabbed panes
        ImageIcon icon = createImageIcon("images/rsplat.gif");
      
        JTabbedPane tabbedPane = new JTabbedPane();
        
        //Component contents = createComponents();
        ClusteringWindow cw = new ClusteringWindow(islnet, windowSize);
//        CWTest cw = new CWTest(islnet.edgeSet, islnet.siteArray);

        
        
        //JPanel tabNet = new JPanel();
        Box tabNet = Box.createVerticalBox(); // simple vertical list of objects
        // Now add the information panels above and below
        IslandParameterWindow IPW = new IslandParameterWindow(islnet);
        //tabNet.add(IPW.getParameterPanel());
        tabNet.add(cw);
        //tabNet.add(IPW.getOutputMeasuresPanel(2));
        tabbedPane.addTab("Network", createImageIcon("images/network.gif"), tabNet, "Shows Network");
        tabbedPane.setSelectedIndex(0);

        SitePanel sp = new SitePanel(islnet,islnet.siteWindowMode);
//        // set up Site Panel chooser use getSelectedIndex() to access
//        final JComboBox sitePanelModeChooser = new JComboBox();
//        sitePanelModeChooser.setToolTipText("Choose Edge Selector");
//        for (int i=0; i<clusterType.numberTypes; i++) clusterChooser.addItem(clusterType.getString(i));
//        clusterChooser.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent arg0) {
//                //clusterType.setclusterType(arg0.getActionCommand());
//                //JComboBox cb = (JComboBox)arg0.getSource();
//                //clusterType.setclusterType((String) cb.getSelectedItem());
//                //clusterType.setclusterType((String) clusterChooser.getSelectedItem());
//                clusterType.value = clusterChooser.getSelectedIndex();
//                vv.restart();
//                //start();
//                //setUpView();
//            }
//        });

        tabbedPane.addTab("Sites", createImageIcon("images/rsplat.gif"), sp.createComponents() , "Shows Site Information");
        
        JPanel tabParam = new JPanel(); // simple vertical list of objects
        tabParam.setLayout( new GridLayout(3,2));
        tabParam.add(IPW.getBasicSettingsPanel()); //,JPanel.LEFT_ALIGNMENT);
        tabParam.add(IPW.getInputVariablesPanel()); //,JPanel.LEFT_ALIGNMENT);
        tabParam.add(IPW.getOutputMeasuresPanel(2)); //,JPanel.LEFT_ALIGNMENT);
        tabbedPane.addTab("Parameters", createImageIcon("images/ysplat.gif"), tabParam, "Shows Parameters");
        
        
  
        frame.getContentPane().add(tabbedPane);
        //Size then Display the window.
        frame.pack();
        frame.setVisible(true);
    }   // eo  private void createAndShowNetWin()




     /* Create the menu.
      *
      */
    private JMenuBar createMenuBar()   {
        JMenuBar menuBar;
        JMenu fileMenu, menu, submenu, helpMenu;
        JMenuItem openFileItem,nextFileItem,menuItem,aboutHelpItem,helpHelpItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;

        MenuListener menuLister = new MenuListener();
        // These are used to set up generic radio button sections
        ButtonGroup bg;
        String buttonType;
        JRadioButtonMenuItem rb;
        String buttonName;
        
//        RadioListener rlistener = new RadioListener();
        
//Create the menu bar.
        menuBar = new JMenuBar();

        //Build the file menu.
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("Manipulate the network File");
        menuBar.add(fileMenu);
        
        openFileItem = new JMenuItem ("Open",KeyEvent.VK_O);
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        openFileItem.getAccessibleContext().setAccessibleDescription("Opens a Network File");
        openFileItem.addActionListener(menuLister);
        //menuBar.add(openFileItem);
        fileMenu.add(openFileItem);
        //fileMenu.addActionListener(menuLister);
        //openFileItem = fileMenu.add(openFileItem);

        nextFileItem = new JMenuItem ("Next",KeyEvent.VK_N);
        
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        nextFileItem.getAccessibleContext().setAccessibleDescription("Gets next network file in sequence");
        nextFileItem.addActionListener(menuLister);
        //menuBar.add(nextFileItem);
        fileMenu.add(nextFileItem);
        //fileMenu.addActionListener(menuLister);
        //openFileItem = fileMenu.add(openFileItem);

        

        
        //Build the first menu.
        menu = new JMenu("Network View");
        //menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        //a group of JMenuItems
//        menuItem = new JMenuItem("A text-only menu item", KeyEvent.VK_T);
        menuItem = new JMenuItem("A text-only menu item");
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
        menu.add(menuItem);

        ImageIcon icon = createImageIcon("images/middle.gif");
        menuItem = new JMenuItem("Both text and icon", icon);
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);

        menuItem = new JMenuItem(icon);
        menuItem.setMnemonic(KeyEvent.VK_D);
        menu.add(menuItem);

        //a group of radio button menu items
        menu.addSeparator();
        ButtonGroup group = new ButtonGroup();
        
        rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        rbMenuItem.setSelected(true);
        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Another one");
        rbMenuItem.setMnemonic(KeyEvent.VK_O);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        //a group of check box menu items
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
        cbMenuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(cbMenuItem);

        cbMenuItem = new JCheckBoxMenuItem("Another one");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        menu.add(cbMenuItem);

// *** Clustering is now to be done on the Network Tabbed Window         
//        // Clustering submenu
//        menu.addSeparator();
//        submenu = new JMenu("Clustering");
//        submenu.setMnemonic(KeyEvent.VK_C);
//        
//
//        menu.addSeparator();
//        bg = new ButtonGroup();
//        buttonType = CLUSTERBUTTONTYPE; // use three character code for buttons
//
//        buttonName="Percolation";
//        rb= new JRadioButtonMenuItem(buttonName);
//        rb.setSelected(true);
////        NWP.clusterTypeString = buttonName;
//        rb.setMnemonic(KeyEvent.VK_G);
//        rb.setActionCommand(buttonType+buttonName);
//        rb.addActionListener(rlistener);
//        bg.add(rb);
//        submenu.add(rb);
//
//        buttonName="Edge Betweenness";
//        rb= new JRadioButtonMenuItem(buttonName);
//        rb.setMnemonic(KeyEvent.VK_G);
//        rb.setActionCommand(buttonType+buttonName);
//        rb.addActionListener(rlistener);
//        bg.add(rb);
//        submenu.add(rb);
//
//        // wrap up sub menu
//        menu.add(submenu);
//// end of Cluster submenu
        
        
// *** Layout is now to be done on the Network Tabbed Window         
//        // Layout submenu
//        menu.addSeparator();
//        submenu = new JMenu("Layout");
//        submenu.setMnemonic(KeyEvent.VK_C);
//
//        //a group of radio button menu items
//        menu.addSeparator();
//        bg = new ButtonGroup();
//        buttonType = LAYOUTBUTTONTYPE; 
//        
//        
//        buttonName= "Geographical";
//        rb= new JRadioButtonMenuItem(buttonName);
//        rb.setSelected(true);
////        NWP.clusterTypeString = buttonName;
//        //rb.setMnemonic(KeyEvent.VK_G);
//        rb.setActionCommand(buttonType+buttonName);
//        rb.addActionListener(rlistener);
//        bg.add(rb);
//        submenu.add(rb);
//
//        buttonName = "Circular";
//        rb= new JRadioButtonMenuItem(buttonName);
//        //rb.setMnemonic(KeyEvent.VK_G);
//        rb.setActionCommand(buttonType+buttonName);
//        rb.addActionListener(rlistener);
//        bg.add(rb);
//        submenu.add(rb);
//        
//        buttonName = "Fruchterman-Rheingold";
//        rb= new JRadioButtonMenuItem(buttonName);
//        rb.setMnemonic(KeyEvent.VK_G);
//        rb.setActionCommand(buttonType+buttonName);
//        rb.addActionListener(rlistener);
//        bg.add(rb);
//        submenu.add(rb);
//         
//// wrap up sub menu
//        menu.add(submenu);
//// end of Layout submenu
        
//a submenu
        menu.addSeparator();
        submenu = new JMenu("A submenu");
        //submenu.setMnemonic(KeyEvent.VK_S);

        menuItem = new JMenuItem("An item in the submenu");
//        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        submenu.add(menuItem);

        menuItem = new JMenuItem("Another item");
        submenu.add(menuItem);
        menu.add(submenu);


        //Build second menu in the menu bar.
        menu = new JMenu("Another Menu");
        //menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

//Build the about menu.
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.getAccessibleContext().setAccessibleDescription("Help on Ariadne");
        menuBar.add(helpMenu);

        aboutHelpItem = new JMenuItem ("About",KeyEvent.VK_A);
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        aboutHelpItem.getAccessibleContext().setAccessibleDescription("About Ariadne");
        aboutHelpItem.addActionListener(menuLister);
        //menuBar.add(openFileItem);
        helpMenu.add(aboutHelpItem);
        
        helpHelpItem = new JMenuItem ("Help",KeyEvent.VK_H);
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        helpHelpItem.getAccessibleContext().setAccessibleDescription("Help for Ariadne");
        helpHelpItem.addActionListener(menuLister);
        //menuBar.add(openFileItem);
        helpMenu.add(helpHelpItem);
        
        return menuBar;
    }

      /** Listens to all the menu items. */
    class MenuListener implements ActionListener 
    { 
        public void actionPerformed(ActionEvent event) 
        {
        try
        {
            String command = event.getActionCommand();
            if (command.equals("Open")) {
            
            JFileChooser fc = new JFileChooser("."); // current directory
            //JFileChooser fc = new JFileChooser(islnet.dirname);
            fc.addChoosableFileFilter(new GeneralExtensionFilter(ARIADNENETWORKFILEEXTENSION));
            fc.setAcceptAllFileFilterUsed(false); // disable accept all
	    fc.setDialogTitle("Open Ariadne Network File");
            
            int returnVal = fc.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // User has not cancelled, nor has an error occurred.
                File fl = fc.getSelectedFile();
                islnet = new islandNetwork(fl, islnet);
                islnet.calcNetworkStats();
                createAndShowNetWin() ;
        
                 }            
            }
            
            if (command.equals("Next")) {
            
            islnet.outputFile.setNextExistingSequenceNumber();    
            islnet = new islandNetwork(islnet.outputFile.getFullLocationFile(), islnet);
                islnet.calcNetworkStats();
                createAndShowNetWin() ;            
            }
            if (command.equals("New")) {
            
            islandNetwork islnetnew = new islandNetwork(islnet);
            InputParameterWindow ipw = new InputParameterWindow(islnetnew.inputFile, islnetnew.Hamiltonian, islnetnew.calcParam);
                //createAndShowNetWin() ;            
            }

            if (command.equals("About")) {
                JOptionPane.showMessageDialog(frame, "island Network Version "+islandNetwork.iNVERSION,  "About", JOptionPane.INFORMATION_MESSAGE);
            }

            if (command.equals("Help")) {
                JOptionPane.showMessageDialog(frame, "<html>Look in the <tt>ariadne/docs</tt> folder.</html>",  "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) 
        {
//         res=10;
         String resMessage="Ariadne Error: "+event;   
//         message.println(-1,resMessage);
         JOptionPane.showMessageDialog(frame, resMessage , "Open File Error", JOptionPane.ERROR_MESSAGE);
         //infoMessageBox(-1,infileFrame, resMessage+", file "+inputNameRoot);
                    
        }
        }
    }
    
    
//       /** Listens to all the radio buttons. */
//    class RadioListener implements ActionListener 
//    { 
//        public void actionPerformed(ActionEvent e) 
//        {
//            String command = e.getActionCommand() ;
//            if (command.substring(0,3)==CLUSTERBUTTONTYPE) NWP.clusterTypeString = command.substring(3);
//            if (command.substring(0,3)==LAYOUTBUTTONTYPE)  NWP.layoutTypeString = command.substring(3);            
//        }
//    }
 
        /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = NetworkWindow.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }



} //eo  class NetworkWindow
