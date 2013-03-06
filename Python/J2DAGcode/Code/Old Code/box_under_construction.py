# Function to create a D dimensional, N point box causal set.

# Naming convention - Young means near to the start of the DAG
#                     Old means near to the end of the DAG
#


# To do: change the names of the nodes to that of a natural ordering
#        to test out the find_all_paths_age function which should
#        use the natural ordering to optimise the path finding


import networkx as nx
import random
import math
import time
import operator
#import matplotlib.pyplot as plt
from random import choice #imports a function to select a list entry

N = 3000
D = 2
num_boxes = 1

outfile = './out.txt'
countfile = './count.txt'
analysisfile = './analysis.txt'

counter1 = 0
counter2 = 0
counter3 = 0
counter4 = 0

##################################################################
# box_generator
#
# Arguments: N - Number of point in the box
#            D - Dimension of the box
#
# Returns:   List - 
#            [0] - DAG of the box
#            [1] - [name of oldest node, name of youngest node]
#
###################################################################
def box_generator(N, D):
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
                DAG.add_edge(node1, node2)
                
    extremes = order_dag_return_extremes(DAG, D) #assigns an age attribute to each dag and finds the extremal points in each box       
    return [DAG, extremes]

def box_analysis(N, D, num_boxes, out, count, analysis):

    boxes = []
    edges = []
    nodes = []
    extremes = []
    
    for i in range(num_boxes):
        full_box = box_generator(N, D) #full box is a list with first entry the new boxDAG and second entry a list containg the two extremal points
        box = full_box[0] #box is the DAG part of full box
        boxes.append(box)
        extremes.append(full_box[1])
        #print 'Number of edges = %s' % str(len(box.edges()))
        #print 'Fraction of possible edges = %s  ' % str( len(box.edges()) * 100.0 // (N*(N+1)/2))
        edges.append(len(box.edges()))
        
    print 'Over %s boxes of dimension %s with %s points:' % (num_boxes, D, N)
    av_edges = sum(edges) // num_boxes
    
    print 'Average number of edges is %s' % str(av_edges)
    print 'Total possible number of edges is %s' % (N*(N+1)/2)
    percentage = str(100 * float(av_edges) / float(N*(N+1)/2))
    print 'Average fraction of possible edges that are allowed: %s' % (percentage[0:5] + ' %')
    
    for box in boxes:
        extremal_points = extremes[i] #associates the right extremal values with the right box
        #n = choice(box.nodes()) #selects a random node
        n = extremal_points[1]
        out.write('\n' + 'The start node is %s and the end node is %s' %(extremal_points[0], extremal_points[1]))

        # Do ageless testing
        #start_time = time.clock()
        #ageless_testing(box, extremal_points, n, out)
        #ageless_time = time.clock() - start_time
        #print 'Ageless time = %s' % ageless_time
        
        # Do age testing
        #start_time = time.clock()
        #age_testing(box, extremal_points, n, out)
        #age_time = time.clock() - start_time
        #print 'Age time = %s' % age_time
        
        # Do Tim algorithm
        start_time = time.clock()
        tim_algorithm(box, extremal_points, n, out)
        tim_time = time.clock() - start_time
        print 'Tim time = %s' % tim_time
        
        
        # Do Tim algorithm with age
        start_time = time.clock()
        tim_algorithm_with_age(box, extremal_points, n, out)
        tim_with_age_time = time.clock() - start_time
        print 'Tim with age time = %s' % tim_with_age_time
        
        analysis.write(str(N) + '\t' + str(tim_time) + '\t' + str(tim_with_age_time) + '\n')
                               
    
def ageless_testing(box, extremal_points, n, out):
    
    print 'Ageless testing started'
    paths = find_all_paths(box, extremal_points[0] , n) #creates list containing lists of lists of lists...containing a list of the nodes in path
    empty = [] #creates the list which will contain all of the paths
    listofpaths = make_list_of_paths(paths, empty) #looks through 'paths' to extract proper paths from the various levels of sublists
    pathset = [] #creates the list which will contain all unique paths
    for path in listofpaths:
        if path not in pathset:
            pathset.append(path) #only adds unique paths to pathset
    out.write('\n' + 'Path testing without age considerations...%d unique paths found' %(len(pathset)))
    for path in pathset:
        out.write('\n' + str(path)) #prints all the unique paths
    longestpath = max(pathset, key=len) #identifies the longest path in the set of paths
    out.write('\n' + 'The longest path from age is %s which is %d nodes long (i.e. path length is this minus 1)' %(str(longestpath), len(longestpath)))
    print 'Ageless testing completed'       

