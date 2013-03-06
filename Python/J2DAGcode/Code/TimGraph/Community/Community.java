/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.timgraph;


import DataAnalysis.MutualInformation;
import java.io.PrintStream;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;


/**
 * General Element community structure.
 * <p>Not limited to partitions of sets.  
 * The Elements can be vertices or edges community.
 * @author time
 */
public class Community {
    
   /**
     * Negative integer used to indicate unset value. {@value}
     */
    public static final int UNSET=-987123456;

   /**
     * Negative integer used to indicate unset value. {@value}
     */
    public static final double DUNSET=-9.7531e86;

    /**
     * Negative integer used to give a temporary community label to just one Element. {@value}
     */
    public static final int NEWCOMMUNITYLABEL =-86421357;  
    
    /**
     * Negative integer used to indicate that element is outside the community structure. {@value}
     */
    public static final int NOCOMMUNITYLABEL =-789456123;  
    

    /**
     * Tolerance used for some tests.
     */
    public static final double TOLERANCE = 1e-6;
         
    /**
     * Label giving name of partition.
     */
    protected String name= "general";
    
    /**
     * Number of Elements in the partition.
     * <p>These could be vertices or edges.
     */
    protected int numberElements= UNSET;
    
    /**
     * Label giving name of elements.
     * <p>The strings to be used if vertices or edges are
     * given as constants in this class.
     */
    protected String nameOfElements= "elements";
    
   /**
     * Names of elements.
     */
    protected String [] elementName;

    
    /**
     * <tt>nameOfElements</tt> for vertices.
     */
    public final static String verticesName = "vertices";
    /**
     * <tt>nameOfElements</tt> for edges.
     */
    public final static String edgesName = "edges";
    
    

    /**
     * Number of communities.
     */
    protected int numberCommunities = UNSET;
    
    /**
     * Names of communities.
     */
    protected String [] communityName;
    

    /**
     * Used to store the community structure.
     * <br><tt>communityMatrix[v][c]</tt> is the fraction of Element v in community c
     */
    //protected double [][] communityMatrix;
     protected SparseDoubleMatrix2D communityMatrix;
    /**
     * The quality class including functional form.
     */
//    protected QualitySparse quality;
    protected Quality quality;
    
    /**
     * Stores last calculated quality value.
     */
    protected double Q = UNSET;
    

    
   /**
     * Graph whose Element community this is.
     * <br>Need not be defined.
     */
    protected timgraph graph;
    
        /**
     * Sets level of information to print out.
     */
    public int infoLevel=-2;


 /**
 * Empty Constructor needed for extensions of community .
 */
public Community(){}

