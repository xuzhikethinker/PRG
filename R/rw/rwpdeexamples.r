colourlist=c("black", "red", "blue", "green", "magenta", "brown");
prlist = c(0.9999,0.1,1e-2,1e-3,1e-4);
EEE <- 1000;
KKK <- 4.0;
NNN <- EEE/KKK;
# pflat is the pr where the first ratio R_1 is one, roughly where the k=E gradient is zero
pflat <- 1/(1+KKK);
# psharp is the pr where second ration R_2 is one, roughly the large k end gradient is zero
psharp <- 1/(EEE+1+KKK);
# ddpdf <- function(kmin,kmax,E,K,pr)
prliststring <- paste(prlist);
pdetitle <- paste("pde E=",EEE," K=",KKK);
i <- 1;
pdell=plot(ddpdf(1,EEE,EEE,KKK,prlist[i]), type="b", log="xy" , col=colourlist[i], lty=i, pch=i, ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main=pdetitle);
for ( i in 2:length(prlist)) lines(ddpdf(1,EEE,EEE,1.99,prlist[i]),col=colourlist[i], lty=i)
for ( i in 2:length(prlist)) points(ddpdf(1,EEE,EEE,1.99,prlist[i]),col=colourlist[i], pch =i)
xxx <- 1;
yyy <- 1e-5;
legend (xxx,yyy, prliststring, col=colourlist[1:length(prlist)],lty=1:length(prlist),pch=1:length(prlist));
