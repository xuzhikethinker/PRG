import models
import dag_lib as dl
import MM_dimension as mm
import midpoint_scaling as ms
import trans_red as tr
import networkx as nx
import numpy as np
import time
import math
import matplotlib.pyplot as plt

def clean_for_plotting(y_list,x_list, error_list):
    
    for i in range(len(x_list)):
        if y_list[i] ==None:
            x_list[i]=None
            error_list[i]=None
        else: continue
    
    def is_true(x):
        if x!=None: 
            return 1
        else: return 0
    
    clean_y_list = filter(is_true, y_list)
    clean_x_list = filter(is_true, x_list)
    clean_error_list = filter(is_true, error_list)
#    print clean_y_list, clean_x_list, clean_error_list
    return [clean_y_list, clean_x_list, clean_error_list]

def stats(input_list):
    list_sum = sum(input_list)
    list_average = list_sum/(float(len(input_list))) 
    square_diff = 0.0
    for value in input_list:
        diff = value - list_average
        diff_squared = diff**2
        square_diff += diff_squared
    std_dev = math.sqrt(square_diff/(float(len(input_list))-1.0))
    std_error = std_dev/math.sqrt(float(len(input_list)))
    return[std_error, std_dev, list_sum, list_average] 
    
def lp_with_removal(number_of_trials, number_of_probabilities_considered, N, D, filename):

    """
    arguments: number_of_trials for each probability, output data is averaged over this.
    number_of_probabilities_considered = integer: number of data points
    N = integer: number of nodes in network
    D = integer: dimension of network
    filename = string: output file path e.g. './testing.txt'
    """
    start_time = time.time() #start clock
    myfile = open(filename, 'w')

    p_step = 10.0/number_of_probabilities_considered #define size of step in probability (prob = prob that edge is removed given it's causally allowed)
    myfile.write(str(D)+"D box w/ random removal prob p, and longest path l. Number trials averaged over=" + str(number_of_trials) + " N="+ str(N) + '\n')
    myfile.write('p' + '\t' + 'l(p)' +'\t' + 'std error'  +  '\n')  #send column headings to output file
    #initialize lists for each output variable
    p_list = []
    lp_average_list = []
    lp_std_error_list = []
    
    x_range = np.arange(1.0,10.0+p_step,p_step)  #defines list of x values to loop over   

    for x in x_range: #Loop over x, where x=10^p, so we get more points around prob's closer to 1 and can see phase transition-y behavior more clearly.
        #initialize lists for each quantity for which we require an individual value to be stored after every trial (e.g. for calculations stats)
        lp_list = []
        p = math.log10(x)

        for _ in range(int(number_of_trials)): 
            model = models.box_model(D, N,1-p)  #create model with prob of no edge given edge is causally allowed = p
            DAG_with_removal =  model[0]    #networkx DAG object from above model
            tr_DAG_with_removal = tr.trans_red(DAG_with_removal)    #transitively reduce DAG - doesn't effect lp or MM dimension, DOES MIDPOINT SCALING
            extremes = model[1] #returns list of extremes from model

            if nx.has_path(DAG_with_removal, extremes[1], extremes[0]):  
                longest_path_between_extremes = dl.lpd(tr_DAG_with_removal, extremes[1],extremes[0]) #calculate longest path between extremes
                length_of_longest_path = longest_path_between_extremes[2]   #save length of longest path between extremes
      
            else: 
                length_of_longest_path=0   #if no path between extremes, longest path = 0 is physical.
        
            lp_list.append(length_of_longest_path)
        
        statistics = stats(lp_list)
        lp_average = statistics[3] #calculates average lp over all trials for the current probability
        
        if p == 0.0:
            lp_0 = lp_average  #calculate average longest path for p==0 and use as normalization constant

        lp_average = lp_average / lp_0  #normalize average longest paths so can compare  results of different dimensions
       
        for i in range(len(lp_list)):
            lp_list[i] /= lp_0  #normalize longest paths (need to do this for std error calculation)
        
        statistics = stats(lp_list)
        lp_std_error = statistics[0]
        
        p_list.append(p)
        lp_average_list.append(lp_average)
        lp_std_error_list.append(lp_std_error)

        myfile.write(str(p)+ '\t' + str(lp_average) + '\t' + str(lp_std_error) + '\n')
        
        print "finished ", p , "probability"
        
    elapsed = (time.time() - start_time)   #calculate time for method completion
    print "finished. Time elapsed = " , elapsed

    return [p_list, lp_average_list, lp_std_error_list] 

