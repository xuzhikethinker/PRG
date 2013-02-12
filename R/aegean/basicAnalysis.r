# Basic Analysis
basicAnalysis <- function(graph,inputDir, outputDir, rootName, graphName, numberRows=39,  printOn=TRUE){
print(paste("Basic Analysis for ",graphName),quote=FALSE)

#meanDist <- array(0,dim=numberRows)
#for (s in (1:numberRows)) meanDist[s] <- mean(distMatrix[s,reslist$PPAMatrix[s,]<degree+0.001])


clusterList <- list(weak= clusters(graph,mode="weak"), strong= clusters(graph,mode="strong"))

#diameter(graph, directed = TRUE, unconnected = TRUE, weights = NULL)
diameterList = list(undirected=diameter(graph, directed = FALSE), directed=diameter(graph, directed = TRUE))
averagePathlength <- average.path.length(graph, directed=TRUE, unconnected=TRUE)

vertexDegreeIn  <- degree(graph, mode="in")
vertexDegreeOut <- degree(graph, mode="out")
eList <- get.edgelist(graph)
edgeSourceDegreeIn <- vertexDegreeIn[eList[,1]]
edgeSourceDegreeOut <- vertexDegreeOut[eList[,1]]
edgeTargetDegreeIn <- vertexDegreeIn[eList[,2]]
edgeTargetDegreeOut <- vertexDegreeOut[eList[,2]]
edgeDegreeMatrix <- cbind(edgeSourceDegreeIn, edgeSourceDegreeOut, edgeTargetDegreeIn, edgeTargetDegreeOut)
degreeCor <- cor(edgeDegreeMatrix)
degreeCov <- cov(edgeDegreeMatrix)


bvec<- betweenness(graph, v=V(graph), directed = TRUE)
prList <- page.rank (graph, vids = V(graph), directed = TRUE, damping = 0.85)

vertexdf <- data.frame(kin=vertexDegreeIn, kout= vertexDegreeOut, betweenness=bvec, pageRank=prList$vector)

resultsOutputFileName <- paste(outputDir,rootName,graphName,"_vertices.dat",sep="")
print(paste("Output vertex results to file",resultsOutputFileName),quote=FALSE)
write.table(vertexdf, file = resultsOutputFileName , append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)

list( vertexdf=vertexdf, kcor = degreeCor, kcov = degreeCov, clusters=clusterList, diameter=diameterList, averagePathlength=averagePathlength )
}
