# Fit power and exponential decay to data 
pdfit2 <- function( dataf, pr, alg) {
# log is natural logarithm
 kmax=length(dataf$k)
 df <- data.frame(k = dataf$k[2:kmax], logp = log(dataf$pk[2:kmax]));
 pp = 1-pr;

 gammaest=1-pr/pp;
 gammalower <- -1.1;
 gammaupper <- 1.1;

 k0est=kmax;
 k0lower <- 0.1;
 k0upper <- 2*kmax;

 Aest   <- dataf$pk[[2]]*exp(1/k0est);
 Alower <- dataf$pk[[2]]*exp(1/k0upper);
 Aupper <- dataf$pk[[2]]*exp(1/k0lower);

 startlist <- list(Afit=Aest,   gammafit=gammaest,   k0fit=k0est);
 lowerlist <- list(Afit=Alower, gammafit=gammalower, k0fit=k0lower)
 upperlist <- list(Afit=Aupper, gammafit=gammaupper, k0fit=k0upper )

 if (alg>0 )
  fitres <- nls( logp ~ log(Afit)-gammafit*log(k) - k/k0fit  , df, startlist, lower=lowerlist, upper=upperlist,  algorithm="port")
 else
  fitres <- nls( logp ~ log(Afit)-gammafit*log(k) - k/k0fit  , df, startlist, lower=lowerlist, upper=upperlist)

}