def age_testing(box, extremal_points, n, out):
    paths = find_all_paths_age(box, extremal_points[0] , n) #creates list containing lists of lists of lists...containing a list of the nodes in path
    empty = [] #creates the list which will contain all of the paths
    listofpaths = make_list_of_paths(paths, empty) #looks through 'paths' to extract proper paths from the various levels of sublists
    pathset = [] #creates the list which will contain all unique paths
    for path in listofpaths:
        if path not in pathset:
            pathset.append(path) #only adds unique paths to pathset
    out.write('\n' + 'Path testing without age considerations...%d unique paths found' %(len(pathset)))
    for path in pathset:
        out.write('\n' + str(path)) #prints all the unique paths
    longestpath = max(pathset, key=len) #identifies the longest path in the set of paths
    out.write('\n' + 'The longest path from age is %s which is %d nodes long (i.e. path length is this minus 1) ' %(str(longestpath), len(longestpath)))
    print 'Testing using age completed'    


def tim_algorithm(box, extremal_points, n, out):
    print 'Starting Tim algorithm'
    #creates a new attribute for each node (the 'path_length') and initialises it to 0
    for node in box.nodes():
        box.node[node]['path_length'] = 0    
    path_length_label = nx.get_node_attributes(box, 'path_length') #gathers the dict that will be passed to the label_distances function
    label_distance_to_nodes(box, n, path_length_label) #changes 'path_length' attribute to reflect path length from the node 'n'
        
    #outputs the path_length attribute of the oldest point, indicating the longest path
    out.write('\n' + 'Path testing using label...distance to the end is %d' %(path_length_label[extremal_points[0]]))
    print 'Finished Tim Algorithm'
    
def tim_algorithm_with_age(box, extremal_points, n, out):
    print 'Starting Tim algorithm With Age'
    #creates a new attribute for each node (the 'path_length_with_label') and initialises it to 0
    for node in box.nodes():
        box.node[node]['path_length_with_age'] = 0    
    path_length_with_age_label = nx.get_node_attributes(box, 'path_length_with_age') #gathers the dict that will be passed to the label_distances function
    age = nx.get_node_attributes(box, 'sum_coords')
    label_distance_to_nodes_with_age(box, n, path_length_with_age_label, age, out) #changes 'path_length' attribute to reflect path length from the node 'n'
        
    #outputs the path_length attribute of the oldest point, indicating the longest path
    out.write('\n' + 'Path testing using label with age...distance to the end is %d' %(path_length_with_age_label[extremal_points[0]]))
    print 'Finished Tim Algorithm With Age'
    
    
def find_all_paths(DAG, start, end, path=[]):
    path = path + [start]
    count.write('0' + '\n') #prints 1 to counting file, to measure how many times function is called
    #counter1 += 1
    if start == end:
        return path
    if DAG.predecessors(start) == []:
        return []
    paths = []
    for node in DAG.predecessors(start):
        if node not in path:
            newpaths = find_all_paths(DAG, node, end, path)
            paths.append(newpaths)
    return paths    
    
def find_all_paths_age(DAG, start, end, path=[]):
    path = path + [start]
    count.write('1' + '\n') #prints 1 to counting file, to measure how many times function is called
    #counter2 += 1
    if start == end:
        return path
    if DAG.predecessors(start) == []:
        return []
    paths = []
    for node in DAG.predecessors(start):
        if not age(DAG, end, node): #if the node is younger than end [end is the most recent paper, right?]
            if node not in path:
                newpaths = find_all_paths_age(DAG, node, end, path)
                paths.append(newpaths)
    return paths

def label_distance_to_nodes(DAG, start, labels): #adds an attribute to each node giving the path length from the initial 'start' node
    count.write('2' + '\n') #prints 2 to counting file, to measure how many times function is called
    # We need to try and order the nodes in DAG.successors so that we pick the youngest (nearest to the start) node to look at
    # 
    for node in DAG.successors(start): #cycles through all of the nodes in 'start's family tree
        length_n = labels[node] #finds current path length label at node
        length_start = labels[start] #finds current path length label at start
        if length_n < length_start + 1: #if the path length at node is less than the path length at start+1, a longer path to node can be found via start
            labels[node] = length_start + 1 #change the path length label to reflect taking a path to node via start
            label_distance_to_nodes(DAG, node, labels)  #now consider the nodes attached to node, to see if their path lengths can be increased via start and node 

