# Need to install then load gplots library
#print("Need to issue library(gplots)", quote=FALSE)
colourList=c("black", "darkred", "darkblue", "darkgreen", "magenta", "brown");


#Root name for all files
#rootName="TSEWoSresearcherid"
# selects paper
#nrowList = c(1,2,4,6);

rootName="PendryWoSresearcherid"
nrowList = c(1,2,3,13);

lastYear=2011



plotsOn=FALSE
OSWindows=TRUE
screenOn=TRUE
pdfOn=TRUE
epsOn=TRUE
pngOn=TRUE



fileName <- paste(rootName,".dat",sep="")
df <- read.table(fileName, header=TRUE, sep="\t", fill=TRUE);
nameList <-names(df)
nCol <-length(nameList)
ccc<-1:nCol
nPapers = length(nrowList)
nPapersSeq =1:nPapers

# create empty lists and vectors
cummCiteData <- list()
shortTitleList <-list()
lengthList <- rep(0,times=nPapers)
maxCite <- rep(0,times=nPapers)

for (ppp in nPapersSeq ){
   nrow<-nrowList[ppp]
   print(paste("Paper",nrow,df$Title[nrow]),quote=FALSE)
   firstYear=df$Year[nrow]
   firstYearString=paste("C",firstYear,sep="")
   firstYearCol<-ccc[nameList==firstYearString]
   lastYearString=paste("C",lastYear,sep="")
   lastYearCol<-ccc[nameList==lastYearString]

   citeData<-df[nrow,firstYearCol:lastYearCol]
   cummCiteData[[ppp]] <- cumsum(t(citeData))
   lengthList[ppp]<-length(cummCiteData[[ppp]])
   maxCite[ppp]=max(cummCiteData[[ppp]])
   shortTitleList[[ppp]] <- paste(substr(df$Title[nrow],1,8),df$Year[nrow])
}

#colList=paste("C",1970:2011,sep="")
#nyears=length(colList)
#df$colList

xlabel="Years after Publication"
ylabel="Citations"

xMin<-1
xMax<-max(lengthList);
yMin<-0
yMax<-max(maxCite);

#if (OSWindows) windows() else quartz()
cmax <- (trunc(max(df$Citations)/10)+1)*10
rmax <- length(df$Rank)
#barplot(df$Citations, beside=TRUE, xlim=c(0,rmax), ylim=c(0,cmax), names.arg=df$Rank,  xaxs = "i",  yaxs = "i", 
#          xlab=xlabel, ylab=ylabel )
#       lines(1:rmax,1:rmax,lty=1)
#       hvalue=11
#       lines(c(0,hvalue),c(hvalue,hvalue),lty=2)
#       lines(c(hvalue+0.5,hvalue+0.5),c(0,hvalue),lty=2)

# Generic Plot Routine
cummPlotFunction <- function(mainTitleString,legendOn=TRUE){
   plot(x=NULL, y=NULL, xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=mainTitleString, 
             xlab=xlabel, ylab=ylabel)
   for (ppp in nPapersSeq ){
        points(1:length(cummCiteData[[ppp]]), cummCiteData[[ppp]], 
             xlab=xlabel, ylab=ylabel, pch=ppp, col=colourList[ppp] )
        }     
        if (legendOn) legend (x="topleft",y=NULL, shortTitleList[nPapersSeq], col=colourList[nPapersSeq],lty=nPapersSeq,pch=nPapersSeq);
}

titleString="Cummulative Citations"
if (screenOn){
       if (OSWindows) windows() else quartz()
       #print(paste(graphName,"on screen"), quote=FALSE)
  cummPlotFunction(titleString,TRUE)
} # end of if screenON

# EPS plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (epsOn){
       epsFileName<- paste(rootName,"CummulativeCite.eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=16, fonts=c("serif", "Palatino"))
       cummPlotFunction(titleString,TRUE)
       dev.off(which = dev.cur())
} # end of if epsOn

# PDF plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (pdfOn){
       pdfFileName<- paste(rootName,"CummulativeCite.pdf",sep="")
       print(paste("pdf plotting",pdfFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=16, fonts=c("serif", "Palatino"))
       cummPlotFunction(titleString,TRUE)
       dev.off(which = dev.cur())
} # end of if pdfOn

# PNG plot
if (pngOn){
       pngFileName<- paste(rootName,"CummulativeCite.png",sep="")
       print(paste("png plotting",pngFileName), quote=FALSE)
       png(pngFileName, height=480, width=480, pointsize=12)
       cummPlotFunction("",FALSE)
       dev.off(which = dev.cur())
}# end of if pngOn



