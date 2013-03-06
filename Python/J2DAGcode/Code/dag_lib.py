# dag_lib.py
# this is a library of useful DAG functions

import networkx as nx
import random
import math
import time

# NAMING CONVENTIONS
#
# Every node in a DAG object should have an attribute called 'birthday'. This has to be a float or an int.
# If 'birthday' is big, that means the node is young. If birthday is small, the paper is old.
# The direction of the graph goes from young to old points, so from big number to small numbers.

###########################################################################################
# GENERATORS
###########################################################################################

##################################################################
# box_generator
#
# Arguments: N - Number of point in the box
#            D - Dimension of the box
#
# Returns:   List - 
#            [0] - networkx DAG object (of the box diagram)
#            [1] - [name of oldest node, name of youngest node]
#
# NB Minkowski generator does not really qwork in a sensible way - needs some coordinate transform first
#
###################################################################
def box_generator(N, D, type='box'):
    print 'Making a %s dimensional box with %s points' % (D, N)
    DAG = nx.DiGraph()
    
    # Create points in the box
    box_list = []
    # Create extremal points
    bottom_corner = []
    top_corner = []
       
    for coord in range(D):
        bottom_corner.append(0.0)
        top_corner.append(1.0)
            
    if type=='minkowski':
        # We need the first and last Minkowski point to be extremal in time, but central in space
        for (i, coord) in enumerate(bottom_corner):
            if i==0: pass
            else:
                bottom_corner[i] = 0.5
                
        for (i, coord) in enumerate(top_corner):
            if i==0: pass
            else:
                top_corner[i] = 0.5                
        
    box_list.append(top_corner)
    box_list.append(bottom_corner)    
       
    # Generate list of co-ordinates
    while len(box_list)<N:
        point = []
        for coord in range(D):
            rand = random.random()
            point.append(rand)
        if type=='minkowski':
            if minkowski_check(point, box_list[1], D):      # Check we are in the light cone of the bottom corner
                if minkowski_check(box_list[0], point, D):  # Check we are in the light cone of the top corner
                        box_list.append(point)
        else:
            box_list.append(point)

         
    # Lists not hashable so need graph objects to be tuples
    for i in range(len(box_list)):
        box_list[i] = tuple(box_list[i])   
    
    # Save the names of the corner points and pass them through. extremes[0] is the youngest point (biggest coords) and extremes[1] is the oldest (smallest coords)
    extremes = [box_list[1], box_list[0]]
    
    # Add points to DAG object
    for node in box_list:
        DAG.add_node(node)
        
    # Add ordering attribute    
    for node in DAG.nodes():
        birthday = sum(node)
        DAG.node[node]['birthday'] = birthday  
        
    # Add edges if nodes are causally connected   
    if type == 'box':
        for node1 in box_list:
            for node2 in box_list:
                link = True
                if node1 == node2:
                    link = False
                for d in range(D):
                    if node1[d] < node2[d]:
                        link = False
                if link:
                    DAG.add_edge(node1, node2) 
    
    if type == 'minkowski':
        for node1 in box_list:
            for node2 in box_list:
                if minkowski_check(node1, node2, D):
                    DAG.add_edge(node1, node2)
                    
    return [DAG, extremes]
        
def minkowski_check(node1, node2, D):
    # Check that node1 is in the timelike future of node2
    # Assume that the first coord is time, and that others are space
    dtime = node1[0] - node2[0]
    dtime2 = dtime**2
       
    dspace2 = 0.0
    for d in range(D-1):
        # Find spatial separation
        dspace2 += (node1[d+1] - node2[d+1])**2
        
    if dtime>0:
        if (dtime2 > dspace2):
            return True
        else: return False
    else: return False
    
###########################################################################################
# ALGORITHMS
###########################################################################################

###########################################################################################
# find_all_paths
#
# Arguments - DAG: The networkx DAG object
#           start: The start node for the paths
#             end: The end node for the paths
#            path: Just used for the recusive calling - this forces certain nodes to be used at the start of the path
#
# Returns - paths: A list of paths, which are lists of nodes in that path
############################################################################################

def find_all_paths(DAG, start, end, path=[]):
    path = path + [start]
    if start == end:
        return path
    if DAG.predecessors(start) == []:
        return []
    paths = []
    for node in DAG.predecessors(start):
        if node not in path:
            newpaths = find_all_paths(DAG, node, end, path)
            if newpaths:
                paths.append(newpaths)
                
    return paths
    
###############################################################################################
# find_all_paths_age
#
# Arguments - DAG: The networkx DAG object 
#           start: The start node for the paths
#             end: The end node for the paths
#            path: Just used for the recusive calling - this forces certain nodes to be used at the start of the path
#
# This function checks the birthday of the nodes - it stops looking for a link from node to end
# if node is older than end, since links go from young to old.
################################################################################################

