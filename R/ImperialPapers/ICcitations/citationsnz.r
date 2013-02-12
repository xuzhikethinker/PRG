#This analyses the citation/reference ratio of papers
#and fits it to log normal 
#MAYBE better to use CDF (CUMMULATIVE distribution), ecdf pLogNormal etc to do better fit?

#format is "article guid" tab "number of references in paper" tab "number of citations";
#rootName="citationstest.txt"
#rootName="ICNScitations"
#numberbins=20

#rootName="ICcitations"
#numberbins=50

#rootName="danastro-phedit"
#numberbins=20

plotcr <- function(rootName,numberbins){
source("logbincount.r")

fullName=paste(rootName,".txt",sep="")
titleString<-paste(fullName,", bins ",numberbins, sep="")
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

epsFileName<-paste(rootName,numberbins,"ll.eps",sep="");
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf, log="xy", main=paste(fullName,sep=""), sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")
dev.off()

pdfFileName<-paste(rootName,numberbins,"ll.pdf",sep="");
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf, log="xy", main=paste(fullName,sep=""), sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")
dev.off()

# semilog plot
windows()
plot(nzpcrdf, log="x", main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")

epsFileName<-paste(rootName,numberbins,"sl.eps",sep="");
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf, log="x", main=paste(fullName,sep=""), sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")
dev.off()

pdfFileName<-paste(rootName,numberbins,"sl.pdf",sep="");
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf, log="x", main=paste(fullName,sep=""), sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count")
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")
dev.off()

lst <-list(df=nzpcrdf, fit=fitres, totalPapers=totalPapers, totalPositivePapers=totalPositivePapers)

}
