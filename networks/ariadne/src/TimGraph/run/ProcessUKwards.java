/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.Coordinate;
import TimGraph.OutputMode;
import TimGraph.io.FileInput;
import TimGraph.timgraph;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Process UKwards data from Elsa Acaute
 * @author time
 */
public class ProcessUKwards {
    final static String SEP = "\t";
    static String basicroot="UNSET";

    static timgraph tg;
    static int infoLevel=0;

        /**
     * Basic routine to analyse a given graph.
     * <p>BasicAnalysis arguments are just the usual <tt>timgraph</tt> arguments.
     * Specify input file name and graph characteristics using timgraph arguments
     * and the outputs will be those defined by the <tt>OutputControl</tt> class.
     * @param args <tt>timgraph</tt> the command line arguments
     * @see TimGraph.timgraph#printUsage
     * @see TimGraph.OutputMode
     */
    public static void main(String[] args)
    {
      System.out.println("ProcessUKwards Arguments:");// just specify file name and characteristics using timgraph arguments alone");
      //System.out.println("                         Output is controled by OutputMode argument -oNNN");
      //OutputMode o = new OutputMode(255);
      //o.printUsage(System.out,"");

     String basicFileName="UKwardsSEinputELS.dat";
     //String basicFileName="UKwardsDEinputELS.dat";
     String [] aList = { "-fin"+basicFileName,"-fisinput/UKwards","-fosoutput/UKwards", "-o255"};
//
//
     if (args.length>0) aList=args;

     tg = new timgraph();
     tg.parseParam(aList);
     tg.outputControl.printMode(System.out, " ");

     tg.setNameRoot(tg.inputName.getNameRoot());
     tg.setNetworkFromInputFiles();

     //checkMultipleEdges();
     //calculateEdgeDistance();
     processDensities();


     //System.out.println("Output: "+tg.outputControl.getModeString(" "));


      //if (tg.getNumberVertices()<20) tg.printNetwork(true);
       //BasicAnalysis.analyse(tg);
    }


    /**
     * Looks for multiple edges in edge list file
     */
    static public void processDensities(){
        String oldEnding =tg.inputName.getNameEnd();
        tg.inputName.setNameEnd("inputDensity.dat");
        String fullFileName =tg.inputName.getFullFileName();
        String cc=timgraph.COMMENTCHARACTER;

        // read in list of vertex names and density
        int [] columnReadList = {1,2};
        String [] columnLabelList = {"name","Density"};
        boolean forceLowerCase=false;
        boolean headerOn=false;
        boolean infoOn=false;
        int columnIndex=1;
        int columnLabel=2;
        //FileInput.readStringIndexLabelList(fullfilename, columnIndex, columnLabel,  indexL, labelL, headerOn);
        ArrayList<ArrayList<String>> outputL = FileInput.readStringColumnsFromFile(fullFileName,
            cc, columnReadList, null, forceLowerCase, infoOn);
        // convert to a map from bname to density
        ArrayList<String> nameL = outputL.get(0);
        ArrayList<String> densityL = outputL.get(1);
        String name;
        double d =-1;
        HashMap<String,Double> nameToDensity = new  HashMap();
        for (int r=0; r<outputL.get(0).size(); r++){
            name=nameL.get(r);
            d=Double.parseDouble(densityL.get(r));
            nameToDensity.put(name, d);
        }
        // now update each weight
        int vs=-1;
        int vt=-1;
        String vsl="";
        String vtl="";
        Coordinate ct;
        double vsd;
        double vtd;
        double w=-1;
        String el="";
        for (int s=0; s<tg.getNumberStubs(); s++){
            vs=tg.getVertexFromStub(s++);
            vsl=tg.getVertexLabel(vs).getName();
            vsd=nameToDensity.get(vsl);
            vt=tg.getVertexFromStub(s);
            vtl=tg.getVertexLabel(vt).getName();
            vtd=nameToDensity.get(vtl);
            w=1.0-Math.abs(vsd-vtd)/(vsd+vtd);
            tg.setEdgeWeight(s, w);
            //PS.println(el+SEP+d+SEP+(d/maxDistance));
        }
        tg.outputName.appendToNameRoot("JD"); // jaccard density
        BasicAnalysis.analyse(tg);
    }
    /**
     * Looks for multiple edges in edge list file
     */
    static public void calculateEdgeDistance(){
        PrintStream PS;
// next bit of code p327 Schildt and p550
        FileOutputStream fout;
        tg.outputName.setNameEnd("outputEdgeDistance.dat");
        String filenamecomplete =tg.outputName.getFullFileName();
        if (infoLevel>-2) System.out.println("Starting to write edge distance to "+ filenamecomplete);
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);

