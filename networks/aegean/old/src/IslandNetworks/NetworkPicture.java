package IslandNetworks;

import IslandNetworks.Edge.IslandEdgeSet;
import java.io.*;
//import java.lang.*;
import java.lang.Math.*;
//import java.util.Date;
//import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;

//import IslandNetworks.NetworkGenerator;
//import TimUtilities.StatisticalQuantity;
import IslandNetworks.Vertex.IslandSite;
//import IslandNetworks.IslandHamiltonian;

/**
 * Defines a JPanel containing a graphical representation of the network.
 * @author time
 */

class NetworkPicture extends JPanel {

	double windowScale;
	private IslandSite[] siteArray;
	int siteWeightFactor;
	IslandEdgeSet edgeSet;

	double minX,minY,maxX,maxY;

	int numberColours, numberSites, infolevel;
	int displayVertexType;
	UpdateMode updateMode;
	int edgeWidthFactor;

	Color[] javaColour;

        

        /*
         * Constructs from an island network and some parameters.
         *@param xsize horizontal (x) size of window
         *@param ysize vertical (y) size of window
         *@param islandNetwork the island Network
         */
        public NetworkPicture (int xsize, int ysize,  islandNetwork in)
        {
		setOpaque(true);
		setMinimumSize(new Dimension(100,100)); //don't hog space
		setPreferredSize(new Dimension(xsize, ysize));
		setBackground(Color.yellow);
		//setSize(new Dimension(800, 400));
		numberSites = in.numberSites;
                siteArray = new IslandSite[numberSites]; 
                for (int i=0; i< numberSites; i++)
                {
                    siteArray[i] = new IslandSite(in.siteArray[i]);
                }
                        
		siteWeightFactor = in.siteWeightFactor ;
		minX = in.minX;
		minY = in.minY;
		maxX = in.maxX;
		maxY = in.maxY;
		edgeSet = new IslandEdgeSet(in.edgeSet);
		// infolevel = in.infolevel;
		displayVertexType = in.DisplayVertexType;

		numberColours = in.numberColours;
		javaColour = in.javaColour; // this is not a deep copy
		updateMode = in.updateMode;

		edgeWidthFactor = in.edgeWidthFactor ;
	}
                
        /*
         * Constructs from explicit list of data.
         *@param xsize horizontal (x) size of window
         *@param ysize vertical (y) size of window
         *@param sites array of IslandSite conatining site data (siteArray[])
         *@param swf site weight factor (siteWeightFactor)
         *@param minimumX smallest x coordinate of sites
         *@param minimumY smallest y coordinate of sites
         *@param maximumX largest x coordinate of sites
         *@param maximumY largest y coordinate of sites
         *@param numCols number of colours (numberColours)
         *@param es edge set (edgeSet)
         *@param numSites number of sites (numberSites)
         *@param ilevel informationlevel (infolevel)
         *@param dvt display vertex type (displayVertexType)
         *@param jCol array of Java colours to be used (javaColour[])
         *@param umode update  mode (updateMode)
         *@param ewf edge width factor (edgeWidthFactor)
         */
	public NetworkPicture (int xsize, int ysize, IslandSite[] sites, int swf, double minimumX, double minimumY, double maximumX, double maximumY, int numCols, IslandNetworks.Edge.IslandEdgeSet es, int numSites, int ilevel, int dvt, Color[] jCol, int umode, int ewf){
		setOpaque(true);
		setMinimumSize(new Dimension(100,100)); //don't hog space
		setPreferredSize(new Dimension(xsize, ysize));
		setBackground(Color.yellow);
		//setSize(new Dimension(800, 400));
		siteArray = sites;
		siteWeightFactor = swf;
		minX = minimumX;
		minY = minimumY;
		maxX = maximumX;
		maxY = maximumY;
		numberColours = numCols;
		edgeSet = es;
		numberSites = numSites;
		infolevel = ilevel;
		displayVertexType = dvt;

		javaColour = jCol;
		updateMode.setMode(umode);

		edgeWidthFactor = ewf;
	}

