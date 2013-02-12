/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package delaunay_triangulation;

import java.util.Iterator;

/**
 *
 * @author time
 */
public class testdt {

    public testdt(){}
    
    public static void main(String[] args) {
    
        double [] pt = {0,0, 4, -1, 4, 1, 6,0};
        Point_dt[] ptdt = new Point_dt[pt.length/2];
        int p=0; 
        while (p<pt.length-1){
            ptdt[p/2] = new Point_dt(pt[p++],pt[p++]);
        }
        System.out.println("Number of points "+ptdt.length);
        for (p=0; p<ptdt.length; p++){
            System.out.println(p+": "+ptdt[p].x+","+ptdt[p].y+","+ptdt[p].z);
        }
        Delaunay_Triangulation dt = new Delaunay_Triangulation(ptdt);
        System.out.println("Number of triangles "+dt.trianglesSize());
        Iterator<Triangle_dt> itert = dt.trianglesIterator();
        int tn=0;
        Triangle_dt t;
        while (itert.hasNext()){
            t=itert.next();
            System.out.println((tn++)+": "+t.p1()+","+t.p2()+","+t.p3());
        } 
        BoundingBox bb =dt.getBoundingBox();
        System.out.println("Bounding Box: x="+bb.minX()+"-"+bb.maxX()+", y="+bb.minY()+"-"+bb.maxY());
    }
    
}
