print("Need to first issue library(gplots)",quote=FALSE)
print("RAEmanagement distance in Community analysis", quote=FALSE)

# ***********************************************************************
# User alterable values
testOn=FALSE
#testOn=TRUE

debugOn=FALSE

numberRuns=10
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

gammaName="91" #"15"  "85" "91" "100"

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

# Assume that first row and column  is for institute number 1 hence must assume all instituites numbered on this scale
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

# Assume that institutes are numbered from 1 to iMax
iMin <- min(vpadInst)
iMax <- max(vpadInst)
nInstitutes <- iMax-iMin+1



nAuthors <- length(vpadComm)
print(paste("Working with ",nAuthors," authors in communities from ",cMin,"to",cMax, " and institutes from",iMin,"to",iMax ), quote=FALSE)

calcCommunityDistanceAveragesOld <- function(commVector,instVector,distMat){
    cMin <- min(commVector)
    cMax <- max(commVector)
    nCommunities <- cMax-cMin+1
    cShift <- 1-cMin
    
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
       distanceTotal[ccc+cShift]    = distanceTotal[ccc+cShift]   + sum(distMat[iii1,instInCommunity])
       nUIPInCommunity[ccc+cShift] = nUIPInCommunity[ccc+cShift] - length(instInCommunity[instInCommunity==iii1])  
      }
    } 
    distanceAverageUIP <- ifelse(nUIPInCommunity==0,0,distanceTotal/nUIPInCommunity) 
    distanceAverageUAP <- ifelse(nUAPInCommunity==0,0,distanceTotal/nUAPInCommunity)  
    list(UIP=distanceAverageUIP, UAP=distanceAverageUAP) 
}

calcCommunityDistanceAverages <- function(commVector,instVector,distMat){
    cMin <- min(commVector)
    cMax <- max(commVector)
    nCommunities <- cMax-cMin+1
    cShift <- 1-cMin
    
    iMin <- min(instVector)
    iMax <- max(instVector)
    #nInstitutes <- iMax-iMin+1
    instIndexVector <- 1:iMax
    pInst <- sample(instIndexVector)
    dm <- distMat[pInst,instIndexVector]
    # pInst[i0]=i1 and i2=pInst[i1] define i0 and i2
    for (iii1 in instIndexVector){
      #dm <- rbind(dm,distMat[,i1])
      iii0 <- instIndexVector[pInst==iii1]
      dm [iii0,iii1] <- distMat[pInst[iii1],iii1]
      dm [iii1,iii1] <- 0
    }
      
    distanceTotal <- rep(0,length=nCommunities)
    #number Unequal Author Pairs In Community
    nUAPInCommunity <- rep(0,length=nCommunities)  
    #number Unequal Institute Pairs In Community
    nUIPInCommunity <- rep(0,length=nCommunities)
    for (ccc in 1:nCommunities) {
      instInCommunity <- instVector[commVector==ccc-cShift]
      totalNumberInCommunity <- length(instInCommunity)
      nUIPInCommunity[ccc] <- totalNumberInCommunity*totalNumberInCommunity
      nUAPInCommunity[ccc] <- nUIPInCommunity[ccc]-totalNumberInCommunity
      for (iii1 in instInCommunity){
       distanceTotal[ccc]    = distanceTotal[ccc]   + sum(distMat[instInCommunity,iii1])
       nUIPInCommunity[ccc] = nUIPInCommunity[ccc] - length(instInCommunity[instInCommunity==iii1])  
      }
    } 
    distanceAverageUIP <- ifelse(nUIPInCommunity==0,0,distanceTotal/nUIPInCommunity) 
    distanceAverageUAP <- ifelse(nUAPInCommunity==0,0,distanceTotal/nUAPInCommunity)  
    list(UIP=distanceAverageUIP, UAP=distanceAverageUAP, nUAP=nUAPInCommunity, nUIP=nUIPInCommunity, distanceTotal=distanceTotal, randomDistanceMatrix=dm, permInst=pInst) 
}


