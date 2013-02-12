/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 *
 * @author time
 */
public class MessageWarning extends Message{


    public static final String warningLabel="!!!";
    public static final String warningText="Warning";

     /**
     * No limit to number of messages given.
     */
    public MessageWarning(){
        countMax =Integer.MAX_VALUE;
        setLabelAndPrefix(warningLabel,warningText);
    }

        /**
     * Limits number of messages given.
     * @param c number of messages to give until
     */
    public MessageWarning(int c){
        countMax =c;
        setLabelAndPrefix(warningLabel,warningText);
    }

    
}
