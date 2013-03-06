# trans_red
# performs a transitive reduction on an ordered DAG
# also has methods for transitive completion
# James, current problem with that TC method is short term memory rather than speed...will discuss on Tuesday.
import networkx as nx
import dag_lib as dl
import random
from random import choice
import data_DAG as maker
from operator import itemgetter
import re
import os
import lightcone as lc
import MM_dimension as MM
import midpoint_scaling as mp

###################################################################
# Transitive Reduction
#
# trans_red
#
# Arguments: DAG - The networkx DAG object
#          
# Returns:   DAG - A transitive reduction of the input DAG, as a networkx DAG object
#####################################################################

def trans_red(DAG):
    E = DAG.number_of_edges()
    i = 0.
    print_limit = 0.1
    for edge in DAG.edges(): #iterates over all edges in the original DAG
        [node1, node2] = edge #the edge goes from node1 to node2
        DAG.remove_edge(node1, node2) #remove this edge from the original DAG
        if not nx.has_path(DAG,node1,node2): #if it is now not possible to reach node2 from node1...
            DAG.add_edge(node1,node2) #...the direct edge is neccessary to preserve causal structure
        
        i += 1.
        pc = (i/float(E))*100. #convert to percentage
        if pc > print_limit:
            #print 'Finished %f percent' %pc
            print_limit += 0.1
    return DAG    #the transitively reduced DAG is returned

#Carry out a partial transitive reduction
#When p = 1, a full TR is carried out
#When p = 0, the original DAG is returned 
def part_trans_red(DAG,p):
   
    for edge in DAG.edges(): #iterates over all edges in the original DAG
        [node1, node2] = edge #the edge goes from node1 to node2
        DAG.remove_edge(node1, node2) #remove this edge from the original DAG
        if not nx.has_path(DAG,node1,node2): #if it is now not possible to reach node2 from node1...
            DAG.add_edge(node1,node2) #...the direct edge is neccessary to preserve causal structure
        else: #doesn't delete an edge that would be deleted in a full TR with probability 1-p
            rand=random.random()
            if rand >= p:  # This defaults to carrying out a full TR(prob_of_deleting_edge=1)
                DAG.add_edge(node1,node2)
        
    return DAG    #the transitively reduced DAG is returned
                    
                    
def trans_red_slow(DAG):
    for node1 in DAG.nodes():
        for node2 in DAG.successors(node1):
            for child in DAG.successors(node1):
                if path_check(DAG, child, node2):
                    # There is a longer path from node1 to node2 than the direct link, so cut the direct link
                    # Delete edge from node1 to node2
                    #print 'Delete edge from %s to %s' % (node1, node2)
                    DAG.remove_edge(node1, node2)
                    break
                    
    return DAG

                    
def path_check(DAG, start, end):
    if end in DAG.successors(start):
        return True
    for child in DAG.successors(start):
        if dl.age_check(DAG, child, end):
            # We can possibly go from this child to the end
            if path_check(DAG, child, end):
                return True
            else:
                return False
                
###################################################################
# Transitive Completion
#
# trans_comp
#
# Arguments: DAG - The networkx DAG object
#          
# Returns:   TC - A transitive completion of the DAG, as a networkx DAG object
#####################################################################

#NEED MORE MEMORY FOR BIG DAG (millions of edges...)
def trans_comp(DAG, ordered_nodes):
            
    for node in ordered_nodes:
        if node in DAG:
            succs = DAG.successors(node)
            preds = DAG.predecessors(node)
            for pred in preds:
                for succ in succs:
                    DAG.add_edge(pred,succ)
        #print (float(i)/float(N))*100.
    return DAG
    
