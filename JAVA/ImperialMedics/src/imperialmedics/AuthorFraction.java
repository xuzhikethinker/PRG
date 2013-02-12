package imperialmedics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.util.ArrayList;

/**
 * Defines fractional ownership of paper based on position of author.
 * The r highest value is normalisation/r where normalisation is set so that
 * the sums of scores is 1.0.
 * The first author always has the highest score (r=1).
 * The last author always has the second highest score (r=2).
 * The second author always has the third highest score (r=3).
 * The penultimate author always has the fourth highest score (r=4).
 * For other positions p the score is the (p+2) highest (r=p+2).
 * We go down this list and thake the first (highest) score we come to that
 * is satisfied for  that paper.
 * Examples
 * <ul>
 * <li>Sole author has score 1.0 and owns the whole paper. </li>
 * <li>For a two author paper we have values of 2/3 and 1/3 for first
 * and last author.</li>
 * <li>Three author papers have scores 6/11, 2/11 and 3/11
 * for first, second and third authors respectively.</li>
 * <li>For papers with four authors it gives 12/25, 4/25, 3/25, 6/25
 * to first, second, third(penultimate) and fourth (last) authors.
 * </li>
 * </ul>
 * @author time
 */
public class AuthorFraction {

    /**
     * score.get(n-1)[p-1] gives the fractional
     * ownership of an author in position p on paper with n authors.
     */
    ArrayList<double []> score;

    /**
     * Maximum number of authors with individual scores.
     * Set in constructor
     */
    private int MAXNUMBERAUTHORS;

    /**
     * Constructor.
     * @param maxNumberAuthors maximum number of authors
     */
    public AuthorFraction(int maxNumberAuthors){
        MAXNUMBERAUTHORS=maxNumberAuthors;
        if (maxNumberAuthors<4) MAXNUMBERAUTHORS=4;
        if (maxNumberAuthors>100) MAXNUMBERAUTHORS=100;
        score = new ArrayList();
        score.add(new double[1]);
        score.get(0)[0]=1.0; //set solo author score
    }

    public static void main(String[] args) {
        int maxa=6;
        AuthorFraction af = new AuthorFraction(maxa);
        for (int na=1; na<=maxa; na++){
            System.out.println(na+": "+af.toString(", ", na));
        }
        System.out.println("..........................");
        for (int na=1; na<=maxa+4; na++){
            System.out.print(na+": ");
            for (int a=1; a<=na; a++)System.out.print(String.format("%6.4f",af.getScore(a,na))+((a<na)?", ":""));
            System.out.println();
            
        }
    }
    /**
     * Gives fractional ownership of paper based on position of author.
     * If inputs illegal negative number is returned.
     * @param primaryAuthorPosition position of author, 1st =1, last = numberAuthors
     * @param numberAuthors number of authors on paper
     * @return ownership fraction
     */
    public double getScore(int primaryAuthorPosition, int numberAuthors)
    {
        if ( (primaryAuthorPosition > numberAuthors) || (primaryAuthorPosition <1) || ( numberAuthors<1) )
            return -963147.0;
        if (numberAuthors>MAXNUMBERAUTHORS ) return getScoreLargePaper(primaryAuthorPosition, numberAuthors);
        if ((numberAuthors>score.size()) || (score.get(numberAuthors-1)==null)) calcScore(numberAuthors);
        return score.get(numberAuthors-1)[primaryAuthorPosition-1];
    }

    /**
     * Sets scores for when there are numberAuthors authors on paper.
     * @param numberAuthors
     */
    public void calcScore(int numberAuthors){
        if (numberAuthors>MAXNUMBERAUTHORS) return;
        while (numberAuthors>score.size()) {
            score.add(null);
        }
        double [] sa = new double [numberAuthors];
        double t=0;
        for (int p=0; p<numberAuthors; p++) {
            sa[p]=1.0/(p+1);
            t+=sa[p];
        }
        for (int p=0; p<numberAuthors; p++) sa[p]=sa[p]/t; // normalisation
        // now must swap scores to deal with last and penultimate papers properly
        if (numberAuthors==3){ // swap scores for last and middle author
            double d=sa[2];
            sa[2]=sa[1];
            sa[1]=d;
        }
        if (numberAuthors>3){ // move score 1, 2,3 to set for last, second and penultimate authors
            double last=sa[1];
            sa[1]=sa[2]; // second author score
            double penu=sa[3];
            for (int p=4; p<numberAuthors; p++) sa[p-2]=sa[p];
            sa[numberAuthors-2]=penu;
            sa[numberAuthors-1]=last;
        }
        score.set(numberAuthors-1,sa);
    }

    /**
     * Gives fractional ownership of paper based on position of author.
     * This is for large number of authors on paper.
     * If inputs illegal negative number is returned.
     * @param primaryAuthorPosition position of author, 1st =1, last = numberAuthors
     * @param numberAuthors number of authors on paper
     * @return ownership fraction
     */
    public double getScoreLargePaper(int primaryAuthorPosition, int numberAuthors){
        calcScore(MAXNUMBERAUTHORS);
        int n=score.size()-1;
        if (primaryAuthorPosition==numberAuthors) return score.get(n)[n]; // last author score
        if (primaryAuthorPosition==(numberAuthors-1)) return score.get(n)[n-1]; // penultimate author score
        if (primaryAuthorPosition>(MAXNUMBERAUTHORS-2)) return 0.0; // Other authors score
        return score.get(n)[primaryAuthorPosition-1];
    }

    /**
     * Gives string with name of fraction.
     * <p>This is AF followed by maximum number of authors.
     * @return string with name of fraction.
     */
    String getName(){
        return "AF"+this.MAXNUMBERAUTHORS;
    }

    String toString(String sep, int numberAuthors){
        String s="";
        for (int p=0; p<numberAuthors; p++) s=s+
                (p==0?"":sep)+String.format("%6.4f", getScore(p+1,numberAuthors));
        return s;
    }

}
