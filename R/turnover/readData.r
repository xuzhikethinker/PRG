# Reads basic data needed for work

readData <- function( inputDir, rootName) {

siteFileName<-paste(inputDir,rootName,"_sitesdata.dat",sep="");
distanceFileName<-paste(inputDir,rootName,"_distancematrix.dat",sep="")
print(paste("---     Site file:",siteFileName),quote=F);
print(paste("--- Distance file:",distanceFileName), quote=FALSE)

sitedf <- read.table(siteFileName, header=TRUE, sep="\t", fill=TRUE);
dm <- scan(distanceFileName,0);
list( sitedf=sitedf, distVector=dm)
}



