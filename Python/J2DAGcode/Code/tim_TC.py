import networkx as nx
import data_DAG as maker

def trans_comp(DAG, ordered_nodes):
    total = len(ordered_nodes)        
    for node in ordered_nodes:
        place = float(ordered_nodes.index(node) + 1)
        print (place/total)*100     
        if node in DAG:
            succs = DAG.successors(node)
            preds = DAG.predecessors(node)
            for pred in preds:
                for succ in succs:
                    DAG.add_edge(pred,succ)
        
    return DAG
	
data = open('./Data/Cit-HepPh.DAT', 'r')
out = open('./Data/Cit-HepPh_TC_1.txt','w')


    
[DAG,node_tuples] = maker.birthdayDAG(data,True,True)
print DAG.number_of_edges()

node_list = []
for thing in node_tuples:
    node_list.append(thing[0])
	
TC_DAG = trans_comp(DAG, node_list)

for edge in TC_DAG.edges():
    out.write(str(edge[0]) + '\t' + str(edge[1]) + '\n')
	
out.write('SUCCESS!')
print 'It worked!'	
