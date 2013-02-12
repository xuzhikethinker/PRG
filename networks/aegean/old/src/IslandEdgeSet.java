/*
 * IslandEdgeSet.java
 *
 * Created on 27 July 2006, 16:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import java.util.Random;



/**
 * Defines set of all edges for the Island Network.
 *<br> replaces edgeValue[][]
 * @author time
 */
public class IslandEdgeSet 
{
        final static String IESVERSION = "IES060825";
    
        int numberSites;
        private int numberEdges = -1; // this is set positive when edges have been ranked by value
        EdgeMode edgeMode;
        private IslandEdge [][] edge; // edge values from site i to site j
       // edgeRankList[r] = j*numbersites + k = the number of the edge ranked r-th is [j][k] where
        private int [] edgeRankList; 
        private double [] outEdgeStrength;
        private double [] inEdgeStrength;
        
        // Separation (effective distance) parameters
        int metricNumber; // set type of separation (eff.distance) measurements to use
        final int numberMetrics=6; // no. of possible metrics numbered 0 .. (numberMetrics-1)
        double DijkstraMaxSep;
        final double MAXSEPARATION = 8.8e22;

        // Display characteristics - these are currently usually implemented via colours
        // and these are calculated in islandNetwork.calcNetworkStats
        double displayMaximumEdgeWeight; // display edges above this weight as thickness and colour of largest possible edge
        double displayMinimumEdgeWeight; // display edges below this weight as thickness and colour of smallest possible non-zero edge
        public double displayZeroEdgeWeight; // display edges below this weight as zero weight edges
        
        private double distanceDiameter =-1; // negative means not set yet
    
    /** 
     * Creates a new instance of IslandEdgeSet 
     *@param ns number of sites.
     *@param edgeModeValue =0 for binary mode, >0 max value mode, <0 max Out strength Mode
     */
    public IslandEdgeSet(int ns, double edgeModeValue) 
    {
        numberSites=ns;
        outEdgeStrength = new double [numberSites];
        inEdgeStrength = new double [numberSites];
        edge = new IslandEdge [numberSites][numberSites] ;
        edgeMode = new EdgeMode(edgeModeValue);
        for (int i=0; i<numberSites; i++) 
        {
          outEdgeStrength[i] = 0;  
           inEdgeStrength[i] = 0;  
          for (int j=0; j<numberSites; j++) edge[i][j] = new IslandEdge();
        }
       
        // Distance parameters
        metricNumber=0; // set type of separation (eff.distance) measurements to use
        DijkstraMaxSep = MAXSEPARATION;        
    }

    /** 
     * Creates a new instance of IslandEdgeSet by deep copying existing one.
     *@param es IslandEdgeSet to copy.
     */
    public IslandEdgeSet(IslandEdgeSet es) 
    {
        numberSites=es.numberSites;
        outEdgeStrength = new double [numberSites];
        inEdgeStrength = new double [numberSites];
        edge = new IslandEdge [numberSites][numberSites] ;
        edgeMode = new EdgeMode(es.edgeMode);
        for (int i=0; i<numberSites; i++)
        {
            outEdgeStrength[i] = es.outEdgeStrength[i] ;  
            inEdgeStrength[i] = es.inEdgeStrength[i] ;  
            for (int j=0; j<numberSites; j++)  edge[i][j] = new IslandEdge(es.edge[i][j]);
        }
        
        // Distance parameters
        metricNumber=es.metricNumber; // set type of separation (eff.distance) measurements to use
        DijkstraMaxSep = es.DijkstraMaxSep;
        
        // Display characteristics - these are currently usually implemented via colours
        // and these are calculated in islandNetwork.calcNetworkStats
        displayMaximumEdgeWeight = es.displayMaximumEdgeWeight; // display edges above this weight as thickness and colour of largest possible edge
        displayMinimumEdgeWeight = es.displayMinimumEdgeWeight; // display edges below this weight as thickness and colour of smallest possible non-zero edge
        displayZeroEdgeWeight = es.displayZeroEdgeWeight; // display edges below this weight as zero weight edges
        
    }

