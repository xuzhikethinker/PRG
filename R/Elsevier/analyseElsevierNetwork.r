# Analyses ELS Elsevier bipartite network file
# C:\DATA\Elsevier\input\ebrp_03_set_01_documents_2002_Phy_PTt_P1inputELS.dat
# 

source("readElsevierNetwork.r")
source("logbincount.r")

# readVerticesFile <- function(inputDir="/DATA/Elsevier/input/",fullName="ebrp_03_set_01_documents_2002_Phy_PTt_P1_GCCvertices.dat", headerOn=TRUE, sepString="\t", weightOn=TRUE) {

termvertexdf <- readVerticesFile(inputDir="/DATA/Elsevier/",fullName="ebrp_03_set_01_documents_2002_Phy_PTt_P1verticesTerms.txt");
papervertexdf <- readVerticesFile(inputDir="/DATA/Elsevier/",fullName="ebrp_03_set_01_documents_2002_Phy_PTt_P1verticesPapers.txt");

numberBins=25
termStrengthHist <- logbincount(termvertexdf$Strength,numberBins)
nnn=length(termStrengthHist$breaks)
tsx <-sqrt(termStrengthHist$breaks[1:nnn-1]*termStrengthHist$breaks[2:nnn])
tsy <-termStrengthHist$counts
plot(tsx,tsy,log="xy")
