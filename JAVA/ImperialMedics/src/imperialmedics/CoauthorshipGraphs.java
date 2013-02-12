/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imperialmedics;

import TimGraph.timgraph;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author time
 */
public class CoauthorshipGraphs {

    /**
     * tgarray array of timgraph, one for each period
     */
    timgraph [] tgarray;
    /**
     * Maps authors to vertex index.
     */
    Map<Author,Integer> authorToIndex;
    /**
     * Switch for dealing with missing authors.
     */
    boolean addMissingAuthors=true;
    /**
     * Type of weight to use for edges.
     * @see imperialmedics.CoauthorshipGraphs#printTypeDescription(java.io.PrintStream, int) 
     */
    int weightType=0;
    AuthorFraction authorFraction;
    int primaryAuthorPosition;
    boolean alphabeticalOrder;
    boolean infoOn;
    /**
     * Number label of initial vertices.
     */
    int initialVertexNumber=0;
    /**
     * Number label of added vertices.
     */
    int addedVertexNumber=1;


    /**
     * File name abbreviations for weight types
     */
    final String [] weightTypeStringShort={
            "1",       "NSL",
            "Ns",     "NsNSL",
            "NsNt", "NsNtNSL",
            "NsNt", "NsNtNSL",
            "St",     "StNSL",
            "NsSt", "NsStNSL",
            "SsSt", "SsStNSL"};
    /**
     * Column abbreviations for weight types
     */
    final String [] weightTypeStringLabel={
            "1",       "1/NSL",
            "N_s",     "N_s/NSL",
            "N_s N_t", "N_s N_t/NSL",
            "N_s N_t", "N_s N_t/NSL",
            "S_t",     "S_t/NSL",
            "N_s S_t", "N_s S_t/NSL",
            "S_s S_t", "S_s S_t/NSL"};


    public CoauthorshipGraphs(int maxVertices, int maxStubs, int numberPeriods){
        String namert="ICMedicsCA";
        String dnameroot=System.getProperty("user.dir"); // current directory
        int infoLevel=0;
        int outputc=255;
        boolean makeDirected=true;
        boolean makeLabelled=true;
        boolean makeWeighted=true;
        boolean makeVertexEdgeList=true;
        tgarray= new timgraph[numberPeriods];
        for (int p=0; p<numberPeriods; p++){
           tgarray[p]= new timgraph(namert, dnameroot, 
              infoLevel, outputc,
              makeDirected, makeLabelled, makeWeighted, makeVertexEdgeList,
              maxVertices, maxStubs);
        }
        authorFraction=new AuthorFraction(20);
    }


    /**
     * Set up initial vertices from given author list.
     * Initialises the authorToIndex map.
     * @param aSet set of authors
     */
    public void setInitialVertices(TreeSet<Author> aSet){
        ArrayList<String> nameList = new ArrayList();
        int [] indexList= new int[aSet.size()];
        authorToIndex = new HashMap();
        int i=0;
        int id=timgraph.IUNSET;
        String name=timgraph.SUNSET;
        for (Author a: aSet){
            id=a.getID();
            indexList[i++]=id;
            nameList.add(a.toString());
            authorToIndex.put(a,id);
        }
        for (int p=0; p<tgarray.length; p++){
                tgarray[p].setVertexNames(indexList, nameList);
                tgarray[p].setVertexNumbers(initialVertexNumber);
            }
    }


        /**
         * Update the coauthorship graphs.
         * Takes list of authors on one publication and adds authors if not yet known.
         * Edges then added between all coauthors according to weight scheme set.
         * @param authorList list of coauthors on one paper
         * @param infoOn true (false) if want (do not want) info on screen
         */
    protected void setSomething(ArrayList<Author> authorList, int period, boolean infoOn){
         //int paperType = PeriodData.getPaperType(numberAuthors, alphabeticalOrder);
         //numberPapersByType[paperType]++;
         //numberAuthorsList.add(numberAuthors);
         //authorPositionList.add(primaryAuthorPosition);
         //this.positionCountsByType[paperType].addOnePaper(primaryAuthorPosition, numberAuthors);
        // find index of authors in graph
        int numberAuthors=authorList.size();
        // need at least two authors for coauthorship
        if (numberAuthors<2) return;

        // check list of authors
        // add any new ones with vertex label number = addedVertexNumber
        Author a;
        Integer v=null; //timgraph.IUNSET;
        int [] vlist= new int[numberAuthors];
        for (int s=0; s<numberAuthors; s++){
             a= authorList.get(s);
             v = authorToIndex.get(a);
             if (v==null && addMissingAuthors){ // add missing author
                 v=tgarray[0].getNumberVertices();
                 for (int g=0; g<tgarray.length; g++) tgarray[g].addVertex(a.toStringNoTitles(), addedVertexNumber);
                 authorToIndex.put(a, v);
             }
             if (v!=null) vlist[s]=v;
         }
    }
        /**
         * Update the coauthorship graphs.
         * Takes list indices of coauthors from one publication in order appearing
         * and adds appropriate weight to edges.
         * @param vertexIndex array of graph vertex indices for coauthors on one paper
         * @param period index of graph to be updated
         * @param infoOn true (false) if want (do not want) info on screen
         */
    protected void setCoauthorshipEdges(int [] vertexIndex, int period, boolean infoOn){
        Author a;
        int numberAuthors=vertexIndex.length;
        double w10SelfLoopCorrection=setSelfLoopCorrection(numberAuthors);
        int source=ProcessSinglePublicationCSVList.IUNSET;
        int target=ProcessSinglePublicationCSVList.IUNSET;
        for (int s=0; s<numberAuthors; s++){ // s is rank of sourceauthor in list
         setCoauthorshipEdgesFromSource(vertexIndex, period,
            s, w10SelfLoopCorrection, infoOn);
        } //eo for s

    }

