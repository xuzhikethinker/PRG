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
public class NetworkGeneratorSiteMode extends GeneralMode {

      final static String[] shortDescription={"fix", "plaw", "bin"};
      final static String[] longDescription={"fixed", "power law", "binomial"}; 
      final static int DEFAULTMODE =1;

   /** Basic Constructor
     */
    public NetworkGeneratorSiteMode() {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
    }
   /** Basic Constructor
    * @param modeName short name of mode to be used to set
     */
    public NetworkGeneratorSiteMode(String modeName) {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
        this.setFromName(modeName);
    }

   /** Basic Constructor
    * @param mode number of mode to be used to set
     */
    public NetworkGeneratorSiteMode(int mode) {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
        this.set(mode);
    }


}
