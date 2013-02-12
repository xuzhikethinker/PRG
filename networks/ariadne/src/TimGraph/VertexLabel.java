/*
 * VertexLabel.java
 *
 * Created on 11 December 2006, 17:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TimGraph;

import TimGraph.algorithms.StructuralHoleVertexData;

/**
 * VertexLabel class.
 *
 * <p>Defines attributes of a vertex.
 * Note that name, number and strength are always stored directly in this class
 * and are private, so accessed only through the routiens in thsi class.
 * However position and rank can be references to external instances not created 
 * within this class. All are null and empty until specifically created.
 *
 * @author time
 */
    
    
    public class VertexLabel 
    {
     final static String DEFAULTNAME = "";

     /**
     * String used to indicate unset or errors {@value }
     */
    final static String SUNSET="UNSET";
    
    /**
     * Integer used to indicate unset or errors {@value }
     */
    final static int IUNSET=-918273645;
    
     /**
     * Integer used to indicate unset or errors {@value }
     */
    final static double DUNSET=-9.182736E45;
    

     
     private String name=""; //SUNSET;
     /**
      * Label used to head column of vertex names in files.
      */
     public static final String nameLabel="Name";
     private Integer number; //=IUNSET;
     /**
      * Label used to head column of vertex numbers in files.
      */
     public static final String numberLabel="Number";
     private Double strength; //=DUNSET;
     private Double maxWeight; //=DUNSET;
     private Double cc; //=DUNSET;
     private StructuralHoleVertexData shvd;

     Coordinate position;
     Rank rank;
        
        /**
         * Constructor.  Leaves all values null.
         */
     public VertexLabel()
        {
          //setValues(DEFAULTNAME,0,0.0,new Coordinate());
        }
        /**
         * Sets number using integer number given.
         * Name left as empty string.
         * <p>All other values left null.
         * @param setNumber numbers used to set name and number
         */
        public VertexLabel(Integer setNumber)
        {
            setName(""); //setNumber.toString());
            setNumber(setNumber);
          //setValues(setNumber.toString(),setNumber,0.0,new Coordinate());
          }
        /**
         * Sets name and number using string and integers given.
         * <p>All other values left null.
         * @param setname name of vertex
         * @param setNumber number of vertex
         */
        public VertexLabel(String setname, Integer setNumber)
        {
          setName(setname);
          setNumber(setNumber);
            //setValues(setname,setNumber,0.0,new Coordinate());
        }
        
        /**
         * Sets strength and position using string and integers given.
         * <p>position is just a reference to the argument and not a deep copy.
         * All other values left null.
         * @param setstrength strength
         * @param setposition position
         */
        public VertexLabel(double setstrength, Coordinate setposition )
        {
             setStrength(setstrength);
             position = setposition;
          //setValues(DEFAULTNAME,0,setstrength,setposition);       
        }
        /**
         * Set name and position.
         * <p>Note that the position is a reference to the argument and is not a proper copy.
         * Other values left null.
         * @param setname name
         * @param setposition position
         */
        public VertexLabel(String setname, Coordinate setposition )
        {
          setName(setname);
          position = setposition;
          //setValues(setname,0,0.0,setposition);
        }

        /**
         * Set number, name and position.
         * <p>Note that the position is a reference to the argument and is not a proper copy.
         * Other values left null.
         * @param setNumber numbers used to set name and number
         * @param setname name
         * @param setposition position
         */
        public VertexLabel(Integer setNumber, String setname, Coordinate setposition )
        {
          setName(setname);
          position = setposition;
          //setValues(setname,0,0.0,setposition);
        }

        /**
         * Set name and position.
         * <p>Note that the position is a reference to the argument and is not a proper copy.
         * Other values left null.
         * @param setname name
         * @param setstrength strength
         * @param setposition position
         */
        public VertexLabel(String setname, double setstrength, Coordinate setposition )
        {
          setName(setname);
          setStrength(setstrength);
          position = setposition;  
          //setValues(setname,0,setstrength,setposition);
        }
        
        /**
         * Deep copy of VertexLabel
         * @param old old VertexLabel
         */
        public VertexLabel(VertexLabel old )
        {
          if (old.hasNumber()) setNumber(old.getNumber());
          if (old.hasName()) setName(old.getName());
          if (old.hasStrength()) setStrength(old.getStrength());
          if (old.hasMaxWeight()) setMaxWeight(old.getMaxWeight());
          if (old.hasClusterCoef()) setClusterCoef(old.getClusterCoef());
          if (old.hasStructuralHoleVertexData()) setStructuralHoleVertexData(old.getStructuralHoleVertexData());
          if (old.hasPosition()) position=new Coordinate(old.position);
          if (old.hasRank()) rank=new Rank(old.rank);
            //setValues(oldVertexLabel.name,oldVertexLabel.number,oldVertexLabel.strength, oldVertexLabel.position);
        }
        /**
         * Sets all values.
         * <p>Position and rank are deep copies not copies.
         * @param setname
         * @param setNumber
         * @param setstrength
         * @param setmaxWeight
         * @param shdnew structural hole data
         * @param setposition
         * @param setrank
         */
        public void setValues(String setname, int setNumber,
                double setstrength, double setmaxWeight,
                double setcc,
                StructuralHoleVertexData shvdnew, Coordinate setposition, Rank setrank )
        {
          setName(name);
          setNumber(number);
          setStrength(setstrength);
          setMaxWeight(setmaxWeight);
          setClusterCoef(setcc);
          setStructuralHoleVertexData(shvdnew);
          position= new Coordinate(setposition);
          rank= new Rank(setrank);
        }
        
        
        public void setName(String newName){name=newName;}
        
        public void removeName(){name=DEFAULTNAME;}
        
        
        /**
         * Returns name, with null string if name is unset.
         * @return name, with null string if name is unset.
         */
        public String getName(){if (name.equals(SUNSET)) return ""; else return name;}
        public boolean hasName(){
            if ((name.length()<1) || (name.equals(SUNSET))) return false; 
        else return true;
        }
        
        //public void setNumber(int newNumber){number=newNumber;}
        
        /**
         * Tests to see if number set
         * @return true (false) if number is set (has not been initialised)
         */
        public boolean hasNumber(){return (number==null?false:true);}

        /**
         * Sets number.
         * @param newNumber new value for number
         */
         public void setNumber(int newNumber){
            if (number==null) number= new Integer(newNumber);
            else number=newNumber;
        }

         /**
          * Sets structural hole vertex data.
          * <p>Shallow copy.
          * @param shvdnew
          */
         public void setStructuralHoleVertexData(StructuralHoleVertexData shvdnew){
             if (shvdnew==null) {shvd=null; return;}
             shvd= shvdnew;
         }
         /**
          * Sets structural hole vertex data.
          * <p>Deep copy.
          * @param shvdnew
          */
         public void setStructuralHoleVertexDataDeepCopy(StructuralHoleVertexData shvdnew){
             if (shvdnew==null) {shvd=null; return;}
             shvd= new StructuralHoleVertexData(shvdnew);
         }
        /**
         * Sets position.
         * <p>A deep copy.
         * @param c new coordinate
         */
         public void setPosition(Coordinate c){
             if (position==null) position = new Coordinate();
             position=new Coordinate(c);
        }
        /**
         * Sets position creating a new object if needed.
         * @param x new x coordinate
         * @param y new y coordinate
         */
         public void setPosition(double x, double y){
             setPosition(new Coordinate(x,y));
        }

        /**
         * Sets rank.
         * <p>A deep copy.
         * @param r new rank
         */
         public void setRank(Rank r){
             if (rank==null) rank= new Rank();
             rank=new Rank(r);
        }

        /**
         * Gets value of number.
         * @return current value of number, or VertexLabel.IUNSET if not initialised.
         */public int getNumber(){
            if (number==null) return IUNSET;
            else return number;
        }        
        /**
         * Tests to see if strength set
         * @return true (false) if strength is set (has not been initialised)
         */public boolean hasStrength(){return (strength==null?false:true);}

        /**
         * Sets strength.
         * @param newStrength new value for strength
         */public void setStrength(double newStrength){
            if (strength==null) strength= new Double(newStrength);
            else strength=newStrength;
        }

        /**
         * Gets value of strength.
         * @return current value of strength, or VertexLabel.DUNSET if not initialised.
         */public double getStrength(){
            if (strength==null) return DUNSET;
            else return strength;
        }

        /**
         * Tests to see if cluster coefficient set
         * @return true (false) if cluster coefficient is set (has not been initialised)
         */
         public boolean hasClusterCoef(){return (cc==null?false:true);}

         /**
         * Sets cluster coefficient.
         * @param newcc new value for cluster coefficient
         */public void setClusterCoef(double newcc){
            if (cc==null) cc= new Double(newcc);
            else cc=newcc;
        }

        /**
         * Gets value of cluster coefficient.
         * @return current value of cluster coefficient, or VertexLabel.DUNSET if not initialised.
         */public double getClusterCoef(){
            if (cc==null) return DUNSET;
            else return cc;
        }

        /**
         * Tests to see if maximum weight set
         * @return true (false) if maximum weight is set (has not been initialised)
         */public boolean hasMaxWeight(){return (maxWeight==null?false:true);}

        /**
         * Sets maximum weight.
         * @param newMaxWeight new value for maximum weight
         */public void setMaxWeight(double newMaxWeight){
            if (maxWeight==null) maxWeight= new Double(newMaxWeight);
            else maxWeight=newMaxWeight;
        }

        /**
         * Gets value of maximum weight.
         * @return current value of maximum weight, or VertexLabel.DUNSET if not initialised.
         */public double getMaxWeight(){
            if (maxWeight==null) return DUNSET;
            else return maxWeight;
        }

        /**
         * Gets position
         * @return current value of position
         */public Coordinate getPosition(){
            return position;
        }
        /**
         * Gets Structural Hole Vertex Data.
         * @return current value of StructuralHoleVertexData
         */public StructuralHoleVertexData getStructuralHoleVertexData(){
            return shvd;
        }

        /**
         * Gets rank.
         * @return current value of strength, or VertexLabel.DUNSET if not initialised.
         */public Rank getRank(){
            return rank;
        }

        /**
         * Adds value to current strength
         * @param newStrength value to add to strength
         */
        public void addStrength(double newStrength){
            if (strength==null) strength= new Double(newStrength);
            else strength+=newStrength;
        }


        /**
         * Adds value to current strength
         * @param newStrength value to add to strength
         */
        public void updateMaxWeight(double newMaxWeight){
            if (maxWeight==null) maxWeight= new Double(newMaxWeight);
            else maxWeight=Math.max(maxWeight,newMaxWeight);
        }


         /**
         * Tests to see if rank set
         * @return true (false) if rank is set (has not been initialised)
         */public boolean hasRank(){return (rank==null?false:true);}

         /**
         * Tests to see if  StructuralHoleVertexData set
         * @return true (false) if  StructuralHoleVertexData is set (has not been initialised)
         */
         public boolean hasStructuralHoleVertexData(){return (shvd==null?false:true);}

        /**
         * Tests to see if position set
         * @return true (false) if position is set (has not been initialised)
         */public boolean hasPosition(){return (position==null?false:true);}


         /**
          * Update values of given input VertexLabel with minimum of this and input.
          * @param minLabel this is returned with all values set to be the minimum
          */
         public void setMinimum(VertexLabel minLabel){
             if (minLabel==null) {
                 minLabel= new VertexLabel(this);
                 return;
             }
             if (this.hasName()) if (getName().compareToIgnoreCase(minLabel.getName())<0) minLabel.setName(getName());
             if (this.hasNumber()) if (getNumber()<minLabel.getNumber()) minLabel.setNumber(getNumber());
             if (this.hasStrength()) if (getStrength()<minLabel.getStrength() ) minLabel.setStrength(getStrength());
             if (this.hasMaxWeight()) if (getMaxWeight()<minLabel.getMaxWeight() ) minLabel.setMaxWeight(getMaxWeight());

             if (this.hasClusterCoef()) if (getClusterCoef()<minLabel.getClusterCoef() ) minLabel.setClusterCoef(getClusterCoef());

             if (this.hasStructuralHoleVertexData()) {
                 if (minLabel.hasStructuralHoleVertexData()) minLabel.getStructuralHoleVertexData().setToMinimum(this.getStructuralHoleVertexData());
                 else minLabel.setStructuralHoleVertexData(this.getStructuralHoleVertexData());
                 }
             if (this.hasPosition()) {
                 if (minLabel.hasPosition()) minLabel.getPosition().setToMinimum(this.getPosition());
                 else minLabel.setPosition(this.getPosition());
                 }
             if (this.hasRank()) {
                 if (minLabel.hasRank()) minLabel.getRank().setMinimum(this.getRank());
                 else minLabel.setRank(this.getRank());
             }
         }

         /**
          * Update values of given input VertexLabel with maximum of this and input.
          * @param maxLabel this is returned with all values set to be the maximum
          */
         public void setMaximum(VertexLabel maxLabel){
             if (maxLabel==null) {
                 maxLabel= new VertexLabel(this);
                 return;
             }
             if (this.hasName()) if (getName().compareToIgnoreCase(maxLabel.getName())>0) maxLabel.setName(getName());
             if (this.hasNumber()) if (getNumber()>maxLabel.getNumber()) maxLabel.setNumber(getNumber());
             if (this.hasStrength()) if (getStrength()>maxLabel.getStrength() ) maxLabel.setStrength(getStrength());
             if (this.hasMaxWeight()) if (getMaxWeight()>maxLabel.getMaxWeight() ) maxLabel.setMaxWeight(getMaxWeight());
             if (this.hasClusterCoef()) if (getClusterCoef()>maxLabel.getClusterCoef() ) maxLabel.setClusterCoef(getClusterCoef());
             if (this.hasStructuralHoleVertexData()) {
                 if (maxLabel.hasStructuralHoleVertexData()) maxLabel.getStructuralHoleVertexData().setToMaximum(this.getStructuralHoleVertexData());
                 else maxLabel.setStructuralHoleVertexData(this.getStructuralHoleVertexData());
                 }
             if (this.hasPosition()) {
                 if (maxLabel.hasPosition()) maxLabel.getPosition().setToMaximum(this.getPosition());
                 else maxLabel.setPosition(this.getPosition());
                 }
             if (this.hasRank()) {
                 if (maxLabel.hasRank()) maxLabel.getRank().setMaximum(this.getRank());
                 else maxLabel.setRank(this.getRank());
             }
         }

 


        public String pajekString()
        {   
            String s=  (hasName()?quotedNameString():"") + "  "+(hasPosition()? position.toString2D(" ")+" 0":"");
            return s;
        }

        
        public String pajekString(double minx, double miny, double maxx, double maxy)
        {            
            double scalex = (maxx-minx);
            double scaley = (maxy-miny);
            String s="";
            if ((scalex==0) || (scaley==0) ) s= pajekString();
            else s=  " "+(hasName()?quotedNameString():"")+"  "+(hasPosition()? ((position.x-minx)/scalex)+" "+((position.y-miny)/scaley)+" 0":"");
            return s;
        }

        public String quotedNameString(){
            return "\""+name+"\"";
        }
        
        public String pajekString(Coordinate min, Coordinate max)
        {            
            return pajekString(min.x,min.y,max.x,max.y);
        }

        /**
         * Gives string of specified values in vertex label.
         * <p>Only gives values which have been defined.
         * <p>Use <tt>labelString(sep)</tt> 
         * to get header line for this string.
         */
        public String printString(String sep)
        {            
            String s="";
            if (hasName()) s=s+name+sep;
            if (hasNumber()) s=s+valueString(number)+sep;
            if (hasPosition()) s=s+position.toString(sep)+sep;
            if (hasStrength()) s=s+valueString(strength)+sep;
            if (hasMaxWeight()) s=s+valueString(maxWeight)+sep;
            if (hasClusterCoef()) s=s+valueString(cc)+sep;
            if (hasStructuralHoleVertexData()) s=s+shvd.getVertexDataNoIndexString(sep)+sep;
            if (hasRank()) s=s+rank.printString(5,sep)+sep;
            if (s.length()>0) s=s.substring(0, s.length()-sep.length()); // remove last sep
            return s;
        }

        /**
         * Gives string of specified values in vertex label.
         * <p>Use <tt>labelString</tt> to get header line for this string.
         * @param sep Separation string 
         * @param printName true (false) to (not) print  name
         * @param printNumber true (false) to (not) print number
         * @param printPosition true (false) to (not) print coordinate
         * @param printStrength true (false) to (not) print strength
         * @param printMaxWeight true (false) to (not) print largest weight
         * @param printClusterCoef true (false) to (not) print strength
         * @param printStructuralHoleVertexData true (false) to print structural hole vertex data.
         * @param printRank true (false) to (not) print rank
         * @return string of specified values in vertex label.
         * @see #labelString(java.lang.String, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)
         */
        public String printString(String sep, boolean printName, boolean printNumber, 
                boolean printPosition,
                boolean printStrength, boolean printMaxWeight,
                boolean printClusterCoef,
                boolean printStructuralHoleVertexData, boolean printRank)
        {            
            String s="";
            if (printName) s=s+name+sep;
            if (printNumber) s=s+valueString(number)+sep;
            if (printPosition) s=s+Coordinate.toString(position,sep)+sep;
            if (printStrength) s=s+valueString(strength)+sep;
            if (printMaxWeight) s=s+valueString(maxWeight)+sep;
            if (printClusterCoef) s=s+valueString(cc)+sep;
            if (printStructuralHoleVertexData) s=s+shvd.getVertexDataNoIndexString(sep)+sep;
            if (printRank) s=s+Rank.toString(rank, 5,sep)+sep;
            if (s.length()>0) s=s.substring(0, s.length()-sep.length()); // remove last sep
            return s;
        }
        
        
        
        /**
         * Gives string of specified values in vertex label.
         * <p>Use <tt>LabelString</tt> to get header line for this string.
         * @param sep Separation string 
         */
        public String labelString(String sep)
        {            
          return labelString(sep,hasName(),hasNumber(),hasPosition(),
                  hasStrength(),hasMaxWeight(),
                  hasClusterCoef(),
                  hasStructuralHoleVertexData(), hasRank());
          //labelString(sep, true, true, true, true, true);
        }
        /**
         * Gives string of specified values in vertex label.
         * <p>Use <tt>LabelString</tt> to get header line for this string.
         * @param sep Separation string 
         * @param printName true (false) to (not) print  name
         * @param printNumber true (false) to (not) print number
         * @param printPosition true (false) to (not) print coordinate
         * @param printStrength true (false) to (not) print strength
         * @param printMaxWeight true (false) to (not) print maximum weight 
         * @param printClusterCoef true (false) to (not) print strength
         * @param  printStructuralHoleVertexData  true (false) to (not) print structural hole vertex data
         * @param printRank true (false) to (not) print rank
         * @return string of specified values in vertex label.
         */
        static public String labelString(String sep, boolean printName,
                boolean printNumber, boolean printPosition,
                boolean printStrength, boolean printMaxWeight,
                boolean printClusterCoef,
                boolean printStructuralHoleVertexData,
                boolean printRank)
        {            
            String s="";
            if (printName) s=s+nameLabel+sep;
            if (printNumber) s=s+numberLabel+sep;
            if (printPosition) s=s+Coordinate.labelString(sep)+sep;
            if (printStrength) s=s+"Strength"+sep;
            if (printMaxWeight) s=s+"Max.Weight"+sep;
            if (printClusterCoef) s=s+"C.C."+sep;
            if (printStructuralHoleVertexData) s=s+StructuralHoleVertexData.getVertexDataNoIndexLabel(sep)+sep;
            if (printRank) s=s+Rank.labelString(sep)+sep;
            if (s.length()>0) s=s.substring(0, s.length()-sep.length()); // remove last sep
            return s;
        }
        
    private String valueString(int i){String s= (i==IUNSET)?"Unset":Integer.toString(i); return s;}
    private String valueString(double d){String s= (d==DUNSET)?"Unset":Double.toString(d); return s;}
    private String valueString(Integer i){String s= (i==null)?"Unset":i.toString(); return s;}
    private String valueString(Double d){String s= (d==null)?"Unset":d.toString(); return s;}

        
    }//eo vertexlabel class
