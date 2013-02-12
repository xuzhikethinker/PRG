# Uses iGraph to make simplified version of graph

inputDir="input/"
outputDir="output/"

rootName="MT119"
inputFileName=paste(rootName,".net",sep="")


library(igraph)
print(paste("Reading  Pajek file from",inputFileName),quote=FALSE)
mtnet  <- read.graph(inputFileName, format = "pajek")
print(paste("Original",inputFileName,"has",length(V(mtnet)),"vertices and ",length(E(mtnet)),"edges"),quote=FALSE)
dsmtnet<-simplify(mtnet)
print(paste("Simplified but directed",inputFileName,"has",length(V(dsmtnet)),"vertices and ",length(E(dsmtnet)),"edges"),quote=FALSE)
smtnet<-as.undirected(dsmtnet, mode = "collapse")
print(paste("Simplified and undirected",inputFileName,"has",length(V(smtnet)),"vertices and ",length(E(smtnet)),"edges"),quote=FALSE)

outputFileName=paste(rootName,"S.net",sep="")
print(paste("Writing Simplified network to ",outputFileName),quote=FALSE)
write.graph(smtnet, outputFileName, format="pajek")

compList<-clusters(smtnet)
compFileName=paste(rootName,"S_VertexComponentLabel.dat",sep="")
print(paste("Writing data on simplified network components to ",compFileName),quote=FALSE)
write.table(compList$membership, compFileName, quote=FALSE, sep="\t")


