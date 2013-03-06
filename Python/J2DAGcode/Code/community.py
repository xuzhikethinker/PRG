#community.py
#use to detect communities in a DAG
#DISCLAIMER: Not neccessarily of any use to anyone, but it certainly seems to do something
import networkx as nx
from random import choice
from collections import defaultdict

############################################################################################################################################
#IDEA BEHIND PROGRAM
#
#This code uses the notion of 'redundancy' to place nodes in a DAG into communities
#Redundancy measures how many alternative paths there are between the two nodes at either end of an edge
#The more alternative paths, the more redundant the edge
#The 'order' of the redundancy, k, caps the length of alternative paths considered
#So, second order redundancy just counts 2-chains between the two nodes, third order counts 2 and 3-chains etc
#If the redundancy of an edge is high, above the 'threshold' value, the program places these nodes in the same community
#If there are still nodes unassigned to a community, the communities are propogated across the network until every node is assigned
############################################################################################################################################
# community
#
# Argument: DAG         - a Networkx DiGraph object
#           k           - an integer, indicates the order of redundancy to be considered (i.e. count from 2-chain up to k-chain alternative paths)
#           threshold   - an integer, if the 'redundancy' is above this threshold, the nodes in the edge will be placed in the same community
#
# Returns:  List        - integers, where each integer has been used to label different communities
#
# Adds Node Attributes: 'community'     - an integer indicating which community a node has been assigned to
#
# Adds Edge Attributes: 'redundancy_2'...  - an integer, counts the number of 2-chains that can be found between the two nodes of an edge
#                       ...'redundancy_k'  - the number of k-chains between the two ends of a given edge
#
#############################################################################################################################################
  
def community(DAG, k = 2, threshold=0):
    
    redundancy(DAG,k) #Assigns a value for the redundancy of each edge, up to order k, as edge attributes
    
    edges = DAG.edges()
    nodes = DAG.nodes()
    
    community_list = range(50) #the numbers in this list will be used as labels for each community
    #Note: Choice of 50 arbitrary...there just needs to be enough items in this list to label each community
    used_communities = [] #Keeps track of which labels in community_list have already been used
    
    #Creates a community attribute for each edge, and initialises it to None
    for node in nodes:
        DAG.node[node]['community'] = None
    
    #Goes through each edge, looks at the total redundancy up to order k
    #If that total is above the threshold value, the two nodes are placed into the same community
    #If it proves interesting, this section could be improved in a number of ways...    
    for edge in edges:
        [node1,node2] = edge
        red = 0 #initialises redundancy total value to 0
        for i in range(k-1):
            red += DAG[node1][node2]['redundancy_%d' %(i+2)] #totals the values of the edge's redundancy attribute, up to the relevant order
        if red > threshold:
            
            #If node1 has already been allocated to a community, assign node2 to this community also (i.e. overwrites node2's current community label - can change this)
            if DAG.node[node1]['community'] != None:
                DAG.node[node2]['community'] = DAG.node[node1]['community']
            
            #If node2 has already been allocated to a community but node1 hasn't, assign node1 to the same community as node2
            elif DAG.node[node2]['community'] != None:
                DAG.node[node1]['community'] = DAG.node[node2]['community']
                    
            #If neither node has been assigned to a community yet, assign them to a new community
            else:
                comm = choice(community_list)
                community_list.remove(comm)
                used_communities.append(comm)
                DAG.node[node1]['community'] = comm
                DAG.node[node2]['community'] = comm
                
    propagate_communities(DAG) #assigns unallocated nodes to a community
    
    return used_communities #return a list of all of the communities that have been used
    

