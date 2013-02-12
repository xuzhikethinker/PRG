#produces dendrogram data based on matrix treated as a similarity matrix
# plotName used in plot titles
# adjacencyMatrix matrix of values
# nameList vector of names must have same dimension of number of rows of adjacencyMatrix
# methodNumber selects method
# OSWindows true for windows plot

# Compares rows of the matrix

MatrixDendrogram <- function(plotName, adjacencyMatrix, nameList, methodNumber, OSWindows) {

#This is a distance matrix using the distance between rows of matrix treated as vectors
adjacencyDist <- as.dist(adjacencyMatrix)

hclustMethod <- c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
methodName=hclustMethod[methodNumber]
adjacencyHclust <- hclust(adjacencyDist, method=methodName) #, members=NULL

# Can't get the xlabel and subtitle to disappear with NULL
#theTitle = title(main = paste(rootName,typeName," ",methodName,sep=""),   sub="", xlab="", ylab="adjacency"  )
#give new plotting window
if (OSWindows) windows() else quartz()
plclust(adjacencyHclust, labels=nameList, frame.plot=FALSE, hang=-10, main = paste(plotName," ",methodName,sep=""),   sub="", xlab="", ylab="adjacency"   )

# now plot eps file
epsFileName<-paste(plotName,methodName,".eps",sep="");
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=7.5, width=11, pointsize=8)
plclust(adjacencyHclust, labels=nameList, frame.plot=FALSE, hang=-10, main = paste(plotName," ",methodName,sep=""),   sub="", xlab="", ylab="adjacency"  )
dev.off()

#plots pdf file
pdfFileName<-paste(plotName,methodName,".pdf",sep="");
pdf(pdfFileName, onefile=FALSE, height=7.5, width=11, pointsize=8)
plclust(adjacencyHclust, labels=nameList, frame.plot=FALSE, hang=-10, main = paste(plotName," ",methodName,sep=""),   sub="", xlab="", ylab="adjacency"   )
dev.off()

}
