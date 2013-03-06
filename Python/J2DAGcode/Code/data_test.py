# data_test.py
# This file contains functions which can give you a factfile about a network, and its transitive reduction

import data_DAG as maker
import dag_lib as dl
import networkx as nx
import trans_red as tr
import clustering as clus

DAG_name = 'Cit-HepTot'

original_data_file = './Data/Cit-HepTot.dat'
tr_data_file = './Data/Cit-HepTot_TR.txt'
results_data_file = './Results/%s-Factfile.txt' % DAG_name

tr_data = open(tr_data_file, 'r')

def create_TR_data(DAG, filename):
   
    print 'Making TRed DAG'
    TR = tr.trans_red(DAG)
    print 'TR made'

    # Write TR to data file
    full_filename = './Data/%s.txt' % filename
    TR_out = open(full_filename, 'w')
    for edge in TR.edges():
        TR_out.write(edge[0] + '\t' + edge[1] + '\n')
    return TR    
        

out = open(results_data_file, 'w')

out.write('DAG - ' + original_data_file + '\n')
original_data = open(original_data_file, 'r')
print 'Making DAG from data'
try:
    DAG = maker.birthdayDAG(original_data)
except:
    DAG = maker.basicDAG(original_data)
    print 'DAG made'

try: # Try getting the TR data from a file
    tr_data = open(tr_data_file, 'r')
    print 'Making TR DAG from data'
    try:
        TR = maker.birthdayDAG(tr_data)
    except:
        TR = maker.basicDAG(tr_data)
    print 'DAG made'  
except: # Couldn't find TR data from a file, so create a new TR now, and save it
    print 'Creating new TR data file'
    tr_data_file = 'new_TR_data'
    TR = create_TR_data(DAG, tr_data_file)
    print 'Completed creating TR'
out.write('TR DAG - ' + tr_data_file + '\n')   



def factfile(DAG, TR, out, DAG_name='temp'):
    N = DAG.number_of_nodes()
    E = DAG.number_of_edges()
    [c_plus,c_zero,c_minus] = clus.clustering(DAG)
    N_TR = TR.number_of_nodes()
    E_TR = TR.number_of_edges()
    [c_plus_TR,c_zero_TR,c_minus_TR] = clus.clustering(TR)
    degree_test(DAG, TR, DAG_name)
    
    out.write('DAG:' + '\n')
    out.write('Number of nodes: ' + str(N) + '\n')
    out.write('Number of edges: ' + str(E) + '\n')
    out.write('clustering_plus: ' + str(c_plus) + '\n')
    out.write('clustering_zero: ' + str(c_zero) + '\n')
    out.write('clustering_minus: ' + str(c_minus) + '\n')
    
    out.write('Transitive Reduction of DAG:' + '\n')
    out.write('Number of nodes: ' + str(N_TR) + '\n')
    out.write('Number of edges: ' + str(E_TR) + '\n')
    out.write('clustering_plus: ' + str(c_plus_TR) + '\n')
    out.write('clustering_zero: ' + str(c_zero_TR) + '\n')
    out.write('clustering_minus: ' + str(c_minus_TR) + '\n')
    
def degree_test(DAG, TR, DAG_name):
    k_in_filename = './Results/%s-k_in.txt' % DAG_name
    k_out_filename = './Results/%s-k_out.txt' % DAG_name

    k_in = open(k_in_filename, 'w')
    k_out = open(k_out_filename, 'w')
    k_in.write('node' + '\t' + 'k_in' + '\t' + 'k_in_TR' + '\n')
    k_out.write('node' + '\t' + 'k_out' + '\t' + 'k_out_TR' + '\n')
    
    max_in = 0
    max_out = 0
    most_cited = 0
    most_references = 0
    
    #try to find nodes which experience largest and smallest changes in degree by considering k_TR/k
    min_gradient = 1. #initialise at 1
    interesting_node = 0
    
    no_change = [] #list of nodes that experience no change in (in-)degree when TR
    no_in = [] #nodes that have 0 in degree
    no_out = [] #nodes that have 0 out degree
    
    for node in DAG.nodes():
        in_data = DAG.in_degree(node)
        out_data = DAG.out_degree(node)
        in_TR = TR.in_degree(node)
        out_TR = TR.out_degree(node)
        
        #Find which node in the original data set has the largest in degree (before TR)
        if in_data > max_in:
            max_in = in_data
            most_cited = node

        #Find which node in the original data set has the largest out degree (before TR)            
        if out_data > max_out:
            max_out = out_data
            most_references = node
            
        if in_data == 0:
            no_in.append(node)
        elif in_data > 0:
            gradient = float(in_TR)/float(in_data)
            if gradient == 1.:
                no_change.append(node)
            if in_data > 30:                    
                    if gradient < min_gradient:
                        interesting_node = node
                        min_gradient = gradient
                    
                
        if out_data == 0:
            no_out.append(node)
        
        #Find out which node sits furthest from the trend line on the graph of k_TR against k_data        

            

        
        k_in.write(str(node) + '\t' + str(in_data) + '\t' + str(in_TR) + '\n')
        k_out.write(str(node) + '\t' + str(out_data) + '\t' + str(out_TR) + '\n')
        
    print 'In'
    print most_cited
    print max_in
    
    print 'Out'
    print most_references
    print max_out
    
    print 'Smallest k_TR/k_data (i.e. interesting node)'
    print interesting_node
    print min_gradient
    
    print 'Number of nodes whose in degree did not change when TRed'
    print len(no_change)
    
    print 'Number of nodes with no citations'
    print len(no_in)
    
    print 'Number of nodes with no references'
    print len(no_out)

factfile(DAG, TR, out, DAG_name)


#Identify loops
'''i = 0
for node in DAG.nodes():
    i +=1
    succs = DAG.successors(node)
    for succ in succs:
        if nx.has_path(DAG, succ,node):
            loop.append(node)
            break'''


                
'''
max_delta_in = 0.
max_in = 0
max_out = 0

interesting_node = 0
            
for node in DAG.nodes():
    in_data = DAG.in_degree(node)
    out_data = DAG.out_degree(node)
    in_TR = DAG_TR.in_degree(node)
    out_TR = DAG_TR.out_degree(node)
    
    if in_data > 0:
        delta_in = (in_data - in_TR)/in_data
        out.write(str(delta_in) + '\n')
    if delta_in > max_delta_in:
        interesting_node = node
        max_delta_in = delta_in
        
    if in_data > max_in:
        max_in = in_data
    
    if out_data > max_out:
        max_out = out_data

print max_in
print max_out
        
print interesting_node
print max_delta_in
       
print clus.calculate_c0(DAG)'''