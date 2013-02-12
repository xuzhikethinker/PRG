colourlist=c("black", "red", "blue", "green", "magenta", "brown");
Nvalue=200;
Nm1value=Nvalue-1;
pelist=c(0.01, 0.25, 0.75, 0.95, 0.99, 0.999);
prpredictlist=c(1.99,	1.51,	0.51,	0.11,	0.03,	0.012);
prguesslist=c(1.99,	1.51,	0.51,	0.11,	0.03,	0.012);
pppredictlist=c(0,	0.242424242,	0.747474747,	0.949494949,	0.98989899,	0.998989899)
ppguesslist=c(0,	0.242424242,	0.747474747,	0.949494949,	0.98989899,	0.998989899)
alist=prguesslist/(ppguesslist);
clist=alist-Nvalue/(ppguesslist);
source("rwlpdfabc.r");
source("ddpdfabc.r");
kmin <- 1;
kmax <- Nm1value
ifirst <- 2;
ilast <- length(prguesslist);

source("SimonData.r");
plot(SimonDataList[[ifirst]],   type="p", log="xy" ,  col=colourlist[ifirst], pch=ifirst, xlim=c(kmin,kmax), ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 1 (ll, Two Step calc prediction)")
for ( i in (ifirst+1):ilast) points(SimonDataList[[i]],  col=colourlist[i], pch = i);

#points(s010, col=colourlist[1], pch = 1);
#points(s250, col=colourlist[2], pch = 2);
#points(s750, col=colourlist[3], pch = 3);
#points(s950, col=colourlist[4], pch = 4);
#points(s990, col=colourlist[5], pch = 5);
#points(s999, col=colourlist[6], pch = 6);

#p1llguess=plot(ddpdfabc(kmin,kmax,alist[ifirst],-Nm1value,clist[ifirst]), type="l", log="xy" ,  col=colourlist[i], ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 1 (ll, Two Step calc prediction)");
for ( i in (ifirst):ilast) lines(ddpdfabc(kmin,kmax,alist[i],-Nm1value,clist[i]),col=colourlist[i])

xxx <- 1;
yyy <- 1e-5;
legend (xxx,yyy, pelist[ifirst:ilast], col=colourlist[ifirst:ilast],lty=ifirst:ilast,pch=ifirst:ilast);
