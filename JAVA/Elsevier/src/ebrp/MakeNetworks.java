/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ebrp;

import TimGraph.algorithms.GCC;
import TimGraph.algorithms.Projections;
import TimGraph.run.BasicAnalysis;
import TimGraph.timgraph;
import TimUtilities.StringAsBoolean;
import TimUtilities.StringUtilities.Filters.StopWords;
import TimUtilities.StringUtilities.Filters.StringFilter;
import TimUtilities.StringUtilities.Stemmers.Porter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Make networks from EBRP publication data
 * @author time
 */
public class MakeNetworks {


    /**
     * Short abbreviation for type of weight used
     */
    static final String [] weightTypeShort = {"T1","P1"};
    /**
     * Long description for type of weight used
     */
    static final String [] weightTypeDescription = {
            "weight one for every user term listed",
            "total weight one for each paper, terms have equal weight"
            };


    /**
     * Creates networks from EBRP data.
     * <p>MakeNetworks Arguments :<em>rootFileName</em> :<em>year</em> :<em>fieldname</em> :<em>weightType</em> :<em>useWhat</em> :<em>infoLevel</em>
     * e.g. defaults are equivalent to 
     * <tt>MakeNetworks :ebrp_03_set_01_documentsTEST :/PRG/JAVA/Elsevier/input 
     * :/DATA/Elsevier/output :2002 :Phy :1 :1 :0</tt>.
     * Publications must be in current directory in subdirectory
     * <tt>input\</tt><em>rootFileName</em><tt>.dat</tt>.
     * Output goes to <tt>output\</tt> subdirectory of current directory.
     * <ul>
     * <li>rootFileName (=ebrp_03_set_01_documentsTEST). If equals &quot;all&quot; then does all EBRP</li>
     * <li>year (=2002) produce network for this year</li>
     * <li>fieldnames are 
     * <ol>
     *   <li><tt>Bus</tt> for Business and International Management</li>
     *   <li><tt>Phy</tt> for Physics & astronomy</li>
     * </ol>
     * <li>weightType (=1) 
     *  <ul>
     *    <li>0 = weight one for every user term, T1 in file name</li>
          <li>1 = total weight one for each paper, terms have equal weight, P1 in file name</li>
     *  </ul>
     * </li>
     * <li>useWhat (=1) binary bits so 1,3= use title, 2,3=use keywords</li>
     * <li>infoLevel 0 for normal, 1 for some, 2 for all debugging info, -1 for minimal</li>
     * </ul>
     * Files are named as follows
     * <tt>rootFileName_year_field_PTtermsource_weighttype</tt>
     * where the PT indicates that network is a Paper - term bipartite network.
     * <em>termsource</em> is either or both of <tt>t</tt> indicates words from title used,
     * <tt>k</tt> if author keywords used.
     * The files ending <tt>EPF<emph>minChar</em>-<em>minL</em></tt> indicate
     * that <tt>ebrp.ElsevierPapersFilter.java</tt> was used to do the stemming
     * and filtering. The name indicates that <emph>minChar</em> is the minimum
     * number of characters for acceptance of any term (eliminate odd stray
     * characters).  The <em>minL</em> is the minimum number of letters for
     * acceptance so that strings of mostly numbers or non-alphabetic characters
     * are rejected. The files ending in <em>StemMap.dat</em> have on each line
     * the stem used in all network files followed by all the words mapped
     * to that single stem. The <em>RejectList.dat</em> file gives all the
     * rejected phrases.
     * @param args 
     */    
    public static void main(String[] args) {

      System.out.println("\n... MakeNetworks Arguments :<rootFileName> "
              +":<inputDirectory>+ :<outputDirectory> +:<extractGCC>"
              + ":<year> :<weightType> :<useWhat> :<infoLevel>");
      System.out.println("Fieldname types are:- Bus for Business and International Management");
      System.out.println("                    - Phy for Physics & astronomy");
      for (int i=0; i<weightTypeDescription.length; i++) {
            System.out.println("... weightType "+i+" = "+weightTypeDescription[i]);
        }       
      System.out.println("... useWhat: 1,3= use title, 2,3=use keywords");
      for(int a=0; a<args.length; a++) {
            System.out.print((a==0?"... Arguments:   ":" ")+args[a]);
        }
      System.out.println();
      
      HashSet<Integer> yearSet = new HashSet();
      HashSet<String> fieldnameSet = new HashSet();
      HashSet<Integer> weightTypeSet = new HashSet();
      HashSet<Integer> useWhatSet = new HashSet();
      
      
      int ano=0;
      //String rootFileName = "ebrp_03_set_01_documentsTEST";
      //String rootFileName = "ebrp_03_set_01_documentsHARDTEST";
      String rootFileName = "ebrp_03_set_01_documents";
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) {
              rootFileName=args[ano].substring(1);
        }
      System.out.println("--- Root of file name is "+rootFileName);
      