	public void paint (Graphics ginput)
	{   
		if (infolevel>2) System.out.println("paint(g) called");
		Graphics2D g = (Graphics2D) ginput;
		int borderSize =30;
		int [] sitePositionX = new int[numberSites];
		int [] sitePositionY = new int[numberSites];
		Dimension d = getSize();
		double wsx = (d.width -borderSize-borderSize) / (maxX-minX);
		double wsy = (d.height -borderSize-borderSize)/(maxY-minY);
		if (wsx < wsy) windowScale = wsx; 
		else windowScale = wsy;
		if (windowScale<0) windowScale =1.0;
		// if (islnet.infolevel>2) System.out.println("Window Scale "+ windowScale);
		//if (islnet.infolevel>2) System.out.println("Window Site i, Size, Coordinates X,Y ");

		int numSites = numberSites;
		for (int i=0; i<numSites; i++)
		{ 
			sitePositionX[i] = (int)((siteArray[i].X-minX)*windowScale);
			sitePositionY[i] = (int)((siteArray[i].Y-minY)*windowScale);
		}


		g.setColor(Color.black);
		//double ew;
		double vw=1.0; // Default value for PPA mode
		double fracEdgeSize;
		double ec;
		int width;
		int greyness;
		int x1,y1,x2,y2;
		for (int i=0; i<numSites; i++)
		{
			if (!updateMode.isPPA())  vw=siteArray[i].getWeight();
			else vw=1;
			for (int j=0; j<numSites; j++)
			{  
				if (i==j) continue;
				ec=edgeSet.getEdgeColour(i,j);
				fracEdgeSize=ec/numberColours;                  
				width = (int)(0.5+edgeWidthFactor*fracEdgeSize);
				if ((ec>0) && (width<1)) width=1;
				if (width>0) 
				{ 
					greyness = 255-(int)(0.5+(fracEdgeSize*255.0)) ;
					if (greyness<0) greyness=0;
					if (greyness>255) greyness=255;
					g.setColor(new Color(greyness, greyness, greyness));
					//(greyness, greyness, greyness);
					BasicStroke bstroke = new BasicStroke((float) width);
					g.setStroke(bstroke);
					x1 = sitePositionX[i];
					y1 = sitePositionY[i];
					x2 = (sitePositionX[i]+sitePositionX[j])/2;
					y2 = (sitePositionY[i]+sitePositionY[j])/2;
					g.drawLine( borderSize + x1, borderSize + y1, 
					borderSize + x2, borderSize + y2);
				}

			} //eo for j

		}// eo for i

		int size=20;
		int maxSiteShade =128;
		if (infolevel>2) System.out.print("\nsite sizes: ");
		for (int i=0; i<numSites; i++)
		{ 
			if (infolevel>2) System.out.print(Double.toString(siteArray[i].displaySize) + ", ");
			double fracsize=  siteArray[i].displaySize/numberColours ;  
			int displayShade = 255-(int)(0.5+ maxSiteShade*fracsize) ;
			size = (int) (siteWeightFactor*Math.sqrt(fracsize)+0.5);
			int zerositeWeight = siteWeightFactor;
			Color vertexColour;
			switch (displayVertexType)
			{
			case 2: 
				int vc = numberColours-siteArray[siteArray[i].influence].influenceRank;
				if (vc<0) vc=0;
				vertexColour = javaColour[vc];
				break;
			case 0: 
				vertexColour = new Color(displayShade,0,0); 
				break;
			case 1: 
				vertexColour = new Color(0,0,displayShade);  
				break;
			default: 
				vertexColour = Color.green;
			};
			g.setColor(vertexColour);
			if (size>0) {
				if (infolevel>2) System.out.println("filled oval");
				g.fillOval( borderSize + sitePositionX[i] - size/2, borderSize + sitePositionY[i] - size/2, size, size);
			} else {
				if (infolevel>2) System.out.println("open oval");
				g.drawOval(borderSize + sitePositionX[i] - zerositeWeight/2, borderSize + sitePositionY[i]- zerositeWeight/2, zerositeWeight, zerositeWeight);
			}

			//g.setXORMode(Color.red); // make sure can see writing
			g.setColor(Color.black);
			String siteLabel="";
			switch (displayVertexType)
			{
			case 2: 
				siteLabel=siteArray[siteArray[i].influence].shortName;
				break;
			case 0:
			case 1:    
			default: 
				siteLabel=siteArray[i].shortName;
			}              
			g.drawString(siteLabel,borderSize + sitePositionX[i], borderSize + sitePositionY[i]);
			g.setPaintMode(); //return to overwrite mode

			if (infolevel>2)
				System.out.println(i+" "+size+" "+sitePositionX[i]+" "+ sitePositionY[i] );
			//siteArray[i].name
			//siteArray[i].displaySize
		}

	}// eo paint
} //eo private class NetworkPicture extends JPanel