def mm_dim_with_removal(number_of_trials, number_of_probabilities_considered, N, D, filename):

    """
    arguments: number_of_trials for each probability, output data is averaged over this.
    number_of_probabilities_considered = integer: number of data points
    N = integer: number of nodes in network
    D = integer: dimension of network
    filename = string: output file path e.g. './testing.txt'
    """
    start_time = time.time() #start clock
    myfile = open(filename, 'w')

    p_step = 1.0/number_of_probabilities_considered #define size of step in probability (prob = prob that edge is removed given it's causally allowed)
    myfile.write(str(D)+"D box w/ random removal prob p, and longest path l. Number trials averaged over=" + str(number_of_trials) + " N="+ str(N) + '\n')
    myfile.write('p' + '\t' + 'mm(p)' +'\t' + 'std error' +  '\n')  #send column headings to output file
    #initialize lists for each output variable
    p_list = []
    mm_average_list = []
    mm_std_error_list = []
    
    p_range = np.arange(0.0,1.0+p_step,p_step)  #defines list of p values to loop over   

    for p in p_range: #Loop over x, where x=10^p, so we get more points around prob's closer to 1 and can see phase transition-y behavior more clearly.
        #initialize lists for each quantity for which we require an individual value to be stored after every trial (e.g. for calculations stats)
        mm_list = []

        for _ in range(int(number_of_trials)): 
            model = models.box_model(D, N,1-p)  #create model with prob of no edge given edge is causally allowed = p
            DAG_with_removal =  model[0]    #networkx DAG object from above model
            mm_dimension = mm.MM_dimension(DAG = DAG_with_removal)
            mm_list.append(mm_dimension)
        
        statistics = stats(mm_list)
        mm_average = statistics[3] #calculates average lp over all trials for the current probability
        
        statistics = stats(mm_list)
        mm_std_error = statistics[0]
        
        p_list.append(p)
        mm_average_list.append(mm_average)
        mm_std_error_list.append(mm_std_error)

        myfile.write(str(p)+ '\t' + str(mm_average) + '\t' + str(mm_std_error) + '\n')
        
        print "finished ", p , "probability"
        
    elapsed = (time.time() - start_time)   #calculate time for method completion
    print "finished. Time elapsed = " , elapsed

    return [p_list, mm_average_list, mm_std_error_list] 

