calcZCorrelations <- function(inputDir="input/",outputDir="output/",rootName="IC20090521",
                                    facultyNotDept=TRUE, arXiv=FALSE, allData=FALSE,
                                    minYearInput=1900, maxYearInput=2009, plotsOn =TRUE,
                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=FALSE, epsPlotOn=TRUE, minCoverMeanC=0,minCRoverMeanCR=0) {
source("logbincount.r")
source("readRCYfile.r")
source("citationReferenceIndex7bk.r")


facultyList <- list("Natural_Sciences","Medicine","Engineering","Business_School","Cross_Disciplinary","Other");
deptList <- list("National_Heart_and_Lung_Institute", "Department_of_Physics", "Division_of_Investigative_Science", 
"Division_of_Surgery,_Oncology,_Reproductive_Biology_and_Anaesthetics", "Division_of_Medicine", 
"Division_of_Neurosciences_and_Mental_Health", "Department_of_Chemistry", 
"Division_of_Epidemiology,_Public_Health_and_Primary_Care", "Division_of_Biology", "Department_of_Chemical_Engineering", 
"Department_of_Mathematics", "Division_of_Clinical_Sciences", "Department_of_Mechanical_Engineering", 
"Department_of_Materials", "Division_of_Cell_and_Molecular_Biology", "Division_of_Molecular_Biosciences", 
"Department_of_Civil_and_Environmental_Engineering", "Department_of_Earth_Science_and_Engineering", 
"Kennedy_Institute_of_Rheumatology", "Department_of_Bioengineering", "Department_of_Computing", 
"Department_of_Electrical_and_Electronic_Engineering", "Centre_for_Environmental_Policy", "Department_of_Aeronautics", 
"College_Headquarters", "Business_School", "Institute_of_Biomedical_Engineering", "Faculty_of_Medicine_Centre", 
"Educational_Quality_Office", "Faculty_of_Natural_Sciences", "Department_of_Humanities", "Faculty_of_Engineering", 
"Institute_for_Security_Science_and_Technology", "Graduate_Schools", "The_Grantham_Institute_for_Climate_Change", 
"Information_and_Communication_Technologies", "Department_of_Life_Sciences", "Development_and_Corporate_Affairs", 
"Health_and_Safety_Services", "Central", "Institute_for_Mathematical_Sciences", "Library_Services", 
"Research_Services_Division")
arXivList <- list("astro-ph","hep-ph","hep-th","gr-qc")



colourlist=c("black", "red", "blue", "green", "magenta", "brown","slategrey")
deptFacultyIndex <- c(2,1,2,2,2,2,1,2,1,3,1,2,3,3,1,1,3,3,2,3,3,3,1,3,5,4,5,2,6,1,6,3,5,6,5,6,1,6,6,6,5,6,6);

yearRangeString = paste((minYear),(maxYear),sep="-")
if (maxYear>2009) yearRangeString = paste((minYear+1),"2009",sep="-")
if ( (maxYear>2009) && (minYear<1950)) yearRangeString = ""
titleString<-paste(rootName,yearRangeString,"All")

valueName="(C/R)"
valueFileName="CR"
if (cNotCoverR) {
valueName="C"
valueFileName="C"
}
normaliseString=""
normaliseFileString=""
if (normalise) {
normaliseString=paste("/<",valueName,">",sep="")
normaliseFileString="norm"
}

parameterString=paste("p",numberParameters,sep="")



unitList <- list("All")
binSize <- seq(from=50, by=0, length=length(unitList))


if(dataSourceIndex==3){
        typeString<-"EBRP"
        unitList <- list(EBRPList[[1]], EBRPList[[2]])
        binSize <- c(15,15) #seq(from=20, by=0, length=length(unitList))
        binType <- c(1,1) #seq(from=1, by=1, length=length(unitList))
        titleString <- "EBRP"
}
if(dataSourceIndex==2){
        typeString<-"arXiv"
        unitList <- list(arXivList[[1]], arXivList[[2]], arXivList[[3]], arXivList[[4]])
        binSize <- c(15,15,15,10) #seq(from=20, by=0, length=length(unitList))
        binType <- c(1,1,1,2) #seq(from=1, by=1, length=length(unitList))
        titleString <- "arXiv"
}
if(dataSourceIndex==1){
    if (facultyNotDept){
        typeString<-"Fac"
        unitList <- list(facultyList[[1]], facultyList[[2]], facultyList[[3]])
         binSize <- c(15,15,10) #seq(from=20, by=0, length=length(unitList))
        binType <- c(1,1,2) #seq(from=1, by=1, length=length(unitList))
    }

    if (!facultyNotDept){
        typeString<-"Dept"

        # These are the largest two from each faculty
        deptIndexList <- c(1,3,2,7,10,13)

        titleString<-paste(rootName,yearRangeString,"Departments Ranked",deptIndexList)
        unitList <- list()
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

nameList <- paste(rootName,unitList,sep="")
unitList = gsub("_"," ",unitList)

Zcall <- numeric()
Zcrall <- numeric()

Zclist <- list()
Zcrlist <- list()
Zdiff <- numeric()
minzc <- Inf
maxzc <- -Inf
minzcr <- Inf
maxzcr <- -Inf


for (iii in 1:length(unitList)) {
 if (dataSourceIndex==1){ # to read in IC files
     fullName=paste(nameList[[iii]],"grcy.dat",sep="")
 }
 citations = readRCYfile(inputDir,fullName,headerOn, sepString, refColumn, citeColumn, yearColumn, dateColumn, typeColumn)
 refList<-list()
 citeList<-list()
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


    CFitData = citationReferenceIndex7bk(refList, citeList, binSize[[iii]], 0, normalise, TRUE, 1, minCoverMeanC) 
    CRFitData = citationReferenceIndex7bk(refList, citeList, binSize[[iii]], 0, normalise, FALSE, 1, minCRoverMeanCR) 



    z_f <- calcZscore(CFitData)
    z_r <- calcZscore(CRFitData)
        
    Zclist[[iii]] <- z_f
    Zcrlist[[iii]] <-  z_r
    Zdiff <- c(Zdiff,z_f - z_r)
    Zcall = c(Zcall,z_f)
    Zcrall = c(Zcrall,z_r)
    
    minzc <- min(minzc,min(z_f))
    maxzc <- max(maxzc,max(z_f))
    
    minzcr <- min(minzcr,min(z_r))
    maxzcr <- max(maxzcr,max(z_r))
    
}


histXmin=-2
histXmax=2
histXLabel=expression(z[f]-z[r])
histBreaks=40

xLabel = expression(z[f])
yLabel = expression(z[r])

minzc=-4
minzcr=-4
maxzc=5
maxzcr=5

densminX=-2
densmaxX=2
densminY=-2
densmaxY=2

density <- kde2d(Zcall,Zcrall,n=50, lims=c(minzc,maxzc,minzcr,maxzcr))
draw45degLine <- TRUE


#On Screen
if(plotsOn){
    if(screenOn){
        if (OSWindows) windows() else quartz()
        
        plot(x=NULL,  y=NULL, type="n", xlim=c(minzc,maxzc) , ylim=c(minzcr,maxzcr) , main=titleString, sub="", 
             xlab=xLabel, ylab=yLabel )
        if(draw45degLine) abline(a=0,b=1,lty=2)
        for (iii in 1:length(unitList)) {
        points(Zclist[[iii]],   Zcrlist[[iii]], pch=iii, col=colourlist[iii], cex=1.2)
        }
          
        legend (x="topleft",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],pch=1:length(unitList));
        
              
        if (OSWindows) windows() else quartz()
        hist(Zdiff, breaks=histBreaks,xlim=c(histXmin,histXmax), xlab=histXLabel)
          

        if (OSWindows) windows() else quartz()
        filled.contour(density,xlim=c(densminX,densmaxX) , ylim=c(densminY,densmaxY) , main=titleString, sub="", 
             xlab=xLabel, ylab=yLabel)
        
        
    }
}

outputDirRootName=paste(outputDir, rootName,sep="")

if (epsPlotOn){
# EPS plot
epsTitleOn=FALSE
epsTitleString=""
if (epsTitleOn) epsTitleString=titleString
epsFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ZCorrel.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)

 
plot(x=NULL,  y=NULL, type="n", xlim=c(minzc,maxzc) , ylim=c(minzcr,maxzcr), main=epsTitleString,
     xlab=xLabel, ylab=yLabel )
if(draw45degLine) abline(a=0,b=1,lty=2)
for (iii in 1:(length(unitList))) {
points(Zclist[[iii]],   Zcrlist[[iii]], pch=iii, col=colourlist[iii], cex=1.2)
}
legend (x="topleft",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],pch=1:length(unitList));
dev.off()



epsFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ZHist.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)
hist(Zdiff, breaks=histBreaks,xlim=c(histXmin,histXmax), xlab=histXLabel, main=epsTitleString)
dev.off()


epsFileName<-paste(outputDirRootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ZDensity.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)
filled.contour(density,xlim=c(densminX,densmaxX) , ylim=c(densminY,densmaxY) , main=epsTitleString, sub="", 
             xlab=xLabel, ylab=yLabel)  
dev.off()
}
}
calcZscore <- function(CFitData){

    Cccc<-coef(summary(CFitData$fit))
    CsigmaValue <- Cccc[[1]]
    CmuValue <- -0.5 * CsigmaValue * CsigmaValue

    lnc <- log(CFitData$crdata)
    z_f <- (lnc - CmuValue) / CsigmaValue
}
