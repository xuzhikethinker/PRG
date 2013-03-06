
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

def age(n1, n2): #is n2 younger than n1?
    YY1 = n1[0:1]
    MM1 = n1[2:3]
    rank1 = n1[4:7]
    YY2 = n2[0:1]
    MM2 = n2[2:3]
    rank2 = n2[4:7]
    if YY1.startswith("0"):
        YY1 = "20" + YY1
    else:
        YY1 = "19" + YY1
    if YY2.startswith("0"):
        YY2 = "20" + YY2
    else:
        YY2 = "19" + YY2
    if int(YY1) < int(YY2):
       return True
    if int(YY1) > int(YY2):
        return False
    if int(YY1) == int(YY2):
        if int(MM1) < int(MM2):
            return True
        if int(MM1) > int(MM2):
            return False
        if int(MM1) == int(MM2):
            if int(rank1) < int(rank2):
                return True
            if int(rank1) > int(rank2):
                return False

def simple_age(n1, n2): #is n2 younger than n1?
    if int(n1) < int(n2):
        return True
    if int(n1) > int(n2):
        return False
            
    
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
            paths.append(newpaths)
    return paths    
    
    
def find_all_paths_age(DAG, start, end, path=[]):
    path = path + [start]
    if start == end:
        return path
    if DAG.predecessors(start) == []:
        return []
    paths = []
    for node in DAG.predecessors(start):
        if age(start, node): #if the start is older than node [start is the most recent paper, right?]
            return paths        
        if node not in path:
            newpaths = find_all_paths_age(DAG, node, end, path)
            paths.append(newpaths)
 #           for newpath in newpaths:
 #               paths.append(newpath)
    return paths
    
def make_list_of_paths(paths, pathlist):
    for path in paths:
        if isinstance(path, str):
            pathlist.append(paths)
        if isinstance(path, list):
            make_list_of_paths(path, pathlist)
    return pathlist

def distance_to_all_nodes(DAG, start, end, path=[] ):
    path_length = nx.get_node_attributes(DAG, 'path_length') 
    path = path + [start]
    if start == end:
        return path
    if DAG.predecessors(start) == []:
        return []
    paths = []
    for node in DAG.successors(end):
        print node
        length_n = float(path_length[node])
        print length_n
        length_end = float(path_length[end])
        if length_n < length_end + 1:
            if node not in path:
                DAG.node[node]['path_length'] = length_end + 1
                newpaths = distance_to_all_nodes(DAG, node, end, path)
                paths.append(newpaths)
    return paths      
            

    
def main(DAG, cite, out):
    DAG = create(cite, DAG)
    print nx.number_of_edges(DAG)
   # largest_component = component(DAG) #uses component function to select the largest subgraph in the data
   # if not largest_component.is_directed():
   #     out.write('\n' + 'yup not directed')
   # out.write('\n' + 'There are %d nodes in the largest component' %nx.number_of_nodes(largest_component))
   # largest_component_DAG = redirect(DAG,largest_component)
   # if not largest_component_DAG.is_directed():
   #     out.write('\n' + 'Not directed')
   # else:
   #     out.write('\n' + 'Directed!')
   # out.write('\n' + 'There are now %d nodes in the largest component' %nx.number_of_nodes(largest_component_DAG))
    #check if it's connected etc!
   # out.write(str(DAG.number_of_edges()))
   # if not nx.is_connected(largest_component_DAG.to_undirected()):
   #     out.write('\n'+'The network is disconnected')
   #     out.write('\n' + "There are %d connected components" %nx.number_connected_components(largest_component_DAG.to_undirected()))
    #out.write(str(nx.average_shortest_path_length(largest_component_DAG)))
    print age('0001001', '9401139')
    print age('0103030', '0204161')
    print simple_age('33', '100')
    print simple_age('45', '2')
    paths = find_all_paths(DAG,'8', '0')
 #   print paths
    empty = []
    listofpaths = make_list_of_paths(paths, empty)
    pathset = []
    for path in listofpaths:
        if path not in pathset:
            pathset.append(path)
    for path in pathset:
        out.write('\n' + str(path))
    out.write('\n' + 'The longest path is ' + str(max(pathset, key=len)))
#    setofpaths = set(listofpaths)

        #Uses method 2 of assigning a number to each node reflecting the path length between that node and the youngest node
        print 'Testing using numbering started'  
        paths3 = distance_to_all_nodes(box, extremal_points[0] , n) #creates list containing lists of lists of lists...containing a list of the nodes in path
        empty3 = [] #creates the list which will contain all of the paths
        listofpaths3 = make_list_of_paths(paths3, empty3) #looks through 'paths' to extract proper paths from the various levels of sublists
        pathset3 = [] #creates the list which will contain all unique paths
        for path in listofpaths3:
            if path not in pathset3:
                pathset3.append(path) #only adds unique paths to pathset
        out.write('\n' + 'Path testing using number...%d unique paths found' %(len(pathset3)))
        for path in pathset3:
            out.write('\n' + str(path)) #prints all the unique paths
#        longestpath3 = max(pathset3, key=len) #identifies the longest path in the set of paths
#        out.write('\n' + 'The longest path from numbering is %s which is %d nodes long' %(str(longestpath3), len(longestpath3)))
        print 'Testing using numbering completed'  

    #nx.draw(DAG) #draw the DAG
    #plt.savefig("C:\Users\Jamie\Project\pic.png") #create the image file
  
    
if __name__ == "__main__":
    DAG = nx.DiGraph() #create an empty DAG
    cite = open('C:\Users\Jamie\Project\TestNet.DAT', 'r') #input data
    out = open('C:\Users\Jamie\Project\out.txt','w') #output file
    main(DAG, cite, out)
    