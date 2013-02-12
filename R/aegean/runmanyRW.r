source("iterRWcalc.r")
source("contourPlotOutput.r")

setRange <- function(name,minVal,maxVal,stepVal){
 print(paste(name,"  min=",minVal,"  max=",maxVal,"  step=",stepVal,sep=" "),quote=FALSE)
 seq(minVal, maxVal, by=stepVal ) 
}

#betaVec<-setRange("beta",0.94, 1.06, 0.0002)
#DScaleVec<-setRange("D",100,1000,5)
betaVec<-setRange("beta",0.94, 1.06, 0.002)
DScaleVec<-setRange("D",50,1000,50)

countMax=100
selfConsistentOn=FALSE
initialConditionsMode=2 
printOn=FALSE
#printOn=TRUE


rrr <- iterRWcalc(betaVec, DScaleVec, countMax, selfConsistentOn, initialConditionsMode, printOn)

#print("Terminals")
zMatTerminal <- array(rrr$df$terminal,dim=c(length(betaVec),length(DScaleVec)))
contourPlotOutput(paste(rrr$root,"_RW_terminals",sep=""),betaVec,DScaleVec,zMatTerminal,"Terminals",substitute(beta),"D")

zMatHub <- array(rrr$df$hub,dim=c(length(betaVec),length(DScaleVec)))
contourPlotOutput(paste(rrr$root,"_RW_HubsAv",sep=""),betaVec,DScaleVec,zMatHub,"Hubs (Average)",substitute(beta),"D")

zMatHubOneZ <- array(rrr$df$hubOneZ,dim=c(length(betaVec),length(DScaleVec)))
contourPlotOutput(paste(rrr$root,"_RW_Hubs1s",sep=""),betaVec,DScaleVec,zMatHubOneZ,paste("Hubs 1 ",substitute(sigma),sep=""),substitute(beta),"D")
