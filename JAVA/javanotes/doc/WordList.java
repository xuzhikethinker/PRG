
/*
   This program lets the user specify a text file for input and a file
   for output.  All the words are read from the input file.  Words are
   converted to lower case.  An alphabetical list of all the words that
   were found, without repetition, is written to the output file, with
   one word per line.  A word in this program is defined to be any
   sequence of letters.
   
   This class depends on the non-standard classes TextIO and TextReader,
*/

import java.io.*;

public class WordList {

   static String[] words;  // An array to hold the words from the file.
                           //   Note that the array will be expanded as 
                           //   necessary, in the insertWord() subroutine.
   
   static int wordCount;   // The number of words currently stored in
                           //   the array.


   public static void main(String[] args) {
   
      TextReader in;    // A stream for reading from the input file.
      PrintWriter out;  // A stream for writing to the output file.
      
      String inputFileName;   // Input file name, specified by the user.
      String outputFileName;  // Output file name, specified by the user.
      
      words = new String[10];  // Start with space for 10 words.
      wordCount = 0;           // Currently, there are no words in the array.
      
      /* Get the input file name from the user and try to create the
         input stream.  If there is a FileNotFoundException, print
         a message and terminate the program. */
      
      TextIO.put("Input file name?  ");
      inputFileName = TextIO.getln().trim();
      try {
         in = new TextReader(new FileReader(inputFileName));
      }
      catch (FileNotFoundException e) {
          TextIO.putln("Can't find file \"" + inputFileName + "\".");
          return;
      }
      
      /* Get the output file name from the user and try to create the
         output stream.  If there is an IOException, print a message
         and terminate the program. */

      TextIO.put("Output file name? ");
      outputFileName = TextIO.getln().trim();
      try {
         out = new PrintWriter(new FileWriter(outputFileName));
      }
      catch (IOException e) {
          TextIO.putln("Can't open file \"" + outputFileName + "\" for output.");
          TextIO.putln(e.toString());
          return;
      }
      
      /* Read all the words from the input stream and insert them into
         the array of words.  Reading from a TextReader can result in
         an error of type TextReader.Error.  If one occurs, print an
         error message and terminate the program. */
      
      try {
         while (true) {
               // Skip past and non-letters in the input stream.  If an
               //   end-of-stream has been reached, end the loop.  Otherwise,
               //   read a word and insert it into the array of words.
            while ( ! in.eof() && ! Character.isLetter(in.peek()) )
               in.getAnyChar();
            if (in.eof())
               break;
            insertWord(in.getAlpha());
         }
      }
      catch (TextReader.Error e) {
         TextIO.putln("An error occured while reading from the input file.");
         TextIO.putln(e.toString());
         return;
      }
      
      /* Write all the words from the list to the ouput stream. */

      for (int i = 0; i < wordCount; i++)
         out.println(words[i]);
      
      /* Finish up by checking for an error on the output stream and
         printing either a warning message or a message that the words
         have been output to the output file. */
      
      if (out.checkError() == true) {
         TextIO.putln("Some error occured while writing output.");
         TextIO.putln("Output might be incomplete or invalid.");
      }
      else {
         TextIO.putln(wordCount + " words from \"" + inputFileName + 
                       "\" output to \"" + outputFileName + "\".");
      }
   
   } // end main()
   

   static void insertWord(String w) {
           // Insert the word w into the array of words, unless it already
           // appears there.  All the words in the array are in lower case,
           // and w is converted to lower case befoer it is processed.
           // Note that the words in the array are kept in alphabetical order.
           // If the array has grown too big to hold w, then it is doubled
           // in size.

      int pos = 0;  // This will be the postion in the array where w belongs.

      w = w.toLowerCase();
      
      /* Find the position in the array where w belongs, after all the
         words that precede w alphabetically.  If a copy of w already
         occupies that position, then it is not necessary to insert
         w, so return immediately. */

      while (pos < wordCount && words[pos].compareTo(w) < 0)
         pos++;
      if (pos < wordCount && words[pos].equals(w))
         return;
         
      /* If the array is full, make a new array that is twice as 
          big, copy all the words from the old array to the new,
          and set the variable, words, to refer to the new array. */

      if (wordCount == words.length) {
         String[] newWords = new String[words.length*2];
         System.arraycopy(words,0,newWords,0,wordCount);
         words = newWords;
      }
      
      /* Put w into its correct position in the array.  Move any
         words that come after w up one space in the array to
         make room for w. */

      for (int i = wordCount; i > pos; i--)
         words[i] = words[i-1];
      words[pos] = w;
      wordCount++;

   }  // end insertWord()
   
   
}  // end class WordList

