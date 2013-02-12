# Gives a data frame of pdf 
# kmin, kmax min/max degree, a,b,c, hypergeometric coef
ddpdfabc <- function(kmin,kmax,a,b,c) {
lkmin <- log(kmin);
lkmax <- log(kmax);
klist <- kmin:kmax;
pklist <- exp(rwlpdfabc(klist,a,b,c))
pdfdf <- data.frame(klist, pklist); # makes a data frame of data
}
