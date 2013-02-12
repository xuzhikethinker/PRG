# Uses iGraph to make simplified version of graph

#inputDir="input/"
#outputDir="output/"
#testOn=FALSE
screenOn=TRUE
epsOn=FALSE
pdfOn=TRUE
OSWindows=TRUE


rootName="MTSGCCO"
#rootName="test"

typeName="_BTF";
distEndingName="maxdist.dat"

methodNumber=4
hclustMethod <- c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
methodName=hclustMethod[methodNumber]

xclustFileName=paste(rootName,typeName,"_clustx",methodName,".dat",sep="")
print(paste("Reading maximum distance table from",xclustFileName),quote=FALSE)
xclust  <- read.table(xclustFileName, header = TRUE)

xvec <- xclust$x
numberRows<-length(xvec)
yvec <- 1:numberRows

vc <- array(c(xvec,yvec),dim=c(numberRows,2))


elistFileName=paste(rootName,typeName,"_EdgeList.dat",sep="")
print(paste("Reading edge list from",elistFileName),quote=FALSE)
eList  <- as.matrix(read.table(elistFileName, header = TRUE))

library(igraph)
# use
dag <- graph.edgelist(eList, directed=TRUE)

#Next three lines are altered as needed
fileNameFullRoot <- paste(rootName,typeName,"_clustx",methodName,sep="")
if (screenOn){
       if (OSWindows) windows() else quartz()
       #print(paste(graphName,"on screen"), quote=FALSE)
       plot(dag,  layout=vc, vertex.label=xclust$name)
       #, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1, vertex.label.family="serif", )
}
# EPS plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       #postscript(epsFileName, fonts=c("serif", "Palatino"))
       plot(dag,  layout=vc, vertex.label=xclust$name)
       #plot(PPAGraph,  layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1)
       dev.off(which = dev.cur())
}
# PDF plot, for iGraph and fonts see see http://lists.gnu.org/archive/html/igraph-help/2007-07/msg00010.html
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10, fonts=c("serif", "Palatino"))
       plot(dag,  layout=vc, vertex.label=xclust$name)
       #plot(PPAGraph,  layout=vc, vertex.label=reslist$sitedf$ShortName, vertex.color=vertexColour, vertex.size=vertexSize, vertex.label.dist=1)
       dev.off(which = dev.cur())
}

