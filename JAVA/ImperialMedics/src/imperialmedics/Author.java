package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.ArrayList;

/**
 * Stores a title, surname and initials.
 * All names are converted to standard ascii using
 * AsciiUtils.convertNonAscii(.
 * @see AsciiUtils#convertNonAscii(java.lang.String)
 * @Author time
 */
public class Author implements Comparable<Author> {

    public static final String [] titleList ={"Dr","Prof","Professor","Lord","Sir","Mr","Ms","Miss"};
    public static final String [] awardList ={"BA", "BS", "BMBCh", "DSc","Emeritus",
                                              "FRCPsych", "FFPHM", "FMedSci","FRCP", "FRCS",
                                              "MA","MB","MCh","MD", "MRCP","MRCPsych",
                                              "PhD", "ScD",
                                              "CBE","OBE"};
    private ArrayList<String> title;
    private ArrayList<String> award;
    /**
     * String of surnames.
     * Surnames should be made up of
     * parts separated by SINGLE spaces e.g
     * Da Silva Xavier,
     * Cunninghame Graham
     * Parts are entries in the list in order given from left to right.
     */
    private String surnames;
    private char [] initial;
    /**
     * First name list.
     * Used for initials
     */
    private ArrayList<String> FirstNameList;
    /**
     * ID number of this author
     */
    private int id=ProcessSinglePublicationCSVList.IUNSET;

    /**
     * Offset used to indicate difference in Surnames.
     * Used for comparison.  If the absolute value is less than this number
     * then two authors differ in initials.
     * Value is {@value }.
     */
    final static int SURNAMEOFFSET=16;

   public static void main(String[] args) {
       String s="Aggarwal R., Crochet P., Dias A., Ziprin P., Darzi A.";
       System.out.println(s);
       ArrayList<Author> aArray = Author.authorList(s,",",' ');
       for (int an=0; an<aArray.size(); an++){
           Author a= aArray.get(an);
           String initialString =a.getInitials(" ");
           System.out.println(a.getTitles(" ") +" : "
                   + a.getSurnames() +" : "
                   + a.getInitials(" ")+ " : "
                   + a.getAwards(" "));
       }
   }

  /**
    * Empty author.
    * Needed for extensions.
    */
   protected Author(){
    }


   /**
    * Splits a string into parts of authors name.
    * Uses splitSurnameNameInitials routine to define input
    * with dots to indicate end of initials and end of
    * surname deduced from this.  Should work with multi part surnames.
    * All names are converted to standard ascii using
    * AsciiUtils.convertNonAscii(.
    * @param a string to process into parts of author name
    * @param separatorSurnameInitials character which indicates the end of surname and start of initials
    * @see #splitSurnameNameInitials(java.lang.String)
    * @see AsciiUtils#convertNonAscii(java.lang.String)
    */
   public Author(String a){int r = this.splitSurnameNameInitials(AsciiUtils.convertNonAscii(a));}

 //   /**
//    * Splits a string into parts of authors name.
//    * Uses splitFullName routine to define input
//    * with space to indicate the end of surname
//    * @param a string to process into parts of author name
//    * @see #splitFullName(java.lang.String, char)
//    */
//   public Author(String a){
//        int r = splitFullName(a, ' ');
//    }
  /**
    * Splits a string into parts of authors name.
    * Uses splitFullName routine to define input
    * with space to indicate the end of surname
    * All names are converted to standard ascii using
    * AsciiUtils.convertNonAscii(.
    * @param separatorSurnameInitials character which indicates the end of surname and start of initials
    * @param a string to process into parts of author name
    * @see #splitFullName(java.lang.String, char)
    * @see AsciiUtils#convertNonAscii(java.lang.String)
    */
   public Author(String a, char separatorSurnameInitials){
        int r = splitFullName(AsciiUtils.convertNonAscii(a), separatorSurnameInitials);
    }

 /**
    * Deep copy.
    */
   public Author(Author a){
       deepCopyBasicAuthorData(a);
    }


