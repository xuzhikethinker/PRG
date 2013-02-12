colourlist=c("black", "red", "blue", "green", "magenta", "brown");
source("SimonData.r")
rm(hypergfpdf,fit010,fit250,fit750,fit950,fit990,fit999)
source("hypergfpdf.r")
source("hypergfpdffit2.r")
rm(fit010,fit250,fit750,fit950,fit990,fit999)

#hypergfpdffit2 <- function( dataf, kmax, pr, alg) 
print("Fitting s010")
fit010 <- hypergfpdffit2(s010, 199, 0.800, 1)

print("Fitting s250")
fit250 <- hypergfpdffit2(s250, 199, 0.800, 1)

print("Fitting s750")
fit750 <- hypergfpdffit2(s750, 199, 0.362, 1)

print("Fitting s950")
fit950 <- hypergfpdffit2(s950, 199, 0.095, 1)

print("Fitting s990")
fit990 <- hypergfpdffit2(s990, 199, 0.028, 1)

print("Fitting s999")
fit999 <- hypergfpdffit2(s999, 199, 0.012, 1)

FitList <- list(fit010 ,fit250, fit750, fit950, fit990 , fit999) 

ifirst=1
ilast=length(SimonDataList)

plot(SimonDataList[[ifirst]],   type="p", log="xy" ,  col=colourlist[ifirst], pch=ifirst, xlim=c(kmin,kmax), ylim=c(1e-9,1), xlab="degree k", ylab="p(k)", main="Laird and Jensen EPL 2006 Fig 1 (ll, ac fit)")
for ( i in (ifirst+1):ilast) points(SimonDataList[[i]],  col=colourlist[i], pch = i);

for ( i in ifirst:ilast) lines(SimonDataList[[i]]$k, exp(fitted(FitList[[i]])), col=colourlist[i]);

