

import networkx as nx
import dag_lib as dl
import trans_red as tr
import re
import math
#import sympy
from sympy import Eq, Symbol, solve

def MM_dimension(DAG, k, out):


    
    DAG_TC = tr.trans_comp(DAG) #Generates the transitive completion of the DAG
    number_nodes = DAG_TC.number_of_nodes() #counts the number of nodes
    
    if k == 2:
        number_chains = DAG_TC.number_of_edges() #the number of two chains in a DAG is the same as the number of edges in its transitive completion
        
    if k==3:
        number_chains = len(three_chain(DAG_TC)) #uses the 'three chain' function to create the list of 3 chains in the DAG
    
    ratio = (float(number_chains))/((float(number_nodes)**2)) #calculates S_{k}/N^{2}, which can be related to the dimension
    
    dimension_function = open('C:\Users\Jamie\Project\gamma.txt', 'r') #input values of the f(dimension) for a range of dimension values
    
    min_diff = 1000
    d_closest = 0
    
    for line in dimension_function:
        values = line.split()
        d = values[0]
        f_d = values[1]
        diff = abs(f_d - ratio)
        if diff < min_diff:
            min_diff = diff
            d_closest = d
            
    out.write('\n' + 'The dimension is %f' %d_closest)
        
        
    
    
def three_chain(DAG_TC): #Uses the transitive completion of the DAG to find all of the three chains in the DAG
    three_chain_list = []
    for edge in DAG_TC.edges(): #Iterates over every edge in the TC, which is also every 2 chain
        [node1, node2] = edge
        for node in DAG_TC.nodes():
            if dl.age_check(DAG_TC, node1, node): #If a node has birthdays between each end of the 2 chain, it could be possible to find a 3 chain that has this node inbetween the the ends of the 2 chain
                if dl.age_check(DAG_TC, node, node2):
                    if nx.has_path(DAG_TC, node1, node):
                        if nx.has_path(DAG_TC, node, node2): 
                            three_chain_list.append([node1, node, node2]) #If a three chain can be formed, add it to the list
    return three_chain_list
    
def dimension_function(d): #calculates the value of the dimension function for 2 chains
    a = math.gamma(d+1)
    b = math.gamma(0.5*d)
    c = math.gamma(1.5*d)
    f = (a*b)/(4.0*c)
    return f

                                
    