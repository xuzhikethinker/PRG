RWtreecalc <-function(numberRows,flowMatrix){


maxOutput <- array(0,dim=c(numberRows))
maxOutputTarget <- array(-1,dim=c(numberRows))
terminal <- array(-1,dim=c(numberRows))
treematrix<-array(0,dim=c(numberRows,numberRows))
treeEdgeList<-array(0,dim=c(0,2))
for (iii in 1:numberRows) {
 inFlow=0
 #maxOutput[iii]=0
 for (jjj in 1:numberRows) {
 inFlow=inFlow+flowMatrix[jjj,iii]
 #treematrix[iii,jjj]=0
 if (maxOutput[iii]<flowMatrix[iii,jjj]) {
   maxOutput[iii] = flowMatrix[iii,jjj]
   maxOutputTarget[iii]=jjj
  } 
 }
 if (maxOutput[iii]>inFlow) { treematrix[iii,maxOutputTarget[iii]]=1
 treeEdgeList<-rbind(treeEdgeList,c(iii,maxOutputTarget[iii]))
 terminal[iii]=0
 } else { terminal[iii]=1 }
}

outputList <- list(terminal=terminal, maxOutput=maxOutput, maxOutputTarget=maxOutputTarget, treeMatrix=treematrix, treeEdgeList=treeEdgeList)
}
