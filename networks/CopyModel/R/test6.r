colourlist=c("black", "red", "blue", "green", "magenta", "brown");
source("C:\\PRG\\networks\\CopyModel\\R\\fitform.r")
source("C:\\PRG\\networks\\CopyModel\\R\\filenamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\dirnamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\fullfilenamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\readandfitdata.r")

rootdirname="C:\\PRG\\networks\\CopyModel\\output\\UPnew\\"
startstring="up-tt20000-tu100"
endstring = "r9999t20000.tdatanh.dat"
Evalue=100
Nvalue=100
prvalue=0.01

mstring="4"

Xvalue=1
resultnew <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
resultlist <- list(resultnew)

Xvalue=50
resultnew <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
resultlist[[length(resultlist)+1]] <- resultnew

Xvalue=100
resultnew <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
resultlist[[length(resultlist)+1]] <- resultnew


mstring="6"

Xvalue=1
resultnew <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
resultlist[[length(resultlist)+1]] <- resultnew


Xvalue=50
resultnew <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
resultlist[[length(resultlist)+1]] <- resultnew


Xvalue=100
resultnew <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
resultlist[[length(resultlist)+1]] <- resultnew



 iii=1
  plot(resultlist[[1]]$time,resultlist[[1]]$F2, col=colourlist[iii], pch= iii)
iii=2;
points(resultlist[[iii]]$time,resultlist[[iii]]$F2, col=colourlist[iii], pch= iii)
iii=3;
points(resultlist[[iii]]$time,resultlist[[iii]]$F2, col=colourlist[iii], pch= iii)
iii=4;
points(resultlist[[iii]]$time,resultlist[[iii]]$F2, col=colourlist[iii], pch= iii)
iii=5;
points(resultlist[[iii]]$time,resultlist[[iii]]$F2, col=colourlist[iii], pch= iii)
iii=6;
points(resultlist[[iii]]$time,resultlist[[iii]]$F2, col=colourlist[iii], pch= iii)

#plot(result1$time,result1$F2, col=colourlist[1])
#lines(spline(result1$time,fitted(result1$fitresult)), col=colourlist[2])

#points(result50$time,result50$F2, col=colourlist[3])
#lines(spline(result50$time,fitted(result50$fitresult)), col=colourlist[4])
