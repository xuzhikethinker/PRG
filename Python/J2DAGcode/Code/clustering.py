# clustering.py
# use to calculate the clustering coefficients of a DAG


import networkx as nx
from random import choice

# Here, the clustering is defined in direct anology to undirected graphs, the difference being that the ordering defines predecessors
# and successors. The coefficient is then the ratio between the number of actual links between a given node's successors/predecessors and the
# number of possible links.
# The zero clustering coefficient considers clustering between the predeccessors and successors of a node
############################################################################################################################################
# clustering
#
# Argument: DAG - a Networkx DiGraph object
#
# Returns:   List -
#            [0]  - The clustering coefficient in the positive time direction averaged across the network
#            [1]  - The clustering coefficient 'across' the network (the 'zero' clustering coefficient)
#            [2]  - The clustering coefficient in the negative time direction averaged across the network
#
# Adds Node Attributes: 'clustering_plus'  - The clustering coefficient in the positive time direction from a given node
#                       'clustering_zero'  - The clustering coefficient 'across' the node
#                       'clustering_minus' - The clustering coefficient in the negative time direction from a given node
#
# NB A second argument can be used if the user is only interested in the clustering in a single time direction:
# Optional Argument: type - string (use 'plus' to specify the positive time direction or 'minus' to specify the negative time direction)
#
#############################################################################################################################################

def clustering(DAG, type='both'):
    # This function is a wrapper that calls the calculate_clustering functions and returns answers in a list
    # If the 2nd argument is blank (or 'both'), then the program calculates the clustering in both time directions and returns both results
    if type == 'both':
        clustering_plus = calculate_clustering(DAG,'plus')
        clustering_zero = calculate_c0(DAG)
        clustering_minus = calculate_clustering(DAG,'minus')
        print 'The average clustering across the network in the positive time direction is %f' %clustering_plus
        print 'The average clustering across the network between is %f' %clustering_zero
        print 'The average clustering across the network in the negative time direction is %f' %clustering_minus
        return [clustering_plus,clustering_zero,clustering_minus]
        
    #If user specifies that they only want to know the clustering in the plus or minus direction
    elif type == 'plus' or type == 'minus':
        return calculate_clustering(DAG,type)
	
    #If user specifies that they only want to know the zeroth clustering	
    elif type == 'zero':
	    return calculate_zero(DAG)
    
    #Returns an error message if a second argument is used that is not 'both', 'plus' or 'minus'    
    else:
        print "Error: Do not recognise second argument - Please use 'plus','zero','minus' or 'both' to examine both + and - clustering"
        
        

#Carries out calculation of the clustering coefficient for each node, and returns the average across the network in either the forward or backward
#time direction, depending on whether type = 'plus' or 'minus        
def calculate_clustering(DAG,type):
    nodes = DAG.nodes()
    total_clustering = 0. #Keeps track of the sum of the individual clustering coefficients, for calculation of the average
    
    for node in nodes:
        #The only difference between the plus and minus clustering is whether the predecessors or successors of the nodes are considered
        if type == 'plus':
            things = DAG.predecessors(node)
        elif type == 'minus':
            things = DAG.successors(node)
            
        if len(things) > 1: #cannot have any of this type of clustering if dead end or only connect to one node
            i = 0.
            
            for thing in things: #for each of the preds/succs of node, test whether each succ has any common succs with node
                
                if type == 'plus':
                    things2 = DAG.predecessors(thing) #creates list of each of the successors of each of node's successors
                if type == 'minus':
                    things2 = DAG.successors(thing)
                
                for thing2 in things2:
                    if thing2 in things: #if a triangle is formed...
                        i+=1. #increase the counter by 1...
                        
            s = len(things)
            max_pairs = float((s*(s-1.))/2.) #calculates the maximum number of pairs that could be formed between a node's succs/preds
            c = float(i)/max_pairs #this is the ratio of the number of pairs formed to those that could possibly be formed between a node's succs/preds
            
        else:
            c = 0. #if the node has no or a single pred/succ, then no pairs are formed between them and the clustering coefficient is 0
        
        #Adds a new attribute to every node indicating the clustering in the +/- direction from that node
        if type == 'plus':
            DAG.node[node]['clustering_plus'] = c
        elif type == 'minus':            
            DAG.node[node]['clustering_minus'] = c
            
        total_clustering += c
    average_clustering = total_clustering/len(nodes) #averages each node's clustering coefficient across the network
    return average_clustering
    
def calculate_c0(DAG):
    nodes = DAG.nodes()
    total_clustering = 0.
    
    for node in nodes:
        preds = DAG.predecessors(node)
        succs = DAG.successors(node)
        
        if len(preds) == 0 or len(succs) == 0:
            c = 0.
        else:
            i = 0.
            for pred in preds:
                for thing in DAG.successors(pred):
                    if thing in succs:
                        i+=1.
            max_pairs = len(preds) * len(succs)
            c = i/max_pairs
        
        DAG.node[node]['clustering_zero'] = c
        
        total_clustering += c
    average_clustering = total_clustering/len(nodes)
    return average_clustering    
                        
    
# calculate_2c
# Calculates 2nd order clustering - types are '++', '0+', '0-', '--'
def calculate_2c(DAG, type):
    nodes = DAG.nodes()
    total_clustering = 0.
    counter = 0.
    total = len(nodes)
    for node in nodes:
        counter += 1
        print (str((counter/total) * 100) + '%')
        i = 0. # counts links
        c = 0. # counts clustering 
        s = 0. # counts possible links        
        if type == '--':
            things1 = DAG.successors(node)
        else: # type is ++
            things1 = DAG.predecessors(node)
            
        if len(things1) > 1:
            for thing1 in things1:
                if type == '--':
                    things2 = DAG.successors(thing1)
                else: # type is ++
                    things2 = DAG.predecessors(thing1)
                    
                if len(things2) > 0: # NB: this SHOULD be 0 and not 1
                    for thing2 in things2:
                        if type == '--':
                            things3 = DAG.successors(thing2)
                        else:
                            things3 = DAG.predecessors(thing2)
                            
                        if len(things3) > 0:
                            for thing3 in things3:
                                if thing3 in things1:
                                    i += 1.
                                    
                                        
                            s += len(things3)
                        else:
                            i = 0.
                            s = 1.
                else:
                    i = 0.
                    s = 1.
        else:
            i = 0.
            s = 1.
                            
        c = i/s
        total_clustering += c
        
    average_clustering = total_clustering/len(nodes)
    return average_clustering    
                    
                
                    