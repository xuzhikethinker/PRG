/*
 * IslandCoordinate.java
 * 
 * Created on 13 March 2006, 16:45
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/**
 * Represents a point in space or a vector.
 * <p>As a vector the source is taken as the origin.
 * @author time
 */

package IslandNetworks.Vertex;

public class VertexCoordinate 
{
     double x;
     double y;
     double z;
     double DUNSET=-1.23456789e45;     
     double vectorLength2D=DUNSET;     
     double vectorLength=DUNSET;     
        public VertexCoordinate()
        {
          x=0;
          y=0;
          z=0;
        }
        
        public VertexCoordinate(int xinit, int yinit )
        {
          x=xinit;
          y=yinit;
          z=0;
        }
        
        public VertexCoordinate(double xinit, double yinit )
        {
          x=xinit;
          y=yinit;
          z=0;
        }
        
        public VertexCoordinate(double xinit, double yinit, double zinit )
        {
          x=xinit;
          y=yinit;
          z=zinit;
        }
        
        public VertexCoordinate(VertexCoordinate oldc    )
        {
          x=oldc.x;
          y=oldc.y;
          z=oldc.z;
        }

        public String print2DString(String separator)
        {
            String s=x+separator+y;
            return s;
        }

        public String printString(String separator)
        {
            String s=x+separator+y+separator+z;
            return s;
        }
        
        public void setToMinimum(VertexCoordinate c)
        {
            x = Math.min(x,c.x);
            y = Math.min(y,c.y);
            z = Math.min(z,c.z);
            return;
        }
        
        public void setToMaximum(VertexCoordinate c)
        {
            x = Math.max(x,c.x);
            y = Math.max(y,c.y);
            z = Math.max(z,c.z);
            return;
        }
        
        public void min(VertexCoordinate c, VertexCoordinate c2)
        {
            x = Math.min(c2.x,c.x);
            y = Math.min(c2.y,c.y);
            z = Math.min(c2.z,c.z);
            return;
        }

        public void max(VertexCoordinate c, VertexCoordinate c2)
        {
            x = Math.max(c2.x,c.x);
            y = Math.max(c2.y,c.y);
            z = Math.max(c2.z,c.z);
            return;
        }
       
        public boolean equals(VertexCoordinate c)
        {
            if ( (x == c.x) && (y == c.y) && (z == c.z))  return true;
            return false;
        }

        /**
         * Returns length of point in 3d.
         * <p>If unset it is calculated.
         */
        public double getLength2D(){
            if (vectorLength2D==DUNSET) vectorLength2D=Math.sqrt(x*x+y+y);
            return vectorLength2D;
        }
        /**
         * Returns length of point in 3d.
         * <p>If unset it is calculated.
         */
        public double getLength(){
            if (vectorLength==DUNSET) vectorLength=Math.sqrt(x*x+y+y+z*z);
            return vectorLength;
        }
        
        public double distance(VertexCoordinate c)
        {
            return Math.sqrt( scalarProduct(c));
        }
        public double scalarProduct(VertexCoordinate c)
        {
            return (x - c.x)*(x - c.x)+ (y - c.y)*(y - c.y) + (z - c.z)* (z - c.z);
        }
        
        public double distance2D(VertexCoordinate c)
        {
            return Math.sqrt( scalarProduct2D(c) ) ;
        }

        public double scalarProduct2D(VertexCoordinate c)
        {
            return (x - c.x)*(x - c.x)+ (y - c.y)*(y - c.y) ;
        }

        /**
         * Find midpoint
         * @param nx x coordinate of point 
         * @param ny
         * @return 
         */
        public VertexCoordinate findMidPoint(double nx, double ny){
            return new VertexCoordinate((x+nx)/2,(y+ny)/2);
        }
        //perpVector =new VertexCoordinate(midPoint.x-centre.x,midPoint.y-centre.y);

    }
   

