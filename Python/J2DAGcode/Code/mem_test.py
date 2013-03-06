import random
import os
N = 1000000

matrix=[]

for i in range(N):
    jlist = []
    for j in range(N):
        jlist.append(random.random())
    matrix.append(jlist)
    print i	

