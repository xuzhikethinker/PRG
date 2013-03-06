# Indecisive path
# Chooses the successor with the most children

import dag_lib as dl
import time

def indecisive_path(DAG, start, max_depth=-1, end_node='', path=[]):
    path.append(start)
    if len(path) == max_depth:
        print 'We have reached the max depth'
        print 'Max Depth = %s' % max_depth
        print 'Path length = %s' % str(len(path))
        return path
    if start == end_node:
        return path
    successors = DAG.successors(start)
    succ_list = []
    for node in successors:
        try:
            num_succ = len(DAG.successors(node))
        except:
            num_succ = 0
        
        succ_list.append(num_succ)
    if max(succ_list) == 0:
        # We are at a dead end
        path.append(succ_list[0])
        return path        
        
    new_node = succ_list.index(max(succ_list))
    path = indecisive_path(DAG, node, max_depth, end_node, path)
    return path
    
if __name__ == '__main__':
    def test(N, D):
        box = dl.box_generator(N, D)
        DAG = box[0]
        ext = box[1] 

        start = ext[1]
        end = ext[0]
        
        the_indec_path = indecisive_path(DAG, start, -1, end)
        print len(the_indec_path)
        
    test(1000, 2)    