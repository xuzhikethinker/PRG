
/*
    Some versions of java do not implement standard input.  Many of
    my programming examples use standard input, through a class named
    TextIO.  This file is an attempt to solve the problem of the
    missing standing input.  It can also be used if you simply prefer
    to have a GUI-style window for doing text input/output.
    
                              * * *

    The class in this file named TextIO is an alternative for another
    class of the same name.  The original TextIO provided an easy
    interface to standard input and standard output.  This version
    defines exactly the same functions and subroutines, but now
    they do input and output in a window that is opened automatically
    when this class is used.  You can use the class in this file
    as a substitute for the original TextIO.  A program that does
    input and output exclusively by using TextIO will work with
    this version of TextIO without any changes.  If the program
    uses System.out, you have two choices:  Either change references
    to System.out.print to TextIO.put and System.out.println to
    TextIO.putln.  Or add the subroutine call
    
                     TextIO.captureStandardOutput();
                    
    at the beginning of the main() routine.  After this line is
    executed, the output from System.out.print and System.out.println
    will go to the window, instead of to their usual destination.
    (TextIO.captureStandardOutput might not work in all versions of Java.)
    
    One thing about this class:  The window will not close when the
    program ends.  You will have to close it yourself by hand, by clicking
    on its close box.  (Alternatively, you could add the line
    System.exit(0); to the end of your main() routine.)  Actually, this
    is not so bad, since it gives you a chance to read all of the
    program's output.
    
    When you compile this file, you'll get five .class files:
    TextIO.class, TextIO$ConsoleCanvas.class, TextIO$1.class,
    TextIO$2.class, and TextIO$TextIOConsole.class.  You need all
    these classes to run your programs.

    
    Written by:  David Eck
                 Department of Mathematics and Computer Science
                 Hobart and William Smith Colleges
                 Geneva, NY 14456
                 Email:  eck@hws.edu
                 WWW:  http://math.hws.edu/eck/

*/

import java.io.*;
import java.awt.*;
import java.awt.event.*;
   
public class TextIO {

   /* The following is the only public routine in this version of TextIO that
      was not in the original version.  It can be used to redirect the
      output that would usually go to System.out to the GUI window used
      by this class.  It will probably not work in all versions of Java.
   */

   public static void captureStandardOutput() {
       OutputStream out = new OutputStream() {
             public void write(int b)  throws IOException {
                char ch = (char)(b & 0x7F);
                if (ch == '\n' || ch == '\r')
                   console.putCR();
                else if (ch >= 0x20 && ch != 0x7F)
                   console.putCh(ch);
             }
          };
       try {
          System.setOut(new PrintStream(out));
       }
       catch (Exception e) {
       }
   }

   // *************************** I/O Methods *********************************
   
         // Methods for writing the primitive types, plus type String,
         // to the console, with no extra spaces.
         //
         // Note that the real-number data types, float
         // and double, a rounded version is output that will
         // use at most 10 or 11 characters.  If you want to
         // output a real number with full accuracy, use
         // "TextIO.put(String.valueOf(x))", for example.
         
   public static void put(int x)     { put(x,0); }   // Note: also handles byte and short!
   public static void put(long x)    { put(x,0); }
   public static void put(double x)  { put(x,0); }   // Also handles float.
   public static void put(char x)    { put(x,0); }
   public static void put(boolean x) { put(x,0); }
   public static void put(String x)  { put(x,0); }


         // Methods for writing the primitive types, plus type String,
         // to the console,followed by a carriage return, with
         // no extra spaces.

   public static void putln(int x)      { put(x,0); newLine(); }  // Note: also handles byte and short!
   public static void putln(long x)     { put(x,0); newLine(); }
   public static void putln(double x)   { put(x,0); newLine(); }  // Also handles float.
   public static void putln(char x)     { put(x,0); newLine(); }
   public static void putln(boolean x)  { put(x,0); newLine(); }
   public static void putln(String x)   { put(x,0); newLine(); }
  

