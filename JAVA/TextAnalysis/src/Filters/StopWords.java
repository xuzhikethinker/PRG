/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Filters;

/**
 * @author time
 */
public class StopWords {
    
      /** An array containing some common English words that are not usually useful
    for searching. 
       *<p>Taken from {@link org.apache.lucene.analysis.StopAnalyzer}
       * @see org.apache.lucene.analysis.StopAnalyzer#StopAnalyzer
       */
  public static final String[] LUCENE_STOP_WORDS = {
    "a", "an", "and", "are", "as", "at", "be", "but", "by",
    "for", "if", "in", "into", "is", "it",
    "no", "not", "of", "on", "or", "such",
    "that", "the", "their", "then", "there", "these",
    "they", "this", "to", "was", "will", "with"
  };

    

        /**
     * An array containing some common English words
     * that are usually not useful for searching.
 * Taken from <a href="http://www.onjava.com/pub/a/onjava/2003/01/15/lucene.html?page=2">OnJava.com</a>
 * (OReilly site) on a porter stemmer for the <b>lucene</b> project of apache.
     */
    public static final String[] ONJAVA_STOP_WORDS =
    {
        "0", "1", "2", "3", "4", "5", "6", "7", "8",
        "9", "000", "$",
        "about", "after", "all", "also", "an", "and",
        "another", "any", "are", "as", "at", "be",
        "because", "been", "before", "being", "between",
        "both", "but", "by", "came", "can", "come",
        "could", "did", "do", "does", "each", "else",
        "for", "from", "get", "got", "has", "had",
        "he", "have", "her", "here", "him", "himself",
        "his", "how","if", "in", "into", "is", "it",
        "its", "just", "like", "make", "many", "me",
        "might", "more", "most", "much", "must", "my",
        "never", "now", "of", "on", "only", "or",
        "other", "our", "out", "over", "re", "said",
        "same", "see", "should", "since", "so", "some",
        "still", "such", "take", "than", "that", "the",
        "their", "them", "then", "there", "these",
        "they", "this", "those", "through", "to", "too",
        "under", "up", "use", "very", "want", "was",
        "way", "we", "well", "were", "what", "when",
        "where", "which", "while", "who", "will",
        "with", "would", "you", "your",
        "a", "b", "c", "d", "e", "f", "g", "h", "i",
        "j", "k", "l", "m", "n", "o", "p", "q", "r",
        "s", "t", "u", "v", "w", "x", "y", "z"
    };

