/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.io.FileInput;
import TimGraph.run.arXiv.arXivIndexComparatorOldestFirst;
import TimGraph.run.arXiv.arXivIndexComparatorYoungestFirst;
import TimGraph.timgraph;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import java.lang.String;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author time
 */
public class ProcessArxivData {


    final static String SEP = "\t";
    static String basicroot="UNSET";

    static timgraph tg;
    static int infoLevel=0;

    static final String genericLabel="yymmnnn";
    static final int labelLength=genericLabel.length();


/**
 * Negative gamma means extract value from the epo string looking for the part between WLG_VP and .dat
 * <p>Ignores any line starting with a * in the file.
 * @param args
 */
   public static void main(String[] args)
    {
      System.out.println("ProcessArxivData Arguments :<filename> :<sourceColumn> :<targetColumn>");

      int ano=0;
      String inputDir="input/arxivcitations/";
      String outputDir="output/arxivcitations/";
      //String inputFileName="arxivcitationstest.dat";
      //String inputFileName="hep-ph-citations.dat";
      //String inputFileName="hep-th-citations.dat";
      //String inputFileName="Cit-HepTh.dat";
      String inputFileName="Cit-HepPh.dat";
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) inputFileName=args[ano].substring(1, args[ano].length());
      System.out.println("--- arXiv data from file "+inputFileName);

