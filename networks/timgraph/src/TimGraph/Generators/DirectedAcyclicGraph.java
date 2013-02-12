/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Generators;

import TimGraph.io.FileOutput;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.StringFilter;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author time
 */
public class DirectedAcyclicGraph extends SimpleERCommunityGraph {

    final static String nameDAG="Growing Directed Acyclic Graph";
    final static String shortNameDAG="GDAG";

    int deltaV=1;
    boolean bestFixedDegree=true;
    boolean longestSpine=true;
    Random rnd;

     public DirectedAcyclicGraph(){}

   /**
     * Creates a growing topologically ordered directed acyclic graph using local rules.
     * <p>Edges always from vertex v to a vertex with index greater than v
     * so topologically ordered.  Will add edges to vertices between <tt>(v+1)</tt>
     * and <tt>(v+dv)</tt> ahead chosen with uniform probability.  The value for
     * <tt>dv</tt> is set to be input <tt>deltaV</tt> unless <tt>bestFixedVertex</tt>
     * is true in which case boundary will always be lesser of <tt>(v+dv)</tt>
     * and largest vertex. To get a preferential attachment type of graph use
     * <tt>deltaV=</tt><em>number of vertices</em>.  In this case if
     * <tt>bestFixedVertex</tt> is true then will get close to requested average
     * degree otherwise will get about half of this.
     * No multiple edges.
     * @param numberVertices number of vertices
     * @param averageOutDegree average out degree to aim for (upper bound)
     * @param deltaV largest number of vertices ahead may connect to
     * @param bestFixedDegree if true try to get fixed degree
     */
     public DirectedAcyclicGraph(Random rndnew,
             int numberVertices, double averageOutDegree, int deltaV,
             boolean bestFixedDegree, boolean longestSpine, String [] args){
            rnd=rndnew;
            graphName=nameDAG;
            shortGraphName=shortNameDAG;
            this.deltaV=deltaV;
            this.bestFixedDegree=bestFixedDegree;
            this.longestSpine=longestSpine;
//            String [] args = { "-gvet", "-gdt", "-gewf", "-gvlf", "-gbf", "-gn99",  "-e0", "-o23", "-xi0"};
            tg = new timgraph(args);
//            tg.setVertexEdgeList(true); // needed to prevent multiple edges
//            nCommunities=numberCommunities;
            nVertices = numberVertices;
            avDegree = averageOutDegree;
            nStubsMax = (int) Math.round(nVertices*avDegree*2);
            System.out.println("nVertices, nStubs "+nVertices+", "+nStubsMax);
            tg.setNetworkWithVertices(nVertices, nStubsMax);
     }

