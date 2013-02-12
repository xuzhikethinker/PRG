colourlist=c("black", "red", "blue", "green", "magenta", "brown", "brown");
source("SimonData.r")
source("ratiodataframe.r")
s010r <- ratiodataframe(s010);
s250r <- ratiodataframe(s250);
s750r <- ratiodataframe(s750);
s950r <- ratiodataframe(s950);
s990r <- ratiodataframe(s990);
s999r <- ratiodataframe(s999);

source("ratiofit.r")
rm(fit010,fit250,fit750,fit950,fit990,fit999)
fit010 <- ratiofit(s010r, 398, 1.99, 0.8, 1)
fit250 <- ratiofit(s250r, 398, 1.99, 0.8, 1)
fit750 <- ratiofit(s750r, 398, 1.99, 0.362, 1)
fit950 <- ratiofit(s950r, 398, 1.99, 0.095, 1)
fit990 <- ratiofit(s990r, 398, 1.99, 0.028, 1)
fit999 <- ratiofit(s999r, 398, 1.99, 0.012, 0)

plot(s250r$k,s250r$pkr, type="p",  col=colourlist[1], xlim=c(0,200),  xlab="degree k", ylab="p(k+1)/p(k)", main="Laird and Jensen EPL 2006 Fig 1 (auto fit)");
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

lines(s250r$k,fitted(fit250), col=colourlist[1]);
lines(s750r$k,fitted(fit750), col=colourlist[2])
lines(s950r$k,fitted(fit950), col=colourlist[3])
lines(s990r$k,fitted(fit990), col=colourlist[4])
lines(s999r$k,fitted(fit999), col=colourlist[5])

lines(s010r$k,fitted(fit010), col=colourlist[6]);
