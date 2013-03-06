#indecisive_path.py
#Finds a path through the DAG by moving connected nodes of the highest degree
#Method can be used from either end of the path

#Needs tidying up

from random import choice
import networkx as nx
import data_DAG as maker
import dag_lib as dl
import re
from lightcone import interval

####################################################################################################################################################
# Greedy Indecisive Path
# 
# gip
####################################################################################################################################################
# An algorithm that estimates the longest path from a start node
#
# Greedy: The greedy path traverses nodes that have the smallest difference in 'birthday' (ie they have the smallest step in the time dimension)
# Indecisive: The indecisive path travels through nodes that have the highest degree (in the path direction, ie in or out)
# gip: Combines both methods. If the difference between the node with the smallest time step and any other potential nodes is under the 'threshold',
#      the node with the highest degree in the path direction will be taken as the next step in the path
####################################################################################################################################################
# Arguments: DAG - A networkx DiGraph object
#            start - The node from which the path will start
#            threshold - Sets the balance between greed and indecisiveness (see NB)
#            direction - Choose the direction (in time) the path will head in. 'forward' for forward in time, or 'backward'
#
# Returns:   lp - A list, where each item is a node along the gip
#
# NB:        The units of threshold are the same as 'birthday'. For the arXiv, 1000 = 1 month, 12000 = 1 year
#            To recover the greedy path, set threshold to 0
#            To recover the indecisive path, set the threshold to be high (eg 300,000)
####################################################################################################################################################
def gip(DAG,start,threshold,direction,axis=False):
    apex = start
     
    path = gip_recursive(DAG,start,threshold,direction,axis,apex,'clean') #give empty path to gip function to prevent hangover path from the last run
    lp = gip_recursive(DAG,start,threshold,direction,axis,apex,'run') #gip_recursive can have the old path at the start for some reason and therefore needs to be cleaned...
    
    return lp

