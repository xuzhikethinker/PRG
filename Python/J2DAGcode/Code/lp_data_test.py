#lp_data_test
#testing of the longest path method and estimate methods on the arXiv

#not pretty to look at...

import dag_lib as dl
import time
import trans_red as tr
import math
import Models2 as m2
import MM_dimension as MM
import data_DAG as maker
from random import choice
import networkx as nx
import re
import longest_path as lp
import lp_estimates as gip

#This should give the longest path between every node along the top and bottom of the DAG, and print it to the file 'out'
def find_all_lpd(DAG,no_in,no_out,out):

    length = nx.get_node_attributes(DAG, 'length')
    N = float(len(no_in))
    i = 0
    for node_in in no_in:
        for node in DAG.nodes():
            length[node] = -1
        length[node_in] = 0
        lp.label_distance_to_nodes(DAG,node_in,length)
        for node_out in no_out:
            if nx.has_path(DAG,node_in,node_out):
                L = length[node_out]
                out.write(str(node_in) + '\t' + str(node_out) + '\t' + str(L) + '\n')
            else:
                out.write(str(node_in) + '\t' + str(node_out) + '\t' + str(None) + '\n')
        i += 1.
        print (i/N) * 100.
        


    
if __name__ == '__main__':
    
    print 'Making DAG'
    
    no_out_file = open('./HepPh_no_out.txt', 'r')
    no_in_file = open('./HepPh_no_in.txt', 'r')

    #outa = open('./HepPh_lp_tests_a.txt','w')
    outb = open('./HepPh_lp_tests_b.txt','w')
    data = open('./Data/Cit-HepPh.DAT', 'r')
    ar = open('./Data/Cit-HepPh_TR.txt', 'r')
    paths_file = open('./HepPh_lp.txt','w') #longest paths between every no_in_node and no_out_node
    #longest_path_file = open('./HepPh_lp_longest.txt','r') #the very longest path found from every no_in_node
    #DAG = maker.birthdayDAG(data)
    DAG = maker.birthdayDAG(ar) #using TR of data
    print 'Made DAG'

    no_out = []
    no_in = []

    for line in no_out_file:
        no_out.append(line.strip())

    for line in no_in_file:
        no_in.append(line.strip())
        
    IN = len(no_in)
    OUT = len(no_out)
    
        
    
    
    #make dictionary of EVERY goddamn combo of no_in, no_out nodes and the longest path length
    
    paths = {}
    longest_paths = {}
    
    #lp = 0
    #furthest = []
    
    '''for line in longest_path_file:
        result = re.findall(r'\w+', line)
        start = result[0]
        length = result[-1]
        longest_paths[start] = int(length)'''
        
    
    very_lp = 0
    lp_start = None
    lp_end = None    
    

    
    ''' i = 0
    for line in paths_file:
        result = re.findall(r'\w+', line)
        print result
        start = result[0]
        end = result[1]
        length = int(result[2])
        paths[(start,end)] = int(length)
        print paths[(start,end)]
        if length > very_lp: #Finds the longest path in the data
            very_lp = length
            lp_start = start
            lp_end = end
        if length > lp:
            furthest = [end]
            lp = length
        elif length == lp:
            furthest.append(end)
        i += 1
        if i == OUT:
            out.write(str(start) + '\t')
            for node in furthest:
                out.write(str(node) + '\t')
            out.write(str(lp) + '\n')
            #initialise all for the next start node

            i = 0
            lp = 0
            furthest = []'''
            
    
            
    '''for node_in in no_in:
        longest = 0
        furthest = None
        for node_out in no_out:
            length = paths[(node_in,node_out)]
            if length > longest:
                longest = length
                furthest = node_out
        longest_path_file.write(str(node_in) + '\t' + str(furthest) + '\t' + str(longest) + '\n')'''
            

            
    list = [0,100,200,300,400,500,750,1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000,16000,20000,24000,28000,32000,36000,40000,50000,60000,70000,80000,90000,100000]
    
    #set up the data nicely...
    #outa.write('Start' + '\t' + 'LP' + '\t' + 'Indic' + '\t')
    '''outb.write('Start' + '\t' + 'LP' + '\t' + 'Indic' + '\t')
    
    for item in list:
        #outa.write(str(item) + '\t')
        outb.write(str(item) + '\t')
        
    #outa.write('\n')
    outb.write('\n')
    
    j = 0
    for node in no_in:
        lp = longest_paths[node]
        #greedy_path = gip.greedy_path(DAG,node) #possible error with code
        indec_path = gip.indecisive_path(DAG,node,'start')
        #outa.write(str(node) + '\t' + str(lp) + '\t' + str(len(indec_path)-1) + '\t')
        outb.write(str(node) + '\t' + str(lp) + '\t' + str(len(indec_path)-1) + '\t')
        for threshold in list:
            path = gip.gip(DAG,node,threshold)
            length = len(path) - 1 #length of the gip, minus one for comparison to lpd which counts edges, not nodes
            #difference_longest = lp - length #difference between the gip length and the longest possible path from start
            #outa.write(str(length) + '\t')
            end = path[-1] #the node where the gip ended
            length_end = paths[(start,end)] - 1 #the longest path between the start and the end
            #difference_end = length_end - length
            outb.write(str(length_end) + '\t')
            
        #outa.write('\n')
        outb.write('\n')
        j += 1
        print (float(j)/float(IN)) *100.
        
    print 'The longest path in the data is %d nodes long and goes from %s to %s' %(very_lp,lp_start,lp_end)    '''    
            
    find_all_lpd(DAG,no_in,no_out,paths_file)    



