/*
 * NetworkGenerator.java
 *
 * Created on 01 February 2007, 17:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimGraph;


import java.io.*;
import DistributionAnalysis.DistributionAnalysis;
        
/**
 * Simplified network generator using timgraph classes.
 * @author time
 */
public class NetworkGenerator {
    
    final String NGVERSION = "NG070202";
    String SEP = "\t"; // tab used as a separator in output
    int repeat =2;
    int type =0;
    int numberVertices=100;
    double averageDegree =2;    
    timgraph tg; 
    String fileName = "NetGenDefault";
    String directoryName = "";
    int outputcontrol= 1;  // set to zero to get no output files
    
    int infoLevel=-2;
    DistributionAnalysis ddAnalysis;
        
    
    
    
    /** 
     * Creates a Network of default characteristics.
     */
    public NetworkGenerator() {
    }

    /** 
     * Creates a Network of a given numberVertices.
     *@param numberVertices number of vertices. 
     *@param averageDegree average degree of vertices
     *@param outputNumber number used by timgraph to control output
     *@param fileName name of file
     *@param directoryName name of directory - use forward slashes and end with a slash
     */
    public NetworkGenerator(int type, int numberVertices, double averageDegree, int outputNumber, String fileName, String directoryName) {
        this.type=type;
        this.numberVertices=numberVertices;
        this.averageDegree = averageDegree;
        this.outputcontrol = outputNumber;
        this.fileName = fileName;
        this.directoryName = directoryName;
    }

   /**
     * Create Network and requested files.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        NetworkGenerator ng = new NetworkGenerator();
        ng.parseParam(args);
        ng.printParameters();
        String filenameRoot = ng.fileName;
        ng.ddAnalysis = new DistributionAnalysis (filenameRoot,ng.directoryName,1.1);        
        ng.ddAnalysis.infolevel=2;
        for (int r=0; r<ng.repeat; r++) 
        {
            if (ng.infoLevel>-2) System.out.println("\n *** NetworkGenerator run "+r);
            ng.fileName =filenameRoot +"r"+r;
            ng.makeNetwork();
        }
        
        ng.outputAverageDegreeDistribution();
  
    }

       /**
     * Makes a network of given type.
     */
    public void makeNetwork()
    {
        if (infoLevel>-2) System.out.println("*** Making "+typeString());
        switch (type )
        {
            case 5: make3DTorus(); break;
            case 4: make2DTorus(); break;
            case 3: makeCircle(); break;
            case 2: makeBA(); break;
            case 1: makeRandomExponential(); break;
            case 0: makeER(); break;
            default: System.out.println(" *** Error - no network of type "+type);
        }
        
        if ((numberVertices<30) && (infoLevel>0))
        {
            System.out.println("--- Network ---");
            printNetwork();
        }
        
    }
    
       /**
     * Gives type of network selected.
     */
    public String typeString()
    {
        String s="UNKNOWN";
        switch (type )
        {
            case 5: s= "3D Torus "; break;
            case 4: s= "2D Torus ";  break;
            case 3: s= "Circle "; break;
            case 2: s= "Scale Free"; break;
            case 1: s= "Random Exponential "; break;
            case 0: s= "Random Erdos-Reyni"; break;
            default:
        }
        return s;    
    }
    
    
    /** 
     * Sets up the adjacency matrix for a ER random Graph.
     */
    public void makeER()
    {
         String [] args = new String[7];
         int n=0;
         args[n++]="-e"+numberVertices; // set number of vertices
         args[n++]="-m"+averageDegree/2.0; // set edges added
         args[n++]="-ve1"; // generate ER graph
         args[n++]="-gn1"; // start with graph of one vertex
         args[n++]="-lw0"; // generation walk length 0
         args[n++]="-vw3"; // start walks from new random vertex every time, fixed length, fixed edge number added
         args[n++]="-p1.0"; // always add new vertex at every event
         makeNetwork(args);
    }

     /** 
     * Sets up the adjacency matrix for a Random Exponential graph (exponential tail).
     */
    public void makeRandomExponential()
    {
         String [] args = new String[7];
         int n=0;
         args[n++]="-e"+numberVertices; // set number of vertices
         args[n++]="-m"+averageDegree/2.0; // set edges added
         args[n++]="-gn2"; // start with graph of two vertices with two connecting edges
         //start with graph of one vertex
         args[n++]="-ve0"; // generate walk graph
         args[n++]="-lw0"; // generation walk length 0
         args[n++]="-vw2"; // start walks from new random vertex every time, fixed length walk, fixed edge number added
         args[n++]="-p1.0"; // always add new vertex at every event
         makeNetwork(args);
    }
    
