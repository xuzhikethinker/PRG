/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DataAnalysis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Calculates Mutual Information and related properties.
 * @author time
 * @see <a href="http://en.wikipedia.org/wiki/Mutual_information">Wikipedia Mutual Information page</a>
 * <br> <a href="http://www.scholarpedia.org/article/Mutual_information">Scholarpedia Mutual Information page</a>
 */
public class MutualInformation {

    /**
     * Used to indicate value not yet calculated.
     */
    static final double DUNSET = -97538642;

    /**
     * Name of set one
     */
    private String name1="Set 1";
    /**
     * Name of set two
     */
    private String name2="Set 2";

    /**
     * Marginal probability of set 1.
     */
         double [] p1;

         /**
          * Order of set one
          */
         int n1;

         /**
          * Names of elements of set 1
          */
         String [] elementNames1;
         
    /**
     * Marginal probability of set 2.
     */
         double [] p2;

         /**
          * Order of set two
          */
         int n2;

         /**
          * Names of elements of set 2
          */
         String [] elementNames2;

         /**
          *  Joint probability distribution.
          */
         double [][] pjd;
         
         /**
          * Mutual information of distributions, I(X,Y)
          */
         private double mutualInformation = DUNSET;

         /**
          * Entropy associated with set one probability, H(X).
          */
         private double entropyOne = DUNSET;

         /**
          * Entropy associated with set two probability, H(Y).
          */
         private double entropyTwo = DUNSET;

         /**
          * Tolerance used for some tests.
          */
         public static final double TOLERANCE = 1e-6;


         /**
          * Constructor.
          * <p>Probabilites are insitialised to zero.
          * @param input n1 size of set 1
          * @param input n2 size of set 2
          */
         public MutualInformation(int inputn1, int inputn2)
         {
             initialise(inputn1, inputn2);
         }

         /**
          * Constructor.
          * <p>Probabilites are initialised to zero.
          * @param input n1 size of set 1
          * @param input n2 size of set 2
          * @param nameOne name of first set
          * @param nameTwo name of second set
          */
         public MutualInformation(int inputn1, int inputn2, String nameOne, String nameTwo)
         {
             initialise(inputn1, inputn2);
             setNames(nameOne, nameTwo);
         }

         /**
          * Constructor.
          * <p>Probabilites and Element names are NOT copied. Fundamental
          * quantities are copied.
          * @param mi MutualInformation to deep copy
          * @param elementCopy (IGNORED) true if want to copy any element names found
          * @param probCopy (IGNORED) true if want to copy any proabilites found
          */
         public MutualInformation(MutualInformation mi, boolean elementCopy, boolean probCopy)
         {
             if (probCopy)initialise(mi.getOrderOne(), mi.getOrderTwo());
             else {n1=mi.getOrderOne(); n2=mi.getOrderTwo();}
             setNames(mi.getName1(), mi.getName2());
             entropyOne=mi.getEntropyOne();
             entropyTwo=mi.getEntropyTwo();
             mutualInformation=mi.getMutualInformation();
         }

         /**
          * Initialise arrays and set values to zero.
          * @param inputn1 size of set 1
          * @param inputn2 size of set 2
          */
         private void initialise(int inputn1, int inputn2){
            n1=inputn1;
            n2=inputn2;
            p1 = new double [n1];
            p2 = new double [n2];
            // probability joint distribution
            pjd = new double [n1][n2];
         }
         

