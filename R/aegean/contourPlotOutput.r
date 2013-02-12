
contourPlotOutput <- function(fileNameFullRoot, xVec,yVec,zMat, mainTitle="", xLabelString="", yLabelString="", screenOn=TRUE, epsOn=TRUE, pdfOn=TRUE, OSWindows=TRUE){

   if (screenOn){
          if (OSWindows) windows() else quartz()
          contour(xVec,yVec,zMat, main=mainTitle, xlab=xLabelString, ylab=yLabelString)
   }
   # EPS plot
   if (epsOn){
          epsFileName<- paste(fileNameFullRoot,".eps",sep="")
          print(paste("eps plotting",epsFileName), quote=FALSE)
          postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
          contour(xVec,yVec,zMat, main=mainTitle, xlab=xLabelString, ylab=yLabelString)
          dev.off(which = dev.cur())
   }
   # PDF plot
   if (pdfOn){
          pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
          print(paste("pdf plotting",pdfFileName), quote=FALSE)
          pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
          contour(xVec,yVec,zMat, main=mainTitle, xlab=xLabelString, ylab=yLabelString)
          dev.off(which = dev.cur())
   }
}


