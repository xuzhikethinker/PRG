/*
 * IslandArguments.java
 *
 * Created on 18 December 2006, 18:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package IslandNetworks;

/**
 *
 * @author time
 */
public class IslandArguments {
    
    final int MAXNUMBERARGS =30;
    String [] args = new String [MAXNUMBERARGS];
    int number=0;
    
    /** Creates a new instance of IslandArguments */
    public IslandArguments() {
    }
    
    /** Creates deep copy of IslandArguments */
    public IslandArguments(IslandArguments ia) {
//        setArguments(ia.args)
        
    }
    
    public void setArguments(String [] setArgs)
    {
        number=0;
        addArguments(setArgs);
    }
    
    public void addArguments(String [] addArgs)
    {
        for (int a=0; a<addArgs.length; a++) addArgument(addArgs[a]);
    }

        public void addArgument(String arg)
    {
        args[number++]=arg;
    }

}