         // Methods for writing the primitive types, plus type String,
         // to the console, with a minimum field width of w,
         // and followed by a carriage  return.
         // If output value is less than w characters, it is padded
         // with extra spaces in front of the value.

   public static void putln(int x, int w)     { put(x,w); newLine(); }   // Note: also handles byte and short!
   public static void putln(long x, int w)    { put(x,w); newLine(); }
   public static void putln(double x, int w)  { put(x,w); newLine(); }   // Also handles float.
   public static void putln(char x, int w)    { put(x,w); newLine(); }
   public static void putln(boolean x, int w) { put(x,w); newLine(); }
   public static void putln(String x, int w)  { put(x,w); newLine(); }


          // Method for outputting a carriage return

   public static void putln() { newLine(); }
   

         // Methods for writing the primitive types, plus type String,
         // to the console, with minimum field width w.
   
   public static void put(int x, int w)     { dumpString(String.valueOf(x), w); }   // Note: also handles byte and short!
   public static void put(long x, int w)    { dumpString(String.valueOf(x), w); }
   public static void put(double x, int w)  { dumpString(realToString(x), w); }     // Also handles float.
   public static void put(char x, int w)    { dumpString(String.valueOf(x), w); }
   public static void put(boolean x, int w) { dumpString(String.valueOf(x), w); }
   public static void put(String x, int w)  { dumpString(x, w); }
   
   
         // Methods for reading in the primitive types, plus "words" and "lines".
         // The "getln..." methods discard any extra input, up to and including
         //    the next carriage return.
         // A "word" read by getlnWord() is any sequence of non-blank characters.
         // A "line" read by getlnString() or getln() is everything up to next CR;
         //    the carriage return is not part of the returned value, but it is
         //    read and discarded.
         // Note that all input methods except getAnyChar(), peek(), the ones for lines
         //    skip past any blanks and carriage returns to find a non-blank value.
         // getln() can return an empty string; getChar() and getlnChar() can 
         //    return a space or a linefeed ('\n') character.
         // peek() allows you to look at the next character in input, without
         //    removing it from the input stream.  (Note that using this
         //    routine might force the user to enter a line, in order to
         //    check what the next character is.)
         // Acceptable boolean values are the "words": true, false, t, f, yes,
         //    no, y, n, 0, or 1;  uppercase letters are OK.
         // None of these can produce an error; if an error is found in input,
         //    the user is forced to re-enter.
         // Available input routines are:
         //
         //            getByte()      getlnByte()    getShort()     getlnShort()
         //            getInt()       getlnInt()     getLong()      getlnLong()
         //            getFloat()     getlnFloat()   getDouble()    getlnDouble()
         //            getChar()      getlnChar()    peek()         getAnyChar()
         //            getWord()      getlnWord()    getln()        getString()    getlnString()
         //
         // (getlnString is the same as getln and is onlyprovided for consistency.)
   
   public static byte getlnByte()       { byte x=getByte();       emptyBuffer();  return x; }
   public static short getlnShort()     { short x=getShort();     emptyBuffer();  return x; }
   public static int getlnInt()         { int x=getInt();         emptyBuffer();  return x; }
   public static long getlnLong()       { long x=getLong();       emptyBuffer();  return x; }
   public static float getlnFloat()     { float x=getFloat();     emptyBuffer();  return x; }
   public static double getlnDouble()   { double x=getDouble();   emptyBuffer();  return x; }
   public static char getlnChar()       { char x=getChar();       emptyBuffer();  return x; }
   public static boolean getlnBoolean() { boolean x=getBoolean(); emptyBuffer();  return x; }
   public static String getlnWord()     { String x=getWord();     emptyBuffer();  return x; }
   public static String getlnString()   { return getln(); }  // same as getln()
   public static String getln() {
      StringBuffer s = new StringBuffer(100);
      char ch = readChar();
      while (ch != '\n') {
         s.append(ch);
         ch = readChar();
      }
      return s.toString();
   }
   
   
   public static byte getByte()   { return (byte)readInteger(-128L,127L); }
   public static short getShort() { return (short)readInteger(-32768L,32767L); }   
   public static int getInt()     { return (int)readInteger((long)Integer.MIN_VALUE, (long)Integer.MAX_VALUE); }
   public static long getLong()   { return readInteger(Long.MIN_VALUE, Long.MAX_VALUE); }
   
