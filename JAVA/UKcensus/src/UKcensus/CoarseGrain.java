/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UKcensus;

import TimGraph.Coordinate;
import TimGraph.VertexLabel;
import TimGraph.run.BasicAnalysis;
import TimGraph.timgraph;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Routines to Coarse Grain Wards to Districts.
 * <p>Wards codes are nnaaaa where first nnaa is the district code
 * @author time
 */
public class CoarseGrain {

    
    
    public static void main(String [] args){
     String basicFileName="EWwardsinputELS.dat";
     String basicCGRootName="EWdist";
//     String basicFileName="UKCensus01EWCommutingFlowinputELS.dat";
//     String basicCGRootName="UKCensus01EWDistCommFlow";
//     String basicFileName="testCommutingFlow2inputELS.dat";
//     String basicCGRootName="testDistCommutingFlow2";
     
     String [] aList = { "-fin"+basicFileName,"-fid/PRG/networks/timgraph","-fisinput/UKwards","-fosoutput", "-o0"};
     if (args.length>0) {
            aList=args;
        }

     boolean infoOn=false; //true;
     processEdgeData(basicFileName, basicCGRootName, aList, infoOn);
     
    }
    
    /**
     * 
     * @param basicFileName
     * @param basicCGRootName
     * @param aList
     * @param outputEdgeCGFullRootName
     * @param sep
     * @param infoOn 
     */
    static public void processEdgeData(String basicFileName,
            String basicCGRootName,
            String [] aList,
            boolean infoOn){
        
//     String basicFileName="UKCensus01EWSymmCommFlowinputELS.dat";
//     String [] aList = { "-fin"+basicFileName,"-fisinput/UKwards","-fosoutput/UKwards", "-o0"};

     timgraph tg = new timgraph();
     tg.parseParam(aList);
//     tg.outputControl.printMode(System.out, " ");
        
     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFiles();
     tg.printParametersVeryBasic();
     
     if (infoOn) {tg.printNetwork(true);}
     
     // make new coarse grained vertex set
     //TreeSet cgVertexSet = new TreeSet();
     TreeMap<String,Integer> cgNameToNumberMap = new TreeMap();
     // now coarse grain
     
     // make list of cg vertex names
     String newName;
     for (int v=0; v<tg.getNumberVertices(); v++){
       newName=tg.getVertexName(v).substring(0, 4);  
       //cgVertexSet.add(newName);
       cgNameToNumberMap.put(newName, -1);
     }
     // assign index to cg vertices
     int cgIndex=0;
     for (String nn: cgNameToNumberMap.keySet()){
       cgNameToNumberMap.put(nn,cgIndex++);
     }
     // make position of cg vertices
     ArrayList<Coordinate> [] coord;
     coord = new ArrayList[cgNameToNumberMap.keySet().size()];
     for (int c=0; c<coord.length; c++){coord[c] = new ArrayList();}
     // now get all microscopic coordinates for each coarse grain vertex
     Coordinate c;
     String cgName;
     int newv;
     for (int v=0; v<tg.getNumberVertices(); v++){
       c=tg.getVertexPosition(v);
       cgName=tg.getVertexName(v).substring(0, 4);
       cgIndex = cgNameToNumberMap.get(cgName);
       coord[cgIndex].add(c);
     }
       
     timgraph cgg =new timgraph();
     cgg.parseParam(aList); // this is set to read in parameters for the old file but name is wrong
     cgg.setNameRoot(basicCGRootName);// correct cgg name
     cgg.setMaximumVertices(coord.length);
     cgg.setMaximumStubs(tg.getNumberStubs());
     cgg.setNetwork(); // initialises empty graph
     
     // Add new vertices with number and position
     // Add up microscopic coordinates and normalise to get new coordinate.
     Coordinate nc =new Coordinate();
     VertexLabel cgvLabel;
     for (int n=0; n<coord.length; n++){
         nc.set(0,0,0);
         for (Coordinate cc:coord[n]){
             nc.add(cc);
         }
         nc.scale((1.0/((double)coord[n].size() )));
         cgvLabel= new VertexLabel(n); 
         cgvLabel.setPosition(nc);
         cgg.addVertex(cgvLabel);
     }
     // now set names of vertices
     for (String cgn: cgNameToNumberMap.keySet()){
         cgIndex = cgNameToNumberMap.get(cgn);
         cgg.setVertexName(cgIndex,cgn);
     }
     // vertex properties complete
     //tg.printNetwork(true);
     
     
     // combine edges
     int source, newSource, target, newTarget;
     double w;
     for (int stub=0; stub<tg.getNumberStubs(); stub++){
         w=tg.getEdgeWeight(stub);
         source=tg.getVertexFromStub(stub++);
         newSource=cgNameToNumberMap.get(tg.getVertexName(source).substring(0,4) );
         target=tg.getVertexFromStub(stub);
         newTarget=cgNameToNumberMap.get(tg.getVertexName(target).substring(0,4) );
         cgg.increaseEdgeWeight(newSource,newTarget, w);
         
         if (infoOn) {
             int newEdge = cgg.getFirstEdgeGlobal(newSource,newTarget);
             double newEdgeWeight =cgg.getEdgeWeight(newEdge);
             System.out.println("stub: "+stub+" => "+source+"("+tg.getVertexName(source)+") -> "+target+"("+tg.getVertexName(target)+") = "+w+"\n !!! "+newSource+" -> "+newTarget+" = "+newEdgeWeight);
         }
     }
     BasicAnalysis.analyse(cgg);
     if (infoOn) cgg.printNetwork(true);
    }
    
}
