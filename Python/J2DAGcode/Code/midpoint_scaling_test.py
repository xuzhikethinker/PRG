# midpoint scaling test

import midpoint_scaling as mps
import dag_lib as dl
import trans_red as tr
import time
import midpoint_scaling as mps
import math
import MM_dimension as mmd

filename = './midpoint.txt.'
file = open(filename, 'w')
mmd.MM_data()
def test(N, D):
    
    box = dl.box_generator(N, D)
    dag = box[0]
    ext = box[1]
    tr_dag = tr.trans_red(dag)
    good_dag = tr_dag
    lp = dl.lpd(tr_dag, ext[1], ext[0])
    path = lp[1]
    tr_dag = good_dag
    dim = mps.mpsd(tr_dag, path)
    mm_dim = mmd.MM_dimension(dag)
    print dim
    print mm_dim
    return [dim[0], mm_dim]
    
def var(list):
    average = sum(list)//(len(list))
    sq_diff = 0.0
    for thing in list:
        diff = thing - average
        diff2 = diff*diff
        sq_diff += diff2
    sq_diff = sq_diff/(len(list))
    diff = math.sqrt(sq_diff)    
    return diff    
   
#listy_n = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 160, 240, 320, 400, 480, 560, 640]
listy_n = range(5, 200)

    
listy_d = [2,3,4,5,6]
numrun = 15.0
runs = range(int(numrun))
for thing_d in listy_d:
    for thing_n in listy_n:
        start_time = time.clock()
        dim_list = []
        mmdim_list = []
        for thing in runs:
             
            result = test(thing_n, thing_d)
            dim_list.append(result[0])
            mmdim_list.append(result[1])
        
        av_dim = sum(dim_list)/numrun
        var_dim = var(dim_list)
        av_dim_mm = sum(mmdim_list)/numrun
        var_dim_mm = var(mmdim_list)
        tr_time = time.clock() - start_time
        print 'Time for %s size box of %s dimensions was %s' % (thing_n, thing_d, tr_time)  
        file.write(str(thing_n) + '\t' + str(thing_d) + '\t' + str(av_dim) + '\t' + str(var_dim) + '\t' + str(av_dim_mm) + '\t' + str(var_dim_mm) + '\t' + str(tr_time) + '\n')
        print '\n \n \n'

      