         /**
     * Makes a random community graph.
     * <p>If first argument starts with a ':'
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>)
     * the rest of that argument is used by this routine. These are as follows
          * <ul>
          * <li><tt>:numberVertices</tt> (int)</li>
          * <li><tt>:averageOutDegree</tt> (double)</li>
          * <li><tt>:deltaV</tt> will add edges top vertices which are <tt>deltaV</tt> ahead of source</li>
          * <li><tt>:bestFixedDegree</tt>(boolean) t, T, y or Y for true will try to get as close as possible to degree</li>
          * <li><tt>:longestSpine</tt> (boolean) t, T, y or Y for true first edge always to next vertex</li>
          * </ul>
     * Remaining arguments start with a '-' and are parsed by timgraph.
     * @param args list of arguments
     */
    public static void main(String[] args) {


     int ano=0;
     int numberVertices=100;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) numberVertices=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     double averageOutDegree=3.01;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) averageOutDegree=Double.parseDouble(args[ano].substring(1, args[ano].length()));

     ano++;
     int deltaV=numberVertices/10;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) deltaV=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     ano++;
     boolean bestFixedDegree=true;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) bestFixedDegree=StringFilter.trueString(args[ano].charAt(1));

     ano++;
     boolean longestSpine=true;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) longestSpine=StringFilter.trueString(args[ano].charAt(1));

     String [] arguments= { "-gvet", "-gdt", "-gewf", "-gvlf", "-gbf", "-gn99",  "-e0", "-o8", "-xi0"};
     if (args.length>0) arguments=args;

     Random rndnew = new Random();
     DirectedAcyclicGraph dag = new DirectedAcyclicGraph(rndnew, numberVertices, averageOutDegree, deltaV, bestFixedDegree, longestSpine, arguments);

     System.out.println("--- Creating "+dag.desciption(""));

     dag.initialise();
     dag.setName();
     dag.createFixedDegreeDAG();
     dag.labelVertices();
     dag.information();
    }


    /**
     * Creates topologically ordered directed acyclic graph with almost constant degree.
     * <p>Edges always from vertex v to a vertex with index greater than v
     * so topologically ordered.  Will add edges to vertices between <tt>(v+1)</tt>
     * and <tt>(v+dv)</tt> ahead chosen with uniform probability.  The value for
     * <tt>dv</tt> is set to be input <tt>deltaV</tt> unless <tt>bestFixedVertex</tt>
     * is true in which case boundary will always be lesser of <tt>(v+dv)</tt>
     * and largest vertex. To get a preferential attachment type of graph use
     * <tt>deltaV=</tt><em>number of vertices</em>.  In this case if
     * <tt>bestFixedVertex</tt> is true then will get close to requested average
     * degree otherwise will get about half of this.
     * <p>Note the fixed degree aimed for is <tt>Math.floor(avDegree)</tt>.
     * No multiple edges.
     */
    static public void createFixedDegreeDAG(Random rnd, timgraph dag,
            int nVertices, double avDegree, int dv,
            boolean bestFixedDegree, boolean longestSpine){

        int nn=-1;
        int avk = (int) Math.floor(avDegree);
        TreeSet<Integer> nnSet;
        for (int v=0; v<nVertices-1; v++){
            if (bestFixedDegree) dv=Math.min(nVertices-v-1,dv);
            nnSet = new TreeSet<Integer>();
            for (int k=0; k<avk; k++){
                nn=v+ (longestSpine && k==0 ? 0 :rnd.nextInt(dv))+1;
                if (nn<nVertices) nnSet.add(nn);
            }
            if (nnSet.size()>0) for (Integer nnn:nnSet) try{
                dag.addEdge(v, nnn);
            } catch(Exception e){
                System.err.println("ERROR "+e+".  Adding edge with "+v+nnn);
            }
        }
    }

    /**
     * Creates topologically ordered directed acyclic graph with almost constant degree.
     * <p>Edges always from vertex v to a vertex with index greater than v
     * so topologically ordered.  Will add edges to vertices between <tt>(v+1)</tt>
     * and <tt>(v+dv)</tt> ahead chosen with uniform probability.  The value for
     * <tt>dv</tt> is set to be input <tt>deltaV</tt> unless <tt>bestFixedVertex</tt>
     * is true in which case boundary will always be lesser of <tt>(v+dv)</tt>
     * and largest vertex. To get a preferential attachment type of graph use
     * <tt>deltaV=</tt><em>number of vertices</em>.  In this case if
     * <tt>bestFixedVertex</tt> is true then will get close to requested average
     * degree otherwise will get about half of this.
     * <p>Note the fixed degree aimed for is <tt>Math.floor(avDegree)</tt>.
     * No multiple edges.
     */
    public void createFixedDegreeDAG(){
        rnd= new Random();
        createFixedDegreeDAG(rnd, tg, nVertices, avDegree, deltaV, bestFixedDegree, longestSpine);
    }


        /**
     * Provides string with long description of basic parameters.
     * @param sep separation string e.g. line feed
     * @return string with long description
     */
    @Override
    public String basicDescription(String sep){
        return graphName
                +sep+this.nVertices+" vertices in total, "
                +sep+this.avDegree+" average degree, "
                +sep+"connecting to "+this.deltaV+" vertices ahead,"
                +sep+(this.bestFixedDegree?" best":" rough")+" approximation to fixed degree, "
                +sep+"each vertex "+(this.longestSpine?" ":" not")+" always connected to next vertex.";
    }
    /**
     * Provides string with long description of input parameters.
     * @param sep separation string e.g. line feed
     * @return string with long description
     */
    @Override
    public String desciption(String sep){
        return basicDescription(sep);
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
        return shortGraphName+"_V"+nVertices+mstring+"dV"+this.deltaV+"F"+(bestFixedDegree?"b":"r")+"S"+(longestSpine?"y":"n");
    }

    /**
     * Sets name of graph
     */
    @Override
    public void setName(){
        tg.setNameRoot(shortName());
    }



}
