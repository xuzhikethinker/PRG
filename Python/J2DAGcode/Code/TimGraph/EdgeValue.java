/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph;

/**
 * Defines attributes of an edge.
 * <p>weight is a double
 * <br>label is an integer.
 * TODO: Use the constants to indicate when not set.
 * @author time
 */
   public class EdgeValue implements Comparable<EdgeValue>
    {
        /**
         * Constant used to indicate label has not been set. {@value }
         */
     public static final int NOLABEL = -98542182;
         /**
         * Constant used to indicate weight has not been set. {@value }
         */
     public static final double NOWEIGHT = -9854218.2;
     int label;
     double weight;
        
     /**
      * Leaves values equal to unset values.
      * <p>These unset values are defined by constants in this class
      */
        public EdgeValue()
        {
          label=NOLABEL;
          weight=1.0;
        }
        
        /**
         * Leaves weight equal to 1.
         * @param setlabel label 
         */
        public EdgeValue(int setlabel)
        {
          label=setlabel;
          weight=1;
        }
        /**
         * Constructor, leaves label equal to NOLABEL constant..
         * @param setweight weight
         */
        public EdgeValue(double setweight )
        {
          label=NOLABEL;
          weight=setweight;
        }
        
        /**
         * Constructor
         * @param setlabel label 
         * @param setweight weight
         */
        public EdgeValue(int setlabel, double setweight )
        {
          label=setlabel;
          weight=setweight;
        }
        
        
        public EdgeValue(EdgeValue oldedgeweight )
        {
          label=oldedgeweight.label;
          weight=oldedgeweight.weight;
        }
        /**
         * Gets edge weight
         * @return edge weight
         */
        public double getWeight(){return weight;}
        /**
         * Sets edge weight
         * @param w new weight
         */public void setWeight(double w){weight=w;}
        /**
         * Increases edge weight
         * @param dw weight increase
         */public void increaseWeight(double dw){weight+=dw;}
        /**
         * Gets integer label
         * @return integer label
         */public int getLabel(){return label;}
        /**
         * Sets integer Label. 
         * @param l new label
         */public void setLabel(int l){label=l;}
        
        
        /**
         * Tests to see if weight set
         * @return true (false) if weight set (has not been set)
         */public boolean hasWeight(){if (weight==NOWEIGHT) return false; return true;}
       
        /**
         * Tests to see if label set
         * @return true (false) if label set (has not been set)
         */public boolean hasLabel(){if (label==NOLABEL) return false; return true;}

         /**
          * Sets given EdgeValue to minimum values.
          * Compares this and given EdgeValue values taking lowest.
          * @param minew lowest values
          */
         public void setMinimum(EdgeValue minew){
             if (minew==null) {minew = new EdgeValue(this); return;}
             if (this.hasLabel() && label < minew.getLabel() ) minew.setLabel(label);
             if (this.hasWeight() && weight < minew.getWeight() ) minew.setWeight(weight);
         }

        /**
          * Sets given EdgeValue to maximum values.
          * Compares this and given EdgeValue values taking largest.
          * @param maxew largest values
          */
         public void setMaximum(EdgeValue maxew){
             if (maxew==null) {maxew = new EdgeValue(this); return;}
             if (this.hasLabel() && label > maxew.getLabel() ) maxew.setLabel(label);
             if (this.hasWeight() && weight > maxew.getWeight() ) maxew.setWeight(weight);
         }

        /**
         * String of values in EdgeValue
         * @param sep separation string
         * @return string of the values in EdgeValue
         */
        public String toString(String sep){return (hasWeight()?weight:"UNSET")+sep+(hasLabel()?label:"UNSET");}
        
        /**
         * Column header for <tt>toString</tt> output.
         * @param sep separation string
         * @return string of appropriate header for <tt>toString</tt>
         */
        static public String headerString(String sep){return "Weight"+sep+"Label";}
        /**
         * Column header for <tt>toString</tt> output.
         * @param sep separation string
         * @return string of appropriate header for <tt>toString</tt>
         */
        static public String headerString(String sep, EdgeValue testEW){
            if (testEW.hasWeight()){
                if (testEW.hasLabel())return "Weight"+sep+"Label";
                else return "Label";
            }
            return "Weight";
            }

        /**
         * Tests equality of labels first and if equal then uses weights.
         * @param obj Test equality of this object with this Edgeweight.
         * @return true if argument is an EdgeValue of same label and weight, otherwise false
         */
    @Override
    public boolean equals(Object obj){
        try{
            EdgeValue other = (EdgeValue) obj;
            if ((other.label==label) && (other.weight==weight)) return true;
            return false;
        } catch(Exception e){return false;}
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.label;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.weight) ^ (Double.doubleToLongBits(this.weight) >>> 32));
        return hash;
    }
        
    /**
     * Comparison of EdgeWeights
     * <p>If the label of this EdgeValue is less than that of the argument then
     * this one is first and negative value is returned.
     * If labels are equal then if this EdgeValue has a larger weight
     * than that of the argument then
     * this one is first and negative a value is returned.
     * If the label and weights are equal then zero is returned.  Otherwise a positive number
     * is given.
     * @param o Comparison of this object with this Edgeweight.
     * @return 0 if equal, negative if this object comes before the argument.
     */
    public int compareTo(EdgeValue o) {
           EdgeValue other = (EdgeValue) o;
            int diff=label-other.label; 
            if (diff==0) {
                double d=weight-other.weight;
                if (d==0) return 0;
                if (d>0) return -1;
                return +1;
            } 
            return diff;
    }
    
    }
