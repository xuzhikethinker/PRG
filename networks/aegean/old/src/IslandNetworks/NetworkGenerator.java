/*
 * NetworkGenerator.java
 *
 * Created on 15 March 2006, 17:46
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package IslandNetworks;

import java.io.*;

/**
 *
 * @author time
 */
public class NetworkGenerator {
    String SEP = "\t";
    String dirname="/PRG/networks/aegean/output/";
    String nameroot="test";
    int NUMBERTYPES=5;
    int numberSites;
    double scale;
    double [] Xpos;
    double [] Ypos;
    double [][] dist;
    String [] name;
    String [] shortname;
    int networkType=0;
    double sizex=1.0;
    double sizey=1.0;
    
    /** Creates a new instance of NetworkGenerator. */
    public NetworkGenerator() 
    {
        
    }
    
    /** 
     * Creates a new instance of NetworkGenerator.
     * <br> Types: 1 (default) lattice; 2,3 line; 4 circle.
     *@param inputType type of network to generate.
     *@param number number of sites
     *@param inputscale distance between nearest neighbours
     */
    public NetworkGenerator(int inputType, int number,  double inputscale) 
    {
        networkType=inputType;
        scale=inputscale;
        System.out.println("Generating type "+networkName()+", number of sites "+number+", nearest neighbour distance "+scale);
        switch (networkType)
        {
            case 4: makeCircleSiteData(number, scale);break;
            case 3:
            case 2: makeLineSiteData(number, scale); break;
            case 1: 
            default: makeLatticeSiteData(number, number, scale);
        }
        calcDistanceData();        
    }


    
    // ---------------------------------------------------------------------------------

    /** 
     * Creates a lattice of sites.
     * @param xnumber number of sites in x direction
     * @param ynumber number of sites in y direction
     * @param scale distance between nearest neighbours
     */
    public void makeLatticeSiteData(int xnumber, int ynumber, double scale) 
    {
        sizex=scale*xnumber;
        sizey=scale*ynumber;
        int ns=xnumber*ynumber;
        Xpos = new double[ns];
        Ypos = new double[ns];
        name = new String[ns];
        shortname = new String[ns];
        
      nameroot=networkName()+"_"+xnumber+"_"+ynumber+"_"+TruncDec(scale, 2)+"_";
      numberSites =0;
      for (int x=0; x<xnumber; x++)
      {
        for (int y=0; y<ynumber; y++)
        {
          name[numberSites] = "("+x+","+y+")";
          shortname[numberSites] = Integer.toString(x*ynumber+y);
          Xpos[numberSites] = x*scale;
          Ypos[numberSites] = y*scale;
          numberSites++;
        }// eo for y
      }//eo for x
    }

// ---------------------------------------------------------------------------------

    /** 
     * Creates a lattice of sites.
     * @param xnumber of sites in x direction
     * @param scale distance between nearest neighbours
     */
    public void makeLineSiteData(int xnumber, double scale) 
    {
        sizex=scale*xnumber;
        int ns=xnumber;
        Xpos = new double[ns];
        Ypos = new double[ns];
        name = new String[ns];
        shortname = new String[ns];
        
        nameroot=networkName()+"_"+xnumber+"_"+TruncDec(scale, 2)+"_";
      numberSites =0;
      for (int x=0; x<xnumber; x++)
      {
          name[numberSites] = "("+x+")";
          shortname[numberSites] = Integer.toString(x);
          Xpos[numberSites] = x*scale;
          Ypos[numberSites] = scale/2.0;
          numberSites++;
      }//eo for x
    }

    // ---------------------------------------------------------------------------------

    /** 
     * Creates a circle of sites.
     * @param number of sites 
     * @param scale is distance between neighbouuring sites.
     */
    public void makeCircleSiteData(int number, double scale) 
    {
        int ns=number;
        Xpos = new double[ns];
        Ypos = new double[ns];
        name = new String[ns];
        shortname = new String[ns];
        
        double theta = 2.0*Math.PI/((double) number);
        double radius = scale/Math.tan(theta/2.0);
        nameroot=networkName()+"_"+number+"_"+TruncDec(scale, 2);
        numberSites =0;
        for (int x=0; x<number; x++)
        {
          name[numberSites] = "("+x+")";
          shortname[numberSites] = Integer.toString(x);
          Xpos[numberSites] = radius*Math.sin(theta*x);
          Ypos[numberSites] = radius*Math.cos(theta*x);
          numberSites++;
        }//eo for x
    }

    
    // ---------------------------------------------------------------------------------

