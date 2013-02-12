#This analyses the citation/reference ratio of papers
# and fits it to log normal using CUMMULATIVE distribution
# 
# rootName 
# is start of file name used.
# Format of file is given in citationReferenceIndex5.r
#
# cNotCoverR 
# is True if want to study C (citations) not 
# C/R (citations/references) of each paper
#
# normalise
# =TRUE if want to study x/<x> not just x for each paper 
#
# parameterAnalysisOn
# =TRUE if want to analyse the parameters 
# of the fit including plots
#
# plotsOn 
# TRUE if want plots of data and fit
#
# minYearInput (maxYearInput) 
# are the first (last) year of any publication of papers to be included
# e.g. 2000 (2005) means papers from 2000 to 2005 inclusive are included
#
# numberParameters
# 3 => fit all of mu, sigma and A, 1=>fit sigma only setting A=1 and mu=-(sigma)^2/2
#
# OSWindows
# True if drawing on MS Windows, FALSE for MAC


patcranalysis <- function(rootName="IC20090521", 
                                    cNotCoverR=TRUE, normalise=TRUE, 
                                    catNotScat=TRUE, allData=FALSE,
                                    minYearInput=1900, maxYearInput=2009, numberParameters=3, 
                                    parameterAnalysisOn=TRUE, plotsOn =TRUE,
                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=FALSE, epsPlotOn=TRUE,minCoverMeanC=0){

print(paste("****************************************"), quote=FALSE)
print(paste("rootName",rootName), quote=FALSE)
print(paste("OSWindows",OSWindows), quote=FALSE)
print(paste("screenOn",screenOn), quote=FALSE)
print(paste("pdfPlotOn",pdfPlotOn), quote=FALSE)
print(paste("epsPlotOn",epsPlotOn), quote=FALSE)


colourlist=c("black", "red", "blue", "green", "magenta", "brown","slategrey")
source("logbincount.r")
source("readRCYfile.r")
source("citationReferenceIndex7bk.r")
source("citationReferenceAllData.r")
                                    
catOn=FALSE
if (catNotScat) {
facOn=TRUE 
}
scatOn=!(catOn)

# minYear (maxYear) 
# are the year BEFORE (AFTER) any publication date to be included 
# e.g. 2000 (2005) means papers from 2001 to 2004 inclusive are included
minYear=minYearInput-1
maxYear=maxYearInput+1


valueName="(C/R)"
valueFileName="CR"
if (cNotCoverR) {
valueName="C"
valueFileName="C"
}
normaliseString=""
normaliseFileString=""
if (normalise) {
normaliseString=paste("/<",valueName,">",sep="")
normaliseFileString="norm"
}
xLabel=paste(valueName,normaliseString,sep="")

yearRangeString = paste((minYear+1),(maxYear-1),sep="-")
if (maxYear>2009) yearRangeString = paste((minYear+1),"2009",sep="-")
if ( (maxYear>2009) && (minYear<1950)) yearRangeString = ""

parameterString=paste("p",numberParameters,sep="")

# default settings use all data
catList <- seq(1,6)
scatList <- c(11,12,13,14,15,19,21,22,23,24,31,32,33,39,41,42,43,44,45,46,49,51,52,53,54,55,59,61,62,63,64,65,66,67,68,69)
scatCatIndex <- c(1,1,1,1,1,1,2,2,2,2,3,3,3,3,4,4,4,4,4,4,4,5,5,5,5,5,5,6,6,6,6,6,6,6,6,6)
titleString<-paste(rootName,yearRangeString,"All") 
unitList <- list("All")
binSize <- seq(from=50, by=0, length=length(unitList))



    if (catOn){
        typeString<-"Cat"
        unitList <- facultyList
        binSize <- c(15,15,15,15,15,10) 
        binType <- c(1,1,1,1,1,2)
    }
    
     if (scatOn){
        typeString<-"Subcat"
        scatIndexList <- c(4,9,19,22)
        
        titleString<-paste(rootName,yearRangeString,"Departments Ranked",scatIndexList) 
        unitList <- list()
        binSize <- list()
        binType <- seq(from=1, by=0, length=length(unitList))
        binColour <- list()
        for (jjj in 1:length(scatIndexList) ){
         ddd <- scatIndexList[[jjj]]
         catIndex <- scatCatIndex[[jjj]]
         unitList[[jjj]] <- scatList[[ddd]]
         bbb=20
         iii=1
         if (ddd>length(colourlist)*2) {
         bbb=10
         iii=2
         }
         if (ddd>length(colourlist)*3)  {
         bbb=10
         iii=3
         }
         binSize[[jjj]] <- bbb
         binType[jjj] <- catIndex
         binColour[[jjj]] <- colourlist[[catIndex]]
        } # eo for ddd
    
    } # eo if 
    
nameList <- paste(rootName,unitList,sep="_")
if(scatOn){
    nameList <- paste(rootName,unitList,"subcat",sep="_")
}
print(nameList)


minCount<-0

# to read in patent files
headerOn   <- TRUE
sepString  <- "\t"
refColumn  <- 2
citeColumn <- 3 
yearColumn <- 4
dateColumn <- -1
typeColumn <- -1

print(paste("##########################################################"), quote=FALSE)
print(paste("###",titleString,"###"), quote=FALSE)
 
crdata <- numeric()
totalRefs=0
totalCitations=0
totalNumberPapers=0
totalNZPCRPapers=0
meanValueUnnorm=0
CRList <- list()
for (iii in 1:length(unitList)) {

fullName=paste(nameList[[iii]],"grcy.dat",sep="")

 print(paste("***",titleString,":",fullName,"***"), quote=FALSE)
 citations = readRCYfile(fullName,headerOn, sepString, refColumn, citeColumn, yearColumn, dateColumn, typeColumn)
 refList<-list()
 citeList<-list()
 if(arXiv){
     refList<-citations$ref[citations$year>minYear & citations$year<maxYear & citations$type==unitList[[iii]]] #$References
    citeList<-citations$cite[citations$year>minYear & citations$year<maxYear & citations$type==unitList[[iii]]] #$Citations
 }
 else{
     refList<-citations$ref[citations$year>minYear & citations$year<maxYear] #$References
     citeList<-citations$cite[citations$year>minYear & citations$year<maxYear] #$Citations
 }
 CRList[[iii]] <- citationReferenceIndex7bk(refList, citeList, binSize[[iii]], minCount, normalise, cNotCoverR, numberParameters, minCoverMeanC) 
 infoList = CRList[[iii]]$infoList
 totalRefs = totalRefs + infoList$totalNumberReferences
 totalCitations = totalCitations + infoList$totalNumberCitations
 
 totalNumberPapers = totalNumberPapers + infoList$totalNumberPapers
 totalNZPCRPapers = totalNZPCRPapers + infoList$totalNZPCRPapers
 crdata = c(crdata,CRList[[iii]]$crdata)
 meanValueUnnorm = meanValueUnnorm + CRList[[iii]]$meanValueUnnorm

}
meanValueUnnorm = meanValueUnnorm / length(unitList)

# don't know how to be sure the parameters are always in this order
dmuVector<-numeric()
sigmaVector <-numeric()
dAVector<-numeric()
dmuVectorError<-numeric()
sigmaVectorError <-numeric()
dAVectorError<-numeric()
sigmatValueVector <-numeric()
sigmaPrtVector <-numeric()
dmutValueVector <-numeric()
dmuPrtVector <-numeric()
noOfPapers <-numeric()
chiSqVal <-numeric()
c0Val <-numeric()
rVal <-numeric()
stdErrpDof <- numeric()
CavRav<-numeric()
binCount<-numeric()
fractionRetained<-numeric()


if(allData){
    CRList[[length(CRList)+1]] = citationReferenceAllData(crdata, numberbins=20, numberParameters,totalRefs,totalCitations,totalNumberPapers,totalNZPCRPapers,meanValueUnnorm)
    unitList <- c(unitList,"All")
    binType <- c(binType,1)
}


for (iii in 1:length(unitList)) {
ccc<-coef(summary(CRList[[iii]]$fit))
sigmaValue <- ccc[[1]]
sigmaError <- ccc[[1+numberParameters]]
sigmatValue <- ccc[[1+numberParameters*2]]
sigmaPrt <- ccc[[1+numberParameters*3]]
if (numberParameters==3) {
dmuValue <- ccc[[2]]
dAValue <- ccc[[3]]
dmuError <- ccc[[5]]
dAError <- ccc[[6]]
dmutValue <- ccc[[8]]
dmuPrt <- ccc[[11]]
}
else{
dmuValue <- 0.0
dAValue <- 0.0
dmuError <- 0.0
dAError <- 0.0
dmutValue <- 0.0
dmuPrt <- 0.0
}
sigmaVector[iii] <- sigmaValue
dmuVector[iii] <- dmuValue
dAVector[iii] <- dAValue
sigmaVectorError[iii] <- sigmaError
dmuVectorError[iii] <- dmuError
dAVectorError[iii] <- dAError
sigmatValueVector[iii]  <-sigmatValue
sigmaPrtVector[iii]  <- sigmaPrt
dmutValueVector[iii]  <- dmutValue
dmuPrtVector[iii]  <- dmuPrt
noOfPapers[iii] <- CRList[[iii]]$noOfPapers
chiSqVal[iii] <- CRList[[iii]]$chisq
print(CRList[[iii]]$meanValueUnnorm)
c0Val[iii] <- CRList[[iii]]$meanValueUnnorm

rVal[iii] <- CRList[[iii]]$rVal
stdErrpDof[iii] <- CRList[[iii]]$stdErrpDof
binCount[iii] <- CRList[[iii]]$binCount

infoList<-CRList[[iii]]$infoList
CavRav[iii] <- infoList$totalNumberCitations/infoList$totalNumberReferences
fractionRetained[iii] <-infoList$totalNZPCRPapers/infoList$totalNumberPapers
 }



if (parameterAnalysisOn) {
if (screenOn){
if (OSWindows) windows() else quartz()
plot(x=dmuVector,  y=sigmaVector, type="p", main=paste(titleString,"dmu and sigma"), xlab="mu", ylab="sigma", pch=binType)
if (OSWindows) windows() else quartz()
plot(x=dmuVector,  y=dAVector, type="p", main=paste(titleString,"dmu and dA"), xlab="mu", ylab="A", pch=binType)
if (OSWindows) windows() else quartz()
plot(x=dAVector,  y=sigmaVector, type="p", main=paste(titleString,"dA and sigma"), xlab="A", ylab="sigma", pch=binType)
#if (OSWindows) windows() else quartz()
#plot(x=dmuVector,  y=vvv, type="p", main=paste(titleString,"mu vs mu+sigma^2/2"), xlab="mu", ylab="mu vs mu+sigma^2/2", pch=binType)

if (OSWindows) windows() else quartz()
plot(x=CavRav,  y=fractionRetained, type="p", main=titleString, sub="<c>/<r> and fraction retained", xlab="<c>/<r>", ylab="fraction retained", pch=binType)

#if (OSWindows) windows() else quartz()
#dmuCRdf <- data.frame(dmu=dmuVector, CR=CavRav)
#dmuCRfit <- lm(dmu ~ CR ,data=dmuCRdf)
#print(summary(dmuCRfit))
#plot(y=dmuVector,  x=CavRav, type="p", main=paste(titleString,"mu and <c>/<r>"), ylab="mu", xlab="<c>/<r>", pch=binType)
#text(y=dmuVector, x=CavRav, labels=1:length(muVector), pos=4)
#lines(fitted(dmuCRfit), x=CavRav)

#if (OSWindows) windows() else quartz()
#muCRdf <- data.frame(mu=muVector, CRav=exp(vvv))
#muCRfit <- lm(mu ~ CR ,data=muCRdf)
#print(summary(muCRfit))
#plot(x=CavRav,  y=exp(vvv), type="p", main=paste(titleString,"<c>/<r> and <c/r>"), xlab="<c>/<r>", ylab="<c/r>", pch=binType)
#text(x=CavRav, y=exp(vvv), labels=1:length(muVector), pos=4)
#lines(fitted(muCRfit), x=CavRav)



# Table of Names
if (OSWindows) windows() else quartz()
barplot(CavRav,names.arg=1:length(unitList))

if (OSWindows) windows() else quartz()
plot(x=NULL,  y=NULL, type="n", xlim=c(-1,25) , ylim=c(0,length(unitList)) , main=titleString, xlab=NULL, ylab=NULL, axes=FALSE)
text(x=seq(0, times=length(unitList)), y=1:length(unitList), labels=1:length(unitList), pos=2)
text(x=seq(1, times=length(unitList)), y=1:length(unitList), labels=unitList, pos=4)
} # end of if screenOn
} #end of if (parameterAnalysisOn)


if (plotsOn) {
iii=1
xMin<-CRList[[iii]]$xMin;
xMax<-CRList[[iii]]$xMax;
yscale <-CRList[[iii]]$meanValueUnnorm
yMin<-CRList[[iii]]$rhoMin/CRList[[1]]$totalPapers;
yMax<-CRList[[iii]]$rhoMax/CRList[[1]]$totalPapers;
if (length(unitList)>1) {
 for (iii in 2:length(unitList)) {
      xMin<-min(xMin, CRList[[iii]]$xMin)
      xMax<-max(xMax, CRList[[iii]]$xMax)
      yscale <-CRList[[iii]]$meanValueUnnorm
      yMin<-min(yMin, CRList[[iii]]$rhoMin/CRList[[iii]]$totalPapers)
      yMax<-max(yMax, CRList[[iii]]$rhoMax/CRList[[iii]]$totalPapers)
 }
}
if(xMin==0){ #Hack to prevent GScale error with naughty logarithms of zero
    xMin = 0.00000001
}
if(yMin==0){
    yMin = 0.00000001
}

# basic plot
#yLabel=paste("<",valueName,"> * Count/Total",sep="")
yLabel="Probability density"

#On Screen
if (screenOn){
print(paste("Windows plotting",yMin,yMax), quote=FALSE)
if (OSWindows) windows() else quartz()

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with both positive citation and reference counts)", 
     xlab=xLabel, ylab=yLabel )
for (iii in 1:length(unitList)) {
yscale <-CRList[[iii]]$meanValueUnnorm
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth), pch=iii, col=colourlist[iii], cex=1.2)
lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth),col=colourlist[iii], lty=iii)
}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", 
#       xlab=xLabel, ylab=paste(expression(c[0]),"* Count/Total"))