   public void deepCopyBasicAuthorData(Author a)
    {
     if (a.hasTitles()) title = new ArrayList(a.getTitles());
     if (a.hasAwards()) award = new ArrayList(a.getAwards());
     if (a.hasSurnames()) surnames = a.getSurnames();
     if (a.hasInitials()) {
         initial = new char[a.getInitialsNumber()];
         initial = java.util.Arrays.copyOf( a.getInitials(), a.getInitialsNumber());
     }
    }


 
    /**
     * Splits a string into surname and initials.
     * Format is white space surname  separatorSurnameInitials namesorinitialsortitles.
     * The namesorinitials are strings of one or more letters separated by white space
     * or periods.  If these strings of letters match any of the title strings in
     * titleList exactly, then it that string is treated as a title not an initial.
     * Likewise with the awards as listed in the awardsList.
     * Trailing or leading white space and periods are allowed
     * in the initial string section.  However each initial must start with a
     * letter which is used as the initial for that part.
     * Flags are -1 if no letters found in string.
     * Less than 0 to 255: number returned is number of initials.
     * Less than 256 to 511: number returned is number of initials processed plus 256.
     * At least one initial taken from string of length greater than one
     * e.g. full first name given.
     * More than 512 or more: number returned is number of initials processed plus 512.
     * However processing stopped when initial found was not a letter.
     * <p>Note if we have a multi part surname and the separator between
     * last part of surname and initials is also a space then the second and later
     * parts of the surname will be treated as a first name.
     * @param a input name to be split up
     * @param separatorSurnameInitials character which indicates the end of surname and start of initials
     * @return integer flag, negative if serious problem, 256 or more if possible issue
     */
    protected int splitFullName(String a, char separatorSurnameInitials){
        int result=0;
        title= new ArrayList();
        award= new ArrayList();
        surnames= "";
        initial=null;
        int firstLetter=findFirstLetter(a);
        if (firstLetter<0) return -1;
        int space = a.indexOf(separatorSurnameInitials,firstLetter);
        if (space<0){
            //surnames=a;
            setSurnames(a);
            return 0;
        }
        //surnames=a.substring(firstLetter, space);
        setSurnames(a.substring(firstLetter, space));
        String initialString=a.substring(space+1);
        result= splitInitialsTitlesAwards(initialString);
        return result;
        }


    /**
     * Splits a string into surname and initials.
     * Input format is white space surnamestring  space initialstring
     * The initialstring is a single string where the initials MUST be separated by
     * a . (white space can also be present).  This is to allow for the
     * surname string to contain a space between parts of
     * multi-word surnames.
     * @param a input name to be split up
     * @return integer flag, negative if serious problem, 256 or more if possible issue
     */
    protected int splitSurnameNameInitials(String a){
        //char initialSeparator='.'
        int result=0;
        title= new ArrayList();
        award= new ArrayList();
        surnames= ""; //new ArrayList();
        initial=null;
        int firstDot=a.indexOf('.');
        if (firstDot<0) { // no initials found
            setSurnames(a);
            return 0;
        }
        // now find the last space before the first .
        // this should mark the end of the surname
        int spaceBeforeInitials=firstDot;
        for (spaceBeforeInitials=firstDot-1; spaceBeforeInitials>=0; spaceBeforeInitials--)
            if (a.charAt(spaceBeforeInitials)==' ') break;

        if (spaceBeforeInitials<0) return -3;
        setSurnames(a.substring(0,spaceBeforeInitials));
        String initialString=a.substring(spaceBeforeInitials);
        result= splitInitialsTitlesAwards(initialString);
        return result;
    }


    /**
     * Splits a string into initials, titles and awards.
     * The input is a string of substrings containing one or more letters
     * separated by white space or periods or hyhpens.
     * If these any of these substrings of letters match any of the title strings in
     * titleList exactly, then it that string is treated as a title not an initial.
     * Likewise with the awards as listed in the awardsList.
     * Trailing or leading white space and periods are allowed
     * in this initial string.  However each initial must start with a
     * letter which is used as the initial for that part.
     * Flags are -1 if no letters found in string.
     * Less than 0 to 255: number returned is number of initials.
     * Less than 256 to 511: number returned is number of initials processed plus 256.
     * At least one initial taken from string of length greater than one
     * e.g. full first name given.
     * More than 512 or more: number returned is number of initials processed plus 512.
     * However processing stopped when initial found was not a letter.
     * Allows for hyphens in initials such as Basanez M.-G. as treats them as separators between
     * names.
     * <p>Note assumes no surnames in this string and this is not changed.
     * @param initialString string containing initials, titles and awards
     * @return integer flag, negative if serious problem, 256 or more if possible issue
     */
    protected int splitInitialsTitlesAwards(String initialString){
        int result=0;
        String[] initialList=initialString.split("[\\s\\.\\-]+");
        FirstNameList = new ArrayList();
        for (int i=0; i<initialList.length; i++){
            if (initialList[i].length()==0) continue; // allow for spaces and . together as separators

            if (isTitle(initialList[i])) {
                title.add(initialList[i]);
                continue;
            }
            if (isAward(initialList[i])) {
                award.add(initialList[i]);
                continue;
            }

            if (!Character.isLetter(initialList[i].charAt(0))) {
                    // must be a letter
                    result=512;
                    break;
            }
            FirstNameList.add(initialList[i]);
            if (initialList[i].length()>1) {
                result=256;
            }
        }
        if (FirstNameList.isEmpty()) return result;
        int initialNumber=FirstNameList.size();
        initial = new char[initialNumber];
        for (int i=0; i<initialNumber; i++) initial[i]=FirstNameList.get(i).charAt(0);
        return result+initialNumber;
    }


