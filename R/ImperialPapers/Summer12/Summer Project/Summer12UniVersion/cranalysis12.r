#This analyses the citation/reference ratio of papers
# and fits it to log normal using CUMMULATIVE distribution
# This should be called from runcranalysisNN.r (where NN=12)
# Sources several other files :-
#  logbincount.r, readRCYfileNN.r, citationReferenceIndexNN.r and citationReferenceAllData.r
# 
# rootName 
# is start of file name used.
# Format of file is given in citationReferenceIndex12.r
#
# cNotCoverR 
# is True if want to study C (citations) not 
# C/R (citations/references) of each paper
#
# normalise
# =TRUE if want to study x/<x> not just x for each paper 
#
# parameterAnalysisOn
# =TRUE if want to analyse the parameters 
# of the fit including plots
#
# plotsOn 
# TRUE if want plots of data and fit
#
# minYearInput (maxYearInput) 
# are the first (last) year of any publication of papers to be included
# e.g. 2000 (2005) means papers from 2000 to 2005 inclusive are included
#
# numberParameters
# 3 => fit all of mu, sigma and A, 1=>fit sigma only setting A=1 and mu=-(sigma)^2/2
#
# OSWindows
# True if drawing on MS Windows, FALSE for MAC


cranalysis <- function(inputDir,outputDir,rootName="IC20090521", 
                                    cNotCoverR=TRUE, normalise=TRUE, 
                                    facultyNotDept=TRUE, 
                                    dataSourceIndex=1, #arXiv=FALSE, 
                                    allData=FALSE,
                                    minYearInput=1900, maxYearInput=2009, numberParameters=3, 
                                    parameterAnalysisOn=TRUE, plotsOn =TRUE,
                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=FALSE, epsPlotOn=TRUE,minCoverMeanC=0){

print(paste("##########################################################"), quote=FALSE)
print(paste("*** cranalysis *************************************"), quote=FALSE)
print(paste("rootName ",rootName," [",minYearInput, ",", maxYearInput,"]",sep=""), quote=FALSE)
print(paste("OSWindows",OSWindows,", screenOn",screenOn,", pdfPlotOn",pdfPlotOn,", epsPlotOn",epsPlotOn), quote=FALSE)

colourlist=c("black", "red", "blue", "green", "magenta", "brown","slategrey","orange","purple","limegreen", "deepskyblue2")
source("logbincount.r")
source("readRCYfile12.r")
source("citationReferenceIndex12.r")
source("citationReferenceAllData.r")
                                    
facOn=FALSE
if (facultyNotDept) {
facOn=TRUE 
}
deptOn=!(facOn)

# minYear (maxYear) 
# are the year BEFORE (AFTER) any publication date to be included 
# e.g. 2000 (2005) means papers from 2001 to 2004 inclusive are included
minYear=minYearInput-1
maxYear=maxYearInput+1


valueName="(C/R)"
valueFileName="CR"
if (cNotCoverR) {
valueName="C"
valueFileName="C"
}
normaliseString=""
normaliseFileString="unorm"
if (normalise) {
normaliseString=paste("/<",valueName,">",sep="")
normaliseFileString="norm"
}
xLabel=paste(valueName,normaliseString,sep="")

yearRangeString = paste((minYear+1),(maxYear-1),sep="-")
if (maxYear>2009) yearRangeString = paste((minYear+1),"2009",sep="-")
if ( (maxYear>2009) && (minYear<1950)) yearRangeString = ""

parameterString=paste("p",numberParameters,sep="")

# default settings use all data
facultyList <- list("Natural_Sciences","Medicine","Engineering","Business_School","Cross_Disciplinary","Other");
deptList <- list("National_Heart_and_Lung_Institute", "Department_of_Physics", "Division_of_Investigative_Science", "Division_of_Surgery,_Oncology,_Reproductive_Biology_and_Anaesthetics", "Division_of_Medicine", "Division_of_Neurosciences_and_Mental_Health", "Department_of_Chemistry", "Division_of_Epidemiology,_Public_Health_and_Primary_Care", "Division_of_Biology", "Department_of_Chemical_Engineering", "Department_of_Mathematics", "Division_of_Clinical_Sciences", "Department_of_Mechanical_Engineering", "Department_of_Materials", "Division_of_Cell_and_Molecular_Biology", "Division_of_Molecular_Biosciences", "Department_of_Civil_and_Environmental_Engineering", "Department_of_Earth_Science_and_Engineering", "Kennedy_Institute_of_Rheumatology", "Department_of_Bioengineering", "Department_of_Computing", "Department_of_Electrical_and_Electronic_Engineering", "Centre_for_Environmental_Policy", "Department_of_Aeronautics", "College_Headquarters", "Business_School", "Institute_of_Biomedical_Engineering", "Faculty_of_Medicine_Centre", "Educational_Quality_Office", "Faculty_of_Natural_Sciences", "Department_of_Humanities", "Faculty_of_Engineering", "Institute_for_Security_Science_and_Technology", "Graduate_Schools", "The_Grantham_Institute_for_Climate_Change", "Information_and_Communication_Technologies", "Department_of_Life_Sciences", "Development_and_Corporate_Affairs", "Health_and_Safety_Services", "Central", "Institute_for_Mathematical_Sciences", "Library_Services", "Research_Services_Division")
arXivList <- list("astro-ph","hep-ph","hep-th","gr-qc");
EBRPList <- list("Physics and astronomy","Business and International Management");
EBRPphysList <- list("3100","3101","3102","3103","3104","3105","3106","3107","3108","3109","3110");
deptFacultyIndex <- c(2,1,2,2,2,2,1,2,1,3,1,2,3,3,1,1,3,3,2,3,3,3,1,3,5,4,5,2,6,1,6,3,5,6,5,6,1,6,6,6,5,6,6);
titleString<-paste(rootName,yearRangeString,"All") 
unitList <- list("All")
binSize <- seq(from=50, by=0, length=length(unitList))

if(dataSourceIndex==4){
        typeString<-"EBRPphys"
        unitList <- list(EBRPphysList[[1]],EBRPphysList[[2]],EBRPphysList[[3]],EBRPphysList[[4]],EBRPphysList[[5]],EBRPphysList[[6]],EBRPphysList[[7]],EBRPphysList[[8]],EBRPphysList[[9]],EBRPphysList[[10]],EBRPphysList[[11]])###############
        binSize <- seq(from=15, by=0, length=length(unitList)) #use this (->) if you want to change the bin size individually: c(20,...,15)
        binType <- seq(from=1, by=1, length=length(unitList)) #use this (->) if you want to change the bin type individually: c(1,...,1) 
        titleString <- paste("EBRP Physics",yearRangeString)
}
if(dataSourceIndex==3){
        typeString<-"EBRP"
        unitList <- list(EBRPList[[1]], EBRPList[[2]])
        binSize <- c(20,15) #seq(from=20, by=0, length=length(unitList))
        binType <- c(1,1) #seq(from=1, by=1, length=length(unitList))
        titleString <- paste("EBRP",yearRangeString)
}
if(dataSourceIndex==2){
        typeString<-"arXiv"
        unitList <- list(arXivList[[1]], arXivList[[2]], arXivList[[3]], arXivList[[4]])
        binSize <- c(15,15,15,10) #seq(from=20, by=0, length=length(unitList))
        binType <- c(1,1,1,2) #seq(from=1, by=1, length=length(unitList))
        titleString <- paste("arXiv",yearRangeString)
}
if(dataSourceIndex==1){
    if (facOn){
        typeString<-"Fac"
        unitList <- list(facultyList[[1]], facultyList[[2]], facultyList[[3]])
        binSize <- c(15,15,10) #seq(from=20, by=0, length=length(unitList))
        binType <- c(1,1,2) #seq(from=1, by=1, length=length(unitList))
    }
    
    if (deptOn){
        typeString<-"Dept"
        
        #nnn=1
        #firstDept <- nnn*length(colourlist)+1
        #lastDept <- firstDept-1+length(colourlist)
        
        #firstDept<-1
        #lastDept<-24 #length(deptList)
        #deptIndexList <- firstDept:lastDept
        
        # These are the largest two from each faculty
        deptIndexList <- c(1,3,2,7,10,13)
        
        titleString<-paste(rootName,yearRangeString,"Departments Ranked",deptIndexList) 
        unitList <- list()
        binSize <- list()
        binType <- seq(from=1, by=0, length=length(unitList))
        binColour <- list()
        for (jjj in 1:length(deptIndexList) ){
         ddd <- deptIndexList[[jjj]]
         facultyIndex <- deptFacultyIndex[[jjj]]
         unitList[[jjj]] <- deptList[[ddd]]
         bbb=20
         iii=1
         if (ddd>length(colourlist)*2) {
         bbb=10
         iii=2
         }
         if (ddd>length(colourlist)*3)  {
         bbb=10
         iii=3
         }
         binSize[[jjj]] <- bbb
         binType[jjj] <- facultyIndex
         binColour[[jjj]] <- colourlist[[facultyIndex]]
        } # eo for ddd
    
    } # eo if deptOn
    
}
    nameList1 <- paste(rootName,unitList,sep="")
	#nameList2 <- paste(unitList,sep="")
minCount<- 0

# to read in IC files
if (dataSourceIndex==1){
   headerOn   <- TRUE
   sepString  <- "\t"
   refColumn  <- 2
   citeColumn <- 3 
   yearColumn <- 4
   dateColumn <- -1
   typeColumn <- -1
}
# to read in arXiv files
if(dataSourceIndex==2){
   fullName = "arXivgrcy.dat"
   headerOn   <- TRUE
   sepString  <- "\t"
   refColumn  <- 3
   citeColumn <- 4 
   yearColumn <- 5
   dateColumn <- -1
   typeColumn <- 2
}
# to read in EBRP files
if(dataSourceIndex==3){
   fullName = "EBRPgrcy.dat"
   headerOn   <- TRUE
   sepString  <- "\t"
   refColumn  <- 5
   citeColumn <- 4 
   yearColumn <- 3
   dateColumn <- -1
   typeColumn <- 2
}
# to read in EBRPphys files
if(dataSourceIndex==4){
   #fullName = "EBRPgrcy.dat"
   headerOn   <- TRUE
   sepString  <- "\t"
   refColumn  <- 7
   citeColumn <- 6 
   yearColumn <- 3
   dateColumn <- -1
   typeColumn <- -1
}

print(paste("###",titleString,yearRangeString,"###"), quote=FALSE)
 
crdata <- numeric()
totalRefs=0
totalCitations=0
totalNumberPapers=0
totalNZPCRPapers=0
meanValueUnnorm=0
CRList <- list()
for (iii in 1:length(unitList)) {
 
 if (dataSourceIndex==1){# full name for other types already set up above
     fullName=paste(nameList1[[iii]],"grcy.dat",sep="")
 }
 if (dataSourceIndex==4){# full name for other types already set up above
     fullName=paste(nameList1[[iii]],"grcy.dat",sep="")
 }
 print(paste("***",titleString,yearRangeString,unitList[[iii]],":",fullName,"***"), quote=FALSE)
 citations = readRCYfile(inputDir,fullName,headerOn, sepString, refColumn, citeColumn, yearColumn, dateColumn, typeColumn)
 refList<-list()
 citeList<-list()
 if (dataSourceIndex==4){ # xxx files
     refList<-citations$ref[citations$year>minYear & citations$year<maxYear] #$References
     citeList<-citations$cite[citations$year>minYear & citations$year<maxYear] #$Citations
 }
 if (dataSourceIndex==3){ #EBRP files
     refList<-citations$ref[citations$year>minYear & citations$year<maxYear & citations$type==unitList[[iii]]] #$References
    citeList<-citations$cite[citations$year>minYear & citations$year<maxYear & citations$type==unitList[[iii]]] #$Citations
 }
 if (dataSourceIndex==2){ #arXiv files
     refList<-citations$ref[citations$year>minYear & citations$year<maxYear & citations$type==unitList[[iii]]] #$References
    citeList<-citations$cite[citations$year>minYear & citations$year<maxYear & citations$type==unitList[[iii]]] #$Citations
 }
 if (dataSourceIndex==1){ # IC files
     refList<-citations$ref[citations$year>minYear & citations$year<maxYear] #$References
     citeList<-citations$cite[citations$year>minYear & citations$year<maxYear] #$Citations
 }
 CRList[[iii]] <- citationReferenceIndex12(refList, citeList, binSize[[iii]], minCount, normalise, cNotCoverR, numberParameters, minCoverMeanC) 
 infoList = CRList[[iii]]$infoList
 totalRefs = totalRefs + infoList$totalNumberReferences
 totalCitations = totalCitations + infoList$totalNumberCitations
 
 totalNumberPapers = totalNumberPapers + infoList$totalNumberPapers
 totalNZPCRPapers = totalNZPCRPapers + infoList$totalNZPCRPapers
 crdata = c(crdata,CRList[[iii]]$crdata)
 meanValueUnnorm = meanValueUnnorm + CRList[[iii]]$meanValueUnnorm

}
meanValueUnnorm = meanValueUnnorm / length(unitList)

# don't know how to be sure the parameters are always in this order
dmuVector<-numeric()
sigmaVector <-numeric()
dAVector<-numeric()
dmuVectorError<-numeric()
sigmaVectorError <-numeric()
dAVectorError<-numeric()
sigmatValueVector <-numeric()
sigmaPrtVector <-numeric()
dmutValueVector <-numeric()
dmuPrtVector <-numeric()
noOfPapers <-numeric()
chiSqVal <-numeric()
c0Val <-numeric()
rVal <-numeric()
stdErrpDof <- numeric()
#vvv<-numeric()
CavRav<-numeric()
binCount<-numeric()
fractionRetained<-numeric()


if(allData){
    CRList[[length(CRList)+1]] = citationReferenceAllData(crdata, numberbins=20, numberParameters,totalRefs,totalCitations,totalNumberPapers,totalNZPCRPapers,meanValueUnnorm)
    unitList <- c(unitList,"All")
    binType <- c(binType,1)
}
unitList <- gsub("_"," ",unitList)
#unitList <- gsub("Department","Dept.",unitList)
unitList <- gsub("Department of ","",unitList)
unitList <- gsub("National ","",unitList)
unitList <- gsub("Division of ","",unitList)



for (iii in 1:length(unitList)) {
ccc<-coef(summary(CRList[[iii]]$fit))
sigmaValue <- ccc[[1]]
sigmaError <- ccc[[1+numberParameters]]
sigmatValue <- ccc[[1+numberParameters*2]]
sigmaPrt <- ccc[[1+numberParameters*3]]
if (numberParameters==3) {
dmuValue <- ccc[[2]]
dAValue <- ccc[[3]]
dmuError <- ccc[[5]]
dAError <- ccc[[6]]
dmutValue <- ccc[[8]]
dmuPrt <- ccc[[11]]
}
else{
dmuValue <- 0.0
dAValue <- 0.0
dmuError <- 0.0
dAError <- 0.0
dmutValue <- 0.0
dmuPrt <- 0.0
}
sigmaVector[iii] <- sigmaValue
dmuVector[iii] <- dmuValue
dAVector[iii] <- dAValue
sigmaVectorError[iii] <- sigmaError
dmuVectorError[iii] <- dmuError
dAVectorError[iii] <- dAError
sigmatValueVector[iii]  <-sigmatValue
sigmaPrtVector[iii]  <- sigmaPrt
dmutValueVector[iii]  <- dmutValue
dmuPrtVector[iii]  <- dmuPrt
noOfPapers[iii] <- CRList[[iii]]$noOfPapers
chiSqVal[iii] <- CRList[[iii]]$chisq
print(CRList[[iii]]$meanValueUnnorm)
c0Val[iii] <- CRList[[iii]]$meanValueUnnorm

rVal[iii] <- CRList[[iii]]$rVal
stdErrpDof[iii] <- CRList[[iii]]$stdErrpDof
binCount[iii] <- CRList[[iii]]$binCount


#vvv[iii] <- muValue+sigmaValue*sigmaValue/2.0
infoList<-CRList[[iii]]$infoList
CavRav[iii] <- infoList$totalNumberCitations/infoList$totalNumberReferences
fractionRetained[iii] <-infoList$totalNZPCRPapers/infoList$totalNumberPapers
 }

muVector <- -sigmaVector*sigmaVector/2
sigmaVectorSquared <- sigmaVector*sigmaVector
print("mu")
print(muVector)
print("dA")
print(dAVector)
print("sigma^2")
print(sigmaVectorSquared)

if (parameterAnalysisOn) {
if (screenOn){
if (OSWindows) windows() else quartz()
plot(x=dmuVector,  y=sigmaVector, type="p", main=paste(titleString,"dmu and sigma"), xlab="mu", ylab="sigma", pch=binType)
if (OSWindows) windows() else quartz()
plot(x=dmuVector,  y=dAVector, type="p", main=paste(titleString,"dmu and dA"), xlab="mu", ylab="A", pch=binType)
if (OSWindows) windows() else quartz()
plot(x=dAVector,  y=sigmaVector, type="p", main=paste(titleString,"dA and sigma"), xlab="A", ylab="sigma", pch=binType)
#if (OSWindows) windows() else quartz()
#plot(x=dmuVector,  y=vvv, type="p", main=paste(titleString,"mu vs mu+sigma^2/2"), xlab="mu", ylab="mu vs mu+sigma^2/2", pch=binType)

if (OSWindows) windows() else quartz()
plot(x=CavRav,  y=fractionRetained, type="p", main=titleString, sub="<c>/<r> and fraction retained", xlab="<c>/<r>", ylab="fraction retained", pch=binType)

#if (OSWindows) windows() else quartz()
#dmuCRdf <- data.frame(dmu=dmuVector, CR=CavRav)
#dmuCRfit <- lm(dmu ~ CR ,data=dmuCRdf)
#print(summary(dmuCRfit))
#plot(y=dmuVector,  x=CavRav, type="p", main=paste(titleString,"mu and <c>/<r>"), ylab="mu", xlab="<c>/<r>", pch=binType)
#text(y=dmuVector, x=CavRav, labels=1:length(muVector), pos=4)
#lines(fitted(dmuCRfit), x=CavRav)

#if (OSWindows) windows() else quartz()
#muCRdf <- data.frame(mu=muVector, CRav=exp(vvv))
#muCRfit <- lm(mu ~ CR ,data=muCRdf)
#print(summary(muCRfit))
#plot(x=CavRav,  y=exp(vvv), type="p", main=paste(titleString,"<c>/<r> and <c/r>"), xlab="<c>/<r>", ylab="<c/r>", pch=binType)
#text(x=CavRav, y=exp(vvv), labels=1:length(muVector), pos=4)
#lines(fitted(muCRfit), x=CavRav)

# Making a Historgram/ Barplot of top z% etc. ############################################################################


if (OSWindows) windows() else quartz()
z<- 20##### should be able to change this somewhere at the top

allData<- CRList[[1]]$crdata
for(iii in 2: length(unitList)){
	allData<-c(allData, CRList[[iii]]$crdata)
}

zTocValue<-zToc(allData, z)

bootFunc<-function(data,indices) {
 cTozBoot(data, indices, zTocValue)
}
numberArtificialDataSets=1000

cToz<- 0
bootResult<-0
cTozLower<-0
cTozUpper<-0
numberSigma<-1
for(uuu in 1:length(unitList)){
	cToz[uuu]<-cToz(CRList[[uuu]]$crdata, zTocValue)
	#weightVec=seq(1, by=0, length=length(CRList[[uuu]]$crdata))
    #cToz2=bootFunc(CRList[[uuu]]$crdata,weightVec)
	#print(paste("***",uuu,cToz[uuu],cToz2))
	bootResult <- boot(CRList[[uuu]]$crdata, bootFunc, numberArtificialDataSets, stype = "i")
	bootFunctionResultMean=mean(bootResult$t) 
    bootFunctionResultSD=apply(bootResult$t,2,sd) #sd(bootResult$t) 
	print(paste("mean", "SD"))
	print(paste(bootFunctionResultMean, bootFunctionResultSD))
	cTozUpper[uuu]=bootFunctionResultMean+numberSigma*bootFunctionResultSD
	cTozLower[uuu]=bootFunctionResultMean-numberSigma*bootFunctionResultSD
}

if(dataSourceIndex==1){
	if (facultyNotDept){ # IC Faculty files
		subgroup <- "Faculty"
	}else {
		subgroup <- "Department"
	}  
}else(subgroup <- "Subgroup")

normalisation<- "Unnormalised"
if (normalise){
	normalisation<-"Normalised"
}

titlename<-paste("Percentage of ", subgroup, " in the Top ", z, "%", yearRangeString, "(", normalisation, ")")
axisName<-unitList
if(dataSourceIndex==1){
	if(facultyNotDept==FALSE){
			axisName<-"H&LI"
			axisName[[2]]<- "IS"
			axisName[[3]]<- "P"
			axisName[[4]]<- "C"
			axisName[[5]]<- "CE"
			axisName[[6]]<- "ME"
	}
}
if(dataSourceIndex==3){
	axisName<-"P&A"
	axisName[[2]]<- "B&IM"
}

barplot2(cToz,names.arg=axisName, xlab=subgroup, ylab=paste("Percentage in the Top ", z, "%"), main=titlename, xpd=FALSE, plot.grid = TRUE, ylim=c(0,ceiling(max(cTozUpper)/10)*10), plot.ci=TRUE, ci.l=cTozLower, ci.u=cTozUpper)

# Table of Names
if (OSWindows) windows() else quartz()
barplot(CavRav,names.arg=1:length(unitList)) # this does a histogram for all the faculties of CavRav

if (OSWindows) windows() else quartz()
plot(x=NULL,  y=NULL, type="n", xlim=c(-1,25) , ylim=c(0,length(unitList)) , main=titleString, xlab=NULL, ylab=NULL, axes=FALSE) 
text(x=seq(0, times=length(unitList)), y=1:length(unitList), labels=1:length(unitList), pos=2)
text(x=seq(1, times=length(unitList)), y=1:length(unitList), labels=unitList, pos=4)
} # end of if screenOn
} #end of if (parameterAnalysisOn)


if (plotsOn) {
iii=1
xMin<-CRList[[iii]]$xMin;
xMax<-CRList[[iii]]$xMax;
yscale <-CRList[[iii]]$meanValueUnnorm
yMin<-CRList[[iii]]$rhoMin/CRList[[1]]$totalPapers;
yMax<-CRList[[iii]]$rhoMax/CRList[[1]]$totalPapers;
if (length(unitList)>1) {
 for (iii in 2:length(unitList)) {
      xMin<-min(xMin, CRList[[iii]]$xMin)
      xMax<-max(xMax, CRList[[iii]]$xMax)
      yscale <-CRList[[iii]]$meanValueUnnorm
      yMin<-min(yMin, CRList[[iii]]$rhoMin/CRList[[iii]]$totalPapers)
      yMax<-max(yMax, CRList[[iii]]$rhoMax/CRList[[iii]]$totalPapers)
 }
}
if(xMin==0){ #Hack to prevent GScale error with naughty logarithms of zero
    xMin = 0.00000001
}
if(yMin==0){
    yMin = 0.00000001
}

# basic plot
#yLabel=paste("<",valueName,"> * Count/Total",sep="")
yLabel="Probability density"

##############On Screen
#############if (screenOn){
#############print(paste("Windows plotting",yMin,yMax), quote=FALSE)
#############if (OSWindows) windows() else quartz()
#############
#############plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with both positive citation and reference counts)", 
#############     xlab=xLabel, ylab=yLabel )
#############for (iii in 1:length(unitList)) {
#############yscale <-CRList[[iii]]$meanValueUnnorm
#############points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth), pch=iii, col=colourlist[iii], cex=1.2)
#############lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth),col=colourlist[iii], lty=iii)
#############}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", 
#       xlab=xLabel, ylab=paste(expression(c[0]),"* Count/Total"))

#On Screen######################## HERE DO THIS
if (screenOn){
print(paste("Windows plotting",yMin,yMax), quote=FALSE)
if (OSWindows) windows() else quartz()

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with positive citation counts)", xlab=xLabel, ylab=yLabel )
for (iii in 1:length(unitList)) {
yscale <-CRList[[iii]]$meanValueUnnorm
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth), pch=iii, col=colourlist[iii], cex=1.2)
x <- CRList[[iii]]$df$citeRefRatio   
sigma <-  sigmaVector[iii]
mu <- -(sigma*sigma)/2 +dmuVector[iii]
A <- 1+dAVector[iii]
y <- A*(plnorm(CRList[[iii]]$df$binUpper, mu, sigma)-plnorm(CRList[[iii]]$df$binLower, mu, sigma) ) /  CRList[[iii]]$df$binWidth
lines(x, y,col=colourlist[iii], lty=iii)
}

