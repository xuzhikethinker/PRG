#Function to transitively reduce a DAG  

# Naming convention - If node1 is a 'direct successor' of node2, in the original DAG, there is an edge from node2 to node1
#                     If node1 is an 'indirect successor' of node 2, there is no edge from node2 to node1 in the original DAG,
#                        but a path can be found from node2 to node1 via other edges - in a TC DAG, there will be a node from node2 to node1
#                     The set of both direct and indirect successors for a given node will be termed 'decendants'
#

#work in progress

import networkx as nx
import longest_path as lp
import random
import math
import time
import operator
import DAG_from_arxiv as arXiv
import DAG_from_box as boxmaker
import matplotlib.pyplot as plt
import re

#def TR_basic(DAG):
#    for edge in DAG.edges():

outfile = 'C:\Users\Jamie\Project\outTR.txt'
#outfile = './out2.txt'
countfile = './count.txt'
analysisfile = './analysis.txt'
    
def make_DAG(data):
    DAG = nx.DiGraph()
    for line in data:
        nodes = re.findall(r'\w+', line)
        DAG.add_edge(nodes[0],nodes[1])
    return DAG
    
def create_successor_lists(DAG, successor_attribute, no_successors=[]):
    for node in DAG.nodes():
        successor_list = DAG.successors(node)
        successor_attribute[node] = successor_list
        if len(successor_list) == 0:
            no_successors.append(node)
    return no_successors        
    

###############################################################################################
# label_with_decendants
#
# Arguments -                 DAG: The networkx DAG object 
#                            node: One of the nodes in the DAG that has no decendants
#             successor_attribute: the dictionary containing the decendants for each node
#
#Updates the attribute 'successor' to be a list of all successor nodes, both direct (the is an edge to this node) and indirect (a path can be found)
#Prior to applying this function, the attribute 'successor' is just a list of direct successors
#There will be a sub set of nodes that have no successors (i.e. they have the lowest birthdays in each line of successors)
#To relabel every node, apply the label_with_decendents function to each of this successorless nodes
#
###############################################################################################
def label_with_decendants(DAG, node, successor_attribute):
    node_successors = successor_attribute[node] #acquires the successor list for 'node'
    for pred in DAG.predecessors(node): #loops over each of node's predecessors
        direct_successors = successor_attribute[pred] #looks at the current 'successor' list of the predecessors, which will just be direct ones
        new_direct_successors = direct_successors #creates a list that will contain both direct and indirect successors
        for indirect_successor in node_successors: #each of 'node's successors that isn't already a successor of 'pred' is an indirect successor of 'pred'
            if indirect_successor not in direct_successors:
                new_direct_successors.append(indirect_successor) #In a TC'ed DAG, edges will be created to all successors, both direct and indirect
        successor_attribute[pred] = new_direct_successors #updates the 'successor' list of pred to include both direct and indirect successors
        label_with_decendants(DAG, pred, successor_attribute) #now consider all of the predecessors of pred...

###############################################################################################
# TC_from_decendants
#
# Arguments -                 DAG: The networkx DAG object 
#                       decendant: the dictionary containing the decendants for each node, using the 'successor' attribute
#
# Returns -                TC_DAG: A transitive completion of DAG
#
# Creates a DAG where there is an edge between each node and all of the nodes contained in it's 'successor' attribute
# Each node's 'successor' attribute is a list of each of that node's decendants
###############################################################################################        
def TC_from_decendants(DAG, decendants):
    TC_DAG = nx.DiGraph()
    for node in DAG.nodes():
        for decendant in decendants[node]:
            TC_DAG.add_edge(node, decendant)
    return TC_DAG
    
###############################################################################################
# TR_from_decendants
#
# Arguments -                 DAG: The networkx DAG object 
#                          TR_DAG: This is the transitively reduced version of DAG which is updated at each recursion
#                            node: One of the nodes in the DAG that has no decendants
#                       decendant: the dictionary containing the decendants for each node
#
# Returns -                TR_DAG: A transitive reduction of DAG
#
# Creates the minimal DAG that preserves the decendants of each node (i.e. removes direct edges when there is an indirect path)
###############################################################################################   

def TR_from_decendants(DAG, TR_DAG, node, decendant):
    for pred in DAG.predecessors(node):
        for successor_of_pred in DAG.successors(pred):
            if node in decendant[successor_of_pred]:
                TR_DAG.remove_edge(pred, node)
                #print 'deleted ' + str(pred) + ' to ' + str(node)
                break
            TR_DAG = TR_from_decendants(DAG, TR_DAG, pred, decendant)
    return TR_DAG

def TR_from_examining_edges(DAG):
    for edge in DAG.edges():
        [source, target] = edge
        DAG.remove_edge(source, target)
        if not nx.has_path(DAG,source,target):
            DAG.add_edge(source,target)
    return DAG
    
        
    
def is_path(DAG, out): #tests whether a path exists between node1 and node 2, where the birthday of node1 is larger than the birthday of node2
    TC_DAG = nx.DiGraph()
    
    for node in DAG.nodes():
        for point in DAG.nodes():
            if DAG.has_edge(node, point):
                TC_DAG.add_edge(node, point)
            if search_for_path(DAG, node, point):
                out.write('\n' + 'The node %s is causally related to the node %s' %(node, point))
                if not node == point:
                    TC_DAG.add_edge(node, point)
            else:
                out.write('\n' + 'The node %s is not causally related to the node %s' %(node, point))
    return TC_DAG
    