    /**
     * Test to see if starts with a title.
     * @param s string to test
     * @return true if string starts with any entry in titleList array.
     */
    static public boolean isAward(String s){
        for (int a=0; a<awardList.length; a++){
          if (s.startsWith(awardList[a]) ) return true;
        }
        return false;
    }
    /**
     * Tests to see if any awards stored.
     * @return true if have some awards
     */
    public boolean hasAwards(){
        return !(award==null || award.isEmpty());
    }
   /**
     * Number of awards stored.
     * @return number of awards stored.
     */
    public int getNumberAwards(){
        return (award==null?0:award.size());
    }

    /**
     * Returns awards.
     * @param sep string added between each award
     * @return award string
     */
    public String getAwards(String sep){
    if(!hasAwards()) return "";
    String s=award.get(0);
    for (int a=0; a<getNumberAwards(); a++) s=s+sep+award.get(a);
    return s;
    }

    /**
     * Returns awards.
     * @return arrayList of strings or null if no awards
     */
    public ArrayList<String> getAwards(){
    if(!hasAwards()) return null;
    return award;
    }


   /**
     * Test to see if starts with a title.
     * @param s string to test
     * @return true if string starts with any entry in titleList array.
     */
    static public boolean isTitle(String s){
        for (int t=0; t<titleList.length; t++){
          if (s.startsWith(titleList[t]) ) return true;
        }
        return false;
    }
     /**
     * Tests to see if any Titles stored.
     * @return true if have some Titles
     */
    public boolean hasTitles(){
        return !(title==null || title.isEmpty());
    }
   /**
     * Number of Titles stored.
     * @return number of Titles stored.
     */
    public int getNumberTitles(){
        return (title==null?0:title.size());
    }

    /**
     * Returns Titles.
     * @parm sep string used to separate titles
     * @return string of titles
     */
    public String getTitles(String sep){
        if(!hasTitles()) return "";
        String s=title.get(0);
        for (int t=1; t<title.size(); t++) s=s+sep+title.get(t);
        return s;
    }


    /**
     * Returns awards.
     * @return arrayList of strings or null if no awards
     */
    public ArrayList<String> getTitles(){
     if(!hasTitles()) return null;
     return title;
    }


    //Authors: Aggarwal, R.; Aggarwal, Rajesh K.

    /**
     * Finds first letter in a string.
     * A letter is defined by java Character.isLetter() method.
     * Returns -1 if none found.
     * @param a string to be tested
     * @return position of first letter, -1 if non found.
     */
    static public int findFirstLetter(String a){
        int firstLetter=0;
        for (firstLetter=0; firstLetter<a.length(); firstLetter++)
        if (Character.isLetter(a.charAt(firstLetter))) return firstLetter;
        return -1;
    }

    /**
     * Returns standard form.
     * If no initials it is just title space surname.
     * Otherwise it is title space surname comma then
     * a list of initials separated by . but no spaces
     * @return standard form of name and initials.
     */
    @Override
    public String toString(){
        return toString(hasTitles());
    }
    /**
     * Returns surname then initials.
     * If no initials it is just title (if requested) surname.
     * Otherwise it is surname comma then
     * a list of initials separated by . but no spaces
     * @param includeTitles true if want titles
     * @return standard form of name and initials.
     */
    public String toString(boolean includeTitles){
        String s=(includeTitles?getTitles(" ")+" ":"")+getSurnames();
        if(!hasInitials()) return s;
        s=s+", "+getInitials(".");
        return s;
    }
    /**
     * Returns surname then initials, with titles and ID if present.
     * If no initials it is just title (if requested) surname.
     * Otherwise it is surname comma then
     * a list of initials separated by . but no spaces
     * Also with ID number
     * @return standard form of name and initials.
     */
    public String toStringWithTitlesAndID(){
        return toString(hasTitles()) +(hasID()?" ("+getID()+")":"");
    }
    /**
     * Returns surname then initials.
     * If no initials it is just title (if requested) surname.
     * Otherwise it is surname comma then
     * a list of initials separated by . but no spaces
     * @param includeTitles true if want titles
     * @param includeID if want ID number if exists
     * @return standard form of name and initials.
     */
    public String toString(boolean includeTitles, boolean includeID){
        String s=(includeTitles?getTitles(" ")+" ":"")+getSurnames();
        if(hasInitials()) {s=s+", "+getInitials(".");}
        if(hasID()) {s=s+" ("+getID()+")";}
        return s;
    }
    /**
     * Returns surname, initials form.
     * If no initials it is just surname.
     * Otherwise it is surname comma then
     * a list of initials separated by . but no spaces
     * @return standard form of name and initials.
     */
    public String toStringNoTitles(){
        return toString(false);
    }



//    /**
//     * Returns surnames.
//     * @sep string to separate parts of surname
//     * @return string of surnames separated by sep string
//     */
//    public String getSurnameString(String sep){
//        String s="";
//        for (int n=0; n<surnames.size(); n++) s= s+(n==0?"":sep)+surnames.get(n);
//        return s;
//    }