distanceAverage <- calcCommunityDistanceAverages (vpadComm,vpadInst,distanceMatrix)
if (debugOn) print("**** 1")


#count <- rep(0,  times=nCommunities)
distanceAverageUAPrandom=matrix(0,numberRuns,nCommunities)
distanceAverageUIPrandom=matrix(0,numberRuns,nCommunities)
instVector=1:nInstitutes
for (rrr in 1:numberRuns){
 # this first bit makes a permutation of the distance between institutes
 pInst <- sample(instVector)
 dm <- distanceMatrix[pInst,instVector]
 dAvRandom <- calcCommunityDistanceAverages (vpadComm,vpadInst,dm) 
 distanceAverageUAPrandom[rrr,]  <- dAvRandom$UAP
 distanceAverageUIPrandom[rrr,]  <- dAvRandom$UIP
}
if (debugOn) print("**** 2")

# Function to gather stats
statsDist <- function(values){
  m<-mean(values)
  q<-quantile(values,c(0.025,0.975))
  v<-var(values)
  #list(mean=m,var=v, valueMin=q[[1]], valueMax=q[[2]])
  c(m,v, q[[1]],q[[2]],min(values),max(values))
}

if (debugOn) print("**** 3")

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

if (debugOn) print("**** 4")

probabilitydf <- data.frame(pUAP=distanceAverageUAPRank/numberRuns,  pUIP=distanceAverageUIPRank/numberRuns)
probOutputFileName <- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob",numberRuns,"Routput.dat",sep="")
print(paste("Writing probabilites of values compared to null model as file",probOutputFileName), quote=FALSE)
write.table(probabilitydf, file = probOutputFileName,  append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)

# *********************************************************************************************
# *** PLOTS
#
print("**** STARTING PLOTS",quote=FALSE)

# 10 was nice for these
epsPointSize=16
pdfPointSize=10
# cex=1 by default
cexSize=2

#xLimit=c(1,nCommunities)
nameList = c(cMin:cMax)
# ....................

distanceProbPlot <- function(probUAPvector,probUIPvector,subTitle,communityName,valueName,nameList, cexSize){
    mainTitle = paste("Probability",valueName,"is larger in Null Model",sep=" ")
    xLabel=paste("Group by",communityName)
    nEntries=length(probUAPvector)
    unitListVector <- c("p(UAP)","p(UIP)")
    xShift=0.2
    plot(NULL,  main=mainTitle, sub=subTitle, ylim=c(0,1), xlim=c(1,nEntries+xShift), xlab=xLabel, ylab="Probability", xaxt="n", cex=cexSize)
    axis(side=1, at=1:nEntries, labels=nameList, las=2) 
    points(probUAPvector,  col=colourlist[1], pch=1)
    points((1:nEntries+xShift),probUIPvector, col=colourlist[2], pch=2)
    legend(x="bottomleft" ,y=NULL, unitListVector, col=colourlist[1:length(unitListVector)],pch=1:length(unitListVector));
}


fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,"Prob","r",numberRuns,sep="")

print(paste("plotting with cexSize",cexSize), quote=FALSE)

mainTitle = paste("Probability",valueName,"is larger in Null Model",sep=" ")
subTitle=paste(rootName,onlyUKName,communityName,valueName)
xLabel=paste("Group by",communityName)
unitListVector <- c("p(UAP)","p(UIP)")
legendPositionString <- "bottomleft"
     if (screenOn) {
      if (OSWindows) windows() else quartz()
      distanceProbPlot(distanceAverageUAPRank/numberRuns,distanceAverageUIPRank/numberRuns,subTitle,communityName,valueName,nameList, cexSize) 
     }


# EPS plot
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=epsPointSize)
       epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob",numberRuns,".eps",sep="")
       distanceProbPlot(distanceAverageUAPRank/numberRuns,distanceAverageUIPRank/numberRuns,subTitle,communityName,valueName,nameList, cexSize) 
       dev.off(which = dev.cur())
}       

