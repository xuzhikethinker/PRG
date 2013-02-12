/*
 * IslandSite.java
 *
 * Created on 24 July 2006, 15:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks.Vertex;

import java.util.ArrayList;
import TimUtilities.NumbersToString;
/**
 * Defines all the characteristics of an Island Site.
 * @author time
 */
                                       
public class IslandSite {

        static final double DUNSET = -76543.2; // some impossible double
        static final int IUNSET = -654321; // some impossible int
        static final double GUNSET = -54321.0987; // some impossible double
        static final String AUNSET = "?!!!!!";
        static final int SSDEFAULTLENGTH =3; // default length for short strings
        static final String SAUNSET = AUNSET.substring(0, SSDEFAULTLENGTH);
        // Variables are counted from 0 upwards so last is numbered (numberVariables-1)
        public String name=AUNSET; //0
        public String shortName=SAUNSET ; //1
        public double X=DUNSET; //2
        public double Y=DUNSET; //3
        public double Z=DUNSET; //4
        public double size=DUNSET; //5
        public double value=DUNSET; //6
        private double weight=DUNSET; //7
        public int weightRank =IUNSET; //8
        public double ranking=DUNSET; //9 
        public int rankingRank=IUNSET; //10
        public double influence=DUNSET;//11
        private int influenceInt=IUNSET; //12 ??? UNUSED
        public int influenceRank=IUNSET;  //13
        public double displaySize=IUNSET; //14
        public double strength=IUNSET; //15
        public double strengthIn=IUNSET; //16
        public double strengthOut=IUNSET; //17
        public int number=IUNSET; //18 number not set should be positive or zero
        public double rankOverWeight = 0; //19
        public int  rankOverWeightRank =IUNSET; //20
        public double cultureMax =IUNSET; //21 value of largest cultural influenceInt
        public int cultureSite = IUNSET; //22 site  of largest cultural influenceInt
        public double strengthSquaredIn=IUNSET; //23
        public double strengthSquaredOut=IUNSET; //24
        public double latitude=DUNSET; //25
        public double longitude=DUNSET; //26
        public String region=AUNSET; //27
        public double inValue=DUNSET; //28
        public double inWeight=DUNSET; //29
        public double betweenness=DUNSET;//30
        public int betweennessRank=IUNSET;  //31
        public double influenceprime=DUNSET;//32
        public int influenceprimeRank=IUNSET;  //33
        public double betweennessprime=DUNSET;//34
        public int betweennessprimeRank=IUNSET;  //35
        public double nbetweenness=DUNSET;//36
        public int nbetweennessRank=IUNSET;  //37
        public double nbetweennessprime=DUNSET;//38
        public int nbetweennessprimeRank=IUNSET;  //39

        private ArrayList<Double> geneList = new ArrayList<Double>(); 
        double geneLength =DUNSET;
        
        private static NumbersToString n2s = new NumbersToString(3);

        /** 
         * Array of characters indicates which of these values are integers, doubles or alphabetic (strings)
         */
        static final String[] parameterNames =
            {"ShortName", "Name", "XPos", "YPos", "ZPos", 
              "Size",  "Value",    "Weight", "WeightRank", "PageRank",
              "PageRankRank", "Influence", "InfluenceInt", "InfluenceRank", "DisplaySize",
              "Strength", "StrengthIn", "StrengthOut", "Number", "Ranking/Weight",
              "Ranking/Weight rank", "CultureMax", "CultureSite", "StrengthInSquared", "StrengthOutSquared",
              "Latitude", "Longitude", "Region", "ValueIn", "WeightIn",
              "Betweenness", "BetweennessRank", "Influence\'", "Influence\'Rank","Betweenness\'",
              "Betweenness\'Rank","NBetweenness", "NBetweennessRank", "NBetweenness\'", "NBetweenness\'Rank", };
        public static final int numberVariables = parameterNames.length; // now set by parameterNames.length

        public static final int valueIndex=IslandSite.getIndex("Value");
        public static final int weightIndex=IslandSite.getIndex("Weight");
        public static final int strengthInIndex=IslandSite.getIndex("StrengthIn");
        public static final int strengthOutIndex=IslandSite.getIndex("StrengthOut");
        public static final int rankingIndex=IslandSite.getIndex("PageRank");
        public static final int influenceIndex=IslandSite.getIndex("Influence");
        public static final int influenceRankIndex=IslandSite.getIndex("InfluenceRank");
        public static final int betweennessIndex=IslandSite.getIndex("Betweenness");
        public static final int betweennessRankIndex=IslandSite.getIndex("BetweennessRank");
        public static final int influenceprimeIndex=IslandSite.getIndex("Influence\'");
        public static final int influenceprimeRankIndex=IslandSite.getIndex("Influence\'Rank");
        public static final int betweennessprimeIndex=IslandSite.getIndex("Betweenness\'");
        public static final int betweennessprimeRankIndex=IslandSite.getIndex("Betweenness\'Rank");
        public static final int nbetweennessIndex=IslandSite.getIndex("NBetweenness");
        public static final int nbetweennessRankIndex=IslandSite.getIndex("NBetweennessRank");
        public static final int nbetweennessprimeIndex=IslandSite.getIndex("NBetweenness\'");
        public static final int nbetweennessprimeRankIndex=IslandSite.getIndex("NBetweenness\'Rank");

        
        static final char INTCHAR = 'i';
        static final char DOUBLECHAR = 'd';
        static final char ALPHACHAR = 'a';
        static final char DLISTCHAR = 'l';
        
