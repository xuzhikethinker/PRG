/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import DataAnalysis.IntegerSequence;
import TimGraph.io.FileInput;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Iterator;


import java.io.PrintStream;
import java.util.Map;
import java.util.Random; //p524 Schildt


/**
 * Element partition of a graph.
 * <br>The Elements can be those of a line graph and thus this can be an edge partition.
 * <br>Contains greedy improvement algorithm.
 * @author time
 */
public class Partition extends Community {

    private Random Rnd = new Random();
    
    /**
     * ElementPartition label of given Element.
     * <code>communityOfElement[v]</code> is label of Element v.
     * <br>Valid labels are positive integers or zero, but need not be a contiguous sequence.
     */
    protected int communityOfElement[]; // the label of the community of given Element
    
    
    /**
     * Next integer to be used as a community label.
     */
    protected int nextCommunityLabel = UNSET;
    
    /**
     * Set of labels being used for communities.
     * <br>Valid labels are positive integers or zero, but need not be a contiguous sequence.
     */
    protected TreeSet<Integer> communityLabels;
    

    /**
     * Array of the index of elements within each community.
     * <p>The local label is the index within the community, starting from zero
     * and going up to the number in the community minus one.  The order is
     * in the order in which elements appear in <code>communityOfElements</code>.
     */
    int [] localLabel ;

    /**
     * This is true if statistics for current community have been calculated. 
     */
    boolean statisticsCalculated = false;
    
    // used for analysis
    protected    int[] numberElementsArray;// = null;
    protected    IntegerSequence communityStatistics;


    
    
 /**
 * Empty Constructor needed for extensions of ElementPartition.
 */
public Partition(){name="generalP";
    }


/**
 * Constructor used to prepare a community.
 * @param nv number of Elements
 */
public Partition(int nv){
    name="generalP";
    numberElements=nv;
    communityOfElement = new int[numberElements];
    }
/**
 * Constructor used to prepare a community.
 * @param newname used to set name of partition
 * @param nv number of Elements
 */
public Partition(String newname, int nv){
    name=newname;
    numberElements=nv;
    communityOfElement = new int[numberElements];
    }
/**
 * Constructor used to store basic information about a community.
 * <br>This does not necessarily provide any ability to recalculate.  
 * It merely stores basic information about a community structure e.g. the best one.
 * @param q quality
 * @param n number of Elements
 * @param cov vector with community labels, i.e. <code>cov[s]</code> is community of Element s.
 */
public Partition(double q, int n, int [] cov){
    name="generalP"+quality.QdefinitionShortString();
    Q=q;
    numberElements=n;
    communityOfElement = new int[numberElements];
    setCommunity(cov);
//        nextCommunityLabel = numberElements;
//        statisticsCalculated = false;
//        numberCommunities=numberElements;
    }


/**
 * Set number of elements and initialise relevant quantities.
 * @param n number of elements 
 */
protected void setNumberElements(int n){
    numberElements=n;
    communityOfElement = new int[numberElements];
}
    
   /**
     * Sets community to be equal to index.
     * <br>Each Element starts in its own community which are labelled by the Element index.
     */
    public void individualCommunity() {
        communityLabels = new TreeSet();
        communityOfElement = new int[numberElements];
        for (int s = 0; s < numberElements; s++) {
            communityOfElement[s] = s;
            communityLabels.add(s);
        }
        nextCommunityLabel = numberElements;
        statisticsCalculated = false;
        numberCommunities=numberElements;
    }

   /**
     * Sets the community of each Element in a proscribed manner.
     * <br>If n is negative then 
    * each Element starts in its own community which are labelled by the Element index.
    * If n is one or zero then all Elements are placed in community zero.
    * Otherwise each Element is assigend to be in random community numbered between 0 and (n-1) inclusive.
    * @param n number of communities to set.
     */
    public void setCommunity(int n) {
        boolean unique=false;
        boolean random=false;
        if (n<0) unique=true;
        else if(n>1) random=true; 
        communityLabels = new TreeSet();
        communityOfElement = new int[numberElements];
        int c=0;
        for (int s = 0; s < numberElements; s++) {
            if (random) c=Rnd.nextInt(n);
            if (unique) c=s;
            communityOfElement[s] = c;
            communityLabels.add(c);
        }
        nextCommunityLabel = communityLabels.last()+1;
        statisticsCalculated = false;
        numberCommunities=communityLabels.size();
    }

