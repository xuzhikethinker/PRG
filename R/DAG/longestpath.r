# find longest path

lnNumberPaths<- function(L,N,D) { 
   LSCALE=N^(1/D)
   (L-1) * log(LSCALE)-lgamma(L)
}

# solves for longest path
longestPath <- function(N,D) { 
   LSCALE=N^(1/D)
   uniroot(function(L) { (L-1) * log(LSCALE)-lgamma(L)},lower=1.5,upper=10*LSCALE )
   }
#   Nvalue=c(100,110,120)
findLongestPath<- function(dimension=2,Nvalue){
   solutionList = list()
   longestPathVector = rep(-1,times=length(Nvalue))
   for (iii in 1:length(Nvalue)) {
      solution = longestPath(Nvalue[iii],dimension)
      solutionList[[iii]]=solution
      longestPathVector[iii] = solution$root
   }
 list(longestPath=longestPathVector, solution=solutionList)
}
# Lscale is N^{1/dimension)
Lscale=(10:200)
Nvalue2 = Lscale^2
res2=findLongestPath(2,Nvalue2)
L2=res2$l
y2vector=L2/Lscale
legendString2="2 dim"

Nvalue3 = Lscale^3
res3=findLongestPath(3,Nvalue3)
L3=res3$l
y3vector=L2/Lscale
legendString3="3 dim"

Nvalue4 = Lscale^4
res4=findLongestPath(4,Nvalue4)
L4=res4$l
y4vector=L2/Lscale
legendString4="4 dim"

# Short list of allowed colours suitable for display or printing
colourlist=c("black", "red", "blue", "green", "magenta", "brown");
  

xMin=min(Lscale)
xMax=max(Lscale)
yMin=min(c(y2vector,y3vector,y4vector))
yMax=max(c(y2vector,y3vector,y4vector))
windows()
plot(x=NULL,  y=NULL, type="n",  xlim=c(xMin,xMax) , ylim=c(yMin,yMax) , main="Cube Space Longest Path",  xlab="N^(1/dim)", ylab="L/(N^(1/dim))", cex=2)
points(Lscale,y2vector,pch=1,col="black")
points(Lscale,y3vector,pch=2,col="red")
points(Lscale,y4vector,pch=3,col="blue")
legend(x="bottomright" ,y=NULL, c(legendString2,legendString3,legendString4), pch=1:3, cex=2, col=colourlist[1:3]);
