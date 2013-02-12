print("Need to first issue library(gplots)")
print("RAEmanagement Value in Community diversity analysis")

testOn=FALSE
#testOn=TRUE

onlyUK=TRUE
#onlyUK=FALSE


numberRuns=1000

# file with all data merged into one
outputMergedFileOn=FALSE

# Original simple plot
simplePlotOn=FALSE

#name	 "index"	 "Community"	 "degree.vp"	 "number"	 
#"strength"	 "k_C"	 "k_C.k"	 "str_C"	 "str_C.str"	 
#"institution"	 "discipline"	 "isolate"	 "latitude"	 "longitude"	 
#"RAE.rating.1996"	 "RAE.rating.2001"	 "AuthorName"	 "submitted"	 "degree.ad"	 
#"output"
valueIndex <- 3 #11=inst, 12=disc, 16=RAE-96, 17=RAE-01, 4=total degree, 6=total strength, 10=str_C/str
communityIndex <- 11 #3=VP Community, 11=inst, 12=disc, 16=RAE-96, 17=RAE-01

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
 vpName <- paste(inputSubdirectory,rootName,"vp.dat",sep="") # "RAEmanGCCLouvainQS100r0vertices.dat"
 adName <- paste(inputSubdirectory,rootName,"ad.dat",sep="") # "RAEmanagementAuthorData.dat"
} else {
 rootName <- "RAEman"
 vpName <- paste(inputSubdirectory,rootName,"GCCLouvainQS100r0vertices.dat",sep="")
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
print(paste("Looking at Community by",communityName,"(index=",communityIndex,") against values",valueName,"(index",valueIndex,")"))
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
nCommunities=length(cList)
print(paste("Communities min,max,number=",cMin,cMax,nCommunities))

# Produces a list of histograms and statistics for given community vector and value vector
doStats <- function(commVector,valueVector){
    cMin=min(commVector)
    cMax=max(commVector)
    cList=cMin:cMax
    vMin=min(valueVector)
    vMax=max(valueVector)
    breakVector=seq(vMin-0.5,vMax+0.5)
    #sumSqMatrix = matrix(NA,length(cList),length(breakVector)-1)
    #entropyMatrix = matrix(NA,length(cList),length(breakVector)-1)
    #This density matrix is suitable for doing correlations
    densityMatrix = NULL #matrix(NA,length(breakVector)-1,length(cList))
    countMatrix = NULL #matrix(NA,length(breakVector)-1,length(cList))
    totalValueVector = matrix(NA,length(cList))
    sumSqVector = matrix(NA,length(cList))
    entropyVector = matrix(NA,length(cList))
    hlist <- list()
    for (iii in 1:length(cList)) {
      cNumber=cList[iii]
      hhh <- hist(valueVector[commVector==cNumber],breaks=breakVector, plot=FALSE)
      hlist[[iii]] <- hhh
      d2v  <- (hhh$density)^2
      sv <-ifelse(hhh$den>0,(hhh$den)*log(hhh$den),0)
      countMatrix <- cbind(countMatrix,hhh$counts)
      densityMatrix <- cbind(densityMatrix,hhh$density)
#      sumSqMatrix[iii,] <- (hhh$density)^2
#     entropyMatrix[iii,] <-ifelse(hhh$den>0,(hhh$den)*log(hhh$den),0)
      totalValueVector[iii] <- sum(valueVector[commVector==cNumber])
      sumSqVector[iii] <- sum(d2v)
      entropyVector[iii] <- sum(sv)
    }
    list(hist=hlist,sumSq=sumSqVector,entropy=entropyVector,communityList=cList, totalValue=totalValueVector, countMatrix=countMatrix, densityMatrix=densityMatrix)
}
# End of doStats function

# Set up stats for Values
statsList<-doStats(vpadComm, vpadValues)

onlyUKName=""
if (onlyUK) onlyUKName="UK"

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
# set above numberRuns=1000
entropyPermList=matrix(0,numberRuns,nCommunities)
sumSqPermList=matrix(0,numberRuns,nCommunities)
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

sumSqRank <- seq(0,length=nCommunities)
entropyRank <- seq(0,length=nCommunities)
entropyStats<-NULL #matrix(0,nCommunities,4)
sumSqStats<-NULL #matrix(0,nCommunities,4)
for (ccc in 1:nCommunities){
 sumSqRank[ccc] <- length(sumSqPermList[,ccc][sumSqPermList[,ccc]>statsList$sumSq[ccc]])
 entropyRank[ccc] <- length(entropyPermList[,ccc][entropyPermList[,ccc]>statsList$entropy[ccc]])
 entropyStats<-rbind(entropyStats,rbind(statsDist(entropyPermList[,ccc])))
 sumSqStats<-rbind(sumSqStats,rbind(statsDist(sumSqPermList[,ccc])))
}

probabilitydf <- data.frame(pSumSq=sumSqRank/numberRuns,  pEntropy=entropyRank/numberRuns)
probOutputFileName <- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob",numberRuns,"Routput.dat",sep="")
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
values1 <-c(statsList$sumSq,sumSqStats[,3],sumSqStats[,4])
v1Min <-min(values1/sumSqStats[,1])
v1Max <-max(values1/sumSqStats[,1])

values2 <-c(statsList$entropy,entropyStats[,3],entropyStats[,4])
v2Min <-min(values2/entropyStats[,1])
v2Max <-max(values2/entropyStats[,1])

yLimit=c(min(v1Min,v2Min),max(v1Max,v2Max))
y1Limit=c(v1Min,v1Max)
y2Limit=c(v2Min,v2Max)
xLimit=c(1,nCommunities)
nameList = c(0:(nCommunities-1))

# ....................
mainTitle = paste("Probability",valueName,"Found in Null Model",sep=" ")
subTitle=paste(rootName,onlyUKName)
xLabel=paste("Community by",communityName)
plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,nCommunities+0.2), xlab=xLabel, ylab="Prob", xaxt="n")
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
points(sumSqRank/numberRuns,  col=colourlist[1], pch=1)
points((1:nCommunities+0.2),entropyRank/numberRuns, col=colourlist[2], pch=2)
unitList=c("p(f^2)","p(S)")
legendPosition<-"topright" 
legend(x=legendPosition ,y=NULL, unitList, col=colourlist[1:length(unitList)],pch=1:length(unitList));

