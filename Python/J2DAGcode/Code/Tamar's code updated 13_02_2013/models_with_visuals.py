#import sys
#sys.path.reverse() #required for matplotlib drawing package on my laptop - path is messed up
from matplotlib.pyplot import *
import networkx as nx
import random
import math
import models
#import matplotlibrc as rc

class FancyDrawing(object):

    def draw_network(self,G,pos,ax,sg=None):
        from matplotlib.patches import FancyArrowPatch
    
        for n in G:
            c=Circle(pos[n],radius=0.04,alpha=0.5, color ='k') #Defines node representation
            ax.add_patch(c)
            G.node[n]['patch']=c
            x,y=pos[n]
        seen={}
        for (u,v,d) in G.edges(data=True):
            n1=G.node[u]['patch']
            n2=G.node[v]['patch']
            rad=0.5    #Radius of circular edges
            if (u,v) in seen:
                rad=seen.get((u,v))
                print rad
                rad=(rad+np.sign(rad)*0.1)*-1
            alpha=0.5
            color='k'
    
            e = FancyArrowPatch(n1.center,n2.center,patchA=n1,patchB=n2,
                                arrowstyle='-|>',
                                connectionstyle='arc3,rad=%s'%rad,
                                mutation_scale=12.0,
                                lw=1,
                                alpha=alpha,
                                color=color)
            seen[(u,v)]=rad
            ax.add_patch(e)
        return e
    
def oneD_box(N,show_plot=False,random_removal_prob=0,line=False):

    '''
    Returns a Box Set style 1D networkx graph object.
    Args: N=number of nodes
            show_plot=boolean, set as True to show figure
            random_removal=number of edges to randomly remove
            line=boolean, set as True to display nodes in straight line with curved edges (better for small N), 
            False to show in circle (better for large N)
    '''
    #Create empty directed graph object (number of nodes == 0) 
    G=nx.DiGraph()
    G.add_nodes_from(range(N))#This makes nodes labeled 0 to N-1, i.e. N nodes

    for i in range(0,N):
        for j in range(0,N):
            if i<j:
                G.add_edge(i,j) 
        
    #Random edge removal
    if random_removal_prob != 0:
        probabilistic_edge_removal(G,random_removal_prob)
        
    def plot_1D_circular(G):
        nx.draw_circular(G, alpha = 1, width = 0.1, node_color = 'red', with_labels=False, node_size=15, linewidths=0.5)
        #Draw nodes in circle, with straight edges
        savefig('1D_circle.pdf')
        show() # display figure  
        return
    
    def plot_1D_linear(G):
        pos = {}
        number_of_nodes = len(nx.nodes(G))
        for node in range(number_of_nodes):
            pos[node] = node,0
        ax=gca()
        FancyDrawing().draw_network(G,pos,ax)
        axis('equal')
        axis('off')
        savefig('1D_line.pdf')
        show()
        return

    #Check this is a DAG
    if nx.is_directed_acyclic_graph(G):
        print "This is a 1D box causal DAG."
        #Create & show figure
        if show_plot == True:
#            rc.set_rc_params(figure_size = [6,6])
            if line == True:#Draw nodes in straight line, with curved edges, if False, time goes in a circle
                plot_1D_linear(G)
            else:  
                plot_1D_circular(G)# display figure           
    else:
        print "This is completely wrong"    
                                             
    return (G)


#Function to  If second argument is True a figure is displayed.

def twoD_box(N,show_plot=False,random_removal_prob=0):
    '''
    Returns a Box Set style 2D networkx graph object, with randomly sprinkled nodes.
    Args: N=number of nodes
        show_plot=boolean, set as True to show figure
        random_removal=number of edges to randomly remove
    '''
    #Creates Empty Directed graph object
    G=nx.DiGraph()
        
    #Generate random coordinates for N points with given density in box, and add nodes to G w/ attributes representing x & y
    point_density=1
    coord_max = math.sqrt(N/point_density)
    coord = {}
        
    for i in range(N):
        rand_x = (random.random())*coord_max
        rand_y = (random.random())*coord_max
        G.add_node(i,x=rand_x, y=rand_y)
        coord[i] = rand_x, rand_y
        
    #Add edge from node a to b iff b has grater x and y values.
    for i in range(N):
        for j in range(N):
            if G.node[i]['x']<G.node[j]['x'] and G.node[i]['y']<G.node[j]['y']:
                G.add_edge(i,j) 
        
    #Random edge removal
    if random_removal_prob != 0:
        probabilistic_edge_removal(G,random_removal_prob)
            
    #Check this is a DAG
    if nx.is_directed_acyclic_graph(G):
        print "This is a 2D box causal DAG."
        if show_plot == True:
        #Create & show figure
