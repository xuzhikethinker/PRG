# Takes vertex information and edge and makes a time ordered edge list
# This version also relabels the indices starting with 0 as the youngest 
# with ties split probably on pajek MT119 id order.

decBY=TRUE # false for earliest Mathematician is vertex zero.
if (decBY){
 zerovertex="Y"
 print("Youngest, Most recent mathematician is vertex zero",quote=F)
} else{
 zerovertex="O"
 print("Oldest, earliest mathematician is vertex zero",quote=F)
}

inputDir="input/"
outputDir="output/"


rootName="MT119S"
inputEdgeFileName=paste(rootName,"EdgeList.dat",sep="")

inputVertexFileName="MT119VertexInfo.dat"

#Columns are: EdgeID	Source	Target	Weight
print(paste("Reading edge information from",inputEdgeFileName),quote=FALSE)
edf <- read.table(inputEdgeFileName, header=TRUE, fill=TRUE)

#Columns are: VertexNumber	BirthTidy	DeathTidy	PajekName119
print(paste("Reading vertex information from",inputVertexFileName),quote=FALSE)
vdf <- read.table(inputVertexFileName, header=TRUE, fill=TRUE)

lastCompIndex <-max(vdf$WeakComponent)+1 # components run from 0 to this value-1
compSize<-rep(0,times=lastCompIndex) # first in list in index 1 so have to index components with +1
nvertices=length(vdf$VertexNumber)
for (vvv in 1:nvertices) {
 if (vvv!=vdf$VertexNumber[vvv]) print(paste("vertex id failure",vvv,vdf$vertexNumber[vvv]),quote=F)
 compIndex=vdf$WeakComponent[vvv]+1 # have to index components with +1
 compSize[compIndex]=compSize[compIndex]+1
}
#GCCSize=max(compSize)
#GCCIndexList=c(1:compSize)[compSize==GCCSize]
#GCCIndex=GCCIndexList[1] # take first if more than one
#print(paste("Largest component is index ",GCCIndex," of size ",GCCSize),quote=FALSE)

# Now order id by birth date NOTE OUTPUT IS FROM VERTEX YID=0

# array n=IdToNid[v] is the new birth date ordered id of vertex with pajek MT119 id of v.
# That is vertex with Nid=1 has the smallest birth date and so is the oldest.  
# Ties resolved by pajek id.
# array v=NidToId[n] tells us that the n-th oldest vertex (n-th smallest birth date) 
# had the old id of v.
# Note we have no shifts in index, all run from 1
lll<-c(1:nvertices)
NidToId <- order(vdf$Birth,lll, decreasing = decBY)
IdToNid <- order(NidToId,lll)

# Now sort edges by id
nedges=length(edf$EdgeID)

# make 2 arrays ready for output, one for forward one for backward
earlyVertex<-array(0,nedges)
lateVertex<-array(0,nedges)

sourceEarly=0;
targetEarly=0;
sourceTargetEqual=0
sourceTargetEqualSwitch=0#
ev=0
lv=0
for (eee in 1:nedges) {
 sss <- edf$Source[eee]
 ttt <- edf$Target[eee]
 nsss <- IdToNid[sss]
 nttt <- IdToNid[ttt]
 sby <- vdf$Birth[sss]
 tby <- vdf$Birth[ttt]
 sdy <- vdf$Death[sss]
 tdy <- vdf$Death[ttt]
 sn <- vdf$PajekName119[sss]
 tn <- vdf$PajekName119[ttt]
 #print(paste("Source",sss,sn,"born",sby,"died",sdy),quote=F)
 #print(paste("Target",ttt,tn,"born",tby,"died",tdy),quote=F)
 if (nsss<nttt){# t is younger than s, s has earliest birth date, t has larger or equal birth date
    sourceEarly=sourceEarly+1
    ev <- nsss
    lv <- nttt
    if ((decBY & (sby<tby)) | (!decBY & (sby>tby)) ) print(paste("*** ERROR Source",sss,nsss,sn,"born",sby,"Target",ttt,nttt,tn,"born",tby),quote=F)
   } else {# s is younger than t, t has earliest birth date, s has larger or equal birth date
    targetEarly=targetEarly+1
    ev <- nttt
    lv <- nsss
    if ((decBY & (sby>tby)) | (!decBY & (sby<tby)) ) print(paste("*** ERROR Source",sss,nsss,sn,"born",sby,"Target",ttt,nttt,tn,"born",tby),quote=F)
  }
  earlyVertex[eee]=ev-1 # make shift to zero as origin
  lateVertex[eee]=lv-1 # make shift to zero as origin
} # eo for eee

print("********************************",quote=F)
print(paste(nedges,"edges"),quote=F)
print(paste(sourceEarly,"source earliest"),quote=F)
print(paste(targetEarly,"target earliest"),quote=F)
#print(paste(sourceTargetEqual,"source and target equal, ",sourceTargetEqualSwitch," switched on index value"),quote=F)




# name is MTSO = MacTutor Simplified Oldest has zero id
# name is MTSY = MacTutor Simplified Youngest has zero id
# note shift to zero as first vertex nid
trootName=paste("MTS",zerovertex,sep="")


# New Vertex Info 
outputTOV=paste(trootName,"VertexInfo.dat",sep="")
print(paste("Writing Vertex Info with time ordered order id to",outputTOV),quote=FALSE)
if (decBY){
 vdf$Yid <- IdToNid-1 # note shift to zero as first
 #print("Youngest, Most recent mathematician is vertex zero",quote=F)
} else{
 vdf$Oid <- IdToNid-1 # note shift to zero as first
 #print("Oldest, earliest mathematician is vertex zero",quote=F)
}
write.table(vdf, file =outputTOV , append = FALSE,  sep = "\t", quote=FALSE, row.names = FALSE, col.names = TRUE)

# BTF = Birth Time Forward means early source points to target which is later
inputBTFEdgeFileName=paste(trootName,"_BTF_EdgeList.dat",sep="")
print(paste("Writing BTF (Birth Time Forward) edge information with time ordered order id to",inputBTFEdgeFileName),quote=FALSE)
tfdf <- data.frame(source=earlyVertex, target=lateVertex)
write.table(tfdf, file = inputBTFEdgeFileName, append = FALSE,  sep = "\t", quote=FALSE, row.names = FALSE, col.names = TRUE)


# BTB = Birth Time Backwards means later source points backwards to target which is earlier
inputBTBEdgeFileName=paste(trootName,"_BTB_EdgeList.dat",sep="")
print(paste("Writing BTB (Birth Time Backwards) edge information time ordered order id to",inputBTBEdgeFileName),quote=FALSE)
tbdf <- data.frame(source=lateVertex, target=earlyVertex)
write.table(tbdf, file = inputBTBEdgeFileName, append = FALSE,  sep = "\t", quote=FALSE, row.names = FALSE, col.names = TRUE)


