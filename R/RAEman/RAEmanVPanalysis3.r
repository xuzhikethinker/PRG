print("Need to first issue library(gplots)")
print("RAEmanagement Value in Community diversity analysis")

testOn=FALSE
#testOn=TRUE

onlyUK=TRUE
#onlyUK=FALSE


numberRuns=1000

# file with all data merged into one
outputMergedFileOn=FALSE

screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE

# Original simple plot
simplePlotOn=FALSE

# bar plot
barPlotOn=FALSE

#name	 "index"	 "Community"	 "degree.vp"	 "number"	 
#"strength"	 "k_C"	 "k_C.k"	 "str_C"	 "str_C.str"	 
#"institution"	 "discipline"	 "isolate"	 "latitude"	 "longitude"	 
#"RAE.rating.1996"	 "RAE.rating.2001"	 "AuthorName"	 "submitted"	 "degree.ad"	 
#"output"
valueIndex <- 3 #11=inst, 12=disc, 16=RAE-96, 17=RAE-01, 4=total degree, 6=total strength, 10=str_C/str
communityIndex <- 11 #3=VP Community, 11=inst, 12=disc, 16=RAE-96, 17=RAE-01

gammaName="15" #"15"  "85" "91" "100"
 


#dataSubdirectory="data/"
inputSubdirectory="input/"
outputSubdirectory="output/"

# FALSE if want  MacOS screen output
OSWindows=TRUE

#***********************************************************************
# Start of main code
#
colourlist=c("black", "red", "blue", "green", "magenta", "brown");

if (testOn)
{
 rootName <- "test"
 gammaName=""
 vpName <- paste(inputSubdirectory,rootName,"vp.dat",sep="") # "RAEmanGCCLouvainQS100r0vertices.dat"
 adName <- paste(inputSubdirectory,rootName,"ad.dat",sep="") # "RAEmanagementAuthorData.dat"
} else {
 rootName <- "RAEman"
 vpName <- paste(inputSubdirectory,rootName,"GCCLouvainQS",gammaName,"vertices.dat",sep="")
 adName <- paste(inputSubdirectory,rootName,"agementAuthorData.dat",sep="")
}

distanceFileName<-paste(inputSubdirectory,rootName,"distance.dat",sep="")



#aaEdgeListFileName<-paste(outputSubdirectory,rootName,"GCCinputELS.dat",sep="")


print(paste("Reading vertex partition from file",vpName))
vp <- read.table(vpName, header=TRUE, sep="\t", fill=TRUE);
print(paste("File",vpName," has",length(vp),"entries in",length(vp[[1]]),"rows(=authors) with columns:-"))
print(names(vp))

print(paste("Reading author discipline information from file",adName))
ad <- read.table(adName, header=TRUE, sep="\t", fill=TRUE);
print(paste("File",adName," has",length(ad),"entries in",length(ad[[1]]),"rows(=authors) with columns:-"))
print(names(ad))

#print(paste("Reading distance matrix from file",distanceFileName))
#distanceMatrix <- as.matrix(read.table(distanceFileName));
#print(paste("Read distnace matrix of dimensions",dim(distanceMatrix)))


vpad <- merge(vp,ad,by.x="name",by.y="id", sort = TRUE, suffixes = c(".vp",".ad"))


# Can we get UK only output?
if (outputMergedFileOn) {
       mergedFileName <- paste(outputSubdirectory,rootName,"MergedVP_AD_table.dat",sep="")
       write.table(vpad,file=mergedFileName, sep = "\t ", row.names=FALSE) 
       print(paste("Combined file has",length(vpad$name)," entires and is output as file",mergedFileName))
} else print("No merged file output")


valueName=names(vpad)[valueIndex]
communityName=names(vpad)[communityIndex]
print(paste("Grouping authors by ",communityName,"(index=",communityIndex,") against values",valueName,"(index",valueIndex,")"))
if (onlyUK) { 
 vpadValues=vpad[[valueIndex]][!is.na(vpad$lat)] 
 vpadComm=vpad[[communityIndex]][!is.na(vpad$lat)] 
 }else {
 vpadValues=vpad[[valueIndex]]
 vpadComm=vpad[[communityIndex]]
}
nAuthors <- length(vpadValues)
print(paste("Working with ",nAuthors," authors"))



cMin <- min(vpadComm)
cMax <- max(vpadComm)
cList=cMin:cMax
nCommunities <- cMax-cMin+1
#nCommunities=length(cList)
cShift <- 1-cMin
# Note values for communities are stored in cvectors etc with an index running from 1 to nCommunities
#  Thus the (community index)=(actual community number)-cShift
# e.g. 1=cMin - cShift, nCommunities = cMax-cShift

vMin <- min(vpadValues)
vMax <- max(vpadValues)
nValues <- vMax-vMin+1


#numberInCommunity = rep(0,nCommunities)
#for (ccc in cList) numberInCommunity[ccc-cShift] <- length(vpadComm[vpadComm==ccc])

