import powerlaw
# 5.22.1 Continuous distributions p681 scipy manual
from scipy.stats import lognorm
from numpy import rint
sdln=lognorm.rvs(1.3,loc=0,scale=10,size=10)
print "lognormal float data", sdln[1:5]
lnresults = powerlaw.distribution_fit(sdln, distribution='lognormal', discrete=False)
print lnresults

sdlnint=rint(sdln).astype(int)
print "lognormal int data", sdlnint[1:5]
lnintresults = powerlaw.distribution_fit(sdlnint, distribution='lognormal')
#lnintresults = powerlaw.distribution_fit(sdlnint, distribution='lognormal', discrete=True)
print lnintresults

