from scipy import stats
from scipy.stats import lognorm
rrr=lognorm.rvs(10,loc=0,scale=2,size=1000)
print rrr[1:10]
print "log normal fit", lognorm.fit(rrr,5,loc=0,scale=3)
rrr[1:10]
from numpy import rint
from numpy import around
ppp = around(rrr)
print ppp[1:10]
print lognorm.fit(ppp,5,loc=0,scale=3)

