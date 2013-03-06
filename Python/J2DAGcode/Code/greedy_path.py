# greedy_path
# needs work

import dag_lib as dl
import time

# From a starting node, forms a path by repeatedly choosing the edge to the node which is nearest in the natural ordering (ie. birthday)
# This requires the DAG to have a 'birthday' attribute representing the natural ordering
def greedy_path(DAG, start, max_depth=-1, end_node='', path=[]):
    path.append(start)
    if len(path) == max_depth:
        print 'We have reached the max depth'
        print 'Max Depth = %s' % max_depth
        print 'Path length = %s' % str(len(path))
        return path
    if start == end_node:
        return path
    successors = DAG.successors(start)
    ordered_successors = order_successors(DAG, successors)
    next_node = None
    for node in ordered_successors:   
        if len(DAG.successors(node)) > 0:
            next_node = node
            break
    if next_node:   
        path = greedy_path(DAG, next_node, max_depth, end_node, path)
    else:
        print 'REACHED THE END'
        path.append(ordered_successors[0])
        return path
    return path
    
def order_successors(DAG, successors):
    # Orders a node's successors from highest birthday to lowest birthday
    # We would expect that differences between birthdays on the longest path are small, so we want to label nodes where the birthday gap is small first
    # This should reduce the amount of relabeling that occurs
    successor_birthday_tuples = []
    ordered_successor_list = []
    for node in successors:
        birthday_of_node = DAG.node[node]['birthday'] #finds 'birthday' attribute of each successor
        successor_birthday_tuples.append((birthday_of_node, node)) #adds a tuple to the 'successor_birthday_tuples' list of the form (birthday, node name)
    sorted_successor_tuples = sorted(successor_birthday_tuples, reverse=True) #sorts the 'successor_birthday_tuples' list from largest to smallest birthday
    for tuple in sorted_successor_tuples:
        ordered_successor_list.append(tuple[1]) #creates a list from the ordered (birthday, node name) list just of the node names
    return ordered_successor_list #return a list of the nodes, reverse ordered by birthday         
        
        
if __name__ == '__main__':
    def average(list):
        summ = sum(list)
        lenn = len(list)
        av = summ/lenn
        return av 
    def test(N, D):
        box = dl.box_generator(N, D)
        DAG = box[0]
        ext = box[1] 

        start = ext[1]
        end = ext[0]

        print DAG.node[start]['birthday']
        print '!!!!!'
        start_time = time.clock()
        the_greedy_path = greedy_path(DAG, start, max_depth=50)
        greedy_time = time.clock() - start_time
        start_time = time.clock()
        lp = dl.lpd(DAG, start, end)
        lp_time = time.clock() - start_time
        print 'Greedy'
        greedy_path_length = len(the_greedy_path)
        #print the_greedy_path    
        
        print 'Longest'
        lp_length = lp[2]
        #print lp[1]
        
        print 'Greedy Time = %s' % str(greedy_time)
        print 'LP Time = %s' % str(lp_time)
        
        return (greedy_path_length, greedy_time, lp_length, lp_time)
       
    gpl = []
    gpt = []
    lpl = []    
    lpt = []
    for thing in range(50):
        print thing
        array = test(5000, 3)
        gpl.append(array[0])
        gpt.append(array[1])
        lpl.append(array[2])
        lpt.append(array[3])
        

        
    print 'Greedy Path length = %s' % str(average(gpl))
    print 'Greedy Path time = %s' % str(average(gpt))
    print 'Longest Path length = %s' % str(average(lpl))
    print 'Longest Path time = %s' % str(average(lpt))
        
        
        
    
    