    /** 
     * Calculates the distances based on site values.
     */
    public void calcDistanceData() 
    {
        dist = new double[numberSites][numberSites];
      // Now calculates distances
      for (int i =0; i<numberSites; i++) 
       {        
            for (int j =0; j<numberSites; j++) 
            {
                switch (networkType)
                {
                    case 3:
                    case 1: dist[i][j]=torusDistance(Xpos[i],Ypos[i],Xpos[j],Ypos[j]); break;
                    default: dist[i][j]=euclideanDistance(Xpos[i],Ypos[i],Xpos[j],Ypos[j]);
                }
            };
       };
    }// eo  makeLatticeSiteData   


    /** 
     * Calculates the distances based on site values.
     *@param x1 first site x coordinate
     *@param y1 first site y coordinate
     *@param x2 second site x coordinate
     *@param y2 second site y coordinate
     */
    public double euclideanDistance(double x1,double y1, double x2, double y2) 
    {
        double dx = (x1-x2);
        double dy = (y1-y2);
        return Math.sqrt(dx*dx+dy*dy);
    }

    /** 
     * Calculates the distances based on site values for torus
     *@param x1 first site x coordinate
     *@param y1 first site y coordinate
     *@param x2 second site x coordinate
     *@param y2 second site y coordinate
     */
    public double torusDistance(double x1,double y1, double x2, double y2) 
    {
        double dx = Math.min(Math.abs(x1-x2), sizex - Math.abs((x1-x2)));
        double dy = Math.min(Math.abs(y1-y2), sizey - Math.abs((y1-y2)));
        return Math.sqrt(dx*dx+dy*dy);
    }
    
    // -------------------------------------------------------------------
  /**
     * Outputs network in islandNetwork format.
     * <br> <emph>dirname</emph>+<emph>nameroot</emph>_input.dat general info
     * @param dec number of decimal places to show
     */
    public void FileOutputSite(int dec) 
    {
       
        String cc="# ";
        String filenamecomplete =  dirname+nameroot+ "_input.dat";        
        System.out.println("Attempting to write general information to "+ filenamecomplete);
            
        PrintStream PS;

        // next bit of code p327 Schildt and p550
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filenamecomplete);
            PS = new PrintStream(fout);
            
            PS.println("# "+SEP+networkName()+SEP+"Scale"+SEP+scale);
            
            PS.print("Name");
            for (int i=0; i<numberSites;i++) PS.print(SEP+name[i]);
            PS.println();
            
            PS.print("ShortName");
            for (int i=0; i<numberSites;i++) PS.print(SEP+shortname[i]);
            PS.println();
             
            PS.print("Size");
            for (int i=0; i<numberSites;i++) PS.print(SEP+"1");
            PS.println();
            
            PS.print("XPos");
            for (int i=0; i<numberSites;i++) PS.print(SEP+Xpos[i]);
            PS.println();
            
            PS.print("YPos");
            for (int i=0; i<numberSites;i++) PS.print(SEP+Ypos[i]);
            PS.println();
            
            PS.println("*Distances");    
            for (int i=0; i<numberSites;i++) 
            {
                PS.print(name[i]);
                for (int j=0; j<numberSites;j++) PS.print(SEP+dist[i][j]);
                PS.println();
            }
            
            try
            { 
               fout.close ();
               System.out.println("Finished writing to "+ filenamecomplete);
            } catch (IOException e) { System.out.println("File Error");}

        } catch (FileNotFoundException e) {
            System.out.println("Error opening output file "+ filenamecomplete);
            return;
        }
        return;
    }//eo 
    
    
    
    /**
     * Returns name of type of network. 
     *@return string with name of network type.
     */
    public String networkName()
    {
        return networkName(networkType);
    }

    /**
     * Returns name of type of network. 
     *@param type network type number.
     *@return string with name of network type.
     */
    public String networkName(int type)
    {
        String s="Unknown";
        switch (type)
        {
            case 4: s="circle"; break;
            case 3: s="torus1D"; break;
            case 2: s="line"; break;
            case 1: s="torus2D"; break;
            default: s="lattice";
        }
        return s;
    }
         /**
          *Truncates a double to a given number of decimal places.
     * @param value has tractional part truncated
     * @param dec number of decimals to retain
     */
    public double TruncDec(double value, int dec)
    {
      double shift = Math.pow(10,dec);
      return ( ( (double) ((int) (value*shift+0.5)))/shift);
    }

  
}