         /**
          * Sets the value of the marginal probabilty of finding one of the set one elements.
          * <p>No tests on inputs.
          * @param i1 index of set one element
          * @param v1 value of P_1(i1)
          */
         public void setMarginalProbabilityOneQuick(int i1, double v1){
             p1[i1]=v1;
         }
         /**
          * Sets the value of the marginal probabilty of finding one of the set one elements.
          * <p>No tests on inputs.
          * @param i2 index of set two element
          * @param v2 value of P_2(i2)
          */
         public void setMarginalProbabilityTwoQuick(int i2, double v2){
             p2[i2]=v2;
         }
         /**
          * Sets the value of the marginal probabilty of finding one of the set one elements.
          * <p>No tests on inputs.
          * @param i1 index of set one element
          * @param i2 index of set two element
          * @param v value of P(i1,i2)
          */
         public void setJointProbabilityQuick(int i1, int i2, double v){
             pjd[i1][i2]=v;
         }
         /**
          * Increases the value of the marginal probabilty of finding one of the set one elements.
          * <p>No tests on inputs.
          * @param i1 index of set one element
          * @param v1 value to increase P_1(i1)
          */
         public void increaseMarginalProbabilityOneQuick(int i1, double v1){
             p1[i1]+=v1;
         }
         /**
          * Increases the value of the marginal probabilty of finding one of the set one elements.
          * <p>No tests on inputs.
          * @param i2 index of set two element
          * @param v2 value to increase  P_2(i2)
          */
         public void increaseMarginalProbabilityTwoQuick(int i2, double v2){
             p2[i2]+=v2;
         }
         /**
          * Increases the value of the joint probabilty of finding an element in two given sets.
          * <p>No tests on inputs.
          * @param i1 index of set one element
          * @param i2 index of set two element
          * @param v value to increase of P(i1,i2)
          */
         public void increaseJointProbabilityQuick(int i1, int i2, double v){
             pjd[i1][i2]+=v;
         }
         /**
          * Gets the value of the joint probability of finding an element in two given sets.
          * <p>No tests on inputs.
          * @param i1 index of set one element
          * @param i2 index of set two element
          * @return value of P(i1,i2)
          */
         public double getJointProbabilityQuick(int i1, int i2){
             return pjd[i1][i2];
         }
         /**
          * Order of set one.
          * @return Order of set one.
          */
         public int getOrderOne(){return n1;}
         /**
          * Order of set two.
          * @return Order of set two.
          */
         public int getOrderTwo(){return n2;}
         /**
          * Sets names of sets
          * @param n1 name of first set
          * @param n2 name of second set
          */
         public void setNames(String n1, String n2){
             name1=n1;
             name2=n2;
         }
        /**
          * Gets name of set one.
          * @return name1
          */
         public String getName1(){
             return name1;
         }

        /**
          * Gets name of set two.
          * @return name2
          */
         public String getName2(){
             return name2;
         }

         /**
          * Sets names of the elements of set one.
          * @param nnn list of names for set one elements
          * @return true if OK, false if not
          */
         public boolean setElementNames1(String [] nnn){
             if (nnn.length!=n1) {
                 System.err.println("*** element names of set one not set since lengths differ "+nnn.length+" != "+n1);
                 return false;
             }
             elementNames1 = new String[n1];
             for (int e=0; e<n1; e++) elementNames1[e]=nnn[e];
             return true;
         }
         /**
          * Sets names of the elements of set two.
          * @param nnn list of names for set two elements
          * @return true if OK, false if not
          */
         public boolean setElementNames2(String [] nnn){
             if (nnn.length!=n2) {
                 System.err.println("*** element names of set two not set since lengths differ "+nnn.length+" != "+n2);
                 return false;
             }
             elementNames2 = new String[n2];
             for (int e=0; e<n2; e++) elementNames2[e]=nnn[e];
             return true;
         }
         /**
          * Returns value of mutual information.
          * <p>Probabilities must have been set.  
          * Calculates if value not already set.
          * @return value of mutual information
          */
         public double getMutualInformation(){
             if (mutualInformation==DUNSET) calculate();
             return mutualInformation;
         }

         /**
          * Returns value of entropy of set one, H(X).
          * <p>All probabilities must have been set.
          * Calculates if value not already set.
          * @return value of of entropy of set one, H(X).
          */
         public double getEntropyOne(){
             if (this.entropyOne==DUNSET) calculate();
             return entropyOne;
         }

         /**
          * Returns value of entropy of set two, H(Y).
          * <p>All probabilities must have been set.
          * Calculates if value not already set.
          * @return value of of entropy of set two, H(Y).
          */
         public double getEntropyTwo(){
             if (this.entropyTwo==DUNSET) calculate();
             return entropyTwo;
         }