        /**
     * Sets all to be in community zero.
     */
    public void oneCommunity() {
        communityLabels = new TreeSet();
        communityOfElement = new int[numberElements];
        communityLabels.add(0);
        for (int s = 0; s < numberElements; s++) {
            communityOfElement[s] = 0;
        }
        nextCommunityLabel = numberElements;
        statisticsCalculated = false;
        numberCommunities=0;
    }



    /**
     * Tests to see if any community labels are negative.
     */
    public int hasNegativeCommunityLabels(){
        for (int s=0; s<numberElements;s++) if (communityOfElement[s]<0) return s;
        return -1;
    }


    /**
     * Relabels the communities.
     * <br>The new labels are integers from the sequence from 0 to (numberCommunities-1) inclusive.
     * Will also recalulate the set of community labels and set the number of communities.
     * @param ignoreNegativeLabels if true will not relabel negative community labels.
     */
    public void relabelCommunities(boolean ignoreNegativeLabels){
        // This map has key of the old community labels (integers but not necessarily consecutive)
        // while the values are the new labels, which must be in [0,numberCommunities-1]
        TreeMap<Integer,Integer> cl2pg = new TreeMap<Integer,Integer> ();
        communityLabels = new TreeSet<Integer> ();
        int cs=-1;
        Integer nl = cs;
        int newCommunityLabel =0;
        for (int s=0; s<numberElements;s++){
            cs=communityOfElement[s];
            if (ignoreNegativeLabels && cs<0) continue;
            nl =cl2pg.get(cs);
            if (nl ==null){
                nl=newCommunityLabel++; 
                cl2pg.put(cs,nl);
                communityLabels.add(nl);
            }
            communityOfElement[s] = nl;
        }
        numberCommunities=newCommunityLabel;
    }
    
    /**
     * Recalculates the list of non-empty community labels.
     *<br>This resets <code>communityLabel</code> and sets the number of communities.
     * <br>Negative community labels are ignored.
     * @return number of distinct communities
     */
    public int recalculateCommunityLabels(){
        communityLabels = new TreeSet<Integer> ();
        for (int s=0; s<numberElements; s++) {
            int c=communityOfElement[s];
            if (c>=0) communityLabels.add(c);
        }               
        numberCommunities = communityLabels.size();
        return numberCommunities;
    }
    /**
     * Calculate quality of graph for current community. 
     * @return quality of graph for current community. 
     */
    @Override
    public double calcQuality(){Q= quality.calc(communityOfElement); return Q;}
    
    /**
     * Contribution to Quality from Element s if it is in community c.
     * @param s Element 
     * @param c community label it is to be added to
     * @return Contribution to Quality 
     */
    public double delta(int s, int c){return quality.delta(s, c, communityOfElement);}

    public int getNumberElementsInCommunityFast(int c){return numberElementsArray[c];}
    public int getNumberElementsInCommunity(int c){
        if (numberElementsArray==null) analyse();
        return numberElementsArray[c];
    }


    /**
     * Number of distinct communities,
     * <br>NOTE does not check that this is current.  but will recalculate using
     * <code>recalculateCommunityLabels()</code> if currently less than one.
     * @return number of distinct communities
     */
    public int getNumberOfCommunities(){
        if (numberCommunities<1) recalculateCommunityLabels();
        return numberCommunities ;
    }
    /**
     * Gives a label of an empty community.
     * @return label of empty community.
     */
    public int getEmptyCommunity(){
        return nextCommunityLabel++;
    }
    /**
     * Gives a reference to the partition into communities.
     * @return community of Element.
     */
    public int [] getCommunity(){
        return this.communityOfElement;
    }
    /**
     * Gives a reference to the partition into communities.
     * @param v index of Element
     * @return community label of Element v.
     */
    public int getCommunity(int v){
        return communityOfElement[v];
    }

