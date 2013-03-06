#community_test.py
#used to test the community detection algorithm
#contains a model DAG and a function that creates a measure of the success of the community detection

import clustering as clus
import networkx as nx
import community as comm
import re
import dag_lib as dl
from random import choice
import time
import math

out = open('./clustering.txt', 'w')

# DEFINITIONS
# Intrabox edge - edge between nodes in a single box
# Interbox edge - edge between nodes in a pair of boxes
##############################################################################################################################
# box_mixer
#
# Arguments: N - The number of nodes in each box
#            D - The dimension of each box
#            b - The number of boxes to be mixed
#            p - (Total #intrabox edges)/(#interbox edges to be added)
#
# Returns: [mixed_box,box_list]
#           mixed_box - a single networkx DiGraph object consisting of b boxes with a number of edges connecting them
#           box_list  - a list of networkx DiGraph objects where each item is one of the b boxes, before the mixing process (i.e. no interbox edges)
#
# This function acts a model DAG to test the community detection algorithm
# Each box is like a single community, with lots of intrabox edges, whilst the interbox edges are the links between communities
# The higher p, the less clear the boundaries between different boxes(the 'communities')
#
# NOTE: box_mixer does not work when points are added at (0,0,0...) and (1,1,1...) to each box - need unique names for each node
############################################################################################################################## 
def box_mixer(N, D, b, p):
    box_list = []
    edges_list = []
    for i in range(b):
        box = dl.box_generator(N, D)[0]
        box_list.append(box)
        edges_list.append(len(box.edges()))
    mixing_edges = int(p*sum(edges_list)) # approx number of edges between the boxes, if added fraction p of the total edges in all of the boxes
    print 'Adding %d edges' %mixing_edges
    mixed_box = nx.union_all(box_list)
    
    #assigns birthdays to the mixed_box DAG
    for node in mixed_box.nodes():
        birthday = sum(node)
        mixed_box.node[node]['birthday'] = birthday 
    birthday = nx.get_node_attributes(mixed_box, 'birthday')   
        
    while mixing_edges > 0:
        box_indices = range(b)
        rand_index_1 = choice(box_indices)
        box_indices.remove(rand_index_1) #prevents extra edge being created within a single box
        rand_index_2 = choice(box_indices)
        nodes_1 = box_list[rand_index_1].nodes()
        nodes_2 = box_list[rand_index_2].nodes()
        node_1 = choice(nodes_1)
        node_2 = choice(nodes_2)
        if birthday[node_1] > birthday[node_2]:
            mixed_box.add_edge(node_1,node_2)
            mixing_edges -= 1
    return [mixed_box,box_list]
    
##############################################################################################################################
# community_test
#
# Arguments: N          - DAG, a networkx DiGraph object which has been generated using 'box_mixer'
#            k          - an integer, indicates the order of redundancy to be considered (i.e. count from 2-chain up to k-chain alternative paths)
#            threshold  - an integer, if the 'redundancy' is above this threshold, the nodes in the edge will be placed in the same community
#
# Returns:  weighted_score - a float, measures the success of the community detection procedure in identifying seperate boxes (1 is max success)
#
# The community detection procedure on the box_mixer DAGs is considered successful if communities do not spill over from one box to another
# (although there may be multiple communities within a single box) 
##############################################################################################################################     
def community_test(DAG,k,threshold):

    communities = comm.community(DAG, k, threshold)
            
    census = []

    #creates a list, 'census', where each item is a list of the nodes in a certain community       
    for community in communities:
        comm_members = []
        for box in box_list:
            for node in box.nodes():
                if box.node[node]['community'] == community:
                    comm_members.append(node)
        census.append(comm_members)

    success = 0. #initialise the 'success' variable
    
    #iterate across each list of community members (the items in 'census')    
    for comm_members in census:
        for box in box_list:
            nodes = box.nodes()
            
            #for a given community, count how many of the nodes come from each box...
            counter = 0.
            for node in nodes:
                if node in comm_members:
                    counter += 1.
                    
            #If over half of the nodes in a given community come from the same box, these nodes are considered to have been successfully placed together...
            #...whilst the rest are unsuccessfully placed in a community
            if len(comm_members) > 0:
                fraction = counter/float(len(comm_members)) #calculates the fraction of a community that come from a single box
                if fraction > 0.5:
                    success += counter/(len(box_list)*len(nodes)) #adds to the success score the RHS, which is the fraction of nodes deemed successful from this community
    return success
    
#calculates the variance of a list
def var(list):
    average = sum(list)//(len(list))
    sq_diff = 0.0
    for thing in list:
        diff = thing - average
        diff2 = diff*diff
        sq_diff += diff2
    sq_diff = sq_diff/(len(list))
    diff = math.sqrt(sq_diff)    
    return diff   


#out.write('\n' + 'th' + '\t' + 'D' + '\t' + 'b' + '\t' + 'k' + '\t' + 'p' + '\t' + 'N' + '\t' + 'av_pc' + '\t' + 'var_pc' + '\t' + 'pc_time')
   
list_n = [100]
list_D = [2]
list_b = [2]
#list_p = [0.001,0.005,0.01,0.02,0.04]
list_p = [0.06,0.08,0.1]
list_th = [6]
list_k = [2]
numrun = 10.
runs = range(int(numrun))
for th in list_th:
    for D in list_D:    
        for b in list_b:    
            for k in list_k:    
                for p in list_p:
                    for N in list_n:
                        start_time = time.clock()
                        pc_list = []
                        for thing in runs:
                            full_box = box_mixer(N,D,b,p)
                            DAG = full_box[0]
                            box_list = full_box[1]                        
                            result = community_test(DAG,k,th)
                            pc_list.append(result)
                        
                        av_pc = sum(pc_list)/numrun
                        var_pc = var(pc_list)
                        pc_time = time.clock() - start_time
                        print 'Time for %s size box of %s dimensions was %s' % (N, D, pc_time)  
                        out.write('\n' + str(th) + '\t' + str(D) + '\t' + str(b) + '\t' + str(k) + '\t' + str(p) + '\t' + str(N) + '\t' + str(av_pc) + '\t' + str(var_pc) + '\t' + str(pc_time))
                        print '\n \n \n'
                        
'''for run in runs:
    for p in list_p:
        full_box = box_mixer(100,2,2,p)
        DAG = full_box[0]
        box_list = full_box[1]
        for th in list_th:
                result = community_test(DAG,2,th)
                out.write('\n' + str(p) + '\t' + str(th) + '\t' + str(result))'''