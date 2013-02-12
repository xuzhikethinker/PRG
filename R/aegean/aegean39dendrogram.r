# Produces dendrograms based on distances for aegean sites
source("Dendrogram.r")

rootName="aegean39";
typeName="S1L3";
numberRows=39
mainTitleOn=FALSE

# methods=c("single", "complete", "ward", "average", "mcquitty", "median", "centroid")
methodNumber=1

DendrogramFromFile ( rootName, typeName, methodNumber,numberRows, mainTitleOn) 
