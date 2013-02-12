print("Need to first issue library(gplots)",quote=FALSE)
print("RAEmanagement distance in Community analysis", quote=FALSE)

# ***********************************************************************
# User alterable values
#testOn=FALSE
testOn=TRUE

numberRuns=100
print(paste("Making",numberRuns,"of null model"), quote=FALSE)

screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE


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


#xLimit=c(1,nCommunities)
nameList = c(cMin:cMax)
# ....................

distanceProbPlot <- function(probUAPvector,probUIPvector,subTitle,communityName,valueName,nameList){
    mainTitle = paste("Probability",valueName,"is larger in Null Model",sep=" ")
    xLabel=paste("Group by",communityName)
    nEntries=length(probUAPvector)
    unitListVector <- c("p(UAP)","p(UIP)")
    xShift=0.2
    plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,nEntries+xShift), xlab=xLabel, ylab="Probability", xaxt="n")
    axis(side=1, at=1:nEntries, labels=nameList, las=2) 
    points(probUAPvector,  col=colourlist[1], pch=1)
    points((1:nEntries+xShift),probUIPvector, col=colourlist[2], pch=2)
    legend(x="bottomleft" ,y=NULL, unitListVector, col=colourlist[1:length(unitListVector)],pch=1:length(unitListVector));
}

#plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,nCommunities+0.2), xlab=xLabel, ylab="Prob", xaxt="n")
#axis(side=1, at=1:nCommunities, labels=nameList, las=2) 
#points(distanceAverageUAPRank/numberRuns,  col=colourlist[1], pch=1)
#points((1:nCommunities+0.2),distanceAverageUIPRank/numberRuns, col=colourlist[2], pch=2)
#legend(x=legendPositionString ,y=NULL, unitListVector, col=colourlist[1:length(unitListVector)],pch=1:length(unitListVector));

fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,"Prob","r",numberRuns,sep="")

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

distancePlot <-function(valueUAPVector, lowerUAPVector, upperUAPVector,
                        valueUIPVector, lowerUIPVector, upperUIPVector,
                          subTitle, communityName, valueName, measureName, measureLabel, nameList){
   nEntries=length(valueUAPVector)                          
   xShift=0.2
   xLabel=paste("Group by",communityName)
   valuesAll=c(valueUAPVector, lowerUAPVector, upperUAPVector,valueUIPVector, lowerUIPVector, upperUIPVector)
   yLimit=range(valuesAll[is.finite(valuesAll)])
   mainTitle = paste(measureName,valueName)
   legendPositionString <- "bottomleft"
   unitListVector <- c("d(UAP)","d(UIP)")
   plotCI((lowerUAPVector+upperUAPVector)/2, pch=NA, gap=0, col=colourlist[1],  xlim=c(1,nEntries+xShift), ylim = yLimit, 
    li = lowerUAPVector, ui = upperUAPVector, 
    main = mainTitle, sub=subTitle, xlab=xLabel, ylab=measureLabel, xaxt="n")
   points(valueUAPVector, col=colourlist[1], pch=1)
   plotCI((1:nEntries+xShift),pch=NA, gap=0, col=colourlist[1],  xlim=c(1,nEntries+xShift), ylim = yLimit, 
    li = lowerUIPVector, ui = upperUIPVector, xaxt="n", add=TRUE)
   points(valueUIPVector, col=colourlist[2], pch=2)
   axis(side=1, at=1:nEntries, labels=nameList, las=2) 
   legend(x=legendPositionString ,y=NULL, unitListVector, col=colourlist[1:length(unitListVector)],pch=1:length(unitListVector));
}


# Absolute DISTANCES ....................

normalise<-1
measureName<-"Distance"
measureLabel<-"Distance"
measureFileLabel<-"Distance"
fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,measureFileLabel,"r",numberRuns,sep="")
# screen plot

if (OSWindows) windows() else quartz()
 distancePlot(distanceAverage$UAP/normalise, as.vector(distanceAverageUAPStats[,3])/normalise, as.vector(distanceAverageUAPStats[,4])/normalise,
              distanceAverage$UIP/normalise, as.vector(distanceAverageUIPStats[,3])/normalise, as.vector(distanceAverageUIPStats[,4])/normalise,
              subTitle, communityName, valueName, measureName, measureLabel, nameList)