      final String fileSep=System.getProperty("file.separator");
      String dirBase=System.getProperty("user.dir")+fileSep;
      
      //String inputDirectory ="C:\\PRG\\JAVA\\Elsevier\\input\\";
      String inputDirectory ="C:\\DATA\\Elsevier\\input\\";
      ano++;
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) inputDirectory=args[ano].substring(1);  
      System.out.println("--- Input directory is "+inputDirectory);
      
      String outputDirectory ="C:\\DATA\\Elsevier\\output\\";
      ano++;
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) outputDirectory=args[ano].substring(1);  
      System.out.println("--- Output directory is "+outputDirectory);
      
      
      boolean extractGCC=true;
      ano++;
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) extractGCC=(StringAsBoolean.isTrue(args[ano].charAt(1))?true:false);  
      System.out.println("--- extracting GCC - "+(extractGCC?"yes":"no"));
        
      int minDegreeIn=3;
      int minDegreeOut=3;
      double minWeight=0;
      System.out.println("--- extracting simplified GCC with min in degree - "
              +minDegreeIn+", min out degree - "+minDegreeOut+", and min weight "+minWeight);
            
      // set up filter
      int minChar=2;
        int minL=3;
        boolean keepRejectList=true;
        ElsevierPapersFilter ipf = new ElsevierPapersFilter(minChar, minL, keepRejectList);
      
      
      if (rootFileName.equalsIgnoreCase("ALL")){
          //rootFileName = "ebrp_03_set_01_documentsHARDTEST"; 
          rootFileName = "ebrp_03_set_01_documents";
          yearSet.add(2002);
          yearSet.add(2006);
          yearSet.add(2010);
          fieldnameSet.add("Bus"); //Business and International Management
          fieldnameSet.add("Phy"); //Physics & astronomy
          weightTypeSet.add(0);
          weightTypeSet.add(1);
          useWhatSet.add(1);
          useWhatSet.add(2);
          useWhatSet.add(3);
          int infoLevelAll=-1;
          process(rootFileName, inputDirectory, outputDirectory,
            yearSet, fieldnameSet,
            weightTypeSet,
            useWhatSet,
            ipf,
            extractGCC,
            minDegreeIn, minDegreeOut, minWeight,
            infoLevelAll);
          System.exit(0);
      }
      System.out.println("--- file name root "+rootFileName);

      int year = 2002;
      ano++;
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) year=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- Year used "+year);
      yearSet.add(year);

      // Bus= Business and International Management
      // Phy= Physics & astronomy
      String fieldname = "Phy"; 
      ano++;
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) fieldname=args[ano].substring(1, args[ano].length());      System.out.println("--- fieldname used "+fieldname);
      fieldnameSet.add(fieldname);

      // 1 = total weight one for each paper, terms have equal weight, P1 in file name
      int weightType=1;
      ano++;
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) weightType=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      if ((weightType<0) || (weightType>=weightTypeShort.length)) throw new RuntimeException("illegal weightType of "+weightType+", must be between 0 and "+(weightTypeShort.length-1)+" inclusive");
      System.out.println("--- Weight Type  "+weightType+" = "+weightTypeDescription[weightType]);
      weightTypeSet.add(weightType);
      

      // 1,3= use title, 2,3=use keywords
      int useWhat=1;
      ano++;
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) useWhat=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      boolean useTitle=((useWhat&1)>0);
      boolean useKeywords=((useWhat&2)>0);
      System.out.println("--- "+(useTitle?"U":"Not u")+"sing titles");
      System.out.println("--- "+(useKeywords?"U":"Not u")+"sing keywords");
      useWhatSet.add(useWhat);

      // 0 for normal, 1 for some, 2 for all debugging info, -1 for minimal
      int infoLevel=0;
      ano++;
      if ( (args.length>ano ) && (timgraph.isOtherArgument(args[ano]) )) infoLevel=Integer.parseInt(args[ano].substring(1, args[ano].length()));  
      System.out.println("--- infoLevel="+infoLevel);
      boolean infoOn=(infoLevel>1);

