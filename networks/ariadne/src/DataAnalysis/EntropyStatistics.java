/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DataAnalysis;

/**
 * Keeps statistics on a set of mutual information values.
 * <p>Note that one typical use is where the mi is for various partitions of the same set and
 * we are cycling through various partitions of set two. Thus if you want to include
 * @author time
 */
public class EntropyStatistics {

    StatisticalQuantity order;
    StatisticalQuantity entropy;
    StatisticalQuantity entropyOverMaximum;
    String name="";

    public EntropyStatistics(String newName){
        name=newName;
        reset();
    }

    public void reset(){
            order = new StatisticalQuantity();
            entropy = new StatisticalQuantity();
            entropyOverMaximum = new StatisticalQuantity();
            order.setName(name+"Order_n");
            entropy.setName(name+"S");
            entropyOverMaximum.setName(name+"S/ln(n)");
    }

    /**
     * Adds values of all set one quantities from given mutual information measurement.
     * @param mi measurement of mutual information
     */
    public void addOne(MutualInformation mi){
        entropy.add(mi.getEntropyOne());
        order.add(mi.getOrderOne());
        entropyOverMaximum.add(mi.getEntropyOneOverMax());
    }

    /**
     * Adds values of all set two quantities from given mutual information measurement.
     * @param mi measurement of mutual information
     */
    public void addTwo(MutualInformation mi){
        entropy.add(mi.getEntropyTwo());
        order.add(mi.getOrderTwo());
        entropyOverMaximum.add(mi.getEntropyTwoOverMax());
    }


    public StatisticalQuantity getOrder(){return order;}
    public StatisticalQuantity getEntropy(){return entropy;}
    public StatisticalQuantity getEntropyOverMaximum(){return entropyOverMaximum;}

    public String toLabel(String sep){
             String s=getOrder().labelString(sep)+sep+
                      getEntropy().labelString(sep)+sep+
                      getEntropyOverMaximum().labelString(sep);
             return s;
         }
    public String toString(String sep){
             String s=getOrder().toString(sep)+sep+
                      getEntropy().toString(sep)+sep+
                      getEntropyOverMaximum().toString(sep);
             return s;
         }


    public String averagesLabel(String sep){
             String s=getOrder().getName()+sep+
                      getEntropy().getName()+sep+
                      getEntropyOverMaximum().getName();
             return s;
         }
    public String averagesString(String sep){
             String s=getOrder().getAverage()+sep+
                      getEntropy().getAverage()+sep+
                      getEntropyOverMaximum().getAverage();
             return s;
         }


}