#Threshold is given in the units of 'birthday'
#When stepping along a greedy path, the node with the very smallest step in birthday becomes the next step
#Here, if other potential nodes are within the 'threshold' of the would-be greedy path next step, these also become candidates to be the next step
#Out of this collection of nodes that represent 'small' steps in terms of birthday, the one with the highest degree becomes the next step
def gip_recursive(DAG, start, threshold,direction,axis,apex,type = 'run',path=[] ,max_depth=-1, end_node='' ):
    
    if type == 'clean': #prevent path rom previous run being put at the start of the new list
        del path[:]
        return path
    
    if type == 'run':
        path.append(start)
        
        if axis:
            if not apex:
                apex = start #on the first run through, the apex will be set as the initial start and then passed to the recursions

            
        if len(path) == max_depth:
            print 'We have reached the max depth'
            print 'Max Depth = %s' % max_depth
            print 'Path length = %s' % str(len(path))
            return path

        if start == end_node:
            return path
        if direction == 'backward':
            things = DAG.successors(start)
        if direction == 'forward':
            things = DAG.predecessors(start)
        if len(things) == 0:

            return path
        ordered_things = order_things(DAG, things,direction)
        if len(ordered_things) == 0:
            next_node = None

        
        i = 0
        next_nodes = [] #list of potential next nodes
        for node in ordered_things: 

            if i ==0:    
                greedy_node = node
                greedy_node_birthday = DAG.node[greedy_node]['birthday']
                if direction == 'backward':
                    threshold_birthday = float(greedy_node_birthday) - float(threshold) #this sets the level of birthday above which other successors will be considered
                if direction == 'forward':
                    threshold_birthday = float(greedy_node_birthday) + float(threshold)
                if threshold_birthday < 0: #stop negative birthday thresholds...if it's equal or less than zero, we are just finding the indecisive path
                    threshold_birthday = 0
                next_nodes.append(greedy_node) #add the greedy node to the list of potential next nodes

                i += 1
            else:
                node_birthday = DAG.node[node]['birthday']
                if direction == 'backward':
                    if node_birthday >= threshold_birthday:
                        next_nodes.append(node) #add to the list of potential next nodes if the birthday is above the threshold level
                if direction == 'forward':
                    if node_birthday <= threshold_birthday:
                        next_nodes.append(node) #add to the list of potential next nodes if the birthday is above the threshold level    
                else:

                    break #as the list is ordered, the remaining nodes will also not be above the threshold
        
        #Choose one of the nodes from next_nodes based upon the degree
        if len(next_nodes) == 1: #only one node is above the threshold, the greedy node (so follow the greedy path at this point)

            path = gip_recursive(DAG, greedy_node,threshold,direction,axis,apex,'run',path, max_depth, end_node)
            return path
        
        elif len(next_nodes) > 1: #now indecisive considerations can come into it...

            k_max = 0 #Maximum degree (in for 'end' or out for 'start') found
            i_max = [] #index in list of node with maximum degree
            i = 0 #counter for the current index
            
            #Look through each of the preds/succs and find which of them has the highest degree (in or out)
            for candidate in next_nodes: #each of these candidates is a small step in birthday...now we will look at their degree
                
                if axis: #consider the size of the interval between the 
                
                    intvl = nx.DiGraph() #to avoid hangovers
                    DAG2 = DAG.copy() #to avoid hangovers
                    if direction == 'backward':
                        intvl = interval(DAG2,apex,candidate) #find interval between potential next step and the apex
                    
                    elif direction == 'forward':
                        intvl = interval(DAG2,candidate,apex)

                    k = intvl.number_of_nodes() #r is the number of nodes in the interval between the apex and potential next steps
                
                else: #ie if axis = False, the local degree will be considered
                    if direction == 'backward':
                        k = DAG.out_degree(candidate)
                    if direction == 'forward':
                        k = DAG.in_degree(candidate)

                
                if k > k_max:
                    k_max = k
                    i_max = [i]
                elif k == k_max:
                    if k_max > 0:
                        i_max.append(i)
                i += 1
                        
            if len(i_max) == 1: #there is a single node that has the maximum degree value
                i_path = i_max[0]
            elif len(i_max) > 1: #there is more than one node with the same (non-zero) degree value
                i_path = choice(i_max)
            else: #i.e. i_max is an empty list...all node have k=0 i.e. the end of the path will be reached on the next step to a random one of 'things'
                i_path = choice(range(len(next_nodes)))

            next_node = next_nodes[i_path]    #This is the next step that the path with take

            
        if next_node:   
            path = gip_recursive(DAG, next_node,threshold,direction,axis,apex,'run',path, max_depth, end_node)
        else:
            #path.append(ordered_successors[0])
            return path
        return path
    
def order_things(DAG, successors,type):
    # Orders a node's successors from highest birthday to lowest birthday
    # We would expect that differences between birthdays on the longest path are small, so we want to label nodes where the birthday gap is small first
    # This should reduce the amount of relabeling that occurs
    successor_birthday_tuples = []
    ordered_successor_list = []
    for node in successors:
        birthday_of_node = DAG.node[node]['birthday'] #finds 'birthday' attribute of each successor
        successor_birthday_tuples.append((birthday_of_node, node)) #adds a tuple to the 'successor_birthday_tuples' list of the form (birthday, node name)
    sorted_successor_tuples = sorted(successor_birthday_tuples, reverse=True) #sorts the 'successor_birthday_tuples' list from largest to smallest birthday
    for tuple in sorted_successor_tuples:
        ordered_successor_list.append(tuple[1]) #creates a list from the ordered (birthday, node name) list just of the node names
    if type == 'backward':
        print type
        return ordered_successor_list #return a list of the nodes, reverse ordered by birthday 
    elif type == 'forward':
        flip_list = list(reversed(ordered_successor_list))
        return flip_list #want it in reverse order when dealing with preds
    else:
        print 'Unrecognised type used in order_successors - use either s or p in quotes'    
