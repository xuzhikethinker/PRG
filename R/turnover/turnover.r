# analyse turnover data

inputDir="GiomettoData/"
rootName="WrightFisherALLData"

dataFileName<-paste(inputDir,rootName,".dat",sep="");
print(paste("--- Turnover data file:",dataFileName), quote=FALSE)

df <- read.table(dataFileName, header=TRUE, sep="\t", fill=TRUE);


mumin=min(df$mu)
#lnmu=round(log(df$mu/mumin)*3)
lnmu=round(log(df$mu/mumin)*9)
lnmuvaluelist<-sort(unique(lnmu))

Nvaluelist<-sort(unique(df$N))
Nmin=min(df$N)
lnN=round(log(df$N/Nmin)*10)
lnNvaluelist<-sort(unique(lnN))

#yvaluedf<-data.frame(valuelist=yvaluelist<-sort(unique(df$y)), max=max(df$y), min=min(df$y))
yvaluelist=yvaluelist<-sort(unique(df$y))
ymax=max(df$y)
ymin=min(df$y)

# gives nearest index in list as a fraction.
# If list is ordered then this is rank as a fraction 
# takes list of values and finds index of value in list with value closest to given value
# It then converts this into a fraction lying between minfraction and maxfraction
fractionIndex <- function( value, valuelist, minfraction=0, maxfraction=1.0) { 
 index<-which.min(abs(valuelist-value))
 rankfraction =minfraction+((index-1)/(length(valuelist)-1))*(maxfraction-minfraction)
}

fractionIndexVector <- function( valueVector, valuelist, minfraction=0, maxfraction=1.0) { 
 rankFractionVector<-rep(0,length=length(valueVector))
 for (iii in 1:length(valueVector)) {rankFractionVector[iii] <- fractionIndex( valueVector[iii], valuelist, minfraction, maxfraction)}
 rankFractionVector
}

# see http://research.stowers-institute.org/efg/R/Color/Chart/
#colors()[grep("red",colors())]
# this gives rgb string value as a col=colValue in lines or points command for plot etc.
fmin=0.1
fmax=0.9
NfractionIndex<-fractionIndexVector(df$N,Nvaluelist,fmin,fmax)
#colValue<-rgb(fractionIndex(yvalue,yvaluelist,fmin,fmax), fractionIndex(Nvalue,Nvaluelist,fmin,fmax), fractionIndex(lnmuvalue,lnmuvaluelist,fmin,fmax) )
colValueVector<-rgb(fractionIndexVector(df$y,yvaluelist,fmin,fmax), NfractionIndex, fractionIndexVector(log(df$mu),lnmuvaluelist,fmin,fmax) )


plot(df$N*df$mu/df$y,df$z/(2*df$N*df$mu),log="xy", main="Wright-Fisher All Data", xlab=expression(N*mu/y), ylab=expression(z/(2*N * mu)), col=colValueVector)

# TYo collect dritical points look for 0.95>(z/2 N mu)>0.75 

