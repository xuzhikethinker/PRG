/*
 * rw.java
 *
 * Created on 18 September 2003, 18:26
 */

import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.utils.*;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.statistics.*;
import edu.uci.ics.jung.statistics.GraphStatistics;
import edu.uci.ics.jung.algorithms.cluster.*;
import edu.uci.ics.jung.io.*;



import cern.colt.list.DoubleArrayList;
import cern.colt.list.ObjectArrayList;

import java.util.*;
import java.util.Date;
import java.util.Random; //p524 Schildt
//import java.util.*;
import java.util.AbstractSet;
import java.util.List;
import java.util.ArrayList;
//import java.lang.Object.*;
//import java.lang.Math.*;

import java.io.*;


/**
 *
 * @author  Tim Evans
 */
public class rw {
    
    UndirectedGraph g = new UndirectedSparseGraph();
    Random Rnd = new Random(0); //Schildt p524
    double adv,ccv,ccgav,adgav,ccgrerr,adgrerr;
    int nn;
    
    /** Constructor -
     */
    public rw() {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        class WSResult {
            int nrw;
            double ad,ad2,cc,cc2;
            
            public WSResult(){}
        }
        
        // class containing all paramters needed for testing
        class Param {
            
            String nameroot;
            int numvertices;
            int connectivity;
            int numrewirings;
            int numruns;
            int dfreq;
            double probnewvertex;
            int infolevel;
            //        boolean SourceVertex;
            //        boolean StartWalkWithVertex;
            int rewiremode;
            int startmode;
            
            // Constructor and default settings
            public Param(){
                nameroot = "test";
                numvertices = 20;
                connectivity =2;
                numrewirings=40;
                numruns=4;
                dfreq = 1;
                probnewvertex= 0.5;
                infolevel = 0;
                startmode = 0;
                rewiremode = 0; // Modes 1=WS, rest = R
            }
            
            public int ParamParse(String[] ArgList){
                
                for (int i=0;i< ArgList.length ;i++){
                    if (ArgList[i].length() <2) {
                        System.out.println("\n*** Argument "+i+" is too short");
                        return 1;};
                        if (ArgList[i].charAt(0) !='-'){
                            System.out.println("\n*** Argument "+i+" does not start with -");
                            return 2;};
                            switch (ArgList[i].charAt(1)) {
                                case 'f': {nameroot = ArgList[i].substring(2);
                                break;}
                                case 'n': {numvertices = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'k': {connectivity =Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'w': {numrewirings=Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'r': {numruns=Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'd': {dfreq = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'p': {probnewvertex= Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'i': {infolevel = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                //                    case 'z': {//FullInfoOn=Integer.parseInt(ArgList[i].substring(2));
                                //                              break;}
                                //                    case 't': {//SourceVertex=Integer.parseInt(ArgList[i].substring(2));
                                //                              break;}
                                case 's': {startmode=Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                case 'm': {rewiremode = Integer.parseInt(ArgList[i].substring(2));
                                break;}
                                default:{
                                    System.out.println("\n*** Argument "+i+" not known, usage:");
                                    Usage();
                                    return 3;
                                }
                                
                            }
                }
                return 0;
            } // eo ParamParse
            
            public void Usage(){
                Param d = new Param();
                System.out.println("....................................................................................");
                System.out.println("Usage: ");
                System.out.println("rw <options> ");
                System.out.println(" where options are -<char><value> separated by space as follows ");
                System.out.println("  -f<nameroot>       Sets root of output files to be nameroot, default "+d.nameroot);
                System.out.println("  -n<numvertices>    Sets number of vertices to be added, default "+d.numvertices);
                System.out.println("  -k<connectivity>   Sets desired connectivity, default "+d.connectivity);
                System.out.println("  -w<numrewirings>   Sets number of rewirings, default "+d.numrewirings);
                System.out.println("  -r<numruns>        Sets number of runs, default "+numruns);
                System.out.println("  -d<dfreq>          Sets freqency of output, default "+d.dfreq);
                System.out.println("  -p<probnewvertex>  Sets probability of adding new vertex each turn, default "+d.probnewvertex);
                System.out.println("  -i<infolevel>      Sets information level, 0 lowest, default "+d.infolevel);
                System.out.println("  -m<rewiremode>     Sets rewire mode, default = "+d.rewiremode);
                System.out.println("                                      0= Fixed one Vertex");
                System.out.println("                                      1= One random edge");
                System.out.println("                                      2= Swap ends between two edges");
                System.out.println("  -s<startmode>      Sets initial graph, default = "+d.startmode);
                System.out.println("                                        0= Lattice 1D");
                System.out.println("                                        1= Lattice 2D");
                System.out.println("                                        2= Random");
                System.out.println("....................................................................................");
                
            } //eo usage
            
            // Print out parameters in param class
            public void print(){
                System.out.println("\n-------------------------------------------------------");
                System.out.println("                        Filename root: "+nameroot);
                System.out.println("       Number of vertices to be added: "+numvertices);
                System.out.println("       Number of edges added per turn: "+connectivity);
                System.out.println("                  Number of rewirings: "+numrewirings);
                System.out.println("                       Number of runs: "+numruns);
                System.out.println("                   Freqency of output: "+dfreq);
                System.out.println(" Prob. of adding new vertex each turn: "+probnewvertex);
                System.out.println("                    Information level: "+infolevel);
                System.out.print("                          Rewire mode: "+rewiremode+" - ");
                switch (rewiremode ){
                    case 0: { System.out.println("Fixed one Vertex");
                    break;
                    }
                    case 1: { System.out.println("One random edge");
                    break;
                    }
                    case 2: { System.out.println("Swap ends between two edges");
                    break;
                    }
                    default:{
                        System.out.println(" *Illegal*");
                    }
                }
                System.out.print("                        Initial Graph: "+startmode+" - ");
                switch (startmode ){
                    case 0: { System.out.println("1D Lattice");
                    break;
                    }
                    case 1: { System.out.println("2D lattice");
                    break;
                    }
                    case 2: { System.out.println("Random");
                    break;
                    }
                    default:{
                        System.out.println(" Unknown*");
                    }
                }
                
                System.out.println("-------------------------------------------------------");
            } //eo print
            
        } // eo class param
        
        
        Date date = new Date();
        System.out.println(date);
        
        //        for (int i=0;i< args.length ;i++){
        //            System.out.println("Parameter "+i+" = "+args[i]);};
        
        Param P = new Param();
        //        System.out.println("Parameters initialised to "); // P.print();
        
        if (P.ParamParse(args)>0) {
            System.out.println("*** ERROR in parameters");
            return;
            // terminate programme here somehow
        };
        P.print();
        String srwtype = new String();
        switch (P.rewiremode ){
            case 0: { srwtype = "vrw";
            break;
            }
            case 1: { srwtype = "erw";
            break;
            }
            case 2: { srwtype = "srw";
            break;
            }
            default:{
                srwtype="*Illegal*";
            }
        }
        String nameroot = P.nameroot+ "n"+Integer.toString(P.numvertices)
        + srwtype +Integer.toString(P.numrewirings)
        + "r"+Integer.toString(P.numruns);
        int i=0;
        String fname;
        int rwt,rwa,rwamax,df,NumEdges;
        rw w = new rw();
        
        df = P.dfreq;
        rwamax=0;
        int WSResultsize = (int) ((P.numrewirings+2.0)*1.2);
        WSResult[] resarr = new WSResult[WSResultsize];
        WSResult rrr = new WSResult();
        rrr.nrw=0;
        
        for (i =0; i<WSResultsize; i++) {
            resarr[i]= new WSResult();
            resarr[i].nrw=0;
            resarr[i].ad=0;
            resarr[i].ad2=0;
            resarr[i].cc=0;
            resarr[i].cc2=0;
        };
        
        double timetaken;
        long initialtime = System.currentTimeMillis();
        long currenttime = initialtime;
        int totsec, timeleft;
        totsec = timeleft = 0;
        
        System.out.println("!!! Starting "+srwtype);
        
        for (int nrun=0; nrun<P.numruns; nrun++){
            timetaken = (double) totsec;
            
            switch (P.startmode ){
                case 0: { P.connectivity = 4;
                w.NewLatticeGraph(P.numvertices);
                break;
                }
                case 1: { System.out.println("Not yet implemented*");
                //w.NewLatticeGraph2D(P.numvertices);
                //break;
                }
                case 2: { NumEdges = P.numvertices * P.connectivity/2;
                w.NewRandomGraph(P.numvertices,NumEdges);
                break;
                }
                default:{
                    System.out.println("*Illegal*");
                }
            }
            fname=nameroot + "x0";
            if ((nrun==0) && (P.infolevel>0)) {
                w.GraphDataPrint();
                w.GraphDataFileOutput(fname, "#", timetaken );
            };
            
            while (w.GraphDataCalculateAll()>0) {};
            
                    int initialedges = w.g.numEdges ();
                    int initialvertices =  w.g.numVertices ();
            
                    System.out.println("Initially - " + initialvertices + " vertices,  "
                                       + initialedges + " edges.");
                    w.GraphDataFileOutput(fname, "#", timetaken );
                    w.GraphDataPrint();
            
            rwt=rwa=0;
            resarr[rwa].nrw++;
            resarr[rwa].ad+=  w.adgav;
            resarr[rwa].cc+=  w.ccgav;
            resarr[rwa].ad2+= w.adgav * w.adgav;
            resarr[rwa].cc2+= w.ccgav * w.ccgav;
            
            // Output data on initial position
            fname = nameroot+ "x"+Integer.toString(rwa);
            w.GraphDataFileOutput(fname, "#", timetaken );
            df = P.dfreq;
            int numrw = df;
            while (rwa<P.numrewirings) {
                if (df>1) {numrw = (rwa -1 ) % df +1;}
                else numrw=1;
                switch (P.rewiremode ){
                    case 0: { //s = "vrw";
                        rwt = w.RewireVertex(numrw);
                        break;
                    }
                    case 1: { //s = "erw";
                        rwt = w.RewireEdge(numrw);
                        break;
                    }
                    case 2: { //s = "srw";
                        rwt = w.RewireEdgePair(numrw);
                        break;
                    }
                    default:{
                        System.out.println("*Illegal*");
                    }
                };
                System.out.println("Tried "+rwa+" rewirings, next was "+rwt);
                if (rwt>0) {
                    rwa+=rwt;
                    System.out.println("Tried "+rwa+" rewirings");
                    if ( (rwa*10) > P.numvertices ) df = P.numvertices/5;
                    else df = P.dfreq;
                    //                          if ( ((rwa % df)>0) && rwa<P.numrewirings ) continue;
                    if (w.GraphDataCalculateAll()>0)  break;
                    if (rwa>rwamax) rwamax =rwa;
                    resarr[rwa].nrw++;
                    resarr[rwa].ad+=w.adgav;
                    resarr[rwa].cc+=w.ccgav;
                    resarr[rwa].ad2+=w.adgav * w.adgav;
                    resarr[rwa].cc2+=w.ccgav * w.ccgav;
                    System.out.print(".");
                    if ((nrun==0) && (P.infolevel>0)) {
                        w.GraphDataPrint();
                        fname=nameroot+ "x"+Integer.toString(rwa);
                        w.GraphDataFileOutput(fname, "#", timetaken );
                    };
                };
                
                
            }; // while rwa<numrewiriings
            
            
            currenttime = System.currentTimeMillis();
            totsec = (int)(0.5 + (currenttime-initialtime)/1000.0);
            timeleft = (int) ( 0.5+ (currenttime-initialtime) * ((double)(P.numruns-nrun-1)) / ((double) (nrun+1) * 1000.0) ) ;
            System.out.println("\n Finished "+srwtype+" graph run "+nrun+" at rw "+rwa+", time taken "+totsec+"s, "+timeleft+"s left");
        } // eo for nrun
        
        int nnn;
        double adav,ccav,aderr,ccerr,adav0,ccav0,adavl,ccavl;
        adav0=ccav0=adavl=ccavl=0;
        PrintStream PS;
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        fname = nameroot+"mr.dat";
        try {
            fout = new FileOutputStream(fname);
            PS = new PrintStream(fout);
            PS.println("# "+srwtype+" Multiple Runs" );
            PS.println("# rewirings | av dist +/-       | av cc +/- | Repeated, norm first rw |  # events");
            nnn = resarr[0].nrw;
            adav0=resarr[0].ad/nnn;
            ccav0=resarr[0].cc/nnn;
            nnn = resarr[rwamax].nrw;
            adavl=resarr[rwamax].ad/nnn;
            ccavl=resarr[rwamax].cc/nnn;
            for (rwa=0;rwa<=P.numrewirings; rwa++){
                nnn = resarr[rwa].nrw;
                if (nnn<1) continue;
                adav=resarr[rwa].ad/nnn;
                ccav=resarr[rwa].cc/nnn;
                aderr = (resarr[rwa].ad2-nnn*adav*adav);
                if (aderr<0) aderr=0; else aderr = Math.sqrt(aderr)/nnn;
                ccerr = resarr[rwa].cc2-nnn*ccav*ccav;
                if (ccerr<0) ccerr=0; else ccerr = Math.sqrt(ccerr)/nnn;
                PS.print(rwa+"   "+adav+"   "+aderr+"   "+ccav+"   "+ ccerr);
                PS.println("   "+(adav-adavl)/(adav0-adavl)+"   "+aderr/(adav0-adavl)
                +"   "+(ccav-ccavl)/(ccav0-ccavl)+"   "+ ccerr/(ccav0-ccavl) +"   " + nnn);
            }
            
            try{ fout.close();
            } catch (IOException e) {
                System.out.println("*** Error closing file "+fname);};
                
        } catch (FileNotFoundException e) {
            System.out.println("*** Error opening output file "+ fname);
            return;
        }
        System.out.println("\n*** Finished RW graph");
        
    } // eo main
    
    // End of main ************************************************************
    
    
    /**
     *  Method of WS
     */
    void GraphDataPrint()  {
        
        System.out.println("#        Graph average distance = "+adgav+" +/- " + adgrerr);
        System.out.println("# Graph average clustering coef = "+ccgav+" +/- " + ccgrerr);
        
    }
    
    
    /**
     *  Method of WS
     */
    int GraphDataCalculateAll()  {
        
        
        
        GraphStatistics gstats = new GraphStatistics();
        adv=ccv=ccgav=adgav=ccgrerr=adgrerr=0;
        nn=0;
        
        if (! g.isDirected() ) {
            DoubleArrayList clustercoef = new DoubleArrayList();
            clustercoef = gstats.clusteringCoefficients(g);
            
            //                System.out.println("Index : Clustering Coef");
            for (int i=0; i<clustercoef.size(); i++)
            { nn++;
              ccv=clustercoef.get(i);
              ccgav+=ccv;
              ccgrerr+=ccv*ccv;
              //                      System.out.println(i + "  " + ccv);
            };
            ccgav=ccgav/nn;
            ccgrerr = Math.sqrt((ccgrerr-nn*ccgav*ccgav))/nn;
            //                System.out.println("# Graph average = "+ccgav+" +/- " + ccgrerr);
            
            nn=0;
            
            DoubleArrayList averagedistances = new DoubleArrayList();
            averagedistances = gstats.averageDistances(g);
            //test for disconnected graph
            if (averagedistances == null) return 1;
            //                System.out.println("Index : Av Distance");
            for (int i=0; i<averagedistances.size(); i++)
            { nn++;
              adv=averagedistances.get(i);
              adgav+=adv;
              adgrerr+=adv*adv;
              //                      System.out.println(i + "  " + adv);
            };
            adgav=adgav/nn;
            adgrerr = Math.sqrt((adgrerr-nn*adgav*adgav))/nn;
            //                System.out.println("# Graph average = "+adgav+" +/- " + adgrerr);
            
            
        }
        
        return 0; // Successful completion
    }
    
    
    /**
     *  Method of RW
     */
    int GraphDataCalculateStat(double relerr)  {
        
        
        
        
        if (g.isDirected() ) return -1;
        adv=ccv=ccgav=adgav=ccgrerr=adgrerr=0;
        Indexer id = Indexer.getIndexer(g);
        int tNumVertices = g.numVertices();
        Set vset= new HashSet(10);
        
        
        Vertex v = null;
        int degree = 0;
        int numPaths =0;
        
        int nsource = 0;
        int nsourcemin = 10;
        do {
            v = (Vertex) id.getVertex((int) (Math.random() * tNumVertices));
            ArrayList neighbors = new ArrayList(v.getNeighbors());
            int numNeighbors = neighbors.size();
            if (numNeighbors <2) continue;
            nsource++;
            double numNeighborNeighborEdges = 0;
            for (int v1Idx=0; v1Idx<numNeighbors-1; v1Idx++) {
                Vertex v1 = (Vertex) neighbors.get(v1Idx);
                
                for (int v2Idx=v1Idx+1; v2Idx<numNeighbors; v2Idx++) {
                    Vertex v2 = (Vertex) neighbors.get(v2Idx);
                    
                    if (v2.isNeighbor(v1)) {
                        numNeighborNeighborEdges += 1.0;
                    }
                }
            }
            double numPossibleEdges = numNeighbors *(numNeighbors +1)/2.0;
            ccv = numNeighborNeighborEdges/numPossibleEdges;
            ccgav+=ccv;
            ccgrerr+=ccv*ccv;
        } while ((ccgrerr>relerr) && (nsource < nsourcemin));
        
        ccgav=ccgav/nn;
        ccgrerr = Math.sqrt((ccgrerr-nn*ccgav*ccgav))/nn;
        //                System.out.println("# Graph average = "+ccgav+" +/- " + ccgrerr);
        
        
        
        // Start of distance code
        UnweightedShortestPath shortestPath = new UnweightedShortestPath(g);
        double dist = 0;
        //            double asp,asperr;
        nsource = 0;
        do {nsource++;
        v = (Vertex) id.getVertex((int) (Math.random() * tNumVertices));
        degree = v.degree();
        double averageShortestPath = 0;
        double averageShortestPath2 = 0;
        
        Map sourceDistanceMap = shortestPath.computeShortestPathsFromSource(g,v);
        
        
        for (Iterator nIt=g.getVertices().iterator();nIt.hasNext();) {
            Vertex v2 = (Vertex) nIt.next();
            if (v2 == v) {
                continue;
            }
            Number sp = (Number) sourceDistanceMap.get(v2);
            dist = sp.doubleValue();
            if (dist > 0) {
                averageShortestPath += dist;
                averageShortestPath2 += dist*dist;
                numPaths ++;
            }
        } // for nIt
        adgav = averageShortestPath / (double) (numPaths);
        adgrerr = Math.sqrt(averageShortestPath2 - numPaths*adgav*adgav) / (double) (numPaths);
        
        } while ((adgrerr>relerr) && (nsource < nsourcemin));
        
        return 0; // Successful completion
    }
    
    
    /**
     * The set of average shortest path distances for each vertex.
     * For each vertex, the shortest path distance
     * to every other vertex is measured and the average is computed.
     * Note: You can use cern.jet.stat.Descriptive to compute various statistics from the DoubleArrayList
     * @param graph the graph whose average distances are to be computed
     * @return the set of average shortest path distances for each vertex (to every other vertex); However, if the
     * graph is not strongly connected null will be returned since the graph diameter is infinite
     */
    public double averageDistances(double relerr) {
        UnweightedShortestPath shortestPath = new UnweightedShortestPath(g);
        Indexer id = Indexer.getIndexer(g);
        Vertex v = null;
        int degree = 0;
        int numPaths =0;
        int tNumVertices = g.numVertices();
        double dist = 0;
        double asp,asperr;
        
        do {
            v = (Vertex) id.getVertex((int) (Math.random() * tNumVertices));
            degree = v.degree();
            double averageShortestPath = 0;
            double averageShortestPath2 = 0;
            
            Map sourceDistanceMap = shortestPath.computeShortestPathsFromSource(g,v);
            
            
            for (Iterator nIt=g.getVertices().iterator();nIt.hasNext();) {
                Vertex v2 = (Vertex) nIt.next();
                if (v2 == v) {
                    continue;
                }
                Number sp = (Number) sourceDistanceMap.get(v2);
                dist = sp.doubleValue();
                if (dist > 0) {
                    averageShortestPath += dist;
                    averageShortestPath2 += dist*dist;
                    numPaths ++;
                }
            } // for nIt
            asp = averageShortestPath / (double) (numPaths);
            asperr = Math.sqrt(averageShortestPath2 - numPaths*asp*asp) / (double) (numPaths);
            
        } while (asperr>relerr);
        
        return asp;
        
    }
    
    
    
    /**
     * Chooses a random vertex from a vertex set vset of size n
     * @param n is number of vertices in vset
     * @param vset is set of vertices
     * @return a random Vertex in the walk
     */
    Vertex RandomVertex( int n, Set vset)  {
        //    int n= g.numVertices();
        int nv = Rnd.nextInt(n)+1; // nv = random number from 1 to n
        // HELP!!! is this from 1 to n+1 or to n?
        //    System.out.println("Choosing vertex " + nv + " out of " + n );
        //    Set vset = g.getVertices();
        Vertex v = null;
        Iterator it = vset.iterator();
        // -- increment operators has precedence over relational >
        while( nv-- > 0 ) {
            //            System.out.print(".");
            if (it.hasNext()) v = (Vertex) it.next();  // Assumes it starts with next vertex the first one
        };
        //    System.out.println("Degree of random vertex " + v.degree() );
        return v;
        
    }
    
    /**
     * Chooses a random vertex from a walk
     * @return a random Vertex in the walk
     */
    //    Vertex GetVertex(int nv)  {
    //    int n = g.numVertices();
    ////    System.out.println("Choosing vertex " + nv + " out of " + n );
    //    Set vset = g.getVertices();
    //    Vertex v = null;
    //    if ((nv>n) || (nv<=0)) return v;
    //    Iterator it = vset.iterator();
    //    // -- increment operators has precedence over relational >
    //    while( nv-- > 0 ) {
    ////            System.out.print(".");
    //            if (it.hasNext()) v = (Vertex) it.next();  // Assumes it starts with next vertex the first one
    //            };
    ////    System.out.println("Degree of random vertex " + v.degree() );
    //    return v;
    //
    //    }
    //
    /**
     * Chooses a random edge from a set of edges
     * @param n is number of edge in eset
     * @param eset is set of edges
     * @return edge choosen
     */
    Edge RandomEdge(int n, Set eset)  {
        int ne = Rnd.nextInt(n) +1 ;
        Edge e = null;
        Iterator it = eset.iterator();
        // -- increment operators has precedence over relational >
        while( ne-- > 0 ) {
            //            System.out.print(".");
            if (it.hasNext()) e = (Edge) it.next();  // Assumes it starts with next vertex the first one
        };
        return e;
        
    }
    
    /**
     * Chooses a random vertex at end of a random edge from a set of edges
     * @param n is number of edge in eset
     * @param eset is set of edges
     * @return Vertex choosen to be one end of a random edge
     */
    Vertex RandomVertexFromEdge(int n, Set eset)  {
        //    int n = g.numEdges();
        int ne = Rnd.nextInt(n) +1 ; // ne = random number from 1 to n
        //    System.out.println("Choosing Edge " + ne + " out of " + n );
        //    Set eset = g.getEdges();
        Edge e = null;
        Iterator it = eset.iterator();
        // -- increment operators has precedence over relational >
        while( ne-- > 0 ) {
            //            System.out.print(".");
            if (it.hasNext()) e = (Edge) it.next();  // Assumes it starts with next vertex the first one
        };
        
        
        Vertex v = new UndirectedSparseVertex();
        
        
        Set vset = e.getIncidentVertices();
        it = vset.iterator();
        v = (Vertex) it.next();
        if (Rnd.nextBoolean() ) v = (Vertex) it.next();
        
        //    System.out.println("Degree of random vertex " + v.degree() );
        return v;
        
    }
    
    /**
     * Chooses a random vertex at end of edge e
     * @param e is the edge
     * @return Vertex choosen to be one end of the edge
     */
    Vertex RandomVertexOfEdge(Edge e)  {
        Set eset = e.getIncidentVertices();
        Iterator it = eset.iterator();
        Vertex v = null;
        if (it.hasNext()) v = (Vertex) it.next();
        if (Rnd.nextDouble()<0.5)  {   if (it.hasNext()) v = (Vertex) it.next();};
        return v;
    }
    
    
    /**
     * Outputs information for a connected Undirected graph
     *  <filenameroot>info.txt general info
     * @param filenameroot basis of name of file as string
     * @param cc comment characters put at the start of every line
     */
    void GraphDataFileOutput(String filenameroot, String cc, double timetaken)  {
        
        String filename = filenameroot +"info.txt";
        PrintStream PS;
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filename);
            PS = new PrintStream(fout);
            Date date = new Date();
            PS.println(cc+" "+date+" ");
            
            PS.print(cc+" Time taken "+timetaken+"sec. ");
            
            if (g.isDirected() ) PS.println("Directed graph: ");
            else PS.println("Undirected graph: ");
            
            PS.println(cc+" No. vertices: "+ g.numVertices() );
            PS.println(cc+" No. edges: "+ g.numEdges() );
            try{ fout.close();
            } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+filename);
            return;
        }
        
        // Output graph as Pajek file
        filename = filenameroot + ".net";
        PajekNetFile file = new PajekNetFile();
        file.save(g, filename);
        
        
        Set uset = g.getVertices();
        
        // Save degree distribution
        Histogram histo = new Histogram();
        DegreeDistributions dd = new DegreeDistributions();
        histo = dd.getIndegreeHistogram(uset, 0, 100,50);
        filename = filenameroot + "dd.txt";
        dd.saveDistribution(histo, filename);
        
        
        GraphStatistics gstats = new GraphStatistics();
        double adv,ccv,ccgav=0,adgav=0,ccgrerr=0,adgrerr=0;
        int nn=0;
        
        
        filename = filenameroot +"cc.txt";
        if (! g.isDirected() ) {
            try {
                fout = new FileOutputStream(filename);
                PS = new PrintStream(fout);
                
                
                
                DoubleArrayList clustercoef;
                clustercoef = gstats.clusteringCoefficients(g);
                
                PS.println(cc+"Index : Clustering Coef");
                for (int i=0; i<clustercoef.size(); i++)
                { nn++;
                  ccv=clustercoef.get(i);
                  ccgav+=ccv;
                  ccgrerr+=ccv*ccv;
                  PS.println(i + "  " + ccv);
                };
                ccgav=ccgav/nn;
                ccgrerr = Math.sqrt((ccgrerr/nn-ccgav*ccgav));
                PS.println("# Graph average = "+ccgav+" +/- " + ccgrerr);
                
                
            } catch (FileNotFoundException e) {
                System.out.println("Error opening output file "+filename);
                return;
            }
        }
        
        nn=0;
        filename = filenameroot +"ad.txt";
        if (! g.isDirected() ) {
            try {
                fout = new FileOutputStream(filename);
                PS = new PrintStream(fout);
                
                
                DoubleArrayList averagedistances;
                averagedistances = gstats.averageDistances(g);
                
                PS.println(cc+"Index : Av Distance");
                for (int i=0; i<averagedistances.size(); i++)
                { nn++;
                  adv=averagedistances.get(i);
                  adgav+=adv;
                  adgrerr+=adv*adv;
                  PS.println(i + "  " + adv);
                };
                adgav=adgav/nn;
                adgrerr = Math.sqrt((adgrerr/nn-adgav*adgav));
                PS.println("# Graph average = "+adgav+" +/- " + adgrerr);
                
                
            } catch (FileNotFoundException e) {
                System.out.println("Error opening output file "+filename);
                return;
            }
        }
        
        
    }
    
    
    
    
    
    /** Output in Pajek format to <filename>.net
     * @param filename filename.net is the file with the walk garph in Pajek format
     */
    void WSGraphFileOutput(String filename)  {
        
        String ss = filename + ".net";
        PajekNetFile file = new PajekNetFile();
        file.save(g, ss);
        
        
    }
    
    
    /**
     * Outputs representation of the walk
     * @param None
     */
    void WSGraphPrintOut()  {
        System.out.println(g.toString());
        
    }
    
    
    
    
    
    /**
     * Outputs statistics for a connected Undirected graph
     * @param None
     */
    void GraphDataOutput()  {
        
        if (g.isDirected() ) System.out.println("Directed graph: ");
        else System.out.println("Undirected graph: ");
        
        System.out.println("No. vertices: "+ g.numVertices() );
        System.out.println("   No. edges: "+ g.numEdges() );
        
        Set uset = g.getVertices();
        ClusterDataOutput(uset);
        
        DegreeDistributions dd = new DegreeDistributions();
        Histogram histo = new Histogram();
        histo = dd.getIndegreeHistogram(uset, 0, 100,20);
        
        GraphStatistics gstats = new GraphStatistics();
        
        if (! g.isDirected() ) {
            DoubleArrayList cc;
            cc = gstats.clusteringCoefficients(g);
            System.out.println("Index : Clustering Coef");
            for (int i=0; i<cc.size(); i++) { System.out.println(i + " : " + cc.get(i)); };
        }
        
    }
    
    /**
     * Outputs statistics for a connected Undirected graph
     * @param None
     */
    void GraphDataOutputSlow()  {
        
        GraphStatistics gstats = new GraphStatistics();
        // DoubleArraySize described in
        // colt/doc/cern/colt/list/package-summary.html#package_description
        DoubleArrayList ad;
        ad = gstats.averageDistances(g);
        if (ad == null) System.out.println("Disconnected Graph");
        else {
            System.out.println("Index : Average Distance");
            for (int i=0; i<ad.size(); i++) { System.out.println(i + " : " + ad.get(i)); };
        }
        
        
        
    }
    
    
    
    
    
    /**
     * Disconnected Graph Statistics
     *
     */
    void DisconGraphDataOutput()  {
        BicomponentClusterer bc = new BicomponentClusterer();
        ClusterSet cs = bc.extract(g);
        System.out.println("Size of Bicomponent Cluster Collection "+ cs.size());
        
        Iterator it = cs.iterator();
        Set currentcluster;
        int cn=0;
        while( it.hasNext() ) {cn++;
        System.out.println("Cluster # " + cn);
        currentcluster = (Set) it.next();
        ClusterDataOutput(currentcluster);
        };
        
        
    }
    
    
    
    
    
    
    
    /**
     * Output graph statistics for a set of vertices
     * @param vset a set of vertices
     */
    void ClusterDataOutput(Set vset)  {
        System.out.println("Cluster size : "+ vset.size() );
        System.out.println("Vertex # : In Deg  : Out Deg : Degree");
        Vertex currentVertex = null;
        Iterator it = vset.iterator();
        int vn=0;
        while( it.hasNext() ) {vn++;
        currentVertex = (Vertex) it.next();
        System.out.println(vn + " : " + currentVertex.inDegree()
        + " : " + currentVertex.outDegree() + " : " + currentVertex.degree() );
        };
        
    }
    
    
    
    /** Method in ws.  Set initial lattice graph
     *  @param numvertices number of vertices in lattice
     *  @return Undirected sparse lattice
     */
    void NewLatticeGraph(int numvertices)  {
        g.removeAllVertices();
        Vertex v0,v1,v2,v3,v4;
        v0 = new UndirectedSparseVertex();
        v1 = new UndirectedSparseVertex();
        g.addVertex(v0);
        g.addVertex(v1);
        g.addEdge(new UndirectedSparseEdge(v0, v1));
        v3=v0;
        v4=v1;
        for (int i= 2; i<numvertices; i++){
            //        System.out.print("Adding vertex "+ i );
            v2=v3;
            v3=v4;
            v4 = new UndirectedSparseVertex();
            g.addVertex(v4);
            //            System.out.print(", 1st edge");
            g.addEdge(new UndirectedSparseEdge(v2, v4));
            //            System.out.print(", 2nd edge");
            g.addEdge(new UndirectedSparseEdge(v3, v4));
        }
        g.addEdge(new UndirectedSparseEdge(v3,v0));
        g.addEdge(new UndirectedSparseEdge(v4,v0));
        g.addEdge(new UndirectedSparseEdge(v4,v1));
    }
    
    /** Method in ws.  Set initial lattice graph
     *  @param numvertices number of vertices in lattice
     *  @return Undirected sparse lattice
     */
    void NewLatticeGraph2D(int lx, int ly)  {
        g.removeAllVertices();
        
        // *** SEE JUNGs KleinbergSmallWorldGenerator  FOR LATTICE ROUTINE
        
        
        //        Vertex v0,v1,v2,v3,v4;
        //        v0 = new UndirectedSparseVertex();
        //        v1 = new UndirectedSparseVertex();
        //        g.addVertex(v0);
        //        g.addVertex(v1);
        //        g.addEdge(new UndirectedSparseEdge(v0, v1));
        //        v3=v0;
        //        v4=v1;
        for (int x= 0; x<lx; x++){
            for (int y= 0; y<ly; y++){
                //        System.out.print("Adding vertex at ("+ x+","+y+")" );
            };
        };
        //        g.addEdge(new UndirectedSparseEdge(v3,v0));
        //        g.addEdge(new UndirectedSparseEdge(v4,v0));
        //        g.addEdge(new UndirectedSparseEdge(v4,v1));
    }
    
    
    /** Method in ws.  Set initial lattice graph
     *  @param numvertices number of vertices in lattice
     *  @return Undirected sparse lattice
     */
    void NewLatticeGraph2(int numvertices, int condiv2)  {
        g.removeAllVertices();
        Vertex v0,v1,v2;
        Set v0arr= new HashSet(10);
        Set varr= new HashSet(10);
        for (int v=0; v<condiv2; v++)
        {v0 = new UndirectedSparseVertex();
         g.addVertex(v0);
         varr.add(v0);
         v0arr.add(v0);
        }
        Iterator itvarr = varr.iterator();
        Iterator itvarr0 = varr.iterator();
        for (int i= condiv2; i<numvertices; i++){
            v2 = new UndirectedSparseVertex();
            g.addVertex(v2);
            
            //            g.addEdge(new UndirectedSparseEdge(v1, v2));
            ////            if (i+condiv >= numvertices) {
            ////          }
            //          g.addEdge(new UndirectedSparseEdge(v2,v0));
            
        }
    }
    
    
    
    
    /** Method in ws.  REWIRE one end of an edge at a VERTEX no allowed Disconnection of vertices.
     * Taken from JUNG's EppsteinPowerLawGenerator.java
     *  @param tNumEdgesToSwap number of rewirings
     *  @return Undirected sparse lattice
     */
    int RewireVertex(int tNumEdgesToSwap)  {
        int nrw=0;
        int tNumVertices = g.numVertices();
        Indexer id = Indexer.getIndexer(g);
        do {
            
            Vertex v = null;
            int degree = 0;
            do {v = (Vertex) id.getVertex((int) (Math.random() * tNumVertices));
                degree = v.degree();
            } while (degree == 0 );

            List edges = new ArrayList(v.getIncidentEdges());
            Edge randomExistingEdge = (Edge) edges.get((int) (Math.random()*degree));
            List VertOfEdge = new ArrayList(randomExistingEdge.getIncidentVertices());
            Vertex v2 = (Vertex) VertOfEdge.get(0);
            if (v2==v) {v2 = (Vertex) VertOfEdge.get(1);};
            if (v2.degree()<2) continue;
            
            Vertex y = (Vertex) id.getVertex((int) (Math.random() * tNumVertices));
            
            if (!y.isSuccessorOf(v) && v != y) {
                g.removeEdge(randomExistingEdge);
                GraphUtils.addEdge(g,v,y);
                nrw++;
                id.updateIndex();
            }
        } while (nrw<tNumEdgesToSwap);
        
        return nrw;
    }
    
    /** Method in ws.  Rewire lattice graph. Will not disconnect a single vertex.
     *  @param n number of rewirings attemped from whole graph
     *  @return Undirected sparse lattice
     */
    int RewireVertex2(int n)  {
        Edge e;
        Vertex v,v1,v2;
        v=v1= null;
        
        int ne,nv,nrw=0;
        
        ne = g.numEdges();
        Set eset= new HashSet(ne);
        Set tset= new HashSet(2);
        Iterator it;
        
        nv = g.numVertices();
        Set vset= new HashSet(nv);
        vset = g.getVertices();
        
        
        for (int i=0; i<n; i++){
            eset = g.getEdges();
            ne = g.numEdges();
            e = RandomEdge(ne, eset);
            // Now check that each vertex has another edge connected to ensure that when we remove
            // edge both vertices will remain connected
            tset = e.getIncidentVertices();
            it = tset.iterator();
            if (it.hasNext()) v = (Vertex) it.next();
            if (it.hasNext()) v1 = (Vertex) it.next();
            if ( (v.degree()<2) || (v.degree()<2) ) continue;
            
            if (Rnd.nextDouble()<0.5)  { v1=v;};
            //            v1 = RandomVertexOfEdge(e);
            v2 = RandomVertex(nv,vset);
            
            
            if ((v2 != v1) && ( ! v1.isNeighbor(v2) ))
            { nrw++;
              g.removeEdge(e);
              g.addEdge(new UndirectedSparseEdge(v1,v2));};
        };
        return nrw;
    }
    
    
    /** Method in ws.  Rewire random Edges of given graph. Will not disconnect a single vertex.
     *  @param n number of rewirings attemped from whole graph
     *  @return Undirected sparse lattice
     */
    int RewireEdge(int tNumEdgesToSwap)  {
        Vertex v1,v2,x,y;
        int nrw =0 ;
        int tNumEdges = g.numEdges();
        int tNumVertices = g.numVertices();
        Indexer id = Indexer.getIndexer(g);
        do {
            
            List edges = new ArrayList(g.getEdges());
            Edge randomExistingEdge = (Edge) edges.get((int) (Math.random()*tNumEdges));
            List VertOfEdge = new ArrayList(randomExistingEdge.getIncidentVertices());
            v1 = (Vertex) VertOfEdge.get(0);
            v2 = (Vertex) VertOfEdge.get(1);
            if ( (v1.degree()<2) || (v2.degree()<2) )  continue;
            
            
            x = (Vertex) id.getVertex((int) (Math.random() * tNumVertices));
            //            y = null;
            do{
                y = (Vertex) id.getVertex((int) (Math.random() * tNumVertices));
            } while (y.isSuccessorOf(x) || (x == y) );
            g.removeEdge(randomExistingEdge);
            GraphUtils.addEdge(g,x,y);
            nrw++;
            id.updateIndex();
            
        } while (nrw<tNumEdgesToSwap);
        
        return nrw;
    }
    
    
    
    /** Method in ws.  Rewire Edge lattice graph. Will not disconnect a single vertex.
     *  @param n number of rewirings attemped from whole graph
     *  @return Undirected sparse lattice
     */
    int RewireEdge2(int n)  {
        Edge e;
        Vertex v,v1,v2;
        v=v1= null;
        
        int ne,nv,nrw=0;
        
        ne = g.numEdges();
        Set eset= new HashSet(ne);
        Set tset= new HashSet(2);
        Iterator it;
        
        nv = g.numVertices();
        Set vset= new HashSet(nv);
        vset = g.getVertices();
        
        
        for (int i=0; i<n; i++){
            eset = g.getEdges();
            ne = g.numEdges();
            e = RandomEdge(ne, eset);
            // Now check that each vertex has another edge connected to ensure that when we remove
            // edge both vertices will remain connected
            v1 = RandomVertex(nv,vset);
            v2 = RandomVertex(nv,vset);
            
            
            if ((v2 != v1) && ( ! v1.isNeighbor(v2) ))
            { nrw++;
              g.removeEdge(e);
              g.addEdge(new UndirectedSparseEdge(v1,v2));};
        };
        return nrw;
    }
    
    
    
    /** Method in ws.  SwitchEdgePair random pairs of edges.
     *  @param n number of rewirings attemped from whole graph
     *  @return Undirected sparse lattice
     */
    int RewireEdgePair(int n)  {
        Edge e1,e2;
        Vertex v1a,v1b,v2a,v2b;
        
        int ne,nrw=0;
        
        ne = g.numEdges();
        Set eset= new HashSet(ne);
        Iterator it;
        
        if (ne<2) return -1;
        
        for (int i=0; i<n; i++){
            eset = g.getEdges();
            ne = g.numEdges();
            
            e1 = RandomEdge(ne, eset);
            do{e2 = RandomEdge(ne, eset);}while(e1==e2);
            
            it = e1.getIncidentVertices().iterator();
            v1a = (Vertex) it.next();
            v1b = (Vertex) it.next();
            
            it = e2.getIncidentVertices().iterator();
            v2a = (Vertex) it.next();
            v2b = (Vertex) it.next();
            
            g.removeEdge(e1);
            g.removeEdge(e2);
            
            if (Rnd.nextDouble()<0.5) {
                g.addEdge(new UndirectedSparseEdge(v1a,v2a));
                g.addEdge(new UndirectedSparseEdge(v1b,v2b)); }
            else {      g.addEdge(new UndirectedSparseEdge(v1a,v2b));
            g.addEdge(new UndirectedSparseEdge(v1b,v2a));  };
            nrw++;
        };
        return nrw;
    }
    
    
    
    
    
    /** Method in ws.  Rewire lattice graph
     *  @param p probability of rewiring each edge
     *  @return Undirected sparse lattice
     */
    void Rewire(double p)  {
        Vertex v0,v1,v2;
        int i;
        Set eset= new HashSet(10);
        Iterator iteset = eset.iterator();
        i=0;
        while (iteset.hasNext()) {
            i++;
            if (Rnd.nextDouble()<p) {
                System.out.println("Edge "+i+" rewired");
                
            }
            else {System.out.println("Edge "+i+" not rewired");};
            
        }
        
        
    }
    
    
    /** Method in ws.  Create new Random graph.
     *  @param nv number of vertices
     *  @param ne number of edges
     *  @return Undirected sparse lattice
     */
    void NewRandomGraph(int nv, int ne)  {
        
        g.removeAllVertices();
        GraphUtils.addVertices( g, nv );
        
        Indexer id = Indexer.getIndexer( g ) ;
        // Next part taken from JUNG's EppsteinPowerLawGenerator.java
        while (g.numEdges() < ne) {
            Vertex u = (Vertex) id.getVertex((int) (Math.random() * nv));
            Vertex v = (Vertex) id.getVertex((int) (Math.random() * nv));
            if (!v.isSuccessorOf(u)) {
                GraphUtils.addEdge(g,u,v);
            }
        }
    }
    
    /** Method in ws.  Create new Random graph.
     *  @param nv number of vertices
     *  @param ne number of edges
     *  @return Undirected sparse lattice
     */
    void NewRandomGraph2(int nv, int ne)  {
        
        g.removeAllVertices();
        GraphUtils.addVertices( g, nv );
        
        Indexer id = Indexer.getIndexer( g ) ;
        Vertex v1,v2;
        int vn1,vn2;
        int edgesadded=ne;
        while (edgesadded>0) {
            vn1 = Rnd.nextInt(nv);
            do {vn2 = Rnd.nextInt(nv);}while(vn1==vn2);
            v1 =  (Vertex) id.getVertex(vn1);
            v2 =  (Vertex) id.getVertex(vn2);
            if (!v1.isSuccessorOf(v2)) {
                GraphUtils.addEdge( g, v1,v2);
                edgesadded--;
            }
        }
    }
    
    
    
    
    
    
    
    /** Method in walk.  Set initial test graphs
     *  the walks are set up to be small simple graphs
     *  those chosen by negative numbers are disconnected
     *  @return Undirected sparse graph
     */
    void NewTestGraph(int n)  {
        g.removeAllVertices();
        Vertex v1 = new UndirectedSparseVertex();
        Vertex v2 = new UndirectedSparseVertex();
        Vertex v3 = new UndirectedSparseVertex();
        Vertex v4 = new UndirectedSparseVertex();
        Vertex v5 = new UndirectedSparseVertex();
        Vertex v6 = new UndirectedSparseVertex();
        Vertex v7 = new UndirectedSparseVertex();
        Vertex v8 = new UndirectedSparseVertex();
        
        switch (n){
            case 1:
                g.addVertex(v1);
                g.addVertex(v2);
                g.addVertex(v3);
                g.addVertex(v4);
                g.addVertex(v5);
                g.addEdge(new UndirectedSparseEdge(v1, v2));
                g.addEdge(new UndirectedSparseEdge(v1, v3));
                g.addEdge(new UndirectedSparseEdge(v3, v2));
                g.addEdge(new UndirectedSparseEdge(v3, v4));
                g.addEdge(new UndirectedSparseEdge(v3, v5));
                g.addEdge(new UndirectedSparseEdge(v4, v5));
                break;
            case -1: // Has disconnected vertex
                g.addVertex(v1);
                g.addVertex(v2);
                g.addVertex(v3);
                g.addVertex(v4);
                g.addVertex(v5);
                g.addVertex(v6);
                g.addEdge(new UndirectedSparseEdge(v1, v2));
                g.addEdge(new UndirectedSparseEdge(v1, v3));
                g.addEdge(new UndirectedSparseEdge(v3, v2));
                g.addEdge(new UndirectedSparseEdge(v3, v4));
                g.addEdge(new UndirectedSparseEdge(v3, v5));
                g.addEdge(new UndirectedSparseEdge(v4, v5));
                break;
            case 2:
                g.addVertex(v1);
                g.addVertex(v2);
                g.addVertex(v3);
                g.addVertex(v4);
                g.addVertex(v5);
                g.addVertex(v6);
                g.addEdge(new UndirectedSparseEdge(v1, v2));
                g.addEdge(new UndirectedSparseEdge(v1, v3));
                g.addEdge(new UndirectedSparseEdge(v3, v2));
                g.addEdge(new UndirectedSparseEdge(v3, v4));
                g.addEdge(new UndirectedSparseEdge(v6, v4));
                g.addEdge(new UndirectedSparseEdge(v6, v5));
                g.addEdge(new UndirectedSparseEdge(v4, v5));
                break;
            case -2:
                g.addVertex(v1);
                g.addVertex(v2);
                g.addVertex(v3);
                g.addVertex(v4);
                g.addVertex(v5);
                g.addVertex(v6);
                g.addEdge(new UndirectedSparseEdge(v1, v2));
                g.addEdge(new UndirectedSparseEdge(v1, v3));
                g.addEdge(new UndirectedSparseEdge(v3, v2));
                g.addEdge(new UndirectedSparseEdge(v6, v4));
                g.addEdge(new UndirectedSparseEdge(v6, v5));
                break;
            case 3:
                g.addVertex(v1);
                g.addVertex(v2);
                g.addVertex(v3);
                g.addEdge(new UndirectedSparseEdge(v1, v2));
                g.addEdge(new UndirectedSparseEdge(v1, v3));
                //g.addEdge(new UndirectedSparseEdge(v3, v2));
                break;
            case -3:
                g.addVertex(v1);
                g.addVertex(v2);
                g.addVertex(v3);
                g.addVertex(v4);
                g.addVertex(v5);
                g.addVertex(v6);
                g.addVertex(v7);
                g.addVertex(v8);
                g.addEdge(new UndirectedSparseEdge(v1, v2));
                g.addEdge(new UndirectedSparseEdge(v1, v3));
                g.addEdge(new UndirectedSparseEdge(v1, v4));
                g.addEdge(new UndirectedSparseEdge(v2, v3));
                g.addEdge(new UndirectedSparseEdge(v2, v4));
                g.addEdge(new UndirectedSparseEdge(v3, v4));
                g.addEdge(new UndirectedSparseEdge(v5, v6));
                g.addEdge(new UndirectedSparseEdge(v5, v7));
                g.addEdge(new UndirectedSparseEdge(v5, v8));
                g.addEdge(new UndirectedSparseEdge(v6, v7));
                g.addEdge(new UndirectedSparseEdge(v6, v8));
                g.addEdge(new UndirectedSparseEdge(v7, v8));
                break;
            case 4:
                g.addVertex(v1);
                g.addVertex(v2);
                g.addVertex(v3);
                g.addEdge(new UndirectedSparseEdge(v1, v2));
                g.addEdge(new UndirectedSparseEdge(v1, v3));
                g.addEdge(new UndirectedSparseEdge(v3, v2));
                break;
            default:
                g.addVertex(v1);
                g.addVertex(v2);
                g.addEdge(new UndirectedSparseEdge(v1, v2));
                
        }
        //        return g;
    }
    
    
    /**
     * Taken from http://www.cafeaulait.org/2002may.html
     * Collects garbage but gives time for this to happen
     */
    
    private static void collectGarbage() {
        
        System.gc();
        try {
            // Give the garbage collector time to work
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            // just continue
        }
        System.gc();
        try {
            // Give the garbage collector time to work
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            // just continue
        }
        System.gc();
        try {
            // Give the garbage collector time to work
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            // just continue
        }
        
    }
    
    
    
    
    
}
