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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.awt.*;
//import java.awt.font.*;
import java.awt.geom.*;
//import java.awt.image.*;
//import java.awt.event.*;

import javax.swing.*;
import javax.swing.JScrollPane;



import TimUtilities.NumbersToString;
import TimUtilities.TimMessage;

/**
 * Displays a windows which includes a graphical display about the sites.
 * @author time
 */
public class SitePanel extends JPanel {

    final String inputFileNameRoot;
    static int smallC =90;
    static int largeC = 210;
    static Color WEIGHTCOLOR = new Color(largeC, smallC, smallC); //new Color(216, 134, 134); //Color.RED;
    static Color RANKCOLOR = new Color(smallC, smallC, largeC); //new Color(134, 134, 216); //Color.BLUE;
    static Color INFLCOLOR = new Color(smallC, largeC, smallC); //new Color(134, 216, 134); //Color.GREEN;
    
    static NumbersToString n2s = new NumbersToString (3);
        //islandNetwork islnet;
        int windowSize=500;
        TimMessage message = new TimMessage(-2);
        
        // following should be replaced by use of an IslandSiteSet
        IslandSiteSet siteSet;
        int numberSites;
        //islandNetwork.SiteRanking siteRank; 
//        int [] siteAlphabeticalOrder;
//        int [] siteWeightOrder;  // points in order [i] = no. of i-throws biggest Size
    
        
         SiteWindowMode siteWindowMode; 
         
