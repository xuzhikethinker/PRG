/*
 * KKWeightedLayout.java
 * 
 * Based on edu.uci.ics.jung.visualization.contrib.KKLayout
 * Copyright (c) 2003, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 * edu.uci.ics.jung.visualization.contrib;
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on 07 December 2007, 17:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks.jungInterfaces;

import java.awt.Dimension;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

//import edu.uci.ics.jung.algorithms.shortestpath.Distance;
//import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.statistics.GraphStatistics;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;


/**
 *
 * Implements the Kamada-Kawai algorithm for node layout.
 * Does not respect filter calls, and sometimes crashes when the view changes to it.
 *
 * @see "Tomihisa Kamada and Satoru Kawai: An algorithm for drawing general indirect graphs. Information Processing Letters 31(1):7-15, 1989" 
 * @see "Tomihisa Kamada: On visualization of abstract objects and relations. Ph.D. dissertation, Dept. of Information Science, Univ. of Tokyo, Dec. 1988."
 *
 * @author Masanori Harada
 * @author time
 */
public class KKWeightedLayoutTest extends AbstractLayout {
    

	private double EPSILON = 0.1d;

	private int currentIteration;
        private int maxIterations = 2000;
 	private String status = "KKLayout";

	private double L;			// the ideal length of an edge
	private double K = 1;		// arbitrary const number
	private double[][] dm;     // distance matrix

	private boolean adjustForGravity = true;
	private boolean exchangeVertices = true;

	private Vertex[] vertices;
	private Coordinates[] xydata;

    /**
     * Retrieves graph distances between vertices of the visible graph
     */
//    protected Distance distance;

    /**
     * The diameter of the visible graph. In other words, the maximum over all pairs
     * of vertices of the length of the shortest path between a and bf the visible graph.
     */
	protected double diameter;

    /**
     * A multiplicative factor which partly specifies the "preferred" length of an edge (L).
     */
    private double length_factor = 0.9;

    /**
     * A multiplicative factor which specifies the fraction of the graph's diameter to be 
     * used as the inter-vertex distance between disconnected vertices.
     */
    private double disconnected_multiplier = 0.5;
    
      private float scale;  
      private float shift;  
      // Would be better to us a JUNG transformer private Transformer transformer;


    
//    public KKWeightedLayoutTest(Graph g) 
//    {
//        this(g, new UnweightedShortestPath(g));
//	}

    public KKWeightedLayoutTest(Graph g, double [][] dmInput, double diameterInput)
    {
        super(g);
        dm = dmInput;
        diameter = diameterInput;
        Dimension d = getCurrentSize();
        float size = Math.min(d.height,d.width);
        scale=size*0.8f;
        shift=size*0.1f;

    }
    public KKWeightedLayoutTest(Graph g, double [][] dmInput, double diameterInput, double scale, double shift)
    {
        super(g);
        dm = dmInput;
        diameter = diameterInput;
        this.scale=(float) scale;
        this.shift= (float) shift;

    }

    /**
     * Sets a multiplicative factor which 
     * partly specifies the "preferred" length of an edge (L).
     */
    public void setLengthFactor(double length_factor)
    {
        this.length_factor = length_factor;
    }
    
    /**
     * Sets a multiplicative factor that specifies the fraction of the graph's diameter to be 
     * used as the inter-vertex distance between disconnected vertices.
     */
    public void setDisconnectedDistanceMultiplier(double disconnected_multiplier)
    {
        this.disconnected_multiplier = disconnected_multiplier;
    }
    
    @Override
	public String getStatus() {
		return status + this.getCurrentSize();
	}

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

	/**
	 * This one is an incremental visualization.
	 */
	public boolean isIncremental() {
		return true;
	}

	/**
	 * Returns true once the current iteration has passed the maximum count.
	 */
	public boolean incrementsAreDone() {
		if (currentIteration > maxIterations) {
			return true;
		}
		return false;
	}

    protected void initialize_local() {
        currentIteration = 0;
	}

