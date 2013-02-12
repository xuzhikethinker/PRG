# produces XTENT models 
inputDir="input/"
rootName="aegean39S1L3a";
typeName="_v1_3e-1.0j0.0m0.5k1.0l4.5b1.2D100.0MC_r4_";
methodNumber<-1
numberRows=39
distanceMeasure<-1
mainTitleOn=TRUE

source("edgeWeightDendrogram.r")

methodNumber<-1
distanceMeasure<-1
edgeWeightDendrogram(inputDir, rootName, typeName, methodNumber, numberRows=39, distanceMeasure, mainTitleOn=TRUE)

distanceMeasure<-2
edgeWeightDendrogram(inputDir, rootName, typeName, methodNumber, numberRows=39, distanceMeasure, mainTitleOn=TRUE)


methodNumber<-2
distanceMeasure<-1
edgeWeightDendrogram(inputDir, rootName, typeName, methodNumber, numberRows=39, distanceMeasure, mainTitleOn=TRUE)

distanceMeasure<-2
edgeWeightDendrogram(inputDir, rootName, typeName, methodNumber, numberRows=39, distanceMeasure, mainTitleOn=TRUE)


methodNumber<-4
distanceMeasure<-1
edgeWeightDendrogram(inputDir, rootName, typeName, methodNumber, numberRows=39, distanceMeasure, mainTitleOn=TRUE)

distanceMeasure<-2
edgeWeightDendrogram(inputDir, rootName, typeName, methodNumber, numberRows=39, distanceMeasure, mainTitleOn=TRUE)



