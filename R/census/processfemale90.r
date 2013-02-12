#rm(list=ls(all=TRUE))
f90 <- read.table("dist.female.first");
source("C:\\PRG\\R\\census\\logbincount.r")
source("C:\\PRG\\R\\census\\powerestimate.r")
nbins=17;
f90hist <- logbincount(f90[,2],nbins);
f90df <- data.frame(nn = f90hist$mids, cc = f90hist$counts);
startlist<- c(powerestimate(5,nbins-5,1,2,f90df), n0=f90df[10,1]);

# basic plot
plot(f90df, log="xy")
simplefunction <- function( k, g, A) {k^(-g)*A}
lines(c(0.005,2.0),c(simplefunction(0.005,startlist$gamma, startlist$A),simplefunction(2.0,startlist$gamma, startlist$A)));

lowerlist <- list(gamma=1e-6, A=1e-10, n0=f90df[1,1])
upperlist <- list(gamma=10, A=1e+10, n0=f90df[nbins,1]*10)
lowerlist
upperlist
fitres <- nls( cc ~ A*nn^(-gamma)*exp(-nn/n0) , f90df, startlist, lower=lowerlist, upper=upperlist);
summary(fitres)
points(f90df$nn,fitted(fitres),col="red")