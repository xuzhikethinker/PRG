#rm(s010,s250,s750,s950,s990,s999,SimonDataList)
#format is "article guid" tab "number of references in paper" tab "number of citations";
#rootName="citationstest.txt"
rootName="ICNScitations"
fullName=paste(rootName,".txt",sep="")
citations <- read.table(fullName, header=FALSE);

print("citations")
clist=citations[[3]]
print(summary(clist))
c5n=fivenum(clist);

print("references")
rlist=citations[[2]]
print(summary(rlist))
r5n=fivenum(rlist);

#print("citations/references")
#crlist=citations[[3]]/citations[[2]]
#print(summary(crlist))
#cr5n=fivenum(crlist);

nzName=paste(rootName,"NonZero.txt",sep="")
nzcitations <- read.table(nzName, header=FALSE);

print("citations/references for ref>0")
nzcrlist=nzcitations[[3]]/nzcitations[[2]]
print(summary(nzcrlist))
nzcr5n=fivenum(nzcrlist);



windows()
hist(rlist, main=paste(fullName," references",sep=""))
windows()
hist(clist, main=paste(fullName," citations",sep=""))
#windows()
#hist(clist/rlist, main=paste(fullName," citations/references",sep=""))
windows()
hist(nzcrlist, main=paste(nzName," citations/(n.z.references)",sep=""))

