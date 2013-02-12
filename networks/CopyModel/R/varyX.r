colourlist=c("black", "red", "blue", "green", "magenta", "brown");
source("C:\\PRG\\networks\\CopyModel\\R\\fitform.r")
source("C:\\PRG\\networks\\CopyModel\\R\\ActualTimings.r")
source("C:\\PRG\\networks\\CopyModel\\R\\filenamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\dirnamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\fullfilenamestring.r")
source("C:\\PRG\\networks\\CopyModel\\R\\readandfitdata.r")

rootdirname="C:\\PRG\\networks\\CopyModel\\output\\UPnew\\"

#These two values are those attempted in the batch file.  Can be modified if not divisors of X
#numberRewiringsTotal =201600
#numberRewiringsPerUpdate = 1000



#Xvaluelist=c(1,10,20,40,50,80,100)
#Xvaluelist=c(1,seq(10,100,by=10))

# list used with ni=100 runs
#numberRewiringsTotal =20160
#numberRewiringsPerUpdate = 100
#Evalue=100
#Nvalue=100
#Xvaluelist=c(1, seq(10,100,by=10) ,5,15,24,35,45,56,64,75,84,96)
#prvaluelist=c(0.1,0.01,0.001)

# list used with ni=1000 runs
numberRewiringsTotal =201600
numberRewiringsPerUpdate = 1000
Evalue=1000
Nvalue=1000
Xvaluelist=c(1,10, seq(100,1000,by=100) ,50,150,240,350,450,560,640,750,840,960)
prvaluelist=c(0.01,0.001,0.0001)


prvalue=prvaluelist[3]
#prvalue=0.01

mstring="4"


# start string set by batch file so uses input values
startstring=paste("up-tt",numberRewiringsTotal,"-tu",numberRewiringsPerUpdate,sep="")
# endstring set by programme so needs to be calculated
runstring = "r9999"
extensionstring =".tdatanh.dat"


# create empty objects of correct mode
F20list4=numeric()
F2inflist4=numeric()
F2inflowlist4=numeric()
F2infuplist4=numeric()
lam2list4=numeric()
lam2lowlist4=numeric()
lam2uplist4=numeric()
resultlist4 <- list()

for (Xvalue in Xvaluelist) {
#print(Xvalue)
timings=ActualTimings(numberRewiringsTotal , numberRewiringsPerUpdate, Xvalue);
endstring=paste(runstring,"t",timings$numberRewiringsTotal,extensionstring,sep="")
lll=length(resultlist4)+1
resultlist4[[lll]] <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
ccc <- coef(resultlist4[[lll]]$fitres) 
#conf <- confint(resultlist4[[lll]]$fitres) 
lam2list4[lll]  = ccc[1][[1]]
#lam2lowlist4[lll] = conf[1]
#lam2uplist4[lll] = conf[4]
F2inflist4[lll] = ccc[2][[1]]
#F2inflowlist4[lll] = conf[2]
#F2infuplist4[lll] = conf[5]
F20list4[lll]   = ccc[3][[1]]
}
tau2list4=-1/log(lam2list4)
tau2X4lm <- lm(tau2 ~ X, data.frame(X=Xvaluelist,tau2=tau2list4))

tau2uplist4=-1/log(lam2lowlist4)
tau2lowlist4=-1/log(lam2uplist4)

mstring="6"
# create empty objects of correct mode
F20list6=numeric()
F2inflist6=numeric()
lam2list6=numeric()
lam2lowlist6=numeric()
lam2uplist6=numeric()
resultlist6 <- list()

