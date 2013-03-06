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
     * <br>note that the Wikipedia page on Web Safe colours indicates that we should be separating the 
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

    /**
     * Web safe colours.
     * First is black, last is white. Taken from 
     * {@link http://mindprod.com/jgloss/netscapewebsafe.html}
     */
    public final static int [] webSafeColours={
        0x000000, 0x000033, 0x000066, 0x000099, 0x0000cc, 0x0000ff,
        0x003300, 0x003333, 0x003366, 0x003399, 0x0033cc, 0x0033ff,
        0x006600, 0x006633, 0x006666, 0x006699, 0x0066cc, 0x0066ff,
        0x009900, 0x009933, 0x009966, 0x009999, 0x0099cc, 0x0099ff,
        0x00cc00, 0x00cc33, 0x00cc66, 0x00cc99, 0x00cccc, 0x00ccff,
        0x00ff00, 0x00ff33, 0x00ff66, 0x00ff99, 0x00ffcc, 0x00ffff,
        0x330000, 0x330033, 0x330066, 0x330099, 0x3300cc, 0x3300ff,
        0x333300, 0x333333, 0x333366, 0x333399, 0x3333cc, 0x3333ff,
        0x336600, 0x336633, 0x336666, 0x336699, 0x3366cc, 0x3366ff,
        0x339900, 0x339933, 0x339966, 0x339999, 0x3399cc, 0x3399ff,
        0x33cc00, 0x33cc33, 0x33cc66, 0x33cc99, 0x33cccc, 0x33ccff,
        0x33ff00, 0x33ff33, 0x33ff66, 0x33ff99, 0x33ffcc, 0x33ffff,
        0x660000, 0x660033, 0x660066, 0x660099, 0x6600cc, 0x6600ff,
        0x663300, 0x663333, 0x663366, 0x663399, 0x6633cc, 0x6633ff,
        0x666600, 0x666633, 0x666666, 0x666699, 0x6666cc, 0x6666ff,
        0x669900, 0x669933, 0x669966, 0x669999, 0x6699cc, 0x6699ff,
        0x66cc00, 0x66cc33, 0x66cc66, 0x66cc99, 0x66cccc, 0x66ccff,
        0x66ff00, 0x66ff33, 0x66ff66, 0x66ff99, 0x66ffcc, 0x66ffff,
        0x990000, 0x990033, 0x990066, 0x990099, 0x9900cc, 0x9900ff,
        0x993300, 0x993333, 0x993366, 0x993399, 0x9933cc, 0x9933ff,
        0x996600, 0x996633, 0x996666, 0x996699, 0x9966cc, 0x9966ff,
        0x999900, 0x999933, 0x999966, 0x999999, 0x9999cc, 0x9999ff,
        0x99cc00, 0x99cc33, 0x99cc66, 0x99cc99, 0x99cccc, 0x99ccff,
        0x99ff00, 0x99ff33, 0x99ff66, 0x99ff99, 0x99ffcc, 0x99ffff,
        0xcc0000, 0xcc0033, 0xcc0066, 0xcc0099, 0xcc00cc, 0xcc00ff,
        0xcc3300, 0xcc3333, 0xcc3366, 0xcc3399, 0xcc33cc, 0xcc33ff,
        0xcc6600, 0xcc6633, 0xcc6666, 0xcc6699, 0xcc66cc, 0xcc66ff,
        0xcc9900, 0xcc9933, 0xcc9966, 0xcc9999, 0xcc99cc, 0xcc99ff,
        0xcccc00, 0xcccc33, 0xcccc66, 0xcccc99, 0xcccccc, 0xccccff,
        0xccff00, 0xccff33, 0xccff66, 0xccff99, 0xccffcc, 0xccffff,
        0xff0000, 0xff0033, 0xff0066, 0xff0099, 0xff00cc, 0xff00ff,
        0xff3300, 0xff3333, 0xff3366, 0xff3399, 0xff33cc, 0xff33ff,
        0xff6600, 0xff6633, 0xff6666, 0xff6699, 0xff66cc, 0xff66ff,
        0xff9900, 0xff9933, 0xff9966, 0xff9999, 0xff99cc, 0xff99ff,
        0xffcc00, 0xffcc33, 0xffcc66, 0xffcc99, 0xffcccc, 0xffccff,
        0xffff00, 0xffff33, 0xffff66, 0xffff99, 0xffffcc, 0xffffff};

    /**
     * Number of colours.
     */
    final private int numberColours;
    
    /**
     * Integer used to separate bathes of colours in variable colour scheme.
     * <br> Negative if fixed colours being used.
     */
    final private int shadeScale;
    /**
     * Name of scheme used to set colours.
     */
    final private int colourSchemeMode;
    /**
     * Name of scheme used to set colours.
     */
    final private String [] colourSchemeNames={"fixed","web safe","variable"};
    
    public Color [] javaColour;
        

    /**
     * Defines colours to be all known fixed colours.
     * <br>Colours run from 0 (white) to <tt>numberColours</tt> =black.
     */
    public JavaColours(){
            numberColours=TIMCOLOUR.length-1;
            colourSchemeMode=0;
            shadeScale = -1; // indicates fixed scheme 
            javaColour = new Color[numberColours+1];
            //for (int c=0; c<=numberColours; c++) javaColour[c] = TIMCOLOUR[c];
            System.arraycopy(TIMCOLOUR, 0, javaColour, 0, TIMCOLOUR.length);
        }

    
    /**
     * Defines suitable colours.
     * <br>Colours run from 0 (white) to <tt>nc</tt> with <tt>(nc+1)</tt> (black). 
     * If the number of colours requested <tt>nc</tt> is not correct for requested
     * mode less than 2 or too many for a fixed or web safe scheme) then
     * too large for fixed or web safe mode) then largest value 
     * for that mode is assumed.
     * @param nc number of colours (use less than 2 to get maximum number for mode)
     * @param mode index of colour scheme to use (default fixed)
     * {@link #colourSchemeNames}
     */
    public JavaColours(int nc, int mode){
        colourSchemeMode=mode;
//        if (mode==0 && nc>=TIMCOLOUR.length) colourSchemeMode=1;
//        if (mode==1 && nc>=webSafeColours.length) colourSchemeMode=2;
        switch (mode){
            case 2: // System.out.println("!!! Using variable colour scheme");
                if (nc>=MAXDEVIATION*NUMBERSHADES) numberColours=MAXDEVIATION*NUMBERSHADES;
                else numberColours=nc;
                if ((numberColours-2)<NUMBERSHADES) shadeScale=0;
                    else {
                          int divisions = (numberColours-2)/NUMBERSHADES;
                          shadeScale = Math.round(MAXDEVIATION/((float) divisions));}
                setColoursVariable();
                break;
            case 1: // System.out.println("!!! Using websafe colour scheme");   
                if (nc<2 || nc>=webSafeColours.length) numberColours=webSafeColours.length;
                else numberColours=nc;
                javaColour = new Color[numberColours+1];
                javaColour[0]=Color.WHITE;
                javaColour[numberColours]=Color.BLACK;
                int ccc;
                for (int c=1; c<numberColours; c++) {
                    ccc=(((numberColours-c)*(webSafeColours.length-1))/numberColours) & 0xFFFFFF;
                    javaColour[c]=new Color(webSafeColours[ccc] );
                }
                shadeScale=-1;
                break;
            default:
            case 0: //System.out.println("!!! Using Fixed Colour scheme");
                shadeScale = -1; // indicates fixed scheme 
                if (nc<2 || nc>=TIMCOLOUR.length) numberColours=TIMCOLOUR.length;
                else numberColours=nc;
                //if (nc>=FIXEDCOLOUR.length) numberColours=FIXEDCOLOUR.length-1;
                javaColour = new Color[numberColours+1];
                javaColour[0]=Color.WHITE;
                javaColour[numberColours]=Color.BLACK;
                System.arraycopy(TIMCOLOUR, 1, javaColour, 1, numberColours - 1);
                break;
                
        }
    }


    /**
     * Defines suitable colours.
     * <br>Colours run from 0 (white) to <tt>nc</tt> 
     * provided this does not exceed the number of fixed colours if that scheme is used.
     * @param nc number of colours 
     * @param fixed true (false) if want fixed (variable) colour scheme.
     * @deprecated use {@link #JavaColours(int, int) }
     */
    public JavaColours(int nc, boolean fixed){
        if (fixed && nc<TIMCOLOUR.length) {
            colourSchemeMode=0;
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
            colourSchemeMode=1;
            if (nc>=MAXDEVIATION*NUMBERSHADES) numberColours=MAXDEVIATION*NUMBERSHADES;
            else numberColours=nc;
            if ((numberColours-2)<NUMBERSHADES) shadeScale=0;
                else {
                      int divisions = (numberColours-2)/NUMBERSHADES;
                      shadeScale = Math.round(MAXDEVIATION/((float) divisions));}
            setColoursVariable();
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
    
    /**
     * Sets up list of variable colours
     */
    private void setColoursVariable(){
        javaColour = new Color[numberColours+1];
        javaColour[0]=Color.WHITE;
        javaColour[numberColours]=Color.BLACK;
        for (int c=1; c<numberColours; c++) javaColour[c] = selectColourVariable(c);
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
    public Color selectColourVariable (int c){
            if (c>=numberColours) return Color.BLACK;
            if (c<=0) return Color.WHITE;
            int s= 255- ((c-1)/NUMBERSHADES)*shadeScale;
            s=s & 0xFF; // ensures legal colour
            Color nc = Color.BLACK;
            switch ((c-1)%NUMBERSHADES){
                case 0: nc = new Color(s,0,0); break;
                case 1: nc = new Color(0,s,0); break;
                case 2: nc = new Color(0,0,s); break;
                case 3: nc = new Color(s,s,0); break;
                case 4: nc = new Color(0,s,s); break;
                case 5: nc = new Color(s,0,s); break;
                default: if ((s<255) && (s>=0)) nc = new Color(s,s,s); 
                          else nc=Color.BLACK; 
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
