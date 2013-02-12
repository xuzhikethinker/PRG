# Need to install then load gplots library
#print("Need to issue library(gplots)", quote=FALSE)
colourList=c("black", "darkred", "darkblue", "darkgreen", "magenta", "brown");


#Root name for all files
#rootName="TSEgscholar111117short"
#rootName="TSEWoSresearcherid"
#rootName="PendryWoSresearcherid"

rootNameList=c("TSEWoSresearcherid","TSEgscholar111117short")
dataLabel=c("WoS","gScholar")
outputRootName="TSEWoSgScholar"

PendryOn=FALSE
if (PendryOn){
 rootNameList=c("PendryWoSresearcherid")
 dataLabel=c("WoS")
 outputRootName="PendryWoS"
}

plotsOn=FALSE
OSWindows=TRUE
screenOn=TRUE
pdfOn=TRUE
epsOn=TRUE
pngOn=TRUE
squareAxesOn=TRUE
hValuesOn=FALSE
hOtherValuesOn=FALSE

readBibData <- function(rootName){
  fileName <- paste(rootName,".dat",sep="")
  df <- read.table(fileName, header=TRUE, sep="\t", fill=TRUE);

  hvalue=0;
  h12value=0;
  h21value=0;

  for (ppp in 1:length(df$Rank)){
   ccc=df$Citations[ppp]
   rrr=df$Rank[ppp]
   if (ccc>=rrr)   hvalue=max(rrr,hvalue)
   if (ccc>=2*rrr) h21value=max(rrr,h21value)
   if (2*ccc>=rrr) h12value=max(rrr,h12value)
  }
  print(paste(rootName,"has h=",hvalue,", h12=",h12value,", h21=",h21value),quote=FALSE)
  outputList <-list(Citations=df$Citations,Rank=df$Rank, hvalue=hvalue, h12value=h12value, h21value=h21value)
}

citeList <- list()
refList <- list()
outputList <-list()

for (iii in 1:length(rootNameList)) {
   outputList[[iii]]<-readBibData(rootNameList[iii])
   #cmax <- (trunc(max(outputList[[iii]]$Citations)/10)+1)*10
   #rmax <- length(outputList[[iii]]$Rank)
   cmax <- 10^(trunc(log10(max(outputList[[iii]]$Citations)))+1)*1.05
   rmax <- length(outputList[[iii]]$Rank)
   if (squareAxesOn) {
    vmax=max(cmax,rmax)
    cmax=vmax
    rmax=vmax
   }
}


xlabel="Rank"
ylabel="Citations"

#if (OSWindows) windows() else quartz()

#barplot(outputList[[1]]$Citations, beside=TRUE, xlim=c(0,rmax), ylim=c(0,cmax), names.arg=outputList[[1]]$Rank,  xaxs = "i",  yaxs = "i", 
#          xlab=xlabel, ylab=ylabel )
#       lines(1:rmax,1:rmax,lty=1)
#       outputList[[1]]$hvalue=11
#       lines(c(0,outputList[[1]]$hvalue),c(outputList[[1]]$hvalue,outputList[[1]]$hvalue),lty=2)
#       lines(c(outputList[[1]]$hvalue+0.5,outputList[[1]]$hvalue+0.5),c(0,outputList[[1]]$hvalue),lty=2)

# generic plot function
mainPlot <- function(){
  cexValue=1.5
  plot(x=NULL,  y=NULL, log="xy", xlim=c(0.9,rmax), ylim=c(0.9,cmax),  xaxs = "i",  yaxs = "i", 
           xlab=xlabel, ylab=ylabel, cex=cexValue )
  for (iii in 1:length(outputList)){
       points(outputList[[iii]]$Rank, outputList[[iii]]$Citations, cex=1.5, col=colourList[1+iii], pch=iii )
       if (hValuesOn) {
         lines(c(1,outputList[[iii]]$hvalue),c(outputList[[iii]]$hvalue,outputList[[iii]]$hvalue),lty=1+iii, col=colourList[1+iii])
         lines(c(outputList[[iii]]$hvalue,outputList[[iii]]$hvalue),c(1,outputList[[iii]]$hvalue),lty=1+iii, col=colourList[1+iii])
         }
  }         
  if (hValuesOn) {
    lines(1:rmax,1:cmax,lty=1)
    text(0.505*rmax,0.5*cmax,"h", pos=1, cex=cexValue )
  }
  if (hOtherValuesOn) {
     lines(1:rmax/2,1:cmax,lty=3)
     h12label=expression(paste("h"["1:2"]))
     text(0.7*rmax/2,0.7*cmax,h12label, pos=2, cex=cexValue )
     h21label=expression(paste("h"["2:1"]))
     text(rmax*0.72,0.7*cmax/2,h21label, pos=1, cex=cexValue )
  }   
  #legend (x=rmax*0.6,y=cmax/10, 
  legend (x="bottomleft",y=NULL, dataLabel[1:length(outputList)], col=colourList[1+1:length(outputList)],lty=1+1:length(outputList),pch=1:length(outputList), cex=cexValue);
}       
# end of generic plot function

typeName="log"
if (hValuesOn) typeName=paste(typeName,"h11",sep="")
if (hOtherValuesOn) typeName=paste(typeName,"h12h21",sep="")

if (screenOn){
       if (OSWindows) windows() else quartz()
       #print(paste(graphName,"on screen"), quote=FALSE)
       mainPlot()
       #abline(v=outputList[[1]]$hvalue, lty=2)
}
# EPS plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (epsOn){
       epsFileName<- paste(outputRootName,typeName,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=16, fonts=c("serif", "Palatino"))
       #postscript(epsFileName, fonts=c("serif", "Palatino"))
       mainPlot()
       dev.off(which = dev.cur())
}
# PDF plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (pdfOn){
       pdfFileName<- paste(outputRootName,typeName,".pdf",sep="")
       print(paste("pdf plotting",pdfFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=16, fonts=c("serif", "Palatino"))
       mainPlot()
       dev.off(which = dev.cur())
}
# PNG plot
if (pngOn){
       pngFileName<- paste(outputRootName,typeName,".png",sep="")
       print(paste("png plotting",pngFileName), quote=FALSE)
       png(pngFileName, height=480, width=480, pointsize=12)
       mainPlot()
       dev.off(which = dev.cur())
}


