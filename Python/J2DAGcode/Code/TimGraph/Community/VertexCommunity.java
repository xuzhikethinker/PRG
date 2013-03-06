/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.Community;

import TimGraph.io.FileInput;
import TimGraph.timgraph;
//import TimGraph.Community.VertexPartition;

//import java.util.TreeMap;
//import java.util.TreeSet;
//import java.util.ArrayList;
//import java.util.Iterator;


import TimUtilities.StringUtilities.Filters.StringFilter;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * General vertex community structure.
 * <p>Not limited to partitions of sets.  
 * The vertices can be those of a line graph and thus this can be an edge community.
 * @author time
 */
public class VertexCommunity extends Community {
    
    


 /**
 * Empty Constructor needed for extensions of community .
 */
public VertexCommunity(){setDefaultNames();}

/**
 * Bare Constructor used to prepare a community.
 * @param inputname name of community
 */
public VertexCommunity(String inputname){
    name=inputname;
    nameOfElements = "vertices";
    }


    /**
     * Initialises a vertex community in terms of vertices of given graph.
     * <br>If the graph is directed then out degree or strength
     * is used to weight the vertices at a vertex.
     * @param tg timgraph whose vertices are forming the communities
     */
    public VertexCommunity (timgraph tg){
        nameOfElements = "vertices";
        initialiseFromGraph(tg);
    }

    /**
     * Defines a full vertex community for given graph.
     * <p>Any vertex in graph but not in list given is given a unique number
     * based on its index.
     * @param tg graph whose vertices are to assigned a community
     * @param vertexToCommunity List of communities associated with each vertex
     */
    public VertexCommunity (timgraph tg, ArrayList<ArrayList<Integer> > vertexToCommunity){
        nameOfElements = "vertices";
        initialiseFromGraph(tg);
        setUp( vertexToCommunity);
    }


    public void initialiseFromGraph(timgraph tg){
        name = tg.inputName.getNameRoot();
        nameOfElements = "vertices";
        graph =tg;

    }

    /**
     * Constructs a vertex community from an edge or vertex partition.
     * <br>If the graph is directed then out degree or strength is used to weight the vertices at a vertex.
     * @param tg timgraph whose vertex partition is given
     * @param p  partition of the graph.
     * @param qdef selects modularity definition to use
     * @param newlambda scaling factor for null model in quality function
     * @param qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory   
     */
    public VertexCommunity (timgraph tg, Partition p, int qdef, int qualityType, double newlambda){
        setDefaultNames();
        initialiseGraph(tg, qdef, qualityType, newlambda);
        if (tg.getNumberVertices()==p.getNumberElements()) initialiseFromVertexPartition(p);
        else if (tg.getNumberStubs()/2==p.getNumberElements()) initialiseFromEdgePartition(tg,p);
        else throw new RuntimeException("*** Community constructor: Neither number of vertices "+tg.getNumberVertices()+" nor the number of edges "+tg.getNumberStubs()/2+" matches number of elements in partition "+p.getNumberElements());
    }
    
    /**
     * Sets up default names for this partition
     */
    private void setDefaultNames() {
        name = "generalVP";
        nameOfElements = "vertices";
    }

          
 /**
 * Constructs a vertex community from a vertex partition.
 * @param vp vertex partitions of the graph.
 */
    private void initialiseFromVertexPartition(Partition vp){
        name=vp.getName()+"vp";
        numberCommunities = vp.getNumberOfCommunities();
        numberElements=vp.getNumberElements();
        communityMatrix = new SparseDoubleMatrix2D(numberElements,numberCommunities);
        for (int v=0; v<numberElements; v++){
            //for (int c=0; c<numberCommunities; c++) communityMatrix[v][c]=0;
            communityMatrix.set(v,vp.getCommunity(v),1.0);
        }
    }

    
 /**
 * Constructs an vertex community from an edge partition.
 * <p>The partitions has elements corresponding to edges of global label
 * <tt>(e/2)</tt> and <tt>(e/2+1)</tt>.
 * <br>If the graph is weighted then out degree or strength is used to weight the 
 * contribution from the community of each edge incident at a vertex.
 * @param tg timgraph whose vertex partition is given
 * @param ep edge partition of the graph.
 */
    private void initialiseFromEdgePartition (timgraph tg, Partition ep){
        name=ep.getName()+"ep2vc";
        graph=tg;
        numberCommunities = ep.getNumberOfCommunities();
        numberElements=tg.getNumberVertices();
        communityMatrix = new SparseDoubleMatrix2D(numberElements,numberCommunities);
        double s=-1;
        int e=-1;
        int ce=-1;
        double f=-1;
        for (int v=0; v<numberElements; v++){
            if (tg.isWeighted()) s=tg.getVertexOutStrength(v); else s=tg.getVertexOutDegree(v);
            //for (int c=0; c<numberCommunities; c++) communityMatrix[v][c]=0;
            for (int ei=0; ei<tg.getVertexOutDegree(v); ei++) {
                e = tg.getStub(v, ei);
                ce = ep.getCommunity(e/2);
                f =communityMatrix.get(v,ce)+(tg.isWeighted()?tg.getEdgeWeight(e):1)/s;
                communityMatrix.set(v,ce,f);
            } 
        }
    }





