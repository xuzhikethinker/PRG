/*
 * SiteWindow.java
 *
 * Created on 23 August 2006, 12:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import IslandNetworks.Vertex.IslandSiteSet;
import IslandNetworks.Vertex.IslandSiteSet;
import IslandNetworks.Vertex.SiteHistogramPanel;
import IslandNetworks.Vertex.VertexTypeSelection;
import IslandNetworks.io.PrintUtilities;
import IslandNetworks.io.imageGraphic;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.awt.*;
//import java.awt.font.*;
//import java.awt.image.*;
//import java.awt.event.*;

import javax.swing.*;
import javax.swing.JScrollPane;



import TimUtilities.NumbersToString;
import TimUtilities.TimMessage;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

/**
 * Displays a windows which includes a graphical display about the sites.
 * @author time
 */
public class SitePanel extends JPanel {

    final String outputFullLocationFileRootSN;
    final String islandNetworkLabel;

    static NumbersToString n2s = new NumbersToString (3);
    //islandNetwork islnet;
    int windowSize=500;
    TimMessage message = new TimMessage(-2);
    boolean infoOn=true;

    IslandSiteSet siteSet;
    int numberSites;
    SiteWindowMode siteWindowMode;
    int [] siteOrderArray ;
    int [] vertexIndexArray = {VertexTypeSelection.WEIGHTINDEX,
                                VertexTypeSelection.PAGERANKINDEX,
                                VertexTypeSelection.INFLUENCEINDEX
                                };
    int jpegCount=0;
    double influenceProbability;

    SiteHistogramPanel siteHistogram;

// Constructor
 public SitePanel(islandNetwork in)
 {
     outputFullLocationFileRootSN = in.outputFile.getFullLocationFileRootSN(); //in.inputFile.getNameRoot();
     islandNetworkLabel = in.getNameString(", ");
     influenceProbability=in.getInfluenceProbability();
     if (in.message.testInformationLevel(-1)) infoOn=false;

     //System.out.println("SiteWindow constructor, islnetinput.inputFile.getNameRoot() " + islnetinput.inputFile.getNameRoot());
     numberSites=in.numberSites;
     siteOrderArray = new int [numberSites];
     for (int iii=0; iii<numberSites; iii++) siteOrderArray[iii] = iii;
     
     siteSet = in.siteSet; // do not deep copy
     //siteRank = in.siteRank;
//     siteAlphabeticalOrder = in.siteAlphabeticalOrder;
//     siteWeightOrder = in.siteWeightOrder;
     siteWindowMode=in.siteWindowMode;   
        //createAndShowNetWin();
        message.println(2,"Finished SiteWindow constructor");
        //System.out.println("Finished SiteWindow constructor, iNVERSION " + iNVERSION);

 }// eo constructor SiteWindow(int ns, islandNetwork islnet)


