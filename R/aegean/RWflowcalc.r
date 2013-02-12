RWflowcalc <-function(fixedOutputvector, wVec, potlMatrix, beta, selfConsistentOn=FALSE){
numberRows<-length(wVec)
wbetaVec <- ((wVec)^beta)
dim(wbetaVec) <- numberRows
tempVec <- potlMatrix %*% wbetaVec
vVec <- 1/tempVec

if (selfConsistentOn) outWeightVec <- (wVec*vVec) 
else outWeightVec <- (fixedOutputvector*vVec)
dim(outWeightVec) <- numberRows

tttMat <- outWeightVec %o% wbetaVec
#print(tttMat)
flowMatrix <- ( tttMat ) * potlMatrix
#print(paste("Flow Matrix"), quote=FALSE)
#print(flowMatrix,quote=FALSE)
oneList <- rep(1,times=numberRows)

outFlowVec <- flowMatrix %*% oneList
inFlowVec <- t(oneList %*% flowMatrix)
resList <-list(inFlowVec=inFlowVec, outFlowVec=outFlowVec, flowMatrix=flowMatrix  )

}
