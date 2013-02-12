# This produces the plot comparing the collapsed p(l) vs k  
# for leadership in Minority Game paper, as seen in Toby Clemson MSci report
print("***************************************************************************",quote=F) 
print("This produces the plot comparing the collapsed p(l) vs k ",quote=F) 
print(" for leadership in Minority Game paper, as seen in Toby Clemson MSci report",quote=F)


# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown");
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

pValue=0.1
NValue=601
print(paste("N =",NValue,", p = ",pValue), quote=FALSE)

#pValueVector=c(0.1)
#NValueVector=c(601)

graphTypeNameList = list("ER", "SF", "Ring")

plotTypeList = c("screen","eps","pdf")
plotTypeSelected = c(1,2,3) # c(1) #list of plots required, use numbers associated with 

logxOn=TRUE



if (useFittedk2Values) {
 # based on fitted k2 values
 k2CollapseType="Fitted k2 values"
 k2CollapseTypeShort="k2fit"
 graphTypeKfitScale = c(1.04,0.67,0.72)
 graphTypeKfitShift = c(-0.18,2.00,-0.60)
} else {
 # based on estimated k2 values
 k2CollapseType="Estimated k2 values"
 k2CollapseTypeShort="k2est"
 graphTypeKfitShift = c(-3.9,1.4,0.5)
 graphTypeKfitScale = c(0.8,0.57,0.5)
} 
print(paste("Collapse <k_s> factors based on",k2CollapseType), quote=FALSE)


ResultsList=list()
fitkmin=0
alg=0

fitFuncVec=c(4,4,1)
fitFuncTypeVec=c("power law multiplied by exponential",
                 "power law modified by Fermi-Dirac function",
                 "power law modified by erfc heaviside function",
                 "two power laws",
                 "exponetial")
                 


