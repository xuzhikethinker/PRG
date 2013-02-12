/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;


/**
 * Defines length measures for paths.
 * <p>These can be straight distance metrics.
 * Alternatively these can be potentials for which large 
 * (small) potential means short (long) distance path.
 * <p>A <em>distance</em> measure is one which a 
 * larger value for the larger the length is along a path.
 * A <em>potential</em> measure is one for which a 
 * smaller value for the shorter the length is along a path.
 * @author time
 */
public interface PathMeasure {

         /**
  * Calculates the length measure for a segement of a path.
  * <p>This can be a distance or a potential.
  * For the latter small potential means long distance.
  * @param seg a segment of a path
  * @return measure of length of path
  */   
 public double getMeasure(PathSegment seg);

 /**
  * Tests to see if it is a distance measure.
  * @return true (false) if it is a distance (potential) measure.
  */
 public boolean isDistanceMeasure();
 /**
  * Tests to see if it is a potential measure.
  * @return true (false) if it is a potential (distance) measure.
  */
 public boolean isPotentialMeasure();
 
 /**
  * Returns value corresponding to very large separations.
  * <p>This is large as defined by <tt>compareMeasures</tt>
  * @return a value for large speartions for this measure.
  */
 public double largeValue();

 /**
  * Returns value corresponding to zero separations.
  * <p>This is small as defined by <tt>compareMeasures</tt>
  * so should be 0 for distance measures and 1 for potentials.
  * @return a value for small separations for this measure.
  */
 public double smallestValue();

  /**
   * Compares length measure of two paths.
   * <p>This can be a distance or a potential.
   * For the latter small potential means long distance. 
   * @param pm1 the measure of path 1
   * @param pm2 the measure of path 2
   * @return true if path 1 has greater length (larger distance or smaller potential) than path 2
   */
   public boolean compareMeasures(double pm1, double pm2);
   
   /**
    * Compact description string suitable for filenames.
    * <p>No spaces should be included.
    * @return Compact description string suitable for filenames.
    */
   public String descriptionStringCompact(); 
  
   /**
    * Human readable pretty description string.
    * @param sep separation string
    * @return Human readable pretty description string.
    */
   public String descriptionStringPretty(String sep); 
  
}
