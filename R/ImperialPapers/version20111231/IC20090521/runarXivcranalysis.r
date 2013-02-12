source("arXivcranalysis.r")

rootName="arXiv090902"
cNotCoverR=FALSE
normalise=TRUE
facultyNotDept=TRUE
minYear=2003
maxYear=2005
numberParameters=3
parameterAnalysisOn=FALSE
plotsOn =TRUE
OSWindows=TRUE
screenOn=FALSE
pdfPlotOn=TRUE
epsPlotOn=FALSE

#arXivcranalysis <- function(rootName="arXivtest", 
#                                    cNotCoverR=TRUE, normalise=TRUE, 
#                                    minYearInput=1900, maxYearInput=2009, numberParameters=3, 
#                                    parameterAnalysisOn=TRUE, plotsOn =TRUE,
#                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=FALSE, epsPlotOn=TRUE){

for (yyy in minYear:maxYear){
lst <-arXivcranalysis(rootName, cNotCoverR, normalise, 
                       yyy, yyy, numberParameters, 
                       parameterAnalysisOn, plotsOn, 
                       OSWindows, screenOn, pdfPlotOn, epsPlotOn) 

}
