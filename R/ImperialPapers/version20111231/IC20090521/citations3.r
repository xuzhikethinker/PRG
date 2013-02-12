#format is "article guid" tab "number of references in paper" tab "number of citations";
#rootName="citationstest.txt"
rootName="ICNScitations"
fullName=paste(rootName,".txt",sep="")
citations <- read.table(fullName, header=FALSE);

nzcrlist<-nzcitations[[3]]/nzcitations[[2]]
nzpcrlist<-nzcrlist[nzcrlist>0 & !is.na(nzcrlist)]

