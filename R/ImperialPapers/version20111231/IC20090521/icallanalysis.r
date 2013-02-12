# Reads in citation data file for full data set

source("logbincount.r")
source("citationReferenceIndex5.r")


minYear=1900
maxYear=2020
rootName = "IC20090521"

cNotCoverR=TRUE
normalise=TRUE



numberbins=50
valueName="(C/R)"
valueFileName="CR"
if (cNotCoverR) {
numberbins=10
valueName="C"
valueFileName="C"
}
normaliseString=""
normaliseFileString=""
if (normalise) {
normaliseString=paste("/<",valueName,">",sep="")
normaliseFileString="norm"
}

xLabel=paste(valueName,normaliseString,sep="")

fullName=paste(rootName,"grcy.dat",sep="")

yearRangeString = paste((minYear+1),(maxYear-1),sep="-")
if (maxYear>2009) yearRangeString = paste((minYear+1),"Today",sep="-")

print(paste("*************",fullName,valueName,minYear,"-",maxYear,"*************"), quote=FALSE)

citations <- read.table(fullName, header=TRUE);
totalNumberPapers <- length(citations[[1]])
print(paste("--- Papers in data ",totalNumberPapers), quote=FALSE)

yearFullList<-citations[[4]]
refList<-citations[[2]][yearFullList>minYear & yearFullList<maxYear] #$References
citeList<-citations[[3]][yearFullList>minYear & yearFullList<maxYear] #$Citations
yearList<-citations[[4]][yearFullList>minYear & yearFullList<maxYear] #$year


# Exclude cases
totalNumberPapersCRPositive <- length(citeList[refList>0 & citeList>0])
print(paste("--- Total number of papers with positive references and citations in given years",totalNumberPapersCRPositive), quote=FALSE)

totalNumberCitations <- sum(citeList[refList>0 & citeList>0])
totalNumberReferences <- sum(refList[refList>0  & citeList>0])
print(paste("--- Total number of references and citations",totalNumberReferences,totalNumberCitations), quote=FALSE)

numberZeroCitations <- length(citeList[citeList==0])
numberZeroReferences <- length(refList[refList==0])
numberZeroCitationsPositiveCitations <- length(refList[refList>0 & citeList==0])
numberNegativeCitations <- length(citeList[citeList<0])
numberNegativeReferences <- length(refList[refList<0])
numberSillyYears <- length(yearFullList[yearFullList<minYear])
print(paste("--- Total number of papers with zero then negative references",numberZeroReferences,numberNegativeReferences), quote=FALSE)
print(paste("--- Total number of papers with zero then negative  citations",numberZeroCitations, numberNegativeCitations), quote=FALSE)
print(paste("--- Total number of papers with years less than",minYear,"is",numberSillyYears), quote=FALSE)
print(paste("--- Total number of papers with zero citations and positive  citations",numberZeroCitationsPositiveCitations), quote=FALSE)

totalNumberCRPositive <- sum(citeList[refList>0 & citeList>0 ])

# ************** HISTOGRAMS

hminYear=1970
hmaxYear=2009
hxLabel="Year"
yearListNotInHist=length(yearList[yearList<hminYear])
print(paste("--- Total number of papers with c,r>0, ",minYear,"<year<",hminYear," is ",yearListNotInHist), quote=FALSE)
windows()
h1<-hist(yearList, breaks=hminYear:hmaxYear,xlab=hxLabel, main="", xlim=c(hminYear,hmaxYear))
h2<-hist(yearList[refList>0 & citeList>0], breaks=hminYear:hmaxYear,xlab=hxLabel, ylab="", main="", xlim=c(hminYear,hmaxYear))
plot(h1$mids,h1$counts,xlab=hxLabel, main="", ylab="", xlim=c(hminYear,hmaxYear), pch=1, col="red", cex=2.0)
points(h2$mids,h2$counts, pch=2, col="blue", cex=2.0)
legend (x="topleft",y=NULL, c("All","c,r>0"), col=c("red","blue"),lty=1:2,pch=1:2);

