# Summer 2012 version
# Takes list of citation data file and converts it to a data frame of ratios of
# citation/reference in bins given by numberbins.
# refList and citeList are vectors of references and citations
# These can be created using readRCYfile.r
# Only papers with both positive reference counts are included in the fits.
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

citationReferenceIndex12 <- function(refList, citeList, numberbins=25, minCount=0, 
                                    normalise=TRUE, cNotCoverR=FALSE, numberParameters=3, minCoverMeanC=0) {

totalNumberPapers <- length(citeList)
print(paste("--- citationReferenceIndex12"), quote=FALSE)
print(paste("--- Papers in data ",totalNumberPapers,", minCount",minCount ), quote=FALSE)


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
 crlist<-citeList
} 
else crlist<-citeList/refList

# make list of ratios which are finite and greater than zero,
# so exclude all with zero ref or cite count.



			##############uncrlist<-crlist[crlist>0 & is.finite(crlist)]
uncrlist<-crlist[is.finite(crlist)]
totalPapers<-length(uncrlist)
#print(paste("--- Papers with cit/c0>", 0,"ref>0",totalPapers), quote=FALSE)
print("Unnormalised summary", quote=FALSE)
sss<-summary(uncrlist)
print(sss)
meanValueUnnorm<-sss[[4]]
meanValue<-meanValueUnnorm
print("meanValue")####################################################
print(meanValue)######################################################

if (normalise) {
# now normalise this
normValue<- meanValue
}else normValue <- 1
ncrlist <- uncrlist/ normValue

print("Normalising, new summary is", quote=FALSE)
sss<-summary(ncrlist)
print(sss)
meanValue<-sss[[4]]

totalNumberPapers <- length(ncrlist)
medianValue<-sss[[3]]
muGuess<-log(medianValue)
sigmaGuess<- sqrt(2*log(meanValue/medianValue))
print(paste("mu, sigma estimates",muGuess, sigmaGuess), quote=FALSE)
nzpcr5n<-fivenum(ncrlist)
			## make a logbin histogram of the c/r data#########################
			#uncrhist<-logbincount(ncrlist, numberbins)
uncrhist<-logbinintegercount(uncrlist, numberbins)

# get a vector of the numbers in each bin
clist <-uncrhist$counts
lll <- length(clist)
binLower<- uncrhist$breaks[1:lll]/ normValue
binUpper<- uncrhist$breaks[2:(lll+1)]/ normValue
binWidth <- binUpper - binLower
print("Bin count")
print(clist)
print("Unnormalised bin boundaries")
print(uncrhist$breaks)
print("Bin lower")
print(binLower)
print("Bin upper")
print(binUpper)
#print(paste("#########",lll,length(binWidth)))


totalPapers2<-sum(clist)

#print(paste("---",totalPapers2," papers with cit,ref>0 between",minYear, "-",maxYear), quote=FALSE)
print(paste("---",totalPapers2," papers with cit,ref>0"), quote=FALSE)

#now set up data frames for actual obervations and a more limited on for fitting
############ 
##crdf <- data.frame(citeRefRatio = uncrhist$mids[clist>minCount], count = clist[clist>minCount], binWidth = binWidth[clist>minCount]);

xfitmin=0.1 #change this to 0.005? #SHOULD THIS (BELOW) BE > OR >=?!
midsNormList <- uncrhist$mids[clist>minCount]/normValue
print(midsNormList[midsNormList>xfitmin])
crdf <- data.frame(citeRefRatio = midsNormList, count = clist[clist>minCount], binWidth = binWidth[clist>minCount], binLower = binLower[clist>minCount], binUpper = binUpper[clist>minCount]);
fitdf <-   data.frame(count = crdf$count[midsNormList>xfitmin] , binLower = crdf$binLower[midsNormList>xfitmin] , binUpper = crdf$binUpper[midsNormList>xfitmin] );
print(fitdf)

# to deal with zeros in lower quartiles use forEst
forEst=nzpcr5n
forEst[nzpcr5n==0]<-0.5

#binWidth <-binWidthTemp[clist>minCount]
# don't forget that parameters of log normal are expressed in terms of log(x)
sigmaMax=max(c(sigma=log(forEst[3])-log(forEst[2]),sigma=log(forEst[4])-log(forEst[3])))
# mu = dmu -sigma^2/2, A =1+dA
#sqrt2pi <- sqrt(2*pi)

bigFactor=2e2
startlist <- list(sigma=sigmaMax/2, dmu=log(forEst[3])/2, dA=0.01)############################################
lowerlist <- list(sigma=sigmaMax/bigFactor, dmu=log(forEst[2])-sigmaMax*bigFactor*sigmaMax*bigFactor/2,     dA=1/bigFactor-1 )
upperlist <- list(sigma=sigmaMax*bigFactor, dmu=log(forEst[4])-(sigmaMax/bigFactor)*(sigmaMax/bigFactor)/2, dA=bigFactor-1 )