        static final char [] typeList = {ALPHACHAR, ALPHACHAR, DOUBLECHAR,DOUBLECHAR,DOUBLECHAR,
                                   DOUBLECHAR,DOUBLECHAR,DOUBLECHAR,INTCHAR,  DOUBLECHAR,
                                   INTCHAR,   DOUBLECHAR,INTCHAR,   INTCHAR,  DOUBLECHAR,
                                   DOUBLECHAR,DOUBLECHAR,DOUBLECHAR,INTCHAR,  DOUBLECHAR,
                                   INTCHAR,   DOUBLECHAR,INTCHAR,   DOUBLECHAR,DOUBLECHAR,
                                   DOUBLECHAR,DOUBLECHAR,ALPHACHAR, DOUBLECHAR, DOUBLECHAR,
                                   DOUBLECHAR, INTCHAR, DOUBLECHAR, INTCHAR, DOUBLECHAR,
                                   INTCHAR, DOUBLECHAR, INTCHAR, DOUBLECHAR, INTCHAR};
        // list parameters in order in which we which to put them in a file
        static final int [] parameterFileList = {18,1,27,2,3,4,25,26,5,6,28,29};
        static final int [] fixedParameterList = {1,2,3,4,5,18,25,26,27};
    

        

/** Creates a new instance of IslandSite */
    public IslandSite() {
    }
    
        
        /* IslandSite Constructor of given site number.
         *@param siteNumber the number of the site
         */
        public IslandSite(int siteNumber)
          {
            number = siteNumber;
            name=Integer.toString(siteNumber);
            //shortName=name;
        }
        
        /* IslandSite Constructor  Deep copies site s.
         *@param s the site to be copied.
         */
          public IslandSite(IslandSite s)
          {
//            numberVariables=s.numberVariables;
            name=s.name; //1
            shortName=s.shortName; //0
            number=s.number; //18
            X=s.X; //2
            Y=s.Y; //3
            Z=s.Z; //4
            
        size=s.size; //5
        value=s.value;
        weight=s.weight; // weight = pure site size*value
        weightRank = s.weightRank;
        ranking=s.ranking;
        rankingRank=s.rankingRank;
        influence=s.influence;
        influenceInt=s.influenceInt; 
        influenceRank=s.influenceRank; 
        displaySize =s.displaySize;
        strength = s.strength; // strength = weight * total edges in and out
        strengthIn = s.strengthIn;
        strengthOut = s.strengthOut;
        rankOverWeight = s.rankOverWeight ; //19
        rankOverWeightRank = s.rankOverWeightRank; //20
        cultureMax = s.cultureMax; //21
        cultureSite = s.cultureSite; //22
        strengthSquaredIn = s.strengthSquaredIn; //23
        strengthSquaredOut = s.strengthSquaredOut; //24
        latitude= s.latitude; //25
        longitude= s.longitude; //26
        region=s.region; //27
        inValue=s.inValue; //28
        inWeight=s.inWeight; //29
        betweenness=s.betweenness;//30
        betweennessRank=s.betweennessRank;  //31
        influenceprime=s.influenceprime;//32
        influenceprimeRank=s.influenceprimeRank;  //33
        betweennessprime=s.betweennessprime;//34
        betweennessprimeRank=s.betweennessprimeRank;  //35
        nbetweenness=s.nbetweenness;//34
        nbetweennessRank=s.nbetweennessRank;  //35
        nbetweennessprime=s.nbetweennessprime;//34
        nbetweennessprimeRank=s.nbetweennessprimeRank;  //35

        
        for (int g=0; g<s.geneList.size(); g++) geneList.add(s.geneList.get(g));


              
          }
        
          /**
           *  Returns string value all data variables.
           *@param SEP separator string e.g. tab or space.
           *@return string with name of data variable number value.
           */
          public String toString(String SEP)
        {
              String s="";
              for (int i=0;i<numberVariables;i++) s=s+toString(i)+SEP;
              for (int g=0; g<geneList.size(); g++) s=s+geneList.get(g).toString()+SEP;
              return s;
          }
          
          /**
           *  Sets value of weight.
           */
          public void setWeight(){weight=value*size;}

