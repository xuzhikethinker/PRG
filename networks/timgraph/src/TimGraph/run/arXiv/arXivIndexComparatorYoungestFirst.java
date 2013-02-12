/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run.arXiv;

import java.util.Comparator;

  /**
    * Provides comparison of arXiv identities
    * <p>arXiv papers are labelled yymmnnn but this means all
    * papers in years 2000 and later are less than papers published before.
    * This methods puts the youngest most recent papers before the
    * older papers. Example 9801987 comes before 9902543 which comes before
    * 0011001.
    * @author time
    */
   public class arXivIndexComparatorYoungestFirst implements Comparator<String> {
        @Override
       public int compare(String s1, String s2){
           String ss1 = (s1.charAt(0)<'5'?1:0)+s1;
           String ss2 = (s2.charAt(0)<'5'?1:0)+s2;
           return ss1.compareTo(ss2);
       }
        public boolean equal(String s1, String s2){
           return s2.equals(s1);
       }

   }
