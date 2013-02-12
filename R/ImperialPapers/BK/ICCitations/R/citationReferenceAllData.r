citationReferenceAllData <- function(nzpcrlist, numberbins=25, numberParameters=3,totalRefs,totalCitations,totalNumberPapers,totalNZPCRPapers,meanValueUnnorm) {

nzpcr5n<-fivenum(nzpcrlist)
nzpcrhist<-logbincount(nzpcrlist, numberbins)

# get a vector of the numbers in each bin
clist <-nzpcrhist$counts
lll <- length(clist)
binWidth <-nzpcrhist$breaks[2:(lll+1)]-nzpcrhist$breaks[1:lll]

totalPapers<-sum(clist)

nzpcrdf <- data.frame(citeRefRatio = nzpcrhist$mids, count = clist, binWidth = 
binWidth);

sigmaMax=max(c(sigma=log(nzpcr5n[3])-log(nzpcr5n[2]),sigma=log(nzpcr5n[4])-log(nzpcr5n[3])))


bigFactor=2e2
startlist <- list(sigma=sigmaMax/2, dmu=log(nzpcr5n[3])/2, dA=0.01)
lowerlist <- list(sigma=sigmaMax/bigFactor, dmu=log(nzpcr5n[2])-sigmaMax*bigFactor*sigmaMax*bigFactor/2,     
dA=1/bigFactor-1 )
upperlist <- list(sigma=sigmaMax*bigFactor, dmu=log(nzpcr5n[4])-(sigmaMax/bigFactor)*(sigmaMax/bigFactor)/2, 
dA=bigFactor-1 )
if (numberParameters==1)
{startlist <- list(sigma=sigmaMax)
 lowerlist <- list(sigma=sigmaMax/bigFactor)
 upperlist <- list(sigma=sigmaMax*bigFactor)
}

binFactor <- nzpcrhist$breaks[2]/nzpcrhist$breaks[1]
midToLowerBinFactor <- 2/(1+binFactor)
midToUpperBinFactor <- 2*binFactor/(1+binFactor)

if (numberParameters==1) {
 fitres <- nls( count ~ totalPapers*(plnorm(citeRefRatio*midToUpperBinFactor, -sigma*sigma/2.0, 
 sigma)-plnorm(citeRefRatio*midToLowerBinFactor, -sigma*sigma/2.0, sigma) ) , nzpcrdf, startlist, lower=lowerlist, 
 upper=upperlist,algorithm="port");
}
else {
 fitres <- nls( count ~ totalPapers*(1+dA)*(plnorm(citeRefRatio*midToUpperBinFactor, dmu-sigma*sigma/2, 
 sigma)-plnorm(citeRefRatio*midToLowerBinFactor, dmu-sigma*sigma/2, sigma) ) , nzpcrdf, startlist, lower=lowerlist, 
 upper=upperlist,algorithm="port")
}


fitressum <- summary(fitres)
print(fitressum)

chisq = 0
x = nzpcrdf$citeRefRatio
sigma <- coef(fitressum)[[1]]
if(numberParameters==1){
        exp <- totalPapers*(plnorm(x*midToUpperBinFactor, -sigma*sigma/2.0, sigma)-plnorm(x*midToLowerBinFactor, 
        -sigma*sigma/2.0, sigma) )
}
else{
        dA <- coef(fitressum)[[2]]
        dmu <- coef(fitressum)[[3]]
        exp <- totalPapers*(1+dA)*(plnorm(x*midToUpperBinFactor, dmu-sigma*sigma/2, sigma)-plnorm(x*midToLowerBinFactor, 
        dmu-sigma*sigma/2, sigma))
}


obs <- nzpcrdf$count
chisqcomp <- (exp - obs) * (exp - obs) / exp
chisqvalue <- sum ( chisqcomp)

################################################################################

probList <- c(0.683, 0.954, 0.9973)
chitest = qchisq(probList,length(x) - 1 - numberParameters)

infoList=list(totalNumberPapers=totalNumberPapers, totalNZPCRPapers=totalPapers, totalNumberCitations=totalCitations, totalNumberReferences=totalRefs)

lst <-list(df=nzpcrdf, fit=fitres, totalPapers=totalPapers,
            xMin=nzpcr5n[1], xMax=nzpcr5n[5],        
            yMin=min(clist), yMax=max(clist), 
           rhoMin=min(clist/binWidth), rhoMax=max(clist/binWidth), 
            infoList=infoList,noOfPapers=totalPapers,meanValueUnnorm=NA,
           chisq=chisqvalue,chitest=chitest,stdErrpDof=fitressum$sigma/fitressum$df[[2]],
           binCount=length(x),rVal=0,values=x )
}


#lst <-list(df=nzpcrdf, fit=fitres, totalPapers=totalPapers,  
 #          xMin=nzpcr5n[1], xMax=nzpcr5n[5], 
  #         yMin=min(clist[clist>minCount]), yMax=max(clist), 
   #        rhoMin=min(clist[clist>minCount]/binWidth[clist>minCount]), rhoMax=max(clist/binWidth), 
    #       infoList=infoList,noOfPapers=totalNumberPapers, meanValueUnnorm= meanValueUnnorm, 
     #      chisq=chisqvalue,chitest=chitest,stdErrpDof=fitressum$sigma/fitressum$df[[2]],
      #     binCount=length(x),rVal=rVal,crdata=x )


