source("MatrixDendrogram.r")
#MatrixDendrogram <- function(plotName, adjacencyMatrix, nameList, methodNumber, OSWindows) {
#methodNumber gives entry from c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");

OSWindows=TRUE

dataSubdirectory="data/"
rootName="UKHEIii"
projTypeList=c("kpinv","kp2inv")
projTypeNumber=1
projType=projTypeList[projTypeNumber]
amextName="outputAdjMat.dat"
amfileName=paste(dataSubdirectory,rootName,projType,amextName,sep="")
print(paste("Reading Adjacency Matrix data from file",amfileName))

# Adjacency Matrix of projected graph
am <- read.table(amfileName)
nameList <- 1:(length(am)) #names(am)



plotName=paste(rootName,projType,amextName,sep="")

methodNumber=1;
MatrixDendrogram ( plotName, am, nameList, methodNumber, OSWindows)


methodNumber=2;
MatrixDendrogram ( plotName, am, nameList, methodNumber, OSWindows)


methodNumber=4;
MatrixDendrogram ( plotName, am, nameList, methodNumber, OSWindows)

