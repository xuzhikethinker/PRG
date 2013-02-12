# Gives a data frame of pdf 
# kmin, kmax min/max degree, E edges, K average degree, pr random attachment protestb
ddpdf <- function(kmin,kmax,E,K,pr) {
lkmin <- log(kmin);
lkmax <- log(kmax);
klist <- kmin:kmax;
pklist <- exp(rwlpdf(klist,E,K,pr))
pdfdf <- data.frame(klist, pklist); # makes a data frame of data
}
