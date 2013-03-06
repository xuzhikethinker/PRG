/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.algorithms;

import TimGraph.timgraph;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Performs Burt's structural hole analysis
 * @author time
 */
public class StructuralHoleAnalysis {

    timgraph tg;
    ArrayList<StructuralHoleVertexData> shData;

    public  StructuralHoleAnalysis(timgraph tginput){
        tg=tginput;
        tg.calcStrengthDirectly(); // this ensures max weights calculated.
        shData = new ArrayList(tg.getNumberVertices());
    }

    public static void main(String[] args) {
     int ano=0;
     int numberVertices=100;
     if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) numberVertices=Integer.parseInt(args[ano].substring(1, args[ano].length()));

     String fileName="vaneshtestinput.net";
     String ssname="vanesh";
     //String fileName="burtinputELS.dat";
     //String fileName="pietroinputELS.dat";
     //String ssname="burt";
     String sidirName="input/"+ssname;
     String sodirName="output/"+ssname;
     String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gelf ", "-gbf", "-fin"+fileName, "-fis"+sidirName, "-fos"+sodirName, "-gn99",  "-e0", "-o23", "-xi0", "-xv20", "-xe40"};
     if (args.length>0) aList=args;

     timgraph tg = new timgraph();
     tg.parseParam(aList);

     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFile(); // this will read in the network
     tg.printParametersBasic();

     StructuralHoleAnalysis sha = new StructuralHoleAnalysis(tg);

     sha.printStructuralHoleMeasuresAll(System.out);

    }

    /**
     * Finds Structural Hole measures for all edges and vertices.
     * <br>Does not check for multiple edges.
     * <br>Assumes undirected graphs.
     */
    public void  calcStructuralHoleMeasuresAll(){
        for (int s=0; s<tg.getNumberVertices(); s++) shData.add(calcStructuralHoleMeasuresVertex(s,true));
    }
    /**
     * Finds Structural Hole measures for all vertices.
     * <br>No individual edge constraint values found.
     * <br>Does not check for multiple edges.
     * <br>Assumes undirected graphs.
     */
    public void  calcStructuralHoleMeasuresVertices(){
        for (int s=0; s<tg.getNumberVertices(); s++) shData.add(calcStructuralHoleMeasuresVertex(s,false));
    }
    /**
     * Finds all structural hole measures for all edges and vertices.
     * <br>Does not check for multiple edges.
     * <br>Assumes undirected graphs.
     * @param s index of input vertex whose structural hole measures are required.
     */
    public void  printStructuralHoleMeasuresAll(PrintStream PS){
        for (int s=0; s<tg.getNumberVertices(); s++) {
            StructuralHoleVertexData shd = calcStructuralHoleMeasuresVertex(s,true);
            PS.print("----- ");
            shd.printVertexData(PS," : ",2);
            shd.printEdgeData(PS," | ",2);
        }
    }
    /**
     * Finds Structural Hole measures for all vertices.
     * <br>No constraint measures for edges given.
     * <br>Does not check for multiple edges.
     * <br>Assumes undirected graphs.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     */
    public void  printStructuralHoleMeasuresVertices(PrintStream PS, String sep){
        StructuralHoleVertexData.printVertexDataLabel(PS,sep);
        for (int s=0; s<tg.getNumberVertices(); s++) {
            StructuralHoleVertexData shd = calcStructuralHoleMeasuresVertex(s,false);
            shd.printVertexData(PS," : ",2);
        }
    }
    /**
     * Finds Structural Hole measures for single vertex.
     * <br>Does not check for multiple edges.
     * <br>Assumes undirected graphs.
     * @param s index of input vertex whose structural hole measures are required.
     * @param edgeDataOn if true then stores the constraint data for each edge
     * @return StructuralHoleData object with info inside, null if there is a problem.
     *
     */
    public StructuralHoleVertexData calcStructuralHoleMeasuresVertex(int s, boolean edgeDataOn){
        return calcStructuralHoleMeasuresVertex(tg, s, edgeDataOn);
    }
    /**
     * Finds Structural Hole measures for single vertex.
     * <br>Does not check for multiple edges.
     * <br>Assumes undirected graphs.
     * @param graph timgraph to be analysed
     * @param s index of input vertex whose structural hole measures are required.
     * @param edgeDataOn if true then stores the constraint data for each edge
     * @return StructuralHoleData object with info inside, null if there is a problem.
     *
     */
    static public StructuralHoleVertexData calcStructuralHoleMeasuresVertex(timgraph graph, int s, boolean edgeDataOn){
        if (!graph.isVertexInGraph(s)) throw new RuntimeException("vertex "+s+" is not between 0 and "+graph.getNumberVertices());
        StructuralHoleVertexData shdata = new StructuralHoleVertexData(s);
        int ks=graph.getVertexDegree(s);
        if (ks<1) return shdata;
        double ss=graph.getVertexOutStrength(s);// strength s
        final double MinStrength=1e-9;
        if(ss<MinStrength) return shdata;
        double sq=-1;
        double wst=-1;
        double wsq=-1;
        double wqt=-1;
        double pst=-1;
        double psq=-1;
        double pqt=-1;
        double maxwq=-1;
        double mqt=-1;
        double psqmqt=-1;
        double psqpqt=-1;
        double pst2=-1;
        double constraintst=-1;
        double effectiveSize=0;
        double constraints=0;
        int stubst =-1;
        int stubsq =-1;
        int stubqt =-1;
        int t =-1; // j in diagrams
        int q =-1;
        for (int est=0; est<ks; est++){ // local edge index from s to t
         stubst = graph.getStub(s,est);
         t=graph.getOtherVertexFromStub(stubst);
         if (s==t) continue; // only if have multiple edges can this occur
         wst = graph.getEdgeWeight(stubst);// weight of s to t edge
         pst=wst/ss;
         pst2=0;
         psqmqt=0;
         psqpqt=0;
         for (int esq=0; esq<ks; esq++){// local edge index from s to q
           if (est==esq) continue;
           stubsq = graph.getStub(s,esq);
           q=graph.getOtherVertexFromStub(stubsq);
           if ((s==q) || (q==t) ) continue;
           wsq = graph.getEdgeWeight(stubsq);// weight of s to q edge
           psq=wsq/ss; // prob moving from s to q edge
           stubqt=graph.getFirstEdgeGlobal(t,q);
           if (stubqt<0) continue; // q and t not connected
           wqt = graph.getEdgeWeight(stubqt);// weight of q to t edge
           sq=graph.getVertexOutStrength(q); // strength q
           if(sq<MinStrength) continue;
           pqt=wqt/sq; // prob moving from  q to t
           maxwq = graph.getVertexMaxWeight(q); // max weight from q
           mqt=wqt/maxwq; // normalised q to t edge wight w.r.t. max q weight
           pst2 += psq*pqt;
           psqmqt += psq*mqt;
           psqpqt += psq*pqt;
           }// eo loop to find all q, the second n.n.
         effectiveSize +=1-psqmqt;
         constraintst = (pst+psqpqt) * (pst+psqpqt);
         constraints+=constraintst;
         if (edgeDataOn) shdata.addnn(t,constraintst);
        }// eo loop to find t, the first nn.
        shdata.addglobal(constraints,effectiveSize,effectiveSize/ks);
        return shdata;
    }

}