         /**
          * Returns value of entropy of set one divided by its maximum value, H(X)/|X|.
          * <p>All probabilities must have been set.
          * Calculates if value not already set.
          * @return value of of entropy of set one divided by its maximum value, H(X)/|X|.
          */
         public double getEntropyOneOverMax(){
             if (this.entropyOne==DUNSET) calculate();
             return entropyOne/getEntropyOneMaximum();
         }

         /**
          * Returns value of entropy of set two divided by its maximum value, H(Y)/|Y|.
          * <p>All probabilities must have been set.
          * Calculates if value not already set.
          * @return value of of entropy of set two divided by its maximum value, H(Y)/|Y|.
          */
         public double getEntropyTwoOverMax(){
             if (this.entropyTwo==DUNSET) calculate();
             return entropyTwo/getEntropyTwoMaximum();
         }


         /**
          * Returns maximum value of entropy of set one, ln(|X|).
          * <p>This assumes that each element of X has an equal probability
          * of being chosen, so that p_x=1/|X|
          * @return maximum value of entropy of set one, ln(|X|).
          */
         public double getEntropyOneMaximum(){
             if (n1>0) return Math.log(n1);
             return n1;
         }

         /**
          * Returns maximum value of entropy of set two, ln(|Y|).
          * <p>This assumes that each element of Y has an equal probability
          * of being chosen.
          * @return maximum value of entropy of set two, ln(|Y|).
          */
         public double getEntropyTwoMaximum(){
             if (n2>0) return Math.log(n2);
             return n2;
         }

         /**
          * Returns value of entropy of set two, H(X,Y).
          * <p>Joint Entropy H(X,Y) = H(X)+H(Y)-I(X,Y)
          * <p>All probabilities must have been set.  
          * Calculates if value not already set.
          * @return value of joint entropy, H(X,Y) = H(X)+H(Y)-I(X,Y).
          */
         public double getJointEntropy(){
             if (mutualInformation==DUNSET) calculate();
             return entropyOne+entropyTwo-mutualInformation;
         }

         /**
          * Returns value of conditional entropy of set one, given set two result, H(X|Y).
          * <p>Conditional entropy of set one H(X|Y) = H(X)-I(X,Y)
          * <p>All probabilities must have been set.  
          * Calculates if value not already set.
          * @return value of conditional entropy of set one, given set two result, H(X|Y).
          */
         public double getConditionalEntropyOne(){
             if (mutualInformation==DUNSET) calculate();
             return entropyOne-mutualInformation;
         }

         /**
          * Returns value of conditional entropy of set two, given set two result, H(Y|X).
          * <p>Conditional entropy of set one H(Y|X) = H(Y)-I(X,Y)
          * <p>All probabilities must have been set.  
          * Calculates if value not already set.
          * @return value of conditional entropy of set two, given set one result, H(Y|X).
          */
         public double getConditionalEntropyTwo(){
             if (mutualInformation==DUNSET) calculate();
             return entropyTwo-mutualInformation;
         }

         /**
          * Returns value of a universal metric d(X,Y).
          * <p>Here d(X,Y) = H(X,Y)-I(X,Y) = H(X)+H(Y)-2I(X,Y).
          * <p>All probabilities must have been set.  
          * Calculates mutual information from these if value not already set.
          * @return value of a universal metric d(X,Y) = H(X)+H(Y)-2I(X,Y)
          */
         public double getUniversalMetric(){
             if (mutualInformation==DUNSET) calculate();
             return entropyOne+entropyTwo-2*mutualInformation;
         }
         /**
          * Returns value of a normalised universal metric  d(X,Y)/H(X,Y).
          * <p>d(X,Y)/H(X,Y) = H(X)+H(Y)-2I(X,Y)
          * <p>All probabilities must have been set.  
          * Calculates mutual information from these if value not already set.
          * @return value of a normalised universal metric  I(x,y)-H(x,y))/H(X,Y)
          */
         public double getUniversalMetricNormalised(){
             if (mutualInformation==DUNSET) calculate();
             return getUniversalMetric()/getJointEntropy();
         }
         
