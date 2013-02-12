/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimGraph.run;

import TimGraph.timgraph;

/**
 * Take a directed graph and make vertex numbers in labels match order.
 * <bR>Will try to later numbers of vertices to make sure
 * the vertex numbers (integer labels for the vertices) respect the order.
 * Following an analogy with time, the links are expected to point from
 * a vertex with a big number to one with a smaller number.
 * vertices which are timgraph.IUNSET are deemed to have no number and
 * these are set to upper and lower bounds as needed.
 * @author time
 */
public class MakeDAGFromNumbers {


    public int setNewNumber(timgraph tg){
        final  int numberVertices =tg.getNumberVertices();
        int n=timgraph.IUNSET;
        //find max and min numbers
        int nmaxall=timgraph.IUNSET;
        int nminall=timgraph.IUNSET;
        for (int v=0; v<numberVertices; v++){
            n=tg.getVertexNumber(v);
            if (n==timgraph.IUNSET)continue;
            if (nmaxall==timgraph.IUNSET) nmaxall=n;
            else  nmaxall=Math.max(nmaxall,n);
            if (nminall==timgraph.IUNSET) nminall=n;
            else  nminall=Math.min(nminall,n);
        }
        if ((nmaxall==timgraph.IUNSET) || (nminall==timgraph.IUNSET)) {
            System.err.println(
                "No vertices have any vertex numbers in labels set");
            return -1;
        }
        // now check to see if numbers are set that they respect edge directions
        int kin=-1;
        int kout=-1;
        int nmaxout=timgraph.IUNSET;
        int nminin=timgraph.IUNSET;
        int vnumber=timgraph.IUNSET;
        int vnnnumber=timgraph.IUNSET;
        int vnn=-1;
        int setSources=-1;
        int setTargets=-1;
        for (int v=0; v<numberVertices; v++){
            kin=tg.getVertexInDegree(v);
            vnumber = tg.getVertexNumber(v);
            setSources=0;
            setTargets=0;
            if (vnumber!=timgraph.IUNSET){
                nminin=nmaxall;
                for (int e=0; e<kin; e++)
                {
                    vnn=tg.getVertexSourceQuick(v, e);
                    vnnnumber=tg.getVertexNumber(vnn);
                    if (vnnnumber!=timgraph.IUNSET) {
                        setSources++;
                        nminin=Math.min(nminin,vnnnumber);
                    }
                }
                nmaxout=nminall;
                for (int e=0; e<kin; e++)
                {
                    vnn=tg.getVertexTargetQuick(v, e);
                    vnnnumber=tg.getVertexNumber(vnn);
                    if (vnnnumber!=timgraph.IUNSET) {
                        setTargets++;
                        nmaxout=Math.max(nmaxout,vnnnumber);
                    }
                }
                //NOW CHECK IF NUMBERS ARE OK
                if (nminin<vnumber && setSources>0) {
                    System.err.println(
                        "vertex "+v+" has "+kin+
                        " neighbouring sources, "+setSources+
                        " with numbers all less than "+vnumber);
                    return -2;
                }
                if (nmaxout>vnumber && setTargets>0){
                    System.err.println(
                        "vertex "+v+" has "+kin+
                        " neighbouring sources, "+setSources+
                        " with numbers all less than "+vnumber);
                    return -3;
                }

                
            }// eo if (vnnumber!=
        }
        return 0;

    }

}
