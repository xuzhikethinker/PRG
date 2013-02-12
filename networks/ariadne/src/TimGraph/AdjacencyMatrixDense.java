/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 *
 * @author time
 */
public class AdjacencyMatrixDense extends AdjacencyMatrix {
        
    
   /** 
     * Creates a AdjacencyMatrix from list of edges.
     *@param tg graph to use to set up the adjacency matrix 
    */
    public AdjacencyMatrixDense(timgraph tg) {
        make(tg);
    }

   /** 
     * Creates a AdjacencyMatrix from adjacency matrix.
     *@param m matrix
    * @param def selects definition of matrix to use. 0= M (simple), 1= <tt>M^2-M</tt>
    */
    public AdjacencyMatrixDense(double [][] m, int def) {
        if (def==1) this.makeA2mA(m);
        else make(m);
    }

   /** 
     * Creates a AdjacencyMatrix from adjacency matrix.
     *@param tg graph to use to set up the adjacency matrix 
    * @param def selects definition of matrix to use. 0= M (simple), 1= <tt>M^2-M</tt>
    */
    public AdjacencyMatrixDense(timgraph tg, int def) {
        if (def==1) {
            AdjacencyMatrix am = new AdjacencyMatrix(tg);
            this.makeA2mA(am.matrix());
        }
        else make(tg);
    }

   /** 
     * Creates a AdjacencyMatrix from list of edges.
     *@param totalNumberVertices total number of vertices (numbered from 0)
     *@param totalNumberStubs total number of stubs = number of edges *2
     *@param edgeSourceList array with element (2e) the source vertex and and element (2e+1) the target vertex  
     *@param directedGraph true if edges are directed
     */
    public AdjacencyMatrixDense(int totalNumberVertices, int totalNumberStubs, int [] edgeSourceList, boolean directedGraph ) {
        dimension=totalNumberVertices;
        initialiseMatrix();
        makeUnweighted(totalNumberVertices, totalNumberStubs, edgeSourceList, directedGraph );
    }

    /** 
     * Inialises adjacency matrix to internal dimension.
     */
    private void initialiseMatrix() {
        matrix = new DenseDoubleMatrix2D(dimension,dimension);
        //for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) matrix[i][j]=0;
        totalWeight = 0;
    }
    
    
 
    // -----------------------------------------------------------------------     
//    /**
//     * get adjacency matrix entry.
//     * @param source source vertex index from (0 .. dimension-1)
//     * @param target target vertex index from (0 .. dimension-1)
//     * @return adjacency matrix entry for given source and target vertices
//     */
//    @Override
//    public double get(int source, int target){
//        return matrix.getQuick(source,target);
//    }
//
//    /**
//     * get reference to the adjacency matrix.
//     * @return adjacency matrix
//     */
//    @Override
//    public double [][] matrix(){
//        return matrix.toArray();
//    }
    
//    /**
//     * get in vertex strength.
//     * @param vertex index of vertex
//     * @return in vertex strength
//     */
//    public double getInStrength(int vertex){return inVector[vertex];}
//       
//    /**
//     * get out vertex strength.
//     * @param vertex index of vertex
//     * @return out vertex strength
//     */
//    public double getOutStrength(int vertex){return outVector[vertex];}

//    /**
//     * Total weight i.e. sum of all adjancey matrix entries
//     * @return taotal weight
//     */
//    public double totalWeight(){
//        return this.totalWeight;
//    }
//    
//    /**
//     * Dimension of mastrix i.e. the number of vertices.
//     */
//    public int dimension(){return dimension;}
    
    
    // -----------------------------------------------------------------------       
    /** 
     * Initialises adjacency matrix using a matrix.
     * @param m matrix used to initialise.
     */
    @Override
    public void make(double [][] m) {
        dimension = m[0].length;
        matrix = new DenseDoubleMatrix2D(m);
        totalWeight = 0;
    }
    
