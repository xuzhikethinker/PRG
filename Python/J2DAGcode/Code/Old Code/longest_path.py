import networkx as nx
import random
import math
import operator

def jsquared_algorithm(DAG, path_ends, out, count):
    
    print 'J^2 testing started'
    paths = find_all_paths(DAG, path_ends) #creates list containing lists of lists of lists...containing a list of the nodes in path
    empty = [] #creates the list which will contain all of the paths
    listofpaths = make_list_of_paths(paths, empty) #looks through 'paths' to extract proper paths from the various levels of sublists
    pathset = [] #creates the list which will contain all unique paths
    for path in listofpaths:
        if path not in pathset:
            pathset.append(path) #only adds unique paths to pathset
    out.write('\n' + 'J^2 without birthday considerations...%d unique paths found' %(len(pathset)))
    #for path in pathset:
    #    out.write('\n' + str(path)) #prints all the unique paths
    longestpath = max(pathset, key=len) #identifies the longest path in the set of paths
    out.write('\n' + 'The longest path from J^2 is %s which is %d nodes long (i.e. path length is this minus 1)' %(str(longestpath), len(longestpath)))
    print 'J^2 testing completed'       

def jsquared_with_birthdays(DAG, path_ends, out, count):
    
    print 'J^2 with birthdays testing started'
    birthday_attribute = nx.get_node_attributes(DAG, 'birthday') #summons the birthday attribute
    paths = find_all_paths_birthdays(DAG, path_ends, birthday_attribute, count, out) #creates list containing lists of lists of lists...containing a list of the nodes in path
    out.write('\n' + str(paths))
    empty = [] #creates the list which will contain all of the paths
    listofpaths = make_list_of_paths(paths, empty) #looks through 'paths' to extract proper paths from the various levels of sublists
    pathset = [] #creates the list which will contain all unique paths
    for path in listofpaths:
        if path not in pathset:
            pathset.append(path) #only adds unique paths to pathset
    out.write('\n' + 'J^2 with birthday considerations...%d unique paths found' %(len(pathset)))
    for path in pathset:
        out.write('\n' + str(path)) #prints all the unique paths
    longestpath = max(pathset, key=len) #identifies the longest path in the set of paths
    out.write('\n' + 'The longest path from J^2 with birthdays is %s which is %d nodes long (i.e. path length is this minus 1) ' %(str(longestpath), len(longestpath)))
    print 'J^2 with birthdays testing completed'    


def tim_algorithm(DAG, path_ends, out, count):
    print 'Starting Tim algorithm'
    #creates a new attribute for each node (the 'path_length') and initialises it to 0
    for node in DAG.nodes():
        DAG.node[node]['path_length'] = -1
        
    DAG.node[path_ends[0]]['path_length_with_birthdays'] = 0        
        
    path_length_label = nx.get_node_attributes(DAG, 'path_length') #gathers the dict that will be passed to the label_distances function
    label_distance_to_nodes(DAG, path_ends[0], path_length_label, count) #changes 'path_length' attribute to reflect path length from the start of the path
        
    #outputs the path_length attribute of the oldest point, indicating the longest path
    out.write('\n' + 'Path testing using label...distance to the end is %d' %(path_length_label[path_ends[1]]))
    print 'Finished Tim Algorithm'
    
def tim_algorithm_with_ordered_search(DAG, path_ends, out, count):
    print 'Starting Tim algorithm With Order'
    #creates a new attribute for each node (the 'path_length_with_label') and initialises it to 0
    for node in DAG.nodes():
        DAG.node[node]['path_length_with_order'] = -1
    DAG.node[path_ends[0]]['path_length_with_order'] = 0
    
    birthday_attribute = nx.get_node_attributes(DAG, 'birthday') #summons the birthday attribute    
    path_length_with_order_label = nx.get_node_attributes(DAG, 'path_length_with_order') #gathers the dict that will be passed to the label_distances function
    label_distance_to_nodes_with_ordered_search(DAG, path_ends[0], path_length_with_order_label, birthday_attribute, count) #changes 'path_length' attribute to reflect path length from the node 'n'
        
    #outputs the path_length attribute of the oldest point, indicating the longest path
    out.write('\n' + 'Path testing using label with order...distance to the end is %d' %(path_length_with_order_label[path_ends[1]]))
    out.write('\n' + 'The path is:')
    path_finder(DAG,path_ends[0], path_ends[1], path_length_with_order_label, out)
    print 'Finished Tim Algorithm With Order'
    
