/*
 * PrintableNetwork.java
 *
 * Created on 07 December 2007, 10:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.print.PrinterException;
//import java.awt.print.PageFormat;
import java.awt.Color;
import java.awt.print.Printable;


//import java.awt.print.PrinterJob;

import edu.uci.ics.jung.visualization.VisualizationViewer;


/**
 * Creates a printable network.
 * <p> Uses code from <tt>GraphEditorDemo.java</tt> in the JUNG package.
 * @author time
 */
    
public class PrintableNetwork implements Printable {
    
  private VisualizationViewer vv;
  private String label;
    
  /** Creates a new instance of PrintableNetwork */
    public PrintableNetwork(VisualizationViewer vvinput, String l){
      vv=vvinput;
      label=l;
  }
    
    
   /**
     * Print the visible part of the JUNG graph.
     * <p>Taken from <tt>GraphEditorDemo.java</tt> in the JUNG package.
     */
     public int print(java.awt.Graphics graphics,
            java.awt.print.PageFormat pageFormat, int pageIndex )
            throws java.awt.print.PrinterException {
        if (pageIndex > 0) {
            return (Printable.NO_SUCH_PAGE);
        } else {
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) graphics;
            vv.setDoubleBuffered(false);
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            vv.paint(g2d);
            g2d.setColor(Color.black);
            g2d.drawString(label, 2, vv.getHeight()-2);
            vv.setDoubleBuffered(true);
            return (Printable.PAGE_EXISTS);
        }
     }
     
     }//eo PrintableNetwork
