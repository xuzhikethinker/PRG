/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routefinder;

//import TimUtilities.StringUtilities.Filters.StringFilter;

import java.util.ArrayList;


/**
 * This finds the best routes.
 * <p>Given a list of routes between some pairs of sites, it will find
 * the best routes between all pairs of sites.  It will also give a list of the
 * optimal routes for each pair.
 * See {@link routefinder.SimpleRouteMatrix#readPathFile(java.lang.String, int, boolean) }
 * for input file format.</p>
 * <p>The command line arguments are as follows:-
 * <ol>
 * <li>Root of all file names e.g. <i>EastMed30</i> </li>
 * <li>Symmetric adjacency matrix - yes if starts with t T y or Y. (default symmetric)</li>
 * <li>Distances not potentials - yes if starts with t T y or Y. (default distances)</li>
 * <li>Distances as matrix  - yes if starts with t T y or Y. (default yes)</li>
 * <li>Distances as list  - yes if starts with t T y or Y. (default yes)</li>
 * <li>Best routes file  - yes if starts with t T y or Y. (default yes)</li>
 * </ol>
 * Defaults are equivalent to
 * <tt>java -Xmx1400m  -classpath timgraph.jar;. RouteFinder.MakeDistanceMatrix test y y y y y y</tt>
 * </p>
 * @author time
 */
public class MakeDistanceMatrix {

    public static void main(String[] args) {
        
      int ano=0;
      String filenameroot="test"; 
      //String filenameroot="testpotl";
      //String filenameroot="EastMed30";
      //String filenameroot="aegean39S1L3a_v1_3e-1.0j0.0m0.5k1.0l4.5b1.2D100.0MC_r4_invew";
      //String filenameroot="aegean39S1L3a_v1_3e-1.0j0.0m0.5k1.0l4.5b1.2D100.0MC_r4_lnew";
      if (args.length>ano ) filenameroot=args[ano];
      String inputDir = "input/";
      String filename = inputDir+filenameroot+"inputroute.dat";
      System.out.println("--- Original route data from file "+filename);
        
      ano++;
      boolean symmetric=true;
      if (args.length>ano ) {char c= args[ano].charAt(0);
          symmetric= ((c=='t' || c=='T' || c=='y' || c=='Y')  ? true : false);
      }
      System.out.println("---  "+(symmetric?"symmetric":"directed")+" adjacency matrix");

      ano++;
      boolean distNotPotl=true;
      if (args.length>ano ) {char c= args[ano].charAt(0);
          distNotPotl= ((c=='t' || c=='T' || c=='y' || c=='Y')  ? true : false);
      }
      System.out.println("---  "+(distNotPotl?"distances":"potentials")+" as input");

      ano++;
      boolean matrixOutput=true;
      if (args.length>ano ) {char c= args[ano].charAt(0);
          matrixOutput= ((c=='t' || c=='T' || c=='y' || c=='Y')  ? true : false);
      }
      System.out.println("---  "+(matrixOutput?"":"no ")+"matrix file as output");

      ano++;
      boolean listOutput=true;
      if (args.length>ano ) {char c= args[ano].charAt(0);
          distNotPotl= ((c=='t' || c=='T' || c=='y' || c=='Y')  ? true : false);
      }
      System.out.println("---  "+(listOutput?"":"no ")+"list file as output");

      ano++;
      boolean bestRouteOutput=true;
      if (args.length>ano ) {char c= args[ano].charAt(0);
          distNotPotl= ((c=='t' || c=='T' || c=='y' || c=='Y')  ? true : false);
      }
      System.out.println("---  "+(bestRouteOutput?"":"no ")+"best route file as output");

      final double [] friction = {1.0, 0.5, 3.0};
      PathMeasure pathMeasure;
      if (distNotPotl) pathMeasure = new SimpleDistanceMeasure(friction);
      else pathMeasure = new SimplePotentialMeasure(friction);
      SimpleRouteMatrix srm = new SimpleRouteMatrix(pathMeasure, symmetric);
      int sampleFrequency=1;
      boolean infoOn=false;
      boolean sumNotProductLengths=true;
      System.out.println("---  Reading in basic routes");
      srm.makeMatrix(filename, sampleFrequency, infoOn, sumNotProductLengths);
      if (infoOn) srm.printAllPretty(System.out, " ,");
      
      // now do the optimisation
      System.out.println("---  Starting optimisation");
      int dimension=srm.dim;
      BestRouteMatrix brm = new BestRouteMatrix();
      brm.calcLength(srm, infoOn);
      System.out.println("---  Finished optimisation");
      
      String outputDir = "output/";
      String fullfilenamerootoutput = outputDir+filenameroot;
      String sep="\t";
      String cc="#";
      boolean headingsOn=true;
      infoOn=true;
      if (listOutput) brm.outputBestRouteListFile(fullfilenamerootoutput, sep, cc,headingsOn,infoOn);
      if (matrixOutput) brm.outputBestRouteMatrixFile(fullfilenamerootoutput, sep, cc,headingsOn,infoOn);
      brm.outputBestRouteFile(fullfilenamerootoutput, sep, cc,headingsOn,infoOn);
    }



}