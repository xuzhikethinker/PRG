/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.timgraph;
import TimGraph.io.FileOutput;
//import TimGraph.Community.VertexPartition;
import java.util.TreeSet;

/**
 * Runs analysis of South Florida Word Assoication Data
 * @author time
 */
public class WordAssociation {
    
    final static String SEP = "\t";
    
    static String basicroot="UNSET";  
    static timgraph tg;
    static timgraph lineGraphtg; 
    static int infoLevel=2;
      
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
      SetUpNetwork setnet = new SetUpNetwork(infoLevel);
      //String name="SFWANPDFV05bright3cons025";
      

//      int n=0;

      String name="SFWANPDFV05cons025LouvainQS10000WLG";
                 //SFWANPDFV05cons025LouvainQS10000WLG
      //SFWANPDFV05cons025l100000"; 
      //String name="SFWANPDFV05cons025";
      
      int ano=0;
      if (args.length>ano ) if (args[ano].charAt(0)=='*') {
          name = args[ano].substring(1, args[ano].length());
      }    
      
      String wordName ="bright";
      ano++;
      if (args.length>ano ) if (args[ano].charAt(0)=='*') {
          wordName = args[ano].substring(1, args[ano].length());
      }    
      
      // set cutoff for strength within community in final graph, if less than one using fractional, otherwise aboslute
      double min=0.9;
      ano++;
      if (args.length>ano ) if (args[ano].charAt(0)=='*') {
          min = Double.parseDouble(args[ano].substring(1, args[ano].length()));
      }    
      boolean normaliseOn;
      if (min<1.0) normaliseOn=true; else normaliseOn=false;
      
      
      
      System.out.println("--- Looking at file name "+name+" for subgraph centred on word "+wordName+", "+(normaliseOn?"fractional ":"absolute ")+"cutoff "+min);
      
      String [] aList = { "-gvet", "-gdf", "-gewt", "-gelt", "-gvlt", "-fin"+name, "-fieinputELS.dat", "-gn99",  "-e0", "-o11", "-xi0"};      
      timgraph big = setnet.setUpNetwork(name, aList, infoLevel);
      if ((infoLevel>0) && big.getNumberVertices()<20) big.printNetwork(true);
      
      TreeSet labelSet =big.getEdgeLabelSet();
      int numberLabelsBig = labelSet.size();
      System.out.println("Network "+big.inputName.getNameRoot()+" has "+numberLabelsBig+" distinct labels");
      
      FileOutput fobig = new FileOutput(big);
      fobig.printEdges(true, false, false, false, true); 
      fobig.graphMLEdgePartition(numberLabelsBig);
        
//      String epname="LouvainQSWLGEP_";
//      
//      VertexPartition ep = new VertexPartition(epname,big.getNumberStubs()/2);
//      ep.readIntPartition(big.inputName.getNameRootFullPath()+"inputEP.dat", 1, 2, false); 
//
//      big.setEdgeLabels(ep);
      // need edge weights on as labels carry the partition
      tg= big.makeEdgeSubGraph(wordName, false, true, big.isVertexLabelled(), big.isVertexEdgeListOn());
      //tg.setNameRoot(epname+wordName+"V_");
      tg.printParametersBasic();
      if ((infoLevel>0) && tg.getNumberVertices()<20) tg.printNetwork(true);
      int numberLabels=tg.relabelEdges(wordName);
      System.out.println("Network "+tg.inputName.getNameRoot()+" has "+numberLabels+" distinct labels around word "+wordName);
      labelSet = tg.getEdgeLabelSet();
      int numberLabelsTotal = labelSet.size();
      System.out.println("Network "+tg.inputName.getNameRoot()+" has "+numberLabelsTotal+" distinct labels ");
      
      // output graph as is.
      //tg.setNameRoot(name+"edgeSG");
      FileOutput fo = new FileOutput(tg);
      fo.informationGeneral("", SEP);
      fo.printEdges(true, false, false, false, true); 
      fo.graphMLEdgePartition(numberLabels);
      boolean splitBipartite=false;
      boolean outputType1=false;
      fo.printEdgeCommunityStats(true, true, splitBipartite, outputType1);
      
      // min value set as third parameter double min=0.9;
      int imin = (int)(min*1000);
      TreeSet<Integer> sgvertexList = tg.edgeLabelToVertexSet(min, normaliseOn, false);
      int vvv = tg.getVertexFromName(wordName);
      sgvertexList.add(vvv);
      
      timgraph newgraph = tg.projectSubgraph("",sgvertexList, tg.isDirected(), 
              tg.isVertexLabelled(), tg.isWeighted(), tg.isVertexEdgeListOn(),
              tg.isBipartite());
      String ending; //="f"+((int)(min*1000));
      if (normaliseOn) ending="f"+((int)(min*1000));
      else ending="a"+min;
      newgraph.setNameRoot(newgraph.inputName.getNameRoot()+"_"+"EP2VC"+ending);
      
      labelSet = newgraph.getEdgeLabelSet();
      numberLabelsTotal = labelSet.size();
      System.out.println("Network "+newgraph.inputName.getNameRoot()+" has "+numberLabelsTotal+" distinct labels ");
      FileOutput fon = new FileOutput(newgraph);
      fon.informationGeneral("", SEP);
      fon.graphMLEdgePartition(numberLabels);
      splitBipartite=false;
      outputType1=false;
      fon.printEdgeCommunityStats(true, true, splitBipartite, outputType1);


    }


}
