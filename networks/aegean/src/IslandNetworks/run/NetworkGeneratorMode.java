/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.run;

import TimUtilities.GeneralMode;

/**
 *
 * @author time
 */
public class NetworkGeneratorMode extends GeneralMode {

      final static String[] shortDescription={"lattice", "torus2D", "line", "torus1D", "circle"}; //,"DP","XTent"};
      final  static String[] longDescription={"lattice", "torus2D", "line", "torus1D", "circle"}; //"Distance Probability", "XTent"};
      final static int DEFAULTMODE =0;

   /** Basic Constructor
     */
    public NetworkGeneratorMode() {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
    }
   /** Basic Constructor
    * @param modeName short name of mode to be used to set
     */
    public NetworkGeneratorMode(String modeName) {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
        this.setFromName(modeName);
    }

   /** Basic Constructor
    * @param mode number of mode to be used to set
     */
    public NetworkGeneratorMode(int mode) {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
        this.set(mode);
    }


}
