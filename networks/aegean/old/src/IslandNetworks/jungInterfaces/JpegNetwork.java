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

/**
 *
 * @author time
 */
public class JpegNetwork {
    
        /**
     * Copy the visible part of the JUNG graph to a file as a jpeg image.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     * <br>Doesn't work.  Maybe need to use setDoubleBuffered image options in JUNG visulaisation viewers?
     * @param fileName full file name (including directories) of file without any extension.
     * @param labelString string used to label graph.
     * @param vv a JUNG visualisation viewer with the graph.
     */
    
    public static void writeJPEGImage(String fileName, String labelString, VisualizationViewer vv ) {
        String filenamecomplete =  fileName+".jpg";        
        //System.out.println("Attempting to write jpg file to "+ filenamecomplete);
        
        File file = new File(filenamecomplete);
        
        int width = vv.getWidth();
        int height = vv.getHeight();

        
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();
        vv.paint(graphics);
        graphics.setColor(Color.black);
        graphics.drawString(labelString, 2, height-2);		
        graphics.dispose();
        
        try {
            ImageIO.write(bi, "jpeg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    
    }

}
