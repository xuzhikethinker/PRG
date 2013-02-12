/*
 * FullVertexIAL.java
 *
 * Created on 15 November 2006, 16:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package CopyModel;

import cern.colt.list.IntArrayList;
import java.util.Random; //p524 Schildt
    
// **********************************************************************
/**
 * Defines attributes of an artefact vertex.
 * <p>This version stores neighbours at random in a simple CernColt IntArrayList.  
 * May be fatser in some circumstances to use a sorted list or TreeList for speed
 * in which case separate classes might be constructed?  
 * TreeList of apache commons collection does not have delete.
 * ToDo: remove Rnd and pass this to random neighbour selector.
 * @author time
 */

    
    public class FullVertexIAL extends Vertex
    {
//     int label;
     IntArrayList sourceList;  // surely best to use something that keeps list in numerical order?
//     Random Rnd = new Random(); //Schildt p524, time is used as seed
     
        
        public FullVertexIAL()
        {
//            label=0;
          weight=0;
          degree=0;
          sourceList = new IntArrayList();
        }

        
        public FullVertexIAL(double setweight )
        {
//          label=0;
          weight=setweight;
          degree=0;
          sourceList = new IntArrayList();
          
        }
        
        public FullVertexIAL(FullVertexIAL oldVertex  )
        {
//          label= oldVertex.label ;
          weight=oldVertex.weight;
          degree=oldVertex.degree;
          sourceList = new IntArrayList();
          for (int n=0; n< oldVertex.sourceList.size() ; n++) sourceList.add(oldVertex.sourceList.get(n));        
        }
        /* Adds neighbour in the list.
         * <p>This is fast as its just put at the end of a list.  
         *@return current number of neighbours
         */
    @Override
        public int addNeighbour(int neighbour)
        {
            sourceList.add(neighbour);
            degree=sourceList.size();
            return(degree);
        }

        /* 
         * Returns a neighbour.
         * @param n number of neighbour required
         * @return number of n'th neighbour
         */
    @Override
        public int getNeighbourQuick(int n)
        {
            return(sourceList.getQuick(n));
        }

    /* 
 * Returns a random neighbour
 * @return number of a random neighbour
 */
    @Override
        public int getRandomNeighbour(Random Rnd)
        {
            return(sourceList.getQuick(Rnd.nextInt(degree)));
        }

        /* Removes the last neighbour in the list
         * <p>This may be too slow.  Use a sorted list or TreeList?
         *@return current number of neighbours
         */
    @Override
        public int deleteNeighbour(int neighbour)
        {
            sourceList.delete(neighbour); //deletes first entry equal to neighbour
            degree=sourceList.size();
            return(degree);
        }

        /* Removes the last neighbour in the list.
         * <p>This should always be fast.
         *@return number of neighbour being removed, -1 if there were no neighbours.
         */
    @Override
        public int deleteLastNeighbour()
        {
            // last entry should be degree-1 = sourcelist.size();
            if (degree==0) return -1;
            int n= sourceList.get(--degree);
            sourceList.remove(degree); //deletes last entry equal to neighbour
            return n;
        }


        /* Wipes source list AND degree.
         */
    @Override
        public void wipeSourceList()
        {
          degree=0;  
          sourceList = new IntArrayList();
        }
        
        /*
         * Produces a string giving the neighbours of the vertex.
         *@param sep separation string often a tab character.
         */
        public String listNeighbours(String sep)
        {
            String s="";
            for (int n=0; n<sourceList.size(); n++) s=s+sourceList.get(n)+sep;
            return(s);
        }
        
                /** 
         * Gives String of information on vertex.
         * @param sep character to separate fields
         * @return string representing the vertex
         */
    @Override
        public String stringInformation(String sep)
        {
          return (stringBasic(sep) +   sep + listNeighbours(sep));
        }
    
            /** 
         * Gives label for columns of the stringInformation string.
         * @param sep character to separate fields
         * @return label for columns
         */
    @Override
        public String stringInformationLabel(String sep)
        {
          return (stringBasicLabel(sep) + sep +"Ind.Neighbours");
        }


    } // eo Vertex class
  
 