    /*
     * Returns edge.
     *@param i source (from) site
     *@param j target (to) site
     *@return edge[i][j]
     */
    public IslandEdge getEdge(int i, int j)
    {
        return(edge[i][j]);
    }

    /*
     * Returns edge value.
     *@param i source (from) site
     *@param j target (to) site
     *@return edgeValue[i][j]
     */
    public double getEdgeValue(int i, int j)
    {
        return(edge[i][j].value );
    }
    
    /*
     * Returns edge value squared.
     *@param i source (from) site
     *@param j target (to) site
     *@return edgeValue[i][j]
     */
    public double getEdgeValueSquared(int i, int j)
    {
        return(edge[i][j].value * edge[i][j].value );
    }

    /*
     * Returns edge Colour.
     *@param i source (from) site
     *@param j target (to) site
     *@return edgeColour[i][j]
     */
    public double getEdgeColour(int i, int j)
    {
        return(edge[i][j].colour );
    }

    /*
     * Returns edge physical fixed distance.
     *@param i source (from) site
     *@param j target (to) site
     *@return edgeDistance[i][j]
     */
    public double getEdgeDistance(int i, int j)
    {
        return(edge[i][j].distance );
    }

    /*
     * Returns edge physical fixed diameter distance.
     *@return fixed distance diamater
     */
    public double getDistanceDiameter()
    {
        if (distanceDiameter<0)
        {
            for (int i=0; i<numberSites; i++){
                for (int j=0; j< numberSites; j++){
                    if (distanceDiameter<edge[i][j].distance) distanceDiameter =edge[i][j].distance;
                }
            }
        }
        return( distanceDiameter);
    }

        /*
     * Returns edge penalty.
     *@param i source (from) site
     *@param j target (to) site
     *@return edgePenalty[i][j]
     */
    public double getEdgePenalty(int i, int j)
    {
        return(edge[i][j].penalty );
    }

    /*
     * Sets and returns edge Distance potential under model 1.
     *@param i source (from) site
     *@param j target (to) site
     *@return edge[i][j].potential1
     */
    public double getEdgePotential1(int i, int j)
    {
        return(edge[i][j].potential1);
    }

    /*
     * Sets and returns edge Distance potential under model 1 using separation (effective distance).
     *@param i source (from) site
     *@param j target (to) site
     *@return old edgePotential1(distance[i][j])
     */
    public double getEdgePotentialSeparation1(int i, int j, IslandHamiltonian H)
    {
        return(edge[i][j].getEdgePotentialSeparation1(H));
    }

    /*
     * Returns edge separation, an effective distance..
     *@param i source (from) site
     *@param j target (to) site
     *@return old distance[i][j]
     */
    public double getEdgeSeparation(int i, int j)
    {
        return(edge[i][j].separation);
    }

    /**
     * Gives the in degree of vertex.
     * @param i vertex number
     * @return the in degree of vertex i 
     */
    public int getInDegree(int i) 
    {  
        int d=0;
        for (int j=0; j<numberSites; j++) if (edge[j][i].value>0) d++; 
        return( d ) ;
    };
    
      /**
     * Gives the Out degree of vertex.
     * @param i vertex number
     * @return the out degree of vertex i
     */
    public int getOutDegree(int i) 
    {  
        int d=0;
        for (int j=0; j<numberSites; j++) if (edge[i][j].value>0) d++; 
        return( d ) ;
    };
    
    
    /*
     * Returns out edge Strength.
     *@param i site
     *@return outEdgeStrength[i]
     */
    public double getOutEdgeStrength(int i)
    {
        return(outEdgeStrength[i]);
    }

