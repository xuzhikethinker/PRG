package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author time
 */
public class ProcessMultiplePublicationLists {
    
    String rootFileName = ProcessSinglePublicationCSVList.SUNSET;
    String inputDirectory ="input\\individuals\\";
    String outputDirectory = "output\\individuals\\";

    
    public static void main(String[] args) {
        ProcessMultiplePublicationLists mpl = new ProcessMultiplePublicationLists();
        FindFile ff= new FindFile();
        String ext=".csv";
        ff.getFileList(mpl.inputDirectory, ext) ;

        for (int f=0; f<ff.getNumberFiles(); f++){
            ProcessSinglePublicationCSVList pa = new ProcessSinglePublicationCSVList();
            pa.rootFileName = ff.getFileNameRoot(f);
            pa.inputDirectory = mpl.inputDirectory;
            boolean infoOn=false;
            //pa.processCSVFileForAuthorData(null, infoOn);
            pa.processCSVFileForAuthorData(infoOn);
            String outputFullFileName = pa.outputDirectory+pa.rootFileName+" PeriodStats.dat";
            ProcessSinglePublicationCSVList.writePeriodStatsData(outputFullFileName,  pa.periodStats, infoOn);
        }

    }





}
