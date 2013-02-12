/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package UKcensus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Deals with the flow data
 * @author time
 */
public class FlowData {

    public static void main(String[] args) {
        //tidyUpFlowTable();
        reconcileCommutingFlowData();
    }
    /**
     * Reconciles Commuting flow and position lists.
     * UKwardsSEinputVertices.dat and UKCensus01CommutingFlowinputELS.dat
     * Assumes flow data in <tt>inputFlowFullFileName</tt> has 3 columns 
     * with first and second column containing vertex labels.   
     * Assumes file containing limited name data in 
     * <tt>inputNameLimitFullFileName</tt> has names needed in first columns.
     * <p>Note that this is duplicated by TimGraph.run.FilterEdgesUsingVertexList 
     * and the other seems to have been used as the final successful version.
     * @see TimGraph.run.FilterEdgesUsingVertexList
     * @see UKcensus.FlowData#reconcileCommutingFlowData(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean) 
     */
    static public void reconcileCommutingFlowData(){
        String inputFlowFullFileName=
                "/NETWORK_DATA/UKCensus/uk_trips_pairinputELS.dat";
                //"/PRG/networks/timgraph/input/UKwards/UKCensus01CommutingFlowinputELS.dat";
        //C:\PRG\networks\timgraph\input\UKwards
        String inputNameLimitFullFileName=
                "/PRG/networks/timgraph/input/UKwards/UKCensus01EWinputVertices.dat";
                //"/PRG/networks/timgraph/input/UKwards/UKwardsSEJDinputVertices.dat";
                
        String outputFlowFullRootName=
                "/PRG/networks/timgraph/output/UKwards/UKCensus01EW";
        //CommunityFlowinputELS.dat";
//        String originalVerticesFullFileName=
//                "/PRG/networks/timgraph/output/UKwards/UKCensus01EWoriginalVertices.dat";
//        String acceptedVerticesFullFileName=
//                "/PRG/networks/timgraph/output/UKwards/UKCensus01EWacceptedVertices.dat";
//        String rejectedVerticesFullFileName=
//                "/PRG/networks/timgraph/output/UKwards/UKCensus01EWrejectedVertices.dat";
//        String missingVerticesFullFileName=
//                "/PRG/networks/timgraph/output/UKwards/UKCensus01EWmissingVertices.dat";
        String sep="\t";
        boolean infoOn=true;
        reconcileCommutingFlowData(inputFlowFullFileName, 
            inputNameLimitFullFileName,
            outputFlowFullRootName, 
            sep, infoOn);
    }
     