     /**
     * Returns surnames.
     * @return string of surnames
     */
    public String getSurnames(){ return surnames;}

     /**
      * Sets surnames.
      * Standard form of surname has parts of surname separated by single space.
      * This routine splits up given string into parts separated by white space
      * and joins them back together in single string with single space between parts.
      * Note hyphenated surnames left as one whole word.
      * @param surnamesString string of surname parts separated by any amount of white space
      * @return string of surnames separated by single space
      */
    public int setSurnames(String surnamesString){
        int result=0;
        String[] surnameList=surnamesString.split("[\\s\\-]+");
        surnames="";
        for (int i=0; i<surnameList.length; i++){
            if (surnameList[i].length()==0) continue; // allow for spaces and . together as separators
            if (!Character.isLetter(surnameList[i].charAt(0))) {
                    // must be a letter
                    result=512;
                    break;
            }
            if (surnames.length()==0) surnames=surnameList[i];
            else surnames=surnames+" "+surnameList[i];
        }
        return result;
    }
     /**
      * Sets surnames.
      * Standard form of surname has parts of surname separated by single space.
      * This routine splits up given string into parts separated by white space
      * and joins them back together in single string with single space between parts.
      * Note this also treats hyphens as a break so will split hyphenated surnames in to two parts.
      * @param surnamesString string of surname parts separated by any amount of white space
      * @return string of surnames separated by single space
      */
    public int setSurnamesSplitHyphens(String surnamesString){
        int result=0;
        String[] surnameList=surnamesString.split("[\\s\\-]+");
        surnames="";
        for (int i=0; i<surnameList.length; i++){
            if (surnameList[i].length()==0) continue; // allow for spaces and . together as separators
            if (!Character.isLetter(surnameList[i].charAt(0))) {
                    // must be a letter
                    result=512;
                    break;
            }
            if (surnames.length()==0) surnames=surnameList[i];
            else surnames=surnames+" "+surnameList[i];
        }
        return result;
    }
//    /**
//     * Returns number of initials.
//     * @return number of initials.
//     */
//    public int getSurnamesNumber(){
//      if(!hasSurnames()) return 0;
//      return surnames.size();
//    }
    /**
     * Tests to see if any surnames stored.
     * @return true if have some surnames
     */
    public boolean hasSurnames(){
        return !(surnames==null || surnames.isEmpty());
    }





    /**
     * Returns initials as a string.
     * @param sep string added after each initial
     * @return surname string
     */
    public String getInitials(String sep){
    if(!hasInitials()) return "";
    String s="";
    for (int i=0; i<numberInitials(); i++) s=s+initial[i]+sep;
    return s;
    }

    /**
     * Returns initials.
     * @return array of characters or null if no initials
     */
    public char [] getInitials(){
    if(!hasInitials()) return null;
    return initial;
    }
    /**
     * Returns number of initials.
     * @return number of initials.
     */
    public int getInitialsNumber(){
      if(!hasInitials()) return 0;
      return initial.length;
    }
    /**
     * Tests to see if any initials stored.
     * @return true if have some initials
     */
    public boolean hasInitials(){
        return !(initial==null || initial.length==0);
    }
    /**
     * Number of initials stored.
     * @return number of initials stored.
     */
    public int numberInitials(){
        return (initial==null?0:initial.length);
    }

    /**
     * Tests to see if has ID number set.
     * @return true if has ID number
     */
    public boolean hasID(){
        return !(id==ProcessSinglePublicationCSVList.IUNSET );
    }

    /**
     * Set ID number.
     * @param new id number true if has ID number
     */
    public void setID(int newid){
        id=newid;
    }

    /**
     * Gets ID number.
     * @return ID number
     */
    public int getID(){
        return id;
    }


/**
 * Tests for equality of two authors.
 * This only tests surname and initials.
 * If list of initials is different does not compares those in the longer list 
 * beyond the number in the shorter list
 * @param otherAuthor
 * @return
 */
    @Override
    public boolean equals(Object otherAuthor){
        return equalUptoFirstInitial(otherAuthor);
    }