    /**
     * Number of Vertices.
     * <p>This is the number of elements in a vertex partition.
     * @return number of Vertices
     */
    public int getNumberVertices(){
        return numberElements ;
    }



    /**
     * Read in vertex community from a file.
     * <p>If file ends in <tt>BVNLS.dat</tt> treats it as a vertex neighbour list.  
     * Here each line has a vertex followed by the communities it is in (separated by white space).  
     * It is assumed that each community has equal weight.
     * <p>If file ends in <tt>vcis</tt> it is treated as a list of vertex properties.  
     * The first column is the one with the vertex label, the second is its community and the eighth
     * gives the fractional membership of that community.
     * @param fullFileName full name of file including directories
     * @param vertexNameToIndex a map of vertex names to index (constructed if not given)
     * @param forceNormalisation forces the community membership vector of each vertex to be normalised.
     * @param checkNormalisation checks that the community membership vector of each vertex sums to one.
     */
    public void readVertexCommunity(String fullFileName,
            TreeMap<String,Integer>vertexNameToIndex,
            boolean forceNormalisation, boolean checkNormalisation){

        boolean  forceLowerCase =false;
        boolean checkBipartite=false;
        int sampleFrequency=1;
        if (fullFileName.endsWith("BVNLS.dat")){
            readStringVertexCommunitiesFile(vertexNameToIndex, 
            fullFileName, 
            forceLowerCase, checkBipartite, sampleFrequency,
            forceNormalisation, checkNormalisation);
        }
        
        if (fullFileName.endsWith("vcis")){
            int columnVertex=1;
            int columnCommunity=2;
            int columnWeight=8; // this should be the fractional S in C column
            boolean headerOn=true;
            readVertexCommunityListFile(vertexNameToIndex, 
            fullFileName, 
            columnVertex, columnCommunity, columnWeight,
            forceNormalisation, checkNormalisation, headerOn);
        }        
    }

    
    /**
      * Reads in list of communities for each vertex, both labelled by strings, from file.
      * <p>Each line has entries separated by whitespace.
      * First entry is the vertex and the remaining entries on that line are its communities.
      * Graph must be a bipartite structure with every vertex appearing on one and only one line.  
      * Each vertex must have at least one community.
      * The weight is the 1/(number target vertices on line).  
      * <p>The vertices are coordinated by name with a list provided by <tt>vertexNameToIndex</tt>.
      * @param vertexNameToIndex a map of vertex names to index (constructed if not given)
      * @param fullFileName used to determine directory and name of file.
      * @param  forceLowerCase force all strings to be lower case
      * @param checkBipartite forces a check of bipartite nature, ignored if not read as bipartite
      * @param sampleFrequency number of lines to skip, 1 or less and all are taken
      * @param forceNormalisation true if wnat to force normalisation
      * @param checkNormalisation true if want to check normalisation
      */
    public void readStringVertexCommunitiesFile(TreeMap<String,Integer>vertexNameToIndex, 
            String fullFileName, 
            boolean forceLowerCase,
            boolean checkBipartite, int sampleFrequency,
            boolean forceNormalisation, boolean checkNormalisation){
        
      System.out.println("Starting to read vertex community using a vertex communities list with string vertex labels from " + fullFileName);

        
      /**
        * List of vertices found 
        */ 
      TreeSet<String> vertexLL = new TreeSet();
       /**
         * List of the communities for the vertices
         */
      TreeSet<String> communityLL = new TreeSet();
      /**
        * List of vertex then community of each edge
        */
      ArrayList<String> edgeLL = new ArrayList();
      
      /**
        * Weights list where weight is the 1/(number target vertices on line)
        * <p>Note that these are given for each edge once.
        */
      DoubleArrayList edgeWeightLL = new DoubleArrayList();  
      
      //Null StringFilters means no test is performed on vertices.  
        StringFilter stringFilterSource = null;
        StringFilter stringFilterTarget = null;
        TreeSet<String> filterL = null;
        
        FileInput.readStringVertexNeighbourFile(fullFileName,
                vertexLL, communityLL,
                edgeLL,
                edgeWeightLL,
                forceLowerCase, checkBipartite,
                sampleFrequency,
                stringFilterSource, stringFilterTarget,
                filterL, (infoLevel>1));
        
       setUp(vertexNameToIndex, 
            vertexLL, communityLL, 
            edgeLL, edgeWeightLL,
            forceNormalisation, checkNormalisation);

        
    }

