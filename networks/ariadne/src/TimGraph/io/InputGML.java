/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.io;

import JavaNotes.TextReader;
import java.util.TreeMap;
import java.util.ArrayList;

import TimGraph.timgraph;
import TimGraph.VertexLabel;


/**
 *
 * @author time
 */
public class InputGML {

    /**
     * String used to indicate unset or errors {@value }
     */
    final static String UNSET="UNSET";
    
    /**
     * Integer used to indicate unset or errors {@value }
     */
    final static int IUNSET=-918273645;
    
     /**
     * Integer used to indicate unset or errors {@value }
     */
    final static double DUNSET=-9.182736E45;
    
    /**
     * String which indicates start of new object.
     * <p>{@value }
     */
    final static String STARTSTRING="[";
    
    /**
     * String which indicates end of new object.
     * <p>{@value }
     */
    final static String ENDSTRING="]";
    
    /**
     * Indicate if to read weights
     */
    boolean weightsOn = false;
    /**
     * Indicate if directed graph 
     */
    public boolean directedGraph = false;
    
    public int infoLevel =0;
    
    TextReader data;
    
    public TreeMap<Integer,Integer> nodeIDToIndex;
    public ArrayList<Integer> nodeID;
    public ArrayList<String> nodeLabel;
    public ArrayList<Integer> nodeValue;

    public TreeMap<Integer,Integer> edgeIDToIndex;
    public ArrayList<Integer> edgeSource;
    public ArrayList<Integer> edgeTarget;
    public ArrayList<Double>  edgeWeight;
    
    

    /**
     * Read in GML file
     * @param newData TextReader already opened
     * @param readWeights true if want to read in and use any weights  found.
     */
    public InputGML(TextReader newData, boolean readWeights){
        weightsOn=readWeights;
        data=newData;
    }


    
    
    /**
     * Processes file.
     *<p>Key words must be lower case.
     * Directed keyword is noted with following argument 1 or 0 to set.  
     * Nodes objects found, id and labels recognised.
     * Edge objects recognised, source, target and weight key words recognised.
     */
    public void findGraph(){
        String s=UNSET;
        
        // find object's key word string
        String objectString = "graph";
        
        // find graph then [
        if (findNextWord(objectString).startsWith(UNSET)) throw new RuntimeException("No line stating with "+objectString+" found before end of file");
        if (findNextWord(STARTSTRING).startsWith(UNSET)) throw new RuntimeException("Object "+objectString+" had no starting string "+STARTSTRING+" before end of file");

        if (infoLevel>1) System.out.println("Found "+objectString+" object and start string "+STARTSTRING);
        
     nodeIDToIndex = new TreeMap();
     nodeID = new ArrayList();
     nodeLabel = new ArrayList();
     nodeValue = new ArrayList();

     edgeSource = new ArrayList();
     edgeTarget = new ArrayList();
     if (weightsOn) edgeWeight =new ArrayList();
     
     while (!data.eof()) 
        {
            s=data.getWord();
            if (infoLevel>1) System.out.println("s=\""+s+"\"");
            // is it the end of the graph object?
            if (s.startsWith(ENDSTRING)) break;
            
            if (s.startsWith("directed")) { 
                s=data.getWord();
                if (s.startsWith("1")) directedGraph=true;
                if (s.startsWith("0")) directedGraph=false;
                if (infoLevel>1) System.out.println("Found directed keyword, value="+(directedGraph?"true":"false"));
                continue;
            }

            
            // is this a node object
            if (s.startsWith("node")) {nodeObject();continue;}
            
            // is this a node object
            if (s.startsWith("edge")) {edgeObject();continue;}
        } 
        
      if (!s.startsWith(ENDSTRING)) throw new RuntimeException("File ended before "+objectString+"  closed by "+ENDSTRING);
      if (infoLevel>1) System.out.println("Found end string "+s+" for object "+objectString);
        
    
     
    }
    
    
    private void nodeObject(){
        String s=UNSET;
        String objectString = "node";
        
         // find object's opening string
        if (findNextWord(STARTSTRING).equals(UNSET)) {
                  throw new RuntimeException("No opening "+STARTSTRING+" found for "+objectString);
              }

        int id=IUNSET;
        String label=UNSET;
        int value=IUNSET;
        while (!data.eof())
        {
            s=data.getWord();
            // is it the end of the graph object?
            if (s.startsWith(ENDSTRING)) break;
            
            // is this an id entry
            if (s.startsWith("id")) {
                id = data.getInt();     
                continue;
            }
            
            // is this an id entry
            if (s.startsWith("label")) {
                label = processLabel(data.getWord());
                continue;
            }

            // is this an id entry
            if (s.startsWith("value")) {
                value = data.getInt();
                continue;
            }

        } 
        
        if (!s.startsWith(ENDSTRING)) throw new RuntimeException("File ended before "+objectString+"  closed by "+ENDSTRING);

        //add new node
        
        nodeIDToIndex.put(id,nodeID.size() );
        nodeLabel.add(label);
        nodeValue.add(value);
        nodeID.add(id);    
        if (infoLevel>1) System.out.println("Found node "+(nodeID.size()-1)+" id="+valueString(id)+" label="+label);

    }
 
