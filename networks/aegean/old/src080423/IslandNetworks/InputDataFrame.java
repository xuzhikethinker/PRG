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
import java.awt.Graphics;
//import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
//import java.awt.image.BufferedImage;
//import java.awt.Paint;
//import java.awt.Shape;
//import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.awt.geom.Point2D;
//import java.awt.print.PrinterJob;
////import java.io.BufferedReader;
import java.io.File;

//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.PrintStream;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
//import javax.imageio.ImageIO;
//import javax.swing.BorderFactory;
import javax.swing.Box;
//import javax.swing.ButtonGroup;
//import javax.swing.ImageIcon;
//import javax.swing.JApplet;
import javax.swing.JButton;
//import javax.swing.JRadioButton;
//import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
//import javax.swing.JSlider;
import javax.swing.JTextField;
//import javax.swing.JToggleButton;
//import javax.swing.border.TitledBorder;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
import javax.swing.SwingConstants;

import TimUtilities.FileUtilities.GeneralEndingFilter;
import TimUtilities.LookAndFeel;

        /** 
         * Main Window for GUI running of islandNetwork.
         * <br>Built from Celcius Converter programme of SWING example web site 
         * <br>This was <tt>IslandNetworkWindow</tt> in islandNetwork.
         * @author time
        */

    public class InputDataFrame extends JPanel implements ActionListener 
    { 
        islandNetwork inet;
        //String  inputnameroot;
        JFrame infileFrame;
        Box contentBox;
        
        JPanel infilePanel,inputPanel,jigglePanel,buttonPanel;
        JTextField fileInputValue,jiggleScaleInputValue ;
        JLabel fileInputMessage,messageLabel,jiggleMessage;
        JButton openButton, readButton;  
        String resMessage="OK";
        JFileChooser fc;
    
    public InputDataFrame(islandNetwork inetinput) {
        // could add browse button and FileDialog box
        //Create and set up the window.
        //inputnameroot = name;
        inet = inetinput;
    }
    
    
/**
 * Draws the input data window.
 */
     public void drawFrame() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //System.out.println("Creating Window");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowIDF();
            }
        });
        }//eo drawNetworkWindow()
 
// --- createAndShowNetWin

     private void createAndShowIDF() {
        //Set the look and feel.
        LookAndFeel.initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        infileFrame = new JFrame("Input File Name - Ariadne " + islandNetwork.iNVERSION);
        infileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        infileFrame.setSize(new Dimension(800,800));

        contentBox = Box.createVerticalBox();
        // *** Can't get images to work
        //contentBox.add(new ariadnePicture(), SwingConstants.CENTER);
        //JLabel pot = new JLabel(new ImageIcon("images/pot11.jpg", "Pot"));
        //contentBox.add(pot, SwingConstants.CENTER);
        
        infilePanel = new JPanel(new GridLayout(2,2));
        infilePanel.add(new JLabel("Basic Input File Name", SwingConstants.CENTER));
        fileInputValue = new JTextField(inet.inputFile.getFullFileRoot(),24);
        infilePanel.add(fileInputValue);
        //contentBox.add(fileInputValue);
        
        infilePanel.add(new JLabel("Jiggle Scale ", SwingConstants.CENTER));
        jiggleScaleInputValue = new JTextField(Double.toString(inet.jiggleScale),4);
        infilePanel.add(jiggleScaleInputValue );
        
        contentBox.add(infilePanel);
        
        
        buttonPanel = new JPanel(new GridLayout(1,2));
        
        //Create a file chooser
        fc = new JFileChooser(".");
        fc.addChoosableFileFilter(new GeneralEndingFilter(islandNetwork.inputFileEnding)); 
            fc.setAcceptAllFileFilterUsed(false); // disable accept all
	    fc.setDialogTitle("Open Ariadne Input File");
            
        
        openButton = new JButton("BROWSE");
        buttonPanel.add(openButton);
        openButton.addActionListener(this);
        
        readButton = new JButton("READ");
        buttonPanel.add(readButton);
        readButton.addActionListener(this);
        
        //Set the default button.
        infileFrame.getRootPane().setDefaultButton(readButton);

        contentBox.add(buttonPanel);


        infileFrame.getContentPane().add(contentBox);
                
        //Display the window.
        infileFrame.pack();
        infileFrame.setVisible(true);
    }


   
    public void actionPerformed(ActionEvent event) {
        //Parse degrees Celsius as a double and convert to Fahrenheit.
        int res=0;
                
        try
        {
            
            String command = event.getActionCommand();
            if (inet.message.getInformationLevel()>2) System.out.println(command);


            boolean tryToReadData = false;
            
            if (event.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(InputDataFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                tryToReadData = true;
                File file = fc.getSelectedFile();
                //String longfilename=file.getName();
                inet.inputFile.setFileLocationSimpleName(file);
            } else {
                inet.message.infoMessageBox(-1,infileFrame, "Can not find file "+inet.inputFile.getNameRoot());
            }
            }// if openbutton

            if (command.equals("READ")) {
                //network = new islandNetwork();
                tryToReadData=true;
                inet.inputFile.setNameRoot(fileInputValue.getText() );
            }
            
            if (tryToReadData)
            {   inet.jiggleScale = Double.parseDouble(jiggleScaleInputValue.getText()); 
                if (inet.message.getInformationLevel()>2) System.out.println(inet.inputFile.getFullFileRoot());
                res = inet.getSiteData();
                if (res<0) {
                    resMessage = "Simple Site Data reading failed - return code "+res+" trying full distance data read"; 
                    inet.message.println(0,resMessage);
                    inet.message.infoMessageBox(0,infileFrame, resMessage+", file "+ inet.inputFile.getNameRoot() );
                }
                else {
                    inet.dataread =true;
                    inet.outputFile.setBasicRoot(inet.inputFile.getBasicRoot());
                    if (inet.message.testInformationLevel(1))
                    {
                        inet.showFixedSiteVariables("#",3);
                        inet.showDistanceValues("#",3);
                    }
                    resMessage="Data reading OK,  number of sites is "+inet.numberSites;
                    inet.message.println(0,resMessage);
                    inet.message.infoMessageBox(-1,infileFrame, resMessage+", file "+inet.inputFile.getNameRoot());
                    // joggle done when data read
                      //if (jiggleScale>0.0) jiggleSiteData(Hamiltonian.distanceScale*jiggleScale);
                    //drawInputWindow();
                }
// now call up the window asking for parameters
                InputParameterFrame iw = new InputParameterFrame(inet);
            } // eo if (tryToReadData)
            
        } catch (Exception e) 
        {
         res=10;
         inet.dataread=false;
         resMessage="Ariadne Error: "+e;   
         inet.message.println(-1,resMessage);
         inet.message.infoMessageBox(-1,infileFrame, resMessage+", file "+inet.inputFile.getNameRoot());
        }        
    } //eo public void actionPerformed(ActionEvent event)


     private class ariadnePicture extends JPanel 
     {
//      Image img = Toolkit.getDefaultToolkit().getImage(URL or file path);
        Image img;

             public ariadnePicture()
              {
                  setMinimumSize(new Dimension(100,100)); //don't hog space
                  setPreferredSize(new Dimension(400, 400));
              }
             
              public void init() {
                  img = Toolkit.getDefaultToolkit().getImage("images/ariadneanddionysos.jpg");
              }

        @Override
              public void paint(Graphics g)
              {
                  g.drawImage(img,0,0,this);
              }
     }// eo private class ariadnePicture 
     


    } // eo InputDataFrame
