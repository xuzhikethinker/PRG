// Tim's old class

private class NetworkPicture extends JPanel {
           double windowScale;



         public NetworkPicture (){
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(400, 400));
             setBackground(Color.cyan);
             //setSize(new Dimension(800, 400));
         }

         public NetworkPicture (int xsize, int ysize){
             setOpaque(true);
             setMinimumSize(new Dimension(100,100)); //don't hog space
             setPreferredSize(new Dimension(xsize, ysize));
             setBackground(Color.yellow);
             //setSize(new Dimension(800, 400));
         }

         public void paint (Graphics ginput)
         {
             Graphics2D g = (Graphics2D) ginput;
             int borderSize =30;
             int [] sitePositionX = new int[numberSites];
             int [] sitePositionY = new int[numberSites];
             Dimension d = getSize();
             double wsx = (d.width -borderSize-borderSize) / (maxX-minX);
             double wsy = (d.height -borderSize-borderSize)/(maxY-minY);
             if (wsx < wsy) windowScale = wsx; else windowScale = wsy;
             if (windowScale<0) windowScale =1.0;
             // if (islnet.infolevel>2) System.out.println("Window Scale "+ windowScale);
             //if (islnet.infolevel>2) System.out.println("Window Site i, Size, Coordinates X,Y ");

             int numSites =islnet.numberSites;
                 for (int i=0; i<numSites; i++)
             {
                 sitePositionX[i] = (int)((islnet.siteArray[i].X-islnet.minX)*windowScale);
                 sitePositionY[i] = (int)((islnet.siteArray[i].Y-islnet.minY)*windowScale);
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
               if (updateMode>0)  vw=islnet.siteArray[i].getWeight();
               else vw=1;
               for (int j=0; j<numSites; j++)
               {
                  if (i==j) continue;
                  ec=islnet.edgeSet.getEdgeColour(i,j);
                  fracEdgeSize=ec/numberColours;
                  width = (int)(0.5+islnet.edgeWidthFactor*fracEdgeSize);
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
             for (int i=0; i<numSites; i++)
             {
              double fracsize=  islnet.siteArray[i].displaySize/islnet.numberColours ;
              int displayShade = 255-(int)(0.5+ maxSiteShade*fracsize) ;
              size = (int) (islnet.siteWeightFactor*Math.sqrt(fracsize)+0.5);
              int zerositeWeight = islnet.siteWeightFactor;
              Color vertexColour;
              switch (DisplayVertexType)
              {
                  case 2: int vc = islnet.numberColours-islnet.siteArray[islnet.siteArray[i].influence].influenceRank;
                          if (vc<0) vc=0;
                          vertexColour = javaColour[vc];
                          break;
                  case 0: vertexColour = new Color(displayShade,0,0); break;
                  case 1: vertexColour = new Color(0,0,displayShade);  break;
                  default: vertexColour = Color.green;
              };
              g.setColor(vertexColour);
              if (size>0) g.fillOval( borderSize + sitePositionX[i] - size/2, borderSize + sitePositionY[i] - size/2, size, size);
              else g.drawOval(borderSize + sitePositionX[i] - zerositeWeight/2, borderSize + sitePositionY[i]- zerositeWeight/2, zerositeWeight, zerositeWeight);

              //g.setXORMode(Color.red); // make sure can see writing
              g.setColor(Color.black);
              String siteLabel="";
              switch (DisplayVertexType)
              {
                  case 2: siteLabel=islnet.siteArray[islnet.siteArray[i].influence].shortName;
                          break;
                  case 0:
                  case 1:
                  default: siteLabel=islnet.siteArray[i].shortName;
              };
              g.drawString(siteLabel,borderSize + sitePositionX[i], borderSize + sitePositionY[i]);
              g.setPaintMode(); //return to overwrite mode

              if (islnet.infolevel>2)
                  System.out.println(i+" "+size+" "+sitePositionX[i]+" "+ sitePositionY[i] );
              //siteArray[i].name
              //siteArray[i].displaySize
              }

         }// eo paint
     } //eo private class NetworkPicture extends JPanel
