# Creates PPA matrix where each entry is the rank of entry in that row.
source("readData.r")

PPAmodel <- function( inputDir, outputDir, rootName, numberRows=39,  printOn=TRUE) {

#siteFileName<-paste(inputDir,rootName,"_sitesdata.dat",sep="");
#distanceFileName<-paste(inputDir,rootName,"_distancematrix.dat",sep="")
#print(paste("---     Site file:",siteFileName),quote=F);
#print(paste("--- Distance file:",distanceFileName), quote=FALSE)

#sitedf <- read.table(siteFileName, header=TRUE, sep="\t", fill=TRUE);
#names(sitedf)
#       



#dm <- scan(distanceFileName,0);
dataList <-readData(inputDir, rootName)
distMatrix <- matrix(dataList$distVector,nrow=numberRows)
for (s in (1:numberRows)) distMatrix[s,s]=NA
# PPA matrix has the rank of the edge
PPAMatrix <- array(0,dim=c(0,numberRows))

# ties.method = "random" a sensible option
# ties.method = "first" a sensible option
for (s in (1:numberRows)) PPAMatrix<-rbind(PPAMatrix,rank(distMatrix[s,], na.last = TRUE, ties.method = "first" ))

resultsdf <- data.frame( name=dataList$sitedf$Name, PPAMatrix=PPAMatrix)
resultsOutputFileName <- paste(outputDir,rootName,"_PPA_fullmatrix.dat",sep="")
print(paste("Output results to file",resultsOutputFileName),quote=FALSE)
write.table(resultsdf, file = resultsOutputFileName , append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)
list( sitedf=dataList$sitedf, PPAMatrix=PPAMatrix, distMatrix=distMatrix)
}
