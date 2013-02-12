# Analyses various citation distributions
#
# Note need to install (once per installation) then load (once per session) gplots library.
# The loading is done by issuing command 
#   library(gplots)
# This is the primary file to source.  It sources several other files.
# This file sources directly (NN=12) cranalysisNN.r and calcZCorrelationsNN.r
# while cranalysisNN.r then sources several other files:-
#  logbincount.r, readRCYfileNN.r, citationReferenceIndexNN.r and citationReferenceAllData.r

# Based on BK files summer 2011

# Need to install then load gplots library
print("Need to issue library(gplots)", quote=FALSE)
source("cranalysis12.r")
source("calcZCorrelations12.r")
colourList=c("black", "red", "blue", "green", "magenta", "brown","slategrey");

# TRUE (FALSE) for c (c/r) 
cNotCoverR=TRUE

# Number of fitting parameters, 1 or 3 
numberParameters=3

# TRUE (FALSE) for normalising measure by its average <c> or <c/r>
normalise=TRUE

# TRUE (FALSE) for Faculties (Departments) 
facultyNotDept=TRUE

# set dataSourceIndex to select data type indicated in dataSourceList
dataSourceList=c("IC","arXiv","EBRP")
dataSourceIndex=3

#True to analyse all Imperial data in which case both above parameters ignored
allData=FALSE

#Calculate correlations between z_f and z_r
calcZCorrel=FALSE

#Only use publications with c/<c> greater than:
minCoverMeanC=0.1 #0.1

# folloing paramters control various outputs
parameterAnalysisOn=TRUE
plotsOn=TRUE
OSWindows=TRUE
screenOn=TRUE
pdfPlotOn=FALSE
epsPlotOn=TRUE
datafileOn=FALSE
texTablesOn=TRUE

# years to study (inclusive) [1997-2007 is standard] 
#minYear=1997 #1996 for depts #1997 for facs #1997 for arXiv #2002 for EBRP
#maxYear=2004 #2007           #2007          #2004           #2010

#Number of years to bin together for analysis 
#binnedYears=1

# Set years to cover
if(dataSourceIndex==1){
  if (facultyNotDept){ # IC Faculty files
       minYear=1997 
       maxYear=2007
       binnedYears=1
       skipYears=1
    }else {# IC Dept files
       minYear=1997 
       maxYear=2007
       binnedYears=3
       skipYears=1
    }  
}
if(dataSourceIndex==2){ # arXiv files
       minYear=1997 
       maxYear=2004
       binnedYears=1
       skipYears=1      
}
if(dataSourceIndex==3){ # EBRP files
       minYear=2002 
       maxYear=2006 #2010
       binnedYears=1
       skipYears=4 # only every 4th year available
  }

# can override years to study (inclusive) [1997-2007 is standard] 
#minYear=1997 #1996 for depts #1997 for facs #1997 for arXiv #2002 for EBRP
#maxYear=2004 #2007           #2007          #2004           #2010

#Number of years to bin together for analysis 
#binnedYears=1

# now set list of years, lower end of each year bin
# for each year, y, in list it will collect all papers between years [y,y+binnedYears-1] inclusive
yearList <- seq(minYear,maxYear,skipYears)
print(paste("yearList=",yearList),quote=FALSE)

# input and output directories - should be subdirectories of one containing this file
inputDir ="input/"
outputDir ="output/"

if(dataSourceIndex==1){ # IC files
  rootName="IC110401"
  typeString<-"Department"
  if (facultyNotDept){typeString<-"Faculty" }
}
if(dataSourceIndex==2){ 
  rootName="arXiv"
  typeString<-"arXiv"
}
if(dataSourceIndex==3){ 
  rootName="ebrp"
  typeString<-"EBRP"
  }
      
outputDirRootName=paste(outputDir, rootName,sep="")

if(calcZCorrel){
calcZCorrelations(inputDir, outputDir, rootName,  facultyNotDept, dataSourceIndex, minYear, maxYear,plotsOn =TRUE,
                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=FALSE, epsPlotOn=TRUE, minCoverMeanC=0,minCRoverMeanCR=0)
    stop("Done")
}


valueName="(C/R)"
valueFileName="CR"
if (cNotCoverR) {
valueName="C"
valueFileName="C"
}
normaliseString=""
normaliseFileString="unorm"
if (normalise) {
normaliseString=paste("/<",valueName,">",sep="")
normaliseFileString="norm"
}