for (graphTypeNumber in 1:length(graphTypeNameList)){
print("=================================================", quote=FALSE)
 
 graphTypeName=graphTypeNameList[graphTypeNumber]
 graphKfitShift = graphTypeKfitShift[graphTypeNumber]
 graphKfitScale = graphTypeKfitScale[graphTypeNumber]
 graphKfitShiftAbs=abs(graphKfitShift)
# dataName= bquote(.(graphTypeName) k[offset] == .(graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  - .(graphKfitShiftAbs) ) )
# if (graphKfitShift<0) dataName= paste(graphTypeName,bquote(k[offset] == (graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  + .(graphKfitShiftAbs) ) )
 dataName= graphTypeName
 dataFileName = paste("MGdata/Toby",graphTypeName,".dat",sep="")
 paramFileName = paste("MGdata/TobyParam",graphTypeName,".dat",sep="")
 outputFileNameRoot = paste("leadership",graphTypeName,sep="")
 print(paste("Working with",dataName))
 print(paste("graphs from files",dataFileName,paramFileName), quote=FALSE)
 print(paste("Collapse <k_s> factors based on",k2CollapseType), quote=FALSE)
 print(paste("graph <k_s> shift factor ",graphKfitShift), quote=FALSE)
 print(paste("graph <k_s> scale factor ",graphKfitScale), quote=FALSE)

 fitfunc=fitFuncVec[graphTypeNumber]
 fitFuncType=fitFuncTypeVec[fitfunc]
 print(paste("Fitting using",fitFuncType),quote=F)

 tobydata <- read.table(dataFileName, header=TRUE);
 tobyparamdata <- read.table(paramFileName, header=TRUE);

 # remember td =tobydata and first column is k 
 nColumns= length(tobydata)
 tdIndexVector = 2:nColumns

 nameListTemp=names(tobydata)
 nameList=nameListTemp[2:nColumns]
 
 #pValue=pValueVector[vvv]
 #NValue=NValueVector[vvv]
 tdColumn = tdIndexVector[tobyparamdata$N==NValue & tobyparamdata$p==pValue] 
 print(paste("tobydata column",tdColumn), quote=FALSE)
 print(paste("---",graphTypeName,nameList[tdColumn-1]), quote=FALSE)
 
 
 
 Etotal <- sum(tobydata[[tdColumn]])
 #logplist<- log(tobydata[[tdColumn]]/Etotal)[tobydata[[tdColumn]]>0]
 klist    <-        tobydata[[1]][tobydata[[tdColumn]]>0]
 n1       <-        tobydata[[tdColumn]][klist==1]
 lognlist <- log(tobydata[[tdColumn]])[tobydata[[tdColumn]]>0]
 logn1    <-        lognlist[klist==1]
 #dataFit <- data.frame(k=klist, logp=logplist)
 #dataFit <- data.frame(k=klist[klist>fitkmin], logp=logplist[klist>fitkmin])
 dataPlot <- data.frame(k=klist[klist>fitkmin], logn=lognlist[klist>fitkmin])
 kaverage <- tobyparamdata$kaverage[tdColumn-1]
 if (graphTypeNumber == 3) { # exclude special points for ring case
 dataFit <- data.frame(k=klist[klist>fitkmin & klist != kaverage & klist != kaverage/2 ], logn=lognlist[klist>fitkmin & klist != kaverage & klist != kaverage/2 ])
 } else {
 dataFit <- dataPlot
 }
 kmax = max(dataFit$k)

 k1est = 0.0 
 k1min = -.5 #0.01
 k1max = 0.5

 k2est = kmax/4
 k2min = 1.0    
 k2max = kmax-5

 k3est = 10.0 #kmax/10.0
 k3min = 0.001    
 k3max = kmax

 gest=1.0
 gmin=0.5
 gmax=1.5
 if (graphTypeNumber==3) { gest=0.5;  gmin=0.1; gmax=1.0; }

 xest=6.0
 if (graphTypeNumber==1) {xest  <- c(15,15,18,25,22)[tdColumn-1] ; }
 xmax=xest+6.0; 
 xmin=max(xest-6.0,0.5);

 normest=logn1         #1.0/log(k2est);
 normmin=normest-1    #normest/1000; #1.0/log(k2max);
 normmax=normest+1    #normest*1000; #1.0/log(k2min);
  
  
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
#   print(paste("FITTING",fitfunc),quote=F)
  if (fitfunc==5) {
  fitres <- nls( logn ~ normfit -(k/k2fit)^xfit + (1.0/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 }
 if (fitfunc==4) {
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -log(1+(k/k2fit)^xfit ) +gfit*log(1.0+k1fit) +log(1+(1.0/k2fit)^xfit )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==3) {
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) +log(erfcOver2( (k-k2fit)/k3fit) ) + gfit*log(1.0+k1fit) - log(erfcOver2( (1.0-k2fit)/k3fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==2) {
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -log(exp((k-k2fit)^xfit)+1 ) , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
  }
 if (fitfunc==1) {
  fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 } 
   print(paste("FITTED",fitfunc),quote=F)

 
# if (fitfunc==2) fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) +log(erfcOver2( (k-k2fit)/k3fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
# if (fitfunc==1) fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -log(exp((k-k2fit)^xfit)+1 ) , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
# if (fitfunc==0) fitres <- nls( logn ~ normfit -gfit*log(k+k1fit) -(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-log( (k+k1fit)^gfit + exp((k-k3fit)/k2fit) )  , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit-gfit*log((k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")
 #fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist) #, lower=lowerlist, upper=upperlist, algorithm="plinear")
 #else fitres <- nls( logn ~ normfit+gfit*log((1+k1fit)/(k+k1fit))-(k/k2fit)^xfit , dataFit, startlist, lower=lowerlist, upper=upperlist, algorithm="port")

 print(summary(fitres))

 cfit <- coef(fitres)
 fitvalues <- list(k1value = cfit[[1]], k2value = cfit[[2]], gammavalue = cfit[[3]], chifit = cfit[[4]], normfit = cfit[[5]] )
 
 
 ResultsList[[graphTypeNumber]] = list(x=dataPlot$k,y=dataPlot$logn,E=Etotal,logn1=logn1, name=dataName, 
                             xFit=dataFit$k,
                             fitvalues=fitvalues,
                             graphKfitScale=graphKfitScale, graphKfitShift=graphKfitShift,
                             N=tobyparamdata$N[tdColumn-1], p=tobyparamdata$p[tdColumn-1], kaverage=kaverage, 
                             xMin=min(dataPlot$k),xMax=max(dataPlot$k),yMin=min(dataPlot$logn),yMax=max(dataPlot$logn),fit=fitres)
} # end for graphTypeNumber

outputFileNameRoot = paste("leadershipAllN",NValue,"p",pValue,sep="")
 

# ................................................................................
# ................................................................................
print("xxx ------------------------------------------------", quote=FALSE)
plotTitle = paste("Minority Game for N=",NValue,", p=",pValue)
plotName=paste("pkcollapsed",k2CollapseTypeShort,pubOnShort,sep="")
print("Plot collapsed p(k) vs k",quote=F)

# Set x axis
#xLabel = paste("k/(",graphKfitScale,"<k_s>-",graphKfitShift,")")
#graphKfitShiftAbs=abs(graphKfitShift)
xLabel = bquote(k/ k[offset]  )
xnorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) xnorm[iii]= (ResultsList[[iii]]$graphKfitScale*ResultsList[[iii]]$kaverage+ResultsList[[iii]]$graphKfitShift) # ResultsList[[iii]]$fitvalues$k2value #
xMin<-ResultsList[[1]]$xMin/xnorm[1];
for (iii in 2:length(ResultsList)) xMin<-min(xMin, ResultsList[[iii]]$xMin/xnorm[iii])
xMax<-ResultsList[[1]]$xMax/xnorm[1];
for (iii in 2:length(ResultsList)) xMax<-max(xMax, ResultsList[[iii]]$xMax/xnorm[iii])

# Set y axis
#yLabel = paste("log(n(k)*p*",shiftFormula)
yLabel=bquote(log(n(k))*p*k[offset] )  
#if (graphKfitShift<0) yLabel=bquote(log(n(k))*p*( .(graphKfitScale)*symbol("\341") * k[s] * symbol("\361")  + .(graphKfitShiftAbs) ) ) 
ynorm=1:length(ResultsList)
for (iii in 1:length(ResultsList)) ynorm[iii]= -log(xnorm[iii]) -log(ResultsList[[iii]]$p)  #-log(ResultsList[[iii]]$kav) -log(ResultsList[[iii]]$p) #log(ResultsList[[iii]]$fitvalues$k2value) #log(ResultsList[[iii]]$E)
yMin<-ResultsList[[1]]$yMin-ynorm[1];
for (iii in 2:length(ResultsList)) yMin<-min(yMin, ResultsList[[iii]]$yMin-ynorm[iii])
yMax<-ResultsList[[1]]$yMax-ynorm[1];
for (iii in 2:length(ResultsList)) yMax<-max(yMax, ResultsList[[iii]]$yMax-ynorm[iii])

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
 plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab=xLabel, ylab=yLabel)
# plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=paste(plotTitle, k2CollapseType), sub="(Toby data)", xlab=xLabel, ylab=yLabel)
 nameVector=1:length(ResultsList)
 for (iii in 1:length(ResultsList)) {
  points(ResultsList[[iii]]$x/(xnorm[iii]),   ResultsList[[iii]]$y-ynorm[iii], pch=iii, col=colourlist[iii])
  lines(ResultsList[[iii]]$xFit/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)-ynorm[iii], col=colourlist[iii], lty=iii)
  abline(h=0)
  abline(v=1)
  nameVector[iii]= ResultsList[[iii]]$name
 }
 xtextPos= sqrt(xMin*xMax) #0.5*log(xMax*xMin)
 ytextPos=yMin #*0.9+yMin*0.1
 posNumber=3 # above coord
 print(paste("x y text positions",xtextPos,ytextPos),quote=F)
 text (x=xtextPos, y =ytextPos , pos=posNumber, labels = paste("N=",NValue,", p=",pValue,sep=""), adj = NULL, cex = 1.5,)
 # Finally add legend
 legend (x="bottomleft",y=NULL, nameVector[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}

# .................................................................
plotName=paste("pkcollapsed2",k2CollapseTypeShort,pubOnShort,sep="")
print("Plot collapsed k.p(k) vs k",quote=F)

# Set x axis as before

# Set y axis
yLabel=bquote(log(n(k))*p*k )  
ynormList = list()
for (iii in 1:length(ResultsList)) ynormList[[iii]]= -log(ResultsList[[iii]]$x) -log(ResultsList[[iii]]$p)  #-log(ResultsList[[iii]]$kav) -log(ResultsList[[iii]]$p) #log(ResultsList[[iii]]$fitvalues$k2value) #log(ResultsList[[iii]]$E)
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
 plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitle, sub=subTitle, xlab=xLabel, ylab=yLabel)
