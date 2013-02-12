# Uses iGraph to make simplified version of graph

#inputDir="input/"
#outputDir="output/"

rootName="MTSGCCO"
#rootName="test"
typeName="_BTF";
distEndingName="maxdist.dat"
distFileName=paste(rootName,typeName,distEndingName,sep="")


print(paste("Reading maximum distance table from",distFileName),quote=FALSE)
distinput  <- read.table(distFileName)
maxDistMat <- distinput+t(distinput)
nvertices=dim(maxDistMat)[1]
print(paste("Found table of dimension",nvertices),quote=FALSE)

#for (iii in 1:nvertices){ for (jjj in 1:nvertices){
#  if (!is.numeric(maxDistMat[iii,jjj])) print(paste("Non-numeric maxDistMat value",maxDistMat[iii,jjj],"at ",iii,jjj),quote=FALSE)}}

namesFileName<-paste(rootName,"names.dat",sep="");
print(paste("Reading names from from",namesFileName),quote=FALSE)
nameList <- scan(namesFileName, what="", skip=1);

# rank time matrix
iii <- array(1:nvertices,c(nvertices,nvertices))
dt <- iii-t(iii)
dtSq <- dt * dt
maxDistMatSq <- maxDistMat * maxDistMat
dx <- sqrt(dtSq-maxDistMatSq)



# Produces dendrograms based on distances for aegean sites
source("Dendrogram.r")
mainTitleOn=FALSE
# methods=c("single", "complete", "ward", "average", "mcquitty", "median", "centroid")
methodNumber=4
#DendrogramFromFile ( rootName, typeName, distEndingName, methodNumber, nvertices, mainTitleOn) 
distanceHclust<-DendrogramCalcPlot ( rootName, typeName, methodNumber, nameList, dx,  mainTitleOn)

df <- data.frame(name=nameList, x=distanceHclust$order)
dfFileName=paste(rootName,typeName,"_clustx",distanceHclust$method,".dat",sep="")
print(paste("Writing names and position to ",dfFileName),quote=FALSE)
write.table(df, dfFileName, quote=FALSE, sep="\t")


