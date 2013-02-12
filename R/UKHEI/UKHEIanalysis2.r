# ***********************************************************************
# User alterable values
testOn=FALSE
#testOn=TRUE

debugOn=FALSE

rootName="UKHEIii"
projTypeList=c("kpinv","kp2inv")
projTypeNumber=1

gammaNameList=c("1000","1500","2000","3000","4000","5000")
vpNumber=2


numberRuns=1000

screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE
# FALSE if want  MacOS screen output
OSWindows=TRUE





#dataSubdirectory="data/"
inputSubdirectory="input/"
outputSubdirectory="output/"



#***********************************************************************
# Start of main code
#

source("weightedAdjacencyRank.r")
colourlist=c("black", "red", "blue", "green", "magenta", "brown");


print(paste("Making",numberRuns,"of null model"), quote=FALSE)


projType=projTypeList[projTypeNumber]
amextName="outputAdjMat.dat"

amFileName=paste(inputSubdirectory,rootName,projType,amextName,sep="")
print(paste("Reading Adjacency Matrix data from file",amFileName))

# Adjacency Matrix of projected graph
am <- read.table(amFileName)
adjMat <- as.matrix(am)
adjMatNSL <- adjMat -diag(adjMat) # this is adjacency ,matrix with zeros on diagonal

numberInst <- length(am)
instVector <- 1:numberInst
str <- c(instVector)
strint <- c(instVector)
for (iii in instVector) {
str[iii] <- sum(am[[iii]])
strint[iii] <- am[[iii]][iii]
}
strext <- str-strint 

gammaName=gammaNameList[vpNumber]
fullRootName=paste(rootName,projType,"nslLouvainQS",gammaName,sep="")
gammaValue=as.double(gammaName)/1000.0
inputTypeName =paste(projType,"nsl")
communityName=paste("Community LVP",gammaName)
valueName="Distance"
vpextName <- "r0vertices.dat"
vpFileName <- paste(inputSubdirectory,fullRootName,vpextName,sep="")
print(paste("Reading Vertex Partition data from file",vpFileName))
# names(vp) should have the following:-
# "index"     "Community" "degree"    "name"      "number"    "strength" 
# "k_C"       "k_C.k"     "str_C"     "str_C.str"

# Adjacency Matrix of projected graph
vp <- read.table(vpFileName, header=TRUE)

# Communities are numbered from zero
numberComm <- max(vp$Community)+1
print(paste("Number Communities is",numberComm))


# Locations 
rootName2="UKHEI143"
locFileName <- paste(inputSubdirectory,rootName2,"nameLatLong.dat",sep="")
print(paste("Reading Location data from file",locFileName))
loc <- read.table(locFileName, header=TRUE)

# Distances 
distFileName <- paste(inputSubdirectory,rootName2,"distances.dat",sep="")
print(paste("Reading distance data from file",distFileName))
distTable <- read.table(distFileName, header=TRUE, row.names=1)
distMatrix <- as.matrix(distTable)

# Institute to Community projection matrix
instToComm <- matrix(0,numberComm,numberInst)
for (iii in instVector) {
 c <- vp$Community[iii]+1 # communitites numbered from zero
 instToComm[c,iii] = 1
}



cNames=list()
for (ccc in 1:numberComm) {
cNames[[ccc]] <- names(am)[vp$Community==(ccc-1)]
}

numberInstInComm <- rowSums(instToComm) # use diag(numberInstInComm) for matrix form
numberDirEdges <- numberInstInComm %o% numberInstInComm
numberDirNSLEdges <- numberDirEdges - diag(numberInstInComm) # exclude self-edges
cAm <- instToComm %*% adjMat   %*% t(instToComm)
distAm <- distMatrix * adjMat 

cStrTotal <- instToComm %*% vp$strength  
cStrInternal <- instToComm %*% vp$str_C  
cDistTotal <- instToComm %*% distMatrix  %*% t(instToComm)
cDistAv <- cDistTotal/numberDirNSLEdges 
cDistAv[is.na(cDistAv)] <- 0 # replaces NaN values by zero.  

cDistAm <- instToComm %*% distAm  %*% t(instToComm)
cWDistAv <- cDistAm/cAm 

# 
cWDistAvNSLdf <- weightedAdjacencyRank(adjMat, distMatrix, instToComm)


