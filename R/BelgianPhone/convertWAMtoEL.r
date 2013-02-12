# Convert Weighted Adjacency Matrix to Edge List 
inputDir="input/"
outputDir="output/"
testOn=FALSE
if (testOn) { rootName="testRW3";
# numberRows=3
} else { rootName="BelgianPhoneDataAgregatedNetwork_DNis";
 #numberRows=39
}
#alpha=4.0
#gamma=1.0
#selfConsistentOn=FALSE
#initialConditionsMode=2
#printON=TRUE
#mainTitleOn=TRUE

endingList = c("_NDis.txt","_NCallsNis.txt","_TCallsNis.txt")

fileName<-paste(inputDir,rootName,"_sitesdata.dat",sep="");
print(paste("--- File:",fileName), quote=FALSE)
inputMatrix <- scan(distanceFileName,0);

# convert array to list
dim(inputMatrix)<-numberRows*numberRows
source<-array(1:numberRows,dim=dim(weight))
target <- t(array(1:numberRows,dim=c(numberRows,numberRows)))
dim(target)=dim(source)
minw=1/numberRows
edgedf <-data.frame(source=source[weight>minw], target=target[weight>minw], weight=weight[weight>minw])

library(igraph)
flowg <- graph.data.frame(edgedf, directed=T, vertices=data.frame(name=1:numberRows))

#xvec <- reslist$sitedf$XPos
#yvec <- reslist$sitedf$YPos
xvec <- reslist$sitedf$Long
yvec <- reslist$sitedf$Lat

vc <- array(c(xvec,yvec),dim=c(numberRows,2))

maxInFlowVector <- max(reslist$siteresultsdf$inFlowVector)
vertexSize <- reslist$siteresultsdf$inFlowVector*10/maxInFlowVector+1

plot(flowg, edge.width=edgedf$weight*10, layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.size=vertexSize)
#plot(flowg, edge.width=edgedf$weight*10, layout=vc, vertex.label=1:numberRows)
#plot(flowg, edge.width=edgedf$weight*10, layout=vc)