    /**
     * An array containing some common English words
     * that are usually not useful for searching.
     * <p>Taken from 
     * <a href="http://dev.mysql.com/doc/refman/5.1/en/fulltext-stopwords.html">MySQL</a>
     * see section 11.8.4 on Full-Text Stopwords.
     * 
     */
    public static final String[] MySQL_STOP_WORDS =
{"a's","able","about","above","according","accordingly","across",
"actually","after","afterwards","again","against","ain't","all",
"allow","allows","almost","alone","along","already","also",
"although","always","am","among","amongst","an","and","another",
"any","anybody","anyhow","anyone","anything","anyway","anyways",
"anywhere","apart","appear","appreciate","appropriate","are",
"aren't","around","as","aside","ask","asking","associated","at",
"available","away","awfully","be","became","because","become",
"becomes","becoming","been","before","beforehand","behind",
"being","believe","below","beside","besides","best","better",
"between","beyond","both","brief","but","by","c'mon","c's",
"came","can","can't","cannot","cant","cause","causes","certain",
"certainly","changes","clearly","co","com","come","comes",
"concerning","consequently","consider","considering","contain",
"containing","contains","corresponding","could","couldn't","course",
"currently","definitely","described","despite","did","didn't",
"different","do","does","doesn't","doing","don't","done",
"down","downwards","during","each","edu","eg","eight","either",
"else","elsewhere","enough","entirely","especially","et","etc",
"even","ever","every","everybody","everyone","everything",
"everywhere","ex","exactly","example","except","far","few","fifth",
"first","five","followed","following","follows","for","former",
"formerly","forth","four","from","further","furthermore","get",
"gets","getting","given","gives","go","goes","going","gone",
"got","gotten","greetings","had","hadn't","happens","hardly",
"has","hasn't","have","haven't","having","he","he's","hello",
"help","hence","her","here","here's","hereafter","hereby","herein",
"hereupon","hers","herself","hi","him","himself","his",
"hither","hopefully","how","howbeit","however","i'd","i'll",
"i'm","i've","ie","if","ignored","immediate","in","inasmuch","inc",
"indeed","indicate","indicated","indicates","inner","insofar",
"instead","into","inward","is","isn't","it","it'd","it'll",
"it's","its","itself","just","keep","keeps","kept","know","knows",
"known","last","lately","later","latter","latterly","least",
"less","lest","let","let's","like","liked","likely","little",
"look","looking","looks","ltd","mainly","many","may","maybe",
"me","mean","meanwhile","merely","might","more","moreover","most",
"mostly","much","must","my","myself","name","namely","nd","near",
"nearly","necessary","need","needs","neither","never",
"nevertheless","new","next","nine","no","nobody","non","none",
"noone","nor","normally","not","nothing","novel","now",
"nowhere","obviously","of","off","often","oh","ok","okay","old",
"on","once","one","ones","only","onto","or","other","others",
"otherwise","ought","our","ours","ourselves","out","outside",
"over","overall","own","particular","particularly","per",
"perhaps","placed","please","plus","possible","presumably","probably",
"provides","que","quite","qv","rather","rd","re","really",
"reasonably","regarding","regardless","regards","relatively",
"respectively","right","said","same","saw","say","saying","says",
"second","secondly","see","seeing","seem","seemed","seeming",
"seems","seen","self","selves","sensible","sent","serious",
"seriously","seven","several","shall","she","should","shouldn't",
"since","six","so","some","somebody","somehow","someone",
"something","sometime","sometimes","somewhat","somewhere","soon",
"sorry","specified","specify","specifying","still","sub",
"such","sup","sure","t's","take","taken","tell","tends","th",
"than","thank","thanks","thanx","that","that's","thats","the",
"their","theirs","them","themselves","then","thence","there",
"there's","thereafter","thereby","therefore","therein","theres",
"thereupon","these","they","they'd","they'll","they're",
"they've","think","third","this","thorough","thoroughly",
"those","though","three","through","throughout","thru","thus","to",
"together","too","took","toward","towards","tried","tries",
"truly","try","trying","twice","two","un","under","unfortunately",
"unless","unlikely","until","unto","up","upon","us","use","used",
"useful","uses","using","usually","value","various","very","via",
"viz","vs","want","wants","was","wasn't","way","we","we'd","we'll",
"we're","we've","welcome","well","went","were","weren't",
"what","what's","whatever","when","whence","whenever","where",
"where's","whereafter","whereas","whereby","wherein",
"whereupon","wherever","whether","which","while","whither","who",
"who's","whoever","whole","whom","whose","why","will","willing",
"wish","with","within","without","won't","wonder","would","would",
"wouldn't","yes","yet","you","you'd","you'll","you're","you've",
"your","yours","yourself","yourselves","zero"};

    
    