         /**
          * Returns value of Redundancy R(X,Y) 
          * <p>R(X,Y) =  I(X,Y)/(H(X)+H(Y)).
          * <p>Value of 0 when two variables are independent. 
          * Value of R_max = (H(X)+H(Y))/min(H(X),H(Y))
          * one when knowledge of one (possibly both)
          * completely determines the other variable.
          * Note that metric d(X,Y) = 1- 2R(X,Y).
          * <p>All probabilities must have been set.  
          * Calculates mutual information from these if value not already set.
          * @return value of Redundancy  R(X,Y) =  I(X,Y)/(H(X)+H(Y))
          */
         public double getRedundancy(){
             if (mutualInformation==DUNSET) calculate();
             return mutualInformation/(entropyOne+entropyTwo);
         }
         
         /**
          * Returns value of normalised Redundancy R(X,Y)/R_max 
          * <p>0 &lt;= R(X,Y)/R_max =  I(X,Y)/min(H(X),H(Y)) &lt;= 1.
          * <p>Value of 0 when two variables are independent. 
          * Value of one when knowledge of one (possibly both) 
          * completely determines the other variable.
          * <p>All probabilities must have been set.  
          * Calculates mutual information from these if value not already set.
          * @return value of normalised Redundancy R(X,Y)/R_max =  I(X,Y)/min(H(X),H(Y)).
          */
         public double getRedundancyNormalised(){
             if (mutualInformation==DUNSET) calculate();
             return mutualInformation/Math.min(entropyOne,entropyTwo);
         }
         
         

          /**
          * Calculates values of entropies, mutual information, etc.
          * <p>Probabilities must have been set.
          * @return value of mutual information
          */
         public double calculate(){
            try{
                mutualInformation=0;
                entropyOne=0;
                entropyTwo=0;
                double jp=-1;
                double p1v=-1;
                double p2v=-1;
                for (int i1=0; i1<n1; i1++){
                    p1v=p1[i1];
                    if (p1v>TOLERANCE) entropyOne-=p1v*Math.log(p1v);
                    for (int i2=0; i2<n2; i2++){
                        p2v=p2[i2];
                        if ((i1==0) &&(p2v>TOLERANCE)) entropyTwo-=p2v*Math.log(p2v);
                        jp=pjd[i1][i2]; 
                        if (jp<TOLERANCE) continue;
                        mutualInformation+=pjd[i1][i2]*Math.log(pjd[i1][i2]/(p1v*p2v));
                    }
                }
            } catch(RuntimeException e){
                System.err.println("calculateMutualInformation failed, error "+e);
                entropyOne=DUNSET;
                entropyTwo=DUNSET;
                mutualInformation=DUNSET;                
            }
            return mutualInformation;
         }

         /**
          * Checks normalisation of probabilites.
          * <p>Uses constant <tt>MutualInformation.TOLERANCE</tt> as tolerance
          * @return 0 if OK, if bit 2^n set then problem with p1 (n=0), p2 (n=1) or joint probability (n=2).
          */
         public int checkNormalisations(){
             return checkNormalisations(TOLERANCE);
         }
         /**
          * Checks normalisation of probabilites.
          * @param tolerance tolerance of check, say 1e-6
          * @return 0 if OK, if bit 2^n set then problem with p1 (n=0), p2 (n=1) or joint probability (n=2).
          */
         public int checkNormalisations(double tolerance){
         double p1norm=0;
         double p2norm=0;
         double pjdnorm=0;
         for (int ci1=0; ci1<n1; ci1++){
                 p1norm += p1[ci1];
                 for (int ci2=0; ci2<n2; ci2++){
                     pjdnorm+=pjd[ci1][ci2];
                     if (ci1==0) p2norm += p2[ci2];
                 }
         }
         
         int r=0;
         if (Math.abs(p1norm-1)>tolerance) r+=1;
         if (Math.abs(p2norm-1)>tolerance) r+=2;
         if (Math.abs(pjdnorm-1)>tolerance) r+=4;
         return r;
         }
         