    /**
     * Matches as many initials as possible.
     * If number of initials differs between authors,
     * then only compare initials where exist for both.
     * These must all be equal for equality but any
     * extra ones are ignored.
     * @param otherAuthor
     * @return tests surname and as many initials as possible.
     */
    public boolean equalsAllPossibleInitials(Object otherAuthor){
        Author a2 = (Author)otherAuthor;
        if (!a2.getSurnames().equalsIgnoreCase(surnames)) return false;
        int ni = Math.min(numberInitials(),a2.numberInitials());
        for (int i=0; i<ni; i++){
            if (initial[i]!=a2.initial[i]) return false;
        }
        return true;
    }

    /**
     * Matches as closely as possible.
     * First test ID number if exists for both.
     * If one or both has no ID then surname and initials must
     * match exactly including the number of initials.
     * @param otherAuthor
     * @return tests surname and as many initials as possible.
     */
    public boolean equalsExactly(Object otherAuthor){
        Author a2 = (Author)otherAuthor;
        if (hasID() && a2.hasID()) return equalsByID(otherAuthor);
        if (this.numberInitials() != a2.numberInitials()) return false;
        return equalsAllPossibleInitials(otherAuthor);
    }

    /**
     * Matches only surnames.
     * Each part of surname must exist and must be identical.
     * Thus Powell and Powell Smith are not the same.
     * Gives equality if neither have surnames.
     * Case ignored.
     * @param otherAuthor
     * @return tests surname only.
     */
    public boolean equalsSurnamesOnly(Object otherAuthor){
        Author a2 = (Author)otherAuthor;
        if (!this.hasSurnames()) return (!a2.hasSurnames()); // equality if neither have surnames
        if (!a2.getSurnames().equalsIgnoreCase(surnames)) return false;
        return true;
    }



    /**
     * Compares two authors upto first initial only.
     * Surname must be exactly equal to be true.
     * Otherwise following steps are followed.
     * If both authors have no initials then true - only surname is compared.
     * If only one author has no initials then false.
     * Otherwise first initials must be equal to be true, otherwise false.
     * @param otherAuthor
     * @return tests surname and first initial to be equal.
     */
    public boolean equalUptoFirstInitial(Object otherAuthor){
        Author a2 = (Author)otherAuthor;
        if (!a2.getSurnames().equalsIgnoreCase(surnames)) return false;
        if (numberInitials()==0) return (a2.numberInitials()==0);
        if (a2.numberInitials()==0) return false;
        if (initial[0]!=a2.initial[0]) return false;
        return true;
    }
    /**
     * Matches id numbers.
     * If either author or both has no id then false is returned.
     * Also false if other author is null.
     * @param otherAuthor other author
     * @return true if both id numbers exists and are equal, otherwise false
     */
    public boolean equalsByID(Object otherAuthor){
        if (otherAuthor==null) return false;
        if (!hasID()) return false;
        Author a2 = (Author) otherAuthor;
        if (!a2.hasID()) return false;
        if (this.id==a2.id) return true;
        return false;
    }

    /**
     * Compares two authors using id.
     * The result is equal to this author's id minus the other author's id.
     * @param otherAuthor the other author
     * @return (this.id-otherAuthor.id).
     */
    public int compareToID(Author otherAuthor){
        return this.id-otherAuthor.id;
    }

    /**
     * Compares two authors using ID if both exist, otherwise uses as many initials as possible.
     * @param otherAuthor
     * @return tests surname and all initials.
     * @see #compareToFirstInitial(imperialmedics.Author)
     */
    public int compareTo(Author otherAuthor){
        return compareToSurnameInitialsAsPossible(otherAuthor);
    }

   /**
     * Compares two authors upto first initial only.
     * Surname must be exactly equal ignoring case to be true.
     * returns the difference in the surnames using String comparToIgnoreLowerCase()
     * multiplied by the value {@value #SURNAMEOFFSET}.
     * This is equal to the
     * <kbd>(this.charAt(k)-otherAuthor.charAt(k))*{@value #SURNAMEOFFSET}</kbd>
     * where k is the smallest value where the two differ.
     * @param otherAuthor
     * @return tests surname and all possible initials.
     */
    public int compareToSurnameOnly(Author otherAuthor){
        Author a2 = (Author)otherAuthor;
        // sc =surnames.charAt(k)-a2.charAt(k) if first difference at character k
        // characters or two byte unicode values
        return surnames.compareToIgnoreCase(a2.surnames)*SURNAMEOFFSET;
    }

