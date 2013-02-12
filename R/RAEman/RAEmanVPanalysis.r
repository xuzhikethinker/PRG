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

nAuthors <- length(vpad$name)

cMin <- min(vpad$Comm)
cMax <- max(vpad$Comm)
cTotal <- cMax-cMin+1
print(paste("Communities min,max,number=",instMin,instMax))
dMin <- min(vpad$disc)
dMax <- max(vpad$disc)
instMin <- min(vpad$inst)
instMax <- max(vpad$inst)
instUKMax <- max(vpad$inst[!is.na(vpad$lat)])
print(paste("Institutions min,max and last UK=",instMin,instMax,instUKMax))

#cInstMatrix <- matrix(0, nrow = cMax-cMin+1, ncol=instMax-instMin+1)
#cInstUKMatrix <- matrix(0, nrow = cMax-cMin+1, ncol=instUKMax)
# Community - Institute Count matrix
#instNameList <- paste("i",instMin:instMax,sep="")
cInstHistList <- list()
#indexList <- c(3,12)
#indexNameList <- names(vpad)[indexList]
#print("Making statistics for",indexNameList)

cSumSq = c()
cEntropy = c()
for (cNumber in cMin:cMax) {
 hhh <- hist(vpad$inst[vpad$Comm==cNumber],breaks=seq(instMin-0.5,instMax+0.5,1), plot=FALSE)
 cInstHistList[[length(cInstHistList)+1]] <- hhh
 sumsq <- (hhh$density)^2
 sss <-ifelse(hhh$den>0,(hhh$den)*log(hhh$den),0)
 cSumSq[cNumber+cMin+1,] <- sumSq
 cEntropy[cNumber+cMin+1,] <-sss
}


# Community - Author projection matrix
#instNameList <- paste("i",instMin:instMax,sep="")
#cInstMatrix <- matrix(0, nrow = cMax-cMin+1, ncol=nAuthors)
#for (aaa in 1:nAuthors) {
# ccc <- vpad$Comm[aaa]
# cInstMatrix[ccc,aaa] =1  
#} 




# Number of Institutes in each Community projection matrix
#print(paste("Institutions min,max=",instMin,instMax))
#instNameList <- paste("i",instMin:instMax,sep="")
#cInstMatrix <- matrix(0, nrow = cMax-cMin+1, ncol=length(instNameList), dimnames = list(paste("c",cMin:cMax.sep=""), instNameList))
#for (aaa in cMin:cMax) {
# cDiscList[[length(cDiscList)+1]] <-vpad$inst[vpad$Comm==cNumber]
#}



#cNumber <- 0
#cDiscList <- list()
#colNameList <- c("1","2","3")
#cStatsMatrix <- matrix(0, nrow = cMax-cMin+1, ncol=length(colNameList), dimnames = list(paste("c",cMin:cMax.sep=""), colNameList))
#for (cNumber in cMin:cMax) {
 #cDiscList[[length(cDiscList)+1]] <-vpad$disc[vpad$Comm==cNumber]
# cDiscList[[length(cDiscList)+1]] <-vpad$disc[vpad$Comm==cNumber]
#} 
#iii <- iii+1
#cNumber <- cNumber+1
#cDiscList[[iii]] <-vpad$disc[vpad$Comm==cNumber]
#iii <- iii+1
#cNumber <- cNumber+1
#cDiscList[[iii]] <-vpad$disc[vpad$Comm==cNumber]
#iii <- iii+1
#cNumber <- cNumber+1

cDiscHistList <- list()
for (cNumber in cMin:cMax) cDiscHistList[[length(cDiscHistList)+1]] <- hist(vpad$disc[vpad$Comm==cNumber],breaks=0.5:24.5, plot=FALSE)
#h0 <- hist(vpad$disc[vpad$Comm==0],breaks=0.5:24.5, plot=FALSE)
#h1 <- hist(vpad$disc[vpad$Comm==1],breaks=0.5:24.5, plot=FALSE)
#h2 <- hist(vpad$disc[vpad$Comm==2],breaks=0.5:24.5, plot=FALSE)
#h3 <- hist(vpad$disc[vpad$Comm==3],breaks=0.5:24.5, plot=FALSE)

c1<-1
c2<-2
ddd <- rbind(cDiscHistList[[1]]$counts,cDiscHistList[[2]]$counts,cDiscHistList[[3]]$counts,cDiscHistList[[4]]$counts)
barplot(ddd,beside=TRUE,names.arg=c(1:24),las=2, col=c("black", "red", "blue", "green"))

if (OSWindows) windows() else quartz()

h0 <- hist(vpad$inst[vpad$Comm==0],breaks=c(-0.5:97.5,10000), plot=FALSE)
h1 <- hist(vpad$inst[vpad$Comm==1],breaks=c(-0.5:97.5,10000), plot=FALSE)
h2 <- hist(vpad$inst[vpad$Comm==2],breaks=c(-0.5:97.5,10000), plot=FALSE)
h3 <- hist(vpad$inst[vpad$Comm==3],breaks=c(-0.5:97.5,10000), plot=FALSE)
ddd <- rbind(h0$counts,h1$counts,h2$counts,h3$counts)
barplot(ddd,beside=TRUE,names.arg=c(0:98),las=2, col=c("black", "red", "blue", "green"),ylim=c(0,20))
