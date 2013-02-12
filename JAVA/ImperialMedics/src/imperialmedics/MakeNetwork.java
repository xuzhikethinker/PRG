/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imperialmedics;

import java.util.TreeSet;

/**
 *
 * @author time
 */
public class MakeNetwork {

    public static void main(String[] args) {

        // set up empty networks with enough room and of coorect types
        CoauthorshipGraphs cg;
        boolean infoOn=true;
        int maxVertices=5000;
        int maxStubs=maxVertices*10;
        int numberPeriods=ProcessSinglePublicationCSVList.yearBoundary.length+1;
        
        cg = new CoauthorshipGraphs (maxVertices, maxStubs,numberPeriods);


        String fileSeparator = System.getProperty("file.separator");
        String inputDirectory ="input"+fileSeparator;
        String outputDirectory = "output"+fileSeparator;

        // now fill networks with IC Medic authors
        String inputFullFileName = inputDirectory+"ICMedicsAuthorList.dat";
        int infoLevel=0;
        TreeSet<Author> authorSet= ProcessAllAuthorFile.readSimpleImperialAuthorList(inputFullFileName, infoLevel);
        cg.setInitialVertices(authorSet);

        // now process individual publication lists and add edges
        String rootFileName = ProcessSinglePublicationCSVList.SUNSET;
        inputDirectory =inputDirectory+"individuals"+fileSeparator;
        

        ProcessMultiplePublicationLists mpl = new ProcessMultiplePublicationLists();
        FindFile ff= new FindFile();
        String ext=".csv";
        ff.getFileList(mpl.inputDirectory, ext) ;

        infoOn=false;
        for (int f=0; f<ff.getNumberFiles(); f++){
            ProcessSinglePublicationCSVList pa = new ProcessSinglePublicationCSVList();
            pa.rootFileName = ff.getFileNameRoot(f);
            pa.inputDirectory = mpl.inputDirectory;
            //pa.processCSVFileForAuthorData(cg, infoOn);
            String outputFullFileName = pa.outputDirectory+pa.rootFileName+" PeriodStats.dat";
            //ProcessSinglePublicationCSVList.writePeriodStatsData(outputFullFileName,  pa.periodStats, infoOn);
        }
    }

}
