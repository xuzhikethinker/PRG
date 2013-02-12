/*
 * Test.java
 *
 * Created on 06 March 2007, 16:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package TransferMatrix;

/**
 *
 * @author time
 */
public class Test {
    
    /** Creates a new instance of Test */
    public Test() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        int dim;
        double [] [] array;
        int n=0;
        switch (n)
        {
            case 1:         
                dim=5;
                array = new double[dim][dim];
                for (int i=0; i<dim; i++)
            for (int j=0; j<dim; j++)
            {
               array[i][j] = ((i==j)?i:0);
            }
                break;
            case 0:
            default:
                // eigenvalues (1 \pm a), e/vectors (1,\mp a)
                dim=2;
                array = new double[dim][dim];
                double a=2.0;
                array[0][0]=1;
                array[1][0]=1;
                array[0][1]=a*a; 
                array[1][1]=1;
                break;

        }
        
            
        TransferMatrix tm = new TransferMatrix(array);
        tm.printTransferMatrix(System.out,"#"," \t ",6);
        tm.calcEigenVectors();
        tm.printEigenInformation(System.out,"#"," \t ",6);
        double [] r = new double[dim];
        tm.getRanking(r);
        
        System.out.println(" Ranking:-");
        for (int i=0; i<dim;i++) System.out.print(i+" \t ");
        System.out.println();
        for (int i=0; i<dim;i++) System.out.print(r[i]+" \t ");
        System.out.println();
    }
}