def ms_dim_with_removal(number_of_trials, number_of_probabilities_considered, N, D, filename):

    """
    arguments: number_of_trials for each probability, output data is averaged over this.
    number_of_probabilities_considered = integer: number of data points
    N = integer: number of nodes in network
    D = integer: dimension of network
    filename = string: output file path e.g. './testing.txt'
    
    """

    start_time = time.time() #start clock
    myfile = open(filename, 'w')

    p_step = 1.0/number_of_probabilities_considered #define size of step in probability (prob = prob that edge is removed given it's causally allowed)
    myfile.write(str(D)+"D box w/ random removal prob p, and longest path l. Number trials averaged over=" + str(number_of_trials) + " N="+ str(N) + '\n')
    myfile.write('p' + '\t' + 'ms dimension (p)' +'\t' + 'std error' +  '\n')  #send column headings to output file
    #initialize lists for each output variable
    p_list_for_ms = []
    ms_average_list = []
    ms_std_error_list = []
    
    p_range = np.arange(0.0,1.0+p_step,p_step)   

    for p in p_range: #Loop over x, where x=10^p, so we get more points around prob's closer to 1 and can see phase transition-y behavior more clearly.
        #initialize lists for each quantity for which we require an individual value to be stored after every trial (e.g. for calculations stats)
       
        ms_list = []
        ms_diff_sq_list = []
        ms_failed_trials = 0
        
        ms_sum = ms_average = 0.0

        for _ in range(int(number_of_trials)): 
            model = models.box_model(D, N,1-p)  #create model with prob of no edge given edge is causally allowed = p
            DAG_with_removal =  model[0]    #networkx DAG object from above model
            tr_DAG_with_removal = tr.trans_red(DAG_with_removal)    #transitively reduce DAG - doesn't effect lp or MM dimension, DOES MIDPOINT SCALING
            extremes = model[1] #returns list of extremes from model

            if nx.has_path(DAG_with_removal, extremes[1], extremes[0]):  
                longest_path_between_extremes = dl.lpd(tr_DAG_with_removal, extremes[1],extremes[0]) #calculate longest path between extremes
                longest_path = longest_path_between_extremes[1]
                length_of_longest_path = longest_path_between_extremes[2]   #save length of longest path between extremes
      
            else: 
                length_of_longest_path=0   #if no path between extremes, longest path = 0 is physical.
        

            if length_of_longest_path > 2:
                ms_dimension = ms.mpsd(DAG_with_removal, longest_path)[0]  #need to trans red DAG first as otherwise lp method does not return the correct longest path. James said he'd sorted this?
            else: 
                ms_dimension = 0
                ms_failed_trials += 1

            ms_list.append(ms_dimension)
        
        if len(ms_list)<number_of_trials:
            ms_list.append(None)
        
        ms_sum = sum(ms_list)
        if (number_of_trials - ms_failed_trials - 1)>0:
            ms_average = ms_sum/float(number_of_trials-ms_failed_trials)
        else: ms_average=None
        
        ms_diff_sq_sum = 0.0
       
        if (number_of_trials - ms_failed_trials - 1)>0:
            for i in range(number_of_trials - ms_failed_trials):
                ms_diff_sq_list.append((ms_list[i] - ms_average)**2)
                ms_diff_sq_sum += ms_diff_sq_list[i]
            ms_std_dev = math.sqrt(ms_diff_sq_sum/float(number_of_trials - ms_failed_trials -1))
            ms_std_error = ms_std_dev/math.sqrt(float(number_of_trials - ms_failed_trials))
        else:
            ms_std_error = 0
            
        p_list_for_ms.append(p)
        ms_average_list.append(ms_average)
        ms_std_error_list.append(ms_std_error)

        myfile.write(str(p)+ '\t' + str(ms_average) + '\t' + str(ms_std_error) + '\n')
        
        clean = clean_for_plotting(ms_average_list,p_list_for_ms, ms_std_error_list)
        ms_average_list = clean[0]
        p_list_for_ms = clean[1]
        ms_std_error_list = clean[2]
        
        print "finished ", p , "probability"
        
    elapsed = (time.time() - start_time)   #calculate time for method completion
    print "finished. Time elapsed = " , elapsed

    return [p_list_for_ms, ms_average_list, ms_std_error_list]
  
def plot_lp_with_removal(D_selection , number_of_trials, number_of_probabilities_considered, N, data_filename, savefig_filename):
    
#    D_selection = [1,2,3,4,5,10,100]   #one dataset produced for each dimension specified here
#    figure_size = [9,6]
#    rc.set_rc_params()
    fraction_font = {'size':'22'}  #fractions on axes appear in smaller font - use this to manually increase font size
    
    plt.figure()
    #plt.axis([-0.1,1.1,-0.1,1.1])  #use to manually set upper and lower axis limits
    
    colors = plt.cm.hsv_r(np.linspace(0, 0.9, len(D_selection)))   #http://dept.astro.lsa.umich.edu/~msshin/science/code/matplotlib_cm/   rainbow hsv (from 0 to 0.9) good
    fmt = ['+','x']
    
    data = {}
    i=0
    for D,c in zip(D_selection, colors):
#        filename = './lp_with_errors_testing.txt'
        data[str(D)] = lp_with_removal(number_of_trials=number_of_trials, number_of_probabilities_considered=number_of_probabilities_considered, N=N, D=D,filename=data_filename)
        print "done ", D, "D test"
        #pass in variable lists
        x = data[str(D)][0]
        y = data[str(D)][1]
        xerr = None
        yerr = data[str(D)][2]
        plt.xlabel(r'$p$')
        plt.ylabel(r'$\frac{L_{max}(p)}{L_{max}(0)}$',**fraction_font)
       
        plt.errorbar(x, y, yerr, xerr, elinewidth = 0.5, capsize = 4, mew = 0.5, fmt=fmt[i%len(fmt)], ms = 5, label=str(D)+'D', color=c)
        i+=1
    plt.legend()
    
    # define the matplotlib.patches.Rectangle instance surrounding the legend
#    frame  = leg.get_frame()  
#    frame.set_facecolor('0.99')
#    frame.set_edgecolor('0.99')
#    
    plt.savefig(savefig_filename)
    plt.show()
    