    /*
     * Sets and returns edge Value.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new colour value
     *@return edgeValue[i][j]
     */
    public double setEdgeValue(int i, int j, double value)
    {
        double dv = value-edge[i][j].value;
        outEdgeStrength[i] += dv;
        inEdgeStrength[j] += dv;
        edge[i][j].value=value;
        if (Math.abs(outEdgeStrength[i] -calcOutStrength(i))>1e-6) 
        {
            System.out.println("Wrong value for outEdgeStrength["+i+"]");
        }
        if (Math.abs(inEdgeStrength[j] -calcInStrength(j))>1e-6) 
        {
            System.out.println("Wrong value for inEdgeStrength["+j+"]");
        }
        return(value);
    }

    /*
     * Sets and returns edge Value if under maximum In/Out strengths.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new colour value
     *@param maxInStrength maximum in strength, no update if exceeded
     *@param maxOutStrength maximum in strength, no update if exceeded
     *@return edgeValue[i][j]
     */
    public double setEdgeValue(int i, int j, double value, double maxInStrength, double maxOutStrength)
    {
        double currentValue = edge[i][j].value;
        double dv = value-currentValue;
        if (outEdgeStrength[i] +dv>maxOutStrength) return(currentValue);
        if (inEdgeStrength[j] +dv>maxInStrength) return(currentValue);
        return(setEdgeValue(i,j,value));
    }
    
    
    /*
     * Sets and returns edge Colour.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new colour value
     *@return edgeColour[i][j]
     */
    public double setEdgeColour(int i, int j, double value)
    {
        edge[i][j].colour=value;
        return(value);
    }

        /*
     * Sets and returns fixed edge Distance.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new Distance value
     *@return old distValue[i][j]
     */
    public double setEdgeDistance(int i, int j, double value)
    {
        edge[i][j].distance=value;
        return(value);
    }

        /*
     * Sets and returns edge Penalty.
     *@param i source (from) site
     *@param j target (to) site
     *@param value new penalty value
     *@return edgePenalty[i][j]
     */
    public double setEdgePenalty(int i, int j, double value)
    {
        edge[i][j].penalty=value;
        return(value);
    }

        /*
     * Sets and returns edge separation, an effective distance..
     *@param i source (from) site
     *@param j target (to) site
     *@param value new penalty value
     *@return old distance[i][j]
     */
    public double setEdgeSeparation(int i, int j, double value)
    {
        edge[i][j].separation=value;
        return(value);
    }

    /**
     * Sets the edge values for all sites to value except for loops which are zero.
     * @param value set the edge values to this number
     */
    public void setEdgeValues(double value) 
    {
       for (int i=0; i<numberSites; i++) 
        for (int j=0; j<numberSites; j++) if (i==j) setEdgeValue(i,j,0.0);
        else setEdgeValue(i,j,value);       
    }

   /**
     * Sets the edge values for all sites.
     * @param max absolute gives maximum value each edge (+ve) or maximum out strength
    *@param maxOutStrength maximum value for out strength (set large if no limit)
     */
    public void setRandomEdgeValues(double max, double maxOutStrength) 
    {
       if (max==0) {setRandomBinaryEdgeValues(); return;} 
       Random rnd = new Random();
       double x=max;
       int i=-1;
       int j=-1;
       Permutation sourcePerm = new Permutation(numberSites);
       Permutation targetPerm;
       for (int ip=0; ip<numberSites; ip++) 
       {
           i = sourcePerm.next();
           targetPerm = new Permutation(numberSites);                
           if (max<0) x=-rnd.nextDouble()*max/numberSites; 
           for (int jp=0; jp<numberSites; jp++) 
           {
                j = targetPerm.next();
                if (i==j) setEdgeValue(i,j,0.0);
                else setEdgeValue(i,j,x*rnd.nextDouble() , 9999.0, maxOutStrength );       
           }
       }
    }

     /**
     * Sets the edge values to be 0 or 1 for all sites.
     */
    public void setRandomBinaryEdgeValues() 
    {
       Random rnd = new Random();
       for (int i=0; i<numberSites; i++) 
          for (int j=0; j<numberSites; j++) if (i==j) setEdgeValue(i,j,0.0); 
                                            else setEdgeValue(i,j,(rnd.nextBoolean()?1.0:0));
    }
    
