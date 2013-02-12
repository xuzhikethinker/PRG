# Need to install then load gplots library
print("Need to issue library(gplots)", quote=FALSE)
source("iccranalysis.r")
colourList=c("black", "red", "blue", "green", "magenta", "brown");


#Root name for all files
rootName="IC20090521"

# TRUE (FALSE) for c (c/r)
cNotCoverR=TRUE

# Number of fitting parameters, 1 or 3
numberParameters=1

# TRUE (FALSE) for normalising measure by its average <c> or <c/r>
normalise=TRUE

# TRUE (FALSE) for Departments (Faculties)
facultyNotDept=TRUE

# years to study (inclusive) [1997-2008 is standard]
minYear=2001 #1997
maxYear=2001 #2008

parameterAnalysisOn=TRUE
plotsOn=TRUE
OSWindows=TRUE
screenOn=FALSE
pdfPlotOn=FALSE
epsPlotOn=TRUE

#iccranalysis(rootName, cNotCoverR, normalise, facultyNotDept, minYearInput, maxYearInput, numberParameters, parameterAnalysisOn, plotsOn, OSWindows, screenOn, pdfPlotOn, epsPlotOn) 

#(rootName="IC20090521", 
#                                    cNotCoverR=TRUE, normalise=TRUE, 
#                                    facultyNotDept=TRUE, 
#                                    minYearInput=1900, maxYearInput=2009, numberParameters=3, 
#                                    parameterAnalysisOn=TRUE, plotsOn =TRUE,
#                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=TRUE, epsPlotOn=TRUE)
yearList <- minYear:maxYear


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

parameterString=paste("p",numberParameters,sep="")

typeString<-"Department"
if (facultyNotDept){
typeString<-"Faculty"
}

yearRangeString = paste((minYear),(maxYear),sep="-")

sigmaValueList <- list()
sigmaErrorList <- list()
sigmatValueList <- list()
sigmaPrtList <- list()
dmuValueList <- list()
dmuErrorList <- list()
dmutValueList <- list()
dmuPrtList <- list()
dAValueList <- list()
dAErrorList <- list()
iii=0
for (iii in 1:length(yearList)){
yyy <- yearList[[iii]]
lst  <-iccranalysis(rootName, cNotCoverR, normalise, facultyNotDept, yyy, yyy, numberParameters, parameterAnalysisOn, plotsOn, OSWindows, screenOn, pdfPlotOn, epsPlotOn) 
sigmaValueList[[iii]] <- lst$sigmaValue
sigmaErrorList[[iii]] <- lst$sigmaError
sigmatValueList[[iii]] <- lst$sigmatValue
sigmaPrtList[[iii]] <- lst$sigmaPrt
dmuValueList[[iii]] <- lst$dmuValue
dmuErrorList[[iii]] <- lst$dmuError
dmutValueList[[iii]] <- lst$dmutValue
dmuPrtList[[iii]] <- lst$dmuPrt
dAValueList[[iii]] <- lst$dAValue
dAErrorList[[iii]] <- lst$dAError
}

if (OSWindows) windows() else quartz()
unitList <- lst$unitList
yValueList <- list()
xshift <- 0.2

yMin=sigmaValueList[[1]][1]-sigmaErrorList[[1]][1] 
yMax=sigmaValueList[[1]][1]+sigmaErrorList[[1]][1] 
for (uuu in 1:length(unitList)){
  for (iii in 1:length(yearList)){
     yMin=min(yMin,sigmaValueList[[iii]][uuu]-sigmaErrorList[[iii]][uuu] )
     yMax=max(yMax,sigmaValueList[[iii]][uuu]+sigmaErrorList[[iii]][uuu] )
  }
}
yLimits <- c(yMin,yMax)


#### sigma or sigma^2 plots #######################################################

xLimits <- c(minYear-length(unitList)*xshift,maxYear+length(unitList)*xshift)
if (cNotCoverR & (numberParameters<2)) {
  sigmaLimits <- c(0.9,1.5)
  sigma2Limits <- c(0.8,2.2)
  legendPosition <-"bottomright"
}
if (cNotCoverR & (numberParameters>2)) {
  sigmaLimits <- c(1.0,2.0)
  sigma2Limits <- c(1.0,3.0)
  legendPosition <-"topleft"
  if (!normalise){
    sigmaLimits <- c(0.7,2.0)
    sigma2Limits <- NULL #c(0.0,5.0)
  }
}
if (!cNotCoverR & (numberParameters<2)) {
  sigmaLimits <- c(0.9,1.4)
  sigma2Limits <- c(0.8,1.8)
  legendPosition <-"topright"
}
if (!cNotCoverR & (numberParameters>2)) {
  sigmaLimits <- c(0.9,1.3)
  sigma2Limits <- c(0.8,1.6)
  legendPosition <-"topright"
}

