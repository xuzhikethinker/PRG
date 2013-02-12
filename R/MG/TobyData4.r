# This produces most of the plots and fits for the data of Toby Clemson 
# for leadership in Minority Game paper, as seen in his MSci report
print("***************************************************************************",quote=F) 
print("This produces most of the plots and fits for the data of Toby Clemson",quote=F) 
print(" for leadership in Minority Game paper, as seen in Toby Clemson MSci report",quote=F)

# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown");
legendPosVec=c("bottomleft","bottomright","topleft","topright")
erfcOver2 <- function(x)  pnorm(x * sqrt(2), lower = FALSE)

OSWindows=TRUE # false for Mac OS
if (OSWindows) screenType="Windows" else screenType="Mac" 
print(paste("Screen type",screenType), quote=FALSE)

useFittedk2Values=TRUE

publicationON=TRUE # plots ready for publication

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
plotTypeSelected = c(1,2,3) # list of plots required, use numbers associated with plotTypeList

logxOn=TRUE



graphTypeName=graphTypeNameList[graphTypeNumber]
dataFileName = paste("MGdata/Toby",graphTypeName,".dat",sep="")
paramFileName = paste("MGdata/TobyParam",graphTypeName,".dat",sep="")
outputFileNameRoot = paste("leadership",graphTypeName,sep="")
print(paste("Working with",graphTypeName))
print(paste("graphs from files",dataFileName,paramFileName), quote=FALSE)
 
 

#rm(s010,s250,s750,s950,s990,s999,SimonDataList)
tobydata <- read.table(dataFileName, header=TRUE);
tobyparamdata <- read.table(paramFileName, header=TRUE);
#klistall<-tobydata[[1]]

nColumns= length(tobydata)

nameListTemp=names(tobydata)
nameList=nameListTemp[2:nColumns]
nameLongList=2:nColumns

if (length(tobyparamdata$k2est)>0) {
 k2estimateExists=TRUE 
 print("Estimates for k2 exist", quote=FALSE)
 } else {
 k2estimateExists=FALSE
 useFittedk2Values=TRUE
 print("Estimates for k2 do NOT exist, forcing use of k2 fitted values for collapse <k_s> factors", quote=FALSE)
}

if (useFittedk2Values) {
 # based on fitted k2 values
 k2CollapseType="Fitted k2 values"
 k2CollapseTypeShort="k2fit"
} else {
 # based on estimated k2 values
 k2CollapseType="Estimated k2 values"
 k2CollapseTypeShort="k2est"
}
print(paste("Collapse <k_s> factors based on",k2CollapseType), quote=FALSE)



fitkmin=0

alg=0

fitFuncVec=c(4,4,1)
fitFuncTypeVec=c("power law multiplied by exponential",
                 "power law modified by Fermi-Dirac function",
                 "power law modified by erfc heaviside function",
                 "two power laws",
                 "exponetial")
                 
fitfunc=fitFuncVec[graphTypeNumber]
fitFuncType=fitFuncTypeVec[fitfunc]
print(paste("Fitting using",fitFuncType),quote=F)

# if (fitfunc==3) {
#  print("Fitting using two power laws",quote=F)
#  }
# if (fitfunc==2) {
#  print("Fitting using power law modified by erfc heaviside function",quote=F)
#  }
# if (fitfunc==1) {
#  print("Fitting using power law modified by Fermi-Dirac function",quote=F)
#  }
# if (fitfunc==0) {
#  print("Fitting using power law multiplied by exponential",quote=F)
# } 


print("====================================================================", quote=FALSE)
ResultsList=list()

