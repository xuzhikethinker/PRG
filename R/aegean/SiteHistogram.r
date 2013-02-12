windowsPlotOn=TRUE
pdfPlotOn=TRUE
epsPlotOn=TRUE

#rootName <-"SiteHistTest"
#rootNameList <- c("aegean39S1L3a_v1_3e-1.0m2.4j-2.0k1.0l3.0b1.2s120.0MC_r0_SiteHist",
#                  "aegean39S1L3a_v1_3e-1.0m1.8j-2.0k1.0l3.0b1.2s120.0MC_r0_SiteHist",
#                  "aegean34S1L3_v1_3e-1.0m1.5j0.0k1.0l4.0b1.2s100.0MC_r0_SiteHist",
#                  "aegean34S1L3_v1_3e-1.0m0.1j-2.0k1.0l3.0b1.2s100.0MC_r0_SiteHist")
                  
rootNameList <- c("aegean39S1L3a_v1_3e-1.0j-2.0m0.3k1.0l3.0b1.2D100.0MC_r0_SiteHist")

for (rootName in rootNameList){

inputFileName <-paste(rootName,".dat",sep="")
print(paste("Site Histogram from ",inputFileName), quote=FALSE)

#df <- read.table("SiteHist.dat", header=TRUE, sep="\t", fill=TRUE);
df <- read.table(inputFileName, header=TRUE, sep="\t", fill=TRUE);

wmax <- max(df$Weight)
rmax <- max(df$Ranking)
imax <- max(df$Influence)

colourList <- c("red", "blue", "green")
quantityList <- c("weight","rank","influence","impact")

# Ordering
orderQuantityNumber <-1
orderFileName<-quantityList[orderQuantityNumber]
print(paste("Ordering using",orderFileName), quote=FALSE)
indexList <- c(1:length(df$Weight))

if (orderFileName=="weight"){
orderList <- order(df$Weight,indexList)
}
if (orderFileName=="rank"){
orderList <- order(df$Ranking,indexList)
}
if (orderFileName=="influence"){
orderList <- order(df$Influence,indexList)
}
if (orderFileName=="impact"){
orderList <- order(df$Ranking/df$Weight,indexList)
}


dataArray <- rbind(df$Weight[orderList]/wmax,df$Ranking[orderList]/rmax,df$Influence[orderList]/imax)

if (windowsPlotOn){
windows()
barplot(dataArray, beside=TRUE, names.arg=df$ShortName[orderList], horiz=TRUE, las=1, col=colourList)
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