    private void edgeObject(){
        String s=UNSET;
        String objectString = "edge";
        
         // find object's opening string
        if (findNextWord(STARTSTRING).equals(UNSET)) {
                  throw new RuntimeException("No opening "+STARTSTRING+" found for "+objectString);
              }

        int source=IUNSET;
        int target=IUNSET;
        double weight=DUNSET;
        while (!data.eof()) 
        {
            s=data.getWord();
            // is it the end of the graph object?
            if (s.startsWith(ENDSTRING)) break;
            
            // is this an source entry
            if (s.startsWith("source")) {
                source = data.getInt(); 
                continue;
            }
            
            // is this an target entry
            if (s.startsWith("target")) {
                target = data.getInt();   
                continue;
            }
            
            // is this an weight entry
            if (s.startsWith("value")) {
                weight = data.getDouble();  
                continue;
            }
            
             
        } 
        
        if (!s.startsWith(ENDSTRING)) throw new RuntimeException("File ended before "+objectString+"  closed by "+ENDSTRING);

        //add new node
        
        if ((source==IUNSET) || (target==IUNSET)) throw new RuntimeException("Edge "+edgeSource.size()+" has bad source or target vertex");
        if (weightsOn && (weight==DUNSET)) throw new RuntimeException("Edge "+edgeSource.size()+" has bad weight ");
        
        edgeSource.add(source);
        edgeTarget.add(target);
        if (weightsOn) edgeWeight.add(weight);

        if (infoLevel>1) {
            s=" source="+valueString(source)+" target="+valueString(target);
            if (weightsOn) s=s+" weight="+valueString(weight);
            System.out.println("Found edge "+(edgeSource.size()-1)+s);
        }

    }
    
    
    
    /**
     * Searches for next word starting with given prefix string.
     * @param prefix word read in must start with this  prefix.
     * @return the whole word read in or {@value #UNSET} if end of file reached.
     */
    private String findNextWord(String prefix){
        String s;
        while (!data.eof()) {
            s= data.getWord();
            if (infoLevel>1) System.out.println("s=\""+s+"\"");
            if (s.startsWith(prefix)) return s;
           }
        return UNSET;
    }
    
        /**
         * Converts input to a timgraph.
         * <p>Weighted and labelled nature set by input parameters.
         * Directed nature set by GML file.
         * Sets up new graph fom this data.
         * @param tg graph to be set up
         * @param weighted true if want to read a weighted graph otherwise weights (of present) are ignored.
         * @param vertexLabelled true if want vertex labels
         */
    public int setTimGraph(timgraph tg, boolean weighted, boolean vertexLabelled ){
        tg.setDirectedGraph(directedGraph);
        tg.setNetwork(nodeID.size(), edgeSource.size()*2);
        
        // Add the vertices
        Integer id = new Integer(IUNSET);
        Integer value = new Integer(IUNSET);
        VertexLabel newLabel = new VertexLabel();
        for (int v=0; v<nodeID.size(); v++)
        {
            if (tg.isVertexLabelled()){
                id=nodeID.get(v);
                if (id==IUNSET) id=v;
                value=nodeValue.get(v);
                String label = nodeLabel.get(v);
                if (label.equals(UNSET)) label=Integer.toString(v);
                newLabel.setName(label);
                if (value==IUNSET) newLabel.setNumber(id);
                else newLabel.setNumber(value);
                tg.addVertex(newLabel);
                }
            else tg.addVertex();            
        }
        
        // Now add the edges
        int source=IUNSET;
        int target=IUNSET;
        int s=IUNSET;
        int t=IUNSET;
        double weight = DUNSET;
//        Iterator<Integer> iterS = edgeSource.iterator();
//        Iterator<Integer> iterT = edgeSource.iterator();
//        Iterator<Integer> iterW = edgeSource.iterator();
        for (int e=0; e < edgeSource.size(); e++)
        {
            source = edgeSource.get(e);
            target = edgeTarget.get(e);
            s = nodeIDToIndex.get(source);
            t = nodeIDToIndex.get(target);
            if (tg.isWeighted()) tg.addEdge(s,t,edgeWeight.get(e));
            else tg.addEdge(s,t);
        }// eo for e

        return 0;

    }
    
    public String processLabel(String sin){
        int f = 0;
        if (sin.charAt(0)=='\"') f=1;
        int l = sin.length();
        if (sin.charAt(l-1)=='\"') l=l-1;
        return sin.substring(f,l);
    }
    
    public int getNumberVertices(){return this.nodeID.size();}
    
    public int getNumberEdges(){return this.edgeSource.size();}
    
    private String valueString(int i){String s= (i==IUNSET)?"Unset":Integer.toString(i); return s;}
    private String valueString(double d){String s= (d==DUNSET)?"Unset":Double.toString(d); return s;}
    
//         /** Prints Edges.
//         * <br>Prints with headers and with vertex labels.
//         *@param PS a print stream for the output such as System.out
//         *@param cc comment string
//         *@param sep separation string
//         */     
//    public void printEdges(PrintStream PS, String cc, String sep){
//        
//    }
//    
    
}
