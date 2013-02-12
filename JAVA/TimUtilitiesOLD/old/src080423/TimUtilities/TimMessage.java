/*
 * TimMessage.java
 *
 * Created on 01 November 2006, 10:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimUtilities;

import java.io.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Creates general, warning and error messages depending on the settings of a variable infolevel.
 * Adds a string <tt>linestart</tt> (e.g. a comment character for data files) to messages.
 * @author time
 */
public class TimMessage {
    private int infolevel=0; 
    String lineStart ="";
    String ERROR = "*** ERROR *** ";
    String WARNING = "--- WARNING --- ";
    String Warning = "... Warning ...";
    
    
    /**
     * Creates a new instance of TimMessage.
     *@param infolevelinput value of inflevel parameter.
     */
    public TimMessage(int infolevelinput) {
        infolevel=infolevelinput;
    }
    
    
    
    
    public void setInformationLevel(int infolevelinput)
    {
        infolevel=infolevelinput;
    }
    
    public int getInformationLevel()
    {
        return infolevel;
    }
    
    /**
     * Test parameter against infomation level setting.
     *@param i parmeter to be tested
     *@return true if i \lt = infolevel, false if i \gt 0.
     */
    public boolean testInformationLevel(int i)
    {
        return (i<=infolevel?true:false);
    }
  
    /*
     * Print message to screen if information level setting is high enough.
     *@param minimumInformationLevel minimum value of infolevel parameter if message is to be printed
     *@param message message string
     */
public void println(int minimumInformationLevel, String message)
    {
        if (testInformationLevel(minimumInformationLevel)) System.out.println(lineStart+message);
    }
    
    /*
     * Print message to screen if information level setting is high enough.
     *@param minimumInformationLevel minimum value of infolevel parameter if message is to be printed
     *@param message message string
     */
    public void println(String message)
    {
        System.out.println(lineStart+message);
    }
    
    public void println(PrintStream PS, String message)
    {
        PS.println(lineStart+message);
    }
    
    /*
     * Print error message to screen.
     *@param message message string
     */
    public void printERROR(String message)
    {
        System.out.println(lineStart+ERROR+message);
    }

    /*
     * Print warning message to screen.
     *@param message message string
     */
    public void printWARNING(String message)
    {
        System.out.println(lineStart+WARNING+message);
    }

    /*
     * Print warning message to screen if information level setting is high enough.
     *@param minimumInformationLevel minimum value of infolevel parameter if warning is to be printed
     *@param message message string
     */
        public void printWarning(int minimumInformationLevel, String message)
    {
        println(minimumInformationLevel, Warning+message);
    }

    public void printInformationLevel()
    {
        printInformationLevel(System.out);
    }
    
    public void printInformationLevel(PrintStream PS)
    {
        switch (infolevel)
        {
            case -2: {println(PS,"Quiet Mode - No information: "+infolevel); break;}
            case -1: {println(PS,"Fairly Quiet Mode - Very Little information: "+infolevel); break;}
            case 0:  {println(PS,"Normal Mode - Some information: "+infolevel); break;}
            case 1:  {println(PS,"Light Debugging Mode - Extra information: "+infolevel); break;}
            case 2:  {println(PS,"Full Debugging Mode - Maximum information: "+infolevel); break;}
            default: {println(PS,"Unknown level of information: "+infolevel); }
        }
        
    }
    
    
    public void infoErrorBox(int i, String s, String windowName)
{
  if (getInformationLevel()>=i) JOptionPane.showMessageDialog(null, s, windowName, JOptionPane.ERROR_MESSAGE);
  //  JOptionPane.showMessageDialog(null, "alert", "alert", JOptionPane.ERROR_MESSAGE);
} 

public void infoErrorBox(int i, JFrame parentFrame, String s, String windowName)
{
  if (getInformationLevel()>=i) JOptionPane.showMessageDialog(parentFrame, s, windowName, JOptionPane.ERROR_MESSAGE);
  //  JOptionPane.showMessageDialog(null, "alert", "alert", JOptionPane.ERROR_MESSAGE);
} 

public void infoMessageBox(int i, String s, String windowName)
{
  if (getInformationLevel()>=i) JOptionPane.showMessageDialog(null, s);
} 

public void infoMessageBox(int i, JFrame frame, String s)
{
  if (getInformationLevel()>=i) JOptionPane.showMessageDialog(frame, s);
}    

   
}
