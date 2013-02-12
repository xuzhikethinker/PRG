/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

/**
 * Note that colours run from 0=white to numbercolours=black.
 * @author time
 */
public class PajekColours {
    public static final String [] colours = { "White", "Yellow","Pink", "Cyan", "Orange", "Magenta", "Purple", "Green", "Blue", "Brown", "Black"};
    public static final int numberColours = colours.length-1;
    public static final double ncd = (double) numberColours;
    public PajekColours(){
     }
    
    static public String getGrey(int i)
    { return( getGrey( i/ncd  ));
            }
    /**
     * Returns Pajek Grey Scale.
     * @param s scale between 0 (white) and 1 (black)
     * @return Pajek string for a grey colour
     */
    static public String getGrey(double s)
    {
        int gn = 5*((int) (0.5+s*20.0) );
        if (gn<5) return "White"; // always have this as zero
        if (gn>95) return "Black"; // always have this as one
        String gns = Integer.toString(gn);
            if (gn>90) gns="95";
            if (gn<10) gns = "05";
            return "Gray"+gns;
            }

}