for (Xvalue in Xvaluelist) {
#print(Xvalue)
timings=ActualTimings(numberRewiringsTotal , numberRewiringsPerUpdate, Xvalue);
endstring=paste(runstring,"t",timings$numberRewiringsTotal,extensionstring,sep="")
lll=length(resultlist6)+1
resultlist6[[lll]] <- readandfitdata(rootdirname,startstring,endstring,Evalue,Nvalue,prvalue,Xvalue,mstring)
ccc <- coef(resultlist6[[lll]]$fitres) 
#conf <- confint(resultlist6[[lll]]$fitres) 
lam2list6[lll]  = ccc[1][[1]]
#lam2lowlist6[lll] = conf[1]
#lam2uplist6[lll] = conf[4]
F2inflist6[lll] = ccc[2][[1]]
F20list6[lll]   = ccc[3][[1]]
}
tau2list6=-1/log(lam2list6)
tau2X6lm <- lm(tau2 ~ X, data.frame(X=Xvaluelist,tau2=tau2list6))

#tau2uplist6=-1/log(lam2lowlist6)
#tau2lowlist6=-1/log(lam2uplist6)

# error bar tricks from http://wiki.r-project.org/rwiki/doku.php?id=tips:graphics-base:errbars

windows()
#titlestring <- ""
titlestring <- paste("E=" ,Evalue, ", N=" ,Nvalue, ", p_r=" ,prvalue, ", X=various, m= 4(",colourlist[1],"), 6(",colourlist[2],")")
plot(Xvaluelist,tau2list4,col=colourlist[1], pch=1,  xlab="X", ylab=expression(tau[2]), main=titlestring)
#segments(Xvaluelist,tau2lowlist4,Xvaluelist,tau2uplist4,col=colourlist[1])
points(Xvaluelist,tau2list6,col=colourlist[2], pch=2)
#segments(Xvaluelist,tau2lowlist6,Xvaluelist,tau2uplist6,col=colourlist[2])
lines(spline(Xvaluelist,fitted(tau2X4lm)), col=colourlist[1], lty=2)
lines(spline(Xvaluelist,fitted(tau2X6lm)), col=colourlist[2], lty=2)

psname <- paste(startstring,"-teALL-ni",Evalue,"-na",Nvalue,"-pr",prvalue,"-mr4n6tau2.eps",sep="")
postscript(psname,horizontal=FALSE, onefile=FALSE, height=8, width=8, pointsize=12)
plot(Xvaluelist,tau2list4,col=colourlist[1], pch=1,  xlab="X", ylab=expression(tau[2]), main=titlestring)
points(Xvaluelist,tau2list6,col=colourlist[2], pch=2)
lines(spline(Xvaluelist,fitted(tau2X4lm)), col=colourlist[1], lty=2)
lines(spline(Xvaluelist,fitted(tau2X6lm)), col=colourlist[2], lty=2)
dev.off()

windows()
#titlestring <- ""
titlestring <- paste("E=" ,Evalue, ", N=" ,Nvalue, ", p_r=" ,prvalue, ", X=various, m= 4(",colourlist[1],"), 6(",colourlist[2],")")
plot(Xvaluelist,F2inflist4,col=colourlist[1], pch=1,  xlab="X", ylab=expression(F[2](infinity)), main=titlestring)
#segments(Xvaluelist,F2inflowlist4,Xvaluelist,F2infuplist4,col=colourlist[1])
points(Xvaluelist,F2inflist6,col=colourlist[2], pch=2)

psname <- paste(startstring,"-teALL-ni",Evalue,"-na",Nvalue,"-pr",prvalue,"-mr4n6F2inf.eps",sep="")
postscript(psname,horizontal=FALSE, onefile=FALSE, height=8, width=8, pointsize=12)
plot(Xvaluelist,F2inflist4,col=colourlist[1], pch=1,  xlab="X", ylab=expression(F[2](infinity)), main=titlestring)
points(Xvaluelist,F2inflist6,col=colourlist[2], pch=2)
dev.off()


print(paste("**** tau fit summaries: E=" ,Evalue, ", N=" ,Nvalue, ", p_r=" ,prvalue, ", X=various, m= 4 and 6"))
print("--- m=4")
print(summary(tau2X4lm))
print("--- m=6")
print(summary(tau2X6lm))