      /**
      * Reads in a file with vertex name, community name, fractional membership.
      * <p>This version has no initial header line.
      * @see #readVertexCommunityListFile(java.util.TreeMap, java.lang.String, int, int, int, boolean, boolean, boolean)
     */
    public void readVertexCommunityListFile(TreeMap<String,Integer>vertexNameToIndex,
            String fullFileName,
            int columnVertex, int columnCommunity, int columnWeight,
            boolean forceNormalisation, boolean checkNormalisation){
        boolean headerOn=false;
        readVertexCommunityListFile(vertexNameToIndex,
            fullFileName,
            columnVertex, columnCommunity, columnWeight,
            forceNormalisation, checkNormalisation, headerOn);
    }

    /**
      * Reads in a file with vertex name, community name, fractional membership.
      * <p>Each line has entries separated by whitespace.
      * First entry is the vertex, the next entry on that line is one of its communities and then comes its fractional membership.
      * Graph must be a bipartite structure with every vertex appearing on one and only one line.
      * Each vertex must have at least one community.
      * <p>The vertices are coordinated by name with a list provided by <tt>vertexNameToIndex</tt>.
      * If no such list is given then it is generated by those given in the file.
      * If such a list is given then only vertices in the given list will be in the community.
      * @param vertexNameToIndex a map of vertex names to index (construcyted if not given)
      * @param fullFileName used to determine directory and name of file.
      * @param columnVertex column with vertex index
      * @param columnCommunity column with community index
      * @param columnWeight  column with community memebrship fraction
      * @param headerOn true if first line of file is a header
     */
    public void readVertexCommunityListFile(TreeMap<String,Integer>vertexNameToIndex,
            String fullFileName,
            int columnVertex, int columnCommunity, int columnWeight,
            boolean forceNormalisation, boolean checkNormalisation, boolean headerOn){

      System.out.println("Starting to read vertex community using a vertex-community edge listlist with string vertex labels from " + fullFileName);

        
      /**
        * List of vertices found 
        */ 
      TreeSet<String> vertexLL = new TreeSet();
       /**
         * List of the communities for the vertices
         */
      TreeSet<String> communityLL = new TreeSet();
      /**
        * List of vertex then community of each edge
        */
      ArrayList<String> edgeLL = new ArrayList();
      
      /**
        * Weights list where weight is the 1/(number target vertices on line)
        * <p>Note that these are given for each edge once.
        */
      DoubleArrayList edgeWeightLL = new DoubleArrayList();  
      
      FileInput fi = new FileInput(infoLevel);
        int columnLabel=-1; // no edge labels here
        fi.readStringEdgeFile(fullFileName, 
            columnVertex, columnCommunity, columnWeight, columnLabel,
            vertexLL, communityLL, edgeLL, edgeWeightLL, null, headerOn);

        
       setUp(vertexNameToIndex, 
            vertexLL, communityLL, 
            edgeLL, edgeWeightLL,
            forceNormalisation, checkNormalisation);

    }
    
