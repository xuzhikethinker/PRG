import math
def f(d):
    top = math.gamma(d+1) * math.gamma(d/2)
    bottom = 4 * math.gamma(3*d/2)
    return (top / bottom)
i = -1.0

file = './gamma.txt'
file_thing = open(file, 'w')

while i<20.0:
    i += 0.0001
    ff = f(i)
    file_thing.write(str(i) + '\t' + str(ff) + '\n')
    
