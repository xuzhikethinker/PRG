# RAEmanagement VP analysis
#testOn=FALSE
testOn=TRUE

# FALSE if want  MacOS screen output
OSWindows=TRUE

dataSubdirectory="data/"

if (testOn)
{
 rootName <- "test"
 vpName <- paste(dataSubdirectory,rootName,"vp.dat",sep="") # "RAEmanGCCLouvainQS100r0vertices.dat"
 adName <- paste(dataSubdirectory,rootName,"ad.dat",sep="") # "RAEmanagementAuthorData.dat"
} else {
 rootName <- "RAEman"
 vpName <- paste(dataSubdirectory,rootName,"GCCLouvainQS100r0vertices.dat",sep="")
 adName <- paste(dataSubdirectory,rootName,"agementAuthorData.dat",sep="")
}
outputName <- paste(dataSubdirectory,rootName,"MergedVP_AD_table.dat",sep="")
distanceFileName<-paste(dataSubdirectory,rootName,"distance.dat",sep="")

print(paste("Reading vertex partition from file",vpName))
vp <- read.table(vpName, header=TRUE, sep="\t", fill=TRUE);
length(vp)
names(vp)

print(paste("Reading author discipline information from file",adName))
ad <- read.table(adName, header=TRUE, sep="\t", fill=TRUE);
length(ad)
names(ad)

print(paste("Reading distance matrix from file",distanceFileName))
distanceMatrix <- as.matrix(read.table(distanceFileName));
print(paste("Read",length(distanceMatrix),"institutions"))


#vpad <- merge(vp,ad,by.x="name",by.y="id", all = TRUE, sort = TRUE, suffixes = c(".vp",".ad"),)
vpad <- merge(vp,ad,by.x="name",by.y="id", sort = TRUE, suffixes = c(".vp",".ad"),)
vpad
#fix(vpad)
#save(vpad)
write.table(vpad,file=outputName, sep = "\t ", row.names=FALSE)
#name	 "index"	 "Community"	 "degree.vp"	 "number"	 
#"strength"	 "k_C"	 "k_C.k"	 "str_C"	 "str_C.str"	 
#"institution"	 "discipline"	 "isolate"	 "latitude"	 "longitude"	 
#"RAE.rating.1996"	 "RAE.rating.2001"	 "AuthorName"	 "submitted"	 "degree.ad"	 "output"

vpadIndex <- 3 
print(paste("Looking at ",names(vpad)[vpadIndex],"index",vpadIndex))

cMin <- min(vpad$Comm)
cMax <- max(vpad$Comm)
cTotal <- cMax-cMin+1
print(paste("Communities min,max,number=",instMin,instMax))


# Produces a list of histograms and statistics for given community vector and value vector
doStats <- function(commVector,valueVector){
    cMin=min(commVector)
    cMax=max(commVector)
    vMin=min(valueVector)
    vMax=max(valueVector)
    sumSq = seq(0,length=length(cMin:cMax))
    entropy = seq(0,length=length(cMin:cMax))
    hlist <- list()
    for (cNumber in cMin:cMax) {
     hhh <- hist(valueVector[commVector==cNumber],breaks=seq(vmin-0.5,vmax+0.5), plot=FALSE)
     hlist[[length(hlist)+1]] <- hhh
     sumSq[cNumber+cMin+1] <- (hhh$density)^2
     entropy[cNumber+cMin+1] <-ifelse(hhh$den>0,(hhh$den)*log(hhh$den),0)
    }
    list(hist=hlist,sumSq=sumSq,entropy=entropy)
}
# End of doStats function

# Set up stats for Institutes
instStats<-doStats(vpad$Comm, vpad[[vpadIndex]])


c1<-1
#ddd <- rbind(cDiscHistList[[c1:c1+3]]$counts,cDiscHistList[[2]]$counts,cDiscHistList[[3]]$counts,cDiscHistList[[4]]$counts)
ddd <- rbind(cDiscHistList[[c1:c1+3]]$counts)
if (OSWindows) windows() else quartz()
barplot(ddd,beside=TRUE,names.arg=c(cMin:cMax),las=2, col=c("black", "red", "blue", "green"))



