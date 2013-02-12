/*
 * ws.java
 *
 * Created on 18 September 2003, 18:26
 */


import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.utils.*;
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
//import java.lang.Object.*;
//import java.lang.Math.*;

import java.io.*;


/**
 *
 * @author  Tim Evans
 */
public class ws {
    
    UndirectedGraph g = new UndirectedSparseGraph();
    Random Rnd = new Random(0); //Schildt p524
    double adv,ccv,ccgav,adgav,ccgrerr,adgrerr;
    int nn;

    /** Constructor - 
     */
    public ws() {
        
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
 
        Date date = new Date();
        System.out.println(date);
        
        ws w = new ws();
        String s = new String();
        int i=0;
        
        String nameroot = "WS";
        int numvertices = 20;
        int connectivity =2;
        int numrewirings=40;
        int numruns=4;
        int dfreq = 1;
        double probnewvertex= 0.5;
        int infolevel = 0;
        boolean FullInfoOn=false;
        boolean SourceVertex=true;
        boolean StartWalkWithVertex=true;           
        
//        System.out.println(args.length+" command line arguments");
//        for (int j =0; j<args.length; j++){System.out.println("args["+j+"] = "+args[j]);}
//        System.out.println(" args.length "+args.length);
        System.out.println(" ws <filenameroot> <#vertices> <connectivity> <#rewirings> <#numruns> <data freq> <infolevel>");
                
        switch (args.length){
        case 9: {
            infolevel = Integer.parseInt(args[8]);            
            }
        case 8: {
            if (args[7]=="e") SourceVertex=false;            
            }
        case 7: {
            infolevel = Integer.parseInt(args[6]);            
            }
        case 6: {
            dfreq = Integer.parseInt(args[5]);
            }
        case 5: {
            numruns= Integer.parseInt(args[4]);
            }
        case 4: {
            numrewirings= Integer.parseInt(args[3]);
            }
        case 3: {
            connectivity = Integer.parseInt(args[2]);
            }
        case 2: {
            numvertices = Integer.parseInt(args[1]);
            }
        case 1: {
            nameroot = args[0];
            break;
            }
        case 0: {
            System.out.println("\n All default parameters used");
            break;
            }
            default:{
                System.out.println("\n*** Wrong number of parameters, usage:");
                System.out.println("  ws <filenameroot> <#vertices> <connectivity> <#rewirings> <#numruns> <data freq> <i=FullInfoOn>");
                System.out.println("Can leave off from end of list as many parameters as wish and default values will be used");
                return;
               }
        }
        
        nameroot=nameroot+ "n"+Integer.toString(numvertices)
                         + "rw"+Integer.toString(numrewirings)
                         + "r"+Integer.toString(numruns);
    
        String fname = nameroot;
        int rwt,rwa,rwamax,df;
        df = dfreq;
        rwamax=0;
        WSResult[] resarr = new WSResult[numrewirings+1];
        WSResult rrr = new WSResult();
        rrr.nrw=0;
        
        for (i =0; i<=numrewirings; i++) {
            resarr[i]= new WSResult();
            resarr[i].nrw=0;
            resarr[i].ad=0;
            resarr[i].ad2=0;
            resarr[i].cc=0;
            resarr[i].cc2=0;
            
        }; 
        double timetaken;
        
                                    
        long initialtime = System.currentTimeMillis ();
        long currenttime = initialtime;
        int totsec, timeleft;
        totsec = timeleft = 0;

        System.out.println("Starting WS: vertices="+numvertices+" rewirings=" + numrewirings + " no.runs= "+numruns);
        System.out.println("             Info level = "+infolevel);
        
        for (int nrun=0; nrun<numruns; nrun++){  
            timetaken = (double) totsec;
            
            w.NewLatticeGraph(numvertices);
            if ((nrun==0) && (infolevel>0)) {
                                w.GraphDataPrint();
                                fname=nameroot+ "x0";
                                w.GraphDataFileOutput(fname, "#", timetaken );
                            };

            while (w.GraphDataCalculateAll()>0) {};
    //        int initialedges = w.g.numEdges ();
    //        int initialvertices =  w.g.numVertices ();
    //        
    //        System.out.println("Initially - " + initialvertices + " vertices,  "
    //                           + initialedges + " edges.");       
    //        w.GraphDataFileOutput(fname, "#", timetaken );
    //        w.GraphDataPrint();

            rwt=rwa=0;
            resarr[rwa].nrw++;
            resarr[rwa].ad+=  w.adgav;
            resarr[rwa].cc+=  w.ccgav;
            resarr[rwa].ad2+= w.adgav * w.adgav;
            resarr[rwa].cc2+= w.ccgav * w.ccgav;

// Output data on initial position
           fname=nameroot+ "x"+Integer.toString(rwa);
           w.GraphDataFileOutput(fname, "#", timetaken );

            
            
            
            while (rwa<numrewirings) {
                        rwt=w.Rewire(1);
    //                    System.out.println("Tried "+rwa+" rewirings, next was "+rwt);    
                        if (rwt>0) {
                            rwa++;                        
    //                       System.out.println("Tried "+rwa+" rewirings");    
                            if ( (rwa*10) > numvertices ) df = numvertices/5; else df = dfreq;
    //                        if (nrun>24)  System.out.println("nrun "+nrun+", rwa="+rwa);
    //                        if ((nrun>24) && (rwa>19)) {w.WSGraphPrintOut(); 
    //                        w.WSGraphFileOutput("debug.net");
    //                        };
                            if ( ((rwa % df)>0) && rwa<numrewirings ) continue;
                            if (w.GraphDataCalculateAll()>0)  break;
                            if (rwa>rwamax) rwamax =rwa;
                            resarr[rwa].nrw++;
                            resarr[rwa].ad+=w.adgav;
                            resarr[rwa].cc+=w.ccgav;
                            resarr[rwa].ad2+=w.adgav * w.adgav;
                            resarr[rwa].cc2+=w.ccgav * w.ccgav;
                            System.out.print(".");
                            if ((nrun==0) && (infolevel>0)) {
                                w.GraphDataPrint();
                                fname=nameroot+ "x"+Integer.toString(rwa);
                                w.GraphDataFileOutput(fname, "#", timetaken );
                            };
                        };


            }; // while rwa<numrewiriings


                    currenttime = System.currentTimeMillis ();
                    totsec = (int)(0.5 + (currenttime-initialtime)/1000.0);
                    timeleft = (int) ( 0.5+ (currenttime-initialtime) * ((double)(numruns-nrun-1)) / ((double) (nrun+1) * 1000.0) ) ;
                    System.out.println("\n Finished WS graph run "+nrun+" at rw "+rwa+", time taken "+totsec+"s, "+timeleft+"s left");
        } // eo for nrun

        
        int nnn;
        double adav,ccav,aderr,ccerr,adav0,ccav0,adavl,ccavl;
        adav0=ccav0=adavl=ccavl=0;
        PrintStream PS;
        
        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(nameroot+"WSmr.dat");
            PS = new PrintStream(fout);
            PS.println("# WS Multiple Runs" );
            PS.println("# rewirings | av dist +/-       | av cc +/- | Repeated, norm first rw |  # events"); 
            nnn = resarr[0].nrw;
            adav0=resarr[0].ad/nnn;
            ccav0=resarr[0].cc/nnn;
            nnn = resarr[rwamax].nrw;
            adavl=resarr[rwamax].ad/nnn;
            ccavl=resarr[rwamax].cc/nnn;
            for (rwa=0;rwa<=numrewirings; rwa++){ 
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
            
            
            
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+fname);
            return;
        }
        System.out.println("\n*** Finished WS graph");



    }


    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
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
            try{ fout.close ();   
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
        System.out.println(g.toString ());
        
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

 /** Method in ws.  Rewire lattice graph. Will not disconnect a single vertex.      
     *  @param n number of rewirings attemped from whole graph
     *  @return Undirected sparse lattice
     */
    int Rewire(int n)  {
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
    
    

 /** Method in ws.  Switch random pairs of edges.       
     *  @param n number of rewirings attemped from whole graph
     *  @return Undirected sparse lattice
     */
    int Switch(int n)  {
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




  
    
      
