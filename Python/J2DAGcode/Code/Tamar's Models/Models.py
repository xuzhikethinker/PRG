'''
    Created on Nov 2, 2012
    @author: TamarLoach
    '''
import sys
sys.path.reverse() #required for matplotlib drawing package on my laptop - path is messed up
from matplotlib.pyplot import *
import networkx as nx
import random
import math
import ArrowsCurvesEtc as fancy

'''
    Returns a Box Set style 1D networkx graph object.
    Args: N=number of nodes
    show_plot=boolean, set as True to show figure
    random_removal=number of edges to randomly remove
    line=boolean, set as True to display nodes in straight line with curved edges (better for small N),
    False to show in circle (better for large N)
    '''
def oneD_box(N,show_plot=False,random_removal_prob=0,line=False):
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
        nx.draw_circular(G, alpha = 0.9, width = 0.4, node_color = 'red')
        #Draw nodes in circle, with straight edges
        show() # display figure
        return

    def plot_1D_linear(G):
        pos = {}
        number_of_nodes = len(nx.nodes(G))
        for node in range(number_of_nodes):
            pos[node] = node,0
        ax=gca()
        fancy.FancyDrawing().draw_network(G,pos,ax)
        axis('equal')
        show()
        return

    #Check this is a DAG
    if nx.is_directed_acyclic_graph(G):
        print "This is a 1D box causal DAG."
        #Create & show figure
        if show_plot == True:
            if line == True:#Draw nodes in straight line, with curved edges, if False, time goes in a circle
                plot_1D_linear(G)
            else:
                plot_1D_circular(G)# display figure
    else:
        print "This is completely wrong"

    return (G)

'''
    Returns a Box Set style 2D networkx graph object, with randomly sprinkled nodes.
    Args: N=number of nodes
    show_plot=boolean, set as True to show figure
    random_removal=number of edges to randomly remove
    '''
#Function to  If second argument is True a figure is displayed.
def twoD_box(N,show_plot=False,random_removal_prob=0):
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
            xlim((0,coord_max))
            ylim((0,coord_max))
            nx.draw_networkx(G, pos=coord, alpha = 0.9, width = 0.2, node_color = 'red', with_labels=False, node_size=80)
            show()

    return (G)

'''
    Returns the input networkx graph object with specified number of nodes removed randomly
    Args: G should be a networkx graph object
    x=number of nodes to randomly remove
    '''
def random_edge_removal(G,x):
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

'''
    Method to remove edge with probability p, (like WS rewiring, but removal). Loops through all edges.
    Returns: nx graph object with *some* edges removed.
    Args: Original nx graph object G, p=probability of removal of each edge.
    '''
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

'''
    Returns a Box Set style XD networkx graph object, with randomly sprinkled nodes.
    Args: N=number of nodes
    show_plot=boolean, set as True to show figure - NB doesn't work for 1D yet - use oneD_box method for this
    random_removal=number of edges to randomly remove
    '''
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
                if point_a_coord_d > point_b_coord_d:
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
                for i in range(len(dict_of_points)):
                    xy_coords_list.append([])
                    for j in range(2):
                        xy_coords_list[i].append(dict_of_points[i][j])
                        xt_coords_dict[i] = xy_coords_list[i]
                xlabel("x")
                ylabel("y")
                nx.draw_networkx(G, pos=xt_coords_dict, alpha = 0.9, width = 0.2, node_color = 'red', with_labels=False, node_size=20)
                #                nx.draw_networkx(G, pos=xt_coords_dict, alpha = 0.9, width = 0.2, node_color = 'red', ege_color = 'gray',with_labels=False, node_size=20,linewidths=0.5)
                show()
    #                savefig("blah.pdf") #Uncomment to save figure - NB need to comment out plt.show()

    else:
        print "not a DAG?!?!"

    return (G)

'''
    c=1 throughout
    D=2 --> 1 time dim. & 1 space dim.
    
    '''

