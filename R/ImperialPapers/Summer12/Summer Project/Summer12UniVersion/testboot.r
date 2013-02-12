#testboot.r
# need to do install.package("boot") once per computer
# the library comend is only actually needed at the start of every session
library(boot)


# this function finds mean of the values in the vector data.
#  The entries are weighted using the weighting vector entries.
# To get the estimated mean of the data use 
# a vector of same length as the data but full of 1's 
# i.e. seq(1, by=0, length=length(data) ).
# In boot strapping weight[i] = number of times the i-th entry is in
# the artificial data set. SO this should sum to length(data).
findMean<-function(data,weight) {
 sum(data*weight)/sum(weight)
}

# this function finds simulates our approach to finding the
# covariance sigma^2 of the x=log (citations/<c>) 
# Cheats as covariance is found by treating looking at the 
# data for x, and finding the covariance as normal, not by fitting
# x data to a normal.
findLogVar<-function(data,weight) {
 ddd=data
 ddd[data<0.5]=0.25 # sets any entries less than 0.5 t be 0.25, middle of first bin
 dmean<-mean(data)
 lll<-log(ddd/dmean)
 www<-weight
 cov.wt(as.matrix(lll),www)$cov
}

falsecitationdata<-function(number=10, meanCitation=10, sdlog = 1.3){
  vec<-round(rlnorm(number, meanlog = -sdlog^2/2, sdlog = sdlog)*meanCitation)  
}

# these are the paraneters for the artificial data
number=500
meanCitation=10
sigmaSq=1.3
sigma=sqrt(sigmaSq)
print(paste("Data represents",number,"papers"),quote=FALSE)
print(paste(" with mean citation of",meanCitation),quote=FALSE)
print(paste(" and log normal variance sigma^2=",sigmaSq),quote=FALSE)
data=falsecitationdata(number, meanCitation, sigma  )

# Simpler data example
#data=1:number

# weight vector if needed
weightVec=seq(1, by=0, length=length(data))

#result from the function applied direct to the data
#actualFunctionResult=findMean(data,weightVec )
actualFunctionResult=findLogVar(data,weightVec )

actualMean=mean(data)
actualSD=sd(data)
print(paste("Mean of data is",actualMean),quote=FALSE)
print(paste(" standard deviation sigma=",actualSD),quote=FALSE)
print(paste(" variance sigma^2=",var(data)),quote=FALSE)
#print(paste("findMean function gives mean of ",actualFunctionResult),quote=FALSE)
print(paste("findLogVar function gives mean of ",actualFunctionResult),quote=FALSE)



numberArtificialDataSets=1000
#bootResult <- boot(data, findMean, numberArtificialDataSets, stype = "w")
bootResult <- boot(data, findLogVar, numberArtificialDataSets, stype = "w")

# Now example of how to get a value out of a boot result. 
# These are listed inboot.pdf under the values section of the booot() command
print(paste(" findMean function mean using boot strap =",bootResult$t0),quote=FALSE)



# The quick way to get things out of bootResult
print(bootResult)

# the bias is the difference between the result when the function (here findMean) is applied
# to the data and when it is applied to the artificial data sets generated by boot
# bootResult$t are the results of the function applied to the artificial data sets so
# its length should equal the number of artificial data sets
bootFunctionResultMean=mean(bootResult$t) 
bootBias =bootFunctionResultMean-actualFunctionResult
bootFunctionResultSD=apply(bootResult$t,2,sd) #sd(bootResult$t) 
print(paste(" Mean of function results on artificial data is  =",bootFunctionResultMean),quote=FALSE)
print(paste(" Bias (difference estimated and actual function results on data) =",bootBias),quote=FALSE)
print(paste(" Estimated standard deviation in function results =",bootFunctionResultSD),quote=FALSE)
 
# other tricks
# print(summary(bootResult))
plot(bootResult)
