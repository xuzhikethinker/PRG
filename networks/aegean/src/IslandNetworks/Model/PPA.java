/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IslandNetworks.Model;

import TimUtilities.TimSort;

/**
 * Performs PPA
 * @author time
 */
public class PPA extends IslandNetworks.NewIslandNetwork{
    
    
        
    /**
     * Does PPA and directed PPA analysis on network.
     * <br> See Broodbank page 180.
     * Uses <tt>betaInitial</tt> and sets this number of links from
     * each site to its nearest neighbours.  Note that there are two modes
     * DPPA (directed) and PPA (undirected, traditional form).
     * Note also that lambda and all site values are set to be 1.0.
     */
   @Override
    public void doEdgeModel() {
        long numEdgesPerSite = Math.round(betaInitial);
        if (numEdgesPerSite<1) numEdgesPerSite = 3;
        if (numEdgesPerSite>=numberSites) numEdgesPerSite = numberSites-1;
        boolean directed=false;
        if (updateMode.isPPAdirected()) directed=true;
        System.out.println("!!! "+(directed?"":"un")+"directed PPA with "+numEdgesPerSite+" edges per site");
        siteSet.setValues(1.0);
        siteSet.setWeights();
        double [] darr = new double[numberSites];
        edgeSet.setEdgePotential1(Hamiltonian);
        siteSet.initialiseSet(1,1,1);
        for (int i=0; i<numberSites; i++)
        {
            for (int j=0; j<numberSites; j++)
            {
                edgeSet.setEdgeValueNoBounds(i,j,0.0);
            }
        }

        for (int s=0; s<numberSites; s++)
        {
            for (int t=0; t<numberSites; t++) if (s==t) darr[t]=1.2345678e99;
            else darr[t]=edgeSet.getEdgeDistance(s, t);
            TimSort ts = new TimSort(darr);
            edgeSet.setEdgeValueBounded(s,s,0.0);  // self-loop zero
            for (int r=0; r<numberSites; r++)
            {
                int t=ts.getIndex(r);
                if (t==s) continue;
                //double v=1.0 - (r/((double) numberSites));
                if (r<=numEdgesPerSite) {
                    edgeSet.setEdgeValueNoBounds(s,t,1.0 );
                    if (!directed) edgeSet.setEdgeValueNoBounds(t,s,1.0);
                }
            }//eo r
        }// eo s

        siteSet.setWeights();
        calcNetworkStats();
        // ** must add this back in
        //siteSet.setAllDisplaySizes(DisplayVertexType.getValueIndex(), DisplayMaxVertexScale);

        if (message.getInformationLevel()>=1) printNetworkStatistics("#",3);
        //FileOutputNetworkStatistics("#", 3);

    }// eo doPPA

   
     /**
   * Sets up string of relevant parameter values, excluding input data name.
   * <br>Used for filenames and display.
   * @param sep separation string e.g. space for display, null for filenames.
   * @see #getParameterString(java.lang.String)
   */
    @Override
    public String getParameterString(String sep)
    {
      return updateMode.toString()+sep+"beta"+String.format("%03d", Math.round(betaInitial));
    }


//   /**
//     * Calculates the edges given the vertex properties.
//     * <p>Deprecated, use doEdgeModel() directly
//     */
//    @Override
//    public void calculateEdgeModel(){
//        doEdgeModel(); 
//    }
}