for (ccc in 2:nColumns) {
 print(paste("---",graphTypeName,nameList[ccc-1],"--------------------------------------------------"), quote=FALSE)

 Etotal <- sum(tobydata[[ccc]])
 #logplist<- log(tobydata[[ccc]]/Etotal)[tobydata[[ccc]]>0]
 klist    <-        tobydata[[1]][tobydata[[ccc]]>0]
 fitkmax <- max(klist)+1
 #if (k2estimateExists) fitkmax=tobyparamdata$k2est[ccc-1]
 print(paste("fitting between k =",fitkmin," and ",fitkmin),quote=FALSE)
 n1       <-        tobydata[[ccc]][klist==1]
 lognlist <- log(tobydata[[ccc]])[tobydata[[ccc]]>0]
 logn1    <-        lognlist[klist==1]
 print(paste("log(n(1))=",logn1), quote=FALSE)
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
 k1min = -.5 #0.01
 k1max = 0.5
 if (graphTypeNumber==3) { k1est = 0.0; k1min = -0.9; k1max = 2.5 }

 k2est = kmax/4
 k2min = 1.0    
 k2max = kmax-5
# if (graphTypeNumber==3 && ) { k1est = 0.0; k1min = -0.9; k1max = 2.5 }

 k3est = 10.0 #kmax/10.0
 k3min = 0.001    
 k3max = kmax

 gest=1.0
 gmin=0.5
 gmax=1.5
 if (graphTypeNumber==3) { gest=0.5;  gmin=0.1; gmax=1.0; }

 xest=6.0
 if (graphTypeNumber==1) {xest  <- c(15,15,18,25,22)[ccc-1] ; }
 xmax=xest+6.0; 
 xmin=max(xest-6.0,0.5);


 normest=logn1        #1.0/log(k2est);
 normmin=normest-1    #normest/1000; #1.0/log(k2max);
 normmax=normest+1    #normest*1000; #1.0/log(k2min);

#if (graphTypeNumber==1) {
# k2fitVec <- c(20,40,60,120,100,100)[ccc-1]; k2min=max(k2est/2,1.0); k2max=min(k2est*1.5,kmax-10); #k2fitVec*1.0
# k3fitVec <- c(2.3,2.5,2.5,2.5,2.5,2.5) #array(2.5,nColumns-1)
# gest=1.0; gmin=0.5; gmax=1.5
# xest  <- c(15,15,18,22,22,22)[ccc-1] ; xmax=xest+5; xmin=max(xest-5.0,0.5);
#}  
  
  
 if (fitfunc==5) {
 startlist <- list(k2fit=k2est, xfit=xest, normfit=normest);
 lowerlist <- list(k2fit=k2min, xfit=xmin, normfit=normmin)
 upperlist <- list(k2fit=k2max, xfit=xmax, normfit=normmax )
 } 
 if (fitfunc==3) {
 startlist <- list(k1fit=k1est, k2fit=k2est, k3fit=k3est, gfit=gest, normfit=normest);
 lowerlist <- list(k1fit=k1min, k2fit=k2min, k3fit=k3min, gfit=gmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, k3fit=k3max, gfit=gmax, normfit=normmax )
 } 
 if (fitfunc<3 | fitfunc==4) {
 startlist <- list(k1fit=k1est, k2fit=k2est, xfit=xest, gfit=gest, normfit=normest);
 lowerlist <- list(k1fit=k1min, k2fit=k2min, xfit=xmin, gfit=gmin, normfit=normmin)
 upperlist <- list(k1fit=k1max, k2fit=k2max, xfit=xmax, gfit=gmax, normfit=normmax )
 }
 #print(startlist)
 
 if (fitfunc==5) {
  print("Fitting using exponential",quote=F)
  fitres <- nls( logn ~ normfit -(k/k2fit)^xfit + (1.0/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==4) {
  print("Fitting using two power laws",quote=F)
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -log(1+(k/k2fit)^xfit ) +gfit*log(1.0+k1fit) +log(1+(1.0/k2fit)^xfit )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==3) {
  print("Fitting using power law modified by erfc heaviside function",quote=F)
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) +log(erfcOver2( (k-k2fit)/k3fit) ) + gfit*log(1.0+k1fit) - log(erfcOver2( (1.0-k2fit)/k3fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==2) {
  print("Fitting using power law modified by Fermi-Dirac function",quote=F)
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -log(exp((k-k2fit)^xfit)+1 ) , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==1) {
  print("Fitting using power law multiplied by exponential",quote=F)
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 } 
 #fitres <- nls( logn ~ normfit-log( (k+k1fit)^gfit + exp((k-k3fit)/k2fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-gfit*log((k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist) #, lower=lowerlist, upper=upperlist, algorithm="plinear")
 #else fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")

 if (k2estimateExists) print(paste("Estimated k2 value",tobyparamdata$k2est[ccc-1]),quote=F)
 print(summary(fitres))

# cfit <- coef(fitres)
# fitvalues <- list(k1value = cfit[[1]], k2value = cfit[[2]], gammavalue = cfit[[3]], chifit = cfit[[4]], normfit = cfit[[5]] )
fitvalues <- as.list(coef(fitres))

 nameLongList[ccc-1] = paste("N=",tobyparamdata$N[ccc-1],", p=",tobyparamdata$p[ccc-1],sep="")
 ResultsList[[ccc-1]] = list(x=dataPlot$k,y=dataPlot$logn,E=Etotal,logn1=logn1, name=nameList[ccc-1], nameLong=nameLongList[ccc-1],
                             xFit=dataFit$k,
                             fitvalues=fitvalues,
                             N=tobyparamdata$N[ccc-1], p=tobyparamdata$p[ccc-1], kaverage=kaverage, 
                             xMin=min(dataPlot$k),xMax=max(dataPlot$k),yMin=min(dataPlot$logn),yMax=max(dataPlot$logn),fit=fitres)
} # end for ccc

print("=============================================================", quote=FALSE)
plotTitle = paste("Minority Game on",graphTypeName)
plotSubTitle = paste("Toby data, fit using ",fitFuncType)
print(paste(plotTitle,plotSubTitle,"Fitting <k_s> to k_2 derived from fit"), quote=FALSE)

k2vector= 1:length(ResultsList)
ksavvector= 1:length(ResultsList)
for (iii in 1:length(ResultsList)) {  
 k2vector[iii] = ResultsList[[iii]]$fitvalues$k2fit
 ksavvector[iii] = ResultsList[[iii]]$kaverage
}
dffit = data.frame(ksav=ksavvector,  k2=k2vector)
lffit <- lm(k2 ~ ksav, dffit)
print(summary(lffit))


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


# Now plot k2 vs <k[s]>
plotName=paste("k2ksav",pubOnShort,sep="")
k2ksavnameList=c("Fitted","Estimated")
nnn=1
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
iii=1

plot(x=dffit$ksav,  y=dffit$k2,  xlim=c(0,xMax) , ylim=c(0,yMax) , main=mainTitle, sub=subTitle, xlab=xLabel, ylab=yLabel, pch=iii, col=colourlist[iii])
lines(x=dffit$ksav,  y=fitted(lffit), pch=iii, col=colourlist[iii])
if (k2estimateExists & !useFittedk2Values) {
 iii=iii+1
 nnn=2
 points(x=dfest$ksav,  y=dfest$k2, pch=iii, col=colourlist[iii])
 lines(x=dfest$ksav,  y=fitted(lfest), pch=iii, col=colourlist[iii])
 }
 
 # Finally add legend if more than one line
 if (iii>1) legend (x="topleft",y=NULL, k2ksavnameList, col=colourlist[1:nnn],lty=1:nnn,pch=1:nnn);
 
 if (plotType>1) dev.off(which = dev.cur())
} # for plottype


if (useFittedk2Values) {
 # based on fitted k2 values
 graphKfitShift = coef(lffit)[1]
 graphKfitScale = coef(lffit)[2]
} else {
 # based on estimated k2 values
 graphKfitShift = coef(lfest)[1]
 graphKfitScale = coef(lfest)[2]
}
print(paste(k2CollapseType,"graph <k_s> shift factor ",graphKfitShift), quote=FALSE)
print(paste(k2CollapseType,"graph <k_s> scale factor ",graphKfitScale), quote=FALSE)

# ................................................................................
print("================================================================", quote=FALSE)
print(paste("Plot p(k) vs k, fits using",fitFuncType),quote=F)

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


plotName=paste("pk",pubOnShort,sep="")
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
 nameVector=1:length(ResultsList)
 plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab="k", ylab="log(p(k))")
 for (iii in 1:length(ResultsList)) {
  points(ResultsList[[iii]]$x,   ResultsList[[iii]]$y-log(ResultsList[[iii]]$E), pch=iii, col=colourlist[iii])
  lines(ResultsList[[iii]]$xFit, fitted(ResultsList[[iii]]$fit)-log(ResultsList[[iii]]$E), col=colourlist[iii], lty=iii)
  nameVector[iii]= ResultsList[[iii]]$nameLong
  }
 xtextPos=sqrt(xMax*xMin) #0.5*log(xMax*xMin)
 ytextPos=yMax #*0.9+yMin*0.1
 posNumber=1 # below coord
 #print(paste("x y text positions",xtextPos,ytextPos),quote=F)
 text (x=xtextPos, y =ytextPos , pos=posNumber, labels = graphTypeName, adj = NULL, cex = 1.5,)
 # Finally add legend
 legend (x="bottomleft",y=NULL, nameVector[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}

# Now plot n(k)/n(1) vs k Anghel et al style.
print("xxx ------------------------------------------------", quote=FALSE)
print("Plot n(k)/n(1) vs k Anghel et al style",quote=F)
plotName=paste("pkp1",pubOnShort,sep="")


ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= ResultsList[[iii]]$y[1]
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 2:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-ynorm[1];
for (iii in 2:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])

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
 nameVector=1:length(ResultsList) 
 plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab="k", ylab="log(p(k)/p(1))")
 for (iii in 1:length(ResultsList)) {
  #print(paste("*****", ResultsList[[iii]]$y[1],ynorm[iii], "*****"),quote=F)
  points(ResultsList[[iii]]$x,   ResultsList[[iii]]$y-ynorm[iii], pch=iii, col=colourlist[iii])
  #lines(ResultsList[[iii]]$xFit, fitted(ResultsList[[iii]]$fit)-ynorm[iii], col=colourlist[iii], lty=iii)
  nameVector[iii]= ResultsList[[iii]]$nameLong
  }
 xtextPos=sqrt(xMax*xMin) #0.5*log(xMax*xMin)
 ytextPos=yMax #*0.9+yMin*0.1
 posNumber=1 # below coord
 #print(paste("x y text positions",xtextPos,ytextPos),quote=F)
 text (x=xtextPos, y =ytextPos , pos=posNumber, labels = graphTypeName, adj = NULL, cex = 1.5,)
 # Finally add legend
 legend (x="bottomleft",y=NULL, nameVector[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}


# ................................................................................
print("xxx ------------------------------------------------", quote=FALSE)
print(paste("Plot collapsed p(k) vs k, fits using",fitFuncType),quote=F)

# Set x axis
#xLabel = paste("k/(",round(graphKfitScale, digits=2),"<k_s>-",round(graphKfitShift, digits=2),")")
graphKfitShiftAbs=round(abs(graphKfitShift), digits=2)
xLabel = bquote(k/(.(round(graphKfitScale, digits=2))*symbol("\341") * k[s] * symbol("\361")  + .(graphKfitShiftAbs) ) )
if (graphKfitShift<0) xLabel = bquote(k/(.(round(graphKfitScale, digits=2))*symbol("\341") * k[s] * symbol("\361")  - .(graphKfitShiftAbs) ) )
#paste("k/",shiftFormula)
xnorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) xnorm[iii]= (graphKfitScale*ResultsList[[iii]]$kaverage-graphKfitShift) # ResultsList[[iii]]$fitvalues$k2fit 
xMin<-ResultsList[[1]]$xMin/xnorm[1];
for (iii in 2:length(ResultsList)) xMin<-min(xMin, ResultsList[[iii]]$xMin/xnorm[iii])
xMax<-ResultsList[[1]]$xMax/xnorm[1];
for (iii in 2:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax/xnorm[iii])