"""
    Class to represent an event at a random point in D-dimensional minkowski space. Includes methods that return t^2 and x^2+y^2+z^2+.... for an event.
    """
class event_in_minkowski():
    
    def __init__(self, D):
        self.D = D
    
    def coord_value_point_n(self,n):
        coord_value_list = []
        for _ in range(self.D):
            rand = random.random()*2 - 1
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

"""
    Method to plot output in 2d for N-dim Minkowski spacetime
    """
def draw_in_minkowski(G,dict_of_points):
    
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
    xlim((-1.01,+1.01))
    xlabel("x")
    ylabel("t")
    ylim((-1.01,+1.01))
    nx.draw_networkx(G, pos=xt_coords_dict, alpha = 0.9, width = 0.2, node_color = 'red', with_labels=False, node_size=20)
    show()
    #            plt.savefig("minkowski.pdf") #Uncomment to save figure - NB need to comment out plt.show()

    return

def minkowski_causality(D,N,show_plot=False):
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
    print nx.is_directed_acyclic_graph(G)

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
                G.add_edge(i,j)
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
        print origin_coords()
        
        def neg_extreme():
            neg_extreme = origin_coords()
            neg_extreme[0]=-1
            return neg_extreme
        
        def pos_extreme():
            neg_extreme = origin_coords()
            neg_extreme[0]=1
            return neg_extreme
        
        
        
        n=1
        while n<=N-2:
            point_n = event_in_minkowski(D)
            coords_n = point_n.coord_value_point_n(n)
            t_squared_n = point_n.t_squared(coords_n)
            sum_space_squared_n = point_n.sum_space_squared(coords_n)
            if t_squared_n > sum_space_squared_n:
                if coords_n[0]<0:
                    coords_n[0] += 1
                    timelike_points.append(coords_n)
                    n+=1
                elif coords_n[0]>0:
                    coords_n[0] -= 1
                    timelike_points.append(coords_n)
                    n+=1
            else:
                pass
    
        timelike_points.insert(0,pos_extreme()) # makes first item in list event at origin
        timelike_points.insert(0,neg_extreme())
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
                    
                    
                    G.add_edge(i,j)
                
                else:
                    pass

    #Check G is a DAG, print model info
    if nx.is_directed_acyclic_graph(G):
        print "This is a network of causal relations between randomly placed events to an event at the origin in ",D,"D Minkowski space-time."

    #Show plot
    if show_plot==True:
        draw_in_minkowski(G,dict_of_points)

    return G



'''
    Barabasi Albert model - mainly based on networkx model but this returns a DAG.
    (http://networkx.lanl.gov/reference/generated/networkx.generators.random_graphs.barabasi_albert_graph.html)
    '''
def BA(N, m0):
    
    def _random_subset(seq,m0):
        """ Returns m0 unique elements from seq.
            """
        targets=set()
        while len(targets)<m0:
            x=random.choice(seq) # returns a random element from seq
            targets.add(x)
        return targets
    
    if m0 < 1 or  m0 >= N:
        return
    #Add m0 initial nodes
    #    G=nx.empty_graph(m0)
    G=nx.DiGraph()
    for node in range(m0):
        G.add_node(node)
    #Target nodes for new edges, returns a list of m0 elements
    targets=list(range(m0))
    print targets
    #List of existing nodes, with nodes repeated once for each adjacent edge they posess
    repeated_nodes=[]
    # Start adding the other N-m0 nodes. The first node is m0.
    source=m0
    while source<N:
        # Add edges to m0 nodes from the source.
        G.add_edges_from(zip([source]*m0,targets))
        # Add one node to the list for each new edge just created.
        repeated_nodes.extend(targets)
        # And the new node "source" has m0 edges to add to the list.
        repeated_nodes.extend([source]*m0)
        # Now choose m0 unique nodes from the existing nodes
        # Pick uniformly from repeated_nodes (preferential attachement)
        targets = _random_subset(repeated_nodes,m0)
        source += 1
    return G



