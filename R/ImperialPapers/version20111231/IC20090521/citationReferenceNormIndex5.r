# Reads in citation data file and converts it to a data frame of ratios of
# citation/reference in bins given by numberbins.
# Input file must start with argument rootName and end with grcy.dat 
# It has to have one line per paper with each line containing (in this grcy order):-
#  gid	References	Citations	year
# Only papers with both positive citation and reference counts are included in the fits.
# Fit of lognormal also done.  This is now done using correct form for 
# integration of distribution over a bin.
# Returns a list with the following elements:
# *** df 
# the data frame used for plotting and fitting the data
# so y and x are df$citeRefRatio and df$count.  
# *** fit
# The result of the fit.
# *** totalPapers 
# Total number of papers used in the fitted data, i.e. all papers with both cite and ref>0
# *** totalPaperFactor
#      =totalPapers*(binFactor-1)/sqrt(2*pi)
# *** xMin xMax yMin yMax
# *** infoList
# Various stats specifically:-
#      totalNumberPapers, 
#      totalPapers (c and r>0), 
#      numberZeroCitations, 
#      numberNegativeCitations, 
#      numberNegativeReferences, 
#      numberZeroReferences, 
#      totalNumberCitations, 
#      totalNumberReferences

citationReferenceIndex5 <- function(rootName, numberbins=25, minCount=0, minYear=1900, maxYear=2010, normalise=TRUE, cNotCoverR=FALSE) {

#fullName=paste(rootName,"grc.dat",sep="")
fullName=paste(rootName,"grcy.dat",sep="")
print(paste("*************",fullName,"*************"), quote=FALSE)

citations <- read.table(fullName, header=TRUE);
totalNumberPapers <- length(citations[[1]])
print(paste("--- Papers in data ",totalNumberPapers), quote=FALSE)

yearFullList<-citations[[4]]
refList<-citations[[2]][yearFullList>minYear & yearFullList<maxYear] #$References
citeList<-citations[[3]][yearFullList>minYear & yearFullList<maxYear] #$Citations
yearList<-citations[[4]][yearFullList>minYear & yearFullList<maxYear] #$year

# Excluse cases
totalNumberCitations <- sum(citeList[refList>0 & citeList>0])
totalNumberReferences <- sum(refList[refList>0  & citeList>0])
print(paste("--- Total number of references and citations",totalNumberReferences,totalNumberCitations), quote=FALSE)

numberZeroCitations <- length(citeList[citeList==0])
numberZeroReferences <- length(refList[refList==0])
numberNegativeCitations <- length(citeList[citeList<0])
numberNegativeReferences <- length(refList[refList<0])
print(paste("--- Total number of papers with zero then negative references",numberZeroReferences,numberNegativeReferences), quote=FALSE)
print(paste("--- Total number of papers with zero then negative  citations",numberZeroCitations, numberNegativeCitations), quote=FALSE)

nzcrlist<-citeList/refList

# make list of ratios which are finite and greater than zero,
# so exclude all with zero ref or cite count.
nzpcrinitiallist<-nzcrlist[nzcrlist>0 & is.finite(nzcrlist)]
totalPapers<-length(nzpcrinitiallist)
print(paste("--- Papers with cit,ref>0",totalPapers), quote=FALSE)

print("Unnormalised summary", quote=FALSE)
sss<-summary(nzpcrinitiallist)
print(sss)
meanValue<-sss[[4]]

if (normalise) {
# now normalise this
nzpcrlist<-nzpcrinitiallist/meanValue
print("Normalising, new summary is", quote=FALSE)
sss<-summary(nzpcrlist)
print(sss)
meanValue<-sss[[4]]
}
else nzpcrlist<-nzpcrinitiallist

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

print(paste("---",totalPapers2," papers with cit,ref>0 between",minYear, "-",maxYear), quote=FALSE)

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

infoList=list(totalNumberPapers=totalNumberPapers, totalNZPCRPapers=totalPapers, numberZeroCitations=numberZeroCitations,  numberNegativeCitations=numberNegativeCitations,  numberNegativeReferences=numberNegativeReferences, numberZeroReferences=numberZeroReferences, totalNumberCitations=totalNumberCitations, totalNumberReferences=totalNumberReferences)

lst <-list(df=nzpcrdf, fit=fitres, totalPapers=totalPapers,  xMin=nzpcr5n[1], xMax=nzpcr5n[5], yMin=min(clist[clist>0]), yMax=max(clist), infoList=infoList )

}
