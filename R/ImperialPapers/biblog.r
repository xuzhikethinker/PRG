# Need to install then load gplots library
#print("Need to issue library(gplots)", quote=FALSE)
colourList=c("black", "darkred", "darkblue", "darkgreen", "magenta", "brown");


#Root name for all files
#rootName="TSEWoSresearcherid"
rootName="PendryWoSresearcherid"


plotsOn=FALSE
OSWindows=TRUE
screenOn=TRUE
pdfOn=TRUE
epsOn=TRUE
pngOn=TRUE
squareAxesOn=TRUE

fileName <- paste(rootName,".dat",sep="")
df <- read.table(fileName, header=TRUE, sep="\t", fill=TRUE);

xlabel="Rank"
ylabel="Citations"


#if (OSWindows) windows() else quartz()
cmax <- (trunc(max(df$Citations)/10)+1)*10
rmax <- length(df$Rank)
if (squareAxesOn) {
 vmax=max(cmax,rmax)
 cmax=vmax
 rmax=vmax
}

#barplot(df$Citations, beside=TRUE, xlim=c(0,rmax), ylim=c(0,cmax), names.arg=df$Rank,  xaxs = "i",  yaxs = "i", 
#          xlab=xlabel, ylab=ylabel )
#       lines(1:rmax,1:rmax,lty=1)
#       hvalue=11
#       lines(c(0,hvalue),c(hvalue,hvalue),lty=2)
#       lines(c(hvalue+0.5,hvalue+0.5),c(0,hvalue),lty=2)

# generic plot function
mainPlot <- function(){
 plot(df$Rank, df$Citations, log="xy", xlim=c(0,rmax), ylim=c(0,cmax),  xaxs = "i",  yaxs = "i", 
          xlab=xlabel, ylab=ylabel, cex=1.5, col=colourList[3] )
       lines(1:rmax,1:cmax,lty=1)
       text(0.9*rmax,0.9*cmax,"h", pos=1)
       lines(1:rmax/2,1:cmax,lty=3)
       h12label=expression(paste("h"["1:2"]))
       text(0.94*rmax/2,0.9*cmax,h12label, pos=1)
       lines(1:rmax,1:cmax/2,lty=3)
       h21label=expression(paste("h"["2:1"]))
       text(rmax*0.9,0.9*cmax/2,h21label, pos=1)
       hvalue=11
       lines(c(0,hvalue),c(hvalue,hvalue),lty=2, col=colourList[2])
       lines(c(hvalue,hvalue),c(0,hvalue),lty=2, col=colourList[2])
}       

if (screenOn){
       if (OSWindows) windows() else quartz()
       #print(paste(graphName,"on screen"), quote=FALSE)
       mainPlot()
       #abline(v=hvalue, lty=2)
}
# EPS plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (epsOn){
       epsFileName<- paste(rootName,"bar.eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=16, fonts=c("serif", "Palatino"))
       #postscript(epsFileName, fonts=c("serif", "Palatino"))
       mainPlot()
       dev.off(which = dev.cur())
}
# PDF plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (pdfOn){
       pdfFileName<- paste(rootName,"bar.pdf",sep="")
       print(paste("pdf plotting",pdfFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=16, fonts=c("serif", "Palatino"))
       mainPlot()
       dev.off(which = dev.cur())
}
# PNG plot
if (pngOn){
       pngFileName<- paste(rootName,"bar.png",sep="")
       print(paste("png plotting",pngFileName), quote=FALSE)
       png(pngFileName, height=480, width=480, pointsize=12)
       mainPlot()
       dev.off(which = dev.cur())
}