def find_all_paths_age(DAG, start, end, path=[]):
    path = path + [start]
    if start == end:
        return path
    if DAG.predecessors(start) == []:
        return []
    paths = []
    for node in DAG.predecessors(start):
        if age(DAG, node, end): # Edges go from big 'birthday' to small 'birthday'. If our node has a smaller birthday than the end, we've gone too far
            if node not in path:
                newpaths = find_all_paths_age(DAG, node, end, path)
                if newpaths:
                    paths.append(newpaths)
    return paths
 
# Returns True if n1 has a bigger birthday than n2 - that means we COULD have an edge from n1 to n2
def age(DAG, n1, n2):
    age = nx.get_node_attributes(DAG, 'birthday')
    age1 = float(age[n1]) # This is the age of n1
    age2 = float(age[n2]) # This is the age of n2
    if age1 > age2: 
        return True
    if age1 < age2: 
        return False
        
###################################################################
# Long-path Djikstra
#
# lpd
#
# Arguments: DAG - The networkx DAG object
#          start - The name of the starting node
#            end - The name of the final node
#   
#        
# Returns: [DAG, path]
#           DAG - The DAG with the length attributes
#           path - A list of the nodes in the (one of the) longest paths from start to end - or if there is no path, returns None
#####################################################################
length_name = 'lengthy'

def lpd_recursive(DAG, start, end, lengths, depth=0): # Adds an attribute to each node giving the path length from the initial 'start' node
    #print depth   

    for node in DAG.successors(start):
        # Bug where 'length' is a member of this list for some reason
        # Get current lengths from original node   
        length_n = lengths[node]
        length_start = lengths[start]
        if length_n < length_start + 1: 
                lengths[node] = length_start + 1
                DAG = lpd_recursive(DAG, node, end, lengths, depth+1)  # Now consider the nodes attached to node, to see if their path lengths can be increased via start and node   
    return DAG

def lpd_pathfinder(DAG, start, end, lengths, real_path_end, path=[]):
    # We are going backwards from the end node towards the start node when we are finding the path
    # There is a bug where the path from the previous run of this function stays, 
    # and then appears at the start of the path the next time we run this function
    # This bit will remove anything attached to the path which should not be
    try:
        while path[0] != real_path_end:
            path.pop(0)
    except:
        'THIS MESSAGE SHOULD APPEAR EXACTLY ONCE'
    
    path.append(end)
    end_length = lengths[end]
    for node in DAG.predecessors(end):
        node_length = lengths[node]
        if (node == start) and (end_length == 1):
            path.append(node)
            return path
        if node_length == (end_length - 1):
            return lpd_pathfinder(DAG, start, node, lengths, real_path_end, path)
            
             
def lpd(DAG, start, end):
    lengths = {}
    for node in DAG.nodes(): 
        lengths[node] = -1
    lengths[start] = 0
    
    DAG = lpd_recursive(DAG, start, end, lengths)
    if lengths[end] == -1:
        return [DAG, 'No path']
    else:   
        path_length = lengths[end]
        print 'Path length is %s' % str(path_length) 
    longest_path = lpd_pathfinder(DAG, start, end, lengths, end)
    longest_path.reverse() # So it goes from start to end
    return [DAG, longest_path, path_length]
    
        
        
 
def order_successors(successors, birthday_attribute):
    # Orders a node's successors from highest birthday to lowest birthday
    # We would expect that differences between birthdays on the longest path are small, so we want to label nodes where the birthday gap is small first
    # This should reduce the amount of relabeling that occurs
    successor_birthday_tuples = []
    ordered_successor_list = []
    for node in successors:
        birthday_of_node = birthday_attribute[node] #finds 'birthday' attribute of each successor
        successor_birthday_tuples.append((birthday_of_node, node)) #adds a tuple to the 'successor_birthday_tuples' list of the form (birthday, node name)
    sorted_successor_tuples = sorted(successor_birthday_tuples, reverse=True) #sorts the 'successor_birthday_tuples' list from largest to smallest birthday
    for tuple in sorted_successor_tuples:
        ordered_successor_list.append(tuple[1]) #creates a list from the ordered (birthday, node name) list just of the node names
    return ordered_successor_list #return a list of the nodes, reverse ordered by birthday 
 
 

def age_check(DAG, A, B):
    #Tests the ordering of two nodes, based upon their 'birthday' attribute
    #If bday of A is higher than bday of B, returns true
    #If bday of A is lower than bday of B(i.e. A was published to the past of B and there can be no ling), returns false
    A_bday = DAG.node[A]['birthday'] #finds the birthday of the current node
    B_bday = DAG.node[B]['birthday']
    if A_bday > B_bday: return True
    else: return False    
    
