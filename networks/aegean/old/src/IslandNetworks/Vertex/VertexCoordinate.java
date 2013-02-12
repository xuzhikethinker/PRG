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
 *
 * @author time
 */

package IslandNetworks.Vertex;

public class VertexCoordinate 
{
     double x;
     double y;
     double z;
     
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

        public double distance(VertexCoordinate c)
        {
            return Math.sqrt( (x - c.x)*(x - c.x)+ (y - c.y)*(y - c.y) + (z - c.z)* (z - c.z));
        }
        public double distance2D(VertexCoordinate c)
        {
            return Math.sqrt( (x - c.x)*(x - c.x)+ (y - c.y)*(y - c.y) );
        }
        
    }
   