#            rc.set_rc_params(figure_size = [6,6])
            xlim((0,coord_max))
            ylim((0,coord_max))
            nx.draw_networkx(G, pos=coord, alpha = 0.9, width = 0.2, node_color = 'red', with_labels=False, node_size=80)
            show()                                             
    
    return (G)
    
def random_edge_removal(G,x):
    '''
    Method to remove edge with probability p, (like WS rewiring, but removal). Loops through all edges.
    Returns: nx graph object with *some* edges removed.
    Args: Original nx graph object G, p=probability of removal of each edge.
    '''
    i=0
    while i < x:
        #Choose an edge randomly
        rand=random.randint(0,nx.number_of_edges(G)-1)
        random_edge = nx.edges(G)[rand]
        source_node = random_edge[0]
        sink_node = random_edge[1]
        #Remove this edge
        G.remove_edge(source_node,sink_node)
        #print nx.edges(G)
        i=i+1
    return(G)

def probabilistic_edge_removal(G,p):
#    int = nx.number_of_edges(G)
#    for i in range(nx.number_of_edges(G)):
    i=0
    initial_number_of_edges = nx.number_of_edges(G)
    import copy

    H= copy.deepcopy(G)


    while i <   initial_number_of_edges :
        edge = nx.edges(G)[i]
        #Introduce probabilistic removal
        rand =random.random()
        if rand <= p:
            source_node = edge[0]
            sink_node = edge[1]
            H.remove_edge(source_node,sink_node)
        i+=1

    return H

def xD_box(D,N,show_plot=True,random_removal_prob=0):
    #Creates Empty Directed graph object
    G=nx.DiGraph()
        
    #Generate random coordinates for N points
    coords_list = []
    dict_of_points = {}

    for i in range(N):
        coords_list.append([])
        for d in range(D):
            rand = random.random()
            coords_list[i].append(rand)

    for j in range(len(coords_list)):
        coords_list[j] = tuple(coords_list[j])
        dict_of_points[j] = coords_list[j]
     
    for point in dict_of_points:
        G.add_node(point)  

        

    for a in dict_of_points:
        for b in dict_of_points:
            connect_a_b = True
            if a==b:
                connect_a_b=False
            coords_a = dict_of_points[a] #this is the tuple x,y for point a
            coords_b = dict_of_points[b]
            for d in range(D):
                point_a_coord_d = coords_a[d]
                point_b_coord_d = coords_b[d]
                if point_a_coord_d < point_b_coord_d:
                    connect_a_b = False
            if connect_a_b:
                G.add_edge(a,b)

    #Random edge removal
    if random_removal_prob != 0:
        probabilistic_edge_removal(G,random_removal_prob)
    
    #Check this is a DAG
    if nx.is_directed_acyclic_graph(G):
        print "This is a ",D,"D box set DAG."

        if show_plot==True:
            if D==1:
                pass
            else:
                #Create & show figure
                    #create dictionary of t & x coordinates only, in order x,t for use in D>2 plotting, i.e. {0:[x0,t0],1:[x1,t1]...}
                xy_coords_list=[]
                xt_coords_dict = {}
#                rc.set_rc_params(figure_size = [6,6])
                for i in range(len(dict_of_points)):
                    xy_coords_list.append([])
                    for j in range(2):
                        xy_coords_list[i].append(dict_of_points[i][j])
                        xt_coords_dict[i] = xy_coords_list[i]
                xlabel("x")
                ylabel("y")
                axis([0,1,0,1])
                nx.draw_networkx(G, pos=xt_coords_dict, alpha = 1, width = 0.1, node_color = 'red', with_labels=False, node_size=15, linewidths=0.5)
#                nx.draw_networkx(G, pos=xt_coords_dict, alpha = 0.9, width = 0.2, node_color = 'red', ege_color = 'gray',with_labels=False, node_size=20,linewidths=0.5)
                savefig("2dBox.pdf")
                show() 
            
 
    else: 
        print "not a DAG?!?!"                          
    
    return (G)

