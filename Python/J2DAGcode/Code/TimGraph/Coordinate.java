package TimGraph;
/*
 * Coordinate.java
 *
 * Created on 08 February 2006, 16:27
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/**
 *
 * @author time
 */
     public class Coordinate
    {
     double x;
     double y;
     double z;
     
        public Coordinate()
        {
         set(0,0,0);
        }
        
        public Coordinate(int xinit, int yinit )
        {
          set(xinit,yinit,0);
        }
        
        public Coordinate(double xinit, double yinit )
        {
          set(xinit,yinit,0);
        }
        
        public Coordinate(double xinit, double yinit, double zinit )
        {
          set(xinit,yinit,zinit);
        }

        /**
         * Deep copy.
         * @param oldc old coordinate
         */
        public Coordinate(Coordinate oldc )
        {
          set(oldc);
        }

        /**
         * Deep copy.
         * @param oldc old coordinate
         */
        public void set(Coordinate c){
          x=c.x;
          y=c.y;
          z=c.z;
        }
        
        public void set(double xxx, double yyy){
          x=xxx;
          y=yyy;
          z=0;
        }

        public void set(double xxx, double yyy, double zzz){
          x=xxx;
          y=yyy;
          z=zzz;
        }

        public void setToMinimum(Coordinate c)
        {
            x = Math.min(x,c.x);
            y = Math.min(y,c.y);
            z = Math.min(z,c.z);
            return;
        }

        /**
         * Sets values of this coordinate with larger of input and current values.
         * @param c input coordinate
         */
        public void setToMaximum(Coordinate c)
        {
            x = Math.max(x,c.x);
            y = Math.max(y,c.y);
            z = Math.max(z,c.z);
            return;
        }
        
        public void min(Coordinate c, Coordinate c2)
        {
            x = Math.min(c2.x,c.x);
            y = Math.min(c2.y,c.y);
            z = Math.min(c2.z,c.z);
            return;
        }

        public void max(Coordinate c, Coordinate c2)
        {
            x = Math.max(c2.x,c.x);
            y = Math.max(c2.y,c.y);
            z = Math.max(c2.z,c.z);
            return;
        }
       
        public boolean equals(Coordinate c)
        {
            if ( (x == c.x) && (y == c.y) && (z == c.z))  return true;
            return false;
        }

        public double getX(){return x;}
        public double getY(){return y;}
        public double getZ(){return z;}

        public double distance(Coordinate c)
        {
            return Math.sqrt( (x - c.x)*(x - c.x)+ (y - c.y)*(y - c.y) + (z - c.z)* (z - c.z));
        }
        public double distance2D(Coordinate c)
        {
            return Math.sqrt( (x - c.x)*(x - c.x)+ (y - c.y)*(y - c.y) );
        }
        
        
        public String toString2D(String sep){
            return x+sep+y;
        }
        public String toString(String sep){
            return x+sep+y+sep+z;
        }
        /**
         * This works even if coordinate not defined
         * @param c coordinate 
         * @param sep separation string
         * @return string representing coordinate even if not defined
         */
        static public String toString(Coordinate c, String sep){
            if (c==null) return "."+sep+"."+sep+".";
            return c.toString(sep);
        }
        
        /**
         * @deprecated use toString
         */
         public String print2DString(String separator)
        {
            String s=x+separator+y;
            return s;
        }

        /**
         * @deprecated use toString
         */
        public String printString(String separator)
        {
            String s=x+separator+y+separator+z;
            return s;
        }
        

        static public String labelString2D(String sep){
            return "x"+sep+"y";
        }
        static public String labelString(String sep){
            return "x"+sep+"y"+sep+"z";
        }

        /**
         * Calculate midpoint of arc connecting two points.
         * <p>The midpoint should be displaced from the midpoint of the line
         * joining the two points given
         * by an amount equal to a fraction factor of the length of the arc
         * along the perpendicular from the midpoint. This is done only in the
         * x/y plane though the z coordinate is set appropriately.
         * @param s source point
         * @param t target point
         * @param factor scale factor
         * @return coordinate of the arc midpoint
         */
       public static Coordinate calcArc2DCoordinate(Coordinate s,  Coordinate t, double factor){
        double f= factor/calcLength(s,t);
        return new Coordinate((s.x+t.x)/2.0 + (t.y-s.y)*f,(s.y+t.y)/2.0 + (s.x-t.x)*f,(s.z+t.z)/2.0);
       }

       public static Coordinate calcMidPoint(Coordinate s,  Coordinate t){
         return new Coordinate((s.x+t.x)/2.0,(s.y+t.y)/2.0,(s.z+t.z)/2.0);
       }

       public static double calcLength(Coordinate s,  Coordinate t){
         return Math.sqrt((s.x-t.x)*(s.x-t.x)+ (s.y-t.y)*(s.y-t.y)+ (s.z-t.z)*(s.z+t.z));
       }

    }
   
