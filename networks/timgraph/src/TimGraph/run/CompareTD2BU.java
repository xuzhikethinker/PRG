/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import DataAnalysis.MutualInformation;
import TimGraph.run.CompareVertexCommunities;
import TimGraph.timgraph;
import TimUtilities.FileUtilities.FindFile;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Compare TopDown to Bottom Up communities.
 * @author time
 */
public class CompareTD2BU {
/**
 * Compare TopDown to Bottom Up communities.
 * <p>CompareTD2BU :<tt>dirName2</tt> :<tt>rootName2</tt> :<tt>ext2</tt>
 * :<tt>outputNameRoot</tt> (*timgraph arguments)
 * <p>First argument is :fileName1 which gives the first community,
 * the fixed standard against which all others are compared.
 * The second community is a set formed from the next three arguments
 * :<tt>dirName2</tt> :<tt>rootName2</tt> :<tt>ext2</tt>
 * so note the <tt>dirName2</tt> must end in a forward slash.
 * This means that file names of the form
 * <tt>dirName2</tt><tt>rootName2</tt>*<tt>ext2</tt> are searched for
 * with the * substring used as the value of gamma involved.
 * The last argument :<tt>outputNameRoot</tt> gives the full directory
 * and name root of the output file.
 * @param args
 */
    public static void main(String[] args)
    {
      System.out.println("CompareTD2BU  :fileName1 :dirName2 :rootName2 :ext2 :outputNameRoot :gammaMultiplier");
//      System.out.println("CompareTD2BU  takes compares a series of vertex communties against a standard community");
//      System.out.println("for the timgraph defined by the");
//      System.out.println("final timgraph arguments (including the input file name and directory).");
//      System.out.println("The first community is given by fileName1 and is the fixed standard against");
//      System.out.println("which all others are compared.  The second community is a set formed from");
//      System.out.println("<dirName2><rootName2>*<ext2> so note the dirName2 must end in a slash.");
//      System.out.println("The * is taken to be the values of gamma involved.  The last argument");
//      System.out.println("outputNameRoot gives the full directory and name root of the output file.");
// 
      //First arg chooses first community file
      //String fileName1="input/ICtest_psecinputBVNLS.dat";
      //String fileName1="input/ICNS090224npstemppew0020inputELS.dat";
      //String fileName1="input/ICNS090729_psecinputBVNLS.dat";
      String fileName1="input/karateTSEActualVPinputBVNLS.dat";
      int ano=0;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) fileName1=args[ano].substring(1);
      System.out.println("--- Using reference network "+fileName1);


      //String dirName2="output/IC/PaperTerm/";
      String dirName2="output/karate/";
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) dirName2=args[ano].substring(1);

      //String rootName2="ICNS090729stempt_PapersVCWLG_VP";
      String rootName2="karateTSE_VC_WLG_VP";
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) rootName2=args[ano].substring(1);

      String ext2 = "output.vcis";
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) ext2=args[ano].substring(1);

      System.out.println("--- Comparing against networks "+dirName2+rootName2+"(gamma)"+ext2);

      //Third arg is output file name
      String rootName3="output/karateTSE_actual_WLG_VP";
      //String rootName3="output/ICNS090729stempt_psec_Papers";
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) rootName3=args[ano].substring(1);
      System.out.println("--- Output to  "+rootName3);

      //Third arg is gamma multiplier
      double gammaMultiplier=1.0;
      ano++;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) gammaMultiplier=Double.parseDouble(args[ano].substring(1));
      System.out.println("--- gamma multiplier  "+gammaMultiplier);


      FindFile ff = new FindFile();
      ff.getFileList(dirName2, rootName2, ext2, true);
      int nf = ff.filelist.length;
      String [] name2Array = new String[nf];
      MutualInformation [] miArray = new MutualInformation[nf];
      double [] gammaArray = new double[nf];
      PrintWriter results = null;
      String outputFile=rootName3+"_gammaMI.dat";
      try {results = new PrintWriter(new FileWriter(outputFile));}// eo try
      catch (IOException e){
          System.err.println("Problem with output file "+outputFile+", "+e);
          System.exit(1);
      }
      System.out.println("--- Opened output file "+outputFile);
      String sep="\t";
      results.println(MutualInformation.toStringBasicLabel(sep)+sep+"gamma"+sep+MutualInformation.toStringLabel(sep));

      for (int f=0; f<nf; f++){
          String fullFileName2=dirName2+ff.filelist[f];
          System.out.println("--- Using network two "+fullFileName2);
          int c0= fullFileName2.lastIndexOf(rootName2)+rootName2.length();
          int c1= fullFileName2.lastIndexOf(ext2);
          String gammaString=fullFileName2.substring(c0, c1);
          double gamma=Double.parseDouble(gammaString)*gammaMultiplier;
          System.out.println("--- gamma =  "+gamma);
          
          MutualInformation mi = CompareVertexCommunities.doComparision(fileName1,fullFileName2);
          results.println(mi.toStringBasic(sep)+sep+gamma+sep+mi.toString(sep));
          results.flush();
          miArray[f]= new MutualInformation(mi,false,false);
          name2Array[f]=ff.filelist[f];
          gammaArray[f]=gamma;
      } // eo for
      results.close();
      System.out.println("--- Closed output file "+outputFile);
    }

     
}
