# Reads ELS Elsevier bipartite network file
# C:\DATA\Elsevier\input\ebrp_03_set_01_documents_2002_Phy_PTt_P1inputELS.dat
# 

readELSFile <- function(inputDir="/DATA/Elsevier/input/",fullName="ebrp_03_set_01_documents_2002_Phy_PTt_P1inputELS.dat", headerOn=TRUE, sepString="\t", weightOn=TRUE) {

if (weightOn) print(paste("--- Reading two string columns then real weight values (source, target, weight) from ",fullName), quote=FALSE)
else print(paste("--- Reading two string columns (source, target) from ",fullName), quote=FALSE)

edgedf <- read.table(paste(inputDir,fullName,sep=""), header=headerOn, sep=sepString);
}

readVerticesFile <- function(inputDir="/DATA/Elsevier/input/",fullName="ebrp_03_set_01_documents_2002_Phy_PTt_P1_GCCvertices.dat", headerOn=TRUE, sepString="\t", weightOn=TRUE) {

vertexdf <- read.table(paste(inputDir,fullName,sep=""), header=headerOn, sep=sepString);
}
