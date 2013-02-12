/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimUtilities;

import java.util.Calendar;

/**
 *
 * @author time
 */
public class TimTime {
 Integer date;
 Integer month;
 Integer year;
 Integer hour;
 Integer minute;
    
 /**
  * Sets the date information from a Calendar instance
  * @param sep separator between time and date sections.
  */
    public TimTime(String sep)
    {
        setDateTime(sep);
    }

 
 /**
  * Sets the date information from a Calendar instance
  * @param cal calendar with date of interest
  * @param sep separator between time and date sections.
  * @return six character string representing date as <tt>yymmdd</tt>
  */
 public String setDateTime(Calendar cal, String sep){
     date=cal.get(Calendar.DATE);
     month=cal.get(Calendar.MONTH)+1; // months counted from 0
     year=cal.get(Calendar.YEAR);
     hour=cal.get(Calendar.HOUR_OF_DAY);
     minute=cal.get(Calendar.MINUTE);
     return fullString(sep);
 }
 /**
  * Sets the date information from crrent time and date.
  * @param sep separator between time and date sections.
  * @return six character string representing date as <tt>yymmdd</tt>
  */
 public String setDateTime(String sep){
     return setDateTime(Calendar.getInstance(), sep);
 }
     
 /**
  * Returns date in six character format <tt>yymmdd</tt>.
  * @return six character string representing date as yymmdd
  */
 public String dateString(){
     String s= year.toString().substring(2);
     String s1= month.toString();
     if (s1.length()<2) s=s+"0"+s1; else s=s+s1;
     s1= date.toString();
     if (s1.length()<2) s=s+"0"+s1; else s=s+s1;
     return s;
 }
 
 /**
  * Returns time in four character format <tt>hhmm</tt>.
  * @return time in four character format <tt>hhmm</tt>
  */
 public String timeString(){
     String s="";
     String s1= hour.toString();
     if (s1.length()<2) s="0"+s1; else s=s1;
     s1= minute.toString();
     if (s1.length()<2) s=s+"0"+s1; else s=s+s1;
     return s;
 }
 
  /**
  * Gives string with date and time in format <tt>yymmdd</tt>+sep+<tt>hhmm</tt>.
   * @param sep separator between time and date sections.
  * @return date and time in format <tt>yymmdd</tt>+sep+<tt>hhmm</tt>
  */
 public String fullString(String sep){
     return dateString()+sep+timeString();
 }

 
}
