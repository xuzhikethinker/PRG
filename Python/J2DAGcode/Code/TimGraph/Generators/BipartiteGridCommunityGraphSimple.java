/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Generators;

import TimGraph.Coordinate;
import TimGraph.OutputMode;
import TimGraph.VertexLabel;
import TimGraph.algorithms.BipartiteTransformations;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import java.util.Random;

/**
 * Creates a rectangular grid of type one vertices, so there are <tt>dimX*dimY</tt>
 * of these.
 * Each type two vertex is associated with either one x ccordinate or one
 * y coordinate, and there are <tt>nPerC2</tt> vertices per type two community
 * giving <tt>dimX*dimY*nPerC2</tt> type two vertices.
 * Bipartite connections are made from a randomly chosen type one source vertex.
 * The edges go to a randomly chosen type two vertex where the choice is limited
 * with probability <tt>probability</tt> to be from one of the two communities
 * associated with the source (based on its coordinate in the grid). Otherwise
 * a totally random type two vertex is chosen.
 * <p>Type 2 vertices represent the communities.
 * First ones are for grid (type 1) vertices of same y coordinate
 * Latter ones for those grid (type 1) vertices of same x coordinate.
 * @author time
 */
public class BipartiteGridCommunityGraphSimple extends SimpleERCommunityGraph {

    final static String nameBGCG="Bipartite Grid Community Graph";
    final static String shortNameBGCG="BGCG";
//    // These should be overrided in setName as needed.
//    String graphName=nameBGCG;
//    String shortGraphName=shortNameBGCG;



    /**
     * X Dimension of grid
     */
    int dimX=timgraph.IUNSET;
    /**
     * Y Dimension of grid
     */
    int dimY=timgraph.IUNSET;

    /**
     * Number type 2 vertices per community.
     */
    int nPerC2=timgraph.IUNSET;


    public BipartiteGridCommunityGraphSimple(int nX, int nY, int nPerC2input, int numberStubs, double prob,
            boolean firstSystematic, boolean systematic, double scale,
            String [] args){
            graphName=nameBGCG;
            shortGraphName=shortNameBGCG;
            tg = new timgraph(args);
            setName();
            dimX=nX;
            dimY=nY;
            nPerC2=nPerC2input;
            probability=prob;
            nCommunities=dimX+dimY;
            tg.setBipartite(dimX*dimY,nPerC2*nCommunities, "Grid", "RowColumn");
            nStubsMax = numberStubs;
            nVertices=tg.getNumberVerticesType1()+tg.getNumberVerticesType2();
            tg.setVertexEdgeList(true); // needed to prevent multiple edges
            tg.setNetwork(nVertices, nStubsMax);
            System.out.println("BipartiteGridCommunityGraph grid "+this.dimX+" by "+this.dimY);
//            initialise();
            rnd = new Random();
            create(firstSystematic, systematic, scale);
            project();
    }