# EPS plot
epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob",numberRuns,".eps",sep="")
print(paste("eps plotting",epsFileName), quote=FALSE)
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,nCommunities+0.2), xlab=xLabel, ylab="Prob", xaxt="n")
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
points(sumSqRank/numberRuns,  col=colourlist[1], pch=1)
points((1:nCommunities+0.2),entropyRank/numberRuns, col=colourlist[2], pch=2)
unitList=c("p(f^2)","p(S)")
legendPosition<-"topright" 
legend(x=legendPosition ,y=NULL, unitList, col=colourlist[1:length(unitList)],pch=1:length(unitList));
dev.off(which = dev.cur())


# Frequency^2 --------------------------
yLimit <- c(v1Min,v1Max)
mainTitle = paste(rootName,onlyUKName,"Community vs",valueName,"(Frequency)^2",sep=" ")
# ....................
if (simplePlotOn){
if (OSWindows) windows() else quartz()
plot(NULL,  main=mainTitle, ylim=yLimit, xlim=xLimit, xlab=xLabel, ylab="(f)^2/<f^2>")
points(statsList$sumSq/sumSqStats[,1], col="black", pch=1)
points(sumSqStats[,3]/sumSqStats[,1],  col="red", pch=2)
points(sumSqStats[,4]/sumSqStats[,1],  col="blue", pch=3)
}

# ....................
if (OSWindows) windows() else quartz()
barplot2(as.vector(statsList$sumSq/sumSqStats[,1]), horiz = FALSE, space=0.5, col = "lightcyan", ylim = c(0,yLimit[2]), 
 axisnames=TRUE, names.arg = nameList, las=2,
 main = mainTitle, xlab=xLabel, ylab="(f)^2/<f^2>",
 plot.ci = TRUE, ci.l = sumSqStats[,3]/sumSqStats[,1], ci.u = sumSqStats[,4]/sumSqStats[,1], ci.color = "black")

