/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

/**
 * Sets of static routines for distances
 * @author time
 */
public class Distances {
    
    /**
     * Tolerate small errors in 1.0 in doubles .
     * <br>e.g. cosines just greater than 1 but less than this are treated as 1.0
     */
    public final static double ONETOLERANCE = 1+1e-10; 

        /** 
     * Calculates Euclidean distance.
     * @param x1 first point's x coordinate       
     * @param y1 first point's y coordinate       
     * @param x2 second point's x coordinate       
     * @param y2 second point's y coordinate       
     *@return distance between point one and two
     */
    static public double euclideanDistance(double x1, double y1, double x2, double y2) 
    {
        return (Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)));
    }

    //Hexa Software Development Center All Rights Reserved 2004            
/** This routine calculates the distance in km between two points given the latitude/longitude of those points.
 * <br> South latitudes are negative, east longitudes are positive
 *@param lat1 Latitude  of point 1 
 *@param lon1 Longitude of point 1 
 *@param lat2 Latitude  of point 2 
 *@param lon2 Longitude of point 2 
*/
static public double sphericalDistance(double lat1, double lon1, double lat2, double lon2) {
  double theta = lon1 - lon2;
  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
  if (Math.abs(dist)>=1) if (Math.abs(dist)>ONETOLERANCE) System.err.println("*** in sphericalDistance cosine of angle is bigger than one. = "+dist);
  else return 0;
  dist = Math.acos(dist);
  dist = rad2deg(dist);
  dist = dist * 60 * 1.1515;
  dist = dist * 1.609344;
  return (dist);
}

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts decimal degrees to radians             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
static public double deg2rad(double deg) {
  return (deg * Math.PI / 180.0);
}

/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*::  This function converts radians to decimal degrees             :*/
/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
static public double rad2deg(double rad) {
  return (rad * 180.0 / Math.PI);
}

    
}
