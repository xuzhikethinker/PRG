import networkx as nx
import models
import statistics as stats
import math



def oneD_random_comparison(N=10,average_in_degree=1):
    p = (float(N)* float(average_in_degree))/float(sum(range((N-1),0,-1)))
    oneD_box = models.box_model(1, N, p)
    DAG = oneD_box[0]
#    nx.draw_circular(DAG, with_labels=False, node_colour ='k', node_size=50, node_color = 'k', width=0.5) 
    #nx.draw_networkx(G, pos=random, with_labels=False, node_colour ='k', node_size=50, node_color = 'k', width=0.5)  
#    plt.show() # display figure
    return oneD_box

def BA_sp(m, N_range):
    number_of_trials = 100
    #D=1
    myfile = open('BA_shortest_path', 'w')
    myfile.write('N' + '\t' + 'average shortest path'+ '\t'+'std err'+'\n')
    
    for N in N_range:
        sp_list = []
        comparison_sp_list = []
        for trial in range(number_of_trials):
            model = models.barabasi_albert_graph(N,m)
    #        model = models.box_model(D, N)
            G = model[0]
            extremes = model[1]
            #tr_DAG = tr.trans_red(G)
            sp_length = nx.astar_path_length(G, extremes[1], extremes[0])
    #        sp_length = 
     
            sp_list.append(sp_length)
            
            average_in_degree = float(m)*float((N-1))/(float(N))
            comparison_model = oneD_random_comparison(N,average_in_degree)
            comparison_G = comparison_model[0]
            comparison_extremes = comparison_model[1]
            
            if nx.has_path(comparison_G, comparison_extremes[1], comparison_extremes[0]): 
                comparison_sp_length = nx.astar_path_length(comparison_G, comparison_extremes[1], comparison_extremes[0])
#                comparison_sp_length = comparison_sp[2]
            else: comparison_sp_length=0
            
            comparison_sp_list.append(comparison_sp_length)
            
        statistics = stats.list_stats(sp_list)
        sp_av = statistics[3]
        sp_std_err = statistics[0]
        
        comparison_stats = stats.list_stats(comparison_sp_list)
        comparison_sp_av = comparison_stats[3]
        comparison_sp_std_err = comparison_stats[0]
        
        print "done ", N
        myfile.write(str(N) + '\t' + str(sp_av) + '\t' + str(sp_std_err) + '\t' + str(N) + '\t' + str(comparison_sp_av) + '\t' + str(comparison_sp_std_err) + '\n')
        
    return
        
        
BA_sp(m=4, N_range = range(5,501,5))        
        
        
        
        