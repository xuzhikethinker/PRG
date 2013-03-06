import models
import dag_lib as dl
import trans_red as tr
import matplotlib.pyplot as plt
import networkx as nx
import numpy as np
import time

start_time = time.time()

model = models.COd(2, 200)

DAG = model[0]
extremes = model[1]
print 'made model'
tr_DAG = tr.trans_red(DAG)
print 'done transitive reduction'
lp = dl.lpd(tr_DAG, extremes[1], extremes[0])
length_lp = lp[2]
path = lp[1]

print 'calculated lp'
t_list = []
x_list = []
path_t_list = []
path_x_list = []
#
for node in range(len(DAG.nodes())):
    t_list.append(DAG.nodes()[node][0])
    x_list.append(DAG.nodes()[node][1])
#    
#for node in range(len(path)):
#    path_t_list.append(path[node][0])
#    path_x_list.append(path[node][1])

params = {'figure.figsize': [6,6]}
plt.rcParams.update(params)

plt.xlim(-1,+1.1)
plt.xlabel(r'$x$')
plt.ylabel(r'$t$')
plt.ylim(-1,+1.1)
plt.scatter(x=x_list, y=t_list,  s=30, marker = '+', color = 'k')
#plt.scatter(x=path_x_list, y=path_t_list,  s=1, marker = '+', color = 'red')

print 'plotted all points, awaiting longest path coloring'

lp_list = []
j=0

paths_list = list(nx.all_simple_paths(tr_DAG, extremes[1], extremes[0], cutoff=length_lp+1))
#paths_list = dl.find_all_paths(tr_DAG, extremes[1], extremes[0])
print 'made list of all simple paths'
for i in range(len(paths_list)):
    if len(paths_list[i])==length_lp+1:
        j+=1
        print j
        path_t_list = []
        path_x_list = []

        for node in range(len(paths_list[i])):
            path_t_list.append(paths_list[i][node][0])
            path_x_list.append(paths_list[i][node][1])
        plt.scatter(x=path_x_list, y=path_t_list,  s=20, marker = 'x', color = 'red', alpha=0.1)
print 'found all longest paths. Awaiting plotting...'   
plt.savefig('lp_picture.pdf')
plt.show()
elapsed = time.time() - start_time

print 'finished. Time elapsed = ',elapsed