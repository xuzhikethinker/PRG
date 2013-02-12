# Following defines aegeanDendrogram <- function( rootName, typeName, methodNumber)
#where methodNumber gives entry from c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
source("aegeanDistanceDendrogram.r")

rootName="RAEman";

methodNumber=1;

typeName="S1L1";
Dendrogram ( rootName, typeName, methodNumber)


methodNumber=2;

Dendrogram ( rootName, typeName, methodNumber)


methodNumber=4;

Dendrogram ( rootName, typeName, methodNumber)


