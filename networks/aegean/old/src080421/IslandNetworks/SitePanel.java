/*
 * SiteWindow.java
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

import TimUtilities.NumbersToString;
import TimUtilities.TimMessage;
import TimUtilities.LookAndFeel;

/**
 * Displays a windows which includes a graphical display about the sites.
 * @author time
 */
public class SitePanel extends JPanel {

    final String inputFileNameRoot;
    static NumbersToString n2s = new NumbersToString (3);
        islandNetwork islnet;
        int windowSize=500;
        TimMessage message = new TimMessage(-2);
        
        // following should be replaced by use of an IslandSiteSet
        IslandSite [] siteArray;
        int numberSites;
        islandNetwork.SiteRanking siteRank; 
        int [] siteAlphabeticalOrder;
        int [] siteWeightOrder;  // points in order [i] = no. of i-throws biggest Size
    
        
        int siteWindowMode=3; // 0=numerical, 1=size, 2=Rank, 3=alphabetical 
        

// Constructor
 public SitePanel(islandNetwork in, int swm)
 {
     siteWindowMode=swm;
     if ((siteWindowMode<0) || (siteWindowMode>3)) siteWindowMode=3;
     inputFileNameRoot = in.inputFile.getNameRoot();   
     //System.out.println("SiteWindow constructor, islnetinput.inputFile.getNameRoot() " + islnetinput.inputFile.getNameRoot());
        //islnet = new islandNetwork();
        numberSites=in.numberSites;
        siteArray = in.siteArray; // do not deep copy
        siteRank = in.siteRank;
        siteAlphabeticalOrder = in.siteAlphabeticalOrder;
        siteWeightOrder = in.siteWeightOrder;
        
        //createAndShowNetWin();
        message.println(2,"Finished SiteWindow constructor");
        //System.out.println("Finished SiteWindow constructor, iNVERSION " + iNVERSION);

 }// eo constructor SiteWindow(int ns, islandNetwork islnet)


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




        @Override
         public void paint (Graphics ginput)
         {
             Graphics2D g = (Graphics2D) ginput;
             int borderSize =30;
             int [] sitePositionX = new int[numberSites];
             int [] sitePositionY = new int[numberSites];

            // find values for sites
             double dns = (double) numberSites;
             double siteWeightMax=-1.0;
             double siteRankMax=-1.0;
             double siteRankOverWeightMax=-1.0;
             double [] sV = new double[numberSites];
             for (int i=0; i<numberSites; i++)
             {
                 if (siteWeightMax<siteArray[i].getWeight() ) siteWeightMax=siteArray[i].getWeight() ;
                 if (siteRankMax<siteArray[i].ranking) siteRankMax=siteArray[i].ranking;
                 if (siteRankOverWeightMax<siteArray[i].rankOverWeight) siteRankOverWeightMax=siteArray[i].rankOverWeight;
             }
           // set window scales
             Dimension d = getSize();
             double wsx = (d.height -borderSize-borderSize) / dns;
             double wsWeight = (d.width -borderSize-borderSize-d.width/10)/ siteWeightMax;
             double wsRank = (d.width -borderSize-borderSize-d.width/10)/ siteRankMax;
             message.println(2,"Site Window scales "+wsx+","+wsWeight+","+wsRank+", window = "+d.width+","+d.height);
             message.println(2," max site weight ="+siteWeightMax+" max site rank="+siteRankMax);
             Font f = g.getFont();
             message.println(2,"Site Window Mode "+siteWindowMode+", font "+f.getName()+" " +f.getSize()+" "+f.getStyle() );
             double vw=1.0;
             int width;
             int greyness;
             int x,y,w,h;
             String s;
             double numberBars = 2.0;
             width = (int) (wsx/(numberBars))-2;
             g.setColor(Color.red);
             g.drawString("Weight",borderSize+d.width/10,borderSize);
             g.setColor(Color.blue);
             g.drawString("RANK",borderSize+(d.width*4)/10,borderSize);
//             g.setColor(Color.green);
//             g.drawString("RANK/Weight",borderSize+(d.width*7)/10,borderSize);
             for (int iii=0; iii<numberSites; iii++)
             {
                 int i=iii;
                 switch (siteWindowMode)
                 {
                     case 4: i=siteRank.siteRankOverWeightOrder[iii]; break;
                     case 3: i=siteAlphabeticalOrder[iii]; break;
                     case 2: i=siteRank.siteRankOrder[iii]; break;
                     case 1: i=siteWeightOrder[iii]; break;
                     case 0:
                     default: i=iii;
                 }

                  int ec = (int) (0.49999+siteArray[i].displaySize);
//                  width = (edgeWidthFactor*ec)/numberColours;
                  if (width<1) width=1;
//                  BasicStroke bstroke = new BasicStroke((float) width);
//                  g.setStroke(bstroke);
                  y = borderSize +( (int) (iii* wsx )) +1; // base for top coordinate
                  x = borderSize +d.width/10; // base for left coordinate
                  w = (int) (wsWeight*siteArray[i].getWeight() ); // width of bar
                  h = width; // height of bar
                  message.println(2,"Site "+i+" Rank "+siteArray[i].ranking+", Rank rank "+siteArray[i].rankingRank );
                  message.println(2,"Site "+i+" at ("+x+","+y+") width "+w+", height "+h+" ");
                  g.setColor(Color.red);
//                  g.drawLine( x1, y1, x2, y2);
                  g.fill(new Rectangle2D.Double( x,y,  w , h));
                  s = n2s.toString(siteArray[i].getWeight() );
                  g.drawString(s,x+w,y+width/2);
                  // second bar
                  y += width; // base for top coordinate
                  w = (int) (wsRank*siteArray[i].ranking); // width of bar
                  g.setColor(Color.blue);
//                  g.drawLine( x1, y1, x2, y2);
                  message.println(2,"Site "+i+"="+" at ("+x+","+y+") width "+w+", height "+h+" ");
                  g.fill(new Rectangle2D.Double( x,y,  w , h));
                  s= n2s.toString(siteArray[i].ranking) ;
                  g.drawString(s,x+w,y+width/2);
                  // third bar here in green if needed
                  
                  g.setColor(Color.black);
                  g.drawString(siteArray[i].name,borderSize,y);

            }// eo for i

             int size=20;


         }// eo paint


     } //eo private class SitePicture extends JPanel


// --- createAndShowNetWin

     public void createAndShowNetWin() {
        //Set the look and feel.
        LookAndFeel.initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        message.println(1," Ariadne Site Display "+windowSize);
        JFrame frame = new JFrame(inputFileNameRoot+" Ariadne Site Display "+ islandNetwork.iNVERSION);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //if (maxX-minX > maxY-minY) windowScale = windowSize/(maxX-minX);
        //else windowScale = windowSize/(maxY-minY);


        Component contents = createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        IslandParameterWindow IPW = new IslandParameterWindow(islnet);
        frame.getContentPane().add(IPW.getParameterPanel(), BorderLayout.NORTH);

        frame.getContentPane().add(IPW.getOutputMeasuresPanel(2), BorderLayout.SOUTH);


        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }   // eo  private void createAndShowNetWin()

//    class RadioListener implements ActionListener
//    {

//    }


// ---     drawSiteWindow
     public void drawSiteWindow() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        //System.out.println("Creating Window");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowNetWin();
            }
        });
        }//eo drawSiteWindow()

} //eo public class SiteWindow
