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
public class MutualInformationStatistics {

    StatisticalQuantity mutualInformation;
    StatisticalQuantity jointEntropy;
    StatisticalQuantity redundancyNormalised;
    StatisticalQuantity universalMetric;
    StatisticalQuantity universalMetricNormalised;
    
    public MutualInformationStatistics(){
        reset();
    }

    public void reset(){
            mutualInformation = new StatisticalQuantity();
            jointEntropy = new StatisticalQuantity();
            redundancyNormalised = new StatisticalQuantity();
            universalMetric = new StatisticalQuantity();
            universalMetricNormalised = new StatisticalQuantity();

            mutualInformation.setName("I(X,Y)");
            jointEntropy.setName("H(X,Y)");
            redundancyNormalised.setName("R(X,Y)/R_max");
            universalMetric.setName("d(x,y)");
            universalMetricNormalised.setName("d(x,y)/H(x,y)");

    }

    /**
     * Adds values of all quantities from given mutual information measurement.
     * @param mi measurement of mutual information
     */
    public void add(MutualInformation mi){
        mutualInformation.add(mi.getMutualInformation());
        jointEntropy.add(mi.getJointEntropy());
        redundancyNormalised.add(mi.getRedundancyNormalised());
        universalMetric.add(mi.getUniversalMetric());
        universalMetricNormalised.add(mi.getUniversalMetricNormalised());
        
    }


    public StatisticalQuantity getMutualInformation(){return mutualInformation;}
    public StatisticalQuantity getJointEntropy(){return jointEntropy;}
    public StatisticalQuantity getRedundancyNormalised(){return redundancyNormalised;}
    public StatisticalQuantity getUniversalMetric(){return universalMetric;}
    public StatisticalQuantity getUniversalMetricNormalised(){return universalMetricNormalised;}
    
    public String toLabel(String sep){
             String s=getMutualInformation().labelString(sep)+sep+
                      getJointEntropy().labelString(sep)+sep+
                      getRedundancyNormalised().labelString(sep)+sep+
                      getUniversalMetric().labelString(sep)+sep+
                      getUniversalMetricNormalised().labelString(sep);
             return s;
         }
    public String toString(String sep){
             String s=getMutualInformation().toString(sep)+sep+
                      getJointEntropy().toString(sep)+sep+
                      getRedundancyNormalised().toString(sep)+sep+
                      getUniversalMetric().toString(sep)+sep+
                      getUniversalMetricNormalised().toString(sep);
             return s;
         }
    public String averagesLabel(String sep){
             String s=getMutualInformation().getName()+sep+
                      getJointEntropy().getName()+sep+
                      getRedundancyNormalised().getName()+sep+
                      getUniversalMetric().getName()+sep+
                      getUniversalMetricNormalised().getName();
             return s;
         }
    public String averagesString(String sep){
             String s=getMutualInformation().getAverage()+sep+
                      getJointEntropy().getAverage()+sep+
                      getRedundancyNormalised().getAverage()+sep+
                      getUniversalMetric().getAverage()+sep+
                      getUniversalMetricNormalised().getAverage();
             return s;
         }


}
