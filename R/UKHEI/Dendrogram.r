#rootName="aegean39";
#typeName="S1L1";
#produces dendrogram data based n adjacency matrix 
# <rootName>distance.dat is table of distances, no column or row names
# <rootName>names.dat is column of names with header names


Dendrogram <- function( rootName, typeName, methodNumber,numberRows=39) {
namesFileName<-paste(rootName,"names.dat",sep="");
distanceFileName<-paste(rootName,typeName,"distance.dat",sep="")
distanceFileName<-paste(rootName,typeName,"distance.dat",sep="")
nameList <- scan(namesFileName, what="", skip=1);
distanceVector <- scan(distanceFileName,0);
distanceMatrix <- matrix(distanceVector,nrow=numberRows);
distanceDist <- as.dist(distanceMatrix)
hclustMethod <- c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
methodName=hclustMethod[methodNumber]
distanceHclust <- hclust(distanceDist, method=methodName) #, members=NULL
# Can't get the xlabel and subtitle to disappear with NULL
#theTitle = title(main = paste(rootName,typeName," ",methodName,sep=""),   sub="", xlab="", ylab="Distance"  )
#give new plotting window
windows()
plclust(distanceHclust, labels=nameList, frame.plot=FALSE, hang=-10, main = paste(rootName,typeName," ",methodName,sep=""),   sub="", xlab="", ylab="Distance"   )
# now plot eps file
epsFileName<-paste(rootName,typeName,methodName,".eps",sep="");
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=7.5, width=11, pointsize=8)
plclust(distanceHclust, labels=nameList, frame.plot=FALSE, hang=-10, main = paste(rootName,typeName," ",methodName,sep=""),   sub="", xlab="", ylab="Distance"  )
dev.off()
#plots pdf file
pdfFileName<-paste(rootName,typeName,methodName,".pdf",sep="");
pdf(pdfFileName, onefile=FALSE, height=7.5, width=11, pointsize=8)
plclust(distanceHclust, labels=nameList, frame.plot=FALSE, hang=-10, main = paste(rootName,typeName," ",methodName,sep=""),   sub="", xlab="", ylab="Distance"   )
dev.off()
}