print(paste("Grouping authors by ",communityName,": min,max,number=",cMin,cMax,nCommunities))
print(paste("Values are ",valueName,": min,max,number=",vMin,vMax))

# Produces a list of histograms and statistics for given community vector and value vector
# If there is no author in a given community then the numberVector will be 0 (others may have 0 or NA entries for this community)
doStats <- function(commVector,valueVector,numberInCommunity){
    cMin=min(commVector)
    cMax=max(commVector)
    cList=cMin:cMax
    vMin=min(valueVector)
    vMax=max(valueVector)
    breakVector=seq(vMin-0.5,vMax+0.5)
    #This density matrix is suitable for doing correlations
    densityMatrix = NULL 
    countMatrix = NULL 
    numberVector =     rep(NA,times=length(cList))
    totalValueVector = rep(NA,times=length(cList))
    sumSqVector =      rep(NA,times=length(cList))
    entropyVector =    rep(NA,times=length(cList))
    hlist <- list()
    for (iii in 1:length(cList)) {
      cNumber=cList[iii]
      hhh <- hist(valueVector[commVector==cNumber],breaks=breakVector, plot=FALSE)
      hlist[[iii]] <- hhh
      d2v  <- (hhh$density)^2
      sv <-ifelse(hhh$den>0,-(hhh$den)*log(hhh$den),0)
      countMatrix <- cbind(countMatrix,hhh$counts)
      densityMatrix <- cbind(densityMatrix,hhh$density)
      numberVector[iii] <- length(commVector[commVector==cNumber])
      totalValueVector[iii] <- sum(valueVector[commVector==cNumber])
      sumSqVector[iii] <- sum(d2v)
      entropyVector[iii] <- sum(sv)
    }
    list(hist=hlist,number=numberVector,sumSq=sumSqVector,entropy=entropyVector,communityList=cList, totalValue=totalValueVector, countMatrix=countMatrix, densityMatrix=densityMatrix)
}
# End of doStats function

# Set up stats for Values
statsList<-doStats(vpadComm, vpadValues)

onlyUKName=paste("g",gammaName,sep="")
if (onlyUK) onlyUKName=paste(onlyUKName,"UK",sep="")

corResultsOutputFileName <- paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,"Cor","Routput.dat",sep="")
print(paste("Writing ",communityName,"-",communityName," correlation matrix on",valueName,"variable as file",corResultsOutputFileName))
write(cor(statsList$densityMatrix), file = corResultsOutputFileName, ncolumns=nCommunities, append = FALSE, sep = "\t")

countMatrixOutputFileName <- paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,"Counts","Routput.dat",sep="")
print(paste("Writing ",valueName,"-",communityName," counts matrix as file",countMatrixOutputFileName))
write(t(statsList$countMatrix), file = countMatrixOutputFileName, ncolumns=nCommunities, append = FALSE, sep = "\t")

densityMatrixOutputFileName <- paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,"Density","Routput.dat",sep="")
print(paste("Writing ",valueName,"-",communityName," density matrix as file",densityMatrixOutputFileName))
write(t(statsList$densityMatrix), file = densityMatrixOutputFileName, ncolumns=nCommunities, append = FALSE, sep = "\t")

totalValueOutputFileName <- paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,"Totals","Routput.dat",sep="")
print(paste("Writing ",valueName," totals by ",communityName," vector as file",totalValueOutputFileName))
write(statsList$totalValue, file = totalValueOutputFileName, ncolumns=1, append = FALSE, sep = "\t")


count <- rep(0,  times=nCommunities)
entropyPermList=matrix(NA,numberRuns,nCommunities)
sumSqPermList=matrix(NA,numberRuns,nCommunities)
for (rrr in 1:numberRuns){
 # this first bit makes a permutation of the matrix
 pAuthors  <- sample(1:nAuthors)
 permComm <- vpadComm[pAuthors]
 pStatsList<-doStats(permComm, vpadValues)
 entropyPermList[rrr,] <- pStatsList$entropy
 sumSqPermList[rrr,]   <- pStatsList$sumSq
}


# Function to gather stats
statsDist <- function(values){
  m<-mean(values)
  q<-quantile(values,c(0.025,0.975))
  v<-var(values)
  #list(mean=m,var=v, valueMin=q[[1]], valueMax=q[[2]])
  c(m,v, q[[1]],q[[2]])
}

nStats<-length(statsDist(1:9)) # dummy call to fix number of statistics produced by routine
sumSqRank <- rep(NA,times=nCommunities)
entropyRank <- rep(NA,times=nCommunities)
entropyStats<-NULL 
sumSqStats<-NULL 
for (ccc in 1:nCommunities){
 if (statsList$number[ccc]==0){
      entropyStats<-rbind(entropyStats,rbind(rep(NA,times=nStats)))
      sumSqStats<-rbind(sumSqStats,rbind(rep(NA,times=nStats)))
 } else {
      sumSqRank[ccc] <- length(sumSqPermList[,ccc][sumSqPermList[,ccc]<statsList$sumSq[ccc]])
      entropyRank[ccc] <- length(entropyPermList[,ccc][entropyPermList[,ccc]>statsList$entropy[ccc]])
      entropyStats<-rbind(entropyStats,rbind(statsDist(entropyPermList[,ccc])))
      sumSqStats<-rbind(sumSqStats,rbind(statsDist(sumSqPermList[,ccc])))
 }
}

