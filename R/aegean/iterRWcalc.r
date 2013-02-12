# produces RW gravity models 
# initialConditionsMode=1 Random, =2 Use site size, =3 Forced
# betaVec and DScaleVec are vectors with all desired values
iterRWcalc <- function(betaVec, DScaleVec, countMax=100, selfConsistentOn=FALSE, initialConditionsMode=2, printOn=FALSE){

inputDir="input/"
outputDir="output/"
rootName="aegean39S1L3a";
numberRows=39
#DScale=200
alpha=4.0
gamma=1.0

source("RWmodel.r")
source("RWcalc.r")
source("RWflowcalc.r")
source("vectorStats.r")
source("RWtreecalc.r")

if (selfConsistentOn) {
 selfConsistentString="SC"
 print("Self Consistent Solution", quote=FALSE)
} else {
 selfConsistentString="FO"
 print("Fixed Output Solution", quote=FALSE)
}

print(paste("***",rootName,"***"),quote=FALSE)
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
if (alpha>0) {
    potTypeName="VAR"
    print("ariadne potential used", quote=FALSE)
   } else {
    potTypeName="VRW"
    print("exponential RW potential used", quote=FALSE)
   }

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

lll <- length(betaVec)*length(DScaleVec)
betaValues <- seq(-1,length=lll)
DScaleValues <- seq(-1,length=lll)
meanVec <- seq(-1,length=lll)
sdVec <- seq(-1,length=lll)
varVec <- seq(-1,length=lll)
SVec <- seq(-1,length=lll)
hubVec <- seq(-1,length=lll)
hubTwoVec <- seq(-1,length=lll)
hubOneZVec <- seq(-1,length=lll)
nTerminal <- seq(-1,length=lll)

iii=0

for (DScale in DScaleVec) {  
   if (printOn) print(paste("*** D =",DScale), quote=FALSE)
   xMatrix <- distMatrix/DScale;
   if (alpha>0) {
    potlMatrix <- (1+xMatrix^alpha)^(-gamma)
    #potTypeName="VAR"
    #print("ariadne potential used", quote=FALSE)
   } else {
    potlMatrix <- exp(-xMatrix)
    #potTypeName="VRW"
    #print("exponential RW potential used", quote=FALSE)
   }
   for (site in (1:numberRows)) potlMatrix[site,site] <-0
   #print(paste("Potential Matrix"), quote=FALSE)
   #print(potlMatrix)

   for (beta in betaVec) { 
    iii=iii+1 
    if (printOn) print(paste("--- beta =",beta,"  run number ",iii), quote=FALSE)
    betaValues[iii] <- beta
    DScaleValues[iii]<-DScale
    rwcalcdf <-RWcalc( sitedf$Size, wVecInitial, potlMatrix, beta, countMax, selfConsistentOn, initialConditionsMode, printOn) 
    wvecStats <- vectorStats(rwcalcdf$wvector)
    meanVec[iii] <- wvecStats$mean
    sdVec[iii] <- wvecStats$var
    varVec[iii] <- wvecStats$var
    SVec[iii] <- wvecStats$entropy
    hubVec[iii] <- wvecStats$hubOneAv
    hubTwoVec[iii] <- wvecStats$hubTwoAv
    hubOneZVec[iii] <- wvecStats$hubOneZ
    flowList <- RWflowcalc(sitedf$Size, rwcalcdf$wvector, potlMatrix, beta, selfConsistentOn)
    treecalcList <- RWtreecalc(numberRows, flowList$flowMatrix)
    nTerminal[iii] <- length(treecalcList$terminal[treecalcList$terminal>0])
   }
}
resdf <- data.frame(beta=betaValues, DScale=DScaleValues, mean = meanVec, sd=sdVec, entropy=SVec, terminal=nTerminal, hub=hubVec, hubTwo=hubTwoVec, hubOneZ=hubOneZVec)
if (printOn) print(resdf)
resultsOutputFileNameRoot <- paste(outputDir,rootName,"_RW_",potTypeName,"_",selfConsistentString,"_",initialConditionsType,sep="")
print(paste("Output results to file",resultsOutputFileNameRoot),quote=FALSE)
resList <- list(root=resultsOutputFileNameRoot, betaVec=betaVec, DScaleVec=DScaleVec, df=resdf)
}