def TC_text_files(DAG,ordered_nodes,type):
    i = 0
    N = len(ordered_nodes)
    
    #run with 'initial' to create text files for every node in the original DAG
    if type == 'initial':
        for node in ordered_nodes:
            pred_file = open('./Data/HepTh/%s_p.txt' %str(node),'w')
            succ_file = open('./Data/HepTh/%s_s.txt' %str(node), 'w')
            preds = DAG.predecessors(node)
            succs = DAG.predecessors(node)
            for pred in preds:
                pred_file.write(str(pred) + '\n')
            for succ in succs:
                succ_file.write(str(succ) + '\n')
            print (float(i)/float(N))*100.
            pred_file.close()
            succ_file.close()
            i += 1
    
    if type == 'run':
        for node in ordered_nodes:
            size = os.path.getsize('./Data/HepTh/%s_p.txt' %node)
            if size != 0: #check whether node has any preds
                pred_file = open('./Data/HepTh/%s_p.txt' %str(node),'r')
                succ_file = open('./Data/HepTh/%s_s.txt' %str(node),'r')
                preds = []
                succs = []
                for line in pred_file:
                    preds.append(line.strip())
                for line in succ_file:
                    succs.append(line.strip())
                pred_file.close() #close pred/succ files of 'node'
                succ_file.close()
                
                #add the successors of node to the file containing the 
                for pred in preds:
                    for succ in succs:
                        succs_of_pred = open('./Data/HepTh/%s_s.txt' %str(pred),'r')
                        preds_of_succ = open('./Data/HepTh/%s_p.txt' %str(succ),'r')
                        sop_list = []
                        pos_list = []
                        for line in succs_of_pred:
                            sop_list.append(line.strip())
                        for line in preds_of_succ:
                            pos_list.append(line.strip())
                        succs_of_pred.close()
                        preds_of_succ.close()
                        
                        sop_list.append(succ)
                        pos_list.append(pred)
                        succs_of_pred = open('./Data/HepTh/%s_s.txt' %str(pred),'w')
                        preds_of_succ = open('./Data/HepTh/%s_p.txt' %str(succ),'w')
                        for thing in sop_list:
                            succs_of_pred.write(str(thing) + '\n')
                        for thing in pos_list:
                            preds_of_succ.write(str(thing) + '\n')
                        succs_of_pred.close()
                        preds_of_succ.close()
            i += 1
            print (float(i)/float(N))*100.

#Use when dividing a DAG into ordered chunks for easier digestion by one's RAM
#Might help, might not help...    
def split_data(DAG,node_list,size,name):
    
    ordered_nodes = []
    for thingo in node_list:
        ordered_nodes.append(thingo[0])
    
    lists = []
    list = []
    
    for node in ordered_nodes:
        if len(list) < size:
            list.append(node)
        else:
            lists.append(list)
            list = []
    lists.append(list)
    i = 1
    for listito in lists:
        out = open('./Data/Cit-%s_ordered_%d.txt' %(name,i),'w') 
        for node in listito:
            out.write(str(node) + '\n')
        i += 1

#return the nodes of a boxDAG in a list ordered by birthday        
def box_ordered_list(DAG):
    birthday_tuples = []
    for node in DAG.nodes():
        birthday = DAG.node[node]['birthday']
        birthday_tuples.append((node,birthday)) 

    birthday_tuples = sorted(birthday_tuples, key=itemgetter(1)) #sorts all of the nodes based upon their birthday, so that the rank time can be found
    return birthday_tuples       

def trans_comp_old(DAG):
    N = DAG.number_of_nodes() #Finds the number of nodes in the input DAG
    if N < 700: #Testing has found that at around 700 nodes, the matrices method becomes faster than the networkx-based method
        TC = trans_comp_nx(DAG)
    else:
        TC = trans_comp_matrices(DAG)
    return TC
    
    

#Naive method that uses the 'has_path' networkx function to test whether pairs of nodes in the DAG should be connected by an edge            
def trans_comp_nx(DAG):
    N = DAG.number_of_nodes()
    i = 0
    print_limit = 0.01
    
    for node1 in DAG.nodes():
        for node2 in DAG.nodes(): #iterates over all pairs of edges
            if dl.age_check(DAG, node1, node2): #ensures that the time ordering of the pair is such that an edge could exist from node1 to node2
                if not node1 == node2: #prevents loops
                    if nx.has_path(DAG, node1, node2):
                        DAG.add_edge(node1, node2) #if there is a path from node1 to node2, add an edge from node1 to node2
        i += 1.
        pc = (i/float(N))*100. #convert to percentage
        if pc > print_limit:
            #print 'Finished %f percent' %pc
            print_limit += 0.01
    return DAG

#Implimentation of Warshall's algorithm for transitive completion
#Step 1: Copy the adjacency matrix of the graph into a new matrix
#Step 2: For every node in the graph, consider the in and out edges
#Step 3: Connect all of the node's predecessors to each of its successors
#See http://datastructures.itgo.com/graphs/transclosure.htm  for more information
def trans_comp_matrices(DAG):
    adj_matrix = nx.to_numpy_matrix(DAG) #Finds adjacency matrix of the DAG (i.e. if u->v then the element {u,v}=1, else = 0)
    TC_matrix = adj_matrix #Creates a copy of the adjacency matrix
    N = DAG.number_of_nodes()
    E = DAG.edges()
    node_list = DAG.nodes() #Creates list of node names - ith row/collumn will correspond to the ith node in this list
    for i in range(0,N): #For every node i...
        for j in range(0, N): 
            if TC_matrix[i,j] == 1.: #If there is an edge from the ith to the jth element...(i.e. j is a successor of i)
                for k in range(0, N): 
                    if TC_matrix[k,i] == 1.: #Consider all predecessors k of the ith node...
                        TC_matrix[k, j] = 1. #...and add an edge from each of the predecessors k to the successors j of i
                        if not DAG.has_edge(node_list[k], node_list[j]): #If the node doesn't already exist in the DAG...
                            DAG.add_edges(node_list[k], node_list[j]) #...add a new edge to the DAG from the kth to jth node   
    return DAG
    

        

            
