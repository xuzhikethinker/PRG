/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Generators;

import TimGraph.Community.VertexPartition;
import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import cern.colt.list.IntArrayList;
import java.util.Random;

/**
 * Generates a graph from a sequence of random subgraphs
 * which are then joined together.
 * @author time
 */
public class SimpleERCommunityGraph {

    final static String nameSERCG="Simple ER Community Graph";
    final static String shortNameSERCG="SERCG";

    // These should be overrided in setName as needed.
    String graphName=nameSERCG;
    String shortGraphName=shortNameSERCG;



    /**
     * Graph to be created.
     */
    timgraph tg;

    /**
     * Vertex to community translation table.
     * v2c[v]=c community of vertex v
     */
    int [] v2c;

    /**
     * Index of first vertex in community.
     * firstVinC[c] first vertex in community c
     * so that (firstVinC[c+1]-1) is last vertex in community c
     */
    int [] firstVinC;

    /**
     * Community to vertex translation table.
     * c2v[c]= set of vertices in community c
     */
    IntArrayList [] c2v;

    /**
     * Maximum number of vertices in graph.
     * <p>NO obvious reason why this should not be the actual number of vertices.
     */
    int nVertices;
    /**
     * Maximum number of stubs in graph.
     * <p>Can be used as a target.
     */
    int nStubsMax;
    /**
     * Target average degree in graph.
     * <p>Actual average degree may be different.
     */
    double avDegree;
    int nCommunities;
    /**
     * Basic probability.
     * <br>Value 1 = random graph, 0 equals completely regular.
     */
    double probability;


    /**
     * Random number generator.
     * <p>Best if used globally
     */
    Random rnd;


    public SimpleERCommunityGraph(){};

    /**
     * Creates random graph with communities.
     * <p>Initialises important quantities
     * @param numberCommunities number of communities
     * @param sizeCommunities number of vertices per community
     * @param averageDegree average degree
     * @param prob probability of links outside communities
     * @param args arguments to pass to the timgraph
     */
    public SimpleERCommunityGraph(int numberCommunities, int sizeCommunities, double averageDegree, double prob, String [] args){
            graphName=SimpleERCommunityGraph.nameSERCG;
            shortGraphName=SimpleERCommunityGraph.shortNameSERCG;
            tg = new timgraph(args);
            tg.setVertexEdgeList(true); // needed to prevent multiple edges
            nCommunities=numberCommunities;
            nVertices = nCommunities * sizeCommunities;
            avDegree = averageDegree;
            nStubsMax = (int) Math.round(nVertices*averageDegree);
            probability=prob;
            System.out.println("nVertices, nStubs "+nVertices+", "+nStubsMax);
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
     int averageDegree=9;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) averageDegree=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     double prob=1.0/averageDegree;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) prob=Double.parseDouble(args[ano].substring(1, args[ano].length()));

     String [] arguments={"-o255"};
     if (args.length>0) arguments=args;

     SimpleERCommunityGraph rg = new SimpleERCommunityGraph(numberCommunities, sizeCommunities, averageDegree, prob, arguments);

     System.out.println("--- Created "+rg.desciption(""));

     rg.initialise();
     rg.setName();
     rg.create();
     rg.labelVertices();

     rg.information();
    }

    /**
     * Generic routine to output information on graph.
     */
    public void information(){
     tg.printParametersBasic();

     FileOutput fotg = new FileOutput(tg);

     fotg.informationGeneral("", "\t");
     boolean asNames=true;
     boolean infoOn=true;
     boolean headerOn=true;
     boolean edgeIndexOn=true;
     boolean edgeLabelOn=false;
     boolean vertexNames=true;
     if (tg.getNumberEdges()<41) tg.printEdges(System.out, timgraph.COMMENTCHARACTER, timgraph.SEP,
                     timgraph.DUNSET, timgraph.IUNSET,
                     vertexNames, infoOn, headerOn, edgeIndexOn, edgeLabelOn);
     fotg.printEdges(asNames, infoOn, headerOn, edgeIndexOn, edgeLabelOn);
     fotg.informationNetworkBasic(timgraph.COMMENTCHARACTER, timgraph.SEP);
     fotg.edgeListSimple(true);
    }

    public void initialise(){
            rnd = new Random();
            v2c = new int [nVertices];
            firstVinC = new int [nCommunities+1];
            c2v = new IntArrayList[nCommunities];
            for (int c=0; c<nCommunities; c++) c2v[c] = new IntArrayList();
    }

    public void create(){
        int sizeCommunities=nVertices/nCommunities;
        createCommunityStructure(sizeCommunities);
        createERCommunities();
    }

    /**
     * Creates structures defining the communities.
     * @param sizeCommunities number of vertices per community
     */
    public void createCommunityStructure(int sizeCommunities){
        int c=-1;
        for (int v=0; v<nVertices; v++){
            c=v/sizeCommunities;
            v2c[v]=c;
            c2v[c].add(v);
        }
        for (c=0; c<=nCommunities; c++) firstVinC[c] = c*sizeCommunities;
    }

    /**
     * Switches on the vertex labels and sets them using community number.
     */
    public void labelVertices(){
        if (!tg.isVertexLabelled()) tg.initialiseVertexLabels();
        for (int v=0; v<tg.getNumberVertices(); v++) tg.setVertexNumber(v, v2c[v]);
    }

    public VertexPartition createVertexPartition(){
        double q = VertexPartition.DUNSET;
        return new VertexPartition(q, this.nVertices, this.nStubsMax/2, this.v2c);
    }

    /**
     * Creates communities of Erdos-Reyni type from given parameters.
     * <p>Each community provides the source vertices for a number of edges
     * equal to half the average degree multiplied by community size.
     * The target vertex in each case (never equal to the source) is chosen from
     * within the same community with probability (1-p)
     * or from another community with probability p.  In both cases the target
     * is chosen uniformly from the set of allowed vertices.
     * This may try to create an edge more than once.  In this case
     * the weight is increased by one if its weighted otherwise the edge is left unchanged.
     * Multiple edges are not created.
     */
    public void createERCommunities(){

        int s=-1; // source vertex index
        int t=-1; // target vertex index
        int ncs=-1; // number vertices in community of source vertex
        // cs community of source vertex
        for (int cs=0; cs<nCommunities; cs++){
            ncs = firstVinC[cs+1] - firstVinC[cs];
            int m= ((int) (avDegree*ncs))/2;
            // number of edges to add to source vertices within one community
            for (int i=0; i<m; i++){
                s=firstVinC[cs]+rnd.nextInt(ncs);
                t=findTarget(probability, s, cs, ncs);
                //System.out.println("edge="+(cs*m+i)+", s="+s+", t="+t);
                tg.increaseEdgeWeight(s, t, 1.0);
                }
        }
    }

    /**
     * Creates communities from given parameters.
     * <p>Sequence of generic subgraphs joined by edges
     * as defined from given parameters.
     * <p>Adds a number of edges equal to
     * half the average degree to every vertex in turn as the source.
     * It then finds a target vertex (not equal to the source) from
     * within the same community with probability (1-p)
     * or from another community with probability p.  In both cases the target
     * is chosen uniformly from the set of allowed vertices.
     */
    public void createSimpleCommunities1(){

        int m= ((int) (avDegree))/2; // number of edges to add  to each source
        int t=-1; // target vertex index
        int cs=-1; // community of source vertex
        int ncs=-1; // number vertices in community of source vertex

        for (int s=0; s<tg.getNumberVertices(); s++){
            cs = v2c[s];
            ncs = firstVinC[cs+1] - firstVinC[cs];
            for (int i=0; i<m; i++){
               t=findTarget(probability, s, cs, ncs);
               tg.addEdge(s, t);
            }
        }
    }
