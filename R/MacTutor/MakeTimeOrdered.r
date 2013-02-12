# Takes vertex information and edge and makes a time oredered edge list

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


nvertices=length(vdf$VertexNumber)
for (vvv in 1:nvertices) {
 if (vvv!=vdf$VertexNumber[vvv]) print(paste("vertex id failure",vvv,vdf$vertexNumber[vvv]),quote=F)
}

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
 sby <- vdf$Birth[sss]
 tby <- vdf$Birth[ttt]
 sdy <- vdf$Death[sss]
 tdy <- vdf$Death[ttt]
 sn <- vdf$PajekName119[sss]
 tn <- vdf$PajekName119[ttt]
 #print(paste("Source",sss,sn,"born",sby,"died",sdy),quote=F)
 #print(paste("Target",ttt,tn,"born",tby,"died",tdy),quote=F)
 if (sby<tby) {
   sourceEarly=sourceEarly+1
   ev <- sss #sby use for debugging and ditto for ttt
   lv <- ttt
  }
 if (sby>tby) {
   targetEarly=targetEarly+1
   ev <- ttt
   lv <- sss
  } 
  if (sby==tby) {
   sourceTargetEqual=sourceTargetEqual+1 
   if (sss<ttt){
    ev <- sss
    lv <- ttt
   } else {
    sourceTargetEqualSwitch=sourceTargetEqualSwitch+1
    ev <- ttt
    lv <- sss
   }
  }  
  earlyVertex[eee]=ev
  lateVertex[eee]=lv
} # eo for eee

print("********************************",quote=F)
print(paste(nedges,"edges"),quote=F)
print(paste(sourceEarly,"source earliest"),quote=F)
print(paste(targetEarly,"target earliest"),quote=F)
print(paste(sourceTargetEqual,"source and target equal, ",sourceTargetEqualSwitch," switched on index value"),quote=F)


# BTF = Birth Time Forward means early source points to target which is later
inputBTFEdgeFileName=paste(rootName,"_BTF_EdgeList.dat",sep="")
print(paste("Writing BTF (Birth Time Forward) edge information to",inputBTFEdgeFileName),quote=FALSE)
tfdf <- data.frame(source=earlyVertex, target=lateVertex)
write.table(tfdf, file = inputBTFEdgeFileName, append = FALSE,  sep = "\t", quote=FALSE, row.names = FALSE, col.names = TRUE)


# BTB = Birth Time Backwards means later source points backwards to target which is earlier
inputBTBEdgeFileName=paste(rootName,"_BTB_EdgeList.dat",sep="")
print(paste("Writing BTB (Birth Time Backwards) edge information to",inputBTBEdgeFileName),quote=FALSE)
tbdf <- data.frame(source=lateVertex, target=earlyVertex)
write.table(tbdf, file = inputBTBEdgeFileName, append = FALSE,  sep = "\t", quote=FALSE, row.names = FALSE, col.names = TRUE)

