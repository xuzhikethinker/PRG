/*
 * FullVertexTreeList.java
 *
 * Created on 15 November 2006, 16:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package CopyModel;

//import cern.colt.list.IntArrayList;
import org.apache.commons.collections.list.TreeList;
import java.util.Random; //p524 Schildt
    
// **********************************************************************
/**
 * Defines attributes of an artefact vertex.
 * <p>This version stores neighbours at random.  
 * Uses a TreeList for speed.  To delete an object uses indexOf to find its index.  
 * Is this fast?  In principle yes as TreeList is stores objects in order.
 * ToDo: remove Rnd and pass this to random neighbour selector.
 * @author time
 */

    
    public class FullVertexTreeList extends Vertex
    {
//     int label;
     TreeList sourceList;  // surely best to use something that keeps list in numerical order?
//     Random Rnd = new Random(); //Schildt p524, time is used as seed
     
        
        public FullVertexTreeList()
        {
//            label=0;
          weight=0;
          degree=0;
          sourceList = new TreeList();
        }

// *** use empty initialisation plus add routine
// initialise with first neighbour
//        public Vertex(int firstNeighbour)
//        {
//            label=0;
//          weight=0;
//          degree=1;
//          sourceList = new IntArrayList();
//          sourceList.add(firstNeighbour);
//        }
        
        public FullVertexTreeList(double setweight )
        {
//          label=0;
          weight=setweight;
          degree=0;
          sourceList = new TreeList();
          
        }
        
        public FullVertexTreeList(FullVertexTreeList oldVertex  )
        {
//          label= oldVertex.label ;
          weight=oldVertex.weight;
          degree=oldVertex.degree;
          sourceList = new TreeList();
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
            return((Integer) sourceList.get(n));
        }

    /* 
 * Returns a random neighbour
 * @return number of a random neighbour
 */
    @Override
        public int getRandomNeighbour(Random Rnd)
        {
            return((Integer) sourceList.get(Rnd.nextInt(degree)));
        }

        /* Removes the last neighbour in the list
         * <p>This may be too slow.  Use a sorted list or TreeList?
         *@return current number of neighbours
         */
    @Override
        public int deleteNeighbour(int neighbour)
        {
// Next line requires delete operation.  Defined for CernColt IntArrayList but not TreeList
            int i=sourceList.indexOf(neighbour); // is this fast?
            if (i<0) return degree; 
            sourceList.remove(i);  
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
            int n= (Integer) sourceList.get(--degree);
            sourceList.remove(degree); //deletes last entry equal to neighbour
            return n;
        }


        /* Wipes source list AND degree.
         */
    @Override
        public void wipeSourceList()
        {
          degree=0;  
          sourceList = new TreeList();
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
          return (stringBasic(sep) +  sep + listNeighbours(sep));
        }
    
            /** 
         * Gives label for columns of the stringInformation string.
         * @param sep character to separate fields
         * @return label for columns
         */
    @Override
        public String stringInformationLabel(String sep)
        {
          return (stringBasicLabel(sep) +  sep +"Ind.Neighbours");
        }

    /**
     * deletes entry equal to neighbour from TreeList storing ints
     * @param sourceList 
     * @param neighbour
     * @return
     */
    private int delete(TreeList tl, int neighbour){
        return -1;
    } 

    } // eo Vertex class
  
 