#lightcone.py
#functions for finding 'lightcone' of an academic paper and of trying to extract a 'spatial' dimension in citation space

import networkx as nx
import numpy
import math
import dag_lib as dl
from random import choice


###########################################################################################################################################
# interval
#
# Finds the 'interval' formed between two points on a DAG. These are points that are causally connected to both the start and the end node
# (i.e. they sit on a path between the start and the end node). The interval is the intersection of the forward lightcone of end,
#  and the backward lightcone of start.
#
# Arguments: DAG - The networkx DAG object
#          start - The name of the starting node (closer to the present day in terms of time)
#            end - The name of the final node (less recent in time than start)
#   
#        
# Returns: DAG - A subset of the original DAG that consists only of nodes on the lightcone between start and end
############################################################################################################################################

def interval(DAG, start, end):
    #start_relations = open('./Interval/start', 'w')
    #end_relations = open('./Interval/end', 'w')
    
    starters = lightcone_list(DAG,start,'backward')
    clean = []
    enders = lightcone_list(DAG,end,'forward',clean)
    
    #ensure that start and end nodes feature in the interval
    starters.append(start)
    enders.append(end)
    
    #print 'There are %d starters and %d enders' %(len(starters),len(enders))
    
    interval_list = []
    for thing in starters:
        if thing in enders:
            interval_list.append(thing)
            
    #print 'There are %d things in interval list' %len(interval_list)
    
    for node in DAG.nodes():
        if node not in interval_list:
            DAG.remove_node(node)
    
    return DAG

###########################################################################################################################################
# lightcone (aka 'citecone'??? Too cheesy?)
#
# Finds the 'lightcone' formed from a node on a DAG. These are all the points that are causally connected (i.e. a path can be found from the node)
# in either the forward time or backward time direction. 
#
# Arguments: DAG - The networkx DAG object
#           node - The node from which the lightcone will be formed
#           type - to find the lightcone forward in time, have type = 'forward', for backward, have type = 'backward'
#   
#        
# Returns: DAG - A subset of the original DAG that consists only of nodes in the lightcone in the chosen direction from node
############################################################################################################################################   
def lightcone(DAG,node,type):
    list = lightcone_list(DAG,node,type)
    list.append(node) #ensure the node itself is in the cone
    cone = DAG.copy()
    
    for node in cone.nodes():
        if node not in list:
            cone.remove_node(node)
    
    return cone       

def lightcone_list(DAG, node, type, relations=[]):
    
    #Change type to change whether relations are found from bottom (of DAG) up or from the top down
    if type == 'backward': #i.e. backward in time, but along the direction of the DAG
        things = DAG.successors(node)
    elif type == 'forward': #i.e. forward in time, but against the direction of the DAG
        things = DAG.predecessors(node)
    else:
        print 'Error: type given in lightcone not recognised'
    
    for thing in things:
        if thing not in relations:
            relations.append(thing)
            relations = lightcone_list(DAG,thing,type,relations)
    return relations
##########################################################################################################################################
#WORK IN PROGRESS

#To be used when we can find a good way of finding the 'time axis' length in the same units at the longest path
#work in progress
def pythag_distance(DAG, node1, node2):

    bday1 = DAG.node[node1]['birthday']
    bday2 = DAG.node[node2]['birthday']
    rank1 = DAG.node[node1]['rank']
    rank2 = DAG.node[node2]['rank']
    
    if bday1 > bday2:
        lp = dl.lpd(DAG,node1,node2)[2]
    else:
        lp = dl.lpd(DAG,node1,node2)[2]

    dbday = bday1-bday2
    drank = rank1-rank2

    distance_bday = math.sqrt(lp**2 - dbday**2) #unit mismatch
    distance_rank = math.sqrt(lp**2 - drank**2)
    
    print 'The distance between %s and %s is %f for bday and %f for rank' %(node1,node2,distance_bday,distance_rank)

#used by the axis_end to speed up the process of finding intervals from the same node to lots of different nodes    
def finish_interval(DAG, apex, point, cone1,type):
    #start_relations = open('./Interval/start', 'w')
    #end_relations = open('./Interval/end', 'w')
    
    if type == 'forward':
        bday1 = DAG.node[point]['birthday']
        bday2 = DAG.node[apex]['birthday']
        new_type = 'backward'
    if type == 'backward':  
        bday1 = DAG.node[apex]['birthday']
        bday2 = DAG.node[point]['birthday'] 
        new_type = 'forward'        
    
    
    if bday1 > bday2: #then the interval obeys time constraints    
        #ensure that start and end nodes feature in the interval
        clean = []
        cone2 = lightcone_list(DAG,point,new_type,clean)
        
        cone1.append(apex)
        cone2.append(point)
        
        interval_list = []
        
        for node1 in cone1:
            if node1 in cone2:
                interval_list.append(node1)
        
        for node in DAG.nodes():
            if node not in interval_list:
                DAG.remove_node(node)
        
        return DAG 
    else: #bday2 > bday1 and the interval does not obey time constraints, the interval should be 0
        return nx.DiGraph() #i.e. and empty graph    