legend (x="topright",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));
}

outputDirRootName=paste(outputDir, rootName,sep="")

# PDF plot
if (pdfPlotOn){
print(paste("pdf plotting",titleString,yMin,yMax), quote=FALSE)
pdfFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ll.pdf",sep="_")
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with positive reference counts)",
     xlab=xLabel, ylab=yLabel )
for (iii in 1:length(unitList)) {
yscale <-CRList[[iii]]$meanValueUnnorm
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth), pch=iii, col=colourlist[iii], cex=1.2)
######################################lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth),col=colourlist[iii], lty=iii)
x <- CRList[[iii]]$df$citeRefRatio   
sigma <-  sigmaVector[iii]
mu <- -(sigma*sigma)/2 +dmuVector[iii]
A <- 1+dAVector[iii]
y <- A*(plnorm(CRList[[iii]]$df$binUpper, mu, sigma)-plnorm(CRList[[iii]]$df$binLower, mu, sigma) ) /  CRList[[iii]]$df$binWidth
lines(x, y,col=colourlist[iii], lty=iii)#######################################

}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", 
#       xlab=xLabel, ylab="Count/Total")
legend (x="topright",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));

dev.off()
}

if (epsPlotOn){
	# EPS plot
	print(paste("eps plotting",titleString,yMin,yMax), quote=FALSE)
	epsTitleOn=FALSE
	epsTitleString=""
	if (epsTitleOn) epsTitleString=titleString
	epsFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ll.eps",sep="_")
	postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)

		 
	plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=epsTitleString,
		 xlab=xLabel, ylab=yLabel )
	for (iii in 1:(length(unitList))) {
	yscale <-CRList[[iii]]$meanValueUnnorm
	#print(list(yscale))
	points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth), pch=iii, col=colourlist[iii], cex=1.2)
	x <- CRList[[iii]]$df$citeRefRatio  
	sigma <-  sigmaVector[iii]
	mu <- -(sigma*sigma)/2 +dmuVector[iii]
	A <- 1+dAVector[iii]
	y <- A*(plnorm(CRList[[iii]]$df$binUpper, mu, sigma)-plnorm(CRList[[iii]]$df$binLower, mu, sigma) ) /  CRList[[iii]]$df$binWidth
	lines(x, y,col=colourlist[iii], lty=iii)
	#lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/(CRList[[iii]]$totalPapers * CRList[[iii]]$df$binWidth),col=colourlist[iii], lty=iii) # < PREVIOUS CODE
	}

	#title(main=titleString, sub="( with both positive citation and reference counts)", xlab=xLabel, ylab="Count/Total")
	legend (x="bottomleft",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));

	#########################################
	#Disable legend above if wanted
	#########################################
	
	dev.off()

	epsTitleString=""
	if (epsTitleOn) epsTitleString=titleString
	epsFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString, parameterString, typeString,"hist.eps",sep="_") # put parameterString here
	postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)
	
	z<- 20 ##### should be able to change this somewhere at the top

	allData<- CRList[[1]]$crdata
	for(iii in 2: length(unitList)){
		allData<-c(allData, CRList[[iii]]$crdata)
	}

	zTocValue<-zToc(allData, z)

	bootFunc<-function(data,indices) {
	 cTozBoot(data, indices, zTocValue)
	}
	numberArtificialDataSets=1000

	cToz<- 0
	bootResult<-0
	cTozLower<-0
	cTozUpper<-0
	numberSigma<-1
	for(uuu in 1:length(unitList)){
		cToz[uuu]<-cToz(CRList[[uuu]]$crdata, zTocValue)
		#weightVec=seq(1, by=0, length=length(CRList[[uuu]]$crdata))
		#cToz2=bootFunc(CRList[[uuu]]$crdata,weightVec)
		#print(paste("***",uuu,cToz[uuu],cToz2))
		bootResult <- boot(CRList[[uuu]]$crdata, bootFunc, numberArtificialDataSets, stype = "i")
		bootFunctionResultMean=mean(bootResult$t) 
		bootFunctionResultSD=apply(bootResult$t,2,sd) #sd(bootResult$t) 
		print(paste("mean", "SD"))
		print(paste(bootFunctionResultMean, bootFunctionResultSD))
		cTozUpper[uuu]=bootFunctionResultMean+numberSigma*bootFunctionResultSD
		cTozLower[uuu]=bootFunctionResultMean-numberSigma*bootFunctionResultSD
	}

	if(dataSourceIndex==1){
		if (facultyNotDept){ # IC Faculty files
			subgroup <- "Faculty"
		}else {
			subgroup <- "Department"
		}  
	}else(subgroup <- "Subgroup")

	normalisation<- "Unnormalised"
	if (normalise){
		normalisation<-"Normalised"
	}

	interval<- c(5, 5, 5, 5, 5,5, 5, 5, 5, 5, 5)

	
	axisName<-unitList
	if(dataSourceIndex==1){
		if(facultyNotDept==FALSE){
				axisName<-"H&LI"
				axisName[[2]]<- "IS"
				axisName[[3]]<- "P"
				axisName[[4]]<- "C"
				axisName[[5]]<- "CE"
				axisName[[6]]<- "ME"
		}
	}
	if(dataSourceIndex==3){
		axisName<-"P&A"
		axisName[[2]]<- "B&IM"
	}

	
	barplot2(cToz,names.arg=axisName, xlab=subgroup, ylab=paste("Percentage in the Top ", z, "%"), main=epsTitleString, xpd=FALSE, plot.grid = TRUE, ylim=c(0,ceiling(max(cTozUpper)/10)*10), plot.ci=TRUE, ci.l=cTozLower, ci.u=cTozUpper)

	dev.off()

}

} # end of if plotsOn