    /**
     * Constructor used to set basic parameters from given timgraph.
     * @param tg input graph
     * @param qdef selects modularity definition to use
     * @param newlambda scaling factor for null model in quality function
     * @param qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory
     * @param newInfoLevel sets level of information to be printed
     */
public Community(timgraph tg, int qdef, double newlambda, int qualityType, int newInfoLevel){
    infoLevel=newInfoLevel;
    initialiseGraph(tg, qdef,  qualityType, newlambda);
}

/**
 * Bare Constructor used to prepare a community.
 * @param inputname name of community
 */
public Community(String inputname){
    name=inputname;
    }

/**
 * Constructor used to prepare a community.
 * @param nv number of Elements
 */
public Community(int nv){
    numberElements=nv;
    }
/**
 * Constructor used to store basic information about a community.
 * <br>This does not necessarily provide any ability to recalculate.  
 * It merely stores basic information about a community structure e.g. the best one.
 * <br>This is really for use with a partition
 * @param q quality
 * @param nv number of Elements
 * @param ne number of edges (not used)
 * @param cov vector for a partition with community labels, i.e. <code>cov[s]</code> is community of Element s. (not used)
 */
public Community(double q, int nv, int ne, int [] cov){
    Q=q;
    numberElements=nv;
    }
    
    
    
/**
 * Constructs a community from a partition.
 * <br>No graph is defined here.
 * @param p A partition of the graph.
 */
    public Community (Partition p)
    {
            initialiseFromPartition(p);
    }
/**
 * Constructs a community from a partition.
 * <br>Compares the number of Elements and (edges/2) in the graph to the
 * number of Elements in the partition to decide if this is a Element or edge partition
 * with the Element partition being used if numbers are equal.  No ElementCommunity is 
 * set up if neither of these numbers matches the number of Elements in the partition.
 * @param tg timgraph whose Element partition is given
 * @param p partition of the graph.
 * @param qdef selects modularity definition to use
 * @param qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory   
 * @param newlambda scaling factor for null model in quality function
 * @deprecated needs to be made general
 */
    public Community (timgraph tg, Partition p, int qdef, int qualityType, double newlambda){
      initialiseGraph(tg, qdef,  qualityType, newlambda);
////      int n =p.getNumberElements();
//      if (tg.getNumberVertices()==p.getNumberElements()) initialiseFromElementPartition(p);
//      else if (tg.getNumberStubs()/2==p.getNumberElements()) initialiseFromEdgePartition(tg,p);
//      else System.err.println("*** Community constructor: Neither number of Elements "+tg.getNumberElements()+" nor the number of edges "+tg.getNumberStubs()+" matches Elements in partition "+p.getNumberElements());
       
    }
    

/**
 * Initialises features related to an input graph.
 * @param tg timgraph whose Element partition is given
 * @param qdef selects modularity definition to use
 * @param qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory   
 * @param newlambda scaling factor for null model in quality function
 */
    public void initialiseGraph(timgraph tg, int qdef, int qualityType, double newlambda){
            graph =tg;
            quality = QualityType.makeQuality(tg, qdef, qualityType, newlambda, infoLevel);
            Q = UNSET;
    }

    
    

/**
 * Description of type of quality class being used.
 */
    public String qualityType(){
            return quality.getQualityTypeDescription();
    }
    
       
 /**
 * Constructs a community from a partition.
 * @param p general partitions of the graph.
 */
    private void initialiseFromPartition(Partition p){
        name=p.getName()+"partition";
        initialiseEmptyCommunity(p.getNumberElements(),p.getNumberOfCommunities());
//        numberCommunities = p.getNumberOfCommunities();
//        numberElements=p.getNumberElements();
//        communityMatrix = new double[numberElements][numberCommunities];
        for (int v=0; v<numberElements; v++){
            //for (int c=0; c<numberCommunities; c++) communityMatrix.set(v,c,0);
            //communityMatrix[v][p.getCommunity(v)]=1;
            communityMatrix.set(v,p.getCommunity(v),1.0);
        }
    }

    /**
     * Initialises empty community matrix and numbers of elements and communities.
     * @param ne number of elements
     * @param nc number of communities
     */
    public void initialiseEmptyCommunity(int ne, int nc){
        numberElements=ne;
        numberCommunities=nc;
        communityMatrix = new SparseDoubleMatrix2D(numberElements,numberCommunities); //new double[numberElements][numberCommunities];
    }
    
    /**
     * Initialises empty element name list.
     */
    public void initialiseElementNameList(){
        elementName = new String[numberElements];
    }
    
    /**
     * Initialises empty community name list.
     */
    public void initialiseCommunityNameList(){
        communityName = new String[numberCommunities];
    }
    
  
   /**
     * Calculate quality of graph for current community.
     * @return quality of graph for current community.
     */
    public double calcQuality(){Q= quality.calc(communityMatrix,numberCommunities); return Q;}

//   /**
//     * Calculate number of communities.
//    * <p>Sets numberCommunities
//     * @return number of communities.
//     */
//    public int calcNumberCommunities(){
//        numberCommunities=UNSET;
//        if (communityMatrix==null) return;
//
//
//    }

        /**
     * Number of distinct communities.
     * @return number of communities
     */
    public int getNumberCommunities(){
        //if (numberCommunities<1) recalculateCommunityLabels();
        return numberCommunities ;
    }

        /**
     * Number of Elements.
     * @return number of Elements
     */
    public int getNumberElements(){
        return numberElements ;
    }
    
    
   /**
     * Returns <tt>communityMatrix[e][c]</tt>, the fraction of element e in community c
     * @param e index of element
     * @param c index of community
     * @return <tt>communityMatrix[e][c]</tt>, the fraction of element e in community c
     */
    public double getCommunityMembership(int e, int c){return communityMatrix.get(e,c);}

