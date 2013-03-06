#indecisive_path.py
#Finds a path through the DAG by moving connected nodes of the highest degree
#Method can be used from either end of the path


from random import choice
import networkx as nx
import data_DAG as maker
import dag_lib as dl


data = open('./Data/Cit-HepPh.DAT', 'r')
DAG = maker.birthdayDAG(data)



def indecisive_path(DAG,node,type,path=[]):
    path.append(node)
    print 'Current Node is: %s' %str(node)
    #Change type to change whether relations are found from bottom (of DAG) up or from the top down
    if type == 'start':
        things = DAG.successors(node)
    elif type == 'end':
        things = DAG.predecessors(node)
    else:
        print 'Error: type given in indecisive_path not recognised'
        
    print 'The things are: %s' %str(things)
    
    if len(things) == 0: #there are no preds/succs to consider so the end of the path has been reached
        print 'Path found!'
        return path
    
    else: #i.e. the end of the path has not been reached...there are still succs/preds to choose from
        print 'Things has length!'
        k_max = 0 #Maximum degree (in for 'end' or out for 'start') found
        i_max = [] #index in list of node with maximum degree
        i = 0 #counter for the current index
        
        #Look through each of the preds/succs and find which of them has the highest degree (in or out)
        for thing in things:
            
            if type == 'start':
                k = DAG.out_degree(thing)
            elif type == 'end':
                k = DAG.in_degree(thing)
            print 'Current thing is %s and it has a k of %d' %(str(thing),k)
            
            if k > k_max:
                k_max = k
                i_max = [i]
                print 'Change kmax at %s to %d' %(thing,k)
            elif k == k_max:
                if k_max > 0:
                    i_max.append(i)
                    print 'Add %s to list' %thing
            i += 1
                    
        if len(i_max) == 1: #there is a single node that has the maximum degree value
            i_path = i_max[0]
        elif len(i_max) > 1: #there is more than one node with the same (non-zero) degree value
            i_path = choice(i_max)
            print 'multiple choice, choose from %s' %str(i_max)
        else: #i.e. i_max is an empty list...all node have k=0 i.e. the end of the path will be reached on the next step to a random one of 'things'
            i_path = choice(range(len(things)))

        next_step = things[i_path]    #This is the next step that the path with take        
                
        path = indecisive_path(DAG,next_step,type,path)
        
        return path

full_box = dl.box_generator(100, 2)
ext = full_box[1]
path = indecisive_path(full_box[0],ext[1],'start')
print len(path)
print path
    

