adGCCtab <-read.table("RAEmanGCCAuthorDisciplineinputBVNLS.dat",header=headerOn, sep="\t")
hist(adGCCtab$V2, breaks=1:24, xlim=c(1,24), main="GCC members by RAE Management Categories", xlab="Category" )

windows()
adtab <-read.table("RAEmanAuthorDisciplineinputBVNLS.dat",header=headerOn, sep="\t")
hist(adtab$V2, breaks=1:24, xlim=c(1,24), main="All members by RAE Management Categories", xlab="Category" )

windows()
barplot(c(adGCCtab$V2, adtab$V2), dim=(24,2), beside=TRUE)
