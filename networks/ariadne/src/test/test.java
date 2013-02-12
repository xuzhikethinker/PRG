/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.sourceforge.jlibeps.epsgraphics.EpsGraphics2D;

/**
 *
 * @author time
 */
public class test {


    public static void main(String[] args) {

         Graphics2D g = new EpsGraphics2D();
         g.setColor(Color.black);

         // Line thickness 2.
         g.setStroke(new BasicStroke(2.0f));

         // Draw a line.
         g.drawLine(10, 10, 50, 10);

         // Fill a rectangle in blue
         g.setColor(Color.blue);
         g.fillRect(10, 0, 20, 20);
         g.setColor(Color.red);
         /*
         * Constructs a new arc, initialized to the specified location,
         * size, angular extents, and closure type.
         *
         * @param x The X coordinate of the upper-left corner of
         *          the arc's framing rectangle.
         * @param y The Y coordinate of the upper-left corner of
         *          the arc's framing rectangle.
         * @param w The overall width of the full ellipse of which
         *          this arc is a partial section.
         * @param h The overall height of the full ellipse of which this
         *          arc is a partial section.
         * @param start The starting angle of the arc in degrees.
         * @param extent The angular extent of the arc in degrees.
         */
         int x=20;
         int y=50;
         int w=100;
         int h=200;
         int start=0;
         int extent=270;
         g.drawArc(x,y,w,h, start, extent);
         g.setColor(Color.green);
         g.drawRect(x, y, w, h);

         // Get the EPS output.
         String output = g.toString();

        System.out.println(output);
        g.dispose();
        String filenamecomplete="test.eps";
        boolean messageOn=true;

        if (messageOn) System.out.println("Attempting to write to "+ filenamecomplete);

        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            PS.print(output);
            try
            {
               fout.close ();
               if (messageOn) System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+ filenamecomplete);
            return;
        }


    }
}
