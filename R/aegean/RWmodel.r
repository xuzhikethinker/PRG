# alpha negative selects exponential potential rather than ariadne one.
# initialConditionsMode=1 Random, =2 Use site size, =3 Forced
# NEED source ("RWcalc.r")
# NEED source ("RWflowcalc.r")

RWmodel <- function( inputDir, outputDir, rootName, numberRows=39, beta=1.05, DScale=100, alpha=4.0, gamma=1.0, countMax=100, selfConsistentOn=FALSE, initialConditionsMode=2, printOn=TRUE) {
#print(paste("Using distance measure number ",distanceMeasure," :- ",distanceMeasureName), quote=FALSE)
#aegean39S1L3a_v1_3e-1.0j0.0m0.5k1.0l4.5b1.2D100.0MC_r4_invewDistS1000C500L3000outputBRM


siteFileName<-paste(inputDir,rootName,"_sitesdata.dat",sep="");
distanceFileName<-paste(inputDir,rootName,"_distancematrix.dat",sep="")
print(paste("---     Site file:",siteFileName),quote=F);
print(paste("--- Distance file:",distanceFileName), quote=FALSE)

sitedf <- read.table(siteFileName, header=TRUE, sep="\t", fill=TRUE);
#names(sitedf)
#       

if (selfConsistentOn) {
 selfConsistentString="SC"
 print("Self Consistent Solution", quote=FALSE)
}
else {
 selfConsistentString="FO"
 print("Fixed Output Solution", quote=FALSE)
}


potTypeName="VAR"
dm <- scan(distanceFileName,0);
distMatrix <- matrix(dm,nrow=numberRows)
#print(paste("Distance Matrix"), quote=FALSE)
#print(distMatrix)
xMatrix <- distMatrix/DScale;
if (alpha>0) {
 potlMatrix <- (1+xMatrix^alpha)^(-gamma)
 potTypeName="VAR"
 print("ariadne potential used", quote=FALSE)
} else {
 potlMatrix <- exp(-xMatrix)
 potTypeName="VRW"
 print("exponential RW potential used", quote=FALSE)
}
for (site in (1:numberRows)) potlMatrix[site,site] <-0
#print(paste("Potential Matrix"), quote=FALSE)
#print(potlMatrix)

wVecInitial <- sitedf$Size
if (initialConditionsMode==1){
 print(paste("Random Initial W"), quote=FALSE)
 initialConditionsType="initR"
}
if (initialConditionsMode==2){
 print(paste("Initial W equal to fixed site size"), quote=FALSE)
 initialConditionsType="initS"
}

if (initialConditionsMode==3){
 wVecInitial <- rep(0, times=numberRows)
 wVecInitial[5]=numberRows/2
 wVecInitial[10]=numberRows/2
 print(paste("Forced Initial W"), quote=FALSE)
 print(wVecInitial)
 initialConditionsType="initF"
}

if (initialConditionsMode==4){
 print(paste("Initial W equal to fixed site size plus 0.1% random Initial W"), quote=FALSE)
 initialConditionsType="initSR"
}

rwcalcdf <-RWcalc( sitedf$Size, wVecInitial, potlMatrix, beta, countMax, selfConsistentOn, initialConditionsMode, printOn) 
wVec <- rwcalcdf$wvector
wVecInitial <- rwcalcdf$wvectorinitial

flowList <- RWflowcalc(sitedf$Size, wVec, potlMatrix, beta, selfConsistentOn)
flowMatrix <- flowList$flowMatrix
inFlowVec <- flowList$inFlowVec
outFlowVec <- flowList$outFlowVec

rankW <- numberRows+1-rank(inFlowVec,  ties.method="first")
totalW <- sum(inFlowVec);

Svec <- inFlowVec*log(inFlowVec/totalW)/totalW
S <- -sum(Svec[is.finite(Svec)])
hubnumber <- length(inFlowVec[inFlowVec>mean(inFlowVec)])
print(paste("var=",var(inFlowVec),"  entropy=",S,"  hubs (>av)=",hubnumber),quote=FALSE)

inFlowPercentage <- round(1000*inFlowVec/totalW)/10

#terminal=terminal, maxOutput, maxOutputTarget, treeMatrix
treecalcList <- RWtreecalc(numberRows, flowMatrix)
print(paste("Number of terminals =",sum(treecalcList$terminal)),quote=FALSE)
print(paste(treecalcList$maxOutput,treecalcList$maxOutputTarget,treecalcList$terminal),quote=FALSE)

#printdf <- data.frame( name=sitedf$Name, rankW = rankW, initialWVec = wVecInitial, outFlowVector=outFlowVec, size=sitedf$Size, inFlowVector = inFlowVec, inWeightVector = wVec, inFlowPercentage = inFlowPercentage, terminal=treecalcList$treecalcList, maxOutput=treecalcList$maxOutput, maxOutputTarget=treecalcList$maxOutputTarget)
printdf <- data.frame( name=sitedf$Name, rankW = rankW, initialWVec = wVecInitial, outFlowVector=outFlowVec, size=sitedf$Size, inFlowVector = inFlowVec, inWeightVector = wVec, inFlowPercentage = inFlowPercentage, terminal=treecalcList$terminal, maxOutput=treecalcList$maxOutput, maxOutputTarget=treecalcList$maxOutputTarget)
#printdf <- data.frame( name=sitedf$Name, rankW = rankW)
print(printdf,quote=FALSE)

resultsdf <- data.frame( name=sitedf$Name, rankW = rankW, initialWVec =wVecInitial, outFlowVector=outFlowVec, size=sitedf$Size, inFlowVector = inFlowVec, inWeightVector = wVec, terminal=treecalcList$terminal, maxOutput=treecalcList$maxOutput, maxOutputTarget=treecalcList$maxOutputTarget, flowMatrix=flowMatrix )
resultsOutputFileName <- paste(outputDir,rootName,"_RW_beta",beta,"D",DScale,"_",potTypeName,"_",selfConsistentString,"_",initialConditionsType,"_Flowoutput.dat",sep="")
print(paste("Output results to file",resultsOutputFileName),quote=FALSE)
write.table(resultsdf, file = resultsOutputFileName , append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)
siteresultsdf <- data.frame( name=sitedf$Name, size=sitedf$Size, rankW = rankW, initialWVec =wVecInitial, outFlowVector=outFlowVec, inFlowVector = inFlowVec, inWeightVector = wVec, terminal=treecalcList$terminal, maxOutput=treecalcList$maxOutput, maxOutputTarget=treecalcList$maxOutputTarget)
list( sitedf=sitedf, siteresultsdf=siteresultsdf, flowMatrix=flowMatrix )
}
