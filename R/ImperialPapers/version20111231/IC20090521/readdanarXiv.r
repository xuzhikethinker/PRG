# reads in dan arxiv citation data file 
# Tab delimited file:
# ID No.Citations No.References DateAdded arXiv-category
# Converts it to a data frame of ratios of
# citation/reference in bins given by numberbins.
#Only papers with both positive citation and reference counts are included.
#Fit of lognormal also done.
#Returns a list with element df begin the data frame used for plotting and fitting the data
# df$citeRefRatio and df$count.
# The second element of list returned is named fit
# and is the result of the fit.
# Third is the total number of papers Papers, totalPapers
# Fourth is the normalisation factor used for the Lognormal fit, totalPaperFactor.

readDanArxiv <- function(rootName, numberbins=25, fromDate=as.Date("1970-01-01"), toDate=Sys.Date()) {
source("logbincount.r")

fullName=paste(rootName,".txt",sep="")
titleString<-paste(fullName, "bins",numberbins)
fileName=paste(rootName,numberbins,sep="_")
if (toDate<Sys.Date() || fromDate>as.Date("1970-01-01")) {
 titleString<-paste(titleString,fromDate,"-",toDate)
 fileName=paste(fileName,fromDate,toDate,sep="_")
}
print(titleString)

citations <- read.table(fullName, header=FALSE);

nzcrlist<-citations[[3]]/citations[[2]]
totalPapers <-length(nzcrlist)
# make list of ratios which are finite and greater than zero, 
# so exclude all with zero ref or cite count.
nzpcrlist<-nzcrlist[nzcrlist>0 & is.finite(nzcrlist)]
print(summary(nzpcrlist))
nzpcr5n<-fivenum(nzpcrlist)

nzpcrhist<-logbincount(nzpcrlist, numberbins) 
clist <-nzpcrhist$counts
totalPositivePapers<-sum(clist)
print(paste(totalPapers,", citations/references for cit,ref>0",totalPositivePapers,", fraction ",totalPositivePapers/totalPapers))
minCount<- 0
nzpcrdf <- data.frame(citeRefRatio = nzpcrhist$mids[clist>minCount], count = clist[clist>minCount]);

# don't forget that parameters of log normal are expressed in terms of log(x) 
sigmaMax=max(c(sigma=log(nzpcr5n[3])-log(nzpcr5n[2]),sigma=log(nzpcr5n[4])-log(nzpcr5n[3])))

#sqrt2pi <- sqrt(2*pi)
bigFactor=1e2
startlist <- list(mu=log(nzpcr5n[3]), sigma=sigmaMax, A=1)
lowerlist <- list(mu=log(nzpcr5n[2]), sigma=sigmaMax/bigFactor, A=1/bigFactor )
upperlist <- list(mu=log(nzpcr5n[4]), sigma=sigmaMax*bigFactor, A=bigFactor )
#print(lowerlist)
#print(startlist)
#print(upperlist)
#Don't forget that the expected number in a bin is the integral over a small region and this approximately removes the 1/x in the log normal.
#perhaps should fit cummulative function
binFactor <- nzpcrhist$breaks[2]/nzpcrhist$breaks[1]
totalPaperFactor <- totalPositivePapers*(binFactor-1)/sqrt(2*pi)
fitres <- nls( count ~ totalPaperFactor*A*exp(-(log(citeRefRatio)-mu)^2/(2*sigma*sigma))/sigma , nzpcrdf, startlist, lower=lowerlist, upper=upperlist, algorithm="port");
#fitres <- nls( count ~ exp(-(log(citeRefRatio)-mu)^2/(2*sigma*sigma))/( sqrt2pi *sigma) , nzpcrdf, startlist, lower=lowerlist, upper=upperlist);
print(summary(fitres))

# basic plot
windows()
plot(nzpcrdf, log="xy", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")

epsFileName<-paste(fileName,numberbins,"ll.eps",sep="");
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf, log="xy", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")
dev.off()

pdfFileName<-paste(fileName,numberbins,"ll.pdf",sep="");
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf, log="xy", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")
dev.off()

# semilog plot
windows()
plot(nzpcrdf, log="x", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")

epsFileName<-paste(fileName,"sl.eps",sep="");
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf, log="x", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")
dev.off()

pdfFileName<-paste(fileName,"sl.pdf",sep="");
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf, log="x", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")
dev.off()

lst <-list(df=nzpcrdf, fit=fitres, totalPapers=totalPapers, totalPositivePapers=totalPositivePapers, totalPaperFactor=totalPaperFactor)

}
