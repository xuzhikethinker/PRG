/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph;

/**
 * Prints limited number of warning to System.out or System.err
 * @author time
 */
public class Warning {

    int number=0;
    int maxNumber=20;

    /**
     * Set with given default maximum number of warnings
     */
    public Warning(){}

    /**
     * Set with given maximum number of warnings
     * @param max maximum number of warnings
     */
    public Warning(int max){maxNumber=max;}

    public void nextOut(String message){
      if ((number++)< maxNumber) System.out.println("!!! Warning "+message);
      if ((number)== maxNumber) System.out.println("!!! Warning no more warnings given");
    }
    public void nextErr(String message){
      if ((number++)< maxNumber) System.out.println("!!! Warning "+message);
      if ((number)== maxNumber) System.out.println("!!! Warning no more warnings given");
    }
}
