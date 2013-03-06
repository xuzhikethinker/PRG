import networkx as nx
import dag_lib as dl
import trans_red as tr
import re
import math
#import sympy
#from sympy import Eq, Symbol, solve
from math import log, gamma, exp

###################################################################################
#
# NOTE: Must run MM_data() method before running MM_dimension method on a new computer
#       MM_data() generates a series of .txt files that will be referenced when
#       the MM_dimension method is estimating value of the dimension
#
###################################################################################

###################################################################################
# Myrheim-Meyer Dimension
# MM_dimension
#
# Arguments: DAG - networkx DAG object
#            k   - the method counts k-chains in the DAG, if empty, k=2 is assumed
#
# Returns:   d - the estimated dimension of the DAG
#            
###################################################################################
def MM_dimension(DAG, k=2):
    
    DAG_TC = tr.trans_comp(DAG) #Generates the transitive completion of the DAG
    #DAG_TC = DAG #here, as a box DAG = the transitive completion of a box DAG
    number_nodes = DAG_TC.number_of_nodes() #counts the number of nodes
    
    if k == 2:
        number_chains = DAG_TC.number_of_edges() #the number of two chains in a DAG is the same as the number of edges in its transitive completion
        
    if k == 3:
        chains = three_chain(DAG_TC)
        number_chains = len(chains)
    if k == 4:
        chains = four_chain(three_chain(DAG_TC), DAG_TC)
        number_chains = len(chains)
    if k == 5:
        chains = five_chain(four_chain(three_chain(DAG_TC), DAG_TC), DAG_TC)
        number_chains = len(chains)
    ratio = float(number_chains)/((float(number_nodes)**k)) #calculates S_{k}/N^{k}, which can be related to the dimension
    #ratio = log(number_chains) - k*log(number_nodes)
    
    dimension_data = './f_of_d_%d.txt' %k #Finds the f(d) values for the appropriate value of the chain length
    
    data = open(dimension_data, 'r')
    min_diff = 1000
    d_closest = 0
    
    for line in data:
        values = line.split()
        d = float(values[0])
        f_d = float(values[1])
        diff = abs(f_d - ratio)
        if diff < min_diff:
            min_diff = diff
            d_closest = d
            
    return d_closest #returns a single float value, indicating the dimension of the DAG
        
        
    
#Used by MM_dimension method to generate a list of the three chains in the DAG    
def three_chain(DAG_TC): #Uses the transitive completion of the DAG to find all of the three chains in the DAG
    three_chain_list = []
    for edge in DAG_TC.edges(): #Iterates over every edge in the TC, which is also every 2 chain
        [node1, node2] = edge
        for node in DAG_TC.nodes():
            if dl.age_check(DAG_TC, node1, node): #If a node has birthdays between each end of the 2 chain, it could be possible to find a 3 chain that has this node inbetween the the ends of the 2 chain
                if dl.age_check(DAG_TC, node, node2):
                    if nx.has_path(DAG_TC, node1, node): #check if there is a path to the middle node from the 1st
                        if nx.has_path(DAG_TC, node, node2): #check if there is a path from the middle node to the 2nd
                            three_chain_list.append([node1, node, node2]) #If a three chain can be formed, add it to the list
    return three_chain_list

#Used by MM_dimension method to generate a list of the four chains in the DAG  
#The four chain method builds on the output of the three chain method
#It is just neccessary to consider adding an extra node inbetween the 1st and 2nd nodes of the three chain - this will cover all 4 chains
#The method takes account of birthdays to ensure that 'has_path' isn't used when birthdays would make an edge between two nodes impossible
def four_chain(three_chain_list, DAG_TC): #Uses the transitive completion of the DAG to find all of the three chains in the DAG
    four_chain_list = []
    for three_chain in three_chain_list: #Iterates over every 3 chain
        [node1, node2, node3] = three_chain
        for node in DAG_TC.nodes(): #Iterates over every node in the DAG
            if dl.age_check(DAG_TC, node1, node): #If a node has birthdays between two of the nodes in the 3 chain, it could be possible to find a 4 chain that has this node added in to the 3 chain
                if dl.age_check(DAG_TC, node, node2): 
                    if nx.has_path(DAG_TC, node1, node):
                        if nx.has_path(DAG_TC, node, node2): 
                            four_chain_list.append([node1, node, node2, node3]) #If a three chain can be formed, add it to the list
    return four_chain_list

#Used by MM_dimension method to generate a list of the three chains in the DAG  
#The five chain method builds on the output of the four chain method
#It is just neccessary to consider adding an extra node inbetween the 1st and 2nd nodes of the four chain - this will cover all 5 chains
#The method takes account of birthdays to ensure that 'has_path' isn't used when birthdays would make an edge between two nodes impossible    
def five_chain(four_chain_list, DAG_TC): #Uses the transitive completion of the DAG to find all of the three chains in the DAG
    five_chain_list = []
    for four_chain in four_chain_list: #Iterates over every 3 chain
        [node1, node2, node3, node4] = four_chain
        for node in DAG_TC.nodes(): #Iterates over every node in the DAG
            if dl.age_check(DAG_TC, node1, node): #If a node has birthdays between two of the nodes in the 3 chain, it could be possible to find a 4 chain that has this node added in to the 3 chain
                if dl.age_check(DAG_TC, node, node2): 
                    if nx.has_path(DAG_TC, node1, node):
                        if nx.has_path(DAG_TC, node, node2): 
                            five_chain_list.append([node1, node, node2, node3, node4]) #If a three chain can be formed, add it to the list
    return five_chain_list

###################################################################################
# MM_data
#
# Arguments: 
#
# Returns:
#
# Functions prints files containing values of a function of the dimension to the folder from which the command is run
# These values are referenced by the MM_dimension method when comparing values for the k_chain/(number_nodes^k) ratio
# with the appropriate value of the function of d, allowing d to be extracted
#            
###################################################################################
    
#Found in the Reid, mainifold dimension paper
def MM_data():    
    j = 2 # initalise the value of k
    while j<=5: #create dimension values for 2-chains, 3-chains, ...
        print j
        file = './f_of_d_%d.txt' %j     #create outfile titled to indicate that this is f(d) for 'k' chains
        file_thing = open(file, 'w')
        
        i = 0.0 #initialise the value of dimension
        while i<20.0: #output the value of f(d) for all dimensions up to this value
            i += 0.0001
            ff = f(i, j)
            file_thing.write(str(i) + '\t' + str(ff) + '\n')
        j += 1

#Used by MM_data to generate values for the function of dimension for any dimension value        
def f(d, k):
    delta = float(0.5*(d)) #make correction to Reid's fomula, which had delta = (d+1)/2
    top1 = float(math.gamma(delta)) * float(math.gamma(2*delta))
    top2 = math.gamma(2*delta + 1)
    top = top1 * (top2 ** (k-1))
    bottom = float((2**(k-1))) * float(k) * float(math.gamma(k * delta)) * float(math.gamma( (k+1)*delta ))
    return float((top/bottom))
                                
    