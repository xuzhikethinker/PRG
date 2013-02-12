# TESTS fit to lognormal

source("logbincount.r")

# generate a list of values from a lognormal distribution
numberValues <- 20000
sigmaActual <- 1.14
muActual <- -sigmaActual*sigmaActual/2
nzpcrlist<-rlnorm(numberValues,muActual,sigmaActual)

# Number of bins
numberbins =50

minCount=0

# ************************************************************
#From now on its a direct copy of citationReferenceIndex?.r

totalPapers<-length(nzpcrlist)
print(paste("--- Papers with cit,ref>0",totalPapers), quote=FALSE)
sss<-summary(nzpcrlist)
print(sss)
meanValue<-sss[[4]]
medianValue<-sss[[3]]
muGuess<-log(medianValue)
sigmaGuess<- sqrt(2*log(meanValue/medianValue))
print(paste("mu, sigma estimates",muGuess, sigmaGuess), quote=FALSE)
nzpcr5n<-fivenum(nzpcrlist)
# make a logbin histogram of the c/r data
nzpcrhist<-logbincount(nzpcrlist, numberbins)
# get a vector of the numbers in each bin
clist <-nzpcrhist$counts

totalPapers2<-sum(clist)

print(paste("---",totalPapers2," papers with cit,ref>0"), quote=FALSE)

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

# binFactor should be constant, that is the top (say x2) and bottom (say x1) values of all bins should be a constant ratio.
# We use the first two bins to find this ratio (binFactor=r=x2/x1) which is set automatically in the logbincount.r function
#  From this we then find the factor needed to convert the mid values stored in the data frame nzpcrdf$citeRefRatio 
#  to the upper and lower values.  Since mid=(x2+x1)/2 = x1(1+r)/2 we have that x1=mid*2/(1+r) and x2=mid*r*2/(1+r). 
binFactor <- nzpcrhist$breaks[2]/nzpcrhist$breaks[1]
midToLowerBinFactor <- 2/(1+binFactor)
midToUpperBinFactor <- 2*binFactor/(1+binFactor)

# Now use the difference of the cummulative disttibution (prob(x<=X)) to find expected number in bin given by mid values.
fitres <- nls( count ~ totalPapers*A*(plnorm(citeRefRatio*midToUpperBinFactor, mu, sigma)-plnorm(citeRefRatio*midToLowerBinFactor, mu, sigma) ) , nzpcrdf, startlist, lower=lowerlist, upper=upperlist, algorithm="port");

# Old fitting approximated the integration over a bin
#totalPaperFactor <- totalPapers*(binFactor-1)/sqrt(2*pi)
#fitres <- nls( count ~ totalPaperFactor*A*exp(-(log(citeRefRatio)-mu)^2/(2*sigma*sigma))/sigma , nzpcrdf, startlist, lower=lowerlist, upper=upperlist, algorithm="port");
#fitres <- nls( count ~ exp(-(log(citeRefRatio)-mu)^2/(2*sigma*sigma))/( sqrt2pi *sigma) , nzpcrdf, startlist, lower=lowerlist, upper=upperlist);

print(summary(fitres))

# ************************************************************
# Next bit similar to that used in citationsnz?.r

#infoList=list(totalNumberPapers=totalNumberPapers, totalNZPCRPapers=totalPapers, numberZeroCitations=numberZeroCitations,  numberNegativeCitations=numberNegativeCitations,  numberNegativeReferences=numberNegativeReferences, numberZeroReferences=numberZeroReferences, totalNumberCitations=totalNumberCitations, totalNumberReferences=totalNumberReferences)

titleString <- "Test lognormal"
subString <- paste(numberValues,"data points,",numberbins," bins")
windows()
plot(nzpcrdf$citeRefRatio,  nzpcrdf$count/totalPapers, type="p", log="xy",  main=titleString, sub=subString, xlab="x", ylab="y/Total", col="black")
lines(nzpcrdf$citeRefRatio, fitted(fitres)/totalPapers, col="red")

# PDF plot
pdfFileName<-paste("testLogNormal_p",numberValues,"b",numberbins,sep="")
print(paste("***** pdf plotting",pdfFileName), quote=FALSE)
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plot(nzpcrdf$citeRefRatio,  nzpcrdf$count/totalPapers, type="p", log="xy",  main=titleString, sub=subString, xlab="x", ylab="y/Total", col="black")
lines(nzpcrdf$citeRefRatio, fitted(fitres)/totalPapers, col="red")