print("nzpcr5n")
print(nzpcr5n)
################################################################################DODGY?
if (numberParameters==1){
	 if(normalise){
	 startlist <- list(sigma=sigmaMax)
	 lowerlist <- list(sigma=sigmaMax/bigFactor)
	 upperlist <- list(sigma=sigmaMax*bigFactor)
	 }
	 else{
	 startlist <- list(sigma=sigmaMax,mu=log(forEst[3])/2) 
	 lowerlist <- list(sigma=sigmaMax/bigFactor,mu=log(forEst[3])/bigFactor)
	 upperlist <- list(sigma=sigmaMax*bigFactor,mu=log(forEst[3])*bigFactor) 
	 }
}
print("startlist")
print(startlist)
print("lowerlist")
print(lowerlist)
print("upperlist")
print(upperlist)
 
# Binfactor should be constant, that is the top (say x2) and bottom (say x1) values of all bins should be a constant ratio.
# We use the first two bins to find this ratio (binFactor=r=x2/x1) which is set automatically in the logbincount.r function
#  From this we then find the factor needed to convert the mid values stored in the data frame crdf$citeRefRatio 
#  to the upper and lower values.  Since mid=(x2+x1)/2 = x1(1+r)/2 we have that x1=mid*2/(1+r) and x2=mid*r*2/(1+r). 
binFactor <- uncrhist$breaks[2]/uncrhist$breaks[1]
##midToLowerBinFactor <- 2/(1+binFactor)
##midToUpperBinFactor <- 2*binFactor/(1+binFactor)

# Now use the difference of the cummulative disttibution (prob(x<=X)) to find expected number in bin given by mid values.

if (numberParameters==1) {
	if(normalise){
     fitres <- nls( count ~ totalPapers*(plnorm(binUpper, -sigma*sigma/2.0, sigma)-plnorm(binLower, -sigma*sigma/2.0, sigma) ) , fitdf, startlist, lower=lowerlist, upper=upperlist,algorithm="port");
    }
    else{
     fitres <- nls( count ~ totalPapers*(plnorm(binUpper, mu, sigma)-plnorm(binLower, mu, sigma) ) , fitdf, startlist, lower=lowerlist, upper=upperlist,algorithm="port");      
    }
}else {
print(paste("binU", "binL"))
print(paste(binLower,"," ,binUpper))
 fitres <- nls( count ~ totalPapers*(1+dA)*(plnorm(binUpper, dmu-sigma*sigma/2, sigma)-plnorm(binLower, dmu-sigma*sigma/2, sigma) ) , fitdf, startlist, lower=lowerlist, upper=upperlist,algorithm="port")

 } ####################################where is the normtn option for the else option?!? i.e. when noparas= 3
fitressum <- summary(fitres)
print(fitressum)

################################################################################
#Calculate expected counts in each bin based on nls fitted parameters
#Calculate chi values, and overall chi square statistic
chisq = 0
x = crdf$citeRefRatio
sigma <- coef(fitressum)[[1]]
if(numberParameters==1){
    if(normalise){
        exp <- totalPapers*(plnorm(x*binUpper, -sigma*sigma/2.0, sigma)-plnorm(x*binLower, -sigma*sigma/2.0, sigma) ) 
        }#######################################################here^&below midToUpperBinFactor/midToLowerBinFactor was changed to binUpper/binLower
    else{
        mu = coef(fitressum)[[2]]
        exp <- totalPapers*(plnorm(x*binUpper, mu-sigma*sigma/2.0, sigma)-plnorm(x*binLower, mu-sigma*sigma/2.0, sigma) )     
    }
    
}
else{
        dA <- coef(fitressum)[[2]]
        dmu <- coef(fitressum)[[3]]
        exp <- totalPapers*(1+dA)*(plnorm(x*binUpper, dmu-sigma*sigma/2, sigma)-plnorm(x*binLower, dmu-sigma*sigma/2, sigma))
}

obs <- crdf$count
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

xMinValue =min(crdf$citeRefRatio)
xMaxValue = nzpcr5n[5]

lst <-list(df=crdf, fit=fitres, totalPapers=totalPapers,  
           xMin=xMinValue, xMax=xMaxValue, 
           yMin=min(clist[clist>minCount]), yMax=max(clist), 
           rhoMin=min(clist[clist>minCount]/binWidth[clist>minCount]), rhoMax=max(clist/binWidth), 
           infoList=infoList,noOfPapers=totalNumberPapers, meanValueUnnorm=meanValueUnnorm, 
           chisq=chisqvalue,chitest=chitest,stdErrpDof=fitressum$sigma/fitressum$df[[2]],
           binCount=length(x),rVal=rVal,crdata=ncrlist)

}
