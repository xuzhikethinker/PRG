# Following defines aegeanDendrogram <- function( rootName, typeName, methodNumber)
#where methodNumber gives entry from c("single", "complete", "ward", "average", "mcquitty", "median", "centroid");
source("aegeanDistanceDendrogram.r")

rootName="aegean39";

methodNumber=1;

numberRows=39, mainTitleOn=TRUE

typeName="S1L1";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)

typeName="S1L2";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)

typeName="S1L3";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)

methodNumber=2;

typeName="S1L1";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)

typeName="S1L2";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)

typeName="S1L3";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)

methodNumber=4;

typeName="S1L1";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)

typeName="S1L2";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)

typeName="S1L3";
DendrogramFromFile ( rootName, typeName, methodNumber, numberRows, mainTitleOn)


