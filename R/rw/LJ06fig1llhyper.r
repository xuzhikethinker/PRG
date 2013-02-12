colourlist=c("black", "red", "blue", "green", "magenta", "brown");
pelist=c(0.01, 0.25, 0.75, 0.95, 0.99, 0.999);
ppguesslist=c(0.006, 0.2, 0.638, 0.905, 0.972 , 0.988);
prlist=1-ppguesslist;
alist=c()

source("rwlpdf.r");
source("ddpdf.r");
kmax <- 199;
klist<-1:kmax;
i <- 1;
p1llguess=plot(hypergfpdf(klist,alist[i],-kmax,clist[i]), type="l", log="xy" ,  col=colourlist[i], ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 1 (ll, Hyper)");
for ( i in 2:length(prlist)) lines(ddpdf(1,199,199,1.99,prlist[i]),col=colourlist[i])
source("SimonData.r");
for ( i in 1:length(SimonDataList)) points(SimonDataList[[i]],  col=colourlist[i], pch = i);
#points(s010, col=colourlist[1], pch = 1);
#points(s250, col=colourlist[2], pch = 2);
#points(s750, col=colourlist[3], pch = 3);res 
#points(s950, col=colourlist[4], pch = 4);
#points(s990, col=colourlist[5], pch = 5);
#points(s999, col=colourlist[6], pch = 6);
xxx <- 1;
yyy <- 1e-5;
legend (xxx,yyy, prlist, col=colourlist[1:length(prlist)],lty=1:length(prlist),pch=1:length(prlist));