# Set y axis
#yLabel = paste("log(n(k)*p*",shiftFormula)
yLabel=bquote(log(n(k))*p*( .(graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  - .(graphKfitShiftAbs) ) ) 
if (graphKfitShift<0) yLabel=bquote(log(n(k))*p*( .(graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  + .(graphKfitShiftAbs) ) ) 
ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= -log(xnorm[iii]) -log(ResultsList[[iii]]$p)  #-log(ResultsList[[iii]]$kav) -log(ResultsList[[iii]]$p) #log(ResultsList[[iii]]$fitvalues$k2fit) #log(ResultsList[[iii]]$E)
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 2:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-ynorm[1];
for (iii in 2:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])

# Now plot 
#plotName=paste("pkcollapsed",k2CollapseTypeShort,sep="")
plotName=paste("pkcollapsed",k2CollapseTypeShort,pubOnShort,sep="")
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
  subTitle=plotSubTitle
  }  
 nameVector=1:length(ResultsList) 
 plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab=xLabel, ylab=yLabel)
 for (iii in 1:length(ResultsList)) {
  points(ResultsList[[iii]]$x/(xnorm[iii]),   ResultsList[[iii]]$y-ynorm[iii], pch=iii, col=colourlist[iii])
  lines(ResultsList[[iii]]$xFit/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)-ynorm[iii], col=colourlist[iii], lty=iii)
  nameVector[iii]= ResultsList[[iii]]$nameLong
 }
 abline(h=0)
 abline(v=1)
 xtextPos=sqrt(xMax*xMin) #0.5*log(xMax*xMin)
 ytextPos=yMax #*0.9+yMin*0.1
 #print(paste("x y text positions",xtextPos,ytextPos),quote=F)
 text (x=xtextPos, y =ytextPos , pos=1, labels = graphTypeName, adj = NULL, cex = 1.5,)
 # Finally add legend
 legend (x="bottomleft",y=NULL, nameVector[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}


# .................................................................
plotName=paste("pkcollapsed2",k2CollapseTypeShort,pubOnShort,sep="")
print(paste("Plot collapsed k.p(k) vs k, fits using",fitFuncType),quote=F)

# Set x axis as before

# Set y axis
yLabel=bquote(log(n(k)*p*k) )  
ynormList = list()
for (iii in 1:length(ResultsList)) ynormList[[iii]]= -log(ResultsList[[iii]]$x) -log(ResultsList[[iii]]$p)  #-log(ResultsList[[iii]]$kav) -log(ResultsList[[iii]]$p) #log(ResultsList[[iii]]$fitvalues$k2fit) #log(ResultsList[[iii]]$E)
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
 subTitle=plotSubTitle
 }  
 plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab=xLabel, ylab=yLabel)
# plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=paste(plotTitle, k2CollapseType), sub="(Toby data)", xlab=xLabel, ylab=yLabel)
 nameVector=1:length(ResultsList)
 for (iii in 1:length(ResultsList)) {
  points(ResultsList[[iii]]$x/(xnorm[iii]),   ResultsList[[iii]]$y-ynormList[[iii]], pch=iii, col=colourlist[iii])
  lines(ResultsList[[iii]]$xFit/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)+log(ResultsList[[iii]]$xFit) +log(ResultsList[[iii]]$p) , col=colourlist[iii], lty=iii)
  #lines(ResultsList[[iii]]$xFit/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)-ynormList[[iii]] , col=colourlist[iii], lty=iii)
  nameVector[iii]= ResultsList[[iii]]$nameLong
 }
 abline(h=0)
 abline(v=1)
 xtextPos= xMin #0.5*log(xMax*xMin)
 ytextPos=yMax*0.5+yMin*0.5
 posNumber=4 # to the right of coord
 if (graphTypeNumber==3) { 
   xtextPos=sqrt(xMax*xMin) #0.5*log(xMax*xMin)
   ytextPos=yMax #*0.9+yMin*0.1
   posNumber=1 # below coord
 }  
 #print(paste("x y text positions",xtextPos,ytextPos),quote=F)
 text (x=xtextPos, y =ytextPos , pos=posNumber, labels = graphTypeName, adj = NULL, cex = 1.5,)
 # Finally add legend
 legendPos="bottomleft"
 if (graphTypeNumber==3) legendPos="topleft"
 legend (x=legendPos,y=NULL, nameVector[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}


