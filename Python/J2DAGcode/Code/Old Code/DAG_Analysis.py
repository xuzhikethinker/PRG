# Function to create a D dimensional, N point box causal set.

# Naming convention - Young means near to the start of the DAG
#                     Old means near to the end of the DAG
#


# To do: change the names of the nodes to that of a natural ordering
#        to test out the find_all_paths_age function which should
#        use the natural ordering to optimise the path finding


import networkx as nx
import longest_path as lp
import random
import math
import time
import operator
import DAG_from_arxiv as arXiv
import DAG_from_box as boxmaker
#import matplotlib.pyplot as plt
from random import choice #imports a function to select a list entry

N = 3000
D = 2
num_boxes = 1

outfile = 'C:\Users\Jamie\Project\out.txt'
#outfile = './out2.txt'
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

def box_analysis(N, D, num_boxes, out, count, analysis):

    boxes = []
    edges = []
    nodes = []
    extremes = []
    
    for i in range(num_boxes):
        full_box = boxmaker.box_generator_corners(N, D) #full box is a list with first entry the new boxDAG and second entry a list containg the two extremal points
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
    i = 0
    for box in boxes:
        extremal_points = extremes[i] #associates the right extremal values with the right box
        n = choice(box.nodes()) #selects a random node from the box

        #Define the points that mark the start and the end of the longest path
        #Such that:
        # path_ends[0] = start of the path, has a higher 'birthday' value (i.e. closer to the present)
        # path_ends[1] = end of the path, has a lower 'birthday value (i.e. further in the past)
        path_ends = [extremal_points[0], n]
        
        out.write('\n' + 'The start node is %s and the end node is %s' %(path_ends[0], path_ends[1]))
        
        # Do J^2 testing
        #start_time = time.clock()
        #jsquared_algorithm(box, path_ends, out, count)
        #jsquared_time = time.clock() - start_time
        #print 'J^2 time = %s' % jsquared_time
        
        # Do age testing
        #start_time = time.clock()
        #lp.jsquared_with_birthdays(box, path_ends, out, count)
        #jsquared_with_birthdays_time = time.clock() - start_time
        #print 'J^2 with birthdays time = %s' % jsquared_with_birthdays_time
        
        # Do Tim algorithm
        #start_time = time.clock()
        #lp.tim_algorithm(box, path_ends, out, count)
        #tim_time = time.clock() - start_time
        #print 'Tim time = %s' % tim_time
        
        
        # Do Tim algorithm with order
        start_time = time.clock()
        lp.tim_algorithm_with_ordered_search(box, path_ends, out, count)
        tim_with_order_time = time.clock() - start_time
        print 'Tim with order time = %s' % tim_with_order_time
        
        # Do Tim algorithm with birthday
        start_time = time.clock()
        lp.tim_algorithm_with_ordered_search_and_birthdays(box, path_ends, out, count)
        tim_with_birthdays_time = time.clock() - start_time
        print 'Tim with birthday time = %s' % tim_with_birthdays_time
        
        analysis.write(str(box.number_of_edges()) + '\n')
        #analysis.write(str(N) + '\t' + str(tim_time) + '\t' + str(tim_with_birthdays_time) + '\n')
        
        i += 1 #iterates the box counter

def data_analysis(data, out, count, analysis):

    
    DAG = arXiv.arXivDAG(data) #full box is a list with first entry the new boxDAG and second entry a list containg the two extremal points

    path_ends = ['0302182', '0012251']
        
    out.write('\n' + 'The start node is %s and the end node is %s' %(path_ends[0], path_ends[1]))
        
    # Do J^2 testing
    #start_time = time.clock()
    #jsquared_algorithm(DAG, path_ends, out, count)
    #jsquared_time = time.clock() - start_time
    #print 'J^2 time = %s' % jsquared_time
    
    # Do J^2 testing with birthday
    #start_time = time.clock()
    #lp.jsquared_with_birthdays(DAG, path_ends, out, count)
    #jsquared_with_birthdays_time = time.clock() - start_time
    #print 'J^2 with birthdays time = %s' % jsquared_with_birthdays_time
        
    # Do Tim algorithm
    #start_time = time.clock()
    #lp.tim_algorithm(DAG, path_ends, out, count)
    #tim_time = time.clock() - start_time
    #print 'Tim time = %s' % tim_time
    
    
    # Do Tim algorithm with order
    start_time = time.clock()
    lp.tim_algorithm_with_ordered_search(DAG, path_ends, out, count)
    tim_with_order_time = time.clock() - start_time
    print 'Tim with order time = %s' % tim_with_order_time
    
    # Do Tim algorithm with order and birthday
    start_time = time.clock()
    lp.tim_algorithm_with_ordered_search_and_birthdays(DAG, path_ends, out, count)
    tim_with_birthdays_time = time.clock() - start_time
    print 'Tim with birthdays time = %s' % tim_with_birthdays_time
    
    analysis.write(str(DAG.number_of_edges()) + '\n')
    #analysis.write(str(N) + '\t' + str(tim_time) + '\t' + str(tim_with_birthdays_time) + '\n')
                               
    

        
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
    data = open('C:\Users\Jamie\Project\CitNet.DAT', 'r') #input data
    analysis = open(analysisfile, 'w') #analysis file
    
    
    #n_list = [10, 20, 30, 40, 50, 75, 100, 200, 300, 400, 500]
    #n_list = [100]
    #for n in n_list:
    #   box_analysis(n, D, num_boxes, out, count, analysis)
    
    data_analysis(data, out, count, analysis)  

    
    # Read out the numbers from count
    #counts = read_count(countfile)
    #print '##########################'
    #for count in counts:
    #    print count
        