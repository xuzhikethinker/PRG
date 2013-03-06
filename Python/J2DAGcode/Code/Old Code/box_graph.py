# Function to create a D dimensional, N point box causal set.

# To do: change the names of the nodes to that of a natural ordering
#        to test out the find_all_paths_age function which should
#        use the natural ordering to optimise the path finding


import networkx as nx
import random
import math

N = 100
D = 2
num_boxes = 100


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
                
    return DAG 

def box_analysis(N, D, num_boxes):

    boxes = []
    edges = []
    nodes = []
    
    for i in range(num_boxes):
        box = box_generator(N, D)
        boxes.append(box)
        #print 'Number of edges = %s' % str(len(box.edges()))
        #print 'Fraction of possible edges = %s  ' % str( len(box.edges()) * 100.0 // (N*(N+1)/2))
        edges.append(len(box.edges()))
        
    print 'Over %s boxes of dimension %s with %s points:' % (num_boxes, D, N)
    av_edges = sum(edges) // num_boxes
    
    print 'Average number of edges is %s' % str(av_edges)
    print 'Total possible number of edges is %s' % (N*(N+1)/2)
    percentage = str(100 * float(av_edges) / float(N*(N+1)/2))
    print 'Average fraction of possible edges that are allowed: %s' % (percentage[0:5] + ' %')
    
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
            for newpath in newpaths:
                paths.append(newpath)
    return paths    
    
def order_dag(DAG):
    # changes the name of the points of a DAG to a natural ordering. It is enough to order by the average of the co-ordinates - this is
    # equivalent to sweeping a diagonal line through the box.
    
    # create dict of node name to coord sum
    for node in DAG.nodes():
        sum_coords = sum(node)
        DAG.node[node]['sum_coords'] = sum_coords
        
    # order the list of     
    
    
if __name__ == "__main__": 
    #box_analysis(N, D, num_boxes)
    
    