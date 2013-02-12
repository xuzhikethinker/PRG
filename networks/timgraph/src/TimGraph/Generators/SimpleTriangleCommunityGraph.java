/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Generators;

import TimGraph.timgraph;



/**
 * Create random graph with communities using triangles.
 * @author time
 */
public class SimpleTriangleCommunityGraph extends SimpleERCommunityGraph {

final static String nameSTCG="Simple Triangle Community Graph";
final static String shortNameSTCG="STCG";

double avTriangleSourceNumber;
int nTriangles;

    /**
     * Creates random graph with communities.
     * <p>Note that averageTriangleSourceNumber is the number of triangles sourced per vertex.
     * <p>Initialises important quantities
     * @param numberCommunities number of communities
     * @param sizeCommunities number of vertices per community
     * @param averageTriangleSourceNumber average number of tria=ngles to source from each vertex
     * @param prob probability of links outside communities
     * @param args arguments to pass to the timgraph
     */
    public SimpleTriangleCommunityGraph(int numberCommunities, int sizeCommunities, double averageTriangleSourceNumber, double prob, String [] args){
            graphName=SimpleTriangleCommunityGraph.nameSTCG;
            shortGraphName=SimpleTriangleCommunityGraph.shortNameSTCG;
            tg = new timgraph(args);
            tg.setVertexEdgeList(true); // needed to prevent multiple edges
            nCommunities=numberCommunities;
            nVertices = nCommunities * sizeCommunities;
            avTriangleSourceNumber = averageTriangleSourceNumber;
            nTriangles = (int) Math.round(nVertices*avTriangleSourceNumber);
            nStubsMax = nTriangles*6;
            avDegree=nStubsMax/nVertices;
            probability=prob;
            System.out.println("nVertices, nTriangles, nStubs "+nVertices+", "+nTriangles+", "+nStubsMax);
            tg.setNetworkWithVertices(nVertices, nStubsMax);
//            initialise();
//            create();
    }

        /**
     * Makes a random community graph.
     * <p>If first argument starts with a ':'
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>)
     * the rest of that argument is used by this routine.
     * Remaining arguments start with a '-' and are parsed by timgraph.
     * <p>Arguments are:-
         * <ol>
         * <li>number of communities (4)
         * <li>size of communities (32)
         * <li>average number of triangles assigned to each source vertex (1.5)
         * <br>    = (number triangles per vertex)/3
         * <br>    = (average degree)/6 (9)
         * <li>probability for connecting second and thrid vertices outside the source (first) vertices community
         * (0.085786438)
         * <br>prob=(2*(numberCommunities-1)/numberCommunities)
//             - Math.sqrt(numberCommunities*numberCommunities*(4-3*pER) - numberCommunities*(8-pER)+4)/numberCommunities;
         * <li> y or t (n or f) if want a systematic (random) choice made for first vertex of each triangle (t)
         * </ol>
     * @param args list of arguments
     */
    public static void main(String[] args) {


     int ano=0;
     int numberCommunities=4;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) numberCommunities=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     int sizeCommunities=32;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) sizeCommunities=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     double averageTriangleSourceNumber=1.5;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) averageTriangleSourceNumber=Double.parseDouble(args[ano].substring(1, args[ano].length()));

     // p_ER = (p/3)(4-2p+p(nc-2)/(nc-2))
     ano++;
//     double avDegreeER=1/9.0;
//     double pER=1/avDegreeER;
//     double prob=(2*(numberCommunities-1)/numberCommunities)
//             - Math.sqrt(numberCommunities*numberCommunities*(4-3*pER) - numberCommunities*(8-pER)+4)/numberCommunities;
     double prob=0.085786438; // value if pER=1/9 and nc=4
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) prob=Double.parseDouble(args[ano].substring(1, args[ano].length()));

     ano++;
    /**
      *  The first vertex, s, is chosen from within the community,
      * either at random or going through in order
      */
     boolean firstSystematic=true;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano]))
         firstSystematic= TimUtilities.StringUtilities.Filters.StringFilter.trueString(args[ano].substring(1, args[ano].length()));


     String [] arguments={"-o255"};
     if (args.length>0) arguments=args;

     SimpleTriangleCommunityGraph rg = new SimpleTriangleCommunityGraph(numberCommunities, sizeCommunities, averageTriangleSourceNumber, prob, arguments);

     System.out.println("--- Created "+rg.desciption("\n"));

     rg.initialise();
     rg.setName();
     rg.create(firstSystematic);
     rg.labelVertices();

     rg.information();
    }


/**
 * Creates graph
 * <p>The first vertex, s, is chosen from within the community, either at
 * random or going through in order.
 * @param firstSystematic true (false) if want to cycle through community vertices systematically (randomly) for first vertex
 */
        public void create(boolean firstSystematic){
        int sizeCommunities=nVertices/nCommunities;
        createCommunityStructure(sizeCommunities);
        createTriangleCommunities(firstSystematic);
    }

   /**
    * Creates communities of Triangles in Erdos-Reyni mannaer from given parameters.
    * <p>The first vertex, s, is chosen from within the community, either at
    * random or going through in order.  The number of
    * source vertices from each community is
    * half the average degree times the community size. The second, t, and
    * third vertices, u, are found from within the same community
    * as the first vertex, s, with probability (1-p)
    * or from another community with probability p.
    * Note that the second and third vertices may be from within the same
    * community as each other. In all cases the target
    * is chosen uniformly from the set of allowed vertices.
    * This may try to create an edge more than once.  In this case
    * the weight is increased by one if its weighted otherwise the edge is left unchanged.
    * Multiple edges are not created.
    * @param firstSystematic true (false) if want to cycle through community vertices systematically (randomly) for first vertex
    */
    public void createTriangleCommunities(boolean firstSystematic){

        int s=-1; // first vertex index
        int t=-1; // second vertex index
        int u=-1; // third vertex index
        int ncs=-1; // number vertices in community of source vertex
        // cs community of source vertex
        for (int cs=0; cs<nCommunities; cs++){
            ncs = firstVinC[cs+1] - firstVinC[cs];
            int m= ((int) (avTriangleSourceNumber*ncs));
            // number of edges to add to source vertices within one community
            for (int i=0; i<m; i++){
                s=firstVinC[cs]+(firstSystematic? i:rnd.nextInt(ncs));
                t=findTarget(probability, s, cs, ncs);
                u=s;
                while ((u==s) || (u==t)) u=findTarget(probability, s, cs, ncs);
                //System.out.println("edge="+(cs*m+i)+", s="+s+", t="+t);
                tg.increaseEdgeWeight(s, t, 1.0);
                tg.increaseEdgeWeight(t, u, 1.0);
                tg.increaseEdgeWeight(u, s, 1.0);
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
                +sep+this.nTriangles+" triangles, "
                +sep+this.avTriangleSourceNumber*3+" average triangles per vertex";
    }


//    /**
//     * Sets name of graph
//     */
//    @Override
//    public void setName(){
//        graphName=SimpleTriangleCommunityGraph.nameSTCG;
//        shortGraphName=SimpleTriangleCommunityGraph.shortNameSTCG;
//        tg.setNameRoot(shortName());
//    }


}
