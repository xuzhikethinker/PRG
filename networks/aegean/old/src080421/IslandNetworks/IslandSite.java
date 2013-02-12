/*
 * IslandSite.java
 *
 * Created on 24 July 2006, 15:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;


/**
 * Defines all the characteristics of an Island Site.
 * @author time
 */
public class IslandSite {

        final double DUNSET = - 76543.2; // some impossible double
        final int IUNSET = - 654321; // some impossible int
        final String AUNSET = "?!!!!!";
        final int SSDEFAULTLENGTH =3; // default length for short strings
        final String SAUNSET = AUNSET.substring(0, SSDEFAULTLENGTH);;
        private int numberVariables=28; // now set by parameterNames.length
        // Variables are counted from 0 upwards so last is numbered (numberVariables-1)
        String name=AUNSET; //0
        String shortName=SAUNSET ; //1
        double X=DUNSET; //2
        double Y=DUNSET; //3
        double Z=DUNSET; //4
        double size=DUNSET; //5
        double value=DUNSET; //6
        private double weight=DUNSET; //7
        int weightRank =IUNSET; //8
        double ranking=DUNSET; //9 
        int rankingRank=IUNSET; //10
        double totalInfluenceWeight;//11
        int influence=IUNSET; //12
        int influenceRank=IUNSET;  //13
        double displaySize=IUNSET; //14
        double strength=IUNSET; //15
        double strengthIn=IUNSET; //16
        double strengthOut=IUNSET; //17
        int number=IUNSET; //18 number not set should be positive or zero
        double rankOverWeight = 0; //19
        int  rankOverWeightRank =IUNSET; //20
        double cultureMax =IUNSET; //21 value of largest cultural influence
        int cultureSite = IUNSET; //22 site  of largest cultural influence
        double strengthSquaredIn=IUNSET; //23
        double strengthSquaredOut=IUNSET; //24
        double latitude=DUNSET; //25
        double longitude=DUNSET; //26
        String region=AUNSET; //27
        

        // indicate which of these values are integers, doubles or strings (alphabetic)
        final int [] intList = {8,10,12,13,18,20,22};
        final int [] doubleList = {2,3,4,5,6,7,9,11,14,15,16,17,19,21,23,24,25,26};
        final int [] stringList = {0,1,27}; 
        // list parameters in order in which we wich to put them in a file
        final int [] parameterFileList = {18,1,2,27,25,26,3,4,5,6};
        final int [] fixedParameterList = {1,2,3,4,5,18,25,26,27};
    
        final String[] parameterNames =
            {"ShortName", "Name", "XPos", "YPos", "ZPos", "Size", "Value",
        "Weight", "WeightRank", "Ranking", "RankingRank",
        "TotInfluenceWeight", "Influence", "InfluenceRank", "DisplaySize",
        "Strength", "StrengthIn", "StrengthOut",
        "Number", "Ranking/Weight", "Ranking/Weight rank", "CultureMax", "CultureSite",
        "StrengthInSquared", "StrengthOutSquared", "Latitude", "Longitude", "Region"};

        

/** Creates a new instance of IslandSite */
    public IslandSite() {
    numberVariables = parameterNames.length;
    }
    
        
        /* IslandSite Constructor of given site number.
         *@param siteNumber the number of the site
         */
        public IslandSite(int siteNumber)
          {
            number = siteNumber;
            name=Integer.toString(siteNumber);
            numberVariables = parameterNames.length;
            //shortName=name;
        }
        
        /* IslandSite Constructor  Deep copies site s.
         *@param s the site to be copied.
         */
          public IslandSite(IslandSite s)
          {
//            numberVariables=s.numberVariables;
            name=s.name;
            shortName=s.shortName;
            number=s.number;
            X=s.X;
            Y=s.Y;
            Z=s.Z;
            
        size=s.size;
        value=s.value;
        weight=s.weight; // weight = pure site size*value
        weightRank = s.weightRank;
        ranking=s.ranking;
        rankingRank=s.rankingRank;
        totalInfluenceWeight=s.totalInfluenceWeight;
        influence=s.influence; 
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


              
          }
        
          /**
           *  Returns string value all data variables.
           *@param SEP separator string e.g. tab or space.
           *@return string with name of data variable number value.
           *
           */
          public String toString(String SEP)
        {
              String s="";
              for (int i=0;i<numberVariables;i++) s=s+toString(i)+SEP;
              return s;
          }
          
          
          /**
           *  Gets strength of site variable.
           *@param nSites number of sites
           *@param edgeValue values of edges
           *@return The strength or -1.0 if not possible.
           *
           */
//          public double getStrengthIn(int nSites, double [][] edgeValue)
//        {
//              strengthIn =0; 
//              if ((number<0) || (number>=nSites)) return -1.0;
//              for (int j=0; j<nSites; j++) strengthIn+=edgeValue[number][j];
//              strengthIn*=getWeight();
//              return strengthIn;
//          }

          /**
           *  Sets value of weight.
           *@return The weight = value*size.
           *
           */
          public double setWeight()
        {
              weight=value*size;
              return weight;
          }