class event_in_minkowski():
    """
    Class to represent an event at a random point in D-dimensional minkowski space. Includes methods that return t^2 and x^2+y^2+z^2+.... for an event.
    """
    
    def __init__(self, D):
        self.D = D
        
    def coord_value_point_n(self,n):
        coord_value_list = []
        for _ in range(self.D):
            rand = random.random()*2 - 1          #   get rid of hash if want backwards light cone / -1 to +1 coord range
            coord_value_list += {rand}
        return (coord_value_list)
    
    def t_squared(self,coord_value_list):
        t_squared = (coord_value_list[0])**2
        return (t_squared)
    
    def sum_space_squared(self,coord_value_list):
        sum_space_squared = 0
        for d in range(1,self.D):
            sum_space_squared+=(coord_value_list[d])**2
        return sum_space_squared
    
def draw_in_minkowski(G,dict_of_points):
    """
    Method to plot output in 2d for N-dim Minkowski spacetime
    """
#    rc.set_rc_params(figure_size = [6,6])

    
    #create dictionary of t & x coordinates only, in order x,t for use in D>2 plotting, i.e. {0:[x0,t0],1:[x1,t1]...}
    xt_coords_list=[]
    xt_coords_dict = {}
    for i in range(len(dict_of_points)):
        xt_coords_list.append([])
        j=1
        while j>=0:
            xt_coords_list[i].append(dict_of_points[i][j])
            j-=1
        xt_coords_dict[i] = xt_coords_list[i]
    
    #Check this is a DAG
#        if nx.is_directed_acyclic_graph(G):
        #Create & show figure
    xlim(-1,+1)
    xlabel(r'$x$')
    ylabel(r'$t$')
    ylim(-1,+1)
    
    nx.draw_networkx(G, pos=xt_coords_dict, alpha = 1, width = 0.1, node_color = 'red', with_labels=False, node_size=15, linewidths=0.5)
    savefig("minkowski.pdf")
    show() 
        
    return

def minkowski_causality(D,N,show_plot=True):
    """
    Instantiates "event_in_minkowski" to return a list of N points.
    """
    def points_in_minkowski(D,N):
           
        points_in_minkowski = []
        
        n=1
        while n<=N:
            point_n = event_in_minkowski(D)
            coords_n = point_n.coord_value_point_n(n)
            points_in_minkowski.append(coords_n)
            n+=1
        return points_in_minkowski

    good_points = points_in_minkowski(D,N)
    
    #List --> Dict as nx needs hashable object to add nodes/edges from.
    dict_of_points = {}
    for i in range(len(good_points)):
        good_points[i] = tuple(good_points[i])
        dict_of_points[i] = good_points[i]
   
    #Add nodes to empty nx graph object
    G=nx.DiGraph()
    for point in dict_of_points:
        G.add_node(point)         
    
    #Add edge (from i to j) to empty nx graph object if node j falls within the future light cone of i
    for i in range(len(dict_of_points)):
        for j in range(len(dict_of_points)):
            if i==j:
                continue
            t_separation = dict_of_points[j][0] - dict_of_points[i][0]
            space_separation=0
            for d in range(1,D):
                space_separation += (dict_of_points[i][d] - dict_of_points[j][d])
            if t_separation>=abs(space_separation):
                G.add_edge(j,i)      
            else:
                pass
                
    #Check G is a DAG, print model info
    
    if nx.is_directed_acyclic_graph(G):
        print "This is a DAG of causal relations between randomly placed events in ",D,"D Minkowski space-time."
    
    #Show plot
    if show_plot==True:
        draw_in_minkowski(G,dict_of_points)
    
    return G
    
