# reads in citation data file and converts it to a data frame of ratios of
# citation/reference in bins given by numberbins.
#Only papers with both positive citation and reference counts are included.
#Fit of lognormal also done.
#Returns a list with element df begin the data frame used for plotting and fitting the data
# df$citeRefRatio and df$count.  
# The second element of list returned is named fit
#and is the result of the fit.
# Third is the total number of papers Papers, totalPapers
# Fourth is the normalisation factor used for the Lognormal fit, totalPaperFactor.
citationReferenceIndex <- function(rootName, numberbins=25) {

fullName=paste(rootName,".txt",sep="")
print(fullName)

citations <- read.table(fullName, header=FALSE);

nzcrlist<-citations[[3]]/citations[[2]]
# make list of ratios which are finite and greater than zero,
# so exclude all with zero ref or cite count.
print("citations/references for cit,ref>0")
nzpcrlist<-nzcrlist[nzcrlist>0 & is.finite(nzcrlist)]
print(summary(nzpcrlist))
nzpcr5n<-fivenum(nzpcrlist)
numberbins=50
nzpcrhist<-logbincount(nzpcrlist, numberbins)
clist <-nzpcrhist$counts
totalPapers<-sum(clist)
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
totalPaperFactor <- totalPapers*(binFactor-1)/sqrt(2*pi)
fitres <- nls( count ~ totalPaperFactor*A*exp(-(log(citeRefRatio)-mu)^2/(2*sigma*sigma))/sigma , nzpcrdf, startlist, lower=lowerlist, upper=upperlist, algorithm="port");
#fitres <- nls( count ~ exp(-(log(citeRefRatio)-mu)^2/(2*sigma*sigma))/( sqrt2pi *sigma) , nzpcrdf, startlist, lower=lowerlist, upper=upperlist);
print(summary(fitres))
lst <-list(df=nzpcrdf, fit=fitres, totalPapers=totalPapers, totalPaperFactor=totalPaperFactor)
}
