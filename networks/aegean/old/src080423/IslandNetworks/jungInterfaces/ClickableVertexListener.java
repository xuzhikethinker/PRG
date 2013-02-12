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

import IslandNetworks.Vertex.IslandSite;

/**
 * Class for popping up an information dialog when a vertex is double-clicked.
 *
 * @author  David Weir
 */
public class ClickableVertexListener implements GraphMouseListener {

    IslandSite[] sites;
    UserDatumNumberVertexValue idKey;


    public ClickableVertexListener(UserDatumNumberVertexValue udnvv, IslandSite[] s) {
        sites = s;
        idKey = udnvv;
    }


    public void graphClicked(Vertex v, MouseEvent me) {
        // Wait for double click
        if (me.getClickCount() == 2) {
            // work out where to look in the sites[] array
            int siteID = idKey.getNumber(v).intValue();


            // build the message from all the fields
            String vertexMessage = "";
            int numberSiteVariables = sites[0].getNumberVariables();
            for(int i=0;i<numberSiteVariables;i++)
                vertexMessage = vertexMessage + 
                    sites[siteID].dataName(i) + ": " + sites[siteID].toShortDoubleString(i,4) + "\n";

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
