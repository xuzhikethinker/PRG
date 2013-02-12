/*
 * ModelNumber.java
 *
 * Created on 03 August 2006, 13:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

/**
 * Describes and encodes the mdels chosen.
 * @author time
 */
public class ModelNumber {
        double number; // model number as a double, do not set this directly
        int major;
        int minor;
        String majorString;
        String minorString;
        boolean bit0; // use these to record bits of the minorModelNumbers
        boolean bit1;
        boolean bit2;

    
    /** Creates a new instance of ModelNumber */
    public ModelNumber() {
        set(5.2);
    }

    /** Creates a new instance of ModelNumber.
     *@param newNumber new model number
     */
    public ModelNumber(double newNumber) {
        set(newNumber);
    }
    
    /** Creates a new instance of ModelNumber by using existing model number.
     *@param old old model number
     */
    public ModelNumber(ModelNumber old) {
        set(old.number);
    }
    
    /** Sets Model Number from a double.
     *@param newNumber new model number
     */
    public void set(double newNumber) {
        number=newNumber;
        major = (int) newNumber;
        minor = (int) ((newNumber - major)*10+0.5);
        bit0 = ( (minor & 1)>0 ?true:false);
        bit1 = ( (minor & 2)>0 ?true:false);
        bit2 = ( (minor & 4)>0 ?true:false);
        setMajorString();
        setMinorString();
     }

    /** Sets Model Number from two doubles.
     *@param j new major model number
     *@param n new minor model number
     */
    public void set(double j, double n) {
        set(j+n/10.0);
     }

    /** Sets Model Number from a String.
     *@param newNumber new model number as string
     */
    public void set(String newNumber) {
        set(Double.parseDouble(newNumber));
     }

    // ----------------------------------------------------------------------
    /**
     * Sets the description string for the major model type.
     * 
     */
    public void setMajorString() 
    {  
        String s="unknown";
        switch (major)
        {
            case 1: s="Standard Hamiltonian";  break;
//            case 2: s="???"; break;
//            case 3: s="!!! Standard Hamiltonian with Island Hopping distances"; break;
//            case 4: s="???"; break;
            case 5: s="Standard Hamiltonian with power site term and separate land/sea populations"; break;
            case 6: s="Gravity Model"; break;
        
            default: s="*** Major Model number"+major+"NOT OPERATIONAL";
        }
        majorString=s ;
    };

    // ----------------------------------------------------------------------
    /**
     * Sets the description string for the minor model type.
     * 
     */
    public void setMinorString() 
    {  
        String s="unknown";
        switch (minor&3)
        {
            case 0: s="no site terms in trade term";  break;
            case 1: s="Supply Side (source site in trade term)";  break;
            case 2: s="Demand Side (target site in trade term)"; break;
            case 3: s="Gravity (source and target site in trade term)"; break;
            }
        if (major ==3) switch (minor &4)
        {
            case 0: s=s+"physical distance used in potential";  break;
            case 4: s=s+"edge potential used in potential";  break;
            }
        minorString=s ;
    };

    
}