    /**
     * Gives string describing results of normalisation check on probabilites.
     * @return string describing issue
     */
    public String checkNormalisationsString(int r) {
        if (r == 0) {
            return "Normalisation of probabilities OK";
        } else {
            String s = "Normalisation of probability failure: ";
            if ((r & 1) > 0) {
                s = "marginal probability of community one, ";
            }
            if ((r & 2) > 0) {
                s = "marginal probability of community two, ";
            }
            if ((r & 4) > 0) {
                s = "joint probability, ";
            }
            return s;
        }


    }
         
         /**
          * Checks consistency of probabilites.
          * <p>Uses constant <tt>MutualInformation.TOLERANCE</tt> as tolerance
          * @return 0 if OK,  negative if not
          */
         public int checkConsistentcy(){
             return checkConsistentcy(TOLERANCE);
         }
         /**
          * Checks consistency of probabilites.
          * @return 0 if OK,  negative if not
          */
         public int checkConsistentcy(double tolerance){
         double p1check=0;
         for (int ci1=0; ci1<n1; ci1++){
                 p1check=0;
                 for (int ci2=0; ci2<n2; ci2++){
                     p1check+=pjd[ci1][ci2];
                 }
                 if (Math.abs(p1check-p1[ci1])>tolerance) return -1;
         }
         double p2check=0;
         for (int ci2=0; ci2<n2; ci2++){
                 p2check=0;
                 for (int ci1=0; ci1<n1; ci1++){
                     p2check+=pjd[ci1][ci2];
                 }
                 if (Math.abs(p2check-p2[ci2])>tolerance) return -2;
         }
         
         return 0;
         }
         
         /**
          * Short string of basic information on sets.
          * <p>Names and orders of sets.
          * @param sep separation string
          * @return String of general values.
          */
         public String toStringBasic(String sep){
             return name1+sep+name2+sep+n1+sep+n2;
         }
         /**
          * Short string of basic information on sets.
          * <p>Names and orders of sets.
          * @param sep separation string
          * @return String of general values.
          */
         static public String toStringBasicLabel(String sep){
             return "Name X"+sep+"Name Y"+sep+"|X|"+sep+"|Y|";
         }

         /**
          * Short string of general values.
          * @param sep separation string
          * @return String of general values.
          */
         public String toStringShort(String sep){
             String s=getMutualInformation()+sep+
                      getUniversalMetric()+sep+
                      getUniversalMetricNormalised()+sep+
                      getJointEntropy();
             return s;
         }
         /**
          * Label for <tt>toStringShort</tt>
          * @param sep separation string
          * @return label for short string of general values.
          */
         static public String toStringShortLabelDescriptive(String sep){
             String s="Mutual Information"+sep+
                      "Universal Metric"+sep+
                      "Universal Metric Normalised"+sep+
                      "Joint Entropy";
             return s;
         }
         /**
          * Label for <tt>toStringShort</tt>
          * @param sep separation string
          * @return label for short string of general values.
          */
         static public String toStringShortLabel(String sep){
             String s="I(X,Y)"+sep+
                      "d(X,Y)"+sep+
                      "d(X,Y)/H(X,Y)"+sep+
                      "H(X,Y)";
             return s;
         }
         
         /**
          * String of general values.
          * @param sep separation string
          * @return String of general values.
          */
         public String toString(String sep){
             String s=getMutualInformation()+sep+
                      getEntropyOne()+sep+
                      getEntropyTwo()+sep+
                      getJointEntropy()+sep+
                      getRedundancyNormalised()+sep+
                      getUniversalMetric()+sep+
                      getUniversalMetricNormalised()+sep+
                      getEntropyOneOverMax()+sep+
                      getEntropyTwoOverMax()+sep+
                      Math.log(n1/n2);
             return s;
         }
         /**
          * Label for <tt>toString</tt>
          * @param sep separation string
          * @return label for string of general values.
          */
         static public String toStringLabelDescriptive(String sep){
             String s="Mutual Information"+sep+
                      "Entropy One"+sep+
                      "Entropy Two"+sep+
                      "Joint Entropy"+sep+
                      "Normalised Redundancy"+sep+
                      "Universal Metric"+sep+
                      "Universal Metric Normalised"+sep+
                      "Entropy 1/max"+sep+
                      "Entropy 2/max"+sep+
                      "ln(|X|/|Y|)";
             return s;
         }
         /**
          * Label for <tt>toString</tt>
          * @param sep separation string
          * @return label for string of general values.
          */
        static public String toStringLabel(String sep){
             String s="I(X,Y)"+sep+
                      "H(X)"+sep+
                      "H(Y)"+sep+
                      "H(X,Y)"+sep+
                      "R(X,Y)/R_max"+sep+
                      "d(X,Y)"+sep+
                      "d(X,Y)/H(X,Y)"+sep+
                      "H(X)/ln(|X|)"+sep+
                      "H(Y)/ln(|Y|)"+sep+
                      "ln(|X|/|Y|)";
             return s;
         }



