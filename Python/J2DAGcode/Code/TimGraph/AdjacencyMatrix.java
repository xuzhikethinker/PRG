/*
 * AdjacencyMatrix.java
 *
 * Created on 24 January 2007, 11:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimGraph;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import java.io.PrintStream;

/**
 * AdjacencyMatrix
 * <p>Now uses a sparse matrix representation.
 * <p>This should work for all types of graph including the calculation
 * of the pi vector, the page rank vector.
 * @author time
 */
public class AdjacencyMatrix {
    //String SEP = "\t"; // tab used as a separator in output
    protected int dimension=-1;
    //double averageDegree =-1;
    /**
     * Adjacency matrix as <code>matrix[source][target]</code>
     */
    //private double [][] matrix;
    protected DoubleMatrix2D  matrix;
    /**
     * Vector of incoming vertex strengths.
     * <code>inVector[t] = sum_s matrix[s][t]</code> where s (t) is the source (target) vertex
     */
    protected double[] inVector;
    /**
     * Vector of outgoing vertex strengths.
     * <code>outVector[s] = sum_t matrix[s][t]</code> where s (t) is the source (target) vertex
     */
    protected double[] outVector;
        
    /**
     * If defined then contains an approximation to the dominant eigenvector.
     * <p>More precisely this should be the limiting vector of A^n k_in
     * and this should be normalised so the sum of entries is one.
     */
    protected DoubleMatrix1D piVector    ;
    /**
     * Sum of all matrix entries.
     */
    protected double totalWeight = 0;

    /**
     * Transfer Matrix
     * <p>If true then weight outgoing edges with vertex strength
     */
    boolean transferMatrix=false;

    static final double DUNSET = -97531.0;
    static final int IUNSET = -86420;


   /** 
     * Empty constructor
    */
    public AdjacencyMatrix() {
        
    }

    /** 
     * Creates a AdjacencyMatrix from list of edges.
     *@param tg graph to use to set up the adjacency matrix 
    */
    public AdjacencyMatrix(timgraph tg) {
        make(tg);
    }

   /** 
     * Creates a AdjacencyMatrix from adjacency matrix.
     *@param m matrix
    * @param def selects definition of matrix to use. 0= M (simple), 1= <tt>M^2-M</tt>
    */
    public AdjacencyMatrix(double [][] m, int def) {
        if (def==1) this.makeA2mA(m);
        else make(m);
    }

   /** 
     * Creates a AdjacencyMatrix from adjacency matrix.
     *@param tg graph to use to set up the adjacency matrix 
    * @param def selects definition of matrix to use. 0= M (simple), 1= <tt>M^2-M</tt>
    */
    public AdjacencyMatrix(timgraph tg, int def) {
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
    public AdjacencyMatrix(int totalNumberVertices, int totalNumberStubs, int [] edgeSourceList, boolean directedGraph ) {
        dimension=totalNumberVertices;
        initialiseMatrix();
        makeUnweighted(totalNumberVertices, totalNumberStubs, edgeSourceList, directedGraph );
    }

    /** 
     * Initialises adjacency matrix to internal dimension.
     */
    private void initialiseMatrix() {
        matrix = new SparseDoubleMatrix2D(dimension,dimension);
        //for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) matrix[i][j]=0;
        totalWeight = 0;
    }
    
    
 
    // -----------------------------------------------------------------------     
    /**
     * get adjacency matrix entry.
     * @param source source vertex index from (0 .. dimension-1)
     * @param target target vertex index from (0 .. dimension-1)
     * @return adjacency matrix entry for given source and target vertices
     */
    public double get(int source, int target){
        return matrix.get(source,target);
    }
    
    /**
     * Get reference to the adjacency matrix as a simple matrix.
     * @return adjacency matrix
     */
    public double [][] matrix(){
        return matrix.toArray();
    }

    /**
     * Get reference to the adjacency matrix as a CERN colt DoubleMatrix2D.
     * @return adjacency matrix
     */
    public DoubleMatrix2D doubleMatrix2D(){
        return matrix;
    }

    /**
     * get in vertex strength.
     * @param vertex index of vertex
     * @return in vertex strength
     */
    public double getInStrength(int vertex){return inVector[vertex];}
       
    /**
     * get out vertex strength.
     * @param vertex index of vertex
     * @return out vertex strength
     */
    public double getOutStrength(int vertex){return outVector[vertex];}

    /**
     * get out vertex strength.
     * @param vertex index of vertex
     * @return out vertex strength
     */
    public double getNormalisedPi(int vertex){return piVector.get(vertex);}

    /**
     * Total weight i.e. sum of all adjacency matrix entries
     * @return total weight
     */
    public double totalWeight(){
        return this.totalWeight;
    }
    
    /**
     * Dimension of matrix i.e. the number of vertices.
     */
    public int dimension(){return dimension;}
    
    
    // -----------------------------------------------------------------------       
    /** 
     * Initialises adjacency matrix using a matrix.
     * @param m matrix used to initialise.
     */
    public void make(double [][] m) {
        dimension = m[0].length;
        initialiseMatrix();
        for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) {
            double value=m[i][j];
            totalWeight+=value;
            matrix.set(i,j,value);
        }
    }
    
