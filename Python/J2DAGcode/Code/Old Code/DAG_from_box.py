import networkx as nx
import random
import math
import time
import operator
#import matplotlib.pyplot as plt
from random import choice #imports a function to select a list entry

##################################################################
# box_generator_corners
#
# Arguments: N - Number of point in the box
#            D - Dimension of the box
#
# Returns:   List - 
#            [0] - DAG of the box
#            [1] - [name of node in extreme corner, name of origin node]
#
###################################################################

def box_generator_corners(N, D):
    # makes box diagram of N points in D dimensions
    DAG = nx.DiGraph()
    # generate list of box points
    box_list = []
    for point in range(N-2): #minus two as points will be added in two extremal corners
        box_list.append([])
        for coord in range(D):
            # generate random co-ord in range 0-1
            rand = random.random()
            box_list[point].append(rand)
    
    #adds to extra points to the random box DAG
    #one at the origin and one at the point (1,...1) for all dimensions
    origin = []
    extreme_corner = []    
    for coord in range(D):
        origin.append(0)
        extreme_corner.append(1)
    box_list.append(origin)
    box_list.append(extreme_corner)
    # box_list is now an N length list of points, which are D length lists of co-ords         
    
    # lists not hashable so need graph objects to be tuples
    for i in range(len(box_list)):
        box_list[i] = tuple(box_list[i])
        
    # add points to DAG object
    for node in box_list:
        DAG.add_node(node)
        
    # add edges if nodes are causally connected    
    for node1 in box_list:
        for node2 in box_list:
            link = True
            if node1 == node2:
                link = False
            for d in range(D):
                if node1[d] > node2[d]:
                    link = False
            if link:
                DAG.add_edge(node2, node1) #direct edges from higher birthdates to lower birthdates
                
    order_dag(DAG) #assigns a birthday attribute to each dag 
    extremes = [tuple(extreme_corner), tuple(origin)]
    return [DAG, extremes]

##################################################################
# box_generator_extremes
#
# Arguments: N - Number of point in the box
#            D - Dimension of the box
#
# Returns:   List - 
#            [0] - DAG of the box
#            [1] - [name of node with highest birthday, name of node with lowest birthday]
#
###################################################################

def box_generator_extremes(N, D):
    # makes box diagram of N points in D dimensions
    DAG = nx.DiGraph()
    # generate list of box points
    box_list = []
    for point in range(N):
        box_list.append([])
        for coord in range(D):
            # generate random co-ord in range 0-1
            rand = random.random()
            box_list[point].append(rand)
            
    # box_list is now a N length list of points, which are D length lists of co-ords         
    
    # lists not hashable so need graph objects to be tuples
    for i in range(len(box_list)):
        box_list[i] = tuple(box_list[i])
        
    # add points to DAG object
    for node in box_list:
        DAG.add_node(node)
        
    # add edges if nodes are causally connected    
    for node1 in box_list:
        for node2 in box_list:
            link = True
            if node1 == node2:
                link = False
            for d in range(D):
                if node1[d] > node2[d]:
                    link = False
            if link:
                DAG.add_edge(node2, node1) #direct edges from higher birthdates to lower birthdates
                
    extremes = order_dag_return_extremes(DAG, D) #assigns an age attribute to each dag and finds the extremal points in each box       
    return [DAG, extremes]

def order_dag(DAG):
    # changes the name of the points of a DAG to a natural ordering. It is enough to order by the average of the co-ordinates - this is
    # equivalent to sweeping a diagonal line through the box.
    
    # create dict of node name to coord sum
    # papers further from the origin will have a higher 'birthday' value
    for node in DAG.nodes():
        sum_coords = sum(node)
        DAG.node[node]['birthday'] = sum_coords #allocates time attribute (the 'birthday') to each node    

    
def order_dag_return_extremes(DAG, D):
    # changes the name of the points of a DAG to a natural ordering. It is enough to order by the total of the co-ordinates - this is
    # equivalent to sweeping a diagonal line through the box but drops constant factors (1/sqrt(D))
    # Whilst asigning each node an age attribute, this also keeps track of the nodes with the largest and smallest ages
    
    min = D #initialises at highest possible time value
    min_node = 0
    max = 0 #initialises at lowest possible time value
    max_node = 0
    for node in DAG.nodes():
        sum_coords = sum(node)
        DAG.node[node]['birthday'] = sum_coords #allocates time attribute to each node
        if sum_coords > max:
            max_node = node
            max = sum_coords
        if sum_coords < min:
            min_node = node
            min = sum_coords
    return [max_node , min_node] #returns two nodes at the extremes of the box for pathlength testing