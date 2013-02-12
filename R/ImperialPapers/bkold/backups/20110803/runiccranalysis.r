# Need to install then load gplots library
print("Need to issue library(gplots)", quote=FALSE)
source("iccranalysis.r")
colourList=c("black", "red", "blue", "green", "magenta", "brown");


#Root name for all files
rootName="IC20090521"

# TRUE (FALSE) for c (c/r) 
cNotCoverR=FALSE

# Number of fitting parameters, 1 or 3 
numberParameters=1

# TRUE (FALSE) for normalising measure by its average <c> or <c/r>
normalise=TRUE

# TRUE (FALSE) for Faculties (Departments) 
facultyNotDept=TRUE

# True to analyse arXiv data in which case above parameter ignored 
arXiv=TRUE

#Only use publications with c/<c> greater than:
minCoverMeanC=0.0 #0.1

# years to study (inclusive) [1997-2007 is standard] 
minYear=1997 #1996 for depts #1997 for facs #1997 for arXiv
maxYear=2004 #2007           #2007          #2004 

#Number of years to bin together for analysis 
binnedYears=1


parameterAnalysisOn=TRUE
plotsOn=TRUE
OSWindows=TRUE
screenOn=FALSE
pdfPlotOn=FALSE
epsPlotOn=TRUE
datafileOn=FALSE
texTablesOn=TRUE

#iccranalysis(rootName, cNotCoverR, normalise, facultyNotDept, minYearInput, maxYearInput, numberParameters, parameterAnalysisOn, plotsOn, OSWindows, screenOn, pdfPlotOn, epsPlotOn) 

#(rootName="IC20090521", 
#                                    cNotCoverR=TRUE, normalise=TRUE, 
#                                    facultyNotDept=TRUE, 
#                                    minYearInput=1900, maxYearInput=2009, numberParameters=3, 
#                                    parameterAnalysisOn=TRUE, plotsOn =TRUE,
#                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=TRUE, epsPlotOn=TRUE)
yearList <- seq(minYear,maxYear,binnedYears)


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