# PDF plot
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=pdfPointSize)
       distanceProbPlot(distanceAverageUAPRank/numberRuns,distanceAverageUIPRank/numberRuns,subTitle,communityName,valueName,nameList) 
       dev.off(which = dev.cur())
}

distancePlot <-function(valueUAPVector, lowerUAPVector, upperUAPVector,
                        valueUIPVector, lowerUIPVector, upperUIPVector,
                          subTitle, communityName, valueName,  measureLabel, nameList, cexSize){
   nEntries=length(valueUAPVector)                          
   xShift=0.3
   xLabel=paste("Group by",communityName)
   valuesAll=c(valueUAPVector, lowerUAPVector, upperUAPVector,valueUIPVector, lowerUIPVector, upperUIPVector)
   yLimit=range(valuesAll[is.finite(valuesAll)])
   mainTitle = paste(valueName)
   legendPositionString <- "bottomleft"
   unitListVector <- c("d(UAP)","d(UIP)")
   plotCI((lowerUAPVector+upperUAPVector)/2, pch=NA, gap=0, col=colourlist[1],  xlim=c(1,nEntries+xShift), ylim = yLimit, 
    li = lowerUAPVector, ui = upperUAPVector, 
    main = mainTitle, sub=subTitle, xlab=xLabel, ylab=measureLabel, xaxt="n", cex=cexSize)
   points(valueUAPVector, col=colourlist[1], pch=1)
   plotCI((1:nEntries)+xShift,(lowerUIPVector+upperUIPVector)/2,pch=NA, gap=0, col=colourlist[2],  xlim=c(1,nEntries+xShift), ylim = yLimit, 
    li = lowerUIPVector, ui = upperUIPVector, xaxt="n", add=TRUE, cex=cexSize)
   points((1:nEntries)+xShift,valueUIPVector+xShift, col=colourlist[2], pch=2)
   axis(side=1, at=1:nEntries, labels=nameList, las=2) 
   legend(x=legendPositionString ,y=NULL, unitListVector, col=colourlist[1:length(unitListVector)],pch=1:length(unitListVector));
}


# Absolute DISTANCES ....................

normalise<-1
measureLabel<-"Distance"
measureFileLabel<-"Distance"
fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,measureFileLabel,"r",numberRuns,sep="")
# screen plot

if (OSWindows) windows() else quartz()
 distancePlot(distanceAverage$UAP/normalise, as.vector(distanceAverageUAPStats[,3])/normalise, as.vector(distanceAverageUAPStats[,4])/normalise,
              distanceAverage$UIP/normalise, as.vector(distanceAverageUIPStats[,3])/normalise, as.vector(distanceAverageUIPStats[,4])/normalise,
              subTitle, communityName, valueName, measureLabel, nameList, cexSize)
# EPS plot
if (epsOn){
       epsFileName<- paste(fileNameFullRoot,".eps",sep="")
       print(paste("eps plotting",epsFileName), quote=FALSE)
       postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=epsPointSize)
       epsFileName<- paste(outputSubdirectory,rootName,onlyUKName,valueName,"Prob",numberRuns,".eps",sep="")
       distancePlot(distanceAverage$UAP/normalise, as.vector(distanceAverageUAPStats[,3])/normalise, as.vector(distanceAverageUAPStats[,4])/normalise,
              distanceAverage$UIP/normalise, as.vector(distanceAverageUIPStats[,3])/normalise, as.vector(distanceAverageUIPStats[,4])/normalise,
              subTitle, communityName, valueName,  measureLabel, nameList, cexSize)
       dev.off(which = dev.cur())
}       

# PDF plot
if (pdfOn){
       pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
       print(paste("pdf plotting",epsFileName), quote=FALSE)
       pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=pdfPointSize)
       distancePlot(distanceAverage$UAP/normalise, as.vector(distanceAverageUAPStats[,3])/normalise, as.vector(distanceAverageUAPStats[,4])/normalise,
              distanceAverage$UIP/normalise, as.vector(distanceAverageUIPStats[,3])/normalise, as.vector(distanceAverageUIPStats[,4])/normalise,
              subTitle, communityName, valueName,  measureLabel, nameList, cexSize)
       dev.off(which = dev.cur())
}



