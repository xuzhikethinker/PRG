/*
 * SitePanel.java
 *
 * Created on 23 August 2006, 12:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import javax.swing.*;          
import java.awt.*;
//import java.awt.font.*;
import java.awt.geom.*;
//import java.awt.image.*;
//import java.awt.event.*;
//import TimUtilities.LookAndFeel;


/**
 * Displays a windows which includes a graphical display about the sites.
 * @author time
 */
public class SitePanelOther extends JPanel {

        private islandNetwork islnet;
        int windowSize=500;
        private int sitePanelMode=0;

// Constructor
 public SitePanelOther(islandNetwork islnetinput, int sitePanelModeInput)
 {
        setMode(sitePanelModeInput);
        //System.out.println("SitePanel constructor, islnetinput.inputFile.getNameRoot() " + islnetinput.inputFile.getNameRoot());
        // Now don't deep copy - assume frame into which this JPanel is put has a master deep copy.
        //islnet = new islandNetwork(islnetinput); 
        islnet = islnetinput;
        //createAndShowNetWin();
        islnet.message.println(2,"Finished SitePanel constructor, islnet.inputFile.getNameRoot() " + islnet.inputFile.getNameRoot());
        //System.out.println("Finished SitePanel constructor, islnet.iNVERSION " + islnet.iNVERSION);

 }// eo constructor SitePanel(int ns, islandNetwork islnet)


