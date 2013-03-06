import networkx as nx
import random
import math
from math import exp
import time
import data_DAG as maker
import dag_lib as dl
import trans_red as TR
import MM_dimension as MM


print 'Making DAG'
out = open('./MMArxivTH.txt', 'w')
ar = open('./Data/Cit-HepPh.DAT', 'r')
DAG = maker.birthdayDAG(ar)
print 'Made DAG'
print 'Finding MM dimension'

MMdim = MM.MM_dimension(DAG)
print MMdim
out.write(MMdim)



'''for N in listN:
    for D in listD:
        
        #DAG = (dl.box_generator(4000,D,'box'))[0]
        DAG = (mod.COd(D, N))[0]
        start_time = time.clock()
        d = MM.MM_dimension(DAG,2)
        t = time.clock()-start_time
        out.write(str(D) + '\t' + str(N) + '\t' + str(d) + '\t' + str(t) + '\n')'''
'''        
for N in listN:
    DAG = (dl.box_generator(N,3,'box'))[0]
    print MM.MM_dimension(DAG)     

t = 0.'''
'''while t<10:
    m = 22000000
    p = 0.02
    q = 0.9
    k = 0
    y = (m/p)*((p+q)**2)*exp(-(p+q)*t)/(1.+(q/p)*exp(-(p+q)*t))+k*m*(1.-(p+q)/(p*exp((p+q)*t)+q))
    out.write(str(t) + '\t' + str(y) + '\n')
    t += 0.001'''



'''big_time = time.clock()
for i in range(25):
    for D in listD:
        for N in listN:
            DAG = (dl.box_generator(N, D, 'box'))[0]
            for k in listk:
                start_time = time.clock()        
                d = MM.MM_dimension(DAG, k)
                t = time.clock() - start_time
                
                out.write(str(D) + '\t' + str(N) + '\t' + str(k) + '\t' + str(d) + '\t' + str(t) + '\n')
    print D
    print i
print 'Overall, this whole mess took %f seconds to complete' %(time.clock() - big_time)'''