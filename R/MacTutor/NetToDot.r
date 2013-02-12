# Uses iGraph to make .net to .dot conversion

inputDir="input/"
outputDir="output/"

rootName="119RemovedDirected"
#rootName="119Removed"
inputFileName=paste(rootName,".net",sep="")

inputDateFileName="MTbirthYearsreduced.txt"


library(igraph)
print(paste("Reading  Pajek file from",inputFileName),quote=FALSE)
mtnet  <- read.graph(inputFileName, format = "pajek")
print(paste("Original",inputFileName,"has",length(V(mtnet)),"vertices and ",length(E(mtnet)),"edges"),quote=FALSE)
smtnet<-simplify(mtnet)
#vcount(smtnet)
#ecount(smtnet)
Vlist <- V(smtnet)
# NOTE igraph numbers vertices from 0 to length-1
Vname <- as.vector(Vlist$id)
print(paste("Simplified",inputFileName,"has",length(Vlist),"vertices and ",length(E(smtnet)),"edges"),quote=FALSE)

# Now link information on birth dates and year, possibly partial information
print(paste("Reading name and birth dates data from",inputDateFileName),quote=FALSE)
nddf <- read.table(inputDateFileName, header=TRUE, fill=TRUE)
nv<-as.vector(nddf$Name)
yv<-as.vector(nddf$Year)
# now exclude names without proper date
#nv<-nvfull[yvfull<3000]
#yv<-yvfull[yvfull<3000]

source<-1
nl <- neighbors(smtnet, source, mode="out")

nvertices=10 #length(Vlist)

yearOfVertex = rep(8888,length(Vlist))
for (vertexIndex in 1:nvertices) {
 year = yv[nv==Vname[vertexIndex]]
 #print(paste(Vname[vertexIndex],"vertex",vertexIndex,", year",year), quote=FALSE)
 if (length(year)==1) yearOfVertex[vertexIndex] = year[1]
 if (length(year)>1) print(paste("*** ERROR vertex",vertexIndex,", year",year), quote=FALSE)
 }

for (vertexIndex in 1:nvertices) {
 sourceYear <- yearOfVertex[vertexIndex] 
 #print(sourceYear)
 if (sourceYear<3000) {
  # MUST subract and add one as igraph indexes from 0, other vectors here count from 1 
  nl <- neighbors(smtnet, vertexIndex-1, mode="in")+1
  #print(nl)
  nyall <- yearOfVertex[nl]
  ny <-nyall[nyall<sourceYear]
  nnall <- Vname[nl]
  nn <-nnall[nyall<sourceYear]
  ni <-nl[nyall<sourceYear]
  #print(ny)
  if (length(ny)>0) {
   print(paste("Target",vertexIndex,sourceYear,Vname[vertexIndex],"neighbours: years, names, indices"),quote=FALSE)
   print(ny,quote=FALSE)
   print(nn,quote=FALSE)
   print(ni,quote=FALSE)
  }
 }
}


