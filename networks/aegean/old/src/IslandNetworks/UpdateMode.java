/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

/**
 * Stores update mode used for producing network.
 * @author time
 */
   public class UpdateMode {
        static int UNSET = -13579;
        private int value=UNSET;
    
        
        static String[] shortDescription={"PPA","MC","DP","VP"};
        static String[] longDescription={"Proximal Point Analysis","Monte Carlo","Distance Probability","Hamiltonian Edge Potential"};
        
    /** Creates a new instance of UpdateMode.
     *@param mode integer value referring to mode
     */
    public UpdateMode(int mode) {
        setMode(mode);     
    }

    /** Creates a new instance of UpdateMode.
     *@param s must contain short identifying string at start tro set mode
     */
    public UpdateMode(String s) {
        setMode(s);     
    }

     /** Creates a new instance of UpdateMode by deep copy.
     *@param em existing UpdateMode
      */
    public UpdateMode(UpdateMode em) {
        value = em.value;        
    }

    /**
     * Sets mode if recognises abreviation as start of given string
     * @param v mode integer
     * @return false if problem, true if set mode correctly
     */
    public boolean setMode(int v){
        value=UNSET;
        if ((v<0) || (v>=shortDescription.length)) return false;
        value=v;
        return true;
    }

       /**
        * Sets mode if recognises abreviation as start of given string
        * @param s
        * @return false if problem, true if set mode correctly
        */
       public boolean setMode(String s) {
           for (int m = 0; m < shortDescription.length; m++) {
               if (s.startsWith(shortDescription[m])) {
                   value = m;
                   return true;
               }
           }
           value = UNSET;
           return false;
       }
    
    /**
     * Gets mode value.
     * @return integer representing mode
     */
    public int getValue(){ return value;  }
    
    public String longDescription()
    {
        return(longDescription[value]);
    }

    /**
     * Gives list of short string descriptions of all modes..
     * @param sep separation string
     * @return list of short descriptions separated by sep string
     */
    public String allModesShort(String sep)
    {
        String s=shortDescription[0];
        for (int m=1; m<shortDescription.length;m++) s=s+sep+shortDescription[m];
        return(s);
    }
    /**
     * Gives list of short string descriptions and their numbers of all modes.
     * @param sep1 separation string between description and its number
     * @param sep2 separation string between different modes
     * @return list of short descriptions separated by sep string
     */
    public String allModesShort(String sep1, String sep2)
    {
        String s=shortDescription[0] + sep1+ "0";
        for (int m=1; m<shortDescription.length;m++) s=s+sep2+shortDescription[m]+sep1+m;
        return(s);
    }

    public String shortDescription()
    {
        return(shortDescription[value]);
    }
    
    /**
     * Tests if we have PPA mode
     * @return true if PPA mode, false otherwise
     */
    public boolean isPPA() { return (value==0?true:false);}
    /**
     * Tests if we have MC mode
     * @return true if MC mode, false otherwise
     */
    public boolean isMC() { return (value==1?true:false);}

   }
    
    