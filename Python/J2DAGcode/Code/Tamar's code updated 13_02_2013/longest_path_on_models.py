import models as models
import dag_lib as dl
import statistics as stats
import networkx as nx

#Run this more m=1,2,3 and as high as seems reasonable?

def oneD_random_comparison(N=10,average_in_degree=1):
    p = (float(N)* float(average_in_degree))/float(sum(range((N-1),0,-1)))
    oneD_box = models.box_model(1, N, p)
    return oneD_box

def BA_lp(m=50, N_range = range(51,555,50)):

    number_of_trials = 100
    myfile = open('BA_lp', 'w')
    myfile.write('N' + '\t' + 'average lp length'+ '\t'+'std err'+'\n')
    
    for N in N_range:
        lp_list = []
        comparison_lp_list = []
        for _ in range(number_of_trials):
            model = models.barabasi_albert_graph(N,m)
    #        model = models.COd(D,N)
            G = model[0]
            extremes = model[1]
            
#            average_in_degree = float(m)*float((N-1))/(float(N))
#            print average_in_degree
            average_in_degree = float(nx.number_of_edges(G)) /float(nx.number_of_nodes(G))
            comparison_model = oneD_random_comparison(N,average_in_degree)
            comparison_G = comparison_model[0]
            comparison_extremes = comparison_model[1]
            
            if nx.has_path(comparison_G, comparison_extremes[1], comparison_extremes[0]): 
                comparison_lp = dl.lpd(comparison_G, comparison_extremes[1], comparison_extremes[0])
                comparison_lp_length = comparison_lp[2]
            else: comparison_lp_length=None
            
            #tr_DAG = tr.trans_red(G)
            lp = dl.lpd(G,extremes[1],extremes[0])
            lp_length = lp[2]
     
            lp_list.append(lp_length)
            comparison_lp_list.append(comparison_lp_length)
        statistics = stats.list_stats(lp_list)
        lp_av = statistics[3]
        lp_std_err = statistics[0]
        
        comparison_stats = stats.list_stats(comparison_lp_list)
        comparison_lp_av = comparison_stats[3]
        comparison_lp_std_err = comparison_stats[0]
        print "done ", N
        myfile.write(str(N) + '\t' + str(lp_av) + '\t' + str(lp_std_err) +'\t'+ str(comparison_lp_av) + '\t' + str(comparison_lp_std_err) + '\n')
    #nx.draw_random(G, with_labels=False, node_colour ='k', node_size=50, node_color = 'k', width=0.5) 
    #nx.draw_networkx(G, pos=random, with_labels=False, node_colour ='k', node_size=50, node_color = 'k', width=0.5)  
    #plt.show() # display figure
    return

BA_lp()
