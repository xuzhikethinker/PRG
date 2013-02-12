#This analyses the citation/reference ratio of papers
#and fits it to log normal 
# unfinished attempt to use CDF AND CUMMULATIVE TO DO BETTER FIT?
#

#format is "article guid" tab "number of references in paper" tab "number of citations";
#rootName="citationstest.txt"
rootName="ICNScitations"

source("logbincount.r")

fullName=paste(rootName,".txt",sep="")
citations <- read.table(fullName, header=FALSE);

nzcrlist<-nzcitations[[3]]/nzcitations[[2]]
# make list of ratios which are finite and greater than zero, 
# so exclude all with zero ref or cite count.
print("citations/references for cit,ref>0")
nzpcrlist<-nzcrlist[nzcrlist>0 & is.finite(nzcrlist)]
print(summary(nzpcrlist))
nzpcr5n<-fivenum(nzpcrlist)
numberbins=25
nzpcrhist<-logbincount(nzpcrlist, numberbins) 
clist <-nzpcrhist$counts
totalPapers<-sum(nzpcrhist$counts)

nzpcrecdf <- ecdf(nzpcrhist$counts)

crvalues<-nzpcrhist$breaks[2:length(nzpcrhist$breaks)]
crcounts<-nzpcrecdf(crvalues)
nzpcrcdfdf <- data.frame(citeRefRatio = crvalues, count = crcounts);

# don't forget that parameters of log normal are expressed in terms of log(x) 
sigmaMax=max(c(sigma=log(nzpcr5n[3])-log(nzpcr5n[2]),sigma=log(nzpcr5n[4])-log(nzpcr5n[3])))

#sqrt2pi <- sqrt(2*pi)
bigFactor=1e2
startlist <- list(mu=log(nzpcr5n[3]), sigma=sigmaMax, A=1)
lowerlist <- list(mu=log(nzpcr5n[2]), sigma=sigmaMax/bigFactor, A=1/bigFactor )
upperlist <- list(mu=log(nzpcr5n[4]), sigma=sigmaMax*bigFactor, A=bigFactor )
print(lowerlist)
print(startlist)
print(upperlist)
#Don't forget that the expected number in a bin is the integral over a small region and this approximately removes the 1/x in the log normal.
#perhaps should fit cummulative function

fitres <- nls( count ~ totalPapers*A*plnorm(citeRefRatio, meanlog = mu, sdlog = sigma, lower.tail = TRUE, log.p = FALSE) , nzpcrcdfdf, startlist, lower=lowerlist, upper=upperlist, algorithm="port");
#fitres <- nls( count ~ exp(-(log(citeRefRatio)-mu)^2/(2*sigma*sigma))/( sqrt2pi *sigma) , nzpcrdf, startlist, lower=lowerlist, upper=upperlist);
# basic plot
print(summary(fitres))
plot(nzpcrcdfdf, log="xy")
lines(nzpcrcdfdf$citeRefRatio,fitted(fitres),col="red")

windows()
plot(nzpcrdf)
lines(nzpcrdf$citeRefRatio,fitted(fitres),col="red")


