/*
 * VertexLabel.java
 *
 * Created on 13 March 2006, 16:44
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/**
 *
 * @author time
 */

package IslandNetworks;
    
    public class VertexLabel 
    {
     double strength;
     IslandCoordinate  position;
     String name;
//     Rank rank;
        
        public VertexLabel()
        {
          name="";
          strength=0;
          position=new IslandCoordinate ();
//          rank = new Rank();
        }
        
        public VertexLabel(double setstrength, IslandCoordinate  setposition )
        {
          name="";
          strength=setstrength;
          position=setposition;
//          rank = new Rank();
        }
        
        public VertexLabel(String setname, IslandCoordinate  setposition )
        {
          name=setname;
          strength=0;
          position=setposition;
//          rank = new Rank();
        }
        
        public VertexLabel(String setname, double setstrength, IslandCoordinate  setposition )
        {
          name=setname;
          strength=setstrength;
          position=setposition;
//          rank = new Rank();
        }
        
        public VertexLabel(VertexLabel oldVertexLabel )
        {
          name=oldVertexLabel.name;
          strength=oldVertexLabel.strength;
          position = new IslandCoordinate  (oldVertexLabel.position);
//          rank = new Rank();
        }
        
        public String pajekString()
        {   
            String s=  " \""+name+"\"  "+position.x+" "+position.y+" 0";
            return s;
        }

        
        public String pajekString(double minx, double miny, double maxx, double maxy)
        {            
            double scalex = (maxx-minx);
            double scaley = (maxy-miny);
            String s="";
            if ((scalex==0) || (scaley==0) ) s= pajekString();
            else s=  " \""+name+"\"  "+((position.x-minx)/scalex)+" "+((position.y-miny)/scaley)+" 0";
            return s;
        }

        public String pajekString(IslandCoordinate  min, IslandCoordinate  max)
        {            
            return pajekString(min.x,min.y,max.x,max.y);
        }

        public String printString(String SEP)
        {            
            String s=  name+SEP+position.printString(SEP)+SEP+strength+SEP; //+rank.printString(5)+SEP;
            return s;
        }
        
    }//eo vertexlabel class    
    
    