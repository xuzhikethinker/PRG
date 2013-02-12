/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

import TimUtilities.GeneralMode;

/**
 *
 * @author time
 */
public class TransferMatrixMode extends GeneralMode {
    
      final static String[] shortDescription={"UEUN","NERV","UETD","UERV"};
      final  static String[] longDescription={
       "Raw edge values plus normalised restart vector to ensure normalisation",
       "Raw edge values, tadpoles equal to remaining deficit so normalised",
       "Normalised edge values (restart vector if deadend), no tadpoles",
       "Raw edge values, no tadpoles, unnormalised"};
      final static int DEFAULTMODE =1;
   /** Basic Constructor
     */
    public TransferMatrixMode() {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
    }
   /** Basic Constructor
    * @param modeName short name of mode to be used to set
     */
    public TransferMatrixMode(String modeName) {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
        this.setFromName(modeName);
    }

   /** Basic Constructor
    * @param mode number of mode to be used to set
     */
    public TransferMatrixMode(int mode) {
        setUniqueNameLength(2);
        setUp(shortDescription, longDescription, DEFAULTMODE);
        this.set(mode);
    }


}
