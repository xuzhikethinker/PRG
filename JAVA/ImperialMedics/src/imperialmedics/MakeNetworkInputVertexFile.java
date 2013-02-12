/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imperialmedics;

import JavaNotes.TextReader;
import ebrp.FileReadUtilities;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Makes vertex file for networks from data
 * @author time
 */
public class MakeNetworkInputVertexFile {

    static String sep=ProcessAllFiles.SEP;

    public static void main(String[] args) {
        makeAllAuthorVerticesFile();
    }

    /**
     * Takes primary authors from simple text file
     */
    static public void makeAllAuthorVerticesFile(){
        String inputDirectory="input/";
        String outputDirectory="output/";
        // next process the existing statistics on all authors
        String simpleImperialAuthorListFileName = inputDirectory+"ICMedicsAuthorList.dat";
        int infoLevel=0;
        TreeSet<Author> authorSet = ProcessAllAuthorFile.readSimpleImperialAuthorList(simpleImperialAuthorListFileName, infoLevel);

        // write information to file
        PrintStream PS;
        FileOutputStream fout;
        //boolean infoOn=true;
        String vertexFullFileName=outputDirectory+"ICMedicsAuthorinputVertices.dat";
        boolean infoOn=false;
        if (infoOn) System.out.println("Writing file of all author data "+ vertexFullFileName);
            try {
            fout = new FileOutputStream(vertexFullFileName);
            PS = new PrintStream(fout);
            PS.println("File"+sep+ProcessSinglePublicationCSVList.summaryStringLabel);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Error opening output file "+vertexFullFileName+" "+e.getMessage());
        }
        //timgraph.vertexIndexLabel
        // timgraph.VertexLabel.nameLabel="Name";
        // timgraph.VertexLabel.numberLabel="Number";
        PS.println("Index"+sep+"Name");
        int i=0;
        for (Author a: authorSet){
            i++;
            PS.println(a.getID()+sep+a.toStringNoTitles());
        }

        try{ fout.close ();
           } catch (IOException e) { throw new RuntimeException("*** File Error with " +vertexFullFileName+" "+e.getMessage());}
        if (infoOn) System.out.println("Finished writing file of author vertex data "+ vertexFullFileName);
    }


    /**
     * This works only if all authors are in the Stata10 file
     */
    public void makeAllAuthorVerticesFileStats10(){
        // next process the existing statistics on all authors
        ProcessAllAuthorFile paaf = new ProcessAllAuthorFile();
        //paaf.rootFileName = "Stata10networkspreadsheetfinalNoPW";
        paaf.rootFileName = "Stata10networkspreadsheetfinalTSE";
        //paaf.rootFileName = Stata10test";
        paaf.inputDirectory ="input\\";
        paaf.outputDirectory ="output\\";
        boolean infoOn=false;
        paaf.processXLSFile(infoOn);

        // write information to file
        PrintStream PS;
        FileOutputStream fout;
        //boolean infoOn=true;
        String vertexFullFileName=paaf.outputDirectory+"ICMedicsAllAuthorinputVertices.dat";
        if (infoOn) System.out.println("Writing file of all author data "+ vertexFullFileName);
            try {
            fout = new FileOutputStream(vertexFullFileName);
            PS = new PrintStream(fout);
            PS.println("File"+sep+ProcessSinglePublicationCSVList.summaryStringLabel);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("*** Error opening output file "+vertexFullFileName+" "+e.getMessage());
        }
        TreeSet<Author> authorSet = paaf.authorSet;
        //timgraph.vertexIndexLabel
        // timgraph.VertexLabel.nameLabel="Name";
        // timgraph.VertexLabel.numberLabel="Number";
        PS.println("Index"+sep+"Name");
        int i=0;
        for (Author a: authorSet){
            PS.println((i++)+sep+a.toStringNoTitles());
        }

        try{ fout.close ();
           } catch (IOException e) { throw new RuntimeException("*** File Error with " +vertexFullFileName+" "+e.getMessage());}
        if (infoOn) System.out.println("Finished writing file of author vertex data "+ vertexFullFileName);
    }



}
