# alpha negative selects exponential potential rather than ariadne one.
# outputVector = fixed output from each site.
# selfConsistentOn=true use W(t) not Output vector at each time step for output vector.,  
# initialConditionsMode=1 Random, =2 Use output vector, =3 use InitialWVector
# countMax = maximum number of iterations

RWcalc <- function( outputVector, initialWVector, potlMatrix, beta=1.05, countMax=100, selfConsistentOn=FALSE, initialConditionsMode=2, printOn=TRUE) {

numberRows=length(outputVector)

if (initialConditionsMode==1){
 wVec <- runif(numberRows,0,1)
}

if (initialConditionsMode==2){
 wVec <- outputVector
}
if (initialConditionsMode==3){
 wVec <- initialWVec
}
if (initialConditionsMode==4){
 wVec <- outputVector*(1+runif(numberRows,0,1)/1000)
}

if (printOn) { 
 print(paste("Initial conditions mode and self consistent:",initialConditionsMode,selfConsistentOn), quote=FALSE)
 print("Initial w vector", quote=FALSE)
 print(wVec, quote=FALSE)
}

wVecInitial<- wVec

vVec <- 1/outputVector


for (count in 1:countMax) {
   #print(paste("---",count), quote=FALSE)
   wbetaVec <- (wVec)^beta
   tempVec <- potlMatrix %*% wbetaVec
   newvVec <- 1/tempVec

   newvVecL<-sum(newvVec)
   vc <- sum(abs(newvVec-vVec))/newvVecL
   vVec <-newvVec

   if (selfConsistentOn) newwVec <-  (t(potlMatrix) %*% (wVec*vVec) )*wbetaVec
   else newwVec <-  (t(potlMatrix) %*% (outputVector*vVec) )*wbetaVec

   newwVecL<-sum(newwVec)
   wc <- sum(abs(newwVec-wVec))/newwVecL
   wVec <-newwVec

   #print(paste(vc,wc),quote=FALSE)
   #print(t(vVec),quote=FALSE)
   #print(t(wVec),quote=FALSE)
}

if (printOn) print(paste("Convergence Factors",vc,wc), quote=FALSE)

dfout <- data.frame(vconvergence=vc, wconvergence=wc, wvector=wVec, wvectorinitial=wVecInitial)

}
