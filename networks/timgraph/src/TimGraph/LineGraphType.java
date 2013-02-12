/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TimGraph;

import TimUtilities.GeneralMode;

/**
 * This defines the index used to represent different types of line graph.
 * <p>The different types are defined in the weighted graph paper
 * <em>Edge Partitions and Overlapping Communities in Complex Networks</em>,
 * T.S.Evans and R.Lambiotte Eur.Phys.J.B <b>77</b> (2010) 265-272 
 * [<tt>arXiv:0912.4389</tt>].  They are set by the
 * <tt>type</tt> parameters and defined by constant strings in this class,
 * {@link #lgExtensionList} for file name additions and {@link #lgExtensionDescription}.
 * The algebraic names are in {@link #lgTypeList} and are of the form <tt>Ts</tt>
 * where <tt>T</tt> = C, D or E, and <tt>s</tt> = n (no self loops), t (tilde, with self-loops).
 * These are in {@link #lgTypeList} 
 * <p>The numbers correspond as follows:
 * <ul>
 * <li>0 = Line Graph L(G)=C(G)</li>
 * <li>1 = Line Graph with self-loops, Ctilde</li>
 * <li>2 = Degree Weighted Line Graph, D(G)</li>
 * <li>3 = Degree Weighted Line Graph with self-loops, Dtilde(G) (E(G) in unweighted paper)</li>
 * <li>4 = Strength Weighted Line Graph, E(G) </li>
 * <li>5 = Strength Weighted Line Graph with self-loops, Etilde(G)</li>
 * </ul>
 * @author time
 */
public class LineGraphType extends GeneralMode {
    
    /**
     * List of types by algebraic type.
     * These are the abbreviations used in  <em>Edge Partitions and Overlapping Communities in Complex Networks</em>,
     * T.S.Evans and R.Lambiotte Eur.Phys.J.B <b>77</b> (2010) 265-272 
     * [<tt>arXiv:0912.4389</tt>].  
     * The algebraic names are in {@link #lgTypeList} and are of the form <tt>Ts</tt>
     * where <tt>T</tt> = C, D or E, and <tt>s</tt> = n (no self loops), t (tilde, with self-loops)
     */
    public final static String [] lgTypeList  = {"Cn", "Ct", "Dn", "Dt", "En", "Et"};
    /**
     * List of extension names.
     * <p>These are more descriptive that the algebraic type.
     */
    public final static String [] lgExtensionList  = {"LG", "LGsl", "DWLG", "DWLGsl", "SWLG", "SWLGsl"};
    /**
     * List of names.
     * <p>These are full descriptions of types of line graph.
     */
    public final static String [] lgExtensionDescription  = {"Line Graph L(G)=C(G)",
    "Line Graph with self-loops, Ctilde",
    "Degree Weighted Line Graph, D(G)",
    "Degree Weighted Line Graph with self-loops, Dtilde(G)",
    "Strength Weighted Line Graph, E(G)",
    "Strength Weighted Line Graph with self-loops, Etilde(G)"};

    /**
     * Default line graph type number.
     * <br>Value is {@value }
     */
     final static int DEFAULTMODE =0;

    
   /** Basic Constructor
     */
    public LineGraphType() {
        setUniqueNameLength(2);
        setUp(lgTypeList, lgExtensionDescription, DEFAULTMODE);
    }
   /** Basic Constructor
    * @param modeName short name of mode to be used to set
     */
    public LineGraphType(String modeName) {
        setUniqueNameLength(2);
        setUp(lgTypeList, lgExtensionDescription, DEFAULTMODE);
        this.setFromName(modeName);
    }

   /** Basic Constructor
    * @param mode number of mode to be used to set
     */
    public LineGraphType(int mode) {
        setUniqueNameLength(2);
        setUp(lgTypeList, lgExtensionDescription, DEFAULTMODE);
        this.set(mode);
    }
    
    /**
     * Indicates if line graph has self-loops.
     * @return true if has self-loops.
     */
    public boolean hasSelfLoops(){return ( ((getNumber()&1)>0) ? true:false);}

    /**
     * The algebraic names are in {@link #lgTypeList} and are of the form <tt>Ts</tt>
     * where <tt>T</tt> = C, D or E, and <tt>s</tt> = n (no self loops), t (tilde, with self-loops).
     * These are in {@link #lgTypeList}.
     * @return the algebraic type
     */
    public String getType(){return lgTypeList[getNumber()];}
}
