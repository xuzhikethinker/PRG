colourlist=c("black", "red", "blue", "green", "magenta", "brown");
source("SimonData.r")
rm(hypergfpdf,fit250,fit750,fit950,fit990,fit999)
source("hypergfpdf.r")
source("hypergfpdffit.r")
rm(fit250,fit750,fit950,fit990,fit999)
fit250 <- hypergfpdffit(s250, 199, 398, 1.99, 0.8)
fit750 <- hypergfpdffit(s750, 199, 398, 1.99, 0.362)
fit950 <- hypergfpdffit(s750, 199, 398, 1.99, 0.095)
fit990 <- hypergfpdffit(s990, 199, 398, 1.99, 0.028)
fit999 <- hypergfpdffit(s999, 199, 398, 1.99, 0.012)

plot(s250$k,log(s250$pk), type="p", log="x", col=colourlist[1], xlim=c(1,200), ylim=c(-10,0), xlab="degree k", ylab="ln(p(k))", main="Laird and Jensen EPL 2006 Fig 1 ");
#lines(s250$k,fitted(fit250), col=colourlist[1]);

points(s750$k,log(s750$pk),  col=colourlist[2] , pch=2)
#lines(s750$k,fitted(fit750), col=colourlist[2])

points(s950$k,log(s950$pk),  col=colourlist[3] , pch=3)
#lines(s950$k,fitted(fit950), col=colourlist[3])

points(s990$k,log(s990$pk),  col=colourlist[4] , pch=4)
#lines(s990$k,fitted(fit990), col=colourlist[4])

points(s999$k,log(s999$pk),  col=colourlist[5] , pch=5)
#lines(s999$k,fitted(fit999), col=colourlist[5])

lines(s250$k,fitted(fit250), col=colourlist[1]);
lines(s750$k,fitted(fit750), col=colourlist[2])
lines(s950$k,fitted(fit950), col=colourlist[3])
lines(s990$k,fitted(fit990), col=colourlist[4])
lines(s999$k,fitted(fit999), col=colourlist[5])

