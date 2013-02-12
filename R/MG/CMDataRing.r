# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown","black", "red", "blue", "green", "magenta", "brown");

# Switch to control Windows vs Mac interfaces.
OSWindows=TRUE

logxOn=TRUE

# use CMDatRingProcess.r to organise data first

prValues=c(0.05,0.025,0.05,0.075,0.1,0.125)
nivalue=201
Nvalue=nivalue #tobyparamdata$N[ccc-1]
ksaveragevalue=40
pvalue=ksaveragevalue/(Nvalue-1)


ncolList <- seq(length=nivalue+1, from=22, by=2)
klist    <- seq(length=nivalue+1, from=0, by=1)
nList=list()
nFiles=length(rootNameList)

rootname <-  "infl_ring201k40r999t201000mr6ppm1prm0"
dataFileName = paste("CopyModeldata/",rootname,"MEAN_r.dat",sep="")
print(paste("Data from file",dataFileName), quote=FALSE)
cmdata <- read.table(dataFileName, header=TRUE, sep="\t", fill=TRUE);

nColumns= length(cmdata)

graphTypeNumber=3
graphTypeNameList = list("ER", "SF", "Ring")
graphTypeKfitShift = c(6.6,1.06,0.0)
graphKfitShift = graphTypeKfitShift[graphTypeNumber]
graphTypeName=graphTypeNameList[graphTypeNumber]
tobyDataFileName = paste("MGdata/Toby",graphTypeName,".dat",sep="")

tobydata <- read.table(tobyDataFileName, header=TRUE);
print(paste("Working with",graphTypeName))
print(paste("graphs from files",tobyDataFileName), quote=FALSE)
print(paste("graph <k> shift factor ",graphKfitShift), quote=FALSE)

ccc = 3
tobynorm <- sum(tobydata[[ccc]])
tobyklist    <-        tobydata[[1]][tobydata[[ccc]]>0 ]
tobyn1       <-        tobydata[[ccc]][klist==1]/tobynorm
tobylogplist <- log(tobydata[[ccc]]/tobynorm)[tobydata[[ccc]]>0]
tobylogn1    <-        logplist[klist==1]


fitkmin=0
alg=1
ResultsList=list()

