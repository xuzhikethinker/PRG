# This produces most of the plots and fits for the data of Toby Clemson 
# for leadership in Minority Game paper, as seen in his MSci report

# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown");
erfcOver2 <- function(x)  pnorm(x * sqrt(2), lower = FALSE)

OSWindows=TRUE # false for Mac OS
if (OSWindows) screenType="Windows" else screenType="Mac" 
print(paste("Screen type",screenType), quote=FALSE)

useFittedk2Values=FALSE

publicationON=FALSE # plots ready for publication
if (publicationON) {
 pubOnShort="pub"
 print("Plots for publication", quote=FALSE) 
} else {
 pubOnShort="dev"
 print("Plots for development", quote=FALSE)
}

graphTypeNameList = list("ER", "SF", "Ring")
graphTypeNumber=2

plotTypeList = c("screen","eps","pdf")
plotTypeSelected = c(1,2,3) # list of plots required, use numbers associated with plotTypeList

logxOn=TRUE



if (useFittedk2Values) {
 # based on fitted k2 values
 k2CollapseType="Fitted k2 values"
 k2CollapseTypeShort="k2fit"
 graphTypeKfitShift = c(-3.9,0.95,0.5)
 graphTypeKfitScale = c(0.62,0.18,0.5)
} else {
 # based on estimated k2 values
 k2CollapseType="Estimated k2 values"
 k2CollapseTypeShort="k2est"
 graphTypeKfitShift = c(-3.9,1.4,0.5)
 graphTypeKfitScale = c(0.8,0.57,0.5)
}
print(paste("Collapse <k_s> factors based on",k2CollapseType), quote=FALSE)

graphKfitShift = graphTypeKfitShift[graphTypeNumber]
graphKfitScale = graphTypeKfitScale[graphTypeNumber]
graphTypeName=graphTypeNameList[graphTypeNumber]
dataFileName = paste("MGdata/Toby",graphTypeName,".dat",sep="")
paramFileName = paste("MGdata/TobyParam",graphTypeName,".dat",sep="")
outputFileNameRoot = paste("leadership",graphTypeName,sep="")
print(paste("Working with",graphTypeName),quote=F)
print(paste("graphs from files",dataFileName,paramFileName), quote=FALSE)
print(paste("Collapse <k_s> factors based on",k2CollapseType), quote=FALSE)
print(paste("graph <k_s> shift factor ",graphKfitShift), quote=FALSE)
print(paste("graph <k_s> scale factor ",graphKfitScale), quote=FALSE)
 
 

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
if (graphTypeNumber==1) fitfunc=0
if (graphTypeNumber==2) fitfunc=3
if (graphTypeNumber==3) fitfunc=0
 if (fitfunc==3) {
  print("TEST Fitting using power law modified",quote=F)
  }
if (fitfunc==2) {
  print("Fitting using power law modified by erfc heaviside function",quote=F)
  }
 if (fitfunc==1) {
  print("Fitting using power law modified by Fermi-Dirac function",quote=F)
  }
 if (fitfunc==0) {
  print("Fitting using power law multiplied by exponential",quote=F)
 } 


ResultsList=list()

