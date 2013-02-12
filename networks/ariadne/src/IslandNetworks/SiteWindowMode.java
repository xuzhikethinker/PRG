/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;


/**
 * Sets up modes for the site window display.
 * @author time
 */
public class SiteWindowMode {
                     
    
 public final static String[] SiteWindowModeArray =  {"Numerical",
                                                 "Weight",
                                                 "Rank",
                                                 "Alphabetical",
                                                 "Rank/Weight",
                                                 "Influence",
                                                 "Betweenness",
                                                 "Influence\'",
                                                 "Betweenness\'",
                                                 "NBetweenness",
                                                 "NBetweenness\'"};
 public static final int numberTypes = SiteWindowModeArray.length;
 public int value=0;

public SiteWindowMode(){
 value=1;   
}

public SiteWindowMode(int m){
 value=m;   
}

            /** Sets numerical code for mode from string , -1 if not known.
             * @param s name of mode
     */
    public void setSiteWindowMode(String s)
    {
        for (int i=0; i<SiteWindowModeArray.length; i++)
            if (SiteWindowModeArray[i].startsWith(s.substring(0,1))) {value=i; return;}
        value=-1;
        return;
    }
    
   /** Returns string for mode i.
     *@param i the site window mode number.
     *@return string describing the site window mode
     */
     public String getString(int i){
     if ((i<0) || (i>=SiteWindowModeArray.length) ) return "UNKNOWN";
     return SiteWindowModeArray[i];
    }

    /** Returns string for current site window mode.
     *@return short string describing the current site window mode
     */
     public String getCurrentTypeString(){
     return getString(value);
    }

}//eo SiteWindowMode class
