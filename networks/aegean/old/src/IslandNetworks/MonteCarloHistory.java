/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IslandNetworks;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;


import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;

/**
 * Statistics on Monte Carlo.
 * @author time
 */
public class MonteCarloHistory {

    DoubleArrayList beta;
    DoubleArrayList energy;
    DoubleArrayList vupdates;
    DoubleArrayList eupdates;

    public MonteCarloHistory(int n) {
        beta = new DoubleArrayList(n);
        energy = new DoubleArrayList(n);
        vupdates = new DoubleArrayList(n);
        eupdates = new DoubleArrayList(n);
    }

    /**
     * Updates statistics on MC.
     * @param betavalue current beta value
     * @param energyvalue current energy value
     * @param vertexupdates fraction of updates made/tried on vertices
     * @param edgeupdates fraction of updates made/tried on edges
     */
    public void add(double betavalue, double energyvalue, double vertexupdates, double edgeupdates) {
        beta.add(betavalue);
        energy.add(energyvalue);
        vupdates.add(vertexupdates);
        eupdates.add(edgeupdates);
    }

    /** Prints out table of values in Monte Carlo Time on printstream.
     *@param PS a print stream for the output such as System.out
     *@param sep separation string  
     */
    public void printTimeEvolution(PrintStream PS, String sep) {
        double energymax = getMax(energy);
        double vmax = getMax(vupdates);
        double emax = getMax(eupdates);
        PS.println("sweep" + sep + "beta" + sep + "energy" + sep + "V.updates" + sep + "E.updates" + sep + "energy/max"  + sep + "V.up.frac" + sep + "E.up.frac");
        for (int t = 0; t < beta.size(); t++) {
            PS.println(t + sep +beta.getQuick(t) + sep + energy.getQuick(t) + sep + vupdates.getQuick(t) + sep + eupdates.getQuick(t)+ sep + energy.getQuick(t)/energymax + sep + vupdates.getQuick(t)/vmax + sep + eupdates.getQuick(t)/emax);
        }
    }

    /** Finds largest value in list.
     * @param list  
     */
    public double getMax(DoubleArrayList list) {
        double max=list.get(0);
        for (int t = 0; t < beta.size(); t++) if (max<list.get(t)) max=list.get(t);
        return max;
    }
    
    /** Finds largest value in list.
     * @param list  
     */
    public int getMax(IntArrayList list) {
        int max=list.get(0);
        for (int t = 0; t < beta.size(); t++) if (max<list.get(t)) max=list.get(t);
        return max;
    }
    
    
    /** Outputs MC history to File.
     *@param filenamecomplete complete file name including path
     */
    public void FileOutputMonteCarloHistory(String filenamecomplete) {
       
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);

            printTimeEvolution(PS, "\t");

            try {
                fout.close();
                System.out.println("Finished writing to " + filenamecomplete);
            } catch (IOException e) {
                System.out.println("File Error");
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file " + filenamecomplete);
            return;
        }
        return;
    }//eo FileOutputDistanceValues
}