for (ccc in 2:nColumns) {
print(paste("===",nameList[ccc-1],"=============================================="), quote=FALSE)
 
 Etotal <- sum(tobydata[[ccc]])
 #logplist<- log(tobydata[[ccc]]/Etotal)[tobydata[[ccc]]>0]
 klist    <-        tobydata[[1]][tobydata[[ccc]]>0]
 fitkmax <- max(klist)+1
 #if (k2estimateExists) fitkmax=tobyparamdata$k2est[ccc-1]
 print(paste("fitting between k =",fitkmin," and ",fitkmax),quote=FALSE)
 n1       <-        tobydata[[ccc]][klist==1]
 lognlist <- log(tobydata[[ccc]])[tobydata[[ccc]]>0]
 logn1    <-        lognlist[klist==1]
 #dataFit <- data.frame(k=klist, logp=logplist)
 #dataFit <- data.frame(k=klist[klist>fitkmin & klist<fitkmax], logp=logplist[klist>fitkmin & klist<fitkmax])
 dataPlot <- data.frame(k=klist[klist>fitkmin & klist<fitkmax], logn=lognlist[klist>fitkmin & klist<fitkmax])
 kaverage <- tobyparamdata$kaverage[ccc-1]
 if (graphTypeNumber == 3) { # exclude special points for ring case
 dataFit <- data.frame(k=klist[klist>fitkmin  & klist<fitkmax & klist != kaverage & klist != kaverage/2 ], logn=lognlist[klist>fitkmin & klist != kaverage & klist != kaverage/2 ])
 } else {
 dataFit <- dataPlot
 }
 kmax = max(dataFit$k)


 k1est = 0.0 
 k1min = -.99 #0.01
 k1max = 10.0

 k2est = kmax/4.0
 if (k2estimateExists) k2est=tobyparamdata$k2est[ccc-1]
 k2min = k2est/2.0 # 1.0    
 k2max = k2est*2 #kmax

 k3est = 1.0 #kmax/10.0
 k3min = 0.1    
 k3max = kmax

 gest=1.0
 gmin=0.5
 gmax=1.5

 xest=1.0
 xmax=1.1
 xmin=0.9;

 normest=logn1         #1.0/log(k2est);
 normmin=normest-1    #normest/1000; #1.0/log(k2max);
 normmax=normest+1    #normest*1000; #1.0/log(k2min);
  
  
 if (fitfunc==-1) {
 startlist <- list(k1fit=k1est, k2fit=k2est, k3fit=k3est, xfit=xest, gfit=gest, normfit=normest);
 lowerlist <- list(k1fit=k1min, k2fit=k2min, k3fit=k3min, xfit=xmin, gfit=gmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, k3fit=k3max, xfit=xmax, gfit=gmax, normfit=normmax )
 } 
 if (fitfunc==-1) {
 startlist <- list(k2fit=k2est, k3fit=k3est, xfit=xest, gfit=gest, normfit=normest);
 lowerlist <- list(k2fit=k2min, k3fit=k3min, xfit=xmin, gfit=gmin, normfit=normmin)
 upperlist <- list(k2fit=k2max, k3fit=k3max, xfit=xmax, gfit=gmax, normfit=normmax )
 } 
 if (fitfunc==2 ) {
 startlist <- list(k1fit=k1est, k2fit=k2est, k3fit=k3est, gfit=gest, normfit=normest);
 lowerlist <- list(k1fit=k1min, k2fit=k2min, k3fit=k3min, gfit=gmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, k3fit=k3max, gfit=gmax, normfit=normmax )
 }
 if (fitfunc==0 | fitfunc==1 ) {
 startlist <- list(k1fit=k1est, k2fit=k2est, xfit=xest, gfit=gest, normfit=normest);
 lowerlist <- list(k1fit=k1min, k2fit=k2min, xfit=xmin, gfit=gmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, xfit=xmax, gfit=gmax, normfit=normmax )
 }
 if (fitfunc==-1) {
 startlist <- list(k2fit=k2est, gfit=gest, normfit=normest);
 lowerlist <- list(k2fit=k2min, gfit=gmin, normfit=normmin)
 upperlist <- list(k2fit=k2max, gfit=gmax, normfit=normmax )
 }
 if (fitfunc==3) {
 startlist <- list(k2fit=k2est, normfit=normest);
 lowerlist <- list(k2fit=k2min, normfit=normmin)
 upperlist <- list(k2fit=k2max, normfit=normmax )
 }
 if (fitfunc==-1) {
 startlist <- list(k2fit=k2est, xfit=xest, gfit=gest, normfit=normest);
 lowerlist <- list(k2fit=k2min, xfit=xmin, gfit=gmin, normfit=normmin)
 upperlist <- list(k2fit=k2max, xfit=xmax, gfit=gmax, normfit=normmax )
 }
 #print(startlist)
 
 if (fitfunc==3) {
  print("TEST Fitting",quote=F)
  #ksigma=1.0
#  fitres <- nls( logn ~ normfit -gfit*log(k2fit) -log( (k/k2fit)^gfit +exp( ( (k-k2fit) )^xfit ) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
   fitres <- nls( logn ~ normfit + log( 1.0 + exp(  (1.0-k2fit) ) ) -log( k + exp(  (k-k2fit) ) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==2) {
  print("Fitting using power law modified by erfc heaviside function",quote=F)
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) +log(erfcOver2( (k-k2fit)/k3fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==1) {
  print("Fitting using power law modified by Fermi-Dirac function",quote=F)
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -log(exp((k-k2fit)^xfit)+1 ) , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==0) {
  print("Fitting using power law multiplied by exponential",quote=F)
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 } 
 #fitres <- nls( logn ~ normfit-log( (k+k1fit)^gfit + exp((k-k3fit)/k2fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-gfit*log((k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist) #, lower=lowerlist, upper=upperlist, algorithm="plinear")
 #else fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")

 print(paste(nameList[ccc-1], "log(n(1))=",logn1), quote=FALSE)
 print(summary(fitres))

# cfit <- coef(fitres)
# fitvalues <- list(k1value = cfit[[1]], k2value = cfit[[2]], gammavalue = cfit[[3]], chifit = cfit[[4]], normfit = cfit[[5]] )
 fitvalues <- coef(fitres)

 ResultsList[[ccc-1]] = list(x=dataPlot$k,y=dataPlot$logn,E=Etotal,logn1=logn1, name=nameList[ccc-1], 
                             xFit=dataFit$k,
                             fitvalues=fitvalues,
                             N=tobyparamdata$N[ccc-1], p=tobyparamdata$p[ccc-1], kaverage=kaverage, 
                             xMin=min(dataPlot$k),xMax=max(dataPlot$k),yMin=min(dataPlot$logn),yMax=max(dataPlot$logn),fit=fitres)
} # end for ccc
print("xxx ------------------------------------------------", quote=FALSE)
plotTitle = paste("Minority Game on",graphTypeName)
plotSubTitle = "(Toby data)"
print(paste(plotTitle,plotSubTitle,"Fitting <k_s> to k_2 derived from fit"), quote=FALSE)




# ................................................................................
print("xxx ------------------------------------------------", quote=FALSE)
print("Plot p(k) vs k",quote=F)

#First calculate the minimum value of y axis.
xMin<-ResultsList[[1]]$xMin;
for (iii in 2:length(ResultsList)) xMin<-min(xMin, ResultsList[[iii]]$xMin)
if (logxOn) xMin = max(1,xMin)

xMax<-ResultsList[[1]]$xMax;
for (iii in 2:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax)

# Now plot p(k)vs k
ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= log(ResultsList[[iii]]$E)
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 2:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-log(ResultsList[[1]]$E);
for (iii in 2:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])


plotName="pk"
for (plotType in plotTypeSelected) {
 if (plotType==1) {if (OSWindows) windows() else quartz()}
 if (plotType==2) {#eps plot
       epsFileName<- paste(outputFileNameRoot,plotName,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       #postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=7.5, width=11, pointsize=8)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       }
 if (plotType==3) {#plots pdf file
       pdfFileName<-paste(outputFileNameRoot,plotName,".pdf",sep="");
       print(paste("pdf plotting",pdfFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=7.5, width=11, pointsize=8)
       }
if (publicationON) { 
 mainTitle=NULL
 subTitle=NULL
 } else {
 mainTitle=plotTitle
 subTitle=plotSubTitle
 }  
 plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab="k", ylab="log(p(k))")
 for (iii in 1:length(ResultsList)) {
  points(ResultsList[[iii]]$x,   ResultsList[[iii]]$y-log(ResultsList[[iii]]$E), pch=iii, col=colourlist[iii])
  lines(ResultsList[[iii]]$xFit, fitted(ResultsList[[iii]]$fit)-log(ResultsList[[iii]]$E), col=colourlist[iii], lty=iii)
  }
 # Finally add legend
 legend (x="topright",y=NULL, nameList[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}



# .................................................................
plotName=paste("pkcollapsed2",k2CollapseTypeShort,pubOnShort,sep="")
print("Plot collapsed k.p(k) vs k",quote=F)

pp=1.0 # 1.2 works well for SF
print(paste("--- p power is",pp),quote=F)


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
for (iii in 2:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax/xnorm[iii])

# Set y axis
yLabel=bquote(log(n(k))*p*k )  
ynormList = list()
for (iii in 1:length(ResultsList)) ynormList[[iii]]= -log(ResultsList[[iii]]$x) -pp*log(ResultsList[[iii]]$p)  #-log(ResultsList[[iii]]$kav) -log(ResultsList[[iii]]$p) #log(ResultsList[[iii]]$fitvalues$k2value) #log(ResultsList[[iii]]$E)
yMin<- +1e99
for (iii in 1:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$y-ynormList[[iii]])
yMax<- -1e99
for (iii in 1:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$y-ynormList[[iii]])

# Now plot 
for (plotType in plotTypeSelected) {
 if (plotType==1) {if (OSWindows) windows() else quartz()}
 if (plotType==2) {#eps plot
       epsFileName<- paste(outputFileNameRoot,plotName,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       #postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=7.5, width=11, pointsize=8)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       }
 if (plotType==3) {#plots pdf file
       pdfFileName<-paste(outputFileNameRoot,plotName,".pdf",sep="");
       print(paste("pdf plotting",pdfFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=7.5, width=11, pointsize=8)
       }
if (publicationON) { 
 mainTitle=NULL
 subTitle=NULL
 } else {
 mainTitle=paste(plotTitle, k2CollapseType)
 subTitle="(Toby data)"
 }  
# plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab=xLabel, ylab=yLabel)
 plot(x=NULL,  y=NULL, type="n", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab=xLabel, ylab=yLabel)
 nameVector=1:length(ResultsList)
 for (iii in 1:length(ResultsList)) {
  points(ResultsList[[iii]]$x/(xnorm[iii]),   ResultsList[[iii]]$y-ynormList[[iii]], pch=iii, col=colourlist[iii])
  lines(ResultsList[[iii]]$xFit/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)+log(ResultsList[[iii]]$xFit) +pp*log(ResultsList[[iii]]$p) , col=colourlist[iii], lty=iii)
  abline(h=0)
  abline(v=1)
  nameVector[iii]= ResultsList[[iii]]$name
 }
 # Finally add legend
 legend (x="bottomleft",y=NULL, nameVector[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}