    /**
     * Makes a random community graph.
     * <p>If first argument starts with a ':'
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>)
     * the rest of that argument is used by this routine.
     * Remaining arguments start with a '-' and are parsed by timgraph.
     * Creates a grid of type one vertices with each row and each column
     * associated with some type two vertices.
     * Each type one vertex is visted initially and given two edges which may be at random or
     * systematically to one of its row and one of its column type 2 vertices.
     * After that either type one vertices are chosen at random or
     * they can be visted in numerical order.
     * <p>Arguments are:-
     * <ol>
     * <li>X dimension for type 1 vertex grid (6)
     * <li>Y dimension for type 1 vertex grid (6)
     * <li>number of type 2 vertices assigned to each row and each column of type 1 grid (32)
     * <li>number stubs
     * <li>probability for connecting type 1 to randomly chosen type two.
     * <br> Otherise with prob (1-p)/2 will choose to connect to type 2 associated with row,
     * <br> and same prob will connect to type 2 associated with column
     * <li>t (or f) if want first connections (not) to be systematic to ensure connectivity.
     * <br>This means each vertex is connected once to it type two vertex for its row,
     * <br>and once to its column type 2 vertex.
     * <li>t (or f) if want subsequent connections (not) to be done with type one
     * <br>vertices visted in numerical order.
     * </ol>
     * @param args list of arguments
     */
    public static void main(String[] args) {


     int ano=0;
     int dimX=10;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) dimX=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     int dimY=10;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) dimY=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     int nPerC2=(dimX+dimY)/3;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) nPerC2=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     int numberStubs=(dimX*dimY)*(2+nPerC2*2);
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) nPerC2=Integer.parseInt(args[ano].substring(1, args[ano].length()));

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
      *  Remaining type 1 vertices are chosen in order (true) or randomly (false)
      */
     boolean systematic=false;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano]))
         firstSystematic= TimUtilities.StringUtilities.Filters.StringFilter.trueString(args[ano].substring(1, args[ano].length()));


     String [] arguments={"-o255"};
     if (args.length>0) arguments=args;

     double scale=200;
     BipartiteGridCommunityGraphSimple bg = new BipartiteGridCommunityGraphSimple(dimX,dimY, nPerC2, numberStubs, prob,
             firstSystematic, systematic, scale, arguments);

     System.out.println("--- Created "+bg.desciption("\n"));

     //bg.initialise();
     bg.setName();
     //double scale=200; // scale for vertex positions
     //bg.create(firstSystematic, systematic, scale);
     //bg.labelVertices();
     bg.information();


    }

    /**
     * Creates graph
     * <p>The first vertex, s, is chosen from within the community, either at
     * random or going through in order.
     * @param firstSystematic true (false) if want to first sweep of type one vertices systematically (randomly) for first vertex
     * @param systematic true (false) if want to cycle through type one vertices systematically (randomly) for first vertex
     * @param scale scale for vertex positions
     */
    public void create(boolean firstSystematic, boolean systematic, double scale){
           createVertices(scale);
           createEdges(firstSystematic,systematic);
    }

    /**
     * Creates vertices with appropriate labels including positions.
     * @param scale scale for vertex positions
     */
    public void createVertices(double scale){
        tg.initialiseVertexLabels();
        VertexLabel vl;
        int x=-1;
        int y=-1;
        for (int v1=0; v1<tg.getNumberVerticesType1(); v1++){
            x=v1%dimX;
            y=v1/dimY;
            vl = new VertexLabel(v1,"("+x+","+y+")",new Coordinate(x*scale,y*scale));
            tg.addVertex(vl);
        }

        //Type 2 represent the communities,
        //first ones are for vertices of same y coordinate
        // latter ones for those of same x coordinate.
        int n=-1;
        int delta=-1;
        for (int v2=0; v2<tg.getNumberVerticesType2(); v2++){
            n=v2/this.nPerC2;
            delta=v2%this.nPerC2;
            if (n<dimY) vl = new VertexLabel(v2+tg.getNumberVerticesType1(),"y"+n+"."+delta,
                    new Coordinate((dimX+delta)*scale,n*scale));
            else vl = new VertexLabel(v2+tg.getNumberVerticesType1(),"x"+(n-dimY)+"."+delta,
                    new Coordinate((n-dimY)*scale,(dimY+delta)*scale));
            tg.addVertex(vl);
        }
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
                +sep+" number type 2 vertices per community "+this.nPerC2;
    }


    /**
     * Creates the edges.
     * <p>Every type one vertex is first visted and one edge is created.
     * @param firstSystematic if true (false) works through type one vertices in numerical (random) order once.
     * @param systematic if true (false) works through remaining type one vertices in numerical (random) order.
     */
     public void createEdges(boolean firstSystematic, boolean systematic)
     {
         double p=1-probability;
         double phalf=p/2;
         //double r;
         int v1=0;
         // Add two edges to each vertex, either to fixed community or usual random choice.
         for (v1=0; v1<tg.getNumberVerticesType1(); v1++){
             if (firstSystematic) {
                 addEdge(v1, 2.0, 3.0);
                 addEdge(v1, -1.0, 3.0);
             }
             else{
                 addEdge(v1, phalf,p);
                 addEdge(v1, phalf,p);
             }
         }
//         else while (tg.getNumberEdges()<tg.getNumberVerticesType1()){
//             v1=rnd.nextInt(tg.getNumberVerticesType1());
//
//         }

         // now choose source vertex at random or in sequence
         while (tg.getNumberStubs()<this.nStubsMax){
             if (systematic) {
                 v1++;
                 if (v1 >= tg.getNumberVerticesType1() ) v1=0;
             }
             else v1=rnd.nextInt(tg.getNumberVerticesType1());
             addEdge(v1, phalf, p);
         }

     }

     /**
      * Adds bipartite edge.
      * If random number less than phalf then uses y coordiante of type 1 vertex to
      * set community (type 2 vertex community) and chooses randomly within this.
      * If random number between phalf and p then uses x coordiante of type 1 vertex to
      * set community and chooses randomly within this.  Otherwise chooses at random.
      * @param v1 type one source vertex
      * @param phalf prob that we use y coordinate community
      * @param p prob that we use x coordinate community
      * @return current total number of stubs, -1 if edge already exists.
      */
     private int addEdge(int v1, double phalf, double p){
             int x1=v1%dimX;
             int y1=v1/dimX;
             double r=rnd.nextDouble();
             int v2=-1;
             if (r < phalf) {
                 v2 = tg.getNumberVerticesType1() + y1 * nPerC2 + rnd.nextInt(nPerC2);
             } else {
                 if (r < p) {
                     v2 = tg.getNumberVerticesType1() + (dimY + x1) * nPerC2 + rnd.nextInt(nPerC2);
                 } else {
                     v2 = tg.getNumberVerticesType1() + rnd.nextInt(tg.getNumberVerticesType2());
                 }
             }
             return tg.addEdgeUnique(v1,v2);
     }

    /**
     * Creates a projection onto type one vertices with appropriate vertex names, numbers and positions.
     */
     public void project(){
         boolean ontoTypeOne=true;
         boolean makeLabelled=true;
         boolean makeWeighted=true;
         boolean makeVertexEdgeList=true;
         boolean noSelfLoops=true;
         timgraph pg = BipartiteTransformations.project(tg,
                 ontoTypeOne, makeLabelled, makeWeighted, makeVertexEdgeList, noSelfLoops);
         VertexLabel vl;
         for (int v=0; v<tg.getNumberVerticesType1(); v++){
             vl = new VertexLabel(tg.getVertexLabel(v));
             pg.setVertexLabelQuick(v,vl);
         }
         pg.setNameRoot(shortName()+"proj");
         FileOutput fopg = new FileOutput(pg);
         //fopg.informationGeneral("#", timgraph.SEP);
         String cc="#"; 
         String sep=timgraph.SEP;
         boolean printTriangles=true;
         boolean printSquares=true;
         boolean vertexListOn=true; 
         boolean edgeListOn=true; 
         boolean graphMLOn=true;
         boolean printNearestNeighbours=false;
         OutputMode outputControl = null; //new OutputMode(0);
         fopg.informationGeneral(cc, sep,
                 vertexListOn, printTriangles, printSquares,
                 printNearestNeighbours,
                 edgeListOn, graphMLOn, outputControl);

    }

    /**
     * Provides string with short decriptive name suitable for a file name.
     * @return string with short description
     */
    @Override
    public String shortName(){
        String mstring="m";
        if (Math.abs(Math.round(avDegree)-avDegree)<1e-6) mstring=mstring+((int) (avDegree+1e-6));
        else mstring=mstring+avDegree;
        return shortGraphName+"x"+dimX+"y"+dimY+"c"+nPerC2+"s"+nStubsMax+"p"+((int) (1000*probability));
    }

    /**
     * Sets name of graph
     */
    @Override
    public void setName(){
        tg.setNameRoot(shortName());
    }


}
