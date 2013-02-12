/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Generators;

import TimGraph.Coordinate;
import TimGraph.VertexLabel;
import TimGraph.timgraph;
import java.util.Random;

/**
 * Creates a rectangular grid, each point in the grid representing a
 * community of dimN vertices, so there are <tt>dimX*dimY*dimN</tt>
 * of these vertices.
 * Connections are made using triangles.
 * The edges go to a randomly chosen vertex where the choice is limited
 * with probability <tt>probability</tt> to be from one of the two communities
 * associated with the source (based on its coordinate in the grid). Otherwise
 * a totally random vertex is chosen.
 * @author time
 */
public class TriangleGridCommunityGraph extends SimpleERCommunityGraph {

    final static String nameTGCG="Triangle Grid Community Graph";
    final static String shortNameTGCG="TGCG";



    /**
     * X Dimension of grid
     */
    int dimX=timgraph.IUNSET;
    /**
     * Y Dimension of grid
     */
    int dimY=timgraph.IUNSET;

    /**
     * Size of communities.
     * <p>Number vertices per grid point.
     */
    int dimN=timgraph.IUNSET;

    /**
     * Test to use to decide when enough edges added.
     * <p>Negative means not used.
     */
    protected int edgeTestType;

    /**
     * Average number of triangles added to each vertex
     */
    double avTriangleSourceNumber;

    /**
     * Number of triangles to create.
     * <p>Negative means not used.
     */
    protected int nTrianglesMax;


    /**
     * Number of triangles to create.
     */
    protected int nTrianglesCurrent;

    /**
     * Types of random distributions
     */
    int typeX=0;
    int typeY=0;
    int typeN=0;

    /**
     * General random grid point index distributions
     */
    private int [] distN;
    /**
     * General random X coordinate distributions
     */
    private int [] distX;
    /**
     * General random y coordinate distributions
     */
    private int [] distY;




     /**
     * true (false) if first visit each grid point in turn to add one triangle in each direction
     */
     boolean firstSystematic=true;
    /**
     * true (false) if on remaining visits are in systematic order
     */
     boolean systematic=false;


     /**
      * Null Constructor.
      * <p>Only provided so class  can be extended.
      */
     public TriangleGridCommunityGraph(){

     }

    /**
     * <p>Sets the distributions for random choice of grid index and coordinates.
     * For type indices see {@code TimGraph.Generators.TriangleGridCommunityGraph#setRandomDistribution}
     * @param nX X dimension
     * @param nY Y dimension
     * @param nC N dimension, number of points per grid point
     * @param tX type of X distribution (<=0 uniform)
     * @param tY type of Y distribution (<=0 uniform)
     * @param tN type of N (grid index) distribution (<=0 uniform)
     * @param eTestType  switch between triangles (0) or edges (1) number as limiting factor.
     * @param averageValue average number of triangles (0) or edges (1) to add per vertex
     * @param prob
     * @param firstSystematic true (false) if first visit each grid point in turn to add one triangle in each direction
     * @param systematic true (false) if on remaining visits are in systematic order
     * @param scale
     * @param args
     */
    public TriangleGridCommunityGraph(int nX, int nY, int nC,
            int tX, int tY, int tN,
            int etestType, double averageValue, double prob,
            boolean firstSyst, boolean syst, double scale,
            String [] args){
            graphName=nameTGCG;
            shortGraphName=shortNameTGCG;
            tg = new timgraph(args);
            tg.setVertexEdgeList(true); // needed to prevent multiple edges
            setName();
            dimX=nX;
            dimY=nY;
            dimN=nC;
            typeX=tX;
            typeY=tY;
            typeN=tN;
           setRandomDistribution(typeN, dimN, distN);
           setRandomDistribution(typeX, dimX, distX);
           setRandomDistribution(typeY, dimY, distY);
            nVertices=dimX*dimY*dimN;
            edgeTestType=etestType;
           switch (edgeTestType){
             case 1:  nStubsMax= (int) Math.round(averageValue*nVertices)+6; 
                      // last triangle could push over edge so set max for one extra triangle
                      nTrianglesMax = nStubsMax/6;
                      avTriangleSourceNumber =nTrianglesMax/nVertices;

             break;
             case 0:
             default: avTriangleSourceNumber = averageValue;
                      nTrianglesMax = (int) Math.round(nVertices*avTriangleSourceNumber);
                      nStubsMax = nTrianglesMax*6;
            }
            nTrianglesCurrent=0;
            avDegree=nStubsMax/nVertices;
            probability=prob;
            nCommunities=dimX+dimY;
            System.out.println("Maximum vertices, triangles, stubs "+nVertices+", "+nTrianglesMax+", "+nStubsMax);
            tg.setNetwork(nVertices, nStubsMax);


            rnd = new Random();
            create(firstSyst, syst, scale);
            
    }