/**
 * Finds a target vertex.
 * <p>It finds a target vertex (not equal to the source) from
 * within the same community with probability (1-p)
 * or from another community with probability p.  In both cases the target
 * is chosen uniformly from the set of allowed vertices.
 * @param p probability for edge added between communities
 * @param s source vertex
 * @param cs community of source vertex
 * @param ncs number vertices in community of source vertex
 * @return global index of target vertex
 */
    public int findTarget(double p, int s, int cs, int ncs){
        int t=-1; // target vertex index
        if (rnd.nextDouble()<p){
                   t=firstVinC[cs+1]+rnd.nextInt(nVertices-ncs);
                   if (t>= nVertices) t=t-nVertices;
               }
               else{
                   t=firstVinC[cs]+rnd.nextInt(ncs);
                   while (t==s) t=firstVinC[cs]+rnd.nextInt(ncs);
               }

        return t;
    }

    /**
     * Provides string with long description of basic parameters.
     * @param sep separation string e.g. line feed
     * @return string with long description
     */
    public String basicDescription(String sep){
        return graphName
                +sep+" with "+this.nCommunities+" communities, "
                +sep+this.nVertices/this.nCommunities+" average vertices each, "
                +sep+this.nVertices+" vertices in total, "
                +sep+this.avDegree+" average degree, "
                +sep+probability+" probability ";
    }
    /**
     * Provides string with long decription of input parameters.
     * @param sep separation string e.g. line feed
     * @return string with long description
     */
    public String desciption(String sep){
        return basicDescription(sep);
    }
    /**
     * Provides string with short decriptive name suitable for a file name.
     * @return string with short description
     */
    public String shortName(){
        String mstring="m";
        if (Math.abs(Math.round(avDegree)-avDegree)<1e-6) mstring=mstring+((int) (avDegree+1e-6));
        else mstring=mstring+avDegree;
        return shortGraphName+"v"+nVertices+mstring+"c"+nCommunities+"p"+((int) (1000*probability));
    }

    /**
     * Sets name of graph
     */
    public void setName(){
        tg.setNameRoot(shortName());
    }
}
