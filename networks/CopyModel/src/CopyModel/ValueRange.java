/*
 * ValueRange.java
 *
 * Created on 17 November 2006, 11:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package CopyModel;

import cern.colt.list.IntArrayList;

/**
 * Encodes a range of Integer Values.
 * <br>minimum..bottomMaximum is a continuous range of values at bottom
 * the values separated by interval counting from minimum
 * and finally topMinimum..maximum range of continuous values at top.
 * <p>To choose all values in the range set
 * <tt>bottomMaximum=maximum</tt> and <tt>interval=0</tt>
 * @author time
 */
public class ValueRange {
    private int minimum;  // smallest value
    private int bottomMaximum;
    private int interval;
    private int topMinimum;
    private int maximum; //largest value
    private IntArrayList value;
    
    /** Creates a new instance of ValueRange. 
     * Still needs to be initialised.
     */
    public ValueRange() {}
    
    
     /** Creates a new instance of ValueRange. 
     * <br>Creates a list of values in numerical order with minimum..bottomMaximum followed
     * by values every interval values (started from 0) and ending with a continuous range
     * topMinimum..maximum
     *@param min lowest possible value
     *@param bottomMax maximum value of continuous range at bottom
     *@param intvl intervals between values (starting from minimum) 
     *@param topMin minimum value of continuous range at top
     *@param max maximum value
     */
    public ValueRange(int min, int bottomMax, int intvl, int topMin, int max) {
        
        minimum =min;
        maximum =max;
        bottomMaximum = bottomMax;
        interval = intvl;
        topMinimum = topMin;
    }
     /** Creates a new instance of ValueRange by deep copying parameters. 
     *@param vr ValueRange containing parameters to be used
     */
    public ValueRange(ValueRange vr) {
        
        maximum = vr.maximum;
        bottomMaximum = vr.bottomMaximum;
        interval = vr.interval;
        topMinimum = vr.topMinimum;
    }
    
    public void setMinimum(int min)
    {
        minimum =min; 
    }

    public void setBottomMaximum(int bottomMax)
    {
        bottomMaximum = bottomMax; 
    }
    
    public void setInterval(int intvl)
    {
        interval = intvl; 
    }
    
    public void setTopMinimum(int topMin)
    {
        topMinimum = topMin; 
    }
    
    public void setMaximum(int max)
    {
        maximum =max; 
    }
    
    /**
     * Ensure values are in correct range.
     */
    private void correctValues()
    {
        bottomMaximum = Math.min(Math.max(bottomMaximum,minimum),maximum-1); // minimum <= bottomMaximum < maximum
        //topMinimum = Math.max(Math.min(topMinimum,maximum),minimum+1); // minimum < topMinimum <= maximumvalue = new IntArrayList();
        topMinimum = Math.max(Math.min(topMinimum,maximum),bottomMaximum+1); //  bottomMaximum < topMinimum <= maximumvalue = new IntArrayList();
        if (interval<1) interval = (maximum-minimum);
    }
    
    public void create()
    {
         correctValues();
         value = new IntArrayList();
         int v;
         for (v=minimum; v<= bottomMaximum; v++) value.add(v); // this must always add minimum to list
         for (v= ( ( ( (v-minimum-1)/interval)+1)*interval)+minimum; v<topMinimum; v+=interval) value.add(v);
         //for (v= ( ( ( (v-bottomMaximum-1)/interval)+1)*interval)+bottomMaximum+1; v<topMinimum; v+=interval) value.add(v);
         for (v=topMinimum; v<=maximum; v++) value.add(v); // this must always add maximum to list

    }
    
    /**
     * Returns value index number i
     */
    public int get(int index)
    {
        return value.get(index);
    }
    
    /**
     * Returns number of values stored
     */
    public int getNumberValues()
    {
        return value.size();
    }

    public int getMinimum()
    {
        return minimum;
    }

    public int getBottomMaximum()
    {
        return bottomMaximum;
    }

    public int getInterval()
    {
        return interval;
    }

    public int getTopMinimum()
    {
        return topMinimum;
    }

   /**
     * Returns maximum value allowed
     */
    public int getMaximum()
    {
        return maximum;
    }
}