   /**
     * Sets <tt>communityMatrix[e][c]</tt>, the fraction of element e in community c
     * @param e index of element
     * @param c index of community
     * @param v value to set <tt>communityMatrix[e][c]</tt>
     */
    public void setCommunityMembership(int e, int c, double v){communityMatrix.set(e,c,v);}

   /**
     * Increases <tt>communityMatrix[e][c]</tt>, the fraction of element e in community c
     * @param e index of element
     * @param c index of community
     * @param v value by which to increase <tt>communityMatrix[e][c]</tt>
     */
    public void increaseCommunityMembership(int e, int c, double v){communityMatrix.set(e,c,v+communityMatrix.get(e,c));}

    /**
     * Returns name of an individual element.
     * <p>If <tt>elementName[]</tt> is null then a string with e followed by the index number
     * is returned.
     * @param e index of element
     * @return <tt>elementName[e]</tt>, the name of the individual element.
     */
    public String getElementName(int e){return (elementName==null?"e"+e:elementName[e]);}
    
    /**
     * Tests to see if has explicit names for each element.
     * @return true if elements have names.
     */
    public boolean hasElementName(){return (elementName==null?false:true);}
    
    /**
     * Sets name of an element.
     * <p>If <tt>elementName[]</tt> is null then it is initialised to be of the correct size.
     * @param e index of an element
     * @param name the name of an element.
     */
    public void setElementName(int e, String name){
        if (elementName==null) initialiseElementNameList();
        elementName[e]=name;
    }
    /**
     * Returns name of community.
     * <p>If <tt>communityName[]</tt> is null then a string with c followed by the index number
     * is returned.
     * @param c index of community
     * @return <tt>communityName[c]</tt>, the name of the community.
     */
    public String getCommunityName(int c){return (communityName==null?"c"+c:communityName[c]);}
    
    /**
     * Tests to see if has explicit names for communities.
     * @return true if communities have names.
     */
    public boolean hasCommunityName(){return (communityName==null?false:true);}
    
    /**
     * Sets name of community.
     * <p>If <tt>communityName[]</tt> is null then it is initialised to be of the correct size.
     * @param c index of community
     * @param name the name of the community.
     */
    public void setCommunityName(int c, String name){
        if (communityName==null) initialiseCommunityNameList();
        communityName[c]=name;
    }
    
    /**
     * Last calculated quality value.
     * <br>Recalculates if Q is unset.
     * @return quality of graph for current community. 
     */
    public double getQuality(){
         if (Q==UNSET) calcQuality();
         return Q;
    }

    /**
     * Sets the current quality value directly.
     * @param Qnew new value of quality.
     */
    public void setQuality(double Qnew){Q=Qnew;}

    /**
     * Check matrix normalisation.
     * <p>Checks to see if <tt>sum_c communityMatrix[v][c] =1</tt> for all vertices v
     * @param tolerance sum must be one within this tolerance
     * @return -1 if OK, otherwise index v where failure first found
     */
    public int checkMatrixNormalisation(double tolerance){
        for (int v=0; v<numberElements; v++){
            double sum=0;
            for (int c=0; c<numberCommunities; c++) 
                sum+= communityMatrix.get(v,c);//[v][c];
            if (Math.abs(sum-1.0)>tolerance) return v;
        }
        return -1;
    }
    
    /**
     * Force matrix normalisation.
     * <p>Makes <tt>sum_c communityMatrix[v][c] =1</tt> for all vertices v
     * @param tolerance sum must be greater than this otherwise fails
     * @return -1 if OK, otherwise index v where failure first found
     */
    public int normaliseMatrix(double tolerance){
        double f=-1;
        for (int v=0; v<numberElements; v++){
            double sum=0;
            for (int c=0; c<numberCommunities; c++) 
                sum+= communityMatrix.get(v,c); //[v][c];
            if (sum<-tolerance) return v;
            for (int c=0; c<numberCommunities; c++) {
                f=communityMatrix.get(v,c);
                if (f!=0) communityMatrix.set(v,c,f/sum);
            }
        }
        return -1;
    }
    
    
    /** Prints Communities.
     * <br>Prints basic information.with headers and with Element labels.
     *@param PS a print stream for the output such as System.out
     *@param cc comment string
     *@param sep separation string
     *@TODO Make stats global and split into calc and print
     */
     public void  printInformation(PrintStream PS, String cc, String sep){ 
          PS.println(cc+"Number of "+nameOfElements+sep+ valueString(numberElements)
                  +sep+"Quality ("+sep+(quality==null?"???":quality.Qdefinition(sep))+") "
                  +sep+valueString(Q)
                  +sep+"Number of Communities"+sep+ valueString(numberCommunities));
        }