#distanceAverageRandom=matrix(0,numberRuns,numberComm)
count <- rep(0,  times=numberComm)
#instIndexVector <- 1:iMax
for (rrr in 1:numberRuns){
 # this first bit makes a permutation of the matrix
 pInst <- sample(instVector)
 dm <- distMatrix[pInst,instVector]
 for (iii1 in instVector){
      iii0 <- instVector[pInst==iii1]
      dm [iii0,iii1] <- distMatrix[pInst[iii1],iii1]
      dm [iii1,iii1] <- 0
 }
 df <- weightedAdjacencyRank(adjMat, dm, instToComm)
 if (rrr==1) avRank <- df$rank   else avRank <- rbind(avRank, df$rank)
 count <- count + as.integer(df$rank > cWDistAvNSLdf$rank)
}
# rankProbability[ccc] is the probability that community ccc has a rank greater than
# that found when locations of institutes are permuted
rankProbability <- 1.0-(count/numberRuns)



# Function to gather stats
statsDist <- function(values){
  m<-mean(values)
  q<-quantile(values,c(0.025,0.975))
  v<-var(values)
  #list(mean=m,var=v, valueMin=q[[1]], valueMax=q[[2]])
  c(m,v, q[[1]],q[[2]],min(values),max(values))
}


randomRankMean <- rep(-1,  times=numberComm)
randomRanksd <- rep(-1,  times=numberComm)
#distanceAverageRank  <- seq(0,length=numberComm)
#distanceAverageStats <-NULL 
for (ccc in 1:numberComm) {
 randomRanksd[ccc] <- sd(avRank[,ccc])
 randomRankMean[ccc] <- mean(avRank[,ccc])
# distanceAverageRank[ccc] <- length(distanceAverageRandom[,ccc][distanceAverageRandom[,ccc]>distanceAverage[ccc]])
# distanceAverageStats<-rbind(distanceAverageStats,rbind(statsDist(distanceAverageRandom[,ccc])))
}



randomRankz <- (cWDistAvNSLdf$rank-randomRankMean)/randomRanksd
resultsdf <- data.frame( rank=cWDistAvNSLdf$rank, rankProbability = rankProbability, randomRankmean=randomRankMean, randomRanksd=randomRanksd, randomRankz=randomRankz, randomRankpnorm=pnorm(randomRankz) , cStrExternal=cStrTotal-cStrInternal, cfStrInternal=cStrInternal/cStrTotal)

fullOutputFileNameRoot <- paste(outputSubdirectory,fullRootName,"r",numberRuns,sep="")

resultsOutputFileName <- paste(fullOutputFileNameRoot,"Routput.dat",sep="")
write.table(resultsdf, file = resultsOutputFileName , append = FALSE, quote = FALSE, sep = "\t", row.names = TRUE, col.names = TRUE)

# *********************************************************************************************
# *** PLOTS
#
print("**** STARTING PLOTS",quote=FALSE)


distanceProbPlot <- function(probvector,subTitle,communityName,nameList){
    mainTitle = paste("Probability",valueName,"is larger in Null Model",sep=" ")
    xLabel=paste("Group by",communityName)
    nEntries=length(probvector)
    xShift=0
    plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,nEntries+xShift), xlab=xLabel, ylab="Probability", xaxt="n")
    axis(side=1, at=1:nEntries, labels=nameList, las=2) 
    points(probvector,  col=colourlist[1], pch=1)
}



mainTitle = paste("Probability",valueName,"is larger in Null Model",sep=" ")
subTitle=paste(rootName,inputTypeName,communityName,valueName)
nameList=1:numberComm
if (screenOn) {
      if (OSWindows) windows() else quartz()
      distanceProbPlot(rankProbability,subTitle,communityName,nameList)       
     }


# EPS plot
if (epsOn){
       epsFileName<- paste(fullOutputFileNameRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
       epsFileName<- paste(fullOutputFileNameRoot,"Prob.eps",sep="")
       distanceProbPlot(rankProbability,subTitle,communityName,nameList)  
       dev.off(which = dev.cur())
}       

# PDF plot
if (pdfOn){
       pdfFileName<- paste(fullOutputFileNameRoot,"Prob.pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
       distanceProbPlot(rankProbability,subTitle,communityName,nameList)  
       dev.off(which = dev.cur())
}