          /**
           *  Sets value of site.
           */
          public void setValue(double v){value=v;}
          
          /**
           *  Sets in value of site.
           */
          public void setInValue(double v){inValue=v;}

          /**
           *  Sets value of in weight.
           */
          public void setInWeight(){inWeight=inValue*size;}


          
          /**
           *  Sets name of site.
           *@param s name of site.
           *@return name.
           */
          public String setName(String s)
        {
              int numberCharacters=2;
              name=s;
              shortName=name;
              if (name.length()<numberCharacters) return(name);
              shortName=name.substring(0, numberCharacters);
              return name;
          }

          
          /**
           *  Sets short name and name of site.
           *@param shorts short name of site.
           *@param s name of site.
           *@return name.
           */
          public String setName(String shorts, String s)
        {
              shortName=shorts;
              name=s;
              return s;
          }
          
          /**
           *  If unset makes short name the first two characters of name. 
           * 
           */
          public void setShortName()
        {
              if (shortName.equals(SAUNSET)) 
              {
                  if (name.length() >2) shortName=name.substring(0, SSDEFAULTLENGTH);
                  else shortName=name;
              }
          }

           
          /**
           *  Sets value of one integer site variable.
           * Returns value if an integer is selected 
           * or -97531 if not an integer variable.
           *@param index of data variable to set.
           *@param value to be taken by site variable.
           *@return value of integer else -97531.
           */
          public int setInt(int index, int value)
        {
              int res =0;
              switch (index)
              {
                  case 8:  weightRank=value; break;
                  case 10: rankingRank=value; break;
                  case 12: influenceInt=value; break;
                  case 13: influenceRank=value; break;
                  case 18: number=value; break;
                  case 20: rankOverWeightRank=value; break;
                  case 22: cultureSite = value; break;
                  case 31: betweennessRank=value; break;
                  case 33: influenceprimeRank=value; break;
                  case 35: betweennessprimeRank=value; break;
                  case 37: nbetweennessRank=value; break;
                  case 39: nbetweennessprimeRank=value; break;
                  default: res=-97531;
              }         
              return res;
          }

          /**
           *  Gets value of one integer site variable.
           *@return The weight = value*size.           *
           */
          public double getWeight()
        {
              setWeight();
              return weight;
          }

          /**
           *  Gets out value of one integer site variable.
           *@return The weight = value*size.           *
           */
          public double getOutWeight()
        {
              setWeight();
              return weight;
          }

          /**
           *  Gets out value of one integer site variable.
           *@return The weight = value*size.           *
           */
          public double getInWeight()
        {
              setInWeight();
              return inWeight;
          }

          
          /**
           *  Returns number of variables stored.
           *@return  number of variables stored.
           * @deprecated access using IslandSite.numberVariables
           */
          public int getNumberVariables() { return numberVariables; }
          
           /**
           *  Returns X coordinate.
           *@return X coordinate
           */
          public double getX() { return X; }

                    /**
           *  Returns Y coordinate.
           *@return Y coordinate
           */
          public double getY() { return Y; }

          /**
           *  Returns display size.
           *@return displaySize value
           */
          public double getDisplaySize() { return displaySize; }
          
          /**
           *  Returns value of site, <tt>v_i</tt>.
           * <p>This is a variable quantity.
           *@return value
           */
          public double getValue() { return value; }
          
          /**
           *  Returns in value of site, <tt>u_i</tt>.
           * <p>This is a variable quantity.
           *@return in value
           */
          public double getInValue() { return inValue; }
          
          /**
           *  Returns name as string.
           *@return name value
           */
          public String getName() { return name; }
          
          /**
           *  Returns full gene.
           *@return full gene
           */
          public ArrayList getGene() { return geneList; }
          
   /**
     * Length of gene.
    * <p>Calculates if not already done so.
     * @return length of gene
     */
    public double getGeneLength() {
        return geneLength;
    }

    
   /**
     * Length of gene.
     * @return length of gene
     */
    public double setGeneLength() {
        double v=0;
            double l2=0;
            for (int g=0; g<geneList.size();g++){
              v = geneList.get(g);
              l2+=v*v;
            }
        geneLength = Math.sqrt(l2);    
        return geneLength;
    }

