# Given explicit parameters, plots ratio and data.
colourlist=c("black", "red", "blue", "green", "magenta", "brown", "brown");
source("SimonData.r")
source("ratiodataframe.r")
s010r <- ratiodataframe(s010);
s250r <- ratiodataframe(s250);
s750r <- ratiodataframe(s750);
s950r <- ratiodataframe(s950);
s990r <- ratiodataframe(s990);
s999r <- ratiodataframe(s999);


plot(s250r$k,s250r$pkr, type="p",  col=colourlist[1], xlim=c(0,200),  xlab="degree k", ylab="p(k+1)/p(k)", main="Laird and Jensen EPL 2006 Fig 1 (hand fit)");
#lines(s250r$k,fitted(fit250), col=colourlist[1]);

points(s750r$k,s750r$pkr,  col=colourlist[2] , pch=2)
#lines(s750r$k,fitted(fit750), col=colourlist[2])

points(s950r$k,s950r$pkr,  col=colourlist[3] , pch=3)
#lines(s950r$k,fitted(fit950), col=colourlist[3])

points(s990r$k,s990r$pkr,  col=colourlist[4] , pch=4)
#lines(s990r$k,fitted(fit990), col=colourlist[4])

points(s999r$k,s999r$pkr,  col=colourlist[5] , pch=5)
#lines(s999r$k,fitted(fit999), col=colourlist[5])

points(s010r$k,s010r$pkr,  col=colourlist[6] , pch=6)

pkratio <- function(k,pList) {(k+pList$a)*(k+pList$b)/((k+pList$c)*(k+1));};

#s250rpList <- list(a=9.6,b=-796,c=-4578);
s250rpList <- list(a=9.6,b=-199,c=-4578);
lines(s250r$k,pkratio(s250r$k,s250rpList), col=colourlist[1]);

s750rpList <- list(a=1.05,b=-796,c=-1181);
lines(s750r$k,pkratio(s750r$k,s750rpList), col=colourlist[2])

s950rpList <- list(a=0.18,b=-796,c=-861);
lines(s950r$k,pkratio(s950r$k,s950rpList),col=colourlist[3])

s990rpList <- list(a=0.016,b=-796,c=-802);
lines(s990r$k,pkratio(s990r$k,s990rpList), col=colourlist[4])

s999rpList <- list(a=0.022,b=-198.6,c=-199.7);
lines(s999r$k,pkratio(s999r$k,s999rpList), col=colourlist[5])

s010rpList <- list(a=56, b=-45, c=-1263);
lines(s010r$k,pkratio(s010r$k,s010rpList), col=colourlist[6]);
