#lightcone_test.py
#Tests methods in the lightcone.py library

import lightcone as lc
import dag_lib as dl
import networkx as nx
import MM_dimension as MM
import midpoint_scaling as mp
import data_DAG as maker
import clustering as clus
from random import choice
import time
import re
import trans_red as tr
import lp_estimates as lpe

#Test properties of an interval in a DAG
def interval_test(DAG,start,end):
    
    lp = dl.lpd(DAG,start,end)
    length = lp[2]
    print 'The longest path between %s and %s is %d edges long' %(start,end,length)
    
    interval = lc.interval(DAG,start,end)
    N = interval.number_of_nodes()
    E = interval.number_of_edges()
    print 'The interval contains %d nodes and %d edges' %(N,E)
    
    c = clus.clustering(interval)
    print 'For the interval, c+ is %f, c0 is %f, c- is %f' %(c[0],c[1],c[2])
    
    #MMd = MM.MM_dimension(interval)
    MPSD = mp.mpsd(interval,lp[1])
    #print 'The MM dimension of the interval is %f and the MPSD is %f' %(MMd,MPSD)
    print 'The MPSD is %f' %MPSD[0]
    
def cone_axis_test(DAG,node,type,surface):
    
    #Make 'surface' list (i.e. list of nodes on the top or bottom 'surface' of the DAG)
    '''surface = []

    for line in surface_file:
        surface.append(line.strip())
    'There are %d nodes along the top' %len(surface)'''
        
    result = lc.cone_axis(DAG,node,type,surface)
    
    print 'The node(s) with the largest interval of %d are %s' %(result[1],str(result[0]))

def gip_test(DAG,apex,threshold,out):
    ax = open('./axis_%s.txt' %apex, 'r')
    
    
    ax_d = {}
    for line in ax:
        stuff = re.findall(r'\w+', line)
        ax_d[stuff[0]] = (stuff[1],stuff[2])
    
    gip = lpe.gip(DAG,apex,threshold,'forward')
    interval_gip = lpe.gip(DAG,apex,threshold,'forward',True)
    end = gip[-1]
    end_prop = ax_d[end]
    end_int = interval_gip[-1]
    end_int_prop = ax_d[end_int]
    
    out.write('Local' + '\t' + str(threshold) + '\t' + str(end) + '\t' + str(len(gip)) + '\t' + str(end_prop[0]) + '\t' + str(end_prop[1]) + '\n')
    out.write('Interval' + '\t' + str(threshold) + '\t' + str(end_int) + '\t' + str(len(interval_gip)) + '\t' + str(end_int_prop[0]) + '\t' + str(end_int_prop[1]) + '\n')

#def find_axis_test(DAG,apex,axis_node):
    
    
    
if __name__ == '__main__':    
    data = open('./Data/Cit-HepPh.DAT', 'r')
    no_in_file = open('./HepPh_no_in.txt', 'r')
    
    start_time = time.clock()
    print 'From function'
    DAG = maker.birthdayDAG(data,True)
    print DAG.number_of_nodes()

    print time.clock() - start_time
    
    no_in = []
    for line in no_in_file:
        no_in.append(line.strip())
    print 'Length no_in is %d' %len(no_in)

    #print lc.cone_axis(DAG,'9910429','forward',no_in)  

    start = '0302265'
    end = '9205221'
    
    '''lc.pythag_distance(DAG,start,end)
    
    start = '9706432'
    end = '9205221'''
    
    lp = dl.lpd(DAG,start,end)
    
    path = lp[1]
    
    start_2 = choice(DAG.nodes())
    #print start_2
    
    '''cone = lc.lightcone(DAG,start_2,'backward')
    print cone.number_of_nodes()
    print cone.number_of_edges()
    
    print 'Doing TC'
    tr.tc_recur(cone,start_2)
    
    print 'Done TC!'
    for node in cone:
        ancestors = cone.node[node]['ancestors']
        for ancestor in ancestors:
            cone.add_edge(node,ancestor)
            
    print cone.number_of_nodes()
    print cone.number_of_edges()'''
            
    
    '''print start_2
    print lc.cone_axis(DAG,start_2,'forward',no_in)'''
    
    '''lc.pythag_distance(DAG,start,end)
    
    start = '0206228'

    #interval_test(DAG,start,end)'''
    
    '''#test_node = ''
    apex = '0104056'
    axis_node = '0302265' #node that is in the middle of the end surface of the lightcone
    #th_list = [0,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,2500,3000,3500,4000,4500,5000,6000,7000,8000,9000,10000,11000,12000,13000,14000,15000,16000,17000,18000,19000,20000,21000,22000,23000,24000,25000]
    numruns = range(40)
    out = open('./gip_%s.txt' %apex, 'w')
 
    for run in numruns:
        th = run*100
        print th
        gip_test(DAG,apex,th,out)
    #cone_axis_test(DAG,start_2,'forward',no_in)
    
    axis = lc.time_axis(DAG,apex,'forward')
    axis_start = axis[0]
    axis_end = axis[-1]
    axis_len = len(axis)
    out.write('The time axis path started on %s, ended on %s and is %d nodes long' %(axis_start,axis_end,axis_len))'''
    
    '''intv = lc.interval(DAG,'0301110',start_2)
    print 'Interval size is %d' %len(intv)
    print dl.lpd(DAG,'0301110',start_2)[2]'''
    
    out = open('./Data/hist.txt', 'w')
    hist = lc.lightcone(DAG,'9205221','forward')
    for thing in hist:
        out.write(str(thing) + '\n')