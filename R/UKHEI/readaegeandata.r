# Following defines aegeanDendrogram <- function( rootName, typeName, methodNumber)
#where methodNumber gives entry from c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
source("aegeanDistanceDendrogram.r")

rootName="aegean39";

methodNumber=1;

typeName="S1L1";
aegeanDendrogram ( rootName, typeName, methodNumber)

typeName="S1L2";
aegeanDendrogram ( rootName, typeName, methodNumber)

typeName="S1L3";
aegeanDendrogram ( rootName, typeName, methodNumber)

methodNumber=2;

typeName="S1L1";
aegeanDendrogram ( rootName, typeName, methodNumber)

typeName="S1L2";
aegeanDendrogram ( rootName, typeName, methodNumber)

typeName="S1L3";
aegeanDendrogram ( rootName, typeName, methodNumber)

methodNumber=4;

typeName="S1L1";
aegeanDendrogram ( rootName, typeName, methodNumber)

typeName="S1L2";
aegeanDendrogram ( rootName, typeName, methodNumber)

typeName="S1L3";
aegeanDendrogram ( rootName, typeName, methodNumber)


