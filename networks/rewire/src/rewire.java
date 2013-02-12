/*
 * rewire.java
 *
 * Created on 09 December 2005, 18:27
 */

/**
 *
 * @author time
 */


//import cern.colt.list.IntArrayList;
//import cern.colt.list.DoubleArrayList;
//import cern.colt.list.ObjectArrayList;

//import java.util.*;
import java.util.Date;
import java.util.Random; //p524 Schildt
//import java.util.*;
//import java.util.AbstractSet;
//import java.lang.Object.*;
//import java.lang.Math.*;

import java.io.*;


public class rewire {
  
    String Version = "rewire:051209";
    String SEP = "\t ";
    String commentcharacter = "#"; 
    timgraph tg; 
    int numberartifacts=6;
    int numberindividuals=3;
    
    
    /** Creates a new instance of rewire */
    public rewire() 
    {
        tg = new timgraph();
    }
    
    public static void main(String[] args) 
    {
        long initialtime;
              

        // set up initial graph
        Date date = new Date();
        System.out.println("\n***********************************************************");
        System.out.println("       STARTING rewire on "+date);
        rewire rw= new rewire();
        rw.bentley(args);
    } //eo main
    

    
    
      public void bentley(String[] args)
    {
        
          
        timgraph tg = new timgraph(); 
        tg.numevents=1000;
        tg.probpp=0.5;
        tg.dirname="/PRG/networks/rewire/";
        if (parseParam(args)>0) return;
//        tg.weightedEdges=true;
        tg.printParam();
//        tg.initialgraph=5;
        numberindividuals = 100;
        numberartifacts=3;
        tg.setBiPartite(numberindividuals, numberartifacts, 0);
        
        tg.calcNumberVertices();
        tg.calcNumberEdges();
        tg.setInitTime();
                
        
        if (tg.TotalNumberVertices<20) tg.printNetwork();
//        System.out.println("--- Finished walk " + tg.dirname + tg.nameroot + " in " + walktimetaken +"sec."  );
        // finished main network generation
         
        tg.DoBPCopyModel(0.01);
        if (tg.TotalNumberVertices<20) tg.printNetwork();
        
        
        
            
        }  
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void test1(String[] args)
    {
        
        tg.numevents=5;
        tg.dirname="/PRG/networks/ties/";
        if (tg.parseParam(args)>0) return;
        tg.weightedEdges=true;
        tg.printParam();
        tg.initialgraph=5;
        tg.setNetwork();
        tg.calcNumberVertices();
        tg.calcNumberEdges();
        tg.setInitTime();
                
        double walktimetaken=0.0;
//        tg.edgegenerator=1; //random graph
//        tg.connectivity=1.0; // critical for connectedness
//        walktimetaken= tg.addAll(0.01);
//        walktimetaken = tg.currentRunTime();
//        tg.generalOutput(commentcharacter,walktimetaken);
        if (tg.TotalNumberVertices<20) tg.printNetwork();
//        System.out.println("--- Finished walk " + tg.dirname + tg.nameroot + " in " + walktimetaken +"sec."  );
        // finished main network generation
         
        int erewire=tg.Rnd.nextInt(tg.TotalNumberEdges);
        int vnew=tg.Rnd.nextInt(tg.TotalNumberVertices );
        System.out.println(" Rewiring edge "+erewire+" to have new target "+vnew);
        tg.rewireEdgeTarget(erewire, vnew);
        if (tg.TotalNumberVertices<20) tg.printNetwork();
        
            
        }
    
    
    
   
    
// *********************************************************************
    /** Method in walk.  Parses command arguments      
     *  @return Undirected sparse graph
     */
    public int parseParam(String[] ArgList)  {

        
//        System.out.println(args.length+" command line arguments");
        for (int j =0; j<ArgList.length; j++){System.out.println("Argument "+j+" = "+ArgList[j]);}
        
                for (int i=0;i<ArgList.length ;i++){
                    if (ArgList[i].length() <3) {
                        System.out.println("\n*** Argument "+i+" is too short");
                        printUsage();
                        return 3;};
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** Argument "+i+" does not start with -, use -? for usage");
                            return 4;};
                            switch (ArgList[i].charAt(1)) {
                                case 'd': {tg.dirname = ArgList[i].substring(2);
                                break;}
                                case 'e': {tg.numevents = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'f': {tg.nameroot = ArgList[i].substring(2);
                                break;}
//                                case 'g': {tg.initialgraph = Integer.parseInt(ArgList[i].substring(2));
//                                break;}                                
                                case 'i': {tg.infolevel = Integer.parseInt(ArgList[i].substring(2));
                                break;}
//                                case 'l': {steps=Double.parseDouble(ArgList[i].substring(2));
//                                break;}
//                                case 'm': {connectivity =Double.parseDouble(ArgList[i].substring(2));
//                                break;}
                                case 'n': {
                                    if (ArgList[i].charAt(2)=='a' ) numberartifacts=Integer.parseInt(ArgList[i].substring(3));
                                    if (ArgList[i].charAt(2)=='i' ) numberindividuals=Integer.parseInt(ArgList[i].substring(3));
                                break;}
                                case 'o': {tg.outputcontrol = Integer.parseInt(ArgList[i].substring(2));
                                break;}
//                                case 'p': {probnewvertex= Double.parseDouble(ArgList[i].substring(2));
//                                break;}
//                                case 'q': {probrndvertex= Double.parseDouble(ArgList[i].substring(2));
//                                break;}

                                //                              break;}
                                //case 't': {initialGraph=Integer.parseInt(ArgList[i].substring(2));
                                //                              break;}
                                //                    case 'z': {//FullInfoOn=Integer.parseInt(ArgList[i].substring(2));

                                case '?': {printUsage();
                                return 1;}

                                default:{
                                    System.out.println("\n*** Argument "+i+" not known, usage:");
                                    printUsage();
                                    return 2;
                                }
                                
                            }
                }
        
        File dir = new File(tg.dirname);
            if (!dir.isDirectory()) 
            {
                System.out.println("*** Error "+tg.dirname+" is not a directory");
                return 1;
            };
       
            
// use -v input to set boolean walk start variables
//          setWalkmode();
    
    return 0;
    
    }//eo ParamParse

// ........................................................................
     /** Method in rewire.  
     * Gives usage of rewire     
     *  
     */

