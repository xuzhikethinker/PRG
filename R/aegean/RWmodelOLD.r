# alpha negative selects exponential potential rather than ariadne one.
# initialConditionsMode=1 Random, =2 Use site size, =3 Forced

RWmodel <- function( inputDir, outputDir, rootName, numberRows=39, beta=1.05, DScale=100, alpha=4.0, gamma=1.0, selfConsistentOn=FALSE, initialConditionsMode=2) {
#print(paste("Using distance measure number ",distanceMeasure," :- ",distanceMeasureName), quote=FALSE)
#aegean39S1L3a_v1_3e-1.0j0.0m0.5k1.0l4.5b1.2D100.0MC_r4_invewDistS1000C500L3000outputBRM

if (selfConsistentOn) {
 selfConsistentString="SC"
 print("Self Consistent Solution", quote=FALSE)
}
else {
 selfConsistentString="FO"
 print("Fixed Output Solution", quote=FALSE)
}


siteFileName<-paste(inputDir,rootName,"_sitesdata.dat",sep="");
distanceFileName<-paste(inputDir,rootName,"_distancematrix.dat",sep="")

sitedf <- read.table(siteFileName, header=TRUE, sep="\t", fill=TRUE);
#names(sitedf)
#       

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



if (initialConditionsMode==1){
 wVec <- runif(numberRows,0,1)
 print(paste("Random Initial W"), quote=FALSE)
 initialConditionsType="initR"
}

if (initialConditionsMode==2){
 wVec <- sitedf$Size
 print(paste("Initial W equal to fixed site size"), quote=FALSE)
 initialConditionsType="initS"
}
if (initialConditionsMode==3){
 wVec <- rep(0, times=numberRows)
 wVec[5]=numberRows/2
 wVec[10]=numberRows/2
 print(paste("Forced Initial W"), quote=FALSE)
 print(wVec)
 initialConditionsType="initF"
}
vVec <- 1/sitedf$Size

wVecInitial<- wVec

countMax=100
for (count in 1:countMax) {
   #print(paste("---",count), quote=FALSE)
   wbetaVec <- (wVec)^beta
   tempVec <- potlMatrix %*% wbetaVec
   newvVec <- 1/tempVec

   newvVecL<-sum(newvVec)
   vc <- sum(abs(newvVec-vVec))/newvVecL
   vVec <-newvVec

   #print(vVec)
   #tttVec <- (sitedf$Size*vVec)
   #print(tttVec)
   if (selfConsistentOn) newwVec <-  (t(potlMatrix) %*% (wVec*vVec) )*wbetaVec
   else newwVec <-  (t(potlMatrix) %*% (sitedf$Size*vVec) )*wbetaVec

   newwVecL<-sum(newwVec)
   wc <- sum(abs(newwVec-wVec))/newwVecL
   wVec <-newwVec

   #print(paste(vc,wc),quote=FALSE)
   #print(vVec,quote=FALSE)
   #print(wVec,quote=FALSE)
}

print(paste("Convergence Factors"), quote=FALSE)
print(paste(vc,wc),quote=FALSE)

#print(paste("Site Weight (Out)"), quote=FALSE)
#print(sitedf$Size*vVec,quote=FALSE)

#print(paste("Site Attractiveness (In Weight)"), quote=FALSE)
#print(wVec,quote=FALSE)

wbetaVec <- ((wVec)^beta)
dim(wbetaVec) <- numberRows

if (selfConsistentOn) outWeightVec <- (wVec*vVec) 
else outWeightVec <- (sitedf$Size*vVec)

dim(outWeightVec) <- numberRows

tttMat <- outWeightVec %o% wbetaVec 
#print(tttMat)
flowMatrix <- ( tttMat ) * potlMatrix
#print(paste("Flow Matrix"), quote=FALSE)
#print(flowMatrix,quote=FALSE)
oneList <- rep(1,times=numberRows)

outFlowVec <- flowMatrix %*% oneList
inFlowVec <- t(oneList %*% flowMatrix)

#rankW <- order(inFlowVec, c(1:numberRows), decreasing = TRUE)
rankW <- numberRows+1-rank(inFlowVec,  ties.method="first")
totalW <- sum(inFlowVec);
F2 <- sum(inFlowVec*inFlowVec)/(totalW*totalW)
#Svec <- inFlowVec*log(inFlowVec/totalW)/totalW
#S <- sum(sVec[isNAN))
#print(paste("F2=",F2,"  entropy=",S),quote=FALSE)
print(paste("F2=",F2),quote=FALSE)

inFlowPercentage <- round(1000*inFlowVec/totalW)/10


printdf <- data.frame( name=sitedf$Name, rankW = rankW, initialWVec = wVecInitial, outFlowVector=outFlowVec, size=sitedf$Size, inFlowVector = inFlowVec, inWeightVector = wVec, inFlowPercentage = inFlowPercentage)
print(printdf,quote=FALSE)

resultsdf <- data.frame( name=sitedf$Name, rankW = rankW, initialWVec =wVecInitial, outFlowVector=outFlowVec, size=sitedf$Size, inFlowVector = inFlowVec, inWeightVector = wVec, flowMatrix=flowMatrix )
resultsOutputFileName <- paste(outputDir,rootName,"beta",beta,"D",DScale,"_",potTypeName,"_",selfConsistentString,"_",initialConditionsType,"_Flowoutput.dat",sep="")
print(paste("Output results to file",resultsOutputFileName),quote=FALSE)
write.table(resultsdf, file = resultsOutputFileName , append = FALSE, quote = FALSE, sep = "\t", row.names = FALSE, col.names = TRUE)
resultsdf
}