          /**
           *  Sets name of site.
           *@param s name of site.
           *@return name.
           *
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
           *
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
           *  Returns true if variable is an integer.
           *@param index of data variable to test.
         * @return true if variable is integer
           */
          public boolean isInt(int index)
        {
              for (int i =0; i<intList.length; i++) if (intList[i]==index) return true;
              return false;
          }

                 /**
           *  Returns true if variable is a double.
           *@param index of data variable to test.
         * @return true if variable is a double
           */
          public boolean isDouble(int index)
        {
              for (int i =0; i<doubleList.length; i++) if (doubleList[i]==index) return true;
              return false;
          }

        /**
           *  Returns true if variable is a string.
           *@param index of data variable to test.
         * @return true if variable is string
           */
          public boolean isAlpha(int index)
        {
              for (int i =0; i<stringList.length; i++) if (stringList[i]==index) return true;
              return false;
          }
          
          /**
           *  Returns true if variable is set.
           *@param index of data variable to test.
         * @return true if variable is set
           */
          public boolean isSet(int index)
        {
           boolean res = false;
            switch(index)
            {
                case 0: return (SAUNSET==shortName?false:true); //break;
                case 1: return (AUNSET==name?false:true); //break;
                case 2: return (DUNSET==X?false:true); //break;
                case 3: return (DUNSET==Y?false:true); //break;
                case 4: return (DUNSET==Z?false:true); //break;
                case 5: return (DUNSET==size?false:true); //break;
                case 6: return (DUNSET==value?false:true); //break;
                case 7: return (DUNSET==weight?false:true); //break;
                case 8: return (IUNSET==weightRank?false:true); //break;
                case 9: return (DUNSET==ranking?false:true); //break;
                case 10: return (IUNSET==rankingRank?false:true); //break;
                case 11: return (DUNSET==totalInfluenceWeight?false:true); //break;
                case 12: return (IUNSET==influence?false:true); //break; 
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
                case 27: return (AUNSET==region?false:true); //break;


                
            }
            return res;
          }
           
          /**
           *  Sets value of one integer site variable.
           * Returns value if an integer is slected 
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
                  case 13: influenceRank=value; break;
                  case 18: number=value; break;
                  case 20: rankOverWeightRank=value; break;
                  case 22: cultureSite = value; break;
        
        
                  default: res=-97531;
              }         
              return res;
          }
/**
           *  Gets value of one integer site variable.
           *@return The weight = value*size.
           *
           */
          public double getWeight()
        {
              weight=value*size;
              return weight;
          }

          
          /**
           *  Returns number of variables stored.
           *@return  number of variables stored.
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
           *  Returns name as string.
           *@return name value
           */
          public String getName() { return name; }
          
          
          /**
           *  Returns string value of one site variable.
           *@param index of site variable to return.
           *@return value of data variable.
           *
           */
          // might be better implemented using object and casting using isInt etc.
          public double getValue(int index)
        {
            double s=-247.0;
            switch(index)
            {
                case 0: s=shortName.codePointAt(0); break;
                case 1: s=name.codePointAt(0); break;
                case 2: s=X; break;
                case 3: s=Y; break;
                case 4: s=Z; break;
                case 5: s=size; break;
                case 6: s=value; break;
                case 7: s=weight; break;
                case 8: s=weightRank; break;
                case 9: s=ranking; break;
                case 10: s=rankingRank; break;
                case 11: s=totalInfluenceWeight; break;
                case 12: s=influence; break; 
                case 13: s=influenceRank; break;
                case 14: s=displaySize; break;
                case 15: s=strength; break;
                case 16: s=strengthIn; break;
                case 17: s=strengthOut; break;
                case 18: s=number; break;
                case 19: s=rankOverWeight; break;
                case 20: s=rankOverWeightRank; break;
                case 21: s=cultureMax; break;
                case 22: s=cultureSite; break;
                case 23: s=strengthSquaredIn; break;
                case 24: s=strengthSquaredOut; break;
                case 25: s=latitude; break;
                case 26: s=longitude; break;
                case 27: s=region.codePointAt(0); break;


                
            }
            return s;
          }

          
         /**
           *  Returns string of values needed to specify results of model
          * <br>Icludes input parameters and basic outputs. Can exclude features hat are derivable.
          *@param sep separation string
           *@param dec number of decimal places to keep
           *@return string with names and values of parameters.
           */
          public String parameterStringValues(String sep, int dec)
          {
            String s="";
            for (int i =0; i<this.parameterFileList.length; i++) {
                s=s+(i==0?"":sep);
                // don'tt shorten long name
                int p = parameterFileList[i];
                if (p==1) s=s+toString(p); 
                else s=s+toShortString(parameterFileList[i],dec);
            }
            return s;
          }

