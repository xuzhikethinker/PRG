adGCCtab <-read.table("RAEmanGCCAuthorDisciplineinputBVNLS.dat",header=FALSE, sep="\t")
histadGCC<-hist(adGCCtab$V2, breaks=1:24, xlim=c(1,24), main="GCC members by RAE Management Categories", xlab="Category" )
print(summary(adGCCtab))
TotalGCCPeople=sum(histadGCC$counts)

#windows()
adtab <-read.table("RAEmanAuthorDisciplineinputBVNLS.dat",header=FALSE, sep="\t")
histad <- hist(adtab$V2, breaks=1:24, xlim=c(1,24), main="All members by RAE Management Categories", xlab="Category" )
print(summary(adtab))
TotalPeople=sum(histad$counts)

windows()
datamat <- array(c(histadGCC$counts, histad$counts), dim=c(24,2) )
barplot( t(datamat), beside=TRUE, names=1:24, main="Totals", legend.text = c("GGC","ALL") )

windows()
datamat <- array(c(histadGCC$counts/TotalGCCPeople, histad$counts/TotalPeople), dim=c(24,2) )
barplot( t(datamat), beside=TRUE, names=1:24, main="Fraction", legend.text = c("GGC","ALL"))