            // first find max and min
            Coordinate cmin=new Coordinate(tg.getVertexPosition(0));
            Coordinate cmax=new Coordinate(tg.getVertexPosition(0));
            Coordinate cs;
            for (int v=0; v<tg.getNumberVertices(); v++){
                cs=tg.getVertexPosition(v);
                cmin.setToMinimum(cs);
                cmax.setToMaximum(cs);
            }

            double maxDistance=Coordinate.calcLength(cmin,cmax);
            PS.println(timgraph.COMMENTCHARACTER+"min="+cmin.toString(",")+ SEP
                    +"max="+cmax.toString(",")+ SEP
                    +"distance="+maxDistance );
            int vs=-1;
            int vt=-1;
            String vsl="";
            String vtl="";
            Coordinate ct;
            double d=-1;
            String el="";
            for (int s=0; s<tg.getNumberStubs(); s++){
                vs=tg.getVertexFromStub(s++);
                cs=tg.getVertexPosition(vs);
                vsl=tg.getVertexLabel(vs).getName();
                vt=tg.getVertexFromStub(s);
                ct=tg.getVertexPosition(vt);
                vtl=tg.getVertexLabel(vt).getName();
                d=Coordinate.calcLength(cs, ct);
                if (vsl.compareTo(vtl)<0) {
                    el = vsl+SEP+vtl+SEP+vsl+vtl;
                } else {
                    el=vtl+SEP+vsl+SEP+vtl+vsl;
                }
                PS.println(el+SEP+d+SEP+(d/maxDistance));
            }

            if (infoLevel>-2) System.out.println("Finished writing edge distance to "+ filenamecomplete);

            try{ fout.close ();
               } catch (IOException e) { System.out.println("File Error with "+ filenamecomplete);}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }

    }
    /**
     * Looks for multiple edges in edge list file
     */
    static public void checkMultipleEdges(){
        ArrayList<String> edgeLL = new ArrayList();
        DoubleArrayList weightLL = new DoubleArrayList();
        IntArrayList labelLL = new IntArrayList();
        // Make a sorted list of the integer labels
        TreeSet<String> sourceLL = new TreeSet();
        String fullFileName = tg.inputName.getFullFileName();
        int columnSource=1;
        int columnTarget=2;
        int columnWeight=3;
        int columnLabel=-1;

        int res = FileInput.readStringEdgeFile(fullFileName,
                columnSource, columnTarget, columnWeight, columnLabel,
                sourceLL, null, edgeLL, weightLL, labelLL) ;
        String v1="";
        String v2="";
        String el;
        int c=-1;
        HashMap<String,Integer> edgeToCount = new HashMap();
        for (int s=0; s<edgeLL.size(); s++){
          v1=edgeLL.get(s++);
          v2=edgeLL.get(s);
          if (v1.compareTo(v2)<0) el = v1+v2; else el=v2+v1;
          if (edgeToCount.containsKey(el)){
              c=edgeToCount.get(el);
              edgeToCount.put(el, ++c);
          } else {edgeToCount.put(el, 1);}
        } // eo for s
        Set<String> edgeLabelSet = edgeToCount.keySet();
        int c0=0;
        int c1=0;
        int c2=0;
        for (String s: edgeLabelSet){
            c=edgeToCount.get(s);
            if (c==0) c0++;
            if (c==1) c1++;
            if (c==2) c2++;
            if (c>2) System.out.println(s+" "+c);
        }

        System.out.println("Found "+c0+" edges occuring 0 times ");
        System.out.println("Found "+c1+" edges occuring 1 times ");
        System.out.println("Found "+c2+" edges occuring 2 times ");
    }

    /**
     * Make label for edge from vertex strings 
     * @param v1 label for vertex 1
     * @param v2 label for vertex 2
     * @return v1+v2 if v1 is before v2, otherwise v2+v1
     */
    public static String edgeLabel(String v1, String v2){
        if (v1.compareTo(v2)<0) return v1+v2; else return v2+v1;
    }
}
