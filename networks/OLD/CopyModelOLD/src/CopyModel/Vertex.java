/*
 * Vertex.java
 *
 * Created on 15 November 2006, 16:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package CopyModel;

import java.util.Random; //p524 Schildt

//import java.io.PrintStream;

// **********************************************************************
/**
 * Defines attributes of an a simple vertex.
 * @author time
 */

    
    public class Vertex
    {
//     int label;
        // Initialise her so all extensions get this too.
     double weight=0;
     int degree=0;
     int rank=Rank.UNRANKED;
        
        public Vertex()
        {
        }

        public Vertex(double setweight )
        {
          weight=setweight;
//          degree=0;
        }
        
        public Vertex(Vertex oldVertex)
        {
            copy(oldVertex);
        }
        public void copy(Vertex oldVertex)
        {
//          label= oldVertex.label ;
          weight=oldVertex.weight;
          degree=oldVertex.degree;
          rank=oldVertex.rank;
        }
        

        
     /* 
 * Dummy routine.  
 * <p>Must be overrided to be useful.
 * @return -1
 */
        public int getRandomNeighbour(Random rnd)
        {
            return(-1);
        }
        
       /* Updates degree only.
         * @param neighbour not used in this implementation.
         * @return the degree of this vertex.
         */
        public int addNeighbour(int neighbour)
        {
            return(++degree);
        }
        
        /* Updates degree only.
         * @param neighbour not used in this implementation.
         * @return the degree of this vertex.
         */
        public int deleteNeighbour(int neighbour)
        {
            return(--degree);
        }


        /* Wipes source list AND degree.
         */
        public void wipeSourceList()
        {
          degree=0;  
        }

        /** 
         * Gives string of information on vertex.
         * @param sep character to separate fields
         * @return string representing the vertex
         */
        public String stringBasic(String sep)
        {
            String s=degree + sep + weight;
            if (rank!=Rank.UNRANKED) s=s+sep+rank;
          return (s);
        }
        /** 
         * Gives label for columns of the stringInformation string.
         * @param sep character to separate fields
         * @return label for columns
         */
        public String stringBasicLabel(String sep)
        {
            String s="Degree" + sep + "Weight";
            if (rank!=Rank.UNRANKED) s=s+sep+"Rank";
          return (s);
        }


        
        /** 
         * Gives String of information on vertex.
         * @param sep character to separate fields
         * @return string representing the vertex
         */
        public String stringInformation(String sep)
        {
          return stringBasic(sep);
        }

        /** 
         * Gives label for columns of the stringInformation string.
         * @param sep character to separate fields
         * @return label for columns
         */
        public String stringInformationLabel(String sep)
        {
            return stringBasicLabel(sep);
        }

        
        /**
         * Dummy class implemeted only in full vertex
         *@return -1 
         */
        public int deleteLastNeighbour()
        {
            return -1;
        }
        
        /** 
         * Returns a neighbour.
         * @param n number of neighbour required
         * @return number of n'th neighbour if known, -1 if not
         */
        public int getNeighbourQuick(int n)
        {
            return(-1);
        }

    } // eo Vertex class
  
 