         /**
          * Prints out joint probability matrix.
          *@param PS PrintStream for output such as System.out
          *@param sep separation string
          * @param entryLabelsOn true if want rows and columns labelled
          * @param inputNumbersOn true if want the index of the community as well as the name
          */
         public void printJointProbability(PrintStream PS, String sep, boolean entryLabelsOn, boolean inputNumbersOn){
             if (entryLabelsOn) {
                 boolean numbersOn=inputNumbersOn;
                 boolean namesOn=true;
                 if (elementNames2==null) {namesOn=false; numbersOn=true;}
                 PS.print("X\\Y"+sep);
                 for (int ci2=0; ci2<n2; ci2++) PS.print("Y"+(numbersOn?"."+ci2:"")+(namesOn?"."+elementNames2[ci2]:"")+sep);
                 PS.println();
             }
             boolean namesOn=true;
             boolean numbersOn=inputNumbersOn;
             if (elementNames1==null) {namesOn=false; numbersOn=true;}
             for (int ci1=0; ci1<n1; ci1++){
                 if (entryLabelsOn) PS.print("X"+(numbersOn?"."+ci1:"")+(namesOn?"."+elementNames1[ci1]:ci1)+sep);
                 for (int ci2=0; ci2<n2; ci2++){
                     PS.print(pjd[ci1][ci2]+sep);
                 }
                 PS.println();
            }
         
         }

       /**
          * Output basic information.
          * @param PS PrintStream such as System.out
          * @param cc comment characters put at the start of every line
          * @param sep separation string
          * @param statsOn true if want basics mi stats output
          */
      public void printSummary(PrintStream PS, String cc, String sep, boolean statsOn){
        PS.println(cc+toStringBasicLabel(sep)+(statsOn?sep+toStringLabel(sep):""));
        PS.println(cc+toStringBasic(sep)+(statsOn?sep+toString(sep):""));
        }

       /**
          * Output information on joint probability to a file.
          * <p>File is <em>rootName</em><tt>mi.dat</tt> if just basic stats
          * and <em>rootName</em><tt>mijp.dat</tt> if has joint probability table
          * @param rootName stem of file name
          * @param cc comment characters put at the start of some lines
          * @param sep separation string
          * @param statsOn true if want basics mi stats output
          * @param jpOn true if want full joint probability output
          * @param entryLabelsOn true if want rows and columns labelled
          * @param numbersOn true if want the community index shown as well as any name
          * @param processInfoOn true if want descriptions of progress on screen.
          */
      public void printToFile(String rootName, String cc, String sep, boolean statsOn, boolean jpOn, boolean entryLabelsOn, boolean numbersOn, boolean processInfoOn){
        String fileName = rootName+(jpOn?"mijp.dat":"mi.dat");
        PrintStream PS;
        FileOutputStream fout;
        if (processInfoOn) System.out.println("Writing JointProbability to file "+ fileName);
            try {
            fout = new FileOutputStream(fileName);
            PS = new PrintStream(fout);
            printSummary(PS, cc,  sep, statsOn);
            if (jpOn) printJointProbability(PS, sep, entryLabelsOn, numbersOn);
            if (processInfoOn) System.err.println("Finished writing JointProbability to file "+ fileName);
            try{ fout.close ();
               } catch (IOException e) { System.err.println("*** File Error with " +fileName+", "+e.getMessage());}

        } catch (FileNotFoundException e) {
            System.err.println("*** Error opening output file "+fileName+", "+e.getMessage());
            return;
        }
       }

         
}