    /**
     * Makes a random graph of overlapping communities based on triangles.
     * <p>If first argument starts with a ':'
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>)
     * the rest of that argument is used by this routine.
     * Remaining arguments start with a '-' and are parsed by timgraph.
     * <p>Creates a grid of vertices with communities for each row and each column.
     * Each vertex is visited initially and given two triangles whose other
     * vertices may be in each of the two communities associated with the first vertex
     * or may be assigned random communities.
     * After that either source vertices are chosen at random or
     * they can be visited in numerical order but remaining vertices in triangles are found
     * using the probability given.
     * <p>Arguments are:-
         * <ol>
         * <li>X dimension for type 1 vertex grid (2)
         * <li>Y dimension for type 1 vertex grid (2)
         * <li>Number of vertices at each grid coordinate (32)
         * <li>average number of triangles assigned to each source vertex (3.0, must be at least 2.0)
         * <br>    = (number triangles per vertex)/3
         * <br>    = (average degree)/6 (9)
         * <li>probability for connecting second and third vertices outside the source (first) vertices community
         * (0.085786438)
         * <li> y or t (n or f) if want a systematic (random) choice made for first vertex of each triangle (t)
         * <li>t (or f) if want subsequent connections (not) to be done with type one
         * <br>vertices visited in numerical order.
         * <li>Scale for vertex positioning (200)
         * </ol>
      * @param args list of arguments for the graph, e.g. -o255 to control output options
     */
    public static void main(String[] args) {


     int ano=0;
     int dimX=2;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) dimX=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     int dimY=2;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) dimY=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     int dimC=32;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) dimC=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     int edgeTestType=1;
     char edgeTestTypeChar='k';
     double averageValue = 16.0;
     if (args.length>ano ) {
         edgeTestTypeChar=args[ano].charAt(1);
         if (timgraph.isOtherArgument(args[ano])) averageValue=Double.parseDouble(args[ano].substring(2, args[ano].length()));
     }
     switch (edgeTestTypeChar){
         case 'k': edgeTestType =1;
         System.out.println("Limit on average degree is "+averageValue);
         break;
         case 't':
         default: edgeTestType =0;
         System.out.println("Limit on average number triangles is "+averageValue);
     }


