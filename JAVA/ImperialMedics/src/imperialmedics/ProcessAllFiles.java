package imperialmedics;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 * Process all files.
 * First process the Stata10... file with existing data by author and period.
 * Next process the individual publication files.
 * Finally merge the stats on each individual and period with the existing data.
 * @author time
 */
public class ProcessAllFiles {

    //String rootFileName = ProcessSinglePublicationCSVList.SUNSET;
    static String inputSinglePublicationDirectory ="input\\individuals\\";
    static String outputSinglePublicationDirectory = "output\\individuals\\";
    static String SEP="\t";


       public static void main(String[] args) {

        //Produce list of medical journals
        ProcessScopusJournalLists psjl = new ProcessScopusJournalLists();
        String rootFileName = "SCOPUS_Journal_Classification_title_list_Simple.txt";
        //String rootFileName = "ScopusMedicalJournals.dat";
        String inputDirectory ="input\\journals\\";
        boolean infoOn=false;
        String fullFileName=inputDirectory+rootFileName;
        int infoLevel=0;
        TreeSet<JournalWithASJC> journalSet;
        journalSet = psjl.readJournalWithASJCData(fullFileName, infoLevel);
        System.out.println("Created list of "+journalSet.size()+" unique medical ISSN numbers");

           
        // next process the existing statistics on all authors
        ProcessAllAuthorFile paaf = new ProcessAllAuthorFile();
        //paaf.rootFileName = "Stata10networkspreadsheetfinalNoPW";
        paaf.rootFileName = "Stata10networkspreadsheetfinalTSE";
        //paaf.rootFileName = Stata10test";
        paaf.inputDirectory ="input\\";
        paaf.outputDirectory ="output\\";
        infoOn=false;
        paaf.processXLSFile(infoOn);
        //String outputFullFileName = paaf.outputDirectory+paaf.rootFileName+" PeriodStats.dat";
        //System.out.println("Periods "+pa.periodStats.length);
        //writePeriodStatsData(outputFullFileName,  pa.periodStats, infoOn);

        // test author comparison, only problems printed out.    
        int nProblems=paaf.testAuthorSet(false);
        if (nProblems==0) { 
            System.out.println("All author set are all distinct");
        }
        else {
            System.out.println("### All author set are not all distinct, found "+nProblems+" problems");
        } 

        // now process the publications of individual authors
        FindFile ff= new FindFile();
        String ext=".csv";
        ff.getFileList(inputSinglePublicationDirectory, ext) ;
        System.out.println("!!!\n!!! Found "+ff.getNumberFiles()+" individual publication files to process\n!!!");

        PrintStream PS;
        FileOutputStream fout;
        //boolean infoOn=true;
        String allAuthorOutputFullFileName=paaf.outputDirectory+"AllAuthorInformation.dat";
        if (infoOn) System.out.println("Writing file of all author data "+ allAuthorOutputFullFileName);
            try {
            fout = new FileOutputStream(allAuthorOutputFullFileName);
            PS = new PrintStream(fout);
            PS.println("File"+SEP+ProcessSinglePublicationCSVList.summaryStringLabel);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Error opening output file "+allAuthorOutputFullFileName+" "+e.getMessage());
        }
        for (int f=0; f<ff.getNumberFiles(); f++){
            //if (f>0) break;
            System.out.println("$$$\n$$$ file "+f+"\n$$$");
            ProcessSinglePublicationCSVList pspl = new ProcessSinglePublicationCSVList();
            pspl.rootFileName = ff.getFileNameRoot(f);
            pspl.inputDirectory = inputSinglePublicationDirectory;
            pspl.outputDirectory = outputSinglePublicationDirectory;
            pspl.journalSet=journalSet;
            //pspl.processCSVFileForAuthorData(null, infoOn);
            pspl.processCSVFileForAuthorData(infoOn);
            PS.println(f+SEP+pspl.summaryString);
            String outputFullFileName = pspl.outputDirectory+pspl.rootFileName+"_PeriodStats.dat";
            boolean infoOnPSPL=false;
            ProcessSinglePublicationCSVList.writePeriodStatsData(outputFullFileName,  pspl.periodStats, infoOnPSPL);


            Author allFileAuthor=null;
            boolean foundAuthor=false;
            for (Author indAuthor: pspl.primaryAuthorList){
                if (indAuthor.getSurnames().startsWith("Jones") ) throw new RuntimeException("!!!HELP JONES");
                allFileAuthor = paaf.authorSet.floor(indAuthor);
                if ((allFileAuthor==null) || (!allFileAuthor.equalUptoFirstInitial(indAuthor))) continue;
                if (!allFileAuthor.equalsExactly(indAuthor)){
                    System.out.println("!!! Individual file author "
                        +indAuthor.toStringWithTitlesAndID()
                        +" is not exactly equal to all file author "
                        +allFileAuthor.toStringWithTitlesAndID());
                    System.err.println("!!! Individual file author "
                        +indAuthor.toStringWithTitlesAndID()
                        +" is not exactly equal to all file author "
                        +allFileAuthor.toStringWithTitlesAndID());
                }
                foundAuthor=true;
                System.out.println("Individual file author "
                        +indAuthor.toStringWithTitlesAndID()
                        +" is equal to all file author "
                        +allFileAuthor.toStringWithTitlesAndID());
                break;
            }
            if (!foundAuthor){
//                throw new RuntimeException("Individual file author "+pspl.getPrimaryAuthor()+" can not be found in all author file");
                System.out.println("### Individual file author "
                        +pspl.getPrimaryAuthor()+" can not be found in all author file");
                System.err.println("### Individual file author "
                        +pspl.getPrimaryAuthor()+" can not be found in all author file");
            }

            AuthorWithData awd = (AuthorWithData) allFileAuthor;
            awd.setPeriodData(pspl.periodStats); // update with info for this period

            } //eo for f


            try{ fout.close ();
               } catch (IOException e) { throw new RuntimeException("*** File Error with " +allAuthorOutputFullFileName+" "+e.getMessage());}
            if (infoOn) System.out.println("Finished writing file of all author data "+ allAuthorOutputFullFileName);
            

        paaf.writeTextFile(infoOn);

        }

    }


