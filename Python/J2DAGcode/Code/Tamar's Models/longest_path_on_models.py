import models as models
import dag_lib as dl
import trans_red as tr
import statistics as stats


m=1
number_of_trials = 100
D=1
myfile = open('BA_lp', 'w')
myfile.write('N' + '\t' + 'average lp length'+ '\t'+'std err'+'\n')

for N in range(2,100,5):
    lp_list = []
    for trial in range(number_of_trials):
        model = models.barabasi_albert_graph(N,m)
#        model = models.COd(D,N)
        G = model[0]
        extremes = model[1]
        #tr_DAG = tr.trans_red(G)
        lp = dl.lpd(G,extremes[1],extremes[0])
        lp_length = lp[2]
 
        lp_list.append(lp_length)
    statistics = stats.list_stats(lp_list)
    lp_av = statistics[3]
    lp_std_err = statistics[0]
    print "done ", N
    myfile.write(str(N) + '\t' + str(lp_av) + '\t' + str(lp_std_err) + '\n')
#nx.draw_random(G, with_labels=False, node_colour ='k', node_size=50, node_color = 'k', width=0.5) 
#nx.draw_networkx(G, pos=random, with_labels=False, node_colour ='k', node_size=50, node_color = 'k', width=0.5)  
#plt.show() # display figure


#D=3
#filename = './' + str(D) + 'lp_with_N.txt'
#myfile = open(filename, 'w')
#N_list = []
#lp_list = []
#lp_list_box = []
#
#for N in np.arange(0,1010,10):
#    print N
#    N_list.append(N)
#    
#    G = models.COd(D,N)
#    DAG = G[0]
#    extremes = G[1]
#    lp = dl.lpd(DAG, extremes[1], extremes[0])
#    length_lp = lp[2]
#    length_scale = length_lp/(N^(1//D))
#    lp_list.append(length_scale)
#    G = models.box_model(D, N)
#    DAG = G[0]
#    extremes = G[1]
#    lp_box = dl.lpd(DAG, extremes[1], extremes[0])
#    length_lp_box = lp[2]
#    length_scale = length_lp_box/(N^(1//D))
#    lp_list_box.append(length_scale)
# 
#
#
#for i in range(len(N_list)):
#    myfile.write(str(N_list[i]) + '\t' + str(lp_list[i]) + '\t' + str(N_list[i]) + '\t'+ str(lp_list_box[i]) + '\n')

