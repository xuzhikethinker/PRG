/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.algorithms;

import TimGraph.timgraph;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import java.io.PrintStream;

/**
 * Stores data about structural hole analysis for one vertex
 * @author time
 */
public class StructuralHoleVertexData {
    /**
     * global index of vertex
     */
    int index=timgraph.IUNSET;
    double effectiveSize=timgraph.DUNSET;
    double efficiency=timgraph.DUNSET;
    double constraint=timgraph.DUNSET;
    IntArrayList nn;
    DoubleArrayList constraintnn;

    public StructuralHoleVertexData (){
        index=timgraph.IUNSET;
        effectiveSize=timgraph.DUNSET;
        efficiency=timgraph.DUNSET;
        constraint=timgraph.DUNSET;
    }
    public StructuralHoleVertexData (int indexN){
        index=indexN;
        effectiveSize=timgraph.DUNSET;
        efficiency=timgraph.DUNSET;
        constraint=timgraph.DUNSET;
    }

    /**
     * Deep copy of all entries.
     * @param shvd existing data
     */
    public StructuralHoleVertexData (StructuralHoleVertexData shvd){
        index=shvd.index;
        effectiveSize=shvd.effectiveSize;
        efficiency=shvd.efficiency;
        constraint=shvd.constraint;
        if (shvd.nn!=null) {
            nn= new IntArrayList();
            nn.addAllOf(shvd.nn);
        }
        if (shvd.constraintnn!=null){
            constraintnn =  new DoubleArrayList();
            constraintnn.addAllOf(shvd.constraintnn);
        }
    }
//    /**
//     * Set all values.
//     * @param effSize effective size
//     * @param constraints constraint
//     * @param efficiencys efficiency.
//     */
//    public StructuralHoleVertexData (int indexN, double effectiveSizeN,
//            double efficiencyN, double constraintN,
//        IntArrayList nnN,  DoubleArrayList constraintnnN){
//        index=indexN;
//        effectiveSize=effectiveSizeN;
//        efficiency=efficiencyN;
//        constraint=constraintN;
//        nn=nnN;
//        constraintnn=constraintnnN;
//    }

    /**
     * Add data on nearest neighbours.
     * <p>Initialises if don't exist.
     * @param t index of neighbour
     * @param constraintst constraint value for link from index to this neighbour
     */
    public void addnn(int t,double constraintst){
        if (nn==null) nn = new IntArrayList();
        nn.add(t);
        if (constraintnn==null) constraintnn = new DoubleArrayList();
        constraintnn.add(constraintst);
    }
    /**
     * Add parameters for vertex.
     * @param constraints constraint
     * @param effSize effective size
     * @param efficiencys efficiency=(effective size/degree).
     */
    public void addglobal(double constraints, double effSize, double efficiencys){
        this.effectiveSize=effSize;
        constraint=constraints;
        efficiency=efficiencys;
    }

    /**
     * Prints data on vertex.
     * <p>Unset values written as strings and limited number of digits and width.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * @param digits number of digits after decimal point
     * See also {@link #printVertexDataLabel(java.io.PrintStream, java.lang.String)}
     */
    public void printVertexData(PrintStream PS,String sep, int digits){
        if (effectiveSize==timgraph.DUNSET) PS.println(index+sep+"UNSET"+sep+"UNSET"+sep+"UNSET");
        else PS.println(timgraph.stringInt(index,digits+3)+sep+timgraph.stringDouble(constraint,digits+3,digits)
                +sep+timgraph.stringDouble(effectiveSize,digits+3,digits)
                +sep+timgraph.stringDouble(efficiency,digits+3,digits));
    }
    /**
     * Prints data on vertex.
     * <p>Unset values written as string but otherwise raw.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * @param digits number of digits after decimal point
     * See also {@link #printVertexDataLabel(java.io.PrintStream, java.lang.String)}
     */
    public void printVertexDataNoIndex(PrintStream PS,String sep){
        PS.println(getVertexDataNoIndexString(sep));
    }
    /**
     * Prints data on vertex.
     * <p>Unformatted raw data
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * See also {@link #printVertexDataLabel(java.io.PrintStream, java.lang.String)}
     */
    public void printVertexData(PrintStream PS,String sep){
        PS.println(getVertexDataString(sep));
    }
    /**
     * Returns string of data on vertex.
     * <p>Unformatted raw data except unset values are written as unset.
     * @param sep separation character
     * See also {@link #printVertexDataLabel(java.io.PrintStream, java.lang.String)}
     */
    public String getVertexDataString(String sep){
        return (timgraph.stringInt(index)+sep+getVertexDataNoIndexString(sep));
    }
    /**
     * Returns string of data on vertex without index.
     * <p>Unformatted raw data except unset values are written as unset.
     * @param sep separation character
     * See also {@link #printVertexDataLabel(java.io.PrintStream, java.lang.String)}
     */
    public String getVertexDataNoIndexString(String sep){
        return (timgraph.stringDouble(constraint)
                +sep+timgraph.stringDouble(effectiveSize)
                +sep+timgraph.stringDouble(efficiency));
    }
    /**
     * Prints label for list of data on edges.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * See also {@link #printVertexData(java.io.PrintStream, java.lang.String, int)}
     */
    static public void printVertexDataLabel(PrintStream PS,String sep){
        PS.println(getVertexDataLabel(sep));
    }

    /**
     * Prints label for list of data on edges.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * See also {@link #printVertexData(java.io.PrintStream, java.lang.String, int)}
     */
    static public void printVertexDataNoIndexLabel(PrintStream PS,String sep){
        PS.println(getVertexDataNoIndexLabel(sep));
    }

    /**
     * Prints label for list of data on edges.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * See also {@link #printVertexData(java.io.PrintStream, java.lang.String, int)}
     */
    static public String getVertexDataNoIndexLabel(String sep){
        return("Constraint"+sep+"Effective.Size"+sep+"Efficiency");
    }
    /**
     * Prints label for list of data on edges.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * See also {@link #printVertexData(java.io.PrintStream, java.lang.String, int)}
     */
    static public String getVertexDataLabel(String sep){
        return("Source"+sep+getVertexDataNoIndexLabel(sep));
    }

    /**
     * Prints list of data on edges.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * @param digits number of digits after decimal point
     * See also {@link #printEdgeDataLabel(java.io.PrintStream, java.lang.String) }
     */
    public void printEdgeData(PrintStream PS,String sep, int digits){
        if (nn==null) return;
        for (int t=0; t<nn.size(); t++){
            PS.println(index+sep+nn.get(t)+sep+timgraph.stringDouble(constraintnn.get(t),digits+3,digits));
        }
    }
    /**
     * Prints label for list of data on edges.
     * @param PS PrintStream such as System.out
     * @param sep separation character
     * See also {@link #printEdgeData(java.io.PrintStream, java.lang.String)}
     */
    static public void printEdgeDataLabel(PrintStream PS,String sep){
        PS.println("Source"+sep+"Target"+sep+"Constraint");
    }


    public void setToMinimum(StructuralHoleVertexData shvd){
        index=Math.min(index,shvd.index);
        effectiveSize=Math.min(effectiveSize,shvd.effectiveSize);
        efficiency=Math.min(efficiency,shvd.efficiency);
        constraint=Math.min(constraint,shvd.constraint);
    }
    public void setToMaximum(StructuralHoleVertexData shvd){
        index=Math.max(index,shvd.index);
        effectiveSize=Math.max(effectiveSize,shvd.effectiveSize);
        efficiency=Math.max(efficiency,shvd.efficiency);
        constraint=Math.max(constraint,shvd.constraint);
    }


}