    /**
     * Calculates the in strength of given site.
     * @param n the site of interest
     */
    public double calcInStrength(int n) 
    {
        double v=0;
       for (int i=0; i<numberSites; i++) v+=getEdgeValue(i,n);
        return v;
    }

    /**
     * Calculates the out strength of given site.
     * @param n the site of interest
     */
    public double calcOutStrength(int n) 
    {
        double v=0;
       for (int i=0; i<numberSites; i++) v+=getEdgeValue(n,i);
        return v;
    }
    /**
     * Calculates the in strength squared of given site.
     * @param n the site of interest
     */
    public double calcInStrengthSquared(int n) 
    {
        double v=0;
        double ev=-1;
       for (int i=0; i<numberSites; i++) {ev=getEdgeValue(i,n); v+=ev*ev;}
        return v;
    }

    /**
     * Calculates the out strength sqaured of given site.
     * @param n the site of interest
     */
    public double calcOutStrengthSquared(int n) 
    {
        double v=0;
        double ev=-1;
       for (int i=0; i<numberSites; i++) {ev=getEdgeValue(n,i); v+=ev*ev;}
        return v;
    }

    /**
     * Sets the distance potential values for model 1 for all sites.
     * @param H the Hamiltonian to use
     */
    public void setEdgePotential1(IslandHamiltonian H) 
    {
       for (int i=0; i<numberSites; i++) 
        for (int j=0; j<numberSites; j++) edge[i][j].setEdgePotential1(H);
        
    }
    
// **************************************************************************

    public class EdgeMode {
        boolean binary;
        boolean maxValueModeOn;
        boolean outStrengthLimitOn;
        double maximumValue;        
    
    /** Creates a new instance of EdgeMode.
     *@param edgeMode =0 for binary mode, >0 max value mode, <0 max Out strength Mode
     */
    public EdgeMode(double edgeMode) {
        setEdgeMode(edgeMode);     
    }

     /** Creates a new instance of EdgeMode by deep copy.
     *@param em existing edgeMode
      */
    public EdgeMode(EdgeMode em) {
        binary = em. binary;
        maxValueModeOn = em.maxValueModeOn;
        outStrengthLimitOn = em. outStrengthLimitOn;
        maximumValue = em. maximumValue;        
    }

    /** Sets value of EdgeMode.
     *@param edgeMode =0 for binary mode, >0 max value mode, <0 max Out strength Mode
     */
    public void setEdgeMode(double edgeMode) {
        if (edgeMode==0) setBinaryModeOn();
        else if (edgeMode>0) setMaxValueModeOn(edgeMode);
        else setMaxOutStrengthModeOn(edgeMode);     
    }

    /* Sets binary mode on.
     */
    public void setBinaryModeOn()
    {
        binary=true;
        maxValueModeOn=false;
        outStrengthLimitOn=false;
        maximumValue=1.0;
    }

    /* Sets Maximum Value Mode mode on.
     *@param value maximum value for each edge
     */
    public void setMaxValueModeOn(double value)
    {
        binary=false;
        maxValueModeOn=true;
        outStrengthLimitOn=false;
        maximumValue=Math.abs(value);
    }

    /* Sets Maximum Out Strength Mode mode on.
     *@param value maximum value for each edge
     */
    public void setMaxOutStrengthModeOn(double value)
    {
        binary=false;
        maxValueModeOn=false;
        outStrengthLimitOn=true;
        maximumValue=Math.abs(value);
    }
    
    public String description()
    {
        String s="";
        if (binary) s=s+"Binary Edge Values";
        if (maxValueModeOn) s= s+"Maximum Edge Value "+maximumValue;
        if (outStrengthLimitOn) s= s+"Limits on Out Strength "+maximumValue;
        return(s);
    }