    /**
     * Set up a vertex community.
     * <p>If <tt>vertexNameToIndex</tt> is given then it will not include any
     * vertex found if it is not in the given list.
     * @param vertexNameToIndex a map of vertex names to index (constructed if not given)
     * @param vertexLL set of all distinct vertex names
     * @param communityLL set of all distinct community labels
     * @param edgeLL list of source then target of each edge
     * @param edgeWeightLL list of weights so <tt>weightLL.get(e)</tt> associated with the edge with source <tt>edgeLL.get(2e)</tt>
     * @param forceNormalisation true if want to force normalisation
     * @param checkNormalisation true if want to check normalisation
     */
    public void setUp(TreeMap<String,Integer>vertexNameToIndex, 
            Set<String> vertexLL, Set<String> communityLL, 
            ArrayList<String> edgeLL, DoubleArrayList edgeWeightLL,
            boolean forceNormalisation, boolean checkNormalisation)
    {
        // If no vertex name to index map given then make one
        if (vertexNameToIndex==null){
          vertexNameToIndex = new TreeMap();
          int vnumber=0;
          for (String vname: vertexLL) vertexNameToIndex.put(vname,vnumber++);
        }
        
        // Give each type 2 index (communities) an index
        TreeMap<String,Integer> communityNameToIndex = new TreeMap();
        int cnumber=0;
        for (String cname: communityLL) {
            communityNameToIndex.put(cname,cnumber++);
        }
        
        // Now set community Matrix
        Integer vertex;
        String vName="";
        String cname="";
        initialiseEmptyCommunity(vertexLL.size(),communityLL.size());
        int numberVertexStubsIncluded=0;
        int numberVertexStubsTotal=0;
        double ew=-1;
        double f=-1;
        for (int e=0; e < edgeLL.size(); e++)
        {
            vName =edgeLL.get(e++);
            vertex = vertexNameToIndex.get(vName);
            numberVertexStubsTotal++;
            if (vertex==null) continue;
            numberVertexStubsIncluded++;
            setElementName(vertex, vName);
            cname = edgeLL.get(e);
            cnumber = communityNameToIndex.get(cname);
            ew=edgeWeightLL.getQuick(e/2);
            f=communityMatrix.get(vertex,cnumber)+ew;
            communityMatrix.set(vertex,cnumber,f);
//            if (vertex==640){
//                System.err.println("*** communityMatrix[vertex="+vertex+"][cnumber="+cnumber+"]");
//            }
//            if (Double.isNaN(communityMatrix[vertex][cnumber])) {
//                System.err.println("*** NaN for communityMatrix[vertex="+vertex+"][cnumber="+cnumber+"]");
//            }
            setCommunityName(cnumber,cname);
        }// eo for e

        if (numberVertexStubsIncluded==numberVertexStubsTotal) System.out.println("  Found "+numberVertexStubsTotal+" and all were included");
        else System.out.println("*** Found "+numberVertexStubsTotal+" vertex stubs, but only included "+numberVertexStubsIncluded);
        
        // force normalisation
        if (forceNormalisation) 
        { 
            int v= normaliseMatrix(1e-6);
            if (v>=0) throw new RuntimeException("Forcing community matrix normalisation failure for vertex "+v);
        }
        
        // check normalisation
        if (checkNormalisation) 
        { 
            int v= this.checkMatrixNormalisation(1e-6);
            if (v>=0) throw new RuntimeException("Matrix normalisation failure for vertex "+v);
        }
        
    }

