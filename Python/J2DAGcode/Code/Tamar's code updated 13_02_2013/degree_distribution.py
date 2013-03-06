
"""
In and out degree distributions for models
"""


import models
import trans_red as tr
import copy

def degree_histogram(G, degree_type):
    """
    Return a list of the frequency of each degree value.

    Arguments
    ----------
    G : networkx graph object
    degree_type: 'in' or 'out'

    Returns
    -------
    hist : list
       A list of frequencies of degrees.
       The degree values are the index in the list.

    Note: the bins are width one, hence len(list) can be large
    (Order(number_of_edges))
    """
    if degree_type=='in':
        deg_seq=list(G.in_degree().values())
    elif degree_type =='out':
        deg_seq=list(G.out_degree().values())
    dmax=max(deg_seq)+1
    freq= [ 0 for d in range(dmax) ]
    for d in deg_seq:
        freq[d] += 1
        
    return freq

def degree_distribution(trans_red = 'False', N=100, m=10, number_of_trials = 1, filename = 'degree_histogram'):
    
    '''
    Outputs node label (=integer time value) and in-degree before and after Transitive reduction.
    
    n, int = final number of nodes, as usual for BA model.
    m, int = number of edges to add with addition of each new node.
    trials, int = number of realisations of model to be averaged over.
    filename, string = txt file name for output
    
    '''
    
    f = open(filename, 'w')
    f.write("<k_in>" + '\t' + "rank <k_in>"+ '\t' + "<k_out>"+ '\t' + "rank <k_out>"+ '\t' + "prob_of_in_degree"+ '\t' + "prob_of_out_degree"+ '\n')
   
    in_freq=[]
    out_freq = []
    for i in range(N):
        in_freq.append(0)
        out_freq.append(0)
        
    for trial in range(number_of_trials):
        
        model = models.barabasi_albert_graph(N, m, None)
        G = model[0]
        
        if trans_red:
            G = tr.trans_red(G)
            
        in_list = degree_histogram(G, 'in')
        out_list = degree_histogram(G, 'out')
        
        for i in range(len(in_list)):
            in_freq[i] += in_list[i] 
        
        for i in range(len(out_list)):
            out_freq[i] += out_list[i]
        
        print "done trial " , trial
    
    prob_of_in_degree = []
    prob_of_out_degree = []
    
    for i in range(len(in_freq)):
        in_freq[i] = float(in_freq[i])/float(number_of_trials) 
        out_freq[i] = float(out_freq[i])/float(number_of_trials)
    
        prob_of_in_degree.append(float(in_freq[i])/float(N))
        prob_of_out_degree.append(float(out_freq[i])/float(N))
        f.write(str(i) + '\t' + str(in_freq[i])+ '\t' + str(i)+ '\t' + str(in_freq[i])+ '\t' + str(prob_of_in_degree[i])+ '\t' + str(prob_of_out_degree[i])+ '\n')
    
    f.close()
    
    return G

def BA_node_degree_pre_post_TR(n=100, m=10, trials = 1000, filename = 'degree_before_after'):
    
    '''
    Outputs node label (=integer time value) and in-degree before and after Transitive reduction.
    
    n, int = final number of nodes, as usual for BA model.
    m, int = number of edges to add with addition of each new node.
    trials, int = number of realisations of model to be averaged over.
    filename, string = txt file name for output
    
    '''

    in_list = []
    in_tr_list = []
    node_list = []
    for i in range(n):
    
        in_list.append(0)
        in_tr_list.append(0)
    
    for trial in range(trials):
        model = models.barabasi_albert_graph(n, m=10)
        G = model[0]
        H= copy.deepcopy(G)
        G_tr = tr.trans_red(H)
        
        f = open(filename, 'w')
        f.write('node' + '\t' + 'degree'+ '\t'+'node_tr'+ '\t' + 'degree_tr'+'\n')
        j=0
        for node, node_tr in zip(G.nodes(), G_tr.nodes()):
            j+=1
            node_list.append(node)
            in_degree = G.in_degree(node)
            in_list[node] += in_degree
            in_degree_tr = G_tr.in_degree(node_tr)
            in_tr_list[node] += in_degree_tr
      
        print "done trial no. ", trial
    
    for i in range(i):
        in_list[i] = in_list[i] / float(trials)
        in_tr_list[i] = in_tr_list[i] / float(trials)
        f.write(str(node_list[i]) + '\t' + str(in_list[i])+ '\t'+str(node_list[i])+ '\t' + str(in_tr_list[i])+'\n')
        
    return