      ano++;
      int columnSource =1;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) columnSource=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Column with edge index  "+columnSource);

      ano++;
      int columnTarget =2;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) columnTarget=Integer.parseInt(args[ano].substring(1, args[ano].length()));
      System.out.println("--- Column with edge community  "+columnTarget);

      ano++;
      boolean removedOn =true;
      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) removedOn=TimUtilities.StringAsBoolean.isTrue(args[ano].charAt(1));
      System.out.println("--- Removing bad edges  "+TimUtilities.BooleanAsString.booleanToYesNo(removedOn));

      boolean paddingOn =true;


      ProcessArxivData pad = new ProcessArxivData();
      pad.readArxivCitationGraph(inputDir, outputDir, inputFileName, columnSource, columnTarget, removedOn, paddingOn);

    }

   /**
    * Read in arXiv citation graph.
    * <p>data has source as the paper doing the citing,
    * and the target as the paper being cited.
    * The papers are in yymmnnn format. Links in wrong
    * directions of time may be removed if option chosen.
    * Links which have labels which are too long or have bad dates encoded
    * are always removed.
    * @param inputFileName full name of file
    * @param inputDir input directory
    * @param inputDir input directory
    * @param outputDir output directory
    * @param columnSource source column numbered with first column 1
    * @param columnTarget target column numbered with first column 1
    * @param removeOn true if want to remove any links found in wrong direction of time
    * @param paddingOn true if want to pad short labels with zeros
    */
   public void readArxivCitationGraph(String inputDir, String outputDir,
           String inputFileName, int columnSource, int columnTarget,
            boolean removedOn,boolean paddingOn){
      //boolean removedOn=true;
      System.out.println("--- Bad links "+(removedOn?"":"not")+" removed if found.");
      System.out.println("--- "+((paddingOn)?"Padding":"No padding")+" of short labels.");
      FileInput fi = new FileInput(infoLevel);
      int columnWeight=-1; // no columns with weights
      int columnLabel=-1; // no columns with labels
      boolean oldestFirst=true;
      Comparator<String> indexComp;
      if (oldestFirst){
          indexComp = new arXivIndexComparatorOldestFirst();
      } else {
          indexComp = new arXivIndexComparatorYoungestFirst();
      }
//      if (oldestFirst){
//          sourceLL = new TreeSet(new arXivIndexComparatorOldestFirst());
//          targetLL = new TreeSet(new arXivIndexComparatorOldestFirst());
//      } else {
//          sourceLL = new TreeSet(new arXivIndexComparatorYoungestFirst());
//          targetLL = new TreeSet(new arXivIndexComparatorYoungestFirst());
//      }
      TreeSet<String> sourceLL = new TreeSet(indexComp);
      TreeSet<String> targetLL = null;
      ArrayList<String> edgeLL = new ArrayList();
      DoubleArrayList weightLL = null;
      IntArrayList labelLL = null;
      boolean headerOn=false;
      String inputFullFileName=inputDir+inputFileName;
      fi.readStringEdgeFile(inputFullFileName,
            columnSource, columnTarget, columnWeight, columnLabel,
            sourceLL, targetLL, edgeLL,
            weightLL, labelLL,
            headerOn);
      // order all the labels

      //Iterator<String> ei = edgeLL.iterator();
      String s;
      String t;
      int linenumber=0;
      int warning=0;
      int warningPad=0;
      int warningDate=0;
      int warningMax=10;
      int edgesRemoved=0;
      boolean removeThisEdge=false;
      for (int stub=0; stub<edgeLL.size(); stub++){
          removeThisEdge=false;
          s=edgeLL.get(stub);
          if (s.length()>labelLength) throw new RuntimeException("*** At line number "+linenumber+" found label too long: "+s);
          if (s.length()<labelLength) {
              warningPad++;
              if (warningPad<warningMax)  System.err.println("!!! line "+linenumber+":"+s+" needs padding with zeros");
              if (warningPad==warningMax) System.err.println("!!! No more padding warnings");
              if (paddingOn){
                  s=padWithZeros(s,labelLength);
                  edgeLL.set(stub, s);
              }
          }
          stub++;
          linenumber++;
          if (stub>=edgeLL.size()) throw new RuntimeException("*** odd number of vertices "+edgeLL.size()+" in edge list");
          t=edgeLL.get(stub);
          if (t.length()>labelLength) throw new RuntimeException("*** At line number "+linenumber+" found label too long: "+t);
          if (t.length()<labelLength) {
              warningPad++;
              if (warningPad<warningMax)  System.err.println("!!! line "+linenumber+":"+t+" needs padding with zeros");
              if (warningPad==warningMax) System.err.println("!!! No more padding warnings");
              if (paddingOn){
                  t=padWithZeros(t,labelLength);
                  edgeLL.set(stub, t);
              }
          }
          if (indexComp.compare(s, t)>0) {
              warning++;
              if (removedOn) removeThisEdge=true;
              if (warning<warningMax)  System.err.println("!!! line "+linenumber+":"+s+">="+t+".");
              if (warning==warningMax) System.err.println("!!! No more warnings");
          }
          if ( (!isValidarXivString(s)) || (!isValidarXivString(t)) ) {
              warningDate++;
              removeThisEdge=true;
              if (warningDate<warningMax)  System.err.println("!!! line "+linenumber+" date in label wrong :"+s+" or "+t+".");
              if (warningDate==warningMax) System.err.println("!!! No more date warnings");
          }
          if (removeThisEdge) {
              edgesRemoved++;
                  stub--; // point to source
                  edgeLL.remove(stub);
                  edgeLL.remove(stub); // target now where stub was
                  stub--; // now point to previous last good source
                  removeThisEdge=false;
              }


      }
      if (warning>0) System.err.println("!!! "+warning+" edges of total "+edgeLL.size()/2
              +" edges found with wrong order"
              +(removedOn?" and removed.":" but left."));
      else System.out.println("--- No labels in wrong order found");


      if (warningPad>0) System.err.println("!!! "+warningPad+" stubs of total "+edgeLL.size()
              +" stubs found with short length label"
              +(paddingOn?" and were padded with zeros.":" but were left alone."));
      else System.out.println("--- No labels padded");

      if (warningDate>0) System.err.println("!!! "+warningDate+" labels of total "+edgeLL.size()
              +" found with bad dates encoded in labels");
      else System.out.println("--- All labels had sensible dates");

      if (edgesRemoved>0) System.err.println("!!! "+edgesRemoved
              +" edges of total "+edgeLL.size()/2+" were removed.");
      else System.out.println("--- No edges removed");

      TreeMap<String,Integer> labelToIndex = new TreeMap(indexComp);
      int index=0;
      String newLabel;
      for (String label : sourceLL){
          if (paddingOn) newLabel=padWithZeros(label,labelLength);
          else newLabel=label;
          if (sourceLL.size()<40) System.out.println(newLabel +" -> "+ index);
          labelToIndex.put(newLabel, new Integer(index++));
      }


      // output files
      int dot = inputFileName.lastIndexOf('.');
      String rootName = inputFileName;
      if (dot>=0) rootName = inputFileName.substring(0, dot);
      //FileNameSequence fns = new FileNameSequence(rootName,"outputEL.dat");
      boolean messagesOn=true; //false;
      TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputDir+rootName+"sortedoutputLabelToIndex.dat",
              SEP, labelToIndex, messagesOn) ;
      int numberPerLine=2;
      TimUtilities.FileUtilities.FileOutput.FileOutputCollection(outputDir+rootName+"sortedoutputEL.dat",
              SEP, edgeLL, labelToIndex,  numberPerLine, messagesOn);
      TimUtilities.FileUtilities.FileOutput.FileOutputCollection(outputDir+rootName+"sortedoutputELS.dat",
              SEP, edgeLL, null,  numberPerLine, messagesOn);

    }


   /**
    * Pad string with zeros upto required length.
    * <p>Strings longer than l are left unchanged.
    * @param s string to be padded
    * @param l length required
    * @return string of length l (or more) padded with zeros as needed.
    */
   public String padWithZeros(String s, int l){
       while (s.length()<l) s="0"+s;
       return s;
   }

   /**
    * Assumes yymmnnn format.
    * Years must be between 91 and 03 inclusive, month between 1 and 12
    * @param s string to test
    * @return false if wrong length or fails other tests.
    */
   public boolean isValidarXivString(String s){
      final int labelLength="yymmnnn".length();
      if (s.length()!=labelLength) return false;
      try{
          int year = Integer.parseInt(s.substring(0, 2));
          int month = Integer.parseInt(s.substring(2, 4));
          if (year>99) return false;
          if (year<0) return false;
          if (year<91 && year>03) return false;
      } catch (Exception e){
          return false;
      }
      return true;
   }
