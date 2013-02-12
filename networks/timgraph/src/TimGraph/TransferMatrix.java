/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * Uses sparse matric representation.
 * <p>Need to decide about in and out vectors.  These ought to be those of the
 * underlying adjacency matrix
 * @author time
 */
public class TransferMatrix extends AdjacencyMatrix {

    /**
     * Inialises adjacency matrix to internal dimension.
     */
    private void initialiseMatrix() {
        matrix = new SparseDoubleMatrix2D(dimension,dimension);
        //for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) matrix[i][j]=0;
        totalWeight = 0;
    }

    /**
     * Calculates the vectors of incoming and outgoing vertex strengths.
     */
    @Override
    public void calculateInOutVectors(){
        throw new RuntimeException("Note defined yet for TransferMatrix class");
//        inVector = new double[dimension];
//        outVector = new double[dimension];
//        for (int i = 0; i < dimension; i++) {
//            outVector[i] = 0;
//            inVector[i]=0;
//            for (int j = 0; j < dimension; j++) {
//                outVector[i]+=get(j,i); //???
//                 inVector[i]+=get(i,j); //??? matrix[j][i];
//            }
//        }

    }


        // -----------------------------------------------------------------------
    /**
     * Initialises adjacency matrix using a matrix.
     * @param m matrix used to initialise.
     */
    @Override
    public void make(double [][] m) {
        throw new RuntimeException("Note defined yet for TransferMatrix class");
//        dimension = m[0].length;
//        initialiseMatrix();
//        for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) {
//            double value=m[i][j];
//            totalWeight+=value;
//            matrix.set(i,j,value);
//        }
    }

    // -----------------------------------------------------------------------
    /**
     * Initialises adjacency matrix to be <tt>M^2-M</tt> where M is given matrix.
     * @param m matrix used to initialise.
     */
    @Override
    public void makeA2mA(double [][] m) {
        throw new RuntimeException("Note defined yet for TransferMatrix class");
//        dimension = m[0].length;
//        initialiseMatrix();
//        for (int i=0; i<dimension;i++) for (int j=0; j<dimension; j++) {
//            double value= - m[i][j];
//            for (int k=0; k<dimension; k++) value += m[i][k]*m[k][j];
//            totalWeight+=value;
//            matrix.set(i,j,value);
//        }
    }

    /**
     * Initialise transfer matrix.
     * !!! NEED TO SET UP IN AND OUT VECTORS !!!
     * @param graph timgraph defining the graph.
     */
    @Override
    public void make(timgraph graph) {
        if (!graph.isVertexEdgeListOn()) throw new RuntimeException("*** "+graph.inputName.getNameRoot()+" needs vertexEdgeList");
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
            double str = graph.getVertexOutStrength(s);
            if (str<1e-20) throw new RuntimeException("strength of vertex "+s+" is too small at "+str);
            int t = graph.getVertexFromStub(e); // e now points to target vertex of edge in edgeSourceList
            increaseEdgeWeight(s,t,w,undirected);
            inVector[t]+=w;
            outVector[s]+=w;

        }
    }

}