if(arXiv){
typeString<-"arXiv"
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
noOfPapersList <- list() 
chiSqValList <- numeric() 
c0ValList <- list() 
stdErrpDofList <- list() 
binCountList <- numeric()
iii=0 
for (iii in 1:length(yearList))
{ 
    yyy <- yearList[[iii]] 
    lst <-iccranalysis(rootName, cNotCoverR, normalise, facultyNotDept, arXiv, yyy, yyy+binnedYears-1, numberParameters, parameterAnalysisOn, plotsOn, OSWindows, screenOn, pdfPlotOn, epsPlotOn,minCoverMeanC) 
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
    noOfPapersList[[iii]] <- lst$noOfPapers 
      
    stdErrpDofList[[iii]] <- lst$stdErrpDof 
    c0ValList[[iii]] <- lst$c0Val

    for(jjj in 1:length(lst$unitList)){
        
        binCountList[(iii-1)*(length(lst$unitList))+ jjj] <- lst$binCount[[jjj]]
        chiSqValList[(iii-1)*(length(lst$unitList))+ jjj] <- lst$chiSqVal[[jjj]]        
    }
    print(lst$chiSqVal)
} 

if (OSWindows) windows() else quartz()
#unitList <- lst$unitList 
unitList <- gsub("Department","Dept.",lst$unitList)
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
yearoffset<-0.5*(binnedYears-1)

#### sigma or sigma^2 plots #######################################################

xLimits <- c(minYear-length(unitList)*xshift,maxYear+length(unitList)*xshift)
if (cNotCoverR & (numberParameters<2)) {
  sigmaLimits <- c(0.9,1.5)
  sigma2Limits <- c(0.7,2)
  #sigma2Limits <- c(0.7,2)
  legendPosition <-"topright"
}
if (cNotCoverR & (numberParameters>2)) {
  sigmaLimits <- c(1.0,2.0)
  #sigma2Limits <- c(0.55,3.55)
  sigma2Limits <- c(0.8,3.2)
  legendPosition <-"topright"
  if (!normalise){
    sigmaLimits <- c(0.7,2.0)
    sigma2Limits <- NULL #c(0.0,5.0)
  }
}
if (!cNotCoverR & (numberParameters<2)) {
  sigmaLimits <- c(0.9,1.4)
  #sigma2Limits <- c(0.4,2.25)
  sigma2Limits <- c(0.8,1.6)
  legendPosition <-"topright"
}
if (!cNotCoverR & (numberParameters>2)) {
  sigmaLimits <- c(0.9,1.3)
  #sigma2Limits <- c(0.35,2.0)
  sigma2Limits <- c(0.7,2.2)
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
  plotCI(yearList+yearoffset+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
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
  plotCI(yearList+yearoffset+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
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
  dmuLimits <- c(-0.7,0.5)
  legendPosition <-"bottomright"
  if (!normalise){
    dmuLimits <- NULL #c(0.0,5.0)
  }
}
else {
  dmuLimits <- c(-0.8,0.2)
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
  plotCI(yearList+yearoffset+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
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
  plotCI(yearList+yearoffset+yearoffset+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
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
  #dALimits <- c(-0.2,0.6)
  dALimits <- c(-0.35,0.45)
  legendPosition <-"topright"
  if (!normalise){
    dALimits <- NULL #c(0.0,5.0)
  }
}
else {
  dALimits <- c(-0.4,0.1)
  legendPosition <-"bottomright"
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
  plotCI(yearList+yearoffset+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
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
  plotCI(yearList+yearoffset+(uuu-(length(unitList)+1)/2)*xshift,yValueList,errorList, add=addValue, 
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

if(texTablesOn){
    texFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"table.txt",sep="_")
    print(paste("Writing tex table to", texFileName))
    sink(texFileName)
    if(numberParameters==3){
        cat("\\begin{tabular}{|l|l|l|l|l|l|l|l|}")
    }
    else{
        cat("\\begin{tabular}{|l|l|l|l|l|l|l|l|}")
    }
    cat("\n\\hline")
    
    if(numberParameters==3){
        if(cNotCoverR){
            cat("\nYear & Faculty & $N_p$ & $c_0$ & $\\sigma^{2}$ & $\\mu + \\frac{\\sigma^{2}}{2}$ &  res. err.r/d.o.f.  \\\\ \\hline")
        }
        else{
            cat("\nYear & Faculty & $N_p$ & $\\langle c_r \\rangle$ & $\\sigma^{2}$ & $\\mu + \\frac{\\sigma^{2}}{2}$ &  res. err./d.o.f.  \\\\ \\hline")
        }
    }
    else{
        if(cNotCoverR){
            cat("\nYear & Faculty & $N_p$ & $c_0$ & $\\sigma^{2}$ &  res. err./d.o.f.  & Bins & $\\chi^2/\\mathrm{d.o.f.}$ \\\\ \\hline");
        }
        else{
            cat("\nYear & Faculty & $N_p$ & $\\langle c_r \\rangle$ & $\\sigma^{2}$ & res. err./d.o.f. & Bins & $\\chi^2/\\mathrm{d.o.f.}$ \\\\ \\hline");
        }
    }
    for (iii in 1:length(yearList)){
        for(jjj in 1:length(unitList)){
            if(jjj==1){
                if(binnedYears==1){
                    cat(paste("\n\\multirow{",length(unitList),"}{*}{",yearList[[iii]],"}",sep=""))
                }
                else{
                    if(iii==length(yearList)){
                        cat(paste("\n\\multirow{",length(unitList),"}{*}{",yearList[[iii]],"}",sep=""))
                    }
                    else{
                        cat(paste("\n\\multirow{",length(unitList),"}{*}{",yearList[[iii]],"-",yearList[[iii+1]]-1,"}",sep=""))
                    }
                }
            }
            sigma2 = round(sigmaValueList[[iii]][jjj]*sigmaValueList[[iii]][jjj],digits=2);
            sigma2err = round(100*2*sigmaValueList[[iii]][jjj]*sigmaErrorList[[iii]][jjj],digits=0);
            if(numberParameters==3){
                cat(paste("\n&",gsub("_"," ",unitList[[jjj]]),
                    "&", noOfPapersList[[iii]][jjj],
                    "&", round(c0ValList[[iii]][jjj],digits=2),
                    "&", paste(sigma2,"(",sigma2err,")",sep=""),
                    "&", paste(round(dmuValueList[[iii]][jjj],digits=1),"(",round(10*dmuErrorList[[iii]][jjj],digits=0),")",sep=""),
                    "&", format(stdErrpDofList[[iii]][jjj],digits=2),"\\\\"))
            }
            else{

                cat(paste("\n&",gsub("_"," ",unitList[[jjj]]),
                 "&", noOfPapersList[[iii]][jjj],
                 "&", round(c0ValList[[iii]][jjj],digits=2),
                 "&", paste(sigma2,"(",sigma2err,")",sep=""),
                 "&", round(stdErrpDofList[[iii]][jjj],digits=2),
                 "&", binCountList[[(iii-1)*(length(unitList))+ jjj]],
                 "&", round(chiSqValList[(iii-1)*length(unitList)+jjj]/(binCountList[[(iii-1)*(length(unitList))+ jjj]]-1),digits=1),"\\\\"))
            }
 
            if(jjj==length(unitList)){
                cat("\\hline")
            }

        }
    }
    cat("\n\\end{tabular}")
  sink()

}
unitAverages <- mat.or.vec(length(unitList),1)
unitErrors <- mat.or.vec(length(unitList),1)

bins = sort(unique(binCountList))
for(kkk in 1:length(bins)){
    print(paste(bins[[kkk]],"bins"))
    chi_tmp = chiSqValList[binCountList == bins[[kkk]]]
    print(paste("min chi2", min(chi_tmp)))
    print(paste("max chi2", max(chi_tmp)))
    print(paste("mean chi2", mean(chi_tmp)))
    print("*****************************************")
}
    print("Overall")
    
    Chi2perDoF = chiSqValList / (binCountList - 1)
    print(paste("min chi2/dof", min(Chi2perDoF)))
    print(paste("max chi2/dof", max(Chi2perDoF)))
    print(paste("mean chi2/dof", mean(Chi2perDoF)))

for (iii in 1:length(yearList)){
    for(jjj in 1:length(unitList)){
        unitAverages[[jjj]] <- unitAverages[[jjj]] + sigmaValueList[[iii]][jjj] * sigmaValueList[[iii]][jjj]
        unitErrors[[jjj]] <- unitErrors[[jjj]] + 2 * sigmaValueList[[iii]][jjj] * sigmaErrorList[[iii]][jjj]
    }
}

#print(paste("Mean Chi2 = ",meanChi2))
unitAverages <- unitAverages / length(yearList)
unitErrors <- unitErrors / length(yearList)
for(jjj in 1:length(unitList)){
    print(paste(unitList[[jjj]]," sigma2 = ",round(unitAverages[[jjj]],digits=2), "+/-",round(unitErrors[[jjj]],digits=2)))
} 
print(paste("Overall average = ",round(mean(unitAverages),digits=2),"+/-",round(mean(unitErrors),digits=2))) 
print(paste("Total papers = ",sum(Reduce(`+`,noOfPapersList))))