    /**
     * Reconciles Commuting flow and position lists.
     * UKwardsSEinputVertices.dat and UKCensus01CommutingFlowinputELS.dat
     * Assumes flow data in <tt>inputFlowFullFileName</tt> has 3 columns 
     * with first and second column containing vertex labels.   
     * Assumes file containing limited name data in 
     * <tt>inputNameLimitFullFileName</tt> has names needed in first columns.
     * @param inputFlowFullFileName flow file to be reduced
     * @param inputNameLimitFullFileName file with limited set of names required
     * @param outputFlowFullFileName output file for reconciled flows
     * @param acceptedVertexFullFileName file name for accepted vertex list
     * @param rejectedVertexFullFileName file name for rejected vertex list
     * @param sep separation character
     * @param infoOn  true for basic info
     */
    static public void reconcileCommutingFlowData(String inputFlowFullFileName, 
            String inputNameLimitFullFileName,
            String outputFlowFullRootName,
            String sep, boolean infoOn){
        //first read in complete flow edge list
        String cc="";
        int [] flowColumnReadList = {1,2,3}; //no column is assumed
        //String [] columnLabelList = null;
        boolean forceLowerCase=false;
        ArrayList<ArrayList<String>> flowVertexList = 
                TimGraph.io.FileInput.readStringColumnsFromFile(inputFlowFullFileName,
                cc,  flowColumnReadList, null, forceLowerCase, false);
        // produce complete list of allowed vertex labels 
        int [] positionColumnReadList = null;
        String [] columnLabelList = {"Name"}; // assumes column header row
        ArrayList<ArrayList<String>> positionVertexList = 
                TimGraph.io.FileInput.readStringColumnsFromFile(inputNameLimitFullFileName,
                cc,  positionColumnReadList, columnLabelList, forceLowerCase, false);
        
        TreeSet<String> positionVertexSet=new TreeSet();
        positionVertexSet.addAll(positionVertexList.get(0));
        PrintStream PS;
        FileOutputStream fout;
        int numberFlowsKept=0;
        TreeSet<String> originalVertexSet=new TreeSet();
        TreeSet<String> rejectedVertexSet=new TreeSet();
        TreeSet<String> acceptedVertexSet=new TreeSet();
        TreeSet<String> missingVertexSet= null;
        String outputFlowFullFileName=outputFlowFullRootName+"CommuterFlowinputELS.dat";
        if (infoOn) System.out.println("Writing incidence matrix vertex partition information file to "+ outputFlowFullFileName);
        try {
            fout = new FileOutputStream(outputFlowFullFileName);
            PS = new PrintStream(fout);
            ArrayList<String> sourceList=flowVertexList.get(0);
            ArrayList<String> targetList=flowVertexList.get(1);
            ArrayList<String> flowList=flowVertexList.get(2);
            String s,t;
            boolean rejectFlow=false;
            for (int i=0; i<sourceList.size(); i++){
                rejectFlow=false;
                s=sourceList.get(i);
                t=targetList.get(i);
                originalVertexSet.add(s);
                originalVertexSet.add(t);                
                if (!positionVertexSet.contains(s)) {
                    rejectedVertexSet.add(s);
                    rejectFlow=true;
                }
                if (!positionVertexSet.contains(t)) {
                    rejectedVertexSet.add(t);
                    rejectFlow=true;
                }
                if (!rejectFlow) {
                    acceptedVertexSet.add(s);
                    acceptedVertexSet.add(t);
                    PS.println(s+sep+t+sep+flowList.get(i));
                    numberFlowsKept++;
                }
            }
            if (infoOn) {
                System.out.println("Finished writing vertex information file to "+ outputFlowFullFileName);
                System.out.println("Kept "+ numberFlowsKept +" flows of "+sourceList.size());
                System.out.println("Original "+ originalVertexSet.size() +" vertices");
                System.out.println("Accepted "+ acceptedVertexSet.size() +" vertices");
                System.out.println("Rejected "+ rejectedVertexSet.size() +" vertices");
                System.out.println("Number of acceptable vertices in keeping set "+positionVertexSet.size());
                // now modify accepted list from positions to see what are NOT in flows
                //positionVertexSet.removeAll(acceptedVertexSet);
                //missingVertexSet=positionVertexSet;
                missingVertexSet=new TreeSet();
                for (String v:positionVertexSet) if (!acceptedVertexSet.contains(v)) missingVertexSet.add(v);
                System.out.println("Number of acceptable vertices missing in new flow list "+missingVertexSet.size());
            }
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +outputFlowFullFileName+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+outputFlowFullFileName+" "+e.getMessage());
            return;
        }    
            // output original vertex list
        TimGraph.io.FileOutput.Collection(originalVertexSet, 
                outputFlowFullRootName+"originalVertices.dat", null, sep, infoOn);    
            // output accepted filelist
        TimGraph.io.FileOutput.Collection(acceptedVertexSet, 
                outputFlowFullRootName+"acceptedVertices.dat", null, sep, infoOn);    
            // output rejected filelist
        TimGraph.io.FileOutput.Collection(rejectedVertexSet, 
                outputFlowFullRootName+"rejectedVertices.dat", null, sep, infoOn);    
            // output missing filelist
        if (missingVertexSet !=null) 
            TimGraph.io.FileOutput.Collection(missingVertexSet, 
                outputFlowFullRootName+"missingVertices.dat", null, sep, infoOn);    