         /**
           *  Returns  value of one site variable as an object.
           *@param index of site variable to return.
           *@return value of data variable.
           */
          public Object getObject(int index)
        {
            switch(index)
            {
                case 0: return shortName; // break; 
                case 1: return name; // break; 
                case 2: return X; // break; 
                case 3: return Y; // break; 
                case 4: return Z; // break; 
                case 5: return size; // break; 
                case 6: return value; // break; 
                case 7: return weight; // break; 
                case 8: return weightRank; // break; 
                case 9: return ranking; // break; 
                case 10: return rankingRank; // break; 
                case 11: return influence; // break; 
                case 12: return influenceInt; // break;  
                case 13: return influenceRank; // break; 
                case 14: return displaySize; // break; 
                case 15: return strength; // break; 
                case 16: return strengthIn; // break; 
                case 17: return strengthOut; // break; 
                case 18: return number; // break; 
                case 19: return rankOverWeight; // break; 
                case 20: return rankOverWeightRank; // break; 
                case 21: return cultureMax; // break; 
                case 22: return cultureSite; // break; 
                case 23: return strengthSquaredIn; // break; 
                case 24: return strengthSquaredOut; // break; 
                case 25: return latitude; // break; 
                case 26: return longitude; // break; 
                case 27: return region; // break;
                case 28: return inValue; // break; 
                case 29: return inWeight; // break;
                case 30: return betweenness; // break;
                case 31: return betweennessRank; // break;
                case 32: return influenceprime; // break;
                case 33: return influenceprimeRank; // break;
                case 34: return betweennessprime; // break;
                case 35: return betweennessprimeRank; // break;
                case 36: return nbetweenness; // break;
                case 37: return nbetweennessRank; // break;
                case 38: return nbetweennessprime; // break;
                case 39: return nbetweennessprimeRank; // break;

            }
            return AUNSET;
          }




          /**
           *  Returns double value of one site variable.
           *@param name variable name to return.
           *@return value of data variable.
           *
           */
          public double getVariable(String name)
        {
              int index=getIndex(name);
              return getVariable(index);
        }
         /**
           *  Returns double value of one site variable.
           *@param name variable name to return.
           *@return value of data variable.
           */
          public double getVariable(int index)
        {
              Object o = getObject(index);
              if (isNumerical(index)) return ((Double) o);
              if (isAlpha(index)) return ((String) o).codePointAt(0);
              return DUNSET;
          }
           /**
           *  Returns integer value of one site variable.
           *@param name variable name to return.
           *@return value of data variable.
           *
           */
          public double getVariableInt(String name)
        {
              int index=getIndex(name);
              return getVariableInt(index);
        }
        /**
           *  Returns double value of one site variable.
           *@param name variable name to return.
           *@return value of data variable.
           */
          public int getVariableInt(int index)
        {
              Object o = getObject(index);
              if (isNumerical(index)) return ((Integer) o);
              if (isAlpha(index)) return ((String) o).codePointAt(0);
              if (isDouble(index)) return IUNSET;
              return IUNSET;
          }

 
//          /**
//           *  Returns double value of one site variable.
//           *@param index of site variable to return.
//           *@return value of data variable.
//           *@deprecated use {@link IslandNetworks.Vertex.IslandSite}.getVariable
//           */
//          public double getVariableOld(int index)
//        {// might be better implemented using object and casting using isInt etc.
//            double s=DUNSET;
//            switch(index)
//            {
//                case 0: s=shortName.codePointAt(0); break;
//                case 1: s=name.codePointAt(0); break;
//                case 2: s=X; break;
//                case 3: s=Y; break;
//                case 4: s=Z; break;
//                case 5: s=size; break;
//                case 6: s=value; break;
//                case 7: s=weight; break;
//                case 8: s=weightRank; break;
//                case 9: s=ranking; break;
//                case 10: s=rankingRank; break;
//                case 11: s=influence; break;
//                case 12: s=influenceInt; break;
//                case 13: s=influenceRank; break;
//                case 14: s=displaySize; break;
//                case 15: s=strength; break;
//                case 16: s=strengthIn; break;
//                case 17: s=strengthOut; break;
//                case 18: s=number; break;
//                case 19: s=rankOverWeight; break;
//                case 20: s=rankOverWeightRank; break;
//                case 21: s=cultureMax; break;
//                case 22: s=cultureSite; break;
//                case 23: s=strengthSquaredIn; break;
//                case 24: s=strengthSquaredOut; break;
//                case 25: s=latitude; break;
//                case 26: s=longitude; break;
//                case 27: s=region.codePointAt(0); break;
//                case 28: s=inValue; break;
//                case 29: s=inWeight; break;
//                case 30: s=betweenness; break;
//                case 31: s=betweennessRank; break;
//
//            }
//            return s;
//          }
//
          
    /**
     *  Returns string of values needed to specify results of model
     * <br>Includes input parameters and basic outputs. Can exclude features that are derivable.
     * <p>No sep string at start or end.
     *@param sep separation string
     *@param dec number of decimal places to keep
     *@return string with names and values of parameters.
     */
          public String parameterStringValues(String sep, int dec)
          {
            String s="";
            for (int i =0; i<parameterFileList.length; i++) {
                s=s+(i==0?"":sep);
                // don'tt shorten long name
                int p = parameterFileList[i];
                if (p==1) s=s+toString(p); 
                else s=s+toStringShort(parameterFileList[i],dec);
            }
            return s+geneStringValues(sep, dec);
          }

