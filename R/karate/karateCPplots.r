screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE

# FALSE if want  MacOS screen output
OSWindows=TRUE

#***********************************************************************
# Start of main code
#
colourlist=c("black", "red", "blue", "green", "magenta", "brown");


rootNamet2<-"karateTSE_t2c3CG_5_10000_5_gammaNC"
fileNamet2 <- paste(rootNamet2,".dat",sep="")
typeStringt2="D(G)"
rootNamet4<-"karateTSE_t4c3CG_5_10000_5_gammaNC"
fileNamet4 <- paste(rootNamet4,".dat",sep="")
typeStringt4="C(G)"



print(paste("Reading gamma vs nc from file",fileNamet2), quote=FALSE)
gnct2 <- read.table(fileNamet2, header=TRUE, sep="\t", fill=TRUE);
print(paste("Reading gamma vs nc from file",fileNamet4), quote=FALSE)
gnct4 <- read.table(fileNamet4, header=TRUE, sep="\t", fill=TRUE);

# *********************************************************************************************
# *** PLOTS
#
plotDataTablePoints<-function(dataTable1,dataTable2, legendString1,legendString2){
    pchList=c(1,3);
    plot(dataTable1$gamma, dataTable1$Communities-2,  ylim=c(0,12), xlim=c(0,2.5), main=NULL, xlab=expression(gamma), ylab="Number Communities", type="p", col=colourlist[2], pch=pchList[1], cex=2, lwd=2)
    points(dataTable2$gamma,dataTable2$Communities-2, col=colourlist[3], pch=pchList[2], cex=2, lwd=2)
    legend(x="bottomright" ,y=NULL, c(legendString1,legendString2), pch=pchList, cex=2, col=colourlist[2:3]);
}
plotDataTable<-function(dataTable1,dataTable2, legendString1,legendString2){
    ltyList=c(2,3);
    plot(dataTable1$gamma, dataTable1$Communities-2,  ylim=c(0,12), xlim=c(0,2.5), main=NULL, xlab=expression(gamma), ylab="Number Communities", type="l", col=colourlist[2], lty=ltyList[1], lwd=3)
    lines(dataTable2$gamma,dataTable2$Communities-2, col=colourlist[3], lty=ltyList[2], lwd=3)
    legend(x="bottomright" ,y=NULL, c(legendString1,legendString2), lty=ltyList, lwd=3, col=colourlist[2:3]);
}


fileNameFullRoot=rootName

# Screen plot
if (screenOn){
       if (OSWindows) windows() else quartz()
       plotDataTable(gnct2,gnct4,typeStringt2,typeStringt4)
}       



# EPS plot
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
       plotDataTable(gnct2,gnct4,typeStringt2,typeStringt4)
       dev.off(which = dev.cur())
}       

# PDF plot
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
       plotDataTable(gnct2,gnct4,typeStringt2,typeStringt4)
       dev.off(which = dev.cur())
}
