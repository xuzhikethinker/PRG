load("C:\\PRG\\R\\census\\census070817.RData")
source("C:\\PRG\\R\\census\\logbincount.r")
source("C:\\PRG\\R\\census\\powerestimate.r")
m90df <- data.frame(nn = male90hist$mids, cc = male90hist$counts);
startlist<- c(powerestimate(4,20,1,2,m90df), c0=m90df[10,1]);

# basic plot
plot(m90df, log="xy")
simplefunction <- function( k, g, A) {k^(-g)*A}
lines(c(0.005,2.0),c(simplefunction(0.005,startlist$gamma, startlist$A),simplefunction(2.0,startlist$gamma, startlist$A)));

lowerlist <- list(gamma=1e-6, A=1e-10, c0=m90df[1,1])
upperlist <- list(gamma=10, A=1e+10, c0=m90df[26,1]*10)
lowerlist
upperlist
nls( nn ~ A*cc^(-gamma) , m90df, startlist, lower=lowerlist, upper=upperlist);