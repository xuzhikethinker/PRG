#data_DAG.py
#Generates DAG from data

import networkx as nx
import string
import re
from operator import itemgetter

#################################################################################################################################################
# birthdayDAG
# Use birthdayDAG when nodes in the data are of the form YYMMNNN (where leading zeros may or may not be dropped)
#
# Arguments: data - the dataset
#            rank - A boolean. If True, an attribute, 'rank' will be added to each node indicating it's rank in time (0 is the earliest) (Default is False)
#            return_list - A boolean. If True, a list of tuples of the form ('node name',birthday(int)) will be returned, ordered by birthday (ascending)
#          
# Returns:   largest_component_DAG - a networkx DiGraph object of the largest connected component of the dataset
#
# Attribute: 'birthday' - an integer indicating when a paper was published (higher birthday values for more recent papers)
#            'rank'     - the node's rank in terms of release order ('rank' = 0 is the first published)
#
# Note: 'birthday' calculated from YYMMNNN node names in the data set:
#        YY is converted to YYYY format...
#        then birthday = (YYYY*12 + MM)*1000 + NNN
#################################################################################################################################################

def birthdayDAG(data, rank=False, return_list=False):

    DAG = create(data) #creates a DAG using the data in 'cite', although it is not necc. connected
  
    largest_component = component(DAG) #uses component function to find the largest subgraph in the data, and creates an undirected graph of it

    largest_component_DAG = redirect(DAG,largest_component) #converts the undirected, largest subgraph into a DAG

    rank_time = assign_birthday(largest_component_DAG, rank) #assigns a 'birthday' attribute to each node, in the form of an integer
    
    if rank:
        i = 0
        for pair in rank_time:
            DAG.node[pair[0]]['rank'] = i
            i += 1
    
    if return_list:
        return [largest_component_DAG,rank_time]
    else:
        return largest_component_DAG

#creates a networkx digraph object from the whole data set
#leading zeros are added back onto the node names if they are omitted    
def create(data):
    DAG = nx.DiGraph()
    for line in data: #loop over each line in the data
        nodes = re.findall(r'\w+', line) #splits up data line whenever there is a space, comma, tab etc
        newnodes = []
        for node in nodes: #adding 0s to the start of data points when dropped
            if len(str(node)) == 4:
                newnodes.append('000' + node.strip())
            if len(str(node)) == 5:
                newnodes.append('00' + node.strip())
            if len(str(node)) == 6:
                newnodes.append('0' + node.strip())
            if len(str(node)) == 7:
                newnodes.append(node.strip()) 
        if check_edges(newnodes[0], newnodes[1]):
            DAG.add_edge(newnodes[0],newnodes[1]) #add the vertex to the graph
    return DAG

#finds the largest connected component of the whole dataset
#returns this component, but undirected   
def component(graph):
    UG=graph.to_undirected()  #undirects the DAG so that the connectivity functions can be used
    if not nx.is_connected(UG): #tests when UG is connected
	    components = nx.connected_component_subgraphs(UG) #creates a list of all of the subgraphs of UG
	    i = 0 
	    nmax = 0
	    imax = 0
	    for subgraph in components: #iterates over list of subgraphs
		    n = nx.number_of_nodes(subgraph) #finds size of subgraph
		    if n > nmax: #compares the current size of the subgraph to the largest previously found
			    nmax = n #updates the largest size value
			    imax = i #remembers the index in the subgraph list of the largest component found
		    i+=1
	    return components[imax] #returns the largest subgraph (by number of nodes)
    if nx.is_connected(UG):
        return UG

#Creates a DAG of the largest subgraph in the data by deleting all nodes in the original, disconnected DAG that do not feature in the largest subgraph      
def redirect(graph, component):
    current_nodes = graph.nodes() #creates a list of all of the 
    retained_nodes = component.nodes() #a list of all the nodes that need to be kept in the DAG, as they were found in the largest component
    for entry in current_nodes:                        
        if not entry in retained_nodes:
            graph.remove_node(entry) #removes a node from the DAG if it is not in the largest subgraph
    return graph
    
def assign_birthday(DAG, rank):
    birthday_tuples = []
    for node in DAG.nodes():
        birthday = find_birthday(node)
        DAG.node[node]['birthday'] = birthday 
        birthday_tuples.append((node,birthday)) 
    if rank:
        birthday_tuples = sorted(birthday_tuples, key=itemgetter(1)) #sorts all of the nodes based upon their birthday, so that the rank time can be found
        return birthday_tuples
   
def find_birthday(node):
    YY = node[0:2] #The first two characters in the node name indicate the year
    MM = node[2:4] #The next two characters indicate the month
    rank = node[4:7] #The final three characters indicate the order of publishing within the month
    if YY.startswith("0"): #converts from YY format to YYYY format
        YYYY = "20" + YY
    else:
        YYYY = "19" + YY
    months = int(YYYY)*12 + int(MM)
    birthday = months*1000 + int(rank)  
    return birthday
    
    
def check_edges(node1, node2):
    # Checks that the edges of the DAG go in the right direction - ie. they go from new papers to old papers
    # This necessarily removes any cycles
    if find_birthday(node1) > find_birthday(node2):
        return True
    else:
        return False

#################################################################################################################################################
# basicDAG
# Use basicDAG rather than birthdayDAG when the vertices in the data are not of the form YYMMNNN (where leading zeros may be dropped)
# i.e. you cannot consistenly work out the birthday of a node from the node labels used in the dataset
#
# Arguments: data - the dataset (in form 'a' 'b' in the data means b->a in the DAG
#          
# Returns:   largest_component_DAG - a networkx DiGraph object of the largest connected component of the dataset
#
# Attribute: 'birthday' - either an integer or 'None'
#
# Note: If there are nodes in the data labelled with the form blahblah99blah, these will be given a birthday of the form YYYY (1999 here)...
#       ...else the birthday attribute will be 'None'
#################################################################################################################################################
def basicDAG(data):
    DAG = nx.DiGraph()
    for line in data:
        nodes = re.findall(r'\w+', line)
        if nodes[0] != nodes[1]:
            DAG.add_edge(nodes[1],nodes[0])
    for node in DAG.nodes():
        DAG.node[node]['birthday'] = None #there doesn't seem to be a way of consistently finding the birthday from the data...
    
    largest_component = component(DAG) #uses component function to find the largest subgraph in the data, and creates an undirected graph of it

    largest_component_DAG = redirect(DAG,largest_component) #converts the undirected, largest subgraph into a DAG
    
    citeseer_birthdays(largest_component_DAG)
    
    return largest_component_DAG

#In the citeseer dataset, it is possible to identify the majority of the node's birthdays
#1435/2118 nodes have birthdays (year only)
#958 edges go the wrong way in time according to birthdays...
def citeseer_birthdays(DAG):
    for node in DAG.nodes():
        if not is_number(node):
            YY_list = re.findall(r'\d+', node)
            if len(YY_list) > 0:
                YY = YY_list[0]
                if YY.startswith("0"): #converts from YY format to YYYY format
                    YYYY = "20" + YY
                else:
                    YYYY = "19" + YY
                DAG.node[node]['birthday'] = float(YYYY)

        
def is_number(s):
    try:
        float(s)
        return True
    except ValueError:
        return False
    
    