    @Override
    protected void initializeLocations() {
		super.initializeLocations();

		Dimension d = getCurrentSize();
		double height = d.getHeight();
		double width = d.getWidth();

        int n = getGraph().getVertices().size();
        dm = new double[n][n];
		vertices = new Vertex[n];
		xydata = new Coordinates[n];

		// assign IDs to all visible vertices
		while(true) {
		    try {
		        int index = 0;
		        for (Iterator iter = getGraph().getVertices().iterator();
		        iter.hasNext(); ) {
		            Vertex v = (Vertex) iter.next();
		            Coordinates xyd = getCoordinates(v);
		            vertices[index] = v;
		            xydata[index].x = xyd.x*scale+shift;
		            xydata[index].y = xyd.y*scale+shift;
		            index++;
		        }
		        break;
		    } catch(ConcurrentModificationException cme) {}
		}

            

        double L0 = Math.min(height, width);
        L = (L0 / diameter) * length_factor;  // length_factor used to be hardcoded to 0.9
		//L = 0.75 * Math.sqrt(height * width / n);

	}


	protected void initialize_local_vertex(Vertex v) {
	}

    public void advancePositions() {
		currentIteration++;
		double energy = calcEnergy();
		status = "Kamada-Kawai V=" + getVisibleVertices().size()
            + "(" + getGraph().numVertices() + ")"
			+ " IT: " + currentIteration
			+ " E=" + energy
			;

		int n = getVisibleGraph().numVertices();
        if (n == 0)
            return;

		double maxDeltaM = 0;
		int pm = -1;            // the node having max deltaM
		for (int i = 0; i < n; i++) {
            if (isLocked(vertices[i]))
                continue;
			double deltam = calcDeltaM(i);
			//System.out.println("* i=" + i + " deltaM=" + deltam);
			if (maxDeltaM < deltam) {
				maxDeltaM = deltam;
				pm = i;
			}
		}
		if (pm == -1)
            return;

        for (int i = 0; i < 100; i++) {
			double[] dxy = calcDeltaXY(pm);
			xydata[pm].add(dxy[0], dxy[1]);
			double deltam = calcDeltaM(pm);
            if (deltam < EPSILON)
                break;
            //if (dxy[0] > 1 || dxy[1] > 1 || dxy[0] < -1 || dxy[1] < -1)
            //    break;
		}

		if (adjustForGravity)
			adjustForGravity();

		if (exchangeVertices && maxDeltaM < EPSILON) {
            energy = calcEnergy();
			for (int i = 0; i < n - 1; i++) {
                if (isLocked(vertices[i]))
                    continue;
				for (int j = i + 1; j < n; j++) {
                    if (isLocked(vertices[j]))
                        continue;
					double xenergy = calcEnergyIfExchanged(i, j);
					if (energy > xenergy) {
						double sx = xydata[i].getX();
						double sy = xydata[i].getY();
						xydata[i].setX(xydata[j].getX());
						xydata[i].setY(xydata[j].getY());
						xydata[j].setX(sx);
						xydata[j].setY(sy);
						//System.out.println("SWAP " + i + " with " + j +
						//				   " maxDeltaM=" + maxDeltaM);
						return;
					}
				}
			}
		}
	}

	/**
	 * Shift all vertices so that the center of gravity is located at
	 * the center of the screen.
	 */
	public void adjustForGravity() {
		Dimension d = getCurrentSize();
		double height = d.getHeight();
		double width = d.getWidth();
		double gx = 0;
		double gy = 0;
		for (int i = 0; i < xydata.length; i++) {
			gx += xydata[i].getX();
			gy += xydata[i].getY();
		}
		gx /= xydata.length;
		gy /= xydata.length;
		double diffx = width / 2 - gx;
		double diffy = height / 2 - gy;
		for (int i = 0; i < xydata.length; i++) {
			xydata[i].add(diffx, diffy);
		}
	}

	/**
	 * Enable or disable gravity point adjusting.
	 */
	public void setAdjustForGravity(boolean on) {
		adjustForGravity = on;
	}

	/**
	 * Returns true if gravity point adjusting is enabled.
	 */
	public boolean getAdjustForGravity() {
		return adjustForGravity;
	}

	/**
	 * Enable or disable the local minimum escape technique by
	 * exchanging vertices.
	 */
	public void setExchangeVertices(boolean on) {
		exchangeVertices = on;
	}

