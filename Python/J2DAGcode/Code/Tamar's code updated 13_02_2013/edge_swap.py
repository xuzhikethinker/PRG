'''
Created on Feb 12, 2013

@author: TamarLoach
'''
import networkx as nx
import networkx.utils as utils
import random
import models
import degree_distribution as dd
import trans_red as tr

def connected_double_edge_swap_with_birthday_check(G, nswap=1):

    """
    Completes nswap double-edge swaps on the graph G.

    A double-edge swap removes two randomly chosen edges u->v and x->y
    and creates the new edges u->x and y->v, provided this move retains the 'birthday' ordering of the nodes in the original edges.

    Parameters G=A directed graph, nswap : integer = Number of successful double-edge swaps to perform.

    Returns The number of successful swaps
    """
#uncomment below if want to ensure connectedness of initial graph. This should be true anyway for all our models/data, unless edge removal used
#    if not nx.is_connected(G):
#        raise nx.NetworkXError("Graph not connected")
#    if len(G) < 4:
#        raise nx.NetworkXError("Graph has less than four nodes.")
    n=0
    swapcount=0
    deg=G.in_degree()
    dk=list(deg.keys()) # Label key for nodes
    cdf=utils.cumulative_distribution(list(G.in_degree().values()))
    window=1
    while swapcount < nswap:
        wcount=0
        swapped=[]
        while wcount < window and  swapcount < nswap:
            # Pick two random edges without creating edge list
            # Choose source nodes from discrete degree distribution
            (ui,xi)=utils.discrete_sequence(2,cdistribution=cdf)
            if ui==xi:
                continue # same source
            u=dk[ui] # convert index to label
            x=dk[xi]
            # Choose targets uniformly from neighbors
            u_neighbors = G.neighbors(u)
            x_neighbors =G.neighbors(x)
            if len(u_neighbors)!=0 and len(x_neighbors) !=0:
                v=random.choice(u_neighbors)
                y=random.choice(x_neighbors)
                if v==y: continue # same target
                if models.birthday_check(G, x, v)==False or models.birthday_check(G, u, y)==False: 
                    print "birthday condition not met" 
                    continue
                else: 
                    if (not G.has_edge(x,v)) and (not G.has_edge(u,y)):
                        G.remove_edge(u,v)
                        G.remove_edge(x,y)
                        G.add_edge(x,v)
                        G.add_edge(u,y)
                        swapped.append((u,v,x,y))
                        swapcount+=1
                        print "swapcount is = ", swapcount
                n+=1
                wcount+=1
#uncomment below if want to ensure connectedness of final graph, is this necessary? WIll be for some measures, but not for k_in...?        
#        UG=G.to_undirected()
#        if nx.is_connected(UG):
#            window+=1
#        else:
#            "graph has become disconnected, undoing changes that caused this"
#            # not connected, undo changes from previous window, decrease window
#            while swapped:
#                (u,v,x,y)=swapped.pop()
#                G.add_edge(u,v)
#                G.add_edge(x,y)
#                G.remove_edge(u,x)
#                G.remove_edge(v,y)
#                swapcount-=1
#            window = int(math.ceil(float(window)/2))
#            print "swapcount = " , swapcount
    return swapcount