    /**
     * Gives size of communities of element v.
     * @param v index of Element
     * @return size of community of Element v.
     */
    public int getCommunitySize(int v){
        return getNumberElementsInCommunity(communityOfElement[v]);
    }

   /**
     * Returns the fraction of element v in community c.
     * <p>Since this is a partition this is 1 or zero.
     * @param e index of element
     * @param c index of community
     * @return 1 if v is in community c, otherwise its returns zero.
     */
    @Override
    public double getCommunityMembership(int e, int c){
    return ((communityOfElement[e]==c)?1:0); }


    
    /**
     * Sets the community vector.
     * <br>Input vector must have at least numberElements entries.
     * @param cov vector with community labels, i.e. <code>cov[s]</code> is community of Element s.
     */
    public void setCommunity(int [] cov){
        for (int s=0; s<numberElements;s++) communityOfElement[s]=cov[s];
    }
    
    /**
     * Sets the community of a Element.
     * @param s Element index
     * @param c community label
     */
    public void setCommunity(int s, int c){
        communityOfElement[s]=c;
    }

    /**
     * Sets the local label of each element.
     * <p>The local label is the index within the community, starting from zero
     * and going up to the number in the community minus one.  The order is
     * in the order in which elements appear in <code>communityOfElements</code>.
     */
    public void setCommunityLocalLabel(){
        int [] nextLocalLabel = new int [this.numberCommunities]; // should be initialised to zero
        localLabel = new int [this.numberElements];
        for (int e=0; e<numberElements; e++) {
            int c =communityOfElement[e];
            localLabel[e]=(nextLocalLabel[c]++);
        }
    }

    /**
     * Gets the local label of each element.
     * <p>The local label is the index within the community, starting from zero
     * and going up to the number in the community minus one.  The order is
     * in the order in which elements appear in <code>communityOfElements</code>.
     * @param e global index of element
     * @return local index for element within its community
     */
    public int getCommunityLocalLabel(int e){
        return localLabel[e];
    }

    /**
    * Calculates the statistics on the community structure.
    * @return communityStatistics copy of these statistics
    */
   public IntegerSequence calculateCommunityStatistics(){
       int maxmu=3;
       int maxF=3;
       int cm=7;
       if (numberElementsArray==null) analyse();
       communityStatistics= new IntegerSequence(maxmu, maxF, cm);
       communityStatistics.calcStats(numberElementsArray);
       return communityStatistics;
   }

   /**
    * Statistics on the community structure.
    * @return communityStatistics
    */
   public IntegerSequence getCommunityStatistics(){
       return communityStatistics;
   }

      /**
    * Finds Entropy of community distribution
    * @return S
    */
   public double getEntropy(){
       if (this.communityStatistics==null) calculateCommunityStatistics();
       return communityStatistics.getEntropy();
   }

   /**
    * Finds F[2] homgeneity measure of community distribution
    * @return F[2]
    */
   public double getF2(){
       if (this.communityStatistics==null) calculateCommunityStatistics();
       return communityStatistics.getF(2);
   }

   /**
    * Sets the number of communities.
    * <p>Number of communities is set to be one larger than the largest 
    * community label found. Tests to see if all labels are 0 or more.
    * However there is no guarantee labels run from 
    * continuously from zero. 
    * @return true (false) if any community label is less than 0
    */
   public boolean setNumberCommunities(){
     numberCommunities=-1;
     int sc=-1;
     boolean fail=false;
     for (int s=0; s<numberElements; s++){
            sc = communityOfElement[s];
            if (numberCommunities<sc) numberCommunities=sc;
            if (sc<0) fail=true;
         }//eo for s
     numberCommunities++;
     return fail;
   }

    /**
     * Basic Community Analysis.
     * <p>Sets arrays defined in class indexed by community number, null if inappropriate.
     * These are initialised here.
     */
    public void analyse(){
        int sc=-1; // community of source vertex
        numberElementsArray = new  int[numberCommunities];
        for (int s=0; s<numberElements; s++){
            sc = communityOfElement[s];
            numberElementsArray[sc]++;
         }//eo for s
    }