parameterString=paste("p",numberParameters,sep="")



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
rValList <- list() 
stdErrpDofList <- list() 
binCountList <- numeric()
iii=0 
for (iii in 1:length(yearList))
{ 
    yyy <- yearList[[iii]] 
    lst <-cranalysis(inputDir, outputDir, rootName, cNotCoverR, normalise, facultyNotDept, dataSourceIndex, allData, yyy, yyy+binnedYears-1, numberParameters, parameterAnalysisOn, plotsOn, OSWindows, screenOn, pdfPlotOn, epsPlotOn,minCoverMeanC) 
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
    rValList[[iii]] <- lst$rVal
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
  sigma2Limits <- c(0.5,2.5)
  #sigma2Limits <- c(0.7,2.2)
  legendPosition <-"topright"
}
if (cNotCoverR & (numberParameters>2)) {
  sigmaLimits <- c(1.0,2.0)
  #sigma2Limits <- c(0.55,3.55)
  sigma2Limits <- c(0.5,2.5)
  legendPosition <-"topleft"
  if (!normalise){
    sigmaLimits <- c(0.7,2.0)
    sigma2Limits <- NULL #c(0.0,5.0)
  }
}
if (!cNotCoverR & (numberParameters<2)) {
  sigmaLimits <- c(0.9,1.4)
  #sigma2Limits <- c(0.4,2.25)
  sigma2Limits <- c(0.7,1.8)
  legendPosition <-"topright"
}
if (!cNotCoverR & (numberParameters>2)) {
  sigmaLimits <- c(0.9,1.3)
  #sigma2Limits <- c(0.35,2.0)
  sigma2Limits <- c(0.8,1.8)
  legendPosition <-"topright"
}

# define position of ticks on x axis
ntick=(maxYear-minYear)/(skipYears*binnedYears)
if (ntick<1) ntick=1
xaxpVector=c(minYear,maxYear,ntick)
  
titleString <- paste(numberParameters," parameter fit for ",typeString,valueName,normaliseString)
if (screenOn){
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
            xaxp=xaxpVector
            )

            
     addValue=TRUE
   }
   if (cNotCoverR) {
    abline(h=1.3,lty=2)
   }
   legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));
}# end of windows() plot

# eps plot
if (epsPlotOn){
   print(paste("eps plotting sigma^2",titleString), quote=FALSE)
   epsTitleOn=FALSE
   epsTitleString=""
   if (epsTitleOn) epsTitleString=titleString
   epsFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"sigma2.eps",sep="_")
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
            xaxp=xaxpVector
            )

            
     addValue=TRUE
   }
   if (cNotCoverR) {
    abline(h=1.3,lty=2)
   }
   legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));

   dev.off()
} # end of eps plot



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
  dmuLimits <- c(-0.4,0.2)
  legendPosition <-"bottomright"
}

# windows() plot
if (screenOn){
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
            xaxp=xaxpVector
            )

            
     addValue=TRUE
   }
   abline(h=0)
   legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));
}# end of windows() plot

# eps plot
if (epsPlotOn){
   print(paste("eps plotting dmu=(mu+sigma^2)",titleString), quote=FALSE)
   epsTitleOn=FALSE
   epsTitleString=""
   if (epsTitleOn) epsTitleString=titleString
   epsFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"dmu.eps",sep="_")
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
            xaxp=xaxpVector
            )

            
     addValue=TRUE
   }
   abline(h=0)
   legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));

   dev.off()
}# end of eps plot

} # *** End of dmu plots


#### dA plots ##########################################################
if (numberParameters>2) {

dALimits <- c(-0.1,0.1)
  
titleString <- paste(numberParameters," parameter fit for ",typeString,valueName,normaliseString)
if (cNotCoverR) {
  #dALimits <- c(-0.2,0.6)
  dALimits <- c(-0.3,0.3)
  legendPosition <-"topleft"
  if (!normalise){
    dALimits <- NULL #c(0.0,5.0)
  }
}
else {
  dALimits <- c(-0.2,0.1)
  legendPosition <-"bottomright"
}

# windows() plot
if (screenOn){
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
           xaxp=xaxpVector
           )

           
    addValue=TRUE
  }
  abline(h=0)
  legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));
}# end of windows() plot

