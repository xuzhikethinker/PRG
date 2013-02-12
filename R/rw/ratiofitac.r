# dataf contains column of k value 0:(length-1) and column of p(k) values labelled pk
# Uses nl2sol packake port to solve (using upper and lower bounds) if alg>0, otherwise uses default
ratiofitac <- function( datafr, E, K, pr, bfixed, alg) {
# log is natural logarithm
 kmax=max(datafr$k);
 pp = 1-pr;
 Ktilde = pr*K/pp; 
 Ebar = E/pp;
 Etilde = pr*Ebar;
 cestm1 <- Ktilde-Ebar;
 startlist <- list(afit=max(Ktilde,0.1), cfit=1+cestm1);
 lowerlist <- list(afit=1e-2,  cfit=1+cestm1*1e2)
 upperlist <- list(afit=max(10*Ktilde,2), cfit=-kmax*1.0001 )
 if (alg >0 )
 fitres <- nls( pkratio ~ (k+afit)*(bfixed+k)/((1+k)*(cfit+k))  , datafr, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 else 
 fitres <- nls( pkratio ~ (k+afit)*(bfixed+k)/((1+k)*(cfit+k))  , datafr, startlist, lower=lowerlist, upper=upperlist)
}
