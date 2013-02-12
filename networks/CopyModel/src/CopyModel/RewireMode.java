/*
 * RewireMode.java
 *
 * Created on 16 November 2006, 10:06
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package CopyModel;

/**
 * Sets up the possible Copy Model rewire modes.
 * <p>
 * ToDo: Get the two step modes working
 * @author time
 */
public class RewireMode {
    final static int [] allowedModes  = {0,1,2,3,4,5,6,7,8,14,15,16,17,18};
    int number;
    String modeString;
    boolean trueBentley; // 0
    boolean pseudoBentley; // 1
    boolean randomPseudoBentley; // 2 
    boolean modeBentley; // 0,1,2 
    boolean generalModel; // 3
    boolean multipleSequentialModel; // 4,14, 7,17
    boolean multiplePermutationModel; // 5,15, 6,16
    boolean artefactRewiring=true; // 8,18
    boolean fixedNumberArtefacts; // all
    boolean fixedNumberIndividuals; // all
    boolean activeArtefactList; // 8,18
    boolean activeIndividualList; // all two step modes 14,15,16,17,18
    boolean inactiveIndividualList; // none?
    boolean artefactNeighbourList; // 6,9 do we need list of individuals attached to given vertex
    boolean permutation; // 2,5,6,15,16 do we need a permuation
    boolean twoSteps; // >10, 14,15,16,17,18 are done in two steps, remove then rewire otherwise done simultaneously
//    /**
//     * True if keeping list of which individual was copied.  May only work for modes >3
//     * (modes using {@link CopyModel.CopyModel#rewireList(cern.colt.list.IntArrayList) ).
//     */
//    boolean influenceNetworkOn; //


    /** Creates a new instance of RewireMode.
     *@param newNumber new rewire mode number
     */
    public RewireMode(int newNumber) {
        set(newNumber);
    }
    
    /** Creates a new instance of RewireMode by using existing RewireMode example.
     *@param old old RewireMode
     */
    public RewireMode(RewireMode old) {
        set(old.number);
    }
    
    /** Sets rewire mode Number from a double.
     *@param newNumber new model number
     */
    public void set(double newNumber) {
        number= (int) (newNumber+0.5);
        setModeString();
            trueBentley=false;
            pseudoBentley=false;
            randomPseudoBentley=false;
            modeBentley =false;
            generalModel= false;
            multipleSequentialModel= false;
            multiplePermutationModel= false;
    fixedNumberArtefacts= true;
    fixedNumberIndividuals= true;
    activeIndividualList= false;
    inactiveIndividualList= false;
    artefactNeighbourList = false;
    activeArtefactList= false; // always needs active neighbour lists
    permutation = false;
    artefactRewiring=false; 
    twoSteps=false;
//    influenceNetworkOn=false;
        
            switch (number)
        {
            case 0: trueBentley=true; modeBentley =true; break;
            case 1: pseudoBentley=true; modeBentley =true; break;
            case 2: randomPseudoBentley=true; permutation=true; modeBentley =true; break;
            case 3: generalModel=true; break;
            
            case 14: twoSteps=true; activeIndividualList= true;
            case 4: multipleSequentialModel=true; 
                    break;
            
            case 15: twoSteps=true; activeIndividualList= true;
            case 5: multiplePermutationModel=true; permutation=true; 
                    break;
            
            case 16: twoSteps=true; activeIndividualList= true;
            case 6: multiplePermutationModel=true; permutation=true; 
                    break;
                    
            case 17: twoSteps=true; activeIndividualList= true;
            case 7: multipleSequentialModel=true; 
                    break;
            
            case 18: twoSteps=true; activeIndividualList= true;
            case 8: artefactRewiring=true; artefactNeighbourList=true; activeArtefactList= true; 
                    break;
            
            default: number = 0;
                     System.out.println(" *** Error - rewire mode "+newNumber+" unknown ***");
        }
//        if ((number<3) && (number>-1)) modeBentley =true; else modeBentley =false;
//        if ((number<6) && (number>-1)) {fixedNumberArtefacts=true; fixedNumberIndividuals=true;}
//        else {fixedNumberArtefacts=false; fixedNumberIndividuals=false;}
     }
    

    /** Sets Model Number from a String.
     *@param newNumber new model number as string
     */
    public void set(String newNumber) {
        set(Double.parseDouble(newNumber));
     }
   /** Gets Model Number from a String.
     *@return model number as string
     */
    public int get() { return number;     }


    // ----------------------------------------------------------------------
    /**
     * Gets the description string for the major model type.
     * 
     */
    public String getModeString() 
    {  
        return modeString;
    }
    // ----------------------------------------------------------------------
    /**
     * Sets the description string for the major model type.
    *
     */
    private void setModeString()
    {
        modeString = modeString(number);
    };

    // ----------------------------------------------------------------------
//    /**
//     * Sets the flag used to indicate individual copied from is to be recorded.
//     * <br>Should not let you set flag if mode value incorrect.
//     *@param on value for flag used to indicate recording individual copied from
//     * @return returns value of the flag.
//     */
//    public boolean setCopiedFromOn(boolean on)
//    {
//        if (this.number>3) influenceNetworkOn = on;
//        else influenceNetworkOn = false;
//        return influenceNetworkOn;
//    }

    /**
     * Sets the time scale.
     * <br>Given number of rewirings and number of rewirings per event
     */
    private int setTimeScale(int rwTotal, int rwPerEvent) 
    {  
        return -1;
    };

        // ----------------------------------------------------------------------
    /**
     * Gives the description string for rewire mode.
     *@param n mode number
     *@return String describing mode n
     */
    public String modeString(int n) 
    {  
        String s="unknown";
        switch (n)
        {
            case 0: s="Fisher-Wright (True Bentley, rewire all simultaneously)";  break;
            case 1: s="Pseudo Bentley (in order from individual 0)"; break;
            case 2: s="Pseudo Random Bentley (in order from a different permutation of individuals each generation)"; break;
            case 3: s="Moran Model (individuals at random)"; break;
            case 4: s="Multiple Sequential Model (X individuals in order)"; break;
            case 5: s="Multiple Permutation Model (X individuals from permutation, using all of one permutation)"; break;
            case 6: s="Multiple Different Random Model (X different individuals at random, new permutation each event)"; break;
            case 7: s="Multiple Random Model (X random individuals, may be used twice)"; break;
            case 8: s="Artfact Rewiring (all edges of one active artefact simultaneously"; break;
            case 14: s="Two step Multiple Sequential Model (X individuals in order)"; break;
            case 15: s="Two Step Multiple Permutation Model (X individuals from permutation)"; break;
            case 16: s="Two step Multiple Different Random Model (X different individuals at random, new permutation each event)"; break;
            case 17: s="Two Step Multiple Random Model (X random individuals, may be used twice)"; break;
            case 18: s="Two Step Artefact Rewiring (remove all edges from one artefact then in second step add these edges back)"; break;
            default: System.out.println(" *** Error - rewire mode "+number+" unknown ***");
        }
        return s ;
    }

    
    
}