   public static char getAnyChar(){ return readChar(); }
   public static char peek()      { return lookChar(); }
   
   public static char getChar() {  // skip spaces & cr's, then return next char
      char ch = lookChar();
      while (ch == ' ' || ch == '\n') {
         readChar();
         if (ch == '\n')
            dumpString("? ",0);
         ch = lookChar();
      }
      return readChar();
   }

   public static float getFloat() {
      float x = 0.0F;
      while (true) {
         String str = readRealString();
         if (str.equals("")) {
             errorMessage("Illegal floating point input.",
                          "Real number in the range " + Float.MIN_VALUE + " to " + Float.MAX_VALUE);
         }
         else {
            Float f = null;
            try { f = Float.valueOf(str); }
            catch (NumberFormatException e) {
               errorMessage("Illegal floating point input.",
                            "Real number in the range " + Float.MIN_VALUE + " to " + Float.MAX_VALUE);
               continue;
            }
            if (f.isInfinite()) {
               errorMessage("Floating point input outside of legal range.",
                            "Real number in the range " + Float.MIN_VALUE + " to " + Float.MAX_VALUE);
               continue;
            }
            x = f.floatValue();
            break;
         }
      }
      return x;
   }
   
   public static double getDouble() {
      double x = 0.0;
      while (true) {
         String str = readRealString();
         if (str.equals("")) {
             errorMessage("Illegal floating point input",
                          "Real number in the range " + Double.MIN_VALUE + " to " + Double.MAX_VALUE);
         }
         else {
            Double f = null;
            try { f = Double.valueOf(str); }
            catch (NumberFormatException e) {
               errorMessage("Illegal floating point input",
                            "Real number in the range " + Double.MIN_VALUE + " to " + Double.MAX_VALUE);
               continue;
            }
            if (f.isInfinite()) {
               errorMessage("Floating point input outside of legal range.",
                            "Real number in the range " + Double.MIN_VALUE + " to " + Double.MAX_VALUE);
               continue;
            }
            x = f.doubleValue();
            break;
         }
      }
      return x;
   }
   
   public static String getWord() {
      char ch = lookChar();
      while (ch == ' ' || ch == '\n') {
         readChar();
         if (ch == '\n')
            dumpString("? ",0);
         ch = lookChar();
      }
      StringBuffer str = new StringBuffer(50);
      while (ch != ' ' && ch != '\n') {
         str.append(readChar());
         ch = lookChar();
      }
      return str.toString();
   }
   
   public static boolean getBoolean() {
      boolean ans = false;
      while (true) {
         String s = getWord();
         if ( s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t") ||
                 s.equalsIgnoreCase("yes")  || s.equalsIgnoreCase("y") ||
                 s.equals("1") ) {
              ans = true;
              break;
          }
          else if ( s.equalsIgnoreCase("false") || s.equalsIgnoreCase("f") ||
                 s.equalsIgnoreCase("no")  || s.equalsIgnoreCase("n") ||
                 s.equals("0") ) {
              ans = false;
              break;
          }
          else
             errorMessage("Illegal boolean input value.",
                          "one of:  true, false, t, f, yes, no, y, n, 0, or 1");
      }
      return ans;
   }
   
   // ***************** Everything beyond this point is private *******************
   
   // ********************** Utility routines for input/output ********************
   
   
   private static TextIOConsole console = new TextIOConsole();

 //  private static InputStream in = System.in;    // rename standard input stream
 //  private static PrintStream out = System.out;  // rename standard output stream

   private static String buffer = null;  // one line read from input
   private static int pos = 0;           // position of next char in input line that has
                                         //      not yet been processed


