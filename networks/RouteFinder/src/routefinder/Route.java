/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;

import java.util.ArrayList;

/**
 * Routes are a sequence of connected paths.
 * <p>The target vertex of one path must be the same as the source vertex of the next.
 * @author time
 */
public class Route {

     ArrayList<Path> routeList;
     
     public Route(){
         routeList= new ArrayList();
     }
     
     /**
      * Adds a path to existing route.
      * <p>Throws an exception if the target vertex of existing path 
      * is not the same as the source vertex of path being added.
      * @param p path to add to route
      * @return number of vertices in route (includes first and last)
      */
     public int addToRoute(Path p){
         int last=routeList.size()-1;
         if ((last>=0) && (routeList.get(last).getTarget()!=p.getSource() )) 
             throw new RuntimeException("Trying to add path with source "+p.getSource()+" to existing route which ends at target "+routeList.get(last).getTarget());
         routeList.add(p);
         return routeList.size();
     }
}
