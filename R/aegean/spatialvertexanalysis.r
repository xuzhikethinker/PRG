# Analyse spatial arrangements of vertices
# Needs ariadne style "inputDir"+"rootName"+"typeName"+"_sitesdata.dat" with names column
# Needs ariadne style distance matrix "inputDir"+"rootName"+"typeName"+"_sitesdata.dat" with names column

#source("readData.r")
source("Dendrogram.r")

rootName="circleN40D100.0-C10-J50.0"; typeName=""; numberRows=40

#rootName="aegean10"; typeName="S1L3a"; numberRows=10
#rootName="aegean39"; numberRows=39


methodNumberList=c(1,4);

OSWindows=TRUE
cat(paste("---     OS Windows for screen plots:",OSWindows,"\n"));  

dendPlotOn=TRUE
cat(paste("---     Dendrogram plots:",dendPlotOn,"\n"));

inputDir="input/"
outputDir="output/"

basicName=paste(rootName,typeName,sep="")
cat(paste("---     Basic file name:",basicName,"\n"));

siteFileName<-paste(inputDir,rootName,typeName,"_sitesdata.dat",sep="");
cat(paste("---     Site file:",siteFileName,"\n"));
sitedf <- read.table(siteFileName, header=TRUE, sep="\t", fill=TRUE);
# site file columns can include
# ShortName	Name	XPos	YPos	Size	Latitude	Longitude	Region




# Use tryCatch to calc distances
#tryCatch(dm <- scan(distanceFileName,0), calculateDistanceFile(distanceFileName,sitedf));
distanceFileName<-paste(inputDir,rootName,typeName,"_distancematrix.dat",sep="")
cat(paste("--- Distance file:",distanceFileName),"\n")
distanceVector <- scan(distanceFileName,0);
distanceMatrix <- matrix(distanceVector,nrow=numberRows);
distanceList<-distanceVector[distanceVector>0] # this removed all zero entries including the diagonal matrix entries


hclustMethod <- c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
dendmainTitleOn=TRUE
dendscreenPlotOn=TRUE
dendepsPlotOn=TRUE
dendpdfPlotOn=TRUE
distanceHclust = list();
maxClusterheight = rep(-1,length=length(methodNumberList))
clusterMethodName= rep("XXX",length=length(methodNumberList))
for (nnn  in 1:length(methodNumberList)){
 methodNumber=methodNumberList[nnn]
 distanceDist <- as.dist(distanceMatrix)
 methodName=hclustMethod[methodNumber]
 clusterMethodName[nnn]=methodName;
 distanceHclust[[nnn]] <- hclust(distanceDist, method=methodName) #, members=NULL
 maxClusterheight[nnn] <- distanceHclust[[nnn]]$height[numberRows-1]
 cat(paste("--- Hierarchical Cluster Method:",methodName," Min distance",maxClusterheight[nnn] ,"\n"))
 if (dendPlotOn) {
  DendrogramPlot ( outputDir, rootName, typeName, methodName, sitedf$Name, distanceDist,  distanceHclust[[nnn]], dendmainTitleOn, OSWindows, dendscreenPlotOn, dendepsPlotOn, dendpdfPlotOn)
 }
}

Lfivenum=fivenum(distanceList)
Lmean=mean(distanceList)
Lsd=sd(distanceList)

distanceStatisticsText <- function(sep=" "){
  cat(paste("Distance statistics for ",rootName,typeName,"\n",sep=sep))
  cat(paste("Average",Lmean,"\n",sep=sep))
  cat(paste("Std.Dev.",Lsd,"\n",sep=sep))
  cat(paste("min","Q1","median","Q3","max","\n",sep=sep))
  cat(paste(Lfivenum[1],Lfivenum[2],Lfivenum[3],Lfivenum[4],Lfivenum[5],"\n",sep=sep))
  cat(paste("Hierarchical Cluster Method","Min distance","\n",sep=sep)) 
  for (nnn  in 1:length(methodNumberList)){
   cat(paste(clusterMethodName[nnn],maxClusterheight[nnn],"\n" ,sep=sep)) 
   }
}

cat("******************************","\n")
distanceStatisticsText(" ")

distanceTextFileName<-paste(outputDir,rootName,typeName,"_Distance.txt",sep="")
cat(paste("--- Distance Text file:",distanceTextFileName,"\n"))
sink(distanceTextFileName) # all screen sent to file, use split=TRUE to copy
 distanceStatisticsText(" ")
sink()

#resdf = data.frame(Lfivenum=fivenum(distanceList),Lmean=Lmean, Lsd=Lsd, Lcluster=maxClusterheight, clusterMethodName=clusterMethodName)
#distanceDataFrameFileName<-paste(outputDir,rootName,typeName,"_DistanceDataFrame.dat",sep="")
#cat(paste("--- Distance Data Frame file:",distanceDataFrameFileName))
#write.table(resdf, file = distanceDataFrameFileName, append = FALSE,  sep = "\t", quote=FALSE, row.names = FALSE, col.names = TRUE)