# value and confidence interval plot
valueConfidencePlot <-function(valueVector, lowerVector, upperVector,
                          subTitle, communityName, valueName,  measureName, measureLabel, nameList, cexSize){
   mainTitle = paste(measureName,"in",communityName, sep=" ")
   xLabel=paste("Grouping by",communityName)
   allyvalues=c(valueVector,lowerVector,upperVector);
   yLimit <-range(allyvalues[is.finite(allyvalues)])
   plotCI( (lowerVector+upperVector)/2, pch=NA, gap=0, col="black", ylim = yLimit,
          li = lowerVector, ui = upperVector,
          main = mainTitle, sub=subTitle, xlab=xLabel, ylab=measureLabel, xaxt="n", cex=cexSize)
   points(valueVector, col="black", pch=1)
   axis(side=1, at=1:length(valueVector), labels=nameList, las=2)
}

# value and confidence interval plot
valueConfidenceAllPlot <-function(valueVector, lowerVector, upperVector,
                          subTitle, communityName, valueName,  measureName, measureLabel, nameList,
                          screenOn,epsOn,pdfOn, cexSize){
   # Screen
   if (screenOn){
    if (OSWindows) windows() else quartz()
    valueConfidencePlot(valueVector, lowerVector, upperVector,
                             subTitle, communityName, valueName, measureName,  measureLabel, nameList, cexSize)
   }

   # PDF plot
   if (pdfOn){
          pdfFileName<- paste(fileNameFullRoot,".pdf",sep="")
          print(paste("pdf plotting",epsFileName), quote=FALSE)
          pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=pdfPointSize)
          valueConfidencePlot(valueVector, lowerVector, upperVector,
                             subTitle, communityName, valueName,  measureName, measureLabel, nameList, cexSize)
          dev.off(which = dev.cur())
   }

   # EPS plot
   if (epsOn){
         epsFileName<- paste(fileNameFullRoot,".eps",sep="")
         print(paste("eps plotting",epsFileName), quote=FALSE)
         postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=epsPointSize)
         valueConfidencePlot(valueVector, lowerVector, upperVector,
                                  subTitle, communityName, valueName,  measureName, measureLabel, nameList, cexSize)
         dev.off(which = dev.cur())
   }
}

# ------------------------------------------------------
# --- UAP - Unequal Author Pairs In Community
#

pairType<-"UAP"
valueVector<-as.vector(distanceAverage$UAP)
lowerVector<-as.vector(distanceAverageUAPStats[,3])
upperVector<-as.vector(distanceAverageUAPStats[,4])
fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,pairType,"r",numberRuns,sep="")

# ....................

normalise<-as.vector(distanceAverageUAPStats[,1])
measureName<-paste(valueName,"/Average",pairType)
measureLabel<-paste("d/<d>",pairType)

valueConfidenceAllPlot(valueVector/normalise, lowerVector/normalise, upperVector/normalise,
                          subTitle, communityName, valueName, measureName, measureLabel, nameList,
                          screenOn,epsOn,pdfOn, cexSize)

# ------------------------------------------------------
# --- UIP - Unequal Author Pairs In Community
#

pairType="UIP"
valueVector<-as.vector(distanceAverage$UIP)
lowerVector<-as.vector(distanceAverageUIPStats[,3])
upperVector<-as.vector(distanceAverageUIPStats[,4])
fileNameFullRoot=paste(outputSubdirectory,rootName,onlyUKName,communityName,valueName,pairType,"r",numberRuns,sep="")

# ....................

normalise<-as.vector(distanceAverageUIPStats[,1])
measureName<-paste("Distance/Average",pairType)
measureLabel<-paste("d/<d>",pairType)

valueConfidenceAllPlot(valueVector/normalise, lowerVector/normalise, upperVector/normalise,
                          subTitle, communityName, valueName, measureName, measureLabel, nameList,
                          screenOn,epsOn,pdfOn, cexSize)



