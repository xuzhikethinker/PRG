'''
Created on Dec 29, 2012

@author: TamarLoach
'''
#import models
#import dag_lib as dl
#import MM_dimension as mm
#import midpoint_scaling as ms
#import trans_red as tr
#import networkx as nx
#import numpy as np
#import time
#import math
#import matplotlib.pyplot as plt
#
##def stats(input_list):
##    list_sum = sum(input_list)
##    list_average = list_sum/(float(len(input_list))) 
##    square_diff = 0.0
##    for value in input_list:
##        diff = value - list_average
##        diff_squared = diff**2
##        square_diff += diff_squared
##    std_dev = math.sqrt(square_diff/(float(len(input_list))-1.0))
##    std_error = std_dev/math.sqrt(float(len(input_list)))
##    return[std_error, std_dev, list_average]  
##    
##input_list = [9,2,1]
##
##stats = stats(input_list)
##sum_of = stats[1]
##av = stats[2]
##error = stats[0]
##print sum_of, av, error
##
##import matplotlib.pyplot as plt
##import numpy as np
##
##x = np.arange(10)
##
##fig=plt.figure()
##
##
##for i in xrange(5):
##    plt.plot(x, i * x, label='$y = %ix$' % i)
##
##leg=plt.legend()
##
##
### define the matplotlib.patches.Rectangle instance surrounding the legend
##frame  = leg.get_frame()  
##frame.set_facecolor('0.99')
##frame.set_edgecolor('0.99')
##
##plt.show()
#
##import networkx as nx
##import matplotlib.pyplot as plt
##
##G=nx.random_geometric_graph(200,0.125)
### position is stored as node attribute data for random_geometric_graph
##pos=nx.get_node_attributes(G,'pos')
##
### find node near center (0.5,0.5)
##dmin=1
##ncenter=0
##for n in pos:
##    x,y=pos[n]
##    d=(x-0.5)**2+(y-0.5)**2
##    if d<dmin:
##        ncenter=n
##        dmin=d
##
### color by path length from node near center
##p=nx.single_source_shortest_path_length(G,ncenter)
##
##plt.figure(figsize=(8,8))
##nx.draw_networkx_edges(G,pos,nodelist=[ncenter],alpha=1, width = 0.03)
##nx.draw_networkx_nodes(G,pos,nodelist=p.keys(),
##                       node_size=80,
##                       node_color=p.values(),
##                       cmap=plt.cm.Reds_r)
##
##plt.xlim(-0.05,1.05)
##plt.ylim(-0.05,1.05)
##
##plt.savefig('random_geometric_graph.pdf')
##plt.show()
##
##When I alter the line width used for edges by specifiying the width argument in the networks draw_networkx() function, e.g:
##
##    nx.draw_networkx(G, width = 0.03)
##
##the image that results from using 
##
##    plt.show()
##
##is as expected (in that I can control the edge widths). However, when I use:
##
##    plt.savefig('foo.pdf')
##
##the resulting pdf appears to have an unaffected edge width. Saving as png seems to work fine, but I really need a pdf output. I slightly altered a sample code from the networkx website as an example:
#
#import matplotlib.pyplot as plt
#
#params = {
#      'figure.figsize': [2,2],
#     }
#plt.rcParams.update(params)
#
#formula = r'$x=\frac{3}{100}$'
#
#fig = plt.figure()
#fig.text(0.5,0.5,formula)
#
#plt.savefig('formula.png')


import models
import dag_lib as dl
import trans_red as tr
import matplotlib.pyplot as plt
import networkx as nx
import math
import time

def stats(input_list):
    list_sum = sum(input_list)
    list_average = list_sum/(float(len(input_list))) 
    square_diff = 0.0
    for value in input_list:
        diff = value - list_average
        diff_squared = diff**2
        square_diff += diff_squared
    std_dev = math.sqrt(square_diff/(float(len(input_list))-1.0))
    std_error = std_dev/math.sqrt(float(len(input_list)))
    return[std_error, std_dev, list_sum, list_average] 


 
for D in [1,2,3,4,10]:   
    myfile = open(str(D)+'how many longest paths?', 'w')
    myfile.write('N' + '\t' + 'L_scale=N^1/D'+ '\t'+ 'average lp length'+ '\t'+'std err'+  '\t'+ 'average no. lp'+ '\t'+'std err' +'\n')
    start_time = time.time()
    trials = 1000
    N_list = []
    for N in range(0,105,5):
        lp_length_list = []
        j_list = []
        for trial in range(trials):
    #        model = models.COd(2, N)
            model = models.box_model(D, N)
            DAG = model[0]
            extremes = model[1]
            tr_DAG = tr.trans_red(DAG)
            lp = dl.lpd(tr_DAG, extremes[1], extremes[0])
            length_lp = lp[2]
            lp_length_list.append(length_lp)
            
            j=0
            paths_list = list(nx.all_simple_paths(tr_DAG, extremes[1], extremes[0], cutoff=length_lp+1))
            for i in range(len(paths_list)):
                if len(paths_list[i])==length_lp+1:
                    j+=1
                    
            j_list.append(j)
        
        lp_stats = stats(lp_length_list)
        lp_av = lp_stats[3]
        lp_err = lp_stats[1]
        
        j_stats = stats(j_list)
        j_av = j_stats[3]
        j_err = lp_stats[1]
        
        l_scale = (N**(1.0/float(D)))
    
        myfile.write(str(N) + '\t' + str(l_scale) +'\t' + str(lp_av)+'\t' + str(lp_err)+ '\t' + str(j_av) +'\t' +  str(j_err)+ '\n')
        print 'done', N
    elapsed = time.time() - start_time
    
    print 'finished.',D,'Dimension. Time elapsed = ',elapsed