        /** Prints Statistics.
     * @param PS a print stream for the output such as System.out
     * @param cc comment string
     * @param sep separation string
     */
     public void  printStats(PrintStream PS, String cc, String sep){
          if  (graph==null) return;
          System.out.println("VARIOUS STATISTICS");
          // ABL Nature paper.  Coverage. Density.  Entropy.
          // Number of elements.
          // For each community and total.
          // Imitate in VertexPartition.
          int maxCutoff = 5; //
          /**
           * Coverage.
           * <p><tt>coverage[n]</tt> is the number of elements
           * which are in communities with more than <tt>n</tt> elements.
           * See ABL Nature paper.
           */
          double [] coverage = new double[maxCutoff];
          /**
           * Total element membership fraction for each element
           * <p>sum_c (getCommunityMembership(v, c)>0?1:0)
           */
          int [] elementCommunityNumber  = new int[this.numberElements];
          /**
           * Total element membership fraction for each community
           * <p>sum_v (getCommunityMembership(v, c)>0?1:0)
           */
          int [] communityElementNumber  = new int[this.numberCommunities];
          /**
           * Total element membership fraction for each community
           * <p>sum_v getCommunityMembership(v, c)
           */
          double [] communityElementFraction  = new double[this.numberCommunities];
          /**
           * Total element membership entropy for each community
           * <p>sum_v f.ln(f) where f=getCommunityMembership(v, c)
           */
          double [] communityEntropy  = new double[this.numberCommunities];
          /**
           * Total vertex membership entropy for each element
           * <p>sum_c f.ln(f) where f=getCommunityMembership(v, c)
           */
          double [] elementEntropy  = new double[this.numberElements];
          double totalCommunityEntropy=0;
          double totalVertexEntropy=0;
          int totalCommunityNumber=0;
          int totalVertexNumber=0;
          double ds=0;
          for (int e=0; e<this.numberElements; e++){
              for (int c=0; c<numberCommunities; c++){
                  double f= this.getCommunityMembership(e, c);
                  communityElementFraction[c]+=f;
                  if (f<1e-20) continue;
                  communityElementNumber[c]++;
                  elementCommunityNumber[e]++;
                  totalCommunityNumber++;
                  totalVertexNumber++;
                  ds= f*Math.log(f);
                  communityEntropy[c]+=ds;
                  elementEntropy[e]+=ds;
                  totalCommunityEntropy+=ds;
                  totalVertexEntropy+=ds;
                  }
              }

          PS.println(cc+"c"+sep+"No.Elements"+sep+"Frac.Elements"+sep+"Entropy");
          for (int c=0; c<numberCommunities; c++){
              PS.println(c+sep+communityElementNumber[c]+sep+communityElementFraction[c]+sep+communityEntropy[c]);
          }


          // Do coverage
          int [] smallCommunityCount = new int [coverage.length];
          for (int e=0; e<this.numberElements; e++){
            int n=elementCommunityNumber[e]; if (n<smallCommunityCount.length) smallCommunityCount[n]++;
          }
          PS.println(cc+"n"+sep+"coverage[n]");
          int n=0;
          coverage[n]=numberElements-smallCommunityCount[n];
          PS.println(n+sep+coverage[n]);
          for (n=1; n<coverage.length; n++) {
              coverage[n]=coverage[n-1]-smallCommunityCount[n];
              PS.println(n+sep+coverage[n]);
          }



        }


    /** Prints basic numbers on Communities.
     * <br>Prints basic information, numbers only.  
     * See <code>printInformationNumbersLabel</code> for label.
     *@param cc comment string
     *@param sep separation string
     * @return String with basic numbers of VP
     * @see #informationNumbersLabel(java.lang.String, java.lang.String) for labels
     */
     public String  informationNumbers(String cc, String sep){ 
          return (cc+ valueString(numberElements)+sep+valueString(Q)+sep
                    + (quality==null?"not set":valueString(quality.getLambda()))
                    + sep+ valueString(numberCommunities));
        }