if __name__ == '__main__': 
    
    data = open('./Data/Cit-HepPh.DAT', 'r')
    #out = open('./Data/Cit-HepTh_TC_1.txt','w')

    #print DAG.number_of_edges()
    
    
    
    [DAG,node_tuples] = maker.birthdayDAG(data,True,True)
    node_list = []
    for thing in node_tuples:
        node_list.append(thing[0])
        
    #trans_comp(DAG,node_list)
        
    sample = 50
    out = open('./Cit-HepPh_dimensiontest.txt','w')
    while sample > 0:
        node1 = choice(node_list)
        node2 = choice(node_list)
        bday1 = DAG.node[node1]['birthday']
        bday2 = DAG.node[node2]['birthday']
        if bday1 > bday2:
            start = node1
            end = node2
        else:
            start = node2
            end = node1
        if nx.has_path(DAG,start,end):
            if (DAG.node[start]['rank']-DAG.node[end]['rank']) < 3000:
                try:
                    intvl = lc.interval(DAG,start,end)
                    sample -= 1 #reduce sample number by 1 as we have found an eligable candidate pair
                    
                    N = len(intvl)
                    E = intvl.number_of_edges()
                    dbday = abs(bday1 - bday2)
                    rankS = DAG.node[start]['rank']
                    rankE = DAG.node[end]['rank']
                    lp_result = dl.lpd(DAG,start,end)
                    lp = lp_result[2]
                    path = lp_result[1]
                    print 'Finding MM'
                    MM_result = MM.MM_dimension(intvl,node_list)
                    intvl.clear() #for memory purposes
                    MMd = MM_result[0]
                    E_TC = MM_result[1]
                    print 'Find MPSD'
                    #MPS_result = mp.mpsd(DAG,path)
                    #MPSd = MPS_result[0]
                    #out.write(str(start) + '\t' + str(end) + '\t' + str(N) + '\t' + str(E) + '\t' + str(lp) + '\t' + str(E_TC) + '\t' + str(MMd) + '\t' + str(MPSd) + '\n')
                    out.write(str(start) + '\t' + str(end) + '\t' + str(dbday) + '\t' + str(rankS) + '\t' + str(rankE) + '\t' + str(N) + '\t' + str(E) + '\t' + str(lp) + '\t' + str(E_TC) + '\t' + str(MMd) + '\n')
                    print sample
                except:
                    print 'Memory error with %s to %s' %(start,end)

    #TC_text_files(DAG,node_list,'run') #Method that keeps the DAG in text files rather than in a digraph object...less RAM needed but SLOWWWW
    #split_data(DAG,node_list,500,'HepTh') #Method to cut up the data so that the ST memory didn't need to kow the whole list at once, and so we could monitor progress
    
    '''lists = range(55) #I split the data into 14 chunks to see whether that would make it easier at all to handle...it didnt...
    
    for j in lists:
        chunk_file = open('./Data/HepTh_ordered_%d.txt' %(j+1),'r') #file containing a list of nodes in one 'chunk' of the data
        chunk = []
        for line in chunk_file:
            node = re.findall(r'\w+', line)
            chunk.append(node[0])
        chunk_file.close()
        TC = trans_comp(DAG,chunk)
        print j
        print TC.number_of_edges()
    
    
    
    for edge in TC.edges():
        out.write(str(edge[0]) + '\t' + str(edge[1]) + '\n')'''
        
    
    #EVIDENCE THAT THE NEW METHOD WORKS (nb uses old function names)
    '''box = dl.box_generator(50,2)[0]
    DAG = trans_red(box)
    print 'There are %d nodes and %d edges in the TR box' %(len(DAG),DAG.number_of_edges())
    DAG2 = DAG.copy()
    
    node_list = box_ordered_list(DAG)
    
    TC = tc_leapfrog(DAG,node_list)
    
    print 'There are %d nodes and %d edges in the TC DAG new meth' %(len(TC),TC.number_of_edges())
    
    i = 0
    for node1 in DAG.nodes():
        for node2 in DAG.nodes():
            if node1 != node2:
                if nx.has_path(DAG,node1,node2):
                    i += 1
                    if not DAG.has_edge(node1,node2):
                        print 'Oh shit!'  
    
    print i   ''' 
    
    
    


    
                        
        
    