#Used to generate the interval size and lp from a node to all the nodes on the top or bottom surface of the DAG
#Finds the node on the surface of the DAG which has the largest interval from the apex
#This is a good candidate to be the node at the end of the 'time axis' from the apex to the surface   
def axis_end(DAG,apex,type,surface):
    
    out = open('./axis_%s.txt' %apex, 'w')
    
    cone_list = lightcone_list(DAG,apex,type)
    print 'There are %d nodes in the cone of %s' %(len(cone_list),apex)
    print 'There are %d nodes along the top of the DAG' %len(surface)
    
    cone_top = []
    for thing in surface:
        if thing in cone_list:
            cone_top.append(thing)
            
    print 'There are %d nodes on the top of the cone' %len(cone_top)
    
    max = 0
    axis_node = [] 
    
    T = float(len(cone_top))
    i = 0. #use to print out progress bar
    for point in cone_top:
        
        DAG2 = DAG.copy()
        if type == 'forward':
            start = point
            end = apex
            
        else: #type = 'backward'
            start = apex
            end = point
        
        size = len(finish_interval(DAG2,end,start,cone_list,type))
        lp = dl.lpd(DAG2,start,end)[2] + 1 #to get lp in nodes, not edges
        out.write(str(point) + '\t' + str(size) + '\t' + str(lp) + '\n')      
        
        
        #find which node in point gives the largest interval (in terms of number of nodes)
        if size > 0:
            if size > max:
                max = size
                axis_node = [point]
            elif size == max:
                axis_node.append(point)
        i += 1
        print (i/T)*100.
    
    return [axis_node,max]

#Very good at finding the axis node (got 2nd best on the first test)
def time_axis(DAG,apex,direction):
    
    path = axis_recursive(DAG,apex,apex,direction,'clean') #give empty path to indic function to prevent hangover path from the last run
    indec = axis_recursive(DAG,apex,apex,direction,'run') #indic_recursive can have the old path at the start for some reason and therefore needs to be cleaned...
    
    return indec
    
def axis_recursive(DAG,apex,node,direction,aim = 'run',path=[]):
    if aim == 'clean': #prevent path rom previous run being put at the start of the new list
        del path[:]
        return path
    
    if aim == 'run':
        path.append(node)
        print 'The path is %d long' %len(path)
        #Change type to change whether relations are found from bottom (of DAG) up or from the top down
        if direction == 'backward':
            things = DAG.successors(node)
        elif direction == 'forward':
            things = DAG.predecessors(node)
        else:
            print 'Error: type given in indecisive_path not recognised'
            
        
        if len(things) == 0: #there are no preds/succs to consider so the end of the path has been reached
            return path
        
        else: #i.e. the end of the path has not been reached...there are still succs/preds to choose from

            r_max = 0 #Maximum degree (in for 'end' or out for 'start') found
            i_max = [] #index in list of node with maximum degree
            i = 0 #counter for the current index
            
            #Look through each of the preds/succs and find which of them has the largest interval to the apex node
            for thing in things:
            
                intvl = nx.DiGraph() #to avoid hangovers
                DAG2 = DAG.copy() #to avoid hangovers
                
                if direction == 'backward':
                    intvl = interval(DAG2,apex,thing) #find interval between potential next step and the apex
                    
                elif direction == 'forward':
                    intvl = interval(DAG2,thing,apex)

                r = intvl.number_of_nodes() #r is the number of nodes in the interval between the apex and potential next steps
                
                if r > r_max:
                    r_max = r
                    i_max = [i]

                elif r == r_max:
                    if r_max > 0:
                        i_max.append(i)

                i += 1
                        
            if len(i_max) == 1: #there is a single node that has the maximum interval size
                i_path = i_max[0]
            elif len(i_max) > 1: #there is more than one node with the same (non-zero) interval size
                i_path = choice(i_max)

            else: #i.e. i_max is an empty list...all node have k=0 i.e. the end of the path will be reached on the next step to a random one of 'things'
                i_path = choice(range(len(things)))

            next_step = things[i_path]    #This is the next step that the path with take        
                    
            path = axis_recursive(DAG,apex,next_step,direction,aim,path)
            
            return path
            
###########################################################################################################################################
# slice_data
#
# Selects nodes in a DAG that fall within a certain range in time (with the time scale defined by rank, birthday or year)
#
# Arguments: DAG - The networkx DAG object
#            low - The bottom end of the range (the lowest value)
#           high - The top end of the range
#           type - Chooses the format in which low/high have been entered (use either 'rank', 'birthday' or 'year'
#   
#        
# Returns: DAG - A subset of the original DAG that consists only of nodes that fall between low and high
#
# NB: for 'year', just enter integer values like 2001 etc
############################################################################################################################################
def slice_data(DAG,low,high,type):
    DAG2 = DAG.copy() #take care to avoid hangovers

    for node in DAG2.nodes():
        if type == 'rank':
            property = DAG.node[node]['rank']
        elif type == 'birthday':
            property = DAG.node[node]['birthday']
        elif type == 'year':
            property = DAG.node[node]['birthday']
            low = low*12000 # to convert to birthday units
            high = high*12000 # to convert to birthday units
        else:
            print 'Type not recognised in slice_data'
        if property < low:
            DAG2.remove_node(node)
        elif property > high:
            DAG2.remove_node(node)
    
    return DAG2
                
    
 
#Work in Progress
def histogram_lightcone(DAG, node, dt_rank,dt_world, type):
    cone = lightcone(DAG,node,type)
    print len(cone)
    hist = histogram(cone)
    return hist
     
def histogram(cone):
    d = {}
    for node in cone.nodes():
        x = cone.node[node]['rank']
        if x in d:
            d[x] += 1
        else:
            d[x] = 1
    return d