	/**
	 * Returns true if the local minimum escape technique by
	 * exchanging vertices is enabled.
	 */
	public boolean getExchangeVertices() {
		return exchangeVertices;
	}

	/**
	 * Determines a step to new position of the vertex m.
	 */
	private double[] calcDeltaXY(int m) {
		double dE_dxm = 0;
		double dE_dym = 0;
		double d2E_d2xm = 0;
		double d2E_dxmdym = 0;
		double d2E_dymdxm = 0;
		double d2E_d2ym = 0;

		for (int i = 0; i < vertices.length; i++) {
			if (i != m) {
                
                double dist = dm[m][i];
				double l_mi = L * dist;
				double k_mi = K / (dist * dist);
				double dx = xydata[m].getX() - xydata[i].getX();
				double dy = xydata[m].getY() - xydata[i].getY();
				double d = Math.sqrt(dx * dx + dy * dy);
				double ddd = d * d * d;

				dE_dxm += k_mi * (1 - l_mi / d) * dx;
				dE_dym += k_mi * (1 - l_mi / d) * dy;
				d2E_d2xm += k_mi * (1 - l_mi * dy * dy / ddd);
				d2E_dxmdym += k_mi * l_mi * dx * dy / ddd;
				d2E_d2ym += k_mi * (1 - l_mi * dx * dx / ddd);
			}
		}
		// d2E_dymdxm equals to d2E_dxmdym.
		d2E_dymdxm = d2E_dxmdym;

		double denomi = d2E_d2xm * d2E_d2ym - d2E_dxmdym * d2E_dymdxm;
		double deltaX = (d2E_dxmdym * dE_dym - d2E_d2ym * dE_dxm) / denomi;
		double deltaY = (d2E_dymdxm * dE_dxm - d2E_d2xm * dE_dym) / denomi;
		return new double[]{deltaX, deltaY};
	}

	/**
	 * Calculates the gradient of energy function at the vertex m.
	 */
	private double calcDeltaM(int m) {
		double dEdxm = 0;
		double dEdym = 0;
		for (int i = 0; i < vertices.length; i++) {
			if (i != m) {
                double dist = dm[m][i];
				double l_mi = L * dist;
				double k_mi = K / (dist * dist);

				double dx = xydata[m].getX() - xydata[i].getX();
				double dy = xydata[m].getY() - xydata[i].getY();
				double d = Math.sqrt(dx * dx + dy * dy);

				double common = k_mi * (1 - l_mi / d);
				dEdxm += common * dx;
				dEdym += common * dy;
			}
		}
		return Math.sqrt(dEdxm * dEdxm + dEdym * dEdym);
	}

	/**
	 * Calculates the energy function E.
	 */
	private double calcEnergy() {
		double energy = 0;
		for (int i = 0; i < vertices.length - 1; i++) {
			for (int j = i + 1; j < vertices.length; j++) {
                double dist = dm[i][j];
				double l_ij = L * dist;
				double k_ij = K / (dist * dist);
				double dx = xydata[i].getX() - xydata[j].getX();
				double dy = xydata[i].getY() - xydata[j].getY();
				double d = Math.sqrt(dx * dx + dy * dy);


				energy += k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij -
									  2 * l_ij * d);
			}
		}
		return energy;
	}

	/**
	 * Calculates the energy function E as if positions of the
	 * specified vertices are exchanged.
	 */
	private double calcEnergyIfExchanged(int p, int q) {
		if (p >= q)
			throw new RuntimeException("p should be < q");
		double energy = 0;		// < 0
		for (int i = 0; i < vertices.length - 1; i++) {
			for (int j = i + 1; j < vertices.length; j++) {
				int ii = i;
				int jj = j;
				if (i == p) ii = q;
				if (j == q) jj = p;

                double dist = dm[i][j];
				double l_ij = L * dist;
				double k_ij = K / (dist * dist);
				double dx = xydata[ii].getX() - xydata[jj].getX();
				double dy = xydata[ii].getY() - xydata[jj].getY();
				double d = Math.sqrt(dx * dx + dy * dy);
				
				energy += k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij -
									  2 * l_ij * d);
			}
		}
		return energy;
	}
}

