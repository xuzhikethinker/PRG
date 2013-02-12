colourlist=c("black", "red", "blue", "green", "magenta", "brown");
#xmin=0.01;
#xmax=2.0;
#xlist <- xmin:xmax:0.01;
#alpha=4.0;
#gamma=1.0;
# gives list of values of Potential
Vfunction <- function(alpha, gamma, xlist) {
Vlist <- (1+xlist^alpha)^(-gamma);
}

# Gravity Function
# Gastner beta=0.5, xscale=4900km
Gfunction <- function(beta, xscale, xlist) {
Glist <- xlist^(-beta)*exp(-xlist/xscale)
}

# makes a list of data
makelist <- function(alpha, gamma, beta, xscale, xlist) {
lll <- list(x=xlist, V=Vfunction(alpha, gamma, xlist), G=Gfunction(beta, xscale, xlist) ); 
}

xlist <- seq(xmin,xmax, by=xstep);
plotlist <- makelist(alpha, gamma, beta, xscale, xlist);
i=1;
plot(plotlist$x, plotlist$V, type="l", col=colourlist[i],  xlab="distance", ylab="Potential", main="Potentials");
i=i+1;
lines(plotlist$x, plotlist$G, col=colourlist[i])
