/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

/**
 *
 * @author time
 */
public class ProbabilityDistance {
    final static double MAXDISTANCE = 1e6;
    final static double MAXPROBABILITY = MAXDISTANCE/(1.0+MAXDISTANCE);
    final static double INFINITEDISTANCE= 2*MAXDISTANCE;
    final static double BADPROBABILITY = -1234.56789;
    final static double BADDISTANCE = -98765.4321;
    
    public ProbabilityDistance(){
        
    } 
    
    static public double probabilityToDistance(double p){
        if ((p<0) || (p>1)) return BADPROBABILITY;
        if (p>MAXPROBABILITY) return INFINITEDISTANCE;
        return 1.0/(1.0-p);
    }
    
    static public String probabilityToDistanceString(double p){
        return distanceString(probabilityToDistance(p));
    }

    static public double distanceToProbability(double d){
        if (d<0) return BADDISTANCE;
        return d/(1.0+d);
    }
    
    static public String distanceString(double d){
        if (d<0) return "Negative Distance";
        if (d>MAXDISTANCE) return "Infinite Distance";
        return Double.toString(d);
    }
    static public String probabilityString(double p){
        if (p<0) return "Negative Probability";
        if (p>1) return "Probability >1";
        return Double.toString(p);
    }
}