          /**
           *  Returns string of values of genes with sep string at start not end.
           *@param sep separation string
           *@param dec number of decimal places to keep
           *@return string with values of genes.
           */
          public String geneStringValues(String sep, int dec)
          {
              if (geneList.size()==0) return "";
            String s="";
            for (int g =0; g<geneList.size(); g++) {
                s=s+sep+n2s.TruncDecimal(geneList.get(g),dec);
            }
            return s;
          }

         /**
           *  Returns string of names of values but not genes needed to specify results of model.
          * <br>Includes input parameters and basic outputs. Can exclude features that are derivable and no genes done here.
          *@param sep separation string
           *@return string with names and values of parameters.
           */
          public static String parameterStringNames(String sep)
          {
            String s="";
            for (int i =0; i<parameterFileList.length; i++) s=s+(i==0?"":sep)+dataName(parameterFileList[i]);
            return s;
          }


          
          /**
           *  Returns short string value of one site variable.
           *@param index of site variable to return.
           *@param dec number of decimal places to keep
           *@return string with name of data variable number value.
           */
          public String toStringShort(int index, int dec)
          {
            return toStringShort(index, dec+3, dec);
          }

          /**
           *  Returns short string value of one site variable.
           *@param index of site variable to return.
           *@param dec number of decimal places to keep
           *@return string with name of data variable number value.
           */
          public String toStringShort(int index, int width, int dec)
          {
            if (isAlpha(index)) return String.format("%"+width+"s", (String) getObject(index));
            if (isDouble(index)) return String.format("%"+(width)+"."+dec+"g", (Double) getObject(index));
            if (isInt(index)) return String.format("%"+width+"d", (Integer) getObject(index));
            return "index "+index+" unknown";
          }
//       /**
//           *  Returns  value of one site variable as an object.
//           *@param index of site variable to return.
//           *@param width of site variable to return.
//           *@param decimal places to return.
//           *@return value of data variable.
//           */
//          public String toStringFormated(int index, int w, int d){
//              Object o = getObject(index);
//              if (isAlpha(index)) {
//                  return String.format("%"+w+"s",(String) o);
//              }
//              if (isInt(index)) {
//                  return String.format("%"+w+"d",(Integer) o);
//              }
//              if (isDouble(index)) {
//                  return String.format("%"+w+"."+d+"g",(Double) o);
//              }
//              return "Index "+index+" unknown";
//          }

//         /**
//           *  Returns short string value of one site variable.
//           *@param index of site variable to return.
//           *@param dec number of decimal places to keep
//           *@return string with name of data variable number value.
//          * @deprecated
//           */
//          public String toShortDoubleString(int index, int dec)
//        {
////            if (!isIndex(index)) return "index "+index+" unknown";
////            if (isAlpha(index)) return toString(index);
////            return NumbersToString.toString(getVariable(index),dec);
//          }

          /**
           *  Returns short string value of one gene variable.
           *@param index gene number.
           *@param dec number of decimal places to keep
           *@return string with name of data variable number value.
           *
           */
          public String geneToShortDoubleString(int index, int dec)
          {
            String s=NumbersToString.toString( getGeneValue(index),dec) ;            
            return s;
          }

 
        /**
           *  Returns  value of one site variable as an object.
           *@param index of site variable to return.
           *@param width of site variable to return.
           *@param decimal places to return.
           *@return value of data variable.
           */
          public String toString(int index){
              Object o = getObject(index);
              if (isAlpha(index)) return (String) o;
              if (isInt(index)) return Integer.toString((Integer) o);
              if (isDouble(index)) return Double.toString((Double) o);
              return "Index "+index+" unknown";
          }

//          /**
//           *
//           *  Returns string value of one site variable.
//           *@param index of site variable to return.
//           *@return string with name of data variable number value.
//           *@deprecated
//           */
//          public String toStringOld(int index)
//        {
//           String s="value unknown";
//            switch(index)
//            {
//                case 0: s=shortName; break;
//                case 1: s=name; break;
//                case 2: s=Double.toString(X); break;
//                case 3: s=Double.toString(Y); break;
//                case 4: s=Double.toString(Z); break;
//                case 5: s=Double.toString(size); break;
//                case 6: s=Double.toString(value); break;
//                case 7: s=Double.toString(weight); break;
//                case 8: s=Integer.toString(weightRank); break;
//                case 9: s=Double.toString(ranking); break;
//                case 10: s=Integer.toString(rankingRank); break;
//                case 11: s=Double.toString(influence); break;
//                case 12: s=Integer.toString(influenceInt); break;
//                case 13: s=Integer.toString(influenceRank); break;
//                case 14: s=Double.toString(displaySize); break;
//                case 15: s=Double.toString(strength); break;
//                case 16: s=Double.toString(strengthIn); break;
//                case 17: s=Double.toString(strengthOut); break;
//                case 18: s=Integer.toString(number); break;
//                case 19: s=Double.toString(strengthOut); break;
//                case 20: s=Integer.toString(rankOverWeightRank); break;
//                case 21: s=Double.toString(cultureMax); break;
//                case 22: s=Integer.toString(cultureSite); break;
//                case 23: s=Double.toString(strengthSquaredIn); break;
//                case 24: s=Double.toString(strengthSquaredOut); break;
//                case 25: s=Double.toString(latitude); break;
//                case 26: s=Double.toString(longitude); break;
//                case 27: s= region; break;
//                case 28: s=Double.toString(inValue); break;
//                case 29: s=Double.toString(inWeight); break;
//                case 30: s=Double.toString(betweenness); break;
//                case 31: s=Integer.toString(betweennessRank); break;
//
//            }
//            return s;
//          }

