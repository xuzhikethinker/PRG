# trans_red
# performs a transitive reduction on an ordered DAG
import networkx as nx
import dag_lib as dl
# IN PROGRESS

def trans_red(DAG): #extremely provisional name...do with it what you will
    for edge in DAG.edges(): #iterates over all edges in the original DAG
        [node1, node2] = edge #the edge goes from node1 to node2
        DAG.remove_edge(node1, node2) #remove this edge from the original DAG
        if not nx.has_path(DAG,node1,node2): #if it is now not possible to reach node2 from node1...
            DAG.add_edge(node1,node2) #...the direct edge is neccessary to preserve causal structure
    return DAG    #the transitively reduced DAG is returned
                    
                    
def trans_red_slow(DAG):
    for node1 in DAG.nodes():
        for node2 in DAG.successors(node1):
            for child in DAG.successors(node1):
                if path_check(DAG, child, node2):
                    # There is a longer path from node1 to node2 than the direct link, so cut the direct link
                    # Delete edge from node1 to node2
                    #print 'Delete edge from %s to %s' % (node1, node2)
                    DAG.remove_edge(node1, node2)
                    break
                    
    return DAG

                    
def path_check(DAG, start, end):
    if end in DAG.successors(start):
        return True
    for child in DAG.successors(start):
        if dl.age_check(DAG, child, end):
            # We can possibly go from this child to the end
            if path_check(DAG, child, end):
                return True
            else:
                return False
                
               
                
                
#basic way of testing whether a transitive reduction preserves all of the causal structure of the original DAG
#does not test whether a reduced representation of a DAG is the transitive reduction (i.e. a graph with the fewest edges possible)
def test_TR(DAG, TR):
    missing_edges = []
    for node1 in DAG.nodes():
        for node2 in DAG.nodes(): #iterates over all pairs of nodes in the DAG
            if dl.age_check(DAG, node1, node2): #ensure that there could possibly be a path from node1 to node2
                if nx.has_path(DAG, node1, node2): #tests whether there is a path between these two nodes in the original DAG
                    if not nx.has_path(TR, node1, node2): 
                        missing_edges.append([node1, node2]) #if there is no longer a path between these two pairs of nodes in the transitive reduction...
    return missing_edges #...then these two edges are stored and printed
  
def trans_comp_dict(DAG):
    successor_attribute = nx.get_node_attributes(DAG, 'successors')
    spinsters = create_successor_lists(DAG, successor_attribute)
    for spinster in spinsters:
        DAG = connect_to_decendants_dict(DAG, spinster, successor_attribute)
    return DAG
        
def trans_comp(DAG):
    for node1 in DAG.nodes():
        for node2 in DAG.nodes():
            if dl.age_check(DAG, node1, node2):
                if not node1 == node2:
                    if nx.has_path(DAG, node1, node2):
                        DAG.add_edge(node1, node2)
    return DAG
    
def trans_comp_search(DAG):
    spinsters = find_spinsters(DAG)
    for spinster in spinsters:
        DAG = connect_to_decendants(DAG, spinster)
    return DAG
  
def connect_to_decendants_dict(DAG, node, successor_attribute):
    node_successors = DAG.successors(node)
    for pred in DAG.predecessors(node): #loops over each of node's predecessors
        direct_successors = successor_attribute[pred] #looks at the current 'successor' list of the predecessors, which will just be direct ones
        new_direct_successors = direct_successors #creates a list that will contain both direct and indirect successors
        for indirect_successor in node_successors: #each of 'node's successors that isn't already a successor of 'pred' is an indirect successor of 'pred'
            if indirect_successor not in direct_successors:
                new_direct_successors.append(indirect_successor) #In a TC'ed DAG, edges will be created to all successors, both direct and indirect
        successor_attribute[pred] = new_direct_successors #updates the 'successor' list of pred to include both direct and indirect successors
        connect_to_decendants_dict(DAG, pred, successor_attribute) #now consider all of the predecessors of pred...
        
def connect_to_decendants(DAG, node):
    for pred in DAG.predecessors(node):
        for succ in DAG.successors(node):
            DAG.add_edge(pred, succ)
        DAG = connect_to_decendants(DAG, pred)
    return DAG            

def find_spinsters(DAG, no_successors=[]):
    for node in DAG.nodes():
        if len(DAG.successors(node)) == 0:
            no_successors.append(node)
    return no_successors            
        
def create_successor_lists(DAG, successor_attribute, no_successors=[]):
    for node in DAG.nodes():
        successor_list = DAG.successors(node)
        successor_attribute[node] = successor_list
        if len(successor_list) == 0:
            no_successors.append(node)
    return no_successors   
            
            
            



    
                        
        
    