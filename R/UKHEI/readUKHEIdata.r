# Following defines aegeanDendrogram <- function( rootName, typeName, methodNumber, numberRows)
#where methodNumber gives entry from c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
source("Dendrogram.r")

rootName="UKHEI";

methodNumber=1;
numberRows=124;

typeName="";
Dendrogram ( rootName, typeName, methodNumber, numberRows)


methodNumber=2;

Dendrogram ( rootName, typeName, methodNumber, numberRows)


methodNumber=4;

Dendrogram ( rootName, typeName, methodNumber, numberRows)


