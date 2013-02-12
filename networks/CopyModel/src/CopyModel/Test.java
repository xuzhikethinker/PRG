/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package CopyModel;

/**
 * Simple routine to test CopyModel.
 * @author time
 */
public class Test {

        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        CopyModel cm = new CopyModel();
        System.out.println("\n***********************************************************");
        System.out.println("       STARTING CopyModel.Test, CopyModel version "+ CopyModel.VERSION+" on "+cm.date+"\n");
        
//        cm.numberIndividuals = 1000;
//        cm.numberArtefacts = 1000;
//        cm.initialIndividuals = cm.numberIndividuals;
//        cm.initialArtefacts = cm.numberArtefacts;
//        
//        cm.numberRewiringsPerEvent=cm.initialIndividuals;
//        double prfraction=10.0;
//        cm.prob.setPrPbarZero(prfraction/((double) cm.initialIndividuals) );
//        cm.numberRewiringsTotal = cm.initialIndividuals * 5; //*  cm.initialIndividuals ;
//        //cm.ppMode = 1; // Random walk on individual graph
//        cm.numEventsPerUpdate = cm.initialIndividuals; // update time stats once per generation
//        cm.rewireMode.set(6);
//        cm.ppMode = 0;
//        cm.outputControl.set(32);
//        cm.artDDStatsTemplate.statisticsMode=35;
//        cm.artDDStatsTemplate.maxMoment=3;
//        
//        cm.infolevel=0;
//        
//        //cm.numberkvalues=-1;  // number of k values to follow, includes 0 and E
//        cm.initialBiGraph =0;
// The next line is for testing the Minority Game analogy
        String [] aMG = {"-firing201k20", "-foMGringni201k20pr0.04877", "-tt1000000", "-tu100000", "-prp-0.04877", "-ppm1", "-prm0", "-mr3", "-g0", "-o32", "-r2"};
//
//        String [] aBasic = {"-tt100", "-tu10", "-te1", "-tf50", "-ni10", "-na10", "-prp-0.9", "-mr4", "-r1", "-sm130", "-sY5", "-xi1", "-o32"};
        String [] aBasicIndNetRing = {"-tt200", "-tu20", "-te1", "-tf40", "-ni20", "-na20", "-prp-0.9", "-mr6", "-r1", "-ig-gn-2", "-ig-gm2", "-ig-gvv20", "-saxt", "-sam7", "-sakb20", "-xi1", "-o32"};
        String [] aBasicIndNetRingInfl = {"-tt200", "-tu20", "-te1", "-tf40", "-ni20", "-na20", "-ppp-0.9", "-mr6", "-r2", "-ig-gn-2", "-ig-gm2", "-ig-gvv20", "-saxt", "-sam7", "-sakb20", "-It", "-sixt", "-sim7", "-sikb20", "-xi1", "-o32"};
        String [] aBasicInfl = {"-tt100", "-tu10", "-te1", "-tf50", "-ni10", "-na10", "-prp-0.9", "-mr6", "-r1", "-It", "-sixt", "-sim7", "-xi1", "-o7"};
//        String [] a = {"-tt100", "-tu1", "-te2", "-ni10", "-na10", "-prp-0.9", "-mr18", "-r1", "-ppm3", "-prm0", "-sf6", "-sm35", "-su3", "-sY5", "-xi0"};
//          String [] a = {"-tt100000", "-tu100", "-te1", "-tf500", "-ni100", "-na100", "-prp-0.1", "-mr6", "-r10", "-sm131", "-sY10", "-sf3",  "-su3", "-xi0", "-o32"};
        
//        String [] a = {"-tt1000", "-tu1000", "-te1000", "-ni1000", "-na1000", "-prp-0.01", "-mr8", "-r1", "-ppm0", "-prm0", "-sf6", "-sm35", "-su3", "-xi-1"};
        String []  a = aBasicIndNetRingInfl;
        if (args.length==0) { if (cm.parseParam(a)>0) return;}
        else {if (cm.parseParam(args)>0) return;}
//        cm.inputNameRoot="WStest";
//        cm.getInputFile=true;
        if (cm.getInputFile) {if (cm.parseParameterFile()>0) return;}
//        int initialIndividuals = cm.numberIndividuals;
//        int initialArtefacts = cm.numberArtefacts;
                
        cm.runGeneralModel() ;
    
    }

    
}