    /** Returns status as an edgeModeValue.
     *@return 0 for binary mode, >0 max value mode, <0 max Out strength Mode
     */
    public double edgeModeValue()
    {
        double edgeModeValue=-987654321;
        if (binary) edgeModeValue=0;
        if (maxValueModeOn) edgeModeValue=maximumValue;
        if (outStrengthLimitOn) edgeModeValue=-maximumValue;
        return(edgeModeValue);
    }
}

    
        // *********************************************************
    /** Runs Dijkstra's algorithm on the network.
     * Does Dijkstra, updates distanceFromV global
     * Sets edgeSet.getEdgeSeparation(i,j) to shortest distance from i to j 
     * using metricNumber to choose the for metric.
     * and DijkstraMaxSep is the longest path between any two sites,
     * and it equals MAXSEPARATION if disconnected
     */
//           *@param metricNumber = 0 plain physical distance, 
//                                = 1 physical distance divided by potential
//                                = 2 inverse edge strength        

    public void doDijkstra(IslandSite [] siteArray) 
    {
        final double superMaxSep=MAXSEPARATION*1.0001;
        double newsep,eee,eeew;
        DijkstraMaxSep=0;
            for (int v=0; v<numberSites; v++) 
            {// look at distances from vertex v
                int mdv=v; // vertex number with minimum separation from v
                double minseparation =MAXSEPARATION; // separation equivalent to infinity
                boolean [] notVisited = new boolean[numberSites];
                for (int i=0; i<numberSites; i++) 
                {
                    setEdgeSeparation(v,i,MAXSEPARATION); notVisited[i]=true;
                };
                setEdgeSeparation(v,v,0.0);
                for (int n=0; n<numberSites; n++) 
                {   // first find mdv, the unvisited vertex with smallest separation from v
                    minseparation = MAXSEPARATION;
                    for (int j=0; j<numberSites; j++)
                        if ( notVisited[j] && (getEdgeSeparation(v,j)<minseparation) ) 
                        {
                         minseparation=getEdgeSeparation(v,j); 
                         mdv = j;
                        };
                        if (minseparation==MAXSEPARATION) break;    // must be finished
                        // visit mdv (fix its separation)  and update separation from v to j the neighbours of mdv
                        notVisited[mdv]=false;
                        for (int j=0; j<numberSites; j++) 
                        {
                            if (!notVisited[j]) continue;
                            eee=getEdgeValue(mdv,j);
                            // if (eee==0) continue; 
                            //Deal with zero edges on metric by metric basis, often by catching an error
                            //eeew=siteArray[mdv].getWeight() *eee  ;
                            newsep = superMaxSep; // if any below fail set new separation to infinite separation
                            try{ // this is the metric
                                switch (metricNumber) 
                                {
                                    case 5:
                                        newsep = minseparation+ getEdgeDistance(mdv,j) / (getEdgePotential1(mdv,j)) ;
                                        break;
                                    case 4:
                                        newsep = minseparation+ 1.0 /  (siteArray[mdv].getWeight() *eee  )  ;
                                        break;
                                    case 3:
                                        newsep = minseparation+ getEdgeDistance(mdv,j) / (getEdgePotential1(mdv,j)* (siteArray[mdv].getWeight() *eee  ) ) ;
                                        break;
                                    case 2:
                                        newsep = minseparation+ 1.0 /  eee  ;
                                        break;
                                    case 1:
                                        newsep = minseparation+ getEdgeDistance(mdv,j) /( getEdgePotential1(mdv,j)* eee ) ;
                                        break;
                                    case 0:
                                    default:
                                        newsep = minseparation+ getEdgeDistance(mdv,j);
                                }
                                if (getEdgeSeparation(v,j)>newsep) setEdgeSeparation(v,j,newsep);
                            } finally{};
                        }//eo for v                       
                }//eo for n
                if (DijkstraMaxSep<minseparation) DijkstraMaxSep=minseparation;
            } // eo for v
        
    }

// *********************************************************