# plot(x=NULL,  y=NULL, type="n", log="x", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=paste(plotTitle, k2CollapseType), sub="(Toby data)", xlab=xLabel, ylab=yLabel)
 nameVector=1:length(ResultsList)
 for (iii in 1:length(ResultsList)) {
  points(ResultsList[[iii]]$x/(xnorm[iii]),   ResultsList[[iii]]$y-ynormList[[iii]], pch=iii, col=colourlist[iii])
  lines(ResultsList[[iii]]$xFit/(xnorm[iii]), fitted(ResultsList[[iii]]$fit)+log(ResultsList[[iii]]$xFit) +log(ResultsList[[iii]]$p) , col=colourlist[iii], lty=iii)
  abline(h=0)
  abline(v=1)
  nameVector[iii]= ResultsList[[iii]]$name
 }
 xtextPos=sqrt(xMin*xMax) #0.5*log(xMax*xMin)
 ytextPos=yMax #*0.9+yMin*0.1
 posNumber=1 # below coord
 #print(paste("x y text positions",xtextPos,ytextPos),quote=F)
 text (x=xtextPos, y =ytextPos , pos=posNumber, labels = paste("N=",NValue,", p=",pValue,sep=""), adj = NULL, cex = 1.5,)
 # Finally add legend
 legend (x="bottomleft",y=NULL, nameVector[1:length(ResultsList)], col=colourlist[1:length(ResultsList)],lty=1:length(ResultsList),pch=1:length(ResultsList));
 if (plotType>1) dev.off(which = dev.cur())
}


