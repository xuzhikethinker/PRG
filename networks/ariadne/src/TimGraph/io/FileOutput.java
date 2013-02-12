/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import TimGraph.*;
import TimGraph.Community.Community;
import TimGraph.Community.Partition;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
//import TimGraph.io.GraphViz;
//import TimGraph.io.GraphMLGenerator;
import TimGraph.Community.VertexPartition;
import TimUtilities.FileUtilities.FileNameSequence;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author time
 */
public class FileOutput {
    
    private final static String NOEDGELABELLINGNAME="NOEDGELABELLINGNAME"; 
    timgraph tg;
    int infoLevel=0;
    public FileNameSequence fileName;
    
    public FileOutput(timgraph inputtg){
      tg= inputtg;
      infoLevel=inputtg.infoLevel;
      fileName= new FileNameSequence(tg.outputName);
    }

    public FileOutput(FileNameSequence inputFNS){
      fileName= new FileNameSequence(inputFNS);
    }
    
    /**
     * Output a map to a file.
     * @param m map to be output
     * @param headerLines array of line to output first 
     * @param sep separation string
     * @param infoOn if true gives information on screen
     */
    static public void map(Map m, String fileName, String[] headerLines, String sep, boolean infoOn){
        PrintStream PS;
        FileOutputStream fout;
        if (infoOn) System.out.println("Writing file of map to "+ fileName);
            try {
            fout = new FileOutputStream(fileName);
            PS = new PrintStream(fout);
            for (int line=0; line<headerLines.length; line++) PS.println(headerLines[line]);

            for (Object k: m.keySet()) PS.println(k+sep+m.get(k));
            //for (String k: m.keySet()) System.out.println(k+sep+m.get(k));
            try{ fout.close ();   
               } catch (IOException e) { throw new RuntimeException("*** File Error with " +fileName+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Error opening output file "+fileName+" "+e.getMessage());
        }
        if (infoOn) System.out.println("Finished writing file of map to "+ fileName);        
    }

    
    // --------------------------------------------------------------------------  
        /**
         * Writes network as a GraphViz file with <tt>.gv</tt> extension.
         */
         public void graphViz(){
        GraphViz gv = new GraphViz();
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("output.gv");
        if (infoLevel>-2) System.out.println("Writing GraphViz file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            gv.output(PS, tg, true);

            if (infoLevel>-2) System.err.println("Finished writing GraphViz file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+", "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        return;
         }

    /**
     * Output as square table.
     * <br><tt>outputAdjMat.dat</tt> extension.
     * @param sep string for separation of columns e.g. tab
     * @param labelsOn rows and columns are labelled
     * @param namesOn labels used are names, otherwise the vertex numbers are used
     */
    public void adjacencyMatrix(String sep, boolean labelsOn, boolean namesOn)  {
        fileName.setNameEnd("outputAdjMat.dat");
        PrintStream PS;
        String [] namesArray=null;
        if (labelsOn && namesOn) namesArray = tg.getVertexNameArray();
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            AdjacencyMatrix am = new AdjacencyMatrix(tg);
            am.printMatrix(PS, sep, namesArray, namesOn);
            if (infoLevel>-2) System.out.println("Finished writing adjacecncy matrix file to "+ fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        return;
    }


    /** 
     * Output in Pajek format. 
     * <p>Edges coloured by the labels of the edge weights (if they exist).
     * Vertices names and coordinates shown if vertices have labels.
     * <br><tt>.net</tt> extension.
     */
    public void pajek()  {pajek(true,true);}
    
    /** 
     * Output in Pajek format. 
     * <p>Edges coloured by the labels of the edge weights.
     * Vertices names and coordinates shown if vertices have labels.
     * <br><tt>.net</tt> extension.
     * @param vertexCoordinateOn prints vertex coordinates if vertices labelled and if minimum coordinante is not null
     * @param colourOn shows colours as a number given by edge weight labels.
     */
    public void pajek(boolean vertexCoordinateOn, boolean colourOn)  {
        String s;
        fileName.setNameEnd("output.net");
        PrintStream PS;
        int ew,ec;
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println("*Vertices "+tg.getNumberVertices());
            tg.calcMinMaxVertexLabel();
            for (int v=1; v<=tg.getNumberVertices(); v++) 
            {
                PS.print(v);
                if (tg.isVertexLabelled()) {
                    if (vertexCoordinateOn && tg.getMaximumVertexCoordinate()!=null) s=" "+tg.getVertexLabel(v-1).pajekString(tg.getMinimumVertexCoordinate(),tg.getMaximumVertexCoordinate());
                    else s=" "+tg.getVertexLabel(v-1).quotedNameString();
                    PS.print(s);
                }
                //PS.print(" "+vertexLabelList[v-1].pajekString());
                PS.println();
            }
            // .net  format for the arcs
            //      1      2       1 c Blue
            // gives an arc between vertex 1 and 2, value 1 colour blue
            if (tg.isDirected()) PS.println("*Arcs "+tg.getNumberEdges());
            else PS.println("*Edges "+tg.getNumberEdges());
            int vs=-1;
            int vt=-1;
            int temp=-1;
            for (int e=0; e<tg.getNumberStubs(); e+=2)  
                {
                    vs=tg.getVertexFromStub(e);
                    vt=tg.getVertexFromStub(e+1);
                    if ((!tg.isDirected()) && (vs>vt))PS.print(  (vt+1)+"   " + (vs+1) );
                    else PS.print(  (vs+1)+"   " + (vt+1) );
                    if (tg.isWeighted()) {
                        s="  " + tg.getEdgeWeight(e);
                        if (colourOn) s= s+ "  c " + tg.getPajekColour(tg.getEdgeLabel(e));
                        PS.println(s);
                    }
                    else PS.println();
                }
            

            if (infoLevel>-2) System.out.println("Finished writing pajek file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        return;
    }

   /** 
     * Output a vertex partition in Pajek <tt>.clu</tt> format. 
     * <p>A simple list of integer values in order of vertex index.
     * @param fileNameEnding end of file name, should include the <tt>.clu</tt> if required.
     * @param values array of integer values to be used to form a pajek partition file
     */
    public void pajekPartition(String fileNameEnding, int [] values)  {
        if (values.length != tg.getNumberVertices()) {
            System.err.println("*** Error length of partition "+values.length+" not equal to number of vertices "+tg.getNumberVertices()); 
            return;
        }
        fileName.setNameEnd(fileNameEnding);
        PrintStream PS;
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println("*Vertices "+tg.getNumberVertices());
            for (int v=0; v<tg.getNumberVertices(); v++) PS.println(values[v]);
            
            if (infoLevel>-2) System.out.println("Finished writing edge list file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        return;
    }
   /** 
     * Output a vertex partition in Pajek <tt>.clu</tt> format. 
     * <p>A simple list of integer values in order of vertex index.
     * @param fileNameEnding end of file name, should include the <tt>.clu</tt> if required.
     * @param vp vertex partition used to give values to partition
     */
    public void pajekPartition(String fileNameEnding, VertexPartition vp)  {
        if (vp.getNumberVertices() != tg.getNumberVertices()) {
            System.err.println("*** Error vertices in partition "+vp.getNumberVertices()+" not equal to number of vertices "+tg.getNumberVertices()); 
            return;
        }
        pajekPartition(fileNameEnding, vp.getCommunity());
    }

    

    
    
    /** 
     * Output edgeList format. 
     * <br><tt>outputEL.dat</tt> extension.
     * Gives bare information on edges, no headers etc.
     * Each line contains Source vertex index, target vertex index, 
     * and if the graph is weighted then finally the edge weight
     * separated by a tab character.
     */
     public void edgeListSimple() {printEdges(timgraph.DUNSET,false, false, false, false, "");}
    
    /** 
     * Output edgeList format. 
     * <br><tt>outputEL.dat</tt> or <tt>outputELS.dat</tt> extension 
     * depending on <tt>asNames</tt> setting..
     * Gives bare information on edges, no headers etc.
     * Each line contains Source vertex index, target vertex index, 
     * and if the graph is weighted then finally the edge weight
     * separated by a tab character.
     * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      */
     public void edgeListSimple(boolean asNames) {printEdges(timgraph.DUNSET,asNames, false, false, false, "");}
    
     /** 
      * Output edgeList format. 
      * <br><em>edgeLabelName</em><tt>outputEL.dat</tt> ending if <tt>edgeLabelOn</tt> is false,
      * otherwise <em>edgeLabelName</em><tt>outputELS.dat</tt> ending
      * <br>A header line can be printed as can an information line.
      * <br>Each line has one edge with:-
      * if edgeIndexOn the edge index of stub at source, edge index of stub at target. 
      * Next and always is the vertex index or name of source, vertex index or name of target, 
      * and finally if edgeLabelOn the edge label and edge weight.
      * No extra naming is given to files because of any edge labelling.
      * In weighted graphs edges with weights below minWeight are not shown
      * <br>Target edge index should be one more than its source and the source indices ought to be even. 
      * @param minWeight minumum weight of edges to be shown, ignored in unweighted graphs.  
      * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      * @param infoOn if true then first line will be an information line starting with the <tt>cc</tt> string.
      * @param headerOn if true then head of each column labels the item
      * @param edgeIndexOn if true the the first two items will be the edge index used internally.  
      * @param edgeLabelOn shows the label part of edge if weighted
      */
    public void printEdges(double minWeight, boolean asNames, boolean infoOn, boolean headerOn,
                           boolean edgeIndexOn, boolean edgeLabelOn)
{
        if (edgeLabelOn) printEdges(minWeight, asNames, infoOn, headerOn, edgeIndexOn, NOEDGELABELLINGNAME); 
        else printEdges(minWeight, asNames, infoOn, headerOn, edgeIndexOn, "") ;
        
    }
     /**
      * Output edgeList format.
      * <br><em>edgeLabelName</em><tt>outputEL.dat</tt> ending if <tt>edgeLabelOn</tt> is false,
      * otherwise <em>edgeLabelName</em><tt>outputELS.dat</tt> ending
      * <br>A header line can be printed as can an information line.
      * <br>Each line has one edge with:-
      * if edgeIndexOn the edge index of stub at source, edge index of stub at target.
      * Next and always is the vertex index or name of source, vertex index or name of target,
      * and finally if edgeLabelOn the edge label and edge weight.
      * No extra naming is given to files because of any edge labelling.
      * In weighted graphs edges with weights below minWeight are not shown
      * <br>Target edge index should be one more than its source and the source indices ought to be even.
      * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      * @param infoOn if true then first line will be an information line starting with the <tt>cc</tt> string.
      * @param headerOn if true then head of each column labels the item
      * @param edgeIndexOn if true the the first two items will be the edge index used internally.
      * @param edgeLabelOn shows the label part of edge if weighted
      */
    public void printEdges(boolean asNames, boolean infoOn, boolean headerOn, boolean edgeIndexOn,
                            boolean edgeLabelOn)
{
        if (edgeLabelOn) printEdges(timgraph.DUNSET, asNames, infoOn, headerOn, edgeIndexOn, NOEDGELABELLINGNAME);
        else printEdges(timgraph.DUNSET, asNames, infoOn, headerOn, edgeIndexOn, "") ;

    }
     /**
      * Output edgeList format.
      * <br><em>edgeLabelName</em><tt>outputEL.dat</tt> ending if <tt>edgeLabelOn</tt> is false,
      * otherwise <em>edgeLabelName</em><tt>outputELS.dat</tt> ending
      * <br>A header line can be printed as can an information line.
      * <br>Each line has one edge with:-
      * if edgeIndexOn the edge index of stub at source, edge index of stub at target.
      * Next and always is the vertex index or name of source, vertex index or name of target,
      * and finally if edgeLabelOn the edge label and edge weight.
      * No extra naming is given to files because of any edge labelling.
      * In weighted graphs edges with weights below minWeight are not shown
      * <br>Target edge index should be one more than its source and the source indices ought to be even.
      * @param minWeight minimum weight of edges to be shown, ignored in unweighted graphs.
      * @param minLabel minimum label of edges to be shown, ignored in unweighted graphs.
      * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      * @param infoOn if true then first line will be an information line starting with the <tt>cc</tt> string.
      * @param headerOn if true then head of each column labels the item
      * @param edgeIndexOn if true the the first two items will be the edge index used internally.
      * @param edgeLabelOn shows the label part of edge if weighted
      */
    public void printEdges(double minWeight, int minLabel, boolean asNames, boolean infoOn, boolean headerOn, boolean edgeIndexOn,
                            boolean edgeLabelOn)
{
        if (edgeLabelOn) printEdges(minWeight, minLabel, asNames, infoOn, headerOn, edgeIndexOn, NOEDGELABELLINGNAME);
        else printEdges(minWeight, minLabel, asNames, infoOn, headerOn, edgeIndexOn, "") ;

    }
         /** 
      * Output edgeList format. 
      * <br><em>edgeLabelName</em><tt>outputEL.dat</tt> ending if <tt>edgeLabelName</tt> null,
      * otherwise <em>edgeLabelName</em><tt>outputELS.dat</tt> ending
      * <br>A header line can be printed as can an information line.
      * <br>Each line has one edge with:-
      * if edgeIndexOn the edge index of stub at source, edge index of stub at target. 
      * Next and always is the vertex index or name of source, vertex index or name of target, 
      * and finally if edgeLabelOn the edge label and edge weight.
      * In weighted graphs edges with weights are shown.
      * <br>Target edge index should be one more than its source and the source indices ought to be even. 
      * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      * @param infoOn if true then first line will be an information line starting with the <tt>cc</tt> string.
      * @param headerOn if true then head of each column labels the item
      * @param edgeIndexOn if true the the first two items will be the edge index used internally.  
      * @param edgeLabelName name of labelling scheme used for edges.  No labels shown if this is empty string
      */
    public void printEdges(boolean asNames, boolean infoOn, boolean headerOn,
                           boolean edgeIndexOn, String edgeLabelName)
    {
        printEdges(timgraph.DUNSET, timgraph.IUNSET, asNames, infoOn, headerOn, edgeIndexOn, edgeLabelName);
    }
    /**
      * Output edgeList format.
      * <br><em>edgeLabelName</em><tt>outputEL.dat</tt> ending if <tt>edgeLabelName</tt> null,
      * otherwise <em>edgeLabelName</em><tt>outputELS.dat</tt> ending
      * <br>A header line can be printed as can an information line.
      * <br>Each line has one edge with:-
      * if edgeIndexOn the edge index of stub at source, edge index of stub at target.
      * Next and always is the vertex index or name of source, vertex index or name of target,
      * and finally if edgeLabelOn the edge label and edge weight.
      * In weighted graphs edges with weights below minWeight are not shown.
      * <br>Target edge index should be one more than its source and the source indices ought to be even.
      * @param minWeight minimum weight of edges to be shown, ignored in unweighted graphs.
      * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      * @param infoOn if true then first line will be an information line starting with the <tt>cc</tt> string.
      * @param headerOn if true then head of each column labels the item
      * @param edgeIndexOn if true the the first two items will be the edge index used internally.
      * @param edgeLabelName name of labelling scheme used for edges.  No labels shown if this is empty string
      */
    public void printEdges(double minWeight, boolean asNames, boolean infoOn,
                            boolean headerOn, boolean edgeIndexOn, String edgeLabelName)
{   printEdges(minWeight, timgraph.IUNSET, asNames, infoOn, headerOn, edgeIndexOn, edgeLabelName);
    }
    /** 
      * Output edgeList format. 
      * <br><em>edgeLabelName</em><tt>outputEL.dat</tt> ending if <tt>edgeLabelName</tt> null,
      * otherwise <em>edgeLabelName</em><tt>outputELS.dat</tt> ending
      * <br>A header line can be printed as can an information line.
      * <br>Each line has one edge with:-
      * if edgeIndexOn the edge index of stub at source, edge index of stub at target. 
      * Next and always is the vertex index or name of source, vertex index or name of target, 
      * and finally if edgeLabelOn the edge label and edge weight.
      * In weighted graphs edges with weights below minWeight are not shown.
      * If labels are on then edges with labels below minLabel are not shown.
      * <br>Target edge index should be one more than its source and the source indices ought to be even. 
      * @param minWeight minimum weight of edges to be shown, ignored in unweighted graphs.
      * @param minLabel minimum label of edges to be shown, ignored in unweighted graphs.
      * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      * @param infoOn if true then first line will be an information line starting with the <tt>cc</tt> string.
      * @param headerOn if true then head of each column labels the item
      * @param edgeIndexOn if true the the first two items will be the edge index used internally.  
      * @param edgeLabelName name of labelling scheme used for edges.  No labels shown if this is empty string
      */
    public void printEdges(double minWeight, int minLabel, boolean asNames, boolean infoOn,
                            boolean headerOn, boolean edgeIndexOn, String edgeLabelName)
{
        boolean edgeLabelOn=false;
        String fullfilename=fileName.getNameRootFullPath();
        if (edgeLabelName.length()>0 )  {
            edgeLabelOn=true;
            if (!edgeLabelName.equals(NOEDGELABELLINGNAME)) fullfilename=fullfilename+edgeLabelName;
        }
        boolean useLabels=(asNames & tg.isVertexLabelled()); 
        if (useLabels) fullfilename=fullfilename+"outputELS.dat";
        else fullfilename=fullfilename+"outputEL.dat";
        PrintStream PS;
        FileOutputStream fout;
        if (infoLevel > -2) {
            System.out.println("Writing edge list file with vertex "+(useLabels?"labels":"numbers")+" to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            tg.printEdges(PS, "#", " \t", minWeight, minLabel, useLabels, infoOn, headerOn, edgeIndexOn, edgeLabelOn);

            if (infoLevel > -2) {
                System.out.println("Finished writing edge list file to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;
    }
      
    /** 
      * Prints a list of information on a vertex partition.
      * <br><tt>output.vpi</tt> for output file extension.
      * <br>A header line can be printed.
      * @param vp the vertex partition to be output.
      * @param cc comment string used at start of each header or info line
      * @param sep string separating items
      * @param infoOn prints an initial line of basic partition information
      * @param headerOn if true then head of each column labels the item
      */
    public void printPartitionStats(Partition vp, String cc, String sep, boolean infoOn, boolean headerOn) 
{
        //boolean edgeLabelOn=false;
        String fullfilename=fileName.getNameRootFullPath()+vp.getName()+"stats.dat";
        PrintStream PS;
        FileOutputStream fout;
        if (infoLevel > -2) {
            System.out.println("Writing partition statistics to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            vp.printStatistics(PS, cc, sep, infoOn, headerOn);
            if (infoLevel > -2) {
                System.out.println("Finished writing partition statistics to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;
 
    }
    
     /** 
      * Prints a list of information on the edge community structure by vertex.
      * <br><em>edgeLabelName</em><tt>output.vci</tt> if by vertex index
      * or <em>edgeLabelName</em><tt>output.vcis</tt> if using vertex names.
      * <br>A header line can be printed.
      * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      * @param headerOn if true then head of each column labels the item
      * @param splitBipartite true if want to split a bipartite graph into type one or two (ignored if not bipartite)
      * @param outputType1 true (false) if want type 1 (2) vertices if splitting them for a bipartite graph
      */
    public void printEdgeCommunityStats(boolean asNames, boolean headerOn,
            boolean splitBipartite, boolean outputType1)
{
        printEdgeCommunityStats("", asNames, headerOn,splitBipartite,  outputType1) ;
    }

    /** 
      * Prints a list of information on the edge community structure by vertex.
      * <br><em>edgeLabelName</em><tt>output.vci</tt> if by vertex index
      * or <em>edgeLabelName</em><tt>output.vcis</tt> if using vertex names.
      * <br>A header line can be printed.
      * @param addToFileNameRoot added to file name root to make file name e.g. name of edge partition
      * @param asNames edges are given as the vertex names in a pair if they exist, otherwise using numerical indices.
      * @param headerOn if true then head of each column labels the item
      * @param splitBipartite true if want to split a bipartite graph into type one or two (ignored if not bipartite)
      * @param outputType1 true (false) if want type 1 (2) vertices if splitting them for a bipartite graph
      */
    public void printEdgeCommunityStats(String addToFileNameRoot, boolean asNames, boolean headerOn,
            boolean splitBipartite, boolean outputType1)
{
       //boolean edgeLabelOn=false;
        String fullfilename=fileName.getNameRootFullPath();
        boolean useLabels=(asNames & tg.isVertexLabelled()); 
        if (useLabels) fullfilename=fullfilename+"_"+addToFileNameRoot+"output.vcis";
        else fullfilename=fullfilename+"_"+addToFileNameRoot+"output.vci";
        PrintStream PS;
        FileOutputStream fout;
        if (infoLevel > -2) {
            System.out.println("Writing edge community information by vertex "+(useLabels?"labels":"numbers")+" to " + fullfilename);
        }
        try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            tg.printEdgeCommunityStats(PS,  "\t", headerOn, useLabels, splitBipartite,  outputType1 );
            if (infoLevel > -2) {
                System.out.println("Finished writing edge community information by vertex file to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;
 
    }
        /**
         * Outputs graph in graphML format suitable for <tt>visone</tt>.
         * <br>File extension is <tt>.graphML</tt>
         */
    public void graphML(){
        graphML("",timgraph.DUNSET);
    }
        /**
         * Outputs graph in graphML format suitable for <tt>visone</tt>.
         * <br>File extension is <em>ending</em><tt>.graphML</tt>
         * @param ending string to add before the <tt>.graphML</tt> extension
         * @param edgeWeightMinimum only edges of this weight or larger are displayed
         */
    public void graphML(String ending, double edgeWeightMinimum){
        GraphMLGenerator gml = new GraphMLGenerator();
        PrintStream PS;
        FileOutputStream fout;
        if (edgeWeightMinimum>0) fileName.setNameEnd(ending+"ew"+edgeWeightMinimum+".graphML");
        else fileName.setNameEnd(ending+".graphML");
        boolean targetArrowsOn=tg.isDirected();
        try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            gml.outputGraph(PS, tg, edgeWeightMinimum, targetArrowsOn);

            if (infoLevel>-2) System.out.println("Finished writing GraphML file to "+ fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;
    }
        /**
         * Outputs graph with vertex partition in graphML format suitable for <tt>visone</tt>.
         * <br>File extension is <tt>VP.graphML</tt>
         * @param c hold vertex partition
         */
        public void graphMLVertexPartition(Partition c){
        GraphMLGenerator gml = new GraphMLGenerator();
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+c.getName()+"VP.graphML");
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            gml.outputVertexPartition(PS, tg, c);
            
            if (infoLevel>-2) System.out.println("Finished writing GraphML file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;
    }

    /**
     * Output edge coloured graph using an edge partition.
     * <br>File extension is <tt>EP.graphML</tt>
     * <br>Note that the community here is a vertex partition 
     * for a line graph of the timgraph used in the definition.  Thus the 
     * vertex index <tt>v</tt> of the given community (and of the line graph)
     * corresponds to the edge in the original input graph <tt>tg</tt>  
     * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     * @param c edge partition where `vertex' <tt>v</tt> is edge with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     */
    public void graphMLEdgePartition(Partition c){
        GraphMLGenerator gml = new GraphMLGenerator();
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+c.getName()+"EP.graphML");
        if (infoLevel>-2) System.out.println("Writing edge partition GraphML file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            gml.outputEdgePartition(PS, tg, c);
            
            if (infoLevel>-2) System.out.println("Finished writing edge partition GraphML file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;        
    }

    /**
     * Output edge coloured graph using built in edge labels.
     * <br>File extension is <tt>EP.graphML</tt>
     * <br>Note that the community here is a vertex partition 
     * for a line graph of the timgraph used in the definition.  Thus the 
     * vertex index <tt>v</tt> of the given community (and of the line graph)
     * corresponds to the edge in the original input graph <tt>tg</tt>  
     * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     * @param numberLabels number of labels
     */
    public void graphMLEdgePartition(int numberLabels){
        GraphMLGenerator gml = new GraphMLGenerator();
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_EP.graphML");
        if (infoLevel>-2) System.out.println("Writing edge partition GraphML file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            gml.outputEdgeLabels(PS, tg, numberLabels);
            
            if (infoLevel>-2) System.out.println("Finished writing edge partition GraphML file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;        
    }


    /**
     * Output vertex and edge coloured graph using a vertex and an edge partition.
     * <br>File extension is <tt>VEP.graphML</tt>
     * <br>Note that the community here is a vertex partition 
     * for a line graph of the timgraph used in the definition.  Thus the 
     * vertex index <tt>v</tt> of the given community (and of the line graph)
     * corresponds to the edge in the original input graph <tt>tg</tt>  
     * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     * @param vp vertex partition
     * @param ep edge partition where `vertex' <tt>v</tt> is edge with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     */
    public void graphMLVertexEdgePartition(VertexPartition vp, VertexPartition ep){
        GraphMLGenerator gml = new GraphMLGenerator();
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+vp.getName()+"VP_"+ep.getName()+"EP.graphML");
        if (infoLevel>-2) System.out.println("Writing DualGraphML file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            gml.outputVertexEdgePartition(PS, tg, vp, ep);
            
            if (infoLevel>-2) System.out.println("Finished writing DualGraphML file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;        
    }

        /**
     * Output vertex and edge coloured graph using an incidence matrix vertex partition.
     * <br>File extension is <tt>IVP.graphML</tt>
     * <br>Note that the community here is a vertex partition 
     * for the incidence matrix of the timgraph used in the definition.  
     * The incidence graph vertices has original vertices indexed as
     * <tt>0..(tg.getNumberVertices()-1)</tt>.  The original edges have index in the partition of
     * <tt>v=(tg.getNumberVertices())..(tg.getNumberVertices()+tg.getNumberStubs()/2-1)</tt>
     * and the original edge index is then <tt>e=(v-tg.getNumberVertices())*2)</tt> and <tt>(e+1)</tt>
     * @param c incidence matrix partition.
     */
    public void graphMLIncidencePartition(VertexPartition c){
        GraphMLGenerator gml = new GraphMLGenerator();
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+c.getName()+"IVP.graphML");
        if (infoLevel>-2) System.out.println("Writing incidence matrix vertex partition GraphML file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            gml.outputIncidenceVertexPartition(PS, tg, c);
            
            if (infoLevel>-2) System.out.println("Finished writing incidence matrix vertex partition GraphML file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;        
    }



    /**
     * Output basic information on a network.
     * @param cc comment string
     * @param sep separation string
     */
    public void informationNetworkBasic(String cc, String sep){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("info.dat");
        if (infoLevel>-2) System.out.println("Writing basic network information file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println(cc+fileName.getNameRoot());
            tg.printParametersBasic(PS, sep);
            if (infoLevel>-2) System.out.println("Finished writing vertex and edge information file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;        
    }
    
    
    
    /**
     * Output information on a vertex and an edge partition.
     * <br>File extension is <tt>vpnameVP_epnameEP_info.dat</tt>
     * <br>Note that the community here is a vertex partition 
     * for a line graph of the timgraph used in the definition.  Thus the 
     * vertex index <tt>v</tt> of the given community (and of the line graph)
     * corresponds to the edge in the original input graph <tt>tg</tt>  
     * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     * @param vp vertex partition
     * @param ep edge partition where `vertex' <tt>v</tt> is edge with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     * @param cc comment string
     * @param sep separation string
     * @param listPartitionOn if true gives full list of parttion, otherwise just summary information
     */
    public void informationVertexEdgePartition(VertexPartition vp, VertexPartition ep, String cc, String sep, boolean listPartitionOn){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+vp.getName()+"VP_"+ep.getName()+"EP_info.dat");
        if (infoLevel>-2) System.out.println("Writing vertex and edge partition information file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println(cc+fileName.getNameRoot());
            vp.printInformation(PS, cc+vp.getName()+" Vertex Partition"+sep, sep);
            ep.printInformation(PS, cc+ep.getName()+" Edge Partition"+sep, sep);
//            int nEPoverlapVP = vp.compareEPtoVP(ep);
//            int nVP=vp.getNumberOfEdgesInVP();
//            double fraction = -1;
//            if (nVP>0) fraction = nEPoverlapVP/((double) nVP);
//            PS.print("Edges in VP communities "+nVP+", number ovelapping edges with EP "+nEPoverlapVP);
//            if (nVP>0) PS.println(", fraction ="+fraction);
//            else PS.println();
            if (listPartitionOn){
                PS.println(cc+fileName.getNameRoot()+sep+"Vertex Partition");
                vp.printCommunityMatrix(PS, cc, sep);
                PS.println(cc+fileName.getNameRoot()+sep+"Edge Partition");
                ep.printCommunityMatrix(PS, cc, sep);
            }
            if (infoLevel>-2) System.out.println("Finished writing vertex and edge information file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;        
    }

   /**
     * Output information on a vertex community.
     * <br>File extension is <tt>VCinfo.dat</tt>
     * @param vc vertex community
     * @param cc comment string
     * @param sep separation string
     * @param listCommunityOn if true gives full list of partition, otherwise just summary information
     */
    public void informationVertexCommunity(Community vc, String cc, String sep, boolean listCommunityOn){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+vc.getName()+"VC_info.dat");
        if (infoLevel>-2) System.out.println("Writing vertex community information file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println(cc+fileName.getNameRoot());
            vc.printInformation(PS, cc+vc.getName()+"Vertex Community"+sep, sep);
            if (listCommunityOn){
                PS.println(cc+fileName.getNameRoot()+sep+"Vertex Community");
                vc.printCommunityMatrix(PS, cc, sep);
            }
            if (infoLevel>-2) System.out.println("Finished writing vertex community information file to "+ fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;
    }
   /**
     * Output information on a vertex community.
     * <br>File extension is <tt>VCinfo.dat</tt>
     * @param vc vertex community
     * @param cc comment string
     * @param sep separation string
     * @param headerOn true if want a header line
     * @param ElementLabelsOn true if want to labels the elements of the communities
     */
    public void printVertexCommunity(Community vc, String cc, String sep, boolean headerOn, boolean ElementLabelsOn){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+vc.getName()+"VC.dat");
        if (infoLevel>-2) System.out.println("Writing vertex community to file "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            vc.printCommunityMatrixSparse(PS, cc, sep, headerOn, ElementLabelsOn);
            if (infoLevel>-2) System.out.println("Finished writing vertex community information file to "+ fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;
    }
   /**
     * Output information on the communities of a vertex partition.
     * <br>File extension is <tt>VPinfo.dat</tt>
     * @param vp partition
     * @param cc comment string
     * @param sep separation string
     * @param listPartitionOn if true gives full list of each partition, otherwise just summary information
     */
    public void informationPartition(Partition vp, String cc, String sep, boolean listPartitionOn){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+vp.getName()+"P_info.dat");
        if (infoLevel>-2) System.out.println("Writing partition information file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println(cc+fileName.getNameRoot());
            vp.printInformation(PS, cc+vp.getName()+"Partition"+sep, sep);
            if (listPartitionOn){
                PS.println(cc+fileName.getNameRoot()+sep+"Vertex Partition");
                vp.printCommunityMatrix(PS, cc, sep);
            }
            if (infoLevel>-2) System.out.println("Finished writing partition information file to "+ fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;
    }


    /**
     * Output information on an edge partition.
     * <br>File extension is <tt>EPinfo.dat</tt>
     * <br>Note that the community here is a vertex partition 
     * for a line graph of the timgraph used in the definition.  Thus the 
     * vertex index <tt>v</tt> of the given community (and of the line graph)
     * corresponds to the edge in the original input graph <tt>tg</tt>  
     * with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     * @param ep edge partition where `vertex' <tt>v</tt> is edge with indices <tt>(2v)</tt> and <tt>(2v+1)</tt>
     * @param cc comment string
     * @param sep separation string
     * @param listPartitionOn if true gives full list of partition, otherwise just summary information
     */
    public void informationEdgePartition(Partition ep, String cc, String sep, boolean listPartitionOn){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+ep.getName()+"EP_info.dat");
        if (infoLevel>-2) System.out.println("Writing edge partition information file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println(cc+fileName.getNameRoot());
            ep.printInformation(PS, cc+ep.getName()+"  Edge Partition"+sep, sep);
            if (listPartitionOn){
                PS.println(cc+fileName.getNameRoot()+sep+"Edge Partition");
                ep.printCommunityMatrix(PS, cc, sep);
            }
            if (infoLevel>-2) System.out.println("Finished writing vertex partition information file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;        
    }

    /**
     * Output information on a vertex partition of an incidence matrix of a graph.
     * <br>File extension is <tt>VPinfo.dat</tt>
     * @param ip incidence matrix vertex partition
     * @param cc comment string
     * @param sep separation string
     * @param listPartitionOn if true gives full list of parttion, otherwise just summary information
     */
    public void informationIncidenceVertexPartition(VertexPartition ip, String cc, String sep, boolean listPartitionOn){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd("_"+ip.getName()+"IVP_info.dat");
        if (infoLevel>-2) System.out.println("Writing incidence matrix vertex partition information file to "+ fileName.getFullFileName());
            try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            PS.println(cc+fileName.getNameRoot());
            ip.printInformation(PS, cc+ip.getName()+"Incidence Matrix Vertex Partition"+sep, sep);
            if (listPartitionOn){
                PS.println(cc+fileName.getNameRoot()+sep+"Incidence Matrix Vertex Partition");
                ip.printCommunityMatrix(PS, cc, sep);
            }
            if (infoLevel>-2) System.out.println("Finished writing vertex information file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;        
    }
    
    /** Prints vertices and their vertex partition statistics.
     * <p>Root of file name is taken from graph plus the name of the vertex partition.
     * <p>A simple vertex-partition pair file has extension <tt>inputBVNLS.dat</tt>
     * while <tt>Vertices.dat</tt>is used for detailed information file.
     * @param cc comment string used at start of each header or info line
     * @param sep string separating items
     * @param infoOn true (false) if want first row to have basic information on partition
     * @param headerOn true (false) if want header row on (off)
     * @param vp vertex partition to be printed
     * @param simpleListOn true gives a vertex-community list
         */     
    public void printVertices(String cc, String sep, boolean infoOn, boolean headerOn,
            VertexPartition vp, boolean simpleListOn){
        PrintStream PS;
        FileOutputStream fout;
        String ext = (simpleListOn?"inputBVNLS.dat":"Vertices.dat");
        fileName.setNameEnd((vp==null?"":vp.getName())+ext);
        // next bit of code p327 Schildt and p550
        try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            //tg.printVertices(PS, cc, sep, printNearestNeighbours);
            
            if (simpleListOn) vp.printSimpleVertexCommunityList(PS, cc, sep, infoOn, headerOn);
            else vp.printVertices(PS, cc, sep, infoOn, headerOn);
            if (infoLevel>-2) {
                if (simpleListOn) System.out.println("Finished writing simple list of vertex - partition pairs to file "+ fileName.getFullFileName());
                else System.out.println("Finished writing vertices and their vertex partition information to file "+ fileName.getFullFileName());
            }
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        return;
     }

    
    /**
     * Output information on vertices.
     * <p>Gives table of vertex properties.
     * <p>Extension of file is <tt>vertices.dat</tt>
     * @param cc comment string
     * @param sep separation string
     * @param vp vertex partition used to indicate community, if null none given
     * @param printTriangles prints number of triangles
     * @param printSquares print number of squares but only if printTriangles also true
     * @param printNearestNeighbours if true gives full list of partition, otherwise just summary information
     */
    public void printVertices(String cc, String sep, VertexPartition vp,
            boolean printTriangles, boolean printSquares,
            boolean printNearestNeighbours )
    {
                boolean printName=false;
                boolean printNumber=false;
                boolean printPosition=false;
                boolean printStrength=false;
                boolean printMaxWeight=false;
                boolean printClusterCoef=false;
                boolean printRank=false;
                boolean printStructuralHoleData=false;
                if (tg.isVertexLabelled())
                 {
                  VertexLabel l=tg.getVertexLabel(0);
                  printName=l.hasName();
                  printNumber=l.hasNumber();
                  printPosition=l.hasPosition();
                  printStrength=l.hasStrength();
                  printClusterCoef=l.hasClusterCoef();
                  printMaxWeight=l.hasMaxWeight();
                  printRank=l.hasRank();
                  printStructuralHoleData=l.hasStructuralHoleVertexData();
                 }
            printVertices(cc, sep, vp, printTriangles, printSquares,
                    printName,printNumber,  printPosition,
                printStrength, printMaxWeight,
                printClusterCoef,
                printRank,
                printStructuralHoleData,
                printNearestNeighbours );
    }
    /**
     * Output information on vertices.
     * <p>This is the PRIMARY vertex printing output routine.
     * <p>Gives table of vertex properties.
     * <p>Extension of file is <i>vertexPartitionName</i><tt>vertices.dat</tt>
     * @param cc comment string
     * @param sep separation string
     * @param vp vertex partition used to indicate community, if null none given
     * @param printTriangles print number of triangles per vertex
     * @param printSquares print number of squares but only if printTriangles also true
     * @param printName true (false) to (not) print  name
     * @param printNumber true (false) to (not) print number
     * @param printPosition true (false) to (not) print coordinate
     * @param printStrength true (false) to (not) print strength
     * @param printRank true (false) to (not) print rank
     * @param printNearestNeighbours if true gives full list of partition, otherwise just summary information
     */
    public void printVertices(String cc, String sep, 
            VertexPartition vp,
            boolean printTriangles, boolean printSquares,
            boolean printName, boolean printNumber, boolean printPosition,
            boolean printStrength, boolean printMaxWeight,
            boolean printClusterCoef,
            boolean printRank,
            boolean printStructuralHoleData,
            boolean printNearestNeighbours ){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd((vp==null?"":"_"+vp.getName())+"vertices.dat");
        // next bit of code p327 Schildt and p550
        try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            //tg.printVertices(PS, cc, sep, printNearestNeighbours);
            tg.printVertices(PS, cc, sep, vp,
                    printTriangles, printSquares,
             printName, printNumber, printPosition, 
             printStrength, printMaxWeight, 
             printClusterCoef,
             printRank, printStructuralHoleData,
             printNearestNeighbours)  ;

            
            if (infoLevel>-2) System.out.println("Finished writing vertex information file to "+ fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+", "+e.getMessage());
            return;
        }
        return;
     }

    /**
     * Output information on motifs of a graph.
     * Format is each line to have (motif index) (motif label) (vertex1) (vertex2) ...
     * Each part is optional depending on the lists being given or switches.
     * @param cc comment string
     * @param sep separation string
     * param ext string with the extension to use
     * @param motifToLabel if length non-zero then list of labels (e.g. community) of
     * @param motifToVertex if not null then list of vertices for each motif
     * @param motifIndexOn true if want first column to be number of motif
     * @param headerOn true if want a header line
     */
    public void printMotifs(String cc, String sep, String ext, int [] motifToLabel, ArrayList<ArrayList<Integer>> motifToVertex,
            boolean motifIndexOn, boolean headerOn){
        PrintStream PS;
        FileOutputStream fout;
        fileName.setNameEnd(ext);
        if (infoLevel>-2) System.out.println("Writing motifs to file "+ fileName.getFullFileName());           try {
            fout = new FileOutputStream(fileName.getFullFileName());
            PS = new PrintStream(fout);
            int numberMotif=-9753257;
            boolean motifLabelOn=false;
            if (motifToLabel.length>0) {motifLabelOn=true; numberMotif = motifToLabel.length;}
            boolean motifVerticesOn=false;
            if (motifToVertex!=null) {
                motifVerticesOn=true;
                if (numberMotif>0) {if (numberMotif != motifToVertex.size()) throw new RuntimeException("Two different numbers of motifs "+numberMotif+" and "+motifToVertex.size());}
                else numberMotif = motifToVertex.size();
            }
            if (numberMotif<=0) {System.err.println("Nothing to print in printMotifs"); return;}

            if (headerOn){
                PS.println((motifIndexOn?"Motif_Index"+sep:"")+(motifLabelOn?"Motif_Label"+sep:"")+(motifLabelOn?"Motif_Vertex_List":""));
            }

            for (int m=0; m<numberMotif; m++){
                PS.print((motifIndexOn?m+sep:"")+(motifLabelOn?motifToLabel[m]+sep:""));
                ArrayList<Integer> motif = motifToVertex.get(m);
                for (int v=0; v<motif.size(); v++) PS.print(((v==0)?"":sep)+motif.get(v));
                PS.println();
            }

            if (infoLevel>-2) System.out.println("Finished writing motif file to "+ fileName.getFullFileName());
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName.getFullFileName()+" "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName.getFullFileName()+" "+e.getMessage());
            return;
        }
        return;
    }



        // ----------------------------------------------------------------------
        /** 
         *  Outputs to file all aspects of a graph.
         * <p>Basic outputs are a very basic information file,
         * simple edge list, vertex list. 
         * @param cc comment string
         * @param sep separation string
         */
    public void informationGeneral(String cc, String sep) 
    {
        informationGeneral(cc, sep, true, true, true, false, true, true, null);
    }
    

    /**
         *  Outputs to file all aspects of a graph.
         * <p>Basic outputs are a very basic information file,
         * with options for simple edge list, vertex list and a graphml file.
         * <p>After that settings of outputControl given here,
         * or if null that in the timgraph, control which extras are given:
         * degree distribution, distances, component, ranking, pajek.
         * @param cc comment string
         * @param sep separation string
         * @param printTriangles true if want number of triangles per vertex listed
         * @param printSquares print number of squares but only if printTriangles also true
         * @param vertexListOn produces file of vertices and their properties
         * @param edgeListOn produces file of edge and their properties
         * @param graphMLOn forces production of graphML file of network whatever other settings are
         * @param outputControl sets the output overriding setting in the timgraph unless null when timgraph setting is used.
         */
    public void informationGeneral(String cc, String sep,
            boolean vertexListOn,
            boolean printTriangles, boolean printSquares,
            boolean printNearestNeighbours,
            boolean edgeListOn, boolean graphMLOn,
            OutputMode outputControl)
    {

        informationNetworkBasic(cc, sep);

        if (tg.isWeighted() && tg.isVertexLabelled()) tg.calcStrength();

        if (edgeListOn) {
            edgeListSimple();
            if (tg.isVertexLabelled() ) edgeListSimple(true);
        }

        OutputMode om;

        boolean infoOn=true;
        boolean headersOn=true;
        if (outputControl==null) om = tg.outputControl;
        else om = outputControl;
        
        if (graphMLOn || om.graphMLFileOn) graphML();


        try{
            if (om.degreeDistributionOn){
              tg.calcDegreeDistribution();
              //tg.printDegreeDistribution(cc, false);
              boolean normalise=false;
              tg.FileOutputDegreeDistribution(cc, normalise, headersOn);
              double lbratio=1.1;
              if (tg.getNumberVertices()>100) tg.FileOutputLogBinnedDegreeDistribution(cc, lbratio, infoOn, headersOn);
            }
        }catch (RuntimeException e) { System.err.println("*** Error in informationGeneral calculating degree distribution: "+e);}
        
        try{
            if (om.distancesOn)
        {
            tg.calcDistanceSample(0.01,10, 100);
            tg.FileOutputDistanceStatistics(cc);
            tg.FileOutputDistanceTotalDistribution(cc);
        }
        }catch (RuntimeException e) { System.err.println("*** Error in informationGeneral calculating distances: "+e);}
        
        try{
        if (om.componentsOn)
        { 
            tg.calcComponents();
            tg.FileOutputComponentInfo(cc);
            tg.FileOutputComponentDistribution(cc);
            tg.FileOutputVertexToComponentList(cc);
        }
        }catch (RuntimeException e) { System.err.println("*** Error in informationGeneral calculating components: "+e);}
        
        
        try{ if (om.rankingOn) tg.calcRanking();
        }catch (RuntimeException e) { System.err.println("*** Error in informationGeneral calculating ranking: "+e);}
        
        try{
            if (om.pajekFileOn) pajek();
                }catch (RuntimeException e) { System.err.println("*** Error in informationGeneral calculating pajek output: "+e);}

        try{
            if (om.structuralHolesOn) {
                tg.calcStructuralHoleVertexData();
                System.out.println("Only vertex structural hole output been calculated, only listed in vertex file.");
            }
                }catch (RuntimeException e) { System.err.println("*** Error in informationGeneral calculating structural hole output: "+e);}

        try{
            if (om.adjacencyFileOn) adjacencyMatrix(timgraph.SEP,true,true);
        }catch (RuntimeException e) { System.err.println("*** Error in informationGeneral calculating adjacency matrix output: "+e);}


        try{
            if (om.clusteringOn){
            tg.calcCCEdgeSample(0.01, 100, 10000);
            tg.FileOutputCC(cc);
        }
                }catch (RuntimeException e) { System.err.println("*** Error in informationGeneral calculating clustering: "+e);}

        VertexPartition vp=null;
        if (vertexListOn)  printVertices(cc, sep, vp,
            printTriangles, printSquares,
            printNearestNeighbours );

        
    }

             
}