   /** Prints statistics on a partition.
     * <p>Calculates them if needed
     * @param PS a print stream for the output such as System.out
     * @param cc comment string used at start of each header or info line
     * @param sep string separating items
     * @param infoOn true (false) if want first row to have basic information on partition
     * @param headerOn true (false) if want header row on (off)
         */
    public void printStatistics(PrintStream PS, String cc, String sep, boolean infoOn, boolean headerOn){
        analyse();
        if (infoOn) {
             PS.println(cc+"Number of "+nameOfElements+sep+ numberElements+sep+"Number of Communities"+sep+ numberCommunities);
             if (communityStatistics==null) this.calculateCommunityStatistics();
             PS.println(communityStatistics.labelString(sep));
             PS.println(communityStatistics.toString(sep));
        }
        if (headerOn) {
             PS.print(cc+"Community"+sep+"N_C");
         }
        for (int c=0; c<this.numberCommunities; c++){
           PS.println(c+sep+numberElementsArray[c]);
         }//eo for s
    }


       /**
         * Read in partition of a graph from a file.
         * <p>The input is assumed to be in the format of a
         * a pajek <tt>.clu</tt> file.  That is first line is the
         * number of vertices and then a single column listing the
         * community number for each vertex in numerical order.
         * Number of communities is set to be one larger than the largest
         * community label found. However unless relabelOn is selected,
         * there is no guarantee labels run from continuously from zero.
         * <p>Reset the number of elements in partition if needed.
         * @param filename full file name containing partition
         * @param relabelOn If true relabels communities to run from 0 to (numberCommunities-1)
         *@see TimGraph.io.FileInput#readIntIndexLabelList(java.lang.String, int, int, java.util.ArrayList, java.util.ArrayList, boolean, java.lang.String, int)
         */
   public int readPajekPartition(String filename, boolean relabelOn){
       String commentLine="*";
       int columnIndex=-1;
       int columnLabel=1;
       boolean headerOn=true;
       return readIntPartition(filename, columnIndex, columnLabel,
           headerOn, commentLine, relabelOn);
   }
           /**
         * Read in partition of a graph from a file.
         * <p>Reset the number of elements in partition if needed.
         * <p>Number of communities is set to be one larger than the largest
         * community label found. However unless relabelOn is selected,
         * there is no guarantee labels run from continuously from zero.
         * No comments lines ignored.
         * @param filename full file name containing partition
         * @param columnIndex column with index, columns counted from 1
         * @param columnLabel column with community label, counting from 1.  If negative no weights read
         * @param headerOn First line ignored if true.
         * @param relabelOn If true relabels communities to run from 0 to (numberCommunities-1)
         *@see TimGraph.io.FileInput#readIntIndexLabelList(java.lang.String, int, int, java.util.ArrayList, java.util.ArrayList, boolean, java.lang.String, int)
         */
   public int readIntPartition(String filename, int columnIndex, int columnLabel,
           boolean headerOn,  boolean relabelOn){
       String commentLine="";
       return readIntPartition(filename, columnIndex, columnLabel,
           headerOn, commentLine, relabelOn);
   }
       /**
         * Read in partition of a graph from a file.
         * <p>Reset the number of elements in partition if needed.
         * <p>Number of communities is set to be one larger than the largest
         * community label found. However unless relabelOn is selected,
         * there is no guarantee labels run from continuously from zero.
         * Comment lines ignored as defined by input string.
         * @param filename full file name containing partition 
         * @param columnIndex column with index, columns counted from 1
         * @param columnLabel column with community label, counting from 1.  If negative no weights read
         * @param headerOn First line ignored if true.
         * @param commentLine if not null, any line starting with this string will be ignored.
         * @param relabelOn If true relabels communities to run from 0 to (numberCommunities-1)
         *@see TimGraph.io.FileInput#readIntIndexLabelList(java.lang.String, int, int, java.util.ArrayList, java.util.ArrayList, boolean, java.lang.String, int)
         */
   public int readIntPartition(String filename, int columnIndex, int columnLabel, 
           boolean headerOn, String commentLine, boolean relabelOn){
      //FileInput fi = new FileInput(infoLevel);
      ArrayList<Integer> indexL = new ArrayList();
      ArrayList<Integer> labelL = new ArrayList();
      FileInput.readIntIndexLabelList(filename, columnIndex,  columnLabel, indexL, labelL, headerOn, commentLine,infoLevel);
      //initialise(lineGraphtg);
      //int [] cov = new int[lineGraphtg.getNumberElements()];
      //for (int v=0; v<lineGraphtg.getNumberElements();v++) cov[v]=ElementPartition.UNSET;
      if (indexL.size()!=labelL.size()) 
      { System.err.println("Two lists have different sizes");
        return -1;
      }

      if ((communityOfElement== null) || (communityOfElement.length != indexL.size())){
          System.out.println("!!! Number of elements is being reset to number found "+indexL.size());
          numberElements=indexL.size();
          communityOfElement = new int[numberElements];
      }

      Iterator ii = indexL.iterator();
      Iterator il = labelL.iterator();
      int index=-1;
      int label=-1;
      int maxLabel=-1;
      while (ii.hasNext()){
          index = ((Integer) ii.next()); //Integer.parseInt((StringInteger.p)ii.next());
          if ((index<0) || (index>=communityOfElement.length)) throw new RuntimeException("In index of element incorrect, value was "+index);
          label = ((Integer) il.next());//Integer.parseInt((String)il.next());
          //lineGraphtg.setElementNumberQuick(index, label);
          setCommunity(index, label);
          if (label>maxLabel) maxLabel=label;
      }
      numberCommunities=maxLabel+1;
      boolean ignoreNegativeLabels=false;
      if (relabelOn) this.relabelCommunities(ignoreNegativeLabels);
      return indexL.size();
   }

