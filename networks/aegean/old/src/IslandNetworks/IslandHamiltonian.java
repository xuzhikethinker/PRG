/*
 * IslandHamiltonian.java
 *
 * Created on 26 July 2006, 18:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

import java.io.PrintStream;
import TimUtilities.NumbersToString;

/**
 * Characteristcis of the Hamiltonians for Island Networks.
 * @author time
 */
public class IslandHamiltonian {
    
        final static String IHVERSION = "iN060727";
        /**
         * Defines the short distance potential value once used in MC updates to exlcude short distance edges.
         * Value = {@value}
         */
        public final static double SHORTDISTANCEPOTENTIAL = -918273645;
        static NumbersToString n2s = new NumbersToString();
             
    
        double edgeSource;
        double vertexSource;
        double alpha ;
        double beta ;
        double gamma ;
        double kappa ;
        private double kappaZ; // normalisation factor for kappa needed for model 5
        double lambda ;
        double distanceScale ;
        double shortDistanceScale;
        private double b; // power used for site variables
//        boolean bEqualsOneH; // true if bH == 1
        double outputcoeff;
        double consumptioncoeff;
        
    
    /** Creates a new instance of IslandHamiltonian. */
    public IslandHamiltonian() {
             edgeSource = 0.5; // mu 
             vertexSource = 0.0; // j
             beta = 0.001;  // doubles as the number of edges per site in PPA mode
             alpha = 4.0;
             gamma = 1.0;
             kappa = 1.0 ;
             lambda = 4.0 ;
             distanceScale = 100;
             shortDistanceScale = distanceScale/20.0;
             setb(1.2);
             outputcoeff=1.0;
             consumptioncoeff=2.0*outputcoeff;   
             
    }
    
    /*
     * Deep copies IslandHamiltonian.
     *@param H Island Hamiltonian to be copied
     */
    public IslandHamiltonian(IslandHamiltonian H) {
             edgeSource = H.edgeSource ; // mu 
             vertexSource = H.vertexSource ; // j
             beta = H.beta;  // doubles as the number of edges per site in PPA mode
             alpha = H.alpha;
             gamma = H.gamma;
             kappa = H.kappa ;
             lambda = H.lambda ;
             distanceScale = H.distanceScale;
             shortDistanceScale = H.shortDistanceScale;
             setb(H.getb());
             outputcoeff= H.outputcoeff;
             consumptioncoeff= H.consumptioncoeff;   
             
    }
    
    /*
     * Returns value of b.
     *@return b parameter
     */
    public double getb() { return (b);
    }
    /*

     * Sets value of b and kappa normalisation for model 5.
     *@param newb is the new value for b.
     *@return b parameter
     */
    public double setb(double newb) { 
        b=newb;
        kappaZ = Math.pow(b,b/(b-1))*(b-1);
    return (b);
    }
    
       /** Sets Parameters of Hamiltonian.
         * @param j is source for sum of site values
         * @param mu is source for sum of edge values
         * @param bt is inverse temperature (beta)
         * @param bs is source power term (b)
         * @param a is scale for short edge scales (alpha)
         * @param k site term coefficient (kappa)
         * @param g beta&*gamme is power for long edge scales (gamma)
         * @param l trade term coefficient (lambda)
         * @param ds scale for edge potential (distanceScale)
         * @param ss short distance scale for edge potential (shortDistanceScale)
         */
    public void setHamiltonianParameters(double j, double mu, double bt, double bs, double a, double k,  double g, double l, double ds, double ss) {
             edgeSource = mu;
             vertexSource = j;
             beta = bt;
             setb(bs);
             alpha = a;
             gamma = g;
             kappa = k;
             lambda = l;
             distanceScale = ds;
             shortDistanceScale = ss;
    }//eo setHam    
    
        /* 
         * Shows Parameters of Hamiltonian on a PrintStream.
         *@param PS a print stream such as System.out
         *@param SepString string used as a separator such as tab or space
         */
    public void printParameters(PrintStream PS, String SepString) {
             PS.println("   vertexSource (j) "+SepString+vertexSource);
             PS.println("     edgeSource (mu)"+SepString+edgeSource );
             PS.println("              kappa "+SepString+kappa);
             PS.println("             lambda "+SepString+lambda);
             PS.println("          distScale "+SepString+distanceScale);
             PS.println("     shortDistScale "+SepString+shortDistanceScale);
             PS.println("               beta "+SepString+beta);
             PS.println("                  b "+SepString+b);
             PS.println("              alpha "+SepString+alpha);
             PS.println("              gamma "+SepString+gamma);
             
    }//eo showHam
    
    /* 
         * Shows parameters of Hamiltonian on a PrintStream suitable for a data file.
         *@param PS a print stream such as System.out
         *@param SepString string used as a separator such as tab or space
         */
    public void printParametersForData(PrintStream PS, String SepString) {
             PS.println("j"+SepString+vertexSource);
             PS.println("mu"+SepString+edgeSource );
             PS.println("kappa"+SepString+kappa);
             PS.println("lambda "+SepString+lambda);
             PS.println("distScale"+SepString+distanceScale);
             PS.println("shortDistScale"+SepString+shortDistanceScale);
             PS.println("beta"+SepString+beta);
             PS.println("b"+SepString+b);
             PS.println("alpha"+SepString+alpha);
             PS.println("gamma"+SepString+gamma);
             
    }//eo showHam
 
    
            /* 
         * Gives Hamiltonian values as string suitable for Parse routine.
         *@param SepString string used as a separator such as tab or space
         */
    public String inputParametersString(String SepString) {
             String s="";
             s=s+SepString+"-j"+vertexSource;
             s=s+SepString+"-m"+edgeSource;
             s=s+SepString+"-k"+kappa;
             s=s+SepString+"-l"+lambda;
             s=s+SepString+"-dl"+distanceScale;
             s=s+SepString+"-ds"+shortDistanceScale;
             s=s+SepString+"-bt"+beta;
             s=s+SepString+"-bs"+b;
             s=s+SepString+"-a"+alpha;
             s=s+SepString+"-g"+gamma;
             return s;
    }//eo inputParametersString
 