legend (x="topright",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));
}

# PDF plot
if (pdfPlotOn){
print(paste("pdf plotting",titleString,yMin,yMax), quote=FALSE)
pdfFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ll.pdf",sep="_")
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with both positive citation and reference counts)",
     xlab=xLabel, ylab=yLabel )
for (iii in 1:length(unitList)) {
yscale <-CRList[[iii]]$meanValueUnnorm
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth), pch=iii, col=colourlist[iii], cex=1.2)
lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth),col=colourlist[iii], lty=iii)
}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", 
#       xlab=xLabel, ylab="Count/Total")
legend (x="topright",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));

dev.off()
}
if (epsPlotOn){
# EPS plot
print(paste("eps plotting",titleString,yMin,yMax), quote=FALSE)
epsTitleOn=FALSE
epsTitleString=""
if (epsTitleOn) epsTitleString=titleString
epsFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ll.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)

 
plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=epsTitleString,
     xlab=xLabel, ylab=yLabel )
for (iii in 1:(length(unitList))) {
yscale <-CRList[[iii]]$meanValueUnnorm
#print(list(yscale))
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth), pch=iii, col=colourlist[iii], cex=1.2)
lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth),col=colourlist[iii], lty=iii)
}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", xlab=xLabel, ylab="Count/Total")
legend (x="bottomleft",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));

#########################################
#Disable legend above if wanted
#########################################



dev.off()
}

} # end of if plotsOn

outputList <-list(cNotCoverR=cNotCoverR, normalise=normalise, 
                                    facultyNotDept=facultyNotDept, 
                                    minYearInput=minYearInput, maxYearInput=maxYearInput, 
                                    numberParameters=numberParameters,
                                    unitList=unitList, 
                                    sigmaValue = sigmaVector , dmuValue=dmuVector, dAValue=dAVector,
                                    sigmaError = sigmaVectorError , dmuError=dmuVectorError, dAError=dAVectorError,
                                    sigmatValue= sigmatValueVector, dmutValue=dmutValueVector,                                    
                                    sigmaPrt   = sigmaPrtVector,    dmuPrt=dmuPrtVector,
                                    noOfPapers = noOfPapers,       chiSqVal=chiSqVal,
                                    c0Val      = c0Val,         rVal = rVal, 
                                    stdErrpDof=stdErrpDof,      binCount = binCount
                                    )




} # end of function