       /**
         * Read in partition of a graph from a file.
         * <p>Entries read as strings even though these are converted to numbers.
         * Automatic relabelling to ensure community numbers run
         * 0 to (numberCommunities-1).
         * @param filename full file name containing partition
         * @param columnIndex column with index, counting from 1
         * @param columnLabel column with label, counting from 1.  If negative no weights read
         * @param headerOn First line ignored if true.
         * @param nameToIndex if not null, index list is mapped to integer vertex index with this map
    */
   public int readStringPartition(String filename, int columnIndex, int columnLabel, 
           boolean headerOn, Map<String,Integer> nameToIndex){
      FileInput fi = new FileInput(infoLevel);
      ArrayList<String> indexL = new ArrayList();
      ArrayList<String> labelL = new ArrayList();
      FileInput.readStringIndexLabelList(filename, columnIndex,  columnLabel, indexL, labelL, headerOn);
      return setUpPartition(indexL, labelL, nameToIndex);
    }

    /**
     * Read in partition from a file.
     * <p>Entries read as strings even though these are converted to numbers.
     * Automatic relabelling to ensure community numbers run
     * 0 to (numberCommunities-1).
     * The first entry in the arrays columnReadList and columnLabelList is for
     * the <em>index</em> of the objects in the partitioned set.
     * The second entry in the arrays columnReadList
     * and columnLabelList is for the <em>community label</em>.
     * @param fullFileNamefilename
     * @param cc comment character
     * @param columnReadList array of integers giving the columns to be read in, null if first non-comment line is to be header row
     * @param columnLabelList array of strings giving headers to search for.
     * @param forceLowerCase
     * @param infoOn
     * @return number of
     */
   public int readStringPartition(String fullFileName,
           String cc, int [] columnReadList, String [] columnLabelList,
           boolean forceLowerCase, boolean infoOn){
//      FileInput fi = new FileInput(infoLevel);
      ArrayList<ArrayList<String>> dataList = FileInput.readStringColumnsFromFile(fullFileName,
            cc, columnReadList, columnLabelList,
            forceLowerCase, infoOn);
      ArrayList<String> indexL = dataList.get(0);
      ArrayList<String> labelL = dataList.get(1);
      return setUpPartition(indexL, labelL);
    }