titleString <- paste(numberParameters," parameter fit for ",typeString,valueName,normaliseString)

# windows() plot
if (OSWindows) windows() else quartz()
#plot(x=NULL,  y=NULL, 
#     xlim=c(minYear,maxYear+length(unitList)*xshift), ylim=c(0.5,1.5),
#     xlab="Year", ylab=expression(sigma), 
#     main = paste(numberParameters," parameter fit for ",typeString,valueName,normaliseString)  )

addValue=FALSE
for (uuu in 1:length(unitList)){
  yValueList <- numeric()
  errorList <- numeric()
  for (iii in 1:length(yearList)){
#     yValueList[iii] <- sigmaValueList[[iii]][uuu] 
#     errorList[iii] <- sigmaErrorList[[iii]][uuu] 
     yValueList[iii] <- sigmaValueList[[iii]][uuu]*sigmaValueList[[iii]][uuu] 
     errorList[iii] <- sigmaErrorList[[iii]][uuu]*2 
  }
  plotCI(yearList+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
         xlim=xLimits, ylim=sigma2Limits,
         pch=uuu, col=colourList[uuu], cex=1.2,
         main = titleString , 
         sub = NULL, xlab = "Year", ylab = expression(sigma^2),
         xaxp=c(minYear,maxYear,maxYear-minYear)
         )

         
  addValue=TRUE
}
if (cNotCoverR) {
 abline(h=1.3,lty=2)
}
legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));
# end of windows() plot

# eps plot
print(paste("eps plotting sigma^2",titleString), quote=FALSE)
epsTitleOn=FALSE
epsTitleString=""
if (epsTitleOn) epsTitleString=titleString
epsFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"sigma2.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)

addValue=FALSE
for (uuu in 1:length(unitList)){
#  print(paste("eps plotting",uuu), quote=FALSE)
  yValueList <- numeric()
  errorList <- numeric()
  for (iii in 1:length(yearList)){
     yValueList[iii] <- sigmaValueList[[iii]][uuu]*sigmaValueList[[iii]][uuu] 
     errorList[iii] <- sigmaErrorList[[iii]][uuu]*2 
  }
  plotCI(yearList+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
         xlim=xLimits, ylim=sigma2Limits,
         pch=uuu, col=colourList[uuu], cex=1.2,
         main = epsTitleString , 
         sub = NULL, xlab = "Year", ylab = expression(sigma^2),
         xaxp=c(minYear,maxYear,maxYear-minYear)
         )

         
  addValue=TRUE
}
if (cNotCoverR) {
 abline(h=1.3,lty=2)
}
legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));

dev.off()
# end of eps plot



#### dmu plots ###################################################################
if (numberParameters>2) {

titleString <- paste(numberParameters," parameter fit for ",typeString,valueName,normaliseString)

if (cNotCoverR) {
  dmuLimits <- c(-0.5,0.5)
  legendPosition <-"bottomright"
  if (!normalise){
    dmuLimits <- NULL #c(0.0,5.0)
  }
}
else {
  dmuLimits <- c(-0.4,0.4)
  legendPosition <-"topright"
}

# windows() plot
if (OSWindows) windows() else quartz()
addValue=FALSE
for (uuu in 1:length(unitList)){
  yValueList <- numeric()
  errorList <- numeric()
  for (iii in 1:length(yearList)){
     yValueList[iii] <- dmuValueList[[iii]][uuu] 
     errorList[iii] <- dmuErrorList[[iii]][uuu] 
  }
  plotCI(yearList+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
         xlim=xLimits, ylim=dmuLimits,
         pch=uuu, col=colourList[uuu], cex=1.2,
         main = titleString , 
         sub = NULL, xlab = "Year", ylab = expression(mu+sigma^2),
         xaxp=c(minYear,maxYear,maxYear-minYear)
         )

         
  addValue=TRUE
}
abline(h=0)
legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));
# end of windows() plot

# eps plot
print(paste("eps plotting dmu=(mu+sigma^2)",titleString), quote=FALSE)
epsTitleOn=FALSE
epsTitleString=""
if (epsTitleOn) epsTitleString=titleString
epsFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"dmu.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)

