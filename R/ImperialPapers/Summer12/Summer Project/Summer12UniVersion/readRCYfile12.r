# Reads file for reference counts, citation counts, year of publication and optionally type
# if year column is negative uses dateColumn and assumes a fixed length string of form
# 01/02/1987, i.e. year must be charaters 7 to 10
# returns a list with numerical entries list$ref list$cite and list$year, each of which is a list

readRCYfile <- function(inputDir="input/",fullName, headerOn=TRUE, sepString="\t", refColumn, citeColumn, yearColumn, dateColumn=-1, typeColumn=-1) {

if (typeColumn>0) print(paste("--- Reading references, citations, year and type from ",fullName), quote=FALSE)
else print(paste("--- Reading references, citations, and year (not type) from ",fullName), quote=FALSE)
citations <- read.table(paste(inputDir,fullName,sep=""), header=headerOn, sep=sepString);
refList<-citations[[refColumn]]
citeList<-citations[[citeColumn]]



if (yearColumn<=0) {
yearList <- type.convert(substr(citations[[dateColumn]],7,10))
}
else yearList<-citations[[yearColumn]]

typeList <- NULL
if (typeColumn>0) {
typeList <-citations[[typeColumn]]
print(paste("--- Length of four lists ",length(refList),length(citeList),length(yearList),length(typeList)), quote=FALSE)
}
else
{
print(paste("--- Length of three lists ",length(refList),length(citeList),length(yearList)), quote=FALSE)
}

list(ref=refList, cite=citeList, year=yearList, type=typeList)
}
