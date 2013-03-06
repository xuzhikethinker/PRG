# interval_test.py
# provides access to all the functions which tell you things about a pair of papers & the interval between them

from dag_lib import lpd as LP
from MM_dimension import MM_dimension as MMD
from midpoint_scaling import mpsd as MPSD
from lightcone import interval

def DT(DAG, node1, node2):
    # Returns time difference in ~months
    # (DAG, new_paper, old_paper) will return a positive number
    bday1 = DAG.node[node1]['birthday']
    bday2 = DAG.node[node2]['birthday']
    int_dt = bday1 - bday2
    float_dt = float(int_dt)
    dt = float_dt/1000 # because birthdays are in months*1000 - see data_dag.py
    return dt
    
def DR(DAG, node1, node2):
    # Returns rank difference
    # (DAG, new_paper, old_paper) will return a positive number
    rank1 = DAG.node[node1]['rank']
    rank2 = DAG.node[node2]['rank']
    dr = rank1 - rank2
    return dr
    
def interval_test(DAG, new, old):
    # new and old are the names of the 2 papers which form the interval
    # Find Interval
    interval_DAG = interval(DAG, new, old)
    interval_list = interval_DAG.nodes()
    inerval_size = len(interval_list)
    if interval_size < 1:
        print 'Interval is empty'
    print 'Interval size is %s' % str(interval_size)
        
    # Find LP
    lp = LP(interval_DAG, new, old)
    lp_len = len(lp)
    print 'Longest Path is %s' % str(lp_len)
    
    # Find DT
    dt = DT(interval_DAG, new, old)
    print 'Delta T is %s' % str(dt)
    
    # Find DR
    dr = DR(interval_DAG, new, old)
    print 'Delta Rank is %s' % str(dr)
    
    # Find MPSD
    mpsd = MPSD(interval_DAG, lp)
    print 'Midpoint Scaling Dimension is %s' % str(mpsd)
    
    # Find MMD
    mmd = MMD(interval_DAG)
    print 'Myrnheim Meyer Dimension is %s' % str(mmd)
    
if __name__ == '__main__':
    inteval_test(##########)
    