/*
 * InputFileWindow.java
 *
 * Created on 15 December 2006, 11:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import javax.swing.*;          
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;

import TimUtilities.TimMessage;

/**
 * Input File Window.
 * Built up from Celcius Converter programme of SWING example web site   
 * @author time
 */
public class InputFileWindow implements ActionListener 
{
    String inputNameRoot = "test";
    double jiggleScale =-1;
    TimMessage message = new TimMessage(0);
//    int infolevel=message.getInformationLevel();
        //String  inputNameRoot;
        JFrame infileFrame;
        Box contentBox;
        JPanel infilePanel,inputPanel,jigglePanel,buttonPanel;
        JTextField fileInputValue,jiggleScaleInputValue ;
        JLabel fileInputMessage,messageLabel,jiggleMessage;
        JButton calcButton, readButton; //drawButton; 
        String resMessage="OK";
    
    public InputFileWindow(int informationLevel) {
        message.setInformationLevel(informationLevel);
        //infolevel = message.setInformationLevel();
        // could add browse button and FileDialog box
        //Create and set up the window.
        //inputNameRoot = name;
        infileFrame = new JFrame("Input File Name ");
        infileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        infileFrame.setSize(new Dimension(800,800));

        contentBox = Box.createVerticalBox();
        // Can't get it to find the image
        //contentBox.add(new ariadnePicture(), SwingConstants.CENTER);
        contentBox.add(new JLabel("Input File Name Root ", SwingConstants.CENTER));
        fileInputValue = new JTextField(inputNameRoot,16);
        contentBox.add(fileInputValue);
        
        jigglePanel = new JPanel(new GridLayout(1,2));
        jigglePanel.add(new JLabel("jiggle Scale ", SwingConstants.CENTER));
        jiggleScaleInputValue = new JTextField(Double.toString(jiggleScale),4);
        jigglePanel.add(jiggleScaleInputValue );
        contentBox.add(jigglePanel);
        
        
        buttonPanel = new JPanel(new GridLayout(1,2));
        readButton = new JButton("READ");
        buttonPanel.add(readButton);
        //Listen to events from the Convert button.
        readButton.addActionListener(this);
        //Set the default button.
        infileFrame.getRootPane().setDefaultButton(readButton);

//        calcButton = new JButton("CALC");
//        buttonPanel.add(calcButton);
//        //Listen to events from the Convert button.
//        calcButton.addActionListener(this);
//
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
            message.println(2,command);
                
            if (command.equals("READ")) {
                //network = new islandNetwork();
                inputNameRoot = fileInputValue.getText();
                jiggleScale = Double.parseDouble(jiggleScaleInputValue.getText()); 
                message.println(0,"Input file name ="+inputNameRoot+", jiggle scale ="+jiggleScale);;
            }
        } catch (Exception e) 
        {
         res=10;
         resMessage="Ariadne Error: "+e;   
         message.println(-1,resMessage);
         JOptionPane.showMessageDialog(infileFrame, resMessage+", file "+inputNameRoot , "Input File Error", JOptionPane.ERROR_MESSAGE);
         //infoMessageBox(-1,infileFrame, resMessage+", file "+inputNameRoot);
                    
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
                  img = Toolkit.getDefaultToolkit().getImage("./ariadneanddionysos.jpg");
              }

              public void paint(Graphics g)
              {
                  g.drawImage(img,0,0,this);
              }
     }// eo private class ariadnePicture 

   

     
    } // eo InputFileWindow   
