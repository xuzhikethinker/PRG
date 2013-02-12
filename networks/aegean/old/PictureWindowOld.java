// **********************************************************************************
 /*
  * class NetworkWindow.
 */
public class NetworkWindow {

        islandNetwork islnet;
        int windowSize=500;

// Constructor
 public NetworkWindow(islandNetwork islnetinput)
 {
        //System.out.println("NetworkWindow constructor, islnetinput.inputnameroot " + islnetinput.inputnameroot);
        islnet = new islandNetwork(islnetinput);
        //createAndShowNetWin();
        infoPrint(2,"Finished NetworkWindow constructor, islnet.inputnameroot " + islnet.inputnameroot);
        //System.out.println("Finished NetworkWindow constructor, islnet.iNVERSION " + islnet.iNVERSION);

 }// eo constructor NetworkWindow(int ns, islandNetwork islnet)


 public Component createComponents() {

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        if (newNetworkDisplaySyle)
        {
            JungAdapter ja = new  JungAdapter(edgeSet, siteArray);
            JPanel pane = new JPanel();
            pane=ja.getViewer(windowSize,windowSize);
            pane.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        30, //bottom
                                        30) //right
                                        );

            return pane;
        }
// continue in old way
        NetworkPicture pane = new  NetworkPicture(windowSize,windowSize, siteArray, siteWeightFactor, minX, minY, maxX, maxY, numberColours, edgeSet, numberSites, infolevel, DisplayVertexType, javaColour, updateMode, edgeWidthFactor);
        pane.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        30, //bottom
                                        30) //right
                                        );

        return pane;
    }






// --- createAndShowNetWin

     private void createAndShowNetWin() {
        //Set the look and feel.
        initLookAndFeel();

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        if (infolevel>-1) System.out.println(" Ariadne Network Display "+windowSize);
        JFrame frame = new JFrame(islnet.inputnameroot+" Ariadne Network Display "+ islnet.iNVERSION);
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



} //eo private class NetworkWindow