    /**
     * Comparison of initials only.
     * Used in some routines after surnames are equal. Done in following order.
     * If both authors have no initials the 0 returned.
     * If this author has initials and the other does not then
     * ({@value #SURNAMEOFFSET}-1) is returned.
     * If this author has no initials and the other does then
     * (1-{@value #SURNAMEOFFSET}) is returned.
     * Otherwise an absolute value of ({@value #SURNAMEOFFSET}-2) is returned
     * if first initials differ with sign equal to difference of this
     * author's first initial from other authors first initial.
     * If the first initial is equal then 0 is returned.
     * @param a2 other author
     * @return comparison value
     */
    public int compareToFirstInitialOnly(Author a2){
        if ( !hasInitials() ) return (a2.hasInitials()?(1-SURNAMEOFFSET):0);
        if ( !a2.hasInitials() ) return (SURNAMEOFFSET-1);
        if (initial[0]==a2.initial[0]) return 0;
        return (initial[0]>a2.initial[0]?(SURNAMEOFFSET-2):(2-SURNAMEOFFSET));
    }
    /**
     * Comparison of only initials where possible.
     * Used in other routines if surnames equal.
     * Extra initials by one or other author are ignored.
     * So if one or other have no initials then zero is returned.
     * The value returned has an
     * absolute value equal to {@value #SURNAMEOFFSET}-i-2
     * where i is the position of the first initial where a difference is found,
     * starting with the initial which has index i=0.  An absolute value of 1 is returned
     * if there are {@value #SURNAMEOFFSET}-2 or more initials.
     * The sign returned for initials differing is the same as
     * <kbd>(this.getInitial(i)-otherAuthor.getInitial(i)</kbd>
     * @param a2 other author
     * @return 0 if equal otherwise indicates if initials of this author comes before those of other author.
     */
    public int compareInitialsAsPossibleOnly(Author a2){
        if ( !hasInitials() || !a2.hasInitials() ) return 0;
        int numberInitials = Math.min(getInitialsNumber(),a2.getInitialsNumber());
        for (int i=0; i<numberInitials; i++){
            if (initial[i]!=a2.initial[i]) {
                int absValue = SURNAMEOFFSET-i-2 ;
                if (absValue<1) absValue=1;
                return (initial[i]>a2.initial[i]?absValue:-absValue);
            }
        }
        return 0;
    }

   /**
     * Comparison of only initials.
     * Used in other routines if surnames equal.
     * Extra initials by one or other author are not ignored.
     * If both authors have no initials the 0 returned.
     * If this author has initials and the other does not then
     * ({@value #SURNAMEOFFSET}-1) is returned.
     * If this author has no initials and the other does then
     * (1-{@value #SURNAMEOFFSET}) is returned.
     * The value returned has an
     * absolute value equal to {@value #SURNAMEOFFSET}-i-2
     * where i is the position of the first initial where a difference is found,
     * starting with the initial which has index i=0.  An absolute value of 1 is returned
     * if there are {@value #SURNAMEOFFSET}-2 or more initials.
     * The sign returned for initials differing is the same as
     * <kbd>(this.getInitial(i)-otherAuthor.getInitial(i)</kbd>
     * @param a2 other author
     * @return 0 if equal otherwise indicates if initials of this author comes before those of other author.
     */
    public int compareInitialsExactlyOnly(Author a2){
        if ( !hasInitials() ) return (a2.hasInitials()?(1-SURNAMEOFFSET):0);
        if ( !a2.hasInitials() ) return (SURNAMEOFFSET-1);
        int sc =compareInitialsAsPossibleOnly(a2);
        if (getInitialsNumber()==a2.getInitialsNumber()) return sc;
        int numberInitials = Math.min(getInitialsNumber(),a2.getInitialsNumber());
        int absValue = SURNAMEOFFSET-numberInitials-3 ;
        if (absValue<1) absValue=1;
        return ((getInitialsNumber()>a2.getInitialsNumber())?absValue:-absValue);
    }


    /**
     * Compares two authors up to first initial only.
     * Surname must be exactly equal to be true.
     * Otherwise following steps are followed.
     * If both authors have no initials the 0 returned.
     * If this author has initials and the other does not then
     * ({@value #SURNAMEOFFSET}-1) is returned.
     * If this author has no initials and the other does then
     * (1-{@value #SURNAMEOFFSET}) is returned.
     * Otherwise an absolute value of ({@value #SURNAMEOFFSET}-2) is returned
     * if first initials differ with sign equal to difference of this
     * author's first initial from other authors first initial.
     * If the first initial is equal then 0 is returned.
     * @param otherAuthor the other author
     * @return tests surname and first initials.
     */
    public int compareToSurnameFirstInitial(Author otherAuthor){
        Author a2 = (Author)otherAuthor;
        int sc =surnames.compareToIgnoreCase(a2.surnames)*SURNAMEOFFSET;
        if (sc!=0) return sc;
        return compareToFirstInitialOnly(a2);
    }

