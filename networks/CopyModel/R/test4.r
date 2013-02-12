colourlist=c("black", "red", "blue", "green", "magenta", "brown");
#testtable4 <- read.table("testall.dat", header=TRUE, sep ="\t", comment.char = "")
testtable4a <- read.table("up-tt20000-tu100-te1-ni100-na100-pr0.01-mr4r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

fitdf <- data.frame(tt = testtable4a$X.t, F2 = testtable4a$F2);
prvalue=0.01
Evalue=100
Nvalue=100
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
fitres <- nls( F2 ~ F2inf + (F20-F2inf)*(lam2)^tt , fitdf , startlist, lower=lowerlist, upper=upperlist,  algorithm = "port");
summary(fitres)

plot(testtable4a[,1],testtable4a[,17], col=colourlist[1])
points(fitdf$tt,fitted(fitres),col=colourlist[2])