def label_distance_to_nodes_with_age(DAG, start, labels, age, out): #adds an attribute to each node giving the path length from the initial 'start' node
    #Work in progress
    #trying to build in knowledge of node ages to get algorithm to label youngest successors of a given node first
    count.write('3' + '\n') #prints 2 to counting file, to measure how many times function is called
    # We need to try and order the nodes in DAG.successors so that we pick the youngest (nearest to the start) node to look at
    # 
    ordered_successors = order_successors(DAG.successors(start), age, out) #creates a list of a node's successors, ordered from youngest to oldest
    for node in ordered_successors: #cycles through all of the nodes in 'start's family tree
        length_n = labels[node] #finds current path length label at node
        length_start = labels[start] #finds current path length label at start
        if length_n < length_start + 1: #if the path length at node is less than the path length at start+1, a longer path to node can be found via start
            labels[node] = length_start + 1 #change the path length label to reflect taking a path to node via start
            label_distance_to_nodes_with_age(DAG, node, labels, age, out)  #now consider the nodes attached to node, to see if their path lengths can be increased via start and node            
    
def order_successors(successors, age, out):
    #Work in progress
    #Orders a node's successors from youngest to oldest
    successor_age_tuples = []
    ordered_successor_list = []
    for node in successors:
        age_of_node = age[node] #finds 'age' attribute of each successor
        successor_age_tuples.append((age_of_node, node)) #adds a tuple to the 'successor_age_tuples' list of the form (age, coordinate)
    sorted_successor_tuples = sorted(successor_age_tuples) #sorts the 'successor_age_tuples' list by age
    for tuple in sorted_successor_tuples:
        ordered_successor_list.append(tuple[1]) #creates a list from the ordered (age, coords) list just of the coords
    return ordered_successor_list #return a list of the coordinates, ordered by age (theoretically)
    
def make_list_of_paths(paths, pathlist):
    for path in paths:
        if isinstance(path, tuple): #if 'path' is a list of tuples (the coordinates) add the list to pathlist
            pathlist.append(paths)
        if isinstance(path, list): #if 'path' is a list of lists, carry out the process again to check the composition of THOSE lists
            make_list_of_paths(path, pathlist)
    return pathlist #Return a list that just contains paths (in list form) of nodes (no lists of lists of lists of...)
    
def order_dag(DAG, D):
    # NEEDS FINISHING
    # changes the name of the points of a DAG to a natural ordering. It is enough to order by the average of the co-ordinates - this is
    # equivalent to sweeping a diagonal line through the box.
    
    # create dict of node name to coord sum
    for node in DAG.nodes():
        sum_coords = sum(node)
        DAG.node[node]['sum_coords'] = sum_coords #allocates time attribute to each node
        
    # order the list of coordinates
    
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
        DAG.node[node]['sum_coords'] = sum_coords #allocates time attribute to each node
        if sum_coords > max:
            max_node = node
            max = sum_coords
        if sum_coords < min:
            min_node = node
            min = sum_coords
    return [max_node , min_node] #returns two nodes at the extremes of the box for pathlength testing
        
        

def age(DAG, n1, n2):
    #Tests the ordering of two nodes. Prevents path being followed if a node is younger (more recently published) than the start node
    #if n2 is younger than n1, returns True
    age = nx.get_node_attributes(DAG, 'sum_coords')
    age1 = float(age[n1]) #This is the age of n1
    age2 = float(age[n2]) #This is the age of n2
    if age1 > age2: #if n1 is further away than n2 from the origin, n2 is younger, so true is returned
        return True
    if age1 < age2: #if n1 is closer than n2 from the origin, n2 is older, so false is returned
        return False
        
def read_count(file):
    # Just reads out how many of each number has been printed out to the count file
    # This is a bad way of doing stuff - promise to change it later
    source = open(file)
    lines = source.readlines()
    #print lines
    counter0 = 0
    counter1 = 0
    counter2 = 0
    counter3 = 0
    
    for thing in lines:
        thing = thing.strip()
        if thing == '0':
            counter0 += 1
        elif thing == '1':
            counter1 += 1
        elif thing == '2':
            counter2 += 1
        elif thing == '3':
            counter3 += 1
    return [counter0, counter1, counter2, counter3]        
        
    
    
if __name__ == "__main__": 
    out = open(outfile,'w') #output file  
    count = open(countfile,'a+') #output file  
    analysis = open(analysisfile, 'w') #analysis file
    
    n_list = [10, 20, 30, 40, 50, 75, 100, 200, 300, 400, 500, 750, 1000, 1250, 1500, 2000, 2250, 2500, 2750, 3000, 3250, 3500, 3750, 4000]
    for n in n_list:
        box_analysis(n, D, num_boxes, out, count, analysis)
        
    
    #box_analysis(N, D, num_boxes, out, count)  

    
    # Read out the numbers from count
    #counts = read_count(countfile)
    #print '##########################'
    #for count in counts:
    #    print count
        