   /**
    * Set up partition from lists of indices and labels.
    * Communities are relabelled to run from zero to number of communities 
    * minus one.
    * @param setIndexList set element index as strings, not necessarily in order
    * @param communityLabelList labels of community assignments as strings
    * @param indexAsName true if want the index list to be matched to vertex names
    * @return number of communities, negative if error
    */
   public int setUpPartition(ArrayList<String> setIndexList, 
                             ArrayList<String> communityLabelList){
       Map<String,Integer> nameToIndex =null; 
       return setUpPartition(setIndexList, communityLabelList,nameToIndex);   
   }
   /**
    * Set up partition from lists of indices and labels.
    * Communities are relabelled to run from zero to number of communities 
    * minus one.
    * @param setIndexList set element index as strings, not necessarily in order
    * @param communityLabelList labels of community assignments as strings
    * @param nameToIndex if not null, index list is mapped to integer vertex index with this map
    * @return number of communities, negative if error
    */
   public int setUpPartition(ArrayList<String> setIndexList, 
                             ArrayList<String> communityLabelList,
                             Map<String,Integer> nameToIndex){
      if (setIndexList.size()!=communityLabelList.size())
      { System.err.println("Two lists have different sizes");
        return -1;
      }

      boolean indexAsName=false;
      if (nameToIndex!=null)indexAsName=true;
      // each set element must appear once and only once -this is a partition
      numberElements = setIndexList.size(); 
      Iterator ii = setIndexList.iterator();
      Iterator il = communityLabelList.iterator();
      TreeMap<String,Integer> communityLabel2communityIndex = new TreeMap<String,Integer> ();
      communityLabels = new TreeSet<Integer> ();
      communityOfElement = new int[numberElements];
      Integer index=-1;
      String label="";
      Integer communityIndex = -1;
      nextCommunityLabel =0;
      String indexString;
      while (ii.hasNext()){
          indexString=(String)ii.next();
          if (indexAsName){
              try{index = nameToIndex.get(indexString);
                  if (index==null) throw new RuntimeException("name "+indexString+" not in map");
              }
              catch (RuntimeException e){
               throw new RuntimeException("Set element name "+indexString+" not recognised, "+e);
              }
          }
          else{
              try{index = Integer.parseInt(indexString);}
              catch (RuntimeException e){
               throw new RuntimeException("Set elements must be given as integer indices, not index "+indexString+", "+e);
              }
          }
          if ((index<0) || (index>=numberElements)) 
              throw new RuntimeException("In index of element incorrect, must be between 0 and "
                  +numberElements+", value was "+index);
          label = (String)il.next();
          communityIndex =communityLabel2communityIndex.get(label);
          if (communityIndex ==null){
                communityIndex=nextCommunityLabel++;
                communityLabel2communityIndex.put(label,communityIndex);
                communityLabels.add(communityIndex);
            }
            communityOfElement[index] = communityIndex;
        }
        numberCommunities=nextCommunityLabel;
      return numberCommunities;
   }

    
    


   /** Prints Partition as a list.
     * <br>Equivalent to a bipartite graph representation.
     * <br>Note for a pajek cluster vector use
     * <code>printCommunities(PS, cc, sep, false, false)</code>
     * and use a print stream pointing to a file with <code>.clu</code> extension.
     *@param PS a print stream for the output such as System.out
     *@param cc comment string used at start of each line
     *@param sep string separating items
     * @param headerOn true (false) if want header lines on (off)
     * @param ElementLabelsOn true (false) if want a column with the Element numbers on (off)
         */
    @Override
     public void  printCommunityBipartiteGraph(PrintStream PS, String cc, String sep, boolean headerOn, boolean ElementLabelsOn){
         if (headerOn) {
             PS.println(cc+"Number of "+nameOfElements+sep+ numberElements+sep+"Number of Communities"+sep+ numberCommunities);
             PS.print(cc);
             if (ElementLabelsOn) PS.print(nameOfElements+sep);
             PS.println("Label");
         }
         for (int s=0; s<numberElements; s++){
             PS.print(cc);
             if (ElementLabelsOn) PS.print(s+sep);
             PS.println(communityOfElement[s]);
         }
    }   

     
}
