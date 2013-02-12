source("Dendrogram.r")

edgeWeightDendrogram <- function( inputDir, rootName, typeName, methodNumber,numberRows=39, distanceMeasure=1, mainTitleOn=TRUE) {
distanceMeasureNameList <- c("lnew","invew")
distanceMeasureName=distanceMeasureNameList[distanceMeasure]
print(paste("Using distance measure number ",distanceMeasure," :- ",distanceMeasureName), quote=FALSE)

#aegean39S1L3a_v1_3e-1.0j0.0m0.5k1.0l4.5b1.2D100.0MC_r4_invewDistS1000C500L3000outputBRM

siteFileName<-paste(inputDir,rootName,typeName,"sitesdata.dat",sep="");
edgeWeightFileName<-paste(inputDir,rootName,typeName,"edgeweightinputAdjMat.dat",sep="")

sitedf <- read.table(siteFileName, header=TRUE, sep="\t", fill=TRUE);
#names(sitedf)
# [1] "ShortName"           "Name"                "XPos"               
# [4] "YPos"                "ZPos"                "Size"               
# [7] "Value"               "Weight"              "WeightRank"         
#[10] "Ranking"             "RankingRank"         "Influence"          
#[13] "InfluenceInt"        "InfluenceRank"       "DisplaySize"        
#[16] "Strength"            "StrengthIn"          "StrengthOut"        
#[19] "Number"              "Ranking.Weight"      "Ranking.Weight.rank"
#[22] "CultureMax"          "CultureSite"         "StrengthInSquared"  
#[25] "StrengthOutSquared"  "Latitude"            "Longitude"          
#[28] "Region"             


ewVector <- scan(edgeWeightFileName,0);
ewMatrix <- matrix(ewVector,nrow=numberRows);
ewMax <- max(ewMatrix)
ewMin <- min(ewMatrix)

if (distanceMeasure==1){
maxValue<- ewMax*1.1
minValue<- ewMin-maxValue/10
dm <- -log((ewMatrix-minValue)/(maxValue-minValue))
}

if (distanceMeasure==2){
maxValue<- ewMax
minValue<- ewMin-maxValue/10
dm <- (maxValue-minValue)/(ewMatrix-minValue)
}

distanceMatrix <- dm + t(dm)

#print(distanceMatrix[1:5,1:5])

#critical angles
#ZZZ <- array(sitedf$Weight, c(numberRows,numberRows))
#criticalAngles <- atan(distanceMatrix/(ZZZ-t(ZZZ)))*180/pi
# Need to be abolute for true critical angle, this is directed
#A true Xtent removes all ca value above a given value.

nameList <- paste(sitedf$Name,sitedf$WeightRank)
#hMin <- (trunc(min(abs(criticalAngles))/10)-1)*10
DendrogramCalcPlot( rootName, paste(typeName,distanceMeasureName,sep="_"), methodNumber, nameList, distanceMatrix, mainTitleOn) 
}