def tim_algorithm_with_ordered_search_and_birthdays(DAG, path_ends, out, count):
    #Work in progress.
    # path_ends = [start, end]
    #Algorithm will both order the successors and ignore paths when the birthday of the current node is lower than that of the target node
    print 'Starting Tim algorithm With Order and Birthdays'
    #creates a new attribute for each node (the 'path_length_with_label') and initialises it to 0
    for node in DAG.nodes():
        DAG.node[node]['path_length_with_birthdays'] = -1
    DAG.node[path_ends[0]]['path_length_with_birthdays'] = 0
    
    birthday_attribute = nx.get_node_attributes(DAG, 'birthday') #summons the birthday attribute    
    path_length_with_birthdays_label = nx.get_node_attributes(DAG, 'path_length_with_birthdays') #gathers the dict that will be passed to the label_distances function
    label_distance_to_nodes_with_birthdays(DAG, path_ends[0], path_ends[1], path_length_with_birthdays_label, birthday_attribute, count,out, 0) #changes 'path_length' attribute to reflect path length from the node 'n'
        
    #outputs the path_length attribute of the oldest point, indicating the longest path
    out.write('\n' + 'Path testing using label with order and birthdays...distance to the end is %d' %(path_length_with_birthdays_label[path_ends[1]]))
    out.write('\n' + 'The path is:')
    path_finder(DAG,path_ends[0], path_ends[1], path_length_with_birthdays_label, out)
    print 'Finished Tim Algorithm With Order and Birthdays'
    
'''def path_finder(DAG, start, current, label, out):
    current_label = label[current]
    out.write('\n' + str(current))
    for node in DAG.predecessors(current):
        node_label = label[node]
        if node_label == 0:
            x = 1 #i.e. do nothing at all...
        elif node_label == current_label - 1:
            path_finder(DAG, start, node, label, out)'''
            
def path_finder(DAG, start, current, label, out):
    current_label = label[current]
    out.write('\n' + str(current))
    for node in DAG.predecessors(current):
        node_label = label[node]
        if node_label == current_label - 1:
            if node == start:
                out.write('\n' + str(node))
                out.write('\n' + 'Longest path found' + '\n')
            else:                
                path_finder(DAG, start, node, label, out)
    
def find_all_paths(DAG, path_ends, count, path=[]):
    start = path_ends[0]
    end = path_ends[1]
    path = path + [start]
    count.write('0' + '\n') #prints 1 to counting file, to measure how many times function is called
    if start == end:
        return path
    if DAG.successors(start) == []:
        return []
    paths = []
    for node in DAG.successors(start):
        if node not in path:
            new_path_ends = [node, end]
            newpaths = find_all_paths(DAG, new_path_ends, count, path)
            paths.append(newpaths)
    return paths    
    
def find_all_paths_birthdays(DAG, path_ends, birthday_attribute, count, out, path=[]):
    start = path_ends[0]
    end = path_ends[1]
    path = path + [start]
    count.write('1' + '\n') #prints 1 to counting file, to measure how many times function is called
    if start == end:
        return path
    if DAG.successors(start) == []:
        return []
    paths = []
    for node in DAG.successors(start):
        if compare_birthdays(DAG, node, end, birthday_attribute): #if the node is younger than end [end is the most recent paper, right?]
            if node not in path:
                new_path_ends = [node, end]                
                newpaths = find_all_paths_birthdays(DAG, new_path_ends, birthday_attribute, count,out, path)
                paths.append(newpaths)
                #out.write('\n' + str(paths))
    return paths

def label_distance_to_nodes(DAG, start, labels, count): #adds an attribute to each node giving the path length from the initial 'start' node
    count.write('2' + '\n') #prints 2 to counting file, to measure how many times function is called
    # We need to try and order the nodes in DAG.successors so that we pick the youngest (nearest to the start) node to look at
    # 
    for node in DAG.successors(start): #cycles through all of the nodes in 'start's family tree
        length_n = labels[node] #finds current path length label at node
        length_start = labels[start] #finds current path length label at start
        if length_n < length_start + 1: #if the path length at node is less than the path length at start+1, a longer path to node can be found via start
            labels[node] = length_start + 1 #change the path length label to reflect taking a path to node via start
            label_distance_to_nodes(DAG, node, labels, count)  #now consider the nodes attached to node, to see if their path lengths can be increased via start and node 

