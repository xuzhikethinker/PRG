/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.run.ImperialPapers.General;
import TimGraph.timgraph;
//import TimGraph.io.FileStemmer;
//import TimUtilities.StringUtilities.Filters.ImperialPapersFilter;
import java.util.TreeSet;
import java.io.PrintStream;
//import java.util.TreeMap;


/**
 * Sets up standard networks from input files.
 * @author time
 */
public class SetUpNetwork {
    /**
     * Separation String used for output.
     */
    final static public String SEP = "\t "; 

    
    //static timgraph tg;
    static int infoLevel=0;
    static int outputControlNumber=0;
    static int graphType=0;
    static String dirRoot = System.getProperty("user.dir");      //"/PRG/networks/timgraph/";
    
    final static String[] type = {
        "EcoOrg1",
        "RAEmanGCC",
        "RAEman",
        "ICteststempp",
        "ICstempp",
        "ICNSstemppUL",
        "ICNSnpstempp",
        "ICNSstempp",
        "ICpsectest",
        "ICpsec",
        "ICpdept",
        "ICteststemtt ",
        "ICstemttew0500",
        "ICstemttew0200",
        "ICstemtt",
        "ICNSstemttew2000",
        "ICNSstemttew1500",
        "ICNSstemttew1250",
        "ICNSstemttew1000",
        "ICNS10stemtt",
        "ICNSstemtt",
        "ICtestpt",
        "ICNStest2pt",
        "ICNSpttest",
        "ICpt",
        "ICstempt",
        "ICNS10stempt",
        "ICNSpt",
        "ICNSstempt",
        "hepphcit",
        "hepthcit",
        "FCommon",
        "CondMatGCC",
        "CondMat",
        "WordAssociationPDFV05Bright5",
        "WordAssociationPDFV05Bright4",
        "WordAssociationPDFV05Bright3",
        "WordAssociationPDFV05Bright2",
        "WordAssociationPDFV05Bright1",
        "WordAssociationPDFV05",
        "WordAssociationTest",
        "WordAssociationBright4",
        "WordAssociationBright3",
        "WordAssociationBright2",
        "WordAssociationBright1",
        "WordAssociation",
        "MacTutor",
        "NetScience",
        "NetScienceGCC",
        "NetScienceTest",
        "KarateClub",
        "KarateClubUCIuw",
        "KarateClubUCIw",
        "CaveMan4x8m1Ring",
        "CaveMan4x8m4Ring",
        "CaveMan8x4m1Ring",
        "CaveMan8x4m4Ring",
        "CaveManCV8x4m4Ring",
        "CaveManCV8x8m4Ring",
        "Line8K1",
        "Line8K2",
        "dBowTie",
        "LongBowTie",
        "BowTie",
        "(default) read in from specified file."
    };
    
    final static String[] longType = {
"EcoOrg1",
"RAEmanGCC GCC of Author-Author projection from Management RAE submissions",
"RAEman Author-Author projection from Management RAE submissions",
"ICteststempp Imperial test paper-paper weighted network",
"ICstempp Imperial all paper-paper weighted network",
"ICNSnpstempp Imperial Natural Sciences numbered papers all paper-paper unlabelled weighted network",
"ICNSstempp Imperial Natural Sciences all paper-paper unlabelled weighted network",
"ICNSstempp Imperial Natural Sciences all paper-paper weighted network",
"ICpsectest Imperial paper-section test bipartite network",
"ICpsec Imperial paper-section bipartite network",
"ICpdeptImperial paper-dept bipartite network",
"ICteststemtt Imperial test stemmed term-term weighted network",
"ICstemttew0500 Imperial terms-terms weighted network, weights >= 4.0",
"ICstemttew0200 Imperial terms-terms weighted network, weights >= 2.0",
"ICstemtt Imperial all terms-terms weighted network",
"ICNSstemttew2000 Imperial Natural Sciences terms-terms weighted network, weights >= 2.0",
"ICNSstemttew1500 Imperial Natural Sciences terms-terms weighted network, weights >= 1.5",
"ICNSstemttew1250 Imperial Natural Sciences terms-terms weighted network, weights >= 1.25",
"ICNSstemttew1000 Imperial Natural Sciences terms-terms weighted network, weights >= 1.0",
"ICNS10stemtt Imperial Natural Sciences 1 in 10 terms-terms weighted network",
"ICNSstemtt Imperial Natural Sciences all terms-terms weighted network",
"ICtestpt test file for Imperial papers-terms network",
"ICNStest2pt test 2 file for Imperial Natural Sciences papers-terms network",
"ICNSpttest test file for Imperial Natural Sciences papers-terms network",
"ICpt Imperial papers-terms(unstemmed) network",
"ICstempt Imperial papers-stemmed terms network",
"ICNS10stempt Imperial Natural Sciences 1 in 10 papers-stemmed terms network",
"ICNSpt Imperial Natural Sciences papers-terms(unstemmed) network",
"ICNSstempt Imperial Natural Sciences papers-stemmed terms network",
"hep-ph-citation, labels are randomly chosen permutation of 1..9999 numbers",
"hep-th-citation labels are arXiv numbers",
"Shaid's Fortran subroutine - common block bipartite network",
"Newman's weighted author-author cond-mat based graph",
"GCC of  Newman's weighted author-author cond-mat based graph",
"WordAssociationBright5 in PDFV05 format (Words within 5 steps of Bright in South Florida data)",
"WordAssociationBright4 in PDFV05 format (Words within 4 steps of Bright in South Florida data)",
"WordAssociationBright3 in PDFV05 format (Words within 3 steps of Bright in South Florida data)",
"WordAssociationBright2 in PDFV05 format (Words within 2 steps of Bright in South Florida data)",
"WordAssociationBright1 in PDFV05 format (Words within 1 steps of Bright in South Florida data)",
"WordAssociation in PDFV05 format",
"WordAssociation Test (Some Aardvark connected words South Florida data)",
"WordAssociationBright4 (Words within 4 steps of Bright in South Florida data)",
"WordAssociationBright3 (Words within 3 steps of Bright in South Florida data)",
"WordAssociationBright2 (Words within 2 steps of Bright in South Florida data)",
"WordAssociationBright1 (Words within 1 steps of Bright in South Florida data)",
"WordAssociation (South Florida data)",
"MacTutor",
"Newman's weighted author-author network science based graph",
"GCC of Newman's weighted author-author network science based graph",
"NetScience Test network",
"Unweighted Zachary Karate Club from Newman",
"Unweighted Zachary Karate Club from UCInet",
"Weighted Zachary Karate Club from UCInet",
"CaveMan4x8m1Ring",
"CaveMan4x8m4Ring",
"CaveMan8x4m1Ring",
"CaveMan8x4m4Ring",
"CaveManCV8x4m4Ring",
"CaveManCV8x8m4Ring",
"Line of eight vertices with connections to nearest-neighbours",
"Line of eight vertices with connections to nearest-neighbours and next-nearest-neighbours",
"directed Bow Tie",
"Bow Tie with edge between two triangles, 6 vertices 7 edges",
"Standard 5 vertex 6 edge Bow Tie graph",
"(default) read in from file specified by timgrph arguments"
    };

