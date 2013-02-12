# Fit power and exponential decay to data 
pdfit <- function( dataf, kmax, E, K, pr) {
# log is natural logarithm
 dataf <- data.frame(k = dataf$k, logp = log(dataf$pk));
 pp = 1-pr;
 Ktilde = pr*K/pp;
 Ebar = E/pp;
 Etilde = pr*Ebar;
 pd <-function(k,gam,k0){k^(-gam)*exp(-k/k0)}
 gammaest=1-Ktilde;
 k0est=-1/log(pp);
 Aest   <- 1/(sum(pd(0:kmax,gammaest,  k0est  )));
 gammalower <- -1.1;
 k0lower <- 0.1;
 Alower <- 1/(sum(pd(0:kmax,gammalower,k0lower)));
 gammaupper <- 1.1;
 k0upper <- 2*kmax;
 Aupper <- 1/(sum(pd(0:kmax,gammaupper,k0upper)));
 startlist <- list(Afit=Aest,   gammafit=gammaest,   k0fit=k0est);
 lowerlist <- list(Afit=Alower, gammafit=gammalower, k0fit=k0lower)
 upperlist <- list(Afit=Aupper, gammafit=gammaupper, k0fit=k0upper )
 b <- -kmax;
 fitres <- nls( logp ~ log(Afit)-gammafit*log(k) - k/k0fit  , dataf, startlist, lower=lowerlist, upper=upperlist,  algorithm="port")
}


