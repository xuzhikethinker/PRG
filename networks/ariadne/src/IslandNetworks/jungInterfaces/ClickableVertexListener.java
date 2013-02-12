/*
 * ClickableVertexListner.java
 *
 * Created on 06 December 2007, 16:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.UserDatumNumberVertexValue;
import edu.uci.ics.jung.visualization.GraphMouseListener;

import IslandNetworks.Vertex.IslandSiteSet;

/**
 * Class for popping up an information dialog when a vertex is double-clicked.
 *
 * @author  David Weir
 */
public class ClickableVertexListener implements GraphMouseListener {

    IslandSiteSet sites;
    UserDatumNumberVertexValue idKey;


    public ClickableVertexListener(UserDatumNumberVertexValue udnvv, IslandSiteSet s) {
        sites = s;
        idKey = udnvv;
    }


    public void graphClicked(Vertex v, MouseEvent me) {
        // Wait for double click
        if (me.getClickCount() == 2) {
            // work out where to look in the sites[] array
            int siteID = idKey.getNumber(v).intValue();
            String vertexMessage = sites.toString(siteID, ": ", "\n", 4);
            
            JOptionPane.showMessageDialog(me.getComponent(),vertexMessage,
                "Vertex information", JOptionPane.PLAIN_MESSAGE);
        }
    }

    public void graphPressed(Vertex v, MouseEvent me) {
        // Don't care
    }

    public void graphReleased(Vertex v, MouseEvent me) {
        // Don't care
    }
}
