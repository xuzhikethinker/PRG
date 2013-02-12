hypergfpdffit3 <- function( dataf, kmax, pr, alg) {
# log is natural logarithm
 datafit <- data.frame(k = dataf$k, logp = log(dataf$pk));
 pp=1-pr
 kactualmax = max(datafit$k)
 
 amin=1e-8
 aest=pr/pp
 amax=aest*10
 
 best <- -(kmax+kactualmax)*2
 bmax=-kactualmax-0.5
 bmin=-kmax
 
 cest <- 1+(pr-kmax)/pp;
 cmax=1-kmax*1.0001
 cmin=cest*10
 
 normest=1.0
 normmin=0.1
 normmax=10.0
 
 
 startlist <- list(afit=aest, bfit=best, cfit=cest, normfit=normest);
 #print(startlist)
 lowerlist <- list(afit=amin, bfit=bmin, cfit=cmin, normfit=normmin)
 upperlist <- list(afit=amax, bfit=bmax, cfit=cmax, normfit=normmax )
 #b <- -kmax;
 if (alg>0)
 fitres <- nls( logp ~ normfit*hypergfpdf(k,afit,bfit,cfit) , datafit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 else
 fitres <- nls( logp ~ normfit*hypergfpdf(k,afit,bfit,cfit) , datafit, startlist, lower=lowerlist, upper=upperlist)
}
