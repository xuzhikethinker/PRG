/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Vertex;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author time
 */
public class SiteHistogramPanel extends JPanel {

    IslandSiteSet siteSet;
    int numberSites;
//    SiteWindowMode siteWindowMode;
    int [] siteOrderArray ;
    int [] vertexIndexArray;
    int jpegCount=0;


//         public SiteHistogramPanel (){
//             setMinimumSize(new Dimension(100,100)); //don't hog space
//             setPreferredSize(new Dimension(400, 40*numberSites));
//         }

         public SiteHistogramPanel (IslandSiteSet siteSetInput,
            int [] siteOrderArrayInput ,
            int [] vertexIndexArrayInput, 
                    int xsize)
         {
            siteSet=siteSetInput;
            siteOrderArray=siteOrderArrayInput;
            vertexIndexArray=vertexIndexArrayInput;
            numberSites=siteSet.getNumberSites();

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
             // is VITAL to avoid having window scrolling problems.
             super.paintComponent(g);
             int borderSize =50;
             //Dimension d = getSize();
             SiteHistogramPanel.paintHistogram(siteSet, g,  getSize().width, borderSize, vertexIndexArray, siteOrderArray);

            }

//    /**
// * Paint histogram.
// * <p>Type of information to be displayed are in terms of the
// * indices of {@see IslandNetworks.Vertex.VertexTypeSelection#name}.
// * @param g 2D graphics to paint
// * @param d width and height
// * @param borderSize size of border
// * @param vertexIndexArray list of indices of types to be displayed
// */
//         public static void paintHistogram(IslandSiteSet siteSet, Graphics2D g, Dimension d,
//                                           int [] vertexIndexArray, int [] siteOrderArray,
//                                           SiteWindowMode siteWindowMode){
//             // find values for sites
//             int numberSites= siteSet.getNumberSites();
//             int numberBars=0;
//             for (int b=0; b<vertexIndexArray.length; b++){
//               if (vertexIndexArray[b]==VertexTypeSelection.NOTHINGINDEX || vertexIndexArray[b]<0 || vertexIndexArray[b]>=VertexTypeSelection.numberTypes ) continue;
//               numberBars++;
//             }
//             int  fsize=g.getFont().getSize();
//             int textWidth=fsize*20;
//             int borderSize=fsize*2;
//             Dimension d= new Dimension((siteSet.getNumberSites()+2)*(f.getSize()+2)*numberBars)
//         }
         /**
          * Paint histogram.
          * <p>Derives height dimensions based font height and number of bars
          * needed.  Fits bars and annotations into width given using font width
          * for typical strings.
          * <p>Type of information to be displayed are in terms of the
          * indices of {@see IslandNetworks.Vertex.VertexTypeSelection#name}.
          * @param siteSet
          * @param g 2D graphics to paint
          * @param totalPixelWidth
          * @param borderSize
          * @param vertexIndexArray list of indices of types to be displayed
          * @param siteOrderArray
          */
         public static void paintHistogram(IslandSiteSet siteSet, Graphics2D g, int totalPixelWidth, int borderSize,
                                           int [] vertexIndexArray, int [] siteOrderArray){
            // font size
             FontMetrics fm = g.getFontMetrics();
             int fheight = fm.getHeight();
             int barHeightGap=2;
             int siteHeightGap=4;
             int textWidth=fm.stringWidth(" (XXXXX) XXXXXXXXXXXX 0.00 ");
             int barLabelWidth=fm.stringWidth("00.000");
             int barPixelWidth = totalPixelWidth -borderSize-textWidth-barLabelWidth-borderSize;
             int barPixelHeight=fheight;
             paintHistogram(siteSet, g, 
                 borderSize, textWidth, barPixelWidth, barPixelHeight,   
                 barHeightGap, siteHeightGap, 
                 vertexIndexArray, siteOrderArray);
    }
         /**
          * Paint histogram.
          * <p>Core histogram painting routine.
          * <p>Type of information to be displayed are in terms of the
          * indices of {@see IslandNetworks.Vertex.VertexTypeSelection#name}.
          * @param siteSet set of sites for histogram
          * @param g  2D graphics to paint
          * @param borderSize width to leave round edges
          * @param textWidth width for site text to left of bars.
          * @param barPixelWidth width of each bar
          * @param barPixelHeight height of each bar
          * @param barHeightGap vertical gap between bars
          * @param siteHeightGap vertical gap between sites
          * @param vertexIndexArray b-th entry indicates index of site variable to be used for b-th bar
          * @param siteOrderArray i-th entry is site index of i-th entry from top.
          */
         public static void paintHistogram(IslandSiteSet siteSet, Graphics2D g,
                 int borderSize, int textWidth, int barPixelWidth, int barPixelHeight,
                 int barHeightGap, int siteHeightGap,
                                           int [] vertexIndexArray, int [] siteOrderArray){

             // find values for sites
             int numberSites= siteSet.getNumberSites();
             double dns = (double) numberSites;
             // find all values needed and their maxima
             double [][] barValue = new double[numberSites][vertexIndexArray.length];
             double [] barMaxValue = new double[vertexIndexArray.length];
             Color [] barColour = new Color[vertexIndexArray.length];
             double [] barScale = new double [vertexIndexArray.length];

             double value = -8652357.7;
             int siteValueIndex=-1;
             int numberBars=0;

             for (int b=0; b<vertexIndexArray.length; b++){
               int indexb =vertexIndexArray[b];
               if (indexb==VertexTypeSelection.NOTHINGINDEX || indexb<0 || indexb>=VertexTypeSelection.numberTypes ) continue;
               barMaxValue[numberBars] =-98765432.1;
               siteValueIndex=VertexTypeSelection.siteValueIndex[indexb];
               if (siteValueIndex<0) {
                   System.err.println("!!! Entry "+b+", bar "+numberBars+", v.selector number "+indexb+", "+VertexTypeSelection.name[indexb]+", has bad site index "+siteValueIndex);
                   continue;
               }
               barColour[numberBars]=VertexTypeSelection.COLOUR[indexb];
               for (int sss=0; sss<numberSites; sss++)
                {
                   value=siteSet.getVariable(sss, siteValueIndex);
                   if (value<0) value=0;
                   barValue[sss][numberBars]=value;
                   if (barMaxValue[numberBars]<value) barMaxValue[numberBars]=value;
                 }
                 if (barMaxValue[numberBars]< 1e-6){
                     System.err.println("!!! Bar "+b+" ("+VertexTypeSelection.name[indexb]+") has maximum value to small for display");
                     continue;
                 }
                 barScale[numberBars] = barPixelWidth/ barMaxValue[numberBars];
                 System.out.println("!!! Entry "+b+", bar "+numberBars+", "+VertexTypeSelection.name[indexb]+", site index "+siteValueIndex);
                 numberBars++;
             } // eo for b
             if (numberBars<1) {
                 System.err.println("!!! No bars to put in site histogram");
                 return;
             }

             // now draw everything
             int y=-99;
             String s;
             int w=-99;
             int x = borderSize +textWidth; // base for left coordinate
             FontMetrics fm = g.getFontMetrics();
             int fheight = fm.getHeight();
             int barLabelWidth=fm.stringWidth("00.000");
             int sitePixelHeight = (numberBars*(barPixelHeight+barHeightGap)+siteHeightGap);
             int barTextyshift = (fheight+barPixelHeight)/2;
             int nameTextyshift = (fheight+sitePixelHeight)/2;
             int sss=-1;
             for (int iii=0; iii<numberSites; iii++)
             {
                 sss=siteOrderArray[iii];
                 // draw site information on far left
                 y = borderSize + (iii* sitePixelHeight ) ; // base for top coordinate
                 g.setColor(Color.black);
                 // note this text format with region below used above to set width
                 s = String.format("%12s [%4.2f] ",siteSet.getName(sss),siteSet.getSize(sss));
                 w = fm.stringWidth(s);  // to right justify
                 g.drawString(s,x-w-2,y+nameTextyshift);
                 if (siteSet.isRegionSet()) {
                          s=siteSet.getRegion(sss);
                          if (s.length()>5) s=s.substring(0,5);
                          g.drawString(" ("+s+")",borderSize,y+nameTextyshift);
                 }
                 // now draw in bars
                 for (int bbb=0; bbb<vertexIndexArray.length; bbb++){
                        g.setColor(barColour[bbb]);
                        w = (int) (barValue[sss][bbb] * barScale[bbb]); // width of bar in pixels
                        g.fill(new Rectangle2D.Double(x, y, w, barPixelHeight));
                        s = String.format("%6.3f",barValue[sss][bbb]);
                        g.drawString(s, x + w, y + barTextyshift);
                        y += barPixelHeight+barHeightGap; // set up base for next bar
//                        message.println(2, "Site " + sss + " Rank " + siteSet.getRanking(i) + ", Rank rank " + siteSet.getRankingRank(i));
//                        message.println(2, "Site " + sss + " at (" + x + "," + y + ") width " + w + ", height " + h + " ");
                 } // for bbb
                 g.setColor(Color.LIGHT_GRAY);
                 y+=barHeightGap/2;
                 g.drawLine(borderSize,y, borderSize +textWidth+barPixelWidth+barLabelWidth, y);
            }// eo for iii
         }// eo paintComponent

         /**
          * Counts number of bars in histogram.
          * @return number of bars
          */
         int getNumberBars(){
             int numberBars=0;
             for (int b=0; b<vertexIndexArray.length; b++){
               if (vertexIndexArray[b]==VertexTypeSelection.NOTHINGINDEX || vertexIndexArray[b]<0 || vertexIndexArray[b]>=VertexTypeSelection.numberTypes ) continue;
               numberBars++;
             }
             return numberBars;
         }
}
