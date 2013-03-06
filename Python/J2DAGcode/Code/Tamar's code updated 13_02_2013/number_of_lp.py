'''
Created on Dec 29, 2012

@author: TamarLoach
'''

import models
import dag_lib as dl
import trans_red as tr
import matplotlib.pyplot as plt
import networkx as nx
import math
import time
import statistics as stats

def number_of_lp():
 
    for D in [2,3,4]:   
        myfile = open(str(D)+'how many longest paths?', 'w')
        myfile.write('N' + '\t' + 'L_scale=N^1/D'+ '\t'+ 'average lp length'+ '\t'+'std err'+  '\t'+ 'average no. lp'+ '\t'+'std err' +'\n')
        start_time = time.time()
        trials = 100
        for N in range(2,40,1):
            lp_length_list = []
            j_list = []
            for _ in range(trials):
                model = models.COd(2, N)
#                model = models.barabasi_albert_graph(n, m, seed)
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
            
            lp_stats = stats.list_stats(lp_length_list)
            lp_av = lp_stats[3]
            lp_err = lp_stats[1]
            
            j_stats = stats.list_stats(j_list)
            j_av = j_stats[3]
            j_err = j_stats[1]
            
            l_scale = (N**(1.0/float(D)))
        
            myfile.write(str(N) + '\t' + str(l_scale) +'\t' + str(lp_av)+'\t' + str(lp_err)+ '\t' + str(j_av) +'\t' +  str(j_err)+ '\n')
            print 'done', N
        elapsed = time.time() - start_time
        
        print 'finished.',D,'Dimension. Time elapsed = ',elapsed
        
    return


def number_of_lp_lattice():
 
    for D in [2]:   
        myfile = open(str(D)+'number_lp_lattice', 'w')
        myfile.write('N_side' + '\t' + 'N'+ '\t'+ 'lp length' +  '\t'+ 'no. lp'+ '\n')
        start_time = time.time()
        for N_side in range(10,16,1):
            N = N_side**2

    #        model = models.COd(2, N)
            model = models.square_lattice_model(D, N_side)
            DAG = model[0]
            extremes = model[1]
            tr_DAG = tr.trans_red(DAG)
            lp = dl.lpd(tr_DAG, extremes[1], extremes[0])
            length_lp = lp[2]
            
            j=0
            paths_list = list(nx.all_simple_paths(tr_DAG, extremes[1], extremes[0], cutoff=length_lp+1))
            for i in range(len(paths_list)):
                if len(paths_list[i])==length_lp+1:
                    j+=1
    
            myfile.write(str(N_side) + '\t' + str(N) +'\t' + str(length_lp)+ '\t' + str(j) + '\n')
            print 'done', N_side
        elapsed = time.time() - start_time
        
        print 'finished.',D,'Dimension. Time elapsed = ',elapsed
        
    return

def number_of_lp_BA(m_range=[1,5,10,50], n_max=50, n_step=5):
 
    for m in m_range:  
        n_range=range(m+1,n_max,n_step) 
#        n_range=range(101,201,10)
        myfile = open(str(m)+'C_for_BA', 'w')
        myfile.write('n' + '\t' + 'lp_av'+'\t' + 'lp_err'+ '\t' + 'no._av' +'\t' +  'no._err'+ '\n')
        start_time = time.time()
        trials = 1000
        for n in n_range:
            lp_length_list = []
            j_list = []
            for _ in range(trials):
        #        model = models.COd(2, N)
                model = models.barabasi_albert_graph(n, m)
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
            
            print j_list
            
            lp_stats = stats.list_stats(lp_length_list)
            lp_av = lp_stats[3]
            lp_err = lp_stats[1]
        
            j_stats = stats.list_stats(j_list)
            j_av = j_stats[3]
            j_err = j_stats[1]
            print j_av
            print j_err
        
            myfile.write(str(n) + '\t' + str(lp_av)+'\t' + str(lp_err)+ '\t' + str(j_av) +'\t' +  str(j_err)+ '\n')
            print 'done', n
        elapsed = time.time() - start_time
        
        print 'finished.',m,'m. Time elapsed = ',elapsed
        
    return


#number_of_lp_lattice()

number_of_lp_BA([10],200,10)

