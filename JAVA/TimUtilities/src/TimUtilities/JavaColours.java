/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

import java.awt.Color;
import java.io.PrintStream;
//import java.util.Formatter;

/**
 *
 * @author time
 */
public class JavaColours {
    
    /**
     * Colours are done in batches of {@value }
     */
    static final int NUMBERSHADES=6; // Avoids Greys otherwise use 7
    
    /**
     * Minimum colour value in any channel will be 255 - {@value }
     * <br>note that the Wikipedia page on Web Safe colours indicates that we shoul be separating the 
     * colour space up in steps of 48 = 30hex
     */
    static final int MAXDEVIATION=204;
    
    /**
     * Array of fixed colours available in java.
     */
    static final Color [] FIXEDCOLOUR = {
    Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, 
    Color.MAGENTA, Color.CYAN, 
    Color.PINK,  Color.ORANGE,  Color.YELLOW, 
    Color.GRAY, Color.DARK_GRAY, Color.LIGHT_GRAY, Color.BLACK};

    
    static final Color DARKRED = new Color(96,0,0);    
    static final Color DARKGREEN = new Color(0,96,0);    
    static final Color DARKBLUE = new Color(0,0,96);    
    static final Color X11DARKORANGE = new Color(255,140,0);    
    static final Color X11PURPLE = new Color(128,0,128);    
    static final Color OLIVE = new Color(102,102,0);    
    static final Color X11DARKGOLDENROD = new Color(184,134,11);    
    static final Color X11PINK = new Color(155,192,203);    

    /**
     * Array of fixed colours defined by eye.
     */
    static final Color [] TIMCOLOUR = {
    Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, 
    Color.CYAN, Color.MAGENTA, Color.YELLOW,
    DARKRED, DARKGREEN, DARKBLUE, 
    X11DARKORANGE,  X11PURPLE,  OLIVE,
    X11DARKGOLDENROD, X11PINK,
    Color.DARK_GRAY, Color.LIGHT_GRAY, Color.BLACK};

    
    final private int numberColours;
    
    /**
     * Integer used to separate bathes of colours in variable colour scheme.
     * <br> Negative if fixed colours being used.
     */
    final private int shadeScale;
    public Color [] javaColour;
        

    /**
     * Defines colours to be all known fixed colours.
     * <br>Colours run from 0 (white) to <tt>numberColours</tt> =black.
     */
    public JavaColours(){
            numberColours=TIMCOLOUR.length-1;
            shadeScale = -1; // indicates fixed scheme 
            javaColour = new Color[numberColours+1];
            //for (int c=0; c<=numberColours; c++) javaColour[c] = TIMCOLOUR[c];
            System.arraycopy(TIMCOLOUR, 0, javaColour, 0, TIMCOLOUR.length);
        }

    
    /**
     * Defines suitable colours.
     * <br>Colours run from 0 (white) to <tt>nc</tt> 
     * provided this does not exceed the number of fixed colours if that scheme is used.
     * @param nc number of colours 
     * @param fixed true (false) if want fixed (variable) colour scheme.
     */
    public JavaColours(int nc, boolean fixed){
        if (fixed && nc<TIMCOLOUR.length) {
            //System.out.println("!!! Using Fixed Colour scheme");
            shadeScale = -1; // indicates fixed scheme 
            numberColours=nc;
            //if (nc>=FIXEDCOLOUR.length) numberColours=FIXEDCOLOUR.length-1;
            javaColour = new Color[numberColours+1];
            javaColour[0]=Color.WHITE;
            javaColour[numberColours]=Color.BLACK;
//            for  (int c=1; c<numberColours; c++) javaColour[c] = TIMCOLOUR[c];
            System.arraycopy(TIMCOLOUR, 1, javaColour, 1, numberColours - 1);
        }
        else {
            //System.out.println("!!! Using Variable Colour scheme");
            
            if (nc>=MAXDEVIATION*NUMBERSHADES) numberColours=MAXDEVIATION*NUMBERSHADES;
            else numberColours=nc;
            if ((numberColours-2)<NUMBERSHADES) shadeScale=0;
                else {int divisions = (numberColours-2)/NUMBERSHADES;
                shadeScale = Math.round(MAXDEVIATION/((float) divisions));}
            setColours();
        }
    }
        
    /**
     * Gives the number of colours.
     * <p>WHITE is colour 0, BLACK is colour <tt>numberColours</tt>
     * In between are different shades.
     * @return maximum number of colours allowed
     */
    public int getNumberColours(){return numberColours;}
    
    /**
     * Returns a Java Colour.
     * <p>If index too small, returns WHITE, iff too big gives BLACK.
     * @param c index of colour, should be between 0 and numberColours inclusive.
     * @return java colour
     */
    public Color getColour(int c){
        if (c<0) return javaColour[0];
        if (c>=numberColours) return javaColour[numberColours];
        return javaColour[c];
    }
    
    
    private void setColours(){
        javaColour = new Color[numberColours+1];
        javaColour[0]=Color.WHITE;
        javaColour[numberColours]=Color.BLACK;
        for (int c=1; c<numberColours; c++) javaColour[c] = selectColour(c);
    }
    
