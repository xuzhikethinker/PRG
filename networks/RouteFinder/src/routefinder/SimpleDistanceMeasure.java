/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;

//import IslandNetworks.IslandHamiltonian;


/**
 * Simple distance measure.
 * @author time
 */
public class SimpleDistanceMeasure implements PathMeasure {

    double [] friction;
    
    /**
     * Defines a basic path measure using distances.
     * <p>The distance penalty is 1.
     */
    public SimpleDistanceMeasure() {
        friction = new double[PathSegment.numberTypes];
        for (int i=0; i<friction.length; i++) friction[i]=1.0;
    }
    /**
     * Defines a basic path measure using distances and friction.
     * <p>The distance penalty is given as the friction array. The index of types
     * is specified by the {@code PathSegment.typeString}.
     * @param inputFriction penalties for each type of segment
     */
    public SimpleDistanceMeasure(double [] inputFriction) {
        if (inputFriction.length!=PathSegment.numberTypes) 
             throw new RuntimeException("friction array of long length, wanted "
                                        +PathSegment.numberTypes+
                                        ", got "+inputFriction.length);
        friction = new double[PathSegment.numberTypes];
        for (int i=0; i<friction.length; i++) friction[i]=inputFriction[i];
    }
 
 /**
  * Calculates the length measure for a segment of a path.
  * <p>This can be a distance or a potential.
  * For the latter small potential means long distance.
  * @param seg segment of path
  * @return measure of length of path
  */   
 public double getMeasure(PathSegment seg){
     return seg.getDistance()*friction[seg.getType()];
 }

   /**
  * Tests to see if it is a distance measure.
  * @return true (false) if it is a distance (potential) measure.
  */
 public boolean isDistanceMeasure(){return true;}
 
 /**
  * Tests to see if it is a potential measure.
  * @return true (false) if it is a potential (distance) measure.
  */
 public boolean isPotentialMeasure(){return false;}
 
 /**
  * Returns value corresponding to very large separations.
  * <p>This is large as defined by <tt>compareMeasures</tt>
  * @return a value for large speartions for this measure.
  */
 public double largeValue(){return 1e99;}

 /**
  * Returns value corresponding to very small separations.
  * <p>This is small as defined by <tt>compareMeasures</tt>
  * @return a value for small separations for this measure.
  */
 public double smallestValue(){return 0.0;}


  /**
   * Compares distance of two paths  
   * @param pm1 the measure of path 1
   * @param pm2 the measure of path 2
   * @return true if path 1 has greater distance than path 2
   */
   public boolean compareMeasures(double pm1, double pm2){
     return (pm1>pm2);
 }
    
   /**
    * Compact description string suitable for filenames.
    * <p>No spaces should be included.
    * @return Compact description string suitable for filenames.
    */
   public String descriptionStringCompact(){
       String s="";
       for(int t=0; t<PathSegment.typeString.length; t++) s=s+PathSegment.typeString[t].charAt(0)+Math.round(friction[t]*1000);
       return "Dist"+s;
   } 
  
   /**
    * Human readable pretty description string.
    * @param sep separation string
    * @return Human readable pretty description string.
    */
   public String descriptionStringPretty(String sep){
          String s="Frction:";
          for(int t=0; t<PathSegment.typeString.length; t++) s=s+sep+PathSegment.typeString[t]+" = "+sep+friction[t];
          return "Distance measure, "+sep+s;
   }


}
