'''
I've changed the models so they return both the DAG and coords of the extremes to fit in with longest path etc. James: COd below is the Minkowski model we've been discussing in meetings, with extremes at (-1,0,0,0...) and (+1,0,0,0...) It's long (but not slow). I'll tidy it up at some point.
'''
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

def random_coords_list(D,length):
    #Returns a list of D length lists of random numbers in [0,1]
    coords_list = []
    for i in range(length):
        coords_list.append([])
        for _ in range(D):
            rand = random.random()
            coords_list[i].append(rand)
    return coords_list

def minkowski_check(node_a, node_b, D):
    #Check whether node_a and node_b are causally connected
    dtime = node_a[0] - node_b[0]
    dtime2 = dtime**2
    
    dspace2 = 0.0
    for d in range(D-1):
        # Find spatial separation
        dspace2 += (node_a[d+1] - node_b[d+1])**2
    
    if dtime>0:
        if (dtime2 > dspace2):
            return True
        else: return False
    else: return False

def box_check(node_a, node_b, D):
    if node_a==node_b:
        return False
    for d in range(D):
        if node_a[d] < node_b[d]:
            return False
    return True

def box_model(D, N, prob_of_adding_edge=1.0):
    #Creates Empty Directed graph object
    G=nx.DiGraph()
    #Generate random coordinates for N-2 points in D dimensions
    coords_list = random_coords_list(D,N-2)
    
    bottom_corner = []
    top_corner = []
    for _ in range(D):
        bottom_corner.append(0.0)
        top_corner.append(1.0)
    coords_list.append(top_corner)
    coords_list.append(bottom_corner)
    
    for j in range(len(coords_list)):
        coords_list[j] = tuple(coords_list[j])
    
    for point in coords_list:
        G.add_node(point)

    # Add edges if nodes are causally connected
    for node_a in coords_list:
        for node_b in coords_list:
            if box_check(node_a, node_b, D):
                rand=random.random()
                if rand <= prob_of_adding_edge:
                    G.add_edge(node_a,node_b)

    extremes = [coords_list[-1], coords_list[-2]]

    #Add ordering attribute
    for node in G.nodes():
        birthday = sum(node)
        G.node[node]['birthday'] = birthday


    #Check this is a DAG
    #    if nx.is_directed_acyclic_graph(G):
    ##        print "This is a ",D,"D", causality ,"DAG."
    #
    #    else:
    #        print "not a DAG?!?!"
    #
    return [G, extremes]

def COd(D,N,prob_of_adding_edge=1.0):
    #Create empty nx graph object
    G=nx.DiGraph()
    
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
    
    #List --> list of tuples as nx needs hashable object to add nodes/edges from.
    for point in range(len(good_points)):
        good_points[point] = tuple(good_points[point])

    for point in good_points:
        G.add_node(point)

    #Add edges to empty nx graph object according to time ordering of each pair of points
    for i in range(len(good_points)):
        for j in range(len(good_points)):
            if i==j:
                continue
            t_separation = good_points[j][0] - good_points[i][0]
            space_separation=0
            for d in range(1,D):
                space_separation += (good_points[i][d] - good_points[j][d])
            if t_separation>=abs(space_separation):
                if good_points[i][0]<good_points[j][0]:
                    rand=random.random()
                    if rand <= prob_of_adding_edge:
                        G.add_edge(good_points[j],good_points[i])
                else:
                    pass

    extremes = [good_points[-1], good_points[-2]]

    #    Add ordering attribute
    for node in G.nodes():
        birthday = node[0]
        print birthday
        G.node[node]['birthday'] = birthday




    #Check G is a DAG, print model info
    #    if nx.is_directed_acyclic_graph(G):

    #        print "This is a network of causal relations between randomly placed events that can be causally connected to two extremal events in" ,D,"D Minkowski space-time"

    return [G, extremes]

def probabilistic_edge_removal(G,p):
    
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


def random_edge_removal(G,p):
    '''
        Method to remove edge with probability p, (like WS rewiring, but removal). Loops through all edges.
        Returns: nx graph object with *some* edges removed.
        Args: Original nx graph object G, p=probability of removal of each edge.
        '''
    import copy
    import random
    initial_number_of_edges = nx.number_of_edges(G)
    H= copy.deepcopy(G)
    
    while nx.number_of_edges(H) > p*initial_number_of_edges:
        #Choose an edge randomly
        rand=random.randint(0,nx.number_of_edges(H)-1)
        random_edge = nx.edges(H)[rand]
        source_node = random_edge[0]
        sink_node = random_edge[1]
        #Remove this edge
        H.remove_edge(source_node,sink_node)
    #print nx.edges(G)
    
    return(H)










