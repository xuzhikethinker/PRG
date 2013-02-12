/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

import java.io.File;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Dimension;

/**
 *
 * @author time
 */
public class JpegNetwork {
    
        /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * <br>Doesn't work.  Maybe need to use setDoubleBuffered image options in JUNG visualisation viewers?
     * @param fileName full file name (including directories) of file without any extension.
     * @param labelString string used to label graph.
     * @param vv a JUNG visualisation viewer with the graph.
     */

    public static void writeJPEGImage(String fileName, String labelString, VisualizationViewer vv) {
        int width = vv.getWidth();
        int height = vv.getHeight();
        writeJPEGImage(fileName, labelString, vv, width, height);
    }

    /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * <br>Doesn't work.  Maybe need to use setDoubleBuffered image options in JUNG visualisation viewers?
     * @param fileName full file name (including directories) of file without any extension.
     * @param labelString string used to label graph.
     * @param vv a JUNG visualisation viewer with the graph.
     * @link http://jung.sourceforge.net/doc/JUNGVisualizationGuide.html
     */

    public static void writeJPEGImage(String fileName, String labelString, VisualizationViewer vv, int width, int height) {
        String filenamecomplete =  fileName+".jpg";        
        //System.out.println("Attempting to write jpg file to "+ filenamecomplete);
        
        File file = new File(filenamecomplete);

        // use double buffering until now
        // turn it off to capture
        vv.setDoubleBuffered( false );
//       // capture: create a BufferedImage
//       // create the Graphics2D object that paints to it
//       vv.paintComponent( replaced_graphics2D )

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();

        vv.paint(graphics);
        graphics.setColor(Color.black);
        graphics.drawString(labelString, 2, height-2);		
        graphics.dispose();
        
        try {
//       // and save out the BufferedImage
//          ImageIO.write(bi, "jpg", new file( ... ));
            ImageIO.write(bi, "jpeg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        vv.setSize(widthVV, heightVV);

       // turn double buffering back on
       vv.setDoubleBuffered( true );

    
    }

}
