/*
**********************************************************************************
 * class SiteWindow
 */
public class SiteWindow extends JPanel {

        islandNetwork islnet;
        int windowSize=500;
        //int siteWindowMode=1;

// Constructor
 public SiteWindow(islandNetwork islnetinput)
 {
        //System.out.println("SiteWindow constructor, islnetinput.inputnameroot " + islnetinput.inputnameroot);
        islnet = new islandNetwork(islnetinput);
        //createAndShowNetWin();
        infoPrint(2,"Finished SiteWindow constructor, islnet.inputnameroot " + islnet.inputnameroot);
        //System.out.println("Finished SiteWindow constructor, islnet.iNVERSION " + islnet.iNVERSION);

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




         public void paint (Graphics ginput)
         {
             Graphics2D g = (Graphics2D) ginput;
             int borderSize =30;
             int [] sitePositionX = new int[numberSites];
             int [] sitePositionY = new int[numberSites];

            // find values for sites
             double dns = (double) islnet.numberSites;
             double siteWeightMax=-1.0;
             double siteRankMax=-1.0;
             double [] sV = new double[islnet.numberSites];
             for (int i=0; i<islnet.numberSites; i++)
             {
              if (siteWeightMax<islnet.siteArray[i].weight) siteWeightMax=islnet.siteArray[i].weight;
              if (siteRankMax<islnet.siteArray[i].ranking) siteRankMax=islnet.siteArray[i].ranking;
              }
             // set window scales
             Dimension d = getSize();
             double wsx = (d.height -borderSize-borderSize) / dns;
             double wsWeight = (d.width -borderSize-borderSize)/ siteWeightMax;
             double wsRank = (d.width -borderSize-borderSize)/ siteRankMax;
             infoPrint(2,"Site Window scales "+wsx+","+wsWeight+","+wsRank+", window = "+d.width+","+d.height);
             infoPrint(2," max site weight ="+siteWeightMax+" max site rank="+siteRankMax);
             Font f = g.getFont();
             infoPrint(2,"Site Window Mode "+islnet.siteWindowMode+", font "+f.getName()+" " +f.getSize()+" "+f.getStyle() );
             double vw=1.0;
             int width;
             int greyness;
             int x,y,w,h;
             String s;
             double numberBars = 2.0;
             width = (int) (wsx/(numberBars))-2;
             g.setColor(Color.red);
             g.drawString("SIZE",borderSize,borderSize);
             g.setColor(Color.blue);
             g.drawString("RANK",borderSize+d.width/2,borderSize);
             for (int iii=0; iii<numberSites; iii++)
             {
                 int i=iii;
                 switch (islnet.siteWindowMode)
                 {
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
                  x = borderSize; // base for left coordinate
                  w = (int) (wsWeight*islnet.siteArray[i].weight); // width of bar
                  h = width; // height of bar
                  infoPrint(2,"Site "+i+" Rank "+siteArray[i].ranking+", Rank rank "+siteArray[i].rankingRank );
                  infoPrint(2,"Site "+i+" Rank "+islnet.siteArray[i].ranking+", Rank rank "+islnet.siteArray[i].rankingRank );
                  infoPrint(2,"Site "+i+" at ("+x+","+y+") width "+w+", height "+h+" ");
                  g.setColor(Color.red);
//                  g.drawLine( x1, y1, x2, y2);
                  g.fill(new Rectangle2D.Double( x,y,  w , h));
                  s = String.valueOf(TruncDec(islnet.siteArray[i].weight,3));
                  g.drawString(s,x+w,y+width/2);
                  // second bar
                  y += width; // base for top coordinate
                  w = (int) (wsRank*islnet.siteArray[i].ranking); // width of bar
                  g.setColor(Color.blue);
//                  g.drawLine( x1, y1, x2, y2);
                  infoPrint(2,"Site "+i+"="+" at ("+x+","+y+") width "+w+", height "+h+" ");
                  g.fill(new Rectangle2D.Double( x,y,  w , h));
                  s= String.valueOf(TruncDec(islnet.siteArray[i].ranking ,3));
                  g.drawString(s,x+w,y+width/2);
                  g.setColor(Color.black);
                  g.drawString(islnet.siteArray[i].name,x,y);

            }// eo for i

             int size=20;


         }// eo paint


     } //eo private class SitePicture extends JPanel


// --- createAndShowNetWin

     public void createAndShowNetWin() {
        //Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        if (infolevel>-1) System.out.println(" Ariadne Site Display "+windowSize);
        JFrame frame = new JFrame(islnet.inputnameroot+" Ariadne Site Display "+ islnet.iNVERSION);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //if (maxX-minX > maxY-minY) windowScale = windowSize/(maxX-minX);
        //else windowScale = windowSize/(maxY-minY);


        Component contents = createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        ParameterBox topBox = new ParameterBox();
        frame.getContentPane().add(topBox.get(islnet), BorderLayout.NORTH);

        OutputMeasuresPanel outputInfo = new OutputMeasuresPanel ();
        frame.getContentPane().add(outputInfo.get(islnet,2), BorderLayout.SOUTH);


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
