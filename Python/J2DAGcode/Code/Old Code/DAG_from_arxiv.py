#Function to create a DAG from arXiv citation data

#Inputs: cite, which is a file object of the arXiv data

#Outputs: A DAG of the largest componant of the arXiv data, with an attribute 'birthday' which can be used to order nodes

import networkx as nx
import string
import re

def arXivDAG(cite):

    DAG = create(cite) #creates a DAG using the data in 'cite', although it is not necc. connected
  
    largest_component = component(DAG) #uses component function to find the largest subgraph in the data, and creates an undirected graph of it

    largest_component_DAG = redirect(DAG,largest_component) #converts the undirected, largest subgraph into a DAG

    assign_birthday(largest_component_DAG) #assigns a 'birthday' attribute to each node, in the form of an integer

    return largest_component_DAG #returns this connected DAG to the program
            
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

#Creates a DAG of the largest subgraph in the data by deleting all nodes in the original, disconnected DAG that do not feature in the largest subgraph      
def redirect(graph, component):
    current_nodes = graph.nodes() #creates a list of all of the 
    retained_nodes = component.nodes() #a list of all the nodes that need to be kept in the DAG, as they were found in the largest component
    for entry in current_nodes:                        
        if not entry in retained_nodes:
            graph.remove_node(entry) #removes a node from the DAG if it is not in the largest subgraph
    return graph
    
def assign_birthday(DAG):
    for node in DAG.nodes():
        birthday = find_birthday(node)
        DAG.node[node]['birthday'] = birthday 
   
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
    
    