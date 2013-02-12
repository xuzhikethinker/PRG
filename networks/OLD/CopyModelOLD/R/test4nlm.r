colourlist=c("black", "red", "blue", "green", "magenta", "brown");
#testtable4 <- read.table("testall.dat", header=TRUE, sep ="\t", comment.char = "")
testtable4a <- read.table("up-tt20000-tu100-te1-ni100-na100-pr0.01-mr4r9999t20000.tdatanh.dat", header=TRUE, sep ="\t", comment.char = "", fill=TRUE)

lowerlist <- list(gamma=1e-6, A=1e-10, n0=m90df[1,1])
upperlist <- list(gamma=10, A=1e+10, n0=m90df[26,1]*10)
lowerlist
upperlist

fn <- function (f2inf,f0,lam2) 

fitres <- nlm( f2 ~ A + B*(l2)^t , testtable4a , startlist, lower=lowerlist, upper=upperlist);

summary(fitres)

plot(testtable4a[,1],testtable4a[,17], col=colourlist[1])

