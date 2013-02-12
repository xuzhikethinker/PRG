hypergfpdffit2 <- function( dataf, kmax, pr, alg) {
# log is natural logarithm
 dataf <- data.frame(k = dataf$k, logp = log(dataf$pk));
 pp=1-pr
 
 amin=1e-8
 aest=pr/pp
 amax=aest*10
 
 cest <- 1+(pr-kmax)/pp;
 cmax=1-kmax*1.0001
 cmin=cest*10
 
 startlist <- list(afit=aest, cfit=cest);
 lowerlist <- list(afit=amin, cfit=cmin)
 upperlist <- list(afit=amax, cfit=cmax )
 b <- -kmax;
 if (alg>0)
 fitres <- nls( logp ~ hypergfpdf(k,afit,b,cfit) , dataf, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 else
 fitres <- nls( logp ~ hypergfpdf(k,afit,b,cfit) , dataf, startlist, lower=lowerlist, upper=upperlist)
}
