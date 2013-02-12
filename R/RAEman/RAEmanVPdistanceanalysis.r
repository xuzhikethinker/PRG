print("Need to first issue library(gplots)",quote=FALSE)
print("RAEmanagement distance in Community analysis", quote=FALSE)

# ***********************************************************************
# USer alterable values
#testOn=FALSE
testOn=TRUE

numberRuns=100
print(paste("Making",numberRuns,"of null model"), quote=FALSE)

# file with all data merged into one
outputMergedFileOn=FALSE

#  Possible indices in numerical order from 1
#name	 "index"	 "Community"	 "degree.vp"	 "number"	 
#"strength"	 "k_C"	 "k_C.k"	 "str_C"	 "str_C.str"	 
#"institution"	 "discipline"	 "isolate"	 "latitude"	 "longitude"	 
#"RAE.rating.1996"	 "RAE.rating.2001"	 "AuthorName"	 "submitted"	 "degree.ad"	 
#"output"
instIndex <- 11 #11=inst, 12=disc, 16=RAE-96, 17=RAE-01, 4=total degree, 6=total strength, 10=str_C/str
communityIndex <- 3 #3=VP Community, 11=inst, 12=disc, 16=RAE-96, 17=RAE-01

gammaName="100" #"15"  "85" "91" "100"

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



print(paste("Reading vertex partition from file",vpName), quote=FALSE)
vp <- read.table(vpName, header=TRUE, sep="\t", fill=TRUE);
print(paste("File",vpName," has",length(vp),"entries in",length(vp[[1]]),"rows(=authors) with columns:-"), quote=FALSE)
print(names(vp), quote=FALSE)

print(paste("Reading author discipline information from file",adName), quote=FALSE)
ad <- read.table(adName, header=TRUE, sep="\t", fill=TRUE);
print(paste("File",adName," has",length(ad),"entries in",length(ad[[1]]),"rows(=authors) with columns:-"), quote=FALSE)
print(names(ad), quote=FALSE)

print(paste("Reading distance matrix from file",distanceFileName), quote=FALSE)
distanceMatrix <- as.matrix(read.table(distanceFileName));
print(paste("Read distance matrix of dimensions",dim(distanceMatrix)), quote=FALSE)

# Name to Community 
vpad <- merge(vp,ad,by.x="name",by.y="id", sort = TRUE, suffixes = c(".vp",".ad"))

if (outputMergedFileOn) {
       mergedFileName <- paste(outputSubdirectory,rootName,"MergedVP_AD_table.dat",sep="")
       write.table(vpad,file=mergedFileName, sep = "\t ", row.names=FALSE)
       print(paste("Combined file has",length(vpad$name)," entires and is output as file",mergedFileName), quote=FALSE)
} else print("No merged file output", quote=FALSE)


onlyUKName=paste("g",gammaName,"UK",sep="")


valueName="Distance" 
communityName=names(vpad)[communityIndex]
print(paste("Looking at Community by",communityName,"(index=",communityIndex,") against UK Institute distance"), quote=FALSE)

vpadInst=vpad$inst[!is.na(vpad$lat)] 
vpadComm=vpad[[communityIndex]][!is.na(vpad$lat)] 

cMin <- min(vpadComm)
cMax <- max(vpadComm)
nCommunities <- cMax-cMin+1
cShift <- 1-cMin

iMin <- min(vpadInst)
iMax <- max(vpadInst)
nInstitutes <- iMax-iMin+1
iShift <-iMin-1


nAuthors <- length(vpadComm)
print(paste("Working with ",nAuthors," authors in communities from ",cMin,"to",cMax, " and institutes from",iMin,"to",iMax ), quote=FALSE)

