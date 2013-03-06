import clustering as clus
import networkx as nx
import re
import dag_lib as dl
from random import choice
import time
import math
import data_DAG as maker

#data = open('./Data/TestNet.DAT', 'r')
out = open('./clustering.txt', 'w')

'''DAG = nx.DiGraph()
for line in data:
    nodes = re.findall(r'\w+', line)
    DAG.add_edge(nodes[0],nodes[1])

list_D = [2,3,4,5,6]
list_N = [25,50,75,100,125,150,175,200,225,250,275,300,325,350,375,400,425,450,475,500]
numrun = 25.
runs = range(int(numrun))
out.write('D' + '\t' + 'N' + '\t' + 'av_plus' + '\t'  + 'av_minus' + '\t' + 'ratio' + '\n')
for D in list_D:
    for N in list_N:
        plus_list = []
        minus_list = []
        for thing in runs:
            DAG = dl.box_generator(N,D)[0]                       
            result = clus.clustering(DAG)
            plus_list.append(result[0])
            minus_list.append(result[1])
        
        av_plus = sum(plus_list)/numrun
        av_minus = sum(minus_list)/numrun
        ratio = av_plus/av_minus
        out.write(str(D) + '\t' + str(N) + '\t' + str(av_plus) + '\t'  + str(av_minus) + '\t' + str(ratio) + '\n')
        print 'Finished %d nodes in %d dimensions' %(N,D)

#for node in DAG.nodes():
#    out.write('\n' + str(node) + '\t' + str(DAG.node[node]['clustering_plus']))'''

#DAG = dl.box_generator(100, 2)[0]
arxiv = open('./Data/Cit-HepTh.DAT', 'r')
DAG = maker.birthdayDAG(arxiv)
result = clus.calculate_2c(DAG, '++')
print result