     /**
     * Compares two authors upto first initial only.
     * Surname must be exactly equal to be true.
     * Otherwise as many initials as possible are compared
     * and all must be exactly the same to be true.
     * If one author has more initials than the other this surplus is ignored.
     * Returns negative if this author comes before argument (otherAuthor).
     * Returns positive if this author comes before argument (otherAuthor).
     * Returns 0 is equal.
     * If the surnames differ then the value returned
     * is equal to the
     * <kbd>(this.charAt(k)-otherAuthor.charAt(k))*{@value #SURNAMEOFFSET}</kbd>
     * where k is the smallest value where the two differ.
     * If surnames equal then the value returned has an
     * absolute value equal to {@value #SURNAMEOFFSET}-i-2
     * where i is the position of the first initial where a difference is found,
     * starting with the initial which has index i=0.  An absolute value of 1 is returned
     * if there are {@value #SURNAMEOFFSET}-2 or more initials.
     * The sign returned for initials differing is the same as
     * <kbd>(this.getInitial(i)-otherAuthor.getInitial(i)</kbd>
     * @param otherAuthor
     * @return tests surname and all possible initials.
     */
    public int compareToSurnameInitialsAsPossible(Author otherAuthor){
        Author a2 = (Author)otherAuthor;
        int sc =surnames.compareToIgnoreCase(a2.surnames)*SURNAMEOFFSET;
        if (sc!=0) return sc;
        return compareInitialsAsPossibleOnly(a2);
    }
    /**
     * Compares two authors upto first initial only.
     * Surname must be exactly equal to be true.
     * Otherwise as many initials as possible are compared
     * and all must be exactly the same to be true.
     * If one author has more initials than the other this surplus
     * has an effect on comparison.
     * Returns negative if this author comes before argument (otherAuthor).
     * Returns positive if this author comes before argument (otherAuthor).
     * Returns 0 is equal.
     * If the surnames differ then the value returned is equal to
     * <kbd>(this.charAt(k)-otherAuthor.charAt(k))*{@value #SURNAMEOFFSET}</kbd>
     * where k is the smallest value where the two differ.
     * If surnames equal then the value returned has an
     * absolute value equal to {@value #SURNAMEOFFSET}-i-2
     * where i is the position of the first initial where a difference is found,
     * starting with the initial which has index i=0.  An absolute value of 1 is returned
     * if there are {@value #SURNAMEOFFSET}-2 or more initials.
     * The sign returned for initials differing is the same as
     * <kbd>(this.getInitial(i)-otherAuthor.getInitial(i)</kbd>
     * @param otherAuthor
     * @return tests surname and all possible initials.
     */
    public int compareToExact(Author otherAuthor){
        Author a2 = (Author)otherAuthor;
        // sc =surnames.charAt(k)-a2.charAt(k) if first difference at character k
        // characters or two byte unicode values
        int sc =surnames.compareToIgnoreCase(a2.surnames);
        if (sc!=0) return sc*SURNAMEOFFSET;
        return compareInitialsExactlyOnly(a2);
    }


//    /**
//     * Splits a string of authors separated by commas into and array of authors.
//     * No titles, and uses Author(String) constructor to define names.
//     * e.g. try <tt>Aggarwal R., Crochet P., Dias A., Ziprin P., Darzi A.</tt>
//     * @param s input string
//     * @return arrayList of type Author, null if none found
//     */
//    public static ArrayList<Author> authorList(String s){
//       String[]  alist = s.split(",+");
//       ArrayList<Author> authorList = new ArrayList();
//       for (int an=0; an<alist.length; an++){
//           String al = alist[an];
//           if (al.length()==0) continue;
//           Author a= new Author(al);
//           authorList.add(a);
//       }
//       if (authorList.isEmpty()) return null;
//       return authorList;
//    }

    // example format fo authorList cell is
                    //Gurusamy K.S., Aggarwal R., Palanivelu L., Davidson B.R.
                    //Cunninghame Graham D.S., Vyse T.J.

    /**
     * Returns surnames as array of strings.
     * Surname is split up at white space or hyphens
     * @retunr array of strings for each part of surname
     */
    public String [] getSurnamesList(){
        return surnames.split("[\\s\\-]+");
    }

