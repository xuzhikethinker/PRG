/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import java.util.ArrayList;

/**
 *
 * @author time
 */
public class OrganisationalUnit {
     /**
     * Negative integer used to indicate unset value. {@value}
     */
    public static final int UNSET=-912345687;
    

    int level=UNSET;
    int index=UNSET;
    String name="";
    
    OrganisationalUnit parentUnit;
    
    ArrayList<OrganisationalUnit> subUnits;
    
    public OrganisationalUnit(String name, OrganisationalUnit parent, int l, int i){
        this.name=name;
        this.parentUnit=parent;
        level=l;
        index=i;
        subUnits = new ArrayList();
    }
    
    public boolean isInUnit(String sections[]){
        if (!name.equals(sections[level])) return false;
        if (sections.length ==level-1) return true;
        for (int u=0; u<subUnits.size(); u++) {
            if (subUnits.get(u).isInUnit(sections)) return true;
        }
        return false;
    }
        
    public void addSubUnits(String sections[]){
       int u=findSubUnit(sections);
       if (u==subUnits.size()) subUnits.add(new OrganisationalUnit(sections[level],this,level+1,subUnits.size())); 
       subUnits.get(u).addSubUnits(sections);
    }
    
    public int findSubUnit(String sections[]){
       for (int u=0; u<subUnits.size(); u++) {
           if (subUnits.get(u).name.equals(sections[level+1])) return u;
       }
       return subUnits.size();
    }
}
