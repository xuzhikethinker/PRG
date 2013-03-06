# midpoint scaling dimension

import dag_lib as dl
import trans_red as tr
import time
import math
import networkx as nx

# DAG - the DAG object in networkx of the interval the path spans
# path - list of the longest path from start to finish
def mpsd(DAG, path):
    pl = len(path)
    start = path.pop(0)
    end = path.pop(-1)
    interval_sizes=[]
    # Find midpoint
    print path
    for node in path:
        interval_a = interval_size(DAG, start, node)
        interval_b = interval_size(DAG, node, end)
        interval = min(interval_a, interval_b)
        interval_sizes.append(interval)
 
    
    midpoint_index = interval_sizes.index(max(interval_sizes))
    midpoint = path[midpoint_index]
    path = []  
      
    interval_a = interval_size(DAG, start, midpoint)
    interval_b = interval_size(DAG, midpoint, end)
    total_interval = interval_size(DAG, start, end)
    
    print 'a = %s' % interval_a
    print 'b = %s' % interval_b
    print 'total = %s' % total_interval
    
    ratio = (float(interval_a + interval_b)/2)/(total_interval)
    # ratio = 1/2**(d)
    if ratio == 0:
        dimension = None
    else:
        dimension = math.log((1.0/ratio), 2)
    
    
    return [dimension, ratio, interval_a, interval_b, total_interval]
    
def interval_size(DAG, start, end):
    
    i = 0
    #print 'checking interval from %s to %s' % (start, end)
    for node in DAG.nodes():        
        if nx.has_path(DAG, start, node):
            if nx.has_path(DAG, node, end):
                i += 1
                #print 'found node %s in interval %s to %s' % (node, start, end)
       
                 
            pass
    return i
    
    
    
    
    
    
    

