/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TestCommonsMath;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.IllinoisSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.special.Beta;

/**
 * Designed to test out root solving routine.
 * @author time
 */
public class TestRootSolving {

    static final double DUNSET = -24680e67;

    public static void main(String[] args)
    {
        double [] darray = {1.0,2.0,3.0,20.0};
        for (int i=0; i<darray.length; i++ ){
            double d= darray[i];
            double ratio = Math.exp(logMryheimMeyersDimension( d));
            double sol = findDimension(ratio);
            System.out.println(d+", solution="+sol+", diff = "+(d-sol));
        }

    }
    /**
     * Solves for dimension of Euclidean space.
     * <p>The parameter is the ratio of the number of two chains to the
     * square of the number of points in a given causal set interval x &lt;  y.
     * That is the set of all points z where  x &lt; z &lt;  y.
     * <p>Based on equation (2) of D.Reid, PRD 67 (2003) 024034
     * @see http://commons.apache.org/math/userguide/analysis.html#a4.3_Root-finding
     * @param ration
     * @return
     */
    static public double findDimension(double ratio){
        UnivariateFunction function = new logMryheimMeyersDimensionFunction(ratio); // some user defined function object
        final double relativeAccuracy = 1.0e-8;
        final double absoluteAccuracy = 1.0e-6;
        //final int    maxOrder         = 5;
        //double functionValueAccuracy
        UnivariateSolver solver   = new IllinoisSolver(relativeAccuracy, absoluteAccuracy);
                //new BracketingNthOrderBrentSolver(relativeAccuracy, absoluteAccuracy, maxOrder);
        double d;
        int maxEval=100;
        double min=0.5;
        double max=100.0;
        //AllowedSolution allowedSolution=AllowedSolution.ANY_SIDE;
        try {
           d = solver.solve(maxEval, function, min, max);
        } catch (RuntimeException e) {
          System.err.println("*** Error "+e);
          return DUNSET;
            // Retrieve the x value.
        }
        return d;
    }

    /**
     * Natural logarithm of Mryheim-Meyers Dimension function.
     * <p>Based on equation (2) of D.Reid, PRD 67 (2003) 024034
     * Gives the number of two chains divided by the square of the number of points
     * in a given causal set interval x &lt;  y.  That is the set of all points
     * z where  x &lt; z &lt;  y.
     * @see http://commons.apache.org/math/userguide/special.html#a5.4_Beta_funtions
     * @param d
     * @return
     */
   static  public double logMryheimMeyersDimension(double d){
        return Math.log(3*d/8)+Beta.logBeta(d+1,d/2) ;
    }

private static class LocalException extends RuntimeException {
     // The x value that caused the problem.
     private final double x;

     public LocalException(double x) {
         this.x = x;
     }

     public double getX() {
         return x;
     }
 }

     /**
     * Natural logarithm of Mryheim-Meyers Dimension function.
     * <p>Based on equation (5) of D.Reid, PRD 67 (2003) 024034
     * Gives the number of two chains divided by the square of the number of points
     * in a given causal set interval x &lt;  y.  That is the set of all points
     * z where  x &lt; z &lt;  y.
     * @see http://commons.apache.org/math/userguide/special.html#a5.4_Beta_funtions
     */
    public static class logMryheimMeyersDimensionFunction implements UnivariateFunction {
     static double lnratio=0.0;

     public logMryheimMeyersDimensionFunction(double ratio){
         lnratio=Math.log(ratio*8/3);
     }

     /**
      * Value should be zero when d satisfies Mryheim-Meyers dimension.
      * <p>The residual is ln(S2/(N^2))- ln(f(d))
      * @param d dimension of space-time
      * @return residual
      */
     public double value(double d) {
         double y = 0;
         try {
            y= lnratio-Math.log(d)-Beta.logBeta(d+1,d/2) ;
         } catch (RuntimeException e){
             throw new LocalException(d);
         }
         return y;
     }
 }


}
