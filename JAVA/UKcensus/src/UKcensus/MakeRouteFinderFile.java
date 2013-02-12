/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UKcensus;

import TimGraph.Coordinate;
import TimGraph.timgraph;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Creates a distance file from vertex positions.
 * <p>Output suitable for RouteFinder package.
 * Note that only sensible if the edges indicate nearest neighbours.
 * @author time
 */
public class MakeRouteFinderFile {
    
     public static void main(String[] args){
         
         String [] siteNetworkArgs = {"-finEWwardsinputELS.dat", 
             "-fid/PRG/networks/timgraph/", 
             "-fisinput/UKwards/", 
             "-fod/PRG/networks/timgraph/", 
             "-fosoutput/UKwards/",
             "-o0"};
         
         // first read in basic map        
         timgraph tg = new timgraph();
         tg.parseParam(siteNetworkArgs);
         if (!tg.isVertexEdgeListOn()) {
             throw new RuntimeException("*** Vertex Edge List NOT on and it is required, use -gvet option ***\n");
         }

         //TimUtilities.FileUtilities.FileNameSequence fns;
         
         tg.setNameRoot(); // needed for ouput files
         tg.setNetworkFromInputFiles(); // this will read in the network
         //tg.setNetworkFromManyInputFiles(int xColumn, int yColumn, int nameColumn, boolean headerOn, boolean infoOn)
         //tg.printParametersBasic();
         //tg.calcStrength();
         
         // now set/get edge separations while outputing route file
         String fullfilename=tg.getNameRootFullPathOutput()+"inputroute.dat";
         PrintStream PS;
         FileOutputStream fout;
         boolean infoOn=true;
         if (infoOn) {
            System.out.println("Writing route file to " + fullfilename);
         }
         try {
            fout = new FileOutputStream(fullfilename);
            PS = new PrintStream(fout);
            String sep = timgraph.SEP;
            int sv=0;
            int tv=0; // source and target vertex index
            Coordinate scoord, tcoord;
            //ArrayList<Double> edgeDistance = new ArrayList();
            Double d;
            for (int stub=0; stub<tg.getNumberStubs(); stub++){
                //EdgeValue edge = tg.getEdgeWeightAll(stub);
                try{
                    sv=tg.getVertexFromStub(stub++);
                    tv=tg.getVertexFromStub(stub);
                    scoord = tg.getVertexPosition(sv);
                    tcoord = tg.getVertexPosition(tv);
                    d = Coordinate.calcLength(scoord, tcoord);
                } catch (RuntimeException e){
                    d=timgraph.DUNSET;
                }
                //edgeDistance.add(d);
                PS.println(String.format("%6d",sv)+sep+String.format("%6d",tv)+sep+String.format("%8.3f",d)+sep+"0");
            }
            
            
            if (infoOn) {
                System.out.println("Finished writing route file to " + fullfilename);
            }
            try {
                fout.close();
            } catch (IOException e) {
                System.err.println("*** File Error with " + fullfilename + ", " + e.getMessage());
            }

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening route file " + fullfilename + ", " + e.getMessage());
            return;
        }
        return;
         
     }
    
}
