calcZCorrelations <- function(rootName="IC20090521",
                                    facultyNotDept=TRUE, arXiv=FALSE, allData=FALSE,
                                    minYearInput=1900, maxYearInput=2009, plotsOn =TRUE,
                                    OSWindows=TRUE, screenOn=TRUE, pdfPlotOn=FALSE, epsPlotOn=TRUE, minCoverMeanC=0,minCRoverMeanCR=0) {

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
titleString<-paste(rootName,yearRangeString,"All")
unitList <- list("All")

if(arXiv){
        typeString<-"arXiv"
        unitList <- list(arXivList[[1]], arXivList[[2]], arXivList[[3]], arXivList[[4]])
        binSize <- c(15,15,15,10) #seq(from=20, by=0, length=length(unitList))
        binType <- c(1,1,1,2) #seq(from=1, by=1, length=length(unitList))
        titleString <- "arXiv"
}
else{
    if (facultyNotDept){
        typeString<-"Fac"
        unitList <- list(facultyList[[1]], facultyList[[2]], facultyList[[3]])
    }

    if (!facultyNotDept){
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
         binColour[[jjj]] <- colourlist[[facultyIndex]]
        } # eo for ddd

    } # eo if deptOn
}
# to read in IC files
headerOn   <- TRUE
sepString  <- "\t"
refColumn  <- 2
citeColumn <- 3 
yearColumn <- 4
dateColumn <- -1
typeColumn <- -1

if(arXiv){
headerOn   <- TRUE
sepString  <- "\t"
refColumn  <- 3
citeColumn <- 4 
yearColumn <- 5
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
 fullName = "arXivgrcy.dat"
 if(!arXiv){
     fullName=paste(nameList[[iii]],"grcy.dat",sep="")
 }
 citations = readRCYfile(fullName,headerOn, sepString, refColumn, citeColumn, yearColumn, dateColumn, typeColumn)
 refList<-list()
 citeList<-list()
 if(arXiv){
     refList<-citations$ref[citations$year>minYear & citations$year<maxYear & citations$type==unitList[[iii]]] #$References
    citeList<-citations$cite[citations$year>minYear & citations$year<maxYear & citations$type==unitList[[iii]]] #$Citations
 }
 else{
     refList<-citations$ref[citations$year>minYear & citations$year<maxYear] #$References
     citeList<-citations$cite[citations$year>minYear & citations$year<maxYear] #$Citations
 }

    citeList <- citeList / mean(citeList)
   

    crlist <- citeList / refList
    crlist <- crlist / mean(crlist[is.finite(crlist)])

    clist <- log(citeList[crlist>minCRoverMeanCR & citeList>minCoverMeanC & is.finite(crlist)])
    crlist <- log(crlist[crlist>minCRoverMeanCR & citeList>minCoverMeanC & is.finite(crlist)])

    z_f <- (clist - mean(clist)) / sd(clist);
    z_r <- (crlist - mean(crlist)) / sd(crlist);
    
    minzc <- min(minzc,min(z_f))
    maxzc <- max(maxzc,max(z_f))
    
    minzcr <- min(minzcr,min(z_r))
    maxzcr <- max(maxzcr,max(z_r))
    
    Zclist[[iii]] <- z_f
    Zcrlist[[iii]] <-  z_r
    Zdiff <- c(Zdiff,z_f - z_r)
    Zcall = c(Zcall,z_f)
    Zcrall = c(Zcrall,z_r)
    
    
    #print(paste("<c>",mean(clist),"s_c",sd(clist)))
    #print(paste("<cr>",mean(crlist),"s_cr",sd(crlist)))
    #print(list(length(citeList),length(refList),length(crlist),length(clist)))
    
}


histXmin=-2
histXmax=2
histXLabel=expression(z[f]-z[r])
histBreaks=60

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

if (epsPlotOn){
# EPS plot
print(paste("eps plotting",titleString,yMin,yMax), quote=FALSE)
epsTitleOn=FALSE
epsTitleString=""
if (epsTitleOn) epsTitleString=titleString
epsFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ZCorrel.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)

 
plot(x=NULL,  y=NULL, type="n", xlim=c(minzc,maxzc) , ylim=c(minzcr,maxzcr), main=epsTitleString,
     xlab=xLabel, ylab=yLabel )
if(draw45degLine) abline(a=0,b=1,lty=2)
for (iii in 1:(length(unitList))) {
points(Zclist[[iii]],   Zcrlist[[iii]], pch=iii, col=colourlist[iii], cex=1.2)
}
legend (x="topleft",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],pch=1:length(unitList));
dev.off()



epsFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ZHist.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)
hist(Zdiff, breaks=histBreaks,xlim=c(histXmin,histXmax), xlab=histXLabel, main=epsTitleString)
dev.off()


epsFileName<-paste(rootName,valueFileName,normaliseFileString,yearRangeString,parameterString,typeString,"ZDensity.eps",sep="_")
postscript(epsFileName, horizontal=FALSE, onefile=FALSE, height=6, width=6, pointsize=12)
filled.contour(density,xlim=c(densminX,densmaxX) , ylim=c(densminY,densmaxY) , main=epsTitleString, sub="", 
             xlab=xLabel, ylab=yLabel)  
dev.off()
}

}
