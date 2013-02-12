hypergfpdffit <- function( dataf, kmax, E, K, pr) {
# log is natural logarithm
 dataf <- data.frame(k = dataf$k, logp = log(dataf$pk));
 pp = 1-pr;
 Ktilde = pr*K/pp;
 Ebar = E/pp;
 Etilde = pr*Ebar;
 cest <- 1-Ktilde-Ebar;
 startlist <- list(a=de, c=cest);
 lowerlist <- list(a=Ktilde*1e-2, c=cest*100)
 upperlist <- list(a=Ktilde*100, c=-1e-2 )
 b <- -kmax;
 fitres <- nls( logp ~ lgamma(1-c+a+b) + lgamma(1-b) - lgamma(a) - lgamma(1+a-c) - lgamma(1+b-c) + lgamma(k+a) + lgamma(1-c-k) - lgamma(k+1) - lgamma(1-b-k) , dataf, startlist, lower=lowerlist, upper=upperlist)
}