    /** Prints column labels for basic information.
     * <br>Used with <code>printInformationNumbersLabel</code>
     *@param cc comment string
     *@param sep separation string
     * @return String with label used for basic numbers of VP
     * @see #informationNumbers(java.lang.String, java.lang.String) for data lines
     */
     public String  informationNumbersLabel(String cc, String sep){ 
          return (cc+"Number of "+nameOfElements+sep+"Quality ("+sep+quality.Qdefinition(sep)+") "
                  +sep+"gamma"+sep+"Number of Communities");
        }


       /** Prints all elements of Community Matrix.
         * <br>Prints with headers and with Element labels.
         * @param PS a print stream for the output such as System.out
         * @param cc comment string
         * @param sep separation string
         */     
     public void  printCommunityMatrix(PrintStream PS, String cc, String sep){
     printCommunityMatrix(PS, cc, sep, true, true);}
     
    /** Prints Community Matrix.
     * @param PS a print stream for the output such as System.out
     * @param cc comment string used at start of each line
     * @param sep string separating items
     * @param headerOn true (false) if want header lines on (off)
     * @param ElementLabelsOn true (false) if want a column with the Element numbers on (off)
     * @deprecated use printCommunityMatrix or printCommunitityBipartiteGraph
         */
     public void  printCommunities(PrintStream PS, String cc, String sep, boolean headerOn, boolean ElementLabelsOn){
         printCommunityMatrix(PS,  cc,  sep, headerOn, ElementLabelsOn);
     }
    /** Prints non-zero elements of Community Matrix.
     * @param PS a print stream for the output such as System.out
     * @param cc comment string used at start of each line
     * @param sep string separating items
     * @param headerOn true (false) if want header lines on (off)
     * @param ElementLabelsOn true (false) if want a column with the Element numbers on (off)
         */
     public void  printCommunityMatrixSparse(PrintStream PS, String cc, String sep, boolean headerOn, boolean ElementLabelsOn){
         if (headerOn) {
             PS.println(cc+"Number of "+nameOfElements+sep+ numberElements+sep+"Number of Communities"+sep+ numberCommunities);
             if (ElementLabelsOn) {
                 PS.print(nameOfElements+sep);
                 for (int c=0; c<numberCommunities; c++) PS.print(getCommunityName(c)+sep);
                 PS.println();
             }
         }
         if (communityMatrix!=null){
             for (int s=0; s<numberElements; s++){
                 PS.print(cc);
                 if (ElementLabelsOn) PS.print(getElementName(s)+sep);
                 for (int c=0; c<numberCommunities; c++) PS.print(communityMatrix.get(s,c)+sep);
                 PS.println();
            }
         }
    }
    /** Prints all elements of Community Matrix.
     * @param PS a print stream for the output such as System.out
     * @param cc comment string used at start of each line
     * @param sep string separating items
     * @param headerOn true (false) if want header lines on (off)
     * @param ElementLabelsOn true (false) if want a column with the Element numbers on (off)
         */
     public void  printCommunityMatrix(PrintStream PS, String cc, String sep, boolean headerOn, boolean ElementLabelsOn){
         if (headerOn) {
             PS.println(cc+"Number of "+nameOfElements+sep+ numberElements+sep+"Number of Communities"+sep+ numberCommunities);
             if (ElementLabelsOn) {
                 PS.print(nameOfElements+sep);
                 for (int c=0; c<numberCommunities; c++) PS.print(getCommunityName(c)+sep);
                 PS.println();
             }
         }
         if (communityMatrix!=null){
             for (int s=0; s<numberElements; s++){
                 PS.print(cc);
                 if (ElementLabelsOn) PS.print(getElementName(s)+sep);
                 for (int c=0; c<numberCommunities; c++) PS.print(communityMatrix.get(s,c)+sep);
                 PS.println();
            }
         }
    }