   private static String readRealString() {   // read chars from input following syntax of real numbers
      StringBuffer s=new StringBuffer(50);
      char ch=lookChar();
      while (ch == ' ' || ch == '\n') {
          readChar();
          if (ch == '\n')
             dumpString("? ",0);
          ch = lookChar();
      }
      if (ch == '-' || ch == '+') {
          s.append(readChar());
          ch = lookChar();
          while (ch == ' ') {
             readChar();
             ch = lookChar();
          }
      }
      while (ch >= '0' && ch <= '9') {
          s.append(readChar());
          ch = lookChar();
      }
      if (ch == '.') {
         s.append(readChar());
         ch = lookChar();
         while (ch >= '0' && ch <= '9') {
             s.append(readChar());
             ch = lookChar();
         }
      }
      if (ch == 'E' || ch == 'e') {
         s.append(readChar());
         ch = lookChar();
         if (ch == '-' || ch == '+') {
             s.append(readChar());
             ch = lookChar();
         }
         while (ch >= '0' && ch <= '9') {
             s.append(readChar());
             ch = lookChar();
         }
      }
      return s.toString();
   }

   private static long readInteger(long min, long max) {  // read long integer, limited to specified range
      long x=0;
      while (true) {
         StringBuffer s=new StringBuffer(34);
         char ch=lookChar();
         while (ch == ' ' || ch == '\n') {
             readChar();
             if (ch == '\n');
                dumpString("? ",0);
             ch = lookChar();
         }
         if (ch == '-' || ch == '+') {
             s.append(readChar());
             ch = lookChar();
             while (ch == ' ') {
                readChar();
                ch = lookChar();
             }
         }
         while (ch >= '0' && ch <= '9') {
             s.append(readChar());
             ch = lookChar();
         }
         if (s.equals("")){
             errorMessage("Illegal integer input.",
                          "Integer in the range " + min + " to " + max);
         }
         else {
             String str = s.toString();
             try { 
                x = Long.parseLong(str);
             }
             catch (NumberFormatException e) {
                errorMessage("Illegal integer input.",
                             "Integer in the range " + min + " to " + max);
                continue;
             }
             if (x < min || x > max) {
                errorMessage("Integer input outside of legal range.",
                             "Integer in the range " + min + " to " + max);
                continue;
             }
             break;
         }
      }
      return x;
   }
   
   private static String realToString(double x) {
         // Goal is to get a reasonable representation of x in at most
         // 10 characters, or 11 characters if x is negative.
      if (Double.isNaN(x))
         return "undefined";
      if (Double.isInfinite(x))
         if (x < 0)
            return "-INF";
         else
            return "INF";
      if (Math.abs(x) <= 5000000000.0 && Math.rint(x) == x)
         return String.valueOf( (long)x );
      String s = String.valueOf(x);
      if (s.length() <= 10)
         return s;
      boolean neg = false;
      if (x < 0) {
         neg = true;
         x = -x;
         s = String.valueOf(x);
      }
      if (x >= 0.00005 && x <= 50000000 && (s.indexOf('E') == -1 && s.indexOf('e') == -1)) {  // trim x to 10 chars max
         s = round(s,10);
         s = trimZeros(s);
      }
      else if (x > 1) { // construct exponential form with positive exponent
          long power = (long)Math.floor(Math.log(x)/Math.log(10));
          String exp = "E" + power;
          int numlength = 10 - exp.length();
          x = x / Math.pow(10,power);
          s = String.valueOf(x);
          s = round(s,numlength);
          s = trimZeros(s);
          s += exp;
      }
      else { // constuct exponential form
          long power = (long)Math.ceil(-Math.log(x)/Math.log(10));
          String exp = "E-" + power;
          int numlength = 10 - exp.length();
          x = x * Math.pow(10,power);
          s = String.valueOf(x);
          s = round(s,numlength);
          s = trimZeros(s);
          s += exp;
      }
      if (neg)
         return "-" + s;
      else
         return s;
   }
   
