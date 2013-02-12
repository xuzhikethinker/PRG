/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TimGraph.Community;

/**
 *
 * @author time
 */
public class QualityNullModel {

    /**
     * Null Model switch.
     * <ul>
     * <li>0 = k_out k_out/W^2</li>
     * <li>1 = k_in k_out/W^2</li>
     * <li>2 = k_out k_in/W^2</li>
     * <li>3 = k_in k_in/W^2</li>
     * <li>4 = pi pi</li>
     * <li>5 = 1/(W^2)</li>
     * </ul>
     * where pi is the normalised page rank vector, the eigenvector of the largest
     * eigenvalue or the limit of A^n (k_in/W)
     */
    protected int number = 0;
    public final static String [] name = {"out-out", "in-out", "out-in", "in-in", "Pi-Pi", "constant"};

    /**
     * Checks to see if needs pi vector.
     * <br>This is the normalised page rank vector, the eigenvector of the largest
     * eigenvalue or the limit of A^n (k_in/W)
     * @return true (false) if (don't) need pi vector
     */
    public boolean usesPiVector(){return ((number==4)?true:false);}
    
    /**
     * Sets number for null model form.
     * @param nullModelSwitch new value for null model switch
     */
    public void setNumber(int newNullModelSwitch){
       if (newNullModelSwitch<0 || newNullModelSwitch>=name.length) {
            System.err.println("!!! null model unchanged, model number "
                    +newNullModelSwitch
                    +" invalid, must be between 0 and "+name.length);
        }
       number=newNullModelSwitch;
    }

    
    /**
     * Gives number of null model.
     * @return null model number.
     */
    public int getNumber(){return number;}
    /**
     * Gets description of null model
     */
    public String getDescription(){return name[number];}

}
