#This analyses the citation/reference ratio of papers
# and fits it to log normal using CUMMULATIVE distribution
# 
# rootName 
# is start of file name used.
# Format of file is given in citationReferenceIndex5.r
#
# unitNumbers
# is a vector of numbers corresponding to the indices of the 
# internal unitListAll. Selects which archives to include
#
# cNotCoverR 
# is True if want to study C (citations) not 
# C/R (citations/references) of each paper
#
# normalise
# =TRUE if want to study x/<x> not just x for each paper 
#
# parameterAnalysisOn
# =TRUE if wnat to analyse the parameters 
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


arXivcranalysis <- function(rootName="arXivtest", 
                                    cNotCoverR=TRUE, normalise=TRUE, 
                                    minYearInput=1900, maxYearInput=2009, numberParameters=3, 
                                    parameterAnalysisOn=TRUE, plotsOn =TRUE,
                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=FALSE, epsPlotOn=TRUE){

print(paste("****************************************"), quote=FALSE)
print(paste("rootName",rootName), quote=FALSE)
print(paste("OSWindows",OSWindows), quote=FALSE)
print(paste("screenOn",screenOn), quote=FALSE)
print(paste("pdfPlotOn",pdfPlotOn), quote=FALSE)
print(paste("epsPlotOn",epsPlotOn), quote=FALSE)


colourlist=c("black", "red", "blue", "green", "magenta", "brown", "cyan", "gray50");
source("logbincount.r")
source("readRCYfile.r")
source("citationReferenceIndex7.r")
                                    

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

parameterString=paste("p",numberParameters)

# default settings use all data
unitList <- list("hep-th","hep-lat","hep-ex","hep-ph","gr-qc","nucl-ex","nucl-th","astro-ph");
titleString<-paste(rootName,yearRangeString) 
typeString<-"arXiv"
binSize <- c(20,20,20,20,20,20,20,20) #seq(from=20, by=0, length=length(unitList))
binType <- 1:8



#nameList <- paste(rootName,unitList,sep="")


minCount<-0

# to read in arXiv files
headerOn   <- TRUE
sepString  <- "\t"
refColumn  <- 7
citeColumn <- 8 
yearColumn <- -1
dateColumn <- 4
typeColumn <- 2

fullName=paste(rootName,".dat",sep="")
citations <- readRCYfile(fullName,headerOn, sepString, refColumn, citeColumn, yearColumn, dateColumn, typeColumn)

#typeList <- citations$type
   
CRList <- list()
infoList <- list()
for (iii in 1:length(unitList)) {
 print(paste("************",unitList[[iii]],"*************"), quote=FALSE)
 refList  <- citations$ref[ citations$type==unitList[[iii]] & citations$year>minYear & citations$year<maxYear]
 citeList <- citations$cite[citations$type==unitList[[iii]] & citations$year>minYear & citations$year<maxYear]
# yearList <- citations$year[citations$type==unitList[[iii]] & citations$year>minYear & citations$year<maxYear]
 CRList[[iii]] <- citationReferenceIndex7(refList, citeList, binSize[[iii]], minCount, normalise, cNotCoverR, numberParameters) 
# CRList[[iii]] <- list(ref=refList, cite=citeList)
 infoList[[iii]] <- CRList[[iii]]$infoList
}



# don't know how to be sure the parameters are always in this order
#vvv = seq(from=1, by=0, length=length(unitList))
dmuVector<-numeric()
sigmaVector <-numeric()
dAVector<-numeric()
#vvv<-numeric()
CavRav<-numeric()


fractionRetained<-numeric()
for (iii in 1:length(unitList)) {
ccc<-coef(CRList[[iii]]$fit)
sigmaValue <- ccc[[1]]
if (numberParameters==3) {
dmuValue <- ccc[[2]]
dAValue <- ccc[[3]]
}
else{
dmuValue <- 0.0
dAValue <- 0.0
}
sigmaVector[iii] <- sigmaValue
dmuVector[iii] <- dmuValue
dAVector[iii] <- dAValue
#vvv[iii] <- muValue+sigmaValue*sigmaValue/2.0
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

xMin<-CRList[[1]]$xMin;
xMax<-CRList[[1]]$xMax;
yMin<-CRList[[1]]$yMin/CRList[[1]]$totalPapers;
yMax<-CRList[[1]]$yMax/CRList[[1]]$totalPapers;
if (length(unitList)>1) {
 for (iii in 2:length(unitList)) {
      xMin<-min(xMin, CRList[[iii]]$xMin)
      xMax<-max(xMax, CRList[[iii]]$xMax)
      yMin<-min(yMin, CRList[[iii]]$yMin/CRList[[iii]]$totalPapers)
      yMax<-max(yMax, CRList[[iii]]$yMax/CRList[[iii]]$totalPapers)
 }
}


xMin <- 10^(floor(log10(xMin)))
yMin <- 10^(floor(log10(yMin)))
xMax <- 10^(ceiling(log10(xMax)))
yMax <- 10^(ceiling(log10(yMax))+1)

# basic plot
#titleString<-rootName 
#titleString<-paste(unitList, sep=" ")

#On Screen
if (screenOn){
print(paste("Windows plotting",yMin,yMax), quote=FALSE)
if (OSWindows) windows() else quartz()

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with both positive citation and reference counts)", xlab=xLabel, ylab="Count/Total")
for (iii in 1:length(unitList)) {
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/CRList[[iii]]$totalPapers, pch=iii, col=colourlist[iii])
lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/CRList[[iii]]$totalPapers,col=colourlist[iii], lty=iii)
}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", xlab=xLabel, ylab="Count/Total")
legend (x="topright",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));
}

# PDF plot
if (pdfPlotOn){
print(paste("pdf plotting",titleString,yMin,yMax), quote=FALSE)
pdfFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ll.pdf",sep="_")
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with both positive citation and reference counts)", xlab=xLabel, ylab="Count/Total")
for (iii in 1:length(unitList)) {
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/CRList[[iii]]$totalPapers, pch=iii, col=colourlist[iii])
lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/CRList[[iii]]$totalPapers,col=colourlist[iii], lty=iii)
}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", xlab=xLabel, ylab="Count/Total")
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

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=epsTitleString, xlab=xLabel, ylab="Count/Total")
for (iii in 1:length(unitList)) {
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/CRList[[iii]]$totalPapers, pch=iii, col=colourlist[iii], cex=1.2)
lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/CRList[[iii]]$totalPapers,col=colourlist[iii], lty=iii)
}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", xlab=xLabel, ylab="Count/Total")
#legend (x="topright",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));

dev.off()
}

} # end of if plotsOn


outputList <-list(cNotCoverR=cNotCoverR, normalise=normalise, 
                                    facultyNotDept=facultyNotDept, 
                                    minYearInput=minYearInput, maxYearInput=maxYearInput, 
                                    numberParameters=numberParameters,
                                    unitList=unitList, 
                                    sigmaValue = sigmaValue , dmuValue=dmuValue, dAValue=dAValue,
                                    infoList = infoList)





} # end of function