         JCheckBox weightCB, rankCB, inflCB;
         
        

// Constructor
 public SitePanel(islandNetwork in)
 {
     inputFileNameRoot = in.inputFile.getNameRoot();   
     //System.out.println("SiteWindow constructor, islnetinput.inputFile.getNameRoot() " + islnetinput.inputFile.getNameRoot());
     numberSites=in.numberSites;
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

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        SitePicture siteHistogram  = new  SitePicture(windowSize);
        siteHistogram.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        30, //bottom
                                        30) //right
                                        );

        JScrollPane pane = new JScrollPane(siteHistogram , JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setPreferredSize(new Dimension(400, 400));
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5,5)); // args leave pixels between components
        panel.add(pane,BorderLayout.CENTER);
        JPanel cp = sitePanelControl();
        panel.add(cp,BorderLayout.EAST);
        return panel;
    }




     public class SitePicture extends JPanel{
           double windowScale;




         public SitePicture (){
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(400, 40*numberSites));

         }

         public SitePicture (int xsize){
             setOpaque(true);
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(xsize, 40*numberSites));
         }

         public void init()
         {
//             setForeground(Color.yellow);
//             setBackground(Color.cyan);
             setSize(new Dimension(800, 400));

         }




        @Override
         //public void paint (Graphics ginput)
         protected void paintComponent(Graphics ginput){
             Graphics2D g = (Graphics2D) ginput;
             // This next line and making this function a paintComponent (not a paint)
             // is VITAL to avoid having windo scrolling problems.
             super.paintComponent(g);
             int borderSize =50;
//             int [] sitePositionX = new int[numberSites];
//             int [] sitePositionY = new int[numberSites];

            // find values for sites
             double dns = (double) numberSites;
             // These variables now in SiteSet and all set by calcStats
             //double siteSet.siteWeightStats.maximum=siteSet.siteWeightStats.maximum;
             //double siteSet.siteInflMax=-1.0;
             //double siteSet.siteRankMax=-1.0;
             //double siteSet.siteRankOverWeightMax=-1.0;
//             double [] sV = new double[numberSites];
//             for (int i=0; i<numberSites; i++)
//             {
//                 //if (siteSet.siteWeightStats.maximum<siteSet.getWeight(i) ) siteSet.siteWeightStats.maximum=siteSet.getWeight(i) ;
//                 if (siteSet.siteInflMax<siteSet.getTotalInfluenceWeight(i) ) siteSet.siteInflMax=siteSet.getTotalInfluenceWeight(i)  ;
//                 if (siteSet.siteRankMax<siteSet.ranking(i)) siteSet.siteRankMax=siteSet.ranking(i);
//                 if (siteSet.siteRankOverWeightMax<siteSet.rankOverWeight(i)) siteSet.siteRankOverWeightMax=siteSet.rankOverWeight(i);
//             }
             
           // set window scales
             Dimension d = getSize();
             int textWidth=d.width/7;
             double wsx = (d.height -borderSize-borderSize) / dns;
             double wsWeight = (d.width -borderSize-borderSize-textWidth)/ siteSet.siteWeightStats.maximum;
             double wsInfl = (d.width -borderSize-borderSize-textWidth)/ siteSet.siteInfluenceStats.maximum;
             double wsRank = (d.width -borderSize-borderSize-textWidth)/ siteSet.siteRankStats.maximum;
             message.println(2,"Site Window scales "+wsx+","+wsWeight+","+wsRank+", window = "+d.width+","+d.height);
             message.println(2," max site weight ="+siteSet.siteWeightStats.maximum+" max site rank="+siteSet.siteRankStats.maximum);
             Font f = g.getFont();
             message.println(2,"Site Window Mode "+siteWindowMode.getCurrentTypeString()+", font "+f.getName()+" " +f.getSize()+" "+f.getStyle() );
//             double vw=1.0;
             int width;
//             int greyness;
             int x,y,w,h;
             String s;
             double numberBars = 0.0;
             if (weightCB.isSelected()) numberBars++;
             if (rankCB.isSelected()) numberBars++;
             if (inflCB.isSelected()) numberBars++;
             if (numberBars<1) numberBars =1.0;
             width = (int) (wsx/(numberBars))-2;
//             Color bc = g.getBackground();
//             g.setBackground(Color.LIGHT_GRAY);
//             g.setColor(WEIGHTCOLOR);
//             g.drawString("Weight",borderSize+d.width/10,borderSize);
//             g.setColor(RANKCOLOR);
//             g.drawString("Rank",borderSize+(d.width*4)/10,borderSize);
//             g.setColor(Color.green);
//             g.drawString("RANK/Weight",borderSize+(d.width*7)/10,borderSize);
//             g.setColor(bc);
             for (int iii=0; iii<numberSites; iii++)
             {
                 int i=iii;
                 switch (siteWindowMode.value)
                 {
                     case 5: i=siteSet.siteInfluenceOrder[iii]; break;
                     case 4: i=siteSet.siteRankOverWeightOrder[iii]; break;
                     case 3: i=siteSet.siteAlphabeticalOrder[iii]; break;
                     case 2: i=siteSet.siteRankOrder[iii]; break;
                     case 1: i=siteSet.siteWeightOrder[iii]; break;
                     case 0:
                     default: i=iii;
                 }

//                  int ec = (int) (0.49999+siteSet[i].displaySize);
//                  width = (edgeWidthFactor*ec)/numberColours;
                  if (width<1) width=1;
//                  BasicStroke bstroke = new BasicStroke((float) width);
//                  g.setStroke(bstroke);
                  y = borderSize +( (int) (iii* wsx )) +1; // base for top coordinate
                  x = borderSize +textWidth; // base for left coordinate
                  h = width; // height of bar
                  
                 // First bar
                 if (weightCB.isSelected()) {
                     w = (int) (wsWeight * siteSet.getWeight(i)); // width of bar
                     g.setColor(WEIGHTCOLOR);
                     g.fill(new Rectangle2D.Double(x, y, w, h));
                     s = n2s.toString(siteSet.getWeight(i));
                     g.drawString(s, x + w, y + width / 2);
                     message.println(2, "Site " + i + " Rank " + siteSet.getRanking(i) + ", Rank rank " + siteSet.getRankingRank(i));
                     message.println(2, "Site " + i + " at (" + x + "," + y + ") width " + w + ", height " + h + " ");
                 }

                 // second bar
                 if (rankCB.isSelected()) {
                     y += width; // base for top coordinate
                     w = (int) (wsRank * siteSet.getRanking(i)); // width of bar
                     g.setColor(RANKCOLOR);
                     g.fill(new Rectangle2D.Double(x, y, w, h));
                     s = n2s.toString(siteSet.getRanking(i));
                     g.drawString(s, x + w, y + width / 2);
                     message.println(2, "Site " + i + "=" + " at (" + x + "," + y + ") width " + w + ", height " + h + " ");
                 }

                 // third bar here in green if needed
                 if (inflCB.isSelected()) {
                     y += width; // base for top coordinate
                     w = (int) (wsInfl * siteSet.getTotalInfluenceWeight(i)); // width of bar
                     g.setColor(INFLCOLOR);
                     message.println(2, "Site " + i + "=" + " at (" + x + "," + y + ") width " + w + ", height " + h + " ");
                     g.fill(new Rectangle2D.Double(x, y, w, h));
                     s = n2s.toString(siteSet.getTotalInfluenceWeight(i));
                     g.drawString(s, x + w, y + width / 2);
                 }

                  
                  
                  g.setColor(Color.black);
                  g.drawString(siteSet.getName(i),borderSize+50,y+width);
                  if (siteSet.isRegionSet()) {
                      g.setColor(Color.black);
                      s=siteSet.getRegion(i);
                      if (s.length()>5) s=s.substring(0,5);
                      g.drawString(" ("+s+")",borderSize,y+width);
                  }
                  //s= s+" ("+siteSet[i].region+")";
                  //g.drawString(s,borderSize,y+((int) (0.5+ width*numberBars)));
                    //g.drawString(s,borderSize,y+width);

            }// eo for i

//             int size=20;


         }// eo paint


     } //eo private class SitePicture extends JPanel


    
     private JPanel sitePanelControl()
     {
//         Box box = Box.createVerticalBox();
         JPanel panel = new JPanel();
         panel.setPreferredSize(new Dimension(100,200));
         //panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
         
         // first set up key
         JPanel keyPanel = new JPanel(new GridLayout(4,1));
         keyPanel.add(new JLabel("Key (On?)", JLabel.CENTER));
         
         
//         JLabel jl = new JLabel("Weight", JLabel.CENTER);
//         jl.setForeground(WEIGHTCOLOR);
//         keyPanel.add(jl);
         weightCB = new JCheckBox("Weight", true);
         weightCB.setForeground(WEIGHTCOLOR);
         weightCB.setToolTipText("Weight Values Displayed");
         keyPanel.add(weightCB);
             
//         jl = new JLabel("Rank", JLabel.CENTER);
//         jl.setForeground(RANKCOLOR);
//         keyPanel.add(jl);
         rankCB = new JCheckBox("Rank",true);
         rankCB.setForeground(RANKCOLOR);
         rankCB.setToolTipText("Weight Values Displayed");
         keyPanel.add(rankCB);
             
         
//         jl = new JLabel("Influence", JLabel.CENTER);
//         jl.setForeground(INFLCOLOR);
//         keyPanel.add(jl);
         inflCB = new JCheckBox("Influence",false);
         inflCB.setForeground(INFLCOLOR);
         inflCB.setToolTipText("Weight Values Displayed");
         keyPanel.add(inflCB);
             
         
         // Now set up order chooser use getSelectedIndex() to access
        final JComboBox orderChooser = new JComboBox();
        orderChooser.setToolTipText("Choose Order of Sites");
        for (int i=0; i<SiteWindowMode.numberTypes; i++) orderChooser.addItem(siteWindowMode.getString(i));
        orderChooser.setSelectedIndex(siteWindowMode.value);
        orderChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                siteWindowMode.value = orderChooser.getSelectedIndex();
            }
        });

         
         
         // add all components
         panel.add(keyPanel);
         panel.add(orderChooser);
         
         return panel;
     }
     

} //eo public class SiteWindow