    public void printUsage()  
    {

        rewire r = new rewire();
        System.out.println("type rewire followed by the following options:");
        System.out.println(" -d<dirname>  directory name default "+r.tg.dirname);
        System.out.println(" -e<int>      number of time steps (events), default "+r.tg.numevents);
        System.out.println(" -f<nameroot> root used for file names, default "+r.tg.nameroot);
//        System.out.println(" -g<int>      initial graph number, default "+r.tg.initialgraph);
        System.out.println(" -i<int>      debugging information level, default "+r.tg.infolevel);
        System.out.println(" -na<int>     number of artifacts default "+r.numberartifacts);
        System.out.println(" -ni<int>     number of individuals default "+r.numberartifacts);
        
//                System.out.println("       -e<#events> -m<connectivity> -l<steps> -p<probnewvertex>");
//                System.out.println("       -g<initialgraph#> -v<mode> -o<outputcontrol> -i<infolevel>\n");
//                System.out.println(" -p<float> probability of adding a new vertex for attachment per event \n");
////                System.out.println(" -q<float> probability of choosing a random old vertex for attachment, else use walk \n");
//                System.out.println(" -ve<int> edge generator: 0 Walk, 1 ER \n");
//                System.out.println(" -vw<int> walk modes \n");
//                System.out.println("vw modes: (vw& 1) ? Starts walks from random vertex : (end of random edge)");
//                System.out.println("        : (vw& 2) ? Starts new walk for every edge : (only for every event)");
//                System.out.println("        : (vw& 4) ? Markovian walk, yes : (or no)");
//                System.out.println("        : (vw& 8) ? Random number edges per vertex, yes : (or no)");
                System.out.println(" -o<int> output modes \n");
                System.out.println("  o modes: (o& 1) ? Degree distribution calc and output on : (off)");
                System.out.println("         : (o& 2) ? Distance calc and output on : (off)");
                System.out.println("         : (o& 4) ? Clustering calc and output on : (off)");
                System.out.println("         : (o& 8) ? Pajek file output on : (off)");
                System.out.println("         : (o&16) ? Component calc and output on : (off)");
                
                
    }

    
}