# ....................
if (OSWindows) windows() else quartz()
plotCI(as.vector((sumSqStats[,3]+sumSqStats[,4])/(2*sumSqStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = sumSqStats[,3]/sumSqStats[,1], ui = sumSqStats[,4]/sumSqStats[,1], 
 main = mainTitle, xlab=xLabel, ylab="(f)^2/<f^2>", xaxt="n")
points(statsList$sumSq/sumSqStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
# PDF plot
pdfFileName<-paste(outputSubdirectory,rootName,onlyUKName,valueName,"CommunityF2",numberRuns,".pdf",sep="")
print(paste("pdf plotting",pdfFileName), quote=FALSE)
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((sumSqStats[,3]+sumSqStats[,4])/(2*sumSqStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = sumSqStats[,3]/sumSqStats[,1], ui = sumSqStats[,4]/sumSqStats[,1], 
 main = mainTitle, xlab=xLabel, ylab="(f)^2/<f^2>", xaxt="n")
points(statsList$sumSq/sumSqStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
dev.off(which = dev.cur())
# EPS plot
epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"CommunityF2",numberRuns,".eps",sep="")
print(paste("eps plotting",epsFileName), quote=FALSE)
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((sumSqStats[,3]+sumSqStats[,4])/(2*sumSqStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = sumSqStats[,3]/sumSqStats[,1], ui = sumSqStats[,4]/sumSqStats[,1], 
 main = mainTitle, xlab=xLabel, ylab="(f)^2/<f^2>", xaxt="n")
points(statsList$sumSq/sumSqStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
dev.off(which = dev.cur())


# Entropy --------------------------
yLimit <- c(v2Min,v2Max)
mainTitle = paste(rootName,onlyUKName,"Community vs",valueName,"Entropy",sep=" ")

# ....................
if (simplePlotOn){
if (OSWindows) windows() else quartz()
plot(NULL,  main=mainTitle, ylim=yLimit, xlim=xLimit,  xlab=xLabel, ylab="S/<S>")
points(statsList$entropy/entropyStats[,1], col="black", pch=1)
points(entropyStats[,3]/entropyStats[,1],  col="red", pch=2)
points(entropyStats[,4]/entropyStats[,1],  col="blue", pch=3)
}

# ....................
if (OSWindows) windows() else quartz()
barplot2(as.vector(statsList$entropy/entropyStats[,1]), horiz = FALSE, space=0.5, col = "lightcyan", ylim = c(0,yLimit[2]), 
 axisnames=TRUE, names.arg = nameList, las=2,
 main = mainTitle, xlab=xLabel, ylab="S/<S>",
 plot.ci = TRUE, ci.l = entropyStats[,3]/entropyStats[,1], ci.u = entropyStats[,4]/entropyStats[,1], ci.color = "black")

# ....................
if (OSWindows) windows() else quartz()
plotCI(as.vector((entropyStats[,3]+entropyStats[,4])/(2*entropyStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = entropyStats[,4]/entropyStats[,1], ui = entropyStats[,3]/entropyStats[,1], 
 main = mainTitle, xlab=xLabel, ylab="S/<S>", xaxt="n")
points(statsList$entropy/entropyStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
# PDF plot
pdfFileName<-paste(outputSubdirectory,rootName,onlyUKName,valueName,"CommunityEntropy",numberRuns,".pdf",sep="")
print(paste("pdf plotting",pdfFileName), quote=FALSE)
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((entropyStats[,3]+entropyStats[,4])/(2*entropyStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = entropyStats[,4]/entropyStats[,1], ui = entropyStats[,3]/entropyStats[,1], 
 main = mainTitle, xlab=xLabel, ylab="S/<S>", xaxt="n")
points(statsList$entropy/entropyStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
dev.off(which = dev.cur())
# EPS plot
epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"CommunityEntropy",numberRuns,".eps",sep="")
print(paste("eps plotting",epsFileName), quote=FALSE)
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((entropyStats[,3]+entropyStats[,4])/(2*entropyStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = entropyStats[,4]/entropyStats[,1], ui = entropyStats[,3]/entropyStats[,1], 
 main = mainTitle, xlab=xLabel, ylab="S/<S>", xaxt="n")
points(statsList$entropy/entropyStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
dev.off(which = dev.cur())


