/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UKcensus;

import TimGraph.timgraph;
import TimUtilities.FileUtilities.FindFile;
import java.util.ArrayList;
//import TimUtilities.FileUtilities.FileNameSequence;
//import TimUtilities.

/**
 * Make communities from commuting flows.
 * Puts communities from flow analysis on top of network of neighbouring wards.
 * @author time
 */
public class MakeFlowPartitionMaps {
    
     public static void main(String[] args){
         
         String [] siteNetworkArgs = {"-finEWdistinputELS.dat", 
             "-fid/PRG/networks/timgraph/", 
             "-fisinput/UKwards/", 
             "-fod/PRG/networks/timgraph/", 
             "-fosoutput/UKwards/",
             "-o0"};
//         String [] siteNetworkArgs = {"-finEWwardsinputELS.dat", 
//             "-fid/PRG/networks/timgraph/", 
//             "-fisinput/UKwards/", 
//             "-fod/PRG/networks/timgraph/", 
//             "-fosoutput/UKwards/",
//             "-o0"};
         
        //String inputFlowFullFileName=
        //        "/PRG/networks/timgraph/output/UKwards/UKCensus01EWCommutingFlowLouvainQSPi-Pi2500r0inputBVNLS.dat";
                //"/PRG/networks/timgraph/input/UKwards/UKCensus01CommutingFlowinputELS.dat";
        String inputCommunityFullDirectory=
                "/PRG/networks/timgraph/output/UKwards/";
        //String inputCommunityRootName="UKCensus01EWCommutingFlowLouvainQSPi-Pi";
        //String inputCommunityRootName="UKCensus01EWSymmCommFlowLouvainQSout-out";
//        String inputCommunityRootName="UKCensus01EWdistCommFlowLouvainQSPi-Pi";
        String inputCommunityRootName="UKCensus01EWdistSymmCommFlowLouvainQSout-out";
        String inputCommunityExt="inputBVNLS.dat";
                //"/PRG/networks/timgraph/input/UKwards/UKCensus01CommutingFlowinputELS.dat";
        
         // first read in basic map
         
         timgraph tg = new timgraph();
         tg.parseParam(siteNetworkArgs);

         if (!tg.isVertexEdgeListOn()) {
             throw new RuntimeException("*** Vertex Edge List NOT on and it is required, use -gvet option ***\n");
         }

         //tg.setNameRoot(tg.inputName.); // needed for output files
         tg.setNameRoot(tg.getNetworkName()); // needed for output files
         tg.setNetworkFromInputFiles(); // this will read in the network
         //tg.setNetworkFromManyInputFiles(int xColumn, int yColumn, int nameColumn, boolean headerOn, boolean infoOn)
         //tg.printParametersBasic();
         //tg.calcStrength();
         
         // now get a vertex partition to label graph
         //timgraph tg, 
         //String vpFilename="/PRG/networks/timgraph/output/UKwards/UKCensus01EWCommutingFlowLouvainQSPi-Pi2500r0inputBVNLS.dat";
         //ArrayList<String> vpFileNameList= new ArrayList();
         FindFile ff = new FindFile();
         boolean infoOn=true;
         ff.getFileList(inputCommunityFullDirectory, inputCommunityRootName, inputCommunityExt, infoOn);
         int numberFiles = ff.getNumberFiles();
         for (int f=0; f<numberFiles; f++){
             String vpFilename=inputCommunityFullDirectory+ff.getFileName(f);
             //="/PRG/networks/timgraph/output/UKwards/UKCensus01EWCommutingFlowLouvainQSPi-Pi2500r0inputBVNLS.dat";
             int vertexIndexColumn=1;
             int vertexCommunityColumn=2;
             double edgeWeightMinimum=0;
             boolean indexString=true;
             boolean indexStringIsName=true;

             boolean graphMLOutput=true;
             boolean vpheaderOn=false;
             boolean vpInformationOn=true;
             TimGraph.run.MakeVertexPartitionFromVertexPartition.makePartition(tg,  vpFilename,
                                        vertexIndexColumn, vertexCommunityColumn,
                                        edgeWeightMinimum,
                                        indexString, indexStringIsName, 
                                        graphMLOutput,
                                        vpheaderOn, vpInformationOn );

             // output results
         }    
     
     }
    
}