for (ccc in 2:nColumns) {


#Nvalue=tobyparamdata$N[ccc-1]
#pvalue=tobyparamdata$p[ccc-1];
#kaverage=tobyparamdata$kaverage[ccc-1]
#intkaverage=as.integer(kaverage+0.5)


Etotal <- sum(cmdata[[ccc]])
logplist<- log(cmdata[[ccc]]/Etotal)[cmdata[[ccc]]>0]
klist    <-        cmdata[[1]][cmdata[[ccc]]>0 ]
n1       <-        cmdata[[ccc]][klist==1]
lognlist <- log(cmdata[[ccc]])[cmdata[[ccc]]>0]
logn1    <-        lognlist[klist==1]
dataFit <- data.frame(k=klist[klist>fitkmin], logn=lognlist[klist>fitkmin ])

#dataFit <- data.frame(k=klist, logp=logplist)
#dataFit <- data.frame(k=klist[klist>fitkmin], logp=logplist[klist>fitkmin])
#dataFit <- data.frame(k=klist[klist>fitkmin & klist!=intkaverage & klist!=intkaverage/2], 
#                   logn=lognlist[klist>fitkmin & klist!=intkaverage & klist!=intkaverage/2])

kmax = max(dataFit$k)

k1est = 0.0 
k1min = -.99 #0.01
k1max = 10.0

k2est = 1.0 #kmax/4
k2min = 0.5    
k2max = kmax

k3est = kmax/2.0
k3min = 1.0    
k3max = kmax

gest=0.1
gmin=-0.5
gmax=3.0

xest=1.0
xmax=10.0
xmin=0.5;

normest=logn1+(gest*(1+k1est))+ (xest/k2est)         #1.0/log(k2est);
normmin=normest-2    #normest/1000; #1.0/log(k2max);
normmax=normest+2    #normest*1000; #1.0/log(k2min);
 
 
 startlist <- list(k1fit=k1est, k2fit=k2est, gfit=gest, xfit=xest, normfit=normest);
 lowerlist <- list(k1fit=k1min, k2fit=k2min, gfit=gmin, xfit=xmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, gfit=gmax, xfit=xmax, normfit=normmax )
# startlist <- list(k1fit=k1est, k2fit=k2est, k3fit=k3est, gfit=gest, normfit=normest);
# lowerlist <- list(k1fit=k1min, k2fit=k2min, k3fit=k3min, gfit=gmin, normfit=normmin)
# upperlist <- list(k1fit=k1max, k2fit=k2max, k3fit=k3max, gfit=gmax, normfit=normmax )
 #print(startlist)
 
 #if (alg>0) 
 fitres <- nls( logn ~ normfit-gfit*log((k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-log( (k+k1fit)^gfit ) +log(erfcOver2( (k-k2fit)/k3fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-log( (k+k1fit)^gfit + exp((k-k3fit)/k2fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-gfit*log((k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist) #, lower=lowerlist, upper=upperlist, algorithm="plinear")
 #else fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist)

print("------------------------------------------------", quote=FALSE)
print(paste(nameList[ccc-1], "log(n(1))=",logn1), quote=FALSE)
#print(paste("N=",Nvalue,", p=",pvalue,", <ks>=",ksaverage), quote=FALSE)
print(rootname, quote=FALSE)
print(summary(fitres))

cfit <- coef(fitres)
fitvalues <- list(k1value = cfit[[1]], k2value = cfit[[2]], gammavalue = cfit[[3]], chifit = cfit[[4]], normfit = cfit[[5]] )
#Avalue <- exp(fitvalues$normfit)
Ascale <- exp(fitvalues$normfit)*(fitvalues$k2value^(2-fitvalues$gammavalue)-1)/((2-fitvalues$gammavalue)*Nvalue)
print(paste("scaling of A (normalisation of p(k)) = ",Ascale), quote=FALSE)

ResultsList[[ccc-1]] = list(x=dataFit$k,y=dataFit$logn,E=Etotal,logn1=logn1, name=nameList[ccc-1], 
                            fitvalues=fitvalues,
                            N=Nvalue, p=pvalue, kaverage=ksaveragevalue, 
                            xMin=min(dataFit$k),xMax=max(dataFit$k),yMin=min(dataFit$logn),yMax=max(dataFit$logn),fit=fitres,Ascale=Ascale)

#ResultsList[[ccc-1]] = list(x=dataFit$k,y=dataFit$logn,E=Etotal,logn1=logn1, name=nameList[ccc-1], 
#                            fitvalues=fitvalues,
#                            N=tobyparamdata$N[ccc-1], p=tobyparamdata$p[ccc-1], kaverage=tobyparamdata$kaverage[ccc-1], 
#                            xMin=min(dataFit$k),xMax=max(dataFit$k),yMin=min(dataFit$logn),yMax=max(dataFit$logn),fit=fitres,Ascale=Ascale)

} # end for ccc
print("------------------------------------------------", quote=FALSE)


plotName = paste("Minority Game on",graphTypeName)

#First calculate the minimum value of y axis.
xMin<-ResultsList[[1]]$xMin;
for (iii in 1:length(ResultsList)) xMin<-min(xMin, ResultsList[[iii]]$xMin)
if (logxOn) xMin = max(1,xMin)

xMax<-ResultsList[[1]]$xMax;
for (iii in 1:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax)


ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= log(ResultsList[[iii]]$E)
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 1:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-log(ResultsList[[1]]$E);
for (iii in 1:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])

# Now plot on screen
if (OSWindows) windows() else quartz()
plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=plotName, sub="(CopyModel data)", xlab="k", ylab="log(p(k))")
for (iii in 1:length(ResultsList)) {
 points(ResultsList[[iii]]$x,   ResultsList[[iii]]$y-log(ResultsList[[iii]]$E), pch=iii+1, col=colourlist[iii+1])
 lines(ResultsList[[iii]]$x, fitted(ResultsList[[iii]]$fit)-log(ResultsList[[iii]]$E), col=colourlist[iii+1], lty=iii+1)
}

points(tobyklist, tobylogplist, pch=1, col=colourlist[1])
# Finally add legend
legend (x="topright",y=NULL, nameList[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));


# ................................................................................

# Set x axis
xLabel = paste("k/(<ks>-",graphKfitShift,")",sep="")
xnorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) xnorm[iii]= ResultsList[[iii]]$kaverage-graphKfitShift # ResultsList[[iii]]$fitvalues$k2value #
xMin<-ResultsList[[1]]$xMin/xnorm[1];
for (iii in 1:length(ResultsList)) xMin<-min(xMin, ResultsList[[iii]]$xMin/xnorm[iii])
xMax<-ResultsList[[1]]$xMax/xnorm[1];
for (iii in 1:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax/ResultsList[[iii]]$kaverage)

# Set y axis
#yLabel = paste("log(n(k)*p*(<ks>-",graphKfitShift,")")
graphpPowerShift=0.85
yLabel = paste("log(n(k)*p^",graphpPowerShift,"*(<ks>-",graphKfitShift,") )",sep="")
ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= -log(xnorm[iii]) -graphpPowerShift*log(ResultsList[[iii]]$p)  #-log(ResultsList[[iii]]$kav) -log(ResultsList[[iii]]$p) #log(ResultsList[[iii]]$fitvalues$k2value) #log(ResultsList[[iii]]$E)
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 1:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-ynorm[1];
for (iii in 1:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])

# Now plot on screen
if (OSWindows) windows() else quartz()
plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=plotName, sub="(CopyModel data)", xlab=xLabel, ylab=yLabel)
for (iii in 1:length(ResultsList)) {
 points(ResultsList[[iii]]$x/(xnorm[iii]),   ResultsList[[iii]]$y-ynorm[iii], pch=iii, col=colourlist[iii])
 lines(ResultsList[[iii]]$x/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)-ynorm[iii], col=colourlist[iii], lty=iii)
}
# Finally add legend
legend (x="topright",y=NULL, nameList[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));


