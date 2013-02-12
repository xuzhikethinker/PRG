# Fits F2.  fitdf must have vriables fitdf$X.t for time and fitdf$F2 for F2 values
fitform <- function(fitdf,Evalue,Nvalue,prvalue) {
kavvalue=Evalue/Nvalue
#startlist <- list(lam2=1-2*prvalue/Evalue-2*(1-prvalue)/(Evalue*Evalue), dlam32=1e-6, F2inf=(1+prvalue*(kavvalue-1)) / (1+prvalue*(Evalue-1)), B=1e-3, C=1e-3 )
#lowerlist <- list(lam2=0, dlam32=0,  F2inf=0, B=0, C=0)
#upperlist <- list(lam2=1, dlam32=1,  F2inf=1, B=1, C=1)
startlist <- list(lam2=1-2*prvalue/Evalue-2*(1-prvalue)/(Evalue*Evalue), F2inf=(1+prvalue*(kavvalue-1)) / (1+prvalue*(Evalue-1)), F20=0 )
lowerlist <- list(lam2=1e-6, F2inf=1e-6, F20=0)
upperlist <- list(lam2=1-1e-6, F2inf=1-1e-6, F20=1-1e-6)
startlist
lowerlist
upperlist
#fitres <- nls( F2 ~ F2inf + (F20-F2inf)*(lam2)^X.t + C*(lam2+dlam32)^X.t , testtable4a , startlist, lower=lowerlist, upper=upperlist,  algorithm = "port");
fitres <- nls( F2 ~ F2inf + (F20-F2inf)*(lam2)^X.t , fitdf , startlist, lower=lowerlist, upper=upperlist,  algorithm = "port");
}
