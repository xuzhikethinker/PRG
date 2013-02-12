/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import TimUtilities.GeneralMode;
 
/**
 *
 * @author time
 */
public class InputFileType extends GeneralMode {
    /**
     * List of possible extensions for input files, used to select input file type
     */
    public final static String [] extensionList =
    {"input.net",      "inputEL.dat",      "inputELS.dat",
     "input.gml",      "inputAdjMat.dat",  "inputVNLS.dat",
     "inputBVNLS.dat", "inputLAM.dat",     "input.gml",
     "inputXY.dat", "inputNames.dat", "inputNumber.dat"};
    public final static String [] extensionDescription =
    {"pajek",    "edge list of integers", "edge list of strings", 
     "gml file", "adjacency matrix", "vertex neighbour list of strings",
     "bipartite vertex neighbour list of strings", "labelled adjacency matrix", "Newman style GML", 
     "XY vertex coordinates list", "vertex number list", "vertex integer label list"};
    
    public InputFileType (int n){setUp(extensionList,extensionDescription,n);};

    /**
     * Gives list of short string descriptions and their numbers of all modes.
     * @param sep1 separation string between description and its number
     * @param sep2 separation string between different modes
     * @return list of short descriptions separated by sep string
     */
    public static String listAllExtensions(String sep1, String sep2)
    {
        String s=extensionList[0] + sep1+ "0";
        for (int m=1; m<extensionList.length;m++) s=s+sep2+extensionList[m]+sep1+m;
        return(s);
    }



}