//     ano++;
//     int numberStubs=(dimX*dimY*dimC)*(2+nPerC2*2);
//     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) numberStubs=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     // p_ER = (p/3)(4-2p+p(nc-2)/(nc-2))
     ano++;
     double prob=0.0; // 0.5 value if pER=1/9 and nc=4
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) prob=Double.parseDouble(args[ano].substring(1, args[ano].length()));

     ano++;
    /**
      *  The first sweep of type 1 vertices is done in order (true) or randomly (false)
      */
     boolean firstSystematic=true;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano]))
         firstSystematic= TimUtilities.StringUtilities.Filters.StringFilter.trueString(args[ano].substring(1, args[ano].length()));

     ano++;
    /**
      *  Remaining vertices are chosen in order (true) or randomly (false)
      */
     boolean systematic=false;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano]))
         firstSystematic= TimUtilities.StringUtilities.Filters.StringFilter.trueString(args[ano].substring(1, args[ano].length()));

     ano++;
    /**
      *  Uniform or Pref Attachment.
      *  lowest bit = type X,
      *  second bit = type Y
      *  third bit = type N
      *  bit=0 uniform, bit=1 pref attachment
      * e.g. 0 = uniform for all, 7=pref attachment for all,
      *      3 = uniform for grid index, pref. attachment for X and Y.
      */
     int typeIndex=0;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) typeIndex=Integer.parseInt(args[ano].substring(1, args[ano].length()));
     int tX =  (((typeIndex & 1)==1) ? 1 : 0);
     int tY =  (((typeIndex & 2)==2) ? 1 : 0);
     int tN =  (((typeIndex & 4)==4) ? 1 : 0);


     ano++;
     double scale=200;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) scale=Double.parseDouble(args[ano].substring(1, args[ano].length()));

     String [] arguments={"-o255"};
     if (args.length>0) arguments=args;

     TriangleGridCommunityGraph tgcg = new TriangleGridCommunityGraph(dimX,dimY, dimC,
             tX,tY,tN,
             edgeTestType, averageValue, prob,
             firstSystematic, systematic, scale, arguments);

     System.out.println("--- Created "+tgcg.desciption("\n"));

     //tgcg.initialise();
     tgcg.setName();
     //double scale=200; // scale for vertex positions
     //tgcg.create(firstSystematic, systematic, scale);
     //tgcg.labelVertices();
     tgcg.information();


    }

    /**
     * Creates graph
     * <p>The first vertex, s, is chosen from within the community, either at
     * random or going through in order.
     * @param firstSyst true (false) if want to first sweep of type one vertices systematically (randomly) for first vertex
     * @param syst true (false) if want to cycle through type one vertices systematically (randomly) for first vertex
     * @param scale scale for vertex positions
     */
    public void create(boolean firstSyst, boolean syst, double scale){

           createVertices(scale);
           createTriangles(firstSyst,syst);
    }

    /**
     * Creates vertices with appropriate labels including positions.
     * <p>Vertices are placed at intelligent locations.
     * @param scale scale for vertex positions
     */
    public void createVertices(double scale){
        final double bigScaleFactor =3.5;
        tg.initialiseVertexLabels();
        VertexLabel vl;
        VertexGridCoordinates t = new VertexGridCoordinates();
        // Place type one vertices in circles of this radius.
        // Set equal to scale if one per grid point
        double radius = (dimN<2?scale:scale*dimN/(Math.PI*2));
        for (int v1=0; v1<this.nVertices; v1++){
            t.set(v1);
            Coordinate ccc = t.getCoordinate(radius, bigScaleFactor);
            vl = new VertexLabel(v1,t.vectorString(),t.getCoordinate(radius, bigScaleFactor));
            vl.setNumber(v1/dimN);
            tg.addVertex(vl);
        }
    }




    /**
     * Creates the edges.
     * <p>Every vertex is first visited and one triangle is created.
     * If <tt>firstSystematic</tt> is true then the first two triangles
     * connected to each vertex (i.e. when it is the primary source vertex
     * of a triangle) are set within each of its two communities.  Otherwise
     * the other vertices of these triangles are set in the usual random way
     * and may be in other communities.
     * <p>If <tt>systematic</tt> is true then adds remaining triangles by
     * working through source vertices for the triangles in numerical order.  Otherwise
     * these are chosen at random.  Note in both cases the other two vertices are
     * chosen using the probabilities given so may lie in other communities.
     * @param firstSyst if true (false) works through vertices in numerical (random) order once.
     * @param syst if true (false) works through remaining type one vertices in numerical (random) order.
     */
     public void createTriangles(boolean firstSyst, boolean syst)
     {
         firstSystematic=firstSyst;
         systematic=syst;
         nTrianglesCurrent=0;
         double p=1-probability;
         double phalf=p/2;
         //double r;
         int v1=0;
         // Add two edges to each vertex, either to fixed community or usual random choice.
         int res=0;
         for (v1=0; v1<tg.getNumberVertices(); v1++){
             if (firstSystematic) {
                 addTriangleFirstCommunityOnly(v1);
                 res = addTriangleSecondCommunityOnly(v1);
             }
             else{
                 addTriangle(v1, phalf,p);
                 res= addTriangle(v1, phalf,p);
             }
             if (res<0) break;
         }

         // now choose source vertex at random or in sequence
         while (moreEdgesNeeded()){
             if (systematic) {
                 v1++;
                 if (v1 >= tg.getNumberVertices() ) v1=0;
             }
             else v1=rnd.nextInt(tg.getNumberVertices());
             addTriangle(v1, phalf, p);
         }

     }

     /**
      * Tests to see if need to continue adding edges.
      * <p>USes global <tt>edgeTestType</tt>.
      * @return true (false) if nee to keep adding edges
      */
     private boolean moreEdgesNeeded(){
         switch (this.edgeTestType){
             case 1: return (tg.getMaximumStubs()-tg.getNumberStubs()>8);  // aim to get within 3 or target
             case 0:
             default:
                 return (this.nTrianglesCurrent < this.nTrianglesMax);
         }
     }

     private int addTriangleFirstCommunityOnly(int v1){
         return addTriangle(v1, 2.0, 3.0);
     }

     private int addTriangleSecondCommunityOnly(int v1){
         return addTriangle(v1, -1.0, 3.0);
     }

     /**
      * Adds random triangle.
      * <p>If random number less than phalf then uses y coordinate of given source vertex to
      * set community of both remaining vertices and chooses randomly within this.
      * If random number between phalf and p then uses x coordinate of source vertex to
      * set community and chooses remaining vertices randomly within this.
      * Otherwise chooses at remaining vertices random from whole set.  In all cases
      * the three vertices are distinct.
      * Always ensures that vertices for triangle are different but does not check to see
      * if edges already exist in graph.  The weight is just increased by one.
      * <p>Negative probabilities and probabilities greater than one can be used to force
      * triangles to be entirely within one community or not to be in another community.
      * @param v1 type one source vertex
      * @param phalf prob that we use y coordinate community
      * @param p prob that we use x coordinate community
      * @return current total number of triangles if one added, negative if nothing added.
      */
     private int addTriangle(int v1, double phalf, double p){
         if ((this.edgeTestType==0) && nTrianglesCurrent>=nTrianglesMax){
             System.err.println("*** In addTriangle, current number of triangles is too large, nothing added. current, max= "+nTrianglesCurrent+", "+nTrianglesMax);
            return -1;
         }
         if ((this.edgeTestType==1) && (tg.getMaximumStubs()-tg.getNumberStubs())<6){
             System.err.println("*** In addTriangle, current number of stubs is within 6 of maximum, nothing added. current, max= "+tg.getNumberStubs()+", "+tg.getMaximumStubs());
            return -2;
         }
            VertexGridCoordinates s = new VertexGridCoordinates(v1);
            double r=rnd.nextDouble();
            int v2=v1;
            int v3=v1;

            if (r<phalf){
                while (v2==v1) v2 = getIndex(rnd.nextInt(dimN), rnd.nextInt(dimX), s.y);
                while ((v3==v1) || (v3==v2)) v3 = getIndex(rnd.nextInt(dimN), rnd.nextInt(dimX), s.y);
            }
            else if(r<p){
                while (v2==v1) v2 = getIndex(rnd.nextInt(dimN), s.x,rnd.nextInt(dimY));
                while ((v3==v1) || (v3==v2)) v3 = getIndex(rnd.nextInt(dimN), s.x,rnd.nextInt(dimY));
            }
            else{
                while (v2==v1) v2 = rnd.nextInt(tg.getNumberVertices());
                while ((v3==v1) || (v3==v2)) v3 = rnd.nextInt(tg.getNumberVertices());
            }

            tg.increaseEdgeWeight(v1, v2, 1.0);
            tg.increaseEdgeWeight(v2, v3, 1.0);
            tg.increaseEdgeWeight(v3, v1, 1.0);
            System.out.println("T"+nTrianglesCurrent+"=("+v1+","+v2+","+v3+")");
            return ++nTrianglesCurrent;
     }

    /**
     * Finds a target vertex.
     * <p>It finds a target vertex from
     * within the same y community with probability phalf,
     * within the same x community with probability (p-phalf),
     * or at random with probability (1-p).
     * <p>Negative probabilities and probabilities greater than one can be used to force
     * target to be entirely within one community
     * or not to be in another community.
     * @param s source vertex
     * @param phalf prob that we use y coordinate community
     * @param p prob that we use x coordinate community
     * @return global index of target vertex
     */
    private int findTarget(VertexGridCoordinates s, double phalf, double p){
            double r=rnd.nextDouble();
            int v=-1;
            if (r < phalf) {
                 v = getIndex(rnd.nextInt(dimN), rnd.nextInt(dimX), s.y);
             } else {
                 if (r < p) {
                   v = getIndex(rnd.nextInt(dimN), s.x,rnd.nextInt(dimY));
                 } else {
                     v = rnd.nextInt(tg.getNumberVertices());
                 }
             }
        return v;
    }