calcCommunityDistanceAverages <- function(commVector,instVector,distMat){
    cMin <- min(commVector)
    cMax <- max(commVector)
    nCommunities <- cMax-cMin+1
    cShift <- 1-cMin
    
    iMin <- min(instVector)
    iMax <- max(instVector)
    nInstitutes <- iMax-iMin+1
    iShift<-1-iMin
    
    distanceTotal <- rep(0,length=nCommunities)
    #number Unequal Author Pairs In Community
    nUAPInCommunity <- rep(0,length=nCommunities)  
    #number Unequal Institute Pairs In Community
    nUIPInCommunity <- rep(0,length=nCommunities)
    for (ccc in cMin:cMax) {
      instInCommunity <- instVector[commVector==ccc] 
      nUAPInCommunity[ccc+cShift] <- length(instInCommunity)*(length(instInCommunity)-1)
      nUIPInCommunity[ccc+cShift] <- length(instInCommunity)*length(instInCommunity)
      for (iii1 in instInCommunity){
       distanceTotal[ccc+cShift]    = distanceTotal[ccc+cShift]   + sum(distMat[iii1+iShift,instInCommunity+iShift])
       nUIPInCommunity[ccc+cShift] = nUIPInCommunity[ccc+cShift] - length(instInCommunity[instInCommunity==iii1])  
      }
    } 
    distanceAverageUIP <- ifelse(nUIPInCommunity==0,0,distanceTotal/nUIPInCommunity) 
    distanceAverageUAP <- ifelse(nUAPInCommunity==0,0,distanceTotal/nUAPInCommunity)  
    list(UIP=distanceAverageUIP, UAP=distanceAverageUAP) 
}
distanceAverage <- calcCommunityDistanceAverages (vpadComm,vpadInst,distanceMatrix)
print("**** 1")


#count <- rep(0,  times=nCommunities)
distanceAverageUAPrandom=matrix(0,numberRuns,nCommunities)
distanceAverageUIPrandom=matrix(0,numberRuns,nCommunities)
for (rrr in 1:numberRuns){
 # this first bit makes a permutation of the distance between institutes
 pInst <- sample(1:nInstitutes)
 dm <- distanceMatrix[pInst,pInst]
 dAvRandom <- calcCommunityDistanceAverages (vpadComm,vpadInst,dm) 
 distanceAverageUAPrandom[rrr,]  <- dAvRandom$UAP
 distanceAverageUIPrandom[rrr,]  <- dAvRandom$UIP
}
print("**** 2")

# Function to gather stats
statsDist <- function(values){
  m<-mean(values)
  q<-quantile(values,c(0.025,0.975))
  v<-var(values)
  #list(mean=m,var=v, valueMin=q[[1]], valueMax=q[[2]])
  c(m,v, q[[1]],q[[2]],min(values),max(values))
}

print("**** 3")

distanceAverageUAPRank  <- seq(0,length=nCommunities)
distanceAverageUIPRank  <- seq(0,length=nCommunities)
distanceAverageUAPStats <-NULL 
distanceAverageUIPStats <-NULL 
for (ccc in 1:nCommunities){
 distanceAverageUAPRank[ccc] <- length(distanceAverageUAPrandom[,ccc][distanceAverageUAPrandom[,ccc]>distanceAverage$UAP[ccc]])
 distanceAverageUAPStats<-rbind(distanceAverageUAPStats,rbind(statsDist(distanceAverageUAPrandom[,ccc])))
 distanceAverageUIPRank[ccc] <- length(distanceAverageUIPrandom[,ccc][distanceAverageUIPrandom[,ccc]>distanceAverage$UIP[ccc]])
 distanceAverageUIPStats<-rbind(distanceAverageUIPStats,rbind(statsDist(distanceAverageUIPrandom[,ccc])))
}

print("**** 4")

probabilitydf <- data.frame(pUAP=distanceAverageUAPRank/numberRuns,  pUIP=distanceAverageUIPRank/numberRuns)
probOutputFileName <- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob",numberRuns,"Routput.dat",sep="")
print(paste("Writing probabilites of values compared to null model as file",totalValueOutputFileName), quote=FALSE)
write.table(probabilitydf, file = probOutputFileName,  append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)

# *********************************************************************************************
# *** PLOTS
#
print("**** STARTING PLOTS")


values1 <-c(distanceAverage$UAP,distanceAverageUAPStats[,3],distanceAverageUAPStats[,4])
v1Min <-min(values1/distanceAverageUAPStats[,1])
v1Max <-max(values1/distanceAverageUAPStats[,1])

values2 <-c(distanceAverage$UIP,distanceAverageUIPStats[,3],distanceAverageUIPStats[,4])
v2Min <-min(values2/distanceAverageUIPStats[,1])
v2Max <-max(values2/distanceAverageUIPStats[,1])

yLimit=c(min(v1Min,v2Min),max(v1Max,v2Max))
y1Limit=c(v1Min,v1Max)
y2Limit=c(v2Min,v2Max)

xLimit=c(1,nCommunities)
nameList = c(cMin:cMax)
fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,"Prob","r",numberRuns,sep="")
# ....................

