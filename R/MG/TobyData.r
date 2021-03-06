# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown");

logxOn=TRUE

#rm(s010,s250,s750,s950,s990,s999,SimonDataList)
tobydata <- read.table("MGdata/TobyER.dat", header=TRUE);
tobyparamdata <- read.table("MGdata/TobyParamER.dat", header=TRUE);
#klistall<-tobydata[[1]]

nColumns= length(tobydata)

nameListTemp=names(tobydata)
nameList=nameListTemp[2:nColumns]

fitkmin=0
alg=1
ResultsList=list()

for (ccc in 2:nColumns) {



Etotal <- sum(tobydata[[ccc]])
#logplist<- log(tobydata[[ccc]]/Etotal)[tobydata[[ccc]]>0]
klist    <-        tobydata[[1]][tobydata[[ccc]]>0]
n1       <-        tobydata[[ccc]][klist==1]
lognlist <- log(tobydata[[ccc]])[tobydata[[ccc]]>0]
logn1    <-        lognlist[klist==1]
#dataFit <- data.frame(k=klist, logp=logplist)
#dataFit <- data.frame(k=klist[klist>fitkmin], logp=logplist[klist>fitkmin])
dataFit <- data.frame(k=klist[klist>fitkmin], logn=lognlist[klist>fitkmin])

k1est = 0.0 
k1min = -.99 #0.01
k1max = 10.0

k2est = max(dataFit$k) 
k2min = k2est/10;
k2max = k2est*10;

gest=1.0
gmin=0.5
gmax=1.5

xest=6.0
xmax=10.0
xmin=0.5;

normest=logn1         #1.0/log(k2est);
normmin=normest-1    #normest/1000; #1.0/log(k2max);
normmax=normest+1    #normest*1000; #1.0/log(k2min);
 
 
 startlist <- list(k1fit=k1est, k2fit=k2est, gfit=gest, xfit=xest, normfit=normest);
 #print(startlist)
 lowerlist <- list(k1fit=k1min, k2fit=k2min, gfit=gmin, xfit=xmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, gfit=gmax, xfit=xmax, normfit=normmax )
 
 #if (alg>0) 
 fitres <- nls( logn ~ normfit-gfit*log((k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist) #, lower=lowerlist, upper=upperlist, algorithm="plinear")
 #else fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist)

print("------------------------------------------------", quote=FALSE)
print(paste(nameList[ccc-1], "log(n(1))=",logn1), quote=FALSE)
print(summary(fitres))

cfit <- coef(fitres)
fitvalues <- list(k1value = cfit[[1]], k2value = cfit[[2]], gammavalue = cfit[[3]], chifit = cfit[[4]], normfit = cfit[[5]] )

ResultsList[[ccc-1]] = list(x=dataFit$k,y=dataFit$logn,E=Etotal,logn1=logn1, name=nameList[ccc-1], 
                            fitvalues=fitvalues,
                            N=tobyparamdata$N[ccc-1], p=tobyparamdata$p[ccc-1], kaverage=tobyparamdata$kaverage[ccc-1], 
                            xMin=min(dataFit$k),xMax=max(dataFit$k),yMin=min(dataFit$logn),yMax=max(dataFit$logn),fit=fitres)
} # end for ccc
print("------------------------------------------------", quote=FALSE)


#First calculate the minimum value of y axis.
xMin<-ResultsList[[1]]$xMin;
for (iii in 2:length(ResultsList)) xMin<-min(xMin, ResultsList[[iii]]$xMin)
if (logxOn) xMin = max(1,xMin)

xMax<-ResultsList[[1]]$xMax;
for (iii in 2:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax)


ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= log(ResultsList[[iii]]$E)
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 2:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-log(ResultsList[[1]]$E);
for (iii in 2:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])

# Now plot on screen
windows()
plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main="Minority Game on ER", sub="(Toby data)", xlab="k", ylab="log(p(k))")
for (iii in 1:length(ResultsList)) {
 points(ResultsList[[iii]]$x,   ResultsList[[iii]]$y-log(ResultsList[[iii]]$E), pch=iii, col=colourlist[iii])
 lines(ResultsList[[iii]]$x, fitted(ResultsList[[iii]]$fit)-log(ResultsList[[iii]]$E), col=colourlist[iii], lty=iii)
}
# Finally add legend
legend (x="topright",y=NULL, nameList[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));


# ................................................................................
xnorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) xnorm[iii]= ResultsList[[iii]]$kaverage-6.6# ResultsList[[iii]]$fitvalues$k2value #
xMin<-ResultsList[[1]]$xMin/xnorm[1];
for (iii in 2:length(ResultsList)) xMin<-min(xMin, ResultsList[[iii]]$xMin/xnorm[iii])
xMax<-ResultsList[[1]]$xMax/xnorm[1];
for (iii in 2:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax/ResultsList[[iii]]$kaverage)

ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= -log(xnorm[iii]) -log(ResultsList[[iii]]$p)  #-log(ResultsList[[iii]]$kav) -log(ResultsList[[iii]]$p) #log(ResultsList[[iii]]$fitvalues$k2value) #log(ResultsList[[iii]]$E)
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 2:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-ynorm[1];
for (iii in 2:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])

# Now plot on screen
windows()
plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main="Minority Game on ER", sub="(Toby data)", xlab="k/(<k>-6.6)", ylab="log(n(k)*p*(<k>-6.6))")
for (iii in 1:length(ResultsList)) {
 points(ResultsList[[iii]]$x/(xnorm[iii]),   ResultsList[[iii]]$y-ynorm[iii], pch=iii, col=colourlist[iii])
 lines(ResultsList[[iii]]$x/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)-ynorm[iii], col=colourlist[iii], lty=iii)
}
# Finally add legend
legend (x="topright",y=NULL, nameList[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));