        String acceptabilityFullFileName=outputFlowFullRootName+"acceptability.dat";
        if (infoOn) System.out.println("Writing acceptability vertex list file to "+ acceptabilityFullFileName);
        try {
            fout = new FileOutputStream(acceptabilityFullFileName);
            PS = new PrintStream(fout);
            for (String v: positionVertexSet){
                    PS.println(v+sep+(acceptedVertexSet.contains(v)?"F":"X"));
                }
            try{ fout.close ();   
               } catch (IOException e) { System.err.println("*** File Error with " +acceptabilityFullFileName+" "+e.getMessage());}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+acceptabilityFullFileName+" "+e.getMessage());
            return;
        }    

            return;
        
    }   
    
    /**
     * This tidies up flow data lists.
     * @see UKcensus.FlowData#processRawFlowData(java.lang.String, java.lang.String, double, java.lang.String, boolean) 
     */
     public static void tidyUpFlowTable() {
        char fileSep=java.io.File.separatorChar;
        String inputFullFileName="/PRG/JAVA/UKCensus/input/FlowTable.txt";
                //System.getProperty("user.dir")+fileSep+"input"+fileSep+"FlowTable.txt";
        String outputRootName="/PRG/JAVA/UKCensus/output/FlowTable";
                //System.getProperty("user.dir")+fileSep+"output"+fileSep+"UKCensusFlow";
        String sep="\t";
        boolean infoOn=true;
        double tolerance=0.1;
        processRawFlowData(inputFullFileName, outputRootName,
            tolerance, sep, infoOn);
    }
    
    /**
     * Reads and corrects flow data list.
     * Designed for original <tt>FlowTable.txt</tt> file received 26/09/12.
     * Input is list of source, target (strings) and flow.
     * Takes first 6 characters of names to define unique names.
     * Combines edges on this basis.  Checks result is close to integer.
     * Then outputs edges lists and a map of name to index.
     * @param inputFullFileName full file name of input file
     * @param outputRootName full root of output file names
     * @param sep separation character
     * @param infoOn true for basic information.
     */
    static public void processRawFlowData(String inputFullFileName, String outputRootName,
            double tolerance,
            String sep, boolean infoOn){
        String cc="";
        int [] columnReadList = {1,2,3};
        boolean forceLowerCase=false;
        boolean headerOn=true;
        ArrayList<String> flowData = TimGraph.io.FileInput.readStringColumnsFromFile(inputFullFileName,
            cc, columnReadList, forceLowerCase, headerOn, false);
        TreeMap<String,Integer> labelToIndex = new TreeMap();
        // now create label to index map from flow data
        int indexMax=0;
        String s;
        String t;
        String source;
        String target;
        Integer sindex;
        Integer tindex;
        for (int i=0; i<flowData.size(); i++){
            source = flowData.get(i++);
            if (source.length()<6)  throw new RuntimeException("*** ERROR line "+(i/3)+" source label less then 6 char");
            s=source.substring(0, 6);
            sindex=labelToIndex.get(s);
            if (sindex==null) {
                sindex=indexMax++;
                labelToIndex.put(s, sindex);
            }
            target = flowData.get(i++);
            if (target.length()<6)  throw new RuntimeException("*** ERROR line "+(i/3)+" target label less then 6 char");
            t=target.substring(0, 6);
            tindex=labelToIndex.get(t);
            if (tindex==null) {
                tindex=indexMax++;
                labelToIndex.put(t, tindex);
            }
            float dflow = Float.parseFloat(flowData.get(i));
            //double flow = Double.parseDouble(flowData.get(i));
        } // for i
        if (infoOn) System.out.println("--- found "+indexMax+" vertices");

        // create index to label conversion
        //Object [] indexToLabel = labelToIndex.keySet().toArray();
        //String sss = (String) indexToLabel[0];
        int si, ti;
        String [] indexToLabel = new String[indexMax];
        for (String label: labelToIndex.keySet()){
           si=labelToIndex.get(label);
           indexToLabel[si]=label;
        }

        // now we know how many indices are present we can process flow matrix
        float [][] flowMatrix = new float[indexMax][indexMax];
        String fsource;
        String ftarget;
        int SI=8636; //5773;
        int TI=SI;
        for (int i=0; i<flowData.size(); i++){
            fsource= flowData.get(i++);
            source = fsource.substring(0, 6);
            si=labelToIndex.get(source);
            ftarget= flowData.get(i++);
            target = ftarget.substring(0, 6);
            ti=labelToIndex.get(target);
            float dflow = Float.parseFloat(flowData.get(i));
            flowMatrix[si][ti]+=dflow;
            if ((si==SI) && (ti==TI)) System.out.println("... "
                    +SI+"("+fsource+") -> "
                    +TI+"("+ftarget+") = "+dflow);
        }

        

        // now we can round results to be integers
        int nonZero=0;
        int divBy3=0;
        int nonIntFlow=0;
        for (si=0; si<indexMax; si++){
          for (ti=0; ti<indexMax; ti++){
            float f = flowMatrix[si][ti];
            int fint = Math.round(f);
            if (Math.abs(f-fint)>tolerance) {
                nonIntFlow++;
                System.err.println("*** ERROR flow from "
                    +si+"("+indexToLabel[si]+") to "
                    +ti+"("+indexToLabel[ti]+") is not integer "+f);
            }
            flowMatrix[si][ti]=fint;
            if (fint>0) {
                nonZero++;
                if (fint%3==0) divBy3++;
              }
          }
        }
        if (nonIntFlow>0) System.err.println("*** ERROR "
                +nonIntFlow+" entries were not integer within "+tolerance);
        else if (infoOn) System.out.println("--- all flows were integer within "+tolerance);
        if (infoOn) System.out.println("--- found "+nonZero
                +" non-zero entries of possible "+(indexMax*indexMax)
                +" with "+divBy3+" divisible by 3");


        // now we can output edge list results
        String edgeFile= outputRootName+"outputEL.dat";
        TimGraph.io.FileOutput.matrixAsSparseEdgeList(edgeFile, null, null, flowMatrix,
                indexToLabel.length, sep, infoOn);
        edgeFile= outputRootName+"outputELS.dat";
//        TimGraph.io.FileOutput.matrixAsSparseEdgeList(edgeFile, null, indexToLabel, flowMatrix,
//                indexToLabel.length, sep, infoOn);
        String mapFile= outputRootName+"outputName.dat";
        String [] headerLines = new String[1];
        headerLines[0]=TimGraph.VertexLabel.NAMELABEL+sep+TimGraph.VertexLabel.VERTEXINDEXLABEL;
        TimGraph.io.FileOutput.map(labelToIndex, mapFile, headerLines, sep, infoOn);
    }


}
