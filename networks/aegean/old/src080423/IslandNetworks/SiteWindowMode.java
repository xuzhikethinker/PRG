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
                     
    
 final String[] SiteWindowModeArray =  {"Numerical", "Weight", "Rank", "Alphabetical", "Rank/Weight"}; 
 public final int numberTypes = SiteWindowModeArray.length;
 public int value=0;

public SiteWindowMode(){
 value=1;   
}

public SiteWindowMode(int m){
 value=m;   
}

            /** Sets numerical code for cluster types from string , -1 if not known.
     */
    public void setSiteWindowMode(String s)
    {
        for (int i=0; i<SiteWindowModeArray.length; i++)
            if (s.substring(0,1).equals(SiteWindowModeArray[i].substring(0,1))) {value=i; return;}
        value=-1;
        return;
    }
    
   /** Returns string for cluster typinet.edgeSet.
     *@param i the cluster mode number.
     *@return short string describing the cluster type
     */
     public String getString(int i){
     if ((i<0) || (i>=SiteWindowModeArray.length) ) return "UNKNOWN";
     return SiteWindowModeArray[i];
    }

    /** Returns string for current cluster type.
     *@return short string describing the current cluster type
     */
     public String getCurrentTypeString(){
     return getString(value);
    }

}//eo SiteWindowMode class