   private static String trimZeros(String num) {  // used by realToString
     if (num.indexOf('.') >= 0 && num.charAt(num.length() - 1) == '0') {
        int i = num.length() - 1;
        while (num.charAt(i) == '0')
           i--;
        if (num.charAt(i) == '.')
           num = num.substring(0,i);
        else
           num = num.substring(0,i+1);
     }
     return num;
   }
   
   private static String round(String num, int length) {  // used by realToString
      if (num.indexOf('.') < 0)
         return num;
      if (num.length() <= length)
         return num;
      if (num.charAt(length) >= '5' && num.charAt(length) != '.') {
         char[] temp = new char[length+1];
         int ct = length;
         boolean rounding = true;
         for (int i = length-1; i >= 0; i--) {
            temp[ct] = num.charAt(i); 
            if (rounding && temp[ct] != '.') {
               if (temp[ct] < '9') {
                  temp[ct]++;
                  rounding = false;
               }
               else
                  temp[ct] = '0';
            }
            ct--;
         }
         if (rounding) {
            temp[ct] = '1';
            ct--;
         }
         // ct is -1 or 0
         return new String(temp,ct+1,length-ct);
      }
      else 
         return num.substring(0,length);
      
   }
   private static void dumpString(String str, int w) {   // output string to console
      for (int i=str.length(); i<w; i++)
         console.putCh(' ');
      for (int i=0; i<str.length(); i++)
         if ((int)str.charAt(i) >= 0x20 && (int)str.charAt(i) != 0x7F)  // no control chars or delete
            console.putCh(str.charAt(i));
         else if (str.charAt(i) == '\n' || str.charAt(i) == '\r')
            newLine();
   }
   
   private static void errorMessage(String message, String expecting) {
                  // inform user of error and force user to re-enter.
       newLine();
       dumpString("  *** Error in input: " + message + "\n", 0);
       dumpString("  *** Expecting: " + expecting + "\n", 0);
       dumpString("  *** Discarding Input: ", 0);
       if (lookChar() == '\n')
          dumpString("(end-of-line)\n\n",0);
       else {
          while (lookChar() != '\n')
             console.putCh(readChar());
          dumpString("\n\n",0);
       }
       dumpString("Please re-enter: ", 0);
       readChar();  // discard the end-of-line character
   }

   private static char lookChar() {  // return next character from input
      if (buffer == null || pos > buffer.length())
         fillBuffer();
      if (pos == buffer.length())
         return '\n';
      return buffer.charAt(pos);
   }

   private static char readChar() {  // return and discard next character from input
      char ch = lookChar();
      pos++;
      return ch;
   }

   private static void newLine() {   // output a CR to console
      console.putCR();
   }

   private static boolean possibleLinefeedPending = false;

   private static void fillBuffer() {    // Wait for user to type a line and press return,
                                         // and put the typed line into the buffer.
      buffer = console.getLine();
      pos = 0;
   }

   private static void emptyBuffer() {   // discard the rest of the current line of input
      buffer = null;
   }

   private static class TextIOConsole extends Frame {
      
      private ConsoleCanvas canvas;