def label_distance_to_nodes_with_ordered_search(DAG, start, labels, birthday_attribute, count, depth=0): #adds an attribute to each node giving the path length from the initial 'start' node
    #Work in progress
    #trying to build in knowledge of node ages to get algorithm to label youngest successors of a given node first
    count.write(str(depth) + '\n') # prints recursion depth
    # We need to try and order the nodes in DAG.successors so that we pick the youngest (nearest to the start) node to look at
    # 
    ordered_successors = order_successors(DAG.successors(start), birthday_attribute) #creates a list of a node's successors, ordered from youngest to oldest
    for node in ordered_successors: #cycles through all of the nodes in 'start's family tree
        length_n = labels[node] #finds current path length label at node
        length_start = labels[start] #finds current path length label at start
        if length_n < length_start + 1: #if the path length at node is less than the path length at start+1, a longer path to node can be found via start
            labels[node] = length_start + 1 #change the path length label to reflect taking a path to node via start
            label_distance_to_nodes_with_ordered_search(DAG, node, labels, birthday_attribute, count, depth+1)  #now consider the nodes attached to node, to see if their path lengths can be increased via start and node

def label_distance_to_nodes_with_birthdays(DAG, start, end, labels, birthday_attribute, count, out,depth): #adds an attribute to each node giving the path length from the initial 'start' node
    #Work in progress
    #trying to build in knowledge of node ages to get algorithm to label youngest successors of a given node first
    #count.write(depth + '\n') #prints the depth
    # We need to try and order the nodes in DAG.successors so that we pick the youngest (nearest to the start) node to look at
    # 
    ordered_successors = order_successors(DAG.successors(start), birthday_attribute) #creates a list of a node's successors, ordered from youngest to oldest
    for node in ordered_successors: #cycles through all of the nodes in 'start's family tree
        birthday_n = birthday_attribute[node]
        birthday_end = birthday_attribute[end]
        if compare_birthdays(DAG, node, end, birthday_attribute):
            length_n = labels[node] #finds current path length label at node
            length_start = labels[start] #finds current path length label at start
            if length_n < length_start + 1: #if the path length at node is less than the path length at start+1, a longer path to node can be found via start
                labels[node] = length_start + 1 #change the path length label to reflect taking a path to node via start
                label_distance_to_nodes_with_birthdays(DAG, node, end, labels, birthday_attribute, count,out, depth+1)  #now consider the nodes attached to node, to see if their path lengths can be increased via start and node            
    

        
    
    
def order_successors(successors, birthday_attribute):
    #Work in progress
    #Orders a node's successors from highest birthday to lowest birthday
    #We would expect that differences between birthdays on the longest path are small, so we want to label nodes where the birthday gap is small first
    #This should reduce the amount of relabeling that occurs
    successor_birthday_tuples = []
    ordered_successor_list = []
    for node in successors:
        birthday_of_node = birthday_attribute[node] #finds 'birthday' attribute of each successor
        successor_birthday_tuples.append((birthday_of_node, node)) #adds a tuple to the 'successor_birthday_tuples' list of the form (birthday, node name)
    sorted_successor_tuples = sorted(successor_birthday_tuples, reverse=True) #sorts the 'successor_birthday_tuples' list from largest to smallest birthday
    for tuple in sorted_successor_tuples:
        ordered_successor_list.append(tuple[1]) #creates a list from the ordered (birthday, node name) list just of the node names
    return ordered_successor_list #return a list of the nodes, reverse ordered by birthday
    
def make_list_of_paths(paths, pathlist):
    for path in paths:
        if isinstance(path, tuple) or isinstance(path, str): #if 'path' is a list of tuples (the coordinates) or strings (arXiv names) add the list to pathlist
            pathlist.append(paths)
        if isinstance(path, list): #if 'path' is a list of lists, carry out the process again to check the composition of THOSE lists
            make_list_of_paths(path, pathlist)
    return pathlist #Return a list that just contains paths (in list form) of nodes (no lists of lists of lists of...)
    
      
def compare_birthdays(DAG, current, target, birthday_attribute):
    #Tests the ordering of two nodes, based upon their 'birthday' attribute
    #If bday of current is higher than bday of target, returns true
    #If bday of current is lower than bday (i.e. current was published to the past of target), returns false
    bday_current = birthday_attribute[current] #finds the birthday of the current node
    bday_target = birthday_attribute[target] #finds the birthday of the target node
    #out.write('\n'+'The current bday is %f and the target bday is %f' %(bday_current, bday_target))
    if bday_current >= bday_target:
        #out.write('\n'+'True')
        return True
    if bday_current < bday_target:
        #out.write('\n'+'False')
        return False