distanceProbPlot <- function(probUAPvector,probUIPvector,subTitle,communityName,valueName,nameList){
    mainTitle = paste("Probability",valueName,"is larger in Null Model",sep=" ")
    xLabel=paste("Group by",communityName)
    nEntries=length(probUAPvector)
    unitListVector <- c("p(UAP)","p(UIP)")
    plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,nEntries+0.2), xlab=xLabel, ylab="Probability", xaxt="n")
    axis(side=1, at=1:nEntries, labels=nameList, las=2) 
    points(probUAPvector,  col=colourlist[1], pch=1)
    points((1:nEntries+0.2),probUIPvector, col=colourlist[2], pch=2)
    legend(x="bottomleft" ,y=NULL, unitListVector, col=colourlist[1:length(unitListVector)],pch=1:length(unitListVector));
}

#plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,nCommunities+0.2), xlab=xLabel, ylab="Prob", xaxt="n")
#axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
#points(distanceAverageUAPRank/numberRuns,  col=colourlist[1], pch=1)
#points((1:nCommunities+0.2),distanceAverageUIPRank/numberRuns, col=colourlist[2], pch=2)
#legend(x=legendPositionString ,y=NULL, unitListVector, col=colourlist[1:length(unitListVector)],pch=1:length(unitListVector));


mainTitle = paste("Probability",valueName,"is larger in Null Model",sep=" ")
subTitle=paste(rootName,onlyUKName,communityName,valueName)
xLabel=paste("Group by",communityName)
unitListVector <- c("p(UAP)","p(UIP)")
legendPositionString <- "bottomleft"
     if (screenOn) {
      if (OSWindows) windows() else quartz()
      distanceProbPlot(distanceAverageUAPRank/numberRuns,distanceAverageUIPRank/numberRuns,subTitle,communityName,valueName,nameList) 
     }




# EPS plot
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
       epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob",numberRuns,".eps",sep="")
       distanceProbPlot(distanceAverageUAPRank/numberRuns,distanceAverageUIPRank/numberRuns,subTitle,communityName,valueName,nameList) 
       dev.off(which = dev.cur())
}       

# PDF plot
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
       distanceProbPlot(distanceAverageUAPRank/numberRuns,distanceAverageUIPRank/numberRuns,subTitle,communityName,valueName,nameList) 
       dev.off(which = dev.cur())
}

# Absolute DISTANCES ....................
mainTitle = valueName
subTitle=paste(rootName,onlyUKName)
xLabel=paste("Community by",communityName)
unitListVector <- c("p(UAP)","p(UIP)")
legendPositionString <- "bottomleft"
valuesUAP <-c(distanceAverage$UAP,distanceAverageUAPStats[,3],distanceAverageUAPStats[,4])
valuesUIP <-c(distanceAverage$UIP,distanceAverageUIPStats[,3],distanceAverageUIPStats[,4])
yMax <- max(valuesUAP,valuesUIP)
yMin <- min(valuesUAP,valuesUIP)
yLimit <-c(yMin,yMax)
unitListVector <- c("d(UAP)","d(UIP)")
legendPositionString <- "bottomleft"
xShift=0.2
xLimit <-c(1,nCommunities+xShift)

# screen plot
if (OSWindows) windows() else quartz()
plotCI(as.vector((distanceAverageUAPStats[,3]+distanceAverageUAPStats[,4])/(2)), pch=NA, gap=0, col=colourlist[1],  xlim=xLimit, ylim = yLimit, 
 li = distanceAverageUAPStats[,3], ui = distanceAverageUAPStats[,4], 
 main = mainTitle, xlab=xLabel, ylab="distance", xaxt="n")
points(distanceAverage$UAP, col=colourlist[1], pch=1)
plotCI((1:nCommunities+xShift),as.vector((distanceAverageUIPStats[,3]+distanceAverageUIPStats[,4])/(2)), pch=NA, gap=0, col=colourlist[2], ylim = yLimit, 
 li = distanceAverageUIPStats[,3], ui = distanceAverageUIPStats[,4], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n", add=TRUE)
points((1:nCommunities+xShift),distanceAverage$UIP, col=colourlist[2], pch=2)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
legend(x=legendPositionString ,y=NULL, unitListVector, col=colourlist[1:length(unitList)],pch=1:length(unitList));
# PDF plot
pdfFileName<-paste(outputSubdirectory,rootName,onlyUKName,valueName,"Distance",numberRuns,".pdf",sep="")
print(paste("pdf plotting",pdfFileName), quote=FALSE)
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((distanceAverageUAPStats[,3]+distanceAverageUAPStats[,4])/(2)), pch=NA, gap=0, col=colourlist[1],  xlim=xLimit, ylim = yLimit, 
 li = distanceAverageUAPStats[,3], ui = distanceAverageUAPStats[,4], 
 main = mainTitle, xlab=xLabel, ylab="distance", xaxt="n")
