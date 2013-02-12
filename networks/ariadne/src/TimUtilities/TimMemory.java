/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 *
 * @author time
 */
public class TimMemory {

    long totalMem;
    long freeMem;
    long maxMem;
    
    public void setValues(){
        totalMem = Runtime.getRuntime().totalMemory();
        freeMem =   Runtime.getRuntime().freeMemory();
        maxMem =   Runtime.getRuntime().maxMemory();
    }
    
    public String totalString(){
        setValues();
       return memoryString(totalMem);}
    
    public String freeString(){
        setValues();
       return memoryString(freeMem);}
    
    public String maxString(){
        setValues();
       return memoryString(maxMem);}
    
    public String StringAllValues(){
        setValues();
        return "total, free, max: "+memoryString(totalMem)+", "+memoryString(freeMem)+", "+memoryString(maxMem);
    }
    
    static String memoryString(long mem){
        long kmem=mem/1000;
        if (kmem<1) return mem+"b";
        long mmem=kmem/1000;
        if (mmem<1) return toThreeDecPoints( ( (float) mem)/1000.0f )+"Kb";
        if (mmem<1000) return toThreeDecPoints(( (float) kmem)/1000.0f)+"Mb";
        return toThreeDecPoints(( (float) mmem)/1000.0f )+"Gb";
    }
    
    static public String toThreeDecPoints(float mem){
        return String.format("%6.3f",  mem) ;
    }
}
