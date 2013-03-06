# midpoint scaling test
#james im running it tonight if you see this! dont bother running it.

#import midpoint_scaling as mps
import dag_lib as dl
import trans_red as tr
import time
#import midpoint_scaling as mps
import math
#import MM_dimension as mmd
#import Models2 as mod2

filename = './lp.txt.'
file = open(filename, 'w')

def test(N, D):
    
    box = dl.box_generator(N, D, 'minkowski')
    #box = mod2.COd(D,N)
    dag = box[0]
    ext = box[1]
    
    tr_dag = tr.trans_red(dag)
    good_dag = tr_dag
    lp = dl.lpd(tr_dag, ext[1], ext[0])
    length = lp[2]
    print length
    return length
    
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
   
#listy_n = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 160, 240, 320, 400, 500, 600]#, 480, 560, 640]
listy_n = range(200)

    
listy_d = [2,3,4,5,6,7,8]
#listy_d = [2]
#listy_d = [7,8,9,10]
numrun = 100.0
runs = range(int(numrun))
for thing_d in listy_d:
    for thing_n in listy_n:
        N = (thing_n+1)*5 #+1 to include 1000 nodes and exclude 0
        start_time = time.clock()
        lp_list = []
        for thing in runs:
             
            result = test(N, thing_d)
            lp_list.append(result)
        
        av_lp = sum(lp_list)/numrun
        var_lp = var(lp_list)
        lp_time = time.clock() - start_time
        print 'Time for %s size box of %s dimensions was %s' % (thing_n, thing_d, lp_time)  
        file.write(str(N) + '\t' + str(thing_d) + '\t' + str(av_lp) + '\t' + str(var_lp) + '\t' + str(lp_time) + '\n')
        print '\n \n \n'

      
