# Compare Networks via Vertex properties

screenOn=TRUE
epsOn=TRUE
pdfOn=TRUE
OSWindows=TRUE

inputDir="input/"
outputDir="output/FixedR/"


rootName=list()
testOn=TRUE
if (testOn) { 
 rootName[[1]]="circleN10D100.0-C5-J50.0_PPAbeta003_r1";
 rootName[[2]]="circleN10D100.0-C5-J50.0_MDND100.000_r0";
 parameterVec<-c("PageRank","Betweenness")
# numberRows=10
} else { 
 rootName="aegean39S1L3a";
 #numberRows=39
}

printON=TRUE

# prepare lists of networks to study 
numberNetworks=length(rootName)
networkIndexVec<-1:numberNetworks

# Prepare parameter lists
numberParameters=length(parameterVec)
parameterIndexVec<-1:numberParameters
print("--- Looking at following parameters:-",quote=FALSE);
print(parameterVec)

#source("netCompReadData.r")
dflist=list()
for (nnn in networkIndexVec){
 networkFileName<-paste(inputDir,rootName[[nnn]],"BareNetwork.dat",sep="");
 print(paste("---     Network File ",nnn,":",networkFileName),quote=FALSE);
 dflist[[nnn]] <- read.table(networkFileName,header=TRUE, sep="\t", fill=TRUE)
 
}

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

#c("pearson", "kendall", "spearman"))
corrMethodVec = c("kendall", "spearman")
numberMethods=length(corrMethodVec)
corrMat<-array(NA,dim=c(numberParameters,numberMethods ) ) 
for (ppp in parameterIndexVec){
 print(paste("--- Looking at parameter ",ppp,":",parameterVec[ppp]),quote=FALSE);
  # now form a matrix whose columns are the data for different networks nnn for same parameter ppp
  dataMat<-array(NA,dim=c(numberParameters,numberMethods ) ) 
  for (nnn in networkIndexVec){
    pi<-netparamArray[nnn,ppp]
    ccc<-dflist[[nnn]][pi]
    
   }# eo for nnn
 for (mmm in 1:numberMethods){
    corrMat[ppp,mmm]<-cor(xxx,yyy,method=corrMethodVec[mmm])xx
 }# eo for mmm
 
}# eo for ppp