 public Component createComponents() {

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        SitePicture pane = new  SitePicture(windowSize,windowSize);
        pane.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        30, //bottom
                                        30) //right
                                        );

        return pane;
    }




     public class SitePicture extends JPanel{
           double windowScale;




         public SitePicture (){
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(400, 400));

         }

         public SitePicture (int xsize, int ysize){
             setOpaque(true);
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(xsize, ysize));

         }

         public void init()
         {
//             setForeground(Color.yellow);
//             setBackground(Color.cyan);
             setSize(new Dimension(800, 400));

         }




         public void paint (Graphics ginput)
         {
             Graphics2D g = (Graphics2D) ginput;
             int borderSize =30;
             int [] sitePositionX = new int[islnet.numberSites];
             int [] sitePositionY = new int[islnet.numberSites];

            // find values for sites
             double dns = (double) islnet.numberSites;
             double siteWeightMax=-1.0;
             double siteRankMax=-1.0;
             double siteRankOverWeightMax=-1.0;
             double [] sV = new double[islnet.numberSites];
             for (int i=0; i<islnet.numberSites; i++)
             {
                 if (siteWeightMax<islnet.siteArray[i].getWeight() ) siteWeightMax=islnet.siteArray[i].getWeight() ;
                 if (siteRankMax<islnet.siteArray[i].ranking) siteRankMax=islnet.siteArray[i].ranking;
                 if (siteRankOverWeightMax<islnet.siteArray[i].rankOverWeight) siteRankOverWeightMax=islnet.siteArray[i].rankOverWeight;
             }
           // set window scales
             Dimension d = getSize();
             double wsx = (d.height -borderSize-borderSize) / dns;
             double wsWeight = (d.width -borderSize-borderSize-d.width/10)/ siteWeightMax;
             double wsRank = (d.width -borderSize-borderSize-d.width/10)/ siteRankMax;
             islnet.message.println(2,"Site Window scales "+wsx+","+wsWeight+","+wsRank+", window = "+d.width+","+d.height);
             islnet.message.println(2," max site weight ="+siteWeightMax+" max site rank="+siteRankMax);
             Font f = g.getFont();
             islnet.message.println(2,"Site Window Mode "+islnet.siteWindowMode+", font "+f.getName()+" " +f.getSize()+" "+f.getStyle() );
             double vw=1.0;
             int width;
             int greyness;
             int x,y,w,h;
             String s;
             double numberBars = 2.0;
             width = (int) (wsx/(numberBars))-2;
             g.setColor(Color.red);
             g.drawString("SIZE",borderSize+d.width/10,borderSize);
             g.setColor(Color.blue);
             g.drawString("RANK",borderSize+(d.width*4)/10,borderSize);
//             g.setColor(Color.green);
//             g.drawString("RANK/Weight",borderSize+(d.width*7)/10,borderSize);
             for (int iii=0; iii<islnet.numberSites; iii++)
             {
                 int i=iii;
                 switch (sitePanelMode)
                 {
                     case 4: i=islnet.siteRank.siteRankOverWeightOrder[iii]; break;
                     case 3: i=islnet.siteAlphabeticalOrder[iii]; break;
                     case 2: i=islnet.siteRank.siteRankOrder[iii]; break;
                     case 1: i=islnet.siteWeightOrder[iii]; break;
                     case 0:
                     default: i=iii;
                 }

                  int ec = (int) (0.49999+islnet.siteArray[i].displaySize);
//                  width = (islnet.edgeWidthFactor*ec)/islnet.numberColours;
                  if (width<1) width=1;
//                  BasicStroke bstroke = new BasicStroke((float) width);
//                  g.setStroke(bstroke);
                  y = borderSize +( (int) (iii* wsx )) +1; // base for top coordinate
                  x = borderSize +d.width/10; // base for left coordinate
                  w = (int) (wsWeight*islnet.siteArray[i].getWeight() ); // width of bar
                  h = width; // height of bar
                  islnet.message.println(2,"Site "+i+" Rank "+islnet.siteArray[i].ranking+", Rank rank "+islnet.siteArray[i].rankingRank );
                  islnet.message.println(2,"Site "+i+" at ("+x+","+y+") width "+w+", height "+h+" ");
                  g.setColor(Color.red);
//                  g.drawLine( x1, y1, x2, y2);
                  g.fill(new Rectangle2D.Double( x,y,  w , h));
                  s = islnet.TruncDecString(islnet.siteArray[i].getWeight() ,3);
                  g.drawString(s,x+w,y+width/2);
                  // second bar
                  y += width; // base for top coordinate
                  w = (int) (wsRank*islnet.siteArray[i].ranking); // width of bar
                  g.setColor(Color.blue);
//                  g.drawLine( x1, y1, x2, y2);
                  islnet.message.println(2,"Site "+i+"="+" at ("+x+","+y+") width "+w+", height "+h+" ");
                  g.fill(new Rectangle2D.Double( x,y,  w , h));
                  s= islnet.TruncDecString(islnet.siteArray[i].ranking ,3);
                  g.drawString(s,x+w,y+width/2);
                  // third bar here in green if needed
                  
                  g.setColor(Color.black);
                  g.drawString(islnet.siteArray[i].name,borderSize,y);

            }// eo for i

             int size=20;


         }// eo paint


     } //eo private class SitePicture extends JPanel


// --- createAndShowNetWin

     public void createAndShowNetWin() {
        // Use look and feel already set
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        islnet.message.println(1," Ariadne Site Display "+windowSize);
        JFrame frame = new JFrame(islnet.inputFile.getNameRoot()+" Ariadne Site Display "+ islnet.iNVERSION);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //if (maxX-minX > maxY-minY) windowScale = windowSize/(maxX-minX);
        //else windowScale = windowSize/(maxY-minY);


        Component contents = createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        IslandParameterWindow IPW = new IslandParameterWindow(islnet);
        frame.getContentPane().add(IPW.getParameterBox(), BorderLayout.NORTH);

        frame.getContentPane().add(IPW.getOutputMeasuresPanel(2), BorderLayout.SOUTH);


        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }   // eo  private void createAndShowNetWin()

//    class RadioListener implements ActionListener
//    {

//    }


// ---     drawSitePanel
     public void drawSitePanel() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //System.out.println("Creating Window");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowNetWin();
            }
        });
        }//eo drawSitePanel()

     /*
      *Set the type of site dipslay to give.
      *@param siteWindowModeInput integer giving display type
      */
     public void setMode (int siteWindowModeInput)
     {
         sitePanelMode = siteWindowModeInput;
     }

     
     
} //eo public class SitePanel
