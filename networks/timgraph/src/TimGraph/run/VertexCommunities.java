/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.*;
import TimGraph.Community.Community;
import java.io.PrintStream;

import TimGraph.Community.VertexPartition;
import TimGraph.Community.QualitySparse;
import TimGraph.Community.QualityType;
import TimGraph.io.FileOutput;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;




/**
 * Basic access to routines that produce vertex partitions of a network.
 * @author time
 */
public class VertexCommunities {
    
    final static String SEP = "\t";
    static String basicroot="UNSET";
      
    static timgraph tg;
    static int infoLevel=0;
    
    public VertexCommunities(timgraph newtg){tg=newtg;}
    
    
    static public void usage(PrintStream PS){
     char arg=timgraph.NOT_TIMGRAPH_ARGUMENT[0];   
     PS.println("VertexCommunities "+arg+"<filename> "+arg+"<method> "+arg+"<lineGraphType> "+arg+"<quality> "+arg+"<lambda> -<timgraphArgument> ...");   
     PS.println("Arguments for this class start with any of "+timgraph.otherArguments(", ")+", but "+arg+" used for example here.");
     PS.println("First "+arg+" argument is the network number for the setup routine OR ? for this help");
     PS.println("Second "+arg+" argument is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing");
     PS.println("Third "+arg+" argument is definition of quality to use, 0=Q(A), 1=Q(A"+arg+"A-A)");
     PS.println("Fourth "+arg+" argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory ");
     PS.println("Fifth "+arg+" argument is value of lambda, scaling parameter of null model.");
     PS.println("Remaining arguments should start with a "+timgraph.TIMGRAPH_ARGUMENT+" and are parsed by <tt>timgraph</tt>.");  
     SetUpNetwork.printNetworkTypes(PS, "", "  ");
    }
    
    /**
     * Basic routine to study vertex communities.
     * <p>The initial arguments start which with a ':' 
     * (or any other character in <tt>timgraph.NOT_TIMGRAPH_ARGUMENT[]</tt>).
     * <p>First ':' argument is the number of the network as specified by <tt>setUpNetwork</tt>.  
     * 0 means use the file specified in the timgraph arguments.     
     * ? means print out some help.
     * <p>Second ':' argument is the method used to find communities, 0=Louvain/Greedy, 1= Simulated Annealing
     * <p>Third ':' argument is type of quality to use, 0=Q(A), 1=Q(A*A-A)
     * <p>Fourth ':' argument is type of quality class to use, 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory
     * <p>Fifth ':' argument is value of lambda, scaling parameter of null model.
     * <p>Remaining arguments should start with a '-' and are parsed by <tt>timgraph</tt>.
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    { 
      //System.out.println("Current directory is "+System.getProperty("user.dir"));
      //System.out.println("Current user's home directory is "+System.getProperty("user.home"));
      
        System.out.println("VertexCommunities Arguments :<networknumber> :<method> :<qualitydef> :<qualitytype> :<lambdaMin> :<lambdaMax> :<lambdaStep>");
      
      //First arg chooses network
      int n=0; //201; //30; //60; //1 30;
      int ano=0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) {
          if (args[ano].charAt(1)=='?') {usage(System.out); return;} 
          else n=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      }
      else n=0;
      System.out.println("--- Using network  "+SetUpNetwork.typeString(n));

      int method =0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) method=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Using community method "+LineGraphCommunities.COMMUNITYMETHOD[method]);


      // 0="QS", 1="QA2mA"
      int qdef=0; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qdef=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality definition used "+QualitySparse.QdefinitionString[qdef]);

      // qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
      int qualityType=1; 
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) qualityType=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Quality class type "+QualityType.qualityLongName[qualityType]);
     

      double lambdaMin = 1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) lambdaMin=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      
      double lambdaMax = lambdaMin+1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) lambdaMax=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      

      double minStep=1e-3;
      double lambdaStep = lambdaMax-lambdaMin;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) lambdaStep=Double.parseDouble(args[ano].substring(1, args[ano].length()));
      if (Math.abs(lambdaStep)<minStep) {
          throw new RuntimeException("Step size must be greater than "+minStep+" in magnitude");
      }
      System.out.println("--- Modularity null model scaling lambda min, max step ="+lambdaMin+"  "+lambdaMax+"  "+lambdaStep);    
      
      
      String basicFileName="ukwardsinputELS.dat";
      String [] aList = { "-fin"+basicFileName, "-o511"};
      if (args.length>0) aList=args;
      
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      
      tg = SetUpNetwork.setUpNetwork(n, aList);

      tg.calcStrength();

      FileOutput fo = new FileOutput(tg);
      PrintStream PS;
      FileOutputStream fout;
      fo.fileName.setNameEnd("lambda_"+lambdaMin+"_"+lambdaMax+"_"+lambdaStep+".dat");
        // next bit of code p327 Schildt and p550
        try {
            fout = new FileOutputStream(fo.fileName.getFullFileName());
            PS = new PrintStream(fout);
            Community c = new Community(tg, qdef, lambdaMin, qualityType,infoLevel);
            PS.println("name"+SEP+c.informationNumbersLabel("", SEP));
            boolean graphMLOutput=false;
            for (double lambda=lambdaMin; lambda<=lambdaMax; lambda+=lambdaStep){
               if (lambda<=0) break;
               System.out.println("-------------------------------------------------------");
               VertexPartition vp = calculateVertexPartition(tg, qdef, qualityType, lambda, method, infoLevel, graphMLOutput);  
               PS.println(vp.getName()+SEP+vp.informationNumbers("", SEP));
            } 
            if (infoLevel>-2) System.out.println("Finished writing VP various lambda information file to "+ fo.fileName.getFullFileName());
            try{ fout.close ();   
               } catch (IOException e) { System.out.println("File Error");}
            
        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fo.fileName.getFullFileName()+", "+e.getMessage());
            return;
        }

      
    }
    
    static public VertexPartition calculateVertexPartition(timgraph tg, int qdef, int qualityType, double lambda, int method, int infoLevel,
            boolean graphMLOutput){
      FileOutput fo = new FileOutput(tg);
      VertexPartition vp = VertexPartition.calculate(tg, qdef, qualityType, lambda, method, infoLevel); 
      tg.setVertexNumbers(vp);
      fo.informationPartition(vp, "", SEP, true);
      fo.printPartitionStats(vp, "", SEP, true, true);
      fo.printVertices("", SEP, false, true, vp, false);
      fo.printVertices("", SEP, false, false, vp, true);
      fo.informationGeneral("", SEP);
      if (tg.outputControl.pajekFileOn) fo.pajekPartition(vp.getName()+".clu", vp);
      if (graphMLOutput) fo.graphMLVertexPartition(vp);

      timgraph projg = vp.getVertexPartitionGraph();
      fo = new FileOutput(projg);
      fo.informationGeneral("", SEP);
      fo.graphML();
      return vp;
    }

    


}