    // -----------------------------------------------------------------------       
    /** 
     * Initialises adjacency matrix to be <tt>M^2-M</tt> where M is given matrix.
     * @param m matrix used to initialise.
     */
    @Override
    public void makeA2mA(double [][] m) {
        dimension = m[0].length;
        matrix = new DenseDoubleMatrix2D(m);
        totalWeight = 0;
        for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) {
            double value= - m[i][j];
            for (int k=0; k<dimension; k++) value += m[i][k]*m[k][j];
            totalWeight+=value;
            matrix.setQuick(i,j,value);
        }
    }
    
//    /**
//     * Initialise quality using simplest (Newman) definition.
//     * @param graph timgraph defining the graph.
//     */
//    @Override
//    public void make(timgraph graph) {
//        if (!graph.isVertexEdgeListOn()) System.err.println("*** in AdjacencyMatrix make, graph "+graph.inputName.getNameRoot()+" needs vertexEdgeList");
//
//        dimension = graph.getNumberVertices();
//        int numberEdges = graph.getNumberStubs();
//        double w = -1;
//        double nw =-1;
//        initialiseMatrix();
//        for (int e = 0; e < numberEdges; e++) {
//            int s = graph.getVertexFromStub(e++); // source vertex
//            if (graph.isWeighted()) {
//                w = graph.getEdgeWeight(e);
//            } else {
//                w = 1;
//            }
//            int t = graph.getVertexFromStub(e); // e now points to target vertex of edge in edgeSourceList
//            nw=w+matrix.getQuick(s, t);
//            matrix.setQuick(s,t,nw);
//            totalWeight += w;
//            // Is the directed test OK?
//            // Note self-loops counted twice correctly as they have both stubs on same vertex.
//            if (!graph.isDirected()) {
//                //if (graph.weightedEdges) w=graph.getEdgeWeight(e); else w=1;
//                matrix.setQuick(t,s,nw);
//                totalWeight += w;
//            }
//        }
//    }
    
    
      /** 
     * Sets up the adjacency matrix by using an internal tim graph.
     *@param args string array of command line arguments for timgraph. 
     */
    public void make(String [] args)
    {
    timgraph tg; 
    String fileName = "AMdefault";
    String directoryName = "";
    int infolevel = -2;
    int outputcontrol= 31;  // set to zero to get no output files
    tg = new timgraph(fileName+"tg", directoryName, infolevel, outputcontrol);
        tg.setNumberEvents(dimension); // do not change!
        tg.parseParam(args);
        if (tg.getInitialVertices() >dimension) tg.setInitialVertices(dimension);
        tg.doOneRun(1); // This will inialise graph and only if need more vertices will it do that
        makeUnweighted(tg.getNumberVertices(), tg.getNumberStubs(), tg.getStubSourceList(), tg.directedGraph);
        
    } // eo makeMatrix
    
//    /**
//     * Calculates the vectors of incoming and outgoing vertex strengths.
//     */
//    public void calculateInOutVectors(){
//        inVector = new double[dimension];
//        outVector = new double[dimension];
//        for (int i = 0; i < dimension; i++) {
//            outVector[i] = 0;
//            inVector[i]=0;
//            for (int j = 0; j < dimension; j++) {
//                outVector[i]+=matrix[i][j];
//                 inVector[i]+=matrix[j][i];
//            }
//        }
//        
//    }
//   
    /** 
     * Sets up the adjacency matrix ignoring edges weights.
      * <br>This is given as <code>matrix[source][target]</code>
     *@param totalNumberVertices total number of vertices (numbered from 0)
     *@param totalNumberStubs total number of stubs = number of edges *2
     *@param edgeSourceList array with element (2e) the source vertex and and element (2e+1) the target vertex  
     *@param directedGraph true if edges are directed
     */
    private void makeUnweighted(int totalNumberVertices, int totalNumberStubs, int [] edgeSourceList, boolean directedGraph ) {
        int source = -1;
        int target = -1;
        double nw=-1;
        for (int e=0; e<totalNumberStubs; e++)
        {
            source = edgeSourceList[e++];
            target = edgeSourceList[e];
            nw=matrix.get(source,target)+1;
            matrix.set(source,target,nw);
            if (!directedGraph) matrix.set(target,source,nw);
        }        
    } // eo makeMatrix

    
    
    
}