         /**
           *  Sets the value of a variable from an input string.
           *@param name Site variable name to be set.
           *@param s string of value for variable.
           *@return 0 if OK, -1 iff error
           */
          public int setVariable(String name, String s)
        {
              int res=0;
              try{ 
                  int i = getIndex(name);
                  setVariable(i,s);
              
              } catch (NumberFormatException e) {
                  System.out.println("Problem in Site.setValue: "+e);
                  res=-1;
              } 
              return res;
          }
          
                   /**
           *  Sets the value of a variable from an input string.
           *@param index Site variable number to be set.
           *@param s string of value for variable.
           *@return 0 if OK, -1 if error
           */
          public int setVariable(int index, String s)
        {              int res=0;
              try{
                  switch(index) {
                      case 0: shortName=s; break;
                      case 1: name=s; break;
                      case 2: X=new Double(s); break;
                      case 3: Y=new Double(s); break;
                      case 4: Z=new Double(s); break;
                      case 5: size= new Double(s); break;
                      case 6: value =new Double(s); break;
                      case 7: weight =new Double(s); break;
                      case 8: weightRank =new Integer(s); break;
                      case 9: ranking =new Double(s); break;
                      case 10: rankingRank =new Integer(s); break;
                      case 11: influence=new Double(s); break;
                      case 12: influenceInt=new Integer(s); break;
                      case 13: influenceRank=new Integer(s); break;
                      case 14: displaySize=new Double(s); break;
                      case 15: strength=new Double(s); break;
                      case 16: strengthIn=new Double(s); break;
                      case 17: strengthOut=new Double(s); break;
                      case 18: number=new Integer(s); break;
                      case 19: rankOverWeight = new Double(s); break;
                      case 20: rankOverWeightRank = new Integer(s); break;
                      case 21: cultureMax = new Double(s); break;
                      case 22: cultureSite = new Integer(s); break;
                      case 23: strengthSquaredIn = new Double(s); break;
                      case 24: strengthSquaredOut = new Double(s); break;
                      case 25: latitude = new Double(s); break;
                      case 26: longitude = new Double(s); break;
                      case 27: region=s; break;
                      case 28: inValue = new Double(s); break;
                      case 29: inWeight = new Double(s); break;
                      case 30: betweenness=new Double(s); break;
                      case 31: betweennessRank=new Integer(s); break;
                      case 32: influenceprime=new Double(s); break;
                      case 33: influenceprimeRank=new Integer(s); break;
                      case 34: betweennessprime=new Double(s); break;
                      case 35: betweennessprimeRank=new Integer(s); break;
                      case 36: nbetweenness=new Double(s); break;
                      case 37: nbetweennessRank=new Integer(s); break;
                      case 38: nbetweennessprime=new Double(s); break;
                      case 39: nbetweennessprimeRank=new Integer(s); break;

                  }
              } catch (NumberFormatException e) {
                  System.out.println("Problem in Site.setValue: "+e);
                  res=-1;
              } 
              return res;
          }

    /**  Gets the value of given gene number.
     *@param g Gene number to be set.
     * @return value of gene
     */
    public double getGeneValue(int g) {
        return geneList.get(g);
    }

    /**  Adds a new gene value to the list, initialised to be GUNSET.
     */
    public void addGeneValue() {
        geneList.add(GUNSET);
    }
    /**  Sets the value of given gene number from an input strings.
     *@param g Gene number to be set.
     *@param str string of value for variable.
     */
    public void setGeneValue(int g, String str) {
        geneList.set(g, Double.parseDouble(str));
    }

    /**  Sets the value of given gene number.
     *@param g Gene number to be set.
     *@param value value for gene.
     */
    public void setGeneValue(int g, Double value) {
        geneList.set(g, value);
    }


          
          /**
           *  Returns string of values of all fixed site data variables.
           *@param SEP separator string e.g. tab or space.
           *@return string with of fixed site data values.
           *
           */
          public String fixedDataNameString(String SEP)
        {
              String s=dataName(0); //shortname
              for (int i=1;i<IslandSite.fixedParameterList.length;i++) s=s+SEP+dataName(fixedParameterList[i]);
              return s;
          }

