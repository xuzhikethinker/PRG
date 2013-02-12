/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 *
 * @author time
 */
public class MessageError extends Message{

    public static final String errorText="Error";

    public static final String errorLabel="***";

        /**
     * No limits to number of messages given.
     */
    public MessageError(){
        countMax =Integer.MAX_VALUE;
        setLabelAndPrefix(errorLabel,errorText);
    }

        /**
     * Limits number of messages given.
     * @param c number of messages to give until
     */
    public MessageError(int c){
        countMax =c;
        setLabelAndPrefix(errorLabel,errorText);
    }

   
}