    final static int [] typeNumber = {
         210,
         201,200,
         149,145,142,141,140,
         139,131,130, 
         129,128,127,126,125,124,123,122,121,120,
         119,118,117,114,113,112, 111,110,
         101,100,
         90,
         81,80,
         75,74,73,72,71,70,
         69,64,63,62,61, 60, 50, 40, 41, 42, 30, 31, 32, 20, 21, 22, 23, 24, 25, 
         11, 10,  3,  2,  1,  0};

    public SetUpNetwork(int infol){infoLevel=infol;};
    
    /**
     * Main routine to set up networks.
     * <p>If first argument exists and begins with a * it is used for the network set up.
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
      printNetworkTypes(System.out, "", "\t") ;       
    } 
 
public timgraph setUpNetwork(String name, String [] args, int infol){
      timgraph tg = new timgraph(args);
      dirRoot=tg.inputName.getDirectoryRoot();
      initialise(tg, name, args, infol);  
      return tg;
    }
    
    public timgraph setUpNetwork(String name, String [] args){
      timgraph tg = new timgraph(args);
      dirRoot=tg.inputName.getDirectoryRoot();
      initialise(tg, name, args, 0);  
      return tg;
    }
    
    /**
     * Sets up files according to the following numbering scheme:
     * <table border>
     * <tr><th ALIGN=RIGHT>Number</th><th ALIGN=LEFT>Description</th></tr>
     * <tr><td ALIGN=RIGHT>210</td><td ALIGN=LEFT>EcoOrg1</td></tr>
     * <tr><td ALIGN=RIGHT>201</td><td ALIGN=LEFT>RAEmanGCC GCC of Author-Author projection from Management RAE submissions</td></tr>
     * <tr><td ALIGN=RIGHT>200</td><td ALIGN=LEFT>RAEman Author-Author projection from Management RAE submissions</td></tr>
     * <tr><td ALIGN=RIGHT>149</td><td ALIGN=LEFT>ICteststempp Imperial test paper-paper weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>145</td><td ALIGN=LEFT>ICstempp Imperial all papers - stemmed paper-paper weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>142</td><td ALIGN=LEFT>ICNSnpstempp Imperial Natural Sciences all papers by number - stemmed paper-paper weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>141</td><td ALIGN=LEFT>ICNSstemULpp Imperial Natural Sciences all papers - stemmed paper-paper unlabelled weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>140</td><td ALIGN=LEFT>ICNSstempp Imperial Natural Sciences all papers - stemmed paper-paper weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>139</td><td ALIGN=LEFT>ICpsectest Imperial paper-section test bipartite network</td></tr>
     * <tr><td ALIGN=RIGHT>131</td><td ALIGN=LEFT>ICpsec Imperial paper-section bipartite network</td></tr>
     * <tr><td ALIGN=RIGHT>130</td><td ALIGN=LEFT>ICpdeptImperial paper-dept bipartite network</td></tr>
     * <tr><td ALIGN=RIGHT>129</td><td ALIGN=LEFT>ICteststemtt Imperial test stemmed term-term weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>128</td><td ALIGN=LEFT>ICstemttew0500 Imperial all papers - stemmed term-term weighted network, not less than 4.0</td></tr>
     * <tr><td ALIGN=RIGHT>127</td><td ALIGN=LEFT>ICstemttew0200 Imperial all papers - stemmed term-term weighted network, not less than 2.0</td></tr>
     * <tr><td ALIGN=RIGHT>126</td><td ALIGN=LEFT>ICstemtt Imperial all papers - stemmed term-term weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>125</td><td ALIGN=LEFT>ICNSstemttew2000 Imperial Natural Sciences stemmed term-term weighted network, weights not less than 2.0</td></tr>
     * <tr><td ALIGN=RIGHT>124</td><td ALIGN=LEFT>ICNSstemttew1500 Imperial Natural Sciences stemmed term-term weighted network, weights not less than 1.5</td></tr>
     * <tr><td ALIGN=RIGHT>123</td><td ALIGN=LEFT>ICNSstemttew1250 Imperial Natural Sciences stemmed term-term weighted network, weights not less than 1.25</td></tr>
     * <tr><td ALIGN=RIGHT>122</td><td ALIGN=LEFT>ICNSstemttew1000 Imperial Natural Sciences stemmed term-term weighted network, weights not less than 1.0</td></tr>
     * <tr><td ALIGN=RIGHT>121</td><td ALIGN=LEFT>ICNS10stemtt Imperial Natural Sciences 1 in 10 papers - stemmed term-term weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>120</td><td ALIGN=LEFT>ICNSstemtt Imperial Natural Sciences all papers - stemmed term-term weighted network</td></tr>
     * <tr><td ALIGN=RIGHT>119</td><td ALIGN=LEFT>ICtestpt Imperial papers test network</td></tr>
     * <tr><td ALIGN=RIGHT>118</td><td ALIGN=LEFT>ICNStest2pt Imperial papers test 2 network, has real key words</td></tr>
     * <tr><td ALIGN=RIGHT>117</td><td ALIGN=LEFT>ICNStestpt Imperial papers test network</td></tr>
     * <tr><td ALIGN=RIGHT>114</td><td ALIGN=LEFT>ICpt Imperial papers-terms(unstemmed) network</td></tr>
     * <tr><td ALIGN=RIGHT>113</td><td ALIGN=LEFT>ICstempt Imperial papers-stemmed and filtered terms network</td></tr>
     * <tr><td ALIGN=RIGHT>112</td><td ALIGN=LEFT>ICNS10stempt Imperial Natural Sciences 1 in 10 papers-stemmed terms network</td></tr>
     * <tr><td ALIGN=RIGHT>111</td><td ALIGN=LEFT>ICNSpt Imperial Natural Sciences papers-terms(unstemmed) network</td></tr>
     * <tr><td ALIGN=RIGHT>110</td><td ALIGN=LEFT>ICNSstempt Imperial Natural Sciences papers-stemmed and filtered terms network</td></tr>
     * <tr><td ALIGN=RIGHT>101</td><td ALIGN=LEFT>hepphcit hep-ph-citation, labels are randomly chosen permutation of 1..9999 numbers</td></tr>
     * <tr><td ALIGN=RIGHT>100</td><td ALIGN=LEFT>hepthcit hep-th-citation labels are arXiv numbers</td></tr>
     * <tr><td ALIGN=RIGHT>90</td><td ALIGN=LEFT>FCommon FortranCommon Shaid's Fortran subroutine - common block bipartite network</td></tr>
     * <tr><td ALIGN=RIGHT>80</td><td ALIGN=LEFT>CondMat Newman's weighted author-author cond-mat based graph</td></tr>
     * <tr><td ALIGN=RIGHT>81</td><td ALIGN=LEFT>CondMat GCC of  Newman's weighted author-author cond-mat based graph</td></tr>
     * <tr><td ALIGN=RIGHT>75</td><td ALIGN=LEFT>WordAssociationBright5 in PDFV05 format (Words within 5 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>74</td><td ALIGN=LEFT>WordAssociationBright4 in PDFV05 format (Words within 4 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>73</td><td ALIGN=LEFT>WordAssociationBright3 in PDFV05 format (Words within 3 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>72</td><td ALIGN=LEFT>WordAssociationBright2 in PDFV05 format (Words within 2 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>71</td><td ALIGN=LEFT>WordAssociationBright1 in PDFV05 format (Words within 1 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>70</td><td ALIGN=LEFT>WordAssociation in PDFV05 format</td></tr>
     * <tr><td ALIGN=RIGHT>69</td><td ALIGN=LEFT>WordAssociation Test (Some Aardvark connected words South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>64</td><td ALIGN=LEFT>WordAssociationBright4 (Words within 4 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>63</td><td ALIGN=LEFT>WordAssociationBright3 (Words within 3 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>62</td><td ALIGN=LEFT>WordAssociationBright2 (Words within 2 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>61</td><td ALIGN=LEFT>WordAssociationBright1 (Words within 1 steps of Bright in South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>60</td><td ALIGN=LEFT>WordAssociation (South Florida data)</td></tr>
     * <tr><td ALIGN=RIGHT>50</td><td ALIGN=LEFT>MacTutor</td></tr>
     * <tr><td ALIGN=RIGHT>40</td><td ALIGN=LEFT>NetScience Newman's weighted author-author network science based graph</td></tr>
     * <tr><td ALIGN=RIGHT>41</td><td ALIGN=LEFT>NetScienceGCC GCC of Newman's weighted author-author network science based graph</td></tr>
     * <tr><td ALIGN=RIGHT>42</td><td ALIGN=LEFT>NetScienceTest</td></tr>
     * <tr><td ALIGN=RIGHT>32</td><td ALIGN=LEFT>KarateClubUCIw Weighted Zachary Karate Club from UCInet</td></tr>
     * <tr><td ALIGN=RIGHT>31</td><td ALIGN=LEFT>KarateClubUCIuw Unweighted Zachary Karate Club from UCInet</td></tr>
     * <tr><td ALIGN=RIGHT>30</td><td ALIGN=LEFT>KarateClub Unweighted Zachary Karate Club from Newman</td></tr>
     * <tr><td ALIGN=RIGHT>20</td><td ALIGN=LEFT>CaveMan4x8m1Ring</td></tr>
     * <tr><td ALIGN=RIGHT>21</td><td ALIGN=LEFT>CaveMan4x8m4Ring</td></tr>
     * <tr><td ALIGN=RIGHT>22</td><td ALIGN=LEFT>CaveMan8x4m1Ring</td></tr>
     * <tr><td ALIGN=RIGHT>23</td><td ALIGN=LEFT>CaveMan8x4m4Ring</td></tr>
     * <tr><td ALIGN=RIGHT>24</td><td ALIGN=LEFT>CaveManCV8x4m4Ring</td></tr>
     * <tr><td ALIGN=RIGHT>25</td><td ALIGN=LEFT>CaveManCV8x8m4Ring</td></tr>
     * <tr><td ALIGN=RIGHT>11</td><td ALIGN=LEFT>Line8K1 Line of eight vertices with connections to nearest-neighbours</td></tr>
     * <tr><td ALIGN=RIGHT>10</td><td ALIGN=LEFT>Line8K2 Line of eight vertices with connections to nearest-neighboursand next-nearest-neighbours</td></tr>
     * <tr><td ALIGN=RIGHT> 3</td><td ALIGN=LEFT>dBowTie directed Bow Tie</td></tr>
     * <tr><td ALIGN=RIGHT> 2</td><td ALIGN=LEFT>LongBowTie Long Bow Tie Bow Tie with edge between two triangles, 6 vertices 7 edges</td></tr>
     * <tr><td ALIGN=RIGHT> 1</td><td ALIGN=LEFT>BowTie Standard 5 vertex 6 edge Bow Tie graph</td></tr>
     * <tr><td ALIGN=RIGHT> 0</td><td ALIGN=LEFT>(default) read in from file specified by timgraph arguments</td></tr>
     * </table>
     * @param n number of graph to set up as given in table above
     * @param args timgraph arguments to use.
     */
 static public timgraph setUpNetwork(int n, String [] args){
      timgraph tg = new timgraph(args);
      graphType=n;
      if ((args.length>0) && (args[0].charAt(0)=='*')) graphType=Integer.parseInt(args[0].substring(1, args[0].length()));
      System.out.println("\n*** SetUpNetwork class, network type "+graphType+" = "+SetUpNetwork.typeString(graphType));
      dirRoot=tg.inputName.getDirectoryRoot();
      String rootName="RAEman";
      boolean getGCC;
      boolean makeDirected;
      boolean makeLabelled;
      boolean makeWeighted;
      boolean makeVertexEdgeList;
      boolean ICconvertPaperID=false;
      String ICDateString="090729";
      boolean useGID=true;
      String ICDateStringnp=ICDateString+(useGID?"":"np");  // used to access numbered papers rather than gid
      switch (graphType) {
          case 210: rootName="MATRIXSNAVSMSystem1";
                  makeDirected=false;
                  makeLabelled=true;
                  makeWeighted=true;
                  makeVertexEdgeList=true;
                  if (graphType==211) getGCC=false; else getGCC=true;
                  return setUpELS(rootName, getGCC,
                           infoLevel, outputControlNumber, 
                           makeDirected, makeLabelled, makeWeighted, makeVertexEdgeList);
              case 201:
              case 200:    
                  rootName="RAEman";
                  makeDirected=false;
                  makeLabelled=true;
                  makeWeighted=true;
                  makeVertexEdgeList=true;
                  if (graphType==200) getGCC=false; else getGCC=true;
                  return setUpELS(rootName, getGCC,
                           infoLevel, outputControlNumber, 
                           makeDirected, makeLabelled, makeWeighted, makeVertexEdgeList);
              case 149:
                  setUpICstem(tg,"ICtest",1,"pp");
                  break;
              case 145:
                  setUpICstem(tg, "IC"+ICDateStringnp, 1,true,"pp");
                  break;
              case 142:
                  String weightCut="0010";
                  setUpICnpDateStemEW(tg,"ICNS"+ICDateStringnp,1,"pp","ew1000");
                  break;
              case 141:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,1,true,"pp");
                  break;
              case 140:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,1,"pp");
                  break;
              case 139:
                  setUpICpunit(tg,"ICtest_psec");
                  break;
              case 131:
                  setUpICpunit(tg,"IC20090521_psec");
                  break;
              case 130:
                  setUpICpunit(tg,"IC20090521_pdept");
                  break;
              case 129:
                  setUpICstem(tg,"ICtest",1,"tt");
                  break;
              case 128:
                  setUpICstem(tg,"IC"+ICDateStringnp,1,"ttew0500");
                  break;
              case 127:
                  setUpICstem(tg,"IC"+ICDateStringnp,1,"ttew0200");
                  break;
              case 126:
                  setUpICstem(tg,"IC"+ICDateStringnp,1,"tt");
                  break;
              case 125:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,1,"ttew2000");
                  break;
              case 124:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,1,"ttew1500");
                  break;
              case 123:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,1,"ttew1250");
                  break;
              case 122:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,1,"ttew1000");
                  break;
              case 121:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,10,"tt");
                  break;
              case 120:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,1,"tt");
                  break;
              case 119:
                  setUpICpt(tg,"ICteststem"+ICDateStringnp);
                  break;
              case 118:
                  setUpICNStest2pt(tg);
                  break;
              case 117:
                  setUpICNStestpt(tg);
                  break;
              case 114:
                  filterStemICpt(tg,"IC"+ICDateString,ICconvertPaperID,1);
                  break;
              case 113:
                  //setUpICstem(tg,"IC",1,"pt");
                  setUpICpt(tg,"IC"+ICDateStringnp+"stem");
                  break;
              case 112:
                  setUpICstem(tg,"ICNS"+ICDateStringnp,10,"pt");
                  break;
              case 111:
                  filterStemICpt(tg,"ICNS"+ICDateString,ICconvertPaperID,1);
                  break;
              case 110:
                  //setUpICstem(tg,"ICNS",1,"pt");
                  setUpICpt(tg,"ICNS"+ICDateStringnp+"stem");
                  break;
              case 101:
                  setUphepphcit(tg);
                  break;
              case 100:
                  setUphepthcit(tg);
                  break;
              case 90:
                  setUpFCommon(tg);
                  break;
              case 80:
                  setUpCondMat(tg,false);
                  break;
              case 81:
                  setUpCondMat(tg,true);
                  break;

              case 75:
                  setUpWordAssociationPDFV05(tg,"bright",5);
                  break;
              case 74:
                  setUpWordAssociationPDFV05(tg,"bright",4);
                  break;
              case 73:
                  setUpWordAssociationPDFV05(tg,"bright",3);
                  break;
              case 72:
                  setUpWordAssociationPDFV05(tg,"bright",2);
                  break;
              case 71:
                  setUpWordAssociationPDFV05(tg,"bright",1);
                  break;
              case 70:
                  setUpWordAssociationPDFV05(tg,"bright",0);
                  break;
              case 69:
                  setUpWordAssociationTest(tg);
                  break;
              case 64:
                  setUpWordAssociationSubGraph(tg,"bright",4);
                  break;
              case 63:
                  setUpWordAssociationSubGraph(tg,"bright",3);
                  break;
              case 62:
                  setUpWordAssociationSubGraph(tg,"bright",2);
                  break;
              case 61:
                  setUpWordAssociationSubGraph(tg,"bright",1);
                  break;
              case 60:
                  setUpWordAssociation(tg);
                  break;
              case 50:
                  setUpMacTutor(tg);
                  break;
              case 40:
                  setUpNetScience(tg,false);
                  break;
              case 41:
                  setUpNetScience(tg,true);
                  break;
              case 42:
                  setUpNetScienceTest(tg);
                  break;

              case 30:
                  setUpKarateClub(tg);
                  break;
              case 31:
                  setUpKarateClubUCIuw(tg);
                  break;
              case 32:
                  setUpKarateClubUCIw(tg);
                  break;

              case 20:
                  setUpCaveMan4x8m1Ring(tg);
                  break;
              case 21:
                  setUpCaveMan4x8m4Ring(tg);
                  break;
              case 22:
                  setUpCaveMan8x4m1Ring(tg);
                  break;
              case 23:
                  setUpCaveMan8x4m4Ring(tg);
                  break;
              case 24:
                  setUpCaveManCV8x4m4Ring(tg);
                  break;
              case 25:
                  setUpCaveManCV8x8m4Ring(tg);
                  break;

              case 11:
                  setUpGraph(tg,"Line8K1");
                  break;
              case 10:
                  setUpGraph(tg,"Line8K2");
                  break;

              case 3:
                  setUpGraph(tg,"dBowTie");
                  break;
              case 2:
                  setUpGraph(tg,"LongBowTie");
                  break;
              case 1:
                  setUpGraph(tg,"BowTie");
                  break;
              
              case 0:    initialise(tg,timgraph.NONAME, args, 0);  
              break;
              
              default: throw new RuntimeException("Unknown graph type +"+graphType);          // Generic run  
                
          }
      return tg;
    }
          
    
   public static void initialise(timgraph tg, String name, String [] arglist, int infolevel){
        tg.initialiseSomeParameters(name, dirRoot, infolevel, 0);
        tg.parseParam(arglist);
        //if (tg.inputName.getNameRoot().equals(tg.outputName.getNameRoot())) tg.setNameRoot(tg.inputName.getNameRoot());
        tg.setNameRoot(tg.inputName.getNameRoot());
        //tg.doOneRun(0);
        tg.setNetworkInitialGraph(true); // this will read in the network
        tg.printParametersBasic();
    }

   /**
    * For paper - unit - unit - ... listings
    * @param tg graph to be set up
    * @param basicFileName <tt>inputBVNLS.dat</tt> is added to this
    */
       public static void setUpICpunit(timgraph tg, String basicFileName){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
       String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-fi"+basicFileName, "-fieinputBVNLS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
       initialise(tg,basicFileName,aList, infoLevel);
           }

   
    public static void setUpICNStestpt(timgraph tg){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
        String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-finICNStestpt", "-fieinputBVNLS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
        initialise(tg,"ICNSpttest",aList, infoLevel);
           }
    
    /**
     * Set up undirected unweighted paper-term bipartite network from a BVNLS file
     * @param tg
     * @param basicFileName
     */
    public static void setUpICpt(timgraph tg, String basicFileName){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
        String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-gbt", "-fin"+basicFileName+"pt", "-fieinputBVNLS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
        initialise(tg,basicFileName+"pt",aList, infoLevel);
           }
    

    public static void setUpICNStest2pt(timgraph tg){
        boolean convertIgnoreColumn=false;
        General.preProcess(tg,"ICNStest2", convertIgnoreColumn, 1, true);
           }
    
    /**
     * File names are of the form 
     * <tt>basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem"+type+"inputELS.dat"</tt>
     * @param tg timgraph to be set up
     * @param basicFileName ICNS or similar
     * @param numberLinesToSkip
     * @param type pt tt or pp
     * @param weightCutString string with weight cut indicator, e.g. ew0750
     */
    public static void setUpICnpDateStemEW(timgraph tg, String basicFileName, int numberLinesToSkip, String type, String weightCutString){
      String outputroot = basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem"+type+weightCutString;
      String bipartite = (type.startsWith("pt")?"t":"f");
      String weighted = (type.startsWith("pt")?"f":"t");
      String [] aList = { "-gvet", "-gdf", "-gew"+weighted, "-gvlt", "-gb"+bipartite, "-fin"+outputroot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
      initialise(tg,basicFileName+type,aList, infoLevel);
    }
    /**
     * File names are of the form 
     * <tt>basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem"+type+"inputELS.dat"</tt>
     * @param basicFileName ICNS or similar
     * @param numberLinesToSkip
     * @param unlabelled
     * @param type pt tt or pp
     */
    public static void setUpICstem(timgraph tg,String basicFileName, int numberLinesToSkip, boolean unlabelled, String type){
      String outputroot = basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem"+(unlabelled?"UL":"")+type;
      String bipartite = (type.startsWith("pt")?"t":"f");
      String weighted = (type.startsWith("pt")?"f":"t");
      String [] aList = { "-gvet", "-gdf", "-gew"+weighted, "-gvlt", "-gb"+bipartite, "-fin"+outputroot, "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
      initialise(tg,basicFileName+type,aList, infoLevel);
    }
    
    /**
     * File names are of the form 
     * <tt>basicFileName+(numberLinesToSkip>1?numberLinesToSkip:"")+"stem"+type+"inputELS.dat"</tt>
     * @param basicFileName ICNS or similar
     * @param numberLinesToSkip
     * @param type pt tt or pp
     */
    public static void setUpICstem(timgraph tg,String basicFileName, int numberLinesToSkip, String type){
        setUpICstem(tg,basicFileName, numberLinesToSkip, false, type);
      
    }
    
    /**
     * Does filtering and stemming on raw data.
     * @param tg
     * @param basicFileName ICNS or similar
     * @param convertIgnoreColumn true if want to convert paper gid to number
     * @param numberLinesToSkip
     */
    public static void filterStemICpt(timgraph tg,String basicFileName,boolean convertIgnoreColumn, int numberLinesToSkip){
        General.preProcess(tg,basicFileName, convertIgnoreColumn, numberLinesToSkip, true);
    }
    


    public static void setUphepphcit(timgraph tg){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
        String [] aList = { "-gvet", "-gdt", "-gewf", "-gvlt", "-finhep-ph-citations", "-fieinputEL.dat", "-gn99",  "-e0", "-o9", "-xi0"};
        initialise(tg,"hepphcit",aList, infoLevel);
           }
    
   public static void setUphepthcit(timgraph tg){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
        String [] aList = { "-gvet", "-gdt", "-gewf", "-gvlt", "-finhep-th-citations", "-fieinputELS.dat", "-gn99",  "-e0", "-o9", "-xi0"};
        initialise(tg,"hepthcit",aList, infoLevel);
           }
    

    public static void setUpFCommon(timgraph tg){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
        String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-finfcommon", "-fieinputBVNLS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
        initialise(tg,"fcommon",aList, infoLevel);
           }

    
    public static void setUpWordAssociation(timgraph tg){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
        String [] aList = { "-gvet", "-gdt", "-gewt", "-gvlt", "-finSFWAN", "-fieinputELS.dat", "-gn99",  "-e0", "-o23", "-xi0"};
        initialise(tg,"SFWAN",aList, infoLevel);
           }

        
    /**
     * Set up South Florida Word Association Graphs.
     * <p>Makes a ring graph centred on vertex of given name, 
     * that is all vertices must be within a certain distance from this named vertex.
     * @param tg graph to be set up
     * @param vertexName name of vertex at centre of ring
     * @param maxDistance max distance from central vertex of vertices to be included.
     */
    public static void setUpWordAssociationSubGraph(timgraph tg, String vertexName, int maxDistance){
        String [] aList = { "-gvet", "-gdt", "-gewt", "-gvlt", "-finSFWAN", "-fieinputELS.dat", "-gn99",  "-e0", "-o11", "-xi0"};
        String name="SFWAN";
        timgraph fullns = new timgraph();
        fullns.initialiseSomeParameters(name, dirRoot, infoLevel, 0);
        fullns.parseParam(aList);
        fullns.setNetworkInitialGraph(true);
        fullns.printParametersBasic();
        if (fullns.getNumberVertices()<20) fullns.printNetwork(true);
        tg = fullns.makeRing(vertexName, maxDistance, fullns.isDirected(), true, fullns.isWeighted(), true);
        if (tg.getNumberVertices()<20) tg.printNetwork(true);
        tg.inputName.setNameRoot(name+vertexName+maxDistance);
        tg.outputName.setNameRoot(name+vertexName+maxDistance);        
    }
    
    /**
     * Sets up graph following PDFV05 Nature paper.
     * <br>If maxDistance is set to be positive then also extracts subgraph 
     * centred on word <tt>vertexName</tt> with distance <tt>maxDistance</tt>.
     * @param tg graph to be set up
     * @param vertexName name of vertex at centre of ring
     * @param maxDistance max distance from central vertex of vertices to be included.
     */
    public static void setUpWordAssociationPDFV05(timgraph tg, String vertexName, int maxDistance){
        String [] aList = { "-gvet", "-gdt", "-gewt", "-gvlt", "-finSFWAN", "-fieinputELS.dat", "-gn99",  "-e0", "-o11", "-xi0"};
        String name="SFWANPDFV05";
        timgraph fullns = new timgraph();
        fullns.initialiseSomeParameters(name, dirRoot, infoLevel, 0);
        fullns.parseParam(aList);
        fullns.setNetworkInitialGraph(true);
        fullns.printParametersBasic();
        
        // first get smaller subgraph if thats whats wanted
        timgraph ng;
        if(maxDistance<=0) {ng=fullns;}
        else {
            ng = fullns.makeRing(vertexName, maxDistance, fullns.isDirected(), fullns.isWeighted(), fullns.isVertexLabelled(), fullns.isVertexEdgeListOn());
            ng.inputName.setNameRoot(name+vertexName+maxDistance);
            ng.outputName.setNameRoot(name+vertexName+maxDistance);        
        }
        ng.printParametersBasic();
        BasicAnalysis.analyse(ng);

        
        if (ng.getNumberVertices()<20) ng.printNetwork(true);
        
        tg = ng.makeConsolidated(0.025, true, true, false, true);     
        tg.addToNameRoot("cons025");
        tg.printParametersBasic();
        if (tg.getNumberVertices()<20) tg.printNetwork(true);
        BasicAnalysis.analyse(tg);
        
    }

    public timgraph makeSubGraph(timgraph fullns, String vertexName, int maxDistance){
        int vertex;
        for (vertex=0; vertex<fullns.getNumberVertices();vertex++) 
            if (fullns.getVertexName(vertex).equalsIgnoreCase(vertexName)) break;
        if (vertex==fullns.getNumberVertices()) System.err.println("*** NOT FOUND VERTEX "+vertexName);
        TreeSet<Integer> sgvertexList= fullns.getRing(vertex,maxDistance);
        // boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList,
        System.out.println("Component of "+fullns.inputName.getNameRoot()+" centred on vertex "+vertexName+" number "+vertex+" upto distance "+maxDistance+" found "+sgvertexList.size()+" vertices");
        timgraph newgraph = fullns.projectSubgraph("proj",sgvertexList, fullns.isDirected(), 
                true, fullns.isWeighted(), true, fullns.isBipartite()) ;
//        if (tg.getNumberVertices()<20) tg.printNetwork(true);
//        newgraph.inputName.setNameRoot(name+vertexName+maxDistance);
//        newgraph.outputName.setNameRoot(name+vertexName+maxDistance);        
        return newgraph;
    }
    
    public static void setUpWordAssociationTest(timgraph tg){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
        String [] aList = { "-gvet", "-gdt", "-gewf", "-gvlt", "-finSFWANtest", "-fieinputELS.dat", "-gn99",  "-e0", "-o11", "-xi0"};
        initialise(tg,"SFWAN",aList, infoLevel);
           }
       public static void setUpMacTutor(timgraph tg){
        //                   -get    -gdt    -gwf    -glt    -finMacTutor    -fieinputVNLS.dat    -gn99     -e0   -o19    -xi2
        String [] aList = { "-gvet", "-gdt", "-gewf", "-gvlt", "-finMacTutor", "-fieinputVNLS.dat", "-gn99",  "-e0", "-o61", "-xi0"};
        //initialise(tg,"ZacharyKarateClubUCIEdges",aList, infoLevel);
        //initialise(tg,"netscienceNeAT",aList, infoLevel);
        initialise(tg,"MacTutor",aList, infoLevel);
           }

   public static void setUpKarateClub(timgraph tg){
        String [] aList = { "   "};
        initialise(tg,"karateNeAT",aList, infoLevel);
    }
    public static void setUpKarateClubUCIuw(timgraph tg){
        String [] aList = { "-gvet", "-gdf", "-gewf", "-fieinputAdjMat.dat", "-gn99",  "-e0", "-o0", "-xi0"};
        initialise(tg,"karateUCIuw",aList, infoLevel);
    }
    public static void setUpKarateClubUCIw(timgraph tg){
        String [] aList = { "-gvet", "-gdf", "-gewt", "-fieinputAdjMat.dat", "-gn99",  "-e0", "-o0", "-xi0"};
        initialise(tg,"karateUCIw",aList, infoLevel);
    }

   /**
     * Sets up a weighted undirected graph from an ELS file.
     * <p>A vertex edge list is created and the option to select a GCC is included
     * @param rootName name of graph, used as the root of all fiel names
     * @param getGCC true if want just the GCC returned.
     * @param infol information level sets information given, >0 for increasing debugging info, <0 for increasing quietness
     * @param outputc controls output levels
     * @param makeDirected true (false) if want (un)directed graph
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     * @return graph created
     */
   public static timgraph setUpELS(String rootName, boolean getGCC,
          int infol, int outputc, 
          boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList){
        String [] aList = { "-gd"+torf(makeDirected), "-gvl"+torf(makeLabelled), "-gew"+torf(makeWeighted), "-gve"+torf(makeVertexEdgeList), "-fieinputELS.dat", "-gn99",  "-e0", "-o"+outputc, "-xi"+infol};
        timgraph tg;
        if (getGCC) tg = extractGCC(rootName, aList);
        else {
            tg = new timgraph(aList);
            initialise(tg,rootName,aList, infoLevel);
        }
        return tg;
   }

    /**
     * Sets up a weighted undirected graph from an ELS file.
     * <p>A vertex edge list is created and the option to select a GCC is included
     * <p>Information level and output levels set by globals.
     * @param rootName name of graph, used as the root of all fiel names
     * @param getGCC true if want just the GCC returned.
     * @param makeDirected true (false) if want (un)directed graph
     * @param makeLabelled true (false) if want (un)labelled graph
     * @param makeWeighted true (false) if want (un)weighted graph
     * @param makeVertexEdgeList true (false) if (don't) want vertexEdgeList to be made.
     * @return graph created
     */
   public static timgraph setUpELS(String rootName, boolean getGCC,
          boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList){
          return setUpELS(rootName, getGCC,
          infoLevel, outputControlNumber, 
          makeDirected, makeLabelled, makeWeighted, makeVertexEdgeList);
   }

   /**
    * Returns String of t or f depedning on boolean argument
    * @param b boolean argument
    * @return String of t if true, f if false
    */
   static public String torf(boolean b){return (b?"t":"f");}
   
    public static void setUpNetScienceEL(timgraph tg){
        String [] aList = { "-gvet", "-gdf", "-gewf", "-gvlt", "-fieinputELS.dat", "-gn99",  "-e0", "-o0", "-xi0"};
        initialise(tg,"netscienceNeAT",aList, infoLevel);
    }
    
    public static void setUpNetScience(timgraph tg, boolean getGCC){
        String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-fieinput.gml", "-gn99",  "-e0", "-o0", "-xi0"};
        //initialise(tg,"netscience",aList, infoLevel);
        String name="netscience";
        if (getGCC) tg = extractGCC(name, aList);
        else initialise(tg,name,aList, infoLevel);
           }
  
     
    public static void setUpCondMat(timgraph tg,boolean getGCC){
        String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-fieinput.gml", "-gn99",  "-e0", "-o0", "-xi0"};
        String name = "cond-mat";
        if (getGCC) tg = extractGCC(name, aList);
        else initialise(tg,name,aList, infoLevel);
           }

  
    /**
     * Extracts a GCC from a graph to be read in.
     * @param name name of original graph, GCC is appended in reulting graph
     * @param aList list of arguments to fees to the new graph.
     * @return timgraph which is the GCC of graph read in.
     */
    static public timgraph extractGCC(String name, String [] aList ){
        timgraph fullns = new timgraph();
        fullns.initialiseSomeParameters(name, dirRoot, infoLevel, 0);
        fullns.parseParam(aList);
        fullns.setNetworkInitialGraph(true);
        fullns.printParametersBasic();
        if (fullns.getNumberVertices()<20) fullns.printNetwork(true);
        fullns.calcComponents();
        fullns.printComponentInfo();
        TreeSet<Integer> sgvertexList = fullns.getGCC();
        //boolean makeDirected, boolean makeLabelled, boolean makeWeighted, boolean makeVertexEdgeList,
        timgraph tg = fullns.projectSubgraph("GCC",sgvertexList, fullns.isDirected(), 
                true, fullns.isWeighted(), true, fullns.isBipartite()) ;
        if (tg.getNumberVertices()<20) tg.printNetwork(true);
        tg.inputName.setNameRoot(name+"GCC");
        tg.outputName.setNameRoot(name+"GCC");
        tg.printParametersBasic();
        return tg;
    }
  
 
    
   
    public static void setUpGraph(timgraph tg, String type){
     if (type.startsWith("BowTie")) setUpBowTie(tg);
     if (type.startsWith("LongBowTie")) setUpLongBowTie(tg);
     if (type.startsWith("dBowTie")) setUpDirectedBowTie(tg);
     if (type.startsWith("Line8K1")) setUpLine8K1(tg);
     if (type.startsWith("Line8K2"))  setUpLine8K2(tg);
     if (type.startsWith("Line7K1"))  setUpLine7K1(tg);
     
     
     //tg.OutputGraphInfo(System.out, "#", 0.0)  ;
      //tg.generalOutput("#",0.0);
     //tg.printVertices(System.out, true)  ;
    }
    
    public static void setUpBowTie(timgraph tg){
        String [] BowTieList = { "-gvet", "-gn8",  "-e0", "-o0", "-xi0"};
        initialise (tg,"BowTie",BowTieList,infoLevel);
    }
    
    public static void setUpLongBowTie(timgraph tg){
        String [] aList = { "-gvet", "-gn9",  "-e0", "-o0", "-xi0"};
        initialise (tg,"LongBowTie",aList,infoLevel);
    }
    
    public static void setUpDirectedBowTie(timgraph tg){
        String [] directedBowTieList = { "-gdt", "-gvet", "-gn8",  "-e0", "-o0", "-xi0"};
        initialise (tg,"DirectedBowTie",directedBowTieList,infoLevel);
    }
    
    public static void setUpLine8K1(timgraph tg){
        String [] line8K1List = { "-gvet", "-gn-1",  "-gv8", "-gm1", "-e0", "-o0", "-xi0"};
        initialise (tg,"Line8K1",line8K1List,infoLevel);
    }
    
    public static void setUpLine8K2(timgraph tg){
        String [] line8K2List = { "-gvet", "-gn-1",  "-gv8", "-gm2", "-e0", "-o0", "-xi0"};
        initialise (tg,"Bline8K2",line8K2List,infoLevel);
    }
    
    public static void setUpLine7K1(timgraph tg){
        String [] line7K1List = { "-gvet", "-gn-1",  "-gv7", "-gm1", "-e0", "-o0", "-xi0"};
        initialise(tg,"line7K1",line7K1List,infoLevel);
    }

    public static void setUpCaveMan4x8m1Ring(timgraph tg){
        String [] line8K1List = { "-gvet", "-gn-12",  "-gv32", "-gx4", "-gm1", "-e0", "-o0", "-xi0"};
        initialise(tg,"CaveMan4x8m1",line8K1List,infoLevel);
    }
    
    public static void setUpCaveMan4x8m4Ring(timgraph tg){
        String [] line8K1List = { "-gvet", "-gn-12",  "-gv32", "-gx4", "-gm4", "-e0", "-o0", "-xi0"};
        initialise(tg,"CaveMan4x8m4",line8K1List,infoLevel);
    }
    
    public static void setUpCaveMan8x4m1Ring(timgraph tg){
        String [] line8K1List = { "-gvet", "-gn-12",  "-gv32", "-gx8", "-gm1", "-e0", "-o0", "-xi0"};
        initialise(tg,"CaveMan8x4m1",line8K1List,infoLevel);
    }
    
    public static void setUpCaveMan8x4m4Ring(timgraph tg){
        String [] line8K1List = { "-gvet", "-gn-12",  "-gv32", "-gx8", "-gm4", "-e0", "-o0", "-xi0"};
        initialise(tg,"CaveMan8x4m4",line8K1List,infoLevel);
    }
    
    public static void setUpCaveManCV8x4m4Ring(timgraph tg){
        String [] line8K1List = { "-gvet", "-gn-14",  "-gv32", "-gx8", "-gm4", "-e0", "-o0", "-xi0"};
        initialise(tg,"CaveManCV8x4m4",line8K1List,infoLevel);
    }
    
    public static void setUpCaveManCV8x8m4Ring(timgraph tg){
        String [] line8K1List = { "-gvet", "-gn-14",  "-gv64", "-gx8", "-gm3", "-e0", "-o0", "-xi0"};
        initialise(tg,"CaveManCV8x8m4",line8K1List,infoLevel);
    }
    
     public static void setUpNetScienceTest(timgraph tg){
        String [] aList = { "-gvet", "-gdf", "-gewt", "-gvlt", "-fieinputELS.dat", "-gn99",  "-e0", "-o0", "-xi0"};
        //initialise(tg,"ZacharyKarateClubUCIEdges",aList, infoLevel);
        //initialise(tg,"netscienceNeAT",aList, infoLevel);
        initialise(tg,"netsciencetest",aList, infoLevel);
           }
    
    public static void setUpBowTieEL(timgraph tg){
        String [] argList = { "-gvet", "-gdf", "-gewf", "-fieinputEL.dat", "-gn99",  "-e0", "-o0", "-xi0"};
        initialise(tg,"BowTie",argList, infoLevel);
    }
    
   
   public static void setUpNetworkCollaboration(timgraph tg){
      String [] aList = { "-gvet", "-gdf", "-gewf", "-fieinputEL.dat", "-gn99",  "-e0", "-o0", "-xi0"};
      initialise(tg,"netscienceNeAT",aList, infoLevel);
   }
    
   
   /**
    * Finds index of given network type
    * @param t number of network type
    * @return index in final static strings, -1 if none found
    */
   private int typeIndex(String t){
       for (int i=0; i<type.length;i++) if ( type[i].startsWith(t)) return i;
       return -1;
   }
   
   /**
    * Finds index of given network type
    * @param t number of network type
    * @return index in final static strings, -1 if none found
    */
   static private int typeIndex(int t){
       for (int i=0; i<typeNumber.length;i++) if (t==typeNumber[i]) return i;
       return -1;
   }
   
   /**
    * Finds name of given network type
    * @param t number of network type
    * @return name of network, UNSET if none found
    */
   static public String typeString(int t){
       int ti=typeIndex(t);
       if (ti<0) return "UNSET"; else return type[ti];
   }
   
   /**
    * Name of current network type
    * @return name of network, UNSET if none found
    */
   static public String typeString(){
       return typeString(graphType);
   }
   
   /**
    * tests if given network type number exists
    * @param type number of network type
    * @return true if type exists, false if not
    */
   public boolean typeExists(int type){
       if (typeIndex(type)<0) return false;
       return true;
   }
   
    /**
     * Prints all types of network available.
     * @param PS a PrintStream such as System.out
     * @param cc comment string
     * @param sep separation string
     */
    static public void printNetworkTypes(PrintStream PS, String cc, String sep)  
    {
        for (int i=0; i<typeNumber.length;i++) PS.println(cc+typeNumber[i]+sep+type[i]+sep+longType[i]);
    }

    
}
