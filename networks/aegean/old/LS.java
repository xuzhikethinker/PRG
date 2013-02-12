// **************************************************************************
   /** LS class
     */

public class LS {

    int [][] entry;
    int [][] rowlist;
    int [][] collist;
    int assoc1, assoc2, assoc3;
    boolean assocflag;

    /** Creates a new
     *
     * instance of LS */
    public LS(int dim)
    {
     entry = new int[dim][dim];
     rowlist = new int[dim][dim];
     collist = new int[dim][dim];
     assoc1=dim;
     assoc2=dim;
     assoc3=dim;
     assocflag=true;

     }
    }