outputList <-list(cNotCoverR=cNotCoverR, normalise=normalise, 
                                    facultyNotDept=facultyNotDept, 
                                    minYearInput=minYearInput, maxYearInput=maxYearInput, 
                                    numberParameters=numberParameters,
                                    unitList=unitList, 
                                    sigmaValue = sigmaVector , dmuValue=dmuVector, dAValue=dAVector,
                                    sigmaError = sigmaVectorError , dmuError=dmuVectorError, dAError=dAVectorError,
                                    sigmatValue= sigmatValueVector, dmutValue=dmutValueVector,                                    
                                    sigmaPrt   = sigmaPrtVector,    dmuPrt=dmuPrtVector,
                                    noOfPapers = noOfPapers,       chiSqVal=chiSqVal,
                                    c0Val      = c0Val,         rVal = rVal, 
                                    stdErrpDof=stdErrpDof,      binCount = binCount
                                    )




} # end of function

# comments
zToc <- function(list,z){
  orderedList <-sort(list,decreasing=TRUE)
  index1 = ceiling(z*length(list)/100)
  index2 = floor(z*length(list)/100)
  c1=orderedList[index1]  
  c2=orderedList[index2]
  c=(c1+c2)/2  
} # end of function

# comments
cToz <- function(list,c){
  orderedList <-sort(list,decreasing=TRUE)
  indexList=1:length(list)
  clocation = indexList[orderedList>=c]
  z1=clocation[length(clocation)]
  clocation = indexList[orderedList<=c]
  z2=clocation[1]
  zav=(z1+z2)/2
  index =zav*100/length(list) 
} # end of function
 
 #comments the weight if the frequency of each entry
 # use for boot routine in bootstrapping
cTozBoot <- function(data,indices,c){
  list <- data[indices]
  orderedList <-sort(list,decreasing=TRUE)
  indexList=1:length(list)
  clocation = indexList[orderedList>=c]
  z1=clocation[length(clocation)]
  clocation = indexList[orderedList<=c]
  z2=clocation[1]
  zav=(z1+z2)/2
  index =round(zav*100/length(list)) 
} # end of function
