#This analyses the citation/reference ratio of papers
#and fits it to log normal 
#MAYBE better to use CDF (CUMMULATIVE distribution), ecdf pLogNormal etc to do better fit?

#format is "article guid" tab "number of references in paper" tab "number of citations";

colourlist=c("black", "red", "blue", "green", "magenta", "brown");

source("logbincount.r")
source("citationReferenceIndex4.r")

rootName="IC20090521"

minYear=1900
maxYear=2010

deptOn=TRUE
facOn=FALSE

# default settings use all data
deptList <- titleString<-paste(rootName,"after",minYear,"before",maxYear,"All") 
unitList <- list("All")
binSize <- seq(from=50, by=0, length=length(unitList))


if (facOn){
deptList <- titleString<-paste(rootName,"after",minYear,"before",maxYear,"Faculties") 
facultyList <- list("Natural_Sciences","Medicine","Engineering");
unitList <- facultyList
binSize <- seq(from=50, by=0, length=length(unitList))
}

if (deptOn){
deptList <- list("National_Heart_and_Lung_Institute", "Department_of_Physics", "Division_of_Investigative_Science", "Division_of_Surgery,_Oncology,_Reproductive_Biology_and_Anaesthetics", "Division_of_Medicine", "Division_of_Neurosciences_and_Mental_Health", "Department_of_Chemistry", "Division_of_Epidemiology,_Public_Health_and_Primary_Care", "Division_of_Biology", "Department_of_Chemical_Engineering", "Department_of_Mathematics", "Division_of_Clinical_Sciences", "Department_of_Mechanical_Engineering", "Department_of_Materials", "Division_of_Cell_and_Molecular_Biology", "Division_of_Molecular_Biosciences", "Department_of_Civil_and_Environmental_Engineering", "Department_of_Earth_Science_and_Engineering", "Kennedy_Institute_of_Rheumatology", "Department_of_Bioengineering", "Department_of_Computing", "Department_of_Electrical_and_Electronic_Engineering", "Centre_for_Environmental_Policy", "Department_of_Aeronautics", "College_Headquarters", "Business_School", "Institute_of_Biomedical_Engineering", "Faculty_of_Medicine_Centre", "Educational_Quality_Office", "Faculty_of_Natural_Sciences", "Department_of_Humanities", "Faculty_of_Engineering", "Institute_for_Security_Science_and_Technology", "Graduate_Schools", "The_Grantham_Institute_for_Climate_Change", "Information_and_Communication_Technologies", "Department_of_Life_Sciences", "Development_and_Corporate_Affairs", "Health_and_Safety_Services", "Central", "Institute_for_Mathematical_Sciences", "Library_Services", "Research_Services_Division")

#nnn=1
#firstDept <- nnn*length(colourlist)+1
#lastDept <- firstDept-1+length(colourlist)

firstDept<-1
lastDept<-24 #length(deptList)

titleString<-paste(rootName,"after",minYear,"before",maxYear,"Departments Ranked",firstDept,"to",lastDept) 
unitList <- deptList[firstDept:lastDept]
binSize <- list()
binType <- seq(from=1, by=0, length=length(unitList))
binColour <- list()
for (ddd in firstDept:lastDept){
 bbb=50
 iii=1
 if (ddd>length(colourlist)*2) {
 bbb=25
 iii=2
 }
 if (ddd>length(colourlist)*3)  {
 bbb=10
 iii=3
 }
 binSize[[ddd-firstDept+1]] <- bbb
 binType[ddd-firstDept+1] <- iii
 binColour[[ddd-firstDept+1]] <- colourlist[[iii]]
} # eo for ddd

} # eo if deptOn

print(paste("!!!!!!!!!!!!!!",titleString,"!!!!!!!!!!!!!!"), quote=FALSE)

nameList <- paste(rootName,unitList,sep="")


minCount<-0

CRList <- list()
for (iii in 1:length(unitList)) CRList[[iii]] <- citationReferenceIndex4(nameList[[iii]],binSize[[iii]], minCount, minYear, maxYear) 

