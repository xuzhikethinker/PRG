# produces RW gravity models 
inputDir="input/"
outputDir="output/"
testOn=FALSE
screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE
OSWindows=TRUE


if (testOn) { rootName="testRW3";
 numberRows=3
 beta=1.02
 DScale=100
 countMax=10
} else { rootName="aegean39S1L3a";
 numberRows=39
 beta=1.04
 DScale=100
 countMax=1000
}
alpha=4.0
gamma=1.0
selfConsistentOn=FALSE
initialConditionsMode=2
printON=TRUE
#mainTitleOn=TRUE

minw=1/numberRows


source("RWmodel.r")
source("RWcalc.r")
source("RWflowcalc.r")
source("vectorStats.r")
source("RWtreecalc.r")
source("basicAnalysis.r")


print(paste(rootName, ": beta=", beta, ", D=",DScale,", alpha=", alpha, ", gamma=",gamma),quote=FALSE)
print(paste("countMax=",countMax,", Initial Conditions Mode=",initialConditionsMode),quote=FALSE)


#RWmodel( inputDir, outputDir, rootName, numberRows=39, beta=1.05, DScale=100, alpha=4.0, gamma=1.0, countMax=100, selfConsistentOn=FALSE, initialConditionsMode=2, printON=TRUE) 
reslist <- RWmodel(inputDir,outputDir,rootName,numberRows,beta,DScale, alpha, gamma, countMax, selfConsistentOn, initialConditionsMode, printON )

#names(reslist$sitedf)
# "ShortName" "Name"      "XPos"      "YPos"      "Size"      "Latitude"  "Longitude" "Region" 

#names(reslist$siteresultsdf)
# [1] "name"            "size"            "rankW"           "initialWVec"     "outFlowVector"  
# [6] "inFlowVector"    "inWeightVector"  "terminal"        "maxOutput"       "maxOutputTarget"
reslist$siteresultsdf


graphName = paste("_b", beta, "_D",DScale,"_a", alpha, "_g",gamma,sep="")
adjMatrix <- reslist$flowMatrix
adjMatrix [reslist$flowMatrix>=minw] =1
adjMatrix [reslist$flowatrix<minw] =0
# now remove diagonal entries
diagList <- c(1:numberRows)*(1+numberRows)-numberRows
adjMatrix[diagList]=0

library(igraph)
# use
RWflowGraph <- graph.adjacency(adjMatrix, mode="directed", weighted=TRUE, diag=FALSE)


# convert array to list
#weight<-reslist$flowMatrix
#dim(weight)<-numberRows*numberRows
#source<-array(1:numberRows,dim=dim(weight))
#target <- t(array(1:numberRows,dim=c(numberRows,numberRows)))
#dim(target)=dim(source)
#minw=1/numberRows
#edgedf <-data.frame(source=source[weight>minw], target=target[weight>minw], weight=weight[weight>minw])
#library(igraph)
#RWflowGraph <- graph.data.frame(edgedf, directed=T, vertices=data.frame(name=1:numberRows))

#xvec <- reslist$sitedf$XPos
#yvec <- reslist$sitedf$YPos
xvec <- reslist$sitedf$Long
yvec <- reslist$sitedf$Lat

vc <- array(c(xvec,yvec),dim=c(numberRows,2))

#vertexSize <- degree(RWflowGraph, mode="in")+1
maxInFlowVector <- max(reslist$siteresultsdf$inFlowVector)
vertexSize <- reslist$siteresultsdf$inFlowVector*10/maxInFlowVector+1

flowWeakClusters <- clusters(RWflowGraph,mode="weak")
flowStrongClusters <- clusters(RWflowGraph,mode="strong")

colourList=c("black", "red", "blue", "green", "magenta", "brown","cyan","gold","darkred","darkblue", "darkgreen","yellow","hotpink","lightblue", "olivedrab");
ncolours <-length(colourList)
vertexColour <- colourList[ (flowStrongClusters$membership +1)%%ncolours +1]

#Next three lines are altered as needed
fileNameFullRoot <- paste(outputDir,rootName,graphName,sep="")
if (screenOn){
       if (OSWindows) windows() else quartz()
       print(paste(graphName,"on screen"), quote=FALSE)
       plot(RWflowGraph,  layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1, vertex.label.family="serif", )
}
# EPS plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       #postscript(epsFileName, fonts=c("serif", "Palatino"))
       plot(RWflowGraph,  layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1)
       dev.off(which = dev.cur())
}
# PDF plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       plot(RWflowGraph,  layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1)
       dev.off(which = dev.cur())
}


RWResListBA <- basicAnalysis (RWflowGraph,inputDir, outputDir, rootName, graphName, numberRows,  printOn=TRUE)

