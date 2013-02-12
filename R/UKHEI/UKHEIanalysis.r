source("weightedAdjacencyRank.r")
dataSubdirectory="data/"
rootName="UKHEIii"
projTypeList=c("kpinv","kp2inv")
projTypeNumber=2
projType=projTypeList[projTypeNumber]
amextName="outputAdjMat.dat"
amFileName=paste(dataSubdirectory,rootName,projType,amextName,sep="")
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

vpNumber=1
gammaNameList=c("1000","1500","2000","3000","4000","5000")
gammaName=gammaNameList[vpNumber]
gammaValue=as.double(gammaName)/1000.0
vpextName <- "r0vertices.dat"
vpFileName <- paste(dataSubdirectory,rootName,projType,"nslLouvainQS",gammaName,vpextName,sep="")
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
locFileName <- paste(dataSubdirectory,rootName2,"nameLatLong.dat",sep="")
print(paste("Reading Location data from file",locFileName))
loc <- read.table(locFileName, header=TRUE)

# Distances 
distFileName <- paste(dataSubdirectory,rootName2,"distances.dat",sep="")
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

count <- rep(0,  times=numberComm)
numberRuns=10000
for (iii in 1:numberRuns){
 # this first bit makes a permutation of the matrix
 pInst <- sample(1:numberInst)
 dm <- distMatrix[pInst,pInst]
 df <- weightedAdjacencyRank(adjMat, dm, instToComm)
 if (iii==1) avRank <- df$rank   else avRank <- rbind(avRank, df$rank)
 count <- count + as.integer(df$rank > cWDistAvNSLdf$rank)
}
# rankProbability[ccc] is the probability that community ccc has a rank greater than
# that found when locations of institutes are permuted
rankProbability <- 1.0-(count/numberRuns)

randomRankMean <- rep(-1,  times=numberComm)
randomRanksd <- rep(-1,  times=numberComm)
for (ccc in 1:numberComm) {
 randomRanksd[ccc] <- sd(avRank[,ccc])
 randomRankMean[ccc] <- mean(avRank[,ccc])
}
randomRankz <- (cWDistAvNSLdf$rank-randomRankMean)/randomRanksd
resultsdf <- data.frame( rank=cWDistAvNSLdf$rank, rankProbability = rankProbability, randomRankmean=randomRankMean, randomRanksd=randomRanksd, randomRankz=randomRankz, randomRankpnorm=pnorm(randomRankz) , cStrExternal=cStrTotal-cStrInternal, cfStrInternal=cStrInternal/cStrTotal)

resultsOutputFileName <- paste(dataSubdirectory,rootName,projType,"nslLouvainQS",gammaName,"_",numberRuns,"Routput.dat",sep="")
write.table(resultsdf, file = resultsOutputFileName , append = FALSE, quote = FALSE, sep = "\t", row.names = TRUE, col.names = TRUE)

#for (ccc in 1:numberComm) { }













## calculate distance average weighted by number of papers excluding internal ones
#distAmNSL <- distMatrix * adjMatNSL 
#cAmNSL <- instToComm %*% adjMatNSL   %*% t(instToComm)
#cDistAmNSL <- instToComm %*% distAmNSL  %*% t(instToComm)
#cWDistAvNSL <- cDistAmNSL/cAmNSL 
#cWDistAvNSL[is.na(cWDistAvNSL)] <- 0 # replaces NaN values by zero.  
#
#cWDistAvNSLrank <- rep(-1,  times=numberComm)
#for (ccc in 1:numberComm) {
# cWDistAvNSLrank[ccc] <- rank(cWDistAvNSL[ccc,])[ccc] 
#}