def plot_mm_with_removal(D_selection , number_of_trials, number_of_probabilities_considered, N, data_filename, savefig_filename):
    
#    D_selection = [1,2,3,4,5,10,100]   #one dataset produced for each dimension specified here
#    figure_size = [9,6]
#    rc.set_rc_params()
#    fraction_font = {'family':'lmroman17-regular', 'size':'22'}  #fractions on axes appear in smaller font - use this to manually increase font size
    
    fig=plt.figure()
    #plt.axis([-0.1,1.1,-0.1,1.1])  #use to manually set upper and lower axis limits
    
    colors = plt.cm.hsv_r(np.linspace(0, 0.9, len(D_selection)))   #http://dept.astro.lsa.umich.edu/~msshin/science/code/matplotlib_cm/   rainbow hsv (from 0 to 0.9) good
    fmt = ['+','x']
    
    data = {}
    i=0
    for D,c in zip(D_selection, colors):
#        filename = './lp_with_errors_testing.txt'
        data[str(D)] = mm_dim_with_removal(number_of_trials=number_of_trials, number_of_probabilities_considered=number_of_probabilities_considered, N=N, D=D,filename=data_filename)
        print "done ", D, "D test"
        #pass in variable lists
        x = data[str(D)][0]
        y = data[str(D)][1]
        xerr = None
        yerr = data[str(D)][2]
        plt.xlabel(r'$p$')
        plt.ylabel(r'$D_{MM}$')
        plt.errorbar(x, y, yerr, xerr, elinewidth = 0.5, capsize = 4, mew = 0.5, fmt=fmt[i%len(fmt)], ms = 5, label=str(D)+'D', color=c)
        i+=1
    leg = plt.legend()
    
    # define the matplotlib.patches.Rectangle instance surrounding the legend
#    frame  = leg.get_frame()  
#    frame.set_facecolor('0.99')
#    frame.set_edgecolor('0.99')
    
    plt.savefig(savefig_filename)
    plt.show()

def plot_ms_with_removal(D_selection , number_of_trials, number_of_probabilities_considered, N, data_filename, savefig_filename):
    
#    D_selection = [1,2,3,4,5,10,100]   #one dataset produced for each dimension specified here
#    figure_size = [9,6]
#    rc.set_rc_params()
#    fraction_font = {'family':'lmroman17-regular', 'size':'22'}  #fractions on axes appear in smaller font - use this to manually increase font size
    
    plt.figure()
    #plt.axis([-0.1,1.1,-0.1,1.1])  #use to manually set upper and lower axis limits
    
    colors = plt.cm.hsv_r(np.linspace(0, 0.9, len(D_selection)))   #http://dept.astro.lsa.umich.edu/~msshin/science/code/matplotlib_cm/   rainbow hsv (from 0 to 0.9) good
    fmt = ['+','x']
    
    data = {}
    i=0
    for D,c in zip(D_selection, colors):
#        filename = './lp_with_errors_testing.txt'
        data[str(D)] = ms_dim_with_removal(number_of_trials=number_of_trials, number_of_probabilities_considered=number_of_probabilities_considered, N=N, D=D,filename=data_filename)
        print "done ", D, "D test"
        #pass in variable lists
        x = data[str(D)][0]
        y = data[str(D)][1]
        xerr = None
        yerr = data[str(D)][2]
        plt.xlabel(r'$p$')
        plt.ylabel(r'$D_{MS}$')
        plt.errorbar(x, y, yerr, xerr, elinewidth = 0.5, capsize = 4, mew = 0.5, fmt=fmt[i%len(fmt)], ms = 5, label=str(D)+'D', color=c)
        i+=1
    leg = plt.legend()
    
    # define the matplotlib.patches.Rectangle instance surrounding the legend
    frame  = leg.get_frame()  
    frame.set_facecolor('0.99')
    frame.set_edgecolor('0.99')
    
    plt.savefig(savefig_filename)
    plt.show()


#plot_lp_with_removal(D_selection =[1,2,3,4,5,10,100], number_of_trials = 10, number_of_probabilities_considered = 10 , N=10, data_filename = './lp_with_errors_testing.txt', savefig_filename = 'lp_with_errors_testing.pdf')


plot_mm_with_removal(D_selection =[1,2,3,4], number_of_trials = 100, number_of_probabilities_considered = 20 , N=20, data_filename = './mm_with_removal.txt', savefig_filename = 'mm_with_removal.pdf')
