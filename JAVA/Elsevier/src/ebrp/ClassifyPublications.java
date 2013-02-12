/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

import TimUtilities.MessageError;
import TimUtilities.MessageWarning;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * This classifies the publications based on the SCOPUS journal classifications.
 * <p>Each publication is assigned ASJC numbers based on those of its journal.
 * @author time
 */
public class ClassifyPublications {

    public static void main(String[] args) {

        boolean infoOn=true;
        int infoLevel=0;

        // take list of publications
        ProcessPublicationList ppl = new ProcessPublicationList();
        String rootFileName = "ebrp_03_set_01_documents.dat";
        //String rootFileName = "ebrp_03_set_01_documentsTEST.txt";
        String inputDirectory ="input/";
        String fullFileName=inputDirectory+rootFileName;
        TreeSet<ebrpPublication> pubList;
        pubList = ppl.readEBRPPublicationData(fullFileName, infoLevel);
        classify(pubList, infoLevel);
        int physicsASJC=3100;
        boolean fullForm=false;
        FileOutputMidLevelPublicationSets(pubList, physicsASJC, fullForm, infoOn);

    }
    /**
     * Classify publications.
     * Uses SCOPUS journal list from 
     * <tt>input/journals/SCOPUSJournalList120726VS.dat</tt>
     * to link journals of publication to ASJC numbers and these are assigned to 
     * journals.
     * @param pubList Set of eprbPublications
     * @param infoLevel 0 for normal, -1 minimal info, 2 for maximum debugging info
     */
    static public void classify(TreeSet<ebrpPublication> pubList, int infoLevel){
        // take list of journals
        ProcessScopusJournalLists psjl = new ProcessScopusJournalLists();
        String rootFileName = "SCOPUSJournalList120726VS.dat";
        //rootFileName = "SCOPUS_Journal_Classification_title_list_Simple.txt";
        //rootFileName = "SCOPUS_Journal_Classification_title_list_SimpleTEST.txt";
        String inputDirectory ="input/journals/";
        String fullFileName=inputDirectory+rootFileName;
        ArrayList<JournalWithASJC> js = psjl.readJournalWithASJCData(fullFileName, infoLevel);
        JournalComparatorByDataID jcbydid = new JournalComparatorByDataID();
        TreeSet<JournalWithASJC> journalSet = new TreeSet(jcbydid );
        journalSet.addAll(js);

//        // find journal inlist which matched journal of publication
//        for (JournalWithASJC j: journalSet){
//            JournalWithASJC j3 = new JournalWithASJC(j.title, "", j.did, "", ";");
//            //boolean hasISSN=j3.hasISSN();
//            JournalWithASJC j2= journalSet.floor(j3);
//            System.out.println("--- Journal from set is "+j3+(j3.hasISSN()?" (i)":" (.)")+" nearest is "+j2);
//            try {
//                System.out.println("---j-"+j.did+"--j2-"+j2.did+"--j3-"+j3.did+"---");
//                System.out.println("---j-"+(j.hasISSN()?" (i)":" (.)")
//                                  +"--j2-"+(j2.hasISSN()?" (i)":" (.)")
//                                  +"--j3-"+(j3.hasISSN()?" (i)":" (.)")+"---");
//                System.out.println("--- "+j.compareTo(j2)+" : "+j2.compareTo(j3)+" : "+j3.compareTo(j) );} catch(RuntimeException e){}
//        }
//        System.exit(1);

        int numberPublicationsOK=0;
        MessageError em = new MessageError(10);
        for (ebrpPublication p: pubList){
            try{
                JournalWithASJC j = journalSet.floor(p.journal);
                if (j!=null && j.did.equalsIgnoreCase(p.journal.did)) {
                    p.setJournal(j);
                    numberPublicationsOK++;
                } else {
                    em.printlnErr("paper "+p.getInternalID()+" is in journal "+p.journal+"\n   nearest but unequal Scopus journal is "+j);
                }
            } catch (RuntimeException e){}
        }// eo for p
        System.out.println("--- "+pubList.size()+" publications found, "+numberPublicationsOK+" had journal recognised");
        em.printCountErr();
    }
    
    
    
    
    
    /**
     * Output list of publications by ASJC class.
     * @param pubList set of publications
     * @param infoOn true if want information
     */
        static public void FileOutputMidLevelPublicationSets(TreeSet<ebrpPublication> pubList,
                int physicsASJC, boolean fullForm, boolean infoOn){
        // print out separate lists of publications for each given mid level
        String outputDirectory ="output/";
        String sep="\t";
        String typeString="";
        if (fullForm){
            typeString="full";
            }else{
            typeString="short";
            }
        MessageWarning mw;
        ArrayList<Integer> ASJClist = ASJCclasses.midLevelList(physicsASJC);
        for (Integer asjc: ASJClist){
            int numberPublicationsOK=0;
            mw = new MessageWarning(10);
            PrintStream PS;
            FileOutputStream fout;
            String pubByASJCOutputFullFileName=outputDirectory+"ebrpPub"+typeString+asjc+".dat";
            if (infoOn) System.out.println("Writing file ebrp publication data "+ pubByASJCOutputFullFileName);
            try {
                fout = new FileOutputStream(pubByASJCOutputFullFileName);
                PS = new PrintStream(fout);
                PS.println((fullForm?ebrpPublication.toStringLabel(sep):ebrpPublication.toStringShortLabel(sep)));
                for (ebrpPublication p: pubList)
                    if (p.journal.hasASJC(asjc) ) {
                         if (fullForm){
                             PS.println(p.toString(sep));
                         }else{
                             PS.println(p.toStringShort(sep));
                         }
                        numberPublicationsOK++;
                    }
                    //else {mw.printlnErr("NO ASJC for paper "+p);}
            } catch (FileNotFoundException e) {
                throw new RuntimeException("*** Error opening output file "+pubByASJCOutputFullFileName+" "+e.getMessage());
            }// eo catch
            System.out.println("--- "+pubList.size()+" publications found, "+numberPublicationsOK+" had ASJC "+asjc);
        }// eo for asjc

    }

}