 public Component createComponents() {

        siteHistogram  = new  SiteHistogramPanel(siteSet,
            siteOrderArray, vertexIndexArray, windowSize);
        siteHistogram.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        30, //bottom
                                        30) //right
                                        );

        JScrollPane histo = new JScrollPane(siteHistogram , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        histo.setPreferredSize(new Dimension(400, 400));
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5,5)); // args leave pixels between components
        panel.add(histo,BorderLayout.CENTER);
        JPanel cp = sitePanelControl();
        panel.add(cp,BorderLayout.EAST);
        return panel;
    } //eo createcomponents





    /**
     * JPanel with controls for site histogram.
     * @return JPanel with controls for site histogram.
     */
     private JPanel sitePanelControl()
     {
//         Box box = Box.createVerticalBox();
         JPanel panel = new JPanel();
         panel.setPreferredSize(new Dimension(150,200));
         panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
         
         // first set up key to bars
         JPanel keyPanel = new JPanel(new GridLayout(4,1));
         keyPanel.setBorder(BorderFactory.createTitledBorder("Bars"));

        // set up vertex type chooser use getSelectedIndex() to access
        final JComboBox vertexChooser = new JComboBox();
        vertexChooser.setToolTipText("Choose information on first bar");
        for (int i=0; i<VertexTypeSelection.numberTypes; i++) vertexChooser.addItem(VertexTypeSelection.name[i]);
        vertexChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                vertexIndexArray[0]=vertexChooser.getSelectedIndex();
                }
        });
        vertexChooser.setSelectedIndex(vertexIndexArray[0]);
        keyPanel.add(vertexChooser);

                 // set up vertex type chooser use getSelectedIndex() to access
        final JComboBox vertexChooser1 = new JComboBox();
        vertexChooser1.setToolTipText("Choose information on second bar");
        for (int i=0; i<VertexTypeSelection.numberTypes; i++) vertexChooser1.addItem(VertexTypeSelection.name[i]);
        vertexChooser1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                vertexIndexArray[1]=vertexChooser1.getSelectedIndex();
            }
        });
        vertexChooser1.setSelectedIndex(vertexIndexArray[1]);
        keyPanel.add(vertexChooser1);

        // set up vertex type chooser use getSelectedIndex() to access
        final JComboBox vertexChooser2 = new JComboBox();
        vertexChooser2.setToolTipText("Choose information on third bar");
        for (int i=0; i<VertexTypeSelection.numberTypes; i++) vertexChooser2.addItem(VertexTypeSelection.name[i]);
        vertexChooser2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                vertexIndexArray[2]=vertexChooser2.getSelectedIndex();
                }
        });
        vertexChooser2.setSelectedIndex(vertexIndexArray[2]);
        keyPanel.add(vertexChooser2);

         
        // set up order of bars
        JPanel orderPanel = new JPanel(new GridLayout(2,1));
        orderPanel.setBorder(BorderFactory.createTitledBorder("Order of Sites"));

         // Now set up order chooser use getSelectedIndex() to access
        final JComboBox orderChooser = new JComboBox();
        orderChooser.setToolTipText("Choose Order of Sites");
        for (int i=0; i<SiteWindowMode.numberTypes; i++) orderChooser.addItem(siteWindowMode.getString(i));
        orderChooser.setSelectedIndex(siteWindowMode.value);
        orderChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                siteWindowMode.value = orderChooser.getSelectedIndex();
                int sss=-1;
                for (int iii=0; iii<numberSites; iii++)
                {
                 switch (siteWindowMode.value)
                 {
                     case 10: sss=siteSet.siteNBetweennessprimeOrder[iii]; break;
                     case 9: sss=siteSet.siteNBetweennessOrder[iii]; break;
                     case 8: sss=siteSet.siteBetweennessprimeOrder[iii]; break;
                     case 7: sss=siteSet.siteInfluenceprimeOrder[iii]; break;
                     case 6: sss=siteSet.siteBetweennessOrder[iii]; break;
                     case 5: sss=siteSet.siteInfluenceOrder[iii]; break;
                     case 4: sss=siteSet.siteRankOverWeightOrder[iii]; break;
                     case 3: sss=siteSet.siteAlphabeticalOrder[iii]; break;
                     case 2: sss=siteSet.siteRankOrder[iii]; break;
                     case 1: sss=siteSet.siteWeightOrder[iii]; break;
                     case 0:
                     default: sss=iii;
                 }
                 siteOrderArray[iii] = sss;
            }

            }
        });
        orderPanel.add(orderChooser);

        // first set up key to bars
        JPanel buttonPanel = new JPanel(new GridLayout(5,1));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Output"));

        // Define jpg button
        final JButton jpgButton = new JButton("JPEG");
        jpgButton.setToolTipText("Writes a jpeg file of site histogram");
        jpgButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                writeJPEGImage();
            }
        });
        buttonPanel.add(jpgButton);

        // Define eps button
        final JButton epsButton = new JButton("EPS");
        epsButton.setToolTipText("Writes a eps file of site histogram");
        epsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //System.out.println("pressed eps button");
                writeEPSImage();
            }
        });
        buttonPanel.add(epsButton);

       // Define print button
        final JButton printButton = new JButton("Print");
        printButton.setToolTipText("Prints a site histogram (partial only)");
        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                PrintUtilities.printComponent(siteHistogram); //produces clipped output

            }
        });
        buttonPanel.add(printButton);

        // Define refresh button
        final JButton refreshButton = new JButton("Refresh");
        refreshButton.setToolTipText("Refreshes display");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                  siteHistogram.repaint();
            }
        });
        buttonPanel.add(refreshButton);

         
         // add all components
         panel.add(keyPanel);
         panel.add(orderPanel);
         panel.add(buttonPanel);

         return panel;
     }

        /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * @param vv a JUNG visualisation viewer with the graph.
     */
    private void writeJPEGImage() {
        int ip = (int) (influenceProbability*1000);
        String filenamecomplete =  outputFullLocationFileRootSN +"_ip"+String.format("%03d",ip)+"_j"+(jpegCount++);
        System.out.println("Attempting to write jpg file to "+ filenamecomplete);
        String labelString="";
        imageGraphic.writeJPEGImage(filenamecomplete, labelString, siteHistogram);
        //writeJPEGDescriptionFile(filenamecomplete);
    }
        /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * @param vv a JUNG visualisation viewer with the graph.
     */
    private void writeEPSImage() {
        int ip = (int) (influenceProbability*1000);
        String filenamecomplete =  outputFullLocationFileRootSN +"_ip"+String.format("%03d",ip)+"_j"+(jpegCount++);
        System.out.println("Attempting to write jpg file to "+ filenamecomplete);
        String labelString="";
        EpsGraphics2D epsg =  new EpsGraphics2D();
        Font f = epsg.getFont();
        int totalPixelWidth=800;
        int borderSize=50;
        Dimension d= new Dimension();
        SiteHistogramPanel.paintHistogram(siteSet, epsg, totalPixelWidth, borderSize,
                                           vertexIndexArray, siteOrderArray);

        imageGraphic.writeEPSImage(filenamecomplete, labelString, epsg, infoOn);
        //writeJPEGDescriptionFile(filenamecomplete);
    }


}
