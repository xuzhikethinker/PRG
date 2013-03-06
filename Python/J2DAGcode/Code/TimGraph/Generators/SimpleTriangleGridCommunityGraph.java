/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Generators;

import TimGraph.timgraph;
import java.util.Random;

/**
 * Creates a rectangular grid, each point in the grid representing one
 * vertex, so there are <tt>dimX*dimY</tt> of these vertices.
 * Connections are made using triangles.
 * In each row and coloumn the first vertex is connected to all
 * other vertices.  Then each remaining vertex in the same row and column
 * is connected to a vertex with lower index that it but not the first vertex.
 * The edges go to a randomly chosen vertex where the choice is limited
 * with probability <tt>probability</tt> to be from one of the two communities
 * associated with the source (based on its coordinate in the grid). Otherwise
 * a totally random vertex is chosen.
 * @author time
 */
public class SimpleTriangleGridCommunityGraph extends TriangleGridCommunityGraph {

    final static String nameSTGCG="Simple Triangle Grid Community Graph";
    final static String shortNameSTGCG="STGCG";




    /**
     * <p>Simple graph of overlapping communities based on triangles.
     * <p>Vertices laid out in a square grid, one per grid point.
     * For each row or column, (dimX-1) or (dimY-1) triangles are created.
     * The first vertex is always 0, the second incremented from 1 to dim
     * and the third is chosen uniformly to be between 0 and j.
     * @param nX X dimension
     * @param nY Y dimension
     * @param scale
     * @param args
     */
    public SimpleTriangleGridCommunityGraph(int nX, int nY, double scale,
            String [] args){
            graphName=nameSTGCG;
            shortGraphName=shortNameSTGCG;
            tg = new timgraph(args);
            tg.setVertexEdgeList(true); // needed to prevent multiple edges
            setName();
            dimX=nX;
            dimY=nY;
            dimN=1; // needed for some inherited routines =nC;
            typeX=-1;
            typeY=-1;
            typeN=-1;
//           setRandomDistribution(typeN, dimN, distN);
//           setRandomDistribution(typeX, dimX, distX);
//           setRandomDistribution(typeY, dimY, distY);
            nVertices=dimX*dimY*dimN;
            avTriangleSourceNumber=2;
            nTrianglesMax = (int) Math.round(nVertices*avTriangleSourceNumber);
            nTrianglesCurrent=0;
            nStubsMax = nTrianglesMax*6;
            avDegree=nStubsMax/nVertices;
            probability=-1;
            nCommunities=dimX+dimY;
            System.out.println("nVertices, nTriangles, nStubs "+nVertices+", "+nTrianglesMax+", "+nStubsMax);
            tg.setNetwork(nVertices, nStubsMax);


            rnd = new Random();
            createSimple(scale);
            
    }

    /**
     * Makes a simple graph of overlapping communities based on triangles.
     * <p>For each row or column, (dimX-1) or (dimY-1) triangles are created.
     * The first vertex is always 0, the second incremented from 1 to dim
     * and the third is chosen uniformly to be between 0 and j.
     <p>Arguments are:-
         * <ol>
         * <li>X dimension for type 1 vertex grid (2)
         * <li>Y dimension for type 1 vertex grid (2)
         * <li>Scale for vertex positioning (200)
         * </ol>
      * @param args list of arguments for the graph, e.g. -o255 to control output options
     */
    public static void main(String[] args) {


     int ano=0;
     int dimX=6;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) dimX=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     int dimY=6;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) dimY=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     String [] arguments={"-o255"};
     if (args.length>0) arguments=args;

     ano++;
     double scale=200;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) scale=Double.parseDouble(args[ano].substring(1, args[ano].length()));

     SimpleTriangleGridCommunityGraph tgcg = new SimpleTriangleGridCommunityGraph(dimX,dimY, scale, arguments);

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
     * @param scale scale for vertex positions
     */
    public void createSimple(double scale){

           createVertices(scale);
           createTrianglesSimply();
    }





    /**
     * Creates the edges.
     * <p>For each row or column, (dimX-1) or (dimY-1) triangles are created.
     * The first vertex is always 0, the second incremented from 1 to dim
     * and the third is chosen uniformly to be between 0 and j.
     */
     public void createTrianglesSimply()
     {
         nTrianglesCurrent=0;
         int x1,x2,x3, y1,y2,y3, v1,v2,v3;

         for (x1=0; x1<dimX; x1++) {
             y1=0;
             for (y2=2; y2< dimY; y2++){
                 y3=(y2==2?1:this.rnd.nextInt(y2-1)+1);
                 v1=this.getIndex(0, x1, y1);
                 v2=this.getIndex(0, x1, y2);
                 v3=this.getIndex(0, x1, y3);
                 tg.increaseEdgeWeight(v1, v2, 1.0);
                 tg.increaseEdgeWeight(v2, v3, 1.0);
                 tg.increaseEdgeWeight(v3, v1, 1.0);
             }
         }
         
         for (y1=0; y1<dimY; y1++) {
             x1=0;
             for (x2=2; x2< dimX; x2++){
                 x3=(x2==2?1:this.rnd.nextInt(x2-1)+1);
                 v1=this.getIndex(0, x1, y1);
                 v2=this.getIndex(0, x2, y1);
                 v3=this.getIndex(0, x3, y1);
                 tg.increaseEdgeWeight(v1, v2, 1.0);
                 tg.increaseEdgeWeight(v2, v3, 1.0);
                 tg.increaseEdgeWeight(v3, v1, 1.0);
             }
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
                +sep+" average number time vertices are source vertex for triangles "+this.avTriangleSourceNumber;
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
        return shortGraphName+"x"+dimX+"y"+dimY;
    }

    /**
     * Sets name of graph
     */
    @Override
    public void setName(){
        tg.setNameRoot(shortName());
    }



}
