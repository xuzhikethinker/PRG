/*
 * ring.java
 *
 * Created Wednesday, July 6, 2005 at 15:36
 *
 */



//import cern.colt.list.IntArrayList;
//import cern.colt.list.DoubleArrayList;

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
public class ring {

    /** Constructor - 
     */
    public ring() 
    {
    }

       /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long initialtime;
        String commentcharacter = "#";       
        Date date = new Date();
        System.out.println(date);
        
        timgraph w = new timgraph(); 
        System.out.println("Uses time to seed Rnd");
//        walk w = new walk(0); System.out.println("Fixed random seed used");
//        w.setWalkmode(); //set boolean walk mode variables
        if (w.parseParam(args)>0) return;
        w.printParam();
        w.numevents=10000;
        w.connectivity=6;
        
        int initialedges = w.TotalNumberEdges;
        int initialvertices =  w.TotalNumberVertices;
        initialtime = System.currentTimeMillis ();
        boolean dowalk=true;        
        double walktimetaken;
        //walktimetaken= w.DoRun(0.01);
        if (dowalk) 
        {
        w.initialgraph =1; // select standard graph
        w.setNetwork();
        w.calcNumberVertices();
        w.calcNumberEdges();
        walktimetaken= w.DoRun(0.01);      
        }
        else
        {
        w.initialgraph = 0; // select empty graph
        w.setNetwork();
        w.calcNumberVertices();
        w.calcNumberEdges();
        w.nameroot=w.nameroot+"ER";
        walktimetaken= w.DoER(0.01);
        System.out.println("--- Finished ER " + w.dirname + w.nameroot + " in " + walktimetaken +"sec."  );
        }    
        System.out.print(" Walk routine took ");
        w.printEllapsedTime(initialtime);
        

            int finaledges = w.calcNumberEdges();
            int finalvertices =  w.calcNumberVertices();
            int addedvertices = (finalvertices-initialvertices);
            int addededges = (finaledges-initialedges);
            System.out.println("    " +  addedvertices + " vertices added, "
                               + w.TruncDec(w.probnewvertex*w.numevents, 2 ) + " expected, " 
                               + w.TruncDec(100.0- (100.0*addedvertices)/(w.probnewvertex*w.numevents ) , 2 ) 
                               + "% deviation.");
 
            System.out.println("    " +  (addededges) + " edges added, requested "
                               + w.TruncDec(w.connectivity*w.numevents *2,2) + ", " 
                               + w.TruncDec(100.0- (100.0*addededges)/((double)(w.connectivity*w.numevents*2) ) ,2 ) 
                               + "% failed." );
            System.out.println(" Average step length " + w.averagesteplength);
        
//            w.GraphDataFileOutput("#", walktimetaken );
            w.FileOutputGraphInfo(commentcharacter, walktimetaken);    
            if (w.TotalNumberVertices<20) w.printNetwork();
            w.calcComponents();
            w.printComponentInfo();
            w.FileOutputComponentInfo("#");
            w.FileOutputComponentDistribution("#");
            int vn = w.Rnd.nextInt(w.TotalNumberVertices);
            w.calcRingParameters(vn);
            w.printRingInfo(System.out,vn);
            w.FileOutputRingInfo("#", vn);
            w.FileOutputDegreeDistribution("#", false);
}
    

}




  
    
      