addValue=FALSE
for (uuu in 1:length(unitList)){
#  print(paste("eps plotting",uuu), quote=FALSE)
  yValueList <- numeric()
  errorList <- numeric()
  for (iii in 1:length(yearList)){
     yValueList[iii] <- dmuValueList[[iii]][uuu]
     errorList[iii] <- dmuErrorList[[iii]][uuu] 
  }
  plotCI(yearList+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
         xlim=xLimits, ylim=dmuLimits,
         pch=uuu, col=colourList[uuu], cex=1.2,
         main = epsTitleString , 
         sub = NULL, xlab = "Year", ylab = expression(mu+sigma^2),
         xaxp=c(minYear,maxYear,maxYear-minYear)
         )

         
  addValue=TRUE
}
abline(h=0)
legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));

dev.off()
# end of eps plot

} # *** End of dmu plots

#### dA plots ##########################################################
if (numberParameters>2) {

dALimits <- c(-0.1,0.1)
  
titleString <- paste(numberParameters," parameter fit for ",typeString,valueName,normaliseString)
if (cNotCoverR) {
  dALimits <- c(-0.4,0.4)
  legendPosition <-"topleft"
  if (!normalise){
    dALimits <- NULL #c(0.0,5.0)
  }
}
else {
  dALimits <- c(-0.1,0.1)
  legendPosition <-"topright"
}

# windows() plot
if (OSWindows) windows() else quartz()
addValue=FALSE
for (uuu in 1:length(unitList)){
  yValueList <- numeric()
  errorList <- numeric()
  for (iii in 1:length(yearList)){
     yValueList[iii] <- dAValueList[[iii]][uuu] 
     errorList[iii] <- dAErrorList[[iii]][uuu] 
  }
  plotCI(yearList+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
         xlim=xLimits, ylim=dALimits,
         pch=uuu, col=colourList[uuu], cex=1.2,
         main = titleString , 
         sub = NULL, xlab = "Year", ylab = expression((A-1)),
         xaxp=c(minYear,maxYear,maxYear-minYear)
         )

         
  addValue=TRUE
}
abline(h=0)
legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));
# end of windows() plot

# eps plot
print(paste("eps plotting dA=(A-1)",titleString), quote=FALSE)
epsTitleOn=FALSE
epsTitleString=""
if (epsTitleOn) epsTitleString=titleString
epsFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"dA.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)

addValue=FALSE
for (uuu in 1:length(unitList)){
#  print(paste("eps plotting",uuu), quote=FALSE)
  yValueList <- numeric()
  errorList <- numeric()
  for (iii in 1:length(yearList)){
     yValueList[iii] <- dAValueList[[iii]][uuu]
     errorList[iii] <- dAErrorList[[iii]][uuu] 
  }
  plotCI(yearList+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
         xlim=xLimits, ylim=dALimits,
         pch=uuu, col=colourList[uuu], cex=1.2,
         main = epsTitleString , 
         sub = NULL, xlab = "Year", ylab = expression((A-1)),
         xaxp=c(minYear,maxYear,maxYear-minYear)
         )

         
  addValue=TRUE
}
abline(h=0)
legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));

dev.off()
# end of eps plot

} # *** End of dA plots


# Output basic data
resultArray <- array(0,c(length(yearList)*10,length(unitList)))
for (iii in 1:length(yearList)){
  resultArray[iii,] <-  sigmaValueList[[iii]]
  resultArray[iii+length(yearList),] <-  sigmaErrorList[[iii]]
  resultArray[iii+length(yearList)*2,] <-  dmuValueList[[iii]]
  resultArray[iii+length(yearList)*3,] <-  dmuErrorList[[iii]]
  resultArray[iii+length(yearList)*4,] <-  dAValueList[[iii]]
  resultArray[iii+length(yearList)*5,] <-  dAErrorList[[iii]]
  resultArray[iii+length(yearList)*6,] <-  sigmatValueList[[iii]]
  resultArray[iii+length(yearList)*7,] <-  sigmaPrtList[[iii]]
  resultArray[iii+length(yearList)*8,] <-  dmutValueList[[iii]]
  resultArray[iii+length(yearList)*9,] <-  dmuPrtList[[iii]]
}

dataFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ValueErrorSigmaDmuDA.dat",sep="_")
print(paste("Writing data file ",dataFileName), quote=FALSE)
#write(resultArray,file=dataFileName, ncol=length(unitList))
#dataFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ValueErrorSigmaDmuDATRANS.dat",sep="_")
write(t(resultArray),file=dataFileName, ncol=length(unitList))
# end of basic data output
