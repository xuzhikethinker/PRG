#rootName="aegean39";
#typeName="S1L1";
#produces dendrogram data based n adjacency matrix 
DendrogramFromFile <- function( rootName, typeName, methodNumber,numberRows=39, mainTitleOn=TRUE) {
   namesFileName<-paste(rootName,"names.dat",sep="");
   #distanceFileName<-paste(rootName,typeName,"distance.dat",sep="")
   distanceFileName<-paste(rootName,typeName,"distance.dat",sep="")
   nameList <- scan(namesFileName, what="", skip=1);
   distanceVector <- scan(distanceFileName,0);
   distanceMatrix <- matrix(distanceVector,nrow=numberRows);
   DendrogramCalcPlot ( rootName, typeName, methodNumber, nameList, distanceMatrix,  mainTitleOn)
}


DendrogramCalcPlot <- function( rootName, typeName, methodNumber, nameList, distanceMatrix,  mainTitleOn=TRUE) {
   distanceDist <- as.dist(distanceMatrix)
   hclustMethod <- c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
   methodName=hclustMethod[methodNumber]

   distanceHclust <- hclust(distanceDist, method=methodName) #, members=NULL
   DendrogramPlot( "", rootName, typeName, methodName, nameList, distanceDist, distanceHclust,  mainTitleOn, TRUE, TRUE, TRUE, TRUE)
}

DendrogramPlot <- function( outputDir, rootName, typeName, methodName, nameList, distanceDist,  distanceHclust, mainTitleOn=TRUE, OSWindows=TRUE, screenOn=TRUE, pngPlotOn=TRUE, epsPlotOn=TRUE, pdfPlotOn=TRUE) {
   # Can't get the xlabel and subtitle to disappear with NULL
   #theTitle = title(main = paste(rootName,typeName," ",methodName,sep=""),   sub="", xlab="", ylab="Distance"  )
   #give new plotting window
   if (mainTitleOn) {mainTitle <- rootName
   subTitle <- paste(typeName," ",methodName,sep="")}
   else {mainTitle <- ""
   subTitle <- ""}
   ymin=min(distanceDist)*0.9
   ymax=max(distanceDist)*1.1
   if (screenOn){
     if (OSWindows) windows() else quartz()
     plclust(distanceHclust, labels=nameList, frame.plot=FALSE,  hang=-10,  main = mainTitle,   sub=subTitle, xlab="", ylab="Distance"   )
     }
   # now plot png file
   if (pngPlotOn) {
     pngFileName<-paste(outputDir,rootName,typeName,methodName,".png",sep="");
     cat(paste("pmg plotting",pngFileName,"\n"))
     png(filename = pngFileName, height=600, width=600, units= "px", pointsize=10)
     # png(filename = "Rplot%03d.png", width = 480, height = 480, units = "px", pointsize = 12, bg = "white", res = NA, restoreConsole = TRUE)
     plclust(distanceHclust, labels=nameList, frame.plot=FALSE,  hang=-10, main = mainTitle,   sub=subTitle, xlab="", ylab="Distance"  )
     dev.off()
   }
   # now plot eps file
   if (epsPlotOn) {
     epsFileName<-paste(outputDir,rootName,typeName,methodName,".eps",sep="");
     cat(paste("eps plotting",epsFileName,"\n"))
     postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
     plclust(distanceHclust, labels=nameList, frame.plot=FALSE,  hang=-10, main = mainTitle,   sub=subTitle, xlab="", ylab="Distance"  )
     dev.off()
   }
   #plots pdf file
   if (pdfPlotOn) {
     pdfFileName<-paste(outputDir,rootName,typeName,methodName,".pdf",sep="");
     cat(paste("pdf plotting",pdfFileName,"\n"))
     pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
     plclust(distanceHclust, labels=nameList, frame.plot=FALSE,  hang=-10, main = mainTitle,   sub=subTitle, xlab="", ylab="Distance"   )
     dev.off()
   }
}
