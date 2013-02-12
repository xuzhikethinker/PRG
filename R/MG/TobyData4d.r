# This is testing for most of the plots and fits for the data of Toby Clemson 
# for leadership in Minority Game paper, as seen in his MSci report
# See TobyData4.r for current complete version.  TobyData5.r has one other plot.

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
graphTypeNumber=1

plotTypeList = c("screen","eps","pdf")
plotTypeSelected = c(1) #c(1,2,3) # list of plots required, use numbers associated with plotTypeList

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

if (length(tobyparamdata$k2est)>0) {
 k2estimateExists=TRUE 
 print("Estimates for k2 exist", quote=FALSE)
 } else {
 k2estimateExists=FALSE
 print("Estimates for k2 do NOT exist", quote=FALSE)
}

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


 k1fitVec <- array(0,nColumns-1)
 if (k2estimateExists) k2fitVec <- tobyparamdata$k2est[1:(nColumns-1)] else k2fitVec <- array(0,nColumns-1)
 k2fitVec <- #k2fitVec*1.0
 k3fitVec <- c(2.3,2.5,2.5,2.5,2.5,2.5) #array(2.5,nColumns-1)
 gfitVec  <- array(1.0,nColumns-1)
 xfitVec  <- c(15,15,18,22,22,22) #array(15.0,nColumns-1)
 normfitVec <- array(0,nColumns-1)
 
if (graphTypeNumber==1) {
 k1fitVec <- array(0,nColumns-1)
 if (k2estimateExists) k2fitVec <- tobyparamdata$k2est[1:(nColumns-1)] else k2fitVec <- array(0,nColumns-1)
 k2fitVec <- c(20,40,60,120,100,100) #k2fitVec*1.0
 k3fitVec <- c(2.3,2.5,2.5,2.5,2.5,2.5) #array(2.5,nColumns-1)
 gfitVec  <- array(1.0,nColumns-1)
 xfitVec  <- c(15,15,18,22,22,22) #array(15.0,nColumns-1)
 normfitVec <- array(0,nColumns-1)
}

ResultsList=list()

#for (ccc in 2:7) {
for (ccc in 2:nColumns) {
print(paste("===",nameList[ccc-1],"=============================================="), quote=FALSE)
 
 Etotal <- sum(tobydata[[ccc]])
 #logplist<- log(tobydata[[ccc]]/Etotal)[tobydata[[ccc]]>0]
 klist    <-        tobydata[[1]][tobydata[[ccc]]>0]
 fitkmax <- max(klist)+1
 #if (k2estimateExists) fitkmax=tobyparamdata$k2est[ccc-1]
 print(paste("Fitting between k =",fitkmin," and ",fitkmax),quote=FALSE)
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

 normfitVec[ccc-1] <- logn1
 fv=list(k1fit=k1fitVec[ccc-1], k2fit=k2fitVec[ccc-1], k3fit=k3fitVec[ccc-1], gfit=gfitVec[ccc-1], xfit=xfitVec[ccc-1], normfit=normfitVec[ccc-1])
# n=A (k+k1)^(-gamma) /(1 + (k/k2)^chi)
 yFit <- fv$normfit -  fv$gfit*log(dataFit$k+fv$k1fit) - log( 1+ (dataFit$k/fv$k2fit)^fv$xfit ) +  fv$gfit*log(1.0+fv$k1fit) + log( 1+ (1.0/fv$k2fit)^fv$xfit ) 
# n=A / ( (k/k2)^gamma + exp( ((k-k2)/k3)^chi ) )
# yFit <- fv$normfit - log( (dataFit$k/fv$k2fit)^fv$gfit +exp( ( (dataFit$k-fv$k2fit)/fv$k3fit )) ) + log( (1.0/fv$k2fit)^fv$gfit +exp( ( (1.0-fv$k2fit)/fv$k3fit ) ) )
 print("Fit values used",quote=FALSE)
 print(t(as.matrix(fv)))
 
 ResultsList[[ccc-1]] = list(x=dataPlot$k,y=dataPlot$logn,E=Etotal,logn1=logn1, name=nameList[ccc-1], 
                             xFit=dataFit$k, yFit=yFit,
                             fitvalues=fv,
                             N=tobyparamdata$N[ccc-1], p=tobyparamdata$p[ccc-1], kaverage=kaverage, 
                             xMin=min(dataPlot$k),xMax=max(dataPlot$k),yMin=min(dataPlot$logn),yMax=max(dataPlot$logn))
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
  lines(ResultsList[[iii]]$xFit, ResultsList[[iii]]$yFit-log(ResultsList[[iii]]$E), col=colourlist[iii], lty=iii)
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
  lines(ResultsList[[iii]]$xFit/(xnorm[iii]), ResultsList[[iii]]$yFit+log(ResultsList[[iii]]$xFit) +pp*log(ResultsList[[iii]]$p) , col=colourlist[iii], lty=iii)
  #lines(ResultsList[[iii]]$xFit/(xnorm[iii]), ResultsList[[iii]]$yFit-ynormList[[iii]]  , col=colourlist[iii], lty=iii)
  abline(h=0)
  abline(v=1)
  nameVector[iii]= ResultsList[[iii]]$name
 }
 # Finally add legend
 legend (x="bottomleft",y=NULL, nameVector[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}


