/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

import java.io.PrintStream;


/**
 * Class to produce KMLGenerator format files for Google Earth.
 * <br>Remember x coord = E/W = Longitude (+/-) = 2nd coordinate in Google Earth (close to zero in Europe)
 * <br>Remember y coord = N/S = Latitude (+/-)  = 1st coordinate in Google Earth
 * @author time
 */
public class KMLGenerator {
    
    final static String firstLine="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    final static String secondLine="<kml xmlns=\"http://www.opengis.net/kml/2.2\">";

    /**
     * Constructor.
     */
    public  KMLGenerator(){
        
    }
    
    public static String startTag(String s){return "<"+s+">";  }
    public static String endTag(String s){return "</"+s+">";  }
    
    public static void printlnStartTag(PrintStream PS,String s){PS.println(startTag(s)); }
    public static void printlnEndTag(PrintStream PS,String s){PS.println(endTag(s));}
    
    public static void printInitialLines(PrintStream PS ){
        PS.println(firstLine);
        PS.println(secondLine);
        printlnStartTag(PS,"Document");
    }
    
    
    public static void printFinalLines(PrintStream PS ){
        printlnEndTag(PS,"Document");        
        printlnEndTag(PS,"kml");
    }
    
    public static void printTextTag(PrintStream PS, String tagName, String text ){
        printlnStartTag(PS,tagName);
        PS.println(text);
        printlnEndTag(PS,tagName);   
    }

    public static String textTag(String tagName, String text ){
        return startTag(tagName)+text+endTag(tagName);   
    }

    /**
     * Icon definition.
     * Put at start of document just after document tag.
     * @param PS Printstream
     * @param placemarkIcon name to use for icon
     * @param placemarkIconFile file for icon e.g. <href>http://maps.google.com/mapfiles/kml/shapes/donut.png</href>
     */
    public static void printIconDefinition(PrintStream PS, String placemarkIcon, String placemarkIconFile){
        printTextTag(PS, "Style id=",placemarkIcon);
        printlnStartTag(PS,"IconStyle");
        printlnStartTag(PS,"Icon");
        printTextTag(PS, "href",placemarkIconFile);
        printlnEndTag(PS,"Icon");
        printlnEndTag(PS,"IconStyle");
        printlnEndTag(PS,"Style");
    }

    /**
     * PinkRing Icon definition.
     * Put at start of document just after document tag.
     * @see http://maps.google.com/mapfiles/kml/shapes/donut.png
     * @param PS Printstream
     * @return name to use for placemarkIcon 
     */
    public static String printIconDefinitionPinkRing(PrintStream PS){
        String placemarkIcon="PinkRing";
        printIconDefinition(PS, placemarkIcon, "http://maps.google.com/mapfiles/kml/shapes/donut.png", "ffff00ff","2");
        return placemarkIcon;
    }

    /**
     * Icon definition.
     * Put at start of document just after document tag.
     * @param PS Printstream
     * @param placemarkIcon name to use for icon
     * @param placemarkIconFile file for icon e.g. <href>http://maps.google.com/mapfiles/kml/shapes/donut.png</href>
     * @param colour colour to use as 8 character, 4 hex codes, no spaces of leading hash
     * @param scale floating point value
     */
    public static void printIconDefinition(PrintStream PS,
            String placemarkIcon, String placemarkIconFile,
            String colour, String scale){
        printTextTag(PS, "Style id=",placemarkIcon);
        printTextTag(PS, "Color",colour);
        printTextTag(PS, "Scale",scale);
        printlnStartTag(PS,"IconStyle");
        printlnStartTag(PS,"Icon");
        printTextTag(PS, "href",placemarkIconFile);
        printlnEndTag(PS,"Icon");
        printlnEndTag(PS,"IconStyle");
        printlnEndTag(PS,"Style");
    }

    public static void printName(PrintStream PS, String name){
        printTextTag(PS, "name", name);    
    } 
    