//       /**
//    * Provides comparison of arXiv identities
//    * <p>arXiv papers are labelled yymmnnn but this means all
//    * papers in years 2000 and later are less than papers published before.
//    * This methods puts the youngest most recent papers before the
//    * older papers. Example 9801987 comes before 9902543 which comes before
//    * 0011001.
//    */
//   public class arXivIndexComparatorYoungestFirst implements Comparator<String> {
//        @Override
//       public int compare(String s1, String s2){
//           String ss1 = (s1.charAt(0)<'5'?1:0)+s1;
//           String ss2 = (s2.charAt(0)<'5'?1:0)+s2;
//           return ss1.compareTo(ss2);
//       }
//        public boolean equal(String s1, String s2){
//           return s2.equals(s1);
//       }
//
//   }
//   /**
//    * Provides comparison of arXiv identities
//    * <p>arXiv papers are labelled yymmnnn but this means all
//    * papers in years 2000 and later are less than papers published before.
//    * This methods puts the oldest papers before the
//    * more recent and younger papers. Example 0011001
//    * comes before 9902543 which comes before
//    * 9801987 .
//    */
//   public class arXivIndexComparatorOldestFirst implements Comparator<String> {
//        @Override
//       public int compare(String s1, String s2){
//           String ss1 = (s1.charAt(0)<'5'?1:0)+s1;
//           String ss2 = (s2.charAt(0)<'5'?1:0)+s2;
//           return ss2.compareTo(ss1);
//       }
//       public boolean equal(String s1, String s2){
//           return s2.equals(s1);
//       }
//   }

}
