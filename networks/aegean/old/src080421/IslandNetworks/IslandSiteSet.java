/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IslandNetworks;

/**
 *
 * @author time
 */
public class IslandSiteSet {
    int numberSites;
    IslandSite [] siteArray;
    islandNetwork.SiteRanking siteRank;  
    int [] siteAlphabeticalOrder; // = new int[numberSites];  // Sites in order of names [i] = no. of i-th site
    int [] siteWeightOrder;  // points in order [i] = no. of i-throws biggest Size
        double minX=0;
        double maxX=0;
        double minY=0;
        double maxY=0;

        public IslandSiteSet(){
            
            
          //maybe delay this?  
          calcSiteOrder(1, siteAlphabeticalOrder, numberSites); // find alphabetic site order

            
        }
        
        
        
        
        // **********************************************************    
/** Produces a list of the rank (order) of a vector of values.
     * @param valueNumber is number of entry in Site class to be ordered
     * @param orderVector is vector of integers [i] = no. of i-th value
     * @param n is number of entries to order
     */
    private void calcSiteOrder(int valueNumber, int [] orderVector, int n)
    {
// Now calculate Ranking Order orderVector[i] = number of site ranked i 
         boolean alpha = siteArray[0].isAlpha(valueNumber);
         for (int i=0; i<n; i++) orderVector[i]=i;
         for (int i=0; i<n; i++) 
            {
                int best = orderVector[i];
                for (int j=i+1; j<n; j++)
                {
                 int newbest = orderVector[j];
                 if ( ((alpha) && (siteArray[best].toString(valueNumber).compareToIgnoreCase(siteArray[newbest].toString(valueNumber))>0)  )
                     || ((!alpha) && (siteArray[best].getValue(valueNumber) < siteArray[newbest].getValue(valueNumber) )  ) )
                 {
                     best = newbest;
                     orderVector[j] = orderVector[i];
                     orderVector[i]=best;
                 }
                }//eo for j                
            }//eo for i
    }

    public void calcSiteRankOrder(int valueNumber, int [] orderVector, int rankNumber )
    {
        int n = orderVector.length;
        calcSiteOrder(valueNumber, orderVector,n);
        // Now calculate rank of sites so rankVector[i] = rank of site in terms of values        
         for (int i=0; i<n; i++) siteArray[orderVector[i]].setInt(rankNumber, i);
        n=0;
    }
        
}