         /**
           *  Returns string of names of values needed to specify results of model
          * <br>Icludes input parameters and basic outputs. Can exclude features that are derivable.
          *@param sep separation string
           *@return string with names and values of parameters.
           */
          public String parameterStringNames(String sep)
          {
            String s="";
            for (int i =0; i<this.parameterFileList.length; i++) s=s+(i==0?"":sep)+dataName(parameterFileList[i]);
            return s;
          }

          
          /**
           * 
           *  Returns short string value of one site variable.
           *@param index of site variable to return.
          *@param dec number of decimal places to keep
           *@return string with name of data variable number value.
           *
           */
          public String toShortString(int index, int dec)
        {
            String s="value unknown";
            if (isAlpha(index)) 
            {
                s=toString(index);
                if (s.length() >dec) s=s.substring(0, dec);
            }
            else if (isDouble(index)) s = TruncDecString( getValue(index),dec) ;
            else s= toString(index); // leave integers unchanged
            return s;
          }

                   /**
           * 
           *  Returns short string value of one site variable.
           *@param index of site variable to return.
          *@param dec number of decimal places to keep
           *@return string with name of data variable number value.
           *
           */
          public String toShortDoubleString(int index, int dec)
        {
            String s="value unknown";
            if (isAlpha(index)) 
            {
                s=toString(index);
            }
            else s = TruncDecString( getValue(index),dec) ;            
            return s;
          }

          /**
           * 
           *  Returns string value of one site variable.
           *@param index of site variable to return.
           *@return string with name of data variable number value.
           *
           */
          public String toString(int index)
        {
           String s="value unknown";
            switch(index)
            {
                case 0: s=shortName; break;
                case 1: s=name; break;
                case 2: s=Double.toString(X); break;
                case 3: s=Double.toString(Y); break;
                case 4: s=Double.toString(Z); break;
                case 5: s=Double.toString(size); break;
                case 6: s=Double.toString(value); break;
                case 7: s=Double.toString(weight); break;
                case 8: s=Integer.toString(weightRank); break;
                case 9: s=Double.toString(ranking); break;
                case 10: s=Integer.toString(rankingRank); break;
                case 11: s=Double.toString(totalInfluenceWeight); break;
                case 12: s=Integer.toString(influence); break; 
                case 13: s=Integer.toString(influenceRank); break;
                case 14: s=Double.toString(displaySize); break;
                case 15: s=Double.toString(strength); break;
                case 16: s=Double.toString(strengthIn); break;
                case 17: s=Double.toString(strengthOut); break;
                case 18: s=Integer.toString(number); break;
                case 19: s=Double.toString(strengthOut); break;
                case 20: s=Integer.toString(rankOverWeightRank); break;
                case 21: s=Double.toString(cultureMax); break;
                case 22: s=Integer.toString(cultureSite); break;
                case 23: s=Double.toString(strengthSquaredIn); break;
                case 24: s=Double.toString(strengthSquaredOut); break;
                case 25: s=Double.toString(latitude); break;
                case 26: s=Double.toString(longitude); break;
                case 27: s= region; break;

            }
            return s;
          }

         /**
           *  Sets the value of a variable from an input string.
           *@param name Site variable name to be set.
           *@param s string of value for variable.
           *@return 0 if OK, -1 iff error
           */
          public int setValue(String name, String s)
        {
              int res=0;
              try{ 
                  int i = getIndex(name);
                  setValue(i,s);
              
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
          public int setValue(int index, String s)
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
                      case 11: totalInfluenceWeight=new Double(s); break;
                      case 12: influence=new Integer(s); break;
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

                  }
              } catch (NumberFormatException e) {
                  System.out.println("Problem in Site.setValue: "+e);
                  res=-1;
              } 
              return res;
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
              for (int i=1;i<this.fixedParameterList.length;i++) s=s+SEP+dataName(fixedParameterList[i]);
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
              for (int i=1;i<this.fixedParameterList.length;i++) s=s+SEP+toString(fixedParameterList[i]);
              return s;
          }
            
          
          /**
           *  Returns string of names of  all data variables.
           *@param SEP separator string e.g. tab or space.
           *@return string with name of data variable number value.
           *
           */
          public String dataNameString(String SEP)
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
        public String dataName(int index)
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
           *  Returns number of site variable with given name.
          * <br>Compares the first length characters of input string
           *@param input name of variable being requested
          * @return variable number, -1 if none found.
           */
        public int getIndex(String input)
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
     if ((latitude==this.DUNSET) || (longitude == DUNSET)) return false;
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

        
        
   /** Truncates a double to number of decimal points.
     * @param value has tractional part truncated
     * @param dec number of decimal to retain
     */
    public String TruncDecString(double value, int dec)
    {
        
      double shift = Math.pow(10,dec);
      Double d =( ( (double) ((int) (value*shift+0.5)))/shift) ;
      return d.toString();
    }

//    /** Truncates a double to number of decimal points.
//     * @param value has tractional part truncated
//     * @param sigFig number of significant figures
//     */
//    public String toSigFigString(double value, int sigFig)
//    {
//        
//      double s=Math.pow(10,Math.floor(Math.log10(value)));
//      double shift = Math.pow(10,dec);
//      Double d =( ( (double) ((int) (value*shift+0.5)))/shift) ;
//      return d.toString();
//    }

        
    }// eo IslandSite class    
