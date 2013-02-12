/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run.ImperialPapers;

import TimGraph.Community.QualitySparse;
import TimGraph.Community.QualityType;
import TimGraph.Community.VertexPartition;
import TimGraph.LineGraphType;
import TimGraph.algorithms.LineGraphProjector;
import TimGraph.io.FileOutput;
//import TimGraph.run.LineGraphCommunities;
import TimGraph.run.SetUpNetwork;
import TimGraph.timgraph;

/**
 *
 * @author time
 */
public class LineGraphCommunity {
    
    final static String [] COMMUNITYMETHOD = {"Louvain","SimAn"};
    final static String SEP = "\t";
    
    static int infoLevel=0;
    
    
        /**
     * Basic routine to study communities and line graph communities.
     * <p>First argument  is the root name of the file, assumed to end in <tt>inputELS.dat</tt>.
     * <p>Second argument is the method used to find communities, default 0=Louvain/Greedy [1= Simulated Annealing]
     * <p>Third argument is type of line graph to use, default 2=D [0=C, 1=Ctilde,  3=E (Dtilde)]
     * <p>Fourth argument is type of quality to use, default 0=Q(A) [1=Q(A*A-A)]
     * <p>Fifth argument is type of quality class to use, deafult 2=minimal memory [0=basic (dense matrix), 1=sparse matrix]");
     * <p>Sixth argument is value of lambda, scaling parameter of null model, default 1.0.
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    { 
        System.out.println("ImperialPapers.LineGraphCommunity Arguments <filenameroot> <method> <linegraphtype> <qualitydef> <qualityclass> <lambda>");
      
      //First arg chooses first community file
      //String fileName1="input/ICtest_psecinputBVNLS.dat";
      String fileNameRoot="BowTie";
      int ano=0;
      if (args.length>ano ) fileNameRoot=args[ano];
      System.out.println("--- Using network  "+fileNameRoot);


      int method =0;
      ano++;
      if (args.length>ano ) method=Integer.parseInt(args[ano]);
      System.out.println("--- Using community method "+COMMUNITYMETHOD[method]);

      int lgmethod = 2; // 0=Line Graph (C), 
                      // 1= Line Graph with self-loops (Ctilde), 
                      // 2=weighted Line Graph (D), 
                      // 3=weighted Line Graph with self loops (Dtilde)
      ano++;
      if (args.length>ano ) lgmethod=Integer.parseInt(args[ano]);
      System.out.println("--- Using Line Graph type "+LineGraphType.lgExtensionDescription[lgmethod]);    

      // 0="QS", 1="QA2mA"
      int qdef=0; 
      ano++;
      if (args.length>ano ) qdef=Integer.parseInt(args[ano]);  
      System.out.println("--- Quality definition used "+QualitySparse.QdefinitionString[qdef]);

      // qualityType 0=basic (dense matrix), 1=sparse matrix, 2=minimal memory 
      int qualityType=2; 
      ano++;
      if (args.length>ano ) qualityType=Integer.parseInt(args[ano]);  
      System.out.println("--- Quality class type "+QualityType.qualityLongName[qualityType]);
     

      double lambda = 1;
      ano++;
      if (args.length>ano ) lambda=Double.parseDouble(args[ano]);
      System.out.println("--- Line Graph null model scaling lambda="+lambda);    

      timgraph tg= new timgraph();
      //String outputroot = basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem"+type+weightCutString;
      String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-gbf", "-fin"+fileNameRoot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
      SetUpNetwork.initialise(tg, fileNameRoot, aList, infoLevel);
      
      System.out.println("\n *** \n *** Making Line Graph type "+LineGraphType.lgExtensionDescription[lgmethod]+" (type "+lgmethod+") *** \n");    
      timgraph lineGraphtg = LineGraphProjector.makeLineGraph(tg,lgmethod, true);
      
      //lineGraphtg.setNameRoot(basicroot+lineGraphType);
      FileOutput folg = new FileOutput(lineGraphtg);
      //folg.printEdges(true,true,true,true,true);

      VertexPartition louvainep;
      System.out.println("*** Line Graph partition of type "+QualitySparse.QdefinitionString[qdef]+", lambda="+lambda);    
      louvainep = VertexPartition.calculate(lineGraphtg, qdef, qualityType, lambda, method, infoLevel);
      louvainep.setName(louvainep.getName() + LineGraphType.lgExtensionList[lgmethod]);
      
        tg.setEdgeLabels(louvainep);
        FileOutput fo = new FileOutput(tg);
        fo.printEdges(true,true, true, true, louvainep.getName());
        fo.printVertices("", SEP, null, false, false, false);
        boolean splitBipartite=false;
        boolean outputType1=false;
        fo.printEdgeCommunityStats(louvainep.getName(),true, true,  splitBipartite, outputType1);


        
  
    
    }

}