    /**
     * Sets up vertex community from motif community.
     * <p>Input is a list of vertices in each motif and a second list of
     * the communities for each motif.
     * A vertex gets a count from each motif it is a member so if its in three
     * motifs and two of these motifs are in the same community (C1) while
     * the third motif is in a different community (C2) then the vertex will
     * have membership of two for C1 and one for C2.  These will be normalised if that
     * option is selected (to 2/3 and 1/3).
     * Motif communities are renumbered to be consequctive from zero,
     * retaining the order in which the original numbering was given  Thus if
     * the original numbereing was a contrinuous sequence from 0,
     * there should be no changes.
     * Vertices which have no community are assigned the next highest available
     * community number, performed by doing lowest index vertices first.
     * <p>If have a vertex to Community list then look at constructors.
     * @param numberVertices total number of vertices
     * @param motifToVertex list of vertices in each motif
     * @param motifToCommunity array of community labels for each motif
     * @param forceNormalisation true if want to force normalisation
     * @param checkNormalisation true if want to check normalisation
     */
    public void setUp(int numberVertices, ArrayList<ArrayList<Integer>> motifToVertex,
            int [] motifToCommunity, 
            boolean forceNormalisation, boolean checkNormalisation ){
    
        ArrayList<ArrayList<Integer>> vertexInCommunity = new ArrayList();
        for (int v=0; v<numberVertices; v++) vertexInCommunity.add(new ArrayList());

        // find how many communities there are
        TreeSet<Integer> vertexInMotifSet = new TreeSet();
        TreeSet<Integer> communitySet = new TreeSet();
        //for (ArrayList<Integer> motif:motifToVertex){
        for (int m=0; m<motifToCommunity.length; m++) {
            ArrayList<Integer> motif = motifToVertex.get(m);
            communitySet.add(motifToCommunity[m]);
            for (Integer v : motif) { vertexInMotifSet.add(v);
            }
        }
        int unassignedVertices = numberVertices-vertexInMotifSet.size();
        int communityNumber=communitySet.size();
        int totalNumberCommunities=unassignedVertices +communityNumber;
        //communityMatrix = new SparseDoubleMatrix2D(this.getNumberVertices(),unassignedVertices +communityNumber);
        this.initialiseEmptyCommunity(numberVertices, totalNumberCommunities);

        // map old community numbers to new, retaining order
        TreeMap<Integer,Integer> oldToNewCommunity = new TreeMap();
        int newc=0;
        for (Integer c:communitySet) oldToNewCommunity.put(c, newc++);

        // now assign vertices in motifs
        for (int m=0; m<motifToCommunity.length; m++) {
            ArrayList<Integer> motif = motifToVertex.get(m);
            newc=oldToNewCommunity.get(motifToCommunity[m]);
            for (Integer v : motif) { 
                this.increaseCommunityMembership(v, newc, 1.0);
            }
        } 

        // now assign vertices not in motifs
        for (int v=0; v<getNumberVertices(); v++) if (!vertexInMotifSet.contains(v)) setCommunityMembership(v,communityNumber++,1.0);
        
                // force normalisation
        if (forceNormalisation) 
        { 
            int v= normaliseMatrix(1e-6);
            if (v>=0) throw new RuntimeException("Forcing community matrix normalisation failure for vertex "+v);
        }
        
        // check normalisation
        if (checkNormalisation) 
        { 
            int v= this.checkMatrixNormalisation(1e-6);
            if (v>=0) throw new RuntimeException("Matrix normalisation failure for vertex "+v);
        }

        
    }


        /**
     * Defines a full vertex community from list of communities assigned to each vertex.
     * <p>Any vertex in graph but not in list given is given a unique number
     * based on its index.
     * Membership is for each community is 1/nc where nc is number
     * of communities a vertex is involved in.
     * Membership is automatically normalised.
     * @param vertexToCommunity List of communities associated with each vertex
     */
    public void setUp (ArrayList<ArrayList<Integer> > vertexToCommunity){

        ArrayList<Integer> vc;
        TreeSet<Integer> communityList = new TreeSet();
        int numberVertices=getNumberVertices();
        for (int v=0; v<numberVertices; v++){
            vc=vertexToCommunity.get(v);
            if (vc.size()>0){
                for (Integer c: vc) {
                    communityList.add(c);
                }
            }
            else{
                // vertex not in list given unique community number
                communityList.add(v+numberVertices);
            }
        }

        int nc=0;
        TreeMap<Integer,Integer> oldToNew = new TreeMap();
        for (Integer c: communityList) oldToNew.put(c, nc++);

        communityMatrix = new SparseDoubleMatrix2D(numberVertices,communityList.size());
        for (int v=0; v<numberVertices; v++){
            vc=vertexToCommunity.get(v);
            double inc=1.0/((double) vc.size());
            for (Integer c: vc){
                int newc=oldToNew.get(c);
                double f=communityMatrix.get(v,newc)+inc;
                communityMatrix.set(v,newc,f);
            }
        }



    }




}
