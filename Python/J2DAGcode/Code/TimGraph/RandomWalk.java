/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph;

import java.util.Random;
import java.io.PrintStream;

//import TimUtilities.TimRandom;

/**
 * Random walk on graph defined by vertexList[].
 * The average walk length and maximum walk length set in the constructor arguments
 * Mode of walks set by bits of the integer randomWalkMode argument of the constructor.
 * Always start new walks with a vertex [edge] if (randomrandomWalkMode & 1)=1 [0]
 * If (randomrandomWalkMode & 2)>0 then always jumps to restart a walk
 * otherwise the walk length just rests the diffusion parameters not its location
 * when walk length reached or no exit available.
 * If (randomrandomWalkMode & 4)>0 [0] then uses random  process for walk
 * else uses fixed length walks to achieve average of averageWalkLength (global) length walks.
 * If (randomrandomWalkMode & 16)>0 [0] then uses binomial distributed walks with averageWalkLength (global)
 * walks and the binomialNumber setting the number of dice (&lt;1 gives fixed length, =1 flat distribution).
 * The alternative is a Markov process where walks continue with probability choosen to achieve averageWalkLength.
 *
 * If starting a new walk from a new vertex then it uses a random vertex unless the setStartVertex routine is used.
 * If vertices are labelled the rank is set using averageWalkLength and (rankingProbability)^averageWalkLength
 * while number of visits is recorded.  Both assumed to be initialised as this is not done in the routine.
 *
 * @author time
 */
    
    
    public class RandomWalk 
    {   
        String SEP="\t"; 
        
        private timgraph graph;
        private int numberVertices;
        private int numberEdges;
        
        public int randomWalkMode=1;
        private boolean StartWalkWithVertex; // if choosing start use random vertex [edge]
        private boolean always_new_walk_start; // true if use start from current walkvertex
        private boolean markov_walk; // true if length uses Markov process
        //private boolean random_connectivity ; //
        private boolean StartWalkWithFixedVertex; // true if starting from random or fixed vertex
        private boolean binomialDistribution;  // true if use binomial else use Markovian for walk length
        private double averageWalkLength; // 
        private double walkprob ; // walk length probability for MArkovian walklengths set from walklength
        private double maximumWalkLength ; // longest allowed single walk length
        private int intAverageWalkLength; //set integer walk length               
        private int startVertex; // initial vertex if using new vertex start, <0 if using random choice
        private int stepOnWalk;
        
        int walkvertex; // current vertex
        int degree; // degree of current vertex
        
        
        private boolean rankingOn; // set true if want ranking to be updated
        private double diffuseValue; // current ranking value
        private double rankingProbability; // probability to use if ranking the walk
        
        // statistics on walks
        int numberWalks;
        int totalStepLength;
        int longestWalk; // longest single walk   
        
        private Random Rnd;
        
        public int infoLevel =-2;

 /**
  * RandomWalk constructor.
  * <br>Sets up random walk with no ranking and no fixed vertex starting point by default.
  *<br>All parameters are taken from those in the graph.
  *@param tg graph upon which random walk is to be performed.
   */        
       public RandomWalk(timgraph tg)
        {
           initialise(tg, tg.randomWalkMode, tg.averageWalkLength, tg.maxWalkLength);
       }
       
  /**
  * RandomWalk constructor.
  * Sets up random walk with no ranking and no fixed vertex starting point by default.
  * <br>parameters provided override any set in the graph.
  *@param tg graph upon which random walk is to be performed.
  *@param randomWalkMode sets mode of random walk
  *@param avWalkLength sets average length of random walks
  *@param maxWalkLength sets maximum length of random walks
  */        
       public RandomWalk(timgraph tg, int randomWalkMode, double avWalkLength, double maxWalkLength)
        {initialise(tg, randomWalkMode, avWalkLength, maxWalkLength);}
       
 /**
  * Initialises random walk with no ranking and no fixed vertex starting point by default.
  * <br>parameters provided override any set in the graph.
  *@param tg graph upon which random walk is to be performed.
  *@param randomWalkMode sets mode of random walk
  *@param avWalkLength sets average length of random walks
  *@param maxWalkLength sets maximum length of random walks
  */        
       public void initialise(timgraph tg, int randomWalkMode, double avWalkLength, double maxWalkLength)
       {
             graph=tg;
             numberVertices=graph.getNumberVertices();
             numberEdges=graph.getNumberStubs();
             Rnd = tg.Rnd;
             infoLevel=graph.infoLevel;
             setStartVertex(-1);  //  must use setStartVertex if want to use fixed starting point
             StartWalkWithVertex = ((randomWalkMode & 1) >0); // v&1
             always_new_walk_start = ((randomWalkMode & 2) >0);  // v&2
             markov_walk = ((randomWalkMode & 4) >0);  // v&4
//        random_connectivity = ((randomrandomWalkMode & 8) >0);  // v&8
             binomialDistribution = ((randomWalkMode & 16) >0);  // v&16
             
             averageWalkLength=avWalkLength;
             walkprob = averageWalkLength/(1.0+averageWalkLength);
             maxWalkLength  = ((int) (averageWalkLength + 0.5))*4;
             intAverageWalkLength= (int) (averageWalkLength+0.5); //set integer walk length
             
             maximumWalkLength = maxWalkLength; // maximum single walk length
             
             startVertex=-1;
             walkvertex=-1;  // make sure initialise the walkvertex for first time.
             degree=-1;

            // statistics on walks
            longestWalk=-1;
            totalStepLength=0;
            numberWalks=0;
             
             // set ranking off, must set up by hand 
             rankingOn=false;
             diffuseValue=-1; // current ranking value
             
             if (infoLevel>2) {
                 //printParam();
                 System.out.println("StartWalkWithVertex, always_new_walk_start, markov_walk, random_connectivity "+StartWalkWithVertex
                         + SEP+ markov_walk);
             }
                         
        }
        
        
         /**
          * Sets the ranking on.
          * Parameter used to reduce the ranking diffusion value. 
          *@param rankingProbabilityLengthScale the graph distance equivalent to the ranking probability
          */
        public void setRankingOn(double rankingProbabilityLengthScale)  
        {         
            rankingOn=true; // set true if want ranking to be updated
            rankingProbability = rankingProbabilityLengthScale/(1.0+rankingProbabilityLengthScale);
        }
         
         /**
          * Sets the start vertex parameter and associated boolean flag.
          * startVertex is used if there is a fixed starting point.
          * If this is negative it will choose appropriate random vertex.
          * StartWalkWithFixedVertex is set false.
          *@param  newStartVertex the number of the startVertex, sometimes used as the initial vertex, if <0
          */
        public int setStartVertex(int newStartVertex)  
{
            startVertex=newStartVertex;
            if (startVertex >= graph.getNumberVertices()) 
            {
                System.out.println("*** Error startVertex "+startVertex + ">="+ graph.getNumberVertices()+" TotalNumberVertices");
                return -1;
            }
            if (startVertex < 0)  StartWalkWithFixedVertex = false;      
            else StartWalkWithFixedVertex = true;      
            return startVertex;            
        }
        
        
        /**
         * Performs one random walk.
         * <p>Will start from last point on walk (walkvertex) if you set the mode properly.
         * Stops if reaches vertex of zero out degree or reaches set number of steps.
         * @param binomialNumber number used in generating binomila distribution
         * @return index of vertex where walk ended
         */
        public int doOneWalk(int binomialNumber)  
    {
        stepOnWalk=-1; // this is the number steps left on this walk
        int e=-1;
        
        // Set length of walk
        if (markov_walk) {
            if (binomialDistribution) stepOnWalk=getRandomBinomial(averageWalkLength,binomialNumber);
            else stepOnWalk=getRandomMarkov(walkprob,binomialNumber);
            if (maximumWalkLength<stepOnWalk) stepOnWalk=(int) maximumWalkLength;
        } else stepOnWalk = intAverageWalkLength; //fixed length walks
        if (longestWalk<stepOnWalk) longestWalk=stepOnWalk;
        
        diffuseValue=1.0; // used for ranking
        
        // reset current walkvertex to new start if needed
        if ((always_new_walk_start) || (walkvertex<0))
        {
            if (StartWalkWithVertex)
                walkvertex = (StartWalkWithFixedVertex ? walkvertex=startVertex : Rnd.nextInt(numberVertices) ); //getRandomVertex();
            else { // find random edge for start
                if (graph.directedGraph) e = graph.makeEven(Rnd.nextInt(numberEdges)) ;
                else e = Rnd.nextInt(numberEdges) ;
                walkvertex=graph.getVertexFromStub(e);
            }//eo if StartWalkWithVertex
            degree=graph.vertexList[walkvertex].size();
        } 
        // degree assumed set if walkVertex is
        
        if (rankingOn && graph.vertexlabels) updateRanking();

        while ((stepOnWalk>0)  && (degree>0))
        { 
            stepOnWalk--;
            totalStepLength++; 
            walkvertex = graph.vertexList[walkvertex].getQuick(Rnd.nextInt(degree));                          
            degree=graph.vertexList[walkvertex].size();                
            if (rankingOn && graph.vertexlabels) updateRanking();
        } // eo while

        numberWalks++;
 
     return(walkvertex);
    }
        
// -------------------------------------------------------------------
        /**
         * Updates ranking statistics using random walk values.
         */
        public void updateRanking()  
        {
                  graph.vertexLabelList[walkvertex].rank.updateRanking(stepOnWalk, diffuseValue);
                  diffuseValue*=rankingProbability;
              }

        
// --------------------------------------------------------------------------    
    /** 
     * Prints mode for random walk to standard output.
     */
    public void printrandomWalkMode()  
    {
        printRandomWalkMode(System.out,"");
        return;
     }      

    // ------------------------------------------------------------------------
    /**
     * Generates integer random numbers binomially distributed.
     * @param average is mean value
     * @param N is number of 'dice' used
     *@return integer between 0 and (int) average*2 inclusive
     */
    public int getRandomBinomial(double average, int N)
    {
        if (N<1) return (int)(average+1e-6);
        double total=0;
        for (int n=0;n<N;n++) total+=Rnd.nextDouble();
        return ( (int) (total*average/((double) N) ) );
    }

    // ------------------------------------------------------------------------
    /**
     * Generates integer random numbers Markov distributed.
     * @param prob is the probability you continue.
     * @param N is maximum number of steps to make 
     *@return integer between 0 and N inclusive
     */
    public int getRandomMarkov(double prob, int N)
    {
        if (N<0) return (-N);
        int n=0;
        for (n=0;n<N;n++) if (Rnd.nextDouble()>prob) break;
        return n;
    }

    
        // --------------------------------------------------------------------------    
    /** 
     * Prints mode for random walk to standard output.
     */
    public void printRandomWalkMode()  
    {
        printRandomWalkMode(System.out,"");
        return;
     }      

        /**
     * 
     * Prints mode for random walk to printstream.
     * @param cc comment string
     * @param PS a PrintStream such as System.out
     */
    public void printRandomWalkMode(PrintStream PS, String cc)  
    {
       printRandomWalkMode(PS,cc,randomWalkMode);
    }
           
        /**
     * 
     * Prints mode for random walk to printstream.
     * @param cc comment string
     * @param PS a PrintStream such as System.out
     * @param rwMode mode number, bits set the various modes
     */
    public void printRandomWalkMode(PrintStream PS, String cc, int rwMode)  
    {
        if ((rwMode & 1)>0) PS.println(cc+"   Start every walk from random vertex");
        else  PS.println(cc+"   Start every walk from random end of random edge");
        if ((rwMode & 2)>0) PS.println(cc+"   Jump to new vertex when walk length reached (every new edge)");
        else  PS.println(cc+"   Do not jump to new vertex when walk length reached (unless new event in walk graph creation or no exits in random walk");
        if ((rwMode & 4)>0) PS.println(cc+"   Walk length variable, average fixed");
        else  PS.println(cc+"   Walk length fixed");
        if ((rwMode & 8)>0) PS.println(cc+"   No. of edges added to each new vertex variable, average fixed");
        else  PS.println(cc+"   No. of edges added to each new vertex fixed");
        if ((rwMode & 16)>0) PS.println(cc+"   Binomial distributed walk lengths");
        else  PS.println(cc+"   Markovian walk lengths");
    }
           
        /**
     * 
     * Prints mode for random walk to printstream.
     * @param cc comment string
     * @param PS a PrintStream such as System.out
     */
    public void printAllModes(PrintStream PS, String cc)  
    {
        int m=1;
        while (m<32){ printRandomWalkMode(PS,cc+"(rwm & "+m+"): ",m);
        m=m<<1;  
        }
 
    }
    
}//eo RandomWalk class