    /**
     * An array containing some common English words
     * that are usually not useful for searching.
     * <p>Taken from 
     * <a href="http://dev.mysql.com/doc/refman/5.1/en/fulltext-stopwords.html">MySQL</a>
     * (see section 11.8.4 on Full-Text Stopwords) but then edited to remove
     * apostrophes ' and one or two removals 
     * (e.g. a's became as already in list).
     * 
     */
    public static final String[] MySQL_STOP_WORDS_EDITED =
    {"able","about","above","according","accordingly","across",
"actually","after","afterwards","again","against","aint","all",
"allow","allows","almost","alone","along","already","also",
"although","always","am","among","amongst","an","and","another",
"any","anybody","anyhow","anyone","anything","anyway","anyways",
"anywhere","apart","appear","appreciate","appropriate","are",
"arent","around","as","aside","ask","asking","associated","at",
"available","away","awfully","be","became","because","become",
"becomes","becoming","been","before","beforehand","behind",
"being","believe","below","beside","besides","best","better",
"between","beyond","both","brief","but","by","cmon",
"came","can","cant","cannot","cant","cause","causes","certain",
"certainly","changes","clearly","co","com","come","comes",
"concerning","consequently","consider","considering","contain",
"containing","contains","corresponding","could","couldnt","course",
"currently","definitely","described","despite","did","didnt",
"different","do","does","doesnt","doing","dont","done",
"down","downwards","during","each","edu","eg","eight","either",
"else","elsewhere","enough","entirely","especially","et","etc",
"even","ever","every","everybody","everyone","everything",
"everywhere","ex","exactly","example","except","far","few","fifth",
"first","five","followed","following","follows","for","former",
"formerly","forth","four","from","further","furthermore","get",
"gets","getting","given","gives","go","goes","going","gone",
"got","gotten","greetings","had","hadnt","happens","hardly",
"has","hasnt","have","havent","having","he","hes","hello",
"help","hence","her","here","heres","hereafter","hereby","herein",
"hereupon","hers","herself","hi","him","himself","his",
"hither","hopefully","how","howbeit","however","id",
"im","ive","ie","if","ignored","immediate","in","inasmuch","inc",
"indeed","indicate","indicated","indicates","inner","insofar",
"instead","into","inward","is","isnt","it","itd","itll",
"its","its","itself","just","keep","keeps","kept","know","knows",
"known","last","lately","later","latter","latterly","least",
"less","lest","let","lets","like","liked","likely","little",
"look","looking","looks","ltd","mainly","many","may","maybe",
"me","mean","meanwhile","merely","might","more","moreover","most",
"mostly","much","must","my","myself","name","namely","nd","near",
"nearly","necessary","need","needs","neither","never",
"nevertheless","new","next","nine","no","nobody","non","none",
"noone","nor","normally","not","nothing","novel","now",
"nowhere","obviously","of","off","often","oh","ok","okay","old",
"on","once","one","ones","only","onto","or","other","others",
"otherwise","ought","our","ours","ourselves","out","outside",
"over","overall","own","particular","particularly","per",
"perhaps","placed","please","plus","possible","presumably","probably",
"provides","que","quite","qv","rather","rd","re","really",
"reasonably","regarding","regardless","regards","relatively",
"respectively","right","said","same","saw","say","saying","says",
"second","secondly","see","seeing","seem","seemed","seeming",
"seems","seen","self","selves","sensible","sent","serious",
"seriously","seven","several","shall","she","should","shouldnt",
"since","six","so","some","somebody","somehow","someone",
"something","sometime","sometimes","somewhat","somewhere","soon",
"sorry","specified","specify","specifying","still","sub",
"such","sup","sure","ts","take","taken","tell","tends","th",
"than","thank","thanks","thanx","that","thats","thats","the",
"their","theirs","them","themselves","then","thence","there",
"theres","thereafter","thereby","therefore","therein","theres",
"thereupon","these","they","theyd","theyll","theyre",
"theyve","think","third","this","thorough","thoroughly",
"those","though","three","through","throughout","thru","thus","to",
"together","too","took","toward","towards","tried","tries",
"truly","try","trying","twice","two","un","under","unfortunately",
"unless","unlikely","until","unto","up","upon","us","use","used",
"useful","uses","using","usually","value","various","very","via",
"viz","vs","want","wants","was","wasnt","way","we","wed","well",
"were","weve","welcome","well","went","were","werent",
"what","whats","whatever","when","whence","whenever","where",
"wheres","whereafter","whereas","whereby","wherein",
"whereupon","wherever","whether","which","while","whither","who",
"whos","whoever","whole","whom","whose","why","will","willing",
"wish","with","within","without","wont","wonder","would","would",
"wouldnt","yes","yet","you","youd","youll","youre","youve",
"your","yours","yourself","yourselves","zero"};

    
}
