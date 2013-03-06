/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import java.io.PrintStream;
import TimGraph.timgraph;
import TimGraph.Community.VertexPartition;

//import java.util.Formatter;
import TimUtilities.JavaColours;

/**
 * Package to deal with GraphViz (dot, neato etc) format, usually .gv files.
 * <br>TODO outputEdgeCommunity
 * @author time
 */
public class GraphViz {
    
   
   
    /**
     * Constructor.
     * 
     */
    public GraphViz(){
      
      
    }
    
/**
 * Outputs a GraphViz format file.
 * @param PS PrintStream
 * @param tg network
 * @param edgeLabels true if want to add labels to edges in form of the edge number
 */
    public void output(PrintStream PS, timgraph tg, boolean edgeLabels){
       // PS.println("comments");
        String name="";
        name="\""+tg.outputName.getNameRoot()+"\"";
        PS.println("graph "+name+"{");
        // Here put global definitions
        //vertexList(PS, tg, vertexC);
        edgeList(PS, tg, edgeLabels);
        PS.println("}");
    }
/**
 * Outputs a GraphViz format file.
 * @param PS PrintStream
 * @param tg network
 * @param vertexC the best vertex partition
 * @param edgeLabels true if want to add labels to edges in form of the edge number
 */
    public void outputVertexCommunity(PrintStream PS, timgraph tg, VertexPartition vertexC, boolean edgeLabels){
       // PS.println("comments");
        String name="";
        name="\""+tg.outputName.getNameRoot()+"\"";
        PS.println("graph "+name+"{");
        // Here put global definitions
        vertexList(PS, tg, vertexC);
        edgeList(PS, tg, edgeLabels);
        PS.println("}");
    }
    
    
    public void vertexList(PrintStream PS, timgraph tg, VertexPartition vertexC){
        JavaColours nc = new JavaColours(vertexC.getNumberOfCommunities()+2,true);
        nc.printAllColours(System.out);
        for (int v=0; v<tg.getNumberVertices(); v++){
            //int c= vertexC.getCommunity(v);
            PS.println(v+"  [ color = \"#"+nc.RGB(vertexC.getCommunity(v)+1)+"\" ]");
        }
    }
        
         
    public void edgeList(PrintStream PS, timgraph tg, boolean gvLabels){
        JavaColours nc = new JavaColours(20,false); //(edgeC.getEmptyCommunity(),true);
        nc.printAllColours(System.out);
        
        String edgeString = " -- ";
        if (tg.isDirected()) edgeString = " -> ";
        
        String ac="";
        String aw="";
        String al="";
        int s=-1;
        int t=-1;
        for (int e=0; e<tg.getNumberStubs(); e=e+2){
            s=tg.getVertexFromStub(e);
            t=tg.getVertexFromStub(e+1);
            ac=nc.RGB(e+1);
            System.out.println("e="+e+", c string = "+ac);
            if (tg.isWeighted() ) aw="\n weight="+tg.getEdgeWeight(e);
            if (gvLabels) al = "\n label = \""+e+"\"";
            PS.println(s+edgeString+t+" [ \n color = \"#"+ac+"\"  "+aw+al+"\n ]");
        }
    }
    
    private void printSingleLineComment(PrintStream PS, String s){
        PS.println("// "+s);
    }

    private void printMultiLineComment(PrintStream PS, String [] s){
        PS.println("/*");
        for (int i=0; i<s.length;i++) PS.println(" * "+s[i]);
        PS.println("*/");
        
    }
            
    
}
