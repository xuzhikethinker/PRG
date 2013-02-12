windowsPlotOn=TRUE
pdfPlotOn=FALSE
epsPlotOn=FALSE

#rootName <-"SiteHistTest"
rootNameList <- c("karateTSE_CPt2c3l500VCr", "karateTSE_CPt4c3l500VCr")
for (fff in 1:length(rootNameList)){
rootName <- rootNameList[fff]

inputFileName <-paste(rootName,".dat",sep="")
print(paste("Site Histogram from ",inputFileName), quote=FALSE)

df <- read.table(inputFileName, header=TRUE, sep="\t", fill=TRUE);

numberCommunities <- length(df)-1

vertexNames <- df[[1]]
numberVertices <- length(vertexNames)


colourList <- c("red", "blue", "green", "magenta", "brown", "black");

# Ordering - set only on first pass
if (fff==1){ 
orderValues <-df[[2]]
for (ccc in 3:length(df)) orderValues <- orderValues*2+df[[ccc]]
indexList <- c(1:numberVertices)
orderList <- order(orderValues,indexList, decreasing = TRUE)
}

dataArray <- df[[2]][orderList]
for (ccc in 3:length(df)) dataArray <- rbind(dataArray,df[[ccc]][orderList])

if (windowsPlotOn){
windows()
barplot(dataArray, beside=FALSE, names.arg=vertexNames[orderList], horiz=FALSE, las=1, col=colourList)
}

# PDF plot
if (pdfPlotOn){
pdfFileName<-paste(rootName,orderFileName,".pdf",sep="_")
print(paste("pdf plotting",pdfFileName), quote=FALSE)
pdf(pdfFileName, onefile=FALSE, height=8, width=6, pointsize=10)
barplot(dataArray, beside=TRUE, names.arg=df$ShortName[orderList], horiz=TRUE, las=1, col=colourList)
dev.off(which = dev.cur())
}

# EPS plot
if (epsPlotOn){
epsFileName<-paste(rootName,orderFileName,".eps",sep="_")
print(paste("eps plotting",epsFileName), quote=FALSE)
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=8, width=6, pointsize=12)
barplot(dataArray, beside=TRUE, names.arg=df$ShortName[orderList], horiz=TRUE, las=1, col=colourList)
dev.off(which = dev.cur())
}

}