    /** Prints list of community entries.
     * <p>This is in the format of an weighted edge list for a buipartite graph.
     * @param PS a print stream for the output such as System.out
     * @param cc comment string used at start of each line
     * @param sep string separating items
     * @param headerOn true (false) if want header lines on (off)
     * @param ElementLabelsOn true (false) if want a column with the Element numbers on (off)
     */
     public void  printCommunityBipartiteGraph(PrintStream PS, String cc, String sep, boolean headerOn, boolean ElementLabelsOn){
         if (headerOn) {
             PS.println(cc+"Number of "+nameOfElements+sep+ numberElements+sep+"Number of Communities"+sep+ numberCommunities);
             if (ElementLabelsOn) {
                 PS.println(nameOfElements+sep+"community"+sep+"weight");
             }
         }
         double f=-1;
         for (int s=0; s<numberElements; s++){
             PS.print(cc);
             for (int c=0; c<numberCommunities; c++) {
                 f =communityMatrix.get(s,c);
                 if (f>0) PS.println(getElementName(s)+sep+c+sep+f);
             }
         }
    }

     
     /**
      * Calculate Mutual Information of two vertex communities.
      * <p>Uses approach of Meila 2007.
      * <p>Requires that for all vertices v
      * <code>sum_c community getCommunityMembership(v, c) > TOLERANCE </code>
      * <P>Currently no weighting of vertices by their weight.
      * <p>Also joint probabilty not used to calculate marginal probabilities.
      * @param c1 community one
      * @param c2 community two
      * @param tg timgraph, can be null, only needed if weighting vertices by strength.
      * @return mutual information object, null if error. 
      */
     public static MutualInformation calcMutualInformation(Community c1, Community c2, timgraph tg){

         boolean inputWeightVertices=false;

         if (c1.getNumberElements() != c2.getNumberElements()) {
             System.err.println("*** In calcMutualInformation communities have different numbers of elements: "+c1.getNumberElements()+"   "+c2.getNumberElements());
             return null; //return -1;
         }
         if (!c1.elementsAreVertices()) {
             System.err.println("*** In calcMutualInformation first community must have vertices as community , but it has "+c1.getNameOfElements());
             return null; //return -2;
         }
         if (!c2.elementsAreVertices()) {
             System.err.println("*** In calcMutualInformation second community must have vertices as community , but it has "+c2.getNameOfElements());
             return null; //return -3;
         }
         
         if ((tg != null) && (c1.getNumberElements() != tg.getNumberVertices())) {
             System.err.println("*** In calcMutualInformation communities have different numbers of vertices from graph: "+c1.getNumberElements()+"   "+tg.getNumberVertices());
             return null; //return -4;
         }
         
         int n = c1.getNumberElements();
         
         double fstr=1.0/n;
         boolean weightVertices =false;
         if (inputWeightVertices && tg!=null && tg.isWeighted())  weightVertices=true;

         MutualInformation mi = new MutualInformation(c1.getNumberCommunities(),c2.getNumberCommunities(),c1.getName(),c2.getName());
         if (c1.communityName!=null) mi.setElementNames1(c1.communityName);
         if (c2.communityName!=null) mi.setElementNames2(c2.communityName);
         double c1mf=-1; // community 1 membership fraction
         double c2mf=-1; // community 2 membership fraction
         double totalStrength=n;
         if (weightVertices) totalStrength = tg.getTotalWeight();
         for (int v=0; v<n; v++){
             // first find and check community normalisation
             double c1norm=0;
             for (int ci1=0; ci1<c1.getNumberCommunities(); ci1++){
                 c1norm+=  c1.getCommunityMembership(v, ci1);
             }
             if (c1norm<TOLERANCE) throw new RuntimeException("*** Normalisation of vertex "+v+" in community 1 found to be zero or negative = "+c1norm);
             double c2norm=0;
             for (int ci2=0; ci2<c2.getNumberCommunities(); ci2++){
                     c2norm+= c2.getCommunityMembership(v, ci2);
//                     if (Double.isNaN(c2norm)) {
//                         System.err.println("*** Problem with NaN for c2norm v,ci2="+v+", "+ci2);
//                     }

                 }//eo ci2
             if (c2norm<TOLERANCE) throw new RuntimeException("*** Normalisation of vertex "+v+" in community 2 found to be zero or negative = "+c1norm);

             if (weightVertices) fstr = tg.getVertexOutStrength(v)/totalStrength;
             for (int ci1=0; ci1<c1.getNumberCommunities(); ci1++){
                 c1mf = c1.getCommunityMembership(v, ci1)/c1norm;
//                 if ((ci1==52) && (c1mf>0)){
//                     System.out.println("Community "+ci1+" - "+c1.getCommunityName(ci1));
//                 }
                 //joint probability not used to calculate marginal probabilities.
                 if (c1mf>0) mi.increaseMarginalProbabilityOneQuick(ci1, c1mf*fstr);
                 else if (ci1>0) continue; // if c1mf1==0 we must still update marginalProb2 when ci1==0
                 for (int ci2=0; ci2<c2.getNumberCommunities(); ci2++){
                     c2mf = c2.getCommunityMembership(v, ci2)/c2norm;
                     if (c2mf==0) continue;
                     if (c1mf>0) mi.increaseJointProbabilityQuick(ci1, ci2, c1mf*c2mf*fstr);
                     if (ci1==0) mi.increaseMarginalProbabilityTwoQuick(ci2, c2mf*fstr); // count only once
//                     if (Double.isNaN(mi.getJointProbabilityQuick(ci1, ci2) )) {
//                         System.err.println("*** Problem with NaN for v,c1,c2="+v+", "+ci1+", "+ci2);
//                     }
                 }//eo ci2
             }//eo ci1
         } //eo v

//         mi.printJointProbability(System.out,  " : ", true, true);
//         int r=mi.checkNormalisations();
//         if (r!=0){
//             String s="Normalisation of probability failure: ";
//            if ((r &1)>0) s="marginal probability of community one, ";
//            if ((r &2)>0) s="marginal probability of community two, ";
//            if ((r &4)>0) s="joint probability, ";
//            throw new RuntimeException(s);
//         }
         if (mi.checkConsistentcy()<0) throw new RuntimeException("Consistency Problem with Mutual Information");
        
         return mi;
     }
     // uses approach of Meila 2007
     /**
      * Unfinished routine
      * @param c1
      * @param c2
      * @param tg
      * @return some integer
      * @deprecated TODO UNFINISED routine
      */
     static int calcMutualInformationGeneralTypes(Community c1, Community c2, timgraph tg){
         if (c1.getNumberElements() != c2.getNumberElements()) {
             System.err.println("*** In calcMutualInformation communities have different numbers of elements: "+c1.getNumberElements()+"   "+c2.getNumberElements());
             return -1;
         }
         if (!c1.elementsAreVerticesOrEdges()) {
             System.err.println("*** In calcMutualInformation first community must have vertices or edges as community , but it has "+c1.getNameOfElements());
             return -2;
         }
         boolean c1ElementsAreVertices= c1.elementsAreVertices();
         
         if (!c2.elementsAreVerticesOrEdges()) {
             System.err.println("*** In calcMutualInformation second community must have vertices or edges as community , but it has "+c2.getNameOfElements());
             return -3;
         }
         boolean c2ElementsAreVertices= c2.elementsAreVertices();
         
         if (c1.getNumberElements() != tg.getMaximumVertices()) {
             System.err.println("*** In calcMutualInformation communities have different numbers of elements from graph: "+c1.getNumberElements()+"   "+c2.getNumberElements());
             return -1;
         }
         
         
         return 0;
     }
     
     public boolean elementsAreVertices(){
         return (getNameOfElements().equals(Community.verticesName));
     }
     
     public boolean elementsAreEdges(){
         return (getNameOfElements().equals(Community.edgesName));
     }
     
     public boolean elementsAreVerticesOrEdges(){
         return (elementsAreVertices() || elementsAreEdges());
     }
     
     
     
     public String getName(){return name;}
     public String getNameOfElements(){return nameOfElements;}
     public void setName(String newName){name=newName;}
     public void setElementNameVertices(){nameOfElements=Community.verticesName;}
     public void setElementNameEdges(){nameOfElements=Community.edgesName;}
     
     protected String valueString(int i){ return ((i==UNSET)?"not set": Integer.toString(i));}
     protected String valueString(double d){ return ((Math.abs(d/DUNSET-1.0)<1e-6)?"not set": Double.toString(d));}

}
