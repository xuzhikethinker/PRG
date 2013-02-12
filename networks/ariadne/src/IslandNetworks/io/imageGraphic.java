/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

/**
 * Routines to get copies of screen output
 * @author time
 */
public class imageGraphic {



    /**
     * Saves component to jpeg file.
     * <p>Filename is <em>fileName</e,><tt>.jpeg</tt>.
     * Uses preferred size of component to set size of file.
     * {@see #writeJPEGImage(String fileName, String labelString, Component component, int width, int height)}
     * @param fileName name of file without extension.
     * @param labelString string to add to image, none added if null
     * @param component component whose paint routines will create image
     */
        public static void writeJPEGImage(String fileName, String labelString, JComponent component) {
            writeJPEGImage(fileName, labelString, component, component.getWidth(), component.getHeight());
        }
   /**
     * Saves component to jpeg file.
     * <p>Filename is <em>fileName</e,><tt>.jpeg</tt>
     * @param fileName name of file without extension.
     * @param labelString string to add to image, none added if null
     * @param component component whose paint routines will create image
     * @param width width of image
     * @param height height of image
     */
        public static void writeJPEGImage(String fileName, String labelString, JComponent component, int width, int height) {
        String filenamecomplete =  fileName+".jpg";
        //System.out.println("Attempting to write jpg file to "+ filenamecomplete);

        File file = new File(filenamecomplete);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();
        component.paint(graphics);

        if (labelString!=null){
            graphics.setColor(Color.black);
            graphics.drawString(labelString, 2, height-2);
            }

        try {
//       // and save out the BufferedImage
//          ImageIO.write(bi, "jpg", new file( ... ));
            ImageIO.write(bi, "jpeg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        graphics.dispose();
    }

    /**
     * Writes component as eps file.
     * <p>Filename is <em>fileName</e,><tt>.eps</tt>.  Uses component.paint()
     * @param fileName name of file without extension.
     * @param labelString string to add to image, none added if null
     * @param component component whose paint routines will create image
     */
        public static void writeEPSImage(String fileName, String labelString,  JComponent vv, boolean infoOn) {
        String filenamecomplete =  fileName+".eps";
        if (infoOn) System.out.println("Attempting to write general information to "+ filenamecomplete);
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            writeEPSImage(PS, labelString,  vv);
            try
            {
               fout.close ();
               if (infoOn) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.err.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }

    /**
     * Produces EPS file from component.paint().
     * @param PS Printstream for the output
     * @param labelString string to add to image, none added if null
     * @param vv Component whose paint() is to be used
     */
      static public void writeEPSImage(PrintStream PS, String labelString, JComponent vv){
        EpsGraphics2D g =  new EpsGraphics2D(); //= bi.createGraphics();
        Dimension oldPS = vv.getPreferredSize();
        System.out.println("JComponent size="+vv.getWidth()+" x "+vv.getHeight() );
        System.out.println("JComponent pref.size="+oldPS.width+" x "+oldPS.height );
        vv.setPreferredSize(vv.getSize());
        vv.paint( g );
        Rectangle r=g.getClipBounds();
        if (r==null) System.out.println("no EPS clip size");
          else System.out.println("EPS clip size="+r.width+  " x " + r.height);
        if (labelString!=null){
            g.setColor(Color.black);
            g.drawString(labelString, 2, vv.getHeight()-2);
            }
       // Get the EPS output.
        String output = g.toString();
        PS.print(output);
        g.dispose();
        vv.setPreferredSize(oldPS);
    }

          /**
     * Writes component as eps file.
     * <p>Filename is <em>fileName</e,><tt>.eps</tt>.  Uses component.paint()
     * @param fileName name of file without extension.
     * @param labelString string to add to image, none added if null
     * @param component component whose paint routines will create image
     */
        public static void writeEPSImage(String fileName, String labelString, EpsGraphics2D g ,  boolean infoOn) {
        String filenamecomplete =  fileName+".eps";
        if (infoOn) System.out.println("Attempting to write general information to "+ filenamecomplete);
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            if (labelString!=null){
            g.setColor(Color.black);
            g.drawString(labelString, 2, g.getFont().getSize()+12);
            }
           // Get the EPS output.
            String output = g.toString();
            PS.print(output);
            g.dispose();
            try
            {
               fout.close ();
               if (infoOn) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.err.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;

    }


}
