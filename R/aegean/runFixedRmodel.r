# produces Fixed R models 
DScale=150;
graphName = paste("FixedR_D",DScale,sep="")

screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE
OSWindows=TRUE

inputDir="input/"
outputDir="output/FixedR/"
testOn=FALSE
if (testOn) { rootName="aegean10S1L3a";
 numberRows=10
 #rootName="testRW3";
 #numberRows=3
} else { rootName="aegean39S1L3a";
 numberRows=39
}
printON=TRUE

source("basicAnalysis.r")

print(paste(rootName, ": number rows=", numberRows, ", max distance=", DScale),quote=FALSE)

dataList <-readData(inputDir, rootName)
distMatrix <- matrix(dataList$distVector,nrow=numberRows)

adjv <- ifelse( dataList$distVector<DScale, 1, 0)
adjMatrix <- matrix(adjv,nrow=numberRows)
# now remove diagonal entries
diagList <- c(1:numberRows)*(1+numberRows)-numberRows
adjMatrix[diagList]=0

library(igraph)
# use
fixedRGraph <- graph.adjacency(adjMatrix, mode="upper", diag=FALSE)

# convert array to list
#weight<-dataList$distVector
#MAXD=987654
#weight[diagList]=MAXD
#dim(weight)<-numberRows*numberRows
#source<-array(1:numberRows,dim=numberRows*numberRows)
#target <- t(array(1:numberRows,dim=c(numberRows,numberRows)))
#dim(target)=dim(source)
#sourceVector <- source[weight<=DScale]
#targetVector <- target[weight<=DScale]
#edgedf <-data.frame(source=sourceVector, target=targetVector)
# edge list has each edge twice for each direction i->j and j>-i
#fixedRGraph <- graph.data.frame(edgedf, directed=F, vertices=data.frame(name=1:numberRows))
#count.multiple(fixedRGraph)


#fixedRWeakClusters <- clusters(fixedRGraph,mode="weak")
fixedRClusters <- clusters(fixedRGraph,mode="strong")

#xvec <- dataList$sitedf$XPos
#yvec <- dataList$sitedf$YPos
xvec <- dataList$sitedf$Long
yvec <- dataList$sitedf$Lat

vc <- array(c(xvec,yvec),dim=c(numberRows,2))

#maxInFlowVector <- max(dataList$siteresultsdf$inFlowVector)
#vertexSize <- dataList$siteresultsdf$inFlowVector*10/maxInFlowVector+1
vertexSize <- degree(fixedRGraph, mode="in")+1

colourList=c("black", "red", "blue", "green", "magenta", "brown","cyan","gold","darkred","darkblue", "darkgreen","yellow","hotpink","lightblue", "olive");
ncolours <-length(colourList)
vertexColour <- colourList[ (fixedRClusters$membership +1)%%ncolours +1]

#plot(fixedRGraph,  layout=vc, vertex.label=dataList$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize)
#plot(fixedRGraph, edge.width=edgedf$weight*10, layout=vc, vertex.label=1:numberRows)
#plot(fixedRGraph, edge.width=edgedf$weight*10, layout=vc)

#Next three lines are altered as needed
fileNameFullRoot <- paste(outputDir,rootName,graphName,sep="")
if (screenOn){
       if (OSWindows) windows() else quartz()
       print(paste(graphName,"on screen"), quote=FALSE)
       plot(fixedRGraph,  layout=vc, vertex.label=dataList$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1, vertex.label.family="serif", )
}
# EPS plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       #postscript(epsFileName, fonts=c("serif", "Palatino"))
       plot(fixedRGraph,  layout=vc, vertex.label=dataList$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1)
       dev.off(which = dev.cur())
}
# PDF plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       plot(fixedRGraph,  layout=vc, vertex.label=dataList$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1)
       dev.off(which = dev.cur())
}


fixedRResList <- basicAnalysis (fixedRGraph,inputDir, outputDir, rootName, graphName, numberRows,  printOn=TRUE)
