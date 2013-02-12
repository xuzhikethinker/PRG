# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown");
erfcOver2 <- function(x)  pnorm(x * sqrt(2), lower = FALSE)

OSWindows=TRUE

logxOn=TRUE

graphTypeNameList = list("ER", "SF", "Ring")
#graphTypeKfitShift = c(6.6,1.8,2.7)
graphTypeKfitShift = c(6.6,1.1,-1.9)
graphTypeKfitScale = c(1.0,0.6,0.7)

graphTypeNumber=1

graphKfitShift = graphTypeKfitShift[graphTypeNumber]
graphKfitScale = graphTypeKfitScale[graphTypeNumber]
graphTypeName=graphTypeNameList[graphTypeNumber]
dataFileName = paste("MGdata/Toby",graphTypeName,".dat",sep="")
paramFileName = paste("MGdata/TobyParam",graphTypeName,".dat",sep="")
print(paste("Working with",graphTypeName))
print(paste("graphs from files",dataFileName,paramFileName), quote=FALSE)
print(paste("graph <k> shift factor ",graphKfitShift), quote=FALSE)
print(paste("graph <k> scale factor ",graphKfitScale), quote=FALSE)
 
 

#rm(s010,s250,s750,s950,s990,s999,SimonDataList)
tobydata <- read.table(dataFileName, header=TRUE);
tobyparamdata <- read.table(paramFileName, header=TRUE);
#klistall<-tobydata[[1]]

nColumns= length(tobydata)

nameListTemp=names(tobydata)
nameList=nameListTemp[2:nColumns]

fitkmin=0
alg=0
fitfunc=0
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
dataPlot <- data.frame(k=klist[klist>fitkmin], logn=lognlist[klist>fitkmin])
kaverage <- tobyparamdata$kaverage[ccc-1]
if (graphTypeNumber == 3) {
dataFit <- data.frame(k=klist[klist>fitkmin & klist != kaverage & klist != kaverage/2 ], logn=lognlist[klist>fitkmin & klist != kaverage & klist != kaverage/2 ])
} else {
dataFit <- dataPlot
}
kmax = max(dataFit$k)

k1est = 0.0 
k1min = -.99 #0.01
k1max = 10.0

k2est = kmax/4
k2min = 1.0    
k2max = kmax/2

k3est = kmax/2.0
k3min = 1.0    
k3max = kmax

gest=1.0
gmin=0.1
gmax=1.5

xest=1.0
xmax=10.0
xmin=0.5;