#################################################################################################################################################
#I don't think this works perfectly...sometimes is doesn't go for the greediest node...
def greedy_path(DAG, start, max_depth=-1, end_node='', path=[]):
    path.append(start)
    if len(path) == max_depth:
        print 'We have reached the max depth'
        print 'Max Depth = %s' % max_depth
        print 'Path length = %s' % str(len(path))
        return path
    if start == end_node:
        return path
    successors = DAG.successors(start)
    ordered_successors = order_successors(DAG, successors)
    next_node = None
    for node in ordered_successors:   
        if len(DAG.successors(node)) > 0:
            next_node = node
            break
    if next_node:   
        path = greedy_path(DAG, next_node, max_depth, end_node, path)
    else:
        path.append(ordered_successors[0])
        return path
    return path

def indecisive_path(DAG,node,type):
    
    path = indec_recursive(DAG,node,type,'clean') #give empty path to indic function to prevent hangover path from the last run
    indec = indec_recursive(DAG,node,type,'run') #indic_recursive can have the old path at the start for some reason and therefore needs to be cleaned...
    
    return indec
    
def indec_recursive(DAG,node,type,aim = 'run',path=[]):
    if aim == 'clean': #prevent path rom previous run being put at the start of the new list
        del path[:]
        return path
    
    if aim == 'run':
        path.append(node)
        #Change type to change whether relations are found from bottom (of DAG) up or from the top down
        if type == 'start':
            things = DAG.successors(node)
        elif type == 'end':
            things = DAG.predecessors(node)
        else:
            print 'Error: type given in indecisive_path not recognised'
            
        
        if len(things) == 0: #there are no preds/succs to consider so the end of the path has been reached
            return path
        
        else: #i.e. the end of the path has not been reached...there are still succs/preds to choose from

            k_max = 0 #Maximum degree (in for 'end' or out for 'start') found
            i_max = [] #index in list of node with maximum degree
            i = 0 #counter for the current index
            
            #Look through each of the preds/succs and find which of them has the highest degree (in or out)
            for thing in things:
                
                if type == 'start':
                    k = DAG.out_degree(thing)
                elif type == 'end':
                    k = DAG.in_degree(thing)
                
                if k > k_max:
                    k_max = k
                    i_max = [i]

                elif k == k_max:
                    if k_max > 0:
                        i_max.append(i)

                i += 1
                        
            if len(i_max) == 1: #there is a single node that has the maximum degree value
                i_path = i_max[0]
            elif len(i_max) > 1: #there is more than one node with the same (non-zero) degree value
                i_path = choice(i_max)

            else: #i.e. i_max is an empty list...all node have k=0 i.e. the end of the path will be reached on the next step to a random one of 'things'
                i_path = choice(range(len(things)))

            next_step = things[i_path]    #This is the next step that the path with take        
                    
            path = indec_recursive(DAG,next_step,type,aim,path)
            
            return path


if __name__ == '__main__':    
    data = open('./Data/Cit-HepPh.DAT', 'r')
    DAG = maker.birthdayDAG(data)

    '''full_box = dl.box_generator(100, 2)
    ext = full_box[1]
    path = indecisive_path(full_box[0],ext[1],'start')
    print len(path)
    print path'''

    '''test_file = open('./Data/TestNet.DAT', 'r')
    DAG = nx.DiGraph()
    for line in test_file:
        nodes = re.findall(r'\w+', line)
        DAG.add_edge(nodes[0],nodes[1])
     
    for node in DAG.nodes():
        birthday = 20 - int(node)
        DAG.node[node]['birthday'] = birthday'''
    #DAG = maker.basicDAG(test_file)
    
    print len(DAG)
    print 'DAG made'

    path = greedy_path(DAG,'0105341')
    print 'The length of the greedy path is %d' %len(path)
    print 'The path is:'
    print path

    path = gip(DAG,'0105341',0)
    print 'The length of the greedy gip path is %d' %len(path)
    print 'The path is:'
    print path

    path = indecisive_path(DAG,'0105341','start')
    print 'The length of the indi path is %d' %len(path)
    print 'The path is:'
    print path

    path = gip(DAG,'0105341',5000)
    print 'The length of the gip path is %d' %len(path)
    print 'The path is:'
    print path
    
    path = dl.lpd(DAG,'0105341','9206241')
    print 'The length of the lpd path is %d' %len(path)
    print 'The path is:'
    print path        

