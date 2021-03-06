# Takes list of citation data file and converts it to a data frame of ratios of
# citation/reference in bins given by numberbins.
# refList and citeList are vectors of references and citations
# These can be created using readRCYfile.r
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

citationReferenceIndex7bk <- function(refList, citeList, numberbins=25, minCount=0, 
                                    normalise=TRUE, cNotCoverR=FALSE, numberParameters=3, minCoverMeanC=0) {

totalNumberPapers <- length(citeList)
print(paste("--- Papers in data ",totalNumberPapers), quote=FALSE)


# Excluse cases
totalNumberCitations <- sum(citeList[refList>0 & citeList>0])
totalNumberReferences <- sum(refList[refList>0  & citeList>0])
rVal <- mean(refList[refList>0  & citeList>0])
print(paste("--- Total number of references and citations",totalNumberReferences,totalNumberCitations), quote=FALSE)

numberZeroCitations <- length(citeList[citeList==0])
numberZeroReferences <- length(refList[refList==0])
numberNegativeCitations <- length(citeList[citeList<0])
numberNegativeReferences <- length(refList[refList<0])
print(paste("--- Total number of papers with zero then negative references",numberZeroReferences,numberNegativeReferences), quote=FALSE)
print(paste("--- Total number of papers with zero then negative  citations",numberZeroCitations, numberNegativeCitations), quote=FALSE)

if (cNotCoverR) {
 nzcrlist<-citeList
} 
else nzcrlist<-citeList/refList

# make list of ratios which are finite and greater than zero,
# so exclude all with zero ref or cite count.



nzpcrinitiallist<-nzcrlist[nzcrlist>minCoverMeanC & is.finite(nzcrlist)]
totalPapers<-length(nzpcrinitiallist)
print(paste("--- Papers with cit/c0>", minCoverMeanC,"ref>0",totalPapers), quote=FALSE)

print("Unnormalised summary", quote=FALSE)
sss<-summary(nzpcrinitiallist)
print(sss)
meanValueUnnorm<-sss[[4]]
meanValue<-meanValueUnnorm

if (normalise) {
# now normalise this
nzpcrlist<-nzpcrinitiallist/meanValue
nzpcrlist<-nzpcrlist[nzpcrlist > minCoverMeanC   ] #%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
totalNumberPapers <- length(nzpcrlist)

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
lll <- length(clist)
binWidth <-nzpcrhist$breaks[2:(lll+1)]-nzpcrhist$breaks[1:lll]
#print(paste("#########",lll,length(binWidth)))


totalPapers2<-sum(clist)

#print(paste("---",totalPapers2," papers with cit,ref>0 between",minYear, "-",maxYear), quote=FALSE)
print(paste("---",totalPapers2," papers with cit,ref>0"), quote=FALSE)

nzpcrdf <- data.frame(citeRefRatio = nzpcrhist$mids[clist>0], count = clist[clist>0], binWidth = binWidth[clist>minCount]);
#binWidth <-binWidthTemp[clist>minCount]

# don't forget that parameters of log normal are expressed in terms of log(x)
sigmaMax=max(c(sigma=log(nzpcr5n[3])-log(nzpcr5n[2]),sigma=log(nzpcr5n[4])-log(nzpcr5n[3])))


# mu = dmu -sigma^2/2, A =1+dA
#sqrt2pi <- sqrt(2*pi)

bigFactor=2e2
startlist <- list(sigma=sigmaMax/2, dmu=log(nzpcr5n[3])/2, dA=0.01)
lowerlist <- list(sigma=sigmaMax/bigFactor, dmu=log(nzpcr5n[2])-sigmaMax*bigFactor*sigmaMax*bigFactor/2,     dA=1/bigFactor-1 )
upperlist <- list(sigma=sigmaMax*bigFactor, dmu=log(nzpcr5n[4])-(sigmaMax/bigFactor)*(sigmaMax/bigFactor)/2, dA=bigFactor-1 )
if (numberParameters==1) 
{startlist <- list(sigma=sigmaMax)
 lowerlist <- list(sigma=sigmaMax/bigFactor)
 upperlist <- list(sigma=sigmaMax*bigFactor)
}


# binFactor should be constant, that is the top (say x2) and bottom (say x1) values of all bins should be a constant ratio.
# We use the first two bins to find this ratio (binFactor=r=x2/x1) which is set automatically in the logbincount.r function
#  From this we then find the factor needed to convert the mid values stored in the data frame nzpcrdf$citeRefRatio 
#  to the upper and lower values.  Since mid=(x2+x1)/2 = x1(1+r)/2 we have that x1=mid*2/(1+r) and x2=mid*r*2/(1+r). 
binFactor <- nzpcrhist$breaks[2]/nzpcrhist$breaks[1]
midToLowerBinFactor <- 2/(1+binFactor)
midToUpperBinFactor <- 2*binFactor/(1+binFactor)

# Now use the difference of the cummulative disttibution (prob(x<=X)) to find expected number in bin given by mid values.
if (numberParameters==1) {
 fitres <- nls( count ~ totalPapers*(plnorm(citeRefRatio*midToUpperBinFactor, -sigma*sigma/2.0, sigma)-plnorm(citeRefRatio*midToLowerBinFactor, -sigma*sigma/2.0, sigma) ) , nzpcrdf, startlist, lower=lowerlist, upper=upperlist,algorithm="port");
}
else {
 fitres <- nls( count ~ totalPapers*(1+dA)*(plnorm(citeRefRatio*midToUpperBinFactor, dmu-sigma*sigma/2, sigma)-plnorm(citeRefRatio*midToLowerBinFactor, dmu-sigma*sigma/2, sigma) ) , nzpcrdf, startlist, lower=lowerlist, upper=upperlist,algorithm="port")
}


fitressum <- summary(fitres)
print(fitressum)

#print("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
#print(clist)
#windows()
#plot(x=seq(1,19),y=clist)

################################################################################
#Calculate expected counts in each bin based on nls fitted parameters
#Calculate chi values, and overall chi square statistic
chisq = 0
x = nzpcrdf$citeRefRatio
sigma <- coef(fitressum)[[1]]
if(numberParameters==1){    
        exp <- totalPapers*(plnorm(x*midToUpperBinFactor, -sigma*sigma/2.0, sigma)-plnorm(x*midToLowerBinFactor, -sigma*sigma/2.0, sigma) ) 
}
else{
        dA <- coef(fitressum)[[2]]
        dmu <- coef(fitressum)[[3]]
        exp <- totalPapers*(1+dA)*(plnorm(x*midToUpperBinFactor, dmu-sigma*sigma/2, sigma)-plnorm(x*midToLowerBinFactor, dmu-sigma*sigma/2, sigma))
}

#A = coef(fitressum)[[1]]
#c0 = coef(fitressum)[[2]]
#gam = coef(fitressum)[[3]]
#exp = totalPapers * A * gamma(x / c0) / gamma(x / c0 + gam)
#exp = totalPapers * (c0*x**gam)
#exp = totalPapers * totalPapers * (pln(citeRefRatio*midToUpperBinFactor,beta,mu,sigma)-pln(citeRefRatio*midToLowerBinFactor,beta,mu,sigma))
obs <- nzpcrdf$count
chisqcomp <- (exp - obs) * (exp - obs) / exp 
chisqvalue <- sum ( chisqcomp)

################################################################################

probList <- c(0.683, 0.954, 0.9973)
chitest = qchisq(probList,length(x) - 1 - numberParameters)
#print("obs")
#print(obs)
#print("exp")
#print(exp)
#print("chisqcomp")
#print(chisqcomp)
#print(paste("chisqvalue=",chisqvalue,"\n"),quote=F)
#print(paste("chitest (",probList,") = ",chitest),quote=F)
#print("----------------------------------------------------------------")
infoList=list(totalNumberPapers=totalNumberPapers, totalNZPCRPapers=totalPapers, numberZeroCitations=numberZeroCitations,  numberNegativeCitations=numberNegativeCitations,  numberNegativeReferences=numberNegativeReferences, numberZeroReferences=numberZeroReferences, totalNumberCitations=totalNumberCitations, totalNumberReferences=totalNumberReferences)

lst <-list(df=nzpcrdf, fit=fitres, totalPapers=totalPapers,  
           xMin=nzpcr5n[1], xMax=nzpcr5n[5], 
           yMin=min(clist[clist>minCount]), yMax=max(clist), 
           rhoMin=min(clist[clist>minCount]/binWidth[clist>minCount]), rhoMax=max(clist/binWidth), 
           infoList=infoList,noOfPapers=totalNumberPapers, meanValueUnnorm=meanValueUnnorm, 
           chisq=chisqvalue,chitest=chitest,stdErrpDof=fitressum$sigma/fitressum$df[[2]],
           binCount=length(x),rVal=rVal,crdata=nzpcrlist )

}