#Place the nodes that remain unallocated into a community 
def propagate_communities(DAG):
    nodes = []
    #If a node has not been allocated to a community, add it to the list 'nodes'
    for node in DAG.nodes():
        if DAG.node[node]['community'] == None:
            nodes.append(node)
    len_nodes = len(nodes) #this keeps count of the number of unassigned nodes
    
    while len(nodes) > 0: #Carry out this loop until the list of unallocated nodes is empty
        for node in nodes:
            
            #Create a list of any communities that the node's predecessors are part of
            preds = DAG.predecessors(node)
            pred_comms = [] #store list of the communities of the node's preds
            for pred in preds:
                comm = DAG.node[pred]['community']
                if comm !=None:
                    pred_comms.append(comm)
            
            #Create a list of any communities that the node's successors are part of
            succs = DAG.predecessors(node)
            succ_comms = []
            for succ in succs:
                if comm !=None:
                    succ_comms.append(comm)
                    
            adjacent_comms = pred_comms + succ_comms #Creates a list of all the communities that are 'adjacent' to the node, forward or backward in time
            
            if len(adjacent_comms) > 0: #i.e. if the node is connected to a node that is part of a community
                modal_values = mode(adjacent_comms) #Find the modal value(s) of the list of adjacent communities
                
                #If there is a single modal value, this is the easiest case: assign the node to be in this community
                if len(modal_values) == 1:
                    DAG.node[node]['community'] = modal_values[0]
                    nodes.remove(node)
                
                #if there is more than one mode in the adjacent communites list... 
                #...we will take the pred list to be dominant, and pick the value which is most common in the pred list
                else:
                    pred_modal_values = mode(pred_comms)
                    #Are any of the modal communities in the adjacent list also the modal communities when just the preds are considered?
                    intersection_1 = list(set(modal_values) & set(pred_modal_values)) #finds the intersection
                    
                    #If only one of the adjacent list modal values is also the modal value when just preds are considered, assign the node to this community
                    if len(intersection_1) == 1:
                        DAG.node[node]['community'] = intersection_1[0]
                        nodes.remove(node)
                    
                    #If more than one of the adjacent list modal values are also the modal values when just preds are considered, assign the node to a random one of these
                    elif len(intersection_1) > 1:
                        DAG.node[node]['community'] = choice(intersection_1)
                        nodes.remove(node)
                    
                    #If there is no intersection between the adjacent list modal communities and the predecessor list modal communites...
                    #...consider how many of the preds are in the adjacent list modal communities, and pick the one they contribute to most
                    else:
                        intersection_2 = list(set(modal_values) & set(preds))
                        if len(intersection_2) > 0:
                            DAG.node[node]['community'] = choice(mode(intersection_2))
                            nodes.remove(node)
                        else:
                            #the preds make no impact on the modal communities - the succs' communities dominate (i.e. the modal values of the adjacent list...
                            #...are the modal values of the successors' community list, so just pick a random one
                            DAG.node[node]['community'] = choice(mode(modal_values))
                            nodes.remove(node) 
                            
        if len(nodes) == len_nodes: #if the size of the list of unallocated nodes has not shrunk after iterating across the unallocated nodes...
        #...the communities have propogated as far as they can, so break the program to stop it looping infinitely
            break
        len_nodes = len(nodes)
    
#Caclculates the value for the redundancy of each edge, and then assigns this as an edge attribute
def redundancy(DAG, k=2):
    edges = DAG.edges()
    for edge in edges:
        [node1, node2]=edge
        nodes = [node1] #initially, just want to consider 2nd order successors
        for j in range(k-1): #carry out the process to kth order
            i = 0 # counts the number of k chains
            new_nodes = []
            for node in nodes:
                for succ in DAG.successors(node):
                    new_nodes.append(succ)                   
                    if node2 in DAG.successors(succ):
                        i+=1
            nodes = new_nodes
            DAG[node1][node2]['redundancy_%d' %(j+2)] = i


#Calculates the modal value(s) of an iterable
def mode(iterable):
    counts = defaultdict(int)
    max_count = 0
    for item in iterable:
        counts[item] += 1
        if counts[item] > max_count:
            max_count = counts[item]
    
    mode = []
    for item in iterable:
        if counts[item] == max_count:
            mode.append(item)
    return list(set(mode))         
            
                