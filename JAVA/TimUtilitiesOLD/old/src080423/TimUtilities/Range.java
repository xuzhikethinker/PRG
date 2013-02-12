package TimUtilities;

/**
 * Range describes a range of values and generates them in a sequence.
 * @author time
 */

public class Range 
{
    public double min;
    public double max;
    public double step;
    public double value;
    public int type=-1; // 0 = add step, 1 = multiply by step, -1 just iterate once

    public Range()
    {
        min=8e99;
        max=-min;
        step=1;  
        value=min;
        type = -1;
    }

    // use to set value to be used, no loops
    public Range(double v)
    {
        if (Math.abs(v)>1e-6)
        {
            min=v;
            max=Math.abs(v)*0.1+v;
            step=v;  
            value=v;
        }
        else
        {
            min=v;
            max=v+2e-6;
            step=1e-5;  
            value=v;
        }
        type = -1;
    }

    /* Makes a deep copy.
     *@param r existing range to be copied
     */
    public Range(Range r)
    {
        min=r.min;
        max=r.max;
        step=r.step;  
        value=r.value;
        type = r.type;
    }
    
    public String toString(String SEP)
    {
        String s= min+SEP+max+SEP+step+SEP+value+SEP+getTypeString()+SEP+count();
        return s;
    }
            
    public String getTypeString()
    {
        String s="Unknown";
        switch (type)
        {
            case -1: s="one value"; break;
            case 1: s="multiply"; break;
            case 0: s="add"; break;
            default: s="UNKNOWN";
        }
        return s;
    }
    public int count()
    {
        int n;
        switch (type)
        {
            case -1: n=1; break;
            case 1: n= (int) (Math.log(max/min)/Math.log(step))+1; break;
            case 0: n =(int) ((max-min)/step) + 1; break;
            default: n=999;
                    
        }
        return ((n>1) ? n : 1);
    }

    public double nextValue()
    {
        switch (type)
        {
            case -1: value=max*2; break;
            case 1: value*=step; break;
            case 0: value+=step; break;
            default: value=max*2;
        }
        return value;
    }
    
    public double getMin()
    {
        return min;
    }

    public double getMax()
    {
        return max;
    }

    public double getStep()
    {
        return step;
    }
    public double getValue()
    {
        return value;
    }

}