# eps plot
if (epsPlotOn){
   print(paste("eps plotting dA=(A-1)",titleString), quote=FALSE)
   epsTitleOn=FALSE
   epsTitleString=""
   if (epsTitleOn) epsTitleString=titleString
   epsFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"dA.eps",sep="_")
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
            xaxp=xaxpVector
            )

            
     addValue=TRUE
   }
   abline(h=0)
   legend (x=legendPosition ,y=NULL, unitList, col=colourList[1:length(unitList)],pch=1:length(unitList));

   dev.off()
}# end of eps plot

} # *** End of dA plots

if(texTablesOn){
    texFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"table.txt",sep="_")
    print(paste("Writing tex table to", texFileName))
    sink(texFileName)
    if(cNotCoverR){
        cat("\\begin{tabular}{|l|l|l|l|l|l|l|l|l|}")
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
            cat("\nYear & Faculty & $N_p$ & $\\langle c_r \\rangle$ & $\\langle r \\rangle$ & $\\sigma^{2}$ & $\\mu + \\frac{\\sigma^{2}}{2}$ &  res. err./d.o.f.  \\\\ \\hline")
        }
    }
    else{
        if(cNotCoverR){
            cat("\nYear & Faculty & $N_p$ & $c_0$ & $\\sigma^{2}$ &  res. err./d.o.f.  & Bins & $\\chi^2/\\mathrm{d.o.f.}$ \\\\ \\hline");
        }
        else{
            cat("\nYear & Faculty & $N_p$ & $\\langle c_r \\rangle$ & $\\langle r \\rangle$ & $\\sigma^{2}$ & res. err./d.o.f. & Bins & $\\chi^2/\\mathrm{d.o.f.}$ \\\\ \\hline");
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
            sigma2 = format(sigmaValueList[[iii]][jjj]*sigmaValueList[[iii]][jjj],digits=2,ns=2);
            sigma2err = round(100*2*sigmaValueList[[iii]][jjj]*sigmaErrorList[[iii]][jjj],digits=0);
            s = "";
            if(cNotCoverR){
                s = paste("&", format(c0ValList[[iii]][jjj],digits=2,ns=2));
            }
            else{
                s = paste("&", format(c0ValList[[iii]][jjj],digits=2,ns=2),"&",format(rValList[[iii]][jjj]));
            }
            if(numberParameters==3){
                cat(paste("\n&",gsub("_"," ",unitList[[jjj]]),
                    "&", noOfPapersList[[iii]][jjj],
                    s,
                    "&", paste(sigma2,"(",sigma2err,")",sep=""),
                    "&", paste(format(dmuValueList[[iii]][jjj],digits=1,ns=1),"(",round(10*dmuErrorList[[iii]][jjj],digits=0),")",sep=""),
                    "&", format(stdErrpDofList[[iii]][jjj],digits=2,ns=2),"\\\\"))
            }
            else{

                cat(paste("\n&",gsub("_"," ",unitList[[jjj]]),
                 "&", noOfPapersList[[iii]][jjj],
                 s,
                 "&", paste(sigma2,"(",sigma2err,")",sep=""),
                 "&", format(stdErrpDofList[[iii]][jjj],digits=2,ns=2),
                 "&", binCountList[[(iii-1)*(length(unitList))+ jjj]],
                 "&", format(chiSqValList[(iii-1)*length(unitList)+jjj]/(binCountList[[(iii-1)*(length(unitList))+ jjj]]-1),digits=1,ns=1),"\\\\"))
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
    print(paste("min chi2", format(min(chi_tmp),digits=2,ns=2)))
    print(paste("max chi2", format(max(chi_tmp),digits=2,ns=2)))
    print(paste("mean chi2", format(mean(chi_tmp),digits=2,ns=2)))
    print("*****************************************")
}
    print("Overall")
    
    Chi2perDoF = chiSqValList / (binCountList - 1)
    print(paste("min chi2/dof", format(min(Chi2perDoF),digits=2,ns=2)))
    print(paste("max chi2/dof", format(max(Chi2perDoF),digits=2,ns=2)))
    print(paste("mean chi2/dof", format(mean(Chi2perDoF),digits=2,ns=2)))

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
    print(paste(unitList[[jjj]]," sigma2 = ",format(unitAverages[[jjj]],digits=2,ns=2), "+/-",format(unitErrors[[jjj]],digits=2)))
} 
#print(paste("Overall average = ",format(mean(unitAverages),digits=2,ns=2),"+/-",format(mean(unitErrors),digits=2,ns=2))) 
if(allData){
    print(paste("Total papers = ",0.5*sum(Reduce(`+`,noOfPapersList))))
}
if(!allData){
    print(paste("Total papers = ",sum(Reduce(`+`,noOfPapersList))))
}