normest=logn1         #1.0/log(k2est);
normmin=normest-1    #normest/1000; #1.0/log(k2max);
normmax=normest+1    #normest*1000; #1.0/log(k2min);
 
 
 if (fitfunc==2) {
 startlist <- list(k1fit=k1est, k2fit=k2est, k3fit=k3est, gfit=gest, normfit=normest);
 lowerlist <- list(k1fit=k1min, k2fit=k2min, k3fit=k3min, gfit=gmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, k3fit=k3max, gfit=gmax, normfit=normmax )
 } else {
 startlist <- list(k1fit=k1est, k2fit=k2est, xfit=xest, gfit=gest, normfit=normest);
 lowerlist <- list(k1fit=k1min, k2fit=k2min, xfit=xmin, gfit=gmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, xfit=xmax, gfit=gmax, normfit=normmax )
 }
 #print(startlist)
 
 if (fitfunc==2) fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) +log(erfcOver2( (k-k2fit)/k3fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 if (fitfunc==1) fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -log(exp((k-k2fit)^xfit)+1 ) , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 if (fitfunc==0) fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-log( (k+k1fit)^gfit + exp((k-k3fit)/k2fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-gfit*log((k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist) #, lower=lowerlist, upper=upperlist, algorithm="plinear")
 #else fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")

print("------------------------------------------------", quote=FALSE)
print(paste(nameList[ccc-1], "log(n(1))=",logn1), quote=FALSE)
print(summary(fitres))

cfit <- coef(fitres)
fitvalues <- list(k1value = cfit[[1]], k2value = cfit[[2]], gammavalue = cfit[[3]], chifit = cfit[[4]], normfit = cfit[[5]] )

ResultsList[[ccc-1]] = list(x=dataPlot$k,y=dataPlot$logn,E=Etotal,logn1=logn1, name=nameList[ccc-1], 
                            xFit=dataFit$k,
                            fitvalues=fitvalues,
                            N=tobyparamdata$N[ccc-1], p=tobyparamdata$p[ccc-1], kaverage=kaverage, 
                            xMin=min(dataPlot$k),xMax=max(dataPlot$k),yMin=min(dataPlot$logn),yMax=max(dataPlot$logn),fit=fitres)
} # end for ccc
print("xxx ------------------------------------------------", quote=FALSE)
plotName = paste("Minority Game on",graphTypeName)

print("Fitting <k_s> to k_2 derived from fit", quote=FALSE)


k2vector= 1:length(ResultsList)
ksavvector= 1:length(ResultsList)
for (iii in 1:length(ResultsList)) {  
 k2vector[iii] = ResultsList[[iii]]$fitvalues$k2value
 ksavvector[iii] = ResultsList[[iii]]$kaverage
}
dffit = data.frame(ksav=ksavvector,  k2=k2vector)
lffit <- lm(k2 ~ ksav, dffit)
print(summary(lffit))


if (length(tobyparamdata$k2est)>0) k2estimateExists=TRUE else k2estimateExists=FALSE
if (k2estimateExists) {
 print("Fitting <k_s> to estimated k_2", quote=FALSE)
 dfest = data.frame(ksav=ksavvector,  k2=tobyparamdata$k2est)
 lfest <- lm(k2 ~ ksav, dfest)
 print(summary(lfest))
} else print("No estimated k_2", quote=FALSE)
 
xLabel = expression(symbol("\341") * k[s] * symbol("\361")) 
yLabel = expression( k[2] ) 
xMax=max(dffit$ksav)
yMax=max(dffit$k2)
if (k2estimateExists) yMax=max(dffit$k2,dfest$k2)
# Now plot on screen
if (OSWindows) windows() else quartz()
iii=1
plot(x=dffit$ksav,  y=dffit$k2,  xlim=c(0,xMax) , ylim=c(0,yMax) , main=plotName, sub="(Toby data)", xlab=xLabel, ylab=yLabel, pch=iii, col=colourlist[iii])
lines(x=dffit$ksav,  y=fitted(lffit), pch=iii, col=colourlist[iii])
if (k2estimateExists) {
 iii=iii+1
 points(x=dfest$ksav,  y=dfest$k2, pch=iii, col=colourlist[iii])
 lines(x=dfest$ksav,  y=fitted(lfest), pch=iii, col=colourlist[iii])
}
print("xxx ------------------------------------------------", quote=FALSE)

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
if (OSWindows) windows() else quartz()
plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=plotName, sub="(Toby data)", xlab="k", ylab="log(p(k))")
for (iii in 1:length(ResultsList)) {
 points(ResultsList[[iii]]$x,   ResultsList[[iii]]$y-log(ResultsList[[iii]]$E), pch=iii, col=colourlist[iii])
 lines(ResultsList[[iii]]$xFit, fitted(ResultsList[[iii]]$fit)-log(ResultsList[[iii]]$E), col=colourlist[iii], lty=iii)
}
# Finally add legend
legend (x="topright",y=NULL, nameList[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));


# ................................................................................

# Set x axis
#xLabel = paste("k/(",graphKfitScale,"<k_s>-",graphKfitShift,")")
graphKfitShiftAbs=abs(graphKfitShift)
xLabel = bquote(k/(.(graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  - .(graphKfitShiftAbs) ) )
if (graphKfitShift<0) xLabel = bquote(k/(.(graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  + .(graphKfitShiftAbs) ) )
#paste("k/",shiftFormula)
xnorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) xnorm[iii]= (graphKfitScale*ResultsList[[iii]]$kaverage-graphKfitShift) # ResultsList[[iii]]$fitvalues$k2value #
xMin<-ResultsList[[1]]$xMin/xnorm[1];
for (iii in 2:length(ResultsList)) xMin<-min(xMin, ResultsList[[iii]]$xMin/xnorm[iii])
xMax<-ResultsList[[1]]$xMax/xnorm[1];
for (iii in 2:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax/ResultsList[[iii]]$kaverage)

# Set y axis
#yLabel = paste("log(n(k)*p*",shiftFormula)
yLabel=bquote(log(n(k))*p*( .(graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  - .(graphKfitShiftAbs) ) ) 
if (graphKfitShift<0) yLabel=bquote(log(n(k))*p*( .(graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  + .(graphKfitShiftAbs) ) ) 
ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= -log(xnorm[iii]) -log(ResultsList[[iii]]$p)  #-log(ResultsList[[iii]]$kav) -log(ResultsList[[iii]]$p) #log(ResultsList[[iii]]$fitvalues$k2value) #log(ResultsList[[iii]]$E)
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 2:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-ynorm[1];
for (iii in 2:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])

# Now plot on screen
if (OSWindows) windows() else quartz()
plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=plotName, sub="(Toby data)", xlab=xLabel, ylab=yLabel)
for (iii in 1:length(ResultsList)) {
 points(ResultsList[[iii]]$x/(xnorm[iii]),   ResultsList[[iii]]$y-ynorm[iii], pch=iii, col=colourlist[iii])
 lines(ResultsList[[iii]]$xFit/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)-ynorm[iii], col=colourlist[iii], lty=iii)
 abline(h=0)
 abline(v=1)
}
# Finally add legend
legend (x="bottomleft",y=NULL, nameList[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));


