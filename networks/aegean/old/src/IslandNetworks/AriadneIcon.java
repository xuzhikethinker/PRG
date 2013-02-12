/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import java.awt.Image;
//import java.awt.Toolkit;
import javax.swing.ImageIcon;


/**
 *
 * @author time
 */
public class AriadneIcon {

    public AriadneIcon(){
     
    }
    
    /**
     * Returns 16x16 pot image suitable for Frame icon.
     * @return 16x16 pot image 
     */
    public Image getPot16(){return getFDImage("images/pot16.gif");}
 
    /** Returns an Image or null.
          * Lifted from <code>FrameDemo2.java</code>
          * @param name path to file
          * @return Image for icon
          */
    protected static Image getFDImage(String name) {
        java.net.URL imgURL = AriadneIcon.class.getResource(name);
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            return null;
        }
    }

    
}