     /** 
     * Sets up the adjacency matrix for a BA gamma=3.0 (power law tail).
     */
    public void makeBA()
    {
         String [] args = new String[7];
         int n=0;
         args[n++]="-e"+numberVertices; // set number of vertices
         args[n++]="-m"+averageDegree/2.0; // set edges added
         args[n++]="-gn2"; // start with graph of two vertices with two connecting edges
         args[n++]="-ve0"; // generate walk graph
         args[n++]="-lw0"; // generation walk length 0
         args[n++]="-vw3"; // start walks from new random edge (random end) every time, fixed length walk, fixed edge number added
         args[n++]="-p1.0"; // always add new vertex at every event
         makeNetwork(args);
    }
    
     /** 
     * Sets up the adjacency matrix for a 1D torus.
     */
    public void makeCircle()
    {
         String [] args = new String[6];
         int m = (int) (0.5+averageDegree/2.0);
         int n=0;
         args[n++]="-e"+numberVertices; // set number of vertices
         args[n++]="-m"+m; // set edges added
         args[n++]="-gn-2"; // initial graph type circle
         args[n++]="-gv"+numberVertices; // initial=final number of vertices
         args[n++]="-gm"+m; // Graph has average degree of (2*m)  
         args[n++]="-p1.0"; // always add new vertex at every event
         makeNetwork(args);
    }
 
        /** 
     * Sets up the adjacency matrix for a 2D torus.
     */
    public void make2DTorus()
    {
         String [] args = new String[7];
         int m = (int) (0.5+averageDegree/2.0);
         int n=0;
         args[n++]="-e"+numberVertices; // set number of vertices
         args[n++]="-m"+m; // set edges added
         args[n++]="-gn-4"; // initial graph type 2D torus
         args[n++]="-gv"+numberVertices; // initial=final number of vertices
         args[n++]="-gx-1"; // Make it an approximate square  
         args[n++]="-gm"+m; // Graph has average degree of (2*m)  
         args[n++]="-p1.0"; // always add new vertex at every event
         makeNetwork(args);
    }
 
        /** 
     * Sets up the adjacency matrix for a 3D torus.
     */
    public void make3DTorus()
    {
         String [] args = new String[7];
         int m = (int) (0.5+averageDegree/2.0);
         int n=0;
         args[n++]="-e"+numberVertices; // set number of vertices
         args[n++]="-m"+m; // set edges added
         args[n++]="-gn-6"; // initial graph type 2D torus
         args[n++]="-gv"+numberVertices; // initial=final number of vertices
         args[n++]="-gx-1"; // Make it an approximate square  
         args[n++]="-gm"+m; // Graph has average degree of (2*m)  
         args[n++]="-p1.0"; // always add new vertex at every event
         makeNetwork(args);
    }
 

     /** 
     * Sets up the network.
     *@param args string array of command line arguments for timgraph. 
     */
    private void makeNetwork(String [] args)
    {
        tg = new timgraph(fileName+"tg", directoryName, infoLevel, outputcontrol);
        tg.setNumberEvents(numberVertices); // do not change!
        tg.parseParam(args);
        if (tg.getInitialVertices() >numberVertices) tg.setInitialVertices(numberVertices);
        tg.doOneRun(1); // This will inialise graph and only if need more vertices will it do that
//        makeNetwork(tg.TotalNumberVertices, tg.TotalNumberEdges, tg.edgeSourceList, tg.directedGraph);
        ddAnalysis.addOneDD(tg.DDtotal.ddarr, fileName+"tg", "#", "dd.dat", "lbdd.dat", true);
        
    } // eo makeNetwork
    
     /** 
     * Outputs the average degree distribution.
     */
    private void outputAverageDegreeDistribution()
    {
     ddAnalysis.FileOutputTotDD(this.fileName+"tot.DD.dat","");   
    }