          /**
           *  Returns string of values of all fixed site data variables.
           *@param SEP separator string e.g. tab or space.
           *@return string with of fixed site data values.
           *
           */
          public String fixedDataString(String SEP)
        {
              String s=toString(0); //shortname
              for (int i=1;i<IslandSite.fixedParameterList.length;i++) s=s+SEP+toString(fixedParameterList[i]);
              return s;
          }
            
          
          /**
           *  Returns string of names of  all data variables.
           *@param SEP separator string e.g. tab or space.
           *@return string with name of data variable number value.
           *
           */
          public static String dataNameString(String SEP)
        {
              String s=dataName(0);
              for (int i=1;i<numberVariables;i++) s=s+SEP+dataName(i);
              return s;
          }
          
           /**
           *  Returns string name of one site variable.
           *@param index Site variable number to return.
           *@return string with name of data variable number value.
           */
        public static String dataName(int index)
        {
            if ((index<0) || (index>=parameterNames.length)) return "value unknown";
            return parameterNames[index];
        }
//            switch(index)
//            {
//                case -1: s="Degree"; break;
//                case 0: s="ShortName"; break;
//                case 1: s="Name"; break;
//                case 2: s="XPos"; break;
//                case 3: s="YPos"; break;
//                case 4: s="ZPos"; break;
//                case 5: s="Size"; break;
//                case 6: s="Value"; break;
//                case 7: s="Weight"; break;
//                case 8: s="WeightRank"; break;
//                case 9: s="Ranking"; break;
//                case 10: s="RankingRank"; break;
//                case 11: s="TotInfluenceWeight"; break;
//                case 12: s="Influence"; break;
//                case 13: s="InfluenceRank"; break;                
//                case 14: s="DisplaySize"; break;                
//                case 15: s="Strength"; break;                
//                case 16: s="StrengthIn"; break;                
//                case 17: s="StrengthOut"; break;                
//                case 18: s="Number"; break;                
//                case 19: s="Ranking/Weight"; break;                
//                case 20: s="Ranking/Weight rank"; break;                
//                case 21: s="CultureMax"; break;                
//                case 22: s="CultureSite"; break;
//                case 23: s="Strength Squared In"; break;
//                case 24: s="Strength Squared Out"; break;
//                case 25: s="Latitude"; break;
//                case 26: s="Longitude"; break;
//
//
//            }
       
         /**
           *  Returns number of site variable with given name.
          * <br>Compares the first length characters of input string
           *@param input name of variable being requested
          * @param length number of characters from start to use for comparison
          * @return variable number, -1 if none found, -2 if input too short.
           */
        public int dataNameNumber(String input, int length)
        {
            if (input.length() < length) return -2;
            int res=-1;
            String s=input.substring(0, length);
            for (int v=0; v<numberVariables;v++) 
              {
                if (dataName(v).length() <length) continue; // no match if data name too short
                  if (s.equalsIgnoreCase(dataName(v).substring(0,length)))
                  {
                      res=v;
                      break;
                  }
              }
            return res;
        }
        
          /**
           *  Returns number of site variable whose name equals given string.
           * <br>Exact string ignoring case.
           *@param input name of variable being requested
           * @return index of first matching variable, -1 if none found.
           */
        public static int getIndex(String input)
        {
            for (int v=0; v<numberVariables;v++)
                  if (input.equalsIgnoreCase(dataName(v))) return v;
            return -1;
        }
          /**
           *  Returns number of first site variable whose name starts with given string.
           * <br>Compares the first length characters of input string
           *@param input name of variable being requested
           * @return index of first matching variable, -1 if none found.
           */
        public static int getFirstIndex(String input)
        {
            int res=-1;
            for (int v=0; v<numberVariables;v++)
              {
                  if (dataName(v).length() < input.length() ) continue; // no match if data name too short
                  if (input.equalsIgnoreCase(dataName(v).substring(0,input.length() )))
                  {
                      res=v;
                      break;
                  }
              }
            return res;
        }
       
        /** Tests if latitude and longitude are set.
         *@return true (false) if both latitude AND longituide are (not) set.
     */
    public boolean isLatLongSet()
    {
     if ((latitude==DUNSET) || (longitude == DUNSET)) return false;
     return true;
    }

            /** Tests if X and Y positions are set.
         *@return true (false) if both X AND Y positions are (not) set.
     */
    public boolean isXYSet()
    {
     if ((X==DUNSET) || (Y==DUNSET)) return false;
     return true;
    }

            /** Tests if X,Y and Z positions are set.
         *@return true (false) if ALL of X, Y and Z positions are (not) set.
     */
    public boolean isXYZSet()
    {
     if ((X==DUNSET) || (Y==DUNSET)|| (Z==DUNSET)) return false;
     return true;
    }

