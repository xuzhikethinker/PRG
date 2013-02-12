hypergfpdffit <- function( dataf, kmax, E, K, pr) {
# log is natural logarithm
 dataf <- data.frame(k = dataf$k, logp = log(dataf$pk));
 pp = 1-pr;
 Ktilde = pr*K/pp;
 Ebar = E/pp;
 Etilde = pr*Ebar;
 cestm1 <- Ktilde-Ebar;
 startlist <- list(afit=Ktilde, cfit=1+cestm1);
 lowerlist <- list(afit=1e-2, cfit=1+cestm1*1e2)
 upperlist <- list(afit=1.99, cfit=1-kmax*1.0001 )
 b <- -kmax;
# fitres <- nls( logp ~ lgamma(1-cfit+a+b) + lgamma(1-b) - lgamma(a) - lgamma(1+a-c) - lgamma(1+b-c) + lgamma(k+a) + lgamma(1-c-k) - lgamma(k+1) - lgamma(1-b-k) , dataf, startlist, lower=lowerlist, upper=upperlist)
 fitres <- nls( logp ~ hypergfpdf(k,afit,b,cfit) , dataf, startlist, lower=lowerlist, upper=upperlist)
}
