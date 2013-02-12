# produces PPA models 
inputDir="input/"
outputDir="output/PPA/"
testOn=FALSE
screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE
OSWindows=TRUE



if (testOn) { rootName="aegean10S1L3a";
 numberRows=10
 #rootName="testRW3";
 #numberRows=3
 degree=3
} else { rootName="aegean39S1L3a";
 numberRows=39
 degree=3
}
printON=TRUE
#mainTitleOn=TRUE

source("PPAmodel.r")
source("basicAnalysis.r")

print(paste(rootName, ": number rows=", numberRows, ", degree=", degree),quote=FALSE)
graphName = paste("k",degree,sep="")

#RWmodel( inputDir, outputDir, rootName, numberRows=39, beta=1.05, DScale=100, alpha=4.0, gamma=1.0, countMax=100, selfConsistentOn=FALSE, initialConditionsMode=2, printON=TRUE) 
reslist <- PPAmodel(inputDir,outputDir,rootName,numberRows, printON )

#names(reslist$sitedf)

meanDist <- array(0,dim=numberRows)
for (s in (1:numberRows)) meanDist[s] <- mean(reslist$distMatrix[s,reslist$PPAMatrix[s,]<degree+0.001])

graphName = paste("k",degree,sep="")
adjMatrix <- reslist$PPAMatrix
adjMatrix [reslist$PPAMatrix>degree+0.001] =0
adjMatrix [reslist$PPAMatrix<=degree+0.001] =1
# now remove diagonal entries
diagList <- c(1:numberRows)*(1+numberRows)-numberRows
adjMatrix[diagList]=0

library(igraph)
# use
PPAGraph <- graph.adjacency(adjMatrix, mode="directed", weighted=TRUE, diag=FALSE)

# convert array to list
#weight<-reslist$PPAMatrix
#dim(weight)<-numberRows*numberRows
#source<-array(1:numberRows,dim=dim(weight))
#target <- t(array(1:numberRows,dim=c(numberRows,numberRows)))
#dim(target)=dim(source)
#minw=degree+0.001
# edge list has each edge twice for each direction i->j and j>-i
#edgedf <-data.frame(source=source[weight<minw], target=target[weight<minw])
#library(igraph)
#PPAGraph <- graph.data.frame(edgedf, directed=T, vertices=data.frame(name=1:numberRows))
#PPAGraph <- graph.data.frame(edgedf, directed=T)
#count.multiple(fixedRGraph)


ppaWeakClusters <- clusters(PPAGraph,mode="weak")
ppaStrongClusters <- clusters(PPAGraph,mode="strong")

#xvec <- reslist$sitedf$XPos
#yvec <- reslist$sitedf$YPos
xvec <- reslist$sitedf$Long
yvec <- reslist$sitedf$Lat

vc <- array(c(xvec,yvec),dim=c(numberRows,2))

vertexSize <- degree(PPAGraph, mode="in")+1

colourList=c("black", "red", "blue", "green", "magenta", "brown","cyan","gold","darkred","darkblue", "darkgreen","yellow","hotpink","lightblue", "olive");
ncolours <-length(colourList)
vertexColour <- colourList[ (ppaStrongClusters$membership +1)%%ncolours +1]

#Next three lines are altered as needed
fileNameFullRoot <- paste(outputDir,rootName,graphName,sep="")
if (screenOn){
       if (OSWindows) windows() else quartz()
       print(paste(graphName,"on screen"), quote=FALSE)
       plot(PPAGraph,  layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1, vertex.label.family="serif", )
}
# EPS plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       #postscript(epsFileName, fonts=c("serif", "Palatino"))
       plot(PPAGraph,  layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1)
       dev.off(which = dev.cur())
}
# PDF plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       plot(PPAGraph,  layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1)
       dev.off(which = dev.cur())
}


PPAResList <- basicAnalysis (PPAGraph,inputDir, outputDir, rootName, graphName, numberRows,  printOn=TRUE)

