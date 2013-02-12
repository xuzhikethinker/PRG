#rootName="aegean39";
#typeName="S1L1";
#produces dendrogram data based n adjacency matrix 
DendrogramFromFile <- function( rootName, typeName, distEndingName="distance.dat", methodNumber,numberRows=39, mainTitleOn=TRUE) {
   namesFileName<-paste(rootName,"names.dat",sep="");
   #distanceFileName<-paste(rootName,typeName,distEndingName,sep="")
   distanceFileName<-paste(rootName,typeName,distEndingName,sep="")
   nameList <- scan(namesFileName, what="", skip=1);
   distanceVector <- scan(distanceFileName,0);
   distanceMatrix <- matrix(distanceVector,nrow=numberRows);
   DendrogramCalcPlot ( rootName, typeName, methodNumber, nameList, distanceMatrix,  mainTitleOn)
}

DendrogramCalcPlot <- function( rootName, typeName, methodNumber=4, nameList, distanceMatrix,  mainTitleOn=TRUE) {
   distanceDist <- as.dist(distanceMatrix)
   hclustMethod <- c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
   methodName=hclustMethod[methodNumber]

   distanceHclust <- hclust(distanceDist, method=methodName) #, members=NULL
   # Can't get the xlabel and subtitle to disappear with NULL
   #theTitle = title(main = paste(rootName,typeName," ",methodName,sep=""),   sub="", xlab="", ylab="Distance"  )
   #give new plotting window
   if (mainTitleOn) {mainTitle <- rootName
   subTitle <- paste(typeName," ",methodName,sep="")}
   else {mainTitle <- ""
   subTitle <- ""}
   ymin=min(distanceDist)*0.9
   ymax=max(distanceDist)*1.1
   windows()
   plclust(distanceHclust, labels=nameList, frame.plot=FALSE,  hang=-10,  main = mainTitle,   sub=subTitle, xlab="", ylab="Distance"   )
   # now plot eps file
   epsFileName<-paste(rootName,typeName,methodName,".eps",sep="");
   print(paste("eps plotting",epsFileName), quote=FALSE)
   postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
   plclust(distanceHclust, labels=nameList, frame.plot=FALSE,  hang=-10, main = mainTitle,   sub=subTitle, xlab="", ylab="Distance"  )
   dev.off()
   #plots pdf file
   pdfFileName<-paste(rootName,typeName,methodName,".pdf",sep="");
   print(paste("pdf plotting",pdfFileName), quote=FALSE)
   pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
   plclust(distanceHclust, labels=nameList, frame.plot=FALSE,  hang=-10, main = mainTitle,   sub=subTitle, xlab="", ylab="Distance"   )
   dev.off()
   distanceHclust
}
