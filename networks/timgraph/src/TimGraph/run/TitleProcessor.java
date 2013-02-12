/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimUtilities.StringUtilities.Filters.TextFileProcessor;
import TimGraph.timgraph;
import TimUtilities.StringUtilities.Filters.ImperialPapersFilter;
import java.util.TreeMap;

/**
 *
 * @author time
 */
public class TitleProcessor {


       /**
     * Processes a title.
     * <p>Applys stemming and filtering.
     * @param tg input graph
     * @param basicFileName basic file name, other info is added to this.
     * @param convertIgnoreColumn if a column is being ignored you can convert it to a number (np added to file name) equal to the line number minus 1 (so counts from 0).
     * @param numberLinesToSkip Only takes certain fraction of lines
     * @param showProcess true if want information displayed on screen.
     */
    public static void process(timgraph tg,String basicFileName, boolean convertIgnoreColumn, int numberLinesToSkip, boolean showProcess){
      String preStemmedFileName=tg.inputName.getDirectoryFull()+basicFileName+"ptinputBVNLS.dat";
      String outputroot = basicFileName+(convertIgnoreColumn?"np":"")+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem";
      String postStemmedFileName=tg.inputName.getDirectoryFull()+outputroot+"ptinputBVNLS.dat";

      TreeMap<String,String> stemMap = new TreeMap();
      TreeMap<String,Integer> acceptedCountMap = new TreeMap();
      int columnIgnored =1;
      String sep="\t";
      ImperialPapersFilter ipf = new ImperialPapersFilter(2,3,true);
      TextFileProcessor.processWordListFile(preStemmedFileName, postStemmedFileName, columnIgnored,  convertIgnoreColumn,"\t ",stemMap, acceptedCountMap, ipf, numberLinesToSkip, false);
      System.out.println("Stemming file "+preStemmedFileName+", taking every "+numberLinesToSkip+" lines only, paper gid "+(convertIgnoreColumn?"converted to simple index":"left as original string"));
      System.out.println("Applied "+ipf.description());
      System.out.println("Producing file "+postStemmedFileName);

      String outputFileName = tg.outputName.getDirectoryFull()+outputroot+"ptStemMap.dat";
      TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputFileName, sep, stemMap, true);
      outputFileName = tg.outputName.getDirectoryFull()+outputroot+"ptRejectList.dat";
      ipf.FileOutputRejectedList(outputFileName, showProcess);
      }


}