    public static void printDescription(PrintStream PS, String description){
        printTextTag(PS, "description", description);    
    } 
    /**
     * 
     * @param PS Printstream
     * @param latitude
     * @param longitude
     * @param altitude in metres
     */
    public static void printLookAt(PrintStream PS, double latitude, double longitude, double altitude){
        String tagName="LookAt";
        printlnStartTag(PS,tagName);
        PS.println(textTag("longitude",Double.toString(longitude)));
        PS.println(textTag("latitude",Double.toString(latitude)));
        PS.println(textTag("altitude",Double.toString(altitude)));
        printlnEndTag(PS,tagName);   
    }
        
    public static void printPlacemarkStart(PrintStream PS){
        printlnStartTag(PS,"Placemark");        
    }
    public static void printPlacemarkStart(PrintStream PS, String name, String description){
        printlnStartTag(PS,"Placemark");
        printTextTag(PS,"name",name);
        printTextTag(PS,"description",description);
    }
    /**
     * Prints code for a placemark.
     * @param PS Printstream such as System.out
     * @param name label for placemark
     * @param description popup description to give
     * @param placemarkIcon url for icon
     */
    public static void printPlacemarkStart(PrintStream PS, String name, String description, String placemarkIcon){
        printlnStartTag(PS,"Placemark");
        printTextTag(PS,"name",name);
        printTextTag(PS,"description",description);
        printTextTag(PS,"styleURL",placemarkIcon);
    }
    public static void printPlacemarkEnd(PrintStream PS){
        printlnEndTag(PS,"Placemark");        
    }

    public static void printPlacemarkPoint(PrintStream PS, String name, String description, double latitude, double longitude){
        printPlacemarkStart(PS,name,description);
        printPoint(PS,latitude, longitude);
        printPlacemarkEnd(PS);
    }
    public static void printPlacemarkPoint(PrintStream PS,
            String name, String description, String placemarkIcon,
            double latitude, double longitude){
        printPlacemarkStart(PS,name,description,placemarkIcon);
        printPoint(PS,latitude, longitude);
        printPlacemarkEnd(PS);
    }

    public static void printPoint(PrintStream PS, double latitude, double longitude){
        String tagName="Point";
        printlnStartTag(PS,tagName);
        printCoordinates(PS, latitude, longitude);
        printlnEndTag(PS,tagName);   
    }

    /**
     * Note that the longitude, l;atitude then altitude is the correct order
     * @param latitude
     * @param longitude
     * @return string representing coordinate
     */
    public static String coordinateString(double latitude, double longitude){
        return longitude+", "+latitude+", 0";   
    }

    public static void printCoordinates(PrintStream PS, double latitude, double longitude){
        String tagName="coordinates";
        PS.println(startTag(tagName)+coordinateString(latitude,longitude)+endTag(tagName));   
    }
    
   public static void printCoordinates(PrintStream PS, double latitude1, double longitude1, double latitude2, double longitude2){
        String tagName="coordinates";
        printlnStartTag(PS,tagName);
        PS.println(coordinateString(latitude1, longitude1));
        PS.println(coordinateString(latitude2, longitude2));
        printlnEndTag(PS,tagName);      
    }

   public static void printCoordinates(PrintStream PS, double [] latitude, double [] longitude){
        String tagName="coordinates";
        printlnStartTag(PS,tagName);
        for (int c=0; c<latitude.length; c++) PS.println(coordinateString(latitude[c], longitude[c]));
        printlnEndTag(PS,tagName);      
    }
    
   public static void printPlacemarkLine(PrintStream PS, boolean tesselateOn, String name, String description, double latitude1, double longitude1, double latitude2, double longitude2){
       printPlacemarkStart(PS,name,description);
       String tagName="LineString";
       printlnStartTag(PS,tagName);
       PS.println(startTag("tessellate")+(tesselateOn?1:0)+endTag("tessellate"));
       printCoordinates(PS,latitude1,longitude1,latitude2,longitude2);
       printlnEndTag(PS,tagName);      
       printPlacemarkEnd(PS);        
   }
   
   

    
}