def COd(D,N,show_plot=True):

    """
    Instantiates "event_in_minkowski" and checks if each event object is time-like to event at origin. Returns a list of N such points (NB: N inc. point @ origin).
    """
    def timelike_set(D,N):
           
        timelike_points = []
        
        def origin_coords():
            origin_coords = []
            for _ in range(D):
                coord = 0.00
                origin_coords += {coord}
            return (origin_coords)
        
        neg_extreme = origin_coords()
        neg_extreme[0]=-1

        pos_extreme = origin_coords()
        pos_extreme[0]=1
 
        n=1
        while n<=N-2:
            point_n = event_in_minkowski(D)
            coords_n = point_n.coord_value_point_n(n)
            # if point is timelike w.r.t two extremal points
            interval_wrt_neg_extreme = point_n.sum_space_squared(coords_n) - (coords_n[0]-neg_extreme[0])**2
            interval_wrt_pos_extreme = point_n.sum_space_squared(coords_n) - (coords_n[0]-pos_extreme[0])**2
            if interval_wrt_neg_extreme  < 0 and interval_wrt_pos_extreme < 0:
                timelike_points.append(coords_n)
                n+=1
                
        timelike_points.append(pos_extreme)
        timelike_points.append(neg_extreme)
         
        return timelike_points
  
    good_points = timelike_set(D,N)
    
    #List --> Dict as nx needs hashable object to add nodes/edges from.
    dict_of_points = {}
    for i in range(len(good_points)):
        good_points[i] = tuple(good_points[i])
        dict_of_points[i] = good_points[i]
    
    #Add nodes to empty nx graph object
    G=nx.DiGraph()
    for point in dict_of_points:
        G.add_node(point)               
    
    #Add edges to empty nx graph object according to time ordering of each pair of points
    for i in range(len(dict_of_points)):
        for j in range(len(dict_of_points)):
            if i==j:
                continue
            t_separation = dict_of_points[j][0] - dict_of_points[i][0]
            space_separation=0
            for d in range(1,D):
                space_separation += (dict_of_points[i][d] - dict_of_points[j][d])
            if t_separation>=abs(space_separation):
                if dict_of_points[i][0]<dict_of_points[j][0]:
                    G.add_edge(j,i)  
                else:
                    pass
    extremes = [good_points[-1], good_points[-2]]

    #Check G is a DAG, print model info
    if nx.is_directed_acyclic_graph(G):
        print "This is a network of causal relations between randomly placed events that can be causally connected to two extremal events in" ,D,"D Minkowski space-time"
    
    #Show plot
    if show_plot==True:
        draw_in_minkowski(G,dict_of_points)
    
    return [G,extremes]

def draw_lattice_model(D, N_side):
    model = models.square_lattice_model(D, N_side)
    DAG = model[0]
    #lp = dl.lpd(DAG, extremes[1], extremes[0])  # recursion depth exceeded --> loop in DAG
    #print lp[2]

    #
    #Check this is a DAG
    if nx.is_directed_acyclic_graph(DAG):
        print "lattice DAG"
    else: print "damn"
    
    coords_list = DAG.nodes()
    
    G=nx.DiGraph()

    dict_of_points = {}
    
    for j in range(len(coords_list)):
        coords_list[j] = tuple(coords_list[j])
        dict_of_points[j] = coords_list[j]
        
    for point in dict_of_points:
        G.add_node(point)  
        
    #    for d in range(D):
    #        if node_a[d] == node_b[d]+1:
    #            for j in range(D):
    #                if j != d:
    #                    if node_a[j] == node_b[j]:
    #                        return True
    #                    else: return False  
    
    for a in dict_of_points:
        for b in dict_of_points:
            connect_a_b = False
            coords_a = dict_of_points[a] #this is the tuple x,y for point a
            coords_b = dict_of_points[b]
            for d in range(2):
                point_a_coord_d = coords_a[d]
                point_b_coord_d = coords_b[d]
                if point_a_coord_d == point_b_coord_d+1:
                    for j in range(2):
                        if j != d:
                            if coords_a[j] == coords_b[j]:
                                connect_a_b = True
            if connect_a_b:
                G.add_edge(a,b)

    
    xy_coords_list=[]
    xt_coords_dict = {}
    #                rc.set_rc_params(figure_size = [6,6])
    for i in range(len(dict_of_points)):
        xy_coords_list.append([])
        for j in range(2):
            xy_coords_list[i].append(dict_of_points[i][j])
            xt_coords_dict[i] = xy_coords_list[i]
        xlabel("x")
        ylabel("y")
    nx.draw_networkx(G, pos=xt_coords_dict, alpha = 1, width = 0.1, node_color = 'red', with_labels=False, node_size=15, linewidths=0.5)
    #                nx.draw_networkx(G, pos=xt_coords_dict, alpha = 0.9, width = 0.2, node_color = 'red', ege_color = 'gray',with_labels=False, node_size=20,linewidths=0.5)
    #plt.savefig("2dBox.pdf")
    show() 
    
    return


#xD_box(3, 100)
#
##minkowski_causality(2,50)
#
##xD_box(2, 50)
#
#
#minkowski_causality(2,50)
#COd(2,75)
#
draw_lattice_model(2, 3)
##oneD_box(20,True)