/**
 * Sets the distributions for random choice of grid index and coordinates.
 * Types are:-
 * <ul>
 * <li>0 and default: uniform</li>
 * <li>1: proportional to index</li>
 * </ul>
 */
    private void setRandomDistribution(int type, int dim, int [] dist){
        switch (type){
            // probability proportional to index
            case 1: 
                int d= (dim+1)*dim/2;
                dist = new int[d];
                int j=0;
                for (int n=0; n<dim; n++) for (int i=0; i<(n+1); i++) dist[j++]=n; return;
            
            // Uniform distribution
            case 0: 
            default:
              dist = new int[dim];
              for (int n=0; n<dim; n++) dist[n]=n; 
        }
    }

    /**
     * Returns a random number for a single grid point.
     * <p>In range from 0 to (dimN-1).
     * @return random grid number.
     */
    private int getRandomN(){
        if (distN.length>0) return distN[rnd.nextInt(distN.length)];
        return rnd.nextInt(dimN);
    }

    /**
     * Returns a random X coordinate.
     * <p>In range from 0 to (dimX-1).
     * @return random X coordinate.
     */
    private int getRandomX(){
        if (distX.length>0) return distX[rnd.nextInt(distX.length)];
        return rnd.nextInt(dimX);
    }

    /**
     * Returns a random Y coordinate.
     * <p>In range from 0 to (dimY-1).
     * @return random Y coordinate.
     */
    private int getRandomY(){
        if (distY.length>0) return distY[rnd.nextInt(distY.length)];
        return rnd.nextInt(dimY);
    }

   /**
     * Provides string with long description of input parameters.
     * @param sep separation string e.g. line feed
     * @return string with long description
     */
    @Override
    public String desciption(String sep){
        return basicDescription(sep)+", "
                +sep+" grid "+this.dimX+" by "+this.dimY
                +sep+" number vertices per grid point "+this.dimN
                +sep+" limit average degree "+String.format("%6.3f",this.avDegree)
                +sep+" actual average degree "+String.format("%6.3f",tg.getAverageDegree())
                +sep+" number stubs "+tg.getNumberStubs()
                +sep+" average number time vertices are source vertex for triangles "+String.format("%6.3f",this.avTriangleSourceNumber)
                +sep+" actual value "+this.nTrianglesCurrent/tg.getNumberVertices()
                +sep+" actual number triangles added "+this.nTrianglesCurrent
                +sep+" actual number of triangles in graph "+tg.getNumberTriangles()
                +sep+" limit on "+((this.edgeTestType==1)?"degree":"triangles");
    }



    /**
     * Provides string with short descriptive name suitable for a file name.
     * @return string with short description
     */
    @Override
    public String shortName(){
        String mstring="m";
        if (Math.abs(Math.round(avDegree)-avDegree)<1e-6) mstring=mstring+((int) (avDegree+1e-6));
        else mstring=mstring+avDegree;
        return shortGraphName+"n"+typeChar(typeN)+dimN+"x"+typeChar(typeX)+dimX+"y"+typeChar(typeY)+dimY
                +(firstSystematic?"s":"r")+(systematic?"s":"r")
                +(this.edgeTestType==0?"t"+this.nTrianglesMax:"s"+this.nStubsMax)+"p"+((int) (1000*probability));
    }

    /**
     * Character describing type of distribution used
     * @param type
     * @return
     */
    private char typeChar(int type){
        switch (type){
            case 1: return 'p';
            case 0:
                default:
                    return 'u';
        }
    }
 

    /**
     * Sets name of graph
     */
    @Override
    public void setName(){
        tg.setNameRoot(shortName());
    }


    /**
     * Calculates index of vertex from coordinates
     * @param n number of vertex at one grid point
     * @param x x coordinate of gid point
     * @param y x coordinate of gid point
     * @return index of vertex
     */
    int getIndex(int n, int x, int y){
            return (n+dimN*(x+dimX*y));
        }


    /**
     * Used to set grid coordinates from vertex number.
     * The index of type one vertices on the grid is
     * <code>v = (y*dimX+x)*dimN+n</code>
     */
    class VertexGridCoordinates{
        /**
         * index within grid point community.
         * <p><tt>dimN</tt> vertices associated with one grid point
         */
        public int n=-1;
        /**
         * X coordinate on grid
         */
        public int x=-1;
        /**
         * Y coordinate on grid
         */
        public int y=-1;

        /**
         * Basic constructor
         */
        VertexGridCoordinates(){}
        
        /**
         * Basic constructor
         * @param v index of type one vertex
         */
        VertexGridCoordinates(int v){
            set(v);
        }
        
        /**
         * Sets coordinate associated with type one vertex.
         * @param v index of type one vertex
         */
        void set(int v){
             n = v%dimN;
             int xy=v/dimN;
             x=xy%dimX;
             y=xy/dimX;
        }


        /**
         * Returns a 2D coordinate suitable for representation.
         * <p>If <tt>dimN</tt> is one then radius is used to separate grid points
         * and points are placed on grid.  Otherwise the <tt>dimN</tt> points for
         * each (x,y) grid point are placed in a circle of given radius and the
         * grid points are separated by <tt>radius*bigScaleFactor</tt> suggesting that
         * <tt>bigScaleFactor</tt> should be at least 3.0.
         * @param radius radius of circle used for each grid point (n coordinate)
         * @param bigScaleFactor number of radii between grid points
         * @return two dimensional coordinate suitable for display.
         */
        Coordinate getCoordinate(double radius, double bigScaleFactor){
            if (dimN<2) return new Coordinate(x*radius,y*radius);
            return new Coordinate((Math.cos(n*Math.PI*2.0/dimN)+x*bigScaleFactor)*radius,(Math.sin(n*Math.PI*2.0/dimN)+y*bigScaleFactor)*radius);
        }


        /**
         * String for display representing coordinate.
         * @return string for display
         */
        String vectorString(){
        return "("+(dimN>1?n+",":"")+x+","+y+")";
        }


    }

}
