#plots.py
#A set of functions to make pretty plots

import numpy as np
import matplotlib.pyplot as plt
import scipy.optimize as so
import data_DAG as maker
 
def find_confidence_interval(x, pdf, confidence_level):
    return pdf[pdf > x].sum() - confidence_level
 
def density_contour(xdata, ydata, nbins_x, nbins_y, ax=None, **contour_kwargs):
    ''' Create a density contour plot.
     
    Parameters
    ----------
    xdata : numpy.ndarray
    ydata : numpy.ndarray
    nbins_x : int
    Number of bins along x dimension
    nbins_y : int
    Number of bins along y dimension
    ax : matplotlib.Axes (optional)
    If supplied, plot the contour to this axis. Otherwise, open a new figure
    contour_kwargs : dict
    kwargs to be passed to pyplot.contour()
    '''
     
    H, xedges, yedges = np.histogram2d(xdata, ydata, bins=(nbins_x,nbins_y), normed=True)
    x_bin_sizes = (xedges[1:] - xedges[:-1]).reshape((1,nbins_x))
    y_bin_sizes = (yedges[1:] - yedges[:-1]).reshape((nbins_y,1))
     
    pdf = (H*(x_bin_sizes*y_bin_sizes))
     
    one_sigma = so.brentq(find_confidence_interval, 0., 1., args=(pdf, 0.68))
    two_sigma = so.brentq(find_confidence_interval, 0., 1., args=(pdf, 0.95))
    three_sigma = so.brentq(find_confidence_interval, 0., 1., args=(pdf, 0.99))
    levels = [one_sigma*0.25,one_sigma*0.5,one_sigma*0.75,one_sigma, two_sigma, three_sigma]
     
    X, Y = 0.5*(xedges[1:]+xedges[:-1]), 0.5*(yedges[1:]+yedges[:-1])
    Z = pdf.T
     
    if ax == None:
        contour = plt.contour(X, Y, Z, levels=levels, origin="lower", **contour_kwargs)
    else:
        contour = ax.contour(X, Y, Z, levels=levels, origin="lower", **contour_kwargs)
     
    return contour
    
#Work in Progress
def histogram_lightcone(DAG, node, dt_rank,dt_world, type):
    cone = lightcone(DAG,node,type)
    print len(cone)
    hist = histogram(cone)
    return hist

def degree_distribution(DAG,name):
    [in_hist,out_hist] = degree_histograms(DAG)
    
    
def degree_histograms(DAG):
    
    d_in = {}
    d_out = {}
    for node in cone.nodes():
        k_in = DAG.in_degree(node)
        k_out = DAG.out_degree(node)
        
        if k_in in d_in:
            d[x] += 1
        else:
            d[x] = 1
            
        if k_out in d_out:
            d[x] += 1
        else:
            d[x] = 1
    return [d_in,d_out]

def forceAspect(ax,aspect=1):
    im = ax.get_images()
    extent =  im[0].get_extent()
    ax.set_aspect(abs((extent[1]-extent[0])/(extent[3]-extent[2]))/aspect)
    
def changers_plot(k_DAG,k_TR,name,type):
    x_range = max(k_DAG)
    y_range = max(k_TR)
    
    fig = plt.figure()
    ax = fig.add_subplot(111)
    H, xedges, yedges = np.histogram2d(k_DAG, k_TR, range=[[0,x_range],[0,y_range]], bins=(x_range,y_range))
    from matplotlib.colors import LogNorm
    extent = [  xedges[0], xedges[-1],yedges[0], yedges[-1]]
    plt.imshow(H.T,'autumn',norm = LogNorm(), extent=extent, interpolation='nearest',origin='lower')
    plt.colorbar()    

    #fig.subplots_adjust(bottom=0.15, left=0.15)
    levels = (2,10,50,100)
    cset = plt.contour(H.T, levels, origin = 'lower',colors=['black'],linewidths=(1.9, 1.6, 1.5, 1.4),extent=extent)
    plt.clabel(cset, inline=1, fontsize=10, fmt='%1.0i')
    for c in cset.collections:
        c.set_linestyle('solid')
    
    forceAspect(ax)
    plt.title("Changes in %s-Degree after TR for %s Data" %(type,name))
    plt.xlabel("%s-Degree before TR" %type)
    plt.ylabel("%s-Degree after TR" %type)
        
    plt.savefig( './Plots/%s_change_%s.png' %(name,type) )
    
def degree_dist(k_DAG,k_TR,name,type):
    fig = plt.figure()
    H,binEdges = np.histogram(k_DAG, range=[0,1000],bins=1000)
    H2,binEdges2 = np.histogram(k_TR, range=[0,1000],bins=1000)
    bincenters = 0.5*(binEdges[1:]+binEdges[:-1])
    bincenters2 = 0.5*(binEdges2[1:]+binEdges2[:-1])
    plt.plot(bincenters,H,'-',label='Original')
    plt.plot(bincenters2,H2,'-',label='After TR')    
    plt.gca().set_xscale("log")
    plt.gca().set_yscale("log")
    
    plt.title("%s-Degree Distribution for %s Data" %(type,name))
    plt.xlabel("log(Node %s Degree)" %type)
    plt.ylabel("Number of Nodes")
    
    plt.legend()
      
    plt.savefig( './Plots/%s_kdist_%s.png' %(name,type) )
    
    
def TR_plot(DAG,TR,name):
    ins_DAG = []
    ins_TR = []
    outs_DAG = []
    outs_TR = []
    
    nodes = DAG.nodes()
    N = len(nodes)
    
    for node in nodes:
        ins_DAG.append(DAG.in_degree(node))
        ins_TR.append(TR.in_degree(node))
        outs_DAG.append(DAG.out_degree(node))
        outs_TR.append(TR.out_degree(node))
    
    changers_plot(ins_DAG,ins_TR,name,'In')
    changers_plot(outs_DAG,outs_TR,name,'Out')

    degree_dist(ins_DAG,ins_TR,name,'In')
    degree_dist(outs_DAG,outs_TR,name,'Out')
    
def data_properties(DAG, name):
    years = {}
    for node in DAG.nodes():
        if node.startswith('0'):
            node = '20' + node
        else:
            node = '19' + node
        year = int(node[0:4])
        if year in years:
            years[year] += 1
        else:
            years[year] = 1
    x = []
    y = []
    for pair in years.iteritems():
        x.append(pair[0])
        y.append(pair[1])
    plt.bar(x,y, color= 'DodgerBlue',edgecolor= 'DarkSlateGrey',align='center',linewidth=0.8)
    plt.xlim(1991,2004)
    plt.title("Hep%s Papers in the arXiv" %name)
    plt.xlabel("Year of Publication")
    plt.ylabel("Number of Papers")
    plt.savefig( './Plots/%s_byyear.png' %name )
    
        
    
    
if __name__ == '__main__':    
    
    
    fields = ['Ph','Th']
    for field in fields:    
        
        data = open('./Data/Cit-Hep%s.DAT' %field, 'r')
        DAG = maker.birthdayDAG(data,rank=True)
        data_TR = open('./Data/Cit-Hep%s_TR.txt' %field, 'r')
        TR = maker.birthdayDAG(data_TR)
        data_properties(DAG,field)
        #TR_plot(DAG,TR,'Hep%s' %field)
        
        