            /** Tests if region is set
         *@return true (false) if region is set
     */
    public boolean isRegionSet()
    {
     if (region.equals(AUNSET)) return false;
     return true;
    }

        /**
     *  Returns true if value is a legitimate index.
     * @param index of data variable to test.
     * @return true index is between 0 and <tt>numberVariables</tt>
     */
    static public boolean isIndex(int index) {
        if ((index<0) || (index>=numberVariables) )
            return false;
        return true;
    }
          

    
    /**
     *  Returns true if variable is an integer.
     *@param index of data variable to test.
     * @return true if variable is integer
     */
    static public boolean isInt(int index) {
        if (typeList[index] == INTCHAR) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  Returns true if variable is a double.
     *@param index of data variable to test.
     * @return true if variable is a double
     */
    static public boolean isDouble(int index) {
        if (typeList[index] == DOUBLECHAR) {
            return true;
        } else {
            return false;
        }
    }
            /**
     *  Returns true if variable is numerical (integer or double).
     *@param index of data variable to test.
     * @return true if variable is numerical
     */
    static public boolean isNumerical(int index) {
        if ((typeList[index] == DOUBLECHAR) || (typeList[index] == INTCHAR) ){
            return true;
        } else {
            return false;
        }
    }


    /**
     *  Returns true if variable is a string.
     * @param index of data variable to test.
     * @return true if variable is string
     */
    static public boolean isAlpha(int index) {
        if (typeList[index] == ALPHACHAR) {
            return true;
        } else {
            return false;
        }
    }
          
    /**
     *  Returns true if variable is set.
     * @param s string with start of name of variable.
     * @return true if variable is set
     */
          public boolean isSet(String s) {return isSet(IslandSite.getIndex(s));}

          
    /**
     *  Returns true if variable is set.
     * @param index of data variable to test.
     * @return true if variable is set
     */
          public boolean isSet(int index)
        {
              switch(index)
            {
                case 0: return (shortName.equals(SAUNSET)?false:true); //break;
                case 1: return (name.equals(AUNSET)?false:true); //break;
                case 2: return (DUNSET==X?false:true); //break;
                case 3: return (DUNSET==Y?false:true); //break;
                case 4: return (DUNSET==Z?false:true); //break;
                case 5: return (DUNSET==size?false:true); //break;
                case 6: return (DUNSET==value?false:true); //break;
                case 7: return (DUNSET==weight?false:true); //break;
                case 8: return (IUNSET==weightRank?false:true); //break;
                case 9: return (DUNSET==ranking?false:true); //break;
                case 10: return (IUNSET==rankingRank?false:true); //break;
                case 11: return (DUNSET==influence?false:true); //break;
                case 12: return (IUNSET==influenceInt?false:true); //break; 
                case 13: return (IUNSET==influenceRank?false:true); //break;
                case 14: return (DUNSET==displaySize?false:true); //break;
                case 15: return (DUNSET==strength?false:true); //break;
                case 16: return (DUNSET==strengthIn?false:true); //break;
                case 17: return (DUNSET==strengthOut?false:true); //break;
                case 18: return (IUNSET==number?false:true); //break;
                case 19: return (DUNSET==rankOverWeight?false:true); //break;
                case 20: return (IUNSET==rankOverWeightRank?false:true); //break;
                case 21: return (DUNSET==cultureMax?false:true); //break;
                case 22: return (IUNSET==cultureSite?false:true); //break;
                case 23: return (DUNSET==strengthSquaredIn?false:true); //break;
                case 24: return (DUNSET==strengthSquaredOut?false:true); //break;
                case 25: return (DUNSET==latitude?false:true); //break;
                case 26: return (DUNSET==longitude?false:true); //break;
                case 27: return (region.equals(AUNSET)?false:true); //break;                
                case 28: return (DUNSET==inValue?false:true); //break;
                case 29: return (DUNSET==inValue?false:true); //break;
                case 30: return (DUNSET==betweenness?false:true); //break;
                case 31: return (IUNSET==betweennessRank?false:true); //break;
                case 32: return (DUNSET==influenceprime?false:true); //break;
                case 33: return (IUNSET==influenceprimeRank?false:true); //break;
                case 34: return (DUNSET==betweennessprime?false:true); //break;
                case 35: return (IUNSET==betweennessprimeRank?false:true); //break;
                case 36: return (DUNSET==nbetweenness?false:true); //break;
                case 37: return (IUNSET==nbetweennessRank?false:true); //break;
                case 38: return (DUNSET==nbetweennessprime?false:true); //break;
                case 39: return (IUNSET==nbetweennessprimeRank?false:true); //break;

            }
            return false;
          }
          

        
    }// eo IslandSite class    
