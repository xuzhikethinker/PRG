
import networkx as nx
import matplotlib.pyplot as plt
import string



def create(data, DAG):

    edges = []

    for line in data: #loop over each line in the data
        nodes = line.split(" ") #split the data into citer and cited
	    #for node in nodes: #adding 0s to the start of data points when dropped
		#   while len(str(node)) < 7:
		#	   newnode = '0' + str(node)
        nodes[1]=nodes[1].strip()
        DAG.add_edge(nodes[0],nodes[1]) #add the vertex to the graph
    return DAG
		
def component(graph):
    UG=graph.to_undirected()  #undirects the DAG so that the connectivity functions can be used
    out.write('Connectivity Tests:')
    out.write('\n' + 'There are %d nodes in the network' %nx.number_of_nodes(UG))
    if not nx.is_connected(UG):
	    out.write('\n'+'The network is disconnected')
	    out.write('\n' + "There are %d connected components" %nx.number_connected_components(UG))
	    components = nx.connected_component_subgraphs(UG)
	    i = 0
	    nmax = 0
	    imax = 0
	    for subgraph in components:
		    n = nx.number_of_nodes(subgraph)
		    if n > nmax:
			    nmax = n
			    imax = i
		    #out.write('\n' + 'There are %d nodes in subgraph %d' %(n,i))
		    i+=1
	    out.write('\n' + 'The largest component is subgraph %d' %imax)
	    return components[imax]

#def age(name):
    
def redirect(graph, component):
    current_nodes = graph.nodes()
    retained_nodes = component.nodes()
    out.write('\n' + 'There are %d nodes in retained_nodes' %len(retained_nodes))
    for entry in current_nodes:                        
        if not entry in retained_nodes:
            graph.remove_node(entry)
    out.write('\n' + 'There are %d nodes in component' %nx.number_of_nodes(component)) 
    out.write('\n' + 'There are %d nodes in graph' %nx.number_of_nodes(graph))
    return graph
	   
def find_all_paths(DAG, start, end, path=[]):
    path = path + [start]
    if start == end:
        return path
    if DAG.predecessors(start) == []:
        return []
    paths = []
    for node in DAG.predecessors(start):
        if node not in path:
            newpaths = find_all_paths(DAG, node, end, path)
            for newpath in newpaths:
                paths.append(newpath)
    return paths

    
def main(DAG, cite, out):
    DAG = create(cite, DAG)
    print nx.number_of_edges(DAG)
    largest_component = component(DAG) #uses component function to select the largest subgraph in the data
    #if not largest_component.is_directed():
    #    out.write('\n' + 'yup not directed')
    #out.write('\n' + 'There are %d nodes in the largest component' %nx.number_of_nodes(largest_component))
    #largest_component_DAG = redirect(DAG,largest_component)
    #if not largest_component_DAG.is_directed():
    #    out.write('\n' + 'Not directed')
    #else:
    #    out.write('\n' + 'Directed!')
    #out.write('\n' + 'There are now %d nodes in the largest component' %nx.number_of_nodes(largest_component_DAG))
    #check if it's connected etc!
    #out.write(str(DAG.number_of_edges()))
    #if not nx.is_connected(largest_component_DAG.to_undirected()):
    #    out.write('\n'+'The network is disconnected')
    #    out.write('\n' + "There are %d connected components" %nx.number_connected_components(largest_component_DAG.to_undirected()))
    #out.write(str(nx.average_shortest_path_length(largest_component_DAG)))
    paths = find_all_paths(DAG,'0', '8')
    print paths

    nx.draw(DAG) #draw the DAG
    plt.savefig("C:\Users\Jamie\Project\pic.png") #create the image file
  
    
if __name__ == "__main__":
    DAG = nx.DiGraph() #create an empty DAG
    cite = open('C:\Users\Jamie\Project\TestNet.DAT', 'r') #input data
    out = open('C:\Users\Jamie\Project\out.txt','w') #output file
    main(DAG, cite, out)
    