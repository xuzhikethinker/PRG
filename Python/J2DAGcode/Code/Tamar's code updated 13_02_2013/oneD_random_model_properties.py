'''
Created on Jan 25, 2013

@author: TamarLoach
'''

import models
import numpy as np
import networkx as nx

def oneD_random_comparison_by_degree(N=10,average_in_degree=1):
    """
    This is the oneD box with probability of edge presence p, but we set average_in_degree instead of p.
    This calculation could be wrong, but I'm pretty confident! 
    """
    p = (float(N)* float(average_in_degree))/float(sum(range((N-1),0,-1)))
    oneD_box = models.box_model(1, N, p)
    return oneD_box

def oneD_random_probabilistic(N,p):
    oneD_box = models.box_model(1, N, p)
    return oneD_box


def random_model_percentage_extremes_connected(N,number_of_trials,output_filename = "./connected_extremes.txt"):
    '''Look for critical point in connectivity of comparison model. 
    Is there phase transition-like behavior as the model extremes go from unconnected to connected (on average)?
    run comparison model over different probabilities of connection and output whether extremes are connected.
    This method takes N=number of nodes and number_of_trials=what to average over, and returns the percentage of models over all trials that have a path between extremes '''
    
    myfile = open(output_filename, 'w')
    myfile.write('N=' + str(N) + 'trials=' + str(number_of_trials) + '\n')
    myfile.write("average in degree"+ '\t' + 'number of trials resulting in connected extremes' + '\t' + 'no. of trials -> disconnected' +'\t'+ 'percentage of trials -> connected' +'\n')
    
    for average_in_degree in np.arange(0,10.01,0.1):
        connected_count = 0
        disconnected_count = 0
        for _ in range(number_of_trials):
            model = oneD_random_comparison_by_degree(N=N,average_in_degree=average_in_degree)
            G=model[0]
            extremes = model[1]
            if nx.has_path(G, extremes[1], extremes[0]):  
    #            print "extremes connected!"
                connected_count += 1
            else: 
    #            print "not connected"
                disconnected_count +=1
        print "done average in degree = ", average_in_degree
        percentage_connected = 100.0*(float(connected_count)/(float(connected_count+disconnected_count)))        
        myfile.write(str(average_in_degree)+ '\t' + str(connected_count) + '\t' + str(disconnected_count) +'\t'+ str(percentage_connected) +'\n')
    
    return