points(distanceAverage$UAP, col=colourlist[1], pch=1)
plotCI((1:nCommunities+xShift),as.vector((distanceAverageUIPStats[,3]+distanceAverageUIPStats[,4])/(2)), pch=NA, gap=0, col=colourlist[2], ylim = yLimit, 
 li = distanceAverageUIPStats[,3], ui = distanceAverageUIPStats[,4], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n", add=TRUE)
points((1:nCommunities+xShift),distanceAverage$UIP, col=colourlist[2], pch=2)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
legend(x=legendPositionString ,y=NULL, unitListVector, col=colourlist[1:length(unitList)],pch=1:length(unitList));
dev.off(which = dev.cur())
# EPS plot
epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Distance",numberRuns,".eps",sep="")
print(paste("eps plotting",epsFileName), quote=FALSE)
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((distanceAverageUAPStats[,3]+distanceAverageUAPStats[,4])/(2)), pch=NA, gap=0, col=colourlist[1],  xlim=xLimit, ylim = yLimit, 
 li = distanceAverageUAPStats[,3], ui = distanceAverageUAPStats[,4], 
 main = mainTitle, xlab=xLabel, ylab="distance", xaxt="n")
points(distanceAverage$UAP, col=colourlist[1], pch=1)
plotCI((1:nCommunities+xShift),as.vector((distanceAverageUIPStats[,3]+distanceAverageUIPStats[,4])/(2)), pch=NA, gap=0, col=colourlist[2], ylim = yLimit, 
 li = distanceAverageUIPStats[,3], ui = distanceAverageUIPStats[,4], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n", add=TRUE)
points((1:nCommunities+xShift),distanceAverage$UIP, col=colourlist[2], pch=2)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
legend(x=legendPositionString ,y=NULL, unitListVector, col=colourlist[1:length(unitList)],pch=1:length(unitList));
dev.off(which = dev.cur())


# ------------------------------------------------------
# --- UAP - Unequal Author Pairs In Community
#

pairType="UAP"

yLimit <- c(v1Min,v1Max)
mainTitle = paste(rootName,onlyUKName,"Community vs",valueName,"UAP",sep=" ")
# ....................
if (simplePlotOn){
if (OSWindows) windows() else quartz()
plot(NULL,  main=mainTitle, ylim=yLimit, xlim=xLimit, xlab=xLabel, ylab=pairType)
points(distanceAverage$UAP/distanceAverageUAPStats[,1], col="black", pch=1)
points(distanceAverageUAPStats[,3]/distanceAverageUAPStats[,1],  col="red", pch=2)
points(distanceAverageUAPStats[,4]/distanceAverageUAPStats[,1],  col="blue", pch=3)
}

# ....................
if (OSWindows) windows() else quartz()
barplot2(as.vector(distanceAverage$UAP/distanceAverageUAPStats[,1]), horiz = FALSE, space=0.5, col = "lightcyan", ylim = c(0,yLimit[2]), 
 axisnames=TRUE, names.arg = nameList, las=2,
 main = mainTitle, xlab=xLabel, ylab=pairType,
 plot.ci = TRUE, ci.l = distanceAverageUAPStats[,3]/distanceAverageUAPStats[,1], ci.u = distanceAverageUAPStats[,4]/distanceAverageUAPStats[,1], ci.color = "black")

# ....................
if (OSWindows) windows() else quartz()
plotCI(as.vector((distanceAverageUAPStats[,3]+distanceAverageUAPStats[,4])/(2*distanceAverageUAPStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = distanceAverageUAPStats[,3]/distanceAverageUAPStats[,1], ui = distanceAverageUAPStats[,4]/distanceAverageUAPStats[,1], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n")
points(distanceAverage$UAP/distanceAverageUAPStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
# PDF plot
pdfFileName<-paste(outputSubdirectory,rootName,onlyUKName,valueName,"CommunityUAP",numberRuns,".pdf",sep="")
print(paste("pdf plotting",pdfFileName), quote=FALSE)
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((distanceAverageUAPStats[,3]+distanceAverageUAPStats[,4])/(2*distanceAverageUAPStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = distanceAverageUAPStats[,3]/distanceAverageUAPStats[,1], ui = distanceAverageUAPStats[,4]/distanceAverageUAPStats[,1], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n")
points(distanceAverage$UAP/distanceAverageUAPStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
dev.off(which = dev.cur())
# EPS plot
epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"CommunityUAP",numberRuns,".eps",sep="")
print(paste("eps plotting",epsFileName), quote=FALSE)
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((distanceAverageUAPStats[,3]+distanceAverageUAPStats[,4])/(2*distanceAverageUAPStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = distanceAverageUAPStats[,3]/distanceAverageUAPStats[,1], ui = distanceAverageUAPStats[,4]/distanceAverageUAPStats[,1], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n")
points(distanceAverage$UAP/distanceAverageUAPStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
dev.off(which = dev.cur())


# ------------------------------------------------------
# --- UIP - Unequal Author Pairs In Community
#

pairType="UIP"

yLimit <- c(v2Min,v2Max)
mainTitle = paste(rootName,onlyUKName,"Community vs",valueName,"UIP",sep=" ")
# ....................
if (simplePlotOn){
if (OSWindows) windows() else quartz()
plot(NULL,  main=mainTitle, ylim=yLimit, xlim=xLimit, xlab=xLabel, ylab=pairType)
points(distanceAverage$UIP/distanceAverageUIPStats[,1], col="black", pch=1)
points(distanceAverageUIPStats[,3]/distanceAverageUIPStats[,1],  col="red", pch=2)
points(distanceAverageUIPStats[,4]/distanceAverageUIPStats[,1],  col="blue", pch=3)
}

# ....................
if (OSWindows) windows() else quartz()
barplot2(as.vector(distanceAverage$UIP/distanceAverageUIPStats[,1]), horiz = FALSE, space=0.5, col = "lightcyan", ylim = c(0,yLimit[2]), 
 axisnames=TRUE, names.arg = nameList, las=2,
 main = mainTitle, xlab=xLabel, ylab=pairType,
 plot.ci = TRUE, ci.l = distanceAverageUIPStats[,3]/distanceAverageUIPStats[,1], ci.u = distanceAverageUIPStats[,4]/distanceAverageUIPStats[,1], ci.color = "black")

# ....................
if (OSWindows) windows() else quartz()
plotCI(as.vector((distanceAverageUIPStats[,3]+distanceAverageUIPStats[,4])/(2*distanceAverageUIPStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = distanceAverageUIPStats[,3]/distanceAverageUIPStats[,1], ui = distanceAverageUIPStats[,4]/distanceAverageUIPStats[,1], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n")
points(distanceAverage$UIP/distanceAverageUIPStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
# PDF plot
pdfFileName<-paste(outputSubdirectory,rootName,onlyUKName,valueName,"CommunityUIP",numberRuns,".pdf",sep="")
print(paste("pdf plotting",pdfFileName), quote=FALSE)
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((distanceAverageUIPStats[,3]+distanceAverageUIPStats[,4])/(2*distanceAverageUIPStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = distanceAverageUIPStats[,3]/distanceAverageUIPStats[,1], ui = distanceAverageUIPStats[,4]/distanceAverageUIPStats[,1], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n")
points(distanceAverage$UIP/distanceAverageUIPStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
dev.off(which = dev.cur())
# EPS plot
epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"CommunityUIP",numberRuns,".eps",sep="")
print(paste("eps plotting",epsFileName), quote=FALSE)
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
plotCI(as.vector((distanceAverageUIPStats[,3]+distanceAverageUIPStats[,4])/(2*distanceAverageUIPStats[,1])), pch=NA, gap=0, col="black", ylim = yLimit, 
 li = distanceAverageUIPStats[,3]/distanceAverageUIPStats[,1], ui = distanceAverageUIPStats[,4]/distanceAverageUIPStats[,1], 
 main = mainTitle, xlab=xLabel, ylab=pairType, xaxt="n")
points(distanceAverage$UIP/distanceAverageUIPStats[,1], col="black", pch=1)
axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
dev.off(which = dev.cur())

