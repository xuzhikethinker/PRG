/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;

/**
 * AdjacencyMatrixSparse
 * <p>AdjacencyMatrix now uses a sparse matrix representationso this
 * class may be redundant?
 * @deprecated use {@link AdjacencyMatrix} class
 * @author time
 */
public class AdjacencyMatrixSparse extends AdjacencyMatrix {

    Algebra alg;

            
    
   /** 
     * Creates a AdjacencyMatrix from list of edges.
     *@param tg graph to use to set up the adjacency matrix 
    */
    public AdjacencyMatrixSparse(timgraph tg) {
        alg = new Algebra();
        make(tg);
    }

   /** 
     * Creates a AdjacencyMatrix from adjacency matrix.
     *@param m matrix
    * @param def selects definition of matrix to use. 0= M (simple), 1= <tt>M^2-M</tt>
    */
    public AdjacencyMatrixSparse(double [][] m, int def) {
        alg = new Algebra();
        if (def==1) this.makeA2mA(m);
        else make(m);
    }

   /** 
     * Creates a AdjacencyMatrix from adjacency matrix.
     *@param tg graph to use to set up the adjacency matrix 
    * @param def selects definition of matrix to use. 0= M (simple), 1= <tt>M^2-M</tt>
    */
    public AdjacencyMatrixSparse(timgraph tg, int def) {
        alg = new Algebra();
        if (def==1) {
            AdjacencyMatrixSparse am = new AdjacencyMatrixSparse(tg);
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
    public AdjacencyMatrixSparse(int totalNumberVertices, int totalNumberStubs, int [] edgeSourceList, boolean directedGraph ) {
        alg = new Algebra();
        dimension=totalNumberVertices;
        initialiseMatrix();
        makeUnweighted(totalNumberVertices, totalNumberStubs, edgeSourceList, directedGraph );
    }

    /** 
     * Inialises adjacency matrix to internal dimension.
     */
    private void initialiseMatrix() {
        matrix = new SparseDoubleMatrix2D(dimension,dimension);
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
    
    
    
    // -----------------------------------------------------------------------       
    /** 
     * Initialises adjacency matrix using a matrix.
     * @param m matrix used to initialise.
     */
    @Override
    public void make(double [][] m) {
        dimension = m[0].length;
        matrix = new SparseDoubleMatrix2D(m);
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
        matrix = new SparseDoubleMatrix2D(m);
        totalWeight = 0;
        for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) {
            double value= - m[i][j];
            for (int k=0; k<dimension; k++) value += m[i][k]*m[k][j];
            totalWeight+=value;
            matrix.setQuick(i,j,value);
        }
    }
    
    /**
     * Initialise quality using simplest (Newman) definition.
     * @param graph timgraph defining the graph.
     */
    @Override
    public void make(timgraph graph) {
        make(graph, transferMatrix) ;
    }

    /**
     * Initialise quality using simplest (Newman) definition.
     * @param graph timgraph defining the graph.
     */
    public void make(timgraph graph, boolean makeTransferMatrix) {
        if (!graph.isVertexEdgeListOn()) System.err.println("*** in AdjacencyMatrix make, graph "+graph.inputName.getNameRoot()+" needs vertexEdgeList");
        transferMatrix = makeTransferMatrix;
        boolean undirected = (!graph.isDirected());
        boolean weighted = graph.isWeighted();
        dimension = graph.getNumberVertices();
        int numberEdges = graph.getNumberStubs();
        double sstrout=-1;
        int s=-1;
        double w = -1;
        initialiseMatrix();
        for (int e = 0; e < numberEdges; e++) {
            s = graph.getVertexFromStub(e++); // source vertex
            if (transferMatrix) {
                sstrout = graph.getVertexOutStrength(s);
                if (sstrout<1e-20) throw new RuntimeException("Out strength is too small for vertex "+s+" of value "+sstrout);
            } // source vertex
            if (weighted) {
                w = graph.getEdgeWeight(e);
            } else {
                w = 1;
            }
            if (transferMatrix) w=w/sstrout;
            int t = graph.getVertexFromStub(e); // e now points to target vertex of edge in edgeSourceList
            increaseEdgeWeight(s,t,w,undirected);
        }
    }

     /**
     * Increases the edge weight in adjacency matrix.
     * <br>Note that it is assumed that each edge appears only once.
     * However for undirected edges this will contribute twice to the adjacency
     * matrix as weight needs to be added to both A_{s,t} and A_{t,s}.
     * This includes the case of self-loops.
     * <br>Total weight is updated.
     * @param s source vertex of edge
     * @param t target vertex of edge
     * @param w increase in weight
     * @param undirected true (false) if graph is undirected (directed)
     */
   @Override
  protected void increaseEdgeWeight(int s, int t, double w, boolean undirected){
            double nw=w+matrix.getQuick(s, t);
            matrix.setQuick(s,t,nw);
            totalWeight += w;
            // Note undirected edges including self-loops counted twice correctly
            //as they have both stubs on same vertex.
            if (undirected) {
                nw=w+matrix.getQuick(t, s);
                matrix.setQuick(t,s,nw);
                totalWeight += w;
            }
   }
   
    /** 
     * Sets up the adjacency matrix by using an internal tim graph.
     *@param args string array of command line arguments for timgraph. 
     */
    @Override
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

    
// ----------------------------------------------------------------------
     

///**
// * Calculates the Eigenvalue decomposition using CERN colt routines.
// *<br> Sets up Vmatrix pointer but not V inverse.
// *Also sets the tolerance parameter.
// *<br>If A(=transfer matrix) is symmetric, then A = V*D*V' where the eigenvalue matrix D is diagonal
// *and the eigenvector matrix V is orthogonal. I.e. A = V.mult(D.mult(transpose(V)))
// *and V.mult(transpose(V)) equals the identity matrix.
// *<br>
// *If A is not symmetric, then the eigenvalue matrix D is block diagonal with the
// *real eigenvalues in 1-by-1 blocks and any complex eigenvalues,
// *lambda + i*mu, in 2-by-2 blocks, [lambda, mu; -mu, lambda].
// *The columns of V represent the eigenvectors in the sense that A*V = V*D,
// *i.e. A.mult(V) equals V.mult(D). The matrix V may be badly conditioned,
// *or even singular, so the validity of the equation A = V*D*inverse(V)
// *depends upon Algebra.cond(V).
// *@param vector initial vector
// *@param tol tolerance for imaginary part of eigenvalues
// *@return 0 if OK
// */
//private int calcLargestEigenvector(double tol, DoubleMatrix1D vector) {
//
//    DoubleMatrix1D vector = new DoubleMatrix1D(dimension);
//    boolean going=true;
//    while (going){
//        DoubleMatrix1D newVector = alg.mult(adjacencyMatrixSparse,)
//    }
//
//
//
//    return 0;
//}
    
    
}