probabilitydf <- data.frame(pSumSq=sumSqRank/numberRuns,  pEntropy=entropyRank/numberRuns)
probOutputFileName <- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob","r",numberRuns,"Routput.dat",sep="")
print(paste("Writing probabilites of values compared to null model as file",totalValueOutputFileName))
write.table(probabilitydf, file = probOutputFileName,  append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)

# i is communityIndex+1
iMin=1
iMax=min(iMin+3,nCommunities)
valueListLength=length(statsList$hist[[iMin]]$density)
densityMatrix = matrix(0,iMax-iMin+1,valueListLength)
for (iii in iMin:iMax) densityMatrix[iii,] = statsList$hist[[iii]]$density

mainTitle = paste(rootName,onlyUKName,communityName,"Bars vs",valueName,sep=" ")
if (OSWindows) windows() else quartz()
barplot(densityMatrix,beside=TRUE,names.arg=1:valueListLength,las=2, main=mainTitle, xlab=valueName, ylab="Frequency", col=c("black", "red", "blue", "green"))


# *** PLOTS

source("vpAnalysisPlots.r")
nameList=cMin:cMax

# ... Probability Plots ....................
vpAnalysisAllProbPlot(sumSqRank/numberRuns, entropyRank/numberRuns,
                          nameList,
                          outputSubdirectory, rootName,onlyUKName,communityName,valueName,
                          screenOn,OSWindows,epsOn,pdfOn)

# ... Frequency^2 --------------------------
#subTitle=paste(rootName,onlyUKName,communityName,valueName,"Frequency Squared",sep=" ")
#print(paste("Before vpAnalysisPlot for ",subTitle))
     
normalise<-sumSqStats[,1]
vpAnalysisPlot(as.vector(statsList$sumSq/normalise), as.vector(sumSqStats[,3]/normalise), as.vector(sumSqStats[,4]/normalise),
                          nameList,
                          outputSubdirectory, rootName, onlyUKName, communityName, valueName, "Frequency Squared/Average", "f^2/<f^2>", "f2oav",
                          simplePlotOn,barPlotOn,screenOn,OSWindows,epsOn,pdfOn)
                          
normalise <- ifelse( statsList$number<nValues, 1/statsList$number, 1/nValues)
vpAnalysisPlot(as.vector(statsList$sumSq/normalise), as.vector(sumSqStats[,3]/normalise), as.vector(sumSqStats[,4]/normalise),
                          nameList,
                          outputSubdirectory, rootName, onlyUKName, communityName, valueName, "Frequency Squared/min", "f^2/f_min", "f2omin",
                          simplePlotOn,barPlotOn,screenOn,OSWindows,epsOn,pdfOn)

normalise <- 1
vpAnalysisPlot(as.vector(statsList$sumSq/normalise), as.vector(sumSqStats[,3]/normalise), as.vector(sumSqStats[,4]/normalise),
                          nameList,
                          outputSubdirectory, rootName, onlyUKName, communityName, valueName, "Frequency Squared", "f^2", "f2",
                          simplePlotOn,barPlotOn,screenOn,OSWindows,epsOn,pdfOn)

# ... Entropy --------------------------
normalise<-entropyStats[,1]
vpAnalysisPlot(as.vector(statsList$entropy/normalise), as.vector(entropyStats[,3]/normalise), as.vector(entropyStats[,4]/normalise),
                          nameList,
                          outputSubdirectory, rootName,onlyUKName,communityName,valueName,"Entropy/Average","S/<S>", "Soav",
                          simplePlotOn,barPlotOn,screenOn,OSWindows,epsOn,pdfOn)
                          
normalise <- ifelse(statsList$number<nValues,log(statsList$number),log(nValues))
vpAnalysisPlot(as.vector(statsList$entropy/normalise), as.vector(entropyStats[,3]/normalise), as.vector(entropyStats[,4]/normalise),
                          nameList,
                          outputSubdirectory, rootName,onlyUKName,communityName,valueName,"Entropy/min","S/Smin", "Somin",
                          simplePlotOn,barPlotOn,screenOn,OSWindows,epsOn,pdfOn)
normalise <- 1
vpAnalysisPlot(as.vector(statsList$entropy/entropyStats[,1]), as.vector(entropyStats[,3]/entropyStats[,1]), as.vector(entropyStats[,4]/entropyStats[,1]),
                          nameList,
                          outputSubdirectory, rootName,onlyUKName,communityName,valueName,"Entropy","S", "S",
                          simplePlotOn,barPlotOn,screenOn,OSWindows,epsOn,pdfOn)

