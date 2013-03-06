import math


#Reid's formula for the function of the dimension found when two chains are counted
#Redundant now, as just take k=2 with the f(d,k) formula below
def f_2(d):
    top = float(math.gamma(d+1.0)) * float(math.gamma(0.5*d))
    bottom = 4.0 * float(math.gamma(1.5*d))
    return (top / bottom)


# Problem with either coding or the equation I am trying to implement
#All found in the Reid, mainifold dimension paper
def f(d, k):
    delta = float(0.5*(d)) #make correction to Reid's fomula, which had delta = (d+1)/2
    top1 = float(math.gamma(delta)) * float(math.gamma(2*delta))
    top2 = math.gamma(2*delta + 1)
    top = top1 * (top2 ** (k-1))
    bottom = float((2**(k-1))) * float(k) * float(math.gamma(k * delta)) * float(math.gamma( (k+1)*delta ))
    return float((top/bottom))

j = 2 # initalise the value of k
while j<=5: #create dimension values for 2-chains, 3-chains, ...
    print j
    file = './f_of_d_%d.txt' %j     #create outfile titled to indicate that this is f(d) for 'k' chains
    file_thing = open(file, 'w')
    
    i = -1.0 #initialise the value of dimension
    while i<20.0: #output the value of f(d) for all dimensions up to this value
        i += 0.0001
        ff = f(i, j)
        file_thing.write(str(i) + '\t' + str(ff) + '\n')
    j += 1
    