    /**
     * Shifts last part of surname as family name.
     * Assumes existing surname is really surname first name.
     * Sometimes listed in publications as
     * <tt>Evans Tim S., Surname Firstname I.J.</tt>.
     * This method splits off the last part of the surname
     * and uses that as new first initial followed by original initials.
     * If no multipart surname found or no surname then deep copy produced of original.
     * @return author with last part of multipart surname used as new first name
     */
    public Author surnameFirstNameSplit(){
        Author a =new Author(this);
        if (!hasSurnames()) return a;
        int lastSpace = surnames.lastIndexOf(' ');
        if (lastSpace<0 || lastSpace>=(surnames.length()-1)) return a;
        a.surnames=surnames.substring(0,lastSpace); // this is last part of surname
        a.initial=new char[this.getInitialsNumber()+1];
        a.initial[0]=surnames.charAt(lastSpace+1); // last part of old surname used as new first name
        for (int i=0; i<getInitialsNumber(); i++) a.initial[i+1]=this.initial[i]; // shift ititials
        return a;
    }
    /**
     * Splits a string of authors separated by commas into and array of authors.
     * No titles, and uses Author(String) constructor to define names.
     * e.g. try <tt>Aggarwal R., Crochet P., Dias A., Ziprin P., Darzi A.</tt>
     * <p>Does not deal with double barrelled family names e.g.
     * <tt>Cunninghame Graham D.S., Vyse T.J.</tt>.
     * Removes accents.
     * @param s input string
     * @param separatorNames string giving the separator between different names (comma in example)
     * @param separatorSurnameInitials character which indicates the end of surname and start of initials (space in this example)
     * @return arrayList of type Author, null if none found
     */
    public static ArrayList<Author> authorList(String s,
                                               String separatorNames,
                                               char separatorSurnameInitials){
       String s2=AsciiUtils.convertNonAscii(s); // removes accents
       String regex = separatorNames+"+";
       String[]  alist = s2.split(regex);
       ArrayList<Author> authorList = new ArrayList();
       for (int an=0; an<alist.length; an++){
           String al = alist[an];
           if (al.length()==0) continue;
           Author a= new Author(al, separatorSurnameInitials);
           authorList.add(a);
       }
       if (authorList.isEmpty()) return null;
       return authorList;
    }

    /**
     * Splits a string of authors separated by commas into and array of authors.
     * Assumes the initials in each name end in a . and that the surname section
     * starts before that.  So both the following should work
     * <tt>Aggarwal R., Crochet P., Dias A., Ziprin P., Darzi A.</tt>
     * <tt>Cunninghame Graham D.S., Vyse T.J.</tt>.
     * <p>Removes accents
     * @param s input string
     * @param separatorNames string giving the separator between different names (comma in example)
     * @return arrayList of type Author, null if none found
     */
    public static ArrayList<Author> authorList(String s,
                                               String separatorNames){
       String s2=AsciiUtils.convertNonAscii(s); // removes accents
       String regex = separatorNames+"+";
       String[]  alist = s2.split(regex);
       ArrayList<Author> authorList = new ArrayList();
       for (int an=0; an<alist.length; an++){
           String al = alist[an];
           if (al.length()==0) continue;
           Author a= new Author(al);
           authorList.add(a);
       }
       if (authorList.isEmpty()) return null;
       return authorList;
    }


    /**
     * Splits a string of authors separated by commas into and array of authors.
     * No titles, and uses Author(String) constructor to define names.
     * e.g. try <tt>Aggarwal R., Crochet P., Dias A., Ziprin P., Darzi A.</tt>
     * <p>Does not deal with double barrelled family names if surname and initials
     * are also separated by spaces
     * e.g. <tt>Cunninghame Graham D.S., Vyse T.J.</tt> fails.
     * @param s input string
     * @param separatorNames string giving the separator between different names (comma in example)
     * @param separatorSurnameInitials character which indicates the end of surname and start of initials (space in this example)
     * @return arrayList of type Author, null if none found
     */
    public static ArrayList<Author> authorListSimple(String s,
                                               String separatorNames,
                                               char separatorSurnameInitials){
       String regex = separatorNames+"+";
       String[]  alist = s.split(regex);
       ArrayList<Author> authorList = new ArrayList();
       for (int an=0; an<alist.length; an++){
           String al = alist[an];
           if (al.length()==0) continue;
           Author a= new Author(al, separatorSurnameInitials);
           authorList.add(a);
       }
       if (authorList.isEmpty()) return null;
       return authorList;
    }

    /**
     * Splits a string of authors separated by commas into and array of authors.
     * If titles present and they match titleList they will be processed.
     * Likewise with awards.
     * Uses Author(String) constructor to define names.
     * e.g. try <tt>Aggarwal R., Crochet P., Dias A., Ziprin P., Darzi A.</tt>
     * @param s input string
     * @param separatorSurnameInitials character which indicates the end of surname and start of initials (space in this example)
     * @return author stored as type Author, null if none found
     */
    public static Author create(String s, char separatorSurnameInitials){
        Author a= new Author(s, separatorSurnameInitials);
       return a;
    }


    /**
     * Tests a list of authors for alphabetical order
     * @param alist list of authors
     * @return true is list is in alphabetical order.
     */
    public static boolean isAlphabeticalOrder(ArrayList<Author> alist){
        Author aPrevious=null;
        for (Author a: alist){
            if (aPrevious!=null && (aPrevious.compareToSurnameInitialsAsPossible(a)>=0) )return false;
            aPrevious=a;
        }
        return true;
    }


}

