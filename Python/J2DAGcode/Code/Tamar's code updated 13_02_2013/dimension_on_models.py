import models as models
import dag_lib as dl
import trans_red as tr
import statistics as stats
import midpoint_scaling as ms
import MM_dimension as mm


def mm_of_BA(N_range = range(1000,1500,50), number_of_trials = 10, data_filename = 'BA_dimension'):
    myfile = open(data_filename, 'w')
    myfile.write('N' + '\t' + 'average D_ms'+ '\t'+'std err'+'\n')
    N_list = []
    ms_av_list = []
    ms_std_err_list = []
    for N in N_range:
        N_list.append(N)
        dim_list = []
        for _ in range(number_of_trials):
            model = models.barabasi_albert_graph(N,1)
            G = model[0]
#            extremes = model[1]
            dim = mm.MM_dimension(G)
            dim_list.append(dim)
        statistics = stats.list_stats(dim_list)
        ms_av = statistics[3]
        ms_std_err = statistics[0]
        print "done ", N
        myfile.write(str(N) + '\t' + str(ms_av) + '\t' + str(ms_std_err) + '\n')
        ms_av_list.append(ms_av)
        ms_std_err_list.append(ms_std_err)
        
    return[N_list, ms_av_list, ms_std_err_list]
    
mm_of_BA()