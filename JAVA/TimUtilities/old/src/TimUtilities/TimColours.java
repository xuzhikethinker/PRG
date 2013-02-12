/*
 * TimColours.java
 *
 * Created on 10 August 2007, 12:29
 *
 * The RGB system Java uses combines red, green, and blue  
 * in amounts represented by a number from 0 to 255. 
 * For example, red is (255, 0, 0) -- 255 units of red, no green, and no blue. 
 *  White is (255, 255, 255) and black is (0,0,0).
 * Predefined colours exist (most also in uppercase):-
 * Color.black, Color.darkGray, Color.gray, Color.lightGray, Color.white, 
 * Color.magenta, Color.red, Color.pink, Color.orange, Color.yellow, 
 * Color.green, Color.cyan, Color.blue.
 * */

package TimUtilities;


import java.awt.Color;

/**
 * Sets up colours.
 * @author time
 */
public class TimColours {
    int numberJavaColours=10;
    
    /** Creates a new instance of TimColours */
    public TimColours() {
    }
    

    /** Returns a Java Color.
     * @param c integer if negative or 10 or more colour is black.
     * @return returns a java colour
     */
    public Color  getColourAlways(int c){
//    Color purple = new Color(255,0,255);
        switch(c) {
        case 0: return Color.white; 
        case 1: return Color.yellow; 
        case 2: return Color.pink; 
        case 3: return Color.cyan; 
        case 4: return Color.orange; 
        case 5: return Color.magenta; 
        case 6: return  new Color(255,0,255); 
        case 7: return Color.green; 
        case 8: return Color.blue; 
        case 9: return Color.red; 
            default:
            case 10: return Color.black; 
        }
       
}// eo getColour
    
}
