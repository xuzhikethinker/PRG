# calculate distance average weighted by number of papers excluding internal ones

weightedAdjacencyRank <- function(adjMat, distMatrix, instToComm){

numberCommunities <- dim(instToComm)[1]
adjMatNSL <- adjMat -diag(adjMat) # this is adjacency ,matrix with zeros on diagonal

distAmNSL <- distMatrix * adjMatNSL 
cAmNSL <- instToComm %*% adjMatNSL   %*% t(instToComm)
cDistAmNSL <- instToComm %*% distAmNSL  %*% t(instToComm)

# The following is a weighted average distance matrix for the communities.
#  Weights are the number of papers and internal papers (diagonal terms) are ignored = NSL (no self-loops)
cWDistAvNSL <- cDistAmNSL/cAmNSL
cWDistAvNSL[is.na(cWDistAvNSL)] <- 0 # replaces NaN values by zero.  Disconnected communities give this

cWDistAvNSLrank <- rep(-1,  times=numberCommunities)
cWDistAvNSLmean <- rep(-1,  times=numberCommunities)
cWDistAvNSLsd <- rep(-1,  times=numberCommunities)
for (ccc in 1:numberCommunities) {
 cWDistAvNSLrank[ccc] <- rank(cWDistAvNSL[ccc,])[ccc]
 cWDistAvNSLsd[ccc] <- sd(cWDistAvNSL[ccc,])
 cWDistAvNSLmean[ccc] <- mean(cWDistAvNSL[ccc,])
}
cWDistAvNSLz <- (diag(cWDistAvNSL)-cWDistAvNSLmean)/cWDistAvNSLsd
data.frame(rank=cWDistAvNSLrank, z=cWDistAvNSLz, pnorm=pnorm(cWDistAvNSLz), mean=cWDistAvNSLmean, sd=cWDistAvNSLsd)
}