    /**
     * Gives the metric type as a String
     * @return the metric number 
     * 
     */
    public String getMetricString() 
    {  
        //int metricNumber=getMetricNumber();
        String s="unknown";
        switch (metricNumber)
        {
            case 0: s="plain physical distance"; break;
            case 1: s="sum of individual physical distance/potential using edge values";  break;
            case 2: s="inverse edge value"; break;
            case 3: s="sum of individual physical distance/potential using edge weights"; break;
            case 4: s="inverse edge weight"; break;       
            case 5: s="sum of physical distance/potential (but no edge values)"; break;       
            default: s="unknown";
        }
        return(s) ;
    };

// ***************************************************************************************
   /* Returns the edge number of edge of given rank.
    *@param rank the rank of the required edge
    *@return the number of the edge from i to j of requested rank in the format i*numbersites + j 
      */   
   public int getEdgeGivenRank(int rank){
       if (numberEdges<0) calcRandomEdgeWeightOrder();
       return(edgeRankList[rank]);
   }
    
    
  // ...................................................................

/*
 * @(#)QSortAlgorithm.java  1.3   29 Feb 1996 James Gosling
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
  */

/**
 * A quick sort demonstration algorithm
 * SortAlgorithm.java
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @version     @(#)QSortAlgorithm.java 1.3, 29 Feb 1996
 */

// Adapted to sort index array
    
    //public class QSortAlgorithm extends SortAlgorithm {
   /** This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    *   QuickSort(a, 0, a.length - 1);
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition
    * @param hi0     right boundary of array partition
    */

  
//   public void sort(int a[]) throws Exception
//   {
//      QuickSort(a, 0, a.length - 1);
//   }
//}end of sort
  
// ---------------------------------------------------------      
   /** 
    * Sets edgeRankList with edges ordered by their edgeweight values
    * according to criteria (see compareWeights(...))
    */    
  public void calcRandomEdgeWeightOrder()  
  {
       Random Rnd = new Random(); //Schildt p524
       numberEdges = numberSites * numberSites;
      // edgeRankList[i] = the edge ranked ith is [j][k] where
       // i = j*numbersites + k
       edgeRankList = new int[numberEdges];
      // set initial list in order 
      for (int e=0; e<numberEdges; e++) edgeRankList[e]=e;
// Randomise initial list to deal with equality
      int eb,e2;
      for (int e=0; e<numberEdges; e++) 
      {
          e2=Rnd.nextInt(numberEdges);
          if (e2 != e) { 
                         eb=edgeRankList[e2];
                         edgeRankList[e2]=edgeRankList[e];
                         edgeRankList[e]=eb;
                         }
      }
      QuickSort(edgeRankList, 0, numberEdges-1);

      return; 
  }

   /** 
    * Sorts integer array.
    *@param a returned as list of indices of edgeweightlist in ranked order.
    *@param lo0 the starting index
    *@param hi0 the maximum index (inclusive)
    */      
    private void QuickSort(int a[], int lo0, int hi0)
   {
      int T;
      int lo = lo0;
      int hi = hi0;
      int midindex;
      double mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         midindex = ( lo0 + hi0 ) / 2 ;
         mid=edge[midindex/numberSites][midindex%numberSites].value;
//         mid = a[ ( lo0 + hi0 ) / 2 ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to
             * the partition element starting from the left Index.
             */
            // while( ( lo < hi0 ) && ( a[lo] < mid ) ) 
            while( ( lo < hi0 ) && ( edge[a[lo]/numberSites][a[lo]%numberSites].value  < mid ) )
               ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
            //while( ( hi > lo0 ) && ( a[hi] > mid ) )
            while( ( hi > lo0 ) && ( edge[a[hi]/numberSites][a[hi]%numberSites].value  > mid ) )
               --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            { // swap elements
               T = a[lo]; 
               a[lo] = a[hi];
               a[hi] = T;
               
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            QuickSort( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            QuickSort(a, lo, hi0 );

      }
   }

// *********************************************************************************
    
    
    
}
