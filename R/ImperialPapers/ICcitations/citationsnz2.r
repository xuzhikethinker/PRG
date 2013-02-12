#This analyses the citation/reference ratio of papers
#and fits it to log normal 
#MAYBE better to use CDF (CUMMULATIVE distribution), ecdf pLogNormal etc to do better fit?

#format is "article guid" tab "number of references in paper" tab "number of citations";

colourlist=c("black", "red", "blue", "green", "magenta", "brown");

source("logbincount.r")
source("citationReferenceIndex.r")

#rootName="citationstest.txt"
ICName="ICcitations"
ICnumberBins=50
ICList <- citationReferenceIndex(ICName,ICnumberBins)

ICNSName="ICNScitations"
ICNSnumberBins=25
ICNSList <- citationReferenceIndex(ICNSName,ICNSnumberBins)

danastroName="danastro-phedit"
danastronumberBins=25
danastroList <- citationReferenceIndex(danastroName,ICNSnumberBins)

nameList <- list(ICName,ICNSName,danastroName);


# basic plot
windows()
titleString<-paste(ICName,ICNSName,danastroName, sep=" ")
ifirst=1
iii=ifirst
plot(ICList$df$citeRefRatio, ICList$df$count/ICList$totalPapers, pch=iii, col=colourlist[iii], log="xy", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count/Total")
lines(ICList$df$citeRefRatio,fitted(ICList$fit)/ICList$totalPapers,col=colourlist[iii], lty=iii)

iii=iii+1
points(ICNSList$df$citeRefRatio, ICNSList$df$count/ICNSList$totalPapers, pch=iii, col=colourlist[iii])
lines(ICNSList$df$citeRefRatio,fitted(ICNSList$fit)/ICNSList$totalPapers,col=colourlist[iii], lty=iii)

iii=iii+1
points(danastroList$df$citeRefRatio, danastroList$df$count/danastroList$totalPapers, pch=iii, col=colourlist[iii])
lines(danastroList$df$citeRefRatio,fitted(danastroList$fit)/danastroList$totalPapers,col=colourlist[iii], lty=iii)

xxx <- 1;
yyy <- 1e-5;
ilast=iii
legend (x="topright",y=NULL, nameList[ifirst:ilast], col=colourlist[ifirst:ilast],lty=ifirst:ilast,pch=ifirst:ilast);

pdfFileName<-"All3_ll.pdf"

pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
iii=ifirst
plot(ICList$df$citeRefRatio, ICList$df$count/ICList$totalPapers, pch=iii, col=colourlist[iii], log="xy", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count/Total")
lines(ICList$df$citeRefRatio,fitted(ICList$fit)/ICList$totalPapers,col=colourlist[iii], lty=iii)

iii=iii+1
points(ICNSList$df$citeRefRatio, ICNSList$df$count/ICNSList$totalPapers, pch=iii, col=colourlist[iii])
lines(ICNSList$df$citeRefRatio,fitted(ICNSList$fit)/ICNSList$totalPapers,col=colourlist[iii], lty=iii)

iii=iii+1
points(danastroList$df$citeRefRatio, danastroList$df$count/danastroList$totalPapers, pch=iii, col=colourlist[iii])
lines(danastroList$df$citeRefRatio,fitted(danastroList$fit)/danastroList$totalPapers,col=colourlist[iii], lty=iii)

legend (x="topright",y=NULL, nameList[ifirst:ilast], col=colourlist[ifirst:ilast],lty=ifirst:ilast,pch=ifirst:ilast);

dev.off()


