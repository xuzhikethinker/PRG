# Plots for VP Analysis
#Need to first issue library(gplots)

# Values and upper and lower bounds  must be normalised and vectors
#measureName="Entropy" etc
vpAnalysisPlot <-function(valueVector, lowerVector, upperVector,
                          nameList,
                          outputSubdirectory, rootName,onlyUKName,communityName,valueName,measureName,measureLabel,measureFileLabel,
                          simplePlotOn,barPlotOn,screenOn,OSWindows,epsOn,pdfOn){

     fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,measureFileLabel,"r",numberRuns,sep="")
     subTitle=paste(rootName,onlyUKName,communityName,valueName,measureName,sep=" ")
     #print(paste("In vpAnalysisPlot for ",subTitle))
     xLabel=paste("Grouping by",communityName)
     allyvalues=c(valueVector,lowerVector,upperVector);
     yLimit <-range(allyvalues[is.finite(allyvalues)])
     #print(yLimit)
     
     # ....................
     if (simplePlotOn){
        if (screenOn) {
        if (OSWindows) windows() else quartz()
           plot(NULL,  main=mainTitle, sub=subTitle, ylim=yLimit, xlim=xLimit,  xlab=xLabel, ylab=measureLabel)
           points(valueVector, col="black", pch=1)
           points(lowerVector,  col="red", pch=2)
           points(upperVector,  col="blue", pch=3)
        }
     }

     # ....................
     if (barPlotOn){
       if (screenOn) {
            if (OSWindows) windows() else quartz()
            barplot2(valueVector, horiz = FALSE, space=0.5, col = "lightcyan", ylim = c(0,yLimit[2]),
              axisnames=TRUE, names.arg = nameList, las=2,
              main = mainTitle, sub=subTitle, xlab=xLabel, ylab="S/<S>",
              plot.ci = TRUE, ci.l = lowerVector, ci.u = upperVector, ci.color = "black")
       }
     }

     # .......................
     if (screenOn) {
      if (OSWindows) windows() else quartz()
      vpAnalysisValueCIPlot(valueVector, lowerVector, upperVector,
                               subTitle, communityName, valueName, measureName, measureLabel, nameList)
     }                               
     
     # PDF plot
     if(pdfOn){pdfFileName<-paste(fileNameFullRoot,".pdf",sep="")
     print(paste("pdf plotting",pdfFileName), quote=FALSE)
     pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
     vpAnalysisValueCIPlot(valueVector, lowerVector, upperVector,
                               subTitle, communityName, valueName, measureName, measureLabel, nameList)
     dev.off(which = dev.cur())
     }
     
     # EPS plot
     if (epsOn) {
     epsFileName<- paste(fileNameFullRoot,".eps",sep="")
     print(paste("eps plotting",epsFileName), quote=FALSE)
     postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)

     vpAnalysisValueCIPlot(valueVector, lowerVector, upperVector,
                               subTitle, communityName, valueName, measureName, measureLabel, nameList)
     dev.off(which = dev.cur())
     }

}

# value and confidence interval plot
vpAnalysisValueCIPlot <-function(valueVector, lowerVector, upperVector,
                          subTitle, communityName, valueName, measureName, measureLabel, nameList){
   mainTitle=paste(measureName, communityName,"vs",valueName,sep=" ")
   xLabel=paste("Grouping by",communityName)
   allyvalues=c(valueVector,lowerVector,upperVector);
   yLimit <-range(allyvalues[is.finite(allyvalues)])
   plotCI( (lowerVector+upperVector)/2, pch=NA, gap=0, col="black", ylim = yLimit,
          li = lowerVector, ui = upperVector,
          main = mainTitle, sub=subTitle, xlab=xLabel, ylab=measureLabel, xaxt="n")
   points(valueVector, col="black", pch=1)
   axis(side=1, at=1:length(valueVector), labels=nameList, las=2)
}



# *********************************************************************************
# Probability plots 
#

vpAnalysisAllProbPlot <-function(sumSqProbVector, entropyProbVector,
                          nameList,
                          outputSubdirectory, rootName,onlyUKName,communityName,valueName,
                          screenOn,OSWindows,epsOn,pdfOn){

     fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,"Prob","r",numberRuns,sep="")
     subTitle=paste(rootName,onlyUKName,communityName,valueName,sep=" ")

     if (screenOn) {
      if (OSWindows) windows() else quartz()
      vpAnalysisValueProbPlot( sumSqProbVector, entropyProbVector, nameList, subTitle, communityName, valueName)
     }
     
     # EPS plot
     if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
       vpAnalysisValueProbPlot( sumSqProbVector, entropyProbVector, nameList, subTitle, communityName, valueName)
       dev.off(which = dev.cur())
     }
     
     # PDF plot
     if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",pdfFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
       vpAnalysisValueProbPlot( sumSqProbVector, entropyProbVector, nameList, subTitle, communityName, valueName)
       dev.off(which = dev.cur())
     }
     
}

vpAnalysisValueProbPlot <-function( sumSqProbVector, entropyProbVector,
                          nameList, subTitle, communityName, valueName){
    mainTitle = paste("Probability",valueName,"Diversity higher in Null Model",sep=" ")
    xLabel=paste("Grouping by",communityName)
    plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,length(sumSqProbVector)+0.2), xlab=xLabel, ylab="Prob", xaxt="n")
    axis(side=1, at=1:length(nameList), labels=nameList, las=2) 
    points(sumSqProbVector,  col=colourlist[1], pch=1)
    points((1:length(sumSqProbVector)+0.2),entropyProbVector, col=colourlist[2], pch=2)
    unitList=c("p(f^2)","p(S)")
    legendPosition<-"topright" 
    legend(x=legendPosition ,y=NULL, unitList, col=colourlist[1:length(unitList)],pch=1:length(unitList));
}