    /**
     * Selects a colour from the variable palette.
     * <p>Less than or equal to zero gives WHITE, 
     * greater than or equal to <tt>numberColours</tt> gives BLACK
     * In between, colours are done in batches of <code>NUMBERSHADES</code>.
     * Exception is when c=7 which the algorithm would give as 255,255,255 = WHITE 
     * which is translated to black.
     * @param c colour number
     * @return a colour
     */
    public Color selectColour (int c){
            if (c>=numberColours) return Color.BLACK;
            if (c<=0) return Color.WHITE;
            int s= 255- ((c-1)/NUMBERSHADES)*shadeScale;
            Color nc = Color.BLACK;
            switch ((c-1)%NUMBERSHADES){
                case 0: nc = new Color(s,0,0); break;
                case 1: nc = new Color(0,s,0); break;
                case 2: nc = new Color(0,0,s); break;
                case 3: nc = new Color(s,s,0); break;
                case 4: nc = new Color(0,s,s); break;
                case 5: nc = new Color(s,0,s); break;
                case 6: if (s<255) nc = new Color(s,s,s); 
                else nc=Color.BLACK; break; 
            }
            return nc;
    }
    
   
/**
 * Returns rgb of colour as a hexadecimal string
 * @param c colour
 * @return colour as RGB hexadecimal string (no leading hash symbol)
 */
    public String RGB(int c){
        Color nc = javaColour[c%javaColour.length];
        return RGB(nc);}
    
    /**
     * Returns rgb of colour as a hexadecimal string.
     * @param nc colour
     * @return colour as RGB hexadecimal string (no leading hash symbol)
     */
    static public String RGB(Color nc){
        int red = nc.getRed();
        int green = nc.getGreen();
        int blue = nc.getBlue();
        //System.out.println("c="+c+" RGB = "+red+"."+green+"."+blue);
        String s = String.format("%02x%02x%02x", red,green,blue);
        return s;
    }

    /**
     * Returns rgb of colour as a hexadecimal string.
     * @param nc colour
     * @return colour as RGB hexadecimal string (no leading hash symbol)
     */
    static public String RGB(Color cmin, Color cmax, double v){
        int red   = shadeSpectrum(cmin.getRed(),cmax.getRed(),v);
        int green = shadeSpectrum(cmin.getGreen(),cmax.getGreen(),v);
        int blue  = shadeSpectrum(cmin.getBlue(),cmax.getBlue(),v);
        String s = String.format("%02x%02x%02x", red,green,blue);
        return s;
    }
    /**
     * Returns legal shade on scale 0 to 255.
     * <p>Given two int values for shade of one hue (red, green, blue, alpha)
     * returns a legal colour on scale 0 to 255 where the fraction linearly interpolates between
     * given values. Any result below 0 is rounded up to 0
     * and likewsie above 255 are returned as 255.  Thus
     * all input values wil retunr a legal hue.
     * @param cmin minimum hue (fraction =0)
     * @param cmax maximum hue (fraction =1)
     * @param fraction scaling factor
     * @return integer between 0 and 2555 representing a hue
     */
    static int shadeSpectrum(int cmin, int cmax, double fraction){
        int h= (int) Math.round(cmin*(1-fraction)+cmax*fraction);
        if (h<0) return 0;
        if (h>255) return 255;
        return h;
    }

   /** Returns a Java Color.
     * @param c integer if negative or 10 or more colour is BLACK.
     * @return returns a java colour
     */
    public Color  getFixedColour(int c){
        if ((c>=FIXEDCOLOUR.length) || (c>=numberColours) ) return Color.BLACK;
        if (c<=0) return Color.WHITE;
        return FIXEDCOLOUR[c]; 
    }

   /** Returns a Tim defined Color.
     * @param c integer if negative or 10 or more colour is BLACK.
     * @return returns a java colour
     */
    public Color  getTimColour(int c){
        if ((c>=TIMCOLOUR.length) || (c>=numberColours) ) return Color.BLACK;
        if (c<=0) return Color.WHITE;
        return TIMCOLOUR[c]; 
    }

    
    public void printColourInfo(PrintStream PS){
        PS.println("Number of Colours "+numberColours+", using "+(this.shadeScale<0?"fixed":"variable")+" colour scheme");
    }
     
    public void printColour(PrintStream PS, int c){
        Color nc = javaColour[c];
        PS.println("Colour "+c+", R="+nc.getRed()+", G="+nc.getGreen()+", B="+nc.getBlue()+", RGB="+RGB(c));
    }
     
    public void printAllColours(PrintStream PS){
        for (int c=0; c<=numberColours; c++) printColour(PS, c);
    }
     
    
}
