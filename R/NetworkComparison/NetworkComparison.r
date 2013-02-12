    # Compare Networks via Vertex properties

screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE
OSWindows=TRUE

inputDir="input/"
outputDir="output/FixedR/"


testOn=FALSE; #TRUE
if (testOn) { 
 rootNameVec=c("circleN10D100.0-C5-J50.0_PPAbeta003_r1","circleN10D100.0-C5-J50.0_MDND100.000_r0");
 parameterVec<-c("PageRank","Betweenness")
# numberRows=10
} else { 
 rootNameVec="aegean39S1L3a";
 rootNameVec=c("circleN10D100.0-C5-J50.0_PPAbeta003_r1",
               "circleN10D100.0-C5-J50.0_DPPAbeta003_r0",
               "circleN10D100.0-C5-J50.0_MDND100.000_r0",
               "circleN10D100.0-C5-J50.0_VPD100.000_r0",
               "circleN10D100.0-C5-J50.0_SGMD100.000_r0",
               "circleN10D100.0-C5-J50.0_DCGMD100.000_r0",
               "circleN10D100.0-C5-J50.0_RWGMD100.000beta0.00100_r0"
               );
 parameterVec<-c("PageRank","Betweenness")
}

printON=TRUE


# prepare lists of networks to study 
numberNetworks=length(rootNameVec)
networkIndexVec<-1:numberNetworks

# Prepare parameter lists
numberParameters=length(parameterVec)
parameterIndexVec<-1:numberParameters
print("--- Looking at following parameters:-",quote=FALSE);
print(parameterVec)


# Prepare Mthods list, available:- "pearson", "kendall", "spearman"
corrMethodVec = c("pearson","kendall", "spearman")
numberMethods=length(corrMethodVec)
print("--- Looking at following correlation methods:-",quote=FALSE);
print(corrMethodVec)

#underbarPosVec<-regexpr("_",rootnameVec);
nameSplitList<-strsplit(rootNameVec,"_")
siteModelName<-rep("",times=numberNetworks)
edgeModelName<-rep("",times=numberNetworks)
for (nnn in networkIndexVec){
 siteModelName[nnn]<-nameSplitList[[nnn]][1] 
 edgeModelName[nnn]<-nameSplitList[[nnn]][2] 
}

#source("netCompReadData.r")
dflist=list()
numberSites=NA
for (nnn in networkIndexVec){
 networkFileName<-paste(inputDir,rootNameVec[nnn],"BareNetwork.dat",sep="");
 print(paste("---     Network File ",nnn,":",networkFileName),quote=FALSE);
 print(paste("---     Site Model ",siteModelName[nnn]),quote=FALSE);
 print(paste("---     Edge Model ",edgeModelName[nnn]),quote=FALSE);
 dflist[[nnn]] <- read.table(networkFileName,header=TRUE, sep="\t", fill=TRUE)
 ns=length(dflist[[nnn]][1])
 if (is.na(numberSites) ) {numberSites=ns
 } else { if (numberSites!=ns) print(paste("!!! WRONG number of sites found ",ns," but previously had ",numberSites),quote=FALSE);
 }
}
print(paste("--- number of sites found ",numberSites),quote=FALSE);


# Set up array netparamArray[nnn,ppp] which tells column in data frame of 
# parameter ppp (parameterVec[ppp]) 
# in network nnn (dflist[[nnn]]) 
# i.e. vector of values we want to look at is dflist[[nnn]][ccc]
#      where ccc=netparamArray[nnn,ppp] 
netparamArray=array(-1,dim=c(numberNetworks,numberParameters))
for (nnn in networkIndexVec){
 namesVec<-names(dflist[[nnn]])
 namesIndex<-1:length(namesVec)
 for (ppp in parameterIndexVec){
   ccc<-namesIndex[namesVec[namesIndex]==parameterVec[ppp]]
   netparamArray[nnn,ppp]=ccc[1] # note take first occurance
  } # eo for ppp
} #eo for nnn

corrList=list();
listNumber=0
for (ppp in parameterIndexVec){
 #print(paste("--- Looking at parameter ",ppp,":",parameterVec[ppp]),quote=FALSE);
  # now form a matrix whose columns are the data for different networks nnn for same parameter ppp
 #  dataMat<-array(NA,dim=c(numberSites,numberNetworks ) ) 
  dataList<-list()
  for (nnn in networkIndexVec){
    #print(paste("--- Looking at network ",nnn,":",rootNameVec[nnn]),quote=FALSE);
    pi<-netparamArray[nnn,ppp]
    dataList[nnn]<-array(dflist[[nnn]][pi],dim=c(numberSites,1))
   }# eo for nnn
  dataMat<-do.call(cbind,dataList)

 # for each matrix of parameter values vs network models find correlation matrix for specified method
 for (mmm in 1:numberMethods){
    corrMat<-cor(dataMat,method=corrMethodVec[mmm])
    listNumber=listNumber+1;
    corrList[[listNumber]]=list(parameter=parameterVec[ppp], pnumber=ppp, method=corrMethodVec[mmm],mnumber=mmm, correlationMatrix=corrMat)
 }# eo for mmm
 
}# eo for ppp