def is_path2(DAG, out): #tests whether a path exists between node1 and node 2, where the birthday of node1 is larger than the birthday of node2
    TC_DAG = nx.DiGraph()
    
    for node in DAG.nodes():
        for point in DAG.nodes():
            if DAG.has_edge(node, point):
                TC_DAG.add_edge(node, point)
            paths = find_all_paths(DAG, node, point)
            pathlist = []
            newpathlist = make_list_of_paths(paths, pathlist)
            if not len(newpathlist) == 0:
                out.write('\n' + 'The node %s is causally related to the node %s' %(node, point))
                if not node == point:
                    TC_DAG.add_edge(node, point)
            else:
                out.write('\n' + 'The node %s is not causally related to the node %s' %(node, point))
    return TC_DAG
        
def search_for_path(DAG, node1, node2):
    #test = False
    if node1 == node2:
        return True
    elif DAG.successors(node1) == []:
        return False
    else:
        for node in DAG.successors(node1):
            return search_for_path(DAG, node, node2)
            
def find_all_paths(DAG, node1, node2, path=[]):
    path = path + [node1]
    if node1 == node2:
        return path
    if DAG.successors(node1) == []:
        return []
    paths = []
    for node in DAG.successors(node1):
        if node not in path:
            newpaths = find_all_paths(DAG, node1, node2, path)
            paths.append(newpaths)
    return paths
    
def make_list_of_paths(paths, pathlist):
    for path in paths:
        if isinstance(path, tuple) or isinstance(path, str): #if 'path' is a list of tuples (the coordinates) or strings (arXiv names) add the list to pathlist
            pathlist.append(paths)
        if isinstance(path, list): #if 'path' is a list of lists, carry out the process again to check the composition of THOSE lists
            make_list_of_paths(path, pathlist)
    return pathlist #Return a list that just contains paths (in list form) of nodes (no lists of lists of lists of...)
    
if __name__ == "__main__": 
    out = open(outfile,'w') #output file  
    count = open(countfile,'a+') #output file
    data = open('C:\Users\Jamie\Project\CitNet.DAT', 'r') #input data
    analysis = open(analysisfile, 'w') #analysis file
    
    #n_list = [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100]
    #n_list = [50, 75, 100, 125, 150, 175, 200, 225, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000, 1100, 1200, 1300, 1400, 1500]
    #n_list = [5, 10, 15, 20, 25, 30, 35, 40, 45, 50]
    #n_list = [10, 10, 10, 20, 30, 30]
    
    DAG = arXiv.arXivDAG(data)
    print 'DAG construction complete'
    start_time = time.clock()
    TR_DAG = TR_from_examining_edges(DAG)
    print 'There are %d edges in the original DAG' %DAG.number_of_edges()
    print 'There are %d edges in the TR DAG' %TR_DAG.number_of_edges()
    total_time = time.clock() - start_time  
    print 'TR complete in %f' %total_time    
    
    '''for N in n_list:
        start_time = time.clock()
        DAG_and_corners = boxmaker.box_generator_corners(N, 2)
        DAG = DAG_and_corners[0]

        #out.write('\n' + 'The edges in the original DAG:')
        #for edge in DAG.edges():
        #    out.write('\n' + str(edge))

        #successor_attribute = nx.get_node_attributes(DAG, 'successors') #summons the successor dict
        #no_successors = create_successor_lists(DAG, successor_attribute)

        #for node in no_successors:
        #    label_with_decendants(DAG, node, successor_attribute)

        #TC_DAG = TC_from_decendants(DAG, successor_attribute)

        TR_DAG_1 = TR_from_examining_edges(DAG)
        
        #TR_DAG_2 = DAG

        #for node in no_successors:
        #    TR_DAG_2 = TR_from_decendants(DAG, TR_DAG_2, node, successor_attribute)
            

        
         
        #out.write('\n' + 'The edges in the TRed DAG:')    
        #for edge in TR_DAG_1.edges():
        #    out.write('\n' + str(edge))
            
        total_time = time.clock() - start_time
        print '%d nodes is complete' %N
        out.write('\n' + str(N) + '\t' + str(total_time))
    
    #for node in DAG.nodes():
    #    print node + ': ' + str(successor_attribute[node])
    
    #TC_DAG = is_path2(DAG, out)
    #Failure1 = nx.difference(TR_DAG_1, TR_DAG_2)
    #Failure2 = nx.difference(TR_DAG_2, TR_DAG_1)
    
    #for edge in Failure1.edges():
    #    out.write('\n' + '%s is in TR_DAG_1 but not in TR_DAG_2' %str(edge))
    #for edge in Failure2.edges():
    #    out.write('\n' + '%s is in TR_DAG_2 but not in TR_DAG_1' %str(edge))

    #nx.draw(DAG) #draw the DAG
    #plt.savefig("C:\Users\Jamie\Project\original.png") #create the image file
    
    #nx.draw(TC_DAG) #draw the DAG
    #plt.savefig("C:\Users\Jamie\Project\TCed.png") #create the image file
    
    
    
    #DAG_and_corners = boxmaker.box_generator_corners(10, 2)
    #is_path(DAG_and_corners[0], out)'''