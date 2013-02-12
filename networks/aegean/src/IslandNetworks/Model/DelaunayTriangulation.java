/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks.Model;

import DelaunayTriangulation.BoundingBox;
import DelaunayTriangulation.Delaunay_Triangulation;
import DelaunayTriangulation.Point_dt;
import DelaunayTriangulation.Triangle_dt;
import IslandNetworks.islandNetwork;
import java.io.PrintStream;
import java.util.Iterator;

/**
 * Delaunay Triangulation Edge Model.
 * <p>Uses positions not distances to set up network
 * @author time
 */
public class DelaunayTriangulation {

    Delaunay_Triangulation dt;
    islandNetwork inet;
    boolean weighted=false;
    /**
     * Array of site points in Delaunay_Triangulation format.
     * <p><code>ptdt[s]</code> is the site s position in Delaunay_Triangulation format.
     */
    Point_dt[] ptdt;
    /**
     * Constructor for Delaunay Triangulation model
     * @param inet island Network with sites set up
     * @param weighted true (false) if want potential used for edge weights
     */
    public DelaunayTriangulation (islandNetwork inet, boolean weighted){
        this.inet=inet;
        this.weighted=weighted;
    }

    public void makeEdges(islandNetwork inet){

        //inet.Hamiltonian.lambda=1;
        //inet.setEdgePotentials();
        inet.edgeSet.setEdgeValuesZero();
        ptdt = new Point_dt[inet.getNumberSites()];
        for (int s=0; s<inet.getNumberSites(); s++){
            ptdt[s] = new Point_dt(inet.siteSet.getX(s),inet.siteSet.getY(s), 0.0, s);
        }
//        System.out.println("Number of points "+ptdt.length);
//        for (p=0; p<ptdt.length; p++){
//            System.out.println(p+": "+ptdt[p].x+","+ptdt[p].y+","+ptdt[p].z);
//        }
        dt = new Delaunay_Triangulation(ptdt);
        System.out.println("Delaunay Triangulation number of triangles "+dt.trianglesSize());
        Iterator<Triangle_dt> itert = dt.trianglesIterator();
        int tn=0;
        Triangle_dt t;
        while (itert.hasNext()){
            t=itert.next();
//            System.out.println((tn++)+": "+t.p1()+","+t.p2()+","+t.p3());
            setEdge(t.p1(),t.p2());
            if (t.p3()==null) continue; // checks for border cases
            setEdge(t.p2(),t.p3());
            setEdge(t.p3(),t.p1());
        }
    }
    /**
     * Given input point finds point closest to this in triangulation.
     * @param p1 source site of edge
     * @param p2 target site of edge
     * @return true if edge was set, false if not set.
     */
    public boolean setEdge(Point_dt p1, Point_dt p2){
        int s1=p1.index();
        int s2=p2.index();
        if ((!inet.isValidIndex(s1)) ||(!inet.isValidIndex(s2))) return false;
        double w=1;
        if (weighted) w=inet.edgeSet.getEdgePotential1(s1, s2);
        inet.edgeSet.setEdgeValueNoBounds(s1, s2, w);
        if (weighted) w=inet.edgeSet.getEdgePotential1(s2, s1);
        inet.edgeSet.setEdgeValueNoBounds(s2, s1, w);
        return true;
    }

    public void printTriangles(){
        Iterator<Triangle_dt> itert = dt.trianglesIterator();
        int tn=0;
        Triangle_dt t;
        while (itert.hasNext()){
            t=itert.next();
            System.out.println((tn++)+": "+t.p1()+","+t.p2()+","+t.p3());
        }
    }
    public void printBoundingBox(){
        BoundingBox bb =dt.getBoundingBox();
        System.out.println("Bounding Box: x="+bb.minX()+"-"+bb.maxX()+", y="+bb.minY()+"-"+bb.maxY());
    }
    public void printTriangles(PrintStream PS, String sep){
        Iterator<Triangle_dt> itert = dt.trianglesIterator();
        int tn=0;
        Triangle_dt t;
        while (itert.hasNext()){
            t=itert.next();
            PS.println((tn++)+sep+t.p1()+sep+t.p2()+sep+t.p3());
        }
    }
    public void printBoundingBox(PrintStream PS, String sep){
        BoundingBox bb =dt.getBoundingBox();
        PS.println("Bounding Box: x min/max, y min/max="+sep+bb.minX()+sep+bb.maxX()+sep+bb.minY()+sep+bb.maxY());
    }




}