            // -----------------------------------------------------------------------       
    /**
     * Prints network to standard output (screen).
     */
    public void printNetwork()  
    {
          tg.printNetwork(true);
    }

    
    
// *********************************************************************
    /** 
     * Parses command arguments.
     * ArgList array of strings containing arguments
     *  @return 0 if no problem 
     */
    public int parseParam(String[] ArgList)  {
//        System.out.println(args.length+" command line arguments");
        if (infoLevel>-1) for (int j =0; j<ArgList.length; j++){System.out.println("NG Argument "+j+" = "+ArgList[j]);}
        for (int i=0;i<ArgList.length ;i++){
                    if (ArgList[i].length() <3) {
                        System.out.println("\n*** NF Argument "+i+" is too short");
                        printUsage();
                        return 3;}
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** NG Argument "+i+" does not start with -, use -? for usage");
                            return 4;}
                            switch (ArgList[i].charAt(1)) {
                                case 'f': {
                                    if (ArgList[i].charAt(2)=='n' ) this.fileName = ArgList[i].substring(3);
                                    if (ArgList[i].charAt(2)=='d' ) this.directoryName = ArgList[i].substring(3);
                                break;}
                                case 'k': {this.averageDegree = Double.parseDouble(ArgList[i].substring(2));
                                break;}
                                case 'n': {this.numberVertices = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'o': {this.outputcontrol = (Integer.parseInt(ArgList[i].substring(2)));
                                break;}
                                case 'r': {this.repeat = (Integer.parseInt(ArgList[i].substring(2)));
                                break;}
                                case 't': {this.type = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'x': {
                                    if (ArgList[i].charAt(2)=='i' ) infoLevel = Integer.parseInt(ArgList[i].substring(3));
                                    break;
                                }
                                case '?': {printUsage();
                                return 1;}
                                default:{
                                    System.out.println("\n*** NG Argument "+i+" "+ArgList[i]+ " not known, usage:");
                                    printUsage();
                                    return 2;
                                } 
                            }
                }
        // test directory name
        File dir = new File(this.directoryName);
            if (!dir.isDirectory()) 
            {
                System.out.println("*** Error "+this.directoryName+" is not a directory");
                return 1;
            }
            
    return 0;
    
    }//eo ParamParse
    
    
// -------------------------------------------------------------------
     /** 
      * Gives usage of NetworkGenerator.
     */
   public void printUsage()  {
        NetworkGenerator temp = new NetworkGenerator();
                System.out.println("OPTIONS for NetworkGenerator version "+NGVERSION+"\n");
                System.out.println(" -fn<filenameroot> root of filename, default "+temp.fileName);
                System.out.println(" -fd<dirname>      directory using forward slashes, default "+temp.directoryName);
                System.out.println(" -k<float> average degree, default "+temp.averageDegree);
                System.out.println(" -n<int> number of vertices, default "+temp.numberVertices);
                System.out.println(" -o<int> output modes (see timgraph), default "+temp.outputcontrol);
                System.out.println(" -r<int> number of times to repeat, default "+temp.repeat);
                System.out.println(" -t<int> type of network, default "+temp.type+" = "+temp.typeString());
                System.out.println(" -xi<int> information level, -2 very quiet, 0 normal, >0 debugging, default "+temp.infoLevel+" = "+temp.typeString());
                System.out.println(" -?      this help screen");
                System.out.println("Network Types:-");
                for (int t=0; t<6; t++) {
                    temp.type=t;
                    System.out.println("       "+t+" = "+temp.typeString());
                }
                
    } // eo printUsage

 // -----------------------------------------------------------------------       
    /**
     * Prints parameters to screen.
     */
    void printParameters()  {
        printParameters(System.out);
        return;
    }
       
  // -----------------------------------------------------------------------       
    /**
     * Prints parameters.
     *@param PS printstream
     */
    void printParameters(PrintStream PS)  {
        PS.println("NetworkGenerator version "+NGVERSION);
        PS.println("   Type of Network "+SEP+this.type+SEP+this.typeString());
        PS.println("Number of Vertices "+SEP+this.numberVertices);
        PS.println("    Average Degree "+SEP+this.averageDegree);
        PS.println("      Output Modes "+SEP+this.outputcontrol);
        PS.println("         File Name "+SEP+this.fileName);
        PS.println("    Directory Name "+SEP+this.directoryName);
        PS.println("    No. of repeats "+SEP+this.repeat);
        return;
    }
       
    
}