    // -----------------------------------------------------------------------       
    /** 
     * Initialises adjacency matrix to be <tt>M^2-M</tt> where M is given matrix.
     * @param m matrix used to initialise.
     */
    public void makeA2mA(double [][] m) {
        dimension = m[0].length;
        initialiseMatrix();
        for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) {
            double value= - m[i][j];
            for (int k=0; k<dimension; k++) value += m[i][k]*m[k][j];
            totalWeight+=value;
            matrix.set(i,j,value);
        }
    }
    
    /**
     * Initialise quality using simplest (Newman) definition.
     * <p>Sets up in and out vectors
     * @param graph timgraph defining the graph. 
     */
    public void make(timgraph graph) {
        if (!graph.isVertexEdgeListOn()) {
            graph.createVertexGlobalEdgeList();
            System.err.println("!!! WARNING in AdjacencyMatrix make, graph "+graph.inputName.getNameRoot()+" needs vertexEdgeList, creating an unsynchronised one");
        }

        dimension=graph.getNumberVertices();
        inVector = new double[dimension];
        outVector = new double[dimension];

        boolean undirected = (!graph.isDirected());
        boolean weighted = graph.isWeighted();
        dimension = graph.getNumberVertices();
        int numberEdges = graph.getNumberStubs();
        double w = -1;
        initialiseMatrix();
        for (int e = 0; e < numberEdges; e++) {
            int s = graph.getVertexFromStub(e++); // source vertex
            if (weighted) {
                w = graph.getEdgeWeight(e);
            } else {
                w = 1;
            }
            int t = graph.getVertexFromStub(e); // e now points to target vertex of edge in edgeSourceList
            increaseEdgeWeight(s,t,w,undirected);
            inVector[t]+=w;
            outVector[s]+=w;
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
     * @param dw increase in weight
     * @param undirected true (false) if graph is undirected (directed)
     */
    protected void increaseEdgeWeight(int s, int t, double dw, boolean undirected){
           double newValue=dw+matrix.get(s, t);
           matrix.set(s,t,newValue); //matrix[s][t] += dw;
           totalWeight += dw;
            // Is the directed test OK?
            // Note self-loops counted twice correctly as they have both stubs on same vertex.
            if (undirected) {
                matrix.set(t,s,newValue); //matrix[t][s] += dw;
                totalWeight += dw;
            }    
    }
    
    /** 
     * Sets up the adjacency matrix by using an internal tim graph.
     * @param args string array of command line arguments for timgraph.
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
    
    /**
     * Calculates the vectors of incoming and outgoing vertex strengths.
     * <p>Not needed if using make(timgraph).
     */
    public void calculateInOutVectors(){
        inVector = new double[dimension];
        outVector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            outVector[i] = 0;
            inVector[i]=0;
            for (int j = 0; j < dimension; j++) {
                 inVector[i]+=get(j,i); //???
                 outVector[i]+=get(i,j); //??? matrix[j][i];
            }
        }
        
    }

     /**
      * Calculates the vector used in pageRank.
      * <p>This can be used for the null model in
      * directed graph modularity.  It finds it by iterating the transition
      * matrix on the in Strength vector so convergence may be slow.
      * <p>Uses default setting for parameters.
      * @deprecated Replaced by {@link #calculatePiVector() }
     */
    public void calculatePageRankVector(){
     int maxIter=100;
     double relError =1e-3;
     double alpha=1.0;
     calculatePiVector(maxIter, relError, alpha);
    }
     /**
      * Calculates the Pi vector.
      * <p>This can be used for the null model in
      * directed graph modularity.  It finds it by iterating the transition
      * matrix on the in Strength vector so convergence may be slow.
      * <p>Uses default setting for parameters.
     */
    public void calculatePiVector(){
     int maxIter=10;
     double relError =1e-2;
     double alpha=1.0;
     calculatePiVector(maxIter, relError, alpha);
    }
     /**
      * Calculates the vector Pi vector.
      * <p>This can be used for the null model in
      * directed graph modularity.  It finds it by iterating the transition
      * matrix on the in Strength vector so convergence may be slow.
      * <p>The relative error used to stop the routine is
      * the total absolute difference between old and new page rank vectors
      * as fraction of current sum of terms.
      * <p><code> pi' = alpha * T * pi + (1-alpha)*k_in/W</T> where
      * <tt>T</tt> is the transfer matrix, <tt>(k_in/W)</tt> is the
      * normalised in-degree vector, and <tt>pi</tt> (<tt>pi'</tt>)
      * are the old (new) estimates for the Pi (PageRank) vector.
      * <p> See also {@link #calcPiVector(double, int) }.
      * @param maxIter maximum number of iterations to use
      * @param relError relative error in length of new to previous page rank vectors
      * @param alpha weight given to transition matrix term rather than fixed invector
     */
    public void calculatePiVector(int maxIter, double relError, double alpha){
       calculateInOutVectors();
       // TODO should use transition matrix class and add this method to that class
       piVector  = new   DenseDoubleMatrix1D(dimension);
       DoubleMatrix2D  T = new SparseDoubleMatrix2D(this.dimension,this.dimension);
       DoubleMatrix1D kinNormVector  = new   DenseDoubleMatrix1D(dimension);
       DoubleMatrix1D rankVector2  = new   DenseDoubleMatrix1D(dimension);
       double v;
       for (int s=0; s<this.dimension; s++){
           v = inVector[s]/totalWeight;
           piVector.setQuick(s, v);
           kinNormVector.setQuick(s, v);
           for (int t=0; t<this.dimension; t++){
               v=matrix.getQuick(s, t);
               if (v!=0) T.setQuick(t, s, v/outVector[s]);
           }
       }
//       if (dimension<11)
//       {
//          printVector(System.out,"  ","in", inVector, true);
//          printVector(System.out,"  ","out", outVector, true);
//          printMatrix(System.out,"  ","transition", T, true);
//        }

// Nice idea here was to use the in built systems but we need a transpose of adjacency matrix
// so that we can use matrix mult on the transition matrix
      Algebra alg= new Algebra();
//       T=alg.transpose(matrix);
//       DefineTransitionMatrix DTM  = new DefineTransitionMatrix();
//       T.forEachNonZero(DTM);

//       double diff=0;
       double newdiff=0;
//       double oneLength=0;
        //double sumOld=-1.0;
        double sumNew = piVector.zSum(); //alg.norm1(piVector);
        //double alpha=1.0;
        double beta=1.0-alpha;
        double absoluteTolerance=relError;
        //cern.jet.math.Functions F = cern.jet.math.Functions.functions; // naming shortcut (alias) saves some keystrokes:
// @see http://acs.lbl.gov/software/colt/api/cern/colt/matrix/doc-files/function1.html
        DoubleMatrix1D tempVector  = new   DenseDoubleMatrix1D(dimension);
        int iter=-1;
        for (iter=0; iter<maxIter; iter++){
         // Or use following with beta=1-alpha, yz=invector.
           // Linear algebraic matrix-vector multiplication; z = alpha * A * y + beta*z.
           //  zMult(DoubleMatrix1D y, DoubleMatrix1D z, double alpha, double beta, boolean transposeA)
           T.zMult(piVector,rankVector2,alpha/sumNew,0.0,false) ;
           if (beta!=0) rankVector2.assign(kinNormVector,Functions.plusMult(beta));
//                   x.assign(y, F.plusMult(a));    // x[i] = x[i] + y[i]*a
           //if (dimension<11) printVector(System.out,"  ",iter+" iteration, rankVector2", rankVector2, true);
           //sumOld=Math.abs(sumNew);
           sumNew = rankVector2.zSum(); //= alg.norm1(piVector);
           //piVector  = new   DenseDoubleMatrix1D(dimension);
           T.zMult(rankVector2,piVector,alpha/sumNew,beta,false) ;
           if (beta!=0) piVector.assign(kinNormVector,Functions.plusMult(beta));
           //if (dimension<11) printVector(System.out,"  ",iter+" iteration, piVector", piVector, true);
           sumNew = piVector.zSum(); //= alg.norm1(piVector);
           //if (Math.abs(Math.abs(sumNew)-sumOld)<absoluteTolerance) break;
           newdiff=0;
           for (int i=0; i<dimension; i++) newdiff+=Math.abs(piVector.getQuick(i)-rankVector2.getQuick(i));
           if (Math.abs(newdiff/sumNew)<relError) break;
//           diff=newdiff;
       }
       sumNew=piVector.zSum();
       for (int i=0; i<piVector.size(); i++) piVector.setQuick(i, piVector.getQuick(i)/sumNew);
       System.out.println("Convergence in calculatePiVector factor "+String.format("%8.6g",Math.abs(newdiff/sumNew))+" after "+iter+" iterations");
    }

//    public class DefineTransitionMatrix implements IntIntDoubleFunction{
//        @Override
//        public double apply(int row,
//                    int column,
//                    double cellValue){
//            return matrix.getQuick(column,row)/outVector[column];
//        }
//    }
    /**
     * Calculates (A)^n k_in
     * <p>Stops when scaling differs by less the 1e-6 between iterations or after 10 iterations.
     * <p> See also {@link #calculatePiVector(double, int) }.
     * @param maxIterations
     * @param absoluteTolerance
     * @deprecated Use {@link #calculatePiVector() }
     */
    public void calcPiVectorAlternate(int maxIterations, double absoluteTolerance){
       piVector  = new   DenseDoubleMatrix1D(inVector);

        // TODO should use transition matrix class and add this method to that class
       DoubleMatrix2D  T = new SparseDoubleMatrix2D(this.dimension,this.dimension);
       double v;
       for (int s=0; s<this.dimension; s++)
           for (int t=0; t<this.dimension; t++){
               v=matrix.getQuick(s, t);
               if (v!=0) T.setQuick(t, s, v/outVector[s]);
           }
//      Algebra alg= new Algebra();
//       T=alg.transpose(matrix);
//       DefineTransitionMatrix DTM  = new DefineTransitionMatrix();
//       T.forEachNonZero(DTM);


        double lambdaOld=1.0;
        double lambda = Math.abs(piVector.zSum()); //alg.norm1(piVector);
        double beta=0.0;
        double alpha=1-beta;
        while((--maxIterations)>0){
            //z = lambda *(1-beta)* matrix * piVector + beta*lambda*piVector.
            piVector = matrix.zMult(piVector,piVector,lambda*alpha,beta*lambda,false) ;
            lambdaOld=lambda;
            lambda =Math.abs(piVector.zSum()); //= alg.norm1(piVector);
            if (Math.abs(lambda-lambdaOld)<absoluteTolerance) break;
        }
        lambda = Math.abs(piVector.zSum());
        for (int s=0; s<this.dimension; s++) piVector.set(s,piVector.get(s)/lambda);
    }

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
        for (int e=0; e<totalNumberStubs; e++)
        {
            source = edgeSourceList[e++];
            target = edgeSourceList[e];
            double newValue=1+matrix.get(source,target);
            matrix.set(source,target,newValue); //matrix[source][target]++;
            if (!directedGraph) matrix.set(target,source,newValue);
        }        
    } // eo makeMatrix

    
    public void check(){
        double totin=0;
        double totout=0;
        double tot=0;
        for (int i = 0; i < dimension; i++) {
            totout+=outVector[i];
            totin+=inVector[i];
            for (int j = 0; j < dimension; j++) {
                tot+=get(i,j); 
            }
        }
        System.out.println("!!! Check in Adjacency Matrix, Totals for Matrix, In, Out "+tot+", "+totin+", "+totout); 
    }
    // -----------------------------------------------------------------------
      /**
     * Prints vector to a PrintStream.
     * @param PS printstream such as System.out
     * @param sep separation string such as a tab character.'
     * @param name of vector
     * @param headersOn true if want row and column labelled with names (if given) or numbers.
     */
    public void printVector(PrintStream PS, String sep, String name, double [] vector, boolean headersOn){
       String doubleFormat ="%6.3f";
       String intFormat ="%6d";
       System.out.println(name+" vector");
       if (headersOn){
            for (int j=0; j<dimension;j++) PS.print(String.format(intFormat,j)+sep);
            PS.println();
        }
        for (int i=0; i<dimension;i++) PS.print(String.format(doubleFormat,vector[i])+sep);
        PS.println();
    }
      /**
     * Prints vector to a PrintStream.
     * @param PS printstream such as System.out
     * @param sep separation string such as a tab character.'
     * @param name of vector
     * @param headersOn true if want row and column labelled with names (if given) or numbers.
     */
    public void printVector(PrintStream PS, String sep, String name, DoubleMatrix1D vector, boolean headersOn){
       printVector(PS, sep, name, vector.toArray(), headersOn);
    }

    /**
     * Prints matrix to a PrintStream.
     * @param PS printstream such as System.out
     * @param sep separation string such as a tab character.'
     * @param name name of matrix
     * @param mat matrix to print
     * @param headersOn true if want row and column labelled with names (if given) or numbers.
     */
    public void printMatrix(PrintStream PS, String sep, String name, DoubleMatrix2D mat, boolean headersOn)
    {
        String doubleFormat ="%6.3f";
        String intFormat ="%6d";
        System.out.println(name+" matrix");
        if (headersOn){
            PS.print("      "+sep);
            for (int j=0; j<dimension;j++) PS.print(String.format(intFormat, j)+sep);
            PS.println();
        }
        for (int i=0; i<dimension;i++)
        {
            if (headersOn) PS.print(String.format(intFormat, i)+sep);
            for (int j=0; j<dimension; j++) PS.print(String.format(doubleFormat,mat.get(i,j))+sep);
            PS.println();
        }
    }//eo printMatrix


    /**
     * Prints matrix to a PrintStream.
     * @param PS printstream such as System.out
     * @param sep separation string such as a tab character.'
     * @param nameList array of names.  Number must equal size of Adjacency matrix
     * @param headersOn true if want row and column labelled with names (if given) or numbers.
     */
    public void printMatrix(PrintStream PS, String sep, String [] nameList, boolean headersOn)
    {
        boolean headerNamesOn=false;
        if (headersOn){
            if (nameList!=null && nameList.length==dimension) headerNamesOn=true;
            PS.print(" "+sep);
            for (int j=0; j<dimension;j++) PS.print((headerNamesOn?nameList[j]:j)+sep);
            PS.println();
        }
        for (int i=0; i<dimension;i++)
        {
            if (headersOn) PS.print((headerNamesOn?nameList[i]:i)+sep);
            for (int j=0; j<dimension; j++) PS.print(this.get(i,j)+sep);
            PS.println();
        }
    }//eo printMatrix
    /**
     * Prints matrix to a PrintStream.
     * @param PS printstream such as System.out
     * @param sep separation string such as a tab character.'
     * @param headersOn true if want row and column labelled with numbers.
     */
    public void printMatrix(PrintStream PS, String sep, boolean headersOn)
    {
       printMatrix(PS, sep, null, headersOn);
    }//eo printMatrix

        // -----------------------------------------------------------------------       
    /**
     * Prints matrix to standard output (screen).
          * @param sep separation string such as a tab character.'
     * @param headersOn true if want row and column labels.
*/
    public void printMatrix(String sep, boolean headersOn)  
    {
          printMatrix(System.out, sep, headersOn);
    }

//    protected void initialisePiVector(){
//      piVector = new DenseDoubleMatrix1D(this.dimension);
//      for (int i=0; i<piVector.size(); i++) piVector.set(i, inVector[i]);
//    }

    
    
}