//      if (args.length>ano ) if (timgraph.isOtherArgument(args[ano])) graphMLOutput=StringFilter.trueString(args[ano].charAt(1));


      process(rootFileName, inputDirectory, outputDirectory,
            yearSet, fieldnameSet,
            weightTypeSet,
            useWhatSet,
            ipf,
            extractGCC,
            minDegreeIn, minDegreeOut, minWeight,
            infoLevel);
    }
    /**
     * Process file.
     * Publications must be in current directory in 
     * <tt>input\</tt><em>rootFileName</em><tt>.dat</tt>.
     * Output goes to <tt>output\</tt> subdirectory.
     * @param rootFileName base name used for file
     * @param inputDirectory full name of directory with input files
     * @param outputDirectory full name of directory with output files
     * @param yearSet set of years to use
     * @param weightTypeSet set of weights types to use (0=K1, 1=T1)
     * @param useWhatSet use titles (1,3) or keywords (2,3)
     * @param ipf ElsevierPapersFilter with correct field dependent stop stems
     * @param extractGCC true if want GCC to be extracted as well - files have GCC added to name
     * @param minDegreeIn  minimum in degree required for edge to be retained
     * @param minDegreeOut minimum out degree required for edge to be retained
     * @param minWeight minimum weight in input graph needed for an edge to be copied to output graph..
    * @param infoLevel -1 for no info0 for normal, 1 or 2 for more debugging info
     */
    public static void process(String rootFileName, 
            String inputDirectory, String outputDirectory, 
            Set<Integer> yearSet, Set<String> fieldnameSet,
            Set<Integer>  weightTypeSet,
            Set<Integer> useWhatSet, 
            ElsevierPapersFilter ipf,
            boolean extractGCC,
            int minDegreeIn, int minDegreeOut, double minWeight,
            int infoLevel){
        System.out.println("--- Processing papers from "+rootFileName);
        final String ext=".dat";
        
        String fullFileName=inputDirectory+rootFileName+ext;
        TreeSet<ebrpPublication> fullPubSet;
        ProcessPublicationList ppl = new ProcessPublicationList();
        fullPubSet = ppl.readEBRPPublicationData(fullFileName, infoLevel);
        System.out.println("--- Have "+fullPubSet.size()+" papers from "+fullFileName);
        
        // optional paper classification
        //ClassifyPublications.classify(fullPubSet, infoLevel);
        
        int sampleFrequency=1; // <=1 means take all
        if (sampleFrequency<1) sampleFrequency=1;
        System.out.println("--- Taking every "+sampleFrequency+"th paper");
        //if (sampleFrequency<=1) pubSet=fullPubSet;
        for (Integer year: yearSet){
            int pubNumber=0;
            TreeSet<ebrpPublication> pubSet = new TreeSet();
            for (String fieldname: fieldnameSet){
                if (fieldname.startsWith("P")){                   
                    ipf.makeStopStemSet(ElsevierStopStems.PHYSICS);
                }
                if (fieldname.startsWith("B")){                   
                    ipf.makeStopStemSet(ElsevierStopStems.BUSINESS);
                }
                for (ebrpPublication p: fullPubSet){
                    if ((p.getYear()!=year) || (!p.fieldName.startsWith(fieldname))) continue;
                    if (((pubNumber++)%sampleFrequency==0)  ) pubSet.add(p);
                }
                System.out.println("--- Have "+pubSet.size()+" papers from year "+year+" and fieldname "+fieldname);
                if ((infoLevel>-2) && (pubSet.size()<20)){
                    int pn=0;
                    for (ebrpPublication p: pubSet){
                        System.out.println((pn++)+p.eid+", "+p.getTitle());
                    }
                }
                for (Integer useWhat:useWhatSet){
                  boolean useTitle=((useWhat&1)>0);
                  boolean useKeywords=((useWhat&2)>0);
                  System.out.println("--- "+(useTitle?"U":"Not u")+"sing titles");
                  System.out.println("--- "+(useKeywords?"U":"Not u")+"sing keywords");
                  for (Integer weightType:weightTypeSet){
                    processOneYear(pubSet, rootFileName, outputDirectory, inputDirectory,
                        year, fieldname, weightType, ipf,
                        useTitle, useKeywords, extractGCC,
                        minDegreeIn, minDegreeOut, minWeight,
                        infoLevel);
                  }
                }
            }// eo for fieldname
        }// eo for year

    }
    /**
     * Process the data for a single year.
     * If no gcc graph is produced or if 
     * all of minDegreeIn, minDegreeOut and minWeight are zero or negative then
     * no simple gcc graph is produced.  The simplification involves application 
     * of all three conditions.  In and out degree limitations are enforced separately
     * even for undirected graphs so best set them equal in that case.
     * @param pubSet
     * @param rootFileName
     * @param outputDirectory
     * @param inputDirectory
     * @param year
     * @param fieldname
     * @param weightType
     * @param ipf ElsevierPapersFilter with correct field dependent stop stems
     * @param useTitle
     * @param useKeywords
     * @param extractGCC true if want GCC to be extracted as well - files have GCC added to name
     * @param minDegreeIn  minimum in degree required for edge to be retained
     * @param minDegreeOut minimum out degree required for edge to be retained
     * @param minWeight minimum weight in input graph needed for an edge to be copied to output graph..
     * @param infoLevel 
     */
    public static void processOneYear(TreeSet<ebrpPublication> pubSet,
            String rootFileName, 
            String outputDirectory, String inputDirectory,
            int year, String fieldname,
            int weightType,
            ElsevierPapersFilter ipf,
            boolean useTitle, boolean useKeywords,
            boolean extractGCC,
            int minDegreeIn, int minDegreeOut, double minWeight,  
            int infoLevel){
        // now process titles into keywords
        //boolean showProcess=false;
        System.out.println("\n--- now processing "
                +(useTitle?"titles":"")
                +(useTitle&&useKeywords?" and ":"")
                +(useKeywords?"keywords":"")+" into keywords");
        System.out.println("--- year "+year);
        System.out.println("--- fieldname "+fieldname);
        System.out.println("--- weight type "+weightTypeDescription[weightType]);
        boolean showProcess=(infoLevel>1?true:false);
        TreeMap<String,String> stemMap = new TreeMap();
//        int minChar=2;
//        int minL=3;
//        boolean keepRejectList=true;
//        ElsevierPapersFilter ipf = new ElsevierPapersFilter(minChar, minL, 
//                StopWords.MySQL_STOP_WORDS_EDITED, ElsevierStopStems.PHYSICS, keepRejectList);
        setUserKeywords(pubSet, stemMap, ipf, useTitle, useKeywords, showProcess);
        
        //String fileRootName="ebrp";
        String sep="\t";
        String outputFileName = outputDirectory+rootFileName+"_"+year+"_"+fieldname+"_"+(useTitle?"t":"")+(useKeywords?"k":"")+"_"+ipf.abbreviation()+"ptStemMap.dat";
        TimUtilities.FileUtilities.FileOutput.FileOutputMap(outputFileName, sep, stemMap, true);
        outputFileName = outputDirectory+rootFileName+"_"+year+"_"+fieldname+"_"+(useTitle?"t":"")+(useKeywords?"k":"")+"_"+ipf.abbreviation()+"ptRejectList.dat";
        ipf.FileOutputRejectedList(outputFileName, showProcess);

        // now build network
        System.out.println("--- now building network ");
        
        stemMap.clear(); // no longer needed so free up memory
        timgraph tg=makePublicationKeywordGraph(pubSet, weightType);

        String subDir="";
        String networkType="PT"+(useTitle?"t":"")+(useKeywords?"k":""); // P=Publication T=Term from title
        String fileRootName=rootFileName+"_"+year+"_"+fieldname+"_"+networkType+"_"+weightTypeShort[weightType];
        tg.inputName.setFileName(inputDirectory, subDir, fileRootName, "");
        tg.outputName.setFileName(outputDirectory, subDir, fileRootName, "");
//        public boolean degreeDistributionOn; // 1
//        public boolean distancesOn; // 2
//        public boolean clusteringOn; // 4
//        public boolean triangleSquareOn; // 8
//        public boolean componentsOn; // 16
//        public boolean rankingOn; // 32
//        public boolean structuralHolesOn; // 64
//        public boolean graphMLFileOn; // 128
//        public boolean pajekFileOn; // 256
//        public boolean adjacencyFileOn; // 512
        //1+2+16
        tg.outputControl.set("19");
        BasicAnalysis.analyse(tg);
        
//        // create list of degree zero or one vertices
//        TreeMap<Integer,Integer> oldToNewVertexMap = new TreeMap();
//        int nextVertex=0;
//        for (int v=0; v<tg.getNumberVertices(); v++){
//            oldToNewVertexMap.put(v,  (tg.getVertexDegree(v)<2)?-1:nextVertex++) ; // 0 partition to retain
//        }
//        System.out.println("--- keeping "+" vertices, eliminating "+(tg.getNumberVertices()-nextVertex));
//        // use Projections routine which makes copy of tg with given list of vertices
//        timgraph rtg = Projections.eliminateVertexSet(tg, addToRoot, partition, numberPartitions, forceUndirected, makeUnweighted); 
    
        
        // now find and produce GCC
        if (!extractGCC) {return;}
        timgraph gcc = GCC.extractGCC(tg);
        gcc.outputName.setDirectory(tg.outputName.getDirectoryFull());
        BasicAnalysis.analyse(gcc);
        
        // now simplify GCC
        if (minDegreeIn<=0 && minDegreeOut<=0 &&  minWeight<=0) {return;}
        boolean makeLabelled=gcc.isVertexLabelled();
        boolean makeWeighted=gcc.isWeighted();
        boolean makeVertexEdgeList=gcc.isVertexEdgeListOn();
        timgraph gccsimple = TimGraph.algorithms.Projections.minimumDegreeOrWeight(gcc, 
                minDegreeIn, minDegreeOut, minWeight, 
                makeLabelled, makeWeighted, makeVertexEdgeList);
        gccsimple.outputName.setDirectory(tg.outputName.getDirectoryFull());
        gccsimple.outputName.appendToNameRoot("MINkin"+minDegreeIn+"kin"+minDegreeOut+"w"+String.format("%06.3f", minWeight));
        BasicAnalysis.analyse(gccsimple);       
    }


    /**
     * Set up vertices and edges of publication-user Keyword bipartite graph.
     * <p>The types of weights are:-
     * <ul>
     * <li>0: weight one for every user keyword listed</li>
     * <li>1: total weight one for each paper, keywords have equal weight</li>
     * </ul>
     * @param fullPubSet
     * @param stemMap
     * @param year year of publication to accept
     * @param weightType type of weight to use.
     * @return
     */
    static public timgraph makePublicationKeywordGraph(Set<ebrpPublication> pubSet,
            int weightType){
        int numberPublications = pubSet.size();
        HashSet keywordCollection = new HashSet(); //stemMap.values();
        int pn =0;
        for(ebrpPublication p: pubSet) if (p.hasUserKeywords()) keywordCollection.addAll(p.getUserKeywordList());
//        for(ebrpPublication p: pubSet) {
//            System.out.println("Pub "+(pn++)+" has "+p.getNumberUserKeywords());
//            ArrayList<String> al= p.getUserKeywordList();
//            keywordCollection.addAll(al);
//        }
        int numberKeywords = keywordCollection.size();
        int nvmax=numberPublications+numberKeywords;
        int stubEstMax=nvmax*20;
        timgraph tg = new timgraph();
        tg.setVertexLabelsOn();
        tg.setWeightedEdges(true);
        tg.setNetwork(nvmax,stubEstMax);

        tg.setBipartite(numberPublications,numberKeywords,"Pub","Term");
        // set up vertices
        TreeMap<ebrpPublication,Integer> pubToVertex = new TreeMap();
        TreeMap<String,Integer> keywordToVertex = new TreeMap();
        for(ebrpPublication p: pubSet){
            pubToVertex.put(p, tg.getNumberVertices());
            tg.addVertex(p.eid, p.id);
        }
        Iterator kwiter = keywordCollection.iterator();
        while(kwiter.hasNext()){
            String k = (String) kwiter.next();
            keywordToVertex.put(k, tg.getNumberVertices());
            tg.addVertex(k);
        }
        // set up edges
        double w=-1;
        double roundFactor=10000.0; // used to round weights
        for(ebrpPublication p: pubSet){
            int pv = pubToVertex.get(p);
            if (!p.hasUserKeywords()) continue;
            ArrayList<String> keywordList = p.getUserKeywordList();
            switch (weightType){
                    case 1: // each paper has weight 1
                        w=Math.round(roundFactor/((double) p.getNumberUserKeywords()))/roundFactor;
                        break;
                    case 0: // one
                    default: w=1;
                }
            for (String k: p.getUserKeywordList() ){
                int kv = keywordToVertex.get(k);
                tg.addEdgeWithTests(pv, kv, w);
            }
        }
        return tg;
    }

    /**
      * Takes list of publications and converts titles or keywords into user keywords for each publication.
      * <p>If useKeywords is true then uses the keywords given with the publication.
      * @param pubSet set of publications
      * @param stemMap if not null, is returned with map from words altered to stemmed words
      * @param sf string filter to apply (does stopping and other jobs)
      * @param sampleFrequency number of publications to skip, 1 or less and all are taken
      * @param useTitle true if want to use the publication title
      * @param useKeywords true if want to use the publication provided keywords
      * @param showProcess show process on screen.
      */
    static public void setUserKeywords(Set<ebrpPublication> pubSet,
            Map<String,String> stemMap,
            ElsevierPapersFilter sf,
            boolean useTitle,
            boolean useKeywords,
            boolean showProcess)
    {
        boolean makeMap=true;
        if (stemMap==null) makeMap=false;
        Porter stemmer =new Porter();
        int pubNumber=0;
        String w;
        String showProcessPrefixString=""; // empty means don't show process
        for(ebrpPublication p: pubSet){  // Read until end-of-file.
                pubNumber++;
                //if (pubNumber>1) break;
                if (showProcess) showProcessPrefixString=pubNumber+": ";
                String sep;
                ArrayList<String> stemWordList = new ArrayList(); 
                if (useTitle){
                    String [] titleArray = p.getTitle().split("\\s+");// split at white space of any length
                    stemWordList.addAll(simplifyString(titleArray, stemMap,
                        stemmer, sf, showProcessPrefixString));
                }
                if (useKeywords){
                    String [] aka = p.getAuthorKeywordArray();
                    for (int kw=0; kw<aka.length; kw++){
                        String [] keywordArray = aka[kw].split("\\s+");// split at white space of any length
                        stemWordList.addAll(simplifyString(keywordArray, stemMap,
                            stemmer, sf, showProcessPrefixString));
                    }
                }
                p.setUserKeywordList(stemWordList);
               }
        return;
    }

    /**
     * Takes an array of strings and applies stemming and filtering to each entry.
     * @param stringList array of strings
     * @param stemMap if not null, will add words as keys, stems as values 
     * @param stemmer stemmer to use 
     * @param sf string filter to apply
     * @param showProcessPrefixString if not empty then shows information with this string at start
     * @param makeMap if true keeps the before and after stemming results
     * @return lists of cleaned stems
     */
    static public ArrayList<String> simplifyString(String [] stringList,
            Map<String,String> stemMap, Porter stemmer,
            ElsevierPapersFilter epf,
            String showProcessPrefixString){
//        String sep="\t";
//        String [] stringList = inputString.split("\\s+"); // split at white space of any length
        String sep="\t";
        ArrayList<String> stemList=new ArrayList();
        boolean makeMap = (stemMap==null?false:true);
        String w0;
        String w;
        boolean showProcess=(showProcessPrefixString.isEmpty()?false: true);
        if (showProcess) System.out.print(showProcessPrefixString);
        for (int i=0; i<stringList.length; i++){
                    w0 = stringList[i];
                    StringBuilder sb =ElsevierPapersFilter.clean(w0);
                    w=sb.toString();
                    if (showProcess &&  (w.length()!=w0.length())) System.out.println(w0+" -> "+w);
                    if (showProcess) System.out.print(sep + w0+"->"+w);
                    if(!epf.isAcceptableElseRemember(w)) continue;
                    String stem = stemmer.stem(w);
                    if(epf.isAcceptableStemElseRemember(stem)) {
                       stemList.add(stem);
                       if ((showProcess) && (stem.length() != w.length())) {
                         System.out.print("->" + stem);
                       }
                      if (makeMap && (stem.length() != w.length())) stemMap.put(w, stem);
                   } // eo if acceptable
                   else if (showProcess) System.out.print(w + "<-### "+sep);
        }//eoln
        if (showProcess) System.out.println();
        return stemList;
    }

}
