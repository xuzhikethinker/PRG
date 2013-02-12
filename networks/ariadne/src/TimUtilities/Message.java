/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

import java.io.PrintStream;

/**
 *
 * @author time
 */
public class Message {

    /**
     * Number of messages given.
     */
    int count;
    /**
     * Maximum number of messages before stop issues messages.
     * Counting still continues.
     */
    int countMax;

    /**
     * Prefix for text messages.
     */
    private String prefix="";

    /**
     * Label in prefix for text messages.
     */
    private String label="";

    /**
     * No limit to number of messages given.
     */
    public Message(){countMax =Integer.MAX_VALUE;}
    /**
     * Limits number of messages given.
     * @param c number of messages to give until
     */
    public Message(int c){countMax =c;}
    /**
     * Puts out message.
     * @param PS Printstream such as System.out
     * @param s text to print out preceeded by prefix
     * @see Message#prefix
     */
    public void println(PrintStream PS, String s){
        if ((++count)<countMax) PS.println(prefix+" "+s);
        if (count==countMax) PS.println("!!! no more messages given");
    }
    /**
     * Message on std out.
     * @param s message string
     */
    public void printlnOut(String s){
        println(System.out,s);
    }
    /**
     * Message on std err.
     * @param s message string
     */
    public void printlnErr(String s){
        println(System.err,s);
    }

    /**
     * Puts out message about total number of messages.
     * @param PS Printstream such as System.out
     */
    public void printCount(PrintStream PS){
        PS.println(prefix+", total number of these was "+count);
    }
    /**
     * Puts out message on System.out giving total number of messages.
     */
    public void printCountOut(PrintStream PS){printCount(System.out);}
    /**
     * Puts out message on System.err about total number of messages.
     */
    public void printCountErr(){printCount(System.err);}


    /**
     * Prefix for text messages.
     * @param newLabel label used at start of messages
     * @param newText text used in prefix messages
     */
    public void setLabelAndPrefix(String newLabel,String newText){
        label=newLabel;
        prefix=label+" "+newText;}

    /**
     * Maximum number of messages given.
     * @param mc maximum number of messages to give
     */
    public void setMaximumCount(int mc){countMax=mc;}
    public int getCount(){return count;}

}
