# dag_lib_test
# just for testing out the dag_lib functions

import dag_lib as dl
import trans_red as tr
import time
'''
my_box = dl.box_generator(10, 2, 'minkowski')
dag = my_box[0]
ext = my_box[1]

print dag.nodes()
'''

def tr_test(N):
    box = dl.box_generator(N, 3)
    dag = box[0]
    ext = box[1]

    '''for node in dag.nodes():
        print '####################'
        print node
        print dag.successors(node)
        print '####################'''
    start_time = time.clock()
    tr_dag = tr.trans_red(dag)
    tr_time = time.clock() - start_time
    #print tr_dag.successors(ext[1])
    print 'N = %s and time for tr was %s' % (N, tr_time)
    


def lpd_test(N, D):
    box = dl.box_generator(N, D, 'minkowski')
    dag = box[0]
    ext = box[1]
    
    tr_dag = tr.trans_red(dag)
    
    print ext[0]
    print ext[1]
    
    
    lp = dl.lpd(tr_dag, ext[1], ext[0])
    print lp[1]
    return lp

lpd_test(1000, 3)

 
        



