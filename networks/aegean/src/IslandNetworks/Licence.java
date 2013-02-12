/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import java.awt.Dimension;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author time
 */
public class Licence {

 static final String citeString="<html><body>Code written by Tim Evans.  "
                + "<br>Concepts developed by Tim Evans, Carl Knappett, and Ray Rivers."
                + "<br>This code is currently copyright protected and not open source."
                + "<br>See <tt>ariadnelicence.html</tt> included with this programme."
                + "<br>You are free to use this code for non commercial purposes provided "
                + "<br>you cite its use. For all other uses please contact Tim Evans."
                + "<br>More information at"
                + "<br><tt>http://theory.ic.ac.uk/time/networks/arch/ariadne.html</tt>."
                + "<br>If you find this useful, please cite one of the following papers:-<ul> "
                + "<li>Carl Knappett, Tim Evans and Ray Rivers,"
                + "<br><em>Modelling Maritime Interaction In The Aegean Bronze Age</em>"
                + "<br>Antiquity <b>82</b> (2008) 1009-1024</li>"
                + "<li>T.S. Evans, R.J. Rivers, C. Knappett, "
                + "<br><em>Interactions In Space For Archaeological Models</em>"
                + "<br>[<a href=\"http://arxiv.org/abs/1102.0251\"><tt>arXiv:1102.0251</tt></a>]</body></html></li></ul>";

 public static void showCiteMessageDialog(JFrame frame){
        JEditorPane citeBox = new JEditorPane();
        citeBox.setContentType("text/html");
        citeBox.setEditable(false);
        citeBox.setPreferredSize(new Dimension(200,200));
        citeBox.setText(Licence.citeString);
//        try {
//                citeBox.setPage(citeString);
//            } catch (IOException e) {
//                System.err.println("Attempted to read bad HTML");
//            }
        //contentBox.add(citeBox);
        JOptionPane.showMessageDialog(frame, citeString,  "ariadne Licence, version "+islandNetwork.iNVERSION, JOptionPane.INFORMATION_MESSAGE);
    }


}
