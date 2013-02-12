/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;

import java.io.PrintStream;
import java.util.ArrayList;


/**
 * A Path is a source and a target with a list of segments.
 * If there are no segments given then path not yet specified. This might be so
 * when there is no direct route and the composite route has not yet
 * been formed.
 * @author time
 */
public class Path {
    
    static final int IUNSET=-13578642;
    /**
     * Index of source vertex of path.
     */
    private int source=IUNSET;
    /**
     * Index of target vertex of path.
     */
    private int target=IUNSET;
    /**
     * Ordered list of segments.
     */
    private ArrayList<PathSegment> segmentList;

    public Path(){
        segmentList = new ArrayList();
    }
    
    public Path(int s, int t){
        source=s;
        target=t;
        segmentList = new ArrayList();
    }
    
    /**
     * Index of source vertex of path.
     * @return Source vertex index
     */
    public int getSource(){return source;}
    /**
     * Index of target vertex of path.
     * @return target vertex index
     */
    public int getTarget(){return target;}
     
    /**
     * Adds a segment to the path.
     * @param seg segment to be added.
     */
    public void addSegment(PathSegment seg){segmentList.add(seg);}
    /**
     * Adds a segment to the path.
     * <p>Will only do so if segment is legal
     * @param d distance along segment
     * @param t type of segment
     */
    public boolean addSegment(double d, String t){
        PathSegment seg = new PathSegment(d, t);
        if (!seg.testSegment()) return false;
        segmentList.add(seg);
        return true;
    }
    /**
     * Gets a segment in the path.
     * @param i index of segment
     * @return i-th segment of path
     */
    public PathSegment getSegment(int i){return segmentList.get(i);}
    /**
     * Gives the total length for the path.
     * <p>Does this using sum or product of the given measure applied to each segment.
     * Sum usuualy used for distance measures (larger means further apart)
     * product usually for potential type meausres (smaller means further apart).
     * @param sumNotProduct sum (product) used to combine length measure of each segment if true (false)
     * @return length measure for whole path.
     */
    public double totalLength(PathMeasure m, boolean sumNotProduct){
        if (sumNotProduct) return sumLengths(m); 
        else return productLengths(m);
    }
    /**
     * Sum of the lengths path using measure.
     * <p>Appropriate for measures which are distance metrics.
     * @param m measure of length
     * @return sum of lengths for each segment in path.
     */
    public double sumLengths(PathMeasure m){
        double d=0;
        for (PathSegment seg: this.segmentList) d+=m.getMeasure(seg);
        return d;
    }
    
    /**
     * Product of the lengths path using measure.
     * <p>Appropriate for measures which are potentials.
     * @param m measure of length
     * @return product of lengths for each segment in path.
     */
    public double productLengths(PathMeasure m){
        double V=0;
        for (PathSegment seg: this.segmentList) V*=m.getMeasure(seg);
        return V;
    }
    
    /**
     * Tests to see if source and target are good.
     * <p>These must not be negative.
     * @return true (false) if source and target are positive semi-definite.
     */
    public boolean testSourceTarget(){
        return ( ((source<0) || (target<0)) ?false:true);
    }
    
    static public Path reversePath(Path p){
        if (p==null) return null;
        Path rp = new Path(p.getTarget(), p.getSource());
        int size = p.segmentList.size();
        for (int i=size-1; i>=0; i-- ){rp.addSegment(p.getSegment(i));}
        return rp;
    }
    
    public String toString(String sep){
        String s = source+sep+target;
        for (PathSegment seg:segmentList ){s=s+sep+seg.toString(sep);}
        return s;
    }
    /**
     * Descriptive string representation of path.
     * <p>Segment type given as a character
     * @param dp number of decimal places to use for distance
     * @return String representation of path with segment types given as characters
     */
    public String descriptiveString(int dp){
        String s = source+" to "+target;
        for (PathSegment seg:segmentList ){s=s+" : "+seg.descriptiveString(dp,",");}
        return s;
    }
    
    public void printPretty(PrintStream PS, String sep){
        PS.print(source+sep+target+sep+" : ");
        for (PathSegment seg:segmentList) PS.print(sep+" ("+seg.descriptiveString(3,sep)+")");
        PS.println();
    }
}