    private double setSelfLoopCorrection(int numberAuthors){
        double w10SelfLoopCorrection=0;
        for (int s=0; s<numberAuthors; s++) w10SelfLoopCorrection+=authorFraction.getScore(s, numberAuthors)*authorFraction.getScore(s, numberAuthors);
        return w10SelfLoopCorrection;
    }
        /**
         * Update the coauthorship graphs from given source vertex.
         * Takes list indices of coauthors from one publication in order appearing
         * and adds appropriate weight to edges.
         * @param vertexIndex array of graph vertex indices for coauthors on one paper
         * @param period index of graph to be updated
         * @param infoOn true (false) if want (do not want) info on screen
         */
    protected double setCoauthorshipEdgesFromSource(int [] vertexIndex, int period,
            int s, double w10SelfLoopCorrection,
            boolean infoOn){
        Author a;
        int numberAuthors=vertexIndex.length;
        if (w10SelfLoopCorrection<0) w10SelfLoopCorrection=setSelfLoopCorrection(numberAuthors);
        int target=ProcessSinglePublicationCSVList.IUNSET;
        // s is rank of sourceauthor in list
        int source = vertexIndex[s];
        if (source<0) return w10SelfLoopCorrection;
         for (int t=0; t<numberAuthors; t++){ // t is rank of target author in list
            target = vertexIndex[t];
            if (target<0) continue;
            double w=timgraph.DUNSET;
            switch (weightType) {
                case 11: // S_s S_t, 1 per paper, variable position score, self loops
                    w = authorFraction.getScore(s, numberAuthors)*authorFraction.getScore(t, numberAuthors);
                    break;
                case 10: // S_s S_t, 1 per paper, variable position score, no self loops
                    if (s==t) w=0;
                    else w = authorFraction.getScore(s, numberAuthors) * authorFraction.getScore(t, numberAuthors) / (1.0- w10SelfLoopCorrection);
                    break;
                case 9: // N_s S_t, 1 per paper, variable position score, self-loops
                    w = authorFraction.getScore(s, numberAuthors)/(numberAuthors);
                    break;
                case 8: // N_s S_t, 1 per paper, variable position score, no self-loops
                    if (s==t) w=0;
                    else w = authorFraction.getScore(s, numberAuthors)/(numberAuthors-1);
                    break;
                case 7: // S_t, 1 per author, variable position score, self-loops
                    w = authorFraction.getScore(t, numberAuthors);
                    break;
                case 6: // S_t, 1 per author, variable position score, no self-loops
                    if (s==t) w=0;
                    else w = authorFraction.getScore(t, numberAuthors)/(1-authorFraction.getScore(s, numberAuthors));
                    break;
                case 5: // N_S N_t, 1 per paper, self loops
                    if (s==t) w=0;
                    else w = 1.0/(numberAuthors*numberAuthors);
                    break;
                case 4: // N_S N_t, 1 per paper, no self loops
                    if (s==t) w=0;
                    else w = 1.0/(numberAuthors*(numberAuthors-1));
                    break;
                case 3: // N_s, 1 per author, self loops
                    w = 1.0/numberAuthors;
                    break;
                case 2: // N_s, 1 per author, no self loops
                    if (s==t) w=0;
                    else w = 1.0/(numberAuthors-1);
                    break;
                case 1: // 1, 1 per coauthorship, self loops
                     w = 1;
                     break;
                case 0: // 1, 1 per coauthorship, no self loops
                default: // simple number of papers
                    if (s==t) w=0;
                    else w = 1;
                }
                //  eo switch
            // no edge added if w=0;
            tgarray[period].addEdgeWithTests(vertexIndex[s], vertexIndex[t], w);
            } // eo for t
    return w10SelfLoopCorrection;
    }


    /**
     * Output description of type of weight used.
     * @param PS PrintStream such as System.out
     * @param wt index of weight type used.
     */
    public void printTypeDescription(PrintStream PS, int wt){
          switch (wt) {
                case 11: PS.println("S_s S_t, 1 per paper, variable position score, self loopsPS.println");
                break;
                case 10: PS.println("S_s S_t, 1 per paper, variable position score, no self loops");
                break;
                case 9: PS.println("N_s S_t, 1 per paper, variable position score, self-loops");
                break;
                case 8: PS.println("N_s S_t, 1 per paper, variable position score, no self-loops");
                break;
                case 7: PS.println("S_t, 1 per author, variable position score, self-loops");
                break;
                case 6: PS.println("S_t, 1 per author, variable position score, no self-loops");
                break;
                case 5: PS.println("N_S N_t, 1 per paper, self loops");
                break;
                case 4: PS.println("N_S N_t, 1 per paper, no self loops");
                break;
                case 3: PS.println("N_s, 1 per author, self loops");
                break;
                case 2: PS.println("N_s, 1 per author, no self loops");
                break;
                case 1: PS.println(" 1, 1 per coauthorship, self loops");
                break;
                case 0: PS.println("1, 1 per coauthorship, no self loops");
                break;
                default: PS.println("Unknown type of weight "+wt
                        +", want value between 0 and "+weightTypeStringLabel.length);
                }
                //  eo switch
    }

}