hepsFileName<-paste(rootName,"yearhist.eps",sep="_")
print(paste("eps plotting",hepsFileName), quote=FALSE)
postscript(hepsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=18)
#hist(yearList, breaks=hminYear:hmaxYear,xlab=hxLabel, main="", xlim=c(hminYear,hmaxYear))
hist(yearList[refList>0 & citeList>0], breaks=hminYear:hmaxYear,xlab=hxLabel, main="", xlim=c(hminYear,hmaxYear))
dev.off(which = dev.cur())

hepsFileName<-paste(rootName,"crposyearhist.eps",sep="_")
print(paste("eps plotting",hepsFileName), quote=FALSE)
postscript(hepsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=18)
plot(h1$mids,h1$counts,xlab=hxLabel, ylab="", main="", xlim=c(hminYear,hmaxYear), pch=1, col="red", cex=2.0)
points(h2$mids,h2$counts, pch=2, col="blue", cex=2.0)
legend (x="topleft",y=NULL, c("All","c,r>0"), col=c("red","blue"),lty=1:2,pch=1:2);


#nzcrlist<-citeList/refList

# make list of ratios which are finite and greater than zero,
# so exclude all with zero ref or cite count.
#nzpcrinitiallist<-nzcrlist[nzcrlist>0 & is.finite(nzcrlist)]


CavList<-numeric()
RavList<-numeric()
#CoverRavList<-numeric()

for (yyy in hminYear:hmaxYear){
  iii=yyy-hminYear+1
  CavList[iii] <- mean(citeList[yearList==yyy & refList>0 & citeList>0])
  RavList[iii] <- mean(refList[yearList==yyy & refList>0 & citeList>0])
#  CoverRavList[iii] <- mean(CoverRList[yearList==yyy])
}

windows()
plot(hminYear:hmaxYear, CavList, type="p", xlab=hxLabel, ylab="", main="",  pch=1, col="red", cex=2.0)
points(hminYear:hmaxYear, RavList,pch=2, col="blue", cex=2.0)
#points(hminYear:hmaxYear, CoverRavList,pch=3)
legend (x="bottomleft",y=NULL, c("<c>","<r>"), col=c("red","blue"),lty=1:2,pch=1:2);

# EPS plot
hepsFileName<-paste(rootName,"cravyear.eps",sep="_")
print(paste("eps plotting",hepsFileName), quote=FALSE)
postscript(hepsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=18)
plot(hminYear:hmaxYear, CavList, type="p", xlab=hxLabel, ylab="", main="", pch=1, col="red", cex=2.0)
points(hminYear:hmaxYear, RavList,pch=2, col="blue", cex=2.0)
#points(hminYear:hmaxYear, CoverRavList,pch=3)
#legend (x="bottomleft",y=NULL, c("<c>","<r>"), col=c("red","blue"),lty=1:2,pch=1:2);
dev.off(which = dev.cur())

fitOn=FALSE
if (fitOn){

minCount=0

CRList <- list()
CRList <-citationReferenceIndex5(rootName, numberbins, minCount, minYear, maxYear, normalise, cNotCoverR) 

windows()
plot(CRList$df$citeRefRatio,   CRList$df$count/CRList$totalPapers, type="p",log="xy", xlab=xLabel, ylab="Count/Total", pch=1, col="black")
lines(CRList$df$citeRefRatio, fitted(CRList$fit)/CRList$totalPapers,col="red", lty=1)

# PDF plot
pdfFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,"all","ll.pdf",sep="_")
print(paste("pdf plotting",pdfFileName), quote=FALSE)
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plot(CRList$df$citeRefRatio,   CRList$df$count/CRList$totalPapers, type="p",log="xy", xlab=xLabel, ylab="Count/Total", pch=1, col="black")
lines(CRList$df$citeRefRatio, fitted(CRList$fit)/CRList$totalPapers,col="red", lty=1)


# EPS plot
epsFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,"all","ll.eps",sep="_")
print(paste("eps plotting",epsFileName), quote=FALSE)
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plot(CRList$df$citeRefRatio,   CRList$df$count/CRList$totalPapers, type="p",log="xy", xlab=xLabel, ylab="Count/Total", pch=1, col="black")
lines(CRList$df$citeRefRatio, fitted(CRList$fit)/CRList$totalPapers,col="red", lty=1)
dev.off(which = dev.cur())

} # eo if fitOn

