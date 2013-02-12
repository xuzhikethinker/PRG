# Gives a data frame of pdf for the case of single edge simple networks
# kmin, kmax min/max degree, E edges, K average degree, pr random attachment protestb
# Simple network means that this is for <k>=K=2E/N where N is number of vertices
seddpdf <- function(kmax,E,K,pr) {
kmin <- 0;
lkmin <- log(kmin);
lkmax <- log(kmax);
klist <- kmin:kmax;

pklist <- exp(rwlpdf(klist,E,K,pr))
pdfdf <- data.frame(klist, pklist); # makes a data frame of data
}