parameterAnalysisOn=TRUE
if (parameterAnalysisOn) {

# don't know how to be sure the parameters are always in this order
#vvv = seq(from=1, by=0, length=length(unitList))
muVector<-numeric()
sigmaVector <-numeric()
AVector<-numeric()
vvv<-numeric()
CavRav<-numeric()
fractionRetained<-numeric()
for (iii in 1:length(unitList)) {
ccc<-coef(CRList[[iii]]$fit)
muVector[iii] <- ccc[[1]]
sigmaVector[iii] <- ccc[[2]]
vvv[iii] <- ccc[[1]]+ccc[[2]]*ccc[[2]]/2
AVector[iii] <- ccc[[3]]
infoList<-CRList[[iii]]$infoList
CavRav[iii] <- infoList$totalNumberCitations/infoList$totalNumberReferences
fractionRetained[iii] <-infoList$totalNZPCRPapers/infoList$totalNumberPapers
 }

windows()
plot(x=muVector,  y=sigmaVector, type="p", main=paste(titleString,"mu and sigma"), xlab="mu", ylab="sigma", pch=binType)
windows()
plot(x=muVector,  y=AVector, type="p", main=paste(titleString,"mu and A"), xlab="mu", ylab="A", pch=binType)
windows()
plot(x=AVector,  y=sigmaVector, type="p", main=paste(titleString,"A and sigma"), xlab="A", ylab="sigma", pch=binType)
#windows()
#plot(x=muVector,  y=vvv, type="p", main=paste(titleString,"mu vs mu+sigma^2/2"), xlab="mu", ylab="mu vs mu+sigma^2/2", pch=binType)

windows()
plot(x=CavRav,  y=fractionRetained, type="p", main=titleString, sub="<c>/<r> and fraction retained", xlab="<c>/<r>", ylab="fraction retained", pch=binType)

windows()
muCRdf <- data.frame(mu=muVector, CR=CavRav)
muCRfit <- lm(mu ~ CR ,data=muCRdf)
print(summary(muCRfit))
plot(y=muVector,  x=CavRav, type="p", main=paste(titleString,"mu and <c>/<r>"), ylab="mu", xlab="<c>/<r>", pch=binType)
text(y=muVector, x=CavRav, labels=1:length(muVector), pos=4)
lines(fitted(muCRfit), x=CavRav)

windows()
#muCRdf <- data.frame(mu=muVector, CRav=exp(vvv))
#muCRfit <- lm(mu ~ CR ,data=muCRdf)
#print(summary(muCRfit))
plot(x=CavRav,  y=exp(vvv), type="p", main=paste(titleString,"<c>/<r> and <c/r>"), xlab="<c>/<r>", ylab="<c/r>", pch=binType)
text(x=CavRav, y=exp(vvv), labels=1:length(muVector), pos=4)
#lines(fitted(muCRfit), x=CavRav)



# Table of Names
windows()
barplot(CavRav,names.arg=1:length(unitList))

windows()
plot(x=NULL,  y=NULL, type="n", xlim=c(-1,25) , ylim=c(0,length(muVector)) , main=titleString, xlab=NULL, ylab=NULL, axes=FALSE)
text(x=seq(0, times=length(unitList)), y=1:length(unitList), labels=1:length(unitList), pos=2)
text(x=seq(1, times=length(unitList)), y=1:length(unitList), labels=unitList, pos=4)

} #end of if (parameterAnalysisOn)

plotsOn =FALSE

if (plotsOn) {

xMin<-CRList[[1]]$xMin;
for (iii in 2:length(unitList)) xMin<-min(xMin, CRList[[iii]]$xMin)
xMax<-CRList[[1]]$xMax;
for (iii in 2:length(unitList)) xMax<-max(xMax, CRList[[iii]]$xMax)

yMin<-CRList[[1]]$yMin/CRList[[1]]$totalPapers;
for (iii in 2:length(unitList)) yMin<-min(yMin, CRList[[iii]]$yMin/CRList[[iii]]$totalPapers)
yMax<-CRList[[1]]$yMax/CRList[[1]]$totalPapers;
for (iii in 2:length(unitList)) yMax<-max(yMax, CRList[[iii]]$yMax/CRList[[iii]]$totalPapers)

xMin <- 10^(floor(log10(xMin)))
yMin <- 10^(floor(log10(yMin)))
xMax <- 10^(ceiling(log10(xMax)))
yMax <- 10^(ceiling(log10(yMax))+1)

# basic plot
#titleString<-rootName 
#titleString<-paste(unitList, sep=" ")

#On Screen
print(paste("Windows plotting",yMin,yMax), quote=FALSE)
windows()

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count/Total")
for (iii in 1:length(unitList)) {
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/CRList[[iii]]$totalPapers, pch=iii, col=colourlist[iii])
lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/CRList[[iii]]$totalPapers,col=colourlist[iii], lty=iii)
}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count/Total")
legend (x="topright",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));


# PDF plot
print(paste("pdf plotting",yMin,yMax), quote=FALSE)
pdfFileName<-paste(rootName,minyear,"all","ll.pdf",sep="_")
pdf(pdfFileName, onefile=FALSE, height=6, width=6, pointsize=10)

plot(x=NULL,  y=NULL, type="n",log="xy", xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count/Total")
for (iii in 1:length(unitList)) {
points(CRList[[iii]]$df$citeRefRatio,   CRList[[iii]]$df$count/CRList[[iii]]$totalPapers, pch=iii, col=colourlist[iii])
lines(CRList[[iii]]$df$citeRefRatio, fitted(CRList[[iii]]$fit)/CRList[[iii]]$totalPapers,col=colourlist[iii], lty=iii)
}

#title(main=titleString, sub="(only papers with both positive citation and reference counts)", xlab="citation/reference", ylab="Count/Total")
legend (x="topright",y=NULL, unitList[1:length(unitList)], col=colourlist[1:length(unitList)],lty=1:length(unitList),pch=1:length(unitList));

dev.off()

} # end of if plotsOn