            /* 
         * Gives Hamiltonian values as string suitable for Parse routine or for use in simple output.
         *@param firstItem string for first item in list.  If null or empty string then first separator is missed out. 
         *@param SepString string used as a separator such as tab or space
         *@param dec number of decimal points to use
         * @todo Use <tt>java.util.Formatter</tt> and <tt> and the <tt>System.out.format(.....)</tt>
         */
    public String inputParametersString(String firstItem, String SepString, int dec) {
             n2s.setDecimalPlaces(dec);
             String s="";
             if (firstItem.length()>0) s=firstItem+SepString;
             s=s+"-j"+n2s.toString(vertexSource); //+"-j"+n2s.toString(vertexSource);
             s=s+SepString+"-m"+n2s.toString(edgeSource);
             s=s+SepString+"-k"+n2s.toString(kappa);
             s=s+SepString+"-l"+n2s.toString(lambda);
             s=s+SepString+"-dl"+n2s.toString(distanceScale);
             s=s+SepString+"-ds"+n2s.toString(shortDistanceScale);
             s=s+SepString+"-bt"+n2s.toString(beta);
             s=s+SepString+"-bs"+n2s.toString(b);
             s=s+SepString+"-a"+n2s.toString(alpha);
             s=s+SepString+"-g"+n2s.toString(gamma);
             return s;
    }//eo inputParametersString
 

    /* 
         * Gives string rtepresenting Hamiltonian parameters.
     *<br> Useful for file names etc.
         *@param SepString string used as a separator such as tab or space
         */
    public String parameterString(String SepString)
    { String s="m"+this.edgeSource+SepString+
                    "j"+this.vertexSource+SepString+
                    "k"+this.kappa+SepString+
                    "l"+this.lambda+SepString+
                    "b"+this.b+SepString+
                    "s"+this.distanceScale;
      return s;
    }
    /**
     * Gives the pure site potential term for model 1.
     * @param size fixed value of site
     * @param value is the variable value of site
     * @return the pure site potential term for model 1.
     */
    public double vertexPotential1(double size, double value) {
    return(  kappa*4.0*value*(1.0-value)*size );
    }
    
  
    
    /**
     * Gives the pure site potential term for model 5 - to be scales by.
     * <br> This should have a maximum for some positive value if kappa is positive.
     * Then the energy contains minus this term. For instance for b=2
     * we should reproduce the model one potential which we do as kappaZ is negative.
     * @param size fixed value of site
     * @param value is the variable value of site
     * @return the pure site potential term for model 5.
     */
    public double vertexPotential5(double size, double value) {
    return(  kappa*kappaZ*size*(value-Math.pow(value,b)) );
    }
    
    /**
     * Gives the potential for model 1 between two site variables, modified by short distance and lambda.
     * <p>The potential times lambda times a linear weighting,
     * i.e. <tt>w_{ij} \lambda V_1(d_{ij}/d_s)</tt> where
     * <tt>W_{ij}</tt> is the linear weighting for the edge.
     * @param distance the distance between two sites
     * @param linearWeighting a weighting factor
     * @return returns the (edge potential for model 1)* lambda * linearWeighting .
     */
    public double edgePotential1(double distance, double linearWeighting) 
    {
    return(  linearWeighting*lambda * edgePotential1Bare(distance) );
    }
    /**
     * Gives the potential for model 1 between two site variables suitable for Monte Carlo updates.
     * <p>Thus it is modified by short distance and lambda so is equal for long distances to
     * the bare potential times lambda, i.e. <tt>\lambda V_1(d_{ij}/d_s)</tt>.
     * <br>For short distances d &gt; <tt>shortDistanceScale</tt> gives {@value #SHORTDISTANCEPOTENTIAL}
     * Try (1/x)^8 for analytic short distance behaviour
     * @param distance the distance between two sites
     * @return returns the edge potential * lambda for model 1 for d &gt; <tt>shortDistanceScale</tt>, {@value #SHORTDISTANCEPOTENTIAL}  for short distances.
     */
    public double edgePotential1MC(double distance) 
    {
    return(  (distance<shortDistanceScale) ? SHORTDISTANCEPOTENTIAL : lambda * edgePotential1Bare( distance)  );
    }
     
    /**
     * Gives the bare potential for model 1 between two site variables, no short distance, lambda or weighting factors.
     * <p>The basic potential <tt>V(d_{ij}/d_s)</tt> wiyth no factor of lambda.
     * <br>For short distances d &gt; <tt>shortDistanceScale</tt> gives zero.
     * @param distance the distance between two sites
     * @return returns the edge potential * lambda for model 1 for d &gt; <tt>shortDistanceScale</tt>, zero for short distances
     */
    public double edgePotential1Bare(double distance) 
    {
//    return(  (distance<shortDistanceScale) ? 0 :  Math.pow((Math.pow((distance/distanceScale), alpha) +1.0 ), -gamma) );
    return(  Math.pow((Math.pow((distance/distanceScale), alpha) +1.0 ), -gamma) );
    }
     
 

}
