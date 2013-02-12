# This produces single plot of k2 vs <k[s]> for the data of Toby Clemson 
# for leadership in Minority Game paper, as seen in his MSci report
print("***************************************************************************",quote=F) 
print("This produces single plot of k2 vs <k[s]> for the data of Toby Clemson",quote=F) 

# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown");
erfcOver2 <- function(x)  pnorm(x * sqrt(2), lower = FALSE)

OSWindows=TRUE # false for Mac OS
if (OSWindows) screenType="Windows" else screenType="Mac" 
print(paste("Screen type",screenType), quote=FALSE)

useFittedk2Values=TRUE

publicationON=TRUE #TRUE # plots ready for publication
if (publicationON) {
 pubOnShort="pub"
 print("Plots for publication", quote=FALSE) 
} else {
 pubOnShort="dev"
 print("Plots for development", quote=FALSE)
}

graphTypeNameList = list("ER", "SF", "Ring")

plotTypeList = c("screen","eps","pdf")
plotTypeSelected = c(1,2,3) # list of plots required, use numbers associated with plotTypeList

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


fitkmin=0

alg=0

fitFuncVec=c(4,4,1)
fitFuncTypeVec=c("power law multiplied by exponential",
                 "power law modified by Fermi-Dirac function",
                 "power law modified by erfc heaviside function",
                 "two power laws",
                 "exponetial")

k2ksavList=list()

for (graphTypeNumber in 1:length(graphTypeNameList)){
   print("=================================================", quote=FALSE)
                      
   graphTypeName=graphTypeNameList[graphTypeNumber]
   graphKfitShift = graphTypeKfitShift[graphTypeNumber]
   graphKfitScale = graphTypeKfitScale[graphTypeNumber]
   graphKfitShiftAbs=abs(graphKfitShift)
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
   nColumns= length(tobydata)
   nameListTemp=names(tobydata)
   nameList=nameListTemp[2:nColumns]

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

#    if (k2estimateExists) print(paste("Estimated k2 value",tobyparamdata$k2est[ccc-1]),quote=F)
    print(summary(fitres))

   # cfit <- coef(fitres)
   # fitvalues <- list(k1value = cfit[[1]], k2value = cfit[[2]], gammavalue = cfit[[3]], chifit = cfit[[4]], normfit = cfit[[5]] )
   fitvalues <- as.list(coef(fitres))

    ResultsList[[ccc-1]] = list(x=dataPlot$k,y=dataPlot$logn,E=Etotal,logn1=logn1, name=nameList[ccc-1], 
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
   zzz =length(k2ksavList)+1
   k2ksavList[[zzz]] = list(x=dffit$ksav,  y=dffit$k2,  fit=lffit, name=graphTypeName)

} # end of for (graphTypeNumber 
 

# *****************************************************
outputFileNameRoot = "leadershipAll"
plotTitle = "Minority Game cutoff vs substrate"

# Now plot k2 vs <k[s]>
 
nnn =  length(k2ksavList)
for (plotType in plotTypeSelected) {
  xLabel = expression(symbol("\341") * k[s] * symbol("\361")) 
  yLabel = expression( k[2] ) 
  xMax=0
  yMax=0
  for (ggg in 1:nnn){
   xMax=max(k2ksavList[[ggg]]$x,xMax)
   yMax=max(k2ksavList[[ggg]]$y,yMax)
  } 
  #if (k2estimateExists) yMax=max(dffit$k2,dfest$k2)

  plotName=paste("k2ksav",pubOnShort,sep="")
  k2ksavnameList=c("Fitted","Estimated")

   if (plotType==1) {if (OSWindows) windows() else quartz()}
   if (plotType==2) {#eps plot
         epsFileName<- paste(outputFileNameRoot,plotName,".eps",sep="")
         print(paste("eps plotting",epsFileName), quote=FALSE)
         #postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=7.5, width=11, pointsize=8)
         postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12, fonts=c("serif", "Palatino"))
         }
   if (plotType==3) {#plots pdf file
         pdfFileName<-paste(outputFileNameRoot,plotName,".pdf",sep="");
         print(paste("pdf plotting",pdfFileName), quote=FALSE)
         pdf(pdfFileName, onefile=FALSE, height=7.5, width=11, pointsize=8)
         }
  if (publicationON) { 
   mainTitle=NULL
   #subTitle=NULL
   } else {
   mainTitle=plotTitle
   #subTitle=plotSubTitle
   }  
  plot(x=NULL,  y=NULL,  xlim=c(0,xMax) , ylim=c(0,yMax) , main=mainTitle, xlab=xLabel, ylab=yLabel, cex=2)
    
  typeNameVec = 1:nnn
  for (ggg in 1:nnn){
    typeNameVec[ggg] = k2ksavList[[ggg]]$name
    points(x=k2ksavList[[ggg]]$x,  y=k2ksavList[[ggg]]$y,  pch=ggg, col=colourlist[ggg], cex=2)
    lines(x=k2ksavList[[ggg]]$x,  y=fitted(k2ksavList[[ggg]]$fit), lwd=3, lty=ggg, pch=ggg, col=colourlist[ggg])
  } # for ggg
     
 # Finally add legend if more than one line
 legend (x="bottomright",y=NULL, typeNameVec, col=colourlist[1:nnn],lwd=3, lty=1:nnn,pch=1:nnn, cex=2);

 if (plotType>1) dev.off(which = dev.cur())
} # for plottype


