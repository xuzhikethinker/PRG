#rm(list=ls(all=TRUE))
m90 <- read.table("dist.male.first");
source("C:\\PRG\\R\\census\\logbincount.r")
source("C:\\PRG\\R\\census\\powerestimate.r")
nbins=25;
m90hist <- logbincount(m90[,2],nbins);
m90df <- data.frame(nn = m90hist$mids, cc = m90hist$counts);
startlist<- c(powerestimate(5,nbins-5,1,2,m90df), n0=m90df[10,1]);

# basic plot
plot(m90df, log="xy")
simplefunction <- function( k, g, A) {k^(-g)*A}
lines(c(0.005,2.0),c(simplefunction(0.005,startlist$gamma, startlist$A),simplefunction(2.0,startlist$gamma, startlist$A)));

lowerlist <- list(gamma=1e-6, A=1e-10, n0=m90df[1,1])
upperlist <- list(gamma=10, A=1e+10, n0=m90df[26,1]*10)
lowerlist
upperlist
fitres <- nls( cc ~ A*nn^(-gamma)*exp(-nn/n0) , m90df, startlist, lower=lowerlist, upper=upperlist);
summary(fitres)
points(m90df$nn,fitted(fitres),col="red")