      public TextIOConsole() {
         super("TextIO Console");
         setBounds(20,40,560,400);
         setResizable(false);
         setFont(new Font("Courier",Font.PLAIN,12));
         canvas = new ConsoleCanvas();
         add(canvas,BorderLayout.CENTER);
         addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent evt) {
                 dispose();
                 System.exit(0);
              }
           });
         show();
      }
      
      public void putCh(char ch) {
         canvas.addChar(ch);
      }
      
      public void putCR() {
         canvas.addCR();
      }
      
      public String getLine() {
         return canvas.readLine();
      }
         
   }  // end class TextIOConsole
   
   

   
   private static class ConsoleCanvas extends Canvas implements FocusListener, KeyListener, MouseListener {
   
      /* A class that implements basic console-oriented input/output, 
         This is identical to my public class ConsoleCanvas
      */
   
      // public interface, constructor and methods
   
      public ConsoleCanvas() {
         addFocusListener(this);
         addKeyListener(this);
      }
   
      public final String readLine() {  // wait for user to enter a line of input;
                                        // Line can only contain characters in the range
                                        // ' ' to '~'.
         return doReadLine();
      }
   
      public final void addChar(char ch) {  // output PRINTABLE character to console
         putChar(ch);
      }
   
      public final void addCR() {  // add a CR to the console
         putCR();
      }
   
      public synchronized void clear() {  // clear console and return cursor to row 0, column 0.
         if (OSC == null)
            return;
         currentRow = 0;
         currentCol = 0;
         OSCGraphics.setColor(Color.white);
         OSCGraphics.fillRect(4,4,getSize().width-8,getSize().height-8);
         OSCGraphics.setColor(Color.black);
         repaint();
         try { Thread.sleep(25); }
         catch (InterruptedException e) { }
      }
      
    
      // focus and key event handlers; not meant to be called excpet by system
   
      public void keyPressed(KeyEvent evt) {
         doKey(evt.getKeyChar());
      }
      
      public void keyReleased(KeyEvent evt) { }
      public void keyTyped(KeyEvent evt) { }
   
      public void focusGained(FocusEvent evt) {
         doFocus(true);
      }
   
      public void focusLost(FocusEvent evt) {
         doFocus(false);
      }
      
      public boolean isFocusTraversable() {
           // Allows the user to move the focus to the canvas
           // by pressing the tab key.
         return true;
      }
   
      // Mouse listener methods -- here just to make sure that the canvas
      // gets the focuse when the user clicks on it.  These are meant to
      // be called only by the system.
   
      public void mousePressed(MouseEvent evt) {
         requestFocus();
      }
      
      public void mouseReleased(MouseEvent evt) { }
      public void mouseClicked(MouseEvent evt) { }
      public void mouseEntered(MouseEvent evt) { }
      public void mouseExited(MouseEvent evt) { }
   
   
      // implementation section: protected variables and methods.
   
      protected StringBuffer typeAhead = new StringBuffer();
                    // Characters typed by user but not yet processed;
                    // User can "type ahead" the charcters typed until
                    // they are needed to satisfy a readLine.
   
      protected final int maxLineLength = 256;
                    // No lines longer than this are returned by readLine();
                    // The system effectively inserts a CR after 256 chars
                    // of input without a carriage return.
   
      protected int rows, columns;  // rows and columns of chars in the console
      protected int currentRow, currentCol;  // current curson position
   
   
   
      protected Font font;      // Font used in console (Courier); All font
                                //   data is set up in the doSetup() method.
      protected int lineHeight; // height of one line of text in the console
      protected int baseOffset; // distance from top of a line to its baseline
      protected int charWidth;  // width of a character (constant, since a monospaced font is used)
      protected int leading;    // space between lines
      protected int topOffset;  // distance from top of console to top of text
      protected int leftOffset; // distance from left of console to first char on line
   
      protected Image OSC;   // off-screen backup for console display (except cursor)
      protected Graphics OSCGraphics;  // graphics context for OSC
   
      protected boolean hasFocus = false;  // true if this canvas has the input focus
      protected boolean cursorIsVisible = false;  // true if cursor is currently visible
   
   
      private int pos = 0;  // exists only for sharing by next two methods
      public synchronized void clearTypeAhead() {
         // clears any unprocessed user typing.  This is meant only to
         // be called by ConsolePanel, when a program being run by
         // console Applet ends.  But just to play it safe, pos is
         // set to -1 as a signal to doReadLine that it should return.
         typeAhead.setLength(0);
         pos = -1;
         notify();
      }
   
   
      protected synchronized String doReadLine() {  // reads a line of input, up to next CR
         if (OSC == null) {  // If this routine is called before the console has
                             // completely opened, we shouldn't procede; give the
                             // window a chance to open, so that paint() can call doSetup().
            try { wait(5000); }  // notify() should be set by doSetup()
            catch (InterruptedException e) {}
         }
         if (OSC == null)  // If nothing has happened for 5 seconds, we are probably in
                           //    trouble, but when the heck, try calling doSetup and proceding anyway.
            doSetup();
         if (!hasFocus)  // Make sure canvas has input focus
            requestFocus();
         StringBuffer lineBuffer = new StringBuffer();  // buffer for constructing line from user
         pos = 0;
         while (true) {  // Read and process chars from the typeAhead buffer until a CR is found.
            while (pos >= typeAhead.length()) {  // If the typeAhead buffer is empty, wait for user to type something
               cursorBlink();
               try { wait(500); }
               catch (InterruptedException e) { }
            }
            if (pos == -1) // means clearTypeAhead was called;
               return "";  // this is an abnormal return that should not happen
            if (cursorIsVisible)
               cursorBlink();
            if (typeAhead.charAt(pos) == '\r' || typeAhead.charAt(pos) == '\n') {
               putCR();
               pos++;
               break;
            }
            if (typeAhead.charAt(pos) == 8 || typeAhead.charAt(pos) == 127) {
               if (lineBuffer.length() > 0) {
                  lineBuffer.setLength(lineBuffer.length() - 1);
                  eraseChar();
               }
               pos++;
            }
            else if (typeAhead.charAt(pos) >= ' ' && typeAhead.charAt(pos) < 127) {
               putChar(typeAhead.charAt(pos));
               lineBuffer.append(typeAhead.charAt(pos));
               pos++;
            }
            else
               pos++;
            if (lineBuffer.length() == maxLineLength) {
               putCR();
               pos = typeAhead.length();
               break;
            }
         }
         if (pos >= typeAhead.length())  // delete all processed chars from typeAhead
            typeAhead.setLength(0);
         else {
            int len = typeAhead.length();
            for (int i = pos; i < len; i++)
               typeAhead.setCharAt(i - pos, typeAhead.charAt(i));
            typeAhead.setLength(len - pos);
         }
         return lineBuffer.toString();   // return the string that was entered
      }
   
      protected synchronized void doKey(char ch) {  // process key pressed by user
         typeAhead.append(ch);
         notify();
      }
   
      private void putCursor(Graphics g) {  // draw the cursor
         g.drawLine(leftOffset + currentCol*charWidth + 1, topOffset + (currentRow*lineHeight),
                    leftOffset + currentCol*charWidth + 1, topOffset + (currentRow*lineHeight + baseOffset));
      }
   
      protected synchronized void putChar(char ch) { // draw ch at cursor position and move cursor
         if (OSC == null) {  // If this routine is called before the console has
                             // completely opened, we shouldn't procede; give the
                             // window a chance to open, so that paint() can call doSetup().
            try { wait(5000); }  // notify() should be set by doSetup()
            catch (InterruptedException e) {}
         }
         if (OSC == null)  // If nothing has happened for 5 seconds, we are probably in
                           //    trouble, but when the heck, try calling doSetup and proceding anyway.
            doSetup();
         if (currentCol >= columns)
            putCR();
         currentCol++;
         Graphics g = getGraphics();
         g.setColor(Color.black);
         g.setFont(font);
         char[] fudge = new char[1];
         fudge[0] = ch;
         g.drawChars(fudge, 0, 1, leftOffset + (currentCol-1)*charWidth, 
                                 topOffset + currentRow*lineHeight + baseOffset); 
         g.dispose();
         OSCGraphics.drawChars(fudge, 0, 1, leftOffset + (currentCol-1)*charWidth, 
                                 topOffset + currentRow*lineHeight + baseOffset);
      }
   
      protected void eraseChar() {  // erase char before cursor position and move cursor
         if (currentCol == 0 && currentRow == 0)
            return;
         currentCol--;
         if (currentCol < 0) {
            currentRow--;
            currentCol = columns - 1;
         }
         Graphics g = getGraphics();
         g.setColor(Color.white);
         g.fillRect(leftOffset + (currentCol*charWidth), topOffset + (currentRow*lineHeight),
                                     charWidth, lineHeight - 1);
         g.dispose();
         OSCGraphics.setColor(Color.white);
         OSCGraphics.fillRect(leftOffset + (currentCol*charWidth), topOffset + (currentRow*lineHeight),
                                     charWidth, lineHeight - 1);
         OSCGraphics.setColor(Color.black);
      }
   
      protected synchronized void putCR() {  // move cursor to start of next line, scrolling window if necessary
         if (OSC == null) {  // If this routine is called before the console has
                             // completely opened, we shouldn't procede; give the
                             // window a chance to open, so that paint() can call doSetup().
            try { wait(5000); }  // notify() should be set by doSetup()
            catch (InterruptedException e) {}
         }
         if (OSC == null)  // If nothing has happened for 5 seconds, we are probably in
                           //    trouble, but when the heck, try calling doSetup and proceding anyway.
            doSetup();
         currentCol = 0;
         currentRow++;
         if (currentRow < rows)
            return;
         OSCGraphics.copyArea(leftOffset, topOffset+lineHeight,
                                columns*charWidth, (rows-1)*lineHeight - leading ,0, -lineHeight);
         OSCGraphics.setColor(Color.white);
         OSCGraphics.fillRect(leftOffset,topOffset + (rows-1)*lineHeight, columns*charWidth, lineHeight - leading);
         OSCGraphics.setColor(Color.black);
         currentRow = rows - 1;
         Graphics g = getGraphics();
         paint(g);
         g.dispose();
         try { Thread.sleep(20); }
         catch (InterruptedException e) { }
      }
   
      protected void cursorBlink() {  // toggle visibility of cursor (but don't show it if focus has been lost)
         if (cursorIsVisible) {
            Graphics g = getGraphics();
            g.setColor(Color.white);
            putCursor(g);
            cursorIsVisible = false;
            g.dispose();
         }
         else if (hasFocus) {
            Graphics g = getGraphics();
            g.setColor(Color.black);
            putCursor(g);
            cursorIsVisible = true;
            g.dispose();
         }
      }
   
      protected synchronized void doFocus(boolean focus) {  // react to gain or loss of focus
         if (OSC == null)
            doSetup();      
         hasFocus = focus;
         if (hasFocus)    // the rest of the routine draws or erases border around canvas
            OSCGraphics.setColor(Color.cyan);
         else
            OSCGraphics.setColor(Color.white);
         int w = getSize().width;
         int h = getSize().height;
         for (int i = 0; i < 3; i++)
            OSCGraphics.drawRect(i,i,w-2*i,h-2*i);
         OSCGraphics.drawLine(0,h-3,w,h-3);
         OSCGraphics.drawLine(w-3,0,w-3,h);
         OSCGraphics.setColor(Color.black);
         repaint();
         try { Thread.sleep(50); }
         catch (InterruptedException e) { }
         notify();
      }
   
      protected void doSetup() {  // get font parameters and create OSC
         int w = getSize().width;
         int h = getSize().height;
         font = new Font("Courier",Font.PLAIN,getFont().getSize());
         FontMetrics fm = getFontMetrics(font);
         lineHeight = fm.getHeight();
         leading = fm.getLeading();
         baseOffset = fm.getAscent();
         charWidth = fm.charWidth('W');
         columns = (w - 12) / charWidth;
         rows = (h - 12 + leading) / lineHeight;
         leftOffset = (w - columns*charWidth) / 2;
         topOffset = (h + leading - rows*lineHeight) / 2;
         OSC = createImage(w,h);
         OSCGraphics = OSC.getGraphics();
         OSCGraphics.setFont(font);
         OSCGraphics.setColor(Color.white);
         OSCGraphics.fillRect(0,0,w,h);
         OSCGraphics.setColor(Color.black);
         notify();
      }
   
      public void update(Graphics g) {
         paint(g);
      }
   
      public synchronized void paint(Graphics g) {
         if (OSC == null)
            doSetup();
         g.drawImage(OSC,0,0,this);
      }
   
   
   }  // end class ConsoleCanvas


} // end of class Console