# EPS plot
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
       epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob",numberRuns,".eps",sep="")
       distancePlot(distanceAverage$UAP/normalise, as.vector(distanceAverageUAPStats[,3])/normalise, as.vector(distanceAverageUAPStats[,4])/normalise,
              distanceAverage$UIP/normalise, as.vector(distanceAverageUIPStats[,3])/normalise, as.vector(distanceAverageUIPStats[,4])/normalise,
              subTitle, communityName, valueName, measureName, measureLabel, nameList)
       dev.off(which = dev.cur())
}       

# PDF plot
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
       distancePlot(distanceAverage$UAP/normalise, as.vector(distanceAverageUAPStats[,3])/normalise, as.vector(distanceAverageUAPStats[,4])/normalise,
              distanceAverage$UIP/normalise, as.vector(distanceAverageUIPStats[,3])/normalise, as.vector(distanceAverageUIPStats[,4])/normalise,
              subTitle, communityName, valueName, measureName, measureLabel, nameList)
       dev.off(which = dev.cur())
}



# value and confidence interval plot
valueConfidencePlot <-function(valueVector, lowerVector, upperVector,
                          subTitle, communityName, valueName, measureName, measureLabel, nameList){
   mainTitle = paste("Distance between",valueName,"in",communityName,measureName, sep=" ")
   xLabel=paste("Grouping by",communityName)
   allyvalues=c(valueVector,lowerVector,upperVector);
   yLimit <-range(allyvalues[is.finite(allyvalues)])
   plotCI( (lowerVector+upperVector)/2, pch=NA, gap=0, col="black", ylim = measureLabel,
          li = lowerVector, ui = upperVector,
          main = mainTitle, sub=subTitle, xlab=xLabel, ylab=measureLabel, xaxt="n")
   points(valueVector, col="black", pch=1)
   axis(side=1, at=1:length(valueVector), labels=nameList, las=2)
}

# value and confidence interval plot
valueConfidenceAllPlot <-function(valueVector, lowerVector, upperVector,
                          subTitle, communityName, valueName, measureName, measureLabel, nameList
                          screenOn,epsOn,pdfOn){
   # Screen
   if (screenOn){
    if (OSWindows) windows() else quartz()
    valueConfidencePlot(valueVector, lowerVector, upperVector,
                             subTitle, communityName, valueName, measureName, measureLabel, nameList)
   }

   # PDF plot
   if (pdfOn){
          pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
          print(paste("pdf plotting",epsFileName), quote=FALSE)
          pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)
          valueConfidencePlot(valueVector, lowerVector, upperVector,
                             subTitle, communityName, valueName, measureName, measureLabel, nameList)
          dev.off(which = dev.cur())
   }

   # EPS plot
   if (epsOn){
         epsFileName<- paste(fileNameFullRoot,".eps",sep="")
         print(paste("eps plotting",epsFileName), quote=FALSE)
         postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=10)
         valueConfidencePlot(valueVector, lowerVector, upperVector,
                                  subTitle, communityName, valueName, measureName, measureLabel, nameList)
         dev.off(which = dev.cur())
   }
}

# ------------------------------------------------------
# --- UAP - Unequal Author Pairs In Community
#

pairType<-"UAP"
valueVector<-as.Vector(distanceAverage$UAP)
lowerVector<-as.vector(distanceAverageUAPStats[,3])
upperVector<-as.vector(distanceAverageUAPStats[,4])
fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,pairType,"r",numberRuns,sep="")

# ....................

normalise<-as.vector(distanceAverageUAPStats[,1])
measureName<-paste("Distance/Average",pairType)
measureLabel<-paste("d/<d>",pairType)

valueConfidenceAllPlot(valueVector/normalise, lowerVector/normalise, upperVector/normalise,
                          subTitle, communityName, valueName, measureName, measureLabel, nameList,
                          screenOn,epsOn,pdfOn)

# ------------------------------------------------------
# --- UIP - Unequal Author Pairs In Community
#

pairType="UIP"
valueVector<-as.Vector(distanceAverage$UIP)
lowerVector<-as.vector(distanceAverageUIPStats[,3])
upperVector<-as.vector(distanceAverageUIPStats[,4])
fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,pairType,"r",numberRuns,sep="")

# ....................

normalise<-as.vector(distanceAverageUIPStats[,1])
measureName<-paste("Distance/Average",pairType)
measureLabel<-paste("d/<d>",pairType)

valueConfidenceAllPlot(valueVector/normalise, lowerVector/normalise, upperVector/normalise,
                          subTitle, communityName, valueName, measureName, measureLabel, nameList,
                          screenOn,epsOn,pdfOn)



